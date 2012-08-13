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
Wtf.customizeHeader= function(config){
    Wtf.customizeHeader.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.customizeHeader,Wtf.Window, {
    resizable: false,
    scope: this,
    layout: 'border',
    modal:true,
    width: 700,
    height: 550,
    iconCls: 'pwnd favwinIcon',
    id: 'crm_customize_header',
    title: WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.customizeheader"),//'Customize Header',
    initComponent: function() {
        Wtf.customizeHeader.superclass.initComponent.call(this);
        this.addEvents("aftersave");
        this.isEdit=false;
        this.addButton(WtfGlobal.getLocaleText("crm.CLOSEBUTTON"), function(){
            this.close();
        },this);
    },
    onRender: function(config) {
        Wtf.customizeHeader.superclass.onRender.call(this, config);

         var Contact=[
            [this.modulename]
           ];

        this.columnDs=new Wtf.data.SimpleStore({
             fields:[{
                name: 'modulename'
            }]
         });

        this.columnDs.loadData(Contact);

        this.moduleColumn = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.masterconfig.modulename.column"),//"Module Name",
                dataIndex:"modulename"
            }
        ]);


        this.moduleGrid = new Wtf.grid.GridPanel({
            sm:this.moduleSm = new Wtf.grid.RowSelectionModel(),
            region:"west",
            width:200,
            hidden:true,
            store:this.columnDs,
            sortable:true,
            cm:this.moduleColumn,
            loadMask:true,
            viewConfig:{
                forceFit:true
            }

        });


        this.moduleHeaderRec = new Wtf.data.Record.create([
            {name:"id"},
            {name:"header"},
            {name:"headerid"},
            {name:"newheader"},
            {name:"ismandotory"},
            {name:"pojoname"},
            {name:"xtype"},
            {name:"columntype"},
            {name:"required"}
        ]);

        this.moduleHeaderReader = new Wtf.data.KwlJsonReader({
            root:"data",
            totalProperty:"count"
        },this.moduleHeaderRec);

        this.moduleHeaderStore = new Wtf.data.GroupingStore({
            url: "Common/CRMCommon/getColumnHeader.do",
            reader:this.moduleHeaderReader,
            baseParams:{
                flag:35,
                modulename:this.modulename
            },
            groupField:"columntype",
            sortInfo: {
                field: 'header',
                direction: "ASC"
            }
        });
        this.moduleHeaderStore.load();
        this.checkColumn = new Wtf.SpreadSheet.CheckColumn({
           header: WtfGlobal.getLocaleText("crm.isMandatory")+'?',//"Is Mandatory?",
           dataIndex: 'ismandotory',
           width: 65,
           modulename:this.modulename,
           editedRecord:this.editedRecord,
           isEdit:this.isEdit,
           scope:this
        });

        this.moduleHeaderColumn = new Wtf.grid.ColumnModel([
            {
                header:WtfGlobal.getLocaleText("crm.custom"),//"Custom",
                hidden:true,
                fixed:true,
                dataIndex:"columntype"
 
            },{
                header:WtfGlobal.getLocaleText("crm.HEADERS"),//"Header",
                dataIndex:"header",
                groupRenderer: WtfGlobal.nameRenderer

            },{
                header:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.customizeheader"),//"Customize Header",
                dataIndex:"newheader",
                editor: new Wtf.ux.TextField({
                     allowBlank: true,
                     maskRe:/[^,]+/
                })
            }
            ,this.checkColumn
        ]);
        this.groupingView = new Wtf.grid.GroupingView({
            forceFit: true,
            showGroupName: false,
            enableGroupingMenu: true,
            groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ?"'+WtfGlobal.getLocaleText("crm.item.plural")+'":"'+WtfGlobal.getLocaleText("crm.item")+'"]})',//"Items" : "Item"]})',
            hideGroupedColumn: true
        });
            this.moduleHeaderGrid = new Wtf.grid.EditorGridPanel({
            plugins:this.checkColumn,
            store:this.moduleHeaderStore,
            region:"center",
            loadMask:true,
            cm:this.moduleHeaderColumn,
            view: this.groupingView
        });
        this.moduleHeaderGrid.on("validateedit",function(e){
            if(e.value.length>50){
                WtfComMsgBox(["Alert",Wtf.MaxLengthText+50]);
                return false;
            }else{
                return true;
            }
        },this);
        this.moduleHeaderGrid.on("beforeedit",function(e){
            var recdata = e.record.data;
               this.editedRecord = e.record;
            var newheader = recdata.newheader;
            this.oldValue=newheader
            if(typeof e.value=="string" ){
               var val = WtfGlobal.HTMLStripper(e.value);
               e.record.data[e.field]=val;
            }
        },this);
       this.moduleHeaderGrid.on("afteredit",function(e){
            var recdata = e.record.data;
            this.editedRecord = e.record;
            var newheader = (recdata.newheader).replace(/\s{2,}/," ");
            newheader = WtfGlobal.replaceAll(newheader, ",", "")
            var oldheader = recdata.header;
            var pojoname = recdata.pojoname;
            var xtype = recdata.xtype;
            var id = recdata.id;
            var headerid = recdata.headerid;

            WtfGlobal.setAjaxReqTimeout();
            Wtf.Ajax.requestEx({
                url: "Common/CRMCommon/saveColumnHeader.do",
                params: {
                    flag: 34,
                    newheader:newheader,
                    oldheader:oldheader,
                    modulename:this.modulename,
                    isMandatory:recdata.ismandotory,
                    pojoname:pojoname,
                    xtype:xtype,
                    id:id,
                    headerid:headerid
                }
            }, this, function(action, response){
                WtfGlobal.resetAjaxReqTimeout();
                if(action.success){
                    if(action.id!=null){
                        this.editedRecord.set("id", action.id);
                        this.editedRecord.commit();
                    }
                    this.fireEvent("aftersave",this.id,true);
                    this.isEdit=true;
                }else{
                    Wtf.MessageBox.alert("Duplicate",action.msg);
                    this.editedRecord.set("newheader",this.oldValue);
                    this.editedRecord.commit();
                }

            }, function(action, response){
                WtfGlobal.resetAjaxReqTimeout();
            });

        },this);


        this.add(

            {
                region : 'north',
                height : 100,
                border : false,
                id:'resolveConflictNorth_panel_Overrite',
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html : getTopHtml(WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.customizeheader"), WtfGlobal.getLocaleText("crm.spredsheet.customizeheaderwin.tophtml"),"../../images/customize-header-popup.jpg")
            },this.moduleGrid,this.moduleHeaderGrid

        );
        this.moduleHeaderGrid.on("afteredit", this.afterEdit, this);
        
    },
    afterEdit:function(e){
        var rec = e.record;
        if (e.field=='newheader'){
           rec.data[e.field]=WtfGlobal.HTMLStripper(rec.data.newheader)
        }
    }
});

