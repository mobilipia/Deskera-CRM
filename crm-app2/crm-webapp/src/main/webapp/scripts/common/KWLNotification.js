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
var fnInt;
function createMaintainanceCall(){
    var time=300000;
    fnInt = setInterval(getSysMaintainanceData, time);
}

function abortMaintainanceCall() {
    clearInterval(fnInt);
}
function getSysMaintainanceData(){
    Wtf.Ajax.requestEx({
        url : "deskeracommon/Notification/getMaintainanceDetails.do"
    }, this,
    function(result, req){
        if(result.data!=undefined&&result.success==true ){
            var announcementpan = Wtf.getCmp('announcementpan');
            announcementpan.setVisible(true);
            notificationMessage(result.data[0].message);           
            announcementpan.doLayout();
            Wtf.getCmp('viewport').doLayout();
        }
        else{
            hideTopPanel();
           // abortMaintainanceCall()//to be removed
        }
    });
}

function notificationMessage(msg) {
    var announcementpan = Wtf.getCmp('announcementpan');
    if(announcementpan !=null) {
        announcementpan.setVisible(true);
        document.getElementById("announcementpandiv").innerHTML =msg;
        announcementpan.doLayout();
        Wtf.getCmp('viewport').doLayout();
    }
}
function hideTopPanel() {
    var announcementpan = Wtf.getCmp('announcementpan');
    if(announcementpan !=null) {
        document.getElementById("announcementpandiv").innerHTML ="";
        announcementpan.setVisible(false);
        announcementpan.doLayout();
        Wtf.getCmp('viewport').doLayout();
        abortMaintainanceCall();
    }
}

