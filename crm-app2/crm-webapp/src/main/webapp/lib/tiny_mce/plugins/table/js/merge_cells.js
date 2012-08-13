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

function init() {
	var f = document.forms[0], v;

	tinyMCEPopup.resizeToInnerSize();

	f.numcols.value = tinyMCEPopup.getWindowArg('numcols', 1);
	f.numrows.value = tinyMCEPopup.getWindowArg('numrows', 1);
}

function mergeCells() {
	var args = [], f = document.forms[0];

	tinyMCEPopup.restoreSelection();

	if (!AutoValidator.validate(f)) {
		alert(tinyMCEPopup.getLang('invalid_data'));
		return false;
	}

	args["numcols"] = f.numcols.value;
	args["numrows"] = f.numrows.value;

	tinyMCEPopup.execCommand("mceTableMergeCells", false, args);
	tinyMCEPopup.close();
}

tinyMCEPopup.onInit.add(init);
