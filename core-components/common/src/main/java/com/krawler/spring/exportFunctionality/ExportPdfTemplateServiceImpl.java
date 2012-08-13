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
package com.krawler.spring.exportFunctionality;

import java.util.HashMap;

import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;

/**
 * @author krawler
 *
 */
public class ExportPdfTemplateServiceImpl implements ExportPdfTemplateService
{

    private exportPdfTemplateDAO exportPdfTemplDAO;

    /**
     * @param exportPdfTemplDAO the exportPdfTemplDAO to set
     */
    public void setExportPdfTemplDAO(exportPdfTemplateDAO exportPdfTemplDAO)
    {
        this.exportPdfTemplDAO = exportPdfTemplDAO;
    }
    
    /* (non-Javadoc)
     * @see com.krawler.spring.exportFunctionality.ExportPdfTemplateService#saveReportTemplate(java.util.HashMap)
     */
    @Override
    public KwlReturnObject saveReportTemplate(HashMap<String, Object> requestParams) throws ServiceException
    {
        
        return exportPdfTemplDAO.saveReportTemplate(requestParams);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.exportFunctionality.ExportPdfTemplateService#deleteReportTemplate(java.lang.String)
     */
    @Override
    public KwlReturnObject deleteReportTemplate(String tempid) throws ServiceException
    {
        return exportPdfTemplDAO.deleteReportTemplate(tempid);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.exportFunctionality.ExportPdfTemplateService#editReportTemplate(java.util.HashMap)
     */
    @Override
    public KwlReturnObject editReportTemplate(HashMap<String, Object> requestParams) throws ServiceException
    {
        return exportPdfTemplDAO.editReportTemplate(requestParams);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.exportFunctionality.ExportPdfTemplateService#getAllReportTemplate(java.lang.String)
     */
    @Override
    public KwlReturnObject getAllReportTemplate(String userid, int templatetype) throws ServiceException
    {
        return exportPdfTemplDAO.getAllReportTemplate(userid,templatetype);
    }

}
