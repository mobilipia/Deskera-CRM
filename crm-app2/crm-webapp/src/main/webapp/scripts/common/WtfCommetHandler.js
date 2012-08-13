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
function subscribeUpdateChannel() {
    if(dojoInitCount <= 0) {
        dojo.cometd.init("../../bind"); 
        dojoInitCount++;
    }
    dojo.cometd.subscribe(crmupdateschannelname+"/"+companyid,"globalUpdateRecordsPublishHandler");
    dojo.cometd.subscribe(channelUpgradeLink+"/"+companyid,"upgradeDeskeraLink");
    dojo.cometd.subscribe(spreadsheetConfigLink+"/"+loginid,"getSpreadsheetconfig");
}

function upgradeDeskeraLink(response) {
	var res = eval("("+response.data+")");
	if(res.isFree && !Wtf.get('crmupgradelink')){
		Wtf.DomHelper.append('shortcuts','| <span id=\'crmupgradelink\' class=\'upgradeLink\'> <a onclick="upgradeCRM()" href=#  wtf:qtip=\'Upgrade Deskera\'>&nbsp;Upgrade Deskera&nbsp;</a> </span> ');
	}
}

function getmoduleStore(moduleName) {
    var modulesheet = Wtf.getCmp(moduleName+'HomePanel');
    return modulesheet;
}

