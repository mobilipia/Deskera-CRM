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


Wtf.account.MailWindow = function(config){
     this.rec=(config.rec==undefined?"":config.rec);
     this.userdata=null;
     this.tax1099=(config.tax1099==undefined?false:config.tax1099);
     this.data=this.rec.data;
     this.store=config.store;
     this.isinvoice=null;
     this.userrec=null;
     this.mode=null;
     this.label=(config.label==undefined?"":config.label);
     this.sendBtn = new Wtf.Button({
        text: 'Send',
        handler:this.handleSend,
        scope:this,
        minWidth:this.minButtonWidth
    });
//    this.sendBtn.on('click', this.handleSend, this);
     this.closeBtn = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("crm.CLOSE"),//,
	    handler:this.handleClose,
	    scope:this,
	    minWidth:this.minButtonWidth
    });
//    this.closeBtn.on('click', this.handleClose, this);

     Wtf.apply(this,{
        title:"Send Mail",
        buttons: [this.sendBtn,this.closeBtn]
    },config);

    Wtf.account.MailWindow.superclass.constructor.call(this, config);
}
Wtf.extend(Wtf.account.MailWindow, Wtf.Window, {
	bodyStyle:'background:#F1F1F1',
   getRecord:function(){
        Wtf.Ajax.requestEx({
//            url:Wtf.req.base+"UserManager.jsp",
            url:"Common/ProfileHandler/getAllUserDetails.do",
            params:{
               mode:11,
               lid:loginid
            }
        },this,this.genSuccessResponse,this.genFailureResponse);

    },
    genSuccessResponse:function(response){
        this.userdata=response.data[0];
        var a='<div style="padding-top:24px">Attach '+this.label+ ' PDF </div>';//this.sendCopy.show();
        if(this.userdata!=null&&this.userdata.emailid!=undefined&&this.userdata.emailid!=""){
            a='<span>Send me ({emailid}) a copy</span>'+a;
        }else{this.sendCopy.hide();}
        this.tplSummary=new Wtf.XTemplate(a);
        this.userdata=response.data[0];
        if(this.doctype==1){
             this.Subject.setValue(this.label+"-"+companyName+"-"+this.data.customername+"-"+this.rec.data.billno);
             this.tplSummary.overwrite(this.southTpl.body,{emailid:this.userdata.emailid});
             this.Message.setValue("Hello "+this.data.customername+",<br/>"+
             "<br/>"+
             "We have enclosed your Quotation for "+this.data.date.format("F Y")+". <br/>"+
             "<br/>"+
//             "If you have any questions about the Quotation, please phone/mail at                         020(335433453). <br/>"+
//             "<br/>"+
//             "We would be happy to help.<br/>"+
//             "<br/>"+
             "Thank you for your Enquiry. We look forward to work with you.<br/>"+
             "<br/>"+
             "Sincerely,<br/>"+
             "<br/>"+
             this.userdata.fname+" "+this.userdata.lname+"<br/>"+
             "<br/>"+
             "<br/>"+
             "Enclosures:<br/>"+
             "Quotation Number "+this.data.billno+"");
         }/* else {
                this.Subject.setValue(this.label+"-"+companyName+"-"+this.rec.data.personname+"-"+this.rec.data.billno);
                this.tplSummary.overwrite(this.southTpl.body,{emailid:this.userdata.emailid});
                this.Message.setValue("Hello "+this.data.personname+"<br/>"+
                "<br/>"+
                "We have enclosed your receipt for "+this.rec.data.billdate.format("F Y")+". <br/>"+
                "<br/>"+
                "If you have any questions about the receipt, please phone/mail at "+Wtf.account.companyAccountPref.companyPhoneNo+(Wtf.account.companyAccountPref.companyPhoneNo.length>0?"/":"")+Wtf.account.companyAccountPref.companyEmailId+". <br/>"+
                "<br/>"+
                "We would be happy to help.<br/>"+
                "<br/>"+
                "Thank you for your business. We look forward to working with you again.<br/>"+
                "<br/>"+
                "Sincerely,<br/>"+
                "<br/>"+
                this.userdata.fname+" "+this.userdata.lname+"<br/>"+
                "<br/>"+
                "<br/>"+
                "Enclosures:<br/>"+
                "Invoice Number "+this.data.billno+"");
         }*/
    }, 
 
    genFailureResponse:function(response){
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        WtfComMsgBox(['Alert',msg],1);
    },
    onRender: function(config){
        this.createForm();
        this.getRecord();
        var image="../../images/accounting_image/bank-reconciliation.jpg";


        this.add({
//            region: 'north',
//            height:75,
//            border: false,
//            bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
//            html:getTopHtml('Send Mail','Send Mail',image,false)
//        },{
            region: 'center',
            border: false,
            baseCls:'bckgroundcolor',
            layout: 'fit',
            items:this.Form
        },{
            region: 'south',
            hidden:this.tax1099,
            border: false,
            height:80,//(Wtf.isIE?(this.tax1099?0:53):(this.tax1099?0:70)),
            autoScroll:true,
            baseCls:'bckgroundcolor',
            style: 'padding:0px 0px 0px 10px;',
            layout: 'border',
            items: [{
                region: 'west',
                border: false,
                width:40,
                layout: 'border',
                items:[{
                    region: 'center',
                    border: false,
                    baseCls:'bckgroundcolor',
                    layout: 'fit',
                    items: this.sendCopy= new Wtf.form.Checkbox({
                        name:'emailcopy',
                        checked:!this.tax1099,
                        width: 20
                    })
                },{
                    layout:'fit',
                    height:45,
                    region:'south',
                    baseCls:'bckgroundcolor',          
                    border:false,
                    items:this.sendpdf= new Wtf.form.Checkbox({
                        name:'sendpdf',
                        checked:!this.tax1099,
                        style: 'padding:0px 0px 10px 0px;',
                        width: 20
                    })
                }]
            },
            this.southTpl]
        });
        Wtf.account.MailWindow.superclass.onRender.call(this, config);
    },
    
    createForm:function(){
         this.southTpl=new Wtf.Panel({
            region: 'center',
            border: false,
            baseCls:'bckgroundcolor',
            style: 'padding-top:8px;',
            layout: 'fit'
         })
         this.Rec = Wtf.data.Record.create ([
            {name: 'userid'},
            {name: 'username',mapping:'accname'},
            {name: "fullname",mapping:'accname'},
            {name: "emailid",mapping:'email'},
            {name: 'image',mapping:''},
            {name:'accid'},
            {name:'openbalance'},
            {name:'id'},
            {name:'title'},
            {name:'accname'},
            {name:'address'},
            {name:'company'},
            {name:'email'},
            {name:'contactno'},
            {name:'contactno2'},
            {name:'fax'},
            {name:'shippingaddress'},
            {name:'pdm'},
            {name:'pdmname'},
            {name:'parentid'},
            {name:'parentname'},
            {name:'bankaccountno'},
            {name:'termid'},
            {name:'termname'},
            {name:'other'},
            {name: 'leaf'},
            {name: 'currencysymbol'},
            {name: 'currencyname'},
            {name: 'currencyid'},
            {name: 'deleted'},
            {name: 'creationDate' ,type:'date'},
            {name: 'level'}
        ]);
        this.url=this.tax1099?"ACCVendor/getVendors.do":"ACCCustomer/getCustomers.do"
        this.baseParams=this.tax1099?{
                deleted:false,
                nondeleted:true
            }:{
                mode:2,
                group:[10]//:[13])
            }
        this.contactStore = new Wtf.data.Store({
            //this.businessPerson+
    //        url:Wtf.req.account+this.businessPerson+'Manager.jsp',
            url:this.url,
            baseParams:this.baseParams,
            reader: new Wtf.data.KwlJsonReader({
                totalProperty:"totalcount",
                root: "data"
            },this.Rec)
        });
        this.resultTpl = new Wtf.XTemplate(
            '<tpl for="."><div class="search-item">',
                '<img src="{[this.f(values)]}">',
                '<div><h3><span>{fullname} - ({username})</span></h3><br>',
                '<div class="search-item-email">{emailid}</div></div>',
            '</div></tpl>', {
            f: function(val){
                if(val.image == "")
                    val.image = "../../images/user100.png";
                return val.image;
            },
            scope: this
        });

        this.To = new Wtf.form.ComboBox({
            store: this.contactStore,
            name:"to",
            fieldLabel:"To*",
         //   defaultValue:this.data.personemail,
            emptyText: 'Type user name and select one from the list',
            tabIndex:1,
            cls: 'search-username-combo',
            displayField: 'emailid',
            typeAhead: false,
            loadingText: 'Searching...',
            pageSize:3,
            anchor:'95%',
            disable : true,
            hideTrigger:true,
            tpl: this.resultTpl,
            itemSelector: 'div.search-item',
            minChars: 1,
            onSelect: function(record){
              // override default onSelect to do redirect
                var v = this.getValue().toString();
                if(v.indexOf(record.data['emailid']) == -1){
                    if(v.charAt(v.length) == ';')
                        this.setValue(v+record.data['emailid'] + ';');
                    else{
                        var temp = '';
                        if(v.indexOf(';') !== -1)
                            temp = v.substring(0, v.lastIndexOf(';')+1);
                        else
                            temp = '';
                        this.reset();
                        this.setValue(temp + record.data['emailid'] + ';');
                    }
                } else {
                    WtfComMsgBox(['Error', 'The selected username is already present in the mail recepient list'], 1);
                }
                this.focus();
            }
        });
        if(this.data.personemail!=undefined||this.data.personemail!=""||this.data.personemail!=null)
        this.To.setValue(this.data.personemail+";")
        this.To.on('beforequery', function(q){
            var qt = q.query.trim();
            var curr_q = qt.substr(qt.lastIndexOf(';')+1);
            curr_q = WtfGlobal.HTMLStripper(curr_q);
            q.query = curr_q;
        }, this)
        this.Subject=new Wtf.form.TextField({
            name:"subject",
            allowBlank:false,
            fieldLabel:"Subject",
            maxLength:100,
            anchor:'95%'
        });
        this.Message=new Wtf.newHTMLEditor({
            name:"message",
            allowBlank:false,
            fieldLabel:"Message*",
            xtype:'htmleditor',
            id:'bio',
            anchor:'95%',

            height: 240,
            border: false,
            enableLists: false,
            enableSourceEdit: false,
            enableAlignments: true,
            hideLabel: true
         });
         this.Form=new Wtf.form.FormPanel({
            region:'north',
            autoScroll:true,
           // height:(Wtf.isIE?(this.tax1099?512:455):(this.tax1099?495:425)),
            border:false,
            items:[{
                layout:'form',
                bodyStyle: "background: transparent; padding: 20px;",
                labelWidth:60,
                border:false,
                items:[this.To/*,{
                            border:false,
                            xtype:'panel',
                            
                            bodyStyle:'padding:0px 0px 10px 65px;',
                            html:'<font color="#555555">separate multiple email addresses with a semicolon(;).</font>'
                        }*/,this.Subject,this.Message]
            }]
        });
    },
    saveData:function(){ 
        if(!this.Form.getForm().isValid())
                WtfComMsgBox(2,1);
        else{
            var rec=this.Form.getForm().getValues();
            callReconciliationLedger(rec)
            this.close();
        }
    },
     handleClose:function(){
         this.fireEvent('cancel',this)
         this.close();
     },
     handleSend: function(bobj, edfd){
        //FIXME: msg sending problem from saved drafts
        if(!this.Form.getForm().isValid()) {
        	
            WtfComMsgBox(['Warning', 'Length of Subject should not be greater than 100'],1);
            return;
        }
        if(!(this.To.isValid())||this.To.getValue().trim()==""){          
                WtfComMsgBox(['Alert', 'Please specify atleast one recipient.'],2);
                //Wtf.Msg.alert('Alert', 'Please specify atleast one recipient.'2);
                return ;
        }
        this.sendBtn.disable();
        this.closeBtn.disable();
        if(this.Subject.getValue().trim()==""){
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), 'Send mail without subject?',
                function(btn){
                  if (btn == "yes") {
                      this.sendMail();
                  }
                  else {
                    this.enableButtons();
                    return false;
                  }
                }, this);
        } else this.sendMail();
    },

    enableButtons : function (){
        this.sendBtn.enable();
        this.closeBtn.enable();
    },

    sendMail:function(){
        var email="";
        if(this.sendCopy.getValue()!=""){
            email+=this.userdata.emailid+";";
        }

        email+=this.To.getValue().trim();

        var msg = this.Message.getValue();
        msg = msg.replace(/<STRONG>/gi,"<b>");
        msg = msg.replace(/<\/STRONG>/gi,"</b>");
        msg = msg.replace(/<em>/gi,"<i>");
        msg = msg.replace(/<\/em>/gi,"</i>");
        var rec=this.Form.getForm().getValues();
        rec.mode=this.doctype;
        rec.billid=this.data.billid;
        rec.mailingDate=WtfGlobal.convertToGenericDate(new Date());
        rec.emailid=email;
        rec.amount=this.data.amount;
        rec.customername=this.data.customername;
        rec.address=this.data.shipping;
        rec.currencyid=this.data.currencyid;
        rec.personid=this.data.customer
        rec.plainmsg = this.Message.getValue();
        rec.sendpdf=this.sendpdf.getValue();
        Wtf.Ajax.requestEx({
            url:Wtf.req.springBase+"common/quotation/sendInvoiceMail.do",
//            url:"CommonFunctions/sendMail.do",
//                    url: Wtf.req.account+this.businessPerson+'Manager.jsp',
            params: rec
        },this,this.success,this.failure);
     }, 

    success:function(response){
        WtfComMsgBox([this.label,response.msg],0);
        this.handleClose();
//        if(this.store)
//            this.store.load();
//        if(response.success){
//             if(this.tax1099){
//                var rec=[];
//                rec.accid=this.data.personid;
//                rec.taxidmailon=WtfGlobal.convertToGenericDate(new Date());;
//                Wtf.Ajax.requestEx({
//                    url:"ACCVendor/saveVendorMailingDate.do",
////                  url:Wtf.req.account+this.businessPerson+'Manager.jsp',
//                    params: rec
//                },this,this.mailSuccessResponse,this.mailFailureResponse);
//             }
//
//        }
    },

    failure:function(response){
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        WtfComMsgBox(['Alert',msg],2);
        this.handleClose();
    },
    mailSuccessResponse:function(response){
        this.close();
    },
    mailFailureResponse:function(response){
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        WtfComMsgBox(['Alert',msg],2);
        this.close();
    }
});
