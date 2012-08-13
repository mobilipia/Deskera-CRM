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
Wtf.campaignViewReport = function (config){
    Wtf.campaignViewReport.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.campaignViewReport,Wtf.Panel,{
    closable:true,
    layout : 'fit',
    border:false,
    iconCls:"pwnd reportsTabIcon",
    initComponent: function(config) {
        Wtf.campaignViewReport.superclass.initComponent.call(this,config);
        this.getEditorGrid();
    },

    getEditorGrid : function () {
        this.emailmarketid = this.emailmrktid
        var Rec = new Wtf.data.Record.create([
            {name:'username'},
            {name:'emailid'},
            {name:'hitcount'},
            {name:'recentview',
            type:'date',
            dateFormat:'time'},
            {name:'marketingname'},
            {name:'campaignname'},
            {name:'campaignlogid'},
            {name:'targetlistname'},
            {name:'details'}
        ]);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'count'
        },Rec);

        this.EditorStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getViewedEmailMarketing.do',
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            baseParams:{
                emailmarketingid:this.emailmrktid
            },
            method:'post',
            reader:EditorReader
        });
        this.EditorStore.load({
            params:{
                start:0,
                limit:15
            }
        });
        this.EditorColumn = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.USERNAMEFIELD"),//'Username',
                dataIndex:'username',
                pdfwidth:60,
                xtype:'textfield',
                title:'username'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.email"),//'Email Address',
                dataIndex:'emailid',
                pdfwidth:60,
                xtype:'textfield',
                title:'emailid'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.targetlistBTN"),//'Target List',
                dataIndex:'targetlistname',
                pdfwidth:60,
                xtype:'textfield',
                title:'targetlistname'
            },{
                header:WtfGlobal.getLocaleText("crm.emailcampaign.urlreport.header.noofhits"),//'No of Hits',
                dataIndex:'hitcount',
                pdfwidth:60,
                xtype:'numberfield',
                title:'hitcount'
            },{
                header:WtfGlobal.getLocaleText("crm.emailcampaign.urlreport.header.recentview"),//'Recent View',
                dataIndex:'recentview',
                pdfwidth:60,
                xtype:'datefield',
                title:'recentview',
                offset:Wtf.pref.tzoffset,
                renderer:WtfGlobal.dateTimeRendererTZ
            },{
                header:WtfGlobal.getLocaleText("crm.audittrail.header.details"),//'Details',
                dataIndex:'details',
                xtype:'textfield',
                renderer : function(val) {
                    return '<div class=\'pwndCRM historyGoalIcon\' > </div>';
                }
            }]);
        this.exp = exportButton(this,"ViewEmail(s)",46);
        this.printprv = printButtonR(this,"ViewEmail",46);

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
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.emailmarketing.urlreport.mtygrid.watermark"))//"There is no record to show.")
            },
            searchEmptyText:WtfGlobal.getLocaleText("crm.emailmarketing.urlreport.search.mtytxt"),//"Search by Username or Email",
            serverSideSearch : true,
            searchField:"username",
            tbar:['-',this.exp,'-',this.printprv,'-']
        });

             this.mainTab=new Wtf.Panel({
               id:this.id+"viewCampaignTab",
               scope:this,
               border:false,
               layout:'fit',
               items:[this.Grid]
            });
            this.MembergridPanel = new Wtf.common.KWLListPanel({
                title: '<span  style="">'+WtfGlobal.getLocaleText("crm.campaigndetails.emailmarktitle")+' :'+this.emailmarketingName+'</span>',
                autoLoad: false,
                autoScroll:true,
                paging: false,
                layout:'fit',
                tbar:[],
                items: [this.mainTab]
            });
            this.add(this.MembergridPanel);

        this.Grid.on("cellclick",this.gridCellClick,this);
    },

    gridCellClick:function(Grid,rowIndex,columnIndex, e){
        var event = e ;
        if(event.target.className == "pwndCRM historyGoalIcon") {
            var recdata = Grid.getSelectionModel().getSelected().data;
            var viewCampWinID = this.id+"viewedCamapignWindow";
            var win = Wtf.getCmp(viewCampWinID);
            if(win == null){
                win = new Wtf.viewDetailWindow({
                    campaignlogid:recdata.campaignlogid,
                    scope:this
                });
            }
            win.show();

        }
    },
    exportfile: function(type){
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="EmailViewTimeReport";  // dependency in ExportInterface.js
        var fromdate="";
        var todate="";
        var report="";
        var exportUrl =  Wtf.req.springBase+'emailMarketing/action/getViewedEmailMarketingExport.do';
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
       exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,this.Grid,undefined,field,dir);
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="EmailViewTimeReport";
        var fromdate="";
        var todate="";
        var report="emailviewlist";
        var exportUrl = Wtf.req.springBase+'emailMarketing/action/getViewedEmailMarketingExport.do';
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,this.Grid,undefined,field,dir);
    }
});

