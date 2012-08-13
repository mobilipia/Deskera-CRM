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
Wtf.newCommentWindow = function(config) {
//    Wtf.apply(this, config);
    Wtf.newCommentWindow.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.newCommentWindow, Wtf.Window, {
    initComponent: function() {
        Wtf.newCommentWindow.superclass.initComponent.call(this);
        this.addEvents({
            "success": true
        });
    },

    onRender: function(config){
        Wtf.newCommentWindow.superclass.onRender.call(this, config);

        this.add({
            border: false,
            layout : 'border',
            items :[{
                region: 'north',
                border:false,
                height:75,
                cls:'windowHeader',
                html: getHeader('images/createuser.png', WtfGlobal.getLocaleText("crm.activitydetailpanel.addCommentBTN"), WtfGlobal.getLocaleText("crm.activitydetailpanel.addComment.html"))
            },{
                region: 'center',
                autoScroll: true,
                bodyStyle : 'background:#f1f1f1;font-size:10px;',
                border:false,
                items: [{
                    border:false,
                    layout:'form',
                    bodyStyle:'padding:13px 13px 13px 13px',
                    labelWidth:80,
                    items: [
                        this.commentVal = new Wtf.form.TextArea({
                            fieldLabel:WtfGlobal.getLocaleText("crm.case.defaultheader.comment")+'*',//'Comment*',
                            anchor:'93%',
                            allowBlank: false,
                            msgTarget: 'side',
                            height: 70
                        })
                    ]
                 }]
           }],
            buttonAlign: 'center',
            buttons:[{
                text: WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                handler: function() {
                    if (this.commentVal.validate()){
                        Wtf.Ajax.requestEx({
                            url: 'reportbuilder.jsp',
                            method: 'POST',
                            params: {
                                action: 12,
                                moduleid : this.moduleid,
                                recordid : this.recordid,
                                comment : ScriptStripper(HTMLStripper(this.commentVal.getValue()))
                            }
                            },
                        this,
                        function(resp) {
                            var resultObj = eval('('+resp+')');
                            if(resultObj.success) {
                                msgBoxShow(["Success", resultObj.msg],2);
                                this.gridstore.reload();
                                if (this.commentStore != undefined){
                                    this.commentStore.reload();
                                }
                            } else {
                                msgBoxShow(28,1);
                            }
                        },
                        function() {
                            msgBoxShow(4,1);
                        }
                        );
                        this.close();
                    }
                },
                scope: this
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                handler: function(){
                   this.close();
                },
                scope: this
            }]
        });
    }
});

