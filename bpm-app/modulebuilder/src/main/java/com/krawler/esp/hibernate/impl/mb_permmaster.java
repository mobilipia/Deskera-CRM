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
 * <a href="mb_permmasterModel.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_permmaster</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;


public class mb_permmaster implements Serializable {
		private String description;
		private String permname;
                private String id;
		private int permid;
                private mb_permactions permaction;
                private mb_permgrmaster permgrid;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public mb_permgrmaster getPermgrid() {
                    return permgrid;
                }

                public void setPermgrid(mb_permgrmaster permgrid) {
                    this.permgrid = permgrid;
                }

                public mb_permactions getPermaction() {
                    return permaction;
                }

                public void setPermaction(mb_permactions permaction) {
                    this.permaction = permaction;
                }

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

		public String getPermname() {
				return permname;
		}


		public void setPermname(String permname) {
				if (

					(permname == null && this.permname != null) ||
					(permname != null && this.permname == null) ||
					(permname != null && this.permname != null && !permname.equals(this.permname))

				) {
					this.permname = permname;
				}
		}

		public int getPermid() {
				return permid;
		}


		public void setPermid(int permid) {
				if (

					permid != this.permid

				) {
					this.permid = permid;
				}
		}
}
