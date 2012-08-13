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

Wtf.importMenuArray = function(obj,moduleName,store,extraParams,extraConfig) {
    var archArray = [];
    var importButton = new Wtf.Action({
        text:WtfGlobal.getLocaleText("crm.common.importcsvfile"),// "Import CSV File",
        id:'importcsvfile'+obj.id,
        tooltip:{
            text:WtfGlobal.getLocaleText("crm.common.importcsvfile")//'Click to import CSV file.'
        },
        iconCls: 'pwnd importTabIcon',
        scope: obj,
        handler:function(){
            if(dojoInitCount <= 0) {
                dojo.cometd.init("../../bind");
                dojoInitCount++;
            }
            var impWin1 = Wtf.commonFileImportWindow(obj, moduleName, store, extraParams, extraConfig);
            impWin1.show();
        }
    });
    archArray.push(importButton);

    var importXLS=new Wtf.Action({
        text: WtfGlobal.getLocaleText("crm.common.importxlsfile"),//"Import XLS File",
        tooltip:{
            text:WtfGlobal.getLocaleText("crm.common.importxlsfile.ttip")//'Click to import XLS file.'
        },
        iconCls: 'pwnd importTabIcon',
        scope: obj,
        handler:function(){
            if(dojoInitCount <= 0) {
                dojo.cometd.init("../../bind");
                dojoInitCount++;
            }
            var impWin1 = Wtf.xlsCommonFileImportWindow(obj,moduleName,store,extraParams, extraConfig);
            impWin1.show();
        }
    });
    archArray.push(importXLS);

     if(moduleName!="Target" && moduleName!= "Account" && moduleName!= "Opportunity" && moduleName!= "Product" && moduleName!= "Calibration" ) {
        var importGoogleContacts=new Wtf.Action({
            text: WtfGlobal.getLocaleText({key:"crm.common.importfromgoogleaccount",params:[moduleName]}),//"Import "+moduleName+"s from Google Account",
            tooltip:{
                text:WtfGlobal.getLocaleText({key:"crm.common.importfromgoogleaccount.ttip",params:[moduleName]})//'Click to import '+moduleName+'s from Google Account.'
            },
            iconCls: "pwnd importTabIcon",
            scope: obj,
            handler:function(){
                Wtf.commonAuthenticationWindow(obj,store,moduleName,obj.mapid);
            }
        });
        archArray.push(importGoogleContacts);
    }
    return archArray;
}

Wtf.importMenuButtonA = function(menuArray,obj,modName) {
    var tbarArchive=new Wtf.Toolbar.Button({
        iconCls: 'pwnd importicon',
        tooltip: {
    	text:WtfGlobal.getLocaleText({key:"crm.common.importBTN.details",params:[WtfGlobal.getLocaleText("crm."+modName.toUpperCase())]})// "Import "+((modName=="Opportunities")?modName:(modName+"(s)"))+" details"
        },
        scope: obj,
        text:WtfGlobal.getLocaleText("crm.IMPORTBUTTON"),//"Import",
        menu: menuArray
    });
    return tbarArchive;
}

/*-------------------- Function to show Mapping Windows -----------------*/

Wtf.callMappingInterface = function(mappingParams, prevWindow){ 
    var mappingWindow = Wtf.getCmp("csvMappingInterface");
    if(!mappingWindow) {
        this.mapCSV=new Wtf.csvFileMappingInterface({
            csvheaders:mappingParams.csvheaders,
            modName:mappingParams.modName,
            moduleid:mappingParams.moduleid,
            customColAddFlag:mappingParams.customColAddFlag,
            typeXLSFile:mappingParams.typeXLSFile,
            impWin1:prevWindow,
            delimiterType:mappingParams.delimiterType,
            index:mappingParams.index,
            moduleName:mappingParams.moduleName,
            store:mappingParams.store,
//            contactmapid:this.contactmapid,
//            targetlistPagingLimit:this.targetlistPagingLimit,
            scopeobj:mappingParams.scopeobj,
            cm:mappingParams.cm,
            extraParams:mappingParams.extraParams,
            extraConfig:mappingParams.extraConfig
        }).show();
    } else {
        mappingWindow.impWin1= prevWindow,
        mappingWindow.csvheaders= mappingParams.csvheaders,
        mappingWindow.index= mappingParams.index,
        mappingWindow.extraParams= mappingParams.extraParams,
        mappingWindow.extraConfig= mappingParams.extraConfig
        mappingWindow.show();
    }

    if(mappingParams.typeXLSFile){ //.XLS File Import 
        Wtf.getCmp("csvMappingInterface").on('importfn', function(mappingJSON, index, moduleName, store, scopeobj, extraParams, extraConfig){
            if(extraConfig == undefined) {
                extraConfig={};
            }
            extraConfig['filepath'] = Wtf.getCmp("importxls").xlsfilename;
            extraConfig['onlyfilename'] = Wtf.getCmp("importxls").onlyfilename;
            extraConfig['filename'] = Wtf.getCmp("importxls").onlyfilename;
            extraConfig['sheetindex'] = index;
            extraConfig['moduleName'] = moduleName;
            extraConfig['modName'] = moduleName;
            extraConfig['extraParams'] = extraParams;
            extraConfig['resjson'] = mappingJSON;
            Wtf.ValidateFileRecords(true, moduleName, store, scopeobj, extraParams, extraConfig);
        },this);
    } else { //.CSV File Import
        Wtf.getCmp("csvMappingInterface").on('importfn', function(resMapping, delimiterType, moduleName, store, scopObj, extraParams, extraConfig){
            if(extraConfig == undefined) {
                extraConfig={};
            }
            extraConfig['resjson'] = resMapping;
            extraConfig['modName'] = moduleName;
            extraConfig['delimiterType'] = delimiterType;
            extraConfig['extraParams'] = extraParams;
            Wtf.ValidateFileRecords(false, moduleName, store, scopObj, extraParams, extraConfig);
        },this);
    }
}
/**********************************************************************************************************
 *                              Mapping Window
 **********************************************************************************************************/
