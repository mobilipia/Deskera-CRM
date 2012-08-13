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
Wtf.CRMTabPanel = function(config){
    Wtf.CRMTabPanel.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.CRMTabPanel, Wtf.Panel, {

    onRender:function(config)
    {
        Wtf.CRMTabPanel.superclass.onRender.call(this, config);
        var str="<a href=# onClick=addTab('Leads',1)>Leads</a>";
        str+="<br><br>"+"<a href=# onClick='addOpportunityTab()'>Opportunity</a>";
        str+="<br><br>"+"<a href=# onClick='addAccountTab()'>Account</a>";
        str+="<br><br>"+"<a href=# onClick='addContactTab()'>Contact</a>";
        str+="<br><br>"+"<a href=# onClick='addCaseTab()'>Cases</a>";
        str+="<br><br>"+"<a href=# onClick='addCampaignTab()'>Campaign</a>";
        str+="<br><br>"+"<a href=# onClick='addProductTab()'>Products</a>";
        str+="<br><br>"+"<a href=# onClick='addActivityTab()'>Activities</a>";
        str+="<br><br>"+"<a href=# onClick='a()'>Report</a>";


        x=new Wtf.TabPanel({
            activeTab:0,border:false,
            id:'CRMTabPanelID',
           // bbar:new Wtf.Toolbar({items:[{text:"BotBut 1"},'-',{text:"BotBut 2"}]}),

            //tbar:new Wtf.Toolbar({items:[{text:"But 1"},'-',{text:"But 2"}]}),
            items:[{title:'DashBoard',
                         layout:'fit',border:false,
                          items:new Wtf.Panel({
                                                 border:false,
                                                 frame:true,
                                                 id:'CRMDashboardID',
                                                 html:str
                                                })
                      }]
        });
        this.add(x);

    }

});
