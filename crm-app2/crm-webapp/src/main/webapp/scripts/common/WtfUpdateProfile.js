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

Wtf.common.UpdateProfile = function(config){
    Wtf.apply(this,{
        title:WtfGlobal.getLocaleText("crm.userprofile.title"),//'My Account',
        id:'updateProfileWin',
        closable: true,
        modal: true,
        iconCls: "pwnd favwinIcon",
        width: 500,
        height:590,
        resizable: false,
        layout: 'border',
        buttonAlign: 'right',
        renderTo: document.body,
        buttons: [{
            text: WtfGlobal.getLocaleText("crm.updateButton.caption"),//'Update',
            scope: this,
            handler:this.saveForm.createDelegate(this)
        }, {
            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
            scope: this,
            handler: function(){
                this.close();
            }
        }]
    },config);
    Wtf.common.UpdateProfile.superclass.constructor.call(this, config);
}

Wtf.extend( Wtf.common.UpdateProfile, Wtf.Window, {
    onRender: function(config){
        Wtf.common.UpdateProfile.superclass.onRender.call(this, config);
        this.createForm();
        this.add({
            region: 'north',
            height: 75,
            border: false,
            bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtmlReqField(WtfGlobal.getLocaleText("crm.userprofile.updateprofile.title"),WtfGlobal.getLocaleText("crm.userprofile.updateprofile.tophtml.detail"),'')
        },{
            region: 'center',
            border: false,
            bodyStyle: 'background:#f1f1f1;font-size:10px;',
            autoScroll:true,
            items:this.userinfo
        });
    },
    createForm:function(){
        if(Wtf.StoreMgr.containsKey("alltimeZone")){
            this.getRecord();
        } else {
            Wtf.timezoneStore.on("load",this.getRecord,this);
        }
        chkalldateFormatload();
        chkalltimeZoneload();

        this.dfCmb= new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("crm.updateprofile.dateformat"),//'Date format',
            hiddenName:'formatid',
            store:Wtf.dfStore,
            valueField:'formatid',
            displayField:'name',
            mode: 'local',
            width:220,
            triggerAction: 'all',
            editable : false
        });

        this.tfCmb= new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("crm.updateprofile.timeformat"),//'Time format',
            hiddenName:'timeformat',
            store:Wtf.tfStore,
            valueField:'timeformatid',
            displayField:'name',
            mode: 'local',
            width:220,
            triggerAction: 'all',
            emptyText:'-Select Time Format-',
            editable : false
        });
        
        this.tzCmb= new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("crm.updateprofile.timezone"),//'Timezone',
            hiddenName:'tzid',
            store:Wtf.timezoneStore,
            valueField:'id',
            displayField:'name',
            mode: 'local',
            width:220,
            triggerAction: 'all',
            editable : false
        });

        this.callWith= new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("crm.updateprofile.callwith"),//'Call With',
            hiddenName:'callwithid',
            store:Wtf.callWithStore,
            valueField:'id',
            displayField:'name',
            mode: 'local',
            width:220,
            triggerAction: 'all',
            editable : false
        });

        this.userinfo= new Wtf.form.FormPanel({
            fileUpload:true,
            baseParams:{
                mode:12
            },
            url:"Common/ProfileHandler/saveUser.do",
            region:'center',
            bodyStyle: "background: transparent;",
            border:false,
            style: "background: transparent;padding:20px;",
            defaultType:'striptextfield',
            labelWidth:125,
            defaults : {
                width:300
            },
            items:[ {
                name:'userid',
                xtype:'hidden'
            },{
                fieldLabel:WtfGlobal.getLocaleText("crm.USERNAMEFIELD"),//'User Name ',
                name:'username',
                id:'username',
                readOnly:true,
                maxLength:30,
                allowBlank:false
            },{
                fieldLabel:WtfGlobal.getLocaleText("crm.EMAIL"),//'E-Mail ',
                name:'emailid',
                id:'emailid',
                maxLength:50,
                regex:Wtf.ValidateMailPatt
            },{
                fieldLabel: WtfGlobal.getLocaleText("crm.lead.defaultheader.fname")+'*',//'First Name* ',
                name: 'fname',
                id:'fname',// used this id
                maxLength:32,
                xtype:'striptextfield',
                allowBlank:false
            },{
                fieldLabel:WtfGlobal.getLocaleText("crm.contact.defaultheader.lname")+'*',//'Last Name* ',
                name: 'lname',
                id:'lname',// used this id
                maxLength:32,
                xtype:'striptextfield',
                allowBlank:false
            },{
                fieldLabel: WtfGlobal.getLocaleText("crm.userprofile.userpic"),//'User Picture ',
                hideLabel:true,
                hidden:true,
                name:'userimage',
                inputType:'file',
                id:'userimage'
            },{
                fieldLabel: WtfGlobal.getLocaleText("crm.userprofile.contactno"),//'Contact No ',
                name: 'contactno',
                regex:Wtf.PhoneRegex,
                maxLength:50,
                id:'contactno'
            },{
                fieldLabel: WtfGlobal.getLocaleText("crm.lead.defaultheader.address"),// 'Address ',
                name: 'address',
                id:'address',
                maxLength:255,
                xtype:'striptextarea'
            },{
                fieldLabel:WtfGlobal.getLocaleText("crm.userprofile.aboutme"),// 'About Me ',
                name: 'aboutuser',
                id:'aboutme',
                maxLength:255,
                xtype:'striptextarea'
            },
            this.dfCmb,
            this.tfCmb,
            this.tzCmb,
            this.callWith,new Wtf.form.Checkbox({
//                inputType: 'checkbox',
                fieldLabel: WtfGlobal.getLocaleText("crm.masterconfig.emailnotificationsetting.label"),//'E-mail Notifications',
                name: 'notificationtype',
                id: 'notificationtype1',
                width:'16px',
//                checked: params.notification,
                style: (Wtf.isIE6 || Wtf.isIE7) ? '': 'top:5px;'
            })]
        });
    },
    getRecord:function(){
        Wtf.Ajax.requestEx({
            url:"Common/ProfileHandler/getAllUserDetails.do",
            params:{
                mode:11,
                lid:loginid
            }
        },this,this.genSuccessResponse,this.genFailureResponse);
    }, 
    saveForm:function(){
        if(!this.userinfo.getForm().isValid()){
            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.lead.webtoleadform.entervalinputmsg"));
        } else{                         
            this.userinfo.getForm().submit({
                waitMsg:WtfGlobal.getLocaleText("crm.userprofile.update.loadmsg"),//'Saving user information',
                success:function(f,a){
                    var res = eval('('+a.response.responseText+')');
                    if(res.data.tzdiff) {
                    	if(Wtf.TimeZoneDiff != res.data.tzdiff)
                    		WtfComMsgBox([WtfGlobal.getLocaleText("crm.userprofile.updateprofile.title"),WtfGlobal.getLocaleText("crm.userprofile.updateprofile.timezonechangesmsg")]);
                        Wtf.TimeZoneDiff = res.data.tzdiff;
                        
                    }
                    this.genSaveSuccessResponse(res)
                },
                failure:function(f,a){
                    this.genSaveFailureResponse(eval('('+a.response.responseText+')'))
                },
                scope:this
            });
        }
    },
    genSuccessResponse:function(response){ 
        this.userinfo.getForm().setValues(response.data[0]);
    },
    genFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("crm.customreport.failurerespmsg");//"Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        Wtf.getCmp('updateProfileWin').close();
        ResponseAlert(['Update Profile',msg]);
    },
    genSaveSuccessResponse:function(response){
        if(response.success==true){
            setFullName(Wtf.getCmp('fname').getValue()+" "+Wtf.getCmp('lname').getValue());
            loginemail= Wtf.getCmp('emailid').getValue();
            updatePreferences();
            logintimeformat=this.tfCmb.getValue();
            Wtf.dfStore.load();
        }
        callwith = this.callWith.getValue();
        callwithStr = getCallWithStr(callwith);
        ResponseAlert([WtfGlobal.getLocaleText("crm.userprofile.updateprofile.title"),response.data.msg]);
        Wtf.getCmp('updateProfileWin').close();
    },
    genSaveFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("crm.customreport.failurerespmsg");//"Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        Wtf.getCmp('updateProfileWin').close();
        ResponseAlert(['Update Profile',msg]);
    }
});



