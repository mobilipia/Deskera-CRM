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
Wtf.activityEditor = function (config){
    config.layout='border';
    Wtf.activityEditor.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.activityEditor,Wtf.Panel,{
    getEditor:Wtf.SpreadSheetGrid.prototype.getEditor,
    initComponent: function(config){
        Wtf.activityEditor.superclass.initComponent.call(this,config);
        this.getStores();
        this.getEditorGrid();
        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
    },
    onRender: function(config){
        Wtf.activityEditor.superclass.onRender.call(this,config);

        // advance search to be added later
        //        this.getAdvanceSearchComponent();

        this.getDetailPanel();

        this.activitypan= new Wtf.Panel({
            layout:'border',
            region:'center',
            border:false,
            id:this.id+'activitypan',
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
            // advance search to be added later
            //this.objsearchComponent,
            {
                region:'center',
                layout:'fit',
                items:[this.spreadSheet],
                tbar:this.toolbarItems,
                bbar:this.btmbar
            },
            {
                region:'south',
                height:240,
                title:'Other Details',
                collapsible:true,
                collapsed : true,
                plugins: new Wtf.ux.collapsedPanelTitlePlugin(),
                split : true,
                layout:'fit',
                items:[this.detailPanel],
                listeners:{
                    'expand':this.getDetails,
                    scope:this
                }
            }]
        });
        this.add(this.activitypan);
        this.activitypan.on("activate",function(){
            Wtf.getCmp("ActivityHomePanelactivitypan").doLayout()
        },this)
    //this.objsearchComponent.on("filterStore",this.filterStore, this);
    //this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);
    },
    getDetails:function(){
    	var sm = this.EditorGrid.getSelectionModel();
    	if(sm.getCount()!=1)
    		return;

    	var commentlist = getDocsAndCommentList(sm.getSelected(), "activityid",this.id,undefined,'Activity',undefined,undefined,undefined,undefined,0);
    },

    getEditorGrid : function(){

        this.viewByStatuStore = new Wtf.data.Store({

            url: 'Common/CRMManager/getComboData.do',
            baseParams:{
                comboname:'Task Status',
                common:'1'
            },
            reader: new Wtf.data.KwlJsonReader({
                root:'data'
            }, Wtf.ComboReader),
            autoLoad:true
        });
        this.viewByStatuStore.on("load",function(){
            var newresentry = new Wtf.ComboReader({
                id: '-1',
                name: 'All'

            });
            this.viewByStatuStore.insert(0, newresentry);
        },this);
        x=[
        {
            name:'activityid'
        },
        {
            name:'ownerid',
            defValue:loginid
        },
        {
            name:'owner',
            defValue:_fullName
        },
        {
            name:'calid',
            customMapping:'calendarid'
        },
        {
            name:'subject'
        },
        {
            name:'flag'
        },
        {
            name:'status'
        },
        {
            name:'statusid'
        },
        {
            name:'relatedto',
            defValue:(this.rFlag==0?"":this.Rrelatedto),
            customMapping:'relatedtoid'
        },
        {
            name:'relatedtoold'
        },
        {
            name:'relatednameid',
            defValue:(this.rFlag==0?"":this.relatedtonameid),
            customMapping:'relatedtonameid'
        },
        {
            name:'relatedname'
        },
        {
            name:'phone'
        },
        {
            name:'priority'
        },
        {
            name:'priorityid'
        },
        {
            name:'startdat'
        },
        {
            name:'enddat'
        },
        {
            name:'startdate',
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getEventDefaultStartTime
        },
        {
            name:'enddate',
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getEventDefaultEndTime
        },
        {
            name:'starttime',
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.setDefaultValueTimefield
        },
        {
            name:'endtime',
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.setDefaultValueEndTimefield
        },
        {
            name:'type'
        },
        {
            name:'typeid'
        },
        {
            name:"createdon",
            dateFormat:'time',
            type:'date',
            defValue:WtfGlobal.getCurrentTime
        },
        {
            name:"validimage"
        },
        {
            name:"cellStyle"
        },
        {
            name:"validflag"
        },
        {
            name:"totalcomment"
        },
        {
            name:"commentcount"
        },{
            name : "isallday",
            defValue:false
        }
        ];

        this.updateInfo = {
    			keyField:'activityid',
    			auditStr:"Activity details updated from activity profile for ",
    			url:"Activity/action/saveActivity.do",
    			flag:22,
    			type:"Activity"
    		};

        
        this.EditorRec = new Wtf.data.Record.create(x);
        this.loadCount=0;
        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.EditorRec);
 
        this.EditorStore = new Wtf.data.Store({
        	proxy: new Wtf.data.HttpProxy(new Wtf.data.Connection({url: Wtf.req.springBase+"Activity/action/getActivity.do", timeout:90000})), 
            //url: Wtf.req.springBase+"Activity/action/getActivity.do",
            pruneModifiedRecords:true,
            remoteSort:Wtf.ServerSideSort,
            paramNames:{sort:'field',dir:'direction'},
            scope:this,
            baseParams:{
                flag:this.urlFlag,
                mapid:this.relatedtonameid,
                isarchive:this.newFlag==3?true:false,
                module:this.Rrelatedto
            },
           extraSortInfo:{xtype:"datefield",xfield:"updatedOn",iscustomcolumn:false},
           sortInfo:{field:"updatedon",direction:"DESC"},
            method:'post',
            reader:EditorReader
        });

        this.EditorColumnArray =[
        {
            header:'',
            width:30,
            id:'validflag',
            unselectable:true,
            dataIndex:'validflag',
            renderer:WtfGlobal.renderValidFlagAndComment
        },
        {
            tip:WtfGlobal.getLocaleText("crm.report.activity.taskorevent"),//'Task/ Event',
            header:WtfGlobal.getLocaleText("crm.report.activity.taskorevent")+ ' *',//"Task/ Event *",
            mandatory:true,
            id:'taskevent',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            editor : this.getEditor({
                xtype:"combo",
                store:this.flagStore,
                useDefault:true
            }),
            dataIndex: 'flag',
            xtype:"combo"
        },
        {
            header:WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.owner")+' *',//"Owner *",
            tip:WtfGlobal.getLocaleText("crm.mydocuments.bottomtoolbar.sortmenu.owner"),//'Owner',
            title:'owner',
            id:'owner',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            mandatory:true,
            hidden:true,
            editor : this.getEditor({
                xtype:"combo",
                store:Wtf.ownerStore,
                useDefault:true,
                loadOnSelect : true,
                searchStoreCombo:true, 
                comboFieldDataIndex : 'owner'
            }),
            dataIndex:'owner',
            dbname:'usersByUserid.userID',
            cname:'owner',
            xtype:'combo'
        },
        {
            header:WtfGlobal.getLocaleText("crm.lead.defaultheader.type"),//"Type",
            tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.type"),//'Type',
            id:'type',
            title:'type',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            hidden:true,
            editor : this.getEditor({
                xtype:"combo",
                store:Wtf.typeStore,
                useDefault:true,
                loadOnSelect : true,
                searchStoreCombo:true, 
                comboFieldDataIndex : 'type'
            }),
            dataIndex: 'type',
            dbname:'crmCombodataByTypeid.ID',
            xtype:'combo',
            cname:'Task Type'
        },{
            header:WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//"Subject",
            tip:WtfGlobal.getLocaleText("crm.case.defaultheader.subject"),//'Subject',
            id:'subject',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            sortable: true,
            editor:new Wtf.form.TextField({
                xtype : 'textfield',
                maxLength : 1024,
                regexText:Wtf.MaxLengthText+"1024"
            }),
            dbname:'subject',
            xtype:'textfield',
            dataIndex: 'subject'

        },
        {
            header:WtfGlobal.getLocaleText("crm.case.defaultheader.status")+' *',//"Status *",
            tip:WtfGlobal.getLocaleText("crm.case.defaultheader.status"),//'Status',
            id:'status',
            title:'status',
            mandatory:true,
            pdfwidth:60,
            width:Wtf.defaultWidth,
            editor : this.getEditor({
                xtype:"combo",
                store:Wtf.statusStore,
                useDefault:true,
                loadOnSelect : true,
                searchStoreCombo:true, 
                comboFieldDataIndex : 'status'
                
            }),
            dataIndex: 'status',
            dbname:'crmCombodataByStatusid.ID',
            xtype:'combo',
            cname:'Task Status'
        },
         {
            header:WtfGlobal.getLocaleText("crm.calendar.calendar"),//"Calendar",
            tip:WtfGlobal.getLocaleText("crm.calendar.calendar"),//'Calendar',
            id:'calname',
            title:'calname',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            hidden:false,
            editor : this.getEditor({
                xtype:"combo",
                store:this.exportDS,
                useDefault:true
            }),
            dataIndex: 'calid',
            xtype:'combo',
            dbname:'calendarid',
            cname:'Calendar'

        },
        {
            header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.startdate")+' *',//"Start Date *",
            tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.startdate"),//'Start Date',
            id:'startdate',
            title:'startdat',
            align:'center',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            mandatory:true,
            sortable: true,
            editor : new Wtf.form.DateField({
                xtype:'datefield',
                offset:Wtf.pref.tzoffset,
                format:WtfGlobal.getOnlyDateFormat()
            }),
            dbname: 'startDate',
            dataIndex: 'startdate',
            xtype:'datefield',
            renderer:WtfGlobal.onlyDateRendererTZ
        },
        {
            header:WtfGlobal.getLocaleText("crm.calendar.eventdetails.starttime"),//"Start Time",
            tip:WtfGlobal.getLocaleText("crm.calendar.eventdetails.starttime"),//'Start Time',
            id:'starttime',
            align:'center',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            sortable: true,
            editor : new Wtf.form.TimeField({
                xtype:'timefield',
                format:WtfGlobal.getLoginUserTimeFormat(),
                minValue: WtfGlobal.setDefaultMinValueTimefield(),
                maxValue: WtfGlobal.setDefaultMaxValueTimefield(),
                useDefault : true
            }),
            dataIndex: 'startdate',
            xtype:'timefield',
            renderer:this.timeRenderer,
            dbname: 'startDate'
        },
        {
            header:WtfGlobal.getLocaleText("crm.campaign.defaultheader.enddate")+' *',//"End Date *",
            tip:WtfGlobal.getLocaleText("crm.campaign.defaultheader.enddate"),//'End Date',
            id:'enddate',
            title:'enddat',
            align:'center',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            mandatory:true,
            sortable: true,
            editor : new Wtf.form.DateField({
                xtype:'datefield',
                offset:Wtf.pref.tzoffset,
                format:WtfGlobal.getOnlyDateFormat()
            }),
            dataIndex: 'enddate',
            xtype:'datefield',
            renderer:WtfGlobal.onlyDateRendererTZ,
            dbname: 'endDate'
        },
        {
            header:WtfGlobal.getLocaleText("crm.calendar.eventdetails.endtime"),//"End Time",
            tip:WtfGlobal.getLocaleText("crm.calendar.eventdetails.endtime"),//'End Time',
            id:'endtime',
            align:'center',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            sortable: true,
            editor : new Wtf.form.TimeField({
                xtype:'timefield',
                format:WtfGlobal.getLoginUserTimeFormat(),
                minValue: WtfGlobal.setDefaultMinValueTimefield(),
                maxValue: WtfGlobal.setDefaultMaxValueTimefield(),
                useDefault : true,
                selectOnFocus:false
            }),
            dataIndex: 'enddate',
            xtype:'timefield',
            renderer:this.timeRenderer,
            dbname: 'endDate'
        },
        {
            header:WtfGlobal.getLocaleText("crm.case.defaultheader.priority"),//"Priority",
            tip:WtfGlobal.getLocaleText("crm.case.defaultheader.priority"),//'Priority',
            id:'priority',
            title:'priority',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            editor : this.getEditor({
                xtype:"combo",
                store:Wtf.cpriorityStore,
                useDefault:true,
                loadOnSelect : true,
                searchStoreCombo:true, 
                comboFieldDataIndex : 'priority'
            }),
            dataIndex: 'priority',
            dbname:'crmCombodataByPriorityid.ID',
            xtype:'combo',
            cname:'Priority'
        },
        {
            header:WtfGlobal.getLocaleText("crm.contact.defaultheader.phone"),//"Phone",
            tip:WtfGlobal.getLocaleText("crm.contact.defaultheader.phone"),//'Phone',
            id:'phone',
            pdfwidth:60,
            width:Wtf.defaultWidth,
            align:'right',
            sortable: true,
            hidden:true,
            editor:new Wtf.form.TextField({
                xtype:'textfield',
                //    regex:Wtf.PhoneRegex,
                maxLength : 100,
                regexText:Wtf.MaxLengthText+"100"
            }),
            dbname:'phone',
            xtype:'textfield',
            dataIndex: 'phone',
            renderer:WtfGlobal.renderContactToCall
        } 
        ];
        this.tbarArchiveArray = Wtf.archivedMenuArray(this,"Activitie");
        this.tbarArchive = Wtf.archivedMenuButtonA(this.tbarArchiveArray,this,"Activities");

        this.exp = exportButton(this,"Activity",9);
        this.printprv = printButton(this,"Activity",9);
        this.toolbarItems = [];
        this.tbSingle = [];
        this.tbMulti = [];
        this.tbDefault = [];
        var tbIndex = 0;

        this.quickSearchTF = new Wtf.KWLTagSearch({
            width: 220,
            emptyText:WtfGlobal.getLocaleText(""),//"Search by Subject",
            id:'quick9',//In use,do not delete.
            Store:this.EditorStore,
            parentGridObj: this
        });
        this.toolbarItems.push(this.quickSearchTF);
        this.tbSingle.push(tbIndex);
        this.tbMulti.push(tbIndex);
        this.tbDefault.push(tbIndex);
        tbIndex++;

        this.deleteAct= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
            tooltip:{
                text:WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip")//'Select row(s) to delete.'
            },
            scope:this,
            iconCls:getTabIconCls(Wtf.etype.delet),
            disabled:(this.newFlag==1?true:false),
            handler:this.activityDelete
        });

        this.tbarCombo1=new Wtf.form.ComboBox({
            fieldLabel: WtfGlobal.getLocaleText("crm.editor.lead.viewbystatuscombo"),//'View  by Status',
            selectOnFocus:true,
            triggerAction: 'all',
            mode: 'local',
            store: this.viewByStatuStore,
            displayField: 'name',
            valueField:'id',
            anchor:'100%',
            value:'All',
            editable: false,
            width:140
        });
        //        this.AdvanceSearchBtn = new Wtf.Toolbar.Button({
        //            text : "Advanced Search",
        //            id:'advanced9',// In use, Do not delete
        //            scope : this,
        //            tooltip:'Search for multiple terms in multiple fields',
        //            handler : this.configurAdvancedSearch,
        //            iconCls : 'pwnd searchtabpane'
        //        });
        
        this.btmbar =Wtf.moduleBootomToolBar(this,this.id+'CRMupdownCompo',undefined,false);
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.manage)) {    
        		if(this.newFlag==2) {
            	    this.toolbarItems.push(Wtf.AddNewButton(this));
                    tbIndex++;
                    this.toolbarItems.push(Wtf.EditRecordButton(this));
                	tbIndex++;
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.del)) {
                    this.toolbarItems.push(this.deleteAct );
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.archive)) {
                    this.toolbarItems.push(this.tbarArchive);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;
                }
                
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.exportt)) {
                    this.toolbarItems.push(this.exp);
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;
                    this.toolbarItems.push(this.printprv);
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    tbIndex++;
                }
                //                this.toolbarItems.push(this.AdvanceSearchBtn);
                //                    this.tbSingle.push(tbIndex);
                //                    this.tbDefault.push(tbIndex);
                //                    this.tbMulti.push(tbIndex);
                //                    tbIndex++;

                this.toolbarItems.push('->');
                tbIndex++;
                this.toolbarItems.push(new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.editor.lead.viewbystatuscombo")));
                tbIndex++;
                this.toolbarItems.push(this.tbarCombo1);
                tbIndex++;
                this.toolbarItems.push('-');
            }
            if(this.newFlag==3){
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.del)) {                    
                    this.toolbarItems.push(this.deleteAct);
                    this.tbSingle.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
                }
                this.tbarUnArchive = Wtf.archivedMenuButtonB(this,"Activity");
                this.toolbarItems.push(this.tbarUnArchive);
                this.tbSingle.push(tbIndex);
                this.tbMulti.push(tbIndex);
                tbIndex++;
            }
        }
        if(this.clearFlag!=undefined || this.subTab!=undefined){
           // if(WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.manage))
                this.toolbarItems.push('->');
            var help=getHelpButton(this,9);
            this.toolbarItems.push(help);
        }
        this.spreadSheet = new Wtf.SpreadSheetGrid({
            cmArray:this.EditorColumnArray,
            store:this.EditorStore,
            moduleName :this.customParentModName,
            isEditor:Wtf.isEditable(this.customParentModName,this),
            pagingFlag : true,
            quickSearchTF : this.quickSearchTF,
            parentGridObj : this,
            keyid : 'activityid',
            id:'ActivityGrid'+this.id,
            updateURL:Wtf.req.springBase+'Activity/action/saveActivity.do',
            allowedNewRecord:!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.manage)
        });
        
        this.EditorStore.on("beforeload",function(){
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.responsealert.msg.500"));//"Loading data...");
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

        this.EditorGrid = this.spreadSheet.getGrid();
        this.EditorGrid.on("beforeupdate",this.addCompulsoryFields,this);
        this.EditorGrid.on("afterupdate",this.afterValidRecordSaved,this);
        this.EditorColumn = this.spreadSheet.getColModel();
        this.EditorStore.load({
            params:{
                start:0,
                limit:20
            }
        });
        if(this.newFlag==2){
            if(!WtfGlobal.EnableDisable(Wtf.UPerm.Activity, Wtf.Perm.Activity.manage)) {
            } else{
                this.EditorGrid.isEditor = false;
            }
        }
        this.EditorStore.on("load",function(){
        	if(this.loadCount !=undefined && this.loadCount != 0)
        		this.loadCount=0;
            this.SelChange();
            Wtf.updateProgress();
        },this);

        this.gridRowClick();
        cellClick(this);
        this.EditorGrid.getSelectionModel().on("rowselect",this.SelChange,this);
        this.EditorGrid.getSelectionModel().on("rowdeselect",this.SelChange,this);
        this.tbarCombo1.on('select',function(a,b,c){

            this.quickSearchTF.setValue("");
            if(c==0) {  //All
                this.statusRawValue='';
            }
            else {  // Completed ,Deferred,In progress and Not Started
                this.statusRawValue=b.data.name;
            }
            this.EditorStore.baseParams={
                flag:this.urlFlag,
                mapid:this.relatedtonameid,
                isarchive:this.newFlag==3?true:false,
                status:this.statusRawValue,
                module:this.Rrelatedto
            };
            this.EditorStore.load({
                params:{
                    start:0,
                    limit:25
                }
            });
        },this);
         
        this.EditorGrid.on("sortchange",this.sortChange,this);
        this.EditorGrid.on("rowclick",this.gridCellClick,this);
        this.EditorGrid.on("mouseover",Wtf.hideNotes,this);
        this.EditorGrid.on("beforeedit",this.beforeEdit,this);
        this.EditorGrid.on("validateedit",this.validateEdit,this);
        this.tbarArchive.on('click',function() {
            if(this.deleteAct.disabled==false)
                this.tbarArchiveArray[0].setTooltip(WtfGlobal.getLocaleText("crm.activity.archiveselected"));//"Archive the selected activity.");
            else
                this.tbarArchiveArray[0].setTooltip(WtfGlobal.getLocaleText("crm.editor.archiveBTN.disabled.ttip"));//"Select row(s) to send in Archive repository.");
        }, this);
        this.tbarPrint.on('mouseover',function() {
            var s = this.EditorGrid.getSelectionModel().getSelections();
            this.printprv.menu.items.items[1].setDisabled(s.length<=0);
        }, this);
        if(this.clearFlag!=undefined || this.subTab!=undefined){
            this.tbarExport.on("mouseover", function(){
                var s = this.EditorGrid.getSelectionModel().getSelections();
                this.exp.menu.items.items[1].setDisabled(s.length<=0);
                this.exp.menu.items.items[3].setDisabled(s.length<=0);
                this.exp.menu.items.items[5].setDisabled(s.length<=0);
            },this);
        }

    },
    
    addCompulsoryFields:function(e){
    	e.json["relatedtoid"] = e.record.get("relatedto");
    	e.json["relatedtonameid"] = e.record.get("relatednameid");
    },
    
    renderValidFlagAndComment: function(value, css, record, row, column, store) {
        var str="";
        var imgSrc="";
        var height="";
        var tip="";
        var clas="";
        var id="";
        var recdata = store.getAt(row).data;
        if(value == 1) {
           str=Wtf.commentRenderer(value,recdata.totalcomment,recdata.commentcount);
           return str;
        } else if(value == -1) {
            imgSrc = "../../images/indent.gif";
            height = "12px";
            tip = " wtf:qtitle='"+WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN")+"' wtf:qtip='"+WtfGlobal.getLocaleText("crm.activity.addnewrecmsg")+"'";
            id="addnew" + record.store.baseParams.flag;
        } else {
            imgSrc = "../../images/FlagRed.png";
            height = "12px";
            tip = " wtf:qtitle='"+WtfGlobal.getLocaleText("crm.activity.incompletinfomsg")+"' wtf:qtip=' "+WtfGlobal.getLocaleText("crm.activity.incompletinfomsgdetail")+"' ";
            id="flag";
            clas = "class = \'showMandatoryFields\'";
            str="<span style='padding-left:1px; float:left;'>";
        }
        str += "<img src='"+imgSrc+"' id='"+id+"' "+clas+" "+tip+" height='"+height+"' style='cursor:pointer'></img>";
        return str;
    },
    timeRenderer:function(v,m,r){
    	if(r.get("isallday")==true){
    	return "";
    	}else{
    		if(!v||!(v instanceof Date)) return v;
        	return new Date(v.getTime()+1*(v.getTimezoneOffset()*60000+Wtf.pref.tzoffset)).format(WtfGlobal.getLoginUserTimeFormat());
    	}
    },
    gridCellClick : function(grid, ri, e) {

        if(e.target.className == "clicktoshowcomment") {
            var selectedRec = grid.getStore().getAt(ri);
           // var commentlist = getDocsAndCommentList(selectedRec, "activityid",this.id,undefined,'Activity',undefined,undefined,undefined,undefined,0);
            Wtf.Ajax.requestEx({
            	method: "POST",
                url: Wtf.req.springBase+"common/DetailPanel/getComment.do",
                params:{ module: this.customParentModName,
            			 id:selectedRec.data.activityid
            	}
            },this,
            function(response) {
            	if(response.comment)
                {
             	   var commentlist=response.comment;
             	   if(commentlist.length>0){
                        var newData = Wtf.commentList(commentlist);
                         Wtf.showNotesLinksContainPanel(newData, e, selectedRec.get("activityid"), ri);
                     }
                }
            },
            function(response) {
            	
            });

        }
        if(e.target.className == "showMandatoryFields") {
            Wtf.emptyMandatoryFields(grid,ri);
        }
    },
    beforeEdit :function(e){
        if(e.record.data.isallday && (e.field =='starttime' || e.field =='endtime')) {
            ResponseAlert(87);
            return false;
        }
    },

    sortChange:function(grid,sortInfo, sortFlag){
        this.sortInfo = sortInfo;
    },
    validateEdit:function(e){
    	
    	if(e.value==''||e.value==undefined){
			e.cancel=true;
			return;
		}
    	if(e.field=='startdate'){
    		var d=new Date().clearTime().getTime();var t=8*60*60*1000;
    		if(e.originalValue && e.value instanceof Date){ // if date field edited
    			d = e.originalValue.getTime()-e.originalValue.clearTime(true).getTime();
    			e.value=new Date(d+e.value.getTime());
    		}
    		else if(typeof e.value=='string'){ // if time field edited
                if(e.originalValue) { // if time field having some old value i'e also having valid date value'
                    d = e.record.get("startdate").clearTime(true).getTime();
                }
                e.value=new Date(d+Date.parseDate(e.value,WtfGlobal.getLoginUserTimeFormat()).getTime()-new Date().clearTime(true).getTime());
    		}
    	}
    	if(e.field=='enddate'){
    		var d=new Date().clearTime().getTime();var t=9*60*60*1000;
    		if(e.originalValue && e.value instanceof Date){
    			d = e.originalValue.getTime()-e.originalValue.clearTime(true).getTime();
    			e.value=new Date(d+e.value.getTime());
    		}
    		else if(typeof e.value=='string'){
                if(e.originalValue) {
                    d = e.record.get("enddate").clearTime(true).getTime();
                }
                e.value=new Date(d+Date.parseDate(e.value,WtfGlobal.getLoginUserTimeFormat()).getTime()-new Date().clearTime(true).getTime());
    		}
    	}
    	if(e.field=="startdate" && e.value > e.record.data.enddate){
        	ResponseAlert(86);
            return false;
        } else if(e.field=="enddate" && e.record.data.startdate > e.value) {
        	ResponseAlert(86);
        	return false;
        }
    },
    storeLoad:function(){
        Wtf.updateProgress();
        this.highLightActivity();
    },
    highLightActivity : function(){
        if(this.highLightId!=undefined){
            this.EditorGrid.getSelectionModel().selectRow(Wtf.highLightSearch(this.highLightId,this.EditorStore,"activityid"));
            this.highLightId=undefined;
        }
    },
    PrintPriview : function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var fromdate="";
        var todate="";
        var report="crm";

        var exportUrl = Wtf.req.springBase;
        if(this.sortInfo != undefined) {
            var field = this.sortInfo.field;
            if(field!=undefined && (field=="createdon"||field=="startdate"||field=="enddate")){
                if(field=="createdon")
                    field="createdOn";
                else if(field=="startdate") {
                    field="startDate";
                } else {
                    field="endDate";
                }
            }
            var dir = this.sortInfo.direction;
        }
        exportUrl += "Activity/action/activityExport.do";
        var name;
        if (this.urlFlag==150) {
            name="AccountActivity";
        } else if(this.urlFlag==151){
            name="LeadActivity";
        } else if(this.urlFlag==152){
            name="ContactActivity";
        } else if(this.urlFlag==155){
            name="CaseActivity";
        } else if(this.urlFlag==153){
            name="OppActivity";
        } else if(this.urlFlag==154){
            name="CampaignActivity";
//            this.titlename="Campaign's Activities";
        }
        exportWithTemplate(this,type,name,fromdate,todate,report+this.relatedtonameid,exportUrl,undefined,undefined,undefined,field,dir);
    },
    gridRowClick:function(grid,rowIndex){
        WtfGlobal.enableDisableTbBtnArr(this.toolbarItems, this.EditorGrid, this.tbSingle, this.tbMulti, this.tbDefault);
        WtfGlobal.enableDisableTbBtnArr(this.tbarArchiveArray, this.EditorGrid, [0,1], [0,1], [1]);
    },

    SelChange:function(){
        var s = this.EditorGrid.getSelectionModel().getSelections();
        var selectedRec="";
        if(s.length == 1){
            selectedRec=this.EditorGrid.getSelectionModel().getSelected();
        }
        getDocsAndCommentList(selectedRec, "activityid",this.id,undefined,'Activity');
        if(s.length == 1 && selectedRec.data.activityid!="0"){
        	var updownCompE = Wtf.getCmp(this.id+'CRMupdownCompo');
        	enableButt(this,updownCompE,false,false);
        }else
        	disableButt(this);

        this.gridRowClick();
        if(this.deleteAct.disabled==false)
            this.deleteAct.setTooltip(WtfGlobal.getLocaleText("crm.activity.delselected"));//'Delete the selected activities.');
        else 
            this.deleteAct.setTooltip(WtfGlobal.getLocaleText("crm.editor.toptoolbar.deleteBTN.ttip"));//'Select row(s) to delete.');
    },

    getDetailPanel:function()
    {
        this.detailPanel = new Wtf.DetailPanel({
            grid:this.EditorGrid,
            Store:this.EditorStore,
            modulename:'activity',
            keyid:'activityid',
            height:200,
            mapid:7,
            id2:this.id,
            moduleName:'Activity',
            detailPanelFlag:(this.archivedFlag==1?true:false)
        });
    },

    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.EditorColumn,
            module : Wtf.crmmodule.activity
        });
    },

    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.AdvanceSearchBtn.disable();
        this.doLayout();
    },
    clearStoreFilter:function(){
        this.EditorStore.baseParams = {
            flag:this.urlFlag,
            mapid:this.relatedtonameid,
            module:this.Rrelatedto,
            isarchive:this.newFlag==3?true:false
        }
        this.EditorStore.load({
            params:{
                ss: this.quickSearchTF.getValue(),
                start:0,
                limit: this.EditorGrid.getBottomToolbar().pageSize
                }
            });
    this.searchJson="";
    this.objsearchComponent.hide();
    this.AdvanceSearchBtn.enable();
    this.doLayout();
},
filterStore:function(json){
    this.searchJson=json;
    this.EditorStore.baseParams = {
        flag:this.urlFlag,
        mapid:this.relatedtonameid,
        isarchive:this.newFlag==3?true:false,
        module:this.Rrelatedto,
        searchJson:this.searchJson
    }
    this.EditorStore.load({
        params:{
            ss: this.quickSearchTF.getValue(),
            start:0,
            limit: this.EditorGrid.getBottomToolbar().pageSize
            }
        });
},

