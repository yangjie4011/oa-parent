package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

//员工排班班组关联表
public class EmpScheduleGroupTbl implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6912357353518876399L;
	
	private Long id;
	
	//员工id
	private Long empId;
	
	//组别id
	private Long groupId;
	
	//创建时间
	private Date createTime;
	
	//创建人
    private Long createUser;
    
    //修改时间
    private Date updateTime;
    
    //修改人
    private Long updateUser;
    
    //删除标识(0未删除，1删除)
    private Integer delFlag;
    
    
    private String remark;
    
    
    private Long version;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getEmpId() {
		return empId;
	}


	public void setEmpId(Long empId) {
		this.empId = empId;
	}


	public Long getGroupId() {
		return groupId;
	}


	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}


	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public Long getCreateUser() {
		return createUser;
	}


	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}


	public Date getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	public Long getUpdateUser() {
		return updateUser;
	}


	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}


	public Integer getDelFlag() {
		return delFlag;
	}


	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public Long getVersion() {
		return version;
	}


	public void setVersion(Long version) {
		this.version = version;
	}
    
    
    
}
