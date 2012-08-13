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


Wtf.TaxUpdateWindow = function(config){
    var btnArr=[];
//     if(config.mode==32) {
//            this.uPermType=Wtf.UPerm.uom;
//            this.permType=Wtf.Perm.uom;
//        } else if(config.mode==34) {
//            this.uPermType=Wtf.UPerm.tax;
//            this.permType=Wtf.Perm.tax;
//        } else if(config.mode==92) {
//            this.uPermType=Wtf.UPerm.creditterm;
//            this.permType=Wtf.Perm.creditterm;
//        } else if(config.mode==52) {
//            this.uPermType=Wtf.UPerm.paymentmethod;
//            this.permType=Wtf.Perm.paymentmethod;
////        } else if(config.mode==26) {
//        } else if(config.mode==82){
//            this.uPermType=3;
//            this.permType={edit:2, view:1};
//        }
//        if(!WtfGlobal.EnableDisable(this.uPermType, this.permType.edit)) {
            btnArr.push({
                text: WtfGlobal.getLocaleText("crm.quotaion.tax.update"),//'Update',
                scope: this,
               handler:this.addArr.createDelegate(this)
            });
        //}
    btnArr.push({
        text:WtfGlobal.getLocaleText("crm.quotaion.tax.close"), //'Close',
        scope: this,
        handler: function(){
            this.close();
        }
    });
    Wtf.apply(this,{
        buttons: btnArr
    },config);
    Wtf.TaxUpdateWindow.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.TaxUpdateWindow, Wtf.Window, {
    closable: true,
    addDeleteCol: true,
    rowDeletedIndexArr:null,
    rowIndexArr:null,
    modal: true,
    iconCls : "pwnd favwinIcon",
    width: 600,
    record:null,
    height: 450,
    resizable: false,
    layout: 'border',
    buttonAlign: 'right',
    initComponent: function(config){
	Wtf.TaxUpdateWindow.superclass.initComponent.call(this, config);
        if(this.addDeleteCol){
            this.cm.push({
                width:50,
                header:WtfGlobal.getLocaleText("crm.quotaion.tax.grid.header.action"),//'Action',
                renderer:this.deleteRenderer.createDelegate(this)
            });
        }
    },

    resetIndexArray: function() {
        this.rowDeletedIndexArr=[];
        this.rowIndexArr=[];
    },
    
    onRender: function(config){
        Wtf.TaxUpdateWindow.superclass.onRender.call(this, config);
        this.resetIndexArray();
        this.createDisplayGrid();
        this.add({
            region: 'north',
            height: 75,
            border: false,
            bodyStyle: 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(this.title+' ',/*"Create and Update "*/WtfGlobal.getLocaleText("crm.quotaion.tax.createandupdate")+' '+this.title+' '+WtfGlobal.getLocaleText("crm.quotaion.tax.details")/*" Details."*/,this.headerImage, true)
        },{
            region: 'center',
            border: false,
            baseCls:'bckgroundcolor',
            layout: 'fit',
            items:this.grid

        });
        this.addEvents({
            'update':true
        });
        this.store=this.grid.getStore();
        this.grid.on('rowclick',this.processRow,this);
        this.grid.on('afteredit',this.addGridRec,this);
        this.grid.on('validateedit',this.checkDuplicate,this);
        this.grid.on('beforeedit',this.checkrecord,this);
        this.store.on('load',this.addGridRec,this);
   },

    createDisplayGrid:function(){
//        if(!WtfGlobal.EnableDisable(this.uPermType, this.permType.edit)){
//            this.grid = new Wtf.grid.EditorGridPanel({
//                plugins:this.gridPlugins,
//                layout:'fit',
//                clicksToEdit:1,
//                store: this.store,
//                cm: new Wtf.grid.ColumnModel(this.cm),
//                border : false,
//                loadMask : true,
//                viewConfig: {
//                    forceFit:true,
//                    emptyText:WtfGlobal.emptyGridRenderer("There is no record to display.")
//                }
//            });
//        }else{
            this.grid = new Wtf.grid.EditorGridPanel({
                plugins:this.gridPlugins,
                layout:'fit',
                clicksToEdit:1,
                store: this.store,
                cm: new Wtf.grid.ColumnModel(this.cm),
                border : false,
                loadMask : true,
                viewConfig: {
                    forceFit:true,
                    emptyText:WtfGlobal.emptyGridRenderer("There is no record to display.")
                }
            });
        //}
    },
    getdeletedArr:function(grid,index,rec){
        var store=grid.getStore();
        var fields=store.fields;
            var recarr=[];
            if(rec.data['taxid']!=""){
                for(var j=0;j<fields.length;j++){
                    var value=rec.data[fields.get(j).name];
                    switch(fields.get(j).type){
                        case "auto": value="'"+value+"'"; break;
                        case "date": value="'"+WtfGlobal.convertToGenericDate(value)+"'";break;
                    }
                    recarr.push(fields.get(j).name+":"+value);
                }
                recarr.push("modified:"+rec.dirty);
                this.rowDeletedIndexArr.push("{"+recarr.join(",")+"}");
            }
},
    processRow:function(grid,rowindex,e){        
        if(e.getTarget(".delete-gridrow")){
            Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.quotaion.tax.warning"), WtfGlobal.getLocaleText("crm.quotaion.tax.warningmsg"), function(btn){
                if(btn!="yes") return;
            var store=grid.getStore();
            var rec=store.getAt(rowindex);
            Wtf.Ajax.requestEx({
                url: Wtf.req.springBase+"common/tax/getTaxRefrences.do", 
                method: 'POST',
                params: {
            			 taxid:rec.data.taxid
                }},this,
                function(response){
                	if(response.success){
                	this.getdeletedArr(grid,rowindex,rec);
                    store.remove(store.getAt(rowindex));
                    this.addGridRec();
                	}else{
                		 var msg=response.msg.substring(0,response.msg.length-2);
                		 WtfComMsgBox([WtfGlobal.getLocaleText("crm.quotaion.tax.success"),WtfGlobal.getLocaleText("crm.quotaion.tax.successmsg1")+Wtf.util.Format.ellipsis(msg,100)],0);
                		grid.getStore().load();
                	}
                },
                function(response) {
                   
            });
            
            }, this);
        }
    },
    checkrecord:function(obj){
        if(this.istax){
            var idx = this.grid.getStore().find("taxid", obj.record.data["taxid"]);
            if(idx>=0)
                obj.cancel=true;
        }
    },

     checkDuplicate:function(obj){
        if(this.istax &&obj.field=="taxname"){
        	obj.value = WtfGlobal.HTMLStripper(obj.value);
        	if(obj.value=="")
        		return;
           var FIND = obj.value;
            FIND =FIND.replace(/\s+/g, '');
            var index=this.grid.getStore().findBy( function(rec){
            var taxname=rec.data['taxname'].trim();
            taxname=taxname.replace(/\s+/g, '');
            if(taxname==FIND)
                return true;
            else
                return false
        })
        if(index>=0){
                obj.cancel=true;
        }
        }
    },
    addArr:function(){
        var inValidRows = new Array();
        var cm = this.grid.getColumnModel();

        var editedarr=[];
         for(var i=0;i<this.store.getCount();i++){
             var   rec=this.store.getAt(i);
            if(rec.dirty){
                editedarr.push(i);

                for(var j=0;j<cm.getColumnCount();j++){
                    var editor = cm.getCellEditor(j,i);
                    var cellData = ""+rec.data[cm.getDataIndex(j)];
                    if(editor != undefined && editor.field.allowBlank !=undefined && !editor.field.allowBlank && cellData.trim().length == 0){
                        inValidRows.push(i+1);
                        break;
                    }
                }
            }
        }

        if(inValidRows.length>0){
            WtfComMsgBox([WtfGlobal.getLocaleText("crm.quotaion.tax.alert"),WtfGlobal.getLocaleText("crm.quotaion.tax.alertmsg1")+(inValidRows.join(","))], 2);
            return;
        }

        this.rowIndexArr=editedarr;
        this.update(editedarr);
    },
    addGridRec:function(){ 
        var size=this.store.getCount();
        if(size>0){
            var lastRec=this.store.getAt(size-1);
            var cm=this.grid.getColumnModel();
            var count = cm.getColumnCount();
            for(var i=0;i<count-1;i++){
                if(lastRec.data[cm.getDataIndex(i)].length<=0)
                    return;
            }
        }
        var rec=this.record;
        rec = new rec({});
        rec.beginEdit();
        var fields=this.store.fields;
        for(var x=0;x<fields.length;x++){
            var value="";
            rec.set(fields.get(x).name, value);
        }      
        rec.endEdit();
        rec.commit();
        this.store.add(rec);
    },

    deleteRenderer:function(v,m,rec){
        var flag=true;
        var cm=this.grid.getColumnModel();
        var count = cm.getColumnCount();
        for(var i=0;i<count-1;i++){
            if(rec.data[cm.getDataIndex(i)].length<=0){
                flag=false;
                break;
            }
        }
        if(flag){
             // var deletegriclass=getButtonIconCls(Wtf.etype.deletegridrow);
            return "<div class='pwnd  delete-gridrow'></div>";
        }
        return "";
    }, 

    update:function(arr){
        var rec;
        rec={
            data:this.getJSONArray(arr),
            deleteddata:"["+this.rowDeletedIndexArr.join(',')+"]",
            applydate:this.getJSONArray(arr)!="[]"?new Date(eval(this.getJSONArray(arr))[0].applydate).getTime():''
        };

       // this.ajxUrl = Wtf.req.account+'CompanyManager.jsp';
        if(this.mode==34) {
        	this.ajxUrl = Wtf.req.springBase+"common/tax/saveTax.do";  
        }
        this.resetIndexArray();
//        else if(this.mode==32) {
//        	this.ajxUrl = "ACCUoM/saveUnitOfMeasure.do";
//        } else if(this.mode==92) {
//            this.ajxUrl = "ACCTerm/saveTerm.do";
//        } else if(this.mode==52) {
//            this.ajxUrl = "ACCPaymentMethods/savePaymentMethod.do";
//        } else if(this.mode==26) {
//            this.ajxUrl = "ACCProduct/saveProductTypes.do";
//        } else if(this.mode==82) {
//            this.ajxUrl = "CostCenter/saveCostCenter.do";
//        }

       if(rec.deleteddata=="[]"&&rec.data=="[]"){
           if(arr!="")
               WtfComMsgBox([WtfGlobal.getLocaleText("crm.quotaion.tax.alert"),WtfGlobal.getLocaleText("crm.quotaion.tax.alertmsg2")], 2);
           return;
       }
       else if(this.istax&&rec.deleteddata=="[]"){
             Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.quotaion.tax.savedata"),WtfGlobal.getLocaleText("crm.quotaion.tax.savedata.msg"),function(btn){
                if(btn!="yes") { return; }
                rec.mode=this.mode;
                Wtf.Ajax.requestEx({
//                    url: Wtf.req.account+'CompanyManager.jsp',
                    url : this.ajxUrl,
                    params: rec
                },this,this.genSuccessResponse,this.genFailureResponse);
            },this)
        }
        else{
            rec.mode=this.mode;
            Wtf.Ajax.requestEx({
//                url: Wtf.req.account+'CompanyManager.jsp',
                url : this.ajxUrl,
                params: rec
            },this,this.genSuccessResponse,this.genFailureResponse);
        }
        //this.close();
    },

    getJSONArray:function(arr){
        return WtfGlobal.getJSONArray(this.grid,false,arr);
    },
