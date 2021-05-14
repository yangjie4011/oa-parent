package com.ule.oa.base.po;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 查询假期流水请求参数
 * @author xujintao
 *
 */
@JsonInclude(Include.NON_NULL)
public class RequestQueryLeaveRecord implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4644517608805464585L;
	//员工编号
	private String employeeCode;
	//员工姓名
	private String employeeName;
	//单据类型
	private Integer source;
	//假期类型
	private Integer leaveType;
	//假期类型列表
	private List<Integer> leaveTypeList;
	//单据提交时间范围
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createStartDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createEndDate;
	//分页
	private Integer page;
	private Integer rows;
	private Integer offset;
	private Integer limit;
	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public Integer getSource() {
		return source;
	}
	public void setSource(Integer source) {
		this.source = source;
	}
	public Integer getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(Integer leaveType) {
		this.leaveType = leaveType;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Date getCreateStartDate() {
		return createStartDate;
	}
	public void setCreateStartDate(Date createStartDate) {
		this.createStartDate = createStartDate;
	}
	public Date getCreateEndDate() {
		return createEndDate;
	}
	public void setCreateEndDate(Date createEndDate) {
		this.createEndDate = createEndDate;
	}
	public Integer getRows() {
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
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
	public List<Integer> getLeaveTypeList() {
		return leaveTypeList;
	}
	public void setLeaveTypeList(List<Integer> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}
	
}
