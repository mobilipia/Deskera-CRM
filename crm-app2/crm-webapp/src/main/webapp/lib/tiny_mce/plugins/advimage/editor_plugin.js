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
(function(){tinymce.create('tinymce.plugins.AdvancedImagePlugin',{init:function(ed,url){ed.addCommand('mceAdvImage',function(){if(ed.dom.getAttrib(ed.selection.getNode(),'class').indexOf('mceItem')!=-1)return;ed.windowManager.open({file:url+'/image.htm',width:480+parseInt(ed.getLang('advimage.delta_width',0)),height:385+parseInt(ed.getLang('advimage.delta_height',0)),inline:1},{plugin_url:url});});ed.addButton('image',{title:'advimage.image_desc',cmd:'mceAdvImage'});},getInfo:function(){return{longname:'Advanced image',author:'Moxiecode Systems AB',authorurl:'http://tinymce.moxiecode.com',infourl:'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/advimage',version:tinymce.majorVersion+"."+tinymce.minorVersion};}});tinymce.PluginManager.add('advimage',tinymce.plugins.AdvancedImagePlugin);})();
