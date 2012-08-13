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
Wtf.form.ExtFnComboBox=function(config){
    Wtf.form.ExtFnComboBox.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.form.ExtFnComboBox,Wtf.form.FnComboBox,{

    initComponent:function(config){
        Wtf.form.ExtFnComboBox.superclass.initComponent.call(this, config);
        var extrafield='';
        var length=this.extraFields.length;
        for (var i=0;i<length;i++)
            extrafield+='<td width="'+100/(length+1)+'%"td>{'+this.extraFields[i]+'}</td>';
        this.tpl=new Wtf.XTemplate('<tpl for="."><div class="x-combo-list-item"><table width="100%"><tr><td width="'+100/(length+1)+'%">{[this.getDots(values.level)]}{'+this.displayField+'}</td>'+extrafield+'</tr></table></div></tpl>',{
            getDots:function(val){
                var str="";
                for(var i=0;i<val;i++)
                    str+="....";
                return str;
            }
        })
    }
}); 

Wtf.account.ProductDetailsGrid=function(config){
    this.isCustomer=config.isCustomer;
    this.currencyid=config.currencyid;
    this.productID=null;
    this.id=config.id;
    this.isOrder=config.isOrder;
    this.record=config.record;
    this.billDate=new Date();
    this.dateChange=false;
    this.pronamearr=[];
    this.fromPO=config.fromPO;
    this.readOnly=config.readOnly;
    this.copyInv=config.copyInv; 
    this.editTransaction=config.editTransaction;
    this.noteTemp=config.noteTemp;
    this.fromOrder=config.fromOrder;
    if(config.isNote!=undefined)
        this.isNote=config.isNote;
    else
        this.isNote=false;
    this.isCN=config.isCN;
    this.createStore();
    this.createComboEditor();
    this.createColumnModel();
    
    Wtf.account.ProductDetailsGrid.superclass.constructor.call(this,config);
    this.addEvents({
        'datachanged':true,
        'pricestoreload':true
    });
}
Wtf.extend(Wtf.account.ProductDetailsGrid,Wtf.grid.EditorGridPanel,{
    clicksToEdit:1,
    stripeRows :true,
    rate:1,
    symbol:null,
    layout:'fit',
    viewConfig:{forceFit:true},
    forceFit:true,
    loadMask:true,
    onRender:function(config){
         Wtf.account.ProductDetailsGrid.superclass.onRender.call(this,config);
         this.isValidEdit = true;
         //this.on('render',this.onGridRender,this);
         this.on('afteredit',this.updateRow,this);
         this.on('validateedit',this.checkRow,this);
         this.on('rowclick',this.handleRowClick,this);
         this.on('beforeedit',function(e){
             if(!this.isValidEdit){ // Fixed Bug[13888]: Overlaping text box on validation alert messages. [on TAB key navigation]
                 e.cancel= true;
                 this.isValidEdit = true;
             }
             if(e.field == "rate"){	// rate editable for product type "Service" 
           		 var beforeEditRecord=Wtf.productStore.getAt(Wtf.productStore.find('productid',e.record.data.productid));
            	 if(beforeEditRecord == undefined || beforeEditRecord == null){
            		 e.cancel = true;
            	 }
//                 else{
//                    if(beforeEditRecord.data.producttype != Wtf.producttype.service){
//                        e.cancel = true;
//                    }
//            	 }
             }
         },this);
     },
     
     onGridRender :function() {
    	 var curIndexCnt = 0;
         this.store.removeAll();
    	 this.addBlankRow();
    	 if(this.editTransaction && this.record.data.dataItem) {
    		 var productDataItem = this.record.data.dataItem;
    		 // on product load
    		 if(productDataItem.data && productDataItem.data.length>0) {
    			 var dataItems = productDataItem.data;
    			 for(var cnt=0;cnt<dataItems.length;cnt++) {
    				 var newrec = this.store.getAt(curIndexCnt);
    				 newrec.set('productid',dataItems[cnt].productid);
    				 newrec.set('desc', dataItems[cnt].description);
    				 newrec.set('quantity', dataItems[cnt].quantity);
    				 newrec.set('rate', dataItems[cnt].orderrate);
    				 newrec.set('amount', dataItems[cnt].amount);
    				 newrec.set('prdiscount', dataItems[cnt].prdiscount);
    				 newrec.set('prtaxid', dataItems[cnt].prtaxid);
    				 newrec.set('taxamount', dataItems[cnt].taxamount);
    				 newrec.set('discountamount', (dataItems[cnt].quantity*dataItems[cnt].orderrate*dataItems[cnt].prdiscount)/100);
    				 newrec.set('isNewRecord', "");
    				 newrec.set('billid', this.record.data.billid);
    				 this.addBlankRow();
    				 ++curIndexCnt;
    			 }	
    		 }
    		 this.store.commitChanges();
    	 }
     },
     createStore:function(){
         chkproductStoreload();
//         this.priceRec = Wtf.data.Record.create ([
//            {name:'productid', mapping : "id"},
//            {name:'productname',mapping : "name"},
//            {name:'desc'},
//            {name:'uomid'},
//            {name:'uomname'},
//            {name:'parentid'},
//            {name:'parentname'},
//            {name:'purchaseaccountid'},
//            {name:'salesaccountid'},
//            {name:'purchaseretaccountid'},
//            {name:'salespricedatewise',mapping : "unitprice"},
//            {name:'purchasepricedatewise'},
//            {name:'salesretaccountid'},
//            {name:'reorderquantity'},
//            {name:'pricedatewise'},
//            {name:'quantity'},
//            {name:'reorderlevel'},
//            {name:'leadtime'},
//            {name:'purchaseprice'},
//            {name:'saleprice'},
//            {name: 'leaf'},
//            {name: 'type'},
//            {name:'prtaxid'},
//            {name:'taxamount'},
//            {name:'prtaxpercent'},
//            {name:'prtaxname'},
//        //        {name: 'currencysymbol'},
//        //        {name: 'currencyrate'},
//            {name: 'level'},
//            {name: 'initialquantity',mapping:'initialquantity'},
//            {name: 'initialprice'},
//            {name: 'producttype'}
//        ]);

//        this.priceStore = new Wtf.data.Store({
//        //        url:Wtf.req.account+'CompanyManager.jsp',
//            url: Wtf.req.springBase+'Product/action/getProductname.do',
//            baseParams:{mode:22
////            	loadInventory:this.isCustomer
//                },
//            reader: new Wtf.data.KwlJsonReader({
//                root: "data"
//            },this.priceRec)
//        });
        
//        this.productcomboReader = new Wtf.data.Record.create([
//        {name:'productid',mapping:'id'},
//        {name:'desc'},
//        {name:'productname',mapping:'name'},
//        {
//            name: 'salespricedatewise',
//            mapping:'unitprice',
//            type: 'string'
//        },
//        {
//            name: 'hasAccess'
//        }
//        ]);
//        this.productComboStore = new Wtf.data.Store({
//                    url: Wtf.req.springBase+'Product/action/getProductname.do',
//                    reader: new Wtf.data.KwlJsonReader({
//                        root:'data'
//                    }, this.productcomboReader),
//                    /*baseParams:{
//                        common:'2'
//                    },*/
//                    autoLoad:false
//        });
//        Wtf.productStore = new Wtf.data.Store({
//            //        url:Wtf.req.account+'CompanyManager.jsp',
//                url: Wtf.req.springBase+'Product/action/getProductname.do',
//                /*baseParams:{
//                	loadInventory:this.isCustomer
//                    },*/
//                reader: new Wtf.data.KwlJsonReader({
//                    root: "data"
//                },this.priceRec)
//            });
//        Wtf.productStore.load();
        
//        this.priceStore.on('load',this.setGridProductValues,this);
//       this.priceStore.load({params:{transactiondate:WtfGlobal.convertToGenericDate(this.billDate)}});
        this.storeRec = Wtf.data.Record.create([
            {name:'rowid'},
            {name:'productname'},
            {name:'billid'},
            {name:'billno'},
            {name:'productid'},
            {name:'desc'},
            {name:'quantity'},
            {name:'copyquantity',mapping:'quantity'},
            {name:'rate'},
            {name:'rateinbase'},
            {name:'discamount'},
            {name:'discount'},
            {name:'prdiscount'},
            {name:'prtaxid'},
            {name:'prtaxname'},
            {name:'prtaxpercent'},
            {name:'taxamount'},
            {name:'amount'},
            {name:'amountwithtax'},
            {name:'taxpercent'},
            {name:'remark'},
            {name:'transectionno'},
            {name:'remquantity'},
            {name:'remainingquantity'},
            {name:'oldcurrencyrate'},
            {name: 'currencysymbol'},
            {name: 'currencyrate'},
            {name: 'externalcurrencyrate'},
            {name:'orignalamount'},
            {name:'typeid'},
            {name:'isNewRecord'},
            {name:'producttype'},
            {name:'discountamount'}
        ]);
        var url=Wtf.req.account+((this.fromOrder||this.readOnly)?((this.isCustomer)?'CustomerManager.jsp':'VendorManager.jsp'):((this.isCN)?'CustomerManager.jsp':'VendorManager.jsp'));
        if(this.fromOrder)
           url=Wtf.req.account+(this.isCustomer?'CustomerManager.jsp':'VendorManager.jsp');
        this.store = new Wtf.data.Store({
            url:url,
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            },this.storeRec)
        });
        this.store.on('load',this.loadPOProduct,this);
 //       chkProductPriceload();
    },  
    createComboEditor:function(){
//        this.poProductRec = Wtf.data.Record.create ([
//            {name:'productid'},
//            {name:'quantity'},
//            {name:'prtaxid'}
//        ]);
//        this.poProductStore = new Wtf.data.Store({
//            //url:Wtf.req.account+(this.isCustomer?'CustomerManager.jsp':'VendorManager.jsp'),
//            url:this.isCustomer?'ACCSalesOrderCMN/getSalesOrderRows.do':'ACCPurchaseOrderCMN/getPurchaseOrderRows.do',
//            reader: new Wtf.data.KwlJsonReader({
//                root: "data"
//            },this.poProductRec)
//        });
//        chkproductStoreload();
        this.productEditor=new Wtf.form.ComboBox({
            name:'productname',
            store:Wtf.productStore,       //Wtf.productStore Previously, now changed bcos of addition of Inventory Non sale product type
            typeAhead: true,
            selectOnFocus:true,
            valueField:'productid',
            displayField:'productname',
            extraFields:['pid','type'],
//            listWidth:400,
            mode : 'local',
//            value : this.editTransaction ? this.record.data.dataItem.data[0].productid : '',
            //editable:false,
            scope:this,
            triggerAction : 'all',
//            hirarchical:true,
           // addNewFn:this.openProductWindow.createDelegate(this),
            forceSelection:true
        });
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.product, Wtf.Perm.product.edit))
//            this.productEditor.addNewFn=this.openProductWindow.createDelegate(this);

