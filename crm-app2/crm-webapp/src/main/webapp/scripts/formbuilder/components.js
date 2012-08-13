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
Wtf.ReadOnlyControl=function(config){
    Wtf.apply(this,config);

    this.events = {
        "addConfig": true
    };
    this.typeStore1 = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({
            root:'data'
        }, ["moduleid","modulename", "mastertype", "tablename"]),
        autoLoad : false,
        url : Wtf.req.mbuild+"form.do",
        baseParams : {
            action : 7,
            moduleid : this.moduleid
        }
    });
    this.typeStore1.load();

    this.modColStore = new Wtf.data.Store({
        url: Wtf.req.mbuild+'form.do',
        baseParams : {
            action : 15
        },
        method: 'POST',
        reader: new Wtf.data.KwlJsonReader({
            root:'data'
        }, ['displayfield','name'])
    });

    this.qType1 = new Wtf.form.ComboBox({
        valueField: 'moduleid',
        displayField: 'modulename',
        store: this.typeStore1,
        fieldLabel: 'Config table name',
        editable: false,
        allowBlank: false,
        anchor: '95%',
        mode: 'local',
        disabled:true,
        triggerAction: 'all',
        selectOnFocus: true,
        emptyText: 'Select config table name...'
    });
    this.colCombo = new Wtf.form.ComboBox({
        fieldLabel: 'Config columnname',
        store : this.modColStore,
        readOnly : true,
        allowBlank: false,
        anchor: '95%',
        displayField:'displayfield',
        valueField : 'name',
        mode: 'local',
        triggerAction: 'all',
        disabled:true,
        emptyText : 'Select column to filter',
        forceSelection:true
    });
    this.parentCombo = new Wtf.form.ComboBox({
        fieldLabel:'Cascade By',
        anchor:'95%',
        id:'ParentCmb'+this.id,
        mode:'local',
        triggerAction:'all',
        typeAhead:true,
        editable:false,
        allowBlank: false,
        disabled:true,
        store:this.parentComboStore,
        displayField:'comboname',
        valueField:'comboid'
    });

    this.subCheckBox = new Wtf.form.Checkbox({
        checked: false,
        boxLabel : "Auto populate on combo selection.",
        labelSeparator : ""
    });

    this.subCheckBox.on('check',function(){
        if(this.subCheckBox.getValue()) {
            this.parentCombo.enable();
            this.colCombo.enable();
            this.qType1.enable();
        } else {
            this.parentCombo.disable();
            this.colCombo.disable();
            this.qType1.disable();

            this.parentCombo.setValue(null);
            this.colCombo.setValue(null);
            this.qType1.setValue(null);
        }
    },this);

    Wtf.ReadOnlyControl.superclass.constructor.call(this, {
        title:'Config',
        layout:'fit',
        iconCls: 'winicon',
        modal: true,
        height:270,
        width:385,
        scope: this,
        id:'mycomp',
        buttons: [{
            text: "Save",
            handler: function() {
                if(this.addForm.form.isValid()){
                    var config={
                        fieldLabel : 'Label',
                        hiddenName : 'readOnlyValue',
                        isNew      :  'true',
                        name       :  'Label',
                        xtype      :  this.xtype,
                        readOnly : true
                    };
                    if(this.subCheckBox.getValue()) {
                        var rec = this.qType1.store.getAt(this.qType1.store.find('moduleid',this.qType1.getValue()));

    //                        var count = this.parentComboStore.getCount()+"";
    //                        if(count.length == 1)
    //                            count = "0"+count;
                        var colVal = this.colCombo.getValue().split(_reportHardcodeStr)[1];//+count;
                        if(rec.data.mastertype == 1){
                            config['dataStore'] = rec.data.moduleid;
                            config['mastertype'] = rec.data.mastertype;
                            config['parentFieldId'] = this.parentCombo.getValue();
                            config['parentComboName'] = Wtf.getCmp(this.parentCombo.getValue()).name;
                            config['readFieldName'] = colVal;
                            config['autoPopulate'] = true;
                        }else{
                            config['dataStore'] = rec.data.tablename;
                            config['mastertype'] = rec.data.mastertype;
                            config['name'] = colVal;
                            config['hiddenName'] = colVal;
                            config['parentFieldId'] = this.parentCombo.getValue();
                            config['parentComboName'] = Wtf.getCmp(this.parentCombo.getValue()).name;
                            config['readFieldName'] = colVal;
                            config['autoPopulate'] = true;
                        }
                    } else {
                        config['autoPopulate'] = false;
                    }
                    this.fireEvent("addConfig",config);
                    this.close();
                }
            },
            scope: this
        },{
            text:"Cancel",
            scope:this,
            handler:function(){
                this.close();
            }
        }],
        items:[
        this.pPanel = new Wtf.Panel({
            layout: 'fit',
            border: false,
            items: this.inP = new Wtf.Panel({
                layout: 'border',
                border: false,
                items: [{
                    region: 'north',
                    border: false,
                    height: 70,
                    cls:'windowHeader',
                    html:getHeader('images/createuser.gif',' Read only field','Configure read only field to auto-populate.')
                },{
                    region: 'center',
                    layout: 'fit',
                    bodyStyle:"background:#f1f1f1;",
                    border: false,
                    items: [this.addForm = new Wtf.form.FormPanel({
                        url: "jspfiles/forms.jsp",
                        region: "center",
                        bodyStyle: "padding: 10px;",
                        border: false,
                        labelWidth: 120,
                        height: 80,
                        buttonAlign: 'right',
                        items: [
                            this.subCheckBox,
                            this.qType1,
                            this.colCombo,
                            this.parentCombo
                        ]
                    })]
                }]
            })
        })]
    });

    this.qType1.on("select",function(obj,newval,oldval) {
        if(newval.data.mastertype == '0') {
            this.modColStore.load({
                params : {
                    moduleid : newval.data.moduleid
                }
            });
            this.colCombo.allowBlank = false;
        } else {
            this.modColStore.removeAll();
            this.colCombo.allowBlank = true;
        }
    },this);
}

Wtf.extend(Wtf.ReadOnlyControl, Wtf.Window, {

});

