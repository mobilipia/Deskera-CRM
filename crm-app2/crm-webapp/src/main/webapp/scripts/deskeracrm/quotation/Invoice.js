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
function getQuoteTab(isWithOutInventory, tabId, moduleName, extraFilters, mainTabId){
    var tabTitle = WtfGlobal.getLocaleText({key:"crm.quotation.tabtitle",params:[moduleName]});//" Quotations";
    var quotePanel = new Wtf.account.TransactionListPanel({
        id : tabId,
        border : false,
        isOrder:true,
        isCustomer:true,
        mainTab : mainTabId,
        tabtype : extraFilters.tabtype,
        moduleid : extraFilters.moduleid,
        modulename : moduleName,
//        isCustBill: isWithOutInventory,
        title: Wtf.util.Format.ellipsis(tabTitle, Wtf.TAB_TITLE_LENGTH),
        tabTip: tabTitle,
//        extraFilters: extraFilters,
        label:WtfGlobal.getLocaleText("crm.quotaion"),
        doctype : 1,
//        helpmodeid:18,
        layout: 'fit',
        closable: true,
        iconCls:"pwndCRM quotationtabicon"
    });
    return quotePanel;
}

function openQuotation(MainTab, moduleId, moduleName) {
//    var rec = this.EditorGrid.getSelectionModel().getSelected();
    var tabId = 'quotation'+MainTab.id;
    var panel = Wtf.getCmp(tabId);
    if(panel==null){
        panel = getQuoteTab(false, tabId, moduleName ,{tabtype:1, moduleid : moduleId}, MainTab.id);
        MainTab.add(panel);
//        panel.on('journalentry',callJournalEntryDetails);
    }
    MainTab.setActiveTab(panel);
    MainTab.doLayout();

//    callSalesOrder(false,null,"quotation1");
}

function callQuotation(isEdit,rec,winid, mainTabId, moduleid, modulename, listtabid){
//    if(!WtfGlobal.EnableDisable(Wtf.UPerm.invoice, Wtf.Perm.invoice.createso)) {
    winid=(winid==null?'quotation':winid);
    var label = modulename+' Quotation';
    var panel = Wtf.getCmp(winid);
    if(panel==null){
        panel = new Wtf.account.TransactionPanel({
            id : winid,
            isEdit: isEdit,
            record: rec,
            isCustomer:true,
            moduleid : moduleid,
            modulename : modulename,
            doctype : 1, // 1 -> Proposal/Quotation, 2->Contract, 3->Invoice
            isOrder:true,
            label:WtfGlobal.getLocaleText("crm.quotaion"),
            border : false,
//            heplmodeid: 11,
//            layout: 'border',
            closable: true,
            title:Wtf.util.Format.ellipsis(((isEdit? WtfGlobal.getLocaleText("crm.quotaion.edit"):WtfGlobal.getLocaleText("crm.quotaion.create"))+label+" "+((rec != null)?rec.data.billno:"")),Wtf.TAB_TITLE_LENGTH),
            iconCls:"pwndCRM quotationtabicon"
        });
        panel.on("activate", function(){
            if(Wtf.isIE7) {
                var northHt=(Wtf.isIE?150:180);
                var southHt=(Wtf.isIE?210:150);
                Wtf.getCmp(winid + 'southEastPanel').setHeight(southHt);
                Wtf.getCmp(winid + 'southEastPanel').setWidth(650);
                panel.NorthForm.setHeight(northHt);
                panel.southPanel.setHeight(southHt);
                panel.on("afterlayout", function(panel, lay){if(Wtf.isIE7) {panel.Grid.setSize(panel.getInnerWidth() - 18,200);}},this);
            }
            panel.doLayout();
        }, this);
        Wtf.getCmp(mainTabId).add(panel);
    }
    Wtf.getCmp(mainTabId).setActiveTab(panel);
    panel.on('update',  function(){
        if(isEdit == true){
            Wtf.getCmp(mainTabId).remove(panel);
        }
        Wtf.getCmp(listtabid).loadStore();
    }, this);
    Wtf.getCmp(mainTabId).doLayout();
//    }
//    else
//        WtfComMsgBox(46,0,false,isEdit?"editing":"creating" +" Sales Order");
}

Wtf.account.ClosablePanel=function(config){
    Wtf.account.ClosablePanel.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.account.ClosablePanel,Wtf.Panel,{
    closeMsg:WtfGlobal.getLocaleText("crm.quotation.closetabwarningmsg"),//"The data you filled is unsaved. Do you still want to close the panel?",
    
    isClosable:true,
    initComponent:function(config){
        Wtf.account.ClosablePanel.superclass.initComponent.call(this,config);
        this.on('beforeclose', this.askToClose,this);
    },

    askToClose:function(){
        if(this.isClosable!==true){
            Wtf.MessageBox.show({
                title: WtfGlobal.getLocaleText("crm.msg.WARNINGTITLE"),//'Warning',
                msg: this.closeMsg,
               // width:500,
                buttons: Wtf.MessageBox.YESNO,
                animEl: 'mb9',
                fn:function(btn){
                    if(btn!="yes")return;
                    this.ownerCt.remove(this);
                },
                scope:this,
                icon: Wtf.MessageBox.QUESTION
            });
        }
        return this.isClosable;
    }
});


function editInvoiceExchangeRates(winid,basecurrency,foreigncurrency,exchangerate){
    function showInvoiceExternalExchangeRate(btn,txt){
        if(btn == 'ok'){
             if(txt.indexOf('.')!=-1)
                 var decLength=(txt.substring(txt.indexOf('.'),txt.length-1)).length;
            if(isNaN(txt)||txt.length>15||decLength>7||txt==0){
                Wtf.MessageBox.show({
                    title: WtfGlobal.getLocaleText("crm.quotation.exchangeratemsgtitle"),// 'Exchange Rate',
                 // "You have entered an incorrect exchange rate. Please note:<br>* Only seven decimal places are allowed <br>* Alpha-Numeric and Special character are not allowed",
                    msg:WtfGlobal.getLocaleText("crm.quotation.exchangeratemsgdetail"),
                    buttons: Wtf.MessageBox.OK,
                    icon: Wtf.MessageBox.WARNING,
//                    width: 300,
                    scope: this,
                    fn: function(){
                        if(btn=="ok"){
                            editInvoiceExchangeRates(winid,basecurrency,foreigncurrency,exchangerate);
                        }
                    }
                });
            }else{
                Wtf.getCmp(winid).externalcurrencyrate=txt;
                Wtf.getCmp(winid).updateFormCurrency();
            }
        }
    }
    //<b>Present Exchange Rate:</b> 1 {0} = {1} + {2} <br><b> Input New Exchange Rate :</b>
    Wtf.MessageBox.prompt(WtfGlobal.getLocaleText("crm.quotation.exchangeratemsgtitle"),WtfGlobal.getLocaleText({key:"crm.quotation.exchangeratemsg",params:[basecurrency,exchangerate,foreigncurrency]}), showInvoiceExternalExchangeRate);
}
/*< COMPONENT USED FOR >
 *      1.Credit Vendor Invoice
 *          callGoodsReceipt(isEdit,rec,winid) --- <  >
 *          [isEdit=true/false, isCustomer=false, record]
 *      2.Invoice
 *          callInvoice(isEdit,rec,winid) --- < Create Invoice >
 *          [isEdit=true/false, isCustomer=true, record]
 *      3.Invoice
 *          callBillingInvoice(isEdit,rec,winid) --- < Create Invoice >
 *          [isEdit=true/false, isCustomer=true, isCustBill:true, record]
 *      4.Sales Receipt
 *          callBillingSalesReceipt(isEdit,rec,winid) --- < Create Cash Sales >
 *          [isEdit=true/false, isCustomer=true, isCustBill:true, cash:true, record]
 *      5.Cash Sales
 *          callSalesReceipt(isEdit,rec,winid) --- < Credit Cash Sales >
 *          [isEdit=true/false, isCustomer=true, cash:true, record]
 *      6.Cash Purchase
 *          callPurchaseReceipt(isEdit,rec,winid) --- <  >
 *          [isEdit=true/false, isCustomer=false, cash:true, record]
 *      7.Sales Order
 *          callSalesOrder(isEdit,rec,winid) --- < Create sales Order >
 *          [isEdit=true/false, isCustomer=true, isOrder=true, record]
 *      8.Puchase Order
 *          callPurchaseOrder(isEdit,rec,winid) --- <  >
 *          [isEdit=true/false, isCustomer=false, isOrder=true, record]
 *
 *      9.this.appendId --- It is used when this.id is appended in the id of component. This is useful for displaying help.
 */

Wtf.account.TransactionPanel=function(config){
    this.DefaultVendor = config.DefaultVendor;
	this.id=config.id;
    this.doctype = config.doctype;
    this.dataLoaded=false;
    this.isExpenseInv=false;
    this.isEdit=config.isEdit;
    this.label=config.label;
    this.copyInv=config.copyInv;
    this.billid=null;
    this.custChange=false;
    this.record=config.record;
    this.datechange=0;
    this.oldval="";this.val="";this.pronamearr=[];
    this.changeGridDetails=true;
    this.appendID = true;
//    var help=getHelpButton(this,config.heplmodeid);
//    this.custUPermType=config.isCustomer?Wtf.UPerm.customer:Wtf.UPerm.vendor;
//    this.custPermType=config.isCustomer?Wtf.Perm.customer:Wtf.Perm.vendor;
//    this.soUPermType=(config.isCustomer?Wtf.UPerm.invoice:Wtf.UPerm.vendorinvoice);
//    this.soPermType=(config.isCustomer?Wtf.Perm.invoice.createso:Wtf.Perm.vendorinvoice.createpo);
    Wtf.apply(this,{
        bbar:[{
            text:WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
            id:"save"+config.heplmodeid+this.id,
            scope:this,
            handler:this.save.createDelegate(this),
            iconCls:"pwnd savemasterdatasequenceicon"
        },'->']
      });
    Wtf.account.TransactionPanel.superclass.constructor.call(this,config);
    this.addEvents({
        'update':true
    });
}

