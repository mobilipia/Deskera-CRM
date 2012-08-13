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

package com.krawler.br;

/**
 * exeception class to be used when the process have some execeptional cases
 * @author Vishnu Kant Gupta
 */
public class ProcessException extends Exception {

    @Override
    public String toString() {
        return super.toString();
    }

    public ProcessException(Throwable cause) {
        super(cause);
    }

    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessException(String message) {
        super(message);
    }

    public ProcessException() {
        super();
    }
}
