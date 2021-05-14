package com.ule.oa.base.po;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 查询标准工时员工请求参数
 * @author xujintao
 *
 */
@JsonInclude(Include.NON_NULL)
public class RequestParamQueryEmpCondition implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6990238289169886930L;
	//汇报人id
	private Long leaderId;
	//部门id
	private Long departId;
	//工时类型
	private Long workType;
	//员工编号
	private String empCode;
	//员工姓名
	private String empCnName;
	//筛选汇报对象
	private String searchLeader;
	//所选日期
	private Date chooseMonth;
	//开始时间 月
	private Date startDate;
	//结束时间 月
	private Date endDate;
	//分页
	private Integer page;
	private Integer rows;
	private Integer offset;
	private Integer limit;
	
	private List<Long> empTypeIdList;//员工类型id列表
	
	private List<Long> employeeIdList;//员工id列表
	
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Long getLeaderId() {
		return leaderId;
	}
	public void setLeaderId(Long leaderId) {
		this.leaderId = leaderId;
	}
	public Long getDepartId() {
		return departId;
	}
	public void setDepartId(Long departId) {
		this.departId = departId;
	}
	public Long getWorkType() {
		return workType;
	}
	public void setWorkType(Long workType) {
		this.workType = workType;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
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
	public String getEmpCode() {
		return empCode;
	}
	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}
	public String getEmpCnName() {
		return empCnName;
	}
	public void setEmpCnName(String empCnName) {
		this.empCnName = empCnName;
	}
	public Date getChooseMonth() {
		return chooseMonth;
	}
	public void setChooseMonth(Date chooseMonth) {
		this.chooseMonth = chooseMonth;
	}
	public String getSearchLeader() {
		return searchLeader;
	}
	public void setSearchLeader(String searchLeader) {
		this.searchLeader = searchLeader;
	}
	public List<Long> getEmpTypeIdList() {
		return empTypeIdList;
	}
	public void setEmpTypeIdList(List<Long> empTypeIdList) {
		this.empTypeIdList = empTypeIdList;
	}
	public List<Long> getEmployeeIdList() {
		return employeeIdList;
	}
	public void setEmployeeIdList(List<Long> employeeIdList) {
		this.employeeIdList = employeeIdList;
	}
	
	
	
}
