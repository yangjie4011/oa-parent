package com.ule.oa.base.po;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.DepartTbl;

/**
  * @ClassName: Depart
  * @Description: 部门表
  * @author minsheng
  * @date 2017年5月8日 下午1:39:42
 */
@JsonInclude(Include.NON_NULL)
public class Depart extends DepartTbl {

	private static final long serialVersionUID = -860920710401850723L;
	
	/** 部门 */
	public static final Integer TYPE_DEPART_VIRTUAL = 0; //虚拟部门
	public static final Integer TYPE_DEPART_ONE = 1; //一级部门
	public static final Integer TYPE_DEPART_TWO = 2; //二级部门
	
	private String positionName;//职位
	
	private String empName;//员工名称
	private String empId; //员工id
	
	private String parentName; //上一级机构名称
	
	private Integer empCount;//员工数量
	
	private String leaderName;//部门负责人名称
	private String leaderId;//部门负责人Id
	private String leaderDeptName;//部门负责人部门
	private Long leaderDeptId;//部门负责人部门Id
	private String managerName;//leader
	private String managerId;//leaderId
	private String powerName;//行使权力人名称
	private Integer beforeLeaderId;//之前的部门负责人id
	
	private List<Depart> children; //组织机构图使用
	private String title; //组织机构图使用
	private String flag; //普通部门显示员工信息,部门汇报关系图使用
	
	public static final Integer DEL_FLAG_DELETE = 1; //无效
	public static final Integer DEL_FLAG_NORMAL = 0; //有效
	
	
	
	public Integer getBeforeLeaderId() {
		return beforeLeaderId;
	}

	public void setBeforeLeaderId(Integer beforeLeaderId) {
		this.beforeLeaderId = beforeLeaderId;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}

	public Integer getEmpCount() {
		return empCount;
	}

	public void setEmpCount(Integer empCount) {
		this.empCount = empCount;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getPowerName() {
		return powerName;
	}

	public void setPowerName(String powerName) {
		this.powerName = powerName;
	}

	public List<Depart> getChildren() {
		return children;
	}

	public void setChildren(List<Depart> children) {
		this.children = children;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getLeaderId() {
		return leaderId;
	}

	public void setLeaderId(String leaderId) {
		this.leaderId = leaderId;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public String getLeaderDeptName() {
		return leaderDeptName;
	}

	public void setLeaderDeptName(String leaderDeptName) {
		this.leaderDeptName = leaderDeptName;
	}

	public Long getLeaderDeptId() {
		return leaderDeptId;
	}

	public void setLeaderDeptId(Long leaderDeptId) {
		this.leaderDeptId = leaderDeptId;
	}
}
