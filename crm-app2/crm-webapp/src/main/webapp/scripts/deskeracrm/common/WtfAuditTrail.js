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
Wtf.common.WtfAuditTrail = function(config){
    Wtf.common.WtfAuditTrail.superclass.constructor.call(this,config);
};

Wtf.extend(Wtf.common.WtfAuditTrail,Wtf.Panel,{
    onRender : function(config){
        Wtf.common.WtfAuditTrail.superclass.onRender.call(this,config);
        
        this.groupingView1 = new Wtf.grid.GroupingView({
            forceFit: true,
            showGroupName: false,
            enableGroupingMenu: true,
            hideGroupedColumn: true
        });
    
        this.auditRecord = Wtf.data.Record.create([
        { 
            name: 'userName',
            type: 'string'
        },{
            name: 'auditid',
            type: 'string'
        },{
            name: 'details',
            type: 'string'
        },{
            name: 'auditTime',dateFormat:'time',type:'date'
        },{
            name: 'ipAddr',
            type: 'string'
        },{
            name: 'actionname',
            type: 'string'
        }
        ]);
    
    
        this.auditReader = new Wtf.data.KwlJsonReader({
            root: "data",
            totalProperty:"count"
        }, this.auditRecord);
    
        this.auditStore = new Wtf.data.Store({
            proxy: new Wtf.data.HttpProxy({
                url: "Common/AuditTrail/getAuditData.do"
            }),
            remoteSort : true,
            reader: this.auditReader
        });
    
        this.cmodel = new Wtf.grid.ColumnModel([new Wtf.grid.RowNumberer({allowIncreament:true,rowspan:1}),
        {
            header: WtfGlobal.getLocaleText("crm.audittrail.header.action"),//"Action",
            tip:WtfGlobal.getLocaleText("crm.audittrail.header.action"),//'Action',
            width: 150,
            hidden:true,
            dataIndex: 'actionname'
        }, {
            header: WtfGlobal.getLocaleText("crm.audittrail.header.details"),//"Details",
            tip:WtfGlobal.getLocaleText("crm.audittrail.header.details"),//'Details',
            width: 240,
            renderer : function(val) {
        	var tmp = Wtf.util.Format.htmlEncode(val);
                return "<div wtf:qtip=\""+tmp+"\"wtf:qtitle='Details'>"+unescape(val)+"</div>";
            },
            dataIndex: 'details',
            sortable:false
        }, {
            header:WtfGlobal.getLocaleText("crm.audittrail.header.user"),// "User",
            tip:WtfGlobal.getLocaleText("crm.audittrail.header.user"),//'User',
            width: 120,
            dataIndex: 'userName'
        }, {
            header:WtfGlobal.getLocaleText("crm.audittrail.header.time"),//"Time",
            tip:WtfGlobal.getLocaleText("crm.audittrail.header.time"),//'Time',
            width: 160,
            align:'center',
            dataIndex: 'auditTime',
            offset:Wtf.pref.tzoffset,
            renderer:WtfGlobal.dateTimeRendererTZ
        }, {
            header:WtfGlobal.getLocaleText("crm.audittrail.header.ipadd"),//"IP Address",
            tip:WtfGlobal.getLocaleText("crm.audittrail.header.ipadd"),//'IP Address',
            align:'right',
            width: 100,
            dataIndex: 'ipAddr',
            groupable:true
        }]);
            
        this.grid=new Wtf.grid.GridPanel({
            ds: this.auditStore,
            cm: this.cmodel,
            border: false,
            view: new Wtf.ux.KWLGridView({forceFit:true}),
            trackMouseOver: true,
            loadMask: {
                msg: 'Loading...'
            }
        });
    
        this.cmodel.defaultSortable = true;
    
        this.comboReader = new Wtf.data.Record.create([
        {
            name: 'id',
            type: 'string'
        },{
            name: 'name',
            type: 'string'
        }
        ]);

        this.groupRecord = Wtf.data.Record.create([
        {
            name: 'groupname',
            type: 'string'
        },{
            name: 'groupid',
            type: 'string'
        }
        ]);

        this.groupReader = new Wtf.data.KwlJsonReader({
            root: "data"
        }, this.groupRecord);

        this.groupStore = new Wtf.data.Store({
            proxy: new Wtf.data.HttpProxy({
                url: "Common/AuditTrail/getAuditGroupData.do"
            }),
            reader: this.groupReader
        });
      
        this.resetBttn=new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.RESETBUTTON"),//'Reset',            
            id: 'btnRec' + this.id,
            scope: this,
            disabled :false,
            tooltip: {text: WtfGlobal.getLocaleText("crm.editor.toptoolbar.resetBTN.ttip")},//'Click to remove any filter settings or search criteria and view all records.'},
            iconCls:'pwndCRM reset'
        });
        this.resetBttn.on('click',this.handleResetClick,this);   

        this.searchBttn = new Wtf.Button({
            text: WtfGlobal.getLocaleText("crm.audittrail.searchBTN"),//'Search',
            scope: this,
            handler: this.searchHandler,
            iconCls : 'pwnd searchtabpane'
        });
        this.fT = new Wtf.form.TextField({
            fieldLabel : "Contains",
            emptyText : " -- Search Text --",
            width : 200
        });
                            
        this.groupCombo=new Wtf.form.ComboBox({
            id:'group' + this.id,
            store : this.groupStore,
            readOnly : true,
            displayField:'groupname',
            mode: 'local',
            triggerAction: 'all',
            emptyText : 'Select a transaction',
            fieldLabel : 'Transaction',
            name : 'groupid',
            valueField:'groupid'
        });

        this.todate = new Wtf.form.DateField({
            fieldLabel : "To",
            emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),// --Select Date--",
            width: 130,
            readOnly:true,
            scope:this
        });

        this.fil = new Wtf.Toolbar.Button({
            text:"Filter",
            scope:this,
            tooltip: {
                text: "Choose a date range using 'From' and 'To' fields to filter records created in the specified time duration."
            },
            iconCls:'pwnd addfilter',
            handler:function(){
                var srch= this.fT.getValue();
                var tr= this.groupCombo.getValue();
                var fromdate=this.fromdate.getValue();
                var todate=this.todate.getValue();

                if((fromdate =="") && (todate =="") && (srch.trim().length == 0) && (tr.trim().length == 0)) {
                    WtfImportMsgBox(3,3);
                    return;
                }
                
                if((fromdate!="" && todate=="")||(fromdate=="" && todate!="")) {
                    WtfImportMsgBox(2,2);
                    return;
                }
                if(fromdate>todate) {
                    WtfImportMsgBox(1,1);
                    return;
                }                
                if(this.filterCombo!=undefined) {
                    var val =this.filterCombo.getValue();
                    this.filterCombo.setValue(val);
                }
                if(((srch.trim().length > 0)||(tr.trim().length > 0)) && ((fromdate =="") && (todate ==""))) {
                    this.searchHandler();                    
                }
                    
                if((fromdate !="") && (todate !="") && (srch.trim().length == 0) && (tr.trim().length == 0)) {
                    fromdate=Wtf.formatDate(this.fromdate.getValue(),0);
                    todate=Wtf.formatDate(this.todate.getValue().add(Date.DAY,1).add(Date.SECOND,-1),0);
                    
                    this.auditStore.baseParams['frm'] = fromdate.toString();
                    this.auditStore.baseParams['to'] = todate.toString();
                    this.auditStore.baseParams['groupid'] = tr;
                    this.auditStore.baseParams['search'] = srch;
                    this.auditStore.reload({
                        params:{
                            start:0,
                            limit:this.pP.combo.value
                        }
                    });
                }                

                if(((fromdate !="") && (todate !="")) && ((srch.trim().length > 0)||(tr.trim().length > 0))) {
                    fromdate=Wtf.formatDate(this.fromdate.getValue(),0);
                    todate=Wtf.formatDate(this.todate.getValue().add(Date.DAY,1).add(Date.SECOND,-1),0);
                    
                    this.auditStore.removeAll();
                    this.auditStore.baseParams = {
                        mode:201,
                        groupid:tr,
                        search:this.fT.getValue(),
                        frm:fromdate.toString(),
                        to:todate.toString()
                    };
                    this.auditStore.reload({
                        params:{
                            start:0,
                            limit:this.pP.combo.value
                        }
                    });
                }
            }
        });
    
        this.fromdate = new Wtf.form.DateField({
            fieldLabel : "From",
            emptyText:WtfGlobal.getLocaleText("crm.date.mtytxt"),// --Select Date--",
            width: 130,
            readOnly:true,            
            scope:this
        });

        this.from = new Wtf.Toolbar.TextItem('From');

        this.to = new Wtf.Toolbar.TextItem('To');

        this.reader = new Wtf.data.JsonReader({
            root: 'data',
            fields: [{
                name: 'id'
            }, {
                name: 'name'
            }]
        });
      
        var innerPanel = new Wtf.Panel({
            border : false,
            layout:'fit',
            items:[this.grid],
            tbar: [' Transaction: ',
                    this.groupCombo,'-',
                    this.fT,
                    //this.searchBttn,
                    this.from,
                    this.fromdate,
                    this.to,
                    this.todate,'-',
                    this.fil,
                    this.resetBttn
            ],
            bbar: new Wtf.PagingSearchToolbar({
                id: 'pgTbar' + this.id,
                searchField: this.fT,
                pageSize: 30,
                store: this.auditStore,
                displayInfo: true,
                emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),
                plugins: this.pP =new Wtf.common.pPageSize({})
            })
        });
        this.auditStore.on('datachanged', function() {
            var p = this.pP.combo.value;
        }, this);
        this.add(innerPanel);
        this.auditStore.baseParams = {
            mode:201,
            groupid:this.groupCombo.getValue(),
            search:this.fT.getValue()
        };
        this.auditStore.load({
            params: {
                start:0,
                limit:30
            }
        });
        this.groupStore.load({
            params: {
                mode:202
            }
        });
    },
    handleResetClick:function(){
        this.groupCombo.reset();
        this.fT.reset();
        this.todate.reset();
        this.fromdate.reset();

        this.auditStore.baseParams = {
            mode:201,
            groupid:'',
            search:''
        };
        this.auditStore.load({
            params: {
                start:0,
                limit:this.pP.combo.value
            }
        });
    },
    searchHandler: function() {
        this.auditStore.removeAll();
        this.auditStore.baseParams = {
            mode:201,
            groupid:this.groupCombo.getValue(),
            search:this.fT.getValue()
        };
        this.auditStore.load({
            params: {
                start:0,
                limit:this.pP.combo.value
            }
        });
    }

});
