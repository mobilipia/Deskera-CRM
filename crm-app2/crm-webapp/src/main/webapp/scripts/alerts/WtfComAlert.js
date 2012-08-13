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
 function WtfComMsgBox(choice, type) {
    var strobj = [];
    switch (choice) {
        case 0:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.0")];//["Alert", "No records have been modified."];
            break;
        case 2:
            strobj = [WtfGlobal.getLocaleText("crm.msg.WARNINGTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.2")];//["Warning", "Please enter appropriate value for the field(s) marked in red."];
            break;
        case 1:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.3")];//["Alert", "No records have been selected."];
            break;
        case 8:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.8")];//["Alert", "Please select a Lead to add its activities."];
            break;
        case 9:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.9")];//["Alert", "Please select only one Lead."];
            break;
        case 10:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.10")];//["Success", "Lead has been removed successfully."];
            break;
        case 11:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.11")];//["Alert", "Please select a Lead to remove."];
            break;
        case 12:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.12")];//["Error", "Sorry! The information could not be saved. Please try again."];
            break;
        case 13:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.13")];//["Error", "Sorry! An Error occurred while connecting to the server."];
            break;
        case 14:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.14")];//["Error", "Sorry! The information could not be saved. Please try again."];
            break;

        case 15:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.15")];//["Alert", "Please select a file to upload."];
            break;
        case 16:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.16")];//["Alert", "Selected record is not present in the current page."];
            break;
        case 17:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.17")];//["Alert", "Please accept CAN-SPAM Act Terms."];
            break;
        case 18:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.18")];//["Error", "Sorry! The list of uploaded files could not be displayed. Please try again."];
            break;
        case 19:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.19")];//["Error", "Sorry! The list of added comments could not be displayed. Please try again."];
            break;
        case 20:
        	strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.20")];//	["Alert", " Imported record already exists."];
        	break;
        case 21:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.21")];//["Alert", "Please enter all the essential details."];
            break;
        case 22:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.22")];//["Alert", "Please select an image to insert."];
            break;
        case 23:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.23")];//["Alert", "Please specify an image URL."];
            break;
        case 25:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.25")];//["Alert", "Please select valid record."];
            break;
        case 26:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.26")];//["Alert", "The record already exists."];
            break;
        case 27:
            strobj = ["Alert", "Percentage Discount cannot be greater than 100."];
            break;
        case 33:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.33")];//["Alert","Please enter the Product(s) first."];
            break;
        case 50:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.50")];//["Alert", "No records have been modified."];
            break;
        case 52:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.55")];//["Error", "Sorry! The information could not be saved. Please try again."];
            break;
        case 56:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.56")];//["Success", "Account has been removed successfully."];
            break;
        case 57:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.57")];//["Alert", "Please select an Account to remove."];
            break;
        case 58:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.58")];//["Alert", "Please select an Account to add its activities."];
            break;
        case 59:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.59")];//["Alert", "Please select a Lead."];
            break;
       
       case 60:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.60")];//["Alert", "Please select a file to upload."];
            break;
       case 61:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.61")];//["Alert", "Please enter all the essential details."];
            break;
            
       case 62:
           strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.62")];//["Alert", "Tab for selected Campaign(s) already opened.<br> Please close them to Archive. "];
           break;
        case 63:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.63")];//["Alert","Please select the record(s) first."];
            break;
        case 64:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.64")];//["Success","Selected quotation(s) has been  deleted successfully."];
            break;
        case 65:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.65")];//["Alert","Please select an Account to add its opportunities."];
            break;
        case 66:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.66")];//["Alert","Please select an Account to add its cases."];
            break;

        case 100:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.100")];//["Alert", "No records have been modified."];
            break;
        case 102:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.102")];//["Error", "Sorry! The information could not be saved. Please try again."];
            break;
        case 106:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.106")];//["Alert", "Please select an Opportunity to Archive."];
            break;
        case 107:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.107")];//["Success", "Opportunity has been removed successfully."];
            break;
        case 108:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.108")];//["Alert", "Please select an Opportunity to remove."];
            break;
        case 109:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.109")];//["Alert", "Please select an Opportunity to add its activities."];
            break;
        case 110:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.110")];//["Alert", "Please select a file to upload."];
            break;
        case 111:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.111")];//["Alert", "Please enter all the essential details."];
            break;
        case 112:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.112")];//["Alert", "Selected field is required. Cannot edit it."];
            break;

        case 150:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.150")];//["Alert", "No records have been modified."];
            break;
        case 152:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.152")];//["Error", "Sorry! The information could not be saved. Please try again."];
            break;
        case 156:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.156")];//["Success", "Campaign has been removed successfully."];
            break;
        case 157:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.157")];//["Alert", "Please select a Campaign to remove."];
            break;
        case 158:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.158")];//["Alert", "Please select only one Campaign."];
            break;
        case 159:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.159")];//["Alert", "Please select a file to upload."];
            break;
         case 160:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.160")];//["Alert", "Please Specify a Campaign Name"];
            break;
       
        case 200:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.200")];//["Alert", "No records have been modified."];
            break;
        case 202:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.202")];//["Error", "Sorry! The information could not be saved. Please try again."];
            break;
        case 206:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.206")];//["Success", "Activity has been removed successfully."];
            break;
        case 207:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.207")];//["Alert", "Please select an Activity to remove."];
            break;
        case 208:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.208")];//["Alert", "Please select an Activity."];
            break;
        case 209:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.209")];//["Alert", "Please select a file to upload."];
            break;
       case 210:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.210")];//["Alert", "Please enter all the essential details."];
            break;

        case 211:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.211")];//["Alert", "Selected target(s) removed successfully."];
            break;
        case 212:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.212")];//["Alert", "Sorry! Selected target(s) could not be removed. Please try again."];
            break;
        case 213:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.213")];//["Alert", "Please select atleast one target from the list."];
            break;
       

        case 250:
           strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.250")];//["Alert", "No records have been modified."];
            break;
        case 252:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.252")];//["Error", "Sorry! The information could not be saved. Please try again."];
            break;
        case 256:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.256")];//["Success", "Case has been removed successfully."];
            break;
        case 257:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.257")];//["Alert", "Please select a Case to remove."];
            break;
        case 258:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.258")];//["Alert", "Please select a Case to add its activities."];
            break;
        case 259:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.259")];//["Alert", "Please select a file to upload."];
            break;
        case 260:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.260")];//["Alert", "Please enter all the essential details."];
            break;


        case 300:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.300")];//["Alert", "No records have been modified."];
            break;
        case 302:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.302")];//["Error", "Sorry! The information could not be saved. Please try again."];
            break;
        case 306:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.306")];//["Success", "Contact has been removed successfully."];
            break;
        case 307:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.307")];//["Alert", "Please select a Contact to remove."];
            break;
        case 308:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.308")];//["Alert", "Please select a Contact to add its activities."];
            break;
        case 309:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.309")];//["Alert", "Please select a file to upload."];
            break;
       case 310:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.310")];//["Alert", "Please enter all the essential details."];
            break;

        case 350:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.350")];//["Alert", "No records have been modified."];
            break;
        case 352:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.352")];//["Error", "Sorry! The information could not be saved. Please try again."];
            break;
        case 356:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.356")];//["Success", "Product has been removed successfully."];
            break;
        case 357:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.357")];//["Alert", "Please select a Product to remove."];
            break;
         case 358:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.358")];//["Alert", "Please select only one Product."];
            break;
        case 359:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.359")];//["Alert", "Please select a file to upload."];
            break;
        case 360:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.360")];//["Alert", "Please enter all the essential details."];
            break;

        case 400:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.400")];//["Alert", "Please select one record."];
            break;
       case 401:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.401")];//["Alert", "Please select one record."];
            break;
       case 451:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.451")];//['Error', 'Sorry! Contacts could not be imported. Please try again.'];
            break;
       case 452:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.452")];//["Alert","Please enter valid file type."];
            break;
       case 453:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.453")];//['Success', "Contact has been added successfully."];
            break;
       case 454:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.454")];//['Error', "An error occurred while importing contacts from the csv file. "];
            break;
      
       case 455:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.455")];//['Success', "Contact has been edited successfully."];
            break;
       case 456:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.456")];//['Alert', 'Please specify an e-mail recipient.'];
            break;
       case 457:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.457")];//['Alert', 'Please specify an e-mail sender.'];
            break;
       case 458:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.458")];//['Success', "New Contacts have been imported successfully and updated to the Contact List."];
            break;
       case 459:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.459")];//['Success', "New Leads have been imported successfully and updated to the Lead List."];
            break;
       case 460:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.460")];//['Success', "New Leads have been imported successfully. Leads having conflict with existing list have been skipped."];
            break;
       case 461:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.461")];//['Error', 'Sorry! Leads could not be successfully imported. Please try again.'];
            break;
       case 462:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.462")];//['Success', "Lead has been added successfully."];
            break;
       case 463:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.463")];//['Success', "New Accounts have been imported successfully and updated to the Account List."];
            break;
       case 464:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.464")];//['Success', "New Accounts have been imported successfully. Accounts having conflict with existing list have been skipped."];
            break;
       case 465:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.465")];//['Error', 'Sorry! Accounts could not be successfully imported. Please try again.'];
            break;
       case 466:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.466")];//['Success', "Accounts has been added successfully"];
            break;
       case 467:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.467")];//['Success', 'Accounts imported successfully.'];
            break;
       case 468:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.468")];//['Success', 'Leads imported successfully.'];
            break;
       case 469:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.469")];//['Success', 'Contacts imported successfully.'];
            break;

        case 470:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.470")];//['Success', "Targets imported successfully. Conflict targets have been skipped."];
            break;
       case 471:
            strobj = ['Alert', 'Please specify an e-mail sender while saving mail.'];
            break;
     
        case 605:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.605")];//["Alert", "Please select a Target list."];
            break;
        case 606:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.606")];//["Alert", "You have already added all Target lists. Create a new Target list to add here."];
            break;
        case 607:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.607")];//["Alert", "No Target list has been created. Please create a Target list to add here."];
            break;

        case 787:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.787")];//["Error", "Sorry! Template could not be edited successfully. Please try again."];
            break;
        case 788:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.788")];//["Alert", "Please validate entries."];
            break;
        case 789:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.789")];//["Error", "The report template has not been saved. Please check entries and try again."];
            break;
        case 791:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.791")];//["Error", "Sorry! Template could not be created successfully. Please try again."];
            break;
        case 792:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.792")];//["Alert", "Please select one template."];
            break;
        case 794:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.794")];//["Error", "Sorry! Template could not be deleted successfully. Please try again."];
            break;

        case 900:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.900")];//["Alert", "No records have been modified."];
            break;
        case 901:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.901")];//["Success", "Target has been saved successfully."];
            break;
        case 902:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.902")];//["Error", "Sorry! The information could not be saved. Please try again."];
            break;
        case 906:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.906")];//["Success", "Target has been removed succcessfully."];
            break;
        case 907:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.907")];//["Alert", "Please select a Target to remove."];
            break;
        case 908:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.908")];//["Alert", "Please select a Target."];
            break;
        case 909:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.909")];//["Alert", "Please select a file to upload."];
            break;
        case 910:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.910")];//["Alert", "Please enter all the essential details."];
            break;

        case 911:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.911")];//["Error","Sorry! Email Configuration could not be saved. Please try again."];
            break;
        case 949:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.949")];//["Alert", "Please fill all the valid and essential fields (marked with *)."];
            break;
        case 950:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.950")];//["Alert", "Please enter all parameter configuration details."];
            break;
        case 951:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.951")];//["Alert", "Please fill all the essential fields (marked with an asterisk *)."];
            break;
        case 952:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.952")];//["Alert", "Please fill all the essential fields ."];
            break;
        case 953:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.953")];//["Success","Campaign Configuration has been saved successfully."];
            break;
        case 954:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.954")];//["Error","Sorry! The selected Email Configuration(s) could not be deleted. Please try again."];
            break;
        case 955:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.955")];//["Alert","Please fill all the essential fields (marked with an asterisk *)."];
            break;
        case 956:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.956")];//["Alert","Please select atleast one target list."];
            break;
        case 957:
            strobj = [WtfGlobal.getLocaleText("crm.msg.COMMISSIONPLANTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.957")];//["Commission Plan","Please select single commission plan."];
            break;
        case 958:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.958")];//["Alert", "Please select default value."];
            break;
        case 959:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.959")];//["Alert", "Please select a parameter type to view its parameter value."];
            break;
        case 960:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.960")];//["Error", "Sorry! The customer login for this email address already exists. <br/>Please change email address for the contact to create customer login."];
            break; 
        case 961:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.961")];
            break;    
        case 1000:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1000")];//["Alert","Please map headers to import."];
            break;
        case 1050:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1050")];//["Alert","Credentials provided by you are invalid. Please try again."];
            break;
        case 1051:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1051")];//["Alert","Please select contacts to import."];
            break;
        case 1052:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1052")];//["Alert","Please enter company name for which you want to import Leads."];
            break;
        case 1053:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1053")];//["Alert","Please select an Account for which you want to import Contacts."];
            break;
        case 1054:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1054")];//["Alert","Please enter a valid email id for your Google Account."];
            break;
        case 1055:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1055")];//["Alert","Please enter a valid password for your Google Account."];
            break;
        case 1056:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1056")];//["Alert","Please specify SMTP username and password."];
            break;
        case 1057:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1057")];//["Success","Outbound email settings have been saved successfully."];
            break;
        case 1058:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1058")];//["Alert", "Please Specify SMTP username and SMTP password"];
            break;
        case 1100:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1100")];//["Alert", "Please select a Campaign to add its activities."];
            break;
        case 1101:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1101")];//["Alert", "Now you won't be able to view Campaign Configurations."];
            break;
            case 1102:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1102")];//["Alert", "Please Enter a valid phone number"];
            break;
        case 1103:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1103")];//["Success", "Search has been saved successfully."];
            break;
        case 1104:
            strobj =  [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1104")];//["Error", "Sorry! The search could not be added. Please try again."];
            break;
        case 1105:
            strobj =  [WtfGlobal.getLocaleText("crm.msg.ALERTTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1105")];//["Alert", "Please enter valid search name to add."];
            break;
        case 1106:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1106")];//["Success", "Search has been deleted successfully."];
            break;
        case 1107:
            strobj = [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1107")];// ["Error", "Sorry! The search could not be deleted. Please try again."];
            break;
        case 1108:
            strobj = [WtfGlobal.getLocaleText("crm.msg.SUCCESSTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1108")];//["Success", "Custom report has been deleted successfully."];
            break;
        case 1109:
            strobj =  [WtfGlobal.getLocaleText("crm.msg.ERRORTITLE"),WtfGlobal.getLocaleText("crm.wtfcomalert.msg.1109")];//["Error", "Sorry! The custom report could not be deleted. Please try again."];
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

