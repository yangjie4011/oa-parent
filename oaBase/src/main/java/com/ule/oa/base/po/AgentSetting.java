package com.ule.oa.base.po;

import java.util.List;

import com.ule.oa.base.po.tbl.AgentSettingTbl;


/**
 * @ClassName: AgentSetting
 * @Description: 流程代理设定表
 * @author wufei
 * @date 2017年6月14日 12:58
*/
public class AgentSetting extends AgentSettingTbl{

	private static final long serialVersionUID = 6235452865723812541L;
	
	//流程类型:100-人事、200-考勤、300-行政、400-财务、500-法务
	public static final Long PROCESS_TYPE_100 = 100L;
	public static final Long PROCESS_TYPE_200 = 200L;
	public static final Long PROCESS_TYPE_300 = 300L;
	public static final Long PROCESS_TYPE_400 = 400L;
	public static final Long PROCESS_TYPE_500 = 500L;

	//授权状态:100-未开始、200-代理中、300-已结束、400-已取消
	public static final Long AUTH_STATUS_100 = 100L;
	public static final Long AUTH_STATUS_200 = 200L;
	public static final Long AUTH_STATUS_300 = 300L;
	public static final Long AUTH_STATUS_400 = 400L;
	
	//授权类型:0-永久、1-指定时间 
	public static final Long AUTH_TYPE_0 = 0L;
	public static final Long AUTH_TYPE_1 = 1L;
	
	private List<Long> ids;
	
	private String startDateStart;
	private String startDateEnd;
	
	private String endDateStart;
	private String endDateEnd;
	
	private String agents;
	private String agentIds;

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public String getStartDateStart() {
		return startDateStart;
	}

	public void setStartDateStart(String startDateStart) {
		this.startDateStart = startDateStart;
	}

	public String getStartDateEnd() {
		return startDateEnd;
	}

	public void setStartDateEnd(String startDateEnd) {
		this.startDateEnd = startDateEnd;
	}

	public String getEndDateStart() {
		return endDateStart;
	}

	public void setEndDateStart(String endDateStart) {
		this.endDateStart = endDateStart;
	}

	public String getEndDateEnd() {
		return endDateEnd;
	}

	public void setEndDateEnd(String endDateEnd) {
		this.endDateEnd = endDateEnd;
	}

	public String getAgents() {
		return agents;
	}

	public void setAgents(String agents) {
		this.agents = agents;
	}

	public String getAgentIds() {
		return agentIds;
	}

	public void setAgentIds(String agentIds) {
		this.agentIds = agentIds;
	}
	
}
