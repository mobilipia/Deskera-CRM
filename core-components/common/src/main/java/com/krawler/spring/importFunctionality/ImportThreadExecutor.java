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
package com.krawler.spring.importFunctionality;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.krawler.esp.utils.ConfigReader;

/**
 * @author krawler
 *
 */
public class ImportThreadExecutor {

	private static final String THREAD_POOL_SIZE = "ImportFileThreadPoolSize";
	
	ExecutorService threadExecutor = null;

	public ImportThreadExecutor() {
		super();
		threadExecutor = Executors.newFixedThreadPool(ConfigReader.getinstance().getInt("ImportFileThreadPoolSize", 1));
	}

	/**
	 * Ideally designed to execute a thread to import files, though it can be
	 * used to execute any runnable object.
	 * 
	 * @param command
	 */
	public void startThread(Runnable command) {
		threadExecutor.execute(command);
	}

}
