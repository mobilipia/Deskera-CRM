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
Wtf.Frame = function(config) {
	if(config.items) delete config.items;
	if(config.html) {
		config.frameHtml = config.html;
		delete config.html;
	}
	Wtf.EditorWindow.superclass.constructor.call(this,config);
};
Wtf.extend(Wtf.Frame,Wtf.Panel, {
	afterRender:function(container){
		Wtf.Frame.superclass.afterRender.call(this, container);
		var iframe = this.body.dom.firstChild;
        var iframe = document.createElement('iframe');
        iframe.name = Wtf.id();
        iframe.frameBorder = '0';
        iframe.style.overflow = "auto";
        iframe.style.width = "100%";
        iframe.style.height = "100%";
        iframe.src="about:blank";
        this.body.dom.appendChild(iframe);
	    iframe = (iframe.contentWindow) ? iframe.contentWindow : (iframe.contentDocument.document) ? iframe.contentDocument.document : iframe.contentDocument;
	    this.doc =  iframe.document;
	    if(this.frameHtml){
	    	this.update(this.frameHtml);
	    }
	},

	update:function(content){
		if(this.doc){
		    this.doc.open();
		    this.doc.write(content);
		    this.doc.close();
		}
	}
});

/** *********************************************************************************************** */
/* 							Wtf.EditorWindow component 										       	*/
/** *********************************************************************************************** */

Wtf.EditorWindow = function(conf) {
    Wtf.apply(this, conf);
    this.addEvents({
        "okClicked": true
    });
    Wtf.EditorWindow.superclass.constructor.call(this, {
        width: 820,
        height: 600,
        resizable: false,
        iconCls: "pwnd favwinIcon",
        layout: "fit",
        title: (this.title && this.title != "") ? this.title : WtfGlobal.getLocaleText("crm.template.edityourcontent"),//"Edit Your Content",
        modal: true,
        buttons: [{
            text: WtfGlobal.getLocaleText("crm.OK"),//"OK",
            scope: this,
            handler: this.okClicked
        }, {
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//"Cancel",
            scope: this,
            handler: this.cancelClicked
        }]
    });
};

