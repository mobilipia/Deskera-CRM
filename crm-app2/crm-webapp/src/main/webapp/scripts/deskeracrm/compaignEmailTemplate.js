/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
Wtf.namespace('Wtf.ux');
Wtf.namespace('Wtf.ux.Wiz');
Wtf.namespace('Wtf.ux.layout');

Wtf.ux.Wiz = Wtf.extend(Wtf.Panel, {
    loadMaskConfig: {
        'default': 'Saving...'
    },
    cards: null,
    previousButtonText: '&lt;&lt; '+WtfGlobal.getLocaleText("crm.common.previousbtn"),
    nextButtonText: WtfGlobal.getLocaleText("crm.common.nextbtn")+' &gt;&gt;',
    cancelButtonText: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
    finishButtonText: WtfGlobal.getLocaleText("crm.FINISHBTN"),//'Finish',
    headerConfig: {},
    cardPanelConfig: {},
    previousButton: null,
    nextButton: null,
    cancelButton: null,
    cardPanel: null,
    currentCard: -1,
    headPanel: null,
    cardCount: 0,

    initComponent: function(){
        this.initButtons();
        this.initPanels();
        var title = this.title || this.headerConfig.title;
        title = title || "";
        Wtf.apply(this, {
            title: title,
            layout: 'border',
            cardCount: this.cards.length,
            tbar: [this.previousButton, this.nextButton],
            items: [this.headPanel, this.cardPanel]
        });
        this.addEvents('cancel', 'finish', 'beforefinish', 'beforeNextcard');
        Wtf.ux.Wiz.superclass.initComponent.call(this);
    },
    getWizardData: function(){
        var formValues = {};
        var cards = this.cards;
        for (var i = 0, len = cards.length; i < len; i++) {
            if (cards[i].form) {
                formValues[cards[i].id] = cards[i].form.getValues(false);
            } else {
                formValues[cards[i].id] = {};
            }
        }
        return formValues;
    },
    switchDialogState: function(enabled, type){
        this.showLoadMask(!enabled, type);
        this.previousButton.setDisabled(!enabled);
        this.nextButton.setDisabled(!enabled);
        this.cancelButton.setDisabled(true);
        if (this.closable) {
            var ct = this.tools['close'];
            switch (enabled) {
                case true:
                    this.tools['close'].unmask();
                    break;
                default:
                    this.tools['close'].mask();
                    break;
            }
        }
    },
    showLoadMask: function(show, type){
        if (!type) {
            type = 'default';
        }
        if (show) {
            if (this.loadMask == null) {
                this.loadMask = new Wtf.LoadMask(this.body);
            }
            this.loadMask.msg = this.loadMaskConfig['type'];
            this.loadMask.show();
        } else {
            if (this.loadMask) {
                this.loadMask.hide();
            }
        }
    },
    initEvents: function(){
        Wtf.ux.Wiz.superclass.initEvents.call(this);
        var cards = this.cards;
        for (var i = 0, len = cards.length; i < len; i++) {
            cards[i].on('show', this.onCardShow, this);
            cards[i].on('hide', this.onCardHide, this);
            cards[i].on('clientvalidation', this.onClientValidation, this);
        }
    },
    initPanels: function(){
        var cards = this.cards;
        var cardPanelConfig = this.cardPanelConfig;
        Wtf.apply(this.headerConfig, {
            steps: cards.length
        });
        this.headPanel = new Wtf.ux.Wiz.Header(this.headerConfig);
        Wtf.apply(cardPanelConfig, {
            layout: new Wtf.ux.layout.CardLayout(),
            items: cards
        });
        Wtf.applyIf(cardPanelConfig, {
            region: 'center',
            border: false,
            activeItem: 0
        });
        this.cardPanel = new Wtf.Panel(cardPanelConfig);
    },
    initButtons: function(){
        this.previousButton = new Wtf.Button({
            text: this.previousButtonText,
            disabled: true,
            minWidth: 75,
            handler: this.onPreviousClick,
            scope: this
        });
        this.nextButton = new Wtf.Button({
            text: this.nextButtonText,
            minWidth: 75,
            handler: this.onNextClick,
            scope: this
        });
    //		this.cancelButton = new Wtf.Button({
    //			text: this.cancelButtonText,
    //			handler: this.onCancelClick,
    //			scope: this,
    //			minWidth: 75
    //		});
    },
    onClientValidation: function(card, isValid){
        if (!isValid) {
            this.nextButton.setDisabled(true);
        }
        else {
            this.nextButton.setDisabled(false);
        }
    },
    onCardHide: function(card){
        if (this.cardPanel.layout.activeItem.id === card.id) {
            this.nextButton.setDisabled(true);
        }
    },
    onCardShow: function(card){
        var parent = card.ownerCt;
        var items = parent.items;
        for (var i = 0, len = items.length; i < len; i++) {
            if (items.get(i).id == card.id) {
                break;
            }
        }
        this.currentCard = i;
        this.headPanel.updateStep(i, card.title);
        if (i == len - 1) {
            this.nextButton.setText(this.finishButtonText);
        }
        else {
            this.nextButton.setText(this.nextButtonText);
        }
        if (card.isValid()) {
            this.nextButton.setDisabled(false);
        }
        if (i == 0) {
            this.previousButton.setDisabled(true);
        }
        else {
            this.previousButton.setDisabled(false);
        }
    },
    onCancelClick: function(){
        if (this.fireEvent('cancel', this) !== false) {
            this.close();
        }
    },
    onFinish: function(){
        if (this.fireEvent('beforefinish', this, this.cards[this.currentCard]) !== false) {
    //            for(var cnt = 0; cnt < this.cards.length; cnt++) {
    //                this.cards[cnt].destroy();
    //                this.cards[cnt].ownerCt.remove(this.cards[cnt], true);
    //            }
    //			this.ownerCt.remove(this, true);
    //            this.fireEvent('finish', this);
    }
    },
    closePanel: function(){
        for(var cnt = 0; cnt < this.cards.length; cnt++) {
            this.cards[cnt].destroy();
            this.cards[cnt].ownerCt.remove(this.cards[cnt], true);
        }
        this.ownerCt.remove(this, true);
    },
    onPreviousClick: function(){
        if (this.currentCard > 0) {
            this.cardPanel.getLayout().setActiveItem(this.currentCard - 1);
        }
    },
    onNextClick: function(){
        if(this.fireEvent("beforeNextcard", this, this.currentCard)) {
            if (this.currentCard == this.cardCount - 1) {
                this.onFinish();
            }
            else {
                this.cardPanel.getLayout().setActiveItem(this.currentCard + 1);
            }
        }
    }
});

