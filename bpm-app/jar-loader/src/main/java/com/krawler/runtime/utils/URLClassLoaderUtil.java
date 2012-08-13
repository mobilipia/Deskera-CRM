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

package com.krawler.runtime.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shayanthan.k
 *
 */
public class URLClassLoaderUtil extends URLClassLoader {

	public URLClassLoaderUtil(URL[] urls) {
		super(urls);
	}


	private static Log log = LogFactory.getLog(URLClassLoaderUtil.class);


	private static final Class[] parameters = new Class[] { URL.class };


	public void addFile(String path) throws Exception {
		String urlPath = "jar:file://" + path + "!/";
		try {
			this.addJarURL(new URL(urlPath));

		} catch (Exception e) {
			throw e;
		}
	}


	public void addFile(File f) throws Exception {
		this.addJarURL(f.toURL());
	}


	public void addJarURL(URL u) throws Exception {
		try {

			URLClassLoader sysLoader = (URLClassLoader) ClassLoader
					.getSystemClassLoader();
			URL urls[] = sysLoader.getURLs();
			for (int i = 0; i < urls.length; i++) {
				if (StringUtils.equalsIgnoreCase(urls[i].toString(), u
						.toString())) {
					if (log.isDebugEnabled()) {
						log.debug("URL " + u + " is already in the CLASSPATH");
					}
					return;
				}
			}
			Class sysclass = URLClassLoader.class;
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysLoader, new Object[] { u });
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(
					"Error, could not add URL to system classloader"
							+ e.getMessage());
		}

	}
        public static void main(String[] args){
        try {
            URLClassLoaderUtil urlcu = new URLClassLoaderUtil(new URL[]{new URL("file:///home/krawler/KrawlerJsonLib.jar")});
            urlcu.addFile("/home/krawler/KrawlerJsonLib.jar");
            Class c = Class.forName("com.krawler.utils.json.base.JSONObject");
            System.out.print(c.getCanonicalName());

        } catch (Exception ex) {
            Logger.getLogger(URLClassLoaderUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        }
}
