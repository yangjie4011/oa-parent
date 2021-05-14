package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 我的消息
 * @Description: 我的消息
 * @author yangjie
 * @date 2017年6月8日
 */
public interface EmpMsgMapper extends OaSqlMapper{
	
	int save(EmpMsg userMsg);
		
	int updateById(EmpMsg userMsg);
	
	List<EmpMsg> getListByCondition(EmpMsg userMsg);
	
	List<EmpMsg> getPageList(EmpMsg userMsg);
	
	List<EmpMsg> getList(EmpMsg userMsg);
	
	Integer getCount(EmpMsg userMsg);
	
	/**
	 * 批量保存
	 * @param empMsgs
	 */
	Integer batchSave(List<EmpMsg> empMsgs);

	/**
	 * 删除节点已执行流程消息
	 * @param userMsg
	 * @return
	 */
	Integer delRuTaskIdORNodeCode(EmpMsg userMsg);
}
