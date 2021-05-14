package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 远程控制记录表
 * @Description: 远程控制记录表
 * @author zhoujinliang	
 * @date 2020年2月24日17:00:00
 */
public class RemoteWorkRegisterTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = -8328486957385100764L;
	
	private Long id;
	//员工
	private Long employeeId;
	//登记日期
	private Date registerDate;
	//班次id
	private Long classsSettingId;
	//是否远程（0-是，1-否）
	private Long isRemote;
	//审阅状态（0-通过，1-不通过，2-未提交）
	private String approveStatus;
	//审阅意见
	private String resaon;
	private Date createTime;
    private String createUser;
    private Date updateTime;
    private String updateUser;
    private Integer delFlag;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	public Long getClasssSettingId() {
		return classsSettingId;
	}
	public void setClasssSettingId(Long classsSettingId) {
		this.classsSettingId = classsSettingId;
	}
	public Long getIsRemote() {
		return isRemote;
	}
	public void setIsRemote(Long isRemote) {
		this.isRemote = isRemote;
	}
	public String getApproveStatus() {
		return approveStatus;
	}
	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}
	
	public String getResaon() {
		return resaon;
	}
	public void setResaon(String resaon) {
		this.resaon = resaon;
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
	
}