Wtf.extend(Wtf.EditorWindow, Wtf.Window, {
    onRender: function(conf) {
        Wtf.EditorWindow.superclass.onRender.call(this, conf);
        var _iArr = [];
        this.createEditor();
        this.createVariableStores();
       
        if(this.headerImage) {
            var _iRec = Wtf.data.Record.create([{
                name: "id"
            },{
                name: "name"
            },{
                name: "url"
            },{
                name: "height"
            }]);
            var _is = new Wtf.data.Store({
                url: Wtf.req.springBase+'emailMarketing/action/getThemeImages.do',
                reader: new Wtf.data.KwlJsonReader({
                    root: "data"
                }, _iRec)
            });
            _is.load();
            var _iCM = new Wtf.grid.ColumnModel([{
                header: WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//"Name",
                dataIndex: "name"
            }]);
            var _iSM = new Wtf.grid.RowSelectionModel({
                singleSelect: true
            });
            _iSM.on("rowSelect", function(obj, ri, rec){
                document.getElementById("email_campaign_header_image").src = rec.data.url;
            }, this);
            this.imageGrid = new Wtf.grid.GridPanel({
                store: _is,
                cm: _iCM,
                height: 200,
                cls: "noborderGrid",
                layout: "fit",
                sm: _iSM,
                border: false,
                viewConfig: {
                    forceFit: true
                }
            });
            _iArr[_iArr.length] = new Wtf.Panel({
                layout: "column",
                region: "north",
                border: false,
                height: 200,
                items: [{
                    border: false,
                    height: 200,
                    columnWidth: 0.2,
                    items: this.imageGrid
                },{
                    columnWidth: 0.79,
                    autoScroll: true,
                    border: false,
                    height: 200,
                    bodyStyle: "text-align: center",
                    html: "<img id='email_campaign_header_image' style='margin-top: 5px' src='' />"
                }]
            });
        }
        
        _iArr[_iArr.length] = new Wtf.Panel({
            layout: "form",
            border:false,
            region: "center",
            items: [this.mce,{
                        xtype:"fieldset",
                        id:this.id+'paramConfig',
                        disabled:true,
                        title:WtfGlobal.getLocaleText("crm.template.parameterconfig"),//"Parameter Configuration",
                        height:110,
                        items:[
                        {
                            layout : 'column',
                            border : false,
                            items: [{
                                columnWidth: '.40',
                                layout : 'form',
                                border : false,
                                items : [this.param1Combo = new Wtf.form.ComboBox({
                                            fieldLabel:WtfGlobal.getLocaleText("crm.template.parametertype"),//"Parameter Type ",
                                            store:this.paramtypestore,
                                            name:"combovalue",
                                            displayField:'name',
                                            valueField:'id',
                                            editable:false,
                                            hiddenName:"param_type",
                                            mode:'local',
                                            triggerAction:'all',
                                            id:this.addNewDashboardCall?"param_type_combo_dash_addnew":(this.dashboardCall==true?'param_type_combo_dash'+this.templateid:'param_type_combo'+this.templateid)
                                        }),this.param2Combo = new Wtf.form.ComboBox({
                                            xtype:"combo",
                                            fieldLabel:WtfGlobal.getLocaleText("crm.template.parametervalue"),//"Parameter Value ",
                                            name:"param_value",
                                            store:this.paramvaluestore,
                                            editable:false,
                                            mode:'local',
                                            displayField:'name',
                                            valueField:'id',
                                            id:this.addNewDashboardCall?"param_value_combo_dash_addnew":(this.dashboardCall==true?'param_value_combo_dash'+this.templateid:'param_value_combo'+this.templateid),
                                            triggerAction:'all',
                                            hiddenName:"param_value"
                                        }),{
                                            xtype:"button",
                                            border:false,
                                            minWidth:80,
                                            text:WtfGlobal.getLocaleText("crm.template.insertbtn"),//"Insert",
                                            scope:this,
                                            handler:function(){

                                				var type= this.param1Combo;
                            					var strdata=this.getDefaultRecVal(true);
                                                this.mce.insertAtCursor(strdata);
                                                ResponseAlert(68);
                                                this.conditionTypeCombo.reset();
                                            }
                                        }]
                            },{columnWidth: '.1',
                                layout : 'form',
                                border : false,
                                items : [{
                                	 xtype: "panel",
                                     border: false,
                                     height: 27,
                                     html:"<img src='images/help.png' title='Click here' onclick = 'showHelp(76)' >",
                                     id: "tmpltparametertypehelp"
                                     } ,{
                                	 xtype: "panel",
                                     border: false,
                                    html:"<img src='images/help.png' title='Click here' onclick = 'showHelp(75)' >",
                                    id: "tmpltparametervaluehelp"
                                   }]
                            
                            },{
                                columnWidth: '.42',
                                layout : 'form',
                                border : false,
                                items : [new Wtf.Panel({
                                            layout:'column',
                                            hidden:!this.defaultValues,
                                            border:false,
                                            defaults:{
                                                border:false
                                            },
                                            items: [{
                                                columnWidth: .9,
                                                layout:'form',
                                                items:[this.defaultValue=new Wtf.ux.TextField({
                                                    fieldLabel:WtfGlobal.getLocaleText("crm.template.defaultvalue"),//'Default Value',
                                                    id:'defaultvalue_textfield'+this.id,
                                                    width:182,
                                                    hidden:!this.defaultValues,
                                                    hideLabel:!this.defaultValues,
                                                    disabled:true,
                                                    xtype:'striptextfield'
                                                })]
                                            },{
                                                html:"&nbsp&nbsp&nbsp&nbsp<img src='images/help.png' title='Click here' onclick = 'showHelp(74)' >",
                                                id: "tmpltdefvalhelp",
                                                columnWidth: .1
                                            }]
                                        }),new Wtf.Panel({
                                            layout:'column',
                                            border:false,
                                            defaults:{
                                                border:false
                                            },
                                            items: [{
                                                columnWidth: .9,
                                                layout:'form',
                                                items:[this.conditionTypeCombo = new Wtf.form.ComboBox({
                                                    xtype:"combo",
                                                    fieldLabel:WtfGlobal.getLocaleText("crm.template.conditiontype"),//"Condition Type ",
                                                    name:"condition_type",
                                                    store:this.conditiontypestore,
                                                    editable:false,
                                                    mode:'local',
                                                    displayField:'name',
                                                    valueField:'id',
                                                    id:"condition_type_combobox",
                                                    triggerAction:'all',
                                                    hiddenName:"condition_type"
                                                })]
                                            },{
                                                html:"&nbsp&nbsp&nbsp&nbsp<img src='images/help.png' title='Click here' onclick = 'showHelp(73)' >",
                                                id: "tmpcondtypehelp",
                                                columnWidth: .1
                                            }]
                                        })]
                            }]
                        }]
                    }]
         });
        this.add(new Wtf.Panel({
            layout: "border",
            border: false,
            items: _iArr
        }));

        this.param1Combo.on("select",function(c, rec){
            this.param2Combo.clearValue();
            this.param2Combo.setValue("");
            this.defaultValue.setValue("");
            this.defaultValue.disable();
        },this);
        this.param2Combo.on("select",function(){
        	var val = this.param1Combo.getValue();
            if(val=='')
            {
            	this.param2Combo.setValue("");
            	 WtfComMsgBox(950,0);
            	 return;
            }
            if(!this.defaultStore){
                return;
            }
            this.defaultValue.enable();

            var strdata=this.getDefaultRecVal(false);

            var index = this.defaultStore.find("varname",strdata);
            this.defaultValue.setValue("");
            if(index>-1){
                var rec = this.defaultStore.getAt(index);
                this.defaultValue.setValue(rec.data.varval);
            }
        },this);

        this.defaultValue.on("change",function(){
            if(!this.defaultStore){
                return;
            }

            var strdata=this.getDefaultRecVal(false);

            var index = this.defaultStore.find("varname",strdata);
            if(index>-1){
                var rec = this.defaultStore.getAt(index);
                var newVal = this.defaultValue.getValue();
                if(newVal.trim()!=""){
                    rec.set("varval",newVal);
                } else {
                    this.defaultStore.remove(rec);
                }

            } else{
                var newDefaultRec = new this.defaultRecord({
                    varname:strdata,
                    varval:this.defaultValue.getValue()
                });
            	this.defaultValues.push(newDefaultRec.data);
                this.defaultStore.add(newDefaultRec);

            }
        } ,this);
        
        this.param2Combo.on("expand", function(obj, rec){
            var val = this.param1Combo.getValue();
            this.paramvaluestore.filter("group", val);
        }, this);
    },
    
   
    createVariableStores:function(){
    	if(this.defaultValues){   	
	        this.defaultRecord = Wtf.data.Record.create([{
	            name: "varname"
	        },{
	            name: "varval"
	        }]);
			  		
			this.defaultStore = new Wtf.data.Store({
				reader: new Wtf.data.JsonReader({
	            }, this.defaultRecord)
			
		    });
			this.defaultStore.loadData(this.defaultValues);
    	}
		
    	var typeData = [], valData=[];
    	for(var i=0;i<this.tplVariables.length;i++){
            typeData.push([this.tplVariables[i].gid, this.tplVariables[i].gname]);
            for(var j=0; j<this.tplVariables[i].gvars.length;j++){
            	valData.push([this.tplVariables[i].gvars[j].id,this.tplVariables[i].gvars[j].name,this.tplVariables[i].gid]);
            }
        }
    	
    	this.paramtypestore = new Wtf.data.SimpleStore({
            fields :['id', 'name'],
            data:typeData
        });
    	
    	 this.paramvaluestore = new Wtf.data.SimpleStore({
            fields: ['id', 'name', 'group'],
            data: valData            
        });	 
    	
        this.conditiontypestore = new Wtf.data.SimpleStore({
            fields :['id', 'name', 'group'],
            data:[['nullorempty','Null or Empty', '1'],['notnullorempty','Not Null or Empty', '1']]
        });
    },
    
    createEditor: function(){
        this.mce = new Wtf.form.HtmlEditor({
            value: this.val,
            width:806,
            height:400,
            hideLabel:true,
            plugins: [
	            new Wtf.ux.form.HtmlEditor.insertImage({
	                imageStoreURL: Wtf.req.springBase+'emailMarketing/action/getEmailTemplateFiles.do?type=img',
	                imageUploadURL: Wtf.req.springBase+'emailMarketing/action/saveEmailTemplateFiles.do?type=img'
	            }),
	            new Wtf.ux.form.HtmlEditor.HR({}),
	            new Wtf.ux.form.HtmlEditor.SpecialCharacters({})
            ]
        });

        this.mce.on('activate',function(){
            Wtf.getCmp(this.id+'paramConfig').setDisabled(false);
        },this);
    },
    
    showTagList: function(){
        var tagStore = new Wtf.data.SimpleStore({
            fields: ["id", "value", "notation"],
            data: [["0", "Email Address", "{emailid}"], ["1", "First Name", "{firstname}"], ["2", "Last Name", "{lastname}"]/*,
                ["3", "Unsubscribe Link", "{unsubscribelink}"], ["4", "Forward To A Friend Link", "{fwdtofriendlink}"], ["5", "Update Profile Link", "{updateprofile}"]*/]
        });
        var tagCM = new Wtf.grid.ColumnModel([{
            header: WtfGlobal.getLocaleText("crm.template.toinsert"),//"To Insert",
            dataIndex: "value"
        }, {
            header: WtfGlobal.getLocaleText("crm.template.usethis"),//"Use This",
            dataIndex: "notation"
        }]);
        var tagGrid = new Wtf.grid.GridPanel({
            layout: "fit",
            cm: tagCM,
            store: tagStore,
            viewConfig: {
                forceFit: true
            }
        });
        var metaTagList = new Wtf.Window({
            title:WtfGlobal.getLocaleText("crm.template.taglist"),// "Tag list",
            layout: "fit",
            modal: true,
            resizable: false,
            height: 250,
            width: 440,
            buttons: [{
                text: WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),//"Close",
                handler: function(){
                    metaTagList.close();
                }
            }],
            items: tagGrid
        });
        metaTagList.show();
    },

    okClicked: function(obj) {
            if(this.fireEvent("okClicked", this))
                this.close();
    },
    cancelClicked: function(obj) {
        this.close();
    },
    getEditorVal: function(){
        var valObj = {};
        valObj["textVal"] = this.mce.getValue();
        if(this.headerImage) {
            valObj["imageRec"] = this.imageGrid.getSelectionModel().getSelected();
        }
        return valObj;
    },

    getDefaultRecVal: function(insertBtnFlag){
        var paramtypeval="";
        var paramvalueval="";
        if(this.addNewDashboardCall){
            paramtypeval = Wtf.getCmp("param_type_combo_dash_addnew").getValue();
            paramvalueval = Wtf.getCmp("param_value_combo_dash_addnew").getValue();
        } else {
            if(this.dashboardCall){
                paramtypeval = Wtf.getCmp("param_type_combo_dash"+this.templateid).getValue();
                paramvalueval = Wtf.getCmp("param_value_combo_dash"+this.templateid).getValue();
            } else{
                paramtypeval = Wtf.getCmp("param_type_combo"+this.templateid).getValue();
                paramvalueval = Wtf.getCmp("param_value_combo"+this.templateid).getValue();
            }
        }

        if(paramtypeval=="company" && paramvalueval=="rname")
            paramtypeval = "mailrecipient";
        if(insertBtnFlag){
            this.isClosable=false;
        }
        if(paramtypeval.trim() == "" || paramvalueval.trim() == ""){
            if(insertBtnFlag){
                this.isClosable=true;
            }
            WtfComMsgBox(950,0);
            return;
        }
        var cond=this.conditionTypeCombo.getValue().trim();

        var strdata = paramtypeval+":"+paramvalueval;
        if(insertBtnFlag){

            if(cond != ""){
            	strdata = "#condition:"+cond+" var:"+strdata+"#<br><span style='color: rgb(150,150,150);'>Write your text here</span><br>#condition#";
            }else{
            	strdata = "#"+strdata+"#";
            }
        }

        return strdata;
    }
});

Wtf.ns('Wtf.ux.form.HtmlEditor');


