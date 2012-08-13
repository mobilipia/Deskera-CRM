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
Wtf.KWLQuickSearch = function(config){
    Wtf.KWLQuickSearch.superclass.constructor.call(this, config);
}
Wtf.extend(Wtf.KWLQuickSearch, Wtf.form.TextField, {
    Store: null,
    StorageArray: null,
    initComponent: function(){
        Wtf.KWLQuickSearch.superclass.initComponent.call(this);
        this.addEvents({
            'SearchComplete': true
        });
    },
    onRender: function(ct, position){
        Wtf.KWLQuickSearch.superclass.onRender.call(this, ct, position);
        this.el.dom.onkeyup = this.onKeyUp.createDelegate(this);
    },
    onKeyUp: function(e){
        if (this.getValue() != "") {
            this.Store.removeAll();
            var i = 0;
            while (i < this.StorageArray.length) {
                var str=new RegExp("^"+this.getValue()+".*$","i");
                if (str.test(this.StorageArray[i].get(this.field))) {
                    this.Store.add(this.StorageArray[i]);
                }
                i++;
            }
        }
        else {
            this.Store.removeAll();
            for (i = 0; i < this.StorageArray.length; i++) {
                this.Store.insert(i, this.StorageArray[i]);
            }
        }
        this.fireEvent('SearchComplete', this.Store);
    },
    StorageChanged: function(store){
        this.Store = store;
        this.StorageArray = this.Store.getRange();
    }
});
Wtf.reg('KWLQuickSearch', Wtf.KWLQuickSearch);



