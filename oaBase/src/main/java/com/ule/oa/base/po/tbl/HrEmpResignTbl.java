package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;
/**
 * @ClassName: Hr_Emp_Resign
 * @Description: 员工辞职申请表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class HrEmpResignTbl extends CommonPo implements Serializable {

	/**
	 *  离职数据表
	 */
	private static final long serialVersionUID = 2753300004831501863L;
	private Long id;
	private Long companyId;//公司id
	private Long employeeId;//员工id
	private String contractCode;//员工姓名
	private Long departId;//部门id
	private String departName;//部门名称
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date entryDate;//入职日期
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date resignationDate;//提出日期
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date lastDate;//预计最后工作日
	private Long importance;//重要度
	private String reasonLeavIng;//离职原因
	private String leaderOpinion;//部门领导意见
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date lEADERdate;//部门领导确认离职日期
	private Long leader;//部门领导ID
	private String leaderName;//部门领导姓名
	private String hrOpinion;//人事领导意见
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date hrDate;//人事领导确认离职日期
	private Long hrId;//人事领导ID
	private String hrName;//人事姓名
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date payRollDate;//薪资核算日期
	private Long turnOverStatus;//离职状态
	private String version;
	private Long delFlag;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date upDateTime;
	private String createUser;
	private String upDateUser;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getContractCode() {
		return contractCode;
	}
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	public Long getDepartId() {
		return departId;
	}
	public void setDepartId(Long departId) {
		this.departId = departId;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getResignationDate() {
		return resignationDate;
	}
	public void setResignationDate(Date resignationDate) {
		this.resignationDate = resignationDate;
	}
	public Date getLastDate() {
		return lastDate;
	}
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	public Long getImportance() {
		return importance;
	}
	public void setImportance(Long importance) {
		this.importance = importance;
	}
	public String getReasonLeavIng() {
		return reasonLeavIng;
	}
	public void setReasonLeavIng(String reasonLeavIng) {
		this.reasonLeavIng = reasonLeavIng;
	}
	public String getLeaderOpinion() {
		return leaderOpinion;
	}
	public void setLeaderOpinion(String leaderOpinion) {
		this.leaderOpinion = leaderOpinion;
	}
	public Date getlEADERdate() {
		return lEADERdate;
	}
	public void setlEADERdate(Date lEADERdate) {
		this.lEADERdate = lEADERdate;
	}
	public Long getLeader() {
		return leader;
	}
	public void setLeader(Long leader) {
		this.leader = leader;
	}
	public String getLeaderName() {
		return leaderName;
	}
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}
	public String getHrOpinion() {
		return hrOpinion;
	}
	public void setHrOpinion(String hrOpinion) {
		this.hrOpinion = hrOpinion;
	}
	public Date getHrDate() {
		return hrDate;
	}
	public void setHrDate(Date hrDate) {
		this.hrDate = hrDate;
	}
	public Long getHrId() {
		return hrId;
	}
	public void setHrId(Long hrId) {
		this.hrId = hrId;
	}
	public String getHrName() {
		return hrName;
	}
	public void setHrName(String hrName) {
		this.hrName = hrName;
	}
	public Date getPayRollDate() {
		return payRollDate;
	}
	public void setPayRollDate(Date payRollDate) {
		this.payRollDate = payRollDate;
	}
	public Long getTurnOverStatus() {
		return turnOverStatus;
	}
	public void setTurnOverStatus(Long turnOverStatus) {
		this.turnOverStatus = turnOverStatus;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setDelFlag(Long delFlag) {
		this.delFlag = delFlag;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpDateTime() {
		return upDateTime;
	}
	public void setUpDateTime(Date upDateTime) {
		this.upDateTime = upDateTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpDateUser() {
		return upDateUser;
	}
	public void setUpDateUser(String upDateUser) {
		this.upDateUser = upDateUser;
	}
	

}