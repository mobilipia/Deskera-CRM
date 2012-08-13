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
 * <a href="mb_005_wwww.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_005_wwww</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;

import java.util.Date;


public class mb_005_wwww implements Serializable {
		private String id;
		private String createdby;
		private Date createddate;
		private Date modifieddate;
		private Double deleteflag;
		private String text;
		private String mb_5_text1;
		private String time;
		private String mb_5_sfhgd;

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

		public String getText() {
                if(this.text == null) {
                      this.text="";
                }
                return this.text;
		}


		public void setText(String text) {
				if (

					(text == null && this.text != null) ||
					(text != null && this.text == null) ||
					(text != null && this.text != null && !text.equals(this.text))

				) {
					this.text = text;
				}
		}

		public String getMb_5_text1() {
                if(this.mb_5_text1 == null) {
                      this.mb_5_text1="";
                }
                return this.mb_5_text1;
		}


		public void setMb_5_text1(String mb_5_text1) {
				if (

					(mb_5_text1 == null && this.mb_5_text1 != null) ||
					(mb_5_text1 != null && this.mb_5_text1 == null) ||
					(mb_5_text1 != null && this.mb_5_text1 != null && !mb_5_text1.equals(this.mb_5_text1))

				) {
					this.mb_5_text1 = mb_5_text1;
				}
		}

		public String getTime() {
                if(this.time == null) {
                      this.time="";
                }
                return this.time;
		}


		public void setTime(String time) {
				if (

					(time == null && this.time != null) ||
					(time != null && this.time == null) ||
					(time != null && this.time != null && !time.equals(this.time))

				) {
					this.time = time;
				}
		}

		public String getMb_5_sfhgd() {
                if(this.mb_5_sfhgd == null) {
                      this.mb_5_sfhgd="";
                }
                return this.mb_5_sfhgd;
		}


		public void setMb_5_sfhgd(String mb_5_sfhgd) {
				if (

					(mb_5_sfhgd == null && this.mb_5_sfhgd != null) ||
					(mb_5_sfhgd != null && this.mb_5_sfhgd == null) ||
					(mb_5_sfhgd != null && this.mb_5_sfhgd != null && !mb_5_sfhgd.equals(this.mb_5_sfhgd))

				) {
					this.mb_5_sfhgd = mb_5_sfhgd;
				}
		}
}
