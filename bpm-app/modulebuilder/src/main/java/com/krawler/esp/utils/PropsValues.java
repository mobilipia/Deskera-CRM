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
package com.krawler.esp.utils;

/**
 * <a href="PropsValues.java.html"><b><i>View Source</i></b></a>
 *
 * @author Krawler
 *
 */

public class PropsValues {

	public static final String PROJECT_NAME = PropsUtil.get(PropsKeys.PROJECT_NAME);

    public static final String PROJECT_HOME = PropsUtil.get(PropsKeys.PROJECT_HOME);

    public static final String TOMCAT_HOME = PropsUtil.get(PropsKeys.TOMCAT_HOME);

    public static final String JSP_FILE_CONTENT = PropsUtil.get(PropsKeys.JSP_FILE_CONTENT);

    public static final String JSP_FILE_PATH = PROJECT_HOME + PropsUtil.get(PropsKeys.JSP_FILE_PATH);

    public static final String PACKAGE_PATH = PropsUtil.get(PropsKeys.PACKAGE_PATH);

    public static final String TPL_ROOT = PropsUtil.get(PropsKeys.TPL_ROOT);

    public static final String TPL_MODEL = TPL_ROOT + PropsUtil.get(PropsKeys.TPL_MODEL);

    public static final String TPL_IMPL_MODEL = TPL_ROOT + PropsUtil.get(PropsKeys.TPL_IMPL_MODEL);

    public static final String TPL_HBM_XML = TPL_ROOT + PropsUtil.get(PropsKeys.TPL_HBM_XML);

    public static final String GENERATE_DIR_PATH = PROJECT_HOME + PropsUtil.get(PropsKeys.GENERATE_DIR_PATH);

    public static final String CFG_SOURCE_FILE_PATH = PROJECT_HOME + PropsUtil.get(PropsKeys.CFG_SOURCE_FILE_PATH);

    public static final String CFG_CLASSES_FILE_PATH = TOMCAT_HOME + PropsUtil.get(PropsKeys.CFG_CLASSES_FILE_PATH);

    public static final String PACKAGE_FILE_PATH = PropsUtil.get(PropsKeys.PACKAGE_FILE_PATH);

    public static final String REPORT_HARDCODE_STR = PropsUtil.get(PropsKeys.REPORT_HARDCODE_STR);

    public static final String STORE_PATH = PROJECT_HOME + PropsUtil.get(PropsKeys.STORE_PATH);

    public static final String MODULE_PROPERTIES = TOMCAT_HOME + PropsUtil.get(PropsKeys.MODULE_PROPERTIES);

    public static final String MODULE_BUILD_XML = TOMCAT_HOME + PropsUtil.get(PropsKeys.MODULE_BUILD_XML);

    public static final String MODULE_SOURCE_DIR = PROJECT_HOME + PropsUtil.get(PropsKeys.MODULE_SOURCE_DIR);

    public static final String MODULE_CLASSES_DIR = TOMCAT_HOME + PropsUtil.get(PropsKeys.MODULE_CLASSES_DIR);

    public static final String MODULE_CLASSES_DESC_DIR = TOMCAT_HOME + PropsUtil.get(PropsKeys.MODULE_CLASSES_DESC_DIR);

    public static final String INDEX_PATH =  PROJECT_HOME + PropsUtil.get(PropsKeys.INDEX_PATH);

    public static final String TOMCAT_LIB = PropsUtil.get(PropsKeys.TOMCAT_LIB);

    public static final String PROJECT_LIB = TOMCAT_HOME + PropsUtil.get(PropsKeys.PROJECT_LIB);

    public static final String SMTPPath1 = PropsUtil.get(PropsKeys.SMTPPath1);

    public static final String SMTPPort1 = PropsUtil.get(PropsKeys.SMTPPort1);

}
