package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpLeaveTbl;

@JsonInclude(Include.NON_NULL)
public class EmpLeave extends EmpLeaveTbl{

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -5927334758454249367L;
	
	private Date planStartTime;//计划请假开始时间
	private Date planEndTime;//计划请假结束时间
	private Double planDays;//计划请假时间
	private String optUser;//操作人
	private boolean applyStatus;//审核状态
    private Double lawDays;//法定年假总天数
    private Double welfareDays;//福利年假总天数
    private List<Long> ids;
    private List<Integer> yearList;
    
	private Employee employee;//员工
	private Double leave1;//法定年假总数-年假
	private Double leave2;//法定年假总数-法定年假
	private Double leave3;//法定年假总数-福利年假
	private Double leave4;//法定年假总数-带薪病假
	private Double leave5;//剩余假期天数-去年法定年假
	private Double leave6;//剩余假期天数-去年福利年假
	private Double leave7;//剩余假期天数-当年法定年假
	private Double leave8;//剩余假期天数-当年福利年假
	private Double leave9;//剩余假期天数-年假
	private Double leave10;//剩余假期天数-带薪病假
	private Double leave11;//剩余假期天数-调休小时数
	private Double leave12;//截止目前剩余假期天数-当年法定年假
	private Double leave13;//截止目前剩余假期天数-当年福利年假
	private Double leave31;//截止目前剩余假期天数-带薪病假
	private Double leave14;//当年已使用假期天数-法定年假
	private Double leave15;//当年已使用假期天数-福利年假
	private Double leave16;//当年已使用假期天数-年假
	private Double leave17;//当年已使用假期天数-带薪病假
	private Double leave18;//当年已使用假期天数-非带薪病假
	private Double leave19;//当年已使用假期天数-事假
	private Double leave20;//当年已使用假期天数-调休小时数
	private Double leave21;//当年已使用假期天数-婚假
	private Double leave22;//当年已使用假期天数-丧假
	private Double leave23;//当年已使用假期天数-陪产假
	private Double leave24;//当年已使用假期天数-产前假
	private Double leave25;//当年已使用假期天数-产假
	private Double leave26;//当年已使用假期天数-哺乳假
	private Double leave27;//当年已使用假期天数-流产假
	private Double leave28;//当年已使用假期天数-其他
	private Double leave29;//透支假期天数-年假
	private Double leave30;//透支假期天数-带薪病假
	private Double leave32;//去年年假总天数-法定
	private Double leave33;//去年年假总天数-福利
	private Double leave34;//去年已用年假天数-法定
	private Double leave35;//去年已用年假天数-福利
	private Double leave36;//去年剩余调休小时数
	private Double leave37;//去年已用调休小时数
	private Integer month;//请假月份
	private String leaveType;//假期类型：1.年假  2.病假 3.调休 4.其他假期
	private Long empapplicationleaveId;//请假单id
	private Long originalBillId;//原单据id（针对假期撤销功能）
	private String remark;//假期修改备注
	
	private String cnName;
	private String code;
	private String departName;
	private Integer departId;
	private long jobStatus;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date quitTime;
	private List<Long> subEmployeeIdList;//所属下属员工id列表

	private List<Long> currentUserDepart;//当前用户所授权的部门集合
	
	private List<Long> jobStatusList;
	
	private List<Long> empTypeIdList;//员工类型id列表
	
	
	public List<Long> getJobStatusList() {
		return jobStatusList;
	}

	public void setJobStatusList(List<Long> jobStatusList) {
		this.jobStatusList = jobStatusList;
	}

	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startTimeFormat;//用于页面格式 后段处理
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endTimeFormat;//用于页面格式 后段处理
	
	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getStartTimeFormat() {
		return startTimeFormat;
	}

