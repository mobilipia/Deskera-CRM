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
Wtf.reportBuilder.reportHome = function(conf){
    Wtf.apply(this, conf);
    Wtf.reportBuilder.reportHome.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.reportBuilder.reportHome, Wtf.Panel, {
    initComponent: function() {
        Wtf.reportBuilder.reportHome.superclass.initComponent.call(this);
        
        this.reportBuildBttn =new Wtf.Toolbar.Button({
            text : (this.gridFlag?"Grid Builder":"Report Builder"),
            iconCls:"pwnd formbuilder",
            scope : this,
            disabled: true,
            handler : function() {
                var title = this.modulesm.getSelected().data.reportname;
                var mid = this.modulesm.getSelected().data.reportid +"_" + title;
                var tobj = Wtf.getCmp(mid);
                if(tobj === undefined) {
                    tobj = new Wtf.reportBuilder.reportPanel({
                        id: mid,
                        title: title,
                        gridFlag: this.gridFlag,
                        reportgridid :this.modulesm.getSelected().data.reportid,
                        tablename : this.modulesm.getSelected().data.tablename,
                        reportkey : this.modulesm.getSelected().data.reportkey,
                        border: false
                    });
                    this.ownerCt.add(tobj);
                }
                this.ownerCt.setActiveTab(tobj);
            }
        });
        
        this.modulesm = new Wtf.grid.CheckboxSelectionModel({
                singleSelect:true
        });
        this.modulesm.on("selectionchange",this.disableBttns,this);
        this.ModCenter = Wtf.data.Record.create([
            {name: 'reportid'},
            {name: 'reportname'},
            {name: 'tablename'},
            {name: 'reportkey'},
            {name: 'createddate', type:'date', dateFormat: 'Y-m-j H:i:s.0'}]);

        this.dsModCenter = new Wtf.data.Store({
            baseParams: {
                action: 2,
                tableflag: (this.gridFlag?1:0)
            },
            url: Wtf.req.rbuild+'report.do',
            reader: new Wtf.data.KwlJsonReader({
                        root:"data",
                        totalProperty: 'TotalCount',
                        remoteGroup:true,
                        remoteSort: true
            }, this.ModCenter)
        });

        this.cmModCenter = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),this.modulesm,
            {
                header: "Name",
                dataIndex: 'reportname'
//                renderer: function(val) {
//                      return "<a href = '#' class='openModule' > " + val + "</a>";
//                }
            },{
                header: "Created Date",
                dataIndex: 'createddate',
                sortable: false,
                renderer: function(val) {
                    if(Wtf.isEmpty(val)) {
                        return val;
                    } else {
                        return val.format(Wtf.getDateFormat());
                    }
                }
            },{
                header: "Delete",
                dataIndex :'deleteflag',
                width: 65,
                renderer: function(val) {
                      return "<img id='DeleteImg' class='delete' src='images/Cancel.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete report'></img>";
                }
            }]);
        this.cmModCenter.defaultSortable = true;

        this.quickPanelSearch = new Wtf.KWLTagSearch({
            width: 150,
            emptyText: 'Enter name',
            field: "reportname"
        });
        this.pg = new Wtf.PagingSearchToolbar({
            pageSize: 15,
            searchField: this.quickPanelSearch,
            store: this.dsModCenter,
            displayInfo: true,
            displayMsg: 'Displaying items {0} - {1} of {2}',
            emptyMsg: "No items to display",
            plugins: this.pP = new Wtf.common.pPageSize({})
        });
        
        this.confBttn=new Wtf.Toolbar.Button({
            text : "Configure Group Tab",
            scope : this,
            iconCls:"pwnd groupconfig",
            disabled: true,
            hidden: (this.gridFlag?false:true),
            handler : function() {
                var moduleid = this.modulesm.getSelected().get("reportid");
                var configTab = new Wtf.ConfigGrTab({
                    moduleid : moduleid,
                    basemode : 1
                });
                configTab.show();
            }
        });
         this.addConfigBttn=new Wtf.Toolbar.Button({
            text : "Standard Config",
            scope : this,
            iconCls:'pwnd configuration',
            hidden: (this.gridFlag?false:true),
            disabled: true,
            handler : function() {
                var moduleid = this.modulesm.getSelected().get("reportid");
                addConfig(moduleid);
            }
        });
        this.grid = new Wtf.grid.GridPanel({
                border: false,
                ds: this.dsModCenter,
                sm: this.modulesm,
                cm: this.cmModCenter,
                autoScroll: true,
                layout: 'fit',
                height: 200,
                viewConfig: {
                    autoFill: true,
                    forceFit:true
                },
                loadMask: {
                    msg: 'Loading...'
                },
                bbar: this.pg,
                tbar : ['Quick Search: ', this.quickPanelSearch ,this.reportBuildBttn,this.confBttn,this.addConfigBttn]
            });
            
        this.formPanel = new Wtf.form.formContainerPanel({
                    pid : this.id,
                    localFlg : true,
                    btnType : ['r'],
                    gridFlag : this.gridFlag
        });
        this.formPanel.on("onsuccess",function(obj){
            obj = eval('('+obj.data[0]+')');
            var tobj = Wtf.getCmp(obj.reportid + "_" + obj.title);
            if(tobj === undefined) {
                tobj = new Wtf.reportBuilder.reportPanel({
                    reportgridid : obj.reportid,
                    reportkey : obj.reportkey,
                    gridFlag: (obj.tableflag==1?true:false),
                    tablename : "",
                    id: obj.reportid + "_" + obj.title,
                    title: obj.title,
                    border: false
                });
                this.ownerCt.add(tobj);
            }
            this.ownerCt.setActiveTab(tobj);
            this.loadStore();
        },this);
        
        this.modulescenter = new Wtf.Panel({
            layout : 'border',
            autoScroll : false,
            bodyStyle :"background-color:transparent;",
            border:false,
            items : [this.formPanel,{
                    region :'center',
                    layout :'fit',
                    autoScroll: false,
                    baseCls: "tempClass123",
                    border :false,
                    items: [this.grid]
            }]
        });
        this.add(this.modulescenter);
        this.dsModCenter.on('load', function(store) {
            this.quickPanelSearch.StorageChanged(store);
        }, this);
        this.dsModCenter.on('datachanged', function() {
            var p = this.pP.combo.value;
            this.quickPanelSearch.setPage(p);
        }, this);
        this.grid.on("render",function(){
           this.loadStore();
        },this)
        this.grid.on('cellclick', function(gd, ri, ci, e) {
            var event = e;
//            if(event.target.className == "openModule") {
//                var reportid = gd.getStore().getAt(ri).data.reportid;
//                var reportComp = Wtf.getCmp("Report"+reportid);
//                configFields={ismodule:false,
//                     data:'',
//                     cmpId:"Report"+reportid,
//                     moduleName:gd.getStore().getAt(ri).data.reportname,
//                     moduleId:reportid,
//                     containerId:mainPanel.id,
//                     isFilter:false,
//                     filterfield:'',
//                     filterValue:''
//                };
//                if(!reportComp) {
//                    Wtf.Ajax.requestEx({
//                            url: Wtf.req.rbuild+'report.do',
//                            method:'POST',
//                            params: {
//                                action: 20,
//                                reportid: gd.getStore().getAt(ri).data.reportid
//                            }
//                        },
//                        this,
//                        function(resp) {
//                            var resObj = eval('('+resp+')');
//                            configFields['permsObj'] = resObj.recordperm;
//                            openGridModule(configFields);
//                    });
//                } else {
//                    openGridModule(configFields);
//                }
//            } else
            if(event.target.className == "delete") {
                Wtf.MessageBox.confirm('Delete', 'Are you sure you want to delete selected report', function(btn){
                    if(btn=="yes"){
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.rbuild+'report.do',
                            method:'POST',
                            params: {
                                action: 6,
                                reportid: gd.getStore().getAt(ri).data.reportid
                            }
                        },
                        this,
                        function(resp) {
                            //                            var obj = eval('(' + resp.responseText + ')');
                            var resultObj = eval('('+resp+')');
                            if(resultObj.success) {
                                msgBoxShow(13,2);
                                this.loadStore();
                            }else{
                                    msgBoxShow(['Info', resultObj.msg], Wtf.MessageBox.Error);
                            }
                        },
                        function() {
                            //panel.enable();
                            }
                            );
                    }
                },this);
            }
        },this);
    },

    disableBttns: function(obj){
        if(obj.getCount() > 0){
            this.reportBuildBttn.enable();
            this.confBttn.enable();
            this.addConfigBttn.enable();
        } else {
            this.reportBuildBttn.disable();
            this.confBttn.disable();
            this.addConfigBttn.disable();
        }
    },
    
    loadStore :function() {
       this.dsModCenter.load({params:{
              start:0,
              limit:this.pP.combo.value
       }});
    }

});


