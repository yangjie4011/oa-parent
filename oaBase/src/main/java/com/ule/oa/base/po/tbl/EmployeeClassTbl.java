package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
/**
 * @ClassName: Attn_Application_Employ_Class
 * @Description: 排班申请表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class EmployeeClassTbl implements Serializable{
      /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -5309671059686607179L;

	private Long id;
    private Long companyId;
    private Long departId;
    private Long employId;//员工ID
    private String employName;//员工姓名
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date classDate;//排班日期
    private Long classSettingId;//排班设置的ID
    private String classSettingCode;//排班设置的code（未用）
    private String classSettingPerson;//排班人
    private Integer delFlag;
    private Date createTime;
    private String createUser;
    private String updateUser;
    private Date updateTime;
    private Long version;
    //应出勤时间
    private double shouldTime;
    //组别
    private Long groupId;

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

	public double getShouldTime() {
		return shouldTime;
	}

	public void setShouldTime(double shouldTime) {
		this.shouldTime = shouldTime;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
    
}