Wtf.ux.Wiz.Header = Wtf.extend(Wtf.BoxComponent, {
    height: 55,
    region: 'north',
    title: WtfGlobal.getLocaleText("crm.wizard"),//'Wizard',
    steps: 0,
    stepText: "Step {0} of {1}: {2}",
    autoEl: {
        tag: 'div',
        cls: 'wtf-ux-wiz-Header',
        children: [{
            tag: 'div',
            cls: 'wtf-ux-wiz-Header-title'
        }, {
            tag: 'div',
            children: [{
                tag: 'div',
                cls: 'wtf-ux-wiz-Header-step'
            }, {
                tag: 'div',
                cls: 'wtf-ux-wiz-Header-stepIndicator-container'
            }]
        }]
    },
    titleEl: null,
    stepEl: null,
    imageContainer: null,
    indicators: null,
    stepTemplate: null,
    lastActiveStep: -1,
    updateStep: function(currentStep, title){
        var html = this.stepTemplate.apply({
            0: currentStep + 1,
            1: this.steps,
            2: title
        });
        this.stepEl.update(html);
        if (this.lastActiveStep != -1) {
            this.indicators[this.lastActiveStep].removeClass('wtf-ux-wiz-Header-stepIndicator-active');
        }
        this.indicators[currentStep].addClass('wtf-ux-wiz-Header-stepIndicator-active');
        this.lastActiveStep = currentStep;
    },
    onRender: function(ct, position){
        Wtf.ux.Wiz.Header.superclass.onRender.call(this, ct, position);
        this.indicators = [];
        this.stepTemplate = new Wtf.Template(this.stepText), this.stepTemplate.compile();
        var el = this.el.dom.firstChild;
        var ns = el.nextSibling;
        this.titleEl = new Wtf.Element(el);
        this.stepEl = new Wtf.Element(ns.firstChild);
        this.imageContainer = new Wtf.Element(ns.lastChild);
        this.titleEl.update(this.title);
        var image = null;
        for (var i = 0, len = this.steps; i < len; i++) {
            image = document.createElement('div');
            image.innerHTML = "&#160;";
            image.className = 'wtf-ux-wiz-Header-stepIndicator';
            this.indicators[i] = new Wtf.Element(image);
            this.imageContainer.appendChild(image);
        }
    }
});

