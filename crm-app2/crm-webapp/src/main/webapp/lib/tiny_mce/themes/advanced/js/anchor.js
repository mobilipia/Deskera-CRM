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
tinyMCEPopup.requireLangPack();

var AnchorDialog = {
	init : function(ed) {
		var action, elm, f = document.forms[0];

		this.editor = ed;
		elm = ed.dom.getParent(ed.selection.getNode(), 'A,IMG');
		v = ed.dom.getAttrib(elm, 'name');

		if (v) {
			this.action = 'update';
			f.anchorName.value = v;
		}

		f.insert.value = ed.getLang(elm ? 'update' : 'insert');
	},

	update : function() {
		var ed = this.editor;
		
		tinyMCEPopup.restoreSelection();

		if (this.action != 'update')
			ed.selection.collapse(1);

		// Webkit acts weird if empty inline element is inserted so we need to use a image instead
		if (tinymce.isWebKit)
			ed.execCommand('mceInsertContent', 0, ed.dom.createHTML('img', {mce_name : 'a', name : document.forms[0].anchorName.value, 'class' : 'mceItemAnchor'}));
		else
			ed.execCommand('mceInsertContent', 0, ed.dom.createHTML('a', {name : document.forms[0].anchorName.value, 'class' : 'mceItemAnchor'}, ''));

		tinyMCEPopup.close();
	}
};

tinyMCEPopup.onInit.add(AnchorDialog.init, AnchorDialog);
