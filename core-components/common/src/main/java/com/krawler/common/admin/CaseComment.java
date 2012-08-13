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
package com.krawler.common.admin;

import java.util.Date;

public class CaseComment {
	
	
	
	 private String comment;
	    private String Id;
	    private String userId;
	    //@@@ - Change fildname leadid
	    private String caseid;
	    private Long postedon;
	    private Long updatedon;
	    private String relatedto;
	    private boolean deleted;
	    private char userflag;
		
	    public CaseComment(){
	    }
	    
	    public CaseComment(String comment,String Id,String userId,String caseid , Long postedon, Long updatedon){	        
	        this.comment=comment;
	        this.Id=Id;
	        this.userId=userId;
	        this.caseid=caseid;
	        this.postedon = postedon;
	        this.updatedon = updatedon;
	    }
	    
	    public char getUserflag() {
			return userflag;
		}

		public void setUserflag(char userflag) {
			this.userflag = userflag;
		}

	    public boolean isDeleted() {
	        return deleted;
	    }

	    public void setDeleted(boolean deleted) {
	        this.deleted = deleted;
	    }

	    public String getComment() {
	        return this.comment;
	    }

	    public void setComment(String comment) {
	        this.comment = comment;
	    }

	    public String getId() {
	        return this.Id;
	    }
	    public void setId(String id) {
	        this.Id = id;
	    }
	    public String getuserId() {
	        return this.userId;
	    }
	    public void setuserId(String id) {
	        this.userId = id;
	    }
	    
	    public String getCaseid() {
			return caseid;
		}


		public void setCaseid(String caseid) {
			this.caseid = caseid;
		}


		public Long getPostedon() {
	        return this.postedon;
	    }

	    public void setPostedon(Long postedon) {
	        this.postedon = postedon;
	    }

	    public Long getUpdatedon() {
	        return updatedon;
	    }

	    public void setUpdatedon(Long updatedon) {
	        this.updatedon = updatedon;
	    }

	    public String getRelatedto() {
	        return this.relatedto;
	    }

	    public void setRelatedto(String relatedto) {
	        this.relatedto = relatedto;
	    }
	    public Date getPostedOn() {
	        if(this.postedon!=null)
	            return new Date(this.postedon);
	        return null;
	    }
	    public Date getUpdatedOn() {
	    	if(this.updatedon!=null)
	            return new Date(this.updatedon);
	        return null;
	    }

}
