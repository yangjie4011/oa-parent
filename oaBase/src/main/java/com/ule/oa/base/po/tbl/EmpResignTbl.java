package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 员工离职申请表
 * @Description: 员工离职申请表
 * @author yangjie
 * @date 2017年5月25日
 */
public class EmpResignTbl extends CommonPo implements Serializable{
	
	private static final long serialVersionUID = -1655580998751549094L;
	
	private Long id;
	//公司
	private Long companyId;
	//员工
	private Long employeeId;
	//员工姓名
	private String contractCode;
	//部门
	private Long departId;
	//部门名称
	private String departName;
	//入职日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date entryDate;
	//提出日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date resignationDate;
	//预计最后工作日
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date lastDate;
	//重要度
	private int importance;
	//离职原因
	private String reasonLeaving;
	//部门领导意见
	private String leaderOpinion;
	//部门领导确认离职日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date leaderDate;
	//部门领导ID
	private Long leader;
	//部门领导姓名
	private String leaderName;
	//人事领导意见
	private String hrOpinion;
	//人事领导确认离职日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date hrDate;
	//人事领导ID
	private Long hrId;
	//人事姓名
	private String hrName;
	//薪资核算日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date payrollDate;
	//离职状态 100-提交、200-部门领导意见、300-人事意见、400-驳回、500-撤销
	private int turnoverStatus;
	
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
	public Date getResignationDate() {
		return resignationDate;
	}
	public void setResignationDate(Date resignationDate) {
		this.resignationDate = resignationDate;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getLastDate() {
		return lastDate;
	}
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	public int getImportance() {
		return importance;
	}
	public void setImportance(int importance) {
		this.importance = importance;
	}
	public String getReasonLeaving() {
		return reasonLeaving;
	}
	public void setReasonLeaving(String reasonLeaving) {
		this.reasonLeaving = reasonLeaving;
	}
	public String getLeaderOpinion() {
		return leaderOpinion;
	}
	public void setLeaderOpinion(String leaderOpinion) {
		this.leaderOpinion = leaderOpinion;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getLeaderDate() {
		return leaderDate;
	}
	public void setLeaderDate(Date leaderDate) {
		this.leaderDate = leaderDate;
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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getPayrollDate() {
		return payrollDate;
	}
	public void setPayrollDate(Date payrollDate) {
		this.payrollDate = payrollDate;
	}
	public int getTurnoverStatus() {
		return turnoverStatus;
	}
	public void setTurnoverStatus(int turnoverStatus) {
		this.turnoverStatus = turnoverStatus;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

}
