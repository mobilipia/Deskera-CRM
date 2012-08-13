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
var widgetcount = 0;

function pagingRedirect(panelid, pager, subPan, searchstr, panelcount){
    var myPanel = Wtf.getCmp(panelid);
    myPanel.doPaging(myPanel.config1[subPan].url, (panelcount * pager), searchstr, pager, subPan);
}

function pagingRedirect1(panelid, pager, subPan, searchstr, panelcount){
    var myPanel = Wtf.getCmp(panelid);
    myPanel.doPaging(myPanel.config0[subPan].url, (panelcount * pager), searchstr, pager, subPan);
}

function quoteReplace(psString){
    var lsRegExp = /'|%/g;
    return String(psString).replace(lsRegExp, "");
}

function btnpressed(panelid){
    var searchid = "search" + panelid;
    var searchstr = document.getElementById(searchid).value;
    searchstr = quoteReplace(searchstr);
    var myPanel = Wtf.getCmp(panelid);
    myPanel.doSearch(myPanel.url, searchstr);
}

function createtooltip1(target, tpl_tool_tip, autoHide, closable, height){
    usertooltip = tpl_tool_tip, new Wtf.ToolTip({
        id: "1KChampsToolTip" + target,
        autoHide: autoHide,
        closable: closable,
        html: usertooltip,
        height: height,
        target: target
    });
}

function getToolsArrayForModules(code,managePerm) {
    var ta = [];
    var tip=listViewTips(code);
    ta.push({
        id:'updatewizardlink',
        qtip : tip.update,
        handler: function(e, target, panel) {
            openUpdate(panel.id,panel.id+'_updatelink');
        }
    });
    if(managePerm && code!=8) {
        ta.push({
            id:'quickwizardlink',
            qtip : tip.addlink,
            handler: function(e, target, panel) {
                openQuickAdd(panel.id,panel.id+'_quickaddlink',code);
            }
        });
    }
    ta.push({
        id:'paichartwizard',
        qtip : tip.chart,
        handler: function(e, target, panel) {
            openGraph(panel.id,panel.id+'_graphlink');
        }
    });
    //TODO
    //    ta.push({
    //        id:'barchartwizard',
    //        qtip : tip.chart,
    //        handler: function(e, target, panel) {
    //            openBarGraph(panel.id,panel.id+'_graphlink');
    //        }
    //    });
    if(code != 6 && code != 8) {
        ta.push({
            id:'detailwizardlink',
            qtip : tip.detail,
            handler: function() {
                switch(code) {
                    case 0 : addCampaignTab();break;
                    case 1 : addLeadTab();break;
                    case 2 : addAccountTab();break;
                    case 3 : addContactTab();break;
                    case 4 : addOpportunityTab();break;
                    case 5 : addCaseTab();break;
                    case 6 : addActivityMasterTab();break;
                    case 7 : addProductMasterTab();break;
                }
            }
        });
    }
    ta.push({
        id: 'close',
        handler: function(e, target, panel){
            var tt = panel.title;
            panel.ownerCt.remove(panel, true);
            panel.destroy();
            removeWidget(tt);
        }
    });
    return ta;
}

function getToolsArrayForReport(code,managePerm) {
    var ta = [];
    var tip=listViewTips(code);
    ta.push({
        id:'paichartwizard',
        qtip : tip.chart,
        handler: function(e, target, panel) {
            openGraph(panel.id,panel.id+'_graphlink');
        }
    });
    return ta;
}

function getToolsArray(ru,onlyRss){
    var ta = [];
    /*if (ru) {
		ta.push({
			id: onlyRss?'rss-white':'rss',
			handler: function(e, target, panel){
				window.open(ru, '_blank');
			}
		});
	}*/
    if(!onlyRss){
        ta.push({
            id: 'close',
            handler: function(e, target, panel){
                var tt = panel.title;
                panel.ownerCt.remove(panel, true);
                panel.destroy();
                removeWidget(tt);
            }
        });
    }
    return ta;
}

function listViewTips(code) {
	 var moduleName = "";
     var plural = undefined;
     switch(code){
			      case 0: moduleName = WtfGlobal.getLocaleText("crm.CAMPAIGN"); break;
			      case 1: moduleName = WtfGlobal.getLocaleText("crm.LEAD"); break;
			      case 2: moduleName = WtfGlobal.getLocaleText("crm.ACCOUNT"); break;
			      case 3: moduleName = WtfGlobal.getLocaleText("crm.CONTACT"); break;
			      case 5: moduleName = WtfGlobal.getLocaleText("crm.CASE"); break;
			      case 7: moduleName = WtfGlobal.getLocaleText("crm.PRODUCT"); break;
			      case 4: moduleName = WtfGlobal.getLocaleText("crm.OPPORTUNITY");
			        	   plural    = WtfGlobal.getLocaleText("crm.OPPORTUNITY.plural"); break;
			      case 6: moduleName = WtfGlobal.getLocaleText("crm.ACTIVITY");
			               plural    = WtfGlobal.getLocaleText("crm.ACTIVITY.plural"); break;
   	}
    var toolTip=new Object();
    toolTip.detail=WtfGlobal.getLocaleText({key:"crm.dashboard.tipmaker.detail",params:[moduleName]});
    toolTip.chart=WtfGlobal.getLocaleText({key:"crm.dashboard.tipmaker.chart",params:[moduleName]});
    moduleName=plural || moduleName;
    toolTip.update=WtfGlobal.getLocaleText({key:"crm.dashboard.tipmaker.update",params:[moduleName]});
    toolTip.addlink=WtfGlobal.getLocaleText({key:"crm.dashboard.tipmaker.addlink",params:[moduleName]});
    return toolTip;
}

function createNewPanel(setting,res,dataFlag){
    if(setting !== undefined) {
        if (setting.config1 != null) {
            return (new Wtf.WtfCustomPanel(setting,res,dataFlag));
        } else if (setting.config0 != null) {
            return (new Wtf.WtfCustomCrmPanel(setting,res,dataFlag));
        }
        else {
            if (setting.url != null) {
                return (new Wtf.WtfIframeWidgetComponent(setting));
            }
            else {
                return (new Wtf.WtfWidgetComponent(setting));
            }
        }
    }
}

