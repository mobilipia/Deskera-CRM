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
Wtf.opportunityEditor = function (config){
    Wtf.opportunityEditor.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.opportunityEditor,Wtf.Panel,{
	getEditor:Wtf.SpreadSheetGrid.prototype.getEditor,
    initComponent: function(config){
        Wtf.opportunityEditor.superclass.initComponent.call(this,config);

        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
    },
    onRender: function(config){
        this.getStores();

        this.getEditorGrid();

        this.getAdvanceSearchComponent();

        this.getDetailPanel();

        this.opportunitypan= new Wtf.Panel({
            layout:'border',
            title:WtfGlobal.getLocaleText({key:"crm.tab.title",params:[WtfGlobal.getLocaleText("crm.OPPORTUNITY")]}),//"Opportunity List",
            iconCls:getTabIconCls(Wtf.etype.opportunity),
            border:false,
            id:this.id+'opportunitypan',
            attachDetailTrigger:true,
            listeners:{
    			'afterlayout':function(p){
        			if(p.attachDetailTrigger){
        				p.layout.south.slideOut = p.layout.south.slideOut.createSequence(this.getDetails,this);
        				delete p.attachDetailTrigger;
        			}
        		},
    			scope:this
        	},
            items:[
            this.objsearchComponent
            ,
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[this.spreadSheet],
                tbar:this.toolbarItems,
                bbar:this.btmbar
            },
            {
                region:'south',
                height:250,
                title:WtfGlobal.getLocaleText("crm.editors.otherdetailregion"),//'Other Details',
                collapsible:true,
                collapsed : true,
                split : true,
                plugins: new Wtf.ux.collapsedPanelTitlePlugin(),
                layout: "fit",
                items:[this.detailPanel],
                listeners:{
            		'expand':this.getDetails,
            		scope:this
            	}
            }
            ]
        });
        this.mainTab=new Wtf.TabPanel({
           id:this.id+"caseTabPanel",
           scope:this,
           border:false,
           resizeTabs: true,
           minTabWidth: 155,
           enableTabScroll: true,
           items:[this.opportunitypan]
        });

        if(this.archivedFlag==1){
            this.add(this.opportunitypan);
        } else if(this.subTab==true){
            this.add(this.opportunitypan);
        }else{
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.opportunitypan);
        }
        this.opportunitypan.on("activate",function(){
            Wtf.getCmp("OpportunityHomePanelopportunitypan").doLayout()
        },this)
        Wtf.opportunityEditor.superclass.onRender.call(this,config);

        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);
    },
    getDetails:function(){
    	var sm = this.EditorGrid.getSelectionModel();
    	if(sm.getCount()!=1)
    		return;
    	var commentlist = getDocsAndCommentList(sm.getSelected(), "oppid",this.id,undefined,'Opportunity',undefined,undefined,'oppownerid',this.contactsPermission,0);    	
    },
    getEditorGrid:function (){


        x=[
        {
            name:'oppid'
        },
        {
            name:'oppownerid',
            defValue:loginid
        },
        {
            name:'oppowner',
            defValue:_fullName
        },
        {
            name:'oppname'
        },
        {
            name:'oppstageid'
        },
        {
            name:'oppstage'
        },
        {
            name:'opptypeid'
        },
        {
            name:'opptype'
        },
        {
            name:'oppregionid'
        },
        {
            name:'oppregion'
        },
        {
            name:'leadsourceid'
        },
        {
            name:'leadsource'
        },
        {
            name:'closingdate',
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },
        {
            name:'closedat'
        },
        {
            name:'accountnameid',
            newValue:(this.accFlag==0?"":this.mapid)
        },
        {
            name:'accountname',
            newValue:(this.accFlag==0?"":this.RelatedRecordName)
        },
        {
            name:'probability',
            type:'float'
        },
        {
            name:'currencyid'
        },
        {
            name:'productserviceid',
            newValue:(this.accFlag==0?"":this.productid)
        },
        {
            name:'price',
            type:'float',
            newValue:(this.accFlag==0?"":this.price)
        },
        {
            name:'salesamount',
            type:'float'
        },
        {
            name:"createdon",
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },
        {
            name:"creatdon",
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },
        {
            name:"updatedon",
            dateFormat:'time',
            type:'date'
        },
        {
            name:'relatedtoid'
        },
        {
            name:'relatedtonameid'
        },
        {
            name:"contact"
        },
        {
            name:'activities'
        },
        {
            name:"totalcomment"
        },
        {
            name:"commentcount"
        },
        {
            name:"cellStyle"
        },
        {
            name:'validflag'
        }

        ];

        this.EditorRec = new Wtf.data.Record.create(x);
        this.loadCount=0;
        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.EditorRec);
        
        this.updateInfo = {
            	keyField:'oppid',
            	auditStr:"Opportunity details updated from opportunity profile for ",
            	url:"Opportunity/action/updateMassOpportunities.do",
            	flag:23,
            	type:"Opportunity"
    		};

        this.EditorStore = new Wtf.data.Store({
        	proxy: new Wtf.data.HttpProxy(new Wtf.data.Connection({url: Wtf.req.springBase+"Opportunity/action/getOpportunities.do", timeout:90000})), 
            //url: Wtf.req.springBase+"Opportunity/action/getOpportunities.do",
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            paramNames:{sort:'field',dir:'direction'},
            baseParams:{
                flag:this.urlFlag,
                mapid:this.mapid,
                isarchive:this.newFlag==3?true:false
            },
            extraSortInfo:{xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false},
            sortInfo:{field:"updatedon",direction:"DESC"},
            method:'post',
            reader:EditorReader
        });
        this.EditorColumnArray = [
            {
                header:'',
                width:30,
                id:'validflag',
                dataIndex:'validflag',
                unselectable:true,
                dbname:'c.validflag',
                sortable: true,
                renderer:WtfGlobal.renderValidFlagAndComment
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.opportunityname"),//"Opportunity Name",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.opportunityname"),//'Opportunity Name',
                id:'oppname',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor : new Wtf.form.TextField({xtype:"textfield", maxLength:255,regexText:Wtf.MaxLengthText+"255"}),
                dbname:'c.oppname',
                xtype:'textfield',
                dataIndex: 'oppname'
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.owner"),//"Owner",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.owner"),//'Owner',
                id:'owner',
                title:'oppowner',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                hidden:true,
                editor : this.getEditor({xtype:"combo", store:Wtf.opportunityOwnerStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'oppowner', loadOnSelect : true}),
                dataIndex:'oppowner',
                dbname:'oo.usersByUserid.userID',
                cname:'owner',
                xtype:'combo'
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.accountname"),//"Account Name",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.accountname"),//'Account Name',
                id:'accountname',
                title:'accountname',
                pdfwidth:60,
                width:Wtf.width,
                editor : this.getEditor({xtype:"combo", store:Wtf.parentaccountstoreSearch, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'accountname'}),
                dataIndex: 'accountname',
                sortable: true,
                dbname:'c.crmAccount.accountid',
                cname:'Account',
                xtype:'combo',
                hidden:(this.accFlag==0?false:true)
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.stage"),//"Stage",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.stage"),//'Stage',
                id:'stage',
                title:'stage',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.oppstageStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'oppstage', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.opportunitystage}),
                dbname:'c.crmCombodataByOppstageid.ID',
                sortable: true,
                cname:'Opportunity Stage',
                xtype:'combo',
                dataIndex: 'oppstage'
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.closeddate"),//"Close Date",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.closeddate"),//'Close Date',
                id:'closingdate',
                title:'closingdat',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'center',
                sortable: true, // ToDo Fix me
                editor : new Wtf.form.DateField({
                    xtype:'datefield',
                    offset:Wtf.pref.tzoffset,
                    format:WtfGlobal.getOnlyDateFormat()
                }),
                dataIndex: 'closingdate',
                dbname:'c.closingdate',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            },
            {
                header:"Product",
                tip:'Double click on a cell below to choose multiple products for an opportunity. Bring the mouse pointer over a cell below to view the products assigned to an opportunity.',
                id:'product',
                title:'exportmultiproduct',
                pdfwidth:60,
                width:Wtf.width,
                editor : this.getEditor({xtype:"select", store:Wtf.productStore, useDefault:true}),
                dbname:'p.productId.productid',
                cname:'Product',
                xtype:'select',
                dataIndex: 'productserviceid'
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.price")+"("+ WtfGlobal.getCurrencySymbol()+")",//"Price ("+WtfGlobal.getCurrencySymbol()+")",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.price"),//'Price',
                id:'price',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                title:'exportprice',
                renderer:WtfGlobal.currencyRenderer,
                sortable: true,
                hidden:true,
                editor : new Wtf.form.NumberField({xtype : "numberfield", maxLength : 15, allowNegative : false,regexText:Wtf.MaxLengthText+"15"}),
                align:"right",
                dbname:'ifnull(CONVERT(c.price,DECIMAL(64,4)),0)',
                xtype:'numberfield',
                dataIndex: 'price'
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.salesammount")+ "("+WtfGlobal.getCurrencySymbol()+")",//Sales Amount ("+WtfGlobal.getCurrencySymbol()+")",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.salesammount"),//'Sales Amount',
                id:'salesamount',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                title:'exportsalesamount',
                hidden:true,
                renderer:WtfGlobal.currencyRenderer,
                sortable: true,
                editor : new Wtf.form.NumberField({xtype : "numberfield", maxLength : 15, allowNegative : false,regexText:Wtf.MaxLengthText+"15"}),
                align:"right",
                dbname:'ifnull(CONVERT(c.salesamount,DECIMAL(64,4)),0)',
                xtype:'numberfield',
                dataIndex: 'salesamount'
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.type"),//"Type",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.type"),//'Type',
                id:'type',
                title:'type',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.opptypeStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'opptype', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.opportunitytype}),
                dataIndex: 'opptype',
                dbname:'c.crmCombodataByOpptypeid.ID',
                sortable: true,
                xtype:'combo',
                cname:'Opportunity Type'
            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.leadsource"),//"Lead Source",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.leadsource"),//'Lead Source',
                id:'leadsource',
                title:'leadsource',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.lsourceStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'leadsource', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.leadsource}),
                dataIndex: 'leadsource',
                dbname:'c.crmCombodataByLeadsourceid.ID',
                sortable: true,
                cname:'Lead Source',
                xtype:'combo'
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.region"),//"Region",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.region"),//'Region',
                id:'region',
                title:'region',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.regionStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'oppregion', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.region}),
                dataIndex: 'oppregion',
                dbname:'c.crmCombodataByRegionid.ID',
                sortable: true,
                xtype:'combo',
                cname:'Region'
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.probability"),//"Probability (&#37;) ",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.probability"),//'Probability',
                id:'probability',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'right',
                sortable: true,
                editor:new Wtf.form.NumberField({
                    xtype : 'numberfield',
                    minValue: 0,
                    maxValue: 100
                }),
                dbname:'ifnull(CONVERT(c.probability,DECIMAL(64,4)),0)',
                xtype:'numberfield',
                dataIndex: 'probability',
                renderer: function(val){
                    if(val!="")
                        val+=" %";
                    else if(val=="0")
                        val="0 %"
                    return val;
                }
            },{
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.createdon"),//"Opportunity Creation Date",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.createdon"),//'Opportunity Creation Date',
                id:'createdon',
                title:'createdon',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'center',
                sortable: true,
                editor : new Wtf.form.DateField({
                    xtype:'datefield',
                    offset:Wtf.pref.tzoffset,
                    format:WtfGlobal.getOnlyDateFormat()
                }),
                dataIndex: 'createdon',
                dbname:'c.createdOn',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            },
            {
                header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.updatedon"),//"Opportunity Updated Date",
                tip:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.updatedon"),//'Opportunity Updation Date',
                id:'updatedon',
                title:'updatedon',
                hidden:true,
                fixed:true,
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'center',
                sortable: true,
                editor : new Wtf.form.DateField({
                    xtype:'datefield',
                    hidden:true,
                    offset:Wtf.pref.tzoffset,
                    format:WtfGlobal.getOnlyDateFormat()
                }),
                dataIndex: 'updatedon',
                readOnly:true,
                dbname:'c.updatedOn',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            }
            ];

        this.tbarShowActivity=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.showActivityBTN"),//'Activities',
            id:'oppactivity',//In use,do not delete.
            tooltip:{text:WtfGlobal.getLocaleText({key:"crm.editor.toptoolbar.showActivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.OPPORTUNITY")]})},//'Select an opportunity to add its activity details.'},
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.todo),
            handler:this.showActivity
        });
        this.deleteopp= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
            tooltip:{text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip")},//'Select row(s) to delete.'},
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            disabled:(this.newFlag==1?true:false),
            handler:this.opportunityDelete
        });
        
        this.recentopportunities = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.opportunity.toptoolbar.viewrecentopp"),//"View Recent Opportunities ",
            scope:this,
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.opportunity.toptoolbar.viewrecentopp.ttip")//"Click to view recent opportunities."
            },
           iconCls:'pwndCRM showgrp',
           handler:this.handleRecentOpportunities
        },this);

        this.tbarArchiveArray = Wtf.archivedMenuArray(this,"Opportunitie");
        this.tbarArchive = Wtf.archivedMenuButtonA(this.tbarArchiveArray,this,"Opportunities");

        var extraConfig = {};
        var extraParams = "{ \"Deleteflag\":0, \"UsersByCreatedbyid\":\""+loginid+"\", \"UsersByUpdatedbyid\":\""+loginid+"\", \"Company\":\""+companyid+"\"}";
        this.importOppsA =Wtf.importMenuArray(this,"Opportunity",this.EditorStore,extraParams, extraConfig);
        this.importOpps = Wtf.importMenuButtonA(this.importOppsA,this,"Opportunities");

        this.toolbarItems = [];
        this.tbSingle = [];
        this.tbMulti = [];
        this.tbDefault = [];
        var tbIndex = 0;

        this.quickSearchTF = new Wtf.KWLTagSearch({
            width: 220,
            emptyText:WtfGlobal.getLocaleText("crm.opportunity.quicksearch.mtytxt"),//"Search by Opportunity Name",
            id:'quick7',//In use,do not delete.
            Store:this.EditorStore,
            parentGridObj: this
        });
        
        this.toolbarItems.push(this.quickSearchTF);
            this.tbSingle.push(tbIndex);
            this.tbMulti.push(tbIndex);
            this.tbDefault.push(tbIndex);
            tbIndex++;
        this.contactsPermission = false;// Detail Panel contacts
        var optbutton=new Array();
        this.btmbar=[Wtf.moduleBootomToolBar(this,this.id+'CRMupdownCompo',true,false),optbutton];
        	if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage)) {
            	if(this.newFlag==2){
            	    this.toolbarItems.push(Wtf.AddNewButton(this));
                	tbIndex++;
                	this.toolbarItems.push(Wtf.EditRecordButton(this));
                	tbIndex++;
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.del)) {
                    this.toolbarItems.push(this.deleteopp);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.archive)) {
                    this.toolbarItems.push(this.tbarArchive);
                        this.tbDefault.push(tbIndex);
                        tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.view)) {
                    this.contactsPermission=true;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)) {
                    this.toolbarItems.push(this.tbarShowActivity);
                  //  this.tbSingle.push(tbIndex);
                    tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.exportt)) {
                    this.exp = exportButton(this,"Opportunities",7);
                    optbutton.push(this.exp);
                   /* this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;
                     */
                    if(this.clearFlag!=undefined || this.subTab!=null) {
                    	 this.tbarExport.on("mouseover", function(){
                         	var s = this.EditorGrid.getSelectionModel().getSelections();
                             this.exp.menu.items.items[1].setDisabled(s.length<=0);
                             this.exp.menu.items.items[3].setDisabled(s.length<=0);
                             this.exp.menu.items.items[5].setDisabled(s.length<=0);
                         },this);
                    }

                    this.printprv = printButton(this,"Opportunity",7);
                    optbutton.push(this.printprv);
                       /* this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        this.tbDefault.push(tbIndex);
                        tbIndex++;
                        */
                    this.tbarPrint.on('mouseover',function() {
                    	var s = this.EditorGrid.getSelectionModel().getSelections();
                        this.printprv.menu.items.items[1].setDisabled(s.length<=0);
                    }, this);
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.importt)) {
                    optbutton.push(this.importOpps);
                }
                this.toolbarItems.push(this.AdvanceSearchBtn = new Wtf.Toolbar.Button({
                    text : WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN"),//"Advanced Search",
                    id:'advanced7',// In use, Do not delete
                    scope : this,
                    tooltip:WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN.ttip"),//'Search for multiple terms in multiple fields.',
                    handler : this.configurAdvancedSearch,
                    iconCls : 'pwnd searchtabpane'
                }));
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;
            }
            if(this.newFlag==3){
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.del)) {
                    this.toolbarItems.push(this.deleteopp);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                this.tbarUnArchive = Wtf.archivedMenuButtonB(this,"Opportunity");
                this.toolbarItems.push(this.tbarUnArchive);
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
            }
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage)) {
            this.toolbarItems.push(this.recentopportunities);
            tbIndex++;
        }
        if(this.clearFlag!=undefined || this.subTab!=null){
            this.toolbarItems.push('->');
            var help=getHelpButton(this,7);
            this.toolbarItems.push(help);
        }
        this.spreadSheet = new Wtf.SpreadSheetGrid({
            cmArray:this.EditorColumnArray,
            store:this.EditorStore,
            moduleName : this.customParentModName,
            moduleid:5,
            isEditor:Wtf.isEditable(this.customParentModName,this),
            pagingFlag : true,
            quickSearchTF : this.quickSearchTF,
            parentGridObj : this,
            id:'OppGrid'+this.id,
            keyid:'oppid',
            updateURL:Wtf.req.springBase+'Opportunity/action/saveOpportunities.do',
            allowedNewRecord:!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage),
            archivedParentName:this.archivedParentName,
            listeners: {
                         "reloadexternalgrid": function(){
                             if(!this.searchparam)
                                this.reloadStore.defer(10,this);
                             else
                                this.showAdvanceSearch.defer(10,this);
                         },
                         scope : this
                       },
            scope : this
        });
        this.EditorGrid = this.spreadSheet.getGrid();
        this.EditorColumn = this.spreadSheet.getColModel();


       if(this.newFlag==2){               //same
            if(WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage)) {
//                this.EditorGrid.on("afteredit",this.fillGridValue,this);
//                this.EditorGrid.on("beforeedit",this.beforeEdit,this);
                this.EditorGrid.isEditor = false;
            }   
        }
        this.EditorStore.on("beforeload",function(){
        	if (!this.archivedFlag)
        		this.EditorStore.baseParams.isarchive = false;
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
        },this);
        this.EditorStore.on("loadexception",function(){
            Wtf.updateProgress();
           	if(this.loadCount <= 2){
           		this.EditorStore.reload();
           		this.loadCount++;
           	}
           	else
           		ResponseAlert(903);

        },this);
        this.EditorStore.on("load",function(){
        	if(this.loadCount !=undefined && this.loadCount != 0)
        		this.loadCount=0;
        	this.SelChange();
            Wtf.updateProgress();
        },this);        
     //   this.EditorGrid.on("validateedit",this.validateEdit,this);
        this.gridRowClick();
        cellClick(this);
        this.EditorGrid.getSelectionModel().on("rowselect",this.SelChange,this);
        this.EditorGrid.getSelectionModel().on("rowdeselect",this.SelChange,this);
        this.EditorGrid.on("sortchange",this.sortChange,this);
        this.EditorGrid.on("rowclick",this.gridCellClick,this);
        this.EditorGrid.on("mouseover",Wtf.hideNotes,this);
        this.EditorGrid.on("beforeupdate",function(e){
        	e.json["oppownerid"]=e.record.data.oppownerid;
        },this);
        this.tbarArchive.on('click',function() {
            if(this.deleteopp.disabled==false)
                this.tbarArchiveArray[0].setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.archiveBTN.ttip",params:[WtfGlobal.getLocaleText("crm.OPPORTUNITY")]}));//Archive the selected opportunity.");
            else
                this.tbarArchiveArray[0].setTooltip(WtfGlobal.getLocaleText("crm.editor.archiveBTN.disabled.ttip"));//"Select row(s) to send in Archive repository.");
        }, this);

    },

    /*
     *
     *  function reloadComboStores() called after successfully imports with master record created if not exist
     *
     */
    reloadComboStores : function () {
//        Wtf.parentaccountstore.load();
//        Wtf.productStore.load();
        Wtf.oppstageStore.load();
        Wtf.lsourceStore.load();
        Wtf.opptypeStore.load();
        Wtf.regionStore.load();
//        Wtf.currencyStore.load();
    },
    
    showAdvanceSearch:function(){
        showAdvanceSearch(this,this.searchparam);
    },
    reloadStore : function(){
            this.EditorStore=this.spreadSheet.getMySortconfig(this.EditorStore);
            if(this.EditorStore.baseParams && this.EditorStore.baseParams.searchJson){
                this.EditorStore.baseParams.searchJson="";
            }
            this.EditorStore.load({
                params:{
                    start:0,
                    limit:25,
                    mapid:this.mapid
                }
            });
    },
