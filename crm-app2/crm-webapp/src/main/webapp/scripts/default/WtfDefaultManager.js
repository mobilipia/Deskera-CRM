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
Wtf.moduleMap ={
    Product:1,
    Account :2,
    Contact : 3,
    Lead : 4,
    Case : 5,
    Campaign :6,
    Opportunity :7
}
function showIcon(searchId){
    var icon = Wtf.get(searchId);
    if(icon){
        icon.dom.style.display = "block";
    }
}

function hideIcon(searchId){
    var icon = Wtf.get(searchId);
    if(icon){
        icon.dom.style.display = "none";
    }
}
/*
*
* Sagar A - Not in used
*/
/*
function callCreateCompany(){
    var p = Wtf.getCmp("createcompany");
    if(!p){
        p= new Wtf.common.CreateCompany({
                title:'Create Company',
                id:'createcompany',
                closable: true,
                modal: true,
                iconCls: "pwnd favwinIcon",
                width: 390,
                height: 440,
                aLoad:false,
                resizable: false,
                buttonAlign: 'right',
                renderTo: document.body
            });p.show();
    }
}
function showFeatureAdmin(){
    var panel=Wtf.getCmp('featureAdminTab');
    if(panel==null)
    {
        panel = new Wtf.common.Features({
            title:'Feature Administration',
            closable:true,
            border:false,
            iconCls: 'pwnd projectTabIcon',
            id:"featureAdminTab"
        });
        mainPanel.add(panel);
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}
function callSystemAdmin(){
    var panel = Wtf.getCmp("systemadmin");
    if(panel==null){
        panel = new Wtf.common.SystemAdmin({
            title : "System Administration",
            layout : 'fit',
            closable:true,
            id:'systemadmin',
            iconCls: 'pwndCRM company',
            
            border:false
        });
        mainPanel.add(panel);
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}
*/
function showConfigMaster(){
    var panel=Wtf.getCmp('masterConfigTab');
    if(panel==null)
    {
        panel = new Wtf.MasterConfigurator({
            layout:"fit",
            title:"<div  wtf:qtip="+WtfGlobal.getLocaleText("crm.dashboard.masterconfig.ttip")+"' wtf:qtitle='"+WtfGlobal.getLocaleText("crm.dashboard.masterconfig")+"'>"+WtfGlobal.getLocaleText("crm.dashboard.masterconfig.ellipsetext")+"</div>",
            closable:true,
            border:false,
            iconCls: 'pwnd projectTabIcon',
            id:"masterConfigTab"
        });
        mainPanel.add(panel);
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function addLeadTab(a,b,c){
    var panel=Wtf.getCmp('LeadHomePanel');
    if(panel==null)
    {
        panel=new Wtf.newLeadEditor({
            border:false,
            title:"<div id='leadtab' wtf:qtip='"+WtfGlobal.getLocaleText("crm.dashboard.leads.ttip")+"'>"+WtfGlobal.getLocaleText("crm.LEAD.plural")+"</div>",//Capture all relevant information on potential sales opportunities or prospects i.e. individuals who have expressed some interest in your product or company.
            layout:'fit',
            closable:true,
            id:'LeadHomePanel',
            modName : "Lead",
            customParentModName : "Lead",
            iconCls:getTabIconCls(Wtf.etype.lead),
            newFlag:2,
            highLightId:a,
            fromExternalLink:!!b,
            activityId:b,
            searchparam:c,
            clearFlag:false
        });
        mainPanel.add(panel);
        panel.on("activate",function(){
            Wtf.getCmp("LeadHomePanelleadpan").doLayout();
        },this);
        if(a!=undefined){
        panel.on("render", function(p){
            this.EditorStore.on('load', function(){
                 if(Wtf.highLightSearch(a,this.EditorStore,"leadid")!=null){
                     Wtf.highLightGlobal(a,this.EditorGrid,panel.EditorStore,"leadid");
                     this.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,this.EditorStore,"leadid"));
                     if(b!=undefined){
                       this.activityId = b;
                       this.showActivity();
                     }
                   }else{
                       WtfComMsgBox(16);
                   }
               
            },this,{single:true});
        }, panel);
        }

    } else {
     //  mainPanel.setActiveTab(panel);
     if(Wtf.highLightSearch(a,panel.EditorStore,"leadid")!=null){
       Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"leadid");
       panel.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,panel.EditorStore,"leadid"));
        if(b==undefined){
                showLeadDetails(panel);
            }else{
                panel.activityId = b;
                panel.showActivity();
            }
     }else{
        if(a!=undefined)
            WtfComMsgBox(16);
     }
        showAdvanceSearch(panel,c);
    }

    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
    return panel;
}

function addLeadTabOld(a,b,c){
    var panel=Wtf.getCmp('LeadHomePanelOld');
    if(panel==null)
    {
        panel=new Wtf.leadEditor({
            border:false,
            title:"<div id='leadtab' wtf:qtip='"+WtfGlobal.getLocaleText("crm.dashboard.leads.ttip")+"'>"+WtfGlobal.getLocaleText("crm.LEAD.plural")+"</div>",//Capture all relevant information on potential sales opportunities or prospects i.e. individuals who have expressed some interest in your product or company.
            layout:'fit',
            closable:true,
            id:'LeadHomePanelOld',
            modName : "LeadOld",
            customParentModName : "Lead",
            iconCls:getTabIconCls(Wtf.etype.lead),
            newFlag:2,
            highLightId:a,
            fromExternalLink:true,
            activityId:b,
            searchparam:c,
            clearFlag:false
        });
        mainPanel.add(panel);
        panel.on("activate",function(){
            Wtf.getCmp("LeadHomePanelOldleadpan").doLayout()
        },this)
    } else {
     //  mainPanel.setActiveTab(panel);
     if(Wtf.highLightSearch(a,panel.EditorStore,"leadid")!=null){
       Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"leadid");
       panel.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,panel.EditorStore,"leadid"));
        if(b==undefined){
                showLeadDetails(panel);
            }else{
                panel.activityId = b;
                panel.showActivity();
            }
     }else{
        if(a!=undefined)
            WtfComMsgBox(16);
     }
        showAdvanceSearch(panel,c);
    }
    
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
    return panel;
}
function addAccountTab(a,b,c){
    var panel=Wtf.getCmp('AccountHomePanel');
    if(panel==null)
    {
        panel=new Wtf.accountEditor({
            border:false,
            title:"<div id='accounttab' wtf:qtip='"+WtfGlobal.getLocaleText("crm.dashboard.accounts.ttip")+"'>"+WtfGlobal.getLocaleText("crm.ACCOUNT.plural")+"</div>",//Maintain comprehensive details of the organization or company you want to track such as customers, partners, or competitors. Easily track your existing customers as well as prospective clients.
            layout:'fit',
            iconCls:getTabIconCls(Wtf.etype.account),
            closable:true,
            id:'AccountHomePanel',
            modName : "Account",
            customParentModName : "Account",
            newFlag:2,
            highLightId:a,
            fromExternalLink:true,
            activityId:b,
            searchparam:c,
            clearFlag:false
        });
        mainPanel.add(panel);
        panel.on("activate",function(){
            Wtf.getCmp("AccountHomePanelaccountpan").doLayout();
        },this);
        if(a!=undefined){
        panel.on("render", function(p){
            this.EditorStore.on('load', function(){
                 if(Wtf.highLightSearch(a,this.EditorStore,"accountid")!=null){
                	 Wtf.highLightGlobal(a,this.EditorGrid,panel.EditorStore,"accountid");
                     this.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,this.EditorStore,"accountid"));
                     if(b!=undefined){
                    	 this.activityId = b;
                         this.showActivity();
                     }
                 }else{
                   WtfComMsgBox(16);
                 }
               
            },this,{single:true});
        }, panel);
        }
    } else {
       // mainPanel.setActiveTab(panel);
        if(Wtf.highLightSearch(a,panel.EditorStore,"accountid")!=null){
            Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"accountid");
            panel.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,panel.EditorStore,"accountid"));
            if(b==undefined){
                showLeadDetails(panel);
            }else{
                panel.activityId = b;
                panel.showActivity();
            }
        }else{
                if(a!=undefined)
                    WtfComMsgBox(16);
            }
            showAdvanceSearch(panel,c);
        }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
    return panel;
}
function addActivityMasterTab(a){
    var panel=Wtf.getCmp('ActivityHomePanel');
    if(panel==null)
    {
        panel=new Wtf.activityEditor({
            border:false,
            title:"<div id='activitytab' wtf:qtip='"+WtfGlobal.getLocaleText("crm.dashboard.activities.ttip")+"'>"+WtfGlobal.getLocaleText("crm.ACTIVITY.plural")+"</div>",//Maintain complete details of all activities including tasks and events associated with existing and prospective customers
            layout:'fit',
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.todo),
            id:'ActivityHomePanel',
            modName : "Activity",
            customParentModName : "Activity",
            newFlag:2,
            rFlag:0,
            Rretatedto:'',
            relatedtonameid:'0',
            urlFlag:81,
            highLightId:a,
            clearFlag:false
        });
      
        mainPanel.add(panel);
        panel.on("activate",function(){
            Wtf.getCmp("ActivityHomePanelactivitypan").doLayout()
        },this)
   } else {
       mainPanel.setActiveTab(panel);
       Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"activityid");
    }
   mainPanel.setActiveTab(panel);
   mainPanel.doLayout();
}
function addModuleActivityMasterTab(activityid,moduleId){
    Wtf.Ajax.requestEx({
        url: Wtf.req.springBase + Wtf.req.widget +"getAcitvityRelatedId.do",
        params: {
            moduleId: moduleId,
            activityid:activityid
        }
    }, this, function(response){
        if(response.success && response.success == true){
            if(response.data){
                var moduleRecId = response.data[0].moduleRecId;
                switch(moduleId){
                    case 2:
                        addAccountTab(moduleRecId,activityid);
                        break;
                    case 3:
                        addContactTab(moduleRecId,activityid);
                        break;
                    case 4:
                        addLeadTab(moduleRecId,activityid);
                        break;
                    case 5:
                        addCaseTab(moduleRecId,activityid);
                        break;
                    case 6:
                        addCampaignTab(moduleRecId,activityid);
                        break;
                    case 7:
                        addOpportunityTab(moduleRecId,activityid);
                        break;

                }
            }
        }
    },function(response){
        
        });
}

function openAdvanceSearchTab(moduleId,searchId){
    Wtf.Ajax.requestEx({
        url:"Common/AdvanceSearch/getSavedSearchQuery.do",
        params: {
            searchid: searchId
        }
    }, this, function(response){
        if(response.success && response.success == true){
            if(response.data){
                var searchstate = response.data[0].searchstate;
                switch(moduleId){
                    case 1:
                        addProductMasterTab(undefined,undefined,searchstate);
                        break;
                    case 2:
                        addAccountTab(undefined,undefined,searchstate);
                        break;
                    case 3:
                        addContactTab(undefined,undefined,searchstate);
                        break;
                    case 4:
                        addLeadTab(undefined,undefined,searchstate);
                        break;
                    case 5:
                        addCaseTab(undefined,undefined,searchstate);
                        break;
                    case 6:
                        addCampaignTab(undefined,undefined,searchstate);
                        break;
                    case 7:
                        addOpportunityTab(undefined,undefined,searchstate);
                        break;
                }
            }
        }
    },function(response){

    });
}
function deleteAdvanceSearch(searchId){
    Wtf.MessageBox.show({
        title: WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//"Confirm",
        msg: WtfGlobal.getLocaleText("crm.advancesearch.delsearchconfirmmsgMain"),//"Are you sure you want to delete selected search ?<br><br><b>Note: This data cannot be retrieved later.",
        buttons: Wtf.MessageBox.OKCANCEL,
        animEl: 'upbtn',
        icon: Wtf.MessageBox.QUESTION,
        scope:this,
        fn:function(bt){
            if(bt=="ok"){
                Wtf.Ajax.requestEx({
                    url:"Common/AdvanceSearch/deleteSavedSearchQuery.do",
                    params: {
                        searchid: searchId
                    }
                }, this, function(response){
                    if(response.success && response.success == true){
                        Wtf.refreshDashboardWidget(Wtf.moduleWidget.advancesearch);
                        WtfComMsgBox(1106,0);
                    }else{
                        WtfComMsgBox(1107,0);
                    }
                },function(response){

                    });
            }
        }
    });
}

function deleteCustomReport(reportno){
    Wtf.MessageBox.show({
        title: WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//"Confirm",
        msg: WtfGlobal.getLocaleText("crm.customreport.delrepoconfirmmsg"),//"Are you sure you want to delete selected custom report ?<br><br><b>Note: This data cannot be retrieved later.",
        buttons: Wtf.MessageBox.OKCANCEL,
        animEl: 'upbtn',
        icon: Wtf.MessageBox.QUESTION,
        scope:this,
        fn:function(bt){
            if(bt=="ok"){
                Wtf.Ajax.requestEx({
                    url:Wtf.req.customReport+'rbuild/deleteCustomReport.do',
                    params: {
                        reportno: reportno
                    }
                }, this, function(response){
                    if(response.success && response.success == true){
                        Wtf.refreshDashboardWidget(Wtf.moduleWidget.customReports);
                        WtfComMsgBox(1108,0);
                    }else{
                        WtfComMsgBox(1109,0);
                    }
                },function(response){
                });
            }
        }
    });
}

