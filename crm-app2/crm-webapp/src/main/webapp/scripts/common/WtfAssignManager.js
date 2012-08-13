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



Wtf.common.AssignManager=function(config){
    Wtf.apply(this,{
        //        buttons:[{
        //            text:'Apply',
        //            id:'apply',
        //            scope:this,
        //            handler:this.ApplyRole
        //        },{
        //            text:'Cancel',
        //            id:'cancel',
        //            scope:this,
        //            handler:this.cancel
        //        }]
        },config);
        this.buttons = [{
            text:'Save',
            handler:this.saveassignManager,
            scope:this
        },{
            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
            handler: function(){
                this.close();
            },
            scope:this
        }];
    Wtf.common.AssignManager.superclass.constructor.call(this,config);
}

Wtf.extend( Wtf.common.AssignManager,Wtf.Window,{
    title:'Assign Manager',
    id:'Assignmanager',
    layout:'fit',
    closable:true,
    width:600,
    height:500,
    border:false,
    scope:this,
    plain:true,
    iconCls: "pwnd favwinIcon",
    modal:true,
    shadow:true,
    resizable:false,
    onRender:function(config){

        this.sm = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });

        this.record = Wtf.data.Record.create([
        {
            name:'userid'
        },
        {
            name:'username'
        },
        {
            name:'designation'
        },
        {
            name:'fname'
        },
        {
            name:'lname'
        }
        ]);

        this.ds = new Wtf.data.Store({
            url: "Common/ProfileHandler/getAllManagers.do",
            baseParams: {
                mode: 50
            },
            reader: new Wtf.data.KwlJsonReader({
                root: 'data',
                totalProperty:'count'
            },
            this.record
            )
        });
        this.ds.load();

        this.cm2 = new Wtf.grid.ColumnModel(
            [
            this.sm,
            {
                header: "Id",
                dataIndex: 'userid',
                autoWidth : true,
                hidden:true,
                sortable: true

            },
            {
                header: "User Name",
                dataIndex: 'username',
                autoWidth : true,
                sortable: true

            },
            {
                header: "Name",
                dataIndex: 'fullname',
                autoWidth : true,
                sortable: true,
                renderer : function(value,p,record){
                    return (record.data.fname + " " + record.data.lname);
                }
            },
            {
                header: "Designation",
                dataIndex: 'designation', 
                sortable: true
            }
            ]);

//        this.saveBut= new Wtf.Button({
//            text:'Save',
//            handler:this.saveassignManager,
//            scope:this
//        });


        this.recGrid=new Wtf.KwlGridPanel({
            cm:this.cm2,
            store:this.ds,
            sm:this.sm,
            viewConfig: {
                forceFit: true
            },
            searchLabel:WtfGlobal.getLocaleText("crm.customreport.header.quicksearch"),//"Quick Search",
            searchEmptyText:"Search by first name ",
            searchField:"fname"
//            tbar:[
//            this.saveBut
//            ]
        });


        this.headingType="Select Manager for selected Users ";
        this.recPanel= new Wtf.Panel({
            frame:true,
            border: false,
            layout:'fit',
            autoScroll:false,
            items:[{
                border:false,
                region:'center',
                layout:"border",
                buttonAlign:'right',
                items:[{
                    region : 'north',
                    height : 75,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                   // html:getTopHtml(this.headingType,"")
                   html: getTopHtml("Select Manager", "Select Manager for selected Users")
                },{
                    border:false,
                    region:'center',
                    bodyStyle : 'background:#f1f1f1;font-size:10px;',
                    layout:"fit",
                    items: [this.recGrid]
                }]
            }]
        });

        this.add(this.recPanel)
        Wtf.common.AssignManager.superclass.onRender.call(this,config);
    },
    saveassignManager:function(){ 
        if(this.recGrid.getSelectionModel().getCount()==0)
            Wtf.Msg.alert("Error","Please select a user");
        else{
            // this.user=this.usergrid.getSelectionModel().getSelections();
            this.userids=[];
            for(var i=0;i<this.user.length;i++){
                this.userids.push(this.user[i].get('userid'));
            }

            this.man=this.recGrid.getSelectionModel().getSelections();
            this.managerids=[];
            for(var i=0;i<this.man.length;i++){ 
                this.managerids.push(this.man[i].get('userid'));
            }

            Wtf.Ajax.requestEx({
                url: Wtf.req.base+'UserManager.jsp', 
                params:  {
                    mode:51,
                    userid:this.userids,
                    managerid:this.managerids
                }
            },this,this.genSuccessResponse,this.genFailureResponse);
        }
    }, 
    
    genSuccessResponse:function(response){
        Wtf.Msg.alert('Assign Manager',response.msg);
         this.close();
    },
    genFailureResponse:function(response){
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert('Assign Manager',msg);
    }


});

