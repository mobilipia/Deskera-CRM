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
Wtf.newLeadEditor = Wtf.extend(Wtf.Panel,{
//    leadStatusQualifiedID:"f01e5a6f-7011-4e2d-b93e-58b5c6270239",
//    leadStatusPreQualifiedID:"94b9007e-696b-4e1b-9b97-0866dbc10c01",
	getEditor:Wtf.SpreadSheetGrid.prototype.getEditor,
    initComponent: function(config){
        Wtf.newLeadEditor.superclass.initComponent.call(this,config);

        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
    },
    onRender: function(config){
//        this.getStores();

        this.getEditorGrid();

        this.getAdvanceSearchComponent();

        this.getDetailPanel();

        this.leadpan= new Wtf.Panel({
            layout:'border',
            title:WtfGlobal.getLocaleText({key:"crm.tab.title",params:[WtfGlobal.getLocaleText("crm.LEAD")]}),//"Lead List",
            iconCls:getTabIconCls(Wtf.etype.lead),
            border:false,
            id:this.id+'leadpan',
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
            items:[this.objsearchComponent
            ,{
            	region:'center',
                layout:'fit',
                border:false,
                items:this.spreadSheet,
                tbar:this.toolbarItems,
                bbar:this.btmbar
            },{
                region:'south',
                height:250,
                title:WtfGlobal.getLocaleText("crm.editors.otherdetailregion"),//'Other Details',
                collapsible:true,
                collapsed : true,
                plugins: new Wtf.ux.collapsedPanelTitlePlugin(),
                layout: "fit",
                split : true,
                items:[this.detailPanel],
                listeners:{
            		'expand':this.getDetails,
            		scope:this
            	}
            }]
        });
        this.mainTab=new Wtf.TabPanel({
           id:this.id+"leadTabPanel",
           scope:this,
           border:false,
           resizeTabs: true,
           minTabWidth: 155,
           enableTabScroll: true,
           items:[this.leadpan]
        });

        if(this.archivedFlag==1){
            this.add(this.leadpan);
        }else{
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.leadpan);
        }
        this.leadpan.on("activate",function(){
            Wtf.getCmp("LeadHomePanelleadpan").doLayout()
        },this)
        Wtf.newLeadEditor.superclass.onRender.call(this,config);

        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);

    },

    getDetails:function(){
    	var sm = this.EditorGrid.getSelectionModel();
    	if(sm.getCount()!=1)
    		return;
    	var commentlist = getDocsAndCommentList(sm.getSelected(), "leadid",this.id,undefined,'Lead',undefined,"email",'leadownerid',this.contactsPermission,0);
    },

    addExternalPanel:function(panel){
        if(this.mainTab){
            this.mainTab.add(panel);
            this.mainTab.setActiveTab(panel);
            this.doLayout();
        }else{
            this.on('render',this.addExternalPanel.createDelegate(this,[panel],false),this);
        }
    },
    getEditorGrid:function (){
        chkproductStoreload();

        this.viewByStatuStore = new Wtf.data.Store({
            url: 'Common/CRMManager/getComboData.do',
            baseParams:{
                comboname:'Lead Status',
                moduleReq:true,   // to filter Qualified status in Lead Status Combo : Kuldeep Singh
                common:'1'
            },
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, Wtf.ComboReader),
            autoLoad:false
        });
        this.viewByStatuStore.on("load",function(){
            this.addStatusAllEntry();
        },this);

        this.addStatusAllEntry();
//        this.viewByStatuStore.load();
        x=[
        {
            name:'leadid'
        },
        {
            name:'leadowner',
            defValue:_fullName
        },
        {
            name:'leadownerid',
            defValue:loginid
        },
        {
            name:"lastname"
        },
        {
            name:"firstname"
        }/*,
        {
            name:"titleid"
        }*/,
        {
            name:"title"
        },
        {
            name:"leadstatusid"
        },
        {
            name:"leadstatus"
        },
        {
            name:"phone"
        },
        {
            name:"email"
        },
        {
            name:"ratingid"
        },
        {
            name:"rating"
        },
        {
            name:"address"
        },
        {
            name:"leadsource"
        },
        {
            name:"leadsourceid"
        },
        {
            name:"industryid"
        },
        {
            name:"industry"
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
            type:'date'
        },{
            name:"updatedon",
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },{
            name:"editconvertedlead"
        },
        {
            name:'activities'
        },
        {
            name:'comment'
        },
        {
            name:'moredetails'
        },
        {
            name:'isconverted'
        },
        {
            name:"revenue",
            type:'float'
        },
        {
            name:"website"
        },
        {
            name:"mobileno"
        },
        {
            name:'fax'
        },
        {
            name:'addstreet'
        },
        {
            name:'city'
        },
        {
            name:"state"
        },
        {
            name:"country"
        },
        {
            name:"subowners"
        },
        {
            name:'zip'
        },
        {
            name:"description"
        },
        {
            name:"noofempid"
        },
        {
            name:"regionid"
        },
        {
            name:"campaignsrcid"
        },
        {
            name:"productid"
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
            name:"validflag"
        },
        {
            name:"price",
            type:'float'
        },
        {
            name:"type",
            defValue:Wtf.leadtyypedefault
        }
        ];
        this.updateInfo = {
        			keyField:'leadid',
        			auditStr:"Lead details updated from lead profile for ",
        			url:"Lead/action/updateMassLeads.do",
        			flag:22,
        			type:"Lead"
        		};
        
        this.EditorRec = new Wtf.data.Record.create(x);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.EditorRec);

        this.EditorStore = new Wtf.data.Store({

            url: Wtf.req.springBase+"Lead/action/getLeads.do",
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            paramNames:{sort:'field',dir:'direction'},
            baseParams:{
                transfered:0,
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
        },this);
        this.EditorStore.on("load",function(){
        	this.SelChange();
            Wtf.updateProgress();
        },this);

        this.EditorColumnArray =[
            {
                header:'',
                width:30,
                unselectable:true,
                id:'validflag',
                dataIndex:'validflag',
                dbname:'c.validflag',
                sortable: true,
                renderer:WtfGlobal.renderValidFlagAndComment
            },
            {
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.type.ttip"),//'Choose Lead Type',
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.type"),//"Type",
                id:'type',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({store:Wtf.LeadTypeStore,xtype:'combo',editable:false, useDefault:true}),
                dataIndex: 'type',
                title:'exportType',
                dbname:'c.type',
                sortable: true,
                cname:'LeadType',
                xtype:'combo'
            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.lname"),//"Last Name/ Company Name",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.lname"),//'Last Name/ Company Name',
                id:'lastname',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor:new Wtf.form.TextField({
                    xtype : 'textfield',
                    maxLength : 255,
                    regexText:Wtf.MaxLengthText+"255"
                }),
                dbname:'c.lastname',
                xtype:'textfield',
                dataIndex: 'lastname'
            },{
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.fname"),//"First Name",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.fname"),//'First Name',
                id:'firstname',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor:new Wtf.form.TextField({
                    xtype : 'textfield',
                    maxLength : 255,
                    regexText:Wtf.MaxLengthText+"255"
                }),
                dbname:'c.firstname',
                xtype:'textfield',
                dataIndex: 'firstname'
            },{
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.email"),//"Email",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.email"),//'Email',
                id:'email',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                vtype:'email',
                editor : new Wtf.form.TextField({
                    xtype : "textfield",
                    regex: Wtf.ValidateMailPatt,
                    maxLength:100,
                    regexText : Wtf.EmailInvalidText
                }),
                dataIndex: 'email',
                dbname:'c.email',
                xtype:'textfield',
                renderer:WtfGlobal.renderEmailTo
            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.owner"),//"Owner",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.owner"),//'Owner',
                id:'owner',
                title:'owner',
                hidden:true,
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({store:Wtf.leadOwnerStore,xtype:'combo', useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'leadowner', loadOnSelect : true}),
                dataIndex:'leadowner',
                dbname:'lo.usersByUserid.userID',
                cname:'owner',
                xtype:'combo'
            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.leadcreatedon"),//"Lead Creation Date",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.leadcreatedon"),//'Lead Creation Date',
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
                readOnly:true,
                dbname:'c.createdOn',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.leadupdatedon"),//"Lead Updated Date",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.leadupdatedon"),//'Lead Updation Date',
                id:'updatedon',
                title:'updatedon',
                hidden:true,
