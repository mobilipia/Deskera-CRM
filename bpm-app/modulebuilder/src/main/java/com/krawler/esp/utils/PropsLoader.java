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

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="PropsLoader.java.html"><b><i>View Source</i></b></a>
 *
 * @author Krawler
 *
 */
public abstract class PropsLoader {
    private static final Log logger = LogFactory.getLog(PropsLoader.class);
public static Properties loadProperties (String name, ClassLoader loader)
    {
        if (name == null)
            throw new IllegalArgumentException ("null input: name");

        if (name.startsWith ("/"))
            name = name.substring (1);

        if (name.endsWith (SUFFIX))
            name = name.substring (0, name.length () - SUFFIX.length ());

        Properties result = null;

        InputStream in = null;
        try
        {
            if (loader == null) loader = ClassLoader.getSystemClassLoader ();

            if (LOAD_AS_RESOURCE_BUNDLE)
            {
                name = name.replace ('/', '.');
                // Throws MissingResourceException on lookup failures:
                final ResourceBundle rb = ResourceBundle.getBundle (name,
                    Locale.getDefault (), loader);

                result = new Properties ();
                for (Enumeration keys = rb.getKeys (); keys.hasMoreElements ();)
                {
                    final String key = (String) keys.nextElement ();
                    final String value = rb.getString (key);

                    result.put (key, value);
                }
            }
            else
            {
                name = name.replace ('.', '/');

                if (! name.endsWith (SUFFIX))
                    name = name.concat (SUFFIX);

                // Returns null on lookup failures:
                in = loader.getResourceAsStream (name);
                if (in != null)
                {
                    result = new Properties ();
                    result.load (in); // Can throw IOException
                }
            }
        }
        catch (Exception e)
        {
            logger.warn(e.getMessage(), e);
            result = null;
        }
        finally
        {
            if (in != null) try { in.close (); } catch (Throwable ignore) {logger.warn(ignore.getMessage(), ignore);}
        }

        if (THROW_ON_LOAD_FAILURE && (result == null))
        {
            throw new IllegalArgumentException ("could not load [" + name + "]"+
                " as " + (LOAD_AS_RESOURCE_BUNDLE
                ? "a resource bundle"
                : "a classloader resource"));
        }

        return result;
    }

    /**
     * A convenience overload of {@link #loadProperties(String, ClassLoader)}
     * that uses the current thread's context classloader.
     */
    public static Properties loadProperties (final String name)
    {
        return loadProperties (name,
            Thread.currentThread ().getContextClassLoader ());
    }

    private static final boolean THROW_ON_LOAD_FAILURE = true;
    private static final boolean LOAD_AS_RESOURCE_BUNDLE = false;
    private static final String SUFFIX = ".properties";
} 