Wtf.csvFileMappingInterface = function(config) {
    Wtf.apply(this, config);
    Wtf.csvFileMappingInterface.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.csvFileMappingInterface, Wtf.Window, {
    iconCls : 'importIcon',
    width:750,
    height:570,
    modal:true,
    layout:"fit",
    id:'csvMappingInterface',
    closable:false,
    initComponent: function() {
        Wtf.csvFileMappingInterface.superclass.initComponent.call(this);
    },

    onRender: function(config){
        Wtf.csvFileMappingInterface.superclass.onRender.call(this, config);
        this.addEvents({
            'importfn':true,
            'customColAdd':true
        });
        //"Map XLS headers" : "Map CSV headers";
        this.title=this.typeXLSFile? WtfGlobal.getLocaleText("crm.common.import.mapxlsheaders"):WtfGlobal.getLocaleText("crm.common.import.mapcsvheaders");
        this.mappingJSON = "";
        this.masterItemFields = "";
        this.moduleRefFields = "";
        this.unMappedColumns = "";
        this.isMappingModified = "";

        this.columnRec = new Wtf.data.Record.create([
            {name: 'id'},
            {name: 'configid'},
            {name: 'validatetype'},
            {name: 'customflag'},
            {name: 'columnName'},
            {name: 'pojoName'},
            {name: 'isMandatory'}
        ]);
        this.columnDs = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                totalProperty:'count',
                root: "data"
            },this.columnRec),
            sortInfo: {field: "isMandatory", direction:"DESC"},//Move all mandatory columns an top
            url: Wtf.req.springBase+"common/ImportRecords/getColumnConfig.do"
        });

        this.columnCm = new Wtf.grid.ColumnModel([
        {
            header: WtfGlobal.getLocaleText("crm.common.import.columns"),//"Columns",
            dataIndex: "columnName",
            renderer:function(a,b,c){
                var qtip="";var style="";
                if(c.get("isMandatory")){
                    style += "font-weight:bold;color:#500;";
                    qtip += WtfGlobal.getLocaleText("crm.common.mandatoryfield");//"Mandatory Field";
                }
                return "<span wtf:qtip='"+qtip+"' style='cursor:pointer;"+style+"'>"+a+"</span>";
            }
        }
        ]);
        this.quickSearchTF1 = new Wtf.KWLQuickSearchUseFilter({
            id : 'tableColumn'+this.id,
            width: 140,
            field : "columnName",
            emptyText:"Search Table Column "
        });
        this.tableColumnGrid = new Wtf.grid.GridPanel({
            ddGroup:"mapColumn",
            enableDragDrop : true,
            store: this.columnDs,
            sm:new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            cm: this.columnCm,
            height:370,
            border : false,
            tbar:[this.quickSearchTF1],
            loadMask : true,
            view:new Wtf.grid.GridView({
                forceFit:true
//                emptyText:"Select module to list columns"
            })
        });

        this.columnDs.on("load",function() {
            if(this.mappedColsDs.getCount()>0){
                for(var i=0; i<this.mappedColsDs.getCount(); i++){
                    for(var j=0; j<this.columnDs.getCount(); j++){
                        if(this.mappedColsDs.getAt(i).get("id")==this.columnDs.getAt(j).get("id")){
                            this.columnDs.remove(this.columnDs.getAt(j));
                            break;
                        }
                    }
                }
            }
            this.quickSearchTF1.StorageChanged(this.columnDs);
        },this);

        //Mapped Columns Grid
        this.mappedColsData="";
        this.mappedRecord = new Wtf.data.Record.create([
            {name: 'id'},
            {name: 'configid'},
            {name: 'validatetype'},
            {name: 'customflag'},
            {
                name: "columnName", type: 'string'
            },
            {
                name: "pojoName", type: 'string'
            },
            {
                name: "isMandatory"
            }
        ]);

        this.mappedColsDs = new Wtf.data.JsonStore({
            jsonData : this.mappedColsData,
            reader: new Wtf.data.JsonReader({
                root:"data"
            }, this.mappedRecord)

        });

        var mappedColsCm = new Wtf.grid.ColumnModel([{
            header: WtfGlobal.getLocaleText("crm.common.mappedcolumns"),//"Mapped Columns",
            dataIndex: 'columnName'
        }]);

        this.mappedColsGrid= new Wtf.grid.GridPanel({
            ddGroup:"restoreColumn",
            enableDragDrop : true,
            sm:new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            store: this.mappedColsDs,
            cm: mappedColsCm,
            height:370,
            border : false,
            tbar:[{xtype:'panel',height:10,border:false}],
            loadMask : true,
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.common.dragndropcolsmsg")//"Drag and Drop columns here"
            })
        });
        this.quickSearchTF = new Wtf.KWLQuickSearchUseFilter({
            id : 'csvHeader'+this.id,
            width: 140,
            field : "header",
            emptyText:"Search "+(this.typeXLSFile?"xls":"csv")+" Headers "
        });
        // CSV header from csv file Grid
        this.csvHeaderDs = new Wtf.data.JsonStore({
            fields: [{
                name:"header"
            },{
                name:"index"
            },{
                name:"isMapped"
            }],
            sortInfo: {field: "header", direction:"ASC"}
        });
        //loadHeaderData
        this.csvHeaderDs.on("datachanged",function(){
            this.totalHeaders=this.csvHeaderDs.getCount();
        },this);
        this.tempFileHeaderDs = new Wtf.data.JsonStore({ //Copy of header store used for auto mapping
            fields: [{
                name:"header"
            },{
                name:"index"
            }],
            sortInfo: {field: "header", direction:"ASC"}
        });
        var headerName = WtfGlobal.getLocaleText("crm.common.import.csvheaders");//"CSV Headers";
        var emptyGridText =  WtfGlobal.getLocaleText("crm.common.import.csvheaders.mtygridtxt");//"CSV Headers from given CSV file";
        if(this.typeXLSFile){
            headerName= WtfGlobal.getLocaleText("crm.common.import.xlsheaders");//"XLS Headers"
            emptyGridText = WtfGlobal.getLocaleText("crm.common.import.xlsheaders.mtygridtext");//"XLS Headers from given XLS file";
        }
        var csvHeaderCm = new Wtf.grid.ColumnModel([{
            header: headerName,
            dataIndex: 'header'
//            renderer:function(a,b,c){
//                var qtip="";var style="";
//                if(c.data.isMapped){
//                    style += "color:GRAY;";
//                    qtip += "";
//                }
//                return "<span wtf:qtip='"+qtip+"' style='cursor:pointer;"+style+"'>"+a+"</span>";
//            }
        }]);
        this.csvHeaderGrid= new Wtf.grid.GridPanel({
            ddGroup:"mapHeader",
            enableDragDrop : true,
            sm:new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            height:370,
            store: this.csvHeaderDs,
            cm: csvHeaderCm,
            border : false,
            loadMask : true,
             tbar:[this.quickSearchTF],
            view:new Wtf.grid.GridView({
                forceFit:true
//                emptyText:emptyGridText
            })
        });


        //Mapped CSV Header Grid
        this.mappedCsvheaders="";
        this.mappedCsvHeaderDs = new Wtf.data.JsonStore({
            fields: [{
                name:"header"
            },{
                name:"index"
            }]
        });
        this.mappedCsvHeaderDs.loadData(this.mappedCsvheaders);
        var mappedCsvHeaderCm = new Wtf.grid.ColumnModel([{
            header: WtfGlobal.getLocaleText("crm.common.mappedcolumns"),//"Mapped Headers",
            dataIndex: 'header'
        }]);
        this.mappedCsvHeaderGrid= new Wtf.grid.GridPanel({
            ddGroup:"restoreHeader",
            enableDragDrop : true,
            sm:new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            store: this.mappedCsvHeaderDs,
            cm: mappedCsvHeaderCm,
            height:370,
            border : false,
            loadMask : true,
            tbar:[{xtype:'panel',height:10,border:false}],
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.common.dragndropcolsmsg")//"Drag and Drop Headers here"
            })
        });

        this.add({
            border: false,
            layout : 'border',
            items :[{
                region: 'north',
                border:false,
                height:80,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                items:[{
                    xtype:"panel",
                    border:false,
                    height:70,
                    //Drag-and-drop '"+headerName+"' fields to the 'Mapped Headers' list and subsequently Drag-and-drop corresponding 'Table Columns' field to the 'Mapped Columns' list.  Click 'Auto Map Columns' to map columns having similar names automatically.
                    html:getImportTopHtml(WtfGlobal.getLocaleText({key:"crm.masterconfig.leadMapping.tophtmlheader",params:[headerName]}),"<ul style='list-style-type:disc;padding-left:15px;'><li>"+WtfGlobal.getLocaleText("crm.common.import.tophtml.dragdropmsg")+"</li><li>"+WtfGlobal.getLocaleText("crm.common.import.tophtml.automappmsg")+"</li></ul>","../../images/link2.jpg", true, "10px 0 0 5px", "7px 0px 0px 10px")
                }]
            },{
                region: 'center',
                autoScroll: true,
                bodyStyle : 'background:white;font-size:10px;',
                border:false,
                layout:"column",
                items: [
                    {
                        xtype:"panel",
                        columnWidth:.24,
                        border:false,
                        layout:"fit",
                        autoScroll:true,
                   // title:headerName,
                        items:this.csvHeaderGrid
                    },{
                        xtype:"panel",
                        columnWidth:.23,
                        border:false,
                        layout:"fit",
                        autoScroll:true,
                   // title:"Mapped Headers",
                        items:this.mappedCsvHeaderGrid
                    },{
                        xtype:"panel",
                        columnWidth:.23,
                        border:false,
                        layout:"fit",
                        autoScroll:true,
                   // title:"Mapped Columns",
                        items:this.mappedColsGrid
                    },{
                        xtype:"panel",
                        columnWidth:.24,
                        border:false,
                        layout:"fit",
                        autoScroll:true,
                  //  title:"Table Columns",
                        items:this.tableColumnGrid
                    }
                ]
            }
            ],
            buttonAlign: 'right',
            buttons:[this.addCustomColumn = new Wtf.Action({
                text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.addcustomcol"),//"Add Custom Column",
                tooltip:{text:'Add new custom column.'},
                iconCls:"pwnd customizeHeader",
                hidden: (this.modName != "Target" ? false : true),
                handler:function (){
                    this.moduleName = this.modName;
                    switch(this.moduleName) {
                        case "Lead" : this.moduleid = "2"; break;
                        case "Contact" : this.moduleid = "6"; break;
                        case "Account" : this.moduleid = "1"; break;
                        case "Case" : this.moduleid = "3"; break;
                        case "Opportunity" : this.moduleid = "5"; break;
                        case "Product" : this.moduleid = "4"; break;
                    }
                    this.colModel = undefined;
                    this.importflag = 1;
                    addCustomColumn(this);
                },
                scope:this
            }),{
                text: WtfGlobal.getLocaleText("crm.common.previousbtn"),//'Previous',
                minWidth: 80,
                handler: function(){
                        this.impWin1.show();                  
                        this.hide();
                        this.impWin1.doLayout();
               },
                scope:this
            },{
                text: WtfGlobal.getLocaleText("crm.common.import.automapcolumnsbtn"),//'Auto Map Columns',
                minWidth: 80,
                handler: this.autoMapHeaders,
                scope:this
            },{
                text:  WtfGlobal.getLocaleText("crm.common.import.analyzedatabtn"),//'Analyze Data',
                minWidth: 80,
                handler: function(){
                    var totalmappedHeaders = this.mappedCsvHeaderDs.getCount();
                    var totalMappedColumns = this.mappedColsDs.getCount();
                    if(totalmappedHeaders==0 && totalMappedColumns==0) {
                        WtfImportMsgBox(43);
                    } else {
                        if(this.columnDs.getCount()>0) {
                            for(var i=0; i<this.columnDs.getCount(); i++) {
                                if(this.columnDs.getAt(i).data.isMandatory) {
                                    WtfImportMsgBox(44);
                                    return;
                                }
                            }
                        }
                        if(totalmappedHeaders==totalMappedColumns){
                            this.generateJsonForXML();
                                if(this.typeXLSFile){
    //                                this.fireEvent('importfn',this.mappingJSON,this.index,this.moduleName,this.store,this.contactmapid,this.targetlistPagingLimit,this.scopeobj,this.extraParams, this.extraConfig);
                                    this.fireEvent('importfn',this.mappingJSON,this.index,this.moduleName,this.store,this.scopeobj,this.extraParams, this.extraConfig);
                                }else {
                                    this.fireEvent('importfn',this.mappingJSON, this.delimiterType, this.moduleName, this.store, this.scopeobj, this.extraParams, this.extraConfig);
                                }
                                this.hide();
                            } else {
                            WtfImportMsgBox(["Header Mapping", "Please select column for selected header"], 0);
                        }
                    }
                },
                scope:this
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                minWidth: 80,
                handler: function(){
                    closeImportWindow();
                },
                scope: this
            }]
        });

        this.on("show", function(){//Reload csv and table column grids
            if(!this.headerConfig || this.headerConfig != this.csvheaders){ // Check for new mapping updates
                this.headerConfig = this.csvheaders;
                this.mappedColsDs.removeAll();
                this.mappedCsvHeaderDs.removeAll();
                this.loadHeaderData();
                if(this.columnDs.getCount()>0){
                    this.columnDs.loadData(this.columnDs.reader.jsonData);
                } else {
                    this.columnDs.load({params : {module : this.modName}});
                }
            }
        });

        //this.isMappingModified: Flag to recall validation function
        this.columnDs.on("add", function(){this.isMappingModified=true;}, this);
        this.mappedColsDs.on("add", function(){this.isMappingModified=true;}, this);
        this.csvHeaderDs.on("add", function(){this.isMappingModified=true;}, this);
        this.mappedCsvHeaderDs.on("add", function(){this.isMappingModified=true;}, this);

        this.on("customColAdd", function(columnName, pojoName, isMandatory) {
//            var newrec = new this.columnRec({
//                columnName:columnName,
//                pojoName:pojoName,
//                isMandatory:isMandatory
//            });
//            this.columnDs.add(newrec);
            this.columnDs.load({params : {module : this.modName}});
        }, this);

        this.on("afterlayout",function(){
            function rowsDiff(store1,store2){
                return diff=store1.getCount()-store2.getCount();
            }

            function unMapRec(atIndex){
                var headerRec = mappedHeaderStore.getAt(atIndex);
                if(headerRec!==undefined){
                    mappedHeaderStore.remove(headerRec);
//                    headerStore.getAt(headerStore.find("index",headerRec.data.index)).data.isMapped=false;
//                    headerGrid.getView().refresh();
//                    headerStore.add(headerRec);//Commented to allow Multiple header mapping{SK}
                }

                var columnRec = mappedColumnStore.getAt(atIndex);
                if(columnRec!==undefined){
                    mappedColumnStore.remove(columnRec);
                    columnStore.add(columnRec);
                     //Rearrange table columns
                    columnStore.sort("columnName","ASC");
                    columnStore.sort("isMandatory","DESC");
                }
            }

            columnStore = this.columnDs;
            columnGrid = this.tableColumnGrid;

            mappedColumnStore = this.mappedColsDs;
            mappedColumGrid = this.mappedColsGrid;

            headerStore = this.csvHeaderDs;
            headerGrid = this.csvHeaderGrid;

            mappedHeaderStore = this.mappedCsvHeaderDs;
            mappedHeaderGrid = this.mappedCsvHeaderGrid;

            // Drag n drop [ Headers -> Mapped Headers ]
            DropTargetEl =  mappedHeaderGrid.getView().el.dom.childNodes[0].childNodes[1];
            DropTarget = new Wtf.dd.DropTarget(DropTargetEl, {
                ddGroup    : 'mapHeader',
//                copy       : true,
                notifyDrop : function(ddSource, e, data){
                    function mapHeader(record, index, allItems) {
                        if(rowsDiff(mappedHeaderStore,mappedColumnStore)==0){
                            if(columnStore.getCount()!=0){
//                                headerStore.getAt(headerStore.find("index",record.data.index)).data.isMapped=true;
//                                headerGrid.getView().refresh();
                                var newHeaderRecord = new Wtf.data.Record(record.data);
                                mappedHeaderStore.add(newHeaderRecord);
//                                ddSource.grid.store.remove(record);//Commented to allow Multiple header mapping{SK}
                            } else {
                            	//"Header Mapping", "No column for mapping"
                                WtfImportMsgBox([WtfGlobal.getLocaleText("crm.common.headermappingtitle"), WtfGlobal.getLocaleText("crm.common.import.nocolstomapmsg")], 0);
                            }
                        }else{
                        	//"Header Mapping" "Please map the previous header first."
                            WtfImportMsgBox([WtfGlobal.getLocaleText("crm.common.headermappingtitle"), WtfGlobal.getLocaleText("crm.common.import.mapprevhdrsmsg")], 0);
                        }
                    }
                    Wtf.each(ddSource.dragData.selections ,mapHeader);
                    return(true);
                }
            });

            // Drag n drop [ Mapped Headers -> Headers ]
            DropTargetEl =  headerGrid.getView().el.dom.childNodes[0].childNodes[1];
            DropTarget = new Wtf.dd.DropTarget(DropTargetEl, {
                ddGroup    : 'restoreHeader',
//                copy       : true,
                notifyDrop : function(ddSource, e, data){
                    function restoreColumn(record, index, allItems) {
                        unMapRec(ddSource.grid.store.indexOf(record));
                    }
                    Wtf.each(ddSource.dragData.selections ,restoreColumn);
                    return(true);
                }
            });

            // Drag n drop [ columns -> Mapped columns ]
            DropTargetEl =  mappedColumGrid.getView().el.dom.childNodes[0].childNodes[1];
            DropTarget = new Wtf.dd.DropTarget(DropTargetEl, {
                ddGroup    : 'mapColumn',
                copy       : true,
                notifyDrop : function(ddSource, e, data){
                    function mapColumn(record, index, allItems) {
                        if(rowsDiff(mappedHeaderStore,mappedColumnStore)==1){
                            mappedColumnStore.add(record);
                            ddSource.grid.store.remove(record);
                        }else{
                        	//Please select the header first.
                            WtfImportMsgBox([WtfGlobal.getLocaleText("crm.common.headermappingtitle"), WtfGlobal.getLocaleText("crm.common.import.selhdrfstmsg")], 0);
                        }
                    }
                    Wtf.each(ddSource.dragData.selections ,mapColumn);
                    return(true);
                }
            });

            // Drag n drop [ Mapped columns -> columns ]
            DropTargetEl =  columnGrid.getView().el.dom.childNodes[0].childNodes[1];
            DropTarget = new Wtf.dd.DropTarget(DropTargetEl, {
                ddGroup    : 'restoreColumn',
                copy       : true,
                notifyDrop : function(ddSource, e, data){
                    function restoreColumn(record, index, allItems) {
                        unMapRec(ddSource.grid.store.indexOf(record));
                    }
                    Wtf.each(ddSource.dragData.selections ,restoreColumn);
                    return(true);
                }
            });
        },this);
    },

    loadHeaderData: function(){
        this.csvHeaderDs.loadData(this.csvheaders);
        this.quickSearchTF.StorageChanged(this.csvHeaderDs);
    },

    autoMapHeaders: function(){
        //Sort columns by name for comparison
        this.columnDs.sort("columnName","ASC");
        this.csvHeaderDs.sort("header","ASC");

        //Clone csv header store
        if(this.csvHeaderDs.getCount()>0){
            if(this.tempFileHeaderDs.getCount()>0){this.tempFileHeaderDs.removeAll();}
            this.tempFileHeaderDs.loadData(this.csvheaders);
        }

        //Exact Match
        for(var i=0; i<this.columnDs.getCount(); i++){
            var colrec = this.columnDs.getAt(i);
            var colHeader = colrec.data.columnName;
            colHeader = colHeader.trim();

            for(var j=0; j<this.tempFileHeaderDs.getCount(); j++){
                var csvrec = this.tempFileHeaderDs.getAt(j);
                var csvHeader = Encoder.htmlDecode(csvrec.data.header);
                csvHeader = csvHeader.trim();

                if(colHeader.toLowerCase()==csvHeader.toLowerCase()){
                    this.columnDs.remove(colrec);
                    this.mappedColsDs.add(colrec);

                    this.tempFileHeaderDs.remove(csvrec);
                    this.mappedCsvHeaderDs.add(csvrec);
                    i--;//'i' decreamented as count of columnDs store is reduce by 1
                }
            }
        }

        //Like Match from Table Columns
        for(i=0; i<this.columnDs.getCount(); i++){
            colrec = this.columnDs.getAt(i);
            colHeader = colrec.data.columnName;
            colHeader = colHeader.trim();
            var regex = new RegExp("^"+colHeader, "i");

            for(j=0; j<this.tempFileHeaderDs.getCount(); j++){
                csvrec = this.tempFileHeaderDs.getAt(j);
                csvHeader = Encoder.htmlDecode(csvrec.data.header);
                csvHeader = csvHeader.trim();

                if(regex.test(csvHeader)){
                    this.columnDs.remove(colrec);
                    this.mappedColsDs.add(colrec);

                    this.tempFileHeaderDs.remove(csvrec);
                    this.mappedCsvHeaderDs.add(csvrec);
                    i--;//'i' decreamented as count of columnDs store is reduce by 1
                }
            }
        }

        //Like Match from CSV Header
        for(j=0; j<this.tempFileHeaderDs.getCount(); j++){
            csvrec = this.tempFileHeaderDs.getAt(j);
            csvHeader = Encoder.htmlDecode(csvrec.data.header);
            csvHeader = csvHeader.trim();
            regex = new RegExp("^"+csvHeader, "i");

            for(i=0; i<this.columnDs.getCount(); i++){
                colrec = this.columnDs.getAt(i);
                colHeader = colrec.data.columnName;
                colHeader = colHeader.trim();

                if(regex.test(colHeader)){
                    this.columnDs.remove(colrec);
                    this.mappedColsDs.add(colrec);

                    this.tempFileHeaderDs.remove(csvrec);
                    this.mappedCsvHeaderDs.add(csvrec);
                    j--;//'j' decreamented as count of csvHeaderDs store is reduce by 1
                }
            }
        }

        //Move all mandatory columns an top
        this.columnDs.sort("isMandatory","DESC");

        if(this.mappedColsDs.getCount()==0){    // No matching pairs
            WtfImportMsgBox(52,0);
        }
    },

    generateJsonForXML : function(){
        this.mappingJSON = "";
        this.masterItemFields = "";
        this.moduleRefFields = "";
        this.unMappedColumns = "";
        for(var i=0;i<this.mappedCsvHeaderDs.getCount();i++){
            this.mappingJSON+="{\"csvindex\":\""+this.mappedCsvHeaderDs.getAt(i).get("index")+"\","+
                                "\"csvheader\":\""+this.mappedCsvHeaderDs.getAt(i).get("header")+"\","+
                                "\"columnname\":\""+this.mappedColsDs.getAt(i).get("pojoName")+"\""+
                              "},";
            var validateType=this.mappedColsDs.getAt(i).get("validatetype");
            if(validateType=="ref" || validateType=="refdropdown"){
                if(this.mappedColsDs.getAt(i).get("configid").trim().length > 0){
                    this.masterItemFields += " "+this.mappedColsDs.getAt(i).get("columnName")+",";
                } else {
                    this.moduleRefFields += " "+this.mappedColsDs.getAt(i).get("columnName")+",";
                }
            }
        }
        this.mappingJSON = this.mappingJSON.substr(0, this.mappingJSON.length-1);
        this.mappingJSON = "{\"root\":["+this.mappingJSON+"]}";

        this.masterItemFields = this.masterItemFields.length>0 ? this.masterItemFields.substr(0, this.masterItemFields.length-1) : this.masterItemFields.trim();
        this.moduleRefFields = this.moduleRefFields.length>0 ? this.moduleRefFields.substr(0, this.moduleRefFields.length-1) : this.moduleRefFields.trim();

        for(i=0;i<this.columnDs.getCount();i++){
            this.unMappedColumns += " "+this.columnDs.getAt(i).get("columnName")+",";
        }
        this.unMappedColumns = this.unMappedColumns.length>0 ? this.unMappedColumns.substr(0, this.unMappedColumns.length-1) : this.unMappedColumns.trim();
    }
});

