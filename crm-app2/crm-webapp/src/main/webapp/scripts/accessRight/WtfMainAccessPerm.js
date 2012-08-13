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
function deleteUPG(gid, cid) {
    Wtf.Msg.show({
       title:'Cofirm Delete',
       msg: 'Are you sure you want to delete this User Profile Group?',
       buttons: Wtf.Msg.YESNO,
       fn: function(btn, text) {
           if (btn == 'yes'){
                Wtf.Ajax.requestEx({
                    url: Wtf.req.accessR+"accessRight.do",
                    method: 'POST',
                    params: {
                        action: 16,
                        groupid : gid
                    }},this,
                    function(response, e){
                        if(Wtf.decode(response).data == "yes") {
                            Wtf.getCmp("rolegrpgrid").getStore().reload();
                        } else if(Wtf.decode(response).data == "no") {
                            Wtf.Msg.alert('Error', "Unable to delete due to dependencies");
                        } else {
                            Wtf.Msg.alert('Error', "Error connecting to server");
                        }
                    },
                    function(response, e) {
                        var i = 0;
                });
            }
       },
       icon: Wtf.MessageBox.QUESTION
    });

}

function copyUPG(originalgid, gname, gdesc) {
    this.rolecmWin = new Wtf.grid.ColumnModel([
        new Wtf.grid.RowNumberer(),
        {
            dataIndex: 'roleid',
            hidden: true
        },{
            header: "Role",
            dataIndex: 'rolename',
            editor: new Wtf.form.TextField({allowBlank: false, maxLength: 100})
    }]);
    this.roleReaderWin = new Wtf.data.Record.create([
        {name: 'roleid'},
        {name: 'rolename'}
    ]);

    this.roledsWin = new Wtf.data.Store({
        url: Wtf.req.accessR+"accessRight.do",
        reader: new Wtf.data.KwlJsonReader({
                root:"data"}, this.roleReaderWin)
    });
    this.roledsWin.baseParams = {
        action: 18,
        grpid: originalgid
    }
    this.roledsWin.load();
    this.cpWin = new Wtf.Window({
        title:'Copy User Profile Group',
        layout:'fit',
        iconCls: 'winicon',
        modal: true,
        height:400,
        width:550,
        scope: this,
        buttons: [{
            text: 'Copy',
            handler: function() {

                if(this.copyForm.form.isValid()) {
                    var str = "";
                    for(var i = 0; i < this.roledsWin.getCount(); i++) {
                        str += Wtf.encode(this.roledsWin.getAt(i).data) + ",";
                    }
                    if(!Wtf.isEmpty(str)) {
                        str = str.substr(0, (str.length - 1));
                        str = "[" + str + "]";
                    }

                    Wtf.Ajax.requestEx({
                        url: Wtf.req.accessR+"accessRight.do",
                        method: 'POST',
                        params: {
                            action: 17,
                            gid: originalgid,
                            gname: this.roleGrpText.getValue(),
                            gdesc: this.roleGrpDesc.getValue(),
                            json: str
                        }},this,
                        function(response, e){
                            Wtf.getCmp("rolegrpgrid").getStore().reload();
                            this.cpWin.close();
                    });
                } /*else {
                    Wtf.Msg.alert('Error', 'Please enter .');
                }*/

            },
            scope: this
        }, {
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
            scope: this,
            handler: function() {
                this.cpWin.close();
            }
        }],
        items: [{
            layout: 'border',
            items: [
                this.copyForm = new Wtf.form.FormPanel({
                    //title: "top",
                    region: "north",
                    bodyStyle: "padding: 10px;",
                    border: false,
                    labelWidth: 160,
                    height: 150,
                    buttonAlign: 'right',
                    items: [this.roleGrpText = new Wtf.form.TextField({
                            fieldLabel: 'Group Name*',
                            anchor: '95%',
                            maxLength: 150,
                            allowBlank: false,
                            value: "Copy of " + gname,
                            id: this.id + 'rolegrpText'
                    }), this.roleGrpDesc = new Wtf.form.TextArea({
                        fieldLabel: 'Description',
                        value: gdesc,
                        maxValue: 200,
                        anchor: '95%'

                    })]

                }),
                this.roleGrid = new Wtf.grid.EditorGridPanel({
                    store: this.roledsWin,
                    cm: this.rolecmWin,
                    region: 'center',
                    loadMask: true,
                    border: false,
                    clicksToEdit: 1,
                    viewConfig: {
                        forceFit: true
                    }
                })

            ]
        }

        ]
    });
    this.cpWin.show();
}