//TODO    gentaxSuccessResponse:function(response){
//       var rec={
//            data:this.getJSONArray(this.rowIndexArr),
//            deleteddata:"["+this.rowDeletedIndexArr.join(',')+"]"
//            };
//        if(response.msg==true&& rec.deleteddata=="[]"){
//             Wtf.MessageBox.confirm("Save Data","Tax details are not editable. Do you wish to continue?",function(btn){
//                if(btn!="yes") { return; }
//            rec.mode=this.mode;
//            Wtf.Ajax.requestEx({
//                url: Wtf.req.account+'CompanyManager.jsp',
//                params: rec
//            },this,this.genSuccessResponse,this.genFailureResponse);
//        },this)
//        }
//        else{
//            rec.mode=this.mode;
//            Wtf.Ajax.requestEx({
//                url: Wtf.req.account+'CompanyManager.jsp',
//                params: rec
//            },this,this.genSuccessResponse,this.genFailureResponse);
//        }
//    },
//
//    gentaxFailureResponse:function(response){
//        var msg="Failed to make connection with Web Server";
//        if(response.msg)msg=response.msg;
//        WtfComMsgBox(['Alert',msg],2);
//
//    },
//
    genSuccessResponse:function(response){
        WtfComMsgBox([WtfGlobal.getLocaleText("crm.quotaion.tax.success"),WtfGlobal.getLocaleText("crm.quotaion.tax.update.msg")],0);
        this.store.reload();
        if(response.success){    
            this.fireEvent('update',this);
//            if(this.mode==32) {
//                Wtf.uomStore.reload();
//            } else if(this.mode==34) {
//                Wtf.taxStore.reload();
//            } else if(this.mode==92) {
//                Wtf.termds.reload();
//            } else if(this.mode==52) {
//                //PaymentMethods
//            } else if(this.mode==26) {
//                Wtf.productTypeStore.reolad();
//            } else if(this.mode==82) {
//                if(Wtf.StoreMgr.containsKey("CostCenter")){Wtf.CostCenterStore.reload();}
//                if(Wtf.StoreMgr.containsKey("FormCostCenter")){Wtf.FormCostCenterStore.reload();}
//            }
            this.close();
        }
    },

    genFailureResponse:function(response){
       // var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        WtfComMsgBox([WtfGlobal.getLocaleText("crm.quotaion.tax.alert"),WtfGlobal.getLocaleText("crm.quotaion.tax.fail.msg")],2);
        this.close();
    }

});  