function createWidget(ix){
    widgetcount--;
    titleSpan.innerHTML = '<span><span style="float:left;">'+WtfGlobal.getLocaleText("crm.dashboard.southregion.title")+'</span><span>'+widgetcount +" "+(widgetcount>1 ? WtfGlobal.getLocaleText("crm.widget.plural"): WtfGlobal.getLocaleText("crm.widget"))+"</span></span>";
    var count = 0
    var lowCountCol = 1;
    var lowCount = 1;
    var _ID = "portal_container_box";
    var box3Comp = Wtf.getCmp(_ID +"3");
    var box2Comp = Wtf.getCmp(_ID +"2");
    var box1Comp = Wtf.getCmp(_ID +"1");
    if (box3Comp.items != null) {
        lowCount =box3Comp.items.length;
    } else if (box2Comp.items != null) {
        lowCount = box2Comp.items.length;
    } else if (box1Comp.items != null) {
        lowCount = box1Comp.items.length;
    }
    

    for (var i = 3; i > 0; i--) {
        count=0;
        var comp = Wtf.getCmp(_ID+i);
        if (comp.items != null){
            count = comp.items.length;
        }
        if (count <= lowCount) {
            lowCount = count;
            lowCountCol = i;
        }
    }

    var pl = Wtf.getCmp(_ID + lowCountCol);
    if (pl != null) {
        var pn = createNewPanel(panelArr[ix],"");
        pl.add(pn);
        pl.doLayout();
        var t = Wtf.get("lix_" + ix);
        t.remove();
    }
    if(widgetIdArray[ix] != "campaign_reports_drag")
        insertIntoWidgetState(lowCountCol,widgetIdArray[ix]);
}
function insertIntoWidgetState(colno,wid){
    Wtf.Ajax.requestEx({
        url:Wtf.req.widget + 'insertWidgetIntoState.do',
        params:{
            flag:3,
            wid:wid,
            colno:colno
        }
    }, this, function(){
        }, function(){})

}
function removeWidget(tt){
    var ix = widgetArr.indexOf(WtfGlobal.HTMLStripper(tt));
    requestForWidgetRemove(widgetIdArray[ix]);
    appendWidget(ix);
}
function widgetTooltip(name){
    var tip="";
    switch(name){
        case 'Campaigns': tip=WtfGlobal.getLocaleText("crm.dashboard.campaigns.ttip");//"Maintain comprehensive details of marketing initiatives such as an advertisement, direct mail, or conference that you conduct in order to generate prospects and build brand awareness.";
            break;
        case 'Accounts': tip=WtfGlobal.getLocaleText("crm.dashboard.accounts.ttip");//"Maintain comprehensive details of the organization or company you want to track such as customers, partners, or competitors. Easily track your existing customers as well as prospective clients.";
            break;
        case 'Opportunities': tip=WtfGlobal.getLocaleText("crm.dashboard.opportunities.ttip");//"Maintain complete information related to specific sales and pending deals that needs to be cracked. Add to that, you can record, all the related contacts and activities information for each opportunity.";
            break; 
        case 'Cases': tip=WtfGlobal.getLocaleText("crm.dashboard.cases.ttip");//"Capture detailed description of a customer\'s feedback, problem or questions. Effectively manage cases through regular tracking of customer queries.";
            break;
        case 'Activities': tip=WtfGlobal.getLocaleText("crm.dashboard.activities.ttip");//"Maintain complete details of all activities including tasks and events associated with existing and prospective customers.";
            break;
        case 'Products': tip=WtfGlobal.getLocaleText("crm.dashboard.products.ttip");//"Maintain comprehensive details of items or services that you sell to your customers. You can also record associated vendor details here.";
            break;
        case 'Leads': tip=WtfGlobal.getLocaleText("crm.dashboard.leads.ttip");//"Capture all relevant information on potential sales opportunities or prospects i.e. individuals who have expressed some interest in your product or company.";
            break;
        case 'Contacts': tip=WtfGlobal.getLocaleText("crm.dashboard.contacts.ttip");//"Maintain complete information about the individuals you know in an account and interact with.";
            break;
    }
    return tip;
}

function appendWidget(ix){
    widgetcount++;
    titleSpan.innerHTML = '<span><span style="float:left;">'+WtfGlobal.getLocaleText("crm.dashboard.southregion.title")+'</span><span>'+widgetcount +" "+(widgetcount>1 ? WtfGlobal.getLocaleText("crm.widget.plural"): WtfGlobal.getLocaleText("crm.widget"))+"</span></span>";
    var _widgetname=widgetArr[ix].replace(" ","<br/>");
    var tip=widgetTooltip(_widgetname);
    var name_markup="<div class='widget_name'>"+_widgetname+"</div>";
    Wtf.DomHelper.append("widgetUl", "<li id='lix_" + ix +"' style='padding-left:10px !important;'><div wtf:qtip=\""+tip+"\" onclick='javascript:createWidget(" + ix +")' class='dashpwnd "+widgetIdArray[ix]+"'  ></div>"+name_markup+"</li>");
}

var widgetArr = ["Updates","Saved Searches"];
var panelArr = [];
var widgetIdArray=[];

while(dashboardloadflag>1){
    if(dashboardloadflag==2){
        dashboardPortletArray();
        break;
    } else if(dashboardloadflag==3){
        break;
    }
}

function dashboardPortletArray(){
    if(Wtf.Perm.Campaign && !WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.view))
        widgetArr.push(WtfGlobal.getLocaleText("crm.CAMPAIGN.plural"));//"Campaigns);"
    if(Wtf.Perm.Lead && !WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.view))
        widgetArr.push(WtfGlobal.getLocaleText("crm.LEAD.plural"));//"Leads);"
    if(Wtf.Perm.Account && !WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.view))
        widgetArr.push(WtfGlobal.getLocaleText("crm.ACCOUNT.plural"));//"Accounts");
    if(Wtf.Perm.Contact && !WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.view))
        widgetArr.push(WtfGlobal.getLocaleText("crm.CONTACT.plural"));//"Contacts");
    if( Wtf.Perm.Opportunity && !WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.view))
        widgetArr.push(WtfGlobal.getLocaleText("crm.OPPORTUNITY.plural"));//"Opportunities");
    if(Wtf.Perm.Case && !WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.view))
        widgetArr.push(WtfGlobal.getLocaleText("crm.CASE.plural"));//"Cases");
    if(Wtf.Perm.Activity && !WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)){
        widgetArr.push(WtfGlobal.getLocaleText("crm.ACTIVITY.plural"));//"Activities");
        widgetArr.push(WtfGlobal.getLocaleText("crm.dashboard.upcomingtaskwidget.title"));//"My Upcoming Tasks");
    }
    if(Wtf.Perm.Product && !WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.view))
        widgetArr.push(WtfGlobal.getLocaleText("crm.PRODUCT.plural"));//"Products");

    widgetArr.push(WtfGlobal.getLocaleText("crm.dashboard.mylinks.title"));//"My Links");
    widgetArr.push(WtfGlobal.getLocaleText("crm.dashboard.reports.title"));//"Reports");
