package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpHandoverWorkTbl;

/**
 * @ClassName: 离职工作交接
 * @Description: 离职工作交接
 * @author yangjie
 * @date 2017年5月31日
 */
@JsonInclude(Include.NON_NULL)
public class EmpHandoverWork extends EmpHandoverWorkTbl{
	
	private static final long serialVersionUID = -2888334084266666269L;
	
	//是否有文件移交 : 0-是，1-否
	public static final int TRANSFER_YES = 0;//是
	public static final int TRANSFER_NO = 1;//否
	
	//是否完成工作交接:0-是、1-否
	public static final int HANDOVERCOMPLETED_YES = 0;//是
	public static final int HANDOVERCOMPLETED_NO = 1;//否
	
	//主管验收:100-未交接完成、200-已完成
	public static final int LEADERSTATUS_NO = 100;//未交接完成
	public static final int LEADERSTATUS_YES = 200;//已完成
	
	//审批状态:100-未交接完成、200-已完成
	public static final int DHSTATUS_NO = 100;//未交接完成
	public static final int DHSTATUS_YES = 200;//已完成
	
	
	//审批意见
    private String leaderView;

	public String getLeaderView() {
		return leaderView;
	}
	public void setLeaderView(String leaderView) {
		this.leaderView = leaderView;
	}

}
