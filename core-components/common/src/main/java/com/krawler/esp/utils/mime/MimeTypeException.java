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
package com.krawler.esp.utils.mime;

/**
 * A class to encapsulate MimeType related exceptions.
 * 
 * @author Hari Kodungallur
 * @author Jerome Charron - http://frutch.free.fr/
 */
public class MimeTypeException extends Exception {

	private static final long serialVersionUID = -1180686934419653100L;

	/**
	 * Constructs a MimeTypeException with no specified detail message.
	 */
	public MimeTypeException() {
		super();
	}

	/**
	 * Constructs a MimeTypeException with the specified detail message.
	 * 
	 * @param msg
	 *            the detail message.
	 */
	public MimeTypeException(String msg) {
		super(msg);
	}

}
