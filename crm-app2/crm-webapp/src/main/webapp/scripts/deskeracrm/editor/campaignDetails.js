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
Wtf.campaignDetails = function (config){
    Wtf.campaignDetails.superclass.constructor.call(this,config);

    this.addEvents({
        "editConfig": true
    });
}

Wtf.extend(Wtf.campaignDetails,Wtf.Panel,{
    closable:true,
    layout : 'fit',
    border:false,
    iconCls:'pwndnewCRM emailmarketingTabicon',
    initComponent: function(config) {
        Wtf.campaignDetails.superclass.initComponent.call(this,config);

        var help=getHelpButton(this,35);

        this.addEmailMarketing= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.addcampconfBTN"),//"Add Campaign Configuration",
            scope:this,
            id:'addemailmarketing35',//In use,do not delete
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.addcampconfBTN.ttip")},//'Build effective campaign configuration by using pre-defined email templates and target lists.'},
            iconCls:"pwnd addEmailMarketing",
            handler:function() {this.emailMarketing(0)}
        });

        this.editEmailMarketing= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.editcampconfBTN"),//"Edit Campaign Configuration",
            scope:this,
            disabled:true,
            iconCls:"pwnd editEmailMarketing",
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.disablededitcampconfBTN")},//'Select a campaign configuration to edit.'},
            handler:function() {
                if(this.Grid.getSelectionModel().getSelections().length==1) {
                    this.emailMarketing(1);
                } else {
                    this.editEmailMarketing.disable();
                    ResponseAlert(72);
                }
            }
        });

        this.scheduleEmailMarketing= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.schedulecampBTN"),//"Schedule Campaign Configuration",
            scope:this,
            disabled:true,
            iconCls:"pwnd scheduleEmailMarketing",
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.schedulecampBTN.ttip")},//'Select an Configuration to schedule the delivery.'},
            handler:function() {
                var sel = this.Grid.getSelectionModel().getSelections();
                if(sel.length==1) {
                    this.showScheduleWindow(sel[0]);
                } else {
                     WtfComMsgBox([WtfGlobal.getLocaleText("crm.campaigndetails.msg.title"),WtfGlobal.getLocaleText("crm.campaigndetails.schedulingcamp.selcampmsg")],0);//"Please select a Campaign Configuration for Scheduling Email Campaign"],0);
            	}
            }
        });

        this.emailTempBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.emailTempBTN"),//"Email Templates",
            scope:this,
            id:'emailtemplate35',//In use, do not delete.
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.emailTempBTN.ttip")},//'Build e-mail templates for campaign configuration of your campaigns. Add rich text formatting, pictures, videos, links and more.'},
            iconCls:"pwndCRM templateEmailMarketing",
            handler:function(){
                var panel = Wtf.getCmp('emailTemplate');
                if(panel==null) {
                    panel=new Wtf.emailTemplate({
                        mainTab:this.mainTab
                    })
                    this.mainTab.add(panel);
                }
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();
            }
        });
        this.TargetListBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.targetlistBTN"),//"Target List",
            scope:this,
            id:'targetlist35', //In use, do not delete.
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.targetlistBTN.ttip")},//'Define the list of recipients for your campaign configuration. Import e-mail addresses from leads, contacts or targets easily.'},
            iconCls:"pwnd targetListEmailMarketing",
            handler:this.targetListHandler
        });
        this.testCampaignBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.testurcampBTN"),//"Test Your Campaign",
            disabled:true,
            scope:this,
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.testurcampBTN.ttip")},//'Send e-mail campaigns to login User.'},
            iconCls:"pwnd addEmailMarketing",
            handler:function() {this.testCamp()}
        });
        this.runCampaignBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.runurcampBTN"),//"Run Your Campaign",
            disabled:true,
            scope:this,
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.runurcampBTN.ttip")},//'Send e-mail campaigns to the chosen target lists.'},
            iconCls:"pwnd addEmailMarketing",
            handler:function() {this.askConfirmToRunCamp()}
                });
        var Rec = new Wtf.data.Record.create([
            {name:'id'},
            {name:'name'},
            {name:'templatename'},
            {name:'templateid'},
            {name:'templatedescription'},
            {name:'templatesubject'},
            {name:'marketingsubject'},
            {name:'captureLead'},
            {name:'fromname'},
            {name:'fromaddress'},
            {name:'replymail'},
            {name:'unsub'},
            {name:'fwdfriend'},
            {name:'archive'},
            {name:'updatelink'},
            {name:'targetcount', type:'int'},
            {name:'lastrunstatus', type:'int'},
            {name:'createdon',type:'date',dateFormat:'time'},
            {name:'campaignlog'},
            {name:'SMarketing'}
        ]);
            var expander = new Wtf.grid.RowExpander({
                tpl: new Wtf.XTemplate(
                    '<div style="display:block;width:100%;" />',
                    '<tpl for=".">{[this.f(values,xindex)]}</tpl></div>', {
                    f: function(val, idx){
                        var obj = val.campaignlog ;
                        var ret = "";
                        if(obj.length>0){
                        ret = "<table style='padding-left:10px;margin-top:7px;width:80%;border:1px solid grey'>  <caption style='margin-top:5px;'>"+WtfGlobal.getLocaleText("crm.campaigndetails.activityhistory")+"</caption><thead><tr>" +
                            "<td style='color:#15428B;border-bottom:1px solid black'>"+WtfGlobal.getLocaleText("crm.calendar.agendaview.header.date")+"</td><td style='color:#15428B;border-bottom:1px solid black'>"+WtfGlobal.getLocaleText("crm.campaigndetails.dashboardreport.sent")+"</td><td style='color:#15428B;border-bottom:1px solid black'>"+WtfGlobal.getLocaleText("crm.campaigndetails.dashboardreport.viewedcampaign")+"</td><td style='color:#15428B;border-bottom:1px solid black'>"+WtfGlobal.getLocaleText("crm.campaigndetails.dashboardreport.failed")+"</td><td style='color:#15428B;border-bottom:1px solid black'>"+WtfGlobal.getLocaleText("crm.campaigndetails.dashboardreport.vieweduser")+"</td><td width='30%' style='color:#15428B;border-bottom:1px solid black'>"+WtfGlobal.getLocaleText("crm.targetlists.title")+"</td><td style='color:#15428B;border-bottom:1px solid black'>"+WtfGlobal.getLocaleText("crm.campaigndetails.header.campcharts")+"</td></tr></thead><tbody>";
                            for(var cnt = 0; cnt < obj.length; cnt++) {
                                ret += "<tr><td>" + new Date(obj[cnt].activitydate).format(WtfGlobal.getOnlyDateFormat());+ "</td>";
                                ret += "<td>" + obj[cnt].totalsent + "</td>";
                                ret += "<td>" + obj[cnt].viewed + "</td>";
                                ret += "<td>" + obj[cnt].failed + "</td>";
                                ret += "<td>" + obj[cnt].usercount + "</td>";
                                ret += "<td>" + Wtf.util.Format.ellipsis(obj[cnt].targetlistname, 50) + "</td>";
                                ret += "<td align>" + "<a href = '#' onclick=Wtf.getCmp('"+this.id+"').openCampStausReport("+(idx-1)+",'"+obj[cnt].targetlistid+"') class='tempdetails' wtf:qtip='View the status of the selected campaign configuration i.e. number of mails sent, number of actual views and number of people unsubscribed.'>"+WtfGlobal.getLocaleText("crm.campaigndetails.dashboardreport.view")+"</a>" + "</td></tr>";
                            }
                        ret += "</tbody></table>";
                        } else{
                           ret = "<table style='padding-left:10px;margin-top:7px;width:80%;border:1px solid grey'>";
                            ret += "<tr><td><center>"+WtfGlobal.getLocaleText("crm.campaigndetails.activityhistory.mtytxt")+"</center></td></tr>";
                            ret += "</tbody></table>";
                        }
                        obj = val.SMarketing ;
                        var ret1 = "";
                        var SMarketingData=obj.SMarketingData;
                        if(SMarketingData!=undefined) {
                            ret1 = "<table style='padding-left:10px;margin-top:7px;width:60%;border:1px solid grey'>  <caption style='margin-top:5px;'>Scheduled Campaign Mail</caption><thead><tr>" +
                                "<td style='color:#15428B;border-bottom:1px solid black'>Date</td><td style='color:#15428B;border-bottom:1px solid black'>Time</td><td style='color:#15428B;border-bottom:1px solid black'>Delete</td></tr></thead><tbody>";
                            for(var cnt = 0; cnt < SMarketingData.length; cnt++) {
                                ret1 += "<tr><td>" + new Date(SMarketingData[cnt].Date).format(WtfGlobal.getOnlyDateFormat()); + "</td>";
                                ret1 += "<td>" + SMarketingData[cnt].Time + "</td>";
                                ret1 += "<td>"+"<a href=\"#\" onclick=removeSchedule('" + this.id + "','" + SMarketingData[cnt].id + "')><img src=\"../../images/sheet/cancel.gif\"/></a>"+"</td></tr>";
                            }
                            ret1 += "</tbody></table>";
                        }
                        return ret+ret1;
                    }
                },this)
        },this);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },Rec);

        this.EditorStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getCampEmailMarketList.do',
            baseParams:{
                flag:9,
                campid : this.campaignid
            },
            reader:EditorReader
        });
        this.loadGridStore();
        this.EditorColumn = new Wtf.grid.ColumnModel(
            [ expander,new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.confname"),//'Configuration Name',
                dataIndex:'name',
                xtype:'textfield',
                renderer : function(val) {
                    return "<a href = '#' class='campAddMarketing' wtf:qtip="+WtfGlobal.getLocaleText("crm.campaigndetails.header.confname.ttip")+"> "+val+"</a>";
                }

            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.originalemailtemp"),//'Original Email Template',
                dataIndex:'templatename',
                xtype:'textfield',
                renderer : function(val) {
                    return "<a href = '#' class='tempdetails' wtf:qtip="+WtfGlobal.getLocaleText("crm.campaigndetails.header.originalemailtemp.ttip")+"> "+val+"</a>";
                }
            }
//            ,{
//                header:'Test Your Campaign',
//                dataIndex:'targetcount',
//                renderer : function(a){
//                    var ret = "";
//                    if(a>0){
//                        ret = "<a href = '#' class='sendTestMail' wtf:qtip='Send e-mail campaigns to login User.'> Test Your Campaign </a>";
//                    }
//                    return ret;
//                }
//            },{
//                header:'Run Your Campaign',
//                dataIndex:'targetcount',
//                renderer : function(a){
//                    var ret = "";
//                    if(a>0){
//                        ret = "<a href = '#' class='campdetails' wtf:qtip='Send e-mail campaigns to the chosen target lists.'> Run Your Campaign </a>";
//                    }
//                    return ret;
//                }
//            }
            ,{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.campcharts"),//'Campaign Charts',
                dataIndex:'targetcount',
                align:'center',
                xtype:'textfield',
                renderer : function(a){
                    var ret = "";
                    if(a>0){
                        ret = "<a href = '#' class='campchart' wtf:qtip="+WtfGlobal.getLocaleText("crm.campaigndetails.header.campcharts.ttip")+">"+WtfGlobal.getLocaleText("crm.view")+"</a>";
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
                        ret = "<a href = '#' class='bouncereport' wtf:qtip="+WtfGlobal.getLocaleText("crm.campaigndetails.header.campstatusrepo.ttip")+"style='color:#15428B;text-decoration:none;' >"+WtfGlobal.getLocaleText("crm.view")+"</a>";
                    }
                    return ret;
                }
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.campviewrepo"),//"'Campaign View' Reports",
                align:'center',
                dataIndex:'id',
                renderer : function(val){
                    var ret = "";
                    if(val != ""){
                        ret = "<a href = '#' class='viewcampaignreport'  style='color:#15428B;text-decoration:none;' >"+WtfGlobal.getLocaleText("crm.view")+" </a>";
                    }
                    return ret;
                }
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.clickthrurepo"),//"'Click Through Reports',
                align:'center',
                xtype:'textfield',
                dataIndex:'id',
                renderer : function(val){
                    var ret = "";
                    if(val != ""){
                        ret = "<a href = '#' class='urlcampaignreport'  style='color:#15428B;text-decoration:none;' >"+WtfGlobal.getLocaleText("crm.view")+" </a>";
                    }
                    return ret;
                }
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon"),//'Created On',
                dataIndex:'createdon',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.lastrunstatus"),//'Last Run Status',
                align:'center',
                dataIndex:'lastrunstatus',
                xtype:'numberfield',
                renderer : function(val){
                    var ret = WtfGlobal.getLocaleText("crm.campaigndetails.lastrunstatus.unknown");//"Unknown";
                    if(val==1) {
                    	ret = WtfGlobal.getLocaleText("crm.campaigndetails.lastrunstatus.running");//"Running";
                    }else if(val==2) {
                    	ret = WtfGlobal.getLocaleText("crm.campaigndetails.lastrunstatus.completed");//"Completed";
                    }
                    else if(val == 3){
                        ret = WtfGlobal.getLocaleText("crm.campaigndetails.lastrunstatus.interrupted");//"Interrupted";
                    }
                    return ret;
                }
            }
        ]);

        this.deleteCon= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            handler:this.campDelete
        });

        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.Grid = new Wtf.KwlGridPanel({
            layout:'fit',
            store: this.EditorStore,
            cm: this.EditorColumn,
            sm : this.selectionModel,
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.campaigndetails.mtygrid.watermark"))//"No email campaigns have been created till now. Click \"Add Campaign Configuration\" on the taskbar to begin")
            },
            searchEmptyText:WtfGlobal.getLocaleText("crm.campaigndetails.searchtext.mtytxt"),//"Search by Configuration Name",
            plugins: expander,
            displayInfo:true,
            serverSideSearch : true,
            searchField:"name",
            tbar : ['-',this.addEmailMarketing,this.emailTempBtn,this.testCampaignBtn,this.runCampaignBtn,this.scheduleEmailMarketing,'->',help]
        });
        this.Grid.on('cellclick', this.afterGridCellClick, this);
        this.campaignpan= new Wtf.Panel({
            layout:'fit',
            border:false,
            items:this.Grid
        })
        this.add(this.campaignpan);
        this.on("editConfig", function(reply, sender, subject) {
            this.campaignSetup.getAt(this.campaignSetup.find("id", "reply")).data.description = reply;
            this.campaignSetup.getAt(this.campaignSetup.find("id", "sender")).data.description = sender;
            this.campaignSetup.getAt(this.campaignSetup.find("id", "subject")).data.description = subject;
            this.finalSetupView.refresh();
        }, this);
    },
    
    openCampStausReport:function(rowIndex, targetid){
        var chartid = "CampaignMailStatus"+this.Grid.store.getAt(rowIndex).data.id;
        var id = this.id+"graph";
        var swf = "../../scripts/graph/krwcolumn/krwcolumn/krwcolumn.swf";
        var dataflag = "crm/Campaign/campaignReport/getCampaignMailStatusChart.do?flag=22&campID="+this.campaignid+"&mailMarID="+this.Grid.store.getAt(rowIndex).data.id+"&targetid="+targetid;
        var mainID = this.mainTab.id;
        var xmlpath='../../scripts/graph/krwcolumn/examples/CampaignMailStatus/CampaignMailStatus_settings.xml';
        var param = "mailMarID="+this.Grid.store.getAt(rowIndex).data.id+"&campID="+this.campaignid;
        var tipTitle =this.Grid.store.getAt(rowIndex).data.name;
        var maintitle = Wtf.util.Format.ellipsis(tipTitle,20);
        var title = "<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.report.chartview")+"'>"+maintitle+"</div>";
        var showHtml = 'false';
        globalChart(chartid,id,swf,dataflag,mainID,xmlpath,Wtf.id(),showHtml,"","",tipTitle);    	
    },
   loadBounceReport: function(emailmarketingid,emailmarketingname   ){

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
       var panel=Wtf.getCmp('bounceReportPanel'+emailmarketingid);
       if(panel==null) {
          panel = new Wtf.Panel({
              layout:'fit',
              id:'bounceReportPanel'+emailmarketingid,
              title:"<div wtf:qtip="+WtfGlobal.getLocaleText("crm.campaigndetails.emailcampstat.ttip")+" wtf:qtitle="+WtfGlobal.getLocaleText("crm.campaigndetails.bouncerepo.emailcampstat.ttip")+">"+title+"</div>",
              closable:true,
              iconCls:"pwnd reportsTabIcon",
              items:[this.MembergridPanel]
          });
          this.mainTab.add(panel);
       } else {
           this.mainTab.setActiveTab(panel);
       }

          this.mainTab.activate(Wtf.getCmp('bounceReportPanel'+emailmarketingid));
   },

   removeSchedule : function (recid) {
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), WtfGlobal.getLocaleText("crm.confirmdeletemsg"), function(btn){
            if (btn == "yes") {
                Wtf.Ajax.requestEx({
                    url: Wtf.req.springBase+'emailMarketing/action/deleteEmailMarketingSchedule.do',
                    params:{
                        id : recid
                    }},this,
                    function(obj, response){
                        this.loadGridStore();
                        ResponseAlert(354);
                     },
                    function() {
                        Wtf.updateProgress();
                        ResponseAlert(355);
                });
            }
        },this);
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
                                            targets: targets,
                                            type:this.reportTypeCombo.getValue()

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
                        this.bounceReportGrid.getView().emptyText =WtfGlobal.getLocaleText("crm.emailmarketing.noviewedmails.mtytxt");// "Looks like there are no viewed emails in this email campaign";
                        Wtf.getCmp(this.id+"move_to_lead").show();
                    } else if(val==2){
                        this.bounceReportGrid.getView().emptyText = WtfGlobal.getLocaleText("crm.emailmarketing.nounviewedmails.mtytxt");//"Looks like there are no Unviewed emails in this email campaign";
                        Wtf.getCmp(this.id+"move_to_lead").hide();
                    } else if(val==3){
                        cm.setHidden(6,false);
                        this.bounceReportGrid.getView().emptyText = WtfGlobal.getLocaleText("crm.emailmarketing.nounsubscribedmails.mtytxt");//"Looks like there are no Unsubscribed emails in this email campaign";
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
        var name="EmailCampaignStatusReport";  // dependency in ExportInterface.js
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
    },
    showScheduleWindow: function(sel){
        var hrStore = new Wtf.data.SimpleStore({
            fields: ["id", "value"],
            data: [["0", "00:00"], ["1", "00:30"], ["2", "01:00"], ["3", "01:30"], ["4", "02:00"], ["5", "02:30"], ["6", "03:00"],
                ["7", "03:30"], ["8", "04:00"], ["9", "04:30"], ["10", "05:00"], ["11", "05:30"], ["12", "06:00"], ["13", "06:30"],
                ["14", "07:00"], ["15", "07:30"], ["16", "08:00"], ["17", "08:30"], ["18", "09:00"], ["19", "09:30"], ["20", "10:00"],
                ["21", "10:30"], ["22", "11:00"], ["23", "11:30"], ["24", "12:00"], ["25", "12:30"], ["26", "13:00"], ["27", "13:30"],
                ["28", "14:00"], ["29", "14:30"], ["30", "15:00"], ["31", "15:30"], ["32", "16:00"], ["33", "16:30"], ["34", "17:00"],
                ["35", "17:30"], ["36", "18:00"], ["37", "18:30"], ["38", "19:00"], ["39", "19:30"], ["40", "20:00"], ["41", "20:30"],
                ["42", "21:00"], ["43", "21:30"], ["44", "22:00"], ["45", "22:30"], ["46", "23:00"], ["47", "23:30"]]
        });
        var hrCombo = new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("crm.emailmarketing.schedulingcamp.scheduletime"),//"Schedule Time ",
            store: hrStore,
            displayField: "value",
            valueField: "id",
            width: 200,
            editable:false,
            allowBlank: false,
            mode: "local",
            triggerAction: "all",
            renderer:WtfGlobal.loginUserTimeRendererTZ
        });
        var dt = new Date();
       // dt.setDate(dt.getDate() + 1);// Kuldeep Singh : To Enable Today's date for Campaign Scheduling
        var scheduleDate = new Wtf.form.DateField({
            fieldLabel:WtfGlobal.getLocaleText("crm.emailmarketing.schedulingcamp.deliverydate"),// "Delivery Date ",
            border: false,
            editable:false,
            minValue: dt,
            width: 200,
            allowBlank: false,
            renderer:WtfGlobal.onlyDateRendererTZ
        });
        var schedularForm = new Wtf.form.FormPanel({
            region: "center",
            border: false,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px 10px 10px 30px;',
            items: [scheduleDate, hrCombo/*, {
                xtype: "panel",
                border: false,
                html: "<center><span style='color: green;font-weight:bold'>As per CST</span></center>"
            }*/]
        });
        var win = new Wtf.Window({
            title:WtfGlobal.getLocaleText("crm.emailmarketing.schedulingcamp.scheduledelivery"),// "Schedule delivery",
            modal: true,
            height: 210,
            iconCls:"pwnd favwinIcon",
            width: 380,
            resizable: false,
            layout: "border",
            items: [{
                region: "north",
                bodyStyle: "background-color: white",
                border:false,
                height: 65,
                html: getTopHtml(WtfGlobal.getLocaleText("crm.calendar.scheduletitle"),WtfGlobal.getLocaleText("crm.emailmarketing.schedulingcamp.wintml") ,"../../images/activity1.gif")
            }, schedularForm],
            buttons: [{
                text: WtfGlobal.getLocaleText("crm.calendar.scheduletitle"),//"Schedule",
                scope: this,
                handler: function(){
                    if(schedularForm.form.isValid()){
                        var sTime = hrCombo.getValue();
                        var index = hrCombo.store.find("id", sTime);
                        if(index != -1)
                            sTime = hrCombo.store.getAt(index).data["value"];
                        else
                            sTime = "00:00";
                        var sDt = scheduleDate.getValue().getTime();
                        var d = scheduleDate.getValue().clearTime(true).getTime();
                        sDt = new Date(d+Date.parseDate(sTime,"H:i").getTime()-new Date().clearTime(true).getTime()).getTime();
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.springBase+'emailMarketing/action/scheduleEmailMarketing.do',
                            params: {
                                flag: 28,
                                emailmarketingid: sel.data["id"],
                                scheduledate: sDt,
                                scheduletime: sTime
                }
                        }, this, function(action, response){
                            if(action.success) {
                                this.loadGridStore();
                                ResponseAlert(104);
                            }/* else {
                                WtfComMsgBox(["Schedule Campaign",action.errormsg],0);
                            }*/
                            win.close();
                        }, function(action, response){
                            win.close();
                        });
                    }
                    else
                        ResponseAlert(103);
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//"Cancel",
                scope: this,
                handler: function(){
                    win.close();
                }
            }]
        });
        win.show();
    },

