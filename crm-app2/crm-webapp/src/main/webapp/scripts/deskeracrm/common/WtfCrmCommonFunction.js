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
Wtf.common.leadStatusID_Qualified = "f01e5a6f-7011-4e2d-b93e-58b5c6270239";
Wtf.common.leadStatusID_PreQualified = "94b9007e-696b-4e1b-9b97-0866dbc10c01";
Wtf.common.campaign_emailMarketID = "b0e71040-b46d-4fc0-bfe3-1fccca96016f";

Wtf.common.accountModuleID = "2904a010-7d32-11df-8c4a-0800200c9a66";
Wtf.common.leadModuleID = "e1e72896-bf85-102d-b644-001e58a64cb6";
Wtf.common.contactModuleID = "8B9B8DB6-7E03-11DF-BC3F-FC8FDFD72085";
Wtf.common.caseModuleID = "41f2d2e6-349a-4864-8bcd-35c1aae9a227";
Wtf.common.productModuleID = "ab60263f-7c72-4727-965c-85effb77e81f";
Wtf.common.oppModuleID = "14a254d9-2a2b-4c7f-8353-023586078f77";
Wtf.common.userModuleID = "users";

Wtf.common.lstatus_Combo = "60548516-75db-472e-8d75-00b40696ecf6";
Wtf.common.lsource_Combo = "9dc0376d-0323-4aa3-8092-67956ad5728b";
Wtf.common.caseStatus_Combo = "500d6f95-a6ab-4f5f-bb26-2ef6161760a1";
Wtf.common.viewType_Combo = "0d028292-17cd-4202-892c-31e337aa510a";
Wtf.common.viewStatus_Combo = "bad22fb4-6743-42ed-98c4-30d79782824b";
Wtf.common.title_Combo = "eb3fa09e-0ca5-4291-9bd1-1dce5b889e4d";
Wtf.common.lrating_Combo = "70b1c417-dd3f-4572-8db6-7cf5bafb3ba8";
Wtf.common.industry_Combo = "fc7c4e27-56bf-4524-a146-41d873829345";
Wtf.common.productcategory_Combo = "e30643eb-2416-4a9d-a70e-d5268995891d";
Wtf.common.productmanufacturer_Combo = "f84e5983-1836-4734-b425-54e69c6088ac";
Wtf.common.accountType_Combo = "24761544-c561-43eb-aa94-f5bcfad31dcb";
Wtf.common.opptype_Combo = "e47acf7c-cfe1-49e3-8fc5-9294f40a5631";
Wtf.common.region_Combo = "b3090c63-8ff9-43af-ab3f-25721a86290c";
Wtf.common.oppstage_Combo = "d49609c2-0abc-47ce-8d5a-5850c03b7291";
Wtf.common.caseorigin_Combo = "6614300c-eb30-4f1e-86db-4f693ee272b0";
Wtf.common.cpriority_Combo = "c82d6115-6c38-4d1f-abb2-3b75ec30a1df";
Wtf.common.type_Combo = "1baae1fc-2a21-4582-8369-56f3aab3725f";
Wtf.common.status_Combo = "cce4b390-5f73-4687-b9d1-fb8c2bad98c9";

Wtf.loadMasterStore = function(configid) { // called from master configuration tab
    if(configid==Wtf.common.lstatus_Combo)
        Wtf.lstatusStore.load();

    else if(configid==Wtf.common.lsource_Combo)
        Wtf.lsourceStore.load();

    else if(configid==Wtf.common.caseStatus_Combo)
        Wtf.caseStatusStore.load();

    else if(configid==Wtf.common.viewType_Combo)
        Wtf.viewStoreType.load();

    else if(configid==Wtf.common.viewStatus_Combo)
        Wtf.viewStoreStatus.load();

    else if(configid==Wtf.common.title_Combo)
        Wtf.titleStore.load();

    else if(configid==Wtf.common.lrating_Combo)
        Wtf.lratingStore.load();

    else if(configid==Wtf.common.industry_Combo)
        Wtf.industryStore.load();

    else if(configid==Wtf.common.productcategory_Combo)
        Wtf.productcategorystore.load();

    else if(configid==Wtf.common.productmanufacturer_Combo)
        Wtf.productmanufacturerstore.load();

    else if(configid==Wtf.common.accountType_Combo)
        Wtf.accountTypeStore.load();

    else if(configid==Wtf.common.opptype_Combo)
        Wtf.opptypeStore.load();

    else if(configid==Wtf.common.region_Combo)
        Wtf.regionStore.load();

    else if(configid==Wtf.common.oppstage_Combo)
        Wtf.oppstageStore.load();

    else if(configid==Wtf.common.caseorigin_Combo)
        Wtf.caseoriginStore.load();

    else if(configid==Wtf.common.cpriority_Combo)
        Wtf.cpriorityStore.load();

    else if(configid==Wtf.common.type_Combo)
        Wtf.typeStore.load();

    else if(configid==Wtf.common.status_Combo)
        Wtf.statusStore.load();
}

function getModuleNameFromIntegerID(intVal) {
    intVal += '';
    var moduleName = "";
    switch(intVal) {
        case "2"  : moduleName = "Lead"; break;
        case "6" : moduleName = "Contact"; break;
        case "1" : moduleName = "Account"; break;
        case "3" : moduleName = "Case"; break;
        case "5" : moduleName = "Opportunity"; break;
        case "4" : moduleName = "Product"; break;
    }
    return moduleName;
}
function getRecords(arr,store,keyid,valueid){
    var records=[];
    var record;
    var index;
    for(var i=0;i<arr.length;i++){
        index=store.find(valueid,arr[i][keyid]);
        if(index != -1){
            record= store.getAt(index);
            if(record)
                records.push(record);
        }
    }
    return records;
}

Wtf.deleteGlobal = function (EditorGrid,EditorStore,title,keyid,valueid,table,msg1,msg2,msg3,treeid)
    {
        var selectionModel = EditorGrid.getSelectionModel();
        var selectedRowCount = selectionModel.getCount();
        if(selectedRowCount>0){
            var tob=Wtf.getCmp('tree');
            var moduleNode=tob.getNodeById(treeid);

            Wtf.MessageBox.show({

                title: WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//"Confirm",
                msg: WtfGlobal.getLocaleText({key:"crm.editor.generaldeletemsg",params:[title]}),//"Are you sure you want to delete selected "+title+"?<br><br><b>Note: This data cannot be retrieved later.",
                buttons: Wtf.MessageBox.OKCANCEL,
                animEl: 'upbtn',
                icon: Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(bt){
                    if(bt=="ok"){
                        var s=selectionModel.getSelections();
                        var openTabCount = 0;
                        var jsondata="";
                        var emailcampaigndata="";
                        for(var i=0;i<selectedRowCount;i++) {
                            if(s[i].get("validflag") != -1 ) {

                                if(table == Wtf.crmmodule.campaign){ // Check for open email campaign tabs before deleting
                                    if(Wtf.getCmp("campaigndetail"+s[i].get(valueid))!=null){
                                        emailcampaigndata +=" "+s[i].data.campaignname+",";
                                        openTabCount++;
                                    }
                                }

                                if(openTabCount == 0){
                                    jsondata+="{'"+keyid+"':'" + s[i].get(valueid) + "'";
                                    if(table == Wtf.crmmodule.activity) {
                                        jsondata+=",'relatedtoid':'" + s[i].get("relatedto") + "'";
                                        jsondata+=",'relatedtonameid':'" + s[i].get("relatednameid") + "'";
                                    }
                                    jsondata+="},";
                                }
                            }
                        }
                        if(openTabCount > 0){
                            var emailcampaginMsg =WtfGlobal.getLocaleText({key:"crm.common.delcampdatamsg",params:[emailcampaigndata]});//"<b> The following Email Campaign is already in use. Please close the tab before deleting </b><br><br>"+emailcampaigndata;
//                            if(openTabCount>1){
//                                emailcampaginMsg ="<b> The following Email Campaigns are already in use. Please close the tabs before deleting them.</b><br><br>"+emailcampaigndata;
//                            }
                            emailcampaginMsg = emailcampaginMsg.substring(0, (emailcampaginMsg.length-1));
                            WtfComMsgBox([WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"), emailcampaginMsg],0);

                        } else {
                            var trmLen = jsondata.length - 1;
                            var finalStr = jsondata.substr(0,trmLen);
                            var action = table;
                            if(table == "TargetList")
                                action = "emailMarketing";

                            var url=action+"/action/delete"+table+".do";
                            var refUrl = "common/crmCommonHandler/delete"+table+".do";

                            var widgetObj = [];
                            var moduleWidget = Wtf.moduleWidget;
                                switch(table){//  to check reference of Product,Contact,Account in other modules
                                    case 'Product' :widgetObj[0] = Wtf.getCmp(moduleWidget.product);
                                                    url=refUrl
                                                    break;
                                    case 'Account' :widgetObj[0] = Wtf.getCmp(moduleWidget.account);
                                                    url=refUrl
                                                    break;
                                    case 'Contact' : widgetObj[0] = Wtf.getCmp(moduleWidget.contact);
                                                    url=refUrl
                                                    break;
                                    case 'Lead'    : widgetObj[0] = Wtf.getCmp(moduleWidget.lead);
                                                    break;
                                    case 'Campaign' : widgetObj[0] = Wtf.getCmp(moduleWidget.campaign);
                                                    widgetObj[1] = Wtf.getCmp(moduleWidget.campaignreport);
                                                    url=refUrl;
                                                    break;
                                    case 'Opportunity' : widgetObj[0] = Wtf.getCmp(moduleWidget.opportunity);
                                                    break;
                                    case 'Case' : widgetObj[0] = Wtf.getCmp(moduleWidget.cases);
                                                    break;
                                    case 'Activity' : widgetObj[0] = Wtf.getCmp(moduleWidget.activity);
                                                    break;
                                }

                                Wtf.deleteRequest(s,EditorGrid,EditorStore,finalStr,url,table,widgetObj,keyid,valueid,tob,moduleNode,title,msg1,msg2);
                            }
                        
                    } 
                }
            });
        }
        else {
            Wtf.updateProgress();
            ResponseAlert(msg3);
        }
        EditorStore.on('load',function(){Wtf.updateProgress();},this);
        EditorStore.on('loadexception',function(){Wtf.updateProgress();},this);
    }
Wtf.deleteRequest = function(s,EditorGrid,EditorStore,finalStr,url,table,widgetObj,keyid,valueid,tob,moduleNode,title,msg1,msg2){

    Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.common.deleteloadmsg"));
    Wtf.Ajax.timeout = '5000000';
    Wtf.Ajax.requestEx({
        url:Wtf.req.springBase +url ,
        params:{
            jsondata:finalStr,
            table:table,
            flag:41
        }
    },
    this,
    function(res)
    {
        Wtf.Ajax.timeout = '30000';
        Wtf.updateProgress();
        if(res.failDelete && res.failDelete.length > 0){        
            var deleteFailInterfaceObj= new Wtf.deleteFailInterface({
                failDelete :  res.failDelete,
                module : table,
                successDeleteCount : res.successDeleteArr.length
            });
            deleteFailInterfaceObj.show();

        }
        if( (!res.successDeleteArr) || res.successDeleteArr.length > 0){
            var params = EditorStore.lastOptions.params;
            var pagesize = EditorGrid.getBottomToolbar().pageSize;

            if(params!=undefined){
                params.limit = pagesize;
                EditorStore.load({
                    params : params
                });
            } else {
                EditorStore.load({
                    params:{
                        start:0,
                        limit: pagesize
                    }
                    });
            }
            var records=s;
            if(res.successDeleteArr){
                records=getRecords(res.successDeleteArr,EditorStore,keyid,valueid);
            }
            Wtf.removeSelectedTreeNode(tob, moduleNode, valueid, records);

            if(title=="Lead"||title=="Opportunities"||title=="Account"||title=="Activities")
            {
                bHasChanged = true;
            }
            if( (!res.failDelete) || res.failDelete.length == 0){
                ResponseAlert(msg1);
            }
            switch(table){
                case 'Product' :
                    Wtf.productStore.reload();
                    break;
            }
            for(var cnt =0; cnt< widgetObj.length; cnt++) {
                if (widgetObj[cnt])
                    widgetObj[cnt].callRequest("","",0);
            }
            Wtf.refreshUpdatesAll();
        }
    },
    function()
    {
        Wtf.updateProgress();
        ResponseAlert(msg2);
    }
    )
}
Wtf.filterBydate = function(frmdate,todate,ds,obj) {
    var proceed = true;
    var msg;
    var invalidDate = false;
    var invalidRange = false;
    var cd = "1";
    var fromDate    = (frmdate);
    var toDate    = (todate);
    if(fromDate == "" || toDate == "" ) {
        invalidDate = true;
        msg = 15;
    }else if(fromDate > toDate ) {
        msg = 16;
        invalidDate = true;
        invalidRange = true;
        obj.todate.setValue("");
        proceed = false;
    }
    if(obj.details.helpID != 10 && obj.details.helpID != 17 && obj.details.helpID != 12 && obj.details.helpID != 27 && obj.details.helpID != 28){
        if(invalidDate || invalidRange) {
            proceed = false;
        }

    }else{
        if(!invalidRange && invalidDate &&  (obj.filterCombo.value == undefined || obj.filterCombo.value == "" ) ) {
            if(obj.details.helpID == 10) {
                msg = 652;
            } else if( obj.details.helpID == 27 || obj.details.helpID == 28) {
                msg = 654;
            } else {
                msg = 653;
            }
            proceed = false;
        }
        if(invalidDate || invalidRange) {
            cd = "0";
        }

    }
    if(!proceed) {
        ResponseAlert(msg);
    }else {
        if(!invalidDate) {
            frmdate =fromDate;
            todate = toDate + 86400000 - 1;// added milliseconds for 1 day
        }
        ds.load({
        params:{
            frm:frmdate,
            to:todate,
            cd:cd,
            start:0,
            limit:obj.grid.getBottomToolbar().pageSize,
            year:obj.yearCombo!=undefined?obj.yearCombo.value:undefined ,
            filterCombo:obj.filterCombo!=undefined?obj.filterCombo.value:undefined,
            ss:(obj.quickSearchTF)?obj.quickSearchTF.getValue():""
            }
        });
   }
}

Wtf.formatDate = function(fdate,format){
    var dateformats = ['D M d Y H:i:s'];
    if(fdate!=""){
        switch(format){
            case 0:
                return fdate.format(dateformats[format]);
                break;
        }
    }
}

Wtf.customReportTopToolbar = function(obj,reportName,reportno)
{
    obj.filterStoreReader =  new Wtf.data.Record.create([{
        name: 'dataindex',
        type: 'string'
    },{
        name: 'displayname',
        type: 'string'
    }]);
    obj.filterStore = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({            
        }, obj.filterStoreReader),
        autoLoad:false
    });

    obj.selColumn = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.common.selcoltoptoolbartxt"));//'Select Column');
    obj.filterCombo=new Wtf.form.ComboBox({
        selectOnFocus:true,
        triggerAction: 'all',
        mode: 'local',
        store: obj.filterStore,
        useDefault:true,
        displayField: 'displayname',
        typeAhead: true,
        valueField:'dataindex',
        anchor:'100%',
        width:140,
        emptyText:"--"+WtfGlobal.getLocaleText("crm.COMBOEMPTYTXT.all")+"--" //-- All --"
    });
    
    obj.fil = new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.FILTERBUTTON"),//"Filter",
        id:'filter'+reportno, //In use,do not delete.
        scope:this,
        tooltip: {text:WtfGlobal.getLocaleText("crm.FILTERBUTTON.ttip")},// "Choose a date range using 'From' and 'To' fields to filter records created in the specified time duration."},
        iconCls:'pwnd addfilter',
        handler:function(){
            obj.filter()
        }
    });

