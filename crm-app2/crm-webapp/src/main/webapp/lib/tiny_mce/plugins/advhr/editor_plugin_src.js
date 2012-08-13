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
	tinymce.create('tinymce.plugins.AdvancedHRPlugin', {
		init : function(ed, url) {
			// Register commands
			ed.addCommand('mceAdvancedHr', function() {
				ed.windowManager.open({
					file : url + '/rule.htm',
					width : 250 + parseInt(ed.getLang('advhr.delta_width', 0)),
					height : 160 + parseInt(ed.getLang('advhr.delta_height', 0)),
					inline : 1
				}, {
					plugin_url : url
				});
			});

			// Register buttons
			ed.addButton('advhr', {
				title : 'advhr.advhr_desc',
				cmd : 'mceAdvancedHr'
			});

			ed.onNodeChange.add(function(ed, cm, n) {
				cm.setActive('advhr', n.nodeName == 'HR');
			});

			ed.onClick.add(function(ed, e) {
				e = e.target;

				if (e.nodeName === 'HR')
					ed.selection.select(e);
			});
		},

		getInfo : function() {
			return {
				longname : 'Advanced HR',
				author : 'Moxiecode Systems AB',
				authorurl : 'http://tinymce.moxiecode.com',
				infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/advhr',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		}
	});

	// Register plugin
	tinymce.PluginManager.add('advhr', tinymce.plugins.AdvancedHRPlugin);
})();
