package com.ule.oa.base.po;

import java.util.List;

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
public class EmployeeApp extends EmployeeTbl {
	    /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -5698861307560451160L;
	/**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	

	//自定义对象
	private Depart depart;//员工部门关系表
	private Position position;//员工职位关系表
	private Company company;//员工入职公司
	private EmpContract empContract;//员工合同
	private EmpPosition empPosition;//员工职位
	private EmpDepart empDepart;//员工部门
	
	//自定义属性
	private String nameOrCode;//员工领导中文名
	private String leaderName;//员工领导中文名
	private String politicalName;//政治面貌中文名
	private String industryRelevanceName;//行业相关性中文名
	private String degreeOfEducationName;//文化程度中文名.
	private String nationName;//民族中文名
	private String countryName;//国籍中文名
	private String workTypeName;//工时类型中文名
	private String whetherSchedulingName;//是否排版中文名
	private String empTypeName;//员工类型中文名
	private String maritalStatusName;//婚姻状况中文名
    private String reportToLeaderName;//汇报对象
    private List<Long> empTypeIdList;//员工类型列表
	
	
	public String getEmpTypeName() {
		return empTypeName;
	}

	public void setEmpTypeName(String empTypeName) {
		this.empTypeName = empTypeName;
	}

	public String getReportToLeaderName() {
		return reportToLeaderName;
	}

	public void setReportToLeaderName(String reportToLeaderName) {
		this.reportToLeaderName = reportToLeaderName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
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

	public String getDegreeOfEducationName() {
		return degreeOfEducationName;
	}

	public void setDegreeOfEducationName(String degreeOfEducationName) {
		this.degreeOfEducationName = degreeOfEducationName;
	}

	public String getNationName() {
		return nationName;
	}

	public void setNationName(String nationName) {
		this.nationName = nationName;
	}

	public String getWorkTypeName() {
		return workTypeName;
	}

	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}

	public String getWhetherSchedulingName() {
		return whetherSchedulingName;
	}

	public void setWhetherSchedulingName(String whetherSchedulingName) {
		this.whetherSchedulingName = whetherSchedulingName;
	}

	public String getMaritalStatusName() {
		return maritalStatusName;
	}

	public void setMaritalStatusName(String maritalStatusName) {
		this.maritalStatusName = maritalStatusName;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public Depart getDepart() {
		return depart;
	}

	public void setDepart(Depart depart) {
		this.depart = depart;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public String getNameOrCode() {
		return nameOrCode;
	}

	public void setNameOrCode(String nameOrCode) {
		this.nameOrCode = nameOrCode;
	}

	public EmpPosition getEmpPosition() {
		return empPosition;
	}

	public void setEmpPosition(EmpPosition empPosition) {
		this.empPosition = empPosition;
	}

	public EmpDepart getEmpDepart() {
		return empDepart;
	}

	public void setEmpDepart(EmpDepart empDepart) {
		this.empDepart = empDepart;
	}

	public List<Long> getEmpTypeIdList() {
		return empTypeIdList;
	}

	public void setEmpTypeIdList(List<Long> empTypeIdList) {
		this.empTypeIdList = empTypeIdList;
	}
	

}
