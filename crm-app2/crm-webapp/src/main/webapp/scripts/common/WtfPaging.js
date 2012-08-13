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
Wtf.PagingSearchToolbar = Wtf.extend(Wtf.PagingToolbar, {
    searchField: null,
    paramNames : {start: 'start', limit: 'limit', ss: 'ss', cd:'cd', frm:'frm', to:'to', year:'year', month:'month' , searchJson:'searchJson', tagSearch:'tagSearch'},
    onRender: function(config) {
        Wtf.PagingSearchToolbar.superclass.onRender.call(this, config);
// Kuldeep Singh : When we delete all records from last page, paging is not working properly coz total count is now decreased after deletion but start is remains same as it is before deletion.
// Solution : adjust start when we delete all records from last page.

    this.store.on('load',function(t,r,o){
            var reader = this.store.reader;
            var tp = reader.meta.totalProperty;
            var tc=reader.jsonData[tp];
            if(o.params!=undefined){
                var l=o.params.limit
                var s=o.params.start
                if( tc != null && tc > 0 ){ // If total count is not null and greater than zero

                    if(s>0 && s>=tc){  // start is greater than zero and greater than total count

                        this.store.load({
                            params: {
                                start:(s-l<0)?0:(s-l),
                                limit:l
                            }
                        });

                    }
                }
            }
            
            
        },this);
    },
    doLoad: function(start) {
        var o = {}, pn = this.paramNames;
        o[pn.start] = start;
        o[pn.limit] = this.pageSize;
        if(this.searchField) {
            o[pn.ss] = this.searchField.getValue();
            if((o[pn.ss] !="") && (this.searchField.tagSearch != undefined)) {
                o[pn.tagSearch] = this.searchField.tagSearch
        }
        } else {
            o[pn.ss] = "";
        }
        if(this.parentGridObj) {
            if (this.parentGridObj.searchJson) {
                o[pn.searchJson] = this.parentGridObj.searchJson;
            }
        } else {
            o[pn.searchJson] = "";
        }
        if(this.fromDate != undefined && this.toDate != undefined ) {
            var fromdate = this.fromDate.getValue();
            var todate = this.toDate.getValue();
            if(fromdate != '' &&  todate != '') {
                fromdate = Wtf.formatDate(this.fromDate.getValue(),0);
                todate = todate.add(Date.DAY,1).add(Date.SECOND,-1);
                todate = Wtf.formatDate(todate,0);
                o[pn.cd] = 1;
                o[pn.frm] = fromdate.toString();
                o[pn.to] = todate.toString();
            } else {
                o[pn.cd] = null;
                o[pn.frm] = null;
                o[pn.to] = null;
            }
        }
        if(this.yearCombo != undefined) {
            o[pn.year] = this.yearCombo.value
        }        
        if(this.monthCombo != undefined ) {
            o[pn.month] = this.monthCombo.value
        }
        this.store.load({params:o});
        }
}); 