// TODO ( Kuldeep Singh ) : Un-comment below code to view Custom Report Portlet
    widgetArr.push(WtfGlobal.getLocaleText("crm.dashboard.customreports.title"));//"Custom Reports");
    if(Wtf.Perm.Campaign && !WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.view)){
        widgetArr.push(WtfGlobal.getLocaleText("crm.dashboard.campaignlinks.title"));//"Campaign Links");
        widgetArr.push(WtfGlobal.getLocaleText("crm.dashboard.campaignreports.title"));//"Campaign Report");
    }
    if(Wtf.URole.roleid == Wtf.AdminId){ // admin check with admin id
        widgetArr.push(WtfGlobal.getLocaleText("crm.dashboard.administration.title"));//"Administration");
    }

    var _workspacelinks="<div id ='workspaceButtons' style='float:right;padding-top:3px;padding-right:5px;color:#AAA;'>";
    _workspacelinks+="<a href='javascript:myworkspace();'>Details</a>";
    //if(EnableDisable(Wtf.UPerm.Workspace, Wtf.Perm.Workspace.WorkspaceRequests)){
    _workspacelinks+="<a href='javascript:createworkspace(1);'>| Create New</a>";
    //}
    _workspacelinks+="</div>";

    //if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.myworkspaces_widget)){
    panelArr.push({
        config1:[{
            //                url:'jspfiles/knowledgeUni/workspace.jsp',
            url : Wtf.req.springBase + Wtf.req.widget + 'getAllUpdatesForWidget.do',
            numRecs:5,
            template:new Wtf.XTemplate(
                "<tpl><div class='workspace'>"+
                "<div>" +
                "<div style='padding-left:15px; background:transparent no-repeat scroll 0 0;'>{desc}</div>" +
                "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
                "</div>" +
                "</div></tpl>"
                ),
            isPaging: true,
            emptyText:WtfGlobal.getLocaleText("crm.dashboard.widget.mtytext"),//'No Updates',
            isSearch: false,
            headerHtml : '',
            //                WorkspaceLinks :_workspacelinks,
            paramsObj: {
                flag:5,
                searchField:'name'
            }

        }],
        id : "DSBMyWorkspaces",
        draggable:true,
        border:true,
        title: getWidgetTitle(WtfGlobal.getLocaleText("crm.dashboard.updates.title")),//'Updates'),
        tools: getToolsArray('jspfiles/rss.jsp?i=my_workspaces&id=' + loginid)
    });
    panelArr.push({
        config1:[{
            //                url:'jspfiles/knowledgeUni/workspace.jsp',
            url :'Common/AdvanceSearch/getSavedSearchQueries.do',
            numRecs:5,
            template:new Wtf.XTemplate(
                "<tpl><div class='workspace'>"+
                "<div>" +
                "<div onmouseover='javascript:showIcon(\"{searchid}\")' onmouseout='javascript:hideIcon(\"{searchid}\")' style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'><a class='emailmark' onclick='openAdvanceSearchTab({module},\"{searchid}\")' href=#>{searchname}</a>" +
                "<img class='stop' onclick='javascript:deleteAdvanceSearch(\"{searchid}\");'  alt='Delete' src='../../images/deleteLink.gif' style='' id=\"{searchid}\" />"+
                "</div></div>" +
                "</div></tpl>"
                ),
            isPaging: true,
            emptyText:WtfGlobal.getLocaleText("crm.dashboard.savedsearch.emptytxt"),//'No saved search',
            isSearch: false,
            headerHtml : '',
            paramsObj: {
                flag:12,
                searchField:'searchname'
            }

        }],
        id : "DSBAdvanceSearch",
        draggable:true,
        border:true,
        title: getWidgetTitle(WtfGlobal.getLocaleText("crm.dashboard.savedsearch.title")),//'Saved Searches'),
        tools: getToolsArray('jspfiles/rss.jsp?i=my_workspaces&id=' + loginid)
    });
    //}

    if(Wtf.Perm.Campaign && !WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.view)) {
        var campaignWidgetConfig = getConfig0Parameters(0,0,21,WtfGlobal.getLocaleText("crm.CAMPAIGN.plural"),Wtf.moduleWidget.campaign,getToolsArrayForModules(0,!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.manage)));
        panelArr.push(campaignWidgetConfig);
    }

    if(Wtf.Perm.Lead && !WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.view)) {
//        getConfig0Parameters(linkcode,type,chartdetailId,title,Id,tools)
        var leadWidgetConfig = getConfig0Parameters(1,1,41,WtfGlobal.getLocaleText("crm.LEAD.plural"),Wtf.moduleWidget.lead,getToolsArrayForModules(1,!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage)));
        panelArr.push(leadWidgetConfig);
    }

    if(Wtf.Perm.Account && !WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.view)) {
//        getConfig0Parameters(linkcode,type,chartdetailId,title,Id,tools)
        var accountWidgetConfig = getConfig0Parameters(2,2,18,WtfGlobal.getLocaleText("crm.ACCOUNT.plural"), Wtf.moduleWidget.account,getToolsArrayForModules(2,!WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.manage)));
        panelArr.push(accountWidgetConfig);
    }

    if(Wtf.Perm.Contact && !WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.view)) {
//        getConfig0Parameters(linkcode,type,chartdetailId,title,Id,tools)
        var contactWidgetConfig = getConfig0Parameters(3,3,27,WtfGlobal.getLocaleText("crm.CONTACT.plural"), Wtf.moduleWidget.contact,getToolsArrayForModules(3,!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.manage)));
        panelArr.push(contactWidgetConfig);
    }

    if(Wtf.Perm.Opportunity && !WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.view)) {
//        getConfig0Parameters(linkcode,type,chartdetailId,title,Id,tools)
        var oppWidgetConfig = getConfig0Parameters(4,4,40,WtfGlobal.getLocaleText("crm.OPPORTUNITY.plural"), Wtf.moduleWidget.opportunity,getToolsArrayForModules(4,!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage)));
        panelArr.push(oppWidgetConfig);
    }

    if(Wtf.Perm.Case && !WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.view)) {
//        getConfig0Parameters(linkcode,type,chartdetailId,title,Id,tools)
        var caseWidgetConfig = getConfig0Parameters(5,5,4,WtfGlobal.getLocaleText("crm.CASE.plural"), Wtf.moduleWidget.cases,getToolsArrayForModules(5,!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.manage)));
        panelArr.push(caseWidgetConfig);
    }

    if(Wtf.Perm.Activity && !WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)) {
        var activityWidgetConfig = getConfig0Parameters(6,6,15,WtfGlobal.getLocaleText("crm.ACTIVITY.plural"), Wtf.moduleWidget.activity,getToolsArrayForModules(6,!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.manage)));
        panelArr.push(activityWidgetConfig);

        var topActivityWidgetConfig = getConfig0Parameters(8,8,15,WtfGlobal.getLocaleText("crm.dashboard.upcomingtaskwidget.title"), Wtf.moduleWidget.topactivity,getToolsArray('jspfiles/rss.jsp?i=my_workspaces&id=' + loginid));
        topActivityWidgetConfig.config0[0].paramsObj.flag = 8;
        panelArr.push(topActivityWidgetConfig);
    }
    obj = Wtf.getCmp(Wtf.moduleWidget.activity);
    if(obj!=null) {
        obj.callRequest("","",0);
        Wtf.refreshUpdatesAll();
    }
    obj = Wtf.getCmp(Wtf.moduleWidget.topactivity);
    if(obj!=null) {
        obj.callRequest("","",0);
        Wtf.refreshUpdatesAll();
    }
    if(Wtf.Perm.Product && !WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.view)) {
        //        getConfig0Parameters(linkcode,type,chartdetailId,title,Id,tools)
        var productWidgetConfig = getConfig0Parameters(7,7,17,WtfGlobal.getLocaleText("crm.PRODUCT.plural"), Wtf.moduleWidget.product,getToolsArrayForModules(7,!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.manage)));
        panelArr.push(productWidgetConfig);
    }

    //if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.announcements_widget)){
    panelArr.push({
        config1:[{
            url:Wtf.req.springBase + Wtf.req.widget + 'getCrmModuleWidget.do',
            numRecs:10,
            isPaging: false,
            isSearch: false,
            template:new Wtf.XTemplate(
                '<tpl><div class="ctitle listpanelcontent" style="padding:0px !important;">',
                "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
                '</div></tpl>'
                ),
            emptyText:WtfGlobal.getLocaleText("crm.dashboard.mylinks.emptytxt"),//'No Modules',
            headerHtml:"<div colspan=\"3\" style='padding:5px 0 5px 0;border-bottom:1px solid #e4e4e4;font-size:13px;font-weight:bold;color:#10559a;' wtf:qtip='Access tools for effective management of marketing and sales efforts.'>CRM Workspaces</div>",
            paramsObj: {
                flag:6,
                searchField:'announceval'
            }
        }],
        draggable:true,
        border:true,
        title: getWidgetTitle(WtfGlobal.getLocaleText("crm.dashboard.mylinks.title")),//'My Links'),
        id : "crmmodule_drag",
        tools: getToolsArray('jspfiles/rss.jsp?i=announcements&u=' + loginid)
    });
    //}

    //if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.announcements_widget)){
    panelArr.push({
        config1:[{
            url:Wtf.req.springBase + Wtf.req.widget + 'getReportWidgets.do',
            numRecs:10,
            isPaging: true,
            isSearch: false,
            template:new Wtf.XTemplate(
                '<tpl><div class="workspace listpanelcontent">',
                "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
                '</div></tpl>'
                ),
            headerHtml:"",
            emptyText:WtfGlobal.getLocaleText("crm.dashboard.reports.emptytxt"),//'Assign Permissions to View Reports',
            paramsObj: {flag:8, searchField:'announceval'}
        }],
        draggable:true,
        border:true,
        title: getWidgetTitle(WtfGlobal.getLocaleText("crm.dashboard.reports.title")),//'Reports'),
        id : "reports_drag",
        tools: getToolsArray('jspfiles/rss.jsp?i=announcements&u=' + loginid)
    });
