package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
  * @ClassName: EmpTrainingTbl
  * @Description: 员工培训表
  * @author minsheng
  * @date 2017年5月8日 下午1:38:13
 */
public class EmpTrainingTbl implements Serializable{
      /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -7633339808015341766L;

	private Long id;
    private Long employeeId;//员工id
    private Integer isCompanyTraining;//是否是当前公司培训（0：否，1：是）
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startTime;//开始时间
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endTime;//结束时间
    private String trainingInstitutions;//培训机构
    private String trainingProName;//培训项目名称
    private String content;//培训内容
    private String obtainCertificate;//获得证书
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String createUser;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String updateUser;
    private Integer delFlag;
    private String remark;
    private Long version;

    public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

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

    public Integer getIsCompanyTraining() {
        return isCompanyTraining;
    }

    public void setIsCompanyTraining(Integer isCompanyTraining) {
        this.isCompanyTraining = isCompanyTraining;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTrainingInstitutions() {
        return trainingInstitutions;
    }

    public void setTrainingInstitutions(String trainingInstitutions) {
        this.trainingInstitutions = trainingInstitutions == null ? null : trainingInstitutions.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getObtainCertificate() {
        return obtainCertificate;
    }

    public void setObtainCertificate(String obtainCertificate) {
        this.obtainCertificate = obtainCertificate == null ? null : obtainCertificate.trim();
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

	public String getTrainingProName() {
		return trainingProName;
	}

	public void setTrainingProName(String trainingProName) {
		this.trainingProName = trainingProName;
	}
}