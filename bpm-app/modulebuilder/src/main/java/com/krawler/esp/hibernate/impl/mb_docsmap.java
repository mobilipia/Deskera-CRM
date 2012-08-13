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
 * <a href="mb_docsmapModel.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_docsmap</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;


public class mb_docsmap implements Serializable {
		private String id;
		private String recid;
		private String reftable;
		private mb_docs docid;

		public String getId() {
                    if(this.id == null) {
                        this.id="";
                    }
                    return this.id;
		}


		public void setId(String id) {
				if (

					(id == null && this.id != null) ||
					(id != null && this.id == null) ||
					(id != null && this.id != null && !id.equals(this.id))

				) {
					this.id = id;
				}
		}

		public String getRecid() {
                    if(this.recid == null) {
                        this.recid="";
                    }
                    return this.recid;
		}


		public void setRecid(String recid) {
				if (

					(recid == null && this.recid != null) ||
					(recid != null && this.recid == null) ||
					(recid != null && this.recid != null && !recid.equals(this.recid))

				) {
					this.recid = recid;
				}
		}

		public String getReftable() {
                    if(this.reftable == null) {
                        this.reftable="";
                    }
                    return this.reftable;
		}


		public void setReftable(String reftable) {
				if (

					(reftable == null && this.reftable != null) ||
					(reftable != null && this.reftable == null) ||
					(reftable != null && this.reftable != null && !reftable.equals(this.reftable))

				) {
					this.reftable = reftable;
				}
		}

		public mb_docs getDocid() {
				return this.docid;
		}


		public void setDocid(mb_docs docid) {
				if (

					docid != this.docid

				) {
					this.docid = docid;
				}
		}
}
