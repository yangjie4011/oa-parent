package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;
/**
 * @ClassName: Bpm_TimIng_Run_Task
 * @Description: 定时流程启动表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class TimingRunTaskTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 5243505603444104556L;
	private Long id;
	private String reProcdefCode;//流程CODE
	private Long processStatus;//流程状态:0-未开始、1-已开始
	private Long creatorId;//创建人ID
	private Date startTime;//开始时间
	private String entityId;//实体ID
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
	public Long getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(Long processStatus) {
		this.processStatus = processStatus;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
}
