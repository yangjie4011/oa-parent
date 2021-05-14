package com.ule.oa.base.po;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.tbl.RunTaskTbl;


/**
 * @ClassName: RunTask
 * @Description: 流程运行进程表
 * @author mahaitao
 * @date 2017年5月27日 12:58
*/
public class RunTask extends RunTaskTbl{

	private static final long serialVersionUID = 6235452865723812541L;

	
	//流程类型:100-人事、200-考勤、300-行政、400-财务、500-法务
	public static final Long PROCESS_TYPE_100 = 100L;
	public static final Long PROCESS_TYPE_200 = 200L;
	public static final Long PROCESS_TYPE_300 = 300L;
	public static final Long PROCESS_TYPE_400 = 400L;
	public static final Long PROCESS_TYPE_500 = 500L;
	
	//流程状态:100-处理中、200-已完成、300-审批拒绝、400-撤消
	public static final Long PROCESS_STATUS_100 = 100L;
	public static final Long PROCESS_STATUS_200 = 200L;
	public static final Long PROCESS_STATUS_300 = 300L;
	public static final Long PROCESS_STATUS_400 = 400L;
	
	//10:入职登记申请、20:离职申请、30:延时工作申请、40:异常考勤、41:(产品管理部)异常考勤、50:排班、60-请假申请、70-外出申请、80-出差申请（普通员工）、
	//81-出差申请（部门负责人）、90-总结报告（普通员工）、91-总结报告（部门负责人）、100-延迟工作晚到,110-值班,120-下属考勤消异常,130-销假申请
	//140-消下属缺勤
	public static final String RUN_CODE_10 = "10";
	public static final String RUN_CODE_20 = "20";
	public static final String RUN_CODE_30 = "30";
	public static final String RUN_CODE_40 = "40";
	public static final String RUN_CODE_41 = "41";
	public static final String RUN_CODE_60 = "60";
	public static final String RUN_CODE_70 = "70";
	public static final String RUN_CODE_80 = "80";
	public static final String RUN_CODE_81 = "81";
	public static final String RUN_CODE_90 = "90";
	public static final String RUN_CODE_91 = "91";
	public static final String RUN_CODE_100 = "100";
	public static final String RUN_CODE_50 = "50";
	public static final String RUN_CODE_110 = "110";
	public static final String RUN_CODE_120 = "120";
	public static final String RUN_CODE_130 = "130";
	public static final String RUN_CODE_140 = "140";
	
	private String assigneeId; //允许流程id
	private String creatorDepart; //创建人部门

	private String cnName;//创建人名称
	private Long departId;//部门ID
	private String createTimeStr;
	private String createTimeStr1;
	private List<Long> processStatuss;
	//下属名称
	private String subordinateName;
	
	public String getAssigneeId() {
		return assigneeId;
	}

	public void setAssigneeId(String assigneeId) {
		this.assigneeId = assigneeId;
	}

	public String getCreatorDepart() {
		return creatorDepart;
	}

	public void setCreatorDepart(String creatorDepart) {
		this.creatorDepart = creatorDepart;
	}
	
	public List<Long> getProcessStatuss() {
		return processStatuss;
	}

	public void setProcessStatuss(List<Long> processStatuss) {
		this.processStatuss = processStatuss;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getCreateTimeStr1() {
		return createTimeStr1;
	}

	public void setCreateTimeStr1(String createTimeStr1) {
		this.createTimeStr1 = createTimeStr1;
	}

	public String getSubordinateName() {
		return subordinateName;
	}

	public void setSubordinateName(String subordinateName) {
		this.subordinateName = subordinateName;
	}


//	/**
//	 * 流程类型
//	 */
//	@SuppressWarnings("serial")
//	public static final Map<Long,String> PROCESS_TYPE_MAP = new HashMap<Long,String>(){
//		{
//			put(RunTask.PROCESS_TYPE_100, "人事");
//			put(RunTask.PROCESS_TYPE_200, "考勤");
//			put(RunTask.PROCESS_TYPE_300, "行政");
//			put(RunTask.PROCESS_TYPE_400, "财务");
//			put(RunTask.PROCESS_TYPE_500, "法务");
//		}
//	};
	
}