Wtf.reportBuilder.reportPanel = function(conf){
    Wtf.apply(this, conf);
    this.previewDOM = document.createElement("div");
    this.previewDOM.className = "projectReportChartDiv";
    this.previewDOM.id = this.id + "_chartDiv";
    this.previewDiv = document.createElement("div");
    this.previewDiv.className = "reportChart";
    this.previewDiv.id = this.id + "_chartContainer";
    var closePreview = document.createElement("div");
    closePreview.id = this.id + "_closeChart";
    closePreview.innerHTML = "[ X ]";
    closePreview.className = "closeChartButton";
    closePreview.onclick = this.closePreviewClicked.createDelegate(this, []);
    this.previewDiv.appendChild(closePreview);
    this.previewDiv.appendChild(this.previewDOM);
    conf.contentEl = this.previewDiv;
    if(this.cls)
        this.cls += " reportPanelBaseCls";
    else
        this.cls = "reportPanelBaseCls";

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
    Wtf.reportBuilder.reportPanel.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.reportBuilder.reportPanel, Wtf.Panel, {
    closable: true,
    layout: 'border',
    iconCls:"pwnd formbuilder",
    tBarConf: null,
    bBarConf: null,
    initComponent: function(conf){
        Wtf.reportBuilder.reportPanel.superclass.initComponent.call(this, conf);
        this.createReportGrid();
        this.tablePanel = new Wtf.Panel({
            autoScroll: true,
            split: true,
            layout:'fit',
            border: false,
            maxHeight: 500,
            minHeight: 200,
            region: "center",
            title: "Tables"
        });
        this.tableColumnPanel = new Wtf.Panel({
            height: 400,
            autoScroll: true,
            split: true,
            border: false,
            maxHeight: 500,
            minHeight: 200,
            region: "south",
            title: "Table Columns"
        });
        this.reportColumnPanel = new Wtf.Panel({
            border: false,
            autoScroll: true,
            layout: 'fit',
            split: true,
            maxHeight: 500,
            minHeight: 200,
            region: "center",
            title: "Grid Columns",
            items : this.reportGrid,
            tbar: [{
                text: 'Generate query',
                scope: this,
                handler: this.getQuery
            },'-',{
                text: "Preview",
                scope: this,
                handler: this.showPreview
            },'-',{
                text: "Report layout",
                scope: this,
                handler: this.layoutBuilder
            },'-',{
                text : 'New Field',
                iconCls:'pwnd newfield',
                scope: this,
                hidden: (this.gridFlag?false:true),
                handler: function(){
                    this.fieldWin = new Wtf.newGridFieldWindow({
                        title:"Add Field",
                        closable:true,
                        border:false,
                        modal:true,
                        width : 340,
                        height: 250,
                        iconCls : 'win',
                        layout: "fit",
                        resizable: false
                    });
                    this.fieldWin.show();
                    this.fieldWin.on('success', function(name, fieldlabel, xtype, combogridconfig) {
                        var rec = new this.reportRec({
                            columnname: getColumnName(name,this.reportkey),
                            displayfield: fieldlabel,
                            defaultValue:'',
                            xtype: xtype,
                            reftable: '',
                            renderer: '',
                            hidden : false,
                            countflag : false,
                            combogridconfig : combogridconfig,
                            summaryType:''
                        });
                        this.reportGrid.store.insert(this.reportGrid.store.getCount(),rec);
                    }, this);
                }
            },{
                text : 'Save',
                iconCls : 'pwnd saveiconstrip',
                scope : this,
                handler : this.saveGridConfig
            },{
                text : 'Set Top Toolbar Config',
                iconCls : 'pwnd saveiconstrip',
                type: "tbar",
                scope : this,
                handler : this.setButtonConf
            },{
                text : 'Set Bottom Toolbar Config',
                type: "bbar",
                iconCls : 'pwnd saveiconstrip',
                scope : this,
                handler : this.setButtonConf
            }]
        });
        this.sqlPanel= new Wtf.Panel({
            autoScroll: true,
            split: true,
            border: false,
            maxHeight: 500,
            minHeight: 200,
            region: "south",
            title: "SQL Query",
            collapsible: true,
            collapsed: true
        });
        this.contEast = new Wtf.Panel({
            region: "east",
            width: "35%",
            split: true,
            layout: 'border',
            items: [this.tablePanel, this.tableColumnPanel]
        });
        this.contEast.on("bodyresize",function(obj,width,height){
            this.tableColumnPanel.setHeight(height/2);
        },this);
        this.contCenter = new Wtf.Panel({
            region: "center",
            width: "65%",
            split: true,
            layout: 'border',
            items: [this.reportColumnPanel,this.sqlPanel]
        });
    },

    setButtonConf: function(obj){
        var buttonConf = this.tBarConf;
        var title = "Top Toolbar Configuration";
        if(obj.type == "bbar"){
            title = "Bottom Toolbar Configuration";
            buttonConf = this.bBarConf;
        }
        var confWin = new Wtf.common.WtfButtonConfigWindow({
            title: title,
            resizable: false,
            border: false,
            height: 470,
            toolbarType: obj.type,
            defaultConf: buttonConf,
            width: 400
        });
        confWin.on("okClicked", function(window, conf){
            var buttonConf = "";
            for(var temp in conf){
                var handler = conf[temp].handler.replace(/"/g,"'");
                handler = handler.replace(/\n/g," ");
                buttonConf += "{\"text\":\"" + conf[temp].text + "\",\"handler\":\"" + handler + "\",\"type\":\"" + conf[temp].type + "\"},";
            }
            if(buttonConf !="")
                buttonConf = "[" + buttonConf.substring(0, (buttonConf.length - 1)) + "]";
            if(window.toolbarType == "tbar")
                this.tBarConf = buttonConf;
            else
                this.bBarConf = buttonConf;
            window.close();
        }, this);
        confWin.on("cancelClicked", function(window){
            window.close();
        }, this);
        confWin.show();
    },
    getQuery: function(){
        alert("build query");
    },
    showPreview: function(){
//        Wtf.get(this.previewDiv.id).slideIn();
        this.previewDiv.style.display = "block";
    },
    layoutBuilder: function(){
        var bObj = Wtf.getCmp(this.id + "_layoutBuilder");
        if(bObj === undefined){
            bObj = new Wtf.reportBuilder.reportLayout({
                title: "layout_" + this.title,
                layout: 'fit',
                closable: true,
                id: this.id + "_layoutBuilder"
            });
            this.ownerCt.add(bObj);
        }
        this.ownerCt.setActiveTab(bObj);
        this.ownerCt.doLayout();
    },
    onRender: function(conf){
        Wtf.reportBuilder.reportPanel.superclass.onRender.call(this, conf);
        this.createTableGrid();
        this.createColumnGrid();
        this.createQueryPanel();
        this.add(this.contEast);
        this.add(this.contCenter);
    },

    afterRender: function(conf){
        Wtf.reportBuilder.reportPanel.superclass.afterRender.call(this, conf);
        this.tableStore.on("load", function(){            
            this.doLayout();
        }, this);
        this.tableStore.load();
    },

    closePreviewClicked: function(){
        this.previewDiv.style.display = "none";
//        Wtf.get(this.previewDiv.id).slideOut();
    },
    createReportGrid : function() {
        this.reportSM = this.createSelectionModel();
        
        var checkHidden = new Wtf.grid.CheckColumn({
           header: "Hidden",
           dataIndex: 'hidden',
           width: 45
        });
        var checkBoxRen = new Wtf.grid.CheckColumn({
           header: "As Count",
           dataIndex: "countflag",
           width: 55
        });
        var xtypeStore
        if(this.gridFlag) {
            xtypeStore = [
                ['None'],
                ['Text'],
                ['Number(Integer)'],
                ['Number(Float)'],
                ['Date'],
                ['Checkbox'],
                ['Combobox']
            ];
        } else {
            xtypeStore = [
                ['None']
            ];
        }
        this.xtypeCombo = new Wtf.form.ComboBox({
            fieldLabel: 'Choose editor',
            bodyStyle: 'margin: auto; padding-top: 5px;',
            store: new Wtf.data.SimpleStore({
                fields: ['xtype'],
                data: xtypeStore
            }),
            displayField: 'xtype',
            valueField: 'xtype',
            allowBlank: false,
            typeAhead: true,
            mode: 'local',
            forceSelection: true,
            editable: false,
            triggerAction: 'all',
            value: 'xtype',
            selectOnFocus: true
        });
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
        this.reportCM = new Wtf.grid.ColumnModel([
            this.reportSM, {
                header: "Column",
                dataIndex: "columnname",
                 width: 200
            },{
                header: "Display Field",
                dataIndex: "displayfield",
                editor: new Wtf.form.TextField({
                    validateOnBlur: false,
                    validationDelay: 1000
                }),
                width: 100
            },{
                header: "Default Value",
                dataIndex: "defaultValue",
                editor: new Wtf.form.TextField({
                })
            },{
                header: "Editor",
                dataIndex: "xtype",
                renderer : Wtf.ux.comboBoxRenderer(this.xtypeCombo),
                editor: this.xtypeCombo,
                width: 62
            },{
                header: "Reference",
                dataIndex: "reftable",
                width: 120
            },{
                header: "renderer",
                dataIndex: "renderer",
                renderer : this.comboBoxRenderer(this.rendererCombo),
                editor: this.rendererCombo,
                width: 55
            },{
                header: "Summary Type",
                dataIndex: "summaryType",
                renderer : Wtf.ux.comboBoxRenderer(this.summaryCombo),
                editor: this.summaryCombo,
                width: 85
            },{
                header: "Combo Filter",
                width: 70,
                renderer: function(val) {
                      return "<img id='DeleteImg' class='comboFilter' src='images/1.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Combo Filter'></img>";
                }
            },checkHidden,checkBoxRen,{
                header: "Actions",
                dataIndex: 'id',
                width:70,
                renderer:function(value, css, record, row, column, store){
                    var actions = "<image src='images/up.png' title='Move Up' onclick=\"changeseq('"+record.get('seq')+"',0, 'repConfigGrid')\"/>"+
                    "<image src='images/down.png' style='padding-left:5px' title='Move Down' onclick=\"changeseq('"+record.get('seq')+"',1, 'repConfigGrid')\"/>";
                    actions +="<img class='delete' src='images/Cancel.gif' style='padding-left:5px' title='Delete Field'></img>";
                    return actions;
                }
            }
        ]);
        this.reportRec = Wtf.data.Record.create([
            {name: "columnname"},{name: "displayfield"},{name: "defaultValue"},{name: "xtype"},{name: "reftable"},{name: "renderer"},
            {name: "hidden"},{name: "countflag"},{name: 'seq'},{name: 'combogridconfig'},{name: 'id'},{name:'summaryType'}]);
        
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
        
        this.reportGrid = new Wtf.grid.EditorGridPanel({
            cm: this.reportCM,
            ddGroup :'GridDDGroup',
            height : 300,
            id : 'repConfigGrid',
            store: this.reportStore,
            clicksToEdit :1,
            sm: this.reportSM,
            plugins:[checkHidden,checkBoxRen],
            border: true,
            viewConfig: {
              //  forceFit: true
            }
        });
        this.reportStore.on("load", function(obj){
            this.buttonConf = "";
            var resObj = eval("(" + obj.reader.responseText + ")");
            var buttonConf = eval("(" + resObj.buttonConf + ")");
            if(buttonConf.tbar !== undefined){
                this.tBarConf = "";
                var tbar= buttonConf.tbar;
                for(var cnt = 0; cnt < tbar.length; cnt++){
                    var handler = tbar[cnt].functext.replace(/"/g,"'");
                    handler = handler.replace(/\n/g," ");
                    this.tBarConf += "{\"text\":\"" + tbar[cnt].text + "\",\"handler\":\"" + handler + "\",\"type\":\"" + tbar[cnt].type + "\"},";
                }
                if(this.tBarConf!="")
                    this.tBarConf = "[" + this.tBarConf.substring(0, (this.tBarConf.length - 1)) + "]";
            }
            if(buttonConf.bbar !== undefined){
                this.bBarConf = "";
                var bbar = buttonConf.bbar;
                for(var cnt = 0; cnt < bbar.length; cnt++){
                    var handler = bbar[cnt].functext.replace(/"/g,"'");
                    handler = handler.replace(/\n/g," ");
                    this.bBarConf += "{\"text\":\"" + bbar[cnt].text + "\",\"handler\":\"" + handler + "\",\"type\":\"" + bbar[cnt].type + "\"},";
                }
                if(this.bBarConf !="")
                    this.bBarConf = "[" + this.bBarConf.substring(0, (this.bBarConf.length - 1)) + "]";
            }
        }, this);
        this.reportStore.load();
        this.reportGrid.on("render",function(){
            var gridDropTargetEl = this.reportGrid.getView().el.dom.childNodes[0].childNodes[1]
            var destGridDropTarget = new Wtf.dd.DropTarget(gridDropTargetEl, {
                    ddGroup: 'ColGridDDGroup',
//                    copy       : false,
                    notifyDrop : function(ddSource, e, data){
                            // Generic function to add records.
                            function addRow(record, index, allItems) {
                                    var tablename = this.tableGrid.getSelectionModel().getSelected().data.name;
                                    var tRec = this.tableSM.getSelected();
                                    var tIndex = this.tableStore.indexOf(tRec);
                                    if(tIndex != -1){
                                        var rowDom = this.tableGrid.getView().getRow(tIndex);
                                        Wtf.get(rowDom).addClass("addedRow");
                                    }
                                    var colname = tablename+"."+record.data.name;
                                    // Search for duplicates
                                    var foundItem = this.reportGrid.store.find('columnname', colname);
                                    
                                    if (foundItem  == -1) {
                                            var rec = new this.reportRec({
                                                columnname : colname,
                                                displayfield : record.data.displayname,
                                                defaultValue:'',
                                                xtype : 'None',
                                                reftable : tablename,
                                                renderer : '',
                                                hidden: false,
                                                countflag: false,
                                                combogridconfig : '-1',
                                                summaryType:''
                                            });
                                            this.reportGrid.store.insert(this.reportGrid.store.getCount(),rec);
                                            // Call a sort dynamically
//                                            this.reportGrid.sort('name', 'ASC');
                                            //Remove Record from the source
//                                            ddSource.grid.store.remove(record);
                                    }
                            }
                            // Loop through the selections
                            Wtf.each(ddSource.dragData.selections ,addRow,this);
                            return(true);
                    }.createDelegate(this)
            },this);
        },this);
        this.reportGrid.on("cellclick", this.deleteRow, this);
        this.reportGrid.on("validateedit", this.validateedit, this);
    },

    validateedit:function(e){
        if (e.field=='displayfield'){
           return validateedit(e);
        }
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

    isAllowed:function(combo,record,index){
         return isAllowed(combo,record,index,this.reportGrid.getSelectionModel().getSelected().data.xtype);
     },
     
    addRow: function(record, index, allItems) {
        var tRec = this.scope.tableSM.getSelected();
        var tIndex = this.scope.tableStore.indexOf(tRec);
        if(tIndex != -1){
            var rowDom = this.scope.tableGrid.getView().getRow(tIndex);
            Wtf.get(rowDom).addClass("addedRow");
        }
//        var foundItem = this.scope.reportStore.query('columnname', record.data.name, true);
//        if (foundItem.length == 0) {
            var rec = new this.scope.reportRec({
                columnname: record.data.name,
                reftable: tRec.data.name
            });
            this.scope.reportGrid.getStore().add(rec);
//        } else {
//            var flg = false;
//            for(var i = 0; i < foundItem.length; i++){
//                if(foundItem.items[i].data["reftable"] == tRec.data.name){
//                    flg = true;
//                    break;
//                }
//            }
//            if(!flg){
//                var rec = new this.scope.reportRec({
//                    columnname: record.data.name,
//                    reftable: tRec.data.name
//                });
//                this.scope.reportGrid.getStore().add(rec);
//            }
//        }
    },
    
    deleteRow: function(gObj, ri, ci, e){
        var event = e;
        var rec = this.reportStore.data.items[ri];
        if(event.target.className == 'delete'){
            this.reportStore.remove(rec);
            var tItem = this.reportStore.query("reftable", rec.data.reftable, true);
            if(tItem.length == 0){
                var tRec = this.tableStore.query("name", rec.data.reftable);
                if(tRec.length != 0){
                    var tIndex = this.tableStore.indexOf(tRec.items[0]);
                    if(tIndex != -1){
                        var rowDom = this.tableGrid.getView().getRow(tIndex);
                        Wtf.get(rowDom).removeClass("addedRow");
                    }
                }
            }
        } else if(event.target.className == 'rendererClass') {
           this.newRendererWin('edit');
        }else if(event.target.className == 'comboFilter') {
            if(rec.data.xtype == 'Combobox' && rec.data.id){
               var comboFilterComponentObj=new  Wtf.comboFilterComponent({
                   gridConfigId:rec.data.id,
                   refTable:rec.data.reftable
               });
               comboFilterComponentObj.show();
            }
           
        }
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
            isstatic:false
        });

        if (type == 'new'){
            this.rendererStore.add(rendererRecord);
        }else{
            this.rendererStore.insert(this.rendererStore.find('id',rendererId),rendererRecord);
            this.reportGrid.getSelectionModel().getSelected().set("renderer",'0');
        }
        this.reportGrid.getSelectionModel().getSelected().set("renderer",rendererId);
    },
    createTableGrid: function(){
        this.tableSM = this.createSelectionModel(true);
        var tcm = new Wtf.grid.ColumnModel([
            this.tableSM, {
                header: "Table Name",
                dataIndex: "displayname"
            }
        ]);
        var tableRec = Wtf.data.Record.create([{
            name: "name",
            mapping: "name"
        },{
            name: "columns",
            mapping: "column"
        },{
            name: "displayname",
            mapping: "displayname"
        }]);
        this.tableStore = new Wtf.data.Store({
            url: Wtf.req.rbuild+"report.do",
            baseParams: {
                action : 0
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, tableRec)
        });
        this.tableGrid = new Wtf.grid.GridPanel({
            cm: tcm,
            store: this.tableStore,
            sm: this.tableSM,
            border: false,
            layout: 'fit',
            viewConfig: {
                forceFit: true
            }
        });
        this.tablePanel.add(this.tableGrid);        
        this.tableSM.on("rowselect", this.tableSelect, this);
    },

    createColumnGrid: function(){
        this.columnSM = this.createSelectionModel();
        var ccm = new Wtf.grid.ColumnModel([
            this.columnSM, {
                header: "Column Name",
                dataIndex: "displayname"
            },{
                header: "Type",
                dataIndex: "type"
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
        }]);
        this.columnStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, columnRec)
        });
        this.columnGrid = new Wtf.grid.GridPanel({
            cm: ccm,
            store: this.columnStore,
            border: false,
            sm: this.columnSM,
            ddGroup: 'ColGridDDGroup',
            enableDragDrop : true,
            layout: 'fit',
            autoHeight: true,
            viewConfig: {
                forceFit: true
            }
        });
        this.tableColumnPanel.add(this.columnGrid);
    },
    createQueryPanel: function(){
        this.queryPanel = new Wtf.reportBuilder.queryField({
            layout: 'fit'
        });
        this.sqlPanel.add(this.queryPanel);
    },
    tableSelect: function(obj, index, rec){
        var dobj = eval("(" + rec.data["columns"].trim() + ")");
        this.columnStore.loadData(dobj, false);
        this.columnGrid.getView().refresh();
        this.queryPanel.addTable(rec);
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
        if(this.reportGrid.store.getCount() > 0) {
            if(this.tablename.length==0) {
                if(this.gridFlag) {
                    this.requestToSaveConf(true);
                } else {
                    this.requestToSaveConf(false);
                }
//                this.createTableOpWin = new Wtf.Window({
//                        title: "Save report",
//                        width: 300,
//                        border: false,
//                        height: 110,
//                        buttons: [{
//                            text: "Save",
//                            id : 'saveBtn'+this.id,
//                            scope: this,
//                            handler: function() {
//                                Wtf.getCmp('saveBtn'+this.id).disable();
//                                this.requestToSaveConf(this.istablecreate.getValue());
//                            }
//                    }, {
//                        text: "Cancel",
//                        scope: this,
//                        handler: function(){
//                            this.createTableOpWin.close();
//                        }
//                    }],
//                    items: [new Wtf.form.FormPanel({
//                        cls: "newReportWin",
//                        border: false,
//                        items: [this.istablecreate = new Wtf.form.Checkbox({
//                          boxLabel : 'Create New Table' ,
//                          labelSeparator : ""
//                        })]
//                    })]
//                });
//                this.createTableOpWin.show();
            } else {
                this.requestToSaveConf(true);
            }
        }
    },
    getGridConf: function(){
        
    },

    requestToSaveConf : function(createTable){
        mainPanel.loadMask.msg = "Saving Configuration ...";
        mainPanel.loadMask.show();
        var jsonData = "[";
        var recCnt = this.reportStore.getCount();
        for(var cnt = 0; cnt < recCnt; cnt++) {
            var record = this.reportStore.getAt(cnt);
            jsonData += this.getJsonFromRecord(record,cnt) + ",";
        }
        jsonData = jsonData.substr(0, jsonData.length - 1) + "]";
        if(!this.storeConfig){
            Wtf.Ajax.request({
                url: Wtf.req.rbuild+'report.do',
                params: {
                    action: 3,
                    jsondata: jsonData,
                    tbar: this.tBarConf,
                    bbar: this.bBarConf,
                    reportid : this.reportgridid,
                    createtable : createTable
                },
                scope: this,
                success: function(response) {
//                    if(!this.storeConfig){
                        msgBoxShow(14,2);
                        if(this.createTableOpWin)
                            this.createTableOpWin.close();
                        mainPanel.loadMask.hide();
//                    } else {
//                        mainPanel.remove(this, true);
//                        mainPanel.setActiveTab(this.modBuildObj);
//                        var confWin = Wtf.getCmp(this.modBuildObj.id + "_gridConfigWindow");
//                        var tObj = eval("(" + response.responseText + ")");
//                        confWin.setGridConf(tObj.data, tObj.reportId);
//                        gridConfig = false;
//                        confWin.show();
//                    }
//                    if(this.createTableOpWin)
//                        this.createTableOpWin.close();
    //                newRepoWin.close();
    //                Wtf.Msg.alert("Success", "Config options saved successfully.");
    //                if(this.createTableOpWin)
    //                    this.createTableOpWin.close();
            //                    this.formStore.load();
                },
                failure: function() {
                    mainPanel.loadMask.hide();
                    msgBoxShow(4,1);
                }
            });
        } else {
            mainPanel.loadMask.hide();
            mainPanel.remove(this, true);
            mainPanel.setActiveTab(this.modBuildObj);
            var confWin = Wtf.getCmp(this.modBuildObj.id + "_gridConfigWindow");
            confWin.rendererStore.on("load",function(){
                confWin.setGridConf(jsonData, this.reportgridid);
                gridConfig = false;
                if(this.createTableOpWin)
                    this.createTableOpWin.close();
                confWin.show();
            },this);
            confWin.rendererStore.load();
        }
    }
});