//        Wtf.productStore.on("load",this.loadPriceAfterProduct,this);
        this.typeStore = new Wtf.data.SimpleStore({
            fields: [{name:'typeid',type:'int'}, 'name'],
            data :[[0,'Normal'],[1,'Defective'],[2,'Return'],[3,'Defective Return'] ]
        });
        this.typeEditor = new Wtf.form.ComboBox({
            store: this.typeStore,
            name:'typeid',
            displayField:'name', 
            valueField:'typeid',
            mode: 'local',
            triggerAction: 'all',
            selectOnFocus:true
        });
        this.remark= new Wtf.ux.TextField({
            name:'remark'
        });
        this.transDiscount=new Wtf.form.NumberField({
            allowBlank: false,
            allowNegative: false,
            defaultValue:0
        });
        this.taxRec = new Wtf.data.Record.create([
           {name: 'prtaxid',mapping:'taxid'},
           {name: 'prtaxname',mapping:'taxname'},
           {name: 'percent',type:'float'},
           {name: 'taxcode'},
//           {name: 'accountid'},
//           {name: 'accountname'},
           {name: 'applydate', type:'date'}

        ]);
        this.taxStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            },this.taxRec),
    //        url: Wtf.req.account + 'CompanyManager.jsp',
            url : Wtf.req.springBase+"common/tax/getTax.do",
            baseParams:{
                mode:33
            }
        });
        //if(this.readOnly)
            this.taxStore.load();
            this.taxStore.on('load',this.onGridRender,this );
        this.transTax= new Wtf.form.FnComboBox({
            hiddenName:'prtaxid',
//           anchor: '70%',
            store:this.taxStore,
//            style:'overflow:hidden',
            width:70,
            valueField:'prtaxid',
        	triggerAction :'all',
            forceSelection: true,
            mode:'local',
            displayField:'prtaxname',
            addNewFn:this.addTax.createDelegate(this),
            scope:this,
            selectOnFocus:true
        });
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.tax, Wtf.Perm.tax.edit))
//            this.transTax.addNewFn=this.addTax.createDelegate(this);
        this.transQuantity=new Wtf.form.TextField({
            allowBlank: false,
            allowNegative: false,
            allowDecimals:false,
            regex:/^\d{0,10}$/,
            maxLength:10
        });
        this.editprice=new Wtf.form.NumberField({
            allowBlank: false,
            allowNegative: false,
            minValue:0.01,
            maxLength:10
        });  
    },
    addTax:function(){
         this.stopEditing();
         var p= callTax("taxwin");
         Wtf.getCmp("taxwin").on('update', function(){this.taxStore.reload();}, this);
  },
