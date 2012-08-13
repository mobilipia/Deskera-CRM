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
package com.krawler.customFieldMaster;


/**
 * A place to keep commonly-used constants.
 */
public class FieldConstants {
    //Image Path

    public static final String Crm_getcombodata = "SELECT `id`,`value` from fieldcombodata  where fieldid = ? and deleteflag=? ";
    public static final String Crm_fc = "fieldManager.getComboData";
    public static final String Crm_fieldid = "fieldid";
    public static final String Crm_deleteflag = "deleteflag";
    public static final String Crm_flag = "flag";
    public static final String Crm_id = "id";
    public static final String Crm_name = "name";
    public static final String Crm_FieldComboData = "FieldComboData";
    public static final String Crm_tablename = "tablename";
    public static final String Crm_maxlength = "maxlength";
}