function globalUpdateRecordsPublishHandler(response) {
    var res = eval("("+response.data+")");
    var owner = res.owner;
    var newRandomNum = res.randomnumber;
    var spreadSheetPanelArray =[];
    var moduleName = res.moduleName;
    var operationCode = res.operationcode;
    var moduleRecId =res.moduleRecId;
    var data = res.data;
    if(owner != loginid/* && (userRe.test(usersList) || companyRe.test(usersList))*/) {
        if(operationCode == 1/* && (userRe.test(managerUsers) || companyRe.test(usersList))*/) { // added new record, new record changes reflected for only his managers interface
            var recordObj = data[0];
            spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,recordObj);
            if(spreadSheetPanelArray.length > 0) {
                if(isAffectedUser(recordObj, moduleName)) {
                    if(Wtf.commetData[moduleName])
                        data = Wtf.commetData[moduleName].concat(data);
                    Wtf.commetData[moduleName] = data;
                    var cmpId = 'cometpublishnotification'+moduleName;
                    if(Wtf.getCmp(cmpId)) {
                       Wtf.getCmp(cmpId).close();
                    }
                    new Wtf.ux.Notification({
                        iconCls:	'x-icon-information',
                        title:	  'Notification',
                        id : cmpId,
                        html:	   'New record(s) created by other user for module <b>'+moduleName+'</b>. <br/>Please, refresh the grid to see the changes.', //<a href="#" onclick="makeNewRecordChanges(\''+escape(response.data)+'\',\''+moduleName+'\',\''+moduleRecId+'\')">Add Changes?</a>',
                        autoDestroy: false/*,
                        hideDelay:  5000,
                        listeners: {
                            'beforerender': function(){
                                Sound.enable();
                                Sound.play('notify.wav');
                                Sound.disable();
                            }
                        }*/
                    }).show(document);
                }
            }
        } else if(operationCode == 2) { // updated record
            var isMassUpdate = data[0].ismassedit;
            var recordIndexes = data[0][moduleRecId].split(",");
            if(isMassUpdate) {
                massUpdate(data, moduleName, moduleRecId, recordIndexes);
            } else {
                recordObj = data[0];
                updateSingleCell(recordObj, moduleName, moduleRecId, recordIndexes, true) // flag to highlight row
            }
        } else if(operationCode == 3) { // 3 = deleted records
            var recordIDS = data[0].ids;
            removeRecordsFromStore(recordIDS, moduleName, moduleRecId, moduleRecId);
            var cmpId = 'cometpublishnotification'+moduleName;
            if(Wtf.getCmp(cmpId)) {
               Wtf.getCmp(cmpId).close();
            }
            new Wtf.ux.Notification({
                iconCls:	'x-icon-information',
                title:	  'Notification',
                id : cmpId,
                html:	   'Record(s) deleted by other user for module <b>'+moduleName+'</b>. <br/> <a href="#" onclick="reloadStoreAfterRecordUnarchived(\''+escape(response.data)+'\',\''+moduleName+'\')">Refresh Grid?</a>',
                autoDestroy: false
            }).show(document);
        } else if(operationCode == 4) { // archived records
            recordIDS = data[0].ids;
            removeRecordsFromStore(recordIDS, moduleName, 'id', moduleRecId);
            var cmpId = 'cometpublishnotification'+moduleName;
            if(Wtf.getCmp(cmpId)) {
               Wtf.getCmp(cmpId).close();
            }
            new Wtf.ux.Notification({
                iconCls:	'x-icon-information',
                title:	  'Notification',
                id : cmpId,
                html:	   'Record(s) archived by other user for module <b>'+moduleName+'</b>. <br/> <a href="#" onclick="reloadStoreAfterRecordUnarchived(\''+escape(response.data)+'\',\''+moduleName+'\')">Refresh Grid?</a>',
                autoDestroy: false
            }).show(document);
            
        } else if(operationCode == 5) { // unarchive records
            recordIDS = data[0].ids;
            spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,undefined, recordIDS[0].id);
            if(spreadSheetPanelArray.length > 0) {
                if(isAffectedUser(recordIDS[0],moduleName)) {
                    cmpId = 'cometpublishnotification'+moduleName;
                    if(Wtf.getCmp(cmpId)) {
                       Wtf.getCmp(cmpId).close();
                    }
                    new Wtf.ux.Notification({
                        iconCls:	'x-icon-information',
                        title:	  'Notification',
                        id : cmpId,
                        html:	   'Record(s) restored/unarchived by other user for module <b>'+moduleName+'</b>. <br/> <a href="#" onclick="reloadStoreAfterRecordUnarchived(\''+escape(response.data)+'\',\''+moduleName+'\')">Refresh Grid?</a>',
                        autoDestroy: false
                    }).show(document);
                }
            }
        }
        else if(operationCode == 9) { // Add Comment
        	if(res.data[0].isCommentAdded){
        		addCommentToAll(moduleName,res.data[0].recid);
        	}
       }
    } else if(owner == loginid  && random_number && newRandomNum && newRandomNum !=random_number) {
        if(operationCode == 1) { // added new record, new record changes reflected for only his managers interface
            recordObj = data[0];
            spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,recordObj);
            if(spreadSheetPanelArray.length > 0) {
                if(Wtf.commetData[moduleName])
                    data = Wtf.commetData[moduleName].concat(data);
                Wtf.commetData[moduleName] = data;
                makeNewRecordChanges(escape(response.data),moduleName,moduleRecId);
            }
        } else if(operationCode == 2) { // updated record
            isMassUpdate = data[0].ismassedit;
            recordIndexes = data[0][moduleRecId].split(",");
            if(isMassUpdate) {
                massUpdate(data, moduleName, moduleRecId, recordIndexes);
            } else {
                recordObj = data[0];
                updateSingleCell(recordObj, moduleName, moduleRecId, recordIndexes, true) // flag to highlight row
            }
        } else if(operationCode == 3) { // 3 = deleted records
            recordIDS = data[0].ids;
            removeRecordsFromStore(recordIDS, moduleName, moduleRecId, moduleRecId);
        } else if(operationCode == 4) { // 4 = archived records
            recordIDS = data[0].ids;
            removeRecordsFromStore(recordIDS, moduleName, "id", moduleRecId);
        } else if(operationCode == 5) { // unarchive records
            reloadStoreAfterRecordUnarchived(escape(response.data),moduleName);
        } else if(operationCode == 7) { // spread-sheet config
            spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,recordObj);
            for(var spreadPanelCnt=0; spreadPanelCnt < spreadSheetPanelArray.length; spreadPanelCnt++) {
                spreadSheetPanelArray[spreadPanelCnt].spreadSheet.getMyConfig(true); // iscustomHeader flag for reportMenu
            }
        }else if(operationCode == 9) { // Add Comment
        	if(res.data[0].isCommentAdded){
        		addCommentToAll(moduleName,res.data[0].recid);
        	}
       }
    }
}

