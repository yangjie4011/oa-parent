package com.ule.oa.common.po;

import java.util.Date;

import org.apache.poi.ss.formula.functions.T;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.utils.PageModel;

public class CommonPo {
	/** 删除 */
	public static final Integer STATUS_DELETE = 1;
	/** 正常 */
	public static final Integer STATUS_NORMAL = 0;
	//待办ID
	private Long ruProcdefId;
	//版本号
	private Long version;
	//状态: 0-有效 1-无效
	private Integer delFlag;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;
	//创建人
	private String createUser;
	//更新人
	private String updateUser;
	private Integer page;
	private Integer rows;
	private Integer offset;
	private Integer limit;
	private PageModel<T> pm = new PageModel<T>();
	
	public Long getRuProcdefId() {
		return ruProcdefId;
	}
	public void setRuProcdefId(Long ruProcdefId) {
		this.ruProcdefId = ruProcdefId;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public Integer getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getRows() {
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public PageModel<T> getPm() {
		return pm;
	}
	public void setPm(PageModel<T> pm) {
		this.pm = pm;
	}
	
}
