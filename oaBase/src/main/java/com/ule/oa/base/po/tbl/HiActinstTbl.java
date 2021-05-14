package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: HiActinstTbl
 * @Description: 流程运行进程表
 * @author mahaitao
 * @date 2017年5月27日 12:58
*/
public class HiActinstTbl extends CommonPo implements Serializable{
	private static final long serialVersionUID = -2496033261053320526L;
	private Long id;
	/** 流程ID */ 
	private String reProcdefCode;
	/** 运行流程ID */ 
	private Long ruTaskId ;
	/** 节点模块 */ 
	private String nodeModule;
	//节点模块名称
	private String nodeModuleName;
	/** 节点模块类型:1-会签，2-普通 */ 
	private Long nodeModuleType;
	/** 节点CODE */ 
	private String nodeCode;
	/** 节点名称 */ 
	private String nodeName;
	/** 节点类型 类型：1-会签，2-普通 */ 
	private Long nodeType;
	/** 受理人ID */ 
	private Long assigneeId;
	/** 受理人 */ 
	private String assignee;
	/** 开始时间 */ 
	private Date startTime ;
	/** 结束时间 */ 
	private Date endTime;
	/** 持续时间 */ 
	private Long duration;
	/** 审批意见 */ 
	private String opinion ;
	/** 是否处理:0-是、1否 */ 
	private Long isStart;
	/** 100-未完成、200-已完成、300-已回退 **/
	private Long status;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getReProcdefCode() {
		return reProcdefCode;
	}
	public void setReProcdefCode(String reProcdefCode) {
		this.reProcdefCode = reProcdefCode;
	}
	public Long getRuTaskId() {
		return ruTaskId;
	}
	public void setRuTaskId(Long ruTaskId) {
		this.ruTaskId = ruTaskId;
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
	public String getNodeCode() {
		return nodeCode;
	}
	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public Long getNodeType() {
		return nodeType;
	}
	public void setNodeType(Long nodeType) {
		this.nodeType = nodeType;
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
	public String getOpinion() {
		return opinion;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	public Long getIsStart() {
		return isStart;
	}
	public void setIsStart(Long isStart) {
		this.isStart = isStart;
	}
	public Long getStatus() {
		return status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}
}
