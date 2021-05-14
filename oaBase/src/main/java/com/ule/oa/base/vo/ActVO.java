package com.ule.oa.base.vo;

import java.util.Date;

/**
 * 流程节点VO对象
 * @author wangwencan
 *
 */
public class ActVO {
	private String departName;//部门名称
	private String assigneeName;//办理人名称
	private String positionName;//办理人职位名称
	private String comment;//审批意见
	private String type;//节点审批状态
	private Date finishTime;
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getAssigneeName() {
		return assigneeName;
	}
	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public Date getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	
	
}
