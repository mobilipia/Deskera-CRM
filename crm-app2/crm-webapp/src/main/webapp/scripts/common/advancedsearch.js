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
Wtf.mbuild.advancedSearchComponent = function(config){
    Wtf.apply(this, config);

    this.events = {
        "filterStore": true,
        "clearStoreFilter": true
    };

    this.columnRecord = Wtf.data.Record.create([{
        name: 'dataIndex'
    },{
        name: 'header'
    },{
        name: 'xtype'
    },{
        name: 'combogridconfig'
    }
    ]);

    this.columnJsonReader = new Wtf.data.JsonReader({
        }, this.columnRecord);

    this.columnStore = new Wtf.data.Store({
        reader: this.columnJsonReader
    });

    this.columnCombo = new Wtf.form.ComboBox({
        store : this.columnStore,
        readOnly : true,
        displayField:'header',
        valueField : 'dataIndex',
        triggerAction: 'all',
        emptyText : 'Select a column...',
        mode:'local'
    })

    this.columnCombo.on("select",this.displayField,this);

    this.checkbox=new Wtf.grid.CheckboxSelectionModel();

    this.cm=new Wtf.grid.ColumnModel([{
        header: WtfGlobal.getLocaleText("crm.customreport.header.column"),//"Column",
        dataIndex:'columnDisplayName'
    },{
        header: WtfGlobal.getLocaleText("crm.mydocuments.quicksearch.mtytxt"),//"Search Text",
        dataIndex:'searchText',
        editor: new Wtf.form.TextField({
            allowBlank: false
        })
    },{
        header: WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
        dataIndex:'delField',
        renderer : function(val) {
            return "<img id='DeleteImg' class='delete' src='images/Cancel.gif' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete filter'></img>";
        }
    }
    ]);


    this.searchRecord = Wtf.data.Record.create([{
        name: 'column'
    },{
        name: 'searchText'
    },{
        name: 'xtype'
    },{
        name: 'comboid'
    },{
        name: 'columnDisplayName'
    },{
        name: 'combogridconfig'
    }]);

    this.GridJsonReader = new Wtf.data.JsonReader({
        root: "data",
        totalProperty: 'count'
    }, this.searchRecord);

    this.searchStore = new Wtf.data.Store({
        reader: this.GridJsonReader
    });

    this.on('cellclick', this.deleteFilter, this);

    Wtf.mbuild.advancedSearchComponent.superclass.constructor.call(this, {

        region :'north',
        height:150,
        hidden:true,
        store: this.searchStore,
        cm:this.cm,
        stripeRows: true,
        autoScroll : true,
        border:false,
        clicksToEdit:1,
        viewConfig: {
            forceFit:true
        },

        tbar: [this.columnCombo,'-',this.text=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.mydocuments.quicksearch.mtytxt")+" : "), this.searchText = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("crm.advancesearch.newmasterrec"),//'New Master Record',
            anchor: '95%',
            maxLength: 100,
            width:125
        }),this.add = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//'Add',
            tooltip: {
                title: WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//'Add',
                text: WtfGlobal.getLocaleText("crm.advancesearch.addbtn.ttip")//'Click to add new filter'
            },
            handler: this.addSearchFilter,
            scope: this
        }),
        this.search = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.audittrail.searchBTN"),//'Search',
            tooltip: {
                title: WtfGlobal.getLocaleText("crm.audittrail.searchBTN"),//'Search',
                text: WtfGlobal.getLocaleText("crm.advancesearch.searchBTN.ttip")//'Click to search'
            },
            handler: this.search,
            scope:this
        }),
        this.cancel = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.CLOSE"),//'Close',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.closebtn.ttip")},//'Clear search terms and close advanced search.'},
            handler: this.cancelSearch,
            scope:this
        })]
    });

}

