package com.ule.oa.base.po.tbl;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: ReProcdefDetailTbl
 * @Description: 业务流程定义数据明细表
 * @author mahaitao
 * @date 2017年5月27日 10:08
*/
@JsonInclude(Include.NON_NULL)
public class ReProcdefDetailTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 2254688407727537747L;
	
	private Long id;
	/** 业务流程ID */ 
	private String reProcdefCode;
	/** 节点模块 */ 
	private String nodeModule;
	//节点模块名称
	private String nodeModuleName;
	/** 节点模块类型:1-会签，2-普通 */ 
	private Long nodeModuleType;
	/** 节点CODE */ 
	private String nodeCode;
	/** 节点名称 */ 
	private String nodeName;
	/** 节点类型 类型：1-会签，2-普通 */ 
	private Long nodeType;
	/** 跳转URL */ 
	private String redirectUrl;
	/** 后续节点CODE */ 
	private String nextNodeCode;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getReProcdefCode() {
		return reProcdefCode;
	}
	public void setReProcdefCode(String reProcdefCode) {
		this.reProcdefCode = reProcdefCode;
	}
	public String getNodeModule() {
		return nodeModule;
	}
	public void setNodeModule(String nodeModule) {
		this.nodeModule = nodeModule;
	}
	public String getNodeModuleName() {
		return nodeModuleName;
	}
	public void setNodeModuleName(String nodeModuleName) {
		this.nodeModuleName = nodeModuleName;
	}
	public Long getNodeModuleType() {
		return nodeModuleType;
	}
	public void setNodeModuleType(Long nodeModuleType) {
		this.nodeModuleType = nodeModuleType;
	}
	public String getNodeCode() {
		return nodeCode;
	}
	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public Long getNodeType() {
		return nodeType;
	}
	public void setNodeType(Long nodeType) {
		this.nodeType = nodeType;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public String getNextNodeCode() {
		return nextNodeCode;
	}
	public void setNextNodeCode(String nextNodeCode) {
		this.nextNodeCode = nextNodeCode;
	}
}
