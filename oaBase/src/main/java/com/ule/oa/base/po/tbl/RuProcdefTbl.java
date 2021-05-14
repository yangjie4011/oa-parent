package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: RuProcdefTbl
 * @Description: 流程待办表
 * @author mahaitao
 * @date 2017年5月26日 下午7:10:01
*/
@JsonInclude(Include.NON_NULL)
public class RuProcdefTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 2254688407727537747L;
	
	private Long id;
	//运行流程ID
	private Long ruTaskId;
	//流程类型:100-人事、200-考勤、300-行政、400-财务、500-法务
	private Long processType;
	//流程名称
	private String processName;
	//最后更新日期
	private Date lastUpdateDate;
	//流程状态:100-处理中、200-已完成、300-已撤回
	private Long processStatus;
	//开始时间
	private Date startTime;
	//结束时间
	private Date endTime;
	//持续时间
	private Long duration;
	//流程ID
	private String reProcdefCode;
	//节点模块
	private String nodeModule;
	//节点模块名称
	private String nodeModuleName;
	//节点模块类型:1-会签，2-普通
	private Long nodeModuleType;
	//节点类型 类型：1-会签，2-普通
	private Long nodeType;
	//节点名称
	private String nodeName;
	//节点CODE
	private String nodeCode;
	//受理人ID
	private Long assigneeId;
	//受理人
	private String assignee;
	//跳转URL
	private String redirectUrl;
	//实体ID
	private String entityId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRuTaskId() {
		return ruTaskId;
	}
	public void setRuTaskId(Long ruTaskId) {
		this.ruTaskId = ruTaskId;
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
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public Long getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(Long processStatus) {
		this.processStatus = processStatus;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
	public String getReProcdefCode() {
		return reProcdefCode;
	}
	public void setReProcdefCode(String reProcdefCode) {
		this.reProcdefCode = reProcdefCode;
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
	public Long getNodeType() {
		return nodeType;
	}
	public void setNodeType(Long nodeType) {
		this.nodeType = nodeType;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeCode() {
		return nodeCode;
	}
	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}
	public Long getAssigneeId() {
		return assigneeId;
	}
	public void setAssigneeId(Long assigneeId) {
		this.assigneeId = assigneeId;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
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
}
