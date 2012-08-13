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


// IE Bug Resolved - Cursor moves before first character in textfield cell editor

Wtf.override(Wtf.Element,{
    focus: function(defer, /* private */dom) {
        var me = this,dom = dom || me.dom;
        try {
            if (Number(defer)) {
                me.focus.defer(defer, null, [null, dom]);
            }else {
                dom.focus();
            }
        } catch (e) { }
        if (document.selection) {
            var range = document.selection.createRange();
            if (dom && dom.value) {
                range.move('character', dom.value.length);
                range.select();
            }
        }
        return me;
    }
});

Wtf.AlignPalette = function(config){
    Wtf.AlignPalette.superclass.constructor.call(this, config);
    this.addEvents(
        'select'
        );
    if(this.handler){
        this.on("select", this.handler, this.scope, true);
    }
};
Wtf.extend(Wtf.AlignPalette, Wtf.Component, {
    itemCls : "x-align-palette",
    value : null,
    clickEvent:'click',
    ctype: "Wtf.AlignPalette",
    aligns : [
    "left", "center", "right"//, "top", "mid", "bottom"
    ],

    onRender : function(container, position){
        var t = new Wtf.XTemplate(
            '<tpl for="."><a href="#" class="align-{.}" hidefocus="on">',
            '<em><span class="sheetBar align-palette-{.}-img" unselectable="on">&#160;</span></em></a></tpl>'
            );
        var el = document.createElement("div");
        el.className = this.itemCls;
        t.overwrite(el, this.aligns);
        container.dom.insertBefore(el, position);
        this.el = Wtf.get(el);
        this.el.on(this.clickEvent, this.handleClick,  this, {delegate: "a"});
        if(this.clickEvent != 'click'){
            this.el.on('click', Wtf.emptyFn,  this, {delegate: "a", preventDefault:true});
        }
    },
    afterRender : function(){
        Wtf.AlignPalette.superclass.afterRender.call(this);
        if(this.value){
            var s = this.value;
            this.value = null;
            this.select(s);
        }
    },
    handleClick : function(e, t){
        e.preventDefault();
        if(!this.disabled){
            var c = t.className.match(/(?:^|\s)align-([^\s]+)(?:\s|$)/)[1];
            this.select(c);
        }
    },
    select : function(align){
        this.value = align;
        this.fireEvent("select", this, align);
    }
});

Wtf.menu.AlignItem = function(config){
    Wtf.menu.AlignItem.superclass.constructor.call(this, new Wtf.AlignPalette(config), config);
    this.palette = this.component;
    this.relayEvents(this.palette, ["select"]);
    if(this.selectHandler){
        this.on('select', this.selectHandler, this.scope);
    }
};
Wtf.extend(Wtf.menu.AlignItem, Wtf.menu.Adapter);
Wtf.menu.AlignMenu = function(config){
    Wtf.menu.AlignMenu.superclass.constructor.call(this, config);
    this.plain = true;
    var ci = new Wtf.menu.AlignItem(config);
    this.add(ci);
    this.palette = ci.palette;
    this.relayEvents(ci, ["select"]);
};
Wtf.extend(Wtf.menu.AlignMenu, Wtf.menu.Menu);
Wtf.override(Wtf.EventObjectImpl, {
    F2 : 113
});

Wtf.override(Wtf.form.ComboBox, {
    beforeBlur : function(){
        if(this.store)this.store.clearFilter();
    }
});

// add keydown event for safari and chrome
Wtf.override(Wtf.form.Field, {
    initEvents : function(){
        this.el.on(Wtf.isIE || Wtf.isSafari || Wtf.isChrome ? "keydown" : "keypress", this.fireKey,  this);
        this.el.on("focus", this.onFocus,  this);
        this.el.on("blur", this.onBlur,  this);
        this.originalValue = this.getValue();
//        var o = this.inEditor && Wtf.isWindows && Wtf.isGecko ? {buffer:10} : null;
//        this.el.on("blur", this.onBlur,  this, {buffer:10});

    }
});

Wtf.SpreadSheet = {};

Wtf.SpreadSheet.constructAuditStr = function(grid,rowIndex, colIndex, newvalue, oldvalue,record){
	var c = grid.colModel.config[colIndex];
    var last = grid.colModel.config.length-1;
	var r = c.renderer||Wtf.grid.ColumnModel.defaultRenderer;
	var s = Wtf.util.Format.htmlDecode;
	var t = WtfGlobal.HTMLStripper;
    var p={id:c.id,css:colIndex == 0 ? 'x-grid3-cell-first ' : (colIndex == last ? 'x-grid3-cell-last ' : ''),attr:"",cellAttr:""};

    var header = c.header;
    if(c.mandatory!=undefined){
        header = (c.mandatory==true?c.header.substring(0, c.header.lastIndexOf("*")-1):c.header);
    }
        
    if(oldvalue&&oldvalue.toString().trim()!=""){
    	return header+" '"+t(s(r(oldvalue,p,record,rowIndex,colIndex,grid.getStore())))+"' updated to '"+t(s(r(newvalue,p,record,rowIndex,colIndex,grid.getStore())))+"' for ";
    } else {
    	return header+" '"+t(s(r(newvalue,p,record,rowIndex,colIndex,grid.getStore())))+"' added for ";
    }	
};

Wtf.SpreadSheet.GridEditor = function(field, config){
    Wtf.SpreadSheet.GridEditor.superclass.constructor.call(this, field, config);
    field.monitorTab = Wtf.isSafari;
};
Wtf.SpreadSheet.GridEditor = Wtf.extend(Wtf.grid.GridEditor, {
    cancelEdit : function(remainVisible){
		var showAlert = this.revertInvalid !== false && !this.field.isValid() && this.field.regexText;
		Wtf.SpreadSheet.GridEditor.superclass.cancelEdit.call(this, remainVisible);
		if(showAlert)
            WtfComMsgBox(["Alert",this.field.regexText]);
	}
});

Wtf.SpreadSheet.ColumnModel = Wtf.extend(Wtf.grid.ColumnModel, {

    setConfig : function(config, initial){
        if(!initial){
            delete this.totalWidth;
            for(var i1 = 0, len1 = this.config.length; i1 < len1; i1++){
                var c1 = this.config[i1];
                if(c1.editor){
                    c1.editor.destroy();
                }
            }
        }
        this.config = config;
        this.lookup = {};

        for(var i = 0, len = config.length; i < len; i++){
            var c = config[i];
            if(typeof c.renderer == "string"){
                c.renderer = Wtf.util.Format[c.renderer];
            }
            if(typeof c.id == "undefined"){
                c.id = i;
            }
            if(c.sheetEditor){
                var myEditor = this.getEditor(c.sheetEditor);

                c.editor = new Wtf.SpreadSheet.GridEditor(myEditor);
                this.config[i].editor = c.editor;
                var editorXtype = c.sheetEditor.xtype;
                if(editorXtype == "combo"){
                    if(c.sheetEditor.searchStoreCombo) {
                        c.renderer = this.getComboNameRenderer(myEditor);
                    } else
                        c.renderer = this.getComboRenderer(myEditor);
                    this.config[i].renderer = c.renderer;
                }else if(editorXtype == "select" ){
                    c.renderer = this.getSelectComboRenderer(myEditor);
                    this.config[i].renderer = c.renderer;
                }else if(editorXtype == "datefield" ){
                	c.renderer=WtfGlobal.onlyDateRendererTZ;
                } else if(editorXtype =="checkbox"){
                    c.renderer  = function(v, p, record){
                        p.css += ' x-grid3-check-col-td';
                        return '<div class="x-grid3-check-col'+(v =="true" ?'-on':'')+' x-grid3-cc-'+this.id+'"> </div>';
                    }
                }
            } else if(c.editor && c.editor.isFormField){
                c.editor = new Wtf.SpreadSheet.GridEditor(c.editor); //
            }

            if(typeof c.headerName == "undefined"){
                c.headerName = this.setConfigHeaderName(i);
                this.config[i].headerName = c.headerName;
            }
            if(typeof c.width == "undefined" && i>1){
                c.width = 180;
                this.config[i].width = c.width;
            }

            this.lookup[c.id] = c;
        }
        if(!initial){
            this.fireEvent('configchange', this);
        }
    },
    validateSelection : function(combo,record,index){
        return record.get('hasAccess' );
    },
    getEditor : function(eObj){
        var editor = null;
        if(eObj.xtype == "combo") {
            if(eObj.useDefault==true){
                eObj.selectOnFocus = true;
                eObj.triggerAction = 'all';
                eObj.mode = 'local';
                eObj.valueField = 'id';
                eObj.displayField = 'name';
//                eObj.typeAhead = true;
//                eObj.tpl= Wtf.comboTemplate;
                }
            if(eObj.searchStoreCombo && eObj.searchStoreCombo == true) {
                eObj.mode = 'remote';
                if(!eObj.loadOnSelect) { // loadOnSelect flag for master store only and handled by storemanager
                    eObj.minChars = 2;
                    eObj.triggerClass ='dttriggerForTeamLead';
                }
                eObj.spreadSheetCombo = true;
            }
            editor = new Wtf.form.ComboBox(eObj);
            editor.on('beforeselect',this.validateSelection,this);
        }else if(eObj.xtype == "select") {
            if(eObj.useDefault==true){
                eObj.selectOnFocus = true;
                eObj.forceSelection = true;
                eObj.multiSelect = true;
                eObj.triggerAction = 'all';
                eObj.mode = 'local';
                eObj.valueField = 'id';
                eObj.displayField = 'name';
                eObj.typeAhead = true;
                eObj.tpl= Wtf.comboTemplate;
            }
            eObj.spreadSheetCombo = true;
            editor = new Wtf.common.Select(eObj);
            editor.on('beforeselect',this.validateSelection,this);
        } else if(eObj.xtype == "textfield") {
            editor = new Wtf.ux.TextField(eObj);
        } else if(eObj.xtype == "numberfield") {
            editor = new Wtf.form.NumberField(eObj);
        } else if(eObj.xtype == "datefield") {
            eObj.readOnly=true;
            eObj.offset=Wtf.pref.tzoffset;
            editor = new Wtf.form.DateField(eObj);
        } else if(eObj.xtype == "timefield") {
            if(eObj.useDefault==true) {
//                eObj.minValue = new Date(new Date().format("M d, Y")+WtfGlobal.setDefaultMinValueTimefield());
//                eObj.maxValue = new Date(new Date().add(Date.DAY, 1).format("M d, Y")+WtfGlobal.setDefaultMaxValueTimefield());
                eObj.value = WtfGlobal.setDefaultValueTimefield();
                eObj.format=WtfGlobal.getLoginUserTimeFormat();
            }
            editor = new Wtf.form.TimeField(eObj);
        } else if(eObj.xtype == "textarea") {
            editor = new Wtf.form.TextArea(eObj);
        }else if(eObj.xtype == "checkbox"){
                   editor = new Wtf.form.Checkbox(eObj);
        }

        return editor;
    },

    getComboRenderer : function(combo){
        return function(value) {
            var idx = combo.store.find(combo.valueField, value);
            if(idx == -1)
                return "";
            var rec = combo.store.getAt(idx);
            return rec.get(combo.displayField);
        }
    },
    getComboNameRenderer : function(combo){
        return function(value,metadata,record,row,col,store) {
            var idx = combo.store.find(combo.valueField, value);
            var fieldIndex = combo.comboFieldDataIndex;
            if(idx == -1) {
                if(record.data[combo.comboFieldDataIndex] && record.data[fieldIndex].length>0) {
                    return record.data[fieldIndex];
                }
                else
                    return "";
            }
            var rec = combo.store.getAt(idx);
            var displayField = rec.get(combo.displayField);
            record.data[fieldIndex+"id"] = value;
            record.data[fieldIndex] = displayField;
            return displayField;
        }
    },
    getSelectComboRenderer : function(combo){
        return function(value) {
            var idx;
            var rec;
            var valStr="";
            if (value != undefined && value != "") {
                var valArray = value.split(",");
                for (var i=0;i < valArray.length;i++ ){
                    idx = combo.store.find(combo.valueField, valArray[i]);
                    if(idx != -1){
                        rec = combo.store.getAt(idx);
                        valStr+=rec.get(combo.displayField)+", ";
                    }
                }
                if(valStr != ""){
                    valStr=valStr.substring(0, valStr.length -2);
                    valStr="<div wtf:qtip=\""+valStr+"\">"+Wtf.util.Format.ellipsis(valStr,27)+"</div>";
                }

            }
            return valStr;
        }
    },

    getColumnHeaderName : function(col){
        return this.config[col].headerName;
    },

    getColumnXtype : function(col){
        return this.config[col].xtype;
    },

    getColumnStore : function(col){
        return this.config[col].sheetEditor.store;
    },

    setConfigHeaderName : function(col){
        var headerConf = this.config[col].header;
        var headerName = WtfGlobal.HTMLStripper(headerConf);
        var indx=headerName.indexOf('(');
        if(indx!=-1) {
            indx = headerName.indexOf("&#");
            if(indx!=-1)
                headerName = headerName.substring(0,headerName.indexOf('('));
        }
        headerName = headerName.replace("*","");
        return headerName;
    }
});

Wtf.SpreadSheet.SelectionModel = function(config){
    Wtf.apply(this, config);
    this.selections = new Wtf.util.MixedCollection(false, function(o){
        return o.id;
    });

    this.navigationType = 0; // 1 Edit, 2 Motion
    this.last = false;
    this.lastActive = false;
    this.selection = null;

    this.addEvents(
        "beforecellselect",
        "cellselect",
        "columnCellsSelect",
        "columnCellsDeSelect",
        "celldeselect",
        "selectionchange",
        "beforerowselect",
        "rowselect",
        "rowdeselect"
        );

    Wtf.SpreadSheet.SelectionModel.superclass.constructor.call(this);
};

