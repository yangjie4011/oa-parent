package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 离职办理清单
 * @Description: 离职办理清单
 * @author yangjie
 * @date 2017年6月6日
 */
public class HrEmpCheckListTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = -6475915441287065015L;
	
	private Long id;
	//离职申请id
	private Long empResignId;
	//部门名称
	private String departName;
	//检查项目
	private String checkItem;
	//交接情况:100-已完成、200-无此项目
	private Integer checkStatus;
	//核准人
	private String approver;
	//核准人ID
	private Long approverId;
	//核准时间
	private Date approverDate;
	//备注
	private String remark;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEmpResignId() {
		return empResignId;
	}
	public void setEmpResignId(Long empResignId) {
		this.empResignId = empResignId;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getCheckItem() {
		return checkItem;
	}
	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}
	public Integer getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(Integer checkStatus) {
		this.checkStatus = checkStatus;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover(String approver) {
		this.approver = approver;
	}
	public Long getApproverId() {
		return approverId;
	}
	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}
	public Date getApproverDate() {
		return approverDate;
	}
	public void setApproverDate(Date approverDate) {
		this.approverDate = approverDate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

}
