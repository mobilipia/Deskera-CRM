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
package com.krawler.spring.mailIntegration;

import com.krawler.common.admin.Company;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.esp.handlers.synchMailHandler;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class mailIntegrationImpl implements mailIntegrationDAO {
    private synchMailHandler synchMailHandlerObj;
//    private profileHandlerDAO profileHandlerDAOObj;
    private HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

//    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
//        this.profileHandlerDAOObj = profileHandlerDAOObj1;
//    }
    
    public void setsynchMailHandler(synchMailHandler synchMailHandlerObj) {
        this.synchMailHandlerObj = synchMailHandlerObj;
    }
    public KwlReturnObject synchMail() throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyQuery = " from Company where deleted = 0";
            List lst = this.hibernateTemplate.find(companyQuery);
            if (lst.size() > 0) {
                Iterator ite = lst.iterator();
                while(ite.hasNext()) {
                    Company company = (Company) ite.next();
                    synchMailHandlerObj.add("", company.getCompanyID(),company.getSubDomain());
                    if (!synchMailHandlerObj.isIsWorking()) {
                        Thread mailSender = new Thread(synchMailHandlerObj);
                        mailSender.start();
                    }
                }
            }
            
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.sendEmailMarketMail : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);

    }
}
