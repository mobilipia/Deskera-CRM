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
Wtf.reportDisplayGrid = function(config){
    Wtf.reportDisplayGrid.superclass.constructor.call(this,config);
};

Wtf.extend(Wtf.reportDisplayGrid,Wtf.Panel,{
    bodyStyle :"background-color: #ffffff;",
    layout : 'fit',
    autoScroll : true,
    border:false,
    initComponent : function(config) {
        Wtf.reportDisplayGrid.superclass.initComponent.call(this,config);
        if(this.isFilter===undefined)
            this.isFilter = false;
    },

    updateGrid : function(response) {
        var obj = eval('(' + response.trim() + ')');
        this.rec =  Wtf.data.Record.create(this.getStoreFields(obj.columnheader));
        this.reportTable = obj.tablename;
        this.isTableCreate = false;
        if(this.reportTable.length>0)
            this.isTableCreate = true;
        this.storeItems = obj.columnheader;
        this.modstore = new Wtf.data.GroupingStore({
            baseParams: {
                action: 9,
                reportid : this.reportid,
                taskid : this.taskid,
                isFilter : this.isFilter,
                filtervalue :this.filtervalue,
                filterfield : this.filterfield,
                filterJson:this.filterJson
            },
            url: 'reportbuilder.jsp',
            remoteSort:true,
            reader: new Wtf.data.KwlJsonReader({
                        root:"data",
                        totalProperty: 'count',
                        remoteGroup:true,
                        remoteSort: true
            }, this.rec),
            sortInfo: {field: '', direction: 'ASC'}
        });

         this.quickPanelSearch = new Wtf.KWLTagSearch({
                width: 150,
                emptyText: 'Enter search value',
                field: "name"
         });

         this.pg = new Wtf.PagingSearchToolbar({
            pageSize: 15,
            searchField: this.quickPanelSearch,
            store: this.modstore,
//            displayInfo: true,
            displayMsg: 'Displaying items {0} - {1} of {2}',
            emptyMsg: "No items to display",

            plugins: this.pP = new Wtf.common.pPageSize({})
        });
//        this.permsObj = eval('('+this.permsObj+')');
        this.modstore.on('load', function(store) {
            this.quickPanelSearch.StorageChanged(store);
//            var addperm = eval('('+this.permsObj[0][2]+')');
            var addperm = eval('('+this.permsObj[Wtf.mbuild.permActions.addRecord]+')');
            if(checktabperms(addperm.permgrid,addperm.perm) && this.isTableCreate) {
                this.addBlankRow();
            }
        }, this);
        this.modstore.on('datachanged', function() {
            var p = this.pP.combo.value;
            this.quickPanelSearch.setPage(p);
        }, this);

        this.chk_box = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:true
        });

        var tBar = [WtfGlobal.getLocaleText("crm.goalsettings.qsearch.label")+": ",this.quickPanelSearch,'-',{
                text : "Advanced Search",
                scope : this,
                tooltip:'Manage search with your preferance',
                iconCls : 'pwnd editicon',
                handler:this.configurAdvancedSearch
           }];
        if(this.isTableCreate) {
//            var addperm = eval('('+this.permsObj[0][2]+')');
            var addperm = eval('('+this.permsObj[Wtf.mbuild.permActions.addRecord]+')');
            if(checktabperms(addperm.permgrid,addperm.perm)){
                tBar.push({text : 'Add',iconCls: 'pwnd addicon',scope : this,handler : function(){
                    this.addBlankRow();
                }});
            }
        }
        var buttonConf = eval("(" + obj.buttonConf + ")");
        this.createToolBar(buttonConf.tbar, tBar);
        this.configInfo = eval('('+obj.stdconfig+')');
        tBar = this.getConfigButton(this,this.configInfo,tBar);
        var bottomTBar = [ this.pg ];
        this.createToolBar(buttonConf.bbar, bottomTBar);
//        tBar.push({ text : 'Add New Comment',scope : this,handler : this.addComment });
        this.groupingView = new Wtf.grid.GroupingView({
   //         forceFit: true,
            showGroupName: false,
    //        enableGroupingMenu: false,
            hideGroupedColumn: false
        });
        this.modulegrid = new Wtf.grid.EditorGridPanel({
            title:this.reportname+" List",
            sm:this.chk_box,
            cm: this.getColModel(obj.columnheader),
            id: 'moduleGr'+this.id,
            clicksToEdit : 1,
            ds: this.modstore,
            border : false,
            region :'center',
            plugins : this.gridPlugins,
            layout:'fit',
            loadMask: {
                msg: 'Loading...'
            },
             view:this.groupingView ,
            //viewConfig: {forceFit: true},
            tbar : tBar,
            bbar: bottomTBar
        });
        this.modulegrid.on("validateedit", this.validateedit, this);
        this.modulegrid.on("cellclick",function(gd, ri, ci, e){
            var event = e;
            var record = gd.getStore().getAt(ri);
            var recData = gd.getStore().getAt(ri).data;
            var recId = this.reportTable+_reportHardcodeStr+"id";
            if(event.target.className == "update") {
                this.insertRecord(record);
            } else if(event.target.className == "delete") {
                Wtf.MessageBox.confirm('Delete', 'Are you sure you want to delete selected record', function(btn){
                    if(btn=="yes"){                        
                        if(record.data[recId].length>0 && this.isTableCreate) {
                            Wtf.Ajax.requestEx({
                                url: 'reportbuilder.jsp',
                                params: {
                                    action: 11,
                                    reportid: this.reportid,
                                    id: record.data[recId]
                                }
                            },
                            this,
                            function(resp) {
                                var resultObj = eval('('+resp+')');
                                if(resultObj.success) {
                                    msgBoxShow(["Success", resultObj.msg],2);
                                    this.loadGridStore();
                                } else {
                                    msgBoxShow(28,1);
                                }
                            },
                            function() {
                                msgBoxShow(28,1);
                            });
                        } else {
                            gd.getStore().remove(record);
                            this.addBlankRow();
                        }
                    }
                },this);
            } else if(event.target.className == "opentab") {
               Wtf.Ajax.requestEx({
                    url: Wtf.req.mbuild+'form.do',
                    method: 'POST',
                    params: ({
                        action : 25,
                        moduleid: this.reportid,
                        taskid: this.taskid,
                        recordid : recData[recId],
                        basemode : 1
                    })},
                    this,
                    function(result, req){
                        var nodeobj = eval("(" + result.trim() + ")");
                        if(nodeobj.success) {
//                            var formitems = URLDecode(nodeobj.data[0].jsondata);
//                            formitems = Wtf.decode(formitems);
                            this.addSubTabs(recData,nodeobj.subtabs)
                        } else {
                            msgBoxShow(4,1);
                        }
                    },
                    function(){
                        msgBoxShow(4,1);
               });
            } else if(event.target.className == "showComments") {
                this.showComments(recData[recId]);
            } else if(event.target.className == "showDocuments") {
                this.showDocuments(recData[recId]);
            }
        },this);
        this.modulegrid.on("render",function(){
             this.loadGridStore();
        },this);
    },

    validateedit:function(e){
        if (e.grid.colModel.config[e.column].xtype == 'Text'){
           e.record.set(e.field,ScriptStripper(HTMLStripper(e.value)));
        }
    },
    createToolBar: function(conf, tBar){
        if(conf !== undefined){
            for(var cnt = 0; cnt < conf.length; cnt++){
                if(checktabperms(conf[cnt].permgrid,conf[cnt].perm)) {
                    if(conf[cnt].type == "js"){
                        tBar.push({
                            text: conf[cnt].text,
                            handler: eval("(" + conf[cnt].functext + ")"),
            //                id: buttonConf[cnt].id +
                            scope: this
                        });
                    } else {
                        tBar.push({
                            text: conf[cnt].text,
                            handler: makeJspRequest,
                            url: conf[cnt].functext,
                            scope: this
            //                id: buttonConf[cnt].id +
                        });
                    }
                }
            }
        }
    },
    showComments:function(recordId){
         var commentsComponentObj=new Wtf.commentsComponent({
            recordId:recordId,
            moduleScope:this,
            permsObj:this.permsObj
        });
        commentsComponentObj.on("updateModuleStore",function(){
            this.loadGridStore();
        },this);
        mainPanel.add(commentsComponentObj);
        mainPanel.setActiveTab(commentsComponentObj.id);
        mainPanel.doLayout();
    },
    showDocuments:function(recordId){
        openDocListWin(this.reportid,recordId,this.permsObj);
    },

    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.doLayout();
        this.objsearchComponent.columnStore.loadData(this.fields);
    },

 getConfigButton:function(scope,configInfo,buttArray){
        if(configInfo.stdconfig){
            for(var i = 0; i<configInfo.stdconfig.length;i++){
//                var addcommperm = eval('('+this.permsObj[3][5]+')');
//                var adddocperm = eval('('+this.permsObj[5][7]+')');
                var addcommperm = eval('('+this.permsObj[Wtf.mbuild.permActions.addComment]+')');
                var adddocperm = eval('('+this.permsObj[Wtf.mbuild.permActions.addDocument]+')');
                if(configInfo.stdconfig[i].id==2 && checktabperms(adddocperm.permgrid,adddocperm.perm)){                
                    buttArray.push(new Wtf.Toolbar.Button({
                        text : WtfGlobal.getLocaleText("crm.editor.exportBTN"),//"Add Documents",
                        scope : scope,
                        disabled: false,
                        handler : function() {
                            if(scope.chk_box.getSelected()) {
                                 var recId = scope.chk_box.getSelected().data[scope.reportTable+_reportHardcodeStr+"id"];
                                openDocListWin(scope.reportid,recId,scope.permsObj);
                            }else{
                                  msgBoxShow(29,1);//select atleast one record.
                            }
                        }
                    }));
                }else if(configInfo.stdconfig[i].id==1 && checktabperms(addcommperm.permgrid,addcommperm.perm)){
                    buttArray.push(new Wtf.Toolbar.Button({
                        text : WtfGlobal.getLocaleText("crm.common.addnewcomment"),//"Add New Comment",
                        scope : this,
                        disabled: false,
                        handler : function() {
                            if(scope.chk_box.getSelected()) {
                                var recId = scope.chk_box.getSelected().data[scope.reportTable+_reportHardcodeStr+"id"];
                                this.commentWin = new Wtf.newCommentWindow({
                                    title:WtfGlobal.getLocaleText("crm.common.addnewcomment"),//"Add New Comment",
                                    closable:true,
                                    border:false,
                                    modal:true,
                                    width : 340,
                                    height: 250,
                                    iconCls : 'win',
                                    layout: "fit",
                                    recordid : recId,
                                    moduleid : scope.reportid,
    //                                reftable : scope.reportTable,
                                    resizable: false,
                                    gridstore:scope.modulegrid.store
                                });
                                this.commentWin.show();
                            }else{
                                  msgBoxShow(29,1);//select atleast one record.
                            }
                        }
                    }));
                }else if(configInfo.stdconfig[i].id==3){
                    buttArray.push(new Wtf.Toolbar.Button({
                        text : WtfGlobal.getLocaleText("crm.editor.exportBTN"),//"Export",
                          iconCls: 'pwnd exporticon',
                        tooltip: {
                            title:WtfGlobal.getLocaleText("crm.common.exportdata"),//'Export Data',
                            text: WtfGlobal.getLocaleText("crm.common.exportbtnttip.clicktoexp")//"Click to export data"
                        },
                        scope : this,
                        disabled: false,
                        menu: [ new Wtf.Action({
                                    text: WtfGlobal.getLocaleText("crm.editor.export.csv"),//'Export to CSV file',
                                    iconCls: 'pwnd exporticon',
                                    handler : function() {
                                        exportmoduleData(scope.reportid,scope.reportname,"csv",false, scope.taskid);
                                    },
                                    scope: this
                                })/*,
                                new Wtf.Action({
                                    text: 'Export to PDF file',
                                    iconCls: 'pwnd exporticon',
                                    handler: function() {
                                        alert("in export PDF functionality");
                                    },
                                    scope: this
                                })*/
                        ]
                    }));
                }

            }
        }
        return buttArray;
    },

    addSubTabs : function(recData,subtabs,formitems) {
        var itemsArray = [];
        var recId = recData[this.reportTable+_reportHardcodeStr+"id"];
        var recIdForTab = "subtabpanelcom" +this.reportTable+_reportHardcodeStr+"id";
        var tabPanel = new Wtf.TabPanel({
            id:  recIdForTab,
            border: false,
            activeTab : 0,
            title : " Record Name ",
            enableTabScroll: true,
            closable : true/*,
            items: itemsArray*/
        });
        mainPanel.add(tabPanel);
        mainPanel.setActiveTab(tabPanel.id);
        mainPanel.doLayout();
        if(subtabs && subtabs.length>0) {

            for(var cnt=0;cnt<subtabs.length;cnt++) {
                var moduleComp = null;
                if(subtabs[cnt].mode=='0') {
                    var nodeobj = eval("(" + subtabs[cnt].data + ")");
                    var formitems = URLDecode(nodeobj.data[0].jsondata);
                    formitems = Wtf.decode(formitems);
                    configFields={ismodule:true,
                         data:formitems,
                         cmpId:'Module_'+recId+"_"+ subtabs[cnt].moduleid,
                         moduleName:subtabs[cnt].name,
                         moduleId:subtabs[cnt].moduleid,
                         containerId:recIdForTab,
                         isFilter:true,
                         filterfield: subtabs[cnt].refcolumn,
                         filterValue:recId
                     };
                    openGridModule(configFields);
                } else {
                     configFields={ismodule:false,
                         data:'',
                         cmpId:'Report' + subtabs[cnt].moduleid,
                         moduleName:subtabs[cnt].name,
                         moduleId:subtabs[cnt].moduleid,
                         containerId:recIdForTab,
                         isFilter:true,
                         filterfield: subtabs[cnt].refcolumn,
                         filterValue:recId
                     };
                    openGridModule(configFields);
                }
            }
        }
    },
    addComment : function() {
        if(this.chk_box.getSelected()) {
            var recId = this.chk_box.getSelected().data[this.reportTable+_reportHardcodeStr+"id"];
            this.commentWin = new Wtf.newCommentWindow({
                title:"Add New Comment",
                closable:true,
                border:false,
                modal:true,
                width : 340,
                height: 250,
                iconCls : 'win',
                layout: "fit",
                recordid : recId,
                moduleid : this.reportid,
                reftable : this.reportTable,
                resizable: false
            });
            this.commentWin.show();
        } else {
            msgBoxShow(29,1);//select atleast one record.
        }
    },

    addBlankRow : function() {
        var recObj = {};
        var recFields = this.modstore.fields.items;
        for(var cnt =0 ;cnt<recFields.length;cnt++) {
            recObj[recFields[cnt].name] = '';
        }
        var p = new this.rec(recObj);
        this.modstore.insert(this.modstore.getCount(), p);
    },
    getStoreFields:function(columnheader) {
        var fields = [];
        for(var fieldcnt = 0; fieldcnt < columnheader.length; fieldcnt++) {
            var fObj = {};
            fObj['name'] = columnheader[fieldcnt][1];
//            fObj['type'] = 'string';
            fields[fields.length] = fObj;
        }
        return fields;
    },

    checkBoxRen : function(value){
        if(value)
            return '<input type="Checkbox" checked="'+value+'" class="checkboxclick"/>';
        else
            return '<input type="Checkbox" class="checkboxclick"/>';
    },

    createComboFilter : function(gridconfigid) {
        this.combColObj = [];
        Wtf.Ajax.requestEx({
            url: 'reportbuilder.jsp',
            method:'POST',
            params: {
                action : 36,
                id : gridconfigid
            }},
            this,
            function(resp) {
                var obj = eval('(' + resp.trim() + ')');
                if(obj.data) {
                    var dataArray = obj.data;
                    for(var cnt = 0; cnt<dataArray.length ; cnt++) {
                         var xtype = dataArray[cnt]['xtype'];
                         var fieldname = dataArray[cnt]['fieldname'];
                         var displayfield = dataArray[cnt]['displayfield'];
                         var fieldObj;
                         if(xtype == 'combo') {
                             var storeObj = new Wtf.data.Store({
                                reader: new Wtf.data.KwlJsonReader({root:'data'}, ["id","name"]),
                                autoLoad : false,
                                url : "reportbuilder.jsp",
                                baseParams : {
                                    action : 37,
                                    refmoduleid : dataArray[cnt]['refmoduleid'],
                                    refcol : dataArray[cnt]['refcol']
                                }
                             });
                             fieldObj = {
                                xtype: 'combo',
                                store: storeObj,
                                mode: 'remote',
                                displayField: "name",
                                valueField: "id",
                                field: "",
                                fieldLabel: displayfield
                             }
                         } else {
                             fieldObj = {
                                fieldLabel: displayfield,
                                xtype: 'textfield'
                             }
                         }
                         this.combColObj.push(fieldObj);
                    }
                }
            },
            function() {
            }
        );
        return this.combColObj;
    },

    getColModel : function (columnHeader) {
            var colConfig = [];
            this.fields = [];
            this.gridPlugins = [];
            colConfig[colConfig.length] = new Wtf.grid.RowNumberer();
            colConfig[colConfig.length] = new Wtf.grid.CheckboxSelectionModel({
                singleSelect:true
            });
        var summaryPluginAdded=false;
        for(var columncnt = 0; columncnt<columnHeader.length ; columncnt++) {
            if(!columnHeader[columncnt][3]){
            var colObj = {};
            colObj['header'] = columnHeader[columncnt][0];
            colObj['dataIndex'] = columnHeader[columncnt][1];
            colObj['hidden']= columnHeader[columncnt][3];
            colObj['sortable'] = true;
            colObj['groupable'] = true;
            colObj['xtype'] = columnHeader[columncnt]['conftype'];
            colObj['combogridconfig'] = columnHeader[columncnt][5];
            columnHeader[columncnt][3] == false ? this.fields.push(colObj):null;
            if (columnHeader[columncnt][6] != undefined ){
                var strfunc=columnHeader[columncnt][6];
                if (strfunc.indexOf("function") < 0) {
                    colObj['renderer'] = this[strfunc].createDelegate(this);
                }else{
                        strfunc=strfunc.replace(/\n/g,"").replace(/&#43;/g,"+");
                        colObj['renderer'] =eval('('+strfunc+ ')') ;
                }
            }
            if (columnHeader[columncnt][7] != undefined && columnHeader[columncnt][7] != '' ){
                if (!summaryPluginAdded){
                    this.summary = new Wtf.ux.grid.GridSummary();
                    this.gridPlugins.push(this.summary);
                    summaryPluginAdded=true;
                }
                colObj['summaryType']= columnHeader[columncnt][7];
                colObj['summaryRenderer']= this['total'].createDelegate(this);
            }

                if(columnHeader[columncnt][2] !='None') {
                    editor = columnHeader[columncnt][2];
                    switch(editor){
                        case 'Text' :
                            colObj['editor'] = new Wtf.form.TextField({
                                validateOnBlur: false,
                                validationDelay: 1000
                            });
                            break;

                        case 'Number(Integer)' :
                            colObj['editor'] = new Wtf.form.NumberField({
                                allowDecimals : false
                            });
                            break;

                        case 'Number(Float)' :
                            colObj['editor'] = new Wtf.form.NumberField({
                            });
                            break;

                        case 'Date' :
                            colObj['editor'] = new Wtf.form.DateField({
                                format: Wtf.getDateFormat()//'D j-m-Y'
                            });
                            colObj['renderer'] = this.formatDate;
                            break;

                        case 'Checkbox' :
                            colObj = new Wtf.grid.CheckColumn(colObj);
                            this.gridPlugins.push(colObj);
                            break;

                        case 'Combobox' :
                            var filterConf = [];
//                            this.filterConf = this.createComboFilter(columnHeader[columncnt]['gridconfigid']);
                            var columnname = columnHeader[columncnt][1];

                            var ComboRecord = Wtf.data.Record.create(this.createComboRecord(columnname,columnHeader[columncnt][4]));
                            var ComboStore = new Wtf.data.Store({
                                reader: new Wtf.data.KwlJsonReader({
                                    root:"data"
                                }, ComboRecord),
                                url: 'reportbuilder.jsp',
                                baseParams : {
                                    action : 8,
                                    columnname : (columnHeader[columncnt][5] != '-1'?this.fTableCol.split(",")[0]:this.fTableCol),
                                    reftable : columnHeader[columncnt][4],
                                    combogridconfig : columnHeader[columncnt][5],
                                    reportid : this.reportid
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
                                selectOnFocus: true,
                                plugins: (filterConf.length > 0) ? new Wtf.common.comboFilter({
                                    filterConf: filterConf
                                }):null
                            });
                            colObj['editor'] = comboObj;
                            colObj['renderer'] = Wtf.ux.comboBoxRenderer(comboObj);
                            if(this.fTableCol.split(",").length>1) {
                                comboObj.on("select",function(combo,record,index){
                                    var columns = combo.store.baseParams.columnname.split(",");
                                    var selRec  = this.modulegrid.getSelectionModel().getSelected();
                                     for(var cnt=0; cnt < columns.length; cnt++){
                                        var columnIndex=this.modulegrid.getColumnModel().findColumnIndex(columns[cnt]);
                                        if (columnIndex != -1){
                                            var column=this.modulegrid.getColumnModel().getColumnById( this.modulegrid.getColumnModel().getColumnId(columnIndex) );
                                            var combogridconfig=column.combogridconfig;
                                            var xtype=column.xtype;
                                            if (xtype == "Combobox" && combogridconfig == -1){
                                                selRec.set(columns[cnt],record.data[columns[cnt].split(_reportHardcodeStr)[0]+_reportHardcodeStr+"id"]);
                                            }else{
                                                selRec.set(columns[cnt],record.data[columns[cnt]]);
                                            }
                                       }else{
                                           selRec.set(columns[cnt],record.data[columns[cnt]]);
                                       }
                                    }
                                },this);
                            }
                            break;
                    }
                }
                colConfig[colConfig.length] = colObj;
            }
            }
            var colObj = {};
//            var updateperm = eval('('+this.permsObj[1][3]+')');
            var updateperm = eval('('+this.permsObj[Wtf.mbuild.permActions.editRecord]+')');
            if(checktabperms(updateperm.permgrid,updateperm.perm)){
                colObj['header'] = "Update";
                colObj['dataIndex'] = "updateflag";
                colObj['renderer'] = function(val) {
                    return "<img class='update' src='images/tick.png' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Update Record'></img>";
                };
                colConfig[colConfig.length] = colObj;
            }
//            var deleteperm = eval('('+this.permsObj[2][4]+')');
            var deleteperm = eval('('+this.permsObj[Wtf.mbuild.permActions.deleteRecord]+')');
            if(this.isTableCreate && checktabperms(deleteperm.permgrid,deleteperm.perm)) {
                colObj = {};
                colObj['header'] = "Delete";
                colObj['dataIndex'] = "deleteflag";
                colObj['renderer'] = function(val) {
                    return "<img class='delete' src='images/Cancel.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete Record'></img>";
                };
                colConfig[colConfig.length] = colObj;
            }
            var reportcm = new Wtf.grid.ColumnModel(colConfig);
            return reportcm;
    },

    renderItalic: function(data, metadata, record, rowIndex, columnIndex, store){
            return '<i>' + data + '</i>';
    },

    detailviewRender : function(val) {
        return "<a href = '#' class ='opentab' > " + val + "</a>";
    },

    total : function(val) {
        return val ;
    },
    formatDate : function(value,metadata,record,row,col,store) {
        if(typeof value=='string') {
            if(value!==""){
                var dateVal = Date.parseDate(value,'Y-m-d H:i:s.0');
                record.set(this.name,dateVal);
                return value ? dateVal.format(Wtf.getDateFormat()) : '';
            }else
                return "";
        }
        else if(typeof value=='object') {
            return value ? value.format(Wtf.getDateFormat()) : '';
        }
    },

    createJsonData : function(record) {
        var jsonData = "{";
        for(var cnt =0;cnt<this.storeItems.length;cnt++) {
            var dataIndex = this.storeItems[cnt][1];
            if(this.storeItems[cnt][2] == 'Date') {
                jsonData += dataIndex+":\""+record.data[dataIndex].format(Wtf.getDateFormat())+"\",";
            } else {
                jsonData += dataIndex+":\""+record.data[dataIndex]+"\",";
            }
        }
        jsonData = jsonData.substr(0,jsonData.length-1)+"}";
        return(jsonData);
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
            if(filedSplit[0] == reftable && filedSplit[1]!=comboField) {
                var fObj = {};
                fObj['name'] = this.storeItems[fieldcnt][1];
                recObj.push(fObj);
                this.fTableCol += this.storeItems[fieldcnt][1]+",";
            }
        }
        this.fTableCol = this.fTableCol.substr(0,this.fTableCol.length-1);
        return recObj;
    },

    insertRecord : function(record){
        if(record) {
            var recId = this.reportTable+_reportHardcodeStr+"id";
            var jsonData = "["+this.createJsonData(record)+"]";
            Wtf.Ajax.requestEx({
                url: 'reportbuilder.jsp',
                method:'POST',
                params: {
                    action : ((this.isTableCreate && record.data[recId].length>0 ) || (!this.isTableCreate)) ? 10 : 7, // 7 for insert and 10 for edit
                    reportid : this.reportid,
                    jsondata : jsonData
                }},
                this,
                function(resp) {
                    var res = eval('('+resp+')');
                    if(res.success) {
                        msgBoxShow(["Success", res.msg],2);
                        this.loadGridStore();
                    } else {
                        msgBoxShow(28,1);
                    }
                },
                function() {
                }
            );
        }
    },

    DisplayTab : function() {
        this.objsearchComponent=new Wtf.mbuild.advancedSearchComponent({
            getComboDataAction: 8,
            isReport:true,
            moduleid:this.reportid
        });
        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);

        this.innerPanel = new Wtf.Panel({
            layout : 'border',
            bodyStyle :"background-color:transparent;",
            border:false,
            items : [this.objsearchComponent,this.modulegrid]
        });
        this.add(this.innerPanel);
        this.doLayout();
    },

    onRender : function(config) {
        Wtf.reportDisplayGrid.superclass.onRender.call(this,config);
        this.getReportData();
    },

    getReportData : function() {
        Wtf.Ajax.requestEx({
            url: 'reportbuilder.jsp',
            method:'POST',
            params: {
                action : 4,
                reportid : this.reportid
            }
            },
            this,
            function(resp) {
                var resText = resp;
                if(this.modulegrid) {
                    var obj = eval('(' + resText + ')');
                    this.modstore.loadData(obj.data);
                } else {
                    this.updateGrid(resText);
                    this.DisplayTab();
                }
            },
            function() {
            }
        );
    },

    filterStore:function(Json){
        this.filterJson=Json;
        this.modstore.baseParams= {
                action: 9,
                reportid : this.reportid,
                taskid : this.taskid,
                isFilter : this.isFilter,
                filtervalue :this.filtervalue,
                filterfield : this.filterfield,
                filterJson:this.filterJson
        };
        this.modstore.load({params:{
              start:0,
              limit:this.pP.combo.value
        }});
    },

    clearStoreFilter:function(){
        this.objsearchComponent.hide();
        this.doLayout();
        this.modstore.baseParams= {
                action: 9,
                reportid : this.reportid,
                taskid : this.taskid,
                isFilter : this.isFilter,
                filtervalue :this.filtervalue,
                filterfield : this.filterfield,
                filterJson:''
        };
        this.modstore.load({params:{
              start:0,
              limit:this.pP.combo.value
        }});
    },
    
    loadGridStore : function() {
        this.modstore.load({params:{
              start:this.pg.cursor,
              limit:this.pP.combo.value
        }});
    } 
});