/*-------------------- Function to show Validate Windows -----------------*/
Wtf.ValidateFileRecords = function(typeXLSFile, moduleName, store, scopeobj, extraParams, extraConfig){
    var url = Wtf.req.springBase+"common/ImportRecords/importRecords.do";
//    if(moduleName=="Accounts") {
//        url = "ACCAccount/importAccounts.do";
//    } else if(moduleName=="DefaultAccount") {
//        url = "ACCAccount/importDefaultAccounts.do";
//    } else if(moduleName=="Customer") {
//        url = "ACCCustomer/importCustomer.do";
//    } else if(moduleName=="Vendor") {
//        url = "ACCVendor/importVendor.do";
//    }
    if(extraConfig == undefined) {
        extraConfig={};
    } else {
        if(extraConfig.url!=undefined){
            url = extraConfig.url;
        }
    }
    extraConfig['moduleName'] = moduleName;
    extraConfig['extraParams'] = extraParams;

    var importParams = {};
    importParams.url = url;
    importParams.extraConfig = extraConfig;
    importParams.extraParams = extraParams;
    importParams.store = store;
    importParams.scopeobj = scopeobj;

    var validateWindow = Wtf.getCmp("IWValidationWindow");
    if(!validateWindow) {
        new Wtf.IWValidationWindow({
           title: WtfGlobal.getLocaleText("crm.common.import.validationanalysisreport"),//"Validation Analysis Report",
           prevWindow: Wtf.getCmp("csvMappingInterface"),
           typeXLSFile: typeXLSFile,
           importParams: importParams
        }).show();
    }else{
        validateWindow.show();
    }
}
/**********************************************************************************************************
 *                              Validation Window
 **********************************************************************************************************/
