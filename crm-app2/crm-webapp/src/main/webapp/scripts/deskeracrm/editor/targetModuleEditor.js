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
Wtf.targetModuleEditor = function (config){
    Wtf.targetModuleEditor.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.targetModuleEditor,Wtf.Panel,{
    border:false,
    initComponent: function(config){
        Wtf.targetModuleEditor.superclass.initComponent.call(this,config);

        Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
        initializeUndoVar(this);

        this.getStores();

        this.getEditorGrid();

        this.getAdvanceSearchComponent();

        this.targetModulepan= new Wtf.Panel({
            layout:'border',
            title:"Targets",
            iconCls:getTabIconCls(Wtf.etype.lead),
            border:false,
            id:this.id+'targetModulepan',
            items:[
            this.objsearchComponent,
            {
                region:'center',
                layout:'fit',
                border:false,
                items:[this.spreadSheet]
            }
            ]
        });
        this.mainTab=new Wtf.TabPanel({
           id:this.id+"targetTabPanel",
           scope:this,
           border:false,
           resizeTabs: true,
           minTabWidth: 155,
           enableTabScroll: true,
           items:[this.targetModulepan]
        });

        if(this.archivedFlag==1){
            this.add(this.targetModulepan);
        }else{
            this.add(this.mainTab);
            this.mainTab.setActiveTab(this.targetModulepan);
        }
        this.doLayout();
        this.targetModulepan.on("activate",function(){
            Wtf.getCmp("TargetModuleHomePaneltargetModulepan").doLayout()
        },this)
        this.objsearchComponent.on("filterStore",this.filterStore, this);
        this.objsearchComponent.on("clearStoreFilter",this.clearStoreFilter, this);

    },
    getEditorGrid : function (){
        x=[
        {
            name:'targetModuleid'
        },
        {
            name:'targetModuleownerid'
        },
        {
            name:'firstname'
        },
        {
            name:'lastname'
        },
        {
            name:'phoneno'
        },
        {
            name:'mobileno'
        },
        {
            name:'email'
        },
        {
            name:'address'
        },
        {
            name:'description'
        },
        {
            name:"createdon"
        },
        {
            name:"cellStyle"
        },
        {
            name:'validflag'
        }
        ];

        this.EditorRec = new Wtf.data.Record.create(x);

        var EditorReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.EditorRec);

        this.EditorStore = new Wtf.data.Store({
            url: Wtf.req.springBase+"Target/action/getTargets.do",
            pruneModifiedRecords:true,
            baseParams:{
                flag:this.urlFlag,
                mapid:this.mapid,
                isarchive:this.newFlag==3?true:false
            },
            method:'post',
            reader:EditorReader
        });
        this.EditorColumnArray =[
            {
                header:'',
                width:30,
                id:'validflag',
                dataIndex:'validflag',
                dbname:'c.validflag',
                renderer:WtfGlobal.renderValidFlagAndComment
            },
            {
                header:"First Name ",
                tip:'First Name',
                id:'firstname',
                pdfwidth:60,
                sortable: true,
                sheetEditor:{
                    xtype : 'textfield',
                    maxLength : 25
                },
                dbname:'c.firstname',
                xtype:'textfield',
                dataIndex: 'firstname'
            },
            {
                header:"Last Name *",
                tip:'Last Name',
                id:'lastname',
                pdfwidth:60,
                sortable: true,
                sheetEditor:{
                    xtype : 'textfield',
                    maxLength : 25
                },
                dbname:'c.lastname',
                xtype:'textfield',
                dataIndex: 'lastname'
            },
            {
                header:"Owner *",
                tip:'Owner',
                id:'owner',
                title:'owner',
                pdfwidth:60,
                sortable: true,
                sheetEditor : {xtype:"combo", store:Wtf.ownerStore, useDefault:true},
                dataIndex: 'targetModuleownerid',
                dbname:'c.usersByUserid.userID',
                cname:'owner',
                xtype:'combo'
            },
            {
                header:"Phone",
                tip:'Phone',
                align:'right',
                id:'phone',
                pdfwidth:60,
                sortable: true,
                sheetEditor : {
                    xtype : "textfield",
             //       regex : Wtf.PhoneRegex,
                maxLength : 100,
                regexText:Wtf.MaxLengthText+"100"
                },
                dbname:'c.phoneno',
                xtype:'textfield',
                dataIndex: 'phoneno',
                renderer:WtfGlobal.renderContactToCall
            },
            {
                header:"Mobile",
                tip:'Mobile',
                align:'right',
                id:'mobile',
                pdfwidth:60,
                sortable: true,
                sheetEditor : {
                    xtype : "textfield",
                 //   regex : Wtf.PhoneRegex,
                    maxLength : 100,
                   regexText:Wtf.MaxLengthText+"100"
                },
                dbname:'c.mobileno',
                xtype:'textfield',
                dataIndex: 'mobileno',
                renderer:WtfGlobal.renderContactToCall
            },
            {
                header:"Email",
                tip:'Email',
                id:'email',
                pdfwidth:60,
                sortable: true,
                sheetEditor : {
                    xtype : "textfield",
                    regex: Wtf.ValidateMailPatt,
                    maxLength:50
                },
                dataIndex: 'email',
                dbname:'c.email',
                xtype:'textfield',
                renderer:WtfGlobal.renderEmailTo
            },
            {
                header:"Address",
                id:'address',
                tip:'Address',
                pdfwidth:60,
                sortable: true,
                sheetEditor : {xtype:"textfield", maxLength:100},
                dataIndex: 'address',
                renderer : function(val) {
                    return "<div wtf:qtip=\""+val+"\"wtf:qtitle='Address'>"+val+"</div>";
                }
            },
            {
                header:"Description",
                id:'description',
                tip:'Description',
                pdfwidth:60,
                sortable: true,
                sheetEditor : {xtype:"textfield", maxLength:1024},
                dataIndex: 'description',
                renderer : function(val) {
                    return "<div wtf:qtip=\""+val+"\"wtf:qtitle='Description'>"+val+"</div>";
                }
            }
            ];

        this.tbarArchiveArray = Wtf.archivedMenuArray(this,"Target");
        this.tbarArchive = Wtf.archivedMenuButtonA(this.tbarArchiveArray,this,"Target");

        this.toolbarItems = [];
        this.tbSingle = [];
        this.tbMulti = [];
        this.tbDefault = [];
        var tbIndex = 0;

        this.deleteTarget= new Wtf.Toolbar.Button({
                        text:"Delete",
                        scope:this,
                        tooltip:{text:'Select row(s) to delete.'},
                        iconCls:getTabIconCls(Wtf.etype.delet),
                        disabled:(this.newFlag==1?true:false),
                        handler:this.targetModuleDelete
        });
        this.AdvanceSearchBtn = new Wtf.Toolbar.Button({
                    text : "Advanced Search",
                    id:'advanced34',// In use, Do not delete
                    scope : this,
                    tooltip:'Search for multiple terms in multiple fields.',
                    handler : this.configurAdvancedSearch,
                    iconCls : 'pwnd searchtabpane'
        });
        this.quickSearchTF = new Wtf.KWLTagSearch({
            width: 220,
            id:'quick34',//ID in use, do not delete
            emptyText:"Search by Name & Owner",
            Store:this.EditorStore,
			parentGridObj: this
        });

        this.importTargetsA =Wtf.importMenuArray(this,"Target",this.EditorStore);
        this.importTargets = Wtf.importMenuButtonA(this.importTargetsA,this,"Target");
        
        this.toolbarItems.push(this.quickSearchTF);
            this.tbSingle.push(tbIndex);
            this.tbMulti.push(tbIndex);
            this.tbDefault.push(tbIndex);
            tbIndex++;
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.manage)) {
            if(this.newFlag==2) {
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.del)) {
                    this.toolbarItems.push(this.deleteTarget);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.archive)) {
                    this.toolbarItems.push(this.tbarArchive);
                        this.tbDefault.push(tbIndex);
                        tbIndex++;
                }

                if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.exportt)) {
                    var exp = exportButton(this,"Target(s)",34);
                    this.toolbarItems.push(exp);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        this.tbDefault.push(tbIndex);
                        tbIndex++;
                }
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.importt)) {
                    this.toolbarItems.push(this.importTargets);
                        this.tbSingle.push(tbIndex);
                        this.tbDefault.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                this.toolbarItems.push(this.AdvanceSearchBtn);
                    this.tbSingle.push(tbIndex);
                    this.tbDefault.push(tbIndex);
                    this.tbMulti.push(tbIndex);
                    tbIndex++;
                    var addNew=getAddNewButton(this);
                    this.toolbarItems.push(addNew);
            }
            if(this.newFlag==3){
                if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.del)) {
                    this.toolbarItems.push(this.deleteTarget);
                        this.tbSingle.push(tbIndex);
                        this.tbMulti.push(tbIndex);
                        tbIndex++;
                }
                this.tbarUnArchive = Wtf.archivedMenuButtonB(this,"Target");
                this.toolbarItems.push(this.tbarUnArchive);
                this.tbSingle.push(tbIndex);
                this.tbMulti.push(tbIndex);
                tbIndex++;
            }
        }
        if(this.clearFlag!=undefined){
            this.toolbarItems.push('->');
            var help=getHelpButton(this,34);
            this.toolbarItems.push(help);
        }
        this.spreadSheet = new Wtf.SpreadSheet.Panel({
            cmArray:this.EditorColumnArray,
            store:this.EditorStore,
            moduleName : this.customParentModName,
            isEditor:(this.archivedFlag!=1),
            tbar:this.toolbarItems,
            pagingFlag : true,
            quickSearchTF : this.quickSearchTF,
            parentGridObj : this,
            keyid : 'targetModuleid',
            gid:'TargetModuleGrid'+this.id,
            id:'TargetModuleSheet'+this.id
        });

        this.EditorStore.on("beforeload",function(){
            Wtf.commonWaitMsgBox("Loading data...");
        },this);
        this.EditorStore.on("loadexception",function(){
            Wtf.updateProgress();
        },this);
        
        this.EditorGrid = this.spreadSheet.getGrid();
        this.EditorColumn = this.spreadSheet.getColModel();
        this.EditorStore.load({
                params:{
                    start:0,
                    limit:25
                }
        });
        if(this.newFlag==2){//new

            if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.manage)) {
                this.EditorGrid.on("afteredit",this.fillGridValue,this);
                this.EditorGrid.on("beforeedit",this.beforeEdit,this);
            } else{
                this.EditorGrid.isEditor = false;
            }
        }
        this.EditorStore.on("load",this.storeLoad,this);
        
        this.gridRowClick();
        this.EditorGrid.getSelectionModel().on("selectionchange",this.SelChange,this);
        this.EditorGrid.on("sortchange",this.sortChange,this);
        this.tbarArchive.on('click',function() {
            if(this.deleteTarget.disabled==false)
                this.tbarArchiveArray[0].setTooltip("Archive the selected target.");
            else
                this.tbarArchiveArray[0].setTooltip("Select row(s) to send in Archive repository.");
        }, this);
    },

    beforeEdit :function(e){
        if(e.record.get('targetModuleid')=="0" && e.record.get('validflag') != -1){
            ResponseAlert(200);
            return false;
        }
    },

    sortChange:function(grid,sortInfo, sortFlag){
        if(this.newFlag != 3 || sortFlag) {
            Wtf.arrangeGridNumbererRemove(this.EditorGrid.getStore().indexOf(this.newRec), this.EditorGrid);
            this.EditorGrid.getStore().remove(this.newRec);
            this.addNewRec();
            Wtf.arrangeGridNumbererAdd(0,this.EditorGrid)
        }
    },
    gridRowClick:function(e,rowIndex) {
        WtfGlobal.enableDisableTbBtnArr(this.toolbarItems, this.EditorGrid, this.tbSingle, this.tbMulti, this.tbDefault);
        WtfGlobal.enableDisableTbBtnArr(this.tbarArchiveArray, this.EditorGrid, [0,1], [0,1], [1]);
    },

    SelChange:function(){
        this.gridRowClick();
        if(this.deleteTarget.disabled==false) 
            this.deleteTarget.setTooltip('Delete the selected target(s).');
        else
            this.deleteTarget.setTooltip('Select row(s) to delete.');
    },

    exportfile: function(type) {
        if(this.searchJson==null) {
            this.searchJson = "";
        }
        var name="Targets";
        var fromdate="";
        var todate="";
        var report="crm"
        var exportUrl = Wtf.req.springBase+"Target/action/targetExport.do";
        exportWithTemplate(this,type,name,fromdate,todate,report, exportUrl);
    },


    storeLoad:function(){
        if(this.newFlag==2){//new
            this.addNewRec();
            Wtf.arrangeGridNumbererAdd(0,this.EditorGrid);
            this.totalCount = this.EditorStore.getTotalCount();
        }
        Wtf.updateProgress();
    },

    getAdvanceSearchComponent:function()
    {
        this.objsearchComponent=new Wtf.advancedSearchComponent({
            cm:this.EditorColumn
        });
    },

    configurAdvancedSearch:function(){
        this.objsearchComponent.show();
        this.doLayout();
    },
    clearStoreFilter:function(){
        this.EditorStore.baseParams = {
             flag:this.urlFlag,
             mapid:this.mapid,
             isarchive:this.newFlag==3?true:false
        }
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
        this.searchJson="";
        this.objsearchComponent.hide();
        this.doLayout();
    },
    filterStore:function(json){
        this.searchJson=json;
        this.EditorStore.baseParams = {
            flag:this.urlFlag,
            searchJson:this.searchJson,
            mapid:this.mapid,
            isarchive:this.newFlag==3?true:false
        }
        this.EditorStore.load({params:{ss: this.quickSearchTF.getValue(), start:0, limit: this.EditorGrid.getBottomToolbar().pageSize}});
    },

    getStores:function(){
        chkownerload();

    },
    addNewRec:function (){
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.TargetModule, Wtf.Perm.TargetModule.manage)) {
            this.newRec = new this.EditorRec({
                validflag:-1,
                targetModuleid:"0",
                targetModuleownerid:'',
                firstname:"",
                lastname:"",
                phoneno:"",
                mobileno:"",
                email:"",
                address:"",
                description:""
            });
            this.EditorStore.insert(0, this.newRec);
        }
    },
    fillGridValue:function (e){
        var count = e.grid.getStore().getCount();
        if(typeof e.value=="string"){
            if(e.row == 0 && e.value.trim()!="") {
                this.addNewRec();
                this.totalCount += 1;
            }
        } else if(e.row == 0 ) {
                this.addNewRec();
                this.totalCount += 1;
        }
        this.count = this.EditorStore.getCount()-1;
        Wtf.arrangeGridNumbererAdd(0,this.EditorGrid);
        this.validSave(e.row,e.record,e.field,e);
        this.spreadSheet.pP.updatePagingMsg(this.count, this.totalCount);

    },
   validSave:function(rowindex,record,field,e){
        var modifiedRecord=this.EditorStore.getModifiedRecords();
        if(modifiedRecord.length<1){
        //    WtfComMsgBox(900,0);
            return false;
        }
        this.saveData(rowindex,record,field,e);
    },
    saveData:function(rowindex,record,field,e){
        var rData = record.data;
        var auditStr=Wtf.SpreadSheet.constructAuditStr(this.EditorGrid,rowindex, e.column, e.value, e.originalValue,record);

        var jsondata="";
        var validFlag=1;

        var temp=rData.targetModuleownerid;
        if(temp=="")
        {
            record.set('targetModuleownerid', loginid);
        }
        if(rData.lastname.trim()=="") {
            validFlag=0;
        }
        if(rData.valiflag != validFlag) record.set('validflag',validFlag);
        
        jsondata+='{"targetModuleid":"' + rData.targetModuleid + '",';
        jsondata+='"targetModuleownerid":"' +rData.targetModuleownerid+ '",';
        jsondata+='"firstname":"' +rData.firstname+ '",';
        jsondata+='"lastname":"' +rData.lastname+ '",';
        jsondata+='"auditstr":"' +auditStr+ '",';
        jsondata+='"phone":"' +rData.phoneno +'",';
        jsondata+='"mobile":"' +rData.mobileno +'",';
        jsondata+='"email":"' + rData.email + '",';
        jsondata+='"address":"' +rData.address + '",';
   //     jsondata+='"createdon":"' + rData.createdon + '",';
        jsondata+='"validflag":"' +validFlag+ '",';
        jsondata+='"description":"' + rData.description + '"},';

        var trmLen = jsondata.length - 1;
        var finalStr = jsondata.substr(0,trmLen);
        rData.validflag=validFlag;
        Wtf.Ajax.requestEx({
            url: Wtf.req.springBase+"Target/action/saveTargets.do",
            params:{
                jsondata:finalStr,
                type:this.newFlag,
                flag:301
            }
        },
        this,
        function(res)
        {
            rData.targetModuleid=res.ID;            
        },
        function()
        {
            WtfComMsgBox(902,1);
        }
        )
    },

    targetModuleDelete:function()
    {
        Wtf.deleteGlobal(this.EditorGrid,this.EditorStore,'Target(s)',"targetModuleid","targetModuleid",'TargetModule',52,53,54);
    },

    ImportTargets :function(type){
        this.TargetsArry = ["cuserid","cusername","cemailid","caddress","ccontactno"];
        this.records =  Wtf.data.Record.create(this.TargetsArry);
        this.jreader = new Wtf.data.KwlJsonReader({
            root: 'data'
        },this.records);
        this.userds = new Wtf.data.Store({
            url : Wtf.req.springBase+"Target/action/getAllTargets.do",
            root:'data',
            reader:this.jreader,
            baseParams : {
                type: "alltargets",
                userid: loginid
            }
        });
        this.userdsFlag = true;
        this.userds.on("load",function(){
            var newresentry = new this.records({
                cuserid: '-1',
                cusername: 'Create New',
                cemailid:""
            });
            this.userds.insert(0, newresentry);
            this.userdsFlag = false;
        },this);
        this.userds.load();
        this.userCombo = new Wtf.form.ComboBox({
            store: this.userds,
            displayField: 'cusername',
            valueField: 'cusername',
            typeAhead: true,
            mode: 'local',
            forceSelection: true,
            emptyText: "Click to select",
            editable: true,
            triggerAction: 'all',
            selectOnFocus: true
        });
        this.userCombo.on("select",function(combo,record,index){
            if(record.data.cuserid == '-1')
                this.CreateNewTarget();
        },this);
        this.userCombo.on("blur",function(comboBox){
            var val = comboBox.lastQuery;
            if(comboBox.store.find("cusername",comboBox.getValue())==-1||comboBox.getValue()=='Create New'){
                comboBox.clearValue();
                comboBox.setValue("");
            }
        });
        this.listds = new Wtf.data.SimpleStore({
            fields: [{
                name:"userid"
            },{
                name:"username"
            },{
                name:"emailid"
            },{
                name:"address"
            },{
                name:"contactno"
            },{
                name:"cusername"
            }]
        });
        this.listcm = new Wtf.grid.ColumnModel([{
            header: "Imported Target User Name",
            dataIndex: 'username',
            renderer: this.displayName
        },{
            header: "Map With Crm User",
            dataIndex: 'cusername',
            editor: this.userCombo,
            renderer : Wtf.comboBoxRenderer(this.userCombo)
        }]);
        this.grid= Wtf.commonConflictWindowGrid('list'+this.id,this.listds,this.listcm)

        this.listcm1 = new Wtf.grid.ColumnModel([{
            header: "Imported Target User Name",
            dataIndex: 'username',
            width:430,
            renderer: this.displayName
        }]);
        this.grid1= Wtf.commonConflictWindowGrid('list1'+this.id,this.listds,this.listcm1)

        this.impWin1 = Wtf.commonUploadWindow(this,type);
        this.impWin1.show();
    },
    displayName:function(value,gridcell,record,d,e){
        var uname=(record.json.username).trim();
        return uname;
    },
    CreateNewTarget : function() {
        var rec = this.grid.getSelectionModel().getSelected();
        this.addExtTargetfunction(0,rec,1);
    },
    mappingCSV:function(Header,res,impWin1,delimiterType)
    {
        this.filename=res.FileName;

        this.mapCSV=new Wtf.csvMappingInterface({
            csvheaders:Header,
            modName:"Targets",
            impWin1:impWin1,
            delimiterType:delimiterType
        }).show();
        Wtf.getCmp("csvMappingInterface").on('importfn',this.importCSVfunc, this);

    },
    mapImportedRes : function(conflictWin,repOrig,resConf) {
        var nrecords=0;
        var temp_h="";
        var nrows=this.listds.getCount();
        var jsonData = "{userdata:[";
        for(var cnt =0 ;cnt< nrows;cnt++) {
            var urec = this.listds.getAt(cnt);
            var ind=this.userds.find("cusername",urec.data.cusername,0,true);

            if(repOrig==true){
                var uName=urec.json.username.trim();
                ind=this.userds.find("cusername",uName,0,true);
            }

            if(ind!=-1){
                nrecords++;
                var crec=this.userds.getAt(ind);
                jsonData +=  temp_h+"{user:\""+crec.data.cusername+"\",email:\""+crec.json.cemailid+"\",targetModuleid:\""+crec.json.cuserid+"\",userid:\""+urec.json.userid+"\",username:\""+urec.json.username+"\",emailid:\""+urec.json.emailid+"\",address:\""+urec.json.address+"\",contactno:\""+urec.json.contactno+"\"}";
                temp_h=",";
            }
        }
       jsonData +="]}";
        if(nrecords>0){
            Wtf.Ajax.requestEx({
                url : Wtf.req.springBase+"Target/action/repTarget.do",
                params: ({
                    type:"repTarget",
                    val: jsonData
                }),
                method: 'POST'
            },
            this,
            function(result, req){
                if(result!=null && result != "")
                WtfComMsgBox(458, 0);
                this.EditorStore.reload();
                conflictWin.close();

            },function(){
               conflictWin.close();
            });
        }
        else{
            WtfComMsgBox(470, 0);
            conflictWin.close();
        }
    },
    importCSVfunc:function(response,delimiterType)
    {
          Wtf.commonConflictWindow(this,response,"Target",this.filename,this.EditorStore,this.listds,this.grid,469,451,'../../images/leads.gif',"Null","Null",this.grid1,470,delimiterType);
    },
    addExtTargetfunction:function(action,record,flag){
        var windowHeading = action==0?"Add Target":"Edit Target";
        var windowMsg = action==0?"Enter new Target details":"Edit existing Target details";
        this.addExtTargetWindow = new Wtf.Window({
            title : action==0?"Add Target":"Edit Target",
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
                            url : Wtf.req.springBase+"Target/action/newTargetAddress.do",
                            params: ({
                                type:"newTargetAddress",
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
                            this.addExtTargetWindow.close();
                         },function(){
                            this.addExtTargetWindow.close();
                        });
                    }
                }
            },{
                text : WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                id:'cancelCreateUserButton',
                scope : this,
                handler : function(){
                    this.addExtTargetWindow.close();
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
                        fieldLabel: 'Email Id* ',
                        id:'tempEmailField',
                        name: 'emailid',
                        validator: WtfGlobal.validateEmail,
                        allowBlank:false,
                        renderer: WtfGlobal.renderEmailTo
                    },{
                        fieldLabel: 'Phone* ',
                        allowBlank:false,
                        id: "tempPhoneField",
                        name: 'phone'
                    },{
                        xtype:"textarea",
                        fieldLabel: 'Address ',
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
        this.addExtTargetWindow.show();
        if(record!=null){
            Wtf.getCmp('tempNameField').setValue(record.json.username);
            Wtf.getCmp('tempEmailField').setValue(record.json.emailid);
            Wtf.getCmp('tempPhoneField').setValue(record.json.contactno);
            Wtf.getCmp('tempAddField').setValue(record.json.address);
            Wtf.getCmp('tempContIdField').setValue(record.json.userid);
        }
    },
    showArchived:function() {
        var panel=Wtf.getCmp('TargetArchivePanel');
        if(panel==null) {
            panel=new Wtf.targetModuleEditor({
                border:false,
                title:'Archived Targets',
                modName : "TargetArchived",
                customParentModName : "Target",
                layout:'fit',
                closable:true,
                id:'TargetArchivePanel',
                iconCls:getTabIconCls(Wtf.etype.archived),
                newFlag:3,
                urlFlag:this.urlFlag,
                archivedFlag:1
            });
            this.mainTab.add(panel);
        }
        this.mainTab.setActiveTab(panel);
        this.mainTab.doLayout();
    },
    ArchiveHandler:function(a) {
        var data={a:a,tbarArchive:this.tbarArchive,EditorGrid:this.EditorGrid,title:'Target',keyid:'id',valueid:"targetModuleid",table:'TargetModule',GridId:'TargetModuleGrid',homePanId:'TargetModuleHomePanel',archivedPanId:'TargetArchivePanel', name:"name", valueName:"lastname"}
        var mod = "TargetModule";
        var audit = "305";
        var auditMod = "Target";
        Wtf.ArchivedGlobal(data, mod, audit,auditMod);
    },
    temp: function(){
        Wtf.highLightGlobal(this.highLightId,this.EditorGrid,this.EditorStore,"targetModuleid");
   }
});
