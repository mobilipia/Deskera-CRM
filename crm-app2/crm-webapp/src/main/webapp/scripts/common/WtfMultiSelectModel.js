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
Wtf.grid.MultiCellSelectionModel = function(config){
    Wtf.apply(this, config);
    this.selections = new Wtf.util.MixedCollection(false, this.cellKey);
    this.rowSelections=new Wtf.util.MixedCollection(false,function(o){
    	return o.id;
    });
    this.colSelections=new Wtf.util.MixedCollection(false);

    this.last = false;
    this.lastActive = false;

    this.addEvents(       
	    "selectionchange",
	    "beforecellselect",
	    "beforecolselect",
	    "beforerowselect",
	    "cellselect",
	    "colselect",
	    "rowselect",
	    "celldeselect",
	    "coldeselect",
	    "rowdeselect"
    );

    Wtf.grid.MultiCellSelectionModel.superclass.constructor.call(this);
};

Wtf.extend(Wtf.grid.MultiCellSelectionModel, Wtf.grid.AbstractSelectionModel, {  
    header: '<div class="x-grid3-hd-checker">&#160;</div>',    
    width: 20,
    sortable: false,
    fixed:true,
    dataIndex: '',
    id: 'checker',
	singleSelect : false,
    renderer : function(v, p, record){
	    return '<div class="x-grid3-row-checker">&#160;</div>';
	},
    cellKey: function(cell) {
        return String.format("{0}::{1}", cell[0], cell[1]);
    },
    
    syncView:function(){
    	var view = this.grid.getView();
    	var ds = this.grid.store;
    	this.rowSelections.each(function(r){
    		view.onRowSelect(ds.indexOfId(r.id));
    	},this);
    	this.selections.each(function(cell){
    		view.onCellSelect(cell[0], cell[1]);
    	},this);
    },

    handleMouseDown : function(g, rowIndex, columnIndex, e){
        if(e.button !== 0 || this.isLocked()){
            return;
        };
        var view = this.grid.getView();
        this.onMouseDown(e, e.target);
        if(e.shiftKey && this.last !== false){
            var last = this.last;
            this.selectRange(last, [rowIndex, columnIndex], e.ctrlKey);
            this.last = last; // reset the last
            this.lastActive = [rowIndex, columnIndex];
            //view.focusCell(rowIndex, columnIndex);
        }else{
            var isSelected = this.isSelected([rowIndex, columnIndex]);
            if(e.ctrlKey && isSelected){
                this.deselectCell([rowIndex, columnIndex]);
            }else if(!isSelected || this.getCount() > 1){
                this.selectCell([rowIndex, columnIndex], e.ctrlKey || e.shiftKey);
                //view.focusCell(rowIndex, columnIndex);
            }
        }
    },
    
    initEvents : function(){

        this.grid.on('headerclick', this.onHdMouseDown, this);
	    if(!this.grid.enableDragDrop && !this.grid.enableDrag){
	        this.grid.on("cellmousedown", this.handleMouseDown, this);
	    }else{ 
	        this.grid.on("cellclick", function(grid, rowIndex, columnIndex, e) {
	            if(e.button === 0 && !e.shiftKey && !e.ctrlKey) {
	                this.selectCell([rowIndex, columnIndex], false);
	                //grid.view.focusCell(rowIndex, columnIndex);
	            }
	        }, this);
	    }
	    this.grid.getGridEl().on(Wtf.isIE ? "keydown" : "keypress", this.handleKeyDown, this);
	    var view = this.grid.view;
	    view.on("refresh", this.onRefresh, this);
	    view.on("rowremoved", this.onRowRemove, this);
	},
	
    onMouseDown : function(e, t){
        if(t.className == 'x-grid3-row-checker'){
            e.stopEvent();
            var row = e.getTarget('.x-grid3-row');
            if(row){
                var index = row.rowIndex;
                if(this.isSelected([index])){
                    this.deselectRow(index);
                }else{
                    this.selectRow(index, true);
                }
            }
        }
    },

    
    onHdMouseDown : function(g,index,e){
    	t=e.target;
        if(t.className == 'x-grid3-hd-checker'){
            e.stopEvent();
            var hd = Wtf.fly(t.parentNode);
            var isChecked = hd.hasClass('x-grid3-hd-checker-on');
            if(isChecked){
                hd.removeClass('x-grid3-hd-checker-on');
                this.deselectAll();
            }else{
                hd.addClass('x-grid3-hd-checker-on');
                this.selectAll();
            }
        }else{
            if(this.isSelected([undefined,index])){
                this.deselectColumn(index);
            }else{
                this.selectColumn(index);
            }
        }
    },
    
    selectAll:function(){
        if(this.locked) return;
        var count = this.grid.store.getCount();
        for(var i=0;i<count;i++)
        	this.selectRow(i, true);
    },
    deselectAll:function(){
        if(this.locked) return;
        var count = this.grid.store.getCount();
        for(var i=0;i<count;i++)
        	this.deselectRow(i);
    },
    selectRows : function(rows, keepExisting){
        if(!keepExisting){
            this.clearSelections();
        }
        for(var i = 0, len = rows.length; i < len; i++){
            this.selectRow(rows[i], true);
        }
    },
	selectRow:function(rowIndex, keepExisting, preventViewNotify){
    	if(this.locked || (rowIndex < 0 || rowIndex >= this.grid.store.getCount())) return;
    	var r = this.grid.store.getAt(rowIndex);
    	if(r && this.fireEvent("beforerowselect", this, rowIndex, keepExisting) !== false){
        	this.selectRange([rowIndex,0], [rowIndex, this.grid.colModel.getColumnCount()-1], keepExisting, preventViewNotify);
        	this.rowSelections.add(r);
        	if(!preventViewNotify){
        		this.grid.view.onRowSelect(rowIndex);
        	}
        	this.fireEvent("rowselect", this, rowIndex);
        	this.fireEvent("selectionchange", this, this.getSelections(),this.getSelectedRows(), this.getSelectedColumns());
    	}
	},
	
	deselectRow:function(rowIndex, preventViewNotify){
		if(this.locked) return;
		var r = this.grid.store.getAt(rowIndex);
		if(r){
	    	this.deselectRange([rowIndex,0], [rowIndex, this.grid.colModel.getColumnCount()-1],preventViewNotify);
	    	this.rowSelections.remove(r);
	    	if(!preventViewNotify){
	    		this.grid.view.onRowDeselect(rowIndex);
	    	}
	    	this.fireEvent("rowdeselect", this, rowIndex);
	    	this.fireEvent("selectionchange", this, this.getSelections(),this.getSelectedRows(), this.getSelectedColumns());
		}
	},
	
    selectColumn:function(i){
        if(this.grid.view.headersDisabled){
            return;
        }
        if (this.fireEvent("beforecolselect", this, i) !== false) {
        	this.grid.stopEditing();
	    	this.selectRange([0,i],[this.grid.store.getCount()-1,i]);
	    	this.colSelections.add(i,i);
	        this.fireEvent("colselect", this, i);
	        this.fireEvent("selectionchange", this, this.getSelections(),this.getSelectedRows(), this.getSelectedColumns());
        }
    },
    
    deselectColumn:function(i){
        if(this.grid.view.headersDisabled){
            return;
        }

        this.grid.stopEditing();
    	this.deselectRange([0,i],[this.grid.store.getCount()-1,i]);
    	this.colSelections.remove(i,i);
        this.fireEvent("coldeselect", this, i);
        this.fireEvent("selectionchange", this, this.getSelections(),this.getSelectedRows(), this.getSelectedColumns());
    },
    
	onRefresh:function(v){
    	v.updateHeaders();
		this.clearSelections(true);
	},
	
	onRowRemove:function(v, index, r){
    	for(var x = 0;x<cc;x++)
    		this.selections.remove(this.cellKey([index,x]));
        if(this.rowSelections.remove(r) !== false){
        	var cc=this.grid.colModel.getColumnCount();
            this.fireEvent('selectionchange', this, this.getSelections(),this.getSelectedRows(), this.getSelectedColumns());
        }
	},
	
    isSelected : function(index){
		if(typeof index[0] == "number"){
			if(typeof index[1] == "number"){
				return this.selections.containsKey(this.cellKey(index[0],index[1]));
			}else{
		        var r = typeof index[0] == "number" ? this.grid.store.getAt(index[0]) : index[0];
		        return (r && this.rowSelections.key(r.id) ? true : false);
			}
		}else if(typeof index[1] == "number") {
			return this.colSelections.containsKey(index[1]);
		}
		
        return false;
    },
    
    getCount : function(){
        return this.rowSelections.length;
    },
    
    getSelectedCells : function() {
    	return [].concat(this.selections.items);
    },
    
    getSelections : function() {
    	return this.getSelectedRows();
    },

    getSelected : function() {
    	return this.rowSelections.itemAt(0);
    },

    getSelectedRows : function() {
    	return [].concat(this.rowSelections.items);
    },
    getSelectedColumns : function() {
    	return [].concat(this.colSelections.items);
    },
    clearSelections : function(fast){
        if(this.locked) return;
        if(fast !== true){
            var ds = this.grid.store;
            var s = this.selections;
            s.each(function(cell){
                this._deselectCell(cell);
            }, this);
            s.clear();
            var ds = this.grid.store;
            var view = this.grid.getView();
            this.rowSelections.each(function(r){
           		view.onRowDeselect(ds.indexOfId(r.id));
            }, this);
            this.rowSelections.clear();
            this.colSelections.clear();
        }else{
            this.selections.clear();
            this.rowSelections.clear();
            this.colSelections.clear();
        }
        this.last = false;
    },
    selectCell : function(index, keepExisting, preventViewNotify){
    	if(this._selectCell(index, keepExisting, preventViewNotify)){
    		this.fireEvent("selectionchange", this, this.getSelections(),this.getSelectedRows(), this.getSelectedColumns());
    	}
    },
    deselectCell : function(index, preventViewNotify){
    	if(this._deselectCell(index, preventViewNotify)){
    		this.fireEvent("selectionchange", this, this.getSelections(),this.getSelectedRows(), this.getSelectedColumns());
    	}
    },

    _selectCell : function(index, keepExisting, preventViewNotify){
        if (this.locked||index[0] < 0 || index[0] >= this.grid.store.getCount() || index[1] < 0 || index[1] >= this.grid.getColumnModel().getColumnCount()) return;
        if (this.isSelected(index)||this.grid.colModel.isUnselectable(index[1])) return;

        if (this.fireEvent("beforecellselect", this, index, keepExisting) !== false) {
            if (!keepExisting || this.singleSelect) {
                this.clearSelections();
            }
            this.selections.add(index);
            this.last = this.lastActive = index;
            if(!preventViewNotify) {
                this.grid.getView().onCellSelect(index[0], index[1]);
            }
            this.fireEvent("cellselect", this, index);
            return true;
        }
    },

    _deselectCell : function(index, preventViewNotify){
        if (this.locked) return;
        if (this.last[0] == index[0] && this.last[1] == index[1]) {
            this.last = false;
        }
        if (this.lastActive[0] == index[0] && this.lastActive[1] == index[1]) {
            this.lastActive = false;
        }

        cell=this.selections.get(this.cellKey(index));
        
        if (cell) {
            this.selections.remove(cell);
            if (!preventViewNotify) {
                this.grid.getView().onCellDeselect(index[0], index[1]);
            }
            this.fireEvent("celldeselect", this, index);
            return true;
        }
    },

    selectRange : function(startCell, endCell, keepExisting, preventViewNotify){
        if(this.locked) return;
        if(!keepExisting){
            this.clearSelections();
        }
        var row, col, colCount;
        var startRow = startCell[0];
        var startCol = startCell[1];
        var endRow = endCell[0];
        var endCol = endCell[1];
        if (endRow < startRow) {
            row = endRow;
            endRow = startRow;
            startRow = row;
        }
        if (endCol < startCol) {
            col = endCol;
            endCol = startCol;
            startCol = col;
        }
        var selChanged = false;
        for (row = startRow; row <= endRow; row++) {
            for (col = startCol; col <= endCol; col++) {
                selChanged = this._selectCell([row, col], true, preventViewNotify);
            }
        }
        return selChanged;
    },

    deselectRange : function(startCell, endCell, preventViewNotify) {
        if(this.locked) return;
        var row, col, colCount;
        var startRow = startCell[0];
        var startCol = startCell[1];
        var endRow = endCell[0];
        var endCol = endCell[1];
        if (endRow < startRow) {
            row = endRow;
            endRow = startRow;
            startRow = row;
        }
        if (endCol < startCol) {
            col = endCol;
            endCol = startCol;
            startCol = col;
        }
        var selChanged = false;
        for (row = startRow; row <= endRow; row++) {
            for (col = startCol; col <= endCol; col++) {
                selChanged = this._deselectCell([row, col], preventViewNotify);
            }
        }
        return selChanged;
    },
    
    isSelectable : function(rowIndex, colIndex, cm){
        return !cm.isHidden(colIndex)&&!cm.isUnselectable(colIndex);
    },
    
    handleKeyDown : function(e){
    	var g = this.grid, s = this.lastActive;
        if(!e.isNavKeyPress()){
        	if(e.getCharCode()==e.F2&&s){
        		g.startEditing(s[0],s[1]);
        	}
            return;
        }

        if(!s){
            e.stopEvent();
            var cell = g.walkCells(0, 0, 1, this.isSelectable,  this);
            if(cell){
                this._selectCell(cell);
                g.getView().focusCell(cell[0],cell[1]);
            }
            return;
        }
        var sm = this;
        var walk = function(row, col, step){
            return g.walkCells(row, col, step, sm.isSelectable,  sm);
        };
        var k = e.getKey(), r = s[0], c = s[1];
        var newCell;

        switch(k){
             case e.TAB:
                 if(e.shiftKey){
                     newCell = walk(r, c-1, -1);
                 }else if(e.r==0){ // Sagar M - case handle for first empty row
                    newCell = walk(e.r+1, e.c+1, 1);
                 }else{
                     newCell = walk(r, c+1, 1);
                 }
             break;
             case e.DOWN:
                 newCell = walk(r+1, c, 1);
                 if(e.shiftKey&&newCell&&newCell[1]!=c)return;
             break;
             case e.UP:
                 newCell = walk(r-1, c, -1);
                 if(e.shiftKey&&newCell&&newCell[1]!=c)return;
             break;
             case e.RIGHT:
                 newCell = walk(r, c+1, 1);
                 if(e.shiftKey&&newCell&&newCell[0]!=r)return;
             break;
             case e.LEFT:
                 newCell = walk(r, c-1, -1);
                 if(e.shiftKey&&newCell&&newCell[0]!=r)return;
             break;
             case e.ENTER:
                 if(g.isEditor && !g.editing){
                    g.startEditing(r, c);
                    e.stopEvent();
                    return;
                }
             break;
        };
        if(newCell){
            if (k==e.TAB||!e.shiftKey) {
	            this.selectCell(newCell);
	        } else {
	            var last = this.last;
	            this.selectRange(this.last, newCell);
	            this.last = last;
	        }
            g.getView().focusCell(newCell[0], newCell[1]);
            this.lastActive = newCell;
            e.stopEvent();
        }
    },

    acceptsNav : function(row, col, cm){
        return this.isSelectable(row, col, cm) && cm.isCellEditable(col, row);
    },

    onEditorKey : function(field, e){
        var k = e.getKey(), newCell, g = this.grid, ed = g.activeEditor;
        var shift = e.shiftKey;
        if (k == e.TAB || k == e.ENTER) {
            e.stopEvent();
            g.stopEditing();
            this.clearSelections();
            
            var row, col, delta = shift ? -1 : 1;
            if (k == e.TAB) {
                if(ed.row==0) {  // Sagar M - case handle for first empty row
                    newCell = g.walkCells(ed.row+1, ed.col+1, 1, this.acceptsNav, this);
                } else {
                    newCell = g.walkCells(ed.row, ed.col+delta, delta, this.acceptsNav, this);
                }
            } else {
                newCell = g.walkCells(ed.row+delta, ed.col, delta, this.acceptsNav, this);
            }
            
            if (newCell) {
                g.startEditing(newCell[0], newCell[1]);
            } else {
                this._selectCell([ed.row, ed.col]);
                g.getView().focusCell(ed.row, ed.col);
            }
        } else if (k == e.ESC) {
            g.stopEditing(true);
            this._selectCell([ed.row, ed.col]);
            g.getView().focusCell(ed.row, ed.col);
        }
    }
});

