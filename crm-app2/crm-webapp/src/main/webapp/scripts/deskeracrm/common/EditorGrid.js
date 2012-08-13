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

Wtf.taskManager = function (config){
    Wtf.apply(this,config);
    Wtf.taskManager.superclass.constructor.call(this);
}

Wtf.extend(Wtf.taskManager,Wtf.Panel,{
    initComponent:function (){
        Wtf.taskManager.superclass.initComponent.call(this);
        this.getEditorGrid();
        this.tmpPanel = new Wtf.Panel({
           
            border:false,
            layout:'fit',
            items:[
            this.EditorGrid
            ]
        });
        this.add(this.tmpPanel);
    },
    getEditorGrid:function (){
       
        this.EditorRec = new Wtf.data.Record.create([
        {
            name:"id"
        },

        {
            name:"businessname"
        },

        {
            name:"ownername"
        },

        {
            name:"phoneno1"
        },

        {
            name:"phoneno2"
        },

        {
            name:"email"
        },

        {
            name:"faxno"
        },
        {
            name:"address"
        }
        ]);

        this.EditorReader = new Wtf.data.KwlJsonReader({
            root:"data",
            totalProperty:"count"
        },this.EditorRec);

        this.EditorStore = new Wtf.data.Store({
            url:"Json/demo.json",
            reader:this.EditorReader
        });
        this.addRec();
        //this.EditorStore.load();
        this.EditorStore.on("load",this.addRec,this);
        this.EditorColumn = new Wtf.grid.ColumnModel([
        {
            header:"Business Name",
            sortable:true,
            dataIndex:"businessname",
            editor:new Wtf.form.TextField()
        },
        {
            header:"Owner Name",
            sortable:true,
            dataIndex:"ownername",
            editor:new Wtf.form.TextField()
        },
        {
            header:"Phone No1",
            sortable:true,
            dataIndex:"phoneno1",
            editor:new Wtf.form.NumberField()
        },
        {
            header:"Phone No2",
            sortable:true,
            dataIndex:"phoneno2",
            editor:new Wtf.form.NumberField()
        },
        {
            header:"Email",
            sortable:true,
            dataIndex:"email",
            editor:new Wtf.form.TextField()
        },
        {
            header:"Fax No",
            sortable:true,
            dataIndex:"faxno",
            editor:new Wtf.form.TextField()
        },
        {
            header:"Address",
            sortable:true,
            dataIndex:"address",
            editor:new Wtf.form.TextField()
        }
        ]);

        this.addBut = new Wtf.Toolbar.Button({
            text:"Save Leads",
            scope:this,
            handler:this.saveData
        });

        this.EditorGrid = new Wtf.grid.EditorGridPanel({
            cm:this.EditorColumn,
            store:this.EditorStore,
            tbar:[
                this.addBut
            ],
            viewConfig:{
                forceFit:true
            },
            clicksToEdit:1,
            border:false
        });

        this.EditorGrid.on("afteredit",this.fillGridValue,this);
        this.EditorGrid.on("cellclick",this.cellclick,this);
      //  this.EditorGrid.getColumnModel().setHidden(6,true);
    },
    addRec:function (){
        this.newRec = new this.EditorRec({
            businessname:"",
            ownername:"",
            phoneno1:"",
            phoneno2:"",
            email:"",
            faxno:"",
            address:""
        });

        this.EditorStore.add(this.newRec);
    },
    fillGridValue:function (e){
        if(e.row == this.EditorStore.getCount()-1){
            this.addRec();
        }
//        for(var i=0;i<this.itemEditorStore.getCount();i++){
//            if(this.itemEditorStore.getAt(i).get("id") == e.value){
//                this.EditorStore.getAt(e.row).set("id",e.value);
//                this.EditorStore.getAt(e.row).set("itemcode",this.itemEditorStore.getAt(i).get("itemcode"));
//                this.EditorStore.getAt(e.row).set("itemdescription",this.itemEditorStore.getAt(i).get("itemdescription"));
//                this.EditorStore.getAt(e.row).set("uom",this.itemEditorStore.getAt(i).get("uom"));
//            }
//
//        }
//
//        for(i=0;i<this.markoutStore.getCount();i++){
//            if(this.markoutStore.getAt(i).get("id") == e.value){
//
//                this.EditorStore.getAt(e.row).set("markoutid",e.value);
//                this.EditorStore.getAt(e.row).set("markouttype",this.markoutStore.getAt(i).get("name"));
//
//                if(this.markoutStore.getAt(i).get("name")=="Markout")
//                    {
//                        this.EditorGrid.getColumnModel().setHidden(6,false);
//                        this.EditorGrid.getColumnModel().setHidden(5,true);
//
//                        this.doLayout();
//                      //  this.EditorGrid.getColumnModel().setEditable(5,false);
//
//                    }
//                    else
//                     {
//                        this.EditorGrid.getColumnModel().setHidden(5,false);
//                        this.EditorGrid.getColumnModel().setHidden(6,true);
//                        this.doLayout();
//                    }
//
//            //this.EditorColumn.setEditor(2,new Wtf.form.NumberField());
//            }
//        }
//        for(i=0;i<this.reasonStore.getCount();i++){
//            if(this.reasonStore.getAt(i).get("id") == e.value){
//
//                this.EditorStore.getAt(e.row).set("reasonid",e.value);
//                this.EditorStore.getAt(e.row).set("reason",this.reasonStore.getAt(i).get("name"));
//
//            //this.EditorColumn.setEditor(2,new Wtf.form.NumberField());
//            }
//        }
    },
    cellclick:function(e,row,column)
    {
        //this.EditorStore.data.items[0].get("markouttype")

//                if(this.EditorStore.data.items[row].get("itemcode")=="")
//                    {
//                        this.EditorGrid.getColumnModel().setEditable(3,false);
//                        this.EditorGrid.getColumnModel().setEditable(4,false);
//                        this.EditorGrid.getColumnModel().setEditable(5,false);
//                        this.EditorGrid.getColumnModel().setEditable(6,false);
//
//                    }
//                    else
//                        {
//                        this.EditorGrid.getColumnModel().setEditable(3,true);
//                        this.EditorGrid.getColumnModel().setEditable(4,true);
//                        this.EditorGrid.getColumnModel().setEditable(5,true);
//                        this.EditorGrid.getColumnModel().setEditable(6,true);
//                    }
//
//        if(column==5||column==6)
//            {
//                if(this.EditorStore.data.items[row].get("markouttype")=="Markout")
//                    {
//                        this.EditorGrid.getColumnModel().setHidden(6,false);
//                        this.EditorGrid.getColumnModel().setHidden(5,true);
//                        this.EditorStore.getAt(row).set("reason","")
//
//
//                    }
//                 else
//                    {
//                        this.EditorGrid.getColumnModel().setHidden(5,false);
//                        this.EditorGrid.getColumnModel().setHidden(6,true);
//                    }
//            }
    },
    saveData:function(){       
         
        var jsondata="";
        for(var i=0;i<this.EditorStore.getCount()-1;i++){
            jsondata += "{'id':'" + this.EditorStore.getAt(i).get("id") + "',";
            jsondata += "'businessname':'" + this.EditorStore.getAt(i).get("businessname") + "',";
            jsondata += "'ownername':'" + this.EditorStore.getAt(i).get("ownername") + "',";
            jsondata += "'phoneno1':'" + this.EditorStore.getAt(i).get("phoneno1") + "',";
            jsondata += "'phoneno2':'" + this.EditorStore.getAt(i).get("phoneno2") + "',";
            jsondata += "'email':'" + this.EditorStore.getAt(i).get("email") + "',";
            jsondata += "'faxno':'" + this.EditorStore.getAt(i).get("faxno") + "',";
            jsondata += "'address':'" + this.EditorStore.getAt(i).get("address") + "'},";
        }
         var trmLen = jsondata.length - 1;
        var finalStr = jsondata.substr(0,trmLen);
        alert(finalStr);
        Wtf.Ajax.requestEx({
            url: Wtf.req.base + "Lead.jsp",
            params: {
                    flag:1,
                    jsondata:finalStr
                }
        }, this,
        function(response){
           Wtf.Msg.alert("Success","Leads data save successfully")
           mainPanel.remove(this);
        },
        function(response){
            alert("sdfsdf");
        })
    }
});
