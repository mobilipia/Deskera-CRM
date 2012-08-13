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

var ExampleDialog = {
	init : function() {
		var f = document.forms[0];

		// Get the selected contents as text and place it in the input
		f.someval.value = tinyMCEPopup.editor.selection.getContent({format : 'text'});
		f.somearg.value = tinyMCEPopup.getWindowArg('some_custom_arg');
	},

	insert : function() {
		// Insert the contents from the input into the document
		tinyMCEPopup.editor.execCommand('mceInsertContent', false, document.forms[0].someval.value);
		tinyMCEPopup.close();
	}
};

tinyMCEPopup.onInit.add(ExampleDialog.init, ExampleDialog);
