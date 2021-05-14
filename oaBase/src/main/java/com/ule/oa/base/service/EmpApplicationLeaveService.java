package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;

import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveAbolish;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.EmpLeaveReport;
import com.ule.oa.base.po.User;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 请假申请
 * @Description: 请假申请
 * @author yangjie
 * @date 2017年6月14日
 */
public interface EmpApplicationLeaveService {
	
    public Map<String,Object> updateById(EmpApplicationLeave leave) throws Exception;
	
	public EmpApplicationLeave getById(Long id);
	
	List<EmpApplicationLeave> getListByEmployeeId(Long employeeId);
	
	public List<EmpApplicationLeaveDetail> getLeaveDetailList(Long leaveId);
	
	public Map<String,Object> save(HttpServletRequest request,User user) throws Exception;
	
	public Map<String, Object> saveAbolishLeave(HttpServletRequest request, User user)  throws Exception;
	
	public void lockVecation(Long leaveId,boolean applyStatus,User user)  throws Exception;
	
	List<EmpApplicationLeaveDetail> getListByEmployee(EmpApplicationLeaveDetail leaveDetail);
	
	/**
	 * 判断是否有重复日期
	 * @param leaveDetail
	 * @return
	 */
	Boolean isContinuityDate(EmpApplicationLeaveDetail leaveDetail);

	public List<EmpApplicationLeave> getListByCondition(EmpApplicationLeave model); 
	
    public PageModel<EmpApplicationLeave> getReportPageList(EmpApplicationLeave leave);
    
    public PageModel<EmpApplicationLeave> myLeaveTaskList(EmpApplicationLeave leave);
	
	public HSSFWorkbook exportExcel(EmpApplicationLeave leave);
	
	public void assembleReport(Long employeeId, Long departId, String employeeName, EmpApplicationLeaveDetail detail, List<EmpLeaveReport> empLeaveReportList);

	public Map<String, Object> getLeaveCountInfoByEmpId(Long employeeId);

	public EmpApplicationLeave queryByProcessInstanceId(String instanceId);
	
	/**
	 * 审批假期
	 * @param processId
	 * @param commentType
	 * @throws Exception
	 */
	public void completeTask(HttpServletRequest request,String processId, String comment ,String commentType) throws Exception;
	
	/**
	 * 审批假期
	 * @param processId
	 * @param commentType
	 * @throws Exception
	 */
	public void completeTaskByAdmin(HttpServletRequest request,String processId, String comment ,String commentType,User user,Task task) throws Exception;
	
	/**
	 * 业务类转化为显示
	 * @param taskVO
	 * @param processInstanceId
	 */
	public void setValueToVO(TaskVO taskVO, String processInstanceId);
	
	/**
	 * 存储流程实例id
	 * @param newLeave
	 */
	public void updateProcessInstanceId(EmpApplicationLeave newLeave);
	
	//发送拒绝假期通知
	public void sendRefuseLeaveNotice(String processInstanceId,String comment);

	public String completeTasks(HttpServletRequest request,String[] processId, String comment, String commentType)throws Exception;

	public PageModel<EmpApplicationLeave> getAbatePageList(
			EmpApplicationLeave leave);
	
	/**
	 * 修复班次对不上的请假数据
	 */
	public void repairErrorEmpClassApply();
	
	public void repairErrorWorkHours(Long id,Integer delFlag);
	
	/**
	 * 修改请假申请单时间段
	 * @param id
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Map<String, Object> updateLeaveTime(Long id,String startTime,String endTime);
	
	/**
	 * 获取普通假期申请开始结束小时数
	 * @param employeeId
	 * @param type
	 * @param date
	 * @param leaveType
	 * @return
	 */
	Map<String,Object> getLeaveHours(Long employeeId, String type, String date, String leaveType);
	
	/**
	 * 计算请假天数
	 * @param empId
	 * @param startTime1
	 * @param startTime2
	 * @param endTime1
	 * @param endTime2
	 * @param leaveType
	 * @return
	 */
	Map<String, Object> calculatedLeaveDays(Long empId, String startTime1, String startTime2, String endTime1,
			String endTime2, String leaveType);
	
	/**
	 * 获取员工小孩数
	 * @param employeeId
	 * @return
	 */
	Map<String, Object> getChildren(Long employeeId);
	
	/**
	 * 获取产假额外的请假天数
	 */
	Map<String, Object> getExtraMaternityLeaveDays(HttpServletRequest request);

}