Wtf.extend(Wtf.mbuild.advancedSearchComponent, Wtf.grid.EditorGridPanel, {

    addSearchFilter:function(){
        var column =this.columnCombo.getValue();
        
        if (column != ""){
            var index=this.searchStore.find('column',column);
            var columnRecord=this.columnStore.getAt(this.columnStore.find('dataIndex',column) );
            var xtype=columnRecord.get('xtype');

            if (xtype != undefined && xtype!= "" &&  xtype != null){
                var valid=true;
                if (xtype == 'datefield' || xtype == 'Date' ){
                    var fromDate=this.fromDate.getValue();
                    var toDate=this.toDate.getValue();
                    var stdate = (fromDate).getTime();
                    var enddate = (toDate).getTime();
                    (stdate <= enddate )?null:valid=false;
                     (fromDate!="" && toDate!="")?null:valid=false;
                }else{
                    var searchText=this.searchText.getValue();
                    (searchText!="")?null:valid=false;
                }

                if (valid){
                    var comboid="";
                    if (xtype == 'datefield' || xtype == 'Date' ){
                        searchText=fromDate+','+toDate;
                    }else{
                        if (xtype == 'combo' || xtype =='Combobox' || xtype =='userscombo'){
                            var comboRecord=this.comboStore.getAt(this.comboStore.find('id',searchText) );
                            if (!this.isReport ){
                                searchText=comboRecord.get('name');
                            }else{
                                searchText=comboRecord.get(column);
                            }
                            comboid=comboRecord.get('id');
                        }
                    }
                    var searchRecord = new this.searchRecord({
                        column: column,
                        searchText: searchText,
                        xtype:xtype,
                        comboid:comboid,
                        columnDisplayName:columnRecord.get('header'),
                        combogridconfig:columnRecord.get('combogridconfig')
                    });
                    if (index == -1 ){
                        this.searchStore.add(searchRecord);
                    }else{
                        this.searchStore.remove(this.searchStore.getAt(index ) );
                        this.searchStore.insert(index,searchRecord);
                    }
                    this.columnCombo.setValue("");
                    (this.searchText != undefined)?this.searchText.setValue(""):null;
                    (this.toDate != undefined)?this.toDate.setValue(""):null;
                    (this.fromDate != undefined)?this.fromDate.setValue(""):null;

                }else{
                    msgBoxShow(37, 1);
                }
            }else{
                msgBoxShow(42, 1);
            }
            
        } else{
            msgBoxShow(37, 1);
        }

    },
    
    doSearch:function(){
        if ( this.searchStore.getCount() > 0 ){
            var filterJson='[';
            this.searchStore.each(function(filterRecord){

                var searchText=filterRecord.data.searchText;
                var xtype=filterRecord.data.xtype;
                if (xtype == 'combo' || xtype =='Combobox' || xtype =='userscombo' ){
                    searchText=filterRecord.data.comboid;
                }
                filterJson+="{ 'column':'"+filterRecord.data.column+"','searchText':'"+searchText+"','xtype':'"+xtype+"','combogridconfig':'"+filterRecord.data.combogridconfig+"'},";
            },this);
            filterJson=filterJson.substring(0,filterJson.length-1);
            filterJson+="]";
            this.fireEvent("filterStore",filterJson);

        }else{
            msgBoxShow(38, 1);
        }
    },

    cancelSearch:function(){
        this.columnCombo.clearValue();
        (this.searchText != undefined)?this.searchText.setValue(""):null;
        (this.toDate != undefined)?this.toDate.setValue(""):null;
        (this.fromDate != undefined)?this.fromDate.setValue(""):null;
        this.searchStore.removeAll();
        this.fireEvent("clearStoreFilter");
    },

    deleteFilter:function(gd, ri, ci, e) {
        var event = e;
        if(event.target.className == "delete") {
            this.searchStore.remove(this.searchStore.getAt(ri));
            this.fireEvent("clearStoreFilter");
            this.doSearch();
        }
        
    },

    displayField:function(combo,record,index){

        if (record.get('xtype') == "None"){
            record.set('xtype','textfield');
        }
        if (this.fromtext){
            this.fromtext.destroy();
            this.fromDate.destroy();
            this.toDate.destroy();
            this.totext.destroy();
        }
        if (this.text){
            this.text.destroy();
        }

        this.searchText.destroy();
        this.add.destroy();
        this.search.destroy();
        this.cancel.destroy();
        this.doLayout();

        if (record.get('xtype') == "datefield" || record.get('xtype') == 'Date'){

            if (this.text){
                this.text.destroy();
            }
            this.fromtext=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.fromdate.label")+": ");
            this.getTopToolbar().add(this.fromtext);

            this.fromDate = new Wtf.form.DateField({
                anchor: '95%',
                maxLength: 100,
                format:Wtf.getDateFormat(),
                renderer:WtfGlobal.onlyDateRendererTZ,
                width:125
            });

            this.getTopToolbar().add(this.fromDate);
            
            this.totext=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.todate.label")+": ");
            this.toDate = new Wtf.form.DateField({
                anchor: '95%',
                maxLength: 100,
                format:Wtf.getDateFormat(),
                renderer:WtfGlobal.onlyDateRendererTZ,
                width:125
            });
            this.getTopToolbar().add(this.totext);
            this.getTopToolbar().add(this.toDate);
        }

        if (record.get('xtype') == "timefield" ){
            this.searchText = new Wtf.form.TimeField({
                anchor: '95%',
                maxLength: 100,
                renderer:WtfGlobal.loginUserTimeRendererTZ,
                width:125
            });
        }

        if (record.get('xtype') == "textfield" || record.get('xtype') == 'Text' || record.get('xtype') =='textarea'){
            this.searchText = new Wtf.form.TextField({
                anchor: '95%',
                maxLength: 100,
                width:125
            });
        }

        if (record.get('xtype') == "numberfield" || record.get('xtype') == 'Number(Integer)' || record.get('xtype') == 'Number(Float)'){
            this.searchText = new Wtf.form.NumberField({
                anchor: '95%',
                maxLength: 100,
                width:125
            });
        }

        if (record.get('xtype') == "radio" || record.get('xtype').search(/checkbox/i)!=-1  ){
            this.booleanStore = new Wtf.data.SimpleStore({
                fields :['id', 'name'],
                data:[['true','True'],['false','False']]
            });

            this.searchText = new Wtf.form.ComboBox({
                valueField: 'id',
                displayField: 'name',
                store: this.booleanStore,
                editable: false,
                anchor: '95%',
                mode: 'local',
                triggerAction: 'all',
                selectOnFocus: true,
                emptyText: WtfGlobal.getLocaleText("crm.advancesearch.searchcombo.mtytxt"),//'Select boolean value',
                width:125
            });

        }

        if (record.get('xtype') == "combo" || record.get('xtype') == "Combobox" ){
            if (!this.isReport ){
                this.comboStore = new Wtf.data.Store({
                    reader: new Wtf.data.KwlJsonReader({
                        root:'data'
                    }, ["id","name"]),
                    url : Wtf.req.mbuild+"form.do",
                    baseParams : {
                        action : this.getComboDataAction,
                        moduleid : this.moduleid,
                        name : combo.getValue()
                    }
                });
                this.displayField='name';
             }else{
                 var columnname="";
                 var reftable="";
                 if (record.get('combogridconfig') == -1){
                      columnname = combo.getValue()+','+combo.getValue().split(_reportHardcodeStr)[0]+_reportHardcodeStr+'id';
                      reftable=combo.getValue().split(_reportHardcodeStr)[0];
                 }else{
                     columnname=combo.getValue();
                     reftable="";
                 }
                this.comboStore = new Wtf.data.Store({
                    reader: new Wtf.data.KwlJsonReader({
                        root:'data'
                    }, ["id",combo.getValue()]),
                    url : "reportbuilder.jsp",
                    baseParams : {
                        action : this.getComboDataAction,
                        combogridconfig : record.get('combogridconfig'),
                        columnname : columnname,
                        reftable:reftable,
                        reportid:this.moduleid
                    }
                });
                 this.displayField=combo.getValue();
            }
            this.comboStore.load();

            this.searchText = new Wtf.form.ComboBox({
                valueField: 'id',
                displayField: this.displayField,
                store: this.comboStore,
                typeAhead:true,
                forceSelection :true,
                anchor: '95%',
                mode: 'local',
                triggerAction: 'all',
                selectOnFocus: true,
                emptyText: WtfGlobal.getLocaleText("crm.advancesearch.searchcombo.seloptmtytxt"),//'Select an option',
                width:125
            });
        }

        if (record.get('xtype') == "userscombo" ){

            this.comboStore = new Wtf.data.Store({
                reader: new Wtf.data.KwlJsonReader({
                    root:'data'
                }, ["id","name"]),
                autoLoad : false,
                url : "reportbuilder.jsp",
                baseParams : {
                    action : 19
                }
            });

            this.comboStore.load();

            this.searchText = new Wtf.form.ComboBox({
                valueField: 'id',
                displayField: "name",
                store: this.comboStore,
                typeAhead:true,
                forceSelection :true,
                anchor: '95%',
                mode: 'local',
                triggerAction: 'all',
                selectOnFocus: true,
                emptyText: WtfGlobal.getLocaleText("crm.advancesearch.searchcombo.seloptmtytxt"),//'Select an option',
                width:125
            });
        }

         if (record.get('xtype') !="datefield" && record.get('xtype') !="Date"){
            this.text=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.mydocuments.quicksearch.mtytxt")+" : ");
            this.getTopToolbar().add(this.text);
        }

        if (record.get('xtype') !="datefield" && record.get('xtype') !="Date"){
            this.getTopToolbar().add(this.searchText);
        }
        this.getTopToolbar().addButton([this.add = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//'Add',
            tooltip: {
                title: WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//'Add',
                text: WtfGlobal.getLocaleText("crm.advancesearch.addbtn.ttip")//'Click to add new filter'
            },
            handler: this.addSearchFilter,
            scope: this
        }),
        this.search = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.audittrail.searchBTN"),//'Search',
            tooltip: {
        		title: WtfGlobal.getLocaleText("crm.audittrail.searchBTN"),//'Search',
        		text: WtfGlobal.getLocaleText("crm.advancesearch.searchBTN.ttip")//'Click to search'
            },
            handler: this.doSearch,
            scope:this
        }),
        this.cancel = new Wtf.Toolbar.Button({
        	text: WtfGlobal.getLocaleText("crm.CLOSE"),//'Close',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.closebtn.ttip")},//'Clear search terms and close advanced search.'},            handler: this.cancelSearch,
            scope:this
        })]);

        this.doLayout();
    }
});