//Add new field
Wtf.newGridFieldWindow = function(config) {
//    Wtf.apply(this, config);
    this.buttons = [{
        text: WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
        handler: function() {
            if (this.fieldformPanel.form.isValid()){
                var combogridconfig = '-1';
                //                    var jsondata;
                if(this.xtypeCombo.getValue() == 'Combobox') {
                    combogridconfig = this.qType1.getValue();
                //                         jsondata = Wtf.common.CreateJsonStart();
                //                         var ColumnList = new Array("id", "name");
                //                         for (var ii = 0; ii < this.otherStore.getCount() - 1; ii++){
                //                             var ContentList = new Array((ii+1), this.otherStore.getAt(ii).data['name']);
                //                             var jobj = Wtf.common.getJsonObject(ColumnList, ContentList);
                //                             jsondata += jobj;
                //                        }
                //                        jsondata = Wtf.common.CreateJsonEnd(jsondata);
                }
                //             this.nameVal.getValue().trim().toLowerCase().replace(" ","_")
                var colName = strformat(this.nameVal.getValue().trim().toLowerCase());
                this.fireEvent('success',colName, this.label.getValue(), this.xtypeCombo.getValue(), combogridconfig);
                this.nameVal.reset();this.label.reset();this.xtypeCombo.reset();
                this.specialPanel.setHeight(0);this.setHeight(250);
            }
        },
        scope: this
    },{
        text: WtfGlobal.getLocaleText("crm.CLOSE"),//,
        handler: function(){
           this.close();
        },
        scope: this
    }];
    Wtf.newGridFieldWindow.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.newGridFieldWindow, Wtf.Window, {
    initComponent: function() {
        Wtf.newGridFieldWindow.superclass.initComponent.call(this);
        this.addEvents({
            "success": true
        });
    },

    onRender: function(config){
        Wtf.newGridFieldWindow.superclass.onRender.call(this, config);
        this.typeStore1 = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, ["moduleid","modulename", "mastertype", "tablename"]),
            autoLoad : false,
            url : Wtf.req.mbuild+'form.do',
            baseParams : {
                action : 7,
                reportFlag : true
            }
        });
        this.qType1 = new Wtf.form.ComboBox({
            valueField: 'moduleid',
            displayField: 'modulename',
            store: this.typeStore1,
            fieldLabel: 'Config table name',
            editable: false,
          //  allowBlank: false,
            anchor: '95%',
            mode: 'remote',
            triggerAction: 'all',
            selectOnFocus: true,
            typeAhead:true,
            emptyText: 'Select config table name...'
        });

        this.xtypeStore = new Wtf.data.SimpleStore({
            fields: ['id', 'name'],
            data :
             [
                ['None', 'None'],
                ['Text', 'Text'],
                ['Number(Integer)', 'Number(Integer)'],
                ['Number(Float)', 'Number(Float)'],
                ['Date', 'Date'],
                ['Checkbox', 'Checkbox'],
                ['Combobox', 'Combobox']
            ]
        });

        this.xtypeCombo = new Wtf.form.ComboBox({
            fieldLabel:'Editor(xtype)*',
            anchor:'93%',
            mode:'local',
            triggerAction:'all',
            typeAhead:true,
            editable:false,
            store:this.xtypeStore,
            displayField:'name',
            valueField:'id',
            allowBlank: false
        }),

