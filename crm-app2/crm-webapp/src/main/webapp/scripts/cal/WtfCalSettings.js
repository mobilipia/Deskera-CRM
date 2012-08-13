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
Wtf.BLANK_IMAGE_URL = "lib/resources/images/default/s.gif";
Wtf.fixHeight = Wtf.isIE8 ? 32 :( Wtf.isWebKit? 31:30 );
Wtf.namespace('Wtf', 'Wtf.cal', 'Wtf.ux');

Wtf.calReq = {
    cal: "calendar/calendar/",
    calevent: "calendar/calendarevent/",
    calCommon: "calendar/common/"
};

Wtf.getCalDateFormat = function() {
    return WtfGlobal.getDateFormat();
}

function getInstrMsg(msg) {
    return "<span style='font-size:10px !important;color:gray !important;'>"+msg+"</span>";
}

function calMsgBoxShow(choice, type) {
    var strobj = [];
    switch (choice) {
        case 4:
            strobj = ["Error", "Error occurred while connecting to the server."];
            break;
        case 5:
            strobj = ['Error', 'Email Address is already present.'];
            break;
        case 56:
            strobj = ["Error","Enter file of valid type."];
            break;
        case 113:
            strobj = ['Error', 'Please enter the valid end date for recurrence!'];
            break;
        case 114:
            strobj = ['Error', 'Please enter the no. of weeks for recurrence!'];
            break;
        case 115:
            strobj = ["Invalid Event", "Event doesn't exist!"];
            break;
        case 116:
            strobj = ['Error', 'You dont have sufficient privileges to modify this event!'];
            break;
        case 117:
            strobj = ['Error', 'Holiday Calendar events cannot be deleted.'];
            break;
        case 118:
            strobj = ['Error', 'Please select a row to edit.'];
            break;
        case 119:
            strobj = ['Error', 'Select only one event at a time.'];
            break;
        case 120:
            strobj = ['Error', 'Please select a row to delete.'];
            break;
        case 121:
            strobj = ['Error', 'You dont have sufficient privileges to delete the events!'];
            break;
        case 122:
            strobj = ['Error', 'Calendar Creation Failed!'];
            break;
        case 123:
            strobj = ['Error', 'Calendar Updation Failed!'];
            break;
        case 124:
            strobj = ['Error', 'Calendar Deletion Failed!'];
            break;
        case 125:
            strobj = ['Error', 'Event Updation Failed!'];
            break;
        case 126:
            strobj = ['Error', 'Events do not exist or have been already deleted!'];
            break;
        case 127:
            strobj = ['Error', 'No event to delete.'];
            break;
        case 128:
            strobj = ['Error', 'Error occurred while importing Calendar.'];
            break;
        case 129:
            strobj = ['Success', 'Calendar imported successfully.'];
            break;
        case 130:
            strobj = ['Error', 'Please select a calendar.'];
            break;
        case 136:
            strobj = ['Success', 'Events deleted successfully.'];
            break;
        case 137:
            strobj = ['Success', 'Calendar has been deleted successfully.'];
            break;
        case 138:
            strobj = ['Error', 'Selected calendar does not exist or has been deleted!'];
            break;
        case 139:
            strobj = ['Success', 'Calendar has been created successfully.'];
            break;
        case 153:
            strobj = ['Error', 'A problem occurred while loading Events.'];
            break;
        case 154:
            strobj = ['Status', 'Invalid operation '];
            break;
        case 155:
            strobj = ['Error', 'Event has been deleted'];
            break;
        case 156:
            strobj = ['Status', 'Event shared successfully'];
            break;
        case 157:
            strobj = ['Error', 'Error in sharing event'];
            break;
        case 158:
            strobj = ['Error', 'Calendar has been deleted'];
            break;
        case 159:
            strobj = ['Error', 'Calendar containing this event has been deleted'];
            break;
        case 160:
            strobj = ['Info', "You can not modify any agenda of CRM Activities Calendar"];
            break;
       case 161:
            strobj = ['Info', "Please select records to import."];
            break;
       case 162:
           strobj = ['Success', 'Calendar has been edited successfully.'];
           break;
       case 163:
           strobj = ['Info', "Sorry! You cannot edit CRM Activities calendar's event"];
           break;
       case 164:
           strobj = ['Error', "Sorry! You cannot edit an event, added through dashboard."];
           break;
        default:
            strobj = [choice[0], choice[1]];
            break;
    }
	var iconType = Wtf.MessageBox.INFO;
    if(type == 0)
        iconType = Wtf.MessageBox.INFO;
	if(type == 1)
	    iconType = Wtf.MessageBox.ERROR;
    else if(type == 2)
        iconType = Wtf.MessageBox.WARNING;
    else if(type == 3)
        iconType = Wtf.MessageBox.INFO;

    Wtf.MessageBox.show({
        title: strobj[0],
        msg: strobj[1],
        buttons: Wtf.MessageBox.OK,
        animEl: 'mb9',
        icon: iconType
    });
}

function calLoadControl(pid){
    if (!Wtf.getCmp(pid + 'Calendar')) {
        var datePicker = new Wtf.DatePicker({
            id: pid + 'calctrlcalpopup1',
            cls: 'datepicker',
            autoWidth: true,
            border: false,
            defaults: {
                autoHeight: true,
                autoScroll: true
            },
            renderTo: 'calendarcontainer'
        });
        var calTree = new Wtf.CalendarTree({
            id: pid + "Calendar",
            url: Wtf.calReq.cal + "getAllEvents.do",
            ownerid: {
                type: 0,
                userid: loginid
            },
            parentid: pid,
            renderTo: "calendartree-container",
            calControl: null,
            parentTabId: pid,
            datePicker: datePicker
        });
    }
}

function toggleMainCal(state){
    //FIXME: mainpanel id hardcoded "as"
    var mainDatePicker = Wtf.getCmp('ascalctrlcalpopup1');
    var mainCalTree = Wtf.getCmp('asCalendar');
    if (state) {
        if (mainDatePicker) {
            mainDatePicker.show();
        }
        if (mainCalTree) {
            mainCalTree.show();
            mainCalTree.getSelectionModel().clearSelections();
        }
    }
    else {
        if (mainDatePicker) {
            mainDatePicker.hide();
        }
        if (mainCalTree) {
            mainCalTree.hide();
        }
    }
}

function guestResponse(eid, userid, response){
    Wtf.Ajax.request({
        url: Wtf.calReq.cal + 'guestStatus.jsp',
        method: 'GET',
        params: ({
            eid: eid,
            userid: userid,
            response: response
        }),
        scope: this,
        success: function(result, req){
            var nodeobj = eval("(" + result.responseText.trim() + ")");
            if (nodeobj.success == "Invalid")
                calShowMsgBox(154, 1);
            else
                if (nodeobj.success == "deleted")
                    calShowMsgBox(155, 1);
                else
                    if (nodeobj.success == "true")
                        calShowMsgBox(156, 0);
                    else
                        if (nodeobj.success == "false")
                            calShowMsgBox(157, 1);
        },
        failure: function(){
            calMsgBoxShow(4, 1);
        }
    });
}