function callTax(winid){
//       var accRec=new Wtf.data.Record.create([
//           {name: 'accountid',mapping:'accid'},
//           {name: 'accountname',mapping:'accname'}
//       ]);
//       var accStore=new Wtf.data.Store({
//           reader: new Wtf.data.KwlJsonReader({
//               root: "data"
//           },accRec),
////            url: Wtf.req.account+'CompanyManager.jsp',
//           url : "ACCAccount/getAccountsForCombo.do",
//           baseParams:{
//               mode:2,
//               group:[3],
//               nondeleted:true
//           }
//       });
//       accStore.load();
   winid=(winid==null?"TaxWindow":winid);
   var p = Wtf.getCmp(winid);
   if(!p){
      var record = new Wtf.data.Record.create([
          {name: 'taxid'},
          {name: 'taxname'},
          {name: 'percent',type:'float'},
          {name: 'taxcode'},
//          {name: 'accountid'},
//          {name: 'accountname'},
          {name: 'applydate', type:'date',dateFormat:'time'}
      ]);

       var store = new Wtf.data.Store({
           reader: new Wtf.data.KwlJsonReader({
               root: "data"
           },record),
//            url: Wtf.req.account + 'CompanyManager.jsp',
           url : Wtf.req.springBase+"common/tax/getTax.do",
           baseParams:{
               mode:33
           }
       });
       store.load();

//       var cmbAccount= new Wtf.form.FnComboBox({
//           hiddenName:'accountid',
//           store:accStore,
//           valueField:'accountid',
//           displayField:'accountname',
//           forceSelection:true,
//           mode: 'local',
//           disableKeyFilter:true,
//           allowBlank:false,
//           triggerAction:'all'
           //hirarchical:true
//            addNewFn:function(){
//                callCOAWindow(false, null, "coaWin",false,false,true);
//
//Wtf.getCmp("coaWin").on("update",function(){accStore.reload()},this);
//            }
    //   });
//       if(!WtfGlobal.EnableDisable(Wtf.UPerm.coa, Wtf.Perm.coa.create)){
//
//           cmbAccount.addNewFn=function(){callCOAWindow(false, null,
//"coaWin",false,false,true);
//           Wtf.getCmp("coaWin").on("update",function(){accStore.reload()},this);
//       }
//       }
        var cm;

       cm= [{
           header:WtfGlobal.getLocaleText("crm.quotaion.tax.grid.header.taxname"),//"Name",
           dataIndex: 'taxname',
           editor: new Wtf.form.TextField({
               allowBlank: false,
               maxLength:50,
               regex:Wtf.specialChar
           })
       },{
           header:WtfGlobal.getLocaleText("crm.quotaion.tax.grid.header.taxpercent"),//"Percent",
           dataIndex: 'percent',
           renderer:function(val){
               if(typeof val != "number") return "";
               return val+'%';
           },
           editor: new Wtf.form.NumberField({
               allowBlank: false,
               maxValue:100,
               allowNegative:false,
               maxLength:50
           })
       }/*,{
           header:"Apply Date",
           dataIndex: 'applydate',
           renderer:WtfGlobal.onlyDateRenderer,
           hidden:true,
           editor:new Wtf.form.DateField({
               //value: Wtf.serverDate.clearTime(true),
               name:'applydate',
               format:WtfGlobal.getOnlyDateFormat(),
               offset:Wtf.pref.tzoffset
           })
       },{
           header:"TaxCode",
           dataIndex:'taxcode',
           editor: new Wtf.form.TextField({
               allowBlank: true,
               maxLength:50,
               regex:Wtf.specialChar
           })
      }*/]
//      {
//           header:WtfGlobal.getLocaleText("acc.masterConfig.taxes.gridAccountName"),//"Account Name",
//           dataIndex: 'accountid',
//           renderer:Wtf.comboBoxRenderer(cmbAccount),
//           editor:cmbAccount
//       }];
     new Wtf.TaxUpdateWindow({
           cm:cm,
           headerImage:"../../images/tax.gif",
           store:store,
           record:record,
           istax:true,
           mode:34,
           title:WtfGlobal.getLocaleText("crm.quotaion.grid.header.tax"),//'Taxes',
           id:winid,
           renderTo: document.body
       }).show();
   }

}





