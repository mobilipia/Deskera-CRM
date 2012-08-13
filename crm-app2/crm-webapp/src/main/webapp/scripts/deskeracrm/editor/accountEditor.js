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
Wtf.accountEditor = function (config){
    Wtf.accountEditor.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.accountEditor,Wtf.Panel,{
	getEditor:Wtf.SpreadSheetGrid.prototype.getEditor,
    initComponent: function(config){
        Wtf.accountEditor.superclass.initComponent.call(this,config);

        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
    },

    onRender: function(config){
        this.getEditorGrid();

        this.getAdvanceSearchComponent();

        this.getDetailPanel();

        this.accountpan= new Wtf.Panel({
            layout:'border',
            title:WtfGlobal.getLocaleText({key:"crm.tab.title",params:[WtfGlobal.getLocaleText("crm.ACCOUNT")]}),//"Account List",
            iconCls:getTabIconCls(Wtf.etype.account),
            border:false,
            id:this.id+'accountpan',
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
           id:this.id+"accountTabPanel",
           scope:this,
           border:false,
           resizeTabs: true,
           minTabWidth: 155,
           enableTabScroll: true,
           items:[this.accountpan]
        });

        if(this.archivedFlag==1){
            this.add(this.accountpan);
        }else{
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.accountpan);
        }
        this.accountpan.on("activate",function(){
            Wtf.getCmp("AccountHomePanelaccountpan").doLayout()
        },this)
        Wtf.accountEditor.superclass.onRender.call(this,config);
        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);

    },

    getDetails:function(){
    	var sm = this.EditorGrid.getSelectionModel();
    	if(sm.getCount()!=1)
    		return;
    	var commentlist = getDocsAndCommentList(sm.getSelected(), "accountid",this.id,undefined,"Account",undefined,"email",'accountownerid',this.contactsPermission,0);
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
    showAdvanceSearch:function(){
        showAdvanceSearch(this,this.searchparam);
    },
    reloadStore : function(obj){
           if(obj.EditorStore.baseParams && obj.EditorStore.baseParams.searchJson){
                obj.EditorStore.baseParams.searchJson="";
            }
            obj.EditorStore=obj.spreadSheet.getMySortconfig(obj.EditorStore);
            obj.EditorStore.load({
                params:{
                    start:0,
                    limit:25
                }
            });
    },

    /*
     *
     *  function reloadComboStores() called after successfully imports with master record created if not exist
     *
     */
    reloadComboStores : function() {
        Wtf.industryStore.load();
//        Wtf.noofempStore.load();
        Wtf.accountTypeStore.load();
    },
    getEditorGrid:function (){
        // 2nd Parameter -> moduleid
        // 3rd Parameter -> false when  called thru accounts tab; true when called directly thru Details Panel
        createAccountModuleConfig(this,1,false);
        this.loadCount=0;
        this.EditorGrid = this.spreadSheet.getGrid();
        this.EditorColumn = this.spreadSheet.getColModel();

       if(this.newFlag==2) {
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.manage)) {
                this.EditorGrid.on("afteredit",this.fillGridValue,this);
            }else{
                this.EditorGrid.isEditor = false;
            }
        }

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
        this.EditorStore.on("load",function(){
        	if(this.loadCount !=undefined && this.loadCount != 0)
        		this.loadCount=0;
        	this.SelChange();
            Wtf.updateProgress();
        },this);
        this.updateInfo = {
            	keyField:'accountid',
            	auditStr:"Account details updated from account profile for ",
            	url:"Account/action/updateMassAccounts.do",
            	flag:21,
            	type:"Account"
    		};

        this.gridRowClick();
        this.EditorGrid.getSelectionModel().on("rowselect",this.SelChange,this);
        this.EditorGrid.getSelectionModel().on("rowdeselect",this.SelChange,this);
        this.EditorGrid.on("sortchange",this.sortChange,this);
        this.EditorGrid.on("rowclick",this.gridCellClick,this);
        this.EditorGrid.on("mouseover",Wtf.hideNotes,this);
        this.tbarArchive.on('click',function() {
            if(this.deleteAcc.disabled==false)
                this.tbarArchiveArray[0].setTooltip(WtfGlobal.getLocaleText("crm.editor.archiveBTN.ttip"));//"Archive the selected account.");
            else
                this.tbarArchiveArray[0].setTooltip(WtfGlobal.getLocaleText("crm.editor.archiveBTN.disabled.ttip"));//"Select row(s) to send in Archive repository.");
        }, this);
        this.tbarPrint.on('mouseover',function() {
        	var s = this.EditorGrid.getSelectionModel().getSelections();
            this.printprv.menu.items.items[1].setDisabled(s.length<=0);
        }, this);

    },
    gridCellClick : function(grid, ri, e) {
        if(e.target.className == "clicktoshowcomment") {
            Wtf.onCellClickShowComments(grid.getStore().getAt(ri).data.accountid, this.customParentModName, ri, e);
        }
        if(e.target.className == "showMandatoryFields") {
            Wtf.emptyMandatoryFields(grid,ri);
        }
        this.gridRowClick();
    },
    sortChange:function(grid,sortInfo, sortFlag){
        this.sortInfo = sortInfo;
    },

    openQuotation : function() {
        openQuotation(this.mainTab, Wtf.common.accountModuleID, "Account");
    },

    storeLoad:function(){

    },

    exportfile: function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Accounts";
        var fromdate="";
        var todate="";
        var report="crm";
        var exportUrl = Wtf.req.springBase+"Account/action/accountExport.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,undefined,undefined,field,dir);
    },

    exportSelected: function(type) {
        var report="crm"
        var name="Accounts";
        var fromdate="";
        var todate="";
        var selArr = [];
        var exportUrl = Wtf.req.springBase+"Account/action/accountExport.do";
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
                jsondata+="{'id':'" + selArr[i].get('accountid') + "'},";
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

    getDetailPanel:function()
    {
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.EditorGrid,
            Store:this.EditorStore,
            modulename:'account',
            keyid:"accountid",
            height:200,
            mapid:4,
            id2:this.id,
            moduleName:'Account',
            ownerid : 'accountownerid',
            leadDetailFlag:true,
            moduleScope:this,
            detailPanelFlag:(this.archivedFlag==1?true:false),
            contactsPermission:this.contactsPermission
        });
    },
