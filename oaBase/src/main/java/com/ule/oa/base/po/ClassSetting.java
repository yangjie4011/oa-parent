package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.ClassSettingTbl;

@JsonInclude(Include.NON_NULL)
public class ClassSetting extends ClassSettingTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -3826307516653666047L;
	
	//是否展示所有班次0-展示前3条，为空-展示所有
	private Long isShowAll;
	
	private String token;
	
	private String departName;

	private String groupNames;
	
	private Long groupId;
	
	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public Long getIsShowAll() {
		return isShowAll;
	}

	public void setIsShowAll(Long isShowAll) {
		this.isShowAll = isShowAll;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getGroupNames() {
		return groupNames;
	}

	public void setGroupNames(String groupNames) {
		this.groupNames = groupNames;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	

}
