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
 * <a href="mb_rolemasterModel.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_rolemaster</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;


public class mb_rolemaster implements Serializable {
		private String description;
		private String rolename;
		private mb_rolegrmaster groupid;
		private int roleid;

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

		public String getRolename() {
				return rolename;
		}


		public void setRolename(String rolename) {
				if (

					(rolename == null && this.rolename != null) ||
					(rolename != null && this.rolename == null) ||
					(rolename != null && this.rolename != null && !rolename.equals(this.rolename))

				) {
					this.rolename = rolename;
				}
		}

		public mb_rolegrmaster getGroupid() {
				return groupid;
		}


		public void setGroupid(mb_rolegrmaster groupid) {
				if (

					groupid != this.groupid

				) {
					this.groupid = groupid;
				}
		}

		public int getRoleid() {
				return roleid;
		}


		public void setRoleid(int roleid) {
				if (

					roleid != this.roleid

				) {
					this.roleid = roleid;
				}
		}
}
