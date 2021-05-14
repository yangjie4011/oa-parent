package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;


/**
 * @ClassName: EmployeeRegister
 * @Description: 员工入职登记表
 * @author yangjie
 * @date 2017年5月22日
*/
public class EmpApplicationRegisterTbl extends CommonPo implements Serializable{
	
	private static final long serialVersionUID = -1933328013909000951L;
    
	private Long id;
	//入职类型：0-正常入职，1-外包/实习生转内
	private Integer type;
	//公司
	private Long companyId;
	//汇报对象（领导）
	private Long leader;
	//部门Id
	private Long departId;
	//员工类型
	private Long employeeTypeId;
	//中文姓名
	private String cnName;
    //英文名
    private String engName;
    //职位ID
    private Long positionId;
    //邮箱
    private String email;
    //手机号
    private String mobile;
    //入职日期
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date employmentDate;
    //延期入职日期
    private Date delayEntryDate;
    //楼层ID
    private Long floorId;
    //座位ID
    private String seatNo;
    //行政协作时间
    private Date adCooperateDate;
    //行政协作人
    private String adCooperateUser;
    //分机号
    private String extensionNumber;
    //IT协作时间
    private Date itCooperateDate;
    //IT协作人
    private String itCooperateUser;
    //入职状态
    private Integer entryStatus;
    //流程状态
    private Integer processStatus;
    //HR协作时间
    private Date hrCooperateDate;
    //HR协作人
    private String hrCooperateUser;
    
    private Long workType;//工时类型
	private Long whetherScheduling;//是否排班
	private Long fingerprintId;//指纹ID
	private String code;//员工编号
	//审批状态：100-待审批 、200-已审批 、300-已拒、400-已撤销
	private Integer approvalStatus;
	//流程实例id
	private String processInstanceId;
	//申请人ID
	private Long employeeId;
	//性别（0-男，1-女）
	private Integer sex;
	private String departName;//申请人部门名称
	private String remarks;//备注
	private Long groupId;//排班组别id
	private Long backEmployeeId;//入职成功员工表回传的id
    private String toPersions;//邮件抄送人
    private String toEmails;//抄送邮箱
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthDate;//出生日期
    private String identificationNum;//员工识别号，20190909001
    private String workAddressProvince;//工作地点-省
    private String workAddressCity;//工作地点-市
    private Integer workAddressType;//0-本地员工（上海），1-外地员工
    private String positionSeq;//职位序列
    private String positionLevel;//职位等级
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getLeader() {
		return leader;
	}

	public void setLeader(Long leader) {
		this.leader = leader;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public Long getEmployeeTypeId() {
		return employeeTypeId;
	}

	public void setEmployeeTypeId(Long employeeTypeId) {
		this.employeeTypeId = employeeTypeId;
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

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEmploymentDate() {
		return employmentDate;
	}

	public void setEmploymentDate(Date employmentDate) {
		this.employmentDate = employmentDate;
	}
	
	public Long getFloorId() {
		return floorId;
	}

	public void setFloorId(Long floorId) {
		this.floorId = floorId;
	}
	
	public String getSeatNo() {
		return seatNo;
	}

	public void setSeatNo(String seatNo) {
		this.seatNo = seatNo;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getAdCooperateDate() {
		return adCooperateDate;
	}

	public void setAdCooperateDate(Date adCooperateDate) {
		this.adCooperateDate = adCooperateDate;
	}

	public String getAdCooperateUser() {
		return adCooperateUser;
	}

	public void setAdCooperateUser(String adCooperateUser) {
		this.adCooperateUser = adCooperateUser;
	}

	public String getExtensionNumber() {
		return extensionNumber;
	}

	public void setExtensionNumber(String extensionNumber) {
		this.extensionNumber = extensionNumber;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getItCooperateDate() {
		return itCooperateDate;
	}

	public void setItCooperateDate(Date itCooperateDate) {
		this.itCooperateDate = itCooperateDate;
	}

	public String getItCooperateUser() {
		return itCooperateUser;
	}

	public void setItCooperateUser(String itCooperateUser) {
		this.itCooperateUser = itCooperateUser;
	}

	public Integer getEntryStatus() {
		return entryStatus;
	}

	public void setEntryStatus(Integer entryStatus) {
		this.entryStatus = entryStatus;
	}

	public Integer getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(Integer processStatus) {
		this.processStatus = processStatus;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getHrCooperateDate() {
		return hrCooperateDate;
	}

	public void setHrCooperateDate(Date hrCooperateDate) {
		this.hrCooperateDate = hrCooperateDate;
	}

	public String getHrCooperateUser() {
		return hrCooperateUser;
	}

	public void setHrCooperateUser(String hrCooperateUser) {
		this.hrCooperateUser = hrCooperateUser;
	}

	public Date getDelayEntryDate() {
		return delayEntryDate;
	}

	public void setDelayEntryDate(Date delayEntryDate) {
		this.delayEntryDate = delayEntryDate;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	public Long getFingerprintId() {
		return fingerprintId;
	}

	public void setFingerprintId(Long fingerprintId) {
		this.fingerprintId = fingerprintId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getBackEmployeeId() {
		return backEmployeeId;
	}

	public void setBackEmployeeId(Long backEmployeeId) {
		this.backEmployeeId = backEmployeeId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getToPersions() {
		return toPersions;
	}

	public void setToPersions(String toPersions) {
		this.toPersions = toPersions;
	}

	public String getToEmails() {
		return toEmails;
	}

	public void setToEmails(String toEmails) {
		this.toEmails = toEmails;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
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
	
}
