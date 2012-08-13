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
Wtf.FileBrowseButton = Wtf.extend(Wtf.Button,{
	input_name : 'file',
	input_file : null,
	original_handler : null,
	original_scope : null,
	
	initComponent : function() {
		Wtf.FileBrowseButton.superclass.initComponent.call(this);
		this.original_handler = this.handler || null;
		this.original_scope = this.scope || window;
		this.handler = null;
		this.scope = null;
	},

	onRender : function(ct, position) {
		Wtf.FileBrowseButton.superclass.onRender.call(this, ct, position);
		this.createInputFile();
	},

	createInputFile : function() {
		var button_container = this.el.child('.x-btn-center');
		button_container.position('relative');
		this.input_file = Wtf.DomHelper.append(button_container,{
			tag : 'input',
			type : 'file',
			size : 1,
			name : this.input_name|| Wtf.id(this.el),
			style : 'position: absolute; display: block; border: none; cursor: pointer'
		}, true);

		this.input_file.setOpacity(0.0);
		this.adjustInputFileBox();

		if (this.handleMouseEvents) {
			this.input_file.on('mouseover', this.onMouseOver, this);
			this.input_file.on('mousedown', this.onMouseDown, this);
		}

		if (this.tooltip) {
			if (typeof this.tooltip == 'object') {
				Wtf.QuickTips.register(Wtf.apply( {
					target : this.input_file
				}, this.tooltip));
			} else {
				this.input_file.dom[this.tooltipType] = this.tooltip;
			}
		}

		this.input_file.on('change', this.onInputFileChange, this);
		this.input_file.on('click', function(e) { e.stopPropagation();});
	},

	autoWidth : function() {
		Wtf.FileBrowseButton.superclass.autoWidth.call(this);
		this.adjustInputFileBox();
	},

	adjustInputFileBox : function() {
		var btn_cont, btn_box, inp_box, adj;

		if (this.el && this.input_file) {
			btn_cont = this.el.child('.x-btn-center');
			btn_box = btn_cont.getBox();
			this.input_file.setStyle('font-size',(btn_box.width * 0.5) + 'px');
			inp_box = this.input_file.getBox();
			adj = {	x : 3, y : 3};
			if (Wtf.isIE) {
				adj = {	x : -84,	y : 3 };//to adjust style for following function setLeft() in all ie
			}
			this.input_file.setLeft(btn_box.width - inp_box.width + adj.x + 'px');
			this.input_file.setTop(btn_box.height - inp_box.height + adj.y + 'px');
		}
	},

	detachInputFile : function(no_create) {
		var result = this.input_file;

		no_create = no_create || false;

		if (typeof this.tooltip == 'object') {
			Ext.QuickTips.unregister(this.input_file);
		} else {
			this.input_file.dom[this.tooltipType] = null;
		}
		this.input_file.removeAllListeners();
		this.input_file = null;

		if (!no_create) {
			this.createInputFile();
		}
		return result;
	},
	
	getInputFile : function() {
		return this.input_file;
	},

	disable : function() {
		Wtf.FileBrowseButton.superclass.disable.call(this);
		this.input_file.dom.disabled = true;
	},

	enable : function() {
		Wtf.FileBrowseButton.superclass.enable.call(this);
		this.input_file.dom.disabled = false;
	},

	destroy : function() {
		var input_file = this.detachInputFile(true);
		input_file.remove();
		input_file = null;
		Wtf.FileBrowseButton.superclass.destroy.call(this);
	},

	onInputFileChange : function() {
		if (this.original_handler) {
			this.original_handler.call(this.original_scope,	this);
		}
	}
});


