package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmployeeTbl;

/**
  * @ClassName: Employee
  * @Description: 员工个人信息表
  * @author minsheng
  * @date 2017年5月8日 下午1:45:30
 */
@JsonInclude(Include.NON_NULL)
public class Employee extends EmployeeTbl {
	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -5500293609497315841L;
	private String yearAndMonth;//年月	
	private Integer year;//年
	private int exportStauts;//导出状态 1导出 0 不导出
	//自定义对象
	private Depart depart;
	private EmpDepart empDepart;//员工部门关系表
	private EmpPosition empPosition;//员工职位关系表
	private EmpType empType;//员工类型
	private CoopCompany coopCompany;//合作公司
	private EmpContract empContract;//员工合同
	private Company company;//员工入职公司
	private List<EmpFamilyMember> empFamilyMembers;//家属表
	private List<EmpUrgentContact> empUrgentContacts;//紧急联系人
	private List<EmpSchool> empSchools;//教育经历
	private List<EmpTraining> empTrainings;//培训经历
	private List<EmpWorkRecord> empWorkRecords;//工作经历
	
	//自定义属性
	private Long leaderId;//员工领导ID（员工表id）
	private String leaderName;//员工领导中文名
	private String departLeaderName;//部门领导中文名
	private String politicalName;//政治面貌中文名
	private String industryRelevanceName;//行业相关性中文名
	private String degreeOfEducationName;//文化程度中文名.
	private String nationName;//民族中文名
	private String empTypeName;//工时类型中文名
	private String workTypeName;//工时类型中文名
	private String whetherSchedulingName;//是否排版中文名
	private String maritalStatusName;//婚姻状况中文名
	private String parentName; //上一级机构名称
	private String positionName; //职称
	private String departName; //部门名称
	private String levelName; //职级
	private String condition; //查询条件string
	private String reportToLeaderName;
	private Long positionId;
	private Long departId;
	private String companyName;//所属公司名称
	private String departmentName;//技术开发部
	private String quitTimeStauts;// 离职日期职位空 作为状态判断
	private Long version;
	private String school;//毕业院校
	private String noticeStr;//入离职通知人员的变量值
	private String pageStr;//页面上临时显示的值
	private String sendEmail;//页面上发送邮件值
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date attnDate;//计算考勤时，暂存员工的考勤日期
	private Long attnTaskId;//计算考勤时，暂存员工的考勤数据主键
	private String scheduleCnName;//排班人姓名
	private String scheduleGroupName;//排班组别名称
	private Long groupId;
	private Long empTypeId;//员工id类型集合
	private List<Long> ids;//员工id集合
	private List<Long> JobStatusList;//员工id集合
	private List<String> codeList;//员工code集合
	
	private List<Long> absenteeismids;//旷工员工id集合
	private List<Integer> departList;//部门
	private List<Long> currentUserDepart;//当前用户所授权的部门集合
	private Integer page;
	private Integer rows;
	private Integer firstDepart;//页面上一级部门
	
	private Integer secondDepart;//页面上二级部门
	
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date protocolEndTimeBegin;//合同到期日 查询开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date protocolEndTimeEnd;//合同到期日 查询结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date firstEntryTimeBegin;//入职日期 查询开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date firstEntryTimeEnd;//入职日期 查询结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date probationEndTimeBegin;//试用到期日 查询开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date probationEndTimeEnd;//试用到期日 查询结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date quitTimeBegin;//离职日期 查询开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date quitTimeEnd;//离职日期 查询结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date quitEltTimeEnd;//离职日期 查询小于等于 结束时间的日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date quitTimeUpdateTime;//离职日期 修改完离职日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date quitUpdateBefore;//修改离职时间  之前选择的离职时间
    private String jobStatusName;//在职状态中文名
    private Long departLeaderId;//部门领导id
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthdayBegin;//出身日期开始
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthdayEnd;//出身日期结束
	
	private String nameCodeStr;//用于模糊查询name或者code
	private List<Long> subEmployeeIdList;//所属下属员工id列表
	private List<Long> empTypeIdList;//员工类型id列表

	

	

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getScheduleCnName() {
		return scheduleCnName;
	}

	public void setScheduleCnName(String scheduleCnName) {
		this.scheduleCnName = scheduleCnName;
	}

	public String getScheduleGroupName() {
		return scheduleGroupName;
	}

	public void setScheduleGroupName(String scheduleGroupName) {
		this.scheduleGroupName = scheduleGroupName;
	}


