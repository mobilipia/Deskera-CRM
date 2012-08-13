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
package com.krawler.esp.httpclient;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.TrustManagerFactory;
import com.sun.net.ssl.X509TrustManager;

/**
 * <p>
 * EasyX509TrustManager unlike default {@link X509TrustManager} accepts
 * self-signed certificates.
 * </p>
 * <p>
 * This trust manager SHOULD NOT be used for productive systems due to security
 * reasons, unless it is a concious decision and you are perfectly aware of
 * security implications of accepting self-signed certificates
 * </p>
 * 
 * @author <a href="mailto:adrian.sutton@ephox.com">Adrian Sutton</a>
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * 
 * <p>
 * DISCLAIMER: HttpClient developers DO NOT actively support this component. The
 * component is provided as a reference material, which may be inappropriate for
 * use without additional customization.
 * </p>
 */

public class EasyX509TrustManager implements X509TrustManager {
	private X509TrustManager standardTrustManager = null;

	/** Log object for this class. */
	private static final Log LOG = LogFactory
			.getLog(EasyX509TrustManager.class);

	/**
	 * Constructor for EasyX509TrustManager.
	 */
	public EasyX509TrustManager(KeyStore keystore)
			throws NoSuchAlgorithmException, KeyStoreException {
		super();
		TrustManagerFactory factory = TrustManagerFactory
				.getInstance("SunX509");
		factory.init(keystore);
		TrustManager[] trustmanagers = factory.getTrustManagers();
		if (trustmanagers.length == 0) {
			throw new NoSuchAlgorithmException(
					"SunX509 trust manager not supported");
		}
		this.standardTrustManager = (X509TrustManager) trustmanagers[0];
	}

	/**
	 * @see com.sun.net.ssl.X509TrustManager#isClientTrusted(X509Certificate[])
	 */
	public boolean isClientTrusted(X509Certificate[] certificates) {
		return this.standardTrustManager.isClientTrusted(certificates);
	}

	/**
	 * @see com.sun.net.ssl.X509TrustManager#isServerTrusted(X509Certificate[])
	 */
	public boolean isServerTrusted(X509Certificate[] certificates) {
		if ((certificates != null) && LOG.isDebugEnabled()) {
			LOG.debug("Server certificate chain:");
			for (int i = 0; i < certificates.length; i++) {
				LOG.debug("X509Certificate[" + i + "]=" + certificates[i]);
			}
		}
		if ((certificates != null) && (certificates.length == 1)) {
			X509Certificate certificate = certificates[0];
			try {
				certificate.checkValidity();
			} catch (CertificateException e) {
				LOG.error(e.toString());
				return false;
			}
			return true;
		} else {
			return this.standardTrustManager.isServerTrusted(certificates);
		}
	}

	/**
	 * @see com.sun.net.ssl.X509TrustManager#getAcceptedIssuers()
	 */
	public X509Certificate[] getAcceptedIssuers() {
		return this.standardTrustManager.getAcceptedIssuers();
	}
}
