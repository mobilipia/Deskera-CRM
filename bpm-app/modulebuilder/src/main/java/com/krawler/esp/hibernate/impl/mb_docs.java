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
 * <a href="mb_docsModel.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_docs</code>
 * table in the database.
 * </p>
 *
 */
import com.krawler.common.admin.User;
import java.io.Serializable;


public class mb_docs implements Serializable {
		private String docid;
		private String docname;
		private String docsize;
		private String doctype;
		private java.util.Date uploadedon;
		private String storename;
		private int storageindex;
		private User userid;

		public String getDocid() {
                    if(this.docid == null) {
                        this.docid="";
                    }
                    return this.docid;
		}


		public void setDocid(String docid) {
				if (

					(docid == null && this.docid != null) ||
					(docid != null && this.docid == null) ||
					(docid != null && this.docid != null && !docid.equals(this.docid))

				) {
					this.docid = docid;
				}
		}

		public String getDocname() {
                    if(this.docname == null) {
                        this.docname="";
                    }
                    return this.docname;
		}


		public void setDocname(String docname) {
				if (

					(docname == null && this.docname != null) ||
					(docname != null && this.docname == null) ||
					(docname != null && this.docname != null && !docname.equals(this.docname))

				) {
					this.docname = docname;
				}
		}

		public String getDocsize() {
                    if(this.docsize == null) {
                        this.docsize="";
                    }
                    return this.docsize;
		}


		public void setDocsize(String docsize) {
				if (

					(docsize == null && this.docsize != null) ||
					(docsize != null && this.docsize == null) ||
					(docsize != null && this.docsize != null && !docsize.equals(this.docsize))

				) {
					this.docsize = docsize;
				}
		}

		public String getDoctype() {
                    if(this.doctype == null) {
                        this.doctype="";
                    }
                    return this.doctype;
		}


		public void setDoctype(String doctype) {
				if (

					(doctype == null && this.doctype != null) ||
					(doctype != null && this.doctype == null) ||
					(doctype != null && this.doctype != null && !doctype.equals(this.doctype))

				) {
					this.doctype = doctype;
				}
		}

		public java.util.Date getUploadedon() {
				return this.uploadedon;
		}


		public void setUploadedon(java.util.Date uploadedon) {
				if (

					uploadedon != this.uploadedon

				) {
					this.uploadedon = uploadedon;
				}
		}

		public String getStorename() {
                    if(this.storename == null) {
                        this.storename="";
                    }
                    return this.storename;
		}


		public void setStorename(String storename) {
				if (

					(storename == null && this.storename != null) ||
					(storename != null && this.storename == null) ||
					(storename != null && this.storename != null && !storename.equals(this.storename))

				) {
					this.storename = storename;
				}
		}

		public int getStorageindex() {
				return this.storageindex;
		}


		public void setStorageindex(int storageindex) {
				if (

					storageindex != this.storageindex

				) {
					this.storageindex = storageindex;
				}
		}

		public User getUserid() {
				return this.userid;
		}


		public void setUserid(User userid) {
				if (

					userid != this.userid

				) {
					this.userid = userid;
				}
		}
}
