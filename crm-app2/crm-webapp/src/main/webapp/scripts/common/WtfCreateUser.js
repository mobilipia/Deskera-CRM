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
Wtf.common.CreateUser=function(config){
    this.userinfo= new Wtf.form.FormPanel({
        fileUpload:true,
        baseParams:{
            mode:12
        },
        url:"Common/ProfileHandler/saveUser.do",
        region:'center',
        cls:'x-panel-body x-panel-body-noheader x-panel-body-noborder',
        bodyStyle: "background: transparent;",
        border:false,
        bodyBorder:false,
        style: "background: transparent;padding-left:20px;padding-top: 20px;padding-right: 0px;",
        width:'100%',
        height:'100%',
        id:'userinfo',
        defaultType:'striptextfield',
        items:[{        
            name:'userid',
            xtype:'hidden'
        },{
            fieldLabel:'User Name *',
            name:'username',
            readOnly:config.isEdit,
            width:'75%',
            maxLength:30,
            allowBlank:false
        },{
            fieldLabel:'E-Mail *',
            name:'emailid',
            maxLength:50,
            allowBlank:false,
            width:'75%',
            regex:Wtf.ValidateMailPatt
        },{
            fieldLabel: 'First Name *',
            name: 'fname',
            id:'fname',
            maxLength:32,
            width:'75%',
            allowBlank:false
        },{
            fieldLabel: 'Last Name *',
            name: 'lname',
            id:'lname',
            maxLength:32,
            width:'75%',
            allowBlank:false
        },{
            fieldLabel: 'User Picture',
            name:'userimage',
            inputType:'file',
            id:'userimage'
        },{
            fieldLabel: 'Contact No ',
            name: 'contactno',
            width:'75%',
            regex:Wtf.PhoneRegex,
            maxLength:30,
            id:'contactno'
        },{
            fieldLabel: 'Address',
            name: 'address',
            width:'75%',
            id:'address',
            maxLength:50,
            xtype:'textarea'
        }]
    });

    this.MainWinPanel= new Wtf.Panel({
        border:false,
        autoScroll:true,
        layout:'border',
        items:[{
            region : 'north',
            height : 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(''+(config.record==null?'Create':'Edit')+' User', "Enter user details "),
            layout:'fit'
        },{
            region:'center',
            id:'center1',
            border:false,
            layout:'fit',
            bodyStyle: "background: rgb(241, 241, 241);",
            items:[this.userinfo]
        }]
    });

    this.win=new Wtf.Window({
        title:'User',
        id:'CNU',
        height:430,
        width:450,
        layout:'fit',
        scope:this,
        modal:true,
        resizable:false,
        iconCls: "pwnd favwinIcon",
        buttonAlign:'right',
        listeners:{scope:this,close:function(){this.fireEvent('close')}},
        items:[this.MainWinPanel],
        buttons: [{
            text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
            scope:this,
            handler:function(){
                if(this.userinfo.getForm().isValid()){
                    this.userinfo.getForm().submit({
                        waitMsg:'Saving user information',
                        success:function(f,a){
                            var obj = eval('('+a.response.responseText+')');
                            if(obj.success) {
                                this.win.close();
                                this.genSuccessResponse(obj)
                            } else {
                                Wtf.Msg.alert('Create User',obj.msg);
                            }
                        },
                        failure:function(f,a){
                            this.genFailureResponse(eval('('+a.response.responseText+')'))
                            },
                        scope:this
                    });
                }else{
                         WtfComMsgBox(61,0);
            }
            }
        },{
            text: 'Close',
            scope:this,
            handler:function(){
                this.fireEvent('close');
                this.win.close();
            }
        }]
    });
    this.win.show();
    Wtf.apply(this,config);
    if(this.record!=null)this.userinfo.getForm().loadRecord(this.record);
    Wtf.common.CreateUser.superclass.constructor.call(this,config);
    this.addEvents({
        'save':true,
        'close':true,
        'notsave':true        
    });
}

Wtf.extend( Wtf.common.CreateUser,Wtf.Panel,{
    onRender:function(config){
        Wtf.common.CreateUser.superclass.onRender.call(config);
    },
    cancel:function(){
        this.win.close();
    },
    genSuccessResponse:function(response){
        this.fireEvent('save',response);
        this.fireEvent('close');
    },
    genFailureResponse:function(response){
        var msg="Failed to make connection with Web Server";
        this.fireEvent('close');
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert('Create User',msg);
    }
}); 
  
