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
Wtf.contactEditor = function (config){
    Wtf.contactEditor.superclass.constructor.call(this,config);
};

Wtf.extend(Wtf.contactEditor,Wtf.Panel,{
    border:false,
    getEditor:Wtf.SpreadSheetGrid.prototype.getEditor,
    initComponent: function(config){
        Wtf.contactEditor.superclass.initComponent.call(this,config);

        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));

        this.getEditorGrid();

        this.getAdvanceSearchComponent();

        this.getDetailPanel();

        this.contactpan= new Wtf.Panel({
            layout:'border',
            title:WtfGlobal.getLocaleText({key:"crm.tab.title",params:[WtfGlobal.getLocaleText("crm.CONTACT")]}),//"Contact List",
            iconCls:getTabIconCls(Wtf.etype.contacts),
            border:false,
            id:this.id+'contactpan',
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
           id:this.id+"contactTabPanel",
           scope:this,
           border:false,
           resizeTabs: true,
           minTabWidth: 155,
           enableTabScroll: true,
           items:[this.contactpan]
        });

        if(this.archivedFlag==1){
            this.add(this.contactpan);
        }else if(this.subTab==true){
            this.add(this.contactpan);
        }else{
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.contactpan);
        }
        this.doLayout();
        this.contactpan.on("activate",function(){
            Wtf.getCmp("ContactHomePanelcontactpan").doLayout();
        },this);
        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);

    },
    getDetails:function(){
    	var sm = this.EditorGrid.getSelectionModel();
    	if(sm.getCount()!=1)
    		return;
    	var commentlist = getDocsAndCommentList(sm.getSelected(), "contactid",this.id,undefined,'Contact',undefined,undefined,'contactownerid',undefined,0);
    },

    showAdvanceSearch:function(){
        showAdvanceSearch(this,this.searchparam);
    },
    reloadStore : function(obj){
            obj.EditorStore=obj.spreadSheet.getMySortconfig(obj.EditorStore);
            if(obj.EditorStore.baseParams && obj.EditorStore.baseParams.searchJson){
                obj.EditorStore.baseParams.searchJson="";
            }
            obj.EditorStore.load({
                params:{
                    start:0,
                    searchJson:this.searchJson,
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
//        Wtf.titleStore.load(); // not in used
        Wtf.lsourceStore.load();
        Wtf.industryStore.load();
//        Wtf.relatedToNameStore.load();
    },
    getEditorGrid : function (){
        /*
         * function createContactModuleConfig()
         * 2nd Parameter -> moduleid
         * 3rd Parameter -> false when called thru contacts tab; true when called directly thru Details Panel
         */
    	this.loadCount=0;
        createContactModuleConfig(this,6,false);

        this.EditorGrid = this.spreadSheet.getGrid();
        this.EditorColumn = this.spreadSheet.getColModel();

        if(this.newFlag==2){//new

            if(WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.manage)) {
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
        this.EditorGrid.on("beforeinsert",this.setDefaultValues,this);
        this.gridRowClick();
        this.updateInfo = {
            	keyField:'contactid',
            	auditStr:"Contact details updated from contact profile for ",
            	url:"Contact/action/updateMassContacts.do",
            	flag:20,
            	type:"Contact"
    		};
        this.EditorGrid.getSelectionModel().on("rowselect",this.SelChange,this);
        this.EditorGrid.getSelectionModel().on("rowdeselect",this.SelChange,this);
        this.EditorGrid.on("sortchange",this.sortChange,this);
        this.EditorGrid.on("rowclick",this.gridCellClick,this);
        this.EditorGrid.on("mouseover",Wtf.hideNotes,this);

        if(((!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.exportt)) ) &&  ( this.clearFlag!=undefined || this.subTab!=null ) && (this.newFlag!=3)){
        	if(this.tbarExport != undefined){
        		this.tbarExport.on("mouseover", function(){
        			var s = this.EditorGrid.getSelectionModel().getSelectedRows();
        			this.exp.menu.items.items[1].setDisabled(s.length<=0);
        			this.exp.menu.items.items[3].setDisabled(s.length<=0);
        			this.exp.menu.items.items[5].setDisabled(s.length<=0);
            	},this);
        	}
        }
    },

    setDefaultValues:function(values){
    	Wtf.apply(values,{
	        relatedname:(this.addFlag==0?"":this.RelatedRecordName),
	        oldrelatedname:(this.addFlag==0?"":this.mapid),
	        relatednameid:(this.addFlag==0?"":this.mapid),
	        leadsourceid:(this.leadsourceid !=undefined ? this.leadsourceid :""),
	        leadsource:(this.leadsource !=undefined ? this.leadsource :""),
	        industry:(this.industry != undefined ? this.industry:""),
	        industryid:(this.industryid != undefined ? this.industryid:""),
	        loginstate:-1
    	});
    },

    handleRecentContacts:function(){
    	 this.EditorStore.extraSortInfo = {xtype:"datefield",xfield:"c.updatedOn",iscustomcolumn:false};
         this.EditorStore.sort("updatedon","DESC");

    },

    gridCellClick : function(grid, ri, e) {

        if(e.target.className == "clicktoshowcomment") {
            Wtf.onCellClickShowComments(grid.getStore().getAt(ri).data.contactid, this.customParentModName, ri, e);
        }
        if(e.target.className == "showMandatoryFields") {
            Wtf.emptyMandatoryFields(grid,ri);
        }
        this.gridRowClick();
    },
    sortChange:function(grid,sortInfo, sortFlag){
        this.sortInfo = sortInfo;
    },
    gridRowClick:function(e,rowIndex) {
        WtfGlobal.enableDisableTbBtnArr(this.toolbarItems, this.EditorGrid, this.tbSingle, this.tbMulti, this.tbDefault);
        WtfGlobal.enableDisableTbBtnArr(this.tbarArchiveArray, this.EditorGrid, [0,1], [0,1], [1]);
        if(this.massUpdate!=undefined){
        	var sel = this.EditorGrid.getSelectionModel().getSelections();
        	var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        	if(sel!=undefined){
        		if(sel.length == 1 && selectedRec.data.contactid!="0" || sel.length>1)
        			this.massUpdate.enable();
        		else
        			this.massUpdate.disable();
        	}
        }
    },

    SelChange:function(){
        var s = this.EditorGrid.getSelectionModel().getSelections();
        var selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        if(s.length == 1 && selectedRec.data.contactid!="0" || s.length>1) {
        	 if(this.massUpdate!=undefined)
        		 this.massUpdate.enable();
        	 this.linkContacts.enable();
        } else {
        	 if(this.massUpdate!=undefined)
        		 this.massUpdate.disable();
            this.linkContacts.disable();
        }

        if(s.length == 1 && selectedRec.data.contactid!="0"){
        	 if(!this.detailPanel.ownerCt.collapsed)
        		 getDocsAndCommentList(selectedRec, "contactid",this.id,undefined,'Contact',undefined,undefined,'contactownerid');
        	 var updownCompE = Wtf.getCmp(this.id+'CRMupdownCompo');
 		    var enableContactsButton=true;
 		    var isMainOwner = selectedRec.get('contactownerid') == loginid;
			docCommentEnable(this, updownCompE, isMainOwner, enableContactsButton);
			enableButt(this, updownCompE, isMainOwner, enableContactsButton);
			this.tbarShowActivity.enable();
			if (selectedRec.data.loginstate == 1) {
				this.tbardeactivelogin.setText(WtfGlobal.getLocaleText("crm.contact.actdeactBTN.deactivatemode"));//"Deactivate");
				this.tbardeactivelogin.enable();
				this.tbarShowLogin.disable();
			} else if (selectedRec.data.loginstate == 2) {
				this.tbardeactivelogin.setText(WtfGlobal.getLocaleText("crm.contact.actdeactBTN.activatemode"));//"Activate");
				this.tbardeactivelogin.enable();
				this.tbarShowLogin.disable();
			} else {
				this.tbarShowLogin.enable();
			}
		} else {
			this.tbarShowActivity.disable();
			docCommentDisable(this);
			disableButt(this);
			this.tbardeactivelogin.disable();
			this.tbarShowLogin.disable();
		}

        this.gridRowClick();
        if(this.deleteCon.disabled==false)
            this.deleteCon.setTooltip(WtfGlobal.getLocaleText("crm.contact.deleteBTN.ttip"));//'Delete the selected contact(s).');
        else
            this.deleteCon.setTooltip(WtfGlobal.getLocaleText("crm.contact.disableddeleteBTN.ttip"));//'Select row(s) to delete');
        if(this.tbarShowActivity.disabled==false && this.newFlag!=3)
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.addactivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.CONTACT")]}));//'Add activity details for the selected contact.');
        else if(this.newFlag!=3)
            this.tbarShowActivity.setTooltip(WtfGlobal.getLocaleText({key:"crm.editor.disabledaddactivityBTN.ttip",params:[WtfGlobal.getLocaleText("crm.CONTACT")]}));//'Select a contact to add its activity details.');
        if(this.tbarShowLogin.disabled==false && this.newFlag!=3)
            this.tbarShowLogin.setTooltip(WtfGlobal.getLocaleText("crm.contact.logindetailwin.title"));//'Login Details');
        else if(this.newFlag!=3)
            this.tbarShowLogin.setTooltip(WtfGlobal.getLocaleText("crm.contact.disabledcustloginBTN.ttip"));//'Select a contact to provide login');
         if(this.tbardeactivelogin.disabled==false)
        	 this.tbardeactivelogin.setTooltip(WtfGlobal.getLocaleText("crm.contact.actdeactBTN.ttip"));//'Activate/Deactivate customer login ');
        // if(this.tbarShowActivity.disabled==true)
        	// this.tbardeactivelogin.setTooltip(WtfGlobal.getLocaleText(""));//'select contacts to deactivate login ');
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var fromdate="";
        var todate="";
        var report="crm";
        var flag = this.urlFlag;
        var exportUrl = Wtf.req.springBase+"Contact/action/contactExport.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        if(this.urlFlag==6) {
            var name="Contacts";
			exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,flag,undefined,undefined,field,dir);
            //exportWithTemplate(this,type,name,fromdate,todate,report, exportUrl, flag);
        } else {
            if(this.relatedName == "Lead") {
                name="LeadContact";
            } else if(this.relatedName != "Opportunity") {
                name="AccountContact";
            } else {
                name="OppContact";
            }
			exportWithTemplate(this,type,name,fromdate,todate,report+this.mapid,exportUrl,flag,undefined,undefined,field,dir);
            //exportWithTemplate(this,type,name,fromdate,todate,report+this.mapid, exportUrl, flag);
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
        var exportUrl = Wtf.req.springBase+"Contact/action/contactExport.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        if(this.urlFlag==6) {
            var name="Contacts";
			exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,flag,undefined,undefined,field,dir);
            //exportWithTemplate(this,type,name,fromdate,todate,report, exportUrl, flag);
        } else {
            if(this.relatedName == "Lead") {
                name="LeadContact";
            }else if(this.relatedName != "Opportunity") {
                name="AccountContact";
            } else {
                name="OppContact";
            }
			exportWithTemplate(this,type,name,fromdate,todate,report+this.mapid,exportUrl,flag,undefined,undefined,field,dir);
            //exportWithTemplate(this,type,name,fromdate,todate,report+this.mapid, exportUrl, flag);
		}
    },

    exportSelected: function(type) {
        var report="crm";
        var flag = this.urlFlag;
        if(this.urlFlag==6) {
            var name="Contacts";
        } else {
            if(this.relatedName == "Lead") {
                name="LeadContact";
            }else if(this.relatedName != "Opportunity") {
                name="AccountContact";
            } else {
                name="OppContact";
            }
            report += this.mapid;
        }
        var fromdate="";
        var todate="";
        var selArr = [];
        var exportUrl = Wtf.req.springBase+"Contact/action/contactExport.do";
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
                jsondata+="{'id':'" + selArr[i].get('contactid') + "'},";
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

    storeLoad:function(){

    },

    getDetailPanel:function()
    {
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.EditorGrid,
            Store:this.EditorStore,
            modulename:'contact',
            keyid:'contactid',
            height:200,
            mapid:2,
            id2:this.id,
            moduleName:'Contact',
            ownerid:'contactownerid',
            leadDetailFlag:true,
            moduleScope:this,
            detailPanelFlag:(this.archivedFlag==1?true:false)
        });
    },

    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.spreadSheet.colArr,
            module : Wtf.crmmodule.contact,
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
            mapid:this.mapid,
            relatedName : this.relatedName,
            searchJson:this.searchJson,
            isarchive:this.newFlag==3?true:false
        };
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
        this.AdvanceSearchBtn.enable();
        this.objsearchComponent.hide();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;
        this.EditorStore.baseParams = {
            flag:this.urlFlag,
            mapid:this.mapid,
            relatedName : this.relatedName,
            isarchive:this.newFlag==3?true:false,
            searchJson:this.searchJson
        };
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
    },

    newGrid:function()
    {
        var newFlag=1;
    },