function isAffectedUser(record, moduleName) {
    var ownerId;var isAffected = true;
   switch(moduleName) {
        case Wtf.crmmodule.campaign:
            ownerId = 'campaignownerid';
            break;
        case Wtf.crmmodule.lead:
            ownerId = 'leadownerid';
            break;
        case Wtf.crmmodule.account:
            ownerId = 'accountownerid';
            break;
        case Wtf.crmmodule.contact:
            ownerId = 'contactownerid';
            break;
        case Wtf.crmmodule.opportunity:
            ownerId = 'oppownerid';
            break;
        case Wtf.crmmodule.cases:
            ownerId = 'caseownerid';
            break;
        case Wtf.crmmodule.product:
            ownerId = 'ownerid';
            break;
        case Wtf.crmmodule.activity:
            ownerId = 'ownerid';
            break;
    }
    var ownerStore = getModuleOwnerStore(moduleName);
//    if(ownerId) {
//        if(searchValueField(ownerStore, record[ownerId], "id", undefined, true)==null)
//            isAffected = false;
//    }
    return isAffected;
}

function removeRecordsFromStore(recordIDS, moduleName, dataIndexName, moduleRecId) {
    for(var cnt=0; cnt<recordIDS.length; cnt++) {
        var deleteId = recordIDS[cnt][dataIndexName];
        if(moduleName != Wtf.crmmodule.activity)
            var spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,undefined, deleteId);
        else
            spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,recordIDS[cnt], deleteId);
        for(var spreadPanelCnt=0; spreadPanelCnt < spreadSheetPanelArray.length; spreadPanelCnt++) {
            var spreadSheetPanel = spreadSheetPanelArray[spreadPanelCnt];
            var grid = spreadSheetPanel.EditorGrid;
            var store = spreadSheetPanel.EditorStore;
            var deleteRecord = searchValueField(store, deleteId, moduleRecId, undefined, true);
            if(deleteRecord && deleteRecord !=null) {
                store.remove(deleteRecord);
                Wtf.arrangeGridNumbererAdd(0,grid);
                spreadSheetPanel.totalCount -= 1;
                Wtf.arrangeGridNumbererAdd(0,grid);
                //spreadSheetPanel.spreadSheet.pP.updatePagingMsg(store.getCount()-1, spreadSheetPanel.totalCount);
            }
        }
    }
}

function updateSingleCell(recordObj, moduleName, moduleRecId, recordIndexes, IshighLightRow) {  // flag to highlight row
    var spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,recordObj);
    for(var spreadPanelCnt=0; spreadPanelCnt < spreadSheetPanelArray.length; spreadPanelCnt++) {
        var spreadSheetPanel = spreadSheetPanelArray[spreadPanelCnt];
        var columnArray = spreadSheetPanel.spreadSheet.colModel;
        var store = spreadSheetPanel.EditorStore;
        var recIndex = store.find(moduleRecId,recordIndexes[0]);
        if(recordObj.dirtyfield) {
            var prop = recordObj.dirtyfield;
            if(recIndex >= 0){
                var record = store.getAt(recIndex);
                setRecordPropValue(recordObj, record, columnArray, moduleName, prop);
                if(recordObj.validflag != record.validflag)
                    setRecordPropValue(recordObj, record, columnArray, moduleName, "validflag");
                if(IshighLightRow) {
                    // highLight newly added record
                    Wtf.onlyhighLightRow(spreadSheetPanel.EditorGrid,"FFFF00",5, recIndex);
                }
                updateAppElements(moduleName, spreadSheetPanel, recordObj, record.validflag,prop);
            } else if(recIndex==-1) {
                updateNewRecordLocalCollection(moduleName, moduleRecId, recordObj)
            }
        }
    }
}

