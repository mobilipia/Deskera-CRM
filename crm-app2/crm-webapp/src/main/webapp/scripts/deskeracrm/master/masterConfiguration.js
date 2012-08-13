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

Wtf.MasterConfigurator = function (config){
    Wtf.apply(this,config);
    Wtf.MasterConfigurator.superclass.constructor.call(this);
};

Wtf.extend(Wtf.MasterConfigurator,Wtf.ux.ClosableTabPanel,{
    isClosable:true,
    closeWindow:false,
    
    initComponent:function (){
        Wtf.MasterConfigurator.superclass.initComponent.call(this);
        this.upperflag=0;
        this.lowerflag=0;
        this.getMasterGrid();
        this.getMasterDataGrid();
        this.getSystemAdmin();
        this.mainPanel = new Wtf.Panel({
            layout:"border",
            border:false,
            items:[
            this.masterDataGrid,
            this.masterGrid,{
                border:false,
                region:'east',
                layout:'fit',
                split:true,
                    width:300,
                items:[
                this.systemPanel
                //      this.masterLinks
                ]
            }]
        });

        this.masterSm.on("selectionchange",function(){
            if(this.masterSm.getSelected()){
                var masterSel = this.masterGrid.getSelectionModel().getSelected();
                this.masterDataAdd.enable();
                this.masterEdit.enable();
                this.masterDataStore.load({
                    params:{
                        configid:masterSel.get("id"),
                        comboname:masterSel.get("name"),
                        customflag:masterSel.get("customflag")
                    }
                });
            } else {
                this.masterDataStore.removeAll();
                this.masterDataAdd.disable();
                this.masterDataEdit.disable();
                this.masterDataDelete.disable();
                this.masterEdit.disable();
            }
        },this);

        this.add(this.mainPanel);
        Wtf.getCmp("masterConfigTab").on("beforeclose",function(){
            if(this.upperflag==0 && this.lowerflag==0){
                return true;
            }else{
                Wtf.MessageBox.show({
                    title:this.title,
                    msg:this.closeMsg,
                    buttons:Wtf.MessageBox.YESNO,
                    animEl:'mb9',
                    fn:function(btn){
                        if(btn=="yes"){
                            Wtf.getCmp("masterConfigTab").ownerCt.remove(Wtf.getCmp("masterConfigTab"));
                        }
                    },
                    scope:this,
                    icon: Wtf.MessageBox.QUESTION
                });
                return false;
            }
        },this);
    },
    getMasterGrid:function (){
        Wtf.masterStore.load();

        this.quickSearchTF = new Wtf.KWLQuickSearchUseFilter({
            id : 'masterFilter'+this.id,
            width: 140,
            field : "name",
            emptyText:WtfGlobal.getLocaleText("crm.masterconfig.totoolbar.searchfieldEmptyText")//"Search Fields "
        });
        this.quickSearchTF.StorageChanged(Wtf.masterStore);

        this.masterColumn = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.masterconfig.fields.column"),//"Field"
                sortable:true,
                dataIndex:"name"
            },
            {
                header:WtfGlobal.getLocaleText("crm.masterconfig.modulename.column"),//"Module Name",
                sortable:true,
                dataIndex:"modulename"
            }
            ]);

        this.masterAdd = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.masterconfig.buttonCaption.AddMaster"),//"Add Master",
            handler:function (){
                this.AddMaster("Add");
            },
            scope:this
        });

        this.masterEdit = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.masterconfig.buttonCaption.EditMaster"),//"Edit Master",
            handler:function (){
                this.AddMaster("Edit");
            },
            scope:this,
            disabled:true
        });


        this.masterGrid = new Wtf.grid.GridPanel({
            sm:this.masterSm = new Wtf.grid.RowSelectionModel(),
            region:"west",
            width:300,
            store:Wtf.masterStore,
            sortable:true,
            cm:this.masterColumn,
            loadMask:true,
            viewConfig:{
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer("No records found.")
            },
            tbar:[this.quickSearchTF]
        //            ,
        //            bbar:[
        //                this.masterAdd,
        //                "-",
        //                this.masterEdit
        //            ]
        });
        var linkData = {links:[
        //                        {fn:"UpdateCommision()",text:"<span wtf:qtip='Commission Plan.'>"+"Commission Plan"+"</span>",viewperm:true}
                ]};
        var tpl = new Wtf.XTemplate(
            '<div class ="dashboardcontent" style=" background: url(&quot;../../images/bullet.gif&quot;) no-repeat scroll 0pt 0pt transparent;>',
            '<ul id="accMasterSettingPane">',
            '<tpl for="links">',
            '<tpl if="viewperm">',
            '<li id="wtf-gen215">',
            '<a onclick="{fn}" href="#" >{text}</a>',
            '</li>',
            '</tpl>',
            '</tpl>',
            '</ul>',
            '</div>'
            );
        this.masterLinks = new Wtf.Panel({
            region:"center",
            bodyStyle:'background:white;',
            layout:'fit',
            border: false,
            split: true,
            loadMask:true
        });
        this.masterLinks.on('render', function(){
            tpl.overwrite(this.masterLinks.body, linkData);
        }, this);

    },
    getMasterDataGrid:function (){
        this.masterDataRec = new Wtf.data.Record.create([
            {name:"id"},
            {name:"name"},
            {name:"parentid"},
            {name:"configid"},
            {name:"isEdit"},
            {name:"itemsequence"},
            {name:"percentStage"},
            {name:"hasAccess"},
            {name:"maxlength"}
        ]);

        this.masterDataReader = new Wtf.data.KwlJsonReader({
            root:"data",
            totalProperty:"count"
        },this.masterDataRec);

        this.masterDataStore = new Wtf.data.Store({
            url: "Common/CRMManager/getComboData.do",
            reader:this.masterDataReader
        });

        this.masterDataColumn = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),{
                header:WtfGlobal.getLocaleText("crm.masterconfig.combodatafield.columnheader"),//"Field",
                sortable:true,
                dataIndex:"name",
                renderer:this.validateRecord
            },{
                header:WtfGlobal.getLocaleText("crm.masterconfig.combodata.sequence.columnheader"),//"Sequence",
                sortable:true,
                dataIndex:"itemsequence",
                dbname:"d.itemsequence",
                renderer:function(val, cell, row, rowIndex, colIndex, ds) {
                    var target = row.data.targeted;
                    var storecount=ds.getTotalCount();
                    if(target!="" || target=="0"){
                        var str = "";
                        if(rowIndex<storecount-1)
                            str +=  '<div class=\'pwndCRM shiftrowdownIcon\'></div>';
                        if(rowIndex > 0)
                            str += ' <div class=\'pwndCRM shiftrowupIcon\' ></div>';
                    	
                        return str;
                    		
                    }
                     
                }
            }
            ]);

        this.masterDataAdd = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.masterconfig.buttonCaption.AddMasterData"),//"Add Master Data",
            iconCls:"pwnd addIcon",
            handler:function (){
                this.AddMasterData("Add");
            },
            disabled:true,
            scope:this
        });

        this.masterDataEdit = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.masterconfig.buttonCaption.EditMasterData"),//"Edit Master Data",
            iconCls:"pwnd editmasterIcon",
            handler:function (){
                this.AddMasterData("Edit");
            },
            scope:this,
            disabled:true
        });

        this.masterDataDelete = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.masterconfig.buttonCaption.DeleteMasterData"),//"Delete Master Data",
            iconCls:"pwnd deleteButtonIcon",
            handler:function (){
                Wtf.MessageBox.show({
                    title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),
                    msg:WtfGlobal.getLocaleText("crm.masterconfig.delmasterdata.confirmmsg"),//"Are you sure you want to delete selected master data?<br><br><b>Note: This data cannot be retrieved later.</b>",
                    icon:Wtf.MessageBox.QUESTION,
                    buttons:Wtf.MessageBox.YESNO,
                    scope:this,
                    fn:function(button){
                        if(button=='yes')
                        {
                            this.DeleteMasterData();
                        }
                    }
                });

            },
            scope:this,
            disabled:true
        });
        
        this.saveSequence = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.masterconfig.buttonCaption.SaveComboDataSeq"),//"Save Combodata Sequence",
            iconCls:"pwnd savemasterdatasequenceicon",
            handler:function (){
                this.saveDataSequence();
            },
            scope:this,
            disabled:true
        });        	
        
        this.quickSearchTF1 = new Wtf.KWLQuickSearchUseFilter({
            id : 'dataFilter'+this.id,
            width: 140,
            field : "name",
            emptyText:WtfGlobal.getLocaleText("crm.masterconfig.totoolbar.searchfieldDataEmptyText")//"Search Field Data "
        });
        this.quickSearchTF1.StorageChanged(this.masterDataStore);

        this.masterDataGrid = new Wtf.grid.GridPanel({
            sm:this.masterDataSm = new Wtf.grid.RowSelectionModel(),
            store:this.masterDataStore,
            region:"center",
            loadMask:true,
            cm:this.masterDataColumn,
            viewConfig:{
                forceFit:true,
                emptyText:WtfGlobal.emptyGridRenderer("No records found.")
            },
            tbar:[this.quickSearchTF1],
            bbar:[
            this.masterDataAdd,
            "-",
            this.masterDataEdit,
            "-",
            this.masterDataDelete,
            "-",
            this.saveSequence
            ]
        });
        
        this.masterDataGrid.on('rowclick', function(grid,rowIndex,e){      
            if(e.target.className == "pwndCRM shiftrowupIcon") {
                moveSelectedRow(this.masterDataGrid,0);
                this.saveSequence.enable();
            } 
            if(e.target.className == "pwndCRM shiftrowdownIcon") {
                moveSelectedRow(this.masterDataGrid,1);
                this.saveSequence.enable();
            } 
        },this);
        
        this.masterDataSm.on("beforerowselect",this.validateSelection,this);
        this.masterDataSm.on("selectionchange",function (){
            var rec = this.masterDataSm.getSelected();
            if(rec){
                if(rec.json.mainid==Wtf.common.leadStatusID_Qualified){  // Kuldeep Singh : Can't delete Qualified Lead Status'
                    this.masterDataDelete.disable();
                } else {
                    this.masterDataDelete.enable();
                }
                this.masterDataEdit.enable();

            } else {
                this.masterDataEdit.disable();
                this.masterDataDelete.disable();
            }
        },this);
    },
    validateRecord : function(value, cell, row, rowIndex, colIndex, ds){
        if(!row.data.hasAccess)
            return '<span class="disabled-record" wtf:qtip="You do not have sufficient permissions to access this data" >'+value+'</span>';
        else
            return value;
    },
    validateSelection : function(sm, index, keepExisting, record){
        return record.get('hasAccess' );
    },
    AddMaster:function (action){
        new Wtf.AddEditMaster({
            title:action +' Master Field',
            layout:"fit",
            modal:true,
            width:400,
            height:230,
            iconCls: "pwnd favwinIcon",
            action:action,
            rec:this.masterSm.getSelected(),
            store:Wtf.masterStore
        }).show();
    } ,
    
    AddMasterData:function (action){
        var maxlength = 50;
        var masterRec = this.masterSm.getSelected();

        var masterGridRec = this.masterGrid.getSelectionModel().getSelected();
        if(masterGridRec != undefined && masterGridRec.get("maxlength") != "") {
            maxlength = masterGridRec.get("maxlength");
        }
        var configid = masterGridRec.get("id");
        var customflag = masterGridRec.get("customflag");
        var parentid = masterGridRec.get("parentid");
        var editable = 1;//this.masterGrid.getSelectionModel().getSelected().get("isEdit");
        if(editable == 1) {
            new Wtf.AddEditMasterData({
            	layout:"fit",
                title:action=="Add"? WtfGlobal.getLocaleText("crm.masterconfig.AddEditWintitle.AddMasterData"):WtfGlobal.getLocaleText("crm.masterconfig.AddEditWintitle.EditMasterData"),
                modal:true,
                configid:configid,
                customflag:customflag,
                parentid:parentid,
                width:400,
                height:230,
                iconCls: "pwnd favwinIcon",
                masterRec:masterRec,
                rec:this.masterDataSm.getSelected(),
                action:action,
                maxlength:maxlength,
                store:this.masterDataStore
            }).show();
        }
    },
    DeleteMasterData:function (action){
        var masterSel = this.masterSm.getSelected();
        var configid = masterSel.get("id");
        var parentid = masterSel.get("parentid");
        var customflag = masterSel.get("customflag");
        var deletable = 1;//this.masterGrid.getSelectionModel().getSelected().get("isEdit");
        if(deletable == 1) {
            Wtf.commonWaitMsgBox("Deleting data...");
            var url = "Common/CRMManager/deleteMasterData.do";
            if(customflag == '1')
                url = Wtf.req.springBase+"common/crmCommonHandler/deleteCustomComboData.do";

            Wtf.Ajax.requestEx({
                url:url,
                params:{
                    configid:configid,
                    parentid:parentid,
                    id: this.masterDataSm.getSelected().get("id"),
                    customflag: customflag
                }
            },
            this,
            function(request, response){
                var msg = request.msg;
                if(request.success && this.masterSm.getSelected().get("customflag") == 1) {
                    msg += "<BR /> Please close the "+this.masterSm.getSelected().get("modulename")+"'s tab and reopen it to reflect the changes.";
                }
                Wtf.updateProgress();
                WtfComMsgBox(["Status",msg]);
                if(request.success){
                    this.masterDataStore.reload();
                    Wtf.loadMasterStore(configid);
                }
            },
            function(){
                Wtf.updateProgress();
                WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"),WtfGlobal.getLocaleText("crm.masterconfig.delmasterdataerrmsg")]);//"Error while deleting master data"]);
            });
        }
    },
    saveDataSequence:function(){
        var json=[];
        for(var i=0;i<this.masterDataStore.getCount();i++){
            json.push({
                "id":this.masterDataStore.getAt(i).get("id"),
                "seq":i+1
            });
        }
    	
        var custom_combo=this.masterGrid.getSelectionModel().getSelected().get("customflag");
        Wtf.Ajax.requestEx({
            url:"Common/CRMManager/saveMasterDataSequence.do",
            params:{
                jsonOb:Wtf.util.JSON.encode(json),
                customflag:custom_combo
            }
        },
        this,
        function(response){
            if(response.success==true)
                Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"), response.msg);
            else
                Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"), WtfGlobal.getLocaleText("crm.masterconfig.saveseqerrmsg"));//"Sequence could not be saved..!");
            this.saveSequence.setDisabled(true);
        
        },
        function(){
            }
            );
    },
    

    getSystemAdmin:function(){
	
        this.check1=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.CAMPAIGN"),//'Campaigns',
            name:'heirarchypermisssioncampaign',
            id : 'campaigncheck'
        });
        this.check1.on("check",function(){
            this.upperflag=1;
        },this);
        this.check2=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.LEAD"),//'Leads',
            id : 'leadcheck',
            name:'heirarchypermisssionleads'
        });
        this.check2.on("check",function(){
            this.upperflag=1;
        },this);
        this.check3=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.ACCOUNT"),//'Accounts',
            id : 'accountcheck',
            name:'heirarchypermisssionaccounts'
        });
        this.check3.on("check",function(){
            this.upperflag=1;
        },this);
        this.check4=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.CONTACT"),//'Contacts',
            id : 'contactcheck',
            name:'heirarchypermisssioncontacts'
        });
        this.check4.on("check",function(){
            this.upperflag=1;
        },this);
        this.check5=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.OPPORTUNITY"),//'Opportunities',
            id : 'oppcheck',
            name:'heirarchypermisssionopportunity'
        });
        this.check5.on("check",function(){
            this.upperflag=1;
        },this);
        this.check6=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.CASE"),//'Cases',
            id : 'casecheck',
            name:'heirarchypermisssioncases'
        });
        this.check6.on("check",function(){
            this.upperflag=1;
        },this);
        this.check7=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.PRODUCT"),//'Products',
            id : 'productcheck',
            name:'heirarchypermisssionproduct'
        });
        this.check7.on("check",function(){
            this.upperflag=1;
        },this);
        this.check8=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.ACTIVITY"),//'Activities',
            id : 'activitycheck',
            name:'heirarchypermisssionactivity'
        });
        this.check8.on("check",function(){
            this.upperflag=1;
        },this);

        this.emailCheck=new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.emailnotificationsetting.label"),//Email Notification
            name:'emailnotification',
            id : 'emailnotificationcheck'
        });
        this.emailCheck.on("check",function(){
            this.upperflag=1;
        },this);

        this.leadtypeIndividual= new Wtf.form.Checkbox({
            boxLabel:" ",
            inputType:'radio',
            style:(Wtf.isIE7) ?"margin-top:-19px" : undefined,
            name:'companydependentLeadTyperadio',
            inputValue:'true',
            id : 'indleadtypecheck',
            fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.leadtypefieldset.Individual")//"Individual"
        });
        this.leadtypeIndividual.on("check",function(){
            this.lowerflag=1;
        },this);
        this.leadtypeCompany= new Wtf.form.Checkbox({
            boxLabel:" ",
            style:(Wtf.isIE7) ?"margin-top:-19px" : undefined,
            inputType:'radio',
            inputValue:'false',
            id : 'companyleadtypecheck',
            name:'companydependentLeadTyperadio',
            fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.leadtypefieldset.Company")//"Company"
        })
        this.leadtypeCompany.on("check",function(){
            this.lowerflag=1;
        },this);

        this.rrleadrouting= new Wtf.form.Radio({
            boxLabel:" ",
            style:(Wtf.isIE7) ?"margin-top:-19px" : undefined,
            name:'defaultroutingradio',
            inputValue:'1',
            id : 'rrleadrouting',
            fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.roundrobin")//"Round Robin"
        })

        this.fcfsleadrouting= new Wtf.form.Radio({
            boxLabel:" ",
            style:(Wtf.isIE7) ?"margin-top:-19px" : undefined,
            name:'defaultroutingradio',
            inputValue:'2',
            id : 'fcfsleadrouting',
            fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.fcfs")//"First Come First Serve"
        })
       
        this.defaultleadrouting= new Wtf.form.Radio({
            boxLabel:" ",
            style:(Wtf.isIE7) ?"margin-top:-19px" : undefined,
            inputValue:'0',
            id : 'defaultleadrouting',
            name:'defaultroutingradio',
            fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.default")//"Default"
        })
        this.systemPanel=new Wtf.form.FormPanel({
        	
            title:WtfGlobal.getLocaleText("crm.masterconfig.companypreferences.header"),//'Company Preferences',
            //   region:"north",
            //   height:700,
            id:(Wtf.isSafari)?"":"companypre",
            split:true,
            width:400,
            autoScroll:true,
            layout:'form',
            url:"Common/CRMManager/setCompanyPref.do",
            baseParams:{
                mode:25
            },
            border:true,
            labelWidth:125,
            cls:'formstyleClass3',
            defaults:{
                labelWidth:150
            },
            bodyStyle:'background:white;padding-top:25px;padding-left:15px',
            items:[{
                xtype:'fieldset',
                width:'85%',
                height:'70%',
                title:WtfGlobal.getLocaleText("crm.masterconfig.hierarchypermission.fieldsettitle"),//'Check to Disable Hierarchy Permission',
                id:this.id+'heirarchypermisssion',
                //                hidden:true,
                border : false,
                items: [
                {
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.check1]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(56)' >"
                    }]
                },{
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.check2]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(57)'  >"
                    }]
                },{
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.check3]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(58)' >"
                    }]
                },{
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.check4]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(59)' >"
                    }]
                },{
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.check5]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(60)' >"
                    }]
                },{
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.check6]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(61)' >"
                    }]
                },{
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.check7]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(62)' >"
                    }]
                },{
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.check8]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(63)' >"
                    }]
                },{
                    xtype:"button",
                    border:false,
                    minWidth:80,
                    text:WtfGlobal.getLocaleText("crm.updateButton.caption"),//"Update"
                    scope:this,
                    handler:function(){
                        this.saveEmpIDformat();
                    }
                }
                ]

            },{
                xtype:'fieldset',
                width:'85%',
                height:'10%',
                title:WtfGlobal.getLocaleText("crm.masterconfig.defaultleadtype.fieldsettitle"),//'Select Default Lead Type',
                id:this.id+'companydependentLeadType',
                //                hidden:true,
                items :[{
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.leadtypeIndividual]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(65)' >"
                    }]
                },
                {
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.leadtypeCompany]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(66)' >"
                    }]
                },{
                    xtype:"button",
                    border:false,
                    minWidth:80,
                    text:WtfGlobal.getLocaleText("crm.updateButton.caption"),//"Update",
                    scope:this,
                    handler:function(){
                        this.saveEmpIDformat2();
                    }
                }

                ]
            },{
                xtype:'fieldset',
                width:'85%',
                height:'20%',
                title:WtfGlobal.getLocaleText("crm.masterconfig.leadMapping.fieldsettitle"),//'Lead Mapping',
                id:this.id+'leadMappings',
                items:[{
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [new Wtf.Panel({
                            html:'<a class=\'mastercongfiglinks\'  href=# onclick=moduleleadmapping(\'Account\') >'+WtfGlobal.getLocaleText("crm.masterconfig.leadMapping.accountmapping")+'</a> '
                            ,
                            border:false,
                            id :'accountmappingcheck'
                        })]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(67)' >"
                    }]
                },
                {
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [new Wtf.Panel({
                            html:'<a class=\'mastercongfiglinks\' href=# onclick=moduleleadmapping(\'Contact\') >'+WtfGlobal.getLocaleText("crm.masterconfig.leadMapping.contactmapping")+'</a> '
                            ,
                            border:false,
                            id :'contactmappingcheck'
                        })]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(68)' >"
                    }]
                },
                {
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [new Wtf.Panel({
                            html:'<a class=\'mastercongfiglinks\' href=# onclick=moduleleadmapping(\'Opportunity\') >'+WtfGlobal.getLocaleText("crm.masterconfig.leadMapping.opportunitymapping")+'</a> '
                            ,
                            border:false,
                            id :'opportunitymappingcheck'
                        })]
                    },{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(69)' >"
                    }]
                } ]
            },{
                xtype:'fieldset',
                width:'85%',
                height:'10%',
                title:WtfGlobal.getLocaleText("crm.materconfig.leadroutingtitle"),//'Select Default Lead Routing Option',
                id:this.id+'leadroutingoption',
                items :[{
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.defaultleadrouting]
                    }/*,{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(65)' >"
                    }*/]
                },
                {
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.rrleadrouting]
                    }/*,{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(66)' >"
                    }*/]
                },
                {
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.fcfsleadrouting]
                    }/*,{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(66)' >"
                    }*/]
                },{
                    xtype:"button",
                    border:false,
                    minWidth:80,
                    text:WtfGlobal.getLocaleText("crm.updateButton.caption"),//"Update",
                    scope:this,
                    handler:function(){
                        this.handleLeadRoutingConf();
                    }
                }]
            },{
                xtype:'fieldset',
                width:'85%',
                height:'20%',
                title:WtfGlobal.getLocaleText("crm.materconfig.notificationsettings.fieldsettitle"),//'Notification Settings',
                id:this.id+'notificationsettings',
                //                hidden:true,
                border : false,
                items: [
                {
                    layout : 'column',
                    border : false,
                    items: [{
                        columnWidth: '.80',
                        layout : 'form',
                        border : false,
                        items : [this.emailCheck]
                    }/*,{
                        columnWidth: '.20',
                        border : false,
                        bodyStyle:"padding-top:8px;",
                        html : "<img src='images/help.png' title='Click here' onclick = 'showHelp(56)' >"
                    }*/]
                },{
                    xtype:"button",
                    border:false,
                    minWidth:80,
                    text:WtfGlobal.getLocaleText("crm.updateButton.caption"),//"Update",
                    scope:this,
                    handler:function(){
                        this.saveNotificationSetup();
                    }
                }
                ]
            },{
                xtype:'fieldset',
                width:'85%',
                height:'20%',
                store:this.cstore,
                title:WtfGlobal.getLocaleText("crm.masterconfig.defaultcaseowner.fieldsettitle"),//Default Case Owner
                id:this.id+'defaultcaseowner',
                border : false,
                items: [
                {
                    layout : 'form',
                    border : false,
                    labelWidth : '0',
                    items : [
                    this.caseOwnerTxt=new Wtf.form.TextField({
                        fieldLabel:WtfGlobal.getLocaleText("crm.masterconfig.defaultcaseowner.label"),//'Owner',
                        name: 'caseowner',
                        width:'100',
                        id:'caseownertext',
                        readOnly:true,
                        value:this.caseowner
                    }),
                    {
                        xtype:"button",
                        border:false,
                        minWidth:80,
                        text:WtfGlobal.getLocaleText("crm.masterconfig.defaultcaseownerupdate.buttonCaption"),//"Set Owner",
                        bodyStyle:'float:right;',
                        scope:this,
                        handler:function(){
                            this.updateCaseOwner();
                        }
                    }]
                }]
            }]
        });
        
        this.getCaseOwner();
        
        
        this.updateCaseOwner = function(){
            //Wtf.caseOwnerStore.load();
            this.caseownerCombo=new Wtf.form.ComboBox({
                selectOnFocus:true,
                fieldLabel:'',
                triggerAction: 'all',
                mode: 'local',
                listAlign:'tl',
                store: Wtf.caseOwnerStore,
                useDefault:true,
                //scope:this,
                displayField: 'name',
                typeAhead: true,
                valueField:'id',
                anchor:'100%',
                value:this.caseowner
            }),

			 this.saveDefaultCaseowner = function(){
        		Wtf.commonWaitMsgBox("Please wait...");
            	Wtf.Ajax.requestEx({
        	   			url:"Common/CRMManager/saveDefaultCaseOwner.do",
        	   			params:{
            					owner : this.caseownerCombo.getValue(),
                                                ownername : this.caseownerCombo.getRawValue()
            			}
        	   	},
        	   	this,
        	   	function(response){
        	   			if(response.success==true){
        	   				var indx=Wtf.caseOwnerStore.find('id',this.caseownerCombo.getValue());
        	   				if(indx!=-1){
        	   						this.caseOwnerTxt.setValue(Wtf.caseOwnerStore.getAt(indx).get('name'));
        	   						this.caseowner=this.caseownerCombo.getValue();
        	   	    			}
        	   	    			else{
        	   	    				this.caseOwnerTxt.setValue("");
        	   	    			}
        	   				this.caseownerwin.close();
        	   				Wtf.Msg.alert('Success', response.msg);
        	   			}
        	   			else
        	   				Wtf.Msg.alert('Error', 'Case Owner could not be saved');
        	   	},function(){                                              				   
        	   	});
            		this.caseownerwin.close();
            	},
            	 
            this.caseownerwin = new Wtf.Window({
               					height:100,
               					width:340,
               					layout:'form',
               					id:'defaultcaseowner',
               					iconCls: "pwnd favwinIcon",
               					title:WtfGlobal.getLocaleText("crm.masterconfig.defaultcaseowner.fieldsettitle"),//"Default Case Owner",
               					modal:true,
               					scope:this,
               					bodyStyle  : 'padding: 5px',
               					shadow:true,
               					resizable:false,
               					buttonAlign:'right',
               					items:[new Wtf.Panel({
               						layout:'fit',
               						items:[ this.caseownerCombo ],
               					      buttons:[
               					               {
               					            	   text:WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//'Save',
               					            	   scope:this,
               					            	   handler: 
               					            	   this.saveDefaultCaseowner
               					            	               		
                					          },
                					          {
                					          	   text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
                					            	   scope:this,
                					            	   handler: function(){
                					        	  			this.caseownerwin.close();
                					               	   }
                					          }]})]
            });
            this.caseownerwin.show();
            
        };
          

            
        this.allModule="";
        //        Wtf.getCmp(this.id+'heirarchypermisssion').setVisible(true);
        //        Wtf.getCmp(this.id+'companydependentLeadType').setVisible(true);
        Wtf.Ajax.requestEx({
            url:"Common/CRMManager/getCompanyPreference.do",
            params:{
                type1:"master"
            }
        },
        this,
        function(request, response){
            var res = request;
            if(res){
                this.doLayout();
                this.check1.setValue(res.campaign);
                this.check2.setValue(res.lead);
                this.check3.setValue(res.account);
                this.check4.setValue(res.contact);
                this.check5.setValue(res.opportunity);
                this.check6.setValue(res.cases);
                this.check7.setValue(res.product);
                this.check8.setValue(res.activity);
                this.emailCheck.setValue(res.emailnotification);
                if(res.leadtype){
                    this.leadtypeIndividual.setValue(true);
                }else {
                    this.leadtypeCompany.setValue(true);
                }

                if(res.leadrouting){
                    switch(res.leadrouting) {
                        case 0 : this.defaultleadrouting.setValue(true); break;
                        case 1 : this.rrleadrouting.setValue(true); break;
                        case 2 : this.fcfsleadrouting.setValue(true); break;
                    }
                } else {
                    this.defaultleadrouting.setValue(true);
                }
            }
            this.upperflag=0;
            this.lowerflag=0;
        },
        function(){
            }
            );
    },
    
    
    getCaseOwner:function(){
        Wtf.caseOwnerStore.on('load',function(){
            this.caseowner="";
            Wtf.Ajax.requestEx({
                url: "Common/CRMManager/getDefaultCaseOwner.do"
  		
            },this,
            function(res){
                if(res.caseownerid !=undefined && res.caseownerid != ""){
                    this.caseowner = res.caseownerid;
                    var ind=Wtf.caseOwnerStore.find('id',this.caseowner);
                    if(ind!=-1){
                        this.caseOwnerTxt.setValue(Wtf.caseOwnerStore.getAt(ind).get('name'));
                    }
                    else{
                        this.caseOwnerTxt.setValue("");
                    }
                }
            },function(){}
                );
        },this);
        Wtf.caseOwnerStore.load();
    },
    
    saveEmpIDformat:function(){
        this.upperflag=0;
        if(this.systemPanel.form.isValid()){
            this.systemPanel.form.submit({
                params:{
                    category:"upper"
                },
                waitMsg:WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.savecompprefwaitmsg"),//'Saving company preferences',
                scope: this,
                success:function(){
                    ResponseAlert(650);
                },
                failure:function(){
                    ResponseAlert(651);
                }
            });
        }
    },
    saveEmpIDformat2:function(){
        this.lowerflag=0;
        if(this.leadtypeIndividual.getValue()){
            Wtf.leadtyypedefault="0";
        } else if(this.leadtypeCompany.getValue()){
            Wtf.leadtyypedefault="1";
        }
        if(this.systemPanel.form.isValid()){
            this.systemPanel.form.submit({
                params:{
                    category:"lower"
                },
                waitMsg:WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.savecompprefwaitmsg"),//'Saving company preferences',
                scope: this,
                success:function(){
                    ResponseAlert(650);
                },
                failure:function(){
                    ResponseAlert(651);
                }
            });
        }
    },

    saveNotificationSetup : function() {
        this.upperflag=0;
        if(this.systemPanel.form.isValid()){
            this.systemPanel.form.submit({
                params:{
                    category:"notificationtype"
                },
                waitMsg:WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.savecompprefwaitmsg"),//'Saving company preferences',
                scope: this,
                success:function(){
                    ResponseAlert(650);
                },
                failure:function(){
                    ResponseAlert(651);
                }
            });
        }
    },

    manageAppTeams: function(leadroutingOption){
        this.creategrid();
        this.centerdiv = document.createElement("div");
        this.centerdiv.appendChild(this.movetoright);
        this.centerdiv.appendChild(this.movetoleft);
        this.centerdiv.style.padding = "135px 10px 135px 10px";
        this.assignTeamWin = new Wtf.Window({
            title: WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.assignusers"),//"Assign Users for Lead Routing",
            closable: true,
            modal: true,
            iconCls: 'pwnd favwinIcon',
            width: 600,
            height: 525,
            resizable: false,
            buttonAlign: 'right',
            buttons: [{
                text: WtfGlobal.getLocaleText("crm.updateButton.caption"),//'Update',
                scope: this,
                handler: function(){
                    this.assignUserSubmit()
                }
            }, {
                text: WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                scope: this,
                handler: function(){
                    this.assignTeamWin.close();
                }
            }],
            layout: 'border',
            items: [{
                region: 'north',
                height: 75,
                border: false,
                baseCls : 'northWinClass',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.masterconfig.leadroutingtophtml.assignusers"), WtfGlobal.getLocaleText("crm.masterconfig.leadroutingtophtml.assignusersdetails"), "../../images/createuser.png")
            }, {
                region: 'center',
                border: false,
                baseCls : 'centerWinClass',
                bodyStyle: 'padding:20px 20px 20px 20px;',
                layout: 'fit',
                items: [{
                    border: false,
                    bodyStyle: 'background:transparent;',
                    layout: 'border',
                    items: [{
                        region: 'west',
                        border: false,
                        width: 250,
                        layout: 'fit',
                        items: [{
                            xtype: 'KWLListPanel',
                            title: WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.newusers"),//'New Users',
                            border: false,
                            paging: false,
                            layout: 'fit',
                            autoLoad: false,
                            items: this.availablegrid
                        }]
                    }, {
                        region: 'center',
                        border: false,
                        contentEl: this.centerdiv
                    }, {
                        region: 'east',
                        border: false,
                        width: 250,
                        layout: 'fit',
                        items: [{
                            xtype: 'KWLListPanel',
                            title: WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.assignedusers"),//'Assigned Users',
                            border: false,
                            paging: false,
                            layout: 'fit',
                            autoLoad: false,
                            items: this.selectedgrid
                        }]
                    }]
                }]
            }]
        });
        this.assignTeamWin.show();

    },

    creategrid: function(appid){
        this.availableds = new Wtf.data.Store({
            url: "Common/CRMManager/getUnAssignedLeadRoutingUsers.do",
            reader: new Wtf.data.KwlJsonReader({
                root: 'data',
                totalProperty: 'count'
            }, ['fullname', 'userid']),
            autoLoad: false
        });

        this.availablesm = new Wtf.grid.CheckboxSelectionModel();

        this.availablecm = new Wtf.grid.ColumnModel([this.availablesm, {
            header:WtfGlobal.getLocaleText("crm.USERNAMEFIELD"),// "User Name",
            dataIndex: 'fullname',
            autoWidth: true,
            sortable: true,
            groupable: true
        }]);
        //this.quickSearchEmp = new Wtf.KWLTagSearch({
        this.quickSearchEmp = new Wtf.KWLQuickSearchUseFilter({
            width: 100,
            field: "fullname"
        });
        this.quickSearchEmp.StorageChanged(this.availableds);
        this.availablegrid = new Wtf.grid.GridPanel({
            layout: 'fit',
            store: this.availableds,
            cm: this.availablecm,
            sm: this.availablesm,
            border: false,
            loadMask: {
                msg: WtfGlobal.getLocaleText("crm.responsealert.msg.500")//'Loading...'
            },
            viewConfig: {
                forceFit: true,
                autoFill: true
            },
            tbar: [WtfGlobal.getLocaleText("crm.customreport.header.quicksearch")+':', this.quickSearchEmp]/*,
            bbar:new Wtf.PagingSearchToolbar({
                pageSize: 15,
                searchField:this.quickSearchEmp,
                store: this.availableds
            })*/
        });

        this.availableds.load();

        /*
         *    For server side search change
         */
//        this.availableds.load({params:{start:0, limit: this.availablegrid.getBottomToolbar().pageSize}});
//        this.availableds.on('load',function(){
//            this.quickSearchEmp.StorageChanged(this.availableds);
//            this.quickSearchEmp.on('SearchComplete', function() {
//                this.availablegrid.getView().refresh();
//            }, this);
//        },this);
        this.selectedds = new Wtf.data.Store({
            url: "Common/CRMManager/getAssignedLeadRoutingUsers.do",
            reader: new Wtf.data.KwlJsonReader({
                root: 'data',
                totalProperty: 'count'
            }, ['fullname', 'userid']),
            autoLoad: false
        });
        this.selectedsm = new Wtf.grid.CheckboxSelectionModel();
        this.selectedcm = new Wtf.grid.ColumnModel([this.selectedsm, {
            header: WtfGlobal.getLocaleText("crm.USERNAMEFIELD"),//"User Name",
            dataIndex: 'fullname',
            sortable: true
        }]);
        //      this.quickSearchAssgEmp = new Wtf.KWLTagSearch({
        this.quickSearchAssgEmp = new Wtf.KWLQuickSearchUseFilter({
            width: 100,
            field: "fullname"
        });
        this.quickSearchAssgEmp.StorageChanged(this.selectedds);
        this.selectedgrid = new Wtf.grid.EditorGridPanel({
            store: this.selectedds,
            cm: this.selectedcm,
            sm: this.selectedsm,
            border: false,
            clicksToEdit: 1,
            view : new Wtf.grid.GridView({
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.mtytxt"),//"<div>No result found</div>",
                getRowClass : function(record, index, rowParams, store) {
                    if(record.data.iscreator == "1") {
                        return "x-item-disabled";
                    }
                }
            }),
            loadMask: {
                msg: WtfGlobal.getLocaleText("crm.responsealert.msg.500")//'Loading...'
            },
            tbar: [WtfGlobal.getLocaleText("crm.customreport.header.quicksearch")+':', this.quickSearchAssgEmp]/*,
            bbar:new Wtf.PagingSearchToolbar({
                pageSize: 15,
                searchField:this.quickSearchAssgEmp,
                store: this.selectedds
            })*/
        });

        /*
         *    For server side search change
         */
//        this.selectedds.load({params:{start:0, limit: this.selectedgrid.getBottomToolbar().pageSize}});
//        this.selectedds.on('load',function(){
//            this.quickSearchAssgEmp.StorageChanged(this.selectedds);
//            this.quickSearchAssgEmp.on('SearchComplete', function() {
//                this.selectedgrid.getView().refresh();
//            }, this);
//        },this);
        this.selectedds.load();

        this.movetoright = document.createElement('img');
        this.movetoright.src = "../../images/arrowright.gif";
        this.movetoright.style.width = "24px";
        this.movetoright.style.height = "24px";
        this.movetoright.style.margin = "5px 0px 5px 0px";
        this.movetoright.onclick = this.movetorightclicked.createDelegate(this, []);
        this.movetoleft = document.createElement('img');
        this.movetoleft.src = "../../images/arrowleft.gif";
        this.movetoleft.style.width = "24px";
        this.movetoleft.style.height = "24px";
        this.movetoleft.style.margin = "5px 0px 5px 0px";
        this.movetoleft.onclick = this.movetoleftclicked.createDelegate(this, []);
        this.selectedsm.on("beforerowselect", function(obj, row, keepExisting, record) {
            if(record.data.iscreator == "1") {
                return false;
            }
        },this);
    },

    assignUserSubmit: function(){
        var addid = "";
        var delid = "";
        for (var ctr = 0; ctr < this.selectedds.getCount(); ctr++) {
            var recData = this.selectedds.getAt(ctr).data;
            addid += recData.userid;
            if (ctr < this.selectedds.getCount() - 1) {
                addid += ',';
            }
        }

        for (ctr = 0; ctr < this.availableds.getCount(); ctr++) {
            delid += this.availableds.getAt(ctr).data.userid;
            if (ctr < this.availableds.getCount() - 1) {
                delid += ',';
            }
        }

        if(this.systemPanel.form.isValid()){
            this.systemPanel.form.submit({
                params:{
                    category:"leadrounting",
                    delid: delid,
                    addid: addid
                },
                waitMsg:WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.savecompprefwaitmsg"),//'Saving company preferences',
                scope: this,
                success:function(){
                    ResponseAlert(650);
                },
                failure:function(){
                    ResponseAlert(651);
                }
            });
        }
        this.assignTeamWin.close();
    },

    movetorightclicked: function(){
        var selected = this.availablesm.getSelections();
        var recArr = [];
        for (var ctr = 0; ctr < selected.length; ctr++) {
            recArr[0] = selected[ctr];
            this.selectedds.add(recArr);
            this.availableds.remove(selected[ctr]);
        }
    },

    movetoleftclicked: function(){
        var selected = this.selectedsm.getSelections();
        if (selected.length > 0) {
            this.availableds.add(selected);
        }
        for (var ctr = 0; ctr < selected.length; ctr++) {
            this.selectedds.remove(selected[ctr]);
        }
    },
    
    handleLeadRoutingConf:function() {
        //        this.lowerflag=0;
        if(this.defaultleadrouting.getValue()){
            this.saveLeadRoutingConf(0);
        } else if(this.rrleadrouting.getValue()){
            this.manageAppTeams(1);
        } else if(this.fcfsleadrouting.getValue()){
            this.manageAppTeams(2);
        }
    },

    saveLeadRoutingConf : function() {
        if(this.systemPanel.form.isValid()){
            this.systemPanel.form.submit({
                params:{
                    category:"leadrounting"
                },
                waitMsg:WtfGlobal.getLocaleText("crm.masterconfig.leadrouting.savecompprefwaitmsg"),//'Saving company preferences',
                scope: this,
                success:function(){
                    ResponseAlert(650);
                },
                failure:function(){
                    ResponseAlert(651);
                }
            });
        }
    }
});