Wtf.ux.Wiz.Card = Wtf.extend(Wtf.Panel, {
    header: false,
    hideMode: 'display',
    initComponent: function(){
        this.addEvents('beforecardhide');
        Wtf.ux.Wiz.Card.superclass.initComponent.call(this);
    },
    isValid: function(){
        if (this.monitorValid) {
            return this.bindHandler();
        }
        return true;
    },
    bindHandler: function(){
        this.form.items.each(function(f){
            if (!f.isValid) {
                f.isValid = Wtf.emptyFn;
            }
        });
        Wtf.ux.Wiz.Card.superclass.bindHandler.call(this);
    },
    initEvents: function(){
        var old = this.monitorValid;
        this.monitorValid = false;
        Wtf.ux.Wiz.Card.superclass.initEvents.call(this);
        this.monitorValid = old;
        this.on('beforehide', this.bubbleBeforeHideEvent, this);
        this.on('beforecardhide', this.isValid, this);
        this.on('show', this.onCardShow, this);
        this.on('hide', this.onCardHide, this);
    },
    bubbleBeforeHideEvent: function(){
        var ly = this.ownerCt.layout;
        var activeItem = ly.activeItem;
        if (activeItem && activeItem.id === this.id) {
            return this.fireEvent('beforecardhide', this);
        }
        return true;
    },
    onCardHide: function(){
        if (this.monitorValid) {
            this.stopMonitoring();
        }
    },
    onCardShow: function(){
        if (this.monitorValid) {
            this.startMonitoring();
        }
    }
});
Wtf.ux.layout.CardLayout = Wtf.extend(Wtf.layout.CardLayout, {
    setActiveItem: function(item){
        item = this.container.getComponent(item);
        if (this.activeItem != item) {
            if (this.activeItem) {
                this.activeItem.hide();
            }
            if (this.activeItem && !this.activeItem.hidden) {
                return;
            }
            this.activeItem = item;
            item.show();
            this.layout();
        }
    }
});


//********************template control
Wtf.campaignMailTemplate = function(conf) {
    Wtf.apply(this, conf);
    Wtf.campaignMailTemplate.superclass.constructor.call(this, {
        layout: "fit",
        border: false
    });
};