//Checkbox/Radio button group control
Wtf.groupingComponent=function(config){
//     Wtf.apply(this,config);

     this.buttons = [{
        text: 'OK',
        scope: this,
        handler: this.makeGroupConfig
    },{
        text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
        scope: this,
        handler: function() {
            this.close();
        }
    }];

    this.events = {
        "groupokclicked": true
    };

    this.boxLabelText = new Wtf.form.TextField({
        fieldLabel: 'New Box Label',
        anchor: '95%',
        maxLength: 100,
        scope:this
    });

    this.addBtn=new Wtf.Toolbar.Button({
        text:'Add Box Label',
        tooltip :'Click to add Box Label',
        handler : function(){
            var boxLabelText=this.boxLabelText.getValue();
            if (boxLabelText != "" & boxLabelText.match(fieldLabelRegex)!=null){
                if (this.boxLabelStore.find('boxLabel',boxLabelText) == -1 ){
                    var boxLabelRecord = new this.boxLabel({
                        boxLabel_id: 100,
                        boxLabel: boxLabelText
                    });
                    this.boxLabelStore.add(boxLabelRecord);
                    this.boxLabelText.setValue("");
                    this.boxLabelText.focus();

                }else{
                    msgBoxShow(39, 1);
                }
            } else{
                msgBoxShow(40, 1);
            }

        },
        scope: this
    });

    this.checkbox=new Wtf.grid.CheckboxSelectionModel();

    this.cm=new Wtf.grid.ColumnModel([
    {
        header: "Box Label",
        id :'boxLabel',
        dataIndex:'boxLabel',
        editor: new Wtf.form.TextField({
            allowBlank: false
        })
    },{
        header: "Delete",
        dataIndex:'delField',
        renderer : function(val) {
            return "<img id='DeleteImg' class='delete' src='images/Cancel.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete filter'></img>";
        }
    }
    ]);

    this.boxLabel = Wtf.data.Record.create([{
        name: 'boxLabel_id'
    },{
        name: 'boxLabel'
    }]);

    this.GridJsonReader = new Wtf.data.JsonReader({
        root: "data",
        totalProperty: 'count'
    }, this.boxLabel);

    this.boxLabelStore = new Wtf.data.Store({
        reader: this.GridJsonReader
    });


    this.boxLabelGrid=new Wtf.grid.EditorGridPanel({
        store: this.boxLabelStore,
        cm:this.cm,
        clicksToEdit:1,
        stripeRows: true,
        width:325,
        height:300,
        border:false,
        autoExpandColumn :'boxLabel',
        tbar:[this.addBtn,this.boxLabelText]

    });
    this.headerTitle='Make '+(this.xtype == 'radiogroup'?'radio':'checkbox')+' button groups';

    this.groupPanel = new Wtf.Panel({
        layout: 'fit',
        border: false,
        items: this.inP = new Wtf.Panel({
            layout: 'border',
            border: false,
            items: [{
                region: 'north',
                border: false,
                height: 70,
                cls:'windowHeader',
                html:getHeader('images/createuser.gif',this.headerTitle,'Specify details')
            },{
                region: 'center',
                bodyStyle:"background:#f1f1f1;",
                border: false,
                items: [
                this.addForm = new Wtf.form.FormPanel({
                    url: "jspfiles/admin/feedback.jsp",
                    region: "center",
                    bodyStyle: "padding: 10px;",
                    border: false,
                    labelWidth: 120,
                    buttonAlign: 'right',
                    items: [
                    this.fieldLabel=new Wtf.form.TextField({
                        fieldLabel: 'Field Label ',
                        allowBlank:false,
                        regex:fieldLabelRegex,
                        regexText : regexText,
                        maxLength:20
                    })

                    ]
                })
                ,this.boxLabelGrid
                ]
            }

            ]
        })
    });

   this.items=[this.groupPanel];
   this.boxLabelGrid.on('cellclick', this.deleteBoxLabel, this);
   Wtf.groupingComponent.superclass.constructor.call(this, config);
}
Wtf.extend(Wtf.groupingComponent, Wtf.Window, {
    title:this.title,
    layout:'fit',
    iconCls: 'winicon',
    modal: true,
    height:400,//220
    width:325,
    scope: this,

    makeGroupConfig:function(){
        var fieldLabel=this.fieldLabel.getValue();
        if (this.fieldLabel.validate() && this.boxLabelStore.getCount() > 0 ){
            var config={
                xtype:this.xtype,
                fieldLabel: fieldLabel,
                itemCls: 'x-check-group-alt',
                isNew : true,
                columns: 1,
                items:[]
            };

            var item;
            this.boxLabelStore.each(function(boxLabel){
                var boxlabel = boxLabel.data.boxLabel;
                item={
                    boxLabel:boxlabel,
                    isNew : true,
                    name:this.strformat(fieldLabel),
                    inputValue:this.strformat(boxlabel)
                };
                config.items.push(item);

            },this);

            this.fireEvent("groupokclicked",config);
            this.close();

        }else{
             msgBoxShow(41, 1);
        }
    },


    deleteBoxLabel:function(gd, ri, ci, e) {
        var event = e;
        if(event.target.className == "delete") {
            this.boxLabelStore.remove(this.boxLabelStore.getAt(ri));
        }
    },

    strformat:function(str){
        str=str.replace(/\-/g,'');// remove -
        str=str.replace(/\*/g,'');// remove *
        str=str.replace(/\//g,'');// remove /
        str=str.replace(/\./g,'') // remove .
        str = str.replace(/\s{2,}/g," "); // strip concecutive spaces
        str = str.replace(/^\s/,"");//trim leading space
        str = str.toLowerCase();
        // replace spaces with underscore
        str = str.replace(/\s/g,"_");
        return str;
    }
});

Wtf.comboSelectInterfaceComponent=function(config){
    Wtf.apply(this,config);

    this.events = {
        "addConfig": true
    };
    this.typeStore1 = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({
            root:'data'
        }, ["moduleid","modulename", "mastertype", "tablename"]),
        autoLoad : false,
        url : Wtf.req.mbuild+"form.do",
        baseParams : {
            action : 7,
            moduleid : this.moduleid
        }
    });
    this.typeStore1.load();

    this.modColStore = new Wtf.data.Store({
        url: Wtf.req.mbuild+'form.do',
        baseParams : {
            action : 15
        },
        method: 'POST',
        reader: new Wtf.data.KwlJsonReader({
            root:'data'
        }, ['displayfield','name'])
    });

    this.parentCombo = new Wtf.form.ComboBox({
        fieldLabel:'Cascade By',
        anchor:'95%',
        id:'ParentCmb'+this.id,
        mode:'local',
        triggerAction:'all',
        typeAhead:true,
        forceSelection :true,
        allowBlank: false,
        store:this.parentComboStore,
        displayField:'comboname',
        valueField:'comboid',
        disabled: true
    });
    this.casColCombo = new Wtf.form.ComboBox({
        fieldLabel: 'Cascade Column',
        store : this.modColStore,
        displayField:'displayfield',
        valueField : 'name',
        anchor:'95%',
        id:'casColCmb'+this.id,
        mode:'local',
        triggerAction:'all',
        typeAhead:true,
        forceSelection :true,
        allowBlank: false,
        disabled: true,
        emptyText : 'Select column to cascade'
    });

    this.subCheckBox = new Wtf.form.Checkbox({
        checked: false
    });

    this.subCheckBox.on('check',function(){
        if(this.subCheckBox.getValue()) {
            this.parentCombo.enable();
            this.casColCombo.enable();
        } else {
            this.parentCombo.disable();
            this.casColCombo.disable();
        }
    },this);

    Wtf.comboSelectInterfaceComponent.superclass.constructor.call(this, {
        title:'Config',
        layout:'fit',
        iconCls: 'winicon',
        modal: true,
        height:210,
        width:380,
        scope: this,
        id:'mycomp',
        buttons: [{
            text: "Save",
            handler: function() {
                if(this.addForm.form.isValid()){
                    var config={
                        fieldLabel : 'Text',
                        hiddenName : 'combovalue',
                        isNew      :  'true',
                        name       :  'Text',
                        xtype      :  this.xtype
                    };

                    if (this.xtype == "select"){
                        config['forceSelection']=true;
                        config['multiSelect']=true;
                    }
                    //var childObj = Wtf.getCmp(comboid).treePanel.getSelectionModel().selNode;
                    if(this.subCheckBox.getValue()) {
                        var parentTreeId = this.parentCombo.store.getAt(this.parentCombo.store.find('comboid',this.parentCombo.getValue())).data["comboTreeId"];
                        config['parentComboTreeId'] = parentTreeId;
                        config['casColName'] = this.casColCombo.getValue();
                        config['parentComboId'] = this.parentCombo.getValue();
                    }
                    //                                        config.elConfig['formPanelId'] = comboid;
                    var rec = this.qType1.store.getAt(this.qType1.store.find('moduleid',this.qType1.getValue()));
                    if(rec.data.mastertype == 1){
                        config['dataStore'] = rec.data.moduleid;
                        config['mastertype'] = rec.data.mastertype;
                    }else{
                        config['dataStore'] = rec.data.tablename;
                        config['mastertype'] = rec.data.mastertype;
                        var count = this.parentComboStore.getCount()+"";
                        if(count.length == 1)
                            count = "0"+count;
                        var colVal = this.colCombo.getValue().split(_reportHardcodeStr)[1]+count;
                        config['name'] = colVal;
                        config['hiddenName'] = colVal;
                    }
                    this.fireEvent("addConfig",config);
                    this.close();
                }
            },
            scope: this
        },{
            text:"Cancel",
            scope:this,
            handler:function(){
                //  this.removeNode(node1);
                this.close();
            }
        }],
        items:[
        this.pPanel = new Wtf.Panel({
            layout: 'fit',
            border: false,
            items: this.inP = new Wtf.Panel({
                layout: 'border',
                border: false,
                items: [{
                    region: 'north',
                    border: false,
                    height: 70,
                    cls:'windowHeader',
                    html:getHeader('images/createuser.gif',' Drop down list','Select a drop down list.')
                },{
                    region: 'center',
                    layout: 'fit',
                    bodyStyle:"background:#f1f1f1;",
                    border: false,
                    items: [this.addForm = new Wtf.form.FormPanel({
                        url: "jspfiles/admin/feedback.jsp",
                        region: "center",
                        bodyStyle: "padding: 10px;",
                        border: false,
                        labelWidth: 120,
                        height: 60,
                        buttonAlign: 'right',
                        items: [this.qType1 = new Wtf.form.ComboBox({
                            valueField: 'moduleid',
                            displayField: 'modulename',
                            store: this.typeStore1,
                            fieldLabel: 'Config table name',
                            typeAhead:true,
                            forceSelection :true,
                            allowBlank: false,
                            anchor: '95%',
                            mode: 'local',
                            triggerAction: 'all',
                            selectOnFocus: true,
                            emptyText: 'Select config table name...'
                        }),this.colCombo = new Wtf.form.ComboBox({
                            fieldLabel: 'Config columnname',
                            store : this.modColStore,
                            readOnly : true,
                            allowBlank: false,
                            anchor: '95%',
                            displayField:'displayfield',
                            valueField : 'name',
                            mode: 'local',
                            triggerAction: 'all',
                            emptyText : 'Select column to filter',
                            forceSelection:true
                        })
//                        this.subTypePanel = new Wtf.Panel({
//                            layout:'column',
//                            columnWidth:1,
//                            border:false,
//                            items:[{
//                                border:false,
//                                columnWidth:0.075,
//                                items:this.subCheckBox
//                            },{
//                                layout:'form',
//                                border:false,
//                                columnWidth:0.925,
//                                items:[
//                                    this.parentCombo,
//                                    this.casColCombo
//                                ]
//                            }]
//                        })
                                ]
                        })]
                }]
            })
        })]
    });

    this.qType1.on("select",function(obj,newval,oldval) {
        if(newval.data.mastertype == '0') {
            this.modColStore.load({
                params : {
                    moduleid : newval.data.moduleid
                }
            });
            this.colCombo.allowBlank = false;
        } else {
            this.modColStore.removeAll();
            this.colCombo.allowBlank = true;
        }
    },this);
    this.on("updateParentComboStore",this.updateParentComboStore,this)
}

