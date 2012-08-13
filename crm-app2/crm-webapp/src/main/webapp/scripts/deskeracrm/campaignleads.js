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
Wtf.campaignLeads = function(config){
    Wtf.campaignLeads.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.campaignLeads, Wtf.Panel,{
    onRender: function(config) {
        Wtf.campaignLeads.superclass.onRender.call(this,config);
        this.quickSearchTF = new Wtf.KWLTagSearch({
            id: 'quick'+ this.id,
            width: 200,
            field: 'name',
            emptyText:'Search by lead name'
        });
        this.callGrid();
    },
    callGrid:function() {
        this.storeRec = new Wtf.data.Record.create([
        {
            name:'leadid'
        },{
            name:'firstname'
        },{
            name:'lastname'
        },{
            name:'creationDate'
        }
        ]);

        this.ds = new Wtf.data.Store({
            url: Wtf.req.base+"report.jsp",
            baseParams: {
                flag : 44 ,
                campaignId:this.campaignId
            },
            reader: new Wtf.data.KwlJsonReader({
                totalProperty: 'totalCount',
                root: "data"
            },this.storeRec)
        });
        this.ds.on("loadexception",function(){
            Wtf.updateProgress();
        },this);
        //Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("crm.loadingdata"));
        this.ds.load();
        this.grid=new Wtf.grid.GridPanel({
            scope:this,
            store:this.ds,
            view:new Wtf.ux.KWLGridView({
                forceFit:true
            }),
            enableColumnHide: false,
            columns:[{
                header: "First Name",
                dataIndex: 'firstname',
                pdfwidth:60,
                tip:'First Name'
            },{
                header: "Last Name",
                dataIndex: 'lastname',
                pdfwidth:60,
                tip:'Last Name'
            },{
                header: "Creation Date",
                dataIndex: 'creationDate',
                pdfwidth:60,
                tip:'Creation Date',
                renderer:WtfGlobal.onlyDateRendererTZ
            }],
            border:false,
            trackMouseOver: true,
            stripeRows: true,
            layout:'fit',
            loadMask: {
                msg: 'Loading Records...'
            },
            tbar:[this.quickSearchTF],
            bbar:new Wtf.PagingSearchToolbar({
                pageSize: 25,
                searchField:this.quickSearchTF,
                id: "pagingtoolbar" + this.id,
                store: this.ds,
                displayInfo: true,
				emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//"No results to display",              
				plugins:this.pP = new Wtf.common.pPageSize({
                    id: "pPageSize_" + this.id
                })
            })
        });
        this.add(this.grid);
        this.ds.on('load',function(){
            this.quickSearchTF.StorageChanged(this.ds);
            this.quickSearchTF.on('SearchComplete', function() {
                this.grid.getView().refresh();
            }, this);
        },this);
        this.ds.on("datachanged",function(){
            this.quickSearchTF.setPage(this.pP.combo.value);
        },this);
    }
    
});