Wtf.extend(Wtf.SpreadSheet.SelectionModel, Wtf.grid.CheckboxSelectionModel,  {
    selType : "None",

    initEvents : function(){
        this.selType = "None";

        /* Check Box */
        this.grid.on('render', function(){
            var view = this.grid.getView();
            view.mainBody.on('mousedown', this.onMouseDown, this);
            Wtf.fly(view.innerHd).on('mousedown', this.onHdMouseDown, this);

        }, this);

        /* Row  */
        if(!this.grid.enableDragDrop && !this.grid.enableDrag){
            this.grid.on("rowmousedown", this.handleRowMouseDown, this);
        }else{
            this.grid.on("rowclick", function(grid, rowIndex, e) {
                if(e.button === 0 && !e.shiftKey && !e.ctrlKey) {
                    this.selectRow(rowIndex, false);
                    grid.view.focusRow(rowIndex);
                }
            }, this);
        }

        this.rowNav = new Wtf.KeyNav(this.grid.getGridEl(), {
            "up" : function(e){
                if(!e.shiftKey){
                    this.selectPrevious(e.shiftKey);
                }else if(this.last !== false && this.lastActive !== false){
                    var last = this.last;
                    this.selectRange(this.last,  this.lastActive-1);
                    this.grid.getView().focusRow(this.lastActive);
                    if(last !== false){
                        this.last = last;
                    }
                }else{
                    this.selectFirstRow();
                }
            },
            "down" : function(e){
                if(!e.shiftKey){
                    this.selectNext(e.shiftKey);
                }else if(this.last !== false && this.lastActive !== false){
                    var last = this.last;
                    this.selectRange(this.last,  this.lastActive+1);
                    this.grid.getView().focusRow(this.lastActive);
                    if(last !== false){
                        this.last = last;
                    }
                }else{
                    this.selectFirstRow();
                }
            },
            scope: this
        });

        var view = this.grid.view;
        view.on("refresh", this.onRefresh, this);
        //       view.on("rowupdated", this.onRowUpdated, this);
        view.on("rowremoved", this.onRemove, this);




        /* Cell */
        this.grid.on("cellmousedown", this.handleCellMouseDown, this);
        if(Wtf.isIE || Wtf.isSafari){
            this.grid.getGridEl().on("keydown" , this.handleKeyDown, this);
//             this.grid.on("keypress" , this.handleCtrlKeyDown, this);
        } else{
            this.grid.getGridEl().on("keypress", this.handleKeyDown, this);
//             this.grid.on("keydown" , this.handleCtrlKeyDown, this);
        }
        //        var view = this.grid.view;
        view.on("refresh", this.onViewChange, this);
        view.on("rowupdated", this.onRowUpdated, this);
        view.on("beforerowremoved", this.clearSelections, this);
        view.on("beforerowsinserted", this.clearSelections, this);
        if (this.grid.isEditor){
            this.grid.on("beforeedit", this.beforeEdit,  this);
        }



    },
    /*
 * Part below not YET required  might be required for future changes
 */
    beforeEdit : function(e){
        this.select(e.row, e.column, false, true, e.record);
    },
    onViewChange : function(){
        this.clearSelections(true);
    },
    getSelectedCell : function(){
        return this.selection ? this.selection.cell : null;
    },
    isSelectable : function(rowIndex, colIndex, cm){
        return !cm.isHidden(colIndex);
    },
    acceptsNav : function(row, col, cm){
        return !cm.isHidden(col) && cm.isCellEditable(col, row);
    },
    onRefresh : function(){
        var ds = this.grid.store, index;
        var s = this.getSelections();
        this.clearSelections(true);
        for(var i = 0, len = s.length; i < len; i++){
            var r = s[i];
            if((index = ds.indexOfId(r.id)) != -1){
                this.selectRow(index, true);
            }
        }
        if(s.length != this.selections.getCount()){
            this.fireEvent("selectionchange", this);
        }
    },
    onRemove : function(v, index, r){
        if(this.selections.remove(r) !== false){
            this.fireEvent('selectionchange', this);
        }
    },
    getCount : function(){
        return this.selections.length;
    },
    selectFirstRow : function(){
        this.selectRow(0);
    },
    selectLastRow : function(keepExisting){
        this.selectRow(this.grid.store.getCount() - 1, keepExisting);
    },
    selectNext : function(keepExisting){
        if(this.hasNext()){
            this.selectRow(this.last+1, keepExisting);
            this.grid.getView().focusRow(this.last);
        }
    },
    selectPrevious : function(keepExisting){
        if(this.hasPrevious()){
            this.selectRow(this.last-1, keepExisting);
            this.grid.getView().focusRow(this.last);
        }
    },
    hasNext : function(){
        return this.last !== false && (this.last+1) < this.grid.store.getCount();
    },
    hasPrevious : function(){
        return !!this.last;
    },
    getSelections : function(){
        return [].concat(this.selections.items);
    },
    getSelected : function(){
        return this.selections.itemAt(0);
    },
    each : function(fn, scope){
        var s = this.getSelections();
        for(var i = 0, len = s.length; i < len; i++){
            if(fn.call(scope || this, s[i], i) === false){
                return false;
            }
        }
        return true;
    },
    hasSelection : function(){
        return this.selections.length > 0;
    },

    isSelected : function(index){
        var r = typeof index == "number" ? this.grid.store.getAt(index) : index;
        return (r && this.selections.key(r.id) ? true : false);
    },
    isIdSelected : function(id){
        return (this.selections.key(id) ? true : false);
    },
    deselectRange : function(startRow, endRow, preventViewNotify){
        if(this.locked) return;
        for(var i = startRow; i <= endRow; i++){
            this.deselectRow(i, preventViewNotify);
        }
    },
    restoreLast : function(){
        if(this._last){
            this.last = this._last;
        }
    },


    //===================================   Changes from here onwards    ==================================================================//



    selectAll : function(){
        if(this.locked) return;
        this.selections.clear();
        this.clearSelections();
        for(var i = 0, len = this.grid.store.getCount(); i < len; i++){
            this.selectRow(i, true, undefined, true);
        }
        this.fireEvent("selectionchange", this);
    },
    handleCellMouseDown : function(g, row, cell, e){
        if(e.button !== 0 || this.isLocked()){
            return;
        }
        this.select(row, cell);
    },
    handleRowMouseDown : function(g, row, cell, e){

    },

    select : function(rowIndex, colIndex, preventViewNotify, preventFocus,  r){
        if(this.fireEvent("beforecellselect", this, rowIndex, colIndex) !== false){
            this.clearCellSelection();
            if(colIndex == 1){
                this.selType = "Row";
            } else {
                this.clearRowSelections();
                this.selType = "Cell";
            }
            r = r || this.grid.store.getAt(rowIndex);
            if(this.grid.colModel.isCellEditable(colIndex, rowIndex)){
                this.selection = {
                    record : r,
                    cell : [rowIndex, colIndex]
                };
            } else {
                if(colIndex != 1)this.selType = "None";
                this.fireEvent("celldeselect", this, rowIndex, colIndex);
                return null;
            }
            if(!preventViewNotify){
                var v = this.grid.getView();
                v.onCellSelect(rowIndex, colIndex);
                if(preventFocus !== true){
                    v.focusCell(rowIndex, colIndex);
                }
            }
            this.fireEvent("cellselect", this, rowIndex, colIndex);
            this.fireEvent("selectionchange", this, this.selection);
        }
    },

    selectColumnCells : function(colIndex){
        var rows = this.grid.store.getCount();
        this.clearSelections();
        this.colCellSelected = true;
        this.selType = "Column";
        this.colCellSelectedIndex = colIndex;
        var v = this.grid.getView();
        for(var row = 0; row < rows; row++){
            v.onColumnCellSelect(row, colIndex);
        }
        this.fireEvent("celldeselect", this, -1, colIndex);
        this.fireEvent("columnCellsSelect", this, colIndex);
        this.fireEvent("selectionchange", this, this.selection);
    },

    onRowUpdated : function(v, index, r){
        if(this.isSelected(r)){
            v.onRowSelect(index);
        }
        if(this.selection && this.selection.record == r){
            v.onCellSelect(index, this.selection.cell[1]);
        }
    },

    selectRecords : function(records, keepExisting){
        if(!keepExisting){
            this.clearRowSelections();
        }
        var ds = this.grid.store;
        for(var i = 0, len = records.length; i < len; i++){
            this.selectRow(ds.indexOf(records[i]), true);
        }
    },

    clearSelections : function(fast){
        if(this.selection){
            var s = this.selection;
            if(s){
                if(fast !== true){
                    this.grid.view.onCellDeselect(s.cell[0], s.cell[1]);
                }
                this.selection = null;
                this.fireEvent("selectionchange", this, null);
            }
        }
        if(this.selections){
            if(this.locked) return;
            if(fast !== true){
                var ds = this.grid.store;
                var s = this.selections;
                s.each(function(r){
                    this.deselectRow(ds.indexOfId(r.id));
                }, this);
                s.clear();
            }else{
                this.selections.clear();
            }
            this.last = false;
        }
        this.clearColumnCellSelections();
        this.selType = "None";
    },

    clearCellSelection : function(fast){
        this.clearColumnCellSelections();
        if(this.selection){
            var s = this.selection;
            if(s){
                if(fast !== true){
                    this.grid.view.onCellDeselect(s.cell[0], s.cell[1]);
                }
                this.selection = null;
                this.fireEvent("selectionchange", this, null);
            }
        }
        this.selType = "None";
    },
    clearRowSelections : function(fast){
        this.clearColumnCellSelections();
        if(this.selections){
            if(this.locked) return;
            if(fast !== true){
                var ds = this.grid.store;
                var s = this.selections;
                s.each(function(r){
                    this.deselectRow(ds.indexOfId(r.id));
                }, this);
                s.clear();
            }else{
                this.selections.clear();
            }
            this.last = false;
        }
        this.selType = "None";
    },

    clearColumnCellSelections : function(){
        if(this.colCellSelected){
            this.colCellSelected = false;
            var rows = this.grid.store.getCount();
            var colIndex = this.colCellSelectedIndex;
            var v = this.grid.getView();
            for(var row = 0; row < rows; row++){
                v.onColumnCellDeSelect(row, colIndex);
            }
            this.fireEvent("selectionchange", this, this.selection);
            this.fireEvent("columnCellsDeSelect", this, colIndex);
            this.colCellSelectedIndex = -1;
        }
        this.selType = "None";
    },

    selectRows : function(rows, keepExisting){
        if(!keepExisting){
            this.clearRowSelections();
        }
        for(var i = 0, len = rows.length; i < len; i++){
            this.selectRow(rows[i], true);
        }
    },

    selectRange : function(startRow, endRow, keepExisting){
        if(this.locked) return;
        if(!keepExisting){
            this.clearRowSelections();
        }
        if(startRow <= endRow){
            for(var i = startRow; i <= endRow; i++){
                this.selectRow(i, true);
            }
        }else{
            for(var i = startRow; i >= endRow; i--){
                this.selectRow(i, true);
            }
        }
    },

    selectRow : function(index, keepExisting, preventViewNotify, dontFireEvent){
        if(this.locked || (index < 0 || index >= this.grid.store.getCount())) return;
        var r = this.grid.store.getAt(index);
        if(r && this.fireEvent("beforerowselect", this, index, keepExisting, r) !== false){
            if(!keepExisting || this.singleSelect){
                this.clearRowSelections();
            }
            this.selections.add(r);
            this.selType = "Row";
            this.last = this.lastActive = index;
            if(!preventViewNotify){
                this.grid.getView().onRowSelect(index);
            }
            this.fireEvent("rowselect", this, index, r);
            if(dontFireEvent==true){}
            else this.fireEvent("selectionchange", this);
        }
    },

    deselectRow : function(index, preventViewNotify){
        if(this.locked) return;
        if(this.last == index){
            this.last = false;
        }
        if(this.lastActive == index){
            this.lastActive = false;
        }
        var r = this.grid.store.getAt(index);
        if(r){
            this.selections.remove(r);
            if(!preventViewNotify){
                this.grid.getView().onRowDeselect(index);
            }
            this.fireEvent("rowdeselect", this, index, r);
            this.fireEvent("selectionchange", this);
        }
        this.selType = "None";
    },

    isAlphaNumKey:function(e){
        var kc = e.getCharCode();
        if( (kc >= 65 && kc <= 90)  || (kc > 47 && kc < 58) || (kc >= 96 && kc <= 121 && !(e.charCode == 0 || e.charCode == undefined) ) )
            return true;
        else
            return false;
    },

    isF2Pressed:function(e){    // Change this Code depending on other browsers
        if(Wtf.isSafari){
            if(e.charCode==63237){
                return true;
            } else if(e.charCode == 0 && e.keyCode==113 ){   // chrome
                return true;
            } else {
                return false;
            }
        } else {
            if( ( e.charCode == 0 || e.charCode == undefined ) && e.keyCode==113 ) {
                return true;
            } else {
                return false;
            }
        }

    },
     handleCtrlKeyDown : function(e){
        var g = this.grid, s = this.selection;
        if(!s)return;
        e.stopEvent();
        if(!e.isNavKeyPress()){
           if(e.getKey() == e.CONTROL || e.getKey() == e.ctrlKey){
                   g.startEditing(s.cell[0], s.cell[1],true);
           }
            return;
        }
    },
    handleKeyDown : function(e){
        var g = this.grid, s = this.selection;
        if(!s)return;
        e.stopEvent();
        if(!e.isNavKeyPress()){
           if(e.getKey() == e.CONTROL || e.getKey() == e.ctrlKey){
                   g.startEditing(s.cell[0], s.cell[1],true);
           }
            if( this.isF2Pressed(e) ) {
                if(g.isEditor && !g.editing) {
                    g.startEditing(s.cell[0], s.cell[1]);
                    this.navigationType = 1;
                }
            }
            else if(this.isAlphaNumKey(e)) {
                if(g.isEditor && !g.editing) {
                    var val = String.fromCharCode(e.getKey());
                    g.startEditing(s.cell[0], s.cell[1], true, val);
                    this.navigationType = 2;
                }
            }
            return;
        }
        if(!s){
            var cell = g.walkCells(0, 0, 1, this.isSelectable,  this);
            if(cell){
                this.select(cell[0], cell[1]);
            }
            return;
        }
        var sm = this;
        var walk = function(row, col, step){
            return g.walkCells(row, col, step, sm.isSelectable,  sm);
        };
        var k = e.getKey(), r = s.cell[0], c = s.cell[1];
        var newCell;
        switch(k){
            case e.TAB:
                if(e.shiftKey){
                    newCell = walk(r, c-1, -1);
                }else{
                    newCell = walk(r, c+1, 1);
                }
                break;
            case e.DOWN:
                newCell = walk(r+1, c, 1);
                break;
            case e.UP:
                newCell = walk(r-1, c, -1);
                break;
            case e.RIGHT:
                newCell = walk(r, c+1, 1);
                break;
            case e.LEFT:
                newCell = walk(r, c-1, -1);
                break;
            case e.ESC:
                newCell = walk(r, c, -1);
                break;
            case e.ENTER:
                if(e.shiftKey){
                    newCell = walk(r-1, c, -1);
                }else{
                    newCell = walk(r+1, c, 1);
                }
                break;
           default:
                newCell = walk(r, c, -1);
                break;
        }
        if(newCell){
            this.select(newCell[0], newCell[1]);
        }
    },

    onEditorKey : function(field, e){
        var k = e.getKey(), newCell, g = this.grid, ed = g.activeEditor;
        if(!e.isNavKeyPress() || e.getKey == undefined){

            return;
        }
        if(k == e.TAB){
            e.stopEvent();
            ed.completeEdit();
            if(e.shiftKey){
                newCell = g.walkCells(ed.row, ed.col-1, -1, this.acceptsNav, this);
            }else if(ed.row==0){
                newCell = g.walkCells(ed.row+1, ed.col+1, 1, this.acceptsNav, this);
            }else{
                newCell = g.walkCells(ed.row, ed.col+1, 1, this.acceptsNav, this);
            }
        }else if(k == e.ENTER){
            ed.completeEdit();
            if(e.shiftKey){
                newCell = g.walkCells(ed.row-1, ed.col, -1, this.acceptsNav, this);
            }else{
                newCell = g.walkCells(ed.row+1, ed.col, 1, this.acceptsNav, this);
            }
            e.stopEvent();
        }else if(k == e.ESC){
            e.stopEvent();
            ed.cancelEdit();
            return;
        }
        if(k == undefined){
            e.stopEvent();
            newCell = g.walkCells(ed.row, ed.col, 1, this.acceptsNav, this);
        }
        if(newCell){
            this.select(newCell[0], newCell[1]);
        }
    }

});

