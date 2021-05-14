package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;

/**@ClassName: URoleTbl
 * @Description:角色管理表
 * @author xujintao
 * @date 2019年4月12日
 */
public class RabcUserRoleTbl implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1859299469144345378L;

	//id
	private Long id;
	
	//用户表id
	private Long userId;
	
	//角色表id
	private Long roleId;
	
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
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
		this.createUser = createUser;
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
		this.updateUser = updateUser;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
   
    

}
