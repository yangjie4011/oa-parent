package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
  * @ClassName: EmployeeTbl
  * @Description: 员工个人信息表
  * @author minsheng
  * @date 2017年5月8日 下午1:36:56
 */
public class EmployeeTbl extends CommonPo implements Serializable{
	
	private static final long serialVersionUID = -2207795842321361467L;

	private Long id;
    private Long companyId;//公司id
    private Long coopCompanyId;//合作公司id
    private String code;//员工编号
    private Long empTypeId;//员工类型
    private String cnName;//中文名
    private String engName;//英文名
    private String picture;//员工靓照
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthday;//生日
    private Integer sex;//性别（0：男，1：女）
    private Integer age;//年龄
    private Double workAge;//总工龄
    private Double beforeWorkAge;//入职前工龄
    private Double ourAge;//司龄
    private String householdRegister;//户籍
    private Long politicalStatus;//政治面貌
    private String politicalStatusOther;//政治面貌（其它）
    private Long degreeOfEducation;//文化程度
    private String degreeOfEducationOther;//文化程度（其它）
    private Integer contractRenewal;//合同续签次数
    private String email;//邮件
    private Long maritalStatus;//婚姻状况
    private Integer country;//国家
    private String countryOther;//国家_其它
    private Long nation;//民族
    private String address;//在沪居住地址
    private String mobile;//手机
    private String telephone;//电话
    private String extensionNumber;//分机
    private Long industryRelevance;//行业相关性
    private String industryRelevanceOther;//行业相关性_其它
    private String workingBackground;//从业背景
    private String uleAccount;//邮乐帐号
    private Long workType;//工时类型
    private Long whetherScheduling;//是否排班
    private Integer entryStatus;//入职状态
    private String floorCode;//楼层号
    private String seatCode;//座位id
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date firstEntryTime;//入职日期
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date quitTime;//离职日期
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date salaryBalanceDate;//薪资结算日期（离职时候要设置该字段）
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date protocolEndTime;//协议到期日
    private Date createTime;
    private String createUser;
    private Date updateTime;
    private String updateUser;
    private Integer delFlag;//删除标识，0：正常，1：删除
    private String remark;//备注
    private Integer jobStatus;//工作状态（0：在职，1：离职  2：待离职  3：已提出离职 （待确认：前台web做出离职操作））
    private String positionTitle;//职称
    private Long reportToLeader;//汇报对象
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date probationEndTime;//试用期截止日
    private Long fingerprintId;//指纹ID
    private Integer autoCalculateLeave;//是否计算年假:0.不计算,1.计算,2.生成下一年年假数据按上一年年假数据来
    private String identificationNum;//员工识别号，20190909001
    private String workAddressProvince;//工作地点-省
    private String workAddressCity;//工作地点-市
    private Integer workAddressType;//0-本地员工（上海），1-外地员工
    private String positionSeq;//职位序列
    private String positionLevel;//职位等级
    private String photo;//员工照片
    
    
    public Integer getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(Integer jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName == null ? null : cnName.trim();
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName == null ? null : engName.trim();
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getHouseholdRegister() {
        return householdRegister;
    }

    public void setHouseholdRegister(String householdRegister) {
        this.householdRegister = householdRegister == null ? null : householdRegister.trim();
    }

    public Long getPoliticalStatus() {
        return politicalStatus;
    }

    public void setPoliticalStatus(Long politicalStatus) {
        this.politicalStatus = politicalStatus;
    }

    public Long getDegreeOfEducation() {
        return degreeOfEducation;
    }

    public void setDegreeOfEducation(Long degreeOfEducation) {
        this.degreeOfEducation = degreeOfEducation;
    }

    public Integer getContractRenewal() {
        return contractRenewal;
    }

    public void setContractRenewal(Integer contractRenewal) {
        this.contractRenewal = contractRenewal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Long getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(Long maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Long getNation() {
        return nation;
    }

    public void setNation(Long nation) {
        this.nation = nation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public Long getIndustryRelevance() {
        return industryRelevance;
    }

    public void setIndustryRelevance(Long industryRelevance) {
        this.industryRelevance = industryRelevance;
    }

    public String getUleAccount() {
        return uleAccount;
    }

    public void setUleAccount(String uleAccount) {
        this.uleAccount = uleAccount == null ? null : uleAccount.trim();
    }

    public Long getWorkType() {
        return workType;
    }

    public void setWorkType(Long workType) {
        this.workType = workType;
    }

    public Long getWhetherScheduling() {
        return whetherScheduling;
    }

    public void setWhetherScheduling(Long whetherScheduling) {
        this.whetherScheduling = whetherScheduling;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Long empTypeId) {
		this.empTypeId = empTypeId;
	}

	public String getExtensionNumber() {
		return extensionNumber;
	}

	public void setExtensionNumber(String extensionNumber) {
		this.extensionNumber = extensionNumber;
	}

	public Integer getEntryStatus() {
		return entryStatus;
	}

	public void setEntryStatus(Integer entryStatus) {
		this.entryStatus = entryStatus;
	}
	
	public Double getWorkAge() {
		return workAge;
	}

	public void setWorkAge(Double workAge) {
		this.workAge = workAge;
	}

	public Double getBeforeWorkAge() {
		return beforeWorkAge;
	}

	public void setBeforeWorkAge(Double beforeWorkAge) {
		this.beforeWorkAge = beforeWorkAge;
	}

	public Double getOurAge() {
		return ourAge;
	}

	public void setOurAge(Double ourAge) {
		this.ourAge = ourAge;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getFirstEntryTime() {
		return firstEntryTime;
	}

	public void setFirstEntryTime(Date firstEntryTime) {
		this.firstEntryTime = firstEntryTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getQuitTime() {
		return quitTime;
	}
	
	public void setQuitTime(Date quitTime) {
		this.quitTime = quitTime;
	}

	public String getIndustryRelevanceOther() {
		return industryRelevanceOther;
	}

	public void setIndustryRelevanceOther(String industryRelevanceOther) {
		this.industryRelevanceOther = industryRelevanceOther;
	}

	public void setWorkingBackground(String workingBackground) {
		this.workingBackground = workingBackground;
	}

	public String getWorkingBackground() {
		return workingBackground;
	}

	public String getPoliticalStatusOther() {
		return politicalStatusOther;
	}

	public void setPoliticalStatusOther(String politicalStatusOther) {
		this.politicalStatusOther = politicalStatusOther;
	}

	public String getDegreeOfEducationOther() {
		return degreeOfEducationOther;
	}

	public void setDegreeOfEducationOther(String degreeOfEducationOther) {
		this.degreeOfEducationOther = degreeOfEducationOther;
	}

	public Integer getCountry() {
		return country;
	}

	public void setCountry(Integer country) {
		this.country = country;
	}

	public String getCountryOther() {
		return countryOther;
	}

	public void setCountryOther(String countryOther) {
		this.countryOther = countryOther;
	}

	public String getPositionTitle() {
		return positionTitle;
	}

	public void setPositionTitle(String positionTitle) {
		this.positionTitle = positionTitle;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Long getCoopCompanyId() {
		return coopCompanyId;
	}

	public void setCoopCompanyId(Long coopCompanyId) {
		this.coopCompanyId = coopCompanyId;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getSalaryBalanceDate() {
		return salaryBalanceDate;
	}

	public void setSalaryBalanceDate(Date salaryBalanceDate) {
		this.salaryBalanceDate = salaryBalanceDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getProtocolEndTime() {
		return protocolEndTime;
	}

	public void setProtocolEndTime(Date protocolEndTime) {
		this.protocolEndTime = protocolEndTime;
	}

	public Long getReportToLeader() {
		return reportToLeader;
	}

	public void setReportToLeader(Long reportToLeader) {
		this.reportToLeader = reportToLeader;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getProbationEndTime() {
		return probationEndTime;
	}

	public void setProbationEndTime(Date probationEndTime) {
		this.probationEndTime = probationEndTime;
	}

	public String getFloorCode() {
		return floorCode;
	}

	public void setFloorCode(String floorCode) {
		this.floorCode = floorCode;
	}

	public String getSeatCode() {
		return seatCode;
	}

	public void setSeatCode(String seatCode) {
		this.seatCode = seatCode;
	}

	public Long getFingerprintId() {
		return fingerprintId;
	}

	public void setFingerprintId(Long fingerprintId) {
		this.fingerprintId = fingerprintId;
	}

	public Integer getAutoCalculateLeave() {
		return autoCalculateLeave;
	}

	public void setAutoCalculateLeave(Integer autoCalculateLeave) {
		this.autoCalculateLeave = autoCalculateLeave;
	}

	public String getIdentificationNum() {
		return identificationNum;
	}

	public void setIdentificationNum(String identificationNum) {
		this.identificationNum = identificationNum;
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

	public Integer getWorkAddressType() {
		return workAddressType;
	}

	public void setWorkAddressType(Integer workAddressType) {
		this.workAddressType = workAddressType;
	}

	public String getPositionSeq() {
		return positionSeq;
	}

	public void setPositionSeq(String positionSeq) {
		this.positionSeq = positionSeq;
	}

	public String getPositionLevel() {
		return positionLevel;
	}

	public void setPositionLevel(String positionLevel) {
		this.positionLevel = positionLevel;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	
}