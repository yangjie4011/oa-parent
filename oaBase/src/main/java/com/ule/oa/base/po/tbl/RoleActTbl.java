package com.ule.oa.base.po.tbl;

import javax.persistence.Table;
/**
 * @ClassName: sys_role_act
 * @Description: 权限 流程 关系 表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
@Table(name="sys_role_act")
public class RoleActTbl {

    private Long id;
    //权限id
    private Long roleId;
    //流程id
    private Long actId;
    //部门id
    private Long departId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getActId() {
		return actId;
	}

	public void setActId(Long actId) {
		this.actId = actId;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}
    
    
}
