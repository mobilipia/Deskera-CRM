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
Wtf.perticularemployeegoals=function(config){

    Wtf.perticularemployeegoals.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.perticularemployeegoals,Wtf.Panel,{
    initComponent:function(config){
        Wtf.perticularemployeegoals.superclass.initComponent.call(this,config);
        var permUpdate=true;
        var permDelete=true;
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.GoalSettings, Wtf.Perm.GoalSettings.manage)) {
            permUpdate=false;
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.GoalSettings, Wtf.Perm.GoalSettings.del)) {
                permDelete=false;
            }
        }

        this.sm= new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });

        this.goalRecord=Wtf.data.Record.create([
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
            name:'gwth'
        },

        {
            name:'gcontext'
        },

        {
            name:'gpriority'
        },
        {
            name:'relatedto'
        },
        {
            name:'targeted'
        },
        {
            name:'empname'
        },
        {
            name:'empdetails'
        },
        {
            name:'achieved'
        },
        {
            name:'percentageachieved'
        },
        {
            name:'empid'
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
            name:"gcomment"
        }
        ]);


        this.goalReader = new Wtf.data.KwlJsonReader({
            root:"data",
            totalProperty:"count"
        },this.goalRecord);


        this.goalstore= new Wtf.data.Store({
            url:Wtf.req.springBase+"common/HRMSIntegration/employeesGoalFinal.do",
            reader:this.goalReader,
            baseParams:{
                flag:821,
                userid:this.empid,
                to:new Date().clearTime().getTime()
            },
            sortInfo: {
            field: 'empdetails',
            direction: "DESC"
        }
        });



        this.text1=new Wtf.form.TextField({
            name:'goalname',
            allowBlank:false,
            maxLength:255
        });
        this.text2=new Wtf.form.TextField({
            name:'goaldescription',
            allowBlank:false,
            maxLength:255
        });
        this.text3=new Wtf.form.TextField({
            name:'goalcomment',
            maxLength:255

        });
        this.text4=new Wtf.form.TextField({
            name:'targeted',
            maxLength:255

        });

        this.fromdate=new Wtf.form.DateField({
            name:'from',
            width:200,
            allowBlank:false,
            format:'Y-m-d'
        });
        this.todate=new Wtf.form.DateField({
            name:'to',
            width:200,
            allowBlank:false,
            format:'Y-m-d'
        });
        this.addbutton=new Wtf.Button({
            text:WtfGlobal.getLocaleText("crm.goalsettings.toptoolbar.assigngoalsBTN"),//'Assign Goals',
            tooltip:WtfGlobal.getLocaleText("crm.goalsettings.toptoolbar.assigngoalsBTN.ttip"),//"Add a new goal to a particular employee and mention the weightage, start & end date, priority and context.",
            iconCls:"pwnd addIcon",
            minWidth:70,
            disabled:true,
            scope:this,
            handler:this.insertgoal
        });

        this.cm1=new Wtf.grid.ColumnModel([
            {
                header: '<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goalsettings.header.empname")+'>'+WtfGlobal.getLocaleText("crm.goalsettings.header.empname")+'</div>',
                dataIndex: 'empdetails',
                hidden:true
           },
            {
                header: '<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goals.header.goaltype")+'>'+WtfGlobal.getLocaleText("crm.goals.header.goaltype")+'</div>',
                dataIndex: 'relatedto',
                renderer:function(val){
                            return WtfGlobal.goalTypeRenderer(val);
                        }
            },
            {
                header: '<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goals.header.target")+'>'+WtfGlobal.getLocaleText("crm.goals.header.target")+'</div>',
                sortable: true,
                align:'right',
                dataIndex: 'targeted',
                renderer:function(value, cell, row, rowIndex, colIndex, ds){
                            return WtfGlobal.goalStatusRenderer(value, row);
                        }

            },{

                header: '<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goals.header.achieved")+'>'+WtfGlobal.getLocaleText("crm.goals.header.achieved")+'</div>',
                align:'right',
                dataIndex: 'achieved',
                renderer:function(value, cell, row, rowIndex, colIndex, ds){
                            return WtfGlobal.goalStatusRenderer(value, row);
                        }
            },{
                header: '<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goals.header.percentageachieved")+'>'+WtfGlobal.getLocaleText("crm.goals.header.percentageachieved")+'</div>',
                sortable: true,
                align:'right',
                dataIndex: 'percentageachieved',
                renderer:function(val){
                            return WtfGlobal.goalPercentageAchivedRenderer(val);
                        }
            },
            {
                header: '<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goals.header.fromdate")+'>'+WtfGlobal.getLocaleText("crm.goals.header.fromdate")+'</div>',
                sortable: true,
                dataIndex: 'gstartdate',
                renderer:WtfGlobal.onlyDateRendererTZ
            },{
                header: '<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goals.header.todate")+'>'+WtfGlobal.getLocaleText("crm.goals.header.todate")+'</div>',
                sortable: true,
                dataIndex: 'genddate',
                renderer:WtfGlobal.onlyDateRendererTZ
            },{
                header: '<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goals.header.createdon")+'>'+WtfGlobal.getLocaleText("crm.goals.header.createdon")+'</div>',
                sortable: true,
                dataIndex: 'createdon',
                renderer:WtfGlobal.onlyDateRendererTZ
            },{
                header:'<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goals.header.update")+'>'+WtfGlobal.getLocaleText("crm.goals.header.update")+'</div>',
                dataIndex:'update',
                width:60,
                fixed:true,
                hidden:permUpdate,
                renderer : function(val, cell, row, rowIndex, colIndex, ds) {
                               var target = row.data.targeted;
                               if(target!="" || target=="0")
                                return '<div class=\'pwnd editGoalIcon\' > </div>';
                }
            },{
                header:'<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goals.header.remove")+'>'+WtfGlobal.getLocaleText("crm.goals.header.remove")+'</div>',
                dataIndex:'remove',
                width:60,
                fixed:true,
                hidden:permDelete,
                renderer : function(val, cell, row, rowIndex, colIndex, ds) {
                               var target = row.data.targeted;
                               if(target!="" || target=="0")
                                return '<div class=\'pwnd deleteButton\' > </div>';
                }
            },{
                header:'<div wtf:qtip='+WtfGlobal.getLocaleText("crm.goals.header.details")+'>'+WtfGlobal.getLocaleText("crm.goals.header.details")+'</div>',
                dataIndex:'history',
                width:50,
                renderer : function(val, cell, row, rowIndex, colIndex, ds) {
                               var relatedto = row.data.relatedto;
                               if(relatedto==2)
                                    return '<div class=\'pwndCRM historyGoalIcon\' > </div>';
                                else
                                    return;
                            }
            }]);

        this.fromPeriod = new Wtf.form.DateField({
            emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),//" -- Select Date --",
            width: 130,
            value: "From",
            scope:this
        });

        this.toPeriod = new Wtf.form.DateField({
            emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),//" -- Select Date --",
            width: 130,
            value: "To",
            scope:this
        });

        this.fil = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.FILTERBUTTON"),//"Filter",
            scope:this,
            disabled:true,
            tooltip: {
                text:WtfGlobal.getLocaleText("crm.FILTERBUTTON.ttip")// "Choose a date range using 'From' and 'To' fields to filter records created in the specified time duration."
            },
            iconCls:'pwnd addfilter',
            handler:function(){
//                var frm = WtfGlobal.convertToOnlyDate(this.fromPeriod.getValue());
//                var to = WtfGlobal.convertToOnlyDate(this.toPeriod.getValue());
                if(checkDates(this.fromPeriod,this.toPeriod)) {
                    this.goalstore.baseParams.frm = this.fromPeriod.getValue().clearTime().getTime();
                    this.goalstore.baseParams.to = this.toPeriod.getValue().add(Date.DAY,1).add(Date.SECOND,-1).getTime();
                    var sm =this.selectionModel;
                    this.goalstore.baseParams.viewAll = false;
                    if(sm.getSelections().length==1){
                                this.addbutton.enable();
                                var rec = sm.getSelected();
                                this.goalstore.baseParams.userid = rec.get('userid');
                                this.goalstore.load({
                                     params : {start : 0,limit : this.pP.combo.value}
                                });
                    }

                }
            }
        });

        this.viewAll = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.goals.viewallBTN"),//"View All",
            scope:this,
            disabled:true,
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.goals.viewallBTN.ttip")//"Click to view all goals."
            },
            iconCls:'pwndCRM showgrp',
            handler:function(){
                    this.fromPeriod.setValue("");
                    this.toPeriod.setValue("");
                    var sm =this.selectionModel;
                    if(sm.getSelections().length==1){
                                this.addbutton.enable();
                                var rec = sm.getSelected();
                                this.goalstore.baseParams.userid = rec.get('userid');
                                this.goalstore.baseParams.viewAll = true;
                                this.goalstore.load({
                                     params : { start : 0,limit : this.pP.combo.value}
                                });
                    }

            }
        });

        this.reset = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.RESETBUTTON"),//"Reset",
            scope:this,
            disabled:true,
            iconCls:'pwndCRM reset',
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.editor.toptoolbar.resetBTN.ttip")//'Click to remove any filter settings and view all records.'
            },
            handler:function(){
                this.fromPeriod.setValue("");
                this.toPeriod.setValue("");
                this.goalstore.baseParams.frm = "";
                this.goalstore.baseParams.to = "";
                this.goalstore.baseParams.viewAll = false;

                var sm =this.selectionModel;
                if(sm.getSelections().length==1){
                            this.addbutton.enable();
                            var rec = sm.getSelected();
                            this.goalstore.baseParams.userid = rec.get('userid');
                            this.goalstore.load({
                                 params : {start : 0,limit : this.pP.combo.value}
                            });
                }
            }

        });

        this.getUsergrid();

        this.pg = new Wtf.PagingSearchToolbar({
            pageSize: 15,
            store: this.goalstore,
            displayInfo:true,
            emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//"No results to display",
            plugins: this.pP = new Wtf.common.pPageSize()
        });
        this.toolbarItems = [];
        this.toolbarItems.push(WtfGlobal.getLocaleText("crm.goalsettings.qsearch.label")+': ');//'Quick Search: ');
        this.toolbarItems.push('-');
        this.toolbarItems.push(this.quickSearchTF);
        this.toolbarItems.push('-');
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.GoalSettings, Wtf.Perm.GoalSettings.manage)) {
            this.toolbarItems.push(this.addbutton);
            this.toolbarItems.push('-');
        }

        this.toolbarItems.push(WtfGlobal.getLocaleText("crm.fromdate.label"));
        this.toolbarItems.push(this.fromPeriod);
        this.toolbarItems.push('-');
        this.toolbarItems.push(WtfGlobal.getLocaleText("crm.todate.label"));
        this.toolbarItems.push(this.toPeriod);
        this.toolbarItems.push('-');
        this.toolbarItems.push(this.fil);
        this.toolbarItems.push('-');
        this.toolbarItems.push(this.viewAll);
        this.toolbarItems.push('-');
        this.toolbarItems.push(this.reset);
        this.goalgrid = new Wtf.grid.GridPanel({
            store: this.goalstore,
            sm:this.sm,
            autoScroll :true,
            border:false,
            id:'goalGrid'+this.id,
            scope:this,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.goalsettings.emptytextwatermark"))//"Please select an employee to view its active and future goals.")
            },
            loadMask:true,
            clicksToEdit :1,
            displayInfo:true,
            bbar:this.pg,
            cm: this.cm1
        });

        this.getDetailPanel();
        this.goalstore.on('datachanged', function() {
            var p = this.pP.combo.value;
        }, this);

        this.goalpan= new Wtf.Panel({
            layout:'fit',
            region:'center',
            border:true,
            id:this.id+'goalpan',

            items:[
            {
                layout:'fit',
                border:false,
                items:[this.goalgrid]
            }
            ]
        });
        this.goalUser= new Wtf.Panel({
            layout:'fit',
            region:'west',
            border:true,
            width:280,
            items:[
            {
                layout:'fit',
                border:false,
                items:[this.ShortUserGrid]
            }
            ]
        });
        this.MembergridPanel = new Wtf.common.KWLListPanel({
            title: '<span  style="">'+WtfGlobal.getLocaleText("crm.goals.title")+'</span><span style="float: right;"><span  class="greenrow" style=" margin: 0px 10px;">&nbsp;&nbsp;&nbsp;&nbsp;</span><span >'+WtfGlobal.getLocaleText("crm.goalsettings.title.completedgoal")+'</span><span class="redrow" style="margin: 0px 10px;">&nbsp;&nbsp;&nbsp;&nbsp;</span><span id="wtf-gen1092">'+WtfGlobal.getLocaleText("crm.goalsettings.title.failedgoal")+'</span></span>',
            autoLoad: false,
            autoScroll:true,
            paging: false,
            tbar:this.toolbarItems,
            layout: 'border',
            items: [this.goalUser,this.goalpan]
        });
        this.add(this.MembergridPanel);
        this.goalgrid .on("validateedit",this.validate,this);
        this.goalgrid.on('rowclick', function(grid, rowIndex, e){
            if(e.target.className == "pwndCommon addComment" || e.target.className == "pwndHRMS viewComment") {
                return;
            }
            var xy = e.getXY();
        }, this);
        this.selectionModel.on('selectionchange', function(selModel) {
            var sm =this.selectionModel;
            if(sm.getSelections().length==1){
                        this.addbutton.enable();
                        this.fil.enable();
                        this.viewAll.enable();
                        this.reset.enable();
                        var rec = sm.getSelected();
                        this.goalstore.baseParams.userid = rec.get('userid');
                        this.goalstore.load({
                             params : {start : 0,limit : this.pP.combo.value}
                        });
            this.goalgrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.goalsettings.nogoalassgnd"));//"No active goal assigned till now to the selected employee.");
            }else{
                this.addbutton.disable();
                this.viewAll.disable();
                this.fil.disable();
                this.reset.disable();
                this.goalgrid.getView().emptyText = WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.goalsettings.emptytextwatermark"));//"Please select an employee to view its active and future goals.");
                this.goalgrid.getStore().removeAll();
            }
        }, this);
        this.goalgrid.on('click', this.docuploadhandler, this);
        this.goalgrid.on("cellclick", this.deleteTarget, this);
        this.goalstore.on('load', this.colorGridRow, this);
        this.goalgrid.on('sortchange',this.colorGridRow, this);

        this.goalgrid.on('render',function(){this.goalgrid.getStore().removeAll();},this);
    },
    getUsergrid : function(){
        this.usersRec = new Wtf.data.Record.create([
        {
            name: 'userid'
        },

        {
            name: 'username'
        },
        {
            name: 'fullname'
        }
        ]);

        this.userds = new Wtf.data.Store({
            reader: new Wtf.data.KwlJsonReader({
                root: "data",
                totalProperty:"count"
            },this.usersRec),
            url:'Common/User/getSubOrdinateUsers.do',
            baseParams:{
                mode:11
            }
        });
        this.userds.load({
            params : {
                start:0,
                limit:15
            }
        });
        this.selectionModel = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:true,
            width: 30,
            id:"goal_setting_users"
        });

        this.gridcm= new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            this.selectionModel,
            {
                header:WtfGlobal.getLocaleText("crm.goalsettings.header.empname"),// "Employee Name",
                tip:WtfGlobal.getLocaleText("crm.goalsettings.header.empname"),//'Employee Name',
                dataIndex: 'fullname',
                autoWidth : true,
                sortable: true,
                groupable: true
            }]);
        this.quickSearchTF = new Wtf.KWLTagSearch({
                id: 'administration_goal'+this.id,
                width: 150,
                emptyText:WtfGlobal.getLocaleText("crm.goalsettings.qsearch.mtytxt")// 'Enter Employee Name '
            });
        this.ShortUserGrid=new Wtf.grid.GridPanel({
            layout:'fit',
            store: this.userds,
            cm: this.gridcm,
            sm : this.selectionModel,
            border : false,
            loadMask : true,
            view:new Wtf.ux.KWLGridView({
                forceFit:true
            }),
            bbar:new Wtf.PagingSearchToolbar({
                pageSize: 15,
                searchField:this.quickSearchTF,
                id: "admin_pagingtoolbar1",
                store: this.userds,
                displayInfo: false
            })
        });
        this.userds.on('load',function(){
            this.quickSearchTF.StorageChanged(this.userds);
            this.quickSearchTF.on('SearchComplete', function() {
                this.ShortUserGrid.getView().refresh();
            }, this);
        },this);

        this.userds.on("datachanged",function(){
            this.quickSearchTF.setPage(this.pP.combo.value);
        },this);

    },
    onRender: function(config) {
        Wtf.perticularemployeegoals.superclass.onRender.call(this, config);
    },

    colorGridRow: function(){
        for(var i=0 ; i< this.goalstore.data.length;i++){
            if(this.goalstore.data.items[i].data.pastgoals==1){
                if(this.goalgrid.view.getRow(i).className.indexOf("greenrow") == -1)
                    this.goalgrid.view.getRow(i).className += " greenrow";
            }else if(this.goalstore.data.items[i].data.pastgoals==0){
                if(this.goalgrid.view.getRow(i).className.indexOf("redrow") == -1)
                    this.goalgrid.view.getRow(i).className += " redrow";
            }
        }
    },
    deleteTarget:function(grid, ri, ci, e) {

        var event = e;
        if(event.target.className == "pwnd deleteButton") {
             this.deletegoal();
        } else if(event.target.className == "pwnd editGoalIcon") {
             this.insertgoal(grid.getSelectionModel().getSelected());
        } else if(event.target.className == "pwndCRM historyGoalIcon") {
              this.historyTabFun(grid.getSelectionModel().getSelected());
        }
    },
    historyTabFun:function(rec){
        this.main=Wtf.getCmp("goalmanagementtabpanel");
        var id = rec.data.gid;
        this.historyTab=Wtf.getCmp("goalhistory"+id);
        if(this.historyTab==null)
        {
            this.historyTab=new Wtf.goalHistory({
                mainTab:this.main,
                id:"goalhistory"+id,
                goalid:id,
                minTabWidth: 155,
                scope:this
            });
            this.main.add(this.historyTab);
        }
        this.main.setActiveTab(this.historyTab);
        this.main.doLayout();
        Wtf.getCmp("as").doLayout();
    },
    validate:function(e){
        if(e.column==7)
        {
            if(e.record.get('gstartdate') > e.value)
            {
                return false;
            }
        }
        if(e.column==6)
        {
            if(e.record.get('genddate')!="")

            {
                if(e.record.get('genddate') < e.value)
                {
                    return false;
                }
            }
        }
    },
    docuploadhandler:function(e, t){
        if(e.target.className == "pwndCommon addComment" )
        {

            if(this.goalgrid.getSelectionModel().getCount()==0){
                calMsgBoxShow(131,0);
            }
            else{
                this.goalrec=this.goalgrid.getSelectionModel().getSelections();
                this.goalarr=[];
                for(var i=0;i<this.goalrec.length;i++){
                    this.goalarr.push(this.goalrec[i].get('gid'));
                }
                this.addcom=new Wtf.goalComment({
                    width:390,
                    modal:true,
                    height:250,
                    title:WtfGlobal.getLocaleText("crm.goalsettings.goalcomments.title"),//"Goal Comments",
                    resizable:false,
                    layout:'fit',
                    note:WtfGlobal.getLocaleText("crm.goalsettings.goalcomments.formdetailnote"),//'Fill up the following form',
                    read:false,
                    blank:false,
                    viewflag:false,
                    applybutton:true,
                    commentflag:true,
                    goalarr:this.goalarr,
                    ds:this.goalstore,
                    cleargrid:this.goalgrid
                });
                this.addcom.show();
            }
        }else{
            if(e.target.className == "pwndHRMS viewComment")
                if(this.goalgrid.getSelectionModel().getCount()==0||this.goalgrid.getSelectionModel().getCount()>1){
                    calMsgBoxShow(131,0);
                }else{
                    this.viewcom=new Wtf.goalComment({
                        width:390,
                        modal:true,
                        height:250,
                        title:WtfGlobal.getLocaleText("crm.goalsettings.goalcomments.title"),//"Goal Comments",
                        resizable:false,
                        layout:'fit',
                        note:WtfGlobal.getLocaleText("crm.goalsettings.goalcomments.empcommentnote"),//'Employee comment is',
                        read:true,
                        blank:true,
                        viewflag:true,
                        comnt:this.goalgrid.getSelectionModel().getSelected().get('gcomment'),
                        applybutton:false
                    });
                    this.viewcom.show();
                }

        }
    },
    insertgoal:function(rec){

        var assigntargetvalue;
        var assignrelatedvalue;
        var assignstartdate=new Date();
        var assignenddate=new Date();
        var assigngoalcreationdate=new Date();
        var gid;
        var empidrec=this.ShortUserGrid.getSelections();

        if(empidrec==0){
            ResponseAlert(800);
            return;
        }
        var empid=empidrec[0].data.userid;
        var empname =empidrec[0].data.fullname

        var recd = rec.data;
        if(recd!=undefined){
            assigntargetvalue=(recd.targeted!="" || recd.targeted=="0")?recd.targeted:valueOf(assigntargetvalue);
            assignrelatedvalue=recd.relatedto!=""?recd.relatedto:assignrelatedvalue;
            assignstartdate=recd.gstartdate!=""?recd.gstartdate:assignstartdate;
            assignenddate=recd.genddate!=""?recd.genddate:assignenddate;
            assigngoalcreationdate=recd.genddate!=""?recd.createdon:assigngoalcreationdate;
            gid=recd.gid!=""?recd.gid:gid;
            empid=recd.empid!=""?recd.empid:empid;
            empname=recd.empname!=""?recd.empname:empname;
        }

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

        this.relatedToStore = new Wtf.data.SimpleStore({
            fields: ['id','name'],
            data : permData
        });




        this.assignGoalform = new Wtf.form.FormPanel({
            region:"center",
            border:false,
            defaults:{
                width:240,
                allowBlank:false
            },
            bodyStyle:"background-color:#f1f1f1;padding:32px",
            items:[this.relatedTo= new Wtf.form.ComboBox({
                fieldLabel: WtfGlobal.getLocaleText("crm.goals.header.goaltype"),//"Goal Type ",
                id:this.id+"relatedto",
                selectOnFocus:true,
                triggerAction: 'all',
                mode: 'local',
                store: this.relatedToStore,
                displayField: 'name',
                emptyText:WtfGlobal.getLocaleText("crm.goalsettings.combo.emptytxt"),//"-- Please Select --",
                typeAhead: true,
                allowBlank:false,
                forceSelection:true,
                valueField:'id',
                msgTarget:'side',
                anchor:'100%',
                width:250,
                value:assignrelatedvalue,
                disabled:assignrelatedvalue==undefined?false:true

            }),
                this.assigntarget= new Wtf.form.TextField({
                fieldLabel:WtfGlobal.getLocaleText("crm.goals.header.target"),//'Target',
                width:250,
                allowBlank:false,
                allowNegative:false,
                decimalPrecision:0,
                id:this.id+"assigntargetfield",
                msgTarget:'side',
                allowDecimals:false,
                regex:/^\d{0,10}$/,
                maxLength:10,
                value:assigntargetvalue

            }),
            this.startDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.goalsettings.assigngoalein.fromdatelabel"),//'From Date',
                format:WtfGlobal.getOnlyDateFormat(),
                anchor:'98%',
                readOnly:true,
                style:'width:90%',
                id:'targetstartdate'+this.id,
                value:assignstartdate
            }),
            this.endDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.goalsettings.assigngoalein.todatelabel"),//'To Date',
                format:WtfGlobal.getOnlyDateFormat(),
                anchor:'98%',
                style:'width:90%',
                readOnly:true,
                id:'targetenddate'+this.id,
                value:assignenddate
            }),
            this.goalCreationDate = new Wtf.form.DateField({
                fieldLabel:WtfGlobal.getLocaleText("crm.goals.header.createdon"),//'Goal Creation Date',
                format:WtfGlobal.getOnlyDateFormat(),
                anchor:'98%',
                style:'width:90%',
                readOnly:true,
                id:'goalcreationdate'+this.id,
                value:assigngoalcreationdate
            })]
        });
        this.assignGoal = new Wtf.Window({
            resizable: false,
            scope: this,
            layout: 'border',
            modal:true,
            width: 450,
            height:350,
            iconCls: 'pwnd favwinIcon',
            title: WtfGlobal.getLocaleText("crm.goalsettings.toptoolbar.assigngoalsBTN"),//'Assign Goal',
            items:[{
                region : 'north',
                height : 80,
                border : false,

                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html : getTopHtml(WtfGlobal.getLocaleText("crm.goals.goalstatuswin.tophtml.title"),WtfGlobal.getLocaleText({key:"crm.goalsettings.assigngoalswin.tophtml.detail",params:[empname]}),"../../images/assign-goal.jpg")
            },this.assignGoalform
            ],
            buttons: [{
                text: WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
                id: 'previous_Overrite',
                type: 'submit',
                scope: this,
                handler: function(){

                    this.saveData(gid,empid);

                }
            },{
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                scope:this,
                handler:function() {
                    this.assignGoal.close();
                }
            }]
        });

        this.assignGoal.show();
        this.relatedTo.on("select",function(store,rec,index){
            if(rec.data.id == "2" || rec.data.id == "4" || rec.data.id == "6"){
                var dd_textfield = Wtf.getCmp(this.id+"assigntargetfield");
                var ct = dd_textfield.el.findParent('div.x-form-item', 3, true);
                var label = ct.first('label.x-form-item-label');
                ct.first('label.x-form-item-label').dom.innerHTML = "Target ("+WtfGlobal.getCurrencySymbol()+") :";
            } else {
                var dd_textfield = Wtf.getCmp(this.id+"assigntargetfield");
                var ct = dd_textfield.el.findParent('div.x-form-item', 3, true);
                var label = ct.first('label.x-form-item-label');
                ct.first('label.x-form-item-label').dom.innerHTML = "Target :";
            }
        }
       ,this);
       if(recd!=undefined ){
            if(recd.relatedto == "2" || recd.relatedto == "4" || recd.relatedto == "6"){
                var dd_textfield = Wtf.getCmp(this.id+"assigntargetfield");
                var ct = dd_textfield.el.findParent('div.x-form-item', 3, true);
                var label = ct.first('label.x-form-item-label');
                ct.first('label.x-form-item-label').dom.innerHTML = "Target ("+WtfGlobal.getCurrencySymbol()+") :";
            }
       }

    },
    saveData:function(gid,empid){
 
        if(this.assignGoalform.form.isValid()) {
        var relatedto = this.relatedTo.getValue();
        var targeted = this.assigntarget.getValue();
        var startdate=Wtf.getCmp('targetstartdate'+this.id).getValue();
        var enddate=Wtf.getCmp('targetenddate'+this.id).getValue();
        var createdate=Wtf.getCmp('goalcreationdate'+this.id).getValue();

        var startdat=new Date(startdate)
        var enddat=new Date(enddate);
        var createddate=new Date(createdate);
        if(enddat<startdat) {
            ResponseAlert(58);
            return;
        }

        var keyArray =['gname','gid','empid','gdescription','gwth','gcontext','gpriority','targeted',
                        'relatedto','gstartdate','genddate','gcreatedate','gcomment'];
        
        var valArray =['',gid,empid,'','','','',targeted,relatedto,startdate.getTime()
            ,enddate.getTime(),createddate.getTime(),''];

        var jsondata = WtfGlobal.JSONBuilder(keyArray, valArray);
                
            Wtf.commonWaitMsgBox("Saving data...");
                Wtf.Ajax.requestEx({
                    url:Wtf.req.springBase+"common/HRMSIntegration/insertGoal.do",
                    params: {
                        flag:822,
                        jsondata:jsondata,
                        empid:this.empid
                    }
                }, this,
                function(response){
                    if(response.success){
                       var sm =this.selectionModel;
                        if(sm.getSelections().length==1){
                                    this.addbutton.enable();
                                    var rec = sm.getSelected();
                                    this.goalstore.baseParams.userid = rec.get('userid');
                                    this.goalstore.load({
                                         params : {start : 0,limit : this.goalgrid.getBottomToolbar().pageSize}
                                    });
                        }
                    }
                    Wtf.updateProgress();
                     var strobj = ["", response.msg];
                     ResponseAlert(strobj);

                },
                function(response)
                {
                    Wtf.updateProgress();
                    ResponseAlert(752);
                })

                this.assignGoal.close();

        } else {
            ResponseAlert(152);
        }
    },

    deletegoal:function() {
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//'Confirm',
            msg:WtfGlobal.getLocaleText("crm.goalsettings.deletegoal.confirmmsg"),//"Are you sure you want to delete selected goal?<br><br><b>Note: This data cannot be retrieved later.</b>",
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            fn:function(button){
                if(button=='yes')
                {
                    this.delkey=this.sm.getSelections();
                    this.ids=[];
                    this.sm.clearSelections();
                    var store=this.goalgrid.getStore();
                    for(var i=0;i<this.delkey.length;i++){
                        var rec=this.goalstore.indexOf(this.delkey[i]);
                        if(this.delkey[i].get('gid'))
                        {
                            this.ids.push(this.delkey[i].get('gid'));
                        }
                        else{
                            store.remove(this.delkey[i]);
                        }
                    }
                    if(this.ids.length>0)
                    {
                    	var waitmsg=WtfGlobal.getLocaleText("crm.goalsettings.waitmsg.deletegoalmsg");
                        Wtf.commonWaitMsgBox(waitmsg);//"Deleting goal...");
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'crm.jsp',
                            url:Wtf.req.springBase+"common/HRMSIntegration/deleteAssignedGoals.do",
                                params:{
                                    flag:823,
                                    ids:this.ids
                                }
                            },this,
                            function(){
                                ResponseAlert(753);
                                var sm =this.selectionModel;
                                if(sm.getSelections().length==1){
                                            this.addbutton.enable();
                                            var rec = sm.getSelected();
                                            this.goalstore.baseParams.userid = rec.get('userid');
                                            this.goalstore.load({
                                                 params : {start : 0,limit : this.goalgrid.getBottomToolbar().pageSize}
                                            });
                                }
                                Wtf.updateProgress();
                            },
                            function(){
                                Wtf.updateProgress();
                                ResponseAlert(754);
                            }
                        )
                    }
                }
            }
        });
    },
    archivegoal:function(){
        if(this.goalgrid.getSelectionModel().getCount()==0){
    //        calMsgBoxShow(42,1);
        }
        else{
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//'Confirm',
                msg:WtfGlobal.getLocaleText("crm.goalsettings.confirmarchivemsg"),//"Are you sure you want to archive selected goal(s)?",
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        this.archiverec=this.goalgrid.getSelectionModel().getSelections();
                        this.archivearr=[];
                        for(var i=0;i<this.archiverec.length;i++){
                            this.archivearr.push(this.archiverec[i].get('gid'));
                        }
                        Wtf.Ajax.requestEx({
                            url:Wtf.req.springBase+"common/HRMSIntegration/insertGoal.do",
                            params:  {
                                flag:822,
                                archiveid:this.archivearr,
                                archive:"true"
                            }
                        },
                        this,
                        function(){
                            var sm =this.selectionModel;
                                if(sm.getSelections().length==1){
                                            this.addbutton.enable();
                                            var rec = sm.getSelected();
                                            this.goalstore.baseParams.userid = rec.get('userid');
                                            this.goalstore.load();
                                }
                            var archivegoal=Wtf.getCmp('archivedgoalemp');
                            if(archivegoal!=null){
                                archivegoal.getStore().load();
                            }
                        },
                        function(){
                        })
                    }
                }
            })
        }
    },
    viewComment:function(){
        if(this.goalgrid.getSelectionModel().getCount()==0||this.goalgrid.getSelectionModel().getCount()>1){
  //          calMsgBoxShow(131,0);
        }
        else{
            this.viewcom=new Wtf.goalComment({
                width:390,
                modal:true,
                height:250,
                title:WtfGlobal.getLocaleText("crm.goalsettings.goalcomments.title"),//"Goal Comments",
                resizable:false,
                layout:'fit',
                note:WtfGlobal.getLocaleText("crm.goalsettings.goalcomments.empcommentnote"),//'Employee comment is',
                read:true,
                blank:true,
                viewflag:true,
                comnt:this.goalgrid.getSelectionModel().getSelected().get('gcomment'),
                applybutton:false
            });
            this.viewcom.show();
        }
    },
    addComment:function(){
        if(this.goalgrid.getSelectionModel().getCount()==0){
            calMsgBoxShow(131,0);
        }
        else{
            this.goalrec=this.goalgrid.getSelectionModel().getSelections();
            this.goalarr=[];
            for(var i=0;i<this.goalrec.length;i++){
                this.goalarr.push(this.goalrec[i].get('gid'));
            }
            this.addcom=new Wtf.goalComment({
                width:390,
                modal:true,
                height:250,
                title:WtfGlobal.getLocaleText("crm.goalsettings.goalcomments.title"),//"Goal Comments",
                resizable:false,
                layout:'fit',
                note:WtfGlobal.getLocaleText("crm.goalsettings.goalcomments.formdetailnote"),//'Fill up the following form',
                read:false,
                blank:false,
                viewflag:false,
                applybutton:true,
                goalarr:this.goalarr,
                ds:this.goalstore,
                commentflag:true,
                cleargrid:this.goalgrid
            });
            this.addcom.show();
        }
    },
    getDetailPanel:function(){
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.goalgrid,
            Store:this.goalstore,
            modulename:'Goals',
            height:200,
            mapid:0,
            id2:this.id
        });
    },
    decideAction:function (){
        var rec=this.sm.getSelections();
        var gid="";
        if(rec.length>0){
            gid=rec[0].data["gid"];
            if(gid!==undefined && gid!=''){
                var s = this.goalgrid.getSelectionModel().getSelections();
                var selectedRec="";
                if(s.length == 1){
                    selectedRec=this.EditorGrid.getSelectionModel().getSelected();
                }
                getDocsAndCommentList(selectedRec,gid,1,this.id);
            }
        }
    }

});

