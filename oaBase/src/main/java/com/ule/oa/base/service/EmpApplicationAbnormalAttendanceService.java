package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;

import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.User;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 考勤异常消除申请
 * @Description: 考勤异常消除申请
 * @author yangjie
 * @date 2017年6月15日
 */
public interface EmpApplicationAbnormalAttendanceService {
	
	public Map<String,Object> save(EmpApplicationAbnormalAttendance attendance) throws Exception;
	
	public Map<String,Object> updateById(EmpApplicationAbnormalAttendance attendance) throws Exception;
	
	public EmpApplicationAbnormalAttendance getById(Long id);
	
	public int getEaoByEmpAndDateCount(EmpApplicationAbnormalAttendance attendance);

	
	List<EmpApplicationAbnormalAttendance> getListByMonth(EmpApplicationAbnormalAttendance attendance);
	
    public PageModel<EmpApplicationAbnormalAttendance> getReportPageList(EmpApplicationAbnormalAttendance attendance);
	
	public HSSFWorkbook exportExcel(List<EmpApplicationAbnormalAttendance> list);
	
	public List<EmpApplicationAbnormalAttendance> getExportReportList(EmpApplicationAbnormalAttendance attendance);

	public void completeTask(HttpServletRequest request,String processId, String comment, String commentType) throws Exception;
	
	public void completeTaskByAdmin(HttpServletRequest request,String processId, String comment ,String commentType,User user,Task task) throws Exception;
	
	public EmpApplicationAbnormalAttendance queryByProcessInstanceId(String instanceId);
	
	public void setValueToVO(TaskVO taskVO,String processInstanceId);

	public void updateProcessInstanceId(EmpApplicationAbnormalAttendance newAbnormal);

	public PageModel<EmpApplicationAbnormalAttendance> myAttnTaskList(
			EmpApplicationAbnormalAttendance attendance);
	
	void updateApprovalStatusById(Long id,Integer approvalStatus);

	
}