//    obj.detailsBttn = new Wtf.Toolbar.Button({
//        text:"View Detail",
//        id:'detail'+reportno, //In use,do not delete.
//        scope:this,
////        disable:true,
//        hidden: obj.details.groupflag?!obj.details.groupflag:true,
//        tooltip: {text: "Select the record from the list to view its details."},
//        iconCls:'pwnd addfilter',
//        handler:function(){
//            obj.showDetailView()
//        }
//    });

    obj.reset = new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.RESETBUTTON"),//"Reset",
        scope:this,
        iconCls:'pwndCRM reset',
        tooltip: {text: WtfGlobal.getLocaleText("crm.editor.toptoolbar.resetBTN.ttip")},//'Click to remove any filter settings and view all records.'},
        handler:function(){
            obj.rst()
        }
    });
    obj.todate = new Wtf.form.DateField({
        emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),//" -- Select Date --",
        width: 130,
        offset:Wtf.pref.tzoffset,
        readOnly:true,
//        value: helpid == 57 || helpid == 56 ? new Date() : undefined,
        scope:this
    });

    obj.fromdate = new Wtf.form.DateField({
        emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),//" -- Select Date --",
        width: 130,
        offset:Wtf.pref.tzoffset,
        readOnly:true,
//        value: helpid == 57 || helpid == 56 ? firstDate : undefined,
        scope:this
    });
    obj.from = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.fromdate.label"));//'From');
    obj.to = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.todate.label"));//'To');
    obj.chartBut = new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.common.chartbtn"),//"Chart",
        id:'chart'+reportno,//In use,do not delete.
        scope:this,
        tooltip:WtfGlobal.getLocaleText({key:"crm.common.chartbtn.ttip",params:[reportName]}),//'Get the graphical view of '+reportName+'.',
        iconCls:getTabIconCls(Wtf.etype.piechart),
        handler:function(){
            obj.chart(obj);
        }
    })
    obj.quickSearchTF = new Wtf.KWLTagSearch({//Hide quick search for grouping store.
        id: 'quick'+ reportno,
        width: 200,
        field: obj.details.searchfield,
        fromDate:obj.fromdate,
        toDate:obj.todate,
        isCustomReport:true,
        hidden: obj.details.groupflag?obj.details.groupflag:false,
        filterCombo:obj.filterCombo,
        emptyText:obj.details.searchbyemptytext
    });   
    
    obj.help= new Wtf.Toolbar.Button({
        scope:this,
        iconCls:'helpButton',
        tooltip:{text:WtfGlobal.getLocaleText("crm.common.help.ttip")},//'Get report help by clicking here!'},
        handler:function(){
            obj.showReportHelp(obj);
        }
    });
    
    function onSelectFilterCombo(obj){
         obj.filterCombo.on("select",function(){
           var rec=[];
           var frm = obj.fromdate.getValue();//verify first
           var to = obj.todate.getValue();
           if(frm!=""||to!=""){
                if(checkDates(obj.fromdate,obj.todate)) {
                    rec.frm =(frm).getTime();
                    rec.to =(to).getTime();
                    rec.cd=1;
                }
            }
            rec.filterCombo=obj.filterCombo.getValue(),
            rec.start=0,
            rec.limit=obj.grid.getBottomToolbar().pageSize
            obj.ds.reload({
                params:rec
            });
        },obj);
    }
    var toolbarItem = [obj.quickSearchTF,/*'-',obj.chartBut,obj.detailsBttn,*/'->',obj.selColumn,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',obj.help];
    return toolbarItem;
}
Wtf.topToolBar = function(obj,reportName,helpid)
{
    if(helpid == 10){
        obj.industry = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.lead.defaultheader.industry"));//'Industry');
        obj.filterCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: Wtf.industryStore,
            useDefault:true,
            displayField: 'name',
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140,
            emptyText:"--"+WtfGlobal.getLocaleText("crm.COMBOEMPTYTXT.all")+"--" //" -- All --"
        });
        Wtf.industryStore.load();
        onSelectFilterCombo(obj);
    }
    if(helpid == 25){
        obj.module = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.common.toptoolbar.selmodbtn"));//'Select Module');
        var modStore = new Wtf.data.SimpleStore({
        fields:['id','name'],
        data: [["All","All"],
               ["Campaign","Campaign"],
               ["Lead","Lead"],
               ["Account","Account"],
               ["Contact","Contact"],
               ["Opportunity","Opportunity"],
               ["Case","Case"]
            ],
         autoLoad: true
        });
        obj.filterCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: modStore,
            useDefault:true,
            displayField: 'name',
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140,
            value:obj.details.baseParams.filterCombo!=undefined?obj.details.baseParams.filterCombo:"All"
        });

        obj.filterCombo.on("select",function(){
        obj.ds.reload({
            params:{
                filterCombo:obj.filterCombo.getValue(),
                start:0,
                limit:obj.grid.getBottomToolbar().pageSize
            }
        });
        },obj);
    }
    if(helpid == 17 || helpid == 12){
        obj.source = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.common.sourcebtn"));//'Source');
        obj.filterCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: Wtf.lsourceStore,
            useDefault:true,
            displayField: 'name',
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140,
            emptyText:"--"+WtfGlobal.getLocaleText("crm.COMBOEMPTYTXT.all")+"--" //" -- All --"
        });
        Wtf.lsourceStore.load();
        onSelectFilterCombo(obj);
    }

    if(helpid == 13){
        obj.stage = new Wtf.Toolbar.TextItem('Stage');

        createOppStageStore();

        obj.filterCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: Wtf.oppstageStore,
            useDefault:true,
            displayField: 'name',
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140,
            emptyText:"--"+WtfGlobal.getLocaleText("crm.COMBOEMPTYTXT.all")+"--"//" -- All --"
        });
        Wtf.oppstageStore.load();

        onSelectFilterCombo(obj);

    }

    if(helpid == 14){
        obj.casestatus = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.report.casereport.casestatus"));//'Case Status');

        obj.filterCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: Wtf.caseStatusStore,
            useDefault:true,
            displayField: 'name',
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140,
            emptyText:"--"+WtfGlobal.getLocaleText("crm.COMBOEMPTYTXT.all")+"--"//" -- All --"
        });
        Wtf.caseStatusStore.load();

        onSelectFilterCombo(obj);

    }

    if(helpid == 19){
        obj.opptype = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.lead.defaultheader.type"));//'Type');

        createOppStageStore();

        obj.filterCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: Wtf.opptypeStore,
            useDefault:true,
            displayField: 'name',
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140,
            emptyText:"--"+WtfGlobal.getLocaleText("crm.COMBOEMPTYTXT.all")+"--"
        });
        Wtf.opptypeStore.load();

        onSelectFilterCombo(obj);

    }

    if(helpid == 37){
        obj.campaigntype = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.lead.defaultheader.type"));//'Type');

        obj.filterCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: Wtf.viewStoreType,
            useDefault:true,
            displayField: 'name',
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140,
            emptyText:"--"+WtfGlobal.getLocaleText("crm.COMBOEMPTYTXT.all")+"--"
        });
        Wtf.viewStoreType.load();

        onSelectFilterCombo(obj);

    }
    if(helpid == 55){
        obj.usercomboname= new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.common.usercombo"));//'User');
        obj.userCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: Wtf.ownerStore,
            useDefault:true,
            displayField: 'name',
            editable: false,
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140

        });
        Wtf.ownerStore.load();
        Wtf.ownerStore.on('load',function(){
            obj.userCombo.setValue(loginid)
        },obj);

        obj.userCombo.on("select",function(){
            obj.ds.reload({
                params:{
                    userCombo:obj.userCombo.getValue(),
                    start:0,
                    limit:obj.grid.getBottomToolbar().pageSize
                }
            });
        },obj);

    }

    if(helpid != 58) {
        obj.todate = new Wtf.form.DateField({
            emptyText:Wtf.emptyTextForDateField,
            width: 130,
            offset:Wtf.pref.tzoffset,
            readOnly:true,
            value: helpid == 57 || helpid == 56 ? new Date() : undefined,
            scope:this
        });

        obj.fromdate = new Wtf.form.DateField({
            emptyText:Wtf.emptyTextForDateField,
            width: 130,
            offset:Wtf.pref.tzoffset,
            readOnly:true,
            value: helpid == 57 || helpid == 56 ? firstDate : undefined,
            scope:this
        });
    }
    if(helpid == 60){
        obj.user= new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.common.usercombo"));//'User');
        obj.userCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: Wtf.ownerStore,
            useDefault:true,
            displayField: 'name',
            editable: false,
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140

        });
        Wtf.ownerStore.load();
        Wtf.ownerStore.on('load',function(){
            obj.userCombo.setValue(loginid)
        },obj);
        obj.goaltype = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.goals.header.goaltype"));//'Goal Type');

        var permData = [];
        var arr = ["0","All"];
        permData.push(arr);
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.view)){
            arr = ["1",Wtf.goaltype.nooflead];
            permData.push(arr);
            arr = ["2",Wtf.goaltype.leadrevenue];
            permData.push(arr);
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.view)) {
            arr = ["3",Wtf.goaltype.noofaccount];
            permData.push(arr);
            arr = ["4",Wtf.goaltype.accountrevenue];
            permData.push(arr);
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.view)) {
            arr = ["5",Wtf.goaltype.noofopportunity];
            permData.push(arr);
            arr = ["6",Wtf.goaltype.opprevenue];
            permData.push(arr);
        }
        var goalStore = new Wtf.data.SimpleStore({
            fields:['id','name'],
            data: permData,
            autoLoad: true
        });
        obj.filterCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: goalStore,
            useDefault:true,
            editable: false,
            displayField: 'name',
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140,
            value:"0"
        });


        obj.filterCombo.on("select",function(){
           var rec=[];
           var frm = obj.fromdate.getValue();//verify first
           var to = obj.todate.getValue();
           if(frm!=""||to!=""){
                if(checkDates(obj.fromdate,obj.todate)) {
                    rec.frm = (frm).getTime();
                    rec.to = (to).getTime();
                }
            }
            rec.filterCombo=obj.filterCombo.getValue(),
            rec.userCombo=obj.userCombo.getValue(),
            rec.start=0,
            rec.limit=obj.grid.getBottomToolbar().pageSize
            obj.ds.reload({
                params:rec
            });
        },obj);

        obj.userCombo.on("select",function(){
            obj.ds.reload({
                params:{
                    userCombo:obj.userCombo.getValue(),
                    filterCombo:obj.filterCombo.getValue(),
                    start:0,
                    limit:obj.grid.getBottomToolbar().pageSize
                }
            });
        },obj);
    }
    if( helpid == 28 || helpid == 27 || helpid == 61){
        obj.priority = helpid == 61?new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.case.defaultheader.priority")):new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.report.casereport.casepriority"));//'Case Priority');
        obj.filterCombo=new Wtf.form.ComboBox({
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: Wtf.cpriorityStore,
            useDefault:true,
            displayField: 'name',
            typeAhead: true,
            valueField:'id',
            anchor:'100%',
            width:140,
            emptyText:"--"+WtfGlobal.getLocaleText("crm.COMBOEMPTYTXT.all")+"--"
        });
        Wtf.cpriorityStore.load();

        onSelectFilterCombo(obj);
    }
    if(helpid == 57 || helpid == 56) {
        var firstDate = new Date().format(new Date().format('Y')+'-' +new Date().format('m')+'-01');
    }

    obj.fil = new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.FILTERBUTTON"),//"Filter",
        id:'filter'+helpid, //In use,do not delete.
        scope:this,
        tooltip: {text:WtfGlobal.getLocaleText("crm.FILTERBUTTON.ttip")},// "Choose a date range using 'From' and 'To' fields to filter records created in the specified time duration."},
        iconCls:'pwnd addfilter',
        handler:function(){
            obj.filter()
        }
    });

    obj.reset = new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.RESETBUTTON"),//"Reset",
        scope:this,
        iconCls:'pwndCRM reset',
        tooltip: {text: WtfGlobal.getLocaleText("crm.editor.toptoolbar.resetBTN.ttip")},//'Click to remove any filter settings and view all records.'},
        handler:function(){
            if(helpid == 60){ // Completed goal report
                obj.filterCombo.setValue("0");
                obj.userCombo.setValue(loginid)
            }
            obj.monthCombo.setValue("");
            obj.yearCombo.setValue(new Date().getFullYear());
            obj.rst()
        }

    });

    obj.month = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.calendar.monthBTN"));
    var monthStore = new Wtf.data.SimpleStore({
        fields:['id','name'],
        data: [["1","January"],
            ["2","February"],
            ["3","March"],
            ["4","April"],
            ["5","May"],
            ["6","June"],
            ["7","July"],
            ["8","August"],
            ["9","September"],
            ["10","October"],
            ["11","November"],
            ["12","December"]],
         autoLoad: true
    });
    obj.monthCombo=new Wtf.form.ComboBox({
        id:obj.id+'monthCombo',
        selectOnFocus:true,
        triggerAction: 'all',
        mode: 'local',
        editable:false,
        store: monthStore,
        displayField: 'name',
        typeAhead: true,
        allowBlank:false,
        valueField:'id',
        anchor:'100%',
        emptyText:WtfGlobal.getLocaleText("crm.monthcombo.emtytxt"),//'--Select month--',
        width:120
    });

   obj.monthCombo.on("select",function(){
        obj.ds.reload({
            params:{
                month:obj.monthCombo.getValue(),
                year:obj.yearCombo.value,
                start:0,
                limit:obj.grid.getBottomToolbar().pageSize
            }
        });
    },obj);

    obj.year = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.common.year"));//'Year');
    var yearStore = new Wtf.data.SimpleStore({
        fields:['id','name'],
        data: [["2005","2005"],
            ["2006","2006"],
            ["2007","2007"],
            ["2008","2008"],
            ["2009","2009"],
            ["2010","2010"],
            ["2011","2011"],
            ["2012","2012"],
            ["2013","2013"],
            ["2014","2014"],
            ["2015","2015"]],
         autoLoad: true
    });
    obj.yearCombo=new Wtf.form.ComboBox({
        id:obj.id+'yearCombo',
        selectOnFocus:true,
        triggerAction: 'all',
        mode: 'local',
        store: yearStore,
        displayField: 'name',
        typeAhead: true,
        allowBlank:false,
        valueField:'id',
        anchor:'100%',
        value:new Date().getFullYear(),
        width:60
    });
    obj.yearCombo.on("select",function(){
        obj.ds.reload({
            params:{
                month:obj.monthCombo.getValue(),
                year:obj.yearCombo.value,
                start:0,
                limit:obj.grid.getBottomToolbar().pageSize
            }
        });
    },obj);
    obj.from = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.fromdate.label"));//'From');
    obj.to = new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.todate.label"));//'To');
    obj.chartBut = new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.common.chartbtn"),//"Chart",
        id:'chart'+helpid,//In use,do not delete.
        scope:this,
        tooltip:WtfGlobal.getLocaleText({key:"crm.common.chartbtn.ttip",params:[reportName]}),//'Get the graphical view of '+reportName+'.',
        iconCls:getTabIconCls(Wtf.etype.piechart),
        handler:function(){
            obj.chart(obj);
        }
    })
    var exp = exportButton(obj,reportName,helpid);
    var printR = printButtonR(obj,reportName,helpid);
    if(helpid == 58) {
        obj.quickSearchTF_2 = new Wtf.KWLTagSearch({
            id: 'quick'+58,
            width: 200,
            field:obj.details.searchfield,
            fromDate:obj.fromdate,
            toDate:obj.todate,
            year:obj.yearCombo,
            month:obj.monthCombo,
            emptyText:obj.details.searchbyemptytext
        });
    } else {
        obj.quickSearchTF = new Wtf.KWLTagSearch({
            id: 'quick'+ helpid,
            width: 200,
            field: obj.details.searchfield,
            fromDate:obj.fromdate,
            toDate:obj.todate,
            year:obj.yearCombo,
            month:obj.monthCombo,
            emptyText:obj.details.searchbyemptytext
        });
    }
    var help=getHelpButton(obj,helpid);
    var editMaster = new Wtf.Toolbar.Button({
        scope:this,
        text:WtfGlobal.getLocaleText("crm.common.addeditmasterconfbtn"),//'Add/Edit Master Configuration',
        iconCls:'pwnd editmasterIcon',
        tooltip:{
            text:WtfGlobal.getLocaleText("crm.common.addeditmasterconfbtn.ttip")//'Add/Edit weightage in pipeline.'
        },
        mode:helpid,
        handler:function(a){
            editMasterConfig(a.mode);
        }
    });

    function onSelectFilterCombo(obj){
         obj.filterCombo.on("select",function(){
           var rec=[];
           var frm = obj.fromdate.getValue();//verify first
           var to = obj.todate.getValue();
           if(frm!=""||to!=""){
                if(checkDates(obj.fromdate,obj.todate)) {
                    rec.frm =(frm).getTime();
                    rec.to =(to).getTime();
                    rec.cd=1;
                }
            }
            rec.filterCombo=obj.filterCombo.getValue(),
            rec.start=0,
            rec.limit=obj.grid.getBottomToolbar().pageSize
            obj.ds.reload({
                params:rec
            });
        },obj);
    }
    
    function editMasterConfig(mode) {
        var panel=Wtf.getCmp('masterConfigTab');
        if(panel==null)
        {
            panel = new Wtf.MasterConfigurator({
                layout:"fit",
                title:WtfGlobal.getLocaleText("crm.dashboard.masterconfig"),//"Master Configuration",
                closable:true,
                border:false,
                iconCls: 'pwnd projectTabIcon',
                id:"masterConfigTab",
                mode:mode
            });
            mainPanel.add(panel);
        } else {
            if(mode == 56 || mode == 58) {
                panel.masterGrid.getSelectionModel().selectRow(15,0);
            } else if (mode == 57) {
                panel.masterGrid.getSelectionModel().selectRow(12,0);
            }
            panel.masterDataGrid.getSelectionModel().selectFirstRow();
        }
        mainPanel.setActiveTab(panel);
        mainPanel.doLayout();

        panel.masterStore.on("load",function(){
            if(mode == 56 || mode == 58) {
                panel.masterGrid.getSelectionModel().selectRow(15,0);
            } else if (mode == 57) {
                panel.masterGrid.getSelectionModel().selectRow(12,0);
            }
        });
        panel.masterDataStore.on("load",function(){
            panel.masterDataGrid.getSelectionModel().selectFirstRow();
        });
    }
    if(helpid != 22 && helpid != 29 && helpid != 56 && helpid != 57 && helpid != 58 && helpid != 10 && helpid != 17 && helpid != 12 && helpid != 13 && helpid != 14 && helpid != 19 && helpid != 37 && helpid != 55 && helpid != 60 && helpid != 26 && helpid != 61 && helpid != 27 && helpid != 28 && helpid != 25) {
        var toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
    } else {
        if(helpid == 17 || helpid == 12) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.source,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
        }else if(helpid == 13) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.stage,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
        }else if(helpid == 14) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.casestatus,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
        }else if(helpid == 19) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.opptype,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
        }else if(helpid == 37) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.campaigntype,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
        }else if(helpid == 55) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.usercomboname,obj.userCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
        }else if(helpid == 60) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.user,obj.userCombo,'-',obj.goaltype,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
        }else if(helpid == 61) {
            toolbarItem = [obj.quickSearchTF,'->',obj.priority,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset];
        } else if(helpid == 10) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.industry,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
        } else if(helpid == 25) {
            if(obj.details.relatedto!=undefined)
                toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
            else
                toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.module,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
        }else if( helpid == 27 || helpid == 28) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.priority,obj.filterCombo,'-',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',help];
        } else if(helpid == 56 && Wtf.URole.roleid == Wtf.AdminId) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',editMaster];
        } else if(helpid == 57 && Wtf.URole.roleid == Wtf.AdminId) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset,'-',editMaster];
        } else if(helpid == 56 || helpid == 57 ) {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset];
        } else if(helpid == 58 && Wtf.URole.roleid == Wtf.AdminId) {
            toolbarItem = [obj.quickSearchTF_2,'->',editMaster];
        } else if(helpid == 58) {
            toolbarItem = [obj.quickSearchTF_2];
        } else if(helpid == 26) {
            toolbarItem = [obj.quickSearchTF,'->',obj.from,obj.fromdate,'-',obj.to,obj.todate,'-',obj.fil,'-',obj.reset];
        } else {
            toolbarItem = [obj.quickSearchTF,'-',obj.chartBut,'->',obj.month,obj.monthCombo,'-',obj.year,obj.yearCombo,'-',obj.reset,'-',help];
        }
    }
    return toolbarItem;
}

 Wtf.commonComboBox = function(obj,store,id,allowblank)
{
    obj.Combo=new Wtf.form.ComboBox({
        fieldLabel:WtfGlobal.getLocaleText("crm.common.campaignownercombo")+'*',// 'Campaign Owner*',
        id:obj.id+id,
        selectOnFocus:true,
        triggerAction: 'all',
        mode: 'local',
        store: store,
        displayField: 'name',
        typeAhead: true,
        allowBlank:allowblank,
        valueField:'id',
        anchor:'100%',
        width:295
    });

    return obj.Combo;
}
/*
*
* Sagar A - Not in used
*/
/*Wtf.commonWindowComboBox = function(fieldLabel,obj,store,id,allowblank,pluginModule,width)
{
    obj.Combo=new Wtf.form.ComboBox({
        fieldLabel: fieldLabel,
        id:obj.id+id,
        selectOnFocus:true,
        triggerAction: 'all',
        mode: 'local',
        store: store,
        displayField: 'name',
        emptyText:"-- Please Select --",
        typeAhead: true,
        allowBlank:allowblank,
        forceSelection:true,
        valueField:'id',
        msgTarget:'side',
        anchor:'100%',
        style:width!=undefined?"width:"+width+";":undefined,
        width:200,
        listWidth:185,
        plugins:(pluginModule!=undefined?[new Wtf.common.comboAddNew({
                handler: function(){
                    if(pluginModule=="product") {
                        obj.popupproduct();
                    } else if(pluginModule=="contact") {
                        obj.addcon();
                    }
                },
                scope: this
                })]:"")
    });

    return obj.Combo;
}*/