Wtf.SpreadSheet.Look = function(config){
    Wtf.apply(this, config);
    Wtf.SpreadSheet.Look.superclass.constructor.call(this);
};
Wtf.extend(Wtf.SpreadSheet.Look, Wtf.grid.GridView, {
    initTemplates : function() {
        var ts = this.templates || {};
        if(!ts.master){
            ts.master = new Wtf.Template(
                '<div class="x-grid3" hidefocus="true">',
                '<div class="x-grid3-viewport">',
                '<div class="x-grid3-header"><div class="x-grid3-header-inner"><div class="x-grid3-header-offset">{header}</div></div><div class="x-clear"></div></div>',
                '<div class="x-grid3-scroller"><div class="x-grid3-body">{body}</div><a href="#" class="x-grid3-focus" tabIndex="-1"></a></div>',
                "</div>",
                '<div class="x-grid3-resize-marker"> </div>',
                '<div class="x-grid3-resize-proxy"> </div>',
                "</div>"
                );
        }

        if(!ts.header){
            ts.header = new Wtf.Template(
                '<table border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
                '<thead><tr class="x-grid3-hd-row x-sheet-hd-row-ssg">{cells}</tr></thead>',
                "</table>"
                );
        }

        if(!ts.hcell){
            ts.hcell = new Wtf.Template(
                '<td class="x-grid3-hd x-grid3-cell x-grid3-td-{id}" style="{style}"><div ' +
                'Wtf:qtip="{tip}" {attr} class="x-grid3-hd-inner x-grid3-hd-{id}" unselectable="on" style="{istyle}">', this.grid.enableHdMenu ? '<a class="x-grid3-hd-btn" href="#"></a>' : '',
                '{value}<img class="x-grid3-sort-icon" src="', Wtf.BLANK_IMAGE_URL, '" />',
                "</div></td>"
                );
        }

        if(!ts.body){
            ts.body = new Wtf.Template('{rows}');
        }

        if(!ts.row){
            ts.row = new Wtf.Template(
                '<div class="x-grid3-row x-sheet-row {alt}" style="{tstyle}"><table class="x-grid3-row-table x-sheet-row-height" border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
                '<tbody><tr>{cells}</tr>',
                (this.enableRowBody ? '<tr class="x-grid3-row-body-tr" style="{bodyStyle}"><td colspan="{cols}" class="x-grid3-body-cell" tabIndex="0" hidefocus="on"><div class="x-grid3-row-body">{body}</div></td></tr>' : ''),
                '</tbody></table></div>'
                );
        }

        if(!ts.cell){
            ts.cell = new Wtf.Template(
                '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css} x-sheet-col-ssg" style="{style}" tabIndex="0" {cellAttr}>',
                '<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}>{value}</div>',
                "</td>"
                );
        }

        for(var k in ts){
            var t = ts[k];
            if(t && typeof t.compile == 'function' && !t.compiled){
                t.disableFormats = true;
                t.compile();
            }
        }

        this.templates = ts;

        this.tdClass = 'x-grid3-cell';
        this.cellSelector = 'td.x-grid3-cell';
        this.hdCls = 'x-grid3-hd';
        this.rowSelector = 'div.x-grid3-row';
        this.colRe = new RegExp("x-grid3-td-([^\\s]+)", "");
        this.modName = this.modulename;
    },
    handleHdMenuClick : function(item,extIndex,sortType){
        var index;
        var sorttype;
        if(item==null){
            index = extIndex;
            sorttype = sortType;
        }else{
            index = this.hdCtxIndex;
            sorttype=item.id;
        }
        var cm = this.cm, ds = this.ds;
        var grid = this;
        switch(sorttype){
            case "asc":
                this.serverSideSort(ds,cm.config[index],grid,cm.getDataIndex(index),"ASC");
                this.grid.ownerCt.SpreadSheetGrid.saveMyState();
                break;
            case "desc":
                this.serverSideSort(ds,cm.config[index],grid,cm.getDataIndex(index),"DESC");
                this.grid.ownerCt.SpreadSheetGrid.saveMyState();
                break;
            default:
                index = cm.getIndexById(item.id.substr(4));
                if(index != -1){
                    if(item.checked && cm.getColumnsBy(this.isHideableColumn, this).length <= 1){
                        this.onDenyColumnHide();
                        return false;
                    }
                    cm.setHidden(index, item.checked);
                }
        }
        return true;
    },

    onHeaderClick : function(g, index){
        if(this.headersDisabled || !this.cm.isSortable(index)){
            if(index!=1)
                g.getSelectionModel().selectColumnCells(index);
            return;
        }
        g.stopEditing();
        g.getSelectionModel().selectColumnCells(index);
    },

    onRowSelect : function(row){
        this.addRowClass(row, "x-grid3-row-selected");
    },

    onRowDeselect : function(row){
        this.removeRowClass(row, "x-grid3-row-selected");
    },

    onCellSelect : function(row, col){
        var cell = this.getCell(row, col);
        if(cell){
            this.fly(cell).addClass("x-sheet-cell-selected");
        }
    },

    onCellDeselect : function(row, col){
        var cell = this.getCell(row, col);
        if(cell){
            this.fly(cell).removeClass("x-sheet-cell-selected");
        }
    },

    onColumnCellSelect : function(row, col){
        var cell = this.getCell(row, col);
        if(cell){
            this.fly(cell).addClass("x-sheet-col-cells-selected");
        }
    },

    onColumnCellDeSelect : function(row, col){
        var cell = this.getCell(row, col);
        if(cell){
            this.fly(cell).removeClass("x-sheet-col-cells-selected");
        }
    },

    doRender : function(cs, rs, ds, startRow, colCount, stripe){
        var ts = this.templates, ct = ts.cell, rt = ts.row, last = colCount-1;
        var cm = this.cm;
        var tstyle = 'width:'+this.getTotalWidth()+';';
        var buf = [], cb, c, p = {}, r;
        var search = "";
        for(var j = 0, len = rs.length; j < len; j++){
            var rp = {tstyle: tstyle};
            r = rs[j]; cb = [];
            var rowIndex = (j+startRow);

            var myCellStyle = r.data.cellStyle; // must be object
            //     var myCellCss = r.data.cellCss;      // must be object
            var reg=new RegExp("[*^+.()[\\]$?]", "gi");
            for(var i = 0; i < colCount; i++){
                c = cs[i];
                p.id = c.id;
                p.css = i == 0 ? 'x-grid3-cell-first ' : (i == last ? 'x-grid3-cell-last ' : '');
                p.attr = p.cellAttr = "";
                p.value = c.renderer(r.data[c.name], p, r, rowIndex, i, ds);
                p.style = c.style;

                if(r.dirty && typeof r.modified[c.name] !== 'undefined'){
                    p.css += ' x-grid3-dirty-cell';
                }

                if(myCellStyle) p.style += myCellStyle[c.id] ? myCellStyle[c.id] : '';
                    //   if(myCellCss) p.css += myCellCss[c.id] ? myCellCss[c.id] : '';

                if(this.rules && i>1){
                    if(p.value){
                    var s1 = p.value.toString();
                    s1 = Wtf.util.Format.htmlDecode(s1);
                    s1 = WtfGlobal.HTMLStripper(s1?s1:"");
                    if(s1.indexOf(WtfGlobal.getCurrencySymbol()) != -1){
                        s1=s1.replace(WtfGlobal.getCurrencySymbol(),"");
                    }
                    if(s1.indexOf("&#160;") != -1){
                        s1=s1.replace("&#160;","");
                    }
                    for(var rli=0; rli<this.rules.length; rli++){
                        var rl = this.rules[rli];
                        search =  rl.search;
                        if ( search.match(reg) ){
                            search = search.replace(reg,"\$&");
                        }else{
                            if (search == "\\"){
                                search ="\\\\"
                            }
                        }
                        var s2 = new RegExp(search, "gi");
                        var m = s1.match(s2);
                        var b = false;
                        if(rl.combo==0 && m){
                            b = true;
                        } else if(rl.combo==1 && !m){
                            b = true;
                        } else if(rl.combo==2 && s1==rl.search){
                            b = true;
                        } else if(rl.combo==3 || rl.combo==4){
                            try{
                                search = search.replace(",","");
                                search = search.replace(" %","");
                                s1= WtfGlobal.replaceAll(s1,",","");
                                s1= s1.replace(" %","");
                                if((!isNaN(search)) && (!isNaN(s1)) && search != "" && s1 != "" ){
                                    var f1 = parseFloat(search);
                                    var f2 = parseFloat(s1);
                                    if(rl.combo==3 && f2<f1){
                                        b = true;
                                    } else if(rl.combo==4 && f2>f1){
                                        b = true;
                                    }
                                }
                            }
                            catch(e){
                            }
                        }

                        if( rl.tCheck && b && s1.trim().length >0 ) {
                            p.style += "color:#"+rl.txtPanel+";";
                        }
                        if( rl.bCheck && b && s1.trim().length >0) {
                            p.style += "background-color:#"+rl.bgPanel+";";
                        }
                    }

                }else{
                    p.value="&nbsp;";
                }
                }

                cb[cb.length] = ct.apply(p);
            }
            var alt = [];
            if(stripe && ((rowIndex+1) % 2 == 0)){
                alt[0] = "x-grid3-row-alt";
            }
            if(r.dirty){
                alt[1] = " x-grid3-dirty-row";
            }
            rp.cols = colCount;
            if(this.getRowClass){
                alt[2] = this.getRowClass(r, rowIndex, rp, ds);
            }
            rp.alt = alt.join(" ");
            rp.cells = cb.join("");
            buf[buf.length] =  rt.apply(rp);
        }
        return buf.join("");
    },

    renderUI : function(){

        var header = this.renderHeaders();
        var body = this.templates.body.apply({
            rows:''
        });

        var html = this.templates.master.apply({
            body: body,
            header: header
        });

        var g = this.grid;

        g.getGridEl().dom.innerHTML = html;
        this.initElements();

        this.mainBody.dom.innerHTML = this.renderRows();
        this.processRows(0, true);

        Wtf.fly(this.innerHd).on("click", this.handleHdDown, this);
        this.mainHd.on("mouseover", this.handleHdOver, this);
        this.mainHd.on("mouseout", this.handleHdOut, this);
        this.mainHd.on("mousemove", this.handleHdMove, this);

        this.scroller.on('scroll', this.syncScroll,  this);
        if(g.enableColumnResize !== false){
            this.splitone = new Wtf.grid.GridView.SplitDragZone(g, this.mainHd.dom);
        }

        if(g.enableColumnMove){
            this.columnDrag = new Wtf.SpreadSheet.ColumnDragZone(g, this.innerHd);      //
            this.columnDrop = new Wtf.SpreadSheet.HeaderDropZone(g, this.mainHd.dom);   //
        }

        if(g.enableHdMenu !== false){
            if(g.enableColumnHide !== false){
                this.colMenu = new Wtf.menu.Menu({
                    id:g.id + "-hcols-menu",
                    cls:'menuHeight'
                });
                this.colMenu.on("beforeshow", this.beforeColMenuShow, this);
                this.colMenu.on("itemclick", this.handleHdMenuClick, this);
            }
            this.hmenu = new Wtf.menu.Menu({
                id: g.id + "-hctx"
            });
            this.hmenu.add(
            {
                id:"asc",
                text: this.sortAscText,
                scope:this,
                handler:this.updateSortStateAsc,
                cls: "xg-hmenu-sort-asc"
            },
            {
                id:"desc",
                text: this.sortDescText,
                scope:this,
                handler:this.updateSortStateDesc,
                cls: "xg-hmenu-sort-desc"
            }
            );
            if(g.enableColumnHide !== false){
                this.hmenu.add('-',
                {
                    id:"columns",
                    text: this.columnsText,
                    menu: this.colMenu,
                    iconCls: 'x-cols-icon'
                }
                );
            }
            this.hmenu.on("itemclick", this.handleHdMenuClick, this);
        }
        if(g.enableDragDrop || g.enableDrag){
            var dd = new Wtf.grid.GridDragZone(g, {
                ddGroup : g.ddGroup || 'GridDD'
            });
        }

    this.updateHeaderSortState();

    },
    serverSideSort : function(store, column, grid, fieldName, dir) {
        var f = store.fields.get(fieldName);
        if(!f){
            return false;
        }
        if(!dir){
            if(store.sortInfo && store.sortInfo.field == f.name){
                dir = (store.sortToggle[f.name] || "ASC").toggle("ASC", "DESC");
            }else{
                dir = f.sortDir;
            }
        }
        store.sortToggle[f.name] = dir;
        store.sortInfo = {field: f.name, direction: dir, xtype : column.xtype, xfield : column.dbname,iscustomcolumn:column.iscustomcolumn?"True":"false"};

        delete store.baseParams['direction'];
        delete store.baseParams['field'];
        delete store.baseParams['xtype'];
        delete store.baseParams['xfield'];
        delete store.baseParams['iscustomcolumn'];
        store.baseParams = Wtf.apply(store.sortInfo || {}, store.baseParams);
        if(!store.remoteSort){
            store.applySort();
            store.fireEvent("datachanged", store);
        }
        else{
                var options = store.lastOptions || {};
                if(store.fireEvent("beforeload", this, options) !== false) {
                    store.storeOptions(options);
                    var p = Wtf.apply(options.params || {}, store.baseParams);
                    if(store.sortInfo && store.remoteSort){
                        p = Wtf.apply(p || {}, store.sortInfo);
                    }
                    store.proxy.load(p, store.reader, store.loadRecords, store, options);
                }
                GlobalSortModel[this.modulename] = store.sortInfo;
        }

    },
    updateSortStateAsc: function() {
                this.grid.ownerCt.SpreadSheetGrid.getStore().resumeEvents();
    },

    updateSortStateDesc: function() {
        var state = this.grid.ownerCt.SpreadSheetGrid.getStore().getSortState();
        if(!state){
            return;
        }
        if(state.direction == 'DESC' && this.sortState == state.field) {
            this.delayFunction=new Wtf.util.DelayedTask(function(){
                this.grid.ownerCt.SpreadSheetGrid.getStore().resumeEvents();
            },this);
            this.grid.ownerCt.SpreadSheetGrid.getStore().suspendEvents();
            this.delayFunction.delay(1400);
        } else {
            this.grid.ownerCt.SpreadSheetGrid.fireEvent('sortchange', this.grid.ownerCt.SpreadSheetGrid, state, true);
            this.sortState = state.field;
        }

    },
    renderHeaders : function(){
        var cm = this.cm, ts = this.templates;
        var ct = ts.hcell;
        var cb = [], sb = [], p = {};
        for(var i = 0, len = cm.getColumnCount(); i < len; i++){
            p.id = cm.getColumnId(i);
            p.value = cm.getColumnHeader(i) || "";
            p.style = this.getColumnStyle(i, true);
            p.tip = cm.config[i].tip;
            if(cm.config[i].align == 'right'){
                p.istyle = 'padding-right:16px';
            }
            cb[cb.length] = ct.apply(p);
        }
        return ts.header.apply({
            cells: cb.join(""),
            tstyle:'width:'+this.getTotalWidth()+';'
        });
    }
});

Wtf.SpreadSheet.HeaderDragZone = Wtf.grid.HeaderDragZone;

Wtf.SpreadSheet.HeaderDropZone =function(grid, hd, hd2){
    this.grid = grid;
    this.view = grid.getView();

    this.proxyTop = Wtf.DomHelper.append(document.body, {
        cls:"col-move-top", html:"&#160;"
    }, true);
    this.proxyBottom = Wtf.DomHelper.append(document.body, {
        cls:"col-move-bottom", html:"&#160;"
    }, true);
    this.proxyTop.hide = this.proxyBottom.hide = function(){
        this.setLeftTop(-100,-100);
        this.setStyle("visibility", "hidden");
    };
    this.ddGroup = "gridHeader" + this.grid.getGridEl().id;


    Wtf.SpreadSheet.HeaderDropZone.superclass.constructor.call(this, grid.getGridEl().dom);
};
Wtf.extend(Wtf.SpreadSheet.HeaderDropZone, Wtf.dd.DropZone, {
    proxyOffsets : [-4, -9],
    fly: Wtf.Element.fly,

    getTargetFromEvent : function(e){
        var t = Wtf.lib.Event.getTarget(e);
        var cindex = this.view.findCellIndex(t);
        if(cindex !== false){
            return this.view.getHeaderCell(cindex);
        }
    },

    nextVisible : function(h){
        var v = this.view, cm = this.grid.colModel;
        h = h.nextSibling;
        while(h){
            if(!cm.isHidden(v.getCellIndex(h))){
                return h;
            }
            h = h.nextSibling;
        }
        return null;
    },

    prevVisible : function(h){
        var v = this.view, cm = this.grid.colModel;
        h = h.prevSibling;
        while(h){
            if(!cm.isHidden(v.getCellIndex(h))){
                return h;
            }
            h = h.prevSibling;
        }
        return null;
    },

    positionIndicator : function(h, n, e){
        var x = Wtf.lib.Event.getPageX(e);
        var r = Wtf.lib.Dom.getRegion(n.firstChild);
        var px, pt, py = r.top + this.proxyOffsets[1];
        if((r.right - x) <= (r.right-r.left)/2){
            px = r.right+this.view.borderWidth;
            pt = "after";
        }else{
            px = r.left;
            pt = "before";
        }
        var oldIndex = this.view.getCellIndex(h);
        var newIndex = this.view.getCellIndex(n);

        if(this.grid.colModel.isFixed(newIndex)){
            return false;
        }

        var locked = this.grid.colModel.isLocked(newIndex);

        if(pt == "after"){
            newIndex++;
        }
        if(oldIndex < newIndex){
            newIndex--;
        }
        if(oldIndex == newIndex && (locked == this.grid.colModel.isLocked(oldIndex))){
            return false;
        }

        var sCol = this.grid.sCol ? this.grid.sCol : 2;
        if(oldIndex < sCol || newIndex < sCol){           //
            return false;
        }

        px +=  this.proxyOffsets[0];
        this.proxyTop.setLeftTop(px, py);
        this.proxyTop.show();
        if(!this.bottomOffset){
            this.bottomOffset = this.view.mainHd.getHeight();
        }
        this.proxyBottom.setLeftTop(px, py+this.proxyTop.dom.offsetHeight+this.bottomOffset);
        this.proxyBottom.show();
        return pt;
    },

    onNodeEnter : function(n, dd, e, data){
        if(data.header != n){
            this.positionIndicator(data.header, n, e);
        }
    },

    onNodeOver : function(n, dd, e, data){
        var result = false;
        if(data.header != n){
            result = this.positionIndicator(data.header, n, e);
        }
        if(!result){
            this.proxyTop.hide();
            this.proxyBottom.hide();
        }
        return result ? this.dropAllowed : this.dropNotAllowed;
    },

    onNodeOut : function(n, dd, e, data){
        this.proxyTop.hide();
        this.proxyBottom.hide();
    },

    onNodeDrop : function(n, dd, e, data){
        var h = data.header;
        if(h != n){
            var cm = this.grid.colModel;
            var x = Wtf.lib.Event.getPageX(e);
            var r = Wtf.lib.Dom.getRegion(n.firstChild);
            var pt = (r.right - x) <= ((r.right-r.left)/2) ? "after" : "before";
            var oldIndex = this.view.getCellIndex(h);
            var newIndex = this.view.getCellIndex(n);
            var locked = cm.isLocked(newIndex);
            if(pt == "after"){
                newIndex++;
            }
            if(oldIndex < newIndex){
                newIndex--;
            }
            if(oldIndex == newIndex && (locked == cm.isLocked(oldIndex))){
                return false;
            }

            var sCol = this.grid.sCol ? this.grid.sCol : 2;
            if(oldIndex < sCol || newIndex < sCol){           //
                return false;
            }

            cm.setLocked(oldIndex, locked, true);
            cm.moveColumn(oldIndex, newIndex);
            this.grid.fireEvent("columnmove", oldIndex, newIndex);
            return true;
        }
        return false;
    }
});

Wtf.SpreadSheet.ColumnDragZone = Wtf.grid.GridView.ColumnDragZone;