//    editDetail: function() {
//        globalMassUpdate(this.spreadSheet,{
//        	keyField:'accountid',
//        	auditStr:"Account details updated from account profile for ",
//        	url:"Account/action/updateMassAccounts.do",
//        	flag:21,
//        	type:"Account"
//        });
//    },
    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.spreadSheet.colArr,
            module : Wtf.crmmodule.account,
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
            flag:2,
            searchJson:this.searchJson,
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
            flag:2,
            searchJson:this.searchJson,
            isarchive:this.newFlag==3?true:false
        };
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
    },

    newGrid:function() {


    },
//    addNewRec:function (){
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.manage)) {
//            var gridRec={
//                validflag:-1,
//                accountid:"0",
//                accountowner:"",
//                accountownerid:'',
//                accountname:"",
//                productid:"",
//                price:"",
//                accounttype:"",
//                accounttypeid:"",
//                phone:"",
//                subowners:'',
//                createdon:'',
//                website:"",
//                email:'',
//                industry:"",
//                industryid:"",
//                revenue:"",
//                address:"",
//                description:""
//
//            };
//            this.newRec = new this.EditorRec(this.spreadSheet.getEmptyCustomFields(gridRec));
//            this.EditorStore.insert(0, this.newRec);
//        }
//    },
    fillGridValue:function (e){
        for(var i=0; i <Wtf.productStore.getCount(); i++){
            if(e.value == Wtf.productStore.getAt(i).get("id")){
                this.EditorStore.getAt(e.row).set("price",Wtf.productStore.getAt(i).get("unitprice"));
            }
        }
    },

    handleRecentAccounts:function(){
        this.EditorStore.extraSortInfo = {xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false};
        this.EditorStore.sort("updatedon","DESC");
    },
    gridRowClick:function(e,rowIndex) {
        WtfGlobal.enableDisableTbBtnArr(this.toolbarItems, this.EditorGrid, this.tbSingle, this.tbMulti, this.tbDefault);
        WtfGlobal.enableDisableTbBtnArr(this.tbarArchiveArray, this.EditorGrid, [0,1], [0,1], [1]);
        if(this.massUpdate!=undefined){
        	var sel = this.EditorGrid.getSelectionModel().getSelections();
        	var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        	if(sel!=undefined){
        		if(sel.length == 1 && selectedRec.data.accountid!="0" || sel.length>1)
        			this.massUpdate.enable();
        		else
        			this.massUpdate.disable();
        	}
        }
    },
    SelChange:function(){
        var s = this.EditorGrid.getSelectionModel().getSelections();
        var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        if(s.length == 1 && selectedRec.data.accountid!="0"){         
            if(!this.detailPanel.ownerCt.collapsed)
                getDocsAndCommentList(selectedRec, "accountid",this.id,undefined,"Account",undefined,"email",'accountownerid',this.contactsPermission);
            this.tbarShowActivity.enable();
			var updownCompE = Wtf.getCmp(this.id+'CRMupdownCompo');
		    var enableContactsButton=true;
		    var isMainOwner = selectedRec.get('accountownerid') == loginid;
            docCommentEnable(this,updownCompE,isMainOwner,enableContactsButton);
            enableButt(this,updownCompE,isMainOwner,enableContactsButton);
        } else {
            this.tbarShowActivity.disable();
            docCommentDisable(this);
            disableButt(this);
        }
        if(this.massUpdate!=undefined){
        	if(s.length == 1 && selectedRec.data.accountid!="0" || s.length>1)
        		this.massUpdate.enable();
        	else
        		this.massUpdate.disable();
        }
        this.gridRowClick();
        if(this.deleteAcc.disabled==false)
            this.deleteAcc.setTooltip(WtfGlobal.getLocaleText("crm.account.deleteBTN.ttip"));//'Delete the selected account(s).');
        else
            this.deleteAcc.setTooltip(WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip"));//'Select row(s) to delete.');
        if(this.tbarShowoppor.disabled==false && this.newFlag!=3) {
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.addactivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.ACCOUNT")]}));//'Add activity details for the selected account.');
            this.tbarShowCases.setTooltip(WtfGlobal.getLocaleText({key:"crm.account.toptoolbar.casebtn.ttip",params:[WtfGlobal.getLocaleText("crm.CASE")]}));//'Record case details related to the selected account.');
            this.tbarShowoppor.setTooltip(WtfGlobal.getLocaleText({key:"crm.account.toptoolbar.casebtn.ttip",params:[WtfGlobal.getLocaleText("crm.OPPORTUNITY")]}));//'Record opportunity details related to the selected account.');
        } else if(this.newFlag!=3){
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText("crm.account.toptoolbar.actvtbtn.ttip"));//'Select an account to record its activity details.');
            this.tbarShowCases.setTooltip(WtfGlobal.getLocaleText("crm.account.toptoolbar.enanbledcasebtn.ttip"));//'Select an account to record related cases.');
            this.tbarShowoppor.setTooltip(WtfGlobal.getLocaleText("crm.account.toptoolbar.enanbledoppbtn.ttip"));//'Select an account to record related opportunities.');
        }
    },
    checkRefRequired:function(e){
    	if(e.record.data[Wtf.SpreadSheetGrid.VALID_KEY]==0&&e.record.modified[Wtf.SpreadSheetGrid.VALID_KEY]==1)
    		e.url= Wtf.req.springBase+"common/crmCommonHandler/saveAccounts.do";
    },

//    validSave:function(row,record,field, e){
//
//        var modifiedRecord=this.EditorStore.getModifiedRecords();
//        if(modifiedRecord.length<1){
//         //   WtfComMsgBox(50,0);
//            return false;
//        }
//        this.saveData(row,record,field, e);
//    },
//    saveData:function(row,record,field, e){
//        var event = e;
//        var rData = record.data;
//        var auditStr=Wtf.SpreadSheet.constructAuditStr(this.EditorGrid,row, e.column, e.value, e.originalValue,record);
//        var jsondata="";
//        var validFlag=1;
//
//        var temp=rData.accountownerid;
//        if(temp=="") {
//            record.set('accountownerid',loginid);
//            record.set('accountowner',_fullName);
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
////        if(rData.accountname.trim()=="" ) {
////            validFlag=0;
////        }
//
//
//        jsondata+='{"accountid":"' + rData.accountid + '",';
//        jsondata+='"accountownerid":"' +rData.accountownerid+ '",';
//        jsondata+='"accountname":"' +rData.accountname+ '",';
//        jsondata+='"auditstr":"' +auditStr+ '",';
//        jsondata+='"productid":"' +rData.productid+ '",';
//        jsondata+='"price":"' +rData.price+ '",';
//        jsondata+='"phone":"' +rData.phone +'",';
//        jsondata+='"website":"' + rData.website + '",';
//        jsondata+='"email":"' + rData.email + '",';
//        jsondata+='"industryid":"' +rData.industryid+ '",';
//        jsondata+='"accounttypeid":"' +rData.accounttypeid + '",';
//        jsondata+='"revenue":"' + rData.revenue + '",';
//        jsondata+='"address":"' + rData.address + '",';
//        jsondata+='"createdon":' + (rData.createdon.getTime?rData.createdon.getTime():new Date().getTime())+ ',';
//        jsondata+='"validflag":"' +validFlag+ '",';
//        jsondata+='"dirtyfield":"' + field + '",';
//        jsondata+='"description":"' + rData.description + '"';
//        jsondata+=this.spreadSheet.getCustomColumnData(rData,false);
//        jsondata+= '},';
//        var trmLen = jsondata.length - 1;
//        var finalStr = jsondata.substr(0,trmLen);
//        var url;
//        // when state changes from valid state to invalid state,redirect to check references
//        if(record.get('validflag') == 1 && validFlag == 0 ){
//            url="common/crmCommonHandler/saveAccounts.do";
//        }else{
//            url="Account/action/saveAccounts.do";
//        }
//        if(rData.createdon==""){
//              var dates=new Date();
//                 record.set('createdon',dates);
//        }
//        Wtf.Ajax.requestEx({
//            url: Wtf.req.springBase+url,
//            params:{
//                jsondata:finalStr,
//                type:this.newFlag,
//                flag:21
//            }
//        },this,
//        function(res) {
//            if(res.revert){
//                // revert if found references in other modules
//                record.set(columnarray[event.column].dataIndex,event.originalValue);
//                WtfComMsgBox(["Alert","Sorry your attempt failed since account is being referenced in <br/><b>"+res.moduleName+"</b>"]);
//            } else{
//                rData.accountid=res.ID;
//                this.afterValidRecordSaved(res,finalStr,validFlag,rData.validflag,field);
//                record.set('validflag',validFlag);
////                if(validFlag==1) {
//
////                    Wtf.getCmp("tree").saveAccount(res,finalStr,1);
////                    Wtf.parentaccountstore.reload();
////                    Wtf.relatedToNameStore.reload();
////                    // update dashboard widget while updating or adding new entry
////                    bHasChanged = true;
////                    var obj=Wtf.getCmp(Wtf.moduleWidget.account);
////                    if(obj!=null){
////                        obj.callRequest("","",0);
////                        Wtf.refreshUpdatesAll();
////                    }
////                }
//            }
//
////            if(field=='accountname' && this.highLightId==rData.accountid) {
////                Wtf.getCmp("tree").getLoader().baseParams={mode:'0',expandaccount:true};
////                Wtf.getCmp("tree").getLoader().load(Wtf.getCmp("tree").root);
////            } else if(field=='accountname' && validFlag==1){
////                var parent;
////                var child;
////                parent=Wtf.getCmp("tree").getNodeById("accountnode");
////                child=parent.findChild('id',rData.accountid);
////                if(child!=null) {
////                    Wtf.getCmp("tree").getLoader().baseParams={mode:'0',expandaccount:true};
////                    Wtf.getCmp("tree").getLoader().load(Wtf.getCmp("tree").root);
////                }
////            }
//        },
//        function(res) {
//            WtfComMsgBox(52,1);
//        })
//    },

    afterValidRecordSaved : function (res,finalStr,validFlag, oldFlag, field) {
//        if(oldFlag != validFlag || field=="accountname") {
//            reloadAccountStore();
//        }
        Wtf.getCmp("tree").saveAccount(res,finalStr,1);
        // update dashboard widget while updating or adding new entry
        if(validFlag == 1) {
            bHasChanged = true;
            var obj=Wtf.getCmp(Wtf.moduleWidget.account);
            if(obj!=null){
                obj.callRequest("","",0);
            }
            Wtf.refreshUpdatesAll();
        }
    },

    accountDelete:function() {
        Wtf.deleteGlobal(this.EditorGrid,this.EditorStore,'Account(s)',"accid","accountid",'Account',29,30,31,"accountnode");
   },

    showcontacts:function() {
        if(this.EditorGrid.getSelectionModel().getSelections().length==1) {
            var recData = this.EditorGrid.getSelectionModel().getSelected().data;
            var tipTitle=recData.accountname+"'s Contacts";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var id= recData.accountid;
            var tabid = 'contactAccountTab'+id;
            var industryId = recData.industryid;
            var panel=Wtf.getCmp(tabid);
            var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
            if(panel==null) {
                panel= new Wtf.contactEditor({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Account'>"+title+"</div>",
                    id:tabid,
                    closable:true,
                    modName : "AccountContact",
                    customParentModName : "Contact",
                    scope:this,
                    mainselectedRec:selectedRec,
                    keyid:'accountid',
                    mainId:this.id,
                    mainOwnerid:'accountownerid',
                    mainContactpermossion:this.contactsPermission,
                    mapid:id,
                    RelatedRecordName:recData.accountname,
                    relatedName:'Account',
                    newFlag:2,
                    iconCls:getTabIconCls(Wtf.etype.contacts),
                    urlFlag:60,
                    subTab:true,
                    layout:'fit',
                    submainTab:this.mainTab,
                    industryid: industryId,
                    industry: recData.industry
                });
                this.mainTab.add(panel);
            }
            this.mainTab.setActiveTab(panel);
            this.mainTab.doLayout();
        }
        else {
            WtfComMsgBox(58,0);
        }
    },

    showLeadDetails : function() {
        /*this.selectedarray=this.EditorGrid.getSelectionModel().getSelections();
        this.rec=this.EditorGrid.getSelectionModel().getSelected();
        this.recname =this.rec.data.accountname;
        if(this.selectedarray.length==1) {
            var tipTitle=this.recname+"'s Activities";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var recData = this.rec.data;
            var id=this.rec.data.accountid;
            var panel=Wtf.getCmp(this.id+'detailAccountTab'+id);
            if(panel==null) {
                var colHeader=getColHeader(this.EditorColumnArray);
                panel= new Wtf.AboutView({
                    id : this.id+'detailAccountTab'+id,
                    closable:true,
                    recid: id,
                    id2:this.id+'detailAccountTab'+id,
                    cm : this.EditorGrid.colModel,
                    record : this.rec.data,
                    layout:"fit",
                    moduleName:"Account",
                    autoScroll:true,
                    mapid:4,
                    moduleScope:this,
                    recname:this.recname,
                    iconCls:"pwndCRM account",
                    fieldCols : ['Account Name','Email','Account Owner','Revenue ('+WtfGlobal.getCurrencySymbol()+')','Product','Price ('+WtfGlobal.getCurrencySymbol()+')','Type','Industry','Phone','Website','Description','Account Creation Date','subowners'],
                    fields:colHeader,
                    values : [recData.accountname,recData.email,this.searchValueField(Wtf.accountOwnerStore,recData.accountownerid,'id','name'),recData.revenue,this.searchValueField(Wtf.productStore,recData.productid,'id','name'),recData.price,this.searchValueField(Wtf.accountTypeStore,recData.accounttypeid,'id','name'),this.searchValueField(Wtf.industryStore,recData.industryid,'id','name'),recData.phone,recData.website,recData.description,WtfGlobal.onlyDateRenderer(recData.createdon),recData.subowners],
                    customField:this.spreadSheet.getCustomField(),
                    customValues:this.spreadSheet.getCustomValues(recData),
                    grid:this.EditorGrid,
                    Store:this.EditorStore,
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Account'>"+title+"</div>",
                    createProject:this.createProject,
                    viewProject:this.viewProject,
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

    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Accounts";
        var fromdate="";
        var todate="";
        var report="crm";
        var exportUrl = Wtf.req.springBase+"Account/action/accountExport.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,undefined,undefined,field,dir);
    },
    showopp:function() {
        if(this.EditorGrid.getSelectionModel().getSelections().length==1)
        {
            var recData =this.EditorGrid.getSelectionModel().getSelected().data;
            var tipTitle=recData.accountname+"'s Opportunities";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var accid=recData.accountid;
            var tabid = 'oppAccountTab'+accid;
            var panel=Wtf.getCmp(tabid);
            if(panel==null) {
                panel= new Wtf.opportunityEditor({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Account'>"+title+"</div>",
                    id:tabid,
                    layout:'fit',
                    border:false,
                    closable:true,
                    iconCls:getTabIconCls(Wtf.etype.opportunity),
                    modName : "AccountOpportunity",
                    customParentModName : "Opportunity",
                    scope:this,
                    RelatedRecordName:recData.accountname,
                    productid:recData.productid,
                    relatedName:'Account',
                    price:recData.price,
                    urlFlag:62,
                    newFlag:2,
                    accFlag:1,
                    mapid:accid,
                    subTab:true,
                    submainTab:this.mainTab,
                    archivedParentName : "Opportunity"
                });
                this.mainTab.add(panel);
            }
            this.mainTab.setActiveTab(panel);
            this.mainTab.doLayout();
        }
        else {
            WtfComMsgBox(65,0);
        }
    },
    showActivity:function() {
        if(this.EditorGrid.getSelectionModel().getCount()==1) {
        	this.rec=this.EditorGrid.getSelectionModel().getSelected();
        	var id=this.rec.data.accountid;
            if(id=="0"){
            	WtfComMsgBox(25);
            	return;
            }
            var ri=this.EditorStore.indexOf(this.rec);
            var recData = this.EditorGrid.getSelectionModel().getSelected().data;
            var accname = recData.accountname;
            var titlename =WtfGlobal.getLocaleText("crm.ACCOUNT");// "Account";
            if(accname.trim()!=""){
                titlename = accname;
            }
            var tipTitle=WtfGlobal.getLocaleText({key:"crm.common.recsactivity",params:[titlename]});//titlename+"'s Activity";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var id=recData.accountid;
            var tabid = 'accountActTab'+id;
            var panel=Wtf.getCmp(tabid);
            var newpanel = true;
            if(panel==null) {
                panel= new Wtf.activityEditor({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.ACCOUNT")+"'>"+title+"</div>",
                    id:tabid,
                    layout:'fit',
                    border:false,
                    closable:true,
                    modName : "AccountActivity",
                    customParentModName : "Activity",
                    scope:this,
                    urlFlag:150,
                    iconCls:getTabIconCls(Wtf.etype.todo),
                    RelatedRecordName:titlename,
                    Rrelatedto:'Account',
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
        else {
            WtfComMsgBox(58,0);
        }
    },

    showCases:function() {
        if(this.EditorGrid.getSelectionModel().getSelections().length==1) {
            var recData = this.EditorGrid.getSelectionModel().getSelected().data;
            var tipTitle=recData.accountname+"'s Cases";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var id = recData.accountid;
            var tabid = 'accountCaseTab'+id;
            var panel=Wtf.getCmp(tabid);
            if(panel==null) {
                panel= new Wtf.caseEditor({
                    border:false,
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Account'>"+title+"</div>",
                    layout:'fit',
                    closable:true,
                    scope:this,
                    urlFlag : 64,
                    modName : "AccountCase",
                    customParentModName : "Case",
                    accid:id,
                    addFlag:1,
                    RelatedRecordName:recData.accountname,
                    productid:recData.productid,
                    relatedName:'Account',
                    id:tabid,
                    iconCls:getTabIconCls(Wtf.etype.cases),
                    newFlag:2,
                    subTab:true,
                    submainTab:this.mainTab,
                    archivedParentName : "Case"
                });
                this.mainTab.add(panel);
            }
            this.mainTab.setActiveTab(panel);
            this.mainTab.doLayout();
        }
        else {
            WtfComMsgBox(66,0);
        }
    },
    showArchived:function() {
        var panel=Wtf.getCmp('AccountArchivePanel');
        if(panel==null) {
            panel=new Wtf.accountEditor({
                border:false,
                title:WtfGlobal.getLocaleText({key:"crm.editor.archivedwin.title",params:[WtfGlobal.getLocaleText("crm.ACCOUNT")]}),//'Archived Accounts',
                layout:'fit',
                closable:true,
                modName : "AccountArchived",
                customParentModName : "Account",
                archivedParentName : "Account",
                id:'AccountArchivePanel',
                iconCls:getTabIconCls(Wtf.etype.archived),
                newFlag:3,
                arcFlag:1,
                archivedFlag:1,
                subTab:true,
                submainTab:this.mainTab
            })
            this.mainTab.add(panel);
        }
        this.mainTab.setActiveTab(panel);
        this.mainTab.doLayout();
    },
    ArchiveHandler:function(a) {
        var data={a:a,tbarArchive:this.tbarArchive,EditorGrid:this.EditorGrid,treeid:"accountnode",title:'Account',keyid:'id',valueid:"accountid",table:'Account',GridId:'AccountGrid',homePanId:'AccountHomePanel',archivedPanId:'AccountArchivePanel',name:"name", valueName:"accountname", ownerName : "accountownerid"}
        var mod = "CrmAccount";
        var audit = "65";
        var auditMod = "Account";
        Wtf.ArchivedGlobal(data, mod, audit,auditMod);
    },
   temp: function(){
        Wtf.highLightGlobal(this.highLightId,this.EditorGrid,this.EditorStore,"accountid");
 },

  displayName:function(value,gridcell,record,d,e){
        var uname=(record.json.accountname).trim();
        return uname;
  },
  CreateNewContact : function() {
        var rec = this.grid.getSelectionModel().getSelected();
        this.addExtContactfunction(0,rec,1);
  },

    addExtContactfunction:function(action,record,flag){
        var windowHeading = action==0?"Add Account":"Edit Account";
        var windowMsg = action==0?"Enter new account details":"Edit existing account details";
        this.addExtContactWindow = new Wtf.Window({
            title : action==0?"Add Account":"Edit Account",
            closable : true,
            modal : true,
            iconCls : 'pwnd favwinIcon',
            width : 430,
            height: 370,
            resizable :false,
            buttons :[{
                text : action==0?"Add":"Edit",
                id: "createUserButton",
                scope : this,
                handler:function(){
                    if(this.createuserForm.form.isValid()){
                        Wtf.Ajax.requestEx({

                            url: Wtf.req.springBase+"Account/action/newAccount.do",
                            params: ({
                                type:"newAccount",
                                userid:Wtf.getCmp('tempContIdField').getValue(),
                                accountname:Wtf.getCmp('tempNameField').getValue(),
                                website:Wtf.getCmp('tempWebField').getValue(),
                                revenue:Wtf.getCmp('tempRevenueField').getValue(),
                                description: Wtf.getCmp('tempDescriptionField').getValue(),
                                contactno:Wtf.getCmp('tempPhoneField').getValue()
                            }),
                            method: 'POST'
                        },
                        this,
                        function(result, req){
                            if(result!=null && result != ""){
                                WtfComMsgBox(466, 0);
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
                    labelWidth: 120,
                    bodyStyle : 'margin-top:20px;margin-left:35px;font-size:10px;',
                    defaults: {
                        width: 200
                    },
                    defaultType: 'textfield',
                    items: [{
                        fieldLabel: 'Account Name* ',
                        id:'tempNameField',
                        name:'name',
                        allowBlank:false
                    },{
                        fieldLabel: 'Revenue* ',
                        allowBlank:false,
                        id: "tempRevenueField",
                        name: 'revenue'
                    },{
                        fieldLabel: 'Website* ',
                        id:'tempWebField',
                        name: 'website',
                        allowBlank:false
                    },{
                        fieldLabel: 'Phone* ',
                        allowBlank:false,
                        id: "tempPhoneField",
                        name: 'phone'
                    },{
                        xtype:"textarea",
                        fieldLabel: 'Description ',
                        id: "tempDescriptionField",
                        name: 'description'
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
        Wtf.getCmp('tempDescriptionField').on("change", function(){
            Wtf.getCmp('tempDescriptionField').setValue(WtfGlobal.HTMLStripper(Wtf.getCmp('tempDescriptionField').getValue()));
        }, this);
        this.addExtContactWindow.show();
        if(record!=null){
            Wtf.getCmp('tempNameField').setValue(record.json.accountname);
            Wtf.getCmp('tempRevenueField').setValue(record.json.revenue);
            Wtf.getCmp('tempWebField').setValue(record.json.website);
            Wtf.getCmp('tempPhoneField').setValue(record.json.contactno);
            Wtf.getCmp('tempDescriptionField').setValue(record.json.address);
            Wtf.getCmp('tempContIdField').setValue(record.json.userid);
        }
    }
});
