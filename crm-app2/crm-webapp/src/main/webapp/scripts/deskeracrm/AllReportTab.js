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
Wtf.AllReportTab = function(config){
    Wtf.apply(this,config);
    this.printR = printButtonR(this,this.details.title,this.details.helpID);
    this.exp = exportButton(this,this.details.title,this.details.helpID);
//    this.bbar=[this.exp,this.printR];
    Wtf.AllReportTab.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.AllReportTab, Wtf.Panel,{
    onRender: function(config) {
        Wtf.AllReportTab.superclass.onRender.call(this,config);
        this.topToolB = Wtf.topToolBar(this,this.details.title,this.details.helpID);
        var tabTitle = '<div id="view'+this.details.helpID+'">'+WtfGlobal.getLocaleText("crm.report.listview")+'</div>';
        if(this.details.helpID == 56){
            tabTitle = '<div id="view'+this.details.helpID+'">'+Wtf.util.Format.ellipsis("Opportunities Pipeline Report",18)+'</div>';
        }else if(this.details.helpID == 74){
            var tip = "Hybrid Report";
            var subtitle = Wtf.util.Format.ellipsis(tip,18);
            tabTitle = "<span wtf:qtip=\'"+tip+"\'>"+subtitle+"</span>";
        }
        this.home=new Wtf.Panel({
            layout:'fit',
            title:tabTitle,
            tbar:this.topToolB,
            iconCls:"listViewTabIcon",
            border:false
        });
        InnerTab= new Wtf.TabPanel({
            activeTab: 0,
            id:this.id+'MainTabPanel',
            border:false,
            items:this.home
        });
        if(this.details.helpID == 56 && !WtfGlobal.EnableDisable(Wtf.UPerm.OpportunityReport, Wtf.Perm.OpportunityReport.oppreport_i)) {
            this.topToolB_oppReport = Wtf.topToolBar(this,"All Opportunities Pipeline Chart",58);
            this.home2=new Wtf.Panel({
                layout:'fit',
                tbar:this.topToolB_oppReport,
                title:'<div id="view58">'+Wtf.util.Format.ellipsis("All Opportunities Pipeline",18)+'</div>',
                iconCls:"listViewTabIcon",
                border:false
            });
            InnerTab.add(this.home2);
        }else if(this.details.helpID == 74 && !WtfGlobal.EnableDisable(Wtf.UPerm.OpportunityReport, Wtf.Perm.OpportunityReport.oppreport_k)) {
//            this.topToolB_oppReport = Wtf.topToolBar(this,"Opportunities By Region (Sales Funnel)",74);
            var tooltip = "Reports By Region";
            var title = Wtf.util.Format.ellipsis(tooltip,18);
            this.home2=new Wtf.Panel({
                layout:'fit',
//                tbar:this.topToolB_oppReport,
                title:"<span wtf:qtip=\'"+tooltip+"\'>"+title+"</span>",
                iconCls:"listViewTabIcon",
                border:false
            });
            InnerTab.add(this.home2);
        }
        this.add(InnerTab);
        if(this.details.helpID == 61){ // For View All Activities List
            
            if(!Wtf.StoreMgr.containsKey("taskstatus") || !Wtf.StoreMgr.containsKey("casepriority") || !Wtf.StoreMgr.containsKey("tasktype")){
                Wtf.statusStore.on('load',function(){
                    this.callGrid1();
                },this);
                chkpriorityload()
                chktaskstatusload();
                chktasktypeload()

            } else{
                this.callGrid1();
            }
           
        } else{
            this.callGrid();
        }
        
        if((this.details.helpID == 56 && !WtfGlobal.EnableDisable(Wtf.UPerm.OpportunityReport, Wtf.Perm.OpportunityReport.oppreport_i))
            || (this.details.helpID == 74 && !WtfGlobal.EnableDisable(Wtf.UPerm.OpportunityReport, Wtf.Perm.OpportunityReport.oppreport_k))) {
            this.callGrid2();
        }
    },
    filter: function() {
        if(this.filterCombo!=undefined){
            var val =this.filterCombo.getValue();
            this.filterCombo.setValue(val);
        }
        var fromdate=(this.fromdate.getValue()).getTime?(this.fromdate.getValue()).getTime():"";
        var todate=(this.todate.getValue()).getTime?(this.todate.getValue()).getTime():"";
        Wtf.filterBydate(fromdate,todate,this.ds,this);
    },
    rst: function() {
        if(this.details.helpID == 56 || this.details.helpID == 57) {
//            var firstDate = new Date().format(new Date().format('Y')+'-' +new Date().format('m')+'-01');
//            var currentDate = new Date().format('Y-m-d');
//            this.todate.setValue(currentDate);
//            this.fromdate.setValue(firstDate);
//            this.ds.load({
//                params:{
//                    frm: this.fromdate.getValue(),
//                    to: this.todate.getValue(),
//                    year:this.yearCombo.value,
//                    start:0,
//                    limit:this.grid.getBottomToolbar().pageSize
//                }
//            });
//            if(this.quickSearchTF){
//                this.quickSearchTF.reset();
//            }
        	if(this.quickSearchTF){
        		this.quickSearchTF.reset();
        	}
        	var currentDate = new Date().format('Y-m-d');
        	this.todate.setValue(currentDate);
        	this.fromdate.setValue("");
        	this.ds.load({
               params:{
                   start:0,
                   limit:this.grid.getBottomToolbar().pageSize
               }});
        } else if(this.details.helpID == 60 || this.details.helpID == 55 ) { // Completed goal
            var filtercombo = this.filterCombo!=undefined?this.filterCombo.getValue():""
            	this.userCombo.setValue(loginid);
            this.ds.reload({
                params:{
                    userCombo:this.userCombo.getValue(),
                    filterCombo:filtercombo,
                    start:0,
                    limit:this.grid.getBottomToolbar().pageSize
                }
            });
            if(this.quickSearchTF){
                this.quickSearchTF.reset();
            }
            this.fromdate.setValue("");
            this.todate.setValue("");
        }else if(this.details.helpID != 22 && this.details.helpID != 29) {
            var refresh = false;
            if(this.details.helpID == 10 || this.details.helpID == 17 || this.details.helpID == 12 || this.details.helpID == 27 || this.details.helpID == 28 || this.details.helpID == 61 || this.details.helpID == 13 || this.details.helpID == 14|| this.details.helpID == 19 || this.details.helpID == 37) {
                if(this.filterCombo.getValue() != ""){
                    refresh = true;
                }
                this.filterCombo.setValue(undefined);
            }
            if(this.details.helpID == 25){
                if(this.details.baseParams.filterCombo!=undefined){
                    this.filterCombo.setValue(this.details.baseParams.filterCombo);
                }else{
                    this.filterCombo.setValue("Campaign");
                    this.ds.load({
                        params:{
                            start:0,
                            limit:this.grid.getBottomToolbar().pageSize
                        }
                     });
                }
            }
            var fromdate = this.fromdate.getValue();
            var todate = this.todate.getValue();
            if((fromdate != "" && todate != "") || (this.quickSearchTF.getValue() != "" ) || refresh) {
                    this.ds.load({
                        params:{
                            start:0,
                            limit:this.grid.getBottomToolbar().pageSize
                        }
                     });
            }
            this.fromdate.setValue("");
            this.todate.setValue("");
            if(this.quickSearchTF){
                this.quickSearchTF.reset();
            }
        } else {
            if((fromdate != "" && todate != "") || (this.quickSearchTF.getValue() != "")) {
                this.ds.load({
                    params:{
                        year:this.yearCombo.value,
                        start:0,
                        limit:this.grid.getBottomToolbar().pageSize
                    }
                });
            }
            if(this.quickSearchTF){
                this.quickSearchTF.reset();
            }
            this.fromdate.setValue("");
            this.todate.setValue("");
        }
        
    },   
    exportfile : function(type,mode) {
        var proceed = true;
        var msg;
        var invalidDate = false;
        var invalidRange = false;
        var tdate=this.todate.getValue();
        var frmdate=this.fromdate.getValue();
        var fromDate =frmdate.getTime?frmdate.getTime():"";
        var toDate = tdate.getTime?tdate.add(Date.DAY,1).add(Date.SECOND,-1).getTime():"";
        
        if(fromDate == "" || toDate == "" ) {
            msg =15;
            frmdate="";
            tdate="";
            invalidDate = true;
        }else if(fromDate > toDate ) {
            msg = 16;
            invalidDate = true;
            invalidRange = true;
            this.todate.setValue("");
            proceed = false;
        }

        if(invalidDate && invalidRange) {
                proceed = false;
        }
        
        if(!proceed) {
            ResponseAlert(msg);
        }else {
            if(!invalidDate) {
                frmdate =fromDate;
                tdate =toDate;
            }
            this.reportFlag = 1;//Used in case of new pdf template creation
            if(mode == 58) {
                exportWithTemplate(this,type,"allopenOppPipeline",frmdate,tdate,this.details.report,this.details.Grid2ExportUrl,undefined,this.grid2);
            } else {
                if(this.filterCombo != undefined) {
                    var comboName = "";
                    var comboValue = this.filterCombo.getValue();
                    var comboDisplayValue = this.filterCombo.selectedIndex != -1 ? this.filterCombo.store.data.items[this.filterCombo.selectedIndex].data.name:undefined;
                    if(this.details.helpID == 10) {
                        comboName = "Industry";
                    } else if (this.details.helpID == 17 || this.details.helpID == 12) {
                        comboName = "Lead Source";
                    } else if (this.details.helpID == 27 ||this.details.helpID == 28) {
                        comboName = "Case Priority";
                    } else if (this.details.helpID == 61) {
                        comboName = "Activity Priority";
                    }
                }
                exportWithTemplate(this,type,this.details.name,frmdate,tdate,this.details.report,this.details.exportUrl,undefined,this.grid,undefined,undefined,undefined,comboName,comboValue,comboDisplayValue);
            }
        }
    }, 
	PrintPriview : function(type,mode) {
        var frmdate=this.fromdate.getValue();
        frmdate = frmdate.getTime?frmdate.getTime():"";
        var tdate=this.todate.getValue();
        tdate = tdate.getTime?tdate.add(Date.DAY,1).add(Date.SECOND,-1).getTime():"";
        if(mode == 58) {
            exportWithTemplate(this,type,"allopenOppPipeline",frmdate,tdate,this.details.report,this.details.Grid2ExportUrl,undefined,this.grid2);
        } else {
            if(this.filterCombo != undefined) {
                var comboName = "";
                var comboValue = this.filterCombo.getValue();
                var comboDisplayValue = this.filterCombo.selectedIndex != -1 ? this.filterCombo.store.data.items[this.filterCombo.selectedIndex].data.name:undefined;
                if(this.details.helpID == 10) {
                    comboName = "Industry";
                } else if (this.details.helpID == 17 ||this.details.helpID == 12) {
                    comboName = "Lead Source";
                } else if ( this.details.helpID == 27 ||this.details.helpID == 28) {
                    comboName = "Case Priority";
                } else if (this.details.helpID == 61) {
                    comboName = "Activity Priority";
                }
            }
            exportWithTemplate(this,type,this.details.name,frmdate,tdate,this.details.report,this.details.exportUrl,undefined,this.grid,undefined,undefined,undefined,comboName,comboValue,comboDisplayValue);
        }
    },
    callGrid:function() {
        this.ds = new Wtf.data.Store({
            url: this.details.url,
            baseParams: this.details.baseParams,
            reader: new Wtf.data.KwlJsonReader({
                totalProperty: 'totalCount',
                root: "data"
            })
        });
        this.ds.on('beforeload', function() {
            if(this.details.helpID == 59 || this.details.helpID == 10) {
                this.ds.baseParams.reportId=this.details.helpID;
            }
            if(this.details.helpID == 10 || this.details.helpID == 17 || this.details.helpID == 12 || this.details.helpID == 27 || this.details.helpID == 28 || this.details.helpID == 61 || this.details.helpID == 25 || this.details.helpID == 13 || this.details.helpID == 14|| this.details.helpID == 19 || this.details.helpID == 37) {
                this.ds.baseParams.filterCombo=this.filterCombo.getValue();
            }
            if(this.details.helpID == 60) {
                this.ds.baseParams.filterCombo=this.filterCombo.getValue();
                this.ds.baseParams.userCombo=this.userCombo.getValue()==""?loginid:this.userCombo.getValue();
            }
            if(this.details.helpID == 55) {
                this.ds.baseParams.userCombo=this.userCombo.getValue()==""?loginid:this.userCombo.getValue();
            }
            this.ds.baseParams=this.details.baseParams;
        },this);

        this.ds.on("load",function(){
            var columns = [];
            var qkSrcText="";
            columns.push(new Wtf.grid.RowNumberer({allowIncreament:true,rowspan:1}));
            Wtf.each(this.ds.reader.jsonData.columns, function(column){
                if(column.renderer)
                    column.renderer = eval('('+ column.renderer +')');
                if(typeof column.editor == 'string')
                    column.editor = eval('('+ column.editor +')');
                if(column.qucikSearchText){
                    qkSrcText=column.qucikSearchText
                }
                columns.push(column);
            })
            this.grid.getColumnModel().setConfig(columns);
            Wtf.updateProgress();
            WtfGlobal.setEmptyTextForQuickSearchField(this.quickSearchTF,this.details.searchbyemptytext+qkSrcText);
            if(this.quickSearchTF_2)
            	this.quickSearchTF_2.emptyText=this.details.searchbyemptytext+qkSrcText;

            if(Wtf.isIE){
                this.setEmptyTextForDateFieldInIE();
            }
        },this);
        this.ds.on("loadexception",function(){
            Wtf.updateProgress();
        },this);
        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
        this.ds.load();
        this.grid=new Wtf.grid.GridPanel({
            scope:this,
            store:this.ds,
            view:new Wtf.ux.KWLGridView({
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.goalsettings.ermsg"))//"No record to display.")
            }),
            enableColumnHide: false,
            columns:[],
            border:false,
            trackMouseOver: true,
            stripeRows: true,
            layout:'fit',
            loadMask: {
                msg: 'Loading Records...'
            },
            bbar:new Wtf.PagingSearchToolbar({
                pageSize: 25,
                searchField:this.quickSearchTF,
                id: "pagingtoolbar" + this.id,
                store: this.ds,
                displayInfo: true,
                fromDate:this.fromdate,
                toDate:this.todate,
                yearCombo:this.yearCombo,
                monthCombo:this.monthCombo,
                emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),
                plugins:this.pP = new Wtf.common.pPageSize({
                    id: "pPageSize_" + this.id
                }),
                items:[this.exp,this.printR]
            })
        });
        this.home.add(this.grid);
        this.home.doLayout();
        this.ds.on('load',function(){
            this.quickSearchTF.StorageChanged(this.ds);
            this.quickSearchTF.on('SearchComplete', function() {
                this.grid.getView().refresh();
            }, this);
        },this);
        this.ds.on("datachanged",function(){
            this.quickSearchTF.setPage(this.pP.combo.value);
        },this);
        if(this.details.baseParams.filterCombo!=undefined){
            this.filterCombo.setValue(this.details.baseParams.filterCombo);
        }
    },
    callGrid1:function() {
        this.ds = new Wtf.data.Store({
            url: this.details.url,
            baseParams: this.details.baseParams,
            reader: new Wtf.data.KwlJsonReader({
                totalProperty: 'totalCount',
                root: "data"
            })
        });
        this.ds.on('beforeload', function() {
            this.ds.baseParams=this.details.baseParams;
        },this);
        this.ds.on("load",function(){
            var columns = [];
            columns.push(new Wtf.grid.RowNumberer({allowIncreament:true,rowspan:1}));
            Wtf.each(this.ds.reader.jsonData.columns, function(column){
                if(column.renderer)
                    column.renderer = eval('('+ column.renderer +')');
                if(typeof column.editor== 'string')
                    column.editor = eval('('+ column.editor +')');
                columns.push(column);
            })
            this.grid.getColumnModel().setConfig(columns);
            Wtf.updateProgress();
        },this);
        this.ds.on("loadexception",function(){
            Wtf.updateProgress();
        },this);
        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
        this.ds.load();
        this.grid=new Wtf.grid.EditorGridPanel({
            scope:this,
            store:this.ds,
            view:new Wtf.ux.KWLGridView({
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.goalsettings.ermsg"))//)"No record to display.")
            }),
            enableColumnHide: false,
            columns:[],
            border:false,
            trackMouseOver: true,
            stripeRows: true,
            layout:'fit',
            loadMask: {
                msg: 'Loading Records...'
            },
            bbar:new Wtf.PagingSearchToolbar({
                pageSize: 25,
                searchField:this.quickSearchTF,
                id: "pagingtoolbar" + this.id,
                store: this.ds,
                displayInfo: true,
                fromDate:this.fromdate,
                toDate:this.todate,
                yearCombo:this.yearCombo,
                monthCombo:this.monthCombo,
                emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),
                plugins:this.pP = new Wtf.common.pPageSize({
                    id: "pPageSize_" + this.id
                }),
                items:[this.exp,this.printR]
            })
        });
        this.home.add(this.grid);
        this.home.doLayout();
        this.ds.on('load',function(){
            this.quickSearchTF.StorageChanged(this.ds);
            this.quickSearchTF.on('SearchComplete', function() {
                this.grid.getView().refresh();
            }, this);
            if(Wtf.isIE){
                this.setEmptyTextForDateFieldInIE();
            }
            
        },this);
        this.ds.on("datachanged",function(){
            this.quickSearchTF.setPage(this.pP.combo.value);
        },this);
        if(this.details.baseParams.filterCombo!=undefined){
            this.filterCombo.setValue(this.details.baseParams.filterCombo);
        }
        this.grid.on("validateedit",function(e){
             if(typeof e.value=="string" ){
                 e.value = WtfGlobal.HTMLStripper(e.value);
             }	
        },this);
        this.grid.on("afteredit",function(e){
            this.saveData(e);
        },this);
    },
    setEmptyTextForDateFieldInIE : function (){
        this.fromdate.getEl().dom.value=Wtf.emptyTextForDateField;
        this.todate.getEl().dom.value=Wtf.emptyTextForDateField;
    },

    saveData : function(e){
        var rec = e.record;
        var recdata = rec.data;
        
        var subject = recdata.subject;
        var statusid = recdata.statusid;
        var priorityid = recdata.priorityid;
        var typeid = recdata.typeid;
        var activityid = recdata.activityid;
        var relatedto = recdata.relatedto;
        var relatedtonameid = recdata.relatednameid;
        var phone = WtfGlobal.HTMLStripper(recdata.phone);
        
//        if(subject.trim()==""){
//           return;
//        }
        var field = "";
        var oldValue = WtfGlobal.HTMLStripper(e.originalValue);
        var newValue = WtfGlobal.HTMLStripper(e.value);
        if(e.field=="subject"){
            field = "Subject";
        } else if(e.field=="statusid"){
            var oldInd = Wtf.statusStore.find("id",oldValue);
            if(oldInd > -1){
                var oldrc = Wtf.statusStore.getAt(oldInd);
                oldValue = oldrc.data.name;
            }

            var newInd = Wtf.statusStore.find("id",newValue);
            if(newInd > -1){
                var newrc = Wtf.statusStore.getAt(newInd);
                newValue = newrc.data.name;
            }
            
            field = "Status";
        } else if(e.field=="priorityid"){
            var oldPriority = Wtf.cpriorityStore.find("id",oldValue);
            if(oldPriority > -1){
                var oldPriorityRec = Wtf.cpriorityStore.getAt(oldPriority);
                oldValue = oldPriorityRec.data.name;
            }

            var newPriority = Wtf.cpriorityStore.find("id",newValue);
            if(newPriority > -1){
                var newPriorityRec = Wtf.cpriorityStore.getAt(newPriority);
                newValue = newPriorityRec.data.name;
            }
            
            field = "Priority";
        }else if(e.field=="typeid"){
            var oldType = Wtf.typeStore.find("id",oldValue);
            if(oldType > -1){
                var oldTypeRec = Wtf.typeStore.getAt(oldType);
                oldValue = oldTypeRec.data.name;
            }

            var newType = Wtf.typeStore.find("id",newValue);
            if(newType > -1){
                var newTypeRec = Wtf.typeStore.getAt(newType);
                newValue = newTypeRec.data.name;
            }

            field = "Type";
        } else if(e.field=="startdat"){
            field = "Start Date";
        } else if(e.field=="enddat"){
            field = "End Date";
        }else if(e.field=="starttime"){
            field = "Start Time";
        } else if(e.field=="endtime"){
            field = "End Time";
        } else if(e.field=="phone"){
            e.record.set('phone',newValue);
        }
        if(typeof e.value=="string" ){
           var val = WtfGlobal.HTMLStripper(e.value);
           recdata[e.field]=val;
        }
//        if(e.field=="startdat" || e.field=="enddat") {
//            if(e.field=="enddat" ){
//                if(recdata.startdate > recdata.enddate) {
//                    rec.set('startdat',recdata.enddate);
//                }
//            }
//            if(e.field=="startdat" ){
//                if(recdata.startdate > recdata.enddate) {
//                    rec.set('enddat',recdata.startdate);
//                }
//            }
//        }
//        if(recdata.startdate!="" && recdata.enddate!=""){
//            var strttime=recdata.starttime;
//            var endtime=recdata.endtime;
//            if(logintimeformat==2){
//              strttime = WtfGlobal.convertValueTimeFormat(strttime);
//              endtime = WtfGlobal.convertValueTimeFormat(endtime);
//            }
//            var st=new Date(new Date().toDateString()+' '+strttime);
//            var et=new Date(new Date().toDateString()+' '+endtime);
//            if(recdata.startdate.format('d-m-y')==recdata.enddate.format('d-m-y') && st.getTime()>et.getTime()){
//                if(field=="starttime" ){
//                    rec.set('endtime',recdata.starttime);
//                }
//                if(field=="endtime" ){
//                    rec.set('starttime',recdata.endtime);
//                }
//                recdata.endtime=recdata.starttime;
//            }
//        }
        var startdate = recdata.startdate;
        var enddate = recdata.enddate;
        var startTime=recdata.starttime;
        var endTime=recdata.endtime;
        
        var auditstr = field+" '"+oldValue+"' updated to '"+newValue+"' for ";
        if(oldValue==""){
            auditstr = field+" '"+newValue+"' added for ";
        }
//        var temp=new Date().clearTime().getTime();
//        var st=Date.parseDate(recdata.starttime,WtfGlobal.getLoginUserTimeFormat()).getTime()-temp;
//        var et=Date.parseDate(recdata.endtime,WtfGlobal.getLoginUserTimeFormat()).getTime()-temp;

//        var jsondata="";
//        jsondata+='{"activityid":"' +activityid + '",';
//        jsondata+='"subject":"' +subject + '",';
//        jsondata+='"statusid":"' +statusid + '",';
//        jsondata+='"priorityid":"' + priorityid+ '",';
//        jsondata+='"typeid":"' + typeid +  '",';
//        jsondata+='"phone":"' + phone +  '",';
////        jsondata+='"startdate":"' + (recdata.startdate.getTime?recdata.startdate.getTime()+st:new Date().getTime()+st)+ '",';
////        jsondata+='"enddate":"' + + (recdata.enddate.getTime?recdata.enddate.getTime()+et:new Date().getTime()+et)+ '",';
////        jsondata+='"starttime":"' + startTime + '",';
////        jsondata+='"endtime":"' +endTime + '",';
//        jsondata+='"relatedtoid":"' +relatedto + '",';
//        jsondata+='"relatedtonameid":"' +relatedtonameid + '",';
//        jsondata+='"auditstr":"' +auditstr + '",';
//        jsondata+= '},';
//        var trmLen = jsondata.length - 1;
 //       var finalStr = jsondata.substr(0,trmLen);
        var keyArray =["activityid","subject","statusid","priorityid","typeid","phone","relatedtoid","relatedtonameid","auditstr"];
        var valArray =[activityid,subject,statusid,priorityid,typeid,phone,relatedto,relatedtonameid,auditstr]

        var jsonData= WtfGlobal.JSONBuilder(keyArray, valArray);

        Wtf.Ajax.requestEx({

            url: Wtf.req.springBase+'Activity/action/saveActivity.do',
            params:{
                jsondata:jsonData
            }
        },
        this,
        function(res)
        {
            if(res.ID) {
                Wtf.refreshDashboardWidget(Wtf.crmmodule.activity);
            }
        },
        function(res)
        {
            WtfComMsgBox(202,1);
        }
        )
    },
	callGrid2:function() {
        this.ds2 = new Wtf.data.Store({
            url: this.details.Grid2Url,
            reader: new Wtf.data.KwlJsonReader({
                totalProperty: 'totalCount',
                root: "data"
            })
        });
        this.ds2.on("load",function(){
            var columns2 = [];
            columns2.push(new Wtf.grid.RowNumberer({allowIncreament:true,rowspan:1}));
            Wtf.each(this.ds2.reader.jsonData.columns, function(column){
                columns2.push(column);
            })
            this.grid2.getColumnModel().setConfig(columns2);
            Wtf.updateProgress();
        },this);
        this.ds2.on("loadexception",function(){
            Wtf.updateProgress();
        },this);
        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
        this.ds2.load();
        this.grid2=new Wtf.grid.GridPanel({
            scope:this,
            store:this.ds2,
            view:new Wtf.ux.KWLGridView({
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.goalsettings.ermsg"))//"No record to display.")
            }),
            enableColumnHide: false,
            columns:[],
            border:false,
            trackMouseOver: true,
            stripeRows: true,
            layout:'fit',
            loadMask: {
                msg: 'Loading Records...'
            },
            bbar:new Wtf.PagingSearchToolbar({
                pageSize: 25,
                searchField:(this.details.helpID == 56)?this.quickSearchTF_2:"",
                id: "pagingtoolbar2" + this.id,
                store: this.ds2,
                displayInfo: true,
                fromDate:this.fromdate,
                toDate:this.todate,
                yearCombo:this.yearCombo,
                monthCombo:this.monthCombo,
                emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),
                plugins:this.pP_2 = new Wtf.common.pPageSize({
                    id: "pPageSize_2" + this.id
                })
            })
        });
        this.home2.add(this.grid2);
        this.home2.on("render", function(){
        	this.ds2.load();
        },this);
        this.home2.doLayout();
        if(this.details.helpID == 56) {
            this.ds2.on('load',function(){
                    this.quickSearchTF_2.StorageChanged(this.ds2);
                    this.quickSearchTF_2.on('SearchComplete', function() {
                        this.grid2.getView().refresh();
                    }, this);
            },this);
            this.grid2.on("render",function(){
                this.ds2.on("datachanged",function(){
                    this.quickSearchTF_2.setPage(this.pP_2.combo.value);
                },this);
            },this);
        }
    },
    chart:function(obj) {
        var fromdate=this.fromdate.getValue();
        var todate=this.todate.getValue();
        if(this.details.helpID== 56 || this.details.helpID== 57){  // for pipeline report's chart
            fromdate =fromdate.getTime?fromdate.getTime():"";
            todate = todate.getTime?todate.getTime()+ 86400000-1:"";
        }
        if(this.details.helpID== 22 || this.details.helpID == 29) {
            var xmlpath=this.details.xmlpath +"&year="+this.yearCombo.getValue();
            var xmlpath2=this.details.xmlpath2 +"&year="+this.yearCombo.getValue();
            var pieDataUrl = this.details.pieDataUrl + "?year="+this.yearCombo.getValue();
            var barDataUrl = this.details.barDataUrl + "?year="+this.yearCombo.getValue();
            globalChart(this.details.chartid,this.details.id1,this.details.swf,pieDataUrl,this.details.mainid,xmlpath,this.details.id2,this.details.swf2,barDataUrl,xmlpath2,undefined,this.details.helpID,fromdate,todate);
        } else if (this.details.helpID== 13) {

            var mainpanid = this.details.mainid;

             this.barChartButton = new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("crm.report.barchartbtn"),//,
                scope:this,
                tooltip:'Get the graphical bar view. ',
                iconCls:'barchartIcon',
                handler:function(){

                    var dataurl = this.details.barDataUrl;
                    var xmlpath = "Common/ChartXmlSetting/getBarChartSetting.do?title=Opportunities by Stage&unit=&#8360";
                    var swfbar = this.details.swf2;
                    clickChartView("bar",mainpanid+"center_panel",dataurl,swfbar,xmlpath,fromdate,todate);
                    xmlpath = "Common/ChartXmlSetting/getBarChartSetting.do?title=Sales Amount by Stage&unit=&#8360";
                    dataurl = "crm/Opportunity/opportunityReport/opportunityByStageRevenueBarChart.do?comboname=Opportunity Stage"
                    clickChartView("bar",mainpanid+"east_panel",dataurl,swfbar,xmlpath,fromdate,todate);
               }
            });

            this.pieChartButton =new Wtf.Toolbar.Button({
                text:WtfGlobal.getLocaleText("crm.report.piechartbtn"),//,
                scope:this,
                tooltip:'Get the graphical pie view. ',
                iconCls:'piechartIcon',
                handler:function(){

                    var dataurl = this.details.pieDataUrl;
                    var xmlpath = this.details.xmlpath;
                    clickChartView("pie",mainpanid+"center_panel",dataurl,this.details.swf,xmlpath,fromdate,todate);
                   
                    dataurl="crm/Opportunity/opportunityReport/opportunityByStageRevenuePieChart.do?comboname=Opportunity Stage"
                    xmlpath="Common/ChartXmlSetting/getPieChartSetting.do?title=Sales Amount by Stage";
                    clickChartView("pie",mainpanid+"east_panel",dataurl,this.details.swf,xmlpath,fromdate,todate);
                    
                }
            });
            var reportPanel = new Wtf.Panel({
                    border : false,
                    title : WtfGlobal.getLocaleText("crm.report.chartview"),//"Chart View",
                    autoScroll:true,
                    iconCls:"pwndCRM piechartIcon",
                    bodyStyle: 'background:white',
                    layout:'column',
                    tbar:[this.pieChartButton,this.barChartButton],
                    closable: true,
                    items:[new Wtf.Panel({
                        id:mainpanid+"center_panel",
                        columnWidth:1,
                        layout:"fit",
                        border : true,
                        height:500
                     }),
                    new Wtf.Panel({
                    	id:mainpanid+"east_panel", 
                        border : true,
                        layout:"fit",
                        columnWidth:1,
                        height:500
                    })]
            });
            var dataurl = this.details.pieDataUrl;
            var xmlpath = this.details.xmlpath;
            showChart(mainpanid+"center_panel",dataurl,this.details.swf,xmlpath,fromdate,todate, undefined, this.details.helpID);

            dataurl="crm/Opportunity/opportunityReport/opportunityByStageRevenuePieChart.do?comboname=Opportunity Stage"
            xmlpath="Common/ChartXmlSetting/getPieChartSetting.do?title=Sales Amount by Stage";

            showChart(mainpanid+"east_panel",dataurl,this.details.swf,xmlpath,fromdate,todate, undefined, this.details.helpID);

            Wtf.getCmp(mainpanid).add(reportPanel);
            Wtf.getCmp(mainpanid).setActiveTab(reportPanel);
            Wtf.getCmp(mainpanid).doLayout();
           
        } else {
            globalChart(this.details.chartid,this.details.id1,this.details.swf,this.details.pieDataUrl,this.details.mainid,this.details.xmlpath,this.details.id2,this.details.swf2,this.details.barDataUrl,this.details.xmlpath2,undefined,this.details.helpID,fromdate,todate,obj);
        }
    }
});