Wtf.extend(Wtf.account.TransactionPanel,Wtf.account.ClosablePanel,{
    autoScroll: true,// layout:'border',//Bug Fixed: 14871[SK]
    bodyStyle: {background:"#DFE8F6 none repeat scroll 0 0"},
    border:'false',
    externalcurrencyrate:0,    
    isCurrencyLoad:false,
    currencyid:null,
    custdatechange:false,
    closable : true,
    cash:false,
    isCustomer:false,
    isCustBill:false,
    isOrder:false,
    fromOrder:false,
    loadRecord:function(){        
        if(this.record!=null&&!this.dataLoaded){
            var data=this.record.data;
            this.NorthForm.getForm().loadRecord(this.record);
            if(this.isEdit)
                this.Number.setValue(data.billno);
            this.Currency.setValue(data.currencyid);
            var store=(this.isCustomer?Wtf.contactStoreSearch:Wtf.vendorAccStore)
            var index=store.findBy( function(rec){
                var parentname=rec.data['accid'];
                if(parentname==data.personid)
                    return true;
                 else
                    return false
            })
            if(index>=0)
                this.Name.setValue(data.personid);
            this.Memo.setValue(data.memo);
            this.billTo.setValue(data.billto); 
            this.DueDate.setValue(data.duedate);
            this.billDate.setValue(data.date);
            this.perDiscount.setValue(data.ispercentdiscount);
            this.Discount.setValue(data.discountval);
            this.isTaxable.setValue(data.taxincluded);
            this.PORefNo.setValue(data.porefno);
            if(data.taxid == ""){
            	this.isTaxable.setValue("No");
                this.Tax.setValue("");
                this.Tax.disable();
            }else{
            	this.Tax.setValue(data.taxid);
            	this.isTaxable.setValue("Yes");
            }
//            this.CostCenter.setValue(data.costcenterid);
            this.dataLoaded=true;
        }
    }, 
    onRender:function(config){
        if(this.isCustomer||this.isCustBill||this.isOrder||this.isEdit||this.copyInv)
            this.add(this.NorthForm,this.Grid,this.southPanel);
        else{
            this.add(this.NorthForm,this.GridPanel,this.southPanel);
        }
        this.on("activate", function(){
            this.Number.focus();
        },this);
        Wtf.account.TransactionPanel.superclass.onRender.call(this, config);
        
        this.initForClose();
    },
    initComponent:function(config){
        Wtf.account.TransactionPanel.superclass.initComponent.call(this,config);
        this.businessPerson=(this.isCustomer?'Customer':'Vendor');
//        this.term=0;

        this.tplSummary=new Wtf.XTemplate(
            '<div class="currency-view">',
            '<table width="100%">',
            '<tpl>',
            '<tr><td>'+WtfGlobal.getLocaleText("crm.quotation.invoice.subtotal")+'</td><td text-align=right>{subtotal}</td></tr>',
            '<tr><td>'+WtfGlobal.getLocaleText("crm.quotation.invoice.discount")+'</td><td align=right>{discount}</td></tr>',
            '</table>',
            '<hr class="templineview">',
            '</tpl>',
            '<table width="100%">',
            '<tr><td>'+WtfGlobal.getLocaleText("crm.quotation.invoice.amount")+'</td><td align=right>{totalamount}</td></tr>',
            '<tr><td><b>'+WtfGlobal.getLocaleText("crm.quotation.invoice.tax")+': </b></td><td align=right>{tax}</td></tr>',
            '</table>',
            '<table width="100%">',
            '</table>',
            '<hr class="templineview">',
            '<table width="100%">',
            '<tr><td>'+WtfGlobal.getLocaleText("crm.quotation.invoice.totalamount")+'</td><td align=right>{aftertaxamt}</td></tr>',
            '</table>',
            '<hr class="templineview">',
            '<hr class="templineview">',
            '</div>'
        );
        this.GridRec = Wtf.data.Record.create ([
            {name:'id'},
            {name:'number'}
        ]);
       
        this.termRec = new Wtf.data.Record.create([
            {name: 'termname'},
            {name: 'termdays'}
        ]);
        this.termds = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            },this.termRec),
    //        url: Wtf.req.account + 'CompanyManager.jsp',
            url : "ACCTerm/getTerm.do",
            baseParams:{
                mode:91
            }
         });
         
         this.currencyRec = new Wtf.data.Record.create([
            {name: 'currencyid',mapping:'tocurrencyid'},
            {name: 'symbol'},
            {name: 'currencyname',mapping:'tocurrency'},
            {name: 'exchangerate'},
            {name: 'htmlcode'}
         ]);
         this.currencyStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"count"
            },this.currencyRec),
    //        url:Wtf.req.account+'CompanyManager.jsp'
            url:"ACCCurrency/getCurrencyExchange.do"
         });
        
//         var transdate=(this.isEdit?WtfGlobal.convertToGenericDate(this.record.data.date):WtfGlobal.convertToGenericDate(new Date()));

         this.Currency= new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("crm.quotation.invoice.currency"),//'Currency',
            hiddenName:'currencyid',
            id:"currency"+this.heplmodeid+this.id,
            anchor: '94%',
            disabled:true,
            store:this.currencyStore,
            valueField:'currencyid',
            forceSelection: true,
            displayField:'currencyname',
            scope:this,
            selectOnFocus:true
        });

        this.Term= new Wtf.form.FnComboBox({
            fieldLabel:(this.isCustomer? WtfGlobal.getLocaleText("crm.quotation.invoice.credit"):WtfGlobal.getLocaleText("crm.quotation.invoice.debit"))+' '+WtfGlobal.getLocaleText("crm.quotation.invoice.term")+'*',//' Term*',
            itemCls : (this.cash||this.isOrder)?"hidden-from-item":"",
            hideLabel:this.cash||this.isOrder,
            id:"creditTerm"+this.heplmodeid+this.id,
            hidden:this.cash||this.isOrder,
            hiddenName:'term',
            anchor: '93.5%',
            store:this.termds,
            valueField:'termdays',
//            allowBlank:this.cash,
            emptyText:'Select '+(this.isCustomer? WtfGlobal.getLocaleText("crm.quotation.invoice.credit"):WtfGlobal.getLocaleText("crm.quotation.invoice.debit"))+' '+WtfGlobal.getLocaleText("crm.quotation.invoice.term")+'...',//'Credit':'Debit')+' Term...',
            forceSelection: true,
            displayField:'termname',
//            addNewFn:this.addCreditTerm.createDelegate(this),
            scope:this,
            /*listeners:{
                'select':{
                    fn:this.updateDueDate,
                    scope:this
                }
            },*/
            selectOnFocus:true
        });
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.creditterm, Wtf.Perm.creditterm.edit))
//            this.Term.addNewFn=this.addCreditTerm.createDelegate(this);
            
        this.relatedTo=new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("crm.quotaion.relatedto")+'*',
            name: 'relatedto',
            id:"relatedto"+this.heplmodeid+this.id,
            allowBlank:false,
            anchor:'60%',
            value : this.modulename,
            readOnly : true,
            scope:this/*,
            allowBlank:this.checkin*/
        });
            
//        this.relatedToStore = getRelatedToForQuote();

//        this.relatedTo= new Wtf.form.ComboBox({
//            fieldLabel:"Related To*",
//            hiddenName:"relatedto",
//            id:"relatedto"+this.heplmodeid+this.id,
//            store: this.relatedToStore,
//            valueField:'moduleid',
//            displayField:'modulename',
//            allowBlank:false,
//            hirarchical:true,
//            emptyText:'Select a module...',
//            mode: 'local',
////            typeAhead: true,
////            forceSelection: true,
////            selectOnFocus:true,
//            anchor:"50%",
////            triggerAction:'all',
//            value : this.moduleid,
//            disable:true,
////            editable:false,
// //           addNewFn:this.addPerson.createDelegate(this,[false,null,this.businessPerson+"window",this.isCustomer],true),
//            scope:this
//        });

//        this.relatedTo.on("select",function(combo,record,index){
//            this.Name.store.baseParams = {
//                moduleid : record.data.moduleid
//            };
//            this.Name.enable();
//        },this);
        
        this.contactRec = new Wtf.data.Record.create([
            {name: 'id'},
            {name: 'name'}
        ]);

        this.contactStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"count"
            },this.contactRec),
            baseParams:{
                moduleid:this.moduleid
            },
    //        url:Wtf.req.account+'CompanyManager.jsp'
            url:this.ajxurl = Wtf.req.springBase+"common/quotation/getRecordName.do"
         });
        this.Name= new Wtf.form.ComboBox({
            fieldLabel:((this.modulename=="Lead")?WtfGlobal.getLocaleText("crm.LEAD"):WtfGlobal.getLocaleText("crm.quotaion.grid.header.customer"))+"*",
            hiddenName:"customer",
//            id:"customer"+this.heplmodeid+this.id,
            store: this.contactStore,
            typeAhead:true,
            triggerClass :'dttriggerForTeamLead',
            valueField:'id',
            displayField:'name',
//            allowBlank:false,
            emptyText:(this.modulename=="Lead")?WtfGlobal.getLocaleText("crm.quotaion.search.lead"):WtfGlobal.getLocaleText("crm.quotaion.search.customer"),
            mode: 'remote',
            minChars:2,
//            disabled : true,
            forceSelection: true,
            allowBlank:false,
            anchor:"60%",
            //value : this.isEdit ? this.record.data.customername : '',
            triggerAction:'all',
            scope:this,
            listeners:{
                'change':{
                    fn:this.setNameID,
                    scope:this
                }
            }
        });
        
        // hidden field for customer guid
        this.NameID=new Wtf.form.Hidden({
        	scope:this,
        	value: this.isEdit ? this.record.data.customer : ''
        });
        // Neeraj
//        if(!(this.DefaultVendor==null || this.DefaultVendor==undefined) && !this.isCustomer){
//        	this.Name.value = this.DefaultVendor;
//        	this.updateData();
//        }
        
