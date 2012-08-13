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
Wtf.namespace("Wtf.ux.grid.plugins");

Wtf.ux.grid.plugins.GroupCheckboxSelection = {

	init: function(grid){

		grid.view.groupTextTpl =	
			'<input type="checkbox" ' +
			'class="x-grid-group-checkbox" x-grid-group-hd-text="{text}" /> ' +
			grid.view.groupTextTpl;
	
		grid.on('render', function() {
			Wtf.ux.grid.plugins.GroupCheckboxSelection.initBehaviors(grid);
		});
	
		grid.view.on('refresh', function() {
			Wtf.ux.grid.plugins.GroupCheckboxSelection.initBehaviors(grid);
		});
	},
	
	initBehaviors: function(grid) {
		var id = "#" + grid.id;
		var behaviors = {};

		// Check/Uncheck all items in group
		behaviors[id + ' .x-grid-group-hd .x-grid-group-checkbox@click'] =
			function(e, target){

				var ds = grid.getStore();
				var sm = grid.getSelectionModel();
				var cm = grid.getColumnModel();
				
				var text = target.getAttribute("x-grid-group-hd-text");
				var parts = text.split(":")
				
				var value = parts[1].trim();
				var header = parts[0].trim();
				var field = cm.getColumnsBy(function(columnConfig, index){
					return (columnConfig.header == header);
				})[0].dataIndex;
				
				var records = ds.query(field, value).items;
				
				for(var i = 0, len = records.length; i < len; i++){
					var row = ds.indexOf(records[i]);
					if (target.checked) {
						sm.selectRow(row, true);
					}
					else {
						sm.deselectRow(row);
					}
				}
			};

		// Avoid group expand/collapse clicking on checkbox
		behaviors[id + ' .x-grid-group-hd .x-grid-group-checkbox@mousedown'] =
			function(e, target){
				e.stopPropagation();
			};
		
		Wtf.addBehaviors(behaviors);
	}

}