//                fixed:true,
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'center',
                sortable: true,
                /*editor : new Wtf.form.DateField({
                    xtype:'datefield',
                    hidden:true,
                    offset:Wtf.pref.tzoffset,
                    format:WtfGlobal.getOnlyDateFormat()
                }),*/
                dataIndex: 'updatedon',
                readOnly:true,
                dbname:'c.updatedOn',
                xtype:'datefield',
                renderer:WtfGlobal.onlyDateRendererTZ
            },
            {
            	header:WtfGlobal.getLocaleText("crm.lead.defaultheader.product"),//"Product",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.product.ttip"),//'Double click on a cell below to choose multiple products for lead. Bring the mouse pointer over a cell below to view the products assigned to lead.',
                title:'exportmultiproduct',
                id:'product',
                resizable:false,
                pdfwidth:60,
                width:Wtf.defaultWidth,
                hidden:true,
                editor : this.getEditor({store:Wtf.productStore,xtype:'select', useDefault:true}),
                dataIndex: 'productid',
                dbname:'p.productId.productid',
                cname:'Product',
                xtype:'select'
            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.expectedRevenue")+" ("+WtfGlobal.getCurrencySymbol()+")",///"Expected Revenue ("+WtfGlobal.getCurrencySymbol()+")",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.expectedRevenue"),//'Revenue',
                title:'exportrevenue',
                id:'revenue',
                pdfwidth:80,
                width:Wtf.defaultWidth,
                sortable: true,
                hidden:true,
                editor : new Wtf.form.NumberField({xtype : "numberfield", maxLength : 15 ,regexText:Wtf.MaxLengthText+"15",allowNegative : false}),
                align:'right',
                dbname:'ifnull(CONVERT(c.revenue,DECIMAL(64,4)),0)',
                xtype:'numberfield',
                renderer:WtfGlobal.currencyRenderer,
                dataIndex: 'revenue'

            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.price")+" ("+WtfGlobal.getCurrencySymbol()+")",//"Price ("+WtfGlobal.getCurrencySymbol()+")",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.price"),//'Price',
                title:'exportprice',
                id:'price',
                pdfwidth:80,
                width:Wtf.defaultWidth,
                sortable: true,
                hidden:true,
                editor : new Wtf.form.NumberField({xtype : "numberfield", maxLength : 10 ,regexText:Wtf.MaxLengthText+"15",allowNegative : false}),
                align:'right',
                dbname:'ifnull(CONVERT(c.price,DECIMAL(64,4)),0)',
                xtype:'numberfield',
                renderer:WtfGlobal.currencyRenderer,
                dataIndex: 'price'

            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.desig"),//"Title/Designation",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.desig"),//'Title/Designation',
                id:'designation',
                title:'title',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                editor:new Wtf.form.TextField({
                    xtype : 'textfield',
                    maxLength : 100,
                    regexText:Wtf.MaxLengthText+"100"
                }),
                dataIndex: 'title',
                dbname:'c.title',
                cname:'Title',
                xtype:'textfield'
            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.leadstatus"),//"Lead Status",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.leadstatus"),//'Lead Status',
                id:'leadstatus',
                title:'status',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({store:Wtf.lstatusStore,xtype:'combo', useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'leadstatus', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.leadstatus}),
                dbname:'c.crmCombodataByLeadstatusid.ID',
                sortable: true,
                cname:'Lead Status',
                xtype:'combo',
                dataIndex: 'leadstatus'

            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.rating"),//"Rating",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.rating"),//'Rating',
                id:'rating',
                title:'rating',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({store:Wtf.lratingStore,xtype:'combo', useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'rating', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.leadrating}),
                dbname:'c.crmCombodataByRatingid.ID',
                sortable: true,
                cname:'Lead Rating',
                xtype:'combo',
                dataIndex: 'rating'
            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.leadsource"),//"Lead Source",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.leadsource"),//'Lead Source',
                id:'leadsource',
                title:'source',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                editor : this.getEditor({store:Wtf.lsourceStore,xtype:'combo', useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'leadsource', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.leadsource}),
                dbname:'c.crmCombodataByLeadsourceid.ID',
                sortable: true,
                cname:'Lead Source',
                xtype:'combo',
                dataIndex: 'leadsource'
            },{
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.industry"),//"Industry",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.industry"),//"Industry",
                id:'industry',
                title:'industry',
                pdfwidth:60,
                width:120,
                editor : this.getEditor({store:Wtf.industryStore,xtype:'combo', useDefault:true, searchStoreCombo:true, comboFieldDataIndex : 'industry', loadOnSelect : true, storemanagerkey : Wtf.StoreManagerKeys.industry}),
                dataIndex: 'industry',
                dbname:'c.crmCombodataByIndustryid.ID',
                sortable: true,
                cname:'Industry',
                xtype:'combo'
            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.phone"),//"Phone",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.phone"),//'Phone',
                id:'phone',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                align:'right',
                sortable: true,
                hidden:true,
                editor : new Wtf.form.TextField({
                    xtype : "textfield",
                //    regex : Wtf.PhoneRegex,
                  maxLength : 100,
                  regexText:Wtf.MaxLengthText+"100"
            //        regexText:Wtf.PhoneInvalidText
                }),
                dbname:'c.phone',
                xtype:'textfield',
                dataIndex: 'phone',
                renderer:WtfGlobal.renderContactToCall
            },
            {
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.address"),//"Address",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.address"),//'Address',
                id:'address',
                pdfwidth:60,
                width:Wtf.defaultWidth,
                sortable: true,
                hidden:true,
                editor : new Wtf.form.TextField({xtype:"textfield", maxLength:1024,regexText:Wtf.MaxLengthText+"1024"}),
                dataIndex: 'addstreet',
                dbname:'c.addstreet',
                xtype:'textfield',
                renderer : function(val) {
            	var tmp = Wtf.util.Format.htmlEncode(val);
                    return "<div wtf:qtip=\""+tmp+"\"wtf:qtitle='Address'>"+val+"</div>";
                }
            },{
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.comment"),//"Comment",
                tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.comment"),//'Comment',
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
            }
            ];

        this.convertlead= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.editor.lead.convertBTN"),//"Convert",
            tooltip:{text:WtfGlobal.getLocaleText("crm.editor.lead.convertBTN.ttip")},//'Select a Lead to convert into an Account, Opportunity or Contact.'},
            scope:this,
            id:'convertlead',//In use,do not delete.
            iconCls:getTabIconCls(Wtf.etype.convertIcon),
            handler:this.leadConvert
        });
        this.webtolead= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.editor.lead.webToleadformBTN"),//"Web to Lead Form",
            iconCls:"pwnd addwebtoleadbttnIcon",
            tooltip:{text:WtfGlobal.getLocaleText("crm.editor.lead.webToleadformBTN.ttip")},//'Generate code for online lead capture form'},
            scope:this,
            id:'webtolead',//In use,do not delete.
            handler:this.getWTLFormGrid
        });
        this.tbarArchiveArray = Wtf.archivedMenuArray(this,"Lead");
        this.tbarArchive = Wtf.archivedMenuButtonA(this.tbarArchiveArray,this,"Lead");

        this.exp = exportButton(this,"Lead(s)",3);
        this.printprv = printButton(this,"Leads",3);
        this.toolbarItems = [];
        this.tbSingle = [];
        this.tbMulti = [];
        this.tbDefault = [];
        var tbIndex = 0;

        this.tbarShowActivity=new Wtf.Button({
            text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.showActivityBTN"),//'Activities',
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.todo),
            tooltip:{text:WtfGlobal.getLocaleText({key:"crm.editor.toptoolbar.showActivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.LEAD")]})},//'Select a lead to add its activity details.'},
            handler:this.showActivity
        });

        this.deletelead= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            tooltip:{text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip")},//'Select row(s) to delete.'},
            handler:this.leadDelete
        });
        this.tbarCombo1=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("crm.editor.lead.viewbystatuscombo"),//'View  by Status ',
            selectOnFocus:true,
            triggerAction: 'all',
            editable: false,
            mode: 'remote',
            store: this.viewByStatuStore,
            displayField: 'name',
            valueField:'id',
            anchor:'100%',
            value:WtfGlobal.getLocaleText("crm.COMBOEMPTYTXT.all"),//"All",
            width:140
        });
        var extraConfig = {};
        var extraParams = "{\"Type\":\""+Wtf.leadtyypedefault+"\", \"Deleteflag\":0, \"Istransfered\":\"0\", \"Isconverted\":\"0\", \"UsersByCreatedbyid\":\""+loginid+"\", \"UsersByUpdatedbyid\":\""+loginid+"\", \"Company\":\""+companyid+"\"}";
        this.importLeadsA =Wtf.importMenuArray(this,"Lead",this.EditorStore,extraParams, extraConfig);
        this.importLeads = Wtf.importMenuButtonA(this.importLeadsA,this,"Lead");

        this.convertlead.on('enable',function(){
            if(this.tbarCombo1.getValue()==1) {
                this.convertlead.disable();
            }
        },this);

        this.quickSearchTF = new Wtf.KWLTagSearch({
            width: 248,
            emptyText:GlobalQuickSearchEmptyText[Wtf.crmmodule.lead],
            id:'quick3',//In use,do not delete.
            Store:this.EditorStore,
            parentGridObj: this
        });
        this.toolbarItems.push(this.quickSearchTF);
            this.tbSingle.push(tbIndex);
            this.tbMulti.push(tbIndex);
            this.tbDefault.push(tbIndex);
            tbIndex++;
        this.contactsPermission = false;
        var optbutton=[];
        this.btmbar = [];
        
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage)) {
                if(this.newFlag==2){
                  	this.toolbarItems.push(Wtf.AddNewButton(this));
                	tbIndex++;
                	this.toolbarItems.push(Wtf.EditRecordButton(this));
                	tbIndex++;
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.del)) {
                    this.toolbarItems.push(this.deletelead);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.convert)) {
                    this.toolbarItems.push(this.convertlead);
                        this.tbSingle.push(tbIndex);
                        tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.archive)) {
                    this.toolbarItems.push(this.tbarArchive);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        this.tbDefault.push(tbIndex);
                        tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.view)) {
                    this.toolbarItems.push(this.tbarShowActivity);
                  //  this.tbSingle.push(tbIndex);
                    tbIndex++;
                }
                this.contactsPermission = true;
                this.btmbar.push(Wtf.moduleBootomToolBar(this,this.id+'CRMupdownCompo',this.contactsPermission,true));
                
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.exportt)) {
                    optbutton.push(this.exp);
                    /*this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;

                    optbutton.push(this.printprv);
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;*/
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.importt)) {
                    optbutton.push(this.importLeads);
               /*         this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        this.tbDefault.push(tbIndex);
                        tbIndex++;*/
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.exportt)) {
                    optbutton.push(this.printprv);
                }

                this.toolbarItems.push(this.AdvanceSearchBtn = new Wtf.Toolbar.Button({
                    text : WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN"),//"Advanced Search",
                    id:'advanced3',// In use, Do not delete
                    scope : this,
                    tooltip:WtfGlobal.getLocaleText("crm.editor.advanceSearchBTN.ttip"),//'Search for multiple terms in multiple fields.',
                    handler : this.configurAdvancedSearch,
                    iconCls : 'pwnd searchtabpane'
                }));
                tbIndex++;
                
                this.forwardLeadBtn= new Wtf.Toolbar.Button({
                    text:WtfGlobal.getLocaleText("crm.editor.forwardleadsBTN"),//"Forward Leads",
                    scope:this,
                    iconCls:"pwnd composemail",
                    tooltip : WtfGlobal.getLocaleText("crm.editor.forwardleadsBTN.ttip"),// "Click on the button to forward leads as an email to anyone",
                    handler:this.forwardLeads
                });
                this.toolbarItems.push(this.forwardLeadBtn)
                this.tbSingle.push(tbIndex);
                this.tbMulti.push(tbIndex);
                tbIndex++;


                this.createQuotation= new Wtf.Toolbar.Button({
                    text:WtfGlobal.getLocaleText("crm.editor.quotationBTN"),//"Quotation",
                    scope:this,
                    iconCls:"pwndCRM quotationtabicon",
                    tooltip : WtfGlobal.getLocaleText({key:"crm.editor.quotationBTN.ttip",params:[WtfGlobal.getLocaleText("crm.LEAD")]}),//"Click on the button to view/create Quotations for Lead",
                    handler:this.openQuotation
                });
                this.toolbarItems.push(this.createQuotation)
                
                if(Wtf.URole.roleid == Wtf.AdminId){
                    optbutton.push(this.webtolead);
                }
                this.toolbarItems.push('->');
                  tbIndex++;
                this.toolbarItems.push(new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.editor.lead.viewbystatuscombo")));//'View  by Status '));
                  tbIndex++;
                this.toolbarItems.push(this.tbarCombo1);
                  tbIndex++;
                this.toolbarItems.push('-');
                this.btmbar.push(optbutton);
                //this.btmbar.push(this.massUpdate);
            }
            if(this.newFlag==3){
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.del)) {
                    this.toolbarItems.push(this.deletelead);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                this.tbarUnArchive = Wtf.archivedMenuButtonB(this,"Lead");
                this.toolbarItems.push(this.tbarUnArchive);
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
            }
        }
