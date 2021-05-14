package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpApplicationRegisterTbl;


/**
 * @ClassName: EmployeeRegister
 * @Description: 员工入职登记表
 * @author yangjie
 * @date 2017年5月22日
*/
@JsonInclude(Include.NON_NULL)
public class EmpApplicationRegister extends EmpApplicationRegisterTbl{

	private static final long serialVersionUID = -7089220633561761939L;
	
	public static final int ENTRY_STATUS_NO = 1;//未入职
	public static final int ENTRY_STATUS_ING = 2;//处理中
	public static final int ENTRY_STATUS_YES = 3;//已入职
	
	public static final int PROCESS_STATUS_ING = 1;//处理中
	public static final int PROCESS_STATUS_YES = 2;//已完成
	
    private String nodeCode;//节点CODE
    
    private String taskId;
	
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
	
	private String companyName;//公司名称
	private String positionName;//职位名称
	private String employeeTypeCName;//员工类型中文名
	private String departName1;//新员工部门名称
	private String leaderName;//汇报对象
	private String departHeader;//部门负责人
	private String seatNo;//座位号
	private String floorNum;//楼层编号
	private String workTypeName;//工时类型
	private String whetherSchedulingName;//是否排班
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	public String getEmployeeTypeCName() {
		return employeeTypeCName;
	}
	public void setEmployeeTypeCName(String employeeTypeCName) {
		this.employeeTypeCName = employeeTypeCName;
	}
	
	public String getDepartName1() {
		return departName1;
	}

	public void setDepartName1(String departName1) {
		this.departName1 = departName1;
	}

	public String getLeaderName() {
		return leaderName;
	}
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}
	public String getDepartHeader() {
		return departHeader;
	}
	public void setDepartHeader(String departHeader) {
		this.departHeader = departHeader;
	}
	public String getSeatNo() {
		return seatNo;
	}
	public void setSeatNo(String seatNo) {
		this.seatNo = seatNo;
	}
	public String getFloorNum() {
		return floorNum;
	}
	public void setFloorNum(String floorNum) {
		this.floorNum = floorNum;
	}

	public String getWorkTypeName() {
		return workTypeName;
	}

	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}

	public String getWhetherSchedulingName() {
		return whetherSchedulingName;
	}

	public void setWhetherSchedulingName(String whetherSchedulingName) {
		this.whetherSchedulingName = whetherSchedulingName;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	
	
}
