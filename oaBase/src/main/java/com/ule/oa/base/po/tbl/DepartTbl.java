package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
  * @ClassName: DepartTbl
  * @Description: 部门表
  * @author minsheng
  * @date 2017年5月8日 下午1:35:40
 */
public class DepartTbl implements Serializable{

	private static final long serialVersionUID = -5381656729339691043L;

	private Long id;
	
	private Long companyId;

    private String code;//部门编码

    private String name;//部门名称

    private Long leader;//部门负责人

    private Long parentId;//上级部门
    
    private Integer rank;//排序

    private Date createTime;

    private String createUser;

    private Date updateTime;

    private String updateUser;

    private Integer delFlag;

    private String remark;
    
    private Integer type;// 类型: 0-虚拟部门, 1-一级部门, 2-二级部门
    
    private Long power;// 行使权力人
    
    private Long version;
    
    private Integer isShowInMo;//0-在MO端显示，1-不在MO端显示
    
    private Integer whetherScheduling; 	//是否排班
    
    public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getLeader() {
		return leader;
	}

	public void setLeader(Long leader) {
		this.leader = leader;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getPower() {
		return power;
	}

	public void setPower(Long power) {
		this.power = power;
	}

	public Integer getIsShowInMo() {
		return isShowInMo;
	}

	public void setIsShowInMo(Integer isShowInMo) {
		this.isShowInMo = isShowInMo;
	}

	public Integer getWhetherScheduling() {
		return whetherScheduling;
	}

	public void setWhetherScheduling(Integer whetherScheduling) {
		this.whetherScheduling = whetherScheduling;
	}
	
	
}