function moduleleadmapping(moduleName){
    Wtf.Ajax.requestEx({
        url: "Common/CRMCommon/getMappingHeaders.do",
        params: {
            modulename : moduleName
        }
    }, this, function(action, response){
        if(action.success){
            this.mapCSV=new Wtf.moduleLeadMappingInterface({
                moduleHeaders:action.moduleHeaders,
                modName:moduleName,
                leadHeaders:action.leadHeaders,
                mappedHeaders:action.mappedHeaders
            }).show();
        }
    }, function(action, response){
        });
}

function moveSelectedRow(grid, direction) {
	var record = grid.getSelectionModel().getSelected();
	if (!record) {
		return;
	}
	var index = grid.getStore().indexOf(record);
	var tempindex;
	var temprecord;
	if (direction == 0) 
	{
		if (index < 0) {
			return;
		}
		tempindex=index-1;
		temprecord=grid.getStore().getAt(tempindex);
	} 
	else 
	{
		if (index >= grid.getStore().getCount()) {
			return;
		}
		tempindex=index+1;
		temprecord=grid.getStore().getAt(tempindex);
	}
	grid.getStore().remove(record);
	grid.getStore().insert(tempindex, record);
	grid.getStore().remove(temprecord);
	grid.getStore().insert(index, temprecord);
	grid.getSelectionModel().selectRow(tempindex, true);
}
