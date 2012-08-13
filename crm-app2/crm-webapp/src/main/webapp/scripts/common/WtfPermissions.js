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

Wtf.common.Permissions=function(config){
    
//### Role may be changed from CRM application, but for that required to update user's role on platform app also'
//    this.roleRecord=new Wtf.data.Record.create(
//        ['roleid','displayrolename']
//        );
//    this.roleStore = new Wtf.data.Store({
//        url: "Common/PermissionHandler/getRoleList.do",
//        reader: new Wtf.data.KwlJsonReader({
//            root: 'data'
//        },this.roleRecord)
//    });
//    this.roleCmb= new Wtf.form.ComboBox({
//        fieldLabel:'Role',
//        hiddenName:'roleid',
//        store:this.roleStore,
//        valueField:'roleid',
//        displayField:'displayrolename',
//        mode: 'local',
//        anchor:'90%',
//        triggerAction: 'all',
//        editable : false
//    });
   
    Wtf.apply(this,{
        buttons:[{
                text:WtfGlobal.getLocaleText("crm.APPLYBTN"),//'Apply',
                id:'apply',
                scope:this,
                handler:this.ApplyPermission
            },{
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                id:'cancel',
                scope:this,
                handler:this.cancel
        }]
    },config);
    Wtf.common.Permissions.superclass.constructor.call(this,config);
}

