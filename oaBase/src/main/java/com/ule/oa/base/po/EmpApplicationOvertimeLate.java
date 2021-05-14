package com.ule.oa.base.po;

import com.ule.oa.base.po.tbl.EmpApplicationOvertimeLateTbl;

/**
 * @ClassName: 晚到申请
 * @Description: 晚到申请
 * @author yangjie
 * @date 2017年6月19日
 */
public class EmpApplicationOvertimeLate extends EmpApplicationOvertimeLateTbl {
	
	private static final long serialVersionUID = -5033983347049198665L;
	
	public static final int APPROVAL_STATUS_WAIT = 100;//待审批
	public static final int APPROVAL_STATUS_YES = 200;//已审批
	public static final int APPROVAL_STATUS_NO = 300;//已拒
	
	private String nodeCode;//节点CODE
	
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

}