Wtf.MainAuthPanel = function(config){
    Wtf.MainAuthPanel.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.MainAuthPanel, Wtf.Panel, {
     initComponent: function() {
         Wtf.MainAuthPanel.superclass.initComponent.call(this);
         this.rolegrRecord = Wtf.data.Record.create([{
            name: 'groupid',
            type: 'string'
            }, {
            name: 'groupname',
            type: 'string'
            }, {
            name: 'description',
            type: 'string'
            }, {
            name: 'del'
            }, {
            name: 'copy'
            }]);

        Wtf.ux.comboBoxRenderer = function(combo) {
            return function(value) {
                var idx = combo.store.find(combo.valueField, value);
                if(idx == -1)
                    return "";
                var rec = combo.store.getAt(idx);
                return rec.get(combo.displayField);
            };
        }

    this.rolegrReader = new Wtf.data.KwlJsonReader({
        root: "data"
    }, this.rolegrRecord);

    this.rolegrStore = new Wtf.data.GroupingStore({
        proxy: new Wtf.data.HttpProxy({
            url: Wtf.req.accessR+"accessRight.do?action=1"
        }),
        reader: this.rolegrReader
    });

    this.rolegrStore.on("load",this.afterComboLoad,this);

    this.rolegrComboItem = new Wtf.form.ComboBox({
                    triggerAction: 'all',
                    store:this.rolegrStore,
                    mode:'local',
                    readOnly : true,
                    displayField:'groupname',
                    fieldLabel : 'Select a Group*',
                    allowBlank:false,
                    valueField:'groupid',
                    hiddenName  : 'groupid',
                    emptyText:'Select a Group'
                });

         this.comboTypeStore=new Wtf.data.SimpleStore({
            fields :['abbr', 'type'],
            data:[['0','No Permission'],['2','View/Edit Permission']]
//            data:[['0','No Permission'],['1','View Permission'],['2','Edit Permission']]
        });
        this.rolegrComboItem.on("select",this.groupComboValueChange,this);
    },

    onRender: function(config) {
        Wtf.MainAuthPanel.superclass.onRender.call(this, config);
        this.rolegrStore.load();
    },

    afterComboLoad : function () {
        if(this.rolegrStore.getCount() > 0)
            this.rolegrComboItem.setValue(this.rolegrStore.getAt(0).data['groupid']);

        Wtf.Ajax.requestEx({
            url: Wtf.req.accessR+"accessRight.do",
            method: 'POST',
            params: {
                action: 21,
                groupid :this.rolegrComboItem.getValue(),
                start:0,
                limit:15
            }},this,
            function(response, e){
                this.addContentInPanel(response);
//                this.addContentInPanel(eval('(' + response.trim() + ')'));
        });
//                        this.addContentInPanel();
    },

    ajaxRequestAfterGrChange : function () {
        Wtf.Ajax.requestEx({
            url: Wtf.req.accessR+"accessRight.do",
            method: 'POST',
            params: {
                action: 21,
                groupid :this.rolegrComboItem.getValue(),
                start:0,
                limit: 5
            }},this,
            function(response, e) {
                this.innerpanel.remove(this.GroupingReportGrid,true);
                if(this.GroupingReportGrid.el)
                    this.GroupingReportGrid.el.remove();
                this.makeReportGrid(response);
                this.innerpanel.add(this.GroupingReportGrid);
                this.innerpanel.doLayout();
                this.loadGridStore();
        });
    },

    loadGridStore : function () {
        this.simstore.load({params : {
            start : 0,
            limit : 5 
        }});
    },
    
    groupComboValueChange : function(field,oldval,newval) {
        this.ajaxRequestAfterGrChange();
    },
    
    makeReportGrid : function (obj) {
////        {"permgrid", "permid","access-rights-set","tasks"};
//        this.permRec = new Wtf.data.Record.create([
//            {name: 'permgrid', type:'string'},
//            {name: 'permid', type:'string'},
//            {name: 'access-rights-set', type:'string'},
//            {name: 'tasks', type:'string'}
//        ]);
//        this.dataReader = new Wtf.data.KwlJsonReader({
//            totalProperty: 'count',
//            root: "data"
//        },this.permRec);
//
//        this.simstore = new Wtf.data.Store({
//            reader: this.dataReader,
//            url: 'jspfiles/accessRight.jsp?action=0&mode=0&companyid='+this.companyid,//mode=0 is user
//            method : 'GET',
//            groupField: "access-rights-set",
//            sortInfo: {
//                field: "permid",
//                direction: "asc"
//            }
//        });
//        this.simstore.load();
       this.simstore = new Wtf.data.GroupingStore({
            reader: new Wtf.data.KwlJsonReader({ root: 'data',totalProperty: 'TotalCount'}, this.createFields(obj.columnheader)),
            url : Wtf.req.accessR+"accessRight.do",
//            data: obj,
            groupField: "access-rights-set",
            baseParams : {
                action: 0,
                groupid :this.rolegrComboItem.getValue(),
                taskid: this.taskid
            },
            sortInfo: {
                field: "permid",
                direction: "asc"
            }
        });
        
        this.quickPanelSearch = new Wtf.KWLTagSearch({
            width: 150,
            emptyText: 'Enter ',
            field: "access-rights-set"
        });
        
        this.pg = new Wtf.PagingToolbar({
            pageSize: 5,
            searchField: this.quickPanelSearch,
            store: this.simstore,
//            displayInfo: true
//            displayMsg: 'Displaying items {0} - {1} of {2}',
//            emptyMsg: "No items to display",
            plugins: this.pP = new Wtf.common.pPageSize({})
        });
        this.sm = new Wtf.grid.CheckboxSelectionModel();
        this.cm = this.createColModel(obj.columnheader);
        this.GroupingReportGrid = new Wtf.grid.EditorGridPanel({
            cm: this.cm,
            sm: this.sm,
            plugins: [Wtf.ux.grid.plugins.GroupCheckboxSelection],
            ds: this.simstore,
            autoScroll: true,
            collapsible: true,
            layout :"fit",
            clicksToEdit: 1,
            viewConfig: {forceFit: true},
            bbar: [this.pg
//                To Do - Need to uncomment when newly created roles and permissions can be used in the system.
//                ,{
//                text: 'Manage Access Rights Sets',
//                tooltip: {
//                    title: 'Access Rights Sets',
//                    text: 'Click to manage Access Rights Sets'
//                },
//                handler: function() {
//                    this.permGroupWin = new Wtf.Window({
//                        title:'Access Rights Sets',
//                        layout:'fit',
//                        iconCls: 'winicon',
//                        modal: true,
//                        height:400,
//                        width:550,
//                        scope: this,
//                        items:[this.poppanel = new Wtf.Panel({
//                            id: 'permgrppanel' + this.id,
//                            layout: 'fit',
//                            cls: 'backcolor',
//                            border: false,
//                            tbar: ['-','New Access Rights Set: ', this.permGrpText = new Wtf.form.TextField({
//                                    fieldLabel: 'New Access Rights Set',
//                                    anchor: '95%',
//                                    maxLength: 100
//                            }),'-',{
//                                text: 'Add',
//                                tooltip: {
//                                    title: 'Add',
//                                    text: 'Click to add Access Rights Set'
//                                },
//                                handler: function() {this.addPermGroup();},
//                                scope: this
//                            },'-'],
//                            items: [this.addPermGrp = new Wtf.Panel({
//                                layout: 'fit',
//                                border: false,
//                                items: [this.permGrpGrid = new Wtf.grid.EditorGridPanel({
//                                    store: this.permGrpds,
//                                    cm: this.permGrpcm,
//                                    border: false,
//                                    clicksToEdit: 1,
//                                    viewConfig: {
//                                        forceFit: true
//                                    }
//                                })]
//                            })]
//                        })]
//                    })
//                    this.permGroupWin.show();
//                    this.permGroupWin.on('close', function(){
//                        this.ajaxRequestAfterGrChange();
//                    }, this);
//                    this.permGrpGrid.on('afteredit', this.permGrpAfterEdit, this);
//
//                },
//                scope: this
//            },{
//                text: 'Manage Tasks',
//                tooltip: {
//                    title: 'Task',
//                    text: 'Click to manage task list'
//                },
//                handler: function() {
//                    this.permcm = new Wtf.grid.ColumnModel([
//                        new Wtf.grid.RowNumberer(),
//                        {
//                            dataIndex: 'permid',
//                            hidden: true
//                        },{
//                            header: "Task",
//                            dataIndex: 'tasks',
//                            editor: new Wtf.form.TextField({allowBlank: false, maxLength: 200})
//                    }]);
//                    this.permds.removeAll();
//                    this.permWin = new Wtf.Window({
//                        title:'Tasks',
//                        layout:'fit',
//                        iconCls: 'winicon',
//                        modal: true,
//                        height:400,
//                        width:550,
//                        scope: this,
//                        items:[this.poppanel = new Wtf.Panel({
//                            layout: 'fit',
//                            cls: 'backcolor',
//                            border: false,
//                            tbar: ['-','Access Rights Set: ', this.permGrpCombo = new Wtf.form.ComboBox({
//                                valueField: 'permgrid',
//                                displayField: 'access-rights-set',
//                                store: this.permGrpds,
//                                editable: false,
//                                typeAhead: true,
//                                mode: 'local',
//                                triggerAction: 'all',
//                                selectOnFocus: true,
//                                emptyText: 'Select Access Rights Set'
//                            }),'-','New Task: ', this.permText = new Wtf.form.TextField({
//                                maxLength: 100
//                            }),'-',{
//                                text: 'Add',
//                                tooltip: {
//                                    title: 'Add',
//                                    text: 'Click to add task in this set'
//                                },
//                                handler: function() {this.addPermissions();},
//                                scope: this
//                            },'-'],
//                            items: [this.addPerm = new Wtf.Panel({
//                                layout: 'fit',
//                                border: false,
//                                items: [this.permGrpGrid = new Wtf.grid.EditorGridPanel({
//                                    store: this.permds,
//                                    cm: this.permcm,
//                                    border: false,
//                                    clicksToEdit: 1,
//                                    viewConfig: {
//                                        forceFit: true
//                                    }
//                                })]
//                            })]
//                        })]
//                    })
//                    this.permWin.show();
//                    this.permWin.on('close', function(){
//                        this.ajaxRequestAfterGrChange();
//                    }, this);
//                    this.permGrpGrid.on('afteredit', this.permAfterEdit, this);
////                    this.permds.load();
//                    this.permGrpCombo.on('select', function(cmb, rec, index) {
//                        this.permds.baseParams = {
////                            action: 11,
//                            permid: rec.data.permgrid
//                        }
//                        this.permds.load();
//                    }, this);
//                },
//                scope: this
//            },{
//                text: 'Manage User Profile Groups',
//                tooltip: {
//                    title: 'User Profile Group',
//                    text: 'Click to manage user profile groups'
//                },
//                handler: function() {
//                    this.roleGrpcm = new Wtf.grid.ColumnModel([
//                    {
//                        dataIndex: 'groupid',
//                        hidden: true
//                    },{
//                        header: "User Profile Group",
//                        dataIndex: 'groupname',
//                        editor: new Wtf.form.TextField({allowBlank: false, maxLength: 100})
//                    },{
//                        header: "Description",
//                        dataIndex: 'description',
//                        editor: new Wtf.form.TextField({allowBlank: false, maxLength: 100})
//                    },{
//                        header: "Copy",
//                        dataIndex: 'copy',
//                        width: 50,
//                        align: 'center',
//                        renderer: function(a, b, c, d, e, f){
//                             return "<img src='images/Copy.gif' style='cursor: pointer;' onclick='copyUPG(\""+ c.get("groupid") +"\", \""+ c.get("groupname") +"\", \""+ c.get("description") +"\")'  title='Click to copy'>";
////                            return "<a href='#' title='Copy' onclick='copyUPG(\""+ c.get("groupid") +"\", \""+ c.get("groupname") +"\", \""+ c.get("description") +"\")'><div class='copyIcon' style='height:16px; width:20px;margin-left: auto;margin-right: auto;'></div></a>";
//                        }
//                    },{
//                        header: "Delete",
//                        dataIndex: 'del',
//                        width: 50,
//                        align: 'center',
//                        renderer: function(a, b, c, d, e, f){
//                            return "<img src='images/Cancel.gif' style='cursor: pointer;' onclick='deleteUPG(\""+ c.get("groupid") +"\", \""+ this.id +"\")'  title='Click to delete'>";
////                            return "<a href='#' title='Delete' onclick='deleteUPG(\""+ c.get("groupid") +"\", \""+ this.id +"\")'><div class='pwnd deliconwt' style='height:16px; width:16px;margin-left: auto;margin-right: auto;'></div></a>";
//                        }
//                    }]);
//                    this.roleGroupWin = new Wtf.Window({
//                        title:'User Profile Group',
//                        layout:'fit',
//                        iconCls: 'winicon',
//                        modal: true,
//                        height:500,
//                        width:610,
//                        minWidth: 610,
//                        minHeight: 450,
//                        scope: this,
//                        items: [
//                            this.pPanel = new Wtf.Panel({
//                                layout: 'fit',
//                                border: false,
//                                items: this.inP = new Wtf.Panel({
//                                    layout: 'border',
//                                    border: false,
//                                    items: [{
//                                            region: 'north',
//                                            border: false,
//                                            height: 90,
//                                            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
//                                            html : getHeader('images/createuser.gif','User Profile Group','Create a new group by entering a name and description and clicking the Add button. Edit existing groups by clicking a cell.')
//                                        },{
//                                            region: 'center',
//                                            layout: 'border',
//                                            border: false,
//                                            items: [
//                                                this.top = new Wtf.form.FormPanel({
//
//                                                    region: "north",
//                                                    bodyStyle: "padding: 10px;",
//                                                    border: false,
//                                                    labelWidth: 160,
//                                                    height: 150,
//                                                    buttonAlign: 'right',
//                                                    buttons: [{
//                                                        text: 'Add',
//                                                        tooltip: {
//                                                            title: 'Add',
//                                                            text: 'Click to add User Profile Group'
//                                                        },
//                                                        handler: function() {this.addRoleGroup();},
//                                                        scope: this
//                                                    }],
//                                                    items: [this.roleGrpText = new Wtf.form.TextField({
//                                                            fieldLabel: 'New User Profile Group*',
//                                                            anchor: '95%',
//                                                            maxLength: 100,
//                                                            id: this.id + 'rolegrpText'
//                                                    }), this.roleGrpDesc = new Wtf.form.TextArea({
//                                                        fieldLabel: 'Description',
//                                                        maxValue: 200,
//                                                        anchor: '95%'
//
//                                                    })]
//                                                }),
//                                                this.roleGrpGrid = new Wtf.grid.EditorGridPanel({
//                                                    region: 'center',
//                                                    height: 200,
//                                                    store: this.rolegrStore,
//                                                    cm: this.roleGrpcm,
//                                                    border: false,
//                                                    clicksToEdit: 1,
//                                                    viewConfig: {
//                                                        forceFit: true
//                                                    }
//                                                })
//                                            ]
//                                        }
//
//                                    ]
//                                })
//                            })
//                        ]
//                    })
//                    this.roleGroupWin.show();
//                    this.roleGroupWin.on('close', function(){
//                        this.roleGrpGrid.destroy();
//                        this.roleGroupWin.remove(this.roleGrpGrid, true);
//                        this.roleGroupWin.destroy();
//
//                        this.ajaxRequestAfterGrChange();
//                    }, this);
//                    this.roleGrpGrid.on('afteredit', this.roleGrpAfterEdit, this);
//                },
//                scope: this
//            },{
//                text: 'Manage Roles',
//                tooltip: {
//                    title: 'Role',
//                    text: 'Click to manage Roles'
//                },
//                handler: function() {
//                    this.roleds.removeAll();
//                    this.rolecm = new Wtf.grid.ColumnModel([
//                            new Wtf.grid.RowNumberer(),
//                            {
//                                dataIndex: 'roleid',
//                                hidden: true
//                            },{
//                                header: "Role",
//                                dataIndex: 'rolename',
//                                editor: new Wtf.form.TextField({allowBlank: false, maxLength: 100})
//                        }]);
//                    this.roleWin = new Wtf.Window({
//                        title:'Roles',
//                        layout:'fit',
//                        modal: true,
//                        iconCls: 'winicon',
//                        height:400,
//                        width:550,
//                        scope: this,
//                        closeAction: 'hide',
//                        items:[this.poppanel = new Wtf.Panel({
//                            layout: 'fit',
//                            cls: 'backcolor',
//                            border: false,
//                            tbar: ['-','User Profile Group: ', this.roleGrpCombo = new Wtf.form.ComboBox({
//                                valueField: 'groupid',
//                                displayField: 'groupname',
//                                store: this.rolegrStore,
//                                editable: false,
//                                typeAhead: true,
//                                mode: 'local',
//                                triggerAction: 'all',
//                                selectOnFocus: true,
//								emptyText: 'Select User Profile Group'
//                            }),'-','New Role: ', this.roleText = new Wtf.form.TextField({
//                                maxLength: 100
//                            }),'-',{
//                                text: 'Add',
//                                tooltip: {
//                                    title: 'Add',
//                                    text: 'Click to add new Role'
//                                },
//                                handler: function() {this.addRoles();},
//                                scope: this
//                            },'-'],
//                            items: [this.addRole = new Wtf.Panel({
//                                layout: 'fit',
//                                border: false,
//                                items: [this.roleGrid = new Wtf.grid.EditorGridPanel({
//                                    store: this.roleds,
//                                    cm: this.rolecm,
//                                    border: false,
//                                    clicksToEdit: 1,
//                                    viewConfig: {
//                                        forceFit: true
//                                    }
//                                })]
//                            })]
//                        })]
//                    })
//                    this.roleWin.show();
//                    this.roleWin.on('close', function(){
//                        this.ajaxRequestAfterGrChange();
//                    }, this);
//                    this.roleGrid.on('afteredit', this.roleAfterEdit, this);
//                    this.roleGrpCombo.on('select', function(cmb, rec, index) {
//                        this.roleds.baseParams = {
//                            action: 9,
//                            grpid: rec.data.groupid
//                        }
//                        this.roleds.load();
//                    }, this);
//                },
//                scope: this
//            }
            ],
            view: new Wtf.grid.GroupingView({
                autoFill:true,
                groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
            })
        });
        
        this.simstore.on('load', function(store) {
            this.quickPanelSearch.StorageChanged(store);
        }, this);
        this.simstore.on('datachanged', function() {
            var p = this.pP.combo.value;
            this.quickPanelSearch.setPage(p);
        }, this);

    },
    addContentInPanel : function(obj){
        this.makeReportGrid(obj);
        
         this.permGrpcm = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            {
                dataIndex: 'permgrid',
                hidden: true
            },{
                header: "Access Rights Set",
                dataIndex: 'access-rights-set',
                editor: new Wtf.form.TextField({allowBlank: false, maxLength: 100})
        }]);

        this.permGrpReader = new Wtf.data.Record.create([
            {name: 'permgrid'},
            {name: 'access-rights-set'}
        ]);

        this.permGrpds = new Wtf.data.Store({
            url: Wtf.req.accessR+"accessRight.do",
            baseParams: {
                action: 10,
                taskid: this.taskid
            },
            reader: new Wtf.data.KwlJsonReader({
                        root:"data"}, this.permGrpReader)
        });
        this.permGrpds.load();

        this.permReader = new Wtf.data.Record.create([
            {name: 'permid'},
            {name: 'tasks'}
        ]);

        this.permds = new Wtf.data.Store({
            url: Wtf.req.accessR+"accessRight.do?action=11",
            reader: new Wtf.data.KwlJsonReader({
                        root:"data"}, this.permReader)
        });

        this.roleReader = new Wtf.data.Record.create([
            {name: 'roleid'},
            {name: 'rolename'}
        ]);

        this.roleds = new Wtf.data.Store({
            url: Wtf.req.accessR+"accessRight.do",
            reader: new Wtf.data.KwlJsonReader({
                    root:"data"}, this.roleReader)
        });

        this.innerpanel = new Wtf.Panel({
            border: false,
            layout :'fit',
            scope: this,
            items:[this.GroupingReportGrid],
            tbar:[/*'Quick Search: ', this.quickPanelSearch,'-',*/"User Profile Group"+":",this.rolegrComboItem,{
                    iconCls: "mbrefresh",
                    handler: function(){
                        this.ajaxRequestAfterGrChange()
                        /* Wtf.Ajax.request({
                            url: 'jspfiles/accessRight.jsp',
                            method: 'POST',
                            params: {
                                action: 0,
                                groupid :this.rolegrComboItem.getValue()
                            },
                            success: function(response, e){
                                this.GroupingReportGrid.destroy();
                                this.GroupingReportGrid.el.remove();
                                this.makeReportGrid(eval('(' + response.responseText.trim() + ')'));
                                this.innerpanel.add(this.GroupingReportGrid);
                                this.innerpanel.doLayout();
                            },
                            scope: this
                        })*/
                    },
                    scope: this
                },{ text: 'Save Changes ',iconCls:'mbsaveicon',tooltip:'Save permission changes',id:this.id+'UpdateEventClick',
                scope:this,handler:this.savePermissions,ctCls: 'fontstyle'
              }
            ]
        })
        this.add(this.innerpanel);
        this.innerpanel.doLayout();
        this.doLayout();
        this.loadGridStore();
    },