function showAdvanceSearch(panel,c){
    if(c!=undefined){
        var data = eval("("+decodeURIComponent(c)+")");
        if(!panel.objsearchComponent){
            panel.getAdvanceSearchComponent();
        }
        panel.configurAdvancedSearch();
        panel.objsearchComponent.searchStore.loadData(data);
        for(var i=0;i<data.data.length;i++){
            panel.objsearchComponent.combovalArr.push(data.data[i].combosearch);
        }
        panel.EditorStore.on("load",panel.storeLoad,panel);
        panel.objsearchComponent.doSearch(false);
        panel.objsearchComponent.search.enable();
        panel.objsearchComponent.saveSearch.enable();
    }
}
function addOpportunityTab(a,b,c){
    var panel=Wtf.getCmp('OpportunityHomePanel');
    if(panel==null)
    {
        panel=new Wtf.opportunityEditor({
            border:false,
            title:"<div id='opptab' wtf:qtip='"+WtfGlobal.getLocaleText("crm.dashboard.opportunities.ttip")+"'>"+WtfGlobal.getLocaleText("crm.OPPORTUNITY.plural")+"</div>",//Maintain complete information related to specific sales and pending deals that needs to be cracked. Add to that, you can record, all the related contacts and activities information for each opportunity.
            layout:'fit',
            closable:true,
            id:'OpportunityHomePanel',
            modName : "Opportunity",
            customParentModName : "Opportunity",
            newFlag:2,
            iconCls:getTabIconCls(Wtf.etype.opportunity),
            urlFlag:7,
            accFlag:0,
            mapid:'0',
            highLightId:a,
            fromExternalLink:true,
            activityId:b,
            searchparam:c,
            clearFlag:false
        });      
        mainPanel.add(panel);
        panel.on("activate",function(){
            Wtf.getCmp("OpportunityHomePanelopportunitypan").doLayout();
           
        },this);
        if(a!=undefined){
        panel.on("render", function(p){
            this.EditorStore.on('load', function(){
                 if(Wtf.highLightSearch(a,this.EditorStore,"oppid")!=null){
                     Wtf.highLightGlobal(a,this.EditorGrid,panel.EditorStore,"oppid");
                     this.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,this.EditorStore,"oppid"));
                     if(b!=undefined){
                    	 this.activityId = b;
                         this.showActivity();
                     }
                }else{
                      WtfComMsgBox(16);
                }
            },this,{single:true});
        }, panel);
        }
    } else {
    	mainPanel.setActiveTab(panel);
        Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"oppid");
        if(b!=undefined){
        	panel.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,panel.EditorStore,"oppid"));
            panel.activityId = b;
            panel.showActivity();
        }
        showAdvanceSearch(panel,c);
    }
    
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}
function addContactTab(a,b,c){
    var panel=Wtf.getCmp('ContactHomePanel');
    if(panel==null)
    {
        panel=new Wtf.contactEditor({
            title:"<div id='contacttab' wtf:qtip='"+WtfGlobal.getLocaleText("crm.dashboard.contacts.ttip")+"'>"+WtfGlobal.getLocaleText("crm.CONTACT.plural")+"</div>",//Maintain complete information about the individuals you know in an account and interact with.
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.contacts),
            id:'ContactHomePanel',
            modName : "Contact",
            customParentModName : "Contact",
            mapid:'0',
            layout:'fit',
            newFlag:2,
            addFlag:0,
            urlFlag:6,
            highLightId:a,
            fromExternalLink:true,
            activityId:b,
            searchparam:c,
            clearFlag:false
        });       
        mainPanel.add(panel);
        panel.on("activate",function(){
            Wtf.getCmp("ContactHomePanelcontactpan").doLayout();
        },this);
        if(a!=undefined){
        	panel.on("render", function(p){
        		this.EditorStore.on('load', function(){
        			if(Wtf.highLightSearch(a,this.EditorStore,"contactid")!=null){
        				Wtf.highLightGlobal(a,this.EditorGrid,panel.EditorStore,"contactid");
        				this.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,this.EditorStore,"contactid"));
        				if(b!=undefined){
        					this.activityId = b;
        					this.showActivity();
        				}
        			}else{
        				WtfComMsgBox(16);
        			}
        		},this,{single:true});
        }, panel);
        }
    } else {
    	if(Wtf.highLightSearch(a,panel.EditorStore,"contactid")!=null){
    		Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"contactid");
            panel.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,panel.EditorStore,"contactid"));
            if(b==undefined){
                showLeadDetails(panel);
            }else{
                panel.activityId = b;
                panel.showActivity();
            }
         }else{
             if(a!=undefined)
                WtfComMsgBox(16);
         }
         showAdvanceSearch(panel,c);
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
    return panel;
}
function addCaseTab(a,b,c){
    var panel=Wtf.getCmp('CaseHomePanel');
    if(panel==null)
    {
        panel=new Wtf.caseEditor({
            border:false,
            title:'<div id="casetab" wtf:qtip="'+WtfGlobal.getLocaleText("crm.dashboard.cases.ttip")+'">'+WtfGlobal.getLocaleText("crm.CASE.plural")+'</div>',//Capture detailed description of a customer\'s feedback, problem or questions. Effectively manage cases through regular  tracking of customer queries.
            layout:'fit',
            closable:true,
            id:'CaseHomePanel',
            modName : "Case",
            customParentModName : "Case",
            urlFlag:16,
            addFlag:0,
            accid:'0',
            highLightId:a,
            fromExternalLink:true,
            activityId:b,
            searchparam:c,
            iconCls:getTabIconCls(Wtf.etype.cases),
            newFlag:2,
            clearFlag:false
        });      
        mainPanel.add(panel);
        panel.on("activate",function(){
            Wtf.getCmp("CaseHomePanelcasepan").doLayout();
        },this);
        if(a!=undefined){
        	panel.on("render", function(p){
        		this.EditorStore.on('load', function(){
        			if(Wtf.highLightSearch(a,this.EditorStore,"caseid")!=null){
        				Wtf.highLightGlobal(a,this.EditorGrid,panel.EditorStore,"caseid");
        				this.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,this.EditorStore,"caseid"));
        				if(b!=undefined){
        					this.activityId = b;
        					this.showActivity();
        				}
        			}else{
        				WtfComMsgBox(16);
        			}
        		},this,{single:true});
        	}, panel);
        }
    } else {
        mainPanel.setActiveTab(panel);
        Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"caseid");
        panel.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,panel.EditorStore,"caseid"));
        if(b!=undefined){
        	panel.activityId = b;
            panel.showActivity();
        }
        showAdvanceSearch(panel,c);
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}
function addCampaignTab(a,b,c){
    var panel=Wtf.getCmp('CampaignHomePanel');
    if(panel==null)
    {
        panel = new Wtf.campaignEditor({
            border:false,
            title:"<div id='campaigntab' wtf:qtip='"+WtfGlobal.getLocaleText("crm.dashboard.campaigns.ttip")+"'>"+WtfGlobal.getLocaleText("crm.CAMPAIGN.plural")+"</div>",//Maintain comprehensive details of marketing initiatives such as an advertisement, direct mail, or conference that you conduct in order to generate prospects and build brand awareness.
            layout:'fit',
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.campaign),
            id:'CampaignHomePanel',
            modName : "Campaign",
            customParentModName : "Campaign",
            newFlag:2,
            highLightId:a,
            fromExternalLink:true,
            activityId:b,
            searchparam:c,
            clearFlag:false
        });
      
        mainPanel.add(panel);
        panel.on("activate",function(){
            Wtf.getCmp("CampaignHomePanelcampaignpan").doLayout();
        },this);
        if(a!=undefined){
        panel.on("render", function(p){
            this.EditorStore.on('load', function(){
                 if(Wtf.highLightSearch(a,this.EditorStore,"campaignid")!=null){
                     Wtf.highLightGlobal(a,this.EditorGrid,panel.EditorStore,"campaignid");
                     this.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,this.EditorStore,"campaignid"));
                     if(b!=undefined){
                    	 this.activityId = b;
                         this.showActivity();
                     }
                 }else{
                	 WtfComMsgBox(16);
                 }
            },this,{single:true});
        }, panel);
        }
    } else {
        mainPanel.setActiveTab(panel);
            Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"campaignid");
            panel.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,panel.EditorStore,"campaignid"));
            if(b!=undefined){
            	panel.activityId = b;
            	panel.showActivity();
            }
    }
    showAdvanceSearch(panel,c);
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function addCampaignWithEmailCampaignTab(rec){

      addCampaignTab(rec.ID);

      if(rec.campaigntype=="Email Campaign"){
            
            var campId = 'campaigndetail'+rec.ID;
            var tipTitle =WtfGlobal.getLocaleText({key:"crm.campaign.tabtitle",params:[rec.campaignname]});//'Campaigns : '+rec.campaignname
            var title= Wtf.util.Format.ellipsis(tipTitle,17);
            var campComp = Wtf.getCmp(campId);
            if(campComp==null) {
                campComp=new Wtf.campaignDetails({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.msg.title")+"'>"+title+"</div>",
                    id:campId,
                    campaignid : rec.ID,
                    newFlag:3,
                    arcFlag:1,
                    mainTab:Wtf.getCmp('CampaignHomePanelcampaignTabPanel'),
                    archivedFlag:1,
                    campaignname:rec.campaignname
                })
                Wtf.getCmp('CampaignHomePanelcampaignTabPanel').add(campComp);
            }
            Wtf.getCmp('CampaignHomePanelcampaignTabPanel').setActiveTab(campComp);
            mainPanel.doLayout();
      }

}

