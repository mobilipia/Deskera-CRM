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

import java.io.Serializable;

public class mb_buttonConf implements Serializable {
        private String buttonid;
        private String caption;
        private String functext;
        private String buttontype;
        private String toolbartype;
        private mb_reportlist reportid;

        public String getButtontype(){
            return this.buttontype;
        }
        public void setButtontype(String type){
            if ((type == null && this.buttontype != null) ||
					(type != null && this.buttontype == null) ||
					(type != null && this.buttontype != null && !type.equals(this.buttontype))){
                this.buttontype = type;
            }
        }
        public String getToolbartype(){
            return this.toolbartype;
        }
        public void setToolbartype(String type){
            if ((type == null && this.toolbartype != null) ||
					(type != null && this.toolbartype == null) ||
					(type != null && this.toolbartype != null && !type.equals(this.toolbartype))){
                this.toolbartype = type;
            }
        }
        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            if (
					(caption == null && this.caption != null) ||
					(caption != null && this.caption == null) ||
					(caption != null && this.caption != null && !caption.equals(this.caption))){
                this.caption = caption;
            }
        }

        public mb_reportlist getReportid() {
            return reportid;
        }

        public void setReportid(mb_reportlist refid) {
            this.reportid = refid;
        }

		public String getFunctext() {
				return functext;
		}


		public void setFunctext(String functext) {
            if (
					(functext == null && this.functext != null) ||
					(functext != null && this.functext == null) ||
					(functext != null && this.functext != null && !functext.equals(this.functext)))
            {
                this.functext = functext;
            }
		}

		public String getButtonid() {
				return this.buttonid;
		}


		public void setButtonid(String id) {
				if (

					(id == null && this.buttonid != null) ||
					(id != null && this.buttonid == null) ||
					(id != null && this.buttonid != null && !id.equals(this.buttonid))

				) {
					this.buttonid = id;
				}
		}
}
