package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @ClassName: 排班申请明细表
 * @Description: 排班申请明细表
 * @author yangjie
 * @date 2017年8月30日
 */
public class ApplicationEmployeeClassDetailTbl implements Serializable{

	private static final long serialVersionUID = -5335517489090662841L;
	
	private Long id;
	//排班申请表ID
	private Long attnApplicationEmployClassId;
    //公司ID
	private Long companyId;
	//员工ID
    private Long employId;
    //员工姓名
    private String employName;
    //排班日期
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date classDate;
    //排班设置的ID
    private Long classSettingId;
    //排班设置的code（未用）
    private String classSettingCode;
    //排班人
    private String classSettingPerson;
    private Integer delFlag;
    private Date createTime;
    private String createUser;
    private String updateUser;
    private Date updateTime;
    private Long version;
    //是否调班
    private Integer isMove;
    //应出勤时间
    private double shouldTime;

    public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getClassSettingPerson() {
		return classSettingPerson;
	}

	public void setClassSettingPerson(String classSettingPerson) {
		this.classSettingPerson = classSettingPerson;
	}

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

    public Long getEmployId() {
        return employId;
    }

    public void setEmployId(Long employId) {
        this.employId = employId;
    }

    public String getEmployName() {
        return employName;
    }

    public void setEmployName(String employName) {
        this.employName = employName == null ? null : employName.trim();
    }
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getClassDate() {
        return classDate;
    }

    public void setClassDate(Date classDate) {
        this.classDate = classDate;
    }

    public Long getClassSettingId() {
        return classSettingId;
    }

    public void setClassSettingId(Long classSettingId) {
        this.classSettingId = classSettingId;
    }

    public String getClassSettingCode() {
        return classSettingCode;
    }

    public void setClassSettingCode(String classSettingCode) {
        this.classSettingCode = classSettingCode == null ? null : classSettingCode.trim();
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
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public Long getAttnApplicationEmployClassId() {
		return attnApplicationEmployClassId;
	}

	public void setAttnApplicationEmployClassId(Long attnApplicationEmployClassId) {
		this.attnApplicationEmployClassId = attnApplicationEmployClassId;
	}

	public Integer getIsMove() {
		return isMove;
	}

	public void setIsMove(Integer isMove) {
		this.isMove = isMove;
	}

	public double getShouldTime() {
		return shouldTime;
	}

	public void setShouldTime(double shouldTime) {
		this.shouldTime = shouldTime;
	}

}
