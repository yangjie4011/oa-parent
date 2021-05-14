package com.ule.oa.base.po.tbl;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;


import com.fasterxml.jackson.annotation.JsonFormat;

@Table(name="sys_update_log")
public class SysUpdateLogTbl {
	@Id
	private Long id;
	private Long updateEmployeeId;
	private String updateEmployeeName;
	private Long resourceEmployeeId;
	private String jsonInfo;
	private Date createTime;
	private Integer delFlag;
	private Integer resourceType;//资源类型:1.假期使用修改,2.假期基数修改
	private String remarkRecord;//新增备注字段

	public String getRemarkRecord() {
		return remarkRecord;
	}
	public void setRemarkRecord(String remarkRecord) {
		this.remarkRecord = remarkRecord;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUpdateEmployeeId() {
		return updateEmployeeId;
	}
	public void setUpdateEmployeeId(Long updateEmployeeId) {
		this.updateEmployeeId = updateEmployeeId;
	}
	public String getUpdateEmployeeName() {
		return updateEmployeeName;
	}
	public void setUpdateEmployeeName(String updateEmployeeName) {
		this.updateEmployeeName = updateEmployeeName;
	}
	public Long getResourceEmployeeId() {
		return resourceEmployeeId;
	}
	public void setResourceEmployeeId(Long resourceEmployeeId) {
		this.resourceEmployeeId = resourceEmployeeId;
	}
	public String getJsonInfo() {
		return jsonInfo;
	}
	public void setJsonInfo(String jsonInfo) {
		this.jsonInfo = jsonInfo;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
	public Integer getResourceType() {
		return resourceType;
	}
	public void setResourceType(Integer resourceType) {
		this.resourceType = resourceType;
	}
}
