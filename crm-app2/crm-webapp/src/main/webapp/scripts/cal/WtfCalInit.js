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
Wtf.namespace('Wtf.cal');
Wtf.KWLCalControl = function(config) {
    Wtf.apply(this, config);
    Wtf.KWLCalControl.superclass.constructor.call(this);
}

Wtf.extend(Wtf.KWLCalControl,Wtf.Panel, {
    layout:'border',
    title: WtfGlobal.getLocaleText("crm.dashboard.calendar"),//"Calendar",
    closable:true,
    autoDestroy: true,
    iconCls: "dpwnd teamcal",
    initComponent:function() {
        Wtf.KWLCalControl.superclass.initComponent.call(this);
        if(this.initbind <= 1)
            dojo.cometd.init("../../bind");
        
        this.calcont=new Wtf.Panel({
            id:"cal-tree-container",
            region:'center',
            autoScroll:'true',
            cls:'calTreeContainer',
            border:false
        });
        this.dtcont=new Wtf.Panel({
            id:"dt-picker-container",
            region:'north',
            height:200,
            border:false
        });
        this.add(this.calendarCtrl = new Wtf.cal.control({
                id: this.parentId + 'calctrl',
                region:'center',
                border: false,
                myToolbar: true,
                closable: true,
                archived: false,
                calTabId: this.parentId + 'tabmycal',
                layout: "fit",
                ownerid: this.uid,
                url: "jspfiles/caltree.jsp",
                calcont:this.calcont,
                dtcont:this.dtcont
            }),{
                region:'west',
                width:190,
                cls:'westcalContainer',
                bodyStyle:'background:#fff;',
                layout:'border',
                border:'false',
                items:[this.calcont,this.dtcont]
        });

        this.on("hide", function() {
            var expandWin = Wtf.getCmp('Expand');
            if(expandWin !== undefined)
                    expandWin.close();
        }, this);
        this.on("destroy", this.destroyCal, this);
    },

    destroyCal: function(obj) {
        var expandWin = Wtf.getCmp('Expand');
        if(expandWin !== undefined)
            expandWin.destroy();
        this.calendarCtrl.currentview = "DayView";
        if(this.calendarCtrl.calTree.defaultNode)
          this.calendarCtrl.stopCalEventBot(this.calendarCtrl.calTree.defaultNode.id);
        this.calendarCtrl.stopCalTreeBot();
    }
});
