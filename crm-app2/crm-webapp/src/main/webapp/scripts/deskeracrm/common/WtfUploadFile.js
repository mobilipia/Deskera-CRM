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
Wtf.UploadFile = function(config) {
    Wtf.apply(this, config);

    Wtf.UploadFile.superclass.constructor.call(this,{
        title: WtfGlobal.getLocaleText("crm.upload.title"),//"Upload Document",
        id: "uploadFileWindow",
        modal: true,
        resizable: false,
        iconCls : 'pwnd favwinIcon',
        width : 400,
        height: 100,
        buttonAlign : 'right',
        items: [this.uploadWin=new Wtf.form.FormPanel({
            frame:true,
            method : 'POST',
            fileUpload : true,
            waitMsgTarget: true,
            url: "crm/common/Document/addDocuments.do?fileAdd=true&mapid="+this.mapid+"",
            id: this.id+"fileUploadFromPanel",
            border: false,
            items: [{
                border: false,
                id: "fileAddressField",
                xtype: "textfield",
                inputType: 'file',
                fieldLabel: WtfGlobal.getLocaleText("crm.upload.windocfield.label"),//"Document",
                name: "document"
            },{
                xtype: "hidden",
                name: "refid",
                value: this.recid
            }]
        })],
        buttons: [{
            text:  WtfGlobal.getLocaleText("crm.uploadbtn"),//"Upload",
            handler:function()
            {
            var idx = this.idX;
            var keyid = this.keyid;
//            var grid = this.grid;
            var fname=Wtf.getCmp("fileAddressField").getValue();
            var upwin=Wtf.getCmp("uploadFileWindow");
            if(Wtf.getCmp("fileAddressField").getValue() != ""){
                Wtf.commonWaitMsgBox("Adding file...");
                this.uploadWin.form.submit({
                    params: {
                        flag: 83,
                        type: 1
                    },
                    scope:this,
                    success: function(a,b,c){
                        Wtf.updateProgress();
                        upwin.close();
                      //  Wtf.getCmp('tree').getLoader().load(Wtf.getCmp('tree').root);
                        if(Wtf.getCmp('doc-mydocs')!=null){
                            if(mainPanel.getActiveTab().id == "tabdocument") {
                               ResponseAlert(901);
                            } else {
                               ResponseAlert(41);
                            }
                             Wtf.getCmp('doc-mydocs').grid1.getStore().reload();
                             var res=eval('('+b.response.responseText+')');
                             Wtf.getCmp("tree").saveDocument(res.data.ID,fname);
                        } else {
                            ResponseAlert(41);
                        }
                        var obj=null;
                        switch(this.moduleName){
                            case Wtf.crmmodule.lead:obj=Wtf.getCmp(Wtf.moduleWidget.lead);break;
                            case Wtf.crmmodule.campaign:obj=Wtf.getCmp(Wtf.moduleWidget.campaign);break;
                            case Wtf.crmmodule.account:obj=Wtf.getCmp(Wtf.moduleWidget.account);break;
                            case Wtf.crmmodule.contact:obj=Wtf.getCmp(Wtf.moduleWidget.contact);break;
                            case Wtf.crmmodule.opportunity:opportunity=Wtf.getCmp(Wtf.moduleWidget.opportunity);break;
                            case Wtf.crmmodule.cases:obj=Wtf.getCmp(Wtf.moduleWidget.cases);break;
                            case Wtf.crmmodule.activity:obj=Wtf.getCmp(Wtf.moduleWidget.activity);break;
                            case Wtf.crmmodule.topactivity:obj=Wtf.getCmp(Wtf.moduleWidget.topactivity);break;
                            case Wtf.crmmodule.product:obj=Wtf.getCmp(Wtf.moduleWidget.product);break;
                        }if(obj!=null){
                        	 obj.callRequest("","",0);
                        }
                        Wtf.refreshUpdatesAll();
                        getDocsAndCommentList(this.selectedRec, keyid, idx, true, this.moduleName, this.profileFlag, 'email');
                    },
                    failure: function(){
                        ResponseAlert(42);
                        upwin.close();
                    }
                });
            } else {
                ResponseAlert(43);
            }
            },
            scope: this
        },{
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//"Cancel",
            scope:this,
            handler: function(){
                this.close();
        }
        }]
    });
}

Wtf.extend(Wtf.UploadFile, Wtf.Window, {

});


Wtf.attachFile = function(config) {
    Wtf.apply(this, config);

    Wtf.attachFile.superclass.constructor.call(this,{
        title: WtfGlobal.getLocaleText("crm.upload.title"),//"Upload Document",
        id: "uploadFileWindow",
        modal: true,
        resizable: false,
        iconCls : 'pwnd favwinIcon',
        width : 400,
        height: 100,
        buttonAlign : 'right',
        items: [this.uploadWin=new Wtf.form.FormPanel({
            frame:true,
            method : 'POST',
            fileUpload : true,
            waitMsgTarget: true,
            url: "Common/MailIntegration/mailIntegrate.do?module=Emails&action=EmailUIAjax&emailUIAction=uploadAttachment&to_pdf=true",
            id: this.id+"fileUploadFromPanel",
            border: false,
            items: [{
                border: false,
                id: "fileAddressField",
                xtype: "textfield",
                inputType: 'file',
                fieldLabel: "File",
                name: "email_attachment"
            }]
        })],
        buttons: [{
            text:WtfGlobal.getLocaleText("crm.uploadbtn"),// "Upload",
            handler:function()
            {
                var upwin=Wtf.getCmp("uploadFileWindow");
                if(Wtf.getCmp("fileAddressField").getValue() != ""){
                    Wtf.commonWaitMsgBox("Attaching file...");
                    this.uploadWin.form.submit({
                        params: {
                            module : 'Emails',
                            action : 'EmailUIAjax',
                            emailUIAction : 'uploadAttachment',
                            to_pdf : true
                        },
                        scope:this,
                        success: function(a,b,c){
                            Wtf.updateProgress();
                            var obj = eval('('+b.response.responseText+')');
                            if(obj.success) {
                                var data = eval('('+obj.data.data+')');
                                if(data.errmsg) {
                                    Wtf.MessageBox.alert(WtfGlobal.getLocaleText("crm.uploadwin.filesixemsgtitle"), data.errmsg);
                                } else {
                                    var info = eval('('+obj.data.data+')');
                                    this.scope.onSuccessAttached(info, true);
                                }
                            }
                            upwin.close();
                        },
                        failure: function(){
                            ResponseAlert(42);
                            upwin.close();
                        }
                    });
                } else {
                    ResponseAlert(43);
                }
            },
            scope: this
        },{
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//"Cancel",
            scope:this,
            handler: function(){
                this.close();
            }
        }]
    });
}

Wtf.extend(Wtf.attachFile, Wtf.Window, {

});
