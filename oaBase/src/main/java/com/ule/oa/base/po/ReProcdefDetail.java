package com.ule.oa.base.po;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.ReProcdefDetailTbl;

/**
 * @ClassName: ReProcdefDetail
 * @Description: 业务流程定义数据明细表
 * @author mahaitao
 * @date 2017年5月27日 10:08
*/
@JsonInclude(Include.NON_NULL)
public class ReProcdefDetail extends ReProcdefDetailTbl{

	private static final long serialVersionUID = 7274185532671759147L;

	/** 流程启动节点 **/
	public static final String NODE_CODE_START = "START";
	/** 流程结束节点 **/
	public static final String NODE_CODE_END = "END";
	
	/**
	 * 节点CODE
	 */
	private List<String> nodeCodes;

	public List<String> getNodeCodes() {
		return nodeCodes;
	}

	public void setNodeCodes(List<String> nodeCodes) {
		this.nodeCodes = nodeCodes;
	}
	
}
