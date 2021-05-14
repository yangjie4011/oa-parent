package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
 * 
  * @ClassName: EmpProbationAssessmentTbl
  * @Description: 员工试用期评估表
  * @author jiwenhang
  * @date 2017年5月27日 下午1:13:32
 */
public class EmpProbationAssessmentTbl implements Serializable{
	
	private static final long serialVersionUID = 2988488215590397841L;

	private Long id;

    private Long employeeId;

    private Date employmentDate;

    private Date probationExpire;

    private Date oneWeekDate;

    private Date oneMonthDate;

    private Date threeMonthDate;

    private Date fiveMonthDate;

    private Integer entryStatus;

    private Integer midWorkSkills;

    private Integer midWorkQuality;

    private Integer midWorkEfficiency;

    private Integer midWorkRelatedKnowledge;

    private Integer midProblemSolvingSkills;

    private Integer midAttitude;

    private Integer midCommunicationSkills;

    private Integer midInitiativeIndependence;

    private Integer midManagementAbility;

    private Integer midWholeScore;

    private Integer midLeaderProcessStatus;

    private Integer midHrProcessStatus;

    private Integer finalWorkSkills;

    private Integer finalWorkQuality;

    private Integer finalWorkEfficiency;

    private Integer finalWorkRelatedKnowledge;

    private Integer finalProblemSolvingSkills;

    private Integer finalAttitude;

    private Integer finalCommunicationSkills;

    private Integer finalInitiativeIndependence;

    private Integer finalManagementAbility;

    private Integer finalWholeScore;

    private Integer hrId;

    private Date hrDate;

    private Integer dhId;

    private Date dhDate;

    private Integer dhStatus;

    private Integer leaderId;

    private Date leaderDate;

    private String version;

    private Long processStatus;

    private Integer delFlag;

    private Integer midEmployeeStatus;
    
    private String oneWeekEvaluation;

    private String oneMonthEvaluation;

    private String threeMonthEvaluation;

    private String fiveMonthEvaluation;

    private String midLeaderComments;

    private String midHrComments;

    private String finalLeaderComments;

    private String employeeAchievement;

    private String employeeAdvantagesAndDisadvantages;

    private String employeeWorkObjective;

    private String finalEmployeeComments;

    private String finalHrComments;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String updateUser;

    private Integer leaderStatus;

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

    public Date getOneWeekDate() {
        return oneWeekDate;
    }

    public void setOneWeekDate(Date oneWeekDate) {
        this.oneWeekDate = oneWeekDate;
    }

    public Date getOneMonthDate() {
        return oneMonthDate;
    }

    public void setOneMonthDate(Date oneMonthDate) {
        this.oneMonthDate = oneMonthDate;
    }

    public Date getThreeMonthDate() {
        return threeMonthDate;
    }

    public void setThreeMonthDate(Date threeMonthDate) {
        this.threeMonthDate = threeMonthDate;
    }

    public Date getFiveMonthDate() {
        return fiveMonthDate;
    }

    public void setFiveMonthDate(Date fiveMonthDate) {
        this.fiveMonthDate = fiveMonthDate;
    }

    public Integer getEntryStatus() {
        return entryStatus;
    }

    public void setEntryStatus(Integer entryStatus) {
        this.entryStatus = entryStatus;
    }

    public Integer getMidWorkSkills() {
        return midWorkSkills;
    }

    public void setMidWorkSkills(Integer midWorkSkills) {
        this.midWorkSkills = midWorkSkills;
    }

    public Integer getMidWorkQuality() {
        return midWorkQuality;
    }

    public void setMidWorkQuality(Integer midWorkQuality) {
        this.midWorkQuality = midWorkQuality;
    }

    public Integer getMidWorkEfficiency() {
        return midWorkEfficiency;
    }

    public void setMidWorkEfficiency(Integer midWorkEfficiency) {
        this.midWorkEfficiency = midWorkEfficiency;
    }

    public Integer getMidWorkRelatedKnowledge() {
        return midWorkRelatedKnowledge;
    }

    public void setMidWorkRelatedKnowledge(Integer midWorkRelatedKnowledge) {
        this.midWorkRelatedKnowledge = midWorkRelatedKnowledge;
    }