Wtf.campaignURLReport = function (config){
    Wtf.campaignURLReport.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.campaignURLReport,Wtf.Panel,{
    closable:true,
    layout : 'fit',
    border:false,
    iconCls:"pwnd reportsTabIcon",
    initComponent: function(config) {
        Wtf.campaignURLReport.superclass.initComponent.call(this,config);
        this.getEditorGrid();
    },

    getEditorGrid : function () {
        this.emailmarketid = this.emailmrktid
        var Rec = new Wtf.data.Record.create([
            {name:'username'},
            {name:'emailid'},
            {name:'hitcount'},
            {name:'recenthit', type:'date',dateFormat:'time'},
            {name:'recentlink'},
            {name:'marketingname'},
            {name:'campaignlogid'},
            {name:'campaignname'},
            {name:'targetlistname'},
            {name:'details'}
        ]);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'count'
        },Rec);

        this.EditorStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getUrlTracking.do',
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            baseParams:{
                emailmarketingid:this.emailmrktid
            },
            method:'post',
            reader:EditorReader
        });
        this.EditorStore.load({
            params:{
                start:0,
                limit:15
            }
        });
        this.EditorColumn = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.USERNAMEFIELD"),//'Username',
                dataIndex:'username',
                pdfwidth:60,
                xtype:'textfield',
                title:'username'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.email"),//'Email Address',
                dataIndex:'emailid',
                pdfwidth:60,
                xtype:'textfield',
                title:'emailid'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.targetlistBTN"),//'Target List',
                dataIndex:'targetlistname',
                pdfwidth:60,
                xtype:'textfield',
                title:'targetlistname'
            },{
                header:WtfGlobal.getLocaleText("crm.emailcampaign.urlreport.header.hitsonlinks"),//'Hits on Links',
                dataIndex:'hitcount',
                pdfwidth:60,
                xtype:'numberfield',
                title:'hitcount'
            },{
                header:WtfGlobal.getLocaleText("crm.emailcampaign.urlreport.header.recenthits"),//'Recent Hit',
                dataIndex:'recenthit',
                pdfwidth:60,
                xtype:'datefield',
                renderer:WtfGlobal.dateTimeRendererTZ,
                title:'recenthit'
            },{
                header:WtfGlobal.getLocaleText("crm.emailcampaign.urlreport.header.recentlinks"),//'Recent Link',
                dataIndex:'recentlink',
                pdfwidth:60,
                xtype:'textfield',
                title:'recentlink'
            },{
                header:WtfGlobal.getLocaleText("crm.audittrail.header.details"),//'Details',
                dataIndex:'details',
                xtype:'textfield',
                renderer : function(val) {
                    return '<div class=\'pwndCRM historyGoalIcon\' > </div>';
                }
            }]);
        this.exp = exportButton(this,"ViewURLEmail(s)",47);
        this.printprv = printButtonR(this,"ViewURLEmail",47);

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
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.emailmarketing.urlreport.mtygrid.watermark"))//"There is no record to show.")
            },
            searchEmptyText:WtfGlobal.getLocaleText("crm.emailmarketing.urlreport.search.mtytxt"),//"Search by Username or Email",
            serverSideSearch : true,
            searchField:"username",
            tbar:['-',this.exp,'-',this.printprv,'-']
        });

             this.mainTab=new Wtf.Panel({
               id:this.id+"urlCampaignTab",
               scope:this,
               border:false,
               layout:'fit',
               items:[this.Grid]
            });
            this.MembergridPanel = new Wtf.common.KWLListPanel({
                title: '<span  style="">'+WtfGlobal.getLocaleText("crm.campaigndetails.emailmarktitle")+ this.emailmarketingName+'</span>',
                autoLoad: false,
                autoScroll:true,
                paging: false,
                layout:'fit',
                tbar:[],
                items: [this.mainTab]
            });
            this.add(this.MembergridPanel);

        this.Grid.on("cellclick",this.gridCellClick,this);
    },

    gridCellClick:function(Grid,rowIndex,columnIndex, e){
        var event = e ;
        if(event.target.className == "pwndCRM historyGoalIcon") {
            var recdata = Grid.getSelectionModel().getSelected().data;
            var urlCampWin = this.id+"urlCampaignWindow";
            var win = Wtf.getCmp(urlCampWin);
            if(win == null){
                win = new Wtf.urlDetailWindow({
                    campaignlogid:recdata.campaignlogid,
                    scope:this
                });
            }
            win.show();

        }
    },
    exportfile: function(type){
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="EmailURLReport";  // dependency in ExportInterface.js
        var fromdate="";
        var todate="";
        var report="";
        var exportUrl =  Wtf.req.springBase+'emailMarketing/action/getUrlTrackingExport.do';
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
       exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,this.Grid,undefined,field,dir);
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="EmailURLReport";
        var fromdate="";
        var todate="";
        var report="emailurllist";
        var exportUrl = Wtf.req.springBase+'emailMarketing/action/getUrlTrackingExport.do';
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,this.Grid,undefined,field,dir);
    }
});

