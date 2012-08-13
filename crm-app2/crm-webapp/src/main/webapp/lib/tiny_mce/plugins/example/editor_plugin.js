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
(function(){tinymce.PluginManager.requireLangPack('example');tinymce.create('tinymce.plugins.ExamplePlugin',{init:function(ed,url){ed.addCommand('mceExample',function(){ed.windowManager.open({file:url+'/dialog.htm',width:320+parseInt(ed.getLang('example.delta_width',0)),height:120+parseInt(ed.getLang('example.delta_height',0)),inline:1},{plugin_url:url,some_custom_arg:'custom arg'});});ed.addButton('example',{title:'example.desc',cmd:'mceExample',image:url+'/img/example.gif'});ed.onNodeChange.add(function(ed,cm,n){cm.setActive('example',n.nodeName=='IMG');});},createControl:function(n,cm){return null;},getInfo:function(){return{longname:'Example plugin',author:'Some author',authorurl:'http://tinymce.moxiecode.com',infourl:'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/example',version:"1.0"};}});tinymce.PluginManager.add('example',tinymce.plugins.ExamplePlugin);})();
