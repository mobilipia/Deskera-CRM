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
Wtf.openCustomReportTab = function(config){
    Wtf.apply(this,config);
    Wtf.openCustomReportTab.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.openCustomReportTab, Wtf.Panel,{
    initComponent: function(config) {
        Wtf.openCustomReportTab.superclass.initComponent.call(this,config);
        Wtf.commonWaitMsgBox("Loading config...");
        Wtf.Ajax.requestEx({
            url: this.details.url,
            params: this.details.baseParams
        },
        this,
        function(res){
            Wtf.updateProgress();
            if(res.columns) {
                this.callGrid(res);
            }
        },
        function(res){
            Wtf.updateProgress();
        })
    },
    onRender: function(config) {
        Wtf.openCustomReportTab.superclass.onRender.call(this,config);
        var tabTitle = '<div id="view'+this.details.helpID+'">'+WtfGlobal.getLocaleText("crm.report.listview")+'</div>';
        this.home=new Wtf.Panel({
            layout:'fit',
            title:tabTitle,
            iconCls:"listViewTabIcon",
            border:false
        });
        InnerTab= new Wtf.TabPanel({
            activeTab: 0,
            id:this.id+'MainTabPanel',
            border:false,
            items:this.home
        });
        this.add(InnerTab);
    },
    filter: function() {
        var fromdate = this.fromdate.getValue();
        var todate = this.todate.getValue();
        if((fromdate != "" && todate != "" && this.filterCombo.getValue() != "")) {
            Wtf.filterBydate(fromdate.getTime(),todate.getTime(),this.ds,this);
        } else {
            ResponseAlert(902);
        }
    },

    exportfile : function(type,mode) {
        this.exportDate(type);
    },
    PrintPriview : function(type,mode) {
        this.exportDate(type);
    },

    exportDate : function (type) {
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
            if(this.filterCombo != undefined) {
                var comboName = "";
                var comboValue = this.filterCombo.getValue();
                var comboDisplayValue = this.filterCombo.selectedIndex != -1 ? this.filterCombo.store.data.items[this.filterCombo.selectedIndex].data.name:undefined;
            }
          
            var extraConfig = {
            		reportno : this.details.reportno,
            		report_categoty : this.details.baseParams.report_categoty
            };
            if(this.details.baseParams.filterSS){
            	extraConfig["filterSS"]=this.details.baseParams.filterSS;
            }
            
            if(this.details.baseParams.filterCol){
            	extraConfig["filterCol"]=this.details.baseParams.filterCol;
            }
            
            if(this.details.baseParams.detailFlag){
            	extraConfig["detailFlag"] = this.details.baseParams.detailFlag;
            }

            exportWithTemplate(this,type,this.details.name,frmdate,tdate,this.details.report,this.details.exportUrl,undefined,this.grid,undefined,undefined,undefined,comboName,comboValue,comboDisplayValue,undefined,Wtf.util.JSON.encode(extraConfig));
       }
    },
    callGrid:function(reportConf) {
        var columns = [];
        columns.push(new Wtf.grid.RowNumberer({allowIncreament:true,rowspan:1}));
        this.pluginArr = new Array();
        if(reportConf.summaryflag) {
            this.summary = new Wtf.ux.grid.GridSummary();
            this.pluginArr.push(this.summary);
        }
        if(reportConf.groupflag) {
            this.details.groupflag = reportConf.groupflag;
        }
        if(reportConf.groupcolumn) {
            this.groupColumn = reportConf.groupcolumn;
        }
        Wtf.each(reportConf.columns, function(column){
            if (column.renderer != undefined && column.renderer != ""){
                var strfunc=column.renderer;
                if(strfunc == "Currency") {
                    column.renderer = WtfGlobal.currencyRenderer;
                }else if(strfunc == "Date") {
                    column.renderer = WtfGlobal.onlyDateRendererTZ;
                }else if(strfunc == "Email") {
                    column.renderer = WtfGlobal.renderEmailTo;
                }else if (strfunc.indexOf("function") < 0) {
                    column.renderer = this[strfunc].createDelegate(this);
                }else{
                    strfunc=strfunc.replace(/\n/g,"").replace(/&#43;/g,"+");
                    column.renderer =eval('('+strfunc+ ')') ;
                }
            }
            if (column.groupcolumn){
                column.renderer =this['groupLinkRenderer'].createDelegate(this);
            }
            if (reportConf.summaryflag && column.summaryType != undefined && column.summaryType != ''){
                column.summaryType= column.summaryType;
                column.summaryRenderer = this['total'].createDelegate(this);
            }
            columns.push(column);
        }, this)

        this.details.searchbyemptytext = reportConf.quickSearchText;
        this.reportdesc = reportConf.reportdesc;
        this.cm = new Wtf.grid.ColumnModel(columns);
        this.printR = printButtonR(this,this.details.title,this.details.helpID);
        this.exp = exportButton(this,this.details.title,this.details.helpID);
        this.topToolB = Wtf.customReportTopToolbar(this,this.details.title,this.details.reportno);
        this.filterStore.loadData(reportConf.datecolumns);
        this.storeBaseParams = this.details.baseParams;
        this.storeBaseParams.dataflag = true;
        this.ds = new Wtf.data.Store({
            url: this.details.url,
            baseParams: this.storeBaseParams,
            reader: new Wtf.data.KwlJsonReader({
                totalProperty: 'totalCount',
                root: "data"
            })
        });
        this.ds.on('beforeload', function() {
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
            this.ds.baseParams=this.storeBaseParams;
        },this);
        this.ds.on("load",function(){
            WtfGlobal.setAjaxReqTimeout();
            Wtf.updateProgress();
            if(Wtf.isIE){
                this.setEmptyTextForDateFieldInIE();
            }
        }, this);
        this.ds.on("loadexception",function(){
            WtfGlobal.resetAjaxReqTimeout();
            Wtf.updateProgress();
        },this);

        this.ds.load({
            params:{
                start:0,
                limit:25
            }
        });
        this.grid=new Wtf.grid.GridPanel({
            scope:this,
            store:this.ds,
            view:new Wtf.ux.KWLGridView({
                forceFit:true
            }),
            enableColumnHide: false,
            cm: this.cm,
            border:false,
            trackMouseOver: true,
            stripeRows: true,
            layout:'fit',
//            loadMask: {
//                msg: 'Loading Records...'
//            },
            tbar:this.topToolB,
            bbar:new Wtf.PagingSearchToolbar({
                pageSize: 25,
                searchField:this.quickSearchTF,
                id: "pagingtoolbar" + this.id,
                store: this.ds,
                displayInfo: true,
                fromDate:this.fromdate,
                toDate:this.todate,
                emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),
                plugins:this.pP = new Wtf.common.pPageSize({
                    id: "pPageSize_" + this.id
                }),
                items:[this.exp,this.printR]
            }),
            plugins: this.pluginArr
        });
        this.home.add(this.grid);
        this.home.doLayout();
        this.ds.on("datachanged",function(){
            this.quickSearchTF.setPage(this.pP.combo.value);
        },this);
        this.quickSearchTF.StorageChanged(this.ds);
        this.quickSearchTF.on('SearchComplete', function() {
            this.grid.getView().refresh();
        }, this);

        this.grid.on("cellclick",this.reportGridCellClick,this);
    },
    reportGridCellClick : function(Grid,rowIndex,columnIndex, e){
        var event = e ;
        if(event.getTarget("a[class='customReportGroup']")) {
            var record = this.grid.getSelectionModel().getSelected();
            var val = record.get(this.groupColumn+"_id");
            if(val && val != "") {
                openCustomReportTab(this.details.reportno, this.details.title, this.details.rcategory, this.reportdesc, true, this.groupColumn, record.get(this.groupColumn+"_id"), record.get(this.groupColumn));
            } else {
                openCustomReportTab(this.details.reportno, this.details.title, this.details.rcategory, this.reportdesc, true, this.groupColumn, "(Blank)", "Blank");
            }            
        }
    },
    total : function(val) {
        return val ;
    },
    groupLinkRenderer : function(val,metadata,record,row,col,store) {
        if(val && val != "") {
            return "<a href=# class='customReportGroup' style='color: #083772; text-decoration: none;' wtf:qtip='Click to view details.'>"+val+"</a>" ;
        } else {
            return "<a href=# class='customReportGroup' style='color: #083772; text-decoration: none;' wtf:qtip='Click to view details.'>(Blank)</a>" ;
        }
    },
    showReportHelp : function(){
        var dat=[];
        var data = {
            id:"0",
            title:"Custom Report - "+this.details.title,
            desc:this.reportdesc,
            compid:"",
            name:"welcome",
            modeid: this.details.reportno
        }
        dat[0] = data;
        _helpContent = dat;
        var we = new Wtf.taskDetail();
        we.welcomeHelp();
    },   
    setEmptyTextForDateFieldInIE : function (){
        this.fromdate.getEl().dom.value=Wtf.emptyTextForDateField;
        this.todate.getEl().dom.value=Wtf.emptyTextForDateField;
    },
    rst: function() {
        var fromdate = this.fromdate.getValue();
        var todate = this.todate.getValue();
        if((fromdate != "" && todate != "") || (this.quickSearchTF.getValue() != "")) {
            this.ds.load({
                params:{
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
        this.filterCombo.setValue("");
    }
//    chart:function(obj) {
//        var fromdate=this.fromdate.getValue();
//        var todate=this.todate.getValue();
//        if(this.details.helpID== 56 || this.details.helpID== 57){  // for pipeline report's chart
//            fromdate = Wtf.formatDate(fromdate,0);
//            todate = Wtf.formatDate(todate,0);
//        }
//        if(this.details.helpID== 22 || this.details.helpID == 29) {
//            var xmlpath=this.details.xmlpath +"&year="+this.yearCombo.getValue();
//            var xmlpath2=this.details.xmlpath2 +"&year="+this.yearCombo.getValue();
//            var pieDataUrl = this.details.pieDataUrl + "?year="+this.yearCombo.getValue();
//            var barDataUrl = this.details.barDataUrl + "?year="+this.yearCombo.getValue();
//            globalChart(this.details.chartid,this.details.id1,this.details.swf,pieDataUrl,this.details.mainid,xmlpath,this.details.id2,this.details.swf2,barDataUrl,xmlpath2,undefined,this.details.helpID,fromdate,todate);
//        } else {
//            globalChart(this.details.chartid,this.details.id1,this.details.swf,this.details.pieDataUrl,this.details.mainid,this.details.xmlpath,this.details.id2,this.details.swf2,this.details.barDataUrl,this.details.xmlpath2,undefined,this.details.helpID,fromdate,todate,obj);
//        }
//    }
});