function showalertwindow()
{
    new Wtf.alertWindow({
        scope:this,
        layout:'fit',
        id:'alert'
    })
}
//function showgraphaccountbyrevenue() {
//    this.Panel = new Wtf.Panel({
//        region:'center',
//        border:false,
//        width:'50%',
//        layout:'fit',
//        items:[new Wtf.Panel({
//            id:'chartPanel',
//            frame:false
//        })]
//    });
//    if(Wtf.getCmp('chartPanel')){
//        Wtf.getCmp('chartPanel').on("render", function(){
//            var swf="../../scripts/graph/krwcolumn/krwcolumn/krwcolumn.swf";
//            var data="../../scripts/graph/krwcolumn/examples/AccountByRevenue/accountbyrevenue_data.xml"
//            var pid = Wtf.getCmp('chartPanel').body.dom.id;
//            createNewChart(swf,'krwpie', '100%', '100%', '8', '#FFFFFF', '../../scripts/graph/krwcolumn/examples/AccountByRevenue/accountbyrevenue_settings.xml',data, pid);
//        }, this);
//    }
//    var variancereportPanel =Wtf.getCmp("ProjectAnalysisChart");
//    if(variancereportPanel==null){
//        variancereportPanel = new Wtf.Panel({
//            id: "ProjectAnalysisChart",
//            border : false,
//            title : "Project Analysis",
//            autoScroll:true,
//            layout:'fit',
//            closable: true,
//            items:[this.Panel]
//        });
//        mainPanel.add(variancereportPanel);
//    } else {
//        var swf="../../scripts/graph/krwcolumn/krwcolumn/krwcolumn.swf";
//        var data="../../scripts/graph/krwcolumn/examples/AccountByRevenue/accountbyrevenue_data.xml"
//        var pid = 'chartPanel';
//        createNewChart(swf,'krwpie', '100%', '100%', '8', '#FFFFFF', '../../scripts/graph/krwcolumn/examples/AccountByRevenue/accountbyrevenue_settings.xml',data, pid);
//    }
//    mainPanel.setActiveTab(variancereportPanel)
//    mainPanel.doLayout();
//}
function addProductMasterTab(a,b,c){
    var panel=Wtf.getCmp('ProductHomePanel');
    if(panel==null) {
    panel = new Wtf.productEditor({
            border:false,
            title:"<div id='productstab' wtf:qtip='"+WtfGlobal.getLocaleText("crm.dashboard.products.ttip")+"'>"+WtfGlobal.getLocaleText("crm.PRODUCT.plural")+"</div>",//Maintain comprehensive details of items or services that you sell to your customers. You can also record associated vendor details here.
            layout:'fit',
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.product),
            modName : "Product",
            customParentModName : "Product",
            id:'ProductHomePanel',
            mapid:'0',
            newFlag:2,
            searchparam:c,
            highLightId:a,
            fromExternalLink:true,
            clearFlag:false
        });
        mainPanel.add(panel);
        panel.on("activate",function(){
            Wtf.getCmp("ProductHomePanelproductpan").doLayout()
        },this)
        if(a!=undefined){
        	panel.on("render",function(p){
        		this.EditorStore.on("load",function(){
        			if(Wtf.highLightSearch(a,this.EditorStore,"productid")!=null){
        				Wtf.highLightGlobal(a,this.EditorGrid,panel.EditorStore,"productid");
                        this.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(a,this.EditorStore,"productid"));
        			}
        		});
        	});
        }
    } else {
       mainPanel.setActiveTab(panel);
       Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"productid");
       showAdvanceSearch(panel,c);
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function openCustomReportTab(rno, rname, rcategory, rtitle, detailFlag, groupCol, groupValID, groupVal) {    
    var val=Object;    
    if(detailFlag) {
        val=getCustomReportDetails(rno, groupVal+" ("+rname+")", rcategory, rtitle, "detailcustomreport_"+groupValID);
        val.baseParams.filterCol = groupCol;
        val.baseParams.filterSS = groupValID;
        val.baseParams.detailFlag = detailFlag;
        val.detailFlag = detailFlag;
    } else {
        val=getCustomReportDetails(rno, rname, rcategory, rtitle);
    }
    var title = Wtf.util.Format.ellipsis(val.title,18);
    var panel=Wtf.getCmp(val.id);
    if(panel==null) {
        panel= new Wtf.Panel({
            title:"<span wtf:qtip=\'"+val.tooltip+"\'>"+title+"</span>",
            id:val.id,
            layout:'fit',
            border:false,
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.reports),
            items:new Wtf.openCustomReportTab({
                scope:this,
                head:val.head,
                layout:'fit',
                id:val.InnerID,
                border:false,
                stageflag:val.stageflag,
                sourceflag:val.sourceflag,
                details:val
            })
        });
        mainPanel.add(panel);        
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function getCustomReportDetails(rno, rname, rcategory, rtitle, parentId) {
    if(parentId == undefined){
        parentId="customreport";
    }
    var x=new Object;
    x.report="report";
    x.swf="../../scripts/graph/krwcolumn/krwpie/krwpie.swf";
    x.swf2="../../scripts/graph/krwcolumn/krwcolumn/krwcolumn.swf";
    x.title=rname;
    x.id=parentId+rno;
    x.InnerID=parentId+"inner"+x.id;
    x.helpID=13;
    x.reportno=rno;
    x.tooltip="";//Wtf.util.Format.ellipsis(rtitle,36);
    x.rcategory=rcategory;
    x.head=3;
    x.sourceflag=2;
    x.url=Wtf.req.customReport+'rbuild/getCustomReportData.do';
    x.exportUrl=Wtf.req.customReport+'rbuild/getCustomReportExportData.do';
    x.stageflag=1;
    x.name=rname;
    x.baseParams={report_categoty:rcategory,reportno:rno};
    x.chartid=parentId+rno;
    x.id2=parentId+rno;
    x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityByStageBarChart.do?comboname=Opportunity Stage";
    x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunities by Stage&unit='+Wtf.pref.CurrencySymbol;
    x.id1=parentId+"OppbyStagebargraph";
    x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityByStagePieChart.do?comboname=Opportunity Stage";
    x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Opportunities by Stage&unit='+Wtf.pref.CurrencySymbol;
    x.mainid=x.InnerID+'MainTabPanel';
    x.searchbyemptytext="Enter search text";//WtfGlobal.getLocaleText("crm.entersearchtxt");
    return x;
}

function addAllReportTab(name) {
    var val=Object;
    val=getPaneldetails(name);
    var title = Wtf.util.Format.ellipsis(val.title,18);
    var panel=Wtf.getCmp(val.id);
    if(panel==null) {
        panel= new Wtf.Panel({
            title:"<span wtf:qtip=\'"+val.tooltip+"\'>"+title+"</span>",
            id:val.id,
            layout:'fit',
            border:false,
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.reports),
            items:new Wtf.AllReportTab({
                scope:this,
                head:val.head,
                layout:'fit',
                id:val.InnerID,
                border:false,
                stageflag:val.stageflag,
                sourceflag:val.sourceflag,
                details:val
                
            })
        });
        mainPanel.add(panel);
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}
function addAllActivityReportTab() {
    var val=Object;
    val=getAllActivityPaneldetails();
    var title = Wtf.util.Format.ellipsis(val.title,18);
    var panel=Wtf.getCmp(val.id);
    if(panel==null) {
        panel= new Wtf.Panel({
            title:"<span wtf:qtip=\'"+val.tooltip+"\'>"+title+"</span>",
            id:val.id,
            layout:'fit',
            border:false,
            closable:true,
            iconCls:getTabIconCls(Wtf.etype.reports),
            items:new Wtf.AllReportTab({
                scope:this,
                head:val.head,
                layout:'fit',
                id:val.InnerID,
                border:false,
                stageflag:val.stageflag,
                sourceflag:val.sourceflag,
                details:val

            })
        });
        mainPanel.add(panel);
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function callAuditTrail(){
    var panel = Wtf.getCmp("auditTrail");
    if(panel==null){
        panel = new Wtf.common.WtfAuditTrail({
            layout : "fit",
            title:"<div wtf:qtip='"+WtfGlobal.getLocaleText("crm.dashboard.audittrail.ttip")+"'>"+WtfGlobal.getLocaleText("crm.dashboard.audittrail")+"</div>",//Track all user activities through comprehensive CRM system records
            border : false,
            id : "auditTrail",
            iconCls:getTabIconCls(Wtf.etype.auditTrail),
            closable: true
        });
        mainPanel.add(panel);
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

//function CRMNorthTipAdd(module, store) {
//    var temp2 ="";
//    if(module.trim() =="Activity") {
//           temp2 = " <table ><tr>"+
//                       "<th><span class='dpGrayNorthTip'><b>  Click on a row below to add/update a "+module.trim()+".&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>"+
//                       //"<th><div style='height:14px;width:14px;background-color:#F5C0C0;float:left'></div></th>"+
//                       "<th><div class='dpStyle' style='background-color:#F5C0C0;'></div></th>"+
//                       "<th><span class='dpGrayNorthTip'>&nbsp;<b>Overdue Activity&nbsp;&nbsp;&nbsp;&nbsp;</th>"+
//                       "<th><div class='dpStyle' style='background-color:#99CC99;'></div></th>"+
//                       "<th><span id='trackactivity' class='dpGrayNorthTip'>&nbsp;<b>Current Activity&nbsp;&nbsp;&nbsp;&nbsp;</th>"+
//                       "<th><div class='dpStyle' style='background-color:silver;'></div></th>"+
//                       "<th><span class='dpGrayNorthTip'>&nbsp;<b>Completed Activity&nbsp;&nbsp;&nbsp;&nbsp;</th>"+
//                       "<th><div class='dpStyle' style='background-color:#FFFFFF;'></div></th>"+
//                       "<th><span class='dpGrayNorthTip'>&nbsp;<b>Upcoming Activity</th>"+
//                     "</tr></table>";
//
//    } else {
//         temp2 ="<table ><tr>"+
//                "<th><span class='dpGrayNorthTip'><b>  Click on a row below to add/update a "+module.trim()+".</th>"+
//                "</tr></table>";
//    }
//
//    var panel = new Wtf.Panel({
//        height:20,
//        border:false,
//        temp1:"<table ><tr>"+
//               "<th><span class='dpGrayNorthTip'><b>  No "+module+" has been created. Click on a row below to add a "+module.trim()+".</th>"+
//               "</tr></table>",
//        temp2:temp2,
//        id:'CRMNorthTip'+module,
//        bodyStyle: "background:#DDE7EE;padding:2px 15px 2px 15px",
//        html:"<span class='dpGrayNorthTip'><b>  <img src='../../images/loading.gif' /> Loading...",
//        region:'north'
//    });
//    var tpl=null;
//    store.on('load', function(){
//        if(store.getCount()<2){
//            tpl =  new Wtf.XTemplate( panel.temp1 );
//        } else {
//            tpl =  new Wtf.XTemplate( panel.temp2 );
//        }
//        tpl.overwrite(panel.body,{});
//    });
//    return panel
//}

function getPaneldetails(no,parentId,relatedto,relatedid) {
    if(parentId == undefined){
        parentId="report";
    }
    var x=new Object;
    x.report="report";
    x.swf="../../scripts/graph/krwcolumn/krwpie/krwpie.swf";
    x.swf2="../../scripts/graph/krwcolumn/krwcolumn/krwcolumn.swf";
    x.searchbyemptytext=WtfGlobal.getLocaleText("crm.SEARCHBYTXT");//"Search by ";
    switch(no) {
        case 0:x.title=WtfGlobal.getLocaleText("crm.reportlink.leadsbyindustry");//"Leads by Industry";
                x.id=parentId+"leadsbyindustryID";
                x.helpID=10;
                x.InnerID=parentId+"inner"+x.id;
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:7, quickSearchFields:"Last Name/ Company Name"};
                x.url=Wtf.req.springBase+"Lead/leadReport/leadsByIndustryReport.do";
                x.exportUrl=Wtf.req.springBase+"Lead/leadReport/leadsByIndustryExport.do";
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.leadsbyindustry.ttip");//'Monitor your leads grouped by type of industry.';'Monitor your leads grouped by industry.';
                x.name="LeadsByIndustry";
                x.chartid=parentId+"Leads by Industry";
                x.id1=parentId+"Leadsbyindustrygraph";
                x.pieDataUrl=Wtf.req.springBase+"Lead/leadReport/leadsByIndustryPieChart.do?comboname=Industry";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Leads by Industry';
                x.id2=parentId+"Leadsbyindustrybargraph";
                x.barDataUrl=Wtf.req.springBase+"Lead/leadReport/leadsByIndustryBarChart.do?comboname=Industry";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Leads by Industry [No. of Leads by Industry]';
                x.idWidget=parentId+"Leadsbyindustrylinegraph";
                x.swfWidget="../../scripts/graph/krwcolumn/krwline/krwline.swf";
                x.dataflagWidget=23;
                x.xmlpathWidget='../../scripts/graph/krwcolumn/examples/LeadsByIndustry/LeadsbyIndustryLine_settings.xml';
                x.mainid=x.InnerID+'MainTabPanel';
               // x.searchbyemptytext="Search by LastName/CompanyName";
                break;
        case 1:x.title=WtfGlobal.getLocaleText("crm.reportlink.convertedleads");//"Converted Leads";
                x.id=parentId+"convertedleadsID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=11;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.convertedleads.ttip");//'View list of converted leads.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:5,converted:1,transfered:1, quickSearchFields:"Last Name/ Company Name"};
                x.url=Wtf.req.springBase+"Lead/leadReport/convertedLeadsReport.do";
                x.exportUrl=Wtf.req.springBase+"Lead/leadReport/convertedLeadsExport.do";
                x.name="ConvertedLeads";
                x.chartid=parentId+"Converted Leads";
                x.id1=parentId+"ConvertedLeadsgraph";
                x.id2=parentId+"Convertedleadsbargraph";
                x.barDataUrl=Wtf.req.springBase+"Lead/leadReport/convertedLeadsBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Converted Leads ';
                x.mainid=x.InnerID+'MainTabPanel';
           //     x.searchbyemptytext="Search by Lead Name";
                break;
        case 2:x.title=WtfGlobal.getLocaleText("crm.reportlink.revenuebyopportunitysource");//"Revenue by Opportunity Source";
                x.id=parentId+"opportunitiesbysourceID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=12;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.revenuebyopportunitysource.ttip");//'Monitor your opportunities grouped by type of lead source.';
                x.head=2;
                x.sourceflag=1;
                x.stageflag=1;
                x.name="OpportunitiesBySource";
                x.baseParams={sf:x.sourceflag,flag:(x.stageflag==1?1:6),head:x.head, quickSearchFields:"Opportunity Name"};
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/revenueByOppSourceReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/revenueByOppSourceExport.do";
                x.chartid=parentId+"Opportunity by Source";
                x.id2=parentId+"opportunitybysource";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityBySourceBarChart.do?comboname=Lead Source";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunity by Source [Total Revenue by Lead Source]&unit='+Wtf.pref.CurrencySymbol;
                x.id1=parentId+"RevenuebyOppsourcebargraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityBySourcePieChart.do?comboname=Lead Source";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Revenue by Opportunity Source&unit='+Wtf.pref.CurrencySymbol;
                x.mainid=x.InnerID+'MainTabPanel';
               // x.searchbyemptytext="Search by Opportunity Name";
                break;
        case 3:x.title=WtfGlobal.getLocaleText("crm.reportlink.opportunitiesbystage");//"Opportunities by Stage";
                x.id=parentId+"opportunitiesbystageID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=13;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.opportunitiesbystage.ttip");//'Monitor your opportunities grouped by corresponding stage such as qualified, closed and won.';
                x.head=3;
                x.sourceflag=2;
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/oppByStageReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppByStageExport.do";
                x.stageflag=1;
                x.name="OpportunitiesByStage";
                x.baseParams={sf:x.sourceflag,flag:(x.stageflag==1?1:6),head:x.head, quickSearchFields:"Opportunity Name"};
                x.chartid=parentId+"Opportunities by Stage";
                x.id2=parentId+"opportunitybystage";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityByStageBarChart.do?comboname=Opportunity Stage";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunities by Stage&unit='+Wtf.pref.CurrencySymbol;
                x.id1=parentId+"OppbyStagebargraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityByStagePieChart.do?comboname=Opportunity Stage";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Opportunities by Stage&unit='+Wtf.pref.CurrencySymbol;
                x.mainid=x.InnerID+'MainTabPanel';
              //  x.searchbyemptytext="Search by Opportunity Name";
                break;
        case 44:x.title=WtfGlobal.getLocaleText("crm.reportlink.opportunitiesbysalesperson");//"Opportunities by Sales Person";
                x.id=parentId+"opportunitiesbypersonID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=73;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.opportunitiesbysalesperson.ttip");//'Monitor your opportunities grouped by corresponding sales person.';
                x.head=3;
                x.sourceflag=2;
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/oppBySalesPersonReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppBySalesPersonExport.do";
                x.stageflag=1;
                x.name="OpportunitiesBySalesPerson";
                x.baseParams={sf:x.sourceflag,flag:(x.stageflag==1?1:6),head:x.head, quickSearchFields:"Opportunity Name"};
                x.chartid=parentId+"Opportunities by Sales Person";
                x.id2=parentId+"opportunitybyperson";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityBySalesPersonBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunities by Sales person';
                x.id1=parentId+"OppbySalesPersonbargraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityBySalesPersonPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Opportunities by Sales person';
                x.mainid=x.InnerID+'MainTabPanel';
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbyowner");//"Search by Owner, ";
                break;
        case 45:x.title=WtfGlobal.getLocaleText("crm.reportlink.opportunitiesbyregionhybrid");//"Opportunities by Region(Hybrid)";
                x.id=parentId+"opportunitiesbyregionIDH";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=74;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.opportunitiesbyregion.ttip");//'Monitor your opportunities grouped by region.';
                x.head=3;
                x.sourceflag=2;
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/oppByRegionHReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppByRegionHExport.do";
                x.Grid2Url=Wtf.req.springBase+"Opportunity/opportunityReport/oppByRegionFunnelReport.do";
                x.Grid2ExportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppByRegionFunnelExport.do";
                x.id2=parentId+"OppbyRegionFbargraph";
                x.name="OppbyRegionFunnel";
                x.stageflag=1;
//                x.name="Opportunities by Region";
                x.baseParams={sf:x.sourceflag,flag:(x.stageflag==1?1:6),head:x.head, quickSearchFields:"Region"};
                x.chartid=parentId+"Opportunity by Region(Hybrid)";
                x.id2=parentId+"opportunitybyregionh";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityByRegionHBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunity by Region(Hybrid)';
                x.id1=parentId+"OppbyRegionHbargraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityByRegionHPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Opportunity by Region(Hybrid)';
                x.mainid=x.InnerID+'MainTabPanel';
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbyowner");//"Search by Owner, ";
                break;
        case 4:x.title=WtfGlobal.getLocaleText("crm.reportlink.casesbystatus");//"Cases by Status";
                x.id=parentId+"casebystatusID";
                x.helpID=14;
                x.InnerID=parentId+"inner"+x.id;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.casesbystatus.ttip");//'Monitor customer cases grouped by corresponding status such as new, pending and escalated.';
                x.sourceflag=null;
                x.url=Wtf.req.springBase+"Case/caseReport/caseByStatusReport.do";
                x.exportUrl=Wtf.req.springBase+"Case/caseReport/caseByStatusExport.do";
                x.stageflag=null;
                x.name="CaseByStatus";
                x.baseParams={flag:4, quickSearchFields:"Subject"};
              //  x.searchbyemptytext="Search by Subject";
                x.chartid=parentId+"Cases by Status";
                x.id1=parentId+"casesbystatusgraph";
                x.pieDataUrl=Wtf.req.springBase+"Case/caseReport/casesByStatusPieChart.do?comboname=Case Status";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Cases by Status';
                x.id2=parentId+"CasebyStatusbargraph";
                x.barDataUrl=Wtf.req.springBase+"Case/caseReport/casesByStatusBarChart.do?comboname=Case Status";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Cases by Status';
                x.mainid=x.InnerID+'MainTabPanel';
                break;
        case 5:x.title=WtfGlobal.getLocaleText("crm.reportlink.keyaccounts");//"Key Accounts";
                x.id=parentId+"keyaccountsID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=15;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.keyaccounts.ttip");//'Monitor your key accounts ordered by corresponding revenues.';
                x.sourceflag=null;
                x.stageflag=null;
                x.name="KeyAccounts";
                x.url=Wtf.req.springBase+"Account/accountReport/keyAccountsReport.do";
                x.exportUrl=Wtf.req.springBase+"Account/accountReport/keyAccountsExport.do";
                x.baseParams={flag:3, quickSearchFields:"Account Name"};
             //   x.searchbyemptytext="Search by Account Name";
                x.chartid=parentId+"KeyAccounts";
                x.id2=parentId+"keyaccountgraph";
                x.barDataUrl=Wtf.req.springBase+"Account/accountReport/keyAccountsBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Key Accounts (Highest revenue generating accounts)&title1=Accounts&unit='+Wtf.pref.CurrencySymbol;
                x.id1=parentId+"Keyaccountsbargraph";
                x.pieDataUrl=Wtf.req.springBase+"Account/accountReport/keyAccountsPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Account by Revenue&unit='+Wtf.pref.CurrencySymbol;
                x.mainid=x.InnerID+'MainTabPanel';
                break;
        case 6:x.title=WtfGlobal.getLocaleText("crm.reportlink.salesbysource");//"Sales by Source";
                x.id=parentId+"salesbysourceID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=16;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.salesbysource.ttip");//'Monitor your sales grouped by type of lead source & stage as closed won.';
                x.sourceflag=1;
                x.stageflag=2;
                x.name="Salesbysource";
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/salesReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/salesExport.do";
                x.baseParams={sf:x.sourceflag,flag:(x.stageflag==1?1:6), quickSearchFields:"Opportunity Name"};
                x.chartid=parentId+"Sales by Source";
                x.id2=parentId+"salesbysource";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/salesBySourceBarChart.do?comboname=Lead Source";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Sales by Source&unit='+Wtf.pref.CurrencySymbol;
                x.id1=parentId+"SalesbySourcebargraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/salesBySourcePieChart.do?comboname=Lead Source";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Sales by Source&unit='+Wtf.pref.CurrencySymbol;
                x.mainid=x.InnerID+'MainTabPanel';
              //  x.searchbyemptytext="Search by Opportunity Name";
                break;
        case 7:x.title=WtfGlobal.getLocaleText("crm.reportlink.leadsbysource");//"Leads by Source";
                x.id=parentId+"leadsbysourceID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=17;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.leadsbysource.ttip");//'Monitor your leads grouped by corresponding source.';
                x.sourceflag=null;
                x.stageflag=null;
                x.name="LeadbySource";
                x.baseParams={flag:2,transfered:0, quickSearchFields:"Last Name/ Company Name"};
                x.url=Wtf.req.springBase+"Lead/leadReport/leadsBySourceReport.do";
                x.exportUrl=Wtf.req.springBase+"Lead/leadReport/leadsBySourceExport.do";
                x.chartid=parentId+"Leads by Source";
                x.id1=parentId+"Leadsbysourcegraph";
                x.pieDataUrl=Wtf.req.springBase+"Lead/leadReport/leadsBySourcePieChart.do?comboname=Lead Source";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Leads by Source';
                x.id2=parentId+"Leadsbysourcebargraph";
                x.barDataUrl=Wtf.req.springBase+"Lead/leadReport/leadsBySourceBarChart.do?comboname=Lead Source";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Leads by Source [No. of Leads by Lead Source]';
                x.mainid=x.InnerID+'MainTabPanel';
             //   x.searchbyemptytext="Search by Lead Name";
                break;
        case 8:x.title=WtfGlobal.getLocaleText("crm.reportlink.closedopportunities");//"Closed Opportunities";
                x.id=parentId+"oppclosedID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=18;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.closedopportunities.ttip");//'Get the list of Opportunities who are Closed-won.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:8, quickSearchFields:"Opportunity Name"};
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/closedOppReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/closedOppExport.do";
                x.chartid=parentId+"Closed Opportunity";
                x.id1=parentId+"ClosedOpportunitygraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/closedOppPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Closed Opportunities by Sales';
                x.id2=parentId+"ClosedOppbargraph";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/closedOppBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Closed Opportunities by Sales';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="OppClosed";
              //  x.searchbyemptytext="Search by Opportunity Name";
                break;
        case 9:x.title=WtfGlobal.getLocaleText("crm.reportlink.opportunitiesbytype");//"Opportunities by Type";
                x.id=parentId+"oppbytypeID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=19;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.opportunitiesbytype.ttip");//'Get the list of Opportunities with respect to their Type.';
                x.sourceflag=null;
                x.stageflag=null;
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/oppByTypeReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppByTypeExport.do";
                x.baseParams={flag:9, quickSearchFields:"Opportunity Name"};
                x.chartid=parentId+"Opportunities by Type";
                x.id1=parentId+"OpportunitiesByTypegraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityByTypePieChart.do?comboname=Opportunity Type";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Opportunity by Type';
                x.id2=parentId+"OppbyTypebargraph";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityByTypeBarChart.do?comboname=Opportunity Type";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunity by Type [No. of Opportunities by Type]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="OppByType";
              //  x.searchbyemptytext="Search by Opportunity Name";
                break;
        case 10:x.title=WtfGlobal.getLocaleText("crm.reportlink.opportunityproductreport");//"Opportunity Product Report";
                x.id=parentId+"oppproductID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=20;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.opportunityproductreport.ttip");//'Get the list of Opportunities and the Product that attracted them.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:10, quickSearchFields:"Opportunity Name"};
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/oppProductReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppProductExport.do";
                x.chartid=parentId+"Opportunity Product Report";
                x.id1=parentId+"OpportunityProductgraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityByProductPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Opportunity by Product';
                x.id2=parentId+"OppProductbargraph";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/opportunityByProductBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunity by Product';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="OppProduct";
               // x.searchbyemptytext="Search by Opportunity Name";
                break;
        case 11:x.title=WtfGlobal.getLocaleText("crm.reportlink.stuckopportunities");//"Stuck Opportunities";
                x.id=parentId+"stuckoppID";
                x.helpID=21;
                x.InnerID=parentId+"inner"+x.id;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.stuckopportunities.ttip");//'Get the list of Opportunities whose Probability is less than 50%.';
                x.sourceflag=null;
                x.stageflag=null;
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/stuckOppReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/stuckOppExport.do";
                x.baseParams={flag:11, quickSearchFields:"Opportunity Name"};
                x.chartid=parentId+"Stuck Opportunity";
                x.id1=parentId+"StuckOpportunitygraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/stuckOpportunitiesPieChart.do?comboname=Opportunity Stage";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title='+encodeURIComponent("Stuck Opportunities (Opportunities having less than 50% Probablitiy) by Stage");
                x.id2=parentId+"StuckOppbargraph";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/stuckOpportunitiesBarChart.do?comboname=Opportunity Stage";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Stuck Opportunities [No. of Stuck Opportunities by Stage]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="StuckOpp";
              //  x.searchbyemptytext="Search by Opportunity Name";
                break;
        case 12:x.title=WtfGlobal.getLocaleText("crm.reportlink.monthlyaccounts");//"Monthly Accounts";
                x.id=parentId+"accountmonthID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=22;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.monthlyaccounts.ttip");//'Get the list of Accounts created every month.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:12, quickSearchFields:"Account Name"};
                x.chartid=parentId+"Accounts Per Month";
                x.url=Wtf.req.springBase+"Account/accountReport/monthlyAccountsReport.do";
                x.exportUrl=Wtf.req.springBase+"Account/accountReport/monthlyAccountsExport.do";
                x.id2=parentId+"AccountsPerMonthgraph";
                x.barDataUrl=Wtf.req.springBase+"Account/accountReport/monthlyAccountsBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Monthly Accounts';
                x.id1=parentId+"Monthlyaccountsbargraph";
                x.pieDataUrl=Wtf.req.springBase+"Account/accountReport/monthlyAccountsPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Monthly Accounts';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="AccountMonth";
               // x.searchbyemptytext="Search by Account Name";
                break;
        case 13:x.title=WtfGlobal.getLocaleText("crm.reportlink.accountowners");//"Account Owners";
                x.id=parentId+"accountownerID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=23;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.accountowners.ttip");//'Get the list of Accounts and their respective Owners.';
                x.sourceflag=null;
                x.stageflag=null;
                x.url=Wtf.req.springBase+"Account/accountReport/accountOwnersReport.do";
                x.exportUrl=Wtf.req.springBase+"Account/accountReport/accountOwnersExport.do";
                x.baseParams={flag:13, quickSearchFields:"Account Name"};
                x.chartid=parentId+"Account Owners";
                x.id1=parentId+"AccountOwnergraph";
                x.pieDataUrl=Wtf.req.springBase+"Account/accountReport/accountsByOwnerPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Accounts by Owner';
                x.id2=parentId+"Accountownersbargraph";
                x.barDataUrl=Wtf.req.springBase+"Account/accountReport/accountsByOwnerBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Account Owners [No. of Accounts by Account Owner]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="AccountOwners";
                //x.searchbyemptytext="Search by Account Name";
                break;
        case 14:x.title=WtfGlobal.getLocaleText("crm.reportlink.sourcesofopportunities");//"Sources of Opportunities";
                x.id=parentId+"oppsourceID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=24;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.sourcesofopportunities.ttip");//'Get the list of Opportunities with respect to their Source.';
                x.sourceflag=null;
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/sourceOfOppReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/sourceOfOppExport.do";
                x.stageflag=null;
                x.baseParams={flag:14, quickSearchFields:"Opportunity Name"};
                x.chartid=parentId+"Sources Of Opportunities";
                x.id1=parentId+"SourceofOppgraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/sourceOffOppPieChart.do?comboname=Lead Source";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Sources Of Opportunities';
                x.id2=parentId+"SourceofOppbargraph";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/sourceOffOppBarChart.do?comboname=Lead Source";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Sources of Opportunities [No. of Opportunities by Lead Source]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="OppSource";
             //   x.searchbyemptytext="Search by Opportunity Name";
                break;
        case 15:x.title=WtfGlobal.getLocaleText("crm.reportlink.highpriorityactivities");//"High Priority Activities";
                x.id=parentId+"highpriorityactivityID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=25;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.highpriorityactivities.ttip");//'Get the list of Activities who are of High Priority and having status as Not Started.';
                x.sourceflag=null;
                x.stageflag=null;
                x.relatedto=relatedto,// related module name for activity
                x.baseParams=relatedto!=undefined?{flag:15,filterCombo:relatedto,relatedid:relatedid}:{flag:15};
                x.url=Wtf.req.springBase+"Activity/activityReport/HighPriorityActivitiesReport.do";
                x.exportUrl=Wtf.req.springBase+"Activity/activityReport/HighPriorityActivitiesExport.do";
                x.chartid=parentId+"High Priority Activities";
                x.id1=parentId+"HighPriorityActivitygraph";
                x.pieDataUrl=Wtf.req.springBase+"Activity/activityReport/highPriorityActivityPieChart.do?comboname=Related To";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=High Priority Activities';
                x.id2=parentId+"HighPriorityActivitybargraph";
                x.barDataUrl=Wtf.req.springBase+"Activity/activityReport/highPriorityActivityBarChart.do?comboname=Related To";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=High Priority Activities';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="HighPriorityActivity";
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbyaccountowner");//"Search by Account Owner";
                break;
        case 16:x.title=WtfGlobal.getLocaleText("crm.reportlink.contactswithhighprioritycases");//"Contacts with High Priority Cases";
                x.id=parentId+"contactshighpriorityID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=26;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.contactswithhighprioritycases.ttip");//'Get the list of Contacts who have Cases with High Priority and are yet to Start.';
                x.sourceflag=null;
                x.stageflag=null;
                x.url=Wtf.req.springBase+"Case/caseReport/contactsHighPriorityCasesReport.do";
                x.exportUrl=Wtf.req.springBase+"Case/caseReport/contactsHighPriorityCasesExport.do";
                x.baseParams={flag:16};
                x.chartid=parentId+"Contacts with High Priority Cases";
                x.id1=parentId+"ContactHighPrioritygraph";
                x.pieDataUrl=Wtf.req.springBase+"Case/caseReport/contactHighPriorityPieChart.do?comboname=Title";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Contacts with Cases';
                x.id2=parentId+"ContactCasesbargraph";
                x.barDataUrl=Wtf.req.springBase+"Case/caseReport/contactHighPriorityBarChart.do?comboname=Title";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Contacts with Cases [No. of Contacts with Cases by Contact Name]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="ContactsCases";
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbycontactowner");//"Search by Contact Owner";
                break;
        case 17:x.title=WtfGlobal.getLocaleText("crm.reportlink.productswithcasesbypriority");//"Products with Cases by Priority";
                x.id=parentId+"producthighpriorityID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=27;
                x.url=Wtf.req.springBase+"Case/caseReport/productCasesReport.do";
                x.exportUrl=Wtf.req.springBase+"Case/caseReport/productCaseExport.do";
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.productswithcasesbypriority.ttip");//'Get the list of Products who have Cases with High Priority and are yet to Start.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:17,quickSearchFields:"Product Name"};
                x.chartid=parentId+"Products with Cases by Priority";
                x.id1=parentId+"ProductHighPrioritygraph";
                x.pieDataUrl=Wtf.req.springBase+"Case/caseReport/productCasesPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Products with High Priority Cases';
                x.id2=parentId+"ProductCasesbargraph";
                x.barDataUrl=Wtf.req.springBase+"Case/caseReport/productCasesBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Product with High Priority Cases [High Priority Cases by Product Name]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="ProductsCases";
              //  x.searchbyemptytext="Search by Product Name";
                break;
        case 18:x.title=WtfGlobal.getLocaleText("crm.reportlink.accountwithcasesbypriority");//"Account with Cases by Priority";
                x.id=parentId+"accounthighpriorityID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=28;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.accountwithcasesbypriority.ttip");//'Get the list of Accounts who have Cases with Priority and are yet to Start.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:18,quickSearchFields:"Account Name"};
                x.chartid=parentId+"Accounts with High Priority Cases";
                x.id1=parentId+"AccountHighPrioritygraph";
                x.url=Wtf.req.springBase+"Case/caseReport/accountHighPriorityCasesReport.do";
                x.exportUrl=Wtf.req.springBase+"Case/caseReport/accountHighPriorityCasesExport.do";
                x.pieDataUrl=Wtf.req.springBase+"Case/caseReport/accountHighPriorityPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Account with High Priority Cases';
                x.id2=parentId+"AccountCasesbargraph";
                x.barDataUrl=Wtf.req.springBase+"Case/caseReport/accountHighPriorityBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Account with High Priority Cases [No. of High Priority Cases by Account Name]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="AccountCases";
              //  x.searchbyemptytext="Search by ";
                break;
       case 19:x.title=WtfGlobal.getLocaleText("crm.reportlink.monthlycases");//"Monthly Cases";
                x.id=parentId+"monthlycasesID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=29;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.monthlycases.ttip");//'Get the list of Cases created by month.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:19,quickSearchFields:"Subject"};
                x.chartid=parentId+"Monthly Cases";
                x.id2=parentId+"MonthlyCasesgraph";
                x.barDataUrl=Wtf.req.springBase+"Case/caseReport/monthlyCasesBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Monthly Cases';
                x.url=Wtf.req.springBase+"Case/caseReport/monthlyCasesReport.do";
                x.exportUrl=Wtf.req.springBase+"Case/caseReport/monthlyCasesExport.do";
                x.id1=parentId+"MonthlyCasesbargraph";
                x.pieDataUrl=Wtf.req.springBase+"Case/caseReport/monthlyCasesPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Monthly Cases';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="MonthlyCases";
            //    x.searchbyemptytext="Search by Case Subject";
                break;
       case 20:x.title=WtfGlobal.getLocaleText("crm.reportlink.industryaccounttypereport");//"Industry-Account Type Report";
                x.id=parentId+"industry-accounttypeID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=30;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.industryaccounttypereport.ttip");//'Get the list of Accounts and the Industry they belong. Select an Account Type to populate the record in the report.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:20,quickSearchFields:"Account Name"};
                x.chartid=parentId+"Industry Account Type Report";
                x.id1=parentId+"IndustryAccounttypegraph";
                x.pieDataUrl=Wtf.req.springBase+"Account/accountReport/industryAccountTypePieChart.do?comboname=Industry";
                x.url=Wtf.req.springBase+"Account/accountReport/industryAccountTypeReport.do";
                x.exportUrl=Wtf.req.springBase+"Account/accountReport/industryAccountTypeExport.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Industry-Account Type Report';
                x.id2=parentId+"IndustryAccountbargraph";
                x.barDataUrl=Wtf.req.springBase+"Account/accountReport/industryAccountTypeBarChart.do?comboname=Industry";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Industry-Account Type [No. of Accounts by Industry]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="IndustryAccount";
              //  x.searchbyemptytext="Search by Account Name";
                break;
       case 21:x.title=WtfGlobal.getLocaleText("crm.reportlink.campaignsbytype");//"Campaigns by Type";
                x.id=parentId+"campaigntypeID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=37;
                x.url=Wtf.req.springBase+"Campaign/campaignReport/campaignByTypeReport.do";
                x.exportUrl=Wtf.req.springBase+"Campaign/campaignReport/campaignByTypeExport.do";
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.campaignsbytype.ttip");//'Get the list of Campaigns according to their type.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:21,quickSearchFields:"Campaign Name"};
                x.chartid=parentId+"CampaignType";
                x.id1=parentId+"campaigntypegraph";
                x.pieDataUrl=Wtf.req.springBase+"Campaign/campaignReport/campaignTypePieChart.do?comboname=Campaign Type";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Campaigns by Type';
                x.id2=parentId+"campaigntypebargraph";
                x.barDataUrl=Wtf.req.springBase+"Campaign/campaignReport/campaignTypeBarChart.do?comboname=Campaign Type";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Campaigns by Type [No. of Campaigns by Campaign Type]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="CampaignType";
              //  x.searchbyemptytext="Search by Campaign Name";
                break;
       case 22:x.title=WtfGlobal.getLocaleText("crm.reportlink.completedcampaignsbytype");//"Completed Campaigns by Type";
                x.id=parentId+"completedcampaignsID";
                x.InnerID=parentId+"inner"+x.id;
                x.url=Wtf.req.springBase+"Campaign/campaignReport/completedCampaignByTypeReport.do";
                x.exportUrl=Wtf.req.springBase+"Campaign/campaignReport/completedCampaignByTypeExport.do";
                x.helpID=38;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.completedcampaignsbytype.ttip");//'Get the list of Campaigns who have their status marked as complete.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:22,quickSearchFields:"Campaign Name"};
                x.chartid=parentId+"CompletedCampaign";
                x.id1=parentId+"completedcampaigngraph";
                x.pieDataUrl=Wtf.req.springBase+"Campaign/campaignReport/completedCampaignPieChart.do?comboname=Campaign Type";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Completed Campaigns';
                x.id2=parentId+"completedcampaignbargraph";
                x.barDataUrl=Wtf.req.springBase+"Campaign/campaignReport/completedCampaignBarChart.do?comboname=Campaign Type";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Completed Campaigns [No. of Completed Campaigns by Campaign Type]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="CompletedCampaign";
             //   x.searchbyemptytext="Search by Campaign Name";
                break;
       case 23:x.title=WtfGlobal.getLocaleText("crm.reportlink.qualifiedleads");//"Qualified Leads";
                x.id=parentId+"qualifiedleadsID";
                x.InnerID=parentId+"inner"+x.id;
                x.url=Wtf.req.springBase+"Lead/leadReport/qualifiedLeadsReport.do";
                x.exportUrl=Wtf.req.springBase+"Lead/leadReport/qualifiedLeadsExport.do";
                x.helpID=39;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.qualifiedleads.ttip");//'Get the list of leads who have their status as Qualified.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:23,quickSearchFields:"Last Name/ Company Name"};
                x.chartid=parentId+"QualifiedLead";
                x.id1=parentId+"qualifiedleadgraph";
                x.id2=parentId+"qualifiedleadbargraph";
                x.barDataUrl=Wtf.req.springBase+"Lead/leadReport/convertedLeadsBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Qualified Leads ';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="QualifiedLead";
            //    x.searchbyemptytext="Search by Lead Name";
                break;
       case 24:x.title=WtfGlobal.getLocaleText("crm.reportlink.accountswithcontacts");//"Accounts with Contacts";
                x.id=parentId+"accountcontactID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=40;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.accountswithcontacts.ttip");//'Get the list of Accounts who have Contacts.';
                x.sourceflag=null;
                x.url=Wtf.req.springBase+"Contact/contactReport/accountsWithContactReport.do";
                x.exportUrl=Wtf.req.springBase+"Contact/contactReport/accountsWithContactExport.do";
                x.stageflag=null;
                x.baseParams={flag:24,quickSearchFields:"Account Name"};
                x.chartid=parentId+"AccountWithContacts";
                x.id1=parentId+"accountcontactgraph";
                x.pieDataUrl=Wtf.req.springBase+"Contact/contactReport/accountContactPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Accounts with Contacts';
                x.id2=parentId+"completedcampaignbargraph";
                x.barDataUrl=Wtf.req.springBase+"Contact/contactReport/accountContactBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Account with Contacts [No. of Contacts by Account Name]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="AccountWithContacts";
              //  x.searchbyemptytext="Search by Account Name";
                break;
       case 25:x.title=WtfGlobal.getLocaleText("crm.reportlink.campaignsbygoodresponse");//"Campaigns with Good Response";
                x.id=parentId+"campaignresponseID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=41;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.campaignsbygoodresponse.ttip");//'Get the list of Campaigns who have generated response greater than 70%.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:25,quickSearchFields:"Campaign Name"};
                x.chartid=parentId+"CampaignResponse";
                x.url=Wtf.req.springBase+"Campaign/campaignReport/campaignWithGoodResponseReport.do";
                x.exportUrl=Wtf.req.springBase+"Campaign/campaignReport/campaignWithGoodResponseExport.do";
                x.id1=parentId+"campaignresponsegraph";
                x.pieDataUrl=Wtf.req.springBase+"Campaign/campaignReport/campaignResponsePieChart.do?comboname=Campaign Type";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title='+encodeURIComponent("Campaign with Good Response [Response > 70%]");
                x.id2=parentId+"campaignresponsebargraph";
                x.barDataUrl=Wtf.req.springBase+"Campaign/campaignReport/campaignResponseBarChart.do?comboname=Campaign Type";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title='+encodeURIComponent("Campaign with Good Response [Response > 70%]");
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="CampaignResponse";
               // x.searchbyemptytext="Search by Campaign Name";
                break;
       case 26:x.title=WtfGlobal.getLocaleText("crm.reportlink.contactedleads");//"Contacted Leads";
                x.id=parentId+"leadaccountsID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=42;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.contactedleads.ttip");//'Get the list of Leads who have their status as Contacted.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:26,quickSearchFields:"Last Name/ Company Name"};
                x.chartid=parentId+"ContactedLeads";
                x.id1=parentId+"leadaccountsgraph";
                x.url=Wtf.req.springBase+"Lead/leadReport/contactedLeadsReport.do";
                x.exportUrl=Wtf.req.springBase+"Lead/leadReport/contactedLeadsExport.do";
                x.id2=parentId+"leadaccountsbargraph";
                x.barDataUrl=Wtf.req.springBase+"Lead/leadReport/convertedLeadsBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Contacted Leads ';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="ContactedLeads";
              //  x.searchbyemptytext="Search by Lead Name";
                break;
       case 27:x.title=WtfGlobal.getLocaleText("crm.reportlink.contactsbyleadsource");//"Contacts by Lead Source";
                x.id=parentId+"leadcontactsID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=43;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.contactsbyleadsource.ttip");//'Get the list of lead source who have Contacts.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:27};
                x.chartid=parentId+"LeadSourceContacts";
                x.id1=parentId+"leadcontactsgraph";
                x.url=Wtf.req.springBase+"Contact/contactReport/contactsByLeadSourceReport.do";
                x.exportUrl=Wtf.req.springBase+"Contact/contactReport/contactsByLeadSourceExport.do";
                x.pieDataUrl=Wtf.req.springBase+"Contact/contactReport/leadSourceContactsPieChart.do?comboname=Lead Source";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Lead Source with Contacts';
                x.id2=parentId+"leadcontactsbargraph";
                x.barDataUrl=Wtf.req.springBase+"Contact/contactReport/leadSourceContactsBarChart.do?comboname=Lead Source";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Lead Source with Contacts [No. of Contacts by Lead Source]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="LeadSourceContacts";
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbycontactname");//"Search by Contact Name";
                break;
       case 28:x.title=WtfGlobal.getLocaleText("crm.reportlink.accountswithopportunities");//"Accounts with Opportunity";
                x.id=parentId+"accountoppID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=44;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.accountswithopportunities.ttip");//'Get the list of Accounts who have Opportunities.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:28,quickSearchFields:"Account Name"};
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/accountsWithOpportunityReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/accountsWithOpportunityExport.do";
                x.chartid=parentId+"AccountWithOpp";
                x.id1=parentId+"accountoppgraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/accountOpportunityPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Accounts with Opportunities&group_percent=0';
                x.id2=parentId+"accountoppbargraph";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/accountOpportunityBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Accounts with Opportunities [No. of Opportunities by Account Name]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="AccountWithOpp";
            //    x.searchbyemptytext="Search by Account Name";
                break;
       case 29:x.title=WtfGlobal.getLocaleText("crm.reportlink.opportunitiesbyleadsource");//"Opportunities by Lead Source";
                x.id=parentId+"leadoppID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=45;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.opportunitiesbyleadsource.ttip");//'Get the list of lead source who have Opportunities.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:29,quickSearchFields:"Opportunity Name"};
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/oppByLeadSourceReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppByLeadSourceExport.do";
                x.chartid=parentId+"LeadOpportunity";
                x.id1=parentId+"leadoppgraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/leadOpportunityPieChart.do?comboname=Lead Source";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Opportunities by Lead Source';
                x.id2=parentId+"leadoppbargraph";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/leadOpportunityBarChart.do?comboname=Lead Source";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunities by Lead Source';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="OpportunitiesByLeadSource";
             //   x.searchbyemptytext="Search by Opportunity Name";
                break;
       case 30:x.title=WtfGlobal.getLocaleText("crm.reportlink.newlyaddedcases");//"Newly Added Cases";
                x.id=parentId+"newlyaddedcaseID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=46;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.newlyaddedcases.ttip");//'Get the list of Newly Added Cases.';
                x.sourceflag=null;
                x.stageflag=null;
                x.url=Wtf.req.springBase+"Case/caseReport/newlyAddedCasesReport.do";
                x.exportUrl=Wtf.req.springBase+"Case/caseReport/newlyAddedCasesExport.do";
                x.baseParams={flag:30,quickSearchFields:"Subject"};
                x.chartid=parentId+"NewlyAddedCases";
                x.id1=parentId+"newlyaddedcasesgraph";
                x.pieDataUrl=Wtf.req.springBase+"Case/caseReport/newlyAddedCasesPieChart.do?comboname=Priority";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Newly Added Cases by Priority';
                x.id2=parentId+"newlyaddedcasesbargraph";
                x.barDataUrl=Wtf.req.springBase+"Case/caseReport/newlyAddedCasesBarChart.do?comboname=Priority";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Newly Added Cases [No.of Newly Added Cases by Priority]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="NewlyAddedCases";
              //  x.searchbyemptytext="Search by Case Subject";
                break;
       case 31:x.title=WtfGlobal.getLocaleText("crm.reportlink.pendingcases");//"Pending Cases";
                x.id=parentId+"pendingcasesID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=47;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.pendingcases.ttip");//'Get the list of Pending Cases.';
                x.sourceflag=null;
                x.url=Wtf.req.springBase+"Case/caseReport/pendingCasesReport.do";
                x.exportUrl=Wtf.req.springBase+"Case/caseReport/pendingCasesExport.do";
                x.stageflag=null;
                x.baseParams={flag:31,quickSearchFields:"Subject"};
                x.chartid=parentId+"PendingCases";
                x.id1=parentId+"pendingcasesgraph";
                x.pieDataUrl=Wtf.req.springBase+"Case/caseReport/pendingCasesPieChart.do?comboname=Priority";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Priority of Pending Cases';
                x.id2=parentId+"completedcampaignbargraph";
                x.barDataUrl=Wtf.req.springBase+"Case/caseReport/pendingCasesBarChart.do?comboname=Priority";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Pending Cases [No. of Pending Cases by Priority]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="PendingCases";
               // x.searchbyemptytext="Search by Case Subject";
                break;
       case 32:x.title=WtfGlobal.getLocaleText("crm.reportlink.escalatedcases");//"Escalated Cases";
                x.id=parentId+"excalatedcasesID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=48;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.escalatedcases.ttip");//'Get the list of Escalated Cases.';
                x.url=Wtf.req.springBase+"Case/caseReport/escalatedCasesReport.do";
                x.exportUrl=Wtf.req.springBase+"Case/caseReport/escalatedCasesExport.do";
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:32, quickSearchFields:"Subject"};
                x.chartid=parentId+"EscalatedCases";
                x.id1=parentId+"escalatedcasesgraph";
                x.pieDataUrl=Wtf.req.springBase+"Case/caseReport/escalatedCasesPieChart.do?comboname=Priority";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Priority of Escalated Cases';
                x.id2=parentId+"escalatedcasesbargraph";
                x.barDataUrl=Wtf.req.springBase+"Case/caseReport/escalatedCasesBarChart.do?comboname=Priority";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Escalated Cases [No. of Escalated Cases by Priority]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="EscalatedCases";
              //  x.searchbyemptytext="Search by Case Subject";
                break;
       case 33:x.title=WtfGlobal.getLocaleText("crm.reportlink.accountswithcases");//"Account with Cases";
                x.id=parentId+"accountcasesID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=49;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.accountswithcases.ttip");//'Get the list of Accounts who have Cases.';
                x.sourceflag=null;
                x.stageflag=null;
                x.url=Wtf.req.springBase+"Case/caseReport/accountsWithCaseReport.do";
                x.exportUrl=Wtf.req.springBase+"Case/caseReport/accountsWithCaseExport.do";
                x.baseParams={flag:33, quickSearchFields:"Subject"};
                x.chartid=parentId+"AccountWithCases";
                x.id1=parentId+"accountcasesgraph";
                x.pieDataUrl=Wtf.req.springBase+"Case/caseReport/accountCasesPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Account with Cases';
                x.id2=parentId+"accountcasesbargraph";
                x.barDataUrl=Wtf.req.springBase+"Case/caseReport/accountCasesBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Account with Cases [No. of Cases by Account Name]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="AccountWithCases";
               // x.searchbyemptytext="Search by Case Subject";
                break;
       case 34:x.title=WtfGlobal.getLocaleText("crm.reportlink.openleads");//"Open Leads";
                x.id=parentId+"openleadsID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=50;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.openleads.ttip");//'Get the list of Leads who have their status as Open.';
                x.sourceflag=null;
                x.stageflag=null;
                x.url=Wtf.req.springBase+"Lead/leadReport/openLeadsReport.do";
                x.exportUrl=Wtf.req.springBase+"Lead/leadReport/openLeadsExport.do";
                x.baseParams={flag:34, quickSearchFields:"Last Name/ Company Name"};
                x.chartid=parentId+"OpenLeads";
                x.id1=parentId+"openleadsgraph";
                x.pieDataUrl=Wtf.req.springBase+"Lead/leadReport/openLeadPieChart.do?comboname=Lead Source";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Open Leads by Age';
                x.id2=parentId+"openleadbargraph";
                x.barDataUrl=Wtf.req.springBase+"Lead/leadReport/openLeadBarChart.do?comboname=Lead Source";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Open Leads [No. of Open Leads by Age]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="OpenLeads";
              //  x.searchbyemptytext="Search by Lead Name";
                break;
       case 35:x.title=WtfGlobal.getLocaleText("crm.reportlink.contactswithcases");//"Contacts with Cases";
                x.id=parentId+"contactcaseID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=51;
                x.url=Wtf.req.springBase+"Case/caseReport/contactsWithCasesReport.do";
                x.exportUrl=Wtf.req.springBase+"Case/caseReport/contactsWithCasesExport.do";
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.contactswithcases.ttip");//'Get the list of Contacts who have Cases.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:35, quickSearchFields:"Subject"};
                x.chartid=parentId+"ContactWithCase";
                x.id1=parentId+"contactwithCasegraph";
                x.pieDataUrl=Wtf.req.springBase+"Case/caseReport/contactCasePieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Contacts with Cases';
                x.id2=parentId+"contactwithcasebargraph";
                x.barDataUrl=Wtf.req.springBase+"Case/caseReport/contactCaseBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Contacts with Cases [No. of Cases by Contact name]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="ContactWithCase";
               // x.searchbyemptytext="Search by Case Subject";
                break;
       case 36:x.title=WtfGlobal.getLocaleText("crm.reportlink.convertleadstoaccount");//"Converted Leads to Accounts";
                x.id=parentId+"convertleadtoaccountID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=52;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.convertleadstoaccount.ttip");//'Get the list of Leads who are converted to Accounts.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:36, quickSearchFields:"Last Name/ Company Name"};
                x.url=Wtf.req.springBase+"Account/accountReport/convertedLeadsToAccountReport.do";
                x.exportUrl=Wtf.req.springBase+"Account/accountReport/convertedLeadsToAccountExport.do";
                x.chartid=parentId+"ConvertedLeadAccount";
                x.id1=parentId+"convertleadtoaccountgraph";
                x.id2=parentId+"convertleadtoaccountbargraph";
                x.barDataUrl=Wtf.req.springBase+"Lead/leadReport/convertedLeadToBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Converted Leads to Account ';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="ConvertedLeadAccount";
             //   x.searchbyemptytext="Search by Lead Name";
                break;
       case 37:x.title=WtfGlobal.getLocaleText("crm.reportlink.convertleadstoopportunity");//"Converted Leads to Opportunity";
                x.id=parentId+"convertleadtooppID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=53;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.convertleadstoopportunity.ttip");//'Get the list of Leads who are converted to Opportunity.';
                x.sourceflag=null;
                x.stageflag=null;
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/convertedLeadsToOpportunityReport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/convertedLeadsToOpportunityExport.do";
                x.baseParams={flag:37, quickSearchFields:"Last Name/ Company Name"};
                x.chartid=parentId+"ConvertedLeadOpp";
                x.id1=parentId+"convertleadtooppgraph";
                x.id2=parentId+"convertleadtooppbargraph";
                x.barDataUrl=Wtf.req.springBase+"Lead/leadReport/convertedLeadToBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Converted Leads to Opportunity ';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="ConvertedLeadOpp";
             //   x.searchbyemptytext="Search by Lead Name";
                break;
       case 38:x.title=WtfGlobal.getLocaleText("crm.reportlink.convertleadstocontacts");//"Converted Leads to Contacts";
                x.id=parentId+"convertleadtocontactID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=54;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.convertleadstocontacts.ttip");//'Get the list of Leads who are converted to Contacts.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:38, quickSearchFields:"Last Name/ Company Name"};
                x.url=Wtf.req.springBase+"Contact/contactReport/convertedLeadsToContactReport.do";
                x.exportUrl=Wtf.req.springBase+"Contact/contactReport/convertedLeadsToContactExport.do";
                x.chartid=parentId+"ConvertedLeadContact";
                x.id1=parentId+"convertleadtocontactgraph";
                x.id2=parentId+"convertleadtocontactbargraph";
                x.barDataUrl=Wtf.req.springBase+"Lead/leadReport/convertedLeadToBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Converted Leads to Contact ';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="ConvertedLeadContact";
               // x.searchbyemptytext="Search by Lead Name";
                break;
       case 39:x.title=WtfGlobal.getLocaleText("crm.reportlink.targetsbyowner");//"Targets by Owner";
                x.id=parentId+"targetownerID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=55;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.targetsbyowner.ttip");//'Get the list of Targets sorted in the order of their Creator.';
                x.url=Wtf.req.springBase+"Target/targetReport/targetsByOwnerReport.do";
                x.exportUrl=Wtf.req.springBase+"Target/targetReport/targetsByOwnerExport.do";
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:39};
                x.chartid=parentId+"TargetOwner";
                x.id1=parentId+"targetownergraph";
                x.pieDataUrl=Wtf.req.springBase+"Target/targetReport/targetsByOwnerPieChart.do";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Targets by Owner';
                x.id2=parentId+"targetownerbargraph";
                x.barDataUrl=Wtf.req.springBase+"Target/targetReport/targetsByOwnerBarChart.do";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Targets by Owner [No. of Targets by Owner Name]';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="TargetOwner";
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbytargetname");//"Search by Target Name";
                break;
       case 40:x.title=WtfGlobal.getLocaleText("crm.reportlink.opportunitypipelinechart");//"Opportunities Pipeline Chart";
                x.id=parentId+"openOppPipelineChartID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=56;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.opportunitypipelinereport.ttip");//'Get the list of Opportunities pipeline data.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:40, quickSearchFields:"Stage"};
                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/oppPipelineReport.do";
                x.Grid2Url=Wtf.req.springBase+"Opportunity/opportunityReport/allOppPipelineReport.do";
                x.Grid2ExportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/allOppPipelineExport.do";
                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppPipelineExport.do";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppPipelinePieChart.do?comboname=Opportunity Stage";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppPipelineBarChart.do?comboname=Opportunity Stage";
                x.chartid=parentId+"openOppPipelinePieChart";
                x.id1=parentId+"openOppPipelineChartgraph";
                x.dataflag=84;
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Opportunities Pipelined Chart';
                x.id2=parentId+"openOppPipelineBargraph";
                x.dataflag2=85;
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunities Pipelined Chart&currency=true&rotate=true&color=FF0000';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="openOppPipeline";
             //   x.searchbyemptytext="Search by Opportunity Stage";
                break;
        case 41:x.title=WtfGlobal.getLocaleText("crm.reportlink.leadspipelinechart");//"Leads Pipeline Chart";
                x.id=parentId+"leadPipelineChartID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=57;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.leadspipelinereport.ttip");//'Get the list of Leads pipeline data.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:41, quickSearchFields:"Lead Status"};
                x.url=Wtf.req.springBase+"Lead/leadReport/leadsPipelineReport.do";
                x.exportUrl=Wtf.req.springBase+"Lead/leadReport/leadsPipelineExport.do";
                x.pieDataUrl=Wtf.req.springBase+"Lead/leadReport/leadsPipelinePieChart.do?comboname=Lead Status";
                x.barDataUrl=Wtf.req.springBase+"Lead/leadReport/leadsPipelineBarChart.do?comboname=Lead Status";
                x.chartid=parentId+"leadPipelinePieChart";
                x.id1=parentId+"leadPipelineChartgraph";
                x.dataflag=86;
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Leads Pipelined Chart';
                x.id2=parentId+"leadPipelineBargraph";
                x.dataflag2=87;
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Leads Pipelined Chart&currency=true&rotate=true&color=FF0000';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="leadPipeline";
              //  x.searchbyemptytext="Search by Lead Status";
                break;
       case 42:x.title=WtfGlobal.getLocaleText("crm.reportlink.campaignsinfluence");//"Campaign Influence";
                x.id=parentId+"campaignInfluenceChartID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=59;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.campaignsinfluence.ttip");//'Get Campaign Influence from graph.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:43};
                x.chartid=parentId+"campaignInfluencePieChart";
                x.id1=parentId+"campaignInfluenceChartgraph";
                x.dataflag=88;
                x.xmlpath=Wtf.req.base+'charData.jsp?flag=83&title=Campaign Influence Chart';
                x.id2=parentId+"campaignInfluenceBargraph";
                x.dataflag2=89;
                x.xmlpath2=Wtf.req.base+'charData.jsp?flag=82&title=Campaign Influence Chart';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="campaignInfluence";
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbycampaignname");//"Search by Campaign Name";
                break;
       case 43:x.title=WtfGlobal.getLocaleText("crm.reportlink.completedgoalsbyusers");//"Completed Goals by Users";
                x.id=parentId+"completedgoalsChartID";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=60;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.completedgoalsbyusers.ttip");//'Get the list of completed goals.';
                x.sourceflag=null;
                x.stageflag=null;
                x.baseParams={flag:60};
                x.url=Wtf.req.springBase+"common/HRMSIntegration/completedGoalReport.do";
                x.exportUrl=Wtf.req.springBase+"common/HRMSIntegration/completedGoalReportExport.do";
                x.pieDataUrl=Wtf.req.springBase+"common/HRMSIntegration/completedGoalPieChart.do?comboname=user";
                x.barDataUrl=Wtf.req.springBase+"common/HRMSIntegration/completedGoalBarChart.do?comboname=user";
                x.chartid=parentId+"completedgoalsPieChart";
                x.id1=parentId+"completedgoalsChartgraph";
                x.dataflag=86;
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Completed Goals by Users';
                x.id2=parentId+"completedgoalsBargraph";
                x.dataflag2=87;
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Completed Goals by Users&currency=true&rotate=true&color=FF0000';
                x.mainid=x.InnerID+'MainTabPanel';
                x.name="completedgoals";
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbyassignee");//"Search by Assignee";
                break;
        case 46:x.title=WtfGlobal.getLocaleText("crm.reportlink.opportunitiessalesbyregion");//"Opportunities Sales by Region";
                x.id=parentId+"opportunitiessalesbyregion";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=74;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.opportunitiessalesbyregion.ttip");//'Monitor your opportunities sales grouped by region.';
                x.head=3;
                x.sourceflag=2;
                x.id2=parentId+"OppSalesbyRegionbargraph";
                x.name="OppSalesbyRegion";
                x.stageflag=1;
