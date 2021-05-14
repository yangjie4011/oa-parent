package com.ule.oa.base.po.tbl;

import java.util.Date;
/**
 * @ClassName: AttnTaskRecord
 * @Description: 考勤状态机记录表
 * @author zhoujinliang
 * @date 2018年7月4日
 */
public class AttnTaskRecordTbl {
    private Long id;

    private Long employId;

    //考勤日期
    private Date attnDate;

    //第一步，从考勤机获取数据状态（0：已处理,1：未处理）
    private Integer setSignRecordStatus;
    //第一步处理时间
    private Date setSignRecordTime;

    //第二步，从考勤数据汇总状态（0：已处理,1：未处理）
    private Integer setWorkHoursStatus;
    //第二步处理时间
    private Date setWorkHoursTime;

    //第三步，从考勤机获取数据状态（0：已处理,1：未处理）
    private Integer setAttnStatisticsStatus;
    //第三步处理时间
    private Date setAttnStatisticsTime;

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

    public Long getEmployId() {
        return employId;
    }

    public void setEmployId(Long employId) {
        this.employId = employId;
    }

    public Date getAttnDate() {
		return attnDate;
	}

	public void setAttnDate(Date attnDate) {
		this.attnDate = attnDate;
	}

	public Integer getSetSignRecordStatus() {
        return setSignRecordStatus;
    }

    public void setSetSignRecordStatus(Integer setSignRecordStatus) {
        this.setSignRecordStatus = setSignRecordStatus;
    }

    public Date getSetSignRecordTime() {
        return setSignRecordTime;
    }

    public void setSetSignRecordTime(Date setSignRecordTime) {
        this.setSignRecordTime = setSignRecordTime;
    }

    public Integer getSetWorkHoursStatus() {
        return setWorkHoursStatus;
    }

    public void setSetWorkHoursStatus(Integer setWorkHoursStatus) {
        this.setWorkHoursStatus = setWorkHoursStatus;
    }

    public Date getSetWorkHoursTime() {
        return setWorkHoursTime;
    }

    public void setSetWorkHoursTime(Date setWorkHoursTime) {
        this.setWorkHoursTime = setWorkHoursTime;
    }

    public Integer getSetAttnStatisticsStatus() {
        return setAttnStatisticsStatus;
    }

    public void setSetAttnStatisticsStatus(Integer setAttnStatisticsStatus) {
        this.setAttnStatisticsStatus = setAttnStatisticsStatus;
    }

    public Date getSetAttnStatisticsTime() {
        return setAttnStatisticsTime;
    }

    public void setSetAttnStatisticsTime(Date setAttnStatisticsTime) {
        this.setAttnStatisticsTime = setAttnStatisticsTime;
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
}