//        if(!WtfGlobal.EnableDisable(this.custUPermType,this.custPermType.create))
//            this.Name.addNewFn=this.addPerson.createDelegate(this,[false,null,this.businessPerson+"window",this.isCustomer],true);
        this.allAccountRec = new Wtf.data.Record.create([
            {name: 'accid'},
            {name: 'accname'},
            {name: 'groupid'},
            {name: 'groupname'},
            {name: 'level'},
            {name: 'leaf'},
            {name: 'openbalance'},
            {name: 'parentid'}, 
            {name: 'parentname'}
        ]);
        this.allAccountStore = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                totalProperty:'count',
                root: "data"
            },this.allAccountRec),
    //        url: Wtf.req.account +'CompanyManager.jsp',
            url:"ACCAccount/getAccountsForCombo.do",
            baseParams:{
                mode:2,
                group:(this.isCustomer?[5]:[7]),
                ignorecustomers:true,
                ignorevendors:true
            }
        });
        this.creditTo= new Wtf.form.FnComboBox({
            fieldLabel:(this.isCustomer?"Credit Account*":"Debit Account*"),
            hiddenName:"creditoraccount",
            anchor:"60%",
            store: this.allAccountStore,
            valueField:'accid',
            displayField:'accname',
            hidden:!this.isCustBill,
            hideLabel:!this.isCustBill,
            itemCls : (!this.isCustBill)?"hidden-from-item":"",
//            allowBlank:!this.isCustBill||this.isOrder,
            hirarchical:true,
            emptyText:'Select an Account...',
            mode: 'local',
            typeAhead: true,
            forceSelection: true,
            selectOnFocus:true,
            triggerAction:'all',
//            addNewFn: this.addAccount.createDelegate(this,[this.allAccountStore],true),
            scope:this
        });
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.coa, Wtf.Perm.coa.create))
            this.creditTo.addNewFn=this.addAccount.createDelegate(this,[this.allAccountStore],true);
        
        this.billTo = new Wtf.form.TextArea({
            fieldLabel: (this.cash?"Address":"Address*"),//'Bill '+(this.isCustomer?'To':'From')+"*",
            name:'billto',
            hidden:this.isOrder,
            hideLabel:this.isOrder,
            itemCls : this.isOrder?"hidden-from-item":"",
//            allowBlank:(this.cash?true:this.isOrder),
            value : this.isEdit ? this.record.data.billid : '',
            height:40,
            maxLength: 255,
            anchor:"60%"
        });

        this.perDiscountStore = new Wtf.data.SimpleStore({
            fields:[{name:'name'},{name:'value',type:'boolean'}],
            data:[['Percentage',true],['Flat',false]]
        });
        this.fromPOStore = new Wtf.data.SimpleStore({
            fields:[{name:'name'},{name:'value',type:'boolean'}],
            data:[['Yes',true],['No',false]]
        });
        this.vendorInvoice=new Wtf.form.TextField({
            fieldLabel:'Vendor Invoice Number*',
            name: 'vendorinvoice',
            id:"vendorInvoiceNo"+this.heplmodeid+this.id,
            hidden:this.label=='Vendor Invoice'?false:true,
            anchor:'60%',
            maxLength:45,
            scope:this/*,
            allowBlank:this.checkin*/
        });
        this.Number=new Wtf.form.TextField({
            fieldLabel:this.label+' '+WtfGlobal.getLocaleText("crm.quotaion.number")+'*',
            name: 'number',
            id:"invoiceNo"+this.heplmodeid+this.id,
            anchor:'60%',
            maxLength:45,
            disabled:this.isEdit?true:false,
            scope:this/*,
            allowBlank:this.checkin*/
        });
        this.PORefNo=new Wtf.form.TextField({
            fieldLabel:'PO Reference Number',
            name: 'porefno',
            hidden:this.isOrder||!this.isCustomer,
            hideLabel:this.isOrder||!this.isCustomer,
            itemCls : (this.isOrder||!this.isCustomer)?"hidden-from-item":"",
            anchor:'60%',
            maxLength:45,
            scope:this
            
        });

        this.templateRec = new Wtf.data.Record.create([
            {name: 'tempid'},
            {name: 'tempname'}
        ]);
        this.templateStore = new Wtf.data.Store({
            url:  "Common/ExportPdfTemplate/getAllReportTemplate.do",
            method: 'GET',
            baseParams : {
                templatetype : this.doctype
            },
            reader: new Wtf.data.KwlJsonReader({
                root: 'data'
            },this.templateRec)
        });

        this.template= new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("crm.quotaion.grid.header.template")+"*",
            hiddenName:"template",
            anchor:"94%",
            store: this.templateStore,
            valueField:'tempid',
            displayField:'tempname',
//            itemCls : (!this.isCustBill)?"hidden-from-item":"",
//            allowBlank:!this.isCustBill||this.isOrder,
//            hirarchical:true,
            emptyText:WtfGlobal.getLocaleText("crm.quotaion.grid.template.emptyText"),
            mode: 'local',
            typeAhead: true,
            forceSelection: true,
            allowBlank:false,
            selectOnFocus:true,
           // value : this.isEdit ? this.record.data.templatename :'',
            triggerAction:'all',
//            addNewFn: this.addAccount.createDelegate(this,[this.allAccountStore],true),
            scope:this,
            listeners:{
                'change':{
                    fn:this.setTemplateID,
                    scope:this
                }
            }
        });
        this.templateID=new Wtf.form.Hidden({
        	scope:this,
        	value: this.isEdit ? this.record.data.templateid : ''
        });
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.coa, Wtf.Perm.coa.create))
        this.template.addNewFn=this.addInvoiceTemplate.createDelegate(this,[this.templateStore],true);
        this.loadTemplateStore();
        this.Memo=new Wtf.form.TextArea({
            fieldLabel:WtfGlobal.getLocaleText("crm.quotaion.grid.header.memo"),
            name: 'memo',
            id:"memo"+this.heplmodeid+this.id,
            height:60,
            anchor:'94%',
            maxLength:1024
        });
        this.Discount=new Wtf.form.NumberField({
            allowNegative:false,
           // hidden:this.isOrder,
            defaultValue:0,
            //hideLabel:this.isOrder,
//            allowBlank:this.isOrder,
            maxLength: 10,
            anchor:'90%',
            fieldLabel:WtfGlobal.getLocaleText("crm.quotaion.grid.header.discount"),
            name:'discount',
            id:"discount"+this.heplmodeid+this.id,
            listeners:{
                'change':{
                    fn:this.updateSubtotal,
                    scope:this
                }
            }
        });
        this.perDiscount= new Wtf.form.ComboBox({
            labelSeparator:'',
            labelWidth:0,
            triggerAction:'all',
            mode: 'local',
            valueField:'value',
            displayField:'name',
            store:this.perDiscountStore,
//            hidden:this.isOrder,
//            hideLabel:!this.isOrder,
//            allowBlank:this.isOrder,
            value:false,
            anchor:'90%',
            typeAhead: true,
            forceSelection: true,
            name:'perdiscount',
            hiddenName:'perdiscount',
            listeners:{
                'select':{
                    fn:this.updateSubtotal,
                    scope:this
                }
            }
        });

         this.PORec = Wtf.data.Record.create ([
            {name:'billid'},
            {name:'journalentryid'},
            {name:'entryno'},
            {name:'billto'},
            {name:'discount'},
            {name:'shipto'},
            {name:'mode'},
            {name:'billno'},
            {name:'date', type:'date'},
            {name:'duedate', type:'date'},
            {name:'shipdate', type:'date'},
            {name:'personname'},
            {name:'creditoraccount'},
            {name:'personid'},
            {name:'shipping'},
            {name:'othercharges'},
            {name:'taxid'},
            {name:'currencyid'},
            {name:'amount'},
            {name:'amountdue'},
            {name:'costcenterid'},
            {name:'costcenterName'},
            {name:'memo'}
        ]);
        this.POStoreUrl = "";
        if(this.businessPerson=="Customer"){
            //mode:(this.isCustBill?52:42)
            this.POStoreUrl = this.isCustBill?"ACCSalesOrderCMN/getBillingSalesOrders.do":"ACCSalesOrderCMN/getSalesOrders.do";
        }else if(this.businessPerson=="Vendor"){
            this.POStoreUrl = this.isCustBill?"ACCPurchaseOrderCMN/getBillingPurchaseOrders.do":"ACCPurchaseOrderCMN/getPurchaseOrders.do";
        }
        this.POStore = new Wtf.data.Store({
            url:this.POStoreUrl,
    //        url: Wtf.req.account+this.businessPerson+'Manager.jsp',
            baseParams:{
                mode:(this.isCustBill?52:42),
                closeflag:true
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:'count'
            },this.PORec)
        });
        
        this.fromPO= new Wtf.form.ComboBox({
            triggerAction:'all',
            hideLabel:this.cash||this.isOrder,
            hidden:this.cash||this.isOrder,
            mode: 'local',
            valueField:'value',
            displayField:'name',
            store:this.fromPOStore,
            id: "linkToOrder"+this.heplmodeid+this.id,
            fieldLabel:"Link to "+(this.isCustomer?"Sales":"Purchase")+" Order",
//            allowBlank:this.isOrder,
            value:false,
            width:50,
            typeAhead: true,
            forceSelection: true,
            name:'prdiscount',
            hiddenName:'prdiscount'/*,
            listeners:{
                'select':{
                    fn:this.enablePO,
                    scope:this
                }
            }*/
        });
        this.includeTaxStore = new Wtf.data.SimpleStore({
            fields:[{name:'name'},{name:'value',type:'boolean'}],
            data:[['Yes',true],['No',false]]
        });
        this.includeProTax= new Wtf.form.ComboBox({
            triggerAction:'all',
            mode: 'local',
            valueField:'value',
            displayField:'name',
            store:this.includeTaxStore,
           // id: "linkToOrder"+this.id+this.heplmodeid,
            fieldLabel:WtfGlobal.getLocaleText("crm.quotaion.includeproducttax"),
            id:"includeprotax"+this.heplmodeid+this.id,
          //  allowBlank:this.isOrder,
            value:false,
            anchor:'94%',
            typeAhead: true,
            forceSelection: true,
//            hidden : true,
//            hideLabel : true,
            name:'includepropax',
            hiddenName:'includepropax',
            listeners:{
                'select':{
                    fn:this.showGridTax,
                    scope:this
                }
            }
        });
        this.PO= new Wtf.form.FnComboBox({
            fieldLabel:(this.isCustomer?"SO":"PO")+" Number",
            hiddenName:"ordernumber",
            id:"orderNumber"+this.heplmodeid+this.id,
            store: this.POStore,
            valueField:'billid',
            hideLabel:this.cash||this.isOrder,
            hidden:this.cash||this.isOrder,
            displayField:'billno',
            disabled:true,
            emptyText:'select a '+(this.isCustomer?"SO":"PO")+'...',
            mode: 'local',
            typeAhead: true,
            forceSelection: true,
            selectOnFocus:true,
            anchor:"85%",
            triggerAction:'all',
//            addNewFn:this.addOrder.createDelegate(this,[false,null,this.businessPerson+"PO"],true),
            scope:this/*,
            listeners:{
                'select':{
                    fn:this.populateData,
                    scope:this
                }
            }*/
        });
//        if(!WtfGlobal.EnableDisable(this.soUPermType, this.soPermType))
//            this.PO.addNewFn=this.addOrder.createDelegate(this,[false,null,this.businessPerson+"PO"],true)
        this.DueDate= new Wtf.form.DateField({
            fieldLabel: 'Due Date*',
            name: 'duedate',
            offset:Wtf.pref.tzoffset,
            itemCls : (this.cash||this.isOrder)?"hidden-from-item":"",
            hideLabel:this.cash||this.isOrder,
            hidden:this.cash||this.isOrder,
            format:WtfGlobal.getOnlyDateFormat(),
            //allowBlank:false,
            value : this.isEdit ? this.record.data.duedate : '',
            anchor:'94%'
        });
        
        this.billDate= new Wtf.form.DateField({
            fieldLabel:this.label+' '+WtfGlobal.getLocaleText("crm.quotaion.date")+'*',
            id:"invoiceDate"+this.heplmodeid+this.id,
            format:WtfGlobal.getOnlyDateFormat(),
            offset:Wtf.pref.tzoffset,
            name: 'billdate',
           // value : this.isEdit ? this.record.data.date : '',
            anchor:'60%',
            listeners:{
                'change':{
                    fn:this.updateDueDate,
                    scope:this
                }
            },
            allowBlank:false
////            disabled: true
//            maxValue: new Date(Wtf.serverDate),
//            minValue: new Date(Wtf.account.companyAccountPref.fyfrom)
        });

