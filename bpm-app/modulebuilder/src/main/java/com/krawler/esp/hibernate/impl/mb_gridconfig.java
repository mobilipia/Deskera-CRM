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
 * <a href="mb_gridconfigModel.java.html"><b><i>View Source</i></b></a>
 *
 * <p>
 * ServiceBuilder generated this class. Modifications in this class will be
 * overwritten the next time is generated.
 * </p>
 *
 * <p>
 * This interface is a model that represents the <code>mb_gridconfig</code>
 * table in the database.
 * </p>
 *
 */
import java.io.Serializable;


public class mb_gridconfig implements Serializable {
        private int columnindex;
        private boolean hidden;
        private boolean countflag;
        private renderer renderer;
        private String reftable;
        private String xtype;
        private String displayfield;
        private String name;
        private String filter;
        private mb_reportlist reportid;
        private String id;
        private String combogridconfig;
        private String summaryType;
        private String defaultValue;

        public String getSummaryType() {
            if (this.summaryType == null) {
                this.summaryType = "";
            }
            return this.summaryType;
        }

        public void setSummaryType(String summaryType) {
            this.summaryType = summaryType;
        }

        public String getCombogridconfig() {
            return combogridconfig;
        }

        public void setCombogridconfig(String combogridconfig) {
            this.combogridconfig = combogridconfig;
        }

        public boolean isCountflag() {
            return countflag;
        }

        public void setCountflag(boolean countflag) {
            if (

					countflag != this.countflag

				) {
					this.countflag = countflag;
				}
        }

        public mb_reportlist getReportid() {
            return reportid;
        }

        public void setReportid(mb_reportlist reportid) {
            this.reportid = reportid;
        }

		public int getColumnindex() {
				return columnindex;
		}


		public void setColumnindex(int columnindex) {
				if (

					columnindex != this.columnindex

				) {
					this.columnindex = columnindex;
				}
		}

		public boolean getHidden() {
				return hidden;
		}

			public boolean isHidden() {
				return hidden;
			}

		public void setHidden(boolean hidden) {
				if (

					hidden != this.hidden

				) {
					this.hidden = hidden;
				}
		}

		public renderer getRenderer() {
				return renderer;
		}


		public void setRenderer(renderer renderer) {
				if (

					(renderer == null && this.renderer != null) ||
					(renderer != null && this.renderer == null) ||
					(renderer != null && this.renderer != null && !renderer.equals(this.renderer))

				) {
					this.renderer = renderer;
				}
		}

		public String getReftable() {
				return reftable;
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

		public String getXtype() {
				return xtype;
		}


		public void setXtype(String xtype) {
				if (

					(xtype == null && this.xtype != null) ||
					(xtype != null && this.xtype == null) ||
					(xtype != null && this.xtype != null && !xtype.equals(this.xtype))

				) {
					this.xtype = xtype;
				}
		}

		public String getDisplayfield() {
				return displayfield;
		}


		public void setDisplayfield(String displayfield) {
				if (

					(displayfield == null && this.displayfield != null) ||
					(displayfield != null && this.displayfield == null) ||
					(displayfield != null && this.displayfield != null && !displayfield.equals(this.displayfield))

				) {
					this.displayfield = displayfield;
				}
		}

		public String getName() {
				return name;
		}


		public void setName(String name) {
				if (

					(name == null && this.name != null) ||
					(name != null && this.name == null) ||
					(name != null && this.name != null && !name.equals(this.name))

				) {
					this.name = name;
				}
		}		

		public String getId() {
				return id;
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
        public String getFilter() {
				return filter;
		}


		public void setFilter(String filter) {
				if (

					(filter == null && this.filter != null) ||
					(filter != null && this.filter == null) ||
					(filter != null && this.filter != null && !filter.equals(this.filter))

				) {
					this.filter = filter;
				}
		}

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
}
