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
Wtf.outboundEmail= function(config){
    Wtf.outboundEmail.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.outboundEmail, Wtf.Panel, {
    onRender: function(config) {
        Wtf.outboundEmail.superclass.onRender.call(this, config);

        this.record = Wtf.data.Record.create([
        {
            name:'id'
        }
        ,{
            name:'email'
        },
        {
            name:'username'
        }
        ,{
            name:'password'
        },
        {
          name:'server'
        },
        {
            name:'port'
        },
        {
            name:'protocol'
        },
        {
            name:'seclayer'
        },
        {
          name:'authenticate'
        }
        ]);
        this.reader= new Wtf.data.KwlJsonReader({
            root: 'data',
            totalProperty:'totalCount'
        },
        this.record
        );

        this.ds = new Wtf.data.Store({
            url: Wtf.req.springBase+'notification/action/getNotificationSettings.do',
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
            header: WtfGlobal.getLocaleText("crm.EMAILIDFIELD"),//Email Id",
            sortable: true,
            dataIndex: 'email'
        },
        {
            header:WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.server"),// "Server",
            width: 100,
            sortable: true,
            align:'right',
            dataIndex: 'server'

        },
        {
            header: WtfGlobal.getLocaleText("crm.mymails.outboundsettings.newwin.port"),//"Port ",
            width: 150,
            sortable: true,
            align:'right',
            dataIndex: 'port'
        },
        {
            header: WtfGlobal.getLocaleText("crm.mymails.outboundsettings.header.protocol"),//"Protocol",
            width: 100,
            sortable: true,
            align:'right',
            dataIndex: 'protocol',
            renderer:Wtf.ux.comboBoxRendererStore(new Wtf.data.SimpleStore({
            	fields:['id','name'],
            	data:[["smtp","SMTP"],["pop3","POP3"],["imap","IMAP"]]
            }),"id","name")
        },
        {
            header: WtfGlobal.getLocaleText("crm.mymails.outboundsettings.header.seclayer"),//"Security Layer",
            width: 100,
            sortable: true,
            align:'right',
            dataIndex: 'seclayer',
            renderer:Wtf.ux.comboBoxRendererStore(new Wtf.data.SimpleStore({
            	fields:['id','name'],
            	data:[["ssl","SSL"],["tls","TLS"]]
            }),"id","name")
        },
        {
            header:  WtfGlobal.getLocaleText("crm.mymails.outboundsettings.header.authenticate"),//"Authenticate",
            width: 100,
            sortable: true,
            align:'right',
            dataIndex: 'authenticate',
            renderer:function(val){
                    if(val)
                        return "Yes";
                    else
                        return "No";
            }
            
        },{
            header:WtfGlobal.getLocaleText("crm.updateButton.caption"),//'Update',
            dataIndex:'update',
            align:'right',
            width:30,
            renderer : function(val, cell, row, rowIndex, colIndex, ds) {
                       if(row.data.targeted!="")
                           return '<div class=\'pwnd editGoalIcon\' > </div>';
            }
        },{
            header:WtfGlobal.getLocaleText("crm.goals.header.remove"),//'Remove',
            dataIndex:'remove',
            align:'right',
            width:30,
            renderer : function(val, cell, row, rowIndex, colIndex, ds) {
                       if(row.data.targeted!="")
                           return '<div class=\'pwnd deleteButton\' > </div>';
        }
        }]);
        this.addSmtpserver = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.targetlists.toptoolbar.newBTN"),//"New",
            scope:this,
            iconCls:'pwnd emailsetting',
            tooltip: {
                text: WtfGlobal.getLocaleText("crm.mymails.outboundsettings.bewbtn.ttip")//'Click to add.'
            },
            handler:function(){
                    Wtf.outboundEmailSettings(this,this.ds);
            }

        });
        this.quickPanelSearch = new Wtf.KWLTagSearch({
            width: 150,
            emptyText: WtfGlobal.getLocaleText("crm.mymails.outboundsettings.searchtxt"),//'Search by Email Id ',
            field: "email"
        });
        this.pg = new Wtf.PagingSearchToolbar({
            pageSize: 15,
            searchField: this.quickPanelSearch,
            store: this.ds,
            displayInfo:true,
            emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),//"No results to display",
            plugins: this.pP = new Wtf.common.pPageSize()
        });
        this.smtpservergrid=new Wtf.grid.GridPanel({
            cm:this.cm,
            store:this.ds,
            sm:this.sm,
            border:false,
            layout:'fit',
            loadMask:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("crm.mymails.outboundsettings.mtygrid.watermark"))//"No email id present till now")
            },
            tbar:[WtfGlobal.getLocaleText("crm.customreport.header.quicksearch"),'-',this.quickPanelSearch,'-',this.addSmtpserver],
            clicksToEdit :1,
            bbar:this.pg,
            displayInfo:true
        });
        this.ds.on('load', function(store) {
            this.quickPanelSearch.StorageChanged(store);
        }, this);
        this.ds.on('datachanged', function() {
            var p = this.pP.combo.value;
            this.quickPanelSearch.setPage(p);
        }, this);
        this.add(this.smtpservergrid);
        this.smtpservergrid.on("cellclick", this.cellClick, this);
    },
    cellClick:function(grid, ri, ci, e) {

        var event = e;
        if(event.target.className == "pwnd deleteButton") {
             this.deletegoal();
        } else if(event.target.className == "pwnd editGoalIcon") {
             Wtf.outboundEmailSettings(this,this.ds,grid.getSelectionModel().getSelected());
        }
    },
    deletegoal:function() {
        Wtf.MessageBox.show({
            title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),//'Confirm',
            msg:WtfGlobal.getLocaleText("crm.mymails.outboundsettings.delsettingsconfirmmsg"),//"Are you sure you want to delete selected email settings?<br><br><b>Note: This data cannot be retrieved later.</b>",
            icon:Wtf.MessageBox.QUESTION,
            buttons:Wtf.MessageBox.YESNO,
            scope:this,
            fn:function(button){
                if(button=='yes')
                {
                    this.delkey=this.sm.getSelections();
                    this.ids=[];
                    this.sm.clearSelections();
                    var store=this.smtpservergrid.getStore();
                    for(var i=0;i<this.delkey.length;i++){
                       
                        if(this.delkey[i].get('id'))
                        {
                            this.ids.push(this.delkey[i].get('id'));
                        }
                        else{
                            store.remove(this.delkey[i]);
                        }
                    }
                    if(this.ids.length>0)
                    {
                        Wtf.Ajax.requestEx({
                            url: Wtf.req.springBase+'notification/action/deleteSettings.do',
                            params:{
                                ids:this.ids
                            }
                        },this,
                        function(){
                            ResponseAlert(820);
                            store.reload();
                        },
                        function(){
                            ResponseAlert(821);
                        }

                        )
                    }
                }
            }
        });
    }
});





/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


