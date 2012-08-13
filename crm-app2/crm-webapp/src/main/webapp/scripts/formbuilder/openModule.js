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

Wtf.OpenModule= function(config){
    Wtf.OpenModule.superclass.constructor.call(this,config);
    this.addEvents({
        "SaveClick": true
    });
    this.expander = new Wtf.grid.RowExpander({

    });
    this.expander.on("expand",this.onRowexpand,this);
};

Wtf.extend(Wtf.OpenModule,Wtf.Panel,{
    bodyStyle :"background-color: #ffffff;",
    layout : 'fit',
    autoScroll : true,
    border:false,
    gridArray: [],
    initComponent : function(config) {
        Wtf.OpenModule.superclass.initComponent.call(this,config);
        this.chk_box = new Wtf.grid.CheckboxSelectionModel({singleSelect:true});
        this.chk_box.on("selectionchange",this.onRowSelectChange,this);
        if(this.isFilter===undefined)
            this.isFilter = false;
    },
    onRowexpand:function(scope, record, body, rowIndex){
        var disHtml = "";
        for(var cnt = 0;cnt< this.innerGridInfo.length;cnt++){
            var obj = this.innerGridInfo[cnt];
            var header = "<span class='gridHeader'>" + obj.fieldLabel + "</span>";
            for(var fld in obj.columnArray){
                header += "<span class='headerRow'>" + obj.columnArray[fld].header + "</span>";
            }
            header += "<br><hr style='width: 80%; margin-left: 0px;'>";
            var recArr = eval('('+record.get(this.innerGridInfo[cnt].name)+')');
            for(var k=0;k<recArr.data.length;k++){
                for(var fld in obj.columnArray){
                    header += "<span class='gridRow'>"+recArr.data[k][obj.columnArray[fld].dataIndex]+"</span>";
                }
                header +="<br>";
            }

            disHtml += "<div class='expanderContainer'>" + header + "</div>";
        }

        body.innerHTML = disHtml;
    },
    onRowSelectChange : function(sm) {
        if(sm.getSelected()) {
//            this.formContainer.onGridRowSelect(sm.getSelected());
            this.selectedRec = sm.getSelected();
        }/* else {
            this.formContainer.onGridRowDeSelect();
        }*/
    },

    addItems : function(itemArray) {
        if(itemArray) {
//            var arrLen1 = parseInt(itemArray.length);
//            for(var cnt=0;cnt<itemArray.length;cnt++) {
//                this.formitems.items[arrLen1] = itemArray[cnt];
//                arrLen1++;
//            }
            this.formitems.items[2].items[0].items = itemArray;
        }
    },

    SaveBttnClick :function(btnConf, formObj, paramsArray, url) {
        var o = {};
        o['moduleid'] = formObj.moduleid;
        for(var key in paramsArray) {
            o[key] = paramsArray[key];
        }
        if(btnConf.btnObj.value =='Save') {
            if(!formObj.isHide) {
                if(formObj.formPanel.form.isValid()) {
                    if(formObj.currentBtnConf.type == 'e') {
                        formObj.formPanel.form.url = url;
                        formObj.formPanel.form.baseParams = {
                            id : formObj.selectedRec.data.id
                        };
                    }else if(formObj.currentBtnConf.type == 'n') {
                        formObj.formPanel.form.url = url;
                    }

                    formObj.formPanel.form.submit({
                        scope: formObj,
                        params:o,
                        success: function(result,action){
                            var resultObj = eval('('+action.response.responseText+')');
                            if(resultObj.success) {
                                formObj.formPanel.form.reset();
                                msgBoxShow(['Success', 'Form submitted successfully'], Wtf.MessageBox.OK);
                                formObj.fireEvent("onsuccess");
                            } else {
                                msgBoxShow(['Error', resultObj.msg], Wtf.MessageBox.OK);
                            }
                        },
                        failure: function(frm, action){
                            msgBoxShow(['Error', 'Error occured at server'], Wtf.MessageBox.OK);
                        }
                    });
                }
            }
        } else if(btnConf.btnObj.value =='Cancel') {
            if(!formObj.isHide)
                formObj.onButtonClick(formObj.currentBtnConf);
        }
    },

    updateGrid : function(response) {
        //var temp = eval('(' + response.trim() + ')');
        this.displayConf = response.displayConf;
        var obj = eval("(" + response.data.trim() + ")");
        var buttonConf = eval("(" + response.buttonConf.trim() + ")");
        this.rec =  Wtf.data.Record.create(this.getStoreFields(obj.columnheader));
        this.modstore = new Wtf.data.GroupingStore({
            baseParams: {
                action: 46,
                moduleid : this.moduleid,
                taskid : this.taskid,
                tablename:this.tablename,
                isFilter : this.isFilter,
                filtervalue :this.filtervalue,
                filterfield : this.filterfield,
                filterJson:this.filterJson
            },
            url: Wtf.req.mbuild+'form.do',
            remoteSort:true,
            reader: new Wtf.data.KwlJsonReader({
                root:"data",
                totalProperty: 'count',
                remoteGroup:true,
                remoteSort: true
            }, this.rec),
            sortInfo: {field: '', direction: 'ASC'}/*,
            groupField: 'name'//'mb_137_MkModuleXoXname']*/
        });
        //        this.dsModCenter = new Wtf.ux.MultiGroupingStore({
//            baseParams: {
//                action: 4
//            },
//            url: Wtf.req.mbuild+'form.do',
//            remoteSort:true,
//            reader:  new Wtf.data.JsonReader({
//                        root:"data",
//                        totalProperty: 'TotalCount',
//                        remoteGroup:true,
//                        remoteSort: true
//            }, this.ModCenter),
//            //  data: xg.dummyData,
////            sortInfo: {field: 'dateval', direction: 'ASC'},
//            groupField: ['dateval']
//
//        });
//   this.groupingView = new Wtf.ux.MultiGroupingView({ hideGroupedColumn :true,
//    this.groupingView = new Wtf.grid.GroupingView({ hideGroupedColumn :true,
//                forceFit: true,
//                emptyGroupText: 'All Group Fields Empty',
//                displayEmptyFields: true, //you can choose to show the group fields, even when they have no values
//                groupTextTpl: '{text} ', //({[values.rs.length]} {[values.rs.length > 1 ? "Records" : "Record"]})',
//                                displayFieldSeperator: ', ' //you can control how the display fields are seperated
//    });

         this.quickPanelSearch = new Wtf.KWLQuickSearch({
                width: 150,
                emptyText: 'Enter '+obj.columnheader[0][0],
                field: obj.columnheader[0][1]
         });
        this.buttArray =  ['Quick Search: ',this.quickPanelSearch,'-',{
            text : "Advanced Search",
            scope : this,
            tooltip:'Manage search with your preferance',
            handler : this.configurAdvancedSearch,
            iconCls : 'pwnd editicon'
        }];
//        this.permsObj = eval('('+this.permsObj+')');
//        var addperm = eval('('+this.permsObj[0][2]+')');
        var addperm = eval('('+this.permsObj[Wtf.mbuild.permActions.addRecord]+')');
        if(checktabperms(addperm.permgrid,addperm.perm)){
            this.buttArray.push(new Wtf.Toolbar.Button({
                text : "New",
                iconCls: 'newicon',
                scope : this,
                disabled: false,
                handler : function() {
                    this.displayConfFunction(0);
                }
            }));
        }
//        var updateperm = eval('('+this.permsObj[1][3]+')');
        var updateperm = eval('('+this.permsObj[Wtf.mbuild.permActions.editRecord]+')');;
        if(checktabperms(updateperm.permgrid,updateperm.perm)){
            this.buttArray.push(new Wtf.Toolbar.Button({
                text : "Edit",
                iconCls: 'editicon1',
                scope : this,
                disabled: false,
                handler : function() {
                    this.displayConfFunction(1);
                }
            }));
        }
        

        this.configInfo = eval('('+obj.stdconfig+')');
        this.pushToolbar(buttonConf.tbar, this.buttArray);
        this.buttArray = getConfigButton(this,this.configInfo,this.buttArray,this.permsObj)
        this.groupingView = new Wtf.grid.GroupingView({
  //          forceFit: true,
            showGroupName: false,
    //        enableGroupingMenu: false,
            hideGroupedColumn: false
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
        var bottomTBar = [ this.pg ];
        this.pushToolbar(buttonConf.bbar, bottomTBar);
        this.modstore.on('load', function(store) {
            this.quickPanelSearch.StorageChanged(store);
//            this.createExpanderTpl(store);
        }, this);
        this.modstore.on('datachanged', function() {
           // var p = this.pP.combo.value;
           // this.quickPanelSearch.setPage(p);
        }, this);
        this.pluginArr = new Array();
        var columnModel = this.getColModel(obj.columnheader);
        if(this.innerGridInfo.length>0){
            this.pluginArr.push(this.expander);
        }
        this.modulegrid = new Wtf.grid.GridPanel({
//            title:this.modulename+" List",
            sm:this.chk_box,
            cm: columnModel,
            id: 'moduleGr'+this.id,
            ds: this.modstore,
            border : false,
            region :'center',
            layout:'fit',
            loadMask: {
                msg: 'Loading...'
            },
            view:this.groupingView ,
            //viewConfig: {forceFit: true},
            tbar :this.buttArray,
            bbar: bottomTBar,
            plugins: this.pluginArr
        });
        this.gridPanel = this.modulegrid;
        this.modulegrid.on("render",function(){
             this.modstore.load({params:{
                  start:0,
                  limit:this.pP.combo.value
           }});
        },this)
        this.modulegrid.on('cellclick', function(gd, ri, ci, e) {
            var event = e;
            var record=gd.getStore().getAt(ri);
            if(event.target.className == "delete") {
                Wtf.MessageBox.confirm('Delete', 'Are you sure you want to delete selected record', function(btn){
                    if(btn=="yes"){
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.mbuild+'form.do',
                            method:'POST',
                            params: {
                                action: 24,
                                id:record.data.id,
                                moduleid : this.moduleid
                            }
                        },
                        this,
                        function(resp) {
//                            var resultObj = eval('('+ resp +')');
                            if(resp.success) {
                                msgBoxShow(['Info', "Record deleted successfully"], Wtf.MessageBox.OK);
                                if(this.modstore.getCount() == 1){
                                    this.modstore.removeAll();
                                }
                                this.modstore.load({
                                    params:{
                                        start:this.pg.cursor,
                                        limit:this.pP.combo.value
                                    }
                                });
                            } else {
                                msgBoxShow(['Info', "Record not deleted successfully"], Wtf.MessageBox.Error);
                            }
                        },
                        function() {
                            msgBoxShow(['Info', "Error occured at server"], Wtf.MessageBox.Error);
                        }
                        );

                    }
                },this);
            } else if(event.target.className == "opentab") {
              var recData = record.data;
              var cellDataIndex=gd.getColumnModel().config[ci].dataIndex;
              var modid = gd.store.baseParams.moduleid;
              var tskid = this.taskid;
               Wtf.Ajax.requestEx({
                    url: Wtf.req.mbuild+'form.do',
                    method: 'POST',
                    params: ({
                        action : 25,
                        moduleid: modid,
                        taskid: tskid,
                        recordid : recData.id,
                        reportid : modid,
                        basemode : 0
                    })},
                    this,
                    function(result, req){
                        var nodeobj = eval("(" + result.trim() + ")");
                        if(nodeobj.success) {
                            var formitems = URLDecode(nodeobj.data[0].jsondata);
                            formitems = Wtf.decode(formitems);
                            this.addSubTabs(modid,recData,nodeobj.subtabs,formitems,cellDataIndex, tskid);
                        } else {
                            msgBoxShow(4,1);
                        }
                    },
                    function(){
                        msgBoxShow(4,1);
               });
            } else if(event.target.className == "showComments") {
                this.showComments(record.data.id);
            } else if(event.target.className == "showDocuments") {
                this.showDocuments(record.data.id);
            }
        }, this);
    },
    
    displayConfFunction : function(btnType) {
        if(btnType==1 && this.chk_box.getCount()==0) {
            msgBoxShow(["Message","Please select record to edit."],0);
            return;
        }
        var activeObjId = null;
        var activeObj = null;
        if(btnType==1) {
            this.selectedRec = this.chk_box.getSelected();
            activeObjId = this.moduleid + this.selectedRec.data.id +'editformContainer';
            activeObj = Wtf.getCmp(activeObjId);
        }
        else {
            activeObjId = this.moduleid + 'addformContainer';
            activeObj = Wtf.getCmp(this.moduleid + 'addformContainer');
        }    

        var winobj = null;
        if(!activeObj) {
            activeObj = new Wtf.form.TabFormPanel({
                id : activeObjId,
                itemArr : this.formitems,
                moduleid : this.moduleid,
                taskid: this.taskid,
                itemid : btnType==1 ? this.selectedRec.data.id : null,
                selectedRec : btnType==1 ? this.selectedRec : null,
                closable : true,
                editFlag : btnType==1 ? true : false
            });
            if(this.displayConf==2) {
                activeObj['title'] = btnType==1 ? "Update "+ this.modulename +" Details" : "Add "+ this.modulename +" Details";
            }
            if(this.displayConf==1) {
                winobj = new Wtf.Window({
                    layout:'fit',
                    iconCls: 'winicon',
                    modal: true,
                    title : btnType==1 ? "Update "+ this.modulename +" Details" : "Add "+ this.modulename +" Details",
                    height:400,
                    width:850,
                    scope: this,
                    resizable :false,
                    items : activeObj
                });
                winobj.show();
                winobj.setHeight(activeObj.formPanel.getSize().height+35);
                winobj.setWidth(activeObj.formPanel.getSize().width+50);
                activeObj.on('onsuccess', function(){
                    this.modstore.reload();
                }, this);
            } else {
                mainPanel.add(activeObj);
            }
                activeObj.on('onsuccess', function(){
                if(this.refreshColumns===true){
                    Wtf.Ajax.requestEx({
                        url: Wtf.req.mbuild+'form.do',
                        method:'post',
                        params: {
                            action : 8,
                            moduleid : this.moduleid
                        }
                        },
                        this,
                        function(resp, responseText) {
                            var columnModel =this.getColModel(eval("(" + resp.data.trim() + ")").columnheader);
                            this.modulegrid.reconfigure(this.modulegrid.getStore(),columnModel);
                    this.modstore.reload();
                        }
                    );

                }else
                    this.modstore.reload();
                }, this);
            }
        if(this.displayConf!=1) {
            mainPanel.setActiveTab(activeObj.id);
            mainPanel.doLayout();
        }
    },
    
    pushToolbar: function(tbar, buttArray){
        if(tbar !== undefined){
            for(var cnt = 0; cnt < tbar.length; cnt++){
                if(checktabperms(tbar[cnt].permgrid,tbar[cnt].perm)) {
                    var handler;
                    if(tbar[cnt].type == "js"){
                        buttArray.push({
                            text: tbar[cnt].text,
                            scope: this,
                            handler: eval("(" + tbar[cnt].functext + ")")
                        });
                    }
                    else{
                        buttArray.push({
                            text: tbar[cnt].text,
                            scope: this,
                            url: tbar[cnt].functext,
                            handler: makeJspRequest
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
            this.modstore.reload({
                params: {
                    start:0,
                    limit:this.pP.combo.value
                }
                });
        },this);
        mainPanel.add(commentsComponentObj);
        mainPanel.setActiveTab(commentsComponentObj.id);
        mainPanel.doLayout();
    },
    showDocuments:function(recordId){
        openDocListWin(this.moduleid,recordId,this.permsObj);
    },
//   createExpanderTpl: function(store){
//        this.tplFunction = {};
//        var tpl = this.getTemplateDom([this.formitems]);
//        if(tpl != ""){
//            tpl = "<div class='expanderContainer'>" + tpl + "</div>";
//            tpl = new Wtf.XTemplate(tpl/*,{
//                getValue: function(value){
//                    return "value";
//                }
//            }*/);
//            tpl.valueMap = this.tplFunction;
//            this.expander.setTemplate(tpl);
//        }
//    },

//    getTemplateDom: function(obj){
//        var templateConf = "";
//        if(obj != undefined){
//            for(var z = 0; z < obj.length; z++){
//                if(obj[z].xtype == "WtfGridPanel" || obj[z].xtype == "WtfEditorGridPanel"){
//                    templateConf += this.getHeaderRow(obj[z]);
//                    templateConf += this.getTemplateFromRecord(obj[z]);
//                    templateConf += "</tpl>";
//                }
//                if(obj[z].items !== undefined){
//                    templateConf += this.getTemplateDom(obj[z].items);
//                }
//            }
//        }
//        return templateConf;
//    },

//    getHeaderRow: function(obj){
//        var columnArray = obj.columnArray;
//        var header = "<span class='gridHeader'>" + obj.fieldLabel + "</span>";
//        for(var fld in columnArray){
//            header += "<span class='headerRow'>" + columnArray[fld].header + "</span>";
//        }
//        return header + "<br><hr style='width: 80%; margin-left: 0px;'><tpl for=\"" + obj.name + "\">";
//    },
//
//    getTemplateFromRecord: function(obj){
//        var columnArray = obj.columnArray;
//        var tpl = "";
//        for(var fld in columnArray){
////            tpl += "<span><tpl this.getValue({" + columnArray[fld].dataIndex + "})></tpl></span>";
//            tpl += "<span><tpl this.getValue(" + obj.name + ")></tpl></span>";
//            this.tplFunction[columnArray[fld].dataIndex] = obj.name;
//        }
//        if(tpl != "")
//            tpl = "<div>" + tpl + "</div>";
//        return tpl;
//    },

    addSubTabs : function(modid,recData,subtabs,formitems,cellDataIndex, taskid) {
        var formContainer = new Wtf.form.TabFormPanel({
            id : recData.id + 'formContainer',
            itemArr : formitems,
            moduleid : modid,
            taskid: taskid,
            title : recData[cellDataIndex] + " Details",
            itemid : recData.id,
            recData : recData,
            editFlag : true
        });
        var itemsArray = [];
        itemsArray.push(formContainer);
        var recIdForTab= "subtabpanelcom" + recData.id;
         var tabPanel = new Wtf.TabPanel({
            id: recIdForTab,
            border: false,
            activeTab : 0,
            title : recData[cellDataIndex],
            enableTabScroll: true,
            closable : true,
            items: itemsArray
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
                         cmpId:'Module_'+recData.id+"_"+ subtabs[cnt].moduleid,
                         moduleName:subtabs[cnt].name,
                         moduleId:subtabs[cnt].moduleid,
                         containerId:recIdForTab,
                         permsObj : nodeobj.recordperm,
                         isFilter:true,
                         filterfield: subtabs[cnt].refcolumn,
                         filterValue: recData.id
                     };
                    openGridModule(configFields);
                } else {
                    configFields={ismodule:false,
                         data:'',
                         cmpId:'Report' + subtabs[cnt].moduleid,
                         moduleName:subtabs[cnt].name,
                         moduleId:subtabs[cnt].moduleid,
                         containerId:recIdForTab,
                         permsObj : nodeobj.recordperm,
                         isFilter:true,
                         filterfield: subtabs[cnt].refcolumn,
                         filterValue:recData.id
                     };
                    openGridModule(configFields);
                }
            }
        }
    },

    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.doLayout();
        this.objsearchComponent.columnStore.loadData(this.fields);
    },

    getInnerGridInfo: function(obj){
        if(obj != undefined){
            for(var z = 0; z < obj.length; z++){
                if(obj[z].xtype == "WtfGridPanel" || obj[z].xtype == "WtfEditorGridPanel"){
                    this.innerGridInfo.push(obj[z]);
                }
                if(obj[z].items !== undefined){
                    this.getInnerGridInfo(obj[z].items);
                }
            }
        }
    },

    getStoreFields:function(columnheader) {
        var fields = [];
        this.innerGridInfo = new Array();
        var gridFlag = false;
        for(var fieldcnt = 0; fieldcnt < columnheader.length; fieldcnt++) {
            if((columnheader[fieldcnt].conftype == "WtfGridPanel" || columnheader[fieldcnt].conftype == "WtfEditorGridPanel") && !gridFlag){
                this.gridArray[this.gridArray.length] = columnheader[fieldcnt]["1"];
                this.getInnerGridInfo([this.formitems]);
                gridFlag = true;
            }
            var fObj = {};
            fObj['name'] = columnheader[fieldcnt][1];
            fObj['type'] = 'string';
            fields[fields.length] = fObj;
       }
        return fields;
    },

    getColModel : function (columnHeader) {
        this.comboStoreArray = {};
        this.createComboStore(this.formitems.items);
        this.fields = [];
        var colConfig = [];
        colConfig[colConfig.length] = new Wtf.grid.RowNumberer();
        colConfig[colConfig.length] = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:true
        });
        if(this.innerGridInfo.length>0){
           colConfig[colConfig.length] = this.expander;
        }

        var summaryPluginAdded=false;
        for(var columncnt =0; columncnt<columnHeader.length ; columncnt++) {
            if(!columnHeader[columncnt][3]){
                if(columnHeader[columncnt].conftype != "WtfGridPanel" && columnHeader[columncnt].conftype != "WtfEditorGridPanel"){
                    var colObj = {};
                    var colDataIndex = columnHeader[columncnt][1];
                    colObj['header'] = columnHeader[columncnt][0];
    //                if(colObj['header']=='name' || colObj['header']=='Name') {
    //                    colObj['scope'] = this;
    //                    colObj['renderer'] = function(val, css, record, row, column, store) {
    //                        return "<a href = '#' class ='opentab' > " + val + "</a>";
    //                    }
    //                }
                    colObj['dataIndex'] = colDataIndex;
                    colObj['hidden']= columnHeader[columncnt][3];
                    colObj['groupable'] = columnHeader[columncnt][4];
                    colObj['sortable'] = columnHeader[columncnt][5];
                    colObj['xtype'] = columnHeader[columncnt].conftype;

                    if(columnHeader[columncnt].conftype == "combo"){
                        var comboStore = this.comboStoreArray[colDataIndex];
                        var valueField = "id";
                        var displayField = "name";

                        colObj['renderer'] = Wtf.ux.comboBoxRendererStore(comboStore, valueField, displayField);
                    }else if(columnHeader[columncnt].conftype == "select" ){
                        comboStore = this.comboStoreArray[colDataIndex];
                        valueField = "id";
                        displayField = "name";

                        colObj['renderer'] = WtfGlobal.getSelectComboRendererStore(comboStore, valueField, displayField);
                    }

                    colConfig[colConfig.length] = colObj;
                    if(colObj['xtype']==='file'){
                        this.refreshColumns=true;
                        var store = new Wtf.data.JsonStore({
                            fields: ['docid', 'docpath','docname'],
                            data :  columnHeader[columncnt].fileinfo
                        });
                        colObj['renderer'] = function(v,s){
                            var idx = s.find('docid',new RegExp('^'+v+'$'));
                            if(idx>=0){
                                var rec = s.getAt(idx);
                                return '<a href="fileDownload.jsp?url='+rec.get('docpath')+ "&docid=" + rec.get('docid') + "&attachment=true"+'" class="jumplink">'+rec.get('docname')+'</a>';
                            }else
                                return '';
                        }.createDelegate(this,[store],1);
                    }
                    if (columnHeader[columncnt][3] == false && colObj['dataIndex'] !="comments" && colObj['dataIndex'] !="docs_id"){
                        this.fields.push(colObj);
                    }
                }
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
                        this.pluginArr.push(this.summary);
                        summaryPluginAdded=true;
                    }
                    colObj['summaryType']= columnHeader[columncnt][7];
                    colObj['summaryRenderer']= this['total'].createDelegate(this);
                }
            }
        }
