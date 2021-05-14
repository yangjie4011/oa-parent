package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;
/**
 * @ClassName: Attn_Class_Setting
 * @Description: 考勤班次设置表
 * @author zhoujinliang
 * @date 2018年7月4日
 */
public class ClassSettingTbl extends CommonPo implements Serializable{
      /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 3302658586704431590L;

	private Long id;
    private Long companyId;//公司id
    private Long departId;//部门id
    private String name;//班次名称
    @DateTimeFormat(pattern="HH:mm")
    private Date startTime;//排班开始时间
    @DateTimeFormat(pattern="HH:mm")
    private Date endTime;//排班结束时间
    private Integer isInterDay;////是否跨天（跨0点） 0：不跨天，1：跨天
    private Double mustAttnTime;//必须出勤时间
    private Integer delFlag;
    private String createUser;
    private Date createTime;
    private String updateUser;
    private Date updateTime;
    private Long version;
    private Integer allowLateTime;//允许迟到时间
    private String fullName;//全称
    private Integer  isEnable;//是否启用
    private String groupIds;
    
    
    
    public Integer getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Integer isEnable) {
		this.isEnable = isEnable;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Integer getAllowLateTime() {
		return allowLateTime;
	}

	public void setAllowLateTime(Integer allowLateTime) {
		this.allowLateTime = allowLateTime;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getIsInterDay() {
        return isInterDay;
    }

    public void setIsInterDay(Integer isInterDay) {
        this.isInterDay = isInterDay;
    }

    public Double getMustAttnTime() {
        return mustAttnTime;
    }

    public void setMustAttnTime(Double mustAttnTime) {
        this.mustAttnTime = mustAttnTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

	public String getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}
    
}