//                x.name="Opportunities sales by Region";
                x.baseParams={sf:x.sourceflag,flag:(x.stageflag==1?1:6),head:x.head};
                x.chartid=parentId+"Opportunity sales by Region";
                x.id2=parentId+"opportunitysalesbyregion";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppSalesamountDashboardBarChart.do?groupbyField=c.crmCombodataByRegionid&valueField=value&idField=ID";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunity by Region';
                x.id1=parentId+"OppSalesbyRegionbargraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppSalesamountDashboardPieChart.do?groupbyField=c.crmCombodataByRegionid&valueField=value&idField=ID";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Opportunity by Region';
                x.mainid=x.InnerID+'MainTabPanel';
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchbyregionowner");//"Search by Region, Owner.";
                break;
        case 47:x.title=WtfGlobal.getLocaleText("crm.reportlink.opportunitiessalesbystage");//"Opportunities Sales by Stage";
                x.id=parentId+"opportunitiessalesbystage";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=74;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.opportunitiessalesbystage.ttip");//'Monitor your opportunities sales grouped by stage.';
                x.head=3;
                x.sourceflag=2;
                x.id2=parentId+"OppSalesbyStagebargraph";
                x.name="OppSalesbyStage";
                x.stageflag=1;
//                x.name="Opportunities sales by Stage";
                x.baseParams={sf:x.sourceflag,flag:(x.stageflag==1?1:6),head:x.head};
                x.chartid=parentId+"Opportunity sales by Stage";
                x.id2=parentId+"opportunitysalesbystage";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppSalesamountDashboardBarChart.do?groupbyField=c.crmCombodataByOppstageid&valueField=value&idField=ID";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=';
                x.id1=parentId+"OppSalesbyStagebargraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppSalesamountDashboardPieChart.do?groupbyField=c.crmCombodataByOppstageid&valueField=value&idField=ID";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=';
                x.mainid=x.InnerID+'MainTabPanel';
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbystageowner");//"Search by Stage, Owner.";
                break;
        case 48:x.title=WtfGlobal.getLocaleText("crm.reportlink.opportunitybysalesperson");//"Opportunities Sales by Person";
                x.id=parentId+"opportunitiessalesbyperson";
                x.InnerID=parentId+"inner"+x.id;
                x.helpID=74;
                x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.opportunitybysalesperson.ttip");//'Monitor your opportunities sales grouped by person.';
                x.head=3;
                x.sourceflag=2;
