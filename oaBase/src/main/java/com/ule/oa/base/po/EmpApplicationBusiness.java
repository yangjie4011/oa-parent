package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpApplicationBusinessTbl;

/**
 * @ClassName: 出差申请表
 * @Description: 出差申请表
 * @author yangjie
 * @date 2017年6月13日
 */

@JsonInclude(Include.NON_NULL)
public class EmpApplicationBusiness extends EmpApplicationBusinessTbl {

	private static final long serialVersionUID = 2781758434686040564L;
	
	public static final int APPROVAL_STATUS_WAIT = 100;//待审批
	public static final int APPROVAL_STATUS_YES = 200;//已审批
	public static final int APPROVAL_STATUS_NO = 300;//已拒
	
	public static final int VEHICLE_BUS = 300;//汽车
	public static final int VEHICLE_AIR = 200;//飞机
	public static final int VEHICLE_TRAIN = 100;//火车
	
	private String nodeCode;//节点CODE
	
	private String token;
	private String workType;//工时类型
	private List<Integer> departList;//部门
	private Integer empTypeId;//员工类型
	private Integer whetherScheduling;//是否排班
	private String auditUser;//审核人
	private String approvalStatusDesc;//审核状态
	private Integer firstDepart;//页面上一级部门
	private Integer secondDepart;//页面上二级部门
	private Long billType;//单据类型
	private List<Long> currentUserDepart;//当前用户所拥有的部门权限	
	private List<Long> subEmployeeIdList;//下属员工

	private Integer peopleNum;//出差人数统计
	
	private Integer frequencyNum;//出差次数统计
	
	private Integer year;	//年
	
	private String assignee;
	
	private  List<Long>  approvalStatusList;//状态集合
	private List<Long> empTypeIdList; //员工类型集合
	
	private Date firstEntryTime;//入职筛选条件 
	private Date quitTime;//离职筛选条件 
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getQuitTime() {
		return quitTime;
	}

	public void setQuitTime(Date quitTime) {
		this.quitTime = quitTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getFirstEntryTime() {
		return firstEntryTime;
	}

	public void setFirstEntryTime(Date firstEntryTime) {
		this.firstEntryTime = firstEntryTime;
	}

	public List<Long> getApprovalStatusList() {
		return approvalStatusList;
	}

	public void setApprovalStatusList(List<Long> approvalStatusList) {
		this.approvalStatusList = approvalStatusList;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getPeopleNum() {
		return peopleNum;
	}

	public void setPeopleNum(Integer peopleNum) {
		this.peopleNum = peopleNum;
	}

	public Integer getFrequencyNum() {
		return frequencyNum;
	}

	public void setFrequencyNum(Integer frequencyNum) {
		this.frequencyNum = frequencyNum;
	}

	public List<Long> getCurrentUserDepart() {
		return currentUserDepart;
	}

	public void setCurrentUserDepart(List<Long> currentUserDepart) {
		this.currentUserDepart = currentUserDepart;
	}
	
	public Long getBillType() {
		return billType;
	}

	public void setBillType(Long billType) {
		this.billType = billType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public List<Integer> getDepartList() {
		return departList;
	}

	public void setDepartList(List<Integer> departList) {
		this.departList = departList;
	}

	public Integer getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Integer empTypeId) {
		this.empTypeId = empTypeId;
	}

	public Integer getWhetherScheduling() {
		return whetherScheduling;
	}

	public void setWhetherScheduling(Integer whetherScheduling) {
		this.whetherScheduling = whetherScheduling;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	public String getApprovalStatusDesc() {
		return approvalStatusDesc;
	}

	public void setApprovalStatusDesc(String approvalStatusDesc) {
		this.approvalStatusDesc = approvalStatusDesc;
	}

	public Integer getFirstDepart() {
		return firstDepart;
	}

	public void setFirstDepart(Integer firstDepart) {
		this.firstDepart = firstDepart;
	}

	public Integer getSecondDepart() {
		return secondDepart;
	}

	public void setSecondDepart(Integer secondDepart) {
		this.secondDepart = secondDepart;
	}

	public static int getVehicleAir() {
		return VEHICLE_AIR;
	}

	public static int getVehicleTrain() {
		return VEHICLE_TRAIN;
	}

	public List<Long> getSubEmployeeIdList() {
		return subEmployeeIdList;
	}

	public void setSubEmployeeIdList(List<Long> subEmployeeIdList) {
		this.subEmployeeIdList = subEmployeeIdList;
	}

	public List<Long> getEmpTypeIdList() {
		return empTypeIdList;
	}

	public void setEmpTypeIdList(List<Long> empTypeIdList) {
		this.empTypeIdList = empTypeIdList;
	}

	
	
}
