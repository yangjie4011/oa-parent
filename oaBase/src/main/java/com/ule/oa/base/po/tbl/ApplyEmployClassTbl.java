package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 排班申请
 * @Description: 排班申请
 * @author yangjie
 * @date 2017年12月11日
 */
public class ApplyEmployClassTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 4121143522538031313L;
	
	private Long id;
	//部门ID
    private Long departId;
    //部门名称
    private String departName;
    //排班月份
    private Date classMonth;
    //部门人数
    private Integer employeeNum;
    //部门已排班人数
    private Integer classEmployeeNum;
    //排班人
    private String classSettingPerson;
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
    //是否调班
    private Integer isMove;
    
    
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
	public Date getClassMonth() {
		return classMonth;
	}
	public void setClassMonth(Date classMonth) {
		this.classMonth = classMonth;
	}
	public Integer getEmployeeNum() {
		return employeeNum;
	}
	public void setEmployeeNum(Integer employeeNum) {
		this.employeeNum = employeeNum;
	}
	public Integer getClassEmployeeNum() {
		return classEmployeeNum;
	}
	public void setClassEmployeeNum(Integer classEmployeeNum) {
		this.classEmployeeNum = classEmployeeNum;
	}
	public String getClassSettingPerson() {
		return classSettingPerson;
	}
	public void setClassSettingPerson(String classSettingPerson) {
		this.classSettingPerson = classSettingPerson;
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
	public Integer getIsMove() {
		return isMove;
	}
	public void setIsMove(Integer isMove) {
		this.isMove = isMove;
	}

}
