package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 外出申请
 * @Description: 外出申请
 * @author yangjie
 * @date 2017年6月9日
 */
public interface EmpApplicationOutgoingMapper extends OaSqlMapper{
	
	int save(EmpApplicationOutgoing userOutgoing);
	
	int updateById(EmpApplicationOutgoing userOutgoing);
	
	EmpApplicationOutgoing getById(Long id);

	int getReportCount(EmpApplicationOutgoing userOutgoing);
	
	List<EmpApplicationOutgoing> getReportPageList(EmpApplicationOutgoing userOutgoing);

	/**
	 * 外出汇总统计，查询总数
	 * @param empApplicationOutgoing
	 * @return
	 */
	Integer getOutTotalCount(EmpApplicationOutgoing empApplicationOutgoing);

	/**
	 * 外出汇总统计查询列表
	 * @param empApplicationOutgoing
	 * @return
	 */
	List<EmpApplicationOutgoing> getOutTotalPageList(EmpApplicationOutgoing empApplicationOutgoing);
	
	/**
	 * 根据流程实例id查询外出申请单
	 * @param instanceId
	 * @return
	 */
	EmpApplicationOutgoing queryByProcessId(String instanceId);

	Integer getApprovePageListCount(EmpApplicationOutgoing empApplicationOutgoing);

	List<EmpApplicationOutgoing> getApprovePageList(EmpApplicationOutgoing empApplicationOutgoing);

	Integer getAuditedPageListCount(EmpApplicationOutgoing empApplicationOutgoing);

	List<EmpApplicationOutgoing> getAuditedPageList(EmpApplicationOutgoing empApplicationOutgoing);
}