Wtf.extend(Wtf.campaignMailTemplate, Wtf.Panel, {
    onRender: function(conf){
        Wtf.campaignMailTemplate.superclass.onRender.call(this, conf);
        var campaignRec = Wtf.data.Record.create([
            {name: "templateid"},
            {name: "templatename"},
            {name: "description"},
            {name: "subject"},
            {name: "thumbnail"},
            {name: "craetedon"},
            {name:'templateclass'}
        ]);
        var mailTemplate = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getEmailTemplateList.do',
            baseParams: {
                flag: 1,
                templateList:true
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, campaignRec)
        });
        this.imageGrid = new Wtf.ThumbnailChooser({
        	tplText : '<div class="thumb"><img src="../../images/{thumbnail}" title="{templatename}"></div><span>{shortName}</span>',
        	store:mailTemplate,
        	height:400,
        	region:'center',
        	emptyText:'<div style="padding:10px;">'+WtfGlobal.getLocaleText("crm.campaigndetails.noimagefoundmsg")+'</div>',
        	containerClass:'tpl-chooser-view',
        	overClass:'templateThumbContainer',
        	selectedClass:'selectedTemplate',
        	bodyStyle:'background:none',
            autoScroll: true,
            border:false,
            prepareData:function(data){
		    	data.shortName = Wtf.util.Format.ellipsis(data.templatename,15);
		    	data.thumbnail = data.thumbnail || "template-default-img.gif";
		    	return data;
		    }
        });

        this.quickSearchTemplateSearch = new Wtf.KWLTagSearch({
            id : 'Quick'+this.id,
            width: 200,
            emptyText:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.mtytxt"),//"Search by Template Name",
            store: mailTemplate
        });
        
        this.selectedTpl=new Wtf.Toolbar.TextItem("");
        this.crateNewTemplate= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.newemailtemplate"),//"New Email Template",
            scope:this,
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.newemailtemplate.ttip")},//'Click to create a new Email Template.'},
            iconCls:"pwndCRM templateEmailMarketing",
            handler:function() {
                var panel = Wtf.getCmp('template_wiz_win'+this.templateid);
                var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate");//"New Template";
                var title = Wtf.util.Format.ellipsis(tipTitle,18);
                if(panel==null) {
                    panel=new Wtf.newEmailTemplate({
                        title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.header.emailtemp")+"'>"+title+"</div>",
                        tipTitle:tipTitle,
                        mailTemplate:mailTemplate
                    });
                    this.mainTab.add(panel);
                }
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();
            }
        });
        
        this.imageGrid.on('tnclick',function(record){
        	this.tplChanged=true;
        	this.selectedTemplate = {tempRec : record};
        	this.selectedTpl.getEl().innerHTML=this.selectedTemplate.tempRec.get('templatename');
        },this); 

        mailTemplate.on('load',function(s){
        	var i = s.find('templateid',this.selectedTemplate);
        	if(i>=0){
        		this.selectedTemplate = {tempRec : s.getAt(i)};
        		this.selectedTpl.getEl().innerHTML=this.selectedTemplate.tempRec.get('templatename');
        	}
            this.quickSearchTemplateSearch.StorageChanged(s);
        },this);
        
        mailTemplate.load();
        this.add(new Wtf.Panel({
            border: false,
            bodyStyle: "background-color: white;",
            items:this.imageGrid,
            childArr: [],
            layout: "fit",
            autoScroll: true,
            tbar:['-',this.quickSearchTemplateSearch,'-',this.crateNewTemplate,'-',this.selectedTpl]
        }));
    },
    getSelectedTemplate: function(){
        return this.selectedTemplate;
    }
});



//********************Add/Edit template control
Wtf.addEmailMarketCmp = function (config){
    Wtf.apply(this, config);
    Wtf.addEmailMarketCmp.superclass.constructor.call(this,{
        border:false,
        layout: "fit"
    });
};

