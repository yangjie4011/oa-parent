package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 我的消息
 * @Description: 我的消息
 * @author yangjie
 * @date 2017年6月8日
 */
public class EmpMsgTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = -8328486957385100764L;
	
	private Long id;
	//公司ID
	private Long companyId;
	//员工
	private Long employeeId;
	//类型
	private Long type;
	//运行流程ID
	private Long ruTaskId;
	//节点CODE
	private String nodeCode;
	//消息标题
	private String title;
	//消息内容
	private String content;
	//阅读标记: 1-未读 2-已读
	private Integer readFlag;
	//阅读时间
	private Date readDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getType() {
		return type;
	}
	public void setType(Long type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getReadFlag() {
		return readFlag;
	}
	public void setReadFlag(Integer readFlag) {
		this.readFlag = readFlag;
	}
	public Date getReadDate() {
		return readDate;
	}
	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}
	public Long getRuTaskId() {
		return ruTaskId;
	}
	public void setRuTaskId(Long ruTaskId) {
		this.ruTaskId = ruTaskId;
	}
	public String getNodeCode() {
		return nodeCode;
	}
	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}
}