Wtf.comboFilterComponent=function(config){
    Wtf.apply(this,config);
     this.buttons = [{
        text: 'OK',
        scope: this,
        handler: function() {
            this.close();
        }
    }];

    this.columnRecord = Wtf.data.Record.create([{
        name: 'name'
    },{
        name: 'refflag'
    },{
        name: 'reftable'
    },{
        name: 'configtype'
    },{
        name: 'displayfield'
    },{
        name: 'reportid'
    }]);
    this.modColStore = new Wtf.data.Store({
        url: Wtf.req.rbuild+'report.do',
        baseParams : {
            action : 32,
            refTable:this.refTable
        },
        method: 'POST',
        reader: new Wtf.data.KwlJsonReader({
            root:'data'
        }, this.columnRecord)

    });

    this.modColStore.load();
    this.colCombo = new Wtf.form.ComboBox({
        store : this.modColStore,
        width:175,
        displayField:'displayfield',
        valueField : 'name',
        mode: 'local',
        triggerAction: 'all',
        emptyText : 'Select column to filter',
        forceSelection:true,
        typeAhead:true
    });
    this.addfilterBtn=new Wtf.Toolbar.Button({
        text:'Add Filter',
        tooltip :'Click to add filter',
        handler : function(){
            if (this.colCombo.getValue() != ""){
                if (this.filterStore.find('fieldname',this.colCombo.getValue()) == -1 ){
                     var columnRecord=this.modColStore.getAt(this.modColStore.find('name',this.colCombo.getValue()));
                     Wtf.Ajax.requestEx({
                        url: Wtf.req.rbuild+'report.do',
                        method:'POST',
                        params: {
                            action: 33,
                            fieldname: columnRecord.data.name,
                            displayfield: columnRecord.data.displayfield,
                            gridconfigid:this.gridConfigId,
                            refmoduleid:columnRecord.data.reportid,
                            xtype:columnRecord.data.configtype,
                            reftable:columnRecord.data.reftable
                        }
                    },
                    this,
                    function(resp) {
                        var resultObj = eval('('+resp+')');
                        if(resultObj.success) {
                           msgBoxShow(['Info', "Filter created successfully"], Wtf.MessageBox.OK);
                           var filterRecord=new this.filterRecord({
                                id:resultObj.id,
                                fieldname: columnRecord.data.name,
                                displayfield: columnRecord.data.displayfield,
                                gridconfigid:this.gridConfigId,
                                reportid:columnRecord.data.reportid,
                                xtype:columnRecord.data.configtype,
                                reftable:columnRecord.data.reftable,
                                refcol:''
                            });
                          this.filterStore.add(filterRecord);
                        }else{
                            msgBoxShow(['Info',  "Error occurred at server"], Wtf.MessageBox.Error);
                        }
                    },
                    function() {
                        //panel.enable();
                        }
                    );
                    this.colCombo.setValue("");
                }else{
                    msgBoxShow(['Error', "Filter already exists"], Wtf.MessageBox.Error);
                }
            } else{
                msgBoxShow(['Error', "Please select column"], Wtf.MessageBox.Error);
            }

        },
        scope: this
    });

    this.cm=new Wtf.grid.ColumnModel([
    {
        header: "Column",
        dataIndex:'displayfield',
        width:250
    },{
        header: "xtype",
        dataIndex:'xtype'
    },{
        header: "Delete",
        dataIndex:'delField',
        renderer : function(val) {
            return "<img id='DeleteImg' class='delete' src='images/Cancel.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete filter'></img>";
        }
    }
    ]);

    this.filterRecord = Wtf.data.Record.create([{
        name: 'id'
    },{
        name: 'fieldname'
    },{
        name: 'displayfield'
    },{
        name: 'xtype'
    },{
        name: 'reftable'
    },{
        name: 'refcol'
    },{
        name: 'gridconfigid'
    },{
        name: 'reportid'
    }]);
    this.GridJsonReader = new Wtf.data.KwlJsonReader({
        root: "data",
        totalProperty: 'count'
    }, this.filterRecord);

    this.filterStore = new Wtf.data.Store({
        reader: this.GridJsonReader,
        url: Wtf.req.rbuild+'report.do',
        method : 'POST',
        baseParams: {
            action: 34,
            gridconfigid:this.gridConfigId
        }
    });

    this.filterStore.load();
    this.filterGrid=new Wtf.grid.GridPanel({
        store: this.filterStore,
        cm:this.cm,
        stripeRows: true,
        width:425,
        height:300,
        border:false,
        forceFit: true,
        tbar:[this.colCombo,this.addfilterBtn]
    });

     this.filterPanel = new Wtf.Panel({
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
                html:getHeader('images/createuser.gif','Combo Filter ','Specify details')
            },{
                region: 'center',
                bodyStyle:"background:#f1f1f1;",
                border: false,
                items: [
                    this.filterGrid
                ]
            }

            ]
        })
    });

   this.items=[this.filterPanel];
   this.filterGrid.on('cellclick', this.deleteComboFilter, this);
   Wtf.comboFilterComponent.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.comboFilterComponent, Wtf.Window, {
    title:this.title,
    layout:'fit',
    iconCls: 'winicon',
    modal: true,
    height:400,//220
    width:425,
    scope: this,
    deleteComboFilter:function(gObj, ri, ci, e){
        var event = e;
        var rec = this.filterStore.data.items[ri];
        if(event.target.className == 'delete'){
           Wtf.Ajax.requestEx({
                        url: Wtf.req.rbuild+'report.do',
                        method:'POST',
                        params: {
                            action: 35,
                            comboFilterId: rec.data.id
                        }
                    },
                    this,
                    function(resp) {
                        var resultObj = eval('('+resp+')');
                        if(resultObj.success) {
                           msgBoxShow(['Info', "Filter deleted successfully"], Wtf.MessageBox.OK);
                           this.filterStore.remove(rec);
                        }else{
                            if (resultObj.msg){
                                msgBoxShow(['Error',  resultObj.msg], Wtf.MessageBox.Error);
                            }else{
                                msgBoxShow(['Error',  "Error occurred at server"], Wtf.MessageBox.Error);
                            }
                        }
                    },
                    function() {
                        //panel.enable();
                        }
                    );
        } 
    }
});
