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
Wtf.ReportsPanel = function(config){
    Wtf.apply(this,config);
        this.repStore = new Wtf.data.JsonStore({
            url: 'jspfiles/report.jsp?type=getreports&flag=true',
            root: 'data',
            totalProperty:"count",
            fields: ['id','name','description','moduleid']            
        });
        this.cmodel1 = new Wtf.grid.ColumnModel([
            new Wtf.KWLRowNumberer(),
            {
                header: "Report Name",
                width: 150,
                dataIndex: 'name'               
            }, {
                header: "Description",
                width: 150,
                dataIndex: 'description'               
            },{
                header : "View",
                dataIndex: 'status',
                width:18,
                sortable: true,
                renderer:function(value, css, record, row, column, store){
                    return "<img id='AcceptImg' class='add'  style='height:18px; width:18px;' src='images/report.gif' title='View Report '></img>";
                }    
            }]);
        
        this.grid = new Wtf.grid.GridPanel({
            store: this.repStore,
            cm :this.cmodel1,
            viewConfig: {
                    forceFit: true
            },
            title:'Report List',
            tbar: [WtfGlobal.getLocaleText("crm.goalsettings.qsearch.label")+": ", this.qPSModules = new Wtf.KWLTagSearch({
                        width: 250,
                        emptyText : 'Enter report name',
                        field:"name"
                    }),{
                    text : "Manage Reports",
                    scope : this,
                    tooltip:'Manage Reports',
                    handler : this.configurReport,
                    iconCls : 'pwnd editicon'
               }],
            bbar:new Wtf.PagingSearchToolbar({
                 pageSize: 10,
                 id: 'pgTbarProgram' + this.id,
                 searchField: this.qPSModules,
                store:this.repStore,
                displayInfo: true,
                emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),
                plugins: this.pP1 = new Wtf.common.pPageSize({})
            })
        });
        this.repStore.on("datachanged",function(store) {
                var p = this.pP1.combo.value;
                this.qPSModules.setPage(p);
            this.qPSModules.StorageChanged(store);
        }, this);
        this.repStore.on("load",function(store){
             var p = this.pP1.combo.value;
                this.qPSModules.setPage(p);
            this.qPSModules.StorageChanged(store);
        },this);
        this.reportList = new Wtf.TabPanel({
            id:'ReportTabPanel'+ this.id,
            activeTab:0,
            border:false,
            enableTabScroll:true,
            items :this.grid
        });
        this.items = this.reportList;
        
        this.layout = 'fit';
       
        Wtf.ReportsPanel.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.ReportsPanel, Wtf.Panel, {
     initComponent: function() {
         Wtf.ReportsPanel.superclass.initComponent.call(this);
         this.addEvents({
             'tabready': true
         });
    },
    onRender: function(config) {
        Wtf.ReportsPanel.superclass.onRender.call(this, config);
        this.repStore.load({
            params : {
                limit : 10,
                start : 0
            }
        });
    },
    configurReport:function(){
        var rec = this.grid.getSelectionModel().getSelected();
        if(rec!=null){
            var configReportWin = new Wtf.configReportWin({
                                       moduleid:rec.get("moduleid")    
                                  });
            configReportWin.show();
        }else{
            msgBoxShow(["Invalid Opperation","Select atleast one report"],0);
        }
        
    }
});


Wtf.configReportWin = function(config){
    Wtf.apply(this, config);
    this.iconCls = 'iconwin';
    Wtf.configReportWin.superclass.constructor.call(this, config);
    this.addEvents = {
       "onsuccess" : true
    };
};


Wtf.extend(Wtf.configReportWin, Wtf.Window, {
    initComponent: function(){
        Wtf.configReportWin.superclass.initComponent.call(this);
        this.title = "Report Configure";
        this.layout = 'fit';
        this.width = 650;
        this.height = 450;
        this.modal = true;
    },
    
    onRender: function(config){
        Wtf.configReportWin.superclass.onRender.call(this, config);
        var Rec = Wtf.data.Record.create([{name:'name'},{name:'hidden'},{name:'sortable'},{name:'groupable'}]);
        this.ds =  new Wtf.data.Store({
            url: Wtf.req.mbuild+'form.do?action=15&moduleid='+this.moduleid,    
            reader: new Wtf.data.KwlJsonReader({
                        root:"data"
            }, Rec)
        });
        this.sm = new Wtf.grid.RowSelectionModel({singleSelect:true});
        this.cm = new Wtf.grid.ColumnModel([{
            header: "Field Name",
            dataIndex: 'name'
        },{
            header: "Hidden",
            dataIndex: 'hidden',
            renderer : this.checkBoxRen
        },{
            header: "Sortable",
            dataIndex: 'sortable',
            renderer : this.checkBoxRen
        },{
            header: "Groupable",
            dataIndex: 'groupable',
            renderer : this.checkBoxRen
        }]);
        this.ds.load();
        this.repConfGrid = new Wtf.grid.EditorGridPanel({
            clicksToEdit : 1,
            store: this.ds,
            cm: this.cm,
            sm : this.sm,
            border : false,
            width: 434,
            loadMask : true,
            viewConfig: {
                forceFit:true
            }
        });
        this.innerpanel = this.add(new Wtf.Panel({
            layout: 'border',
            items: [{
              region : 'north',
              height : 75,
              border : false,
              cls:'windowHeader',
              html : getTopHtml("Configure Report", "Select appropriate option to configure report")
            },{
              region:'center',
              border:false,
              bodyStyle:'background:#f1f1f1;font-size:10px;',
              layout : 'fit',
              items:[this.repConfGrid]
            }],
            buttons: [{
                text : 'Configure',
                scope: this,
                handler: this.configureReport
            },{
                text : WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
                scope : this,
                id:"cancelUserToProjButton",
                handler : function(){
                    this.close();
                }
            }]
        }));
       
   },
   configureReport:function(){
       
   },
   checkBoxRen : function(){
        return '<input type="Checkbox" class="checkboxclick"/>';
   }
    
});
        





var reporttab = Wtf.getCmp("taballReports");
//reportTabs.remove(reporttab);
var reportPanel = new Wtf.ReportsPanel({
    id:'reportTabPanel' + reporttab.id,
    deferredRender: true,
    border: false,
    layout : "fit",
    enableTabScroll: true
});
reporttab.add(reportPanel);
reporttab.doLayout();