//    loadPriceAfterProduct : function(){
//        if(Wtf.getCmp(this.id)){ //Load price store if component exists
//            this.loadPriceStore();
//        } else {
//            Wtf.productStore.un("load",this.loadPriceAfterProduct,this);//Remove event handler if Not exists
//        }
//    },
//    loadPriceStore:function(val){
//        this.billDate=(val==undefined?this.billDate:val);
//        if(this.editTransaction)
//         this.priceStore.on('load',this.setGridProductValues.createDelegate(this),this)
//        this.priceStore.load({params:{transactiondate:WtfGlobal.convertToGenericDate(this.billDate)}});
//    },
//    loadPriceStoreOnly:function(val,pricestore){  //scope related issue
//        this.dateChange=true;
//        this.billDate=(val==undefined?this.billDate:val);        //if(this.editTransaction)
//        pricestore.load({params:{transactiondate:WtfGlobal.convertToGenericDate(this.billDate)}});
//    },
//    openProductWindow:function(){
//        this.stopEditing();
//        callProductWindow(false, null, "productWin");
//       // this.productStore.on('load',function(){this.productStore.})
//        Wtf.getCmp("productWin").on("update",function(obj,productid){this.productID=productid;},this);
//    },

    createColumnModel:function(){
        this.summary = new Wtf.ux.grid.GridSummary();
        this.rowno=(this.isNote)?new Wtf.grid.CheckboxSelectionModel():new Wtf.grid.RowNumberer();
        var columnArr = [this.rowno,{
            dataIndex:'rowid',
            hidelabel:true,
            hidden:true
        },{
            dataIndex:'billid',
            hidelabel:true,
            hidden:true
        },{
            header:WtfGlobal.getLocaleText("crm.opportunity.defaultheader.product"),//"Product",
            width:200,
            dataIndex:this.readOnly?'productname':'productid',
            renderer:this.readOnly?"":Wtf.comboBoxRenderer(this.productEditor),
            editor:(this.isNote||this.readOnly)?"":this.productEditor
        },{
            header:this.isCN?"Invoice No.":"Vendor Invoice No.",
            width:150,
            dataIndex:this.noteTemp?'transectionno':'billno',
            hidden:!this.isNote
        },{
             header:WtfGlobal.getLocaleText("crm.case.defaultheader.desc"),//"Description",
             dataIndex:"desc",
             hidden:this.isNote,
             width:250,
             editable:false,
             editor:(this.isNote||this.readOnly)?"":this.remark
         },{
             header:WtfGlobal.getLocaleText("crm.quotation.grid.quantity"),//"Quantity",
             dataIndex:"quantity",
             align:'right',
             width:100,
//             renderer:this.storeRenderer(Wtf.productStore,"productid","uomname"),
             editor:(this.isNote||this.readOnly)?"":this.transQuantity
         },{
             header:WtfGlobal.getLocaleText("crm.quotation.grid.remainingquantity"),//"Remaining Quantity",
             dataIndex:"remainingquantity",
             align:'right',
             hidden:!this.isNote||this.noteTemp,
             width:150,
//             renderer:this.storeRenderer(Wtf.productStore,"productid","uomname"),
             editor:(this.isNote||this.readOnly)?"":this.transQuantity
        },{
             header:"<b>"+WtfGlobal.getLocaleText("crm.quotation.grid.enterquantity")+"</b>",//"<b>Enter Quantity</b>",
             dataIndex:"remquantity",
             align:'right',
             hidden:!this.isNote||this.noteTemp,
             width:180,
//             renderer:this.storeRenderer(Wtf.productStore,"productid","uomname"),
             editor:this.readOnly?"":this.transQuantity
        },{
             header:WtfGlobal.getLocaleText("crm.product.defaultheader.unitprice"),// "Unit Price", 
             dataIndex: "rate",
             align:'right',
             width:150,
             renderer:(this.isNote||this.readOnly?WtfGlobal.withoutRateCurrencySymbol:WtfGlobal.currencyRenderer),
             editor:(this.isNote||this.readOnly)?"":this.editprice,
             editable:true		 
        },{
             header: WtfGlobal.getLocaleText("crm.quotaion.grid.header.discount")+"%",//"Discount %",
             dataIndex:"prdiscount",
             align:'right',
             width:150,
             //hidden:this.isOrder,
             renderer:function(v){return'<div class="currency">'+v+'%</div>';},
             editor:this.readOnly||this.isNote?"":this.transDiscount
         },{
             header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.tax"),// "Tax",
             dataIndex:"prtaxid",
             id:this.id+"prtaxid",
             //align:'right',
             width:200,
             hidden:!(this.readOnly),// || this.isOrder,
             renderer:Wtf.comboBoxRenderer(this.transTax),
             editor:this.transTax  //this.transTax
        },{
             header: WtfGlobal.getLocaleText("crm.quotaion.grid.header.taxamount"),//"Tax Amount",
             dataIndex:"taxamount",
             id:this.id+"taxamount",
             //align:'right',
             width:150,
            hidden:!(this.readOnly),// || !this.isOrder,
             renderer:this.setTaxAmount.createDelegate(this) 
        },{
            header: WtfGlobal.getLocaleText("crm.quotaion.grid.header.discountamt"),//"Discount Amount",
            dataIndex:"discountamount",
            id:this.id+"discountamount",
            //align:'right',
            width:150,
//           hidden:!(this.readOnly),// || !this.isOrder,
          renderer:this.setDiscountAmount.createDelegate(this) 
       },{
             header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.tax"),//"Tax",
             dataIndex:"taxpercent",
             align:'right',

             hidden:!this.isNote,
             width:200,
             renderer:function(v){return'<div class="currency">'+v+'%</div>';}
        },{
             header:WtfGlobal.getLocaleText("crm.quotaion.grid.header.producttax"),//"Product Tax",
             dataIndex:"prtaxpercent",
             align:'right',
             hidden:!this.isNote,
             width:200,
             renderer:function(v){return'<div class="currency">'+v+'%</div>';}
        },{
             header:this.isNote? WtfGlobal.getLocaleText("crm.quotaion.grid.header.originalamount"):WtfGlobal.getLocaleText("crm.quotaion.grid.header.invoiceamount"),//"Original Amount":"Invoice Amount",
             dataIndex:"orignalamount",
             align:'right',
             width:150,
             hidden:!(this.isNote||this.readOnly),
             renderer:(this.isNote||this.readOnly?WtfGlobal.withoutRateCurrencySymbol:WtfGlobal.currencyRendererSymbol)
        },{
             header:this.isNote? WtfGlobal.getLocaleText("crm.quotaion.grid.header.currentamount"):WtfGlobal.getLocaleText("crm.report.pipelinereport.amount"),//"Current Amount ":"Amount",
             dataIndex:"amount",
             hidden:this.readOnly,
             align:'right',
             width:200,
             renderer:(this.isNote||this.readOnly?WtfGlobal.withoutRateCurrencySymbol:this.calAmount.createDelegate(this))

//    },{
//             header:"Amount (Including Tax)",
//             dataIndex:"amountwithtax",
//             align:'right',
//             hidden:!this.isNote,
//             width:200,
//             renderer:WtfGlobal.withoutRateCurrencySymbol

        },{
             header:(this.readOnly)? WtfGlobal.getLocaleText("crm.report.pipelinereport.amount"):"<b>"+WtfGlobal.getLocaleText("crm.quotation.grid.enteramount")+"</b>",//"Amount":"<b>Enter Amount</b>",
             dataIndex:this.noteTemp?'discount':'discamount',
             align:'right',
             width:200,
             hidden:!this.isNote,
             renderer:WtfGlobal.withoutRateCurrencySymbol,
             editor:this.readOnly?"":new Wtf.form.NumberField({
                allowBlank: false,
                allowNegative: false
             })
        },{ 
            header:WtfGlobal.getLocaleText("crm.quotation.grid.notetype"),//"Note Type",
            width:200,
            dataIndex:'typeid',
            hidden:(!this.isNote ||this.noteTemp),
            renderer:Wtf.comboBoxRenderer(this.typeEditor),
            editor:this.readOnly?"":this.typeEditor
        }];
        if(!this.isNote && !this.readOnly || this.editTransaction) {
            columnArr.push({
                header:WtfGlobal.getLocaleText("crm.audittrail.header.action"),//"Action",
                align:'center',
                width:60,
                dataIndex:'isNewRecord',
                renderer: this.deleteRenderer.createDelegate(this)
            });
        }
        this.cm=new Wtf.grid.ColumnModel(columnArr);
    },
    deleteRenderer:function(v,m,rec){
    	if(v=='1')
    		return "";
    	return "<div class='pwnd  delete-gridrow'></div>";
//        return "";
    },
    handleRowClick:function(grid,rowindex,e){
    	var store=grid.getStore();
    	var rec=store.getAt(rowindex)
        var total=store.getCount();
       //var rec=store.data.items[0].data;
        if(e.getTarget(".delete-gridrow")){
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msg.WARNINGTITLE"), WtfGlobal.getLocaleText("crm.confirmdeletemsg"),function(btn){  //'Are you sure you want to delete?', function(btn){
                if(btn!="yes") return;

                var billid=rec.data.billid;
                var productid=rec.data.productid;
                Wtf.Ajax.requestEx({
                    url: Wtf.req.springBase+"common/quotation/deleteQuotationItems.do",
                    method: 'POST',
                    params: {
                			 billid:billid,
                			 productid:productid
                    }},this,
                    function(response, e){
                    	store.remove(store.getAt(rowindex));
                        this.ArrangeNumberer(rowindex);
                        this.fireEvent('datachanged',this)
                        if(grid.getStore().getCount()==0){
                        	this.addBlankRow();
                        }
                    });
             
            }, this);
        }
    },

    ArrangeNumberer: function(currentRow) {
        var plannerView = this.getView();
        var length = this.store.getCount();
        for (var i = currentRow; i < length; i++)
            plannerView.getCell(i, 0).firstChild.innerHTML = i + 1;
    },
    
    storeRenderer:function(store, valueField, displayField) {
        return function(value, meta, record) {
            var idx = store.find(valueField, record.data[valueField]);
            if(idx == -1)
                return value;
            var rec = store.getAt(idx);
            return value+" "+rec.data[displayField];
        }
    },

    checkRow:function(obj){
        var rec=obj.record;
        if(obj.field=="productid"){
        var billid=obj.record.data.billid;
           var productid=obj.originalValue;
           if(productid!=""){
           Wtf.Ajax.requestEx({
               url: Wtf.req.springBase+"common/quotation/deleteQuotationItems.do",
               method: 'POST',
               params: {
           			 billid:billid,
           			 productid:productid
               }},this,
               function(response, e){
                   this.fireEvent('datachanged',this)
                    this.record.store.reload();
               },
               function(response, e) {
                  
           });
           }
            var index=Wtf.productStore.findBy(function(rec){
                if(rec.data.productid==obj.value)
                    return true;
                else
                    return false;
            })
            var prorec=Wtf.productStore.getAt(index);
            index=Wtf.productStore.find('productid',obj.value)
            rec=Wtf.productStore.getAt(index);
            if(this.store.find("productid",obj.value)>=0&&obj.ckeckProduct==undefined){
                WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText({key:"crm.quotation.alertmsg.forselection",params:[rec.data['productname']]})], 2);
                obj.cancel=true;
            }else if((this.isCustomer&&rec.data["salespricedatewise"]==0)||(!this.isCustomer&&rec.data["purchasepricedatewise"]==0)){
                 /*if(!WtfGlobal.EnableDisable(Wtf.UPerm.product, Wtf.Perm.product.addprice)){//permissions
                    Wtf.Msg.confirm("Alert","Price for "+rec.data['productname']+" is not set. Do you want to set now?",
                    this.showPriceWindow.createDelegate(this,[rec, obj],true));
                }else{*/
                    WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText({key:"crm.quotation.alertmsg.pricenotset",params:[rec.data['productname']]})], 2);//"Price for <b>"+rec.data['productname']+"</b> is not set"], 2);
