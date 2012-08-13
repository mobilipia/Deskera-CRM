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
function KeyAlert(e, o){
    if (e.keyCode == 13) {
        trstr = MakeTable(o.value);
        Wtf.DomHelper.insertHtml('beforeBegin', Wtf.DomQuery.select("table#schTable")[0].lastChild, trstr);
        DrawLines1();
        DrawLines2();
        //Wtf.getCmp('scheduleTab').doLayout();			
    }
}


function MakeTable(val){
    var trstr = "<tr><td width=\"50\">" + val + "</td>";
    var ctr = 12;
    var str = "AM"
    for (var i = 0; i < 48; i++) {
        if (val == "") {
            if (i % 2 == 0) {
                trstr += "<td name=" + ctr + ":00" + str + " width=\"17\" height=\"25\" style=\"border:none; color:#15428b\">" + ctr + "</td>";
                
                
            }
            else {
                trstr += "<td name=" + ctr + ":30" + str + " width=\"17\" height=\"25\" style=\"border:none\">&nbsp;</td>";
                if (ctr == 11) 
                    str = "PM"
                if (ctr == 12) 
                    ctr = 1;
                else 
                    ctr++;
            }
            
        }
        else {
            if (i % 2 == 0) {
                trstr += "<td name=" + ctr + ":00" + str + " width=\"17\" height=\"25\">&nbsp;</td>";
                
            }
            else {
                trstr += "<td name=" + ctr + ":30" + str + " width=\"17\" height=\"25\">&nbsp;</td>";
                if (ctr == 11) 
                    str = "PM"
                if (ctr == 12) 
                    ctr = 1;
                else 
                    ctr++;
            }
        }
    }
    trstr += "</tr>";
    return trstr;
}

function DrawLines(str){
    var domel = document.getElementsByName(Wtf.get(str).getValue().toString().trim());
    for (i = 1; i < domel.length; i++) {
        domel[i].style.borderLeft = "thin solid #c1bfbf";
    }
}

function DrawLines1(){
    DrawLines('starttime1');
    setTimeout(function(){
        var domel = document.getElementsByName(Wtf.get('starttime1').getValue().toString().trim());        
        for (i = 1; i < domel.length - 1; i++) {
            domel[i].style.borderLeft = "medium solid red";
        }
    }, 100);
}

function DrawLines2(){
    DrawLines('endtime1');
    setTimeout(function(){
        var domel = document.getElementsByName(Wtf.get('endtime1').getValue().toString().trim());
        for (i = 1; i < domel.length - 1; i++) {
            domel[i].style.borderLeft = "medium solid red";
        }
    }, 100);
}

