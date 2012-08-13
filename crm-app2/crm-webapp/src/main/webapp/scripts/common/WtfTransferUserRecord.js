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
Wtf.common.userDelTransfer=function(config){
        Wtf.apply(this,{
            buttons:[{
                text:'Transfer and Delete',
                id:'apply',
                scope:this,
                handler:this.ApplyTransfer
            },{
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                id:'cancel',
                scope:this,
                handler:this.cancel
            }]
        },config);
    Wtf.common.userDelTransfer.superclass.constructor.call(this,config);
}

Wtf.extend( Wtf.common.userDelTransfer,Wtf.Window,{
    title:'Transfer user data',
    id:'tud',
    height:220,
    width:410,
    resizable:false,
    modal:true,
    iconCls: "pwnd favwinIcon",
    shadow:true,
    onRender:function(config){
         var comboReader = new Wtf.data.Record.create([
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        }
        ]);

     this.ownerstore = new Wtf.data.Store({
            url: Wtf.req.base + 'crm.jsp?flag=201&deluserid='+this.userid,
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, comboReader),
            autoLoad:true
        });


    this.ownerCombo=new Wtf.form.ComboBox({
            fieldLabel: 'Transfer to * ',
            id:this.id+'owner',
            allowBlank:false,
            selectOnFocus:true,
            triggerAction: 'all',
            emptyText:"-- Please Select --",
            mode: 'local',
            store: this.ownerstore,
            displayField: 'name',
            valueField:'id',
            anchor:'100%'
        });

this.ownerstore.on('load',function(){
    this.ownerCombo.setValue(loginid)

},this);

        this.transForm= new Wtf.FormPanel({
          //  bodyStyle: "background: transparent; padding: 10 10 10 10 ",
            bodyStyle:"background-color:transparent; padding:15px",
            border:false,
            labelWidth:90,
            autoScroll:true,
            bodyBorder:false,
            width:'90%',
            height:'100%',
            id:'transferuserform',
            defaultType:'textfield',
            items:[                
                   this.ownerCombo
                ]
        });

        this.mainWin=new Wtf.Panel({
            border:false,
            height:160,
            layout:'border',
            items:[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                //html:'<br><div><font size= 2>Assign Role</font></div><br><font size= 1>Asssign Role for the Users</font>',
                html: getTopHtml("Transfer user data", " Select a user to transfer data "),
                layout:'fit'
            },{
                region:'center',
                border:false,
                layout:'fit',
                bodyStyle: "background: rgb(241, 241, 241);",
                items:[this.transForm]
            }]
        });

        
        Wtf.common.userDelTransfer.superclass.onRender.call(this,config);
        this.add(this.mainWin);
        
    },

    
    
    ApplyTransfer:function(){

       Wtf.Ajax.requestEx({
            url: "Common/CRMCommon/deleteUser.do",
            params: {
                mode:13,
                userids:this.userid,
                oldowner:this.userid,
                newowner:this.ownerCombo.getValue()
            }
        },this,this.genSuccessResponse,this.genFailureResponse);
    }, 

    cancel:function(){
        this.close();
    },

    genSuccessResponse:function(response){
        Wtf.Msg.alert('Permission',response.msg);
        this.ds.load();
         this.close();
    },

    genFailureResponse:function(response){
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert('Permission',msg);
    }
});

