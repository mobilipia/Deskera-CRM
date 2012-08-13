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
Wtf.common.WtfGridConfigWin = function(conf){
    Wtf.apply(this, conf);
    this.buttons = [{
        text: 'OK',
        scope: this,
        handler: function() {
            var smVal = this.selectionCombo.getValue();
            var singleselect = this.singleCheck.getValue();
            var gridType = Wtf.getCmp("modulebuilder_gridpanel").getValue() ? "WtfGridPanel" : "WtfEditorGridPanel";
            var conf = this.createGrid(smVal, singleselect, gridType);
            this.fireEvent("okclicked", conf);
            this.close();
        }
    },{
        text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
        scope: this,
        handler: function() {
            this.close();
        }
    }];
    this.events = {
        "okclicked": true
    };
    Wtf.common.WtfGridConfigWin.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.common.WtfGridConfigWin, Wtf.Window, {
    onRender: function(conf){
        Wtf.common.WtfGridConfigWin.superclass.onRender.call(this, conf);
        var numbererCheck = new Wtf.form.Checkbox({
            boxLabel:"Row Numberer"
        });
        var selStore = new Wtf.data.SimpleStore({
            fields: ['name'],
            data: [['RowSelectionModel'],
            ['CellSelectionModel'],
            ['CheckboxSelectionModel']]
        });
        this.selectionCombo = new Wtf.form.ComboBox({
            fieldLabel:"Selection Model",
            editable: false,
            labelStyle: "font-size: 11px;",
            store: selStore,
            mode: 'local',
            triggerAction: 'all',
            value: "RowSelectionModel",
            displayField: 'name',
            width: 155
        });
        this.selectionCombo.on('select', function(cmb) {
            var val = cmb.getValue();
            if(val == 'CellSelectionModel') {
                this.singleCheck.ownerCt.hide();
            } else {
                this.singleCheck.ownerCt.show();
            }
        }, this);
        this.singleCheck = new Wtf.form.Checkbox({
            boxLabel:"Single Select"
        });
        var configCombo = this.getConfigCombo();
        this.gridRec = Wtf.data.Record.create([{
            name: 'header',
            type: 'string'
        },{
            name: 'xtype',
            type: 'string'
        },{
            name: 'reftable',
            type: 'string'
        },{
            name: 'renderer',
            type: 'string'
        },{
            name:'summaryType'
        }]);
        this.gridds = new Wtf.data.Store({
            reader: this.gridRec
        });
        var xtypeds = new Wtf.data.SimpleStore({
            fields: ['name'],
            data: [['None'],
            ['Text'],
            ['Number(Integer)'],
            ['Number(Float)'],
            ['Date'],
            ['Checkbox'],
            ['Combobox']]
        });
        var xtypeCombo = new Wtf.form.ComboBox({
            store: xtypeds,
            displayField: 'name',
            valueField: 'name',
            typeAhead: true,
            mode: 'local',
            emptyText: "Click to select",
            editable: true,
            triggerAction: 'all'
        });
        xtypeCombo.on("blur",function(comboBox){
            comboBox.store.clearFilter(true);
        },this)

    this.rendererRecord = Wtf.data.Record.create([{
        name: 'id'
    },{
        name: 'name'
    },{
        name: 'value'
    },{
        name: 'isstatic'
    }
    ]);
    this.rendererJsonReader = new Wtf.data.KwlJsonReader({
        root: "data"
    }, this.rendererRecord);
    this.rendererStore = new Wtf.data.Store({
        reader: this.rendererJsonReader,
        url: Wtf.req.rbuild+'report.do',//mode=0 is user
        method : 'GET',
        baseParams: {
            action: 14
        }
    });
    this.rendererStore.load();
    this.rendererCombo = new Wtf.form.ComboBox({
            emptyText: "< Choose renderer >",
            bodyStyle: 'margin: auto; padding-top: 5px;',
            store:this.rendererStore,
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            typeAhead: true,
            mode: 'local',
            forceSelection: true,
            editable: false,
            triggerAction: 'all',
            selectOnFocus: true
    });
    this.rendererCombo.on("select", this.rendererChange, this);

    this.summaryCombo = new Wtf.form.ComboBox({
            emptyText: "< Choose summaryType >",
            bodyStyle: 'margin: auto; padding-top: 5px;',
            store:summaryTypeStore,
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            typeAhead: true,
            mode: 'local',
            forceSelection: true,
            triggerAction: 'all',
            selectOnFocus: true
    });
    this.summaryCombo.on("beforeselect",this.isAllowed,this);
    
        var gridcm = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(), {
                header: 'Header',
                dataIndex: 'header',
                editor: new Wtf.form.TextField({
                    allowBlank: false
                })
            },{
                header: 'Xtype',
                dataIndex: 'xtype',
                editor: xtypeCombo,
                renderer : function(value) {
                    var idx = xtypeCombo.store.find(xtypeCombo.valueField, value);
                    if(idx == -1)
                        return "";
                    var rec = xtypeCombo.store.getAt(idx);
                    return rec.get(xtypeCombo.displayField);
                }

            },{
                header: 'Ref Table',
                dataIndex: 'reftable',
                editor: new Wtf.form.TextField({
                    allowBlank: false
                })
            },{
                header: "renderer",
                dataIndex: "renderer",
                renderer : this.comboBoxRenderer(this.rendererCombo),
                editor: this.rendererCombo
            },{
                header: "Summary Type",
                dataIndex: "summaryType",
                renderer : Wtf.ux.comboBoxRenderer(this.summaryCombo),
                editor: this.summaryCombo
            },{
                header: 'Editor',
                dataIndex: 'editor',
                editor: new Wtf.form.TextField({
                    allowBlank: false
                }),
                hidden: true
            }]);
        this.reportGrid = new Wtf.grid.EditorGridPanel({
            cm: gridcm,
            ds: this.gridds,
            viewConfig: {
                forceFit:true
            },
            clicksToEdit: 1,
            sm: new Wtf.grid.RowSelectionModel
        });
        this.reportGrid.on("cellclick", this.deleteRow, this);
        var gridType = new Wtf.Panel({
            height: 25,
            border: false,
            defaultType: 'radio',
            cls: "gridRadio",
            items:[{
                boxLabel: "Grid-Panel",
                checked: true,
                id: "modulebuilder_gridpanel",
                name: 'gridType'
            },{
                width: 100,
                boxLabel: "Editor-Grid-Panel",
                id: "modulebuilder_editorgridpanel",
                name: 'gridType'
            }]
        });
        this.labelField = new Wtf.form.TextField({
            fieldLabel: "Grid Label",
            labelStyle: "font-size: 11px;",
            allowBlank: false
        });
        var configPanel = new Wtf.Panel({
            layout: 'fit',
            border: false,
            height: 105,
            items: [{
                xtype:"form",
                border: false,
                bodyStyle: 'padding: 15px',
                items:[{
                    layout: 'form',
                    autoHeight: true,
                    border: false,
                    items: [ this.labelField ]
                },{
                    layout:"column",
                    border:false,
                    items:[{
                        columnWidth:0.75,
                        layout:"form",
                        border:false,
                        items:[this.selectionCombo,configCombo]
                    },{
                        columnWidth:0.25,
                        border:false,
                        items:[this.singleCheck,numbererCheck]
                    }]
                }]
            }]
        });
        var colGrid = new Wtf.Panel({
            layout: 'fit',
            height: 188,
            border: false,
            items: [this.reportGrid]
        });
        Wtf.getCmp("modulebuilder_editorgridpanel").on("change", function(obj, nVal, oVal){
            if(nVal)
                gridcm.setHidden(5, false);
            else
                gridcm.setHidden(5, true);
        });
        Wtf.getCmp("modulebuilder_gridpanel").on("change", function(obj, nVal, oVal){
            if(nVal)
                gridcm.setHidden(5, true);
            else
                gridcm.setHidden(5, false);
        });
        this.add(gridType);
        this.add(configPanel);
        this.add(colGrid);
    },

    isAllowed:function(combo,record,index){
        return isAllowed(combo,record,index,this.reportGrid.getSelectionModel().getSelected().data.xtype);
    },

    comboBoxRenderer : function(combo) {
        return function(value) {
            var idx = combo.store.find(combo.valueField, value);
            if(idx == -1)
                return "";
            var rec = combo.store.getAt(idx);
            if (rec.get(combo.displayField) == 'None'){
                return rec.get(combo.displayField);
            }else{
                return "<a href='#' class='rendererClass'>"+rec.get(combo.displayField)+"</a>";
            }
        };
    },

    rendererChange: function(cObj, rec, index){
        if(rec.data.id == '0' && rec.data.name != 'None'){
           this.newRendererWin('new');
        }
    },

    newRendererWin: function(type){

        var editButtondisabled=false;
        var rendererId="";
        if (type == 'new'){
            this.rendererValue="";
            this.rendererNameValue="";

        }else{
            var idx = this.rendererCombo.store.find(this.rendererCombo.valueField, this.reportGrid.getSelectionModel().getSelected().data.renderer);
            if(idx == -1){
                this.rendererValue= "";
            }else{
                var rec = this.rendererCombo.store.getAt(idx);
                this.rendererValue= rec.get("value");
                this.rendererNameValue=rec.get(this.rendererCombo.displayField);
                editButtondisabled=rec.get("isstatic");
                rendererId=this.reportGrid.getSelectionModel().getSelected().get("renderer");
            }
        }

        this.rendererObj=new Wtf.moduleRenderer({
            rendererNameValue:this.rendererNameValue,
            rendererValue:this.rendererValue,
            type:type,
            rendererId :rendererId,
            editButtondisabled:editButtondisabled
        });
        this.rendererObj.show();
        this.rendererObj.on('updateRendererStore',this.updateRendererStore,this);
    },

    updateRendererStore:function(type,rendererId,name,value){
        var rendererRecord = new this.rendererRecord({
            id: rendererId,
            name: name,
            value:value,
            isstatic:'false'
        });

        if (type == 'new'){
            this.rendererStore.add(rendererRecord);
        }else{
            this.rendererStore.insert(this.rendererStore.find('id',rendererId),rendererRecord);
            this.reportGrid.getSelectionModel().getSelected().set("renderer",'0');
        }
        this.reportGrid.getSelectionModel().getSelected().set("renderer",rendererId);
    },

    deleteRow: function(gObj, ri, ci, e){
        var event = e;
        if(event.target.className == 'rendererClass') {
            this.newRendererWin('edit');
        }
    },


    createGrid: function(smVal, singleselect, gridType, add){
        var tempsm;
        switch(smVal) {
            case 'RowSelectionModel':
                tempsm = {
                    xtype: "rowselectionmodel",
                    singleSelect: singleselect
                }
                break;
            case 'CellSelectionModel':
                tempsm = {
                    xtype: "cellselectionmodel",
                    singleSelect: singleselect
                }
                break;
            case 'CheckboxSelectionModel':
                tempsm = {
                    xtype: "checkboxselectionmodel",
                    singleSelect: singleselect
                }
                break;
        }
        var cols = {};
        var recFieldArr = {};
        var count = this.gridds.getCount();
        for(var i = 0; i < count; i++) {
            var rec = this.gridds.getAt(i).data;
            cols[i] = {
                header: rec.header,
                dataIndex: rec.header
            };
            if(gridType == "WtfEditorGridPanel")
                cols[i].editor = rec.editor;
            recFieldArr[i] = {
                name:rec.header,
                type: rec.xtype
            };
        }
        var config = {
            xtype: gridType,
            columnArray: cols,
            sm: tempsm,
            recordArray: recFieldArr,
            viewConfig: {
                forceFit: true
            },
            moduleGrid: true,
            reportId: this.reportId,
            reportConf: this.reportConf
        };
        return config;
    },
    getConfigCombo: function(){
//        var configRec = Wtf.data.Record.create([{
//            name: "id"
//        },{
//            name: "name"
//        }])
//        var selStore = new Wtf.data.Store({
//            url: Wtf.req.mbuild+"form.do",
//            baseParams: {
//                action: 25
//            },
//            reader: new Wtf.data.KwlJsonReader({
//                root: "data",
//                id: 'task-reader'
//            }, configRec)
//        });
//        selStore.on("load", function(){
//            var temp = new configRec({
//                id: 0,
//                name: "Create New"
//            })
//            selStore.add(temp);
//        });
//        selStore.on("loadexception", function(){
//            var temp = new configRec({
//                id: 0,
//                name: "Create New"
//            })
//            selStore.insert(0, temp);
//        });
//        selStore.load();
        var selStore = new Wtf.data.SimpleStore({
            fields: ['id', 'name'],
            data :[
                [0, 'Create New']
            ]
        });
        var storeConfig = new Wtf.form.ComboBox({
            fieldLabel:"Store Configuration",
            editable: false,
            store: selStore,
            mode: 'local',
            labelStyle: "font-size: 11px;",
            triggerAction: 'all',
            displayField: 'name',
            valueField: "id",
            width: 155
        });
        storeConfig.on('select', function(cmb) {
            var val = cmb.getValue();
            if(!gridConfig) {
                if(val == 0){
                    gridConfig = true;
                    this.configureStore();
                }
            } else {
                msgBoxShow(["Grid Configuration", "Can not configure datastore as another datastore configuration still pending."]);
            }
        }, this);
        return storeConfig;
    },
    configureStore: function(){
        var gridLabel = this.labelField.getValue();
        if(gridLabel == ""){
            gridConfig = false;
            msgBoxShow(["Grid Configuration", "Please set the label for the grid before configuring it."]);
            return;
        } else {
//            Wtf.getCmp("gridConfigWindow").hide();
            this.hide();
            Wtf.Ajax.request({
                url: Wtf.req.rbuild+'report.do',
                method: 'POST',
                params: {
                    action: 1,
                    name: gridLabel,
                    tableflag: 1
                },
                scope: this,
                success: function(resp) {
                    var title = this.labelField.getValue() + "-Configuration";
                    var obj = eval('(' + resp.responseText.trim() + ')');
                    if(obj.success == true || obj.success == "true") {
                        var tobj = Wtf.getCmp(obj.reportid + "_" + title);
                        if(tobj === undefined) {
                            obj = eval("(" + obj.data[0] + ")");
                            this.reportId = obj.reportid;
                            tobj = new Wtf.reportBuilder.reportPanel({
                                reportgridid: obj.reportid,
                                modBuildObj: this.modBuildObj,
                                reportkey : obj.reportkey,
                                gridFlag: true,
                                tablename: "",
                                id: obj.reportid + "_" + title,
                                title: title,
                                storeConfig: true,
                                closable: false,
                                border: false
                            });
                            mainPanel.add(tobj);
                        }
                        mainPanel.setActiveTab(tobj);
                    }
                },
                failure: function() {
                    msgBoxShow(4,0);
                }
            });
        }
    },
    setGridConf: function(gridConf, rId){
        this.reportConf = gridConf;
        var confObj = eval('(' + gridConf + ')');
        for(var c = 0; c < confObj.length; c++){
            confObj[c].displayfield = confObj[c].displayfield == "" ? confObj[c].name.split(".")[1] : confObj[c].displayfield;
            var temp = new this.gridRec({
                header: confObj[c].displayfield,// == "" ? confObj[c].name.split(".")[1] : confObj[c].displayfield,
                reftable: confObj[c].reftable,
                renderer: confObj[c].renderer,
                xtype: confObj[c].xtype,
                summaryType:confObj[c].summaryType
            });
            this.gridds.insert(c, temp);
            this.reportId = rId;
        }
    }
});

