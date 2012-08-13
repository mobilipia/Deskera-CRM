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
Wtf.campaignEditor = function (config){
    Wtf.campaignEditor.superclass.constructor.call(this,config);
};

Wtf.extend(Wtf.campaignEditor,Wtf.Panel,{
//    emailCampaignID:"b0e71040-b46d-4fc0-bfe3-1fccca96016f",
	getEditor:Wtf.SpreadSheetGrid.prototype.getEditor,
    initComponent: function(config){
        Wtf.campaignEditor.superclass.initComponent.call(this,config);

        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
        this.getStores();

        this.getEditorGrid();

        this.getAdvanceSearchComponent();

        this.getDetailPanel();

        this.campaignpan= new Wtf.Panel({
            layout:'border',
            border:false,
            title:WtfGlobal.getLocaleText({key:"crm.tab.title",params:[WtfGlobal.getLocaleText("crm.CAMPAIGN")]}),//"Campaign List",
            iconCls:getTabIconCls(Wtf.etype.campaign),
            id:this.id+'campaignpan',
            attachDetailTrigger:true,
            listeners:{
    			'afterlayout':function(p){
        			if(p.attachDetailTrigger){
        				p.layout.south.slideOut = p.layout.south.slideOut.createSequence(this.getDetails,this);
        				delete p.attachDetailTrigger;
        			}
        		},
    			scope:this
        	},
            items:[this.objsearchComponent,
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[this.spreadSheet],
                tbar:this.toolbarItems,
                bbar:this.btmbar
            },
            {
                region:'south',
                height:250,
                title:WtfGlobal.getLocaleText("crm.editors.otherdetailregion"),//'Other Details',
                collapsible:true,
                collapsed : true,
                plugins: new Wtf.ux.collapsedPanelTitlePlugin(),
                split : true,
                layout: "fit",
                items:[this.detailPanel],
                listeners:{
            		'expand':this.getDetails,
            		scope:this
            	}
            }
            ]
        });
        this.mainTab=new Wtf.TabPanel({
           id:this.id+"campaignTabPanel",
           scope:this,
           border:false,
           resizeTabs: true,
           minTabWidth: 155,
           enableTabScroll: true,
           items:[this.campaignpan]
        });
        if(this.archivedFlag==1){
            this.add(this.campaignpan);
        }else{
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.campaignpan);
        }
        this.doLayout();
        this.campaignpan.on("activate",function(){
            Wtf.getCmp("CampaignHomePanelcampaignpan").doLayout();
        },this);
        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);

    }, 
    getDetails:function(){
    	var sm = this.EditorGrid.getSelectionModel();
    	if(sm.getCount()!=1)
    		return;
    	var commentlist = getDocsAndCommentList(sm.getSelected(), "campaignid",this.id,undefined,"Campaign",undefined,undefined,undefined,undefined,0);    	
    },
    getEditorGrid : function (){
        
        x=[
        {
            name:'campaignid'
        },

        {
            name:'campaignname'
        },

        {
            name:'campaignowner',
            defValue:_fullName
        },

        {
            name:'campaignownerid',
            defValue:loginid
        },

        {
            name:'campaignstatusid'
        },

        {
            name:'parentcampaign'
        },

        {
            name:'campaigntypeid'
        },

        {
            name:'objective'
        },

        {
            name:'active'
        },

        {
            name:'startdate',
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },

        {
            name:'startdat'
        },
        {
            name:'enddate',
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },
        {
            name:'enddat'
        },

        {
            name:'numsent'
        },

        {
            name:'expectedresponse',
            type:'float'
        },

        {
            name:'expectedrevenue'
        },

        {
            name:'budgetedcost'
        },

        {
            name:'actualcost'
        },

        {
            name:"createdon",
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },
        {
            name:"creatdon",
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },{
            name:"updatedon",
            dateFormat:'time',
            type:'date'
        },
        {
            name:"totalcomment"
        },
        {
            name:"commentcount"
        },
        {
            name:"typemainid"
        },
        {
            name:"cellStyle"
        },
        {
            name:"validflag"
        }
        ];

        this.EditorRec = new Wtf.data.Record.create(x);
        this.loadCount=0;
        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.EditorRec);
        this.updateInfo = {
        		keyField:'campaignid',
	        	auditStr:"Campaign details updated from campaign profile for ",
	        	url:"Campaign/action/updateMassCampaigns.do",
	        	flag:25,
	        	type:"Campaign"
    		};

        this.EditorStore = new Wtf.data.Store({
        	proxy: new Wtf.data.HttpProxy(new Wtf.data.Connection({url: Wtf.req.springBase+"Campaign/action/getCampaigns.do", timeout:90000})), 
//            url: Wtf.req.springBase+"Campaign/action/getCampaigns.do",
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            paramNames:{sort:'field',dir:'direction'},
            baseParams:{
                flag:10,
                isarchive:this.newFlag==3?true:false
            },
            extraSortInfo:{xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false},
            sortInfo:{field:"updatedon",direction:"DESC"},
            method:'post',
            reader:EditorReader
        });
        
        this.EditorColumnArray = [
            {
                header:'',
                width:30,
                dataIndex:'validflag',
                unselectable:true,
                sortable: true,
                renderer: WtfGlobal.renderValidFlagAndComment
            },
            {
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.campaignname") + " *", // "Campaign Name *",
                tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.campaignname"),//'Campaign Name',
                id:'campaignname',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor:new Wtf.form.TextField({
                    xtype : 'textfield',
                    maxLength : 255,
                    regexText:Wtf.MaxLengthText+"255"
                }),
                dbname:'c.campaignname',
                xtype:'textfield',
                dataIndex: 'campaignname',
                renderer : function(val,a,rec) {
                   if(rec.data.typemainid==Wtf.common.campaign_emailMarketID){
                        return "<a href = '#' class='emailmark' wtf:qtip='Click to view/ create campaign configuration.'> "+val+"</a>";
                    } else {
                        return val;
                    }

                }
            },
            {
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.objective"),//"Objective",
                tip :WtfGlobal.getLocaleText("crm.campaign.defaultheader.objective"),//"Objective",
                id:'objective',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor:new Wtf.form.TextField({
                    xtype : 'textfield',
                    maxLength : 1024,
                    regexText:Wtf.MaxLengthText+"1024"
                }),
                dbname:'c.objective',
                xtype:'textfield',
                dataIndex: 'objective',
                renderer : function(val) {
                    return "<div wtf:qtip=\""+val+"\"wtf:qtitle='Objective'>"+(val)+"</div>";
                }
            },
            {
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.owner")+" *",//"Owner *",
                tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.owner"),//'Owner',
                id:'owner',
                title:'owner',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                hidden:true,
                editor : this.getEditor({xtype:"combo", store:Wtf.campaignOwnerStore, useDefault:true}),
                dataIndex: 'campaignownerid',
                dbname:'c.usersByUserid.userID',
                sortable: true,
                cname:'owner',
                xtype:'combo'
            },
            {
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.startdate"),//"Start Date *",
                tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.startdate"),//'Start Date',
                id:'startdate',
                title:'startdate',
                align:'center',
                pdfwidth:60,
                width:Wtf.defaultWidth,
//                sortable: true, // ToDo Fix me 
                editor : new Wtf.form.DateField({
                    xtype:'datefield',
                    offset:Wtf.pref.tzoffset,
                    format:WtfGlobal.getOnlyDateFormat()
                }),
                dataIndex: 'startdate',
                dbname:'c.startingdate',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            },
            {
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.enddate")+" *",//"End Date *",
                tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.enddate"),//'End Date',
                id:'enddate',
                title:'enddate',
                align:'center',
                pdfwidth:60,
                width:Wtf.defaultWidth,
//                sortable: true, // ToDo Fix me
                editor : new Wtf.form.DateField({
                    xtype:'datefield',
                    offset:Wtf.pref.tzoffset,
                    format:WtfGlobal.getOnlyDateFormat()
                }),
                dataIndex: 'enddate',
                dbname:'c.endingdate',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            },
            {
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.type"),//"Type",
                tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.type"),//'Type',
                title:'type',
                id:'type',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.viewStoreType, useDefault:true}),
                dbname:'c.crmCombodataByCampaigntypeid.ID',
                xtype:'combo',
                cname:'Campaign Type',
                sortable: true,
                dataIndex: 'campaigntypeid'
            },
            {
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.status"),//"Status",
                tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.status"),//'Status',
                title:'status',
                id:'status',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.viewStoreStatus, useDefault:true}),
                dbname:'c.crmCombodataByCampaignstatusid.ID',
                sortable: true,
                xtype:'combo',
                cname:'Campaign Status',
                dataIndex: 'campaignstatusid'
            },
            {
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.response"),//"Response (&#37;)",
                tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.response"),//"Response (%)",
                id:'response',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'right',
                sortable: true,
                editor:new Wtf.form.NumberField({
                    xtype : 'numberfield',
                    minValue: 0,
                    maxValue: 100
                }),
                dbname:'ifnull(CONVERT(c.expectedresponse,DECIMAL(64,4)),0)',
                xtype:'numberfield',
                dataIndex: 'expectedresponse',
                renderer:function(val){
                    if(val!="" || val=="0")
                        val += " %";
                    else if(val=="0")
                        val="0 %";
                    return val;
                }
            },{
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.createdon"),//"Campaign Creation Date",
                tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.createdon"),//'Campaign Creation Date',
                id:'createdon',
                title:'createdon',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'center',
                sortable: true,
                editor : new Wtf.form.DateField({
                    xtype:'datefield',
                    offset:Wtf.pref.tzoffset,
                    format:WtfGlobal.getOnlyDateFormat()
                }),
                dataIndex: 'createdon',
                dbname:'c.createdOn',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            },
            {
                header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.updatedon"),//"Campaign Updated Date",
                tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.updatedon"),//'Campaign Updation Date',
                id:'updatedon',
                title:'updatedon',
                hidden:true,
                fixed:true,
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'center',
                sortable: true,
                editor : new Wtf.form.DateField({
                    xtype:'datefield',
                    hidden:true,
                    offset:Wtf.pref.tzoffset,
                    format:WtfGlobal.getOnlyDateFormat()
                }),
                dataIndex: 'updatedon',
                readOnly:true,
                dbname:'c.updatedOn',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            }
            ]; 

        this.tbarArchiveArray = Wtf.archivedMenuArray(this,"Campaign");
        this.tbarArchive = Wtf.archivedMenuButtonA(this.tbarArchiveArray,this,"Campaign");
        this.tbartargetlistArray = Wtf.TargetListMenuArray(this);
        this.toolbarItems = [];
        this.tbSingle = [];
        this.tbMulti = [];
        this.tbDefault = [];
        var tbIndex = 0;

        this.quickSearchTF = new Wtf.KWLTagSearch({            
            width: 220,
            emptyText:WtfGlobal.getLocaleText("crm.emailmarketing.campdetail.quicksearchtxt"),//"Search by Campaign Name,Objective or Type",
            id:'quick2', //In use,do not delete.
            Store:this.EditorStore,
            parentGridObj: this
        });
        this.toolbarItems.push(this.quickSearchTF);
            this.tbSingle.push(tbIndex);
            this.tbMulti.push(tbIndex);
            this.tbDefault.push(tbIndex);
            tbIndex++;
        
        this.deleteCon= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            tooltip:{text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip")},//'Select row(s) to delete.'},
            handler:this.campDelete
        });

        this.tbarShowActivity=new Wtf.Button({
            text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.showActivityBTN"),//'Activities',
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.todo),
            tooltip:{text:WtfGlobal.getLocaleText({key:"crm.editor.toptoolbar.showActivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.CAMPAIGN")]})},//'Select a campaign to add its activity details.'},
            handler:this.showActivity
        });
        
        this.emailMarketing= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaign.toptoolbar.campconfbtn"),//"Campaign Configurations",
            scope:this,
            iconCls:"pwnd EmailMarketing",
            tooltip:{text:WtfGlobal.getLocaleText("crm.campaign.toptoolbar.disabledcampconfbtn.ttip")},//'To enable the campaign configuration button,change your campaign type to "E-mail campaign"'},
            handler:this.emailMarketingHandler
        });
        
        this.recentcampaigns = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.campaign.toptoolbar.viewrecentcamp"),//"View Recent Campaigns ",
            scope:this,
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.campaign.toptoolbar.viewrecentcamp.ttip")//"Click to view recent campaign."
            },
           iconCls:'pwndCRM showgrp',
           handler:this.handleRecentCampaigns
        },this);
        var optbutton=new Array();
        this.btmbar = [Wtf.moduleBootomToolBar(this,this.id+'CRMupdownCompo',undefined,false),optbutton];
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.manage)) {    
        		if(this.newFlag==2) {
            	    this.toolbarItems.push(Wtf.AddNewButton(this));
                	tbIndex++;	
                this.toolbarItems.push(Wtf.EditRecordButton(this));
            		tbIndex++;
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.del)) {
                    this.toolbarItems.push(this.deleteCon);
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
                }

                 this.toolbarItems.push(this.emailMarketing);
                 this.tbSingle.push(tbIndex);
                 tbIndex++;
                 if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.view) || !WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.manage)) {
                    this.toolbarItems.push(this.tbartargetlistArray);
                    tbIndex++;
                 }
                 
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)) {
                    this.toolbarItems.push(this.tbarShowActivity);
                  //  this.tbSingle.push(tbIndex);
                    tbIndex++;
                }

                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.archive)) {
                    this.toolbarItems.push(this.tbarArchive);
                        this.tbDefault.push(tbIndex);
                        tbIndex++;
                }                
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.exportt)) {
                    this.exp = exportButton(this,"Campaign(s)",2);
                    optbutton.push(this.exp);
                    /*this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;*/
                    this.printprv = printButton(this,"Campaign",2);
                    this.printprv.on('mouseover',function() {
                        if(this.deleteCon.disabled==false)
                            this.printprv.menu.items.items[1].setDisabled(false);
                        else
                            this.printprv.menu.items.items[1].setDisabled(true);
                    }, this);
                    optbutton.push(this.printprv);
//                    this.tbSingle.push(tbIndex);
//                    this.tbMulti.push(tbIndex);
//                    this.tbDefault.push(tbIndex);
//                    tbIndex++;

                    this.tbarPrint.on('mouseover',function() {
                    	var s = this.EditorGrid.getSelectionModel().getSelections();
                        this.printprv.menu.items.items[1].setDisabled(s.length<=0);
                    }, this);

                    if(this.clearFlag!=undefined){
                    	 this.tbarExport.on("mouseover", function(){
                         	var s = this.EditorGrid.getSelectionModel().getSelections();
                             this.exp.menu.items.items[1].setDisabled(s.length<=0);
                             this.exp.menu.items.items[3].setDisabled(s.length<=0);
                             this.exp.menu.items.items[5].setDisabled(s.length<=0);
                         },this);
                    }
                }

                this.toolbarItems.push(this.AdvanceSearchBtn = new Wtf.Toolbar.Button({
                    text : WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN"),// "Advanced Search",
                    id:'advanced2',// In use, Do not delete
                    scope : this,
                    tooltip:WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN.ttip"),//'Search for multiple terms in multiple fields.',
                    handler : this.configurAdvancedSearch,
                    iconCls : 'pwnd searchtabpane'
                }));
                    this.tbSingle.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
            }
            if(this.newFlag==3){
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.del)) {
                    this.toolbarItems.push(this.deleteCon);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                this.tbarUnArchive = Wtf.archivedMenuButtonB(this,"campaign");
                this.toolbarItems.push(this.tbarUnArchive);
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
            }
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.manage)) {
            this.toolbarItems.push(this.recentcampaigns);
            tbIndex++;
        }
        if(this.clearFlag!=undefined) {
            this.toolbarItems.push('->');
            var help=getHelpButton(this,2);
            this.toolbarItems.push(help);
        }
        this.spreadSheet = new Wtf.SpreadSheetGrid({
            cmArray:this.EditorColumnArray,
            store:this.EditorStore,
            moduleName : this.customParentModName,
            isEditor:Wtf.isEditable(this.customParentModName,this),
            moduleid:7,
            pagingFlag : true,
            quickSearchTF : this.quickSearchTF,
            parentGridObj : this,
            keyid : 'campaignid',
            id:'CampaignGrid'+this.id,
            archivedParentName:this.archivedParentName,
            updateURL:Wtf.req.springBase+'Campaign/action/saveCampaigns.do',
            allowedNewRecord:!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.manage)
        });

        this.EditorStore.on("beforeload",function(){
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
        },this);
        this.EditorStore.on("loadexception",function(){
            Wtf.updateProgress();
           	if(this.loadCount <= 2){
           		this.EditorStore.reload();
           		this.loadCount++;
           	}
           	else
           		ResponseAlert(903);
        },this);
        
        this.EditorGrid = this.spreadSheet.getGrid();
        this.EditorColumn = this.spreadSheet.getColModel();
        if(this.newFlag==2) {//new
            if(WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.manage)) {
//                this.EditorGrid.on("afteredit",this.fillGridValue,this);
//                this.EditorGrid.on("beforeedit",this.beforeEdit,this);
                this.EditorGrid.isEditor = false;
            } 
            
            this.EditorGrid.on("cellclick",this.gridCellClick,this);
        }
        this.EditorStore.on("load",function(){
        	if(this.loadCount !=undefined && this.loadCount != 0)
        		this.loadCount=0;
        	this.SelChange();
            Wtf.updateProgress();
        },this);
        
        this.EditorGrid.on("validateedit",function(e){
        	if(e.field=="startdate" && e.value > e.record.data.enddate){
               	ResponseAlert(86);
                   return false;
               } else if(e.field=="enddate" && e.record.data.startdate > e.value){
               	ResponseAlert(86);
               	return false;
               }
        },this);
        
        this.gridRowClick();
        cellClick(this);
        this.EditorGrid.getSelectionModel().on("rowselect",this.SelChange,this);
        this.EditorGrid.getSelectionModel().on("rowdeselect",this.SelChange,this);
        this.EditorGrid.on("sortchange",this.sortChange,this);
        this.EditorGrid.on("rowclick",this.gridCellClick1,this);
        this.EditorGrid.on("mouseover",Wtf.hideNotes,this);
        this.EditorGrid.on("afteredit",this.afterEdit,this);
        this.spreadSheet.on('beforeupdate',this.checkRefRequired, this);
        this.tbarArchive.on('click',function() {
            if(this.deleteCon.disabled==false)
                this.tbarArchiveArray[0].setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.archiveBTN.ttip",params:[WtfGlobal.getLocaleText("crm.CAMPAIGN")]}));//"Archive the selected campaign.");
            else
                this.tbarArchiveArray[0].setTooltip(WtfGlobal.getLocaleText("crm.editor.archiveBTN.disabled.ttip"));//"Select row(s) to send in Archive repository.");
        }, this);
        
    },
    
    afterEdit:function(e){
    	if(e.field=="campaigntypeid"){
    		var rec = Wtf.viewStoreType.getAt(Wtf.viewStoreType.find('id',e.value));
    		e.record.data.typemainid = rec.data.mainid;
    	}
    },
    
    gridCellClick1: function(grid, ri, e) {

        if(e.target.className == "clicktoshowcomment") {
            Wtf.onCellClickShowComments(grid.getStore().getAt(ri).data.campaignid, this.customParentModName, ri, e);
            if(this.massUpdate!=undefined){
            	var sel = this.EditorGrid.getSelectionModel().getSelections();
            	if(sel!=undefined){
            		if(sel.length>=1)
            			this.massUpdate.enable();
            		else
            			this.massUpdate.disable();
            	}
            }
        }
        if(e.target.className == "emailmark") {
            var recData = this.EditorGrid.getStore().getAt(ri).data;
            var campId = 'campaigndetail'+recData.campaignid;
            var tipTitle ='Campaigns : '+recData.campaignname;
            var title= Wtf.util.Format.ellipsis(tipTitle,17);
            var campComp = Wtf.getCmp(campId);
            if(campComp==null) {
                campComp=new Wtf.campaignDetails({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Campaign Configuration'>"+title+"</div>",
                    id:campId,
                    campaignid : recData.campaignid,
                    newFlag:3,
                    arcFlag:1,
                    mainTab:this.mainTab,
                    archivedFlag:1,
                    campaignname:recData.campaignname
                });
                this.mainTab.add(campComp);
            }
            this.mainTab.setActiveTab(campComp);
            this.doLayout();
        }
        if(e.target.className == "showMandatoryFields") {
            Wtf.emptyMandatoryFields(grid,ri);
        }
    },