//                }
                obj.cancel=true;
            }/**else if(this.isCustomer&&rec.data['quantity']<obj.record.data['quantity']&&prorec.data.producttype!=Wtf.producttype.service){
                this.isValidEdit = false;
                WtfComMsgBox(["Alert",'Maximum available quantity for '+rec.data['productname']+' is '+rec.data['quantity']], 2);
                obj.cancel=true;
            }*/
        }/*else if(this.isCustomer&&obj.field=="quantity"&&obj.record.data['productid'].length>0){
            prorec=Wtf.productStore.getAt(Wtf.productStore.find('productid',obj.record.data.productid));
            if(((!this.editTransaction||this.copyInv)&&prorec.data.quantity<obj.value&&prorec.data.producttype!=Wtf.producttype.service)||(this.editTransaction&&!this.copyInv&&obj.value>obj.record.data.copyquantity&&(obj.value-obj.record.data.copyquantity)>prorec.data.quantity&&prorec.data.producttype!=Wtf.producttype.service)){
             //  alert(this.editTransaction+"a----"+this.copyInv+"b----"+obj.value+"c----"+obj.record.data.copyquantity+"d----"+prorec.data.quantity+"e----"+obj.value)
                this.isValidEdit = false;
                var quantity=prorec.data.quantity;
                if((this.editTransaction&&!this.copyInv&&obj.value>obj.record.data.copyquantity&&(obj.value-obj.record.data.copyquantity)>prorec.data.quantity&&prorec.data.producttype!=Wtf.producttype.service)){
                    quantity=prorec.data.quantity+obj.record.data.copyquantity
                }
               WtfComMsgBox(["Alert",'Maximum available quantity for '+prorec.data.productname+' is '+quantity], 2);
                obj.cancel=true;
            }
        }*/
        if(this.isNote){
            if(obj.field=="typeid"&&(obj.value==0||obj.value==1))
                rec.set('remquantity',0);

            if(obj.field=="remquantity"){
                if(rec.data['typeid']==0||rec.data['typeid']==1){
                    obj.cancel=true;
                    WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.quotation.changenotetypemsg")], 2);//'Change the Note Type first' ], 2);
                    rec.set('remquantity',0);
                }
                else{
                    rec=this.store.getAt(this.store.find('productid',obj.record.data['productid']));
                    if(rec.data['remainingquantity']<obj.value){
                        WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText({key:"crm.quotation.maxproductavailmsg",params:[rec.data['productname'],rec.data.remainingquantity]})], 2);//'Maximum available quantity for '+rec.data['productname']+ ' is '+rec.data.remainingquantity], 2);
                        obj.cancel=true;
                        rec.set('remquantity',0);
                    }
                }
            }
            if(obj.field=="typeid"){
                rec=this.store.getAt(this.store.find('productid',obj.record.data['productid']));
                if(rec.data['typeid']==0||rec.data['typeid']==1)
                    rec.set('remquantity',0);
            }
            if(obj.field=="discamount"){
                if(rec.data['orignalamount']<obj.value){
                    WtfComMsgBox(["Alert",'Remaining amount of '+rec.data['productname']+ ' for selected transaction  is '+WtfGlobal.getCurrencySymbol()+" "+(rec.data['amount'])], 2);
                    obj.cancel=true;
                    rec.set('discamount',0);
                }
            }
        }
    }, 
    addBlankRow:function(){
        var newrec = new this.storeRec({
            productname:"",
            billid:"",
            productid:"",
            desc:"",
            quantity:1,
            rate:0,
            prdiscount:0,
            prtaxname:"",
            prtaxid:"",
            taxamount:0,
            discountamount:0,
            amount:0,
            taxpercent:0,
            prtaxpercent:0,
            amountwithtax:0,
            remark:"",
            typeid:0,
            currencyrate:1,
            currencysymbol:this.symbol,
            oldcurrencyrate:1,
            isNewRecord:"1"
        });
        this.store.add(newrec);
        return newrec;
    }, 
    updateRow:function(obj){
         if(obj!=null){
             var rec=obj.record;
             rec.data['isNewRecord']='0';
             if(obj.field=="prdiscount" && obj.value >100){
                 rec=obj.record;
                        WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.WARNINGTITLE"),WtfGlobal.getLocaleText("crm.quotation.discountavailmsg")], 2);//"Discount cannot be greater than 100 "], 2);
                        rec.set("prdiscount",0);
                  }
                  if(obj.field=="productid"){
                var index=Wtf.productStore.find('productid',obj.value);
                if(index>=0){
                    rec=Wtf.productStore.getAt(index);
                    obj.record.set("desc",rec.data["desc"]);

                    obj.record.set("quantity",1);
                    if(this.isCustomer)
                        obj.record.set("rate",rec.data["salespricedatewise"]);
                    else
                        obj.record.set("rate",rec.data["purchasepricedatewise"]);
                }
            }else if(obj.field=="quantity"){
                rec=obj.record;
                if((rec.data.isNewRecord=="" || rec.data.isNewRecord==undefined) && this.fromOrder&&!(this.editTransaction||this.copyInv)) {
                    if(obj.value!=rec.data.copyquantity) {
                        //"Product Quantity entered in Invoice is different from original quantity mentioned in SO. DO you want to continue?:Product Quantity entered in Vendor Invoice is different from original quantity mentioned in PO. DO you want to continue?
                        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msg.WARNINGTITLE"),this.isCustomer? WtfGLobal.getLocaleText("crm.quotation.quantitydifferentmsgcustomer"): WtfGLobal.getLocaleText("crm.quotation.quantitydifferentmsgvendor"), function(btn){
                            if(btn!="yes") {obj.record.set(obj.field, obj.originalValue)}
                        },this)
                    }
                }
                if((obj.record.data["quantity"])==0){
                    this.store.remove(obj.record);
                }
            }else if(obj.field=="prtaxid"){
                rec=obj.record;
                var discount=rec.data.rate*rec.data.quantity*rec.data.prdiscount/100
                var val=(rec.data.quantity*rec.data.rate)-discount;
                var taxpercent=0;
               index=this.taxStore.find('prtaxid',rec.data.prtaxid);
                if(index>=0){
                    var taxrec=this.taxStore.getAt(index);
                    taxpercent=taxrec.data.percent;
                }
                var taxamount= (val*taxpercent/100);
                rec.set("taxamount",taxamount);
            }else if(obj.field=="desc"){
                rec=obj.record;
                rec.set("desc",WtfGlobal.HTMLStripper(rec.data.desc));
            }
        }
        
        this.fireEvent('datachanged',this);
        if(this.store.getCount()>0&&this.store.getAt(this.store.getCount()-1).data['productid'].length<=0)
            return;
        if(!this.isNote)
            this.addBlankRow();
    },
    calTaxAmount:function(rec){
        var discount=rec.data.rate*rec.data.quantity*rec.data.prdiscount/100
        var val=(rec.data.quantity*rec.data.rate)-discount;
        var taxpercent=0;
            var index=this.taxStore.find('prtaxid',rec.data.prtaxid);
            if(index>=0){
               var taxrec=this.taxStore.getAt(index);
                taxpercent=taxrec.data.percent;
            }
        return (val*taxpercent/100);

    },
    calDiscountAmount:function(rec){
        var discount=rec.data.rate*rec.data.quantity*rec.data.prdiscount/100
        
        return discount;

    },
    setDiscountAmount:function(v,m,rec){
        var discount= this.calDiscountAmount(rec);
        rec.set("discountamount",discount);
         return WtfGlobal.currencyRenderer(discount);//WtfGlobal.currencyRendererSymbol(taxamount,m,rec);
     },
    setTaxAmount:function(v,m,rec){
       var taxamount= this.calTaxAmount(rec);
       rec.set("taxamount",taxamount);
        if(this.isNote||this.readOnly)
             return WtfGlobal.withoutRateCurrencySymbol(taxamount,m,rec);
        return WtfGlobal.currencyRenderer(taxamount);//WtfGlobal.currencyRendererSymbol(taxamount,m,rec);
    },
    calAmount:function(v,m,rec){
        var discount=rec.data.rate*rec.data.quantity*rec.data.prdiscount/100
        var val=(rec.data.quantity*rec.data.rate)-discount;///rec.data.oldcurrencyrate
        var taxamount= this.calTaxAmount(rec);
        val+=taxamount;
        rec.set("discountamount",discount);
        rec.set("amount",val);
       if(this.isNote||this.readOnly)
             return WtfGlobal.withoutRateCurrencySymbol(val,m,rec);
       return WtfGlobal.currencyRenderer(val);//WtfGlobal.currencyRendererSymbol(val,m,rec);
    },     
    calSubtotal:function(){
        var subtotal=0;
        var count=this.store.getCount();
        for(var i=0;i<count;i++){
            var total=this.setCurrencyAmount(this.store.getAt(i).data['amount']);
            if(this.editTransaction&&!this.fromPO){
                total=total/this.store.getAt(i).data['oldcurrencyrate'];
            }
            subtotal+=total;
        }
        return subtotal;
    },
    addBlank:function(){
       this.setGridDiscValues();
        this.addBlankRow();
    },
    setGridDiscValues:function(){
        this.store.each(function(rec){
            if(!this.editTransaction)
                rec.set('prdiscount',0)
        },this);            
    },