function ScheduleTab(MainPanel){
    var a = document.getElementById(MainPanel.id + 'scheduleTab')
    //alert(a);
    if (a == null) {
        var str = "<div id=MainPanel.id+'schTableDiv' ><table id='schTable' cellspacing=\"0\" class=\"TableStyle\"><tr></table></div>";
        
        
        
        var panel1 = new Wtf.Panel({
            title: WtfGlobal.getLocaleText("crm.calendar.scheduletitle"),//'Schedule',
            width: '100%',
            html: str,
            frame: true,
            height: 480,
            resizeEl: 'schTableDiv',
            autoScroll: true,
            style: 'background-color:#dfe8f6',
            renderTo: MainPanel.id + 'schedulediv',
            id: MainPanel.id + 'scheduleTab',
            items: [{
                xtype: 'form',
                height: '100%',
                width: '100%',
                border: false,
                hideBorders: true,
                items: [{
                    xtype: 'fieldset',
                    title: 'Time',
                    width: '75%',
                    //autoHeight:true,	
                    cls: 'timeDiv',
                    items: [{
                        xtype: "checkbox",
                        boxLabel: WtfGlobal.getLocaleText("crm.calendar.addeventwin.alldayevent"),//'All Day Event',
                        id: MainPanel.id + 'CheckTime1',
                        hideLabel: true,
                        width: '100%',
                        cls: "margins",
                        name: 'AllDayEvent'
                    }, {
                        border: false,
                        hideBorders: true,
                        cls: 'timeContainer',
                        items: [{
                            xtype: 'fieldset',
                            id: MainPanel.id + 'timeLeft1',
                            style: 'border:none',
                            labelWidth: 90,
                            baseCls: 'eventTimeLeftDiv',
                            items: [{
                                xtype: "datefield",
                                fieldLabel:WtfGlobal.getLocaleText("crm.campaign.defaultheader.startdate"),// 'Start Date',
                                id: MainPanel.id + 'StartTime1',
                                cls: 'dateWidth'
                            }, {
                                xtype: "datefield",
                                fieldLabel: WtfGlobal.getLocaleText("crm.campaign.defaultheader.enddate"),//'End Date',
                                id: MainPanel.id + 'EndTime1',
                                cls: 'dateWidth'
                            }]
                        }, {
                            id: MainPanel.id + 'RightPanel',
                            border: false,
                            items: [{
                                xtype: 'fieldset',
                                id: MainPanel.id + 'timeRight1',
                                baseCls: 'eventTimeRightDiv',
                                labelWidth: 90,
                                style: 'border:none',
                                items: [{
                                    xtype: 'timefield',
                                    fieldLabel: WtfGlobal.getLocaleText("crm.audittrail.header.time"),//'Time',
                                    id: MainPanel.id + 'starttime1',
                                    name: 'starttime1',
                                    minValue: '12:00am',
                                    maxValue: '11:00pm',
                                    cls: 'dateWidth'
                                }, {
                                    xtype: 'timefield',
                                    fieldLabel: WtfGlobal.getLocaleText("crm.audittrail.header.time"),// 'Time',
                                    id: MainPanel.id + 'endtime1',
                                    name: 'endtime1',
                                    minValue: '12:00am',
                                    maxValue: '11:00pm',
                                    cls: 'dateWidth'
                                }]
                            }]
                        }]
                    }, new Wtf.form.Checkbox({
                        id: MainPanel.id + 'RecurringPatternSchedule',
                        boxLabel: WtfGlobal.getLocaleText("crm.calendar.addeventwin.addrecurringpattern"),//'Add Recurring Pattern',
                        cls: "margins",
                        hideLabel: true
                    })]
                }]
            }]
        
        
        });
        var trstr = MakeTable("");
        //alert(trstr);
        //document.getElementById('schTable').innerHTML += trstr;
        Wtf.DomHelper.insertHtml('afterEnd', Wtf.DomQuery.select("table#schTable")[0].lastChild, trstr);
        //panel1.doLayout();
        //alert(document.getElementById('schTable').innerHTML);
        var trstr = MakeTable("All Attendees");
        Wtf.DomHelper.insertHtml('afterEnd', Wtf.DomQuery.select("table#schTable")[0].lastChild, trstr);
        
        //document.getElementById('schTable').innerHTML += trstr;
        
        function GetTextField(){
            var textfield = new Wtf.form.TextField({
                id: MainPanel.id + 'textbox1'
            })
            return textfield.getEl();
        }

        function DisplaySch(obj, rec, opt){
            //var value = eval('(' + response.responseText + ')');
            var trstr = "";
            for (var i = 0; i < rec.length; i++) {
                trstr = MakeTable(rec[i].data['cname']);
                var busy = rec[i].data['busy'];
                Wtf.DomHelper.insertHtml('afterEnd', Wtf.DomQuery.select("table#schTable")[0].lastChild, trstr);
                
                //document.getElementById('schTable').innerHTML += trstr;
                if (busy != null) {
                    for (j = 0; j < busy.length; j++) {
                        var domel = document.getElementsByName(busy[j]);
                        domel[domel.length - 1].style.backgroundColor = "red";
                    }
                }
            }
            trstr = MakeTable("<input type=\"text\" size=15 onkeypress=\"KeyAlert(event,this)\" ></input>");
            Wtf.DomHelper.insertHtml('afterEnd', Wtf.DomQuery.select("table#schTable")[0].lastChild, trstr);
            
            //document.getElementById('schTable').innerHTML += trstr;
        }
 
        var reader = new Wtf.data.JsonReader({
            root: 'data',
            fields: [{
                name: 'cname'
            }, {
                name: 'phone'
            }, {
                name: 'email'
            }, {
                name: 'busy'
            }]
        });
        var agendastore = new Wtf.data.Store({
            url: 'Json/summary.json',
            reader: reader
        });
        agendastore.load();
        agendastore.on("load", DisplaySch, this);
        
        
        /*Wtf.Ajax.request({
         id: MainPanel.id + 'ajax1',
         url: 'Json/summary.json',
         method: 'GET',
         success: DisplaySch
         });*/
        var p1 = Wtf.get(MainPanel.id + "RecurringPatternSchedule").addListener('click', oncheck2);
        Wtf.get(MainPanel.id + 'starttime1').on("blur", DrawLines1);
        Wtf.get(MainPanel.id + 'endtime1').on("blur", DrawLines2);
        var p = Wtf.get(MainPanel.id + 'CheckTime1').addListener("click", oncheckschedule, MainPanel);
    }
}

function oncheckschedule(q, p){

    if (p.checked) {
        var startTime = document.getElementById(this.id + 'RightPanel');
        startTime.style.display = "none";
    }
    if (!p.checked) {
        //var startTime = Wtf.get(this.id+'RightPanel');
        //	startTime.show();
        var startTime = document.getElementById(this.id + 'RightPanel');
        startTime.style.display = "block";
        
    }
}