	public Long getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Long empTypeId) {
		this.empTypeId = empTypeId;
	}

	public List<Long> getSubEmployeeIdList() {
		return subEmployeeIdList;
	}

	public void setSubEmployeeIdList(List<Long> subEmployeeIdList) {
		this.subEmployeeIdList = subEmployeeIdList;
	}

	public String getNameCodeStr() {
		return nameCodeStr;
	}

	public void setNameCodeStr(String nameCodeStr) {
		this.nameCodeStr = nameCodeStr;
	}

	public List<Long> getCurrentUserDepart() {
		return currentUserDepart;
	}

	public void setCurrentUserDepart(List<Long> currentUserDepart) {
		this.currentUserDepart = currentUserDepart;
	}

	public List<String> getCodeList() {
		return codeList;
	}

	public void setCodeList(List<String> codeList) {
		this.codeList = codeList;
	}

	public List<Long> getJobStatusList() {
		return JobStatusList;
	}

	public void setJobStatusList(List<Long> jobStatusList) {
		JobStatusList = jobStatusList;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(String sendEmail) {
		this.sendEmail = sendEmail;
	}

	public Date getQuitEltTimeEnd() {
		return quitEltTimeEnd;
	}

	public void setQuitEltTimeEnd(Date quitEltTimeEnd) {
		this.quitEltTimeEnd = quitEltTimeEnd;
	}

	public String getDepartLeaderName() {
		return departLeaderName;
	}

	public void setDepartLeaderName(String departLeaderName) {
		this.departLeaderName = departLeaderName;
	}

	public Long getDepartLeaderId() {
		return departLeaderId;
	}

	public void setDepartLeaderId(Long departLeaderId) {
		this.departLeaderId = departLeaderId;
	}

	public Date getQuitUpdateBefore() {
		return quitUpdateBefore;
	}

	public void setQuitUpdateBefore(Date quitUpdateBefore) {
		this.quitUpdateBefore = quitUpdateBefore;
	}

	public Date getQuitTimeUpdateTime() {
		return quitTimeUpdateTime;
	}

	public void setQuitTimeUpdateTime(Date quitTimeUpdateTime) {
		this.quitTimeUpdateTime = quitTimeUpdateTime;
	}

	public String getJobStatusName() {
		return jobStatusName;
	}

	public void setJobStatusName(String jobStatusName) {
		this.jobStatusName = jobStatusName;
	}

	public Long getLeaderId() {
		return leaderId;
	}

	public void setLeaderId(Long leaderId) {
		this.leaderId = leaderId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getAttnDate() {
		return attnDate;
	}

	public void setAttnDate(Date attnDate) {
		this.attnDate = attnDate;
	}

	public Long getAttnTaskId() {
		return attnTaskId;
	}

	public void setAttnTaskId(Long attnTaskId) {
		this.attnTaskId = attnTaskId;
	}
	
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getReportToLeaderName() {
		return reportToLeaderName;
	}

	public void setReportToLeaderName(String reportToLeaderName) {
		this.reportToLeaderName = reportToLeaderName;
	}

	public String getMaritalStatusName() {
		return maritalStatusName;
	}

	public void setMaritalStatusName(String maritalStatusName) {
		this.maritalStatusName = maritalStatusName;
	}

	public String getWhetherSchedulingName() {
		return whetherSchedulingName;
	}

	public void setWhetherSchedulingName(String whetherSchedulingName) {
		this.whetherSchedulingName = whetherSchedulingName;
	}

	public String getWorkTypeName() {
		return workTypeName;
	}

	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}

	public String getNationName() {
		return nationName;
	}

	public void setNationName(String nationName) {
		this.nationName = nationName;
	}

	public String getDegreeOfEducationName() {
		return degreeOfEducationName;
	}

	public void setDegreeOfEducationName(String degreeOfEducationName) {
		this.degreeOfEducationName = degreeOfEducationName;
	}

	public String getPoliticalName() {
		return politicalName;
	}

	public void setPoliticalName(String politicalName) {
		this.politicalName = politicalName;
	}

	public String getIndustryRelevanceName() {
		return industryRelevanceName;
	}

	public void setIndustryRelevanceName(String industryRelevanceName) {
		this.industryRelevanceName = industryRelevanceName;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public EmpType getEmpType() {
		return empType;
	}

	public void setEmpType(EmpType empType) {
		this.empType = empType;
	}

	public CoopCompany getCoopCompany() {
		return coopCompany;
	}

	public void setCoopCompany(CoopCompany coopCompany) {
		this.coopCompany = coopCompany;
	}

	public EmpContract getEmpContract() {
		return empContract;
	}

	public void setEmpContract(EmpContract empContract) {
		this.empContract = empContract;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<EmpFamilyMember> getEmpFamilyMembers() {
		return empFamilyMembers;
	}

	public void setEmpFamilyMembers(List<EmpFamilyMember> empFamilyMembers) {
		this.empFamilyMembers = empFamilyMembers;
	}

	public List<EmpUrgentContact> getEmpUrgentContacts() {
		return empUrgentContacts;
	}

	public void setEmpUrgentContacts(List<EmpUrgentContact> empUrgentContacts) {
		this.empUrgentContacts = empUrgentContacts;
	}

	public List<EmpSchool> getEmpSchools() {
		return empSchools;
	}

	public void setEmpSchools(List<EmpSchool> empSchools) {
		this.empSchools = empSchools;
	}

	public List<EmpTraining> getEmpTrainings() {
		return empTrainings;
	}

	public void setEmpTrainings(List<EmpTraining> empTrainings) {
		this.empTrainings = empTrainings;
	}

	public List<EmpWorkRecord> getEmpWorkRecords() {
		return empWorkRecords;
	}

	public void setEmpWorkRecords(List<EmpWorkRecord> empWorkRecords) {
		this.empWorkRecords = empWorkRecords;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public EmpDepart getEmpDepart() {
		return empDepart;
	}

	public void setEmpDepart(EmpDepart empDepart) {
		this.empDepart = empDepart;
	}

	public EmpPosition getEmpPosition() {
		return empPosition;
	}

	public void setEmpPosition(EmpPosition empPosition) {
		this.empPosition = empPosition;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public List<Integer> getDepartList() {
		return departList;
	}

	public void setDepartList(List<Integer> departList) {
		this.departList = departList;
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

	public Integer getFirstDepart() {
		return firstDepart;
	}

	public void setFirstDepart(Integer firstDepart) {
		this.firstDepart = firstDepart;
	}

	public Integer getSecondDepart() {
		return secondDepart;
	}

	public void setSecondDepart(Integer secondDepart) {
		this.secondDepart = secondDepart;
	}

	public Depart getDepart() {
		return depart;
	}

	public void setDepart(Depart depart) {
		this.depart = depart;
	}
	
	public String getYearAndMonth() {
		return yearAndMonth;
	}

	public void setYearAndMonth(String yearAndMonth) {
		this.yearAndMonth = yearAndMonth;
	}

	public int getExportStauts() {
		return exportStauts;
	}

	public void setExportStauts(int exportStauts) {
		this.exportStauts = exportStauts;
	}

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public Date getProtocolEndTimeBegin() {
		return protocolEndTimeBegin;
	}

	public void setProtocolEndTimeBegin(Date protocolEndTimeBegin) {
		this.protocolEndTimeBegin = protocolEndTimeBegin;
	}

	public Date getProtocolEndTimeEnd() {
		return protocolEndTimeEnd;
	}

	public void setProtocolEndTimeEnd(Date protocolEndTimeEnd) {
		this.protocolEndTimeEnd = protocolEndTimeEnd;
	}

	public Date getFirstEntryTimeBegin() {
		return firstEntryTimeBegin;
	}

	public void setFirstEntryTimeBegin(Date firstEntryTimeBegin) {
		this.firstEntryTimeBegin = firstEntryTimeBegin;
	}

	public Date getFirstEntryTimeEnd() {
		return firstEntryTimeEnd;
	}

	public void setFirstEntryTimeEnd(Date firstEntryTimeEnd) {
		this.firstEntryTimeEnd = firstEntryTimeEnd;
	}

	public Date getProbationEndTimeBegin() {
		return probationEndTimeBegin;
	}

	public void setProbationEndTimeBegin(Date probationEndTimeBegin) {
		this.probationEndTimeBegin = probationEndTimeBegin;
	}

	public Date getProbationEndTimeEnd() {
		return probationEndTimeEnd;
	}

	public void setProbationEndTimeEnd(Date probationEndTimeEnd) {
		this.probationEndTimeEnd = probationEndTimeEnd;
	}

	public Date getQuitTimeBegin() {
		return quitTimeBegin;
	}

	public void setQuitTimeBegin(Date quitTimeBegin) {
		this.quitTimeBegin = quitTimeBegin;
	}

	public Date getQuitTimeEnd() {
		return quitTimeEnd;
	}

	public void setQuitTimeEnd(Date quitTimeEnd) {
		this.quitTimeEnd = quitTimeEnd;
	}

	public String getEmpTypeName() {
		return empTypeName;
	}

	public void setEmpTypeName(String empTypeName) {
		this.empTypeName = empTypeName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}


    public String getQuitTimeStauts() {
		return quitTimeStauts;
	}

	public void setQuitTimeStauts(String quitTimeStauts) {
		this.quitTimeStauts = quitTimeStauts;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public Date getBirthdayBegin() {
		return birthdayBegin;
	}

	public void setBirthdayBegin(Date birthdayBegin) {
		this.birthdayBegin = birthdayBegin;
	}

	public Date getBirthdayEnd() {
		return birthdayEnd;
	}

	public void setBirthdayEnd(Date birthdayEnd) {
		this.birthdayEnd = birthdayEnd;
	}


	public String getPageStr() {
		return pageStr;
	}

	public void setPageStr(String pageStr) {
		this.pageStr = pageStr;
	}

	public List<Long> getAbsenteeismids() {
		return absenteeismids;
	}

	public void setAbsenteeismids(List<Long> absenteeismids) {
		this.absenteeismids = absenteeismids;
	}

	public String getNoticeStr() {
		return noticeStr;
	}

	public void setNoticeStr(String noticeStr) {
		this.noticeStr = noticeStr;
	}

	public List<Long> getEmpTypeIdList() {
		return empTypeIdList;
	}

	public void setEmpTypeIdList(List<Long> empTypeIdList) {
		this.empTypeIdList = empTypeIdList;
	}
	
}
