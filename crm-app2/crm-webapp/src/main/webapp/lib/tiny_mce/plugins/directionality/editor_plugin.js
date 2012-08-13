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
(function(){tinymce.create('tinymce.plugins.Directionality',{init:function(ed,url){var t=this;t.editor=ed;ed.addCommand('mceDirectionLTR',function(){var e=ed.dom.getParent(ed.selection.getNode(),ed.dom.isBlock);if(e){if(ed.dom.getAttrib(e,"dir")!="ltr")ed.dom.setAttrib(e,"dir","ltr");else ed.dom.setAttrib(e,"dir","");}ed.nodeChanged();});ed.addCommand('mceDirectionRTL',function(){var e=ed.dom.getParent(ed.selection.getNode(),ed.dom.isBlock);if(e){if(ed.dom.getAttrib(e,"dir")!="rtl")ed.dom.setAttrib(e,"dir","rtl");else ed.dom.setAttrib(e,"dir","");}ed.nodeChanged();});ed.addButton('ltr',{title:'directionality.ltr_desc',cmd:'mceDirectionLTR'});ed.addButton('rtl',{title:'directionality.rtl_desc',cmd:'mceDirectionRTL'});ed.onNodeChange.add(t._nodeChange,t);},getInfo:function(){return{longname:'Directionality',author:'Moxiecode Systems AB',authorurl:'http://tinymce.moxiecode.com',infourl:'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/directionality',version:tinymce.majorVersion+"."+tinymce.minorVersion};},_nodeChange:function(ed,cm,n){var dom=ed.dom,dir;n=dom.getParent(n,dom.isBlock);if(!n){cm.setDisabled('ltr',1);cm.setDisabled('rtl',1);return;}dir=dom.getAttrib(n,'dir');cm.setActive('ltr',dir=="ltr");cm.setDisabled('ltr',0);cm.setActive('rtl',dir=="rtl");cm.setDisabled('rtl',0);}});tinymce.PluginManager.add('directionality',tinymce.plugins.Directionality);})();