Wtf.addWidgetComboBox = function(fieldLabel,obj,store,id,mode)
{
    mode=(mode == undefined ? 'remote' : mode );
    obj.Combo=new Wtf.form.ComboBox({
        fieldLabel: fieldLabel,
        id:obj.id+id,
        selectOnFocus:true,
        triggerAction: 'all',
        mode: mode,
        store: store,
        displayField: 'name',
        emptyText:WtfGlobal.getLocaleText("crm.goalsettings.combo.emptytxt"),//"-- Please Select --",
        typeAhead: true,
        allowBlank:false,
        valueField:'id',
        msgTarget:'side',
        anchor:'100%',
        style:"width:90%",
        width:200
    });

    return obj.Combo;
}

Wtf.ArchivedGlobal = function(data,module,audit,auditMod) {
        var un=data.a;
        if(un=="Unarchive") {
           data.tbarArchive.setText(WtfGlobal.getLocaleText("crm.common.restorebtn"));//"Restore");
        }
        var title = data.title;
        var treeid = data.treeid;
        var valueid = data.valueid;
        var EditorGrid = data.EditorGrid;
        var EditorStore = data.EditorGrid.getStore();
        var s=EditorGrid.getSelectionModel().getSelections();
        var duration = 60;
        EditorGrid.getSelectionModel().clearSelections();
        if(s.length>0){
            var tob=Wtf.getCmp('tree');
            var moduleNode=tob.getNodeById(treeid);
            Wtf.onlyhighLightRecordLoop(EditorGrid, "FF0000", duration, s, title);
            Wtf.highLightTreeNodeLoop("FF0000", duration, s, tob, moduleNode, valueid);
            Wtf.MessageBox.show({
                title:data.title,
                msg: ' '+data.tbarArchive.getText()+' selected '+(data.plural||data.title+'(s)')+'?',
                buttons: Wtf.MessageBox.YESNO,
                animEl: 'upbtn',
                scope:this,
                fn:function(btn){
                    if(btn=="yes") {
                        var jsondata="";
                        for(var i=0;i<s.length;i++) {
                            if(s[i].get("validflag") != -1 ) {
                               jsondata+="{'"+data.keyid+"':'" + s[i].get(""+data.valueid+"") + "','"+data.name+"': \"" + encodeURI(s[i].get(""+data.valueName+"")) + "\",'"+data.ownerName+"' : \""+s[0].get(data.ownerName)+"\"},";
                             }
                        }
                        var trmLen = jsondata.length - 1;
                        var finalStr = jsondata.substr(0,trmLen);
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.springBase+"common/Archive/archiveGlobal.do",
//                            url: "Common/Archive/archiveGlobal.do",
                            params:{
                                jsondata:finalStr,
                                module: module,
                                table:data.table,
                                flag:42,
                                text:un,
                                auditNo:audit,
                                auditMod:auditMod
                            }
                        },
                        this,
                        function(res) {
                            if(data.tbarArchive.getText()=="Restore") {
                                var archiveGrid=Wtf.getCmp(data.GridId+data.archivedPanId);
                                archiveGrid.getStore().load({
                                    params:{
                                        start:0,
                                        limit:archiveGrid.getBottomToolbar().pageSize,
                                        isarchive:true,
                                        isconverted:0
                                    }
                                });
                                var homeGrid = Wtf.getCmp(data.GridId+data.homePanId);
                                homeGrid.getStore().reload();
                            }
                            if(data.title=="Product"){
                                Wtf.productStore.reload();
                            }
//                            if(data.title=="Account"){
//                                Wtf.parentaccountstore.reload();
//                            }
                            if(data.tbarArchive.getText()=="Archive") {
                                this.delayFunction=new Wtf.util.DelayedTask(function(){
                                    Wtf.removeSelectedTreeNode(tob, moduleNode, valueid, s);
                                    Wtf.removeSelectedRows(EditorGrid, s);
                                    EditorStore.load({params:{start:0, limit: EditorGrid.getBottomToolbar().pageSize}});
                                },this);
                                this.delayFunction.delay(1800);
                            }
                             Wtf.onlyhighLightRecordLoop(EditorGrid, "ffffff", duration, s, title, true);
                             Wtf.highLightTreeNodeLoop("ffffff", duration, s, tob, moduleNode, valueid);

                             Wtf.onlyhighLightRecordLoop(EditorGrid, "FF0000", 3, s, title);
                             Wtf.highLightTreeNodeLoop("FF0000", 3, s, tob, moduleNode, valueid);

                            bHasChanged = true;
                            Wtf.refreshDashboardWidget(auditMod);
                            ResponseAlert([data.title, (data.plural||data.title+'(s)')+' '+data.tbarArchive.getText()+'d successfully.']);

                        },
                        function() {
                            ResponseAlert([(data.plural||data.title+'(s)')+' Failed to '+data.tbarArchive.getText()+'.']);
                        }
                        )

                    } else{
                        var rows =[];
                        Wtf.highLightTreeNodeLoop("ffffff", duration+1, s, tob, moduleNode, valueid);
                        Wtf.onlyhighLightRecordLoop(EditorGrid, "ffffff", duration+1, s, title, true);
                        for(var i=0; i<s.length; i++) {
                            var ri=EditorStore.indexOf(s[i]);
                            rows.push(ri);
                        }
                        EditorGrid.getSelectionModel().selectRows(rows);
                    }
                },
                icon: Wtf.MessageBox.QUESTION
            },this);
        }
        else {
            ResponseAlert([data.title,'Please select a '+(data.plural||data.title+'(s)')+' to '+data.tbarArchive.getText()+'.']);
        }
}

Wtf.highLightGlobal = function(highLightId,EditorGrid,EditorStore,primaryKeyId) {
        if(highLightId!=undefined) {
             this.row=Wtf.highLightSearch(highLightId,EditorStore,primaryKeyId)
             if(this.row!=null) {
                if(primaryKeyId!="activityid") {
                    Wtf.highLightRow(EditorGrid,"FFFF00",2, this.row);
                }else {
                     Wtf.highLightText(EditorGrid,"FFFF00",2, this.row);
                }
            }
        }
}

Wtf.highLightSearch= function(highLightId,EditorStore,primaryKeyId) {
         var index =  EditorStore.findBy(function(record) {
            if(record.get(primaryKeyId)==highLightId)
                return true;
            else
                return false;
         });
        if(index == -1)
            return null;
        return index;
}
Wtf.highLightRow = function(EditorGrid,color,duration,row) {
        var rowEl = EditorGrid.getView().getRow(row);
        if(rowEl!=undefined){
            var el=EditorGrid.getView().scroller;
            var a=Wtf.fly(rowEl).getOffsetsTo(el);
            el.scrollTo("top",a[1],true);
            Wtf.fly(rowEl).highlight(color
                ,{   attr: "background-color",
                     duration: duration,
                     endColor: "ffffff",
                     easing: 'easeIn',
                     stopFx:true,
                     concurrent:true
            });
        }
}
Wtf.highLightText = function(EditorGrid,color,duration,row) {
        var rowEl = EditorGrid.getView().getRow(row);
        if(rowEl!=undefined){
            var el=EditorGrid.getView().scroller;
            var a=Wtf.fly(rowEl).getOffsetsTo(el);
            el.scrollTo("top",a[1],true);
            Wtf.fly(rowEl).fadeIn({
                endOpacity: 1,
                easing: 'easeOut',
                     duration: duration
            });
        }
}
Wtf.onlyhighLightRow = function(EditorGrid,color,duration,row) {
        var rowEl = EditorGrid.getView().getRow(row);
        if(rowEl!=undefined){
            Wtf.fly(rowEl).highlight(
            color,{
                attr: "background-color",
                duration: duration,
                endColor: "ffffff",
                easing: 'easeIn',
                stopFx:true,
                concurrent:true
            })
         }
}
Wtf.onlyFadeIn = function(EditorGrid,color,duration,row) {
        var rowEl = EditorGrid.getView().getRow(row);
        if(rowEl!=undefined){
            Wtf.fly(rowEl).fadeIn({
                endOpacity: 1,
                easing: 'easeOut',
                stopFx:true,
                duration: 2
            });
        }
}
Wtf.onlyFadeOut = function(EditorGrid,color,duration,row) {
        var rowEl = EditorGrid.getView().getRow(row);
        if(rowEl!=undefined){
            Wtf.fly(rowEl).fadeOut({
                endOpacity: .25,
                easing: 'easeOut',
                stopFx:true,
                duration: duration
            });
        }
}
Wtf.highLightTreeNode = function(node,color,duration) {
        var nodeEl = node.ui.elNode;
        Wtf.fly(nodeEl).highlight(
        color,{
            attr: "background-color",
            duration: duration,
            endColor: "ffffff",
            easing: 'easeIn',
            stopFx:true,
            concurrent:true
        });
        }
Wtf.highLightTreeNodeLoop = function(color, duration, s, tob, moduleNode, valueid, remove) {
    if(moduleNode != undefined){
        for(var i=0; i<s.length; i++) {
            var node=tob.getNodeById(s[i].get(valueid));
            var nodeIndex = moduleNode.indexOf(node);
            if(nodeIndex != -1) {
                Wtf.highLightTreeNode(node,color,duration);
                if(!moduleNode.isExpanded()){
                    moduleNode.expand();
                }
            }
        }
    }
}
Wtf.onlyhighLightRecordLoop = function(EditorGrid, color, duration, s, title, inn) {
    for(var i=0; i<s.length; i++) {
        var ri=EditorGrid.getStore().indexOf(s[i]);
        if(title!="Activity") {
            Wtf.onlyhighLightRow(EditorGrid,color,duration, ri)
        } else {
            if(inn == true){
                Wtf.onlyFadeIn(EditorGrid,color,duration, ri);
            }
            else{
                Wtf.onlyFadeOut(EditorGrid,color,duration, ri);
            }
        }
    }
}
Wtf.removeSelectedRows = function(EditorGrid, selections){
    for(var i=0; i<selections.length; i++) {
       if(selections[i].get("validflag") != -1 ) {
           Wtf.arrangeGridNumbererRemove(EditorGrid.getStore().indexOf(selections[i]), EditorGrid)
           EditorGrid.getStore().remove(selections[i]);
       }
    }
}
Wtf.removeSelectedTreeNode = function(tob, moduleNode, valueid, selections){
    if(moduleNode != undefined){
        for(var i=0; i<selections.length; i++) {
            var node=tob.getNodeById(selections[i].get(valueid));
            var nodeIndex = moduleNode.indexOf(node);
            if(nodeIndex != -1) {
                if(!moduleNode.isExpanded())
                    moduleNode.expand();
                moduleNode.removeChild(node);
            }
        }
    }
    }
Wtf.arrangeGridNumbererAdd = function(currentRow,grid) {
    var view = grid.getView();
    var length = grid.store.getCount();
    for (var i = currentRow; i < length; i++)
    {   if(i==0 && grid.isEditor)
    	view.getCell(i, 0).firstChild.innerHTML = "N";
    else
        view.getCell(i, 0).firstChild.innerHTML = i;
    }
}
Wtf.arrangeGridNumbererRemove = function(currentRow,grid) {
    if(currentRow<0)return;
    var view = grid.getView();
    var length = grid.store.getCount();
    for (var i = currentRow; i < length; i++)
        view.getCell(i, 0).firstChild.innerHTML = i;
}

Wtf.archivedMenuButtonA = function(menuArray,obj,modName) {
    var tbarArchive=new Wtf.Toolbar.Button({
        iconCls: getTabIconCls(Wtf.etype.archived),
        tooltip: {text: WtfGlobal.getLocaleText({key:"crm.editor.toptoolbar.archiveBTN.mainttip",params:[modName]})},//"Send inactive "+modName+" to Archive repository."},
        scope: obj,
        text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.archiveBTN"),//"Archive",
        menu: menuArray
    });
    return tbarArchive;
}

Wtf.archivedMenuButtonB = function(obj,module) {
    var tbarUnArchive=new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.common.restorebtn"),//'Restore',
        scope:obj,
        tooltip:{text:WtfGlobal.getLocaleText({key:"crm.editor.restorebtnmainttip",params:[module]})},//'Send an archived '+module+' to the active '+module+' list.'},
        iconCls:getTabIconCls(Wtf.etype.archived),
        handler:function() {
            obj.ArchiveHandler("Unarchive");
        }
    });
    return tbarUnArchive;
}

Wtf.archivedMenuArray = function(obj,moduleName) {
    var archArray = [];
    var addToArchive = new Wtf.Action({
        text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.archive.addtoarchive"),// "Add to Archive",
        id:'addAchrive'+obj.id,
        tooltip:{text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.archive.addtoarchive.ttip")},// 'Select row(s) to send in Archive repository.'},
        iconCls: getTabIconCls(Wtf.etype.archivedMenuIcon),
        scope: obj,
        handler:function(){
            obj.ArchiveHandler("Archive");
        }
    });
    archArray.push(addToArchive);

    var viewArchive=new Wtf.Action({
        text: WtfGlobal.getLocaleText({key:"crm.editor.toptoolbar.archive.viewarchived",params:[moduleName]}),// "View Archived "+moduleName+"s",
        tooltip:{text:WtfGlobal.getLocaleText({key:"crm.editor.toptoolbar.archive.viewarchived.ttip",params:[moduleName]})},//'Show list of archived '+moduleName+'s.'},
        iconCls: getTabIconCls(Wtf.etype.archivedMenuIcon),
        scope: obj,
        handler:function(){
            obj.showArchived();
        }
    });
    archArray.push(viewArchive);
    return archArray;
}
Wtf.TargetListMenuArray = function(obj) {
    var listArray = [];
    var createlist = new Wtf.Action({
        text:WtfGlobal.getLocaleText("crm.common.addnewlist"),// "Add New List",
        id:'createtargetlist'+obj.id,
        tooltip:{text:WtfGlobal.getLocaleText("crm.common.addnewlist.ttip")},//'Create a new list of Targets for Email Marketing.'},
        iconCls:"targetlistIcon",
        scope: obj,
        handler:function(){
            obj.createTargetlist();
        }
    });
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.view)) {
        listArray.push(createlist);
    }
    var viewlist=new Wtf.Action({
        text: WtfGlobal.getLocaleText("crm.common.managelists"),//"Manage Lists",
        tooltip:{text:WtfGlobal.getLocaleText("crm.common.managelists.ttip")},//'View list of Targets for Email Marketing.'},
        iconCls:"targetlistIcon",
        scope: obj,
        handler:function(){
            obj.showtargetlists();
        }
    });
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.manage)) {
        listArray.push(viewlist);
    }
    obj.tbartargetlistButton=new Wtf.Toolbar.Button({
        iconCls:"targetlistIcon",
        tooltip:{text:WtfGlobal.getLocaleText("crm.common.targetlistbtn.ttip")},//'Compile and Save lists of targets for Campaign Configurations. Use existing Leads, Contacts etc. or upload from convenient file formats.'},
        scope: this,
        text:WtfGlobal.getLocaleText("crm.campaigndetails.toptoolbar.targetlistBTN"),//"Target List",
        menu: listArray
    });
    return obj.tbartargetlistButton;

}

Wtf.commonUploadWindow = function(obj,type)
{
    var delimiterStore = new Wtf.data.SimpleStore({
            fields: ['delimiterid','delimiter'],
            data : [
            [0,"Colon"],
            [1,'Comma'],
            [2,'Semicolon'],
            [3,'Space'],
            [4,'Tab']
            ]
    });
    this.conowner= new Wtf.form.ComboBox({
                fieldLabel:'Delimiter ',
                hiddenName:'Delimiter',
                store:delimiterStore,
                valueField:'delimiter',
                displayField:'delimiter',
                mode: 'local',
                triggerAction: 'all',
                emptyText:'--Select delimiter--',
                typeAhead:true,
                selectOnFocus:true,
                allowBlank:false,
                anchor:'90%',
                value:'Comma'
           });
    var ImportPanel1 = new Wtf.FormPanel({
            width:'80%',
            method :'POST',
            scope: this,
            border:false,
            fileUpload : true,
            waitMsgTarget: true,
            labelWidth: 70,
            bodyStyle: 'background:#f1f1f1;font-size:10px;padding:10px;',
            layout: 'form',
            items:[{
                xtype : 'textfield',
                id:'browseBttn',
                border:false,
                inputType:'file',
                fieldLabel:WtfGlobal.getLocaleText("crm.importlog.header.filename"),//'File name ',
                name: 'test'
            },this.conowner]
        },
        this);
    var impWin1 = new Wtf.Window({
        resizable: false,
        scope: this,
        layout: 'border',
        modal:true,
        width: 380,
        height: 220,
        iconCls: 'pwnd favwinIcon',
        id: 'importwindow',
        title: WtfGlobal.getLocaleText("crm.common.importcsvfile"),//'Import CSV File',
        items: [
                {
                    region:'north',
                    height:70,
                    border : false,
                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                    html: getTopHtml("Import CSV File", "Import CSV File ","../../images/import.png")
                },{
                    region:'center',
                    layout:'fit',
                    border:false,
                    bodyStyle: 'background:#f1f1f1;font-size:10px;',
                    items:[ImportPanel1]
                }
        ],
        buttons: [{
            text: WtfGlobal.getLocaleText("crm.common.nextbtn"),//'Next',
            id: 'submitPicBttn',
            type: 'submit',
            scope: this,
            handler: function(){
                var parsedObject = document.getElementById('browseBttn').value;
                var extension =parsedObject.substr(parsedObject.lastIndexOf(".")+1);
                var patt1 = new RegExp("csv","i");
                var delimiterType=this.conowner.getValue();
                if(delimiterType==undefined || delimiterType==""){
                    ResponseAlert(82);
                    return;
                }
                if(patt1.test(extension)) {
                    ImportPanel1.form.submit({
                        url:Wtf.req.springBase+'common/ImportRecords/importRecords.do?type='+type+'&do=getMapCSV&delimiterType='+delimiterType+'',
                        waitMsg :'importing...',
                        scope:this,
                        success: function (action, res) {
                            impWin1.hide();

                            var resobj = eval( "(" + res.response.responseText.trim() + ")" );
                            if(resobj.data != "") {
                                obj.mappingCSV(resobj.Header,resobj,impWin1,delimiterType);
                            }
                        },
                        failure:function() {
                            impWin1.close();
                        }

                    },obj);
                } else
                    ResponseAlert(83);
            }
        },{
            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
            id:'canbttn1',
            scope:this,
            handler:function() {
                impWin1.close();
            }
        }]
    },this);

    return impWin1;
}