Wtf.common.WtfGridPanel = function(conf){
    Wtf.apply(this, conf);
    if(this.insertMode)
        this.tbar = [];
    Wtf.common.WtfGridPanel.superclass.constructor.call(this, conf);
    var selectionM = "rowselectionmodel";
    if(this.selModel instanceof Wtf.grid.RowSelectionModel){
        selectionM = "rowselectionmodel";
    } else if(this.selModel instanceof Wtf.grid.CheckboxSelectionModel){
        selectionM = "checkboxselectionmodel";
    } else if(this.selModel instanceof Wtf.grid.CellSelectionModel){
        selectionM = "cellselectionmodel";
    } else {
        selectionM = this.selModel.xtype;
    }
    switch(selectionM){
        case "rowselectionmodel":
            this.selModel = new Wtf.grid.RowSelectionModel({
                singleSelect: this.selModel.singleSelect
            });
        break;
        case "checkboxselectionmodel":
            this.selModel = new Wtf.grid.CheckboxSelectionModel({
                singleSelect: this.selModel.singleSelect
            });
        break;
        case "cellselectionmodel":
            this.selModel = new Wtf.grid.CellSelectionModel({
                singleSelect: this.selModel.singleSelect
            });
        break;
    }
    this.colModel = new Wtf.grid.ColumnModel([]);
    this.store = new Wtf.data.Store({});
}

