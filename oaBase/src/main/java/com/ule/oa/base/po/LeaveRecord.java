package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.base.po.tbl.LeaveRecordTbl;

/**
 * @author yangjie
 * 假期流水表
 */
public class LeaveRecord extends LeaveRecordTbl{

	private static final long serialVersionUID = 4281731211278108043L;
	
	//对应假期表（base_emp_leave：year）
	//年份或者月份
	private Integer year;
	//对应假期表（base_emp_leave：category）
	//假期分类
	private Integer category;
	//对应假期表（base_emp_leave：parend_id）
	
	private List<Integer> years;
	
	private List<Integer> types;
	//父ID
	private Long parendId;
	
	private Date startTime;//关联单据假期开始时间
	
	private Date endTime;//关联单据假期结束时间
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public Long getParendId() {
		return parendId;
	}
	public void setParendId(Long parendId) {
		this.parendId = parendId;
	}
	public List<Integer> getYears() {
		return years;
	}
	public void setYears(List<Integer> years) {
		this.years = years;
	}
	public List<Integer> getTypes() {
		return types;
	}
	public void setTypes(List<Integer> types) {
		this.types = types;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	

}
