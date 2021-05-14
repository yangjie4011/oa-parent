package com.ule.oa.base.po.tbl;

import java.io.Serializable;

/**
 * @ClassName: HiActinstTbl
 * @Description: 流程节点人员配置表
 * @author mahaitao
 * @date 2017年6月5日 14:58
*/
public class ReIdentitylinkTbl implements Serializable {
	private static final long serialVersionUID = -8319884490821029947L;
	private Long id ;
	/** 类型：USER-用户、org-组织、pos-岗位、role-角色、group-用户分组 */
	private String type;
	/** 员工ID */
	private Long employeeId ;
	/** 其他类型ID */
	private Long groupId ;
	/** 业务流程ID */
	private String reProcdefCode;
	/** 节点CODE */
	private String nodeCode;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public String getReProcdefCode() {
		return reProcdefCode;
	}
	public void setReProcdefCode(String reProcdefCode) {
		this.reProcdefCode = reProcdefCode;
	}
	public String getNodeCode() {
		return nodeCode;
	}
	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}
}