Wtf.commentsComponent = function(config){

     this.events = {
        "updateModuleStore": true
    };

    Wtf.apply(this, config);
//    var delcommperm = eval('('+this.permsObj[4][6]+')');
    var delcommperm = eval('('+this.permsObj[Wtf.mbuild.permActions.deleteComment]+')');
    if(checktabperms(delcommperm.permgrid,delcommperm.perm)) {
        this.cm=new Wtf.grid.ColumnModel([{
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.comment"),// "Comment",
                dataIndex:'comment'
            },{
                header:WtfGlobal.getLocaleText("crm.profileview.comment.addedby"),// "Added By",
                dataIndex:'addedBy'
            },{
                header:WtfGlobal.getLocaleText("crm.audittrail.header.time"),// "Time",
                dataIndex:'Time'
            },{
                header: WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
                dataIndex:'delField',
                renderer : function(val) {
                    return "<img id='DeleteImg' class='delete' src='images/Cancel.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete comment'></img>";
                }
            }]);
    } else {
        this.cm=new Wtf.grid.ColumnModel([{
                header: WtfGlobal.getLocaleText("crm.case.defaultheader.comment"),//"Comment",
                dataIndex:'comment'
            },{
                header: WtfGlobal.getLocaleText("crm.profileview.comment.addedby"),//"Added By",
                dataIndex:'addedBy'
            },{
                header:WtfGlobal.getLocaleText("crm.audittrail.header.time"),// "Time",
                dataIndex:'Time'
            }]);
    }

   this.commentRecord = Wtf.data.Record.create([{
        name: 'id'
    },{
        name: 'comment'
    },{
        name: 'addedBy'
    },{
        name: 'Time'
    },{
        name: 'recordId'
    }
    ]);
    this.commentJsonReader = new Wtf.data.KwlJsonReader({
        root: "data",
        totalProperty: 'count',
        remoteGroup:true,
        remoteSort: true
    }, this.commentRecord);
    this.commentStore = new Wtf.data.Store({
        reader: this.commentJsonReader,
        url: 'reportbuilder.jsp',
        method : 'POST',
        baseParams: {
            action: 17,
            recordId:this.recordId
        }
    });

    this.quickPanelSearch = new Wtf.KWLTagSearch({
        width: 150,
        emptyText: WtfGlobal.getLocaleText("crm.common.entername"),//'Enter name',
        field: "name"
    });

    this.pg = new Wtf.PagingSearchToolbar({
        pageSize: 15,
        searchField: this.quickPanelSearch,
        store: this.commentStore,
        displayInfo: true,
        displayMsg: 'Displaying items {0} - {1} of {2}',
        emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//"No items to display",
        plugins: this.pP = new Wtf.common.pPageSize({})
    });

    this.commentStore.on('load', function(store) {
        this.quickPanelSearch.StorageChanged(store);
    }, this);

    this.commentStore.on('datachanged', function() {
        var p = this.pP.combo.value;
        this.quickPanelSearch.setPage(p);
    }, this);
    
//    var addcommperm = eval('('+this.permsObj[3][5]+')');
    var addcommperm = eval('('+this.permsObj[Wtf.mbuild.permActions.addComment]+')');
    this.commentGrid=new Wtf.grid.GridPanel({
        store: this.commentStore,
        cm:this.cm,
        stripeRows: true,
        width:300,
        region : 'center',
        border:false,
        viewConfig: {
            forceFit:true
        },
        sm: new Wtf.grid.RowSelectionModel,
        tbar: ['Quick Search: ',this.quickPanelSearch,{
                    text : WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN"),//"Advanced Search",
                    scope : this,
                    tooltip:WtfGlobal.getLocaleText("crm.common.advancedsearchtip"),//'Manage search with your preferance',
                    handler : this.configurAdvancedSearch,
                    iconCls : 'pwnd editicon'
             },{
            text: WtfGlobal.getLocaleText("crm.activitydetailpanel.addComment.html"),//"Add New Comment",
            scope : this,
            handler: this.addComment,
            hidden:!checktabperms(addcommperm.permgrid,addcommperm.perm)
        }],
        bbar:this.pg
    });

    this.commentGrid.on("render",function(){
        this.commentStore.load({
            params:{
                start:0,
                limit:this.pP.combo.value
            }
            });
    },this)

    this.commentGrid.on('cellclick',this.deleteComment,this);

    this.objsearchComponent=new Wtf.mbuild.advancedSearchComponent({
        moduleid:0,
        getComboDataAction: 21
    });
    this.objsearchComponent.on("filterStore",this.filterStore, this);
    this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);

    Wtf.commentsComponent.superclass.constructor.call(this, {
            border: false,
            id:'Comment'+this.recordId,
            title :WtfGlobal.getLocaleText("crm.common.comments"),// "Comments",
            enableTabScroll: true,
            closable : true,
            layout:'border',
            width:400,
            items:[this.commentGrid,this.objsearchComponent]
    });
}
Wtf.extend(Wtf.commentsComponent, Wtf.Panel, {
    addComment:function(){
            var  commentWin = new Wtf.newCommentWindow({
                title:WtfGlobal.getLocaleText("crm.activitydetailpanel.addComment.html"),//"Add New Comment",
                closable:true,
                border:false,
                modal:true,
                width : 340,
                height: 250,
                iconCls : 'win',
                layout: "fit",
                recordid : this.recordId,
                moduleid : this.moduleScope.moduleid,
                resizable: false,
                gridstore:this.moduleScope.modulegrid.store,
                commentStore:this.commentStore
            });
            commentWin.show();
            commentWin.on("groupokclicked", function(){
                alert("fired");
            }, this);

    },

    deleteComment:function(gd, ri, ci, e){
        var event = e;
        if(event.target.className == "delete") {
            var id =gd.getStore().getAt(ri).data.id;
            if (id != undefined && id!=""){
                Wtf.Ajax.requestEx({
                    url: 'reportbuilder.jsp',
                    method:'POST',
                    params: {
                        action: 18,
                        commentid: id
                    }
                },
                this,
                function(resp) {
                    var resultObj = eval('('+ resp +')');
                    if(resultObj.success) {
                        msgBoxShow(['Info', "Record deleted successfully"], Wtf.MessageBox.OK);
                        this.fireEvent("updateModuleStore");
                        gd.getStore().remove(gd.getStore().getAt(ri));
                    } else {
                        msgBoxShow(['Info', "Record not deleted successfully"], Wtf.MessageBox.Error);
                    }
                },
                function() {
                    msgBoxShow(['Info', "Error occured at server"], Wtf.MessageBox.Error);
                }
                );

            }
        }
    },
    configurAdvancedSearch:function(){
        this.fields = [];
        var item={};
        item['dataIndex']='comment';
        item['header']='comment';
        item['xtype']='textfield';
        this.fields.push(item);
        item={};
        item['dataIndex']='addedby';
        item['header']='Added By';
        item['xtype']='userscombo';
        this.fields.push(item);
        item={};
        item['dataIndex']='createddate';
        item['header']='Time';
        item['xtype']='datefield';
        this.fields.push(item);

        this.objsearchComponent.show();
        this.doLayout();
        this.objsearchComponent.columnStore.loadData(this.fields);
    },
    filterStore:function(Json){
        this.commentStore.reload({
            params: {
                start:0,
                limit:this.pP.combo.value,
                filterJson:Json
            }
            });
    },
    clearStoreFilter:function(){
        this.objsearchComponent.hide();
        this.doLayout();
        this.commentStore.reload({
            params: {
                start:0,
                limit:this.pP.combo.value
            }
            });
    }
});

