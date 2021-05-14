package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
  * @ClassName: AffairTbl
  * @Description: 日程信息
  * @author wufei
  * @date 2017年5月17日 上午9:08:10
 */
public class AffairTbl implements Serializable{
	
	private static final long serialVersionUID = 3335486829813252382L;
	private Long id;
    private String employeeId; //用户ID
    private Date beginTime; //起始日期
    private Date endTime; //结束日期
    private String type; //提醒类型 2：按日提醒 3：按周提醒 4：按月提醒 5：按年提醒 6：按工作日提醒
    private Date remindDate; //提醒日期
    private String content; //事务内容
    private Date lastRemind; //最近一次提醒的时间
    private String mobileRemind; //是否使用手机短信提醒 0：否，1是
    private Date lastMobileRemind; //最近一次手机短信提醒的时间
    private String emailRemind; //是否使用email提醒 0：否，1是
    private Date lastEmailRemind; //最近一次email提醒的时间
    private String taker; //参与人
    private String version; //版本号
    private Integer delFlag; //状态 1-有效 0-无效
    private Date createTime;
    private String createUser;
    private Date updateTime;
    private String updateUser;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getRemindDate() {
		return remindDate;
	}
	public void setRemindDate(Date remindDate) {
		this.remindDate = remindDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMobileRemind() {
		return mobileRemind;
	}
	public void setMobileRemind(String mobileRemind) {
		this.mobileRemind = mobileRemind;
	}
	public Date getLastRemind() {
		return lastRemind;
	}
	public void setLastRemind(Date lastRemind) {
		this.lastRemind = lastRemind;
	}
	public Date getLastMobileRemind() {
		return lastMobileRemind;
	}
	public void setLastMobileRemind(Date lastMobileRemind) {
		this.lastMobileRemind = lastMobileRemind;
	}
	public String getEmailRemind() {
		return emailRemind;
	}
	public void setEmailRemind(String emailRemind) {
		this.emailRemind = emailRemind;
	}
	public Date getLastEmailRemind() {
		return lastEmailRemind;
	}
	public void setLastEmailRemind(Date lastEmailRemind) {
		this.lastEmailRemind = lastEmailRemind;
	}
	public String getTaker() {
		return taker;
	}
	public void setTaker(String taker) {
		this.taker = taker;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Integer getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
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


}