afterGridCellClick : function(Grid,rowIndex,columnIndex, e ) {
        if(this.Grid.getSelectionModel().getSelections().length==1) {
            this.editEmailMarketing.enable();
        } else{
            this.editEmailMarketing.disable();
        }
        var event = e ;
        if(event.getTarget("a[class='campdetails']")) {
            Wtf.Ajax.timeout=1200000;
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.emailmarketing.schedulingcamp.sendingmail"));//"Sending mail...");
            Wtf.Ajax.requestEx({
                url: Wtf.req.springBase+'emailMarketing/action/sendEmailMarketMail.do',
                params:{
                    emailmarkid : Grid.store.getAt(rowIndex).data.id,
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
        }
        if(event.getTarget("a[class~='campchart']")) {
            var chartid = "CampaignMailStatus"+Grid.store.getAt(rowIndex).data.id;
            var id = this.id+"graph";
            var swf = "../../scripts/graph/krwcolumn/krwcolumn/krwcolumn.swf";
            var dataflag = "crm/Campaign/campaignReport/getCampaignMailStatusChart.do?flag=22&campID="+this.campaignid+"&mailMarID="+Grid.store.getAt(rowIndex).data.id+"";
            var mainID = this.mainTab.id;
            var xmlpath='../../scripts/graph/krwcolumn/examples/CampaignMailStatus/CampaignMailStatus_settings.xml';
            var param = "mailMarID="+Grid.store.getAt(rowIndex).data.id+"&campID="+this.campaignid;
            var tipTitle =Grid.store.getAt(rowIndex).data.name;
            var maintitle = Wtf.util.Format.ellipsis(tipTitle,20);
            var title = "<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.report.chartview")+"'>"+maintitle+"</div>";
            var showHtml = 'false';
            globalChart(chartid,id,swf,dataflag,mainID,xmlpath,Wtf.id(),showHtml,"","",tipTitle);
        }
        var panel = Wtf.getCmp('comEmailMarket'+this.campaignid+'_'+this.mode+"_"+Grid.store.getAt(rowIndex).data.id);
        if(panel!=null){
            this.editEmailMarketing.disable();
            //this.scheduleEmailMarketing.disable();
            this.addEmailMarketing.disable();
            //this.testCampaignBtn.disable();
            //this.runCampaignBtn.disable();
        }
        //else{
            this.scheduleEmailMarketing.enable();
            this.testCampaignBtn.enable();
            this.runCampaignBtn.enable();
        //}
        if(event.getTarget("a[class='campAddMarketing']")) {
            this.emailMarketing(1);
        }
        if(event.getTarget("a[class='sendTestMail']")) {
            Wtf.showTestMailWindow(Grid,rowIndex,this.campaignid);

        }if(event.getTarget("a[class='bouncereport']")) {
            var emailmarketingid =Grid.store.getAt(rowIndex).data.id;
            var emailmarketingname =Grid.store.getAt(rowIndex).data.name;
            this.loadBounceReport(emailmarketingid,emailmarketingname);
        }
        if(event.getTarget("a[class='resumecamp']")) {
        	this.askConfirmToResumeCamp(Grid.store.getAt(rowIndex));
        }

        if(event.getTarget("a[class='tempdetails']")) {
            var recdata = Grid.getSelectionModel().getSelected().data;
            var panel = Wtf.getCmp('template_wiz_win'+recdata.templateid);
            var tipTitle=WtfGlobal.getLocaleText({key:"crm.campaigndetails.edittemplate",params:[recdata.templatename]});//recdata.templatename+" : Edit Template";
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
                        title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.header.emailtemp")+"'>"+title+"</div>",
                        tipTitle:tipTitle
                    });
                    this.mainTab.add(panel);
                }
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();

        }
        if(event.getTarget("a[class='viewcampaignreport']")) {
            var recordData = Grid.getSelectionModel().getSelected().data;
            var ViewPanel=Wtf.getCmp('view_campaignmodule_tab_id'+recordData.id);
            if(ViewPanel==null)
            {
                ViewPanel=new Wtf.campaignViewReport({
                    title:WtfGlobal.getLocaleText("crm.campaigndetails.emailviewreport"),//"Email View Report",
                    mainTab:this.mainTab,
                    id:"view_campaignmodule_tab_id"+recordData.id,
                    emailmarketingName:recordData.name,
                    emailmrktid:recordData.id
                })
                 this.mainTab.add(ViewPanel);
            }
             this.mainTab.setActiveTab(ViewPanel);
             this.mainTab.doLayout();

        }
        if(event.getTarget("a[class='urlcampaignreport']")) {
            var recordData = Grid.getSelectionModel().getSelected().data;
            var URLPanel=Wtf.getCmp('url_campaignmodule_tab_id'+recordData.id);
            if(URLPanel==null)
            {
                var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.header.clickthrurepo");//"Click Through Reports";
                var title = Wtf.util.Format.ellipsis(tipTitle,18);
                URLPanel=new Wtf.campaignURLReport({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.header.clickthrurepo")+"'>"+title+"</div>",
                    mainTab:this.mainTab,
                    id:"url_campaignmodule_tab_id"+recordData.id,
                    emailmarketingName:recordData.name,
                    emailmrktid:recordData.id
                })
                 this.mainTab.add(URLPanel);
            }
             this.mainTab.setActiveTab(URLPanel);
             this.mainTab.doLayout();

        }
    },
    testCamp:function(){
        if(this.Grid.getSelectionModel().getCount()!=1){
        	WtfComMsgBox([WtfGlobal.getLocaleText("crm.campaigndetails.msg.title"),WtfGlobal.getLocaleText("crm.emailmarketing.selcampconffortestcamp.msg")],0);//"Please select a Campaign Configuration to Test Your Campaign"],0);
            return;
        }
        var rec = this.Grid.getSelectionModel().getSelected();
        var rowIndex = this.Grid.getStore().indexOf(rec);
        Wtf.showTestMailWindow(this.Grid,rowIndex,this.campaignid);
    },
    askConfirmToRunCamp: function(){
        if(this.Grid.getSelectionModel().getCount()!=1){
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
                	var rec = this.Grid.getSelectionModel().getSelected();
                    this.runCamp(rec);
                }else{
                    return;
                }
            }
        });
    },
    askConfirmToResumeCamp: function(rec){
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),
            msg:WtfGlobal.getLocaleText("crm.campaigndetails.resumecampconfirmmsg"),//"Are you sure you want to resume this campaign?",
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            fn:function(button){
                if(button=='yes')
                {
                    this.runCamp(rec,true);
                }else{
                    return;
                }
            }
        });
    },
    runCamp:function(rec,resume){
        Wtf.Ajax.timeout=1200000;
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.emailmarketing.schedulingcamp.sendingmail"));//"Sending mail...");
            Wtf.Ajax.requestEx({
                url: Wtf.req.springBase+'emailMarketing/action/sendEmailMarketMail.do',
                params:{
                    emailmarkid : rec.data.id,
                    campid : this.campaignid,
                    resume:resume,
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
    createCampaignsStatusReportGrid:function(emailmarketingid){
        var campaignsRec =new Wtf.data.Record.create([
            {name:'email'},
            {name:'fname'},
            {name:'lname'},
            {name:'status'}

        ]);
    this.campaignsStatusReportColumn = new Wtf.grid.ColumnModel(
            [new Wtf.grid.CheckboxSelectionModel({}),new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.email"),//'Email Address',
                xtype:'textfield',
                dataIndex:'email'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.fname"),//'First Name',
                xtype:'textfield',
                dataIndex:'fname'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.lname"),//'Last Name',
                xtype:'textfield',
                dataIndex:'lname'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.status"),//'Status',
                xtype:'textfield',
                dataIndex:'status'
            }

        ]);
        this.campaignsStatusReportStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getCampaignsStatus.do',
            baseParams:{
                emailmarketingid:emailmarketingid
            },
            reader:new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
            },campaignsRec)
        });
        this.campaignsStatusReportStore.load({params : {start:0,limit:15}});

        this.campaignsStatusReportGrid = new Wtf.KwlGridPanel({

            store: this.campaignsStatusReportStore,
            cm: this.campaignsStatusReportColumn,
            border : false,
            loadMask : true,
            searchEmptyText:WtfGlobal.getLocaleText("crm.emailmarketing.searchbymailtext"),//"Search by email",
            searchField:"email",
            viewConfig: {
                forceFit:true
               // emptyText:'Looks like there are no bounced emails in this email campaign, or the bounced targets have already been removed.'
            },
            bbar: new Wtf.PagingSearchToolbar({
                pageSize: 30,
                displayInfo: true,
                store: this.campaignsStatusReportStore,
                plugins:this.pP = new Wtf.common.pPageSize({
                })
            })
        });


    },

    emailMarketing : function(mode) {
        this.mode = mode;
        var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.addcampconfBTN");//"Add Campaign Configuration ";
        var IconCls = "pwnd addEmailMarketingTab";
        this.emailMarkId = "0";

        // Store for Default Values used in Email Template
        this.defaultRec = Wtf.data.Record.create([{
            name: "id"
        },{
            name: "varname"
        },{
            name: "varval"
        }]);
        this.defaultValues = [];

        if(mode==1) {
            this.recData = this.Grid.getSelectionModel().getSelected().data;
            tipTitle=WtfGlobal.getLocaleText({key:"crm.campaigndetails.editcampconf",params:[this.recData.name]});//"Edit Campaign Configuration : "+this.recData.name;
            IconCls ="pwnd editEmailMarketingTab";
            this.emailMarkId = this.recData.id;

    		Wtf.Ajax.requestEx({
				url : Wtf.req.springBase+'emailMarketing/action/getEmailMarketingDefaults.do',
                params:{
                    emailmarketingid:this.emailMarkId
                }},this,
				function(res) {
    				this.defaultValues = res.data;
				}
			);
        }
        this.panel = Wtf.getCmp('comEmailMarket'+this.campaignid+'_'+this.mode+"_"+this.emailMarkId);//+this.recDataId);

        var title = Wtf.util.Format.ellipsis(tipTitle,17);
        if(this.panel==null) {

            this.addEmailMarketing.disable();
            this.editEmailMarketing.disable();
            //this.scheduleEmailMarketing.disable();
            //this.testCampaignBtn.disable();
            //this.runCampaignBtn.disable();
            this.setupRec = Wtf.data.Record.create([{
                name: "id"
            },{
                name: "title"
            },{
                name: "description"
            },{
                name: "licls"
            }]);
            this.campaignSetup = new Wtf.data.SimpleStore({
                fields: ["id", "title", "description", "licls"]
            });
            this.addEmailMarketFun();
            this.card1 = new Wtf.ux.Wiz.Card({
                title: WtfGlobal.getLocaleText("crm.emailmarketing.campconf.setemaildetails"),//"Set Email Details",
                layout: "fit",
                border: false,
                items: this.addEmailMarketCmp
            });
            this.card2 = new Wtf.ux.Wiz.Card({
                border: false,
                title: WtfGlobal.getLocaleText("crm.emailmarketing.campconf.choosetemplate"),//"Choose an Email Template",
                layout: "fit"
            });
            this.card2.on("show", this.showTemplateSelector, this);
            this.card3 = new Wtf.ux.Wiz.Card({
                border: false,
                title: WtfGlobal.getLocaleText("crm.emailmarketing.campconf.edittemplate"),//"Edit Email Template",
                layout: "fit"
            });
            this.card3.on("show", this.showTemplateEditor, this);
            this.card4 = new Wtf.ux.Wiz.Card({
                border: false,
                title: WtfGlobal.getLocaleText("crm.emailmarketing.campconf.entermsg"),//"Enter your Plain-Text Message",
                layout: "fit"
            });
            this.card4.on("show", this.showPlainMessageEditor, this);
            this.card5 = new Wtf.ux.Wiz.Card({
                layout: "fit",
                border: false,
                title: WtfGlobal.getLocaleText("crm.emailmarketing.campconf.finalcampsetup")//"Final Campaign Setup"
            });
            this.card5.on("show", this.showFinalSetup, this);
            this.panel=new Wtf.ux.Wiz({
                closable: true,
                iconCls:IconCls,
                id : 'comEmailMarket'+this.campaignid+'_'+this.mode+"_"+this.emailMarkId,
                title:"<div wtf:qtip=\""+Wtf.util.Format.htmlEncode(tipTitle)+"\"wtf:qtitle='Campaign Configuration'>"+title+"</div>",
                headerConfig: {
                    title:"<div wtf:qtip=\""+Wtf.util.Format.htmlEncode(tipTitle)+"\"wtf:qtitle='Campaign Configuration'>"+tipTitle+"</div>"
                },
                cards:[this.card1, this.card2, this.card3, this.card4, this.card5]
            });
            this.panel.on("beforeNextcard", this.beforeNext, this);
            this.panel.on("beforefinish", this.beforeFinish, this);
            this.mainTab.add(this.panel);
        }
        this.mainTab.setActiveTab(this.panel);
        this.mainTab.doLayout();
        this.mainTab.on('remove',function(tp,panel){
            if(panel.id=='comEmailMarket'+this.campaignid+'_'+this.mode+"_"+this.emailMarkId) {
                this.addEmailMarketing.enable();
                this.Grid.getSelectionModel().clearSelections();
            }
        },this)
    },
    showFinalSetup: function(){
        var finalSetup = Wtf.getCmp("final_setup_card");
        if(!finalSetup) {
            this.finalSetupView = new Wtf.DataView({
                store: this.campaignSetup,
                itemSelector: "final_setup_card",
                tpl: new Wtf.XTemplate('<ul class="finalList"><tpl for=".">{[this.f(values)]}</tpl></div>', {
                    f: function(val){
                        return "<li class='" + val.licls + "'><label>" + val.title + "</label>" + val.description + "</li>";
                    },
                    scope: this
                })
            });
            finalSetup = new Wtf.Panel({
            	region:'center',
            	layout:'fit',
                id: "final_setup_card",
                bodyStyle: "background-color: white",
                //autoScroll: true,
                items: this.finalSetupView
            });
            this.createCanSpamForm();
            var temp = new Wtf.Panel({
            	layout:'border',
            	items:[new Wtf.Panel({
            		region:'west',
            		width:450,
            		layout:'border',
            		items:[{
            			region: 'north',
            			height: 75,
            			border: false,
            			bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
            			html: getTopHtml('CAN-SPAM Act Terms','Please read the CAN-SPAM Act terms.')
            		},{
            			border:false,
            			autoScroll:true,
            			bodyStyle:'background:PapayaWhip;padding:10px',
            			autoLoad:'canspam.txt',
            			region: 'center'
            		},this.southform]
            	}),finalSetup]
            });
            this.card5.add(temp);
        }
        this.card5.doLayout();
    },

    createCanSpamForm:function(){
    	this.chkCSAccept=new Wtf.form.Checkbox({
                hideLabel: true,
                name: 'csaccept',
                boxLabel:WtfGlobal.getLocaleText("crm.emailmarketing.campconf.acceptterms")//'I accept all the terms'
        });

        this.checkPane= new Wtf.form.FormPanel({
            region:'south',
            height:25,
            bodyStyle: 'background:#f1f1f1;font-size:10px;border-top: 1px solid #BFBFBF;',
            border:false,
            labelWidth:125,
            items:[{
                name:'userid',
                xtype:'hidden'
            },this.chkCSAccept]
        });

        this.southform= new Wtf.Panel({
            region:'south',
            layout:'border',
            height:125,
            bodyStyle: 'background:#f1f1f1;font-size:10px;border-top: 1px solid #BFBFBF;',
            border:false,
            items:[{
                html:"<span style='font-weight:bold'>Disclaimer :</span> The CAN-SPAM act is very clear in that you are not to harvest email addresses and send bulk emails to people who don't want them. It goes further in regulating the types of commercial messages you can send so as to not be deceptive or misleading. Buying email lists, borrowing email lists, and making lists from people with whom you don't have a relationship puts your company at tremendous risk.",
                border:false,
                bodyStyle:'padding:10px ;background-color:#b0e0e6',
                xtype:'panel',
                region:'center'
            },this.checkPane]
        });

        //this.createDetailWindow();
        //this.chkAccept.on("check",function(cb, flag){if(flag===true&&this.senderDetail.getForm().isValid()==false)this.senderWin.show();},this);
    },

    showTemplateEditor: function(){
        var templateEditor = Wtf.getCmp("wizardTemplateEditor_" + this.id);
        if(templateEditor == null) {           
            templateEditor = new Wtf.EmailTemplateEditor({
            	region:'center',
                id: "wizardTemplateEditor_" + this.id,
                tplVariables:[{
                	gid:'mailsender', gname:'Sender (You)',
                	gvars:[{id:'fname',name:'First name'},{id:'lname',name:'Last name'},{id:'phone',name:'Phone No'},{id:'email',name:'Email'}]
                },{
                	gid:'mailrecipient', gname:'Recipient',
                	gvars:[{id:'fname',name:'First name'},{id:'lname',name:'Last name'},{id:'phone',name:'Phone No'},{id:'email',name:'Email'}]
                },{
                	gid:'company', gname:'Company',
                	gvars:[{id:'cname',name:'Sender Company name'},{id:'caddress',name:'Sender Company address'},{id:'cmail',name:'Sender Company email'},{id:'rname',name:'Recipient Company name'}]
                },{
                	gid:'other', gname:'Other',
                	gvars:[{id:'currentyear',name:'Current year'}]
                }],
                defaultValues:this.defaultValues
            });

            this.card3.add(templateEditor);
            this.card3.doLayout();
            
            var selTemp = this.selEmailTempCmp.selectedTemplate;
            if(selTemp) {
            	var reqParams = {
        	            url: Wtf.req.springBase+'emailMarketing/action/getTemplateContent.do',
        	            params: {
        	                flag: 27,
        	                templateid: selTemp.tempRec.data.templateid,
        	                templateClass:selTemp.tempRec.data.templateclass
        	            }
        	        };
            	if(this.mode==1&&this.selEmailTempCmp.tplChanged!==true){
            		reqParams = {
                            url: Wtf.req.springBase+'emailMarketing/action/getEmailMrktContent.do',
                            params: {
                                flag: 26,
                                marketid: this.recData.id
                            }
                        };
            	}
    	    	Wtf.Ajax.requestEx(reqParams, templateEditor, function(action, response){
    	            if(action.success){
    	                this.editorHtmlComp.setHtml(unescape(action.data.html));
    	            }
    	        }, function(){
    	            ResponseAlert([WtfGlobal.getLocaleText("crm.msg.FAILURETITLE"),WtfGlobal.getLocaleText("crm.campaigndetails.failtoloadtempmsg")]);
    	        });   
            }
        }
    },
    showPlainMessageEditor: function(){
        var plainMessage = Wtf.getCmp("Plaintext_textarea_form");
        if(!plainMessage) {
            var val = Wtf.getCmp("wizardTemplateEditor_" + this.id).editorHtmlComp.getPlainText();
            plainMessage = new Wtf.form.FormPanel({
                cls: "plainTextForm",
                id: "Plaintext_textarea_form",
                defaults: {
                    labelStyle: "width: 100%; margin-bottom: 7px;",
                    ctCls: "newTicketField"
                },
                items: [this.txtArea  = new Wtf.form.TextArea({
                    value: val.trim(),
                    id: "mail_plaintext_textfield",
                    fieldLabel: "This plain-text email is displayed if recipients can't (or won't) display your HTML email",
                    xtype: "textarea",
                    height: "95%",
                    width: "98%"
                })]
            });
            this.card4.add(plainMessage);
           this.txtArea.on('resize',function(obj){
                var wid = obj.getSize();
                if(wid.height<100)
                    obj.setHeight((0.6*(wid.width)));
            },this)
        }
    },
    addEmailMarketFun: function() {
        var recDataIdEmail="";
        if(this.mode==1){
            recDataIdEmail = this.recData.id;
        }
        var addEmailMarketCmp = Wtf.getCmp("addEmailMarketCmp_" + this.id+this.mode+recDataIdEmail);
        if(!addEmailMarketCmp) {
            this.addEmailMarketCmp = new Wtf.addEmailMarketCmp({
                id: "addEmailMarketCmp_" + this.id+this.mode+recDataIdEmail,
                emailmarkid: this.mode==1 ? this.recData.id :'',
                campaignid: this.campaignid,
                templateid: this.mode==1 ? this.recData.templateid :'',
                recData: this.recData,
                mode: this.mode,
                campaignname:this.campaignname,
                captureleadstatus:this.recData!=undefined?this.recData.captureLead:false,
                mainTab:this.mainTab
            });
        }
    },
    showTemplateSelector: function() {
        var selEmailTempCmp = Wtf.getCmp("selEmailTempCmp_"+this.templateid);
        if(selEmailTempCmp == null) {
            this.selEmailTempCmp =new Wtf.campaignMailTemplate({
                id : "selEmailTempCmp_"+this.templateid,
                selectedTemplate: (this.mode == 1) ? this.recData.templateid : null,
                border: false,
                mainTab:this.mainTab
            });
            this.selEmailTempCmp.tempCount;
            this.card2.add(this.selEmailTempCmp);
            this.card2.doLayout();
        }
    },
    storeSetupInformation: function(obj, activeCard){
        switch(activeCard) {
            case 0:
                this.campaignSetup.add(this.getInitialSetup());
                break;
            case 1:
                this.campaignSetup.add(this.getTemplateSetup());
                break;
//            case 2:
//                break;
            case 3:
                this.campaignSetup.add(this.getPlainTextSetup());
                break;
        }
    },
    getInitialSetup: function(){
        var _aEM = this.addEmailMarketCmp;
        var temp = [];
        var list = _aEM.getList();
        var desc = "Deskera CRM will deliver this to the ";
        var _lN = "";
        for(var cnt = 0; cnt < list.length; cnt++)
            _lN += list[cnt].data["listname"] + ",";
        var liCls;
        if(_lN != ""){
            desc += _lN.substring(0, (_lN.length - 1));
            liCls = "success";
        } else {
            desc = "No list selected to send this campaign to."
            liCls = "error";
        }
        this.removeSetupRec("list");
        temp[temp.length] = new this.setupRec({
            id: "list",
            title: "List",
            licls: liCls,
            description: desc
        });
        desc = _aEM.getReplyMail();
        this.removeSetupRec("reply");
        temp[temp.length] = new this.setupRec({
            id: "reply",
            title: "Replies"+"<img onclick='editValue(\""+_aEM.id+"\", \""+this.id+"\")' src='images/edit.gif' style=\"cursor:pointer; margin-right:5px; float:right; height : 13px\" title='Edit Configurations'></img>",
            licls: (desc != "") ? "success" : "error",
            description: (desc != "") ? desc : "No reply email specifed."
        });
        desc = _aEM.getSenderMail();
        this.removeSetupRec("sender");
        temp[temp.length] = new this.setupRec({
            id: "sender",
            title: "Sender email"+"<img onclick='editValue(\""+_aEM.id+"\", \""+this.id+"\")' src='images/edit.gif' style=\"cursor:pointer; margin-right:5px; float:right; height : 13px\" title='Edit Configurations'></img>",
            licls: (desc != "") ? "success" : "error",
            description: (desc != "") ? desc : "No sender email specified."
        });
//        desc = "All the tags have be specified.";
//        if(_aEM.getUnsubscribeLink() == "" || _aEM.getForwardLink() == "" || _aEM.getUpdateLink() == "" || _aEM.getArchiveLink() == "") {
//            desc = "";
//        }
//        this.removeSetupRec("tags");
//        temp[temp.length] = new this.setupRec({
//            id: "tags",
//            title: "Tag setup",
//            licls: (desc != "") ? "success" : "error",
//            description: (desc != "") ? desc : "Some of the tags are not specified."
//        });
        return temp;
    },
    getTemplateSetup: function(){
        var _aEM = this.addEmailMarketCmp;
        var temp = [];
        var selTemp = this.selEmailTempCmp.getSelectedTemplate();
        if(selTemp!=null) {
            selTemp = selTemp.tempRec;
            this.removeSetupRec("subject");
            var desc = _aEM.getSubject();
            temp[temp.length] = new this.setupRec({
                id: "subject",
                title: "Subject line"+"<img onclick='editValue(\""+_aEM.id+"\", \""+this.id+"\")' src='images/edit.gif' style=\"cursor:pointer; margin-right:5px; float:right; height : 13px\" title='Edit Configurations'></img>",
                licls: "success",
                description: desc
            });
            this.removeSetupRec("html");
            temp[temp.length] = new this.setupRec({
                id: "html",
                title: "HTML email",
                licls: "success",
                description: "You're sending an HTML email from the <span class='boldtext'>" + selTemp.data["templatename"] + "</span> template"
            });
        }
        return temp;
    },
    getPlainTextSetup: function(){
        var temp = [];
        var desc = Wtf.getCmp("mail_plaintext_textfield").getValue().trim();
        var licls = "error";
        if(desc != ""){
            desc = "You included a plain-text version.";
            licls = "success";
        } else
            desc = "You included a plain-text version.";
        this.removeSetupRec("plainmsg");
        temp[temp.length] = new this.setupRec({
            id: "plainmsg",
            title: "Plain-text email",
            licls: licls,
            description: desc
        });
        return temp;
    },
    removeSetupRec: function(id){
        if(id != "") {
            var _cR = this.campaignSetup.query("id", id, false, true);
            _cR = _cR.items;
            if(_cR.length > 0)
                for(var cnt = 0; cnt < _cR.length; cnt++)
                    this.campaignSetup.remove(_cR[cnt]);
        }
    },
    beforeNext: function(obj, activeCard) {
        var flg = true;
        if(activeCard == 0) {
            flg = this.addEmailMarketCmp.activityform.form.isValid();
            if(flg){
                var list = this.addEmailMarketCmp.getList();
                if(list.length > 0){
                    flg = true;
                } else {
                    flg = false;
                    WtfComMsgBox(956,0);
                }
            }
                else{
                    WtfComMsgBox(955,0);
                    return false;
                }

        } else if(activeCard == 1) {
            if(this.selEmailTempCmp.tempCount==0){
                WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.campaigndetails.createmailtemplateb4proceedmsg")], 0);
                return false;
            }else{
                var selTemp = this.selEmailTempCmp.getSelectedTemplate();
                if(selTemp != null && selTemp.tempRec) {
                    var _tE = Wtf.getCmp("wizardTemplateEditor_" + this.id);
                    if(!!this.selEmailTempCmp.tplChanged&&_tE && _tE.templateid!=selTemp.tempRec.data.templateid){
                    	Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.springBase+'emailMarketing/action/getTemplateContent.do',
                            params: {
                                flag: 27,
                                templateid: selTemp.tempRec.data.templateid,
                                templateClass:selTemp.tempRec.data.templateClass
                            }
                        }, _tE, function(action, response){
                            if(action.success){
                            	
                                this.editorHtmlComp.setHtml(unescape(action.data.html)); 
                            }
                            Wtf.updateProgress();
                        }, function(){
                        	Wtf.updateProgress();
                            ResponseAlert([WtfGlobal.getLocaleText("crm.msg.FAILURETITLE"),WtfGlobal.getLocaleText("crm.campaigndetails.failtoloadtempmsg")]);
                        });
                    }
                    flg = true;
                    this.tempID = selTemp.tempRec.data.templateid;
                } else{
                    ResponseAlert(600);
                    flg = false;
                }
            }
        }
        if(flg)
            this.storeSetupInformation(obj, activeCard);
        return flg;
    },
    beforeFinish: function(wizObj){
        if(this.chkCSAccept.getValue()){
            this.panel.nextButton.disable();
            var _aEM = this.addEmailMarketCmp;
            var _tE = Wtf.getCmp("wizardTemplateEditor_" + this.id);
            var senderId = _aEM.getSenderMail();
            var _tD = "[";
            var _tL = _aEM.getList();
            for(var cnt = 0; cnt < _tL.length; cnt++) {
                _tD += '{"listid" : "'+_tL[cnt].data.listid+'"},';
            }
            _tD = _tD.substring(0, (_tD.length - 1)) + "]";
            var captureLead = _aEM.captureLead.checked;
            Wtf.Ajax.requestEx({
                url: Wtf.req.springBase+'emailMarketing/action/saveCampEmailMarketConfig.do',
                params:{
                    name: WtfGlobal.HTMLStripper(_aEM.getName()),
                    unsub: "",
                    fwdfriend: "",
                    archive: "",
                    updatelink: "",
                    captureLead:captureLead,
                    marketingsubject: _aEM.getSubject(),
                    fromaddress: senderId,
                    replyaddress: _aEM.getReplyMail(),
                    fromname: _aEM.getFromName(),
                    inboundemail: senderId,
                    templateid: this.tempID,
                    campid: this.campaignid,
                    csaccept:this.chkCSAccept.getValue(),
                    targetlist: _tD,
                    colortheme: _tE.themePanel.getSelectedTheme(),
                    htmlcont: _tE.editorHtmlComp.getHtml(),
                    plaincont: Wtf.getCmp("mail_plaintext_textfield").getValue().trim(),
                    emailmarkid: this.mode ==1 ? this.recData.id: '',
                    mode: this.mode,
                    flag: 10,
                    defaulttemplatestore:Wtf.util.JSON.encode(this.defaultValues)
                }
            },this,
            function(res,req){
                WtfComMsgBox(953,0);
                this.loadGridStore();
                Wtf.getCmp('comEmailMarket'+this.campaignid+'_'+this.mode+"_"+this.emailMarkId).closePanel();
                this.addEmailMarketing.enable();
                this.Grid.getSelectionModel().clearSelections();
            },
            function() {
                WtfComMsgBox(911,1);
                this.panel.nextButton.enable();
            });
        } else {
            WtfComMsgBox(17,0);
        }
    },

    loadGridStore : function() {
        this.EditorStore.load({params : {start:0,limit:15}});
    },

    targetListHandler : function() {
        var tlId = 'targetlistgrid';
        var targetComp = Wtf.getCmp(tlId );
        if(targetComp==null) {
            targetComp=new Wtf.targetListDetails({
                title:'Target Lists',
                id:tlId,
                mainTab:this.mainTab
            })
            this.mainTab.add(targetComp);
        }
        this.mainTab.setActiveTab(targetComp);
        this.mainTab.doLayout();
    }
});

