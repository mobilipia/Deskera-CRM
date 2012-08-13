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
Wtf.common.SystemAdmin=function(config){

    this.usersRec = new Wtf.data.Record.create([
    {
        name: 'companyid'
    },

    {
        name: 'companyname'
    },
    {
        name: 'image'
    },

    {
        name: 'address'
    },

    {
        name: 'admin_fname'
    },

    {
        name: 'admin_lname'
    },

    {
        name: 'admin_uname'
    },

    {
        name: 'subdomain'
    },

    {
        name: 'emailid'
    },

    {
        name: 'city'
    },

    {
        name: 'country'
    },

    {
        name: 'phoneno'
    }
    //"website","emailid"

    ]);

    this.userds = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({
            root: "data"
        },this.usersRec),
        url: "Common/CRMManager/getAllCompanyDetails.do",
        baseParams:{
            mode:111
       }
    });
    this.userds.load();

    this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
    this.rowNo=new Wtf.grid.RowNumberer();
    this.gridcm= new Wtf.grid.ColumnModel([
        new Wtf.grid.RowNumberer(),
        this.selectionModel,{
            dataIndex: 'image',
            width : 80,
            renderer : function(value){
                if(!value||value == ""){
                    value = Wtf.DEFAULT_COMPANY_URL;
                }
                return String.format("<img src='{0}' style='height:18px;vertical-align:text-top;'/>",value);
            }
        },{
            header: "Company Name",
            width : 120,
            dataIndex: 'companyname'
        },{
            header: "Subdomain",
            dataIndex: 'subdomain',
            width : 60

        },{
            header: "Administrator",
            dataIndex: 'admin_uname',
            width : 120,
            renderer:function(v,m,r){
                return r.get('admin_fname')+" "+r.get('admin_lname')+" ("+v+")";
            }
        },{
            header: "Email",
            dataIndex: 'emailid',
            width : 100,
            renderer:WtfGlobal.renderEmailTo
        },{
            header :'Contact No',
            width : 60,
            dataIndex: 'phoneno',
            renderer:WtfGlobal.renderContactToSkype
        },{
            header: "Address",
            dataIndex: 'address'
        },{
            header :'City',
            width : 40,
            dataIndex: 'city'
        },{
            header :'Country',
            dataIndex: 'country'
        }
        ]);

    this.usergrid = new Wtf.KwlPagingEditorGrid({
        layout:'fit',
        store: this.userds,
        cm: this.gridcm,
        sm : this.selectionModel,
        border : false,
        loadMask : true,
        //displayInfo:true,
        trackMouseOver: true,
        stripeRows: true,
        searchLabel:"Quick Search",
        searchEmptyText:"search by Company Name ",
        searchField:"companyname",
        viewConfig: {
            forceFit:true
        }
    });

    this.userds.on("load",function(){
        var count=0;
        this.userds.filterBy(function(record,id){
            count++;
            if(count > 0 && count<= this.usergrid.pPageSizeObj.combo.value)
                return true;
            else
                return false;
        },this);
    },this)

    
    this.UsergridPanel  = new Wtf.Panel({
        //id : "Usergridpanel"+this.id,

        autoLoad : false,
        paging : false,
        layout : 'fit',
        items:[this.usergrid]
    });

    this.innerpanel = new Wtf.Panel({
        layout : 'fit',
        cls : 'backcolor',
        border : false,
        items:[this.usergrid]
    });

    Wtf.apply(this,{
        layout : "fit",
        defaults:{
            border:false,
            bodyStyle:"background: transparent;"
        },
        loasMask:true,
        autoScroll:true,
        items:[this.innerpanel],
        tbar:[{
            text:'View Users',
                iconCls:'pwndCRM userTabIcon',
            handler:this.viewUser.createDelegate(this)
        },{
            text:'Create Company',
                iconCls: 'newcompany',                
            handler:this.createCompany.createDelegate(this)
        },{

            text:'Delete Company',
            iconCls: 'pwnd projectTabIcon',
            handler:this.deleteComapny.createDelegate(this)
        }]
    });

    Wtf.common.SystemAdmin.superclass.constructor.call(this,config);
},

Wtf.extend(Wtf.common.SystemAdmin,Wtf.Panel,{
    loadStore:function(){
        this.userds.load();
    },
    createCompany:function(){

        var p = Wtf.getCmp("createcompany");

        if(!p){
            p= new Wtf.common.CreateCompany({
                title:'Create Company',
                id:'createcompany',
                closable: true,
                modal: true,
                  iconCls: "pwnd favwinIcon",
                width: 390,
                height: 440,
                aLoad:true,
                ds:this.userds,
                resizable: false,
            //layout: 'fit',
                buttonAlign: 'right',
                renderTo: document.body
            });p.show();
//            p.on("update",this.loadStore,this);
        }


},
viewUser:function(){
             var rec = this.usergrid.getSelectionModel().getSelected();
              if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1){
                        Wtf.MessageBox.alert("View User", "Please select one company");
                        return;
                    }
      var panel = Wtf.getCmp('companyuser'+rec.get('companyid'));
                if(!panel){
                             panel = new Wtf.common.CompanyUser({
                                    title:'Users of:'+rec.get('companyname'),
                                    layout : 'fit',
                                     id : 'companyuser'+rec.get('companyid'),
                iconCls:'pwnd userTabIcon',
                closable:true,
                companyid:rec.get('companyid'),
                companyname:rec.get('companyname'),
                border:false
            });

            Wtf.getCmp('as').add(panel);
            Wtf.getCmp('as').setActiveTab(panel);
        }
        else{
            Wtf.getCmp('as').setActiveTab(new Wtf.common.CompanyUser({
                title:'Users of:'+rec.get('companyname'),
                layout : 'fit',
                id : 'companyuser'+rec.get('companyid'),
                iconCls:'pwnd userTabIcon',
                closable:true,
                companyid:rec.get('companyid'),
                companyname:rec.get('companyname'),
                border:false
            }));
            Wtf.getCmp('as').setActiveTab(panel);
        }
        Wtf.getCmp('as').doLayout();


    },

    deleteComapny:function(){
        if(this.usergrid.getSelectionModel().hasSelection()==false||this.usergrid.getSelectionModel().getCount()>1){
            Wtf.MessageBox.alert("Delete Company", "Please select one company to delete");
            return;
        }
        var rec = this.usergrid.getSelectionModel().getSelected().data;
        Wtf.MessageBox.confirm("Delete Company", "Are you sure to delete this company?",function(btn){
            if(btn!="yes") { 
                return;
            }
            rec.mode=22;
            Wtf.Ajax.requestEx({
                url: "Common/CompanyDetails/deleteCompany.do",
                params: rec
            },this,this.genSuccessResponse,this.genFailureResponse);
        },this);
    },
    genSuccessResponse:function(response){

        this.userds.load();
        Wtf.Msg.alert('Delete Company',response.msg);
    },
    genFailureResponse:function(response){
        var msg="Failed to make connection with Web Server";
        if(response.msg)msg=response.msg;
        Wtf.Msg.alert('Delete Company',msg);
    }
});
