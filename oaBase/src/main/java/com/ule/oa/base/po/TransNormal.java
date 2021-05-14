package com.ule.oa.base.po;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.TransNormalTbl;

@JsonInclude(Include.NON_NULL)
public class TransNormal extends TransNormalTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -4945135084748733239L;
	
	private String name;
    private Long oaEmpId;
    
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startTime;
    
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endTime;
    
    private String employeeIds;
    
	private Integer offset;
	private Integer limit;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getOaEmpId() {
		return oaEmpId;
	}
	public void setOaEmpId(Long oaEmpId) {
		this.oaEmpId = oaEmpId;
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
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public String getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(String employeeIds) {
		this.employeeIds = employeeIds;
	}
}
