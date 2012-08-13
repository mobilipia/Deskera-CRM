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
Wtf.myGoals= function(config){
    Wtf.myGoals.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.myGoals, Wtf.Panel, {
    initComponent: function() {
        Wtf.myGoals.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        Wtf.myGoals.superclass.onRender.call(this, config);

        this.record = Wtf.data.Record.create([
       {
            name:'gname'
        },
        {
          name:'gid'
        },
        {
            name:'gdescription'
        },
        {
          name:'gassignedby'
        },

        {
            name:'gwth'
        },

        {
            name:'gcontext'
        },

        {
            name:'gpriority'
        },

        {
            name:'pastgoals'
        },
        {
            name:'gstartdate',
            dateFormat:'time',
            type:'date'
        },

        {
            name:"genddate",
            dateFormat:'time',
            type:'date'
        },
        {
            name:"createdon",
            dateFormat:'time',
            type:'date'
        },
        {
            name:'targeted'

        },
        {
            name:'relatedto'

        },

        {
            name:"achieved"

        }

        ]);
        this.reader= new Wtf.data.KwlJsonReader({
                root: 'data',
                 totalProperty:'count'
            },
            this.record
            );

        this.ds = new Wtf.data.Store({
//            url: Wtf.req.base + 'crm.jsp',
           url:Wtf.req.springBase+"common/HRMSIntegration/loginEmployeeGoals.do",
           remoteSort:true,
           baseParams: {
                flag: 825,
                to:new Date().clearTime().getTime()
            },
           reader:this.reader
        });
        
        this.ds.load({
            params:{
                start:0,
                limit:15
            }
        });

        this.sm= new Wtf.grid.RowSelectionModel({
            singleSelect:true
        });
        this.cm = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
        {
            header: WtfGlobal.getLocaleText("crm.goals.header.goaltype"),//"Goal Type",
            dbname:'relatedto',
            dataIndex: 'relatedto',
            renderer:function(val){
                        return WtfGlobal.goalTypeRenderer(val);
                     }
        },
        {
            header:WtfGlobal.getLocaleText("crm.goals.header.target"),// "Target",
            width: 100,
            sortable: true,
            align:'right',
            dbname:'targeted',
            dataIndex: 'targeted',
            renderer:function(value, cell, row, rowIndex, colIndex, ds){
                        return WtfGlobal.goalStatusRenderer(value, row);
                      }

        },
        {
            header: WtfGlobal.getLocaleText("crm.goals.header.achieved"),//"Achieved ",
            width: 150,
            align:'right',
            dataIndex: 'achieved',
            renderer:function(value, cell, row, rowIndex, colIndex, ds){
                        return WtfGlobal.goalStatusRenderer(value, row);
                     }

        },
        {
            header: WtfGlobal.getLocaleText("crm.goals.header.fromdate"),//"From Date ",
            width: 100,
            sortable: true,
            dbname:'gstartdate',
            dataIndex: 'gstartdate',
            renderer:WtfGlobal.onlyDateRendererTZ
        },
        {
            header:WtfGlobal.getLocaleText("crm.goals.header.todate"),// "To Date ",
            width: 100,
            sortable: true,
            dbname:'genddate',
            dataIndex: 'genddate',
            renderer:WtfGlobal.onlyDateRendererTZ
        },
        {
            header: WtfGlobal.getLocaleText("crm.goals.header.assignedby"),//"Assigned By ",
            width: 100,
            sortable: true,
            dbname:'gassignedby',
            dataIndex: 'gassignedby',
            renderer : function(val){
                        return "<div wtf:qtip=\""+val+"\"wtf:qtitle='Assigned By'>"+val+"</div>";
            }
        },{
            header:WtfGlobal.getLocaleText("crm.goals.header.createdon"),//"Goal Creation Date",
            sortable: true,
            dataIndex: 'createdon',
            readOnly:true,
            renderer:WtfGlobal.onlyDateRendererTZ
        }]);
        this.goalStatus = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.goals.goalstatusBTN"),//"Goal Status",
            scope:this,
            disabled:true,
            tooltip: {
                text:WtfGlobal.getLocaleText("crm.goals.goalstatusBTN.ttip")// "Please select a goal to view its status."
            },
            iconCls:'pwndCRM goalStatus',
            handler:function(){
                    this.goalStatusWindow();

            }
        });
        this.fromPeriod = new Wtf.form.DateField({
            emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),// --Select Date--",
            width: 130,
            fieldLabel:"From",
            readOnly:true,
            value:WtfGlobal.getLocaleText("crm.fromdate.label"),// "From",
            scope:this
        });

        this.toPeriod = new Wtf.form.DateField({
            emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),//" -- Select Date --",
            width: 130,
            readOnly:true,
            value: "To",
            scope:this
        });

        this.fil = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.FILTERBUTTON"),//"Filter",
            scope:this,
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.FILTERBUTTON.ttip")//"Choose a date range using 'From' and 'To' fields to filter records created in the specified time duration."
            },
            iconCls:'pwnd addfilter',
            handler:function(){
                this.ds.baseParams.viewAll =false;
//                var frm = WtfGlobal.convertToOnlyDate(this.fromPeriod.getValue());
//                var to = WtfGlobal.convertToOnlyDate(this.toPeriod.getValue());
                if(checkDates(this.fromPeriod,this.toPeriod)) {
                    this.ds.baseParams.frm = this.fromPeriod.getValue().clearTime().getTime();
                    this.ds.baseParams.to = this.toPeriod.getValue().add(Date.DAY,1).add(Date.SECOND,-1).getTime();
                    this.ds.baseParams.relatedto=this.searchByType.getValue();
                    this.loadStore();
                }
            }
        });
        this.viewAll = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.goals.viewallBTN"),//"View All",
            scope:this,
            enableToggle : true,
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.goals.viewallBTN.ttip")//"Click to view all goals."
            },
           iconCls:'pwndCRM showgrp'
        },this);
        this.viewAll.on('toggle',function(a,b){
            if(b){
                    this.ds.baseParams.viewAll =true;
                    this.ds.baseParams.relatedto="";
                    this.fromPeriod.setValue("");
                    this.toPeriod.setValue("");
                    this.searchByType.reset();
                    this.loadStore();
            }else{
                this.ds.baseParams.viewAll =false;
            }
        },this);
        
        var permData = [];
        var arr=[];

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

        var GoalTypeStore = new Wtf.data.SimpleStore({
            fields: ['id','name'],
            data : permData
        });
        
        
        this.searchByType=new Wtf.form.ComboBox({
        	emptyText:'Please select type',
        	store:GoalTypeStore,
        	triggerAction:'all',
            forceSelection:true,
            mode: 'local',
            valueField:'id',
            displayField: 'name',
            selectOnFocus:true,
            width:250
        });
        
        this.reset = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.RESETBUTTON"),//"Reset",
            scope:this,
            iconCls:'pwndCRM reset',
            tooltip: {
                text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.resetBTN.ttip")//'Click to remove any filter settings and view all records.'
            },
            handler:function(){
                this.fromPeriod.setValue("");
                this.toPeriod.setValue("");
                this.searchByType.reset();
                this.ds.baseParams.frm = "";
                this.ds.baseParams.to = "";
                this.ds.baseParams.viewAll = false;
                this.ds.baseParams.relatedto="";
                this.viewAll.toggle(false);
                this.loadStore();
            }

        });
        
        this.pg = new Wtf.PagingSearchToolbar({
            pageSize: 15,
            border : false,
            id : "paggintoolbar"+this.id,
            searchField: this.quickSearchTF,
            store: this.ds,
            displayInfo: true,
            width:1330,
            plugins : this.pPageSizeObj = new Wtf.common.pPageSize({
                id : "pPageSize_"+this.id
            })
        });
        
        this.myGoalsgrid=new Wtf.grid.GridPanel({
            cm:this.cm,
            store:this.ds,
            sm:this.sm,
            border:false,
            layout:'fit',
            loadMask:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.goals.emptygrid.watermark"))//"No goal assigned till now")
            },
            tbar : ['Search By Goal Type',this.searchByType,'-',this.goalStatus,'-',WtfGlobal.getLocaleText("crm.fromdate.label"),this.fromPeriod,'-',WtfGlobal.getLocaleText("crm.todate.label"),this.toPeriod,'-',this.fil,'-',this.viewAll,'-',this.reset],
            bbar : [this.pg],
            clicksToEdit :1,
            displayInfo:true
        });

        
