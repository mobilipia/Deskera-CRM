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
Wtf.account.TransactionListPanel=function(config){
    this.label = config.label;
    this.isOrder=config.isOrder;
    this.tabtype = config.tabtype;
    this.mainTab = config.mainTab;
    this.moduleid = config.moduleid;
    this.modulename = config.modulename;
    this.expandRec = Wtf.data.Record.create ([
        {name:'productname'},
        {name:'productdetail'},
        {name:'prdiscount'},
        {name:'amount'},
        {name:'productid'},
        {name:'accountid'},
        {name:'accountname'},
        {name:'quantity'},
        {name:'unitname'},
        {name:'rate'},
        {name:'rateinbase'},
        {name:'externalcurrencyrate'},
        {name:'prtaxpercent'},
        {name:'orderrate'},
        {name:'desc', convert:WtfGlobal.shortString},
        {name:'productmoved'},
        {name:'currencysymbol'},
        {name:'currencyrate'},
        {name: 'type'},
        {name: 'pid'},
        {name:'carryin'},
        {name:'prtaxid'},
        {name:'taxamount'}
    ]);

    if(this.tabtype==1)
        this.expandStoreUrl = Wtf.req.springBase+"common/quotation/getQuotationItems.do";
    this.expandStore = new Wtf.data.Store({
        url:this.expandStoreUrl,
        baseParams:{
        },
        reader: new Wtf.data.KwlJsonReader({
            root: "data"
        },this.expandRec)
    });
    
    this.GridRec = Wtf.data.Record.create ([
        {name:'billid'},
        {name:'customername'},
        {name:'customer'},
        {name:'journalentryid'},
        {name:'entryno'},
        {name:'billto'},
        {name:'discount'},
        {name:'currencysymbol'},
        {name:'orderamount'},
        {name:'isexpenseinv'},
        {name:'currencyid'},
        {name:'shipto'},
        {name:'mode'},
        {name:'billno'},
        {name:'date', type:'date',dateFormat:'time'},
        {name:'duedate', type:'date',dateFormat:'time'},
        {name:'shipdate', type:'date',dateFormat:'time'},
        {name:'personname'},
        {name:'personemail'},
        {name:'personid'},
        {name:'shipping'},
        {name:'othercharges'},
        {name:'amount'},
        {name:'amountdue'},
        {name:'termdays'},
        {name:'termname'},
        {name:'incash'},
        {name:'taxamount'},
        {name:'taxid'},
        {name:'orderamountwithTax'},
        {name:'taxincluded',type:'boolean'},
        {name:'taxname'},
        {name:'deleted'},
        {name:'amountinbase'},
        {name:'memo'},
        {name:'externalcurrencyrate'},
        {name:'ispercentdiscount'},
        {name:'discountval'},
        {name:'crdraccid'},
        {name:'creditDays'},
        {name:'isRepeated'},
        {name:'porefno'},
        {name:'costcenterid'},
        {name:'costcenterName'},
        {name:'interval'},
        {name:'intervalType'},
        {name:'startDate', type:'date'},
        {name:'nextDate', type:'date'},
        {name:'expireDate', type:'date'},
        {name:'repeateid'},
        {name:'status'},
        {name:'templateid'},
        {name:'templatename'},
        {name:'discountamount'},
        {name:'taxamount'},
        {name:'discount'},
        {name:'perdiscount'},
        {name:'istaxable'},
        {name:'isproducttax'},
        {name:'subtotal'},
        {name:'dataItem'}
    ]);
    this.StoreUrl = Wtf.req.springBase+"common/quotation/getQuotationList.do";
    this.Store = new Wtf.data.Store({
        url:this.StoreUrl,
//        url: Wtf.req.account+this.businessPerson+'Manager.jsp',
        baseParams:{
            moduleid : this.moduleid
//            mode:this.isOrder?(this.isCustBill?52:42):(this.isCustBill?16:12),
//            costCenterId: this.costCenterId,
//            deleted:false,
//            nondeleted:true,
//            cashonly:false,
            
//            creditonly:false
        },
        reader: new Wtf.data.KwlJsonReader({
            root: "data",
            totalProperty:'count'
        },this.GridRec)
    });
    this.expander = new Wtf.grid.RowExpander({hiddenColumns:5});
    this.sm = new Wtf.grid.CheckboxSelectionModel();
        this.grid = new Wtf.grid.GridPanel({
        stripeRows :true,
        store:this.Store,
        id:"gridmsg"+config.helpmodeid+config.id,
        border:false,
        sm:this.sm,
        tbar: this.tbar2,
        layout:'fit',
        loadMask:true,
        plugins: this.expander,
        viewConfig:{forceFit:true,emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.quotaion.mtytxt"))},//"No quotations have been created till now. Click on \"Add\" to begin.")},
        forceFit:true,
        columns:[this.expander,this.sm,{
            hidden:true,
            dataIndex:'billid'
        },{
            header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.customer"),//"Customer",
            dataIndex:'customername',
            pdfwidth:75/*,
            renderer:config.isOrder?"":WtfGlobal.linkDeletedRenderer*/
        },{
            header:WtfGlobal.getLocaleText({key:"crm.quotaion.grid.header.quotno",params:[this.label]}),//this.label+" No",
            dataIndex:'billno',
            pdfwidth:75/*,
            renderer:config.isOrder?"":WtfGlobal.linkDeletedRenderer*/
        }/*,{
            header:"Journal Entry No",
            dataIndex:'entryno',
            hidden:this.isOrder,
            pdfwidth:75/*,
            renderer:WtfGlobal.linkDeletedRenderer
        },{
            header:"Vendor Invoice No",
            dataIndex:'this.isOrder?'orderamount':vendorinvoice',
            hidden:this.isOrder,
            pdfwidth:75
        }*/,{
            header:WtfGlobal.getLocaleText({key:"crm.quotaion.grid.header.quotdate",params:[this.label]}),//this.label+" Date",
            dataIndex:'date',
            align:'center',
            pdfwidth:80,
            renderer:WtfGlobal.onlyDateRendererTZ
        },{
            header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.duedate"),//"Due Date",
            dataIndex:'duedate',
            align:'center',
            hidden:this.tabtype < 2 ? true : false,
            pdfwidth:80/*,
            renderer:WtfGlobal.onlyDateDeletedRenderer*/
        },{
            dataIndex:'shipdate',
            hidden:true
        }/*,{
            header:this.businessPerson,
            pdfwidth:75,
//            renderer:WtfGlobal.deletedRenderer,
            dataIndex:'personname'
        }*/,{
            header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.subtotal"),//"Sub Total",
            dataIndex:'subtotal',
            align:'right',
            pdfwidth:75,
            renderer:WtfGlobal.currencyRenderer
           // hidden:this.isOrder
        },{
            header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.discountamt"),//"Discount Amount",
            dataIndex:'discountamount',
            align:'right',
            pdfwidth:75,
            renderer:WtfGlobal.currencyRenderer
           // hidden:this.isOrder
        },{
            header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.taxname"),//"Tax Name",
            dataIndex:'taxname',
            pdfwidth:75,
            renderer:WtfGlobal.deletedRenderer
           // hidden:!this.isOrder
        },{
            header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.taxamount"),//"Tax Amount",
            dataIndex:'taxamount',
            align:'right',
            pdfwidth:75,
            renderer:WtfGlobal.currencyRenderer
        },{
            header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.totalamt"),//"Total Amount",
            align:'right',
            dataIndex:(this.tabtype < 2?'amount':'orderamountwithTax'),
            pdfwidth:75,
            renderer:WtfGlobal.currencyRenderer
        }/*,{
            header:"Total Amount (In Home Currency)",
            align:'right',
            hidden:this.isOrder,
            dataIndex:'amountinbase',
            pdfwidth:75,
            renderer:WtfGlobal.currencyDeletedRenderer
        }*/,{
            header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.amtdue"),//"Amount Due",
            dataIndex:'amountdue',
            align:'right',
            hidden:this.isOrder,
            pdfwidth:75,
            renderer:(this.isOrder?WtfGlobal.currencyRendererDeletedSymbol:WtfGlobal.withoutRateCurrencyDeletedSymbol)
        },{
            header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.memo"),//"Memo",
            dataIndex:'memo',
            renderer:function(value){
                var res = "<span class='gridRow' style='width:200px;'  wtf:qtip='"+value+"'>"+Wtf.util.Format.ellipsis(value,20)+"</span>";
                return res;
            },
            pdfwidth:100
        },{
            header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.template"),//"Template",
            dataIndex:'templatename',
            pdfwidth:75/*,
            renderer:config.isOrder?"":WtfGlobal.linkDeletedRenderer*/
        },{
            header:WtfGlobal.getLocaleText("crm.case.defaultheader.status"),//"Status",
            dataIndex:'status',
            pdfwidth:75/*,
            renderer:config.isOrder?"":WtfGlobal.linkDeletedRenderer*/
        }
//        ,{
//            header:"Status",
//            dataIndex:'status',
//             hidden:!this.isOrder,
//             renderer:WtfGlobal.deletedRenderer,
//            pdfwidth:100
////         },{
////            header:"Expense Type",
////            dataIndex:'isexpenseinv',
////            hidden:this.isOrder,
////            pdfwidth:100
//        }
        ]
    });

    var btnArr=[];
    btnArr.push(this.addBttn=new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//'Add',
        tooltip :(this.isOrder)?WtfGlobal.getLocaleText("crm.quotation.createquotmsgttip"):WtfGlobal.getLocaleText("crm.quotation.editinvoicettip"),//'Allows you to create Quotation.':'Allows you to edit Invoice.',
        id: 'btnAdd' + this.id,
        scope: this,
        handler : this.addTransaction,
        iconCls :getTabIconCls(Wtf.etype.add)
    }));