function removeSchedule(cid,rec){
    Wtf.getCmp(cid).removeSchedule(rec);
}

Wtf.emailTemplate = function (config){
    Wtf.emailTemplate.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.emailTemplate,Wtf.Panel,{
    closable:true,
    layout : 'fit',
    title : WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.emailTempBTN"),//'Email Templates',
    id : this.id!="emailTemplatedashboard"?'emailTemplate':this.id,
    border:false,
    iconCls:"pwndnewCRM emailtemplateTabicon",
    initComponent: function(config) {
        Wtf.campaignDetails.superclass.initComponent.call(this,config);
        this.getEditorGrid();
    },

    getEditorGrid : function () {
        var Rec = new Wtf.data.Record.create([
            {name:'templateid'},
            {name:'templatename'},
            {name:'description'},
            {name:'subject'},
            {name:'bodyhtml'},
            {name:'createdon',type:'date',dateFormat:'time'},
            {name:'templateclass'}
        ]);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },Rec);

        this.EditorStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getEmailTemplateList.do',
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            baseParams:{
                flag:1,
                excludeDefault:true
            },
            method:'post',
            reader:EditorReader
        });
        this.EditorStore.load();
        this.DefaultEditorStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getEmailTemplateList.do',
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            baseParams:{
                flag:1,
                defaultOnly:true
            },
            method:'post',
            reader:EditorReader
        });
        this.DefaultEditorStore.load();
        this.EditorColumn = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
              new Wtf.grid.CheckboxSelectionModel({id:"emailtemplatelist_selectionmodel",width:30}),
            {
                header:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.templatename"),//'Template Name',
                dataIndex:'templatename',
                sortable:true,
                xtype:'textfield',
                dbname:'p.name',
                renderer : function(val) {
                    return "<a href = '#' class='campdetails'> "+val+"</a>";
                }
            },{
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//'Subject',
                xtype:'textfield',
                dataIndex:'subject'
            },{
                header:WtfGlobal.getLocaleText("crm.contact.defaultheader.desc"),//'Description',
                xtype:'textfield',
                dataIndex:'description'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon"),//'Created On',
                sortable:true,
                xtype:'datefield',
                dataIndex:'createdon',
                dbname:'p.createdOn',
                renderer:WtfGlobal.onlyDateRendererTZ
            }]);
        this.DefaultEditorColumn = new Wtf.grid.ColumnModel(

            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.templatename"),//'Template Name',
                dataIndex:'templatename',
                sortable:true,
                dbname:'p.name',
                xtype:'textfield',
                renderer : function(val) {
                    return "<a href = '#' class='campdetails'> "+val+"</a>";
                }
            },{
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//'Subject',
                xtype:'textfield',
                dataIndex:'subject'
            },{
                header:WtfGlobal.getLocaleText("crm.contact.defaultheader.desc"),//'Description',
                xtype:'textfield',
                dataIndex:'description'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon"),//'Created On',
                sortable:true,
                xtype:'datefield',
                dataIndex:'createdon',
                dbname:'p.createdOn',
                renderer:WtfGlobal.onlyDateRendererTZ
            }]);

        this.deleteCon= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            handler:this.campDelete
        });

        this.templateButton = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.addtemplate"),//'Add template',
            iconCls:"pwnd newEmailTemplate",
            menu:{
                items:[{
                text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmbasictmp"),//'Start from Basic Template',
                scope : this,
                tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmbasictmp.ttip")},//'Add New E-mail template including rich text formatting.'},
                iconCls:"pwnd newEmailTemplate",
                handler:function(){
                    var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate");//"New Template";
                    var title = Wtf.util.Format.ellipsis(tipTitle,18);
                    var panel="";
                    if(this.id=="emailTemplatedashboard"){
                        panel = Wtf.getCmp('template_dash_win'+this.templateid);
                    } else {
                        panel = Wtf.getCmp('template_wiz_win'+this.templateid);
                    }
                    if(panel==null) {
                        panel=new Wtf.newEmailTemplate({
                            store: this.EditorStore,
                            title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate.ttiptitle")+"'>"+title+"</div>",
                            tipTitle:tipTitle,
                            mailTemplate:this.mailTemplate,
                            dashboardCall:this.id=="emailTemplatedashboard"?true:false
                        });
                        this.mainTab.add(panel);
                    }
                    this.mainTab.setActiveTab(panel);
                    this.mainTab.doLayout();
                    }
            },{
                text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmscratch"),//'Start from scratch',
                scope : this,
                tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmscratch.ttip")},//'Add New E-mail template without formatting.'},
                iconCls:"pwnd newEmailTemplate",
                handler:function(){
                    var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate");//"New Template";
                    var title = Wtf.util.Format.ellipsis(tipTitle,18);
                    var panel="";
                    if(this.id=="emailTemplatedashboard"){
                        panel = Wtf.getCmp('template_dash_win_def'+this.templateid);
                    } else {
                        panel = Wtf.getCmp('template_wiz_win_def'+this.templateid);
                    }
                    if(panel==null) {
                        panel=new Wtf.newEmailTemplate({
                            store: this.EditorStore,
                            title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate.ttiptitle")+"'>"+title+"</div>",
                            tipTitle:tipTitle,
                            mailTemplate:this.mailTemplate,
                            dashboardCall:this.id=="emailTemplatedashboard"?true:false,
                            templateType:'default'
                        });
                        this.mainTab.add(panel);
                    }
                    this.mainTab.setActiveTab(panel);
                    this.mainTab.doLayout();
                }
                    },{
                        text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmdefaulttemplate"),//'Start from Default Templates',
                        scope : this,
                        iconCls:"pwnd newEmailTemplate",
                        menu:{
                            items:[{
                                    text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmdefaulttemplate.leftcol"),//'Left Column',
                                    scope : this,
                                    tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmdefaulttemplate.leftcol.ttip")},//'Add New E-mail template with Left Column formatting.'},
                                    iconCls:"pwnddeftemplate leftTemplate",
                                    handler:function() {
                                        var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate");//"New Template";
                                        var title = Wtf.util.Format.ellipsis(tipTitle,18);
                                        var panel="";
                                        if(this.id=="emailTemplatedashboard"){
                                            panel = Wtf.getCmp('template_dash_win_leftcolumn'+this.templateid);
                                        } else {
                                            panel = Wtf.getCmp('template_wiz_win_leftcolumn'+this.templateid);
                                        }
                                        if(panel==null) {
                                            panel=new Wtf.newEmailTemplate({
                                                store: this.EditorStore,
                                                title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate.ttiptitle")+"'>"+title+"</div>",
                                                tipTitle:tipTitle,
                                                mailTemplate:this.mailTemplate,
                                                dashboardCall:this.id=="emailTemplatedashboard"?true:false,
                                                templateType:'systemplate',
                                                systemplate:3   //Left Column
                                            });
                                            this.mainTab.add(panel);
                                        }
                                        this.mainTab.setActiveTab(panel);
                                        this.mainTab.doLayout();
                                    }
                            },{
                                    text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmdefaulttemplate.rightcol"),//'Right Column',
                                    scope : this,
                                    tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmdefaulttemplate.rightcol.ttip")},//'Add New E-mail template with Right Column formatting.'},
                                    iconCls:"pwnddeftemplate rightTemplate",
                                    handler:function() {
                                        var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate");//"New Template";
                                        var title = Wtf.util.Format.ellipsis(tipTitle,18);
                                        var panel="";
                                        if(this.id=="emailTemplatedashboard"){
                                            panel = Wtf.getCmp('template_dash_win_rightcolumn'+this.templateid);
                                        } else {
                                            panel = Wtf.getCmp('template_wiz_win_rightcolumn'+this.templateid);
                                        }
                                        if(panel==null) {
                                            panel=new Wtf.newEmailTemplate({
                                                store: this.EditorStore,
                                                title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate.ttiptitle")+"'>"+title+"</div>",
                                                tipTitle:tipTitle,
                                                mailTemplate:this.mailTemplate,
                                                dashboardCall:this.id=="emailTemplatedashboard"?true:false,
                                                templateType:'systemplate',
                                                systemplate:2   //Right Column
                                            });
                                            this.mainTab.add(panel);
                                        }
                                        this.mainTab.setActiveTab(panel);
                                        this.mainTab.doLayout();
                                    }
                            },{
                                    text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmdefaulttemplate.postcard"),//'Post Card',
                                    scope : this,
                                    tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmdefaulttemplate.postcard.ttip")},//'Add New E-mail template with Post Card formatting.'},
                                    iconCls:"pwnddeftemplate postTemplate",
                                    handler:function() {
                                        var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate");//"New Template";
                                        var title = Wtf.util.Format.ellipsis(tipTitle,18);
                                        var panel="";
                                        if(this.id=="emailTemplatedashboard"){
                                            panel = Wtf.getCmp('template_dash_win_postcard'+this.templateid);
                                        } else {
                                            panel = Wtf.getCmp('template_wiz_win_postcard'+this.templateid);
                                        }
                                        if(panel==null) {
                                            panel=new Wtf.newEmailTemplate({
                                                store: this.EditorStore,
                                                title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate.ttiptitle")+"'>"+title+"</div>",
                                                tipTitle:tipTitle,
                                                mailTemplate:this.mailTemplate,
                                                dashboardCall:this.id=="emailTemplatedashboard"?true:false,
                                                templateType:'systemplate',
                                                systemplate:6   //Right Column
                                            });
                                            this.mainTab.add(panel);
                                        }
                                        this.mainTab.setActiveTab(panel);
                                        this.mainTab.doLayout();
                                    }
                            },{
                                    text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmdefaulttemplate.richtext"),//'Rich Text',
                                    scope : this,
                                    tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.templateconf.startfrmdefaulttemplate.richtext.ttip")},//'Add New E-mail template with Rich Text formatting.'},
                                    iconCls:"pwnddeftemplate richTemplate",
                                    handler:function() {
                                        var tipTitle=WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate");//"New Template";
                                        var title = Wtf.util.Format.ellipsis(tipTitle,18);
                                        var panel="";
                                        if(this.id=="emailTemplatedashboard"){
                                            panel = Wtf.getCmp('template_dash_win_richtext'+this.templateid);
                                        } else {
                                            panel = Wtf.getCmp('template_wiz_win_richtext'+this.templateid);
                                        }
                                        if(panel==null) {
                                            panel=new Wtf.newEmailTemplate({
                                                store: this.EditorStore,
                                                title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate.ttiptitle")+"'>"+title+"</div>",
                                                tipTitle:tipTitle,
                                                mailTemplate:this.mailTemplate,
                                                dashboardCall:this.id=="emailTemplatedashboard"?true:false,
                                                templateType:'systemplate',
                                                systemplate:5   //Right Column
                                            });
                                            this.mainTab.add(panel);
                                        }
                                        this.mainTab.setActiveTab(panel);
                                        this.mainTab.doLayout();
                                    }
                            }]
                        }
                    }]
                }
        });

        this.defaultSelectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.Grid = new Wtf.KwlGridPanel({
            layout:'fit',
            region:"center",
            store: this.EditorStore,
            cm: this.EditorColumn,
            sm : this.defaultSelectionModel,
            displayInfo: true,
            border : false,
            height:200,
            loadMask : true,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.mtytxt"))//"No email template created till now")
            },
            searchEmptyText:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.quicksearch.mtytxt"),//"Search by Template Name or Description",
            serverSideSearch : true,
            searchField:"templatename",
            tbar:['-',this.templateButton,{
                text:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.deltemplatebtn"),//'Delete Template',
                scope : this,
                tooltip:{text:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.deltemplatebtn.ttip")},//'Select an e-mail template to delete.'},
                iconCls:"pwnd deleteEmailTemplate",
                handler:function(){
                    Wtf.deleteGlobal(this.Grid,this.EditorStore,'Email Template(s)',"templateid","templateid",'emailMarketing',55,56,57);
                }
            }]
        });
         this.DefaultGrid = new Wtf.KwlGridPanel({
            layout:'fit',
            iconCls:"pwndnewCRM emailtemplatePanelicon",
            title:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.defaulttemBTN"),//"Default Templates",
            store: this.DefaultEditorStore,
            cm: this.DefaultEditorColumn,
            sm : this.selectionModel,
            region:"south",
            displayInfo: true,
            border : false,
            height:250,
            loadMask : true,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.mtytxt"))//"No email template created till now")
            },
            searchEmptyText:WtfGlobal.getLocaleText("crm.campaigndetails.campaigntemplate.quicksearch.mtytxt"),//"Search by Template Name, Description",
            serverSideSearch : true,
            searchField:"templatename"
        });
    //    this.add(this.Grid);
        this.templatePanel= new Wtf.Panel({
                title:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.emailTempBTN"),//"Email Templates",
                iconCls:this.id=="emailTemplatedashboard"?"pwndnewCRM emailtemplateTabicon":"pwndnewCRM emailtemplatePanelicon",
                border:false,
                id:this.id+'emailtemplatepan',
                layout:'fit',
                items:[{
                    layout:'border',
                    border:false,
                    items:[this.Grid,this.DefaultGrid]
                }
                ]
         });
        if(this.id=="emailTemplatedashboard"){

            this.mainTab=new Wtf.TabPanel({
               id:this.id+"emailtemplatetabPanel",
               scope:this,
               border:false,
               resizeTabs: true,
               minTabWidth: 155,
               enableTabScroll: true,
               items:[this.templatePanel]
            });
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.templatePanel);
         } else{
              this.add(this.templatePanel);
         }
            this.doLayout();

        this.Grid.on("cellclick",this.gridCellClick,this);
        this.DefaultGrid.on("cellclick",this.gridCellClick,this);
    },

    gridCellClick:function(Grid,rowIndex,columnIndex, e){  //kapil2
        var event = e ;
        if(event.getTarget("a[class='campdetails']")) {
            var recdata = Grid.getSelectionModel().getSelected().data;
            var panel = Wtf.getCmp('template_wiz_win'+recdata.templateid);
            var tipTitle=recdata.templatename+" : Edit Template";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
                if(panel==null) {
                    panel=new Wtf.newEmailTemplate({
                        templateid : recdata.templateid,
                        tname : recdata.templatename,
                        tdesc : recdata.description,
                        templateClass :recdata.templateclass,
                        tsubject : recdata.subject,
                        tbody : recdata.bodyhtml,
                        store: this.EditorStore,
                        title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.campaigndetails.newtemplate.ttiptitle")+"'>"+title+"</div>",
                        tipTitle:tipTitle
                    });
                    this.mainTab.add(panel);
                }
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();

        }
        }
});

