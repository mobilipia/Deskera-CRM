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

Wtf.advancedSearchComponent = function(config){
    Wtf.apply(this, config);

    this.events = {
        "filterStore": true,
        "clearStoreFilter": true
    };

    this.combovalArr=[];
    this.xtypeArr=[];
    
    this.combostore = new Wtf.data.SimpleStore({
        fields: [
        {
            name: 'header'
        },

        {
            name: 'name'
        },
        {
            name: 'xtype'
        },
        {
            name: 'cname'
        },
        {
            name: 'iscustomcolumn'
        },
        {
            name: 'dbname'
        },
        {
            name:'sheetEditor'
        },
        {
            name:'fieldtype'
        },
        {
            name: 'refdbname'
        }
        ]
    });
    
    this.columnCombo = new Wtf.form.ComboBox({
        store : this.combostore,
        editable:false,
        selectOnFocus:true,
        displayField:'header',
        valueField : 'name',
        triggerAction: 'all',
        emptyText : WtfGlobal.getLocaleText("crm.responsealert.msg.12"),//'Select a Search Field to search',
        mode:'local'
    })

    this.columnCombo.on("select",this.displayField,this);


    this.cm=new Wtf.grid.ColumnModel([{
        header: WtfGlobal.getLocaleText("crm.customreport.header.column"),//"Column",
        dataIndex:'column'
    },{
        header: "Search1 Text",
        dataIndex:'searchText',
        hidden:true
     
    },
    {
        header: WtfGlobal.getLocaleText("crm.mydocuments.quicksearch.mtytxt"),//"Search Text",
        dataIndex:'id'
    },{
        header: WtfGlobal.getLocaleText("crm.DELETEBUTTON"),//"Delete",
        dataIndex:'delField',
        renderer : function(val, cell, row, rowIndex, colIndex, ds) {
            return "<div class='pwnd deleteButton' > </div>";
        }
    }
    ]);

    this.searchRecord = Wtf.data.Record.create([{
        name: 'column'
    },{
        name: 'searchText'
    },{
        name: 'dbname'
    },
    {
        name: 'id'
    },{
        name: 'xtype'
    },{
        name: 'refdbname'
    },{
        name: 'iscustomcolumn'
    },{
        name:'xfield'
    },{
        name:'fieldtype'
    }]);

    this.GridJsonReader = new Wtf.data.JsonReader({
        root: "data",
        totalProperty: 'count'
    }, this.searchRecord);

    this.searchStore = new Wtf.data.Store({
        reader: this.GridJsonReader
    });

     this.on("cellclick", this.deleteFilter, this);

    this.saveSearchName = new Wtf.form.TextField({
            anchor: '95%',
            maxLength: 100,
            width:125
   });
   this.saveSearch = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.advancesearch.remembersearch"),//'Remember Search',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.remembersearch.ttip")},//'Save search state.'},
            handler: this.RememberSearch,
            scope: this,
            disabled:true,
            iconCls : 'pwnd add'
    });
    
    Wtf.advancedSearchComponent.superclass.constructor.call(this, {

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

        tbar: [this.text1=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.advancesearch.searchfield")+": "),this.columnCombo,'-',this.text=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.mydocuments.quicksearch.mtytxt")+": "), this.searchText = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("crm.advancesearch.newmasterrec"),//'New Master Record',
            anchor: '95%',
            maxLength: 100,
            width:125
        }),this.add = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//'Add',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.addtermtosearchtip")},//'Add a term to search.'},
            handler: this.addSearchFilter,
            scope: this,
            iconCls : 'pwnd addfilter'
        }),
        this.search = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.audittrail.searchBTN"),//'Search',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.addtermtosearchtip")},//'Add terms to search.'},
            handler: this.doSearch,
            scope:this,
            disabled:true,
            iconCls : 'pwnd searchtabpane'
        }),
        this.cancel = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.CLOSE"),//'Close',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.closebtn.ttip")},//'Clear search terms and close advanced search. '},
            handler: this.cancelSearch,
            scope:this,
            iconCls:'pwnd clearfilter'
        }),this.saveSearch,this.saveSearchName]
    });

}

