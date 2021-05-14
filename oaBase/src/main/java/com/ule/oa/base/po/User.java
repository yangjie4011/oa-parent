package com.ule.oa.base.po;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.UserTbl;

/**
 * @ClassName: UserTbl
 * @Description: 用户信息
 * @author minsheng
 * @date 2017年5月10日 上午10:08:10
*/
@JsonInclude(Include.NON_NULL)
public class User extends UserTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 7245680597756525262L;
	
	//自定义字段
	private String companyCode;
    private String phone;//手机号
	private String randomCode;//验证码
	private String oldPwd;//原密码
	private String returnCode;//登录返回的状态码
	
	//自定义对象
	private Employee employee;
	private List<Long> ids;
	private List<Long> employeeIds;
	private Depart depart;
	private Position position;
	private Company company;
	private List<Long> JobStatusList;//在职状态集合
	private List<Long> empTypeIdList;//员工类型集合

	
	private String cnName;
	private String code;
	private String roleStr;
	private Long roleId;
	private List<Long> roleIdList;
	private String employIdsStr;
	private Long departId;
	

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<Long> getRoleIdList() {
		return roleIdList;
	}

	public void setRoleIdList(List<Long> roleIdList) {
		this.roleIdList = roleIdList;
	}

	public String getEmployIdsStr() {
		return employIdsStr;
	}

	public void setEmployIdsStr(String employIdsStr) {
		this.employIdsStr = employIdsStr;
	}

	public String getRoleStr() {
		return roleStr;
	}

	public void setRoleStr(String roleStr) {
		this.roleStr = roleStr;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Depart getDepart() {
		return depart;
	}

	public void setDepart(Depart depart) {
		this.depart = depart;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getRandomCode() {
		return randomCode;
	}

	public void setRandomCode(String randomCode) {
		this.randomCode = randomCode;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public List<Long> getEmployeeIds() {
		return employeeIds;
	}

	public void setEmployeeIds(List<Long> employeeIds) {
		this.employeeIds = employeeIds;
	}

	public List<Long> getJobStatusList() {
		return JobStatusList;
	}

	public void setJobStatusList(List<Long> jobStatusList) {
		JobStatusList = jobStatusList;
	}

	public List<Long> getEmpTypeIdList() {
		return empTypeIdList;
	}

	public void setEmpTypeIdList(List<Long> empTypeIdList) {
		this.empTypeIdList = empTypeIdList;
	}
	
}