//    beforeEdit :function(e){
//        if(e.record.get('campaignid')=="0" && e.record.get('validflag') != -1){
//            ResponseAlert(200);
//            return false;
//        }
//    },
//    validateEdit:function(e){
//            if(e.field=="startdate" && e.value==""){
//                return false;
//            }
//            if(e.field=="enddate" && e.value==""){
//                return false;
//            }
//            if(e.field=="createdon" && e.value==""){
//                return false;
//            }
//    },
    sortChange:function(grid,sortInfo,sortFlag){
        this.sortInfo = sortInfo;
//        if((this.newFlag != 3 || sortFlag) && this.newRec != undefined ) {
//            Wtf.arrangeGridNumbererRemove(this.EditorGrid.getStore().indexOf(this.newRec), this.EditorGrid);
//            this.EditorGrid.getStore().remove(this.newRec);
//            this.addNewRec();
//            Wtf.arrangeGridNumbererAdd(0,this.EditorGrid)
//        }
    },
    showAdvanceSearch:function(){
        showAdvanceSearch(this,this.searchparam);
    },
    storeLoad:function(){
      
    },
    exportfile: function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Campaign";
        var fromdate="";
        var todate="";
        var report="crm";
        var exportUrl = Wtf.req.springBase+"Campaign/action/exportCampaign.do";
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,undefined,undefined,field,dir);
    },
    exportSelected: function(type) {
        var report="crm";
        var name="Campaign";
        var fromdate="";
        var todate="";
        var selArr = [];
        var exportUrl = Wtf.req.springBase+"Campaign/action/exportCampaign.do";
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            var dir = this.sortInfo.direction;
        }
        selArr = this.EditorGrid.getSelectionModel().getSelections();
        var jsondata = "";
        for(var i=0;i<selArr.length;i++)
        {
            if(selArr[i].get("validflag") != -1 && selArr[i].get("validflag") != 0) {
                jsondata+="{'id':'" + selArr[i].get('campaignid') + "'},";
            }
        }
        if(jsondata.length > 0) {
            var trmLen = jsondata.length - 1;
            var finalStr = jsondata.substr(0,trmLen);
            exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,undefined,finalStr,field,dir);
        } else {
            if(type=='print')
                ResponseAlert(553);
            else
                ResponseAlert(552);
        }
    },
    createTargetlist: function() {
        addNewTargetListTab(this.targetStore);
    },
    showtargetlists: function() {
        var tlId = 'targetlistgridfromcamp';
        var targetComp = Wtf.getCmp(tlId );
        if(targetComp==null) {
            targetComp=new Wtf.targetListDetails({
                title:'Target Lists',
                id:tlId,
                comboStore:this.targetStore,
                mainTab:mainPanel
            });
            mainPanel.add(targetComp);
        }
        mainPanel.setActiveTab(targetComp);
        mainPanel.doLayout();
    },

    Addfiles:function()
    {

        var temp = this.EditorGrid.getSelectionModel().getSelected();
        var s=this.EditorGrid.getSelectionModel().getSelections();
        if(s.length==1)
            this.gridCellClick(temp);

        else
            WtfComMsgBox(158,0);
    },
    showActivity:function()
    {
		this.selectedarray=this.EditorGrid.getSelectionModel().getSelections();
        this.rec=this.EditorGrid.getSelectionModel().getSelected();
        if(this.selectedarray.length==1)
        {
            var id=this.rec.data.campaignid;
            if(id=="0"){
            	WtfComMsgBox(25);
            	return;
            }
			this.addactivityflag=true;
            var campname = this.rec.data.campaignname;
            var titlename = "Campaign";
            if(campname.trim()!=""){
                titlename = campname;
            }
            var tipTitle=titlename+"'s Activity";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var panel=Wtf.getCmp(this.id+'activityCampaignTab'+id);
            var newpanel = true;
            if(panel==null)
            {
                panel= new Wtf.activityEditor({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Campaign'>"+title+"</div>",
                    id:this.id+'activityCampaignTab'+id,
                    layout:'fit',
                    border:false,
                    closable:true,
                    urlFlag:154,
                    scope:this,
                    RelatedRecordName:titlename,
                    modName : "CampaignActivity",
                    customParentModName : "Activity",
                    iconCls:getTabIconCls(Wtf.etype.todo),
                    Rrelatedto:'Campaign',
                    relatedtonameid:id,
                    highLightId:this.activityId,
                    newFlag:2,
                    subTab:true,
                    mainTab:this.mainTab
                });
                this.mainTab.add(panel);
                newpanel = undefined;
            }
            if(this.activityId && panel){
           	   panel.on("render",function(p){
           		   panel.EditorStore.on("load",function(){
           			   panel.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(panel.initialConfig.highLightId,panel.EditorStore,"activityid"));
           		   },panel.EditorStore);
           	   },panel);
             }
            this.mainTab.setActiveTab(panel);
            this.mainTab.doLayout();
        }
        else if (this.fromExternalLink)
        {
            WtfComMsgBox(400);
        }
        else
        {
            WtfComMsgBox(1100,0);
        }
    },
    gridRowClick:function(e,rowIndex) {
        WtfGlobal.enableDisableTbBtnArr(this.toolbarItems, this.EditorGrid, this.tbSingle, this.tbMulti, this.tbDefault);
        WtfGlobal.enableDisableTbBtnArr(this.tbarArchiveArray, this.EditorGrid, [0,1], [0,1], [1]);
        var isEmailCamp = true;
        var count = this.EditorGrid.getSelectionModel().getCount();
        if(count==1){
            var idx="";
            var rec = this.EditorGrid.getSelectionModel().getSelected();
            if(Wtf.viewStoreType.find("id", rec.get('campaigntypeid'))!=-1)
                idx = Wtf.viewStoreType.getAt(Wtf.viewStoreType.find("id", rec.get('campaigntypeid'))).get("mainid");

            if(idx==Wtf.common.campaign_emailMarketID && rec.get('validflag')==1){
                isEmailCamp = false;
            }
        } 
        this.emailMarketing.setDisabled(isEmailCamp);
    },
    
    handleRecentCampaigns:function(){
    	 this.EditorStore.extraSortInfo = {xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false};
         this.EditorStore.sort("updatedon","DESC");
    },

    gridCellClick:function(Grid,rowIndex,columnIndex, e){
        var event = e ;
        if(event.getTarget("img[class='gridnewcomment']")) {
            var gridInfo = new Object;
            var record = this.EditorGrid.getStore().getAt(rowIndex);
            this.selRecordid=record.get('campaignid');
            gridInfo.grid=this.EditorGrid;
            gridInfo.store=this.EditorStore;
            gridInfo.keyid='campaignid';
            gridInfo.mapid=0;
            gridInfo.recid=this.selRecordid;
            gridInfo.id=this.id;
            gridInfo.record=record;
            Wtf.getCmp(this.id+'CRMupdownCompo').addComments(gridInfo);
        }
        if(event.getTarget("img[class='emailmarketingicon']")) {
            var recData = Grid.store.getAt(rowIndex).data;
            var campId = 'campaigndetail'+recData.campaignid;
            var tipTitle ='Campaigns : '+recData.campaignname;
            var title= Wtf.util.Format.ellipsis(tipTitle,17);
            var campComp = Wtf.getCmp(campId);
            if(campComp==null) {
                campComp=new Wtf.campaignDetails({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Campaign Configuration'>"+title+"</div>",
                    id:campId,
                    campaignid : recData.campaignid,
                    newFlag:3,
                    arcFlag:1,
                    campaignname : recData.campaignname,
                    archivedFlag:1
                });
                mainPanel.add(campComp);
            }
            mainPanel.setActiveTab(campComp);
            mainPanel.doLayout();
        }
     },

    SelChange:function(){
        var s = this.EditorGrid.getSelectionModel().getSelections();
        var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        if(s.length == 1 && selectedRec.data.campaignid!="0"){   
            this.tbarShowActivity.enable();
            getDocsAndCommentList(selectedRec, "campaignid",this.id,undefined,"Campaign");
        } else {
            this.tbarShowActivity.disable();
            docCommentDisable(this);
        }
       
        this.gridRowClick();

        if(this.deleteCon.disabled==false) {
            this.deleteCon.setTooltip(WtfGlobal.getLocaleText("crm.campaign.toptoolbar.deletebtn.ttip"));//'Delete the selected campaign(s).');
        } else {
            this.deleteCon.setTooltip(WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip"));//'Select row(s) to delete.');
        }

        if(this.tbarShowActivity.disabled==false && this.newFlag!=3) {
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.addactivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.CAMPAIGN")]}));//'Add activity details for the selected Campaign.');
        } else if(this.newFlag!=3) {
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.toptoolbar.showActivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.CAMPAIGN")]}));//'Select a campaign to add its activity details.');
        }

        if(this.emailMarketing.disabled==false && this.newFlag!=3) {
            this.emailMarketing.setTooltip(WtfGlobal.getLocaleText("crm.campaign.toptoolbar.campconfbtn.ttip"));//'Add Campaign Configurations to the selected Campaign.');
        } else if(this.newFlag!=3) {
            this.emailMarketing.setTooltip(WtfGlobal.getLocaleText("crm.campaign.toptoolbar.nonemailcampconfbtn.ttip"));//'Select a Campaign Type as "Email Campaign" to add Campaign Configurations.');
        }
        if(this.massUpdate!=undefined){
        	if(s.length == 1 && selectedRec.data.campaignid!="0" || s.length>1)
        		this.massUpdate.enable();
        	else
        		this.massUpdate.disable();
        }
    },

    getDetailPanel:function()
    {
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.EditorGrid,
            Store:this.EditorStore,
            modulename:'Campaign',
            keyid:'campaignid',
            height:200,
            mapid:0,
            id2:this.id,
            moduleName:'Campaign',
            detailPanelFlag:(this.archivedFlag==1?true:false)
        });
    },

    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.EditorColumnArray,
            module : Wtf.crmmodule.campaign,
            advSearch:false
        });
    },

    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.objsearchComponent.advSearch = true;
        this.objsearchComponent.cm = this.EditorColumnArray;
        this.objsearchComponent.getComboData();
        this.AdvanceSearchBtn.disable();
        this.doLayout();
    },
    clearStoreFilter:function(){
       
        this.EditorStore.baseParams = {
            flag:10,
            isarchive:this.newFlag==3?true:false
        };
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
        this.searchJson="";
        this.objsearchComponent.hide();
        this.AdvanceSearchBtn.enable();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;

        this.EditorStore.baseParams = {
            flag:10,
            searchJson:this.searchJson,
            isarchive:this.newFlag==3?true:false
        };
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Campaign";
        var fromdate="";
        var todate="";
        var report="crm";
        var exportUrl = Wtf.req.springBase+"Campaign/action/exportCampaign.do";
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,undefined,undefined,field,dir);
    },
    getStores:function(){
        if(!Wtf.StoreMgr.containsKey("campaigntype")){
            Wtf.viewStoreType.load();
            Wtf.StoreMgr.add("campaigntype",Wtf.viewStoreType);
        }

        if(!Wtf.StoreMgr.containsKey("campaignstatus")){
            Wtf.viewStoreStatus.load();
            Wtf.StoreMgr.add("campaignstatus",Wtf.viewStoreStatus);
        }
        chkownerload(Wtf.crmmodule.campaign);

    },
    newGrid:function()
    {
        var newFlag=1;
    },
    checkRefRequired:function(e){
    	if(e.record.data[Wtf.SpreadSheetGrid.VALID_KEY]==0&&e.record.modified[Wtf.SpreadSheetGrid.VALID_KEY]==1)
    		e.url= Wtf.req.springBase+"common/crmCommonHandler/saveCampaigns.do";
    },
