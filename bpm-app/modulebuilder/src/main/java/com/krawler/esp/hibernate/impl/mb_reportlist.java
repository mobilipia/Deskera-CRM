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
package com.krawler.esp.hibernate.impl;

/**
 * <a href="mb_reportlistModel.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_reportlist</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;

public class mb_reportlist implements Serializable
{
    private int deleteflag;
    private java.util.Date createddate;
    private java.util.Date modifieddate;
    private String createdby;
    private String reportname;
    private String reportid;
    private String tablename;
    private int reportkey;
    private int type;
    private Integer displayconf;
    private int tableflag;
    
    private String companyid;

    public Integer getDisplayconf()
    {
        return displayconf;
    }

    public void setDisplayconf(Integer displayconf)
    {
        this.displayconf = displayconf;
    }

    public int getTableflag()
    {
        return tableflag;
    }

    public void setTableflag(int tableflag)
    {
        this.tableflag = tableflag;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getReportkey()
    {
        return reportkey;
    }

    public void setReportkey(int reportkey)
    {
        this.reportkey = reportkey;
    }

    public int getDeleteflag()
    {
        return deleteflag;
    }

    public void setDeleteflag(int deleteflag)
    {
        if (

        deleteflag != this.deleteflag

        )
        {
            this.deleteflag = deleteflag;
        }
    }

    public String getTablename()
    {
        return tablename;
    }

    public void setTablename(String tablename)
    {
        this.tablename = tablename;
    }

    public java.util.Date getCreateddate()
    {
        return createddate;
    }

    public void setCreateddate(java.util.Date createddate)
    {
        if (

        createddate != this.createddate

        )
        {
            this.createddate = createddate;
        }
    }

    public java.util.Date getModifieddate()
    {
        return modifieddate;
    }

    public void setModifieddate(java.util.Date modifieddate)
    {
        if (

        modifieddate != this.modifieddate

        )
        {
            this.modifieddate = modifieddate;
        }
    }

    public String getCreatedby()
    {
        return createdby;
    }

    public void setCreatedby(String createdby)
    {
        if (

        (createdby == null && this.createdby != null) || (createdby != null && this.createdby == null) || (createdby != null && this.createdby != null && !createdby.equals(this.createdby))

        )
        {
            this.createdby = createdby;
        }
    }

    public String getReportname()
    {
        return reportname;
    }

    public void setReportname(String reportname)
    {
        if (

        (reportname == null && this.reportname != null) || (reportname != null && this.reportname == null) || (reportname != null && this.reportname != null && !reportname.equals(this.reportname))

        )
        {
            this.reportname = reportname;
        }
    }

    public String getReportid()
    {
        return reportid;
    }

    public void setReportid(String reportid)
    {
        if (

        (reportid == null && this.reportid != null) || (reportid != null && this.reportid == null) || (reportid != null && this.reportid != null && !reportid.equals(this.reportid))

        )
        {
            this.reportid = reportid;
        }
    }

    /**
     * @return the companyid
     */
    public String getCompanyid()
    {
        return companyid;
    }

    /**
     * @param companyid the companyid to set
     */
    public void setCompanyid(String companyid)
    {
        this.companyid = companyid;
    }
}