Wtf.extend(Wtf.comboSelectInterfaceComponent, Wtf.Window, {

});

WtfComponents = {
    getComponents : function() {
        var docdata = {
            "id":"pkg-Wtf",
            "text":"Form Components",
            "iconCls":"icon-pkg",
            "cls":"package",
            "expanded": true,
            "children":[{
                "id":"pkg-form",
                "text":"Form",
                "iconCls":"icon-pkg",
                "cls":"package",
                "singleClickExpand":true,
                "children":[{
                    "text":"Checkbox",
                    "id":"Wtf.form.Checkbox",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A checkbox",
                    "config":{
                        xtype:'checkbox',
                        fieldLabel:'Label',
                        boxLabel:'Box label',
                        name:'checkbox',
                        inputValue:'cbvalue'
                    }
                },{
                    "text":"ComboBox",
                    "id":"Wtf.form.ComboBox",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A combo box",
                    "config":function(add){
                       if(!this.parentComboStore) {
                            this.parentComboRecord = Wtf.data.Record.create([{
                                name: 'comboid'
                            },{
                                name: 'comboname'
                            },{
                                name: 'comboTreeId'
                            }
                            ]);

                            this.parentComboStore = new Wtf.data.Store({
                                reader: new Wtf.data.KwlJsonReader({
                                    root:"data"
                                }, this.parentComboRecord),
                                url: Wtf.req.mbuild+'form.do'
                            });
                        }
                        var comboSelectInterfaceComponentObj=new Wtf.comboSelectInterfaceComponent({
                            xtype:'combo',
                            parentComboStore:this.parentComboStore

                        });
                        comboSelectInterfaceComponentObj.show();
                        comboSelectInterfaceComponentObj.on("addConfig", function(config){
                            add.call(this, config);
                        }, this);
                     }
                },{
                    "text":"DateField",
                    "id":"Wtf.form.DateField",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A Text Field where you can only enter a date",
                    "config":{
                        xtype:'datefield',
                        fieldLabel:'Date',
                        name:'datevalue'
                    }
                },{
                    "text":"FieldSet",
                    "id":"Wtf.form.FieldSet",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A Fieldset, containing other form elements",
                    "config":{
                        xtype:'fieldset' ,
                        title:'Legend',
                        autoHeight:true
                    }
                },{
                    "text":"HtmlEditor",
                    "id":"Wtf.form.HtmlEditor",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"An HTML Editor",
                    "config":{
                        xtype:'htmleditor',
                        hideLabel:true,
                        name:'htmleditor'
                    }
                },{
                    "text":"NumberField",
                    "id":"Wtf.form.NumberField",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A Text Field where you can only enter numbers",
                    "config":{
                        xtype:'numberfield',
                        fieldLabel:'Number',
                        name:'numbervalue'
                    }
                },{
                    "text":"Radio",
                    "id":"Wtf.form.Radio",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A radio form element",
                    "config":{
                        xtype:'radio',
                        fieldLabel:'Label',
                        boxLabel:'Box label',
                        name:'radio',
                        inputValue:'radiovalue'
                    }
                },{
                    "text":"TextArea",
                    "id":"Wtf.form.TextArea",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A Text Area",
                    "config":{
                        xtype:'textarea',
                        fieldLabel:'Text Area',
                        name:'longtextvalue'
                    }
                },{
                    "text":"TextField",
                    "id":"Wtf.form.TextField",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A Text Field",
                    "config":{
                        xtype:'textfield',
                        fieldLabel:'Text',
                        name:'textvalue'
                    }
                },{
                    "text":"FileUpload",
                    "id":"Wtf.form.FileUpload",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A Text Field with file upload",
                    "config":{
                        xtype:'textfield',
                        fieldLabel:'File',
                        name:'filevalue',
                        inputType:'file',
                        autoCreate : {tag: "input", type: "text", size: "20", autocomplete: "off"}
                    }
                },{
                    "text":"TimeField",
                    "id":"Wtf.form.TimeField",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A Text Field where you can only enter a time",
                    "config":{
                        xtype:'timefield',
                        fieldLabel:'Time',
                        name:'timevalue'
                    }
//To do - need to uncomment
//                },{
//                    "text":"GridPanel",
//                    "id":"Wtf.grid.GridPanel",
//                    "isClass":true,
//                    "iconCls":"icon-cmp",
//                    "cls":"cls",
//                    "leaf":true,
//                    "tooltip":"A Grid Panel",
//                    "config": this.addGrid
                },{

                    "text":"ReadOnlyControl",
                    "id":"Wtf.ReadOnlyControl",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A read only control",
                    "config":function(add){
                        if(!this.parentComboStore) {
                            this.parentComboRecord = Wtf.data.Record.create([{
                                name: 'comboid'
                            },{
                                name: 'comboname'
                            },{
                                name: 'comboTreeId'
                            }
                            ]);

                            this.parentComboStore = new Wtf.data.Store({
                                reader: new Wtf.data.KwlJsonReader({
                                    root:"data"
                                }, this.parentComboRecord),
                                url: Wtf.req.mbuild+'form.do'
                            });
                        }
                        var ReadOnlyControl=new Wtf.ReadOnlyControl({
                            xtype:'readOnlyCmp',
                            parentComboStore:this.parentComboStore
                        });
                        ReadOnlyControl.show();
                        ReadOnlyControl.on("addConfig", function(config){
                            add.call(this, config);
                        }, this);
                     }
                },{

                    "text":"RadioGroup","id":"Wtf.RadioGroup","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true,
                    "tooltip":"A radio group control to group radio buttons ",
                    "config":function(add){
                        var groupingComponentObj=new Wtf.groupingComponent({
                            xtype:'radiogroup',
                            title:'Radio Grouping'
                        });
                        groupingComponentObj.show();
                        groupingComponentObj.on("groupokclicked", function(config){
                            add.call(this, config);
                        }, this);

                     }
                },{
                    "text":"CheckboxGroup",
                    "id":"Wtf.CheckboxGroup",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A checkbox group control to group checkbox buttons ",
                    "config":function(add) {

                        var groupingComponentObj=new Wtf.groupingComponent({
                            xtype:'checkboxgroup',
                            title:'Checkbox Grouping'
                        });
                        groupingComponentObj.show();
                        groupingComponentObj.on("groupokclicked", function(config){
                            add.call(this, config);
                        }, this);


                    }
                },{
                    "text":"MultiSelectComboBox",
                    "id":"Wtf.form.MultiSelectComboBox",
                    "isClass":true,
                    "iconCls":"icon-cmp",
                    "cls":"cls",
                    "leaf":true,
                    "tooltip":"A MultiSelect ComboBox",
                    "config":function(add){
                       if(!this.parentComboStore) {
                            this.parentComboRecord = Wtf.data.Record.create([{
                                name: 'comboid'
                            },{
                                name: 'comboname'
                            },{
                                name: 'comboTreeId'
                            }
                            ]);

                            this.parentComboStore = new Wtf.data.Store({
                                reader: new Wtf.data.KwlJsonReader({
                                    root:"data"
                                }, this.parentComboRecord),
                                url: Wtf.req.mbuild+'form.do'
                            });
                        }
                        var comboSelectInterfaceComponentObj=new Wtf.comboSelectInterfaceComponent({
                            xtype:'select',
                            parentComboStore:this.parentComboStore

                        });
                        comboSelectInterfaceComponentObj.show();
                        comboSelectInterfaceComponentObj.on("addConfig", function(config){
                            add.call(this, config);
                        }, this);
                     }
                }],
                "pcount":0
            }],
            "pcount":9
        };
        return docdata;
    },

    addGrid: function(add){
        var win = new Wtf.common.WtfGridConfigWin({
            title: 'Grid Config',
            modal: true,
            iconCls: 'iconwin',
            id: this.id + "_gridConfigWindow",
            height: 370,
            modBuildObj: this,
            width: 425,
            resizable: false,
            border: false
        });
        win.show();
        win.on("okclicked", function(config){
            add.call(this, config);
        }, this);
    }
};