getStores:function(){  
    var permData = [];
    var arr=[];
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Account, Wtf.Perm.Account.manage)){
        arr = ["Account","Account"];
        permData.push(arr);
    }
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Opportunity, Wtf.Perm.Opportunity.manage)){
        arr = ["Opportunity","Opportunity"];
        permData.push(arr);
    }
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Lead, Wtf.Perm.Lead.manage)){
        arr = ["Lead","Lead"];
        permData.push(arr);
    }
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Contact, Wtf.Perm.Contact.manage)){
        arr = ["Contact","Contact"];
        permData.push(arr);
    }
    if(!WtfGlobal.EnableDisable(Wtf.UPerm.Case, Wtf.Perm.Case.manage)){
        arr = ["Case","Case"];
        permData.push(arr);
    }

    this.relatedToStore = new Wtf.data.SimpleStore({
        fields: ['id','name'],
        data : permData
    });
    this.flagStore = new Wtf.data.SimpleStore({
        fields: ['id','name'],
        data : [
        ["Task","Task"],
        ["Event","Event"],
        ["Phone Call","Phone Call"]
        ]
    });

    chkownerload(Wtf.crmmodule.activity);
    chkpriorityload();
    chktasktypeload();
    chktaskstatusload();
    chkreminderload();

    var exportRecord = new Wtf.data.Record.create([
    {
        name: "name",
        mapping:'cname'
    },{
        name: "id",
        mapping:'cid'
    }
    ]);
    var exportRecordReader = new Wtf.data.KwlJsonReader1({
        root: "data",
        totalProperty: 'count'
    }, exportRecord);

   this.exportDS = new Wtf.data.Store({
        url: Wtf.calReq.cal + "getCalendarlist.do",
        reader: exportRecordReader,
        method: 'POST',
        baseParams: {
            userid: loginid
        }
    });

    this.exportDS.load();

},
newGrid:function() 
{  
    var newFlag=1;
},