Wtf.commonConflictWindow = function(obj,response,modName,filename,EditorStore,listds,grid,success,failed,imgPath,mapid,relatedName,grid1,skipMsg,delimiterType,randomNum)
{
    Wtf.Ajax.timeout=900000;
    Wtf.commonWaitMsgBox("Importing data... It may take few moments...");
    var sustr1 = modName =='Target'? ('&tlid='+randomNum):"";
    Wtf.Ajax.requestEx({
        url:Wtf.req.springBase+'common/ImportRecords/importRecords.do?type=submit&do=import'+sustr1,
        waitMsg :'importing...',
        method:'POST',
        scope:this,
        params: ({
            filename:filename,
            resjson:response,
            modName : modName,
            mapid:mapid,
            relatedName:relatedName,
            delimiterType:delimiterType
        })
    },
    this,
    function (action,res) {
        Wtf.updateProgress();
        if(action.success){
            if(EditorStore!=undefined)
                EditorStore.reload();
                if(action.data.data != "") {

                    this.replaceOriginal= new Wtf.form.Checkbox({
                        boxLabel:" ",
                        inputType:'radio',
                        name:'rectype',
                        checked:true,
                        inputValue:'false',
                        width: 50,
                        fieldLabel:"Replace these "+modName +"s with the new details. Your original details for these "+modName +"s will be lost by choosing this option."
                    })
                    this.resolveConflict= new Wtf.form.Checkbox({
                        boxLabel:" ",
                        width: 50,
                        inputType:'radio',
                        inputValue:'true',
                        name:'rectype',
                        fieldLabel:"Resolve conflict by mapping these "+modName +"s with your existing "+modName +"s. This helps you to save new "+modName +"s details to any of your existing "+modName +"s."
                    })
                    this.skipButton= new Wtf.form.Checkbox({
                        boxLabel:" ",
                        width: 50,
                        inputType:'radio',
                        inputValue:'true',
                        name:'rectype',
                        fieldLabel:"Do not import these "+modName +"s. This option would retain your original "+modName +"s and only import "+modName +"s without any conflict with the existing "+modName +"s."
                    })
                   this.TypeForm=new Wtf.form.FormPanel({
                        region:'center',
                        autoScroll:true,
                        border:false,
                        labelWidth:430,
                        style: "background: #f1f1f1;padding-left: 35px;padding-top: 20px;padding-right: 30px;",
                        defaultType: 'textfield',
                         items:[this.replaceOriginal,this.resolveConflict,this.skipButton]
                    });
                    var conflictChkParameter="Name and Email";
                    if(modName=="Account"){
                        conflictChkParameter="Account and Website";
                    }
                    var subTitle="Your imported "+modName +"s have entries with similar "+conflictChkParameter+" that already exist in your "+modName +" List.  What do you want to do?";

                    this.OverriteWin = new Wtf.Window({
                        resizable: false,
                        scope: this,
                        layout: 'border',
                        modal:true,
                        draggable:false,
                        width: 400,
                        height: 450,
                        iconCls: 'pwnd favwinIcon',
                        id: 'import_window_overrite',
                        title:WtfGlobal.getLocaleText("crm.common.overwriteconflict"),// 'Overwrite Conflict',
                        items:[{
                            region : 'north',
                            height : 100,
                            border : false,
                            id:'resolveConflictNorth_panel_Overrite',
                            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                            html : ""
                        },
                        grid1],
                        buttons: [{
                            text:WtfGlobal.getLocaleText("crm.common.previousbtn"),// 'Previous',
                            id: 'previous_Overrite',
                            type: 'submit',
                            scope: this,
                            handler: function(){
                                this.OverriteWin.hide();
                                this.resolveWin.show();

                            }
                        },{
                            text: WtfGlobal.getLocaleText("crm.common.overwritebtn"),//"Overwrite",
                            id: 'importBttn_Overrite',
                            type: 'submit',
                            scope: this,
                            handler: function(){

                                     this.conflictWin.show();
                                     this.conflictWin.close();
                                     obj.mapImportedRes(this.OverriteWin,this.repOrig,this.resConf);
                            }
                        },{
                            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                            id:'canbttn_Overrite',
                            scope:this,
                            handler:function() {
                                this.OverriteWin.close();
                                this.resolveWin.close();

                                this.conflictWin.show();
                                this.conflictWin.close();
                             }
                        }]
                    });

                    this.conflictWin = new Wtf.Window({
                        resizable: false,
                        scope: this,
                        layout: 'border',
                        modal:true,
                        draggable:false,
                        width: 400,
                        height: 450,
                        iconCls: 'pwnd favwinIcon',
                        id: 'importConf_window',
                        title: WtfGlobal.getLocaleText("crm.common.resolveconflict"),//'Resolve Conflict',
                        items:[{
                            region : 'north',
                            height : 100,
                            border : false,
                            id:'resolveConflictNorth_panel',
                            bodyStyle :'background:white;border-bottom:1px solid #bfbfbf;',
                            html : ""
                        },
                        grid],
                        buttons: [{
                            text: WtfGlobal.getLocaleText("crm.common.previousbtn"),//'Previous',
                            id: 'previous',
                            type: 'submit',
                            scope: this,
                            handler: function(){
                                this.conflictWin.hide();
                                this.resolveWin.show();


                            }
                        },{
                            text:WtfGlobal.getLocaleText("crm.IMPORTBUTTON"),//'Import',
                            id: 'importBttn',
                            type: 'submit',
                            scope: this,
                            handler: function(){

                                     this.OverriteWin.show();
                                     this.OverriteWin.close();
                                     obj.mapImportedRes(this.conflictWin,this.repOrig,this.resConf);
                            }
                        },{
                            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                            id:'canbttn2',
                            scope:this,
                            handler:function() {
                                this.conflictWin.close();
                                this.resolveWin.close();

                                this.OverriteWin.show();
                                this.OverriteWin.close();
                            }
                        }]
                    });


                    this.resolveWin= new Wtf.Window({
                        height:300,
                        width:600,
                        resizable:false,
                        modal:true,
                        scope:this,
                        title:"Resolve Duplicate "+modName+"s",
                        iconCls: 'pwnd favwinIcon',
                        layout: 'border',
                        items:[ {
                                    region : 'north',
                                    height : 75,
                                    border : false,
                                    bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                                    html : getTopHtml("Resolve Duplicate "+modName+"s",""+subTitle+"",imgPath)
                                },
                                this.TypeForm],
                        buttons: [{
                                text: 'Next',
                                scope: this,
                                id:'next'+this.id,
                                handler: function(){
                                        listds.loadData(action.data.data);
                                        this.resolveWin.hide();
                                        var conflictSubTitle="";
                                        this.repOrig=this.replaceOriginal.getValue();
                                        this.resConf=this.resolveConflict.getValue();

                                        if(this.repOrig==true){
                                            conflictSubTitle="Map "+modName+"s already present in your "+modName+" List with the imported "+modName+"s.Click to Overwrite existing "+modName+"s with the imported "+modName+".";

                                            Wtf.getCmp("resolveConflictNorth_panel_Overrite").html = getTopHtml("Map Duplicate "+modName+"s",""+conflictSubTitle+"",imgPath);

                                            this.OverriteWin.show();

                                        } else if(this.resConf==true){
                                            conflictSubTitle="Map "+modName+"s already present in your "+modName+" List with the imported "+modName+"s.Click to select a "+modName+" from your "+modName+" list for mapping it with the imported "+modName+".";

                                            Wtf.getCmp("resolveConflictNorth_panel").html =getTopHtml("Map Duplicate "+modName+"s",""+conflictSubTitle+"",imgPath)
                                            this.conflictWin.show();
                                        }
                                }
                              },{
                                text:WtfGlobal.getLocaleText("crm.common.skipnfinish"),// 'Skip & Finish',
                                scope: this,
                                id:'skipnfinish'+this.id,
                                disabled:true,
                                handler:function(){
                                    WtfComMsgBox(skipMsg, 0);

                                    this.resolveWin.close();

                                    this.conflictWin.show();
                                    this.conflictWin.close();

                                    this.OverriteWin.show();
                                    this.OverriteWin.close();
                                }
                             },{
                                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                                scope: this,
                                handler:function(){
                                    this.resolveWin.close();

                                    this.conflictWin.show();
                                    this.conflictWin.close();

                                    this.OverriteWin.show();
                                    this.OverriteWin.close();
                                }
                            }]
                    });

                    this.resolveWin.show();

                    this.replaceOriginal.on("check",function(){
                                            this.resolveConflict.checked=false;
                                            this.skipButton.checked=false;
                                            Wtf.getCmp("next"+this.id).enable();
                                            Wtf.getCmp("skipnfinish"+this.id).disable();}
                                        ,this)
                    this.resolveConflict.on("check",function(){
                                            this.replaceOriginal.checked=false;
                                            this.skipButton.checked=false;
                                            Wtf.getCmp("next"+this.id).enable();
                                            Wtf.getCmp("skipnfinish"+this.id).disable();}
                                        ,this)
                    this.skipButton.on("check",function(){
                                            this.replaceOriginal.checked=false;
                                            this.resolveConflict.checked=false;
                                            Wtf.getCmp("next"+this.id).disable();
                                            Wtf.getCmp("skipnfinish"+this.id).enable();}
                                        ,this)


            } else {
                WtfComMsgBox(success, 0);
            }
            if(action.data.importedRecords!=""){
                obj.addEvents({
                    'importrecs':true
                });
                obj.fireEvent('importrecs',action.data.importedRecords.data);
            }
            if(EditorStore!=undefined)
                EditorStore.reload();

        } else {
           WtfComMsgBox(454,1);
        }
        Wtf.Ajax.timeout=30000;
    },
    function ( action,res) {
        Wtf.updateProgress();
        WtfComMsgBox(failed, 1);
        Wtf.Ajax.timeout=30000;
    })
}

Wtf.commonWaitMsgBox = function(msg) {
        Wtf.MessageBox.show({
            msg: msg,
            width:290,
            wait:true,
            title:WtfGlobal.getLocaleText("crm.common.processingloadmsg"),//"Processing your request. Please wait...",
            waitConfig: {interval:200}
        });
}

Wtf.updateProgress =function() {
    Wtf.MessageBox.hide();
}

Wtf.refreshUpdatesAll = function(currentRow,grid) { // Refresh "updates for all"  widget on Dashboard
    var objUpdatesAll=Wtf.getCmp("DSBMyWorkspaces");
    if(objUpdatesAll!=undefined)
        objUpdatesAll.callRequest("","",0);
}
Wtf.refreshDashboardWidget = function(module) { // Refresh  Widget on Dashboard
    var obj=null;
    var obj1=null;
    var moduleWidget = Wtf.moduleWidget;
    var moduleName = Wtf.crmmodule;
    if(module==moduleName.lead){
        obj=Wtf.getCmp(moduleWidget.lead);
    }  else if(module==moduleName.account){
        obj=Wtf.getCmp(moduleWidget.account);
    } else if(module==moduleName.contact){
        obj=Wtf.getCmp(moduleWidget.contact);
    } else if(module==moduleName.opportunity){
        obj=Wtf.getCmp(moduleWidget.opportunity);
    } else if(module==moduleName.cases){
        obj=Wtf.getCmp(moduleWidget.cases);
    } else if(module==moduleName.product){
        obj=Wtf.getCmp(moduleWidget.product);
    } else if(module==moduleName.activity){
        obj=Wtf.getCmp(moduleWidget.activity);
        obj1=Wtf.getCmp(moduleWidget.topactivity);
    } else if(module==moduleName.campaign){
        obj=Wtf.getCmp(moduleWidget.campaign);
    }else if(module==moduleWidget.advancesearch){
        obj=Wtf.getCmp(moduleWidget.advancesearch);
    }else if(module==moduleWidget.customReports){
        obj=Wtf.getCmp(moduleWidget.customReports);
    }

    if(obj!=null){
        obj.callRequest("","",0);
    }
    if(obj1!=null){
        obj1.callRequest("","",0);
    }
    Wtf.refreshUpdatesAll();
}
function cellClick(obj) {
    obj.EditorGrid.on("cellcontextmenu",
    function(grid,row,cellindex,e) {
        var record = grid.getStore().getAt(row);
        var fieldName = grid.getColumnModel().getDataIndex(cellindex);
        var dFormat=['Y-m-d H:i:s','Y-m-d H:i:s T','F, Y','d-m-y','m-d-y','d/m/y','m/d/y','D j-m-y','Y-m-d','n/j/y','l, F d, Y','l, F d, Y g:i:s A','F d','Y-m-d T H:i:s'];
        obj.idFormat=[1,10,11,12,13,14,15,16,2,3,4,5,6,9];
        var date=[];
        if(typeof record.get(fieldName)==='object') {
            e.preventDefault();
            var submenu = new Wtf.menu.Menu();
            var menu = new Wtf.menu.Menu();
            for(var i=0;i<dFormat.length;i++){
                    var id=obj.idFormat[i];
                    date[i] = new Wtf.menu.Item({
                                    text:new Date().format(dFormat[i]).toString(),
                                    id:'Date'+id,
                                    scope:obj,
                                    valX:id,
                                    handler:function(a){
                                        setDates(obj, a);
                                    }
                                });
                submenu.add(date[i]);
            }
            var subHolder = new Wtf.menu.Item({text:"<span wtf:qtip='Choose date format of your choice'>Date Formats</span>",menu:submenu});
            menu.add(subHolder);
            menu.showAt(e.getXY());
        }
    },obj);
}

function setDates(scope, a){
    var format=a.valX;
    Wtf.Ajax.requestEx({
        url: "Common/ProfileHandler/saveDateFormat.do",
        params: {
            mode:786,
            newformat:format,
            userid:loginid
        }
    }, this,
    function(f){
        if(f.success==true) {
            updatePreferences();
            scope.EditorStore.reload();
        }
    },
    function(f){
        var msg=WtfGlobal.getLocaleText("crm.customreport.failurerespmsg");//"Failed to make connection with Web Server";
        if(f.msg)msg=f.msg;
        ResponseAlert([WtfGlobal.getLocaleText("crm.common.updatedateformat"),msg]);
    });
}

Wtf.scrollToLastRow = function(EditorGrid,row) {
    var rowEl = EditorGrid.getView().getRow(row);
    if(rowEl!=undefined){
        var el=EditorGrid.getView().scroller;
        var a=Wtf.fly(rowEl).getOffsetsTo(el);
        el.scrollTo("top",a[1],true);
    }

}

Wtf.commonConflictWindowGrid = function(id,listds,listcm)
{
    var grid= new Wtf.grid.EditorGridPanel({
            region:'center',
            id:id,
            clicksToEdit : 1,
            store: listds,
            cm:listcm,
            sm : new Wtf.grid.RowSelectionModel(),
            border : false,
            width: 434,
            viewConfig: {
                forceFit:true
            }
    })

    return grid;
}

function createCellStyle(sel, keyId,cellStyleId,isDeleted){
   var jsondata = "";
    for(var i=0; i<sel.length; i++){
        var rData = sel[i].data;
        jsondata+='{"id":"' + rData[keyId] + '",';
        jsondata+='"cellStyle":' +Wtf.encode(rData[cellStyleId]) + ',';
        jsondata+='"isdeleted":' +isDeleted + '},';
    }

    var finalStr="";
    if(jsondata.length > 0)
       finalStr = "[" +  jsondata.substr(0, jsondata.length - 1)+ "]";
    return finalStr ;
}


function initializeUndoVar(obj){
    obj.storeFormat=[];
    obj.redoObject =[] ;
}
Wtf.saveStyleInDB = function( sel,excludeStore,isDeleted,keyId,moduleName,obj ) {

    var jsonstr = createCellStyle(sel, keyId,"cellStyle");
    if(jsonstr.length > 0){
        if(!excludeStore){
            if(obj.storeFormat.length == Wtf.UNDO_ARRAY_LENGTH){
                obj.storeFormat.shift();
            }
            obj.storeFormat.push(eval ('(' + createCellStyle(sel, keyId,"tempCellStyle",isDeleted) + ')'));
            obj.spreadSheet.enableDisableUndoButt();
            obj.redoObject =new Array() ;
            obj.spreadSheet.enableDisableRedoButt();
        }
        if(moduleName.indexOf('Activity') != -1 ){
            moduleName ='ActivityMaster';
        }else{
            var index = moduleName.indexOf('Archived');
            if( index != -1 ){
                moduleName =moduleName.substring(0, index);
            }else{
                if(moduleName.endsWith("Contact")){
                    moduleName="Contact";
                }else{
                    if(moduleName.endsWith("Opportunity")){
                        moduleName="Opportunity";
                    }else{
                        if(moduleName.endsWith("Case")){
                            moduleName="Case";
                        }
                    }
                }
            }
        }
        Wtf.Ajax.requestEx({

            url: "Common/Spreadsheet/saveModuleRecordStyle.do",
            params:{
                jsondata : jsonstr,
                module : moduleName,
                action : 11
            }
        },
        this,
        function(res) {

        },
        function() {

        });

    }

},

Wtf.onCellClickShowComments = function(recId, module, ri, event) {
    Wtf.Ajax.requestEx({
        method: "POST",
        url: Wtf.req.springBase+"common/DetailPanel/getComment.do",
        params:{ module: module,
            id:recId
        }
    },this,
    function(response, req) {
        if(response.comment)
        {
            var commentlist=response.comment;
            if(commentlist.length>0){
                var newData = Wtf.commentList(commentlist);
                Wtf.showNotesLinksContainPanel(newData, event, req.params.id, ri);
            }
        }
    });
}

