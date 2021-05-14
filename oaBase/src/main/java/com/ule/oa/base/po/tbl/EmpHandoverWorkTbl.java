package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 离职工作交接
 * @Description: 离职工作交接
 * @author yangjie
 * @date 2017年5月31日
 */

public class EmpHandoverWorkTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = -350121392678446715L;
	
	private Long id;
	//辞职申请ID
	private Long empResignId;
	//序号
	private Integer number;
	//工作交接内容
	private String handoverContent;
	//是否有文件移交 : 0-是，1-否
	private Integer isTransfer;
	//备注
	private String remark;
	//交接人
	private String receiver;
	//交接人ID
	private Long receiverId;
	//是否完成工作交接:0-是、1-否
	private Integer isHandoverCompleted;
	//完成交接日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date completeHandoverDate;
	//主管验收:100-未交接完成、200-已完成
	private Integer leaderStatus;
	//主管ID
	private Long leaderId;
	//汇报对象ID
	private Long dhId;
	//审批状态
	private Integer dhStatus;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEmpResignId() {
		return empResignId;
	}
	public void setEmpResignId(Long empResignId) {
		this.empResignId = empResignId;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getHandoverContent() {
		return handoverContent;
	}
	public void setHandoverContent(String handoverContent) {
		this.handoverContent = handoverContent;
	}
	public Integer getIsTransfer() {
		return isTransfer;
	}
	public void setIsTransfer(Integer isTransfer) {
		this.isTransfer = isTransfer;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public Long getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}
	public Integer getIsHandoverCompleted() {
		return isHandoverCompleted;
	}
	public void setIsHandoverCompleted(Integer isHandoverCompleted) {
		this.isHandoverCompleted = isHandoverCompleted;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getCompleteHandoverDate() {
		return completeHandoverDate;
	}
	public void setCompleteHandoverDate(Date completeHandoverDate) {
		this.completeHandoverDate = completeHandoverDate;
	}
	public Integer getLeaderStatus() {
		return leaderStatus;
	}
	public void setLeaderStatus(Integer leaderStatus) {
		this.leaderStatus = leaderStatus;
	}
	public Long getLeaderId() {
		return leaderId;
	}
	public void setLeaderId(Long leaderId) {
		this.leaderId = leaderId;
	}
	public Long getDhId() {
		return dhId;
	}
	public void setDhId(Long dhId) {
		this.dhId = dhId;
	}
	public Integer getDhStatus() {
		return dhStatus;
	}
	public void setDhStatus(Integer dhStatus) {
		this.dhStatus = dhStatus;
	}

}