function addCommentToAll(moduleName,RecId){
	var spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,undefined);
    for(var spreadPanelCnt=0; spreadPanelCnt < spreadSheetPanelArray.length; spreadPanelCnt++) {
      	var spreadSheetPanel = spreadSheetPanelArray[spreadPanelCnt];
        var store = spreadSheetPanel.EditorStore;
       // var panelid=spreadSheetPanel.getId();
        for(var i=0;i<store.getCount();i++){
           	var rec=store.getAt(i);
           	for(k=0;k<rec.fields.items.length;k++){
           		if(rec.get(rec.fields.items[k].name)==RecId){
                	Wtf.MessageBox.confirm("Confirm Reload","New comment has been added in the module <b>"+moduleName+'</b>.'+'<br/> Would you like to <b>refresh</b> the grid?', function(btn){
                		if(btn=="yes"){
                			//var tabpan=Wtf.getCmp(panelid);
                			if(!this.rendered)
                				this.activeTab=spreadSheetPanel;
                            store.reload();
                            
                 		}
                	});
           			return;
           		}
           	}
        }
    }
}

function massUpdate(data, moduleName, moduleRecId, recordIndexes) {
    for(i=0;i<data.length;i++) {
        var recordObj = data[i];
        var spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,recordObj);
        for(var spreadPanelCnt=0; spreadPanelCnt < spreadSheetPanelArray.length; spreadPanelCnt++) {
            var spreadSheetPanel = spreadSheetPanelArray[spreadPanelCnt];
            var columnArray = spreadSheetPanel.spreadSheet.getGrid().colModel;
            var store = spreadSheetPanel.EditorStore;
            var recarr=[];
            for(var j=0;j<recordIndexes.length;j++) {
                var recIndex = store.find(moduleRecId,recordIndexes[j]);
                if(recIndex >= 0) {
                    var record = store.getAt(recIndex);
                    setRecordValues(recordObj, record, columnArray, moduleName);
                    recarr.push(recIndex);
                } else if(recIndex==-1) {
                    var localData = Wtf.commetData[moduleName];
                    delete Wtf.commetData[moduleName];
                    for(var i=0;i<localData.length;i++) {
                        var storedRecordObj = localData[i];
                        if(storedRecordObj[moduleRecId] == recordObj[moduleRecId]) {
                            for(var prop in recordObj) {
                                if(storedRecordObj[prop]!=undefined) {
                                    storedRecordObj[prop] =recordObj[prop];
                                }
                            }
                            break;
                        }
                    }
                    Wtf.commetData[moduleName] = localData;
                }
            }
            store.commitChanges();
            var view=spreadSheetPanel.spreadSheet.getGrid().getView();
            for(k=0;k<recarr.length;k++){
            	Wtf.get(view.getRow(recarr[k])).highlight("E5F06E", { attr: 'background-color', duration: 5 });
            }
            new Wtf.ux.Notification({
                iconCls:	'x-icon-information',
                title:	  'Notification',
                html:'<b>Records have been updated for '+moduleName+'</b>.',
                autoDestroy: true
            }).show(document);
        }
    }

}

function updateNewRecordLocalCollection(moduleName, moduleRecId, recordObj) {
    var localData = Wtf.commetData[moduleName];
    delete Wtf.commetData[moduleName];
    for(var i=0;i<localData.length;i++) {
        var storedRecordObj = localData[i];
        if(storedRecordObj[moduleRecId] == recordObj[moduleRecId]) {
            localData[i] = recordObj;
            break;
        }
    }
    Wtf.commetData[moduleName] = localData;
}