//// TODO ( Kuldeep Singh ) : Un-comment below code to view Custom Report Portlet
    panelArr.push({
        config1:[{
            url:Wtf.req.springBase + Wtf.req.widget + 'getCustomReportWidgets.do',
            numRecs:10,
            isPaging: true,
            isSearch: false,
            template:new Wtf.XTemplate(
                '<tpl><div class="workspace listpanelcontent">',
                "<div>" +
                "<div  onmouseover='javascript:showIcon(\"{reportno}\")' onmouseout='javascript:hideIcon(\"{reportno}\")' style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}" +
                "{delete}"+
//                "<img class='stop' onclick='javascript:deleteCustomReport(\"{reportno}\");'  alt='Delete' src='../../images/deleteLink.gif' style='' id=\"{reportno}\" />"+
                "</div></div>" +
                '</div></tpl>'
                ),
            headerHtml:"",
            emptyText:WtfGlobal.getLocaleText("crm.reportlink.customreport.mtytxt"),//'Assign Permissions to View Custom Reports',
            paramsObj: {flag:13, searchField:'announceval'}
        }],
        draggable:true,
        border:true,
        title: getWidgetTitle(WtfGlobal.getLocaleText("crm.dashboard.customreports.title")),//'Custom Reports'),
        id:"crm_custom_reports",
        tools: getToolsArray('jspfiles/rss.jsp?i=my_workspaces&id=' + loginid)
    });
    if(Wtf.Perm.Campaign && !WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.view)) {
        panelArr.push({
            config1:[{
                url:Wtf.req.springBase + Wtf.req.widget + 'getCampaignLinks.do',
                numRecs:10,
                isPaging: false,
                isSearch: false,
                template:new Wtf.XTemplate(
                    '<tpl><div class="ctitle listpanelcontent" style="padding:0px !important;">',
                    "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
                    '</div></tpl>'
                    ),
                emptyText:'No Modules',
                headerHtml:"<div colspan=\"3\" style='padding:5px 0 5px 0;border-bottom:1px solid #e4e4e4;font-size:13px;font-weight:bold;color:#10559a;' wtf:qtip='Access tools for effective management of marketing and sales efforts.'>Email Marketing</div>",
                paramsObj: {
                    flag:9,
                    searchField:'announceval'
                }
            }],
            draggable:true,
            border:true,
            title: getWidgetTitle(WtfGlobal.getLocaleText("crm.dashboard.campaignlinks.title")),//'Campaign Links'),
            id : "marketing_drag",
            tools: getToolsArray('jspfiles/rss.jsp?i=announcements&u=' + loginid)
        });
    
        panelArr.push({
            config1:[{
                url:Wtf.req.springBase + Wtf.req.widget + 'campEmailMarketingStatus.do',
                numRecs:10,
                isPaging: true,
                isSearch: false,
                template:new Wtf.XTemplate(
                    '<tpl><div class="workspace listpanelcontent">',
                    "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
                    '</div></tpl>'
                    ),
                headerHtml:"",
                emptyText:WtfGlobal.getLocaleText("crm.dashboard.reports.emptytxt"),//'Assign Permissions to View Reports',
                paramsObj: {
                    flag:10,
                    searchField:'announceval'
                }
            }],
            draggable:true,
            border:true,
            title: getWidgetTitle(WtfGlobal.getLocaleText("crm.dashboard.campaignreports.title")),//'Campaign Report'),
            id : Wtf.moduleWidget.campaignreport,
            tools: getToolsArray('jspfiles/rss.jsp?i=announcements&u=' + loginid)
        });
    }
    //if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.yahooweather_widget)){
    //panelArr.push({
    //	url: Wtf.req.widget + 'weather.jsp',
    ////url : 'jspfiles/widget.jsp',
    //	draggable: true,
    //	border: true,
    //	height: 300,
    //	useShim: true,
    //	title: 'Yahoo! Weather',
    //	id: "yahooweather_drag",
    //	tools: getToolsArray("http://weather.yahooapis.com/forecastrss?p=SNXX0006&u=c")
    //});
    if(Wtf.URole.roleid == Wtf.AdminId){ // admin check with admin id
        panelArr.push({
            config1:[{
                url:Wtf.req.springBase + Wtf.req.widget + 'getAdminWidget.do',
                numRecs:10,
                isPaging: false,
                isSearch: false,
                template:new Wtf.XTemplate(
                    '<tpl><div class="ctitle listpanelcontent" style="padding:0px !important;">',
                    "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
                    '</div></tpl>'
                    ),
                emptyText:WtfGlobal.getLocaleText("crm.dashboard.administration.emptytxt"),//'No Permissions to View ',
                headerHtml:"<div colspan=\"3\" style='padding:5px 0 5px 0;border-bottom:1px solid #e4e4e4;font-size:13px;font-weight:bold;color:#10559a;' wtf:qtip='Access tools for effective management of marketing and sales efforts.'>Administration Modules</div>",
                paramsObj: {
                    flag:11,
                    searchField:'announceval'
                }
            }],
            draggable:true,
            border:true,
            title: getWidgetTitle(WtfGlobal.getLocaleText("crm.dashboard.administration.title")),//'Administration'),
            id : "crm_admin_widget",
            tools: getToolsArray('jspfiles/rss.jsp?i=announcements&u=' + loginid)
        });
    }
    //if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.library_widget))
    widgetIdArray.push("DSBMyWorkspaces");
    widgetIdArray.push("DSBAdvanceSearch");
    if(Wtf.Perm.Campaign && !WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.view))
        widgetIdArray.push(Wtf.moduleWidget.campaign);
    if(Wtf.Perm.Lead && !WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.view))
        widgetIdArray.push(Wtf.moduleWidget.lead);
    if(Wtf.Perm.Account && !WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.view))
        widgetIdArray.push(Wtf.moduleWidget.account);
    if(Wtf.Perm.Contact && !WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.view))
        widgetIdArray.push(Wtf.moduleWidget.contact);
    if(Wtf.Perm.Opportunity && !WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.view))
        widgetIdArray.push(Wtf.moduleWidget.opportunity);
    if(Wtf.Perm.Case && !WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.view))
        widgetIdArray.push(Wtf.moduleWidget.cases);
    if(Wtf.Perm.Activity && !WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)){
        widgetIdArray.push(Wtf.moduleWidget.activity);
        widgetIdArray.push(Wtf.moduleWidget.topactivity);
    }
    if(Wtf.Perm.Product && !WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.view))
        widgetIdArray.push(Wtf.moduleWidget.product);

    widgetIdArray.push("crmmodule_drag");
    widgetIdArray.push("reports_drag");