//                x.url=Wtf.req.springBase+"Opportunity/opportunityReport/oppByRegionHReport.do";
//                x.exportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppByRegionHExport.do";
//                x.Grid2Url=Wtf.req.springBase+"Opportunity/opportunityReport/oppByRegionFunnelReport.do";
//                x.Grid2ExportUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppByRegionFunnelExport.do";
                x.id2=parentId+"OppSalesbyPersonbargraph";
                x.name="OppSalesbyPerson";
                x.stageflag=1;
//                x.name="Opportunities sales by Person";
                x.baseParams={sf:x.sourceflag,flag:(x.stageflag==1?1:6),head:x.head};
                x.chartid=parentId+"Opportunity sales by Person";
                x.id2=parentId+"opportunitysalesbyperson";
                x.barDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppSalesamountDashboardBarChart.do?groupbyField=oo.usersByUserid&valueField=firstName&idField=userID";
                x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=';
                x.id1=parentId+"OppSalesbyPersonbargraph";
                x.pieDataUrl=Wtf.req.springBase+"Opportunity/opportunityReport/oppSalesamountDashboardPieChart.do?groupbyField=oo.usersByUserid&valueField=firstName&idField=userID";
                x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=';
                x.mainid=x.InnerID+'MainTabPanel';
                x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbypersonowner");//"Search by Person, Owner.";
                break;
            }
            return x;
} 