function makeNewRecordChanges(response,moduleName,moduleRecId) {
    var notifiWin = Wtf.getCmp("cometpublishnotification"+moduleName);
    if(notifiWin)
        notifiWin.close();
    var data = Wtf.commetData[moduleName];
    delete Wtf.commetData[moduleName];
    for(var i=0;i<data.length;i++) {
        var recordObj = data[i];
        var spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,recordObj);
        for(var spreadPanelCnt=0; spreadPanelCnt < spreadSheetPanelArray.length; spreadPanelCnt++) {
            var spreadSheetPanel = spreadSheetPanelArray[spreadPanelCnt];
            var grid = spreadSheetPanel.EditorGrid;
            var columnArray = spreadSheetPanel.spreadSheet.colModel;
            var store = spreadSheetPanel.EditorStore;
            //setRecordValues(recordObj, spreadSheetPanel.newRec, columnArray, moduleName);
            // highLight newly added record
//            Wtf.onlyhighLightRow(spreadSheetPanel.EditorGrid,"FFFF00",5, 0);
            //spreadSheetPanel.addNewRec(); // add blank row at first position
            spreadSheetPanel.totalCount += 1;
            Wtf.arrangeGridNumbererAdd(0,grid);
            spreadSheetPanel.spreadSheet.pP.updatePagingMsg(store.getCount()-1, spreadSheetPanel.totalCount);
//            Wtf.getCmp("pagingtoolbar" + spreadSheetPanel.spreadSheet.id).updateInfo();
            if(recordObj.validflag == 1) {
                updateAppElements(moduleName, spreadSheetPanel, recordObj, 0);
            }
        }
    }
}

function reloadStoreAfterRecordUnarchived(response,moduleName) {
    var notifiWin = Wtf.getCmp("cometpublishnotification"+moduleName);
    if(notifiWin)
        notifiWin.close();
    var res = eval("("+unescape(response)+")");
    var data = res.data;
    var recordIDS = data[0].ids;
    var storeObj = {};
    for(var cnt=0; cnt<recordIDS.length; cnt++) {
        var deleteId = recordIDS[cnt].id;
        var spreadSheetPanelArray = getActivespreadSheetPanel(moduleName,undefined, deleteId);
        for(var spreadPanelCnt=0; spreadPanelCnt < spreadSheetPanelArray.length; spreadPanelCnt++) {
            var spreadSheetPanel = spreadSheetPanelArray[spreadPanelCnt];
//            var columnArray = spreadSheetPanel.spreadSheet.SpreadSheetGrid.colModel;
            var store = spreadSheetPanel.EditorStore;
            storeObj[spreadSheetPanel.id] = store;
        }
    }

    for(var key in storeObj) {
        storeObj[key].load({params : storeObj[key].lastOptions.params});
    }
}
// need to update master store, left-tree and dashboards
function updateAppElements(moduleName, spreadSheetPanel, recordObj, oldValidFlag, field) {
    if(moduleName == Wtf.crmmodule.account) {
        spreadSheetPanel.afterValidRecordSaved(undefined,recordObj,recordObj.validflag,oldValidFlag,field);
    } else if(moduleName == Wtf.crmmodule.contact) {
        spreadSheetPanel.afterValidRecordSaved(undefined, recordObj.validflag, field);
    } else if(moduleName == Wtf.crmmodule.lead) {
        spreadSheetPanel.afterValidRecordSaved(undefined,recordObj,recordObj.validflag,oldValidFlag);
    } else if(moduleName == Wtf.crmmodule.product) {
        spreadSheetPanel.afterValidRecordSaved(undefined,recordObj,recordObj.validflag,field);
    } else if(moduleName == Wtf.crmmodule.cases) {
        spreadSheetPanel.afterValidRecordSaved(undefined,recordObj,recordObj.validflag);
    } else if(moduleName == Wtf.crmmodule.opportunity) {
        spreadSheetPanel.afterValidRecordSaved(undefined,recordObj,recordObj.validflag);
    } else if(moduleName == Wtf.crmmodule.campaign) {
        spreadSheetPanel.afterValidRecordSaved(undefined,recordObj,recordObj.validflag);
    }
}
function setRecordValues (responseRecord, storeRecord, columnArray, moduleName) {
    for(var prop in responseRecord) {
        setRecordPropValue(responseRecord, storeRecord, columnArray, moduleName, prop);
    }
}

