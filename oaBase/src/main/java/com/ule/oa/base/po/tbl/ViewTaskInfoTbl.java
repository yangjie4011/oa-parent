package com.ule.oa.base.po.tbl;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
/**
 * @ClassName: act_task_info
 * @Description: 流程任务信息表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
import com.fasterxml.jackson.annotation.JsonFormat;

@Table(name="act_task_info")
public class ViewTaskInfoTbl {
	@Id
	private Long id;
	private String processId;//流程实例id
	private String processKey;//流程定义key
	private String taskId;//任务id
	private Date finishTime;//节点完成时间
	private String assigneeName;//办理人名称
	private String positionName;//职位名称
	private String comment;//节点审批意见
	private String departName;//部门名称
	private Integer statu;//流程状态
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	public Date getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
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
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getProcessKey() {
		return processKey;
	}
	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}
	public Integer getStatu() {
		return statu;
	}
	public void setStatu(Integer statu) {
		this.statu = statu;
	}
	
}
