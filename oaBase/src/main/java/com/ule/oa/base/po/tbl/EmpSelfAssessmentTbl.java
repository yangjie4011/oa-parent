package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
/**
 * @ClassName: Base_Emp_Self_AssessMeNt
 * @Description: 员工自我评估
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class EmpSelfAssessmentTbl implements Serializable{
	
	private static final long serialVersionUID = -3915441378317390755L;

	private Long id;

    private Long employeeId;
    //试用期开始
    private Date employmentDate;
    //试用期结束
    private Date probationExpire;
    //入职状态
    private Integer entryStatus;
    //流程状态
    private Long processStatus;
    //版本号
    private String version;
    
    private Integer delFlag;
    
    private Date createTime;
    
    private Date updateTime;
    
    private String createUser;
    
    private String updateUser;
    //入职一周
    private String entryWeek;
    //入职一个月
    private String entryOneMonth;
    //入职三个月
    private String entryThreeMonth;
    //入职五个月
    private String entryFiveMonth;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getProbationExpire() {
        return probationExpire;
    }

    public void setProbationExpire(Date probationExpire) {
        this.probationExpire = probationExpire;
    }

    public Integer getEntryStatus() {
        return entryStatus;
    }

    public void setEntryStatus(Integer entryStatus) {
        this.entryStatus = entryStatus;
    }

    public Long getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(Long processStatus) {
        this.processStatus = processStatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
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

	public String getEntryWeek() {
		return entryWeek;
	}

	public void setEntryWeek(String entryWeek) {
		this.entryWeek = entryWeek;
	}

	public String getEntryOneMonth() {
		return entryOneMonth;
	}

	public void setEntryOneMonth(String entryOneMonth) {
		this.entryOneMonth = entryOneMonth;
	}

	public String getEntryThreeMonth() {
		return entryThreeMonth;
	}

	public void setEntryThreeMonth(String entryThreeMonth) {
		this.entryThreeMonth = entryThreeMonth;
	}

	public String getEntryFiveMonth() {
		return entryFiveMonth;
	}

	public void setEntryFiveMonth(String entryFiveMonth) {
		this.entryFiveMonth = entryFiveMonth;
	}
}