//    addNewRec:function (){
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Campaign, Wtf.Perm.Campaign.manage)) {
//            var gridRec={
//                validflag:-1,
//                campaignid:"0",
//                campaignownerid:'',
//                campaignname:"",
//                objective:"",
//                campaigntypeid:"",
//                campaignstatusid:"",
//                createdon:'',
//                startdate:"",
//                enddate:"",
//                expectedresponse:""
//
//            };
//            this.newRec = new this.EditorRec(this.spreadSheet.getEmptyCustomFields(gridRec));
//            this.EditorStore.insert(0, this.newRec);
//        }
//    },
//    fillGridValue:function (e){
//    	this.validSave(e.row,e.record,e.field,e);
//    },
//
//    validSave:function(rowindex,record,field, e){
//        var modifiedRecord=this.EditorGrid.getStore().getModifiedRecords();
//        var flag=false;
//        if(modifiedRecord.length<1){
//        //    WtfComMsgBox(150,0);
//            return false;
//        }
//
//        if(field=="startdate" || field=="enddate") {
//            if(field=="enddate" ){
//                if(record.data.startdate > record.data.enddate) {
//                    record.set('startdate',record.data.enddate);
//                }
//            }
//            if(field=="startdate" ){
//                if(record.data.startdate > record.data.enddate) {
//                    record.set('enddate',record.data.startdate);
//                }
//            }
//        }
//        this.saveData(rowindex,record,field, e);
//
//    },
//
//    saveData:function(rowindex,record,field, e){
//    	var event = e;
//        var rData = record.data;
//        var auditStr=Wtf.SpreadSheet.constructAuditStr(this.EditorGrid,rowindex, e.column, e.value, e.originalValue,record);
//
//        idx = Wtf.viewStoreType.find("id", e.originalvalue);
//        if(idx!=-1 && Wtf.viewStoreType.getAt(idx).get("mainid") == Wtf.common.campaign_emailMarketID)
//        	WtfComMsgBox(1101);
//
//        var jsondata="";
//        var validFlag=1;
//        if(rData.startdate=="")
//        {
//     	   record.set('startdate',new Date());
//        }if(rData.enddate=="")
//        {
//     	   record.set('enddate',new Date());
//        }
//
////        if(rData.campaignname.trim()=="" || rData.startdate=="" || rData.enddate=="" || new Date(rData.startdate)>new Date(rData.enddate)){
////            validFlag=0;
////        }
////        else if(rData.campaignname.trim()=="" || rData.startdate=="" || rData.enddate=="" || new Date(rData.startdate)==new Date(rData.enddate)) {
////            if(rData.starttime > rData.endtime)
////            {
////                validFlag=0;
////            }
////        }
////        else{
////            validFlag=1;
////        }
//        var temp=rData.campaignownerid;
//        if(temp=="")
//        {
//            record.set('campaignownerid',loginid);
//        }
//
//        var columnarray = this.spreadSheet.getGrid().colModel.config;
//        for(var ctr=0;ctr<columnarray.length;ctr++){
//            if(columnarray[ctr].mandatory){
//                  if(rData[columnarray[ctr].dataIndex]==""){
//                      if(columnarray[ctr].id=="response" && rData[columnarray[ctr].dataIndex]=="0" ){
//                          validFlag=1;
//                      }else{
//                        validFlag=0;
//                        break;
//                      }
//                  }
//            }
//
//        }
//
//        var isCampaignNameEdit;
//        if(field=='campaignname'){
//            isCampaignNameEdit = true;
//        }else{
//            isCampaignNameEdit = false;
//        }
//        jsondata+='{"campaignid":"' + rData.campaignid + '",';
//        jsondata+='"campaignownerid":"' + rData.campaignownerid + '",';
//        jsondata+='"campaignname":"' +rData.campaignname + '",';
//        jsondata+='"auditstr":"' +auditStr+ '",';
//        jsondata+='"objective":"' + rData.objective + '",';
//        jsondata+='"campaigntypeid":"' + rData.campaigntypeid + '",';
//        jsondata+='"campaignstatusid":"' + rData.campaignstatusid + '",';
//        jsondata+='"startdate":' +(rData.startdate.getTime?rData.startdate.getTime():new Date().getTime()) + ',';
//        jsondata+='"validflag":"' +validFlag+ '",';
//        jsondata+='"enddate":' + (rData.enddate.getTime?rData.enddate.getTime():new Date().getTime()) + ',';
//        jsondata+='"createdon":"' + (rData.createdon.getTime?rData.createdon.getTime():new Date().getTime())+ '",';
//        jsondata+='"isCampaignNameEdit":"' + isCampaignNameEdit + '",';
//        jsondata+='"dirtyfield":"' + field + '",';
//        jsondata+='"expectedresponse":"' + rData.expectedresponse + '"},';
//
//        var trmLen = jsondata.length - 1;
//        var finalStr = jsondata.substr(0,trmLen);
//        var url;
//        if(record.get('validflag') == 1 && validFlag == 0 ){
//            url="common/crmCommonHandler/saveCampaigns.do";
//        }else{
//            url="Campaign/action/saveCampaigns.do";
//        }
//        if(rData.createdon==""){
//              var dates=new Date();
//                 record.set('createdon',dates);
//        }
//        Wtf.Ajax.requestEx({
//            url: Wtf.req.springBase + url,
//            params:{
//                jsondata:finalStr,
//                type:this.newFlag,
//                flag:30
//            }
//        },
//        this,
//        function(res) {
//        	if(res.revert){
//                // revert if found references in other modules
//                record.set(columnarray[event.column].dataIndex,event.originalValue);
//                WtfComMsgBox(["Alert","Sorry your attempt failed since account is being referenced in <br/><b>"+res.moduleName+"</b>"]);
//            } else{
//            rData.campaignid=res.ID;
//            this.afterValidRecordSaved(res,finalStr,validFlag,rData.validflag);
//            record.set('validflag',validFlag);
//            }
//        },
//        function() {
//            WtfComMsgBox(152,1);
//        });
//    },


    afterValidRecordSaved : function (res,finalStr, newValidFlag, oldValidFlag) {
        if(newValidFlag == 1) {
            bHasChanged = true;
            var obj=Wtf.getCmp(Wtf.moduleWidget.campaign);
            if(obj!=null){
                obj.callRequest("","",0);
                Wtf.refreshUpdatesAll();
            }
        }
        if(res && res.reloadLeadSourceStore){
            Wtf.lsourceStore.reload();
        }
        Wtf.getCmp("tree").savecamp(res,finalStr,1);
    },

    campDelete:function() {
        Wtf.deleteGlobal(this.EditorGrid,this.EditorStore,'Campaign(s)',"campaignid","campaignid",'Campaign',17,18,19,"campaignnode");
    },
    emailMarketingHandler:function() {
         var s=this.EditorGrid.getSelectionModel().getSelections();
         if(s.length==1){
            var idx = Wtf.viewStoreType.getAt(Wtf.viewStoreType.find("id",s[0].get('campaigntypeid') )).get("mainid");
            if(idx==Wtf.common.campaign_emailMarketID){
                
                var recData = this.EditorGrid.getSelectionModel().getSelected().data;
                var campId = 'campaigndetail'+recData.campaignid;
                var tipTitle ='Configuration : '+recData.campaignname;
                var title= Wtf.util.Format.ellipsis(tipTitle,17);
                var campComp = Wtf.getCmp(campId);
                if(campComp==null) {
                    campComp=new Wtf.campaignDetails({
                        title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Campaign Configuration'>"+title+"</div>",
                        id:campId,
                        campaignid : recData.campaignid,
                        newFlag:3,
                        arcFlag:1,
                        mainTab:this.mainTab,
                        archivedFlag:1,
                        campaignname:recData.campaignname
                    });
                    this.mainTab.add(campComp);
                }
                this.mainTab.setActiveTab(campComp);
                this.doLayout();
            }
            else{
                ResponseAlert(70);
            }
        } else {
                ResponseAlert(71);
        }
       
    },
    campTargetHandler:function() {
        var s=this.EditorGrid.getSelectionModel().getSelections();
        if(s.length==1){
            var recData = this.EditorGrid.getSelectionModel().getSelected().data;
            var campId = 'campaigntargetdetail'+recData.campaignid;
            var tipTitle =recData.campaignname+"'s Targets";
            var title= Wtf.util.Format.ellipsis(tipTitle,17);
            var campComp = Wtf.getCmp(campId);
            if(campComp==null) {
                campComp=new Wtf.campaignTargetList({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Campaign Targets'>"+title+"</div>",
                    id:campId,
                    campaignid : recData.campaignid,
                    newFlag:3,
                    arcFlag:1,
                    archivedFlag:1,
                    mainTab:this.mainTab
                });
                this.mainTab.add(campComp);
            }
            this.mainTab.setActiveTab(campComp);
            campComp.on("activate",function(campComp){
                campComp.northGrid.setHeight(200);
                campComp.doLayout();
            },this);
            this.doLayout();
        } else {
            ResponseAlert(71);
        }

    },
    showArchived:function() {
        var panel=Wtf.getCmp('CampaignArchivePanel');
        if(panel==null) {
            panel=new Wtf.campaignEditor({
                border:false,
                title:"<div wtf:qtip=\"Archived Campaigns\"wtf:qtitle='Archived'>Archived Campaigns</div>",
                layout:'fit',
                closable:true,
                id:'CampaignArchivePanel',
                customParentModName : "Campaign",
                modName : "CampaignArchived",
                iconCls:getTabIconCls(Wtf.etype.archived),
                newFlag:3,
                arcFlag:1,
                archivedFlag:1,
                archivedParentName:'Campaign',
                subTab:true,
                submainTab:this.mainTab
            });
            this.mainTab.add(panel);
        }
        this.mainTab.setActiveTab(panel);
        this.mainTab.doLayout();
    },
  
    ArchiveHandler:function(a) {
    	//Check whether the selected campain's tab is opened or not.
		var selectedRec=this.EditorGrid.getSelectionModel().getSelections();
    	var archiveFlag=true;
        var archivedCampMsg="";
        var archivedCampCount =0;
    	for(var i=0;i<selectedRec.length;i++){
    		var emailmarketingTabid='campaigndetail' +selectedRec[i].get("campaignid");
    		var opencampaigntab=Wtf.getCmp(emailmarketingTabid);
    		if(opencampaigntab!=null) {
    			archiveFlag=false;
    			archivedCampMsg+=" "+selectedRec[i].get("campaignname")+",";
                archivedCampCount++;
    		}
    	}
    	if(archiveFlag){
            var data={a:a,tbarArchive:this.tbarArchive,EditorGrid:this.EditorGrid,title:'Campaign',keyid:'id',valueid:"campaignid",treeid:"campaignnode",table:'Campaign',GridId:'CampaignGrid',homePanId:'CampaignHomePanel',archivedPanId:'CampaignArchivePanel',name:"name", valueName:"campaignname", ownerName : "campaignownerid"};
            var mod = "CrmCampaign";
            var audit = "25";
            var auditMod = "Campaign";
            Wtf.ArchivedGlobal(data, mod, audit,auditMod);
    	}else{

            archivedCampMsg = archivedCampMsg.substr(0,(archivedCampMsg.length-1));
            var finalMsg = "<b>There is "+archivedCampCount+" campaign which is already opened. Please close its tabs or de-select it from archived list.</b><br><br>"+archivedCampMsg;
            if(archivedCampCount>1){
                finalMsg = "<b>There are "+archivedCampCount+" campaigns which are already opened. Please close their tabs or de-select them from archived list.</b><br><br>"+archivedCampMsg;
            }
            WtfComMsgBox(["Alert",finalMsg]);
        }
    },
    temp: function(){
        Wtf.highLightGlobal(this.highLightId,this.EditorGrid,this.EditorStore,"campaignid");
   }

});
