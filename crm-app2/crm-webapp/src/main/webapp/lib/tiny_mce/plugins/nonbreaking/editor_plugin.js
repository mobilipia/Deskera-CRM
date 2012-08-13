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
(function(){tinymce.create('tinymce.plugins.Nonbreaking',{init:function(ed,url){var t=this;t.editor=ed;ed.addCommand('mceNonBreaking',function(){ed.execCommand('mceInsertContent',false,(ed.plugins.visualchars&&ed.plugins.visualchars.state)?'<span class="mceItemHidden mceVisualNbsp">&middot;</span>':'&nbsp;');});ed.addButton('nonbreaking',{title:'nonbreaking.nonbreaking_desc',cmd:'mceNonBreaking'});if(ed.getParam('nonbreaking_force_tab')){ed.onKeyDown.add(function(ed,e){if(tinymce.isIE&&e.keyCode==9){ed.execCommand('mceNonBreaking');ed.execCommand('mceNonBreaking');ed.execCommand('mceNonBreaking');tinymce.dom.Event.cancel(e);}});}},getInfo:function(){return{longname:'Nonbreaking space',author:'Moxiecode Systems AB',authorurl:'http://tinymce.moxiecode.com',infourl:'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/nonbreaking',version:tinymce.majorVersion+"."+tinymce.minorVersion};}});tinymce.PluginManager.add('nonbreaking',tinymce.plugins.Nonbreaking);})();
