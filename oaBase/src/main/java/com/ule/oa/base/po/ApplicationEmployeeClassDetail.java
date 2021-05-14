package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.ApplicationEmployeeClassDetailTbl;

/**
 * @ClassName: 排班申请明细表
 * @Description: 排班申请明细表
 * @author yangjie
 * @date 2017年8月29日
 */
@JsonInclude(Include.NON_NULL)
public class ApplicationEmployeeClassDetail extends ApplicationEmployeeClassDetailTbl{
	
	private static final long serialVersionUID = 1L;

   @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
   private Date startTime;//排班开始时间
   
   @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
   private Date endTime;//排班结束时间
	
   private String name;//班次名称
   
   private Long oldClassSettingId;//老的班次名称
   
   private String oldName;//老的班次名称
   
   private Date oldStartTime;//排班开始时间
   
   private Date oldEndTime;//排班结束时间
   
   private Integer isInterDay;//是否跨天

   private Double mustAttnTime;//排班出勤时间

   private List<Long> employeeIds;//批量查询时，存放批量员工id
   private Long departId;//部门id
   private Integer alreadyClassSettingCount;//已经排班的人数
   private Integer mustClassSettingCount;//应该排班人数
   
   @DateTimeFormat(pattern="yyyy-MM-dd")
   private Date firstEntryTime;//入职日期
   @DateTimeFormat(pattern="yyyy-MM-dd")
   private Date quitTime;//离职日期
   
   private Double surplusHours;//上月剩余工时
   
   private List<Date> classDateList;
   
   private Double minusHours;
   
   private Long groupId;//组别id

   public Integer getMustClassSettingCount() {
		return mustClassSettingCount;
	}

	public void setMustClassSettingCount(Integer mustClassSettingCount) {
		this.mustClassSettingCount = mustClassSettingCount;
	}

	public Integer getAlreadyClassSettingCount() {
		return alreadyClassSettingCount;
	}

	public void setAlreadyClassSettingCount(Integer alreadyClassSettingCount) {
		this.alreadyClassSettingCount = alreadyClassSettingCount;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public List<Long> getEmployeeIds() {
		return employeeIds;
	}

	public void setEmployeeIds(List<Long> employeeIds) {
		this.employeeIds = employeeIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Double getMustAttnTime() {
		return mustAttnTime;
	}

	public void setMustAttnTime(Double mustAttnTime) {
		this.mustAttnTime = mustAttnTime;
	}

	public Integer getIsInterDay() {
		return isInterDay;
	}

	public void setIsInterDay(Integer isInterDay) {
		this.isInterDay = isInterDay;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getFirstEntryTime() {
		return firstEntryTime;
	}

	public void setFirstEntryTime(Date firstEntryTime) {
		this.firstEntryTime = firstEntryTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getQuitTime() {
		return quitTime;
	}

	public void setQuitTime(Date quitTime) {
		this.quitTime = quitTime;
	}

	public Double getSurplusHours() {
		return surplusHours;
	}

	public void setSurplusHours(Double surplusHours) {
		this.surplusHours = surplusHours;
	}

	public List<Date> getClassDateList() {
		return classDateList;
	}

	public void setClassDateList(List<Date> classDateList) {
		this.classDateList = classDateList;
	}

	public Double getMinusHours() {
		return minusHours;
	}

	public void setMinusHours(Double minusHours) {
		this.minusHours = minusHours;
	}

	public Long getOldClassSettingId() {
		return oldClassSettingId;
	}

	public void setOldClassSettingId(Long oldClassSettingId) {
		this.oldClassSettingId = oldClassSettingId;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	public Date getOldStartTime() {
		return oldStartTime;
	}

	public void setOldStartTime(Date oldStartTime) {
		this.oldStartTime = oldStartTime;
	}
	@JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
	public Date getOldEndTime() {
		return oldEndTime;
	}

	public void setOldEndTime(Date oldEndTime) {
		this.oldEndTime = oldEndTime;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

}