Wtf.extend(Wtf.advancedSearchComponent, Wtf.grid.EditorGridPanel, {        
    addSearchFilter:function(){
        if(this.columnCombo.getRawValue().trim()==""){
            ResponseAlert(12);
            return;
        }
        var column =this.columnCombo.getValue();
        var searchText="";
        if(this.searchText.getXType()=="numberfield" || this.searchText.getXType()=="datefield"){
            searchText=this.searchText.getValue();
        } else {
            searchText=this.searchText.getValue().trim();
        }
        if(this.searchText.getXType()=="timefield") {
           searchText =  WtfGlobal.convertToGenericTime(Date.parseDate(searchText,WtfGlobal.getLoginUserTimeFormat()));
        }
        var do1=0;
        if (column != "" &&  searchText !=""){
            this.searchText1=this.searchText.getRawValue();
            this.combovalArr.push(this.searchText1);
            if(this.searchText.getXType()=="datefield"){
                this.xtypeArr.push("datefield");
            } else{
                this.xtypeArr.push(this.searchText.store);
            }
            this.columnText="";
            if(searchText != "") {
                for(var i=0;i<this.combostore.getCount();i++) {
                    if(this.combostore.getAt(i).get("name")== column) {
                        this.columnText=this.combostore.getAt(i).get("header");
                        do1=1;
                    }
                }
                if(do1==1) {
                    this.search.enable();
                    this.saveSearch.enable();
                    this.search.setTooltip(WtfGlobal.getLocaleText("crm.advancesearch.searchonmulterms"));
                    var searchRecord = new this.searchRecord({
                        column: this.columnText,
                        searchText: searchText,
                        dbname:column,
                        id:this.searchText1,
                        xtype:this.searchText.getXType(),
                        iscustomcolumn:this.searchText.iscustomcolumn,
                        xfield:this.searchText.dbname,
                        fieldtype:this.searchText.fieldtype,
                        refdbname:this.searchText.refdbname
                    });
                    
                    var index=this.searchStore.find('column',this.columnText);
                    if (index == -1 ) {
                        this.searchStore.add(searchRecord);
                    } else {
                        this.searchStore.remove(this.searchStore.getAt(index ) );
                        this.searchStore.insert(index,searchRecord);
                    }
                }
            }
        } else {
            if(column == "") {
                ResponseAlert(12);
                } else if(searchText =="") {
                    ResponseAlert(13);
                }
            }
        this.searchText.setValue("");
    },
    getJsonofStore : function(){
       var filterJson=[];
       var i=0;
       this.searchStore.each(function(filterRecord){

            var searchText=filterRecord.data.searchText+"";
            var xtype=filterRecord.data.xtype;

            if (xtype == 'datefield' || xtype =='Date' ){
                 if(filterRecord.data.searchText && filterRecord.data.searchText.format)
                        searchText=WtfGlobal.convertToOnlyDate(filterRecord.data.searchText);
            }
            searchText = WtfGlobal.replaceAll(searchText, "\\\\" , "\\\\");
            if(this.combovalArr[i])
                this.combovalArr[i] = WtfGlobal.replaceAll(this.combovalArr[i], "\\\\" , "\\\\");
           // value =  WtfGlobal.replaceAll(value, "\\\\" , "\\\\");
            //searchText = searchText.replace(/"/g,"");
            var recdata = filterRecord.data;
            filterJson.push({
                column:encodeURIComponent(recdata.column),
                refdbname:recdata.refdbname,
                xfield:recdata.xfield,
                iscustomcolumn:recdata.iscustomcolumn,
                fieldtype:recdata.fieldtype,
                searchText:searchText,
                dbname : recdata.dbname,
                id : recdata.id,
                xtype:xtype,
                combosearch:this.combovalArr[i]
                
            });
            i++;
        },this);

        filterJson = {
            data:filterJson
        }

        return Wtf.encode(filterJson);
    },
    RememberSearch:function(){
        if ( this.searchStore.getCount() > 0 ){
            this.saveSearchName.setValue( WtfGlobal.HTMLStripper(this.saveSearchName.getValue()));
            if(this.saveSearchName.getValue()!=""){
                var json = this.getJsonofStore();
                var saveSearchName = this.saveSearchName.getValue();
                Wtf.Ajax.requestEx({
                    url:'Common/AdvanceSearch/saveSearchQuery.do',
                    params:{
                        searchstate:json,
                        module:Wtf.moduleMap[this.module],
                        searchname:saveSearchName
                    }
                },
                this,
                function(res)
                {
                    if(res.msg!=undefined){
                        Wtf.MessageBox.show({
                            title:WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"),
                            msg:res.msg,
                            icon:Wtf.MessageBox.QUESTION,
                            buttons:Wtf.MessageBox.YESNO,
                            scope:this,
                            fn:function(button){
                                if(button=='yes')
                                {
                                    this.SaveRememberSearch(json,Wtf.moduleMap[this.module],saveSearchName);
                                }
                            }
                        });
                    } else {
                        this.saveSearchName.setValue("");
                        Wtf.refreshDashboardWidget(Wtf.moduleWidget.advancesearch);
                        WtfComMsgBox(1103,0);
                    }
                    
                },
                function(res)
                {
                    WtfComMsgBox(1104,1);
                }
                )
            }else{
                 WtfComMsgBox(1105,0);
            }
        }else{
            ResponseAlert(14);
        }
    },
    SaveRememberSearch:function(json,module,searchname){
        
            Wtf.Ajax.requestEx({
                url:'Common/AdvanceSearch/saveSearchQuery.do',
                params:{
                    searchstate:json,
                    module:module,
                    searchname:searchname,
                    confirmationFlag:true
                }
            },
            this,
            function(res)
            {
                Wtf.refreshDashboardWidget(Wtf.moduleWidget.advancesearch);
                WtfComMsgBox(1103,0);
            },
            function(res)
            {
                WtfComMsgBox(1104,1);
            }
            )
            
        
    },
    doSearch:function(formatdate){
        if ( this.searchStore.getCount() > 0 ){
        var filterJson=[];
        var i=0;

        this.searchStore.each(function(filterRecord){
            ///  for combo case also searchText is combodataid
            var searchText=filterRecord.data.searchText+"";
            var value="";
            var xType = "";
            value=filterRecord.data.searchText+"";
            var xtype=filterRecord.data.xtype;
            if (xtype == 'datefield' || xtype =='Date' ){
                    if(formatdate && filterRecord.data.searchText && filterRecord.data.searchText.format)
                        searchText=WtfGlobal.convertToOnlyDate(filterRecord.data.searchText);
            }

            searchText = WtfGlobal.replaceAll(searchText, "\\\\" , "\\\\");
            if(this.combovalArr[i])
                this.combovalArr[i] = WtfGlobal.replaceAll(this.combovalArr[i], "\\\\" , "\\\\");
            value =  WtfGlobal.replaceAll(value, "\\\\" , "\\\\");
            // object is push in xtypeArr for Combo field else it is datefield. No value is push for numberfield and textfield.
            if(this.xtypeArr[i]!=undefined){
                if(this.xtypeArr[i]=="datefield"){
                    xType ='datefield'
                }else if(typeof(this.xtypeArr[i])=="object"){
                    xType ='Combo'
                }
            }else {
                xType ="";
            }
            filterJson.push({
                column:filterRecord.data.dbname,
                refdbname:filterRecord.data.refdbname,
                xfield:filterRecord.data.xfield,
                iscustomcolumn:filterRecord.data.iscustomcolumn,
                fieldtype:filterRecord.data.fieldtype,
                searchText:searchText,
                columnheader:encodeURIComponent(filterRecord.data.column),
                search:value,
                xtype:xtype,
                combosearch:this.combovalArr[i]
            });
            i++;
        },this);

        filterJson = {
            root:filterJson
        }
            this.fireEvent("filterStore",Wtf.encode(filterJson));

        }else{
            ResponseAlert(14);
            this.fireEvent("filterStore","");
        }
    },
    cancelSearch:function(){
        this.columnCombo.setValue("");
        var searchXtype = this.searchText.getXType();
        if(searchXtype=='combo')
            this.columnCombo.fireEvent("select",undefined,'');
        (this.toDate != undefined)?this.toDate.setValue(""):null;
        (this.fromDate != undefined)?this.fromDate.setValue(""):null;
        this.searchStore.removeAll();
        this.combovalArr=[];
        this.xtypeArr=[];
        this.fireEvent("clearStoreFilter");
    },

    deleteFilter:function(gd, ri, ci, e) {
        var event = e;
        if(event.target.className == "pwnd deleteButton") {
            this.searchStore.remove(this.searchStore.getAt(ri));
            if(this.searchStore.getCount()==0) {
//                this.search.disable();
                this.saveSearch.disable();
                this.search.setTooltip(WtfGlobal.getLocaleText("crm.advancesearch.addtermtosearchtip"));
            }
            this.combovalArr.splice(ri,1);
            this.xtypeArr.splice(ri,1);
            this.doSearch();
        }
        
    },

	displayField:function(combo,record){
        if(record == '')
            var recXtype = "textfield";
        else
            recXtype=record.get('xtype');
        if (recXtype == "None"){
            record.set('xtype','textfield');
        }

        if (this.text){
            this.text.destroy();
        }
        this.saveSearchName.destroy();
        this.saveSearch.destroy();

        this.searchText.destroy();
        this.add.destroy();
        this.search.destroy();
        this.cancel.destroy();
        this.doLayout();
        var iscustomcolumn,fieldtype,refdbname,dbname;
        if(record!=''){
             iscustomcolumn=record.get('iscustomcolumn');
             fieldtype=record.get('fieldtype');
             refdbname = record.get('refdbname');
             dbname=record.get('dbname');
        }
        iscustomcolumn = iscustomcolumn?iscustomcolumn:"";
        if (recXtype == "textfield" || recXtype == 'Text' || recXtype =='textarea'){
            this.searchText = new Wtf.form.TextField({
                anchor: '95%',
                maxLength: 100,
                width:125,
                iscustomcolumn:iscustomcolumn,
                fieldtype:fieldtype,
                dbname:dbname,
                refdbname:refdbname
            });
        }

        if (recXtype == "numberfield" || recXtype == 'Number(Integer)' || recXtype == 'Number(Float)'){
            this.searchText = new Wtf.form.NumberField({
                anchor: '95%',
                maxLength: 100,
                width:125,
                iscustomcolumn:iscustomcolumn,
                fieldtype:fieldtype,
                dbname:dbname,
                refdbname:refdbname
            });
        }



        if (recXtype == "combo" || recXtype == "Combobox" || recXtype == "select" ){
               
                var editor = record.data.sheetEditor;
                 var comboReader = new Wtf.data.Record.create([
                {
                    name: 'id',
                    type: 'string'
                },
                {
                    name: 'name',
                    type: 'string'
                }
                ]);
                var comboboxname=record.get('cname')
                
                if(iscustomcolumn){
                        this.comboStore = record.get('sheetEditor').store;
                }else {
                    if(comboboxname=="LeadType"){
                        this.comboStore = Wtf.LeadTypeStore;
                    } else if(comboboxname=='Account') {
//                        if(editor && editor.searchStoreCombo) {
                            this.comboStore = Wtf.parentaccountstoreSearch
//                        }
//                        else {
//                            chkrelatedToNameStoreload();
//                            this.comboStore = Wtf.relatedToNameStore
//                        }
                    } else if(comboboxname=='Product'){
                        chkproductStoreload()
                        this.comboStore = Wtf.productStore
                    } else if(comboboxname=='Contact'){
//                        if(editor && editor.searchStoreCombo) {
                            this.comboStore = Wtf.contactStoreSearch;
//                        }
//                        else {
//                            chkcontactstorestoreload()
//                            this.comboStore = Wtf.contactstore;
//                        }
                    } else if(comboboxname=='Campaign Type'){
                        chkviewStoreTypeload()
                        this.comboStore = Wtf.viewStoreType;
                    } else if(comboboxname=='Campaign Status'){
                        chkviewStoreStatusload()
                        this.comboStore = Wtf.viewStoreStatus;
                    } else if(comboboxname=='Product Category'){
                        chkproductcategorystoreload()
                        this.comboStore = Wtf.productcategorystore;
                    } else if(comboboxname=='Industry'){
                        chkindustryload()
                        this.comboStore = Wtf.industryStore;
                    } else if(comboboxname=='Title'){
                        chktitleload()
                        this.comboStore = Wtf.titleStore;
                    } else if(comboboxname=='Lead Status'){
                        chklstatusStoreload()
                        this.comboStore = Wtf.lstatusStore;
                    } else if(comboboxname=='Lead Rating'){
                        chklratingStoreload()
                        this.comboStore = Wtf.lratingStore;
                    } else if(comboboxname=='Lead Source'){
                        chkleadsourceload()
                        this.comboStore = Wtf.lsourceStore;
                    } else if(comboboxname=='Account Type'){
                        chkaccounttypeload()
                        this.comboStore = Wtf.accountTypeStore;
                    } else if(comboboxname=='Opportunity Type'){
                        chkopptypeStoreload()
                        this.comboStore = Wtf.opptypeStore;
                    } else if(comboboxname=='Region'){
                        chkregionStoreload()
                        this.comboStore = Wtf.regionStore;
                    } else if(comboboxname=='Opportunity Stage'){
                        chkoppstageload()
                        this.comboStore = Wtf.oppstageStore;
                    } else if(comboboxname=='Case Type'){
                        chkcaseoriginStoreload()
                        this.comboStore = Wtf.caseoriginStore;
                    } else if(comboboxname=='Priority'){
                        chkpriorityload()
                        this.comboStore = Wtf.cpriorityStore;
                    } else if(comboboxname=='Case Status'){
                        chkstatusload()
                        this.comboStore = Wtf.caseStatusStore;
                    } else if(comboboxname=='assignedto'){
                        this.comboStore = Wtf.caseAssignedUserStore
                    } else if(comboboxname=='Task Type'){
                        chktasktypeload()
                        this.comboStore = Wtf.typeStore;
                    } else if(comboboxname=='Task Status'){
                        chktaskstatusload()
                        this.comboStore = Wtf.statusStore;
                    } else if(comboboxname=='owner'){
                        this.comboStore = new Wtf.data.Store({
                            reader: new Wtf.data.KwlJsonReader({
                                root:'data'
                            }, comboReader),
                            url: "Common/User/getOwner.do?module="+this.module
                        });
                        this.comboStore.load({params:{common:'1'}});
                    }
                }
                 this.displayField=combo.getValue();

            this.searchText = new Wtf.form.ComboBox({
                valueField: 'id',
               // displayField: this.displayField,
                displayField: 'name',
                store: this.comboStore,
                typeAhead:true,
                forceSelection :true,
                anchor: '95%',
                mode: editor.mode ? (editor.storemanagerkey ? (Wtf.StoreMgr.containsKey(editor.storemanagerkey) ? "local":editor.mode):editor.mode) : "local",
                minChars : 2,
                triggerClass : (editor.searchStoreCombo && !editor.loadOnSelect) ? "dttriggerForTeamLead" : "",
                triggerAction: 'all',
                selectOnFocus: true,
                emptyText: editor.searchStoreCombo && !editor.loadOnSelect ? WtfGlobal.getLocaleText("crm.advancesearch.searchanoption"):WtfGlobal.getLocaleText("crm.advancesearch.searchcombo.seloptmtytxt"),
                width:125,
                iscustomcolumn:iscustomcolumn,
                fieldtype:fieldtype,
                dbname:dbname,
                refdbname:refdbname
            });
        }

        if (recXtype == "datefield" || recXtype == 'Date' ){
            this.searchText=new Wtf.form.DateField({
                width:125,
                format:"M d, Y ",
                iscustomcolumn:iscustomcolumn,
                fieldtype:fieldtype,
                dbname:dbname,
                refdbname:refdbname
            });
        }
        if (recXtype == "timefield"  ){
            this.searchText=new Wtf.form.TimeField({
                width:125,
                value:WtfGlobal.setDefaultValueTimefield(),
                format:WtfGlobal.getLoginUserTimeFormat(),
                iscustomcolumn:iscustomcolumn,
                fieldtype:fieldtype,
                dbname:dbname,
                refdbname:refdbname
            });
        }
            this.text=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.mydocuments.quicksearch.mtytxt")+" : ");
            this.getTopToolbar().add(this.text);
            this.getTopToolbar().add(this.searchText);
        this.getTopToolbar().addButton([this.add = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//'Add',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.addtermtosearchtip")},//'Add a term to search.'},
            handler: this.addSearchFilter,
            scope: this,
            iconCls : 'pwnd addfilter'
        }),
        this.search = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.audittrail.searchBTN"),//'Search',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.addtermtosearchtip")},//'Add terms to search.'},
            handler: this.doSearch,
            disabled:true,
            scope:this,
            iconCls : 'pwnd searchtabpane'
        }),
        this.cancel = new Wtf.Toolbar.Button({
            text:  WtfGlobal.getLocaleText("crm.CLOSE"),//'Close',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.closebtn.ttip")},//'Clear search terms and close advanced search.'},
            handler: this.cancelSearch,
            scope:this,
            iconCls:'pwnd clearfilter'
        })]);
        this.saveSearchName = new Wtf.form.TextField({
            anchor: '95%',
            maxLength: 100,
            width:125
        });
        this.saveSearch = new Wtf.Toolbar.Button({
                text: WtfGlobal.getLocaleText("crm.advancesearch.remembersearch"),//'Remember Search',
                tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.remembersearch.ttip")},//'Save search state.'},
                handler: this.RememberSearch,
                scope: this,
                disabled:true,
                iconCls : 'pwnd add'
        });

        this.getTopToolbar().add(this.saveSearch);
        this.getTopToolbar().add(this.saveSearchName);
        
        this.add.getEl().dom.style.paddingLeft="4px";
        this.doLayout();
    },

    getComboData: function(){
        if(!this.myData){
            var mainArray=[];
            for (var i=0;i<this.cm.length;i++) {
                var tmpArray=[];
                if(this.cm[i].dbname && !NotAllowSearchFields[this.cm[i].dbname] && (this.cm[i].sheetEditor||this.cm[i].editor) && (this.cm[i].hidden==undefined || this.cm[i].hidden==false)) {
                    var header=headerCheck(WtfGlobal.HTMLStripper(this.cm[i].header));
                    header=header.replace("*","");
                    header=header.trim();
                    tmpArray.push(header);
                    tmpArray.push(this.cm[i].dbname);
                    tmpArray.push(this.cm[i].xtype);
                    tmpArray.push(this.cm[i].cname);
                    tmpArray.push(this.cm[i].iscustomcolumn);
                    tmpArray.push(this.cm[i].dbname);
                    tmpArray.push(this.cm[i].sheetEditor||this.cm[i].editor.field);
                    tmpArray.push(this.cm[i].fieldtype);
                    tmpArray.push(this.cm[i].refdbname);
                    mainArray.push(tmpArray)
                }
            }
            this.myData = mainArray;
            if(this.advSearch)
                this.combostore.loadData(this.myData);
        }
    }

});

