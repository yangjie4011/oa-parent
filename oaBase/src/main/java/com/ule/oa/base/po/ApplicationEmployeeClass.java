package com.ule.oa.base.po;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.ApplicationEmployeeClassTbl;

/**
 * @ClassName: 排班申请表
 * @Description: 排班申请表
 * @author yangjie
 * @date 2017年8月31日
 */
@JsonInclude(Include.NON_NULL)
public class ApplicationEmployeeClass extends ApplicationEmployeeClassTbl{

	private static final long serialVersionUID = -2234249226355333542L;
	
	public static final int IS_MOVE_0 = 0;//不是调班
	public static final int IS_MOVE_1 = 1;//调班
	
	//查询条件
	private Date startTime;
	private Date endTime;
	private String parentDepartName;
	private String token;
	private List<Integer> approvalStatusNoList;//不包含的状态
	private String assignee;//待办人
	private String month;//年月
	private String firstDepart;//部门
	private Double shouldTime;//应出勤工时
	private List<Long> employeeIds;
	private Long attnApplicationEmployClassId;
	private Date classDate;
	private String groupName;//组别名称
	private String auditorName;//批核人名称
	private List<Integer> approvalStatusList;//包含的状态
	private List<Long> groupIdList;
	
	private Map<String, Object> map=new HashMap<String, Object>();
	
	
	
	public Map<String, Object> getMap() {
		return map;
	}
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	public List<Integer> getApprovalStatusList() {
		return approvalStatusList;
	}
	public void setApprovalStatusList(List<Integer> approvalStatusList) {
		this.approvalStatusList = approvalStatusList;
	}
	public Long getAttnApplicationEmployClassId() {
		return attnApplicationEmployClassId;
	}
	public void setAttnApplicationEmployClassId(Long attnApplicationEmployClassId) {
		this.attnApplicationEmployClassId = attnApplicationEmployClassId;
	}
	public Date getClassDate() {
		return classDate;
	}
	public void setClassDate(Date classDate) {
		this.classDate = classDate;
	}
	public List<Integer> getApprovalStatusNoList() {
		return approvalStatusNoList;
	}
	public void setApprovalStatusNoList(List<Integer> approvalStatusNoList) {
		this.approvalStatusNoList = approvalStatusNoList;
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
	public String getParentDepartName() {
		return parentDepartName;
	}
	public void setParentDepartName(String parentDepartName) {
		this.parentDepartName = parentDepartName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getFirstDepart() {
		return firstDepart;
	}
	public void setFirstDepart(String firstDepart) {
		this.firstDepart = firstDepart;
	}
	public Double getShouldTime() {
		return shouldTime;
	}
	public void setShouldTime(Double shouldTime) {
		this.shouldTime = shouldTime;
	}
	public List<Long> getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(List<Long> employeeIds) {
		this.employeeIds = employeeIds;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getAuditorName() {
		return auditorName;
	}
	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}
	public List<Long> getGroupIdList() {
		return groupIdList;
	}
	public void setGroupIdList(List<Long> groupIdList) {
		this.groupIdList = groupIdList;
	}
	
	
}
