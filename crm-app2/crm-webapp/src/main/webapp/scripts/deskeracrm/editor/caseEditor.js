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
Wtf.caseEditor = function (config){
    Wtf.caseEditor.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.caseEditor,Wtf.Panel,{
	getEditor:Wtf.SpreadSheetGrid.prototype.getEditor,
    initComponent: function(config){
        Wtf.caseEditor.superclass.initComponent.call(this,config);

        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
    },
    onRender: function(config){
        this.getStores();

        this.getEditorGrid();

        this.getAdvanceSearchComponent();

        this.getDetailPanel();

        this.casepan= new Wtf.Panel({
            layout:'border',
            title:WtfGlobal.getLocaleText({key:"crm.tab.title",params:[WtfGlobal.getLocaleText("crm.CASE")]}),//"Case List",
            iconCls:getTabIconCls(Wtf.etype.cases),
            border:false,
            id:this.id+'casepan',
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
            items:[
            this.objsearchComponent
            ,
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
                layout:'fit',
                items:[this.detailPanel],
                listeners:{
            		'expand':this.getDetails,
            		scope:this
            	}
            }
            ]
        });
        this.mainTab=new Wtf.TabPanel({
           id:this.id+"caseTabPanel",
           scope:this,
           border:false,
           resizeTabs: true,
           minTabWidth: 155,
           enableTabScroll: true,
           items:[this.casepan]
        });

        if(this.archivedFlag==1){
            this.add(this.casepan);
        } else if(this.subTab==true){
            this.add(this.casepan);
        }else{
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.casepan);
        }
        this.casepan.on("activate",function(){
            Wtf.getCmp("CaseHomePanelcasepan").doLayout()
        },this)
        Wtf.caseEditor.superclass.onRender.call(this,config);

        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);
    },
    getDetails:function(){
    	var sm = this.EditorGrid.getSelectionModel();
    	if(sm.getCount()!=1)
    		return;
    	var commentlist = getDocsAndCommentList(sm.getSelected(), "caseid",this.id,undefined,'Case',undefined,undefined,undefined,undefined,0);    	
    },
    getEditorGrid:function (){
        x=[
        {
            name:'caseid'
        },
        {
            name:'caseowner',
            defValue:_fullName
        },
        {
            name:'caseownerid',
            defValue:loginid
        },
        {
            name:'casename'
        },
        {
        	name:'caseassignedtoid'
        },
        {
            name:'caseassignedto'
        },
        {
        	name:'assignedto'
        },
        {
            name:'caseoriginid'
        },
        
        {
            name:'casetypeid'
        },
        {
            name:'casetype'
        },
        
        {
            name:'subject'
        },
        
        {
            name:'reportedbyid'
        },
        {
            name:"createdon",
            dateFormat:"time",
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },
        {
            name:"creatdon",
            dateFormat:"time",
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },
        {
            name:'updatedon',
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },
        {
            name:'accountnameid',
            newValue:(this.addFlag==0?"":this.accid)
        },{
            name:'accountname',
            newValue:(this.addFlag==0?"":this.RelatedRecordName)
        },
        
        {
            name:'contactnameid'
        },
        {
            name:'contactname'
        },
        {
            name:'productnameid'
        },
        
        {
            name:'casereasonid'
        },
        
        {
            name:'casestatusid'
        },
        {
            name:'casestatus'
        },
        
        {
            name:'casepriorityid'
        },
        
        {
            name:'casepriority'
        },{
            name:'resolution'
        },
        
        {
            name:'phone'
        },
        
        {
            name:'email'
        },
        
        {
            name:'description'
        },
        {
            name:'internalcomments'
        },
        
        {
            name:'fileuploadid'
        },
        {
            name:'activities'
        },
        {
            name:"totalcomment"
        },
        {
            name:"comment"
        },
        {
            name:"commentcount"
        },
        {
            name:"cellStyle"
        },
        {
            name:'validflag'
        },
        {
            name:'casecreatedby'
        },
        {
            name:'createdbyflag'
        }
                
        ];
        
        this.EditorRec = new Wtf.data.Record.create(x);
        this.loadCount=0;
        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.EditorRec);
        this.updateInfo = {
            	keyField:'caseid',
            	auditStr:"Case details updated from case profile for ",
            	url:"Case/action/updateMassCases.do",
            	flag:33,
            	type:"Case"
    		};
        this.EditorStore = new Wtf.data.Store({
        	proxy: new Wtf.data.HttpProxy(new Wtf.data.Connection({url: Wtf.req.springBase+"Case/action/getCases.do", timeout:90000})), 
//            url: Wtf.req.springBase+"Case/action/getCases.do",
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            paramNames:{sort:'field',dir:'direction'},
            baseParams:{
                flag:this.urlFlag,
                mapid:this.accid,
                isarchive:this.newFlag==3?true:false
            },
            extraSortInfo:{xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false},
            sortInfo:{field:"updatedon",direction:"DESC"},
            method:'post',
            reader:EditorReader
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
        
        this.EditorColumnArray = [
            {
                header:'',
                width:30,
                id:'validflag',
                dataIndex:'validflag',
                unselectable:true,
                dbname:'c.validflag',
                sortable: true,
                renderer:WtfGlobal.renderValidFlagAndComment
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//"Subject",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//'Subject',
                id:'subject',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable:true,
                editor:new Wtf.form.TextField({
                    xtype : 'textfield',
                    maxLength : 1024,
                    regexText:Wtf.MaxLengthText+"1024"
                }),
                dataIndex:'subject',
                dbname:'c.subject',
                xtype:'textfield',
                renderer : function(val) {
            	var tmp = Wtf.util.Format.htmlEncode(val);
                    return "<div wtf:qtip=\""+tmp+"\"wtf:qtitle='Subject'>"+val+"</div>";
                }
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.desc"),//"Description",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.desc"),//'Descritpion',
                id:'description',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor:new Wtf.form.TextField({
                    xtype : 'textfield',
                    maxLength : 1024,
                    regexText:Wtf.MaxLengthText+"1024"
                }),
                dataIndex: 'description',
                dbname:'c.description',
                xtype:'textfield',
                renderer : function(val) {
            		var tmp = Wtf.util.Format.htmlEncode(val);
                    return "<div wtf:qtip=\""+tmp+"\"wtf:qtitle='Description'>"+val+"</div>";
                }
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.casename"),//"Case Name ",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.casename"),//'Case Name',
                id:'casename',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                hidden:true,
                editor:new Wtf.form.TextField({
                    xtype : 'textfield',
                    maxLength : 255,
                    regexText:Wtf.MaxLengthText+"255"
                }),
                dbname:'c.casename',
                xtype:'textfield',
                dataIndex:'casename'
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.caseowner"),//"Owner",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.caseowner"),//'Owner',
                id:'owner',
                title:'owner',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                hidden:true,
                editor : this.getEditor({xtype:"combo", store:Wtf.caseOwnerStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'caseowner', loadOnSelect : true}),
                dataIndex: 'caseowner',
                dbname:'c.usersByUserid.userID',
                sortable: true,
                cname:'owner',
                xtype:'combo'
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.status"),//"Status",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.status"),//'Status',
                id:'status',
                title:'status',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.caseStatusStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'casestatus', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.casestatus}),
                dataIndex: 'casestatus',
                dbname:'c.crmCombodataByCasestatusid.ID',
                sortable: true,
                xtype:'combo',
                cname:'Case Status'
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.assignedto"),//"Assigned To",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.assignedto"),//'Assigned To',
                id:'assignedto',
                title:'assignedto',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.caseAssignedUserStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'caseassignedto', loadOnSelect : true}),
                dataIndex: 'caseassignedto',
                dbname:'c.assignedto.userID',
                sortable: true,
                cname:'assignedto',
                xtype:'combo'
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.priority"),//"Priority",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.priority"),//'Priority',
                id:'priority',
                title:'priority',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.cpriorityStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'casepriority', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.casepriority}),
                dataIndex: 'casepriority',
                dbname:'c.crmCombodataByCasepriorityid.ID',
                sortable: true,
                xtype:'combo',
                cname:'Priority'
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.type"),//"Type",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.type"),//'Type',
                id:'type',
                title:'type',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.caseoriginStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'casetype', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.caseorigin}),
                dataIndex: 'casetype',
                dbname:'c.crmCombodataByCasetypeid.ID',
                sortable: true,
                xtype:'combo',
                cname:'Case Type'
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.accountname"),//"Account Name ",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.accountname"),//'Account Name',
                id:'accountname',
                title:'accountname',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.parentaccountstoreSearch, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'accountname'}),
                dataIndex: 'accountname',
                dbname:'c.crmAccount.accountid',
                sortable: true,
                cname:'Account',
                xtype:'combo',
                hidden:this.addFlag==0?false:true
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.contactname"),//"Contact Name ",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.contactname"),//'Contact Name',
                id:'contactname',
                title:'contactname',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.contactStoreSearch, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'contactname'}),
                dbname:'c.crmContact.contactid',
                sortable: true,
                cname:'Contact',
                xtype:'combo',
                dataIndex:'contactname'
            },
            {
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.productname"),//"Product Name ",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.productname"),//'Product Name',
                id:'productname',
                title:'exportmultiproduct',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                hidden:true,
                editor : this.getEditor({xtype:"select", store:Wtf.productStore, useDefault:true}),
                dbname:'p.productId.productid',
                cname:'Product',
                xtype:'select',
                dataIndex: 'productnameid'
            },{
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.createdon"),//"Case Creation Date",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.createdon"),//'Case Creation Date',
                id:'createdon',                
                title:'createdon',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'center',
                sortable: true,
                editor :  new Wtf.form.DateField({
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
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.updatedon"),//"Case Updated Date",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.updatedon"),//'Case Updation Date',
                id:'updatedon',
                title:'updatedon',
                hidden:true,
//                fixed:true,
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'center',
                sortable: true,
                /*editor :  new Wtf.form.DateField({
                    xtype:'datefield',
                    hidden:true,
                    offset:Wtf.pref.tzoffset,
                    format:WtfGlobal.getOnlyDateFormat()
                }),*/
                dataIndex: 'updatedon',
                readOnly:true,
                dbname:'c.updatedOn',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRenderer
            },{
                header:WtfGlobal.getLocaleText("crm.case.defaultheader.comment"),//"Comment",
                tip:WtfGlobal.getLocaleText("crm.case.defaultheader.comment"),//'Comment',
                id:'comment',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                hidden:true,
                align:'left',
                fixed:true,
                exportOnly:true,
                editor : new Wtf.form.TextField({
                    xtype : "textfield",
                    maxLength:100
                }),
                dataIndex: 'comment',
                xtype:'textfield'
            },
            {
                header:"Case Created By",
                tip:'Case Created By',
                id:'casecreatedby',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'left',
                editable:false,
                editor : new Wtf.form.TextField({
                    xtype : "textfield",
                    maxLength:100
                }),
                dataIndex: 'casecreatedby',
                dbname:'c.casecreatedby',
                xtype:'textfield'
            }
            
            ];

        this.tbarShowActivity=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.showActivityBTN"),//'Activities',
            id:'caseactivity',//In use,do not delete.
            tooltip:{text:WtfGlobal.getLocaleText({key:"crm.editor.toptoolbar.showActivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.CASE")]})},//{text:'Select a case to add its activity details.'},
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.todo),
            handler:this.showActivity
        });
        this.deletecase= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
            tooltip:{text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip")},//'Select row(s) to delete.'},
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            handler:this.caseDelete
        });

        this.AdvanceSearchBtn = new Wtf.Toolbar.Button({
                    text :  WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN"),//"Advanced Search",
                    id:'advanced8',// In use, Do not delete
                    scope : this,
                    tooltip:WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN.ttip"),//'Search for multiple terms in multiple fields.',
                    handler : this.configurAdvancedSearch,
                    iconCls : 'pwnd searchtabpane'
        });
        this.recentcases = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.case.toptoolbar.viewrecentcase"),//"View Recent Cases ",
            scope:this,
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.case.toptoolbar.viewrecentcase.ttip")//"Click to view recent cases."
            },
           iconCls:'pwndCRM showgrp',
           handler:this.handleRecentCases
        },this);
        this.tbarArchiveArray = Wtf.archivedMenuArray(this,"Case");
        this.tbarArchive = Wtf.archivedMenuButtonA(this.tbarArchiveArray,this,"Case");

        this.exp = exportButton(this,"Case(s)",8);
        this.printprv = printButton(this,"Case",8);
        this.toolbarItems = [];
        this.tbSingle = [];
        this.tbMulti = [];
        this.tbDefault = [];
        var tbIndex = 0;

        this.quickSearchTF = new Wtf.KWLTagSearch({
            width: 220,
            emptyText:GlobalQuickSearchEmptyText[Wtf.crmmodule.cases],
            id:'quick8',//In use,do not delete.
            Store:this.EditorStore,
            parentGridObj: this
        });
        this.toolbarItems.push(this.quickSearchTF);
            this.tbSingle.push(tbIndex);
            this.tbMulti.push(tbIndex);
            this.tbDefault.push(tbIndex);
            tbIndex++;

        var optbutton=new Array();
        this.btmbar=[Wtf.moduleBootomToolBar(this,this.id+'CRMupdownCompo',undefined,true),optbutton];
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.manage)) {    
        		if(this.newFlag==2) {
            	    this.toolbarItems.push(Wtf.AddNewButton(this));
                	tbIndex++;
                	this.toolbarItems.push(Wtf.EditRecordButton(this));
                	tbIndex++;
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.del)) {
                    this.toolbarItems.push(this.deletecase);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.archive)) {
                    this.toolbarItems.push(this.tbarArchive);
                        this.tbDefault.push(tbIndex);
                        tbIndex++;
                }

                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)) {
                    this.toolbarItems.push(this.tbarShowActivity);
                     //   this.tbSingle.push(tbIndex);
                        tbIndex++;
                }                
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.exportt)) {
                   optbutton.push(this.exp);
                     /*   this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        this.tbDefault.push(tbIndex);
                        tbIndex++;
*/
                    optbutton.push(this.printprv);
                       /* this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        this.tbDefault.push(tbIndex);
                        tbIndex++;*/
                }
                
                this.toolbarItems.push(this.AdvanceSearchBtn);
                    this.tbSingle.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;

            }
            if(this.newFlag==3){
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.del)) {
                    this.toolbarItems.push(this.deletecase);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                this.tbarUnArchive = Wtf.archivedMenuButtonB(this,"Case");
                this.toolbarItems.push(this.tbarUnArchive);
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
            }
        }
         if(!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.manage)) {
            this.toolbarItems.push(this.recentcases);
            tbIndex++;
        }
        if(this.clearFlag!=undefined || this.subTab!=null){
            this.toolbarItems.push('->');
            var help=getHelpButton(this,8);
            this.toolbarItems.push(help);
        }
        this.spreadSheet = new Wtf.SpreadSheetGrid({
            cmArray:this.EditorColumnArray,
            store:this.EditorStore,
            moduleName : this.customParentModName,
            moduleid:3,
            isEditor:Wtf.isEditable(this.customParentModName,this),
            pagingFlag : true,
            quickSearchTF : this.quickSearchTF,
            parentGridObj : this,
            keyid:'caseid',
            id:'CaseGrid'+this.id,
            archivedParentName : this.archivedParentName,
            updateURL:Wtf.req.springBase+'Case/action/saveCases.do',
            allowedNewRecord:!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.manage),
            listeners: {
                         "reloadexternalgrid": function(){
                              if(!this.searchparam)
                                this.reloadStore.defer(10,this);
                             else
                                this.showAdvanceSearch.defer(10,this);
                         },
                         scope : this
                       },
            scope : this
        });

        this.EditorGrid = this.spreadSheet.getGrid();
        this.EditorColumn = this.spreadSheet.getColModel();
        
       if(this.newFlag==2){//new
            if(WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.manage)) {
//                this.EditorGrid.on("afteredit",this.fillGridValue,this);
//                this.EditorGrid.on("beforeedit",this.beforeEdit,this);
                    this.EditorGrid.isEditor = false;
            } 
        }
        this.EditorStore.on("load",function(){
        	if(this.loadCount !=undefined && this.loadCount != 0)
        		this.loadCount=0;
        	this.SelChange();
            Wtf.updateProgress();
        },this);