//        chkFormCostCenterload();
//        this.CostCenter= new Wtf.form.FnComboBox({
//            fieldLabel:"Cost Center",
//            hiddenName:"costcenter",
//            store: Wtf.FormCostCenterStore,
//            valueField:'id',
//            displayField:'name',
//            mode: 'local',
//            typeAhead: true,
//            forceSelection: true,
//            selectOnFocus:true,
//            anchor:"50%",
//            triggerAction:'all',
//            addNewFn:this.addCostCenter,
//            scope:this
//        });


        var itemArr = [{
                layout:'column',
                border:false,
                defaults:{border:false},items:[ {
                    layout:'form',
                    ctCls : (this.cash||this.isOrder)?"hidden-from-item":"",
                    width:215,
                    items:this.fromPO
                },{
                    width:210,
                    layout:'form',
                    labelWidth:70,
                    items:this.PO
               }]},this.relatedTo,this.Name,this.Number,this.billDate,this.billTo,this.creditTo,this.PORefNo/*, this.CostCenter*/];
       var ht=(this.isOrder?(Wtf.isIE?180:250):(Wtf.isIE?290:260));
       if(this.isCustBill)ht+=25;
        this.NorthForm=new Wtf.form.FormPanel({
            region:'north',
            //height:ht,
            border:false,
            items:[{
                layout:'form',
                baseCls:'northFormFormat',
                labelWidth:155,
                cls:"visibleDisabled",
                items:[{
                    layout:'column',
                    border:false,
                    defaults:{border:false},
                    items:[{
                        layout:'form',
                        columnWidth:0.60,
                        border:false,
                        items:itemArr
                    },{
                        layout:'form',
                        columnWidth:0.40,
                        cls: 'quotation',
                        border:false,
                        items:[this.Term,this.DueDate,{
                            //itemCls : (this.isOrder)?"hidden-from-item":"",
                            layout:'column',
                            border:false,
                            defaults:{border:false},
                            items:[{
                                layout:'form',
                                columnWidth:0.55,
                                items:this.Discount
                            },{
                                columnWidth:0.45,
                               // layout:'form',
                                items:this.perDiscount
                           }]
                        },this.template,this.Memo,
                        this.templateID,this.NameID/*,this.Currency*/,this.includeProTax]
                    }]
                }]
            }]
        });
       this.southCenterTplSummary=new Wtf.XTemplate(
    "<div style='line-height:18px;'> <b>"+WtfGlobal.getLocaleText("crm.quotaion.currency")+":</b> {basecurrency}</div>",
             '<tpl if="editable==true">',
         "<b>Applied Exchange Rate for the current transaction:</b>",
         "<div style='line-height:18px;padding-left:30px;'>1 {basecurrency} (Home Currency) = {exchangerate} {foreigncurrency} (Foreign Currency) </div>",
         "<div style='line-height:18px;padding-left:30px;padding-bottom:5px;'>1 {foreigncurrency} (Foreign Currency) = {revexchangerate} {basecurrency} (Home Currency) </div>",

     this.isOrder?"":"If you want to change the Exchange Rate for the current transaction only, then please <a class='tbar-link-text' href='#' onClick='javascript: editInvoiceExchangeRates(\""+this.id+"\",\"{basecurrency}\",\"{foreigncurrency}\",\"{exchangerate}\")'wtf:qtip=''>Click Here</a>",
             '</tpl>'
        );
        this.southCenterTpl=new Wtf.Panel({
            border:false,
            html:this.southCenterTplSummary.apply({basecurrency:WtfGlobal.getCurrencyName(),exchangerate:'x',foreigncurrency:"Foreign Currency", editable:false})
        });
        this.southCalTemp=new Wtf.Panel({
            border:false,
            baseCls:'tempbackgroundview',
            html:this.isEdit?this.tplSummary.apply({subtotal:WtfGlobal.currencyRenderer(0),discount:WtfGlobal.currencyRenderer(0),totalamount:WtfGlobal.currencyRenderer(this.record.data.amount),tax:WtfGlobal.currencyRenderer(0),aftertaxamt:WtfGlobal.currencyRenderer(this.record.data.amount)}):this.tplSummary.apply({subtotal:WtfGlobal.currencyRenderer(0),discount:WtfGlobal.currencyRenderer(0),totalamount:WtfGlobal.currencyRenderer(0),tax:WtfGlobal.currencyRenderer(0),aftertaxamt:WtfGlobal.currencyRenderer(0)})
        });
        this.helpMessage= new Wtf.Button({
            text:'Help',
            handler:this.helpmessage,
            scope:this,
            tooltip: 'Click for help',
            iconCls: 'help'
        });
        this.addGrid();
        this.isTaxable= new Wtf.form.ComboBox({
            triggerAction:'all',
            mode: 'local',
            valueField:'value',
            displayField:'name',
            id:"includetax"+this.heplmodeid+this.id,
            store:this.fromPOStore,
            listWidth:50,
            fieldLabel:WtfGlobal.getLocaleText("crm.quotaion.includetax"),//"Include Tax",
            allowBlank:this.isOrder,
            value:false,
            width:50,
            typeAhead: true,
//            hidden : true,
//            hideLabel : true,
            forceSelection: true,
            name:'includetax',
            hiddenName:'includetax',
            listeners:{
                'select':{
                    fn:this.enabletax,
                    scope:this
                }
            }
        });
        this.Tax= new Wtf.form.FnComboBox({
            fieldLabel:WtfGlobal.getLocaleText("crm.quotaion.grid.header.tax"),//'Tax',
            id:"tax"+this.heplmodeid+this.id,
            disabled:!this.isEdit,
            hiddenName:'tax',
            anchor: '97%',
            store:this.Grid.taxStore, 
            valueField:'prtaxid',
            mode:'local',
            triggerAction :'all',
//            hidden : true,
//            hideLabel : true,
            forceSelection: true,
            displayField:'prtaxname',
           addNewFn:this.addTax.createDelegate(this),
            scope:this,
            listeners:{
                'select':{
                    fn:this.updateSubtotal,
                    scope:this
                }
            },
            selectOnFocus:true
        });
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.tax, Wtf.Perm.tax.view))
//            this.Tax.addNewFn=this.addTax.createDelegate(this);

        this.southPanel=new Wtf.Panel({
            region:'south',
            border:false,
            style:'padding:0px 10px 10px 10px',
            layout:'column',//layout:'border',//Bug Fixed: 14871[SK] Scrolling issue : changed layout from border to column
            height:(Wtf.isIE?210:150),
            items:[{
                columnWidth: .45,// width: 570,//region:'center',
                border:false,
                items:this.southCenterTpl
            },{
//                region:'east',
                id : this.id + 'southEastPanel',
                columnWidth: .55,//width:650,
                border:false,
                layout:'column',
                items:[{
                    layout:'form',
                    width:130,
                    labelWidth:70,
                    border:false,
                    items:this.isTaxable
                },{
                    layout:'form',
                    columnWidth:0.4,
                    labelWidth:30,
                    border:false,
                    items:this.Tax
                },{
                    columnWidth:0.6,
                    layout:'form',
                    cls:'bckgroundcolor',
                    bodyStyle:'padding:10px',
                    labelWidth:70,
                    items:this.southCalTemp
               }]
            }]
        });        
        this.NorthForm.doLayout();
//        this.NorthForm.doLayout();
        this.southPanel.doLayout();
        this.POStore.on('load',this.updateSubtotal,this)
//        this.DueDate.on('blur',this.dueDateCheck,this);
        this.billDate.on('change',this.onDateChange,this);

        if(this.isEdit){
            this.Grid.taxStore.on('load',this.DetailPanelRender,this );
            
        }else{
            this.setTransactionNumber();
        }
//        WtfComMsgBox(29,4,true);
//        this.isCustomer?chkcustaccload():chkvenaccload();
        this.ajxUrl = "CommonFunctions/getInvoiceCreationJson.do";
//        var params={
////            transactiondate:transdate,
//            loadtaxstore:true,
//            loadpricestore:!(this.isCustBill||this.isExpenseInv),
//            loadcurrencystore:true,
//            loadtermstore:true
////            loadInventory:this.isCustomer
//        }
//        Wtf.Ajax.requestEx({url:this.ajxUrl,params:params}, this, this.successCallback, this.failureCallback);
       this.currencyStore.on('load',this.changeTemplateSymbol,this);
       if(!this.isCustBill&&!this.isCustomer&&!this.isOrder&&!this.isEdit&&!this.copyInv){
           this.ProductGrid.on('pricestoreload',function(arr){//alert("1111"+arr.length)
               if(!this.isExpenseInv){
                    this.datechange=1;
                    this.changeCurrencyStore(arr);
               }
           },this);//.createDelegate(this)
       }else if(!this.isCustBill&&!this.isExpenseInv){//alert("2222"+arr.length)
           this.Grid.on('pricestoreload',function(arr){
                this.datechange=1;
                this.changeCurrencyStore(arr);
       }.createDelegate(this),this);}
    },

    DetailPanelRender: function() {
        var data=this.record.data;
        this.Name.valueNotFoundText=undefined;
        this.template.valueNotFoundText=undefined;
        this.setTransactionNumber(this.isEdit);
        this.billDate.setValue(data.date);
        this.Name.setValue(data.customername);
        this.template.setValue(data.templatename);
        this.isTaxable.setValue(data.istaxable);
        this.Memo.setValue(data.memo);
        if(data.istaxable) {
            this.Tax.setValue(data.taxid);
        } else {
            this.Tax.disable();
            this.Tax.setValue("");
        }
        this.Discount.setValue(data.discount!=""?data.discount:0);
        this.perDiscount.setValue(data.perdiscount);
        this.includeProTax.setValue(data.isproducttax);
        if(this.includeProTax.getValue())
            this.showGridTax(null,null,false);
        this.updateSubtotal();
    },
    
    onDateChange:function(a,val,oldval){
        this.val=val;
        this.oldval=oldval;
//        this.loadTax(val);
        this.externalcurrencyrate=0;
        this.custdatechange=true;
//        if(!(this.isCustBill||this.isExpenseInv))
//            this.Grid.loadPriceStoreOnly(val,this.Grid.priceStore);
//        else{
//            this.changeCurrencyStore();
//            this.updateSubtotal();
//            this.applyCurrencySymbol();
//            var subtotal=WtfGlobal.addCurrencySymbolOnly(this.Grid.calSubtotal(),this.symbol)
//            var discount=WtfGlobal.addCurrencySymbolOnly(this.getDiscount(),this.symbol)
//            var totalamount=WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount(),this.symbol)
//            var tax=WtfGlobal.addCurrencySymbolOnly(this.caltax(),this.symbol);
//            var aftertaxamt=WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount()+this.caltax(),this.symbol);
//            this.tplSummary.overwrite(this.southCalTemp.body,{subtotal:subtotal,discount:discount,totalamount:totalamount,tax:tax,aftertaxamt:aftertaxamt});
//        }
   },
    loadTax:function(val){ 
        this.Grid.taxStore.load({params:{transactiondate:WtfGlobal.convertToGenericDate(val)}});
        this.Tax.setValue("");
        this.Grid.getStore().each(function(rec){
            rec.set('prtaxid','')
            rec.set('taxamount',0)
        },this);
    },
