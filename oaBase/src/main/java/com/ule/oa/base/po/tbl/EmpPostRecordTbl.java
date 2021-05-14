package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
  * @ClassName: EmpPostRecordTbl
  * @Description: 员工职位（岗位）记录表
  * @author minsheng
  * @date 2017年5月8日 下午1:37:28
 */
public class EmpPostRecordTbl implements Serializable{
      /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 2376435284802932734L;

	private Long id;
    private Long employeeId;//员工gid
    private Long preDepartId;//调整前的部门id
    private Long prePositionId;//调整前的职位id
    private Long departId;//调整后的部门id
    private Long positionId;//调整后的职位id
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date effectiveDate;//生效日期
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date adjustDate;//调整日期
    private Integer isCurrentPosition;//是否是当前职位（0：否，1：是）
    private Date createTime;
    private String createUser;
    private Date updateTime;
    private String updateUser;
    private Integer delFlag;
    private String remark;
    private Long version;
    
    public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getPreDepartId() {
		return preDepartId;
	}

	public void setPreDepartId(Long preDepartId) {
		this.preDepartId = preDepartId;
	}

	public Long getPrePositionId() {
		return prePositionId;
	}

	public void setPrePositionId(Long prePositionId) {
		this.prePositionId = prePositionId;
	}

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getAdjustDate() {
        return adjustDate;
    }

    public void setAdjustDate(Date adjustDate) {
        this.adjustDate = adjustDate;
    }

    public Long getDepartId() {
        return departId;
    }

    public void setDepartId(Long departId) {
        this.departId = departId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Integer getIsCurrentPosition() {
        return isCurrentPosition;
    }

    public void setIsCurrentPosition(Integer isCurrentPosition) {
        this.isCurrentPosition = isCurrentPosition;
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
        this.createUser = createUser == null ? null : createUser.trim();
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
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}