Wtf.extend(Wtf.common.WtfGridPanel, Wtf.grid.GridPanel, {
  onRender: function(conf){
        Wtf.common.WtfGridPanel.superclass.onRender.call(this, conf);
        if(!this.moduleGrid && (this.reportId !== undefined && this.reportId != "")){
            Wtf.Ajax.requestEx({
                url: Wtf.req.rbuild+"report.do",
                params: {
                    action: 4,
                    reportid: this.reportId
                }
            }, this, function(response){
                var tObj = response;//eval("(" + response.trim() + ")");
                this.tableName = tObj.tablename;
                if(tObj.columnheader !== undefined){
                    var obj = tObj.columnheader;
                    var ca = [];
                    var ra = [];
                    for(var i = 0; i < obj.length; i++){
                        var temp = {};
                        temp.hidden = obj[i]["3"];
                        temp.header = obj[i]["0"];
                        temp.dataIndex = obj[i]["1"];
                        var rtemp = {};
                        rtemp.name = obj[i]["1"];
                        rtemp.type = obj[i].conftype == "None" ? "Text" : obj[i].conftype;
                        ca.push(temp);
                        ra.push(rtemp);
                    }
                    this.gridConfig(ca, ra);
                } else {
                    this.gridConfig();
                }
            }, function(response){
                this.gridConfig();
            });
        } else {
            this.gridConfig();
        }
    },

    setTbar: function(buttonConf){
        var toolBar = this.getTopToolbar();
        this.setToolBarConf(toolBar, buttonConf);
    },
    setBbar: function(buttonConf){
        var toolBar = this.getBottomToolbar();
        this.setToolBarConf(toolBar, buttonConf);
    },
    setToolBarConf: function(toolBar, buttonConf){
        for(var tConf in buttonConf){
            var temp = buttonConf[tConf];
            temp.handler = eval(temp.handler);
            toolBar.addButton(temp);
        }
    },
    addBlankRow: function(){
        var br = {};
        for(var t = 0; t < this.recField.length; t++){
            br[this.recField[t].name] = "";
        }
        this.store.add(new this.record(br));
    },
    gridConfig: function(ca, ra){
        if(ca === undefined){
            if(!this.colModel !== undefined){
                ca = [];
                for(var c in this.columnArray)
                    ca.push(this.columnArray[c]);
            }
        }
        if(ra === undefined){
            if(this.recordArray !== undefined){
                ra = [];
                for(var c in this.recordArray)
                    ra.push(this.recordArray[c]);
            }
        }
        this.colModel.setConfig(ca);
        this.recField = ra;
        this.record = Wtf.data.Record.create(ra);
        this.reader = new Wtf.data.KwlJsonReader({
            root: 'data'
        },this.record);
        this.store = new Wtf.data.Store({
            reader: this.reader
        });
        this.reconfigure(this.store, this.colModel);
        if(this.insertMode){
            this.setHeight(120);
            this.addBlankRow();
            this.getTopToolbar().add({
                text: "Add",
                scope: this,
                handler: this.addBlankRow
            });
        }
    }
});