Wtf.IWValidationWindow=function(config){
    Wtf.IWValidationWindow.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.IWValidationWindow,Wtf.Window,{
    iconCls : 'importIcon',
    width: 750,
    height: 570,
    modal: true,
    layout: "border",
    id: 'IWValidationWindow',
    closable: false,
    initComponent:function(config){
        Wtf.IWValidationWindow.superclass.initComponent.call(this,config);
        this.prevButton = new Wtf.Button({
            text: WtfGlobal.getLocaleText("crm.common.import.remapheader"),//"Remap Header",
            scope: this,
            minWidth: 80,
            handler: function(){
                if(this.prevWindow){
                    this.prevWindow.show();
                    this.prevWindow.doLayout();
                }
                this.hide();
            }
        });
        this.importButton = new Wtf.Button({
            text: WtfGlobal.getLocaleText("crm.common.import.importdatabtn"),//"Import Data",
            scope: this,
            minWidth: 80,
            handler: this.importRecords
        });
        this.cancelButton = new Wtf.Button({
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel
            scope: this,
            minWidth: 80,
            handler: function(){
                closeImportWindow();
            }
        });
        this.buttons = [this.prevButton, this.importButton, this.cancelButton];
    },

    onRender:function(config){
        Wtf.IWValidationWindow.superclass.onRender.call(this,config);

        this.on("show", function(){
            var uploadWindow = Wtf.getCmp("importwindow");          //check for pref updates
            var mappingWindow = Wtf.getCmp("csvMappingInterface");  //check for mapping updates
            if((uploadWindow && uploadWindow.isPrefModified) || (mappingWindow && mappingWindow.isMappingModified)){ // Check for new mapping updates
                if(uploadWindow){uploadWindow.isPrefModified=false;}
                if(mappingWindow){mappingWindow.isMappingModified=false;}
                this.validateRecords();
            }
        },this);

        this.northMessage = "<ul style='list-style-type:disc;padding-left:15px;'>" +
                    "<li>If you wish to map headers again, click on 'Remap Header'</li>"+
                    "<li>To continue with the import process, click on 'Import Data'</li>";

        this.add(this.northPanel= new Wtf.Panel({
            region: 'north',
            height: 70,
            border: false,
            bodyStyle: 'background:white;padding:7px',
            html: getImportTopHtml("List of all invalid records from the file.", this.northMessage+"</ul>" ,"../../images/import.png", true, "0px", "2px 0px 0px 10px")
        }));

        this.columnRec = new Wtf.data.Record.create([
            "col0","col1","col2","col3","col4","col5","col6","col7","col8","col9",
            "col10","col11","col12","col13","col14","col15","col16","col17","col18","col19",
            "col20","col21","col22","col23","col24","col25","col26","col27","col28","col29",
            "col30","col31","col32","col33","col4","col335","col36","col37","col38","col39",
            "col40","col41","col42","col43","col44","col45","col46","col47","col48","col49",
            "col50","col51","col52","col53","col54","col55","col56","col57","col58","col59",
            "col60","col61","col62","col63","col64","col65","col66","col67","col68","col69",
            "col70","col71","col72","col73","col74","col75","col76","col77","col78","col79",
            "col80","col81","col82","col83","col84","col85","col86","col87","col88","col89",
            "invalidcolumns","validateLog"]);

        this.colsReader = new Wtf.data.JsonReader({
            root: 'data',
            totalProperty: 'count'
        },this.columnRec);

        this.columnDs = new Wtf.data.Store({
            proxy: new Wtf.data.PagingMemoryProxy([]),
            reader: new Wtf.data.JsonReader({
                totalProperty: "count",
                root: "data"
            },this.columnRec)
        });

        this.columnCm = new Wtf.grid.ColumnModel([
        {
            header: " ",
            dataIndex: "col0"
        }
        ]);
        this.sm = new Wtf.grid.RowSelectionModel({singleSelect:true});
        this.gridView = new Wtf.grid.GridView({
//                forceFit:true,
//                emptyText:"All records are valid, click on \"Import\" to continue."
            });
//        this.gridView = new Wtf.ux.grid.BufferView({
//            scrollDelay: false,
//            autoFill: true
//        });
        this.Grid = new Wtf.grid.GridPanel({
            store: this.columnDs,
            sm:this.sm,
            cm: this.columnCm,
            border : true,
            loadMask : true,
            view: this.gridView,
            bbar: this.pag=new Wtf.PagingToolbar({
                pageSize: 30,
                border : false,
                id : "paggintoolbar"+this.id,
                store: this.columnDs,
                plugins : this.pPageSizeObj = new Wtf.common.pPageSize({
                    id : "pPageSize_"+this.id
                }),
                autoWidth : true,
                displayInfo: true,
                emptyMsg: ""
            })
        });

        this.sm.on("selectionChange", function(){
            this.updateLogDetails(true);
        }, this);

        this.add({
            region: 'center',
            layout: 'fit',
            border: false,
            autoScroll: true,
            bodyStyle: 'background:white;padding:7px',
            items: this.Grid
        });

        this.ValidationDetails = new Wtf.Panel({
            border: false,
            bodyStyle: 'padding-top:7px',
            html: "<div>&nbsp</div>"
        });

        this.progressBar = new Wtf.ProgressBar({
            text:WtfGlobal.getLocaleText("crm.common.import.validatingloadmsg"),//'Validating...',
            hidden: true,
            cls: "x-progress-bar-default"
        });
        this.add(this.ValidationDetails);
        this.add({
            region: 'south',
            autoScroll: true,
            height: 70,
            border: false,
            bodyStyle: 'background:white;padding:0 7px 7px 7px',
            items: [
                this.progressBar,
                this.ValidationDetails
            ]
        });
    },

    validateRecords: function(){
        if(this.columnDs.getCount()>0) {        // clear previous validation
            this.columnDs.removeAll();
        }
        this.updateLogDetails(true);
        this.enableDisableButtons(false);

        Wtf.Ajax.timeout=900000;
//        Wtf.commonWaitMsgBox("Validating data... It may take few moments...");

        this.validateSubstr = "/ValidateFile/" + this.importParams.extraConfig.filename ;
        dojo.cometd.subscribe(this.validateSubstr, this, "globalInValidRecordsPublishHandler");

        Wtf.Ajax.requestEx({
            url: this.importParams.url+'?type=submit&do=validateData',
            waitMsg :WtfGlobal.getLocaleText("crm.common.import.validatingloadmsg"),//'Validating...',
            scope:this,
            params: this.importParams.extraConfig
        },
        this,
        function (action,res) {
            Wtf.updateProgress();
            if(action.success){
                this.createGrid(action);
                this.enableDisableButtons(true);
            } else {
            	//"Failure","An error occurred while validating the records from file.<br/>"+action.msg"
                WtfImportMsgBox([WtfGlobal.getLocaleText("crm.msg.FAILURETITLE"), WtfGlobal.getLocaleText({key:"crm.common.import.valrecerrmsg",params:[action.msg]})], 1);
            }
            if(action.exceededLimit=="yes"){ // update north panel
                this.northMessage = this.northMessage+"<li>This report shows invalid records from the first 1500 records in your file as it contains more than 1500 records</li><br/>";
                this.northPanel.body.dom.innerHTML=getImportTopHtml("List of all invalid records from the file.", this.northMessage+"</ul>","../../images/import.png", true, "0px", "2px 0px 0px 10px")
            }
            this.importParams.extraConfig.exceededLimit = action.exceededLimit;
            Wtf.Ajax.timeout=30000;
        },
        function (action,res) {
            Wtf.updateProgress();
            WtfImportMsgBox(50, 1);
            this.importParams.extraConfig.exceededLimit = action.exceededLimit;
            Wtf.Ajax.timeout=30000;
        });
    },

    importRecords: function(){
        dojo.cometd.unsubscribe(this.validateSubstr);

        Wtf.Ajax.timeout=900000;
        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.common.import.waitmsg"));//"Importing data... It may take few moments...
        Wtf.Ajax.requestEx({
            url: this.importParams.url+'?type=submit&do=import',
            waitMsg :WtfGlobal.getLocaleText("crm.common.import.loadmsg"),//'importing...',
            scope:this,
            params: this.importParams.extraConfig
        },
        this,
        function(res) {
            Wtf.updateProgress();
            if(res.success){
                if(res.exceededLimit=="yes"){ // Importing data with thread
//                    Wtf.Msg.alert('Success', 'We are now importing your data from the uploaded file.<br/>Depending on the number of records, this process can take anywhere from few minutes to several hours.<br/>A detailed report will be sent to you via email and will also be displayed in the import log after the process is completed.');
                    // StoreManager (Global Store) reload
//                    Wtf.globalStorereload(this.importParams.scopeobj,this.importParams.extraConfig);
                    showImportSummary(true, res);
                } else {
                    if(this.importParams.store!=undefined) {
                        Wtf.globalStorereload(this.importParams.scopeobj,this.importParams.extraConfig);
                        this.importParams.store.reload();
                    }
//                    WtfImportMsgBox(["Alert", res.msg], 0);
                    showImportSummary(false, res);
                }
                Wtf.Ajax.timeout=30000;
                closeImportWindow();
            }
        },
        function(res){
            Wtf.updateProgress();
            //Error while importing data from the uploaded file','Error while importing records.<br/>Please try again after some time
            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.common.import.importdataerrormsgtitle"),WtfGlobal.getLocaleText("crm.common.import.importdataerrormsgdetail"));
            Wtf.Ajax.timeout=30000;
            closeImportWindow();
        })
    },

    createGrid: function(response){
        this.createColumnModel(response);

        this.columnDs.proxy.data = response;
        this.columnDs.load({params:{start:0, limit:this.pag.pageSize}});
        this.Grid.reconfigure(this.columnDs, this.columnCm);
        this.updateLogDetails(false);
    },

    createColumnModel: function(response){
        this.columnRec = new Wtf.data.Record.create(response.record);
        this.columnCm = new Wtf.grid.ColumnModel(response.columns);

        var sheetStartIndex = this.importParams.extraConfig.startindex;
        this.columnCm.setRenderer(0, function(val, md, rec){ //Add start index and row no. column [in case of .XLS import with start index > 1]
            if(sheetStartIndex!= undefined){
                val = (val*1) + (sheetStartIndex*1);
            }
            return ""+val;
        });

        for(var j=1; j<this.columnCm.getColumnCount()-1; j++){
            this.columnCm.setRenderer(j, function(val, md, rec, ri, ci, store){ // Add renderer to invalid columns
                var invalidColumns = rec.data.invalidcolumns;
                var columnDataIndex = "col"+ci+",";
                var regex = new RegExp(columnDataIndex);
                if(regex.test(invalidColumns)){
                    return "<div style='color:#F00'>"+val+"</div>";
                } else {
                    return val;
                }
            });
        }

        this.columnCm.setRenderer(this.columnCm.getColumnCount()-1, function(val){ // Add renderer to last column
            return "<span wtf:qtip=\""+val+"\">"+val+"</span>";
        });
        this.Grid.reconfigure(this.columnDs, this.columnCm);
    },

    updateLogDetails: function(onSelection){
        var msg = "";
        if(this.columnDs.getCount()>0) {
            if(this.sm.getCount()==1){
                var rec = this.sm.getSelected();
                var rowNo = rec.data.col0;
                var sheetStartIndex = this.importParams.extraConfig.startindex;
                if(sheetStartIndex!= undefined){
                    rowNo = (rowNo*1) + (sheetStartIndex*1);
                }
                msg = "<div><b>Validation Details for row "+rowNo+":</b><br/>"+
                        ""+ replaceAll(rec.data.validateLog,"\\.",".<br/>") + ""+
                      "</div>";
            } else {
                msg = "<div><b>Validation Details:</b><br/>"+
                        "Please select a record."+
                      "</div>";
            }
        } else if(this.columnDs.getCount()==0) {
            if(!onSelection){
                msg = "All records are valid, Please click on \"Import Data\" button to continue.";
                this.gridView.emptyText= "<b>"+msg+"</b>";
                this.gridView.refresh();
            }
        }

        this.ValidationDetails.body.dom.innerHTML= msg;
    },

    globalInValidRecordsPublishHandler: function(response){
        var msg = "";
        var res = eval("("+response.data+")");

        if(res.finishedValidation){
            this.enableDisableButtons(true);
            return;
        }

        if(res.isHeader){
            this.createColumnModel(res);
        }else{
            if(res.parsedCount){
//                msg = (res.invalidCount==0?"Validated":("Found <b>"+res.invalidCount+"</b> invalid record"+(res.invalidCount>1?"s":"")+" out of top"))+" <b>"+res.parsedCount+"</b> record"+(res.parsedCount>1?"s":"")+" from the file.";
                msg = "Validated <b>"+res.parsedCount+"</b>"+ (res.invalidCount==0?"":(" and found <b>"+res.invalidCount+"</b> invalid record"+(res.invalidCount>1?"s":"")+""))+" out of <b>"+res.fileSize+"</b> record"+(res.fileSize>1?"s":"")+" from the file.";
                this.progressBar.updateProgress(res.parsedCount/res.fileSize, msg);
            }else{
                var newRec = new this.columnRec(res);
                this.columnDs.add(newRec);//if(this.columnDs.getCount()<=this.pag.pageSize)this.columnDs.add(newRec);//this.columnDs.insert(0,newRec);
//                msg = (res.count==0?"Validated":("Found <b>"+res.count+"</b> invalid record"+(res.count>1?"s":"")+" out of top"))+" <b>"+res.totalrecords+"</b> record"+(res.totalrecords>1?"s":"")+" from the file.";
                msg = "Validated <b>"+res.totalrecords+"</b>"+ (res.invalidCount==0?"":(" and found <b>"+res.count+"</b> invalid record"+(res.count>1?"s":"")+""))+" out of <b>"+res.fileSize+"</b> record"+(res.fileSize>1?"s":"")+" from the file.";
                this.progressBar.updateProgress(res.parsedCount/res.fileSize, msg);
                this.gridView.scroller.dom.scrollTop = this.gridView.scroller.dom.scrollHeight-2;
            }
        }
    },

    enableDisableButtons: function(enable){
        if(enable){
            this.prevButton.enable();
            this.importButton.enable();
            this.cancelButton.enable();
            this.progressBar.hide();
        }else{
            this.prevButton.disable();
            this.importButton.disable();
            this.cancelButton.disable();
            this.progressBar.show();
            this.progressBar.updateProgress(0,WtfGlobal.getLocaleText("crm.common.import.validatingloadmsg"));
            this.gridView.emptyText= "";
            this.gridView.refresh();
        }
    }
});