///////////////////////////////////////////


Wtf.advancedSearchComponentForCustomReport = function(config){
    Wtf.apply(this, config);

    this.events = {
        "customReportFilterStore": true,
        "customReportClearStoreFilter": true
    };

    this.combovalArr=[];
    this.xtypeArr=[];

    this.combostore = new Wtf.data.SimpleStore({
        fields: [
        {
            name: 'name'
        },
        {
            name: 'dbcolumnname'
        },
        {
            name: 'displayname'
        },
        {
            name: 'modulename'
        },
        {
            name: 'tablename'
        },{
            name: "defaultname"
        },
        {
            name:'type'
        },{
            name :'iscustomcolumn'
        },{
            name :'pojoname'
        },{
            name :'dataindex'
        },{
            name :'configid'
        }
        ]
    });

    this.columnCombo = new Wtf.form.ComboBox({
        store : this.combostore,
        editable: false,
        selectOnFocus:true,
        displayField:'displayname',
        valueField : 'name',
        triggerAction: 'all',
        emptyText : WtfGlobal.getLocaleText("crm.responsealert.msg.12"),//'Select a Search Field to search',
        mode:'local'
    })

    this.columnCombo.on("select",this.displayField,this);


    this.cm=new Wtf.grid.ColumnModel([{
        header: WtfGlobal.getLocaleText("crm.customreport.header.column"),//"Column",
        dataIndex:'column'
    },{
        header: WtfGlobal.getLocaleText("crm.advancesearch.search1txt"),//"Search1 Text",
        dataIndex:'searchText',
        hidden:true

    },
    {
        header: WtfGlobal.getLocaleText("crm.mydocuments.quicksearch.mtytxt"),//"Search Text",
        dataIndex:'id'
    },{
        header:WtfGlobal.getLocaleText("crm.DELETEBUTTON"),// "Delete",
        dataIndex:'delField',
        renderer : function(val, cell, row, rowIndex, colIndex, ds) {
            return "<div class='pwnd deleteButton' > </div>";
        }
    }
    ]);

    this.searchRecord = Wtf.data.Record.create([{
        name: 'column'
    },{
        name: 'searchText'
    },{
        name: 'dbname'
    },
    {
        name: 'id'
    },{
        name: 'xtype'
    },{
        name: 'refdbname'
    },{
        name: 'iscustomcolumn'
    },{
        name:'xfield'
    },{
        name:'fieldtype'
    },{
        name:'dataindex'
    },{
        name :'configid'
    }]);

    this.GridJsonReader = new Wtf.data.JsonReader({
        root: "data",
        totalProperty: 'count'
    }, this.searchRecord);

    this.searchStore = new Wtf.data.Store({
        reader: this.GridJsonReader
    });

     this.on("cellclick", this.deleteFilter, this);

    Wtf.advancedSearchComponentForCustomReport.superclass.constructor.call(this, {

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

        tbar: [this.text1=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.advancesearch.searchfield")+": "),this.columnCombo,'-',this.text=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.mydocuments.quicksearch.mtytxt")+": "), this.searchText = new Wtf.form.TextField({
            fieldLabel: WtfGlobal.getLocaleText("crm.advancesearch.newmasterrec"),//'New Master Record',
            anchor: '95%',
            maxLength: 100,
            width:125
        }),this.add = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.addtermtosearchtip")},//'Add a term to search.'},
            handler: this.addSearchFilter,
            scope: this,
            iconCls : 'pwnd addfilter'
        }),
        this.search = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.advancesearch.savefilterbtn"),//'Save Filter',
            tooltip: {text:  WtfGlobal.getLocaleText("crm.advancesearch.savefilterbtn.ttip")},//'Click to save filter for this report.'},
            handler: this.doSearch,
            scope:this,
            disabled:true,
            iconCls : 'pwnd searchtabpane'
        }),
        this.cancel = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.CLOSE"),// 'Close',
            tooltip: {text:this.call_from_custom_report? WtfGlobal.getLocaleText("crm.advancesearch.clobtntip"):WtfGlobal.getLocaleText("crm.advancesearch.closebtn.ttip")},//'Clear search terms and close the search. ' :'Clear search terms and close advanced search. '},
            handler: this.confirmationToClose,
            scope:this,
            iconCls:'pwnd clearfilter'
        })]
    });

}

