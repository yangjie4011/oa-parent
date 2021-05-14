package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
  * @ClassName: CompanyTbl
  * @Description: 公司
  * @author minsheng
  * @date 2017年5月8日 下午1:10:17
 */
public class CompanyTbl implements Serializable{
      /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 49349553236219966L;

	private Long id;
    private String code;//公司编码
    private String name;//公司全称
    private Integer isUle;//是否是邮乐公司，0：否，1：是
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

	public Integer getIsUle() {
		return isUle;
	}

	public void setIsUle(Integer isUle) {
		this.isUle = isUle;
	}
}