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
Wtf.newEmailTemplate = function (config){
    Wtf.apply(this, config);
    if(this.systemplate==3) {
        this.defaultString = "leftcolumn";
    } else if(this.systemplate==2) {
        this.defaultString = "rightcolumn";
    } else if(this.systemplate==5) {
        this.defaultString = "richtext";
    } else if(this.systemplate==6) {
        this.defaultString = "postcard";
    } else {
        this.defaultString = "def";
    }
    
    this.paramtypestore = new Wtf.data.SimpleStore({
        fields :['id', 'name', 'group'],
        data:[['mailsender','Sender (You)', '1'],['mailrecipient','Recipient', '2'],['company','Company', '3'],['other','Other', '4']]
    });
    this.paramvaluestore = new Wtf.data.SimpleStore({
        fields :['id', 'name', 'group'],
        data:[['fname','First name', '1'],['lname','Last name', '1'],['phone','Phone No','1'],['email','Email','1'],
            ['currentyear','Current year', '4'],['cname','Sender Company name', '3'],['caddress','Sender Company address','3'], ['cmail','Sender Company email','3'],['rname','Recipient Company name', '3']]
    });

    this.tempEditId = "";
    if(this.addNewDashboardCall) {
        this.newEmailTemplateId ="template_wiz_win_addnew_dash";
        this.templateNameId = "template_name_txt_dash_addnew";
        this.templateDescId ="template_desc_dash_addnew"
        this.templateSubId = "template_subject_dash_addnew";
    } else if(this.dashboardCall) {
        if(this.templateType) {
            this.newEmailTemplateId ="template_dash_win_"+this.defaultString+this.templateid;
            this.templateNameId = "template_name_txt_dash"+this.defaultString+this.templateid;
            this.thistemplateDescId ="template_desc_dash"+this.defaultString+this.templateid;
            this.templateSubId = "template_subject_dash"+this.defaultString+this.templateid;
            this.templateDescId ="template_desc_dash"+this.defaultString+this.templateid;
        } else {
            this.newEmailTemplateId ="template_dash_win_"+this.templateid;
            this.templateNameId = "template_name_txt_dash"+this.templateid;
            this.thistemplateDescId ="template_desc_dash"+this.templateid;
            this.templateSubId = "template_subject_dash"+this.templateid;
            this.templateDescId ="template_desc_dash"+this.templateid;
        }
        
    } else {
        if(this.templateType) {
            this.newEmailTemplateId ="template_wiz_win"+this.defaultString+this.templateid;
            this.templateNameId = 'template_name_txt'+this.defaultString +this.templateid;
            this.templateDescId ="template_desc"+this.defaultString+this.templateid;
            this.templateSubId = "template_subject"+this.defaultString+this.templateid;
            this.tempEditId = "wizardTemplateEditor_" + this.defaultString + this.id;
        } else {
            this.newEmailTemplateId ="template_wiz_win"+this.templateid;
            this.templateNameId = 'template_name_txt'+this.templateid;
            this.templateDescId ="template_desc"+this.templateid;
            this.templateSubId = "template_subject"+this.templateid;
            this.tempEditId = "wizardTemplateEditor_" + this.id;
        }
    }

    this.templateEditor1=new Wtf.EmailTemplateEditor({
    	region:'center',
        id: this.tempEditId,
        emailtype:this.emailtype,
        plaintext :this.plaintext,
        tplVariables:[{
        	gid:'mailsender', gname:'Sender (You)',
        	gvars:[{id:'fname',name:'First name'},{id:'lname',name:'Last name'},{id:'phone',name:'Phone No'},{id:'email',name:'Email'}]
        },{
        	gid:'campaign', gname:'Campaign',
        	gvars:[{id:'campaignname',name:'Campaign Name'},{id:'campaignstarted',name:'Campaign Started On'},{id:'emailcampaigname',name:'Campaign Configuration'},{id:'totalemailsent',name:'Total Email Sent'},{id:'failbouncedmail',name:'Failed/Bounced Emails'},{id:'sentcount',name:'Sent Mail Count'}]
        }]
    });
    Wtf.newEmailTemplate.superclass.constructor.call(this,{
        id:this.newEmailTemplateId,
        border:false,
        closable:true,
        layout : 'fit',
        iconCls: "pwnd newEmailTemplate",
        tbar:[new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.savetempBTN"),//"Save Template",
            tooltip:{
                text:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.savetempBTN.ttip")//'Click to save Template.'
            },
            scope:this,
            iconCls:"pwnd saveBtn",
            handler:this.saveTemplate
        }),new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.defaulttemBTN"),//"Default Template",
            tooltip:{
                text:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.defaulttemBTN.ttip")//'Click here to revert your changes and go back to default template.'
            },
            scope:this,
            hidden:this.emailtype?false:true,
            iconCls:"pwndCRM templateEmailMarketing",
            handler:function(){
                    Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//'Confirm',
                        msg:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.reverttemchngmsg"),//"Are you sure you want go back to default template?",
                        icon:Wtf.MessageBox.QUESTION,
                        buttons:Wtf.MessageBox.YESNO,
                        scope:this,
                        fn:function(button){
                            if(button=='yes')
                            {
                                this.resetTemplate();
                            }else{
                                return;
                            }
                        }
                    });
            
            }
                
        })],
        items:  {
            border:false,
            autoWidth:true,
            layout:'border',
            bodyStyle:"background-color:#FFFFFF;margin:0px 50px;",
            items:[{
                region:'north',
                height:130,
                layout:'fit',
                cls:'templateMargin',
                border:false,
                items:[{
                        xtype:"fieldset",
                        title:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.metadata"),//"Meta Data",
                        height:120,
                        defaults :{
                            xtype:'striptextfield',
                            width:280
                        },
                        items:[{
                            fieldLabel:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.templatename")+'*',//"Template Name*",
                            id:this.templateNameId,
                            maxLength:255,
                            allowBlank:false,
                            disabled:this.emailtype?true:false,
                            xtype:'extstriptextfield',
                            name:"template_name"
                        },{
                            fieldLabel:WtfGlobal.getLocaleText("crm.case.defaultheader.desc"),//"Description ",
                            maxLength:1024,
                            xtype:'striptextfield',
                            id:this.templateDescId,
                            name:"template_desc"
                        },{
                            fieldLabel:WtfGlobal.getLocaleText("crm.case.defaultheader.subject")+'*',//"Subject* ",
                            id:this.templateSubId,
                            allowBlank:false,
                            maxLength:255,
                            xtype:'striptextfield',
                            name:"template_subject"
                     }]
                    
                }]
            }, this.templateEditor1]
        }
    });
     }

