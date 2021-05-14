package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;

import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.User;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 延时工作申请
 * @Description: 延时工作申请
 * @author yangjie
 * @date 2017年6月12日
 */
public interface EmpApplicationOvertimeService {
	
	public void save(HttpServletRequest request,EmpApplicationOvertime userOvertime) throws Exception;
	
	public EmpApplicationOvertime getById(Long id);
	
	public void chargeTime(EmpApplicationOvertime old,User user) throws Exception;

	EmpApplicationOvertime getEaoByEmpAndDate(EmpApplicationOvertime userOvertime);
	
	int getEaoByEmpAndDateCount(EmpApplicationOvertime userOvertime);
	
	Double getTotalWorkHours(EmpApplicationOvertime userOvertime);
	
	public List<Map<String, Object>> getApplyOverTimeExcel(Date startTime,Date endTime, Long departId) throws Exception;
	
	/**
	  * getApplyOverTimeSumReport(月度加班统计报表导出)
	  * @Title: getApplyOverTimeSumReport
	  * @Description: 月度加班统计报表导出
	  * @param applyStartDate
	  * @param applyEndDate
	  * @param departId
	  * @return
	  * @throws Exception    设定文件
	  * List<Map<String,Object>>    返回类型
	  * @throws
	 */
	public List<Map<String,Object>> getApplyOverTimeSumReport(Date applyStartDate,Date applyEndDate,Long departId) throws Exception;
	
	public PageModel<EmpApplicationOvertime> getReportPageList(EmpApplicationOvertime userOvertime);
	
	public HSSFWorkbook exportExcel(EmpApplicationOvertime overtime);
	
	/**
	  * getApplyOverTimeSumReport(月度加班统计报表分页查询)
	  * @Title: getApplyOverTimeSumReport
	  * @Description: 月度加班统计报表分页查询
	  * @return
	  * @throws Exception    设定文件
	  * List<Map<String,Object>>    返回类型
	  * @throws
	 */
	public PageModel<Map<String,Object>> getApplyOverTimeSumReportByPage(EmpApplicationOvertime userOvertime) throws Exception;
	
	public HSSFWorkbook exportSumReport(EmpApplicationOvertime overtime) throws Exception;
	
	//根据流程实例Id查询加班对象
	public EmpApplicationOvertime queryByProcessInstanceId(String processInstanceId);
	
	/**
	 * 业务类转化为显示
	 * @param taskVO
	 * @param processInstanceId
	 */
	public void setValueToVO(TaskVO taskVO,String processInstanceId,String key);
	
	//加班申请最终充值调休
	public void endHandle(String processInstanceId,User user) throws Exception;
	
	/**
	 * 审批加班
	 * @param processId
	 * @param commentType
	 * @throws Exception
	 */
	public void completeTask(HttpServletRequest request,String processId, String comment ,String commentType,EmpApplicationOvertime param) throws Exception;
	
	/**
	 * 修改流程Id
	 * @param newOvertime
	 */
	public void updateProcessInstanceId(EmpApplicationOvertime newOvertime);
	
	/**
	 * 业务类转化为显示
	 * @param taskVO
	 * @param processInstanceId
	 */
	public void setValueToVO1(TaskVO taskVO,String processInstanceId,String key);
	
	
	/**
	 * 回退加班流程
	 * @param processInstanceId  
	 * @param taskDefKey
	 */
	public void backProcess(String processInstanceId,String taskDefKey) throws Exception;

	public PageModel<EmpLeave> getOvertimeManagePageList(
			EmpLeave empovertimeLeave) throws OaException;

	public Map<String, Object> saveOverTimeManage(EmpLeave empovertimeLeave);

	public Map<String, Object> updateOverTimeManage(EmpLeave empovertimeLeave);

	public Map<String, Object> queryInfoByEmp(long employeeId, Integer year,
			Integer page, Integer rows);

	public Map<String, Object> queryInfoById(long id);
	
	/**
	 * 提醒填写延时工作申请
	 */
	public void remindWriteOvertimeApply();
	
}