//    addNewRec:function (){
//        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.manage)) {
//            var gridRec={
//                validflag:-1,
//                contactid:"0",
//                contactownerid:'',
//                contactowner:'',
//                firstname:"",
//                subowners:'',
//                lastname:"",
//                relatedname:(this.addFlag==0?"":this.RelatedRecordName),
//                oldrelatedname:(this.addFlag==0?"":this.mapid),
//                relatednameid:(this.addFlag==0?"":this.mapid),
//                phoneno:"",
//                mobileno:"",
//                email:"",
//                street:"",
//                leadsourceid:(this.leadsourceid !=undefined ? this.leadsourceid :""),
//                leadsource:(this.leadsource !=undefined ? this.leadsource :""),
//                createdon:'',
//                title:"",
//                industry:(this.industry != undefined ? this.industry:""),
//                industryid:(this.industryid != undefined ? this.industryid:""),
//                description:"",
//                loginstate:-1
//            };
//            this.newRec = new this.EditorRec(this.spreadSheet.getEmptyCustomFields(gridRec));
//            if(this.relatedName == 'Lead' && this.mainselectedRec){
//                this.setOthermoduleValues(this.newRec,this.mainselectedRec,"Contact");
//            }
//            this.EditorStore.insert(0, this.newRec);
//        }
//    },
//    setOthermoduleValues : function(newRec,mainselectedRec,moduleName){
//         Wtf.Ajax.requestEx({
//            url: "Common/CRMCommon/getMappedHeaders.do",
//            params: {
//                modulename : moduleName
//            }
//        }, this, function(action, response){
//            if(action.success){
//                var data = action.data;
//                for(var i=0;i<data.length;i++){
//                    try{
//                        if(mainselectedRec.get(data[i].leadfieldname)!=""){
//                            newRec.set(data[i].modulefieldname,mainselectedRec.get(data[i].leadfieldname));
//                        }
//                    }catch(e){
////                        clog(e);
//                    }
//                }
//            }
//        }, function(action, response){
//        });
//    },
    checkRefRequired:function(e){
    	if(this.relatedName == "Lead"){
    		e.json['leadid']=this.mapid;
    	}
    	e.json['accountid']=e.json['relatednameid'];
    	e.json['oldaccountid']=e.json['oldrelatedname'];
    	if(e.record.data[Wtf.SpreadSheetGrid.VALID_KEY]==0&&e.record.modified[Wtf.SpreadSheetGrid.VALID_KEY]==1)
    		e.url= Wtf.req.springBase+"common/crmCommonHandler/saveContacts.do";
    },