/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.MidasCommand component 							*/
/** *********************************************************************************************** */


Wtf.ux.form.HtmlEditor.MidasCommand = Wtf.extend(Wtf.util.Observable, {
    init: function(cmp){
        this.cmp = cmp;
        this.btns = [];
        this.cmp.on('render', this.onRender, this);
        this.cmp.on('initialize', this.onInit, this, {
            delay: 100,
            single: true
        });
    },
    onInit: function(){
        Wtf.EventManager.on(this.cmp.getDoc(), {
            'mousedown': this.onEditorEvent,
            'dblclick': this.onEditorEvent,
            'click': this.onEditorEvent,
            'keyup': this.onEditorEvent,
            buffer: 100,
            scope: this
        });
    },
    onRender: function(){
        var midasCmdButton, tb = this.cmp.getToolbar(), btn;
        Wtf.each(this.midasBtns, function(b){
            if (Wtf.isObject(b)) {
                midasCmdButton = {
                    iconCls: 'x-edit-' + b.cmd,
                    handler: function(){
                        this.cmp.relayCmd(b.cmd);
                    },
                    scope: this,
                    tooltip: b.tooltip ||
                    {
                        title: b.title
                    },
                    overflowText: b.overflowText || b.title
                };
            } else {
                midasCmdButton = new Wtf.Toolbar.Separator();
            }
            btn = tb.addButton(midasCmdButton);
            if (b.enableOnSelection) {
                btn.disable();
            }
            this.btns.push(btn);
        }, this);
    },
    onEditorEvent: function(){
        var doc = this.cmp.getDoc();
        Wtf.each(this.btns, function(b, i){
            if (this.midasBtns[i].enableOnSelection || this.midasBtns[i].disableOnSelection) {
                if (doc.getSelection) {
                    if ((this.midasBtns[i].enableOnSelection && doc.getSelection() !== '') || (this.midasBtns[i].disableOnSelection && doc.getSelection() === '')) {
                        b.enable();
                    } else {
                        b.disable();
                    }
                } else if (doc.selection) {
                    if ((this.midasBtns[i].enableOnSelection && doc.selection.createRange().text !== '') || (this.midasBtns[i].disableOnSelection && doc.selection.createRange().text === '')) {
                        b.enable();
                    } else {
                        b.disable();
                    }
                }
            }
            if (this.midasBtns[i].monitorCmdState) {
                b.toggle(doc.queryCommandState(this.midasBtns[i].cmd));
            }
        }, this);
    }
});




/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.Divider component 								*/
/** *********************************************************************************************** */
Wtf.ux.form.HtmlEditor.Divider = Wtf.extend(Wtf.util.Observable, {
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        this.cmp.getToolbar().addButton([new Wtf.Toolbar.Separator()]);
    }
});




/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.IndentOutdent  component 						*/
/** *********************************************************************************************** */
Wtf.ux.form.HtmlEditor.IndentOutdent = Wtf.extend(Wtf.ux.form.HtmlEditor.MidasCommand, {
    midasBtns: ['|', {
        cmd: 'indent',
        tooltip: {
            title: WtfGlobal.getLocaleText("crm.template.indenttext")//'Indent Text'
        },
        overflowText: WtfGlobal.getLocaleText("crm.template.indenttext")//'Indent Text'
    }, {
        cmd: 'outdent',
        tooltip: {
            title: WtfGlobal.getLocaleText("crm.template.outdenttext")//'Outdent Text'
        },
        overflowText: WtfGlobal.getLocaleText("crm.template.outdenttext")//'Outdent Text'
    }]
});


/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.RemoveFormatt  component 						*/
/** *********************************************************************************************** */
Wtf.ux.form.HtmlEditor.RemoveFormat = Wtf.extend(Wtf.ux.form.HtmlEditor.MidasCommand, {
    midasBtns: ['|', {
        enableOnSelection: true,
        cmd: 'removeFormat',
        tooltip: {
            title: WtfGlobal.getLocaleText("crm.template.removeformatting")//'Remove Formatting'
        },
        overflowText: WtfGlobal.getLocaleText("crm.template.removeformatting")//'Remove Formatting'
    }]
});

/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.SubSuperScript  component 						*/
/** *********************************************************************************************** */
Wtf.ux.form.HtmlEditor.SubSuperScript = Wtf.extend(Wtf.ux.form.HtmlEditor.MidasCommand, {
    midasBtns: ['|', {
        enableOnSelection: true,
        cmd: 'subscript',
        tooltip: {
            title: WtfGlobal.getLocaleText("crm.template.subscript")//'Subscript'
        },
        overflowText: WtfGlobal.getLocaleText("crm.template.subscript")//'Subscript'
    }, {
        enableOnSelection: true,
        cmd: 'superscript',
        tooltip: {
            title: WtfGlobal.getLocaleText("crm.template.subscript")//'Superscript'
        },
        overflowText: WtfGlobal.getLocaleText("crm.template.subscript")//'Superscript'
    }]
});

/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.SpecialCharacters  component 					*/
/** *********************************************************************************************** */

Wtf.ux.form.HtmlEditor.SpecialCharacters = Wtf.extend(Wtf.util.Observable, {
    specialChars: [],
    charRange: [160, 256],
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        var cmp = this.cmp;
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-char',
            disabled:true,
            handler: function(){
                if (this.specialChars.length == 0) {
                    Wtf.each(this.specialChars, function(c, i){
                        this.specialChars[i] = ['&#' + c + ';'];
                    }, this);
                    for (i = this.charRange[0]; i < this.charRange[1]; i++) {
                        this.specialChars.push(['&#' + i + ';']);
                    }
                }
                this.specialChars[9][1]="&copy;";
                var charStore = new Wtf.data.SimpleStore({
                    fields: ['char','code'],
                    data: this.specialChars
                });
                this.charWindow = new Wtf.Window({
                    title: WtfGlobal.getLocaleText("crm.template.insertspecialchars"),//'Insert Special Character',
                    width: 436,
                    resizable: false,
                    modal: true,
                    autoHeight: true,
                    layout: 'fit',
                    items: [this.charView = new Wtf.DataView({
                        style: "background-color:white;",
                        store: charStore,
                        autoHeight: true,
                        multiSelect: true,
                        tpl: new Wtf.XTemplate('<tpl for="."><div class="char-item">{char}</div></tpl><div class="x-clear"></div>'),
                        overClass: 'char-over',
                        itemSelector: 'div.char-item',
                        listeners: {
                            dblclick: function(t, i, n, e){
                    			var rec=t.getStore().getAt(i);
                    			var val = rec.get('code');
                    			if(!val)
                    				val = rec.get('char');
                                this.insertChar(val);
                                this.charWindow.close();
                            },
                            scope: this
                        }
                    })],
                    buttons: [{
                        text: 'Insert',
                        handler: function(){
                            Wtf.each(this.charView.getSelectedRecords(), function(rec){
                    			var c = rec.get('code');
                    			if(!c)
                    				c = rec.get('char');

                                this.insertChar(c);
                            }, this);
                            this.charWindow.close();
                        },
                        scope: this
                    }, {
                        text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                        handler: function(){
                            this.charWindow.close();
                        },
                        scope: this
                    }]
                });
                this.charWindow.show();
            },
            scope: this,
            tooltip: {
                title: WtfGlobal.getLocaleText("crm.template.insertspecialchars"),//'Insert Special Character.',
                text:WtfGlobal.getLocaleText("crm.template.insertspecialchars.ttip")//'Click to get the list of special characters and insert .'
            },
            overflowText: WtfGlobal.getLocaleText("crm.template.specialchars")//'Special Characters'
        });
    },
    insertChar: function(c){
        if (c) {
            if(Wtf.isIE){
                this.cmp.win.focus();
                var r =  this.cmp.doc.selection.createRange();
                if(r){
                    r.collapse(true);
                    r.pasteHTML(c);
                    this.cmp.syncValue();
                    this.cmp.deferFocus();
                }
            }else{
                this.cmp.execCmd('InsertHTML', c);
            }
        }
    }
});

/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.customButton  component 							*/
/** *********************************************************************************************** */
Wtf.ux.form.HtmlEditor.customButton = function(conf) {
    Wtf.apply(this, conf);
};

