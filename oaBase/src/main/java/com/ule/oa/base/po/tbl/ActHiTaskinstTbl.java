package com.ule.oa.base.po.tbl;

import java.io.Serializable;

//流程历史记录表，flowable自带的
public class ActHiTaskinstTbl implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	//流程节点key
	private String taskDefKey;
	//流程实例唯一标识
	private String procInstId;
	//流程节点id
	private String executionId;
	//流程节点名称
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTaskDefKey() {
		return taskDefKey;
	}
	public void setTaskDefKey(String taskDefKey) {
		this.taskDefKey = taskDefKey;
	}
	public String getProcInstId() {
		return procInstId;
	}
	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}
	public String getExecutionId() {
		return executionId;
	}
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}