//saveData:function(row,record,field, e){
//    var rData = record.data;
//    var auditStr=Wtf.SpreadSheet.constructAuditStr(this.EditorGrid,row, e.column, e.value, e.originalValue,record);
//
//    var jsondata="";
//    var validFlag=1;
//    if(rData.statusid=="" || rData.flag.trim()==""|| rData.startdate=="" || rData.enddate=="" || new Date(rData.startdate)>new Date(rData.enddate) || rData.ownerid.trim()=="" || rData.relatedto.trim()=="" || rData.relatednameid.trim()=="" )
//    {
//        validFlag=0;
//    }
//    else if(rData.statusid=="" || rData.flag.trim()==""|| rData.ownerid.trim()=="" || rData.relatedto.trim()=="" || rData.relatednameid.trim()==""  || rData.startdate=="" || rData.enddate=="" || new Date(rData.startdate)==new Date(rData.enddate) ) {
//          validFlag=0;
//   }
//    var temp=rData.ownerid;
//    if(temp=="")
//    {
//        record.set('ownerid',loginid);
//    }
//    if(rData.starttime=="") {
//        rData.starttime=WtfGlobal.setDefaultValueTimefield()
//    }
//    if(rData.endtime=="") {
//        rData.endtime=WtfGlobal.setDefaultValueEndTimefield()
//    }
//
//    if(rData.validflag != validFlag) record.set('validflag',validFlag);
//    jsondata+='{"activityid":"' +rData.activityid + '",';
//    jsondata+='"ownerid":"' +rData.ownerid + '",';
//    jsondata+='"subject":"' +rData.subject+ '",';
//    jsondata+='"auditstr":"' +auditStr+ '",';
//    jsondata+='"flag":"' +rData.flag + '",';
//    jsondata+='"relatedtoid":"' +rData.relatedto+ '",';
//    jsondata+='"relatedtoold":"'+rData.relatedtoold+'",';
//    jsondata+='"relatedtonameid":"' +rData.relatednameid+ '",';
//    jsondata+='"phone":"' +rData.phone +'",';
//    jsondata+='"statusid":"' +rData.statusid +'",';
//    jsondata+='"priorityid":"' + rData.priorityid+ '",';
//    jsondata+='"startdate":' + (rData.startdate.getTime?rData.startdate.getTime():new Date().getTime())+ ',';
//    jsondata+='"enddate":' + (rData.enddate.getTime?rData.enddate.getTime():new Date().getTime())+ ',';
//    jsondata+='"createdon":' + rData.createdon.getTime()+ ',';
//    jsondata+='"calendarid":"' + rData.calid + '",';
//    jsondata+='"validflag":"' +validFlag+ '",';
//    jsondata+='"dirtyfield":"' + field + '",';
//    jsondata+='"typeid":"' + rData.typeid +  '"';
//    jsondata+=this.spreadSheet.getCustomColumnData(rData,false);
//    jsondata+= '},';
//    var trmLen = jsondata.length - 1;
//    var finalStr = jsondata.substr(0,trmLen);
//    Wtf.Ajax.requestEx({
//
//        url: Wtf.req.springBase+'Activity/action/saveActivity.do',
//        params:{
//            jsondata:finalStr,
//            type:this.newFlag,
//            flag:82
//        }
//    },
//    this,
//    function(res)
//    {
//        if(res.ID) {
//            if(validFlag==1)bHasChanged = true;
//            rData.activityid=res.ID;
//
//            if(field=='flag' && validFlag==1)
//                Wtf.getCmp("tree").getLoader().load(Wtf.getCmp("tree").root);
//
//            var obj = Wtf.getCmp(Wtf.moduleWidget.topactivity);
//            if(obj!=null) {
//                obj.callRequest("","",0);
//                Wtf.refreshUpdatesAll();
//            }
//            obj = Wtf.getCmp(Wtf.moduleWidget.activity);
//            if(obj!=null) {
//                obj.callRequest("","",0);
//                Wtf.refreshUpdatesAll();
//            }
//
//        }
//    },
//    function(res)
//    {
//        WtfComMsgBox(202,1);
//    }
//    )
//
//},