Wtf.viewDetailWindow = function (config){
    config.title = "Email View Time Report ";
    Wtf.apply(this, config);
    Wtf.viewDetailWindow.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.viewDetailWindow,Wtf.Window,{
    iconCls : "pwnd favwinIcon",
    layout : 'fit',
    modal:true,
    resizable:false,
    height : 500,
    width : 500,
    onRender: function(config){
        Wtf.viewDetailWindow.superclass.onRender.call(this,config);

        this.viewDetailRecord = new Wtf.data.Record.create([{
            name:'viewtime',
            type:'date',
            dateFormat:'time'
        },{
            name:'marketingname'
        },{
            name:'campaignname'
        }]);
        var viewDetailReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.viewDetailRecord);

        this.viewDetailStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getViewedEmailMarketingTiming.do',
            baseParams:{
				campaignlogid : this.campaignlogid
            },
            method:'post',
            reader:viewDetailReader
        });

        this.colModel = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.emailmarketing.urlreport.viewedtime"),//'Viewed Time',
                xtype:'timefield',
                dataIndex:'viewtime',
                renderer:WtfGlobal.dateTimeRendererTZ
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.emailmarktitle"),//'Email Marketing Name',
                xtype:'textfield',
                dataIndex:'marketingname'
            },{
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.campaignname"),//'Campaign Name',
                xtype:'textfield',
                dataIndex:'campaignname'
            }]);

        this.viewDetailGrid = new Wtf.grid.GridPanel({
            store: this.viewDetailStore,
            cm: this.colModel,
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true
            }
        });
        var task = new Wtf.util.DelayedTask(function() {
        this.viewDetailStore.load();
        }, this);
        task.delay(50);

        this.mainPanel = new Wtf.Panel({
            layout :'border',
            border : false,
            autoScroll:true,
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),// 'Close',
                scope:this,
                handler:function() {
                    this.close()
                }
            }],
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.emailmarketing.viewdmailreport.tophtml.title"), WtfGlobal.getLocaleText("crm.emailmarketing.viewdmailreport.tophtml.title"), "../../images/import.png")
            },{
                layout :'fit',
                region : 'center',
                items : this.viewDetailGrid
            }]
        });

        this.add(this.mainPanel);
    }
});

Wtf.urlDetailWindow = function (config){
    config.title = "URL View Time Report ";
    Wtf.apply(this, config);
    Wtf.urlDetailWindow.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.urlDetailWindow,Wtf.Window,{
    iconCls : "pwnd favwinIcon",
    layout : 'fit',
    modal:true,
    resizable:false,
    height : 500,
    width : 550,
    onRender: function(config){
        Wtf.urlDetailWindow.superclass.onRender.call(this,config);

        this.viewDetailRecord = new Wtf.data.Record.create([{
            name:'hittime',
            type:'date',
            dateFormat:'time'
        },{
            name:'hiturl'
        },{
            name:'marketingname'
        },{
            name:'campaignname'
        }]);
        var viewDetailReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.viewDetailRecord);

        this.viewDetailStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getUrlTrackingDetail.do',
            baseParams:{
				campaignlogid : this.campaignlogid
            },
            method:'post',
            reader:viewDetailReader
        });

        this.colModel = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.emailmarketing.urlreport.viewedtime"),//'Viewed Time',
                xtype:'timefield',
                dataIndex:'hittime',
                renderer:WtfGlobal.dateTimeRendererTZ
            },{
                header:WtfGlobal.getLocaleText("crm.emailmarketing.urlreport.viewedurl"),//'Viewed URL',
                xtype:'textfield',
                dataIndex:'hiturl'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.emailmarktitle"),//'Email Marketing Name',
                xtype:'textfield',
                dataIndex:'marketingname'
            },{
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.campaignname"),//'Campaign Name',
                xtype:'textfield',
                dataIndex:'campaignname'
            }]);

        this.viewDetailGrid = new Wtf.grid.GridPanel({
            store: this.viewDetailStore,
            cm: this.colModel,
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true
            }
        });
        var task = new Wtf.util.DelayedTask(function() {
        this.viewDetailStore.load();
        }, this);
        task.delay(50);

        this.mainPanel = new Wtf.Panel({
            layout :'border',
            border : false,
            autoScroll:true,
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),// 'Close',
                scope:this,
                handler:function() {
                    this.close()
                }
            }],
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml("URL View Time Report " , "URL View Time Report ", "../../images/import.png")
            },{
                layout :'fit',
                region : 'center',
                items : this.viewDetailGrid
            }]
        });

        this.add(this.mainPanel);
    }
});
