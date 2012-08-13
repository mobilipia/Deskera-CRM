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
Wtf.quickadd = function(config){
    this.autoScroll=true;
    this.ht= 180;
    Wtf.apply(this, config);
    Wtf.quickadd.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.quickadd, Wtf.Panel, {
	 layout:'fit',
     initComponent: function() {
		Wtf.quickadd.superclass.initComponent.call(this);

        this.addEvents({
            "aftersave": true,
            "closeform": true
        });
    },
    onRender: function(config){
        Wtf.quickadd.superclass.onRender.call(this, config);
         this.objPanel = new Wtf.attributeComponent({
            configType:this.configType,
            getallfields:this.getallfields,
            callFrom:this.callFrom,
            id:this.compid,
            refid:this.lid,
            chk:1
        });
        this.add(this.objPanel);
        this.objPanel.on("setObject",function(respobj){
            this.objresponse = respobj;
           
        },this);

        this.objPanel.on('closeform',function(){
            this.doLayout();
            this.showQuickAdd();
            this.fireEvent("closeform",this.id);
        },this);

        
    },

   showQuickAdd:function(){
       if(this.objresponse && this.objresponse.data !='' && this.objresponse.data !=null){
           for(var i = 0; i < this.objresponse.data.length; i++) {
                this.ht += 30;
                if(Wtf.get(this.objresponse.data[i].recordname+this.compid))
                    Wtf.get(this.objresponse.data[i].recordname+this.compid).dom.parentNode.style.width="";
           }
       }

   },

    saveobj:function(){
        this.jsondata = {};
        var value="";
        var validflag=1;
        if(this.objresponse.data !='' && this.objresponse.data !=null){
                this.count = this.objresponse.data.length;
                var custcolumn = [];
                for(var i = 0; i < this.objresponse.data.length; i++) {
                    var cmpobj = Wtf.getCmp(this.objresponse.data[i].recordname+this.compid);
                    if(cmpobj!=null){
                        value = cmpobj.getValue();
                    if(this.objresponse.data[i].configtype == "3"&&value.getTime){
                        value=value.getTime();
                     }
                    
                    }else
                        continue;
                    if (typeof (value ) == 'string'){
                        value = value.trim();
                        if(value.length>cmpobj.maxLength){
                        	
                       	 WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.lead.webtoleadform.entervalinputmsg")]);
                       	 cmpobj.setValue("");
                       	 cmpobj.markInvalid();
                       	 cmpobj.allowBlank=false;
                       	 return;
                       }
                    }
                    if(value != ""||this.objresponse.data[i].allowblank==true){
                        if(!this.objresponse.data[i].customflag){
                           /* if(this.objresponse.data[i].configtype == "3"){
                               var val = (Wtf.getCmp(this.objresponse.data[i].recordname+this.compid).getValue());
                               value=val.getTime();
                            }else{
                                value = Wtf.getCmp(this.objresponse.data[i].recordname+this.compid).getValue();
                            }*/
                            this.jsondata[this.objresponse.data[i].recordname]=WtfGlobal.HTMLStripper(value);
                            if(WtfGlobal.HTMLStripper(value)=="99"){
                                validflag = 0;
                            }
                        }else{
                            var tmpObj = { filedid:this.objresponse.data[i].pojoname,
                            refcolumn_name:this.objresponse.data[i].refcolumn_number,
                            fieldname:this.objresponse.data[i].fieldname,
                            xtype:this.objresponse.data[i].configtype};

                            tmpObj[this.objresponse.data[i].fieldname]=this.objresponse.data[i].column_number;
                            tmpObj[this.objresponse.data[i].column_number]=value;
                            custcolumn.push(tmpObj);
                        }
                    }else{
                    for(var i = 0; i < this.objresponse.data.length; i++) {
                        cmpobj = Wtf.getCmp(this.objresponse.data[i].recordname+this.compid);
                        if(cmpobj!=null)
                            value = cmpobj.getValue();
                        else
                            continue;
                        if (typeof (value ) == 'string'){
                            value = value.trim();
                        }
                        if(value == ""){
                            cmpobj.setValue("");
                        }
                    }
                    Wtf.Msg.alert('Invalid Input',"Please enter all the essential details.");
                    return;
                }
                }
                if(this.configType=="Campaign"){
                    this.jsondata["campaigntypeid"]=WtfGlobal.HTMLStripper(Wtf.getCmp("quick_insert_campaign_typeid"+this.compid).getValue());
                    if(this.jsondata["enddate"] && this.jsondata["startdate"] && this.jsondata["enddate"] < this.jsondata["startdate"]) {
                        Wtf.Msg.alert('Invalid Input',"End date should not be less than Start date.");
                        return;
                    }
                }
                if(this.callFrom=="MoveLead"){
                    this.jsondata["firstname"]=WtfGlobal.HTMLStripper(Wtf.getCmp(Wtf.move_to_lead_fname+this.compid).getValue());
                    this.jsondata["lastname"]=WtfGlobal.HTMLStripper(Wtf.getCmp(Wtf.move_to_lead_lname+this.compid).getValue());
                    this.jsondata["email"]=WtfGlobal.HTMLStripper(Wtf.getCmp(Wtf.move_to_lead_email+this.compid).getValue());
                }
                this.jsondata['validflag']=validflag;
                this.jsondata['customfield']=custcolumn;
                Wtf.applyIf(this.jsondata, this.jsonstr);
                this.saveRecordReq(this.jsondata,this.paramObj,this.actionCode);
                for(var i = 0; i < this.objresponse.data.length; i++) {
                    var cmpobj = Wtf.getCmp(this.objresponse.data[i].recordname+this.compid);
                    if(cmpobj!=null)
                        cmpobj.setValue('');
                }
            }
    },

     saveRecordReq : function () {
        Wtf.commonWaitMsgBox("Saving data...");
        for(key in this.jsondata){ if(this.callFrom!="MoveLead")if(this.jsondata[key]=="")delete this.jsondata[key];}
        this.paramObj['jsondata'] = Wtf.util.JSON.encode(this.jsondata);
        this.paramObj['type'] = 1;
        if(this.callFrom=="MoveLead"){
            this.paramObj['movetolead'] = this.callFrom;
            this.actionCode = 8;
        }
        Wtf.Ajax.requestEx({
//            url:Wtf.req.base +'crm.jsp',
            url:this.url,
            params:this.paramObj
        },this,
        function(res) {
           	Wtf.updateProgress();
        	if(this.dashcomp!=null && Wtf.getCmp(this.dashcomp)!=undefined){
                var obj=Wtf.getCmp(this.dashcomp);
                if(obj!=null){
                    obj.callRequest("","",0);
                    Wtf.refreshUpdatesAll();
                }
            }
            if(this.treeflag){
                var treeObj = Wtf.getCmp("tree");
                if(this.configType==Wtf.crmmodule.lead)
                    treeObj.saveLead(res,this.jsondata,0);// last parameter to reload spreadsheet store
                else if(this.configType==Wtf.crmmodule.account)
                    treeObj.saveAccount(res,this.jsondata,0);// last parameter to reload spreadsheet store
                else if(this.configType==Wtf.crmmodule.product)
                    treeObj.saveProduct(res,this.jsondata,0);// last parameter to reload spreadsheet store
                else if(this.configType==Wtf.crmmodule.campaign)
                    treeObj.savecamp(res,this.jsondata,0);// last parameter to reload spreadsheet store
            } else {
                switch(this.configType) {
                    case Wtf.crmmodule.opportunity :
                    case Wtf.crmmodule.cases :
                    case Wtf.crmmodule.contact : 
                        var ID = res.ID;
                        var spreadSheetPanelArray = getActivespreadSheetPanel(this.configType,undefined, ID);
                        for(var spreadPanelCnt=0; spreadPanelCnt < spreadSheetPanelArray.length; spreadPanelCnt++) {
                            spreadSheetPanelArray[spreadPanelCnt].EditorStore.reload();
                        }
                        break;
                }
            }
             
            // StoreManager (Global Store) reload
            Wtf.ModuleGlobalStoreReload(this.configType);
            
            switch(this.actionCode) {
                case 0:
                    ResponseAlert(11);
                    break;
                case 1:
                    ResponseAlert(9);
                    break;
                case 2:
                    ResponseAlert(7);
                    break;
                case 3:
                    ResponseAlert(5);
                    break;
                case 4:
                    ResponseAlert(4);
                    break;
                case 5:
                    ResponseAlert(6);
                    break;
                case 6:
                    ResponseAlert(8);
                    break;
                case 7:
                    ResponseAlert(10);
                    break;
                case 8:// Move to Lead from Campaign
                	if(res.success){
                	ResponseAlert(["Success",res.movetoleadMsg]);
                		 Wtf.Ajax.requestEx({
                	        	url:Wtf.req.springBase+'emailMarketing/action/deleteTargets.do',
                	        	params:{
                	        		targetid:this.jsonstr.targetid
                	        	}
                	        },this,
                	        function(res){},
                	        function(){}
                	  );
                	}else{
                		
                		ResponseAlert(["Duplicate Lead",res.movetoleadMsg]);
                	}
                    break;
            }
            this.fireEvent("aftersave",res,this.jsondata);
        },
        function(res){
            switch(this.actionCode) {
                case 0:
                    WtfComMsgBox(152,1);
                    break;
                case 1:
                    WtfComMsgBox(14,1);
                    break;
                case 2:
                    WtfComMsgBox(52,1);
                    break;
                case 3:
                    WtfComMsgBox(302,1);
                    break;
                case 4:
                    WtfComMsgBox(102,1);
                    break;
                case 5:
                    WtfComMsgBox(252,1);
                    break;
                case 6:
                    WtfComMsgBox(202,1);
                    break;
                case 7:
                    WtfComMsgBox(352,1);
                    break;
                case 8: // Move to Lead from Campaign
                    WtfComMsgBox(14,1);
                    break;
            }
        });
    }
});
