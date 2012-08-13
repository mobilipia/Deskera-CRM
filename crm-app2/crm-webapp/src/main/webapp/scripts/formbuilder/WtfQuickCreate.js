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

Wtf.form.TabFormPanel = function(config){
    Wtf.apply(this, config);//Needs to comment for implement
    Wtf.form.TabFormPanel.superclass.constructor.call(this, config);
    this.addEvents({
        "onsuccess": true,
        "rowselect":true,
        "SaveClick":true
    });
};

Wtf.extend(Wtf.form.TabFormPanel, Wtf.Panel, {
    gridArray: {},
    autoScroll: true,
    bodyStyle:"background-color:#F6F6F6;",
    initComponent: function(conf){
        Wtf.form.TabFormPanel.superclass.initComponent.call(this, conf);
        this.maxcombcnt = 0;
        this.loadCnt = 0;
        this.createComboStore(this.itemArr.items);
        itemArr = this.checkGridItem(this.itemArr.items);
        this.formPanel = new Wtf.Panel({
                labelAlign:"left",
                //layout:'fit',
                //autoScroll : true,
                fileUpload:this.fileupload,
                border : false,
                cls : 'fieldformpanel',
                id :'quickCreateForm'+this.id,
                labelWidth:100,
                buttonAlign:"left",
                items : this.itemArr,
                buttons : [{
                    text: this.editFlag ==true ? 'Update' : 'Submit',
                    scope: this,
                    handler: this.onFormBtnClick
                }]
        });
        this.add(this.formPanel);
        this.panelForm = this.formPanel.items.items[0].form;
        if(this.maxcombcnt==0) {
            this.formPanel.on("render",function(){
                if(this.editFlag) {
                    this.fillFormFields();
                }    
            },this);
        }
    },
    
    checkGridItem: function(item){
        if(item){
        for(var cnt = 0; cnt < item.length; cnt++){
            if(item[cnt].xtype == "WtfGridPanel" || item[cnt].xtype == "WtfEditorGridPanel"){
                item[cnt].id = "QuickCreate_" + item[cnt].id;
                this.gridArray[item[cnt].name] = item[cnt].id;
                item[cnt].xtype = "WtfEditorGridPanel";
                item[cnt].insertMode = true;
            }
            if(item[cnt].items !== undefined){
                this.checkGridItem(item[cnt].items);
            }
        }
        }
        return item;
    },
    
    parentComboLoad : function(store, record, opt) {
        var o = opt.params;
        var nameValue = Wtf.getCmp(o.comboID).name;
        var selectedID = this.selectedRec.data[nameValue];
        Wtf.getCmp(o.comboID).setValue(selectedID);
        for(var cnt=0; cnt < o.childComboID.length; cnt++) {
            var p = {};
            var casColName = Wtf.getCmp(o.childComboID[cnt]).casColName;
            p['comboID'] = o.childComboID[cnt];
            p['cascadeCombo'] = 1;
            p['cascadeComboName'] = casColName;
            p['cascadeComboRefName'] = nameValue,
//            p['cascadeComboName'] = o.selectedName;
            p['cascadeComboValue'] = selectedID;

            Wtf.getCmp(o.childComboID[cnt]).store.load({
                params: p
            });
            Wtf.getCmp(o.childComboID[cnt]).store.on('load', this.childComboLoad, this);
        }
        store.un('load', this.parentComboLoad, this);
    },
    
    childComboLoad : function(store, record, opt) {
        var o = opt.params;
        Wtf.getCmp(o.comboID).setValue(this.selectedRec.data[Wtf.getCmp(o.comboID).name]);
        store.un('load', this.childComboLoad, this);
    },

    comboLoad : function(store, record, opt) {
        var o = opt.params;
        Wtf.getCmp(o.comboID).setValue(this.selectedRec.data[Wtf.getCmp(o.comboID).name]);
        store.un('load', this.comboLoad, this);
    },
    
    fillFormFields:function() {
        var itemsa1 = this.itemArr.items[0].items;
        if(itemsa1) {
            for(var cnt1=0;cnt1<itemsa1.length;cnt1++) {
                var itemsa = itemsa1[cnt1].items[0].items;
                if(itemsa) {
                    for(var cnt=0;cnt<itemsa.length;cnt++) {
                        var xtype = itemsa[cnt].xtype;
                        if (xtype && ['panel','viewport','form','window','tabpanel','toolbar','fieldset'].indexOf(xtype) == -1) {
                            if(itemsa[cnt].xtype == 'combo' || itemsa[cnt].xtype == 'select' ) {
                                var o = {};
                                if(itemsa[cnt].childComboId) {
                                    o['comboID'] = itemsa[cnt].id;
                                    o['selectedName'] = itemsa[cnt].name;
                                    o['childComboID'] = itemsa[cnt].childComboId;
                                    itemsa[cnt].store.on('load', this.parentComboLoad, this);
                                    itemsa[cnt].store.load({
                                        params: o
                                    });
                                } else if(!itemsa[cnt].childComboId && !itemsa[cnt].parentComboId){
                                    o['comboID'] = itemsa[cnt].id;
                                    itemsa[cnt].store.on('load', this.comboLoad, this);
                                    itemsa[cnt].store.load({
                                        params: o
                                    });
                                }

                            } else if(itemsa[cnt].xtype == 'datefield') {
                                Wtf.getCmp(itemsa[cnt].id).setValue(Date.parseDate(this.selectedRec.data[itemsa[cnt].name],'Y-m-d'));
                            } else if(itemsa[cnt].xtype == 'radiogroup') {
                                var container=itemsa[cnt].items;
                                for(var i=0;i<container.length;i++){
                                    if( this.selectedRec.data[itemsa[cnt].name] == container[i].inputValue ){
        //                                Wtf.getCmp(container[i].id).setValue(true);
        //                                break;
                                        var compID = container[i].id;
                                        this.taTask = new Wtf.util.DelayedTask(function(compID){
                                            Wtf.getCmp(compID).setValue(true);
                                        }, this, [compID]);
                                        this.taTask.delay(100);
                                        break;
                                    }
                                }

                            }else if(itemsa[cnt].xtype == 'checkboxgroup') {
                                var checkboxstr=this.selectedRec.data[itemsa[cnt].name];
                                var checkboxArray = new Array();
                                checkboxArray=checkboxstr.split(', ');

                                var container=itemsa[cnt].items;
                                for(var i=0;i<container.length;i++){
                                    if( checkboxArray.inArray(container[i].inputValue) ){
        //                                Wtf.getCmp(container[i].id).setValue(true);
                                        var compID = container[i].id;
                                        this.taTask1 = new Wtf.util.DelayedTask(function(compID){
                                            Wtf.getCmp(compID).setValue(true);
                                        }, this, [compID]);
                                        this.taTask1.delay(100);
                                    }
                                }
                            }else if(itemsa[cnt].xtype == 'radio') {
                                if (this.selectedRec.data[itemsa[cnt].name] == 'true'){
                                    Wtf.getCmp(itemsa[cnt].id).setValue(true);
                                }
                            } else{
                                if(!itemsa[cnt].inputType && itemsa[cnt].inputTypy != "file"){
                                    Wtf.getCmp(itemsa[cnt].id).setValue(this.selectedRec.data[itemsa[cnt].name]);
                                }
                            }
                        }
                    }
                }
            }
        }

        for(var c in this.gridArray) {
            var gridObj = Wtf.getCmp(this.gridArray[c]);
            if(gridObj !== undefined){
                if(gridObj.gridRendered){
                    var gData = eval("(" + this.selectedRec.data[c] + ")");
                    var oStore = gridObj.getStore();
                    oStore.loadData(gData, false)
                } else {
                    gridObj.on("gridrendered", function(gridObject){
                        var gData = eval("(" + this.selectedRec.data[c] + ")");
                        var oStore = gridObject.getStore();
                        oStore.loadData(gData, false)
                    }, this);
                }
            }
        }
    },
    
    createComboStore:function(objarr) {
        var itemsa = objarr;
        if(itemsa) {
            for(var cnt=0;cnt<itemsa.length;cnt++) {
                if (itemsa[cnt].regex){
                    itemsa[cnt].regex=eval(itemsa[cnt].regex);
                }
                if (itemsa[cnt].validator){
                    
                    itemsa[cnt].validator=eval('(' + Wtf.decode(itemsa[cnt].validator) + ')');
                }
                var currXType = itemsa[cnt].xtype;
                itemsa[cnt].id += this.id;
                itemsa[cnt].appenID = this.id;
                if(itemsa[cnt].childFieldId) {
                     for(var cnt1=0;cnt1<itemsa[cnt].childFieldId.length;cnt1++) {
                         itemsa[cnt].childFieldId[cnt1] += this.id;
                     }
                }
                if(itemsa[cnt].childComboId) {
                     for(var cnt1=0;cnt1<itemsa[cnt].childComboId.length;cnt1++) {
                         itemsa[cnt].childComboId[cnt1] += this.id;
                     }
                }
                if(itemsa[cnt].parentComboId) {
                    itemsa[cnt].parentComboId += this.id;
                }
                if(itemsa[cnt].items!=null) {
                    if(currXType == "fieldset")
                        itemsa[cnt]['cls'] = "fieldsetborder";
                    this.createComboStore(itemsa[cnt].items);
                } else {
                    if(currXType == "fieldset")
                        itemsa[cnt]['cls'] = "fieldsetborder";
                    else if(currXType == 'textfield' && itemsa[cnt].inputType && itemsa[cnt].inputType=='file') {
                           this.fileupload = true;
                    } else if(currXType == 'datefield') {
                           itemsa[cnt]['format'] = WtfGlobal.getDateFormat();
                    }
                    else if(currXType == 'combo' || currXType == 'select') {
                        var obj = new Wtf.data.Store({
                            reader: new Wtf.data.KwlJsonReader({root:'data'}, ["id","name"]),
                            autoLoad : false,
                            url : Wtf.req.mbuild+"form.do",
                            baseParams : {
                                action : 21,
                                moduleid : this.moduleid,
                                name : itemsa[cnt].name
                            }
                        });
                        var assignedL = itemsa[cnt]['listeners'];
                        itemsa[cnt]['listeners'] = {
                           scope : this,
                           'select' : function(cmb, rec, idx) {

                               if(cmb.childComboId) {//For cascading
                                   for(var cnt1=0;cnt1<cmb.childComboId.length;cnt1++) {
                                       var childObjId = cmb.childComboId[cnt1];
                                       var nameValue = cmb.name;
                                        Wtf.getCmp(childObjId).clearValue();
                                        Wtf.getCmp(childObjId).store.removeAll();
                                        if(Wtf.getCmp(childObjId).parentComboId === cmb.id) {
                                            Wtf.getCmp(childObjId).store.load({
                                                params: {
                                                    'cascadeCombo': 1,
                                                    'cascadeComboName': Wtf.getCmp(childObjId).casColName,
                                                    'cascadeComboRefName': nameValue,
                                                    'cascadeComboValue': cmb.getValue()
                                                }
                                            });
                                            Wtf.getCmp(childObjId).enable();
                                        } else {
                                            Wtf.getCmp(childObjId).disable();
                                        }
                                   }
                               }

                               if(cmb.childFieldId) {//For read only fields
                                    Wtf.Ajax.requestEx({
                                        url: Wtf.req.mbuild+'form.do',
                                        method:'POST',
                                        params: {
                                            action: 33,
                                            moduleid: cmb.moduleid,
                                            appenid:cmb.childFieldId,
                                            refTName: cmb.name,
                                            comboValueId: cmb.getValue()
                                        }},
                                        this,
                                        function(obj) {
//                                            var obj = eval('(' + resp + ')');
                                            for(cnt1=0;cnt1<cmb.childFieldId.length;cnt1++) {
                                               childObjId = cmb.childFieldId[cnt1];
                                               Wtf.getCmp(childObjId).setValue(obj[childObjId]);
                                            }
                                        }
                                    );
                               }
                                if (assignedL !=undefined){
                                    if(assignedL["select"] !== undefined){
                                        assignedL["select"].call(this, cmb, rec,idx);
                                    }
                                }
                        }
                        }
                        if(itemsa[cnt].parentComboId) {
                            itemsa[cnt]['mode'] = 'local';
                        }else {
                            itemsa[cnt]['mode'] = 'remote';
                        }
                        itemsa[cnt]['store'] = obj;
                        itemsa[cnt]['displayField'] = 'name';
                        itemsa[cnt]['valueField'] = 'id';
                        itemsa[cnt]['triggerAction'] = 'all';
                        if(this.isFilter==true) {
                            itemsa[cnt]['mode'] = 'local';
//                            itemsa[cnt].store.on("load") {
//                                
//                            }
                        } 
                    }
                }
            }
        }
    },
    
    onFormBtnClick :function(btnConf) {
        var submitConfig = this.itemArr.submitConfig;
        if(submitConfig.triggerTypeCombo && submitConfig.triggerTypeCombo == "2"){

            var Obj = eval(submitConfig.triggerCustomJSCode);
           
        }else{
        if(submitConfig.triggerTypeCombo && submitConfig.triggerTypeCombo == "1"){
            var recIndex = Wtf.processStore.find("processid",submitConfig.triggerProcess);
            if(recIndex != -1){
                    var rec = Wtf.processStore.getAt(recIndex);
                var processurl = Wtf.req.process+rec.get("processname")+".do";
            }
        }
        if(this.panelForm.isValid()) {
            var gJson = "";
            for(var c in this.gridArray) {
                var gridObj = Wtf.getCmp(this.gridArray[c]);
                if(gridObj !== undefined){
                    var gridJson = this.getJsonFromGrid(gridObj);
                    gJson += "\"" + gridObj.reportId + "\":[" + gridJson + "],";
                }
            }
            if(gJson != "")
                gJson = "[{" + gJson.substring(0, (gJson.length - 1)) + "}]";
            var pObj = {};
            pObj.moduleid = this.moduleid;
            pObj.taskid = this.taskid;
            if(gJson != "")
                pObj.jsondata = gJson;
            if(this.editFlag) {
                Wtf.MessageBox.confirm('Edit', 'Are you sure you want to edit record', function(btn){
                    if(btn=="yes"){
                        var formUrl = Wtf.req.mbuild+"form.do?action=23&";
                        if(this.fileupload){
                            formUrl = formUrl + "fileAdd=true&";
                        }
                        this.panelForm.url = formUrl;
                        this.panelForm.baseParams = {
                            id : this.itemid
                        };
                        this.submitform(pObj);
                    }
                },this);
            }else {
                var formUrl = Wtf.req.mbuild+"form.do?action=22&";
                if(this.fileupload){
                    formUrl = formUrl + "fileAdd=true&";
                }
                this.panelForm.url = formUrl;
                this.submitform(pObj);
            }
        }
        }
    },
    
    submitform:function(pObj){
        var items=this.panelForm.items.items;
        for(var i=0;i<items.length;i++){
            if ((items[i].xtype=='textfield' || items[i].xtype=='textarea') && items[i].inputType != "file"){
                items[i].setValue(ScriptStripper(HTMLStripper(items[i].getValue())));
            }
        }
        this.panelForm.submit({
            scope: this,
            params:pObj,
            success: function(result,action){
                var resultObj = eval('('+action.response.responseText+')');
                if(resultObj.success) {
                    if(!this.editFlag)
                        this.resetFormPanel();
                    for(var c in this.gridArray){
                        var gridObj = Wtf.getCmp(this.gridArray[c]);
                        if(gridObj !== undefined){
                            gridObj.getStore().removeAll();
                        }
                    }
                    msgBoxShow(6,2);
                    this.fireEvent("onsuccess",resultObj);
                } else {
                    msgBoxShow(['Error',resultObj.msg],1);
                }

//                var successConfig = this.itemArr.successConfig;
//                if(successConfig.triggerTypeCombo && successConfig.triggerTypeCombo == "3"){
//                    var Obj = eval(successConfig.triggerCustomJSCode);
//                }else{
//                    if(successConfig.triggerTypeCombo && successConfig.triggerTypeCombo == "2"){
//                        openModuleTab(successConfig.triggerModule);
//                    }
//                }
//                if(successConfig.triggerTypeCombo && successConfig.triggerTypeCombo == "1"){
//                    var resultObj = eval('('+action.response.responseText+')');
//                    if(resultObj.success) {
//                        if(!this.editFlag)
//                            this.resetFormPanel();
//                        for(var c in this.gridArray){
//                            var gridObj = Wtf.getCmp(this.gridArray[c]);
//                            if(gridObj !== undefined){
//                                gridObj.getStore().removeAll();
//                            }
//                        }
//                        msgBoxShow(6,2);
//                        this.fireEvent("onsuccess",resultObj);
//                    } else {
//                        msgBoxShow(['Error',resultObj.msg],1);
//                    }
//                }
            },
                failure: function(frm, action){
                    msgBoxShow(5,1);
                }
            });
    },
    
    resetFormPanel : function() {
        this.panelForm.reset();
    },
    
    getJsonFromGrid: function(gridObj){
        var ds = gridObj.getStore();
        var r = "";
        for(var a = 0; a < ds.getCount(); a++){
             var record = ds.getAt(a);
//             r += this.createJsonData(gridObj,record)+",";
            r += Wtf.encode(ds.getAt(a).data) + ",";
        }
        return r.substring(0, (r.length - 1));
    }
});