Wtf.targetListDetails = function (config){
    Wtf.targetListDetails.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.targetListDetails,Wtf.Panel,{
    closable:true,
    layout : 'fit',
    border:false,
    iconCls:"targetlistIcon",
    initComponent: function(config) {
        Wtf.targetListDetails.superclass.initComponent.call(this,config);

        var Rec = new Wtf.data.Record.create([
            {name:'listid'},
            {name:'listname'},
            {name:'description'},
            {name:'targetsrc'},
            {name:'creator'},
            {name:'updatedon',type:'date',dateFormat:'time'},
            {name:'createdon',type:'date',dateFormat:'time'}
        ]);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },Rec);

        this.EditorStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getTargetList.do',
            remoteSort:Wtf.ServerSideSort,
            baseParams:{
                flag:4
            },
            method:'post',
            reader:EditorReader
        });
        this.EditorStore.load();
        this.EditorColumn = new Wtf.grid.ColumnModel(
            [new Wtf.grid.RowNumberer(),
             new Wtf.grid.CheckboxSelectionModel(),
            {
                header:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.targetlistBTN"),//'Target List',
                dataIndex:'listname',
                dbname:'t.name',
                sortable:true,
                xtype:'textfield',
                renderer : function(val) {
                    if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.manage)){
                        return "<a href = '#' class='campTargetList'> "+val+"</a>";
                    } else{
                        return val;
                    }
                }
            },{
                header:WtfGlobal.getLocaleText("crm.targetlists.header.targetsource"),//'Target Source',
                dataIndex:'targetsrc',
                xtype:'combo',
                renderer : function(val) {
                    return "<div wtf:qtip=\""+val+"\"wtf:qtitle="+WtfGlobal.getLocaleText("crm.targetlists.header.targetsource")+">"+(val|| "&nbsp;")+"</div>";
                }
            },{
                header:WtfGlobal.getLocaleText("crm.account.defaultheader.desc"),//'Description',
                xtype:'textfield',
                dataIndex:'description',
                renderer : function(val) {
                    return "<div wtf:qtip=\""+val+"\"wtf:qtitle="+WtfGlobal.getLocaleText("crm.account.defaultheader.desc")+">"+(val|| "&nbsp;")+"</div>";
                }
            },{
                header:WtfGlobal.getLocaleText("crm.targetlists.header.creator"),//'Creator',
                xtype:'textfield',
                dataIndex:'creator'
            },{
                header:WtfGlobal.getLocaleText("crm.campaigndetails.header.createdon"),//'Created On',
                sortable:true,
                dataIndex:'createdon',
                dbname:'t.createdOn',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            },{
                header:WtfGlobal.getLocaleText("crm.targetlists.header.updatedon"),//'Updated On',
                sortable:true,
                dataIndex:'updatedon',
                dbname:'t.modifiedOn',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
        }]);
       var dashboardCall=false;
       if(this.id=="campaigntargetdetail"){
           dashboardCall=true;
       }
       this.newTargetsBtn= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.targetlists.toptoolbar.newBTN"),//"New",
            tooltip:{text:WtfGlobal.getLocaleText("crm.targetlists.toptoolbar.newBTN.ttip")},//'Add New Target List.'},
            scope:this,
            iconCls:"pwnd newTargetListEmailMarketing",
            handler:function(){addNewTargetListTab(this.EditorStore,this.comboStore)}
        });

        this.deleteTargetsBtn = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
            tooltip:{text:WtfGlobal.getLocaleText("crm.targetlists.toptoolbar.deletebtn.ttip")},//'Delete the selected Target Lists.'},
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            handler:function() {
                    Wtf.deleteGlobal(this.Grid,this.EditorStore,'Target List(s)',"listid","listid",'TargetList',64,65,66);
                 }
        });
        this.tlisttoolbarItems = [];
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.manage)){
            this.tlisttoolbarItems.push('-');
            this.tlisttoolbarItems.push(this.newTargetsBtn);

        }

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.del)){
            this.tlisttoolbarItems.push('-');
            this.tlisttoolbarItems.push(this.deleteTargetsBtn);

        }

        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.Grid = new Wtf.KwlGridPanel({
            store: this.EditorStore,
            cm: this.EditorColumn,
            sm : this.selectionModel,
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.targetLists.mtygrid.watermark")//'Looks like you have not added any Target List. Create a new list by clicking \'New\' button.'
            },
            tbar : this.tlisttoolbarItems,
            searchEmptyText:WtfGlobal.getLocaleText("crm.targetlist.search.mtytxt"),//"Search by Target List",
            serverSideSearch : true,
            displayInfo : true,
            searchField:"listname"
        });

        this.targetList= new Wtf.Panel({
            layout:'fit',
            border:false,
            height:400,
            items:this.Grid
        })

       if(this.id=="campaigntargetdetail"){
            this.targetPanel= new Wtf.Panel({
                title:WtfGlobal.getLocaleText({key:"crm.tab.title",params:[WtfGlobal.getLocaleText("crm.goals.header.target")]}),//"Target Lists",
                iconCls:"targetlistIcon",
                border:false,
                layout:'fit',
                id:this.id+'targetpan',
                items:[{
                    layout:'fit',
                    border:false,
                    items:[this.targetList]
                }
                ]
            });
            this.mainTab=new Wtf.TabPanel({
               id:this.id+"targetTabPanel",
               scope:this,
               border:false,
               resizeTabs: true,
               minTabWidth: 155,
               enableTabScroll: true,
               items:[this.targetPanel]
            });
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.targetPanel);
        } else {
            this.add(this.targetList);
        }
        this.doLayout();
        this.Grid.on("cellclick",this.targetGridCellClick,this);
    },
    targetGridCellClick:function(Grid,rowIndex,columnIndex, e){
        var event = e ;
        if(event.getTarget("a[class='campTargetList']")) {

            var mode=1;//for Edit
            var record = this.Grid.getSelectionModel().getSelected();
            var listID = record.get('listid');
            var listName = record.get('listname')+" ";
            var targetsource = record.get('targetsrc')+" ";

            var tlId = 'targetListTabnewedit_dash'+mode+listID;

            var targetListTab = Wtf.getCmp(tlId );
            if(targetListTab == null) {
                targetListTab = new Wtf.targetListWin({
                    mode : mode,
                    id : tlId,
                    listID : listID,
                    TLID : listID,
                    store:this.EditorStore,
                    listname : Encoder.htmlDecode(listName),
                    targetsource:Encoder.htmlDecode(targetsource),
                    description: Encoder.htmlDecode(record.get("description")),
                    iconCls: "pwnd editTargetListEmailMarketingWin",
                    mainTab:this.mainTab
                })
                this.mainTab.add(targetListTab);
            }
            this.mainTab.setActiveTab(targetListTab);
            this.mainTab.doLayout();

        }
    },
    targetsHandler : function(mode,dashboardCall) {
        var record;
        var listID = '';
        var listName ="New ";
        if(mode == 1) {
             record = this.Grid.getSelectionModel().getSelected();
             listID = record.get('listid');
           //  listName = record.get('listname')+" ";
        }
        var tlId = 'targetListTabnewedit'+mode+listID;
        if(dashboardCall){
            tlId = 'targetListTabnewedit_dash'+mode+listID;
        }
        var targetListTab = Wtf.getCmp(tlId );
        if(targetListTab == null) {
            targetListTab = new Wtf.targetListWin({
                mode : mode,
                id : tlId,
                listID : listID,
                TLID : listID,
                store:this.EditorStore,
                listname : record.get('listname'),
                description : record.get('description'),
                iconCls: (mode==0?"pwnd newTargetListEmailMarketingWin":"pwnd editTargetListEmailMarketingWin"),
                mainTab:this.mainTab
            })
            this.mainTab.add(targetListTab);
        }
        this.mainTab.setActiveTab(targetListTab);
        this.mainTab.doLayout();
    }
});

