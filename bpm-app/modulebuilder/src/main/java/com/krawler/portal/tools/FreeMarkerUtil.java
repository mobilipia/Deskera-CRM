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

package com.krawler.portal.tools;


import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FreeMarkerUtil {
    private static final Log logger = LogFactory.getLog(FreeMarkerUtil.class);
    private static Configuration _configuration;
    public static String process(String name, Object context)
            throws Exception {

            StringWriter writer = new StringWriter();

            process(name, context, writer);

            return writer.toString();
    }

    public static void process(String name, Object context, Writer writer)
            throws Exception {
            try {
                Template template = _getConfiguration().getTemplate(name);

                template.process(context, writer);
            } catch(TemplateException ex) {
                logger.warn(ex.getMessage(), ex);
            } catch(java.io.IOException ex) {
                logger.warn(ex.getMessage(), ex);
            } 
    }

    private static Configuration _getConfiguration() {
        if (_configuration == null) {
            try {
                _configuration = new Configuration();

                _configuration.setObjectWrapper(new DefaultObjectWrapper());
//                _configuration.setTemplateLoader(createTemplateLoader("file://home/sm/KWLTools"));
                _configuration.setTemplateLoader(new ClassTemplateLoader(FreeMarkerUtil.class, "/"));
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }

            return _configuration;
    }
	
    protected static TemplateLoader createTemplateLoader(String templatePath) throws IOException
    {
        if (templatePath.startsWith("class://")) {
            // substring(7) is intentional as we "reuse" the last slash
            return new ClassTemplateLoader(FreeMarkerUtil.class, templatePath.substring(7));
        } else {
//            if (templatePath.startsWith("file://")) 
            {
                templatePath = "/"+templatePath.substring(7);
                return new FileTemplateLoader(new File(templatePath));
            } /*else {
                return new WebappTemplateLoader(this.getServletContext(), templatePath);
            }*/
        }
    }
}