    public Integer getMidProblemSolvingSkills() {
        return midProblemSolvingSkills;
    }

    public void setMidProblemSolvingSkills(Integer midProblemSolvingSkills) {
        this.midProblemSolvingSkills = midProblemSolvingSkills;
    }

    public Integer getMidAttitude() {
        return midAttitude;
    }

    public void setMidAttitude(Integer midAttitude) {
        this.midAttitude = midAttitude;
    }

    public Integer getMidCommunicationSkills() {
        return midCommunicationSkills;
    }

    public void setMidCommunicationSkills(Integer midCommunicationSkills) {
        this.midCommunicationSkills = midCommunicationSkills;
    }

    public Integer getMidInitiativeIndependence() {
        return midInitiativeIndependence;
    }

    public void setMidInitiativeIndependence(Integer midInitiativeIndependence) {
        this.midInitiativeIndependence = midInitiativeIndependence;
    }

    public Integer getMidManagementAbility() {
        return midManagementAbility;
    }

    public void setMidManagementAbility(Integer midManagementAbility) {
        this.midManagementAbility = midManagementAbility;
    }

    public Integer getMidWholeScore() {
        return midWholeScore;
    }

    public void setMidWholeScore(Integer midWholeScore) {
        this.midWholeScore = midWholeScore;
    }

    public Integer getMidLeaderProcessStatus() {
        return midLeaderProcessStatus;
    }

    public void setMidLeaderProcessStatus(Integer midLeaderProcessStatus) {
        this.midLeaderProcessStatus = midLeaderProcessStatus;
    }

    public Integer getMidHrProcessStatus() {
        return midHrProcessStatus;
    }

    public void setMidHrProcessStatus(Integer midHrProcessStatus) {
        this.midHrProcessStatus = midHrProcessStatus;
    }

    public Integer getFinalWorkSkills() {
        return finalWorkSkills;
    }

    public void setFinalWorkSkills(Integer finalWorkSkills) {
        this.finalWorkSkills = finalWorkSkills;
    }

    public Integer getFinalWorkQuality() {
        return finalWorkQuality;
    }

    public void setFinalWorkQuality(Integer finalWorkQuality) {
        this.finalWorkQuality = finalWorkQuality;
    }

    public Integer getFinalWorkEfficiency() {
        return finalWorkEfficiency;
    }

    public void setFinalWorkEfficiency(Integer finalWorkEfficiency) {
        this.finalWorkEfficiency = finalWorkEfficiency;
    }

    public Integer getFinalWorkRelatedKnowledge() {
        return finalWorkRelatedKnowledge;
    }

    public void setFinalWorkRelatedKnowledge(Integer finalWorkRelatedKnowledge) {
        this.finalWorkRelatedKnowledge = finalWorkRelatedKnowledge;
    }

    public Integer getFinalProblemSolvingSkills() {
        return finalProblemSolvingSkills;
    }

    public void setFinalProblemSolvingSkills(Integer finalProblemSolvingSkills) {
        this.finalProblemSolvingSkills = finalProblemSolvingSkills;
    }

    public Integer getFinalAttitude() {
        return finalAttitude;
    }

    public void setFinalAttitude(Integer finalAttitude) {
        this.finalAttitude = finalAttitude;
    }

    public Integer getFinalCommunicationSkills() {
        return finalCommunicationSkills;
    }

    public void setFinalCommunicationSkills(Integer finalCommunicationSkills) {
        this.finalCommunicationSkills = finalCommunicationSkills;
    }

    public Integer getFinalInitiativeIndependence() {
        return finalInitiativeIndependence;
    }

    public void setFinalInitiativeIndependence(Integer finalInitiativeIndependence) {
        this.finalInitiativeIndependence = finalInitiativeIndependence;
    }

    public Integer getFinalManagementAbility() {
        return finalManagementAbility;
    }

    public void setFinalManagementAbility(Integer finalManagementAbility) {
        this.finalManagementAbility = finalManagementAbility;
    }

    public Integer getFinalWholeScore() {
        return finalWholeScore;
    }