//    successCallback:function(response){
//        if(response.success){
//            if(!this.isCustBill&&!this.isCustomer&&!this.isOrder&&!this.isEdit&&!this.copyInv){
//                this.ProductGrid.taxStore.loadData(response.taxdata);
//                this.ExpenseGrid.taxStore.loadData(response.taxdata);
//            }
//            else
//                this.Grid.taxStore.loadData(response.taxdata);
//            this.termds.loadData(response.termdata);
//            this.currencyStore.loadData(response.currencydata);
////            if(!(this.isCustBill||this.isExpenseInv)){
////                this.Grid.priceStore.loadData(response.productdata); }
//             if(this.currencyStore.getCount()<=1){
//                callCurrencyExchangeWindow();
//                WtfComMsgBox(['Alert',"Please set Currency Exchange Rates"],2);
//            }
//            else{
//                this.isCurrencyLoad=true;
//                this.applyTemplate(this.currencyStore,1);
//            }
//            if(this.cash)
//                this.Term.setValue(0);
//
//            if(this.isEdit && this.record!=null) {
//                this.Tax.setValue(this.record.data.taxid);
//            }
//            if(this.isEdit)this.getTerm();
//            if(this.isEdit)this.loadRecord();
//            this.hideLoading();
//            if(this.isEdit && !this.isOrder)
//                this.loadEditableGrid();
//            else if(this.isEdit && this.isOrder)
//            	this.loadEditableGridisOrder();
//
//        }
//    },

