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


Wtf.common.Roles=function(config){
    Wtf.apply(this,{
        buttons:[{
            text:'Apply',
            id:'apply',
            scope:this,
            handler:this.ApplyRole
        },{
            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
            id:'cancel',
            scope:this,
            handler:this.cancel
        }]
    },config);
    Wtf.common.Roles.superclass.constructor.call(this,config);
}

Wtf.extend( Wtf.common.Roles,Wtf.Window,{
    title:'Assign Role',
    id:'AR',
    height:300,
    width:400,
    iconCls: "pwnd favwinIcon",
    modal:true,
    shadow:true,
    onRender:function(config){
        this.roleRecord=new Wtf.data.Record.create(
            ['roleid','rolename','displayrolename','isadmin']
            );

        this.roleStore = new Wtf.data.Store({
            url: Wtf.req.base+'UserManager.jsp',
            baseParams:{
                mode:31    
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            },this.roleRecord)
        });

        this.userRoleStore = new Wtf.data.Store({
            url: Wtf.req.base+'UserManager.jsp',
            baseParams:{
                mode:36,
                userid:this.userid
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            },this.roleRecord)
        });
        this.userRoleStore.load();
        this.roleStore.load();
        Wtf.common.Roles.superclass.onRender.call(this,config);
        this.roleStore.on('load',function(){
            this.createWindow()
        },this)


       
        
    },

    createWindow:function(){
        this.createForm();
        this.MainWinPanel= new Wtf.Panel({
            border:false,
            height:333,
            layout:'border',
            items:[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                //html:'<br><div><font size= 2>Assign Role</font></div><br><font size= 1>Asssign Role for the Users</font>',
                html: getTopHtml("Assign Role", "Asssign Role for the Users "),
                layout:'fit'
            },{
                region:'center',
                border:false,
                layout:'fit',
                bodyStyle: "background: rgb(241, 241, 241);",
                items:[this.AssRoleForm]
            }]
        });
        this.add(this.MainWinPanel);
        this.doLayout();
    },

    createForm:function(){
        this.AssRoleForm= new Wtf.FormPanel({
            region:'center',
            cls:'x-panel-body x-panel-body-noheader x-panel-body-noborder',
            bodyStyle: "background: transparent;",
            border:false,
            labelWidth:150,
            autoScroll:true,
            bodyBorder:false,
            style: "background: transparent;",
            width:'100%',
            height:'100%',
            id:'AssRoleForm',
            defaultType:'textfield',
            items:[{
                name:'userid',
                xtype:'hidden'
            }]
        });

        this.roleSet = new Wtf.form.FieldSet({
            id:'RoleSet',
            xtype:"fieldset",
            style:'margin:10px',
            autoHeight:true,
            title:'Roles'
        })
       var roleid=this.userRoleStore.getAt(0).get('roleid');
       var isadmin=this.userRoleStore.getAt(0).get('isadmin');
        for(var i=0;i<this.roleStore.getCount();i++)
        {
            var rec=this.roleStore.getAt(i);            
            this.roleSet.add(new Wtf.form.Checkbox({
                fieldLabel: rec.get('displayrolename'),
                name: "rolegroup"+rec.get('roleid'),
                id:"roleid"+rec.get('roleid'),
                checked:this.isChecked(roleid,i),
                disabled:(i == 0 ?isadmin:false),
                autoHeight:true,
                width:150
            })
            )
        }
        this.AssRoleForm.add(this.roleSet)
    },
    isChecked:function(roleid,index){                
            if( ( Math.pow(2,index) & roleid )== Math.pow(2,index))
                return true;
            else
                return false;        
    },
    ApplyRole:function(){
        var bit=0;
        for(var x=0;x<this.roleStore.getCount();x++){
            var rec=this.roleStore.getAt(x);
            if(Wtf.getCmp("roleid"+rec.get('roleid')).checked)
            {
                bit+=Math.pow(2,x);
              
            }            
        }
        
        Wtf.Ajax.requestEx({
            url: Wtf.req.base+'UserManager.jsp',
            params: {
                mode:35,
                userid:this.userid,
                bit:bit
            }
        },this,this.genSuccessResponse,this.genFailureResponse);
    },  

    cancel:function(){  
        this.close();
    },

    genSuccessResponse:function(response){
        this.ugrid.getStore().reload();
        ResponseAlert(['Assign Role',response.msg]);
        this.close();
    }, 
 
    genFailureResponse:function(response){ 
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
           ResponseAlert(['Assign Role',msg]);
    }
}); 

