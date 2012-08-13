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
package com.krawler.spring.auditTrailModule;

import java.util.HashMap;
import java.util.Map;

public class AuditTrailConstants
{
    public static final Map<String, Long> auditGroupMap = new HashMap<String, Long>();
    
    public static final Map<Long, String> auditGroupReverseMap = new HashMap<Long, String>();

    static
    {
        auditGroupMap.put("Account", 25l);
        auditGroupMap.put("Activity", 28l);
        auditGroupMap.put("Admin", 50l);
        auditGroupMap.put("Campaign", 21l);
        auditGroupMap.put("Case", 27l);
        auditGroupMap.put("Contact", 23l);
        auditGroupMap.put("Document", 29l);
        auditGroupMap.put("Lead", 22l);
        auditGroupMap.put("Opportunity", 26l);
        auditGroupMap.put("Product", 24l);
        auditGroupMap.put("Report", 51l);
        auditGroupMap.put("Target", 30l);
        auditGroupMap.put("User", 52l);
    }
    
    static
    {
        auditGroupReverseMap.put(25l, "Account");
        auditGroupReverseMap.put(28l, "Activity");
        auditGroupReverseMap.put(50l, "Admin");
        auditGroupReverseMap.put(21l, "Campaign");
        auditGroupReverseMap.put(27l, "Case");
        auditGroupReverseMap.put(23l, "Contact");
        auditGroupReverseMap.put(29l, "Document");
        auditGroupReverseMap.put(22l, "Lead");
        auditGroupReverseMap.put(26l, "Opportunity");
        auditGroupReverseMap.put(24l, "Product");
        auditGroupReverseMap.put(51l, "Report");
        auditGroupReverseMap.put(30l, "Target");
        auditGroupReverseMap.put(52l, "User");
    }
}