//    this.addBttn.on('click',this.addTransaction,this);

    btnArr.push(this.editBttn=new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.EDITTEXT"),//'Edit',
        tooltip :(this.isOrder)? WtfGlobal.getLocaleText("crm.quotation.editorderttip"):WtfGlobal.getLocaleText("crm.quotation.editinvoicettip"),
        id: 'btnEdit' + this.id,
        scope: this,
        handler : this.editTransaction,
        iconCls :getTabIconCls(Wtf.etype.edit),
        disabled :true
    }));
//    this.editBttn.on('click',this.isOrder?this.editOrderTransaction:this.editTransaction.createDelegate(this,[false]),this);

    btnArr.push(this.deleteTrans=new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText({key:"crm.quotation.grid.delbtn",params:[this.label]}),//'Delete '+this.label,
        scope: this,
        //hidden:config.isOrder,
        tooltip:{
            text:WtfGlobal.getLocaleText({key:"crm.quotation.delmsgtoselect",params:[this.label]}),//"Select a "+this.label+" to delete.",
            dtext:WtfGlobal.getLocaleText({key:"crm.quotation.delmsgtoselect",params:[this.label]}),//"Select a "+this.label+" to delete.",
            etext:WtfGlobal.getLocaleText({key:"crm.quotation.delselectedmsg",params:[this.label]})//"Delete selected "+this.label+" details."
            },
        iconCls : getTabIconCls(Wtf.etype.delet),
        disabled :true,
        handler:this.handleDelete.createDelegate(this)
    }))

     btnArr.push(this.email=new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("crm.contact.defaultheader.email"),//"Email",
        tooltip : WtfGlobal.getLocaleText("crm.quotation.exportbtn.ttip"),//"Click on the button to send mail with quotation to the selected customer",
        scope: this,
//        hidden:true,
        iconCls : "pwnd pmsgicon",
        disabled : true,
        handler : this.sendMail
    }));

    btnArr.push(this.exportBtn=new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("crm.editor.exportBTN"),//"Export",
        tooltip : WtfGlobal.getLocaleText("crm.quotation.exportbtn.quotdetails"),//"Export Quotation details in PDF format",
        scope: this,
        disabled : true,
        iconCls : "pwnd exporticon",
        handler : this.exportPdf
    }));
    
    this.quickPanelSearch = new Wtf.KWLTagSearch({
        emptyText:WtfGlobal.getLocaleText({key:"crm.quotation.quicksearch.mtytxt",params:[this.label]}),//'Quick Search by '+this.label+' No.',
        width: 200,
  //      id:"quickSearch"+config.helpmodeid+config.id,
        field: 'billno',
        Store:this.Store
    })

    this.resetBttn=new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.RESETBUTTON"),//'Reset',
        tooltip :WtfGlobal.getLocaleText("crm.quotation.reset.ttip"),//'Allows you to add a new search term by clearing existing search terms.',
        id: 'btnRec' + this.id,
        scope: this,
        iconCls :getTabIconCls(Wtf.etype.refreshbutton),
        disabled :false
    });
    this.resetBttn.on('click',this.handleResetClick,this);
    
    this.tbar1 = new Array();
    this.tbar1.push(this.quickPanelSearch, this.resetBttn, btnArr);
    
    Wtf.apply(this,{
        border:false,
        layout : "fit",
        tbar: this.tbar1,//this.quickPanelSearch,
//            this.resetBttn,btnArr,'->',(config.isCustomer && !config.isOrder)?'View':'',(!config.isOrder)?this.typeEditor:'',getHelpButton(this,config.helpmodeid)],
        items:[this.grid],
        bbar: this.pagingToolbar = new Wtf.PagingSearchToolbar({
            pageSize: 15,
            id: "pagingtoolbar" + this.id,
            store: this.Store,
            searchField: this.quickPanelSearch,
            displayInfo: true,
            emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//No record to display,
            plugins: this.pP = new Wtf.common.pPageSize({id : "pPageSize_"+this.id})
        })
    });
    this.loadParmStore();
    this.expandStore.on('load',this.fillExpanderBody,this);
    this.expander.on("expand",this.onRowexpand,this);
    this.sm.on("selectionchange",this.enableDisableButtons.createDelegate(this),this);
    Wtf.account.TransactionListPanel.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.account.TransactionListPanel,Wtf.Panel,{
    loadParmStore:function(){
//        this.typeEditor.setValue(4);
        this.Store.on('load',this.expandRow, this);
//        if(this.invID==null)
            this.Store.load({params:{start:0,limit:15}});
        this.Store.on('datachanged', function() {
            if(this.invID==null){
                    var p = this.pP.combo.value;
                    this.quickPanelSearch.setPage(p);
            }
        }, this);
//        WtfComMsgBox(29,4,true);
    },
    loadStore:function(){
       this.Store.load({
           params : {
               start : 0,
               limit : this.pP.combo.value,
               ss : this.quickPanelSearch.getValue()
           }
       });
       this.Store.on('load',this.storeloaded,this);
    },
    storeloaded:function(store){
  //      this.hideLoading();
    	 var hd = Wtf.fly(this.grid.getView().innerHd).child('.x-grid3-hd-checker-on');
    	    if(hd) {
    	      hd.removeClass('x-grid3-hd-checker-on');
    	    }

        this.quickPanelSearch.StorageChanged(store);
    },
    expandRow:function(){
//        if(this.Store.getCount()==0){
//            if(this.exportButton)this.exportButton.disable();
//            if(this.printButton)this.printButton.disable();
//            var selTypeVal = this.typeEditor.getValue();
//            var emptyTxt = "";
//            if(selTypeVal == 3) {//deleted
//                emptyTxt = this.deletedRecordsEmptyTxt;
//            } else if(selTypeVal == 0 || selTypeVal == 4) {//All or Exclude deleted
//                emptyTxt = this.emptytext1+(this.isOrder?"":"<br>"+this.emptytext2);
//            } else if(selTypeVal == 1) {//Cash Sales
//                emptyTxt = this.isOrder?"":"<br>"+this.emptytext2;
//            } else if(selTypeVal == 2) {//Invoice
//                emptyTxt = this.emptytext1;
//            }
//            this.grid.getView().emptyText=emptyTxt;
//            this.grid.getView().refresh();
//        }else{
//            if(this.exportButton)this.exportButton.enable();
//            if(this.printButton)this.printButton.enable();
//        }
//        this.Store.filter('billid',this.invID);
        if(this.exponly)
            this.expander.toggleRow(0);
    },
    onRowexpand:function(scope, record, body){
        this.expanderBody=body;
        this.isexpenseinv=!this.isCustomer&&record.data.isexpenseinv;
        this.expandStore.load({params:{bills:record.data.billid,isexpenseinv:(!this.isCustomer&&record.data.isexpenseinv)}});
    },
    
    handleResetClick:function(){
        if(this.quickPanelSearch.getValue()){
            this.quickPanelSearch.reset();
            this.loadStore();
            this.Store.on('load',this.storeloaded,this);
        }
    },
    
    fillExpanderBody:function(){
        var disHtml = "";
        var arr=[];
        if(this.isexpenseinv){//for vendor expense invoice[PS]
            arr=['Account Name' ,'Amount','Discount','Tax Percent','Total Amount','                  '];//(this.isCustBill?'':'Remark'),
            var header = "<span class='gridHeader'>Account List</span>";
            header += "<span class='gridNo' style='font-weight:bold;'>S.No.</span>";
            for(var i=0;i<arr.length;i++){
                header += "<span class='headerRow'>" + arr[i] + "</span>";
            }
            header += "<span class='gridLine'></span>";
            for(i=0;i<this.expandStore.getCount();i++){
                var rec=this.expandStore.getAt(i);
                var accountname= rec.data['accountname'];
                header += "<span class='gridNo'>"+(i+1)+".</span>";
                header += "<span class='gridRow'  wtf:qtip='"+accountname+"'>"+Wtf.util.Format.ellipsis(accountname,15)+"</span>";
                header += "<span class='gridRow'>"+WtfGlobal.addCurrencySymbolOnly(rec.data.rate,rec.data['currencysymbol'],[true])+"</span>";
                header += "<span class='gridRow'>"+rec.data.prdiscount+"% "+"&nbsp;</span>";
                header += "<span class='gridRow'>"+rec.data.prtaxpercent+"% "+"&nbsp;</span>";
                var amount=rec.data.rate-(rec.data.rate*rec.data.prdiscount)/100;
                amount+=(amount*rec.data.prtaxpercent/100);
                header += "<span class='gridRow'>"+WtfGlobal.addCurrencySymbolOnly(amount,rec.data['currencysymbol'],[true])+"</span>";
                header +="<br>";
            }
            disHtml += "<div class='expanderContainer' style='width:100%'>" + header + "</div>";
        }else{
            arr=[/*(this.isCustBill?'':'Product ID'),*/(this.isCustBill?WtfGlobal.getLocaleText("crm.quotation.details.productdetails"):WtfGlobal.getLocaleText("crm.product.defaultheader.productname")),/*(this.isCustBill?'':'Product Type'),*/WtfGlobal.getLocaleText("crm.quotation.grid.quantity"),WtfGlobal.getLocaleText("crm.product.defaultheader.unitprice"),/*this.isOrder?'':*/WtfGlobal.getLocaleText("crm.quotaion.grid.header.subtotal"),WtfGlobal.getLocaleText("crm.product.defaultheader.unitprice"),WtfGlobal.getLocaleText("crm.quotaion.grid.header.discountamt"),WtfGlobal.getLocaleText("crm.quotaion.grid.header.taxamount"),WtfGlobal.getLocaleText("crm.report.pipelinereport.amount"),"                  "];//(this.isCustBill?'':'Remark'),
            header = "<span class='gridHeader'>"+WtfGlobal.getLocaleText("crm.quotation.productlist")+"</span>";
            header += "<span class='gridNo' style='font-weight:bold;'>"+WtfGlobal.getLocaleText("crm.srno")+"</span>";
            for(i=0;i<arr.length;i++){
                header += "<span class='headerRow'>" + arr[i] + "</span>";
            }
            header += "<span class='gridLine'></span>";
            for(i=0;i<this.expandStore.getCount();i++){
                rec=this.expandStore.getAt(i);
                var productname=this.isCustBill?rec.data['productdetail']: rec.data['productname'];
                header += "<span class='gridNo'>"+(i+1)+".</span>";
//                if(!this.isCustBill)
//                    header += "<span class='gridRow'>"+rec.data['pid']+"&nbsp;</span>";
                header += "<span class='gridRow'  wtf:qtip='"+productname+"'>"+Wtf.util.Format.ellipsis(productname,15)+"</span>";
//                if(!this.isCustBill)
//                    header += "<span class='gridRow'>"+rec.data['type']+"</span>";

                header += "<span class='gridRow'>"+rec.data['quantity']+" "+rec.data['unitname']+"</span>";
                var rate=this.isOrder&&!this.isCustBill?rec.data.orderrate:rec.data.rate;
                header += "<span class='gridRow'>"+WtfGlobal.addCurrencySymbolOnly(rate,rec.data['currencysymbol'],[true])+"</span>";
                amount=0;
                if(this.isOrder){
                    amount=rec.data['quantity']*rate;
                    amount+=(amount*rec.data['prtaxpercent']/100);
                }else{
                    amount=rec.data['quantity']*rate-(rec.data['quantity']*rate*rec.data['prdiscount'])/100;
                    amount+=(amount*rec.data['prtaxpercent']/100);
                }
                header += "<span class='gridRow'>"+WtfGlobal.addCurrencySymbolOnly(amount,rec.data['currencysymbol'],[true])+"</span>";
                header += "<span class='gridRow'>"+rec.data['prdiscount']+"%</span>";
                header += "<span class='gridRow'>"+WtfGlobal.addCurrencySymbolOnly((rec.data['prdiscount']*amount/100),rec.data['currencysymbol'],[true])+"</span>";
                header += "<span class='gridRow'>"+WtfGlobal.addCurrencySymbolOnly(rec.data['taxamount'],rec.data['currencysymbol'],[true])+"</span>";
                header += "<span class='gridRow'>"+WtfGlobal.addCurrencySymbolOnly(rec.data['amount'],rec.data['currencysymbol'],[true])+"</span>";
                
//                if(!this.isOrder)
//                    header += "<span class='gridRow'>"+rec.data['prdiscount']+"% "+"&nbsp;</span>";
//                header += "<span class='gridRow'>"+rec.data['prtaxpercent']+"% "+"&nbsp;</span>";
               
                if(!this.isCustBill)
                    header += "<span class='gridRow'>"+rec.data['productmoved']+"</span>";
                //           if(!this.isCustBill)
                //                header += "<span class='gridRow'>"+rec.data['desc']+"&nbsp;</span>";
                header +="<br>";
            }
            disHtml += "<div class='expanderContainer' style='width:100%'>" + header + "</div>";
        }
        this.expanderBody.innerHTML = disHtml;
    },

    enableDisableButtons:function(){
        if(this.deleteTrans){this.deleteTrans.enable();}
        var arr=this.grid.getSelectionModel().getSelections();
        if(arr.length==0){
            if(this.deleteTrans){this.deleteTrans.disable();}
        }

        var rec = this.sm.getSelected();
        if(this.sm.getCount()==1){
            if(this.email)this.email.enable();
            if(this.exportBtn)this.exportBtn.enable();
            if(this.editBttn)this.editBttn.enable();
            if(this.addBttn)this.addBttn.disable();
//            if(this.copyInvBttn)this.copyInvBttn.enable();
        }else{
            if(this.email)this.email.disable();
            if(this.exportBtn)this.exportBtn.disable();
            if(this.editBttn)this.editBttn.disable();
            if(this.addBttn)this.addBttn.enable();
//            if(this.copyInvBttn)this.copyInvBttn.disable();
        }
//        if(this.operationType==Wtf.autoNum.Invoice || this.operationType==Wtf.autoNum.GoodsReceipt || this.operationType==Wtf.autoNum.BillingInvoice || this.operationType==Wtf.autoNum.BillingGoodsReceipt) {
//            if(this.paymentButton != undefined) {
//                if(this.sm.getCount()==1 && rec.data.amountdue!=0 && rec.data.incash != true && rec.data.deleted != true){
//                    this.paymentButton.enable();
//                } else {
//                    this.paymentButton.disable();
//                }
//            }
//        }
//
//        if(this.operationType==Wtf.autoNum.Invoice || this.operationType==Wtf.autoNum.BillingInvoice) {
//            if(this.RepeateInvoice != undefined) {
//                if(this.sm.getCount()==1 && rec.data.incash != true && rec.data.deleted != true){
//                    this.RepeateInvoice.enable();
//                } else {
//                    this.RepeateInvoice.disable();
//                }
//            }
//        }
    },

    sendMail:function(){
        var formrec=null;
        if(this.grid.getSelectionModel().hasSelection()==false||this.grid.getSelectionModel().getCount()>1){
                WtfComMsgBox(400,2);
                return;
        }
        
        formrec = this.grid.getSelectionModel().getSelected();
        if(formrec.data.personemail== undefined || formrec.data.personemail.length ==0) {
                WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"), WtfGlobal.getLocaleText({key:"crm.quotation.entervalemailidmsg",params:[formrec.data.customername]})],2);
                return;
        }
        if(this.doctype==1){
            callEmailWin("editwin",formrec,this.label,this.doctype, this.grid.store);
        }

    },

    handleDelete:function(){
        if(this.grid.getSelectionModel().hasSelection()==false){
            WtfComMsgBox(63,2);
            return;
        }
        var data=[];
        var arr=[];
        this.recArr = this.grid.getSelectionModel().getSelections();
        this.grid.getSelectionModel().clearSelections();
//        WtfGlobal.highLightRowColor(this.grid,this.recArr,true,0,2);
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msg.WARNINGTITLE"), WtfGlobal.getLocaleText({key:"crm.editor.generaldeletemsg",params:[this.label]}),function(btn){//"Are you sure you want to delete the selected "+this.label+"(s)?",function(btn){
        if(btn!="yes") {
            for(var i=0;i<this.recArr.length;i++){
                var ind=this.Store.indexOf(this.recArr[i])
                var num= ind%2;
//                WtfGlobal.highLightRowColor(this.grid,this.recArr[i],false,num,2,true);
            }
            return;
        }
        for(i=0;i<this.recArr.length;i++){
            arr.push(this.Store.indexOf(this.recArr[i]));
        }
        var mode=(this.isCustBill?23:15);
        if(this.isOrder){
            mode=(this.isCustBill?54:44);
        }
        data= WtfGlobal.getJSONArray(this.grid,true,arr);
        this.ajxUrl = "";
        if(this.tabtype==1) {
            this.ajxUrl = Wtf.req.springBase+"common/quotation/deleteQuotations.do";
        }
        //this.isCustBill?23:15
//        if(this.businessPerson=="Customer"){
//            this.ajxUrl = "ACCInvoiceCMN/"+(this.isCustBill?"deleteBillingInvoices":"deleteInvoice")+".do";
//            if(this.isOrder){
//                this.ajxUrl = this.isCustBill?"ACCSalesOrder/deleteBillingSalesOrders.do":"ACCSalesOrder/deleteSalesOrders.do"
//            }
//        }else if((this.businessPerson=="Vendor")){
//            this.ajxUrl = "ACCGoodsReceiptCMN/"+(this.isCustBill?"deleteBillingGoodsReceipt":"deleteGoodsReceipt")+".do";
//            if(this.isOrder){
//                this.ajxUrl = this.isCustBill?"ACCPurchaseOrder/deleteBillingPurchaseOrders.do":"ACCPurchaseOrder/deletePurchaseOrders.do"
//            }
//        }
            Wtf.Ajax.requestEx({
                url:this.ajxUrl,
//                url: Wtf.req.account+this.businessPerson+'Manager.jsp',
                params:{
                   data:data,
                    mode:mode
                }
            },this,this.genSuccessResponse,this.genFailureResponse);
        },this);
    },
    genSuccessResponse:function(response){
//         WtfComMsgBox([this.label,response.msg],response.success*2+1);
//        for(var i=0;i<this.recArr.length;i++){
//             var ind=this.Store.indexOf(this.recArr[i])
//             var num= ind%2;
//             WtfGlobal.highLightRowColor(this.grid,this.recArr[i],false,num,2,true);
//        }
        if(response.success){
            this.loadStore();
            WtfComMsgBox(64,0);
        } else {
            var msg = "";
            if(response.msg)
                msg=response.msg;
            WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),msg],2);

        }
    },
    genFailureResponse:function(response){
//         for(var i=0;i<this.recArr.length;i++){
//             var ind=this.Store.indexOf(this.recArr[i])
//             var num= ind%2;
//             WtfGlobal.highLightRowColor(this.grid,this.recArr[i],false,num,2,true);
//        }
        var msg=WtfGlobal.getLocaleText("crm.customreport.failurerespmsg");//"Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),msg],2);
    },

    addTransaction : function() {
        callQuotation(false,null,"addquotation"+this.moduleid,this.mainTab, this.moduleid, this.modulename, this.id);
    },
    editTransaction : function() {
        callQuotation(true,this.grid.getSelectionModel().getSelected(),"addquotation"+this.moduleid,this.mainTab, this.moduleid, this.modulename, this.id);
    },

    exportPdf : function() {
        if(this.grid.getSelectionModel().hasSelection()==false||this.grid.getSelectionModel().getCount()>1){
                WtfComMsgBox(400,2);
                return;
        }
        var rec = this.grid.getSelectionModel().getSelected().data;
        var ajxUrl = Wtf.req.springBase+"common/quotation/invoiceExport.do?filetype=pdf&billid="+encodeURIComponent(rec.billid)
            +"&customername="+encodeURIComponent(rec.customername)+"&address="+encodeURIComponent(rec.shipping)+"&mode=1&isinvoice=true";
        window.open(ajxUrl , "mywindow","menubar=1,resizable=1,scrollbars=1");
    }
});


function callEmailWin(winid,rec,label,doctype,store){
    rec=(rec==undefined?"":rec);
    winid=(winid==null?"editwin":winid);
    var panel = Wtf.getCmp(winid);
    if(!panel){
//        new Wtf.MailWin({
       new Wtf.account.MailWindow({
            id:winid,
            closable: true,
            rec:rec,
//            isinvoice:isinvoice,
            doctype:doctype,
            label:label,
            modal: true,
            iconCls :getTabIconCls(Wtf.etype.favwinIcon),
            width: 700,
            store : store,
            height: (Wtf.isIE?595:557),
            resizable: false,
//            layout: 'border',
            buttonAlign: 'right',
            renderTo: document.body
        }).show();
    }
}