//        this.toolbarItems.push('-');
//         var tooltip="Please click here to sort lead on updation date";
//            this.toolbarItems.push("<a class='tbar-link-text' href='#' onClick='javascript:handleRecentLeads()'wtf:qtip='"+tooltip+"'>Sort On Updation Date</a>");

        this.recentleads = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.editor.lead.viewrecentBTN"),//"View Recent Leads ",
            scope:this,
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.editor.lead.viewrecentBTN.ttip")//"Click to view recent leads."
            },
           iconCls:'pwndCRM showgrp',
           handler:this.handleRecentLeads
        },this);

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage)) {
            this.toolbarItems.push(this.recentleads);
        }
        if(this.clearFlag!=undefined){
            if(WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage))
                this.toolbarItems.push('->');
            var help=getHelpButton(this,3);
            this.toolbarItems.push(help);
        }
       this.spreadSheet = new Wtf.SpreadSheetGrid({
            cmArray:this.EditorColumnArray,
//            remoteSort : true,
            store:this.EditorStore,
            moduleName : this.customParentModName,
            moduleid:2,
            isEditor:Wtf.isEditable(this.customParentModName,this),
            pagingFlag : true,
            quickSearchTF : this.quickSearchTF,
            parentGridObj : this,
            id:'LeadGrid'+this.id,
            keyid : 'leadid',
            archivedParentName:this.archivedParentName,
            updateURL:Wtf.req.springBase+'Lead/action/saveLeads.do',
            allowedNewRecord:!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage),
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
//
//        this.isConverted=0;
//
        if(this.newFlag==2){               //same
            if(WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage)) {
                this.EditorGrid.isEditor = false;
            }
        }

        this.gridRowClick();
        this.EditorGrid.getSelectionModel().on("rowselect",this.SelChange,this);
        this.EditorGrid.getSelectionModel().on("rowdeselect",this.SelChange,this);
        ///////////////archive/////////
       this.tbarCombo1.on('select',function(a,b,c){
            this.tbarShowActivity.enable();
            this.convertlead.enable();
            this.deletelead.enable();
            this.tbarArchive.enable();
            this.tbarArchive.setText(WtfGlobal.getLocaleText("crm.editor.archiveBTN"));//"Archive");
            this.quickSearchTF.setValue("");
//            if(this.tbarCombo1.getValue()==Wtf.common.leadStatusID_Qualified){//converted
//                this.convertlead.disable();
//                this.tbarArchive.disable();
//                this.isConverted=1;
//            }
            this.status = this.tbarCombo1.getValue();

            this.EditorStore.baseParams = {
                flag:1,
                isconverted:this.isConverted,
                transfered:0,
                status:this.status
            }
            this.EditorStore.load({
                    params:{
                        start:0,
                        limit:this.EditorGrid.getBottomToolbar().pageSize
                    }
           });
        },this);


    /////////////////////////////
    this.EditorGrid.on("sortchange",this.sortChange,this);
    this.EditorGrid.on("rowclick",this.gridCellClick,this);
    this.EditorGrid.on("mouseover",Wtf.hideNotes,this);
    this.EditorGrid.on("afterupdate",this.afterValidRecordSaved,this);
    this.tbarArchive.on('click',function() {
        if(this.deletelead.disabled==false)
            this.tbarArchiveArray[0].setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.archiveBTN.ttip",params:[WtfGlobal.getLocaleText("crm.LEAD")]}));//"Archive the selected lead.");
        else
            this.tbarArchiveArray[0].setTooltip(WtfGlobal.getLocaleText("crm.editor.archiveBTN.disabled.ttip"));//"Select row(s) to send in Archive repository.");
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

    handleRecentLeads:function(){
        this.EditorStore.extraSortInfo = {xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false};
        this.EditorStore.sort("updatedon","DESC");
    },

    gridCellClick : function(grid, ri, e) {
        if(e.target.className == "clicktoshowcomment") {
            Wtf.onCellClickShowComments(grid.getStore().getAt(ri).data.leadid, this.customParentModName, ri, e);
        }
        if(e.target.className == "showMandatoryFields") {
            Wtf.emptyMandatoryFields(grid,ri);
        }
        this.gridRowClick();
    },

    addStatusAllEntry : function() {
        var newresentry = new Wtf.ComboReader({
            id: '',
            name: 'All',
            mainid:''
        });
        this.viewByStatuStore.insert(0, newresentry);
    },

    sortChange:function(grid,sortInfo, sortFlag){
        this.sortInfo = sortInfo;
    },
    reloadStore : function(){
//        if(Wtf.ServerSideSort && GlobalSortModel[2]){
//            this.EditorStore.sortInfo = GlobalSortModel[2];
//            delete this.EditorStore.baseParams['direction'];
//            delete this.EditorStore.baseParams['field'];
//            delete this.EditorStore.baseParams['xtype'];
//            delete this.EditorStore.baseParams['xfield'];
//            delete this.EditorStore.baseParams['iscustomcolumn'];
//            this.EditorStore.baseParams = Wtf.apply(this.EditorStore.sortInfo || {}, this.EditorStore.baseParams);
//        }
        this.EditorStore=this.spreadSheet.getMySortconfig(this.EditorStore);
        if(this.EditorStore.baseParams && this.EditorStore.baseParams.searchJson){
            this.EditorStore.baseParams.searchJson="";
        }
        this.EditorStore.load({
            params:{
                start:0,
                limit:25,
                isconverted:this.isConverted
            }
        });
        this.EditorStore.on("load",this.storeLoad,this);
    },
    storeLoad:function(){

    },

    getDetailPanel:function()
    {
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.EditorGrid,
            Store:this.EditorStore,
            modulename:'lead',
            keyid:'leadid',
            height:200,
            mapid:1,
            id2:this.id,
            ownerid : 'leadownerid',
            moduleName:'Lead',
            mainTab:this.mainTab,
            leadDetailFlag:true,
            moduleScope:this,
            detailPanelFlag:(this.archivedFlag==1?true:false),
            contactsPermission:this.contactsPermission
        });
    },

    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.spreadSheet.colArr,
            module : Wtf.crmmodule.lead,
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
            flag:1,
            isconverted:this.isConverted,
            searchJson:this.searchJson,
            transfered:0
        }
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});

        this.AdvanceSearchBtn.enable();
        this.objsearchComponent.hide();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;

        this.EditorStore.baseParams = {
            flag:1,
            searchJson:this.searchJson,
            isconverted:this.isConverted,
            transfered:0
        }
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
    },

    getStores:function(){

//        chkownerload(Wtf.crmmodule.lead);
//        chktitleload();
//        chkleadsourceload();
//        chkindustryload();

//        if(!Wtf.StoreMgr.containsKey("leadstatus")){
//            Wtf.lstatusStore.load();
//            Wtf.StoreMgr.add("leadstatus",Wtf.lstatusStore)
//        }
//
//        if(!Wtf.StoreMgr.containsKey("leadrating")){
//            Wtf.lratingStore.load();
//            Wtf.StoreMgr.add("leadrating",Wtf.lratingStore)
//        }

    },

    /*
     *
     *  function reloadComboStores() called after successfully imports with master record created if not exist
     *
     */
    reloadComboStores : function () {
//        Wtf.titleStore.load(); // not in used
        Wtf.lsourceStore.load();
        Wtf.industryStore.load();
        Wtf.lstatusStore.load();
        Wtf.lratingStore.load();
    },

    gridRowClick:function(e,rowIndex) {
        WtfGlobal.enableDisableTbBtnArr(this.toolbarItems, this.EditorGrid, this.tbSingle, this.tbMulti, this.tbDefault);
        WtfGlobal.enableDisableTbBtnArr(this.tbarArchiveArray, this.EditorGrid, [0,1], [0,1], [1]);
        if(this.massUpdate!=undefined){
        	var sel = this.EditorGrid.getSelectionModel().getSelections();
        	var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        	if(sel!=undefined){
        		if(sel.length == 1 && selectedRec.data.leadid!="0" || sel.length>1)
        			this.massUpdate.enable();
        		else
        			this.massUpdate.disable();
        	}
        }

    },

    SelChange:function(){
        var s = this.EditorGrid.getSelectionModel().getSelections();
       var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        if(s.length == 1 && selectedRec.data.leadid!="0"){
            if(!this.detailPanel.ownerCt.collapsed)
            	getDocsAndCommentList(selectedRec, "leadid",this.id,undefined,'Lead',undefined,"email",'leadownerid',this.contactsPermission,0);
            this.tbarShowActivity.enable();
			var updownCompE = Wtf.getCmp(this.id+'CRMupdownCompo');
		    var enableContactsButton=true;
		    var isMainOwner = selectedRec.get('leadownerid') == loginid;
            docCommentEnable(this,updownCompE,isMainOwner,enableContactsButton);
            enableButt(this,updownCompE,isMainOwner,enableContactsButton);
        } else {
            this.tbarShowActivity.disable();
            docCommentDisable(this);
            disableButt(this);
        }
        if(this.massUpdate!=undefined){
        	if(s.length == 1 && selectedRec.data.leadid!="0"||s.length>1)
        		this.massUpdate.enable();
        	else
        		this.massUpdate.disable();
        }

        this.gridRowClick();
        if(this.deletelead.disabled==false)
            this.deletelead.setTooltip(WtfGlobal.getLocaleText("crm.lead.delbtn.ttip"));//'Delete the selected lead(s).');
        else
            this.deletelead.setTooltip(WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip"));//'Select row(s) to delete');
        if(this.tbarShowActivity.disabled==false && this.newFlag!=3)
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.addactivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.LEAD")]}));//'Add activity details for the selected lead.');
        else if(this.newFlag!=3)
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.disabledaddactivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.LEAD")]}));//'Select a lead to add its activity details.');

        if(this.convertlead.disabled==false && this.newFlag!=3)
            this.convertlead.setTooltip(WtfGlobal.getLocaleText("crm.editor.lead.convertBTN.mainttip"));//'Convert the selected Lead into an Account, Opportunity or Contact.');
        else if(this.newFlag!=3)
            this.convertlead.setTooltip(WtfGlobal.getLocaleText("crm.editor.lead.convertBTN.ttip"));//'Select a Lead to convert into an Account, Opportunity or Contact.');

//        if(this.tbarShowLeadDetail.disabled==false && this.newFlag!=3)
//            this.tbarShowLeadDetail.setTooltip('To view details for the selected lead');
//        else if(this.newFlag!=3)
//            this.tbarShowLeadDetail.setTooltip('Select a Lead to view it\'s details.');
    },
    showcontacts:function() {
        if(this.EditorGrid.getSelectionModel().getSelections().length==1) {
          var  selectedRec=this.EditorGrid.getSelectionModel().getSelected();
            var recData = this.EditorGrid.getSelectionModel().getSelected().data;
            var tipTitle=recData.lastname+"'s Contacts";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var id= recData.leadid;
            var tabid = 'leadContactTab'+id;
            var panel=Wtf.getCmp(tabid);
            if(panel==null) {
                panel= new Wtf.contactEditor({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Lead'>"+title+"</div>",
                    id:tabid,
                    closable:true,
                    modName : "LeadContact",
                    customParentModName : "Contact",
                    scope:this,
                    mapid:id,
                    RelatedRecordName:recData.lastname,
                    relatedName:'Lead',
                    mainselectedRec:selectedRec,
                    keyid:'leadid',
                    mainId:this.id,
                    mainOwnerid:'leadownerid',
                    mainContactpermossion:this.contactsPermission,
                    newFlag:2,
                    iconCls:getTabIconCls(Wtf.etype.contacts),
                    urlFlag:60,
                    subTab:true,
                    layout:'fit',
                    submainTab:this.mainTab,
                    industryid:recData.industryid,
                    industry:recData.industry,
                    leadsourceid:recData.leadsourceid,
                    leadsource:recData.leadsource,
                    addFlag:0
                });
                this.mainTab.add(panel);
            }
            this.mainTab.setActiveTab(panel);
            this.mainTab.doLayout();
        }
        else {
            WtfComMsgBox(59,0);
        }
    },

    showLeadDetails : function() {/*
        this.selectedarray=this.EditorGrid.getSelectionModel().getSelections();
        this.rec=this.EditorGrid.getSelectionModel().getSelected();
        this.recname =this.rec.data.lastname;
        if(this.selectedarray.length==1) {
            var tipTitle=this.recname+"'s Activities";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var recData = this.rec.data;
            var id=this.rec.data.leadid;
            var panel=Wtf.getCmp(this.id+'detailLeadTab'+id);
            if(panel==null) {
                var colHeader=getColHeader(this.EditorColumnArray);
                panel= new Wtf.AboutView({
                    id : this.id+'detailLeadTab'+id,
                    closable:true,
                    recid: id,
                    id2:this.id+'detailLeadTab'+id,
                    cm : this.EditorGrid.colModel,
                    record : this.rec.data,
                    layout:"fit",
                    moduleName:"Lead",
                    autoScroll:true,
                    moduleid:2,
                    mapid:1,
                    moduleScope:this,
                    recname:this.recname,
                    iconCls:"pwndCRM lead",
                    fieldCols : ['Type','Last Name','Email','Lead Owner','Created On','Product','Expected Revenue('+WtfGlobal.getCurrencySymbol()+')','Price('+WtfGlobal.getCurrencySymbol()+')','Designation','Lead Status','Rating','Lead Source','Industry','Phone','Address','subowners'],
                    fields:colHeader,
                    values : [this.searchValueField(Wtf.LeadTypeStore,recData.type,'id','name'),recData.lastname,recData.email,this.searchValueField(Wtf.leadOwnerStore,recData.leadownerid,'id','name'),WtfGlobal.onlyDateRenderer(recData.createdon),this.searchValueField(Wtf.productStore,recData.productid,'id','name'),recData.revenue,recData.price,recData.title,this.searchValueField(Wtf.lstatusStore,recData.leadstatusid,'id','name'),this.searchValueField(Wtf.lratingStore,recData.ratingid,'id','name'),this.searchValueField(Wtf.lsourceStore,recData.leadsourceid,'id','name'),this.searchValueField(Wtf.industryStore,recData.industryid,'id','name'),recData.phone,recData.addstreet,recData.subowners],
                    customField:this.spreadSheet.getCustomField(),
                    customValues:this.spreadSheet.getCustomValues(recData),
                    grid:this.EditorGrid,
                    Store:this.EditorStore,
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Lead'>"+title+"</div>",
                    contactsPermission:this.contactsPermission,
                    selected:this.rec
                });
                this.mainTab.add(panel);
            }
            this.mainTab.setActiveTab(panel);
            this.mainTab.doLayout();
        }*/

        showLeadDetails(this);

    },

    afterValidRecordSaved : function (res,data) {
        if(data.record.get(Wtf.SpreadSheetGrid.VALID_KEY) == 1) {
            bHasChanged = true;
            var obj=Wtf.getCmp(Wtf.moduleWidget.lead);
            if(obj!=null){
                obj.callRequest("","",0);
            }
            Wtf.refreshUpdatesAll();
        }
        Wtf.getCmp("tree").saveLead(res,Wtf.encode(data.params.jsondata),1);
    },

    leadDelete:function()
    {
        Wtf.deleteGlobal(this.EditorGrid,this.EditorStore,'Lead(s)',"leadid","leadid",'Lead',20,21,22,"leadnode");
    },

    ArchiveHandler:function(a)
    {
       var data={a:a,tbarArchive:this.tbarArchive,EditorGrid:this.EditorGrid,title:'Lead',treeid:"leadnode",keyid:'id',valueid:"leadid",table:'Lead',GridId:'LeadGrid',homePanId:'LeadHomePanel',archivedPanId:'LeadArchivePanel',name:"name", valueName:"lastname", ownerName : "leadownerid"}
       var mod = "CrmLead";
       var audit = "36";
       var auditMod = "Lead";
       Wtf.ArchivedGlobal(data, mod, audit,auditMod);
    },

webtoleadform:function(formId){
    var tipTitle=WtfGlobal.getLocaleText("crm.editor.lead.webToleadformBTN");//"Web to Lead Form";
    var title = Wtf.util.Format.ellipsis(tipTitle,18);
    var formRec = null;
    var formFieldsIndex = null;
    if(formId && formId != ""){
        tipTitle=WtfGlobal.getLocaleText({key:"crm.lead.webtoleadeditor.ttip",params:[this.webtoleadformGrid.getSelectionModel().getSelected().get("formname")]});//this.webtoleadformGrid.getSelectionModel().getSelected().get("formname")+"'s Web to Lead Form";
        title = Wtf.util.Format.ellipsis(tipTitle,18);
        var foundindex = this.wtlfStore.find("formid",formId);
        if(foundindex != -1){
            formRec = this.wtlfStore.getAt(foundindex);
            this.WLFormRec = formRec;
            formFieldsIndex = eval(formRec.get("formfields"));
        }
    } else {
        this.WLFormRec = undefined;
    }
    var formFields =[];
    this.arrayStore =[];
    for(var cnt=2;cnt<this.spreadSheet.colArr.length;cnt++){
        var sheetEditor = this.spreadSheet.colArr[cnt].sheetEditor;
        if(!sheetEditor&&this.spreadSheet.colArr[cnt].editor)sheetEditor = this.spreadSheet.colArr[cnt].editor.field;
        if( sheetEditor && this.spreadSheet.colArr[cnt].header !="" && this.spreadSheet.colArr[cnt].header != "Owner *" && this.spreadSheet.colArr[cnt].xtype !="timefield"){
            var fieldChecked = false;
            if(formFieldsIndex){
                for(var j=0;j< formFieldsIndex.length;j++){
                    if(formFieldsIndex[j]==this.spreadSheet.colArr[cnt].dataIndex){
                        fieldChecked=true; //For default columns
                    }else if(formFieldsIndex[j].substr(0, 12) == 'custom_field' && formFieldsIndex[j]==this.spreadSheet.colArr[cnt].id){
                        fieldChecked=true; //For custom columns
                    }
                }
            }
            var dataIndex = this.spreadSheet.colArr[cnt].dataIndex
            var cstmPattern = /custom_field\w/i;

            if(this.spreadSheet.colArr[cnt].id.match(cstmPattern)){

                dataIndex = this.spreadSheet.colArr[cnt].id;
            }
            var tempObj = {
                xtype:'checkbox',
                fieldLable:this.spreadSheet.colArr[cnt].header,
                name:'wlf_'+this.spreadSheet.colArr[cnt].header,
                spreadSheetFieldType:sheetEditor ? sheetEditor.xtype : '',
                spreadSheetDataIndex:dataIndex,
                maxlength:sheetEditor ? sheetEditor.maxLength : '',
                fieldcombostore:sheetEditor ? sheetEditor.store : '',
                value:false,
                bodyStyle:'margin-right:10px;',
                checked:fieldChecked,
                inputValue:true
            }
            if(sheetEditor.store && sheetEditor.store.getCount()<=0){
                this.arrayStore.push(sheetEditor.store);
            }
            var tempobj1 = {
                html:this.spreadSheet.colArr[cnt].header,
                border:false,
                bodyStyle:'margin-left:25px;',
                cls:'x-form-item'

            }
            formFields.push(tempobj1);
            formFields.push(tempObj);
        }
    }
      var panel=Wtf.getCmp('webtoleadformPanel' + formId);
      this.WTLFleadOwner = this.cloneLeadOwnerComboStore(Wtf.leadOwnerStore);
      if(this.WTLFleadOwner.getCount()==0) {
          this.WTLFleadOwner.on("load",this.setLeadOwner,this);
          this.WTLFleadOwner.load({params : {"allUsers":true}});
      }
      if(panel==null) {
            panel = new Wtf.Panel({
                title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Lead'>"+title+"</div>",
                iconCls:formRec != null ?"pwnd editwebtoleadIcon":"pwnd addwebtoleadIcon",
                layout:'fit',
                closable:true,
                id:'webtoleadformPanel' + formId,
                scope:this,
                items:[{
                xtype:"form",
                layout:'form',
                id:'webtoleadform' + formId,
                bodyStyle:'padding:20px;',
                items:[{
                    xtype:"fieldset",
                    title:WtfGlobal.getLocaleText("crm.lead.webtoleadform.formprops"),//"Form Properties",
                    id:'wtlformprops'+formId,
                    autoHeight:true,
                    items:[{
                        xtype:"textfield",
                        fieldLabel:WtfGlobal.getLocaleText("crm.lead.webtoleadform.formname")+'*',//"Form Name*",
                        allowBlank:false,
                        value: formRec != null ? formRec.get("formname") : undefined,
                        name:"formname",
                        maxLength:100
                    },{
                        xtype:"textfield",
                        fieldLabel:WtfGlobal.getLocaleText("crm.lead.webtoleadform.redirecturl"),//"Redirect URL",
                        value: formRec != null ? formRec.get("redirecturl") : undefined,
                        name:"redirectvalue",
                        vtype:'url'
                    },{
                        xtype:"hidden",
                        value:(formId != null && formId != "") ? formId : 0,
                        name:"formid"
    //                },{
    //                    xtype:"checkbox",
    //                    fieldLabel:"Use Client Side Validation and UI elements",
    //                    name:"usercljs",
    //                    value:formRec != null ? formRec.get("usercljs") : ""
                    }]
                },{
                    xtype:"fieldset",
                    title:WtfGlobal.getLocaleText("crm.lead.webtoleadform.fieldselection"),//"Fields Selection",
                    id:"wtlformfields"+formId,
                    autoHeight:true,
                    items:[{
                            layout:'table',
                            layoutConfig:{
                              columns:8
                            },
                        border:false,
                        items:formFields
                    }]
                },this.ownerCombo=new Wtf.form.ComboBox({
                    xtype:"combo",
                    fieldLabel:WtfGlobal.getLocaleText("crm.lead.webtoleadform.leadowner")+'*',//"Lead Owner*",
                    allowBlank:false,
                    store:this.WTLFleadOwner,
//                    id : "wlleadowner" + this.id,
                    name:"wlleadowner",
                    displayField:'name',
                    mode: 'local',
                    triggerAction:'all',
                    typeAhead:true,
                    valueField:'id',
//                        value:"",
                    hiddenName:'wlleadowner',
                    forceSelection:true
                })/*,{
                 xtype:"fieldset",
                 title:"Lead Assignment",
                autoHeight:true,
                items:[{
                            xtype:'checkbox',
                            fieldLabel:'Manual Lead Approval',
                            inputValue:true,
                            id:'manualApproval'


                         },{
                             html:'You will be the default owner of the leads captured throught this form<br/> Default Owner: '+_fullName,
                             border:false
                        }]
            }*/]
            }],
              bbar:[new Wtf.Button({
                      text:WtfGlobal.getLocaleText("crm.lead.webtoleadform.saveformbtn"),//'Save Form',
                      handler:function() {this.saveWTLForm(formId)},
                    scope:this
              }),
                  new Wtf.Button({
                    text:WtfGlobal.getLocaleText("crm.lead.webtoleadform.generateformbtn"),//'Generate Form',
                    handler:function() {this.beforeForm(formId)},
                    scope:this

            })]

            });

          this.mainTab.add(panel);
      } else {
          this.mainTab.setActiveTab(panel);
      }
      this.mainTab.activate(panel);
      panel.doLayout();
    },

    cloneLeadOwnerComboStore : function(store) {
        return new Wtf.data.Store( {
            proxy : store.proxy,
            reader : store.reader,
            baseParams : store.baseParams,
            autoLoad : store.autoLoad
        });
    },
    setLeadOwner : function() {
        if(this.WLFormRec)
            this.ownerCombo.setValue(this.WLFormRec.get("leadowner"));
        this.WTLFleadOwner.un("load",this.setLeadOwner,this);
    },

    saveWTLForm:function(formId){
        var formitems = Wtf.getCmp("webtoleadform"+formId).form.items.items;
        formitems[0].setValue(WtfGlobal.HTMLStripper(formitems[0].getValue()));
        var formname =formitems[0].getValue();
        if(formname.length>formitems[0].maxLength){
            WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.lead.webtoleadform.entervalinputmsg")]);//"Please enter valid inputs for the mandatory fields.<br/>Value in the field exceeding to max length."]);
            formitems[0].setValue("");
            formitems[0].markInvalid();
            formitems[0].allowBlank=false;
            return;
        }
        var formdomain = "";//formitems[1].getValue();
        var redirectURL = formitems[1].getValue();
        var formid=formitems[2].getValue();
        var leadowner=formitems[formitems.length-1].getValue();
        if(!Wtf.getCmp("webtoleadform"+formId).getForm().isValid()) {
            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"), WtfGlobal.getLocaleText("crm.lead.webtoleadform.validationerrormsg"));//'Error in validating your input.\nPlease enter valid inputs for the mandatory fields.')
            return false;
        }
        if(formitems[1].validate() == false) {
            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"), WtfGlobal.getLocaleText("crm.lead.webtoleadform.redirectionformatmsg"));//'Redirect URL field should be a URL in the format <b>"http:/'+'/www.domain.com" </b>');
            return false;
        }
        var formfields="{formfields:[ ";
        var checkFlag = false;
        for(var i=3;i<(formitems.length-1);i++){
            if(formitems[i].checked==true){
               formfields +="\""+formitems[i].spreadSheetDataIndex+"\"";
               formfields +=","
               checkFlag = true;
            }
        }
        formfields = formfields.substr(0, formfields.length-1);
        formfields +="]}";
        if(!checkFlag) {
            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"), WtfGlobal.getLocaleText("crm.lead.webtoleadform.errornseleonemsg"));//'Error in saving Web to Lead Form. Please select atleast one form field.');
            return false;
        }

       Wtf.Ajax.requestEx({
                url: Wtf.req.springBase+"common/Lead/saveEditWTLForm.do",
                params:({
                    flag:829,
                    formname:formname,
                    formid:formid,
                    formfields:formfields,
                    formdomain:formdomain,
                    redirectURL:redirectURL,
                    leadowner:leadowner
                }),
                method: 'POST'
            },
            this,
            function(result, req){
                Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"), WtfGlobal.getLocaleText("crm.lead.webtoleadform.formsavesuccessmsg"));//'Form has been saved successfully.');
                this.wtlfStore.load({
                    params:{
                        start:0,
                        limit:this.webtoleadformGrid.getBottomToolbar().pageSize
                    }
                });
                if(req.params.formid=="0") {
                    this.mainTab.remove(Wtf.getCmp("webtoleadformPanel"));
                }
            },function(){
               Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"), WtfGlobal.getLocaleText("crm.lead.webtoleadform.formsaveerrormsg"));//'Error in saving Web to Lead Form.');
            });
    },
    getWTLFormGrid:function(){
        this.webtoleadformGrid = Wtf.getCmp("managewtlgrid");
        if(this.webtoleadformGrid == null) {
            var reader = new Wtf.data.Record.create([{name:'formname'},{name:'formdomain'},{name:'redirecturl'},{name:'formfields'},{name:'lastupdatedon'},{name:'formid'},{name:'leadowner'}]);
            var columnModel =  new Wtf.grid.ColumnModel([
                                        new Wtf.grid.RowNumberer(),
                                        {
                                            header: WtfGlobal.getLocaleText("crm.lead.webtoleadform.formname"),//"Form Name",
                                            dataIndex: 'formname'
                                        },/*{
                                            header: "Access Domain",
                                            dataIndex: 'formdomain'
                                        },*/{
                                            header: WtfGlobal.getLocaleText("crm.lead.webtoleadform.redirectionURL"),//"Redirection URL",
                                            dataIndex: 'redirecturl'
                                        },{
                                            header: WtfGlobal.getLocaleText("crm.lead.webtoleadform.lastupon"),//"Last Updated on",
                                            dataIndex: 'lastupdatedon'/*,
                                            renderer:WtfGlobal.dateTimeRenderer*/
                                        }]);
            this.wtlfStore = new Wtf.data.Store({
                    url: Wtf.req.springBase+"common/Lead/getWebtoleadFormlist.do",
                    autoLoad:false,
                    reader: new Wtf.data.KwlJsonReader({
                        root:'data',
                        totalProperty:'totalCount'
                    }, reader)
                });
            this.quickPanelSearch  = new Wtf.KWLTagSearch({
                width: 150,
                emptyText:  WtfGlobal.getLocaleText("crm.lead.webtoleadform.searchbyforname"),//'Search by Form Name ',
                Store:this.wtlfStore
            });
            this.pg = new Wtf.PagingSearchToolbar({
                pageSize: 15,
                searchField: this.quickPanelSearch,
                store: this.wtlfStore,
                displayInfo: true,
                emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),
                plugins: this.pP = new Wtf.common.pPageSize()
            });
            var ttitle =  WtfGlobal.getLocaleText("crm.lead.webtoleadform.managewtlfom");//"Manage Web to Lead Forms";
            var title = Wtf.util.Format.ellipsis(ttitle,18);
            this.webtoleadformGrid = new Wtf.grid.GridPanel({
                id:"managewtlgrid",
                sm:this.webtoleadformSm = new Wtf.grid.RowSelectionModel({singleSelect:true}),
                cm:columnModel,
                title:"<div wtf:qtip=\""+ttitle+"\"wtf:qtitle='Lead'>"+title+"</div>",
                iconCls: "pwnd addwebtoleadlistIcon",
                closable:true,
                border:false,
                viewConfig:{
                    forceFit:true,
                    autoFill:true
                },
                store:this.wtlfStore,
                tbar:[this.quickPanelSearch, new Wtf.Button({
                        text:WtfGlobal.getLocaleText("crm.lead.webtoleadform.newformbtn"),//'New Form',
                        handler:this.addwebtoleadform,
                        iconCls:"pwnd addwebtoleadIcon",
                        scope:this
                }),this.editFormBttn = new Wtf.Button({
                        text:WtfGlobal.getLocaleText("crm.lead.webtoleadform.editformbtn"),//'Edit Form',
                        handler:this.editwebtoleadform,
                        iconCls:"pwnd editwebtoleadIcon",
                        scope:this,
                        disabled:true
                }),this.deleteFormBttn = new Wtf.Button({
                        text:WtfGlobal.getLocaleText("crm.lead.webtoleadform.deleteformbtn"),//'Delete Form',
                        handler:this.deletewebtoleadform,
                        iconCls:getTabIconCls(Wtf.etype.delet),
                        tooltip:{text:WtfGlobal.getLocaleText("crm.lead.webtoleadform.deleteformbtn.ttip")},//'Select form to delete.'},
                        scope:this,
                        disabled:true
                })],
                bbar:this.pg

            });
            this.wtlfStore.load({
                params:{
                    start:0,
                    limit:this.webtoleadformGrid.getBottomToolbar().pageSize
                }
            });
            this.wtlfStore.on('load', function(store) {
                this.quickPanelSearch.StorageChanged(store);
            }, this);
            this.wtlfStore.on('datachanged', function() {
                var p = this.pP.combo.value;
                this.quickPanelSearch.setPage(p);
            }, this);

            this.webtoleadformSm.on("selectionchange",function(){
                this.editFormBttn.disable();
                this.deleteFormBttn.disable();
                if(this.webtoleadformSm.getSelected()){
                    this.editFormBttn.enable();
                    this.deleteFormBttn.enable();
                }
            },this);

          this.mainTab.add(this.webtoleadformGrid);
      } else {
          this.mainTab.setActiveTab(this.webtoleadformGrid);
      }
      this.mainTab.activate(this.webtoleadformGrid);
    },
    addwebtoleadform:function(){
      this.webtoleadform("");
    },

    editwebtoleadform:function(){
      var wtlGridSM = this.webtoleadformGrid.getSelectionModel();
      var rec = wtlGridSM.getSelected();
      this.webtoleadform(rec.get("formid"));
    },
    deletewebtoleadform:function(){
        Wtf.MessageBox.show({
            title: WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//"Confirm",
            msg: WtfGlobal.getLocaleText("crm.lead.webtoleadform.delconfirmmsg"),//"Are you sure you want to delete selected Web to Lead Form?",
            buttons: Wtf.MessageBox.YESNO,
            animEl: 'mb9',
            scope:this,
            icon: Wtf.MessageBox.INFO,
            fn:function(btn,text){
                if(btn=="yes"){
                  var wtlGridSM = this.webtoleadformGrid.getSelectionModel();
                  var rec = wtlGridSM.getSelected();
                    Wtf.Ajax.requestEx({
                        url: Wtf.req.springBase+"common/Lead/deleteWTLForm.do",
                        params:({
                            formid:rec.get("formid")
                        }),
                        method: 'POST'
                    },
                    this,
                    function(result, req){
                        if(result.success) {
                            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"), WtfGlobal.getLocaleText("crm.lead.webtoleadform.delsuccessmmsg"));//'Form has been  deleted successfully.');
                            this.wtlfStore.load({
                                params:{
                                    start:0,
                                    limit:this.webtoleadformGrid.getBottomToolbar().pageSize
                                }
                            });
                        } else {
                            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.lead.webtoleadform.delerrormmsg"));//'Error in deleting Web to Lead Form.');
                        }
                    },function(){
                       Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.lead.webtoleadform.delerrormmsg"));//'Error in deleting Web to Lead Form.');
                    });
                }
            }
        })
    },
    getWebFromHTML:function(item){
        var result = "";
        var fieldName  = "wl_"+item.spreadSheetDataIndex;
//        var cstmPattern = /custom_field\d/i;

//        if(fieldName.match(cstmPattern)){
//           fieldName = item.spreadSheetDataIndex;
//        }
      switch(item.spreadSheetFieldType){
          case "textfield":
        	  	result = "<td><input type='text' name='"+fieldName+"' maxlength='"+item.maxlength+"' onblur=\"nohtml(this);\"/></td>\n\t\t";
              	break;
          case "checkbox":
                result = "<td><input type='checkbox' name='"+fieldName+"'/></td>";
              break;
          case "combo":
                result = "<td><select name='"+fieldName+"'>\n\t\t\t";
                var storeRecords = item.fieldcombostore.getRange();
                for(var cnt=0;cnt<storeRecords.length;cnt++){
                    result +="\n\t\t\t<option value='"+storeRecords[cnt].get('id')+"'>"+storeRecords[cnt].get('name')+"</option>";
                }
                result +="\n\t\t</select></td>\n\t\t";
                break;
          case "select":
                result = "<td><select name='"+fieldName+"' multiple>\n\t\t\t";
                storeRecords = item.fieldcombostore.getRange();
                for(cnt=0;cnt<storeRecords.length;cnt++){
                    result +="\n\t\t\t<option value='"+storeRecords[cnt].get('id')+"'>"+storeRecords[cnt].get('name')+"</option>";
                }
                result +="\n\t\t</select></td>\n\t\t";
                break;
          case "timefield":
                result ="<td><input type='text' name='"+fieldName+"' maxlength='5' rel='timefield' onblur=\"nohtml(this);\"/></td>\n\t\t<tr><td>&nbsp;</td><td><span style='font-size:11px'>(hh:mm)</span></td>\n\t\t";
                break;
          case  "datefield":
                result ="<td><input type='text' name='"+fieldName+"' maxlength='10' rel='datefield' onblur=\"nohtml(this);\"/></td></tr>\n\t\t<tr><td>&nbsp;</td><td><span style='font-size:11px'>(yyyy-mm-dd )</span></td>\n\t\t";
                break;
          case "numberfield":
                result ="<td><input type='text' name='"+fieldName+"' maxlength='"+item.maxlength+"' rel='numberfield' onblur=\"nohtml(this);\"/></td>\n\t\t";
                break;

      }
      return result;

    },
    
    getFormCode:function(formId){
        var formitems = Wtf.getCmp("webtoleadform"+formId).form.items.items;

        var actionurl = location.href;
        var splits = Wtf.DomainPatt.exec(actionurl);
        var base = actionurl.replace(splits[0],"b/"+splits[1]);
        actionurl = base+"/"+Wtf.req.springBase+"common/storeLead.do";
        var fieldjsonKeys=[];
        var fieldjsonValues=[];
        
        var resultForm  ="<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<script type=\"text/javascript\"> ";
        	resultForm +=" \n if (typeof String.prototype.trim !== 'function') {";
        	resultForm +=" \n\t String.prototype.trim = function() { ";
        	resultForm +=" \n\t\t return this.replace(/^\\s+|\\s+$/g, \"\");";
        	resultForm +=" \n\t}\n}";
        	resultForm +=" \n function nohtml(ob)";
        	resultForm +=" \n {\n\t var val=ob.value;";
        	resultForm +=" \n\t var chk=val;";
        	resultForm +=" \n\t ob.value= val.replace(/(?:<script.*?>)((\\n|\\r|.)*?)(?:<\\/script>)/ig, \"\").replace(/<\\/?[^>]+>/gi,\"\").trim();";
        	resultForm +=" \n\t if(chk!=ob.value)";	
        	resultForm +=" \n\t\t alert('Input value is containing html tags or scripts that have been removed.')";
        	resultForm +=" \n }";
        	resultForm +=" \n</script>\n</head>\n<body>";
         	
        	resultForm  += "\n\t<div class=\"form_wrapper\">\n\t<h1>"+formitems[0].getValue()+"</h1>\n\t<form method=\"post\" action=\""+actionurl+"\">\n\t\t";
            resultForm +="<table>";
        for(var i=3;i<(formitems.length);i++){
            if(formitems[i].checked==true){
            		resultForm +="<tr><td><label for='wl_"+formitems[i].spreadSheetDataIndex+"'>"+formitems[i].fieldLable+"</label></td>";
                    resultForm +=this.getWebFromHTML(formitems[i])+"</tr>";
                    if(formitems[i].spreadSheetFieldType=="combo" || formitems[i].spreadSheetFieldType=="select"){
                    	fieldjsonKeys.push(formitems[i].spreadSheetDataIndex+"_DROPDOWN#");
                    }else{
                    	fieldjsonKeys.push(formitems[i].spreadSheetDataIndex);
                    }
                    fieldjsonValues.push(formitems[i].fieldLable);                    
            }
        }
        var fieldJSON=WtfGlobal.JSONBuilder(fieldjsonKeys,fieldjsonValues);
        resultForm +="<tr><td><input type='hidden' name='leadOwner' value='"+formitems[formitems.length-1].getValue()+"'/>\n";
        resultForm +="<tr><td><input type='hidden' name='fieldJSON' value='"+ Encoder.htmlEncode(fieldJSON) +"'/>\n";
        resultForm +="<input type='hidden' name='returnurl' value='"+formitems[1].getValue()+"'/>\n";
        resultForm +="<input type='hidden' name='host' id='wl_hidden_host' value=''/></td></tr>\n";

        resultForm +="<tr><td>&nbsp;</td><td><input type='submit' name='submit' value='Submit' style='width:80px;' onclick=\"this.disabled=true;\"/></td></tr></table>\n";
        resultForm +="\t</form>\n</div>\n</body>\n</html>";
        return resultForm;

    },
    showFormCode:function(formId){
        var formitems = Wtf.getCmp("webtoleadform"+formId).form.items.items;
        formitems[0].setValue(WtfGlobal.HTMLStripper(formitems[0].getValue()));
        if(Wtf.getCmp("webtoleadform"+formId).form.isValid()){
            new Wtf.Window({
                width:600,
                height:300,
                modal:true,
                title:WtfGlobal.getLocaleText("crm.editor.lead.webtoleadwin.title"),//'Web to Lead form',
                html:'<dl class="codebox"><dt>Form Code: <a onclick="WtfGlobal.selectCode(); return false;" href="#" style="color:#083772;text-decoration:none;">Select all</a></dt><dd><code><pre id=\'weblead_code\'>'+Wtf.util.Format.htmlEncode(this.getFormCode(formId))+'</pre></code>'

            }).show();
        }else{
             Wtf.Msg.alert('Error', 'Error in validating your input.\nPlease enter valid inputs for the mandatory fields and generate form code again.');
        }
    },
    beforeForm : function(formId){
        this._form_ID = formId;
        if(this.arrayStore.length>0){

            for(var j =0 ; j<this.arrayStore.length; j++){

                this.arrayStore[j].on('load',function(s){
                
                    if(this.arrayStore.length>0){
                        for(var i =0 ; i<this.arrayStore.length; i++){
                            if(this.arrayStore[i]==s){
                                this.arrayStore.splice(i,1);
                                break;
                            }
                        }
                    }
                    if (this.arrayStore.length==0){
                        this.showFormCode(this._form_ID);
                    }
                },this)

                this.arrayStore[j].load();
            }
           
        } else {
            this.showFormCode(this._form_ID);
        }
        
    },
    openQuotation : function() {
        openQuotation(this.mainTab, Wtf.common.leadModuleID, "Lead");
    },
    
    forwardLeads : function() {
        this.selectedarray=this.EditorGrid.getSelectionModel().getSelections();
        this.rec=this.EditorGrid.getSelectionModel().getSelected();
        this.recname =this.rec.data.lastname;
        if(this.selectedarray.length > 0) {
            var detailMsg = "Hi, <br>"+_fullName+" has forwarded you following lead(s):<br>";
            for(var cnt=0;cnt<this.selectedarray.length; cnt++) {
                var recdata = this.selectedarray[cnt].data;
                if(recdata.validflag){
                detailMsg += "<br>Lead Name: "+ recdata.firstname+" "+recdata.lastname;
                detailMsg += "<br>Contact Number: "+ recdata.phone;
                detailMsg += "<br>Email Address: "+ recdata.email;
                detailMsg += "<hr ALIGN='LEFT'>";
            	}
            }
            detailMsg += "<br><br><br><br>";
            this.sendLeadWindow = new Wtf.Window({
                title:'Send lead details',
                layout:'fit',
                iconCls: "pwnd favwinIcon",
                modal: true,
                height:400,
                width:550,
                scope: this,
                items:[this.composeLeadPanel = new Wtf.ReplyWindow({
                        uLabel: WtfGlobal.getLocaleText("crm.lead.forward.to"),//'To',
                        bLabel: WtfGlobal.getLocaleText("crm.lead.forward.subject"),//'Subject',
                        tdisabled: false,
                        userId: loginid,
                        groupId: "",
                        id: 'composeMessagePanel'+this.id,
                        firstReply: "",
                        uFieldValue: "",
                        bFieldValue: "",
                        type: "Mail",
                        sendFlag: "newmsg",
                        details : detailMsg,
                        composeMail: 1,
                        emailid : '',
                        forwardFlag:1
                    })
                ]
            })
            this.sendLeadWindow.show();
            this.composeLeadPanel.on("beforeClosewindow",function(){
                this.sendLeadWindow.close()
            },this);
        }
    },
    leadConvert:function()
    {
        var s=this.EditorGrid.getSelectionModel().getSelections();
        if(s.length==1){
            if(s[0].get('leadstatusid').trim() == "") {
                var strobj = ["Alert", "Please specify the lead status."];
                ResponseAlert(strobj);
                return;
            }
//            var combodataid = Wtf.lstatusStore.getAt(Wtf.lstatusStore.find("id", s[0].get('leadstatusid'))).get("mainid");
//            if(combodataid==Wtf.common.leadStatusID_Qualified){
//                var name = Wtf.lstatusStore.getAt(Wtf.lstatusStore.find("id", s[0].get('leadstatusid'))).get("name");
//                strobj = ["Alert", name +" Lead has been already converted."];
//                ResponseAlert(strobj);
////                Wtf.notify.msg(strobj[0],strobj[1]);
//            } else {
            //Can convert all type of lead
//            var idx2 = Wtf.lstatusStore.getAt(Wtf.lstatusStore.find("id", s[0].get('leadstatusid'))).get("mainid");
//            if(idx2==Wtf.common.leadStatusID_PreQualified){

                var window=Wtf.getCmp('ConvertLeadWindow');
                if(window==null)
                {
                    window=new Wtf.convleadpanel({
                        title:WtfGlobal.getLocaleText(" crm.helpedit.title.23"),//'Convert Lead',
                        resizable:false,
                        frame:true,
                        scope:this,
                        ds:this.EditorStore,
                        rec:this.EditorGrid.getSelectionModel().getSelected(),
                        spreadSheet:this.spreadSheet,
                        id:'ConvertLeadWindow',
                        modal:true,
                        shadow:true
                    });
                }
                window.show();
//            }
//            else{
//                ResponseAlert(48);
//            }
//           }
           }
        else if(s.length==0){
            ResponseAlert(49);
        }
        else if(s.length>1){
            ResponseAlert(50);
        }
    },


    showArchived:function()
    {
        var panel=Wtf.getCmp('LeadArchivePanel');
        if(panel==null)
        {
            panel=new Wtf.newLeadEditor({
                border:false,
                title:WtfGlobal.getLocaleText("crm.editor.archivewin.title"),//'Archived Leads',
                layout:'fit',
                closable:true,
                modName : "LeadArchived",
                customParentModName : "Lead",
                id:'LeadArchivePanel',
                iconCls:getTabIconCls(Wtf.etype.archived),
                newFlag:3,
                arcFlag:1,
                archivedFlag:1,
                archivedParentName : "Lead",
                subTab:true,
                submainTab:this.mainTab
            })
            this.mainTab.add(panel);
        }
        this.mainTab.setActiveTab(panel);
        this.mainTab.doLayout();
    },

    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Leads";
        var fromdate="";
        var todate="";
        var report="crm";
        var exportUrl = Wtf.req.springBase+"Lead/action/leadExport.do";
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,undefined,undefined,field,dir);
    },

    exportfile: function(type){
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name=WtfGlobal.getLocaleText("crm.LEAD.plural");//"Leads";
        var fromdate="";
        var todate="";
        var report="crm";
        var exportUrl = Wtf.req.springBase+"Lead/action/leadExport.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        //exportWithTemplate(this,type,name,fromdate,todate,report, exportUrl);
		exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,undefined,undefined,field,dir);
    },

    exportSelected: function(type) {
        var name=WtfGlobal.getLocaleText("crm.LEAD.plural");//"Leads";
        var fromdate="";
        var todate="";
        var report="crm";
        var selArr = [];
        var exportUrl = Wtf.req.springBase+"Lead/action/leadExport.do";
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
                jsondata+="{'id':'" + selArr[i].get('leadid') + "'},";
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
    showActivity:function()
    {

        if(this.EditorGrid.getSelectionModel().getCount()==1){
        this.rec=this.EditorGrid.getSelectionModel().getSelected();
        var id=this.rec.data.leadid;
        if(id=="0"){
            	WtfComMsgBox(25);
            	return;
            }
            this.addactivityflag=true;
            var leadname = this.rec.data.lastname;
            var titlename = WtfGlobal.getLocaleText("crm.LEAD");//"Lead";
            if(leadname.trim()!=""){
                titlename = leadname;
            }
            var tipTitle=WtfGlobal.getLocaleText({key:"crm.common.recsactivity",params:[titlename]});//titlename+"'s Activity";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var panel=Wtf.getCmp(this.id+'activityLeadTab'+id);
            var newpanel = true;
            if(panel==null)
            {
                panel= new Wtf.activityEditor({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.LEAD")+"'>"+title+"</div>",
                    id:this.id+'activityLeadTab'+id,
                    layout:'fit',
                    border:false,
                    closable:true,
                    urlFlag:151,
                    scope:this,
                    RelatedRecordName:titlename,
                    modName : "LeadActivity",
                    customParentModName : "Activity",
                    iconCls:getTabIconCls(Wtf.etype.todo),
                    Rrelatedto:'Lead',
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
            WtfComMsgBox(16);
        }
        else
        {
            WtfComMsgBox(8,0);
        }
        this.fromExternalLink=false;
    },
   temp: function(){
           Wtf.highLightGlobal(this.highLightId,this.EditorGrid,this.EditorStore,"leadid");
   },

    displayName:function(value,gridcell,record,d,e){
        var uname=(record.json.username).trim();
        return uname;
    },
    CreateNewContact : function() {
        var rec = this.grid.getSelectionModel().getSelected();
        this.addExtContactfunction(0,rec,1);
    },

    custommizeHeader:function(){

       this.customizeHeader=new Wtf.customizeHeader({
            scope:this,
            modulename:"Lead"
        });

        this.customizeHeader.show();

   },
    addExtContactfunction:function(action,record,flag){
        var windowHeading = action==0?WtfGlobal.getLocaleText("crm.mymails.addlead"):WtfGlobal.getLocaleText("crm.lead.editlead");//"Add Lead":"Edit Lead";
        var windowMsg = action==0?WtfGlobal.getLocaleText("crm.lead.enternewleaddetails"):WtfGlobal.getLocaleText("crm.lead.editexistingleaddetails");//"Enter new lead details":"Edit existing lead details";
        this.addExtContactWindow = new Wtf.Window({
            title : action==0?WtfGlobal.getLocaleText("crm.mymails.addlead"):WtfGlobal.getLocaleText("crm.lead.editlead"),//"Add Lead":"Edit Lead",
            closable : true,
            modal : true,
            iconCls : 'pwnd favwinIcon',
            width : 430,
            height: 370,
            resizable :false,
            buttons :[{
                text : action==0? WtfGlobal.getLocaleText("crm.ADDTEXT"):WtfGlobal.getLocaleText("crm.EDITTEXT"),//"Add":"Edit",
                id: "createUserButton",
                scope : this,
                handler:function(){
                    if(this.createuserForm.form.isValid()){
                        Wtf.Ajax.requestEx({

                            url: Wtf.req.springBase+"Lead/action/newLead.do",
                            params: ({
                                type:"newLead",
                                userid:Wtf.getCmp('tempContIdField').getValue(),
                                username:Wtf.getCmp('tempNameField').getValue(),
                                emailid:Wtf.getCmp('tempEmailField').getValue(),
                                company:Wtf.getCmp('tempCompanyField').getValue(),
                                address: Wtf.getCmp('tempAddField').getValue(),
                                contactno:Wtf.getCmp('tempPhoneField').getValue()
                            }),
                            method: 'POST'
                        },
                        this,
                        function(result, req){
                            if(result!=null && result != ""){
                                WtfComMsgBox(462, 0);
                            }
                            this.listds.remove(record);
                            this.EditorStore.reload();
                            this.addExtContactWindow.close();
                         },function(){
                            this.addExtContactWindow.close();
                        });
                    }
                }
            },{
                text : WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                id:'cancelCreateUserButton',
                scope : this,
                handler : function(){
                    this.addExtContactWindow.close();
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
                    labelWidth: 140,
                    bodyStyle : 'margin-top:20px;margin-left:35px;font-size:10px;',
                    defaults: {
                        width: 200
                    },
                    defaultType: 'textfield',
                    items: [{
                        fieldLabel: WtfGlobal.getLocaleText("crm.mydocuments.header.name")+'*',//'Name* ',
                        id:'tempNameField',
                        name:'name',
                        validator:WtfGlobal.validateUserName,
                        allowBlank:false
                    },{
                        fieldLabel:WtfGlobal.getLocaleText("crm.EMAILIDFIELD")+'*',// 'Email Id* ',
                        id:'tempEmailField',
                        name: 'emailid',
                        validator: WtfGlobal.validateEmail,
                        allowBlank:false,
                        renderer: WtfGlobal.renderEmailTo
                    },{
                        fieldLabel: WtfGlobal.getLocaleText("crm.lead.defaultheader.phone")+'*',// 'Phone* ',
                        allowBlank:false,
                        id: "tempPhoneField",
                        name: 'phone'
                    },{
                        fieldLabel: WtfGlobal.getLocaleText("crm.masterconfig.leadtypefieldset.Company")+'*',//'Company* ',
                        id:'tempCompanyField',
                        name:'company',
                        //validator:WtfGlobal.validateUserName,
                        allowBlank:false
                    },{
                        xtype:"textarea",
                        fieldLabel:WtfGlobal.getLocaleText("crm.lead.defaultheader.address"),// 'Address ',
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
        this.addExtContactWindow.show();
        if(record!=null){
            Wtf.getCmp('tempNameField').setValue(record.json.username);
            Wtf.getCmp('tempEmailField').setValue(record.json.emailid);
            Wtf.getCmp('tempCompanyField').setValue(record.json.company);
            Wtf.getCmp('tempPhoneField').setValue(record.json.contactno);
            Wtf.getCmp('tempAddField').setValue(record.json.address);
            Wtf.getCmp('tempContIdField').setValue(record.json.userid);
        }
        }
});
