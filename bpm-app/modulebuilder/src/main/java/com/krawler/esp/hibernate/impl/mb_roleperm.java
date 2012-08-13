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
 * <a href="mb_rolepermModel.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_roleperm</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;


public class mb_roleperm implements Serializable {
		private mb_permgrmaster permgrid;
		private mb_rolemaster roleid;
		private int id;
        private int permvalview;
        private int permvaledit;

    public mb_permgrmaster getPermgrid() {
        return permgrid;
    }

    public void setPermgrid(mb_permgrmaster permgrid) {
        this.permgrid = permgrid;
    }

		

    public int getPermvaledit() {
        return permvaledit;
    }

    public void setPermvaledit(int permvaledit) {
        this.permvaledit = permvaledit;
    }

    public int getPermvalview() {
        return permvalview;
    }

    public void setPermvalview(int permvalview) {
        this.permvalview = permvalview;
    }


		
		public mb_rolemaster getRoleid() {
				return roleid;
		}


		public void setRoleid(mb_rolemaster roleid) {
				if (

					roleid != this.roleid

				) {
					this.roleid = roleid;
				}
		}

		public int getId() {
				return id;
		}


		public void setId(int id) {
				if (

					id != this.id

				) {
					this.id = id;
				}
		}
}