//    validateEdit:function(e){
//            if(e.field=="createdon" && e.value==""){
//                return false;
//            }
//            if(e.field=="closingdate" && e.value==""){
//                return false;
//            }
//},

    gridCellClick : function(grid, ri, e) {
        if(e.target.className == "clicktoshowcomment") {
            Wtf.onCellClickShowComments(grid.getStore().getAt(ri).data.oppid, this.customParentModName, ri, e);
        }
        if(e.target.className == "showMandatoryFields") {
            Wtf.emptyMandatoryFields(grid,ri);
        }
        this.gridRowClick();
    },

//    beforeEdit :function(e){
//        if(e.record.get('oppid')=="0" && e.record.get('validflag') != -1){
//            ResponseAlert(200);
//            return false;
//        }
//    },

    sortChange:function(grid,sortInfo, sortFlag){
        this.sortInfo = sortInfo;
    },
    storeLoad:function(){
       
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var fromdate="";
        var todate="";
        var report="crm";
        var flag = this.urlFlag;
        var exportUrl = Wtf.req.springBase+"Opportunity/action/opportunityExport.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        if (this.urlFlag==7) {
            var name="Opportunity";
			exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,flag,undefined,undefined,field,dir);
        } else {
            name="AccountOpportunity";
            exportWithTemplate(this,type,name,fromdate,todate,report+this.mapid,exportUrl,flag,undefined,undefined,field,dir);
        }
    },
    getDetailPanel:function()
    {
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.EditorGrid,
            Store:this.EditorStore,
            modulename:'opportunity',
            keyid:'oppid',
            height:200,
            mapid:5,
            id2:this.id,
            moduleName:'Opportunity',
            ownerid : 'oppownerid',
            moduleScope:this,
            detailPanelFlag:(this.archivedFlag==1?true:false),
            contactsPermission:this.contactsPermission
        });
    },
    
    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.spreadSheet.colArr,
            module : Wtf.crmmodule.opportunity,
            advSearch:false
        });
    },

    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.objsearchComponent.advSearch = true;
        this.objsearchComponent.cm = this.spreadSheet.colArr;
        this.objsearchComponent.getComboData();
        this.AdvanceSearchBtn.disable();
        this.doLayout();
    },
    clearStoreFilter:function(){
        this.searchJson="";
        this.EditorStore.baseParams = {
            flag:this.urlFlag,
            mapid:this.mapid,
            searchJson:this.searchJson,
            isarchive:this.newFlag==3?true:false
        }
        this.storeLoadWithSS();
        this.objsearchComponent.hide();
        this.AdvanceSearchBtn.enable();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;
        this.EditorStore.baseParams = {
            searchJson:this.searchJson,
            flag:this.urlFlag,
            mapid:this.mapid,
            isarchive:this.newFlag==3?true:false
        }
        this.storeLoadWithSS();
    },

    getStores:function(){


        /*var productcomboReader = new Wtf.data.Record.create([
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'unitprice',
            type: 'string'
        }
        ]);

        this.productStore = new Wtf.data.Store({

            url: Wtf.req.springBase+'Product/action/getProductname.do',
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, productcomboReader),
            autoLoad:false
        });*/

        /*this.parentaccountstore=new Wtf.data.Store({

            url: Wtf.req.springBase+'Opportunity/action/getAllAccounts.do',
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, Wtf.ComboReader),
            autoLoad:false
        });*/

        //this.parentaccountstore.load();
//        chkparentaccountstoreload();
        chkproductStoreload();
//        chkownerload(Wtf.crmmodule.opportunity);
//        chkoppstageload();
//        chkleadsourceload();
//        chkregionStoreload();
//        if(!Wtf.StoreMgr.containsKey("opportunitytype")){
//            Wtf.opptypeStore.load();
//            Wtf.StoreMgr.add("opportunitytype",Wtf.opptypeStore)
//        }
//        if(!Wtf.StoreMgr.containsKey("currencystore")){
//            Wtf.currencyStore.load();
//            Wtf.StoreMgr.add("currencystore",Wtf.currencyStore)
//        }

    },
    newGrid:function()
    {
        var newFlag=1;
    },