Wtf.SpreadSheet.Grid = Wtf.extend(Wtf.grid.GridPanel, {
    clicksToEdit: 1,
    isEditor : true,
    detectEdit: false,
    trackMouseOver: false,
    loadMask:false,
    border:false,

    initComponent : function(){
        Wtf.SpreadSheet.Grid.superclass.initComponent.call(this);
        this.activeEditor = null;
        this.addEvents(
            "beforeedit",
            "afteredit",
            "validateedit",
            "columnCellsSelect",
            "columnCellsDeSelect",
            "cellselect",
            "celldeselect",
            "savemystate",
            "aftersave"
            );
       this.addListener('render',this.addKeyMap, this);
    },
    addKeyMap:function(){
    	thisGrid = this;
    	Wtf.DomQuery.selectNode('div[class*=x-grid3-scroller]', this.getEl().dom).style.overflowX='visible';

	 	// map multiple keys to multiple actions by strings and array of codes
	 	new Wtf.KeyMap(Wtf.DomQuery.selectNode('div[class*=x-grid3-scroller]', this.getEl().dom).id, [{
	        key: "c",

	        ctrl:true,
	        fn: function(){

                                 var row1 = thisGrid.selModel.selection.cell[0];
                                 var col1=thisGrid.selModel.selection.cell[1];
                                    this.ra=[row1,col1];
		thisGrid.copyToClipBoard(this.ra);
		}
	    },{
	    	key: "v",
	        ctrl:true,
	        fn: function(){
                        var row1 = thisGrid.selModel.selection.cell[0];
                                 var col1=thisGrid.selModel.selection.cell[1];
                                    this.ra=[row1,col1];
                                    var g = thisGrid ;
                                    var s=this.ra;

	       thisGrid.pasteFromClipBoard(this.ra);
			}
	    }]);
	},
        copyToClipBoard:function(rows){
    	this.collectGridData(rows);
    	if( window.clipboardData && clipboardData.setData )	{
			clipboardData.setData("text", this.tsvData);
		} else {

			var hiddentextarea = this.getHiddenTextArea();
			hiddentextarea.dom.value = this.tsvData;
	    	hiddentextarea.focus();
	        hiddentextarea.dom.setSelectionRange(0, hiddentextarea.dom.value.length);

		}
    },
    collectGridData:function(cr){
        var row1 		= cr[0], col1 = cr[1];
        this.tsvData 	="";
        var columnName;
        columnName=thisGrid.selModel.grid.colModel.config[col1].dataIndex;
        this.tsvData = this.store.getAt(row1).data[columnName];
    	return this.tsvData;

	},




	pasteFromClipBoard:function(s){
//
//    	var hiddentextarea = this.getHiddenTextArea();
//    	hiddentextarea.dom.value ="";
//    	hiddentextarea.focus();
		this.store.getAt(s[0]).set( this.store.fields.itemAt(s[1]).name, this.tsvData);
    },
    updateGridData:function(){

    	var Record 			= Wtf.data.Record.create(this.store.fields.items);
    	var tsvData 		= this.hiddentextarea.getValue();
    	tsvData				= tsvData.split("\n");
    	var column			= [];
         var row1 = thisGrid.selModel.selection.cell[0];
                                 var col1=thisGrid.selModel.selection.cell[1];
                                    this.ra=[row1,col1];
    	var cr 				= this.ra;
		var nextIndex 		= cr[0];
		if( tsvData[0].split("\t").length==1 && ( (tsvData.length==1) || (tsvData.length==2  && tsvData[1].trim()== ""))){//if only one cell in clipboard data, block fill process (i.e. copy a cell, then select a group of cells to paste)

			for( var rowIndex = cr[0]; rowIndex<= cr[2]; rowIndex++){
				for( var columnIndex = cr[1]; columnIndex<= cr[3]; columnIndex++){
					this.store.getAt(rowIndex).set( this.store.fields.itemAt(columnIndex).name, tsvData[0] );

				}
			}
		}else{
			var gridTotalRows	= this.store.getCount();
			for(var rowIndex = 0; rowIndex < tsvData.length; rowIndex++ ){
				if( tsvData[rowIndex].trim()== "" ){
					continue;

				}
				columns	= tsvData[rowIndex].split("\t");
				if( nextIndex > gridTotalRows-1 ){
					var NewRecord 	= new Record({});
					this.stopEditing();
					this.store.insert(nextIndex, NewRecord);

				}
				pasteColumnIndex = cr[1];
				for(var columnIndex=0; columnIndex < columns.length; columnIndex++ ){
					this.store.getAt(nextIndex).set( this.store.fields.itemAt(pasteColumnIndex).name, columns[columnIndex] );

					pasteColumnIndex++;
				}
				nextIndex++;
			}
		}
    	this.hiddentextarea.blur();
    },
    getHiddenTextArea:function(){
		if(!this.hiddentextarea){
    		this.hiddentextarea = new Wtf.Element(document.createElement('textarea'));
			this.hiddentextarea.setStyle('border','2px solid #ff0000');
			this.hiddentextarea.setStyle('position','absolute');
			this.hiddentextarea.setStyle('z-index','-1');
			this.hiddentextarea.setStyle('width','100px');
			this.hiddentextarea.setStyle('height','30px');


    		this.hiddentextarea.addListener('keyup', this.updateGridData, this);
    		Wtf.get(this.getEl().dom.firstChild).appendChild(this.hiddentextarea.dom);
    	}
    	return this.hiddentextarea;

    },
    initEvents : function(){
        Wtf.SpreadSheet.Grid.superclass.initEvents.call(this);
        this.on("bodyscroll", this.stopEditing, this);

        if(this.isEditor) {
            this.on("cellclick", this.onCellSngClick, this);
            this.on("cellmousedown", this.onCellSngClick, this);
            this.on("celldblclick", this.onCellDblClick, this);
        }

        this.on("columnmove", this.saveMyState, this);
        this.on("columnresize", this.saveMyState, this);
        this.colModel.on("hiddenchange", this.saveMyState, this);
        this.colModel.on("widthchange", this.saveMyState, this);
        this.colModel.on("configchanged", this.saveMyState, this);
        this.KeyNavigation = true;
    },

    saveMyState : function(){
        var state = this.getState();
        this.fireEvent("savemystate", this, state);
    },

    applyState : function(state){
        var cm = this.colModel;
        var cs = state.columns;
        var chechwidth=false
        var checholdIndex=""
        var checholdi=""
        if(cs){
            for(var i = 0, len = cs.length; i < len; i++){
                var s = cs[i];
                var c = cm.getColumnById(s.id);
                if(c){
                    c.hidden = s.hidden;
                    c.width = s.width;
                    cm.setColumnWidth(i, s.width, true);
                    var oldIndex = cm.getIndexById(s.id);
                    if(oldIndex != i){
                        cm.moveColumn(oldIndex, i);
                    }
                    if(s.id=="checker"){
                        chechwidth=true;
                        checholdIndex=oldIndex;
                        checholdi=i;
                }
            }
        }
        this.colModel = cm;
        }
        if(state.sort){
            this.store[this.store.remoteSort ? 'setDefaultSort' : 'sort'](state.sort.field, state.sort.direction);
        }
        if(chechwidth && Wtf.isGecko){
            c = cm.getColumnById("checker");
            if(c){
                c.width = 18;
                cm.moveColumn(checholdIndex, checholdi);
            }
        }
    },

    applyCustomHeader: function(header){
        var cm = this.colModel;
        var cs = cm.config;
        for(var i = 0, len = cs.length; i < len; i++){
            var s = cs[i];
            var c = cm.getColumnById(s.id);
            var ismandotory = false;
            for(var j = 0 ; j< header.length && header[j].oldheader ; j++){
                var oldHeader = header[j].oldheader.trim();
                var newHeader = header[j].newheader.trim();
                ismandotory = header[j].ismandotory;
                if(header[j].recordname.replace(" ","_") == c.dataIndex || header[j].recordname == c.validationId || header[j].recordname==c.dataIndex){
                    if(ismandotory==true)
                        c.mandatory = true;
                    else
                        c.mandatory = false;
                }
                var currency = c.header.trim().split("(");
                var currency1;
                if(currency.length>1)
                    currency1= currency[1].split(")");
                        if(oldHeader == c.headerName.trim()){
                            c.header = newHeader;
                            if(currency[1]!=null){
                               newHeader = newHeader+"("+currency1[0]+")";
                               c.header = newHeader;
                            }
                            if(ismandotory && oldHeader.substring(oldHeader.length-1,oldHeader.length).trim()!="*"){
                               c.header = newHeader+" *";
                            } else if(ismandotory !=true && oldHeader.substring(oldHeader.length-1,oldHeader.length).trim()=="*"){
                               c.header =newHeader.substring(0,newHeader.length-1).trim()
                            }
                        }
                    }
                }
    },

    onCellDblClick : function(g, row, col){
        this.startEditing(row, col);
        this.KeyNavigation = false;
    },

    onCellSngClick : function(g, row, column){
//        var e ={
//            row:row,
//            column:column,
//            grid:g,
//            record:g.store.getAt(row)
//        }
        this.selModel.select(row, column, false, true, g.store.getAt(row));
    //    this.fireEvent('beforeedit',e);
    },

    onEditComplete : function(ed, value, startValue, oldCellValue){
        this.editing = false;
        this.activeEditor = null;
        ed.un("specialkey", this.selModel.onEditorKey, this.selModel);
        if(String(value) !== String(startValue)){
            var r = ed.record;
            var field = this.colModel.getDataIndex(ed.col);
            var header = this.colModel.getColumnHeaderName(ed.col);
            var xtype = this.colModel.getColumnXtype(ed.col);
            var editflag = true; // set editflag false if value is not present in Store.  (Kuldeep Singh)
            if(xtype=='combo'){
                var store = this.colModel.getColumnStore(ed.col);
                editflag = false;
                store.queryBy(function(record){
                    if(record.get("id")==value){
                        editflag = true;
                    }
                },this);
           }
            if(editflag){
                var e = {
                    grid: this,
                    record: r,
                    field: field,
                    originalValue: startValue,
                    value: value,
                    header:header,
                    oldCellValue: oldCellValue,
                    row: ed.row,
                    column: ed.col,
                    cancel:false
                };
                if(this.fireEvent("validateedit", e) !== false && !e.cancel){
                    if(typeof e.value == "string"){
                        r.set(field, WtfGlobal.HTMLStripper(e.value));
                    } else {
                        r.set(field, e.value);
                    }
                    delete e.cancel;
                    this.fireEvent("afteredit", e);
                }
            }
        }
        //this.view.focusCell(ed.row, ed.col);
    },

    startEditing : function(row, col, setNull, val){
        this.stopEditing();
        if(this.colModel.isCellEditable(col, row)){
            this.view.ensureVisible(row, col, true);
            var r = this.store.getAt(row);
            var field = this.colModel.getDataIndex(col);
            var e = {
                grid: this,
                record: r,
                field: field,
                value: r.data[field],
                row: row,
                column: col,
                cancel:false
            };
            if(this.fireEvent("beforeedit", e) !== false && !e.cancel){
                this.editing = true;
                var ed = this.colModel.getCellEditor(col, row);
                if(!ed.rendered){
                    ed.render(this.view.getEditorParent(ed));
                }
                (function(){
                    ed.row = row;
                    ed.col = col;
                    ed.record = r;
                    ed.on("complete", this.onEditComplete, this, {single: true});
                    ed.on("specialkey", this.selModel.onEditorKey, this.selModel);
                    this.activeEditor = ed;
                    var v = r.data[field];
                    ed.startEdit(this.view.getCell(row, col), v);
                    if(setNull)ed.field.setValue(val);
                }).defer(50, this);
            }
        }
    },

    stopEditing : function(){
        if(this.activeEditor){
            this.activeEditor.completeEdit();
        }
        this.activeEditor = null;
    },
    walkCells : function(row, col, step, fn, scope){
        var sCol=this.sCol?this.sCol:0;
        var eCol=this.eCol?this.eCol:0;
        var cm = this.colModel, clen = cm.getColumnCount()-eCol;
        var ds = this.store, rlen = ds.getCount(), first = true;

        if(step < 0){
            if(col < sCol){
                row--;
                first = false;
            }
            while(row >= 0){
                if(!first){
                    col = clen-1;
                }
                first = false;
                while(col >= sCol){
                    if(fn.call(scope || this, row, col, cm) === true){
                        return [row, col];
                    }
                    col--;
                }
                row--;
            }
        } else {
            if(col >= clen){
                row++;
                first = false;
            }
            while(row < rlen){
                if(!first){
                    col = sCol;
                }
                first = false;
                while(col < clen){
                    if(fn.call(scope || this, row, col, cm) === true){
                        return [row, col];
                    }
                    col++;
                }
                row++;
            }
        }
        return null;
    }

});

Wtf.reg('spreadSheetGrid', Wtf.SpreadSheet.Grid);

function showRWPalette(cid, panelID, imgDivID, checkID){
    Wtf.getCmp(cid).showColorPanel(cid, panelID, imgDivID, checkID);
}
function removeRWFieldSet(fid, cid, no){
            Wtf.getCmp(cid).removeFieldSet(fid, no);
        }
function addMoreRWFieldSet(cid){
    Wtf.getCmp(cid).addMoreFieldSet(true);
}



Wtf.SpreadSheet.RuleWindow = function (config){
    Wtf.apply(this,{
        buttons:[{
            text:WtfGlobal.getLocaleText("crm.common.savenapplybtn"),//'Save and Apply',
            scope:this,
            handler:this.applyHandler
        },{
            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//'Cancel',
            scope:this,
            handler:this.cancelHandler
        }]
    },config);

    Wtf.SpreadSheet.RuleWindow.superclass.constructor.call(this,config);
    this.addEvents(
        "ruleApply"
        );
}

