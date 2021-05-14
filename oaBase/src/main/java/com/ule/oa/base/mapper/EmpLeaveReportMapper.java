package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.EmpLeaveReport;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 员工请假报表
 * @Description: 员工请假报表
 * @author yangjie
 * @date 2017年10月24日
 */
public interface EmpLeaveReportMapper extends OaSqlMapper{
	
	int save(EmpLeaveReport empLeaveReport);
	
	int batchSave(List<EmpLeaveReport> list);
	
	List<EmpLeaveReport> getListByCondition(EmpLeaveReport empLeaveReport);
	
	int deleteByCondition(EmpLeaveReport empLeaveReport);

	void update(EmpLeaveReport empLeaveReport);

	void deleteAll();
	
	List<EmpLeaveReport> getList(EmpLeaveReport empLeaveReport);

}