function setRecordPropValue (responseRecord, storeRecord, columnArray, moduleName, prop) {
    var keyRe = new RegExp("Custom");
    var value = responseRecord[prop];
    if(typeof prop == "string" && prop.match(keyRe)){ // for custom column
        var fieldArray = responseRecord.customfield;
        for(var customCnt=0; customCnt< fieldArray.length; customCnt++) {
            if(fieldArray[customCnt][prop]) {
                value = fieldArray[customCnt][fieldArray[customCnt][prop]];
                break;
            }
        }
    }
    if(storeRecord.fields.containsKey(prop) && prop!="id") {
        var columnObject = columnArray.getColumnById(getColumnIdByDataIndex(columnArray.config,prop));
        if(columnObject) {
            if(columnObject.xtype && columnObject.xtype!='datefield') {
                if(moduleName == Wtf.crmmodule.activity && columnObject.xtype && columnObject.xtype=='timefield') {
                    activityStEndTimeSet(responseRecord, storeRecord);
                } else
                    storeRecord.set(prop,value);
            } else if(columnObject.xtype && columnObject.xtype=='datefield' && value) {
                if(moduleName == Wtf.crmmodule.activity && prop=="startdate" || prop=="enddate") {
                    activityStEndTimeSet(responseRecord, storeRecord)
                } else {
                    storeRecord.set(prop, WtfGlobal.convertToUserTimezone(value));
                }
            } else {
                storeRecord.set(prop,value);
            }
        } else {
            storeRecord.set(prop,value);
        }
    }
}