//        this.EditorGrid.on("validateedit",this.validateEdit,this);
//        this.spreadSheet.on('beforeupdate',this.checkRefRequired, this);
        this.gridRowClick();
        this.EditorGrid.getSelectionModel().on("rowselect",this.SelChange,this);
        this.EditorGrid.getSelectionModel().on("rowdeselect",this.SelChange,this);
        this.EditorGrid.on("sortchange",this.sortChange,this);
        this.EditorGrid.on("rowclick",this.gridCellClick,this);
        this.EditorGrid.on("mouseover",Wtf.hideNotes,this);
        this.tbarArchive.on('click',function() {
            if(this.deletecase.disabled==false)
                this.tbarArchiveArray[0].setTooltip("Archive the selected case.");
            else
                this.tbarArchiveArray[0].setTooltip("Select row(s) to send in Archive repository.");
        }, this);
        this.tbarPrint.on('mouseover',function() {
        	var s = this.EditorGrid.getSelectionModel().getSelections();
            this.printprv.menu.items.items[1].setDisabled(s.length<=0);
        }, this);
        this.tbarExport.on("mouseover", function(){
        	var s = this.EditorGrid.getSelectionModel().getSelections();
            this.exp.menu.items.items[1].setDisabled(s.length<=0);
            this.exp.menu.items.items[3].setDisabled(s.length<=0);
            this.exp.menu.items.items[5].setDisabled(s.length<=0);
        },this);
        
    },
    showAdvanceSearch:function(){
        showAdvanceSearch(this,this.searchparam);
    },
    reloadStore : function(){
            this.EditorStore=this.spreadSheet.getMySortconfig(this.EditorStore);
             if(this.EditorStore.baseParams && this.EditorStore.baseParams.searchJson){
                this.EditorStore.baseParams.searchJson="";
            }
            this.EditorStore.load({
                params:{
                    start:0,
                    limit:25,
                    mapid:this.mapid
                }
            });
    },