// TODO ( Kuldeep Singh ) : Un-comment below code to view Custom Report Portlet
    widgetIdArray.push("crm_custom_reports");
    if(Wtf.Perm.Campaign && !WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.view)){
        widgetIdArray.push("marketing_drag");
        widgetIdArray.push(Wtf.moduleWidget.campaignreport);
    }
    if(Wtf.URole.roleid == Wtf.AdminId){ // admin check with admin id
        widgetIdArray.push("crm_admin_widget");
    }
//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.rssaggregator_widget))
//widgetIdArray.push("yahooweather_drag");
//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.gmail_widget))
//widgetIdArray.push("DSBGmail");
//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.chat_widget))
//widgetIdArray.push("DSBChat");

//}


}

function getWidgetTitle (title) {
    return "<div wtf:qtip="+WtfGlobal.getLocaleText("crm.dashboard.generaltitle.ttip")+">"+title+"</div>";//'Click to drag and place widget anywhere on the dashboard.'
}

function getConfig0Parameters(linkcode,type,chartdetailId,title,Id,tools) {
    return {
            config0:[{
                url : Wtf.req.springBase + Wtf.req.widget + 'getUpdatesForSingleWidgets.do',
                numRecs:5,
                template:new Wtf.XTemplate(
                    "<tpl><div class='workspace listpanelcontent'>"+
                    "<div>" +
                    "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
                    "</div>" +
                    "</div></tpl>"
                    ),
                isPaging: true,
                emptyText:WtfGlobal.getLocaleText("crm.dashboard.widget.mtytext"),//'No Updates',
                isSearch: false,
                headerHtml : '',
                linkcode : linkcode,
                paramsObj: {
                    flag:7,
                    type:type,
                    searchField:'name'
                }
            }],
            draggable:true,
            border:true,
            chartdetails:getPaneldetails(chartdetailId),
            title: getWidgetTitle(title),
            id : Id,
            tools: tools
    }
}