Wtf.extend(Wtf.newEmailTemplate,Wtf.ux.ClosableTabPanel,{
    isClosable:true,
    closeWindow:false,
    initComponent: function(config) {
        this.addEvents({
            'onsuccess': true
        });
        Wtf.newEmailTemplate.superclass.initComponent.call(this,config);
    },
    onRender: function(config){
        Wtf.newEmailTemplate.superclass.onRender.call(this,config);
        
        if(this.systemplate){ 
        	this.getTemplateContent(this.systemplate,'DefaultTemplates');
        }
        if(this.templateid) {
            if(this.dashboardCall){
                Wtf.getCmp("template_name_txt_dash"+this.templateid).setValue(this.tname);
                Wtf.getCmp("template_desc_dash"+this.templateid).setValue(this.tdesc);
                Wtf.getCmp("template_subject_dash"+this.templateid).setValue(this.tsubject);
            }else{
                Wtf.getCmp(this.templateNameId).setValue(this.tname);
                Wtf.getCmp(this.templateDescId).setValue(this.tdesc);
                Wtf.getCmp(this.templateSubId).setValue(this.tsubject);
            }
            if(this.emailtype){
                this.getEmailTypeBody();
            } else {
            	this.getTemplateContent(this.templateid,this.templateClass);
            }
        }
        
        this.on('activate',function(){
            this.doLayout();
        },this);
    },
   
    getTemplateContent: function(templateid,templateClass)
    {
    	Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
    	Wtf.Ajax.requestEx({	 
                url: Wtf.req.springBase+'emailMarketing/action/getTemplateContent.do',
                params: {
                    flag: 27,
                    templateid:templateid,
                    templateClass:templateClass
                }
            }, this, function(action, response){
                if(action.success){
                	var replaceText=(unescape(action.data.html))
                    replaceText=replaceText.replace(/line-height[\s{1,}]?:[\s{1,}]?\d{2,3}[\s{1,}]?%[\s{1,}]?;/g,"");
                    replaceText=replaceText.replace(/line-height[\s{1,}]?:[\s{1,}]?\d{2,3}[\s{1,}]?px[\s{1,}]?;/g,"");
                    replaceText=replaceText.replace(/line-height[\s{1,}]?:[\s{1,}]\d{1,2}\.\d{1,}em;/g,"");
                    this.templateEditor1.editorHtmlComp.setHtml(unescape(replaceText));
                }
                Wtf.updateProgress();
            }, function(){
            	 Wtf.updateProgress();
                ResponseAlert(["Failure","Failed to get Template content."]);
            });
    },
    getEmailTypeBody: function(){
        Wtf.Ajax.requestEx({
            url: Wtf.req.springBase+'emailMarketing/action/getEmailTypeContent.do',
            params: {
                typeid: this.templateid,
                plaintext :this.plaintext
                
            }
        }, this,
        function(action, response){
            if(action.success){
                var replaceText=(unescape(action.data.html))
                replaceText=replaceText.replace(/line-height[\s{1,}]?:[\s{1,}]?\d{2,3}[\s{1,}]?%[\s{1,}]?;/g,"");
                replaceText=replaceText.replace(/line-height[\s{1,}]?:[\s{1,}]?\d{2,3}[\s{1,}]?px[\s{1,}]?;/g,"");
                replaceText=replaceText.replace(/line-height[\s{1,}]?:[\s{1,}]\d{1,2}\.\d{1,}em;/g,"");
                this.templateEditor1.editorHtmlComp.setHtml(replaceText);
            }
        },
        function(action, response){

        });
    },

    resetTemplate: function(){
        Wtf.commonWaitMsgBox("Saving Default Template...")
        Wtf.Ajax.requestEx({
            url: Wtf.req.springBase+'emailMarketing/action/resetEmailType.do',
            params: {
            }
        }, this,
        function(action, response){
            Wtf.updateProgress();
            if(action.success){
                ResponseAlert(74);
                if(this.store)this.store.load();
                this.isClosable=true;
                this.closeWindow=true;
                this.fireEvent("closeTemplate");
            }
        },
        function(action, response){
            Wtf.updateProgress();
            ResponseAlert(69);
        });
    },
    
    saveTemplate:function(){
        this.isClosable=false;
        if(!Wtf.getCmp(this.templateNameId).isValid() || !Wtf.getCmp(this.templateSubId).isValid() || !Wtf.getCmp(this.templateDescId).isValid()){
        	ResponseAlert(103);
        	return;
        }else{
        
        var tname=Wtf.getCmp(this.templateNameId).getValue();
        var tsub=Wtf.getCmp(this.templateSubId).getValue();
        var tdesc=Wtf.getCmp(this.templateDescId).getValue();
        if(tname.trim() == "" || tsub.trim()=="" ){
            if(tname.trim() == ""){
                Wtf.getCmp(this.templateNameId).markInvalid("This field is mandatory");
            }
            if(tsub.trim() == ""){
                Wtf.getCmp(this.templateSubId).markInvalid("This field is mandatory");
            }
            WtfComMsgBox(955,0);
            return false;
        } else {
            Wtf.commonWaitMsgBox("Saving Template...")
            var plaintext = this.templateEditor1.editorHtmlComp.getPlainText();
            var SavereplaceText=this.templateEditor1.editorHtmlComp.getHtml();
                SavereplaceText=SavereplaceText.replace(/line-height[\s{1,}]?:[\s{1,}]?\d{2,3}[\s{1,}]?%[\s{1,}]?;/g,"");
                SavereplaceText=SavereplaceText.replace(/line-height[\s{1,}]?:[\s{1,}]?\d{2,3}[\s{1,}]?px[\s{1,}]?;/g,"");
                SavereplaceText=SavereplaceText.replace(/line-height[\s{1,}]?:[\s{1,}]\d{1,2}\.\d{1,}em;/g,"");

             if(this.emailtype){
                  Wtf.Ajax.requestEx({
                    url: Wtf.req.springBase+'emailMarketing/action/saveEmailType.do',
                    params: {
                        tname:tname,
                        tsub:tsub,
                        tdesc:tdesc,
                        tbody:SavereplaceText,
                        tid:this.templateid,
                        plaintext :plaintext,
                        flag : 2,
                        mode : this.templateid ? 1 : 0,
                        templateclass:this.templateClass
                    }
                },
                this,
                function(res){
                    if(res.success) {
                        if(res.msg!=undefined){
                            WtfComMsgBox(["Alert", res.msg], 0);
                        }else{
                            if(this.templateid==undefined)
                                ResponseAlert(59);
                            else
                                ResponseAlert(60);
                            if(this.store)this.store.load();
                            this.isClosable=true;
                            this.closeWindow=true;
                            this.fireEvent("closeTemplate");
                            if(this.mailTemplate!=undefined){
                                this.mailTemplate.load();
                            }
                            Wtf.updateProgress();
                        }
                   }
                },
                function(){
                    if(this.templateid==undefined)
                        ResponseAlert(61);
                    else
                        ResponseAlert(62);
                    Wtf.updateProgress();
                });
            } else {
                    Wtf.Ajax.requestEx({
                        url: Wtf.req.springBase+'emailMarketing/action/saveEmailTemplate.do',
                        params: {
                            tname:tname,
                            tsub:tsub,
                            tdesc:tdesc,
                            tbody:SavereplaceText,
                            tid:this.templateid,
                            flag : 2,
                            mode : this.templateid ? 1 : 0,
                            templateclass:this.templateClass
                        }
                    },
                    this,
                    function(res){
                        if(res.success) {
                            if(res.msg!=undefined){
                                WtfComMsgBox(["Alert", res.msg], 0);
                            }else{
                                if(this.templateid==undefined)
                                    ResponseAlert(59);
                                else
                                    ResponseAlert(60);
                                if(this.store)this.store.load();
                                this.isClosable=true;
                                this.closeWindow=true;
                                this.fireEvent("closeTemplate");
                                if(this.mailTemplate!=undefined){
                                    this.mailTemplate.load();
                                }
                                Wtf.updateProgress();
                            }
                       }
                    },
                    function(){
                        if(this.templateid==undefined)
                            ResponseAlert(61);
                        else
                            ResponseAlert(62);
                        Wtf.updateProgress();
                    });
            }
            return true;
        }
    }
    }
});
