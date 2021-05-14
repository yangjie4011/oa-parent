package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**@ClassName: UPrivilege
 * @Description:权限管理表
 * @author xujintao
 * @date 2019年8月12日
 */
public class RabcPrivilegeTbl implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1859299469144345378L;

	//id
	private Long id;
	
	//权限类型（0-菜单权限，1-操作权限）
	private Integer type;
	
	//关联菜单资源id
	private Long resourceId;
	
	//创建时间
	private Date createTime;
	
	//创建人
    private String createUser;
    
    //修改时间
    private Date updateTime;
    
    //修改人
    private String updateUser;
    
    //删除标识
    private Integer delFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
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
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
    
}
