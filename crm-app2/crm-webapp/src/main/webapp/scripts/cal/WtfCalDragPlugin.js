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
var timeStr = null;
var activecell = '`';
var x = null;
var xy = null;
var y = null;
var counter = 0;
var activeDay = '`';
var dayStr = null;
var xM = null;
var xyM = null;
var xOffset = 0;
var yM = null;
var counterMonth = 0;

var activeAllDay = '`';
var ADStr = null;
var xAD = null;
var xyAD = null;
var xOffsetAD = 0;
var yAD = null;
var counterAD = 0;

Wtf.DataView.calDragSelector = function(cfg){
    cfg = cfg ||
    {};
    var view, regions, proxy, tracker;
    var rs, bodyRegion, dragRegion = new Wtf.lib.Region(0, 0, 0, 0);
    var dragSafe = cfg.dragSafe === true;

    this.init = function(dataView){
        view = dataView;
        view.on('render', onRender);
    };

    function fillRegions(){
        rs = [];
        view.all.each(function(el){
            rs[rs.length] = el.getRegion();
        });
        bodyRegion = view.el.getRegion();
    }

    function cancelClick(){
        return false;
    }

    function onBeforeStart(e){
        return !dragSafe || e.target == view.el.dom;
    }

    function onStart(e){
        view.on('containerclick', cancelClick, view, {
            single: true
        });
        if (!proxy)
            proxy = view.el.createChild({
                cls: 'x-view-selector'
            });
        else
            proxy.setDisplayed('block');
        fillRegions();
        view.clearSelections();
    }

    function onRender(view){
        if(cfg.calDrag==true && cfg.calAllDayWeek==false && cfg.calMonthView==false) {
            tracker = new Wtf.dd.DragTracker({
                onBeforeStart: onCalDragBeforeStart,
                onStart: onCalDragStart,
                onDrag: onCalDrag,
                onEnd: onCalDragEnd
            });
        } else if(cfg.calDrag==true && cfg.calAllDayWeek==true && cfg.calMonthView==false) {
            tracker = new Wtf.dd.DragTracker({
                onBeforeStart: onCalADDragBeforeStart,
                onStart: onCalADDragStart,
                onDrag: onCalADDrag,
                onEnd: onCalADDragEnd
            });
        } else if(cfg.calDrag==true && cfg.calAllDayWeek==false && cfg.calMonthView==true) {
            // Vertical drag to be implemented, as of now, drag end does provide proper end date, but it's proxy is not visible there
            tracker = new Wtf.dd.DragTracker({
                onBeforeStart: onCalMonthDragBeforeStart,
                onStart: onCalMonthDragStart,
                onDrag: onCalMonthDrag,
                onEnd: onCalMonthDragEnd
            });
        }
        tracker.initEl(view.el);
    }

    if (cfg.calDrag && !cfg.calAllDayWeek && !cfg.calMonthView) {
        MainPanel=cfg.calContainer;

        function onCalDrag(e){
/*            var arr = e.target.id.split('cell');
            arr = arr[0].split('View');
            var MainPanel = Wtf.get(arr[1]);
            //MainPanel=MainPanel.split('View')[1];
            //MainPanel = Wtf.getCmp(MainPanel);
            var MainPanel =Wtf.cal.utils.getCalCtrl(); 
            //alert(MainPanel.id);*/
            if (activecell.match('`')) {
                var cellone = e.getTarget("td");
                if(cellone){
                activecell = cellone.id;
                xy = Wtf.get(activecell).getXY();
                }
            }
            else {
                xy = tracker.getXY();
            }
            var arrtime = activecell.split("_");
            var startXY = tracker.startXY;
            var width = Wtf.get(activecell).dom.offsetWidth;
            if (x == null) {
                x = Math.min(startXY[0], xy[0]);
            }
            y = Math.min(startXY[1], xy[1]);
            var w = width;//Math.abs(startXY[0] - xy[0]);
            var h = Math.abs(startXY[1] - xy[1]);
            dragRegion.left = x;
            dragRegion.top = y;
            dragRegion.right = x + w;
            dragRegion.bottom = y + h;
            var actcell = Wtf.get(activecell);
            var starttime = parseInt(arrtime[1]) - 1;
            var paneheight = Wtf.get(MainPanel.id + MainPanel.currentview + 'eventPanel').dom.offsetHeight;
            //	var cellH=actcell.getHeight().split('%');
            //	var cellH = actcell.height.split('%');
            //	var h1 = parseInt(cellH[0]);
            var cellH = actcell.height;
            var h1 = parseInt(cellH);

            h1 = (h1 * paneheight) / 100;
            var endtime = (starttime + (h / (h1)) - 0.1).toPrecision("2");

            //timeStr = "";
            //var stArr = [];
            //stArr = Wtf.cal.utils.getHourStr(starttime + 1);
            //timeStr = stArr[0] + stArr[1];
            timeStr = starttime + 1
            
            dragRegion.constrainTo(bodyRegion);
//            dragRegion.constrainTo(MainPanel.id+MainPanel.currentview+'eventPanel');
            proxy.setRegion(dragRegion);
            
            for (var i = 0, len = rs.length; i < len; i++) {
                var r = rs[i], sel = dragRegion.intersect(r);
                if (sel && !r.selected) {
                    r.selected = true;
                    view.select(i, true);
                }
                else 
                    if (!sel && r.selected) {
                        r.selected = false;
                        view.deselect(i);
                    }
            }
        }
        
        function onCalDragEnd(e){
            e.stopPropagation();
            e.preventDefault();
            if (proxy) {
                
               /* var arr = e.target.id.split('cell');
                arr = arr[0].split('View');
                var MainPanel = Wtf.get(arr[1]);
                MainPanel = Wtf.getCmp(MainPanel.id);
                //var MainPanel=getCalCtrl();*/
                var backFlag = 0;
                //FIXME:checked just to prevent errors in case of event giving wrong target
                var lcell = e.getTarget("td");
                if(lcell && lcell.id.indexOf('_') != -1){
                    var lastcell = lcell.id;
                    var st = lastcell.indexOf('_');
                    var ed = lastcell.lastIndexOf('_');
                    var h1 = Wtf.get(proxy.id).dom.offsetHeight;
                    var h2 = Wtf.get(lastcell).dom.offsetHeight;
                    var hei = parseInt((h1 / h2), 10);
                    var tot = 0;
                    if (h1 > h2) 
                        tot = (h2 * hei) + h2;
                    else 
                        tot = h2;
                    h1 = tot;

                    var t = lastcell.split('_');
                    var t1 = activecell.split('_');
                    t[1] = parseInt(t[1], 10);
                    t1[1] = parseInt(t1[1], 10);

                    var endd = lastcell.substring(st + 1, ed);
                    if (t1[1] > t[1]) {
                        backFlag = activecell;
                        activecell = lastcell;
                        lastcell = backFlag;
                        backFlag = 1;
                    }
                    endd = parseInt(endd) + 1;
                    //var endArr = [];
                    //endArr = Wtf.cal.utils.getHourStr(endd);

                    if (backFlag == 0) 
                        timeStr += "-" + endd;
                    else {
                        /*endArr[0]--;
                        if (endArr[0] == 0) {
                            endArr[0] = 1;
                            //endArr[1] = "am";
                        }
                        else 
                            if (endArr[0] == 12 && endArr[1] == "pm") {
                                //endArr[1] = "am";
                            }*/
                        //timeStr = endArr[0] + endArr[1] + "-" + timeStr;
                        timeStr = endd  + "-" + timeStr;
                    }

                    var w1 = Wtf.get(activecell).dom.offsetWidth;
                    var panewidth = Wtf.get(MainPanel.id + MainPanel.currentview + 'eventPanel').dom.offsetWidth;
                    var paneheight = Wtf.get(MainPanel.id + MainPanel.currentview + 'eventPanel').dom.offsetHeigh;
                    w1 = (w1 / panewidth) * 100 - 1;
                    //            h1=h1/paneheight;
                    h1 = (h1 / paneheight) * 100;
    //                counter++;
                    var timeString = [];
                   //var cal = Wtf.getCmp(MainPanel.id);
                    proxy.setDisplayed(false);
//                    eventWin =new Wtf.cal.eventWindow(MainPanel);
                    MainPanel.eventWin.showWindowWeek(activecell, timeStr, h1, w1);
                    //	    createEventCell(counter,"dd4477",activecell,timeStr,"A brief description of the event no."+counter,h1,w1);
                    timeStr = null;
                    x = null;
            }else{
                proxy.setDisplayed(false);
            }
            activecell = '`';
            }
            proxy = null;
        }
        
        function onCalDragBeforeStart(e){
            return !dragSafe || e.target == view.el.dom;
        }

        function onCalDragStart(e){
            view.on('containerclick', cancelClick, view, {
                single: true
            });
            if (!proxy) 
                proxy = view.el.createChild({
                    cls: 'x-view-selector'
                });
            else 
                proxy.setDisplayed('block');
            fillRegions();
            view.clearSelections();
        }
    }

    else if(cfg.calDrag && !cfg.calAllDayWeek && cfg.calMonthView) {
        MainPanel = cfg.calContainer;

        function onCalMonthDrag(e) {
            if (activeDay.match('`')) {
                var cellone = e.getTarget("div");
                if(cellone && cellone.id.indexOf("Day1") != -1 && cellone.id.indexOf("more") == -1) {
                    activeDay = cellone.id;
                    xyM = Wtf.get(activeDay).getXY();
                }
                else if(cellone.parentNode.id.indexOf("Day1") != -1 && cellone.parentNode.id.indexOf("more") == -1) {
                        activeDay = cellone.parentNode.id;
                        xyM = Wtf.get(activeDay).getXY();
                }
                else {
                    if(proxy)
                        proxy.setDisplayed(false);
                    activeDay = '`';
                    proxy = null;
                    return;
                }
            }
            else {
                xyM = tracker.getXY();
            }
            var startDay = Wtf.get(activeDay);
            var startXY = tracker.startXY;

            var x = Math.min(xyM[0],startDay.getX());
            var y = startDay.getY();
            var w = Math.abs((xyM[0] > startDay.getX() ?  startXY[0] : x) - xyM[0]);
            var h = startDay.dom.offsetHeight;
            var h1 = Math.abs((tracker.lastXY[1]-tracker.startXY[1])); // Kuldeep Singh : Get the height of draggable area
            //xxxif (x == null) {
                //xxxx = Math.min(startXY[0], xyM[0]);
            //xxx}
            dragRegion.left = x+10;
            dragRegion.top = y;
            dragRegion.right = w + xOffset;//xxxx + w;
            dragRegion.bottom = y + h1;

            dragRegion.constrainTo(bodyRegion);
            proxy.setRegion(dragRegion);

            for(var i = 0, len = rs.length; i < len; i++) {
                var r = rs[i], sel = dragRegion.intersect(r);
                if(sel && !r.selected) {
                    r.selected = true;
                    view.select(i, true);
                } else if(!sel && r.selected) {
                    r.selected = false;
                    view.deselect(i);
                }
            }
        }

        function onCalMonthDragEnd(e) {
            e.stopPropagation();
            e.preventDefault();
            if (proxy) {
                var fcell = activeDay;
                var lcell = e.getTarget("div");
                if(lcell && (lcell.id.indexOf("Day1") == -1 || lcell.id.indexOf("more") != -1)) {
                    if(lcell.parentNode.id.indexOf("Day1") != -1 && lcell.parentNode.id.indexOf("more") == -1)
                        lcell = lcell.parentNode;
                }
                if(lcell && lcell.id.indexOf("Day1") != -1 && lcell.id.indexOf("more") == -1) {
                    var backFlag = 0;
                    var thisMonth = Wtf.getCmp(lcell.id.split("Day")[0]);
                    var lastcell = lcell.id;
                    var st = fcell.split('Day1')[1];
                    var ed = lastcell.split('Day1')[1];

                    if(st.indexOf("/") == -1)
                        dayStr = thisMonth.seldate.format("Y-m-") + st;
                    else {
                        st = parseInt(st.split("/")[0], 10);
                        if(st < 7)
                            dayStr = thisMonth.seldate.getLastDateOfMonth().add(Date.DAY, 1).format("Y-m-") + "0"+ st;
                        else
                            dayStr = thisMonth.seldate.getFirstDateOfMonth().add(Date.DAY, -1).format("Y-m-") + st;
                    }
                    dayStr += "/";
                    if(ed.indexOf("/") == -1)
                        dayStr += thisMonth.seldate.format("Y-m-") + ed;
                    else {
                        ed = parseInt(ed.split("/"[0], 10));
                        if(ed < 7)
                            dayStr += thisMonth.seldate.getLastDateOfMonth().add(Date.DAY, 1).format("Y-m-") + "0"+ ed;
                        else
                            dayStr += thisMonth.seldate.getFirstDateOfMonth().add(Date.DAY, -1).format("Y-m-") + ed;
                    }

                    if (st > ed) {
                        backFlag = activeDay;
                        activeDay = lastcell;
                        lastcell = backFlag;
                        backFlag = 1;
                    }

                    proxy.setDisplayed(false);

                    MainPanel.eventWin.showWindowWeek(activeDay, dayStr, 0, 0, false, true);

                    dayStr = null;
                    x = null;
                } else {
                    proxy.setDisplayed(false);
                }
                activeDay = '`';
            }
            proxy = null;
        }

        function onCalMonthDragBeforeStart(e) {
            return !dragSafe || e.target == view.el.dom;
        }

        function onCalMonthDragStart(e) {
            view.on('containerclick', cancelClick, view, {
                single: true
            });
            if (!proxy)
                proxy = view.el.createChild({
                    cls: 'x-view-selector'
                });
            else
                proxy.setDisplayed('block');
            fillRegions();
            view.clearSelections();
            xOffset = tracker.startXY[0];
        }
    }

    else if(cfg.calDrag && cfg.calAllDayWeek && !cfg.calMonthView) {

        function onCalADDrag(e) {
            var cellone = e.getTarget("div");
            if (activeAllDay.match('`')) {
                if(cellone && cellone.id.indexOf("_allDay_") != -1) {
                    activeAllDay = cellone.id;
                    xyAD = Wtf.get(activeAllDay).getXY();
                }
                else if(cellone.parentNode.id.indexOf("_allDay_") != -1) {
                        activeAllDay = cellone.parentNode.id;
                        xyAD = Wtf.get(activeAllDay).getXY();
                }
                else {
                    if(proxy)
                        proxy.setDisplayed(false);
                    activeAllDay = '`';
                    proxy = null;
                    return;
                }
            }
            else
                xyAD = tracker.getXY();

            if(proxy == null) {
                activeAllDay = '`';
                proxy = null;
                return;
            }
            var startDay = Wtf.get(activeAllDay);
            var startXY = tracker.startXY;

            var x = startDay.getX();
            var y = startDay.getY();
            var w = Math.abs(startXY[0] - xyAD[0]);
            var h = startDay.dom.offsetHeight;

            dragRegion.left = x;
            dragRegion.top = y;
            dragRegion.right = w + xOffsetAD;
            dragRegion.bottom = y + h;

            dragRegion.constrainTo(bodyRegion);
            proxy.setRegion(dragRegion);

            for(var i = 0, len = rs.length; i < len; i++) {
                var r = rs[i], sel = dragRegion.intersect(r);
                if(sel && !r.selected) {
                    r.selected = true;
                    view.select(i, true);
                } else if(!sel && r.selected) {
                    r.selected = false;
                    view.deselect(i);
                }
            }
        }

        function onCalADDragEnd(e) {
            e.stopPropagation();
            e.preventDefault();
            if (proxy) {
                var fcell = activeAllDay;
                var lcell = e.getTarget("div");
                if(lcell && (lcell.id.indexOf("_allDay_") == -1)) {
                    if(lcell.parentNode.id.indexOf("_allDay_") != -1)
                        lcell = lcell.parentNode;
                }
                if(lcell && lcell.id.indexOf("_allDay_") != -1) {
                    var thisAllDay = Wtf.getCmp(lcell.id.split("_allDay_")[0]);
                    var lastcell = lcell.id;
                    var st = fcell.split('_allDay_')[1];
                    var ed = lastcell.split('_allDay_')[1];

                    var stdt = thisAllDay.startdate;
                    stdt = stdt.add(Date.DAY,parseInt(st));

                    var endt = thisAllDay.startdate;
                    endt = endt.add(Date.DAY, parseInt(ed));

                    ADStr = stdt.format("Y-m-d") + "/" + endt.format("Y-m-d");

                    proxy.setDisplayed(false);

                    thisAllDay.eventWin.showWindowWeek(lastcell, ADStr, 0, 0, false, false, true);

                    ADStr = null;
                    xAD = null;
                } else {
                    proxy.setDisplayed(false);
                }
                activeAllDay = '`';
            }
            proxy = null;
        }

        function onCalADDragBeforeStart(e) {
            return !dragSafe || e.target == view.el.dom;
        }

        function onCalADDragStart(e) {
            view.on('containerclick', cancelClick, view, {
                single: true
            });
            if (!proxy)
                proxy = view.el.createChild({
                    cls: 'x-view-selector'
                });
            else
                proxy.setDisplayed('block');
            fillRegions();
            view.clearSelections();
            xOffsetAD = tracker.startXY[0];
        }
    }
};
