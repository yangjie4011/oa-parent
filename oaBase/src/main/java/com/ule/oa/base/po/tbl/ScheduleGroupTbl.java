package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;

/**@ClassName: ScheduleGroupTbl
 * @Description:排班班组表
 * @author xujintao
 * @date 2019年3月6日
 */
public class ScheduleGroupTbl implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8238710872904105662L;
	
	//id
	private Long id;
	
	//部门id
	private Long departId;
	
	//分组名称
	private String name;
	
	//排班人
	private Long scheduler;
	
	//审核人
	private Long auditor;
	
	//创建时间
	private Date createTime;
	
	//创建人
    private Long createUser;
    
    //修改时间
    private Date updateTime;
    
    //修改人
    private Long updateUser;
    
    //删除标识(0未删除，1删除)
    private Integer delFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getScheduler() {
		return scheduler;
	}

	public void setScheduler(Long scheduler) {
		this.scheduler = scheduler;
	}

	public Long getAuditor() {
		return auditor;
	}

	public void setAuditor(Long auditor) {
		this.auditor = auditor;
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

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Long getCreateUser() {
		return createUser;
	}

	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}

	public Long getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}
	
}