//     validateEdit:function(e){
//
//            if(e.field=="createdon" && e.value==""){
//                return false;
//            }
//    },

    handleRecentCases:function(){
    	 this.EditorStore.extraSortInfo = {xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false};
         this.EditorStore.sort("updatedon","DESC");
    },

    gridCellClick : function(grid, ri, e) {

        if(e.target.className == "clicktoshowcomment") {
            Wtf.onCellClickShowComments(grid.getStore().getAt(ri).data.caseid, this.customParentModName, ri, e);
        }
        if(e.target.className == "showMandatoryFields") {
            Wtf.emptyMandatoryFields(grid,ri);
        }
        this.gridRowClick();
    },
//    beforeEdit :function(e){
//        if(e.record.get('caseid')=="0" && e.record.get('validflag') != -1){
//            ResponseAlert(200);
//            return false;
//        }
//    },

    sortChange:function(grid,sortInfo, sortFlag){
        this.sortInfo = sortInfo;
    },
    storeLoad:function(){
      
    }, 
    getDetailPanel:function()
    {
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.EditorGrid,
            Store:this.EditorStore,
            modulename:'case',
            keyid:'caseid',
            height:200,
            mapid:6,
            id2:this.id,
            moduleName:'Case',
            detailPanelFlag:(this.archivedFlag==1?true:false)
        });
    },
    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.spreadSheet.colArr,
            module : Wtf.crmmodule.cases,
            advSearch:false
        });
    },

    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.objsearchComponent.advSearch = true;
        this.objsearchComponent.cm = this.spreadSheet.colArr;
        this.objsearchComponent.getComboData();
        this.AdvanceSearchBtn.disable();
        this.doLayout();
    },
    clearStoreFilter:function(){
         this.searchJson="";
        this.EditorStore.baseParams = {
            flag:this.urlFlag,
            mapid:this.accid,
            searchJson:this.searchJson,
            isarchive:this.newFlag==3?true:false
        }
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
        this.searchJson="";
        this.objsearchComponent.hide();
        this.AdvanceSearchBtn.enable();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;
        this.EditorStore.baseParams = {
            mapid:this.accid,
            flag:this.urlFlag,
            isarchive:this.newFlag==3?true:false,
            searchJson:this.searchJson
        }
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
    },

    getStores:function(){

        var XReader = new Wtf.data.Record.create([
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'relatedto',
            type: 'string'
        },
        {
            name: 'relatednameid',
            type: 'string'
        },
        {
            name: 'productid',
            type: 'string'
        }
        ]);

       /*this.productStore = new Wtf.data.Store({

            url: Wtf.req.springBase+'Product/action/getProductname.do',
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, XReader),
            autoLoad:false
        });*/

        /*this.parentaccountstore=new Wtf.data.Store({

            url: Wtf.req.springBase+'Case/action/getAllAccounts.do',
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, XReader),
            autoLoad:false
        });*/

        /*Wtf.contactstore=new Wtf.data.Store({

            url: Wtf.req.springBase+'Case/action/getAllContacts.do',
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, XReader),
            autoLoad:false
        });*/

        chkproductStoreload();
        //this.productStore.load();
       //this.parentaccountstore.load();