//    failureCallback:function(response){
//         this.hideLoading();
//         WtfComMsgBox(["Alert", "Fail to load the record(s). "+response.msg], 2);
//    },
//    hideLoading:function(){  Wtf.MessageBox.hide(); },

    applyTemplate:function(store,index){
        var editable=this.Currency.getValue()!=WtfGlobal.getCurrencyID()&&this.Currency.getValue()!=""//&&!this.isOrder;
        var exchangeRate = store.getAt(index).data['exchangerate'];
        if(this.externalcurrencyrate>0) {
            exchangeRate = this.externalcurrencyrate;
        } else if(this.isEdit && this.record.data.externalcurrencyrate&&!this.custdatechange){
            var externalCurrencyRate = this.record.data.externalcurrencyrate-0;//??[PS]
            if(externalCurrencyRate>0){
                exchangeRate = externalCurrencyRate;
            }
        } 
        var revExchangeRate = 1/(exchangeRate-0);
        revExchangeRate = (Math.round(revExchangeRate*10000000))/10000000;
        this.southCenterTplSummary.overwrite(this.southCenterTpl.body,{foreigncurrency:store.getAt(index).data['currencyname'],exchangerate:exchangeRate,basecurrency:WtfGlobal.getCurrencyName(),editable:editable,revexchangerate:revExchangeRate
            });
    },

    changeCurrencyStore:function(pronamearr){
        this.pronamearr=pronamearr;
        var currency=this.Currency.getValue();
        if(this.val=="")this.val=this.billDate.getValue();         
        if(currency!=""||this.custChange)
            this.currencyStore.load({params:{mode:201,transactiondate:WtfGlobal.convertToGenericDate(this.val),tocurrencyid:this.Currency.getValue()}});
        else
            this.currencyStore.load({params:{mode:201,transactiondate:WtfGlobal.convertToGenericDate(this.val)}});                 
    },
    changeTemplateSymbol:function(){


     /*if date of without inventory changes. price store will not be loaded in this case.[PS]*/
        if(this.isCustBill||this.isExpenseInv){
            if(this.currencyStore.getCount()==0){
            callCurrencyExchangeWindow();
             WtfComMsgBox(["Alert", "Please set the Currency Rate for the selected date: <b>"+WtfGlobal.convertToGenericDate(this.val)+"</b>"], 0);
             this.billDate.setValue("");
            }
            else
                this.updateFormCurrency();
        }


     /*if date of withinventory changes. After price store load. [PS]   */
//     alert(this.datechange+"---"+this.pronamearr.length)
        if(this.datechange==1){
            var str=""
            if(this.pronamearr!=undefined&&this.pronamearr.length>0){
                str+=this.pronamearr[0];
                for(var i=1;i<this.pronamearr.length;i++){
                    str+="</b>, <b>"+ this.pronamearr[i]
                }
            }
            if(this.currencyStore.getCount()==0){
                    callCurrencyExchangeWindow();
                    str=" and price of <b>"+str+"</b>";
                    WtfComMsgBox(["Alert", "Please set the currency rate"+str+" for the selected date: <b>"+WtfGlobal.convertToGenericDate(this.val)+"</b>"], 0);
                    this.billDate.setValue("");
                    //                if(this.oldval!=""||this.oldval!=undefined){
                    //                    if(!this.isCustBill)
                    //                        this.Grid.loadPriceStoreOnly(this.oldval,this.Grid.priceStore);
                    //                    this.Grid.taxStore.load({params:{transactiondate:this.oldval}});
                    //                }
        } else {
                this.updateFormCurrency();
                if(this.pronamearr!=undefined&&this.pronamearr.length>0){
                    str=" price of <b>"+str+"</b>";
                    WtfComMsgBox(["Alert", "Please set the "+str+" for the selected date: <b>"+WtfGlobal.convertToGenericDate(this.val)+"</b>"], 0);}
                this.Grid.pronamearr=[];
                this.updateFormCurrency();
            }
            this.datechange=0;
            this.updateSubtotal();
            this.applyCurrencySymbol();
            var subtotal=WtfGlobal.addCurrencySymbolOnly(this.Grid.calSubtotal(),this.symbol)
            var discount=WtfGlobal.addCurrencySymbolOnly(this.getDiscount(),this.symbol)
            var totalamount=WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount(),this.symbol)
            var tax=WtfGlobal.addCurrencySymbolOnly(this.caltax(),this.symbol);
            var aftertaxamt=WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount()+this.caltax(),this.symbol);
            this.tplSummary.overwrite(this.southCalTemp.body,{subtotal:subtotal,discount:discount,totalamount:totalamount,tax:tax,aftertaxamt:aftertaxamt});
        }
        

    /*when customer/vendor name changes [PS]*/
        if(this.custChange){
            if(this.currencyStore.getCount()==0){
                callCurrencyExchangeWindow();
                WtfComMsgBox(["Alert", "Please set the currency rate for the selected date: <b>"+WtfGlobal.convertToGenericDate(this.val)+"</b>"], 0);
                this.Name.setValue("");
        } else{this.Currency.setValue(this.currencyid)
                this.updateFormCurrency();    }
            this.custChange=false;
        }
        this.Grid.pronamearr=[];
    },
    updateFormCurrency:function(){
       this.applyCurrencySymbol();
       this.tplSummary.overwrite(this.southCalTemp.body,{subtotal:WtfGlobal.addCurrencySymbolOnly(this.Grid.calSubtotal(),this.symbol),discount:WtfGlobal.addCurrencySymbolOnly(this.getDiscount(),this.symbol),totalamount:WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount(),this.symbol),tax:WtfGlobal.addCurrencySymbolOnly(this.caltax(),this.symbol),aftertaxamt:WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount()+this.caltax(),this.symbol)});
    },

    getCurrencySymbol:function(){ 
        var index=null;
        this.currencyStore.clearFilter(true);
        var FIND = this.Currency.getValue();
        index=this.currencyStore.findBy( function(rec){
             var parentname=rec.data['currencyid'];
            if(parentname==FIND)
                return true;
             else
                return false
            })
       this.currencyid=this.Currency.getValue();       
       return index;
    },

    applyCurrencySymbol:function(){
        var index=this.getCurrencySymbol();
        var rate=this.externalcurrencyrate;
        if(index>=0){
           rate=(rate==""?this.currencyStore.getAt(index).data.exchangerate:rate);
            this.symbol=  this.currencyStore.getAt(index).data.symbol;
            this.Grid.setCurrencyid(this.currencyid,rate,this.symbol,index);
            this.applyTemplate(this.currencyStore,index);        
       }
       return this.symbol;
    },
    
    loadEditableGrid:function(){ 
    this.StoreUrl = "";
        this.subGridStoreUrl = "";
        if (this.businessPerson=='Customer') {
            this.storeMode = this.isCustBill?16:12;
            this.StoreUrl = this.isCustBill?"ACCInvoiceCMN/getBillingInvoices.do":"ACCInvoiceCMN/getInvoices.do";
            this.subGridStoreUrl = this.isCustBill?"ACCInvoiceCMN/getBillingInvoiceRows.do":"ACCInvoiceCMN/getInvoiceRows.do";
        } else{
            this.storeMode = this.isCustBill?16:12;
            this.StoreUrl = this.isCustBill?"ACCGoodsReceiptCMN/getBillingGoodsReceipts.do":"ACCGoodsReceiptCMN/getGoodsReceipts.do";
            this.subGridStoreUrl = this.isCustBill?"ACCGoodsReceiptCMN/getBillingGoodsReceiptRows.do":"ACCGoodsReceiptCMN/getGoodsReceiptRows.do";
        }
        this.billid=this.record.data.billid;
        var mode=this.isCustBill?17:14;
        this.Grid.getStore().proxy.conn.url = this.subGridStoreUrl;
        this.Grid.getStore().load({params:{bills:this.billid,mode:mode}});
    },
    
    loadEditableGridisOrder:function(){ 
        this.subGridStoreUrl = "";
            if (!this.isCustomer) {
            	if(!this.isCustBill){
            		this.subGridStoreUrl = "ACCPurchaseOrderCMN/getPurchaseOrderRows.do";
            	}else{
            		this.subGridStoreUrl = "ACCPurchaseOrderCMN/getBillingPurchaseOrderRows.do";
            	}
            } else{
            	if(!this.isCustBill){
            		this.subGridStoreUrl = "ACCSalesOrderCMN/getSalesOrderRows.do";
            	}else{
            		this.subGridStoreUrl = "ACCSalesOrderCMN/getBillingSalesOrderRows.do";
            	}
            }
            this.billid=this.record.data.billid;
            this.Grid.getStore().proxy.conn.url = this.subGridStoreUrl;
            this.Grid.getStore().load({params:{bills:this.billid}});
    },
    
    addGrid:function(){
        this.ProductGrid=new Wtf.account.ProductDetailsGrid({
            height: 200,//region:'center',//Bug Fixed: 14871[SK]
            layout:'fit',
            title: 'Inventory',
            border:true,
            id:this.id+"editproductdetailsgrid",
            viewConfig:{forceFit:true},
            isCustomer:this.isCustomer,
            currencyid:this.currencyid,
            fromOrder:true,
            isOrder:this.isOrder,
            forceFit:true,
            loadMask : true
        });

        if(this.isCustBill){  //Without Inventory.[PS]
//            if(this.isCustomer){
                 this.Grid=new Wtf.account.BillingProductDetailsGrid({
                    height: 200,//region:'center',//Bug Fixed: 14871[SK]
                    cls:'gridFormat',
                    layout:'fit',
                    viewConfig:{forceFit:true},
                    isCustomer:this.isCustomer,
                    editTransaction:this.isEdit,
                    isCustBill:this.isCustBill,
                    id:this.id+"billingproductdetailsgrid",
                    currencyid:this.Currency.getValue(),
                    fromOrder:true,
                    isOrder:this.isOrder,
                    forceFit:true,
                    loadMask : true
                });
//            }else{ //Add this code if ixpense invoice needed in non-inventory[PS]
//                this.ProductGrid=new Wtf.account.BillingProductDetailsGrid({
//                    height: 200,//region:'center',//Bug Fixed: 14871[SK]
//                    border:true,
//                    title: 'Inventory',
//                    viewConfig:{forceFit:true},
//                    isCustomer:this.isCustomer,
//                    editTransaction:this.isEdit,
//                    isCustBill:this.isCustBill,
//                    id:this.id+"billingproductdetailsgrid",
//                    currencyid:this.Currency.getValue(),
//                    fromOrder:true,
//                    isOrder:this.isOrder,
//                    closable: false,
//                    forceFit:true,
//                    loadMask : true
//                });
//               this.ExpenseGrid=new Wtf.account.ExpenseInvoiceGrid({
//                    height: 200,
//                    border:true,
//                    title: 'Expense',
//                    viewConfig:{forceFit:true},
//                    isCustomer:this.isCustomer,
//                    editTransaction:this.isEdit,
//                    isCustBill:this.isCustBill,
//                    id:this.id+"expensegrid",
//                    currencyid:this.Currency.getValue(),
//                    fromOrder:true,
//                    closable: false,
//                    isOrder:this.isOrder,
//                    forceFit:true,
//                    loadMask : true
//                });
//                this.GridPanel= new Wtf.TabPanel({
//                    id : this.id+'invoicegrid',
//                    iconCls:'accountingbase coa',
//                    border:false,
//                    style:'margin:10px;',
//                    cls:'invgrid',
//                    activeTab:0,
//                    height: 200,
//                    items: [this.ExpenseGrid,this.ProductGrid]
//                });
//                this.Grid = Wtf.getCmp(this.id+"expensegrid");
//                this.ProductGrid.on('datachanged',this.updateSubtotal,this);
//                this.ProductGrid.on("activate", function(){//alert("A")
//                    this.Grid = Wtf.getCmp(this.id+"billingproductdetailsgrid");
//                    this.isExpenseInv=false;
//                    this.applyCurrencySymbol();
//                    if(this.southCalTemp.body!=undefined)
//                        this.tplSummary.overwrite(this.southCalTemp.body,{subtotal:WtfGlobal.addCurrencySymbolOnly(this.Grid.calSubtotal(),this.symbol),discount:WtfGlobal.addCurrencySymbolOnly(this.getDiscount(),this.symbol),totalamount:WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount(),this.symbol),tax:WtfGlobal.addCurrencySymbolOnly(this.caltax(),this.symbol),aftertaxamt:WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount()+this.caltax(),this.symbol)});
//                }, this);
//                this.ExpenseGrid.on("activate", function(){//alert("B")
//                    this.Grid = Wtf.getCmp(this.id+"expensegrid");
//                    this.isExpenseInv=true
//                    this.applyCurrencySymbol();
//                    if(this.southCalTemp.body!=undefined)
//                        this.tplSummary.overwrite(this.southCalTemp.body,{subtotal:WtfGlobal.addCurrencySymbolOnly(this.Grid.calSubtotal(),this.symbol),discount:WtfGlobal.addCurrencySymbolOnly(this.getDiscount(),this.symbol),totalamount:WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount(),this.symbol),tax:WtfGlobal.addCurrencySymbolOnly(this.caltax(),this.symbol),aftertaxamt:WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount()+this.caltax(),this.symbol)});
//                }, this);
//            }
        }else{    //With Inventory[PS]
//            if(this.isEdit && !this.isOrder){
//                this.Grid=new Wtf.account.ProductDetailsGrid({
//                    height: 200,//region:'center',//Bug Fixed: 14871[SK]
//                    cls:'gridFormat',
//                    layout:'fit',
//                    id:this.id+"productdetailsgrid",
//                    viewConfig:{forceFit:true},
//                    autoScroll:true,
//                    editTransaction:true,
//                    record:this.record,
//                    copyInv:this.copyInv,
//                    fromPO:false,
//                    //readOnly:true,
//                    isCN:false,
//                    isCustomer:this.isCustomer,
//                    loadMask : true
//                });
//            }
//            else{
                if(this.isCustomer||this.isOrder||this.isEdit){
                    this.Grid=new Wtf.account.ProductDetailsGrid({
                        height: 300,//region:'center',//Bug Fixed: 14871[SK]
                        cls:'gridFormat',
                        layout:'fit',
                        id:this.id+"editproductdetailsgrid",
                        viewConfig:{forceFit:true},
                        record:this.record,
                        isCustomer:this.isCustomer,
                        currencyid:this.currencyid,
                        fromPO:this.isOrder,
                        isOrder:this.isOrder,
                        forceFit:true,
                        editTransaction: this.isEdit,
                        loadMask : true
                    });
                  }else{
//                      this.ProductGrid=new Wtf.account.ProductDetailsGrid({
//                        height: 200,//region:'center',//Bug Fixed: 14871[SK]
//                        layout:'fit',
//                        title: 'Inventory',
//                        border:true,
//                        id:this.id+"editproductdetailsgrid",
//                        viewConfig:{forceFit:true},
//                        isCustomer:this.isCustomer,
//                        currencyid:this.currencyid,
//                        fromOrder:true,
//                        isOrder:this.isOrder,
//                        forceFit:true,
//                        loadMask : true
//                    });
                   this.ExpenseGrid=new Wtf.account.ExpenseInvoiceGrid({
                        height: 200,
                        border:true,
                        title: 'Expense',
                        viewConfig:{forceFit:true},
                        isCustomer:this.isCustomer,
                        editTransaction:this.isEdit,
                        isCustBill:this.isCustBill,
                        id:this.id+"expensegrid",
                        currencyid:this.Currency.getValue(),
                        fromOrder:true,
                        closable: false,
                        isOrder:this.isOrder,
                        forceFit:true,
                        loadMask : true
                    });
                    this.GridPanel= new Wtf.TabPanel({
                        id : this.id+'invoicegrid',
                        iconCls:'accountingbase coa',
                        border:false,
                        style:'margin:10px;',
                        cls:'invgrid',
                        activeTab:0,
                        height: 200,
                        items: [this.ProductGrid,this.ExpenseGrid]
                    });
                    this.Grid = Wtf.getCmp(this.id+"editproductdetailsgrid");
                    this.ExpenseGrid.on('datachanged',this.updateSubtotal,this); 
                    this.ProductGrid.on('datachanged',this.updateSubtotal,this);                    
                    if(this.symbol==undefined)this.symbol=WtfGlobal.getCurrencySymbol();
                    this.isExpenseInv=false; //work fine in case of 2 tabs
                    this.GridPanel.on('beforetabchange', this.beforeTabChange,this);
                }
            //}
        }



//        this.Name.on('select',this.setTerm,this)
//        this.NorthForm.on('render',this.setDate,this);
        this.Grid.on('datachanged',this.updateSubtotal,this);        
        this.Grid.getStore().on('load',function(store){
            this.updateSubtotal();
            this.Grid.addBlank(store);
            if(this.isEdit){
                if(this.record.data.externalcurrencyrate!=undefined){
                    this.externalcurrencyrate=this.record.data.externalcurrencyrate;
                    this.updateFormCurrency();
                }
            }
        }.createDelegate(this),this);

    },
    
    beforeTabChange:function(a,newTab,currentTab){
    	if(currentTab!=null && newTab!=currentTab){
             Wtf.MessageBox.confirm("Save Data","Switching to "+(this.isExpenseInv?"Inventory":"Expense")+" section will empty the data filled so far in "+(this.isExpenseInv?"Expense":"Inventory")+" section. Do you wish to continue?",function(btn){
              if(btn=="yes") {
                a.suspendEvents();
                a.activate(newTab);
                this.Discount.setValue(0);
                this.isTaxable.setValue("No");
                this.Tax.setValue("");
                this.Tax.disable();
                a.resumeEvents();
                this.onGridChange(newTab,currentTab);
              }
             }.createDelegate(this),this)
            return false;
        }
        else{
            return true;
        }
   }, 

    onGridChange:function(newTab){
        this.Grid.getStore().removeAll();
        this.Grid.addBlankRow();
            this.Grid = newTab;
            this.Tax.store=this.Grid.taxStore;
            this.isExpenseInv=!this.isExpenseInv; //work fine in case of 2 tabs 
//            if(!this.isCustBill&&!this.isExpenseInv&&this.Grid.priceStore.getCount()==0)
//                this.Grid.priceStore.load({params:{transactiondate:WtfGlobal.convertToGenericDate(this.billDate.getValue())}});
            this.applyCurrencySymbol();
            if(this.southCalTemp.body!=undefined)
                 this.tplSummary.overwrite(this.southCalTemp.body,{subtotal:WtfGlobal.addCurrencySymbolOnly(this.Grid.calSubtotal(),this.symbol),discount:WtfGlobal.addCurrencySymbolOnly(this.getDiscount(),this.symbol),totalamount:WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount(),this.symbol),tax:WtfGlobal.addCurrencySymbolOnly(this.caltax(),this.symbol),aftertaxamt:WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount()+this.caltax(),this.symbol)});
    },
    caltax:function(){
        var totalamount=this.calTotalAmount();
        var rec= this.Grid.taxStore.getAt(this.Grid.taxStore.find('prtaxid',this.Tax.getValue()));
        var taxamount=(rec==null?0:(totalamount*rec.data["percent"])/100);
        return taxamount;
     },
    addAccount: function(store){
        callCOAWindow(false,null,"coaWin",this.isCustomer,false,false,false,false,false,true);
        Wtf.getCmp("coaWin").on('update',function(){store.reload();},this);
    },
    addOrder:function(){
        var tabid = "ordertab";
        if(this.isCustomer){
            if(this.isCustBill) {
                tabid = "bsalesorder";
                callBillingSalesOrder(false,null,tabid);
            } else {
                tabid = "salesorder";
                callSalesOrder(false,null,tabid);
            }
        }else{
            if(this.isCustBill) {
                tabid = "bpurchaseorder";
                callBillingPurchaseOrder(false,null,tabid);
            } else {
                tabid = "purchaseorder";
                callPurchaseOrder(false,null,tabid);
            }
        }
        if(Wtf.getCmp(tabid)!=undefined) {
            Wtf.getCmp(tabid).on('update',function(){this.POStore.reload();},this);
        }
    },
    showGridTax:function(c,rec,val){
        var hide=(val==null||undefined?!rec.data['value']:val) ;
        var id=this.Grid.getId()
        var rowtaxindex=this.Grid.getColumnModel().getIndexById(id+"prtaxid");
        var rowtaxamountindex=this.Grid.getColumnModel().getIndexById(id+"taxamount");
        this.Grid.getColumnModel().setHidden( rowtaxindex,hide) ;
        this.Grid.getColumnModel().setHidden( rowtaxamountindex,hide) ;
        if(!this.isEdit){
            this.Grid.getStore().each(function(rec){
                rec.set('prtaxid','')
                rec.set('taxamount',0)
            },this);
        }
         if(hide) {
        	 this.Grid.getStore().each(function(rec){
                 rec.set('prtaxid','')
                 rec.set('taxamount',0)
             },this);
         this.updateSubtotal();
         }
  },
    
