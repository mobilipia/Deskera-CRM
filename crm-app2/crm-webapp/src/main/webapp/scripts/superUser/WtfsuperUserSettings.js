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
function superuser(mode, cid){
    switch(mode){
        case "status":
            mainPanel.loadTab("companystats.jsp?flag=1", "CompanyStatistics", "List of companies", "navareadashboard");
            break;
        case "subdomain":
            loadSubdomainList("offSubdomainList", "List of subdomain");
            break;
        case "subdetails":
            loadSubscriptionDetails("subscriptionDetails", "Subscription details");
            break;
        case "managesub":
            loadManageSubscription("manageSubscriptionPanel", "Manage subscription", cid);
            break;
    }
}

function loadSubscriptionDetails(id, title){
    var temp = Wtf.getCmp(id);
    if(temp === undefined){
        var compRec = Wtf.data.Record.create([{
            name: 'name'
        },{
            name: 'companyid'
        }]);
        var compStore = new Wtf.data.Store({
            url: "subscriptionStatus.jsp",
            baseParams: {
                mode: 1
            },
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            }, compRec)
        });
        compStore.load();
        temp = new Wtf.Panel({
            id: id,
            title: title,
            layout: 'fit',
            autoScroll: true,
            tbar: [new Wtf.form.ComboBox({
                id: "companyListCombo",
                allowBlank: false,
                emptyText:'< Select company >',
                typeAhead: false,
                forceSelection: true,
                triggerAction: 'all',
                store:compStore,
                displayField: "name",
                valueField:'companyid',
                mode: 'local',
                editable: false,
                height: 80,
                width: 200
            }), {
                text: "Submit",
                handler: function(){
                    var cid = Wtf.getCmp("companyListCombo").getValue();
                    if(cid != null && cid != ""){
                        temp.load({
                            url: 'subscriptionStatus.jsp',
                            params: {
                                mode: 2,
                                companyid: cid
                            }
                        });
                    }
                }
            }],
            closable: true
        });
        mainPanel.add(temp);
    }
    mainPanel.setActiveTab(temp);
}

function loadSubdomainList(id, title){
    var temp = Wtf.getCmp(id);
    if(temp === undefined){
        temp = new Wtf.Panel({
            id: id,
            title: title,
            layout: 'fit',
            autoScroll: true,
            autoLoad: {
                url: 'offdomain.jsp'
            },
            tbar: [new Wtf.form.TextField({
                id: 'subdomainField'
            }), {
                text: "Submit",
                handler: function(){
                    var domain = Wtf.getCmp("subdomainField").getValue();
                    if(domain != null && domain != ""){
                        temp.load({
                            url: 'offdomain.jsp',
                            params:{
                                subdomain: domain
                            }
                        });
                    }
                }
            }],
            closable: true
        });
        mainPanel.add(temp);
    }
    mainPanel.setActiveTab(temp);
}

function loadManageSubscription(id, title, cid){
    var temp = Wtf.getCmp(id);
    if(temp === undefined){
        var compRec = Wtf.data.Record.create([{
            name: 'name'
        },{
            name: 'companyid'
        }]);
        var compStore = new Wtf.data.Store({
            url: "subscriptionStatus.jsp",
            baseParams: {
                mode: 1
            },
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            }, compRec)
        });
        compStore.on("load", function(obj){
            loadSubTab(cid, id);
        });
        compStore.load();
        temp = new Wtf.Panel({
            id: id,
            title: title,
            layout: 'fit',
            autoScroll: true,
            tbar: [new Wtf.form.ComboBox({
                id: "companyListComboBox",
                allowBlank: false,
                emptyText:'< Select company >',
                typeAhead: false,
                forceSelection: true,
                triggerAction: 'all',
                store:compStore,
                displayField: "name",
                valueField:'companyid',
                mode: 'local',
                editable: false,
                height: 80,
                width: 200
            }), {
                text: "Submit",
                handler: function(){
                    var cid = Wtf.getCmp("companyListComboBox").getValue();
                    if(cid != null && cid != ""){
                        temp.load({
                            url: 'manageSubscription.jsp',
                            params: {
                                mode: 1,
                                companyid: cid
                            }
                        });
                    }
                }
            }],
            closable: true
        });
        mainPanel.add(temp);
    }
    mainPanel.setActiveTab(temp);
//    Wtf.getCmp("companyListComboBox").store.load();
    if(cid !== undefined)
        loadSubTab(cid, id);
}
function loadSubTab(cid, panelid){
    var t = Wtf.getCmp("companyListComboBox");
    if(cid !== undefined && t !== undefined){
        t.setValue(cid);
        Wtf.getCmp(panelid).load({
            url: 'manageSubscription.jsp',
            params: {
                mode: 1,
                companyid: cid
            }
        });
    }
}
function subscribemodeule(){
    var cid = Wtf.getCmp("companyListComboBox").getValue();
    if(cid != null && cid != ""){
        var tab = document.getElementById("companySubscriptionStatusTable");
        var ip = tab.getElementsByTagName("input");
        var pr =  {};
        pr["mode"] = 2;
        pr["companyid"] = cid;
        for(var i = 0; i < ip.length; i++){
            pr[ip[i].name] = ip[i].checked;
        }
        Wtf.getCmp("manageSubscriptionPanel").load({
            url: 'manageSubscription.jsp',
            params: pr
        });
    }
}
