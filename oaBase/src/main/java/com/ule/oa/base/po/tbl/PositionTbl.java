package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
  * @ClassName: PositionTbl
  * @Description: 岗位表
  * @author minsheng
  * @date 2017年5月8日 下午1:38:56
 */
public class PositionTbl implements Serializable{
      /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 2333554051401250871L;
	private Long id;
	private Long companyId;//公司id
    private String positionCode;//职位编码
    private String positionName;//职位名称
    private Long positionLevelId;//职级id
    private Long positionSeqId;//职位序列id
    private Long parentId;//上级职位id
    private Integer rank;//排序
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

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode == null ? null : positionCode.trim();
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName == null ? null : positionName.trim();
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

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Long getPositionLevelId() {
		return positionLevelId;
	}

	public void setPositionLevelId(Long positionLevelId) {
		this.positionLevelId = positionLevelId;
	}

	public Long getPositionSeqId() {
		return positionSeqId;
	}

	public void setPositionSeqId(Long positionSeqId) {
		this.positionSeqId = positionSeqId;
	}
}