Wtf.showNotesLinksContainPanel= function(data, e, taskid, ri){
        var tasktooltip="<div style='padding:1px 1px 1px 1px;overflow-x:auto;overflow-y:auto;border:1px solid #8DB2E3 !important;'>"+
                        "<div style='background:#E9F2FC;padding:0 5px;overflow-x:auto;overflow-y:auto;max-height:200px;font-size:11px;'>"+data+"</div>"+
                        "</div>";
        var tplRow = tasktooltip;
        if(tplRow) {
            var oldContainPane = Wtf.getCmp("containNotes");
            if(oldContainPane)      // if containpane already present then destroy it and create again as template could not be overwritten
                oldContainPane.destroy();

            var containNotes = new Wtf.Panel({
                id: "containNotes",
                frame: true,
                hideBorders: true,
                baseCls: "sddsf",
                header: false,
                headerastext: false
            });

            var oldWin1 = Wtf.getCmp("winNotes");
            if(oldWin1)             // if win1 already present then destroy it and create again
                oldWin1.destroy();

            var winWidth = 350;
            var winMaxHeight = 200;

            new Wtf.Window({
                id: "winNotes",
                width: winWidth,
                maxHeight: winMaxHeight,
                bodyStyle: 'padding-top: 5px; padding-bottom: 5px;',
                plain: true,
                shadow: true,
                header: false,
                closable: false,
                border: false,
                bodyBorder: true,
                frame: false,
                resizable : false,
                items: containNotes
            }).show();

            var tplNotes = new Wtf.Template(tplRow);
            tplNotes.compile();
            tplNotes.insertAfter("containNotes");
            if(Wtf.getCmp('winNotes')){
                if(!Wtf.get('TaskNotes_'))
                    Wtf.getCmp('winNotes').destroy();
                else
                    Wtf.getCmp('winNotes').setPosition(e.getPageX(), e.getPageY());
            }

        }
},
Wtf.hideNotes = function(event){
    var notesWin = Wtf.getCmp('winNotes');
        if(notesWin){
             notesWin.hide();
    }
},
function parseSmiley(str){
    str = unescape(str);
    var tdiv = document.createElement('div');
    var arr = [];
    arr = str.match(/(:\(\()|(:\)\))|(:\))|(:x)|(:\()|(:P)|(:D)|(;\))|(;;\))|(&gt;:D&lt;)|(:-\/)|(:&gt;&gt;)|(:-\*)|(=\(\()|(:-O)|(X\()|(:&gt;)|(B-\))|(:-S)|(#:-S)|(&gt;:\))|(:\|)|(\/:\))|(=\)\))|(O:-\))|(:-B)|(=;)|(:-c)/g);
    if (arr == null) {
        tdiv.innerHTML = str;
    } else {
        var i;
        tdiv.innerHTML = str;
        for (i = 0; i < arr.length; i++) {
            smiley(tdiv, arr[i]);
        }
    }
    return tdiv.innerHTML;
}
Wtf.commentList = function(commentlist){
    var newData = "<b>Comment(s) :</b><br>";
    var str=new RegExp("\n","g")
    commentlist=commentlist.replace(str,"<br>");
    newData+=commentlist;
    return parseSmiley(unescape(newData)) ;
},

Wtf.AddNewButton = function(o){
	this.addNew= new Wtf.Toolbar.Button({
        text:WtfGlobal.getLocaleText("crm.editor.addRecord"),//"Add Record",
        iconCls:"pwnd addIcon",
        scope:o,
        tooltip:{text:WtfGlobal.getLocaleText("crm.editor.addnewBTN.ttip")},//'Add new record by clicking here.'},
        handler:function(o){
        	var addrecurl=this.spreadSheet.getGrid().updateURL;
        	var params={
            			addNewRec:true,
            			url:addrecurl,
            			key:this.updateInfo.keyField,
            			flag:this.updateInfo.flag
            }
            spreadSheetDetailWindow(this,this.spreadSheet,params);
            	
        }
    },this);
    return this.addNew;
},

Wtf.EditRecordButton = function(o){
	var moduleName = o.modName;
	o.editRec= new Wtf.Toolbar.Button({
		text : WtfGlobal.getLocaleText("crm.editor.editRecord"),//"Edit Record",
		tooltip :{text :(moduleName!='Opportunity' && moduleName!='Account')? WtfGlobal.getLocaleText({key:"crm.editor.editRecord.a.ttip",params:[moduleName]}):WtfGlobal.getLocaleText({key:"crm.editor.editRecord.an.ttip",params:[moduleName]})},//"Select a "+moduleName+" to update" : "Select an "+moduleName+" to update"},
		disabled:true,
		scope : o,
		iconCls:'pwnd massEdit',
		handler : function(o) {
			if(this.updateInfo.massUpdate!=undefined)
				delete this.updateInfo["massUpdate"];
			if(this.updateInfo.detailPanelRecUpdate!=undefined)
				delete this.updateInfo["detailPanelRecUpdate"];
			this.updateInfo["singleRecUpdate"]=true;
			spreadSheetDetailWindow(this,this.spreadSheet,this.updateInfo);
        }
    },this);
    return o.editRec;
},

Wtf.editDetailButton = function(o){
	 this.editBtn=new Wtf.Toolbar.Button({
         text : WtfGlobal.getLocaleText({key:"crm.spreadsheet.addEditUpdateWin.detailpanedit.tophtml",params:[o.moduleName]}),//"Edit "+o.moduleName+" Details","Edit "+o.moduleName+" Details",
         scope : o,
         iconCls:"pwndCRM contactsTabIcon",
         handler : function() {
     		var params = eval(this.moduleScope.updateInfo);
     		if(params.massUpdate!=undefined)
     			delete params["massUpdate"];
     		if(params.singleRecUpdate!=undefined)
     			delete params["singleRecUpdate"];
      		params["detailPanelRecUpdate"]=true;
     		spreadSheetDetailWindow(this,this.moduleScope.spreadSheet,params);
	 	 }
	 },this);
	 return this.editBtn;
},
Wtf.addNewCampaign = function() {

    var flag=0;
    this.campPanel = new Wtf.quickadd({
                dashcomp:Wtf.moduleWidget.campaign,
                configType:"Campaign",
                compid:"campcomptree",
                border: false,
                paramObj:{flag:20,auditEntry:1},
                url:Wtf.req.springBase+'Campaign/action/saveCampaigns.do',
                actionCode:0,
                jsonstr:{isCampaignNameEdit:true, campaignid:'0',campaignownerid:loginid}

            })
    this.campPanel.on("aftersave",function(res,json){
        this.win.close();
        addCampaignWithEmailCampaignTab(res);
    },this);
    this.campPanel.on("closeform",function(){
            if(flag!=0)
        this.win.setHeight(180+this.campPanel.objresponse.data.length*40);
    },this);
        this.win=new Wtf.Window({
            height:270,
            width:400,
            id:'quickinsert',
            iconCls: "pwnd favwinIcon",
            title:WtfGlobal.getLocaleText("crm.CAMPAIGN"),//"Campaign",
            modal:true,
            shadow:true,
            resizable:false,
            buttonAlign:'right',
            buttons: [{
                text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
                scope:this,
                handler:function(){
                    var obj = this.campPanel.objPanel;
                    var sdateObj = Wtf.getCmp(obj.sdate+obj.id);
                    var edateObj = Wtf.getCmp(obj.edate+obj.id);

                    var startdate = sdateObj.getValue();
                    var enddate = edateObj.getValue();

                    if(startdate > enddate) {
                    	 Wtf.Msg.alert('Invalid Input',"End date should not be less than Start date.");
                    	 edateObj.setValue("");
                    	 return;
                    } 

                    this.campPanel.saveobj();
                }
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                scope:this,
                handler:function(){
                    this.win.close()
                }
            }],
            layout : 'border',
            items :[{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.campaign.createcampaignwin.tophtml.title"),WtfGlobal.getLocaleText("crm.campaign.createcampaignwin.tophtml.detail"),"../../images/Campaigns.gif")
            },{
                region : 'center',
                border : false,

                bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
                layout : 'fit',
                items :[   this.campPanel   ]
            }]

        });
        flag=1;
        this.win.show();

}
Wtf.moduleBootomToolBar = function(moduleObj,detailPanelId,contactsPermission,leadDetailFlag){
    var toolItems=new Array();
    var moduleName=moduleObj.modName;

    var msgModName="";
	switch(moduleName){
    case "Account":msgModName=WtfGlobal.getLocaleText("crm.ACCOUNT");
    			   break;
    case "Contact":msgModName=WtfGlobal.getLocaleText("crm.CONTACT");
    			   break;
    case "Opportunity":msgModName=WtfGlobal.getLocaleText("crm.OPPORTUNITY");
    			   break;
    case "Leads":msgModName=WtfGlobal.getLocaleText("crm.LEAD");
	   			   break;
    case "Activity":msgModName=WtfGlobal.getLocaleText("crm.ACTIVITY");
	   			   break;	  
    case "Case":msgModName=WtfGlobal.getLocaleText("crm.CASE");
	   			   break;
    case "Product":msgModName=WtfGlobal.getLocaleText("crm.PRODUCT");
	   break;
	}	
	if(msgModName=="")
		msgModName=moduleName;
        var module="";
        if(moduleName=="Account" ||moduleName=="Opportunity" || moduleName=="Activity")
            module="an "+moduleName;
        else
            module="a "+moduleName;


    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Document, Wtf.Perm.Document.doc_up)) {
      toolItems.push(moduleObj.document = new Wtf.Toolbar.Button({
        text: WtfGlobal.getLocaleText("crm.activitydetailpanel.addfilesBTN"),//'Add Files',
        iconCls: 'pwnd doctabicon',
        scope: moduleObj,
        tooltip: {text: WtfGlobal.getLocaleText({key:"crm.activitydetailpanel.addfilesBTN.ttip",params:[msgModName]})},//'Select '+module+' to add files.'},
        disabled:true,
        handler : function() {
            Wtf.getCmp(detailPanelId).Addfiles();
        }
      })
      );
    }

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Comments, Wtf.Perm.Comments.comment_a)) {
          toolItems.push(moduleObj.comment=new Wtf.Toolbar.Button({
            text :WtfGlobal.getLocaleText("crm.activitydetailpanel.addCommentBTN"),// "Add Comment",
            pressed: false,
            scope : moduleObj,
            tooltip: {text: WtfGlobal.getLocaleText({key:"crm.activitydetailpanel.addCommentBTN.ttip",params:[msgModName]})},//'Select '+moduleName+' to add comments.'},
            iconCls:'pwnd addcomment',
            disabled:true,
            handler : function() {
                Wtf.getCmp(detailPanelId).addComment();
            }
          })
          );
        }
        if(moduleName=="Account" ||moduleName=="Opportunity" || moduleName=="Lead" || moduleName=="Contact" ||moduleName=="LeadOld") {
           var add;
           if(moduleName=="Account" || moduleName=="Opportunity"){
              add="an";
           }else{
              add="a";
           }
          toolItems.push(moduleObj.addOwners=new Wtf.Toolbar.Button({
            id:"tempButton",
            text : WtfGlobal.getLocaleText("crm.editor.bottomtoolbar.admodOwnersBTN"),//"Add/Modify Owners",
            pressed: false,
            scope : moduleObj,
            tooltip: {text: WtfGlobal.getLocaleText({key:"crm.editor.bottomtoolbar.admodOwnersBTN.ttip",params:[msgModName]})},//'Only main owner can add/modify owners.Select '+add+" "+moduleName+' to add/modify owners.'},
            iconCls:'pwnd addowners',
            disabled:true,
            handler : function() {
                Wtf.getCmp(detailPanelId).addSubOwners();
            }
          })
          );
          if(moduleName != "Contact" && contactsPermission){
              toolItems.push(moduleObj.showcontactsButton=new Wtf.Toolbar.Button({
                text : moduleName=="Lead"?WtfGlobal.getLocaleText("crm.editor.lead.companyContactsBTN"):WtfGlobal.getLocaleText("crm.editor.contactsBTN"),//"Company Contacts":"Contacts",
                pressed: false,
                scope : moduleObj,
                tooltip: {text:WtfGlobal.getLocaleText({key:"crm.editor.lead.companyContactsBTN.ttip",params:[msgModName]})},// 'Select a '+moduleName+' to record related contacts.'},
                iconCls:getTabIconCls(Wtf.etype.contacts),
                disabled:true,
                handler : function() {
                    Wtf.getCmp(detailPanelId).addContacts();
                }
              })
              );
          }
        }
        
        if(moduleName=="Account" ||moduleName=="Opportunity" || moduleName=="Lead" || moduleName=="Contact" ||moduleName=="Case" ||moduleName=="Product"||moduleName=="Campaign"){
        	toolItems.push(moduleObj.massUpdate=new Wtf.Toolbar.Button({
        		text:WtfGlobal.getLocaleText("crm.editor.massupdateBTN"),//'Mass Update',
                iconCls:'pwnd massEdit',
                disabled:true,
                tooltip:{text:WtfGlobal.getLocaleText({key:"crm.editor.massupdateBTN.ttip",params:[moduleName]})},//'Select '+moduleName+'(s) to mass update.'},
                handler:function(){
        			if(moduleObj.updateInfo.singleRecUpdate!=undefined)
        				//moduleObj.updateInfo["singleRecUpdate"]=false;
        				delete moduleObj.updateInfo["singleRecUpdate"];
        			if(moduleObj.updateInfo.detailPanelRecUpdate!=undefined)
        				delete moduleObj.updateInfo["detailPanelRecUpdate"];
        			moduleObj.updateInfo["massUpdate"]=true;
        			spreadSheetDetailWindow(moduleObj,moduleObj.spreadSheet,moduleObj.updateInfo);
                }
        	})
        	);
        }

        if(leadDetailFlag) {
            toolItems.push(moduleObj.moduleDetail=new Wtf.Toolbar.Button({
                text : WtfGlobal.getLocaleText({key:"crm.editor.bottomtoolbar.moddetailsBTN",params:[msgModName]}),//moduleName+" Details",
                pressed: false,
                scope : moduleObj,
                tooltip: {
                    text: WtfGlobal.getLocaleText({key:"crm.editor.bottomtoolbar.moddetailsBTN.ttip",params:[msgModName]})//'Select a ' + moduleName + ' to view details.'
                    },
                iconCls:getTabIconCls(Wtf.etype.todo),
                disabled:true,
                handler : function() {
                    moduleObj.showLeadDetails();
                }
            })
            );
        }
        if(moduleName == "Account" && Wtf.createProject) {
            toolItems.push(moduleObj.addProject=new Wtf.Toolbar.Button({
                text : "Create Project",
                pressed: false,
                scope : moduleObj,
//                hidden:true,
                tooltip: {
                    text: 'Add projects to selected '+moduleName
                },
                iconCls:'pwnd addProject',
                disabled:true,
                handler : function() {
                    Wtf.getCmp(detailPanelId).addAccProjects();
                }
            })
            );
        }

    return toolItems;
}

function docCommentEnable(obj,detailPanelObj,isMainOwner,enableContactsButton){
    if(!detailPanelObj.detailPanelFlag|| ( detailPanelObj.detailPanelFlag && obj.document && obj.comment) ) {
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Document, Wtf.Perm.Document.doc_up)) {
          obj.document.enable();
          obj.document.setTooltip('Add files to the selected '+obj.customParentModName+'.');
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Comments, Wtf.Perm.Comments.comment_a)) {
          obj.comment.enable();
          obj.comment.setTooltip('Add comments to the selected '+obj.customParentModName+'.');
        }
     }
}

function enableButt(obj,detailPanelObj,isMainOwner,enableContactsButton){
        if(!detailPanelObj.detailPanelFlag) {
        	docCommentEnable(obj,detailPanelObj,isMainOwner,enableContactsButton);
            if(obj.addOwners !=  undefined && isMainOwner){
                obj.addOwners.enable();
                obj.addOwners.setTooltip('Add/modify owners to the selected '+obj.customParentModName+'.');
            }
            if(obj.moduleDetail) {
                obj.moduleDetail.enable();
                obj.moduleDetail.setTooltip('View details of the selected '+obj.customParentModName+' record.');
            }
            if(obj.addProject) {
                obj.addProject.enable();
                obj.addProject.setTooltip('Add projects to selected '+obj.customParentModName);
            }
            if(obj.showcontactsButton !=  undefined && enableContactsButton){
                obj.showcontactsButton.enable();
                obj.showcontactsButton.setTooltip('Record contact details related to the selected '+obj.customParentModName+'.');
            }
        }
        if(obj.editRec !=undefined){
        	obj.editRec.enable();
        }
}