function showImportSummary(backgroundProcessing, response){
    var message = "";
    if(backgroundProcessing){
        message = "<div class=\"popup-info\">"+
            "<h2 class=\"blue-h2\">View your import progress</h2>"+
            "<br/>"+
            "<div class=\"right-bullets\"><span>1</span>We are now importing your data from the uploaded file.</div>"+
            "<div class=\"right-bullets\"><span>2</span>Depending on the number of records, this process can take anywhere from few minutes to several hours.</div>"+
            "<div class=\"right-bullets\"><span>3</span>A detailed report will be sent to you via email and will also be displayed in the <a wtf:qtip=\"Click here to open Import Log\" href=\"#\" onclick=\"linkImportFilesLog()\">'Import Log'</a> after the process is completed.</div>"+
            "<img border=\"0\" src=\"../../images/importWizard/import-log.jpg\"/>"+
        "</div>";
    } else {
        message = "<div class=\"popup-info\">"+
            "<h2 class=\"blue-h2\">Import Status</h2>"+
            "<br/>"+
            "<div class=\"right-bullets\"><span>1</span>"+response.msg+"</div>"+
            "<div class=\"right-bullets\"><span>2</span>You can find detailed report in the <a wtf:qtip=\"Click here to open Import Log\" href=\"#\" onclick=\"linkImportFilesLog()\">'Import Log'</a>.</div>"+
            "<img border=\"0\" src=\"../../images/importWizard/import-log.jpg\"/>"+
        "</div>";

    }
    var win = new Wtf.Window({
        resizable: true,
        layout: 'border',
        modal:true,
        width: 655,
        height: 350,
        iconCls: 'importIcon',
        title: 'Import Status',
        id: "importSummaryWin",
        items: [
                {
                    region:'center',
                    layout:'fit',
                    border:false,
                    bodyStyle: 'background:white;font-size:10px;padding-left:10px;',
                    html: message
                }
        ],
        buttons: [{
            text:"View Import Log",
            scope: this,
            handler:function() {
                linkImportFilesLog();
            }
        },{
            text:WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),//"Close",
            scope: this,
            handler:function() {
                Wtf.getCmp("importSummaryWin").close();
            }
        }]
    },this).show();
}

function linkImportFilesLog(){
    // After importing any file close the 'Import Summary Window' if open.[SK]
    var SummaryWin = Wtf.getCmp("importSummaryWin");
    if(SummaryWin) {
        if(SummaryWin.isVisible()){
            SummaryWin.close();
        } else {
            SummaryWin.destroy();
        }
    }
    callImportFilesLog();
}
/*-------------------- Function to show Upload Windows -----------------*/
Wtf.commonFileImportWindow = function(obj, moduleName, store, extraParams, extraConfig){
    var impWin1 = new Wtf.UploadFileWindow({
        title: WtfGlobal.getLocaleText("crm.common.importcsvfile"),//'Import CSV File',
        width: 600,
        height: 400,
        iconCls: 'importIcon',
        obj: obj,
        moduleName: moduleName,
        store: store,
        extraParams: extraParams,
        extraConfig:extraConfig,
        typeXLSFile: false
    });
    return impWin1;
}

Wtf.xlsCommonFileImportWindow = function(obj,moduleName,store,extraParams, extraConfig) {
    var impWin1 = new Wtf.UploadFileWindow({
        title: WtfGlobal.getLocaleText("crm.common.importxlsfile"),//'Import XLS File',
        width: 600,
        height: 380,
        iconCls: 'importIcon',
        obj: obj,
        moduleName: moduleName,
        store: store,
        extraParams: extraParams,
        extraConfig:extraConfig,
        typeXLSFile: true
    });
    return impWin1;
}
/*----------------------------------------------------------------------------
--------------------------- commonUploadWindow -------------------------------
------------------------------------------------------------------------------*/
Wtf.UploadFileWindow=function(config){
    Wtf.UploadFileWindow.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.UploadFileWindow, Wtf.Window,{
    id: 'importwindow',
    layout: "border",
    closable: false,
    resizable: false,
    modal: true,
    iconCls: 'importIcon',
    initComponent:function(config){
        Wtf.UploadFileWindow.superclass.initComponent.call(this,config);

        this.nextButton = new Wtf.Button({
            text: WtfGlobal.getLocaleText("crm.NEXTBTN"),//"Next",
            scope: this,
            minWidth: 80,
            handler: function(){
                if(this.typeXLSFile){
                    this.uploadXLSFile();
                }else {
                    this.uploadCSVFile();
                }
            }
        });
        this.cancelButton = new Wtf.Button({
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//"Cancel",
            scope: this,
            minWidth: 80,
            handler: function(){
                closeImportWindow();
            }
        });
        this.buttons = [this.nextButton, this.cancelButton];
    },

    onRender:function(config){
        Wtf.UploadFileWindow.superclass.onRender.call(this,config);
        this.isPrefModified = false;

        var delimiterStore = new Wtf.data.SimpleStore({
            fields: ['delimiterid','delimiter'],
            data : [
            [0,"Colon"],
            [1,'Comma'],
            [2,'Semicolon']/*,
            [3,'Space'],
            [4,'Tab']*/
            ]
        });
        this.conowner= new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("crm.import.importwin.delimeter"),//'Delimiter ',
            hiddenName:'Delimiter',
            store:delimiterStore,
            valueField:'delimiter',
            displayField:'delimiter',
            mode: 'local',
            triggerAction: 'all',
            emptyText:WtfGlobal.getLocaleText("crm.import.importwin.delimeter.mtytxt"),//'--Select delimiter--',
            typeAhead:true,
            selectOnFocus:true,
            allowBlank:false,
            width: 200,
            hidden: this.typeXLSFile,
            hideLabel: this.typeXLSFile,
            forceSelection: true,
            value:'Comma'
       });

      this.masterPreference = new Wtf.form.FieldSet({
            title: WtfGlobal.getLocaleText("crm.import.importwin.fieldset.title"),//"For missing entries in dropdown fields",
            autoHeight: true,
            border: false,
            defaultType: 'radio',
            items: [
                this.master0 = new Wtf.form.Radio({
                    checked: true,
                    fieldLabel: '',
                    labelSeparator: '',
                    boxLabel: WtfGlobal.getLocaleText("crm.import.importwin.fieldset.ignoreentirerec"),//"Ignore entire record",
                    name: 'masterPreference',
                    inputValue: "0"
                }),
                this.master1 = new Wtf.form.Radio({
                    ctCls:"fieldset-item",
                    fieldLabel: '',
                    labelSeparator: '',
                    boxLabel: WtfGlobal.getLocaleText("crm.import.importwin.fieldset.ignorerntryforthatrec"),//"Ignore entry for that record",
                    name: 'masterPreference',
                    inputValue: "1"
                }),
                this.master2 = new Wtf.form.Radio({
                    ctCls:"fieldset-item",
                    fieldLabel: '',
                    labelSeparator: '',
                    boxLabel: WtfGlobal.getLocaleText("crm.import.importwin.fieldset.addnewentrytomasterrecindrpdwn"),//"Add new entry to master record in dropdown",
                    name: 'masterPreference',
                    inputValue: "2"
                })]
        });

        this.dfRec = Wtf.data.Record.create ([
            {name:'formatid'},
            {name:'name'}
        ]);
        this.dfStore=new Wtf.data.Store({
            url:"Common/KwlCommonTables/getAllDateFormats.do",
            baseParams:{
                mode:32
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            },this.dfRec)
        });
        this.dfStore.load();
        this.dfStore.on('load',function(){
            if(this.dfStore.getCount()>0){
                var removeRecId = ['7','8']; // removed format "h:mm a" and "h:mm:ss a" while import csv file
                for(var cnt=0; cnt< removeRecId.length; cnt++) {
                    var index = this.dfStore.findBy(function(record){
                        if(record.data.formatid == removeRecId[cnt])
                            return true;
                        else return false;
                    },this);
                    if(index > -1) {
                        var rec = this.dfStore.getAt(index);
                        this.dfStore.remove(rec);
                    }
                }
                this.datePreference.setValue("2"); // Default for YYYY-MM-DD
            }
        },this);
        this.datePreference= new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("crm.updateprofile.dateformat"),//'Date Format',
            hiddenName:'dateFormat',
            store:this.dfStore,
            valueField:'formatid',
            displayField:'name',
            mode: 'local',
            triggerAction: 'all',
            width: 200,
            hidden: this.typeXLSFile,
            hideLabel: this.typeXLSFile,
