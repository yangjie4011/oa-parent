package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpResignTbl;

/**
 * @ClassName: 员工离职申请
 * @Description: 员工离职申请
 * @author yangjie
 * @date 2017年5月25日
 */

@JsonInclude(Include.NON_NULL)
public class EmpResign extends EmpResignTbl{
	
	private static final long serialVersionUID = 1220663713181580237L;
	
	public static final int TURNOVER_STATUS_SUBMIT = 100;//提交
	public static final int TURNOVER_STATUS_LEADER = 200;//部门领导意见
	public static final int TURNOVER_STATUS_HR = 300;//人事意见
	public static final int TURNOVER_STATUS_BACK = 400;//驳回
	public static final int TURNOVER_STATUS_CANCEL = 500;//撤销
	

}
