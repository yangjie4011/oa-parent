package com.ule.oa.base.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TaskVO {
	private String processId;//流程实例id
	private String processName;//流程名称
	private String processKey;//流程key
	private Integer processStatu;
	
	private String actName;//节点名称
	private String creatorDepart;//申请人部门
	private String creator;//申请人
	private Date createTime;//创建时间
	private String operation;//操作：已拒绝,已完成
	
	private String reProcdefCode;//流程code
	private String resourceId;//源id
	private String redirectUrl;//源url
	
	
	//显示所需参数
	private String view3;
	private String view4;
	private String view5;
	private String view6;
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	public String getCreatorDepart() {
		return creatorDepart;
	}
	public void setCreatorDepart(String creatorDepart) {
		this.creatorDepart = creatorDepart;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getView3() {
		return view3;
	}
	public void setView3(String view3) {
		this.view3 = view3;
	}
	public String getView4() {
		return view4;
	}
	public void setView4(String view4) {
		this.view4 = view4;
	}
	public String getView5() {
		return view5;
	}
	public void setView5(String view5) {
		this.view5 = view5;
	}
	
	
	public Integer getProcessStatu() {
		return processStatu;
	}
	public void setProcessStatu(Integer processStatu) {
		this.processStatu = processStatu;
	}
	
	public String getProcessKey() {
		return processKey;
	}
	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getReProcdefCode() {
		return reProcdefCode;
	}
	public void setReProcdefCode(String reProcdefCode) {
		this.reProcdefCode = reProcdefCode;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getView6() {
		return view6;
	}
	public void setView6(String view6) {
		this.view6 = view6;
	}
	
	
}
