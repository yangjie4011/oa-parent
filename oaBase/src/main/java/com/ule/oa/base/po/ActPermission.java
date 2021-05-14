package com.ule.oa.base.po;


import javax.persistence.Table;
import javax.persistence.Transient;

import com.ule.oa.base.po.tbl.ActPermissionTbl;
@Table(name="ACT_PERMISSION")
public class ActPermission extends ActPermissionTbl{
	
	@Transient
   private String departId;

	public String getDepartId() {
		return departId;
	}
	
	public void setDepartId(String departId) {
		this.departId = departId;
	}
	   
}