Wtf.extend(Wtf.addEmailMarketCmp,Wtf.Panel,{
    onRender: function(config){
        Wtf.addEmailMarketCmp.superclass.onRender.call(this,config);
        this.targetRecord = new Wtf.data.Record.create([{
            name:'listid'
        },{
            name:'listname'
        },{
            name:'targetsrc'
        },{
            name:'description'
        }]);
        var targetReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.targetRecord);
        this.targetStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getTargetList.do',
            baseParams:{
                flag:4,
                allflag:true,
                avoidblanklist:true
            },
            method:'post',
            reader:targetReader
        });
        this.targetStore.load();
        
        var emailcomboReader = new Wtf.data.Record.create([
        {
            name: 'id'
        },
        {
            name: 'name'
        }
        ]);
        this.emailtStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'notification/action/getNotificationSettings.do',
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, emailcomboReader),
            autoLoad:false
        });
        this.emailtStore.load();
        
        this.emailtStore.on('load',function(){
        	var defaultemail = new  emailcomboReader({
                id:"0",
                name:'newsletters@deskera.com'
            });
            this.emailtStore.insert(0, defaultemail);
        },this);
        
        
        this.userMailCombo = new Wtf.form.ComboBox({
            fieldLabel : WtfGlobal.getLocaleText("crm.targetlists.sendermailtext")+' *',//'Sender Mail * ',
            store: this.emailtStore,
            displayField: 'name',
            valueField: 'name',
            //width : 350,
            typeAhead: true,
            mode: 'local',
            triggerClass :'dttrigger',
            forceSelection: true,
            emptyText: WtfGlobal.getLocaleText("crm.targetlists.clicktoseltext"),//"Click to select",
            //cls: 'outboundCombo',
            editable: true,
            triggerAction: 'all',
            selectOnFocus: true,
            plugins:(new Wtf.common.comboAddNew({
                handler: function(){
                    Wtf.outboundEmailSettings(this,this.emailtStore);
                },
                scope: this
            }))
        });
        
        this.activityform=new Wtf.form.FormPanel({
            autoScroll:true,
            border:false,
            height:500,
            items :[{
                border: false,
                defaults: {
                    border: false,
                    xtype: "fieldset",
                    autoHeight: true
                },
                items: [{
                    cls: "marketingFieldset",
                    title: WtfGlobal.getLocaleText("crm.emailmarketing.campconf.campconfsetup"),//"Campaign Configuration setup",
                    layout:'column',
                    //labelWidth: 150,
                    items: [{
                        columnWidth: '.50',
                        layout : 'form',
                        border : false,
                        labelWidth: 150,
                        defaults:{anchor:'80%'},
                        items : [
                    /*layout: 'form',
                        border:false,
                        labelWidth: 150,
//                        cls:'mailMarketingForm',
                        defaults: {
                            anchor:'-10',
                            allowBlank : false,
                            ctCls: "newTicketField"
                        },
                        items: [*/
                    this.name = new Wtf.ux.TextField({
                        fieldLabel:WtfGlobal.getLocaleText("crm.campaigndetails.header.confname")+'*',// 'Configuration Name* ',
                        allowBlank : false,
                        maxLength : 255

                    }),

                    this.subject = new Wtf.ux.TextField({
                        fieldLabel: WtfGlobal.getLocaleText("crm.case.defaultheader.subject")+'*',//'Subject* ',
                        maxLength : 255,
                        allowBlank : false
                    }),
                    this.userMailCombo
                    ]},
                    {
                    	 columnWidth: '.50',
                         layout : 'form',
                         defaults:{anchor:'80%'},
                         border : false,
                         labelWidth: 150,
                         items : [
                    	this.fromname= new Wtf.form.TextField({
                        fieldLabel: WtfGlobal.getLocaleText("crm.emailmarketing.campconf.fromname")+'*',//'From Name* ',
                        allowBlank : false
                    }),
                    this.replayMail = new Wtf.form.TextField({
                        fieldLabel: WtfGlobal.getLocaleText("crm.targetlists.replymailtext")+'*',//'Reply Mail* ',
                        regex:Wtf.ValidateMailPatt,
                        allowBlank : false
                    }),
                    this.captureLead=new Wtf.form.Checkbox({
                        hideLabel: true,
                        name: 'captureleadfromcampaign',
                        boxLabel:WtfGlobal.getLocaleText("crm.emailmarketing.campconf.capturedleadchkbx")+'   '+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("crm.emailmarketing.campconf.capturedleadchkbx.help"))//"Check the box to move targets, who have viewed the campaign, to lead module. Duplicate leads are not created and duplicacy is checked by target's first name, last name and email address.")
                    })
                    	]}     
                    // ]
                    /*,{
                        columnWidth: 0.49,
//                        cls: "marketingFieldsetRight",
                        layout: 'form',
                        border:false,
                        defaults: {
                            width: 430,
                            allowBlank : false,
//                            labelStyle: "width: 100%;",
                            ctCls: "newTicketField"
                        },
                        items:[
                        this.fromname= new Wtf.form.TextField({
                            fieldLabel: 'From Name* '
                        }),
                        this.replayMail = new Wtf.form.TextField({
                            fieldLabel: 'Reply Mail* ',
                            vtype:'email'
                        })
                        ]
                   
                    }*/]
                }]
            }]
        });
        this.campTargetStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getEmailMarkTargetList.do',
            baseParams:{
                flag:5,
                emailmarkid : this.mode==1 ? this.emailmarkid : ''
            },
            method:'post',
            reader:targetReader
        });
        if(this.mode==1){
            this.fromname.setValue(this.recData.fromname);
            this.userMailCombo.setValue(this.recData.fromaddress);
            this.name.setValue(this.recData.name);
            this.subject.setValue(this.recData.marketingsubject);
            this.replayMail.setValue(this.recData.replymail);
            this.captureLead.setValue(this.captureleadstatus);
        //            this.uSub.setValue(this.recData.unsub);
        //            this.ffl.setValue(this.recData.fwdfriend);
        //            this.arch.setValue(this.recData.archive);
        //            this.up.setValue(this.recData.updatelink);
        } else if (this.mode==0) {
            this.fromname.setValue("Newsletters");
            this.userMailCombo.setValue("newsletters@deskera.com");
            this.name.setValue("");
            this.replayMail.setValue("newsletters@deskera.com");
        }
        this.campTargetStore.load();
        this.targetColumn = new Wtf.grid.ColumnModel([ 
             new Wtf.grid.RowNumberer()
            ,new Wtf.grid.CheckboxSelectionModel(),
            {
                header:WtfGlobal.getLocaleText("crm.targetlists.title"),//'Target List',
                dataIndex:'listname',
                renderer : function(val) {
                    if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.manage)){
                        return "<a href = '#' class='listofTargets' wtf:qtip="+WtfGlobal.getLocaleText("crm.targetlists.ttip")+"> "+val+"</a>";
                    } else {
                        return val;
                    }
                }
        }]);
        this.quickSearchTF = new Wtf.KWLQuickSearchUseFilter({
            id : 'masterFilter'+this.id,
            width: 140,
            field : "listname",
            emptyText:WtfGlobal.getLocaleText("crm.targetlist.search.mtytxt")//"Search by Target List "
        });
        this.quickSearchTF.StorageChanged(this.targetStore);
        this.camptoolbarItems = [];
        this.camptoolbarItems.push(this.quickSearchTF);
        this.createTargetListBtn = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.targetlist.newlistcreateBTN"),//"New Target List",
            scope:this,
            iconCls:"targetlistIcon",
            tooltip:{text:WtfGlobal.getLocaleText("crm.dashboard.createlist.ttip")},//'Create a new list of Targets for Email Marketing.'},
            handler: function() {
                addNewTargetListTab(this.targetStore,undefined, true);
            }
        });

        this.viewTargetListBtn = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.targetlists.viewtargetlistBTN"),//"View Target List",
            scope:this,
            iconCls:"targetlistIcon",
            tooltip:{text:WtfGlobal.getLocaleText("crm.targetlists.viewtargetlistBTN.clicktoviewttip")},//'Click to view targets of selected list.'},
            handler: this.viewList
        });

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.manage)){
            this.camptoolbarItems.push('-');
            this.camptoolbarItems.push(this.createTargetListBtn);
            this.camptoolbarItems.push('-');
        }

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.view)){
            this.camptoolbarItems.push('-');
            this.camptoolbarItems.push(this.viewTargetListBtn);
            this.camptoolbarItems.push('-');
        }
        this.targetGrid = new Wtf.grid.GridPanel({
            store: this.targetStore,
            cm: this.targetColumn,
            sm : new Wtf.grid.CheckboxSelectionModel(),
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.targetlists.grid.mtytext")//"You have not created any Target Lists yet. Create a new Target List by clicking on 'New Target List'"
            },
            tbar:this.camptoolbarItems
        });
        this.targetListGridPanel = new Wtf.common.KWLListPanel({
            id: "targetListGridPanel" + this.id,
            title: WtfGlobal.getLocaleText("crm.targetlists.gridtitle"),//'Select a target list from the list below.',
            autoLoad: false,
            autoScroll:true,
            paging: false,
            layout: 'fit',
            items: [this.targetGrid]
        });
        this.add({
            region : 'center',
            border : false,
            layout : 'fit',
            items :[{
                layout :'border',
                border : false,
                defaults : {
                    border : false,
                    layout : 'fit'
                },
                items :[{
                    region : 'north',
                    cls: "panelCls",
                    bodyStyle : 'background:#f1f1f1;font-size:10px;padding:40px 10px 10px 25px;',
                    items : this.activityform
                },{
                    region : 'center',
                    bodyStyle : 'background:#f1f1f1;padding:0px 10px 10px 20px;',
                    items : this.targetListGridPanel
                }]
            }]
        });
        this.activityform.ownerCt.doLayout();
        this.targetGrid.on("cellclick",this.listofTargets,this);
        this.campTargetStore.load();
        this.targetStore.on('load',function(){
            this.userMailCombo.el.dom.nextSibling.nextSibling.className = 'outboundConfig';
            this.userMailCombo.el.dom.nextSibling.nextSibling.style.left = '';
            for(var i=0 ;i < this.targetStore.getCount(); i++){
                for(var j=0 ;j < this.campTargetStore.getCount(); j++){
                    if(this.targetStore.data.items[i].data.listid==this.campTargetStore.data.items[j].data.listid){
                        this.targetGrid.getSelectionModel().selectRow(i,true);
                    }
                }
            }
           this.doLayout();
        },this);

        this.campTargetStore.on('load',function(){
            this.targetStore.load();
        },this);
    },
    listofTargets:function(Grid,rowIndex,columnIndex, e){
        var event = e ;
         if(event.getTarget("a[class='listofTargets']")) {

            var mode=1;//for Edit
            var record = Grid.getSelectionModel().getSelected();
            var listID = record.get('listid');
            var listName = record.get('listname')+" ";
            var targetsource = record.get('targetsrc')+" ";
            var description=record.get("description");

            var tlId = 'targetListTabnewedit_wizard'+mode+listID;

            var targetListTab = Wtf.getCmp(tlId );
            if(targetListTab == null) {
                targetListTab = new Wtf.targetListWin({
                    mode : mode,
                    id : tlId,
                    listID : listID,
                    TLID : listID,
                    store:this.EditorStore,
                    listname : listName,
                    targetsource:Encoder.htmlDecode(targetsource),
                    description:Encoder.htmlDecode(description) ,
                    iconCls: "pwnd editTargetListEmailMarketingWin",
                    mainTab:this.mainTab
                });
                this.mainTab.add(targetListTab);
            }
            this.mainTab.setActiveTab(targetListTab);
            this.mainTab.doLayout();
        }
    },
    viewList: function(){
        var s =this.getList();
        if(s.length==1){
            var record = this.targetGrid.getSelectionModel().getSelected();
            var targetlistId =record.get('listid');
            var targetlistname=record.get('listname');
            var tipTitle=targetlistname+"'s Targets";
            var title = Wtf.util.Format.ellipsis(tipTitle,19);
            var tlId = 'targetListsTargets'+this.id+targetlistId;
            var targetListTab = Wtf.getCmp(tlId );
            if(targetListTab == null) {
                targetListTab = new Wtf.targetListTargets({
                    id:'targetListsTargets'+this.id+targetlistId,
                    closable:true,
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle="+WtfGlobal.getLocaleText("crm.targetlists.title.plural")+">"+title+"</div>",
                    layout:'fit',
                    border:false,
                    targetlistId:targetlistId,
                    iconCls:"targetlistIcon"
                });
                this.mainTab.add(targetListTab);
            }
            this.mainTab.setActiveTab(targetListTab);
            this.mainTab.doLayout();

        } else if(s.length==0) {
            WtfComMsgBox(956);

        } else {
            WtfComMsgBox([WtfGlobal.getLocaleText("crm.ALERTTITLE"),WtfGlobal.getLocaleText("crm.targetlists.selonetargetmsg")]);//" Please select only one target list to view."]);

        }
    },
    getList: function(){
        var list = this.targetGrid.getSelectionModel().getSelections();
        return list;
    },
    getName: function(){
        return this.name.getValue().trim();
    },
    getSubject: function(){
        return this.subject.getValue().trim();
    },
    getSenderMail: function(){
        return this.userMailCombo.getValue().trim();
    },
    getFromName: function(){
        return this.fromname.getValue().trim();
    },
    getReplyMail: function(){
        return this.replayMail.getValue().trim();
    },
    setValues: function(cmpDetailId){
        this.defaultForm = new Wtf.form.FormPanel({
            waitMsgTarget: true,
            labelWidth:110,
            border : false,
            bodyStyle : 'font-size:10px;padding:10px 20px;margin-top:3%',
            autoScroll:false,
            autoHeight:true,
            defaultType:'textfield',
            layoutConfig: {
                deferredRender: false
            },
            items: [
                    this.senderCmb = new Wtf.form.ComboBox({
                        fieldLabel :WtfGlobal.getLocaleText("crm.targetlists.sendermailtext")+'*',//Sender Mail * ',
                        store: this.emailtStore,
                        displayField: 'name',
                        valueField: 'name',
                        width : 185,
                        typeAhead: true,
                        mode: 'local',
                        triggerClass :'dttrigger',
                        forceSelection: true,
                        emptyText: WtfGlobal.getLocaleText("crm.targetlists.clicktoseltext"),//"Click to select",
//                        cls: 'outboundCombo',
                        editable: true,
                        triggerAction: 'all',
                        selectOnFocus: true,
                        value: this.userMailCombo.getValue()
                    }),
                    this.replyMailTxt = new Wtf.form.TextField({
                        fieldLabel: WtfGlobal.getLocaleText("crm.targetlists.replymailtext")+'*',//'Reply Mail* ',
                        regex:Wtf.ValidateMailPatt,
                        width:185,
                        allowBlank : false,
                        value: this.replayMail.getValue().trim()
                    }),
                    this.subjectTxt = new Wtf.ux.TextField({
                        fieldLabel:WtfGlobal.getLocaleText("crm.case.defaultheader.subject")+'*',// 'Subject* ',
                        width:185,
                        allowBlank : false,
                        maxLength : 255,
                        value: this.subject.getValue().trim()
                    })]
        });
        var assmngrbtn=[];
        assmngrbtn.push({
            text:'Ok',
            handler:function(){
                var sender = this.senderCmb.getValue();
                var replyM = this.replyMailTxt.getValue().trim();
                var subjectVal = this.subjectTxt.getValue().trim();
                this.userMailCombo.setValue(sender);
                this.replayMail.setValue(replyM);
                this.subject.setValue(subjectVal);
                Wtf.getCmp(cmpDetailId).fireEvent('editConfig', replyM, sender, subjectVal);
                this.default_window.close();
            },
            scope:this
        });

        assmngrbtn.push({
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
            scope:this,
            handler: function(){
                this.default_window.close();
            }
        });
        this.default_window =new Wtf.Window({
            title:WtfGlobal.getLocaleText("crm.targetlists.editwin.title"),//"Edit Window",
            autoHeight:true,
            width:400,
            modal:true,
            items:[{
                border:false,
                cls : 'panelstyleClass2',
                items:[this.defaultForm]
            }],
            buttonAlign :'right',
            buttons: assmngrbtn
        });
        this.default_window.show();
    }
});