/*
 *  function comboLoad() Not in Used
 */
//    comboLoad:function (e){
//    //        if(e.field=='relatedtonameid'){
//    //            this.relatedToNameStore.load({
//    //                params:{
//    //                    relatedtoid:e.record.get('relatedtoid')
//    //                }
//    //            })
//    //        }
//    },

//    validSave:function(rowindex,record,field, e){
//        var modifiedRecord=this.EditorStore.getModifiedRecords();
//        if(modifiedRecord.length<1){
//        //    WtfComMsgBox(300,0);
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
//        var temp=rData.contactownerid;
//        if(temp=="")
//        {
//            record.set('contactownerid', loginid);
//            record.set('contactowner',_fullName);
//        }
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
//        jsondata+='{"contactid":"' + rData.contactid + '",';
//        jsondata+='"contactownerid":"' +rData.contactownerid+ '",';
//        jsondata+='"firstname":"' +rData.firstname+ '",';
//        jsondata+='"lastname":"' +rData.lastname+ '",';
//        if(this.relatedName == "Lead"){
//            jsondata+='"leadid":"' +this.mapid+ '",';
//        }
//        jsondata+='"accountid":"' +rData.relatednameid+ '",';
//        jsondata+='"relatedname":"' +rData.relatednameid+ '",';   // added for commet
//        jsondata+='"auditstr":"' +auditStr+ '",';
//        jsondata+='"oldaccountid":"' +rData.oldrelatedname+ '",';
//        jsondata+='"oldrelatedname":"' +rData.oldrelatedname+ '",';   // added for commet
//        jsondata+='"phoneno":"' +rData.phoneno +'",';
//        jsondata+='"mobileno":"' +rData.mobileno +'",';
//        jsondata+='"email":"' + rData.email + '",';
//        jsondata+='"industryid":"' + rData.industryid+ '",';
//        jsondata+='"leadsourceid":"' + rData.leadsourceid + '",';
//        jsondata+='"title":"' + rData.title + '",';
//        jsondata+='"street":"' +rData.street + '",';
//        jsondata+='"createdon":"' + (rData.createdon.getTime?rData.createdon.getTime():new Date().getTime())+ '",';
//        jsondata+='"validflag":"' +validFlag+ '",';
//        jsondata+='"dirtyfield":"' + field + '",';
//        jsondata+='"description":"' + rData.description + '"';
//        jsondata+=this.spreadSheet.getCustomColumnData(rData,false);
//        jsondata+= '},';
//        var trmLen = jsondata.length - 1;
//        var finalStr = jsondata.substr(0,trmLen);
//        var url;
//        // when state changes from valid state to invalid state,redirect to check references
//        if(record.get('validflag') == 1 && validFlag == 0 ) {
//            url="common/crmCommonHandler/saveContacts.do";
//        } else {
//            url="Contact/action/saveContacts.do";
//        }
//        if(rData.createdon==""){
//              var dates=new Date();
//                 record.set('createdon',dates);
//        }
//        Wtf.Ajax.requestEx({
//
//            url: Wtf.req.springBase+url,
//            params:{
//                jsondata:finalStr,
//                type:this.newFlag,
//                flag:22
//            }
//        },
//        this,
//        function(res)
//        {
//            if(res.revert){
//                // revert if found references in other modules
//                record.set(columnarray[event.column].dataIndex,event.originalValue);
//                WtfComMsgBox(["Alert","Sorry your attempt failed since account is being referenced in <br/><b>"+res.moduleName+"</b>"]);
//            }else{
//                rData.contactid=res.ID;
//                this.afterValidRecordSaved(validFlag, record.data.validflag,field);
//                record.set('validflag',validFlag);
//
//            }
//        },
//        function()
//        {
//            WtfComMsgBox(302,1);
//        }
//        );
//    },

    afterValidRecordSaved : function (newValidFlag, oldValidFlag,field) {
//        if(oldValidFlag != newValidFlag || field=='firstname' || field=='lastname') {
//            reloadContactStore();
//        }
        if(newValidFlag == 1) {
            bHasChanged = true;
            var obj=Wtf.getCmp(Wtf.moduleWidget.contact);
            if(obj!=null){
                obj.callRequest("","",0);
            }
            Wtf.refreshUpdatesAll();
        }
    },

    contactDelete:function()
    {
        Wtf.deleteGlobal(this.EditorGrid,this.EditorStore,'Contact(s)',"contactid","contactid",'Contact',23,24,25);
    },

    showActivity:function()
    {
    	 if(this.EditorGrid.getSelectionModel().getCount()==1){
         	this.rec=this.EditorGrid.getSelectionModel().getSelected();
         	var id=this.rec.data.contactid;
         	if(id=="0"){
             	WtfComMsgBox(25);
             	return;
             }
            var contname = this.rec.data.lastname;
            var titlename = WtfGlobal.getLocaleText("crm.CONTACT");//"Contact";
            if(contname.trim()!=""){
                titlename = contname;
            }
            var tipTitle=WtfGlobal.getLocaleText({key:"crm.common.recsactivity",params:[titlename]});//titlename+"'s Activity";
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var panel=Wtf.getCmp(this.id+'contactLeadTab'+id);
            var newpanel = true;
            if(panel==null)
            {
                panel= new Wtf.activityEditor({
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.CONTACT")+"'>"+title+"</div>",
                    id:this.id+'contactLeadTab'+id,
                    layout:'fit',
                    border:false,
                    closable:true,
                    scope:this,
                    urlFlag:152,
                    customParentModName : "Activity",
                    modName : "ContactActivity",
                    RelatedRecordName:titlename,
                    iconCls:getTabIconCls(Wtf.etype.todo),
                    Rrelatedto:'Contact',
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
            WtfComMsgBox(308,0);
        }
    },
    showLogin:function(){
    	if(this.EditorGrid.getSelectionModel().getCount()==1){
         	this.rec=this.EditorGrid.getSelectionModel().getSelected();
         	var contactid=this.rec.data.contactid;
         	var validflag=this.rec.data.validflag;
            var email = this.rec.data.email;
            if(email==""||email==undefined)
            {
            	Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),"<b>"+WtfGlobal.getLocaleText("crm.contact.logindetailwin.selvalemailmsg")+"</b>");
            	return false;
            }
            if(validflag=='0')
            {
            	WtfComMsgBox(25,1);
            	return false;
            }
            this.loginmail= new Wtf.form.TextField({
        	 	fieldLabel: WtfGlobal.getLocaleText("crm.contact.logindetailwin.sendto"),//'Send To',
	            name: 'to',
	            id: 'emailField',
	            width:270,
	            allowBlank:false,
	            disabled:true,
	            value:email,
	            anchor:'70%'  // anchor width by percentage
            });
            this.form = new Wtf.form.FormPanel({
    	        baseCls: 'x-plain',
    	        labelWidth: 55,
    	        url:'save-form.php',
    	        defaultType: 'textfield',
    	        items: [this.loginmail]
    	    });

            this.win=new Wtf.Window({
            	height:200,
            	width:350,
            	id:'logindetails',
            	iconCls: "pwnd favwinIcon",
            	title:WtfGlobal.getLocaleText("crm.contact.logindetailwin.title"),//"Login Details",
            	modal:true,
            	shadow:true,
            	resizable:false,
            	buttonAlign:'right',
            	items: [{
            		region : 'north',
            		height : 75,
            		border : false,
            		bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            		html: getTopHtml(WtfGlobal.getLocaleText("crm.contact.logindetailwin.tophtmltitle"),WtfGlobal.getLocaleText("crm.contact.logindetailwin.tophtmldetail"))
            	},{
            		region : 'center',
            		border : false,
            		bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
            		layout : 'fit',
            		items :[   this.form   ]
                	}],
                	buttons: [{
                		text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                		scope:this,
                		handler:function(){
                		Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.common.processingloadmsg"));
                		Wtf.Ajax.requestEx({
                			url: Wtf.req.springBase+"Contact/action/saveLogin.do",
                			params: ({
                				type:"loginmail",
                				contactid:contactid,
                				emailid:this.loginmail.getValue(),
                				companyid:companyid,
                				setActive:true
                		}),
                		method: 'POST'
                		},this,
                		function(res){
                			this.win.close();
                			if(res.mailIdExist){
                				WtfComMsgBox(960);
                			}else{
                				if(res.success){
                					this.rec.set("loginstate",1);
                					this.tbarShowLogin.setDisabled(true);
                				}
                				Wtf.Msg.alert('Success',res.msg);
                			}
                		});
                	}
                	},{
                		text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),// 'Cancel',
                		scope:this,
                		handler:function(){
                		this.win.close();
                	  }
                	}]
            });
            this.win.show();
    	}
    },

 activedeactivelogin:function (){
    	 var selectionModel=this.EditorGrid.getSelectionModel();
         var selectedRowCount = selectionModel.getCount();
         var rcs=this.EditorGrid.getSelectionModel().getSelected();
         var cnt=0;
         var validflag=rcs.data.validflag;
         if(validflag=='0') {
             	WtfComMsgBox(25,1);
        		return false;
         }
         Wtf.MessageBox.show({
                title: WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//"Confirm",
                msg:rcs.data.loginstate==true? WtfGlobal.getLocaleText("crm.contact.logindetailwin.confirmdeactmsg"):WtfGlobal.getLocaleText("crm.contact.logindetailwin.confirmActmsg"),//Are you sure you want to deactivate selected customer login?":"Are you sure you want to activate selected customer login?",
                buttons: Wtf.MessageBox.OKCANCEL,
                animEl: 'upbtn',
                icon: Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(bt){
                if(bt=="ok"){
                    if(rcs.data.loginstate==1)
                    	this.activedeactivateRequest(rcs,rcs.data.contactid,false);
                    else
                    	this.activedeactivateRequest(rcs,rcs.data.contactid,true);
                }
            }
            });
 },