//    addNewRec:function (){
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage)) {
//            var gridRec={
//                validflag:-1,
//                oppid:'0',
//                oppownerid:'',
//                oppowner:'',
//                oppname:'',
//                relatedtoid:"None",
//                relatedtonameid:"",
//                closingdate:"",
//                oppstageid:'',
//                oppstage:'',
//                opptypeid:'',
//                opptype:'',
//                leadsourceid:'',
//                leadsource:'',
//                oppregionid:'',
//                oppregion:'',
//                probability:'',
//                currencyid:'',
//                createdon:'',
//                salesamount:'',
//                productserviceid:"",
//                accountnameid:(this.accFlag==0?"":this.mapid),
//                accountname:(this.accFlag==0?"":this.RelatedRecordName),
//                price:''
//            };
//            this.newRec = new this.EditorRec(this.spreadSheet.getEmptyCustomFields(gridRec));
//            this.EditorStore.insert(0, this.newRec);
//        }
//    },
//    fillGridValue:function (e){
//    	this.validSave(e.row,e.record,e.field,e);
//    },
    handleRecentOpportunities:function(){
    	this.EditorStore.extraSortInfo = {xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false};
        this.EditorStore.sort("updatedon","DESC");
    },

    gridRowClick:function(e,rowIndex) {
        WtfGlobal.enableDisableTbBtnArr(this.toolbarItems, this.EditorGrid, this.tbSingle, this.tbMulti, this.tbDefault);
        WtfGlobal.enableDisableTbBtnArr(this.tbarArchiveArray, this.EditorGrid, [0,1], [0,1], [1]);
        if(this.massUpdate!=undefined){
        	var sel = this.EditorGrid.getSelectionModel().getSelections();
        	var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        	if(sel!=undefined){
        		if(sel.length == 1 && selectedRec.data.oppid!="0" || sel.length>1)
        			this.massUpdate.enable();
        		else
        			this.massUpdate.disable();
        	}
        }
    },

    SelChange:function(){
        var s = this.EditorGrid.getSelectionModel().getSelections();
        var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        if(s.length == 1 && selectedRec.data.oppid!="0"){
            if(!this.detailPanel.ownerCt.collapsed)
            	getDocsAndCommentList(selectedRec, "oppid",this.id,undefined,'Opportunity',undefined,undefined,'oppownerid',this.contactsPermission,0);
            var updownCompE = Wtf.getCmp(this.id+'CRMupdownCompo');
		    var enableContactsButton=true;
		    var isMainOwner = selectedRec.get('oppownerid') == loginid;
            docCommentEnable(this,updownCompE,isMainOwner,enableContactsButton);
            enableButt(this,updownCompE,isMainOwner,enableContactsButton);
            this.tbarShowActivity.enable();
        } else {
            this.tbarShowActivity.disable();
            docCommentDisable(this);
            disableButt(this);
        }
        if(this.massUpdate!=undefined){
        	if(s.length == 1 && selectedRec.data.oppid!="0" || s.length>1)
        		this.massUpdate.enable();
        	else
        		this.massUpdate.disable();
        }
        this.gridRowClick();
        if(this.deleteopp.disabled==false)
            this.deleteopp.setTooltip('Delete the selected opportunities.');
        else
            this.deleteopp.setTooltip(WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip"));//'Select row(s) to delete');
        if(this.tbarShowActivity.disabled==false && this.newFlag!=3) {
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.addactivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.OPPORTUNITY")]}));//'Add activity details for the selected opportunity.');
        } else if(this.newFlag!=3){
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.disabledaddactivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.OPPORTUNITY")]}));//'Select an opportunity to add its activity details.');
        }
        if(s.length == 1){
            if((selectedRec.get("accountnameid") == "" || selectedRec.get("accountnameid") == "99") && this.showcontactsButton) {
                this.showcontactsButton.setDisabled(true);
            }
        }
    },

/*
 *  function comboLoad() Not in Used
 */
//    comboLoad:function (e){
//        if(e.field=='relatedtonameid'){
//            this.relatedToNameStore.load({
//                params:{
//                    relatedtoid:e.record.get('relatedtoid')
//                }
//            })
//        }
//    },

//    validSave:function(rowindex,record,field, e){
//        var modifiedRecord=this.EditorStore.getModifiedRecords();
//        var flag=false;
//        if(modifiedRecord.length<1){
//            WtfComMsgBox(100,0);
//            return false;
//        }
//        this.saveData(rowindex,record,field,e);
//    },
//    saveData:function(rowindex,record,field,e){
//        var rData = record.data;
//        var auditStr=Wtf.SpreadSheet.constructAuditStr(this.EditorGrid,rowindex, e.column, e.value, e.originalValue,record);
//
//        var jsondata = "";
//        var validFlag=1;
//
////        if(rData.oppname.trim()==""||rData.accountnameid.trim()=="" || rData.oppstageid.trim()=="" || rData.closedate =="")
////        {
////            validFlag=0;
////        }
//
//        var temp=rData.oppownerid;
//        if(temp=="")
//        {
//            record.set('oppownerid',loginid);
//            record.set('oppowner',_fullName);
//        }
//
//        var columnarray = this.spreadSheet.getGrid().colModel.config;
//        for(var ctr=0;ctr<columnarray.length;ctr++){
//            if(columnarray[ctr].mandatory){
//                  if(rData[columnarray[ctr].dataIndex]==" " || rData[columnarray[ctr].dataIndex]=="" || ( columnarray[ctr].xtype=="textfield" && rData[columnarray[ctr].dataIndex].trim()=="")||(columnarray[ctr].xtype=="combo"&&rData[columnarray[ctr].dataIndex]=="99")){
//                      if(columnarray[ctr].id=="probability" && rData[columnarray[ctr].dataIndex]=="0" ){
//                          validFlag=1;
//                      }else{
//                        validFlag=0;
//                        break;
//                      }
//                 }
//            }
//
//        }
//
//        // account can be changed even opportunity tab is opened from perticular account
//        if(this.urlFlag==62)
//        {
////            rData.accountnameid=this.mapid;
//        }
//       if(rData.valiflag != validFlag) record.set('validflag',validFlag);
//
//        jsondata+='{"oppid":"' + rData.oppid + '",';
//        jsondata+='"oppownerid":"' +rData.oppownerid+ '",';
//        jsondata+='"oppname":"' +rData.oppname+ '",';
//        jsondata+='"relatedtoid":"' +rData.relatedtoid+ '",';
//        jsondata+='"relatedtonameid":"' +rData.relatedtonameid+ '",';
//        jsondata+='"auditstr":"' +auditStr+ '",';
//        jsondata+='"closingdate":' + (rData.closingdate.getTime?rData.closingdate.getTime():new Date().getTime()) + ',';
//        jsondata+='"oppstageid":"' +rData.oppstageid +'",';
//        jsondata+='"opptypeid":"' + rData.opptypeid + '",';
//        jsondata+='"oppregionid":"' + rData.oppregionid + '",';
//        jsondata+='"leadsourceid":"' + rData.leadsourceid+ '",';
//        jsondata+='"probability":"' +rData.probability + '",';
//        jsondata+='"currencyid":"' + rData.currencyid + '",';
//        jsondata+='"salesamount":"' + rData.salesamount + '",';
//        jsondata+='"createdon":' + (rData.createdon.getTime?rData.createdon.getTime():new Date().getTime())+ ',';
//        jsondata+='"productserviceid":"' +rData.productserviceid + '",';
//        jsondata+='"validflag":"' +validFlag+ '",';
//        jsondata+='"accountnameid":"' + rData.accountnameid + '",';
//        jsondata+='"dirtyfield":"' + field + '",';
//        jsondata+='"price":"' + rData.price + '"';
//        jsondata+=this.spreadSheet.getCustomColumnData(rData,false);
//        jsondata+= '},';
//        var trmLen = jsondata.length - 1;
//        var finalStr = jsondata.substr(0,trmLen);
//        rData.validflag=validFlag;
//        if(rData.createdon==""){
//              var dates=new Date();
//                 record.set('createdon',dates);
//        }
//        Wtf.Ajax.requestEx({
//
//            url: Wtf.req.springBase+'Opportunity/action/saveOpportunities.do',
//            params:{
//                jsondata:finalStr,
//                type:this.newFlag,////////type for new or edit
//                flag:23/////////////flag for case
//            }
//        },
//        this,
//        function(res)
//        {
//            rData.oppid=res.ID;
//            this.afterValidRecordSaved(undefined, undefined, validFlag);
////            if(validFlag==1) {
////                bHasChanged = true;
////                var obj=Wtf.getCmp(Wtf.moduleWidget.opportunity);
////                if(obj!=null){
////                    obj.callRequest("","",0);
////                    Wtf.refreshUpdatesAll();
////                }
////            }
////            if(validFlag==1){
////                bHasChanged = true;
////            }
////            if((this.urlFlag==62) && (field == 'accountnameid')) {
////                this.storeLoadWithSS();
////            }
////
////            if((field=='oppname' || field=='accountnameid') && validFlag==1)
////                Wtf.getCmp("tree").getLoader().load(Wtf.getCmp("tree").root);
//        },
//        function(res)
//        {
//            WtfComMsgBox(102,1);
//
//        }
//        )
//    },
    
    afterValidRecordSaved : function (res,finalStr,validFlag) {
        if(validFlag==1) {
            bHasChanged = true;
            var obj=Wtf.getCmp(Wtf.moduleWidget.cases);
            if(obj!=null){
                obj.callRequest("","",0);
            }
            Wtf.refreshUpdatesAll();
        }
    },

    opportunityDelete:function()
    {
        Wtf.deleteGlobal(this.EditorGrid,this.EditorStore,'Opportunities',"oppid","oppid",'Opportunity',32,33,34);
    },

    /*load grid store with search parameters*/
    storeLoadWithSS : function() {
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
    },

    ArchiveHandler:function(a) {
        if(this.parentId == undefined) {
            var data={
                a:a,
                tbarArchive:this.tbarArchive,
                EditorGrid:this.EditorGrid,
                title:WtfGlobal.getLocaleText("crm.OPPORTUNITY"),//'Opportunity',
                plural:'Opportunities',
                keyid:'id',
                valueid:"oppid",
                table:'Opportunity',
                GridId:'OppGrid',
                homePanId:'OpportunityHomePanel',
                archivedPanId:'OppArchivePanel',
                name:"name",
                valueName:"oppname"
            }
        } else {
            var temp=this.parentId.split('OppGrid');
            if(this.parenturlFlag==62) {
                data={
                    a:a,
                    tbarArchive:this.tbarArchive,
                    EditorGrid:this.EditorGrid,
                    title:WtfGlobal.getLocaleText("crm.OPPORTUNITY"),//'Opportunity',
                    keyid:'id',
                    plural:'Opportunities',
                    valueid:"oppid",
                    table:'Opportunity',
                    GridId:'OppGrid',
                    homePanId:temp[1],
                    archivedPanId:'OppArchivePanel'+this.mapid,
                    name:"name",
                    valueName:"oppname"
                }
            } else {
                data={
                    a:a,
                    tbarArchive:this.tbarArchive,
                    EditorGrid:this.EditorGrid,
                    title:WtfGlobal.getLocaleText("crm.OPPORTUNITY"),//'Opportunity',
                    keyid:'id',
                    plural:'Opportunities',
                    valueid:"oppid",
                    table:'Opportunity',
                    GridId:'OppGrid',
                    homePanId:'OpportunityHomePanel',
                    archivedPanId:'OppArchivePanel'+this.mapid,
                    name:"name",
                    valueName:"oppname"
                }
            }
        }
        var mod = "CrmOpportunity";
        var audit = "75";
        var auditMod = "Opportunity";
        data["ownerName"] = "oppownerid";
        Wtf.ArchivedGlobal(data, mod, audit,auditMod);
    },

    showcontacts:function()
    {
        this.selectedarray=this.EditorGrid.getSelectionModel().getSelections();
        this.rec=this.EditorGrid.getSelectionModel().getSelected()
        if(this.selectedarray.length==1) {
            var recData = this.rec.data;
            var tipTitle=recData.oppname+"'s Contacts";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var id=recData.oppid;
            var accid=recData.accountnameid;
            var leadSourceId = recData.leadsourceid;
            var panel=Wtf.getCmp('contactOppTab'+id);
            var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
            if(panel==null)
            {
                panel= new Wtf.contactEditor({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Opportunity'>"+title+"</div>",
                    id:'contactOppTab'+id,
                    scope:this,
                    closable:true,
                    RelatedRecordName:recData.accountname,
                    modName : "OpportunityContact",
                    customParentModName : "Contact",
                    mainselectedRec:selectedRec,
                    keyid:'oppid',
                    mainId:this.id,
                    mainOwnerid:'oppownerid',
                    mainContactpermossion:this.contactsPermission,
                    iconCls:getTabIconCls(Wtf.etype.contacts),
                    mapid:accid,
                    relatedName:'Opportunity',
                    newFlag:2,
                    urlFlag:60,
                    subTab:true,
                    layout:'fit',
                    submainTab:this.submainTab!=undefined?this.submainTab:this.mainTab,
                    leadsourceid: leadSourceId,
                    leadsource : recData.leadsource
                });
                if(this.submainTab!=undefined)
                    this.submainTab.add(panel);
                else
                    this.mainTab.add(panel);
            }
            if(this.submainTab!=undefined){
                this.submainTab.setActiveTab(panel);
                this.submainTab.doLayout();
            } else{
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();
            }
        }
        else
        {
            WtfComMsgBox(109,0);
        }
    },

    showArchived:function()
    {

        var panel=Wtf.getCmp('OppArchivePanel'+this.mapid);
        this.archiveUrlFlag=7;
        var tipTitle=WtfGlobal.getLocaleText({key:"crm.editor.archivedwin.title",params:[WtfGlobal.getLocaleText("crm.OPPORTUNITY")]});//"Archived Opportunities";
        var qtitle=WtfGlobal.getLocaleText("crm.editor.archivewin.title");//"Archived";
        if(this.relatedName=="Account"){
            this.archiveUrlFlag=62;
            tipTitle=this.RelatedRecordName+"'s Archived Opportunities";
            qtitle="Account";
        }
        var title = Wtf.util.Format.ellipsis(tipTitle,18);
        if(panel==null)
        {
            panel=new Wtf.opportunityEditor({
                border:false,
                title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+qtitle+"'>"+title+"</div>",
                layout:'fit',
                closable:true,
                id:'OppArchivePanel'+this.mapid,
                modName : "OpportunityArchived",
                customParentModName : "Opportunity",
                newFlag:3,
                parenturlFlag:this.urlFlag,
                parentId:this.EditorGrid.getId(),
                iconCls:getTabIconCls(Wtf.etype.archived),
                urlFlag:this.archiveUrlFlag,
                mapid:this.mapid,
                archivedFlag:1,
                mainTab:this.submainTab!=undefined?this.submainTab:this.mainTab,
                archivedParentName:"Opportunity",
                subTab:true,
                submainTab:this.submainTab!=undefined?this.submainTab:this.mainTab
            })
            if(this.submainTab!=undefined)
               this.submainTab.add(panel);
            else
               this.mainTab.add(panel);
        }
        if(this.submainTab!=undefined){
            this.submainTab.setActiveTab(panel);
            this.submainTab.doLayout();
        } else{
            this.mainTab.setActiveTab(panel);
            this.mainTab.doLayout();
        }
    },

    showActivity:function()
    {
    	if(this.EditorGrid.getSelectionModel().getCount()==1){
        	this.rec=this.EditorGrid.getSelectionModel().getSelected();
        	var id=this.rec.data. oppid;
             if(id=="0"){
            	WtfComMsgBox(25);
            	return;
            }
            var oppname = this.rec.data.oppname;
            var titlename = WtfGlobal.getLocaleText("crm.OPPORTUNITY");//"Opportunity";
            if(oppname.trim()!=""){
                titlename = oppname;
            }
            var tipTitle=titlename+"'s Activity";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var id=this.rec.data.oppid;
            var panel=Wtf.getCmp(this.id+'activityOppTab'+id);
            var newpanel = true;
            if(panel==null)
            {
                panel= new Wtf.activityEditor({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Opportunity'>"+title+"</div>",
                    id:this.id+'activityOppTab'+id,
                    modName : "OpportunityActivity",
                    layout:'fit',
                    border:false,
                    closable:true,
                    scope:this,
                    urlFlag:153,
                    iconCls:getTabIconCls(Wtf.etype.todo),
                    RelatedRecordName:titlename,
                    Rrelatedto:'Opportunity',
                    customParentModName : "Activity",
                    relatedtonameid:id,
                    highLightId:this.activityId,
                    newFlag:2,
                    subTab:true,
                    mainTab:this.submainTab!=undefined?this.submainTab:this.mainTab
                });
                if(this.submainTab!=undefined)
                    this.submainTab.add(panel);
                else
                    this.mainTab.add(panel);
                newpanel = undefined;
            }
            if(this.activityId && panel){
          	   panel.on("render",function(p){
          		   panel.EditorStore.on("load",function(){
          			   panel.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(panel.initialConfig.highLightId,panel.EditorStore,"activityid"));
          		   },panel.EditorStore);
          	   },panel);
            }
            if(this.submainTab!=undefined){
                this.submainTab.setActiveTab(panel);
                this.submainTab.doLayout();
            } else{
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();
            }
        }
        else if (this.fromExternalLink)
        {
            WtfComMsgBox(16);
        }
        else
        {
            WtfComMsgBox(109,0);
        }
    },
    exportfile: function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var fromdate="";
        var todate="";
        var report="crm";
        var flag = this.urlFlag;
        var exportUrl = Wtf.req.springBase+"Opportunity/action/opportunityExport.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        if (this.urlFlag==7) {
            var name="Opportunity";
			exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,flag,undefined,undefined,field,dir);
        } else {
            name="AccountOpportunity";
            exportWithTemplate(this,type,name,fromdate,todate,report+this.mapid,exportUrl,flag,undefined,undefined,field,dir);
        }

    },
    exportSelected: function(type) {
        var report="crm"
        var flag = this.urlFlag;
        if (this.urlFlag==7) {
            var name="Opportunity";
        } else {
            name="AccountOpportunity";
            report += this.mapid;
        }
        var fromdate="";
        var todate="";
        var selArr = [];
        var exportUrl = Wtf.req.springBase+"Opportunity/action/opportunityExport.do";
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        selArr = this.EditorGrid.getSelectionModel().getSelections();
        var jsondata = "";
        for(var i=0;i<selArr.length;i++)
        {
            if(selArr[i].get("validflag") != -1 && selArr[i].get("validflag") != 0) {
                jsondata+="{'id':'" + selArr[i].get('oppid') + "'},";
            }
        }
        if(jsondata.length > 0) {
            var trmLen = jsondata.length - 1;
            var finalStr = jsondata.substr(0,trmLen);
            exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,flag,undefined,finalStr,field,dir);
        } else {
            if(type=='print')
                ResponseAlert(553);
            else
                ResponseAlert(552);
        }
    },
    temp: function(){
        Wtf.highLightGlobal(this.highLightId,this.EditorGrid,this.EditorStore,"oppid");
   }
});
