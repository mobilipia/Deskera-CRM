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

(function() {
	var Event = tinymce.dom.Event;

	tinymce.create('tinymce.plugins.NonEditablePlugin', {
		init : function(ed, url) {
			var t = this, editClass, nonEditClass;

			t.editor = ed;
			editClass = ed.getParam("noneditable_editable_class", "mceEditable");
			nonEditClass = ed.getParam("noneditable_noneditable_class", "mceNonEditable");

			ed.onNodeChange.addToTop(function(ed, cm, n) {
				var sc, ec;

				// Block if start or end is inside a non editable element
				sc = ed.dom.getParent(ed.selection.getStart(), function(n) {
					return ed.dom.hasClass(n, nonEditClass);
				});

				ec = ed.dom.getParent(ed.selection.getEnd(), function(n) {
					return ed.dom.hasClass(n, nonEditClass);
				});

				// Block or unblock
				if (sc || ec) {
					t._setDisabled(1);
					return false;
				} else
					t._setDisabled(0);
			});
		},

		getInfo : function() {
			return {
				longname : 'Non editable elements',
				author : 'Moxiecode Systems AB',
				authorurl : 'http://tinymce.moxiecode.com',
				infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/noneditable',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		},

		_block : function(ed, e) {
			var k = e.keyCode;

			// Don't block arrow keys, pg up/down, and F1-F12
			if ((k > 32 && k < 41) || (k > 111 && k < 124))
				return;

			return Event.cancel(e);
		},

		_setDisabled : function(s) {
			var t = this, ed = t.editor;

			tinymce.each(ed.controlManager.controls, function(c) {
				c.setDisabled(s);
			});

			if (s !== t.disabled) {
				if (s) {
					ed.onKeyDown.addToTop(t._block);
					ed.onKeyPress.addToTop(t._block);
					ed.onKeyUp.addToTop(t._block);
					ed.onPaste.addToTop(t._block);
				} else {
					ed.onKeyDown.remove(t._block);
					ed.onKeyPress.remove(t._block);
					ed.onKeyUp.remove(t._block);
					ed.onPaste.remove(t._block);
				}

				t.disabled = s;
			}
		}
	});

	// Register plugin
	tinymce.PluginManager.add('noneditable', tinymce.plugins.NonEditablePlugin);
})();