Wtf.reg('WtfGridPanel', Wtf.common.WtfGridPanel);


Wtf.common.WtfEditorGridPanel = function(conf){
    Wtf.apply(this, conf);
    if(this.insertMode)
        this.tbar = [];
    this.colModel = new Wtf.grid.ColumnModel([]);
    this.store = new Wtf.data.Store({});
    this.summary=new Wtf.ux.grid.GridSummary();
    conf['plugins']= [this.summary];
    Wtf.common.WtfEditorGridPanel.superclass.constructor.call(this, conf);
    var selectionM = "rowselectionmodel";
    if(this.selModel instanceof Wtf.grid.RowSelectionModel){
        selectionM = "rowselectionmodel";
    } else if(this.selModel instanceof Wtf.grid.CheckboxSelectionModel){
        selectionM = "checkboxselectionmodel";
    } else if(this.selModel instanceof Wtf.grid.CellSelectionModel){
        selectionM = "cellselectionmodel";
    } else {
        selectionM = this.selModel.xtype;
    }
    switch(selectionM){
        case "rowselectionmodel":
            this.selModel = new Wtf.grid.RowSelectionModel({
                singleSelect: this.selModel.singleSelect
            });
        break;
        case "checkboxselectionmodel":
            this.selModel = new Wtf.grid.CheckboxSelectionModel({
                singleSelect: this.selModel.singleSelect
            });
        break;
        case "cellselectionmodel":
            this.selModel = new Wtf.grid.CellSelectionModel({
                singleSelect: this.selModel.singleSelect
            });
        break;
    }
}

