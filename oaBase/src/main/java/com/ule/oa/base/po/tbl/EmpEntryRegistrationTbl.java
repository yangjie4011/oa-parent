package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;
/**
 * @ClassName: Emp_Entry_RegistRation
 * @Description:  入职登记表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class EmpEntryRegistrationTbl extends CommonPo implements Serializable {

	/**
	 * 入职登记表
	 */
	private static final long serialVersionUID = -1800304987223844530L;
	private Long id;
	private Integer type;//入职类型：0-正常入职，1-外包/实习生转内
	private Long companyId;//公司
	private Long leader;//汇报对象
	private Long departId;//部门Id
	private Long employeeTypeId;//员工类型
	private String cnName;//中文姓名
	private String engName;//英文名
	private Long positionId;//职位ID
	private String email;//邮箱
	private String mobile;//手机号
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date employmentDate;//入职日期
	private Long floorId;//楼层ID
	private String seatNo;//座位号
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date adCooperateDate;//行政协作时间
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date delayEntryDate;//延期入职日期
	private String adCooperateUser;//行政协作人
	private String extensionNumber;//分机号
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date itCooperateDate;//IT协作时间
	private String itCooperateUser;//IT协作人
	private Long entryStatus;//入职状态
	private Long processStatus;//流程状态
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date hrCooperateDate;//HR协作时间
	private String hrCooperateUser;//HR协作人
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date upDateTime;
	private String createUser;
	private String upDateUser;
	private Long workType;//工时类型
	private Long whetherScheduling;//是否排班
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getDelayEntryDate() {
		return delayEntryDate;
	}
	public void setDelayEntryDate(Date delayEntryDate) {
		this.delayEntryDate = delayEntryDate;
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
	public Long getEntryStatus() {
		return entryStatus;
	}
	public void setEntryStatus(Long entryStatus) {
		this.entryStatus = entryStatus;
	}
	public Long getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(Long processStatus) {
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
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getUpDateTime() {
		return upDateTime;
	}
	public void setUpDateTime(Date upDateTime) {
		this.upDateTime = upDateTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpDateUser() {
		return upDateUser;
	}
	public void setUpDateUser(String upDateUser) {
		this.upDateUser = upDateUser;
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
}