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
Wtf.csvMappingInterface = function(config) {
    Wtf.apply(this, config);
    Wtf.csvMappingInterface.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.csvMappingInterface, Wtf.Window, {
    iconCls : 'pwnd favwinIcon',
    width:700,
    height:570,
    modal:true,
    layout:"fit",
    id:'csvMappingInterface',
    closable:false,
    initComponent: function() {
        Wtf.csvMappingInterface.superclass.initComponent.call(this);
    },

    onRender: function(config){
        Wtf.csvMappingInterface.superclass.onRender.call(this, config);
        this.addEvents({
            'importfn':true
        });
        this.title=this.typeXLSFile?"Map XLS headers" : "Map CSV headers";
        // Columns in Module Grid
       if(this.modName=="Leads") {

            var Contact=[];
            for (var i=0;i<this.cm.length;i++) {
                var tmpArray=[];
                var headername=this.cm[i].headerName.trim();
                var header=headerCheck(WtfGlobal.HTMLStripper(this.cm[i].header));
                if(header.substring(header.length-1,header.length)=="*"){
                    header=header.substring(0,header.length-1);
                }
                if(headername=="Email") {
                    tmpArray.push(header);
                    tmpArray.push("100");
                    tmpArray.push(headername);
                    Contact.push(tmpArray);
                } else if(headername=="Name"){
                    tmpArray.push(header);
                    tmpArray.push("255");
                    tmpArray.push(headername)
                    Contact.push(tmpArray);

                } else if(headername=="Phone"){
                    tmpArray.push(header);
                    tmpArray.push("100");
                    tmpArray.push(headername)
                    Contact.push(tmpArray);

                } else if(headername=="Address"){
                    tmpArray.push(header);
                    tmpArray.push("1024");
                    tmpArray.push(headername)
                    Contact.push(tmpArray);
                }
            }

       }else if(this.modName=="Accounts") {

            Contact=[];
            for (i=0;i<this.cm.length;i++) {
                tmpArray=[];
                headername=this.cm[i].headerName.trim();
                header=headerCheck(WtfGlobal.HTMLStripper(this.cm[i].header));
                if(header.substring(header.length-1,header.length)=="*"){
                    header=header.substring(0,header.length-1);
                }
                if(headername=="Account Name") {
                    tmpArray.push(header);
                    tmpArray.push("255");
                    tmpArray.push(headername);
                    Contact.push(tmpArray);
                } else if(headername=="Revenue"){
                    tmpArray.push(header);
                    tmpArray.push("15");
                    tmpArray.push(headername)
                    Contact.push(tmpArray);

                } else if(headername=="Phone"){
                    tmpArray.push(header);
                    tmpArray.push("100");
                    tmpArray.push(headername)
                    Contact.push(tmpArray);

                } else if(headername=="Website"){
                    tmpArray.push(header);
                    tmpArray.push("100");
                    tmpArray.push(headername)
                    Contact.push(tmpArray);
                }
            }

       } else if(this.modName=="Targets") {
         Contact=[
            ['First Name','254'],
            ['Last Name','254'],
            ['Company','254'],
            ['Email','254'],
            ['Phone','39'],
            ['Address','254']
           ];
       } else if(this.modName=="Target Lists") {
         Contact=[
            ['First Name','254'],
            ['Last Name','254'],
            ['Company','254'],
            ['Email','254'],
            ['Phone','39'],
            ['Address','254']
           ];
       } else {
            Contact=[];
            for (i=0;i<this.cm.length;i++) {
                tmpArray=[];
                headername=this.cm[i].headerName.trim();
                header=headerCheck(WtfGlobal.HTMLStripper(this.cm[i].header));
                if(header.substring(header.length-1,header.length)=="*"){
                    header=header.substring(0,header.length-1);
                }
                if(headername=="First Name") {
                    tmpArray.push(header);
                    tmpArray.push("254");
                    tmpArray.push(headername);
                    Contact.push(tmpArray);
                } else if(headername=="Last Name"){
                    tmpArray.push(header);
                    tmpArray.push("254");
                    tmpArray.push(headername)
                    Contact.push(tmpArray);

                } else if(headername=="Email"){
                    tmpArray.push(header);
                    tmpArray.push("254");
                    tmpArray.push(headername)
                    Contact.push(tmpArray);

                } else if(headername=="Phone"){
                    tmpArray.push(header);
                    tmpArray.push("39");
                    tmpArray.push(headername)
                    Contact.push(tmpArray);
                } else if(headername=="Address"){
                    tmpArray.push(header);
                    tmpArray.push("254");
                    tmpArray.push(headername)
                    Contact.push(tmpArray);
                }
            }
      }
        this.columnDs=new Wtf.data.SimpleStore({
             fields:[{
                name: 'columnname'
            }]
        });

        this.columnDs.loadData(Contact);

        this.columnCm = new Wtf.grid.ColumnModel([
        {
            header: "Columns",
            dataIndex: "columnname",
            renderer:function(a,b,c){
                var qtip="";var style="";
                if(c.get("allownull")=="NO"){
                    style += "font-weight:bold;color:#500;";
                    qtip += "Allow Null False";
                }
                if(c.get("key")=="PRI"){
                    style += "font-weight:bold;color:#050;";
                    qtip = "Primary Key Column<br/>"+qtip;
                }
                return "<span wtf:qtip='"+qtip+"' style='cursor:pointer;"+style+"'>"+a+"</span>";
            }
        }
        ]);
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
            loadMask : true,
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:"Select module to list columns"
            })
        });


        //Mapped Columns Grid
        this.mappedColsData="";
        this.mappedRecord = new Wtf.data.Record.create([
        {
            name: "columnname",
            type: 'string'
        },

        {
            name: "allownull",
            type: 'string'
        },

        {
            name: "key",
            type: 'string'
        },

        {
            name: "index"
        }
        ]);

        this.mappedColsDs = new Wtf.data.JsonStore({
            jsonData : this.mappedColsData,
            reader: new Wtf.data.JsonReader({
                root:"data"
            }, this.mappedRecord)

        });

        var mappedColsCm = new Wtf.grid.ColumnModel([{
            header: "Mapped Columns",
            dataIndex: 'columnname'
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
            loadMask : true,
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:"Drag and Drop columns here"
            })
        });

        // CSV header from csv file Grid
        this.csvHeaderDs = new Wtf.data.JsonStore({
            fields: [{
                name:"header"
            },{
                name:"index"
            }]
        });
        this.csvHeaderDs.loadData(this.csvheaders);
        this.csvHeaderDs.on("load",function(){
            this.totalHeaders=this.csvHeaderDs.getCount();
        },this);
        var headerName = "CSV Headers";
        var emptyGridText = "CSV Headers from given CSV file";
        if(this.typeXLSFile){
            headerName="XLS Headers"
            emptyGridText = "XLS Headers from given CSV file";
        }
        var csvHeaderCm = new Wtf.grid.ColumnModel([{
            header: headerName,
            dataIndex: 'header'
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
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:emptyGridText
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
            header: "Mapped Headers",
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
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:"Drag and Drop Headers here"
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
                    html:getTopHtml("Map Headers","Drag-and-drop '"+headerName+"' fields to the 'Mapped Headers' list and subsequently Drag-and-drop corresponding 'Table Columns' field to the 'Mapped Columns' list.","../../images/link2.jpg")
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
                    columnWidth:.25,
                    border:false,
                    layout:"fit",
                    autoScroll:true,
                   // title:headerName,
                    items:this.csvHeaderGrid
                },{
                    xtype:"panel",
                    columnWidth:.25,
                    border:false,
                    layout:"fit",
                    autoScroll:true,
                   // title:"Mapped Headers",
                    items:this.mappedCsvHeaderGrid
                },{
                    xtype:"panel",
                    columnWidth:.25,
                    border:false,
                    layout:"fit",
                    autoScroll:true,
                  //  title:"Mapped Columns",
                    items:this.mappedColsGrid
                },{
                    xtype:"panel",
                    columnWidth:.25,
                    border:false,
                    layout:"fit",
                    autoScroll:true,
                 //   title:"Table Columns",
                    items:this.tableColumnGrid
                }
                ]
            }
            ],
            buttonAlign: 'right',
            buttons:[{
                text: 'Previous',
                handler: function(){
                    this.impWin1.show();
                    this.close();
               },
                scope:this
            },{
                text: WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                handler: function(){
                    if(this.mappedCsvHeaderDs.getCount()==0 && this.mappedColsDs.getCount()==0)
                        WtfComMsgBox(1000);
                    else {
                        this.impWin1.destroy();
                        var totalmappedHeaders = this.mappedCsvHeaderDs.getCount();
                        var totalMappedColumns = this.mappedColsDs.getCount();
                        if(totalmappedHeaders==totalMappedColumns){
                            this.generateJsonForXML();
                            if(this.typeXLSFile){
                                this.fireEvent('importfn',this.mappingJSON,this.index,this.moduleName,this.store,this.contactmapid,this.targetlistPagingLimit,this.scopeobj);
                            }else {
                                this.fireEvent('importfn',this.mappingJSON,this.delimiterType);
                            }
                            this.close();
                        } else {
                            WtfComMsgBox(["Header Mapping", "Please select column for selected header"], 0);
                        }
                    }
                },
                scope:this
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                handler: function(){
                    this.impWin1.destroy();
                    this.close();
                },
                scope: this
            }]
        });


        this.on("afterlayout",function(){
            function rowsDiff(store1,store2){
                return diff=store1.getCount()-store2.getCount();
            }

            function unMapRec(atIndex){
                var headerRec = mappedHeaderStore.getAt(atIndex);
                if(headerRec!==undefined){
                    mappedHeaderStore.remove(headerRec);
                    headerStore.add(headerRec);
                }

                var columnRec = mappedColumnStore.getAt(atIndex);
                if(columnRec!==undefined){
                    mappedColumnStore.remove(columnRec);
                    columnStore.add(columnRec);
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
                copy       : true,
                notifyDrop : function(ddSource, e, data){
                    function mapHeader(record, index, allItems) {
                        if(rowsDiff(mappedHeaderStore,mappedColumnStore)==0){
                            if(columnStore.getCount()!=0){
                                mappedHeaderStore.add(record);
                                ddSource.grid.store.remove(record);
                            } else {
                                WtfComMsgBox(["Header Mapping", "No column for mapping"], 0);
                            }
                        }else{
                            WtfComMsgBox(["Header Mapping", "Please map previous header"], 0);
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
                copy       : true,
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
                            WtfComMsgBox(["Header Mapping", "Please select header first"], 0);
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

    generateJsonForXML : function(){
        var tablename = "";
        if(this.modName=="Leads") {
            tablename = "crm_lead";
        } else if(this.modName=="Accounts"){
            tablename = "crm_account";
        } else if(this.modName=="Targets"){
            tablename = "target_module";
        } else {
            tablename = "crm_contact";
        }
        this.mappingJSON = "";
        for(var i=0;i<this.mappedCsvHeaderDs.getCount();i++){
            this.mappingJSON+="{\"csvindex\":\""+this.mappedCsvHeaderDs.getAt(i).get("index")+"\",\n\
                                        \"csvheader\":\""+this.mappedCsvHeaderDs.getAt(i).get("header")+"\",\n\
                                         \"columnname\":\""+this.mappedColsDs.getAt(i).get("columnname")+"\",\n\
                                        \"datatruncation\":\""+this.mappedColsDs.data.items[i].json[1]+"\",\n\
                                        \"colHeader\":\""+this.mappedColsDs.data.items[i].json[2]+"\",\n\
                                        \"reftablename\":\""+tablename+"\"},";
        }
        this.mappingJSON = this.mappingJSON.substr(0, this.mappingJSON.length-1);
        this.mappingJSON = "{\"root\":["+this.mappingJSON+"]}";
    }
});


