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
 * <a href="mb_007_fysalesorder.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_007_fysalesorder</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;

import java.util.Date;


public class mb_007_fysalesorder implements Serializable {
		private String id;
		private String createdby;
		private Date createddate;
		private Date modifieddate;
		private Double deleteflag;
		private String mb_7_so_number;
		private mb_008_fycustomer mb_8_customer_name00;

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

		public String getMb_7_so_number() {
                if(this.mb_7_so_number == null) {
                      this.mb_7_so_number="";
                }
                return this.mb_7_so_number;
		}


		public void setMb_7_so_number(String mb_7_so_number) {
				if (

					(mb_7_so_number == null && this.mb_7_so_number != null) ||
					(mb_7_so_number != null && this.mb_7_so_number == null) ||
					(mb_7_so_number != null && this.mb_7_so_number != null && !mb_7_so_number.equals(this.mb_7_so_number))

				) {
					this.mb_7_so_number = mb_7_so_number;
				}
		}

		public mb_008_fycustomer getMb_8_customer_name00() {
				return this.mb_8_customer_name00;
		}


		public void setMb_8_customer_name00(mb_008_fycustomer mb_8_customer_name00) {
				if (

					mb_8_customer_name00 != this.mb_8_customer_name00

				) {
					this.mb_8_customer_name00 = mb_8_customer_name00;
				}
		}
}
