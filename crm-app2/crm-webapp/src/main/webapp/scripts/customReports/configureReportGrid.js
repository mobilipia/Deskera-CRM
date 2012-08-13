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
Wtf.override(Wtf.grid.CheckColumn,{
    onMouseDown : function(e, t){
        if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1 && (this.dataIndex == "qsearch" || this.dataIndex == "groupflag")){
            e.stopEvent();
            var index = this.grid.getView().findRowIndex(t);
            var record = this.grid.store.getAt(index);
//             var xtype ="";
//             switch(record.data.columntype){
//                case "1":xtype = "textfield";break;
//                case "2":xtype = "numberfield";break;
//                case "3":xtype = "Datefield";break;
//                case "4":xtype = "Combo";break;
//                case "5":xtype = "Timefield";break;
//                case "6":xtype = "Checkbox";break;
//                case "7":xtype = "Multiselect Combo";break;
//                case "8":xtype = "Ref. Combo";break;
//            }
            var columntype = record.data.columntype;
            if(this.dataIndex == "qsearch") {
                if(columntype == "3" || columntype == "4" || columntype == "5" || columntype == "7" || columntype == "8"){//Date Field
                    msgBoxShow(['Error', "You can't set Date, Time and Combo field as Quick search column."], Wtf.MessageBox.Error);
                    return false;
                }else {
                    record.set(this.dataIndex, !record.data[this.dataIndex]);
                }
            } else if(this.dataIndex == "groupflag") {
                if(columntype == "4"){
                    if(!this.groupflag) {
                        record.set(this.dataIndex, !record.data[this.dataIndex]);
                        this.groupflag = true;
                    } else {
                        if(record.data[this.dataIndex]) {
                            record.set(this.dataIndex, !record.data[this.dataIndex]);
                            this.groupflag = false;
                        } else {
                            msgBoxShow(['Error', "You can set grouping on one Combo field only."], Wtf.MessageBox.Error);
                            return false;
                        }
                    }
                }else if (columntype == "7" || columntype == "8"){
                    msgBoxShow(['Error', "You can't set grouping on Multi-Select/ Reference Combo field."], Wtf.MessageBox.Error);
                    return false;
                } else {
                    msgBoxShow(['Error', "You can set grouping on Combo field only."], Wtf.MessageBox.Error);
                    return false;
                }
            }
        }
    }
});