//        this.otherRec = Wtf.data.Record.create([
////            {name: 'id'},
//            {name: 'name'}, {name: 'deleteField'}
//            ]);
//        this.otherReader = new Wtf.data.JsonReader({
//            root: "data"
//        }, this.otherRec);
//
//        this.otherStore = new Wtf.data.Store({
//            root: 'data',
//            reader: this.otherReader
//        });
//
//        this.otherCM = new Wtf.grid.ColumnModel([
////           {
////                header: "Id",
////                dataIndex: 'id',
////                hidden : true
////           },
//           {
//                header: "Record",
//                dataIndex: 'name',
//                editor:this.recordText = new Wtf.form.TextField({})
//           },{
//                header: "Delete",
//                dataIndex: 'deleteField',
//                renderer: function() {
//                      return "<img id='DeleteImg' class='delete' src='images/Cancel.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete task'></img>";
//                }
//           }]);
//
//
//        this.otherGrid = new Wtf.grid.EditorGridPanel({
//                ds: this.otherStore,
//                cm: this.otherCM,
//                autoScroll: true,
//                collapsible: true,
//                layout :"fit",
//                clicksToEdit: 1,
//                viewConfig: {
//                    forceFit: true,
//                    autoFill: true
//                }
//        });

        this.add({
            border: false,
            layout : 'border',
            items :[{
                region: 'north',
                border:false,
                height:75,
                cls:'windowHeader',
                html: getHeader('images/createuser.png', 'Add Field', 'Add new field to the grid')
            },{
                region: 'center',
                autoScroll: true,
                bodyStyle : 'background:#f1f1f1;font-size:10px;',
                border:false,
                items: [this.fieldformPanel = new Wtf.form.FormPanel({
                    border:false,
                    layout:'form',
                    bodyStyle:'padding:13px 13px 13px 13px',
                    labelWidth:122,
                    items: [
                        this.nameVal = new Wtf.form.TextField({
                            fieldLabel:'Field Name*',
                            anchor:'93%',
                            allowBlank: false,
                            regex:columnNameRegex,
                            regexText : regexText,
                            maxLength:20
                        }),
                        this.label = new Wtf.form.TextField({
                            fieldLabel:'Field Label*',
                            anchor:'93%',
                            allowBlank: false,
                            regex:fieldLabelRegex,
                            regexText : regexText,
                            maxLength:20
                        }),
                        this.xtypeCombo,
                        this.specialPanel = new Wtf.Panel({
                            height:0,
                            layout:'form',
                            border:false,
                            labelWidth:122,
                            items:this.qType1
//                            items:[{
//                                region:'center',
//                                layout:'fit',
//                                border:false,
////                                items:this.otherGrid
//                            }]
                         })
                    ]
                 })]
           }],
            buttonAlign: 'center'/*,
            buttons:[{
                text: WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                handler: function() {
                    var combogridconfig = '-1';
//                    var jsondata;
                    if(this.xtypeCombo.getValue() == 'Combobox') {
                           combogridconfig = this.qType1.getValue();
//                         jsondata = Wtf.common.CreateJsonStart();
//                         var ColumnList = new Array("id", "name");
//                         for (var ii = 0; ii < this.otherStore.getCount() - 1; ii++){
//                             var ContentList = new Array((ii+1), this.otherStore.getAt(ii).data['name']);
//                             var jobj = Wtf.common.getJsonObject(ColumnList, ContentList);
//                             jsondata += jobj;
//                        }
//                        jsondata = Wtf.common.CreateJsonEnd(jsondata);
                    }
                    this.fireEvent('success', this.nameVal.getValue().trim().toLowerCase().replace(" ","_"), this.label.getValue(), this.xtypeCombo.getValue(), combogridconfig);
                    this.nameVal.reset();this.label.reset();this.xtypeCombo.reset();
                    this.specialPanel.setHeight(0);this.setHeight(250);
                },
                scope: this
            },{
                text: WtfGlobal.getLocaleText("crm.CLOSE"),//,
                handler: function(){
                   this.close();
                },
                scope: this
            }]*/
        });
//        this.otherGrid.on('afteredit',this.addBlank,this);
        this.xtypeCombo.on('select',function() {
            if(this.xtypeCombo.getValue() == 'Combobox') {
//                this.otherStore.removeAll();
//                this.otherGrid.getView().refresh();

                this.specialPanel.setHeight(30);
                this.setHeight(280);
                this.qType1.allowBlank = false;
//                this.addBlank();
            } else {
                this.specialPanel.setHeight(0);
                this.setHeight(250);
                this.qType1.allowBlank = true;
            }
        },this);

//        this.otherGrid.on('cellclick', function(gd, ri, ci, e) {
//            var event = e;
//            if(event.target.className == "delete") {
//                this.otherStore.remove(gd.getStore().getAt(ri));
//            }
//        }, this);
    }

//    addBlank: function() {
//        this.otherStore.insert(this.otherStore.getCount(), new this.otherRec({
////            id: this.otherStore.getCount(),
//            name: ''
//        }));
//    }
});
