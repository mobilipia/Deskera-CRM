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
Wtf.targetListTargets= function(config){
    Wtf.targetListTargets.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.targetListTargets, Wtf.Panel, {
    initComponent: function() {
        Wtf.targetListTargets.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        Wtf.targetListTargets.superclass.onRender.call(this, config);

        var record2 = new Wtf.data.Record.create([
                {name:'id'},
                {name:'name'},
                {name:'emailid'},
                {name:'phone'},
                {name:'company'},
                {name:'relatedto'},
                {name:'relatedid'}
        ]);

        var reader2 = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },record2);

        this.store2 = new Wtf.data.Store({
            url: Wtf.req.springBase+'emailMarketing/action/getTargetListTargets.do',
            method:'post',
            reader:reader2
        });
        
        this.store2.baseParams = {listID:this.targetlistId,flag:7};
        this.store2.load({
            params:{
                start:0,
                limit:25
            }
            });
        var checkBoxSM =  new Wtf.grid.CheckboxSelectionModel();
        var column2 = new Wtf.grid.ColumnModel(
            [ new Wtf.grid.RowNumberer(),
            {
                header:WtfGlobal.getLocaleText("crm.masterconfig.AddEditWin.AddEditMasterData.nameText"),//'Name',
                dataIndex:'name'
            },{
                header:WtfGlobal.getLocaleText("crm.lead.defaultheader.email"),//'Email',
                dataIndex:'emailid',
                renderer:WtfGlobal.renderEmailTo
            },{
                header:WtfGlobal.getLocaleText("crm.masterconfig.leadtypefieldset.Company"),//'Company',
                dataIndex:'company',
                pdfwidth:60
            },{
                header:WtfGlobal.getLocaleText("crm.targetlists.importtargets.header.importedfrom"),//'Imported From/ Added as',
                dataIndex:'relatedto',
                renderer: WtfGlobal.relatedtoIdRenderer
            }
            ]);

        this.centerGrid = new Wtf.grid.GridPanel({
            store: this.store2,
            cm: column2,
            region:'center',
            sm : checkBoxSM,
            border : false,
            loadMask : true,
            searchEmptyText:WtfGlobal.getLocaleText("crm.contact.toptoolbar.quiksearch.mtytxt"),//"Search by Name",
            searchField:"name",
            viewConfig: {
                forceFit:true,
                emptyText:WtfGlobal.getLocaleText("crm.emailmarketing.urlreport.mtygrid.watermark")
            },
            bbar :this.pag=new Wtf.PagingToolbar({
                pageSize: 25,
                border : false,
                id : "paggintoolbar"+this.id,
                store: this.store2,
                plugins : this.pPageSizeObj = new Wtf.common.pPageSize({
                    id : "pPageSize_"+this.id
                }),
                autoWidth : true,
                displayInfo:true
            })
        });

        this.add(this.centerGrid);
    },
    add1:function(){

    }
});