function spreadSheetDetailWindow(moduleObj,spreadSheet,updateInfo){
	var textFieldArr = [];
	var textFieldArr1 = [];
	var addNewRecFlag = updateInfo.addNewRec ? updateInfo.addNewRec :false;
	var singleRecUpdateFlag = updateInfo.singleRecUpdate ? updateInfo.singleRecUpdate :false;
	var massUpdateFlag = updateInfo.massUpdate ? updateInfo.massUpdate :false;
	var detailPanelFlag= updateInfo.detailPanelRecUpdate ? updateInfo.detailPanelRecUpdate : false;
	var cm=spreadSheet.getGrid().colModel;
	var sel=spreadSheet.getGrid().getSelectionModel().getSelections();
	var selectedRec = ( (singleRecUpdateFlag || detailPanelFlag) && !addNewRecFlag && !massUpdateFlag ) ? spreadSheet.getGrid().getSelectionModel().getSelected() : undefined;
	var fields=spreadSheet.getGrid().getStore().fields.items;
	var defVal="";
	var defValId="";
	var colConf=undefined;
	var editor=undefined;
	var val="";
	var windowTitle="";
	var topHtml="";
	// var windowTitle=updateInfo.addNewRec ?WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.addnew.tophtml"): (updateInfo.singleRecUpdate ? WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.editrec.title"): (updateInfo.detailPanelRecUpdate? WtfGlobal.getLocaleText({key:"crm.spreadsheet.addEditUpdateWin.detailpanedit.title",params:[moduleObj.moduleName]}):WtfGlobal.getLocaleText("crm.editor.massupdateBTN")));
	// var topHtml= getTopHtml(updateInfo.addNewRec ?WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.addnew.tophtml"):(updateInfo.singleRecUpdate ? WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.editrec.tophtml"):(updateInfo.detailPanelRecUpdate? WtfGlobal.getLocaleText({key:"crm.spreadsheet.addEditUpdateWin.detailpanedit.tophtml",params:[moduleObj.moduleName]}):WtfGlobal.getLocaleText("crm.editor.massupdateBTN"))), updateInfo.addNewRec ?WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.addnew.tophtmldetail"):(updateInfo.singleRecUpdate ? WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.editrec.tophtmldetail"):(updateInfo.detailPanelRecUpdate? WtfGlobal.getLocaleText({key:"crm.spreadsheet.addEditUpdateWin.detailpanedit.tophtmldetail",params:[moduleObj.recname]}):WtfGlobal.getLocaleText("crm.editor.massupdateBTN.detailwin"))))
	if(updateInfo.addNewRec){
		windowTitle = WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.addnew.tophtml");
		topHtml = getTopHtml(WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.addnew.tophtml"),WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.addnew.tophtmldetail"));
	}else if(updateInfo.singleRecUpdate){
		windowTitle = WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.editrec.title");
		topHtml = getTopHtml(WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.editrec.tophtml"),WtfGlobal.getLocaleText("crm.spreadsheet.addEditUpdateWin.editrec.tophtmldetail"));
	}else if(updateInfo.detailPanelRecUpdate){
		windowTitle = WtfGlobal.getLocaleText({key:"crm.spreadsheet.addEditUpdateWin.detailpanedit.title",params:[moduleObj.moduleName]});
		topHtml = getTopHtml(WtfGlobal.getLocaleText({key:"crm.spreadsheet.addEditUpdateWin.detailpanedit.tophtml",params:[moduleObj.moduleName]}),WtfGlobal.getLocaleText({key:"crm.spreadsheet.addEditUpdateWin.detailpanedit.tophtmldetail",params:[moduleObj.recname]}));
	}else{
		windowTitle = WtfGlobal.getLocaleText("crm.editor.massupdateBTN");
		topHtml = getTopHtml(WtfGlobal.getLocaleText("crm.editor.massupdateBTN"),WtfGlobal.getLocaleText("crm.editor.massupdateBTN.detailwin"));
	}
	
	for(var colModelCount=3; colModelCount < cm.config.length; colModelCount++) {
		colConf = cm.config[colModelCount];
		editor = colConf.sheetEditor;
		defVal="";
		defValId="";
		if(addNewRecFlag){
			for(var x=0;x<fields.length;x++){
				if(fields[x].name==colConf.dataIndex && fields[x].defValue!=undefined && fields[x].defValue!="")
					defVal=fields[x].convert((typeof fields[x].defValue == "function"?fields[x].defValue.call():fields[x].defValue));
				if(fields[x].name==colConf.dataIndex+"id" && fields[x].defValue!=undefined && fields[x].defValue!="")
					defValId = fields[x].convert((typeof fields[x].defValue == "function"?fields[x].defValue.call():fields[x].defValue));
			}
		}
		if(!editor&&colConf.editor)
			editor=colConf.editor.field;
		if(colConf.fixed)
			continue;
		if(colConf.editable==false) {
			continue;
		}

		if(editor) {
			if((selectedRec && singleRecUpdateFlag)||(selectedRec && detailPanelFlag)||addNewRecFlag){
				if(colConf.xtype=="numberfield"){
					if(val!=""){
						mod = WtfGlobal.replaceAll("'"+val+"'","([^\\d\\.])","");
						val=mod;
					}
				}
				if(colConf.xtype=="combo" && editor.searchStoreCombo){
					var hiddenVal = addNewRecFlag?defValId:selectedRec.data[colConf.dataIndex];
					if(colModelCount>cm.config.length/2){
						textFieldArr1.push({
							xtype : 'hidden',
							name : addNewRecFlag?colConf.dataIndex+"id":colConf.dataIndex,
							value: hiddenVal == ""?undefined:hiddenVal
						});
					}else{
						textFieldArr.push({
							xtype : 'hidden',
							name : addNewRecFlag?colConf.dataIndex+"id":colConf.dataIndex,
							value: hiddenVal == ""?undefined:hiddenVal
						});
					}
				}
			}
			val=(selectedRec==undefined)? defVal: selectedRec.data[(editor.comboFieldDataIndex && !addNewRecFlag)? editor.comboFieldDataIndex+"id" : colConf.dataIndex];

			if(colModelCount>cm.config.length/2){
				textFieldArr1.push({
					xtype: (colConf.xtype=="textfield")?"striptextfield":colConf.xtype,
					fieldLabel: colConf.header,
					value: val == ""?undefined:val,
					store: (colConf.xtype == "combo" || colConf.xtype == "select") ? editor.store : null,
					displayField: "name",	
					name:(editor.comboFieldDataIndex && !addNewRecFlag)? editor.comboFieldDataIndex+"id" : colConf.dataIndex,
					hiddenName:(editor.comboFieldDataIndex && !addNewRecFlag)? editor.comboFieldDataIndex+"id" : colConf.dataIndex,
					valueField: (colConf.xtype=='timefield')?"":"id",
					allowNegative : (colConf.xtype=="numberfield")?false:"",
					editable : (colConf.xtype=="combo" && !editor.searchStoreCombo)?false:true,
					maxLength : editor.maxLength != undefined?editor.maxLength:Number.MAX_VALUE,
					mode: editor.mode ? (editor.storemanagerkey ? (Wtf.StoreMgr.containsKey(editor.storemanagerkey) ? "local":editor.mode):editor.mode) : "local",
					emptyText: colConf.xtype == "combo" ? WtfGlobal.getLocaleText({key:"crm.commoncombomtytxt",params:[(editor.searchStoreCombo && !editor.loadOnSelect ? WtfGlobal.getLocaleText("crm.audittrail.searchBTN"):WtfGlobal.getLocaleText("crm.selecttxt"))]}) : "",
					minChars : 2,
					//triggerClass : (editor.searchStoreCombo && !editor.loadOnSelect) ? "dttriggerForTeamLead" : "",
					triggerAction: "all",
					width:230,
					format:(colConf.xtype=="datefield")?WtfGlobal.getOnlyDateFormat():WtfGlobal.getLoginUserTimeFormat(),
					offset:Wtf.pref.tzoffset,
					vtype:colConf.vtype!=undefined?colConf.vtype:"",
					multiSelect:colConf.xtype == "select",
					tpl: (colConf.xtype == "combo" || colConf.xtype == "select") ? Wtf.comboTemplate : undefined,
					listeners : {
							"beforeselect": spreadSheet.validateSelection,
							"select":(!updateInfo.massUpdate)?function(combo, record, index){
									if(combo.xtype=="combo") {
										this.editForm.getForm().findField(addNewRecFlag?combo.name+"id":combo.name).setValue(record.data.id);
										if(!addNewRecFlag)
											this.editForm.getForm().findField(combo.name.substring(0,combo.name.length-2)).setValue(record.data.name);
									}
							}:function(c,r,i){},
							scope : this
					}
				});
			}else{
				textFieldArr.push({
					xtype:(colConf.xtype=="textfield")?"striptextfield":colConf.xtype,
					fieldLabel: colConf.header,
					value: val == ""?undefined:val,
					store: (colConf.xtype == "combo" || colConf.xtype == "select") ? editor.store : null,
					displayField: "name",
					name:(editor.comboFieldDataIndex && !addNewRecFlag)? editor.comboFieldDataIndex+"id" :colConf.dataIndex,
					hiddenName:(editor.comboFieldDataIndex && !addNewRecFlag)? editor.comboFieldDataIndex+"id" : colConf.dataIndex,
					valueField: (colConf.xtype=='timefield')?"":"id",
					allowNegative : (colConf.xtype=="numberfield")?false:"",
					editable : (colConf.xtype=="combo" && !editor.searchStoreCombo)?false:true,
					maxLength : editor.maxLength != undefined?editor.maxLength:Number.MAX_VALUE,
					mode: editor.mode ? (editor.storemanagerkey ? (Wtf.StoreMgr.containsKey(editor.storemanagerkey) ? "local":editor.mode):editor.mode) : "local",
					emptyText: colConf.xtype == "combo" ? WtfGlobal.getLocaleText({key:"crm.commoncombomtytxt",params:[(editor.searchStoreCombo && !editor.loadOnSelect ? WtfGlobal.getLocaleText("crm.audittrail.searchBTN"):WtfGlobal.getLocaleText("crm.selecttxt"))]}) : "",
					minChars : 2,
					//triggerClass : (editor.searchStoreCombo && !editor.loadOnSelect) ? "dttriggerForTeamLead" : "",
					triggerAction: "all",
					width:230,
					format:(colConf.xtype=="datefield")?WtfGlobal.getOnlyDateFormat():WtfGlobal.getLoginUserTimeFormat(),
					offset:Wtf.pref.tzoffset,
					vtype:colConf.vtype!=undefined?colConf.vtype:"",
					multiSelect:colConf.xtype == "select",
					tpl: (colConf.xtype == "combo" || colConf.xtype == "select") ? Wtf.comboTemplate : undefined,
					listeners : {
							"beforeselect": spreadSheet.validateSelection,
							"select":(!updateInfo.massUpdate)?function(combo, record, index){
									if(combo.xtype=="combo") {
										this.editForm.getForm().findField(addNewRecFlag?combo.name+"id":combo.name).setValue(record.data.id);
										if(!addNewRecFlag)
											this.editForm.getForm().findField(combo.name.substring(0,combo.name.length-2)).setValue(record.data.name);
									}
							}:function(c,r,i){},
							scope : this
					}
				});
			}
		}
	}
	this.editForm = new Wtf.form.FormPanel({
		layout:'column',
		border :false,
		items:[{
			columnWidth: .5,
			border:false,
			labelWidth:125,
			layout:'form',
			items:textFieldArr
		},{
			columnWidth: .5,
			border:false,
			labelWidth:125,
			layout:'form',
			items:textFieldArr1
		}
		]
	});
	function disableButton(ob){
		ob.editWindow.buttons[0].disable();
	};
	this.editWindow = new Wtf.Window({
		autoHeight:true,
		width:800,
		autoScroll:true,
		iconCls: "pwnd favwinIcon",
		title:windowTitle,
		modal:true,
		resizable:false,
		buttonAlign:'right',
		buttons: [{
			text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
			scope:this,
			handler:function() {
			 	disableButton(this);
				saveUpdatedData(moduleObj,spreadSheet,updateInfo);
			}
		},{
			text:WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),// 'Close',
			scope:this,
			handler:function(){
			this.editWindow.close();
		}
		}],
		layout : 'fit',
		items :[{
			region : 'north',
			autoHeight:true,
			layout :'fit',
			border : false,
			bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
			html: topHtml
		},{
			region : 'center',
			border : false,
			//layout :'fit',
			bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 20px 10px 20px',
			//autoHeight:true,
			height : 350,
			autoScroll:true,
			items :[this.editForm]
		}]
	});
	this.editWindow.show();
} 


function saveUpdatedData (moduleSource,spreadSheet,updateInfo) {
    this.EditorGrid=spreadSheet.getGrid();
    var baseForm = this.editForm.getForm();
    if(!baseForm.isValid()){
        WtfComMsgBox(951,0);
        return;
    }
    var rec = baseForm.getValues();
    var jsonData = {auditstr:updateInfo.auditStr};
    var hasData = false;
    var customData=undefined;
    if(updateInfo.addNewRec==undefined || !updateInfo.addNewRec){
    	var recData = this.EditorGrid.getSelectionModel().getSelections();
    	var ids=[];
    	for(var i=0;i<this.EditorGrid.getSelectionModel().getSelections().length;i++){
    		if(this.EditorGrid.moduleName=="Campaign"){
    			var endd = baseForm.findField("enddate");
    			var startd = baseForm.findField("startdate");
    			if(endd.getValue()!=""){
    				if(startd!="" && startd.getValue().getTime() > endd.getValue().getTime()){
    					ResponseAlert(86);
    					return false;
    				}
    			}
    			if(startd.getValue()!=""){
    				if(endd!="" && startd.getValue().getTime() > endd.getValue().getTime()){
    					ResponseAlert(86);
    					return false;
    				}
    			}
    		}
    		var tmpid= recData[i].data[updateInfo.keyField];
    		if(tmpid!="0")
    			ids.push(tmpid);
       }
       jsonData[updateInfo.keyField] = ids.join(',');
       if(rec.startdate > rec.enddate){
    	   ResponseAlert(86);
           return false;
       }
       var formitems = baseForm.items.items;
       var item=undefined;
       for(var i=0;i<formitems.length;i++){
    	   item = formitems[i];
    	   for(var key in rec){
    		   if(key==item.name){
    			   if((item.originalValue != "" && key==item.name && item.originalValue==rec[key]) || (item.originalValue=="" && (rec[key]==""||rec[key]==undefined))){
    				   delete rec[key];
    			   }else{
    				   hasData = true;
    				   var fld = baseForm.findField(key);
    				   if(fld.xtype=="datefield"){
    					   rec[key] = fld.getValue().getTime();
    				   }
    				   if(fld.xtype=="timefield"){
    					   rec[key] = WtfGlobal.convertToGenericTime(Date.parseDate(rec[key],WtfGlobal.getOnlyTimeFormat()));
    				   }
    			   } 
        		   break;
    		   }
    	   }
       }
    }else{
    	
		if(this.EditorGrid.moduleName.endsWith("Activity")){
			var endd = baseForm.findField("enddate");
			var startd = baseForm.findField("startdate");
			if(endd.getValue()!=""){
				if(startd!="" && startd.getValue().getTime() > endd.getValue().getTime()){
					ResponseAlert(86);
					return false;
				}
			}
			if(startd.getValue()!=""){
				if(endd!="" && startd.getValue().getTime() > endd.getValue().getTime()){
					ResponseAlert(86);
					return false;
				}
			}
		}

		   hasData = true;
	       for(var key in rec){
	    	   hasData = true;
	    	   var fld = baseForm.findField(key);
	    	   if(fld.xtype=="datefield"){
	    		   if(fld.getValue()!="" && fld.getValue()!=undefined)
	    			   rec[key] = fld.getValue().getTime();
	    		   else
	    			   delete rec[key];
	    	   }
	    	   if(fld.xtype=="timefield"){
	    		   if(fld.getValue()!="" && fld.getValue()!=undefined)
	    			   rec[key] = rec[key];
	    		   else
	    			   delete rec[key];
	    	   }
	       }   	
    }   
	jsonData[updateInfo.key]=0;
    customData = spreadSheet.getCustomColumnData(rec,true).substring(13);
    if(customData.length > 4){
 	   jsonData.customfield = eval('('+customData+')');
 	   hasData = true;
    }
	jsonData["relatedtoid"] = moduleSource.Rrelatedto!=undefined ? moduleSource.Rrelatedto:"";
	jsonData["relatedtonameid"] = moduleSource.relatedtonameid!=undefined ? moduleSource.relatedtonameid:"";
    Wtf.applyIf(jsonData,rec);
    if(updateInfo.addNewRec!=undefined || updateInfo.addNewRec)
    	jsonData["createdon"]=new Date().getTime();
    if(hasData && updateInfo.url.length > 0) {
    	var parameters=[];
    	if(updateInfo.addNewRec!=undefined && updateInfo.addNewRec){
    		parameters= {
    				jsondata:Wtf.util.JSON.encode(jsonData),
    				flag:updateInfo.flag,
    				massEdit:true
    		}
        }else{
    		parameters = {
                    jsondata:Wtf.util.JSON.encode(jsonData),
                    type:2,
                    flag:updateInfo.flag,
                    massEdit:true
            }
    	}
        Wtf.Ajax.requestEx({
            url: (updateInfo.addNewRec!=undefined && updateInfo.addNewRec)? updateInfo.url:Wtf.req.springBase+updateInfo.url,
            params:parameters
        },this,
        function(res) {
            this.editWindow.close();
            this.EditorGrid.getStore().reload();
            Wtf.refreshDashboardWidget(updateInfo.type);
            if(moduleSource.view16!=undefined || moduleSource.view17!=undefined){
            	var tempRec= recData[0].data;
           		var ds=undefined;
           		for(var key in rec){
           			tempRec[key]=rec[key];
           		}
           		if(moduleSource.moduleName=="Lead")
           			ds=moduleSource.getLeadDataViewStore(tempRec,res.createdon);
           		else if(moduleSource.moduleName=="Case")
           			ds=moduleSource.getCaseDataViewStore(tempRec,res.createdon);
           		else if(moduleSource.moduleName=="Account")
           			ds=moduleSource.getAccountDataViewStore(tempRec,res.createdon);
           		else if(moduleSource.moduleName=="Contact")
           			ds=moduleSource.getContactDataViewStore(tempRec,res.createdon);

           		if(moduleSource.view16!=undefined && moduleSource.view16.store){
           			moduleSource.view16.setStore(ds);
           			moduleSource.setAboutDetails("", moduleSource.fields,moduleSource.values, moduleSource.customField, moduleSource.customValues,undefined,moduleSource.fieldCols,true);
           		}
            	if(moduleSource.view17!=undefined && moduleSource.view17.store){
            		moduleSource.view17.setStore(ds);
            		moduleSource.setAboutDetails("", moduleSource.fields,moduleSource.values, moduleSource.customField, moduleSource.customValues,undefined,moduleSource.fieldCols,true);
            	}
            	
            	ResponseAlert(['',moduleSource.moduleName + ' details successfully edited.']);
            }
            if(updateInfo.addNewRec!=undefined && updateInfo.addNewRec){
            	ResponseAlert(['',moduleSource.modName + ' added successfully.']);
            }
            
        },
        function(res) {
            WtfComMsgBox(12,1);
        });
    }
}


function docCommentDisable(obj){
     if(obj.document) {
         obj.document.disable();
         obj.document.setTooltip('Select a ' + obj.customParentModName + ' to add files.');
     }
     if(obj.comment) {
         obj.comment.disable();
         obj.comment.setTooltip('Select a ' + obj.customParentModName + ' to add comments.');
     }
 }
//kapil kumar chhattani
    function disableButt(obj){
    	docCommentDisable(obj);
       if(obj.addOwners !=  undefined ){
            obj.addOwners.disable();
            obj.addOwners.setTooltip('Only main owner can add/modify owners. Select '+obj.customParentModName+' to add/modify owners.');
        }
        if(obj.moduleDetail) {
            obj.moduleDetail.disable();
            obj.moduleDetail.setTooltip('Select a ' + obj.customParentModName + ' to view details.');
        }
        if(obj.addProject) {
            obj.addProject.disable();
            obj.addProject.setTooltip('Select a ' + obj.customParentModName + ' to add projects to it.');
        }
        if(obj.showcontactsButton !=  undefined ){
            obj.showcontactsButton.disable();
            obj.showcontactsButton.setTooltip('Select a '+obj.customParentModName+' to record related contacts.');
        }
        if(obj.editRec !=undefined){
        	obj.editRec.disable();
        	//obj.singlerecUpdateBtn.setTooltip()
        }
    }
