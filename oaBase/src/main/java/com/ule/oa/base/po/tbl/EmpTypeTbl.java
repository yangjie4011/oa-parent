package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
  * @ClassName: EmpTypeTbl
  * @Description: 员工类型表
  * @author minsheng
  * @date 2017年6月30日 下午2:26:58
 */
public class EmpTypeTbl implements Serializable{
      /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 2016054247816753433L;

	private Long id;
    private Long companyId;//公司id
    private String typeCName;//员工类型名称
    private Date createTime;
    private String createUser;
    private Date updateTime;
    private String updateUser;
    private Integer delFlag;
    private String remark;

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

    public String getTypeCName() {
        return typeCName;
    }

    public void setTypeCName(String typeCName) {
        this.typeCName = typeCName == null ? null : typeCName.trim();
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