package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 延时工作申请
 * @Description: 延时工作申请
 * @author yangjie
 * @date 2017年6月12日
 */
public interface EmpApplicationOvertimeMapper extends OaSqlMapper{
	
    int save(EmpApplicationOvertime userOvertime);
	
	int updateById(EmpApplicationOvertime userOvertime);
	
	EmpApplicationOvertime getById(Long id);
	
	EmpApplicationOvertime getEaoByEmpAndDate(EmpApplicationOvertime userOvertime);
	
	int getEaoByEmpAndDateCount(EmpApplicationOvertime userOvertime);
	
	List<EmpApplicationOvertime> getTotalWorkHours(EmpApplicationOvertime userOvertime);
	
	List<Map<String,Object>> getApplyOverTimeExcel(EmpApplicationOvertime userOvertime);
	
	List<Map<String,Object>> getApplyOverTimeSumReport(EmpApplicationOvertime userOvertime);
	
	List<Map<String,Object>> getApplyOverTimeSumReportByPage(EmpApplicationOvertime userOvertime);
	
    int getReportCount(EmpApplicationOvertime userOvertime);
	
	List<EmpApplicationOvertime> getReportPageList(EmpApplicationOvertime userOvertime);
	
	List<EmpApplicationOvertime> getExportReportList(EmpApplicationOvertime userOvertime);
	
    EmpApplicationOvertime queryByProcessInstanceId(String processInstanceId);
    
    List<EmpApplicationOvertime> getListByCondition(EmpApplicationOvertime userOvertime);
    
    //获取未完成的单据
    List<EmpApplicationOvertime> getUnCompleteList(EmpApplicationOvertime userOvertime);
    
    //拉取已完成列表
    List<EmpApplicationOvertime> getCompleteList(EmpApplicationOvertime userOvertime);
	
}