Wtf.targetListWin = function (config){
	var title = config.listname + WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.targetlistBTN");//"Target List";
	config.title="<div wtf:qtip='"+title+"'>"+Wtf.util.Format.ellipsis(title,18)+"</div>";	
    Wtf.apply(this, config);
    Wtf.targetListWin.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.targetListWin,Wtf.ClosablePanel,{
    iconCls: "pwnd favwinIcon",
    closable:true,
    isClosable:true,
    layout:'fit',
    onRender: function(config){
        this.toolItems = [];
/* save button */
           var saveButton = new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
                tooltip:{text:WtfGlobal.getLocaleText("crm.targetlists.newtarget.saveBTN.ttip")},//'Save the target list.'},
                scope:this,
                iconCls:"pwnd saveBtn",
                handler:this.saveTargetList_Targets
            });
            this.toolItems.push(saveButton);
/* import menu button*/
        this.importArray = [];
        var addNewTargets = new Wtf.Action({
            text: 'Open Targets',
            scope: this,
            flag:0,
            listid : this.listID,
            handler:this.addNewTargetHandler
        });
        var importLeads = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("crm.targetlists.importtargets.leads.ttip")+'>'+WtfGlobal.getLocaleText("crm.importzoho.logs.header.leads")+'</span>',
            scope: this,
            flag:1,
            iconCls:"pwndCRM leadSearch",
            listid : this.listID,
            handler:this.importHandler
        });
        var importContacts = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("crm.targetlists.importtargets.contacts.ttip")+'>'+WtfGlobal.getLocaleText("crm.editor.contactsBTN")+'</span>',
            scope: this,
            flag:2,
            iconCls:"pwndCRM contactsTabIconSearch",
            listid : this.listID,
            handler:this.importHandler
        });
        var importUsers = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("crm.targetlists.importtargets.users.ttip")+'>'+WtfGlobal.getLocaleText("crm.targetlists.importtargets.users")+'</span>',
            scope: this,
            flag:3,
            iconCls:"pwndCRM author",
            listid : this.listID,
            handler:this.importHandler
        });
        var importNewTargets = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("crm.targetlists.importtargets.targetlist.ttip")+'>'+WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.targetlistBTN")+'</span>',
            scope: this,
            flag:4,
            iconCls:"pwndCRM targetlistButtonicon",
            listid : this.listID,
            handler:this.importHandler
        });