//    enablePO:function(c,rec){
//        if(rec.data['value']==true){
//            if(!this.isCustBill&&!this.isCustomer&&!this.isEdit&&!this.copyInv){//this.isExpenseInv=false;
//                this.GridPanel.setActiveTab(this.ProductGrid);
//                this.ExpenseGrid.disable();
//
//            }
//            this.POStore.load();
//            this.PO.enable();
//            this.fromOrder=true;
//        }
//        else{
//            this.loadStore();
//            this.ExpenseGrid.enable();
//            this.setDate();
//        }
//        this.currencyStore.load(); 	       // Currency id issue 20018
//    },
    enabletax:function(c,rec){
        if(rec.data['value']==true)           
           this.Tax.enable();                    
        else{
            this.Tax.disable();
            this.Tax.setValue("");           
        }
        this.updateSubtotal();
    },
//    populateData:function(c,rec) {
//        this.Grid.fromPO=true;
//        this.includeProTax.setValue(true)
//        this.showGridTax(null,null,false);
//        this.Memo.setValue(rec.data['memo']);
//        this.Name.setValue(rec.data['personid']);
//        if(rec.data['taxid']!=""){
//            this.Tax.enable();
//            this.isTaxable.setValue(true);
//            this.Tax.setValue(rec.data['taxid']);
//        }else{
//            this.Tax.disable();
//            this.isTaxable.reset();
//            this.Tax.reset();
//        }
//        this.getCreditTo(rec.data.creditoraccount);
//        this.Currency.setValue(rec.data['currencyid']);
//        var perstore=this.isCustomer? Wtf.customerAccStore:Wtf.vendorAccStore
//        var storerec=perstore.getAt(perstore.find('accid',rec.data['personid']));
//        this.billTo.setValue(storerec.data['billto']);
//        this.Term.setValue(storerec.data['termdays']);
////        this.CostCenter.setValue(rec.data.costcenterid);
//        this.updateDueDate();
//        var url = "";
//		//(this.isCustBill?53:43)
//		if(this.isCustomer){
//            url = this.isCustBill?"ACCSalesOrderCMN/getBillingSalesOrderRows.do":'ACCSalesOrderCMN/getSalesOrderRows.do';
//        } else {
//            url = this.isCustBill?"ACCPurchaseOrderCMN/getBillingPurchaseOrderRows.do":'ACCPurchaseOrderCMN/getPurchaseOrderRows.do';
//        }
//		this.Grid.getStore().proxy.conn.url = url;
//        this.Grid.loadPOGridStore(rec);
//    },

    setTerm:function(c,rec,ind){
        this.Term.setValue(rec.data['termdays']);
        this.updateDueDate();
    },
    updateSubtotal:function(a,val){        
        if(this.calDiscount())return;
        this.isClosable=false; // Set Closable flag after updating grid data
        this.applyCurrencySymbol();
        this.tplSummary.overwrite(this.southCalTemp.body,{subtotal:WtfGlobal.addCurrencySymbolOnly(this.Grid.calSubtotal(),this.symbol),discount:WtfGlobal.addCurrencySymbolOnly(this.getDiscount(),this.symbol),totalamount:WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount(),this.symbol),tax:WtfGlobal.addCurrencySymbolOnly(this.caltax(),this.symbol),aftertaxamt:WtfGlobal.addCurrencySymbolOnly(this.calTotalAmount()+this.caltax(),this.symbol)});
    },
    getDiscount:function(){
        var disc=this.Discount.getValue();
        var per=this.perDiscount.getValue();
        return isNaN(parseFloat(disc))?0:(per?(disc*this.Grid.calSubtotal())/100:disc);
    },
    calDiscount:function(){
        var disc=this.Discount.getValue();
        var per=this.perDiscount.getValue();
        if(per && disc > 100){
            WtfComMsgBox(27,2);
            this.NorthForm.getForm().setValues({perdiscount:false});
            return true;
        }
        else
            return false;
    },
    calTotalAmount:function(){
        var subtotal=this.Grid.calSubtotal();
        var discount=this.getDiscount();
        return subtotal-discount;
    },
    save:function(){
       var incash=false;
        this.Number.setValue(WtfGlobal.HTMLStripper(this.Number.getValue().trim()));
        if(this.Number.getValue().trim()=="")
        {   this.Number.setValue("");
        	this.Number.allowBlank=false;
        	return;
        }
        this.Memo.setValue(WtfGlobal.HTMLStripper(this.Memo.getValue().trim()));
        if(this.Memo.getValue().trim()=="")
        {   this.Memo.setValue("");
        }
        this.billTo.setValue(WtfGlobal.HTMLStripper(this.billTo.getValue().trim()));
        
        if(this.NorthForm.getForm().isValid()){
            var count=this.Grid.getStore().getCount();
            if(count<=1){
                WtfComMsgBox(33, 2);
                return;
            }
//            if(this.getDiscount()>this.Grid.calSubtotal()){
//                WtfComMsgBox(12, 2);
//                return;
//            }
//            var datediff=new Date(this.billDate.getValue()).getElapsed(this.DueDate.getValue());
//            if(datediff==0)
//                  incash=true;
//              else
                  incash=this.cash;
            var rec=this.NorthForm.getForm().getValues();
			this.ajxurl = "";
            if(this.doctype==1) { //Quotation/Proposal
                this.ajxurl = Wtf.req.springBase+"common/quotation/saveQuotation.do";
            }
            if(this.Name.getValue().trim().length==0) {
                WtfComMsgBox(['Warning',"Please enter valid customer name."],2);
                return;
            }
//			if(this.businessPerson=="Customer"){
//                //(this.isOrder?(this.isCustBill?51:41):(this.isCustBill?13:11))
//                this.ajxurl = "ACC" + (this.isOrder?(this.isCustBill?"SalesOrder/saveBillingSalesOrder":"SalesOrder/saveSalesOrder"):(this.isCustBill?"Invoice/saveBillingInvoice":"Invoice/saveInvoice")) + ".do";
//            }else if(this.businessPerson=="Vendor"){
//                this.ajxurl = "ACC"+ (this.isOrder?(this.isCustBill?"PurchaseOrder/saveBillingPurchaseOrder":"PurchaseOrder/savePurchaseOrder"):(this.isCustBill?"GoodsReceipt/saveBillingGoodsReceipt":"GoodsReceipt/saveGoodsReceipt")) +".do";
//            }
//            var currencychange=this.Currency.getValue()!=WtfGlobal.getCurrencyID()&&this.Currency.getValue()!=""&&!this.isOrder;
//            var msg=currencychange?"Currency rate you have applied cannot be changed again. ":"";
            Wtf.MessageBox.confirm("Save Data","Do you want to save the record?",function(btn){
                if(btn!="yes") { return; }
                rec.taxid=this.Tax.getValue();
                rec.taxamount=this.caltax();
                if(this.isEdit){
                    rec.detail=this.Grid.getProductDetails();
                    //rec.isExpenseInv=this.isExpenseInv; 
                }
                else
                    rec.detail=this.Grid.getProductDetails();
                if((rec.detail!=undefined&&rec.detail!="[]")||(rec.expensedetail!=undefined&&rec.expensedetail!="[]")){
//                    this.msg= WtfComMsgBox(27,4,true);
                    rec.subTotal=this.Grid.calSubtotal()
                    this.applyCurrencySymbol();
                    rec.perdiscount=this.perDiscount.getValue();
                    rec.currencyid=this.Currency.getValue();
                    rec.externalcurrencyrate=this.externalcurrencyrate;
                    rec.discount=this.Discount.getValue();
                    rec.discountAmount=this.getDiscount();
                    rec.isTaxable=this.isTaxable.getValue();
                    rec.taxid=this.Tax.getValue();
                    rec.isProductTax=this.includeProTax.getValue();
                    rec.taxAmount=this.caltax();
                    rec.customer=this.NameID.getValue();
    //                rec.vendorinvoice = this.vendorInvoice!=null?this.vendorInvoice.getValue():'';
                    rec.number=this.Number.getValue();
                    rec.duedate=this.DueDate.getValue().getTime();
                    rec.moduleid=this.moduleid;
                    rec.billdate=this.billDate.getValue().getTime();
                    rec.shipdate=this.billDate.getValue().getTime();;
                    rec.invoiceid=this.copyInv?"":this.billid;
                    rec.billto=this.billTo.getValue();
                    rec.templateid = this.templateID.getValue();
                    rec.mode=(this.isOrder?(this.isCustBill?51:41):(this.isCustBill?13:11));
                    rec.incash=incash;
                    

                    Wtf.Ajax.requestEx({
                        url:this.ajxurl,
    //                    url: Wtf.req.account+this.businessPerson+'Manager.jsp',
                        params: rec
                    },this,this.genSuccessResponse,this.genFailureResponse);
                }
                else
                    WtfComMsgBox(['Warning',"Product(s) details are not valid."],2);
            },this); 
        }else{
            WtfComMsgBox(2, 2);
        }
    },
    setNameID:function(combo,nval,oval){
    	this.NameID.setValue(nval);
    },
    setTemplateID:function(combo,nval,oval){
    	this.templateID.setValue(nval);
    },
    updateData:function(){
        var customer=this.Name.getValue();
        Wtf.Ajax.requestEx({
            url:"ACC"+this.businessPerson+"/getAddress.do",
//            url:Wtf.req.account+this.businessPerson+'Manager.jsp',
            params:{
                mode:4,
                customerid:customer
            }
        }, this,this.setAddress);
    },