Wtf.MultiFlieUploadPanel = Wtf.extend(Wtf.Panel, {
	autoUpload:false,
	initComponent:function(){
		this.uploading = false;
		this.initialQueuedCount = 0;
		Wtf.MultiFlieUploadPanel.superclass.initComponent.call(this);
		this.addEvents({
			'beforefileadd':true,
			'fileadd':true,
			'beforefileremove':true,
			'fileremove':true
		});
		
		this.fileRecord=Wtf.data.Record.create([
           {name: 'filename'},
           {name: 'state', type: 'int'},
           {name: 'note'},
           {name: 'input_element'},
           {name: 'params'}
        ]);

	    var store = new Wtf.data.Store({
	        proxy: new Wtf.data.MemoryProxy([]),
	        reader: new Wtf.data.JsonReader({}, this.fileRecord),
	        pruneModifiedRecords: true
	    });
	      
	    var cm = new Wtf.grid.ColumnModel( [ {
			header : 'File Name',
			dataIndex : 'filename'
		},{
			header : 'Status',
			dataIndex : 'state',
			width:25,
			renderer : function(val) {
				var str="images/s.gif";
				switch(val){
					case 1: str = "images/inbox.png"; break;
					case 2: str = "images/loading.gif"; break;
					case 3: str = "images/check16.png"; break;
					case 4: str = "images/exclamation.gif"; break;
					case 5: str = "images/Cancel.gif"; break;
				}
	        	return "<img src='"+str+"' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Upload image'></img>";
	    	}
		},{
			header : 'Note',
			dataIndex : 'note'
		},{
			header : 'Remove',
			width:25,
			dataIndex : 'delfield',
			renderer : function() {
            	return "<img class='delete' src='images/Delete.png' style=\"margin-left:5px;vertical-align:middle;height : 13px\" title='Delete this entry'></img>";
        	}
		}]);
	    
        this.grid_panel = new Wtf.grid.GridPanel( {
			ds : store,
			cm : cm,
			border : false,
			viewConfig : {
				autoFill : true,
				forceFit : true
			}
		});

        this.grid_panel.on('render', this.attachDrop, this);
        this.grid_panel.on('cellclick', function(g, r, c, e){
        	if(e.getTarget('.delete')){
        		this.removeFile(g.getStore().getAt(r));
        	}
        }, this);
		this.add(this.grid_panel);
	},

	attachDrop:function(){
	    var dropEl = this.grid_panel.getEl();
        dropEl.on('dragover', function(e) {
            e.stopPropagation();
            e.preventDefault();
            var dt = e.browserEvent.dataTransfer;               
            dt.effectAllowed = 'copy';
 
            if (dt.effectAllowed.match(/all|copy/i)) {
                dt.dropEffect = 'copy';
            }
        }, this);
        
        dropEl.on('drop', function(e) {
            e.stopPropagation();
            e.preventDefault();

            //TODO prform Click handling as if browse button clicked 
            
            var dt = e.browserEvent.dataTransfer;
            var files = dt.files;
            
            //TODO add files from the filelist to the queue 
        }, this);
	},

	onRender : function(ct, position) {
		Wtf.MultiFlieUploadPanel.superclass.onRender.call(this, ct,	position);
	    this.form = Wtf.DomHelper.append(this.body, {
	        tag: 'form',
	        method: 'post',
	        action: this.url,
	        style: 'position: absolute; left: -100px; top: -100px; width: 100px; height: 100px'
	      });

	},

	addFileToUploadQueue : function(btn) {
		var input_file = btn.detachInputFile();

		input_file.appendTo(this.form);
		input_file.setStyle('width', '100px');
		input_file.dom.disabled = true;

		var store = this.grid_panel.getStore();
		store.add(new this.fileRecord( {
			state :1,
			filename : input_file.dom.value,
			note : 'Queued to upload',
			input_element : input_file
		}));
	},
	addFiles : function(btn) {
		if (this.fireEvent('beforefileadd', this, btn.getInputFile().dom.value) !== false) {
			this.addFileToUploadQueue(btn);
			this.fireEvent('fileadd', this, btn.getInputFile().dom.value);
			if(this.uploading===true){
				this.initialQueuedCount++;
			}else if(this.autoUpload){
				this.startUpload();
			}
		}
	},
	
	removeFile : function(record) {
		if (record&&this.fireEvent('beforefileremove', this, record) !== false) {
			record.get('input_element').remove();
			this.grid_panel.getStore().remove(record);
			this.fireEvent('fileremove', this, record);
		}
	},
	
	prepareNextUploadTask : function() {
		var store = this.grid_panel.getStore();
		var record = null;

		store.each(function(r) {
			if (!record && r.get('state') == 1) {
				record = r;
			} else {
				r.get('input_element').dom.disabled = true;
			}
		});

		record.get('input_element').dom.disabled = false;
		record.set('state', 2);
		record.set('note', "Processing");
		record.commit();
		this.uploadFile(record);
	},

	getQueuedCount : function(includeProcessing) {
		var count = 0;
		this.grid_panel.getStore().each(function(r) {
			if (r.get('state') == 1||(includeProcessing && r.get('state') == 2)) {
				count++;
			}
		});
		return count;
	},
  
	startUpload:function(){
		if(this.getQueuedCount() > 0){
			this.uploading = true;
			this.initialQueuedCount = this.getQueuedCount();
			this.prepareNextUploadTask();
		}else{
			WtfComMsgBox(["Alert","Please add/ select image(s) first"]);
		}
	},
	
	cancelUpload:function(){
		var store = this.grid_panel.getStore();
		store.each(function(r) {
			if (r.get('state') == 1) {
				this.updateRecordState(r, 5, "Upload Cancelled");
			}
		}, this);
	},
	
	clearAll : function() {
		var store = this.grid_panel.getStore();
		store.each(function(r) {
			r.get('input_element').remove();
		}, this);
		store.removeAll();
	},
	
	uploadFile:function(record){
	    Wtf.Ajax.request({
	        url : this.url,
	        params : Wtf.applyIf(record.get('params') || {}, this.baseParams || this.params),
	        method : 'POST',
	        form : this.form,
	        isUpload : true,
	        success : this.onAjaxSuccess,
	        failure : this.onAjaxFailure,
	        scope : this,
	        record:record
	    });
	},
	
	updateRecordState:function(record, state, message){
        record.set('state', state);
        record.set('note', message);
		record.commit();
	},
	
	onAjaxSuccess:function(resp, data){
		result = eval('('+resp.responseText+')');
		var state = 4, msg;
	    if (result.data.success==true) {
	    	state = 3;
	    	msg = "Uploded Successfully";
	    }else{
	    	msg=result.data.msg||"Upload Failed";
	    }
	    this.updateRecordState(data.record, state, msg);
		if(this.getQueuedCount() > 0){
			this.prepareNextUploadTask();
		}else{
			this.uploading = false;
		}
	},
	
	onAjaxFailure:function(resp, data){
		this.updateRecordState(data.record, 4, "Communication Failed");
		if(this.getQueuedCount() > 0){
			this.prepareNextUploadTask();
		}else{
			this.uploading = false;
		}		
	}
});
