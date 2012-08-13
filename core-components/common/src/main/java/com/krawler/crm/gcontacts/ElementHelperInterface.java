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

package com.krawler.crm.gcontacts;

import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;

import java.io.PrintStream;

/**
 * Interface to define the common methods of the {@link ElementHelper}.
 * 
 * 
 */
public interface ElementHelperInterface {

  /**
   * Parses an element from the textual description, and sets or adds it to
   * the contact entry.
   * 
   * @param contact  the contact the parsed element should be added or set. 
   * @param parser   the parser used for the parsing of the description.
   * 
   * @see ElementParser
   */
  public void parse(ContactEntry contact, ElementParser parser); 
  
  /**
   * Parses an element from the textual description, and sets or adds it to
   * the group entry.
   * 
   * @param group  the group the parsed element should be added or set. 
   * @param parser the parser used for the parsing of the description.
   * 
   * @throws UnsupportedOperationException in case the specific element
   *         can not be set on a ContactGroupEntry.
   * 
   * @see ElementParser
   */
  public void parseGroup(ContactGroupEntry group, ElementParser parser); 

  /**
   * Prints the content of the element to a stream.
   * 
   * @param out      output stream.
   * @param contact  the contact containing the element to print.
   */
  public void print(PrintStream out, ContactEntry contact); 
  
  /**
   * Updates element of destination contact with data from source contact.
   * If the source contact entry does not has the specific element, it should
   * leave the destination contact entry as is, otherwise is should copy the
   * element from the source to the destination contact entry.
   *  
   * @param dest  the destination contact entry.
   * @param src   the source contact entry.
   */
  public void update(ContactEntry dest, ContactEntry src);

  /**
   * Returns the usage help text regarding the formating of an element
   * description.
   * 
   * @return the usage help text for the element description.
   */
  public String getUsage();

}