Wtf.extend(Wtf.SpreadSheet.RuleWindow, Wtf.Window,{
    modal : true,
    id : 'RuleWindow',
    shadow : true,
    constrain : true,
    bufferResize : true,
    resizable : false,
    draggable:false,
    title : WtfGlobal.getLocaleText("crm.spreadsheet.conditionalcolorcodingBTN"),//'Conditional Color Coding',
    iconCls : "pwnd favwinIcon",
    initComponent : function(config){
        Wtf.SpreadSheet.RuleWindow.superclass.initComponent.call(this,config);

        this.fsid = this.id+'fieldSet';
        this.tRule = 0;
        this.width = 740;
        this.cnt=0;
        this.height = 'auto';
        this.fsArray = [];
        this.ruleCount = 0;
        this.createWindow();
        this.on("show",function()
        {
            var restoreWSize = this.getSize();
            var restorePSize = this.MainWinPanel.getSize();
            if((restoreWSize.height)>300 && this.grid.view.rules.length==0){
                restoreWSize.height=299;
                restorePSize.height=230;
                this.setSize(restoreWSize.width, restoreWSize.height );
                this.MainWinPanel.setSize(restorePSize.width, restorePSize.height);
                this.doLayout();
            }
        },this);
    },

    createWindow : function(){
        this.getUserRules();
        this.MainWinPanel= new Wtf.Panel({
            border : false,
            height : 160,
            layout : 'border',
            items : [{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.spreadsheet.conditionalcolorcodingBTN"), WtfGlobal.getLocaleText("crm.spreadsheet.conditionalcolorcodingBTN.ttip"), "../../images/sheet/ruleWin.png"),
                layout:'fit'
            },
            this.rulesPanel,
            {
                region : 'south',
                height : 30,
                border : false,
                bodyStyle : "background:rgb(241, 241, 241);",
                html: "<span title="+WtfGlobal.getLocaleText("crm.spreadsheet.conditionalcolorcodingaddmore")+" id='southAddRulesID' class='shortcuts'><a href='#' style='padding-right:30px;float:right;' onclick=addMoreRWFieldSet('"+this.id+"')>"+WtfGlobal.getLocaleText("crm.spreadsheet.colorcodingaddmore")+"</a></span>",
                layout:'fit'
            }]
        });
        this.add(this.MainWinPanel);
        this.MainWinPanel.doLayout();
    },

    getUserRules : function(){
        this.rulesPanel = new Wtf.Panel({
            style : "background:rgb(241, 241, 241);padding:0px 5px 0px 5px;",
            border : false,
            region : 'center',
            layout : "fit"
        });
        if(this.grid.view.rules!=undefined && this.grid.view.rules.length>0){
            this.on('show', this.editRulePanel, this);
        }
        else {
            this.createRulePanel();
        }
    },

    createRulePanel : function(){
        this.tRule++;
        this.addMoreFieldSet(false);
    },

    editRulePanel : function(rw){
        var rules = rw.grid.view.rules;
        for(var i=0;i<rules.length; i++){
            this.rulesPanel.add(this.getRuleForm(rules[i],false));
            if(i>0)this.adjustHeight(80, false);
            }
        this.doLayout();
    },

    addMoreFieldSet : function(doLayout){
        if(this.ruleCount>=4) {
            if(this.ruleCount == 4) {
                Wtf.get('southAddRulesID').dom.style.display="none";
            } else {
                return;
            }
        }
        this.rulesPanel.add(this.getRuleForm(false,true));
        if(doLayout){
            this.adjustHeight(80, true);
        }
    },

    removeFieldSet : function(fid, no){
        if(this.fsArray.length == 1 && !Wtf.getCmp(this.fsid + this.fsArray[0]+'search').isValid()) {
            ResponseAlert(511);
            return;
        }
        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("crm.msgbox.CONFIRMTITLE"), WtfGlobal.getLocaleText("crm.confirmdeletemsg"), function(btn){
            if (btn == "yes") {
                Wtf.get('southAddRulesID').dom.style.display="block";
                if(this.ruleCount<2){
                    this.addMoreFieldSet(true);
                }
                this.rulesPanel.remove(Wtf.getCmp(fid));
                this.ruleCount--;
                this.fsArray.remove(no);
                this.adjustHeight(-80, true);

                var rules = this.generateConditionRule(true,false);
                this.fireEvent('ruleApply', this, rules, true);
                }
        },this);

    },

    adjustHeight : function(height, doLayout){
         if(this.ruleCount==5){
             Wtf.get('southAddRulesID').dom.style.display="none";
             }
        var restoreWSize = this.getSize();
        var restorePSize = this.MainWinPanel.getSize();
        var restorePos = this.getPosition(true);
        if(this.cnt==0&&(restoreWSize.height + height)>350){
            restoreWSize.height=259;
            restorePSize.height=200;
        }
        this.cnt++;
        this.setPosition(restorePos[0], restorePos[1] - (height/2));
        this.setSize(restoreWSize.width, restoreWSize.height + height);
        this.MainWinPanel.setSize(restorePSize.width, restorePSize.height + height);

        if(doLayout)this.doLayout();

    },

    getRuleForm : function(values,isNew){
        this.ruleCount++;
        var no = this.tRule++;
        var cid = this.fsid + no;
        this.fsArray.push(no);
        //type
        var ruleTypeStore = new Wtf.data.SimpleStore({
            fields : ['id','name'],
            data : [
            [0,"Text contains"],
            [1,"Text does not contains"],
            [2,"Text exactly matches"],
            [3,"Number is less than"],
            [4,"Number is greater than"],
            [5,"Date is before"],
            [6,"Date is after"]
            ]
        });
        var ruleTypeCombo = new Wtf.form.ComboBox({
            fieldLabel : 'Rule',
            store : ruleTypeStore,
            value : 0,
            id : cid + 'combo',
            valueField : 'id',
            displayField : 'name',
            mode : 'local',
            triggerAction : 'all',
            editable : false,
            anchor : '70%',
            width : 155
        });

        ruleTypeCombo.on('change', function(combo, newValue, oldValue){
            var ser = Wtf.getCmp(cid + 'search');
            var val = ser.getValue();
            var owner = ser.ownerCt;
            if(!(oldValue==3 || oldValue==4)&&(newValue==3 || newValue==4)){
                var idx=owner.items.indexOf(ser);
                owner.remove(ser,true);
                ser=new Wtf.form.NumberField({
                    fieldLabel : 'Text',
                    id : cid + 'search',
                    allowBlank : false,
                    maxLength : 500,
                    width:'95%'
                });
                owner.insert(idx,ser);
            }else if(!(oldValue==5 || oldValue==6)&&(newValue==5 || newValue==6)){           	    
                    var idx=owner.items.indexOf(ser);
                    owner.remove(ser,true);
                    ser=new Wtf.form.DateField({
                        fieldLabel : 'Text',
                        id : cid + 'search',
                        offset:Wtf.pref.tzoffset,
                        format:WtfGlobal.getOnlyDateFormat(),
                        allowBlank : false,
                        anchor:'95%'
                    });
                    owner.insert(idx,ser);
            } else if(!(oldValue==0 ||oldValue==1 || oldValue==2)&&(newValue==0 ||newValue==1 || newValue==2)){
                var idx=owner.items.indexOf(ser);
                owner.remove(ser,true);
                ser=new Wtf.ux.TextField({
                    fieldLabel : 'Text',
                    id : cid + 'search',
                    allowBlank : false,
                    regex:/\w/,
                    maxLength : 500,
                    width:'95%'
                });
                owner.insert(idx,ser);
            }
            owner.doLayout();
            ser.setValue(val);
        }

        );


        //string
        var searchText = new Wtf.ux.TextField({
            fieldLabel : WtfGlobal.getLocaleText("crm.common.txt"),//'Text',
            id : cid + 'search',
            allowBlank : false,
            regex:/^[^<>]*$/,
            invalidText:"Values '<' & '>' are invalid for the field",
            maxLength : 500,
            width:'95%'
        });
       var padding="";
        if(Wtf.isIE7){
            padding='padding-left:4px !important;';
        }
        //text check
        var textColorCheck = new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.common.txtcolor"),// "Text Color",
            id : cid + 'tCheck',
            width : 'auto',
            name: "ssRuleTextColor",
            style:(Wtf.isOpera)?"left: -2px !important; top: 0px !important":""
        });

        //bg check
        var bgColorCheck = new Wtf.form.Checkbox({
            fieldLabel:WtfGlobal.getLocaleText("crm.common.backcolor"),// "Background Color",
            id : cid + 'bCheck',
            width : 'auto',
            name: "ssRuleBgColor",
            style:(Wtf.isOpera)?"left: -2px !important; top: 0px !important":""
        });

        var items = [
        {
            border : false,
            columnWidth : .27,
            frame : false,
            style : 'padding:4px;',
            items : ruleTypeCombo
        },
        {
            border : false,
            columnWidth : .28,
            frame : false,
            height:35,
            style : 'padding:4px;',
            items : searchText
        },
        {
            border : false,
            columnWidth : .05,
            frame : false,
            style : 'padding:2px 0px 4px 12px;',
            items : textColorCheck
        },
        {
            border : false,
            columnWidth : .1,
            id : cid+'txtPanel',
            mColor : '000000',
            frame : false,
            style : 'padding:6px 2px 2px 0px;',
            html : '<div style="float:left;'+padding+'"> Text: </div> <div id = "'+cid+'txtImgDiv" class="s-color-div" wtf:qtip="Click to add color" \n\
                            style="background-color:#000000;" onclick=showRWPalette("'+this.id+'","'+cid+'txtPanel'+'","'+cid+'txtImgDiv","' + cid + 'tCheck")></div>'
        },
        {
            border : false,
            columnWidth : .05,
            frame : false,
            style : 'padding:2px 0px 4px 12px;',
            items : bgColorCheck
        },
        {
            border : false,
            columnWidth : .15,
            id : cid+'bgPanel',
            mColor : 'FFFFFF',
            frame : false,
            style : 'padding:6px 2px 2px 0px;',
            html : '<div style="float:left;'+padding+'"> Background: </div><div id = "'+cid+'bgImgDiv" class="s-color-div" wtf:qtip="Click to add color" \n\
                            style="background-color:#FFFFFF;" onclick=showRWPalette("'+this.id+'","'+cid+'bgPanel'+'","'+cid+'bgImgDiv","' + cid + 'bCheck")></div>'
        },
        {
            border : false,
            columnWidth : .08,
            id : 'remove'+cid,
            frame : false,
            style : 'padding:8px 0px 10px 4px;',
            //            html : '<span class="shortcuts" ><a href="#"  onclick=removeRWFieldSet("' + cid + '","' + this.id + '",'+ no +')>remove</a></span>'
            html : '<span title="Click to remove rule" style="float:right;"><a href="#" onclick=removeRWFieldSet("' + cid + '","' + this.id + '",'+ no +')><img src="../../images/sheet/cancel.gif"/></a></span>'
        }
        ];

        // fieldset
        var fieldSet = new Wtf.form.FieldSet({
            style : 'padding:0px 10px 4px 10px;',
            layout : 'column',
            id : cid,
            isNew:isNew,
            autoHeight : true,
            title :WtfGlobal.getLocaleText("crm.common.setrule"),// 'Set Rule ',
            items : items
        });

        if(values){
            this.setRuleValues(cid, values);
        }

        return fieldSet;
    },

    showColorPanel : function(cid, panelID, imgDivID, checkID) {
        Wtf.getCmp(checkID).setValue(true);
        var colorPicker = new Wtf.menu.ColorItem();
        var contextMenu = new Wtf.menu.Menu({
            items: [ colorPicker ]
        });
        contextMenu.showAt(Wtf.get(panelID).getXY());
        colorPicker.on('select', function(palette, selColor){
            Wtf.getCmp(panelID).mColor = selColor;
            Wtf.get(imgDivID).dom.style.backgroundColor = '#' + selColor;
        },this);
    },

    setRuleValues : function(fsids, values){
        Wtf.getCmp(fsids+'combo').setValue(values.combo);
        var newValue = values.combo;
        var ser = Wtf.getCmp(fsids+'search');
        var owner = ser.ownerCt;
        var val = values.search;
        if(newValue==3 || newValue==4){
            var idx=owner.items.indexOf(ser);
            owner.remove(ser,true);
            ser=new Wtf.form.NumberField({
                fieldLabel : 'Text',
                id : fsids + 'search',
                allowBlank : false,
                maxLength : 500,
                width:'95%'
            });
            owner.insert(idx,ser);
            val = val*1;
        }else if(newValue==5 || newValue==6){           	    
            var idx=owner.items.indexOf(ser);
            owner.remove(ser,true);
            ser=new Wtf.form.DateField({
                fieldLabel : 'Text',
                id : fsids + 'search',
                offset:Wtf.pref.tzoffset,
                format:WtfGlobal.getOnlyDateFormat(),
                allowBlank : false,
                anchor:'95%'
            });
            owner.insert(idx,ser);
            val = new Date(val);
        }
        owner.doLayout();
        ser.setValue(val);        
        if(values.combo == 3 || values.combo == 4){
            Wtf.getCmp(fsids+'search').regex=/^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$/;
        }

        Wtf.getCmp(fsids+'tCheck').setValue(values.tCheck);
        Wtf.getCmp(fsids+'txtPanel').mColor = values.txtPanel;

        Wtf.getCmp(fsids+'bCheck').setValue(values.bCheck);
        Wtf.getCmp(fsids+'bgPanel').mColor = values.bgPanel;

        Wtf.getCmp(fsids).on('afterlayout',function(c, d, e){
            Wtf.get(fsids+'txtImgDiv').dom.style.backgroundColor = '#' + values.txtPanel;
            Wtf.get(fsids+'bgImgDiv').dom.style.backgroundColor = '#' + values.bgPanel;
        });

    },

    generateConditionRule: function(delFlag,isSave) {
        /*List of ids
         x + combo
         x + search
         x + tCheck
         x + bCheck
         x + bgPanel
         x + txtPanel
        */

        var rules = [];
        var contradictRules="";
        var contradictCount=0;
        for(var i=0; i<this.fsArray.length; i++) {
            var fsids = this.fsid + this.fsArray[i];
            Wtf.getCmp(fsids+'search').validate();
            if(Wtf.getCmp(fsids+'search').isValid()){
                  if(!Wtf.getCmp(fsids).isNew || isSave){
                var combo    = Wtf.getCmp(fsids+'combo').getValue();
                var search   = Wtf.getCmp(fsids+'search').getValue();
                var tCheck   = Wtf.getCmp(fsids+'tCheck').getValue();
                var txtPanel = Wtf.getCmp(fsids+'txtPanel').mColor;
                var bCheck   = Wtf.getCmp(fsids+'bCheck').getValue();
                var bgPanel  = Wtf.getCmp(fsids+'bgPanel').mColor;

                var obj = {};
                obj.combo = combo;
                obj.search = (search.getTime?search.getTime():search);
                obj.tCheck = tCheck;
                obj.txtPanel = txtPanel;
                obj.bCheck = bCheck;
                obj.bgPanel = bgPanel;

                //if(!(search.trim()=="")){
                    if(tCheck || bCheck){
                    	var ruleFlag = true;
                    	for(var j=0 ; j<rules.length ; j++){
                    		if(rules[j].search==obj.search){
                    			ruleFlag=false;
                                contradictRules+= " "+Wtf.getCmp(fsids+'combo').getRawValue()+" : "+search+","
                                contradictCount++;
                    			break;
                    		}
                    	}
                    	if(ruleFlag){
                    		rules[rules.length] = obj;

                    	}

                    }
                //}
            }
            }else{
                if(!delFlag && Wtf.getCmp(fsids+'search').getValue().trim()=="") {
                	Wtf.Msg.alert('Invalid Input',"Please enter all the essential details.");
                	return false;
                } else {
                    rules = "";
                }
            }
        }

        if(contradictCount>0){
            var contRuleLength= contradictRules.length;
            contradictRules = contradictRules.substr(0, (contRuleLength-1));
            var msg ="<b>There are "+contradictCount+" rules which are contradicting to existing rules.</b><br><br>"+contradictRules
            if(contradictCount==1){
                msg ="<b>There is "+contradictCount+" rule which is contradicting to existing rule.</b><br><br>"+contradictRules
            }
            WtfComMsgBox(["Alert", msg]);
            return false;
        }else {
            return rules;
        }

    },

    applyHandler : function(){
        var rules = this.generateConditionRule(false,true);

        if(rules.length<1){
            ResponseAlert(510);
        }
        if(!rules){
            //alert("there is some mistake in the rule");
        }else{
            this.fireEvent('ruleApply', this, rules, false);
            this.close();

        }
    },

    cancelHandler : function(){
        this.close();
        this.destroy();
    }

});


