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
Wtf.allModulesHome = function(config){
    Wtf.apply(this, config);
    Wtf.allModulesHome.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.allModulesHome, Wtf.Panel, {
    initComponent: function() {
        Wtf.allModulesHome.superclass.initComponent.call(this);
        this.modulesm = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:true
        });
        this.ModCenter = Wtf.data.Record.create([
            {name: 'moduleid', type: 'string'},
            {name: 'modulename',type: 'string'},
            {name: 'tablename',type: 'string'},
            {name: 'formid'},
            {name: 'displayconf'},
            {name: 'reportkey'},
            {name:'icon'},
            {name: 'dateval', type:'date', dateFormat: 'Y-m-j H:i:s.0'},
            {name:'displayInd'},
            {name:'abstractInd'}]);

        this.dsModCenter = new Wtf.data.Store({
            baseParams: {
                action: 4
            },
            url: Wtf.req.mbuild+'form.do',
            remoteSort:true,
            reader: new Wtf.data.KwlJsonReader({
                root:"data",
                totalProperty: 'TotalCount',
                remoteGroup:true,
                remoteSort: true
            }, this.ModCenter)
        });

        this.cmModCenter = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),this.modulesm,
            {
                header: "Module Name",
                dataIndex: 'modulename'
            //                renderer: function(val) {
            //                      return "<a href = '#' class='openModule' > " + val + "</a>";
            //                }
            },{
                header: "Created Date",
                dataIndex: 'dateval',
                //                sortable: false,
                renderer: function(val) {
                    if(Wtf.isEmpty(val)) {
                        return val;
                    } else {
                        return val//.format(Wtf.getDateFormat());               TODO - by vishnu
                    }
                }
            },{
                header:"Icon",
                dataIndex: 'icon',
                width:30,
                renderer:function(val,a,b){
                    return "<img src='images/store/?recordid="+b.data.moduleid+"&size=16&forcereload="+Math.floor(Math.random()*11)+"' style=\"margin-left:5px;vertical-align:middle;height : 13px\"></img>"
                }

            },{
                header:"Display Configuration",
                dataIndex: 'displayconf',
                width:30,
                renderer: function(val) {
                    return val==1 ? "Window" : "Tab";
                }
            },
            {
                header:"Deployed?",
                dataIndex: 'displayInd',
                width:20,
                renderer: function(val) {
                    return val==true ? "Yes" : "No";
                }
            },
            {
                header:"Abstract?",
                dataIndex: 'abstractInd',
                width:20,
                renderer: function(val) {
                    return val==true ? "Yes" : "No";
                }
            },
            {
                header: "Delete",
                width: 65,
                renderer: function(val) {
                    return "<img id='DeleteImg' class='delete' src='images/Cancel.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete module'></img>";
                }
            }]);
        this.cmModCenter.defaultSortable = true;

        this.quickPanelSearch = new Wtf.KWLTagSearch({
            width: 150,
            emptyText: 'Enter module name',
            field: "modulename"
        });
        this.pg = new Wtf.PagingSearchToolbar({
            pageSize: 15,
            searchField: this.quickPanelSearch,
            store: this.dsModCenter,
            displayInfo: true,
            displayMsg: 'Displaying items {0} - {1} of {2}',
            emptyMsg: "No items to display",
            plugins: this.pP = new Wtf.common.pPageSize({})
        });

        this.formsBttn=new Wtf.Toolbar.Button({
            text : "Form Builder",
            id:"cfiltetr",
            iconCls:"pwnd formbuilder",
            scope : this,
            disabled: true,
            handler : function() {
            var formid = this.modulesm.getSelected().get("formid");
                var moduleid = this.modulesm.getSelected().get("moduleid");
                var reportkey = this.modulesm.getSelected().get('reportkey');
                if(!Wtf.getCmp("formBuilder"+moduleid)){
                    mainPanel.loadMask.msg = "Rendering Components ...";
                    mainPanel.loadMask.show();
                    var studRscPanel = new  Wtf.formBuilderMain({
                        id: "formBuilder"+moduleid,
                        border: false,
                        moduleid: moduleid,
                        formid : formid,
                        reportkey:reportkey,
                        layout : "fit",
                        modulename : this.modulesm.getSelected().get("modulename"),
                        title: this.modulesm.getSelected().get("modulename") + " - Form Builder",
                        closable: true
                    });
                    mainPanel.add(Wtf.getCmp("formBuilder"+moduleid));
                }
                mainPanel.setActiveTab(Wtf.getCmp("formBuilder"+moduleid));
                mainPanel.doLayout();
            }
        });

        this.confBttn=new Wtf.Toolbar.Button({
            text : "Configure Group Tab",
            scope : this,
            disabled: true,
            hidden:true,
            iconCls:"pwnd groupconfig",
            handler : function() {
                var moduleid = this.modulesm.getSelected().get("moduleid");
                var configTab = new Wtf.ConfigGrTab({
                    moduleid : moduleid,
                    basemode : 0
                });
                configTab.show();
            }
        });
        this.addConfigBttn=new Wtf.Toolbar.Button({
            text : "Standard Config",
            scope : this,
            iconCls:'pwnd configuration',
            disabled: true,
            hidden:true,
            handler : function() {
                var moduleid = this.modulesm.getSelected().get("moduleid");
                addConfig(moduleid);
            }
        });

        this.openModBttn=new Wtf.Toolbar.Button({
            text : "Open Module",
            scope : this,
            iconCls:'pwnd configuration',
            disabled: true,
            hidden:true,
            handler : function() {
                
                var rec = this.modulesm.getSelected();
                openModuleUITab(rec.get("moduleid"));
            }
        });

        this.accRightBttn=new Wtf.Toolbar.Button({
            text : "Access Rights",
            scope : this,
            iconCls:'pwnd configuration',
            disabled: true,
            handler : function() {
                var moduleid = this.modulesm.getSelected().get("moduleid");
                assignModulePermissions(moduleid);
            }
        });
        
        this.deployModBttn=new Wtf.Toolbar.Button({
            text : "Deploy Module",
            scope : this,
            iconCls:'pwnd configuration',
            disabled: true,
            handler : function() {
                var rec = this.modulesm.getSelected();
                if(rec){
                    Wtf.Ajax.requestEx({
                        url:Wtf.req.mbuild+"form.do",
                        params:{
                            action:40,
                            moduleid:rec.get("moduleid"),
                            tablename:rec.get("tablename")
                        }
                    }, this,function(response){
                        if(response.data.success===true){
                            Wtf.moduleStore.load();
                            Wtf.getCmp("quickLinks_portlet").callRequest();
                            Wtf.Msg.alert("Success","Module deployed successfully");
                            this.dsModCenter.load({params:{start:0,limit:this.pg.pageSize}});
                        }else{
                            Wtf.Msg.alert("Error","Error in deploying module. Please check module configuration");
                        }
                    }, function(){
                        Wtf.Msg.alert("Error","Error in deploying module. Please check module configuration");
                    })

                    
                      

                }

            }
        });
        
        this.unDeployModBttn = new Wtf.Toolbar.Button({
            text : "Undeploy Module",
            scope : this,
            iconCls:'pwnd configuration',
            disabled: true,
            handler : function() {
        	Wtf.MessageBox.confirm("Undeploy Module Confirmation",
        			"You will lose all the data related to this module if you undeploy. Do you really want to continue?",
        			function(btn){
                        if(btn == "yes")
                        {
                            var rec = this.modulesm.getSelected();
                            if(rec){
                                Wtf.Ajax.requestEx({
                                    url:Wtf.req.mbuild+"undeployModule.do",
                                    params:{
                                        moduledid:rec.get("moduleid")
                                    }
                                }, this,function(){
                                    Wtf.Msg.alert("Success","Module undeployed successfully");
                                    Wtf.getCmp("quickLinks_portlet").callRequest();
                                }, function(){
                                    Wtf.Msg.alert("Error","Error undeploying module. Please check module configuration");
                                })
                                this.dsModCenter.load({params:{start:0,limit:this.pg.pageSize}});
                            }
                        }
        			}
        			,this)
            }
        });

        this.addToDashboardBttn = new Wtf.Toolbar.Button({
                    text: "Add To Dashboard",
                    toolTip: "Send to dashboard as shortcut link",
                    handler: this.createLink,
                    iconCls:'pwnd configuration',
                    disabled:true,
                    scope: this
                });
        this.grid = new Wtf.grid.GridPanel({
            border: false,
            id : "accGrid" + this.id,
            ds: this.dsModCenter,
            sm: this.modulesm,
            cm: this.cmModCenter,
            autoScroll: true,
            layout: 'fit',
            height: 200,
            viewConfig: {
                autoFill: true,
                forceFit:true
            },
            loadMask: {
                msg: 'Loading...'
            },
            bbar: this.pg,
            tbar : ['Quick Search: ', this.quickPanelSearch, '-',this.formsBttn,this.confBttn,this.addConfigBttn, this.accRightBttn, this.openModBttn,this.addToDashboardBttn,this.deployModBttn, this.unDeployModBttn]
        });
        this.modulesm.on("selectionchange",this.disableBttns,this);
        this.formPanel = new Wtf.form.formContainerPanel({
                    pid : "taballModules",
                    localFlg : true,
                    btnType : ['m','em'],
                    gridId : "accGrid" + this.id
        });
        this.formPanel.on("onsuccess",function(commenttext,time){
            this.dsModCenter.load({params:{
                    start:0,
                    limit:this.pg.pageSize
           }});
        },this);
        this.modulescenter = new Wtf.Panel({
            layout : 'border',
            autoScroll : false,
            bodyStyle :"background-color:transparent;",
            border:false,
            items : [this.formPanel,{
                region :'center',
                layout :'fit',
                autoScroll: false,
                baseCls: "tempClass123",
                border :false,
                items: [this.grid]
            }]
        });
        this.add(this.modulescenter);
    },

    createLink: function(){
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
        this.groupCombo = new Wtf.form.ComboBox({
            displayField: "groupname",
            allowBlank:false,
            fieldLabel: "Link Group* ",
            valueField: "groupid",
            store: this.linkGroupStore,
            mode: 'local',
            anchor: '90%',
            typeAhead: true,
            forceSelection:true
        });
        this.linkname = new Wtf.form.TextField({
            allowBlank:false,
            anchor: '90%',
            fieldLabel:"Link Name* ",
            name:"linkname"
        });
        this.linkGroupStore.on('loadexception', function() {alert('in')}, this);
        this.linkGroupStore.load();

          var linkFormPanel = new Wtf.FormPanel({
            layout:"form",
            region:'center',
            url:  Wtf.req.rbuild+"report.do?action=26",
            border : false,
            labelWidth:110,
            bodyStyle:'padding:13px 0px 0px 13px',
            //autoScroll: true,
            items:[this.linkname,this.groupCombo,{
                    xtype: 'hidden',
                    name: 'linkjson',
                    id: this.id+"linkJsonField"
                }]
          });
          this.moduleid = this.modulesm.getSelected().get("moduleid");
          var createLinkWin = new Wtf.Window({
                title : "Create Shortcut Link",
                closable : true,
                modal : true,
                iconCls : 'iconwin',
                width :350,
                height: 150,
                resizable :false,
                buttonAlign : 'right',
                buttons :[{
                    text : 'Create',
                    scope:this,
                    handler:function(){
                        if(linkFormPanel.form.isValid()) {
                            var linkJson = "[{";
                            linkJson += '"link":"' + this.linkname.getValue() + '",';
                            linkJson += '"processid":"' + this.moduleid + '",';
                            linkJson += '"groupid":"' + this.groupCombo.getValue() + '"';
                            linkJson += "}]";
                            Wtf.getCmp(this.id+"linkJsonField").setValue(linkJson);
                            linkFormPanel.form.submit({
                                scope: this,
                                params:{
                                    linksAddFlag : true
                                },
                                success: function(result,action){
                                    var resultObj = eval('('+action.response.responseText+')');
                                    if(resultObj.success) {
                                        msgBoxShow(47,2);
                                    } else if(!resultObj.success && resultObj.error != "") {
                                        msgBoxShow(["Error", resultObj.error], 1);
                                    } else {
                                        msgBoxShow(5,1);
                                    }
                                    Wtf.getCmp("quickLinks_portlet").callRequest();
                                    createLinkWin.close();
                                },
                                failure: function(frm, action){
                                    var resultObj = eval('('+action.response.responseText+')');
                                    if(!resultObj.success && resultObj.error != "") {
                                        msgBoxShow(["Error", resultObj.error], 1);
                                    } else {
                                        msgBoxShow(5,1);
                                    }
                                    createLinkWin.close();
                                }
                            });
                       }
                    }

                },{
                    text : "Cancel",
                    handler : function(){
                        createLinkWin.close();
                    }
                }],
                layout : 'border',
                items :[linkFormPanel]
            });

            createLinkWin.show();

    },
    
    disableBttns: function(obj){
        if(obj.getCount() > 0){
        	var rec = this.modulesm.getSelected();
            this.formsBttn.enable();
            this.confBttn.enable();
            this.addConfigBttn.enable();
            if(rec.get("displayInd")==true){
                this.openModBttn.enable();
                this.addToDashboardBttn.enable();
                if (rec.get('abstractInd') != true){
                    this.formsBttn.disable();
                }
            }
            if (rec.get('abstractInd') != true)
            {
                if(rec.get("displayInd") == false){
                    
                        this.deployModBttn.enable();
                }
                  if(rec.get("displayInd") == true){

                       	this.unDeployModBttn.enable();
                }
                this.accRightBttn.enable();
            }
        } else {
            this.formsBttn.disable();
            this.confBttn.disable();
            this.addConfigBttn.disable();
            this.openModBttn.disable();
            this.accRightBttn.disable();
            this.deployModBttn.disable();
            this.unDeployModBttn.disable();
            this.addToDashboardBttn.disable();
        }
    },

    onRender: function(config){
        Wtf.allModulesHome.superclass.onRender.call(this, config);
        this.grid.on('cellclick', function(gd, ri, ci, e) {
            var event = e;
            //            if(event.target.className == "openModule") {
            //                var mid = gd.getStore().getAt(ri).data.moduleid;
            //                if(!Wtf.getCmp("Module"+mid)) {
            //                    Wtf.Ajax.requestEx({
            //                        url: Wtf.req.mbuild+'form.do',
            //                        method:'POST',
            //                        params: {
            //                            action: 2,
            //                            formid: gd.getStore().getAt(ri).data.formid,
            //                            reportid : mid
            //                        }
            //                        },
            //                        this,
            //                        function(resp) {
            //                            var obj = eval('(' + resp + ')');
            //                            if(obj.data[0]) {
            //                                var data = URLDecode(obj.data[0].jsondata);
            //                                data = Wtf.decode(data);
            //                                var newdata = this.evalListener(data);
            //                                if(data.items[0].items) {
            //                                    configFields={ismodule:true,
            //                                        data: newdata,
            //                                        cmpId:'Module' + mid,
            //                                        moduleName:gd.getStore().getAt(ri).data.modulename,
            //                                        moduleId:mid,
            //                                        containerId:mainPanel.id,
            //                                        isFilter:false,
            //                                        filterfield:'',
            //                                        filterValue:'',
            //                                        permsObj : obj.recordperm
            //                                    };
            //                                    openGridModule(configFields);
            //                                }
            //                            }
            //                        },
            //                        function() {
            //                        }
            //                    );
            //                } else {
            //                    mainPanel.setActiveTab(Wtf.getCmp("Module"+mid));
            //                    mainPanel.doLayout();
            //                }
            //            } else
            if(event.target.className == "delete") {
                Wtf.MessageBox.confirm('Delete', 'Are you sure you want to delete selected module', function(btn){
                    if(btn=="yes"){
                        Wtf.Ajax.request({
                            url: Wtf.req.mbuild+'form.do',
                            params: {
                                action: 9,
                                moduleid: gd.getStore().getAt(ri).data.moduleid
                            },
                            scope: this,
                            success: function(resp) {
                                //                            var obj = eval('(' + resp.responseText + ')');
                                var resultObj = eval('('+resp.responseText+')');
                                if(resultObj.success) {
                                    msgBoxShow(10,2);
                                    
                                       
                                    
                                    this.dsModCenter.reload({
                                        params:{
                                            start:0,
                                            limit:this.pg.pageSize
                                        }
                                    });
                                }else{
                                    msgBoxShow(['Error', resultObj.msg], Wtf.MessageBox.Error);
                                }
                            },
                            failure: function() {
                            }
                        });
                    }
                },this);
            }
        }, this);

        this.dsModCenter.on('load', function(store) {
            this.quickPanelSearch.StorageChanged(store);
        }, this);
        this.dsModCenter.on('datachanged', function() {
            var p = this.pP.combo.value;
            this.quickPanelSearch.setPage(p);
        }, this);
        this.grid.on("render",function(){
            this.dsModCenter.load({
                params:{
                    start:0,
                    limit:this.pP.combo.value
                }
            });
        },this);
    },

    evalListener: function(obj){
        if(obj.items !== undefined){
            for(var cnt = 0; cnt < obj.items.length; cnt++){
                var temp = obj.items[cnt];
                if(temp.listener !== undefined && temp.listener != "{}"){
                    temp.listeners = eval("(" + temp.listener + ")");
                //                    temp.listeners = {};
                //                    for(var listener in lObj){
                //                        temp.listeners[listener] = lObj[listener];
                ////                        lObj[listener] = eval("(" + lObj[listener] + ")");
                //                    }
                }
                this.evalListener(obj.items[cnt]);
            }
        }
        return obj;
    }
});

