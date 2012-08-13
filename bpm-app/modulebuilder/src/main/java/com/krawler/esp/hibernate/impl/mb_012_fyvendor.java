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
 * <a href="mb_012_fyvendor.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_012_fyvendor</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;

import java.util.Date;


public class mb_012_fyvendor implements Serializable {
		private String id;
		private String createdby;
		private Date createddate;
		private Date modifieddate;
		private Double deleteflag;
		private String mb_12_vendor_id;
		private String mb_12_vendor_name;
		private String mb_12_email;
		private String mb_12_contact;
		private String mb_12_address;
		private Double mb_12_payment_term;

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

		public String getCreatedby() {
                if(this.createdby == null) {
                      this.createdby="";
                }
                return this.createdby;
		}


		public void setCreatedby(String createdby) {
				if (

					(createdby == null && this.createdby != null) ||
					(createdby != null && this.createdby == null) ||
					(createdby != null && this.createdby != null && !createdby.equals(this.createdby))

				) {
					this.createdby = createdby;
				}
		}

		public Date getCreateddate() {
				return this.createddate;
		}


		public void setCreateddate(Date createddate) {
				if (

					(createddate == null && this.createddate != null) ||
					(createddate != null && this.createddate == null) ||
					(createddate != null && this.createddate != null && !createddate.equals(this.createddate))

				) {
					this.createddate = createddate;
				}
		}

		public Date getModifieddate() {
				return this.modifieddate;
		}


		public void setModifieddate(Date modifieddate) {
				if (

					(modifieddate == null && this.modifieddate != null) ||
					(modifieddate != null && this.modifieddate == null) ||
					(modifieddate != null && this.modifieddate != null && !modifieddate.equals(this.modifieddate))

				) {
					this.modifieddate = modifieddate;
				}
		}

		public Double getDeleteflag() {
				return this.deleteflag;
		}


		public void setDeleteflag(Double deleteflag) {
				if (

					deleteflag != this.deleteflag

				) {
					this.deleteflag = deleteflag;
				}
		}

		public String getMb_12_vendor_id() {
                if(this.mb_12_vendor_id == null) {
                      this.mb_12_vendor_id="";
                }
                return this.mb_12_vendor_id;
		}


		public void setMb_12_vendor_id(String mb_12_vendor_id) {
				if (

					(mb_12_vendor_id == null && this.mb_12_vendor_id != null) ||
					(mb_12_vendor_id != null && this.mb_12_vendor_id == null) ||
					(mb_12_vendor_id != null && this.mb_12_vendor_id != null && !mb_12_vendor_id.equals(this.mb_12_vendor_id))

				) {
					this.mb_12_vendor_id = mb_12_vendor_id;
				}
		}

		public String getMb_12_vendor_name() {
                if(this.mb_12_vendor_name == null) {
                      this.mb_12_vendor_name="";
                }
                return this.mb_12_vendor_name;
		}


		public void setMb_12_vendor_name(String mb_12_vendor_name) {
				if (

					(mb_12_vendor_name == null && this.mb_12_vendor_name != null) ||
					(mb_12_vendor_name != null && this.mb_12_vendor_name == null) ||
					(mb_12_vendor_name != null && this.mb_12_vendor_name != null && !mb_12_vendor_name.equals(this.mb_12_vendor_name))

				) {
					this.mb_12_vendor_name = mb_12_vendor_name;
				}
		}

		public String getMb_12_email() {
                if(this.mb_12_email == null) {
                      this.mb_12_email="";
                }
                return this.mb_12_email;
		}


		public void setMb_12_email(String mb_12_email) {
				if (

					(mb_12_email == null && this.mb_12_email != null) ||
					(mb_12_email != null && this.mb_12_email == null) ||
					(mb_12_email != null && this.mb_12_email != null && !mb_12_email.equals(this.mb_12_email))

				) {
					this.mb_12_email = mb_12_email;
				}
		}

		public String getMb_12_contact() {
                if(this.mb_12_contact == null) {
                      this.mb_12_contact="";
                }
                return this.mb_12_contact;
		}


		public void setMb_12_contact(String mb_12_contact) {
				if (

					(mb_12_contact == null && this.mb_12_contact != null) ||
					(mb_12_contact != null && this.mb_12_contact == null) ||
					(mb_12_contact != null && this.mb_12_contact != null && !mb_12_contact.equals(this.mb_12_contact))

				) {
					this.mb_12_contact = mb_12_contact;
				}
		}

		public String getMb_12_address() {
                if(this.mb_12_address == null) {
                      this.mb_12_address="";
                }
                return this.mb_12_address;
		}


		public void setMb_12_address(String mb_12_address) {
				if (

					(mb_12_address == null && this.mb_12_address != null) ||
					(mb_12_address != null && this.mb_12_address == null) ||
					(mb_12_address != null && this.mb_12_address != null && !mb_12_address.equals(this.mb_12_address))

				) {
					this.mb_12_address = mb_12_address;
				}
		}

		public Double getMb_12_payment_term() {
				return this.mb_12_payment_term;
		}


		public void setMb_12_payment_term(Double mb_12_payment_term) {
				if (

					mb_12_payment_term != this.mb_12_payment_term

				) {
					this.mb_12_payment_term = mb_12_payment_term;
				}
		}
}
