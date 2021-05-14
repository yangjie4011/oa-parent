package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.HrEmpResign;
import com.ule.oa.base.po.QuitHistory;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 离职查询
 * @Description: 离职查询
 * @author yangjie
 * @date 2017年6月15日
 */
public interface PositionQuitMapper extends OaSqlMapper {
	
	
	int getReportCount(QuitHistory quitHistory);
		
	List<QuitHistory> getReportPageList(QuitHistory quitHistory);
	
	List<Map<String, Object>> getExportReportList(Employee employeeResign);
	
	int insert(HrEmpResign hrEmpResign);

}