var categoryarray  = [],count=0;
function showcategory(val){
    if(Wtf.get(val).dom.style.display=='none'){
        Wtf.get(val).dom.style.display = 'block';
        Wtf.get(val).dom.style.paddingLeft = '15px';
        Wtf.get('x'+val).dom.className = 'x-tool mycategory-expand';
        var  flag=1;
        var i=0;
        for(i=0;i<count;i++){
            if(Wtf.get(categoryarray[i])!=null){
                if(categoryarray[i]==val){
                    flag=0;
                }else{
                    Wtf.get(categoryarray[i]).dom.style.display = 'none';
                    Wtf.get('x'+categoryarray[i]).dom.className = 'x-tool mycategory-collapse';
                }
            }
            if(flag==1){
                categoryarray[count] = val;
                count++;
            }
        }
    }
    else{
        Wtf.get(val).dom.style.display = 'none';
        Wtf.get('x'+val).dom.className = 'x-tool mycategory-collapse';
    }
}
function loadtab_main(id,book){
    id = '   '+id ;
    book.replace(/'/,'\'');

    mainPanel.loadTab('communityHome.html',id,book,'navareadashboard',1,true);
}


var manageLinks='<div id ="workspaceButtons" style="float:right;padding-top:3px;padding-right:5px;color:#AAA;">';
//if(EnableDisable(Wtf.UPerm.CenterManagement,Wtf.Perm.CenterManagement.centermanager)){
//manageLinks +='<a href="#" style="color:#083772" onclick="javascript:openManageWindow();">Create Report</a>';

//}
//if(EnableDisable(Wtf.UPerm.CenterManagement,Wtf.Perm.CenterManagement.centermanager)
//    || EnableDisable(Wtf.UPerm.CenterManagement,Wtf.Perm.CenterManagement.districtmanager)
//    || EnableDisable(Wtf.UPerm.CenterManagement,Wtf.Perm.CenterManagement.finance)
//    || EnableDisable(Wtf.UPerm.CenterManagement,Wtf.Perm.CenterManagement.management)){
manageLinks +='<a href="#" style="color:#083772" onclick="javascript:openReportGrid();">List Reports</a>|';
manageLinks +='<a href="#" style="color:#083772" onclick="javascript:uploadCMData(128);">Import Reports</a>|';
manageLinks +='<a href="#" style="color:#083772" onclick="javascript:openCreateCMRep();">Submit Report</a>';
//manageLinks +='<a href="#" style="color:#ct083772" onclick="javascript:showMainWindow();">Configure</a>';
//}

//if(EnableDisable(Wtf.UPerm.CenterManagement,Wtf.Perm.CenterManagement.districtmanager)
//                || EnableDisable(Wtf.UPerm.CenterManagement,Wtf.Perm.CenterManagement.finance)){
//manageLinks +='<a href="#" style="color:#083772" onclick="javascript:listReportGrid();">View Reports</a>';
//}
manageLinks +='</div>';


Wtf.Panel.prototype.afterRender = Wtf.Panel.prototype.afterRender.createInterceptor(function() {// Fix For IE  Scrollable Player Bug Fix
    if(this.autoScroll) {
        this.body.dom.style.position = 'relative';
    }
});

//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.videobrowser_widget))
//panelArr.push(vimeoWidget);

//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.workspacecloud_widget))
//panelArr.push(tagPanel);

//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.aboutkc_widget))
//panelArr.push(aboutKC);



var isToAppendArray=new Array();
for(var i=0;i<widgetIdArray.length;i++){
    isToAppendArray[i]=1;
}

/************initially 3 widgets added to dashboard************/
var column_wise_widgets="";
var mainLM=new Wtf.LoadMask(mainPanel.el.dom,{
    msg:WtfGlobal.getLocaleText("crm.dashboard.loadmsg")//"Loading Widget Thumbnails..."
});
mainLM.show();

getWidgetFrame();

function getWidgetFrame (){

    Wtf.Ajax.requestEx2({
        url:Wtf.req.springBase + Wtf.req.widget + 'getWidgetFrame.do',
        params:{
            flag:1,
            start:0,
            limit:5,
            limitReport:10,
            dataFlag:false
        }
    , scope:this, success:function(res){
        Wtf.Ajax.timeout = '30000';
        var responseWidgets = eval( '(' + res.colLength+ ')');
        var reportWidgets = res.reportwidget;
        var widgetFrame = res.widgetFrame;

        getWidgetData(widgetFrame);

        var res = eval( '(' + res.widgetData+ ')');
        var dataFlag = false;
        var _col1=new Array();
        var index=0;
        column_wise_widgets=responseWidgets;
        for(var i=0;i<responseWidgets.col1.length;i++){
            index=widgetIdArray.indexOf(responseWidgets.col1[i].id);
            isToAppendArray[index]=0;
            if(panelArr[index]!==undefined)
                Wtf.getCmp('portal_container_box1').add(createNewPanel(panelArr[index],res,dataFlag));
            Wtf.getCmp('portal_container').doLayout();
        }
        var _col2=new Array();
        for(i=0;i<responseWidgets.col2.length;i++){
            index=widgetIdArray.indexOf(responseWidgets.col2[i].id);
            isToAppendArray[index]=0;
            if(panelArr[index]!==undefined)
                Wtf.getCmp('portal_container_box2').add(createNewPanel(panelArr[index],res,dataFlag));
            Wtf.getCmp('portal_container').doLayout();
        }
        var _col3=new Array();
        for(i=0;i<responseWidgets.col3.length;i++){
            index=widgetIdArray.indexOf(responseWidgets.col3[i].id);
            isToAppendArray[index]=0;
            if(panelArr[index]!==undefined)
                Wtf.getCmp('portal_container_box3').add(createNewPanel(panelArr[index],res,dataFlag));
            Wtf.getCmp('portal_container').doLayout();
        }

        appendRemainingWidgets();
        handleReportWidget(reportWidgets);
        mainLM.hide();
        var objCampaignReportl=Wtf.getCmp(Wtf.moduleWidget.campaignreport);
        if(objCampaignReportl!=undefined)
            objCampaignReportl.callRequest("","",0);
        
        Wtf.getCmp("tabdashboard").doLayout.defer(1000);
    }, failure:function(){
        mainLM.hide();
    }});

}

function handleReportWidget(reportWidget) {
    for(var cnt = 0 ; cnt < reportWidget.length; cnt++) {
        getReportWidgetObject(parseInt(reportWidget[cnt].reportcode),reportWidget[cnt].reportname);
        var index = (cnt+1)%3;
        if(index == 0)
            index = 3;
        Wtf.getCmp('portal_container_box'+index).add(createNewPanel(panelArr[panelArr.length-1],reportWidget,true));
        Wtf.getCmp('portal_container').doLayout();
    }
}