function getColHeader(EditorColumnArray){
    var colHeader=[];
    var colName;
    for(var i =0 ; i < EditorColumnArray.length ; i++){
        if(EditorColumnArray[i].fixed) {
            continue;
        }
        colName= EditorColumnArray[i].header.trim();
        if(colName.substring(colName.length-1,colName.length)=="*"){
            colName = colName.substring(0,colName.length-1);
        }
        colName = colName.trim();
        if(colName!=""){
            colHeader.push(colName);
        }
    }
    colHeader.push("subowners");
    return colHeader;
}
   function showLeadDetails(openPanel) {

    this.rec=openPanel.EditorGrid.getSelectionModel().getSelected();
    if(this.rec!=undefined){

        var recData = this.rec.data;
        var id="";
        var mapid;
        var fieldcols=[];
        var FieldValues=[];
        var ownerStore =  getModuleOwnerStore(openPanel.customParentModName);
        if(openPanel.customParentModName=="Lead"){
            id=this.rec.data.leadid;
            mapid=1;
            var ProductValue=searchValueFieldMultiSelect(Wtf.productStore,this.rec.data.productid,'id','name')
            this.recname =this.rec.data.lastname;
            fieldcols=['Type','Last Name','First Name','Email','Lead Owner','Created On','Updated On','Product','Expected Revenue('+WtfGlobal.getCurrencySymbol()+')','Price('+WtfGlobal.getCurrencySymbol()+')','Designation','Lead Status','Rating','Lead Source','Industry','Phone','Address','subowners'];
            FieldValues= [searchValueField(Wtf.LeadTypeStore,recData.type,'id','name'),recData.lastname,recData.firstname,recData.email,recData.leadowner,WtfGlobal.onlyDateRenderer(recData.createdon),WtfGlobal.onlyDateRenderer(recData.updatedon),ProductValue,recData.revenue,recData.price,recData.title,recData.leadstatus,recData.rating,recData.leadsource,recData.industry,recData.phone,recData.addstreet,recData.subowners];
        }else if(openPanel.customParentModName=="Account"){
            id=this.rec.data.accountid;
            mapid=4;
            ProductValue=searchValueFieldMultiSelect(Wtf.productStore,this.rec.data.productid,'id','name')
            this.recname=this.rec.data.accountname
            fieldcols = ['Account Name','Email','Account Owner','Revenue ('+WtfGlobal.getCurrencySymbol()+')','Product','Price ('+WtfGlobal.getCurrencySymbol()+')','Type','Industry','Phone','Website','Description','Account Creation Date','Account Updated Date','subowners'];
            FieldValues = [recData.accountname,recData.email,recData.accountowner,recData.revenue,ProductValue,recData.price,recData.accounttype,recData.industry,recData.phone,recData.website,recData.description,WtfGlobal.onlyDateRenderer(recData.createdon),WtfGlobal.onlyDateRenderer(recData.updatedon),recData.subowners];
        } else if(openPanel.customParentModName=="Contact"){
            id=this.rec.data.contactid;
            mapid=2;
            this.recname=this.rec.data.lastname
            fieldcols = ['First Name','Last Name','Contact Owner','Account Name','Designation','Lead Source','Industry','Email','Phone','Mobile','Address','Description','Contact Creation Date','Contact Updated Date','subowners'],
            FieldValues = [recData.firstname,recData.lastname,recData.contactowner,recData.relatedname,recData.title,recData.leadsource,recData.industry,recData.email,recData.phoneno,recData.mobileno,recData.street,recData.description,WtfGlobal.onlyDateRenderer(recData.createdon),WtfGlobal.onlyDateRenderer(recData.updatedon),recData.subowners];
        }
         var tipTitle=this.recname+"'s Details";
          var title = Wtf.util.Format.ellipsis(tipTitle,18);
        var panel=Wtf.getCmp(openPanel.id+'detail'+openPanel.customParentModName+'Tab'+id);
        if(panel==null) {
            var colHeader=getColHeader(openPanel.EditorColumnArray);
            panel= new Wtf.AboutView({
                id : openPanel.id+'detail'+openPanel.customParentModName+'Tab'+id,
                closable:true,
                recid: id,
                id2:openPanel.id+'detail'+openPanel.customParentModName+'Tab'+id,
                cm : openPanel.EditorGrid.colModel,
                record : this.rec.data,
                layout:"fit",
                moduleName:openPanel.customParentModName,
                autoScroll:true,
                moduleid:2,
                mapid:mapid,
                moduleScope:openPanel,
                recname:this.recname,
                iconCls:openPanel.iconCls,
                fieldCols : fieldcols,
                fields:colHeader,
                values :FieldValues,
                customField:openPanel.spreadSheet.getCustomField(),
                customValues:openPanel.spreadSheet.getCustomValues(recData),
                grid:openPanel.EditorGrid,
                contactsPermission:openPanel.contactsPermission,
                Store:openPanel.EditorStore,
                title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+openPanel.customParentModName+"'>"+title+"</div>",
                selected:this.rec
            });
            openPanel.mainTab.add(panel);
        }
        openPanel.mainTab.setActiveTab(panel);
        openPanel.mainTab.doLayout();
    }else{
        WtfComMsgBox(16);
    }


}

Wtf.moduleLeadMappingInterface = function(config) {
    Wtf.apply(this, config);
    Wtf.moduleLeadMappingInterface.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.moduleLeadMappingInterface, Wtf.Window, {
iconCls : 'pwnd favwinIcon',
    width:700,
    height:520,
    modal:true,
    layout:"fit",
    id:'csvMappingInterface',
    closable:false,
    isModified: false,
    initComponent: function() {
        Wtf.moduleLeadMappingInterface.superclass.initComponent.call(this);
    },

    onRender: function(config){
        Wtf.moduleLeadMappingInterface.superclass.onRender.call(this, config);
        this.addEvents({
            'importfn':true
        });

        this.title="Map "+this.modName+" Headers";
        this.headerName=WtfGlobal.getLocaleText("crm."+this.modName.toUpperCase())+ " "+WtfGlobal.getLocaleText("crm.HEADERS");
        this.leadHead = WtfGlobal.getLocaleText("crm.masterconfig.leadMapping.leadheaders");//"Lead Headers";
        

        this.title=this.headerName+ " "+WtfGlobal.getLocaleText("crm.masterconfig.leadMappingwin.title");
        this.columnDs=new Wtf.data.JsonStore({
             fields:[{
                name: 'columnname'
            },{
                name:"isDefaultMapping"
            },
            {
                name:"leadfieldid"
            },
            {
                name:"leadfieldxtype"
            }]
        });
        this.quickSearchTF = new Wtf.KWLQuickSearchUseFilter({
            id : 'csvHeader'+this.id,
            width: 140,
            field : "columnname",
            emptyText:WtfGlobal.getLocaleText("crm.masterconfig.leadMappingwin.search")//"Search the Headers "
        });
        this.columnDs.loadData(this.leadHeaders);
        this.quickSearchTF.StorageChanged(this.columnDs);
        this.columnCm = new Wtf.grid.ColumnModel([
        {
            header: this.leadHead,
            dataIndex: "columnname",
            renderer:function(a,b,c){
                var qtip="";var style="";
                if(c.get("allownull")=="NO"){
                    style += "font-weight:bold;color:#500;";
                    qtip += "Allow Null False";
                }
                if(c.get("key")=="PRI"){
                    style += "font-weight:bold;color:#050;";
                    qtip = "Primary Key Column<br/>"+qtip;
                }
                return "<span wtf:qtip='"+qtip+"' style='cursor:pointer;"+style+"'>"+a+"</span>";
            }
        },{
                header: WtfGlobal.getLocaleText("crm.masterconfig.leadMappingwin.fieldtypeheader"),//"Field type",
                dataIndex: "leadfieldxtype",
            renderer:function(a,b,c){
               switch(a){
                    case "1":
                        return "Textfield";
                        break;
                    case "2":
                        return "Numberfield";
                        break;
                    case "3":
                        return "Datefield";
                        break;
                    case "4":
                        return "Combo";
                        break;
                    case "5":
                        return "Timefield";
                        break;
                    case "6":
                        return "Checkbox";
                        break;
                    case "7":
                        return "Multiselect Combo";
                        break;
                    case "8":
                        return "Ref. Combo";
                    case "9":
                        return "Auto Number";
                }
            }
            }
        ]);
        this.tableColumnGrid = new Wtf.grid.GridPanel({
            ddGroup:"mapColumn",
            enableDragDrop : true,
            store: this.columnDs,
            sm:new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            cm: this.columnCm,
            height:370,
            border : false,
            tbar:[this.quickSearchTF],
            loadMask : true,
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.common.selmodmsg")//"Select module to list columns"
            })
        });


        //Mapped Columns Grid
        this.mappedColsData="";
        this.mappedRecord = new Wtf.data.Record.create([
            {
                name: "columnname",
                type: 'string'
            },

            {
                name: "allownull",
                type: 'string'
            },

            {
                name: "key",
                type: 'string'
            },

            {
                name: "index"
            },
            {
                name:"isDefaultMapping"
            },
            {
                name:"leadfieldid"
            },{
                name:"leadfieldxtype"
            },
            {
                name: 'isMandatory'
            }
            ]);

        this.mappedColsDs = new Wtf.data.JsonStore({
            jsonData : this.mappedColsData,
            reader: new Wtf.data.JsonReader({
                root:"data"
            }, this.mappedRecord)

        });

        var mappedColsCm = new Wtf.grid.ColumnModel([{
            header: WtfGlobal.getLocaleText({key:"crm.masterconfig.leadMappingwin.mappedfield",params:[this.leadHead]}),//"Mapped "+leadHeaders,
            dataIndex: 'columnname'
        }]);

        this.mappedColsGrid= new Wtf.grid.GridPanel({
            ddGroup:"restoreColumn",
            enableDragDrop : true,
            sm:new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            store: this.mappedColsDs,
            cm: mappedColsCm,
            height:370,
            border : false,
            tbar:[{xtype:'panel',height:(Wtf.isIE)?8:10,border:false}],
            loadMask : true,
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.common.dragndropcolsmsg")//"Drag and Drop columns here"
            })
        });

        // CSV header from csv file Grid
        this.csvHeaderDs = new Wtf.data.JsonStore({
            fields: [{
                name:"header"
            },{
                name:"index"
            },{
                name:"isDefaultMapping"
            },
            {
                name:"modulefieldid"
            },
            {
                name:"modulefieldxtype"
            },
            {name: 'isMandatory'}]
        });

        this.quickSearchTF1 = new Wtf.KWLQuickSearchUseFilter({
            id : 'tableColumn'+this.id,
            width: 140,
            field : "header",
            emptyText:WtfGlobal.getLocaleText("crm.masterconfig.leadMappingwin.search")//"Search the Headers "
        });

        this.csvHeaderDs.loadData(this.moduleHeaders);
        this.quickSearchTF1.StorageChanged(this.csvHeaderDs);
        this.csvHeaderDs.on("load",function(){
            this.totalHeaders=this.csvHeaderDs.getCount();
        },this);

        var emptyGridText = "CSV Headers from given CSV file";
        var csvHeaderCm = new Wtf.grid.ColumnModel([{
            header: this.headerName,
            dataIndex: 'header',
            renderer:function(a,b,c){
                var qtip="";var style="";
                if(c.get("isMandatory")){
                    style += "font-weight:bold;color:#500;";
                    qtip += "Mandatory Field";
                }
                return "<span wtf:qtip='"+qtip+"' style='cursor:pointer;"+style+"'>"+a+"</span>";
            }
        },{
            header: WtfGlobal.getLocaleText("crm.masterconfig.leadMappingwin.fieldtypeheader"),//"Field type",
            dataIndex: "modulefieldxtype",
            scope:this,
            renderer:function(a,b,c){
                switch(a){
                    case "1":
                        return "Textfield";
                        break;
                    case "2":
                        return "Numberfield";
                        break;
                    case "3":
                        return "Datefield";
                        break;
                    case "4":
                        return "Combo";
                        break;
                    case "5":
                        return "Timefield";
                        break;
                    case "6":
                        return "Checkbox";
                        break;
                    case "7":
                        return "Multiselect Combo";
                        break;
                    case "8":
                        return "Ref. Combo";
                    case "9":
                        return "Auto Number";
                }
            }

            }]);
        this.csvHeaderGrid= new Wtf.grid.GridPanel({
            ddGroup:"mapHeader",
            enableDragDrop : true,
            sm:new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            height:370,
            store: this.csvHeaderDs,
            cm: csvHeaderCm,
            border : false,
            tbar:[this.quickSearchTF1],
            loadMask : true,
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:emptyGridText
            })
        });


        //Mapped CSV Header Grid
        this.mappedCsvheaders="";
        this.mappedCsvHeaderRecord = new Wtf.data.Record.create([
            {
                name:"header"
            },
            {
                name:"index"
            },
            {
                name:"isDefaultMapping"
            },
            {
                name:"modulefieldid"
            },
            {
                name:"modulefieldxtype"
            },
            {name: 'isMandatory'}
        ]);
        this.mappedCsvHeaderDs = new Wtf.data.JsonStore({
            jsonData : this.mappedCsvheaders,
            reader: new Wtf.data.JsonReader({
                root:"data"
            }, this.mappedCsvHeaderRecord)
        });

        //this.mappedCsvHeaderDs.loadData(this.mappedCsvheaders);
        var mappedCsvHeaderCm = new Wtf.grid.ColumnModel([{
            header: WtfGlobal.getLocaleText({key:"crm.masterconfig.leadMappingwin.mappedfield",params:[this.headerName]}),//"Mapped "+this.headerName,
            dataIndex: 'header'
        }]);
        this.mappedCsvHeaderGrid= new Wtf.grid.GridPanel({
            ddGroup:"restoreHeader",
            enableDragDrop : true,
            sm:new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            store: this.mappedCsvHeaderDs,
            cm: mappedCsvHeaderCm,
            height:370,
            tbar:[{xtype:'panel',height:(Wtf.isIE)?8:10,border:false}],
            border : false,
            loadMask : true,
            view:new Wtf.grid.GridView({
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.common.dragndropcolmsg")//"Drag and Drop Header here"
            })
        });

        var leadRecord;
        var moduleRecord;
        for(var i=0; i< this.mappedHeaders.length;i++){
                 leadRecord = new this.mappedRecord({
                    columnname: this.mappedHeaders[i].leadfieldname,
                    isDefaultMapping :this.mappedHeaders[i].isDefaultMapping,
                    leadfieldid:this.mappedHeaders[i].leadfieldid,
                    leadfieldxtype:this.mappedHeaders[i].leadfieldxtype
                });
                 moduleRecord = new this.mappedCsvHeaderRecord({
                    header: this.mappedHeaders[i].modulefieldname,
                    isDefaultMapping :this.mappedHeaders[i].isDefaultMapping,
                    modulefieldid:this.mappedHeaders[i].modulefieldid,
                    modulefieldxtype:this.mappedHeaders[i].modulefieldxtype
                });
                this.mappedCsvHeaderDs.add(moduleRecord);
                this.mappedColsDs.add(leadRecord);
        }

        this.add({
            border: false,
            layout : 'border',
            items :[{
                region: 'north',
                border:false,
                height:80,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                items:[{
                    xtype:"panel",
                    border:false,
                    height:70,
                    html:getTopHtml(WtfGlobal.getLocaleText("crm.masterconfig.leadMapping.tophtmlheader"),WtfGlobal.getLocaleText({key:"crm.masterconfig.leadMappingwin.tophtmlDesc",params:[this.headerName]}),"../../images/link2.jpg")
                    //html:getTopHtml("Map Headers","Drag-and-drop '"+headerName+"' fields to the 'Mapped Headers' list and subsequently Drag-and-drop corresponding 'Table Columns' field to the 'Mapped Columns' list.","../../images/link2.jpg")
                }]
            },{
                region: 'center',
                autoScroll: true,
                bodyStyle : 'background:white;font-size:10px;',
                border:false,
                layout:"column",
                items: [
                {
                    xtype:"panel",
                    columnWidth:.25,
                    border:false,
                    layout:"fit",
                    autoScroll:true,
                   // title:leadHeaders,
                    items:this.tableColumnGrid
                },{
                    xtype:"panel",
                    columnWidth:.25,
                    border:false,
                    layout:"fit",
                    autoScroll:true,
                  //  title:"Mapped "+leadHeaders,
                    items:this.mappedColsGrid
                },{
                    xtype:"panel",
                    columnWidth:.25,
                    border:false,
                    layout:"fit",
                    autoScroll:true,
                 //   title:(this.modName == 'Opportunity' ? 'Mapped Headers':"Mapped "+headerName),
                    items:this.mappedCsvHeaderGrid
                },{
                    xtype:"panel",
                    columnWidth:.25,
                    border:false,
                    layout:"fit",
                    autoScroll:true,
                 //   title:headerName,
                    items:this.csvHeaderGrid
                }
                ]
            }
            ],
            buttonAlign: 'right',
            buttons:[{
                text: WtfGlobal.getLocaleText("crm.SAVEnCLOSEBTN"),//'Save & close',
                handler: function(){
                    var isMapped=false;
                    if(this.mappedCsvHeaderDs.getCount()==0 && this.mappedColsDs.getCount()==0)
                        WtfComMsgBox(1000);
                    else {
                        if(this.csvHeaderDs.getCount()>0) {
                            for(var i=0; i<this.csvHeaderDs.getCount(); i++) {
                                if(this.csvHeaderDs.getAt(i).data.isMandatory) {
                                    isMapped=true;
                                    break;
                                }
                            }
                        }

                        var totalmappedHeaders = this.mappedCsvHeaderDs.getCount();
                        var totalMappedColumns = this.mappedColsDs.getCount();
                        if(totalmappedHeaders==totalMappedColumns){
                            this.generateJsonForXML();
                            //                            if(this.typeXLSFile){
                            //                                this.fireEvent('importfn',this.mappingJSON,this.index,this.moduleName,this.store,this.contactmapid,this.targetlistPagingLimit,this.scopeobj);
                            //                            }else {
                            //                                this.fireEvent('importfn',this.mappingJSON,this.delimiterType);
                            //                            }


                            if(isMapped){
                                Wtf.MessageBox.show({
                                    title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//'Confirm',
                                    msg:WtfGlobal.getLocaleText("crm.common.mandatorycolnotmappedmsg"),//"Mandatory columns are not mapped, are you sure you want to save the changes?",
                                    icon:Wtf.MessageBox.QUESTION,
                                    buttons:Wtf.MessageBox.YESNO,
                                    scope:this,
                                    fn:function(button){
                                        if(button=='yes')
                                        {
                                            this.submitMappHeaders();
                                        }else{
                                            return;
                                        }
                                    }
                                });
                            }else{
                                this.submitMappHeaders();
                            }

                        } else {
                            WtfComMsgBox([WtfGlobal.getLocaleText("crm.common.headermappingtitle"), WtfGlobal.getLocaleText("crm.common.selcolforheadermsg")], 0);
                        }
                    }
                            Wtf.getCmp('csvMappingInterface').close();
        },
                scope:this
            },{
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                handler: function(){
                if(this.isModified){
                     Wtf.MessageBox.show({
                    title:WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),//"Alert",
                    msg:WtfGlobal.getLocaleText("crm.common.continuewidoutsavingmsg"),
                    buttons:Wtf.MessageBox.YESNO,
                    animEl:'mb9',
                    fn:function(btn){
                        if(btn=="yes"){
                             this.close();
                        }
                    },
                    scope:this,
                    icon: Wtf.MessageBox.QUESTION
                });
                } else {
                    this.close();
                }

                },
                scope: this
            }]
        });

