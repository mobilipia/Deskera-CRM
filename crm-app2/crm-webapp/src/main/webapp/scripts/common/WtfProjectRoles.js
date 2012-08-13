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


Wtf.common.AddRole=function(config){
    Wtf.common.AddRole.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.common.AddRole, Wtf.Panel, {
    layout:'fit',
    defaults:{split:true,border:false},
    initComponent:function(config){
        Wtf.apply(this,{
            bbar:[{
                text:'Add Role',
                handler:function(){this.showRoleForm(false)},
                scope:this
            },{
                text:'Edit Role',
                handler:function(){this.showRoleForm(true)},
                scope:this
            },{
                text:'Delete Role',
                handler:this.deleteRole,
                scope:this
            }
            ]
        });
        Wtf.common.AddRole.superclass.initComponent.call(this,config);
    },
    onRender:function(config){
        Wtf.common.AddRole.superclass.onRender.call(this,config);
        this.RoleRecord=new Wtf.data.Record.create(['roleid','rolename','displayrolename']);
        this.RoleStore = new Wtf.data.Store({
            url: Wtf.req.base+'UserManager.jsp',
            baseParams:{
                mode:31
            },
            reader: new Wtf.data.KwlJsonReader({
                        root: 'data'
                    },this.RoleRecord)
        });
        this.RoleGrid=new Wtf.grid.GridPanel({
            store:this.RoleStore,
            sm:new Wtf.grid.RowSelectionModel({singleSelect:true}),
            viewConfig:{forceFit:true},
            layout:'fit',
            columns:[
                {header:'Role Name',dataIndex:'rolename'},
                {header:'Role Display Name',dataIndex:'displayrolename'}
            ]
        });

        this.add(this.RoleGrid);
        this.RoleStore.load();
    },

    showRoleForm:function(isEdit){
        var rec=null;
        if(isEdit){
            if(this.RoleGrid.getSelectionModel().hasSelection()==false){
                Wtf.MessageBox.alert("Edit Role", "Please select a role to edit");
                return;
            }

            rec = this.RoleGrid.getSelectionModel().getSelected();
        }

        this.createRoleWindow(rec,isEdit);
    },

    createRoleWindow:function(rec,isEdit){
        this.form=new Wtf.form.FormPanel({
            frame:true,
            url: Wtf.req.base+'UserManager.jsp?mode=32',
            labelWidth: 125,
            autoHeight:true,
            bodyStyle:'padding:5px 5px 0',
            autoWidth:true,
            defaults: {width: 175},
            defaultType: 'textfield',
            items:[{
                fieldLabel:'Role Name',
                name:'rolename',
                maskRe:/[A-Za-z_]+/,
                allowBlank:false
            },{
                fieldLabel:'Role Display Name',
                name:'displayrolename',
                allowBlank:false
            }],
            buttons:[{
                text:'Save',
                handler:function(){
                    this.form.getForm().submit({
                        waitMsg:'Saving Role...',
                        scope:this,
                        success:function(f,a){this.win.close();this.genSuccessResponse(eval('('+a.response.responseText+')'))},
                        failure:function(f,a){this.win.close();this.genFailureResponse(eval('('+a.response.responseText+')'))}
                    });

                },
                scope:this
            }]
        });
        this.form.add({xtype:'hidden', name:'roleid'})

        this.win=new Wtf.Window({
            title: (isEdit?'Edit':'Add')+' Role',
            closable:true,
            autoWidth:true,
            autoHeight:true,
            plain:true,
            modal:true,
            items:this.form
        });this.win.show();
        if(isEdit)this.form.getForm().loadRecord(rec);
    },

    genSuccessResponse:function(response){
        Wtf.Msg.alert('Role',response.msg);
        if(response.success==true)this.RoleStore.reload();
    },

    genFailureResponse:function(response){
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert('Role',msg);
    },

    deleteRole:function(){
        if(this.RoleGrid.getSelectionModel().hasSelection()==false){
            Wtf.MessageBox.alert("Role deletion", "Please select a role to delete");
            return;
        }
        Wtf.MessageBox.confirm("Role deletion", "Are you sure to delete the selected role?",function(btn){
            if(btn!="yes") return;
            var rec = this.RoleGrid.getSelectionModel().getSelected();
            Wtf.Ajax.requestEx({
                url: Wtf.req.base+'UserManager.jsp',
                params: {
                    mode:33,
                    roleid:rec.get('roleid')
                }
            },this,this.genSuccessResponse,this.genFailureResponse);
        },this); 

    }
}); 
