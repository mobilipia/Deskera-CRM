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
function deletePortlet(obj, admin){
    Wtf.MessageBox.confirm('Confirm', 'Are you sure you want to delete the portlet?', function(btn){
        if(btn == "yes")
            Wtf.getCmp(admin).deletePortlet(obj.id.substring(4));
        },
    this);
}
function addPortlet(admin){
    Wtf.getCmp(admin).addPortlet();
}
function deleteLinkGroup(obj, admin){
    Wtf.MessageBox.confirm('Confirm', 'Are you sure you want to delete the portlet?', function(btn){
        if(btn == "yes")
            Wtf.getCmp(admin).deleteLinkGroup(obj.id.substring(4));
        },
    this);
}
function addLinkGroup(admin){
    Wtf.getCmp(admin).addLinkGroup();
}
function deleteLink(obj, admin){
    Wtf.MessageBox.confirm('Confirm', 'Are you sure you want to delete the portlet?', function(btn){
        if(btn == "yes")
            Wtf.getCmp(admin).deleteLink(obj.id.substring(4));
        },
    this);
}
function addLink(admin){
    Wtf.getCmp(admin).addLink();
}

Wtf.portletWindow = function(conf){
    Wtf.apply(this, conf);
    this.buttons = [{
        text: "Add",
        handler: this.getPortletConf,
        scope: this
    },{
        text: "Cancel",
        handler: function(){
            this.close();
        },
        scope: this
    }];
    this.addEvents({
        "okClicked": true
    });
    Wtf.portletWindow.superclass.constructor.call(this, conf);
};

Wtf.extend(Wtf.portletWindow, Wtf.Window, {
    modal: true,
    resizable: false,
    onRender: function(conf){
        Wtf.portletWindow.superclass.onRender.call(this, conf);
        var chkSM = new Wtf.grid.CheckboxSelectionModel({
            singleSelect: false
        });
        this.simpleS = new Wtf.data.SimpleStore({
            fields :['0', '1'],
            data:[]
        });
        this.modDetailGrid = new Wtf.grid.GridPanel({
            sm: chkSM,
            cm: new Wtf.grid.ColumnModel([chkSM,
                { header: "Field", mapping: "0", renderer: function(val, a, rec, c){
                    return rec.data["0"];
                } }
            ]),
            viewConfig: { forceFit : true },
            ds: this.simpleS,
            autoScroll: true,
            height: 102
        });
        this.modCombo = new Wtf.form.ComboBox({
            fieldLabel: "Module",
            itemCls: "portletField",
            mode: "local",
            store: this.modStore,
            displayField: "modulename",
            valueField: "moduleid"
        });
        this.modCombo.on("change", function(obj, oVal, nVal){
            var mStore = obj.store;
            var rec = mStore.query("moduleid", obj.getValue()).items[0];
            var columns = eval("(" + rec.data["columns"] + ")").columnheader;
            var dispCol = [];
            for(var cnt = 0; cnt < columns.length; cnt++){
                if(!columns[cnt]["3"]){
                    dispCol.push(columns[cnt]);
                }
            }
            this.simpleS.loadData(dispCol, false);
        }, this);
        this.pTitle = new Wtf.form.TextField({
            itemCls: "portletField",
            fieldLabel: "Portlet Title"
        });
        this.configPanel = this.add(new Wtf.form.FormPanel({
            items: [this.pTitle,this.modCombo,this.modDetailGrid]
        }));
    },
    getPortletConf: function(){
        var conf = {};
        conf["title"] = this.pTitle.getValue();
        conf["moduleid"] = this.modCombo.getValue();
        conf["modulename"] = this.modCombo.el.dom.value;
        var cols = this.modDetailGrid.getSelectionModel().getSelections();
        var colJ = "";
        for(var cnt = 0; cnt < cols.length; cnt++){
            colJ += "{\"columnname\":" + cols[cnt].data["1"] + "},";
        }
        if(colJ != "")
            colJ = "[" + colJ.substring(0, (colJ.length - 1)) + "]";
        conf["columns"] = colJ;
        this.fireEvent("okClicked", this, conf);
        this.close();
    }
});




