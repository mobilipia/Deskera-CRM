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

import java.util.LinkedList;
import java.util.Queue;

public class MessageStore {
    //Message sent from producer to consumer.
    private Queue<MessageInfo> msgQueue=new LinkedList<MessageInfo>();
    private int limit =100;
    //True if consumer should wait for producer to send message, false
    //if producer should wait for consumer to retrieve message.


    public synchronized MessageInfo get() {
        //Wait until message is available.
        while (msgQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        MessageInfo msg = msgQueue.poll();
        //Toggle status.

        //Notify producer that status has changed.
        notifyAll();
        return msg;
    }

    public synchronized void put(MessageInfo message) {
        //Wait until message has been retrieved.
        while (msgQueue.size()>=limit) {
            try { 
                wait();
            } catch (InterruptedException e) {}
        }
        //Store message.
        msgQueue.add(message);
        //Notify consumer that status has changed.
        notifyAll();
    }

}
