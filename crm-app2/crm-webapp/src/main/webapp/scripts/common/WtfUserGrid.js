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
Wtf.common.UserGrid=function(config){

    this.usersRec = new Wtf.data.Record.create([
    {
        name: 'userid'
    },

    {
        name: 'username'
    },

    {
        name: 'fname'
    },

    {
        name: 'lname'
    },
    {
        name: 'fullname'
    },

    {
        name: 'image'
    },

    {
        name: 'emailid'
    },

    {
        name: 'lastlogin',
        type: 'date'
    },

    {
        name: 'aboutuser'
    },

    {
        name: 'address'
    },
    {
        name: 'roleid'
    },
    {
        name: 'rolename'
    },
    {
        name:'contactno'
    }
    ]);

    this.userds = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({
            root: "data",
            totalProperty:'count'
        },this.usersRec),
        url:"Common/ProfileHandler/getAllUserDetails.do",
        baseParams:{
            mode:11
        }
    });
    this.userds.load();
    this.selectionModel = new Wtf.grid.CheckboxSelectionModel({singleSelect:true});

    this.gridcm= new Wtf.grid.ColumnModel([
        new Wtf.grid.RowNumberer(),
        this.selectionModel,
        {
        header:'',
        dataIndex: 'image',
        width : 30,
        hidden:true,
        renderer : function(value){
            if(!value||value == ""){
                value = Wtf.DEFAULT_USER_URL;
            }
            return String.format("<img src='{0}' style='height:18px;width:18px;vertical-align:text-top;'/>",value);
        }
    },{
        header:WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),// "Name",
        tip:WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//'Name',
        dataIndex: 'fullname',
        autoWidth : true,
        sortable: true,
        groupable: true
    },{
        header :WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.email"),//"Email Address",
        tip:WtfGlobal.getLocaleText("crm.campaigndetails.bouncereport.header.email"),//'Email Address',
        dataIndex: 'emailid',
        autoSize : true,
        sortable: true,
        renderer: WtfGlobal.renderEmailTo,
        groupable: true
    },{
        header :WtfGlobal.getLocaleText("crm.usermanagement.header.roles"),//"Roles",
        tip:WtfGlobal.getLocaleText("crm.usermanagement.header.roles"),//'Roles',
        dataIndex: 'rolename',
        autoSize : true,
        sortable: true,
        groupable: true
    },{
        header :WtfGlobal.getLocaleText("crm.lead.defaultheader.address"),//"Address",
        tip:WtfGlobal.getLocaleText("crm.lead.defaultheader.address"),//'Address',
        dataIndex: 'address',
        autoSize : true,
        sortable: true,
        groupable: true
    }]);

    var help=getHelpButton(this,33);
    this.usergrid=new Wtf.grid.GridPanel({
        layout:'fit',
        store: this.userds,
        cm: this.gridcm,
        sm : this.selectionModel,
        border : false,
        loadMask : true,
        view:new Wtf.ux.KWLGridView({forceFit:true}),
        tbar:[this.quickSearchTF = new Wtf.KWLTagSearch({
            id: 'administration',
            width: 200,
            emptyText:WtfGlobal.getLocaleText("crm.mtytext.searchbyname")//'Search By Name'
        }),'->',help],
        bbar:new Wtf.PagingSearchToolbar({
                pageSize: 25,
                searchField:this.quickSearchTF,
                id: "admin_pagingtoolbar",
                store: this.userds,
                displayInfo: true,
      			emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//"No results to display",
                plugins:this.pP = new Wtf.common.pPageSize({
                    id: "pPageSize_" + this.id
                }),
                items:['-', this.assignPermBtn = new Wtf.Button({
                    text : WtfGlobal.getLocaleText("crm.usermanagement.bottomtoolbar.assignpermBTN"),//"Assign Permissions",
                    id : "permissions",//In use,do not delete.
                    allowDomMove:false,
                    disabled: true,
                    tooltip:{text:WtfGlobal.getLocaleText("crm.usermanagement.bottomtoolbar.assignpermBTN.ttip")},//'Assign permissions such as view, edit, delete, export or import all CRM Modules to the users in the system.'},
                    scope : this,
                    handler : this.requestPermissions,
                    iconCls : 'pwnd permicon'
                }), this.configureReportBtn = new Wtf.Button({
                    text : WtfGlobal.getLocaleText("crm.usermanagement.bottomtoolbar.confrepoBTN"),//"Configure Reports",
                    id : this.id + "configReports",//In use,do not delete.
                    allowDomMove:false,
                    disabled: true,
                    tooltip:{text:WtfGlobal.getLocaleText("crm.usermanagement.bottomtoolbar.confrepoBTN")},//'Configure reports.'},
                    scope : this,
                    handler : this.configureReports,
                    iconCls : 'pwnd permicon'
                })]
        })
//            }),'-','->',
//           /* this.assignRoleBtn = new Wtf.Button({
//                text : "Assign Roles",
//                id : "roles",//In use,do not delete.
//                allowDomMove:false,
//                scope : this,
//				disabled: true,
//                tooltip:{text:'Assign roles such as Company Administrator, Sales Manager or Sales Executive to the users of the system.'},
//                handler : this.requestRoles,
//                iconCls : 'pwnd permicon'
//            }),*/'-', this.assignPermBtn = new Wtf.Button({
//                text : "Assign Permissions",
//                id : "permissions",//In use,do not delete.
//                allowDomMove:false,
//				disabled: true,
//                tooltip:{text:'Assign permissions such as view,edit, delete, export or import all CRM Modules to the users in the system.'},
//                scope : this,
//                handler : this.requestPermissions,
//                iconCls : 'pwnd permicon'
//            })/*,'-', this.assignCommissionBtn = new Wtf.Button({
//                text : "Assign Commission Plan",
//                id : "commisionbtn",//In use,do not delete.
//                allowDomMove:false,
//				disabled: true,
//                tooltip:{text:'Assign commission plan to the users in the system.'},
//                scope : this,
//                handler : this.assignCommissionPlan,
//                iconCls : 'pwndCRM assignperm'
//            }), this.viewCommissionBtn = new Wtf.Button({
//                text : "View Commissions",
//                id : "commisionviewbtn",//In use,do not delete.
//                allowDomMove:false,
//				disabled: true,
//                tooltip:{text:'View commissions to the users in the system.'},
//                scope : this,
//                handler : this.viewCommissionPlan,
//                iconCls : 'pwndCRM viewperm'
//            })*/
    });
    this.userds.on('load',function(){
        this.quickSearchTF.StorageChanged(this.userds);
        this.quickSearchTF.on('SearchComplete', function() {
            this.usergrid.getView().refresh();
        }, this);
    },this);

    this.userds.on("datachanged",function(){
        this.quickSearchTF.setPage(this.pP.combo.value);
    },this);
        
    Wtf.apply(this,{
        layout : "fit",
        defaults:{
            border:false,
            bodyStyle:"background: transparent;"
        },
        loadMask:true,
        autoScroll:true,
        items:this.usergrid
    });

    Wtf.common.UserGrid.superclass.constructor.call(this,config);
    this.usergrid.getSelectionModel().on('selectionchange',this.enableDisable,this);
},