//    setGridProductValues:function(){
//        var rate;
//       this.pronamearr=[];
//        this.store.each(function(record){
//            var index=this.priceStore.find('productid',record.data.productid);
//                if(index>=0){
//                var rec=this.priceStore.getAt(index);
//                    if(this.isCustomer){
//                       rate=(record.data.rowid!=null?WtfGlobal.currencyRendererWithoutSymbol(rec.get('salespricedatewise'),null,record):rec.get('salespricedatewise'))
//                   record.set("rate",rate);
//                   if(rate==0)
//                        this.pronamearr.push(rec.get('productname'))
//                    }else{
//                        rate=(record.data.rowid!=null?WtfGlobal.currencyRendererWithoutSymbol(rec.get('purchasepricedatewise'),null,record):rec.get('purchasepricedatewise'))
//                    record.set("rate",rate);
//                    if(rate==0)
//                        this.pronamearr.push(rec.get('productname'))
//                }
//                }
//                if(this.editTransaction){
//                   record.set('oldcurrencyrate',record.get('currencyrate'));
//                }
//                index=Wtf.productStore.find('productid',record.data.productid);
////                if(index>=0){
////                    var prorec=Wtf.productStore.getAt(index);
////                    if((this.copyInv&&prorec.data.quantity<record.data.quantity&&prorec.data.producttype!=Wtf.producttype.service)){
////                        WtfComMsgBox(["Alert",'Maximum available quantity for '+prorec.data.productname+' is '+prorec.data.quantity], 2);
////                        record.set("quantity",0);
////                    }
////                }
//            },this);
//            if( this.dateChange){//alert(this.pronamearr.length);//>0&&
//                 this.fireEvent('pricestoreload', this.pronamearr,this);}
//    },
    getProductDetails:function(){
        if(this.editTransaction && !this.isOrder){
            this.store.each(function(rec){//converting in home currency
                if(rec.data.rowid!=null){
                    var amount,rate;
                    if(this.record.data.externalcurrencyrate!=undefined&&this.record.data.externalcurrencyrate!=0){
                        amount=rec.get('amount')/this.record.data.externalcurrencyrate;
                        rate=rec.get('rate')/this.record.data.externalcurrencyrate;
                    }else{
                        amount=rec.get('amount')/rec.get('oldcurrencyrate');
                        rate=rec.get('rate')/rec.get('oldcurrencyrate');
                    }
                    rec.set('amount',amount);
                    rec.set('rate',rate);
                }
            },this);
        }
        var arr=[];
        this.store.each(function(rec){
            if(rec.data.rate!=0){
                arr.push(this.store.indexOf(rec));
            }
        })
        var jarray=WtfGlobal.getJSONArray(this,false,arr);
        //converting back in person currency
        this.store.each(function(rec){
            if(rec.data.rowid!=null && this.fromPO == false){
                var amount,rate;
                if(this.record.data.externalcurrencyrate!=undefined&&this.record.data.externalcurrencyrate!=0){
                    amount=rec.get('amount')*this.record.data.externalcurrencyrate;
                    rate=rec.get('rate')*this.record.data.externalcurrencyrate;
                }else{
                    amount=rec.get('amount')*rec.get('oldcurrencyrate');
                    rate=rec.get('rate')*rec.get('oldcurrencyrate');
                }
                rec.set('amount',amount);
                rec.set('rate',rate);
            }
        },this);
        return jarray;
    }, 
          
    getCMProductDetails:function(){
        var arr=[];
        var selModel=  this.getSelectionModel();
        var len=Wtf.productStore.getCount();
        for(var i=0;i<len;i++){
            if(selModel.isSelected(i)){
            var rec =selModel.getSelected()
            if(rec.data.typeid==2||rec.data.typeid==3)
                if(rec.data.remquantity==0){
                    WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGLobal.getLocaleText({key:"crm.quotation.enterquantitymsg",params:[rec.data.productname]})], 2);//'Please enter the quantity of product "'+rec.data.productname+'" you want to return' ], 2);
                    return "Error";
                }
            arr.push(i);
            }
            	// arr.push(i); moved to above line cos of issue no: 20258
        }
        return WtfGlobal.getJSONArray(this,true,arr);
    }, 
    loadPOProduct:function(){
      if(this.fromPO)
         this.store.each(function(rec){
              var taxamount= this.calTaxAmount(rec);
             rec.set("taxamount",taxamount);
             rec.set("prdiscount",0);
        },this);
        
    },
