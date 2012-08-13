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
Wtf.emailCampaignList=function(config){

    Wtf.emailCampaignList.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.emailCampaignList,Wtf.Panel,{
    initComponent:function(config){
        Wtf.emailCampaignList.superclass.initComponent.call(this,config);

        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.testCampaignBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.testurcampBTN"),//"Test Your Campaign",
            scope:this,
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.testurcampBTN.ttip")},//'Send e-mail campaigns to login User.'},
            iconCls:"pwnd addEmailMarketing",
            handler:function() {this.testCamp()}
        });
        this.runCampaignBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.runurcampBTN"),//"Run Your Campaign",
            scope:this,
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.runurcampBTN.ttip")},//'Send e-mail campaigns to the chosen target lists.'},
            iconCls:"pwnd addEmailMarketing",
            handler:function() {this.askConfirmToRunCamp()}
        });
        var Rec=new Wtf.data.Record.create([
            {name:'id'},
            {name:'name'},
            {name:'templatename'},
            {name:'templateid'},
            {name:'templatedescription'},
            {name:'templatesubject'},
            {name:'fromname'},
            {name:'fromaddress'},
            {name:'replymail'},
            {name:'unsub'},
            {name:'fwdfriend'},
            {name:'archive'},
            {name:'updatelink'},
            {name:'targetcount', type:'int'},
            {name:'createdon',type:'date',dateFormat:'time'},
            {name:'campaignlog'},
            {name:'SMarketing'}
        ]);

        this.emailmarketingReader = new Wtf.data.KwlJsonReader({
            root:"data",
            totalProperty:"totalCount"
        },Rec);


        this.emailmarketingstore=new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getCampEmailMarketList.do',
            baseParams:{
                flag:9,
                campid : this.campaignid
            },
            reader:this.emailmarketingReader
        });

        this.cm1=new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.name"),//'Name',
                dataIndex:'name'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.emailtemp"),//'Email Template',
                dataIndex:'templatename',
                xtype:'textfield',
                renderer : function(val) {
                    return "<a href = '#' class='tempdetails' wtf:qtip='Click to edit email template.'> "+val+"</a>";
                }
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon"),//'Created On',
                dataIndex:'createdon',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ	
            }
            ,{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.campcharts"),//'Campaign Charts',
                align:'center',
                dataIndex:'targetcount',
                renderer : function(a){
                    var ret = "";
                    if(a>0){
                    	ret = "<a href = '#' class='campchart' wtf:qtip="+WtfGlobal.getLocaleText("crm.campaigndetails.header.campcharts.ttip")+"> View  </a>";
                    }
                    return ret;
                }
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.campstatusrepo"),//'Campaign Status Reports',
                align:'center',
                dataIndex:'id',
                xtype:'textfield',
                renderer : function(val){
                    var ret = "";
                    if(val != ""){
                    	ret = "<a href = '#' class='bouncereport' wtf:qtip="+WtfGlobal.getLocaleText("crm.campaigndetails.header.campstatusrepo.ttip")+"style='color:#15428B;text-decoration:none;' >View</a>";
                    }
                    return ret;
                }
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.campviewrepo"),//"'Campaign View' Reports",
                dataIndex:'id',
                align:'center',
                xtype:'textfield',
                renderer : function(val){
                    var ret = "";
                    if(val != ""){
                        ret = "<a href = '#' class='viewcampaignreport'  style='color:#15428B;text-decoration:none;' >View </a>";
                    }
                    return ret;
                }
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.clickthrurepo"),//'Click Through Reports',
                align:'center',
                xtype:'textfield',
                dataIndex:'id',
                renderer : function(val){
                    var ret = "";
                    if(val != ""){
                        ret = "<a href = '#' class='urlcampaignreport'  style='color:#15428B;text-decoration:none;' >View </a>";
                    }
                    return ret;
                }
            }
        ]);

        this.getEmailCampaignGrid();
 
        this.pg = new Wtf.PagingSearchToolbar({
            pageSize: 15,
            store: this.emailmarketingstore,
            displayInfo:true,
            emptyMsg:WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),// "No results to display",
            plugins: this.pP = new Wtf.common.pPageSize()
        });
        this.toolbarItems = [];
        this.toolbarItems.push(WtfGlobal.getLocaleText("crm.goalsettings.qsearch.label")+": ");
        this.toolbarItems.push('-');
        this.toolbarItems.push(this.quickSearchTF);

        this.emailmarketinggrid = new Wtf.grid.GridPanel({
            store: this.emailmarketingstore,
            sm:this.sm,
            autoScroll :true,
            border:false,
            id:'emailmarketinggrid'+this.id,
            scope:this,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.emailmarketing.grid.mtytext"))//"Please select a campaign to view its campaign configurations.")
            },
            loadMask:true,
            clicksToEdit :1,
            displayInfo:true,
            bbar:this.pg,
            tbar:[this.testCampaignBtn,this.runCampaignBtn],
            cm: this.cm1
        });

      //  this.getDetailPanel();
        this.emailmarketingstore.on('datachanged', function() {
            var p = this.pP.pagingToolbar.pageSize;
        }, this);

        this.goalpan= new Wtf.Panel({
            layout:'fit',
            region:'center',
            border:true,
            id:this.id+'goalpan',

            items:[
            {
                layout:'fit',
                border:false,
                items:[this.emailmarketinggrid]
            }
            ]
        });
        this.emailcampaign= new Wtf.Panel({
            layout:'fit',
            region:'west',
            border:true,
            width:280,
            items:[
            {
                layout:'fit',
                border:false,
                items:[this.ShortUserGrid]
            }
            ]
        });
        this.MembergridPanel = new Wtf.common.KWLListPanel({
            title: '<span  style="">'+WtfGlobal.getLocaleText("crm.emailmarketing.grid.title")+'</span>',
            autoLoad: false,
            autoScroll:true,
            paging: false,
            tbar:this.toolbarItems,
            layout: 'border',
            items: [this.emailcampaign,this.goalpan]
        });
        this.add(this.MembergridPanel);

        this.selectionModel.on('selectionchange', function(selModel) {
            var sm =this.selectionModel;
            if(sm.getSelections().length==1){
                        var rec = sm.getSelected();
                        this.emailmarketingstore.baseParams.campid = rec.get('campaignid');
                        this.emailmarketingstore.load({
                             params : {start : 0,limit : this.pP.pagingToolbar.pageSize}
                        });
            this.emailmarketinggrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.emailmarketing.emptygridafterselcamp.mtytxt"));//"No email campaign assigned till now to the selected campaign.");
            }else{

                this.emailmarketinggrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.emailmarketing.emptygrid.mtytxt"));//"Please select a campaign to view its email campaign..");
                this.emailmarketinggrid.getStore().removeAll();
            }
        }, this);
        this.emailmarketinggrid.on("cellclick", this.afterGridCellClick, this);
   
        this.emailmarketinggrid.on('render',function(){this.emailmarketinggrid.getStore().removeAll();},this);
    },
    testCamp:function(){
        if(this.emailmarketinggrid.getSelectionModel().getCount()!=1){
            WtfComMsgBox([WtfGlobal.getLocaleText("crm.campaigndetails.msg.title"),WtfGlobal.getLocaleText("crm.emailmarketing.selcampconffortestcamp.msg")],0);//"Please select a Campaign Configuration to Test Your Campaign"],0);
          	return;
        }
        var rec = this.emailmarketinggrid.getSelectionModel().getSelected();
        var rowIndex = this.emailmarketinggrid.getStore().indexOf(rec);
        Wtf.showTestMailWindow(this.emailmarketinggrid,rowIndex,this.campaignid);
    },

    askConfirmToRunCamp: function(){
        if(this.emailmarketinggrid.getSelectionModel().getCount()!=1){
            WtfComMsgBox([WtfGlobal.getLocaleText("crm.campaigndetails.msg.title"),WtfGlobal.getLocaleText("crm.emailmarketing.selcampconftoruncamp.msg")],0);//"Please select a Campaign Configuration to Run Your Campaign"],0);
            return;
        }
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//'Confirm',
            msg:WtfGlobal.getLocaleText("crm.emailmarketing.confirmmsgtoruncamp.msg"),//"Are you sure you want to run your selected campaign?",
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            fn:function(button){
                if(button=='yes')
                {
                    this.runCamp();
                }else{
                    return;
                }
            }
        });
    },
    runCamp:function(){
        var rec = this.emailmarketinggrid.getSelectionModel().getSelected();
        var rowIndex = this.emailmarketinggrid.getStore().indexOf(rec);
        Wtf.Ajax.timeout=1200000;
            Wtf.commonWaitMsgBox("Sending mail...");
            Wtf.Ajax.requestEx({
                url: Wtf.req.springBase+'emailMarketing/action/sendEmailMarketMail.do',
                params:{
                    emailmarkid : this.emailmarketinggrid.store.getAt(rowIndex).data.id,
                    campid : this.campaignid,
                    flag : 11
                }},this,function(obj ,req){
//                    var obj = eval('('+res+')');
                    Wtf.updateProgress();
                    Wtf.Ajax.timeout=30000;
                    Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("crm.emailmarketing.runcamploadmask.msg"),//"Sending E-mails for your Campaign...",
                        msg:obj.msgs,
                        icon:Wtf.MessageBox.INFO,
                        buttons:Wtf.MessageBox.OK
                    });
                 },function() {
                    Wtf.updateProgress();
                    Wtf.Ajax.timeout=30000;
            });
    },
    getEmailCampaignGrid : function(){
        this.emailcampaignRec = new Wtf.data.Record.create([
        {
            name: 'campaignid'
        },

        {
            name: 'campaignname'
        },
        {
            name: 'objective'
        }
        ]);

        this.emailcampaignds = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"totalCount"
            },this.emailcampaignRec),
            url: Wtf.req.springBase+"Campaign/action/getCampaigns.do",
            baseParams:{
                config:true,
                emailcampaign:true
            }
        });
        this.emailcampaignds.load({
            params : {
                start:0,
                limit:15
            }
        });
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:true,
            width: 30,
            id:"emailcampaignlist_selectionmodel"
        });

        this.gridcm= new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            this.selectionModel,
            {
                header: WtfGlobal.getLocaleText("crm.campaign.defaultheader.campaignname"),//"Campaign Name",
                tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.campaignname"),//'Campaign Name',
                dataIndex: 'campaignname',
                autoWidth : true,
                xtype:'textfield',
                sortable: true,
                groupable: true
            }]);
        this.quickSearchTF = new Wtf.KWLTagSearch({
                id: 'administration_goal'+this.id,
                width: 150,
                emptyText: WtfGlobal.getLocaleText("crm.emailmarketing.quicksearch.mtytxt")//'Enter Campaign Name '
            });
        this.ShortUserGrid=new Wtf.grid.GridPanel({
            layout:'fit',
            store: this.emailcampaignds,
            cm: this.gridcm,
            sm : this.selectionModel,
            border : false,
            loadMask : true,
            view:new Wtf.ux.KWLGridView({
                forceFit:true
            }),
            bbar:new Wtf.PagingSearchToolbar({
                pageSize: 15,
                searchField:this.quickSearchTF,
                id: "emailcampaignlist_toolbar",
                store: this.emailcampaignds,
                displayInfo: false
            })
        });
        this.emailcampaignds.on('load',function(){
            this.quickSearchTF.StorageChanged(this.emailcampaignds);
            this.quickSearchTF.on('SearchComplete', function() {
            }, this);
        },this);

        this.emailcampaignds.on("datachanged",function(){
            this.quickSearchTF.setPage(this.pP.combo.value);
        },this);

    },

    onRender: function(config) {
        Wtf.emailCampaignList.superclass.onRender.call(this, config);
    },

    afterGridCellClick:function(Grid,rowIndex,columnIndex, e ) {
        var campID = this.selectionModel.getSelections()[0].data.campaignid;
        var event = e ;
        if(event.getTarget("a[class='campdetails']")) {
            Wtf.Ajax.timeout=1200000;
            Wtf.commonWaitMsgBox("Sending mail...");
            Wtf.Ajax.requestEx({
                url: Wtf.req.springBase+'emailMarketing/action/sendEmailMarketMail.do',
                params:{
                    emailmarkid : Grid.store.getAt(rowIndex).data.id,
                    campid : campID,
                    flag : 11
                }},this,function(obj ,req){
                    Wtf.updateProgress();
                    Wtf.Ajax.timeout=30000;
                    Wtf.MessageBox.show({
                        title:WtfGlobal.getLocaleText("crm.emailmarketing.runcamploadmask.msg"),//"Sending E-mails for your Campaign...",
                        msg:obj.msgs,
                        icon:Wtf.MessageBox.INFO,
                        buttons:Wtf.MessageBox.OK
                    });
                 },function() {
                    Wtf.updateProgress();
                    Wtf.Ajax.timeout=30000;
            });
        }
        if(event.getTarget("a[class='campchart']")) {
            var chartid = "EmailCampaignMailStatus"+Grid.store.getAt(rowIndex).data.id;
            var id = this.id+"emailgraph";
            var swf = "../../scripts/graph/krwcolumn/krwcolumn/krwcolumn.swf";
            var dataflag = "crm/Campaign/campaignReport/getCampaignMailStatusChart.do?flag=22&campID="+campID+"&mailMarID="+Grid.store.getAt(rowIndex).data.id+"";
            var mainID = "crmemailcampaigntabpanel";
            var xmlpath='../../scripts/graph/krwcolumn/examples/CampaignMailStatus/CampaignMailStatus_settings.xml';
            var param = "mailMarID="+Grid.store.getAt(rowIndex).data.id+"&campID="+campID;
            var tipTitle =Grid.store.getAt(rowIndex).data.name;
            var maintitle = Wtf.util.Format.ellipsis(tipTitle,20);
            var title = "<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.report.chartview")+"'>"+maintitle+"</div>";
            var showHtml = 'false';
            globalChart(chartid,id,swf,dataflag,mainID,xmlpath,Wtf.id(),showHtml,"","",tipTitle);
        }

        if(event.getTarget("a[class='sendTestMail']")) {
            Wtf.showTestMailWindow(Grid,rowIndex,campID);

        }if(event.getTarget("a[class='bouncereport']")) {
            var emailmarketingid =Grid.store.getAt(rowIndex).data.id;
            var emailmarketingname =Grid.store.getAt(rowIndex).data.name;
            this.loadBounceReport(emailmarketingid,emailmarketingname);
        }
        if(event.getTarget("a[class='tempdetails']")) {
            var recdata = Grid.getSelectionModel().getSelected().data;
            var panel = Wtf.getCmp('template_dash_win_'+recdata.templateid);
            var tipTitle=recdata.templatename+" : Edit Template";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
                if(panel==null) {
                    panel=new Wtf.newEmailTemplate({
                        templateid : recdata.templateid,
                        tname : recdata.templatename,
                        tdesc : recdata.templatedescription,
                        templateClass :recdata.templateclass,
                        tsubject : recdata.templatesubject,
                        tbody : recdata.bodyhtml,
                        store: this.EditorStore,
                        title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Email Template'>"+title+"</div>",
                        tipTitle:tipTitle,
                        dashboardCall:true
                    });
                    Wtf.getCmp("crmemailcampaigntabpanel").add(panel);
                }
                Wtf.getCmp("crmemailcampaigntabpanel").setActiveTab(panel);
                Wtf.getCmp("crmemailcampaigntabpanel").doLayout();

        }
        if(event.getTarget("a[class='viewcampaignreport']")) {
            var recordData = Grid.getSelectionModel().getSelected().data;
            var ViewPanel=Wtf.getCmp('view_campaignreport_tab_id'+recordData.id);
            if(ViewPanel==null)
            {
                ViewPanel=new Wtf.campaignViewReport({
                    title:"'Campaign View' Report",
                    mainTab:this.mainTab,
                    id:"view_campaignreport_tab_id"+recordData.id,
                    emailmarketingName:recordData.name,
                    emailmrktid:recordData.id
                })
                Wtf.getCmp("crmemailcampaigntabpanel").add(ViewPanel);
            }
            Wtf.getCmp("crmemailcampaigntabpanel").setActiveTab(ViewPanel);
            Wtf.getCmp("crmemailcampaigntabpanel").doLayout();

        }
        if(event.getTarget("a[class='urlcampaignreport']")) {
            var recordData = Grid.getSelectionModel().getSelected().data;
            var URLPanel=Wtf.getCmp('url_campaignreport_tab_id'+recordData.id);
            if(URLPanel==null)
            {
                URLPanel=new Wtf.campaignURLReport({
                    title:"Click Through Report",
                    mainTab:this.mainTab,
                    emailmarketingName:recordData.name,
                    id:"url_campaignreport_tab_id"+recordData.id,
                    emailmrktid:recordData.id
                })
                Wtf.getCmp("crmemailcampaigntabpanel").add(URLPanel);
            }
            Wtf.getCmp("crmemailcampaigntabpanel").setActiveTab(URLPanel);
            Wtf.getCmp("crmemailcampaigntabpanel").doLayout();

        }
    },
    loadBounceReport: function(emailmarketingid,emailmarketingname){

       this.createBounceReportGrid(emailmarketingid);
       this.MembergridPanel = new Wtf.common.KWLListPanel({
    	   title: '<span  style="">'+WtfGlobal.getLocaleText("crm.campaigndetails.emailmarktitle")+emailmarketingname+'</span>',
            autoLoad: false,
            autoScroll:true,
            paging: false,
            layout:'fit',
            tbar:[],
            items: [this.bounceReportGrid]
       });
       var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.bouncerepo.emailcampstat.ttip");//"Email Campaign Status";
       var title = Wtf.util.Format.ellipsis(tipTitle,18);
       var panel=Wtf.getCmp('bounceReportPanelemailcampaign'+emailmarketingid);
       if(panel==null) {
          panel = new Wtf.Panel({
              layout:'fit',
              id:'bounceReportPanelemailcampaign'+emailmarketingid,
              title:"<div wtf:qtip="+WtfGlobal.getLocaleText("crm.campaigndetails.emailcampstat.ttip")+" wtf:qtitle="+WtfGlobal.getLocaleText("crm.campaigndetails.bouncerepo.emailcampstat.ttip")+">"+title+"</div>",
              closable:true,
              iconCls:"pwnd reportsTabIcon",
              items:[this.MembergridPanel]
          });
          Wtf.getCmp("crmemailcampaigntabpanel").add(panel);
       } else {
           Wtf.getCmp("crmemailcampaigntabpanel").setActiveTab(panel);
       }

          Wtf.getCmp("crmemailcampaigntabpanel").activate(Wtf.getCmp('bounceReportPanelemailcampaign'+emailmarketingid));
   },
    createBounceReportGrid:function(emailmarketingid){
        this.emailmarketid = emailmarketingid;
        var bounceRec =new Wtf.data.Record.create([
            {name:'email'},
            {name:'fname'},
            {name:'lname'},
            {name:'status'},
            {name:'description'},
            {name:'targetid'},
            {name:'statustype'},
            {name: 'subcription'}

        ]);
        this.reportType = new Wtf.data.SimpleStore({
            fields:['id','value'],
            data:[
                ['0','Bounced'],
                ['1','Viewed'],
                ['2','Unviewed'],
                ['3','Unsubscribed']
            ]
        });
            this.reportTypeCombo = new Wtf.form.ComboBox({
                mode: 'local',
                triggerAction: 'all',
                typeAhead: true,
                width:150,
                editable: false,
                store: this.reportType,
                displayField: 'value',
                valueField:'id',
                allowBlank:false,
                msgTarget: 'side'
            });
            this.reportTypeCombo.setValue('0');
            this.sm= new Wtf.grid.CheckboxSelectionModel({});
        this.bounceReportColumn = new Wtf.grid.ColumnModel(
            [
             new Wtf.grid.RowNumberer(),
             this.sm,
            {
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.email"),//'Email Address',
                dataIndex:'email',
                xtype:'textfield',
                pdfwidth:60,
                title:'email'

            },
            {
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.fname"),//'First Name',
                dataIndex:'fname',
                pdfwidth:60,
                xtype:'textfield',
                title:'fname'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.lname"),//'Last Name',
                dataIndex:'lname',
                pdfwidth:60,
                xtype:'textfield',
                title:'lname'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.reason"),//'Reason',
                dataIndex:'status',
                pdfwidth:60,
                xtype:'textfield',
                title:'status',
                renderer : function(val,meta,record){
                    var ret = "";
                    if(record.data.statustype==0 && val !="" ){
                        ret = "<span wtf:qtip='"+record.get("description")+"'>"+val+"</span><img src=\"images/information.png\" style='vertical-align:middle;margin-left:5px;'wtf:qtip='"+record.get("description")+"'>";
                    }
                    return ret;
                }
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.status"),//'Status',
                dataIndex:'subcription',
                pdfwidth:60,
                xtype:'textfield',
                title:'subcription',
                renderer :function(val,mera,record) {
                    var ret = "";
                    if(val == "removed" ){
                        ret = "Unsubscribed";
                    }
                    return ret;
                }
            }

        ]);
        this.bounceReportStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getBounceReport.do',
            baseParams:{
                flag:827,
                emailmarketingid:emailmarketingid,
                type:0
            },
            reader:new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
            },bounceRec)
        });
        this.exp = exportButton(this,"Target(s)",45);
        this.printprv = printButtonR(this,"Targets",45);
        this.moveToLead =moveToLeadButton(this);
        this.bounceReportGrid = new Wtf.KwlGridPanel({

            store: this.bounceReportStore,
            cm: this.bounceReportColumn,
            border : false,
            loadMask : true,
            sm:this.sm,
            displayInfo: true,
            searchEmptyText:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.searchtxt.mtytxt"),//"Search by Email",
            searchField:"email",
            serverSideSearch : true,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.emailmarketing.nobouncemailmtygrid.mtytxt")//"Looks like there are no bounced emails in this email campaign, or the bounced targets have already been removed."
            },
            tbar:['-',new Wtf.Button({
                    text:WtfGlobal.getLocaleText("crm.emailmarketing.toptoolbar.deltargetBTN"),//'Remove From Target List',
                    iconCls:'pwnd deleteButtonIcon',
                    disabled:true,
                    id:this.id+'removaltargetlist',
                    handler:function(){
                        selModel = this.bounceReportGrid.getSelectionModel();

                        var recarray = selModel.getSelections();

                        var targets = "";
                        for(var i=0; i< recarray.length;i++){
                            if(i>0){
                            targets +=",";
                            }
                            targets +=recarray[i].get("targetid");
                        }
                        if(targets !=""){
                         Wtf.MessageBox.show({
                            title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),// "Confirm",
                            msg: WtfGlobal.getLocaleText("crm.emailmarketing.deletebouncedtrgtconfirm.msg"),//"Are you sure you want to remove selected target(s) from target list?<br><br><b>Note: This data cannot be retrieved later.",
                            buttons: Wtf.MessageBox.OKCANCEL,
                            animEl: 'upbtn',
                            icon: Wtf.MessageBox.QUESTION,
                            scope:this,
                            fn:function(bt){
                                if(bt=="ok"){
                                    Wtf.Ajax.requestEx({
                                        url: Wtf.req.springBase+'emailMarketing/action/deleteBouncedTargets.do',
                                        params: {
                                            flag: 828,
                                            targets: targets

                                        }
                                    }, this, function(action, response){
                                        WtfComMsgBox(211,0);
                                          this.bounceReportStore.load({
                                              params:{
                                                  start:0,
                                                  limit:this.pP.pagingToolbar.pageSize
                                              }
                                          });
                                    }, function(action, response){
                                        WtfComMsgBox(212,0);
                                    });
                                }
                            }
                         });
                        }else{
                            WtfComMsgBox(213,0);
                        }
                    },scope:this})
                ,'-',this.exp,'-',this.printprv,'-',this.moveToLead,'-','->',WtfGlobal.getLocaleText("crm.REPORTTYPETEXT")+':','-',this.reportTypeCombo,'-'
            ],
            bbar: new Wtf.PagingSearchToolbar({
                pageSize: 15,
                displayInfo: true,
                store: this.bounceReportStore,
                plugins:this.pP = new Wtf.common.pPageSize({
                })
            })
        });

        this.reportTypeCombo.on('select',function(){
                var val = this.reportTypeCombo.getValue();
                var cm = this.bounceReportColumn;
                if(val==0) {
                    cm.setHidden(5,false);
                    cm.setHidden(6,true);
                    this.bounceReportGrid.getView().emptyText = WtfGlobal.getLocaleText("crm.emailmarketing.nobouncemailmtygrid.mtytxt");//"Looks like there are no bounced emails in this email campaign, or the bounced targets have already been removed.";
                    Wtf.getCmp(this.id+"move_to_lead").hide();
                } else if(val==1 || val==2 || val==3) {
                    cm.setHidden(5,true);
                    cm.setHidden(6,true);
                    if(val==1){
                        this.bounceReportGrid.getView().emptyText = WtfGlobal.getLocaleText("crm.emailmarketing.noviewedmails.mtytxt");//"Looks like there are no viewed emails in this email campaign";
                        Wtf.getCmp(this.id+"move_to_lead").show();
                    } else if(val==2){
                        this.bounceReportGrid.getView().emptyText =  WtfGlobal.getLocaleText("crm.emailmarketing.nounviewedmails.mtytxt");//"Looks like there are no Unviewed emails in this email campaign";
                        Wtf.getCmp(this.id+"move_to_lead").hide();
                    } else if(val==3){
                        cm.setHidden(6,false);
                        this.bounceReportGrid.getView().emptyText =  WtfGlobal.getLocaleText("crm.emailmarketing.nounsubscribedmails.mtytxt");//"Looks like there are no Unsubscribed emails in this email campaign";
                        Wtf.getCmp(this.id+"move_to_lead").hide();
                    }
                }
                Wtf.getCmp(this.id+'removaltargetlist').disable();
                this.bounceReportStore.load({
                  params:{
                      start:0,
                      limit:this.pP.pagingToolbar.pageSize
                  }
                });
        },this)

          this.bounceReportStore.on('beforeload',function(){
            this.bounceReportStore.baseParams = {
                flag:827,
                emailmarketingid:emailmarketingid,
                type:this.reportTypeCombo.getValue()
            }
        },this);
        this.reportTypeCombo.fireEvent("select");
        this.bounceReportGrid.on('rowclick', function(grid, rowIndex, e){
            var count= this.bounceReportGrid.getSelectionModel().getSelections();
            var btn= Wtf.getCmp(this.id+'removaltargetlist');
            if(count.length > 0){
                btn.enable();
            }else{
                btn.disable();
            }
        }, this);
    },
    exportfile: function(type){
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="BounceReport";  // dependency in ExportInterface.js
        var fromdate="";
        var todate="";
        var report="";
        var exportUrl =  Wtf.req.springBase+'emailMarketing/action/getBounceReportExport.do';
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        //exportWithTemplate(this,type,name,fromdate,todate,report, exportUrl);
		exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,this.bounceReportGrid,undefined,field,dir);
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="BounceReport";
        var fromdate="";
        var todate="";
        var report="targetlist";
        var exportUrl = Wtf.req.springBase+'emailMarketing/action/getBounceReportExport.do';
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,this.bounceReportGrid,undefined,field,dir);
    }
});