function getReportWidgetObject(reportcode, reportname) {
    panelArr.push({
        config0:[{
            numRecs:5,
            template:new Wtf.XTemplate(
                "<tpl><div class='workspace listpanelcontent'>"+
                "<div>" +
                "<div style='padding-left:15px; background:transparent no-repeat scroll 0 0;'>{desc}</div>" +
                "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
                "</div>" +
                "</div></tpl>"
                ),
            isPaging: true,
            emptyText:WtfGlobal.getLocaleText("crm.dashboard.widget.mtytext"),//'No Updates',
            isSearch: false,
            headerHtml : '',
            linkcode : 1
        }
        ],
        title: reportname,
        draggable:true,
        isCallRequest : true,
        border:true,
        chartdetails:getPaneldetails(reportcode),//Leads Pipelined Report
        barchart:true,
        defaultChartView:true,
        tools: getToolsArrayForReport()
    });
}
function getWidgetData(widgetFrame){

    Wtf.Ajax.timeout = '5000000';
    Wtf.Ajax.requestEx({
        url:Wtf.req.springBase + Wtf.req.widget + 'getWidgetData.do',
        params:{
            flag:1,
            start:0,
            limit:5,
            limitReport:10,
            widgetFrame:widgetFrame
        }
    }, this, function(res){
        Wtf.Ajax.timeout = '30000';
        var responseWidgets = column_wise_widgets;
        var res = eval( '(' + res.widgetData+ ')');
        var _col1=new Array();
        var index=0;
 
        if(responseWidgets!=""){
            for(var i=0;i<responseWidgets.col1.length;i++){
                index=widgetIdArray.indexOf(responseWidgets.col1[i].id);
                isToAppendArray[index]=0;
                if(panelArr[index]!==undefined)
                    loadWidgetData(panelArr[index],res)
                Wtf.getCmp('portal_container').doLayout();
            }
            var _col2=new Array();
            for(i=0;i<responseWidgets.col2.length;i++){
                index=widgetIdArray.indexOf(responseWidgets.col2[i].id);
                isToAppendArray[index]=0;
                if(panelArr[index]!==undefined)
                    loadWidgetData(panelArr[index],res)
                Wtf.getCmp('portal_container').doLayout();
            }
            var _col3=new Array();
            for(i=0;i<responseWidgets.col3.length;i++){
                index=widgetIdArray.indexOf(responseWidgets.col3[i].id);
                isToAppendArray[index]=0;
                if(panelArr[index]!==undefined)
                    loadWidgetData(panelArr[index],res)
                Wtf.getCmp('portal_container').doLayout();
            }
        }

        var objCampaignReportl=Wtf.getCmp(Wtf.moduleWidget.campaignreport);
        if(objCampaignReportl!=undefined)
            objCampaignReportl.callRequest("","",0);
        var obj = Wtf.getCmp(Wtf.moduleWidget.topactivity);
        if(obj!=null) {
            obj.callRequest("","",0);
            Wtf.refreshUpdatesAll();
        }

    }, function(){
        });


}
function loadWidgetData(setting,res){
    if(setting !== undefined) {
        if (setting.config1 != null) {
            //  return (new Wtf.WtfCustomPanel(setting,res,dataFlag));
            for(var count = 0;count<setting.config1.length;count++){
                this.count = count;
                this.newObj = setting.config1[count];
                if(res) {
                    if(res.ModuleUpdates!=undefined && this.newObj.paramsObj.flag==5){
                        Wtf.getCmp("DSBMyWorkspaces").CrmModuleWidget(res.ModuleUpdates[0]);
                    } else if(res.CrmModuleDrag!=undefined && this.newObj.paramsObj.flag==6){
                        Wtf.getCmp("crmmodule_drag").CrmModuleWidget(res.CrmModuleDrag);
                    } else if(res.ReportUpdates!=undefined && this.newObj.paramsObj.flag==8){
                        Wtf.getCmp("reports_drag").CrmModuleWidget(res.ReportUpdates[0])
                    } else if(res.marketing_drag!=undefined && this.newObj.paramsObj.flag==9){
                        Wtf.getCmp("marketing_drag").CrmModuleWidget(res.marketing_drag)
                    } else if(res.CampaignReportUpdates!=undefined && this.newObj.paramsObj.flag==10){
                        Wtf.getCmp(Wtf.moduleWidget.campaignreport).CrmModuleWidget(res.CampaignReportUpdates[0])
                    } else if(res.crm_admin_widget!=undefined && this.newObj.paramsObj.flag==11){
                        Wtf.getCmp("crm_admin_widget").CrmModuleWidget(res.crm_admin_widget)
                    } else if(res.DSBAdvanceSearch!=undefined && this.newObj.paramsObj.flag==12){
                        Wtf.getCmp("DSBAdvanceSearch").CrmModuleWidget(res.DSBAdvanceSearch)
                    } else if(res.CustomReportUpdates!=undefined && this.newObj.paramsObj.flag==13){
                        Wtf.getCmp("crm_custom_reports").CrmModuleWidget(res.CustomReportUpdates[0])
                    }
                }
              
            }
        } else if (setting.config0 != null) {
            for(var count = 0;count<setting.config0.length;count++){
                this.count = count;
                this.newObj = setting.config0[count];
                   
                if(res.Campaign!=undefined && this.newObj.paramsObj.type==0){
                    Wtf.getCmp(Wtf.moduleWidget.campaign).dashBoardWidgetRequest(res.Campaign[0]);
                } else if(res.Lead!=undefined && this.newObj.paramsObj.type==1){
                    Wtf.getCmp(Wtf.moduleWidget.lead).dashBoardWidgetRequest(res.Lead[0]);
                } else if(res.Account!=undefined && this.newObj.paramsObj.type==2){
                    Wtf.getCmp(Wtf.moduleWidget.account).dashBoardWidgetRequest(res.Account[0]);
                } else if(res.Contact!=undefined && this.newObj.paramsObj.type==3){
                    Wtf.getCmp(Wtf.moduleWidget.contact).dashBoardWidgetRequest(res.Contact[0]);
                } else if(res.Opportunity!=undefined && this.newObj.paramsObj.type==4){
                    Wtf.getCmp(Wtf.moduleWidget.opportunity).dashBoardWidgetRequest(res.Opportunity[0]);
                } else if(res.Case!=undefined && this.newObj.paramsObj.type==5){
                    Wtf.getCmp(Wtf.moduleWidget.cases).dashBoardWidgetRequest(res.Case[0]);
                } else if(res.Activity!=undefined && this.newObj.paramsObj.type==6){
                    Wtf.getCmp(Wtf.moduleWidget.activity).dashBoardWidgetRequest(res.Activity[0]);
                } else if(res.Product!=undefined && this.newObj.paramsObj.type==7){
                    Wtf.getCmp(Wtf.moduleWidget.product).dashBoardWidgetRequest(res.Product[0]);
                }

                  
            }
        }
        else {
            if (setting.url != null) {
                return (new Wtf.WtfIframeWidgetComponent(setting));
            }
            else {
                return (new Wtf.WtfWidgetComponent(setting));
            }
        }
    }
}
//var panel1 = createNewPanel(panelArr[1]);
//var panel3 = createNewPanel(panelArr[3]);
//var panel2 = createNewPanel(panelArr[2]);