//commented code from line 259 to 1081 to remove layout and panel node from form builder tree
//
//            //                      },{
            //                          "id":"pkg-grid",
            //                          "text":"grid",
            //                          "iconCls":"icon-pkg",
            //                          "cls":"package",
            //                          "singleClickExpand":true,
            //                          "children":[{
            //                                  "text":"EditorGridPanel","id":"Wtf.grid.EditorGridPanel","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            //                              },{
            //                                  "text":"GridPanel","id":"Wtf.grid.GridPanel","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            //                              },{
            //                                  "text":"PropertyGrid","id":"Wtf.grid.PropertyGrid","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            //                              }],"pcount":0

//
//commented code from line 33 to 37 to remove 'form panel' subnode from form builder tree

            //                      ,{
            //                          "id":"pkg-layout",
            //                          "text":"Layout",
            //                          "iconCls":"icon-pkg",
            //                          "cls":"package",
            //                          "singleClickExpand":true,
            //                          "children":[{
            //                                  "text":"AbsoluteLayout","id":"Wtf.layout.AbsoluteLayout","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true,
            //			"tooltip":"Layout containing many elements, absolutely positionned with x/y values",
            //			"config":{layout:'absolute',title:'AbsoluteLayout Container'}
            //                              },{
            //                                  "text":"Accordion","id":"Wtf.layout.Accordion","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true,
            //                                  "tooltip":"Layout as accordion", "config":function(add) {
            //			var w = new Wtf.Window({
            //				title:"New Accordion Layout",
            //                                width:619,
            //				height:465,
            //                                items:[{
            //						xtype:"form",
            //						labelWidth:120,
            //                                                items:[{
            //								border:false,
            //								hideLabels:true,
            //                                                                layout:"form",
            //                                                                items:[{
            //										xtype:"checkbox",
            //										boxLabel:"collapseFirst <span class=\"notice\">True to make sure the collapse/expand toggle button always renders first (to the left of) any other tools in the contained panels' title bars, false to render it last (defaults to false).</span>",
            //										name:"collapseFirst"
            //									},{
            //										xtype:"checkbox",
            //										boxLabel:"autoWidth <span class=\"notice\">True to set each contained item's width to 'auto', false to use the item's current width (defaults to true).</span>",
            //										name:"autoWidth",
            //										checked:true
            //									},{
            //										xtype:"checkbox",
            //										boxLabel:"animate <span class=\"notice\">True to swap the position of each panel as it is expanded so that it becomes the first item in the container, false to keep the panels in the rendered order. This is NOT compatible with \"animate:true\" (defaults to false).</span>",
            //										name:"animate"
            //									},{
            //										xtype:"checkbox",
            //										boxLabel:"activeOnTop <span class=\"notice\">True to swap the position of each panel as it is expanded so that it becomes the first item in the container, false to keep the panels in the rendered order. This is NOT compatible with \"animate:true\" (defaults to false).</span>",
            //										name:"activeOnTop"
            //									},{
            //										xtype:"checkbox",
            //										boxLabel:"fill <span class=\"notice\">True to adjust the active item's height to fill the available space in the container, false to use the item's current height, or auto height if not explicitly set (defaults to true).</span>",
            //										name:"fill",
            //										checked:true
            //									},{
            //										xtype:"checkbox",
            //										boxLabel:"hideCollapseTool <span class=\"notice\">True to hide the contained panels' collapse/expand toggle buttons, false to display them (defaults to false). When set to true, titleCollapse should be true also.</span>",
            //										name:"hideCollapseTool"
            //									},{
            //										xtype:"checkbox",
            //										boxLabel:"titleCollapse <span class=\"notice\">True to allow expand/collapse of each contained panel by clicking anywhere on the title bar, false to allow expand/collapse only when the toggle tool button is clicked (defaults to true). When set to false, hideCollapseTool should be false also.</span>",
            //										name:"titleCollapse",
            //										checked:true
            //									}]
            //							},{
            //								xtype:"textfield",
            //								fieldLabel:"extraCls",
            //								name:"extraCls"
            //							},{
            //								xtype:"checkbox",
            //								boxLabel:"<span class=\"notice\">Add dummy panels to help render layout (useful for debug)</span>",
            //								name:"adddummy",
            //								checked:true,
            //								fieldLabel:"Add dummy panels"
            //							}]
            //					}],
            //					buttons:[{
            //						text:'Ok',
            //						scope:this,
            //						handler:function() {
            //							var values = w.items.first().form.getValues();
            //							w.close();
            //							var config = {layout:'accordion',layoutConfig:{},items:[]};
            //							config.layoutConfig.activeOnTop = (values.activeOnTop ? true : false);
            //							config.layoutConfig.animate = (values.animate ? true : false);
            //							config.layoutConfig.autoWidth = (values.autoWidth ? true : false);
            //							config.layoutConfig.collapseFirst = (values.collapseFirst ? true : false);
            //							config.layoutConfig.fill = (values.fill ? true : false);
            //							config.layoutConfig.hideCollapseTool = (values.hideCollapseTool ? true : false);
            //							config.layoutConfig.titleCollapse = (values.titleCollapse ? true : false);
            //							if (values.extraCls) { config.layoutConfig.extraCls = values.extraCls; }
            //							if (values.adddummy) {
            //								config.items.push(
            //									{title:'Panel 1',html:'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Sed non risus.'},
            //									{title:'Panel 2',html:'Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor.'},
            //									{title:'Panel 3',html:'Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi.'},
            //									{title:'Panel 4',html:'Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat.'});
            //							}
            //							add.call(this, config);
            //						}
            //					},{
            //						text:'Cancel',
            //						handler:function() {w.close();}
            //					}]
            //			});
            //			w.show();
            //
            //		}
            //                              },{
            //                                  "text":"AnchorLayout","id":"Wtf.layout.AnchorLayout","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true,
            //			"tooltip":"Layout containing many elements, sized with 'anchor' percentage values",
            //			"config":{layout:'anchor',title:'AnchorLayout Container'}
            //                              },{
            //                                  "text":"BorderLayout","id":"Wtf.layout.BorderLayout","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true,
            //                                  "tooltip":"Layout with regions", "config":function(add,parent) {
            //				var w = new Wtf.Window({
            //					title:"Border Layout",
            //					width:550,
            //					height:400,
            //					layout:'fit',
            //					items:[{
            //						autoScroll:true,
            //						xtype:"form",
            //						frame:true,
            //						defaults:{
            //							style:"margin:10px"
            //						},
            //						items:[{
            //								xtype:"fieldset",
            //								title:"Center",
            //								autoHeight:true,
            //								items:[{
            //										xtype:"textfield",
            //										fieldLabel:"Title",
            //										name:"title_center",
            //										width:299
            //									}]
            //							},{
            //								xtype:"fieldset",
            //								title:"Add north region",
            //								autoHeight:true,
            //								checkboxToggle:true,
            //								collapsed:true,
            //								checkboxName:"active_north",
            //								items:[{
            //										xtype:"textfield",
            //										fieldLabel:"Title",
            //										name:"title_north",
            //										width:299
            //									},{
            //										layout:"table",
            //										items:[{
            //												layout:"form",
            //												items:[{
            //														xtype:"numberfield",
            //														fieldLabel:"Height (px)",
            //														name:"height_north",
            //														allowDecimals:false,
            //														allowNegative:false,
            //														width:66
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"split_north",
            //														boxLabel:"Split"
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"collapsible_north",
            //														boxLabel:"Collapsible"
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"titleCollapse_north",
            //														boxLabel:"TitleCollapse"
            //													}]
            //											}]
            //									}]
            //							},{
            //								xtype:"fieldset",
            //								title:"Add south region",
            //								autoHeight:true,
            //								checkboxToggle:true,
            //								collapsed:true,
            //								checkboxName:"active_south",
            //								items:[{
            //										xtype:"textfield",
            //										fieldLabel:"Title",
            //										name:"title_south",
            //										width:299
            //									},{
            //										layout:"table",
            //										items:[{
            //												layout:"form",
            //												items:[{
            //														xtype:"numberfield",
            //														fieldLabel:"Height (px)",
            //														name:"height_south",
            //														allowDecimals:false,
            //														allowNegative:false,
            //														width:66
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"split_south",
            //														boxLabel:"Split"
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"collapsible_south",
            //														boxLabel:"Collapsible"
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"titleCollapse_south",
            //														boxLabel:"TitleCollapse"
            //													}]
            //											}]
            //									}]
            //							},{
            //								xtype:"fieldset",
            //								title:"Add west region",
            //								autoHeight:true,
            //								checkboxToggle:true,
            //								collapsed:true,
            //								checkboxName:"active_west",
            //								items:[{
            //										xtype:"textfield",
            //										fieldLabel:"Title",
            //										name:"title_west",
            //										width:299
            //									},{
            //										layout:"table",
            //										items:[{
            //												layout:"form",
            //												items:[{
            //														xtype:"numberfield",
            //														fieldLabel:"Width (px)",
            //														name:"width_west",
            //														allowDecimals:false,
            //														allowNegative:false,
            //														width:66
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"split_west",
            //														boxLabel:"Split"
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"collapsible_west",
            //														boxLabel:"Collapsible"
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"titleCollapse_west",
            //														boxLabel:"TitleCollapse"
            //													}]
            //											}]
            //									}]
            //							},{
            //								xtype:"fieldset",
            //								title:"Add east region",
            //								autoHeight:true,
            //								checkboxToggle:true,
            //								collapsed:true,
            //								checkboxName:"active_east",
            //								items:[{
            //										xtype:"textfield",
            //										fieldLabel:"Title",
            //										name:"title_east",
            //										width:299
            //									},{
            //										layout:"table",
            //										items:[{
            //												layout:"form",
            //												items:[{
            //														xtype:"numberfield",
            //														fieldLabel:"Width (px)",
            //														name:"width_east",
            //														allowDecimals:false,
            //														allowNegative:false,
            //														width:66
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"split_east",
            //														boxLabel:"Split"
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"collapsible_east",
            //														boxLabel:"Collapsible"
            //													}]
            //											},{
            //												layout:"form",
            //												hideLabels:true,
            //												style:"margin-left:10px",
            //												items:[{
            //														xtype:"checkbox",
            //														name:"titleCollapse_east",
            //														boxLabel:"TitleCollapse"
            //													}]
            //											}]
            //									}]
            //							}],
            //						buttons:[{
            //							text:'Ok',
            //							scope:this,
            //							handler:function() {
            //								var values = w.items.first().form.getValues();
            //								w.close();
            //								var config = {layout:'border',items:[]};
            //								config.items.push({region:'center',title:values.title_center||null});
            //								Wtf.each(['north','south','west','east'], function(r) {
            //									if (values['active_'+r]) {
            //										config.items.push({
            //											region        : r,
            //											title         : values['title_'+r]||null,
            //											width         : parseInt(values['width_'+r], 10)||null,
            //											height        : parseInt(values['height_'+r], 10)||null,
            //											split         : (values['split_'+r]?true:null),
            //											collapsible   : (values['collapsible_'+r]?true:null),
            //											titleCollapse : (values['titleCollapse_'+r]?true:null)
            //										});
            //									}
            //								});
            //								if (parent) { parent.layout = 'fit'; }
            //								add.call(this, config);
            //							}
            //						},{
            //							text:'Cancel',
            //							handler:function() {w.close();}
            //						}]
            //					}]
            //				});
            //				w.show();
            //			}
            ////                              },{
            ////                                  "text":"BorderLayout.Region","id":"Wtf.layout.BorderLayout.Region","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true
            ////                              },{
            ////                                  "text":"BorderLayout.SplitRegion","id":"Wtf.layout.BorderLayout.SplitRegion","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true
            //                              },{
            //                                  "text":"CardLayout","id":"Wtf.layout.CardLayout","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true,
            //			"tooltip":"Layout containing many elements, only one can be displayed at a time",
            //			"config":{layout:'card',title:'CardLayout Container'}
            //                              },{
            //                                  "text":"ColumnLayout","id":"Wtf.layout.ColumnLayout","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true,
            //                                  "tooltip":"Layout of columns", "config":function(add) {
            //			var w = new Wtf.Window({
            //        width:425,
            //        height:349,
            //        layout:"fit",
            //        title:"New Column Layout",
            //        items:[{
            //            xtype:"form",
            //            frame:true,
            //            items:[{
            //                columns:"3",
            //                layout:"table",
            //                layoutConfig:{
            //                  columns:3
            //                },
            //                defaults:{
            //                  style:"margin:2px"
            //                },
            //                items:[{
            //                    html:"Column"
            //                  },{
            //                    html:"Size *"
            //                  },{
            //                    html:"Title **"
            //                  },{
            //                    xtype:"checkbox",
            //										name:'active_1'
            //                  },{
            //                    xtype:"textfield",
            //                    maskRe:/[0-9%]/,
            //                    width:53,
            //										name:'size_1'
            //                  },{
            //                    xtype:"textfield",
            //										name:'title_1'
            //                  },{
            //                    xtype:"checkbox",
            //										name:'active_2'
            //                  },{
            //                    xtype:"textfield",
            //                    maskRe:/[0-9.%]/,
            //                    width:53,
            //										name:'size_2'
            //                  },{
            //                    xtype:"textfield",
            //										name:'title_2'
            //                  },{
            //                    xtype:"checkbox",
            //										name:'active_3'
            //                  },{
            //                    xtype:"textfield",
            //                    maskRe:/[0-9.%]/,
            //                    width:53,
            //										name:'size_3'
            //                  },{
            //                    xtype:"textfield",
            //										name:'title_3'
            //                  },{
            //                    xtype:"checkbox",
            //										name:'active_4'
            //                  },{
            //                    xtype:"textfield",
            //                    maskRe:/[0-9.%]/,
            //                    width:53,
            //										name:'size_4'
            //                  },{
            //                    xtype:"textfield",
            //										name:'title_4'
            //                  },{
            //                    xtype:"checkbox",
            //										name:'active_5'
            //                  },{
            //                    xtype:"textfield",
            //                    maskRe:/[0-9.%]/,
            //                    width:53,
            //										name:'size_5'
            //                  },{
            //                    xtype:"textfield",
            //										name:'title_5'
            //                  },{
            //                    xtype:"checkbox",
            //										name:'active_6'
            //                  },{
            //                    xtype:"textfield",
            //                    maskRe:/[0-9.%]/,
            //                    width:53,
            //										name:'size_6'
            //                  },{
            //                    xtype:"textfield",
            //										name:'title_6'
            //                  }]
            //              },{
            //								html:"* Size : can be a percentage of total width (i.e. 33%),"+
            //									"a fixed with (i.e. 120), or empty (autosize)<br/>"+
            //									"** Title : not set if empty"
            //							}]
            //          }],
            //					buttons:[{
            //						text:'Ok',
            //						scope:this,
            //						handler:function() {
            //							var values = w.items.first().form.getValues();
            //							w.close();
            //							var config = {layout:'column',items:[]};
            //							Wtf.each([1,2,3,4,5,6], function(r) {
            //								if (values['active_'+r]) {
            //									var item = {title:values['title_'+r]||null};
            //									var widthVal = values['size_'+r];
            //									var width = parseInt(widthVal,10);
            //									if (!isNaN(width)) {
            //										if (widthVal[widthVal.length-1] == '%') {
            //											item.columnWidth = width/100;
            //										} else {
            //											item.width = width;
            //										}
            //									}
            //									config.items.push(item);
            //								}
            //							});
            //							add.call(this, config);
            //						}
            //					},{
            //						text:'Cancel',
            //						handler:function() {w.close();}
            //					}]
            //			});
            //			w.show();
            //
            //		}
            ////                              },{
            ////                                  "text":"ContainerLayout","id":"Wtf.layout.ContainerLayout","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true,
            ////                                  "tooltip":"Layout as a form",
            ////                                  "config":{layout:'auto',title:'Form Layout Container'}
            //                              },{
            //                                  "text":"FitLayout","id":"Wtf.layout.FitLayout","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true,
            //			"tooltip":"Layout containing only one element, fitted to container",
            //			"config":{layout:'fit',title:'FitLayout Container'}
            //                              },{
            //                                  "text":"FormLayout","id":"Wtf.layout.FormLayout","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true,
            //                                  "tooltip":"Layout as a form",
            //                                  "config":{layout:'form',title:'Form Layout Container'}
            //                              },{
            //                                  "text":"TableLayout","id":"Wtf.layout.TableLayout","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true,
            //                                  "tooltip":"Layout as a table", "config":function(add) {
            //			var w = new Wtf.Window({
            //        width:300,
            //        height:300,
            //        layout:"fit",
            //        title:"New Table Layout",
            //        items:[{
            //            xtype:"form",
            //            frame:true,
            //            labelWidth:120,
            //            items:[{
            //                layout:"table",
            //                layoutConfig:{
            //                  columns:2
            //                },
            //                items:[{
            //                    layout:"form",
            //                    items:[{
            //                        xtype:"numberfield",
            //                        fieldLabel:"Columns x Rows",
            //                        width:48,
            //                        allowNegative:false,
            //                        allowDecimals:false,
            //                        name:"cols"
            //                      }]
            //                  },{
            //                    layout:"form",
            //                    labelWidth:10,
            //                    labelSeparator:" ",
            //                    style:"margin-left:5px",
            //                    items:[{
            //                        xtype:"numberfield",
            //                        fieldLabel:"x",
            //                        width:48,
            //                        allowNegative:false,
            //                        allowDecimals:false,
            //                        name:"rows"
            //                      }]
            //                  }]
            //              },{
            //                xtype:"textfield",
            //                fieldLabel:"Cells padding (px)",
            //                width:48,
            //                name:"cellpadding"
            //              },{
            //                xtype:"textfield",
            //                fieldLabel:"Cells margin (px)",
            //                width:48,
            //                name:"cellmargin"
            //              },{
            //                xtype:"checkbox",
            //								fieldLabel:"Borders",
            //								name:"borders",
            //								checked:true
            //              },{
            //                xtype:"checkbox",
            //								fieldLabel:"Add some content (useful for debug)",
            //								name:"addcontent",
            //								checked:true
            //              }]
            //          }],
            //					buttons:[{
            //						text:'Ok',
            //						scope:this,
            //						handler:function() {
            //							var values = w.items.first().form.getValues();
            //							var cols = parseInt(values.cols,10);
            //							var rows = parseInt(values.rows,10);
            //							if (isNaN(cols) || isNaN(rows)) {
            //								Wtf.Msg.alert("Error", "Columns/Rows are incorrect");
            //								return;
            //							}
            //							w.close();
            //							var config = {layout:'table',layoutConfig:{columns:cols},items:[]};
            //							for (var i = 0; i < cols; i++) {
            //								for (var j = 0; j < rows; j++) {
            //									config.items.push({html:(values.addcontent?'col '+i+', row '+j:null)});
            //								}
            //							}
            //							var defaults = {};
            //							var pad = parseInt(values.cellpadding,10);
            //							if (!isNaN(pad)) { defaults.bodyStyle = 'padding:'+pad+'px;'; }
            //							var margin = parseInt(values.cellmargin,10);
            //							if (!isNaN(margin)) { defaults.style = 'margin:'+margin+'px;'; }
            //							if (!values.borders) { defaults.border = false; }
            //							if (defaults != {}) { config.defaults = defaults; }
            //							add.call(this, config);
            //						}
            //					},{
            //						text:'Cancel',
            //						handler:function() {w.close();}
            //					}]
            //			});
            //			w.show();
            //
            //		}
            //                              }],"pcount":0
            ////                      },{
            ////                          "id":"pkg-menu",
            ////                          "text":"menu",
            ////                          "iconCls":"icon-pkg",
            ////                          "cls":"package",
            ////                          "singleClickExpand":true,
            ////                          "children":[{
            ////                                  "text":"Adapter","id":"Wtf.menu.Adapter","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.BaseItem.html","text":"BaseItem","id":"Wtf.menu.BaseItem","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.CheckItem.html","text":"CheckItem","id":"Wtf.menu.CheckItem","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.ColorItem.html","text":"ColorItem","id":"Wtf.menu.ColorItem","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.ColorMenu.html","text":"ColorMenu","id":"Wtf.menu.ColorMenu","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.DateItem.html","text":"DateItem","id":"Wtf.menu.DateItem","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.DateMenu.html","text":"DateMenu","id":"Wtf.menu.DateMenu","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.Item.html","text":"Item","id":"Wtf.menu.Item","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.Menu.html","text":"Menu","id":"Wtf.menu.Menu","isClass":true,"iconCls":"icon-cls","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.MenuMgr.html","text":"MenuMgr","id":"Wtf.menu.MenuMgr","isClass":true,"iconCls":"icon-static","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.Separator.html","text":"Separator","id":"Wtf.menu.Separator","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                              },{
            ////                                  "href":"output\/Wtf.menu.TextItem.html","text":"TextItem","id":"Wtf.menu.TextItem","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                              }],"pcount":0
            ////                      },{
            ////                          "id":"pkg-tree",
            ////                          "text":"tree",
            ////                          "iconCls":"icon-pkg",
            ////                          "cls":"package",
            ////                          "singleClickExpand":true,
            ////                          "children":[{
            ////                                  "text":"TreeEditor","id":"Wtf.tree.TreeEditor","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                              },{
            ////                                  "text":"TreePanel","id":"Wtf.tree.TreePanel","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                              }],"pcount":0
            //                      },{
            //                          "id":"pkg-panel",
            //                          "text":"Panel",
            //                          "iconCls":"icon-pkg",
            //                          "cls":"package",
            //                          "singleClickExpand":true,
            //                          "children":[{
            //                          "text":"Panel","id":"Wtf.Panel","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true,
            //				"tooltip":"A simple panel with default layout",
            //				"config":{xtype:'panel',title:'Panel'}
            ////                      },{
            ////                          "text":"ProgressBar","id":"Wtf.ProgressBar","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            //                      },{
            //                          "text":"TabPanel","id":"Wtf.TabPanel","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true,
            //                          "tooltip":"A panel with many tabs", "config": function(add) {
            //                            var w = new Wtf.Window({
            //                                width:586,
            //                                height:339,
            //                                title:"New Tab Panel",
            //                                items:[{
            //                                    xtype:"form",
            //                                    frame:true,
            //                                    items:[{
            //                                        layout:"table",
            //                                        layoutConfig:{
            //                                          columns:2
            //                                        },
            //                                        defaults:{
            //                                          style:"margin:1px;",
            //                                          border:true
            //                                        },
            //                                        xtype:"fieldset",
            //                                        title:"Tabs",
            //                                        autoHeight:true,
            //                                        items:[{
            //                                            title:"Title"
            //                                          },{
            //                                            title:"activeTab"
            //                                          },{
            //                                            xtype:"textfield",
            //                                            name:"title_1",
            //                                            width:200
            //                                          },{
            //                                            xtype:"radio",
            //                                            fieldLabel:"Label",
            //                                            boxLabel:"This tab is the default active one",
            //                                            name:"active",
            //                                                                                                        inputValue:0
            //                                          },{
            //                                            xtype:"textfield",
            //                                            name:"title_2",
            //                                            width:200
            //                                          },{
            //                                            xtype:"radio",
            //                                            fieldLabel:"Label",
            //                                            boxLabel:"This tab is the default active one",
            //                                            name:"active",
            //                                                                                                        inputValue:1
            //                                          },{
            //                                            xtype:"textfield",
            //                                            name:"title_3",
            //                                            width:200
            //                                          },{
            //                                            xtype:"radio",
            //                                            fieldLabel:"Label",
            //                                            boxLabel:"This tab is the default active one",
            //                                            name:"active",
            //                                                                                                        inputValue:2
            //                                          },{
            //                                            xtype:"textfield",
            //                                            name:"title_4",
            //                                            width:200
            //                                          },{
            //                                            xtype:"radio",
            //                                            fieldLabel:"Label",
            //                                            boxLabel:"This tab is the default active one",
            //                                            name:"active",
            //                                                                                                        inputValue:3
            //                                          },{
            //                                            xtype:"textfield",
            //                                            name:"title_5",
            //                                            width:200
            //                                          },{
            //                                            xtype:"radio",
            //                                            fieldLabel:"Label",
            //                                            boxLabel:"This tab is the default active one",
            //                                            name:"active",
            //                                                                                                        inputValue:4
            //                                          },{
            //                                            xtype:"textfield",
            //                                            name:"title_6",
            //                                            width:200
            //                                          },{
            //                                            xtype:"radio",
            //                                            fieldLabel:"Label",
            //                                            boxLabel:"This tab is the default active one",
            //                                            name:"active",
            //                                                                                                        inputValue:5
            //                                          }]
            //                                      }]
            //                                  }],
            //                                                                buttons:[{
            //                                                                        text:'Ok',
            //                                                                        scope:this,
            //                                                                        handler:function() {
            //                                                                                var values = w.items.first().form.getValues();
            //                                                                                w.close();
            //                                                                                var config = {xtype:'tabpanel',items:[]};
            //                                                                                var activeTab = 0;
            //                                                                                Wtf.each([1,2,3,4,5,6], function(i) {
            //                                                                                        if (values['title_'+i]) {
            //                                                                                                config.items.push({xtype:'panel',title:values['title_'+i]});
            //                                                                                                if (values.active == i) { activeTab = i; }
            //                                                                                        }
            //                                                                                });
            //                                                                                config.activeTab = activeTab;
            //                                                                                add.call(this, config);
            //                                                                        }
            //                                                                },{
            //                                                                        text:'Cancel',
            //                                                                        handler:function() {w.close();}
            //                                                                }]
            //                                                });
            //                                                w.show();
            //
            //                                        }
            ////                      },{
            ////                          "text":"Toolbar","id":"Wtf.Toolbar","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                      },{
            ////                          "text":"Toolbar.Button","id":"Wtf.Toolbar.Button","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                      },{
            ////                          "text":"Viewport","id":"Wtf.Viewport","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                      },{
            ////                          "text":"Window","id":"Wtf.Window","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            //                      }],"pcount":0
            ////                  },{
            ////                      "text":"Button","id":"Wtf.Button","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true,
            ////                      "tooltip":"A Button",
            ////                      "config":{xtype:'button',text:'Button',name:'button'}
            ////                      },{
            ////                          "text":"ColorPalette","id":"Wtf.ColorPalette","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true,
            ////                          "tooltip":"A panel containing colors",
            ////                          "config":{xtype:'colorpalette',fieldLabel:'Color Palette',name:'palette'}
            ////                      },{
            ////                          "text":"CycleButton","id":"Wtf.CycleButton","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                      },{
            ////                          "text":"DataView","id":"Wtf.DataView","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                      },{
            ////                          "text":"DatePicker","id":"Wtf.DatePicker","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true,
            ////                          "tooltip":"A Date picker",
            ////                          "config":{xtype:'datepicker',fieldLabel:'Date Picker',name:'datepicker'}
            ////                      },{
            ////                          "text":"Editor","id":"Wtf.Editor","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            ////                      },{
            ////                          "text":"PagingToolbar","id":"Wtf.PagingToolbar","isClass":true,"iconCls":"icon-cmp","cls":"cls","leaf":true
            //                      }
