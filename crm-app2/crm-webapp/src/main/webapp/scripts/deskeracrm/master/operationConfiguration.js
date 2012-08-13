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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Wtf.OperationConfigurator = function (config){
    Wtf.apply(this,config);
    Wtf.OperationConfigurator.superclass.constructor.call(this);
}

Wtf.extend(Wtf.OperationConfigurator,Wtf.Panel,{
    initComponent:function (){
        Wtf.OperationConfigurator.superclass.initComponent.call(this);
        this.getMasterGrid();
        this.getMasterDataGrid();
        this.mainPanel = new Wtf.Panel({
            layout:"border",
            border:false,
            items:[
                this.masterDataGrid,
                this.masterGrid
            ]
        });

        this.masterSm.on("selectionchange",function(){
            if(this.masterSm.getSelected()){
                this.masterDataAdd.enable();
                this.masterEdit.enable();
                this.masterDataStore.load({
                    params:{
                        operationid:this.masterGrid.getSelectionModel().getSelected().get("id")
                    }
                });
            } else {
                this.masterDataAdd.disable();
                this.masterEdit.disable();
            }
        },this);

        this.add(this.mainPanel);
    },
    getMasterGrid:function (){
        this.masterRec = new Wtf.data.Record.create([
            {name:"id"},
            {name:"name"},
            {name:"parentid"}
        ]);

        this.masterReader = new Wtf.data.KwlJsonReader({
            root:"data",
            totalProperty:"count"
        },this.masterRec);

        this.masterStore = new Wtf.data.Store({
            url: Wtf.req.base + "starbucks/itemHandler.jsp",
            reader:this.masterReader,
            baseParams:{
                flag:28,
                type:31
            }
        });

        this.masterStore.load();

        this.masterColumn = new Wtf.grid.ColumnModel([
            {
                header:"Field",
                dataIndex:"name"
            }
        ]);

        this.masterAdd = new Wtf.Toolbar.Button({
            text:"Add Master",
            handler:function (){
                this.AddMaster("Add");
            },
            scope:this
        });

        this.masterEdit = new Wtf.Toolbar.Button({
            text:"Edit Master",
            handler:function (){
                this.AddMaster("Edit");
            },
            scope:this,
            disabled:true
        });


        this.masterGrid = new Wtf.grid.GridPanel({
            sm:this.masterSm = new Wtf.grid.RowSelectionModel(),
            region:"west",
            width:300,
            store:this.masterStore,
            cm:this.masterColumn,
            loadMask:true,
            viewConfig:{
                forceFit:true
            },
            tbar:[
                this.masterAdd,
                "-",
                this.masterEdit
            ]
        });
    },
    getMasterDataGrid:function (){
        this.masterDataRec = new Wtf.data.Record.create([
            {name:"id"},
            {name:"name"},
            {name:"parentid"},
            {name:"configid"}
        ]);

        this.masterDataReader = new Wtf.data.KwlJsonReader({
            root:"data",
            totalProperty:"count"
        },this.masterDataRec);

        this.masterDataStore = new Wtf.data.Store({
            url: Wtf.req.base + "starbucks/itemHandler.jsp",
            reader:this.masterDataReader,
            baseParams:{
                flag:44,                
                type:1
            }
        });

        this.masterDataColumn = new Wtf.grid.ColumnModel([
            {
                header:"Field",
                dataIndex:"name"
            }
        ]);

        this.masterDataAdd = new Wtf.Toolbar.Button({
            text:"Add Master",
            handler:function (){
                this.AddMasterData("Add");
            },
            disabled:true,
            scope:this
        });

        this.masterDataEdit = new Wtf.Toolbar.Button({
            text:"Edit Master",
            handler:function (){
                this.AddMasterData("Edit");
            },
            scope:this,
            disabled:true
        });


        this.masterDataGrid = new Wtf.grid.GridPanel({
            sm:this.masterDataSm = new Wtf.grid.RowSelectionModel(),
            store:this.masterDataStore,
            region:"center",
            loadMask:true,
            cm:this.masterDataColumn,
            viewConfig:{
                forceFit:true
            },
            tbar:[
                this.masterDataAdd,
                "-",
                this.masterDataEdit
            ]
        });
        this.masterDataSm.on("selectionchange",function (){
            if(this.masterDataSm.getSelected()){
                this.masterDataEdit.enable();
            } else {
                this.masterDataEdit.disable();
            }
        },this);
    },
    AddMaster:function (action){
        new Wtf.AddEditMaster({
            title:action +' Master Field',
            layout:"fit",
            modal:true,
            width:400,
            height:230,
            action:action,
            rec:this.masterSm.getSelected(),
            store:this.masterStore
        }).show();
    },
    AddMasterData:function (action){
        var id = this.masterSm.getSelected().get("id");
        var name = this.masterSm.getSelected().get("name");
        new Wtf.categorySelectWindow({
            title : "Operations Map Window",
            closable : true,
            modal : true,
            width : 600,
            height: 525,
            layout : 'fit',
            group_id:"12",
            id:id,
            name:name,
            store:this.masterDataStore,
            //createFor : 'Contributor',
            resizable :false
        }).show();
    },
    DeleteMasterData:function (){
        Wtf.Ajax.requestEx({
            url: Wtf.req.base + "starbucks/itemHandler.jsp",
            params:{
                flag:30,
                id:this.masterDataSm.getSelected().get("id")
            }
        },
        this,
        function (){
            Wtf.MessageBox.show({
                title:"Status",
                msg:"Master field deleted successfully",
                icon:Wtf.MessageBox.INFO,
                buttons:Wtf.MessageBox.OK
            });
            this.masterDataStore.load({
                params:{
                    configid:this.configid
                }
            });
        },function (){
            Wtf.MessageBox.show({
                title:"Status",
                msg:"Error while deleting master field",
                icon:Wtf.MessageBox.ERROR,
                buttons:Wtf.MessageBox.OK
            });
        });
    },
    DeleteMaster:function (){
        Wtf.Ajax.requestEx({
            url: Wtf.req.base + "starbucks/itemHandler.jsp",
            params:{
                flag:29,
                id:this.masterSm.getSelected().get("id")
            }
        },
        this,
        function (){
            Wtf.MessageBox.show({
                title:"Status",
                msg:"Master field deleted successfully",
                icon:Wtf.MessageBox.INFO,
                buttons:Wtf.MessageBox.OK
            });
            this.masterStore.load();
        },function (){
            Wtf.MessageBox.show({
                title:"Status",
                msg:"Error while deleting master field",
                icon:Wtf.MessageBox.ERROR,
                buttons:Wtf.MessageBox.OK
            });
           });
      }
});

