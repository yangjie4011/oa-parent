package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 值班申请
 * @Description: 值班申请
 * @author yangjie
 * @date 2017年12月11日
 */
public class ApplyEmployDutyTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 4264801540124353009L;
	
	private Long id;
	//部门ID
    private Long departId;
    //部门名称
    private String departName;
    //值班人数
    private Integer dutyNum;
    //参与人员
    private String employeeIds;
    //排班人
    private String classSettingPerson;
    //节日年份
    private String year;
    //节日名称
    private String vacationName;
    //审批状态：100-待审批 、200-已审批 、300-已拒
  	private Integer approvalStatus;
  	//审批意见
  	private String approvalReason;
    private Integer delFlag;
    private Date createTime;
    private String createUser;
    private String updateUser;
    private Date updateTime;
    private Long version;
	
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Integer getDutyNum() {
		return dutyNum;
	}
	public void setDutyNum(Integer dutyNum) {
		this.dutyNum = dutyNum;
	}
	public String getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(String employeeIds) {
		this.employeeIds = employeeIds;
	}
	public String getClassSettingPerson() {
		return classSettingPerson;
	}
	public void setClassSettingPerson(String classSettingPerson) {
		this.classSettingPerson = classSettingPerson;
	}
	public String getVacationName() {
		return vacationName;
	}
	public void setVacationName(String vacationName) {
		this.vacationName = vacationName;
	}
	public Integer getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public String getApprovalReason() {
		return approvalReason;
	}
	public void setApprovalReason(String approvalReason) {
		this.approvalReason = approvalReason;
	}
	public Integer getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
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
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
}