//            editable : false,
            forceSelection: true
        });

        this.browseField = new Wtf.form.TextField({
            id:'browseBttn',
            border:false,
            inputType:'file',
            fieldLabel:WtfGlobal.getLocaleText("crm.import.importwin.upfile.filename"),//'File name ',
            name: 'test'
        });

        this.ImportForm = new Wtf.FormPanel({
            width:'90%',
            method :'POST',
            scope: this,
            border:false,
            fileUpload : true,
            waitMsgTarget: true,
            labelWidth: 80,
            bodyStyle: 'background:#f1f1f1;font-size:10px;padding:15px 0 0 60px;',
            layout: 'form',
            items:[
                this.browseField,
                this.conowner,
                this.datePreference,
                this.masterPreference
            ]
        });

        this.add({
                    region:'north',
                    height:70,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                    html: getImportTopHtml(WtfGlobal.getLocaleText({key:"crm.import.importwin.tohtml",params:[this.typeXLSFile?"XLS":"CSV"]}), "<ul style='list-style-type:disc;padding-left:15px;'><li>"+WtfGlobal.getLocaleText({key:"crm.import.importwin.tohtml.details",params:[this.typeXLSFile?".XLS":".CSV"]})+"</li></ul>","../../images/import.png", true, "5px 0px 0px 0px", "7px 0px 0px 10px")
                });

        this.add({
                    region:'center',
//                    layout:'fit',
                    border:false,
                    bodyStyle: 'background:#f1f1f1;font-size:10px;',
                    items:[
                        this.ImportForm,
                        new Wtf.Panel({
                            border: false,
                            bodyStyle: 'padding:5px;',
                            html: WtfGlobal.getLocaleText("crm.import.importwin.basemsg")//"<b>* It is recommended that you import a small test file of 5 records before importing all of your data to ensure that you have correctly prepared your import file.</b> "
                        })]
                })

        this.conowner.on("change", function(){this.isPrefModified=true;},this);
        this.master0.on("change", function(){this.isPrefModified=true;},this);
        this.master1.on("change", function(){this.isPrefModified=true;},this);
        this.master2.on("change", function(){this.isPrefModified=true;},this);
        this.datePreference.on("change", function(){this.isPrefModified=true;},this);
    },

    uploadCSVFile : function(){
        var master = 0;
        if(this.master0.getValue()){
            master = 0;
        } else if(this.master1.getValue()){
            master = 1;
        } else if(this.master2.getValue()){
            master = 2;
        }
        if(!this.browseField.disabled){
            var parsedObject = document.getElementById('browseBttn').value;
            var extension =parsedObject.substr(parsedObject.lastIndexOf(".")+1);
            var patt1 = new RegExp("csv","i");
            var delimiterType=this.conowner.getValue();
            if(delimiterType==undefined || delimiterType==""){
                WtfImportMsgBox(47);
                return;
            }
            if(patt1.test(extension)) {
                if(this.extraConfig == undefined) {
                    this.extraConfig={};
                }
                this.extraConfig['delimiterType'] = this.conowner.getValue();
                this.extraConfig['masterPreference'] = master;
                this.extraConfig['dateFormat'] = this.datePreference.getValue();

                this.ImportForm.form.submit({
                    url:Wtf.req.springBase+"common/ImportRecords/importRecords.do?type="+this.moduleName+"&do=getMapCSV&delimiterType="+delimiterType,
                    waitMsg :WtfGlobal.getLocaleText("crm.import.importwin.upfile.loading"),//'Uploading File...',
                    scope:this,
                    success: function (action, res) {
                        var resobj = eval( "(" + res.response.responseText.trim() + ")" );
                        if(resobj.data != "") {
                            this.mappingCSVInterface(resobj.Header, resobj, this, delimiterType, this.extraParams, this.extraConfig, this.obj, this.moduleName, this.store);
                        }
                        this.browseField.disable();
                        this.conowner.disable();
                    },
                    failure:function(action, res) {
                        var resobj = eval( "(" + res.response.responseText.trim() + ")" );
                        WtfImportMsgBox(["Error",resobj.msg], 1);
                    }

                });
            } else {
                WtfImportMsgBox(48);
            }
        } else {
            var mappingWindow = Wtf.getCmp("csvMappingInterface");
            if(mappingWindow.extraConfig == undefined) {
                mappingWindow.extraConfig={};
            }
            mappingWindow.extraConfig['delimiterType'] = this.conowner.getValue();
            mappingWindow.extraConfig['masterPreference'] = master;
            mappingWindow.extraConfig['dateFormat'] = this.datePreference.getValue();
            mappingWindow.show();
        }
    },

    mappingCSVInterface: function(Header, res, impWin1, delimiterType, extraParams, extraConfig, obj, moduleName, store) {
       obj.filename=res.FileName;

       if(extraConfig == undefined) {
            extraConfig={};
       }
       extraConfig['delimiterType'] = delimiterType;
       extraConfig['filename'] = res.FileName;

        this.mappingParams = {};
        this.mappingParams.csvheaders = Header;
        this.mappingParams.typeXLSFile = false;
        this.mappingParams.delimiterType = delimiterType;
        this.mappingParams.moduleName = moduleName;
        this.mappingParams.modName = moduleName;
        this.mappingParams.store = store;
        this.mappingParams.cm = obj.gridcm;
        this.mappingParams.extraParams = extraParams;
        this.mappingParams.extraConfig = extraConfig;
        this.mappingParams.scopeobj = obj;

        Wtf.callMappingInterface(this.mappingParams, this);
        this.hide();
    },

    uploadXLSFile: function(){
        var master = 0;
        if(this.master0.getValue()){
            master = 0;
        } else if(this.master1.getValue()){
            master = 1;
        } else if(this.master2.getValue()){
            master = 2;
        }
        if(!this.browseField.disabled){
            var parsedObject = document.getElementById('browseBttn').value;
            var extension =parsedObject.substr(parsedObject.lastIndexOf(".")+1);
            var patt1 = /^xls$/;
            var patt2 = /^xlsx$/;
            if(patt1.test(extension) || patt2.test(extension)) {
                this.extension = extension;
                var url = Wtf.req.springBase+"common/ImportRecords/fileUploadXLS.do";
                if(patt2.test(extension)) {
                    url = Wtf.req.springBase+"common/ImportRecords/fileUploadXLSX.do";
                }
                if(this.extraConfig == undefined) {
                    this.extraConfig={};
                }
                this.extraConfig['masterPreference'] = master;

                this.ImportForm.getForm().submit({
                    url:url,
                    waitMsg:'Uploading File...',
                    scope:this,
                    success:function(f,a){
                        this.browseField.disable();
                        this.genUploadResponse(a.request,true,a.response,this.moduleName,this.store, this.obj, this.extraParams, this.extraConfig)
                    },
                    failure:function(f,a){
                        this.genUploadResponse(a.request,false,a.response,this.moduleName,this.store, this.obj, this.extraParams, this.extraConfig)
                    }
                });
            } else {
//                patt1 = /^xlsx$/;
//                if(patt1.test(extension)) {
//                    WtfImportMsgBox(53);
//                } else {
                    WtfImportMsgBox(48);
//                }
            }
        } else {
            var xlsPreviewWindow = Wtf.getCmp("importxls");
            if(xlsPreviewWindow) {
                if(xlsPreviewWindow.extraConfig == undefined) {
                    xlsPreviewWindow.extraConfig={};
                }
                xlsPreviewWindow.extraConfig['masterPreference'] = master;
                xlsPreviewWindow.show();
            }
        }
    },

    genUploadResponse: function(req,succeed,res,moduleName,store,obj,extraParams, extraConfig){
        var msg="Failed to make connection with Web Server";
        var response=eval('('+res.responseText+')');
        if(succeed){
            succeed=response.lsuccess;
            if(succeed){
                var xlsPreviewWindow = Wtf.getCmp("importxls");
                if(xlsPreviewWindow) {
                    if(xlsPreviewWindow.isVisible()){
                        xlsPreviewWindow.close();
                    } else {
                        xlsPreviewWindow.destroy();
                    }
                }

                this.win=new Wtf.SheetViewer1({
                    title: 'Available Sheets',
                    iconCls: 'importIcon',
                    autoScroll:true,
                    plain:true,
                    modal:true,
                    data:response,
                    layout:'border',
                    prevWindow: Wtf.getCmp("importwindow"),
                    moduleName:moduleName,
                    store:store,
                    obj:obj,
                    extraParams: extraParams,
                    extraConfig: extraConfig,
                    fileextension : this.extension
                });
                this.win.show();
                Wtf.getCmp("importwindow").hide();
            }else{
                msg=response.msg;
                if(msg!=undefined && msg.trim()!=""){
                    Wtf.Msg.alert('File Upload',msg);
                }
            }
        }
    }
});


