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
function addTab(title,id){
    if(!Wtf.getCmp(title+id))
    {
        x={       title:title,
            id:title+id,
            layout:'fit',
            closable:true,
            items:new Wtf.LeadTab({scope:this,layout:'fit'})
        }
        Wtf.getCmp('CRMTabPanelID').add(x);
    }

    Wtf.getCmp('CRMTabPanelID').setActiveTab(title+id);
    Wtf.getCmp('CRMTabPanelID').doLayout();
}


function addOpportunityTab(){
    if(!Wtf.getCmp('OpportunityTabPanID'))
    {
        x={       title:"Opportunity",
            id:"OpportunityTabPanID",
            layout:'fit',border:false,
            closable:true,
            items:new Wtf.OpportunityTab({scope:this,layout:'fit',id:'Opportunity'})
        }
        Wtf.getCmp('CRMTabPanelID').add(x);
    }

    Wtf.getCmp('CRMTabPanelID').setActiveTab('OpportunityTabPanID');
    Wtf.getCmp('CRMTabPanelID').doLayout();
}




function addAccountTab(){
    if(!Wtf.getCmp('AccountTabPanID'))
    {
        x={       title:"Account",
            id:"AccountTabPanID",
            layout:'fit',border:false,
            closable:true,
            items:new Wtf.AccountTab({scope:this,layout:'fit',id:'Account'})
        }
        Wtf.getCmp('CRMTabPanelID').add(x);
    }

    Wtf.getCmp('CRMTabPanelID').setActiveTab('AccountTabPanID');
    Wtf.getCmp('CRMTabPanelID').doLayout();
}

function addLeadTab(){
if(!Wtf.getCmp('LeadTabPanID'))
{
    x={       title:"Lead",
        id:"LeadTabPanID",
        layout:'fit',border:false,
        closable:true,
        items:new Wtf.AccountTab({scope:this,layout:'fit',id:'Lead'})
    }
    Wtf.getCmp('CRMTabPanelID').add(x);
}

Wtf.getCmp('CRMTabPanelID').setActiveTab('LeadTabPanID');
Wtf.getCmp('CRMTabPanelID').doLayout();
}


function addContactTab(){
if(!Wtf.getCmp('ContactTabPanID'))
{
    x={       title:"Contact",
        id:"ContactTabPanID",
        layout:'fit',border:false,
        closable:true,
        items:new Wtf.ContactsTab({scope:this,layout:'fit',id:'Contact'})
    }
    Wtf.getCmp('CRMTabPanelID').add(x);
}

Wtf.getCmp('CRMTabPanelID').setActiveTab('ContactTabPanID');
Wtf.getCmp('CRMTabPanelID').doLayout();
}

function addCaseTab(){
if(!Wtf.getCmp('CaseTabPanID'))
{
    x={       title:"Cases",
        id:"CaseTabPanID",
        layout:'fit',border:false,
        closable:true,
        items:new Wtf.CasesTab({scope:this,layout:'fit',id:'Case'})
    }
    Wtf.getCmp('CRMTabPanelID').add(x);
}

Wtf.getCmp('CRMTabPanelID').setActiveTab('CaseTabPanID');
Wtf.getCmp('CRMTabPanelID').doLayout();
}


function addCampaignTab(){
if(!Wtf.getCmp('CampaignTabPanID'))
{
    x={       title:"Campaign",
        id:"CampaignTabPanID",
        layout:'fit',border:false,
        closable:true,
        items:new Wtf.CampaignTab({scope:this,layout:'fit',id:'Campaign'})
    }
    Wtf.getCmp('CRMTabPanelID').add(x);
}

Wtf.getCmp('CRMTabPanelID').setActiveTab('CampaignTabPanID');
Wtf.getCmp('CRMTabPanelID').doLayout();
}



function addProductTab(){

if(!Wtf.getCmp('ProductTabPanID'))
{
    x={    title:"Products",
        id:"ProductTabPanID",
        layout:'fit',border:false,
        closable:true,
        items:new Wtf.ProductTab({scope:this,layout:'fit',id:'Product'})
    }
    Wtf.getCmp('CRMTabPanelID').add(x);
}

Wtf.getCmp('CRMTabPanelID').setActiveTab('ProductTabPanID');
Wtf.getCmp('CRMTabPanelID').doLayout();




}

function addActivityTab(){

if(!Wtf.getCmp('ActivityTabPanID'))
{
    x={    title:"Activities",
        id:"ActivityTabPanID",
        layout:'fit',border:false,
        closable:true,
        items:new Wtf.ActivityTab({scope:this,layout:'fit',id:'Activity'})
    }
    Wtf.getCmp('CRMTabPanelID').add(x);
}

Wtf.getCmp('CRMTabPanelID').setActiveTab('ActivityTabPanID');
Wtf.getCmp('CRMTabPanelID').doLayout();




}

function addReportTab(){

if(!Wtf.getCmp('ReportTabPanID'))
{
    x={    title:"Reports",
        id:"ReportTabPanID",
        layout:'fit',border:false,
        closable:true,
        items:new Wtf.ReportTab({scope:this,layout:'fit',id:'Report'})
    }
    Wtf.getCmp('CRMTabPanelID').add(x);
}

Wtf.getCmp('CRMTabPanelID').setActiveTab('ReportTabPanID');
Wtf.getCmp('CRMTabPanelID').doLayout();
  

}

