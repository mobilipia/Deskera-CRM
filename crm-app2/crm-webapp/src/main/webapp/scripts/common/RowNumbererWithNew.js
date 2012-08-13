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
Wtf.RowNumbererWithNew = function(config) {
    Wtf.RowNumbererWithNew.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.RowNumbererWithNew, Wtf.grid.RowNumberer, {
		header: "",
        width: 23,
		sortable: false,
		fixed:false,
		dataIndex: '',
		id: 'numberer',
		rowspan: 1,
		newRowName:'N',
    initComponent: function(config) {
        Wtf.RowNumbererWithNew.superclass.initComponent.call(this,config);
    },
    
    renderer : function(v, p, record, rowIndex, colIndex, store){
    	if(this.rowspan){
            p.cellAttr = 'rowspan="'+this.rowspan+'"';
        }
        if(rowIndex==0){
        	return this.newRowName;
        }
        
        var x = (!store.lastOptions||!store.lastOptions.params||isNaN(store.lastOptions.params.start)||!this.allowIncreament?0:store.lastOptions.params.start);

        return rowIndex+x;
    },
    
    init:function(grid){
	    this.view = grid.getView();
	    this.view.processRows = this.view.processRows.createSequence(this.refresh, this);
    },
    
    refresh:function(rowIndex){
    	store=this.view.grid.store;
        var x = (!store.lastOptions||!store.lastOptions.params||isNaN(store.lastOptions.params.start)||!this.allowIncreament?0:store.lastOptions.params.start);
        var length = store.getCount();
        for (var i = rowIndex+1; i < length; i++) {
        	this.view.getCell(i, 0).firstChild.innerHTML = i+x;
        }
    }
});
