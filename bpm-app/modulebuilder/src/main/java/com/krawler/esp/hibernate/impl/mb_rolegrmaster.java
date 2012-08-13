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
package com.krawler.esp.hibernate.impl;

/**
 * <a href="mb_rolegrmasterModel.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_rolegrmaster</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;


public class mb_rolegrmaster implements Serializable {
		private String description;
		private String groupname;
		private int groupid;

		public String getDescription() {
				return description;
		}


		public void setDescription(String description) {
				if (

					(description == null && this.description != null) ||
					(description != null && this.description == null) ||
					(description != null && this.description != null && !description.equals(this.description))

				) {
					this.description = description;
				}
		}

		public String getGroupname() {
				return groupname;
		}


		public void setGroupname(String groupname) {
				if (

					(groupname == null && this.groupname != null) ||
					(groupname != null && this.groupname == null) ||
					(groupname != null && this.groupname != null && !groupname.equals(this.groupname))

				) {
					this.groupname = groupname;
				}
		}

		public int getGroupid() {
				return groupid;
		}


		public void setGroupid(int groupid) {
				if (

					groupid != this.groupid

				) {
					this.groupid = groupid;
				}
		}
}
