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
package com.krawler.workflow.module.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * @author Ashutosh
 *
 */
public class DataObjectRowMapper implements RowMapper 
{
    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
    private Map<Integer, String> columnMap = new HashMap<Integer, String>();

    private DateFormat formatter;

    /**
     * Create a new BeanPropertyRowMapper.
     * @see #setMappedClass
     */
    public DataObjectRowMapper()
    {
    }

    public DataObjectRowMapper(DateFormat formatter) {
        this.formatter = formatter;
    }

    public Object mapRow(ResultSet rs, int rowNumber) throws SQLException
    {
        Map<String, Object> mappedObject = new HashMap<String, Object>();

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for (int index = 1; index <= columnCount; index++)
        {
            String column = columnMap.get(index);
            if (column == null)
            {
                column = JdbcUtils.lookupColumnName(rsmd, index).toLowerCase();
                columnMap.put(index, column);
            }
            Object value = rs.getObject(index);
            if(formatter!=null && value !=null && value instanceof Date)
                value = formatter.format(value);
            mappedObject.put(column, value);
        }

        return mappedObject;
    }


}