Wtf.goalHistory = function (config){
    Wtf.goalHistory.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.goalHistory,Wtf.Panel,{
    closable:true,
    layout : 'fit',
    title : WtfGlobal.getLocaleText("crm.goalsettings.goaldetails"),//'Goal Details',
    border:false,
    iconCls:"pwndCRM historyGoalTabIcon",
    initComponent: function(config) {
        Wtf.goalHistory.superclass.initComponent.call(this,config);
        this.getEditorGrid();
    },

    getEditorGrid : function () {
        var Rec = new Wtf.data.Record.create([
            {name:'accountid'},
            {name:'accountname'},
            {name:'revenue'},
            {name:'product'},
            {name:'productid'},
            {name:'website'},
            {name:'industry'},
            {name:'industryid'},
            {name:'type'},
            {name:'typeid'},
            {name:"createdon",
             type:'date'
            }
        ]);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'count'
        },Rec);

        this.EditorStore = new Wtf.data.Store({
           url:Wtf.req.springBase+"common/HRMSIntegration/showGoalHistory.do",
           remoteSort:true,
           baseParams: {
                goalid:this.goalid
            },
           reader:EditorReader
        });
        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));

        this.exp = exportButton(this,"Goal(s)",44);
        this.printprv = printButtonR(this,"Goal",44);
        this.EditorColumn = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:'Account Name',
                pdfwidth:60,
                dataIndex:'accountname'
            },{
                header:"Revenue ("+WtfGlobal.getCurrencySymbol()+")",
                renderer:WtfGlobal.currencyRenderer,
                headerName:"Revenue",
                align:'right',
                pdfwidth:60,
                dataIndex:'revenue'
            },{
                header:'Product',
                pdfwidth:60,
                title:'exportmultiproduct',
                dataIndex:'product'

            },{
                header:'Account Type',
                pdfwidth:60,
                dataIndex:'type'
            },{
                header:"Industry",
                pdfwidth:60,
                dataIndex:'industry'
            },{
                header:'Website',
                pdfwidth:60,
                dataIndex:'website'

            },{
                header:'Created On',
                format:WtfGlobal.getOnlyDateFormat(),
                pdfwidth:60,
                renderer:WtfGlobal.onlyDateRenderer,
                dataIndex:'createdon'

            }]);

        this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
        this.quickPanelSearch = new Wtf.KWLTagSearch({
            width: 150,
            emptyText: 'Enter Account Name ',
            field: "accountname"
        });
        this.pg = new Wtf.PagingSearchToolbar({
            pageSize: 25,
            searchField: this.quickPanelSearch,
            store: this.EditorStore,
            displayInfo:true,
            emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//"No results to display",
            plugins: this.pP = new Wtf.common.pPageSize()
        });
        this.Grid = new Wtf.grid.GridPanel({
            layout:'fit',
            store: this.EditorStore,
            cm: this.EditorColumn,
            sm : this.selectionModel,
            scope:this,
            loadMask:true,
            displayInfo: true,
            border : false,
            height:400,
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.goalsettings.ermsg"))
            },
            bbar:this.pg,
            tbar : ['-',this.exp,'-',this.printprv,'-']
        });


         this.add(this.Grid);
         this.doLayout();
         this.EditorStore.load({
            params : {
                start : 0,
                limit : this.Grid.getBottomToolbar().pageSize
          }});
         this.EditorStore.on('load',function(){
            Wtf.updateProgress();
         },this);
         this.EditorStore.on("loadexception",function(){
            Wtf.updateProgress();
        },this);
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Goal Details";
        var fromdate="";
        var todate="";
        var report="crm";
        var exportUrl = Wtf.req.springBase+"common/HRMSIntegration/exportGoalHistory.do";
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,undefined,undefined,field,dir);
    },
    exportfile: function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="GoalDetails";
        var fromdate="";
        var todate="";
        var report="crm"
        var exportUrl = Wtf.req.springBase+"common/HRMSIntegration/exportGoalHistory.do";
		if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && field=="createdon"){
                field="createdOn";
            }
            var dir = this.sortInfo.direction;
        }
        exportWithTemplate(this,type,name,fromdate,todate,report,exportUrl,undefined,undefined,undefined,field,dir);
    }

});
