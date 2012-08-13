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

import java.util.Timer;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KrawlerLog;
import com.krawler.esp.httpclient.EasySSLProtocolSocketFactory;
import com.krawler.esp.stats.KrawlerPerf;
import com.krawler.esp.utils.mime.MimeType;
import com.krawler.esp.utils.mime.MimeTypeException;
import com.krawler.esp.utils.mime.MimeTypes;

/**
 * Class that encapsulates the initialization and shutdown of services needed by
 * any process that adds mail items. Services under control include redo logging
 * and indexing.
 */
public class KrawlerApp {
	private static boolean sInited = false;

	public static synchronized void startup() throws ServiceException {
		if (sInited)
			return;
		// should be last, so that other subsystems can add dynamic stats
		// counters
		KrawlerPerf.initialize();
		sInited = true;
	}

	public static synchronized void shutdown() throws ServiceException {
		if (!sInited)
			return;

		sInited = false;

		sTimer.cancel();
	}

	public static Timer sTimer = new Timer(true);

	/**
	 * Logs the given message and shuts down the server.
	 * 
	 * @param message
	 *            the message to log before shutting down
	 */
	public static void halt(String message) {
		try {
			KrawlerLog.system.fatal(message);
		} finally {
			Runtime.getRuntime().halt(1);
		}
	}

	/**
	 * Logs the given message and shuts down the server.
	 * 
	 * @param message
	 *            the message to log before shutting down
	 * @param t
	 *            the exception that was thrown
	 */
	public static void halt(String message, Throwable t) {
		try {
			KrawlerLog.system.fatal(message, t);
		} finally {
			Runtime.getRuntime().halt(1);
		}
	}

	public static void toolSetup() {
		toolSetup("INFO");
	}

	public static void toolSetup(String defaultLogLevel) {
		toolSetup(defaultLogLevel, null, false);
	}

	public static void toolSetup(String defaultLogLevel, String logFile,
			boolean showThreads) {
		KrawlerLog.toolSetupLog4j(defaultLogLevel, logFile, showThreads);
		boolean allowuntrustedcerts = Boolean.parseBoolean(ConfigReader
				.getinstance().get("ssl_allow_untrusted_certs", "true"));
		if (allowuntrustedcerts)
			EasySSLProtocolSocketFactory.init();
	}

	public static String getContentType(String typeName, String url, byte[] data) {

		MimeTypes mimeTypes = MimeTypes.get("mime-types.xml");
		boolean mimeTypeMagic = true;
		MimeType type = null;

		try {
			typeName = MimeType.clean(typeName);
			type = typeName == null ? null : mimeTypes.forName(typeName);
		} catch (MimeTypeException mte) {
			// Seems to be a malformed mime type name...
		}

		if (typeName == null || type == null || !type.matches(url)) {
			// If no mime-type header, or cannot find a corresponding registered
			// mime-type, or the one found doesn't match the url pattern
			// it shouldbe, then guess a mime-type from the url pattern
			type = mimeTypes.getMimeType(url);
			typeName = type == null ? typeName : type.getName();
		}
		if (typeName == null || type == null
				|| (mimeTypeMagic && type.hasMagic() && !type.matches(data))) {
			// If no mime-type already found, or the one found doesn't match
			// the magic bytes it should be, then, guess a mime-type from the
			// document content (magic bytes)
			type = mimeTypes.getMimeType(data);
			typeName = type == null ? typeName : type.getName();
		}
		return typeName;
	}

}
