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
(function(){tinymce.create('tinymce.plugins.AutoSavePlugin',{init:function(ed,url){var t=this;t.editor=ed;window.onbeforeunload=tinymce.plugins.AutoSavePlugin._beforeUnloadHandler;},getInfo:function(){return{longname:'Auto save',author:'Moxiecode Systems AB',authorurl:'http://tinymce.moxiecode.com',infourl:'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/autosave',version:tinymce.majorVersion+"."+tinymce.minorVersion};},'static':{_beforeUnloadHandler:function(){var msg;tinymce.each(tinyMCE.editors,function(ed){if(ed.getParam("fullscreen_is_enabled"))return;if(ed.isDirty()){msg=ed.getLang("autosave.unload_msg");return false;}});return msg;}}});tinymce.PluginManager.add('autosave',tinymce.plugins.AutoSavePlugin);})();
