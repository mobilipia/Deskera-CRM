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
Wtf.formBuilderMain = function(config){
    Wtf.apply(this, config);
    this.submitConfig={ triggerTypeCombo : "",triggerProcess:"",customJSCODE : ""};
    this.successConfig={ triggerTypeCombo : "",triggerProcess:"",customJSCODE : ""};
    Wtf.formBuilderMain.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.formBuilderMain, Wtf.Panel, {
    iconCls:"pwnd formbuilder",
    tBarConf: null,
    bBarConf: null,
    initComponent: function() {
        Wtf.formBuilderMain.superclass.initComponent.call(this);
        Wtf.QuickTips.init();
        this.addEvents({
            "combodrop": true,
            "newMaster": true
        });
        this.idCounter = 0;
        this.labelArr =[];
        this.childComboIds = [];
        this.autoUpdate = true;
        this.cookies = new Wtf.state.CookieProvider();
        this.initResizeLayer();
        this.initUndoHistory();
        this.initTreePanel();
        this.initEditPanel();
        this.initComponentsPanel();
        this.initFormListPanel();
        this.formCenter = new Wtf.Panel({
            id: 'accPan' + this.id,
            layout : 'border',
            items: [{
                region  : 'north',
                autoHeight  : true,
                tbar    : [{
                    text : 'Reset All',
                    iconCls:'icon-reset',
                    scope: this,
                    handler:function() {
                        this.markUndo("Reset All");
                        this.resetAll();
                    }
                },{
                    text:'Show JSON',
                    iconCls:'icon-editEl',
                    scope:this,
                    handler:this.editConfig
                },'-',{
                    iconCls : 'icon-update',
                    text    : 'Update',
                    tooltip : 'Apply changes',
                    scope   : this,
                    handler : function() {
                        this.updateForm(true);
                    }
                },this.autoUpdateCB = new Wtf.form.Checkbox({
                    boxLabel : 'Auto',
                    id       : 'FBAutoUpdateCB'+this.id,
                    scope   : this,
                    tooltip  : 'Auto update the form if checked. Disable it if rendering is too slow',
                    checked  : this.autoUpdate
                }),'-',{
                    iconCls : 'icon-time',
                    text    : 'Rendering time : <i>unknown</i>',
                    scope   : this,
                    id      : 'FBRenderingTimeBtn'+this.id,
                    tooltip : 'Click to update form and display rendering time',
                    handler : function() {
                        this.updateForm(true);
                    }
                },'-',{
                    id      : 'FBUndoBtn'+this.id,
                    iconCls : 'icon-undo',
                    text    : 'Undo',
                    disabled: true,
                    tooltip : "Undo last change",
                    handler : this.undo,
                    scope   : this
                },'-',{
                    id      : 'FBSaveBtn'+this.id,
                    iconCls : 'icon-save',
                    text    : 'Save',
                    tooltip : "Save the built form",
                    handler : this.showSaveWin,
                    scope   : this
                },'-',{
                    text: "Access Rights",
                    toolTip: "Assign permissions to the module.",
                    handler : function() {
                        assignModulePermissions(this.moduleid);
                    },
                    iconCls: "icon-save",
                    scope: this
                }/*,{
                    text: "On Form Submit",
                    toolTip: "Trigger a Process on form submit or write your custom JS code",
                    handler: this.formSubmitButtonConfigWindow,
                    iconCls: "icon-form-submit",
                    scope: this
                },{
                    text: "On Form Success",
                    toolTip: "Action to be performed when form is submitted successfully",
                    handler: this.formSuccessActionConfigWindow,
                    iconCls: "icon-form-success",
                    scope: this
                }*/]
            },{
                region: 'east',
                border: false,
                width : 255,
                minWidth: 145,
                split : true,
                xtype : 'panel',
                layout: 'border',
                items : [
                    this.treePanel,
                    this.editPanel
                ]
            },{
                region: 'west',
                border: false,
                width : 255,
                split : true,
                xtype : 'panel',
                layout: 'border',
                minWidth: 215,
                items: [{
                    layout: 'fit',
                    border: false,
                    region: 'north',
                    height: 350,
                    title: 'Form Components',
                    collapsible: true,
                    tbar: this.colExpBar,
                    items : [
                        this.componentsPanel
                    ]
                },{
                    region: 'center',
                    layout: 'fit',
                    border: false,
                    items: [
                        this.formListPanel
                    ]
                }]
            },{
                region:'center',
                layout:'fit',
                border:false,
                bodyBorder:false,
                style:'padding:3px 5px;background:white;border:2px solid #99BBE8;',
                items:new Wtf.Panel({
                    border:false,
                    bodyBorder:false,
                    bodyStyle:'background:transparent;',
                    layout:'fit',
                    id:'FBBuilderPanel'+this.id,
                    autoScroll : true
                })
            }]
        });

        this.add(this.formCenter);

        this.builderPanel = Wtf.getCmp('FBBuilderPanel'+this.id);
        var root = this.treePanel.root;
        root.fEl = this.builderPanel;
        root.elConfig = this.builderPanel.initialConfig;
        this.builderPanel._node = root;
        this.builderPanel.on("render",function(){
            var drop = new Wtf.dd.DropZone(this.builderPanel.el, {//main.
                ddGroup:'component',
                notifyOver : function(src,e,data) {
                    var node = this.getNodeForEl(e.getTarget());//main.
                    if (node) {
                        data.node = node; //node.select();
                        this.highlightElement(node.fEl.el);
                        if (this.canAppend(data.compData.config, node) === true) {
                            return true;
                        } else {
                            data.node = null;
                            return false;
                        }
                    } else {
                        data.node = null;
                        return false;
                    }
                }.createDelegate(this),
                notifyDrop : function(src,e,data) {
                    if (!data.node || !data.compData) {
                        return;
                    }
                    var c = data.compData.config;
                    var n;
                    if (typeof c == 'function') {
                        c.call(this,function(config) {
                            n = this.appendConfig(config, data.node, true, true);
                            this.setCurrentNode(n, true);
                        }, data.node.elConfig);
                        return true;
                    } else {
                        n = this.appendConfig(this.cloneConfig(data.compData.config), data.node, true, true);
                        this.setCurrentNode(n, true);
                    }
                            return true;
						}.createDelegate(this),
					notifyOut : function(src,e,data) {
							data.node = null;
						}
				}, this);
                this.builderPanel.drop = drop;//main.
                 // select elements on form with single click
            this.builderPanel.el.on('click', function(e,el) {
                e.preventDefault();
                var node = this.getNodeForEl(el);
                if (!node) {
                    node = this.treePanel.root;
                }
                this.highlightElement(node.fEl.el);
                this.setCurrentNode(node, true);
            }, this);
            // menu on form elements
            this.builderPanel.el.on('contextmenu', function(e,el) {
                e.preventDefault();
                var node = this.getNodeForEl(el);
                if (!node || typeof node.id !='string') {
                    return;
                }
                this.highlightElement(node.fEl.el);
                this.setCurrentNode(node, true);
                this.contextMenu.node = node;
                this.contextMenu.showAt(e.getXY());
            }, this);
        },this);
        this.treePanel.on("render",function(){
            this.treePanel.el.on('contextmenu', function(e) {
                e.preventDefault();
            }, this)
        },this);

    },

    afterRender : function(conf) {
        Wtf.formBuilderMain.superclass.afterRender.call(this,conf);
        this.autoUpdateCB.on('check', function(c) {
            this.autoUpdate = c.checked;
        }, this)
    },

    getConfData : function(obj,flag) {
        var rootConf = {};
        for (var propName in obj) {
            if(propName !='items') {
                rootConf[propName]= obj[propName];
                if(propName=='id') {
                    if(obj[propName].split("_")[0]==3 || obj[propName].split("_")[0]==6) {
                        if(flag) {
                            delete obj['html'];
                        }
                        else
                            rootConf['html'] = "<span style=\"color:gray;\">Please drop component here...</span>";
                    }
                }
            }
        }
        return rootConf;
    },
    
    createTreeFromJson:function(objarr,targetnode) {
        var itemsa = objarr;
        if(itemsa) {
            for(var cnt=0;cnt<itemsa.length;cnt++) {
                if(itemsa[cnt].items!=null && itemsa[cnt].xtype !='radiogroup' && itemsa[cnt].xtype !='checkboxgroup' ) {
                    var rootConf = this.getConfData(itemsa[cnt]);
                    var n = this.appendConfig(this.cloneConfig(rootConf), targetnode, true, false);
                    this.setCurrentNode(n, true);
                    this.createTreeFromJson(itemsa[cnt].items,n);
                } else {
                    if(itemsa[cnt].id && (itemsa[cnt].id.split("_")[0]==3 || itemsa[cnt].id.split("_")[0]==6))
                        itemsa[cnt]['html'] = "<span style=\"color:gray;\">Please drop component here...</span>";

                    if(itemsa[cnt].xtype=='combo' || itemsa[cnt].xtype=='select' ) {
                        if(!this.parentComboStore) {
                            this.parentComboRecord = Wtf.data.Record.create([{
                                name: 'comboid'
                            },{
                                name: 'comboname'
                            },{
                                name: 'comboTreeId'
                            }]);

                            this.parentComboStore = new Wtf.data.Store({
                                reader: new Wtf.data.KwlJsonReader({
                                    root:"data"
                                }, this.parentComboRecord),
                                url: Wtf.req.mbuild+'form.do'
                            });
                        }
                        var newId = itemsa[cnt].id;
                        var fieldLabel = itemsa[cnt].fieldLabel;
                        var p  = new this.parentComboRecord({
                            comboTreeId: newId,
                            comboid: newId,
                            comboname : fieldLabel
                        });
                        this.parentComboStore.insert(this.parentComboStore.getCount(), p);
                    }
                    var n = this.appendConfig(this.cloneConfig(itemsa[cnt]),targetnode,true, false);
                }
            }
        }
    },

    // the tree panel, listing elements
    initTreePanel : function() {
        var tree = new Wtf.tree.TreePanel({
            region: 'north',
            title: "Elements Tree",
            iconCls: "icon-el",
            collapsible: true,
            floatable: false,
            autoScroll: true,
            height: 200,
            split: true,
            animate: false,
            enableDD: true,
//      		enableDrop: true,
            ddGroup: 'component',
            containerScroll: true,
            selModel: new Wtf.tree.DefaultSelectionModel(),
            bbar: [{
                text: 'Expand All',
                tooltip: 'Expand all elements',
                scope: this,
                handler: function() {
                    this.treePanel.expandAll();
                }
            },{
                text: 'Collapse All',
                tooltip: 'Collapse all elements',
                scope: this,
                handler: function() {
                    this.treePanel.collapseAll();
                }
            }]
        });

        var root = new Wtf.tree.TreeNode({
            text: 'GUI Builder elements',
            id: this.getNewId(),
            draggable: false
        });
        tree.setRootNode(root);

        tree.on('click', function(node, e) {
            e.preventDefault();
            if (!node.fEl || !node.fEl.el) {
                return;
            }
            this.highlightElement(node.fEl.el);
            this.setCurrentNode(node);
            window.node = node; // debug
        }, this);

        // clone a node
        var cloneNode = function(node) {
            var config = Wtf.apply({}, node.elConfig);
            var id=this.getNewId();
            config['id'] = id;
            var newNode = new Wtf.tree.TreeNode({
                id:id,
                text:this.configToText(config)
                });
            newNode.elConfig = config;

            // clone children
            for(var i = 0; i < node.childNodes.length; i++){
                n = node.childNodes[i];
                if(n) {
                    newNode.appendChild(cloneNode(n));
                }
            }

            return newNode;

        }.createDelegate(this);

        // assert node drop
        tree.on('nodedragover', function(de) {
            var p = de.point, t= de.target;
            if(p == "above" || t == "below") {
                t = t.parentNode;
            }
            if (!t) {
                return false;
            }
            this.highlightElement(t.fEl.el);
            return (this.canAppend({}, t) === true);
        }, this);

        // copy node on 'ctrl key' drop
        tree.on('beforenodedrop', function(de) {
            if (!de.rawEvent.ctrlKey) {
                this.markUndo("Moved " + Wtf.util.Format.ellipsis((String)(de.dropNode.text), 20));
                return true;
            }
            this.markUndo("Copied " + Wtf.util.Format.ellipsis((String)(de.dropNode.text), 20));
            var ns = de.dropNode, p = de.point, t = de.target;
            if(!(ns instanceof Array)){
                ns = [ns];
            }
            var n;
            for(var i = 0, len = ns.length; i < len; i++){
                n = cloneNode(ns[i]);
                if(p == "above"){
                    t.parentNode.insertBefore(n, t);
                }else if(p == "below"){
                    t.parentNode.insertBefore(n, t.nextSibling);
                }else{
                    t.appendChild(n);
                }
            }
            n.ui.focus();
            if(de.tree.hlDrop){
                n.ui.highlight();
            }
            t.ui.endDrop();
            de.tree.fireEvent("nodedrop", de);
            return false;
        }, this);

        // update on node drop
        tree.on('nodedrop', function(de) {
            var node = de.target;
            if (de.point != 'above' && de.point != 'below') {
                node = node.parentNode || node;
            }
            this.updateForm(false, node);
        }, this, {
            buffer:100
        });

        // get first selected node
        tree.getSelectedNode = function() {
            return this.selModel.getSelectedNode();
        };

        // context menu to delete / duplicate...
        var contextMenu = new Wtf.menu.Menu({
            items:[{
                text    : 'Delete this element',
                iconCls : 'icon-deleteEl',
                scope   : this,
                handler : function(item) {
                    this.removeNode(contextMenu.node);
                }
            },{
                text    : 'Add new element as child',
                iconCls : 'icon-addEl',
                scope   : this,
                handler : function(item) {
                    var node = this.appendConfig({}, contextMenu.node, true, true);
                    if(node)
                        this.treePanel.expandPath(node.getPath());
                }
            },{
                text    : 'Add new element under',
                iconCls : 'icon-addEl',
                scope   : this,
                handler : function(item) {
                    var node = this.appendConfig({}, contextMenu.node.parentNode, true, true);
                    this.treePanel.expandPath(node.getPath());
                }
            },{
                text    : 'Duplicate this element',
                iconCls : 'icon-dupEl',
                scope   : this,
                handler : function(item) {
                    var node = contextMenu.node;
                    this.markUndo("Duplicate " + Wtf.util.Format.ellipsis((String)(node.text), 20));
                    var newNode = cloneNode(node);
                    if (node.isLast()) {
                        node.parentNode.appendChild(newNode);
                    } else {
                        node.parentNode.insertBefore(newNode, node.nextSibling);
                    }
                    this.updateForm(false, node.parentNode);
                }
            },{
                text    : 'Visual resize / move',
                tooltip : 'Visual resize the element.<br/>You can move it too if in an <b>absolute</b> layout',
                iconCls : 'icon-resize',
                scope   : this,
                handler : function(item) {
                    this.visualResize(contextMenu.node);
                }
            }]
        });
        tree.on('contextmenu', function(node, e) {
            e.preventDefault();
            if (node != this.treePanel.root && node.parentNode != this.treePanel.root && typeof node.id =='string' ) {
                contextMenu.node = node;
                contextMenu.showAt(e.getXY());
            }
        }, this);
        this.contextMenu = contextMenu;

        this.treePanel = tree;
    },

    // layer used for selection resize
    initResizeLayer : function() {

        this.resizeLayer = new Wtf.Layer({
            cls:'resizeLayer',
            html:'Resize me'
        });
        this.resizeLayer.setOpacity(0.5);
        this.resizeLayer.resizer = new Wtf.Resizable(this.resizeLayer, {
            handles:'all',
            draggable:true,
            dynamic:true
        });
        this.resizeLayer.resizer.dd.lock();
        this.resizeLayer.resizer.on('resize', function(r,w,h) {
            var n = this.editPanel.currentNode;
            if (!n || !n.elConfig) {
                return false;
            }
            this.markUndo("Resize element to " + w + "x" + h);
            var s = n.fEl.el.getSize();
            if (s.width != w) {
                n.elConfig.width = w;
                if (n.parentNode.elConfig.layout == 'column') {
                    delete n.elConfig.columnWidth;
                }
            }
            if (s.height != h) {
                n.elConfig.height = h;
                delete n.elConfig.autoHeight;
            }
            this.updateForm(true, n.parentNode);
            this.setCurrentNode(n);
            this.highlightElement(n.fEl.el);
        }, this);
        this.resizeLayer.resizer.dd.endDrag = function(e) {
            var n = this.editPanel.currentNode;
            if (!n || !n.elConfig) {
                return false;
            }
            var pos = this.resizeLayer.getXY();
            var pPos = n.parentNode.fEl.body.getXY();
            var x = pos[0] - pPos[0];
            var y = pos[1] - pPos[1];
            this.markUndo("Move element to " + x + "x" + y);
            n.elConfig.x = x;
            n.elConfig.y = y;
            this.updateForm(true, n.parentNode);
            this.setCurrentNode(n);
            this.highlightElement(n.fEl.el);
        }.createDelegate(this);

    },

    // customized property grid for attributes
    initEditPanel : function() {

        var fields = [];
        for (var i in Wtf.formBuilderMain.FIELDS) {
            fields.push([i,i,Wtf.formBuilderMain.FIELDS[i].events]);
        }
        var newPropertyField = new Wtf.form.ComboBox({
            mode: 'local',
            valueField: 'value',
            displayField: 'name',
            listWidth: 180,
            width: 180,
            store: new Wtf.data.SimpleStore({
                sortInfo : {
                    field:'name',
                    order:'ASC'
                },
                fields: ['value','name','events'],
                data: fields
            })
        });
        newPropertyField.on('specialkey', function(tf,e) {
            var name = tf.getValue();
            var ds = this.editPanel.store;
            if (e.getKey() == e.ENTER && name != '' && !ds.getById(name)) {
                var defaultVal = "";
                if (this.attrType(name) == 'object') {
                    defaultVal = "{}";
                }
                if (this.attrType(name) == 'number') {
                    defaultVal = 0;
                }
                ds.add(new Wtf.grid.PropertyRecord({
                    name:name,
                    value:defaultVal
                }, name));
                this.editPanel.startEditing(ds.getCount()-1, 1);
                tf.setValue('');
            }
        }, this);
        newPropertyField.on('select', function(tf, rec) {
            var name = tf.getValue();
            var ds = this.editPanel.store;
            if (name != '' && !ds.getById(name)) {
                if(name == "listener"){
                    var eventStore = new Wtf.data.SimpleStore({
                        fields: ["name", "value"],
                        data: rec.data.events
                    });
                    var listenerWin = new Wtf.common.WtfButtonConfigWindow({
                        listenerWin: true,
                        eventStore: eventStore,
                        resizable: false,
                        border: false,
                        height: 470,
                        width: 400
                    });
                    listenerWin.on("okClicked", function(obj, conf){
                        ds.add(new Wtf.grid.PropertyRecord({
                            name: name,
                            value: ""
                        }, name));
                        var t = ds.getAt(ds.getCount()-1);
                        t.set("value", conf);
//                        this.editPanel.startEditing(ds.getCount()-1, 1);
                        obj.close();
                    }, this);
                    listenerWin.on("cancelClicked", function(window){
                        window.close();
                    }, this);
                    listenerWin.show();
                } else {
                    if(name == "validator"){
                       var validatorComponentObj=new Wtf.validatorComponent({
                       functionValue:''
                       });
                       validatorComponentObj.show();
                       validatorComponentObj.on("okClicked", function(conf){
                            ds.add(new Wtf.grid.PropertyRecord({
                                name: name,
                                value: ""
                            }, name));
                            var t = ds.getAt(ds.getCount()-1);
                            t.set("value", Wtf.encode(conf));
                            validatorComponentObj.close();
                        }, this);
                    }else{
                        var defaultVal = "";
                        if (this.attrType(name) == 'object') {
                            defaultVal = "{}";
                        }
                        if (this.attrType(name) == 'number') {
                            defaultVal = 0;
                        }
                        ds.add(new Wtf.grid.PropertyRecord({
                            name:name,
                            value:defaultVal
                        }, name));
                        this.editPanel.startEditing(ds.getCount()-1, 1);
                    }
                }
                tf.setValue('');
            }
        }, this);

        var grid = new Wtf.grid.PropertyGrid({
            title: 'Parameters',
            height: 300,
            split: true,
            region: 'center',
            source: {},
            bbar: ['Add :', newPropertyField ],
            customEditors: Wtf.formBuilderMain.getCustomEditors(),
            newPropertyField: newPropertyField
        });

        grid.on('cellclick',function(grid, rowIndex, columnIndex, e){
            var record = grid.getStore().getAt(rowIndex);  // Get the Record
            var fieldName = grid.getColumnModel().getDataIndex(columnIndex); // Get field name
            var field=record.get('name');
            var value=record.get('value');
            if(field == 'validator'){
                var validatorComponentObj=new Wtf.validatorComponent({
                    functionValue:Wtf.decode(value)
                });
                validatorComponentObj.show();
                validatorComponentObj.on("okClicked", function(conf){
                    record.set("value", Wtf.encode(conf));
                    validatorComponentObj.close();
                }, this);
            }
        },this);

        var valueRenderer = function(value, p, r) {
            if (typeof value == 'boolean') {
                p.css = (value ? "typeBoolTrue" : "typeBoolFalse");
                return (value ? "True" : "False");
            } else if (this.attrType(r.id) == 'object') {
                p.css = "typeObject";
                return value;
            } else {
                return value;
            }
        }.createDelegate(this);
        var propertyRenderer = function(value, p) {
            var t = Wtf.formBuilderMain.FIELDS[value];
            qtip = (t ? t.desc : '');
            p.attr = 'qtip="' + qtip.replace(/"/g,'&quot;') + '"';
            return value;
        };
        grid.colModel.getRenderer = function(col){
            return (col == 0 ? propertyRenderer : valueRenderer);
        };

        grid.on('beforeedit',function(e){
            var field = e.record.data.name;
            if(field=='isNew' || field=='id' || field=='name' || field=='xtype' || field == "reportId" || field == "sm") {
                e.cancel = true;
                return;
            }
/*            var store = e.grid.getStore();
            var idx = store.find("name", "xtype");
            if(idx != -1){
                var tRec = store.getAt(idx);
                if((tRec.data["value"] == "WtfGridPanel" || tRec.data["value"] == "WtfEditorGridPanel") && (field == "tbar" || field == "bbar")){
                    var GridObject = e.grid.currentNode.fEl;
                    var confWin = new Wtf.common.WtfButtonConfigWindow({
                        title: "Toolbar Configuration",
                        resizable: false,
                        border: false,
                        gridObj: GridObject,
                        height: 400,
                        width: 400
                    });
                    confWin.on("okClicked", function(window, conf){
                        window.gridObj.setTbar(conf);
                        window.close();
                    }, this);
                    confWin.on("cancelClicked", function(window){
                        window.close();
                    }, this);
                    confWin.show();
                    e.cancel = true;
                    return;
                }
            }*/
        },this);

        grid.on('validateedit',function(e){
            var field = e.record.data.name;
            if(field =="fieldLabel") {
                var newId = this.formid+this.strformat(e.value);
                var newName = this.strformat(e.value);
                var store = e.grid.store;
                var items=store.query('name', 'xtype').items;
                var isNew = store.query('name', 'isNew');
                if(isNew.items[0] && isNew.items[0].data.value==true) {
                    store.query('name', 'id').items[0].set('value',newId);
                    if(items[0].data.value !='combo' && items[0].data.value !='select')
                        if(items[0].data.value =='readOnlyCmp') {
                            var autoPopulateFlg=store.query('name', 'autoPopulate').items;
                            if(autoPopulateFlg[0].data.value == false) {
                                store.query('name', 'name').items[0].set('value',getColumnName(newName,this.reportkey));
                            } else {
                                var parentComboName = store.query('name', 'parentComboName');
                                store.query('name', 'name').items[0].set('value',parentComboName.items[0].data.value+getColumnName(newName,this.reportkey));
                            }
                        } else {
                            store.query('name', 'name').items[0].set('value',getColumnName(newName,this.reportkey));
                        }
                    else {
                        if(store.query('name', 'mastertype').items[0].data.value == '1') {
                            store.query('name', 'name').items[0].set('value',getColumnName(newName,this.reportkey));
                            store.query('name', 'hiddenName').items[0].set('value',getColumnName(newName,this.reportkey));
                        }
                    }
                }
            }
        },this);

        grid.on('afteredit',function(e){
            var field = e.record.data.name;
            if(field =="fieldLabel") {
            var store = e.grid.store;
                if(store.query('name', 'xtype').items[0].data.value=='combo' || store.query('name', 'xtype').items[0].data.value=='select' ) {
                    var newId = store.query('name', 'id').items[0].get('value');
                    var fieldLabel = store.query('name', 'fieldLabel').items[0].get('value');
                    var p  = new this.parentComboRecord({
                        comboTreeId: newId,
                        comboid: newId,
                        comboname : fieldLabel
                    });
                    this.parentComboStore.insert(this.parentComboStore.getCount(), p);
                }
               }
        }, this);

        var contextMenu = new Wtf.menu.Menu({
            items:[{
                id      : 'FBMenuPropertyDelete'+grid.id,
                iconCls : 'icon-delete',
                text    : 'Delete this property',
                scope   : this,
                handler : function(item,e) {
                    this.markUndo('Delete property <i>' + item.record.id + '</i>');
                    var ds = grid.store;
                    delete grid.getSource()[item.record.id];
                    ds.remove(item.record);
                    delete item.record;
                    this.updateNode(grid.currentNode);
                    var node = grid.currentNode.parentNode || grid.currentNode;
                    this.updateForm.defer(200, this, [false, node]);
                }
            }]
            });

        // property grid contextMenu
        grid.on('rowcontextmenu', function(g, idx, e) {
            e.stopEvent();
            var r = this.store.getAt(idx);
            if (!r) {
                return false;
            } else if(r.data["name"] == "xtype" || r.data["name"] == "reportId" || r.data["name"] == "sm"){
                return false;
            }
            var i = contextMenu.items.get('FBMenuPropertyDelete'+grid.id);
            i.setText('Delete property "' + r.id + '"');
            i.record = r;
            contextMenu.showAt(e.getXY());
        }, grid);


        // update node text & id
        grid.store.on('update', function(s,r,t) {
            if (t == Wtf.data.Record.EDIT) {
                this.markUndo('Set <i>' + r.id + '</i> to "' +
                    Wtf.util.Format.ellipsis((String)(r.data.value), 20) + '"');
                var node = grid.currentNode;
                this.updateNode(grid.currentNode);
                this.updateForm(false, node.parentNode || node);
            }
        }, this, {
            buffer:100
        });

        this.editPanel = grid;
    },

    // Components panel
    initComponentsPanel : function() {

        // components; config is either an object, or a function called with the adding function and parent config
        var data = WtfComponents.getComponents(this);
        //		var ds = new Wtf.data.SimpleStore({
        //			fields: ['category','name','description','config'],
        //			data : data
        //		});
        //		var tpl = new Wtf.XTemplate(
        //			'<ul id="FormBuilderComponentSelector">',
        //			'<tpl for=".">',
        //				'<li class="component" qtip="{description}">{name}</li>',
        //			'</tpl>',
        //			'<div class="x-clear"></div>',
        //			'</ul>');
        //		var view = new Wtf.DataView({
        //			store        : ds,
        //			tpl          : tpl,
        //			overClass    : 'over',
        //			selectedClass: 'selected',
        //			singleSelect : true,
        //			itemSelector : '.component'
        //		});
        //		view.on('dblclick', function(v,idx,node,e) {
        //				e.preventDefault();
        //				var n = this.editPanel.currentNode;
        //				if (!n) { return false; }
        //				var c = view.store.getAt(idx).data.config;
        //				if (!c) { return false; }
        //				if (typeof c == 'function') {
        //					c.call(this,function(config) {
        //						var newNode = this.appendConfig(config, n, true, true);
        //					}, n.elConfig);
        //				} else {
        //					var newNode = this.appendConfig(this.cloneConfig(c), n, true, true);
        //				}
        //			}, this);
        //		view.on('render', function() {
        //				var d = new Wtf.dd.DragZone(view.el, {
        //						ddGroup         : 'component',
        //						containerScroll : true,
        //						getDragData     : function(e) {
        //								view.onClick(e);
        //								var r = view.getSelectedRecords();
        //								if (r.length == 0) { return false; }
        //								var el = e.getTarget('.component');
        //								if (el) { return {ddel:el,compData:r[0].data}; }
        //							},
        //						getTreeNode : function(data, targetNode) {
        //								if (!data.compData) { return null; }
        //
        //								var c = data.compData.config;
        //								if (typeof c == 'function') {
        //									c.call(this,function(config) {
        //										var n = this.appendConfig(config, targetNode, true, true);
        //										this.setCurrentNode(n, true);
        //									}, targetNode.elConfig);
        //								} else {
        //									var n = this.appendConfig(this.cloneConfig(data.compData.config), targetNode, true, true);
        //									this.setCurrentNode(n, true);
        //									return n;
        //								}
        //								return null;
        //
        //							}.createDelegate(this)
        //					});
        //				view.dragZone = d;
        //			}, this);
        //
        //		var filter = function(b) { ds.filter('category', new RegExp(b.text)); };
        //		var tb = ['<b>Components categories : </b>', {
        //				text         : 'All',
        //				toggleGroup  : 'categories',
        //				enableToggle : true,
        //				pressed      : true,
        //				scope        : ds,
        //				handler      : ds.clearFilter
        //			}, '-'];
        //		var cats = [];
        //		ds.each(function(r) {
        //			var tokens = r.data.category.split(",");
        //			Wtf.each(tokens, function(token) {
        //				if (cats.indexOf(token) == -1) {
        //					cats.push(token);
        //				}
        //			});
        //		});
        //		Wtf.each(cats, function(v) {
        //			tb.push({
        //					text         : v,
        //					toggleGroup  : 'categories',
        //					enableToggle : true,
        //					handler      : filter
        //				});
        //			});
        //
        //		var panel = new Wtf.Panel({
        //			region:'south',
        //			height:100,
        //			layout:'fit',
        //			autoScroll:true,
        //			items:[view],
        //			tbar:tb
        //		});
        //
        //		panel.view = view;

        var hd = new Wtf.Toolbar({
            cls:'top-toolbar',
            items:[ ' ',
            new Wtf.form.TextField({
                width: 150,
                emptyText:'Find a Component',
                listeners:{
                    render: function(f){
                        f.el.on('keydown', filterTree, f, {
                            buffer: 350
                        });
                    }
                }
            }), ' ', ' ',
            {
                iconCls: 'icon-expand-all',
                tooltip: 'Expand All',
                handler: function(){
                    panel.root.expand(true);
                }
            }, '-', {
                iconCls: 'icon-collapse-all',
                tooltip: 'Collapse All',
                handler: function(){
                    panel.root.collapse(true);
                }
            }]
        });

        var panel = new Wtf.tree.TreePanel({
            id: 'comptree'+this.id,
            width: 250,
            collapsible: true,
            enableDrag: false,
            lines: false,
            autoScroll: true,
            loader: new Wtf.tree.TreeLoader({
                preloadChildren: true,
                clearOnLoad: false
            })
        });
        var root = new Wtf.tree.AsyncTreeNode(data);
        panel.setRootNode(root);

        panel.on('dblclick', function(node, e) {
            e.preventDefault();
            var n = this.editPanel.currentNode;
            if (!n) {
                return false;
            }
            var c = node.attributes.config;
            if (!c) {
                return false;
            }
            if (typeof c == 'function') {
                c.call(this,function(config) {
                    var newNode = this.appendConfig(config, n, true, true);
                    this.setCurrentNode(newNode, true);
                }, n.elConfig);
            } else {
                var newNode = this.appendConfig(this.cloneConfig(c), n, true, true);
                this.setCurrentNode(newNode, true);
            }
        }, this);
        panel.on('render', function() {
            var d = new Wtf.dd.DragZone(panel.el, {
                ddGroup         : 'component',
                containerScroll : true,
                getDragData     : function(e) {
                    var htmlNode = Wtf.get(e.getTarget().parentNode.parentNode.id);
                    if(!htmlNode) {
                        return false;
                    }
                    var arr = htmlNode.dom.attributes;
                    if(arr.length <= 2) {
                        return false;
                    }
                    var r = panel.getNodeById(arr[2].nodeValue);
                    if(!r) {
                        return false;
                    }
                    r.select();
                    return {
                        ddel: r.ui.elNode,
                        compData:r.attributes,
                        node: r
                    };
                },
                getTreeNode : function(data, targetNode) {
                    if (!data.compData) {
                        return null;
                    }
                    var c = data.compData.config;
                    if (typeof c == 'function') {
                        c.call(this,function(config) {
                            var n = this.appendConfig(config, targetNode, true, true);
                            this.setCurrentNode(n, true);
                        }, targetNode.elConfig);
                    } else {
                        var n = this.appendConfig(this.cloneConfig(data.compData.config), targetNode, true, true);
                        this.setCurrentNode(n, true);
                        return n;
                    }
                    return null;
                }.createDelegate(this)
            });
            panel.dragConfig = d;
        }, this);

        var filter = new Wtf.tree.TreeFilter(panel, {
            clearBlank: true,
            autoClear: true
        });
        var hiddenPkgs = [];
        function filterTree(e){
            var text = e.target.value;
            Wtf.each(hiddenPkgs, function(n){
                n.ui.show();
            });
            if(!text){
                filter.clear();
                return;
            }
            panel.expandAll();

            var re = new RegExp('^' + Wtf.escapeRe(text), 'i');
            filter.filterBy(function(n){
                return !n.attributes.isClass || re.test(n.text);
            });

            // hide empty packages that weren't filtered
            hiddenPkgs = [];
            panel.root.cascade(function(n){
                if(!n.attributes.isClass && n.ui.ctNode.offsetHeight < 3){
                    n.ui.hide();
                    hiddenPkgs.push(n);
                }
            });
        }
        this.componentsPanel = panel;
        this.colExpBar = hd;
    },

    initFormListPanel : function() {
        this.formRoot = new Wtf.tree.TreeNode({
            id: 'rootnode' + this.id,
            text: this.modulename,
            draggable: false,
            expandable: true,
            expanded: true
        });
        var newNode = new Wtf.tree.TreeNode({
            text:"Saved Forms",
            id :'formsnode'+this.id
        });
        this.formRoot.appendChild(newNode);
        newNode = new Wtf.tree.TreeNode({
            text:"Module Fields",
            id : 'avalField'+this.id
        });
//        newNode.appendChild(new Wtf.tree.TreeNode({
//            text:"Defaults"
//        }));
        this.formRoot.appendChild(newNode);
        newNode = new Wtf.tree.TreeNode({
            text: "Top Toolbar",
            id: "topBarNode"+this.id
        });
        this.formRoot.appendChild(newNode);
        newNode.on("dblclick", function(){
            this.buttonConfWindow("tbar");
        }, this);
        newNode = new Wtf.tree.TreeNode({
            text: "Bottom Toolbar",
            id: "bottomBarNode"+this.id
        });
        this.formRoot.appendChild(newNode);
        newNode.on("dblclick", function(){
            this.buttonConfWindow("bbar");
        }, this);
        var panel = new Wtf.tree.TreePanel({
            id: 'formlist' + this.id,
            title: 'Form List',
            width: 200,
            autoScroll: true,
            split: true,
            root: this.formRoot,
            rootVisible: true
        });
        panel.on('click', function(node) {
            if(node.parentNode.id != 'formsnode' + this.id && node.id == 'avalField'+this.id) {
                new Wtf.userFieldsChoice({
                    moduleid : this.moduleid,
                    layout: 'fit',
                    modal : true
                }).show();
            }
        },this);
//        panel.on('dblclick', function(node) {
//            if(node.parentNode.id != 'formsnode' + this.id) {
//                return;
//            }
//            panel.disable();
//                Wtf.Ajax.requestEx({
//                url: Wtf.req.mbuild+'form.do',
//                method:'POST',
//                params: {
//                    action: 2,
//                    formid: node.id
//                }
//            },
//            this,
//            function(resp) {
//                var obj = eval('(' + resp + ')');
//                var data = URLDecode(obj.data[0].jsondata);
//                data = Wtf.decode(data);
//                var tempPanel = new Wtf.Panel({
//                    autoScroll: true,
//                    border: false,
//                    items: [data]
//                })
//                var formWin = new Wtf.Window({
//                    layout: 'fit',
//                    title: Wtf.util.Format.stripTags(node.text),
//                    border: false,
//                    height: 500,
//                    width: 600,
//                    resizable: true,
//                    modal: true,
//                    items: [tempPanel]
//                });
//                formWin.on('close', function() {
//                    panel.enable();
//                }, this);
//                formWin.show();
//            },
//            function() {
//                panel.enable();
//            });
//        }, this);
        this.formStore = new Wtf.data.Store({
            url: Wtf.req.mbuild+'form.do',
            baseParams: {
                action: 1,
                moduleid: this.moduleid
            },
            reader: new Wtf.data.KwlJsonReader({
                root:"data"
            }, ['formid', 'name',"jdata"])
        });
        this.formStore.on('load', function(store, rec) {
            if(this.jsonflag){
                var resObj = eval("(" + store.reader.responseText.trim() + ")");
                var btnObj = eval("(" + resObj.buttonConf.trim() + ")");
                this.tBarConf = this.getConfString(btnObj.tbar);
                this.bBarConf = this.getConfString(btnObj.bbar);
                var count = this.store.getCount();
                var formNode = this.formListPanel.getNodeById("formsnode"+this.id);
                var obj = eval('(' + this.rec[0].data.jdata + ')');
                for(var i = 0; i < count; i++) {
                    formNode.appendChild(new Wtf.tree.TreeNode({
                        id: this.rec[i].data.formid,
                        name: this.rec[i].data.name,
                        text: this.rec[i].data.name + '<a href="#"><img id="deleteimg_' + this.rec[i].data.formid +
                        '" style="height:12px;" src="../icons/cancel12.png"/></a>'
                        }));
                    this.formListPanel.getNodeById(this.formid).addListener('click', this.deleteForm, this);
                }
                this.formRoot.expand();

                
                var data = "";
                if(obj.jsondata) {
                    data = Wtf.decode(WtfGlobal.URLDecode(obj.jsondata));
                    this.submitConfig = data.submitConfig||{ triggerTypeCombo : "",triggerProcess:"",customJSCODE : ""};
                    this.successConfig = data.successConfig||{ triggerTypeCombo : "",triggerProcess:"",customJSCODE : ""};
                    var rootConf = this.getConfData(data);
                    var n = this.appendConfig(this.cloneConfig(rootConf),false,true, false);
                    this.setCurrentNode(n, true);
                    this.createTreeFromJson(data.items,n);
                }
                mainPanel.loadMask.hide();
            } else {
                var resObj = eval("(" + store.reader.responseText.trim() + ")");
                var btnObj = eval("(" + resObj.buttonConf.trim() + ")");
                this.tBarConf = this.getConfString(btnObj.tbar);
                this.bBarConf = this.getConfString(btnObj.bbar);
                var count = store.getCount();
                var formNode = this.formListPanel.getNodeById("formsnode"+this.id);
                for(var i = 0; i < count; i++) {
                    formNode.appendChild(new Wtf.tree.TreeNode({
                        id: rec[i].data.formid,
                        name: rec[i].data.name,
                        text: rec[i].data.name + '<a href="#"><img id="deleteimg_' + rec[i].data.formid +
                        '" style="height:12px;" src="../icons/cancel12.png"/></a>'
                        }));
                    this.formListPanel.getNodeById(rec[i].data.formid).addListener('click', this.deleteForm, this);
                }
                this.formRoot.expand();
                var obj = eval('(' + rec[0].data.jdata + ')');
                var data = "";
                if(obj.jsondata) {
                    data = Wtf.decode(WtfGlobal.URLDecode(obj.jsondata));
                    this.submitConfig = data.submitConfig||{ triggerTypeCombo : "",triggerProcess:"",customJSCODE : ""};
                this.successConfig = data.successConfig||{ triggerTypeCombo : "",triggerProcess:"",customJSCODE : ""};
                    var rootConf = this.getConfData(data);
                    var n = this.appendConfig(this.cloneConfig(rootConf),false,true, false);
                    this.setCurrentNode(n, true);
                    this.createTreeFromJson(data.items,n);
                }
                mainPanel.loadMask.hide();
            }
        }, this);
        this.formStore.load();
        this.formListPanel = panel;
    },
     formSuccessActionConfigWindow : function(){
     var triggerTypeRec= new Wtf.data.Record.create([
        {
            name: 'name',
            mapping: 0
        },{
            name:'id',
            mapping: 1
        }

        ]);
    var triggertypeRecreader = new Wtf.data.ArrayReader({
     }, triggerTypeRec);
        var triggerTypeData= [["Built-in Success Handler",1],["Open Module",2],["Custom JS Code",3]]
    var triggerTypeStore=new Wtf.data.Store({
            reader:triggertypeRecreader,
            data:triggerTypeData
        })
     this.formSuccessActionTriggerWin =  new Wtf.Window({
            width:500,
            layout:'fit',
            title:'On Success trigger configuration',
            bodyStyle:'padding:10px',
            height:200,
            items:[this.formSuccessTriggerForm=new Wtf.form.FormPanel({
                    xtype:'form',
                    id:Wtf.id(),
                    items:[this.triggerTypeCombo = new Wtf.form.ComboBox({
                            fieldLabel:'Trigger Type',
                            displayField:'name',
                            valueField:'id',
                            hiddenName:'triggerTypeCombo',
                            store:triggerTypeStore,
                            value:this.successConfig.triggerTypeCombo ? this.successConfig.triggerTypeCombo : 1,
                            triggerAction:'all',
                        typeAhead:true,
                        mode:'local'

                    }),this.triggerModuleCombo = new Wtf.form.ComboBox({
                        xtype:'combo',
                        fieldLabel:'Module Name',
                        width:230,
                        id:Wtf.id(),
                        hiddenName:'triggerModule',
                        store:Wtf.moduleStore,
                        displayField:'modulename',
                        valueField:'moduleid',
                        triggerAction:'all',
                        value:this.successConfig.triggerModule ? this.successConfig.triggerModule : "",
                        checked:true,
                        typeAhead:true,
                        mode:'local'
                    }),this.triggerSuccessJSTextArea=new Wtf.form.TextArea({
                        xtype:'textarea',
                        width:230,
                        id:Wtf.id(),
                        fieldLabel:'Custom Javascript Code',
                        name:'triggerCustomJSCode',
                        value:this.successConfig.triggerCustomJSCode ? this.successConfig.triggerCustomJSCode : "",
                        height:100
                    })]
            })],
        buttons:[{
                text:'Save',
                handler:function(){
                    this.successConfig = this.formSuccessTriggerForm.form.getValues(false);
                     this.formSuccessActionTriggerWin.close();
                },
                scope:this
        },{
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                handler:function(){
                    this.formSuccessActionTriggerWin.close();
                },
                scope:this
        }]
        })
        this.formSuccessActionTriggerWin.show();
        this.hideSuccessForm_items(this.triggerTypeCombo.getValue());
        this.triggerTypeCombo.on("select",function(combo){
                this.hideSuccessForm_items(combo.getValue());
        },this);

    },
    formSubmitButtonConfigWindow : function(){
     var triggerTypeRec= new Wtf.data.Record.create([
        {
            name: 'name',
            mapping: 0
        },{
            name:'id',
            mapping: 1
        }

        ]);
    var triggertypeRecreader = new Wtf.data.ArrayReader({
     }, triggerTypeRec);
        var triggerTypeData= [["Trigger a Process",1],["Custom JS Code",2]]
    var triggerTypeStore=new Wtf.data.Store({
            reader:triggertypeRecreader,
            data:triggerTypeData
        })
     this.formSubmitTriggerWin =  new Wtf.Window({
            width:500,
            layout:'fit',
            title:'On Submit trigger configuration',
            bodyStyle:'padding:10px',
            height:200,
            items:[this.formSubmitTriggerForm=new Wtf.form.FormPanel({
                    xtype:'form',
                    id:Wtf.id(),
                    items:[this.triggerTypeCombo = new Wtf.form.ComboBox({
                            fieldLabel:'Trigger Type',
                            displayField:'name',
                            valueField:'id',
                            hiddenName:'triggerTypeCombo',
                            value:this.submitConfig.triggerTypeCombo ? this.submitConfig.triggerTypeCombo : 1,
                            store:triggerTypeStore,
                            triggerAction:'all',
                        typeAhead:true,
                        mode:'local'

                    }),this.triggerProcessCombo = new Wtf.form.ComboBox({
                        xtype:'combo',
                        fieldLabel:'Process Name',
                        width:230,
                        id:Wtf.id(),
                        hiddenName:'triggerProcess',
                        store:Wtf.processStore,
                        displayField:'processname',
                        value:this.submitConfig.triggerProcess ? this.submitConfig.triggerProcess : "",
                        valueField:'processid',
                        triggerAction:'all',
                        checked:true,
                        typeAhead:true,
                        mode:'local'
                    }),this.triggerJSTextArea=new Wtf.form.TextArea({
                        xtype:'textarea',
                        width:230,
                        id:Wtf.id(),
                        fieldLabel:'Custom Javascript Code',
                        name:'triggerCustomJSCode',
                        value:this.submitConfig.triggerCustomJSCode ? this.submitConfig.triggerCustomJSCode : "",
                        height:100
                    })]
            })],
        buttons:[{
                text:'Save',
                handler:function(){
                   this.submitConfig = this.formSubmitTriggerForm.form.getValues(false);
                   this.formSubmitTriggerWin.close();
                },
                scope:this
        },{
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                handler:function(){
                    this.formSubmitTriggerWin.close();
                },
                scope:this
        }]
        })
       this.formSubmitTriggerWin.show();
        this.hideSubmitForm_items(this.triggerTypeCombo.getValue());
        this.triggerTypeCombo.on("select",function(combo){
                this.hideSubmitForm_items(combo.getValue());
                
        },this);
    },
    hideSubmitForm_items: function (val){
    if(val ==2){
                    this.triggerJSTextArea.showItem();
                    this.triggerProcessCombo.hideItem();
                    this.triggerProcessCombo.reset();
                }else{
                    this.triggerProcessCombo.showItem();
                    this.triggerJSTextArea.reset();
                    this.triggerJSTextArea.hideItem();
                }
    },
        hideSuccessForm_items: function (val){
   if(val ==2){
                    this.triggerModuleCombo.showItem();
                    this.triggerSuccessJSTextArea.hideItem();
                    this.triggerModuleCombo.reset();
                }else if(val == 3){
                    this.triggerModuleCombo.hideItem();
                    this.triggerSuccessJSTextArea.reset();
                    this.triggerSuccessJSTextArea.showItem();
                }else{
                    this.triggerModuleCombo.hideItem();
                    this.triggerSuccessJSTextArea.hideItem();
                }
    },
    buttonConfWindow: function(type){
        var title = "Top Toolbar configuration";
        var defaultConf = this.tBarConf;
        if(type == "bbar"){
            defaultConf = this.bBarConf;
            title = "Bottom Toolbar Configuration";
        }
        var confWin = new Wtf.common.WtfButtonConfigWindow({
            title: title,
            resizable: false,
            border: false,
            type: type,
            height: 470,
            defaultConf: defaultConf,
            width: 400
        });
        confWin.on("okClicked", function(window, conf){
            var buttonConf = "";
            for(var temp in conf){
                var handler = conf[temp].handler.replace(/"/g,"'");
                handler = handler.replace(/\n/g," ");
                buttonConf += "{\"text\":\"" + conf[temp].text + "\",\"handler\":\"" + handler + "\",\"type\":\"" + conf[temp].type + "\"},";
            }
            if(buttonConf !="")
                buttonConf = "[" + buttonConf.substring(0, (buttonConf.length - 1)) + "]";
            if(window.type == "tbar")
                this.tBarConf = buttonConf;
            else
                this.bBarConf = buttonConf
            window.close();
        }, this);
        confWin.on("cancelClicked", function(window){
            window.close();
        }, this);
        confWin.show();
    },

    getConfString: function(conf){
        if(conf !== undefined){
            var buttonConf = "";
            for(var cnt = 0; cnt < conf.length; cnt++){
                var handler = conf[cnt].functext.replace(/"/g,"'");
                handler = handler.replace(/\n/g," ");
                buttonConf += "{\"text\":\"" + conf[cnt].text + "\",\"handler\":\"" + handler + "\",\"type\":\"" + conf[cnt].type + "\"},";
            }
            if(buttonConf !="")
                buttonConf = "[" + buttonConf.substring(0, (buttonConf.length - 1)) + "]";
            return buttonConf;
        }
    },
// Undo history
    initUndoHistory : function() {
        this.undoHistoryMax = 20;
        this.undoHistory = [];
    },

    // add current config to undo
    markUndo : function(text) {
        this.undoHistory.push({
            text:text,
            config:this.getTreeConfig()
            });
        if (this.undoHistory.length > this.undoHistoryMax) {
            this.undoHistory.remove(this.undoHistory[0]);
        }
        this.updateUndoBtn();
    },

    // update undo button according to undo history
    updateUndoBtn : function() {
        if (this.undoHistory.length == 0) {
            Wtf.ComponentMgr.get('FBUndoBtn'+this.id).disable().setText('Undo');
        } else {
            Wtf.ComponentMgr.get('FBUndoBtn'+this.id).enable().setText('<b>Undo</b> : ' +
                this.undoHistory[this.undoHistory.length-1].text);
        }
    },

    // undo last change
    undo : function() {

        var undo = this.undoHistory.pop();
        this.updateUndoBtn();
        if (!undo || !undo.config) {
            return false;
        }
        this.setCurrentNode(null);
        this.setConfig(undo.config);
        return true;
    },

    // return the node corresponding to an element (search upward)
    getNodeForEl : function(el) {
        var search = 0;
        var target = null;
        while (search < 10) {
            target = Wtf.ComponentMgr.get(el.id);
            if (target && target._node) {
                return target._node;
            }
            el = el.parentNode;
            if (!el) {
                break;
            }
            search++;
        }
        return null;
    },

    // show the layer to visually resize / move element
    visualResize : function(node) {
        if (node == this.treePanel.root || !node || !node.fEl) {
            return;
        }
        if (node.parentNode && node.parentNode.elConfig && node.parentNode.elConfig.layout == 'fit') {
            msgBoxShow(["Error", "You won't be able to resize an element" +
                " contained in a 'fit' layout.<br/>Update the parent element instead."],1);
        } else {
            if (node.parentNode && node.parentNode.elConfig && node.parentNode.elConfig.layout == 'absolute') {
                this.resizeLayer.resizer.dd.unlock();
                this.resizeLayer.resizer.dd.constrainTo(node.parentNode.fEl.body);
            } else {
                this.resizeLayer.resizer.dd.lock();
            }
            this.resizeLayer.setBox(node.fEl.el.getBox());
            this.resizeLayer.show();
        }
    },

    // hide select layers (e is click event)
    hideHighligt : function(e) {
        if (e) {
            e.preventDefault();
        }
        this.builderPanel.el.select('.selectedElement').removeClass('selectedElement');
        this.builderPanel.el.select('.selectedElementParent').removeClass('selectedElementParent');
    },

    // set current editing node
    setCurrentNode : function(node, select) {
        var p = this.editPanel;
        p.enable();
        if (!node || !node.elConfig) {
            p.currentNode = null;
            p.setSource({});
            p.disable();
        } else {
            config = node.elConfig;
            for (k in config) {
                if (this.attrType(k) == 'object' && typeof config[k] == 'object') {
                    try {
                        var ec = Wtf.encode(config[k]);
                        config[k] = ec;
                    } catch(e) {}
                    }
                }
            p.setSource(config);
            p.currentNode = node;
            if (node.fEl == this.builderPanel) {
                p.disable();
            }
        }
        if (select) {
            this.treePanel.expandPath(node.getPath());
            node.select();
        }
    },

    // update node text & id (if necessary)
    updateNode : function(node) {
        if (!node) {
            return;
        }
        node.setText(this.configToText(node.elConfig));
        if (node.elConfig.id && node.elConfig.id != node.id) {
            //            node.getOwnerTree().unregisterNode(node);
            node.id = node.elConfig.id;
        //            node.getOwnerTree().registerNode(node);
        }
    },

    // update the form at the specified node (if force or autoUpdate is true)
    updateForm : function(force, node) {
        node = node || this.treePanel.root;
        var updateTime = (node == this.treePanel.root);
        var time = null;

        // search container to update, upwards
        node = this.searchForContainerToUpdate(node);

        if (force === true || this.autoUpdate) {
            var config = this.getTreeConfig(node, true);
            time = this.setFormConfig(config, node.fEl);
            this.updateTreeEls(node.fEl);
            this.hideHighligt();

        // save into cookies
        //				this.cookies.set('formbuilderconfig', this.getTreeConfig());
        }

        if (time && updateTime) {
            Wtf.ComponentMgr.get('FBRenderingTimeBtn'+this.id).setText(
                'Rendering time : <i>' + time + 'ms</i>');
        }
    },

    // load from cookies if present
    loadConfigFromCookies : function() {
        var c = this.cookies.get('formbuilderconfig');
        if (c) {
            try {
                this.setConfig(c);
            } catch(e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    },

    // search upware for a container to update
    searchForContainerToUpdate : function(node) {

        // search for a parent with border or column layout
        var found = null;
        var root = this.treePanel.root;
        var n = node;
        while (n != root) {
            if (n && n.elConfig &&
                (n.elConfig.layout == 'border' ||
                    n.elConfig.layout == 'table' ||
                    n.elConfig.layout == 'column')) {
                found = n;
            }
            n = n.parentNode;
        }
        if (found !== null) {
            return found.parentNode;
        }

        // no column parent, search for first container with items
        n = node;
        while (n != root) {
            if (!n.fEl || !n.fEl.items) {
                n = n.parentNode;
            } else {
                break;
            }
        }
        return n;
    },

    // hilight an element
    highlightElement : function(el) {
        this.resizeLayer.hide();
        if (el == this.builderPanel.el) {
            return;
        }
        if (el) {
            var elParent = el.findParent('.x-form-element', 5, true);
            this.hideHighligt();
            if (elParent) {
                elParent.addClass("selectedElementParent");
            }
            el.addClass("selectedElement");
        }
    },

    // get the tree config at the specified node
    getTreeConfig : function(node, addNodeInfos) {
        if (!node) {
            node = this.treePanel.root;
        }
        var config = Wtf.apply({}, node.elConfig);
        if (!config.id && addNodeInfos) {
            config.id = node.id;
        }
        for (var k in config) {
            if (this.attrType(k) == 'object') {
                try {
                    config[k] = Wtf.decode(config[k]);
                } catch(e) {}
            }
        }
        if (addNodeInfos) {
            config._node = node;
        }
        var items = [];
        node.eachChild(function(n) {
            items.push(this.getTreeConfig(n, addNodeInfos));
        }, this);
        if (items.length > 0) {
            config.items = items;
        } else if (config.xtype == 'form') {
            config.items = {};
        } else {
            delete config.items;
        }
        return config;
    },

    // update node.fEl._node associations
    updateTreeEls : function(el) {
        if (!el) {
            el = this.builderPanel;
        }
        if (el._node) {
            el._node.fEl = el;
            // workaround for fieldsets
            if (el.xtype == 'fieldset') {
                el.el.dom.id = el.id;
            }
        }
        if (!el.items) {
            return;
        }
        try {
            el.items.each(function(i) {
                this.updateTreeEls(i);
            }, this);
        } catch (e) {}
    },

    // node text created from config of el
    configToText : function(c) {
        var txt = [];
        c = c || {};
        if (c.xtype)      {
            txt.push(c.xtype);
        }
        if (c.fieldLabel) {
            txt.push('[' + c.fieldLabel + ']');
        }
        if (c.boxLabel)   {
            txt.push('[' + c.boxLabel + ']');
        }
        if (c.layout)     {
            txt.push('<i>' + c.layout + '</i>');
        }
        if (c.title)      {
            txt.push('<b>' + c.title + '</b>');
        }
        if (c.text)       {
            txt.push('<b>' + c.text + '</b>');
        }
        if (c.region)     {
            txt.push('<i>(' + c.region + ')</i>');
        }
        return (txt.length == 0 ? "Element" : txt.join(" "));
    },

    // return type of attribute
    attrType : function(name) {
        if (!Wtf.formBuilderMain.FIELDS[name]) {
            return 'unknown';
        }
        return Wtf.formBuilderMain.FIELDS[name].type;
    },

    // return a cloned config
    cloneConfig : function(config) {
        if (!config) {
            return null;
        }
        var newConfig = {};
        for (i in config) {
            if (typeof config[i] == 'object') {
                newConfig[i] = this.cloneConfig(config[i]);
            } else if (typeof config[i] != 'function') {
                newConfig[i] = config[i];
            }
        }
        return newConfig;
    },

    // erase all
    resetAll : function() {
        //		var w = this.viewport.layout.center.getSize().width - 50;
        var node = this.setConfig({
            items:[]
        });
        this.setCurrentNode(node, true);
        this.formStore.reload();
    },

    // get a new ID
    getNewId : function() {
        return "form"+this.formid+"_" + (this.idCounter++);
    },

    // return true if config can be added to node, or an error message if it cannot
    canAppend : function(config, node) {
        if (node == this.treePanel.root && this.treePanel.root.hasChildNodes()) {
            return "Only one element can be directly under the GUI Builder";
        }
        var xtype = node.elConfig.xtype;
//        if (xtype && ['panel','viewport','form','window','tabpanel','toolbar','fieldset','checkboxgroup','radiogroup','readOnlyCmp'].indexOf(xtype) == -1) {
        if (!xtype&&(['checkbox','datefield','htmleditor','numberfield','radio','textarea','textfield','timefield'].indexOf(config.xtype)>=0||typeof config=="function")) {
            return false;//'You cannot add element under xtype "'+xtype+'"';
        }
        return true;
    },

    // add a config to the tree
    appendConfig : function(config, appendTo, doUpdate, markUndo) {

        if (!appendTo) {
            appendTo = this.treePanel.getSelectedNode() ||
            this.treePanel.root;
        }
        var canAppend = this.canAppend(config,appendTo);
        while(canAppend === false){
            appendTo = appendTo.parentNode;
            appendTo.select();
            canAppend = this.canAppend(config,appendTo);
        }
        if (canAppend !== true) {
            msgBoxShow(["Unable to add element", canAppend],0);
            return false;
        }
        var ConfItems = config.items;
        delete config.items;
        var id = config.id||(config._node ? config._node.id : this.getNewId());
        config['id'] = id;
        var newNodeConf = {
            id:id,
            text:this.configToText(config)
            };
        if(typeof id == 'number' && id<=6) {
            if(id==1 || id==2 || id==5)
                newNodeConf['allowDrop'] = false;
            newNodeConf['draggable']= false;
        }
        var newNode = new Wtf.tree.TreeNode(newNodeConf);
        for(var k in config) {
            if (config[k]===null) {
                delete config[k];
            }
        }
        newNode.elConfig = config;

        if (markUndo === true) {
            newNode.elConfig['isNew'] = true;
            if(!newNode.elConfig['fieldLabel'])
                newNode.elConfig['fieldLabel'] = "Label";
            if(newNode.elConfig['fieldLabel'] && newNode.elConfig['xtype'] != "combo" && newNode.elConfig['xtype'] != "select")
                newNode.elConfig['name'] = this.strformat(newNode.elConfig['fieldLabel']);
            else if (newNode.elConfig['xtype'] != "combo" && newNode.elConfig['xtype'] != "select")
                    newNode.elConfig['name'] = "label";
            this.markUndo("Add " + newNode.text);
        }
        appendTo.appendChild(newNode);
//        var items11 = config.items;
        if (ConfItems) {
            for (itemObj in ConfItems) {
                if (typeof ConfItems[itemObj] !== 'function')
                    this.appendConfig(ConfItems[itemObj], newNode, false);
            }
        }
        if (doUpdate !== false) {
            this.updateForm(false, newNode);
        }
        return newNode;
    },

    // remove a node
    deleteNodes: new Array(),
    removeNode : function(node) {
        if (!node || node == this.treePanel.root||node.parentNode == this.treePanel.root) {
            return;
        }
        this.markUndo("Remove " + Wtf.util.Format.ellipsis((String)(node.text), 20));
        var nextNode = node.nextSibling || node.parentNode;
        var pNode = node.parentNode;
        //                        if(node.elConfig.isNew==null)
        //                            this.deleteNodes.push(node.elConfig);
        pNode.removeChild(node);
        this.updateForm(false, pNode);
        this.setCurrentNode(nextNode, true);
    },

    // update the form
    setFormConfig : function(config, el) {
        el = el || this.builderPanel;

        // empty the form
        if (el.items) {
            while (el.items.first()) {
                el.remove(el.items.first(), true);
            }
        }
        if (el.getLayoutTarget()) {
            el.getLayoutTarget().update();
        } else {
            el.update();
        }

        // adding items
        var start = new Date().getTime();
        if (config.items) {
            for (var i=0;i<config.items.length;i++) {
                el.add(config.items[i]);
            }
        }
        el.doLayout();
        var time = new Date().getTime() - start;
        return time;

    },

    // show a window with the json config
    editConfig : function() {
        var size = this.formCenter.getSize();
        if (!this.jsonWindow) {
            var tf = new Wtf.form.TextArea({
                readOnly : true
            });
            this.jsonWindow = new Wtf.Window({
                title       : "Form Config",
                width       : 400,
                height      : size.height - 50,
                autoScroll  : true,
                layout      : 'fit',
                items       : [tf],
                modal       : true,
                closeAction : 'hide'
            });
            this.jsonWindow.tf = tf;
            this.jsonWindow.addButton({
                text    : "Close",
                scope   : this.jsonWindow,
                handler : function() {
                    this.hide();
                }
            });
        }
        var cleanConfig = this.getTreeConfig();
        cleanConfig = (cleanConfig.items?cleanConfig.items[0]||{}:{});
        cleanConfig = Wtf.formBuilderMain.JSON.encode(cleanConfig);
        this.jsonWindow.tf.setValue(cleanConfig);
        this.jsonWindow.show();
    },

    // remove all nodes
    removeAll : function() {
        var root = this.treePanel.root;
        while(root.firstChild){
            root.removeChild(root.firstChild);
        }
    },

    // set config (remove then append a whole new config)
    setConfig : function(config) {
        if (!config || !config.items) {
            return false;
        }
        // delete all items
        this.removeAll();
        // add all items
        var root = this.treePanel.root;
        var node = null;
        for (var i = 0; i < config.items.length; i++) {
            try {
                node = this.appendConfig(config.items[i], root);
            } catch(e) {
                msgBoxShow(["Error", "Error while adding : " + e.name + "<br/>" + e.message],1);
            }
        }
        this.updateForm(true, root);
        return node || root;
    },

    removeAllChilds : function(node) {
        while(node.firstChild) {
            node.removeChild(node.firstChild);
        }
    },

    showSaveWin : function() {
        this.saveForm(this.modulename);
    },
    deleteExtraFields:function(){
        var tempArr = new Array();
        for(var cnt=0;cnt<this.formfieldArr.length;cnt++){
            if(this.formfieldArr[cnt].items==null || this.formfieldArr[cnt].xtype == 'radiogroup' || this.formfieldArr[cnt].xtype == 'checkboxgroup'
                || this.formfieldArr[cnt].xtype == 'readOnlyCmp'){
                tempArr.push(this.formfieldArr[cnt]);
            }
        }
        return this.createFormFieldJson(tempArr);
    },
    createFormFieldJson:function(fieldArr){
        var jsonData = " ";
        for(var cnt=0;cnt<fieldArr.length;cnt++){
            if(fieldArr[cnt].fieldLabel!=null){
                jsonData = jsonData+"{";
                jsonData = jsonData+"'fieldLabel':'"+fieldArr[cnt].fieldLabel+"',";
                if(fieldArr[cnt].xtype=='readOnlyCmp') {
                    if(fieldArr[cnt].autoPopulate) {
                        //Save name as fieldname + X-X + columnname of original column
                        jsonData = jsonData+"'name':'"+fieldArr[cnt].name +_reportHardcodeStr+ fieldArr[cnt].readFieldName+_reportHardcodeStr+fieldArr[cnt].parentComboName+"',";
                    } else {
                        jsonData = jsonData+"'name':'"+fieldArr[cnt].name +"',";
                    }
                    jsonData = jsonData+"'autoPopulate':'"+fieldArr[cnt].autoPopulate +"',";
                } else {
                    jsonData = jsonData+"'name':'"+fieldArr[cnt].name +"',";
                }

                if(fieldArr[cnt].xtype=='combo' || fieldArr[cnt].xtype=='select'
                        || (fieldArr[cnt].xtype=='readOnlyCmp' && fieldArr[cnt].autoPopulate)){
                    jsonData = jsonData+"'datastore':'"+fieldArr[cnt].dataStore +"',";
                    jsonData = jsonData+"'hiddenName':'"+fieldArr[cnt].hiddenName +"',";
                    jsonData = jsonData+"'mastertype':'"+fieldArr[cnt].mastertype +"',";
                }
                if(fieldArr[cnt].xtype=="numberfield"){
                    if(fieldArr[cnt].allowDecimals!=null){
                        jsonData = jsonData+"'allowDecimals':'"+fieldArr[cnt].allowDecimals +"',";
                    }
                }
                if(fieldArr[cnt].inputType!=null && fieldArr[cnt].inputType == "file"){
                    jsonData = jsonData+"'xtype':'file'";
                }else{
                    jsonData = jsonData+"'xtype':'"+fieldArr[cnt].xtype+"'";
                }
                if (fieldArr[cnt].value){
                    jsonData = jsonData+",'value':'"+fieldArr[cnt].value+"'";
                }else{
                    jsonData = jsonData+",'value':''";
                }

                if (fieldArr[cnt].NewObject){
                    jsonData = jsonData+",'NewObject':'true'";
                }else{
                    jsonData = jsonData+",'NewObject':'false'";
                }
                
                jsonData = jsonData+"},";
            }
        }
        var trmLen = jsonData.length - 1;
        var finalStr = jsonData.substr(0,trmLen);
        return finalStr;
    },
    createFormJson:function(objarr){
        var itemsa = objarr;
        if(itemsa){
            for(var i=0;i<itemsa.length;i++){
                if(itemsa[i].items!=null){
                    this.createFormJson(itemsa[i].items);
                }
                if(itemsa[i].isNew) {
                    delete itemsa[i].isNew;
                    this.formfieldArr.push(itemsa[i]);
                }
            }
        }
    },

    highlightInvalidNode : function(id) {
//        var n = this.treePanel.getNodeById(id);
        var n = Wtf.getCmp(id);
        this.highlightElement(n._node.fEl.el);
        this.setCurrentNode(n._node, true);
        this.editPanel.startEditing(this.editPanel.store.find('name', "fieldLabel"),1);
    },

       compLabel : function(label , id) {
        if(label.match(fieldLabelRegex)==null || label == 'Label'){
//        if(label == 'Label') {
                msgBoxShow(["Invalid Entry","Please enter valid fieldLabel for selected field"],0);
                this.highlightInvalidNode(id);
                return false;
        } else if(this.labelArr.inArray(label)==true) {
            msgBoxShow(["Invalid Entry","fieldLabel is duplicate,Please enter valid fieldLabel for selected field"],0);
            this.highlightInvalidNode(id);
            return false;
        } else {
            this.labelArr.push(label);
            return true;
        }
    },
    iterateOverTree : function(treenode,configid) {
        if(treenode.childNodes && treenode.childNodes.length>0) {
            if(treenode.elConfig.parentComboId == configid) {
                this.childIds.push(treenode.elConfig.id);
            }
            for(var cnt=0;cnt<treenode.childNodes.length;cnt++) {
                var childN = treenode.childNodes[cnt];
                this.iterateOverTree(childN,configid);
            }
        } else {
            if(treenode.elConfig.parentComboId == configid) {//For Cascading
                this.childIds.push(treenode.elConfig.id);
            }
            if(treenode.elConfig.parentFieldId == configid) {//For read only fields
                this.childFieldIds.push(this.formid+this.strformat(treenode.elConfig.name));
            }
        }
        return;
    },
    checkValidLabel:function(objarr) {
        var itemsa = objarr;
        if(itemsa) {
            for(var cnt=0;cnt<itemsa.length;cnt++) {
                if(itemsa[cnt].items!=null && itemsa[cnt].xtype !='radiogroup'  && itemsa[cnt].xtype !='checkboxgroup') {
                    var rootConf = this.getConfData(itemsa[cnt],true);
                    if(rootConf.fieldLabel) {
                        if(!this.compLabel(rootConf.fieldLabel,rootConf.id))
                            return false;
                    }
                    if(!this.checkValidLabel(itemsa[cnt].items))
                        return false;
                } else {
                    if(itemsa[cnt].fieldLabel) {
                        if(!this.compLabel(itemsa[cnt].fieldLabel,itemsa[cnt].id))
                            return false;
                    }
                    if(itemsa[cnt].isNew) {
                        //                            var n = this.treePanel.getNodeById(itemsa[cnt].id);
                        //                            delete n.elConfig.isNew;
                            var newid ;
                            var xtype = itemsa[cnt].xtype;
                        //                            n.elConfig.id = newid;
                            if (itemsa[cnt].xtype == "combo" || itemsa[cnt].xtype == "select"){
                                newid = this.formid+this.strformat(itemsa[cnt].fieldLabel);
                            }else{
                                newid = this.formid+this.strformat(itemsa[cnt].name);
                            }

                        itemsa[cnt].id = newid;
                        itemsa[cnt].NewObject = true;
                        delete itemsa[cnt].isNew;

                    } else {
                        itemsa[cnt].NewObject = false;
                    }
                    this.childIds = [];
                    this.childFieldIds = [];
                    this.iterateOverTree(this.treePanel.root,itemsa[cnt].id);
                    if(this.childIds.length>0) { //For cascading
                        itemsa[cnt]["childComboId"] = this.childIds;
                    }
                    if(this.childFieldIds.length>0) { //For read only fields
                        itemsa[cnt]["childFieldId"] = this.childFieldIds;
                        itemsa[cnt]["moduleid"] = this.moduleid;
                    }
                    if(itemsa[cnt].id && (itemsa[cnt].id.split("_")[0]==3 || itemsa[cnt].id.split("_")[0]==6))
                        delete itemsa[cnt]['html'];
                    this.formfieldArr.push(itemsa[cnt]);
                }
            }
        }
        return true;
    },

    saveForm : function(filename) {
        this.labelArr = [];
        this.formfieldArr = [];
        var cleanConfig = this.getTreeConfig();
        cleanConfig = (cleanConfig.items?cleanConfig.items[0]||{}:{});
        if(!this.checkValidLabel(cleanConfig.items)) {
            return;
        }
        cleanConfig.successConfig=this.successConfig;
        cleanConfig.submitConfig=this.submitConfig;
        cleanConfig = Wtf.formBuilderMain.JSON.encode(cleanConfig);
        //            var delJson = this.createFormFieldJson(this.deleteNodes);
        //            this.deleteNodes=[];
        var formjson = this.deleteExtraFields();
        mainPanel.loadMask.msg = "Saving Configuration ...";
        mainPanel.loadMask.show();
        Wtf.Ajax.requestEx({
            url: Wtf.req.mbuild+'form.do',
            method: 'POST',
            params: {
                action: 0,
                name: filename,
                jsondata: cleanConfig,
                tbar: this.tBarConf,
                bbar: this.bBarConf,
                formid: this.formid,
                moduleid: this.moduleid,
                formjson: formjson,
                parentmodule:this.parentmodule/*,
                    deljson:  delJson*/
            }},
            this,
            function() {
                msgBoxShow(7,2);
                mainPanel.loadMask.hide();
            //                    this.formStore.load();
            },
            function() {
                msgBoxShow(4,1);
                mainPanel.loadMask.hide();
            }
        );
    },

    deleteForm : function(node, e) {
        var target = e.getTarget();
        if(target.id == "deleteimg_" + node.id) {
            Wtf.Msg.show({
                title: "Delete Form",
                msg: "Are you sure you want to delete the form?",
                buttons: Wtf.Msg.YESNO,
                scope: this,
                fn: function(btn) {
                    if(btn == 'yes') {
                        this.formListPanel.disable();
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.mbuild+'form.do',
                            params: {
                                action: 3,
                                id: node.id
                            }
                        },
                        this,
                        function(resp) {
                            var obj = eval('(' + resp + ')');
                            if(obj.success == true) {
                                    msgBoxShow(8,2);
                                this.formStore.load();
                            } else {
                                    msgBoxShow(9,1);
                            }
                            this.formListPanel.enable();
                        },
                        function() {
                            msgBoxShow(4,1);
                            this.formListPanel.enable();
                        }
                        );
                    }
                }
            });
        }
    },

    onRender: function(config){
        Wtf.formBuilderMain.superclass.onRender.call(this, config);
    },

    strformat:function(str){
        str=str.replace(/\-/g,'');// remove -
        str=str.replace(/\*/g,'');// remove *
        str=str.replace(/\//g,'');// remove /
        str=str.replace(/\./g,'') // remove .
        str = str.replace(/\s{2,}/g," "); // strip concecutive spaces
        str = str.replace(/^\s/,"");//trim leading space
        str = str.toLowerCase();
        // replace spaces with underscore
        str = str.replace(/\s/g,"_");
        return str;
//        return str.trim().toLowerCase().replace(" ","_")
   }


});


/*
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

//Wtf.formBuilderMain = function() {
//};
//
//Wtf.formBuilderMain.prototype = {
//
//};


// modified Wtf.util.JSON to display a readable config
Wtf.formBuilderMain.JSON = new (function(){
    var useHasOwn = {}.hasOwnProperty ? true : false;
    var pad = function(n) {
        return n < 10 ? "0" + n : n;
    };
    var m = {
        "\b": '\\b',
        "\t": '\\t',
        "\n": '\\n',
        "\f": '\\f',
        "\r": '\\r',
        '"' : '\\"',
        "\\": '\\\\'
    };
    var encodeString = function(s){
        if (/["\\\x00-\x1f]/.test(s)) {
            return '"' + s.replace(/([\x00-\x1f\\"])/g, function(a, b) {
                var c = m[b];
                if(c){
                    return c;
                }
                c = b.charCodeAt();
                return "\\u00" +
                Math.floor(c / 16).toString(16) +
                (c % 16).toString(16);
            }) + '"';
        }
        return '"' + s + '"';
    };

    var indentStr = function(n) {
        var str = "", i = 0;
        while (i<n) {
            str += "  ";
            i++;
        }
        return str;
    };

    var encodeArray = function(o, indent){
        indent = indent || 0;
        var a = ["["], b, i, l = o.length, v;
        for (i = 0; i < l; i += 1) {
            v = o[i];
            switch (typeof v) {
                case "undefined":
                case "function":
                case "unknown":
                    break;
                default:
                    if (b) {
                        a.push(',');
                    }
                    a.push(v === null ? "null" : Wtf.formBuilderMain.JSON.encode(v, indent + 1));
                    b = true;
            }
        }
        a.push("]");
        return a.join("");
    };

    var encodeDate = function(o){
        return '"' + o.getFullYear() + "-" +
        pad(o.getMonth() + 1) + "-" +
        pad(o.getDate()) + "T" +
        pad(o.getHours()) + ":" +
        pad(o.getMinutes()) + ":" +
        pad(o.getSeconds()) + '"';
    };

    this.encode = function(o, indent){
        indent = indent || 0;
        if(typeof o == "undefined" || o === null){
            return "null";
        }else if(o instanceof Array){
            return encodeArray(o, indent);
        }else if(o instanceof Date){
            return encodeDate(o);
        }else if(typeof o == "string"){
            return encodeString(o);
        }else if(typeof o == "number"){
            return isFinite(o) ? String(o) : "null";
        }else if(typeof o == "boolean"){
            return String(o);
        }else {
            var a = ["{\n"], b, i, v;
            if (o.items instanceof Array) {
                var items = o.items;
                delete o.items;
                o.items = items;
            }
            for (i in o) {
                if (i === "_node") {
                    continue;
                }
                if(!useHasOwn || o.hasOwnProperty(i)) {
                    v = o[i];
                    if (i === "id" && /^form-gen-/.test(o[i])) {
                        continue;
                    }
                    if (i === "id" && /^ext-comp-/.test(o[i])) {
                        continue;
                    }
                    switch (typeof v) {
                        case "undefined":
                        case "function":
                        case "unknown":
                            break;
                        default:
                            if(b){
                                a.push(',\n');
                            }
                            a.push(indentStr(indent), i, ":",
                                v === null ? "null" : this.encode(v, indent + 1));
                            b = true;
                    }
                }
            }
            a.push("\n" + indentStr(indent-1) + "}");
            return a.join("");
        }
    };

})();

// parse DocRefs
var fields = {};
var fileName;
var infos;
var type;
var desc;
for (fileName in DocRefs) {
    for (key in DocRefs[fileName]) {
        infos = DocRefs[fileName][key];
        if (infos.type == "Function") {
            continue;
        }
        desc = "<i>"+fileName+"</i><br/><b>"+infos.type+"</b> "+infos.desc;
        if (!fields[key]) {
            fields[key] = {
                desc:desc
            };
            if (infos.type == "Boolean") {
                type = "boolean";
            } else if (infos.type == "Number") {
                type = "number";
            } else if (infos.type.match(/Array/)) {
                type = "object";
            } else if (infos.type.match(/Object/)) {
                type = "object";
            } else {
                type = "string";
            }
            fields[key].type = type;
            fields[key].events = infos.events;
        } else {
            fields[key].desc += "<hr/>" + desc;
        }
    }
}
Wtf.apply(fields, {
    xtype  : {
        desc:"",
        type:"string",
        values:'component box viewport panel window dataview colorpalette datepicker tabpanel button splitbutton cycle toolbar tbitem tbseparator tbspacer tbfill tbtext tbbutton tbsplit paging editor treepanel field textfield trigger textarea numberfield datefield combo checkbox radio hidden form fieldset htmleditor timefield grid editorgrid progress'.split(' ')
        },
    region : {
        desc:"",
        type:"string",
        values:'center west north south east'.split(' ')
        },
    hideMode         : {
        desc:"",
        type:"string",
        values:'visibility display offsets'.split(' ')
        },
    msgTarget        : {
        desc:"",
        type:"string",
        values:'qtip title under side'.split(' ')
        },
    shadow           : {
        desc:"",
        type:"string",
        values:'sides frame drop'.split(' ')
        },
    tabPosition      : {
        desc:"",
        type:"string",
        values:'top bottom'.split(' ')
        },
    columnWidth      : {
        desc:"Size of column (0 to 1 for percentage, >1 for fixed width",
        type:"number"
    },
    fieldLabel       : {
        desc:"Label of the field",
        type:"string"
    },
    x                : {
        desc:"X position in pixels (for absolute layouts",
        type:"string"
    },
    y                : {
        desc:"Y position in pixels (for absolute layouts",
        type:"string"
    },
    anchor           : {
        desc:"Anchor size (width) in %",
        type:"string"
    }
});
fields.layout.values = [];
for (i in Wtf.Container.LAYOUTS) {
    fields.layout.values.push(i);
}
fields.vtype.values = [];
for (i in Wtf.form.VTypes) {
    fields.vtype.values.push(i);
}
fields.defaultType.values = fields.defaults.xtype;
Wtf.formBuilderMain.FIELDS = fields;


// custom editors for attributes
Wtf.formBuilderMain.getCustomEditors = function() {
    var g = Wtf.grid;
    var f = Wtf.form;
    var cmEditors = new g.PropertyColumnModel().editors;
    var eds = {};
    var fields = Wtf.formBuilderMain.FIELDS;
    for (i in fields) {
        if (fields[i].values) {
            var values = fields[i].values;
            var data = [];
            for (j=0;j<values.length;j++) {
                data.push([values[j],values[j]]);
            }
            eds[i] = new g.GridEditor(new f.SimpleCombo({
                forceSelection:false,
                data:data,
                editable:true
            }));
        } else if (fields[i].type == "boolean") {
            eds[i] = cmEditors['boolean'];
        } else if (fields[i].type == "number") {
            eds[i] = cmEditors['number'];
        } else if (fields[i].type == "string") {
            eds[i] = cmEditors['string'];
        }
    }
    return eds;
};

Wtf.userFieldsChoice = function(config) {
    this.rendererRecord = Wtf.data.Record.create([{
        name: 'id'
    },{
        name: 'name'
    },{
        name: 'value'
    },{
        name: 'isstatic'
    }
    ]);
    this.rendererJsonReader = new Wtf.data.KwlJsonReader({
        root: "data"
    }, this.rendererRecord);
    this.rendererStore = new Wtf.data.Store({
        reader: this.rendererJsonReader,
        url: Wtf.req.rbuild+'report.do',//mode=0 is user
        method : 'GET',
        baseParams: {
            action: 14
        }
    });
    this.rendererStore.load();
    this.rendererCombo = new Wtf.form.ComboBox({
            emptyText: "< Choose renderer >",
            bodyStyle: 'margin: auto; padding-top: 5px;',
            store:this.rendererStore,
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            typeAhead: true,
            mode: 'local',
            forceSelection: true,
            triggerAction: 'all',
            selectOnFocus: true
    });
    this.rendererCombo.on("select", this.rendererChange, this);
    Wtf.userFieldsChoice.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.userFieldsChoice, Wtf.Window, {
    height : 400,
    width : 700,
    initComponent: function() {
        Wtf.userFieldsChoice.superclass.initComponent.call(this);
        this.summaryCombo = new Wtf.form.ComboBox({
            emptyText: "< Choose summaryType >",
            bodyStyle: 'margin: auto; padding-top: 5px;',
            store:summaryTypeStore,
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            typeAhead: true,
            mode: 'local',
            forceSelection: true,
            triggerAction: 'all',
            selectOnFocus: true
        });
        this.summaryCombo.on("beforeselect",this.isAllowed,this);

        this.reportSM = new Wtf.grid.CheckboxSelectionModel({singleSelect : true});
        var checkBoxRen;
        this.reportCM = new Wtf.grid.ColumnModel([
            this.reportSM, {
                header: "Column",
                dataIndex: "columnname"
            },{
                header: "Display Field",
                dataIndex: "displayfield",
                editor: new Wtf.form.TextField({
                    validateOnBlur: false,
                    validationDelay: 1000
                })
            },{
                header: "renderer",
                dataIndex: "renderer",
                renderer : Wtf.ux.comboBoxRenderer(this.rendererCombo),
                editor: this.rendererCombo
            },{
                header: "Summary Type",
                dataIndex: "summaryType",
                renderer : Wtf.ux.comboBoxRenderer(this.summaryCombo),
                editor: this.summaryCombo
            },{
                header: "xtype",
                dataIndex: "xtype",
                hidden:true
            }, checkBoxRen = new Wtf.grid.CheckColumn({
                header: "Hidden",
                dataIndex: "hidden"
            }),{
                header: "",
                dataIndex: 'type',
                renderer:function(value, css, record, row, column, store){
                    return "<image src='images/up.png' onclick=\"changeseq('"+record.get('seq')+"',0, 'modConfigGrid')\"/>"+
                    "<image src='images/down.png' style='padding-left:5px' onclick=\"changeseq('"+record.get('seq')+"',1, 'modConfigGrid')\"/>";
                }
            }
        ]);
        this.reportRec = Wtf.data.Record.create([
            {name: 'id'},{name: "columnname"},{name: "displayfield"},{name: "hidden"},{name: 'seq'},{name: 'renderer'},{name:'summaryType'},{name:'xtype'}]);

        this.reportStore = new Wtf.data.Store({
            url: Wtf.req.mbuild+"form.do",
            baseParams: {
                action : 11,
                moduleid : this.moduleid
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, this.reportRec)
        });

        this.reportGrid = new Wtf.grid.EditorGridPanel({
            cm: this.reportCM,
            id : 'modConfigGrid',
            store: this.reportStore,
            clicksToEdit :1,
            sm: this.reportSM,
            plugins:[checkBoxRen],
            border: true,
            viewConfig: {
                forceFit: true
            }
        });

        this.reportGrid.on("cellclick", this.deleteRow, this);
        this.reportGrid.on("validateedit", this.validateedit, this);
    },

     validateedit:function(e){
        if (e.field == 'displayfield'){
            return validateedit(e);
        }
    },
    isAllowed:function(combo,record,index){
         return isAllowed(combo,record,index,this.reportGrid.getSelectionModel().getSelected().data.xtype);
    },

    comboBoxRenderer : function(combo) {
        return function(value) {
            var idx = combo.store.find(combo.valueField, value);
            if(idx == -1)
                return "";
            var rec = combo.store.getAt(idx);
            if (rec.get(combo.displayField) == 'None'){
                return rec.get(combo.displayField);
            }else{
                return "<a href='#' class='rendererClass'>"+rec.get(combo.displayField)+"</a>";
            }
        };
    },

    rendererChange: function(cObj, rec, index){
        if(rec.data.id == '0' && rec.data.name != 'None'){
           this.newRendererWin('new');
        }
    },

    newRendererWin: function(type){

        var editButtondisabled=false;
        var rendererId="";
        if (type == 'new'){
            this.rendererValue="";
            this.rendererNameValue="";

        }else{
            var idx = this.rendererCombo.store.find(this.rendererCombo.valueField, this.reportGrid.getSelectionModel().getSelected().data.renderer);
            if(idx == -1){
                this.rendererValue= "";
            }else{
                var rec = this.rendererCombo.store.getAt(idx);
                this.rendererValue= rec.get("value");
                this.rendererNameValue=rec.get(this.rendererCombo.displayField);
                editButtondisabled=rec.get("isstatic");
                rendererId=this.reportGrid.getSelectionModel().getSelected().get("renderer");
            }
        }

        this.rendererObj=new Wtf.moduleRenderer({
            rendererNameValue:this.rendererNameValue,
            rendererValue:this.rendererValue,
            type:type,
            rendererId :rendererId,
            editButtondisabled:editButtondisabled
        });
        this.rendererObj.show();
        this.rendererObj.on('updateRendererStore',this.updateRendererStore,this);
    },

    updateRendererStore:function(type,rendererId,name,value){
        var rendererRecord = new this.rendererRecord({
            id: rendererId,
            name: name,
            value:value,
            isstatic:'false'
        });

        if (type == 'new'){
            this.rendererStore.add(rendererRecord);
        }else{
            this.rendererStore.insert(this.rendererStore.find('id',rendererId),rendererRecord);
            this.reportGrid.getSelectionModel().getSelected().set("renderer",'0');
        }
        this.reportGrid.getSelectionModel().getSelected().set("renderer",rendererId);
    },

    deleteRow: function(gObj, ri, ci, e){
        var event = e;
        if(event.target.className == 'rendererClass') {
            this.newRendererWin('edit');
        }
    },

    onRender: function(config){
        Wtf.userFieldsChoice.superclass.onRender.call(this, config);
        this.add({
            border: false,
            layout : 'fit',
            items :this.reportGrid,
            buttonAlign: 'center',
            buttons:[{
                text: WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                handler: this.requestToSaveConf,
                scope: this
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                handler: function(){
                   this.close();
                },
                scope: this
            }]
        });

        this.reportStore.load();
    },

    getJsonFromRecord : function(record, cnt) {
        var jsonData = "{";
        var dataObj = record.data;
        for (var dataIndex in dataObj) {
            jsonData += dataIndex+":\""+record.data[dataIndex]+"\",";
        }
        jsonData = jsonData.substr(0,jsonData.length-1)+"}";
        return(jsonData);
    },

    requestToSaveConf : function(){
        var jsonData = "[";
        var recCnt = this.reportStore.getCount();
        for(var cnt = 0; cnt < recCnt; cnt++) {
            var record = this.reportStore.getAt(cnt);
            jsonData += this.getJsonFromRecord(record,cnt) + ",";
        }
        jsonData = jsonData.substr(0, jsonData.length - 1) + "]";
        Wtf.Ajax.requestEx({
            url: Wtf.req.mbuild+'form.do',
            params: {
                action: 13,
                jsondata: jsonData,
                moduleid : this.moduleid
            }},
            this,
            function() {
                msgBoxShow(14,2);
                this.close();
            },
            function() {
                msgBoxShow(4,1);
        });
    }
});
Wtf.validatorComponent=function(config){
    Wtf.apply(this,config);
    this.addEvents({
            "okClicked": true
    });
     this.buttons = [{
        text: 'OK',
        scope: this,
        handler: function() {
            if (this.validatorFunction.validate()){
                this.fireEvent("okClicked",this.validatorFunction.getValue());
                this.close();
            }
        }
    },{
            text:"Cancel",
            scope:this,
            handler:function(){
                this.close();
            }
    }];

    this.validatorPanel = new Wtf.Panel({
        layout: 'fit',
        border: false,
        items: this.inP = new Wtf.Panel({
            layout: 'border',
            border: false,
            items: [{
                region: 'north',
                border: false,
                height: 70,
                cls:'windowHeader',
                html:getHeader('images/createuser.gif','Validator function ','Specify details')
            },{
                region: 'center',
                bodyStyle:"background:#f1f1f1;",
                border: false,
                items: [
                    this.addForm = new Wtf.form.FormPanel({
                    url: "jspfiles/admin/feedback.jsp",
                    region: "center",
                    bodyStyle: "padding: 10px;",
                    border: false,
                    labelWidth: 120,
                    buttonAlign: 'right',
                    items: [
                    this.validatorFunction=new Wtf.form.TextArea({
                        fieldLabel: 'Field Label ',
                        allowBlank:false,
                        width: 240,
                        height: 110,
                        value:this.functionValue
                    })

                    ]
                })
                ]
            }
            ]
        })
    });

   this.items=[this.validatorPanel];
   Wtf.validatorComponent.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.validatorComponent, Wtf.Window, {
    title:this.title,
    layout:'fit',
    iconCls: 'winicon',
    modal: true,
    height:300,//220
    width: 400,
    scope: this
});
