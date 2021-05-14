package com.ule.oa.base.po.tbl;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: ReProcdefTbl
 * @Description: 流程待办表
 * @author mahaitao
 * @date 2017年5月27日 10:08
*/
@JsonInclude(Include.NON_NULL)
public class ReProcdefTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 2254688407727537747L;
	
	private Long id;
	/** 流程编号 **/
	private String code;
	/**流程类型:100-人事、200-考勤、300-行政、400-财务、500-法务*/
	private Long processType;
	/**流程名称*/
	private String processName;
	/**流程说明*/
	private String processDescription;
	/**使用范围*/
	private Long rangeOfUse;
	/**审批类型:100-一级审批、200-逐级审批*/
	private Long approvalType;
	/**授权需审批:0：否，1：是*/
	private Long authorization;
	/**流程状态:0：停用，1：启用*/
	private Long processStatus;
	/**跳转URL*/
	private String redirectUrl;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getProcessType() {
		return processType;
	}
	public void setProcessType(Long processType) {
		this.processType = processType;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getProcessDescription() {
		return processDescription;
	}
	public void setProcessDescription(String processDescription) {
		this.processDescription = processDescription;
	}
	public Long getRangeOfUse() {
		return rangeOfUse;
	}
	public void setRangeOfUse(Long rangeOfUse) {
		this.rangeOfUse = rangeOfUse;
	}
	public Long getApprovalType() {
		return approvalType;
	}
	public void setApprovalType(Long approvalType) {
		this.approvalType = approvalType;
	}
	public Long getAuthorization() {
		return authorization;
	}
	public void setAuthorization(Long authorization) {
		this.authorization = authorization;
	}
	public Long getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(Long processStatus) {
		this.processStatus = processStatus;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
