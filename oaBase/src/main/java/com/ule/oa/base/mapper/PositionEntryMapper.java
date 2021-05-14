package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;
import com.ule.oa.base.po.EmpEntryRegistration;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 入职查询
 * @Description: 入职查询
 * @author yangjie
 * @date 2017年6月15日
 */
public interface PositionEntryMapper extends OaSqlMapper {
	
	List<EmpEntryRegistration> getListByMonth(EmpEntryRegistration entryRegistration);
	
	int getReportCount(EmpEntryRegistration entryRegistration);
		
	List<EmpEntryRegistration> getReportPageList(EmpEntryRegistration entryRegistration);
	
	List<Map<String,Object>> getExportReportList(EmpEntryRegistration entryRegistration);

}
