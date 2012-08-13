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
Wtf.productEditor = function (config){
    Wtf.productEditor.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.productEditor,Wtf.Panel,{
	getEditor:Wtf.SpreadSheetGrid.prototype.getEditor,
    initComponent: function(config){
        Wtf.productEditor.superclass.initComponent.call(this,config);

        Wtf.commonWaitMsgBox("Loading data...");

//        this.getStores();

        this.getEditorGrid();

        this.getAdvanceSearchComponent();

        this.getDetailPanel();

        this.productpan= new Wtf.Panel({
            layout:'border',
            title:WtfGlobal.getLocaleText({key:"crm.tab.title",params:[WtfGlobal.getLocaleText("crm.PRODUCT")]}),//"Product List",
            iconCls:getTabIconCls(Wtf.etype.product),
            border:false,
            id:this.id+'productpan',
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
            this.objsearchComponent,
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
                split : true,
                plugins: new Wtf.ux.collapsedPanelTitlePlugin(),
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
           id:this.id+"productTabPanel",
           scope:this,
           border:false,
           resizeTabs: true,
           minTabWidth: 155,
           enableTabScroll: true,
           items:[this.productpan]
        });

        if(this.archivedFlag==1){
            this.add(this.productpan);
        }else{
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.productpan);
        }
        this.doLayout();
        this.productpan.on("activate",function(){
            Wtf.getCmp("ProductHomePanelproductpan").doLayout()
        },this)
        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);
        
        
    },
    getDetails:function(){
    	var sm = this.EditorGrid.getSelectionModel();
    	if(sm.getCount()!=1)
    		return;
    	var commentlist = getDocsAndCommentList(sm.getSelected(), "productid",this.id,undefined,'Product',undefined,undefined,undefined,undefined,0);    	
    },
    getEditorGrid : function (){
        x=[
        {
            name:'owner',
            defValue:_fullName
        },
        {
            name:'ownerid',
            defValue:loginid
        },
        {
            name:'productid'
        },
        {
            name:'productname'
        },        
        {
            name:'unitprice',
            type:'float'
        },
        {
            name:'vendornamee'
        },        
        {
            name:'vendorname'
        },
        {
            name:'vendorphoneno'
        },
        {
            name:'vendoremail'
        },
        {
            name:'categoryid'
        },
        {
            name:'category'
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
        },
        {
            name:'updatedon',
            dateFormat:'time',
            type:'date'
        },
        {
            name:'description'
        },
        {
            name:"totalcomment"
        },
        {
            name:"commentcount"
        },
        {
            name:"cellStyle"
        },
        {
            name:'validflag'
        }

        ];

        this.EditorRec = new Wtf.data.Record.create(x);
        this.loadCount=0;
        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.EditorRec);
        this.updateInfo = {
               	keyField:'productid',
            	auditStr:"Product details updated from product profile for ",
            	url:"Product/action/updateMassProducts.do",
            	flag:24,
            	type:"Product"
    		};

        this.EditorStore = new Wtf.data.Store({
        	proxy: new Wtf.data.HttpProxy(new Wtf.data.Connection({url: Wtf.req.springBase+"Product/action/getProducts.do", timeout:90000})), 
//            url: Wtf.req.springBase+"Product/action/getProducts.do",
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            paramNames:{sort:'field',dir:'direction'},
            baseParams:{
                flag:12,
                isarchive:this.newFlag==3?true:false
            },
            extraSortInfo:{xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false},
            sortInfo:{field:"updatedon",direction:"DESC"},
            method:'post',
            reader:EditorReader
        });

        this.EditorColumnArray =[
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
                header:WtfGlobal.getLocaleText("crm.product.defaultheader.productname"),//"Product Name",
                tip:WtfGlobal.getLocaleText("crm.product.defaultheader.productname"),//'Product Name',
                id:'productname',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor : new Wtf.form.TextField({xtype:"textfield", maxLength:255,regexText:Wtf.MaxLengthText+"255"}),
                dbname:'c.productname',
                xtype:'textfield',
                validationId:"pname",
                dataIndex: 'productname'
            },
            {
                header:WtfGlobal.getLocaleText("crm.product.defaultheader.unitprice")+"("+ WtfGlobal.getCurrencySymbol()+")",//"Unit Price ("+WtfGlobal.getCurrencySymbol()+")",
                tip:WtfGlobal.getLocaleText("crm.product.defaultheader.unitprice"),//'Unit Price',
                id:'unitprice',
                title:'exportprice',
                pdfwidth:60,
                width:Wtf.width,
                renderer:WtfGlobal.currencyRenderer,
                sortable:true,
                editor : new Wtf.form.NumberField({xtype : "numberfield", maxLength : 15,allowNegative : false,regexText:Wtf.MaxLengthText+"15"}),
                dataIndex:'unitprice',
                dbname:'ifnull(CONVERT(c.unitprice,DECIMAL(64,4)),0)',
                xtype:'numberfield',
                align:"right"
            },
            {
                header:WtfGlobal.getLocaleText("crm.product.defaultheader.owner"),//"Owner",
                tip:WtfGlobal.getLocaleText("crm.product.defaultheader.owner"),//'Owner',
                id:'owner',
                title:'owner',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                hidden:true,
                editor : this.getEditor({xtype:"combo", store:Wtf.productOwnerStore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'owner', loadOnSelect : true}),
                dataIndex:'owner',
                dbname:'c.usersByUserid.userID',
                sortable: true,
                xtype:'combo',
                cname:'owner'
            },
            {
                header:WtfGlobal.getLocaleText("crm.product.defaultheader.category"),//"Category",
                tip:WtfGlobal.getLocaleText("crm.product.defaultheader.category"),//'Category',
                id:'category',
                title:'category',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({xtype:"combo", store:Wtf.productcategorystore, useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'category', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.productcategory}),
                dataIndex: 'category',
                dbname:'c.crmCombodataByCategoryid.ID',
                sortable: true,
                xtype:'combo',
                cname:'Product Category'
            },{
                header:WtfGlobal.getLocaleText("crm.product.defaultheader.desc"),//"Description",
                tip : WtfGlobal.getLocaleText("crm.product.defaultheader.desc"),//"Description",
                id:'description',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable:true,
                editor : new Wtf.form.TextField({xtype:"textfield", maxLength:1024,regexText:Wtf.MaxLengthText+"1024"}),
                dataIndex:'description',
                dbname:'c.description',
                xtype:'textfield',
                renderer : function(val) {
            	var tmp = Wtf.util.Format.htmlEncode(val);
                    return "<div wtf:qtip=\""+tmp+"\"wtf:qtitle='Description'>"+val+"</div>";
                }
            },{
                header:WtfGlobal.getLocaleText("crm.product.defaultheader.vendorname"),//"Vendor Name",
                tip:WtfGlobal.getLocaleText("crm.product.defaultheader.vendorname"),//'Vendor Name',
                id:'vendorname',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor : new Wtf.form.TextField({xtype:"textfield", maxLength:255,regexText:Wtf.MaxLengthText+"255"}),
                dbname:'c.vendornamee',
                xtype:'textfield',
                dataIndex: 'vendornamee',
                validationId:"vendornameid"
            },
            {
                header:WtfGlobal.getLocaleText("crm.product.defaultheader.vendoremail"),//"Vendor Email",
                tip:WtfGlobal.getLocaleText("crm.product.defaultheader.vendoremail"),//'Vendor Email',
                id:'vendoremail',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor : new Wtf.form.TextField({
                    xtype : "textfield",
                    regex: Wtf.ValidateMailPatt,
                    maxLength:100,
                    regexText : Wtf.EmailInvalidText
                }),
                dbname:'c.vendoremail',
                xtype:'textfield',
                dataIndex: 'vendoremail',
                renderer:WtfGlobal.renderEmailTo
            },
            {
                header:WtfGlobal.getLocaleText("crm.product.defaultheader.vendorcontactno"),//"Vendor Contact No",
                tip:WtfGlobal.getLocaleText("crm.product.defaultheader.vendorcontactno"),//'Vendor Contact No',
                id:'vendorcontactno',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor : new Wtf.form.TextField({
                    xtype : "textfield",
               //     regex : Wtf.PhoneRegex,
                    maxLength : 100,
                    regexText:Wtf.MaxLengthText+"100"
               //     regexText:Wtf.PhoneInvalidText
                }),
                dbname:'c.vendorphoneno',
                xtype:'textfield',
                dataIndex: 'vendorphoneno',
                renderer:WtfGlobal.renderContactToCall
            },{
                header:WtfGlobal.getLocaleText("crm.product.defaultheader.productcreatedon"),//"Product Creation Date",
                tip:WtfGlobal.getLocaleText("crm.product.defaultheader.productcreatedon"),//'Product Creation Date',
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
                header:WtfGlobal.getLocaleText("crm.product.defaultheader.productupdatedon"),//"Product Updated Date",
                tip:WtfGlobal.getLocaleText("crm.product.defaultheader.productupdatedon"),//'Product Updation Date',
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
                    offset:Wtf.pref.tzoffset,
                    hidden:true,
                    format:WtfGlobal.getOnlyDateFormat()
                }),
                dataIndex: 'updatedon',
                readOnly:true,
                dbname:'c.updatedOn',
                xtype:'datefield',
                offset:Wtf.pref.tzoffset,
                renderer:WtfGlobal.onlyDateRendererTZ
            }
            ];


        this.tbarArchiveArray = Wtf.archivedMenuArray(this,"Product");
        this.tbarArchive = Wtf.archivedMenuButtonA(this.tbarArchiveArray,this,"Product");
 
        var extraConfig = {};
        var extraParams = "{ \"Deleteflag\":0, \"UsersByUserid\":\""+loginid+"\", \"UsersByCreatedbyid\":\""+loginid+"\", \"UsersByUpdatedbyid\":\""+loginid+"\", \"Company\":\""+companyid+"\"}";
        this.importProductsA =Wtf.importMenuArray(this,"Product",this.EditorStore,extraParams, extraConfig);
        this.importProducts = Wtf.importMenuButtonA(this.importProductsA,this,"Product");
        
        this.toolbarItems = [];
        this.tbSingle = [];
        this.tbMulti = [];
        this.tbDefault = [];
        var tbIndex = 0;
        this.deleteCon= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
            tooltip:{text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip")},//'Select row(s) to delete.'},
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            disabled:(this.newFlag==1?true:false),
            handler:this.productDelete
        });

       this.syncAccProduct= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.product.toptoolbar.datasyncBTN"),//"Data Sync",
            tooltip:{text:WtfGlobal.getLocaleText("crm.product.toptoolbar.datasyncBTN.ttip")},//"Data syncing operation between CRM and Accounting can't be performed as you are not currently subscribed to Accounting."},
            scope:this,
            disabled:true,
            iconCls:"pwndCRM syncIcon",
            handler:this.syncAccProductHandler
       });
        this.quickSearchTF = new Wtf.KWLTagSearch({
            width: 220,
            emptyText:GlobalQuickSearchEmptyText[Wtf.crmmodule.product],
            id:'quick5', //In use,do not delete.
            Store:this.EditorStore,
            parentGridObj: this
        });
        this.recentproducts = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.product.toptoolbar.recentproductBTN"),//"View Recent Products ",
            scope:this,
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.product.toptoolbar.recentproductBTN.ttip")//"Click to view recent products."
            },
           iconCls:'pwndCRM showgrp',
           handler:this.handleRecentProducts
        },this);
        
        this.toolbarItems.push(this.quickSearchTF);
            this.tbSingle.push(tbIndex);
            this.tbMulti.push(tbIndex);
            this.tbDefault.push(tbIndex);
            tbIndex++;
        var optbutton=new Array();
        this.btmbar =[Wtf.moduleBootomToolBar(this,this.id+'CRMupdownCompo',undefined,false),optbutton];
        	if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.manage)) {    
        		if(this.newFlag==2) {
                    this.toolbarItems.push(Wtf.AddNewButton(this));
                	tbIndex++;
                	this.toolbarItems.push(Wtf.EditRecordButton(this));
                	tbIndex++;
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.del)) {
                    this.toolbarItems.push(this.deleteCon);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.archive)) {
                this.toolbarItems.push(this.tbarArchive);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.exportt)) {
                    this.exp = exportButton(this,"Product(s)",5);
                    optbutton.push(this.exp);
                 /*   this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;
*/
                    if(this.clearFlag!=undefined){
                    	 this.tbarExport.on("mouseover", function(){
                         	var s = this.EditorGrid.getSelectionModel().getSelections();
                             this.exp.menu.items.items[1].setDisabled(s.length<=0);
                             this.exp.menu.items.items[3].setDisabled(s.length<=0);
                             this.exp.menu.items.items[5].setDisabled(s.length<=0);
                         },this);
                    }
        
                    this.printprv = printButton(this,"Product",5);
                    this.tbarPrint.on('mouseover',function() {
                    	var s = this.EditorGrid.getSelectionModel().getSelections();
                        this.printprv.menu.items.items[1].setDisabled(s.length<=0);
                    }, this);
                    optbutton.push(this.printprv);
                   /* this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;*/
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.importt)) {
                    optbutton.push(this.importProducts);
                }
                this.toolbarItems.push(this.AdvanceSearchBtn = new Wtf.Toolbar.Button({
                        text : WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN"),//"Advanced Search",
                        id:'advanced5',// In use, Do not delete
                        scope : this,
                        tooltip:WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN.ttip"),//'Search for multiple terms in multiple fields.',
                        handler : this.configurAdvancedSearch,
                        iconCls : 'pwnd searchtabpane'
                }));
                    this.tbSingle.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.importacc)) {
                    this.toolbarItems.push(this.syncAccProduct);
                }
            }
            if(this.newFlag==3) {
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.del)) {
                    this.toolbarItems.push(this.deleteCon);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                this.tbarUnArchive = Wtf.archivedMenuButtonB(this,"Product");
                this.toolbarItems.push(this.tbarUnArchive);
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
            }
        }
         if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.manage)) {
            this.toolbarItems.push(this.recentproducts);
            tbIndex++;
        }
        if(this.clearFlag!=undefined){
            this.toolbarItems.push('->');
            var help=getHelpButton(this,5);
            this.toolbarItems.push(help);
        }
                 
        this.spreadSheet = new Wtf.SpreadSheetGrid({
            cmArray:this.EditorColumnArray,
            store:this.EditorStore,
            moduleName : this.customParentModName,
            moduleid:4,
            isEditor:Wtf.isEditable(this.customParentModName,this),
            pagingFlag : true,
            quickSearchTF : this.quickSearchTF,
            parentGridObj : this,
            id:'ProductGrid'+this.id,
            keyid : 'productid',
            archivedParentName:this.archivedParentName,
            updateURL:Wtf.req.springBase+'Product/action/saveProducts.do',
            allowedNewRecord:!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.manage),
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

        this.EditorStore.on("beforeload",function(){
            Wtf.commonWaitMsgBox("Loading data...");
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
        
       if(this.newFlag==2){
            if(WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.manage)) {
                this.EditorGrid.isEditor = false;
//                this.EditorGrid.on("afteredit",this.fillGridValue,this);
//                this.EditorGrid.on("beforeedit",this.beforeEdit,this);
            }
//            else{
//
//            }
        }
        this.EditorStore.on("load",function(){
        	if(this.loadCount !=undefined && this.loadCount != 0)
        		this.loadCount=0;
        	this.SelChange();
            Wtf.updateProgress();
        },this);
        
        this.spreadSheet.on("afterupdate",function(resp,data){
       	 var rec=data.record.data;
       	 	 if(resp.success){
       		 if(rec.unitprice ||  rec.description){
       		Wtf.productStore.load();
       		}}
          
       },this);
  //      this.EditorGrid.on("beforeinsert",this.setDefaultValues,this);
//        this.EditorGrid.on("validateedit",this.validateEdit,this);
        this.gridRowClick();
        this.EditorGrid.getSelectionModel().on("rowselect",this.SelChange,this);
        this.EditorGrid.getSelectionModel().on("rowdeselect",this.SelChange,this);       
        this.EditorGrid.on("sortchange",this.sortChange,this);
        this.EditorGrid.on("rowclick",this.gridCellClick,this);
        this.EditorGrid.on("mouseover",Wtf.hideNotes,this);
        this.spreadSheet.on('beforeupdate',this.checkRefRequired, this);
        this.tbarArchive.on('click',function() {
            if(this.deleteCon.disabled==false)
                this.tbarArchiveArray[0].setTooltip("Archive the selected product.");
            else
                this.tbarArchiveArray[0].setTooltip("Select row(s) to send in Archive repository.");
        }, this);

    },
    reloadComboStores : function () {
        Wtf.productcategorystore.load();
    },
    showAdvanceSearch:function(){
        showAdvanceSearch(this,this.searchparam);
    },
    reloadStore : function(){
            if(this.EditorStore.baseParams && this.EditorStore.baseParams.searchJson){
                this.EditorStore.baseParams.searchJson="";
            }
            this.EditorStore=this.spreadSheet.getMySortconfig(this.EditorStore);
            this.EditorStore.load({
                params:{
                    start:0,
                    limit:25
                }
            });
    },

    handleRecentProducts:function(){
    	this.EditorStore.extraSortInfo = {xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false};
        this.EditorStore.sort("updatedon","DESC");
    },

    gridCellClick : function(grid, ri, e) {

        if(e.target.className == "clicktoshowcomment") {
            Wtf.onCellClickShowComments(grid.getStore().getAt(ri).data.productid, this.customParentModName, ri, e);
        }
        if(e.target.className == "showMandatoryFields") {
            Wtf.emptyMandatoryFields(grid,ri);
        }
        var sel = this.EditorGrid.getSelectionModel().getSelections();
        var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        if(sel!=undefined){
            if(sel.length == 1 && selectedRec.data.productid!="0" || sel.length>1)
                this.massUpdate.enable();
            else
                this.massUpdate.disable();
        }
    },
//    beforeEdit :function(e){
//        if(e.record.get('productid')=="0" && e.record.get('validflag') != -1){
//            ResponseAlert(200);
//            return false;
//        }
//    },
//     validateEdit:function(e){
//
//            if(e.field=="createdon" && e.value==""){
//                return false;
//            }
//    },

    sortChange:function(grid,sortInfo, sortFlag){
        this.sortInfo = sortInfo;
    },
    storeLoad:function(){
      
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Product";
        var fromdate="";
        var todate="";
        var report="crm"
        var url = Wtf.req.springBase+"Product/action/exportProduct.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,url,undefined,undefined,undefined,field,dir);
    },
    getDetailPanel:function()
    {
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.EditorGrid,
            Store:this.EditorStore,
            modulename:'product',
            keyid:'productid',
         //   height:200,
            mapid:3,
            id2:this.id,
            moduleName:'Product',
            detailPanelFlag:(this.archivedFlag==1?true:false)
        });
    },
    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.spreadSheet.colArr,
            module : Wtf.crmmodule.product,
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
            flag:12,
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
            flag:12,
            searchJson:this.searchJson
        }
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
    },

    getStores:function(){

//        chkownerload(Wtf.crmmodule.product);
//
//        if(!Wtf.StoreMgr.containsKey("productcategory")){
//            Wtf.productcategorystore.load();
//            Wtf.StoreMgr.add("productcategory",Wtf.productcategorystore)
//        }

    },

    syncAccProductHandler:function(){

        Wtf.MessageBox.show({
            title: WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//"Confirm",
            msg: WtfGlobal.getLocaleText("crm.product.confirmtosyncwidaccount.msg"),//"Are you sure you want to synchronize Product list with Accounting?",
            buttons: Wtf.MessageBox.OKCANCEL,
            animEl: 'upbtn',
            icon: Wtf.MessageBox.QUESTION,
            scope:this,
            fn:function(bt){
                if(bt=="ok"){
                  Wtf.Ajax.requestEx({
                    url: Wtf.req.springBase+"Integration/AccountIntegration/syncAccountingProducts.do"
                },
                this,
                function(res) {
                    if(res.success){
                        Wtf.productcategorystore.load();
                        this.EditorStore.load({params:{start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
                        ResponseAlert(900);
                    } else {

                       var subscribePanel = new Wtf.FormPanel({
                            width:'80%',
                            method :'POST',
                            scope: this,
                            border:false,
                            fileUpload : true,
                            waitMsgTarget: true,
                            labelWidth: 70,
                            bodyStyle: 'font-size:10px;padding:10px;',
                            layout: 'form',
                            items:[{
                                border:false,
                                        html:"<div style = 'font-size:12px; width:100%;height:100%;position:relative;float:left;'>"
                                        +"Data syncing operation between CRM and Accounting can't be performed as you are not currently subscribed to Accounting."
                                        +" In order to subscribe to Accounting, click <a target='_blank' class='linkCls' href='http://www.deskera.com/erp/pricing-and-signup'> <b> Subscribe</b> </a> else click <b>Cancel</b>"
                                        +"</div>"
                            }]
                        },
                        this);
                        var impWin1 = new Wtf.Window({
                            resizable: false,
                            scope: this,
                            layout: 'border',
                            modal:true,
                            width: 380,
                            height: 220,
                            border : false,
                            iconCls: 'pwnd favwinIcon',
                            title: 'Data Syncing',
                            items: [
                                    {
                                        region:'north',
                                        height:70,
                                        border : false,
                                        bodyStyle : 'background:white;',
                                        html: getTopHtml("Dear "+ _fullName+",", "")
                                    },{
                                        region:'center',
                                        layout:'fit',
                                        border:false,
                                        bodyStyle : 'background:white;',
                                        items:[subscribePanel]
                                    }
                            ],
                            buttons: [{
                                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                                id:'canbttn1',
                                scope:this,
                                handler:function() {
                                    impWin1.close();
                                }
                            }]
                        },this);

                        impWin1.show();
                    }
                },
                function() {

                })

                }
            }
        });
    },
    newGrid:function()
    {
        var newFlag=1;
    },
//    addNewRec:function (){
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Product, Wtf.Perm.Product.manage)) {
//             var gridRec={
//                validflag:-1,
//                productid:"0",
//                //ownerid:loginid,
//                ownerid:'',
//                owner:'',
//                productname:"",
//                vendornamee:"",
//                vendorphoneno:"",
//                vendoremail:"",
//                categoryid:"",
//                unitprice:"",
//                createdon:'',
//                description:""
//            }
//            this.newRec = new this.EditorRec(this.spreadSheet.getEmptyCustomFields(gridRec));
//            this.EditorStore.insert(0, this.newRec);
//        }
//    },
    
//    fillGridValue:function (e){
//
//    	this.validSave(e.row,e.record,e.field,e);
//    },

     gridRowClick:function(e,rowIndex) {
        WtfGlobal.enableDisableTbBtnArr(this.toolbarItems, this.EditorGrid, this.tbSingle, this.tbMulti, this.tbDefault);
        WtfGlobal.enableDisableTbBtnArr(this.tbarArchiveArray, this.EditorGrid, [0,1], [0,1], [1]);
        if(this.massUpdate!=undefined){
        	var sel = this.EditorGrid.getSelectionModel().getSelections();
        	var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        	if(sel!=undefined){
        		if(sel.length == 1 && selectedRec.data.productid!="0" || sel.length>1)
        			this.massUpdate.enable();
        		else
        			this.massUpdate.disable();
        	}
        }
    },

    SelChange:function(){
        var s = this.EditorGrid.getSelectionModel().getSelections();
        var  selectedRec=this.EditorGrid.getSelectionModel().getSelected();;
        if(s.length == 1 && selectedRec.data.productid!="0"){
        	var updownCompE = Wtf.getCmp(this.id+'CRMupdownCompo');
		    var enableContactsButton=true;
		    var isMainOwner = selectedRec.get('productownerid') == loginid;
            enableButt(this,updownCompE,isMainOwner,true);
        }
        else{
        	 disableButt(this);
        }
        getDocsAndCommentList(selectedRec, "productid",this.id,undefined,'Product');
        this.gridRowClick();
        if(this.deleteCon.disabled==false)
            this.deleteCon.setTooltip('Delete the selected product(s).');
        else
            this.deleteCon.setTooltip('Select row(s) to delete.');
        if(s.length == 1 && selectedRec.data.productid!="0" || s.length>1){
        	if(this.massUpdate!=undefined)
        		this.massUpdate.enable();
        }else{
        	if(this.massUpdate!=undefined)
        		this.massUpdate.disable();
        	docCommentDisable(this);
        }
        

    },
    checkRefRequired:function(e){
    	
    	e.json['pname']=e.json['productname'];
        e.json['vendornameid']=e.json['vendornamee'];
    	if(e.record.data[Wtf.SpreadSheetGrid.VALID_KEY]==0&&e.record.modified[Wtf.SpreadSheetGrid.VALID_KEY]==1)
    		e.url= Wtf.req.springBase+"common/crmCommonHandler/saveProducts.do";
    },
//    validSave:function(rowindex,record,field, e){
//        var modifiedRecord=this.EditorGrid.getStore().getModifiedRecords();
//        if(modifiedRecord.length<1){
//         //   WtfComMsgBox(350,0);
//            return false;
//        }
//        this.saveData(rowindex,record,field, e);
//    },
//    saveData:function(rowindex,record,field, e){
//        var event=e;
//        var rData = record.data;
//        var auditStr=Wtf.SpreadSheet.constructAuditStr(this.EditorGrid,rowindex, e.column, e.value, e.originalValue,record);
//        var jsondata="";
//        var validFlag=1;
//
//        //unit price mandatory check removed - 16-02-10 (zice)
//
////      if(rData.productname.trim()=="")
////        {
////            validFlag=0;
////        }
//        var temp=rData.ownerid;
//        if(temp=="")
//        {
//            record.set('ownerid', loginid);
//            record.set('owner', _fullName);
//        }
//
//        var columnarray = this.spreadSheet.getGrid().colModel.config;
//        for(var ctr=0;ctr<columnarray.length;ctr++){
//            if(columnarray[ctr].mandatory){
//                 if(rData[columnarray[ctr].dataIndex]==" " || rData[columnarray[ctr].dataIndex]=="" || ( columnarray[ctr].xtype=="textfield" && rData[columnarray[ctr].dataIndex].trim()=="")||(columnarray[ctr].xtype=="combo"&&rData[columnarray[ctr].dataIndex]=="99")){
//                      validFlag=0;
//                      break;
//                  }
//            }
//
//        }
//        jsondata+='{"productid":"' + rData.productid + '",';
//        jsondata+='"ownerid":"' +rData.ownerid+ '",';
//        jsondata+='"pname":"' +rData.productname+ '",';
//        jsondata+='"productname":"' +rData.productname+ '",';   // added for commet
//        jsondata+='"vendornamee":"' +rData.vendornamee+ '",';   // added for commet
//        jsondata+='"vendornameid":"' +rData.vendornamee+ '",';
//        jsondata+='"auditstr":"' +auditStr+ '",';
//        jsondata+='"vendorphoneno":"' +rData.vendorphoneno+ '",';
//        jsondata+='"vendoremail":"' +rData.vendoremail+ '",';
//        jsondata+='"unitprice":"' +rData.unitprice+ '",';
//        jsondata+='"description":"' +rData.description+ '",';
//        jsondata+='"createdon":' +(rData.createdon.getTime?rData.createdon.getTime():new Date().getTime())+ ',';
//        jsondata+='"validflag":"' +validFlag+ '",';
//        jsondata+='"dirtyfield":"' + field + '",';   // added for commet
//        jsondata+='"categoryid":"' +rData.categoryid + '"';
//        jsondata+=this.spreadSheet.getCustomColumnData(rData,false);
//        jsondata+= '},';
//
//
//        var trmLen = jsondata.length - 1;
//        var finalStr = jsondata.substr(0,trmLen);
//        var url;
//        // when state changes from valid state to invalid state,redirect to check references
//        if(record.get('validflag') == 1 && validFlag == 0 ){
//            url="common/crmCommonHandler/saveProducts.do";
//        }else{
//            url="Product/action/saveProducts.do";
//        }
//        if(rData.createdon==""){
//            var dates=new Date();
//            record.set('createdon',dates);
//        }
//        Wtf.Ajax.requestEx({
//            url: Wtf.req.springBase+url,
//            params:{
//                jsondata:finalStr,
//                type:this.newFlag,
//                flag:31
//            }
//        },
//        this,
//        function(res) {
//            if(res.revert){
//                // revert if found references in other modules
//                record.set(columnarray[event.column].dataIndex,event.originalValue);
//                WtfComMsgBox(["Alert","Sorry your attempt failed since account is being referenced in <br/><b>"+res.moduleName+"</b>"]);
//            }else{
//                rData.productid=res.ID;
//                var recData = record.data;
////                if(recData.validflag != validFlag ) {
////                    reloadProductStore();
////                }
//                this.afterValidRecordSaved(res, finalStr, validFlag, recData.validflag, field);
//                record.set('validflag',validFlag);
//
////                if(validFlag == 1) {
////                    bHasChanged = true;
////                    var obj=Wtf.getCmp(Wtf.moduleWidget.product);
////                    if(obj!=null){
////                        obj.callRequest("","",0);
////                        Wtf.refreshUpdatesAll();
////                    }
////                }
////                Wtf.getCmp("tree").saveProduct(res,finalStr,1);
//            }
//
////            if(this.highLightId==rData.productid && field=='productname') {
////                Wtf.getCmp("tree").getLoader().baseParams={mode:'0',expandproduct:true};
////                Wtf.getCmp("tree").getLoader().load(Wtf.getCmp("tree").root);
////            } else if(field=='productname' && validFlag==1){
////                var parent;
////                var child;
////                parent=Wtf.getCmp("tree").getNodeById("productnode");
////                child=parent.findChild('id',rData.productid);
////                if(child!=null) {
////                    Wtf.getCmp("tree").getLoader().baseParams={mode:'0',expandproduct:true};
////                    Wtf.getCmp("tree").getLoader().load(Wtf.getCmp("tree").root);
////                }
////            }
//        },
//        function() {
//            WtfComMsgBox(352,1);
//        })
//    },

    afterValidRecordSaved : function (res,finalStr,newValidFlag, oldValidFlag, field) {
        if(oldValidFlag != newValidFlag || field == "productname" || field =='unitprice') {
            reloadProductStore();
        }
        if(newValidFlag == 1) {
            bHasChanged = true;
            var obj=Wtf.getCmp(Wtf.moduleWidget.product);
            if(obj!=null){
                obj.callRequest("","",0);
            }
            Wtf.refreshUpdatesAll();
        }
        Wtf.getCmp("tree").saveProduct(res,finalStr,1);
    },


    productDelete:function()
    {
        Wtf.deleteGlobal(this.EditorGrid,this.EditorStore,'Product(s)',"productid","productid",'Product',26,27,28,"productnode");
    },
    exportfile: function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Product";
        var fromdate="";
        var todate="";
        var report="crm"
        var url = Wtf.req.springBase+"Product/action/exportProduct.do";        
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,url,undefined,undefined,undefined,field,dir);
    },

    exportSelected: function(type) {
        var report="crm"
        var name="Product";
        var fromdate="";
        var todate="";
        var selArr = [];
        var url = Wtf.req.springBase+"Product/action/exportProduct.do";
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            var dir = this.sortInfo.direction;
        }
        selArr = this.EditorGrid.getSelectionModel().getSelections();
        var jsondata = "";
        for(var i=0;i<selArr.length;i++)
        {
            if(selArr[i].get("validflag") != -1 && selArr[i].get("validflag") != 0) {
                jsondata+="{'id':'" + selArr[i].get('productid') + "'},";
            }
        }
        if(jsondata.length > 0) {
            var trmLen = jsondata.length - 1;
            var finalStr = jsondata.substr(0,trmLen);
            exportWithTemplate(this,type,name,fromdate,todate,report,url,undefined,undefined,finalStr,field,dir);
        } else {
            if(type=='print')
                ResponseAlert(553);
            else
                ResponseAlert(552);
        }
    },

    showArchived:function()
    {
        var panel=Wtf.getCmp('ProductArchivePanel');
        if(panel==null)
        {
            panel=new Wtf.productEditor({
                border:false,
                title:WtfGlobal.getLocaleText({key:"crm.editor.archivedwin.title",params:[WtfGlobal.getLocaleText("crm.PRODUCT")]}),//'Archived Products',
                layout:'fit',
                closable:true,
                modName : "ProductArchived",
                customParentModName : "Product",
                id:'ProductArchivePanel',
                iconCls:getTabIconCls(Wtf.etype.archived),
                newFlag:3,
                arcFlag:1,
                archivedFlag:1,
                archivedParentName:"Product",
                subTab:true,
                submainTab:this.mainTab
            })
            this.mainTab.add(panel);
        }
        this.mainTab.setActiveTab(panel);
        this.mainTab.doLayout();
    },
    ArchiveHandler:function(a) {
        var data={a:a,tbarArchive:this.tbarArchive,EditorGrid:this.EditorGrid,title:'Product',treeid:"productnode",keyid:'id',valueid:"productid",table:'Product',GridId:'ProductGrid',homePanId:'ProductHomePanel',archivedPanId:'ProductArchivePanel',name:"name", valueName:"productname", ownerName : "leadownerid"}
        var mod = "CrmProduct";
        var audit = "55";
        var auditMod = "Product";
        Wtf.ArchivedGlobal(data, mod, audit, auditMod);
    },
    temp: function(){
        Wtf.highLightGlobal(this.highLightId,this.EditorGrid,this.EditorStore,"productid");
   }
});
