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
	tinymce.create('tinymce.plugins.IESpell', {
		init : function(ed, url) {
			var t = this, sp;

			if (!tinymce.isIE)
				return;

			t.editor = ed;

			// Register commands
			ed.addCommand('mceIESpell', function() {
				try {
					sp = new ActiveXObject("ieSpell.ieSpellExtension");
					sp.CheckDocumentNode(ed.getDoc().documentElement);
				} catch (e) {
					if (e.number == -2146827859) {
						ed.windowManager.confirm(ed.getLang("iespell.download"), function(s) {
							if (s)
								window.open('http://www.iespell.com/download.php', 'ieSpellDownload', '');
						});
					} else
						ed.windowManager.alert("Error Loading ieSpell: Exception " + e.number);
				}
			});

			// Register buttons
			ed.addButton('iespell', {title : 'iespell.iespell_desc', cmd : 'mceIESpell'});
		},

		getInfo : function() {
			return {
				longname : 'IESpell (IE Only)',
				author : 'Moxiecode Systems AB',
				authorurl : 'http://tinymce.moxiecode.com',
				infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/iespell',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		}
	});

	// Register plugin
	tinymce.PluginManager.add('iespell', tinymce.plugins.IESpell);
})();