//    setAddress:function(response){
//        if(response.success){
//            this.externalcurrencyrate=0;
//            this.custdatechange=true;
//            this.billTo.setValue(response.billingAddress);
//            this.Currency.setValue(response.currencyid);
//            this.currencyid=response.currencyid;
//            this.custChange=true;
//            this.changeCurrencyStore();
//
//            if(this.fromPO)					// Currency id issue 20018
//            	this.currencyStore.load();
//        }
//    },
//    getTerm:function(val1,val2){
//        val1=new Date(this.record.data.date);
//        val2=new Date(this.record.data.duedate);
//        var msPerDay = 24 * 60 * 60 * 1000
//        var termdays = Math.floor((val2-val1)/ msPerDay) ;
//        var FIND =termdays;
//        var index=this.termds.findBy( function(rec){
//             var parentname=rec.data.termdays;
//            if(parentname==FIND)
//                return true;
//             else
//                return false
//            })
//            if(index>=0){
//                var  rec=this.termds.getAt(index)
//                this.Term.setValue(rec.data.termdays);
//            }
//    },
//    getCreditTo:function(val){
//        var index=this.allAccountStore.findBy( function(rec){
//             var name=rec.data.accid;
//            if(name==val)
//                return true;
//             else
//                return false
//            },this)
//            if(index>=0)
//                this.creditTo.setValue(val);
//    },
    updateDueDate:function(a,val){
        var term=null;
        if(this.Term.getValue()!=""&&isNaN(this.Term.getValue())==false){

            term=new Date(this.billDate.getValue()).add(Date.DAY, this.Term.getValue());}
        else
            term=this.billDate.getValue();
        this.NorthForm.getForm().setValues({duedate:term});
    },

    genSuccessResponse:function(response){
        WtfComMsgBox(["Success",response.msg],response.success*2+1);
         if(response.success){ 
        	 //this.record.store.reload();
        	 if(!this.isEdit)
            this.loadStore();
            this.externalcurrencyrate=0; //Reset external exchange rate for new Transaction.
            this.isClosable= true;       //Reset Closable flag to avoid unsaved Message.
//            Wtf.dirtyStore.product = true;
            this.fireEvent('update',this);
//            if(!this.isCustBill)
////            	this.Grid.productComboStore.reload();
//            	Wtf.productStore.reload();   //Reload all product information to reflect new quantity, price etc
         }
    },

    genFailureResponse:function(response){
        Wtf.MessageBox.hide();
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        WtfComMsgBox(['Alert',msg],2);
    },

    loadStore:function(){
//        if(!(this.isCustBill||this.isExpenseInv))
//            this.Grid.priceStore.purgeListeners();
        this.Grid.getStore().removeAll();
        this.setTransactionNumber();
        this.PO.setDisabled(true);
        this.NorthForm.getForm().reset();
        this.fromPO.setValue(false);
//        this.POStore.reload();			Code Optimizing :)  Unnecessary Reload removed
        this.Grid.getStore().removeAll();
        this.Tax.setValue("");
        this.Tax.setDisabled(true);				// 20148 fixed
        this.isTaxable.setValue("No");
        this.showGridTax(null,null,true);
        this.Grid.symbol=undefined; // To reset currency symbol. BUG Fixed #16202
        this.Grid.updateRow(null);
        this.resetForm = true;
//        var date=WtfGlobal.convertToGenericDate(new Date())
        if(!(this.isCustBill||this.isExpenseInv)) {
//            this.Grid.loadPriceStoreOnly(new Date(),this.Grid.priceStore);
        }
        else
            this.currencyStore.load({params:{tocurrencyid:WtfGlobal.getCurrencyID(),mode:201,transactiondate:WtfGlobal.convertToGenericDate(new Date())}});
        this.currencyStore.on("load",function(store){
            if(this.resetForm){
                if(this.currencyStore.getCount()<1){
                    callCurrencyExchangeWindow();
                    WtfComMsgBox(['Alert',"Please set Currency Exchange Rates"],2);
                } else {
                    this.isCurrencyLoad=true;
                    this.Currency.setValue(WtfGlobal.getCurrencyID());
                    this.currencyid=WtfGlobal.getCurrencyID();
                    this.applyCurrencySymbol();
                    this.isTaxable.setValue("No");
                    this.showGridTax(null,null,true);
                    this.Tax.setValue("");
                    this.applyTemplate(this.currencyStore,0);
                    this.resetForm = false;
                }
            }
        },this);
},

//    setDate:function(){
//        var height = 0;
//        if(this.isOrder)
//            height=172;
//        if(this.isCustBill){
//        	if(this.isEdit)
//                this.allAccountStore.on('load',this.getCreditTo.createDelegate(this,[this.record.data.crdraccid]),this)
//            this.allAccountStore.load();
//            height+=20;
//        }
//        if(height>=172) this.NorthForm.setHeight(height);
//
//        if(!this.isEdit){
//            this.Discount.setValue(0);
//
//            this.billDate.setValue(Wtf.serverDate);//(new Date());
//            this.DueDate.setValue(new Date());
//        }
//    },
    addTax:function(){
         var p= callTax("taxwin");
         Wtf.getCmp("taxwin").on('update', function(){
        	 this.Grid.taxStore.reload();
        	 }, this); 
    },
//    addCreditTerm:function(){
//        callCreditTerm('credittermwin');
//        Wtf.getCmp('credittermwin').on('update', function(){this.termds.reload();}, this);
//    },
//    addPerson:function(isEdit,rec,winid,isCustomer){
//        callBusinessContactWindow(isEdit, rec, winid, isCustomer);
//        Wtf.getCmp(winid).on('update', function(){
//           this.isCustomer?Wtf.customerAccStore.reload():Wtf.vendorAccStore.reload();
//        }, this);
//    },
//
//    addCostCenter:function(){
//        callCostCenter('addCostCenterWin');
//    },
    setTransactionNumber:function(isEdit){
//        if(!this.isEdit||this.copyInv){
//            var temp=this.isCustBill*1000+this.isCustomer*100+this.isOrder*10+this.cash*1;
//            var temp2=0;
//            var format="";
//            switch(temp){
//                case 0:format=Wtf.account.companyAccountPref.autogoodsreceipt;
//                    temp2=Wtf.autoNum.GoodsReceipt;
//                    break;
//                case 1:format=Wtf.account.companyAccountPref.autocashpurchase;
//                    temp2=Wtf.autoNum.CashPurchase;
//                    break;
//                case 10:format=Wtf.account.companyAccountPref.autopo;
//                    temp2=Wtf.autoNum.PurchaseOrder;
//                    break;
//                case 100:format=Wtf.account.companyAccountPref.autoinvoice;
//                    temp2=Wtf.autoNum.Invoice;
//                    break;
//                case 101:format=Wtf.account.companyAccountPref.autocashsales;
//                    temp2=Wtf.autoNum.CashSale;
//                    break;
//                case 110:format=Wtf.account.companyAccountPref.autoso;
//                    temp2=Wtf.autoNum.SalesOrder;
//                    break;
//                case 1000:format=Wtf.account.companyAccountPref.autobillinggoodsreceipt;
//                    temp2=Wtf.autoNum.BillingGoodsReceipt;
//                    break;
//                case 1001:format=Wtf.account.companyAccountPref.autobillingcashpurchase;
//                    temp2=Wtf.autoNum.BillingCashPurchase;
//                    break;
//                case 1010:format=Wtf.account.companyAccountPref.autobillingpo;
//                    temp2=Wtf.autoNum.BillingPurchaseOrder;
//                    break;
//                case 1100:format=Wtf.account.companyAccountPref.autobillinginvoice;
//                    temp2=Wtf.autoNum.BillingInvoice;
//                    break;
//                case 1101:format=Wtf.account.companyAccountPref.autobillingcashsales;
//                    temp2=Wtf.autoNum.BillingCashSale;
//                    break;
//                case 1110:format=Wtf.account.companyAccountPref.autobillingso;
//                    temp2=Wtf.autoNum.BillingSalesOrder;
//                    break;
//            }
//
//            if(format&&format.length>0){
    			if(isEdit){
    				var data=this.record.data;
    				this.Number.setValue(data.billno);
    			}else{
                var temp2=50;
                WtfGlobal.fetchAutoNumber(temp2, function(resp){this.Number.setValue(resp.data)}, this);
    			}
//            }
//        }
    },
//    dueDateCheck:function(){
//        if(this.DueDate.getValue().getTime()<this.billDate.getValue().getTime()){
//           WtfComMsgBox(["Warning", "The Due Date should be greater than the Order Date."], 2);
//           this.DueDate.setValue(this.billDate.getValue());
//        }
//    },

    initForClose:function(){
        this.cascade(function(comp){
            if(comp.isXType('field')){
                comp.on('change', function(){this.isClosable=false;},this);
            }
        },this);
    },

    loadTemplateStore : function() {
        this.templateStore.load();
    },
    addInvoiceTemplate : function(isCreatedNow,tempid) {
        if(isCreatedNow===true){
            this.loadTemplateStore();
            this.templateStore.on("load",function(){
            	this.template.setValue(tempid);
            	this.templateID.setValue(tempid);
            },this)
            	
        }else{
        new Wtf.selectTempWin({
            isreport : false,
            tabObj : this,
            templatetype : 1 // for quotation
//            type:type,
//            cd:(frm!="")?1:null,
//            name:name,
//            titlename:obj.titlename,
//            fromdate:frm,
//            ss : (obj.quickSearchTF!=undefined) ? obj.quickSearchTF.getValue() : "",
//            todate:to,
//            year:obj.yearCombo != undefined ? obj.yearCombo.getValue() : null,
//            storeToload:obj.pdfStore,
//            gridConfig : jsonGrid,
//            grid:obj.EditorGrid,
//            heading:(obj.RelatedRecordName!=undefined) ? obj.RelatedRecordName : "",
//            mapid:mapid,
//            flag:flag,
//            reportFlag:obj.reportFlag!=undefined?obj.reportFlag:"0",
//            tabObj : obj,
//            url:url==undefined?"../../exportmpx.jsp":url,
//            selectExport:selectionJson,
//            field:sortField,
//            dir:sortDir,
//            comboName:filterComboName,
//            comboValue:filterComboValue,
//            comboDisplayValue:comboDisplayValue,
//            TLID:obj.TLID!=undefined ? obj.TLID : null,
//            json:(obj.searchJson!=undefined)?obj.searchJson:"",
//            goalid:obj.goalid!=undefined ? obj.goalid : "",
//            userCombo:obj.userCombo!=undefined ?obj.userCombo.getValue():"",
//            emailmarketid:obj.emailmarketid!=undefined ? obj.emailmarketid : "",
//            bouncereportcombo:obj.reportTypeCombo!=undefined ?obj.reportTypeCombo.getValue():""
        });
        }
    }
}); 