//        chkparentaccountstoreload();
        //this.contactstore.load();
//        chkcontactstorestoreload();

//        chkownerload(Wtf.crmmodule.cases);
//        chkpriorityload();
//        chkstatusload();
//        if(!Wtf.StoreMgr.containsKey("caseorigin")){
//            Wtf.caseoriginStore.load();
//            Wtf.StoreMgr.add("caseorigin",Wtf.caseoriginStore)
//        }

        //this.contactstore.filter('relatednameid','null');
        //this.productStore.filter('relatednameid','null');
    },
    newGrid:function()
    {
        var newFlag=1;
    },
//    addNewRec:function (){
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.manage)) {
//            var gridRec={
//                validflag:-1,
//                caseid:"0",
//                caseownerid:'',
//                caseassignedtoid:'',
//                caseowner:'',
//                caseassignedto:'',
//                casename:'',
//                contactname:'',
//                contactnameid:'',
//                company:'',
//                createdon:new Date(),
//                productnameid:this.productid==undefined?"":this.productid,
//                casetypeid:'',
//                casestatusid:'',
//                casepriorityid:'',
//                accountnameid:(this.addFlag==0?'':this.accid),
//                casetype:'',
//                casestatus:'',
//                casepriority:'',
//                accountname : (this.addFlag==0?'':this.RelatedRecordName),
//                description:'',
//                subject:''
//            };
//            this.newRec = new this.EditorRec(this.spreadSheet.getEmptyCustomFields(gridRec));
//            this.EditorStore.insert(0, this.newRec);
//        }
//    },
//    fillGridValue:function (e){
//    	this.validSave(e.row,e.record,e.field,e);
//    },

     gridRowClick:function(e,rowIndex) {
        WtfGlobal.enableDisableTbBtnArr(this.toolbarItems, this.EditorGrid, this.tbSingle, this.tbMulti, this.tbDefault);
        WtfGlobal.enableDisableTbBtnArr(this.tbarArchiveArray, this.EditorGrid, [0,1], [0,1], [1]);
        if(this.massUpdate!=undefined){
        	var sel = this.EditorGrid.getSelectionModel().getSelections();
        	var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        	if(sel!=undefined){
        		if(sel.length == 1 && selectedRec.data.caseid!="0" ||sel.length>1)
        			this.massUpdate.enable();
        		else
        			this.massUpdate.disable();
        	}
        }
    },

    SelChange:function(){
        var s = this.EditorGrid.getSelectionModel().getSelections();
        var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        if(s.length == 1 && selectedRec.data.caseid!="0"){
            if(!this.detailPanel.ownerCt.collapsed)
            	 getDocsAndCommentList(selectedRec, "caseid",this.id,undefined,'Case');
            var updownCompE = Wtf.getCmp(this.id+'CRMupdownCompo');
		    var enableContactsButton=true;
		    var isMainOwner = false
            docCommentEnable(this,updownCompE,isMainOwner,enableContactsButton);
		    enableButt(this,updownCompE,isMainOwner,enableContactsButton);
            this.tbarShowActivity.enable();
        } else {
            this.tbarShowActivity.disable();
            docCommentDisable(this);
            disableButt(this);
        }
        if(this.massUpdate!=undefined){
        	if(s.length == 1 && selectedRec.data.caseid!="0" || s.length>1)
        		this.massUpdate.enable();
        	else
        		this.massUpdate.disable();
        }
        this.gridRowClick();
        if(this.deletecase.disabled==false) 
            this.deletecase.setTooltip(WtfGlobal.getLocaleText("crm.case.deleteBTN.ttip"));//'Delete the selected case(s).');
        else 
            this.deletecase.setTooltip(WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip"));//'Select row(s) to delete');
        if(this.tbarShowActivity.disabled==false && this.newFlag!=3)
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.addactivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.CASE")]}));//'Add activity details related to the selected case.');
        else if(this.newFlag!=3)
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.disabledaddactivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.CASE")]}));//'Select a case to add its activity details.');
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var fromdate="";
        var todate="";
        var report="crm";
        var flag = this.urlFlag;
        var exportUrl = Wtf.req.springBase+"Case/action/caseExport.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
       if(this.urlFlag==16) {
            var name="Cases";
			exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,flag,undefined,undefined,field,dir);
       } else {
           name="AccountCase";
			exportWithTemplate(this,type,name,fromdate,todate,report+this.accid,exportUrl,flag,undefined,undefined,field,dir);
       }
    },
//    validSave:function(rowindex,record,field, e){
//
//        var modifiedRecord=this.EditorGrid.getStore().getModifiedRecords();
//        var flag=false;
//        if(modifiedRecord.length<1){
//        //    WtfComMsgBox(250,0);
//            return false;
//        }
//        this.saveData(rowindex,record,field, e);
//    },
//    saveData:function(rowindex,record,field, e){
//        var rData = record.data;
//        var auditStr=Wtf.SpreadSheet.constructAuditStr(this.EditorGrid,rowindex, e.column, e.value, e.originalValue,record);
//        var jsondata = "";
//        var validFlag=1;
//
////        if(rData.caseownerid.trim()==""||rData.subject.trim()==""||rData.casestatusid.trim()==""||rData.casepriorityid.trim()=="")
////        {
////            validFlag=0;
////        }
//
//        var temp=rData.caseownerid;
//        if(temp=="") {
//            record.set('caseownerid',loginid);
//            record.set('caseowner',_fullName);
//        }
//
//        var columnarray = this.spreadSheet.getGrid().colModel.config;
//        for(var ctr=0;ctr<columnarray.length;ctr++){
//            if(columnarray[ctr].mandatory){
//                  if(rData[columnarray[ctr].dataIndex]==" " || rData[columnarray[ctr].dataIndex]=="" || ( columnarray[ctr].xtype=="textfield" && rData[columnarray[ctr].dataIndex].trim()=="")||(columnarray[ctr].xtype=="combo"&&rData[columnarray[ctr].dataIndex]=="99")){
//                      validFlag=0;
//                      break;
//                  }
//            }
//
//        }
//        if(this.urlFlag==64)
//        {
//            rData.accountnameid=this.accid;
//        }
//
//        if(rData.validflag != validFlag) record.set('validflag',validFlag);
//
//        jsondata+='{"caseid":"' + rData.caseid + '",';
//        jsondata+='"caseownerid":"' +rData.caseownerid+ '",';
//        jsondata+='"caseassignedtoid":"' +rData.caseassignedtoid+ '",';
//        jsondata+='"casename":"' +rData.casename+ '",';
//        jsondata+='"company":"' +rData.company +'",';
//        jsondata+='"auditstr":"' +auditStr+ '",';
//        jsondata+='"contactnameid":"' + rData.contactnameid + '",';
//        jsondata+='"productnameid":"' + rData.productnameid+ '",';
//        jsondata+='"casetypeid":"' + rData.casetypeid + '",';
//        jsondata+='"casestatusid":"' + rData.casestatusid + '",';
//        jsondata+='"casepriorityid":"' + rData.casepriorityid + '",';
//        jsondata+='"accountnameid":"' + rData.accountnameid + '",';
//        jsondata+='"description":"' + rData.description + '",';
//        jsondata+='"validflag":"' +validFlag+ '",';
//        jsondata+='"createdon":' + (rData.createdon.getTime?+rData.createdon.getTime():"")+ ',';
//        jsondata+='"dirtyfield":"' + field + '",';
//        jsondata+='"subject":"' + rData.subject +'"';
//        jsondata+=this.spreadSheet.getCustomColumnData(rData,false);
//        jsondata+= '},';
//
//
//        var trmLen = jsondata.length - 1;
//        var finalStr = jsondata.substr(0,trmLen);
//        if(rData.createdon==""){
//              var dates=new Date();
//                 record.set('createdon',dates);
//        }
//        Wtf.Ajax.requestEx({
//
//            url: Wtf.req.springBase+'Case/action/saveCases.do',
//            params:{
//                jsondata:finalStr,
//                type:this.newFlag,////////type for new or edit
//                flag:33/////////////flag for case
//            }
//        },
//        this,
//        function(res)
//        {
//            //  bHasChanged = true;
//            rData.caseid=res.ID;
//            this.afterValidRecordSaved(undefined,undefined,validFlag);
////            if(validFlag==1) {
////                bHasChanged = true;
////                var obj=Wtf.getCmp(Wtf.moduleWidget.cases);
////                if(obj!=null){
////                    obj.callRequest("","",0);
////                    Wtf.refreshUpdatesAll();
////                }
////            }
////            if((field=='casename' || field=='accountnameid') && validFlag==1)
////                Wtf.getCmp("tree").getLoader().load(Wtf.getCmp("tree").root);
//        },
//        function(res)
//        {
//            WtfComMsgBox(252,1);
//        }
//        )
//    },

    afterValidRecordSaved : function (res,finalStr,validFlag) {
        if(validFlag==1) {
            bHasChanged = true;
            var obj=Wtf.getCmp(Wtf.moduleWidget.cases);
            if(obj!=null){
                obj.callRequest("","",0);
            }
            Wtf.refreshUpdatesAll();
        }
    },


    caseDelete:function()
    {
       Wtf.deleteGlobal(this.EditorGrid,this.EditorStore,'Case(s)',"caseid","caseid",'Case',35,36,37);
    },

    showActivity:function()
    {
        
       
        if(this.EditorGrid.getSelectionModel().getCount()==1){
        	 this.rec=this.EditorGrid.getSelectionModel().getSelected();
        	 var id=this.rec.data.caseid;
             if(id=="0"){
             	WtfComMsgBox(25);
             	return;
             }
            var casename = this.rec.data.subject;
            var titlename = WtfGlobal.getLocaleText("crm.CASE");//"Case";
            if(casename.trim()!=""){
                titlename = casename;
            }
            var tipTitle=WtfGlobal.getLocaleText({key:"crm.common.recsactivity",params:[titlename]});//titlename+"'s Activity";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var id=this.rec.data.caseid;

            var panel=Wtf.getCmp(this.id+'activityCaseTab'+id);
            var newpanel = true;
            if(panel==null)
            {
                panel= new Wtf.activityEditor({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.CASE")+"'>"+title+"</div>",
                    id:this.id+'activityCaseTab'+id,
                    layout:'fit',
                    border:false,
                    closable:true,
                    scope:this,
                    urlFlag:155,
                    modName : "CaseActivity",
                    customParentModName : "Activity",
                    iconCls:getTabIconCls(Wtf.etype.todo),
                    RelatedRecordName:titlename,
                    Rrelatedto:'Case',
                    relatedtonameid:id,
                    highLightId:this.activityId,
                    newFlag:2,
                    subTab:true,
                    mainTab:this.submainTab!=undefined?this.submainTab:this.mainTab
                });
                if(this.submainTab!=undefined)
                    this.submainTab.add(panel);
                else
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
            if(this.submainTab!=undefined){
                this.submainTab.setActiveTab(panel);
                this.submainTab.doLayout();
            } else{
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();
            }
        }
        else if (this.fromExternalLink)
        {
            WtfComMsgBox(16);
        }
        else
        {
            WtfComMsgBox(258,0);
        }
    },
        showLeadDetails : function() {
        this.selectedarray=this.EditorGrid.getSelectionModel().getSelections();
        this.rec=this.EditorGrid.getSelectionModel().getSelected();
        if(this.rec!=undefined){
        this.recname =this.rec.data.subject;
        if(this.selectedarray.length==1) {
            var tipTitle=this.recname+"'s Details";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var recData = this.rec.data;
            var id=this.rec.data.caseid;
            var panel=Wtf.getCmp(this.id+'detailCaseTab'+id);
            if(panel==null) {
                var colHeader=getColHeader(this.EditorColumnArray);
                panel= new Wtf.AboutView({
                    id : this.id+'detailCaseTab'+id,
                    closable:true,
                    recid: id,
                    id2:this.id+'detailCaseTab'+id,
                    cm : this.EditorGrid.colModel,
                    record : this.rec.data,
                    layout:"fit",
                    moduleName:"Case",
                    autoScroll:true,
                    mapid:6,
                    moduleid:3,
                    moduleScope:this,
                    recname:this.recname,
                    iconCls:"pwndCRM contactsTabIcon",
                    fieldCols : ['Subject','Description','Case Name','Owner','Status',"Assigned To",'Priority','Type','Account Name','contacts','Product Name','Case Creation Date','Case Updated Date','Case Created By',"subowners",],
                    fields:colHeader,
                    values : [recData.subject,recData.description,recData.casename,recData.caseowner,recData.casestatus,recData.caseassignedto,recData.casepriority,recData.casetype,recData.accountname,recData.contactname,searchValueFieldMultiSelect(Wtf.productStore,recData.productnameid,'id','name'),WtfGlobal.onlyDateRenderer(recData.createdon),WtfGlobal.onlyDateRenderer(recData.updatedon),recData.casecreatedby,""],
                    customField:this.spreadSheet.getCustomField(),
                    customValues:this.spreadSheet.getCustomValues(recData),
                    grid:this.EditorGrid,
                    Store:this.EditorStore,
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Lead'>"+title+"</div>",
                    selected:this.rec
                });
                if(this.submainTab!=undefined)
                    this.submainTab.add(panel);
                else
                    this.mainTab.add(panel);
            }
            if(this.submainTab!=undefined){
                this.submainTab.setActiveTab(panel);
                this.submainTab.doLayout();
            } else{
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();
            }
        }
        }
        else{
        	WtfComMsgBox(400);
        }

    },

    exportfile: function(type) {
       if(this.searchJson==null) {
           this.searchJson = "";
       }
       var fromdate="";
       var todate="";
       var report="crm";
       var flag = this.urlFlag;
       var exportUrl = Wtf.req.springBase+"Case/action/caseExport.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
       if(this.urlFlag==16) {
            var name="Cases";
			exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,flag,undefined,undefined,field,dir);
       } else {
           name="AccountCase";
			exportWithTemplate(this,type,name,fromdate,todate,report+this.accid,exportUrl,flag,undefined,undefined,field,dir);
       }
    },
	exportSelected: function(type) {
        var report="crm"
        var flag = this.urlFlag;
        if(this.urlFlag==16) {
            var name="Cases";
       } else {
           name="AccountCase";
           report += this.accid;
       }
        var fromdate="";
        var todate="";
        var selArr = [];
        var exportUrl = Wtf.req.springBase+"Case/action/caseExport.do";
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        selArr = this.EditorGrid.getSelectionModel().getSelections();
        var jsondata = "";
        for(var i=0;i<selArr.length;i++)
        {
            if(selArr[i].get("validflag") != -1 && selArr[i].get("validflag") != 0) {
                jsondata+="{'id':'" + selArr[i].get('caseid') + "'},";
            }
        }
        if(jsondata.length > 0) {
            var trmLen = jsondata.length - 1;
            var finalStr = jsondata.substr(0,trmLen);
            exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,flag,undefined,finalStr,field,dir);
        } else {
            if(type=='print')
                ResponseAlert(553);
            else
                ResponseAlert(552);
        }
    }, 
  showArchived:function() {
        var panel=Wtf.getCmp('CaseArchivePanel'+this.accid);
        this.archiveUrlFlag=16;
        var tipTitle=WtfGlobal.getLocaleText({key:"crm.editor.archivedwin.title",params:[WtfGlobal.getLocaleText("crm.CASE")]});//"Archived Cases";
        var qtitle=WtfGlobal.getLocaleText("crm.editor.archivewin.title");//"Archived";
        if(this.relatedName=="Account"){
            this.archiveUrlFlag=64
            tipTitle=this.RelatedRecordName+"'s Archived Cases";
            qtitle="Account";
        }
        var title = Wtf.util.Format.ellipsis(tipTitle,18);
        if(panel==null) {
            panel=new Wtf.caseEditor({
                border:false,
                title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+qtitle+"'>"+title+"</div>",
                layout:'fit',
                closable:true,
                id:'CaseArchivePanel'+this.accid,
                iconCls:getTabIconCls(Wtf.etype.archived),
                newFlag:3,
                modName : "CaseArchived",
                customParentModName : "Case",
                parenturlFlag:this.urlFlag,
                parentId:this.EditorGrid.getId(),
                arcFlag:1,
                urlFlag:this.archiveUrlFlag,
                accid:this.accid,
                addFlag:0,
                archivedFlag:1,
                mainTab:this.submainTab!=undefined?this.submainTab:this.mainTab,
                archivedParentName:"Case",
                subTab:true,
                submainTab:this.submainTab!=undefined?this.submainTab:this.mainTab
             })
            
            if(this.submainTab!=undefined)
                this.submainTab.add(panel);
            else
                this.mainTab.add(panel);
        }
        if(this.submainTab!=undefined){
            this.submainTab.setActiveTab(panel);
            this.submainTab.doLayout();
        } else{
            this.mainTab.setActiveTab(panel);
            this.mainTab.doLayout();
        }
        
    },
    ArchiveHandler:function(a) {
        if(this.parentId == undefined) {
            var data={
                a:a,
                tbarArchive:this.tbarArchive,
                EditorGrid:this.EditorGrid,
                title:'Case',
                keyid:'id',
                valueid:"caseid",
                table:'Case',
                GridId:'CaseGrid',
                homePanId:'CaseHomePanel',
                archivedPanId:'CaseArchivePanel',
                name:"name",
                valueName:"subject"
            }
        } else {
            var temp=this.parentId.split('CaseGrid');
            if(this.parenturlFlag==64) {
                data={
                    a:a,
                    tbarArchive:this.tbarArchive,
                    EditorGrid:this.EditorGrid,
                    title:WtfGlobal.getLocaleText("crm.CASE"),//"'Case',
                    keyid:'id',
                    valueid:"caseid",
                    table:'Case',
                    GridId:'CaseGrid',
                    homePanId:temp[1],
                    archivedPanId:'CaseArchivePanel'+this.accid,
                    name:"name",
                    valueName:"subject"
                }
            } else {
                data={
                    a:a,
                    tbarArchive:this.tbarArchive,
                    EditorGrid:this.EditorGrid,
                    title:WtfGlobal.getLocaleText("crm.CASE"),//'Case',
                    keyid:'id',
                    valueid:"caseid",
                    table:'Case',
                    GridId:'CaseGrid',
                    homePanId:'CaseHomePanel',
                    archivedPanId:'CaseArchivePanel'+this.accid,
                    name:"name",
                    valueName:"subject"
                }
            }
        }
        var mod = "CrmCase";
        var audit = "85";
        var auditMod = "Case";
        data["ownerName"] = "caseownerid";
        Wtf.ArchivedGlobal(data, mod, audit,auditMod);
    },
    temp: function(){
        Wtf.highLightGlobal(this.highLightId,this.EditorGrid,this.EditorStore,"caseid");
   }
});