//    loadPOGridStore:function(rec){
//        this.store.load({params:{bills:rec.data['billid'],mode:43,closeflag:true}});
//    },
//    showPriceWindow:function(btn,text,rec, obj){
//        if(btn!="yes")return;
//        callPricelistWindow(rec,"pricewindow",!this.isCustomer,this.billDate);
//        this.priceStore.on('load',this.setPrevProduct.createDelegate(this,[rec,obj]), this);
//        Wtf.getCmp("pricewindow").on('update',function(){this.loadPriceStore()},this);
//    },

//    setPrevProduct:function(rec,obj){
//        obj.cancel=false;
//        obj.ckeckProduct=false
//        if(this.fireEvent("validateedit", obj) !== false && !obj.cancel){
//            obj.record.set(obj.field, obj.value);
//            delete obj.cancel;
//            this.fireEvent("afteredit", obj);
//        }
//    },
//    setCurrencyid:function(currencyid,rate,symbol,rec,store){
//        this.symbol=symbol;
//        this.currencyid=currencyid;
//        this.rate=rate;
//        for(var i=0;i<this.store.getCount();i++){
//            this.store.getAt(i).set('currencysymbol',this.symbol)
//            this.store.getAt(i).set('currencyrate',this.rate)
//        }
//        this.getView().refresh();
//    //     this.store.commitChanges();
//     },
    setCurrencyAmount:function(amount){
    if(this.isNote)
        return amount;
          return (amount*this.rate)
    }
//    isAmountzero:function(store){
//        var amount;
//        var selModel=  this.getSelectionModel();
//        var len=Wtf.productStore.getCount();
//        for(var i=0;i<len;i++){
//            if(selModel.isSelected(i)){
//                amount=store.getAt(i).data["discamount"];
//                if(amount<=0)
//                    return true;
//            }
//        }
//        return false;
//    }
});    