activedeactivateRequest:function(rec,ids,activeflg){
        Wtf.Ajax.requestEx({
            url:Wtf.req.springBase+"Contact/action/activate_deactivateLogin.do",
            params:{
        		contactid:ids,
                active:activeflg
            },
        method: 'POST'
        },
        this,
        function(res)
        {
        	if(res.success==true){
        		var msg="";
        		var state;
        		if(activeflg){
        			msg=WtfGlobal.getLocaleText("crm.contact.logindetailwin.activateSuccessmsg");//"Login has been activated for the contact successfully.";
        			state=1;
        		}else{
        			msg=WtfGlobal.getLocaleText("crm.contact.logindetailwin.deactivateSuccessmsg");//"Login has been deactivated for the contact successfully.";
        			state=2;
        		}
        		this.tbardeactivelogin.setDisabled(true);
        		rec.set("loginstate",state);
        		Wtf.Msg.alert('Success',msg);
        	}
        });

},
    showLeadDetails : function() {
        this.selectedarray=this.EditorGrid.getSelectionModel().getSelections();
        this.rec=this.EditorGrid.getSelectionModel().getSelected();
        if(this.rec!=undefined){
        this.recname =this.rec.data.lastname;
        if(this.selectedarray.length==1) {
            var tipTitle=WtfGlobal.getLocaleText({key:"crm.editor.bottomtoolbar.moddetailsBTN",params:[this.recname]});
            var title = Wtf.util.Format.ellipsis(tipTitle,18);
            var recData = this.rec.data;
            var id=this.rec.data.contactid;
            var panel=Wtf.getCmp(this.id+'detailContactTab'+id);
            if(panel==null) {
                var colHeader=getColHeader(this.EditorColumnArray);
                panel= new Wtf.AboutView({
                    id : this.id+'detailContactTab'+id,
                    closable:true,
                    recid: id,
                    id2:this.id+'detailContactTab'+id,
                    cm : this.EditorGrid.colModel,
                    record : this.rec.data,
                    layout:"fit",
                    moduleName:"Contact",
                    autoScroll:true,
                    mapid:2,
                    moduleScope:this,
                    recname:this.recname,
                    iconCls:"pwndCRM contactsTabIcon",
                    fieldCols : ['First Name','Last Name','Contact Owner','Account Name','Designation','Lead Source','Industry','Email','Phone','Mobile','Address','Description','Contact Creation Date','Contact Updated Date',,'subowners'],
                    fields:colHeader,
                    values : [recData.firstname,recData.lastname,recData.contactowner,recData.relatedname,recData.title,recData.leadsource,recData.industry,recData.email,recData.phoneno,recData.mobileno,recData.street,recData.description,WtfGlobal.onlyDateRendererTZ(recData.createdon),WtfGlobal.onlyDateRenderer(recData.updatedon),recData.subowners],
                    customField:this.spreadSheet.getCustomField(),
                    customValues:this.spreadSheet.getCustomValues(recData),
                    grid:this.EditorGrid,
                    Store:this.EditorStore,
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("crm.CONTACT")+"'>"+title+"</div>",
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

    displayName:function(value,gridcell,record,d,e){
        var uname=(record.json.username).trim();
        return uname;
    },
    CreateNewContact : function() {
        var rec = this.grid.getSelectionModel().getSelected();
        this.addExtContactfunction(0,rec,1);
    },

    addExtContactfunction:function(action,record,flag){
        var windowHeading = action==0?WtfGlobal.getLocaleText("crm.contact.addcontact"):WtfGlobal.getLocaleText("crm.contact.editcontact");//"Add Contact":"Edit Contact";
        var windowMsg = action==0?WtfGlobal.getLocaleText("crm.contact.addcontact.windowmsg"):WtfGlobal.getLocaleText("crm.contact.editcontact.windowmsg");//"Enter new contact details":"Edit existing contact details";
        this.addExtContactWindow = new Wtf.Window({
            title : action==0?WtfGlobal.getLocaleText("crm.contact.addcontact"):WtfGlobal.getLocaleText("crm.contact.editcontact"),//"Add Contact":"Edit Contact",
            closable : true,
            modal : true,
            iconCls : 'pwnd favwinIcon',
            width : 430,
            height: 370,
            resizable :false,
            buttons :[{
                text : action==0?WtfGlobal.getLocaleText("crm.ADDTEXT"):WtfGlobal.getLocaleText("crm.EDITTEXT"),//"Add":"Edit",
                id: "createUserButton",
                scope : this,
                handler:function(){
                    if(this.createuserForm.form.isValid()){
                        Wtf.Ajax.requestEx({

                            url: Wtf.req.springBase+"Contact/action/newContact.do",
                            params: ({
                                type:"newAddress",
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
                            this.EditorStore.reload();
                            this.addExtContactWindow.close();
                         },function(){
                            this.addExtContactWindow.close();
                        });
                    }
                }
            },{
                text : WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
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
                        fieldLabel: 'Name* ',
                        id:'tempNameField',
                        name:'name',
                        validator:WtfGlobal.validateUserName,
                        allowBlank:false
                    },{
                        fieldLabel: WtfGlobal.getLocaleText("crm.EMAILIDFIELD")+'*',//'Email Id* ',
                        id:'tempEmailField',
                        name: 'emailid',
                        validator: WtfGlobal.validateEmail,
                        allowBlank:false,
                        renderer: WtfGlobal.renderEmailTo
                    },{
                        fieldLabel: WtfGlobal.getLocaleText("crm.lead.defaultheader.phone")+'*',//'Phone* ',
                        allowBlank:false,
                        id: "tempPhoneField",
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
        this.addExtContactWindow.show();
        if(record!=null){
            Wtf.getCmp('tempNameField').setValue(record.json.username);
            Wtf.getCmp('tempEmailField').setValue(record.json.emailid);
            Wtf.getCmp('tempPhoneField').setValue(record.json.contactno);
            Wtf.getCmp('tempAddField').setValue(record.json.address);
            Wtf.getCmp('tempContIdField').setValue(record.json.userid);
        }
    },
    showArchived:function() {
        var panel=Wtf.getCmp('ContactArchivePanel'+this.mapid);

        this.archiveUrlFlag=6;
        var tipTitle=WtfGlobal.getLocaleText({key:"crm.editor.archivedwin.title",params:[WtfGlobal.getLocaleText("crm.CONTACT.plural")]});//"Archived Contacts";
        var qtitle=WtfGlobal.getLocaleText("crm.editor.archivewin.title");//Archived";
        if(this.relatedName=="Account" ||this.relatedName=="Opportunity"){
            this.archiveUrlFlag=60;
            tipTitle=tipTitle=this.RelatedRecordName+"'s Archived Contacts";
            qtitle=this.relatedName;
        }
        var title = Wtf.util.Format.ellipsis(tipTitle,18);
        if(panel==null) {
            panel=new Wtf.contactEditor({
                border:false,
                title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+qtitle+"'>"+title+"</div>",
                layout:'fit',
                closable:true,
                id:'ContactArchivePanel'+this.mapid,
                modName : "ContactArchived",
                customParentModName : "Contact",
                iconCls:getTabIconCls(Wtf.etype.archived),
                newFlag:3,
                arcFlag:1,
                parenturlFlag:this.urlFlag,
                parentId:this.urlFlag==6?undefined:this.EditorGrid.getId(),
                mapid:this.mapid,
                urlFlag:this.archiveUrlFlag,
                archivedFlag:1,
                mainTab:this.submainTab!=undefined?this.submainTab:this.mainTab,
                archivedParentName : "Contact",
                subTab:true,
                submainTab:this.submainTab!=undefined?this.submainTab:this.mainTab
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
    },
    ArchiveHandler:function(a) {

        if(this.parentId == undefined) {
            var data={
                a:a,
                tbarArchive:this.tbarArchive,
                EditorGrid:this.EditorGrid,
                title:WtfGlobal.getLocaleText("crm.CONTACT"),//'Contact',
                keyid:'id',
                valueid:"contactid",
                table:'Contact',
                GridId:'ContactGrid',
                homePanId:'ContactHomePanel',
                archivedPanId:'ContactArchivePanel'+this.mapid,
                name:"name",
                valueName:"lastname"
            };
        } else {
            var temp=this.parentId.split('ContactGrid');
            if(this.parenturlFlag==60) {
                data={
                    a:a,
                    tbarArchive:this.tbarArchive,
                    EditorGrid:this.EditorGrid,
                    title:WtfGlobal.getLocaleText("crm.CONTACT"),//'Contact',
                    keyid:'id',
                    valueid:"contactid",
                    table:'Contact',
                    GridId:'ContactGrid',
                    homePanId:temp[1],
                    archivedPanId:'ContactArchivePanel'+this.mapid,
                    name:"name",
                    valueName:"lastname"
                };
            } else {
                data={
                    a:a,
                    tbarArchive:this.tbarArchive,
                    EditorGrid:this.EditorGrid,
                    title:WtfGlobal.getLocaleText("crm.OPPORTUNITY"),//'Opportunity',
                    keyid:'id',
                    valueid:"oppid",
                    table:'Opportunity',
                    GridId:'OppGrid',
                    homePanId:'OpportunityHomePanel',
                    archivedPanId:'OppArchivePanel'+this.mapid,
                    name:"name",
                    valueName:"lastname"
                };
            }
        }
        var mod = "CrmContact";
        var audit = "46";
        var auditMod = "Contact";
        data["ownerName"] = "contactownerid";
        Wtf.ArchivedGlobal(data, mod, audit,auditMod);
    },
    temp: function(){
        Wtf.highLightGlobal(this.highLightId,this.EditorGrid,this.EditorStore,"contactid");
   }
}); 