	public void setStartTimeFormat(Date startTimeFormat) {
		this.startTimeFormat = startTimeFormat;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getEndTimeFormat() {
		return endTimeFormat;
	}

	public void setEndTimeFormat(Date endTimeFormat) {
		this.endTimeFormat = endTimeFormat;
	}

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getQuitTime() {
		return quitTime;
	}

	public void setQuitTime(Date quitTime) {
		this.quitTime = quitTime;
	}

	public long getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(long jobStatus) {
		this.jobStatus = jobStatus;
	}

	public List<Long> getSubEmployeeIdList() {
		return subEmployeeIdList;
	}

	public void setSubEmployeeIdList(List<Long> subEmployeeIdList) {
		this.subEmployeeIdList = subEmployeeIdList;
	}

	public List<Long> getCurrentUserDepart() {
		return currentUserDepart;
	}

	public void setCurrentUserDepart(List<Long> currentUserDepart) {
		this.currentUserDepart = currentUserDepart;
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

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public Integer getDepartId() {
		return departId;
	}

	public void setDepartId(Integer departId) {
		this.departId = departId;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public Date getPlanStartTime() {
		return planStartTime;
	}

	public void setPlanStartTime(Date planStartTime) {
		this.planStartTime = planStartTime;
	}

	public Date getPlanEndTime() {
		return planEndTime;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setPlanEndTime(Date planEndTime) {
		this.planEndTime = planEndTime;
	}

	public Double getWelfareDays() {
		return welfareDays;
	}

	public void setWelfareDays(Double welfareDays) {
		this.welfareDays = welfareDays;
	}

	public Double getPlanDays() {
		return planDays;
	}

	public void setPlanDays(Double planDays) {
		this.planDays = planDays;
	}

	public String getOptUser() {
		return optUser;
	}

	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}

	public boolean getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(boolean applyStatus) {
		this.applyStatus = applyStatus;
	}

	public Double getLawDays() {
		return lawDays;
	}

	public void setLawDays(Double lawDays) {
		this.lawDays = lawDays;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Double getLeave1() {
		return converToDouble(leave1);
	}

	public void setLeave1(Double leave1) {
		this.leave1 = leave1;
	}

	public Double getLeave2() {
		return converToDouble(leave2);
	}

	public void setLeave2(Double leave2) {
		this.leave2 = leave2;
	}

	public Double getLeave3() {
		return converToDouble(leave3);
	}

	public void setLeave3(Double leave3) {
		this.leave3 = leave3;
	}

	public Double getLeave4() {
		return converToDouble(leave4);
	}

	public void setLeave4(Double leave4) {
		this.leave4 = leave4;
	}

	public Double getLeave5() {
		return converToDouble(leave5);
	}

	public void setLeave5(Double leave5) {
		this.leave5 = leave5;
	}

	public Double getLeave6() {
		return converToDouble(leave6);
	}

	public void setLeave6(Double leave6) {
		this.leave6 = leave6;
	}

	public Double getLeave7() {
		return converToDouble(leave7);
	}

	public void setLeave7(Double leave7) {
		this.leave7 = leave7;
	}

	public Double getLeave8() {
		return converToDouble(leave8);
	}

	public void setLeave8(Double leave8) {
		this.leave8 = leave8;
	}

	public Double getLeave9() {
		return converToDouble(leave9);
	}

	public void setLeave9(Double leave9) {
		this.leave9 = leave9;
	}

	public Double getLeave10() {
		return converToDouble(leave10);
	}

	public void setLeave10(Double leave10) {
		this.leave10 = leave10;
	}

	public Double getLeave11() {
		return converToDouble(leave11);
	}

	public void setLeave11(Double leave11) {
		this.leave11 = leave11;
	}

	public Double getLeave12() {
		return converToDouble(leave12);
	}

	public void setLeave12(Double leave12) {
		this.leave12 = leave12;
	}

	public Double getLeave13() {
		return converToDouble(leave13);
	}

	public void setLeave13(Double leave13) {
		this.leave13 = leave13;
	}

	public Double getLeave14() {
		return converToDouble(leave14);
	}

	public void setLeave14(Double leave14) {
		this.leave14 = leave14;
	}

	public Double getLeave15() {
		return converToDouble(leave15);
	}

	public void setLeave15(Double leave15) {
		this.leave15 = leave15;
	}

	public Double getLeave16() {
		return converToDouble(leave16);
	}

	public void setLeave16(Double leave16) {
		this.leave16 = leave16;
	}

	public Double getLeave17() {
		return converToDouble(leave17);
	}

	public void setLeave17(Double leave17) {
		this.leave17 = leave17;
	}

	public Double getLeave18() {
		return converToDouble(leave18);
	}

	public void setLeave18(Double leave18) {
		this.leave18 = leave18;
	}

	public Double getLeave19() {
		return converToDouble(leave19);
	}

	public void setLeave19(Double leave19) {
		this.leave19 = leave19;
	}

	public Double getLeave20() {
		return converToDouble(leave20);
	}

	public void setLeave20(Double leave20) {
		this.leave20 = leave20;
	}

	public Double getLeave21() {
		return converToDouble(leave21);
	}

	public void setLeave21(Double leave21) {
		this.leave21 = leave21;
	}

	public Double getLeave22() {
		return converToDouble(leave22);
	}

	public void setLeave22(Double leave22) {
		this.leave22 = leave22;
	}

	public Double getLeave23() {
		return converToDouble(leave23);
	}

	public void setLeave23(Double leave23) {
		this.leave23 = leave23;
	}

	public Double getLeave24() {
		return converToDouble(leave24);
	}

	public void setLeave24(Double leave24) {
		this.leave24 = leave24;
	}

	public Double getLeave25() {
		return converToDouble(leave25);
	}

	public void setLeave25(Double leave25) {
		this.leave25 = leave25;
	}

	public Double getLeave26() {
		return converToDouble(leave26);
	}

	public void setLeave26(Double leave26) {
		this.leave26 = leave26;
	}

	public Double getLeave27() {
		return converToDouble(leave27);
	}

	public void setLeave27(Double leave27) {
		this.leave27 = leave27;
	}

	public Double getLeave28() {
		return converToDouble(leave28);
	}

	public void setLeave28(Double leave28) {
		this.leave28 = leave28;
	}

	public Double getLeave29() {
		return converToDouble(leave29);
	}

	public void setLeave29(Double leave29) {
		this.leave29 = leave29;
	}

	public Double getLeave30() {
		return converToDouble(leave30);
	}

	public void setLeave30(Double leave30) {
		this.leave30 = leave30;
	}

	public List<Integer> getYearList() {
		return yearList;
	}

	public void setYearList(List<Integer> yearList) {
		this.yearList = yearList;
	}

	public Double getLeave31() {
		return converToDouble(leave31);
	}

	public void setLeave31(Double leave31) {
		this.leave31 = leave31;
	}
	
	public Double converToDouble(Double num){
		return null == num ? 0.0 : num;
	}

	public Long getEmpapplicationleaveId() {
		return empapplicationleaveId;
	}

	public void setEmpapplicationleaveId(Long empapplicationleaveId) {
		this.empapplicationleaveId = empapplicationleaveId;
	}

	public Double getLeave32() {
		return leave32;
	}

	public void setLeave32(Double leave32) {
		this.leave32 = leave32;
	}

	public Double getLeave33() {
		return leave33;
	}

	public void setLeave33(Double leave33) {
		this.leave33 = leave33;
	}

	public Double getLeave34() {
		return leave34;
	}

	public void setLeave34(Double leave34) {
		this.leave34 = leave34;
	}

	public Double getLeave35() {
		return leave35;
	}

	public void setLeave35(Double leave35) {
		this.leave35 = leave35;
	}

	public Double getLeave36() {
		return leave36;
	}

	public void setLeave36(Double leave36) {
		this.leave36 = leave36;
	}

	public Double getLeave37() {
		return leave37;
	}

	public void setLeave37(Double leave37) {
		this.leave37 = leave37;
	}
	

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public Long getOriginalBillId() {
		return originalBillId;
	}

	public void setOriginalBillId(Long originalBillId) {
		this.originalBillId = originalBillId;
	}

	public List<Long> getEmpTypeIdList() {
		return empTypeIdList;
	}

	public void setEmpTypeIdList(List<Long> empTypeIdList) {
		this.empTypeIdList = empTypeIdList;
	}
	
	
}