function getAllActivityPaneldetails(no,parentId,relatedto,relatedid) {
    if(parentId == undefined){
        parentId="report";
    }
    var x=new Object;
    x.report="report";
    x.swf="../../scripts/graph/krwcolumn/krwpie/krwpie.swf";
    x.swf2="../../scripts/graph/krwcolumn/krwcolumn/krwcolumn.swf";
    x.title=WtfGlobal.getLocaleText("crm.reportlink.viewallactivities");//"View All Activities";
    x.id=parentId+"viewallactivityID";
    x.InnerID=parentId+"inner"+x.id;
    x.helpID=61;
    x.tooltip=WtfGlobal.getLocaleText("crm.reportlink.viewallactivities.ttip");//'Get the list of all Activities.';
    x.sourceflag=null;
    x.stageflag=null;
    x.relatedto=relatedto,// related module name for activity
    x.baseParams=relatedto!=undefined?{flag:15,filterCombo:relatedto,relatedid:relatedid}:{flag:15};
    x.url=Wtf.req.springBase+"Activity/activityReport/getAllActivitiesReport.do";
    x.exportUrl=Wtf.req.springBase+"Activity/activityReport/getAllActivitiesExport.do";
    x.chartid=parentId+"High Priority Activities";
    x.id1=parentId+"HighPriorityActivitygraph";
    x.pieDataUrl=Wtf.req.springBase+"Activity/activityReport/highPriorityActivityPieChart.do?comboname=Related To";
    x.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=High Priority Activities';
    x.id2=parentId+"HighPriorityActivitybargraph";
    x.barDataUrl=Wtf.req.springBase+"Activity/activityReport/highPriorityActivityBarChart.do?comboname=Related To";
    x.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=High Priority Activities';
    x.mainid=x.InnerID+'MainTabPanel';
    x.name="ActivitiesByPriority";
    x.searchbyemptytext=WtfGlobal.getLocaleText("crm.reportlink.searchtext.searchbyownername");//"Search by Owner name";
    return x;
}