/*----------------------------------------------------------------------------
--------------------------- Imported Files Log  Grid -------------------------
------------------------------------------------------------------------------*/
//--------- function to show tab ----------//
function callImportFilesLog(){
    var panel=Wtf.getCmp('importFilesLog');
    if(panel==null)
    {
        panel = new Wtf.ImportedFilesLog({
            title:'Imported Files Log',
            closable:true,
            layout: "fit",
            border:false,
            iconCls: 'pwnd projectTabIcon',
            id:"importFilesLog"
        });
        mainPanel.add(panel);
    } else {
        panel.dataStore.reload(); //Reload log if already opened
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

//--------- Component Code ----------//
Wtf.ImportedFilesLog = function(config) {
    Wtf.apply(this, config);
    Wtf.ImportedFilesLog.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.ImportedFilesLog, Wtf.Panel, {
    onRender: function(config){
        this.startDate=new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("crm.fromdate.label"),//'From',
            name:'stdate',
            format:WtfGlobal.getOnlyDateFormat(),
            readOnly:true,
            value: this.getDefaultDates(true)
        });

        this.endDate=new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("crm.fromdate.label"),//'To',
            format:WtfGlobal.getOnlyDateFormat(),
            readOnly:true,
            name:'enddate',
            value: this.getDefaultDates(false)
        });

        this.fetchButton = new Wtf.Button({
            text: WtfGlobal.getLocaleText("crm.importlog.toptoolbar.fetchBTN"),//"Fetch",
            scope:this,
            iconCls:'pwnd addfilter',
            tooltip: {text:  WtfGlobal.getLocaleText("crm.importlog.toptoolbar.fetchBTN.ttip")},//"Choose a date range using 'From' and 'To' fields to filter records created in the specified time duration."},
            handler:function(){
                if(this.startDate.getValue()>this.endDate.getValue()){
                    WtfImportMsgBox(1,1);
                    return;
                }
                this.initialLoad();
           }
        }),

        this.resetBttn=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.RESETBUTTON"),//'Reset',
            id: 'btnRec' + this.id,
            scope: this,
            disabled :false,
            tooltip: {text: WtfGlobal.getLocaleText("crm.editor.toptoolbar.resetBTN.ttip")},//'Click to remove any filter settings or search criteria and view all records.'},
            iconCls:'pwndCRM reset'
        });
        this.resetBttn.on('click',this.handleResetClick,this);

        this.columnRec = new Wtf.data.Record.create([
            {name: 'id'},
            {name: 'filename'},
            {name: 'storename'},
            {name: 'failurename'},
            {name: 'log'},
            {name: 'imported'},
            {name: 'total'},
            {name: 'rejected'},
            {name: 'type'},
            {name: 'importon', type:"date"},
            {name: 'module'},
            {name: 'importedby'},
            {name: 'company'}
        ]);

        this.dataStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                totalProperty:'count',
                root: "data"
            },this.columnRec),
            url: Wtf.req.springBase+"common/ImportRecords/getImportLog.do"
        });
        this.columnCm = new Wtf.grid.ColumnModel([
        new Wtf.grid.RowNumberer(),
        {
            header: WtfGlobal.getLocaleText("crm.importlog.header.module"),//"Module",
            dataIndex: "module"
        },{
            header: WtfGlobal.getLocaleText("crm.importlog.header.filename"),//"File Name",
            sortable:true,
            dataIndex: "filename"
        },{
            header: WtfGlobal.getLocaleText("crm.importlog.header.filetype"),//"File Type",
            dataIndex: "type",
            renderer: function(val){
                return Wtf.util.Format.capitalize(val);
            }
        },{
            header:WtfGlobal.getLocaleText("crm.importlog.header.importedby"),//"Imported By",
            sortable:true,
            dataIndex:"importedby"
        },{
            header:WtfGlobal.getLocaleText("crm.importlog.header.importedon"),//"Imported On",
            sortable:true,
            dataIndex:"importon",
            renderer : function(val){
                return val.format("Y-m-d H:i:s");
            }
        },{
            header:WtfGlobal.getLocaleText("crm.importlog.header.totalrecs"),//"Total Records",
            align: "right",
            dataIndex: "total"
        },{
            header: WtfGlobal.getLocaleText("crm.importlog.header.importedrecs"),//"Imported Records",
            align: "right",
            dataIndex: "imported"
        },{
            header: WtfGlobal.getLocaleText("crm.importlog.header.rejectedrecs"),//"Rejected Records",
            align: "right",
            dataIndex: "rejected"
        },{
            header:WtfGlobal.getLocaleText("crm.importlog.header.importlog"),//"Import Log",
            sortable:true,
            dataIndex:"log",
            renderer : function(val){
                return "<div wtf:qtip=\""+val+"\">"+val+"</div>";
            }
        },{
            header:WtfGlobal.getLocaleText("crm.importlog.header.originalfile"),//"Original File",
            sortable:true,
            dataIndex:"imported",
            align: "center",
            renderer : function(val){
                return "<div class=\"pwnd downloadIcon original\" wtf:qtip=\"Download Original File\" style=\"height:16px;\">&nbsp;</div>";
            }
        },{
            header:WtfGlobal.getLocaleText("crm.importlog.header.rejectedfile"),//"Rejected File",
            sortable:true,
            align: "center",
            dataIndex:"rejected",
            renderer : function(val){
                if(val>0){
                    return "<div class=\"pwnd downloadIcon rejected\" wtf:qtip=\"Download Rejected File\" style=\"height:16px;\">&nbsp;</div>";
                }
                return "";
            }
        }
        ]);

        this.sm = new Wtf.grid.RowSelectionModel({singleSelect: true});
        this.grid = new Wtf.grid.GridPanel({
            store: this.dataStore,
            sm:this.sm,
            cm: this.columnCm,
            border : false,
            //loadMask : true,
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.importlog.mtygridwatermark")//"No files were imported between selected dates."
            }),
            bbar: this.pag=new Wtf.PagingToolbar({
                pageSize: 30,
                border : false,
                id : "paggintoolbar"+this.id,
                store: this.dataStore,
                plugins : this.pPageSizeObj = new Wtf.common.pPageSize({
                    id : "pPageSize_"+this.id
                }),
                autoWidth : true,
                displayInfo:true//,
//                items:['-',
//                        new Wtf.Toolbar.Button({
//                            text:'Downloads',
//                            width:200,
//                            scope:this,
//                            menu:[
//                                {
//                                    text: 'Original',
//                                    id: 'Original',
//                                    disabled:true,
//                                    scope:this,
//                                    iconCls:"dwnload",
//                                    handler:function(){
//                                        var rec = this.grid.getSelectionModel().getSelections()[0].data;
//                                        Wtf.get('downloadframe').dom.src = Wtf.req.springBase+'common/ImportRecords/downloadFileData.do?storagename='+rec.storename+'&filename='+rec.filename+'&type='+rec.type;
//                                    }
//                                },
//                                {
//                                    text: 'Rejected',
//                                    id: 'Rejected',
//                                    disabled:true,
//                                    scope:this,
//                                    iconCls:"faileddwnload",
//                                    handler:function(){
//                                        var rec = this.grid.getSelectionModel().getSelections()[0].data;
//                                        var filename = rec.filename;
//                                        var storagename = rec.failurename;
//                                        var type = rec.type;
//                                        if(type=="xls"){
//                                            type = "csv";
//                                            filename = filename.substr(0, filename.lastIndexOf(".")) + ".csv";
//                                            storagename = storagename.substr(0, storagename.lastIndexOf(".")) + ".csv";
//                                        }
//                                        Wtf.get('downloadframe').dom.src = Wtf.req.springBase+'common/ImportRecords/downloadFileData.do?storagename='+storagename+'&filename=Failure_'+filename+'&type='+type;
//                                    }
//                                }
//                            ]
//                        })
//                    ]
            })
        });


        this.grid.on('rowclick',this.handleRowClick,this);

        this.sm.on("selectionchange",function(sm){
//            var sels = this.sm.getSelections();
//            if(sels.length==1){
//                Wtf.getCmp("Original").enable();
//                var rec = sels[0];
//                if(rec.data.rejected>1){
//                    Wtf.getCmp("Rejected").enable();
//                }
//            }else{
//                Wtf.getCmp("Original").disable();
//                Wtf.getCmp("Rejected").disable();
//            }
        },this);

        this.wrapperBody = new Wtf.Panel({
            border: false,
            layout: "fit",
            tbar : [WtfGlobal.getLocaleText("crm.fromdate.label"),this.startDate,"-",WtfGlobal.getLocaleText("crm.fromdate.label"),this.endDate,"-",this.fetchButton,this.resetBttn],
            items : this.grid
        });

        this.initialLoad();
        this.add(this.wrapperBody);
        Wtf.csvFileMappingInterface.superclass.onRender.call(this, config);
    },

    handleResetClick:function(){
        this.startDate.reset();
        this.endDate.reset();

        this.initialLoad();
    },

    handleRowClick:function(grid,rowindex,e){
        if(e.getTarget(".original")){
            var rec = this.grid.getSelectionModel().getSelections()[0].data;
            Wtf.get('downloadframe').dom.src = Wtf.req.springBase+'common/ImportRecords/downloadFileData.do?storagename='+rec.storename+'&filename='+rec.filename+'&type='+rec.type;
        } else if(e.getTarget(".rejected")){
            rec = this.grid.getSelectionModel().getSelections()[0].data;
            var filename = rec.filename;
            var storagename = rec.failurename;
            var type = rec.type;
            if(type=="xls" || type=="xlsx"){
                type = "csv";
                filename = filename.substr(0, filename.lastIndexOf(".")) + ".csv";
                storagename = storagename.substr(0, storagename.lastIndexOf(".")) + ".csv";
            }
            Wtf.get('downloadframe').dom.src = Wtf.req.springBase+'common/ImportRecords/downloadFileData.do?storagename='+storagename+'&filename=Failure_'+filename+'&type='+type;
        }
    },

    initialLoad: function(){
        this.dataStore.baseParams = {
            startdate: this.getDates(true).format("Y-m-d H:i:s"),
            enddate: this.getDates(false).format("Y-m-d H:i:s")
        }
        this.dataStore.load({params : {
            start: 0,
            limit: this.pag.pageSize
        }});
    },

    getDefaultDates:function(start){
        var d=new Date();
        if(start){
            d = new Date(d.getFullYear(),d.getMonth(),1);
        }
        return d;
    },

    getDates:function(start){
        var d=new Date();
        if(start){
            d = this.startDate.getValue();
            d = new Date(d.getFullYear(),d.getMonth(),d.getDate(),0,0,0);
        } else {
            d = this.endDate.getValue();
            d = new Date(d.getFullYear(),d.getMonth(),d.getDate(),23,59,59);
        }
        return d;
    }
});





