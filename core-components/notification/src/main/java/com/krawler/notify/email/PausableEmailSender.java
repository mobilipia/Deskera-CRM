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
package com.krawler.notify.email;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.mail.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.krawler.notify.NotificationException;

public class PausableEmailSender extends SimpleEmailSender {
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition unpaused = pauseLock.newCondition();
	private long pauseTime = 0;
	private long pauseSize = 0;
	private long pauseCounter = 0;
	private Log logger = LogFactory.getLog(PausableEmailSender.class);
	private boolean paused = false;

	public void setPauseTime(long pauseTime) {
		this.pauseTime = pauseTime;
	}

	public void setPauseSize(long pauseSize) {
		this.pauseSize = pauseSize;
	}
	
	public boolean isPaused(){
		return paused;
	}

	@Override
	public void send(Message msg) throws NotificationException {
		pauseLock.lock();
		try {
			if (pauseSize > 0 && pauseCounter == pauseSize) {
				paused = true;
				logger.debug("Wait Start");
				unpaused.await(pauseTime, TimeUnit.MILLISECONDS);
				pauseCounter = 0;
				logger.debug("Wait End");
			}
		} catch (InterruptedException ie) {
			logger.warn("Pause Interrupted", ie);
			Thread.currentThread().interrupt();
		} finally {
			paused = false;
			pauseCounter++;
			pauseLock.unlock();
		}
		
		super.send(msg);		
	}
}
