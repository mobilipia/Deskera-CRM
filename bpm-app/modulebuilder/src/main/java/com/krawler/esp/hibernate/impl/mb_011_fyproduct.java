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
 * <a href="mb_011_fyproduct.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_011_fyproduct</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;

import java.util.Date;


public class mb_011_fyproduct implements Serializable {
		private String id;
		private String createdby;
		private Date createddate;
		private Date modifieddate;
		private Double deleteflag;
		private String mb_11_product_name;
		private String mb_11_product_id;
		private String mb_11_bar_code;
		private String mb_11_product_image;
		private Double mb_11_purchase_price;
		private Double mb_11_discount;
		private Double mb_11_profit_margin;
		private Double mb_11_sales_price;
		private String mb_11_description;
		private Double mb_11_reorder_level;
		private Double mb_11_reorder_qty;
		private Double mb_11_cc_interval;
		private Double mb_11_cc_tolerance;
		private mb_012_fyvendor mb_12_vendor_name00;
		private mb_012_fyvendor mb_12_vendor_name01;

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

		public String getMb_11_product_name() {
                if(this.mb_11_product_name == null) {
                      this.mb_11_product_name="";
                }
                return this.mb_11_product_name;
		}


		public void setMb_11_product_name(String mb_11_product_name) {
				if (

					(mb_11_product_name == null && this.mb_11_product_name != null) ||
					(mb_11_product_name != null && this.mb_11_product_name == null) ||
					(mb_11_product_name != null && this.mb_11_product_name != null && !mb_11_product_name.equals(this.mb_11_product_name))

				) {
					this.mb_11_product_name = mb_11_product_name;
				}
		}

		public String getMb_11_product_id() {
                if(this.mb_11_product_id == null) {
                      this.mb_11_product_id="";
                }
                return this.mb_11_product_id;
		}


		public void setMb_11_product_id(String mb_11_product_id) {
				if (

					(mb_11_product_id == null && this.mb_11_product_id != null) ||
					(mb_11_product_id != null && this.mb_11_product_id == null) ||
					(mb_11_product_id != null && this.mb_11_product_id != null && !mb_11_product_id.equals(this.mb_11_product_id))

				) {
					this.mb_11_product_id = mb_11_product_id;
				}
		}

		public String getMb_11_bar_code() {
                if(this.mb_11_bar_code == null) {
                      this.mb_11_bar_code="";
                }
                return this.mb_11_bar_code;
		}


		public void setMb_11_bar_code(String mb_11_bar_code) {
				if (

					(mb_11_bar_code == null && this.mb_11_bar_code != null) ||
					(mb_11_bar_code != null && this.mb_11_bar_code == null) ||
					(mb_11_bar_code != null && this.mb_11_bar_code != null && !mb_11_bar_code.equals(this.mb_11_bar_code))

				) {
					this.mb_11_bar_code = mb_11_bar_code;
				}
		}

		public String getMb_11_product_image() {
                if(this.mb_11_product_image == null) {
                      this.mb_11_product_image="";
                }
                return this.mb_11_product_image;
		}


		public void setMb_11_product_image(String mb_11_product_image) {
				if (

					(mb_11_product_image == null && this.mb_11_product_image != null) ||
					(mb_11_product_image != null && this.mb_11_product_image == null) ||
					(mb_11_product_image != null && this.mb_11_product_image != null && !mb_11_product_image.equals(this.mb_11_product_image))

				) {
					this.mb_11_product_image = mb_11_product_image;
				}
		}

		public Double getMb_11_purchase_price() {
				return this.mb_11_purchase_price;
		}


		public void setMb_11_purchase_price(Double mb_11_purchase_price) {
				if (

					mb_11_purchase_price != this.mb_11_purchase_price

				) {
					this.mb_11_purchase_price = mb_11_purchase_price;
				}
		}

		public Double getMb_11_discount() {
				return this.mb_11_discount;
		}


		public void setMb_11_discount(Double mb_11_discount) {
				if (

					mb_11_discount != this.mb_11_discount

				) {
					this.mb_11_discount = mb_11_discount;
				}
		}

		public Double getMb_11_profit_margin() {
				return this.mb_11_profit_margin;
		}


		public void setMb_11_profit_margin(Double mb_11_profit_margin) {
				if (

					mb_11_profit_margin != this.mb_11_profit_margin

				) {
					this.mb_11_profit_margin = mb_11_profit_margin;
				}
		}

		public Double getMb_11_sales_price() {
				return this.mb_11_sales_price;
		}


		public void setMb_11_sales_price(Double mb_11_sales_price) {
				if (

					mb_11_sales_price != this.mb_11_sales_price

				) {
					this.mb_11_sales_price = mb_11_sales_price;
				}
		}

		public String getMb_11_description() {
                if(this.mb_11_description == null) {
                      this.mb_11_description="";
                }
                return this.mb_11_description;
		}


		public void setMb_11_description(String mb_11_description) {
				if (

					(mb_11_description == null && this.mb_11_description != null) ||
					(mb_11_description != null && this.mb_11_description == null) ||
					(mb_11_description != null && this.mb_11_description != null && !mb_11_description.equals(this.mb_11_description))

				) {
					this.mb_11_description = mb_11_description;
				}
		}

		public Double getMb_11_reorder_level() {
				return this.mb_11_reorder_level;
		}


		public void setMb_11_reorder_level(Double mb_11_reorder_level) {
				if (

					mb_11_reorder_level != this.mb_11_reorder_level

				) {
					this.mb_11_reorder_level = mb_11_reorder_level;
				}
		}

		public Double getMb_11_reorder_qty() {
				return this.mb_11_reorder_qty;
		}


		public void setMb_11_reorder_qty(Double mb_11_reorder_qty) {
				if (

					mb_11_reorder_qty != this.mb_11_reorder_qty

				) {
					this.mb_11_reorder_qty = mb_11_reorder_qty;
				}
		}

		public Double getMb_11_cc_interval() {
				return this.mb_11_cc_interval;
		}


		public void setMb_11_cc_interval(Double mb_11_cc_interval) {
				if (

					mb_11_cc_interval != this.mb_11_cc_interval

				) {
					this.mb_11_cc_interval = mb_11_cc_interval;
				}
		}

		public Double getMb_11_cc_tolerance() {
				return this.mb_11_cc_tolerance;
		}


		public void setMb_11_cc_tolerance(Double mb_11_cc_tolerance) {
				if (

					mb_11_cc_tolerance != this.mb_11_cc_tolerance

				) {
					this.mb_11_cc_tolerance = mb_11_cc_tolerance;
				}
		}

		public mb_012_fyvendor getMb_12_vendor_name00() {
				return this.mb_12_vendor_name00;
		}


		public void setMb_12_vendor_name00(mb_012_fyvendor mb_12_vendor_name00) {
				if (

					mb_12_vendor_name00 != this.mb_12_vendor_name00

				) {
					this.mb_12_vendor_name00 = mb_12_vendor_name00;
				}
		}

		public mb_012_fyvendor getMb_12_vendor_name01() {
				return this.mb_12_vendor_name01;
		}


		public void setMb_12_vendor_name01(mb_012_fyvendor mb_12_vendor_name01) {
				if (

					mb_12_vendor_name01 != this.mb_12_vendor_name01

				) {
					this.mb_12_vendor_name01 = mb_12_vendor_name01;
				}
		}
}
