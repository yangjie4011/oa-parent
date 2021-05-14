package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 出差申请表
 * @Description: 出差申请表
 * @author yangjie
 * @date 2017年6月13日
 */
public class EmpApplicationBusinessTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 7421443182252328796L;
	
	private Long id;
	//员工ID
	private Long employeeId;
	//员工姓名
	private String cnName;
	//员工编号
	private String code;
	//部门
	private Long departId;
	//部门名称
	private String departName;
	//职位名称
	private String positionName;
	//职位ID
	private Long positionId;
	//提交日期
	private Date submitDate;
	//预计开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startTime;
	//预计结束时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endTime;
	//预计工作时长
	private Double duration;
	//出差地址
	private String address;
	//交通工具：100-火车、200-飞机、300-汽车
	private Integer vehicle;
	//出差事由:100-项目出差、200-业务出差
	private Integer businessType;
	//事由说明
	private String reason;
	//审批状态：100-待审批 、200-已审批 、300-已拒
	private Integer approvalStatus;
	private Integer approvalReportStatus;
	//审批意见
	private String approvalReason;
	//出差流程实例id
	private String processinstanceId;
	//出差报表流程实例id
	private String processinstanceReportId;
	//备注
	private String remark;	
	//第一个目的地省
	private String travelProvince1;
	//第一个目的地市
	private String travelCity1;		
	//第2个目的地省
	private String travelProvince2;
	//第2个目的地市
	private String travelCity2;	
	//第3个目的地省
	private String travelProvince3;
	//第3个目的地市
	private String travelCity3;
	//第4个目的地省
	private String travelProvince4;
	//第4个目的地市
	private String travelCity4;	
	//第5个目的地省
	private String travelProvince5;
	//第5个目的地市
	private String travelCity5;	
	//出差开始省
	private String startProvinceAddress;
	//出差开始市
	private String startCityAddress;	
	//出差结束省
	private String endProvinceAddress;
	//出差结束市
	private String endCityAddress;	
	//原始单据id
	private Long originalBillId;
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public Long getDepartId() {
		return departId;
	}
	public void setDepartId(Long departId) {
		this.departId = departId;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	public Long getPositionId() {
		return positionId;
	}
	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
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
	public Double getDuration() {
		return duration;
	}
	public void setDuration(Double duration) {
		this.duration = duration;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getVehicle() {
		return vehicle;
	}
	public void setVehicle(Integer vehicle) {
		this.vehicle = vehicle;
	}
	public Integer getBusinessType() {
		return businessType;
	}
	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Integer getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public String getApprovalReason() {
		return approvalReason;
	}
	public void setApprovalReason(String approvalReason) {
		this.approvalReason = approvalReason;
	}
	public String getProcessinstanceId() {
		return processinstanceId;
	}
	public void setProcessinstanceId(String processinstanceId) {
		this.processinstanceId = processinstanceId;
	}
	public String getProcessinstanceReportId() {
		return processinstanceReportId;
	}
	public void setProcessinstanceReportId(String processinstanceReportId) {
		this.processinstanceReportId = processinstanceReportId;
	}
	public Integer getApprovalReportStatus() {
		return approvalReportStatus;
	}
	public void setApprovalReportStatus(Integer approvalReportStatus) {
		this.approvalReportStatus = approvalReportStatus;
	}
	public String getTravelProvince1() {
		return travelProvince1;
	}
	public void setTravelProvince1(String travelProvince1) {
		this.travelProvince1 = travelProvince1;
	}
	public String getTravelCity1() {
		return travelCity1;
	}
	public void setTravelCity1(String travelCity1) {
		this.travelCity1 = travelCity1;
	}
	public String getTravelProvince2() {
		return travelProvince2;
	}
	public void setTravelProvince2(String travelProvince2) {
		this.travelProvince2 = travelProvince2;
	}
	public String getTravelCity2() {
		return travelCity2;
	}
	public void setTravelCity2(String travelCity2) {
		this.travelCity2 = travelCity2;
	}
	public String getTravelProvince3() {
		return travelProvince3;
	}
	public void setTravelProvince3(String travelProvince3) {
		this.travelProvince3 = travelProvince3;
	}
	public String getTravelCity3() {
		return travelCity3;
	}
	public void setTravelCity3(String travelCity3) {
		this.travelCity3 = travelCity3;
	}
	public String getTravelProvince4() {
		return travelProvince4;
	}
	public void setTravelProvince4(String travelProvince4) {
		this.travelProvince4 = travelProvince4;
	}
	public String getTravelCity4() {
		return travelCity4;
	}
	public void setTravelCity4(String travelCity4) {
		this.travelCity4 = travelCity4;
	}
	public String getTravelProvince5() {
		return travelProvince5;
	}
	public void setTravelProvince5(String travelProvince5) {
		this.travelProvince5 = travelProvince5;
	}
	public String getTravelCity5() {
		return travelCity5;
	}
	public void setTravelCity5(String travelCity5) {
		this.travelCity5 = travelCity5;
	}
	public String getStartProvinceAddress() {
		return startProvinceAddress;
	}
	public void setStartProvinceAddress(String startProvinceAddress) {
		this.startProvinceAddress = startProvinceAddress;
	}
	public String getStartCityAddress() {
		return startCityAddress;
	}
	public void setStartCityAddress(String startCityAddress) {
		this.startCityAddress = startCityAddress;
	}
	public String getEndProvinceAddress() {
		return endProvinceAddress;
	}
	public void setEndProvinceAddress(String endProvinceAddress) {
		this.endProvinceAddress = endProvinceAddress;
	}
	public String getEndCityAddress() {
		return endCityAddress;
	}
	public void setEndCityAddress(String endCityAddress) {
		this.endCityAddress = endCityAddress;
	}
	public Long getOriginalBillId() {
		return originalBillId;
	}
	public void setOriginalBillId(Long originalBillId) {
		this.originalBillId = originalBillId;
	}
	
}
