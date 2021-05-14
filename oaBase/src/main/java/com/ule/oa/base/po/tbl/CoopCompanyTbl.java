package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;
/**
 * @ClassName: Base_Coop_Company
 * @Description: 合作公司表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class CoopCompanyTbl implements Serializable{
      /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -5209898949854358450L;

	private Long id;
    private Long empTypeId;//员工类型id
    private String coopCompanyCode;//合作公司编号
    private String coopCompanyName;//合作公司名称
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

    public Long getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Long empTypeId) {
		this.empTypeId = empTypeId;
	}

	public String getCoopCompanyCode() {
        return coopCompanyCode;
    }

    public void setCoopCompanyCode(String coopCompanyCode) {
        this.coopCompanyCode = coopCompanyCode == null ? null : coopCompanyCode.trim();
    }

    public String getCoopCompanyName() {
        return coopCompanyName;
    }

    public void setCoopCompanyName(String coopCompanyName) {
        this.coopCompanyName = coopCompanyName == null ? null : coopCompanyName.trim();
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