Wtf.ux.form.HtmlEditor.customButton = Wtf.extend(Wtf.ux.form.HtmlEditor.customButton, Wtf.util.Observable, {
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on("render", this.onRender, this);
    },
    onRender: function(){
        if(!this.buttonConf.scope){
            this.buttonConf.scope = this;
        }
        this.btn = this.cmp.getToolbar().addButton(this.buttonConf);
    }
});



/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.Table  component 								*/
/** *********************************************************************************************** */
Wtf.ux.form.HtmlEditor.Table = Wtf.extend(Wtf.util.Observable, {
    cmd: 'table',
    tableBorderOptions: [['none', 'None'], ['1px solid #000', 'Sold Thin'], ['2px solid #000', 'Solid Thick'], ['1px dashed #000', 'Dashed'], ['1px dotted #000', 'Dotted']],
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-table',
            handler: function(){
                if (!this.tableWindow){
                    this.tableWindow = new Wtf.Window({
                        title: WtfGlobal.getLocaleText("crm.template.inserttable"),//'Insert Table',
                        closeAction: 'hide',
                        items: [{
                            itemId: 'insert-table',
                            xtype: 'form',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 60,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'numberfield',
                                allowBlank: false,
                                allowDecimals: false,
                                fieldLabel: WtfGlobal.getLocaleText("crm.template.rows"),//'Rows',
                                name: 'row',
                                width: 60
                            }, {
                                xtype: 'numberfield',
                                allowBlank: false,
                                allowDecimals: false,
                                fieldLabel: WtfGlobal.getLocaleText("crm.template.columns"),//'Columns',
                                name: 'col',
                                width: 60
                            }, {
                                xtype: 'combo',
                                fieldLabel: WtfGlobal.getLocaleText("crm.template.specialchars"),//'Border',
                                name: 'border',
                                forceSelection: true,
                                mode: 'local',
                                store: new Wtf.data.ArrayStore({
                                    autoDestroy: true,
                                    fields: ['spec', 'val'],
                                    data: this.tableBorderOptions
                                }),
                                triggerAction: 'all',
                                value: 'none',
                                displayField: 'val',
                                valueField: 'spec',
                                width: 90
                            }]
                        }],
                        buttons: [{
                            text: WtfGlobal.getLocaleText("crm.template.insertbtn"),//'Insert',
                            handler: function(){
                                var frm = this.tableWindow.getComponent('insert-table').getForm();
                                if (frm.isValid()) {
                                    var border = frm.findField('border').getValue();
                                    var rowcol = [frm.findField('row').getValue(), frm.findField('col').getValue()];
                                    if (rowcol.length == 2 && rowcol[0] > 0 && rowcol[0] < 10 && rowcol[1] > 0 && rowcol[1] < 10) {
                                        var html = "<table>";
                                        for (var row = 0; row < rowcol[0]; row++) {
                                            html += "<tr>";
                                            for (var col = 0; col < rowcol[1]; col++) {
                                                html += "<td width='20%' style='border: " + border + ";'>" + row + "-" + col + "</td>";
                                            }
                                            html += "</tr>";
                                        }
                                        html += "</table>";
                                        this.cmp.insertAtCursor(html);
                                    }
                                    this.tableWindow.hide();
                                }else{
                                    if (!frm.findField('row').isValid()){
                                        frm.findField('row').getEl().frame();
                                    }else if (!frm.findField('col').isValid()){
                                        frm.findField('col').getEl().frame();
                                    }
                                }
                            },
                            scope: this
                        }, {
                            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                            handler: function(){
                                this.tableWindow.hide();
                            },
                            scope: this
                        }]
                    });

                }else{
                    this.tableWindow.getEl().frame();
                }
                this.tableWindow.show();
            },
            scope: this,
            tooltip: {
                title: WtfGlobal.getLocaleText("crm.template.inserttable")//'Insert Table'
            },
            overflowText: WtfGlobal.getLocaleText("crm.template.table")//'Table'
        });
    }
});
/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.Word  component 									*/
/** *********************************************************************************************** */

Wtf.ux.form.HtmlEditor.Word = Wtf.extend(Wtf.util.Observable, {
    curLength: 0,
    lastLength: 0,
    lastValue: '',
    wordPasteEnabled: true,
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
		this.cmp.on('initialize', this.onInit, this, {delay:100, single: true});
    },
    onInit: function(){
        Wtf.EventManager.on(this.cmp.getDoc(), {
            'keyup': this.checkIfPaste,
            scope: this
        });
        this.lastValue = this.cmp.getValue();
        this.curLength = this.lastValue.length;
        this.lastLength = this.lastValue.length;
    },
    checkIfPaste: function(e){
        var diffAt = 0;
        this.curLength = this.cmp.getValue().length;
        if (e.V == e.getKey() && e.ctrlKey && this.wordPasteEnabled){
            this.cmp.suspendEvents();
            diffAt = this.findValueDiffAt(this.cmp.getValue());
            var parts = [
            this.cmp.getValue().substr(0, diffAt),
            this.fixWordPaste(this.cmp.getValue().substr(diffAt, (this.curLength - this.lastLength))),
            this.cmp.getValue().substr((this.curLength - this.lastLength)+diffAt, this.curLength)
            ];
            this.cmp.setValue(parts.join(''));
            this.cmp.resumeEvents();
        }
        this.lastLength = this.cmp.getValue().length;
        this.lastValue = this.cmp.getValue();
    },
    findValueDiffAt: function(val){
        for (i=0;i<this.curLength;i++){
            if (this.lastValue[i] != val[i]){
                return i;
            }
        }
    },
    fixWordPaste: function(wordPaste) {
        var removals = [/&nbsp;/ig, /[\r\n]/g, /<(xml|style)[^>]*>.*?<\/\1>/ig, /<\/?(meta|object|span)[^>]*>/ig,
        /<\/?[A-Z0-9]*:[A-Z]*[^>]*>/ig, /(lang|class|type|href|name|title|id|clear)=\"[^\"]*\"/ig, /style=(\'\'|\"\")/ig, /<![\[-].*?-*>/g,
        /MsoNormal/g, /<\\?\?xml[^>]*>/g, /<\/?o:p[^>]*>/g, /<\/?v:[^>]*>/g, /<\/?o:[^>]*>/g, /<\/?st1:[^>]*>/g, /&nbsp;/g,
        /<\/?SPAN[^>]*>/g, /<\/?FONT[^>]*>/g, /<\/?STRONG[^>]*>/g, /<\/?H1[^>]*>/g, /<\/?H2[^>]*>/g, /<\/?H3[^>]*>/g, /<\/?H4[^>]*>/g,
        /<\/?H5[^>]*>/g, /<\/?H6[^>]*>/g, /<\/?P[^>]*><\/P>/g, /<!--(.*)-->/g, /<!--(.*)>/g, /<!(.*)-->/g, /<\\?\?xml[^>]*>/g,
        /<\/?o:p[^>]*>/g, /<\/?v:[^>]*>/g, /<\/?o:[^>]*>/g, /<\/?st1:[^>]*>/g, /style=\"[^\"]*\"/g, /style=\'[^\"]*\'/g, /lang=\"[^\"]*\"/g,
        /lang=\'[^\"]*\'/g, /class=\"[^\"]*\"/g, /class=\'[^\"]*\'/g, /type=\"[^\"]*\"/g, /type=\'[^\"]*\'/g, /href=\'#[^\"]*\'/g,
        /href=\"#[^\"]*\"/g, /name=\"[^\"]*\"/g, /name=\'[^\"]*\'/g, / clear=\"all\"/g, /id=\"[^\"]*\"/g, /title=\"[^\"]*\"/g,
        /<span[^>]*>/g, /<\/?span[^>]*>/g, /class=/g];
        Wtf.each(removals, function(s){
            wordPaste = wordPaste.replace(s, "");
        });
        wordPaste = wordPaste.replace(/<div[^>]*>/g, "<p>");
        wordPaste = wordPaste.replace(/<\/?div[^>]*>/g, "</p>");
        return wordPaste;

    },
    onRender: function() {
        this.cmp.getToolbar().add({
            iconCls: 'x-edit-wordpaste',
            pressed: true,
            handler: function(t){
                t.toggle(!t.pressed);
                this.wordPasteEnabled = !this.wordPasteEnabled;
            },
            scope: this,
            tooltip: {
                text:  WtfGlobal.getLocaleText("crm.template.cleantxtmsgttip")//'Cleanse text pasted from Word or other Rich Text applications.'
            }
        });
    }
});

