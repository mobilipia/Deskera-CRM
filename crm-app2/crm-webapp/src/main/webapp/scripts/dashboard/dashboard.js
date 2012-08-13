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
Wtf.dashboardPanel = function(conf){
    Wtf.apply(this, conf);
    Wtf.dashboardPanel.superclass.constructor.call(this, conf);
};

Wtf.extend(Wtf.dashboardPanel, Wtf.Panel, {
    iconCls: getTabIconCls(Wtf.etype.home),
    tabType: Wtf.etype.home,
    onRender: function(conf){
        Wtf.dashboardPanel.superclass.onRender.call(this, conf);
        this.contPanel = this.add(new Wtf.Panel({
            layout: "column",
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
                columnWidth: .33,
                cls: 'portletcls',
                id: 'portal_container_box3',
                border: false
            }]
        }));
        Wtf.getCmp('portal_container_box3').add(new Wtf.mbuild.WtfCustomPanel({
            config1:[{
//                url:'reportbuilder.jsp',
                autoLoad: true,
                emptyText:'No Workspaces',
                isSearch: false,
                headerHtml : ''
//                paramsObj: {action:27}
            }],
            autoLoad: {
                url: Wtf.req.rbuild+"report.do",
                params: {action: 27},
                scripts: true
            },
            id: "quickLinks_portlet",
            draggable:true,
            border:true,
            bodyStyle:'margin-bottom: 10px;',
            title: 'Quick Links'
        }));
        Wtf.Ajax.requestEx({
            url: Wtf.req.rbuild+"report.do",
            params: {
                action: 29
            }
        }, this, function(responseText){
//            var obj = eval('(' + responseText + ')');
            if(responseText.portletdata) {
                this.insertPortlet(responseText.portletdata);
            }
            if(responseText.groupdata) {
                this.insertLinkGroups(responseText.groupdata);
            }
        }, function(){
            alert("failure");
        });
    },
    insertPortlet: function(response){
        for(var cnt = 0; cnt < response.length; cnt++){
            var contId = "portal_container_box" + ((cnt%3) + 1);
            Wtf.getCmp(contId).add(new Wtf.WtfCustomPanel({
                config1:[{
                    autoLoad: true,
                    emptyText:'No Workspaces',
                    isSearch: false,
                    headerHtml : ''
//                    paramsObj: {action:27}
                }],
                autoLoad: {
                    url: Wtf.req.rbuild+"report.do",
                    params: {
                        action: 30,
                        portletid: response[cnt].portletid
                    },
                    scripts: true
                },
                id: "portlet_" + response[cnt].portletid,
                draggable:true,
                border:true,
                bodyStyle:'margin-bottom: 10px;',
                title: response[cnt].portlettitle
//                tools: getToolsArray('jspfiles/rss.jsp?i=library&u=' + getCookie("lid"))
            }));
        }
        this.contPanel.doLayout();
    },
    insertLinkGroups: function(response){
        for(var cnt = 0; cnt < response.length; cnt++){
            var contId = "portal_container_box" + ((cnt%3) + 1);

            Wtf.getCmp(contId).add(new Wtf.WtfCustomPanel({
                config1:[{
    //                url:'reportbuilder.jsp',
                    autoLoad: true,
                    emptyText:'No Workspaces',
                    isSearch: false,
                    headerHtml : ''
    //                paramsObj: {action:27}
                }],
                autoLoad: {
                    url: Wtf.req.rbuild+"report.do",
                    params: {
                        action: 40,
                        groupid: response[cnt].groupid
                    },
                    scripts: true
                },
                id: "quickLinks_"+ response[cnt].groupid,
                draggable:true,
                border:true,
                bodyStyle:'margin-bottom: 10px;',
                title: response[cnt].grouptext
            }));
        }
        this.contPanel.doLayout();
    }
});

var designPanel = new Wtf.dashboardPanel({
    id: "_custom_dashboardPanel",
    layout: "fit"
});

Wtf.getCmp("tab_custom_dashboard").add(designPanel);
Wtf.getCmp("tab_custom_dashboard").doLayout();


function getToolsArray(ru){
    var ta = [];
    if (ru) {
        ta.push({
            id: 'rss',
            handler: function(e, target, panel){
                window.open(ru, '_blank');
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

function getReportDetails(processid, taskids, moduleids, linkName){
    Wtf.Ajax.requestEx({
        url: Wtf.req.rbuild+"report.do",
        params: {
            action: 28,
            reportids: processid,
            taskids: '4028808429a619170129a6a625cc000a' //temp id will be used for process states later on - task denotes a step in workflow activity
        }
    }, this, function(response){
        var obj1 = response;
        var reportObj = obj1.reportdata;
        for(var ctr = 0; ctr < reportObj.length; ctr++) {
            var obj = reportObj[ctr];
            var formData = {};
            if(obj.jdata !== undefined){
                formData = eval("(" + obj.jdata + ")");
            }
            //var recperm=[{"2":["{\"permgrid\":52,\"perm\":0}"]},{"3":["{\"permgrid\":52,\"perm\":1}"]},{"4":["{\"permgrid\":52,\"perm\":2}"]},{"5":["{\"permgrid\":52,\"perm\":3}"]},{"6":["{\"permgrid\":52,\"perm\":4}"]},{"7":["{\"permgrid\":52,\"perm\":5}"]},{"8":["{\"permgrid\":52,\"perm\":6}"]}];
            var configFields={
                ismodule: !obj.isreport,
                data: formData,
                cmpId: obj.taskid + obj.reportid,
                moduleName: obj.reportname,
                moduleId: obj.reportid,
                taskid: obj.taskid,
                containerId: mainPanel.id,
                isFilter: false,
                filterfield: '',
                filterValue: '',
                permsObj : eval("(" + obj.perms + ")").recordperm
            };

            openGridModule(configFields);
        }
    },
    function(response){
        alert(response);
    });
}

//Wtf.getCmp("tab_custom_dashboard").add(new Wtf.dashboardPanel({
//    layout: 'fit'
//}));
//Wtf.getCmp("tab_custom_dashboard").doLayout();