this.columnDs.on("add",this.changeFlag,this);
this.mappedCsvHeaderDs.on("add",this.changeFlag,this);
this.mappedColsDs.on("add",this.changeFlag,this);
this.csvHeaderDs.on("add",this.changeFlag,this);
        this.on("afterlayout",function(){

            function rowsDiff(store1,store2){
                return diff=store1.getCount()-store2.getCount();
            }

            function unMapRec(atIndex){
                var headerRec = mappedHeaderStore.getAt(atIndex);
                if(headerRec!==undefined){
                    mappedHeaderStore.remove(headerRec);
                    headerStore.add(headerRec);
                }

                var columnRec = mappedColumnStore.getAt(atIndex);
                if(columnRec!==undefined){
                    mappedColumnStore.remove(columnRec);
                    columnStore.add(columnRec);
                }
            }

            columnStore = this.columnDs;
            columnGrid = this.tableColumnGrid;

            mappedColumnStore = this.mappedColsDs;
            mappedColumGrid = this.mappedColsGrid;

            headerStore = this.csvHeaderDs;
            headerGrid = this.csvHeaderGrid;

            mappedHeaderStore = this.mappedCsvHeaderDs;
            mappedHeaderGrid = this.mappedCsvHeaderGrid;

            // Drag n drop [ Headers -> Mapped Headers ]
            DropTargetEl =  mappedHeaderGrid.getView().el.dom.childNodes[0].childNodes[1];
            DropTarget = new Wtf.dd.DropTarget(DropTargetEl, {
                ddGroup    : 'mapHeader',
                copy       : true,
                notifyDrop : function(ddSource, e, data){
                    function mapHeader(record, index, allItems) {

                        var mappedxtype=mappedColumnStore.data.items[mappedColumnStore. getCount()-1].data.leadfieldxtype;
                        var colxtype=record.data.modulefieldxtype;
                        var isMapped=true;
                        if(mappedxtype==1&& (colxtype==2 || colxtype==3 || colxtype==5)){
                            isMapped=false;
                        }else if(mappedxtype==2 && (colxtype==3 || colxtype==5 || colxtype==4 || colxtype==7  || colxtype==8)){
                            isMapped=false;
                        }
                        else if(mappedxtype==3 && (colxtype==2 ||colxtype==4 || colxtype==5 || colxtype==7  || colxtype==8)){
                            isMapped=false;
                        }else if(mappedxtype==5 && (colxtype==2 ||colxtype==4 ||colxtype==3 || colxtype==7  || colxtype==8)){
                            isMapped=false;
                        }else if((mappedxtype==4 || mappedxtype==7 || mappedxtype==8) && (colxtype==3 || colxtype==5 || colxtype==2)){
                            isMapped=false;
                        }

                        if(isMapped){
                            if(rowsDiff(mappedColumnStore,mappedHeaderStore)==1){
                                mappedHeaderStore.add(record);
                                ddSource.grid.store.remove(record);
                            }else{
                                WtfComMsgBox([WtfGlobal.getLocaleText("crm.common.headermappingtitle"), WtfGlobal.getLocaleText("crm.common.selheadermsg")], 0);
                            }
                        }  else{
                            WtfComMsgBox([WtfGlobal.getLocaleText("crm.common.headermappingtitle"), WtfGlobal.getLocaleText("crm.common.incompatibleheadermsg")], 0);
                        }
                    }
                    Wtf.each(ddSource.dragData.selections ,mapHeader);
                    return(true);
                }
            });

            // Drag n drop [ Mapped Headers -> Headers ]
            DropTargetEl =  headerGrid.getView().el.dom.childNodes[0].childNodes[1];
            DropTarget = new Wtf.dd.DropTarget(DropTargetEl, {
                ddGroup    : 'restoreHeader',
                copy       : true,
                notifyDrop : function(ddSource, e, data){
                    function restoreColumn(record, index, allItems) {
                        if(!record.get('isDefaultMapping')){
                            unMapRec(ddSource.grid.store.indexOf(record));
                        }else{
                            WtfComMsgBox([WtfGlobal.getLocaleText("crm.common.headermappingtitle"), WtfGlobal.getLocaleText("crm.common.headermappingfailuremsg")], 0);
                        }

                    }
                    Wtf.each(ddSource.dragData.selections ,restoreColumn);
                    return(true);
                }
            });

            // Drag n drop [ columns -> Mapped columns ]
            DropTargetEl =  mappedColumGrid.getView().el.dom.childNodes[0].childNodes[1];
            DropTarget = new Wtf.dd.DropTarget(DropTargetEl, {
                ddGroup    : 'mapColumn',
                copy       : true,
                notifyDrop : function(ddSource, e, data){
                    function mapColumn(record, index, allItems) {

                       if(rowsDiff(mappedHeaderStore,mappedColumnStore)==0){
                                mappedColumnStore.add(record);
                                ddSource.grid.store.remove(record);
                            }else{
                               WtfComMsgBox([WtfGlobal.getLocaleText("crm.common.headermappingtitle"),WtfGlobal.getLocaleText("crm.common.mapprevioushdrmsg")], 0);
                            }

                    }
                    Wtf.each(ddSource.dragData.selections ,mapColumn);
                    return(true);
                }
            });

            // Drag n drop [ Mapped columns -> columns ]
            DropTargetEl =  columnGrid.getView().el.dom.childNodes[0].childNodes[1];
            DropTarget = new Wtf.dd.DropTarget(DropTargetEl, {
                ddGroup    : 'restoreColumn',
                copy       : true,
                notifyDrop : function(ddSource, e, data){
                    function restoreColumn(record, index, allItems) {
                        if(!record.get('isDefaultMapping')){
                            unMapRec(ddSource.grid.store.indexOf(record));
                        }else{
                            WtfComMsgBox([WtfGlobal.getLocaleText("crm.common.headermappingtitle"), WtfGlobal.getLocaleText("crm.common.headermappingfailuremsg")], 0);
                        }
                    }
                    Wtf.each(ddSource.dragData.selections ,restoreColumn);
                    return(true);
                }
            });
        },this);
    },
submitMappHeaders : function(){

        Wtf.Ajax.requestEx({

            url: 'Common/CRMCommon/saveMappedheaders.do',
            params:{
                jsondata:this.mappingJSON,
                moduleName : this.modName
            }
        },this,
        function(res) {
            if(res.success){
                WtfComMsgBox([WtfGlobal.getLocaleText("crm.common.headermappingtitle"), WtfGlobal.getLocaleText("crm.common.headermappingsuccessmsg")], 0);
            }
        },
        function(res) {
            WtfComMsgBox(12,1);
        })

},
changeFlag:function(){
    this.isModified=true;
},
    generateJsonForXML : function(){
        this.mappingJSON = "";
        for(var i=0;i<this.mappedCsvHeaderDs.getCount();i++){
            if(!this.mappedCsvHeaderDs.getAt(i).get("isDefaultMapping")){
                this.mappingJSON+="{\"modulefieldid\":\""+this.mappedCsvHeaderDs.getAt(i).get("modulefieldid")+"\",";
                this.mappingJSON+="\"leadfieldid\":\""+this.mappedColsDs.getAt(i).get("leadfieldid")+"\"},";
            }
        }
        this.mappingJSON = this.mappingJSON.substr(0, this.mappingJSON.length-1);
        this.mappingJSON = "{\"root\":["+this.mappingJSON+"]}";
    }
});


Wtf.deleteFailInterface = function(config) {
    Wtf.apply(this, config);
    Wtf.deleteFailInterface.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.deleteFailInterface, Wtf.Window, {
    iconCls : 'pwnd favwinIcon',
    width:this.module=="emailMarketing"?750:650,
    height:470,
    modal:true,
    layout:"fit",
    closable:false,
    title:WtfGlobal.getLocaleText("crm.common.deletelogtitle"),//'Delete Log',

    onRender: function(config){
        Wtf.deleteFailInterface.superclass.onRender.call(this, config);
        var haeder = WtfGlobal.getLocaleText("crm.masterconfig.modulename.column");//"Module Name";
        var modulemsg = this.module;
        if(this.module=="emailMarketing"){
            haeder = WtfGlobal.getLocaleText("crm.campaigndetails.campconfname");//"Campaign Configuration Name";
            modulemsg = WtfGlobal.getLocaleText("crm.campaigndetails.header.emailtemp")//"Email Template"
       }
        this.colsDs=new Wtf.data.JsonStore({
            fields:[{
                name: 'name'
            },{
                name:"moduleName"
            }]
        });
        this.colsDs.loadData(this.failDelete);
        var colsCm = new Wtf.grid.ColumnModel([{
            header:WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),// "Name",
            width : 40,
            dataIndex: 'name',
            renderer : function(val) {
        	var tmp = Wtf.util.Format.htmlEncode(val);
                return "<div wtf:qtip=\""+tmp+"\"wtf:qtitle="+WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText")+">"+tmp+"</div>";
            }
        },{
            header: haeder,
            dataIndex: 'moduleName',
            renderer : function(val) {
        	var tmp = Wtf.util.Format.htmlEncode(val);
                return "<div wtf:qtip=\""+tmp+"\"wtf:qtitle="+WtfGlobal.getLocaleText("crm.masterconfig.modulename.column")+">"+tmp+"</div>";
            }
        }]);

        this.colsGrid= new Wtf.grid.GridPanel({
            sm:new Wtf.grid.RowSelectionModel({
                singleSelect:true
            }),
            store: this.colsDs,
            cm: colsCm,
            border : false,
            loadMask : true,
            view:new Wtf.grid.GridView({
                forceFit:true
            })
        });


        if(this.successDeleteCount > 0){
            this.successMsg = this.successDeleteCount +" "+modulemsg+(this.successDeleteCount == 1? "":"s")+" deleted successfully.";
        }else{
            this.successMsg = "No "+modulemsg+"s were deleted.";
        }
        if(this.failDelete.length > 1){
            this.failMsg = this.failDelete.length +" "+modulemsg+"s could not be deleted as they are being used elsewhere.";
        }else{
            this.failMsg = this.failDelete.length +" "+modulemsg+" could not be deleted as it is being used elsewhere.";
        }
        
        this.add({
            border: false,
            layout : 'border',
            items :[{
                region: 'north',
                border:false,
                height:85,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                items:[{
                    xtype:"panel",
                    border:false,
                    height:75,
                    html:getTopHtml(WtfGlobal.getLocaleText("crm.common.deletelogtitle"),"Some "+modulemsg+"s could not be deleted. Below is the list of "+modulemsg+"s which could not be deleted. ","../../images/trash_icon.jpg")
                }]
            },{
                region: 'center',
                border:false,
                layout : 'border',
                items : [{
                region: 'north',
                border:false,
                height:35,
                layout:"fit",
                bodyStyle : 'background:#f1f1f1;border-bottom:1px solid #bfbfbf;',
                items:[{
                    xtype:"panel",
                    border:false,
                    height:25,
                    html:" <div class='deleteLog' > <span class='deleteLog1' >" +this.successMsg+" </span><span>"+this.failMsg+"</span> </div>"
                }]},{
                    region: 'center',
                    autoScroll: true,
                    bodyStyle : 'background:white;font-size:10px;',
                    border:false,
                    items: [this.colsGrid]
                }]
                //items: [this.colsGrid]
            }
            ],
            buttonAlign: 'right',
            buttons:[{
                text: WtfGlobal.getLocaleText("crm.OK"),//'OK',
                handler: function(){
                    this.close();
                },
                scope: this
            }]
        });
    }
});

Wtf.showTestMailWindow = function(Grid,rowIndex,campid){
        this.reciepientMail = new Wtf.form.TextField({
            fieldLabel:WtfGlobal.getLocaleText("crm.common.recipientemail")+'*',//'Recipient Email* ',
            regex:Wtf.ValidateMailPatt,
            width:240,
            value:loginemail,
            allowBlank : false
        })
        var reciepientMailForm = new Wtf.form.FormPanel({
            region: "center",
            border: false,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:10px 10px 10px 30px;',
            items: [this.reciepientMail]
        });
        var win = new Wtf.Window({
            title: WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.sndtestmailBTN"),
            modal: true,
            height: 190,
            iconCls:"pwnd favwinIcon",
            width: 420,
            resizable: false,
            layout: "border",
            items: [{
                region: "north",
                bodyStyle: "background-color: white",
                border:false,
                height: 65,
                html: getTopHtml(WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.sndtestmailBTN"), WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.sndtestmailBTN"),"../../images/esetting.gif")
            }, reciepientMailForm],
            buttons: [{
                text: WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.sndtestmailBTN"),//"Send Test Email",
                scope: this,
                handler: function(){
                    if(reciepientMailForm.form.isValid()){
                        var reciepientMailId = this.reciepientMail.getValue();
                        var ldmsg=WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.sndmailloading");
                        Wtf.commonWaitMsgBox(ldmsg);//"Sending test mail...");
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.springBase+'emailMarketing/action/sendTestEmailMarketMail.do',
                            params:{
                                emailmarkid : Grid.store.getAt(rowIndex).data.id,
                                campid : campid,
                                flag : 12,
                                reciepientMailId:reciepientMailId
                            }},this,
                            function(){
                                Wtf.updateProgress();
                                ResponseAlert(400);
                                win.close();
                             },
                            function() {
                                Wtf.updateProgress();
                                ResponseAlert(401);
                            }
                        );
                    }
                }
            },{
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),// "Cancel",
                scope: this,
                handler: function(){
                    win.close();
                }
            }]
        });
        win.show();
 }

Wtf.emptyMandatoryFields = function(Grid,rowIndex){
    var rec = Grid.getStore().getAt(rowIndex);
    var colConfig = Grid.getColumnModel().config;
    var emptyFields="";
    for(var i=0 ; i< colConfig.length ; i++){
        if(colConfig[i].mandatory!=undefined && colConfig[i].mandatory==true){
            if(rec.get(colConfig[i].dataIndex)==""){
                emptyFields+=" "+colConfig[i].header.substring(0, colConfig[i].header.lastIndexOf("*")-1)+","
          }
        }
    }
    if(emptyFields.trim()!=""){
        var finalStr = emptyFields.substring(0, (emptyFields.length-1));
        var msg =WtfGlobal.getLocaleText({key:"crm.common.mandatorymsg",params:[finalStr]});//"<b> The following field(s) are mandatory and are required to be filled to make it a valid record.</b><br><br>"+finalStr+".";

        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),//'Alert',
            msg:msg,
            buttons:Wtf.MessageBox.OK,
            icon:Wtf.MessageBox.INFO,
            scope:this
        });
    }
    
    
}

Wtf.moveToLeadRec = function(recData,obj){
    var flag=0;
    this.leadPanel = new Wtf.quickadd({
            dashcomp:Wtf.moduleWidget.lead,
            configType:"Lead",
            callFrom : "MoveLead",
            compid:Wtf.move_to_lead_id,
            border: false,
            paramObj:{flag:20,auditEntry:1},
            url: Wtf.req.springBase+'Lead/action/saveLeads.do',
            actionCode:1,
            jsonstr:{leadid:'0',leadownerid:loginid,targetid:recData.targetid}

    })
    this.leadPanel.on("aftersave",function(){
        if(this.win!=null)
            this.win.close();
        var lgrid=Wtf.getCmp('LeadHomePanel');
        if(lgrid!=null)
        {
            lgrid.EditorStore.reload();
        }
        obj.bounceReportStore.load();
    },this);
    this.leadPanel.on("closeform",function(){
            if(flag!=0)
        this.win.setHeight(180+(this.leadPanel.objresponse.data.length>3?this.leadPanel.objresponse.data.length:3)*27);

        Wtf.getCmp(Wtf.move_to_lead_fname+Wtf.move_to_lead_id).setValue(recData.fname.trim());
        Wtf.getCmp(Wtf.move_to_lead_lname+Wtf.move_to_lead_id).setValue(recData.lname.trim());
        Wtf.getCmp(Wtf.move_to_lead_email+Wtf.move_to_lead_id).setValue(recData.email);
    },this);
    
    this.win=new Wtf.Window({
        height:240,
        width:400,
        id:Wtf.move_to_lead_win,
        iconCls: "pwnd favwinIcon",
        title:WtfGlobal.getLocaleText("crm.LEAD"),//"Lead",
        modal:true,
        shadow:true,
        buttonAlign:'right',
        buttons: [{
            text:WtfGlobal.getLocaleText("crm.SUBMITBTN"),//'Submit',
            scope:this,
            handler:function(){
        		if(Wtf.getCmp(Wtf.move_to_lead_email+Wtf.move_to_lead_id).isValid() && Wtf.getCmp(Wtf.move_to_lead_lname+Wtf.move_to_lead_id).isValid())
        			this.leadPanel.saveobj();
        		else
        			ResponseAlert(["Alert","Please enter valid data."]);
        			return;
            }
        },{
            text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
            scope:this,
            handler:function(){
                this.win.close()
            }
        }],
        layout : 'border',
        items :[{
            region : 'north',
            height : 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(WtfGlobal.getLocaleText("crm.editor.campaign.moveToleadBTN"), WtfGlobal.getLocaleText("crm.editor.campaign.moveToleadBTN"),"../../images/leads.gif")
        },{
            region : 'center',
            border : false,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 10px 10px 30px',
            autoScroll:true,
            items :[   this.leadPanel   ]
        }]

    });
    flag=1;
    this.win.show();
}