Wtf.dashDesignPanel = function(conf){
    Wtf.apply(this, conf);
    this.tbar = [{
        text: "Save",
        handler: this.saveConfig,
        scope: this
    }/*,{
        text: "Launch",
        handler: this.previewDashboard,
        scope: this
    }*/];
    Wtf.dashDesignPanel.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.dashDesignPanel, Wtf.Panel,{
    randid: 0,
    onRender: function(conf){
        Wtf.dashDesignPanel.superclass.onRender.call(this, conf);
        var seperator = {
            border: false,
            html: '<hr style = "width: 75%;margin-left: 10px">'
        };
        var defConf = {ctCls: 'dashfieldContainerClass',labelStyle: 'font-size: 11px; text-align: right;'};
        this.portletRec = Wtf.data.Record.create([
            {name: "portlet", mapping: "portlettitle" },
            {name: "module", mapping: "reportname"},
            {name: "moduleid", mapping: "reportid"},
            {name: "colconfig", mapping: "config"}
        ]);
        this.portletStore = new Wtf.data.Store({
            url: Wtf.req.rbuild+"report.do",
            baseParams: {
                action: 24
            },
            reader: new Wtf.data.JsonReader({
                root: "data"
            }, this.portletRec)
        });
        this.linkGroupRec = Wtf.data.Record.create([
            {name: "groupname"},
            {name: "groupid"}
        ]);
        this.linkGroupStore = new Wtf.data.Store({
            url: Wtf.req.rbuild+"report.do",
            baseParams: {
                action: 22
            },
            reader: new Wtf.data.JsonReader({
                root: "data"
            }, this.linkGroupRec)
        });
        this.linkRec = Wtf.data.Record.create([
            {name: "link", mapping: "linktext"},
            {name: "process", mapping: "processname"},
            {name: "processid", mapping: "processid"},
            {name: "group", mapping: "grouptext"},
            {name: "groupid", mapping: "groupid"}
        ]);
        this.linkStore = new Wtf.data.Store({
            url: Wtf.req.rbuild+"report.do",
            baseParams: {
                action: 23
            },
            reader: new Wtf.data.JsonReader({
                root: "data"
            }, this.linkRec)
        });
        this.portletStore.load();
        this.linkGroupStore.load();
        this.linkStore.load();
        var modRec = Wtf.data.Record.create([
            { name: "modulename" },
            { name: "moduleid" },
            { name: "columns" }
        ]);
        this.modStore = new Wtf.data.Store({
            url: Wtf.req.rbuild+"report.do",
            baseParams: {
                action: 25
            },
            reader: new Wtf.data.JsonReader({
                root: "data"
            }, modRec)
        });
        this.modStore.load();
        var processRec = Wtf.data.Record.create([
            { name: "processname", mapping:"modulename" },
            { name: "processid", mapping:"moduleid" }
        ]);
        this.processStore = new Wtf.data.Store({
            url: Wtf.req.rbuild+"report.do",
            baseParams: {
                action: 25
            },
            reader: new Wtf.data.JsonReader({
                root: "data"
            }, processRec)
        });
        this.processStore.load();
        this.processCombo = new Wtf.form.ComboBox({
            displayField: "processname",
            fieldLabel: "Module",
            valueField: "processid",
            store: this.processStore,
            mode: 'local',
            typeAhead: true
        });
        this.groupCombo = new Wtf.form.ComboBox({
            displayField: "groupname",
            fieldLabel: "Link Group",
            valueField: "groupid",
            store: this.linkGroupStore,
            mode: 'local',
            typeAhead: true
        });
        var saveButton = new Wtf.Button({
            text: "Add link",
            handler: this.addLink,
            scope: this
        });
        this.companyDetailsPanel = new Wtf.form.FormPanel({
            id: 'companyDetailsForm',
            url: Wtf.req.rbuild+"report.do?action=26",
            fileUpload: true,
            cls: 'adminFormPanel',
            autoScroll: true,
            border: false,
            items: [{
                xtype: 'hidden',
                name: 'grpjson',
                id: "grpJsonField"
            },{
                xtype: 'hidden',
                name: 'linkjson',
                id: "linkJsonField"
            },{
                xtype: 'hidden',
                name: 'portletjson',
                id: "portletJsonField"
            },{
                layout: 'column',
                border: false,
                items:[{
                    columnWidth: 0.49,
                    border: false,
                    items: [
//                    {
//                        id:'compfieldset',
//                        xtype : 'fieldset',
//                        cls: "dashcompanyFieldSet",
//                        title: 'Company Logo',
//                        defaultType: 'textfield',
//                        autoHeight: true,
//                        items:[{
//                            labelStyle: 'font-size: 11px; text-align: right;',
//                            name: 'logo',
//                            id: 'logoFileDialog',
//                            inputType: "file",
//                            ctCls: 'dashfieldContainerClass',
//                            fieldLabel: 'Logo  ',
//                            width: 200
//                        }]
//                    },
//                    seperator,
                    {
                        xtype: 'fieldset',
                        id: "newPortletCont",
                        cls: "dashcompanyFieldSet",
                        defaults: defConf,
                        title: 'Dashboard Portlet',
                        defaultType: 'textfield',
                        autoHeight: true,
                        items:[{
                            xtype:"panel",
                            layout:'form',
                            autoDestroy: true,
                            border: false,
                            id: 'newHolidayPanel',
                            html: "<a href='#' onclick=\"addPortlet('" + this.id + "')\">Add new portlet</a>"
                        },{
                            xtype: 'dataview',
                            id: 'portletDataView',
                            itemSelector: "newPortletCont",
                            tpl: new Wtf.XTemplate('<div class="listpanelcontent" style="width:300px !important; margin: auto"><tpl for=".">{[this.f(values)]}</tpl></div>', {
                                f: function(val){
                                    return "<div id='" + val.portlet + "'><span class='dashholidaySpan dashholidayspanwidth'>" + val.portlet + "</span>" +
                                        "<span class='dashholidaySpan dashholidayspanwidth'>" + val.module + "</span>" +
                                        "<img src='images/Delete.png' class='dashholidayDelete' onclick=\"deletePortlet(this,'" + this.scope.id + "')\" id='del_"+val.portlet+"' title='Delete Portlet'>"+
                                        "<div><span class='dashholidayDiv'></span></div></div>";
                                },
                                scope: this
                            }),
                            store: this.portletStore,
                            emptyText: '<span class="dashholidaySpan">There are no portlets</span>'
                        }]
                    }]
                },{
                    columnWidth: 0.49,
                    border: false,
                    items:[{
                        xtype : 'fieldset',
                        cls: "dashcompanyFieldSet",
                        title: 'Dashboard Link Group',
                        defaultType: 'textfield',
                        defaults : defConf,
                        id: "newDashboardLinkGroup",
                        autoHeight: true,
                        items:[{
                            xtype:"panel",
                            layout:'form',
                            autoDestroy: true,
                            border: false,
                            html: '<div style="display: block; padding-top:5px;" id="addHoliday">' +
                                '<span style="float: left; margin-right: 5px;">Group title: </span>' + 
                                '<input type="text" style="float:left; margin-right: 5px;" id="groupTextF" maxlength="512">' +
                                '<img src="images/check16.png" class="dashholidaDelete" onclick= \'addLinkGroup("' + this.id + '")\' title=\'Add link group\'></div>'
                        },{
                            xtype: 'dataview',
                            id: 'linkGroupDataView',
                            itemSelector: "newDashboardLinkGroup",
                            tpl: new Wtf.XTemplate('<div class="listpanelcontent" style="width:160px !important; margin: auto"><tpl for=".">{[this.f(values)]}</tpl></div>', {
                                f: function(val){
                                    return "<div id='" + val.groupname + "'><span class='dashholidaySpan dashholidayspanwidth'>" + val.groupname + "</span>" +
                                        "<img src='images/Delete.png' class='dashholidayDelete' onclick=\"deleteLinkGroup(this,'" + this.scope.id + "')\" id='del_"+val.groupname+"' title='Delete Group'>"+
                                        "<div><span style = 'width: 150px !important'class='dashholidayDiv'></span></div></div>";
                                },
                                scope: this
                            }),
                            store: this.linkGroupStore,
                            emptyText: '<span class="dashholidaySpan">There are no link-groups</span>'
                        }]
                    }, seperator, {
                        xtype : 'fieldset',
                        cls: "dashcompanyFieldSet",
                        title: 'Dashboard Links',
                        defaults : defConf,
                        id: "newDashboardLinks",
                        autoHeight: true,
                        items:[{
                            xtype:"panel",
                            id: "linkFormPanel",
                            layout:'form',
                            autoDestroy: true,
                            border: false,
                            items:[new Wtf.form.TextField({
                                fieldLabel: "Link ",
                                id: "linkTextField"
                            }), this.processCombo, this.groupCombo, saveButton]
                        },{
                            xtype: 'dataview',
                            id: 'linkDataView',
                            itemSelector: "newDashboardLinks",
                            tpl: new Wtf.XTemplate('<div class="listpanelcontent" style="width:450px !important; margin: auto"><tpl for=".">{[this.f(values)]}</tpl></div>', {
                                f: function(val){
                                    return "<div id='" + val.link + "'><span class='dashholidaySpan dashholidayspanwidth'>" + val.link + "</span>" +
                                        "<span class='dashholidaySpan dashholidayspanwidth'>" + val.process + "</span>" +
                                        "<span class='dashholidaySpan dashholidayspanwidth'>" + val.group + "</span>" +
                                        "<img src='images/Delete.png' class='dashholidayDelete' onclick=\"deleteLink(this,'" + this.scope.id + "')\" id='del_"+val.link+"' title='Delete Links'>"+
                                        "<div><span class='dashholidayDiv' style='width: 450px'></span></div></div>";
                                },
                                scope: this
                            }),
                            store: this.linkStore,
                            emptyText: '<span class="dashholidaySpan">There are no links.</span>'
                        }]
                    }]
                }]
            }]
        });
        this.add(this.companyDetailsPanel);
    },
    deletePortlet: function(day){
        var rec = this.portletStore.getAt(this.portletStore.find("portlet", day));
        this.portletStore.remove(rec);
        Wtf.getCmp('portletDataView').refresh();
    },
    addPortlet: function(){
        var pWin = new Wtf.portletWindow({
            height: 250,
            modStore: this.modStore,
            width: 350,
            title: "Portlet Configuration"
        });
        pWin.on("okClicked", function(obj, conf){
            var temp = new this.portletRec({
                portlet: conf.title,
                module: conf.modulename,
                moduleid: conf.moduleid,
                colconfig: conf.columns
            });
            this.portletStore.add(temp);
            Wtf.getCmp("portletDataView").refresh();
        }, this);
        pWin.show();
    },
    deleteLinkGroup: function(day){
        var rec = this.linkGroupStore.getAt(this.linkGroupStore.find("groupname", day));
        this.linkGroupStore.remove(rec);
        Wtf.getCmp('linkGroupDataView').refresh();
    },
    addLinkGroup: function(){
        var grpText = Wtf.get("groupTextF").dom.value;
        if(grpText != ""){
            var temp = new this.linkGroupRec({
                groupname: grpText,
                groupid: ++this.randid
            });
            this.linkGroupStore.add(temp);
        }
        Wtf.getCmp('linkGroupDataView').refresh();
    },
    deleteLink: function(day){
        var rec = this.linkStore.getAt(this.linkStore.find("link", day));
        this.linkStore.remove(rec);
        Wtf.getCmp('linkDataView').refresh();
    },
    addLink: function(){
        var lnkText = Wtf.getCmp("linkTextField").getValue();
        var modId = this.processCombo.getValue();
        var grpId = this.groupCombo.getValue();
        if(lnkText != "" && modId != ""){
            var temp = new this.linkRec({
                link: lnkText,
                processid: modId,
                process: this.processCombo.el.dom.value,
                group: this.groupCombo.el.dom.value,
                groupid: grpId
            });
            this.linkStore.add(temp);
        }
        Wtf.getCmp('linkDataView').refresh();
    },
    saveConfig: function(){
        var grpJson = "";
        for(var cnt = 0; cnt < this.linkGroupStore.getCount(); cnt++){
            grpJson += Wtf.encode(this.linkGroupStore.getAt(cnt).data) + ",";
        }
        if(grpJson != "")
            grpJson = "[" + grpJson.substring(0, (grpJson.length - 1)) + "]";
        var linkJson = "";
        for(var cnt = 0; cnt < this.linkStore.getCount(); cnt++){
            linkJson += Wtf.encode(this.linkStore.getAt(cnt).data) + ",";
        }
        if(linkJson != "")
            linkJson = "[" + linkJson.substring(0, (linkJson.length - 1)) + "]";
        var pJson = "";
        for(var cnt = 0; cnt < this.portletStore.getCount(); cnt++){
            pJson += Wtf.encode(this.portletStore.getAt(cnt).data) + ",";
        }
        if(pJson != "")
            pJson = "[" + pJson.substring(0, (pJson.length - 1)) + "]";
        Wtf.getCmp("grpJsonField").setValue(grpJson);
        Wtf.getCmp("linkJsonField").setValue(linkJson);
        Wtf.getCmp("portletJsonField").setValue(pJson);
        this.companyDetailsPanel.form.submit({
            scope: this,
            params:{
                linksAddFlag : false
            },
            success: function(result,action){
                var resultObj = eval('('+action.response.responseText+')');
                if(resultObj.success) {
                    msgBoxShow(46,2);
                } else {
                    msgBoxShow(5,1);
                }
            },
            failure: function(frm, action){
                msgBoxShow(5,1);
            }
        });
//        Wtf.Ajax.requestEx({
//            url: "reportbuilder.jsp",
//            params: {
//                action: 26,
//                grpjson: grpJson,
//                linkjson: linkJson,
//                portletjson: pJson
//            }
//        }, this, function(){
//            this.linkGroupStore.reload();
//            this.linkStore.reload();
//        }, function(){
//            msgBoxShow(["Failure", "Error occurred at server"]);
//        });
    },
    previewDashboard: function(){
        mainPanel.loadTab("customDash.html", "   _custom_dashboard", WtfGlobal.getLocaleText("crm.dashboard"));
    }
});

var designPanel = new Wtf.dashDesignPanel({
    id: "dashboardDesignerPanel",
    layout: "fit"
});

Wtf.getCmp("tabdashboardDesigner").add(designPanel);
Wtf.getCmp("tabdashboardDesigner").doLayout();