function activityStEndTimeSet(responseRecord, storeRecord) {
    if(responseRecord['startdate'] && responseRecord['startdate']!="") {
        var userTZDate = WtfGlobal.convertToUserTimezone(responseRecord['startdate']);
        storeRecord.set("startdate",userTZDate);
        storeRecord.set('starttime',userTZDate.format(WtfGlobal.getLoginUserTimeFormat()));
    }

    if(responseRecord['enddate'] && responseRecord['enddate']!="") {
        userTZDate = WtfGlobal.convertToUserTimezone(responseRecord['enddate']);
        storeRecord.set("enddate",userTZDate);
        storeRecord.set('endtime',userTZDate.format(WtfGlobal.getLoginUserTimeFormat()));
    }
}
function getActivespreadSheetPanel(moduleName,recordObj,id) {
    var activePanelArray = [];
    var index = 0;
    var spreadSheetPanel = Wtf.getCmp(moduleName+'HomePanel');
    if(spreadSheetPanel)
        activePanelArray[index++] = spreadSheetPanel;
    switch(moduleName) {
        case Wtf.crmmodule.lead :
            spreadSheetPanel = Wtf.getCmp('LeadArchivePanel');
            if(spreadSheetPanel)
                activePanelArray[index++] = spreadSheetPanel;
            break;
        case Wtf.crmmodule.account :
            spreadSheetPanel = Wtf.getCmp('AccountArchivePanel');
            if(spreadSheetPanel)
                activePanelArray[index++] = spreadSheetPanel;
            break;
        case Wtf.crmmodule.contact :
            if(recordObj) {
                var newId = recordObj.accountid;
            }
            spreadSheetPanel = Wtf.getCmp('ContactArchivePanel' + "0");
            if(spreadSheetPanel)
                activePanelArray[index++] = spreadSheetPanel;
            if(id || newId) {
                id = newId;
                spreadSheetPanel = Wtf.getCmp('contactAccountTab' + id);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;
                spreadSheetPanel = Wtf.getCmp('contactOppTab' + id);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;

                spreadSheetPanel = Wtf.getCmp('ContactArchivePanels' +id);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;
            }
            // for lead's contact
            newId = undefined;
            if(recordObj) {
                newId = recordObj.leadid;
            }
            if(newId) {
                id = newId;
                spreadSheetPanel = Wtf.getCmp('leadContactTab' + id);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;
            }
            break;
        case Wtf.crmmodule.opportunity :
            spreadSheetPanel = Wtf.getCmp('OppArchivePanel' + "0");
            if(spreadSheetPanel)
                activePanelArray[index++] = spreadSheetPanel;
            if(recordObj) {
                newId = recordObj.accountnameid;
            }
            if(id || newId) {
                id = newId;
                spreadSheetPanel = Wtf.getCmp('oppAccountTab' + id);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;

                spreadSheetPanel = Wtf.getCmp('OppArchivePanel' + id);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;
            }
            break;
        case "Cases" :
        case Wtf.crmmodule.cases :
            spreadSheetPanel = Wtf.getCmp('CaseArchivePanel' + "0");
            if(spreadSheetPanel)
                activePanelArray[index++] = spreadSheetPanel;

            if(recordObj) {
                newId = recordObj.accountnameid;
            }
            if(id || newId) {
                id = newId;
                spreadSheetPanel = Wtf.getCmp('accountCaseTab' + id);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;

                spreadSheetPanel = Wtf.getCmp('CaseArchivePanel' + id);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;
            }
            break;
        case Wtf.crmmodule.activity :
            var parentModuleName = recordObj.relatedtoid;
            var parentModuleId = recordObj.relatedtonameid;
            spreadSheetPanel = Wtf.getCmp('accountActTab' + parentModuleId);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;
            var activityParentPanelArray = getActivespreadSheetPanel(parentModuleName,recordObj,parentModuleId)
            for (var parentCnt = 0; parentCnt < activityParentPanelArray.length; parentCnt++) {
                var parentId = activityParentPanelArray[parentCnt].id;
                spreadSheetPanel = Wtf.getCmp(parentId + 'activityCampaignTab' + parentModuleId);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;
                spreadSheetPanel = Wtf.getCmp(parentId + 'activityCaseTab' + parentModuleId);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;
                spreadSheetPanel = Wtf.getCmp(parentId + 'activityLeadTab' + parentModuleId);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;
                spreadSheetPanel = Wtf.getCmp(parentId + 'activityOppTab' + parentModuleId);
                if(spreadSheetPanel)
                    activePanelArray[index++] = spreadSheetPanel;
            }
            break;

    }
    return activePanelArray;
}
function getColumnIdByDataIndex(columnArray,index) {
    var columnId = "";
    for(var i = 0, len = columnArray.length; i < len; i++){
        if(columnArray[i].dataIndex == index){
            columnId = columnArray[i].id;
            return columnId;
        }
    }
    return columnId;
}

function getSpreadsheetconfig(response) {
    var res = eval("("+response.data+")");
    var module = res.module
    if(!GlobalSpreadSheetConfig[module]) {
        GlobalSpreadSheetConfig[module] = res.data;
        getQuickSearchEmptyText(res.data.Header,module);
    }
}