Wtf.extend(Wtf.common.UserGrid,Wtf.Panel,{
	enableDisable:function(sm){
    	if(sm.hasSelection()){
            if(Wtf.URole.roleid == Wtf.AdminId) { 
                this.assignPermBtn.enable();
                this.configureReportBtn.enable();
//                this.assignCommissionBtn.enable();
//                this.viewCommissionBtn.enable();
            }
    	} else {
			this.assignPermBtn.disable();
            this.configureReportBtn.disable();
//			this.assignCommissionBtn.disable();
//            this.viewCommissionBtn.disable();
    	}
	},  
    
    genSuccessResponse:function(response){
        Wtf.Msg.alert('User Management',response.msg);
        if(response.success==true)this.userds.reload();
        this.enable();
    },

    genFailureResponse:function(response){ 
        var msg=WtfGlobal.getLocaleText("crm.customreport.failurerespmsg");//"Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        this.enable();
        Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.usermanagement.msg.titlePermission"),msg);//'Permission',msg);
    },

    requestPermissions:function(){
        var rec=null;
        if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1){
            Wtf.MessageBox.alert(WtfGlobal.getLocaleText("crm.usermanagement.msg.titleUserPermission"), WtfGlobal.getLocaleText("crm.usermanagement.selusermsg"));
            return;
        }

        rec = this.usergrid.getSelectionModel().getSelected();
        var permWindow=new Wtf.common.Permissions({
            title:WtfGlobal.getLocaleText("crm.usermanagement.msg.titleUserPermission"),//"User Permissions",
            width:700,
            resizable: false,
            userid:rec.get('userid'),
            username:rec.get('fname')+" "+rec.get('lname')+"  ( "+rec.get('rolename')+" )",
            roleid:rec.get('roleid')

        });
        permWindow.show();
    },

    configureReports:function(){
        Wtf.Ajax.requestEx({
            url:"Common/CRMCommon/getUserReportConfig.do",
            params:{
                userid: this.usergrid.getSelectionModel().getSelected().get("userid")
            }
        },this,
        function(res) {
            if(res.success)
                this.reportconfig = res.reportconfig;
            this.configureReportOnClick();
        },
        function(res) {
            WtfComMsgBox(13,1);
        })
    },

    configureReportOnClick : function() {
        this.reportStore=new Wtf.data.SimpleStore({
            fields:['reportid','reportname'],
            data: [
            [46,"Opportunity by Region"],
            [47,"Opportunity Sales by Stage"],
            [48,"Opportunity Sales by Person"]]
        });
        this.reportCombo = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("crm.customreport.savereportwin.reportname"),//'Report Name*',
            hiddenName:'reportid',
            store:this.reportStore,
            valueField:'reportid',
            displayField:'reportname',
            mode: 'local',
            width:220,
            triggerAction: 'all',
            allowBlank:false,
            editable : false
        });
        this.reportCombo.on("select",function(combo,rec,index){
            var repId = rec.data.reportid;
            for(var cnt = 0; cnt<this.reportconfig.length;cnt++) {
                var reportCong = this.reportconfig[cnt];
                if(reportCong.reportcode == repId) {
                    this.dashdisplay.setValue(reportCong.dashboardreport);
                    this.emailreport.setValue(reportCong.emailreport);
                    break;
                }
            }
        },this);
        this.reportinfo= new Wtf.form.FormPanel({
            fileUpload:true,
            url:"Common/CRMCommon/setDashboardReportConfig.do",
            region:'center',
            bodyStyle: "background: transparent;",
            border:false,
            style: "background: transparent;padding:20px;",
            defaultType:'striptextfield',
            labelWidth:125,
            items:[
                this.reportCombo,
                this.dashdisplay = new Wtf.form.Checkbox({
                    fieldLabel:WtfGlobal.getLocaleText("crm.usermanagement.dashdisplay"),// 'Show on Dashboard',
                    name: 'dashdisplay',
                    id: 'dashdisplay1',
                    width:'16px',
                    style: (Wtf.isIE6 || Wtf.isIE7) ? '': 'margin-top:5px;'
                }),
                this.emailreport = new Wtf.form.Checkbox({
                    fieldLabel: WtfGlobal.getLocaleText("crm.usermanagement.emailreport"),//'E-mail Report',
                    name: 'emailreport',
                    id: 'emailreport1',
                    width:'16px',
                    style: (Wtf.isIE6 || Wtf.isIE7) ? '': 'margin-top:5px;'
                })
            ]
        });

        this.editToWindow = new Wtf.Window({
            title: WtfGlobal.getLocaleText("crm.usermanagement.bottomtoolbar.confrepoBTN"),//"Configure Report",
            modal: true,
            layout: 'fit',
            height: 200,
            resizable: false,
            width: 415,
            iconCls: "pwnd favwinIcon",
            bodyStyle:'background:#f1f1f1;font-size:10px;',
            items: [ this.reportinfo ],
            buttons: [{
                text: WtfGlobal.getLocaleText("crm.SAVEBUTTON"),//"Save",
                scope: this,
                handler: function() {
                    if(!this.reportinfo.getForm().isValid()){
                        Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.usermanagement.bottomtoolbar.confrepoBTN"),WtfGlobal.getLocaleText("crm.usermanagement.entereqfieldsmsg"));//" Enter required fields.");
                    } else{
                        Wtf.Ajax.requestEx({
                            url: 'Common/CRMCommon/setDashboardReportConfig.do',
                            params:{
                              userid: this.usergrid.getSelectionModel().getSelected().get("userid"),
                              reportid: this.reportCombo.getValue(),
                              reportname: this.reportStore.getAt(this.reportStore.find("reportid", this.reportCombo.getValue())).data.reportname,
                              dashdisplay:this.dashdisplay.checked?1:0,
                              emailreport:this.emailreport.checked?1:0
                            }
                        },
                        this,
                        function(res){
                            if(res.success){
                                ResponseAlert([WtfGlobal.getLocaleText("crm.usermanagement.bottomtoolbar.confrepoBTN"),WtfGlobal.getLocaleText("crm.usermanagement.repoconfmsg")]);
                                this.editToWindow.close();
                            } else {
                                ResponseAlert([WtfGlobal.getLocaleText("crm.usermanagement.bottomtoolbar.confrepoBTN"),WtfGlobal.getLocaleText("crm.usermanagement.reponotconfmsg")]);
                            }
                        },function(res){
                            ResponseAlert([WtfGlobal.getLocaleText("crm.usermanagement.bottomtoolbar.confrepoBTN"), WtfGlobal.getLocaleText("crm.usermanagement.reponotconfmsg")]);
                        });
                    }
                }
            },{
                text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),// "Cancel",
                scope: this,
                handler: function() {
                    this.editToWindow.close();
                }
            }]
        });
        this.editToWindow.show();
    },