Wtf.extend(Wtf.advancedSearchComponentForCustomReport, Wtf.grid.EditorGridPanel, {
    addSearchFilter:function(){
        if(this.columnCombo.getRawValue().trim()==""){
            ResponseAlert(12);
            return;
        }
//        var column =this.columnCombo.getValue();
        var column =this.columnCombo.getRawValue();
        var searchText="";
        if(this.searchText.getXType()=="numberfield" || this.searchText.getXType()=="datefield"){
            searchText=this.searchText.getValue();
        } else {
            searchText=this.searchText.getValue().trim();
        }
        if(this.searchText.getXType()=="timefield") {
           searchText =  WtfGlobal.convertToGenericTime(Date.parseDate(searchText,WtfGlobal.getLoginUserTimeFormat()));
        }
        var do1=0;
        if (column != "" &&  searchText !=""){
            this.searchText1=this.searchText.getRawValue();
            this.combovalArr.push(this.searchText1);
            if(this.searchText.getXType()=="datefield"){
                this.xtypeArr.push("datefield");
            } else{
                this.xtypeArr.push(this.searchText.store);
            }
            this.columnText="";
            this.dataInd="";
            this.configID="";
            if(searchText != "") {
                for(var i=0;i<this.combostore.getCount();i++) {
//                    if(this.combostore.getAt(i).get("name")== column) {
//                        this.columnText=this.combostore.getAt(i).get("name");
                    if(this.combostore.getAt(i).get("displayname")== column) {
                        this.columnText=this.combostore.getAt(i).get("displayname");
                        this.dataInd=this.combostore.getAt(i).get("dataindex");
                        this.configID=this.combostore.getAt(i).get("configid");
                        do1=1;
                    }
                }
                if(do1==1) {
                    this.search.enable();
                   // this.saveSearch.enable();
                    this.search.setTooltip(WtfGlobal.getLocaleText("crm.advancesearch.searchonmulterms"));//'Search on multiple terms');
                    var searchRecord = new this.searchRecord({
                        column: this.columnText,
                        searchText: searchText,
                        dbname:column,
                        id:this.searchText1,
                        xtype:this.searchText.getXType(),
                        iscustomcolumn:this.searchText.iscustomcolumn,
                        xfield:this.searchText.dbname,
                        fieldtype:this.searchText.fieldtype,
                        refdbname:this.searchText.refdbname,
                        dataindex:this.dataInd,
                        configid:this.configID
                    });

                    var index=this.searchStore.find('column',this.columnText);
                    if (index == -1 ) {
                        this.searchStore.add(searchRecord);
                    } else {
                        this.searchStore.remove(this.searchStore.getAt(index ) );
                        this.searchStore.insert(index,searchRecord);
                    }
                }
            }
        } else {
            if(column == "") {
                ResponseAlert(12);
                } else if(searchText =="") {
                    ResponseAlert(13);
                }
            }
        this.searchText.setValue("");
    },

    doSearch:function(formatdate){
        if ( this.searchStore.getCount() > 0 ){
        var filterJson='{"root":[';
        var i=0;

        this.searchStore.each(function(filterRecord){
            ///  for combo case also searchText is combodataid
            var searchText=filterRecord.data.searchText+"";
            var value="";
            var xType = "";
            value=filterRecord.data.searchText+"";
            var xtype=filterRecord.data.xtype;
            if (xtype == 'datefield' || xtype =='Date' ){
                    if(formatdate && filterRecord.data.searchText && filterRecord.data.searchText.format)
                        searchText=WtfGlobal.convertToOnlyDate(filterRecord.data.searchText);
            }

            searchText = searchText.replace(/"/g,"");
            if(this.combovalArr[i])
                this.combovalArr[i] = this.combovalArr[i].replace(/"/g,"");
            value =  value.replace(/"/g,"");
            // object is push in xtypeArr for Combo field else it is datefield. No value is push for numberfield and textfield.
            if(this.xtypeArr[i]!=undefined){
                if(this.xtypeArr[i]=="datefield"){
                    xType ='datefield'
                }else if(typeof(this.xtypeArr[i])=="object"){
                    xType ='Combo'
                }
            }else {
                xType ="";
            }
            
            // HELP
            //
            //column : tablename.columnname.
            //refdbname : table name in database
            //xfield : Name of column in database
            //iscustomcolumn : true for custom colummn and false for default column
            //fieldType : 1(textfield) , 2 (Numberfield) , 3 (datefield) , 4 (Combofield) ,5( Timefield)
            //searchText : search text ( id for combo ie value field )
            //columnheader : header of column in column model ( decoded)
            //search : same as searchText
            //xtype : xtype of field like textfield, numberfield ,combo etc
            //combosearch  : displayfield
            //pojoheadername  : pojoheader for custom column

            
            filterJson+='{ "column":"'+filterRecord.data.dataindex+'","refdbname":"'+filterRecord.data.refdbname+'","xfield":"'+filterRecord.data.xfield+'","iscustomcolumn":"'+filterRecord.data.iscustomcolumn+'","fieldtype":"'+filterRecord.data.fieldtype+'","searchText":"'+searchText+'","columnheader":"'+encodeURIComponent(filterRecord.data.column)+'","search":"'+value+'","xtype":"'+xtype+'","pojoname":"'+filterRecord.data.pojoname+'","combosearch":"'+this.combovalArr[i]+'"},';
            i++;
        },this);

        filterJson=filterJson.substring(0,filterJson.length-1);
        filterJson+="]}";
            this.fireEvent("customReportFilterStore",filterJson);

        }else{
            ResponseAlert(14);
            this.fireEvent("customReportFilterStore","");
        }
    },
    cancelSearch:function(){
        this.columnCombo.setValue("");
        var searchXtype = this.searchText.getXType();
        (this.toDate != undefined)?this.toDate.setValue(""):null;
        (this.fromDate != undefined)?this.fromDate.setValue(""):null;
        this.searchStore.removeAll();
        this.combovalArr=[];
        this.xtypeArr=[];
        this.fireEvent("customReportClearStoreFilter");
    },
    confirmationToClose:function(){
       if(this.searchStore.data.items.length>0){
             Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),//"Alert",
                msg:WtfGlobal.getLocaleText("crm.advancesearch.filternotsavedmsg"),//"Filters applied, are not saved. Do you want to exit without saving it?",
                buttons:Wtf.MessageBox.YESNO,
                animEl:'mb9',
                fn:function(btn){
                    if(btn=="yes"){
                        this.cancelSearch();
                    } 
                },
                scope:this,
                icon: Wtf.MessageBox.QUESTION
            });
       }else {
           this.cancelSearch();
       }
    },

    deleteFilter:function(gd, ri, ci, e) {
        var event = e;
        if(event.target.className == "pwnd deleteButton") {
            this.searchStore.remove(this.searchStore.getAt(ri));
            if(this.searchStore.getCount()==0) {
                this.search.disable();
                this.search.setTooltip(WtfGlobal.getLocaleText("crm.advancesearch.addtermtosearchtip"));
            }
            this.combovalArr.splice(ri,1);
            this.xtypeArr.splice(ri,1);
        }
    },

	displayField:function(combo,record){
       
        if(record == '')
            var recXtype = "textfield";
        else
            recXtype=record.get('type');
        if (recXtype == "None"){
            record.set('type','textfield');
        }

        if (this.text){
            this.text.destroy();
        }
        if(this.searchText)
            this.searchText.destroy();
        this.add.destroy();
        this.search.destroy();
        this.cancel.destroy();
        this.doLayout();
        var name,dbcolumnname,displayname,modulename,tablename,type,iscustomcolumn;
        if(record!=''){
             modulename=record.get('modulename');
             type=record.get('type');
             tablename = record.get('tablename');
             dbcolumnname=record.get('dbcolumnname');
             iscustomcolumn=record.get('iscustomcolumn');
             
        }
         var a = record!=''?record.get('type'):""
         var xtype ="";
         switch(a){
            case "1":
                xtype = "textfield";
                break;
            case "2":
                xtype = "numberfield";
                break;
            case "3":
                xtype = "Datefield";
                break;
            case "4":
                xtype = "Combo";
                break;
            case "5":
                xtype = "Timefield";
                break;
            case "6":
                xtype = "Checkbox";
                break;
            case "7":
                xtype = "Multiselect Combo";
                break;
            case "8":
                xtype = "Ref. Combo";
                break;

        }
        recXtype = xtype;
        if (recXtype == "textfield"){
            this.searchText = new Wtf.form.TextField({
                anchor: '95%',
                maxLength: 100,
                width:125,
                iscustomcolumn:iscustomcolumn,
                fieldtype:type,
                dbname:dbcolumnname,
                refdbname:tablename
            });
        }

        if (recXtype == "numberfield" ){
            this.searchText = new Wtf.form.NumberField({
                anchor: '95%',
                maxLength: 100,
                width:125,
                iscustomcolumn:iscustomcolumn,
                fieldtype:type,
                dbname:dbcolumnname,
                refdbname:tablename
            });
        }

        if (recXtype == "Combo" || recXtype == "Multiselect Combo" || recXtype == "Ref. Combo"){
                var editor = record.data.sheetEditor;
                var comboReader = new Wtf.data.Record.create([
                {
                    name: 'id',
                    type: 'string'
                },
                {
                    name: 'name',
                    type: 'string'
                }
                ]);
                var comboboxname=record.get('defaultname')

                if(!iscustomcolumn){ // for Default Column
                    if(modulename=="Campaign"){

                        if(comboboxname=='Campaign Type' || comboboxname=='Type'){
                            chkviewStoreTypeload()
                            this.comboStore = Wtf.viewStoreType;
                        } else if(comboboxname=='Campaign Status' || comboboxname=='Status'){
                            chkviewStoreStatusload()
                            this.comboStore = Wtf.viewStoreStatus;
                        }
                        
                    } else if(modulename=="Lead"){

                        if(comboboxname=="LeadType"){
                            this.comboStore = Wtf.LeadTypeStore;
                        } else if(comboboxname=='Industry'){
                            chkindustryload()
                            this.comboStore = Wtf.industryStore;
                        } else if(comboboxname=='Lead Status'){
                            chklstatusStoreload()
                            this.comboStore = Wtf.lstatusStore;
                        } else if(comboboxname=='Lead Rating' || comboboxname=='Rating'){
                            chklratingStoreload()
                            this.comboStore = Wtf.lratingStore;
                        } else if(comboboxname=='Lead Source'){
                            chkleadsourceload()
                            this.comboStore = Wtf.lsourceStore;
                        } else if(comboboxname=='Product'|| comboboxname=='Product Name'){
                            chkproductStoreload()
                            this.comboStore = Wtf.productStore
                        }

                    } else if(modulename=="Account"){

                        if(comboboxname=='Account'|| comboboxname=='Account Name') {
                            this.comboStore = Wtf.parentaccountstoreSearch
                        }else if(comboboxname=='Account Type' || comboboxname=='Type'){
                            chkaccounttypeload()
                            this.comboStore = Wtf.accountTypeStore;
                        }else if(comboboxname=='Industry'){
                            chkindustryload()
                            this.comboStore = Wtf.industryStore;
                        }else if(comboboxname=='Product'){
                            chkproductStoreload()
                            this.comboStore = Wtf.productStore
                        }else if(comboboxname=='Lead Source'){
                            chkleadsourceload()
                            this.comboStore = Wtf.lsourceStore;
                        }else if(comboboxname=='Lead Status'){
                            chklstatusStoreload()
                            this.comboStore = Wtf.lstatusStore;
                        }

                    } else if(modulename=="Contact"){

                        if(comboboxname=='Contact'){
                            this.comboStore = Wtf.contactStoreSearch;
                        }else if(comboboxname=='Title'){
                            chktitleload()
                            this.comboStore = Wtf.titleStore;
                        }else if(comboboxname=='Industry'){
                            chkindustryload()
                            this.comboStore = Wtf.industryStore;
                        }else if(comboboxname=='Lead Source'){
                            chkleadsourceload()
                            this.comboStore = Wtf.lsourceStore;
                        }else if(comboboxname=='Account' || comboboxname=='Account name'|| comboboxname=='Account Name') {
                            this.comboStore = Wtf.relatedToNameStoreSearch
                        }

                    } else if(modulename=="Opportunity"){

                        if(comboboxname=='Opportunity Type' || comboboxname=='Type'){
                            chkopptypeStoreload()
                            this.comboStore = Wtf.opptypeStore;
                        } else if(comboboxname=='Region'){
                            chkregionStoreload()
                            this.comboStore = Wtf.regionStore;
                        } else if(comboboxname=='Opportunity Stage' || comboboxname=='Stage'){
                            chkoppstageload()
                            this.comboStore = Wtf.oppstageStore;
                        } else if(comboboxname=='Account' || comboboxname=='Account name'|| comboboxname=='Account Name') {
                            this.comboStore = Wtf.relatedToNameStoreSearch
                        } else if(comboboxname=='Lead Source'){
                            chkleadsourceload()
                            this.comboStore = Wtf.lsourceStore;
                        } else if(comboboxname=='Product'|| comboboxname=='Product Name'){
                            chkproductStoreload()
                            this.comboStore = Wtf.productStore
                        }

                    } else if(modulename=="Case"){

                        if(comboboxname=='Case Type' || comboboxname=='Type'){
                            chkcaseoriginStoreload()
                            this.comboStore = Wtf.caseoriginStore;
                        } else if(comboboxname=='Priority'){
                            chkpriorityload()
                            this.comboStore = Wtf.cpriorityStore;
                        } else if(comboboxname=='Case Status' || comboboxname=='Status'){
                            chkstatusload()
                            this.comboStore = Wtf.caseStatusStore;
                        } else if(comboboxname=='owner' || comboboxname=='Owner'){
                            this.comboStore = new Wtf.data.Store({
                                reader: new Wtf.data.KwlJsonReader({
                                    root:'data'
                                }, comboReader),
                                url: "Common/User/getOwner.do?module="+this.module
                            });
                            this.comboStore.load({params:{common:'1'}});
                        } else if(comboboxname=='assignedto'){
                            this.comboStore = Wtf.caseAssignedUserStore
                        } else if(comboboxname=='Account' || comboboxname=='Account name'|| comboboxname=='Account Name') {
                            this.comboStore = Wtf.relatedToNameStoreSearch
                        }else if(comboboxname=='Contact' || comboboxname=='Contact Name'){
                            this.comboStore = Wtf.contactStoreSearch;
                        }else if(comboboxname=='Product'|| comboboxname=='Product Name'){
                            chkproductStoreload()
                            this.comboStore = Wtf.productStore
                        }

                    } else if(modulename=="Product"){

                        if(comboboxname=='Product'){
                            chkproductStoreload()
                            this.comboStore = Wtf.productStore
                        }else if(comboboxname=='Product Category' || comboboxname=='Category'){
                            chkproductcategorystoreload()
                            this.comboStore = Wtf.productcategorystore;
                        }
                        
                    } else {

                        if(comboboxname=='Task Type'){
                            chktasktypeload()
                            this.comboStore = Wtf.typeStore;
                        } else if(comboboxname=='Task Status'){
                            chktaskstatusload()
                            this.comboStore = Wtf.statusStore;
                        } else if(comboboxname=='owner'){
                            this.comboStore = new Wtf.data.Store({
                                reader: new Wtf.data.KwlJsonReader({
                                    root:'data'
                                }, comboReader),
                                url: "Common/User/getOwner.do?module="+this.module
                            });
                            this.comboStore.load({params:{common:'1'}});
                        }
                    }
                    
                } else {  // for custom column
                        if(type=='8'){ // for reference custom combo
                                var cnfgID = record.data.configid
                                if(cnfgID=="users"){

                                    this.comboStore = new Wtf.data.Store({
                                        reader: new Wtf.data.KwlJsonReader({
                                            root:'data'
                                        }, comboReader),
                                        url: "Common/User/getOwner.do?module="+this.module
                                    });
                                    this.comboStore.load({params:{common:'1',allUsers:true}});

                                } else {
                                    this.comboStore = new Wtf.data.Store({
                                        url: 'Common/CRMManager/getRefComboData.do',
                                        baseParams:{
                                            common:'1',
                                            customflag:1,
                                            configid :cnfgID
                                        },
                                        reader: new Wtf.data.KwlJsonReader({
                                            root:'data'
                                        }, comboReader)
                                    });

                                    this.comboStore.load();
                               }


                        } else {
                                this.comboStore = new Wtf.data.Store({
                                    url: 'Common/CRMManager/getComboData.do',
                                    baseParams:{
                                        comboname:comboboxname,
                                        common:'1',
                                        customflag:1,
                                        configid :record.data.pojoname
                                    },
                                    reader: new Wtf.data.KwlJsonReader({
                                        root:'data'
                                    }, comboReader)
                                });

                                this.comboStore.load();
                        }
                }

            this.displayField=combo.getValue();

            this.searchText = new Wtf.form.ComboBox({
                valueField: 'id',
                displayField: 'name',
                store: this.comboStore,
                typeAhead:true,
                forceSelection :true,
                anchor: '95%',
                mode: 'local',
                triggerAction: 'all',
                selectOnFocus: true,
                emptyText: WtfGlobal.getLocaleText("crm.advancesearch.searchcombo.seloptmtytxt"),//'Select an option',
                width:125,
                iscustomcolumn:iscustomcolumn,
                fieldtype:type,
                dbname:dbcolumnname,
                refdbname:tablename
            });
        }

        if (recXtype == "Datefield"){
            this.searchText=new Wtf.form.DateField({
                width:125,
                format:"M d, Y ",
                iscustomcolumn:iscustomcolumn,
                fieldtype:type,
                dbname:dbcolumnname,
                refdbname:tablename
            });
        }
        if (recXtype == "Timefield"  ){
            this.searchText=new Wtf.form.TimeField({
                width:125,
                value:WtfGlobal.setDefaultValueTimefield(),
                format:WtfGlobal.getLoginUserTimeFormat(),
                iscustomcolumn:iscustomcolumn,
                fieldtype:type,
                dbname:dbcolumnname,
                refdbname:tablename
            });
        }
            this.text=new Wtf.Toolbar.TextItem(WtfGlobal.getLocaleText("crm.mydocuments.quicksearch.mtytxt")+": ");
            this.getTopToolbar().add(this.text);
            

            this.getTopToolbar().add(this.searchText);
        this.getTopToolbar().addButton([this.add = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.targetlists.creatarr.addBTN"),//'Add',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.addtermtosearchtip")},//'Add a term to search.'},
            handler: this.addSearchFilter,
            scope: this,
            iconCls : 'pwnd addfilter'
        }),
        this.search = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.advancesearch.savefilterbtn"),//WtfGlobal.getLocaleText("crm.advancesearch.savefilterbtn"),//
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.savefilterbtn.ttip")},//'Click to save filter for this report.'},
            handler: this.doSearch,
            disabled:true,
            scope:this,
            iconCls : 'pwnd searchtabpane'
        }),
        this.cancel = new Wtf.Toolbar.Button({
            text: WtfGlobal.getLocaleText("crm.CLOSE"),//'Close',
            tooltip: {text: WtfGlobal.getLocaleText("crm.advancesearch.closebtn.ttip")},//'Clear search terms and close advanced search.'},
            handler: this.confirmationToClose,
            scope:this,
            iconCls:'pwnd clearfilter'
        })]);

        this.add.getEl().dom.style.paddingLeft="4px";
        this.doLayout();
    },

    getComboData: function(grid,reconfigurestore){
        this.storeItmes = grid.getStore().data.items
        if(!this.myData || reconfigurestore==true){
            var mainArray=[];
            for (var i=0;i<this.storeItmes.length;i++) {
                var tmpArray=[];
                if(this.storeItmes[i].data.name!=undefined || this.storeItmes[i].data.name!="") {
                    var header=headerCheck(WtfGlobal.HTMLStripper(this.storeItmes[i].data.name));
                    header=header.replace("*","");
                    header=header.trim();
                    
                    var modulename = this.storeItmes[i].data.modulename!=undefined?this.storeItmes[i].data.modulename:"";
                    var displayname = this.storeItmes[i].data.displayname!=undefined?this.storeItmes[i].data.displayname:"";
                    if(modulename != "") {
                        displayname += " [" + modulename + "]";
                    }
                    tmpArray.push(header);
                    tmpArray.push(this.storeItmes[i].data.dbcolumnname!=undefined?this.storeItmes[i].data.dbcolumnname:"");
                    tmpArray.push(displayname);
                    tmpArray.push(modulename);
                    tmpArray.push(this.storeItmes[i].data.tablename!=undefined?this.storeItmes[i].data.tablename:"");
                    tmpArray.push(this.storeItmes[i].data.defaultname!=undefined?this.storeItmes[i].data.defaultname:"");
                    tmpArray.push(this.storeItmes[i].data.type!=undefined?this.storeItmes[i].data.type:"");
                    tmpArray.push(this.storeItmes[i].data.iscustomcolumn!=undefined?this.storeItmes[i].data.iscustomcolumn:"");
                    tmpArray.push(this.storeItmes[i].data.pojoname!=undefined?this.storeItmes[i].data.pojoname:"");
                    tmpArray.push(this.storeItmes[i].data.dataindex!=undefined?this.storeItmes[i].data.dataindex:"");
                    tmpArray.push(this.storeItmes[i].data.configid!=undefined?this.storeItmes[i].data.configid:"");
 
                    mainArray.push(tmpArray)
                }
            }
            this.myData = mainArray;
            if(this.advSearch)
                this.combostore.loadData(this.myData);
        }
    }

});