function getQuickSearchEmptyText(header,module) {
    var qkSrcTxt ="";
  
    if(!GlobalQuickSearchEmptyText[module]) {
        
        if(module==Wtf.crmmodule.product){
            qkSrcTxt=WtfGlobal.getLocaleText("crm.searchtxt.productmodule");//"Search by Product Name,Category or Vendor Name";
            for(var i=0; i< header.length; i++){
               
                if(header[i].oldheader=="Product Name" || header[i].oldheader=="Category" || header[i].oldheader=="Vendor Name"){
                    qkSrcTxt = getQuoteStrippedText(qkSrcTxt,header[i].oldheader, header[i].newheader);
                }
            }
            GlobalQuickSearchEmptyText[Wtf.crmmodule.product] = qkSrcTxt;

        } else if(module==Wtf.crmmodule.lead){
            qkSrcTxt=WtfGlobal.getLocaleText("crm.searchtxt.leadmodule");//"Search by Last Name/ Company Name or Title/Designation";
            for(var i=0; i< header.length; i++){

                if(header[i].oldheader=="Last Name/ Company Name" || header[i].oldheader=="Title/Designation"){
                    qkSrcTxt = getQuoteStrippedText(qkSrcTxt,header[i].oldheader, header[i].newheader);
                }
            }
            GlobalQuickSearchEmptyText[Wtf.crmmodule.lead] = qkSrcTxt;

        } else if(module==Wtf.crmmodule.account){
            qkSrcTxt=WtfGlobal.getLocaleText("crm.searchtxt.accountmodule");//"Search by Account Name or Website";
            for(var i=0; i< header.length; i++){

                if(header[i].oldheader=="Account Name" || header[i].oldheader=="Website"){
                   qkSrcTxt = getQuoteStrippedText(qkSrcTxt,header[i].oldheader, header[i].newheader);
                }
            }
            GlobalQuickSearchEmptyText[Wtf.crmmodule.account] = qkSrcTxt;

        } else if(module==Wtf.crmmodule.contact){
            qkSrcTxt=WtfGlobal.getLocaleText("crm.searchtxt.contactmodule");//"Search by First Name, Last Name or Title/Designation";
            for(var i=0; i< header.length; i++){

                if(header[i].oldheader=="First Name" || header[i].oldheader=="Last Name" || header[i].oldheader=="Title/Designation"){
                    qkSrcTxt = getQuoteStrippedText(qkSrcTxt,header[i].oldheader, header[i].newheader);
                }
            }
            GlobalQuickSearchEmptyText[Wtf.crmmodule.contact] = qkSrcTxt;

        } else if(module==Wtf.crmmodule.opportunity){
            qkSrcTxt=WtfGlobal.getLocaleText("crm.searchtxt.opportunuitymodule");//"Search by Opportunity Name";
            for(var i=0; i< header.length; i++){

                if(header[i].oldheader=="Opportunity Name"){
                    qkSrcTxt = getQuoteStrippedText(qkSrcTxt,header[i].oldheader, header[i].newheader);
                }
            }
            GlobalQuickSearchEmptyText[Wtf.crmmodule.opportunity] = qkSrcTxt;
            
        }else if(module==Wtf.crmmodule.cases){
            qkSrcTxt=WtfGlobal.getLocaleText("crm.searchtxt.casemodule");//"Search by Subject";
            for(var i=0; i< header.length; i++){

                if(header[i].oldheader=="Subject"){
                    qkSrcTxt = getQuoteStrippedText(qkSrcTxt,header[i].oldheader, header[i].newheader);
                }
            }
            GlobalQuickSearchEmptyText[Wtf.crmmodule.cases] = qkSrcTxt;
            
        }else if(module==Wtf.crmmodule.campaign){
            qkSrcTxt=WtfGlobal.getLocaleText("crm.searchtxt.campaignmodule");//"Search by Campaign Name,Objective or Type";
            for(var i=0; i< header.length; i++){

                if(header[i].oldheader=="Campaign Name" || header[i].oldheader=="Objective" || header[i].oldheader=="Type"){
                    qkSrcTxt = getQuoteStrippedText(qkSrcTxt,header[i].oldheader, header[i].newheader);
                }
            }
            GlobalQuickSearchEmptyText[Wtf.crmmodule.campaign] = qkSrcTxt;
        }
       
    }
}

function getQuoteStrippedText(text,replaceTo,replaceWith){
    text=WtfGlobal.replaceAll(text, replaceTo, replaceWith);
    text=WtfGlobal.replaceAll(text, "&#39;", "\'");
    text=WtfGlobal.replaceAll(text, "&quot;", "\"");
    
    return text;
}
//
//function updateWidget(response) {
//    var res = eval("("+response.data+")");
//}
