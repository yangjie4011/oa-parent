package com.ule.oa.base.po.tbl;

import java.util.Date;

import javax.persistence.Id;

public class ActPermissionTbl {
	@Id
    private Long id;

    private Integer type;
    //流程key
    private String processKey;

    private String processName;
    //活动节点id
    private String actId;

    private String actName;

    private Date createTime;

    private Long createEmpId;

    private Date updateTime;

    private Long updateEmpId;

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

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey == null ? null : processKey.trim();
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName == null ? null : processName.trim();
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId == null ? null : actId.trim();
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName == null ? null : actName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateEmpId() {
        return createEmpId;
    }

    public void setCreateEmpId(Long createEmpId) {
        this.createEmpId = createEmpId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateEmpId() {
        return updateEmpId;
    }

    public void setUpdateEmpId(Long updateEmpId) {
        this.updateEmpId = updateEmpId;
    }
}