Wtf.extend( Wtf.common.Permissions,Wtf.Window,{
    title: WtfGlobal.getLocaleText("crm.usermanagement.bottomtoolbar.assignpermBTN"),//'Assign Permission',
    id:'AP',
    height:450,
    width:610,
    modal:true,
    iconCls: "pwnd favwinIcon",
    shadow:true,
    onRender:function(config){
        chkPermFeatureLoad()
        this.featureStore = Wtf.permFeatureStore
        chkPermActivityLoad()
        this.ActStore = Wtf.permActivityStore
        Wtf.common.Permissions.superclass.onRender.call(this,config);
        
//        this.roleStore.on('load',function(){
//            this.roleCmb.setValue(this.roleid);
//            var length= this.roleStore.getCount();
//            var rec=new this.roleRecord({
//                roleid:1234,
//                displayrolename:'Add New'
//            });
//            this.roleStore.insert(length,rec);
//        },this);
//        this.roleCmb.on('select',this.loadRolePermissions,this);
//        this.roleStore.load();
        this.loadPermissions();
    },
 
    loadRolePermissions:function(c) {
        if(c != null && c.getValue() == 1234){
            c.setValue(this.roleStore.getAt(0).get('roleid'));
            this.openNewRoleWindow();
            return;
        }
        this.windowFlag = false;
        this.ActStore.clearFilter();
        this.PerStore.baseParams={userid:this.userid,roleid:this.roleCmb.getValue()};
        this.PerStore.load();
    },

    openNewRoleWindow:function(){
        Wtf.Msg.prompt(WtfGlobal.getLocaleText("crm.userpermission.newrolemsgtitle"), WtfGlobal.getLocaleText("crm.userpermission.rolenamemsg")+':', function(btn, text){
            if (btn == 'ok'){
                Wtf.Ajax.requestEx({
                    url: "Common/PermissionHandler/saveRoleList.do",
                    params: {
                        userid:this.userid,
                        rolename:text,
                        displayrolename:text
                    }
                },this,this.genSuccessResponse,this.genFailureResponse);
            }
        },this);  
    },

    deleteRole:function() {
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), deleteMsgBox('role'),function(btn){
            if(btn!="yes") return;
            Wtf.Ajax.requestEx({
                url: "Common/CRMCommon/deleteRole.do",
                params: {
                    roleid:this.roleCmb.getValue()
                }
            },this,this.genSuccessResponse,this.genFailureResponse);
        },this);
    },

    checkActivities:function(){
        var comp;
        var featureids=this.ActStore.collect("featureid");
//        var userRole=this.roleCmb.getValue();
        for(var i=0;i<featureids.length;i++){
            this.ActStore.filter('featureid',featureids[i]);
            for(var x=0;x<this.ActStore.getCount();x++){
                var actRec=this.ActStore.getAt(x);
                comp=Wtf.getCmp("activity"+actRec.get('activityid'));
                comp.setValue(this.isChecked(actRec,x));
//                comp.setDisabled(userRole==Wtf.AdminId);
            }
            this.ActStore.clearFilter();
        }
//        this.applyBtn.setDisabled(userRole==Wtf.AdminId);
    }, 
    
    loadPermissions:function(){
        this.PerRecord=new Wtf.data.Record.create(
            ['featureid','permission']
            );
       
        this.PerStore = new Wtf.data.Store({
            url: 'Common/PermissionHandler/getRolePermissions.do',
            baseParams:{
                userid:this.userid,
                roleid:this.roleid
            },
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            },this.PerRecord)
        });
        this.windowFlag = true;
        this.PerStore.on('load',this.createWindow,this);
        this.PerStore.load();
    },

    createForm:function(){
        this.fieldSet1=new Wtf.form.FieldSet({
            id:this.id+'fieldset1',
            columnWidth:.49,
            baseCls:'xFieldSetBorder0',
            autoHeight:true
        });
        this.fieldSet2=new Wtf.form.FieldSet({
            id:this.id+'fieldset2',
            columnWidth:.49,
            baseCls:'xFieldSetBorder0',
            autoHeight:true
        });
        this.AssPerForm= new Wtf.form.FormPanel({
            region:'center',
            cls:'x-panel-body x-panel-body-noheader x-panel-body-noborder',
            bodyStyle: "background: transparent;",
            border:false,
            autoScroll:true,
            bodyBorder:false,
            style: "background: transparent;",
            width:'100%',
            height:'100%',
            id:'AssPerForm',
            items:[{
                name:'userid',
                xtype:'hidden'
            }/*,{
                layout:'column',
                style:'padding:20px',
                border:false,
                items:[{
                    columnWidth:.73,
                    layout:'form',
                    border:false,
                    labelWidth:50,
                    items:this.roleCmb
                },{
                    columnWidth:.23,
                    layout:'form',
                    border:false,
                    labelWidth:50,
                    items:{
                        xtype:'button',
                        text:'Delete Role',
                        scope:this,
                        handler:this.deleteRole
                    }
                }]
            }*/,{
                layout:'column',
                border:false,
                items:[this.fieldSet1,this.fieldSet2]
            }]
        });
        this.leftRight = false;
        for(var i=0;i<this.featureStore.getCount();i++) {
            var tooltip = this.getTooltips(i);
            this.createFeatureSet(i,tooltip);
        }
    },

    getTooltips:function(i){
        var tip=WtfGlobal.getLocaleText("crm.usermanagement.tip");//Click on arrow to assign permissions for viewing, editing, deleting, archiving';
        switch(i){
            case 0: tip+=WtfGlobal.getLocaleText("crm.usermanagement.tip0");// and exporting campaign lists.';
                break;
            case 1: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip1");//Click on arrow to assign permissions for importing data from Zoho.';
                break;
            case 2 : tip+=WtfGlobal.getLocaleText("crm.usermanagement.tip2");//, exporting and importing lead lists.';
                break;
            case 3: tip+=WtfGlobal.getLocaleText("crm.usermanagement.tip3");//, exporting and importing contact lists.';
                break;
            case 4 : tip+=WtfGlobal.getLocaleText("crm.usermanagement.tip4");//, exporting and importing account lists.';
                break;
            case 5 : tip+=WtfGlobal.getLocaleText("crm.usermanagement.tip5");// and exporting opportunity lists.';
                break;
            case 6 : tip+=WtfGlobal.getLocaleText("crm.usermanagement.tip6");// and exporting activity lists.';
                break;
            case 7 : tip+=WtfGlobal.getLocaleText("crm.usermanagement.tip7");// and exporting product lists.';
                break;
            case 8: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip8");//Click on arrow to assign permissions for viewing reports from the list of standard lead reports.';
                break;
            case 9 : tip+=WtfGlobal.getLocaleText("crm.usermanagement.tip9");// and exporting case lists.';
                break;
            case 10 : tip=WtfGlobal.getLocaleText("crm.usermanagement.tip10");//Click on arrow to assign permissions for viewing, uploading and downloading documents.';
                break;
            case 11 : tip=WtfGlobal.getLocaleText("crm.usermanagement.tip11");//Click on arrow to assign permissions for viewing and adding comments to a campaign, contact, lead, opportunity, product, case, activity or account.';
                break;
            case 12: tip+=WtfGlobal.getLocaleText("crm.usermanagement.tip12");// and exporting target lists.';
                break;
            case 13: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip13");//Click on arrow to assign permissions for viewing reports from the list of standard opportunity reports.';
                break;
            case 14: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip14");//Click on arrow to assign permissions for viewing reports from the list of standard account reports.';
                break;
            case 15: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip15");//Click on arrow to assign permissions for viewing reports from the list of standard product reports.';
                break;
            case 16: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip16");//Click on arrow to assign permissions for viewing reports from the list of standard contact reports.';
                break;
            case 17: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip17");//Click on arrow to assign permissions for viewing reports from the list of standard opportunity product reports.';
                break;
            case 18: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip18");//Click on arrow to assign permissions for viewing reports from the list of standard sales reports.';
                break;
            case 19: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip19");//Click on arrow to assign permissions for viewing reports from the list of standard case reports.';
                break;
            case 20: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip20");//Click on arrow to assign permissions for viewing reports from the list of standard activity reports.';
                break;
            case 21: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip21");//Click on arrow to assign permissions for viewing reports from the list of standard campaign reports.';
                break;
            case 22: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip22");//Click on arrow to assign permissions for viewing reports from the list of standard target reports.';
                break;
            case 23: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip23");//Click on arrow to assign permissions for viewing audit trail.';
                break;
            case 24: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip24");//Click on arrow to assign permissions for viewing, editing amd deleting goal lists.';
                break;
            case 25: tip=WtfGlobal.getLocaleText("crm.usermanagement.tip25");//Click on arrow to assign permissions for viewing reports from the list of standard goal management reports.';
                break;
        }
        return tip;
    },
    createWindow:function(){
        if(this.windowFlag == false) {
            this.checkActivities();
        } else {
            this.createForm();
            this.MainWinPanel= new Wtf.Panel({
                border:false,
                height:378,
                layout:'border',
                items:[{
                    region : 'north',
                    height : 75,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                    html: getTopHtml(WtfGlobal.getLocaleText("crm.usermanagement.tophtmltitle") ,WtfGlobal.getLocaleText({key:"crm.usermanagement.tophtmldetail",params:[this.username]})),
                    layout:'fit'
                },{
                    region:'center',
                    border:false,
                    layout:'fit',
                    bodyStyle: "background:#f1f1f1; padding:3px ",
                    items:[this.AssPerForm]
                }]
            });
            this.add(this.MainWinPanel);
            this.doLayout();
            this.MainWinPanel.doLayout();
        }
   },

    createFeatureSet:function(index,tip){
        var rec=this.featureStore.getAt(index);
        var feature=new Wtf.form.FieldSet({
            id:'feature'+rec.get('featureid'),
            xtype:"fieldset",
            collapsed: true,
            collapsible: true,
            style:'margin:10px',
            autoHeight:true,
            title:"<span wtf:qtip=\'"+tip+"\'>"+rec.get('displayfeaturename')+"</span>"
        });
        this.ActStore.filter('featureid',rec.get('featureid'));
        for(var i=0;i<this.ActStore.getCount();i++)
            this.createActivity(i,feature);
        if(this.leftRight == false){
            this.fieldSet1.add(feature);
            this.leftRight = true;
        } else {
            this.fieldSet2.add(feature);
            this.leftRight = false;
        }
    },

    createActivity:function(index,feature){
        var rec=this.ActStore.getAt(index);
//        var userRole=this.roleCmb.getValue();
        var activity=new Wtf.form.Checkbox({
            fieldLabel: rec.get('displayactivityname'),
            name: "act"+rec.get('activityid'),
            id:"activity"+rec.get('activityid'),
            checked:this.isChecked(rec,index),
//            disabled:userRole==Wtf.AdminId,
            //style:'margin-top:4px;'
            labelStyle :"width:200px !important;"
        });
        feature.add(activity);
//        this.applyBtn.setDisabled(userRole==Wtf.AdminId);
    },

    isChecked:function(actRec,index){
        var permRec=this.PerStore.getAt(this.PerStore.find('featureid',actRec.get('featureid')));
        if(permRec==null)return false;
        var permCode=permRec.get('permission');
        while(index>0){
            permCode=Math.floor(permCode/2);
            index--;
        }
        return permCode%2==1;
    },
    
    ApplyPermission:function(){
        var permCode=[];
        var features=[];
        var formVal=this.AssPerForm.getForm().getValues();
        for(var i=0;i<this.featureStore.getCount();i++){
            var feature=this.featureStore.getAt(i).get('featureid');
            features.push(feature);
            permCode.push(this.getNewFeatureValue(feature,formVal));
        }

        Wtf.Ajax.requestEx({
            url: 'Common/PermissionHandler/setPermissions.do',
            params: {
                userid:this.userid,
                features:features,
                permissions:permCode,
                roleid:this.roleid//this.roleCmb.getValue()
            }
        },this,this.genSuccessResponse,this.genFailureResponse);
    },  

    getNewFeatureValue:function(featureid,formVal){
        var code=0;
        var tmp=1;
        this.ActStore.filter('featureid',featureid);
        for(var i=0;i<this.ActStore.getCount();i++){
            var id=this.ActStore.getAt(i).get('activityid');
            if(eval('('+'formVal.act'+id+')'))code=code+tmp;
            tmp=2*tmp;
        }
        this.ActStore.clearFilter();
        return code;
    },
    cancel:function(){
        this.close();
    },
    
    genSuccessResponse:function(response){
        ResponseAlert([WtfGlobal.getLocaleText("crm.usermanagement.msg.titlePermission"),response.msg]);
        this.close();
        if(this.userid == loginid){
            if(response.Perm != undefined) {
                getPermissionObjects(response);
                WtfComMsgBox([WtfGlobal.getLocaleText("crm.usermanagement.msg.titleUserPermission"),"Permission changes on dashboard will take effect after you log out and log in again <br/>or refresh the screen."]);
            }
        }
    },

    genFailureResponse:function(response){
        var msg=WtfGlobal.getLocaleText("crm.customreport.failurerespmsg");//"Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        ResponseAlert([WtfGlobal.getLocaleText("crm.usermanagement.msg.titlePermission"),msg]);
    }
});

