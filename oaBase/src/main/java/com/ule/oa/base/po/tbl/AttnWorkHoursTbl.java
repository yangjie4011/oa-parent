package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * @ClassName: Attn_Work_Hours
 * @Description: 考勤工时表
 * @author zhoujinliang
 * @date 2018年7月4日
 */
public class AttnWorkHoursTbl implements Serializable{
	private static final long serialVersionUID = -5207649535042503303L;

	private Long id;
    //公司id
    private Long companyId;
    //员工id
    private Long employeeId;

    //工作日期
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date workDate;

    //考勤状态，0：正常，1：异常
    private Integer attnStatus;

    //数据类型0：打卡考勤数据，其他:单据申请考勤数据
    private Integer dataType;
    //请假类型
    private Integer leaveType;
    //开始时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    //结束时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    //工作小时
    private Double workHours;

    private Integer delFlag;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String updateUser;
    
    private String dataReason;
    
    private Long billId;//单据id，用于还考勤

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
        return employeeId;
    }

    public void setEmployId(Long employId) {
        this.employeeId = employId;
    }
    
    public Integer getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(Integer leaveType) {
		this.leaveType = leaveType;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public Integer getAttnStatus() {
        return attnStatus;
    }

    public void setAttnStatus(Integer attnStatus) {
        this.attnStatus = attnStatus;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Double getWorkHours() {
		return workHours;
	}

	public void setWorkHours(Double workHours) {
		this.workHours = workHours;
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
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

	public String getDataReason() {
		return dataReason;
	}

	public void setDataReason(String dataReason) {
		this.dataReason = dataReason;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}
    
	
}