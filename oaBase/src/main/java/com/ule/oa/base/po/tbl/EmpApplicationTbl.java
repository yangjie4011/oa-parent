package com.ule.oa.base.po.tbl;

import java.util.Date;
/**
 * @ClassName: Base_Emp_Application
 * @Description: 员工资料申请表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class EmpApplicationTbl {
    private Long id;
    //类型
    private Long type;
    //子类型
    private String moduleDetail;
    //模块
    private String module;
    //老数据
    private String oldValue;
    //新数据
    private String newValue;
    //展示
    private String showValue;
    //审批状态
    private Long approvalStatus;
    //审批人ID
    private Long approvalId;
    //审批人姓名
    private String approvalName;
    //版本号
    private Long arrayValue;

    private String version;

    private Long delFlag;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String updateUser;

    private Long employeeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }


    public String getModuleDetail() {
		return moduleDetail;
	}

	public void setModuleDetail(String moduleDetail) {
		this.moduleDetail = moduleDetail;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue == null ? null : oldValue.trim();
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue == null ? null : newValue.trim();
    }

    public String getShowValue() {
        return showValue;
    }

    public void setShowValue(String showValue) {
        this.showValue = showValue == null ? null : showValue.trim();
    }

    public Long getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Long approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public String getApprovalName() {
        return approvalName;
    }

    public void setApprovalName(String approvalName) {
        this.approvalName = approvalName == null ? null : approvalName.trim();
    }

    public Long getArrayValue() {
        return arrayValue;
    }

    public void setArrayValue(Long arrayValue) {
        this.arrayValue = arrayValue;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Long getDelFlag() {
        return delFlag;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

}