var paneltop = new Wtf.Panel({
    border: false,
    layout: 'border',
    frame: false,
    items: [{
        region: 'center',
        xtype: 'portal',
        id:'portal_container',
        bodyStyle: "background:white;",
        border: false,
        html:renderHelpDiv(),
        items: [{
            columnWidth: .33,
            cls: 'portletcls',
            id: 'portal_container_box1',
            border: false
        }, {
            columnWidth: .33,
            border: false,
            cls: 'portletcls',
            id: 'portal_container_box2'
        }, {
            columnWidth: .334,
            cls: 'portletcls',
            id: 'portal_container_box3',
            border: false
        }]
    }, {
        region: "south",
        height: 145,
        id : "dashboard-south",
        title: WtfGlobal.getLocaleText("crm.dashboard.southregion.title"),//"Add Dashboard Widgets",
        autoScroll:true,
        collapsible: true,
        collapsed: true,
        split: true,
        frame: true,
        html: '<div class="widgets" id="widgets">' +
    '<ul id="widgetUl">' +
    '</ul>' +
    '</div>'
    }]
});
Wtf.getCmp('portal_container').on('drop',function(e){
    Wtf.Ajax.requestEx({
        url:Wtf.req.widget + 'changeWidgetState.do',
        params:{
            flag:4,
            colno:e.columnIndex+1,
            position:e.position,
            wid:e.panel.id
        }
    }, this, function(){
        Wtf.getCmp('portal_container').doLayout();
    }, function(){});
},this);

Wtf.getCmp("tabdashboard").add(paneltop);
Wtf.getCmp("tabdashboard").doLayout();
/************initially 3 widgets added to top widget bar************/
var titleSpan = document.createElement("div");
titleSpan.innerHTML = WtfGlobal.getLocaleText("crm.dashboard.southregion.title");//"Add Dashboard Widgets";
titleSpan.id="southdash";
titleSpan.className = "collapsed-header-title";
Wtf.getCmp("dashboard-south").container.dom.lastChild.appendChild(titleSpan);

Wtf.QuickTips.register({
    target:  Wtf.get('southdash'),
    trackMouse: true,
    text: WtfGlobal.getLocaleText("crm.dashboard.unexpanded.title")//'Click to expand Dashboard Widget'
});
Wtf.QuickTips.enable();

function appendRemainingWidgets(){
    for(var i=0;i<isToAppendArray.length;i++){
        if(isToAppendArray[i]==1){
            appendWidget(i);
        }
    }
}

function takeTour(){
    if(document.getElementById('titlehelp') == null) {
        showHelp(1);
    }
}

function saveHelpState(){
    var chek = Wtf.get('showHelpCheck');
    if(chek && chek.dom.checked){
        Wtf.Ajax.requestEx({
            url: "Common/ProfileHandler/updateHelpflag.do",
            params:{
                userid:loginid,
                helpflag:1
            }
        },this,function() {
            },function() {});
    }
    noThanks();
}
function noThanks(){
    if(Wtf.get('dashhelp')){
        Wtf.get('dashhelp').slideOut('t',{
            remove: true
        });
    }
}
function renderHelpDiv(){
    var txt = helpFlag==1?"":"<div class='outerHelp' id='dashhelp'>" +
    "<div style='float:left; padding-left:1%; margin-top:-1px;'><img src='../../images/alerticon.jpg'/></div>" +
    "<div class='helpHeader'>"+WtfGlobal.getLocaleText("crm.deskera.helptext.newtodeskera")+"</div><div class='helpContent' id='wtf-gen285'>"+
    "<div style='padding-top: 5px; float: left;'><a href='#' class='helplinks guideme' onclick='takeTour()'>"+WtfGlobal.getLocaleText("crm.deskera.quicktourlink")+"</a>"+
    "  <a class='helplinks nothanks' href='#' onclick='saveHelpState()'>"+WtfGlobal.getLocaleText("crm.deskera.nothanx.msg")+"</a></div>"+
    "<div style='float:right; margin-right:10px;'><div class='checkboxtext'>"+
    "<input type='checkbox' id='showHelpCheck' style='margin-right: 5px; vertical-align: middle;'/><span style='margin-right:1%; color:#15428B;'>"+WtfGlobal.getLocaleText("crm.deskera.donotshowmsg")+"</span></div>"+
    "<span style='color:#333333; cursor:pointer; margin-top:1px; padding-top:5px; float:right;' id='closehelp' onclick='saveHelpState()'><img style='height:12px; width:12px;'src='../../images/cancel16.png' align='bottom'/></span></div></div></div>";
    return txt;
}
//appendWidget(0);
//appendWidget(4);
//appendWidget(5);
//appendWidget(6);
//appendWidget(7);
//appendWidget(8);
//appendWidget(9);
//appendWidget(10);
//appendWidget(11);
//appendWidget(12);
//appendWidget(14);
//appendWidget(15);
//appendWidget(16);
//appendWidget(17);

function requestForWidgetRemove(wid) {
    Wtf.Ajax.requestEx({
        url:Wtf.req.widget + 'removeWidgetFromState.do',
        params:{
            flag:2,
            wid:wid
        }
    }, this, function(){
        }, function(){});
}


//window.onbeforeunload = function() {
//        Wtf.Ajax.requestEx({
//            url: "jspfiles/chatmessage.jsp",
//            params: {
//                type : 5
//            }
//        }, this, function(result, req){
//        }, function(result, req){
//
//    });
//};

