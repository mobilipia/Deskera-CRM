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
Wtf.emailType = function (config){
    Wtf.emailType.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.emailType,Wtf.Panel,{
    closable:true,
    layout : 'fit',
    border:false,
    iconCls:"pwndnewCRM emailtemplateTabicon",
    initComponent: function(config) {
        Wtf.emailType.superclass.initComponent.call(this,config);
        this.getEditorGrid();
    },

    getEditorGrid : function () {
        var Rec = new Wtf.data.Record.create([
            {name:'templateid'},
            {name:'templatename'},
            {name:'description'},
            {name:'subject'},
            {name:'bodyhtml'},
            {name:'plaintext'},
            {name:'createdon',dateFormat:'time',type:'date'},
            {name:'templateclass'}
        ]);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },Rec);

        this.EditorStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getEmailTypeList.do',
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            baseParams:{
                flag:1
            },
            method:'post',
            reader:EditorReader
        });
        this.EditorStore.load();
        this.EditorColumn = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.emailtype.header.emailtype"),//'Email Type',
                dataIndex:'templatename',
                dbname:'c.name',
                renderer : function(val) {
                    return "<a href = '#' class='editEmailType'> "+val+"</a>";
                }
            },{
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//'Subject',
                dataIndex:'subject'
            },{
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.desc"),//'Description',
                dataIndex:'description'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon"),//'Created On',
                dataIndex:'createdon',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ,
                dbname:'c.createdOn'
            }]);

        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.Grid = new Wtf.KwlGridPanel({
            layout:'fit',
            store: this.EditorStore,
            cm: this.EditorColumn,
            sm : this.selectionModel,
            displayInfo: true,
            border : false,
            height:400,
            loadMask : true,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.emailtype.emtygrid.watermark"))//"No email type created till now")
            },
            searchEmptyText:WtfGlobal.getLocaleText("crm.emailtype.searchtext"),//"Search by Email Type",
            serverSideSearch : true,
            searchField:"templatename",
            tbar:[]
        });
            this.templatePanel= new Wtf.Panel({
                title:WtfGlobal.getLocaleText("crm.emailtype.templatepanel.title"),//"Email Types",
                iconCls:"pwndnewCRM emailtemplateTabicon",
                border:false,
                id:this.id+'emailtypepan',
                layout:'fit',
                items:[{
                    layout:'fit',
                    border:false,
                    items:[this.Grid]
                }
                ]
            });

             this.mainTab=new Wtf.TabPanel({
               id:this.id+"emailtypetabPanel",
               scope:this,
               border:false,
               resizeTabs: true,
               minTabWidth: 155,
               enableTabScroll: true,
               items:[this.templatePanel]
            });
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.templatePanel);

        this.Grid.on("cellclick",this.gridCellClick,this);
    },

    gridCellClick:function(Grid,rowIndex,columnIndex, e){ 
        var event = e ;
        if(event.getTarget("a[class='editEmailType']")) {
            var recdata = Grid.getSelectionModel().getSelected().data;
            var panel = Wtf.getCmp('template_wiz_win'+recdata.templateid);
            var tipTitle=recdata.templatename+" : Edit Template";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
                if(panel==null) {
                    panel=new Wtf.newEmailTemplate({
                        emailtype:true,
                        templateid : recdata.templateid,
                        tname : recdata.templatename,
                        tdesc : recdata.description,
                        templateClass :recdata.templateclass,
                        tsubject : recdata.subject,
                        tbody : recdata.bodyhtml,
                        plaintext : recdata.plaintext,
                        store: this.EditorStore,
                        title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Email Type'>"+title+"</div>",
                        tipTitle:tipTitle
                    });
                    this.mainTab.add(panel);
                }
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();

        }
    }
});