Wtf.moduleConfig = function(config){
    Wtf.apply(this,config);
    this.modComboRecord = Wtf.data.Record.create([{
            name: 'moduleid',
            type: 'string'
        },{
            name: 'modulename',
            type: 'string'
        },{
            name: 'tablename',
            type: 'string'
        },{
            name: 'mastertype',
            type:'string'
        }]);

        this.modComboReader = new Wtf.data.KwlJsonReader({
            root:'data'
        }, this.modComboRecord);
        this.modStore = new Wtf.data.Store({
            reader: this.modComboReader,
            //            autoLoad : false,
            url : Wtf.req.mbuild+"form.do",
            baseParams : {
                action : 37,
                moduleid: this.moduleid
            }
        });

        this.otherRecord = Wtf.data.Record.create([{
            name: 'column',
            type: 'string'
        },{
            name: 'type',
            type: 'string'
        },{
            name: 'displayname',
            type: 'string'
        }]);
        this.otherReader = new Wtf.data.KwlJsonReader({
            root:'data'
        }, this.otherRecord);
        this.otherStore = new Wtf.data.Store({
            reader: this.otherReader,
            url : Wtf.req.mbuild+"form.do",
            baseParams : {
                action : 38
            }
        });

        this.sm = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });

        this.otherCM = new Wtf.grid.ColumnModel([
            this.sm,{
            header: "Column Name",
            dataIndex: 'displayname'
        }]);

        this.otherGrid = new Wtf.grid.GridPanel({
            ds: this.otherStore,
            cm: this.otherCM,
            autoScroll: true,
            sm:this.sm,
            layout :"fit",
            viewConfig: {
                forceFit: true
            }
        });

        this.istablecreate = new Wtf.form.Checkbox({
            boxLabel : 'Use Existing Module' ,
            labelSeparator : ""
        });

        this.istablecreate.on("check",function(chkbox,val){
            if(val){
                this.modCombo.enable();
            } else {
                this.modCombo.disable();
                this.otherStore.load({
                params: {
                        moduleid : '1'
                    }
                });
            }
        },this);

        this.modCombo = new Wtf.form.ComboBox({
            valueField: 'moduleid',
            displayField: 'modulename',
            store: this.modStore,
            fieldLabel: 'Module Name',
            editable: false,
            anchor: '60%',
            mode: 'remote',
            triggerAction: 'all',
            selectOnFocus: true,
            typeAhead:true,
            emptyText: 'Select module name...',
            disabled:true
        }),
        this.otherStore.on('loadexception', function() {
            //alert(1);
            }, this);
        this.modCombo.on('select', function(){
            this.otherStore.load({
                params: {
                    moduleid : this.modCombo.getValue()
                }
            });
        }, this);

        this.southRegion = new Wtf.Panel({
            layout:'fit',
            border:false,
            bodyStyle : 'padding:20px;',
            items:[this.otherGrid,this.modCombo]

        });

        this.formlayout = new Wtf.form.FormPanel({
            labelWidth: 120,
            border:false,
            bodyStyle : 'padding:20px;',
            items: [this.istablecreate,this.modCombo]
        });

        this.outerPanel = new Wtf.Panel({
            layout:'border',
            border:false,
            bodyStyle : 'background:#f1f1f1;font-size:10px;',
            items:[{
                region : 'center',
                border : false,
                layout:'fit',
                items:  this.formlayout
            },{
                region : 'south',
                height:280,
                layout:'fit',
                border : false,
                items:this.southRegion
            }]
        });

        Wtf.moduleConfig.superclass.constructor.call(this, {
        title:'Configure module',
        items:this.outerPanel,
        width : 550,
        height:400,
        modal:true,
        layout:'fit',
        resizable: false,
        bbar:['->',{
                text:'Proceed',
                iconCls:'savebuttonImg',
                scope:this,
                handler: function() {
                    /*var formid = this.modulesm.getSelected().get("formid");
                    var moduleid = this.modulesm.getSelected().get("moduleid");
                    var reportkey = this.modulesm.getSelected().get('reportkey');*/
                    var formid = this.formid;
                    var moduleid = this.moduleid;
                    var reportkey = this.reportkey;
                    var column = "";
                    for(var cnt=0;cnt<this.otherStore.data.items.length;cnt++) {
                        var bflag = false;
                        var clname = this.otherStore.data.items[cnt].data.column;
                        for(var c= 0;c<this.sm.selections.items.length;c++) {
                            if(clname==this.sm.selections.items[c].data.column) {
                                bflag = true;
                                break;
                            }
                        }
                        if(!bflag){
                            column += clname+",";
                        }
                    }
                    
                    if(column.length>0){
                        column =column.substring(0,column.length-1);
                    }

                    this.formStore = new Wtf.data.Store({
                        url: Wtf.req.mbuild+'form.do',
                        baseParams: {
                            action:39,
                            moduleid:moduleid,
                            modulevar:column,
                            childmodule:moduleid,
                            parentmodule:this.modCombo.getValue()
                        },
                        reader: new Wtf.data.KwlJsonReader({
                            root:"data"
                        }, ['formid', 'name',"jdata"])
                    });
                    this.formStore.on('load', function(store, rec) {                        
                        if(!Wtf.getCmp("formBuilder"+moduleid)){
                            var pmodule = "";
                            if(this.istablecreate.checked) {
                                pmodule = this.modCombo.getValue();
                            }
                            //mainPanel.loadMask.msg = "Rendering Components ...";
                            //mainPanel.loadMask.show();
                            var studRscPanel = new  Wtf.formBuilderMain({
                                id: "formBuilder"+moduleid,
                                border: false,
                                moduleid: moduleid,
                                formid : formid,
                                reportkey:reportkey,
                                layout : "fit",
                                modulename : this.modulename,
                                title: this.modulename + " - Form Builder",
                                closable: true,
                                jsonflag:true,
                                store:store,
                                rec:rec,
                                parentmodule:pmodule
                            });
                            mainPanel.add(Wtf.getCmp("formBuilder"+moduleid));
                        }
                        mainPanel.setActiveTab(Wtf.getCmp("formBuilder"+moduleid));
                        mainPanel.doLayout();
                    }, this);

                    this.formStore.load();
                    this.close();

                }
        },{
            text:'Close',
            iconCls:'closebuttonImg',
            scope:this,
            handler:function(){
                this.close();
            }
        }]
    });
}

Wtf.extend(Wtf.moduleConfig,Wtf.Window, {
    onRender:function(config){
        Wtf.moduleConfig.superclass.onRender.call(this,config);

    }
});