//        var deleteperm = eval('('+this.permsObj[2][4]+')');
        var deleteperm = eval('('+this.permsObj[Wtf.mbuild.permActions.deleteRecord]+')');
        if(checktabperms(deleteperm.permgrid,deleteperm.perm)) {
            colObj = {};
            colObj['header'] = "Delete";
            colObj['sortable'] = false;
            colObj['dataIndex'] = "delField";
            colObj['renderer'] = function(val) {
                  return "<img id='DeleteImg' class='delete' src='images/Cancel.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete record'></img>";
            };
            colConfig[colConfig.length] = colObj;
        }
        var reportcm = new Wtf.grid.ColumnModel(colConfig);
        return reportcm;
    },

    createComboStore:function(objarr) {
        var itemsa = objarr;
        if(itemsa) {
            for(var cnt=0;cnt<itemsa.length;cnt++) {
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
                    this.createComboStore(itemsa[cnt].items);
                } else {
                    if(currXType == 'combo' || currXType == 'select') {
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

//                               if(cmb.childComboId) {//For cascading
//                                   for(var cnt1=0;cnt1<cmb.childComboId.length;cnt1++) {
//                                       var childObjId = cmb.childComboId[cnt1];
//                                       var nameValue = cmb.name;
//                                        Wtf.getCmp(childObjId).clearValue();
//                                        Wtf.getCmp(childObjId).store.removeAll();
//                                        if(Wtf.getCmp(childObjId).parentComboId === cmb.id) {
//                                            Wtf.getCmp(childObjId).store.load({
//                                                params: {
//                                                    'cascadeCombo': 1,
//                                                    'cascadeComboName': Wtf.getCmp(childObjId).casColName,
//                                                    'cascadeComboRefName': nameValue,
//                                                    'cascadeComboValue': cmb.getValue()
//                                                }
//                                            });
//                                            Wtf.getCmp(childObjId).enable();
//                                        } else {
//                                            Wtf.getCmp(childObjId).disable();
//                                        }
//                                   }
//                               }
//
//                               if(cmb.childFieldId) {//For read only fields
//                                    Wtf.Ajax.requestEx({
//                                        url: Wtf.req.mbuild+'form.do',
//                                        method:'POST',
//                                        params: {
//                                            action: 33,
//                                            moduleid: cmb.moduleid,
//                                            appenid:cmb.appenID,
//                                            refTName: cmb.name,
//                                            comboValueId: cmb.getValue()
//                                        }},
//                                        this,
//                                        function(resp) {
//                                            var obj = eval('(' + resp + ')');
//                                            for(cnt1=0;cnt1<cmb.childFieldId.length;cnt1++) {
//                                               childObjId = cmb.childFieldId[cnt1];
//                                               Wtf.getCmp(childObjId).setValue(obj[childObjId]);
//                                            }
//                                        }
//                                    );
//                               }
                                if (assignedL !=undefined){
                                    if(assignedL["select"] !== undefined){
                                        assignedL["select"].call(this, cmb, rec,idx);
                                    }
                                }
                            }
                        }
//                        if(itemsa[cnt].parentComboId) {
//                            itemsa[cnt]['mode'] = 'local';
//                        }else {
                            itemsa[cnt]['mode'] = 'remote';
//                        }
                        this.comboStoreArray[itemsa[cnt]['name']] = obj;
                        obj.load();
                        itemsa[cnt]['store'] = obj;
                        itemsa[cnt]['displayField'] = 'name';
                        itemsa[cnt]['valueField'] = 'id';
                        itemsa[cnt]['triggerAction'] = 'all';
                        if(this.isFilter==true) {
                            itemsa[cnt]['mode'] = 'local';
                        }
                    }
                }
            }
        }
    },

    total : function(val) {
        return val ;
    },
    renderItalic: function(data, metadata, record, rowIndex, columnIndex, store){
            return '<i>' + data + '</i>';
    },

    detailviewRender : function(val) {
        return "<a href = '#' class ='opentab' > " + val + "</a>";
    },

    DisplayTab : function() {
        this.fileupload = false;
        if(this.formitems.id){
            this.formitems.id += this.id;
        }
//        this.createComboStore(this.formitems.items);
//        var btnType = [];
//        var addperm = eval('('+this.permsObj[0][2]+')');
//        if(checktabperms(addperm.permgrid,addperm.perm)){
//            btnType.push('n');
//        }
//        var updateperm = eval('('+this.permsObj[1][3]+')');
//        if(checktabperms(updateperm.permgrid,updateperm.perm)){
//            btnType.push('e');
//        }
//
//        this.formContainer = new Wtf.form.formContainerPanel({
//            pid : this.id,
//            id : this.id + 'formContainer',
//            btnType : btnType,
//            localFlg : true, //Need to set false for implement
//            itemObj : this.formitems,
//            fileupload : this.fileupload,
//            moduleid : this.moduleid
//        });
//        this.formContainer.on('onsuccess', function(){
//            //this.getModuleData();
//            this.modstore.reload();
//        }, this);
//
//        this.formContainer.on('SaveClick', function(btnConf, formObj) {
//            this.fireEvent('SaveClick', btnConf, formObj);
//        }, this);
        this.objsearchComponent=new Wtf.mbuild.advancedSearchComponent({
            moduleid:this.moduleid,
            getComboDataAction: 21,
            isReport:false
        });
        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);

        this.innerPanel = new Wtf.Panel({
            layout : 'border',
            bodyStyle :"background-color:transparent;",
            border:false,
            items : [/*this.formContainer,*/{
                    region :'center',
                    layout :'border',
                    autoScroll: true,
                    baseCls: "tempClass123",
                    border :false,
                    items:[this.modulegrid,this.objsearchComponent]
            }]
        });
        this.add(this.innerPanel);
        this.doLayout();
    },

    onRender : function(config) {
        Wtf.OpenModule.superclass.onRender.call(this,config);
        this.getModuleData();
    },

    getModuleData : function() {
        Wtf.Ajax.requestEx({
            url: Wtf.req.mbuild+'form.do',
            method:'post',
            params: {
                action : 8,
                moduleid : this.moduleid
            }
            },
            this,
            function(resp, responseText) {                
                if(this.modulegrid) {
                    var obj = resp;
                    this.modstore.loadData(obj.data);
                } else {
                    this.updateGrid(resp);
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
                action: 47,//10,
                moduleid : this.moduleid,
                tablename:this.tablename,
                taskid : this.taskid,
                isFilter : this.isFilter,
                filtervalue :this.filtervalue,
                filterfield : this.filterfield,
                filterJson:this.filterJson
        };
          this.modstore.removeAll();
        this.modstore.reload({params: {
                start:0,
                limit:this.pP.combo.value
        }});
    },

    clearStoreFilter:function(){
        this.modstore.baseParams= {
                action: 47,//10,
                moduleid : this.moduleid,
                tablename:this.tablename,
                taskid : this.taskid,
                isFilter : this.isFilter,
                filtervalue :this.filtervalue,
                filterfield : this.filterfield,
                filterJson:''
        };
        this.objsearchComponent.hide();
        this.doLayout();
        this.modstore.reload({params: {
                start:0,
                limit:this.pP.combo.value,
                filterJson:''
        }});
    }
});

