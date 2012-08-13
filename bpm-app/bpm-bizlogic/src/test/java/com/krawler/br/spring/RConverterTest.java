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

package com.krawler.br.spring;

import com.krawler.br.modules.ModuleBag;
import com.krawler.br.modules.ModuleDefinitionParser;
import com.krawler.br.modules.SimpleModuleBag;
import com.krawler.br.modules.XmlModuleDefinitionParser;
import com.krawler.br.nodes.BProcess;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.operations.SimpleOperationParameter;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.XmlFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpSession;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 *
 * @author krawler-user
 */
public class RConverterTest extends TestCase {
    ModuleBag mBag;
    
    public RConverterTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
            /**
             * Loading module definitions
             */
            // create module factory from the specified XML resource
            SimpleModuleBag mf = new SimpleModuleBag();
            // create Module definition parser for the spplied XMLFactory
            ModuleDefinitionParser mp = new XmlModuleDefinitionParser();
            // Load all Definitions into the factory by the supplied module definition parser

            Properties p = new Properties();
            p.setProperty("path", "client/trial_moduleEx.xml");
            SourceFactory src = new XmlFactory(p,mp);

            mf.load(src);

            mBag = mf;
    }

    /**
     * Test of convert method, of class RConverter.
     */
    public void testConvert() throws Exception {
        System.out.println("convert");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("user", "{name:\"M1\",timeZone:\"GMT-8:00\",startTime:\"10:00 AM\",endTime:\"8:00 PM\"}");
        HttpSession se = request.getSession();
        se.setAttribute("timeformat", "1");
        se.setAttribute("tzdiff", "+5:30");
        BProcess process = new BProcess();
        HashMap<String, OperationParameter> hm = new HashMap<String, OperationParameter>();
        SimpleOperationParameter op = new SimpleOperationParameter();
        op.setName("user");
        op.setType("person");
        hm.put("user", op);
        process.setInputParams(hm);
        RConverterImpl instance = new RConverterImpl();
        instance.setModuleBag(mBag);
        try{
            Map result = instance.convert(request, process);
            Iterator<String> itr = result.keySet().iterator();
            while(itr.hasNext()){
                String key = itr.next();
                System.out.println("user: "+result.get("user"));
            }
        }catch(Exception ex){
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}
