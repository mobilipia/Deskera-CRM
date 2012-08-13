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

import java.util.HashMap;
import java.util.Map;

import com.krawler.notify.factory.SenderFactory;

public class SenderCache<E extends NotificationSender> {
	private Map<String,E> senders = new HashMap<String, E>();
	private SenderFactory<? extends E> factory;
	
	public SenderCache(Map<String, E> senders) {
		this.senders = senders;
	}

	public void setFactory(SenderFactory<? extends E> factory) {
		this.factory = factory;
	}

	public void addSender(String key, E sender){
		senders.put(key, sender);
	}
	
	public E removeSender(String key){
		return senders.remove(key);
	}
	
	public boolean containsSender(String key){
		return senders.containsKey(key);
	}
	
	public E getSender(String key){
		return senders.get(key);
	}

	public E getSender(String key, Map<String, Object> settings, String userName, String password){
		synchronized (senders) {
			E sender= senders.get(key);
			if(sender==null){
				sender = factory.createSender(settings, userName, password);
				senders.put(key, sender);
			}	
			return sender;
		}
	}
	
	public E createSender() {
		E sender = null; 
		
		return sender;
	}
}
