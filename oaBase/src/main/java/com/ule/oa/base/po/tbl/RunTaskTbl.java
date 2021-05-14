package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: HiActinstTbl
 * @Description: 流程运行进程表
 * @author mahaitao
 * @date 2017年5月27日 12:58
*/
public class RunTaskTbl extends CommonPo implements Serializable{
	private static final long serialVersionUID = -2496033261053320526L;
	private Long id;
	private Long processType;//流程类型:100-人事、200-考勤、300-行政、400-财务、500-法务 
	private String reProcdefCode;//流程Code  
	private String processName;//流程名称  
	private Long processStatus;//流程状态:100-处理中、200-已完成、300-已撤回
	private String subject;//流程标题  
	private Long creatorId;//创建人ID  
	private String creator;//创建人姓名  
	private Date startTime;//开始时间  
	private Date endTime;//结束时间  
	private Long duration;//持续时间  
	private String nodeModule;//节点模块  
	//节点模块名称
	private String nodeModuleName;
	private Long nodeModuleType;//节点模块类型:1-会签，2-普通  
	private String redirectUrl;//跳转URL
	private String entityId;//实体ID
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
	public String getReProcdefCode() {
		return reProcdefCode;
	}
	public void setReProcdefCode(String reProcdefCode) {
		this.reProcdefCode = reProcdefCode;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
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
	public Long getDuration() {
		return duration;
	}
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	public String getNodeModule() {
		return nodeModule;
	}
	public void setNodeModule(String nodeModule) {
		this.nodeModule = nodeModule;
	}
	public String getNodeModuleName() {
		return nodeModuleName;
	}
	public void setNodeModuleName(String nodeModuleName) {
		this.nodeModuleName = nodeModuleName;
	}
	public Long getNodeModuleType() {
		return nodeModuleType;
	}
	public void setNodeModuleType(Long nodeModuleType) {
		this.nodeModuleType = nodeModuleType;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public Long getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(Long processStatus) {
		this.processStatus = processStatus;
	}
}
