package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.ule.oa.base.po.tbl.AttnUpdateLogTbl;

public class AttnUpdateLog extends AttnUpdateLogTbl {

	private static final long serialVersionUID = 7504148723083003551L;
	
	//员工编号
	private String code;
	//员工姓名
	private String cnName;
	//部门名称
	private String departName;
	//所属部门
	private Long departId;
	//开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startTime;
    //结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endTime;
	
	private List<Long> currentUserDepart;//当前用户所拥有的部门权限	
	
	private List<Long> subEmployeeIdList;//下属员工

	public List<Long> getCurrentUserDepart() {
		return currentUserDepart;
	}

	public void setCurrentUserDepart(List<Long> currentUserDepart) {
		this.currentUserDepart = currentUserDepart;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public List<Long> getSubEmployeeIdList() {
		return subEmployeeIdList;
	}

	public void setSubEmployeeIdList(List<Long> subEmployeeIdList) {
		this.subEmployeeIdList = subEmployeeIdList;
	}

}