Wtf.SpreadSheet.FormulaeWindow = function (config){
    Wtf.apply(this,{
        buttons:[{
            text:WtfGlobal.getLocaleText("crm.common.previousbtn"),//'Previous',
            scope:this,
            handler:this.previousHandler,
            hidden:!this.createFieldFlag
        },{
            text:WtfGlobal.getLocaleText("crm.common.savenapplybtn"),//'Save and Apply',
            scope:this,
            handler:this.applyHandler
        },{
            text:WtfGlobal.getLocaleText("crm.CANCELBUTTON"),//Cancel,
            scope:this,
            handler:this.cancelHandler
        }]
    },config);

    Wtf.SpreadSheet.FormulaeWindow.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.SpreadSheet.FormulaeWindow, Wtf.Window,{
    modal : true,
    id : 'FormulaeWindow',
    shadow : true,
    constrain : true,
    iconCls: 'pwnd favwinIcon',
    bufferResize : true,
    resizable : false,
    closable:false,
    title : WtfGlobal.getLocaleText("crm.common.applyformula"),//'Apply Formula',
    initComponent : function(config){
        Wtf.SpreadSheet.FormulaeWindow.superclass.initComponent.call(this,config);

        this.fsid = this.id+'fieldSet';
        this.tRule = 0;
        this.width = 680;
        this.height = 'auto';
        this.fsArray = [];
        this.ruleCount = 0;
        this.createWindow();
        this.show();
    },

    createWindow : function(){
        this.getUserRules();
        this.MainWinPanel= new Wtf.Panel({
            border : false,
            height : 210,
            layout : 'border',
            items : [{
                region : 'north',
                height : 75,
                border : false,
                bodyStyle : 'background:white;border-bottom:1px solid #bfbfbf;',
                html: getTopHtml(WtfGlobal.getLocaleText("crm.common.applyformula"),WtfGlobal.getLocaleText("crm.common.applyformulawin.tophtml.detail")),//"Add formula to the custom column"),
                layout:'fit'
            },
            this.rulesPanel
            ]
        });
        this.add(this.MainWinPanel);
        this.MainWinPanel.doLayout();
    },

    getUserRules : function(){
        this.rulesPanel = new Wtf.form.FormPanel({
            style : "background:rgb(241, 241, 241);padding:10px;",
            border : false,
            region : 'center',
            layout : "fit"
        });
        this.createRulePanel();
    },

    createRulePanel : function(){
        this.tRule++;
        this.addMoreFieldSet(false);
    },

    editRulePanel : function(rw){
        var rules = rw.grid.view.rules;
        for(var i=0;i<rules.length; i++){
            this.rulesPanel.add(this.getRuleForm(rules[i]));
            if(i>0)this.adjustHeight(80, false);
            }
        this.doLayout();
    },

    addMoreFieldSet : function(doLayout){
        if(this.ruleCount>=4) {
            if(this.ruleCount == 4) {
                Wtf.get('southAddRulesID').dom.style.display="none";
            } else {
                return;
            }
        }
        this.rulesPanel.add(this.getRuleForm());
        if(doLayout){
            this.adjustHeight(80, true);
        }
    },

    removeFieldSet : function(fid, no){
         Wtf.get('southAddRulesID').dom.style.display="block";
        if(this.ruleCount<2){
            this.addMoreFieldSet(true);
        }
        this.rulesPanel.remove(Wtf.getCmp(fid));
        this.ruleCount--;
        this.fsArray.remove(no);
        this.adjustHeight(-80, true);
    },

    adjustHeight : function(height, doLayout){
        var restoreWSize = this.getSize();
        var restorePSize = this.MainWinPanel.getSize();
        var restorePos = this.getPosition(true);

        this.setPosition(restorePos[0], restorePos[1] - (height/2));
        this.setSize(restoreWSize.width, restoreWSize.height + height);
        this.MainWinPanel.setSize(restorePSize.width, restorePSize.height + height);

        if(doLayout)this.doLayout();

    },

    getRuleForm : function(values){
        this.ruleCount++;
        var no = this.tRule++;
        var cid = this.fsid + no;
        this.fsArray.push(no);
        //type
        var mainArray=[];

        for (i=0;i<this.cm.config.length;i++) {
            var tmpArray=[];
            if((this.cm.config[i].xtype=="numberfield" || (this.cm.config[i].sheetEditor!=undefined && this.cm.config[i].sheetEditor.xtype=="numberfield" && this.cm.config[i].id.substr(0, 12) == "custom_field"))
                && (this.cm.config[i].hidden==undefined || this.cm.config[i].hidden==false)) {
                var header=headerCheck(WtfGlobal.HTMLStripper(this.cm.config[i].header));
                 if(header!=this.fieldlabel && header!=this.fieldlabel+" *"){
                    var idField = "";
                    if(this.cm.config[i].dataIndex.indexOf("Custom")!= -1)
                        idField = "custom_"+this.cm.config[i].id.replace("custom_field","");
                    else
                        idField = this.modulename.toLowerCase()+"."+this.cm.config[i].dataIndex+"num";
                    if(idField=="custom_"+this.fieldid)
                    	continue;
                    tmpArray.push(idField);
                    tmpArray.push(this.cm.config[i].dataIndex);
                    tmpArray.push(header);
                    mainArray.push(tmpArray);
                 }
            }
        }
        var myData = mainArray;

        this.combostore = new Wtf.data.SimpleStore({
            fields: [
            {
                name: 'id'
            },{
                name: 'colname'
            },{
                name: 'name'
            }
            ]
        });
        this.combostore.loadData(myData);

        var operatorStore = new Wtf.data.SimpleStore({
            fields : ['id','name'],
            data : [
            ["+","+"],
            ["-","-"],
            ["*","*"],
            ["/","/"]
            ]
        });

        var opertorText1 = new Wtf.form.ComboBox({
            fieldLabel : WtfGlobal.getLocaleText("crm.customreport.header.column"),//'Column',
            store : this.combostore,
            id : cid + 'combo1',
            valueField : 'id',
            displayField : 'name',
            mode : 'local',
            triggerAction : 'all',
            editable : false,
           // anchor : '70%',
            width : 100
        });

        //string
        var operatorText = new Wtf.form.ComboBox({
            fieldLabel : WtfGlobal.getLocaleText("crm.common.operator"),//'Operator',
            store : operatorStore,
            id : cid + 'combo',
            valueField : 'id',
            displayField : 'name',
            mode : 'local',
            triggerAction : 'all',
            editable : false,
           // anchor : '70%',
            width : 110
        });

        var operatorText2 = new Wtf.form.ComboBox({
            fieldLabel : WtfGlobal.getLocaleText("crm.customreport.header.column"),//'Column',
            store : this.combostore,
            id : cid + 'combo2',
            valueField : 'id',
            displayField : 'name',
            mode : 'local',
            triggerAction : 'all',
            editable : false,
            // anchor : '70%',
            width : 110
        });

        var items = [
       {
            border : false,
            columnWidth : .28,
            frame : false,
            layout:'form',
            labelWidth:50,
            hidden:this.ruleCount>1?true:false,
            style : 'padding:4px;',
            items : opertorText1
        },
        {
            border : false,
            columnWidth : .28,
            frame : false,
            layout:'form',
            labelWidth:50,
            style : 'padding:4px;',
            items : operatorText
        },
        {
            border : false,
            columnWidth : .28,
            layout:'form',
            frame : false,
            labelWidth:50,
            style : 'padding:4px;',
            items : operatorText2
        },
        {
            border : false,
            columnWidth : .08,
            id : 'add'+cid,
            frame : false,
            hidden:this.ruleCount>1?true:false,
            style : 'padding:8px 0px 10px 4px;',
            html : '<span title='+WtfGlobal.getLocaleText("crm.common.clicktoextendformula")+' id="southAddRulesID" style="float:right;"><a href="#" onclick=addMoreRWFieldSet("'+this.id+'")><img src="../../images/add.gif"/></a></span>'
        },
        {
            border : false,
            columnWidth : .08,
            id : 'remove'+cid,
            frame : false,
            hidden:this.ruleCount>1?false:true,
            style : 'padding:8px 0px 10px 4px;',
            html : '<span title='+WtfGlobal.getLocaleText("crm.common.clicktoremoveformula")+' id="southRemoveRulesID" style="float:right;"><a href="#" onclick=removeRWFieldSet("' + cid + '","' + this.id + '",'+ no +')><img src="../../images/orgchart/Delete.gif"/></a></span>'
        }
        ];

        // fieldset
        var fieldSet = new Wtf.form.FieldSet({
            style : 'padding:10px;',
            layout : 'column',
            id : cid,
            autoHeight : true,
            title : WtfGlobal.getLocaleText("crm.common.setformula"),//'Set Formula',
            labelWidth:50,
            items : items
        });

        if(values){
            this.setRuleValues(cid, values);
        }

        return fieldSet;
    },

    applyHandler : function(){
        var formulae = "";
        var formulaeName = "";
        for(var i=0; i<this.fsArray.length; i++) {
            var fsids = this.fsid + this.fsArray[i];

            var operation    = Wtf.getCmp(fsids+'combo').getValue();
            var oppCode = "";
            if(operation == "+") {
                oppCode = "&#43;";
            } else if(operation == "-") {
                oppCode = "&#45;";
            }else if(operation == "*") {
                oppCode = "&#42;";
            }else if(operation == "/") {
                oppCode = "&#47;";
            }
            var operator1   = Wtf.getCmp(fsids+'combo1').getValue();
            var operator2   = Wtf.getCmp(fsids+'combo2').getValue();
            var opp1Index = this.combostore.find("id", Wtf.getCmp(fsids+'combo1').getValue());
            var opp2Index = this.combostore.find("id", Wtf.getCmp(fsids+'combo2').getValue());
            if(opp1Index != -1 && opp2Index != -1) {
                opp1Index = this.combostore.getAt(opp1Index).get("colname");
                opp2Index = this.combostore.getAt(opp2Index).get("colname");
            }
            if(operator1 != undefined) {
                formulae += operator1+oppCode+operator2;
                formulaeName += opp1Index+oppCode+opp2Index;
            } else {
                formulae += oppCode+operator2;
            }

        }
        if(this.fsArray.length.length<1){
            ResponseAlert(551);
        }
        var url = 'crm/common/fieldmanager/addCustomColumnFormulae.do';
        if(this.createFieldFlag){
            url = 'crm/common/fieldmanager/insertfield.do';
        }
        if(formulae != "") {
            Wtf.Ajax.requestEx({
                url: url,
                params:{
                    mode:1,
                    fieldlabel:this.fieldlabel,
                    maxlength:this.maxlength,
                    validationType:this.validationType,
                    fieldType:this.fieldType,
                    modulename:this.modulename,
                    moduleid:this.moduleid,
                    isessential:this.isessential,
                    iseditable:false,
                    customregex:this.customRegex,
                    rules:formulae,
                    rulesWithColname:formulaeName,
                    createFieldFlag : this.createFieldFlag,
                    defaultval : this.defaultval,
                    fieldid:this.fieldid,
                    ismandatory :this.ismandatory?"True":"false"
                }
            },
            this,
            function(res) {
                if(res.msg) {
                    Wtf.Msg.alert('Status', res.msg);
                    if(this.ismandatory){
                        this.spreadsheet.getMyConfig(true);
                        this.spreadsheet.fireEvent('reloadexternalgrid',true);
                    }
                } else {
                    Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.common.columncreatedmsg"));//'New column created successfully.<br/> Please close the tab and open again to use the new field');
                }
                loadCustomFieldColModel(this.moduleid);
            },
            function(res) {
                Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.customreport.failurerespmsg"));
            }
            );
            this.close();
            if(this.createFieldFlag) {
                Wtf.getCmp("new_custom_field_window").destroy();
            }
        } else {
            Wtf.Msg.alert(WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"), WtfGlobal.getLocaleText("crm.common.noformulagenerated"));//'No formulae generated to apply');
        }
    },

    cancelHandler : function(){
        if(this.createFieldFlag) {
            Wtf.getCmp("new_custom_field_window").destroy();
        }
        this.close();

    },

    previousHandler: function(){
        this.close();
        Wtf.getCmp("new_custom_field_window").show();
    }

});

Wtf.SpreadSheet.Panel = function (config){
    Wtf.SpreadSheet.Panel.superclass.constructor.call(this,config);
}

Wtf.extend(Wtf.SpreadSheet.Panel, Wtf.Panel,{
    layout:'fit',
    border:false,
    selColIndex : 0,
    selType : "None",
    sCol:3,
    eCol:0,
    initComponent: function(config){
        this.ssDBid = -1;
        this.rules = [];
        this.selectflag=0;
        if(!this.isDetailPanel)
            this.getMyConfig();

        this.addEvents({
            'reloadexternalgrid': true
        });

        Wtf.SpreadSheet.Panel.superclass.initComponent.call(this,config);

        this.createSsTbar();
        this.createSelModel();
        this.createColModel();
        this.createSpreadSheetLook();

        this.createGrid();

        this.addEvents2Comp();

        this.add(this.SpreadSheetGrid);

    },

    addEvents2Comp:function(){
        this.SpreadSheetGrid.selModel.on('cellselect',this.setSelectedCellText,this);
        this.SpreadSheetGrid.selModel.on('celldeselect',this.setSelectedCellTextNull,this);
        this.SpreadSheetGrid.on('cellcontextmenu', this.cellcontextmenu, this);
        this.SpreadSheetGrid.on('rowcontextmenu', this.rowcontextmenu, this);

        this.SpreadSheetGrid.selModel.on('columnCellsSelect',this.columnCellsSelectHandler,this);
        this.SpreadSheetGrid.selModel.on('columnCellsDeSelect',this.columnCellsDeSelectHandler,this);
        this.SpreadSheetGrid.selModel.on('selectionchange',this.selectionChangeHandler,this);
        this.SpreadSheetGrid.on('savemystate', this.saveMyStateHandler, this);

    },

    cellcontextmenu:function(grid, rowIndex, cellIndex, e){
        e.stopEvent();
    },
    validateSelection : function(combo,record,index){
        return record.get('hasAccess' );
    },
    rowcontextmenu:function(grid, rowIndex, e){
        e.stopEvent();
    },
    getCustomColumnData:function(rData,isMassordetails){
        jsondata = ",customfield:[]";
        var GlobalcolumnModel=GlobalColumnModel[this.moduleid];
        var isEmpty=true;
        if(GlobalcolumnModel){
            jsondata =',customfield:[{';
            for(var cnt = 0;cnt<GlobalcolumnModel.length;cnt++){
                var fieldname = GlobalcolumnModel[cnt].fieldname;
                var refcolumn_number = GlobalcolumnModel[cnt].refcolumn_number;
                var column_number = GlobalcolumnModel[cnt].column_number;
                var fieldid = GlobalcolumnModel[cnt].fieldid;
                var fieldtype = GlobalcolumnModel[cnt].fieldtype;
                if(isMassordetails&&(rData[fieldname]=="" || rData[fieldname]==undefined)){
                    continue;
                }
                if(cnt > 0){
                    jsondata +="},{";
                }
                 var recData = "";
                if(fieldname.indexOf('.')>=0){
                    recData = rData[fieldname.replace(".","")]
                }else{
                    recData = rData[fieldname]
                }
                if(GlobalcolumnModel[cnt].fieldtype=="3" && recData!=""){
                    var daterec =recData;
                    if(recData!=undefined && recData!="" ){
                    	daterec =new Date(recData).getTime();
//                        if(isMassordetails){
//                        }
                        if(daterec=="")
                            daterec="";
                    }
                    jsondata +="\"refcolumn_name\": \""+refcolumn_number+"\",\"fieldname\": \""+fieldname+"\",\""+column_number+"\": \""+daterec+"\",\""+fieldname+"\": \""+column_number+"\",\"filedid\":\""+fieldid+"\",\"xtype\":\""+fieldtype+"\"";
                }else if(GlobalcolumnModel[cnt].fieldtype=="5"){  // Time Field
                    if(recData!=undefined && recData!="" ){
                        recData =  WtfGlobal.convertToGenericTime(Date.parseDate(recData,WtfGlobal.getLoginUserTimeFormat()));
                    }
                    jsondata +="\"refcolumn_name\": \""+refcolumn_number+"\",\"fieldname\": \""+fieldname+"\",\""+column_number+"\": \""+recData+"\",\""+fieldname+"\": \""+column_number+"\",\"filedid\":\""+fieldid+"\",\"xtype\":\""+fieldtype+"\"";
                }else
                    jsondata +="\"refcolumn_name\": \""+refcolumn_number+"\",\"fieldname\": \""+fieldname+"\",\""+column_number+"\": \""+recData+"\",\""+fieldname+"\": \""+column_number+"\",\"filedid\":\""+fieldid+"\",\"xtype\":\""+fieldtype+"\"";
            }
            jsondata +='}]';
        }
        return jsondata;
    },
    getCustomField:function(){
        var field = [];
        var colModelArray = GlobalColumnModel[this.moduleid];
        if(colModelArray) {
            for(var cnt = 0;cnt<colModelArray.length;cnt++) {
                field.push(colModelArray[cnt].fieldlabel);
            }
        }
        return field;
    },
    getCustomValues:function(rData){
        var values = [];
        var colModelArray = GlobalColumnModel[this.moduleid];
        if(colModelArray) {
            for(var cnt = 0;cnt<colModelArray.length;cnt++) {
                var colModelObj = this.customColumnModel[cnt];
                if(colModelObj!=undefined){
                     var fieldname=colModelArray[cnt].fieldname.replace(".","");
                     var fieldid = colModelObj.fieldid;
                    if(GlobalComboStore["cstore"+fieldid]) {
                        if(colModelObj.fieldtype=="7"){ // for multiselet combo
                            var store = GlobalComboStore["cstore"+fieldid];
                            var pushValue=searchValueFieldMultiSelect(store,rData[fieldname],'id','name')

                            values.push(pushValue);
                        }
                        else {
                            store = GlobalComboStore["cstore"+fieldid];
                            var num = store.find("id", rData[fieldname]);
                            if(num != -1) {
                                values.push(store.getAt(num).get('name'));
                            } else {
                                values.push("");
                            }
                        }
                    } else {
                        values.push(rData[fieldname]);
                    }
                }
            }
        }
        return values;
    },
    getEmptyCustomFields:function(gridRec){
       var colModelArray = GlobalColumnModel[this.moduleid];
       if(colModelArray) {
            for(var cnt = 0;cnt<colModelArray.length;cnt++) {
                gridRec[colModelArray[cnt].fieldname]='';
            }
        }
        return gridRec;
    },
    createGrid:function(){
         var colModelArray = GlobalColumnModel[this.moduleid];
         if(colModelArray){
            for(var cnt = 0;cnt < colModelArray.length;cnt++){
                var fieldname = colModelArray[cnt].fieldname;
                var newField = new Wtf.data.Field({
                    name:fieldname.replace(".",""),
                    sortDir:'ASC',
                    type:this.customColumnModel[cnt].fieldtype == 3 ?  'date' : 'auto',
                    dateFormat:this.customColumnModel[cnt].fieldtype == 3 ?  'time' : undefined
                });
                this.store.fields.items.push(newField);
                this.store.fields.map[fieldname]=newField;
                this.store.fields.keys.push(fieldname);
            }
        }
        this.bbar1 = (this.pagingFlag ? new Wtf.PagingSearchToolbar({
                pageSize: 25,
                searchField:this.quickSearchTF,
                parentGridObj:this.parentGridObj,
                id: "pagingtoolbar" + this.id,
                store: this.store,
                displayInfo: true,
                emptyMsg: WtfGlobal.getLocaleText("crm.goalsettings.ermsg"),
                plugins:this.pP = new Wtf.common.pPageSize({
                    id: "pPageSize_" + this.id,
                    recordsLimit: 100,
                    spreadSheet:true
                })
            }):'')
       this.store.reader = new Wtf.data.KwlJsonReader(this.store.reader.meta, this.store.fields.items);
        this.SpreadSheetGrid = new Wtf.SpreadSheet.Grid({
            store        :   this.store,
//            loadMask     :   this.loadMask,
            isEditor     :   this.isEditor==false?false:true,
            cm           :   (this.cmArray?this.getColModel():this.cm),
            selModel     :   this.getSelModel(),
            view         :   this.getSpreadSheetLook(),
            tbar         :   this.getSsTbar(),
            bbar         :   this.bbar1,
            sCol         :   this.sCol,
            eCol         :   this.eCol,
            stripeRows   :   true,
            id           :   this.gid
        });

        this.store.on('load',function(store, records, options){
            var timeFiledIndex=[];
            var fieldCnt = 0;
            if(this.moduleid && GlobalColumnModel[this.moduleid] && logintimeformat==2)  {
                // For Custom Field Only - need to convert custom time field (which is stored as 12 hrs format with AM/PM) into 24hrs format if having user's preference
                var GlobalcolumnModel=GlobalColumnModel[this.moduleid];
                for(var cnt = 0;cnt<GlobalcolumnModel.length;cnt++){
                    if(GlobalcolumnModel[cnt].fieldtype=="5"){ // time field
                        timeFiledIndex[fieldCnt++] = cnt;
                    }
                }
                if(timeFiledIndex.length>0) {
                    for(var cnt1=0; cnt1<records.length; cnt1++) {
                        var rData = records[cnt1].data;
                        for(var innerCnt=0;innerCnt<timeFiledIndex.length;innerCnt++) {
                            var fieldName = GlobalcolumnModel[timeFiledIndex[innerCnt]].fieldname;
                            var recData = rData[fieldName]
                            if(recData!=undefined && recData!="") {
                                var dateObj = Date.parseDate(recData,"g:i A");
                                if(dateObj) {
                                    recData =  dateObj.format(WtfGlobal.getLoginUserTimeFormat());
                                    records[cnt1].set(fieldName,recData);
                                }
                            }
                        }
                    }
                    this.store.commitChanges();
                }
            }

             var cell = this.SpreadSheetGrid.view.getHeaderCell(1);
             var hd = Wtf.fly(cell).child('.x-grid3-hd-checker');
             if(hd)
                hd.removeClass('x-grid3-hd-checker-on');
            if(this.pagingFlag) {
                this.quickSearchTF.StorageChanged(this.store);
                this.quickSearchTF.on('SearchComplete', function() {
                    this.SpreadSheetGrid.getView().refresh();
                }, this);
            }
        },this);
        this.store.on("datachanged",function(){
            if(this.pagingFlag) {
                if(this.pP.combo)
                    this.quickSearchTF.setPage(this.pP.combo.value);
            }
        },this);
    },

    createSsTbar:function(){
        this.createStyleButtons();

        this.clearFormatBut = new Wtf.Toolbar.Button({
            tooltip:WtfGlobal.getLocaleText("crm.common.clearformatting"),//"Clear formatting.",
            iconCls:'sheetBar clear-format-img',
            scope : this,
            handler : function(but, e){
                this.clearFormatHandler();
            }
        });
        this.clearFormatBut.setDisabled(true);

        this.boldBut = new Wtf.Toolbar.Button({
            tooltip:WtfGlobal.getLocaleText("crm.common.bold"),//"Bold",
            iconCls:'sheetBar bold-img',
            scope : this,
            handler : function(bold, e){
                this.extraStyleHandler("font-weight", "bold");
            }
        });
        this.boldBut.setDisabled(true);

        this.strikeBut = new Wtf.Toolbar.Button({
            tooltip:"Strikethrough",
            iconCls:'sheetBar strike-img',
            scope : this,
            handler : function(strike, e){
                this.extraStyleHandler("text-decoration", "line-through");
            }
        });
        this.strikeBut.setDisabled(true);

        this.alignBut = new Wtf.Toolbar.Button({
            tooltip:WtfGlobal.getLocaleText("crm.common.align"),//"Align",
            iconCls:'sheetBar align-palette-left-img',
            menu: this.alignMenu
        });
        this.alignBut.setDisabled(true);

        this.textColorBut = new Wtf.Toolbar.Button({
            tooltip:WtfGlobal.getLocaleText("crm.common.txtcolor"),//"Text color",
            iconCls:'sheetBar text-color-but-img',
            menu: this.textColorMenu
        });
        this.textColorBut.setDisabled(true);
        this.bgColorBut = new Wtf.Toolbar.Button({
            tooltip:WtfGlobal.getLocaleText("crm.common.backcolor"),//"Background color",
            iconCls:'sheetBar bg-color-but-img',
            menu: this.bgColorMenu
        });
        this.bgColorBut.setDisabled(true);
        this.undoBut = new Wtf.Toolbar.Button({
            tooltip:WtfGlobal.getLocaleText("crm.common.undo"),//"Undo formatting",
            iconCls:'sheetBar undo-img',
            scope : this,
            handler :this.undoOperation
        });

        this.undoBut.setDisabled(true);

        this.redoBut = new Wtf.Toolbar.Button({
            tooltip:WtfGlobal.getLocaleText("crm.common.redo"),//"Redo formatting",
            iconCls:'sheetBar redo-img',
            scope : this,
            handler :this.redoOperation
        });

        this.redoBut.setDisabled(true);

        this.ruleWin = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("crm.spreadsheet.conditionalcolorcodingBTN"),//"Conditional Color Coding",
            iconCls:"applyRulesIcon",
            tooltip:WtfGlobal.getLocaleText("crm.spreadsheet.conditionalcolorcodingBTN.ttip"),//"Change color based on rules.",
            scope:this,
            handler: function(){
                var ruleWin = new Wtf.SpreadSheet.RuleWindow({
                    sSheet : this,
                    grid : this.getGrid()
                });
                ruleWin.show();
                ruleWin.on('ruleApply', this.saveMyRuleHandler, this);
            }
        });

        this.customizeHeader = new Wtf.Action({
            text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.customizeheader"),//"Customize Header",
            iconCls:"pwnd customizeHeader",
            handler:function (){

                this.customizeHeaderWin=new Wtf.customizeHeader({
                    scope:this,
                    modulename:this.SpreadSheetGrid.view.modulename
                });
                Wtf.getCmp("crm_customize_header").show();
                this.customizeHeaderWin.on("aftersave",function(id,iscustomHeader){
                    if(!this.isDetailPanel)
                    this.getMyConfig(iscustomHeader);
                    this.store.reload();
                },this)

            },
            scope:this
        });

        this.customFormulae = new Wtf.Action({
            text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.addcustomformula"),//"Add Custom Formula",
            iconCls:"pwnd customizeHeader",
            handler:function (){
                if(this.selColIndex != -1) {
                    var fieldid = this.colModel.config[this.selColIndex].id;
                    if(fieldid.substr(0, 12) == "custom_field") {
                        var colModelConfigArray = this.colModel.config[this.selColIndex];
                        var sheetEditor = colModelConfigArray.sheetEditor;
                        if(sheetEditor != undefined && (sheetEditor.xtype == "combo"
                                || sheetEditor.xtype == "datefield" || sheetEditor.xtype == "timefield")) {
                             WtfComMsgBox(["Status", "You cannot set formula to the Combo, Date or Time fields."]);
                             return;
                        }

//                        callExpressionManager({
//                            ismandatory : colModelConfigArray.mandatory,
//                            fieldlabel:colModelConfigArray.dataIndex,
//                            modulename:this.moduleName,
//                            moduleid:this.moduleid,
//                            createFieldFlag : false,
//                            fieldid:fieldid.substr(12,fieldid.length),
//                            spreadsheet : this
//                        });
                        new Wtf.SpreadSheet.FormulaeWindow({
                            id:this.id+'addCustomFormulae',
                            cm:this.colModel,
                            ismandatory : colModelConfigArray.mandatory,
                            fieldlabel:colModelConfigArray.dataIndex,
                            modulename:this.moduleName,
                            moduleid:this.moduleid,
                            createFieldFlag : false,
                            fieldid:fieldid.substr(12,fieldid.length),
                            spreadsheet : this
                        });
                    } else {
                        WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"),WtfGlobal.getLocaleText("crm.managecolumnmenu.customformula.msg")]);
                    }
                } else {
                    WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.customformula.msg")]);
                }
            },
            scope:this
        });

        this.delCustomColumn = new Wtf.Action({
            text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.deletecustomcol"),//"Delete Custom Column",
            tooltip:{text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.deletecustomcol.ttip")},//'Delete selected custom column.'},
            iconCls:"pwnd customizeHeader",
            handler:function(){
            	deleteCustomColumn(this);
            },
            scope:this
        });

        this.addCustomColumn = new Wtf.Action({
            text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.addcustomcol"),//"Add Custom Column",
            tooltip:{text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.addcustomcol")},//'Add new custom column.'},
            iconCls:"pwnd customizeHeader",
            handler:function (){
                addCustomColumn(this);
            },
            scope:this
        });
        this.editCustomColumn = new Wtf.Action({
            text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.editcustomcol"),//"Edit Custom Column",
            tooltip:{text:WtfGlobal.getLocaleText("crm.spredsheet.managecolumnmenu.editcustomcol.ttip")},//'Edit new custom column.'},
            iconCls:"pwnd customizeHeader",
            handler:function (){
                if(this.selColIndex != -1) {
                    var colModelConfigArray = this.colModel.config[this.selColIndex];
                    var sheetEditor = colModelConfigArray.sheetEditor;

                    var fieldid = colModelConfigArray.id;
                    if(fieldid.substr(0, 12) == "custom_field") {
                        if(sheetEditor != undefined && (sheetEditor.xtype == "combo" || sheetEditor.xtype == "select" )  ) {
                            WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.customformula.cannoteditcombomsg")]);
                            return;
                        }else if(sheetEditor == undefined && colModelConfigArray.xtype == "numberfield") {
                            WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.customformula.cannoteditfieldmsg")]);
                            return;
                        }
                        EditCustomColumn(this,colModelConfigArray);
                    }else {
                        WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"), WtfGlobal.getLocaleText("crm.managecolumnmenu.editcustomcol.msg")]);
                    }
                }else {
                     WtfComMsgBox([WtfGlobal.getLocaleText("crm.case.defaultheader.status"),WtfGlobal.getLocaleText("crm.managecolumnmenu.editcustomcol.msg")]);
                }
            },
            scope:this
        });
        var menuArray=[this.customizeHeader];
        if(this.moduleName != "Campaign"){
            menuArray.push(this.addCustomColumn);
            menuArray.push(this.editCustomColumn);
            menuArray.push(this.delCustomColumn);
            menuArray.push(this.customFormulae);
        }
        this.customize=new Wtf.Toolbar.Button({
            iconCls: "pwnd customizeHeader",
            text:"Manage Columns",
            menu: menuArray
        });

        this.selectedCellText = new Wtf.form.TextField({
            maxLength:85,
            style : 'text-align:center;',
            width:200,
            readOnly:true
        });
        this.SSEditorText = new Wtf.form.TextField({
            maxLength:300,
            width:210,
            readOnly:true
        });
        this.refreshButton = new Wtf.Toolbar.Button({
            scope:this,
            iconCls:"pwndCRM refreshButton",
            tooltip:{text:'Click to refresh.'},
            handler:function(){this.store.reload();}
        });

        this.sstbar = [];
        this.sstbar.push(this.selectedCellText);
        this.sstbar.push("-");
        this.sstbar.push(this.SSEditorText);
        this.sstbar.push("-");
        this.sstbar.push(this.clearFormatBut);
        this.sstbar.push("-");
        this.sstbar.push(this.boldBut);
        this.sstbar.push(this.strikeBut);
        //        this.sstbar.push(this.blinkBut);
        this.sstbar.push("-");
        this.sstbar.push(this.alignBut);
        this.sstbar.push(this.textColorBut);
        this.sstbar.push(this.bgColorBut);
        this.sstbar.push("-");
        this.sstbar.push(this.undoBut);
        this.sstbar.push(this.redoBut);
        this.sstbar.push(this.ruleWin);
        this.sstbar.push("-");
        if(Wtf.URole.roleid == Wtf.AdminId){
            var modName = this.parentGridObj.modName;
            if(modName!="AccountActivity" && modName!="CampaignActivity" && modName!="CaseActivity" && modName!="ContactActivity" && modName!="LeadActivity"  && modName!="OpportunityActivity"){
                if(this.parentGridObj.newFlag==2){
                    this.sstbar.push(this.customize);
                    this.sstbar.push("-");
                }
            }
        }
          this.sstbar.push("->");
         this.sstbar.push(this.refreshButton);
    },
    enableDisableUndoButt : function(){
        if (this.parentGridObj.storeFormat.length > 0) {
            this.undoBut.setDisabled(false);
        }else{
            this.undoBut.setDisabled(true);
        }
    },
    enableDisableRedoButt : function(){
        if (this.parentGridObj.redoObject.length > 0) {
            this.redoBut.setDisabled(false);
        }else{
            this.redoBut.setDisabled(true);
        }
    },
    redoOperation : function(){
        if (this.parentGridObj.redoObject.length > 0) {
            var jsonObject=eval('('+this.parentGridObj.redoObject.pop()+')');
            this.parentGridObj.storeFormat.push(jsonObject);
            this.enableDisableUndoButt();
            var row;
            var recordid;
            var recordIndex;
            var record;
            var cellStyle;
            var colIndex;
            var recordname;
            var style;
            var lastStyle;
            var sel=[];
            var styleSplit;
            var styleValue;
            var isDeleted;
            for(var i=0; i < jsonObject.length;i++ ){
                row = jsonObject[i];
                recordid = row.id;
                recordIndex = this.store.find(this.keyid,recordid);
                record = this.store.getAt(recordIndex);
                cellStyle=row.cellStyle;
                isDeleted=row.isdeleted;
                for(var key in cellStyle){
                    recordname = key;
                    colIndex=this.colModel.getIndexById(this.colModel.lookup[recordname].id);
                    style = cellStyle[recordname];
                    lastStyle=style.trim();
                    if(style != ""){
                        if(lastStyle != ""){
                            if(isDeleted){
                                styleSplit=lastStyle.split(' ');
                                for(var j = 0; j < styleSplit.length;j++){
                                    style = styleSplit[j];
                                    this.removeSpecificStyleFromRecCell(record,recordname,style);
                                }
                            }else{
                                styleSplit=lastStyle.split(':');
                                styleValue=styleSplit[1].trim();
                                styleValue=styleValue.substring(0,styleValue.length-1);
                                this.addStyleToRecCell(styleSplit[0],styleValue.trim(),recordIndex,colIndex);
                            }
                        }

                    }
                }
                sel.push(record);

            }
            this.refreshMyView(false);
            Wtf.saveStyleInDB(sel,true,undefined,this.keyid,this.moduleName,this.parentGridObj);
         //   this.parentGridObj.redoObject =new Array() ;
            this.enableDisableRedoButt();
        }
    },
    undoOperation : function(){
        if (this.parentGridObj.storeFormat.length > 0) {
            var jsonObject=this.parentGridObj.storeFormat.pop();
            this.enableDisableUndoButt();

            var row;
            var recordid;
            var recordIndex;
            var record;
            var cellStyle;
            var colIndex;
            var recordname;
            var style;
            var lastStyle;
            var sel=[];
            var isDeleted;
            var tempJsonObject = Wtf.encode(jsonObject);
            for(var i=0; i < jsonObject.length;i++ ){
                row = jsonObject[i];
                recordid = row.id;
                recordIndex = this.store.find(this.keyid,recordid);
                if(recordIndex != -1){
                    record = this.store.getAt(recordIndex);
                    cellStyle=row.cellStyle;
                    isDeleted=row.isdeleted;
                    for(var key in cellStyle){
                        recordname = key;
                        colIndex=this.colModel.getIndexById(this.colModel.lookup[recordname].id);
                        style = cellStyle[recordname];
                        lastStyle = style;
                        lastStyle=lastStyle.trim();
                        if(lastStyle != ""){
                            if(isDeleted){
                                  this.addStylesToRecCell(lastStyle,recordIndex,colIndex);
                            }else{
                                this.removeSpecificStyleFromRecCell(record,recordname,lastStyle);
                            }

                        }
                    }
                    sel.push(record);
                }else{
                    tempJsonObject=eval('('+tempJsonObject+')');
                    tempJsonObject.splice(i,1);
                    tempJsonObject = Wtf.encode(tempJsonObject);
                }
            }
            tempJsonObject=eval('('+tempJsonObject+')');
            if(tempJsonObject.length > 0){
                this.refreshMyView(false);
                Wtf.saveStyleInDB(sel,true,undefined,this.keyid,this.moduleName,this.parentGridObj);
                //this.parentGridObj.redoObject = Wtf.encode(tempJsonObject);// for redo operation
                this.parentGridObj.redoObject.push(Wtf.encode(tempJsonObject));
                this.enableDisableRedoButt();
            }else{
                this.undoOperation();
            }

        }
    },
    createColModel:function(){
        this.reloadflag = true;
        this.colArr = [];
        var rowNum = new Wtf.RowNumbererWithNew({width:30});
        this.colArr.push(rowNum);
        this.colArr.push(this.getSelModel());
        if(this.cmArray)
            for(var i =0; i<this.cmArray.length; i++){
                this.colArr.push(this.cmArray[i]);
            }
           this.customColumnModel = GlobalColumnModel[this.moduleid];
           if(this.customColumnModel){
            for(var cnt=0;cnt<this.customColumnModel.length;cnt++){
                var tempObj=null;
                var colModelObj = this.customColumnModel[cnt];
                var fieldtype = colModelObj.fieldtype;
                var fieldid = colModelObj.fieldid;
                var editorObj = {
                    xtype:WtfGlobal.getXType(fieldtype),
                    maxLength:colModelObj.maxlength,
                    required:colModelObj.isessential,
                    store:null,
                    useDefault:true
                }
                 if(fieldtype == 3){
                     editorObj['format']=WtfGlobal.getOnlyDateFormat();
                 }
                if(fieldtype==4 || fieldtype==7){
                    editorObj = {
                        xtype:WtfGlobal.getXType(fieldtype),
                        required:colModelObj.isessential,
                        store:null,
                        useDefault:true
                    }
                    if(GlobalComboStore["cstore"+fieldid] == null){
                            this.reloadflag = false;
                            this.laststorecnt = cnt;
                            GlobalComboStore["cstore"+fieldid] = new Wtf.data.Store({
                                url:'crm/common/fieldmanager/getCustomCombodata.do',
                                baseParams : {
                                               fieldid : fieldid,
                                               _dc : Math.random(),
                                               valreq:'0'
                                    },
                                 reader: new Wtf.data.JsonReader({
                                         root:'data'
                                         }, GlobalComboReader),
                                autoLoad:true
                            });
                      var tempScopeObj = this;
//                      GlobalComboStore["cstore"+fieldid].on("load",function(){
//                          tempScopeObj.SpreadSheetGrid.view.refresh();
//                      })
                    } else {
                        GlobalComboStore["cstore"+fieldid].reload();
                    }
                    editorObj.store=GlobalComboStore["cstore"+fieldid];
                }
                if(fieldtype==8){// Ref Module Combo
                    editorObj = {
                        xtype:WtfGlobal.getXType(fieldtype),
                        required:colModelObj.isessential,
                        store:null,
                        useDefault:true
                    }
                    var store;
                    if(GlobalComboStore["cstore"+fieldid] == null){
                        if(colModelObj.moduleflag == 1) {
                            var comboid = colModelObj.comboid;
                            if(comboid == Wtf.common.productModuleID) {
                                store = Wtf.productStore;
                            }
                            /*
                            * Not in used
                            */
                            /*else if(comboid == Wtf.common.leadModuleID) {
                                store = Wtf.leadStore;
                            } else if(comboid == Wtf.common.contactModuleID) {
                                store = Wtf.contactStore;
                            } else if(comboid == Wtf.common.caseModuleID) {
                                store = Wtf.caseStore;
                            } else if(comboid == Wtf.common.oppModuleID) {
                                store = Wtf.opportunityStore;
                            } */else if(comboid == Wtf.common.userModuleID) {
                                store = Wtf.allUsersStore;
                            }
                        } else {
                            store = new Wtf.data.Store({
                                url: 'Common/CRMManager/getComboData.do',
                                baseParams:{
                                    comboname:colModelObj.comboname,
                                    common:'1'
                                },
                                reader: new Wtf.data.KwlJsonReader({
                                    root:'data'
                                },  Wtf.ComboReader),
                                autoLoad:false
                            });
                        }
                        GlobalComboStore["cstore"+fieldid] = store;
                    } else {
                        store = GlobalComboStore["cstore"+fieldid];
                    }
                    store.load();
                    editorObj.store=GlobalComboStore["cstore"+fieldid];
                }

                var suffix = colModelObj.isessential ? " *":"";
                var dbname = fieldtype==7?colModelObj.refcolumn_number:colModelObj.column_number;
                tempObj = {
                    header:colModelObj.fieldlabel+suffix,
                    tip:colModelObj.fieldlabel+suffix,
                    id:'custom_field'+fieldid,
                    fieldid:fieldid,
                    sheetEditor: colModelObj.iseditable=="true" ? editorObj: undefined,
                    dataIndex:colModelObj.fieldname.replace(".",""),
                    pdfwidth:60,
                    dbname:dbname,//Custom field column in which value is saved.
                    iscustomcolumn:true,
                    sortable:colModelObj.iseditable=="true"?true:false,
                    xtype:WtfGlobal.getXType(fieldtype),
                    fieldtype:fieldtype,
                    refdbname : colModelObj.column_number//Custom field column in which sort(single) value for multi select dropdown is saved.
                }
                if(fieldtype == 3){
                     tempObj['renderer']=WtfGlobal.onlyDateRenderer;
                }else if(fieldtype == 5){
                     tempObj['renderer']=WtfGlobal.getTimeFieldRenderer;
                }
                else if(fieldtype==2){
                    var currencyColName;
                    var colnamelegth = colModelObj.fieldname.length;
                    if(colnamelegth > 8) {
                        currencyColName = colModelObj.fieldname.substr(colnamelegth-8, colnamelegth);
                    }
                    if(currencyColName == "currency" || currencyColName == "Currency") {
                      tempObj['renderer']=WtfGlobal.currencyRenderer;
                    } else {
                	  tempObj['renderer']=WtfGlobal.zeroRenderer;
                    }
                }

               this.colArr.push(tempObj);
            }
          }
            if(this.reloadflag){
                this.fireEvent('reloadexternalgrid',this.reloadflag);
            }else{
                GlobalComboStore["cstore"+this.customColumnModel[this.laststorecnt].fieldid].on('load',function(){
                    this.fireEvent('reloadexternalgrid',this.reloadflag);
                },this);
            }
        this.colModel = new Wtf.SpreadSheet.ColumnModel(this.colArr);
    },

    createSelModel:function(){
        this.selModel = new Wtf.SpreadSheet.SelectionModel();
    },

    createSpreadSheetLook:function(){
        this.spreadSheetLook = new Wtf.SpreadSheet.Look({
            forceFit : false,
            moduleid:this.moduleid,
            id:this.id,
            gridObjScope: this,
            modulename:this.moduleName,
            parentGridObj:this.parentGridObj
        });
    },

    getSsTbar:function(){
        return this.sstbar;
    },

    getGrid:function(){
        return this.SpreadSheetGrid;
    },

    getColModel:function(){
        return this.colModel;
    },

    getSelModel:function(){
        return this.selModel;
    },

    getSpreadSheetLook:function(){
        return this.spreadSheetLook;
    },

    changeSSEditorText:function(){
        this.SSEditorText = null;
    },

    setSelectedCellText : function(selModel, rowIndex, colIndex){
        this.setSelType(false);
        var header = this.getColModel().getColumnHeaderName(colIndex);
        this.selectedCellText.setValue(header+" X "+(rowIndex+1) );

        var valData = this.getGrid().getView().getCell(rowIndex,colIndex).innerHTML;
        valData = valData.replace("&nbsp;", " ");
        valData = Wtf.util.Format.htmlDecode(valData);
        var val = WtfGlobal.HTMLStripper(valData?valData:"");
        this.SSEditorText.setValue(val);
    },

    setSelectedCellTextNull : function(selModel, rowIndex, colIndex){
        this.setSelType();
        this.selectedCellText.setValue("");
        this.SSEditorText.setValue("");
    },

    columnCellsDeSelectHandler : function(obj, index){
        this.setSelType();
        this.selColIndex = -1;
    },

    columnCellsSelectHandler : function(obj, index){
        this.setSelType();
        this.selColIndex = index;
    },

    selectionChangeHandler : function(sm){
       var grid = sm.grid;
        var cell = grid.view.getHeaderCell(1);
        var hd = Wtf.fly(cell).child('.x-grid3-hd-checker');

        if (sm.getCount() === grid.getStore().getCount()) {
            if(hd)
                hd.addClass('x-grid3-hd-checker-on');
        } else {
            if(hd)
                hd.removeClass('x-grid3-hd-checker-on');
        }
        this.setSelType();
    },

    setSelType : function() {
        var disable = false;
        var sm = this.getSelModel();
        if(sm.selType == "None" || (sm.getSelections().length==0&&sm.selType != "Cell"))disable=true;

        if(sm.selType == 'Cell'){
            if(sm.selection && sm.selection.record && sm.selection.record.get(this.keyid) == "0"){
                disable=true;
            }
        }
        if(!disable && (sm.selType == "Row" ) ){
            var selections=sm.getSelections();
            var record;
            for(var i=0;i< selections.length;i++){
                record=selections[i];
                if (record.get(this.keyid) == "0") {
					disable = true;
					// break;
				} else {
					disable = false;
				}
            }
        }
        this.alignBut.setDisabled(disable);
        this.textColorBut.setDisabled(disable);
        this.bgColorBut.setDisabled(disable);
        this.boldBut.setDisabled(false);
        this.strikeBut.setDisabled(disable);
        //     this.blinkBut.setDisabled(disable);
        this.clearFormatBut.setDisabled(false);
    },

    createStyleButtons : function(){
        this.alignMenu = new Wtf.menu.AlignMenu();
        this.alignMenu.on('select',function(cm, style){
            this.extraStyleHandler("text-align", style);
        },this);


        this.textColorMenu = new Wtf.menu.ColorMenu({
            allowReselect : true
        });
        this.textColorMenu.on('select',function(cm, color){
            this.extraStyleHandler('color', '#'+color);
        },this);


        this.bgColorMenu = new Wtf.menu.ColorMenu({
            allowReselect : true
        });
        this.bgColorMenu.on('select',function(cm, color){
            this.extraStyleHandler('background-color', '#'+color);
        },this);

    },

    //==============================================================================================================================//

    addStyleToRecCell : function(property, value, row, col){
        if(this.selectType=="Cell"){
            this.selectArray=[];
            this.selectArray[0]=row;
            this.selectArray[1]=col;
        }
        var hID = this.getColModel().getColumnId(col);
        var rec = this.store.getAt(row);
        if(!rec.data.cellStyle)rec.data.cellStyle={};
        if(!rec.data.cellStyle[hID])rec.data.cellStyle[hID]='';
        rec.data.cellStyle[hID] += " "+property+":"+value+";";

        if(!rec.data.tempCellStyle)rec.data.tempCellStyle={};
        if(!rec.data.tempCellStyle[hID])rec.data.tempCellStyle[hID]='';
        rec.data.tempCellStyle[hID] += " "+property+":"+value+";";

    },
    addStylesToRecCell : function(styles, row, col){
        var hID = this.getColModel().getColumnId(col);
        var rec = this.store.getAt(row);
        if(!rec.data.cellStyle)rec.data.cellStyle={};
        if(!rec.data.cellStyle[hID])rec.data.cellStyle[hID]='';
        rec.data.cellStyle[hID] += " "+styles;
    },

    removeStyleFromRecCell : function(rec, hID, property) {
        if(property && rec.data.cellStyle[hID]) {
            var re = new RegExp(' ' + property + ':[\\w\\s-,#]*;', "g");
            rec.data.cellStyle[hID] = rec.data.cellStyle[hID].replace(re, "","g");
        }
    },

    removeSpecificStyleFromRecCell : function(rec, hID, style) {
        if(rec.data.cellStyle[hID]) {
            style = " "+style;
            var re = new RegExp(style+'(?!.*'+style+')', "g");
            rec.data.cellStyle[hID] = rec.data.cellStyle[hID].replace(re, "");
        }
    },

    clearRecTempCellStyle : function(rec) {
            rec.data.tempCellStyle={};
    },

    clearStyleFromRecCell : function(row, col) {
        var hID = this.getColModel().getColumnId(col);
        var rec = this.store.getAt(row);
        if(rec.data.cellStyle && rec.data.cellStyle[hID]) {
            rec.data.tempCellStyle[hID]=rec.data.cellStyle[hID];
            delete rec.data.cellStyle[hID];
        }
    },

//==============================================================================================================================//

    clearFormatHandler : function(){
      //  ResponseAlert(500);
        var selType = this.getSelModel().selType;
        if(selType == "None") {

        } else if(selType == "Row") {
            this.clearStyleRowHandler();
        } else if(selType == "Column") {
            this.clearStyleColumnHandler();
        } else if(selType == "Cell") {
            this.clearStyleCellHandler();
        }
        this.refreshMyView(false);

    },

    clearStyleCellHandler : function(property, value){
        var sel = [];
        var cell = this.getSelModel().getSelectedCell();
        if(cell){
            var record = this.getGrid().getStore().getAt(cell[0]);
            this.clearRecTempCellStyle(record);
            sel.push(record);
            this.clearStyleFromRecCell(cell[0], cell[1]);
        }
        Wtf.saveStyleInDB(sel,undefined,1,this.keyid,this.moduleName,this.parentGridObj);
    },

    clearStyleColumnHandler : function(property, value){
        var col = this.selColIndex;
        var sel = [];
        var rows = this.getGrid().getStore().getCount();
        for(var row=0; row<rows; row++){
            sel.push(this.getGrid().getStore().getAt(row));
            this.clearStyleFromRecCell(row, col);
        }
        this.fireEvent('addStyle', this, sel);
    },

    clearStyleRowHandler : function(property, value){
        var sel = this.getSelModel().getSelections();
        var cCount = this.getColModel().getColumnCount();
        for(var i=0;i<sel.length;i++){
            this.clearRecTempCellStyle(sel[i]);
            var row = this.getGrid().getStore().indexOf(sel[i]);
            for(var col=this.sCol; col < cCount-this.eCol; col++){
                this.clearStyleFromRecCell(row, col);
            }
        }
        Wtf.saveStyleInDB(sel,undefined,1,this.keyid,this.moduleName,this.parentGridObj);
        this.getSelModel().clearSelections();
    },



//==============================================================================================================================//

    extraStyleHandler : function(propery, value){
      //  ResponseAlert(500);
        var selType = this.getSelModel().selType;
        this.selectType=selType;
        if(selType == "None") {

        } else if(selType == "Row") {
            this.extraStyleRowHandler(propery, value);
        } else if(selType == "Column") {
            this.extraStyleColumnHandler(propery, value);
        } else if(selType == "Cell") {
            this.extraStyleCellHandler(propery, value);
        }
        this.refreshMyView(false);
        this.selectBack();
    },

    extraStyleCellHandler : function(property, value){
        var sel = [];
        var cell = this.getSelModel().getSelectedCell();
        if(cell){
            var record = this.getGrid().getStore().getAt(cell[0]);
            this.clearRecTempCellStyle(record);
            sel.push(record);
            this.addStyleToRecCell(property, value, cell[0], cell[1]);
        }
       Wtf.saveStyleInDB(sel,undefined,undefined,this.keyid,this.moduleName,this.parentGridObj);
    },

    extraStyleColumnHandler : function(property, value){
        var col = this.selColIndex;
        var sel = [];
        var rows = this.getGrid().getStore().getCount();
        for(var row=0; row<rows; row++){
            sel.push(this.getGrid().getStore().getAt(row));
            this.addStyleToRecCell(property, value, row, col);
        }
        Wtf.saveStyleInDB(sel,undefined,undefined,this.keyid,this.moduleName,this.parentGridObj);
    },

    extraStyleRowHandler : function(property, value){
        var sel = this.getSelModel().getSelections();
        this.selectArray=[];
        var cCount = this.getColModel().getColumnCount();
        if(this.getSelModel().isSelected(0))
            this.selectflag=1;
        if(this.selectflag==1){
            for(var i=1;i<sel.length;i++){
                this.clearRecTempCellStyle(sel[i]);
                var row = this.getGrid().getStore().indexOf(sel[i]);
                this.selectArray[i]=row;
                for(var col=this.sCol; col < cCount-this.eCol; col++){
                    this.addStyleToRecCell(property, value, row, col);
                }
            this.selectflag=0;
            }
        }else{
        this.selectflag=0;
        for(var i=0;i<sel.length;i++){
            this.clearRecTempCellStyle(sel[i]);
            var row = this.getGrid().getStore().indexOf(sel[i]);
            this.selectArray[i]=row;
            for(var col=this.sCol; col < cCount-this.eCol; col++){
                this.addStyleToRecCell(property, value, row, col);
            }
        }
        }
        Wtf.saveStyleInDB(sel,undefined,undefined,this.keyid,this.moduleName,this.parentGridObj);
        this.getSelModel().clearSelections();
    },


    //==============================================================================================================================//

    getReportMenu : function(reports){
        var report;
        var reportArr = [];
        var temp;
        for(var i=0;i<reports.length;i++){
            report=reports[i];
            temp = new Wtf.Action({
                text: report.link,
                tooltip:{
                    text:report.tooltip
                    },
                iconCls:getTabIconCls(Wtf.etype.reportsMenuIcon),
                handler: function(e) {
                    this.openReportTab(parseInt(e.initialConfig.reportid));
                },
                scope: this,
                initialConfig : {
                  reportid : report.id
                }
            });
            reportArr.push(temp);
        }
        if(reportArr.length != 0){
            var tbar=this.getTopToolbar();
            var btnPos = tbar.items.length;
            if(tbar.items.items[tbar.items.length-1].iconCls=="helpButton")
                btnPos = tbar.items.length -1;
            else {
                tbar.add('->');
                btnPos++;
            }
            tbar.insertButton(btnPos,new Wtf.Toolbar.Button({
                iconCls:getTabIconCls(Wtf.etype.reports),
                tooltip: {text:WtfGlobal.getLocaleText({key:"crm.REPORTSBTN.ttip",params:[this.moduleName]})},// "Click to view reports related to "+this.moduleName+"."},
                scope: this,
                text:WtfGlobal.getLocaleText("crm.dashboard.reports.title"),//"Reports",
                menu: reportArr
            }));
            tbar.insertButton(tbar.items.length-1,new Wtf.Toolbar.Separator());
        }
    },

    openReportTab :  function(name){
        var mainTab="";
        if(this.parentGridObj.subTab && this.parentGridObj.submainTab != undefined){
            mainTab=this.parentGridObj.submainTab;
        }else{
            mainTab=this.parentGridObj.mainTab;
        }
        var val=Object;
        val=getPaneldetails(name,mainTab.id,this.parentGridObj.initialConfig.Rrelatedto,this.parentGridObj.initialConfig.relatedtonameid);
        var title = Wtf.util.Format.ellipsis(val.title,18);
        var panel=Wtf.getCmp(val.id);


        if(panel==null) {
            panel= new Wtf.Panel({
                title:"<span wtf:qtip=\'"+val.tooltip+"\'>"+title+"</span>",
                id:val.id,
                layout:'fit',
                border:false,
                closable:true,
                iconCls:getTabIconCls(Wtf.etype.reports),
                items:new Wtf.AllReportTab({
                    scope:this,
                    head:val.head,
                    layout:'fit',
                    id:val.InnerID,
                    border:false,
                    stageflag:val.stageflag,
                    sourceflag:val.sourceflag,
                    details:val

                })
            });
            mainTab.add(panel);
        }
        mainTab.setActiveTab(panel);
        mainTab.doLayout();
    },
    getMySortconfig : function(EditorStore){
       if(Wtf.ServerSideSort && GlobalSortModel[this.moduleName]){
            EditorStore.sortInfo = GlobalSortModel[this.moduleName];
            delete EditorStore.baseParams['direction'];
            delete EditorStore.baseParams['field'];
            delete EditorStore.baseParams['xtype'];
            delete EditorStore.baseParams['xfield'];
            delete EditorStore.baseParams['iscustomcolumn'];
            EditorStore.baseParams = Wtf.apply(EditorStore.sortInfo || {}, EditorStore.baseParams);
       }
       return EditorStore;
    },
    getMyConfig : function(iscustomHeader){
        //ResponseAlert(500);
        var module = this.moduleName;

        if(this.archivedParentName!=undefined){
            module=this.archivedParentName
        }else{
            if(module.endsWith("Contact")){
                module="Contact";
            }
        }
        if(GlobalSpreadSheetConfig[module]) {
            new Wtf.util.DelayedTask(function() {
                try{
                    var res = GlobalSpreadSheetConfig[module];
                    var data = res.data[0];
                    var header = res.Header;
                    this.setMyConfig(data.state.columns,header,data.state.sort);
                    this.updateStylesheet(data.rules.rules);
                    if(res.data[1] && !iscustomHeader)
                        this.getReportMenu(res.data[1]);
                    this.moduleHeader = header;
                } catch (e){
                    clog(e);
                }
            },this).delay(10);
        } else {
        Wtf.Ajax.requestEx({
//            url: Wtf.req.base + 'spreadSheet/spreadsheet.jsp',
            url: "Common/Spreadsheet/getSpreadsheetConfig.do",
            params:{
                action : 5,
                module : module
            }
        },
        this,
        function(res) {
            if(!GlobalSpreadSheetConfig[module]) {
                GlobalSpreadSheetConfig[module] = res;
                getQuickSearchEmptyText(res.Header,module);
            }
            var data = res.data[0];
            var header = res.Header;
            this.setMyConfig(data, true, true, true,header);
            if(res.data[1] && !iscustomHeader)
                this.getReportMenu(res.data[1]);
            this.moduleHeader = header;
            this.afterRefreshView();
        },
        function(res) {

        });
        }
    },

    afterRefreshView : function () {
        var store = this.SpreadSheetGrid.getStore();
        var rec = searchRecord(store, "0", this.keyid);
        store.remove(rec);
        this.parentGridObj.addNewRec();
        Wtf.arrangeGridNumbererAdd(0,this.getGrid());
    },

    setMyConfig : function(data, applyRule, applyState, refresh,header){
        this.ssDBid = data.cid;
        if(applyRule){
            this.rules = data.rules.rules;
            this.getGrid().view.rules = this.rules;
        }
        if(applyState){
            if(data.state){
                if(data.state.columns != false){
                    this.getGrid().applyState(data.state);
                    this.getGrid().reconfigure(this.getGrid().getStore(),this.getGrid().getColumnModel());
                    this.getGrid().getView().updateAllColumnWidths();
                    if(Wtf.ServerSideSort && data.state.sort && data.state.sort.xfield){
                        GlobalSortModel[this.moduleName]=data.state.sort;
                    }
                }
                if(header != undefined){
                   this.getGrid().applyCustomHeader(header);
                }
            }
        }
        
        WtfGlobal.setEmptyTextForQuickSearchField(this.quickSearchTF,GlobalQuickSearchEmptyText[this.moduleName]);

        if(refresh){
            this.refreshMyView(applyState);
        }
    },

    saveMyStateHandler : function(grid, state){

        for(var i = 0; i < state.columns.length; i++){
            if(state.columns[i].id=='checker'){
                state.columns[i].width=18;
            }
        }
        if(GlobalSortModel[this.moduleName])
            state.sort = GlobalSortModel[this.moduleName];
        var module = this.moduleName;
        Wtf.Ajax.requestEx({
            url: "Common/Spreadsheet/saveSpreadsheetConfig.do",
            params:{
                action : 7,
                cid : this.ssDBid,
                module : module,
                state : Wtf.encode(state)
            }
        },
        this,
        function(res) {
            var data;
            delete GlobalSpreadSheetConfig[this.moduleName];
            delete GlobalQuickSearchEmptyText[this.moduleName];
            if(res.data!=undefined){
                data = res.data[0];
                this.setMyConfig(data, false, false, false,"");
            }
        },
        function(res) {
        });

    },

    saveMyRuleHandler : function(ruleWin, rules, delFlag){
//        if(rules.length>0){
            ResponseAlert(500);
//        }
        var ruleo = {
            rules:rules
        };
        var module = this.moduleName;
        Wtf.Ajax.requestEx({
            url: "Common/Spreadsheet/saveSpreadsheetConfig.do",
            params:{
                action : 6,
                cid : this.ssDBid,
                module : module,
                rules : (rules==""?rules:Wtf.encode(ruleo))
            }
        },
        this,
        function(res) {
            delete GlobalSpreadSheetConfig[this.moduleName];
            delete GlobalQuickSearchEmptyText[this.moduleName];
            var data = res.data[0];
            this.setMyConfig(data, true, false, true,"");
        },
        function(res) {

        });

    },

    //==============================================================================================================================//

    refreshMyView : function(headers){
  //      ResponseAlert(501);
        this.getSelModel().selType="None";
        this.setSelType();
        var view = this.getGrid().view;
        view.refresh(headers);
    }, selectBack : function(){
        if(this.selectType=="Cell"){
            this.getSelModel().select(this.selectArray[0],this.selectArray[1]);
        }else if(this.selectType=="Row"){
            this.getGrid().getSelectionModel().selectRows(this.selectArray);
        }
    }
});