Wtf.KWLTagSearch = function(config){
    Wtf.KWLTagSearch.superclass.constructor.call(this, config);
}
Wtf.extend(Wtf.KWLTagSearch, Wtf.form.TextField, {
    Store: null,
    StorageArray: null,
    limit: 15,
    initComponent: function(){
        Wtf.KWLTagSearch.superclass.initComponent.call(this);
        this.addEvents({
            'SearchComplete': true
        });
    },
    timer:new Wtf.util.DelayedTask(this.callKeyUp),
    setPage: function(val) {
        this.limit = val;
    },
    onRender: function(ct, position){
        Wtf.KWLTagSearch.superclass.onRender.call(this, ct, position);
        this.el.dom.onkeyup = this.onKeyUp.createDelegate(this);
    },
    onKeyUp: function(e){
        if(this.Store) {
            if (this.getValue() != "") {
                this.timer.cancel();
                this.timer.delay(1000,this.callKeyUp,this);
            }
            else {
                if(this.isCustomReport != undefined && this.isCustomReport) {//CRM custom report - Component - openReportGrid.js
                    if(this.fromDate != undefined && this.toDate != undefined) {
                       var frm = this.fromDate.getValue();
                       var to = this.toDate.getValue();
                       var cd = null;
                       if(frm!=""||to!=""){
                            if(checkDates(this.fromDate,this.toDate)) {
                                frm =(frm).getTime();
                                to =(to).getTime() + 86400000;
                                cd=1;
                            }
                        }
                        this.Store.reload({
                            params: {
                                start: 0,
                                limit: this.limit,
                                cd:cd,
                                frm: frm!="" ? frm : null,
                                to:to!="" ? to : null,
                                filterCombo:this.filterCombo.getValue(),
                                ss: "",
                                tagSearch:this.tagSearch
                            }
                        });
                    } else {
                        this.Store.reload({
                            params: {
                                start: 0,
                                limit: this.limit,
                                ss: "",
                                tagSearch:this.tagSearch
                            }
                        });
                    }
                } else {
                    if(this.fromDate != undefined && this.toDate != undefined && this.isValidDate(this.fromDate.getValue(),this.toDate.getValue())) {
                        var fromdate = this.fromDate.getValue().getTime();
                        var todate  = this.toDate.getValue().add(Date.DAY,1).add(Date.SECOND,-1).getTime();
                        this.Store.reload({
                            params: {
                                start: 0,
                                limit: this.limit,
                                year:this.year != undefined ? this.year.getValue() : null,
                                cd: fromdate != "" &&  todate != "" ? 1 : null,
                                frm:fromdate != "" && todate != "" ? fromdate : null,
                                to:fromdate != "" && todate != "" ? todate : null,
                                ss: "",
                                tagSearch:this.tagSearch,
                                month:this.month != undefined ? this.month.getValue() : null,
                                quickSearchFields:""
                            }
                        });
                    } else if(this.month != undefined) {
                        this.Store.reload({
                            params: {
                                start: 0,
                                limit: this.limit,
                                year:this.year != undefined ? this.year.getValue() : null,
                                month:this.month != undefined ? this.month.getValue() : null,
                                ss: "",
                                tagSearch:this.tagSearch,
                                quickSearchFields:""
                            }
                        });
                    } else {
                        this.Store.reload({
                            params: {
                                start: 0,
                                limit: this.limit,
                                year:this.year != undefined ? this.year.getValue() : null,
                                month:this.month != undefined ? this.month.getValue() : null,
                                ss: "",
                                tagSearch:this.tagSearch,
                                quickSearchFields:""
                            }
                        });
                    }
                }
                this.fireEvent('SearchComplete', this.Store);
            }
        }
    },
    isValidDate : function(fromDate,toDate){
         if(fromDate=="" || toDate=="" || fromDate == "Invalid Date" || toDate == "Invalid Date" ) {
            return false;
        }else if(fromDate > toDate ) {
            return false;
        }
        return true;
    },
    callKeyUp: function() {
        var val = WtfGlobal.replaceAll(this.getValue(), "\\\\" , "\\\\");
        if(this.isCustomReport != undefined && this.isCustomReport) {//CRM custom report - Component - openReportGrid.js
            if(this.fromDate != undefined && this.toDate != undefined) {
               var frm = this.fromDate.getValue();//verify first
               var to = this.toDate.getValue();
               var cd = null;
               if(frm!=""||to!=""){
                    if(checkDates(this.fromDate,this.toDate)) {
                        frm =(frm).getTime();
                        to =(to).getTime() + 86400000;
                        cd=1;
                    }
                }
                this.Store.reload({
                    params: {
                        start: 0,
                        limit: this.limit,
                        ss: val,
                        tagSearch:this.tagSearch,
                        cd:cd,
                        frm: frm!="" ? frm : null,
                        to:to!="" ? to : null,
                        filterCombo:this.filterCombo.getValue(),
                        searchJson: (this.parentGridObj && (this.parentGridObj.searchJson != undefined
                            || this.parentGridObj.searchJson != "")?this.parentGridObj.searchJson:null)                        
                    }
                });
            } else {
                this.Store.reload({
                    params: {
                        start: 0,
                        limit: this.limit,
                        ss: val,
                        tagSearch:this.tagSearch,
                        searchJson: (this.parentGridObj && (this.parentGridObj.searchJson != undefined
                            || this.parentGridObj.searchJson != "")?this.parentGridObj.searchJson:null)
                    }
                });
            }
        } else {
            if(this.fromDate != undefined && this.toDate != undefined && this.isValidDate(this.fromDate.getValue(),this.toDate.getValue())) {
                var fromdate =this.fromDate.getValue().getTime();
                var todate  = this.toDate.getValue().add(Date.DAY,1).add(Date.SECOND,-1).getTime();
                this.Store.reload({
                    params: {
                        start: 0,
                        limit: this.limit,
                        ss: val,
                        tagSearch:this.tagSearch,
                        cd:fromdate != "" && todate != "" ? 1 : null,
                        frm:fromdate != "" && todate != "" ? fromdate : null,
                        to:fromdate != "" && todate != "" ? todate : null,
                        year:this.year != undefined ? this.year.getValue() : null,
                        month:this.month != undefined ? this.month.getValue() : null,
                        quickSearchFields:"",
                        searchJson: (this.parentGridObj && (this.parentGridObj.searchJson != undefined
                            || this.parentGridObj.searchJson != "")?this.parentGridObj.searchJson:null)
                    }
                });
            } else if(this.month != undefined){
                this.Store.reload({
                    params: {
                        start: 0,
                        limit: this.limit,
                        year:this.year != undefined ? this.year.getValue() : null,
                        month:this.month != undefined ? this.month.getValue() : null,
                        ss: val,
                        tagSearch:this.tagSearch,
                        quickSearchFields:"",
                        searchJson: (this.parentGridObj && (this.parentGridObj.searchJson != undefined
                            || this.parentGridObj.searchJson != "")?this.parentGridObj.searchJson:null)
                    }
                });
            } else {
                this.Store.reload({
                    params: {
                        start: 0,
                        limit: this.limit,
                        year:this.year != undefined ? this.year.getValue() : null,
                        month:this.month != undefined ? this.month.getValue() : null,
                        ss: val,
                        tagSearch:this.tagSearch,
                        quickSearchFields:"",
                        searchJson: (this.parentGridObj && (this.parentGridObj.searchJson != undefined
                            || this.parentGridObj.searchJson != "")?this.parentGridObj.searchJson:null)
                    }
                });
            }
        }
        this.fireEvent('SearchComplete', this.Store);
    }, 
    StorageChanged: function(store){
        this.Store = store;
        this.StorageArray = this.Store.getRange();
    }
});

Wtf.reg('KWLTagSearch', Wtf.KWLTagSearch);

Wtf.KWLQuickSearchUseFilter = function(config){
    Wtf.KWLQuickSearchUseFilter.superclass.constructor.call(this, config);
}
Wtf.extend(Wtf.KWLQuickSearchUseFilter, Wtf.form.TextField, {
    Store: null,
    initComponent: function(){
        Wtf.KWLQuickSearchUseFilter.superclass.initComponent.call(this);
    },
    onRender: function(ct, position){
        Wtf.KWLQuickSearchUseFilter.superclass.onRender.call(this, ct, position);
        this.el.dom.onkeyup = this.onKeyUp.createDelegate(this);
    },
    onKeyUp: function(e){

        this.Store.filter(this.field,this.getValue())
    },
    StorageChanged: function(store){
        this.Store = store;
        this.StorageArray = this.Store.getRange();
    }
});
