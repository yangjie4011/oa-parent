package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
  * @ClassName: EmpMidtermAssessmentTbl
  * @Description: 员工转正期中评估表
  * @author minsheng
  * @date 017年5月7日 上午10:01:10
 */
public class EmpMidtermAssessmentTbl implements Serializable{
	
	private static final long serialVersionUID = 845671303188165937L;

	private Long id;

    private Long employeeId;

    private Date employmentDate;

    private Date probationExpire;

    private Integer entryStatus;

    private Integer workSkills;

    private Integer workQuality;

    private Integer workEfficiency;

    private Integer workRelatedKnowledge;

    private Integer problemSolvingSkills;

    private Integer attitude;

    private Integer communicationSkills;

    private Integer initiativeIndependence;

    private Integer managementAbility;

    private Integer wholeScore;

    private Integer hrId;

    private Integer leaderId;

    private String version;

    private Long processStatus;

    private Integer delFlag;
    
    private String entryWeek;

    private String entryOneMonth;

    private String entryThreeMonth;

    private String entryFiveMonth;

    private String hrComments;

    private String hrStatus;

    private String leaderComments;

    private String employeeStatus;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String updateUser;

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

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
    }

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

    public Integer getWorkSkills() {
        return workSkills;
    }

    public void setWorkSkills(Integer workSkills) {
        this.workSkills = workSkills;
    }

    public Integer getWorkQuality() {
        return workQuality;
    }

    public void setWorkQuality(Integer workQuality) {
        this.workQuality = workQuality;
    }

    public Integer getWorkEfficiency() {
        return workEfficiency;
    }

    public void setWorkEfficiency(Integer workEfficiency) {
        this.workEfficiency = workEfficiency;
    }

    public Integer getWorkRelatedKnowledge() {
        return workRelatedKnowledge;
    }

    public void setWorkRelatedKnowledge(Integer workRelatedKnowledge) {
        this.workRelatedKnowledge = workRelatedKnowledge;
    }

    public Integer getProblemSolvingSkills() {
        return problemSolvingSkills;
    }

    public void setProblemSolvingSkills(Integer problemSolvingSkills) {
        this.problemSolvingSkills = problemSolvingSkills;
    }

    public Integer getAttitude() {
        return attitude;
    }

    public void setAttitude(Integer attitude) {
        this.attitude = attitude;
    }

    public Integer getCommunicationSkills() {
        return communicationSkills;
    }

    public void setCommunicationSkills(Integer communicationSkills) {
        this.communicationSkills = communicationSkills;
    }

    public Integer getInitiativeIndependence() {
        return initiativeIndependence;
    }

    public void setInitiativeIndependence(Integer initiativeIndependence) {
        this.initiativeIndependence = initiativeIndependence;
    }

    public Integer getManagementAbility() {
        return managementAbility;
    }

    public void setManagementAbility(Integer managementAbility) {
        this.managementAbility = managementAbility;
    }

    public Integer getWholeScore() {
        return wholeScore;
    }

    public void setWholeScore(Integer wholeScore) {
        this.wholeScore = wholeScore;
    }

    public Integer getHrId() {
        return hrId;
    }

    public void setHrId(Integer hrId) {
        this.hrId = hrId;
    }

    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Long getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(Long processStatus) {
        this.processStatus = processStatus;
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

	public String getHrComments() {
		return hrComments;
	}

	public void setHrComments(String hrComments) {
		this.hrComments = hrComments;
	}

	public String getHrStatus() {
		return hrStatus;
	}

	public void setHrStatus(String hrStatus) {
		this.hrStatus = hrStatus;
	}

	public String getLeaderComments() {
		return leaderComments;
	}

	public void setLeaderComments(String leaderComments) {
		this.leaderComments = leaderComments;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}
}