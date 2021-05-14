package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ule.oa.base.po.EmpEntryRegistration;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 入职查询
 * @Description: 入职查询
 * @author zhoujinliang
 * @date 2018年3月21日15:40:52
 */
public interface PositionEntryService {
	
	public int getEaoByEmpAndDateCount(EmpEntryRegistration entry);
	
    public PageModel<EmpEntryRegistration> getReportPageList(EmpEntryRegistration entry);

	public List<Map<String, Object>> getExportReportList(
			EmpEntryRegistration entry);

	public HSSFWorkbook exportExcel(EmpEntryRegistration list);
	
	//根据入职申请单id取消入职
	public Map<String,Object> cancelEntry(Long entryApplyId) throws Exception;

	
}