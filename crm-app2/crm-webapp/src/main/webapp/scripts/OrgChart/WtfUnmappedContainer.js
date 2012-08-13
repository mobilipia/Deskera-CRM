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
Wtf.UnmappedContainer = function(config) {
    Wtf.apply(this, config);

    this.selectedUnMappedNode = "";

    Wtf.UnmappedContainer.superclass.constructor.call(this);
};

Wtf.extend(Wtf.UnmappedContainer, Wtf.Panel, {

    initComponent : function(){
        Wtf.UnmappedContainer.superclass.initComponent.call(this);
    },

    onRender : function(ct){
        Wtf.UnmappedContainer.superclass.onRender.call(this, ct);

        this.unmappedRec = Wtf.data.Record.create([
                {name: 'projid'},
                {name: 'userid'},
                {name: 'image'},
                {name: 'fname'},
                {name: 'lname'},
                {name: 'designation'}
        ]);

        this.unMappedNodeStore = new Wtf.data.Store({
            url: 'Common/OrganizationChart/getUnmappedUsers.do',
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            }, this.unmappedRec)
        });

        this.unMappedNodeStore.on("load", this.renderUnmappedNodes, this);
        this.unMappedNodeStore.load({
            params: {
                action: 1
            }
        });
    },

    reloadUnmappedContainer: function() {
        this.body.dom.innerHTML = "";
        this.unMappedNodeStore.reload();
    },

    renderUnmappedNodes: function(obj, rec, opt) {

        var displayName = "";
        var displayDesignation = "";
        var totalStore = rec.length;

        if(totalStore > 0) {
            for(var cntStore = 0; cntStore < totalStore; cntStore++) {
                displayName = rec[cntStore].data.fname + " " + rec[cntStore].data.lname;
                var displayNameEllipsis = Wtf.util.Format.ellipsis(displayName,20);
                if(Wtf.isIE6 || Wtf.isIE7){
                    displayNameEllipsis = Wtf.util.Format.ellipsis(displayName,10);
                }
                displayDesignation = rec[cntStore].data.designation != "" ? rec[cntStore].data.designation : "-";
                var displayDesignationEllipsis = Wtf.util.Format.ellipsis(displayDesignation,8);
                var unmappedNode = new Wtf.ChartNode({
                    autoscroll: true,
                    blankString: "",
                    blockId: "blockUnMapped_",
                    nodeid : rec[cntStore].data.userid,
                    layout: 'fit',
                    mainDivStyle: " blockUnMapped ",
                    nodeClass : "blocknodeUnmapped",
                    nodeName: displayNameEllipsis,
                    nodeNameTip: displayName,
                    nodeDesignation: displayDesignationEllipsis,
                    nodeDesignationTip: displayDesignation,
                    projid: this.projid,
                    renderTo: this.body.id
                });

                unmappedNode.on("unMappedNodeDblClick", function(unmappedNode, e, flag) {
                    var MainPanel = Wtf.getCmp(this.projid + "unmappedContainer");
                    MainPanel.selectedUnMappedNode = unmappedNode.nodeid;
                    MainPanel.addTo();
                });

                unmappedNode.on("mapClicked", function(unmappedNode, e, flag) {
                    var MainPanel = Wtf.getCmp(this.projid + "unmappedContainer");
                    MainPanel.selectedUnMappedNode = unmappedNode.nodeid;
                    MainPanel.addTo();
                });

                this.makeUnmappedDraggable(rec[cntStore].data.userid);
            }
            this.doLayout();
        }
    },

    addTo: function() {
            var checkBoxSM = new Wtf.grid.CheckboxSelectionModel({
                singleSelect: true
            });

            this.cmodel = new Wtf.grid.ColumnModel([checkBoxSM, {
                header: "",
                width: 30,
                dataIndex:'image',
                renderer: this.pic
            },{
                header: "First name",
                dataIndex: 'fname'
            },{
                header: "Last name",
                dataIndex: 'lname'
            },{
                header: "Roles",
                width: 70,
                dataIndex: 'designation'
            }]);

            var mappedStore = Wtf.getCmp(this.projid + "chartContainer").mappedNodeStore;

            this.UnmapGrid = new Wtf.grid.GridPanel({
                ds: mappedStore,
                cm: this.cmodel,
                sm: checkBoxSM,
                autoScroll: true,
                layout: 'fit',
                height: 300,
                viewConfig: {
                    forceFit: true
                },
                border: false,
                autoWidth: true,
                loadMask: { msg: 'Loading...' },
                bbar:[
                    this.addNode = new Wtf.Toolbar.Button({
                        text: "Add Node",
                        scope: this,
                        handler: this.addUserTo
                    }),

                    new Wtf.Toolbar.Button({
                        text: "Cancel",
                        scope: this,
                       handler: this.cancelAddTo
                    })
                ]
            });

            this.buttonform = new Wtf.Panel({
                items: [{
                    region: 'north',
                    layout: 'fit',
                    items: [this.UnmapGrid]
                }]
            });

            this.addToWindow = new Wtf.Window({
                title: "Add",
                modal: true,
                layout: 'fit',
                height: 340,
                resizable: false,
                width: 400,
                items: [ this.buttonform ]
            });
        
        this.addToWindow.show();
    },

    pic: function(path) {
        if(path == '')
            path = '../../images/defaultuser.png';
        return '<img src= ' + path + ' height= "20px" width= "20px"></img>';
    },

    cancelAddTo: function() {
        if(this.addToWindow)
            this.addToWindow.close();
    },

    addUserTo: function() {

        if(this.UnmapGrid.selModel.selections.length < 1)
            Wtf.MessageBox.alert("Error", "Please select a node to add the unmapped node.");

        else {
            var selectNode = this.UnmapGrid.selModel.selections.items[0].data;

            var level = parseFloat(selectNode.level) + 1;

            var selectedRec = this.unMappedNodeStore.query("userid", this.selectedUnMappedNode);

            this.insertNewNode(selectedRec.items[0].data.userid, selectNode.nodeid, level);
        }
    },

    insertNewNode: function(userid, parentid, level) {
        Wtf.Ajax.requestEx({
            method: 'GET',
            url: "Common/CRMCommon/insertNode.do",
            params: ({
                action: 2,
                userid: userid,
                fromId: parentid,
                level: level
            })
            },
        this,
        function(result, req) {
            var data = eval( '(' + result.data + ')');
                    
            if(!data.success){
                if(data.msg){
                    ResponseAlert(["Error", data.msg]);
                }
                else{
                    ResponseAlert(["Error", "Error occurred while inserting node."]);
                }
                var node = Wtf.get("node_" + parentid);
                node.removeClass("chartDragClass");
            }
            else {
                ResponseAlert(["Success", "Node successfully inserted."]);
                this.reloadUnmappedContainer();
                var chartContainer = Wtf.getCmp(this.projid + "chartContainer");
                chartContainer.reloadChartContainer();
            }

            if(this.addToWindow)
                this.addToWindow.close();
        },
        function(result, req) {
            ResponseAlert(["Error", "Error occurred while connecting to server."]);
            if(this.addToWindow)
                this.addToWindow.close();
        });
    },
 
    makeUnmappedDraggable: function(userid) {
        var blockId = "blockUnMapped_" + userid;
        var node = Wtf.get(blockId);
        node.dd = new Wtf.ChartDDProxy(blockId, "group");
    }
});
