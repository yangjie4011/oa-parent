package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.RuProcdefTbl;

/**
 * @ClassName: RuProcdef
 * @Description: 流程待办表
 * @author mahaitao
 * @date 2017年5月26日 下午7:10:01
*/
@JsonInclude(Include.NON_NULL)
public class RuProcdef extends RuProcdefTbl{

	private static final long serialVersionUID = 1633055328074207351L;
	
	
	//流程状态:100-处理中、200-已完成、300-已撤回
	public static final Long PROCESS_STATUS_100 = 100L;
	public static final Long PROCESS_STATUS_200 = 200L;
	public static final Long PROCESS_STATUS_300 = 300L;
	//节点类型 类型：1-会签，2-普通,3-单独任务执行人需要传递,4-单独任务,5手动分配配送员
	public static final Long NODE_TYPE_H = 1L;
	public static final Long NODE_TYPE_P = 2L;
	public static final Long NODE_TYPE_DE = 3L;
	public static final Long NODE_TYPE_D = 4L;
	public static final Long NODE_TYPE_S = 5L;
	//节点模块类型:1-会签，2-普通
	public static final Long NODE_MODULE_TYPE_H = 1L;
	public static final Long NODE_MODULE_TYPE_P = 2L;
	
	private String taskSubject; //单据名称
	private String creator; //创建人姓名
	private String creatorDepart; //创建人部门
	private Date ltCreateTime;
	private Long creatorId;
	private List<String> reProcdefCodes;
	
	private String view3;
	private String view4;
	private String view5;
	private String view6;
	//下属名称
	private String subordinateName;
	
	public String getTaskSubject() {
		return taskSubject;
	}
	public void setTaskSubject(String taskSubject) {
		this.taskSubject = taskSubject;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreatorDepart() {
		return creatorDepart;
	}
	public void setCreatorDepart(String creatorDepart) {
		this.creatorDepart = creatorDepart;
	}
	public Date getLtCreateTime() {
		return ltCreateTime;
	}
	public void setLtCreateTime(Date ltCreateTime) {
		this.ltCreateTime = ltCreateTime;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public List<String> getReProcdefCodes() {
		return reProcdefCodes;
	}
	public void setReProcdefCodes(List<String> reProcdefCodes) {
		this.reProcdefCodes = reProcdefCodes;
	}
	public String getView3() {
		return view3;
	}
	public void setView3(String view3) {
		this.view3 = view3;
	}
	public String getView4() {
		return view4;
	}
	public void setView4(String view4) {
		this.view4 = view4;
	}
	public String getView5() {
		return view5;
	}
	public void setView5(String view5) {
		this.view5 = view5;
	}
	public String getView6() {
		return view6;
	}
	public void setView6(String view6) {
		this.view6 = view6;
	}
	public String getSubordinateName() {
		return subordinateName;
	}
	public void setSubordinateName(String subordinateName) {
		this.subordinateName = subordinateName;
	}
	
}