/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.HR   component 									*/
/** *********************************************************************************************** */
Wtf.ux.form.HtmlEditor.HR = Wtf.extend(Wtf.util.Observable, {
    cmd: 'hr',
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        var cmp = this.cmp;
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-hr',
            disabled:true,
            handler: function(){
                if (!this.hrWindow){
                    this.hrWindow = new Wtf.Window({
                        title: WtfGlobal.getLocaleText("crm.template.insertrule"),//'Insert Rule',
                        closeAction: 'hide',
                        width:200,
                        items: [{
                            itemId: 'insert-hr',
                            xtype: 'form',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 60,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'textfield',
                                maskRe: /[0-9]|%/,
                                regex: /^[1-9][0-9%]{1,3}/,
                                fieldLabel: WtfGlobal.getLocaleText("crm.exportinterface.width"),//'Width',
                                name: 'hrwidth',
                                width: 60,
                                listeners: {
                                    specialkey: function(f, e){
                                        if ((e.getKey() == e.ENTER || e.getKey() == e.RETURN) && f.isValid()) {
                                            this.doInsertHR();
                                        }else{
                                            f.getEl().frame();
                                        }
                                    },
                                    scope: this
                                }
                            }]
                        }],
                        buttons: [{
                            text: WtfGlobal.getLocaleText("crm.template.cleantxtmsgttip"),//'Insert',
                            handler: function(){
                                var frm = this.hrWindow.getComponent('insert-hr').getForm();
                                if (frm.isValid()){
                                    this.doInsertHR();
                                }else{
                                    frm.findField('hrwidth').getEl().frame();
                                }
                            },
                            scope: this
                        }, {
                            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                            handler: function(){
                                this.hrWindow.hide();
                            },
                            scope: this
                        }]
                    });
                }else{
                    this.hrWindow.getEl().frame();
                }
                this.hrWindow.show();
            },
            scope: this,
            tooltip: {
                title: WtfGlobal.getLocaleText("crm.template.inserthorizontalrule"),//'Insert Horizontal Rule.',
                text:WtfGlobal.getLocaleText("crm.template.inserthorizontalrule.ttip")//'Click to insert horizontal rule.'
            },
            overflowText: WtfGlobal.getLocaleText("crm.template.horizontalrule")//'Horizontal Rule'
        });
    },
    doInsertHR: function(){
        var frm = this.hrWindow.getComponent('insert-hr').getForm();
        if (frm.isValid()) {
            var hrwidth = frm.findField('hrwidth').getValue();
            if (hrwidth) {
                this.insertHR(hrwidth);
            } else {
                this.insertHR('100%');
            }
            frm.reset();
            this.hrWindow.hide();
        }
    },
    insertHR: function(w){
        this.cmp.insertAtCursor('<hr width="' + w + '">');
    }
});

/** *********************************************************************************************** */
/* 							Wtf.ThumbnailChooser  component 							*/
/** *********************************************************************************************** */
Wtf.ThumbnailViewer = Wtf.extend(Wtf.Panel, {
	tpl : new Wtf.XTemplate(
			'<div class="details">',
				'<tpl for=".">',
					'<img src="{url}"><div class="details-info">',
					'<b>'+WtfGlobal.getLocaleText("crm.template.imagename")+':</b>',
					'<span>{imgname}</span>',
				'</tpl>',
			'</div>'
		),

	initComponent:function(){
		this.tpl.compile();
		Wtf.ThumbnailViewer.superclass.initComponent.call(this);
	},
	
	update : function(record){
	    var detailEl = this.body;
		if(record&&record.data){
            detailEl.hide();
            this.tpl.overwrite(detailEl, record.data);
            detailEl.slideIn('l', {stopFx:true,duration:.2});
		}else{
		    detailEl.update('');
		}
	}
});


Wtf.ThumbnailChooser = Wtf.extend(Wtf.Panel, {
	initComponent:function(){
	    if(this.filterKey){
	    	this.tbar = [{
	        	text: WtfGlobal.getLocaleText("crm.FILTERBUTTON")+':'//'Filter:'
	        },this.txtFilter = new Wtf.form.TextField({
	        	xtype: 'textfield',
	        	selectOnFocus: true,
	        	listeners: {
	        		'render': {fn:function(){
	        				this.txtFilter.getEl().on('keyup', function(){
				    		this.filter(this.txtFilter.getValue());
				    	}, this, {buffer:500});
	        		}, scope:this}
	        	}
	        })];
	    };
	    this.tpl = new Wtf.XTemplate(
    			'<tpl for=".">',
				'<div class="thumb-wrap">',
				this.tplText,
				'</div>',
			'</tpl>'
		);
		this.tpl.compile();
		Wtf.ThumbnailChooser.superclass.initComponent.call(this);
		this.addEvents({
			'tndblclick':true,
			'tnclick':true
		});
	},
	onRender : function(ct, position){
		Wtf.ThumbnailChooser.superclass.onRender.call(this, ct, position);
		    this.view = new Wtf.DataView({
				tpl: this.tpl,
				singleSelect: true,
				cls:this.containerClass,
				style:'overflow-x:hidden',
				overClass:this.overClass,
				selectedClass:this.selectedClass,
				itemSelector: 'div.thumb-wrap',
				emptyText : this.emptyText,
				store: this.store,
				listeners: {
					'selectionchange': {fn:function(v, sel){if(!sel||!sel[0]) return;var rec=v.getRecord(sel[0]);this.fireEvent('tnclick',rec);}, scope:this},
					'dblclick'       : {fn:function(v,i,n){var rec=v.getRecord(n);this.fireEvent('tndblclick', rec);}, scope:this},
					'beforeselect'   : {fn:function(view){return view.store.getRange().length > 0;}}
				}
			});
		    if(this.prepareData)
		    	this.view.prepareData = this.prepareData;
			
			this.on('bodyresize',function(){this.doLayout();}, this);
			
			this.add(this.view);
	},

	filter : function(filterVal){
		this.view.store.filter(this.filterKey, filterVal);
	},

	reset : function(){
		if(this.win.rendered){
			if(this.txtFilter)this.txtFilter.reset();
			this.view.getEl().dom.scrollTop = 0;
		}
	    this.view.store.clearFilter();
	},
	
	getSelected : function(){
		return this.view.getSelectedRecords()[0];
	}
});