Wtf.SheetViewer1=function(config){
    Wtf.SheetViewer1.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.SheetViewer1,Wtf.Window,{
    id: 'importxls',
    closable: false,
    width: 750,
    height: 600,
    initComponent:function(config){
        Wtf.SheetViewer1.superclass.initComponent.call(this,config);
        this.prevButton = new Wtf.Button({
            text: WtfGlobal.getLocaleText("crm.import.changeprefBTN"),//"Change Preferences",
            scope: this,
            minWidth: 80,
            handler: function(){
                if(this.prevWindow){
                    this.prevWindow.show();
                }
                this.hide();
            }
        });
        this.nextButton = new Wtf.Button({
            text:WtfGlobal.getLocaleText("crm.NEXTBTN"),// "Next",
            scope: this,
            minWidth: 80,
            disabled: true,
            id: "nextButton"+this.id,
            handler: function(){
                var mappingWindow = Wtf.getCmp("csvMappingInterface");
                if(!mappingWindow) { //For first time dump data
                    this.dumpFileData();
                } else { //For second time check any sheet changes to dump data
                    var sheetRec= this.shgrid.getSelectionModel().getSelected();
                    var rowRec= this.shdgrid.getSelectionModel().getSelected();
                    var currSheetIndex = sheetRec.get('index');
                    var currRowIndex = this.shdgrid.getStore().indexOf(rowRec);

                    var prevSheetIndex = mappingWindow.index;
                    var prevRowIndex = mappingWindow.extraConfig.startindex;
                    if(currSheetIndex!=prevSheetIndex || currRowIndex!=prevRowIndex){
                        this.dumpFileData();
                    } else {
                        this.getMappingInterface();
                    }
                }
            }
        });

        this.cancelButton = new Wtf.Button({
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//"Cancel",
            scope: this,
            minWidth: 80,
            handler: function(){
                closeImportWindow();
            }
        });
        this.buttons = [this.prevButton, this.nextButton, this.cancelButton];
    },

    onRender:function(config){
        Wtf.SheetViewer1.superclass.onRender.call(this,config);
        this.xlsfilename=this.data.file;
        this.onlyfilename=this.data.filename;
        this.sheetIndex=0;
        this.rowIndex=0;
        this.totalColumns=0;
        for(var x=0;x<this.data.data.length;x++){
            this.data.data[x].srow='1';
        }
        var rec=new Wtf.data.Record.create([
            {name:'name'},{name:'index'},{name:'srow'}
        ])
        var store=new Wtf.data.Store({
            reader: new Wtf.data.JsonReader({
                root: "data"
            },rec),
            data:this.data
        });
        this.shgrid=new Wtf.grid.GridPanel({
            viewConfig:{
                forceFit:true
            },
            columns:[{
                header:WtfGlobal.getLocaleText("crm.import.sheetdetail.header.sheetname"),//'Sheet Name',
                dataIndex:'name'
            },{
                header:WtfGlobal.getLocaleText("crm.import.sheetdetail.header.startingrow"),//'Starting Row',
                dataIndex:'srow'
            }],
            store:store
        });

        //Select Default sheet at index 0
        this.shgrid.on("render", function(){
            if(this.shgrid.getStore().getCount()>0){
                this.shgrid.getSelectionModel().selectRow(0);
                this.shgrid.fireEvent("rowclick",this.shgrid,0);
            }
        }, this);

        this.shgrid.on('rowclick',this.showDetail,this);

        var shdrec=new Wtf.data.Record.create([
            {name:'name'}
        ])
        var shdstore=new Wtf.data.Store({
            reader: new Wtf.data.JsonReader({
                root: "fields"
            },shdrec)
        });
        this.shdgrid=new Wtf.grid.GridPanel({
            columns:[],
            store:shdstore
        });
        this.shdgrid.on('rowclick',this.updateStartRow,this);
        this.add({
            region:'north',
            height:70,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getImportTopHtml(WtfGlobal.getLocaleText("crm.import.sheetdetail.tophtml.title"), WtfGlobal.getLocaleText("crm.import.sheetdetail.tophtml.detail"),"../../images/import.png",true, "0px", "7px 0px 0px 10px")
        });
        this.add({
            region:'center',
            layout:'fit',
            autoScroll:true,
            items:this.shgrid
        });
        this.add({
            region:'south',
            height:320,
            layout:'fit',
            autoScroll:true,
            items:this.shdgrid

        });
    },
    highlightonChange:function(rec){
        this.shgrid.getSelectionModel().clearSelections();
        var rowIndex = this.shgrid.getStore().indexOf(rec);
        Wtf.onlyhighLightRow(this.shgrid,"FFFF00",450,rowIndex);
        (function(){this.shgrid.getSelectionModel().selectRow(rowIndex);}).defer(400,this);
    },
    updateStartRow:function(g,i,e){
        if(this.shdgrid.getSelectionModel().getCount()==1){ //Perform on selection of any 1 row.
            var rec = this.shgrid.getSelectionModel().getSelected();
            if(rec!=undefined){
                rec.set('srow',i+1);
                var dt = this.shdgrid.getSelectionModel().getSelected();
                this.highlightonChange(rec);
                var fieldKeys = dt.fields.keys
                var tmpArray=[];

                for(var i=0 ; i < fieldKeys.length ; i++){
                    if(dt.get(dt.fields.keys[i]).trim()!=""){
                        var rec1 = {};
                        var j =i-1;
                        rec1.header = dt.get(dt.fields.keys[i]);
                        rec1.index  = j;
                        if(i>0){
                            tmpArray.push(rec1);
                        }
                    }
                }
                this.Header = tmpArray;
            }
        }
    },

    genUploadResponse12:function(req,succeed,res){
        var msg=WtfGlobal.getLocaleText("crm.customreport.failurerespmsg");//"Failed to make connection with Web Server";
        var response=eval('('+res.responseText+')');
        if(succeed){
            msg=response.msg;
            succeed=response.lsuccess;
            this.Header= response.Header;
            this.xlsParserResponse = response;
            if(succeed){
                this.cursheet=response.index;
                var cm=this.createColumnModel1(response.maxcol);
                var store=this.createStore1(response,cm);
                this.shdgrid.reconfigure(store,cm);
                var rowno=this.shgrid.getStore().getAt(this.shgrid.getStore().find('index',this.cursheet)).get('srow');
                if(rowno)
                    this.shdgrid.getSelectionModel().selectRow(rowno-1);
                this.sheetIndex= response.index;
                this.totalColumns= response.maxcol;
                this.rowIndex= response.startrow;

                if(response.maxcol==0 || response.maxrow==0){ // Disable next button of no. of row=0 or columns=0
                    this.nextButton.disable();
                } else {
                    this.nextButton.enable();
                }
            }else{
                Wtf.Msg.alert('File Import',msg);
            }
        }
        this.shdgrid.enable();
    },

    createColumnModel1:function(cols){
        var fields=[new Wtf.grid.RowNumberer()];
        for(var i=1;i<=cols;i++){
            var temp=i;
            var colHeader="";
            while(temp>0){
                temp--;
                colHeader=String.fromCharCode(Math.floor(temp%26)+"A".charCodeAt(0))+colHeader;
                temp=Math.floor(temp/26);
            }
            fields.push({header:colHeader,dataIndex:colHeader});
        }
        return new Wtf.grid.ColumnModel(fields);
    },

    createStore1:function(obj,cm){
        var fields=[];
        for(var x=0;x<cm.getColumnCount();x++){
            fields.push({name:cm.getDataIndex(x)});
        }

        var rec=new Wtf.data.Record.create(fields);
        var store = new Wtf.data.Store({
            reader: new Wtf.data.JsonReader({
                root: "data"
            },rec),
            data:obj
        });

        return store;
    },


    showDetail:function(g,i,e){
        if(this.shgrid.getSelectionModel().getCount()==1){
            Wtf.getCmp("nextButton"+this.id).enable();
            var rec=this.shgrid.getStore().getAt(i);
            if(this.cursheet&&this.cursheet==rec.get('index'))return;
            this.shdgrid.disable();
            this.sheetIndex = rec.get('index');
            var url = Wtf.req.springBase+"common/ImportRecords/importRecords.do?do=getXLSData";
            if(this.fileextension == "xlsx") {
                url = Wtf.req.springBase+"common/ImportRecords/importRecords.do?do=getXLSXData";
            }
            Wtf.Ajax.request({
                method: 'POST',
                url: url,
    //            url: 'XLSDataExtractor',
                params:{
                    filename:this.xlsfilename,
                    onlyfilename:this.onlyfilename,
                    index:this.sheetIndex
                },
                scope: this,
                success: function(res, req){
                    this.genUploadResponse12(req, true, res);
                },
                failure: function(res, req){
                    this.genUploadResponse12(req, false, res);
                }
            });
        } else {
            Wtf.getCmp("nextButton"+this.id).disable();
        }
    },

    dumpFileData: function(){
        //Create table to dump .xls file data
        var rec1=this.shdgrid.getSelectionModel().getSelected();
        this.rowIndex = this.shdgrid.getStore().indexOf(rec1);
        var url = Wtf.req.springBase+"common/ImportRecords/importRecords.do?do=dumpXLS";
        if(this.fileextension == "xlsx") {
            url = Wtf.req.springBase+"common/ImportRecords/importRecords.do?do=dumpXLSX";
        }
        Wtf.Ajax.timeout=900000;
        Wtf.Ajax.request({
            method: 'POST',
            url: url,
            params:{
                filename: this.xlsfilename,
                onlyfilename: this.onlyfilename,
                index: this.sheetIndex,
                rowIndex: this.rowIndex,
                totalColumns: this.totalColumns
            },
            scope: this,
            success: function(res, req){
                this.getMappingInterface();
                Wtf.Ajax.timeout=30000;
            },
            failure: function(res, req){
                this.getMappingInterface();
                Wtf.Ajax.timeout=30000;
            }
        });
    },

    getMappingInterface:function(g,i,e){
       var rec=this.shgrid.getSelectionModel().getSelected();
       if(this.extraConfig == undefined) {
            this.extraConfig={};
       }
       this.extraConfig['startindex'] = this.rowIndex;

        this.mappingParams = {};
        this.mappingParams.csvheaders = this.Header;
        this.mappingParams.modName = this.moduleName,
        this.mappingParams.moduleid = this.obj.moduleid,
        this.mappingParams.customColAddFlag = this.obj.customColAddFlag,
        this.mappingParams.typeXLSFile = true,
        this.mappingParams.delimiterType = "";
        this.mappingParams.index = rec.get('index');
        this.mappingParams.moduleName = this.moduleName;
        this.mappingParams.store = this.store;
        this.mappingParams.scopeobj = this.obj;
        this.mappingParams.cm = this.obj.EditorColumnArray;
        this.mappingParams.extraParams = this.extraParams;
        this.mappingParams.extraConfig = this.extraConfig;

       Wtf.callMappingInterface(this.mappingParams, Wtf.getCmp("importxls"));
       Wtf.getCmp("importxls").hide();
    }
});

function WtfImportMsgBox(choice, type) {
    var strobj = [];
    switch (choice) {
        case 1:
            strobj = [WtfGlobal.getLocaleText("crm.msg.FAILURETITLE"), WtfGlobal.getLocaleText("crm.importlog.properdateselectionmsg")];//"Please select 'From date' less than 'To date'."];
            break;
        case 2:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"), WtfGlobal.getLocaleText("crm.importlog.columnmapmsg")];//'Please map all columns of date field'];
            break;
        case 3:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"), WtfGlobal.getLocaleText("crm.importlog.mapalert")];//'Please map atleast one of the search columns'];
            break;
        case 43:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.importlog.maphdrmsg")];//"Please map the headers to import."];
            break;
        case 44:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.importlog.mapallhdrmsg")];//"Please map all the mandatory columns."];
            break;
        case 45:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"), WtfGlobal.getLocaleText("crm.importlog.errorimportcsvmsg")];//"An error occurred while importing the records from the csv file. "];
            break;
        case 46:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"), WtfGlobal.getLocaleText("crm.importlog.errorimportfilemsg")];//"An error occurred while importing the records from file."];
            break;
        case 47:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"), WtfGlobal.getLocaleText("crm.importlog.seldelimetermsg")];//"Please select a Delimiter type for CSV."];
            break;
        case 48:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"), WtfGlobal.getLocaleText("crm.importlog.validfileupmsg")];// "Please upload a file with valid file type."];
            break;
        case 50:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"), WtfGlobal.getLocaleText("crm.importlog.validaterecorderrormsg")];//"An error occurred while validating the records from the file."];
            break;
        case 51:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"), WtfGlobal.getLocaleText("crm.importlog.fileuperrormsg")];//"An error occurred while uploading the file."];
            break;
        case 52:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"), WtfGlobal.getLocaleText("crm.importlog.nomatchfoundmsg")];//"No matching pair found."];
            break;
        case 53:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"), WtfGlobal.getLocaleText("crm.importlog.unsupportedxlformatmsg")];//"XLSX format is Microsoft Excel 2007 format which is currently not supported. Please upload a file in Microsoft Excel 2003 format."];
            break;
        default:
            strobj = [choice[0], choice[1]];
            break;
    }

	var iconType = Wtf.MessageBox.INFO;

    if(type == 0)
        iconType = Wtf.MessageBox.INFO;
	if(type == 1)
	    iconType = Wtf.MessageBox.ERROR;
    else if(type == 2)
        iconType = Wtf.MessageBox.WARNING;
    else if(type == 3)
        iconType = Wtf.MessageBox.INFO;

    Wtf.MessageBox.show({
        title: strobj[0],
        msg: strobj[1],
        buttons: Wtf.MessageBox.OK,
//        animEl: 'mb9',
        icon: iconType
    });
}
Wtf.globalStorereload = function(obj, extraConfig) {
    if(extraConfig.masterPreference == '2') {
        var moduleName = extraConfig.moduleName;
        if(moduleName==Wtf.crmmodule.lead) {
            obj.reloadComboStores();
            Wtf.reloadCustomCombo(2);
        } else if(moduleName==Wtf.crmmodule.account) {
             obj.reloadComboStores();
            Wtf.reloadCustomCombo(1);
        } else if(moduleName==Wtf.crmmodule.contact) {
            obj.reloadComboStores();
            Wtf.reloadCustomCombo(6);
        } else if(moduleName==Wtf.crmmodule.opportunity) {
            obj.reloadComboStores();
            Wtf.reloadCustomCombo(5);
        } else if(moduleName==Wtf.crmmodule.product) {
            obj.reloadComboStores();
            Wtf.reloadCustomCombo(4);
        }
    }
}

Wtf.reloadCustomCombo = function(moduleid) {
    var customColumnModel = GlobalColumnModel[moduleid];
    if(customColumnModel) {
        for(var cnt=0;cnt<customColumnModel.length;cnt++) {
            var cStore = GlobalComboStore["cstore"+customColumnModel[cnt].fieldid];
            if(customColumnModel[cnt].fieldtype==4 || customColumnModel[cnt].fieldtype==7) {
                if(cStore != null){
                    cStore.reload();
                }
            }
            if(customColumnModel[cnt].fieldtype==8){// Ref Module Combo
                if(cStore != null){
                    cStore.load();
                }
            }
        }
    }
}

function closeImportWindow(){
    destroyWindow("importwindow");
    destroyWindow("importxls");
    destroyWindow("csvMappingInterface");
    destroyWindow("IWValidationWindow");
}

function destroyWindow(windowId){
    var window = Wtf.getCmp(windowId);
    if(window) {
        if(window.isVisible()){
            window.close();
        } else {
            window.destroy();
        }
    }
}

function getImportTopHtml(text, body,img,isgrid, imagemargin, textmargin){
    if(isgrid===undefined)isgrid=false;
    if(imagemargin===undefined)imagemargin='0';
    if(textmargin===undefined)textmargin='15px 0px 0px 10px';
    if(img===undefined) {
        img = '../../images/import.png';
    }
     var str =  "<div style = 'width:100%;height:100%;position:relative;float:left;'>"
                    +"<div style='float:left;height:100%;width:auto;position:relative;margin:"+imagemargin+";'>"
                    +"<img src = "+img+" style='height:52px;margin:5px;width:40px;'></img>"
                    +"</div>"
                    +"<div style='float:left;height:100%;width:90%;position:relative;'>"
                    +"<div style='font-size:12px;font-style:bold;float:left;margin:"+textmargin+";width:100%;position:relative;'><b>"+text+"</b></div>"
                    +"<div style='font-size:10px;float:left;margin:5px 0px 10px 10px;width:100%;position:relative;'>"+body+"</div>"
                        +(isgrid?"":"<div class='medatory-msg'>* indicates required fields</div>")
                        +"</div>"
                    +"</div>" ;
     return str;
}

function replaceAll(txt, replace, with_this) {
    return txt.replace(new RegExp(replace, 'g'),with_this);
}