Wtf.customReportBuilder.reportPanel = function(conf){
    Wtf.apply(this, conf);
    if(this.cls)
        this.cls += " reportPanelBaseCls";
    else
        this.cls = "reportPanelBaseCls";

    Wtf.customReportBuilder.reportPanel.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.customReportBuilder.reportPanel, Wtf.Panel, {
    closable: true,
    layout: 'border',
    bodyStyle:"background-color:white",
    iconCls:"pwnd formbuilder",
    tBarConf: null,
    bBarConf: null,
    initComponent: function(conf){
        Wtf.customReportBuilder.reportPanel.superclass.initComponent.call(this, conf);
    },
    onRender: function(conf){
        Wtf.customReportBuilder.reportPanel.superclass.onRender.call(this, conf);
        this.createColumnGrid();
        this.createReportGrid();
        this.getAdvanceSearchComponent()

       this.reportpan= new Wtf.Panel({
            layout:'border',
            region:'center',
            iconCls:getTabIconCls(Wtf.etype.lead),
            border:false,
            id:this.id+'leadpan',
            items:[
            this.objsearchComponent,
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[this.reportGrid]
            }
            ]
        });

        this.add(this.reportpan);

        this.objsearchComponent.on("customReportFilterStore",this.filterStore, this);
        this.objsearchComponent.on("customReportClearStoreFilter",this.clearStoreFilter, this);
    },

    createReportGrid : function() {
        this.reportSM = this.createSelectionModel();

        var quickSearchCol = new Wtf.grid.CheckColumn({
           header: WtfGlobal.getLocaleText("crm.customreport.header.quicksearch"),//"Quick Search",
           dataIndex: 'qsearch',
           width: 80
        });

        var groupByCol = new Wtf.grid.CheckColumn({
           header:WtfGlobal.getLocaleText("crm.customreport.header.group"),// "Group",
           dataIndex: 'groupflag',
           width: 80
        });

        var rendererStore = new Wtf.data.SimpleStore({
            fields:['id','name'],
            data: [["Currency","Currency"],["Date","Date"],["Email","Email"]/*,["Website","Website"]*/]
        });
        var summaryTypeStore = new Wtf.data.SimpleStore({
            fields :['id', 'name'],
            data:[['None','None'],['sum','sum'],['count','count'],['max','max'],['min','min'],['avg','average']]
        });

        this.rendererCombo = new Wtf.form.ComboBox({
            store:rendererStore,
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            mode: 'local',
            forceSelection: true,
            triggerAction: 'all',
            selectOnFocus: true
        });

        this.summaryCombo = new Wtf.form.ComboBox({
            store:summaryTypeStore,
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            mode: 'local',
            forceSelection: true,
            triggerAction: 'all',
            selectOnFocus: true
        });

        this.summaryCombo.on("beforeselect",this.isAllow,this);
        this.reportCM = new Wtf.grid.ColumnModel([
            this.reportSM, {
                header: WtfGlobal.getLocaleText("crm.customreport.header.column"),//"Column",
                dataIndex: "columnname",
                 width: 220,
                 renderer: function(value) {
                    var valArray = value.split(".");
                    return valArray[1]+" ["+valArray[0]+"]";
                }
            },{
                header: WtfGlobal.getLocaleText("crm.customreport.header.displayname"),//"Display Name",
                dataIndex: "displayfield",
                editor: new Wtf.ux.TextField({
                    validateOnBlur: true,
                    allowBlank:false
                }),
                width: 230
            },{
                header: WtfGlobal.getLocaleText("crm.customreport.header.columntype"),//"Column Type",
                dataIndex: "columntype",
                width: 100,
                renderer: function(val) {
                    if(val == "1") {
                        return "Text";
                    }else if(val == "2") {
                        return "Number";
                    }else if(val == "3") {
                        return "Date Field";
                    }else if(val == "4" || val == "8") {
                        return "Combo Field";
                    }else if(val == "7") {
                        return "Multiselect Combo";
                    }else if(val == "5") {
                        return "Time Field";
                    }else if(val == "6") {
                        return "Checkbox";
                    }else if(val == "9") {
                        return "Auto No";
                    }else{
                        return val;
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("crm.customreport.header.fieldtype"),//"Field Type",
                dataIndex: "renderer",
                renderer : Wtf.ux.comboBoxRenderer(this.rendererCombo),
                editor: this.rendererCombo,
                width: 100
            },{
                header: WtfGlobal.getLocaleText("crm.customreport.header.summarytype"),//"Summary Type",
                dataIndex: "summaryType",
                renderer : Wtf.ux.comboBoxRenderer(this.summaryCombo),
                editor: this.summaryCombo,
                width: 100

            },quickSearchCol,groupByCol,
            {
                header: WtfGlobal.getLocaleText("crm.customreport.header.actions"),//"Actions",
                dataIndex: 'id',
                width:Wtf.isWebKit?75:70,
                renderer:function(value, css, record, row, column, store){
                    var actions = "<image src='images/up.png' title='Move Up' onclick=\"changeseq('"+record.get('seq')+"',0, 'customReportConfigGrid')\"/>"+
                    "<image src='images/down.png' style='padding-left:5px' title='Move Down' onclick=\"changeseq('"+record.get('seq')+"',1, 'customReportConfigGrid')\"/>";
                    actions +="<img class='delete' src='images/Cancel.gif' style='padding-left:5px' title='Delete Field'></img>";
                    return actions;
                }
            }
        ]);
        this.reportRec = Wtf.data.Record.create([
            {name: "headerid"},{name: "columnname"},{name: "displayfield"},{name: "columntype"},{name: "reftable"},{name: "renderer"},
            {name: "qsearch"},{name: "groupflag"},{name: 'seq'},{name: 'id'},{name:'summaryType'},{name:'dataindex'},{name:'reftablename'},{name:'xtype'},{name:'seq'}]);

        this.reportStore = new Wtf.data.Store({
            url: Wtf.req.rbuild+"report.do",
            baseParams: {
                action : 5,
                reportid : this.reportgridid
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, this.reportRec)
        });
        this.AdvanceSearchBtn = new Wtf.Toolbar.Button({
            text : WtfGlobal.getLocaleText("crm.customreport.toptoolbar.appfilterBTN"),//"Apply Filter",
            scope : this,
            tooltip: WtfGlobal.getLocaleText("crm.customreport.toptoolbar.appfilterBTN.ttip"),//'Apply filter for multiple terms in multiple fields.',
            handler : this.configurAdvancedSearch,
            iconCls : 'pwnd searchtabpane'
        });
        this.reportGrid = new Wtf.grid.EditorGridPanel({
            cm: this.reportCM,
            ddGroup :'GridDDGroup',
            region: "center",
            id : 'customReportConfigGrid',
            store: this.reportStore,
            clicksToEdit :1,
            sm: this.reportSM,
            view:new Wtf.ux.KWLGridView({
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer("Drag & Drop fields here.")
            }),
            plugins:[quickSearchCol,groupByCol],
            tbar: [{
                text : WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
                iconCls : 'pwnd saveBtn',
                scope : this,
                handler : this.saveGridConfig
            },'-',this.AdvanceSearchBtn],
            layout:'fit'
        });
        this.reportStore.load();
        this.reportGrid.on("render",function(){
        	this.reportGrid.view.refresh();
            var gridDropTargetEl = this.reportGrid.getView().el.dom.childNodes[0].childNodes[1]
            var destGridDropTarget = new Wtf.dd.DropTarget(gridDropTargetEl, {
                    ddGroup: 'ColGridDDGroup',
                    notifyDrop : function(ddSource, e, data){
                            // Generic function to add records.
                            function addRow(record, index, allItems) {
                                    var colname = record.data.modulename+"."+record.data.name;
                                    var reftablename = record.data.tablename;
//                                    var dataindex = reftablename+"."+record.data.dbcolumnname;
                                    var dataindex = record.data.dataindex;
                                    // Search for duplicates
                                    var foundItem = this.reportGrid.store.find('dataindex', dataindex);

                                    if (foundItem  == -1) {
                                            var recPosition = this.reportGrid.store.getCount();
                                            var rec = new this.reportRec({
                                                headerid : record.data.headerid,
                                                columnname : colname,
                                                displayfield : record.data.displayname,
                                                dataindex : dataindex,
                                                reftablename:reftablename,
                                                renderer : '',
                                                qsearch: false,
                                                groupflag: false,
                                                summaryType:'',
                                                columntype: record.data.type,
                                                seq:recPosition
                                            });
                                            this.reportGrid.store.insert(recPosition,rec);
                                    }
                            }
                            // Loop through the selections
                            Wtf.each(ddSource.dragData.selections ,addRow,this);
                            return(true);
                    }.createDelegate(this)
            },this);
        },this);
        this.reportGrid.on("cellclick", this.deleteRow, this);
        this.reportGrid.on("afteredit", this.afterEdit, this);
    },
    afterEdit:function(e){
        var rec = e.record;
        if (e.field=='displayfield'){
           var val = WtfGlobal.HTMLStripper(rec.data.displayfield)
           if(val.trim()==""){
            WtfComMsgBox(["Alert","Please enter a valid display name"], 0)
           }
           rec.set("displayfield",val);
        }
    },
    validateedit:function(e){
        if (e.field=='displayfield'){
           return validateedit(e);
        }
    },

    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponentForCustomReport({
            cm:this.ccm,
            call_from_custom_report:true
        } );
    },

    configurAdvancedSearch:function(reconfigurestore,moduleComboFlag){
        if(!(moduleComboFlag == true) && this.reportGrid.getStore().getCount() == 0) {            
            WtfComMsgBox(["Alert","No column has been selected."], 0)
            return;
        }
        this.objsearchComponent.show();
        this.objsearchComponent.advSearch = true;
      //  this.objsearchComponent.cm = this.spreadSheet.colArr;
        this.objsearchComponent.getComboData(this.columnGrid,reconfigurestore);
        this.AdvanceSearchBtn.disable();
        this.doLayout();
        if(moduleComboFlag==true){
            this.hideAdvancedSearch();  // Hide Advanced Search Component
            this.objsearchComponent.cancelSearch(); // Clear Advanced Search fields
            this.searchJson="";
        }
    },
    filterStore:function(json){
        this.searchJson=json;
        if(this.searchJson.length >0){
            WtfComMsgBox(["Success","Filter has been saved successfully."], 0)
            this.hideAdvancedSearch();
        }

    },
    clearStoreFilter:function(){
        this.searchJson="";
        this.hideAdvancedSearch();
    },
    hideAdvancedSearch :function(){
          this.AdvanceSearchBtn.enable();
          this.objsearchComponent.hide();
          this.doLayout();
    },

    isAllow:function(combo,record,index){
         var a = this.reportGrid.getSelectionModel().getSelected().data.columntype;
         var xtype ="";
         switch(a){
            case "1":
                xtype = "textfield";
                break;
            case "2":
                xtype = "numberfield";
                break;
            case "3":
                xtype = "Datefield";
                break;
            case "4":
                xtype = "Combo";
                break;
            case "5":
                xtype = "Timefield";
                break;
            case "6":
                xtype = "Checkbox";
                break;
            case "7":
                xtype = "Multiselect Combo";
                break;
            case "8":
                xtype = "Ref. Combo";
            case "9":
                xtype = "Auto No";
                break;

        }
         return isAllowed(combo,record,index,xtype);
     },

    deleteRow: function(gObj, ri, ci, e){
        var event = e;
        var rec = this.reportStore.data.items[ri];
        if(event.target.className == 'delete'){
            this.reportStore.remove(rec);
        }
    },

    createColumnGrid: function(){
        this.columnSM = this.createSelectionModel();
        var module = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.customreport.toptoolbar.moduleselect"));//'Select Module');
        var modStore = new Wtf.data.SimpleStore({
        fields:['id','name'],
        data: [["Product","Product"],["Account","Account"],["Lead","Lead"], ["Opportunity","Opportunity"],["Contact","Contact"],["Case","Case"]/*,["Campaign","Campaign"]*/],
            autoLoad: true
        });

        this.moduleCombo = new Wtf.form.ComboBox({
            store:modStore,
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            typeAhead: true,
            mode: 'local',
            forceSelection: true,
            triggerAction: 'all',
            selectOnFocus: true,
            value:"Product"
        });

        this.ccm = new Wtf.grid.ColumnModel([
            this.columnSM, {
                header: WtfGlobal.getLocaleText("crm.customreport.header.columnname"),//"Column Name",
                dataIndex: "displayname",
                dbname:"",
                sortable: true,
                groupRenderer: WtfGlobal.nameRenderer
            },{
                header:WtfGlobal.getLocaleText("crm.customreport.header.type"),// "Type",
                dataIndex: "type",
                dbname:"",
                renderer: function(val) {
                    if(val == "1") {
                        return "Text";
                    }else if(val == "2") {
                        return "Number";
                    }else if(val == "3") {
                        return "Date Field";
                    } else if(val == "4" || val == "8") {
                        return "Combo Field";
                    } else if(val == "7") {
                        return "Multiselect Combo";
                    }else if(val == "5") {
                        return "Time Field";
                    }else if(val == "6") {
                        return "Checkbox";
                    }else if(val == "9") {
                        return "Auto No";
                    }else{
                        return val;
                    }
                }
            }, {
                header: WtfGlobal.getLocaleText("crm.customreport.header.modulename"),//"Module Name",
                dbname:"",
                dataIndex: "modulename"
            }
        ]);
        var columnRec = Wtf.data.Record.create([{
            name: "name",
            mapping: "column"
        },{
            name: "type",
            mapping: "type"
        },{
            name: "displayname",
            mapping: "displayname"
        },{
            name: "modulename",
            mapping: "modulename"
        },{
            name : "dbcolumnname"
        },{
            name : "dataindex"
        },{
            name : "headerid"
        },{
            name : "iscustomcolumn"
        },{
            name : "pojoname"
        },{
            name : "defaultname",
            mapping : "defaultname"
        },{
            name : "tablename"
        },{
            name : "configid"
        }]);
        this.columnStore = new Wtf.data.GroupingStore({
            url:Wtf.req.customReport+'rbuild/getModuleColumns.do',
            groupField:"modulename",
            sortInfo: {
                field: 'displayname',
                direction: "ASC"
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, columnRec)
        });
        this.groupingView = new Wtf.grid.GroupingView({
            forceFit: true,
            showGroupName: false,
            enableGroupingMenu: true,
            groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Columns" : "Column"]})',
            hideGroupedColumn: true
        });
        this.columnGrid = new Wtf.grid.GridPanel({
            cm: this.ccm,
            store: this.columnStore,
            autoScroll:true,
            tbar:[module,this.moduleCombo],
//            border: false,
            sm: this.columnSM,
            region:"west",
            width:"25%",
            ddGroup: 'ColGridDDGroup',
            enableDragDrop : true,
            layout: 'fit',
            view: this.groupingView,
            viewConfig: {
                forceFit: true
            }
        });
        this.add(this.columnGrid);

        this.columnGrid.on("render",function(){
            this.moduleSelect("Product");
        },this);

        this.moduleCombo.on("select", function(a,rec,index){
            var modulename = rec.data["name"];
            this.moduleSelect(modulename,true);
            this.reportStore.removeAll();
        }, this);
        this.moduleCombo.on("change", function(){
            this.reportStore.removeAll();
        }, this);
    },
    moduleSelect: function(modulename,reconfigurestore){
        this.columnStore.load({
            params : {
                modulename : modulename
            }
        });
        this.columnGrid.getView().refresh();
        if(reconfigurestore){
            this.columnStore.on("load",function(){
                this.configurAdvancedSearch(reconfigurestore,true);
            },this);

        }

    },
    createSelectionModel: function(flag){
        if(!flag)
            flag = false;
        var temp = new Wtf.grid.CheckboxSelectionModel({singleSelect : flag});
        return temp;
    },

    getJsonFromRecord : function(record, cnt) {
        var jsonData = "{";
        var dataObj = record.data
        for (var dataIndex in dataObj) {
            if(dataIndex =='columnname')
                jsonData += 'name'+":\""+record.data[dataIndex]+"\",";
            else
                jsonData += dataIndex+":\""+record.data[dataIndex]+"\",";
        }
        jsonData = jsonData.substr(0,jsonData.length-1)+"}";
        return(jsonData);
    },

    saveGridConfig : function() {
        var store = this.reportGrid.getStore();
        var recCount = this.reportGrid.getStore().getCount();
        if(recCount == 0) {
            WtfComMsgBox(["Alert","No column has been selected."], 0)
            return;
        }else{
            for(var cnt = 0; cnt < recCount; cnt++) {
                var record = store.getAt(cnt);
                if(record.data.displayfield.trim()==""){
                    WtfComMsgBox(["Alert","Please enter a valid display name"], 0)
                    return;
                }
            }
        }
        
         var p = Wtf.getCmp("savereportWin");
        if(!p){
            new Wtf.saveReportWin({
                reportGrid : this.reportGrid,
                reportid : this.reportgridid,
                createtable : this.gridFlag,
                rfilterjson : this.searchJson,
                rcategory:this.moduleCombo.getValue()
            }).show();
        }
    }
});