Wtf.form.formContainerPanel = function(config){
    Wtf.apply(this, config);
    Wtf.form.formContainerPanel.superclass.constructor.call(this, config);
    this.addEvents({
        "onsuccess": true,
        "rowselect":true
    });
}

Wtf.extend(Wtf.form.formContainerPanel, Wtf.Panel, {
    region: 'north',
    baseCls: "tempClass123",
    autoScroll : true,
    gridArray: {},
    autoHeight :true,
    bodyStyle : 'background-color:#FFFFFF;margin-bottom:2px;margin-left:10px;',
    initComponent: function(conf){
        Wtf.form.formContainerPanel.superclass.initComponent.call(this, conf);
        this.headercontent = document.createElement('div');
        this.headercontent.id = "headerCon"+this.id;
        this.label = document.createElement('span');
        this.headercontent.className = 'headercontent12';
        this.headercontent.style.width = '100%';
        this.headercontent.appendChild(this.label);
    
    },
    
    afterRender: function(conf){
        Wtf.form.formContainerPanel.superclass.afterRender.call(this, conf);
        
        this.bwrap.insertFirst(this.headercontent);
        this.headercontent.style.marginLeft = "5px";
        this.isHide = true;
        
        this.createHtmlContent(this.btnType);
    },
    
    createHtmlContent: function(btnTypeArray){
        var htmlContent = "";
        for(var cnt=0;cnt<btnTypeArray.length;cnt++) {
            var type = btnTypeArray[cnt];
            var btn = new Wtf.Button({
                handler : this.onButtonClick,
                scope : this,
                text : this.setBtnText(type),
                renderTo : this.headercontent.id,
                type : type
            });
        }
    },
    
    setBtnText : function(type) {
        var value = "";
        switch (type) {
            case 'm':
                value = "New Module";break;
            case 'n':
                value = "New";break;
            case 'e':
                value = "Edit";break;
            case 'em':
                value = "Edit Module";break;
            case 'r':
                value = (this.gridFlag?"New Grid":"New Report");break;
        }
        return value;
    },
    
    createFormPanel : function(urlP,itemArr) {
        itemArr = this.checkGridItem(itemArr);
        this.formPanel = new Wtf.form.FormPanel({
                labelAlign:"left",
                layout:'fit',
                //autoScroll : true,
                border : false,
                fileUpload: this.fileupload,
                cls : 'quickCreateForm',
                id :'quickCreateForm'+this.id,
                labelWidth:150,
                buttonAlign:"left",
                url : urlP,
                items : itemArr,
                buttons : [{
                    text: 'Save',
                    scope: this,
                    handler: this.onFormBtnClick
                },{
                    text: 'Cancel',
                    scope: this,
                    handler: this.onFormBtnClick
                }]
        });

        this.formPanel.on("expand", function(){
            if(this.formPanel.getSize().height > 250)
                this.formPanel.setHeight(250)
            Wtf.getCmp(this.pid).ownerCt.doLayout();
        }, this);
        
        this.formPanel.on("collapse", function(){
            Wtf.getCmp(this.pid).ownerCt.doLayout();
        }, this);
        
        return this.formPanel;
    },
    checkGridItem: function(item){
        for(var cnt = 0; cnt < item.length; cnt++){
            if(item[cnt].xtype == "WtfGridPanel" || item[cnt].xtype == "WtfEditorGridPanel"){
                item[cnt].id = "QuickCreate_" + item[cnt].id;
                this.gridArray[item[cnt].name] = item[cnt].id;
                item[cnt].xtype = "WtfEditorGridPanel";
                item[cnt].insertMode = true;
//                item[cnt].height = 120;
            }
            if(item[cnt].items !== undefined){
                this.checkGridItem(item[cnt].items);
            }
        }
        return item;
    },

    addOnButtonClick: function(butConf) {
        var QuickCreate = null;
        if(!Wtf.getCmp('quickCreateForm'+this.id)) {
            var urlpath ="";var itemArr = null;
            switch (butConf.type) {
                case 'm':
                    var selStore = new Wtf.data.SimpleStore({
                        fields: ['name','displayconfig'],
                        data: [['Window',1],
                        ['Tab',2]]
                    });
                    var displayConfCombo = new Wtf.form.ComboBox({
                        fieldLabel:"Display Configuration",
                        editable: false,
                        store: selStore,
                        mode: 'local',
                        triggerAction: 'all',
                        value: 1,
                        displayField: 'name',
                        hiddenName : 'displayconfig',
                        valueField: 'displayconfig',
                        width: 125
                    });
                    urlpath = Wtf.req.mbuild+"form.do?action=5&";
                    this.fileupload = true;
                    itemArr = [{
                        layout:"form",
                        border : false,
                        labelWidth:150,
                        columnWidth:0.5,
                        //height: 100,
                        fileUpload: true,
                        //autoScroll: true,
                        //cls: "quickInsFrmPnl",
                        defaults : {anchor : '40%',xtype:"textfield"},
                        items:[{
                            allowBlank:false,
                            fieldLabel:"Module Name",
                            name:"name",
                            regex:nameRegex,
                            regexText : regexText,
                            maxLength:20
                         },{
                            fieldLabel:"Upload Icon",
                            inputType: 'file',
                            name:"label"
                         },displayConfCombo,
                         new Wtf.form.Checkbox({
                             fieldLabel:"Is Abstract?", name:"abstractInd"
                          })]
                      }]
                    break;
                case 'em':
                    var selStore = new Wtf.data.SimpleStore({
                        fields: ['name','displayconf'],
                        data: [['Window',1],
                        ['Tab',2]]
                    });
                    var displayConfCombo = new Wtf.form.ComboBox({
                        fieldLabel:"Display Configuration",
                        editable: false,
                        store: selStore,
                        mode: 'local',
                        triggerAction: 'all',
                        value: 1,
                        displayField: 'name',
                        hiddenName : 'displayconf',
                        valueField: 'displayconf',
                        width: 125
                    });
                    urlpath = Wtf.req.mbuild+"form.do?action=36&";
                    this.fileupload = true;
                    itemArr = [{
                        layout:"form",
                        border : false,
                        labelWidth:150,
                        columnWidth:0.5,
                        height: 75,
                        fileUpload: true,
                        autoScroll: true,
                        cls: "quickInsFrmPnl",
                        defaults : {anchor : '40%',xtype:"textfield"},
                        items:[{
                            allowBlank:false,
                            fieldLabel:"Module Name",
                            name:"modulename",
                            regex:nameRegex,
                            regexText : regexText,
                            maxLength:20,
                            readOnly:true
                         },{
                            fieldLabel:"Upload Icon",
                            inputType: 'file',
                            name:"label"
                         },displayConfCombo,{
                            name:"id",
                            xtype:'hidden'
                         }]
                      }]
                    break;
                case 'r':
                    urlpath = Wtf.req.rbuild+"report.do?action=1&tableflag="+(this.gridFlag?1:0);
                    itemArr = [{
                            layout:"form",
                            border : false,
                            labelWidth:150,
                            columnWidth:0.5,
                            height: 50,
                            autoScroll: true,
                            cls: "quickInsFrmPnl",
                            defaults : {anchor : '40%',xtype:"textfield"},
                            items:[{
                                allowBlank:false,
                                    fieldLabel:(this.gridFlag?"Grid Name":"Report Name"),
                                name:"name",
                                regex:nameRegex,
                                regexText : regexText,
                                maxLength:20
                             }]
                      }]
                    break;
                case 'n':
                    urlpath = Wtf.req.mbuild+"form.do?action=22&";
                    itemArr = [this.itemObj];
                    break;
                case 'e':
                    itemArr = [this.itemObj];
                    urlpath = Wtf.req.mbuild+"form.do?action=23&";
                break;
            }
            if(itemArr) {
                this.add(this.createFormPanel(urlpath,itemArr));
                this.formPanel.expand();
                this.doLayout();
                Wtf.getCmp(this.pid).doLayout();
                Wtf.getCmp(this.pid).ownerCt.doLayout();
            }
         }
    },
    
    resetFormPanel : function() {
        this.formPanel.form.reset();
    },
    
    onButtonClick: function(butConf){
        if (Wtf.getCmp('quickCreateForm'+this.id)){
                this.collapseFormPanel();
         }
        if(this.isHide) {
            if(butConf.type =='e' && Wtf.getCmp(this.pid).modulegrid.getSelectionModel().getCount()==0) {
                msgBoxShow(["Message","Please select record to edit."],0);
                return;
            }
            var selectionModel;
            if(butConf.type == 'em'){
                selectionModel=Wtf.getCmp(this.gridId).getSelectionModel();
                if (!selectionModel.hasSelection()){
                    msgBoxShow(["Message","Please select module to edit."],0);
                    return;
                }
            }
            if (Wtf.getCmp('quickCreateForm'+this.id)){
                Wtf.getCmp('quickCreateForm'+this.id).destroy();
            }
            if(!Wtf.getCmp('quickCreateForm'+this.id))
                this.addOnButtonClick(butConf);
            else if(this.formPanel.collapsed==true){
                this.formPanel.expand();
                this.formPanel.addClass("quickCreateForm");
            }
            if(butConf.type == 'em'){
                this.formPanel.getForm().loadRecord(selectionModel.getSelected());
                this.formPanel.find('name','id')[0].setValue(selectionModel.getSelected().data.moduleid);
            }
            butConf.setText("  Close  ");
            this.isHide = false;
            this.currentBtnConf = butConf;
            if(butConf.type =='e') {
                this.selectedRec = Wtf.getCmp(this.pid).modulegrid.getSelectionModel().getSelected();
                this.fillFormFields();
            } else if(butConf.type =='n') {
                this.resetFormPanel();
                for(var c in this.gridArray){
                    var gObj = Wtf.getCmp(this.gridArray[c]);
                    if(gObj!== undefined)
                        gObj.getStore().removeAll();
                }
            }
        } else {
//            if(Wtf.getCmp('quickCreateForm'+this.id)) {
//                this.formPanel.collapse();
//                this.formPanel.removeClass("quickCreateForm");
//            }    
            if(this.currentBtnConf) {
                if(butConf.text.match("Close")=="Close") {
                    this.collapseFormPanel();
                } else {
                    this.currentBtnConf.setText(this.setBtnText(this.currentBtnConf.type));
    //                this.isHide = true;
                    if(this.currentBtnConf.type != butConf.type) {
                        if(butConf.type =='e') {
                            if(Wtf.getCmp(this.pid).modulegrid.getSelectionModel().getCount()==1) {
                                this.selectedRec = Wtf.getCmp(this.pid).modulegrid.getSelectionModel().getSelected();
                                this.fillFormFields();
                            } else {
                                msgBoxShow(["Message","Please select record to edit."],0);
                                return;
                            }
                        } else {
                            this.formPanel.form.reset();
                        }

    //                    window.setTimeout(onFormButtonClick,400,this,butConf);
    //                    return;
                    }
                    butConf.setText("  Close  ");
                    this.currentBtnConf = butConf;
                }
            }
            else {
                butConf.setText(this.setBtnText(butConf.type));
                this.currentBtnConf = null;
                this.isHide = true;
                if(Wtf.getCmp('quickCreateForm'+this.id)) {
                    this.formPanel.collapse();
                    this.formPanel.removeClass("quickCreateForm");
                }
            }
        }
        Wtf.getCmp(this.pid).doLayout();
    },
    
    onFormBtnClick: function(btnConf) {
        if(btnConf.text =='Save') {
            if(!this.localFlg) {
                this.fireEvent('SaveClick', btnConf, this);
            } else {
                if(!this.isHide) {
                    if(this.currentBtnConf.type =='e' && Wtf.getCmp(this.pid).modulegrid.getSelectionModel().getCount()==0) {
                        msgBoxShow(["Message","Please select record to edit."],0);
                        return;
                    }
                    if(this.formPanel.form.isValid()) {
                        var gJson = "";
                        for(var c in this.gridArray){
                            var gridObj = Wtf.getCmp(this.gridArray[c]);
                            if(gridObj !== undefined){
                                var gridJson = this.getJsonFromGrid(gridObj);
                                gJson += "\"" + gridObj.reportId + "\":[" + gridJson + "],";
                            }
                        }
                        if(gJson != "")
                            gJson = "[{" + gJson.substring(0, (gJson.length - 1)) + "}]";
                        var pObj = {};
                        pObj.moduleid = this.moduleid;
                        pObj.taskid = this.taskid;
                        if(gJson != "")
                            pObj.jsondata = gJson;
                        if(this.currentBtnConf.type == 'e') {
                            Wtf.MessageBox.confirm('Edit', 'Are you sure you want to edit record', function(btn){
                                if(btn=="yes"){
                                    var formUrl = Wtf.req.mbuild+"form.do?action=23&";
                                    if(this.fileupload){
                                        formUrl = formUrl + "fileAdd=treu&";
                                    }
                                    this.formPanel.form.url = formUrl;
                                    this.formPanel.form.baseParams = {
                                        id : this.selectedRec.data.id
                                    };
                                    this.submitform(pObj);
                                }
                            },this);
                        }else if(this.currentBtnConf.type == 'n') {
                            var formUrl = Wtf.req.mbuild+"form.do?action=22&";
                            if(this.fileupload){
                                formUrl = formUrl + "fileAdd=treu&";
                            }
                            this.formPanel.form.url = formUrl;
                        }
                        if(this.currentBtnConf.type != 'e') {
                            this.submitform(pObj);
                        }
                      }
                    }
                }
        } else if(btnConf.text =='Cancel') {
            this.collapseFormPanel();
        }
    },
    
    collapseFormPanel : function() {
        if(!this.isHide) {
            if(this.currentBtnConf) 
                this.currentBtnConf.setText(this.setBtnText(this.currentBtnConf.type));
            this.currentBtnConf = null;
            this.isHide = true;
            if(Wtf.getCmp('quickCreateForm'+this.id)) {
                this.formPanel.collapse();
                this.formPanel.removeClass("quickCreateForm");
            }
        }
    },
    
    submitform:function(pObj){
       // this.formPanel.form.items.items[0].setValue("My");
        if (this.currentBtnConf.type != 'm' && this.currentBtnConf.type != 'em'){
            var items=this.formPanel.form.items.items;
            for(var i=0;i<items.length;i++){
                if (items[i].xtype=='textfield' || items[i].xtype=='textarea'){
                    items[i].setValue(ScriptStripper(HTMLStripper(items[i].getValue())));
                }
            }
        }
        this.formPanel.form.submit({
            scope: this,
            params:pObj,
            success: function(result,action){
                var resultObj = eval('('+action.response.responseText+')');
                if(resultObj.success) {
                    if(this.currentBtnConf.type != 'e')
                        this.formPanel.form.reset();
                    //                                        for(var c = 0; c < this.gridArray.length; c++){
                    var data = Wtf.decode(resultObj.data);
                    var formid = data.formid;
                    var moduleid = data.moduleid;
                    var reportkey = data.reportkey;
                    var modulename = data.modulename;
                    for(var c in this.gridArray){
                        var gridObj = Wtf.getCmp(this.gridArray[c]);
                        if(gridObj !== undefined){
                            gridObj.getStore().removeAll();
                        }
                    }
                    //msgBoxShow(6,2);
                    this.fireEvent("onsuccess",resultObj);
                    var existingModule = new Wtf.moduleConfig({
                        formid:formid,
                        moduleid:moduleid,
                        reportkey:reportkey,
                        modulename:modulename
                    });
                    existingModule.show();
                } else {
                    msgBoxShow(['Error',resultObj.msg],1);
                //                                        this.fireEvent("onsuccess");
                }
                this.collapseFormPanel();
            },
            failure: function(frm, action){
                msgBoxShow(5,1);
            }
        });
    },
    getJsonFromGrid: function(gridObj){
        var ds = gridObj.getStore();
        var r = "";
        for(var a = 0; a < ds.getCount(); a++){
             var record = ds.getAt(a);
//             r += this.createJsonData(gridObj,record)+",";
            r += Wtf.encode(ds.getAt(a).data) + ",";
        }
        return r.substring(0, (r.length - 1));
    },

     createJsonData : function(gridObj, record) {
        var jsonData = "{";
        for(var cnt =0;cnt<gridObj.storeItems.length;cnt++) {
            var dataIndex = gridObj.storeItems[cnt].name;
            if(gridObj.storeItems[cnt].xtype == 'Date') {
                jsonData += dataIndex+":\""+record.data[dataIndex].format(Wtf.getDateFormat())+"\",";
            } else {
                jsonData += dataIndex+":\""+record.data[dataIndex]+"\",";
            }
        }
        jsonData = jsonData.substr(0,jsonData.length-1)+"}";
        return(jsonData);
    },

    parentComboLoad : function(store, record, opt) {
        var o = opt.params;
        var nameValue = Wtf.getCmp(o.comboID).name;
        var selectedID = this.selectedRec.data[nameValue];
        Wtf.getCmp(o.comboID).setValue(selectedID);
        for(var cnt=0; cnt < o.childComboID.length; cnt++) {
            var p = {};
            var casColName = Wtf.getCmp(o.childComboID[cnt]).casColName;
            p['comboID'] = o.childComboID[cnt];
            p['cascadeCombo'] = 1;
            p['cascadeComboName'] = casColName;
            p['cascadeComboRefName'] = nameValue,
//            p['cascadeComboName'] = o.selectedName;
            p['cascadeComboValue'] = selectedID;

            Wtf.getCmp(o.childComboID[cnt]).store.load({
                params: p
            });
            Wtf.getCmp(o.childComboID[cnt]).store.on('load', this.childComboLoad, this);
        }
        store.un('load', this.parentComboLoad, this);
    },

    childComboLoad : function(store, record, opt) {
        var o = opt.params;
        Wtf.getCmp(o.comboID).setValue(this.selectedRec.data[Wtf.getCmp(o.comboID).name]);
        store.un('load', this.childComboLoad, this);
    },

    comboLoad : function(store, record, opt) {
        var o = opt.params;
        Wtf.getCmp(o.comboID).setValue(this.selectedRec.data[Wtf.getCmp(o.comboID).name]);
        store.un('load', this.comboLoad, this);
    },
    
    fillFormFields:function() {
        var itemsa = this.formPanel.form.items.items;
        if(itemsa) {
            for(var cnt=0;cnt<itemsa.length;cnt++) {
                var xtype = itemsa[cnt].xtype;
                if (xtype && ['panel','viewport','form','window','tabpanel','toolbar','fieldset'].indexOf(xtype) == -1) {
                    if(itemsa[cnt].xtype == 'combo' || itemsa[cnt].xtype == 'select' ) {
                        var o = {};
                        if(itemsa[cnt].childComboId) {
                            o['comboID'] = itemsa[cnt].id;
                            o['selectedName'] = itemsa[cnt].name;
                            o['childComboID'] = itemsa[cnt].childComboId;

                            itemsa[cnt].store.on('load', this.parentComboLoad, this);
                            itemsa[cnt].store.load({
                                params: o
                            });

                        } else if(!itemsa[cnt].childComboId && !itemsa[cnt].parentComboId){
                            o['comboID'] = itemsa[cnt].id;
                            itemsa[cnt].store.on('load', this.comboLoad, this);
                            itemsa[cnt].store.load({
                                params: o
                            });
                        }
                        
                    } else if(itemsa[cnt].xtype == 'datefield') {
                        itemsa[cnt].setValue(Date.parseDate(this.selectedRec.data[itemsa[cnt].name],'Y-m-d'));
                    } else if(itemsa[cnt].xtype == 'radiogroup') {
//                            var container=itemsa[cnt].items.items;
                            var container=itemsa[cnt].items;
                            for(var i=0;i<container.length;i++){
                                if( this.selectedRec.data[itemsa[cnt].name] == container[i].inputValue ){
                                    Wtf.getCmp(container[i].id).setValue(true);
                                    break;
                                }
                            }

                    }else if(itemsa[cnt].xtype == 'checkboxgroup') {
                            var checkboxstr=this.selectedRec.data[itemsa[cnt].name];
                            var checkboxArray = new Array();
                            checkboxArray=checkboxstr.split(',');

//                            var container=itemsa[cnt].items.items;
                            var container=itemsa[cnt].items;
                            for(var i=0;i<container.length;i++){
                                if( checkboxArray.inArray(container[i].inputValue) ){
                                    Wtf.getCmp(container[i].id).setValue(true);
                                }
                            }
                    }else if(itemsa[cnt].xtype == 'radio') {
                        if (this.selectedRec.data[itemsa[cnt].name] == 'true'){
                            itemsa[cnt].setValue(true);
                        }
                    } else{
                        if(!itemsa[cnt].inputType && itemsa[cnt].inputTypy != "file"){
                            itemsa[cnt].setValue(this.selectedRec.data[itemsa[cnt].name]);
                        }
                    }
                }
            }
        }

        for(var c in this.gridArray) {
            var gridObj = Wtf.getCmp(this.gridArray[c]);
            if(gridObj !== undefined){
                if(gridObj.gridRendered){
                    var gData = eval("(" + this.selectedRec.data[c] + ")");
                    var oStore = gridObj.getStore();
                    oStore.loadData(gData, false)
                } else {
                    gridObj.on("gridrendered", function(gridObject){
                        var gData = eval("(" + this.selectedRec.data[c] + ")");
                        var oStore = gridObject.getStore();
                        oStore.loadData(gData, false)
                    }, this);
                }
                //gridObj.getStore().data = this.selectedRec.data['item_grid'];
//                gridObj.getView().refresh();
            }
        }
    },
    onGridRowSelect : function(rec) {
        if(!this.isHide && this.currentBtnConf.type =='e') {
            this.selectedRec = rec;
            this.fillFormFields();
        }
    },
    
    onGridRowDeSelect : function() {
        if(!this.isHide && this.currentBtnConf.type =='e') {
            this.formPanel.form.reset();
            for(var c in this.gridArray){
                var gridObj = Wtf.getCmp(this.gridArray[c]);
                if(gridObj !== undefined){
                    gridObj.getStore().removeAll();
                }
            }
        }
    }
});

function onFormButtonClick(scopeObj,conf) {
    Wtf.getCmp(scopeObj.id).onButtonClick(conf);
} 