afterValidRecordSaved : function (res,data) {
    if(data.record.get(Wtf.SpreadSheetGrid.VALID_KEY) == 1) {
        bHasChanged = true;
        var refresh = false;
        var obj=Wtf.getCmp(Wtf.moduleWidget.activity);
        if(obj!=null){
            obj.callRequest("","",0);
            refresh=true;
        }
        obj=Wtf.getCmp(Wtf.moduleWidget.topactivity);
        if(obj!=null){
            obj.callRequest("","",0);
            refresh=true;
        }
        if(refresh==true)
        	Wtf.refreshUpdatesAll();
        Wtf.getCmp("tree").getLoader().load(Wtf.getCmp("tree").root);
    }
},


activityDelete:function()
{
    Wtf.deleteGlobal(this.EditorGrid,this.EditorStore,'Activities',"activityid","activityid",'Activity',38,39,40);
},
exportfile: function(type) {
    if(this.searchJson==null) {
        this.searchJson = "";
    }
    var fromdate="";
    var todate="";
    var report="crm";
    var flag = this.urlFlag;
    var exportUrl = Wtf.req.springBase;
    exportUrl += "Activity/action/activityExport.do";
    if(this.sortInfo != undefined) {
        var field = this.sortInfo.field;
        if(field!=undefined && (field=="createdon"||field=="startdate"||field=="enddate")){
            if(field=="createdon")
                field="createdOn";
            else if(field=="startdate") {
                field="startDate";
            } else {
                field="endDate";
            }
        }
        var dir = this.sortInfo.direction;
    }
    var name;
    if (this.urlFlag==150) {
        name="AccountActivity";
    } else if(this.urlFlag==151){
        name="LeadActivity";
    } else if(this.urlFlag==152){
        name="ContactActivity";
    } else if(this.urlFlag==155){
        name="CaseActivity";
    } else if(this.urlFlag==153){
        name="OppActivity";
    } else if(this.urlFlag==154){
        name="CampaignActivity";
    }
    exportWithTemplate(this,type,name,fromdate,todate,report+this.relatedtonameid,exportUrl,flag,undefined,undefined,field,dir);
},
exportSelected: function(type) {
    var report="crm";
    var name = "";
    var flag = this.urlFlag;
    var exportUrl = Wtf.req.springBase;
    exportUrl += "Activity/action/activityExport.do";
    if (this.urlFlag==150) {
        name="AccountActivity";
    } else if(this.urlFlag==151){
        name="LeadActivity";
    } else if(this.urlFlag==152){
        name="ContactActivity";
    } else if(this.urlFlag==155){
        name="CaseActivity";
    } else if(this.urlFlag==153){
        name="OppActivity";
    } else if(this.urlFlag==154){
        name="CampaignActivity";
    }
    report += this.relatedtonameid;
    var fromdate="";
    var todate="";
    var selArr = [];
    if(this.sortInfo != undefined) {
        var field = this.sortInfo.field;
        if(field!=undefined && (field=="createdon"||field=="startdate"||field=="enddate")){
            if(field=="createdon")
                field="createdOn";
            else if(field=="startdate") {
                field="startDate";
            } else {
                field="endDate";
            }
        }
        var dir = this.sortInfo.direction;
    }
    selArr = this.EditorGrid.getSelectionModel().getSelections();
    var jsondata = "";
    for(var i=0;i<selArr.length;i++)
    {
        if(selArr[i].get("validflag") != -1 && selArr[i].get("validflag") != 0) {
            jsondata+="{'id':'" + selArr[i].get('activityid') + "'},";
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
showArchived:function() {
    var panel=Wtf.getCmp('ActivityArchivePanel'+this.relatedtonameid);

    this.archiveUrlFlag=81;
    var tipTitle="Archived Activities";
    var qtitle="Archived";
    if(this.Rrelatedto=="Lead" ||this.Rrelatedto=="Contact" ||this.Rrelatedto=="Account"||this.Rrelatedto=="Opportunity"||this.Rrelatedto=="Case"||this.Rrelatedto=="Campaign"  ){
        this.archiveUrlFlag=this.urlFlag;
        tipTitle=tipTitle=this.RelatedRecordName+"'s Archived Activities";
        qtitle=this.Rrelatedto;
    }
    var title = Wtf.util.Format.ellipsis(tipTitle,18);
    if(panel==null) {
        panel=new Wtf.activityEditor({
            border:false,
            title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+qtitle+"'>"+title+"</div>",
            layout:'fit',
            closable:true,
            scope:this,
            modName : "ActivityArchived",
            customParentModName : "Activity",
            id:'ActivityArchivePanel'+this.relatedtonameid,
            iconCls:getTabIconCls(Wtf.etype.archived),
            newFlag:3,
            arcFlag:1,
            parenturlFlag:this.urlFlag,
            parentId:this.EditorGrid.getId(),
            rFlag:0,
            Rrelatedto:(this.Rrelatedto?this.Rrelatedto:''),
            relatedtonameid:this.relatedtonameid,
            urlFlag:this.archiveUrlFlag,
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
    if(this.parentId == undefined) {
        data={
            a:a,
            tbarArchive:this.tbarArchive,
            EditorGrid:this.EditorGrid,
            title:'Activity',
            plural:'Activities',
            keyid:'id',
            valueid:"activityid",
            table:'Activity',
            GridId:'ActivityGrid',
            homePanId:'ActivityHomePanel',
            archivedPanId:'ActivityArchivePanel',
            name:"name",
            valueName:"flag"
        }
            
    } else {
        var temp=this.parentId.split('ActivityGrid');
        if(this.parenturlFlag==151) {
            var data={
                a:a,
                tbarArchive:this.tbarArchive,
                EditorGrid:this.EditorGrid,
                title:'Activity',
                plural:'Activities',
                keyid:'id',
                valueid:"activityid",
                table:'Activity',
                GridId:'ActivityGrid',
                homePanId:temp[1],
                archivedPanId:'ActivityArchivePanel'+this.relatedtonameid,
                name:"name",
                valueName:"flag"
            }

        } else if(this.parenturlFlag==152) {
            data={
                a:a,
                tbarArchive:this.tbarArchive,
                EditorGrid:this.EditorGrid,
                title:WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Activity',
                plural:'Activities',
                keyid:'id',
                valueid:"activityid",
                table:'Activity',
                GridId:'ActivityGrid',
                homePanId:temp[1],
                archivedPanId:'ActivityArchivePanel'+this.relatedtonameid,
                name:"name",
                valueName:"flag"
            }
        } else if(this.parenturlFlag==150) {
            data={
                a:a,
                tbarArchive:this.tbarArchive,
                EditorGrid:this.EditorGrid,
                title:WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Activity',
                plural:'Activities',
                keyid:'id',
                valueid:"activityid",
                table:'Activity',
                GridId:'ActivityGrid',
                homePanId:temp[1],
                archivedPanId:'ActivityArchivePanel'+this.relatedtonameid,
                name:"name",
                valueName:"flag"
            }
        } else if(this.parenturlFlag==153) {
            data={
                a:a,
                tbarArchive:this.tbarArchive,
                EditorGrid:this.EditorGrid,
                title:WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Activity',
                plural:'Activities',
                keyid:'id',
                valueid:"activityid",
                table:'Activity',
                GridId:'ActivityGrid',
                homePanId:temp[1],
                archivedPanId:'ActivityArchivePanel'+this.relatedtonameid,
                name:"name",
                valueName:"flag"
            }
        } else if(this.parenturlFlag==155) {
            data={
                a:a,
                tbarArchive:this.tbarArchive,
                EditorGrid:this.EditorGrid,
                title:WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Activity',
                plural:'Activities',
                keyid:'id',
                valueid:"activityid",
                table:'Activity',
                GridId:'ActivityGrid',
                homePanId:temp[1],
                archivedPanId:'ActivityArchivePanel'+this.relatedtonameid,
                name:"name",
                valueName:"flag"
            }
        } else if(this.parenturlFlag==154) {
            data={
                a:a,
                tbarArchive:this.tbarArchive,
                EditorGrid:this.EditorGrid,
                title:WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Activity',
                plural:'Activities',
                keyid:'id',
                valueid:"activityid",
                table:'Activity',
                GridId:'ActivityGrid',
                homePanId:temp[1],
                archivedPanId:'ActivityArchivePanel'+this.relatedtonameid,
                name:"name",
                valueName:"flag"
            }
        } else {
            data={
                a:a,
                tbarArchive:this.tbarArchive,
                EditorGrid:this.EditorGrid,
                title:WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Activity',
                plural:'Activities',
                keyid:'id',
                valueid:"activityid",
                table:'Activity',
                GridId:'ActivityGrid',
                homePanId:'ActivityHomePanel',
                archivedPanId:'ActivityArchivePanel'+this.relatedtonameid,
                name:"name",
                valueName:"flag"
            }
        }
    }
    var mod = "CrmActivityMaster";
    var audit = "95";
    var auditMod = "Activity";
    Wtf.ArchivedGlobal(data, mod, audit,auditMod);
},
temp: function(){
    Wtf.highLightGlobal(this.highLightId,this.EditorGrid,this.EditorStore,"activityid");
}
}); 
