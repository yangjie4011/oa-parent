package com.ule.oa.base.po.tbl;
/**
 * @ClassName: EmployeeApplicationLogTblVO
 * @Description: 员工申请日志
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class EmployeeApplicationLogTbl {

	private Long id;
	//手机号码
    private String mobile;
	//ule账户
    private String uleAccount;
	//登记住户人员姓名
    private String householdRegister;
	//婚姻状况
    private Long maritalStatus;
	//政治地位
    private Long politicalStatus;
	//政治地位其他
    private String politicalStatusOther;
	//地址
    private String address;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUleAccount() {
		return uleAccount;
	}

	public void setUleAccount(String uleAccount) {
		this.uleAccount = uleAccount;
	}

	public String getHouseholdRegister() {
		return householdRegister;
	}

	public void setHouseholdRegister(String householdRegister) {
		this.householdRegister = householdRegister;
	}

	public Long getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(Long maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Long getPoliticalStatus() {
		return politicalStatus;
	}

	public void setPoliticalStatus(Long politicalStatus) {
		this.politicalStatus = politicalStatus;
	}

	public String getPoliticalStatusOther() {
		return politicalStatusOther;
	}

	public void setPoliticalStatusOther(String politicalStatusOther) {
		this.politicalStatusOther = politicalStatusOther;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeApplicationLogTbl [id=");
		builder.append(id);
		builder.append(", mobile=");
		builder.append(mobile);
		builder.append(", uleAccount=");
		builder.append(uleAccount);
		builder.append(", householdRegister=");
		builder.append(householdRegister);
		builder.append(", maritalStatus=");
		builder.append(maritalStatus);
		builder.append(", politicalStatus=");
		builder.append(politicalStatus);
		builder.append(", politicalStatusOther=");
		builder.append(politicalStatusOther);
		builder.append(", address=");
		builder.append(address);
		builder.append("]");
		return builder.toString();
	}
    
}
