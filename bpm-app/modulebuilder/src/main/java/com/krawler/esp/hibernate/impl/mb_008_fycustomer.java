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
 * <a href="mb_008_fycustomer.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_008_fycustomer</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;

import java.util.Date;


public class mb_008_fycustomer implements Serializable {
		private String id;
		private String createdby;
		private Date createddate;
		private Date modifieddate;
		private Double deleteflag;
		private String mb_8_customer_id;
		private String mb_8_customer_name;
		private String mb_8_email;
		private String mb_8_contact;
		private mb_configmasterdata mb_8_payment_method;
		private Double mb_8_payment_term;
		private String mb_8_shipping_address;
		private String mb_8_billing_address;

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

		public String getMb_8_customer_id() {
                if(this.mb_8_customer_id == null) {
                      this.mb_8_customer_id="";
                }
                return this.mb_8_customer_id;
		}


		public void setMb_8_customer_id(String mb_8_customer_id) {
				if (

					(mb_8_customer_id == null && this.mb_8_customer_id != null) ||
					(mb_8_customer_id != null && this.mb_8_customer_id == null) ||
					(mb_8_customer_id != null && this.mb_8_customer_id != null && !mb_8_customer_id.equals(this.mb_8_customer_id))

				) {
					this.mb_8_customer_id = mb_8_customer_id;
				}
		}

		public String getMb_8_customer_name() {
                if(this.mb_8_customer_name == null) {
                      this.mb_8_customer_name="";
                }
                return this.mb_8_customer_name;
		}


		public void setMb_8_customer_name(String mb_8_customer_name) {
				if (

					(mb_8_customer_name == null && this.mb_8_customer_name != null) ||
					(mb_8_customer_name != null && this.mb_8_customer_name == null) ||
					(mb_8_customer_name != null && this.mb_8_customer_name != null && !mb_8_customer_name.equals(this.mb_8_customer_name))

				) {
					this.mb_8_customer_name = mb_8_customer_name;
				}
		}

		public String getMb_8_email() {
                if(this.mb_8_email == null) {
                      this.mb_8_email="";
                }
                return this.mb_8_email;
		}


		public void setMb_8_email(String mb_8_email) {
				if (

					(mb_8_email == null && this.mb_8_email != null) ||
					(mb_8_email != null && this.mb_8_email == null) ||
					(mb_8_email != null && this.mb_8_email != null && !mb_8_email.equals(this.mb_8_email))

				) {
					this.mb_8_email = mb_8_email;
				}
		}

		public String getMb_8_contact() {
                if(this.mb_8_contact == null) {
                      this.mb_8_contact="";
                }
                return this.mb_8_contact;
		}


		public void setMb_8_contact(String mb_8_contact) {
				if (

					(mb_8_contact == null && this.mb_8_contact != null) ||
					(mb_8_contact != null && this.mb_8_contact == null) ||
					(mb_8_contact != null && this.mb_8_contact != null && !mb_8_contact.equals(this.mb_8_contact))

				) {
					this.mb_8_contact = mb_8_contact;
				}
		}

		public mb_configmasterdata getMb_8_payment_method() {
				return this.mb_8_payment_method;
		}


		public void setMb_8_payment_method(mb_configmasterdata mb_8_payment_method) {
				if (

					mb_8_payment_method != this.mb_8_payment_method

				) {
					this.mb_8_payment_method = mb_8_payment_method;
				}
		}

		public Double getMb_8_payment_term() {
				return this.mb_8_payment_term;
		}


		public void setMb_8_payment_term(Double mb_8_payment_term) {
				if (

					mb_8_payment_term != this.mb_8_payment_term

				) {
					this.mb_8_payment_term = mb_8_payment_term;
				}
		}

		public String getMb_8_shipping_address() {
                if(this.mb_8_shipping_address == null) {
                      this.mb_8_shipping_address="";
                }
                return this.mb_8_shipping_address;
		}


		public void setMb_8_shipping_address(String mb_8_shipping_address) {
				if (

					(mb_8_shipping_address == null && this.mb_8_shipping_address != null) ||
					(mb_8_shipping_address != null && this.mb_8_shipping_address == null) ||
					(mb_8_shipping_address != null && this.mb_8_shipping_address != null && !mb_8_shipping_address.equals(this.mb_8_shipping_address))

				) {
					this.mb_8_shipping_address = mb_8_shipping_address;
				}
		}

		public String getMb_8_billing_address() {
                if(this.mb_8_billing_address == null) {
                      this.mb_8_billing_address="";
                }
                return this.mb_8_billing_address;
		}


		public void setMb_8_billing_address(String mb_8_billing_address) {
				if (

					(mb_8_billing_address == null && this.mb_8_billing_address != null) ||
					(mb_8_billing_address != null && this.mb_8_billing_address == null) ||
					(mb_8_billing_address != null && this.mb_8_billing_address != null && !mb_8_billing_address.equals(this.mb_8_billing_address))

				) {
					this.mb_8_billing_address = mb_8_billing_address;
				}
		}
}
