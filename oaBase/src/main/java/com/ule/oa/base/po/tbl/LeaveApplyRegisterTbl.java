package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * 休假登记
 * @author yangjie
 *
 */
public class LeaveApplyRegisterTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 7914682105705213510L;
	
	private Long id;
	
	@NotNull(message = "请选择所需登记员工！")
	private Long employeeId;//员工Id
	
	private String code;//员工编号
	
	private String cnName;//员工姓名
	
	private String departName;//部门名称
	
	private String positionName;//职位名称
	
	private String typeOfWork;//工时制
	
	@NotNull(message = "请选择登记假期类型！")
	private Integer leaveType;//请假类型
	
	@NotNull(message = "请选择登记假期时间！")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startTime;//开始时间
	
	@NotNull(message = "请选择登记假期时间！")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endTime;//结束时间
	
	private BigDecimal leaveDays;//请假天数
	
	private BigDecimal leaveHours;//请假小时数（调休，哺乳假等使用，产假额外天数也用）
	
	private Integer childrenNum;//小孩数（产假，哺乳假）
	
	private Integer dayType;//请假时间（上午，下午，哺乳假）
	
	private Integer livingState;//生产情况（产假）
	
	private Date childrenBirthday;//出生日期（产假）
	
	private Integer relatives;//亲属关系（丧假）
	
	@NotBlank(message = "登记理由不能为空！")
	private String reason;//登记理由
	
	private Date createTime;//登记时间
	
	private String createUser;//登记人
	
	private Date updateTime;
	
	private String updateUser;
	
	private Integer delFlag;
	
	private Date rowCreateTime;
	
	private Date rowLastUpdateTime;

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
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

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getTypeOfWork() {
		return typeOfWork;
	}

	public void setTypeOfWork(String typeOfWork) {
		this.typeOfWork = typeOfWork;
	}

	public Integer getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(Integer leaveType) {
		this.leaveType = leaveType;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getLeaveDays() {
		return leaveDays;
	}

	public void setLeaveDays(BigDecimal leaveDays) {
		this.leaveDays = leaveDays;
	}

	public BigDecimal getLeaveHours() {
		return leaveHours;
	}

	public void setLeaveHours(BigDecimal leaveHours) {
		this.leaveHours = leaveHours;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Integer getChildrenNum() {
		return childrenNum;
	}

	public void setChildrenNum(Integer childrenNum) {
		this.childrenNum = childrenNum;
	}

	public Integer getDayType() {
		return dayType;
	}

	public void setDayType(Integer dayType) {
		this.dayType = dayType;
	}

	public Integer getLivingState() {
		return livingState;
	}

	public void setLivingState(Integer livingState) {
		this.livingState = livingState;
	}

	public Date getChildrenBirthday() {
		return childrenBirthday;
	}

	public void setChildrenBirthday(Date childrenBirthday) {
		this.childrenBirthday = childrenBirthday;
	}

	public Integer getRelatives() {
		return relatives;
	}

	public void setRelatives(Integer relatives) {
		this.relatives = relatives;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getRowCreateTime() {
		return rowCreateTime;
	}

	public void setRowCreateTime(Date rowCreateTime) {
		this.rowCreateTime = rowCreateTime;
	}

	public Date getRowLastUpdateTime() {
		return rowLastUpdateTime;
	}

	public void setRowLastUpdateTime(Date rowLastUpdateTime) {
		this.rowLastUpdateTime = rowLastUpdateTime;
	}
	

}