Wtf.SpreadSheet.CheckColumn = function(config){
    Wtf.apply(this, config);
    if(!this.id){
        this.id = Wtf.id();
    }
    this.renderer = this.renderer.createDelegate(this);
};

Wtf.SpreadSheet.CheckColumn.prototype ={
    init : function(grid){
        this.grid = grid;
        this.grid.on('render', function(){
            var view = this.grid.getView();
            view.mainBody.on('mousedown', this.onMouseDown, this);
        }, this);
    },

    onMouseDown : function(e, t){
        if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
            e.stopEvent();
            var index = this.grid.getView().findRowIndex(t);
            var record = this.grid.store.getAt(index);
            if(record.data.ismandotory && record.data.required){
                WtfComMsgBox(112, 0);
                return false;
            }else {
                Wtf.MessageBox.show({
                            title:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.customizeheader.validation"),// "Validation Check",
                            msg: WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.customizeheader.validation.msg"),//"This action will mark all the previously valid records which don't satisfy the new conditions as invalid. Do you want to continue ?",
                            buttons: Wtf.MessageBox.YESNO,
                            animEl: 'mb9',
                            scope:this,
                            icon: Wtf.MessageBox.INFO,
                            fn:function(btn,text){
                                if(btn=="yes"){
                                    this.savedata();
            } else {
                                    this.editedRecord.set("ismandotory",!record.data.ismandotory);
                                    return false;
                        }
                    }
                        });
                    this.editedRecord = record;
                    this.editedRecord.set(this.dataIndex, !record.data[this.dataIndex]);

                }
            }
    },

    savedata : function(){
             WtfGlobal.setAjaxReqTimeout();
             Wtf.commonWaitMsgBox("Validating records...");
        Wtf.Ajax.requestEx({
            url: "Common/CRMCommon/saveColumnHeader.do",
            params: {
                flag: 34,
                id:this.editedRecord.get("id"),
                newheader:this.editedRecord.get("newheader"),
                oldheader:this.editedRecord.get("header"),
                modulename:this.modulename,
                isMandatory:this.editedRecord.get("ismandotory"),
                pojoname:this.editedRecord.get("pojoname"),
                xtype:this.editedRecord.get("xtype"),
                    headerid : this.editedRecord.get("headerid")
            }
        }, this, function(action, response){
                WtfGlobal.resetAjaxReqTimeout();
            if(action.success){
                if(action.id!=null){
                    this.editedRecord.set("id", action.id);
                    this.editedRecord.commit();
                }
                    this.scope.fireEvent("aftersave",this.id,true);
            }
            this.isEdit=true;
                Wtf.updateProgress();
            },function() {
                Wtf.updateProgress();
                WtfGlobal.resetAjaxReqTimeout();
        });
    },

    renderer : function(v, p, record){
        p.css += ' x-grid3-check-col-td';
        return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
    }
};



