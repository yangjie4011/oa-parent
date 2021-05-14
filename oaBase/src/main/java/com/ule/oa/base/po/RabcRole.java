package com.ule.oa.base.po;

import com.ule.oa.base.po.tbl.RabcRoleTbl;

public class RabcRole extends RabcRoleTbl{

	/**
	 * 
	 */
	private static final long serialVersionUID = 683690289429820261L;
	
	private Integer isSelected;

	private Integer userId;
	
	private String departName;
	
	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Integer isSelected) {
		this.isSelected = isSelected;
	}
	

}