//        toolItems.push(addNewTargets);

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.importt)) {
            this.importArray.push(importLeads);
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.importt)) {
            this.importArray.push(importContacts);
        }
        this.importArray.push(importUsers);
        this.importArray.push(importNewTargets);
      /*  this.importTargetListA =Wtf.importMenuArray(this,"Target List",this.campTargetStore,"undefined",this.pP.combo.value);
        for(var i=0;i<importArray.length;i++){
            this.importTargetListA.push(importArray[i]);
        }
        this.importTargetListA.push(importArray)
        var importEmails = new Wtf.Toolbar.Button({
            tooltip: {text: " Import e-mail addresses from leads, contacts or targets easily."},
            scope: this,
            text:"Import",
            iconCls:"pwnd importicon",
            menu: this.importTargetListA
        });
        this.toolItems.push(importEmails);*/
/*end import buttons*/

 var createNewArray = [];

        var createNewLeads = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("crm.targetlists.creatarr.addlead.ttip")+'>'+WtfGlobal.getLocaleText("crm.importzoho.logs.header.leads")+'</span>',
            scope: this,
            iconCls:"pwndCRM leadSearch",
            handler:function(){this.createNewTarget(1)}
        });

        var createNewContacts = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("crm.targetlists.creatarr.addcontact.ttip")+'>'+WtfGlobal.getLocaleText("crm.editor.contactsBTN")+'</span>',
            scope: this,
            iconCls:"pwndCRM contactsTabIconSearch",
            handler:function(){this.createNewTarget(2)}
        });
        var createNewTargets = new Wtf.Action({
            text: '<span wtf:qtip='+WtfGlobal.getLocaleText("crm.targetlists.creatarr.addtarget.ttip")+'>'+WtfGlobal.getLocaleText("crm.targetlists.creatarr.addtarget")+'</span>',            scope: this,
			scope: this,            
			iconCls:"pwndCRM targetlistButtonicon",
            handler:function(){this.createNewTarget(4)}
        });

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage)) {
            createNewArray.push(createNewLeads);
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.manage)) {
            createNewArray.push(createNewContacts);
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.manage)) {
            createNewArray.push(createNewTargets);
        }
        var createNewEmails = new Wtf.Toolbar.Button({
            tooltip: {text: WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN.ttip")},//" Add Leads, Contacts or Targets easily."},
            scope: this,
            text:WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//"Add",
            iconCls:"pwnd addIcon",
            menu: createNewArray
        });
        this.toolItems.push(createNewEmails);
        this.printprv = printButtonR(this,"Target list",20);
        this.toolItems.push(this.printprv);
        this.exportBtn = exportButton(this,"Target list",50);
        this.toolItems.push(this.exportBtn);
        Wtf.targetListWin.superclass.onRender.call(this,config);
        this.activityform=new Wtf.form.FormPanel({
                autoScroll:true,
                border:false,
                height:120,
                items :{
                    layout: 'column',
                    border: false,
                    defaults: {border: false},
                    items: [{
                        columnWidth: 1,
                        items: [{
                            layout: 'form',
                            border:false,
                            defaultType: 'striptextfield',
                            labelWidth:150,
                            defaults: {
                                width: 250
                            },
                            items: [
                                this.name = new Wtf.ux.TextField({
                                    fieldLabel: WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText")+'*',//'Name* ',
                                    allowBlank : false,
                                    maxLength:255,
                                    value : this.listname
                                }),
                                this.targetsource = new Wtf.ux.TextArea({
                                    fieldLabel: WtfGlobal.getLocaleText("crm.targetlists.header.targetsource"),//'Target Source ',
                                    maxLength:255,
                                    value : this.targetsource
                                }),
                                this.desc = new Wtf.ux.TextArea({
                                    fieldLabel: WtfGlobal.getLocaleText("crm.account.defaultheader.desc"),//'Description ',
                                    maxLength:1024,
                                    value : this.description
                                })
                            ]
                        }]
                    }]
                }
        });

        this.targetRecord = new Wtf.data.Record.create([
                {name:'id'},
                {name:'name'},
                {name:'emailid'},
                {name:'fname'},
                {name:'relatedto'},
                {name:'relatedid'},
                {name:'company'},
                {name:'targetscount'},
                {name:'targetlistDescription'}
        ]);

        this.targetReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.targetRecord);

        this.campTargetStore = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getTargetListTargets.do',
            baseParams:{
                listID:this.TLID,
                flag:7
            },
            method:'post',
            reader:this.targetReader
        });
        this.targetColumn = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//'Name',
                dataIndex:'name',
                xtype:'textfield',
                pdfwidth:60
            },{
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.email"),//'Email',
                dataIndex:'emailid',
                xtype:'textfield',
                pdfwidth:60
            },{
                header:WtfGlobal.getLocaleText("crm.masterconfig.leadtypefieldset.Company"),//'Company',
                dataIndex:'company',
                xtype:'textfield',
                pdfwidth:60
            },{
                header:WtfGlobal.getLocaleText("crm.targetlists.importtargets.header.importedfrom"),//'Imported From/ Added as',
                dataIndex:'relatedto',
                xtype:'textfield',
                pdfwidth:60,
                title:"related",
                renderer: WtfGlobal.relatedtoIdRenderer
            },{
                header:WtfGlobal.getLocaleText("crm.goals.header.remove"),//'Remove',
                dataIndex:'remove',
                renderer : function(val, cell, row, rowIndex, colIndex, ds) {
                               return "<div class='pwnd deleteButton' > </div>";
                }
            }

            ]);
        this.campTargetStore.load({
            params:{
                start:0,
                limit:50
            }
            });
        this.targetGrid = new Wtf.grid.GridPanel({
            store: this.campTargetStore,
            cm: this.targetColumn,
            tbar: [],
            clicksToEdit:1,
            border : false,
            loadMask : true,
            searchEmptyText:WtfGlobal.getLocaleText("crm.contact.toptoolbar.quiksearch.mtytxt"),//"Search by Name",
            searchField:"name",
            view: new Wtf.ux.grid.BufferView({
                    scrollDelay: false,
                    autoFill: true,
                    forceFit:true,
                    emptyText:WtfGlobal.getLocaleText("crm.targetlists.grid.mtytxt")//'You have not imported any targets. Import targets from \n\
                           // Lead List, Contact List, User List, Target List by clicking on \'Import\' button.\n\
                           // You can also add Leads, Contacts and Targets by clicking on \'Add\' button or by import using a CSV or XLS file.'
            }),
            bbar: new Wtf.PagingSearchToolbar({
                pageSize: 50,
                displayInfo: true,
                store: this.campTargetStore,
                plugins:this.pP = new Wtf.common.pPageSize({
                    id: "pPageSize_" + this.id
                })
            })
        });

        this.targetGrid.on("cellclick", this.deleteTarget, this);
        this.targetGrid.on("render", this.gridAfterRender, this);
        this.mainPanel = new Wtf.Panel({
            layout :'border',
            border : false,
            items :[{
                layout :'fit',
                region : 'north',
                height : 210,
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px 10px 10px 30px;',
                items : this.activityform
            },{
                layout :'fit',
                region : 'center',
                items : this.targetGrid
            }]
        });
        this.add(this.mainPanel);
    },

    gridAfterRender: function(){
        var extraConfig = {
            tlid : this.TLID,
            targetlistPagingLimit: this.pP.combo.value
        };
        var extraParams = "{\"UsersByUserid\":\""+loginid+"\",\"UsersByCreatedbyid\":\""+loginid+"\", \"UsersByUpdatedbyid\":\""+loginid+"\", \"Company\":\""+companyid+"\"}";
        this.importTargetListA =Wtf.importMenuArray(this,"Target",this.campTargetStore,extraParams, extraConfig);
        this.importTargetList = Wtf.importMenuButtonA(this.importTargetListA,this,"Target");

        for(var i=0;i<this.importTargetListA.length;i++){
            this.importArray.push(this.importTargetListA[i]);
        }
          var importEmails = new Wtf.Toolbar.Button({
            tooltip: {text:WtfGlobal.getLocaleText("crm.targetlists.importBTN.ttip")},// " Import e-mail addresses from leads, contacts or targets easily."},
            scope: this,
            text:WtfGlobal.getLocaleText("crm.IMPORTBUTTON"),//"Import",
            iconCls:"pwnd importicon",
            menu: this.importArray
        });
        this.toolItems.push(importEmails);
        this.targetGrid.getTopToolbar().addButton(this.toolItems);
    },

    deleteTarget:function(grid, ri, ci, e) {
        var event = e;
        if(event.target.className == "pwnd deleteButton") {
             this.isClosable = false;

             Wtf.MessageBox.show({
                title: WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//"Confirm",
                msg: WtfGlobal.getLocaleText("crm.targetlists.deletetarget.confirmmsg"),//"Are you sure you want to delete selected target(s)?<br><br><b>Note: This data cannot be retrieved later.",
                buttons: Wtf.MessageBox.OKCANCEL,
                animEl: 'upbtn',
                icon: Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(bt){
                    if(bt=="ok"){
                        Wtf.Ajax.requestEx({
                        url : Wtf.req.springBase+'emailMarketing/action/deleteTargets.do',
                        params:{
                            listid : this.TLID,
                            relatedid : grid.selModel.selections.items[0].data.relatedid
                        }},this,
                        function(res,action){
                            ResponseAlert(52);
                            grid.store.load({params:{start:0, limit:this.pP.combo.value}});
                         },
                        function() {
                            ResponseAlert(53);
                    });
                    }
                }
             });
        }
    },
    importHandler:function(butObj, event){
        var importID = "ImportEmails";
        if(butObj.flag==2 ){
        if(WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.view)){
        	ResponseAlert(105);
        	return;
        }
        }
        if(butObj.flag==1){
        	if(WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.view)){
        	ResponseAlert(106);
        	return;
        	}
        }
        var win = Wtf.getCmp(importID);
        if(win == null){
            win = new Wtf.importTargetWindow({
                id:importID,
                TLID: this.TLID,
                scope:this,
                butObj:butObj
            });
        }
        win.show();
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Target List";
        var fromdate="";
        var todate="";
        var report="targetlist";
        var exportUrl = Wtf.req.springBase+'emailMarketing/action/targetListExport.do';
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,this.targetGrid,undefined,field,dir);
    },

    exportfile: function(type){
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Target_List";
        var fromdate="";
        var todate="";
        var report="targetlist";
        var exportUrl = Wtf.req.springBase+'emailMarketing/action/targetListExport.do';
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,this.targetGrid,undefined,field,dir);
    },
    
    displayName:function(value,gridcell,record,d,e){
        var uname=(record.json.username).trim();
        return uname;
    },
    CreateNewTarget : function() {
        var rec = this.grid.getSelectionModel().getSelected();
        this.addExtTargetfunction(0,rec,1);
    },
    insertIntoGrid:function(res,targetlistPagingLimit){
        var limit = this.pP.combo.value;
        if(targetlistPagingLimit != undefined)
            limit = targetlistPagingLimit;
        if(res.TLID !== undefined)
            this.TLID = res.TLID;
        this.campTargetStore.baseParams.listID = this.TLID;
        this.campTargetStore.load({params:{start:0, limit:limit}});
    },
    addExtTargetfunction:function(action,record,flag){
        var windowHeading = action==0?WtfGlobal.getLocaleText("crm.targetlists.addtarget.caption"):WtfGlobal.getLocaleText("crm.targetlists.edittarget.caption");//Add Target":"Edit Target";
        var windowMsg = action==0?WtfGlobal.getLocaleText("crm.targetlists.addtarget.msg"):WtfGlobal.getLocaleText("crm.targetlists.edittarget.msg");//"Enter new Target details":"Edit existing Target details";
        this.addExtTargetWindow = new Wtf.Window({
            title : action==0?WtfGlobal.getLocaleText("crm.targetlists.addtarget.caption"):WtfGlobal.getLocaleText("crm.targetlists.edittarget.caption"),//"Add Target":"Edit Target",
            closable : true,
            modal : true,
            iconCls : 'pwnd favwinIcon',
            width : 430,
            height: 370,
            resizable :false,
            buttons :[{
                text : action==0?WtfGlobal.getLocaleText("crm.ADDTEXT"):WtfGlobal.getLocaleText("crm.EDITTEXT"),
                id: "createUserButton",
                scope : this,
                handler:function(){
                    if(this.createuserForm.form.isValid()){
                        Wtf.Ajax.requestEx({
                            url : Wtf.req.springBase+"Target/action/newTargetAddress.do",
                            params: ({
                                type:"newTargetAddress",
                                userid:Wtf.getCmp('tempContIdField').getValue(),
                                username:Wtf.getCmp('tempNameField').getValue(),
                                emailid:Wtf.getCmp('tempEmailField').getValue(),
                                address: Wtf.getCmp('tempAddField').getValue(),
                                contactno:Wtf.getCmp('tempPhoneField').getValue()
                            }),
                            method: 'POST'
                        },
                        this,
                        function(result, req){
                            if(result!=null && result != ""){
                                WtfComMsgBox(453, 0);
                            }
                            this.listds.remove(record);
                            var newrec = new this.targetRecord({
                                id:"",
                                name:Wtf.getCmp('tempNameField').getValue(),
                                emailid:Wtf.getCmp('tempEmailField').getValue(),
                                relatedto:"4",
                                relatedid:""

                            });
                            this.campTargetStore.add(newrec);
                            this.addExtTargetWindow.close();
                         },function(){
                            this.addExtTargetWindow.close();
                        });
                    }
                }
            },{
                text : WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                id:'cancelCreateUserButton',
                scope : this,
                handler : function(){
                    this.addExtTargetWindow.close();
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                id: "userwinnorth",
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html :  getTopHtml(windowHeading,windowMsg)
            },{
                region : 'center',
                border : false,
                id : 'userwincenter',
                bodyStyle : 'background:#f1f1f1;font-size:10px;',
                layout : 'fit',
                items :[this.createuserForm = new Wtf.form.FormPanel({
                    border : false,
                    labelWidth: 120,
                    bodyStyle : 'margin-top:20px;margin-left:35px;font-size:10px;',
                    defaults: {
                        width: 200
                    },
                    defaultType: 'textfield',
                    items: [{
                        fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText")+'*',// 'Name* ',
                        xtype:'textfield',
                        id:'tempNameField',
                        name:'name',
                        validator:WtfGlobal.validateUserName,
                        allowBlank:false
                    },{
                        fieldLabel:WtfGlobal.getLocaleText("crm.EMAILIDFIELD")+'*',// 'Email Id* ',
                        id:'tempEmailField',
                        xtype:'textfield',
                        name: 'emailid',
                        validator: WtfGlobal.validateEmail,
                        allowBlank:false,
                        renderer: WtfGlobal.renderEmailTo
                    },{
                        fieldLabel: WtfGlobal.getLocaleText("crm.lead.defaultheader.phone")+ '*',//'Phone* ',
                        allowBlank:false,
                        id: "tempPhoneField",
                        xtype:'textfield',
                        name: 'phone'
                    },{
                        xtype:"textarea",
                        fieldLabel: WtfGlobal.getLocaleText("crm.lead.defaultheader.address"),//'Address ',
                        id: "tempAddField",
                        name: 'address'
                    },{
                        xtype:"hidden",
                        id: "tempContIdField",
                        name: 'id'
                    }]
                })]
            }]
        });
        Wtf.getCmp('tempPhoneField').on("change", function(){
            Wtf.getCmp('tempPhoneField').setValue(WtfGlobal.HTMLStripper(Wtf.getCmp('tempPhoneField').getValue()));
        }, this);
        Wtf.getCmp('tempAddField').on("change", function(){
            Wtf.getCmp('tempAddField').setValue(WtfGlobal.HTMLStripper(Wtf.getCmp('tempAddField').getValue()));
        }, this);
        this.addExtTargetWindow.show();
        if(record!=null){
            var rdata = record.json;
            Wtf.getCmp('tempNameField').setValue(rdata.username);
            Wtf.getCmp('tempEmailField').setValue(rdata.emailid);
            Wtf.getCmp('tempPhoneField').setValue(rdata.contactno);
            Wtf.getCmp('tempAddField').setValue(rdata.address);
            Wtf.getCmp('tempContIdField').setValue(rdata.userid);
        }
    },
    // TODO - create new targets
    addNewTargetHandler:function(){
        addTargetModuleTab();
    },

    saveTargetList_Targets : function() {
        var targetdata = '';
        if(this.campTargetStore.getCount()<1){
           ResponseAlert(73);
           return ;
        }
        if(this.name.getValue().trim()==""){
            ResponseAlert(63);
            return ;
        }
        if(this.activityform.form.isValid()==false){
        	WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.lead.webtoleadform.entervalinputmsg")]);
        	return;
        }
        Wtf.Ajax.requestEx({
            url: Wtf.req.springBase+'emailMarketing/action/saveTargetListTargets.do',
            params:{
                targets : targetdata,
                listid : this.TLID,
                mode : this.mode,
                name : this.name.getValue(),
                desc : WtfGlobal.HTMLStripper(this.desc.getValue()),
                targetsource : WtfGlobal.HTMLStripper(this.targetsource.getValue())
            }},this,
            function(){
               this.isClosable = true;
               this.fireEvent("close");
               //var targetListPanel=Wtf.getCmp(this.id);
               this.ownerCt.remove(this);
               if(this.addNewDashboardCall){
                   //mainPanel.remove(targetListPanel);
                   mainPanel.doLayout();
               } else {
                   //this.mainTab.remove(targetListPanel);
                   this.mainTab.doLayout();
               }
               if(this.store)this.store.load();

               WtfComMsgBox([WtfGlobal.getLocaleText("crm.goals.header.target"),WtfGlobal.getLocaleText("crm.targetlists.savetargetlistmsg")],0);
          },
            function() {
               WtfComMsgBox([WtfGlobal.getLocaleText("crm.goals.header.target"),WtfGlobal.getLocaleText("crm.targetlists.failedtosavetargetlistmsg")],1);
        })

        },
    createNewTarget:function(flag){

        this.relatedToMod ="";
        var subTitle ="";
        var title="";
        var imgPath="";
        if(flag==1){
            this.relatedToMod ="1";
            title=WtfGlobal.getLocaleText("crm.targetlists.creatarr.addlead.ttip");//"Add a New Lead";
            subTitle=WtfGlobal.getLocaleText("crm.targetlists.addLeadwin.subtitle");//"Provide required information to add Lead.";
            imgPath="../../images/leads.gif";
        } else if(flag==2){
            this.relatedToMod ="2";
            title=WtfGlobal.getLocaleText("crm.targetlists.creatarr.addcontact.ttip");//"Add a New Contact";
            subTitle=WtfGlobal.getLocaleText("crm.targetlists.addContactwin.subtitle");//"Provide required information to add Contact.";
            imgPath="../../images/contacts3.gif";
        } else if(flag==4){
            this.relatedToMod ="4";
            title=WtfGlobal.getLocaleText("crm.targetlists.creatarr.addtarget.ttip");//"Add a New Target";
            subTitle=WtfGlobal.getLocaleText("crm.targetlists.addTargettwin.subtitle");//"Provide required information to add Target.";
            imgPath="../../images/createuser.png";
        }

        if(flag==2){
            this.form1=new Wtf.form.FormPanel({
                border:false,
                autoScroll:false,
                items:[
                name=new Wtf.ux.TextField({
                    fieldLabel: WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//"Name",
                    id:'target_name'+this.id,
                    allowBlank:false,
                    maxLength:255,
                    msgTarget: (Wtf.isIE)?"under":"side",
                    width:200
                }),{
                    fieldLabel:WtfGlobal.getLocaleText("crm.EMAILIDFIELD"),//'Email Id',
                    id:'target_email_id'+this.id,
                    width:200,
                    maxLength:100,
                    allowBlank:false,
                    msgTarget: (Wtf.isIE)?"under":"side",
                    regex:Wtf.ValidateMailPatt,
                    xtype:'striptextfield'
                }]
            });
        } else if(flag==1){
            this.form1=new Wtf.form.FormPanel({
                border:false,
                autoScroll:false,
                items:[
               name = new Wtf.ux.TextField({
                    fieldLabel: WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//"Name",
                    id:'target_name'+this.id,
                    allowBlank:false,
                    maxLength:255,
                    xtype:'textfield',
                    width:200
                }),{
                    fieldLabel:WtfGlobal.getLocaleText("crm.EMAILIDFIELD"),//'Email Id',
                    id:'target_email_id'+this.id,
                    width:200,
                    allowBlank:false,
                    maxLength:100,
                    regex:Wtf.ValidateMailPatt,
                    xtype:'striptextfield'
                },{
                   fieldLabel:WtfGlobal.getLocaleText("crm.campaign.defaultheader.type"),// 'Type',
                   selectOnFocus:true,
                   forceSelection:true,
                   triggerAction:'all',
                   mode:'local',
                   valueField:'id',
                   displayField:'name',
                   typeAhead : true,
                   store:Wtf.LeadTypeStore,
                   width:200,
                   id:'leadType'+this.id,
                   xtype:'combo',
                   allowBlank:false
              }]
            });
        } else {
            this.form1=new Wtf.form.FormPanel({
                border:false,
                autoScroll:true,
                items:[
                name=new Wtf.ux.TextField({
                    fieldLabel: WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//"Name",
                    id:'target_name'+this.id,
                    allowBlank:false,
                    xtype:'textfield',
                    maxLength:255,
 					width:200
                }),{
                    fieldLabel:WtfGlobal.getLocaleText("crm.EMAILIDFIELD"),//'Email Id',
                    id:'target_email_id'+this.id,
                    width:200,
                    allowBlank:false,
                    maxLength:100,
 					regex:Wtf.ValidateMailPatt,
                    xtype:'striptextfield'
                },
                company=new Wtf.ux.TextField({
                    fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.leadtypefieldset.Company"),// "Company",
                    id:'target_company'+this.id,
                    allowBlank:false,
                    maxLength:255,
                    width:200
                })]
            });
        }
       this.impWin1 = Wtf.getCmp('create_new_target_window');
       if(!this.impWin1){
       this.impWin1 = new Wtf.Window({
            resizable: false,
            scope: this,
            layout: 'border',
            modal:true,
            width: 380,
            height: flag==2?250:280,
            iconCls: 'pwnd favwinIcon',
            id: 'create_new_target_window',
            title: WtfGlobal.getLocaleText("crm.targetlists.addtargetwin.title"),//'New Target',
            items: [
            {
                region:'north',
                height:80,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(""+title+"", ""+subTitle+"",""+imgPath+"")
            },{
                region:'center',
                layout:'fit',
                border:false,
                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                items:this.form1
            }
            ],
            buttons: [{
                text: WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                type: 'submit',
                scope: this,
                handler: function(){
                    if(this.form1.form.isValid()){
                        var targetName=Wtf.getCmp('target_name'+this.id).getValue();
                        var targetEmail=Wtf.getCmp('target_email_id'+this.id).getValue();
                        var company="";
                        if(flag==2){
                            if(targetEmail.trim()==""|| targetName.trim()==""){
                                ResponseAlert(152)
                                return;
                            }
                            this.saveContact(targetName,targetEmail);
                        } else if(flag==1){
                            var type =Wtf.getCmp('leadType'+this.id).getValue();
                            if(targetEmail.trim()==""|| targetName.trim()==""||type.trim()==""){
                                ResponseAlert(152)
                                return;
                            }
                            this.saveLead(targetName,targetEmail,company);

                        } else{
                            company =Wtf.getCmp('target_company'+this.id).getValue();
                            if(targetEmail.trim()==""|| targetName.trim()==""||company.trim()==""){
                                ResponseAlert(152)
                                return;
                            }
                            this.saveTarget(targetName,targetEmail,company);

                        }
                        if(this.mode==0)
                                    this.isClosable=false
                                else
                                    this.isClosable=true
                        this.impWin1.close();
                    }else{
                         WtfComMsgBox(21,1);
                    }
                }
            },{
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                scope:this,
                handler:function() {
                    this.impWin1.close();
                }
            }]
        });
        this.impWin1.show();
        }
    },saveLead:function(name,email,comp){
        var type=Wtf.getCmp('leadType'+this.id).getValue();
        var splitName = name.split(" ");
        var lname="";
        var fname="";
        if(splitName.length==1){
            lname=name;
        }else{
            fname = splitName[0];
            for(var i=1;i<splitName.length;i++)
                lname+=splitName[i]+" ";
        }
        var leadid='0';
        var leadownerid=loginid;
        var jsondata = {};
        var validFlag=1;
        var title="";
        var leadstatusid="";
        var phone="";
        var ratingid="";
        var leadsourceid="";
        var industryid="";
        var street="";

        jsondata={
        			leadid:leadid,
        			leadownerid:leadownerid,
        			lastname:lname,
        			firstname:fname,
        			validflag:validFlag,
        			title:title,
        			phone:phone,
        			leadstatusid:leadstatusid,
        			email:email,
        			street:street,
        			ratingid:ratingid,
        			industryid:industryid,
        			leadsourceid:leadsourceid,
        			activities:'',
        			productid:'',
        			price:'',
        			revenue:'',
        			type:type,
        			addstreet:'',
        			moredetails:'',
        			company:comp
        };
        var finalstr=Wtf.encode(jsondata);
        this.saveRecordReq(finalstr,{flag:20,auditEntry:1},1, Wtf.req.springBase+'Lead/action/saveLeads.do');



    },saveContact:function(name,email) {
        var splitName = name.split(" ");
        var lname="";
        var fname="";
        if(splitName.length==1){
            lname=name;
        }else{
            fname = splitName[0];
            for(var i=1;i<splitName.length;i++)
                lname+=splitName[i]+" ";
        }
        var contactid='0';
        var contactownerid=loginid;
        var jsondata = "";
        var validFlag=1;

        jsondata={
        		contactid:contactid,
        		contactownerid:contactownerid,
        		firstname:fname,
        		lastname:lname.trim(),
        		accountid:'',
        		phone:'',
        		mobile:'',
        		email:email,
        		industryid:'',
        		leadsourceid:'',
        		title:'',
        		street:'',
        		createdon:new Date().getTime(),
        		validflag:validFlag,
        		activities:'',
        		description:''
    	   };
        var finalStr = Wtf.encode(jsondata);
        this.saveRecordReq(finalStr,{flag:22,auditEntry:1},2, Wtf.req.springBase+'Contact/action/saveContacts.do');

    },saveTarget:function(name,email,company){
        var splitName = name.split(" ");
        var lname="";
        var fname="";
        if(splitName.length==1){
            lname=name;
        }else{
            fname = splitName[0];
            for(var i=1;i<splitName.length;i++)
            lname+=splitName[i];
        }
        var targetid='0';
        var targetownerid=loginid;
        var jsondata = "";
        var validFlag=1;

        jsondata={
        		targetModuleid:targetid ,
        		targetModuleownerid:targetownerid,
        		firstname:fname,
        		lastname:lname.trim(),
        		company:company,
        		auditstr:"",
        		phone:"",
        		mobile:"",
        		email:email,
        		address:"",
        		validflag:validFlag,
        		description:""
       };
       var finalStr = Wtf.encode(jsondata);
       this.saveRecordReq(finalStr,{flag:301,auditEntry:1},3, Wtf.req.springBase+"Target/action/saveTargets.do");
    },
    saveRecordReq : function (jsondata,paramObj,actionCode, url) {
        Wtf.commonWaitMsgBox("Saving data...");
        paramObj['jsondata'] = jsondata;
        paramObj['type'] = 1;
        paramObj['TLID'] = this.TLID;
        var recID="";
        Wtf.Ajax.requestEx({
            url:url,
            params:paramObj
        },this,
        function(res) {
            if(res.TLID)
                this.TLID = res.TLID;
            Wtf.updateProgress();
            var jdata ="[{\"rid\":\""+res.ID+"\"}]";
            var rto = actionCode;
            switch(actionCode) {
                case 3 : rto = 4;break;
            }
        Wtf.Ajax.requestEx({
            url : Wtf.req.springBase+'emailMarketing/action/saveModuleTargetsForTemplate.do',
            params:{
                data : jdata,
                listid : this.TLID,
                    relatedto : rto
            }},this,
            function(res,action){
            	if(res.success&&res.success==true){
                	if(res.TLID)
                    	this.TLID = res.TLID;
	                Wtf.updateProgress();
    	            this.campTargetStore.baseParams.listID = this.TLID;
        	        this.campTargetStore.load({params:{start:0, limit:this.pP.combo.value}});
            	 } else{
             		WtfComMsgBox(26,2);
             	 }
          	},
            function() {
        });
        },
        function(res){
            WtfComMsgBox(152,1);
        })
    }
});

