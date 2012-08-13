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
package com.krawler.common.notification.web;

public class NotificationConstants {

    // Please add new entry into notification_channel table also
    public static enum CHANNEL{
        SMS, EMAIL;
    }
    // Please add new entry into notification_status table also
    public static enum NOTIFICATIONSTATUS{
        REQUEST,SENT,DELIVERY;
    }
    // Please add new entry into notification_type table also
    public static int ACT_REMAINDER = 1;
    public static int ACT_ASSIGNED = 2;
    public static int OPPORTUNITY_CREATION = 3;
    public static int OPPORTUNITY_ASSIGNED = 4;
    public static int ACCOUNT_CREATION = 5;
    public static int ACCOUNT_ASSIGNED = 6;
    public static int GOAL_CREATION = 7;
    public static int GOAL_ASSIGNED = 8;
    public static int LEADACTIVITY_ASSIGNED = 9;
    public static int ACCOUNTACTIVITY_ASSIGNED = 10;
    public static int CONTACTACTIVITY_ASSIGNED = 11;
    public static int OPPACTIVITY_ASSIGNED = 12;
    public static int CASEACTIVITY_ASSIGNED = 13;
    public static int CAMPAIGNACTIVITY_ASSIGNED = 14;
    public static int CASE_CLOSED = 15;
    public static int CASE_ASSIGNED = 16;
    public static int FCFS_LEADROUTING = 17;
    public static int ACCOUNT_ASSIGNED_MASS_UPDATE=18;
    public static int OPPORTUNITY_ASSIGNED_MASS_UPDATE=19;


    // Constant Placeholders
    public static String LOGINURL = "#LOGINURL#";
    public static String PARTNERNAME = "#PARTNERNAME#";
    public static String COMPANYNAME = "COMPANYNAME";
}
