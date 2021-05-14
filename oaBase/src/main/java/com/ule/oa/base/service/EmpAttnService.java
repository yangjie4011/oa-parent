package com.ule.oa.base.service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.EmpAttn;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

public interface EmpAttnService {

	PageModel<Map<String, Object>> getAttnReportPageList(EmpAttn empAttn);

	HSSFWorkbook exportAttnReport(EmpAttn empAttn);

	PageModel<Map<String, Object>> getSignRecordReportPageList(EmpAttn empAttn);

	HSSFWorkbook exportSignReportReport(EmpAttn empAttn);

	PageModel<ClassSetting> getAllClassShowList(ClassSetting classSetting);

	PageModel<Map<String, Object>> getMonthLackDetailPageList(EmpAttn empAttn);

	HSSFWorkbook exportMonthLackDetailReport(EmpAttn empAttn);

	PageModel<Map<String, Object>> getMonthLackTotalPageList(EmpAttn empAttn);

	HSSFWorkbook exportMonthLackTotalReport(EmpAttn empAttn);

	/**
	 * 读取上传的excel
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	String readExcelFile(MultipartFile file) throws IOException;
	
	//oaAdmin端直接修改员工考勤
	Map<String, Object> updateAttnTime(String employeeIds,String attnDate,String startTime,String endTime,String dataType,String remark) throws OaException;

	//通过id和考情时间 来查询员工当天的上下班打卡时间
	EmpAttn getAttnTimeByIdAndAttnTime(String employeeIds, String attnDate, Integer type) throws Exception;
	
	//员工签到
	PageModel<Map<String, Object>> getEmployeeSignReportPageList(EmpAttn empAttn);
	
	//定位签到
	PageModel<Map<String, Object>> getLocationCheckInData(EmpAttn empAttn);
	
	//定位签到-导出
	HSSFWorkbook exportLocationCheckInData(EmpAttn empAttn);
	
	//员工签到-导出
	HSSFWorkbook exportEmployeeSignReport(EmpAttn empAttn);
	
	void deleteSignRecordById(Long id,Integer delFlag);

}
