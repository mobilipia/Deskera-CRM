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
 function ResponseAlert(choice) {
    var strobj = [];
    switch (choice) {
        case 0:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.0")];// ["","The template has been deleted successfully."];
            break;
        case 1:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.1")];//["","The template has been saved."];
            break;
        case 2:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.2")];//["","The template has been updated successfully."];
            break;
            
        case 3:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.3")];//["","Activity has been saved successfully."];
            break;
        case 4:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.4")];//["","Opportunity has been saved successfully."];
            break;
        case 5:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.5")];//["","Contact has been saved successfully."];
            break;
        case 6:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.6")];//["","Case has been saved successfully."];
            break;
        case 7:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.7")];//["","Account has been saved successfully."];
            break;
        case 8:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.8")];//["","Activity has been saved successfully."];
            break;
        case 9:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.9")];//["","Lead has been saved successfully."];
            break;
        case 10:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.10")];//["","Product has been saved successfully."];
            break;
        case 11:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.11")];//["","Campaign has been saved successfully."];
            break;
            
        case 12:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.12")];//["","Select a Search Field to search"];
            break;
        case 13:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.13")];//["","Please specify a Search Term"];
            break;
        case 14:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.14")];//["","Select a Search Term and add it to get search results."];
            break;

        case 15:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.15")];//["","Please select dates to filter results"];
            break;
        case 16:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.16")];//["","From Date' cannot be greater than 'To Date"];
            break;
            
        case 17:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.17")];//["", "The selected Campaign(s) has been deleted successfully."];
            break;
        case 18:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.18")];//["", "Sorry! The selected Campaign(s) could not be deleted. Please try again."];
            break;
        case 19:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.19")];//["", "Please select a Campaign to delete."];
            break;

        case 20:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.20")];//["", "The selected Lead(s) has been deleted successfully."];
            break;
        case 21:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.21")];//["", "Sorry! The selected Lead(s) could not be deleted. Please try again."];
            break;
        case 22:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.22")];//["", "Please select a Lead to delete."];
            break;
            
        case 23:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.23")];//["", "The selected Contact(s) has been deleted successfully."];
            break;
        case 24:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.24")];//["", "Sorry! The selected Contact(s) could not be deleted. Please try again."];
            break;
        case 25:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.25")];//["", "Please select a Contact to delete."];
            break;
            
        case 26:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.26")];//["", "The selected Product(s) has been deleted successfully."];
            break;
        case 27:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.27")];//["", "Sorry! The selected Product(s) could not be deleted. Please try again."];
            break;
        case 28:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.28")];//["", "Please select a Product to delete."];
            break;
            
        case 29:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.29")];//["", "The selected Account(s) has been deleted successfully."];
            break;
        case 30:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.30")];//["", "Sorry! The selected Account(s) could not be deleted. Please try again."];
            break;
        case 31:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.31")];//["", "Please select an Account to delete."];
            break;
            
        case 32:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.32")];//["", "The selected Opportunities has been deleted successfully."];
            break;
        case 33:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.33")];//["", "Sorry! The selected Opportunities could not be deleted. Please try again."];
            break;
        case 34:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.34")];//["", "Please select an Opportunities to delete."];
            break;
            
        case 35:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.35")];//["", "The selected Case(s) has been deleted successfully."];
            break;
        case 36:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.36")];//["", "Sorry! The selected Case(s) could not be deleted. Please try again."];
            break;
        case 37:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.37")];//["", "Please select a Case to delete."];
            break;

        case 38:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.38")];//["", "The selected Activities has been deleted successfully."];
            break;
        case 39:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.39")];//["", "Sorry! The selected Activities could not be deleted. Please try again."];
            break;
        case 40:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.40")];//["", "Please select an Activities to delete."];
            break;
            
        case 41:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.41")];//["", "File has been uploaded successfully."];
            break;
        case 42:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.42")];//["", "Sorry! Document could not be uploaded successfully. Please try again."];
            break;
        case 43:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.43")];//["", "Please select a file to upload."];
            break;
            
        case 44:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.44")];//["", "Comment has been added successfully."];
            break;
        case 45:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.45")];//["", "Sorry! Comment could not be saved successfully. Please try again."];
            break;
            
        case 46:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.46")];//["", "The selected Lead has been converted successfully."];
            break;
        case 47:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.47")];//["", "Sorry! Lead could not be converted successfully. Please try again."];
            break;
        case 48:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.48")];//["", "Please select a Pre-Qualified Lead to convert."];
            break;
        case 49:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.49")];//["", "Please select a Lead to convert."];
            break;
        case 50:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.50")];//["", "Please select only one Lead to convert."];
            break;
        case 51:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.51")];//["", "Email has been sent successfully to the selected Target List"];
            break;
        case 52:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.52")];//["", "The selected Target has been deleted successfully."];
            break;
        case 53:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.53")];//["", "Sorry! Target could not be deleted successfully. Please try again."];
            break;
        case 54:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.54")];//["", "Please select a Target to delete."];
            break;
        case 55:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.55")];//["", "Email Template has been deleted successfully."];
            break;
        case 56:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.56")];//["", "Sorry! E-mail Template could not be deleted successfully. Please try again."];
            break;
        case 57:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.57")];//["", "Please select an Email Template to delete."];
            break;
        case 58:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.58")];//["","'From Date' cannot be greater than 'To Date'"];
            break;
        case 59:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.59")];//["","E-mail Template has been created successfully"];
            break;
        case 60:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.60")];//["","E-mail Template has been updated successfully"];
            break;
        case 61:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.61")];//["","Sorry! E-mail Template could not be saved successfully. Please try again."];
            break;
        case 62:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.62")];//["","Sorry! E-mail Template could not be updated successfully. Please try again."];
            break;
        case 63:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.63")];//["","Please enter a Name for the Target list."];
            break;
        case 64:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.64")];//["","Target List has been deleted successfully."];
            break;
        case 65:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.65")];//["","Sorry! Target List could not be deleted successfully. Please try again."];
            break;
        case 66:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.66")];//["","Please select a Target List to delete."];
            break;
        case 67:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.67")];//["","Please select a Target List to edit."];
            break;
        case 68:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.68")];//["","Parameter configuration has been inserted successfully."];
            break;
        case 69:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.69")];//["","Sorry! E-mail Type could not be set to default. Please try again."];
            break;
        case 70:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.70")];//["","Select Campaign Type as 'Email Campaign' for adding several email campaigns."];
            break;
        case 71:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.71")];//["","Please select a Campaign."];
            break;
        case 72:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.72")];//["","Please select an Email Campaign to edit."];
            break;
        case 73:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.73")];//["","Target List is empty. Please import/add targets to save the list. "];
            break;
        case 74:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.74")];//["","E-mail Template has been set to default successfully"];
            break;
        case 80:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.80")];//["", "Lead status cannot be changed to Qualified, unless it is converted into Opportunity or Account."];
            break;
        case 81:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.81")];//["", "Qualified Lead cannot be updated."];
            break;
        case 82:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.82")];//["", "Please select a Delimiter type for CSV."];
            break;
        case 83:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.83")];//["", "Please upload a file with valid file type."];
            break;
        case 84:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.84")];//["","'Start time' cannot be greater than 'End time' for same date."];
            break;
        case 85:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.85")];//["","'End Time' cannot be greater than 'Till Date'"];
            break;
        case 86:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.86")];//["","'Start Date' cannot be greater than 'End Date'"];
            break;
        case 87:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.87")];//["","It is an All day event. You can't edit time part"];
            break;
        case 89:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.89")];//["", "Comment has been deleted successfully."];
            break;
        case 90:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.90")];//["", "Sorry! Comment could not be deleted successfully. Please try again."];
            break;
        case 91:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.91")];//["", "Record(s) has been deleted successfully."];
            break;
        case 92:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.92")];//["", "Sorry! Record(s) could not be deleted successfully. Please try again."]
            break;
            
        case 93:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.93")];//["", "Comment has been edited successfully."];
            break;
        case 94:
            strobj = ["",  WtfGlobal.getLocaleText("crm.responsealert.msg.94")];//"Document has been deleted successfully."];
            break;
        case 95:
            strobj = ["",  WtfGlobal.getLocaleText("crm.responsealert.msg.95")];//"Sorry! Document could not be deleted successfully. Please try again."];
            break;
        case 96:
            strobj = ["",  WtfGlobal.getLocaleText("crm.responsealert.msg.96")];//"Lead(s) has been forwarded successfully."];
            break;
        case 97:
            strobj = ["",  WtfGlobal.getLocaleText("crm.responsealert.msg.97")];//Selected column has been deleted successfully."];
            break;
        case 98:
            strobj = ["",  WtfGlobal.getLocaleText("crm.responsealert.msg.98")];//"Custom column cannot be deleted."];
            break;
        case 99:
            strobj = ["",  WtfGlobal.getLocaleText("crm.responsealert.msg.99")];//"Custom column cannot be deleted."];
            break;
                  
        case 101:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.101")];//["","Please enter some information to add comment."];
            break;
        case 102:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.102")];//["","Please select an Email Campaign for Scheduling Email Campaign."];
            break;
        case 103:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.103")];//["","Please fill in all required fields."];
            break;
        case 104:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.104")];//["","Email Campaign Scheduled successfully."];
            break;
        case 105:
            strobj = ["",WtfGlobal.getLocaleText("crm.responsealert.msg.105")];
            break;
        case 106:
            strobj = ["",WtfGlobal.getLocaleText("crm.responsealert.msg.106")];
            break;
        case 151:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.151")];//["", "Please select an Account or Opportunity with Contact to convert the selected Lead."];
            break;
        case 152:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.152")];//["", "Please enter the required fields."];
            break;
        case 153:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.153")];//["", "Please select a Parent Account for Opportunity."];
            break;
        case 154:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.154")];//["", "Please enter an Account name."];
            break;
        case 155:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.155")];//["", "Please enter a Subject for Activity."];
            break;
        case 200:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.200")];//["", "Processing your request."];
            break;
        case 201:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.201")];//["", "Template has been already saved."];
            break;
        case 250:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.250")];//["","The template has been successfully edited and downloaded."];
            break;
        case 300:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.300")];//["","Help tooltip has been added successfully."];
            break;
        case 301:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.301")];//["", "Sorry! Help tooltip could not be saved successfully. Please try again."];
            break;
        case 351:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.351")];//["", "Selected Google Contacts have been imported successfully."];
            break;
        case 352:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.352")];//["", "Sorry! Google Contacts could not be imported successfully. Please try again."];
            break;
        case 353:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.353")];//["", "Google Contacts have been imported successfully."];
            break;
        case 354:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.354")];//["", "Selected campaign schedule has been deleted successfully."];
            break;
        case 355:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.355")];//["","Sorry! Selected campaign schedule could not be deleted successfully. Please try again."];
            break;
        case 400:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.400")];//["", "Test Email has been sent successfully to you."];
            break;
        case 401:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.401")];//["", "Sorry! Test E-mail could not be sent successfully. Please try again."];
            break;
        case 500:
            strobj = [WtfGlobal.getLocaleText("crm.responsealert.msg.500"), ""];//["Loading...", ""];
            break;
        case 501:
            strobj = [WtfGlobal.getLocaleText("crm.responsealert.msg.501"), ""];//["Reloading...", ""];
            break;
        case 510:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.510")];//["", "There are no rules to apply."];
            break;
        case 511:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.511")];//["", "There are no rules to delete."];
            break;
        case 550:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.550")];//["", "You do not have required permission to download document."];
            break;
        case 551:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.551")];//["", "There are no formulae to apply."];
            break;
        case 552:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.552")];//["", "Invalid records selected to export."];
            break;
        case 553:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.553")];//["", "Invalid records selected to print."];
            break;
        case 554:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.554")];//["", "You do not have required permission to view Campaigns."];
            break;
        case 555:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.555")];//["", "You do not have required permission to view Contacts."];
            break;
        case 556:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.556")];//["", "You do not have required permission to view Leads."];
            break;
        case 557:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.557")];//["", "You do not have required permission to view Accounts."];
            break;
        case 600:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.600")];//["", "Please select a Template."];
            break;
        case 630:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.630")];//["", "Schedule Email Campaign have been saved successfully."];
            break;
        case 631:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.631")];//["", "Sorry! Schedule Email Campaign could not be saved successfully. Please try again."];
            break;
        case 650:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.650")];//["", "Company preferences have been saved successfully."];
            break;
        case 651:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.651")];//["", "Sorry! Company preferences could not be saved successfully. Please try again."];
            break;
        case 652:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.652")];//["","Please select either dates or industry to filter results"];
            break;
        case 653:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.653")];//["","Please select either dates or lead source to filter results"];
            break;
        case 654:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.654")];//["","Please select either dates or case priority to filter results"];
            break;
        case 700:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.700")];//["","Target has been saved successfully."];
            break;
        case 751:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.751")];//["","Goals added successfully."];
            break;
        case 752:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.752")];//["","Sorry! Goal could not be saved successfully. Please try again."];
            break;
        case 753:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.753")];//['', 'Goals deleted successfully'];
            break;
        case 754:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.754")];//['', "Sorry! Goals could not be deleted successfully. Please try again."];
            break;
       case 800:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.800")];//["", "Please select an employee to add Target."];
            break;
       case 810:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.810")];//["", "Please select main owner."];
            break;
       case 811:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.811")];//["", "Owners have been saved successfully."];
            break;
       case 812:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.812")];//["", "Sorry! Owners could not be saved successfully. Please try again."];
            break;
       case 813:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.813")];//["", "Project created successfully."];
            break;
       case 814:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.814")];//["", "Sorry! Project could not be saved successfully. Please try again."];
            break;
        case 820:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.820")];//['', 'Outbound email settings deleted successfully'];
            break;
        case 821:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.821")];//['', "Sorry! Outbound email settings could not be deleted successfully. Please try again."];
            break;
        case 900:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.900")];//['', "Product synchronization has been done successfully."];
            break;
        case 901:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.901")];//["", "Document has been uploaded successfully. Please wait for a few minutes and refresh the document list if uploaded document is not listed."];
            break;
        case 902:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.902")];//["","Please select Column, From Date & To Date to filter results"];
            break;
        case 903:
            strobj = ["", WtfGlobal.getLocaleText("crm.responsealert.msg.903")];//["","Data could not load.Please reload the grid"];
            break;
        default:
            strobj = [choice[0], choice[1]];
            break;
    }
    Wtf.notify.msg(strobj[0],strobj[1]);
}



