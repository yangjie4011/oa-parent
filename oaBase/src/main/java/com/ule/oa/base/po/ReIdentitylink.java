package com.ule.oa.base.po;

import com.ule.oa.base.po.tbl.ReIdentitylinkTbl;

/**
 * @ClassName: HiActinstTbl
 * @Description: 流程节点人员配置表
 * @author mahaitao
 * @date 2017年6月5日 14:58
 */
public class ReIdentitylink extends ReIdentitylinkTbl {

	private static final long serialVersionUID = -5702505325604658205L;
	/** 类型：USER-用户、org-组织、pos-岗位、role-角色、group-用户分组 */
	/** 类型： USER-用户、DH-部门负责人、POSITION-角色、LEADER-主管、SELF-提交人**/
	public static final String TYPE_USER = "USER";
	public static final String TYPE_DH = "DH";
	public static final String TYPE_POSITION = "POSITION";
	public static final String TYPE_LEADER = "LEADER";
	public static final String TYPE_SELF = "SELF";
}