/** *********************************************************************************************** */
/* 							Wtf.ux.form.HtmlEditor.insertImage  component 							*/
/** *********************************************************************************************** */
Wtf.ux.form.HtmlEditor.insertImage = Wtf.extend(function(conf){
    Wtf.apply(this, conf);
},Wtf.util.Observable, {
    init: function(cmp){
        this.cmp = cmp;
        this.uType = "upload";
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-image',
            handler: this.showImageWindow,
            scope: this,
            disabled:true,
            tooltip: {
                title: WtfGlobal.getLocaleText("crm.template.insertimage"),//'Insert image',
                text:WtfGlobal.getLocaleText("crm.template.insertimage.ttip")//'Click to insert image(s).'
            }
        });
    },
        
    showImageWindow: function(){
        var tmbStore = new Wtf.data.Store({
            url: this.imageStoreURL,
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, Wtf.data.Record.create(["id","imgname", "url"]))
        });
        tmbStore.load();
    	
        this.imageGrid = new Wtf.ThumbnailChooser({
        	tplText : '<div class="thumb"><img src="{url}" title="{imgname}"></div><span>{shortName}</span>',
        	store:tmbStore,
        	//layout:'fit',
        	height:200,
        	region:'center',
        	filterKey:'imgname',
        	emptyText:'<div style="padding:10px;">No images match found</div>',
        	containerClass:'img-chooser-view',
        	overClass:'templateThumbContainer',
        	selectedClass:'selectedTemplate',
        	bodyStyle:'background:none',
            autoScroll: true,
            border:false,
            prepareData:function(data){
		    	data.shortName = Wtf.util.Format.ellipsis(data.imgname,15);
		    	return data;
		    }
        });

		this.detailPane=new Wtf.ThumbnailViewer({
			layout:'fit',
			id :'qickviewimage',
			region: 'west',
			cls:'img-detail-pane',
			bodyStyle:'background-color:white;',
			width: 540,
			hideCollapseTool:false,
			collapsible: true,
	        collapsed: true,
	        autoScroll:true,
	        margins: '0 0 0 0',
	        plugins: new Wtf.ux.collapsedPanelTitlePlugin(),
	        title: WtfGlobal.getLocaleText("crm.template.viewimage")//'View Image'
			
		});	
        this.imageGrid.on('tnclick',function(record){
        	this.detailPane.update(record);    	
        },this); 
        this.imageGrid.on('tndblclick',function(record){
        	this.insertImg(record.get('url'));
    		this.insertImgWin.close();        	
        },this); 
        
        this.uploadForm = new Wtf.MultiFlieUploadPanel({
			methodType : 'upload',
			title : WtfGlobal.getLocaleText("crm.template.uploadnewimage"),//'Upload New Image',
			layout : 'fit',
			border : false,
			url : this.imageUploadURL,
			bbar : [new Wtf.FileBrowseButton({
				text : WtfGlobal.getLocaleText("crm.activitydetailpanel.addfilesBTN"),//'Add Files',
				tooltip : WtfGlobal.getLocaleText("crm.template.addfiles.ttip"),//'Click here to browse and add your files to upload',
				handler : function(btn) {
					this.uploadForm.addFiles(btn);
				},
				scope : this
			}),{
				text : WtfGlobal.getLocaleText("crm.uploadbtn"),//'Upload',
				tooltip : WtfGlobal.getLocaleText("crm.template.uploadbtn.ttip"),//'Click here to start uploading your files which are listed above',
				handler : function() {
					this.uploadForm.startUpload();
				},
				scope : this
			},{
				text : WtfGlobal.getLocaleText("crm.template.stop"),//'Stop',
				tooltip : WtfGlobal.getLocaleText("crm.template.canceluploading.ttip"),//'Cancel uploading of files which are not uploaded yet',
				handler : function() {
					this.uploadForm.cancelUpload();
				},
				scope : this
			},{
				text : WtfGlobal.getLocaleText("crm.template.clear"),//'Clear',
				tooltip : WtfGlobal.getLocaleText("crm.template.removefilesttip"),//'remove all files which are listed above',
				handler : function() {
					this.uploadForm.clearAll();
				},
				scope : this
			}]
		});

        var urlFieldSet = new Wtf.Panel({
			methodType : 'url',
			title : WtfGlobal.getLocaleText("crm.template.useweburl"),//'Use Web URL',
			bodyStyle : "border: 1px solid #B5B8C8; padding: 10px 10px 6px 10px;",
			layout : "form",
			labelWidth : 70,
			border : false,
			items : [ this.urlField = new Wtf.form.TextField({
				width : 280,
				fieldLabel : WtfGlobal.getLocaleText("crm.template.imageurl")//"Image URL"
			})]
		});       
        
        this.newImage = new Wtf.Panel({
            border:false,
            layout:'accordion',
            layoutConfig:{
                animate:true
            },
            items: [this.imageGridPanel=new Wtf.Panel({
            	border:false,
                layout:'border',
                methodType:'select',
                title:WtfGlobal.getLocaleText("crm.template.selectimagetoinsert"),//'Select image to insert',
                items: [this.imageGrid,this.detailPane]          	
            }),this.uploadForm,urlFieldSet]
        });

        this.insertImgWin = new Wtf.Window({
            title:WtfGlobal.getLocaleText("crm.template.uploadimage"),// "Upload Image",
            bodyStyle: "background-color:#FFFFFF",
            modal: true,
            resizable: false,
            height: 400,
            width: 550,
            layout:'fit',
            items: this.newImage,
            buttons: [{
                text: WtfGlobal.getLocaleText("crm.OK"),//"OK",
                scope: this,
                handler: this.insertSelectedImage
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//"Cancel",
                scope: this,
                handler: function(btn){
                    this.insertImgWin.close();
                }
            }]
        });
        this.insertImgWin.show();
    },
    
    insertSelectedImage: function(btn){
    	var methodType = this.newImage.layout.activeItem.methodType;
    	var imageURL="";
    	if(methodType==='select'){
    		var tmp = null;
    		tmp = this.imageGrid.getSelected();
    		if(tmp==null){
    			WtfComMsgBox(22,0);
    			return;
    		}
    		imageURL = tmp.get('url');
    	}else if(methodType==='upload'){
    		this.newImage.layout.setActiveItem(this.imageGridPanel);
            this.imageGrid.store.reload();
		}else if(methodType==='url'){
			imageURL = this.urlField.getValue().trim();
			if(imageURL==""){
				WtfComMsgBox(23,0);
				return;
			}			
		}
    	
    	if(imageURL&&imageURL.length>0){
    		this.insertImg(imageURL);
    		this.insertImgWin.close();
    	}
    },
    insertImg: function(c){
        if(c) {
            this.cmp.insertAtCursor("<img src='" + c +"' />");
        }
    }
});

/** *********************************************************************************************** */
/* 							Wtf.TemplateHolder component 											*/
/** *********************************************************************************************** */

