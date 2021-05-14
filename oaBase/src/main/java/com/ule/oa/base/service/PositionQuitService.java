package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.HrEmpResign;
import com.ule.oa.base.po.QuitHistory;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 离职查询
 * @Description: 离职查询
 * @author zhoujinliang
 * @date 2018年3月21日16:52:51
 */
public interface PositionQuitService {
	
	public int getEaoByEmpAndDateCount(Employee emp);
	
	public int insertEmpQuitInfo(HrEmpResign hrEmpResign);
	
    public PageModel<QuitHistory> getReportPageList(QuitHistory quitHistory);
	
    public List<Map<String, Object>> getExportReportList(
    		Employee emp);

	public HSSFWorkbook exportExcel(Employee emp);
	
	public Map<String,Object> updateEmpQuitInfo(Employee employee) throws Exception;

	public Map<String,Object> updateEmpQuitDate(Employee employee);

	public List<CompanyConfig> quitSendMailEmps();

}