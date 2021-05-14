package com.ule.oa.base.po;

import java.util.List;

import com.ule.oa.base.po.tbl.RabcResourceTbl;

public class RabcResource extends RabcResourceTbl{

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -4698125503780341445L;
	
	
	private Long roleId;
	
	private List<Integer> typeList;
	
	List<Long> ids;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public List<Integer> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<Integer> typeList) {
		this.typeList = typeList;
	}
	
}