Wtf.extend(Wtf.common.WtfEditorGridPanel, Wtf.grid.EditorGridPanel, {
    clicksToEdit: 1,
    gridRendered: false,
    initComponent: function(conf){
        Wtf.common.WtfEditorGridPanel.superclass.initComponent.call(this, conf);
        this.addEvents({
            "gridrendered": true
        });
    },

    onRender: function(conf){
        Wtf.common.WtfEditorGridPanel.superclass.onRender.call(this, conf);
        if(this.reportId !== undefined && this.reportId != ""){
            Wtf.Ajax.requestEx({
                url: Wtf.req.rbuild+"report.do",
                params: {
                    action: 4,
                    reportid: this.reportId
                }
            }, this, function(response){
//                this.insertMode = true;
                var tObj = response;//eval("(" + response.trim() + ")");
                if(tObj.columnheader !== undefined){
                    var obj = tObj.columnheader;
                    this.storeItems = obj;
                    this.tableName = tObj.tablename;
                    var ca = [];
                    var ra = [];
                    this.gridPlugins = [];
                    for(var cnt = 0; cnt < obj.length; cnt++){
                        var temp = {};
                        temp.hidden = obj[cnt]["3"];
                        temp.header = obj[cnt]["0"];
                        temp.dataIndex = obj[cnt]["1"];

                        if (obj[cnt][6] != undefined ){
                            var strfunc=obj[cnt][6];
                            if (strfunc.indexOf("function") < 0) {
                                temp.renderer = this[strfunc].createDelegate(this);
                            }else{
                                strfunc=strfunc.replace(/\n/g,"").replace(/&#43;/g,"+");
                                temp.renderer =eval('('+strfunc+ ')') ;
                            }
                        }
                        if (obj[cnt][7] != undefined && obj[cnt][7] != '' ){
                            temp.summaryType= obj[cnt][7];
                        }

//                        temp.editor = obj[i].editor === undefined ? new Wtf.form.TextField({}) : obj[i].editor;
                        if(obj[cnt]["2"] !='None') {
                            var editor = obj[cnt]["2"];
                            switch(editor){
                                case 'Text' :
                                    temp.editor = new Wtf.form.TextField({
                                        validateOnBlur: false,
                                        validationDelay: 1000
                                    });
                                    break;

                                case 'Number(Integer)' :
                                    temp.editor = new Wtf.form.NumberField({
                                        allowDecimals : false
                                    });
                                    break;

                                case 'Number(Float)' :
                                    temp.editor = new Wtf.form.NumberField({
                                    });
                                    break;

                                case 'Date' :
                                    temp.editor = new Wtf.form.DateField({
                                        format: Wtf.getDateFormat()//'D j-m-Y'
                                    });
                                    temp.renderer = this.formatDate;
                                    break;

                                case 'Checkbox' :
                                    temp = new Wtf.grid.CheckColumn(temp);
                                    this.gridPlugins.push(temp);
                                    break;

                                case 'Combobox' :
                                    var columnname = obj[cnt]["1"];

                                    var ComboRecord = Wtf.data.Record.create(this.createComboRecord(columnname,obj[cnt]["4"]));
                                    var ComboStore = new Wtf.data.Store({
                                        reader: new Wtf.data.KwlJsonReader({
                                            root:"data"
                                        }, ComboRecord),
                                        url: Wtf.req.rbuild+'report.do',
                                        baseParams : {
                                            action : 8,
                                            columnname : (obj[cnt]["5"] != '-1'?this.fTableCol.split(",")[0]:this.fTableCol),
                                            reftable : obj[cnt]["4"],
                                            combogridconfig : obj[cnt]["5"]
                                        }
                                    });
                                    ComboStore.load();
                                    var comboObj = new Wtf.form.ComboBox({
                                        store: ComboStore,
                                        allowBlank: false,
                                        typeAhead: true,
                                        displayField : columnname,
                                        valueField: 'id' ,
                                        mode: 'local',
                                        forceSelection: true,
                                        editable: false,
                                        triggerAction: 'all',
                                        selectOnFocus: true
                                    });
                                    temp.editor = comboObj;
                                    temp.renderer = Wtf.ux.comboBoxRenderer(comboObj);
                                    if(this.fTableCol.split(",").length>1) {
                                        comboObj.on("select",function(combo,record,index){
                                            var columns = combo.store.baseParams.columnname.split(",");
                                            var selRec  = this.getSelectionModel().getSelected();
                                            for(var cnt=0; cnt < columns.length; cnt++){
                                                selRec.set(columns[cnt],record.data[columns[cnt]]);
                                            }
                                        },this);
                                    }
                                    break;
                            }
                        }

                        var rtemp = {};
                        rtemp.name = obj[cnt]["1"];
//                        rtemp.type = obj[i].xtype == "None" ? "text" : obj[i].xtype;
                        ca.push(temp);
                        ra.push(rtemp);
                    }
                    this.gridConfig(ca, ra);
                } else {
                    this.gridConfig();
                }
                this.gridRendered = true;
                this.fireEvent("gridrendered", this);
            }, function(response){
                this.gridConfig();
            });
        } else {
            this.gridConfig();
        }
    },

    renderItalic: function(data, metadata, record, rowIndex, columnIndex, store){
            return '<i>' + data + '</i>';
    },

    createComboRecord : function(comboField,reftable) {
        var recObj = [];
        var fObj = {};
        fObj['name'] = "id";
        recObj.push(fObj);
        fObj = {};
        fObj['name'] = comboField;
        recObj.push(fObj);
        this.fTableCol = comboField + ",";
        for(var fieldcnt = 0; fieldcnt < this.storeItems.length; fieldcnt++) {
            var filedSplit = this.storeItems[fieldcnt][1].split(_reportHardcodeStr);
            if(filedSplit[0] == reftable && this.storeItems[fieldcnt][1]!=comboField) {
                var fObj = {};
                fObj['name'] = this.storeItems[fieldcnt][1];
                recObj.push(fObj);
                this.fTableCol += this.storeItems[fieldcnt][1]+",";
            }
        }
        this.fTableCol = this.fTableCol.substr(0,this.fTableCol.length-1);
        return recObj;
    },
    
    addBlankRow: function(){
        var br = {};
        for(var t = 0; t < this.recField.length; t++){
            br[this.recField[t].name] = "";
        }
        this.store.add(new this.record(br));
    },
    gridConfig: function(ca, ra){
        if(ca === undefined){
            if(!this.colModel !== undefined){
                ca = [];
                for(var c in this.columnArray){
                    if(this.columnArray[c].editor === undefined){
                        this.columnArray[c].editor = new Wtf.form.TextField({});
                    }
                    ca.push(this.columnArray[c]);
                }
            }
        }
        if(ra === undefined){
            if(this.recordArray !== undefined){
                ra = [];
                for(var c in this.recordArray)
                    ra.push(this.recordArray[c]);
            }
        }
        this.colModel.setConfig(ca);
        this.recField = ra;
        this.record = Wtf.data.Record.create(ra);
        this.reader = new Wtf.data.KwlJsonReader({
            root: 'data'
        },this.record);
        this.store = new Wtf.data.Store({
            reader: this.reader
        });
        this.store.on({
            add: this.refreshSummary,
            remove: this.refreshSummary,
            clear: this.refreshSummary,
            update: this.refreshSummary,
            scope: this
        });

        this.reconfigure(this.store, this.colModel);
        if(this.insertMode){
            this.setHeight(120);
//            this.addBlankRow();
            this.getTopToolbar().add({
                text: "Add",
                scope: this,
                handler: this.addBlankRow
            });
        }
    },
    refreshSummary:function(){
        this.summary.refreshSummary();
    }
});

Wtf.reg('WtfEditorGridPanel', Wtf.common.WtfEditorGridPanel);