Wtf.saveReportWin = function(config){
    Wtf.apply(this,{
        title:WtfGlobal.getLocaleText("crm.customreport.savereportwin.title"),//'Save Report',
        id:'savereportWin',
        closable: true,
        modal: true,
        iconCls: "pwnd favwinIcon",
        width: 540,
        height:290,
        resizable: false,
        layout: 'border',
        buttonAlign: 'right',
        renderTo: document.body,
        buttons: [{
            text: WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
            scope: this,
            handler:this.saveForm.createDelegate(this)
        }, {
            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
            scope: this,
            handler: function(){
                this.close();
            }
        }]
    },config);
    Wtf.saveReportWin.superclass.constructor.call(this, config);
}

Wtf.extend( Wtf.saveReportWin, Wtf.Window, {
    onRender: function(config){
        Wtf.saveReportWin.superclass.onRender.call(this, config);
        this.createForm();
        this.add({
            region: 'north',
            height: 75,
            border: false,
            bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtmlReqField(WtfGlobal.getLocaleText("crm.customreport.msg.title.saverepo"),WtfGlobal.getLocaleText("crm.customreport.savereportwin.tophtmldtl"),'../../images/Save.gif',WtfGlobal.getLocaleText("crm.customreport.savereportwin.tophtml.warnmsg"))//'HTML code and "\\\" character are not allowed')
        },{
            region: 'center',
            border: false,
            bodyStyle: 'background:#f1f1f1;font-size:10px;',
            autoScroll:true,
            items:this.reportConfigInfo
        });
    },
    createForm:function(){

        this.reportConfigInfo= new Wtf.form.FormPanel({
            baseParams:{
                mode:12
            },
            url:Wtf.req.customReport+'rbuild/saveReportConfig.do',
            region:'center',
            bodyStyle: "background: transparent;",
            border:false,
            style: "background: transparent;padding:20px;",
            defaultType:'striptextfield',
            labelWidth:165,
            items:[ {
                name:'reportno',
                xtype:'hidden',
                value : 0
            },{
                fieldLabel:WtfGlobal.getLocaleText("crm.customreport.savereportwin.reportname"),//'Report Name* ',
                name:'rname',
                id:'rname',// used this id
                width:220,
                maxLength:50,
                allowBlank:false
            },/*{
                fieldLabel:'Report Unique Name* ',
                name:'runiquename',
                width:220,
                maxLength:50,
                allowBlank:false
            },*/{
                fieldLabel:WtfGlobal.getLocaleText("crm.customreport.savereportwin.reportdesc"),//'Report Description ',
                name: 'rdescription',
                id:'rdescription',// used this id
                width:220,
                maxLength:250,
                xtype:'striptextarea'
            }
        ]
         });
    },
    getRecord:function(){
        Wtf.Ajax.requestEx({
            url:"Common/ProfileHandler/getAllUserDetails.do",
            params:{
                mode:11,
                lid:loginid
            }
        },this,this.genSuccessResponse,this.genFailureResponse);
    },
    saveForm:function(){
        var rname = WtfGlobal.HTMLStripper(Wtf.getCmp("rname").getValue());
        var rdesc = Wtf.getCmp("rdescription").getValue();
        rname=WtfGlobal.replaceAll(rname, "\\\\", "");
        rdesc=WtfGlobal.replaceAll(rdesc, "\\\\", "");
        if(rname.trim()==""){
            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.customreport.msg.title.saverepo"),WtfGlobal.getLocaleText("crm.customreport.msg.validreponamemsg"));//" Please enter a valid report name.");
            Wtf.getCmp("rname").setValue("");
            return;
        }
        if(!this.reportConfigInfo.getForm().isValid()){
            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.customreport.msg.title.saverepo"),WtfGlobal.getLocaleText("crm.customreport.validation.msg"));//" Enter required fields.");
        } else{
            var jsonData = "[";
            var reportStore = this.reportGrid.store;
            var recCnt = reportStore.getCount();
            for(var cnt = 0; cnt < recCnt; cnt++) {
                var record = reportStore.getAt(cnt);
                jsonData += this.getJsonFromRecord(record,cnt) + ",";
            }
            jsonData = jsonData.substr(0, jsonData.length - 1) + "]";

            this.reportConfigInfo.getForm().submit({
                params : {
                    reportcolumnsetting : jsonData,
                    rcategory: this.rcategory,
                    reportid : this.reportid,
                    createtable : this.createtable,
                    rfilterjson : this.rfilterjson
                },
                waitMsg:WtfGlobal.getLocaleText("crm.customreport.savereportwin.waitmsg"),//'Saving report configuration',
                success:function(f,a){
                    var res = eval('('+a.response.responseText+')');
                    this.genSaveSuccessResponse(res)
                },
                failure:function(f,a){
                    this.genSaveFailureResponse(eval('('+a.response.responseText+')'))
                },
                scope:this
            });
        }
    },

    genSuccessResponse:function(response){
        Wtf.getCmp('savereportWin').close();
    },
    genFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("crm.customreport.failurerespmsg");//"Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        Wtf.getCmp('savereportWin').close();
        ResponseAlert([WtfGlobal.getLocaleText("crm.customreport.msg.title.saverepo"),msg]);
    },
    genSaveSuccessResponse:function(response){
        ResponseAlert([WtfGlobal.getLocaleText("crm.customreport.msg.title.saverepo"),response.data.msg]);
        Wtf.refreshDashboardWidget(Wtf.moduleWidget.customReports);
        Wtf.getCmp('savereportWin').close();
    },
    genSaveFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("crm.customreport.failurerespmsg");//"Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        Wtf.getCmp('savereportWin').close();
        ResponseAlert([WtfGlobal.getLocaleText("crm.customreport.msg.title.saverepo"),msg]);
    },

    getJsonFromRecord : function(record, cnt) {
        var dataObj = record.data;
        var keyArray=[];
        var valArray=[];
        for (var dataIndex in dataObj) {
            if(dataIndex =='columnname'){
                keyArray.push("name");
            }else{
                keyArray.push(dataIndex);
            }
            valArray.push(dataObj[dataIndex]);
        }
        keyArray.push("displayorder");
        valArray.push(cnt);
        
        var jsonData = WtfGlobal.JSONBuilder(keyArray, valArray);
        return(jsonData);
    }
});
