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
Wtf.convleadpanel=function(config) {

    Wtf.convleadpanel.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.convleadpanel,Wtf.Window,{
    iconCls: "pwnd favwinIcon",
    width:450,
    onRender: function(config){
        Wtf.convleadpanel.superclass.onRender.call(this,config);

        this.parentaccountstore=new Wtf.data.Store({

            url: Wtf.req.springBase+'Contact/action/getAllAccounts.do',
            baseParams: {hierarchy : true},
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, Wtf.ComboReader)/*,
            autoLoad:true*/
        });

        if(Wtf.oppstageStore.getCount()==0) {
            Wtf.commonWaitMsgBox("Loading data...");
            Wtf.oppstageStore.on("load",this.OppStageStoreLoad,this);
            chkoppstageload();
        }
        this.parentaccount=new Wtf.form.ComboBox({
                fieldLabel: 'Parent Account  ',
                id:this.id+'parentaccount',
                allowBlank:false,
//                selectOnFocus:true,
//                triggerAction: 'all',
                mode: 'remote',
//                typeAhead: true,
//                forceSelection:true,
                triggerClass: "dttriggerForTeamLead",
                emptyText: 'Search an account',
                editable : true,
                store: Wtf.parentaccountstoreSearch,
                displayField: 'name',
                valueField:'id'
            });
        this.accountCheck=new Wtf.form.FieldSet({
            checkboxToggle:true,
            title: 'Convert to Account',
            autoHeight:true,
            defaults: {
                width: 210
            },
            defaultType: 'textfield',
            collapsed: true,
            labelWidth:150,
            items :[            
            this.accname = new Wtf.ux.TextField({
                fieldLabel: 'Account Name ',
                id:this.id+'accname',
                allowBlank:false,
                maxLength:255,
                value:this.rec.get("lastname")+' - A'
            })
           ]
        });

        this.opportunityCheck=new Wtf.form.FieldSet({
            checkboxToggle:true,
            title: 'Create Opportunity',
            autoHeight:true,
            defaults: {
                width: 210
            },
            defaultType: 'textfield',
            labelWidth:150,
            collapsed: true,
            items :[
            this.oppName = new Wtf.ux.TextField({
                fieldLabel: 'Opportunity Name ',
                id:this.id+'oppname',
                allowBlank:false,
                maxLength:255,
                value:this.rec.get("lastname")+' - O'
            }),
            this.parentaccountOpp=new Wtf.form.ComboBox({
                fieldLabel: 'Parent Account  ',
                id:this.id+'parentaccountOpp',
                allowBlank:false,
//                selectOnFocus:true,
                triggerAction: 'all',
                mode: 'remote',
                tpl: Wtf.comboTemplate ,
                minChars : 2,
                triggerClass: "dttriggerForTeamLead",
//                typeAhead: true,
                forceSelection:true,
                store: Wtf.parentaccountstoreSearch,
                displayField: 'name',
                valueField:'id'
            }),
            this.accnameOpp = new Wtf.form.TextField({
                fieldLabel: 'Account Name ',
                id:this.id+'accnameOpp',
                //allowBlank:false,
                inputType:'hidden'
            }),
            this.oppstage = new Wtf.form.ComboBox({
                fieldLabel: 'Opportunity Stage*',
                id: this.id + 'oppstage',
                allowBlank:false,
                selectOnFocus:true,
                triggerAction: 'all',
                mode: Wtf.StoreMgr.containsKey(Wtf.StoreManagerKeys.opportunitystage) ? "local" : "remote",
                forceSelection:true,
                typeAhead: true,
                emptyText:'--Select Opportunity Stage--',
                store: Wtf.oppstageStore,
                displayField: 'name',
                valueField:'id'
            }),
            this.oppclosedate = new Wtf.form.DateField({
                fieldLabel:'Close Date',
                format:WtfGlobal.getOnlyDateFormat(),
                offset:Wtf.pref.tzoffset,
                readOnly:true,
                id:this.id+'oppclosedate',
                allowBlank:false,
                value:new Date()
            })
            ]
        });
        this.contactCheck=new Wtf.form.FieldSet({
            checkboxToggle:true,
            title: 'Add to Contact list',
            layout:'form',
            autoHeight:true,
            defaults: {
                width: 210
            },
            defaultType: 'textfield',
            collapsed: true
        });

        var itemArr=[];
        itemArr.push(this.accountCheck);
        itemArr.push(this.opportunityCheck);
        if(this.rec.get("type")=="0") // Kuldeep Singh : Contact fieldset visible only for Leads having type Individual
            itemArr.push(this.contactCheck);
        
        this.taskCheck=new Wtf.form.FieldSet({
            checkboxToggle:true,
            title: 'Assign Task',
            autoHeight:true,
//            bodyStyle : 'font-size:10px;padding:8px 7% 0px 10px',
           defaults: {
                	   width:210
            },
            labelWidth:150,
//           defaultType: 'textfield',
            collapsed: true,
            items : getActivityFields(this,true) // true to hide relatedto and relatedname
            
        });
        itemArr.push(this.taskCheck);
        this.add({
            height:420,
            layout:'border',
            items :[{
                region : 'north',
                height : 80,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml("Convert Lead", "Convert Lead into Account/ Opportunity and Contact. For Company type lead, all Contact(s) under the selected lead will also get converted.","../../images/convertLead.gif")
//                html: getTopHtml("Convert Lead", "Leads can be converted to accounts, contacts, opportunities, and followup tasks.\n\
//                     <br/>After this lead has been converted, it can no longer be viewed or edited as a lead, but can be viewed in lead reports.","../../images/convertLead.gif")
            },new Wtf.form.FormPanel({
                region : 'center',
                border : false,
                autoScroll: true,
                id:'convertToAccId',
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 10px',
                items:itemArr
            })],
            buttonAlign:'right',
            buttons: [{
                text:'Save',
                scope:this,
                id:this.id+'convertleadbutton',
                handler:this.validSave
            },{
                text: WtfGlobal.getLocaleText("crm.CLOSE"),//,
                scope:this,
                handler:function(){
                    this.close()
                }
            }]

        });
//        this.parentaccountstore.on('load',function(){
//            this.parentaccountOpp.setValue(99);
//            this.parentaccount.setValue(99);
            
//        },this);

        this.parentaccountstore.on("loadexception",function(){
            Wtf.updateProgress();
        },this);

        this.accountCheck.on('expand',function(){
            this.accCflag=1;
            this.parentaccountOpp.allowBlank = true;
//            this.parentaccountOpp.setValue( this.parentaccount.getValue())
            this.accnameOpp.setValue(this.accname.getValue())
            this.parentaccountOpp.disable();
            this.accnameOpp.disable();

        },this);

        this.accountCheck.on('collapse',function(){
            this.accCflag=0;
            this.parentaccountOpp.allowBlank = false;
            this.parentaccountOpp.enable();
            this.accnameOpp.enable();
    
        },this);


        this.opportunityCheck.on('expand',function(){
            this.oppCflag=1;
//            this.parentaccountOpp.setValue( this.parentaccount.getValue())
            this.accnameOpp.setValue(this.accname.getValue())
        },this);


        this.contactCheck.on('expand',function(){
            this.conCflag=1;
        },this);
        this.taskCheck.on('expand',function(){
            this.taskCflag=1;
        },this);

        this.opportunityCheck.on('collapse',function(){
            this.oppCflag=0;
        },this);
        this.contactCheck.on('collapse',function(){
            this.conCflag=0;
        },this);
        this.taskCheck.on('collapse',function(){
            this.taskCflag=0;
        },this);
    },

    OppStageStoreLoad :function() {
        Wtf.updateProgress();
        Wtf.oppstageStore.un("load",this.OppStageStoreLoad,this);
    },
    validSave:function() 
    {
        var validd=false;
        var validd1=false;
       
        if(this.oppCflag==1) {
            this.oppName.validate();
            this.parentaccountOpp.validate();
            this.oppstage.validate();
            this.oppclosedate.validate();
           
            if(this.accCflag==1) {
                var accName = this.accname.getValue()

                var oppName = this.oppName.getValue()
                
                if(accName.trim()=="" || oppName.trim()==""){
                    ResponseAlert(152);
                    return false;
                }

                if(!this.oppstage.isValid() || !this.oppName.isValid() || !this.oppclosedate.isValid() ) {
                    ResponseAlert(152);
                    validd=false;
                    return false; 
                }
                else{
                    validd=true;
                }
            }
            else{

                var oppName = this.oppName.getValue()

                if(oppName.trim()==""){
                    ResponseAlert(152);
                    return false;
                }
                if(!this.oppstage.isValid() ||this.parentaccountOpp.getValue()=="0" || !this.oppName.isValid() || !this.parentaccountOpp.isValid() || !this.oppclosedate.isValid()) {

                    if(this.parentaccountOpp.getValue()==0) {
                       ResponseAlert(153);
                    }  else {
                       ResponseAlert(152);
                    }
                    validd=false;
                    return false; 
                }
                else{
                    validd=true;
                }
            }
        }

         if(this.accCflag==1) {
            var accName = this.accname.getValue()
            if(accName.trim()==""){
                ResponseAlert(152);
                return false;
            }
            if(!this.accname.isValid())  {
                ResponseAlert(154);
                validd=false;
                return false;
            }
            else{
                validd=true;
            }
        }
        if(this.conCflag==1 &&((this.accCflag==0) || (this.oppCflag==0))) {
            validd1=true;
        }

        var activityFinalStr = "";
        if(this.taskCflag==1) {
            activityFinalStr = validateActivityFields(this,true);
            if(activityFinalStr.trim().length>0)
                validd=true;
            else
                validd=false;
        }

        if(validd) {
            this.saveConvert(activityFinalStr)
        }
        else{
            if(validd1) {
                ResponseAlert(151);
            }
            else {
                ResponseAlert(152);
            }
        }
    
    },
    saveConvert:function(activityFinalStr) {
        
        var validFlag=1;
        var closingdate=(Wtf.getCmp(this.id+'oppclosedate').getValue()).getTime();
        var convertedbttnid=this.id+'convertleadbutton';
        Wtf.getCmp(convertedbttnid).disable();
        var cstmdata = this.spreadSheet.getCustomColumnData(this.rec.data,false);
        cstmdata = cstmdata.substring(13);

        var keyArray =["accflag","oppflag","conflag","taskflag","phone","revenue","price","addstreet","ratingid","industryid","leadowner","leadownerid",
                    "leadid","leadstatusid","type","createdon","email","lastname","firstname","title","leadsourceid","productid","accountname","accparentaccount",
                    "oppparentaccount","oppparentaccountid","oppaccountname","oppstageid","oppstage","validflag","closingdate","annualrevenue","sendmailacc","oppname",
                    "probability","amount","sendmailopp","customfield"];
        var valArray =[this.accCflag,this.oppCflag,this.conCflag,this.taskCflag,this.rec.get("phone"),this.rec.get("revenue"),this.rec.get("price"),this.rec.get("addstreet"),this.rec.get("ratingid"),this.rec.get("industryid"),this.rec.get("leadowner"),this.rec.get("leadownerid"),
                    this.rec.get("leadid"),this.rec.get("leadstatusid"),this.rec.get("type"),this.rec.get("createdon").getTime(),this.rec.get("email"),this.rec.get("lastname"),this.rec.get("firstname"),this.rec.get("title"),this.rec.get("leadsourceid"),this.rec.get("productid"),this.accname.getValue(),this.parentaccount.getRawValue(),
                    this.parentaccountOpp.getRawValue(),this.parentaccountOpp.getValue(),this.accnameOpp.getValue(),this.oppstage.getValue(),this.oppstage.getRawValue(),validFlag,closingdate,"","",this.findById(this.id+'oppname').getValue(),
                    "","","",Wtf.decode(cstmdata)]
        
        var jsondata = WtfGlobal.JSONBuilder(keyArray, valArray);

        var params = {
            jsondata:jsondata,
            flag:19
        };
        if(activityFinalStr.trim().length > 0)
            params["activitydata"] =activityFinalStr;
        WtfGlobal.setAjaxReqTimeout();
        Wtf.Ajax.requestEx({

            url: Wtf.req.springBase+"common/Lead/convertLeads.do",
            params:params
        },
        this,
        function(res) {
            WtfGlobal.resetAjaxReqTimeout();
            if(res.success) {
                this.ds.reload();
                ResponseAlert(46);
//                if(this.accCflag == 1){
//                    Wtf.parentaccountstore.reload();
//                    Wtf.relatedToNameStore.reload();
//                }
                this.close();
                bHasChanged = true;
                var obj=Wtf.getCmp(Wtf.moduleWidget.lead);
                if(obj!=null)
                    obj.callRequest("","",0);
                obj=Wtf.getCmp(Wtf.moduleWidget.account);
                if(obj!=null)
                    obj.callRequest("","",0);
                obj=Wtf.getCmp(Wtf.moduleWidget.contact);
                if(obj!=null)
                    obj.callRequest("","",0);
                obj=Wtf.getCmp(Wtf.moduleWidget.opportunity);
                if(obj!=null)
                    obj.callRequest("","",0);
                obj=Wtf.getCmp("DSBMyWorkspaces");
                if(obj!=null)
                    obj.callRequest("","",0);
                Wtf.refreshUpdatesAll();
                
            } else {
                if(res.msg) {
                    Wtf.Msg.alert('Status', res.msg);
                } else {
                    ResponseAlert(47);
                }
                Wtf.getCmp(convertedbttnid).enable();
            }
        },
        function() {
            WtfGlobal.resetAjaxReqTimeout();
            Wtf.getCmp(convertedbttnid).enable();
            ResponseAlert(47);
        }
        )
    }

});
  
