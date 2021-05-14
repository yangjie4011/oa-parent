package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 请假申请明细
 * @Description: 请假申请明细
 * @author yangjie
 * @date 2017年6月14日
 */
public class EmpApplicationLeaveDetailTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = -3981384335207969773L;
	
	private Long id;
	//请假申请表ID
	private Long leaveId;
	//请假类型
	private Integer leaveType;
	//请假开始时间
	private Date startTime;
	//请假结束时间
	private Date endTime;
	//请假天数
	private Double leaveDays;
	//请假小时数
	private Double leaveHours;
	//子女数
	private Integer childrenNum;
	//生产情况100-顺产，200-难产
	private Integer birthType;
	//哺乳假请假时间100-上午，200-下午
	private Integer dayType;
	//亲属关系 100-父母，200-配偶，300-子女，400-祖父母，500-外祖父母，600-兄弟姐妹，700-配偶父母
	private Integer relatives;
	//预产期
	private Date expectedDate;
	//年假扣减详情
	private String cutLeaveDetail;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getLeaveId() {
		return leaveId;
	}
	public void setLeaveId(Long leaveId) {
		this.leaveId = leaveId;
	}
	public Integer getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(Integer leaveType) {
		this.leaveType = leaveType;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Double getLeaveDays() {
		return leaveDays;
	}
	public void setLeaveDays(Double leaveDays) {
		this.leaveDays = leaveDays;
	}
	public Double getLeaveHours() {
		return leaveHours;
	}
	public void setLeaveHours(Double leaveHours) {
		this.leaveHours = leaveHours;
	}
	public Integer getChildrenNum() {
		return childrenNum;
	}
	public void setChildrenNum(Integer childrenNum) {
		this.childrenNum = childrenNum;
	}
	public Integer getBirthType() {
		return birthType;
	}
	public void setBirthType(Integer birthType) {
		this.birthType = birthType;
	}
	public Integer getDayType() {
		return dayType;
	}
	public void setDayType(Integer dayType) {
		this.dayType = dayType;
	}
	public Integer getRelatives() {
		return relatives;
	}
	public void setRelatives(Integer relatives) {
		this.relatives = relatives;
	}
	public Date getExpectedDate() {
		return expectedDate;
	}
	public void setExpectedDate(Date expectedDate) {
		this.expectedDate = expectedDate;
	}
	public String getCutLeaveDetail() {
		return cutLeaveDetail;
	}
	public void setCutLeaveDetail(String cutLeaveDetail) {
		this.cutLeaveDetail = cutLeaveDetail;
	}
}
