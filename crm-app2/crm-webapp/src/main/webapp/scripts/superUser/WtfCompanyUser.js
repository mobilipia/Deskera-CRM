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
Wtf.common.CompanyUser = function(config){
    this.companyid=config.companyid;
    //  this.createStore();
    //   this.createDisplayGrid();
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
        name: 'roles'
    },
    {
        name:'contactno'
    }
    ]);

    this.userds = new Wtf.data.Store({
        reader: new Wtf.data.KwlJsonReader({
            root: "data"
        },this.usersRec),
        url: "Common/ProfileHandler/getUserofCompany.do",
        baseParams:{
            mode:112,
            companyid:this.companyid
        }
    });
    this.userds.load();

    this.selectionModel = new Wtf.grid.CheckboxSelectionModel();
    this.rowNo=new Wtf.grid.RowNumberer();
    this.gridcm= new Wtf.grid.ColumnModel([
        this.rowNo,
      //  this.selectionModel,

    {
        dataIndex: 'image',
        width : 30,
        renderer : function(value){
            if(!value||value == ""){
                value = Wtf.DEFAULT_USER_URL;
            }
            return String.format("<img src='{0}' style='height:18px;width:18px;vertical-align:text-top;'/>",value);
        }
    },
    {
        header: "User Name",
        dataIndex: 'username',
        autoWidth : true,
        sortable: true,
        groupable: true
    },{
        header: "Name",
        dataIndex: 'fullname',
        autoWidth : true,
        sortable: true,
        groupable: true,
        renderer : function(value,p,record){
            return (record.data["fname"] + " " + record.data["lname"]);
        }
    },{
        header :'Email Address',
        dataIndex: 'emailid',
        autoSize : true,
        sortable: true,
        renderer: WtfGlobal.renderEmailTo,
        groupable: true
    },{
        header :'Last Login',
        dataIndex: 'lastlogin',
        renderer:WtfGlobal.dateRenderer,
        autoSize : true,
        sortable: true,
        groupable: true
    },{
        header :'Roles',
        dataIndex: 'roles',
        autoSize : true,
        sortable: true,
        groupable: true
    },{
        header :'Address',
        dataIndex: 'address',
        autoSize : true,
        sortable: true,
        groupable: true
    }]);
    this.usergrid = new Wtf.KwlPagingEditorGrid({
        store: this.userds,
        cm: this.gridcm,
        sm : this.selectionModel,
        border : false,
        displayInfo:true,
        trackMouseOver: true,
        stripeRows: true,
        searchLabel:"Quick Search",
        searchEmptyText:"search by User Name ",
        searchField:"username",
        loadMask : true,
        viewConfig: {
            forceFit:true
        }
    });

Wtf.apply(this,{
        layout : "fit",
        defaults:{
            border:false,
            bodyStyle:"background: transparent;"
        },
        loasMask:true,
        autoScroll:true,
        items:[this.usergrid]
    });
    
    Wtf.common.CompanyUser.superclass.constructor.call(this, config);
}

Wtf.extend(Wtf.common.CompanyUser, Wtf.Panel, {

    });











