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
Wtf.reportBuilder.queryField = function(conf){
    Wtf.apply(this, conf);
    Wtf.reportBuilder.queryField.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.reportBuilder.queryField, Wtf.Panel, {
    initComponent: function(conf){
        Wtf.reportBuilder.queryField.superclass.initComponent.call(this, conf);
        this.tableCount = 0;
        this.tables = {};
    },
    onRender: function(conf){
        Wtf.reportBuilder.queryField.superclass.onRender.call(this, conf);
        if(this.header)
            this.el.dom.removeChild(this.header.dom);
        var contDiv = document.createElement("div");
        this.selectDiv = document.createElement("div");
        this.fromDiv = document.createElement("div");
        this.whereDiv = document.createElement("div");
        this.whereDiv.style.display = 'none';
        this.selectDiv.innerHTML = "<span style='color: blue'>SELECT</span> ";
        this.fromDiv.innerHTML = "<span style='color: blue'>FROM</span> ";
        contDiv.className = "queryDiv";
        contDiv.appendChild(this.selectDiv);
        contDiv.appendChild(this.fromDiv);
        contDiv.appendChild(this.whereDiv);
        this.el.dom.appendChild(contDiv);
    },

    addTable: function(rec){
        var tablename = rec.data["name"];
        var appendText = " " + tablename;
        if(this.tables[tablename] === undefined){
            if(this.tableCount != 0)
                appendText = " <span style='color: blue'>INNER JOIN </span> " + tablename;
            this.tables[tablename] = rec;
            this.tableCount++;
            this.fromDiv.innerHTML += appendText;
        }
    }
});
