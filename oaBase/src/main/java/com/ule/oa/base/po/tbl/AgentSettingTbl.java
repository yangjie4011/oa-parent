package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: AgentSettingTbl
 * @Description: 流程代理设定表
 * @author wufei
 * @date 2017年6月14日 12:58
*/
public class AgentSettingTbl extends CommonPo implements Serializable{
	private static final long serialVersionUID = -2496033261053320526L;
	
	private Long id;
	private Long authId;//授权人ID 
	private String authName;//授权人 
	private Long authType;//授权类型:0-永久、1-指定时间 
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startDate;//开始时间  
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endDate;//结束时间  
	private Long agentId;//代理人ID 
	private String agent;//代理人
	private String processId;//流程ID 
	private String processName;//流程名称 
	private Long processType;//流程类型:100-人事、200-考勤、300-行政、400-财务、500-法务
	private Long authStatus;//授权状态:100-未开始、200-代理中、300-已结束、400-已取消
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAuthId() {
		return authId;
	}
	public void setAuthId(Long authId) {
		this.authId = authId;
	}
	public String getAuthName() {
		return authName;
	}
	public void setAuthName(String authName) {
		this.authName = authName;
	}
	public Long getAuthType() {
		return authType;
	}
	public void setAuthType(Long authType) {
		this.authType = authType;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public Long getProcessType() {
		return processType;
	}
	public void setProcessType(Long processType) {
		this.processType = processType;
	}
	public Long getAuthStatus() {
		return authStatus;
	}
	public void setAuthStatus(Long authStatus) {
		this.authStatus = authStatus;
	}
	
}