//     addContentInPanel : function(obj)   {
////        this.makeReportGrid(obj);
//        this.permGrpcm = new Wtf.grid.ColumnModel([
//            new Wtf.grid.RowNumberer(),
//            {
//                dataIndex: 'permgrid',
//                hidden: true
//            },{
//                header: "Access Rights Set",
//                dataIndex: 'access-rights-set',
//                editor: new Wtf.form.TextField({allowBlank: false, maxLength: 100})
//        }]);
//
//        this.permGrpReader = new Wtf.data.Record.create([
//            {name: 'permgrid'},
//            {name: 'access-rights-set'}
//        ]);
//
//        this.permGrpds = new Wtf.data.Store({
//            url: "jspfiles/admin/authorization.jsp?action=10",
//            reader: new Wtf.data.JsonReader({
//                        root:"data"}, this.permGrpReader)
//        });
//        this.permGrpds.load();
//
//        this.permcm = new Wtf.grid.ColumnModel([
//            new Wtf.grid.RowNumberer(),
//            {
//                dataIndex: 'permid',
//                hidden: true
//            },{
//                header: "Task",
//                dataIndex: 'tasks',
//                editor: new Wtf.form.TextField({allowBlank: false, maxLength: 200})
//        }]);
//
//        this.permReader = new Wtf.data.Record.create([
//            {name: 'permid'},
//            {name: 'tasks'}
//        ]);
//
//        this.permds = new Wtf.data.Store({
//            url: "jspfiles/admin/authorization.jsp",
//            reader: new Wtf.data.JsonReader({
//                        root:"data"}, this.permReader)
//        });
//
//
//        this.rolecm = new Wtf.grid.ColumnModel([
//            new Wtf.grid.RowNumberer(),
//            {
//                dataIndex: 'roleid',
//                hidden: true
//            },{
//                header: "Role",
//                dataIndex: 'rolename',
//                editor: new Wtf.form.TextField({allowBlank: false, maxLength: 100})
//        }]);
//
//        this.roleReader = new Wtf.data.Record.create([
//            {name: 'roleid'},
//            {name: 'rolename'}
//        ]);
//
//        this.roleds = new Wtf.data.Store({
//            url: "jspfiles/admin/authorization.jsp",
//            reader: new Wtf.data.JsonReader({
//                    root:"data"}, this.roleReader)
//        });
//
//        if (!this.innerpanel) {
//        this.innerpanel = new Wtf.Panel({
//            id : "InnAuthPanel" + this.tabid,
//            border: false,
//            layout :'fit',
//            scope: this,
//            tbar:["User Profile Group"+":",this.rolegrComboItem,{
//                    iconCls: "reportRefresh",
//                    handler: function(){
//                         Wtf.Ajax.request({
//                            url: 'jspfiles/admin/authorization.jsp',
//                            method: 'POST',
//                            params: {
//                                action: 0,
//                                groupid :this.rolegrComboItem.getValue()
//                            },
//                            success: function(response, e){
//                                this.GroupingReportGrid.destroy();
//                                this.GroupingReportGrid.el.remove();
//                                this.makeReportGrid(eval('(' + response.responseText.trim() + ')'));
//                                this.innerpanel.add(this.GroupingReportGrid);
//                                this.innerpanel.doLayout();
//                            },
//                            scope: this
//                        })
//                    },
//                    scope: this
//                },{ text: 'Save Changes ',iconCls:'pwnd saveicon caltb',tooltip:'Save permission changes',id:this.id+'UpdateEventClick',
//                scope:this,handler:this.savePermissions,ctCls: 'fontstyle'
//              }
//            ],
//            items:[new Wtf.Panle({
//                    layout:'fit'
//            })/*this.GroupingReportGrid*/],
//            bbar: [{
//                text: 'Manage Access Rights Sets',
//                tooltip: {
//                    title: 'Access Rights Sets',
//                    text: 'Click to manage Access Rights Sets'
//                },
//                handler: function() {
//                    this.permGroupWin = new Wtf.Window({
//                        id: 'permgrp' + this.id,
//                        title:'Access Rights Sets',
//                        layout:'fit',
//                        iconCls: 'winicon',
//                        modal: true,
//                        height:400,
//                        width:550,
//                        scope: this,
//                        items:[this.poppanel = new Wtf.Panel({
//                            id: 'permgrppanel' + this.id,
//                            layout: 'fit',
//                            cls: 'backcolor',
//                            border: false,
//                            tbar: ['-','New Access Rights Set: ', this.permGrpText = new Wtf.form.TextField({
//                                    fieldLabel: 'New Access Rights Set',
//                                    anchor: '95%',
//                                    maxLength: 100,
//                                    id: this.id + 'permgrpText'
//                            }),'-',{
//                                text: 'Add',
//                                tooltip: {
//                                    title: 'Add',
//                                    text: 'Click to add Access Rights Set'
//                                },
//                                handler: function() {this.addPermGroup();},
//                                scope: this
//                            },'-'],
//                            items: [this.addPermGrp = new Wtf.Panel({
//                                id: 'addPermgrp' + this.id,
//                                layout: 'fit',
//                                border: false,
//                                items: [this.permGrpGrid = new Wtf.grid.EditorGridPanel({
//                                    id: 'permgrpgrid' + this.id,
//                                    store: this.permGrpds,
//                                    cm: this.permGrpcm,
//                                    border: false,
//                                    clicksToEdit: 1,
//                                    viewConfig: {
//                                        forceFit: true
//                                    }
//                                })]
//                            })]
//                        })]
//                    })
//                    this.permGroupWin.show();
//                    this.permGroupWin.on('close', function(){
//                        this.ajaxRequestAfterGrChange();
//                    }, this);
//                    this.permGrpGrid.on('afteredit', this.permGrpAfterEdit, this);
//                },
//                scope: this
//            },{
//                text: 'Manage Tasks',
//                tooltip: {
//                    title: 'Task',
//                    text: 'Click to manage task list'
//                },
//                handler: function() {
//                    this.permds.removeAll();
//                    this.permWin = new Wtf.Window({
//                        id: 'perm' + this.id,
//                        title:'Tasks',
//                        layout:'fit',
//                        iconCls: 'winicon',
//                        modal: true,
//                        height:400,
//                        width:550,
//                        scope: this,
//                        items:[this.poppanel = new Wtf.Panel({
//                            id: 'permpanel' + this.id,
//                            layout: 'fit',
//                            cls: 'backcolor',
//                            border: false,
//                            tbar: ['-','Access Rights Set: ', this.permGrpCombo = new Wtf.form.ComboBox({
//                                valueField: 'permgrid',
//                                displayField: 'access-rights-set',
//                                id: this.id + 'permgrpcmb',
//                                store: this.permGrpds,
//                                editable: false,
//                                typeAhead: true,
//                                mode: 'local',
//                                triggerAction: 'all',
//                                selectOnFocus: true,
//								emptyText: 'Select Access Rights Set'
//                            }),'-','New Task: ', this.permText = new Wtf.form.TextField({
//                                maxLength: 100,
//                                id: this.id + 'permText'
//                            }),'-',{
//                                text: 'Add',
//                                tooltip: {
//                                    title: 'Add',
//                                    text: 'Click to add task in this set'
//                                },
//                                handler: function() {this.addPermissions();},
//                                scope: this
//                            },'-'],
//                            items: [this.addPerm = new Wtf.Panel({
//                                id: 'addPerm' + this.id,
//                                layout: 'fit',
//                                border: false,
//                                items: [this.permGrpGrid = new Wtf.grid.EditorGridPanel({
//                                    id: 'permgrid' + this.id,
//                                    store: this.permds,
//                                    cm: this.permcm,
//                                    border: false,
//                                    clicksToEdit: 1,
//                                    viewConfig: {
//                                        forceFit: true
//                                    }
//                                })]
//                            })]
//                        })]
//                    })
//                    this.permWin.show();
//                    this.permWin.on('close', function(){
//                        this.ajaxRequestAfterGrChange();
//                    }, this);
//                    this.permGrpGrid.on('afteredit', this.permAfterEdit, this);
//                    this.permGrpCombo.on('select', function(cmb, rec, index) {
//                        this.permds.baseParams = {
//                            action: 11,
//                            permid: rec.data.permgrid
//                        }
//                        this.permds.load();
//                    }, this);
//                },
//                scope: this
//            },{
//                text: 'Manage User Profile Groups',
//                tooltip: {
//                    title: 'User Profile Group',
//                    text: 'Click to manage user profile groups'
//                },
//                handler: function() {
//                    this.roleGrpcm = new Wtf.grid.ColumnModel([
//                    {
//                        dataIndex: 'groupid',
//                        hidden: true
//                    },{
//                        header: "User Profile Group",
//                        dataIndex: 'groupname',
//                        editor: new Wtf.form.TextField({allowBlank: false, maxLength: 100})
//                    },{
//                        header: "Description",
//                        dataIndex: 'description',
//                        editor: new Wtf.form.TextField({allowBlank: false, maxLength: 100})
//                    },{
//                        header: "Copy",
//                        dataIndex: 'copy',
//                        width: 50,
//                        align: 'center',
//                        renderer: function(a, b, c, d, e, f){
//                            return "<a href='#' title='Copy' onclick='copyUPG(\""+ c.get("groupid") +"\", \""+ c.get("groupname") +"\", \""+ c.get("description") +"\")'><div class='copyIcon' style='height:16px; width:20px;margin-left: auto;margin-right: auto;'></div></a>";
//                        }
//                    },{
//                        header: "Delete",
//                        dataIndex: 'del',
//                        width: 50,
//                        align: 'center',
//                        renderer: function(a, b, c, d, e, f){
//                            return "<a href='#' title='Delete' onclick='deleteUPG(\""+ c.get("groupid") +"\", \""+ this.id +"\")'><div class='pwnd deliconwt' style='height:16px; width:16px;margin-left: auto;margin-right: auto;'></div></a>";
//                        }
//                    }]);
//                    this.roleGroupWin = new Wtf.Window({
//                        id: 'rolegrp' + this.id,
//                        title:'User Profile Group',
//                        layout:'fit',
//                        iconCls: 'winicon',
//                        modal: true,
//                        height:500,
//                        width:610,
//                        minWidth: 610,
//                        minHeight: 450,
//                        scope: this,
//                        items: [
//                            this.pPanel = new Wtf.Panel({
//                                layout: 'fit',
//                                border: false,
//                                items: this.inP = new Wtf.Panel({
//                                    layout: 'border',
//                                    border: false,
//                                    items: [{
//                                            region: 'north',
//                                            border: false,
//                                            height: 90,
//                                            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
//                                            html : getHeader('images/createuser.gif','User Profile Group','Create a new group by entering a name and description and clicking the Add button. Edit existing groups by clicking a cell.')
//                                        },{
//                                            region: 'center',
//                                            layout: 'border',
//                                            border: false,
//                                            items: [
//                                                this.top = new Wtf.form.FormPanel({
//
//                                                    region: "north",
//                                                    bodyStyle: "padding: 10px;",
//                                                    border: false,
//                                                    labelWidth: 160,
//                                                    height: 150,
//                                                    buttonAlign: 'right',
//                                                    buttons: [{
//                                                        text: 'Add',
//                                                        tooltip: {
//                                                            title: 'Add',
//                                                            text: 'Click to add User Profile Group'
//                                                        },
//                                                        handler: function() {this.addRoleGroup();},
//                                                        scope: this
//                                                    }],
//                                                    items: [this.roleGrpText = new Wtf.form.TextField({
//                                                            fieldLabel: 'New User Profile Group*',
//                                                            anchor: '95%',
//                                                            maxLength: 100,
//                                                            id: this.id + 'rolegrpText'
//                                                    }), this.roleGrpDesc = new Wtf.form.TextArea({
//                                                        fieldLabel: 'Description',
//                                                        maxValue: 200,
//                                                        anchor: '95%'
//
//                                                    })]
//                                                }),
//                                                this.roleGrpGrid = new Wtf.grid.EditorGridPanel({
//                                                    id: 'rolegrpgrid',
//                                                    region: 'center',
//                                                    height: 200,
//                                                    store: this.rolegrStore,
//                                                    cm: this.roleGrpcm,
//                                                    border: false,
//                                                    clicksToEdit: 1,
//                                                    viewConfig: {
//                                                        forceFit: true
//                                                    }
//                                                })
//                                            ]
//                                        }
//
//                                    ]
//                                })
//                            })
//                        ]
//                    })
//                    this.roleGroupWin.show();
//                    this.roleGroupWin.on('close', function(){
//                        this.roleGrpGrid.destroy();
//                        this.roleGroupWin.remove(this.roleGrpGrid, true);
//                        this.roleGroupWin.destroy();
//
//                        this.ajaxRequestAfterGrChange();
//                    }, this);
//                    this.roleGrpGrid.on('afteredit', this.roleGrpAfterEdit, this);
//                },
//                scope: this
//            },{
//                text: 'Manage Roles',
//                tooltip: {
//                    title: 'Role',
//                    text: 'Click to manage Roles'
//                },
//                handler: function() {
//                    this.roleds.removeAll();
//                    this.roleWin = new Wtf.Window({
//                        id: 'role' + this.id,
//                        title:'Roles',
//                        layout:'fit',
//                        modal: true,
//                        iconCls: 'winicon',
//                        height:400,
//                        width:550,
//                        scope: this,
//                        closeAction: 'hide',
//                        items:[this.poppanel = new Wtf.Panel({
//                            id: 'rolepanel' + this.id,
//                            layout: 'fit',
//                            cls: 'backcolor',
//                            border: false,
//                            tbar: ['-','User Profile Group: ', this.roleGrpCombo = new Wtf.form.ComboBox({
//                                valueField: 'groupid',
//                                displayField: 'groupname',
//                                id: this.id + 'rolegrpcmb',
//                                store: this.rolegrStore,
//                                editable: false,
//                                typeAhead: true,
//                                mode: 'local',
//                                triggerAction: 'all',
//                                selectOnFocus: true,
//								emptyText: 'Select User Profile Group'
//                            }),'-','New Role: ', this.roleText = new Wtf.form.TextField({
//                                maxLength: 100,
//                                id: this.id + 'roleText'
//                            }),'-',{
//                                text: 'Add',
//                                tooltip: {
//                                    title: 'Add',
//                                    text: 'Click to add new Role'
//                                },
//                                handler: function() {this.addRoles();},
//                                scope: this
//                            },'-'],
//                            items: [this.addRole = new Wtf.Panel({
//                                id: 'addRole' + this.id,
//                                layout: 'fit',
//                                border: false,
//                                items: [this.roleGrid = new Wtf.grid.EditorGridPanel({
//                                    id: 'rolegrid' + this.id,
//                                    store: this.roleds,
//                                    cm: this.rolecm,
//                                    border: false,
//                                    clicksToEdit: 1,
//                                    viewConfig: {
//                                        forceFit: true
//                                    }
//                                })]
//                            })]
//                        })]
//                    })
//                    this.roleWin.show();
//                    this.roleWin.on('close', function(){
//                        this.ajaxRequestAfterGrChange();
//                    }, this);
//                    this.roleGrid.on('afteredit', this.roleAfterEdit, this);
//                    this.roleGrpCombo.on('select', function(cmb, rec, index) {
//                        this.roleds.baseParams = {
//                            action: 9,
//                            grpid: rec.data.groupid
//                        }
//                        this.roleds.load();
//                    }, this);
//                },
//                scope: this
//            }]
//        });
//        this.add(this.innerpanel);
//        this.innerpanel.doLayout();
//        this.doLayout();
//		};
//    },

    permGrpAfterEdit: function(e) {
        Wtf.Ajax.requestEx({
            url: Wtf.req.accessR+"accessRight.do",
            method: 'POST',
            params: {
                action: 14,
                groupid: e.record.data.permgrid,
                groupname: e.value
            }},this,
            function(response, e){
        });
    },

    permAfterEdit: function(e) {
        Wtf.Ajax.requestEx({
            url: Wtf.req.accessR+"accessRight.do",
            method: 'POST',
            params: {
                action: 15,
                permid: e.record.data.permid,
                permname: e.value/*,
                groupid: this.permGrpCombo.getValue()*/
            }},this,
            function(response, e){
        })
    },

    roleGrpAfterEdit: function(e) {
          Wtf.Ajax.requestEx({
                url: Wtf.req.accessR+"accessRight.do",
                method: 'POST',
                params: {
                    action: 4,
                    groupid: e.record.data.groupid,
                    data: e.value,
                    column: e.field
                }},this,
                function(response, e){
            })
    },

    roleAfterEdit: function(e) {
        Wtf.Ajax.requestEx({
            url: Wtf.req.accessR+"accessRight.do",
            method: 'POST',
            params: {
                action: 8,
                roleid: e.record.data.roleid,
                rolename: e.value,
                roledesc: '',
                groupid: this.roleGrpCombo.getValue()
            }},this,
            function(response, e){
        });
    },

    addPermGroup: function() {
        if(this.permGrpText.getValue()) {
            Wtf.Ajax.requestEx({
                url: Wtf.req.accessR+"accessRight.do",
                method: 'POST',
                params: {
                    action: 12,
                    groupname: this.permGrpText.getValue()
                }},this,
                function(response, e){
                    this.permGrpds.reload();
            });
            this.permGrpText.setValue("");
        } else {
            msgBoxShow(["Add Access Rights Set", "Please enter some text"]);
        }
    },

    addPermissions: function() {
//        if(this.permGrpCombo.getValue()) {
            if(this.permText.getValue()) {
                Wtf.Ajax.requestEx({
                    url: Wtf.req.accessR+"accessRight.do",
                    method: 'POST',
                    params: {
                        action: 13,
                        permname: ScriptStripper(HTMLStripper(this.permText.getValue())),
                        groupid: this.permGrpCombo.getValue()
                    }},this,
                    function(response, e){
                        this.permds.reload();
                });
                this.permText.setValue("");
            } else {
                msgBoxShow(["Add Tasks", "Please enter some text"]);
            }
//        } else {
//            msgBoxShow(["Add Tasks", "Select an Access Rights Set"]);
//        }
    },

    addRoleGroup: function() {
        if(this.roleGrpText.getValue()) {
            Wtf.Ajax.requestEx({
                url: Wtf.req.accessR+"accessRight.do",
                method: 'POST',
                params: {
                    action: 2,
                    groupname: ScriptStripper(HTMLStripper(this.roleGrpText.getValue())),
                    groupdesc: ScriptStripper(HTMLStripper(this.roleGrpDesc.getValue()))
                }},this,
                function(response, e){
                    this.rolegrStore.reload();
            });
            this.roleGrpText.setValue("");
            this.roleGrpDesc.setValue("");
        } else {
            msgBoxShow(["Add User Profile Groups", "Please enter a profile group name"]);
        }
    },

    addRoles: function() {
        if(this.roleGrpCombo.getValue()) {
            if(this.roleText.getValue()) {
                Wtf.Ajax.requestEx({
                    url: Wtf.req.accessR+"accessRight.do",
                    method: 'POST',
                    params: {
                        action: 6,
                        rolename: ScriptStripper(HTMLStripper(this.roleText.getValue())),
                        roledesc: '',
                        groupid: this.roleGrpCombo.getValue()
                    }},this,
                    function(response, e){
                        this.roleds.reload();
                });
                this.roleText.setValue("");
            } else {
                msgBoxShow(["Add Roles", "Please enter some text"]);
            }
        } else {
            msgBoxShow(["Add Roles", "Select a User Profile Group"]);
        }
    },

    createFields : function(columnheader) {
        var fields = [];
        for(var fieldcnt = 0; fieldcnt < columnheader.length; fieldcnt++) {
            var fObj = {};
            fObj['name'] = columnheader[fieldcnt][0];
            fObj['type'] = 'string';
            fObj['mapping'] = columnheader[fieldcnt][0];
            fields[fields.length] = fObj;
        }
        return (new Wtf.data.Record.create(fields));
//        return(new Wtf.data.ArrayReader({},fields));
    },

    createColModel: function(columnHeader){
        
        var colConfig = [];
        //        {"permgrid", "permid","access-rights-set","tasks"};
        colConfig.push(this.sm);
        colConfig.push(new Wtf.grid.RowNumberer());
        for(var columncnt =0; columncnt<columnHeader.length ; columncnt++) {
            var colObj = {};
            colObj['header'] = columnHeader[columncnt][0];
            if(colObj['header']=="permid" || colObj['header']=="permgrid" || colObj['header']=="access-rights-set"){
                colObj['hidden'] = true;
                colObj['id'] = Math.random();
            }else {
                colObj['hidden'] = false;
                if(colObj['header']!= "permname") {
                    this.comboType = new Wtf.form.ComboBox({
                        id: 'cmb' + this.id + colObj['header'],
                        triggerAction: 'all',
                        store:this.comboTypeStore,
                        mode:'local',
                        displayField:'type',
                        fieldLabel : 'Select Permission Mode*',
                        valueField:'abbr',
                        hiddenName :'typem',
                        emptyText:'Select type',
                        allowBlank:false,
                        editable :false
                    });
                    colObj['editor'] = this.comboType;
                    colObj['renderer'] = Wtf.ux.comboBoxRenderer(this.comboType);
                    colObj['id'] = columnHeader[columncnt][1];
                }else{
                    colObj['id'] = Math.random();
                }
            }
            colObj['dataIndex'] = columnHeader[columncnt][0];
            colObj['width'] = 70;
            colObj['sortable'] = false;
            colConfig.push(colObj);
        }
        var reportcm = new Wtf.grid.ColumnModel(colConfig);
        return reportcm;
    },

    savePermissions : function() {
        var localJData ="[";
        var localRoleidJData ="["
        var record;
        var dataIndex;
        var value;
        var Vsum = 0;   //read only
        var Asum = 0;   // write only
        for(var col=6;col<this.cm.getColumnCount();col++) {
             var oldGrEntry = [];
             var localroleID = this.cm.getColumnId(col);
//             record = this.simstore.getAt(0);
             dataIndex = this.cm.getDataIndex(col);

             var cnt =0;
//             var permgroup = record.data['permgrid'];
             var gridStore = this.GroupingReportGrid.store;
             var selectedRecords = this.GroupingReportGrid.getSelectionModel().getSelections();
             while(cnt < selectedRecords.length) {
                 recordData = selectedRecords[cnt].data;
                 if(oldGrEntry.join().indexOf(recordData.permgrid)==-1) {
                     oldGrEntry.push(recordData.permgrid);
                     
//                     this.dstore.query("parent", rec.data["taskid"]).items;
                    var grIdRecords = gridStore.queryBy(function(record) {
                        var perm = record.get("permgrid");
                        if(perm == recordData.permgrid)
                            return true;
                        return false;
                    },this);
                    
                    Vsum = 0;Asum = 0;
                    for(var cntRec= 0; cntRec < grIdRecords.length; cntRec++) {
                        value = grIdRecords.items[cntRec].data[dataIndex];
                        if(value == 1)
                            Vsum +=this.TwoPowerN(grIdRecords.items[cntRec].data['permid']);
                        if(value == 2)
                            Asum += this.TwoPowerN(grIdRecords.items[cntRec].data['permid']);
                    }
                    
                    localJData += "{permgrid:\""+recordData.permgrid+"\",roleid:\""+localroleID+"\",permvalview:\""+Vsum+"\",pervaledit:\""+Asum+"\"},";
                     
                 }
                 cnt++;
             }
             
//             var prevRecord  =  this.simstore.getAt(cnt-1);
//             localJData += "{permgrid:\""+prevRecord.data["permgrid"]+"\",roleid:\""+localroleID+"\",permvalview:\""+Vsum+"\",pervaledit:\""+Asum+"\"},";
             
             Vsum = 0;Asum = 0;
        }
        if(localJData.length>1)
            localJData = localJData.substr(0,localJData.length -1)+"]";
        else
            localJData += "]";

        Wtf.Ajax.requestEx({
            url: Wtf.req.accessR+"accessRight.do",
            method: 'POST',
            params: {
                action: 22,//"addpermval",
                data :localJData
            }},this,
            function(response, e) { 
                var obj = response;//eval('('+response+')');
                if(obj.success) {
                    msgBoxShow(43,2);
                    SetModulePermissions(loginid);
                    Wtf.getCmp("quickLinks_portlet").callRequest();
                }
        });
    },

    TwoPowerN: function(n){
        return Math.pow(2, n);
    }
});


