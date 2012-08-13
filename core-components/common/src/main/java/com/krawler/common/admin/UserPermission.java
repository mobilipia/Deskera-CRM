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

import java.io.Serializable;

/**
 *
 * @author krawler-user
 */
public class UserPermission implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RoleUserMapping roleId;
    private ProjectFeature feature;
    private long permissionCode;

    public ProjectFeature getFeature() {
        return feature;
    }

    public void setFeature(ProjectFeature feature) {
        this.feature = feature;
    }

    public long getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(long permissionCode) {
        this.permissionCode = permissionCode;
    }

    public RoleUserMapping getRoleId() {
        return roleId;
    }

    public void setRoleId(RoleUserMapping roleId) {
        this.roleId = roleId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((feature == null) ? 0 : feature.hashCode());
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserPermission other = (UserPermission) obj;
		if (feature == null) {
			if (other.feature != null)
				return false;
		} else if (!feature.equals(other.feature))
			return false;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		return true;
	}
}