Wtf.importTargetWindow = function (config){
    config.title = WtfGlobal.getLocaleText({key:"crm.targetlist.importwin.tophtml",params:[config.butObj.text]});//"Import "+config.butObj.text;
    Wtf.apply(this, config);
    Wtf.importTargetWindow.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.importTargetWindow,Wtf.Window,{
    iconCls : "pwnd favwinIcon",
    layout : 'fit',
    modal:true,
    resizable:false,
    height : 550,
    width : 750,
    onRender: function(config){
        Wtf.importTargetWindow.superclass.onRender.call(this,config);
        var storeUrl = "";
        switch(this.butObj.flag) {
            case 1: storeUrl = Wtf.req.springBase + "Lead/action/getLeadsToEmail.do";
                break;
            case 2: storeUrl = Wtf.req.springBase+"Contact/action/getContactToEmail.do";
                break;
            case 3: storeUrl = "Common/ProfileHandler/getUserToEmail.do";
                break;
            case 4: storeUrl = Wtf.req.springBase+"emailMarketing/action/targetListForImport.do";
                break;
        }
        this.importTargetStore = new Wtf.data.Store({
            url: storeUrl,
            baseParams:{
				importID:this.butObj.flag,
                tlid : this.TLID
            },
            method:'post',
            reader:this.scope.targetReader
        });
        var checkBoxSM =  new Wtf.grid.CheckboxSelectionModel({
            singleSelect: this.butObj.flag=="4"?true:false
        });
       this.importTargetStore.on("load",function(){

                var columns = [];
                columns.push(new Wtf.grid.CheckboxSelectionModel());
                columns.push(new Wtf.grid.RowNumberer());
                Wtf.each(this.importTargetStore.reader.jsonData.columns, function(column){
                    if(column.renderer)
                        column.renderer = eval('('+ column.renderer +')');
                    columns.push(column);
                })
                this.importGrid.getColumnModel().setConfig(columns);
 
            Wtf.updateProgress();
        },this);
       
        this.quickPanelSearch  = new Wtf.KWLTagSearch({
            width: 150,
            emptyText: WtfGlobal.getLocaleText("crm.mtytext.searchbyname"),//'Search by Name ',
            Store:this.importTargetStore
        });
        this.pg = new Wtf.PagingSearchToolbar({
            pageSize: 15,
            searchField: this.quickPanelSearch,
            store: this.importTargetStore,
            displayInfo: true,
            emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//No results to display
            plugins: this.pP = new Wtf.common.pPageSize()
        });

        this.importGrid = new Wtf.grid.GridPanel({
            store: this.importTargetStore,
            columns:[],
            scope:this,
            enableColumnHide: false,
            sm : checkBoxSM,
            border : false,
            loadMask : true,
            /*viewConfig: {
                forceFit:true
            },*/
            tbar:['-',this.quickPanelSearch,'-'],
            bbar:this.pg
        });
        var task = new Wtf.util.DelayedTask(function() {
        this.importTargetStore.load({
            params:{
                start:0,
                limit:this.importGrid.getBottomToolbar().pageSize
            }
        });
        }, this);
        task.delay(50);
        this.importTargetStore.on('load', function(store) {
            this.quickPanelSearch.StorageChanged(store);
        }, this);
        this.importTargetStore.on('datachanged', function() {
            var p = this.pP.combo.value;
            this.quickPanelSearch.setPage(p);
        }, this);

        this.mainPanel = new Wtf.Panel({
            layout :'border',
            border : false,
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:this.addEmailsToGrid
            },{
                text: WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),//'Close',
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
                html: getTopHtml(WtfGlobal.getLocaleText({key:"crm.targetlist.importwin.tophtml",params:[this.butObj.text]}),WtfGlobal.getLocaleText({key:"crm.targetlist.importwin.tophtmldetail",params:[this.butObj.text]}), "../../images/import.png")
            },{
                layout :'fit',
                region : 'center',
                items : this.importGrid
            }]
        });

        this.add(this.mainPanel);
    },
    addEmailsToGrid:function(){
        if(!this.importGrid.getSelectionModel().hasSelection()) {
            calMsgBoxShow(161,0);
            return;
        }
        if(this.butObj.flag=="4") { // for target list
            var rec = this.importGrid.getSelectionModel().getSelected().data;
            Wtf.Ajax.requestEx({
                    url : Wtf.req.springBase+'emailMarketing/action/importTargetList.do',
                    params:{
                        importtl : rec.relatedid,
                        listid : this.scope.TLID
                    }},this,
                    function(res,action){
                        if(res.TLID)
                            this.scope.TLID = res.TLID;
                        Wtf.updateProgress();
                        if(this.scope.mode==0)
                        this.scope.isClosable=false
                    else
                        this.scope.isClosable=true
                        var targetStore = this.scope.targetGrid.getStore();
                        targetStore.baseParams.listID = this.scope.TLID;
                        targetStore.load({params:{start:0, limit:this.scope.pP.combo.value}});
                        Wtf.MessageBox.show({
                            title: WtfGlobal.getLocaleText("crm.IMPORTBUTTON"),//"Import",
                            msg: WtfGlobal.getLocaleText("crm.targetlists.importtarget.successmsg"),//"Selected Target list imported successfully. Do you want to continue importing more Target list?",
                            buttons: Wtf.MessageBox.YESNO,
                            animEl: 'mb9',
                            scope:this,
                            icon: Wtf.MessageBox.INFO,
                            fn:function(btn,text){
                                if(btn=="yes"){

                                } else {
                                    this.close();
                                }
                            }
                        });
                     },
                    function() {
                });
        } else {
                var jdata = "[";
                var rto = "";
                var sel = this.importGrid.getSelectionModel().getSelections();
                for(var i =0 ; i < sel.length ;i++ ){
                     var ID = sel[i].get("relatedid");
                     jdata+="{\"rid\":\""+ID+"\"},";
                     if(i==0)
                         rto = sel[i].get("relatedto");
                }
                jdata = jdata.substr(0, jdata.length-1);
                jdata += "]";
                Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.common.import.waitmsg"));//"Importing Data...");
                Wtf.Ajax.requestEx({
                    url : Wtf.req.springBase+'emailMarketing/action/saveModuleTargetsForTemplate.do',
                    params:{
                        data : jdata,
                        listid : this.scope.TLID,
                        relatedto : rto
                    }},this,
                    function(res){
                    Wtf.updateProgress();
                    if(res.success&&res.success==true){
                    var recCount=sel.length;
                    if(res.count)
                    	recCount=res.count;
                    if(res.TLID)
                        this.scope.TLID = res.TLID;
                    if(this.scope.mode==0)
                        this.scope.isClosable=false
                    else
                        this.scope.isClosable=true

                    var targetStore = this.scope.targetGrid.getStore();
                    targetStore.baseParams.listID = this.scope.TLID;
                        targetStore.load({params:{start:0, limit:this.scope.pP.combo.value}});
                    Wtf.MessageBox.show({
                        title: WtfGlobal.getLocaleText("crm.IMPORTBUTTON"),//"Import",
                        msg: WtfGlobal.getLocaleText({key:"crm.targetlists.importtargetcount.successmsg",params:[recCount]}),//recCount+" record(s) imported successfully. Do you want to continue importing more records?",
                        buttons: Wtf.MessageBox.YESNO,
                        animEl: 'mb9',
                        scope:this,
                        icon: Wtf.MessageBox.INFO,
                        fn:function(btn,text){
                            if(btn=="yes"){

                            } else {
                                this.close();
                            }
                        }
                    });
                } 
                 else{
                	 	WtfComMsgBox(20,2);
                	}},
                    function() {
                });
            }
    }
});

function editValue(_aEM, cmpDetailId){
    Wtf.getCmp(_aEM).setValues(cmpDetailId);
}
