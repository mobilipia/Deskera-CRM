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

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.Projreport_Template;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

/**
 *
 * @author Karthik
 */
public class exportPdfTemplateDAOImpl extends BaseDAO implements exportPdfTemplateDAO {

    /* (non-Javadoc)
     * @see com.krawler.spring.exportFunctionality.exportPdfTemplateDAO#saveReportTemplate(java.util.HashMap)
     */
    public KwlReturnObject saveReportTemplate(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {

            Projreport_Template proj_temp = new Projreport_Template();
            if (requestParams.containsKey("tempid") && !requestParams.get("tempid").toString().equals("")) {
                proj_temp.setTempid(requestParams.get("tempid").toString());
            }
            if (requestParams.containsKey("name") && !requestParams.get("name").toString().equals("")) {
                proj_temp.setTempname(requestParams.get("name").toString());
            }
            if (requestParams.containsKey("desc") && !requestParams.get("desc").toString().equals("")) {
                proj_temp.setDescription(requestParams.get("desc").toString());
            }
            if (requestParams.containsKey("jsondata") && !requestParams.get("jsondata").toString().equals("")) {
                proj_temp.setConfigstr(requestParams.get("jsondata").toString());
            }
            if (requestParams.containsKey("userid") && !requestParams.get("userid").toString().equals("")) {
                proj_temp.setUserid((User) get(User.class, requestParams.get("userid").toString()));
            }
            if (requestParams.containsKey("templatetype") && !requestParams.get("templatetype").toString().equals("")) {
                proj_temp.setType(Integer.valueOf(requestParams.get("templatetype").toString()));
            }
            if (requestParams.containsKey("pretext") && !requestParams.get("pretext").toString().equals("")) {
                proj_temp.setPreText(requestParams.get("pretext").toString());
            }
            if (requestParams.containsKey("posttext") && !requestParams.get("posttext").toString().equals("")) {
                proj_temp.setPostText(requestParams.get("posttext").toString());
            }
            if (requestParams.containsKey("letterhead") && !requestParams.get("letterhead").toString().equals("")) {
                proj_temp.setLetterHead(requestParams.get("letterhead").toString());
            }
            save(proj_temp);

            ll.add(proj_temp);
        } catch (HibernateException ex) {
            throw ServiceException.FAILURE("exportPdfTemplateDAOImpl.saveReportTemplate", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.exportFunctionality.exportPdfTemplateDAO#getAllReportTemplate(java.lang.String)
     */
    public KwlReturnObject getAllReportTemplate(String userid, int templateType) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        String Hql = "select p from com.krawler.common.admin.Projreport_Template p where p.userid.userID=? and p.deleteflag=0 and p.type = ?";
        ll = executeQuery(Hql, new Object[] { userid, templateType});
        dl = ll.size();
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.exportFunctionality.exportPdfTemplateDAO#deleteReportTemplate(java.lang.String)
     */
    public KwlReturnObject deleteReportTemplate(String tempid) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            if (!StringUtil.isNullOrEmpty(tempid)) {
                Projreport_Template proj_temp = (Projreport_Template) get(Projreport_Template.class, tempid);
                proj_temp.setDeleteflag(1);
                save(proj_temp);

                ll.add(proj_temp);
            }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("exportPdfTemplateDAOImpl.deleteReportTemplate", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.exportFunctionality.exportPdfTemplateDAO#editReportTemplate(java.util.HashMap)
     */
    public KwlReturnObject editReportTemplate(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            if (requestParams.containsKey("tempid") && !requestParams.get("tempid").toString().equals("")) {
                Projreport_Template proj_temp = (Projreport_Template) get(Projreport_Template.class, requestParams.get("tempid").toString());
                if (requestParams.containsKey("newconfig") && !requestParams.get("newconfig").toString().equals("")) {
                    proj_temp.setConfigstr(requestParams.get("newconfig").toString());
                }
                if (requestParams.containsKey("pretext") && !requestParams.get("pretext").toString().equals("")) {
                    proj_temp.setPreText(requestParams.get("pretext").toString());
                }
                if (requestParams.containsKey("posttext") && !requestParams.get("posttext").toString().equals("")) {
                    proj_temp.setPostText(requestParams.get("posttext").toString());
                }if (requestParams.containsKey("letterhead") && !requestParams.get("letterhead").toString().equals("")) {
                    proj_temp.setLetterHead(requestParams.get("letterhead").toString());
                }
                
                
                save(proj_temp);
                ll.add(proj_temp);
            }
        } catch (HibernateException e) {
            throw ServiceException.FAILURE("exportPdfTemplateDAOImpl.editReportTemplate", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
}