    public void setFinalWholeScore(Integer finalWholeScore) {
        this.finalWholeScore = finalWholeScore;
    }

    public Integer getHrId() {
        return hrId;
    }

    public void setHrId(Integer hrId) {
        this.hrId = hrId;
    }

    public Date getHrDate() {
        return hrDate;
    }

    public void setHrDate(Date hrDate) {
        this.hrDate = hrDate;
    }

    public Integer getDhId() {
        return dhId;
    }

    public void setDhId(Integer dhId) {
        this.dhId = dhId;
    }

    public Date getDhDate() {
        return dhDate;
    }

    public void setDhDate(Date dhDate) {
        this.dhDate = dhDate;
    }

    public Integer getDhStatus() {
        return dhStatus;
    }

    public void setDhStatus(Integer dhStatus) {
        this.dhStatus = dhStatus;
    }

    public Integer getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
    }

    public Date getLeaderDate() {
        return leaderDate;
    }

    public void setLeaderDate(Date leaderDate) {
        this.leaderDate = leaderDate;
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

    public Integer getMidEmployeeStatus() {
        return midEmployeeStatus;
    }

    public void setMidEmployeeStatus(Integer midEmployeeStatus) {
        this.midEmployeeStatus = midEmployeeStatus;
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

    public Integer getLeaderStatus() {
        return leaderStatus;
    }

    public void setLeaderStatus(Integer leaderStatus) {
        this.leaderStatus = leaderStatus;
    }

	public String getOneWeekEvaluation() {
		return oneWeekEvaluation;
	}

	public void setOneWeekEvaluation(String oneWeekEvaluation) {
		this.oneWeekEvaluation = oneWeekEvaluation;
	}

	public String getOneMonthEvaluation() {
		return oneMonthEvaluation;
	}

	public void setOneMonthEvaluation(String oneMonthEvaluation) {
		this.oneMonthEvaluation = oneMonthEvaluation;
	}

	public String getThreeMonthEvaluation() {
		return threeMonthEvaluation;
	}

	public void setThreeMonthEvaluation(String threeMonthEvaluation) {
		this.threeMonthEvaluation = threeMonthEvaluation;
	}

	public String getFiveMonthEvaluation() {
		return fiveMonthEvaluation;
	}

	public void setFiveMonthEvaluation(String fiveMonthEvaluation) {
		this.fiveMonthEvaluation = fiveMonthEvaluation;
	}

	public String getMidLeaderComments() {
		return midLeaderComments;
	}

	public void setMidLeaderComments(String midLeaderComments) {
		this.midLeaderComments = midLeaderComments;
	}

	public String getMidHrComments() {
		return midHrComments;
	}

	public void setMidHrComments(String midHrComments) {
		this.midHrComments = midHrComments;
	}

	public String getFinalLeaderComments() {
		return finalLeaderComments;
	}

	public void setFinalLeaderComments(String finalLeaderComments) {
		this.finalLeaderComments = finalLeaderComments;
	}

	public String getEmployeeAchievement() {
		return employeeAchievement;
	}

	public void setEmployeeAchievement(String employeeAchievement) {
		this.employeeAchievement = employeeAchievement;
	}

	public String getEmployeeAdvantagesAndDisadvantages() {
		return employeeAdvantagesAndDisadvantages;
	}

	public void setEmployeeAdvantagesAndDisadvantages(
			String employeeAdvantagesAndDisadvantages) {
		this.employeeAdvantagesAndDisadvantages = employeeAdvantagesAndDisadvantages;
	}

	public String getEmployeeWorkObjective() {
		return employeeWorkObjective;
	}

	public void setEmployeeWorkObjective(String employeeWorkObjective) {
		this.employeeWorkObjective = employeeWorkObjective;
	}

	public String getFinalEmployeeComments() {
		return finalEmployeeComments;
	}

	public void setFinalEmployeeComments(String finalEmployeeComments) {
		this.finalEmployeeComments = finalEmployeeComments;
	}

	public String getFinalHrComments() {
		return finalHrComments;
	}

	public void setFinalHrComments(String finalHrComments) {
		this.finalHrComments = finalHrComments;
	}
}