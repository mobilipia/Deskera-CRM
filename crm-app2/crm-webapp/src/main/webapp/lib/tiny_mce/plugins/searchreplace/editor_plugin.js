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
(function(){tinymce.create('tinymce.plugins.SearchReplacePlugin',{init:function(ed,url){function open(m){ed.windowManager.open({file:url+'/searchreplace.htm',width:420+parseInt(ed.getLang('searchreplace.delta_width',0)),height:160+parseInt(ed.getLang('searchreplace.delta_height',0)),inline:1,auto_focus:0},{mode:m,search_string:ed.selection.getContent({format:'text'}),plugin_url:url});};ed.addCommand('mceSearch',function(){open('search');});ed.addCommand('mceReplace',function(){open('replace');});ed.addButton('search',{title:'searchreplace.search_desc',cmd:'mceSearch'});ed.addButton('replace',{title:'searchreplace.replace_desc',cmd:'mceReplace'});ed.addShortcut('ctrl+f','searchreplace.search_desc','mceSearch');},getInfo:function(){return{longname:'Search/Replace',author:'Moxiecode Systems AB',authorurl:'http://tinymce.moxiecode.com',infourl:'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/searchreplace',version:tinymce.majorVersion+"."+tinymce.minorVersion};}});tinymce.PluginManager.add('searchreplace',tinymce.plugins.SearchReplacePlugin);})();
