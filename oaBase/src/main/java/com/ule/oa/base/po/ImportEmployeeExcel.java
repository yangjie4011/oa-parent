package com.ule.oa.base.po;

import java.util.Date;

import com.ule.oa.common.utils.excel.Excel;

public class ImportEmployeeExcel {
	@Excel(name="公司名称")
	private String company;	//公司名称
	@Excel(name="员工类型")
	private String empType;	//员工类型
	@Excel(name="员工编号")
	private String code;	//员工编号
	@Excel(name="中文名")
	private String cnName;	//中文名
	@Excel(name="英文名")
	private String engName;	//英文名
	@Excel(name="入职日期")
	private Date firstEntryTime;	//入职日期
	@Excel(name="司龄")
	private Double ourAge;			//司龄
	@Excel(name="离职日期")
	private Date quitTime;			//离职日期
	@Excel(name="汇报对象")
	private String reportToLeader;	//汇报对象
	@Excel(name="汇报对象编号")
	private String reportToLeaderCode;//汇报对象编号
	@Excel(name="部门")
	private String departName;		//部门
	@Excel(name="子部门")
	private String childDepart;		//子部门
	@Excel(name="部门负责人")
	private String departLeader;	//部门负责人
	@Excel(name="部门负责人编号")
	private String departLeaderCode;//部门负责人编号
	@Excel(name="职位")
	private String positionTitle;	//职位
	@Excel(name="工作地点（省）")
	private String workAddressProvince;	//工作地点（省）
	@Excel(name="工作地点（市）")
	private String workAddressCity;	//工作地点（市）
	@Excel(name="出生日期")
	private Date birthDay;			//出生日期
	@Excel(name="性别")
	private String sex;				//性别
	@Excel(name="职级")
	private String poistionLevel;	//职级
	@Excel(name="职位序列")
	private String poistionSeq;		//职位序列
	@Excel(name="工时种类")
	private String workType;		//工时种类
	@Excel(name="是否排班")
	private String whetherScheduling;	//是否排班
	@Excel(name="工作邮箱")
	private String email;			//工作邮箱
	@Excel(name="手机号")
	private String phoneNum;		//手机号
	@Excel(name="指纹编号")
	private Long fingerPrint;		//指纹
	

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getEngName() {
		return engName;
	}

	public void setEngName(String engName) {
		this.engName = engName;
	}

	public Date getFirstEntryTime() {
		return firstEntryTime;
	}

	public void setFirstEntryTime(Date firstEntryTime) {
		this.firstEntryTime = firstEntryTime;
	}

	public Double getOurAge() {
		return ourAge;
	}

	public void setOurAge(Double ourAge) {
		this.ourAge = ourAge;
	}

	public Date getQuitTime() {
		return quitTime;
	}

	public void setQuitTime(Date quitTime) {
		this.quitTime = quitTime;
	}

	public String getReportToLeader() {
		return reportToLeader;
	}

	public void setReportToLeader(String reportToLeader) {
		this.reportToLeader = reportToLeader;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getChildDepart() {
		return childDepart;
	}

	public void setChildDepart(String childDepart) {
		this.childDepart = childDepart;
	}

	public String getDepartLeader() {
		return departLeader;
	}

	public void setDepartLeader(String departLeader) {
		this.departLeader = departLeader;
	}

	public String getPositionTitle() {
		return positionTitle;
	}

	public void setPositionTitle(String positionTitle) {
		this.positionTitle = positionTitle;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPoistionLevel() {
		return poistionLevel;
	}

	public void setPoistionLevel(String poistionLevel) {
		this.poistionLevel = poistionLevel;
	}

	public String getPoistionSeq() {
		return poistionSeq;
	}

	public void setPoistionSeq(String poistionSeq) {
		this.poistionSeq = poistionSeq;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getWhetherScheduling() {
		return whetherScheduling;
	}

	public void setWhetherScheduling(String whetherScheduling) {
		this.whetherScheduling = whetherScheduling;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public Long getFingerPrint() {
		return fingerPrint;
	}

	public void setFingerPrint(Long fingerPrint) {
		this.fingerPrint = fingerPrint;
	}

	public String getReportToLeaderCode() {
		return reportToLeaderCode;
	}

	public void setReportToLeaderCode(String reportToLeaderCode) {
		this.reportToLeaderCode = reportToLeaderCode;
	}

	public String getDepartLeaderCode() {
		return departLeaderCode;
	}

	public void setDepartLeaderCode(String departLeaderCode) {
		this.departLeaderCode = departLeaderCode;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getWorkAddressProvince() {
		return workAddressProvince;
	}

	public void setWorkAddressProvince(String workAddressProvince) {
		this.workAddressProvince = workAddressProvince;
	}

	public String getWorkAddressCity() {
		return workAddressCity;
	}

	public void setWorkAddressCity(String workAddressCity) {
		this.workAddressCity = workAddressCity;
	}
}