Wtf.TemplateHolder = Wtf.extend(Wtf.Component, {
	defaultMenuConfig:{
        tag: 'ul', 
        cls: 'edit-links', 
        children: [
            {tag: 'li', menuname:'edit', cls: 'edit tpl-link', html: WtfGlobal.getLocaleText("crm.templateeditor.edit")},
            {tag: 'li', menuname:'remove', cls: 'remove tpl-link', html: WtfGlobal.getLocaleText("crm.templateeditor.remove")}
        ]
  	},
    onRender: function(ct, position){
        Wtf.TemplateHolder.superclass.onRender.call(this, ct, position);
        this.defaultMenuConfig.cls = "section-menu "+ (this.defaultMenuConfig.cls||"");
        this.elDom = Wtf.get(this.renderTo).createChild({
            tag: "div",
            cls: "templateCompCont"
        });
        this.table1 = document.createElement("table");
        this.table1.setAttribute("cellspacing", 0);
        this.table1.setAttribute("width", "100%");
        this.table1.className = "tplBodyHolder";
        var tab1Body = document.createElement("tbody");
        var tab1Row = document.createElement("tr");
        var tab1Data = document.createElement("td");
        tab1Data.setAttribute("align", "center");
        tab1Row.appendChild(tab1Data);
        tab1Body.appendChild(tab1Row);
        this.table1.appendChild(tab1Body);
        var table2 = document.createElement("table");
        table2.setAttribute("cellspacing", 0);
        table2.setAttribute("cellpadding", 0);
        table2.setAttribute("width", "100%");
         var tab2Body = document.createElement("tbody");
        var tab2Row = document.createElement("tr");
        this.contentHolder = document.createElement("td");
        tab2Row.appendChild(this.contentHolder);
        tab2Body.appendChild(tab2Row);
        table2.appendChild(tab2Body);
        tab1Data.appendChild(table2);
        Wtf.get(this.contentHolder).addListener("click", this.contentClicked,this);
        this.elDom.appendChild(this.table1);
        this.setHtml(this.bodyHtml);
    },
    appendSectionMenu : function(el, menuConfig){
    	if(menuConfig){
    		menuConfig.cls = "section-menu "+ (menuConfig.cls||"");
    	}
        Wtf.DomHelper.insertFirst(el, menuConfig || this.defaultMenuConfig);
    },
    removeSectionMenu : function(el){
        var chArr= Wtf.DomQuery.select("ul[class*=section-menu]",el);
        for(var i=0;i<chArr.length;i++){
            el.removeChild(chArr[i]);
        }
    },   
    setHtml: function(html){
        this.contentHolder.innerHTML = html;
        var sectionArray = Wtf.DomQuery.select("*[class*=tpl-content]", this.contentHolder);
        for( var i =0 ; i< sectionArray.length;i++){
            this.appendSectionMenu(sectionArray[i]);
        }       
    },    
    getHtml: function(){
        var x = this.contentHolder.cloneNode(true);
        var sectionArray = Wtf.DomQuery.select("*[class*=tpl-content]", x);
        for( var i =0 ; i< sectionArray.length;i++){
            this.removeSectionMenu(sectionArray[i]);
        }
       return x.innerHTML;
    },   
    removeSection:function(sectionEl){
    	sectionEl.parentNode.removeChild(sectionEl);
    },
    contentClicked: function(e){
        var _to = e.getTarget();
        if(_to.className.indexOf("tpl-content") != -1 || _to.className.indexOf("edit tpl-link") != -1){
            var contentEl = e.getTarget(".tpl-content").cloneNode(true);
            this. removeSectionMenu(contentEl);
            var _tw = new Wtf.EditorWindow({
                headerImage: (_to.className == "tpl-content-image"),
                val: contentEl.innerHTML,
                parentCont: e.getTarget(".tpl-content"),
                emailtype:this.emailtype,
                plaintext :this.plaintext,
                defaultStore:this.defaultStore,
                defaultRec:this.defaultRec,
                tplVariables:this.tplVariables,
                defaultValues:this.defaultValues,
                editEmailMaketingFlag:this.editEmailMaketingFlag
            });
            _tw.on("okClicked", function(obj){
                var valObj = obj.getEditorVal();
                valObj.textVal=valObj.textVal.replace(/<ul[\s{1,}]?>/,"<ul  class='ultag'>");
                valObj.textVal=valObj.textVal.replace(/<UL[\s{1,}]?>/,"<ul  class='ultag'>");
                obj.parentCont.innerHTML = valObj.textVal.replace(/float[\s{1,}]?:[\s{1,}]?left|right/,"");
                this.defaultValues = obj.defaultValues;
                this.appendSectionMenu(obj.parentCont);
                if(valObj.imageRec) {
                    obj.parentCont.parentNode.style.height = valObj.imageRec.data["height"] + "px";
                    obj.parentCont.parentNode.style.background = 'url(' + valObj.imageRec.data["url"] + ') no-repeat';
                }
            }, this);
            _tw.show();
        }
        if( _to.className.indexOf("remove tpl-link") != -1){
            var sectionArray = Wtf.DomQuery.select("*[class*=tpl-content]", this.contentHolder);
            if(sectionArray.length>1){
                var contentEl = e.getTarget(".tpl-content");
                Wtf.MessageBox.show({
                    title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),
                    msg:WtfGlobal.getLocaleText("crm.template.delsectionconfirmmsg"),//"Do you really want to delete this section?",
                    icon:Wtf.MessageBox.QUESTION,
                    buttons:Wtf.MessageBox.YESNO,
                    scope:this,
                    fn:function(button){
                        if(button=='yes')
                        {
                            contentEl.parentNode.removeChild(contentEl);
                        }else{
                            return;
                        }
                    }
                });
            } else {
                WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.template.dontdelallsecmsg")]);
            }

        }
    },
    getPlainText: function(){
        var htm = this.elDom.dom.innerHTML;
        htm = htm.replace(/<p>/g, "");
        htm = htm.replace(/<\p>/g, "");
        htm = htm.replace(/<P>/g, "");
        htm = htm.replace(/<\P>/g, "");
        htm = htm.replace(/&nbsp;/g, "");
        htm = Wtf.util.Format.stripTags(htm);
        return htm;
    },
    
    applyTemplateTheme: function(theme){
        if(theme) {
        	
        	for(elAttr in theme){
        		var elArr = Wtf.DomQuery.select(elAttr,this.table1.parentNode);
        		if(elArr.length==0){
        			ResponseAlert(99);
        			return;
        		}else{
        		for(var i=0; i< elArr.length; i++){
        			var el = elArr[i];
        			for(styleAttr in theme[elAttr])
        			el.style[styleAttr] = theme[elAttr][styleAttr];
        		}
        	 }
        	}
        }
    }
});
/**************************************************************************************************/
/*                     		 Wtf.EmailTemplateEditor component                                    */
/**************************************************************************************************/

Wtf.EmailTemplateEditor = Wtf.extend(Wtf.Panel, {
	bodyStyle : "background-color:white;",
	border : false,
	tplContent:" <table id=\"wtf-gen5095\" width=\"100%\" cellspacing=\"0\"><tbody><tr><td align=\"center\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td id=\"wtf-gen5093\"><table id=\"wtf-gen2835\" width=\"100%\" cellspacing=\"0\"><tbody><tr><td align=\"center\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td id=\"wtf-gen2833\"><table style=\"background-color: rgb(238, 238, 238);\" class=\"backgroundTable\" width=\"100%\" cellpadding=\"10\" cellspacing=\"0\"><tbody><tr><td id=\"wtf-gen2937\" valign=\"top\" align=\"center\"><table style=\"border: 0px none rgb(0, 0, 0); margin-top: 10px;\" id=\"contentTable\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td><table width=\"600\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td style=\"background-color: rgb(238, 238, 238); border-top: 0px none rgb(0, 0, 0); border-bottom: 0px none rgb(255, 255, 255); text-align: center; padding: 0px;\" class=\"headerTop\" align=\"right\"><div style=\"font-size: 10px; color: rgb(51, 51, 51); font-family: Helvetica; text-decoration: none;\" mc:edit=\"header\" class=\"adminText\"><span id=\"tpl-content-header\" class=\"tpl-content\"> <!-- --> Aliquam erat volutpat. Sed quis velit. Nulla facilisi. Nulla libero. Vivamus pharetra posuere sapien. Nam consectetuer. <!-- --></span></div></td></tr></tbody></table><table class=\"bodyTable\" width=\"600\" cellpadding=\"20\" cellspacing=\"0\"><tbody><tr><td id=\"wtf-gen2938\" style=\"font-size: 12px; color: rgb(0, 0, 0); font-family: Helvetica; width: 400px; background-color: rgb(255, 255, 255); padding: 20px;\" mc:edit=\"main\" class=\"defaultText\" valign=\"top\" align=\"left\"><span id=\"tpl-content-main\" class=\"tpl-content\"> <span class=\"title\">Primary Heading</span><br> <p id=\"wtf-gen2939\">Sample copy. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Morbi commodo, ipsum sed pharetra gravida, orci magna rhoncus neque, id pulvinar odio lorem non turpis. Nullam sit amet enim. Suspendisse id velit vitae ligula volutpat condimentum. Aliquam erat volutpat. Sed quis velit. Nulla facilisi. Nulla libero. Vivamus pharetra posuere sapien. Nam consectetuer. Sed aliquam, nunc eget euismod ullamcorper, lectus nunc ullamcorper orci, fermentum bibendum enim nibh eget ipsum. Donec porttitor ligula eu dolor. Maecenas vitae nulla consequat libero cursus venenatis. Nam magna enim, accumsan eu, blandit sed, blandit a, eros.</p> <span class=\"subTitle\">Subheading</span><br> <p>Click here to add your email copy and images.</p></span></td><td id=\"wtf-gen2940\" style=\"margin: 0px; background-color: rgb(255, 255, 255); border-left: 1px solid rgb(221, 221, 221); text-align: left; width: 200px; padding: 20px;\" class=\"sideColumn\" valign=\"top\" align=\"left\"><div style=\"font-size: 11px; font-weight: normal; color: rgb(102, 102, 102); font-family: Helvetica;\" mc:edit=\"sidecolumn\" class=\"sideColumnText\"><span id=\"tpl-content-sidecolumn\" class=\"tpl-content\"> <span class=\"sideColumnTitle\">Subheading</span><br> Click here to add your side column copy and images.</span></div></td></tr><tr><td id=\"wtf-gen2941\" style=\"background-color: rgb(255, 255, 255); border-top: 0px none rgb(255, 255, 255); padding: 20px;\" colspan=\"2\" class=\"footerRow\" valign=\"top\" align=\"left\"><div style=\"font-size: 10px; color: rgb(102, 102, 102); font-family: Helvetica;\" mc:edit=\"footer\" class=\"footerText\"><span id=\"tpl-content-footer\" class=\"tpl-content\"> <!-- --> Our mailing address is:<br> #company:caddress#<br> <!-- --> Copyright (C) #other:currentyear#&nbsp; #company:cname#&nbsp; All rights reserved.</span></div></td></tr></tbody></table></td></tr></tbody></table></td></tr></tbody></table><span style=\"padding: 0px;\"></span></td></tr></tbody></table></td></tr></tbody></table></td></tr></tbody></table></td></tr></tbody></table>",
	initComponent:function(){
		Wtf.apply(this,{layout:'border'});
		Wtf.EmailTemplateEditor.superclass.initComponent.call(this);
	},
	onRender : function(ct, position) {
		Wtf.EmailTemplateEditor.superclass.onRender.call(this, ct, position);
		this.templateEditor = new Wtf.Panel( {
			region : "center",
			layout : "fit",
			autoScroll : true,
			title : WtfGlobal.getLocaleText("crm.quotaion.grid.header.template"),//'Template',
			split : true,
			collapsible : false,
			buttons: [{
                text: WtfGlobal.getLocaleText("crm.template.preview"),//"Preview",
                scope: this,
                handler: this.preview
            }]
			
		});
		this.themePanel = new Wtf.HTMLThemePanel( {
			region : "east",
			theamLayout:'horizontal',
			plugins : new Wtf.ux.collapsedPanelTitlePlugin(),
			title : WtfGlobal.getLocaleText("crm.template.designthemes"),//"Design Themes",
			maxSize : 300,
			width : 300,
			split : true,
			collapsible : true
		});
		this.templateEditor.on("render", function() {
			this.editorHtmlComp = new Wtf.TemplateHolder({
				renderTo : this.templateEditor.body.dom,
				tplVariables:this.tplVariables,
				defaultValues:this.defaultValues,
				defaultStore:this.defaultStore,
				layout:'fit',
				bodyHtml : this.tplContent,
				editEmailMaketingFlag : false
				
			});
			this.themePanel.on("themeSelect", this.editorHtmlComp.applyTemplateTheme, this.editorHtmlComp);
		}, this);
		this.add(this.templateEditor, this.themePanel);
	},

	preview : function() { 
		var win = new Wtf.Window({
			cls:'tpl-preview',
            iconCls: "pwnd favwinIcon",
            title: WtfGlobal.getLocaleText("crm.activitydetailpanel.addfilesBTN"),//"Preview",
            resizable: false,
            height: 500,
            bodyStyle:"background-color:#FFFFFF;",
            width: 750,
            modal:true,
            maximizable: true,
            autoScroll:true,
            layout:'fit',
            items:new Wtf.Frame({html:'<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">'+this.editorHtmlComp.getHtml(), border:false})         
        });
		win.show();
	},
	
	makeFrame:function (url) {
		this.editorHtmlComp.setHtml("<span class='tpl-content'><iframe src='"+url+"' width='100%' height='100%'/></span>");
//		Wtf.Ajax.request({
//			url:url,
//			method:'GET',
//			success:function(res){
//				this.editorHtmlComp.setHtml(res.responseText);
//			},
//			failure:function(res){
//				Wtf.Msg.alert("Error",res.statusText);
//			},	
//			scope:this
//		});
	}
});

