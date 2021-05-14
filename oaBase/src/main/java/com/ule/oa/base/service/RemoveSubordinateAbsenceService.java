package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.RemoveSubordinateAbsence;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

public interface RemoveSubordinateAbsenceService {

	void commitApplicationForm(HttpServletRequest request,RemoveSubordinateAbsence removeSubordinateAbsence) throws OaException;

	void completeTask(HttpServletRequest request,String processId, String comment, String commentType) throws OaException;
	Map<String, Object> getRemoveSubordinateAbsencePage(Long departId,
			String month, String empCode, String empCnName,
			Integer page, Integer rows) throws OaException;

	Map<String, Object> getAttnDetail(Long empId, Date date) throws OaException;

	void setValueToVO(TaskVO taskVO, String processInstanceId);

	RemoveSubordinateAbsence getById(Long removeSubordinateAbsenceId);

	PageModel<RemoveSubordinateAbsence> getReportPageList(
			RemoveSubordinateAbsence attendanceQuery);

	RemoveSubordinateAbsence queryByProcessInstanceId(String processId);


	HSSFWorkbook exportExcel(RemoveSubordinateAbsence subAttn);

}
