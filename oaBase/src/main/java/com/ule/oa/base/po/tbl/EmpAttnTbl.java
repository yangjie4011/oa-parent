package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;
/**
 * @ClassName: EmpAttnTbl(VO表)
 * @Description: 员工考勤VO表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class EmpAttnTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long id;
	//公司id
    private Long companyId;
	//员工id
    private Long employId;
	//考勤时间 
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date attnDate;
	//开始工作时间
    
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startWorkTime;
	//结束工作时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endWorkTime;
	//迟到时间
    private Integer comeLateTime;
	//早到时间 (不太确认)
    private Integer leftEarlyTime;
	//旷工时间
    private Integer absenteeismTime;
	//规定的考勤时间 （不太确认）
    private Double mustAttnTime;
	//全部的考勤时间
    private Double allAttnTime;
    //实际总出勤时间
    private Double actAttnTime;
	//锁定的考勤时间
    private Double lackAttnTime;
	//考勤状态
    private Integer attnStatus;

    private String remark;

    private Integer delFlag;

    private String createUser;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String updateUser;

    
	public Double getActAttnTime() {
		return actAttnTime;
	}

	public void setActAttnTime(Double actAttnTime) {
		this.actAttnTime = actAttnTime;
	}

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

	public Long getEmployId() {
		return employId;
	}

	public void setEmployId(Long employId) {
		this.employId = employId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getAttnDate() {
		return attnDate;
	}

	public void setAttnDate(Date attnDate) {
		this.attnDate = attnDate;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getStartWorkTime() {
		return startWorkTime;
	}

	public void setStartWorkTime(Date startWorkTime) {
		this.startWorkTime = startWorkTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEndWorkTime() {
		return endWorkTime;
	}

	public void setEndWorkTime(Date endWorkTime) {
		this.endWorkTime = endWorkTime;
	}

	public Integer getComeLateTime() {
		return comeLateTime;
	}

	public void setComeLateTime(Integer comeLateTime) {
		this.comeLateTime = comeLateTime;
	}

	public Integer getLeftEarlyTime() {
		return leftEarlyTime;
	}

	public void setLeftEarlyTime(Integer leftEarlyTime) {
		this.leftEarlyTime = leftEarlyTime;
	}

	public Integer getAbsenteeismTime() {
		return absenteeismTime;
	}

	public void setAbsenteeismTime(Integer absenteeismTime) {
		this.absenteeismTime = absenteeismTime;
	}

	public Double getMustAttnTime() {
		return mustAttnTime;
	}

	public void setMustAttnTime(Double mustAttnTime) {
		this.mustAttnTime = mustAttnTime;
	}

	public Double getAllAttnTime() {
		return allAttnTime;
	}

	public void setAllAttnTime(Double allAttnTime) {
		this.allAttnTime = allAttnTime;
	}

	public Double getLackAttnTime() {
		return lackAttnTime;
	}

	public void setLackAttnTime(Double lackAttnTime) {
		this.lackAttnTime = lackAttnTime;
	}

	public Integer getAttnStatus() {
		return attnStatus;
	}

	public void setAttnStatus(Integer attnStatus) {
		this.attnStatus = attnStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
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

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

}
