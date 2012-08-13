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
package com.krawler.notify;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PausableThreadPoolExecutor extends ThreadPoolExecutor {
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition unpaused = pauseLock.newCondition();
	private long pauseTime = 0;
	private long pauseSize = 0;

	public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, long pauseTime, long pauseSize) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.pauseTime = pauseTime;
		this.pauseSize = pauseSize;
	}

	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		long c=super.getCompletedTaskCount();
		if (pauseSize > 0 && c > 0 && c % pauseSize == 0) {
			pauseLock.lock();
			try {
				System.err.println("Wait Start");
				unpaused.await(pauseTime, TimeUnit.MILLISECONDS);
				System.err.println("Wait End");
			} catch (InterruptedException ie) {
				t.interrupt();
			} finally {
				pauseLock.unlock();
			}
		}
	}
}