//If you want to enable button ,if only one record selected ,otherwise disable
        this.MembergridPanel = new Wtf.common.KWLListPanel({
            id: "membergridpanel" + this.id,
            title: WtfGlobal.getLocaleText("crm.goals.title"),//'List of active and future goals.',
            autoLoad: false,
            autoScroll:true,
            paging: false,
            layout: 'fit',
            items: [this.myGoalsgrid]
        });
        this.add(this.MembergridPanel);
        this.ds.on('load', this.colorGridRow, this);
        this.myGoalsgrid.on('sortchange',this.colorGridRow, this);
        this.myGoalsgrid.on('rowclick',function(){
            if(this.myGoalsgrid.getSelectionModel().getCount()==1){
                this.goalStatus.enable();
                this.goalStatus.setTooltip(WtfGlobal.getLocaleText("crm.goals.enabledgoalstatusBTN.ttip"));//"Click to view status of selected goal.");
            }else if (this.myGoalsgrid.getSelectionModel().getCount()>1){
                this.goalStatus.disable();
                this.goalStatus.setTooltip(WtfGlobal.getLocaleText("crm.goals.goalstatusBTN.ttip"));//"Please select only one goal to view its status.");
            }
        },this);
        this.searchByType.on('select',this.searchOnSelect,this);

   },
   
   searchOnSelect:function(){
       this.ds.baseParams.relatedto = this.searchByType.getValue();
       this.loadStore();
    },
   
    colorGridRow: function(){
        this.goalStatus.disable();
        this.goalStatus.setTooltip(WtfGlobal.getLocaleText("crm.goals.goalstatusBTN.ttip"));//"Please select a goal to view its status.");
        for(var i=0 ; i< this.ds.data.length;i++){
            if(this.ds.data.items[i].data.pastgoals==1){
                if(this.myGoalsgrid.view.getRow(i).className.indexOf("greenrow") == -1)
                    this.myGoalsgrid.view.getRow(i).className += " greenrow";
            }else if(this.ds.data.items[i].data.pastgoals==0){
                if(this.myGoalsgrid.view.getRow(i).className.indexOf("redrow") == -1)
                    this.myGoalsgrid.view.getRow(i).className += " redrow";
            }
        }
    },
    add1:function(){

    },

    loadStore : function() {
        this.ds.load({
            params:{
                start:0,
                limit:this.pg.initialConfig.plugins.combo.value
            }
        });
    },
    
    goalStatusWindow:function(){
        var recData = this.myGoalsgrid.getSelectionModel().getSelected().data;
        var goaltype='<div class="mygoalstatus">';
        if(recData.relatedto==1){
            goaltype+=Wtf.goaltype.nooflead;
        } else if(recData.relatedto==2){
            goaltype+=Wtf.goaltype.leadrevenue;
        } else if(recData.relatedto==3){
            goaltype+=Wtf.goaltype.noofaccount;
        } else if(recData.relatedto==4){
            goaltype+=Wtf.goaltype.accountrevenue;
        } else if(recData.relatedto==5){
            goaltype+=Wtf.goaltype.noofopportunity;
        } else if(recData.relatedto==6){
            goaltype+=Wtf.goaltype.opprevenue;
        } else {
            goaltype = "";
        }
        if(goaltype.length>0) {
            goaltype+='</div>';
        }
        var target =recData.targeted;
        var status = '<span style=\'color:blue !important;\'>Active</span>';

        var diff = WtfGlobal.getDaysDiff(recData.genddate, new Date());

        var futurediff = WtfGlobal.getDaysDiff(recData.gstartdate, new Date());
        
        var achieved = recData.achieved;
        if(achieved=="" || achieved=="0"){
            achieved =0;
        }
        var remaining = target-achieved;
        if(diff< 0){
            diff ="Due date passed"
            if(remaining > 0){
                status = '<span style=\'color:red !important;\'>Failed</span>';
            }else {
                status = '<span style=\'color:green !important;\'>Completed</span>';;
            }
        } else {
            if(futurediff >0){
                status = "Future Dated";
            }
            diff+= " day(s)";
        }
        if(remaining < 0){
            remaining=0;
        }
        if(recData.relatedto==2 || recData.relatedto==4 || recData.relatedto==6){
            achieved = '<div class="mygoalstatus">'+WtfGlobal.getCurrencySymbolWithValue(achieved)+'</div>';
            target = '<div class="mygoalstatus">'+WtfGlobal.getCurrencySymbolWithValue(target)+'</div>';
            remaining = '<div class="mygoalstatus">'+WtfGlobal.getCurrencySymbolWithValue(remaining)+'</div>';
            
        }else{
            achieved = '<div class="mygoalstatus">'+achieved+'</div>';
            target = '<div class="mygoalstatus">'+target+'</div>';
            remaining = '<div class="mygoalstatus">'+remaining+'</div>';
        }
        diff = '<div class="mygoalstatus">'+diff+'</div>';
        status = '<div class="mygoalstatus">'+status+'</div>';
        
        var html = "<table class='mygoaltable'>";
        var htmlstyle = "<tr><td style='font-size:12px;font-style:bold;float:left;margin:0px 0px 0px 30px;'>";
        var html1="</b> :</td><td class='mygoalTD'>";
        html += htmlstyle+"<b>Goal Type"+html1+goaltype+" </td></tr>";
        html += htmlstyle+"<b>Target"+html1+target+" </td></tr>";
        html += htmlstyle+"<b>Achieved"+html1+achieved+" </td></tr>";
        html += htmlstyle+"<b>Remaining"+html1+remaining+" </td></tr>";
        html += htmlstyle+"<b>Due in"+html1+diff+"   </td></tr>";
        html += htmlstyle+"<b>Status"+html1+status+" </td></tr>";
        html += "</table>";
        this.goalStatusWin = new Wtf.Window({
        height:410,
        width:520,
        autoScroll:true,
        iconCls: "pwnd favwinIcon",
        title:WtfGlobal.getLocaleText("crm.goals.goalstatuswin.tophtml.title"),//"Goal Status",
        modal:true,
        resizable:false,
        buttonAlign:'right',
        buttons: [{
            text: WtfGlobal.getLocaleText("crm.CLOSEBUTTON"),//'Close',
            scope:this,
            handler:function(){
                this.goalStatusWin.close();
            }
        }],
        layout : 'border',
        items :[{
            region : 'north',
            height : 75,
            border : false,
            bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
            html: getTopHtml(WtfGlobal.getLocaleText("crm.goals.goalstatuswin.tophtml.title"),WtfGlobal.getLocaleText("crm.goals.goalstatuswin.tophtml.detail"),'../../images/assign-goal.jpg')
        },{
            region : 'center',
            border : false,
            bodyStyle : 'background:#f1f1f1;font-size:10px;padding:20px 20px 10px 20px',
            height:250,
            autoScroll:true,
            html: html
        }]
    });
    this.goalStatusWin.show();
    

    }
});