/*
*
* Sagar A - Not in used
*/
/*
    requestRoles:function(){
        var rec=null;
        if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1){
            Wtf.MessageBox.alert("Assign Role", "Please select one user");
            return;
        }
        rec = this.usergrid.getSelectionModel().getSelected();
        var roleWindow=new Wtf.common.Roles({
            title:"Assign Role",
            resizable: false,
            ugrid:this.usergrid,
            scope:this,
            userid:rec.get('userid')

        });
        roleWindow.show();
    },
    
    assignCommissionPlan:function() {
        var rec=null;
        if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1) {
            Wtf.MessageBox.alert("Assign Role", "Please select one user");
            return;
        }
        
        rec = this.usergrid.getSelectionModel().getSelected().data;
        var commissionWindow = new Wtf.CommisionPlanWin({
            title : 'Assign Commission Plan',
            userid: rec.userid,
            userfullname: rec.fname + " " + rec.lname
        });
        commissionWindow.show();
    },

    viewCommissionPlan : function() {
        var rec=null;
        if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1) {
            Wtf.MessageBox.alert("Assign Role", "Please select one user");
            return;
        }
        rec = this.usergrid.getSelectionModel().getSelected().data;
        var commissionWindow = new Wtf.CommisionViewWin({
            title : 'View Commission Plan',
            userid: rec.userid,
            userfullname: rec.fname + " " + rec.lname
        });
        commissionWindow.show();
    },
    */
    
	assignManager:function(){
        if(this.usergrid.getSelectionModel().getCount()==0)
            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.usermanagement.assignmanagertitle"),WtfGlobal.getLocaleText("crm.usermanagement.selusermsg"));//"Please select a user");

        else{
            var amanage= new Wtf.common.AssignManager({
                user:this.usergrid.getSelectionModel().getSelections()
            });
            amanage.show();
        }
  
    }
});  
 