/** *********************************************************************************************** */
/* 							Wtf.HTMLThemePanel component 											*/
/** *********************************************************************************************** */

Wtf.HTMLThemePanel = Wtf.extend(Wtf.Panel,{
	theamLayout:'vertical',
	initComponent:function(){
		Wtf.apply(this,{layout:'border'});
		Wtf.HTMLThemePanel.superclass.initComponent.call(this);
		this.addEvents( {
			"themeSelect" : true
		});
	},
	onRender : function(ct, position) {
		Wtf.HTMLThemePanel.superclass.onRender.call(this, ct, position);
		this.addCategoryGrid();
		this.addThemeGrid();
		this.categoryGrid.getStore().load();
	},
	addThemeGrid : function() {
		var themeRec = Wtf.data.Record.create( [ {
			name : "id"
		}, {
			name : "theme"
		}, {
			name : "background"
		}, {
			name : "headerbackground"
		}, {
			name : "headertext"
		}, {
			name : "footerbackground"
		}, {
			name : "footertext"
		}, {
			name : "bodybackground"
		}, {
			name : "bodytext"
		}, {
			name : "groupid"
		} ]);

		var themeStore = new Wtf.data.Store({
			url : Wtf.req.springBase + 'emailMarketing/action/getColorThemes.do',
			baseParams : {
				  flag : 24
			},
			reader : new Wtf.data.KwlJsonReader( {
				root : "data"
			}, themeRec)
		});
		themeStore.load();
		var themeCM = new Wtf.grid.ColumnModel([{
			header : WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//"Name",
			dataIndex : "theme",
			renderer : function(val, meta, rec) {
				return "<div class='themeImage'>"
						+ "<div class='themeImgBox' style='background-color:"+ rec.data.background+ "'></div>"
						+ "<div class='themeImgBox' style='background-color:"+ rec.data.headerbackground+ "'></div>"
						+ "<div class='themeImgBox' style='background-color:"+ rec.data.headertext+ "'></div>"
						+ "<div class='themeImgBox' style='background-color:"+ rec.data.footerbackground+ "'></div>"
						+ "<div class='themeImgBox' style='background-color:"+ rec.data.bodybackground+ "'></div>" + "</div>"
						+"<span style='margin-left: 8px;'>"+ val + "</span>";
			}
		}]);
		var themeSM = new Wtf.grid.RowSelectionModel( {
			singleSelect : true
		});
		
		this.themeGrid = new Wtf.grid.GridPanel( {
			cm : themeCM,
			border : true,
			region : 'center',
			weidth:150,
			layout : 'fit',
			cls : "noborderGrid",
			title :WtfGlobal.getLocaleText("crm.template.themes"),// "Themes",
			ds : themeStore,
			autoScroll : true,
			sm : themeSM,
			viewConfig : {
				forceFit : true
			}
		});
		
		themeSM.on("rowselect", this.signalThemeEvent,this);
		this.add(this.themeGrid);
	},
	
	addCategoryGrid : function() {
		var grpRec = Wtf.data.Record.create( [ {
			name : "id"
		}, {
			name : "groupname"
		} ]);
		var categoryStore = new Wtf.data.Store({
					url : Wtf.req.springBase + 'emailMarketing/action/getColorThemeGroup.do',
					baseParams : {
						flag : 25
					},
					reader : new Wtf.data.KwlJsonReader( {
						root : "data"
					}, grpRec)
				});
		var categoryCM = new Wtf.grid.ColumnModel( [ {
			header :WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),// "Name",
			dataIndex : "groupname"
		} ]);
		var categorySM = new Wtf.grid.RowSelectionModel( {
			singleSelect : true
		});
		this.categoryGrid = new Wtf.grid.GridPanel( {
			cm : categoryCM,
			region : (this.theamLayout=='horizontal'?'west':'north'),
			width:150,
			border : true,
			layout : 'fit',
			cls : "noborderGrid",
			ds : categoryStore,
			columnWidth : 1,
			sm : categorySM,
			height : 150,
			title : WtfGlobal.getLocaleText("crm.template.categories"),//"Categories",
			viewConfig : {
				forceFit : true
			}
		});
		categorySM.on("rowselect", this.filterThemes,this);
		this.add(this.categoryGrid);
		categoryStore.on('load',function(){
			this.categoryGrid.getSelectionModel().selectFirstRow();
		},this);
	},
	
	signalThemeEvent : function(obj, ri, rec) {
		var theme = {
			"*[class=tplBodyHolder]" : {
				"backgroundColor" : rec.get("background")
			},
			"*[class=headerTop]" : {
				"backgroundColor" : rec.get("headerbackground"),
				"color" : rec.get("headertext")
			},
			"*[class=footerRow]" : {
				"backgroundColor" : rec.get("footerbackground"),
				"color" : rec.get("footertext")
			},
			"*[class=defaultText]" : {
				"backgroundColor" : rec.get("bodybackground"),
				"color" : rec.get("bodytext")

			}
		};
		this.fireEvent("themeSelect", theme, rec.data.id);
	},
	
	filterThemes : function(sm, ri, rec) {	
		this.themeGrid.getStore().filter("groupid", rec.data.id);
	},
	
	getSelectedTheme:function()	{
		var sm=this.themeGrid.getSelectionModel();
		if(sm.getCount()>0){
			return sm.getSelected().get('id');
		}
	}
});