function addTargetModuleTab(a){
    var panel=Wtf.getCmp('TargetModuleHomePanel');
    if(panel==null)
    {
        panel=new Wtf.targetModuleEditor({
            border:false,
            title:"<div wtf:qtip='Enhance the effectiveness of your campaign configurations by building lists of prospective customers whom you want to focus. You can easily add or import targets in convenient file formats.'>Targets</div>",
            layout:'fit',
            closable:true,
            id:'TargetModuleHomePanel',
            modName : "Target",
            customParentModName : "Target",
            iconCls:getTabIconCls(Wtf.etype.lead),
            newFlag:2,
            urlFlag:300,
            highLightId:a,
            clearFlag:false
        });
        mainPanel.add(panel);
    } else {
       mainPanel.setActiveTab(panel);
       Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"targetModuleid");
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function addTargetListTab(a){
   
    var panel=Wtf.getCmp('campaigntargetdetail');
    if(panel==null)
    {
        panel=new Wtf.targetListDetails({
            title:'Target Lists',
            id:'campaigntargetdetail',
            mainTab:this.mainTab
        })
        mainPanel.add(panel);
    } else {
       mainPanel.setActiveTab(panel);
       Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"targetModuleid");
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function addEmailTemplate(a){

    var panel=Wtf.getCmp('emailTemplatedashboard');
    if(panel==null)
    {
        panel=new Wtf.emailTemplate({
            mainTab:this.mainTab,
            id:"emailTemplatedashboard"
        })
        mainPanel.add(panel);
    }else {
       mainPanel.setActiveTab(panel);
       Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"targetModuleid");
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function addNewCampaignTab(a){

   Wtf.addNewCampaign();
}

function crmEmailTypes(){

  var panel=Wtf.getCmp('emailTypedashboard');
    if(panel==null)
    {
        panel=new Wtf.emailType({
            title:"<div id='emailtypetab' wtf:qtip='Edit and Customize the System Generated E-mail formats and personalize them as per your organizational requirements.'>Email Types</div>",
            mainTab:this.mainTab,
            id:"emailTypedashboard"
        })
        mainPanel.add(panel);
    }else {
       mainPanel.setActiveTab(panel);
       
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function addNewTargetListTab(store,targetDropDownStore, closePanel){
    var newtargetform = null;
    var newtargetWin = new Wtf.Window({
        width:400,
        height:350,
        resizable:false,
        layout:'border',
        iconCls: 'pwnd favwinIcon',
        title: WtfGlobal.getLocaleText("crm.targetlist.createtargetlistwin.title"),//'Create Target List',
        modal:true,
        id:'newTargetWin',
        items:[{
            region : 'north',
            height : 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(WtfGlobal.getLocaleText("crm.targetlist.createtargetlistwin.title"), WtfGlobal.getLocaleText("crm.targetlists.createnewlist"),"../../images/target-list-wind-icon.jpg")
        },{
            region : 'center',
            border : false,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
            layout : 'fit',
            items:[newtargetform = new Wtf.form.FormPanel({
                buttonAlign:'right',
                border:false,
                items:[this.tname = new Wtf.ux.TextField({
                    fieldLabel: WtfGlobal.getLocaleText("crm.mydocuments.header.name")+'*',//Name* ',
                    allowBlank : false,
                    maxLength:255,
                    name:'name',
                    width:230,
                    xtype:'striptextfield'
                }),{
                    xtype:'striptextarea',
                    fieldLabel:WtfGlobal.getLocaleText("crm.targetlists.header.targetsource"),//'Target Source',
                    name:'targetsource',
                    maxLength:255,
                    width:230
                },{
                    xtype:'striptextarea',
                    fieldLabel:WtfGlobal.getLocaleText("crm.contact.defaultheader.desc"),//'Description',
                    name:'desc',
                    maxLength:1024,
                    width:230
                },{
                    xtype:'hidden',
                    name:'listid'
                }],
                buttons:[{
                    text:WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
                    scope : this,
                    title:WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
                    handler:function(e){
                        if(this.tname.getValue().trim()==""){
                            ResponseAlert(63);
                            return ;
                        }
                        
                        if(newtargetform.form.isValid()==false){
                        	WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.lead.webtoleadform.entervalinputmsg")]);
                        	return;
                        }
                        e.disable();
                        var tempObj = newtargetform.form.getValues();
                        tempObj.listid=null,
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.springBase+'emailMarketing/action/saveTargetListTargets.do',
                            params:tempObj
                        },this,
                        function(res) {
                            if(res.success == true){
                            	e.enable();
                                Wtf.getCmp("newTargetWin").close();
                                var panel=Wtf.getCmp('campaigntargetdetail');
                                if(panel==null)
                                {
                                    panel=new Wtf.targetListDetails({
                                        title:WtfGlobal.getLocaleText("crm.targetlists.title.plural"),//'Target Lists',
                                        id:'campaigntargetdetail',
                                        mainTab:this.mainTab
                                    })
                                    mainPanel.add(panel);
                                    panel.on('close',function(){
                                        if(store)
                                            store.reload();
                                        if(targetDropDownStore)
                                            targetDropDownStore.reload();
                                    },this);

                                } else {
                                    mainPanel.setActiveTab(panel);
//                                    Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"targetModuleid");
                                }
                                mainPanel.setActiveTab(panel);
                                mainPanel.doLayout();
                                panel.EditorStore.reload();
                                var mode=1;//for Edit

                                var listID = res.listid;
                                var tlId = 'targetListTabnewedit_dash'+mode+listID;

                                var targetListTab = Wtf.getCmp(tlId);

                                if(targetListTab == null) {
                                    targetListTab = new Wtf.targetListWin({
                                        mode : mode,
                                        id : tlId,
                                        listID : listID,
                                        TLID : listID,
                                        store:panel.EditorStore,
                                        listname : Encoder.htmlDecode(tempObj.name),
                                        description : Encoder.htmlDecode(tempObj.desc),
                                        targetsource :Encoder.htmlDecode(tempObj.targetsource),
                                        iconCls: "pwnd editTargetListEmailMarketingWin",
                                        mainTab:panel.mainTab
                                    })
                                    panel.mainTab.add(targetListTab);
                                    targetListTab.on('close',function(){
                                        if(store){
                                            this.fireEvent('close',this);
                                            if(closePanel===true)
                                            this.ownerCt.remove(this);
                                        }
                                        	
                                    },panel);
                                }
                                panel.mainTab.setActiveTab(targetListTab);
                                panel.mainTab.doLayout();
                            }
                        });

                    }
                },{
                    text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                    handler:function(){
                        Wtf.getCmp("newTargetWin").close();
                    }
                }]
            })]
        }]
}).show();

 /*   var panel=Wtf.getCmp('targetListTabnewedit_dash_addnew');
    if(panel==null)
    {
        var record;
        var listID = '0';
        var listName ="New ";
        var tlId = 'targetListTabnewedit_dash_addnew';
        panel=new Wtf.targetListWin({
            mode : 0,
            record : record,
            id : tlId,
            listID : listID,
            listname : listName,
            iconCls: "pwnd newTargetListEmailMarketingWin",
            addNewDashboardCall:true

        })
        mainPanel.add(panel);
    } else {
        mainPanel.setActiveTab(panel);
        Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"targetModuleid");
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();*/
}

function addNewEmailTemplate(a){


    var panel=Wtf.getCmp('template_wiz_win_addnew_dash');
    if(panel==null)
    {
        var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate");//"New Template";
        var title = Wtf.util.Format.ellipsis(tipTitle,18);
        panel=new Wtf.newEmailTemplate({
            title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle="+WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate.ttiptitle")+">"+title+"</div>",
            tipTitle:tipTitle,
            id:'template_wiz_win_addnew_dash',
            addNewDashboardCall:true

        });
        mainPanel.add(panel);
    }else {
        mainPanel.setActiveTab(panel);
        Wtf.highLightGlobal(a,panel.EditorGrid,panel.EditorStore,"targetModuleid");
    }
    mainPanel.setActiveTab(panel);
    mainPanel.doLayout();
}

function EmailCampaignTabPanel()
{
    var main=Wtf.getCmp("as");
    var demoTab=Wtf.getCmp("crmemailcampaigntabpanel");
    if(demoTab==null)
    {
        demoTab=new Wtf.TabPanel({
            title:"<div wtf:qtip='Campaign Configuration'>"+Wtf.util.Format.ellipsis("Campaign Configuration",18)+"</div>",
            id:'crmemailcampaigntabpanel',
            activeTab:0,
            border:false,
            closable:true,
            enableTabScroll:true,
            iconCls:"pwnd EmailMarketingTabIcon"
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();

}

function showEmailCampaigns(a){
    EmailCampaignTabPanel();
    var main=Wtf.getCmp("crmemailcampaigntabpanel");
    var demoTab=Wtf.getCmp("emailcampaignlist");
    if(demoTab==null)
    {
        demoTab=new Wtf.emailCampaignList({
            id:"emailcampaignlist",
            title:WtfGlobal.getLocaleText("crm.campaign.toptoolbar.campconfbtn"),//"Campaign Configurations",
            iconCls:"pwnd EmailMarketingTabIcon",
            layout:'fit',
            border:false,
            assign:true
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();

}
function goalSettings(a){

    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var demoTab=Wtf.getCmp("perticularemployeesforgoal");
    if(demoTab==null)
    {
        demoTab=new Wtf.perticularemployeegoals({
            id:"perticularemployeesforgoal",
            title:WtfGlobal.getLocaleText("crm.mygoals.goals"),//"Goals",
            iconCls:"pwndCRM assignGoalIcon",
            layout:'fit',
            border:false,
            assign:true
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();

}

function GoalManagementTabPanel()
{
    var main=Wtf.getCmp("as");
    var demoTab=Wtf.getCmp("goalmanagementtabpanel");
    if(demoTab==null)
    {
        demoTab=new Wtf.TabPanel({
            title:'<div wtf:qtip="Assessing an employee\'s performance can be done in an easier way.List down and manage the competencies, assign goals or set realistic goals by yourself.">Goals</div>',
            id:'goalmanagementtabpanel',
            activeTab:0,
            border:false,
            closable:true,
            enableTabScroll:true,
            iconCls:"barchartIcon"
        });
        main.add(demoTab);
    }
    main.setActiveTab(demoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();

}

function myGoals()
{
    GoalManagementTabPanel();
    var main=Wtf.getCmp("goalmanagementtabpanel");
    var mgdemoTab=Wtf.getCmp("mygoals");
    if(mgdemoTab==null)
    {
        mgdemoTab=new Wtf.myGoals({
            id:"mygoals",
            title:"<div wtf:qtip="+WtfGlobal.getLocaleText("crm.mygoalstab.title.ttip")+">"+WtfGlobal.getLocaleText("crm.dashboard.mygoals")+"</div>",
            layout:'fit',
            border:false,
            iconCls:"pwndCRM assignGoalIcon"
        });
        main.add(mgdemoTab);
    }
    main.setActiveTab(mgdemoTab);
    main.doLayout();
    Wtf.getCmp("as").doLayout();
}


Wtf.ux.collapsedPanelTitlePlugin = function(){
    this.init = function(p) {
        if (p.collapsible){
            var r = p.region;
            if ((r == 'north') || (r == 'south')){
                p.on ('render', function(){
                    var ct = p.ownerCt;
                    ct.on ('afterlayout', function(){
                        if (ct.layout[r].collapsedEl){
                            p.collapsedTitleEl = ct.layout[r].collapsedEl.createChild ({
                                tag: 'div',
                                cls: 'x-panel-header x-unselectable ',
                                style: 'padding:0px 3px 4px 5px;',
                                html:"<div class='x-panel-header-text' wtf:qtip='Click to have access to details of the selected record'>"+ p.title+"</div>"
                            });
                            p.setTitle = Wtf.Panel.prototype.setTitle.createSequence (function(t){
                                p.collapsedTitleEl.dom.innerHTML = t;
                            });
                        }
                    }, false, {
                        single:true
                    });
                    p.on ('collapse', function(){
                        if (ct.layout[r].collapsedEl && !p.collapsedTitleEl){
                            p.collapsedTitleEl = ct.layout[r].collapsedEl.createChild ({
                                tag: 'div',
                                cls: 'x-panel-header x-unselectable x-panel-header-text',
                                style: 'padding:0px 3px 4px 5px;',
                                html: "<span>"+p.title+"</span>"
                            });
                            p.setTitle = Wtf.Panel.prototype.setTitle.createSequence (function(t){
                                p.collapsedTitleEl.dom.innerHTML = t;
                            });
                        }
                    }, false, {
                        single:true
                    });
                });
            }
            else if ((r == 'east') || (r == 'west')){
                var html = "";
                if(p.id == 'navigationpanel'){
                    html = "<img id='quickview' src='../../images/quick-view.gif' wtf:qtip='"+WtfGlobal.getLocaleText("crm.dashboard.westpanel.quickview.ttip")+"'/>";//Click to have a quick access to your accounts, products, campaigns, leads and documents'/>";
                }
                else if(p.id == 'qickviewimage'){
                    html = "<img id='quickview' src='../../images/view-image-link.gif' wtf:qtip='Click here to take Preview of the selected image'/>";
                }
                p.on ('render', function(){
                    var ct = p.ownerCt;
                    ct.on ('afterlayout', function(){
                        if (ct.layout[r].collapsedEl){
                            p.collapsedTitleEl = ct.layout[r].collapsedEl.createChild ({
                                tag: 'div',
                                style: 'padding:15px 3px 4px 0px;border:0px;',
                                html:html
                            });
                            p.setTitle = Wtf.Panel.prototype.setTitle.createSequence (function(t){
                                p.collapsedTitleEl.dom.innerHTML = t;
                            });
                        }
                        Wtf.QuickTips.register({
                            target:  Wtf.get('wtf-gen60'),
                            trackMouse: true,
                            text: 'Click to expand Quick View'
                        });
                        Wtf.QuickTips.enable();
                    }, false, {
                        single:true
                    });
                    p.on ('collapse', function(){
                        if (ct.layout[r].collapsedEl && !p.collapsedTitleEl){
                            p.collapsedTitleEl = ct.layout[r].collapsedEl.createChild ({
                                tag: 'div',
                                style: 'padding:15px 3px 4px 0px;border:0px;',
                                html:html
                            });
                            p.setTitle = Wtf.Panel.prototype.setTitle.createSequence (function(t){
                                p.collapsedTitleEl.dom.innerHTML = t;
                            });
                        }
                    }, false, {
                        single:true
                    });
                });
            }
        }
    };
}
function openCampaign(){
  if(!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.view)) {
        var panel=Wtf.getCmp('CampaignHomePanel');
        if(panel==null)
        {
            panel = new Wtf.campaignEditor({
                border:false,
                title:"<div id='campaigntab' wtf:qtip='Maintain comprehensive details of marketing initiatives such as an advertisement, direct mail, or conference that you conduct in order to generate prospects and build brand awareness.'>Campaigns</div>",
                layout:'fit',
                closable:true,
                iconCls:getTabIconCls(Wtf.etype.campaign),
                id:'CampaignHomePanel',
                modName : "Campaign",
                customParentModName : "Campaign",
                newFlag:2,
                clearFlag:false
            });

            mainPanel.add(panel);
        }
        mainPanel.setActiveTab(panel);
        mainPanel.doLayout();
  } else {
      ResponseAlert(554);
  }
}

// Call from Dashboard - Help Window(Take a quick tour)
function importContact(){
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.view)) {
        var obj = addContactTab();
        var extraConfig={
            mapid : obj.mapid,
            relatedName : obj.relatedName
        };
        var extraParams = "{\"Deleteflag\":0, \"Validflag\":1, \"UsersByCreatedbyid\":\""+loginid+"\", \"UsersByUpdatedbyid\":\""+loginid+"\", \"Company\":\""+companyid+"\"}";
        var impWin1 = Wtf.commonFileImportWindow(obj,"Contact",obj.EditorStore,extraParams,extraConfig);
        impWin1.show();
    }  else {
        ResponseAlert(555);
    }
}
function importLead() {
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.view)) {
        var obj = addLeadTab();
        var extraConfig = {};
        var extraParams = "{\"Type\":\""+Wtf.leadtyypedefault+"\", \"Deleteflag\":0, \"Istransfered\":\"0\", \"Isconverted\":\"0\", \"UsersByCreatedbyid\":\""+loginid+"\", \"UsersByUpdatedbyid\":\""+loginid+"\", \"Company\":\""+companyid+"\"}";
        var impWin1 = Wtf.commonFileImportWindow(obj,"Lead",obj.EditorStore,extraParams, extraConfig);
        impWin1.show();
    } else {
        ResponseAlert(556);
    }
}

function importAccount() {
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.view)) {
        var obj = addAccountTab();
        var extraConfig = {};
        var extraParams = "{\"Deleteflag\":0, \"Validflag\":1, \"UsersByCreatedbyid\":\""+loginid+"\", \"UsersByUpdatedbyid\":\""+loginid+"\", \"Company\":\""+companyid+"\"}";
        var impWin1 = Wtf.commonFileImportWindow(obj,"Account",obj.EditorStore,extraParams, extraConfig);
        impWin1.show();
    } else {
        ResponseAlert(557);
    }
}

function getHTMLComment(comment,modName) {
    
    var CommentWin = new Wtf.Window({
                resizable: false,
                scope: this,
                modal:true,
                width: 400,
                height:320,
                layout: 'border',
                iconCls: 'pwnd favwinIcon',
                id: 'dashboard_updates_comment_window',
                title: 'Comment',
                items:[{
                    region : 'north',
                    height : 80,
                    border : false,
                    id:'dashboard_updates_comment_window_north_panel',
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                    html : getTopHtml("Comment", "Comment for "+modName+"", "../../images/comment.gif")
                },{
                    region:'center',
                    border:false,
                    autoScroll:true,
                    bodyStyle : "background: #f1f1f1;padding-left: 35px;padding-top: 20px;padding-right: 30px;",
                    html:comment

                }]
            });
    CommentWin.show();
}
/*
*
* Sagar A - Not in used
*/
/*function UpdateCommision(winid){
    winid = (winid==null?"CommisionWin":winid);
    var p = Wtf.getCmp(winid);
    if(!p){
        new Wtf.CommisionEditor({ 
            title:Wtf.util.Format.ellipsis('Commission Plan',18),
            tabTip:'Commission Plan',
            //headerImage:"../../images/",
            id: winid,
            renderTo: document.body
        }).show();
    }
}
*/
function getChart() {
        var  parentId="report";
        var details=  new Object;
        details.report="report";
        details.swf="../../scripts/graph/krwcolumn/krwpie/krwpie.swf";
        details.swf2="../../scripts/graph/krwcolumn/krwcolumn/krwcolumn.swf";
        details.title=WtfGlobal.getLocaleText("crm.reportlink.leadsbyindustry");//"Leads by Industry";
        details.id=parentId+"ModulesTimeID";
        details.helpID=10;
        details.InnerID=parentId+"inner"+details.id;
        details.chartid=parentId+"ModulesTime";
        details.id1=parentId+"ModulesTimegraph";
        details.pieDataUrl="Common/CRMCommon/openModuleUsagePie.do?comboname=Lead Source";
        details.xmlpath='Common/ChartXmlSetting/getPieChartSetting.do?title=Modules Access Duration&group_percent=0&hide_balloon_value=true';
        details.id2=parentId+"Leadsbyindustrybargraph";
        details.barDataUrl="Common/CRMCommon/moduleBarChart.do?comboname=Industry";
        details.xmlpath2='Common/ChartXmlSetting/getBarChartSetting.do?title=Modules and Hits';
        details.mainid=details.InnerID+'MainTabPanel';
        globalModuleChart("modulewisechart",details.id1,details.swf,details.pieDataUrl,"as",details.xmlpath,details.id2,details.swf2,details.barDataUrl,details.xmlpath2,undefined,undefined)
}
