package com.ule.oa.base.service;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;

import com.ule.oa.base.po.EmpApplicationLeaveAbolish;
import com.ule.oa.base.po.User;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.utils.PageModel;


public interface EmpApplicationLeaveAbolishService {
		
		/**
		 * 保存销假单
		 * @param processId
		 * @param user
		 * @throws Exception
		 */
		public Map<String,Object> save(HttpServletRequest request,EmpApplicationLeaveAbolish leaveAbolish,User user) throws Exception;
		
		/**
		 * 根据流程id查询销假单
		 * @param processId
		 */
		public EmpApplicationLeaveAbolish queryByProcessInstanceId(String instanceId);
		
		/**
		 * 审批假期
		 * @param processId
		 * @param commentType
		 * @throws Exception
		 */
		public void completeTask(HttpServletRequest request,String processId, String comment ,String commentType) throws Exception;
		
		
		/**
		 * 业务类转化为显示
		 * @param taskVO
		 * @param processInstanceId
		 */
		public void setValueToVO(TaskVO taskVO, String processInstanceId);
		
		/**
		 * 根据id查询销假单
		 * @param processId
		 */
		public EmpApplicationLeaveAbolish getById(Long id);
		
		/**
		 * 销假最终还假期，考勤，月度假期报表
		 * @param processId
		 */
		public void endHandle(String processInstanceId,User user) throws Exception;
		
		/**
		 * 获取实际的请假开始,结束时间
		 * @param employeeId
		 * @param leaveType
		 * @param startTime
		 * @param endTime
		 */
		public Map<String,String> getActualEndTime(Long employeeId,int leaveType,Date startTime,Date endTime);
		
		/**
		 * 校验销假的开始时间是否合格
		 * @param leaveId
		 * @param employeeId
		 * @param leaveType
		 * @param startTime
		 * @param endTime
		 */
		public Map<String,String> checkStartTime(Long leaveId,Date startTime,Date endTime) throws Exception;

		public PageModel<EmpApplicationLeaveAbolish> getReportPageList(
				EmpApplicationLeaveAbolish leave);

		public void completeTaskByAdmin(HttpServletRequest request,String processId, String comment,
				String commentType, User user, Task task) throws Exception;

		public PageModel<EmpApplicationLeaveAbolish> myLeaveTaskList(
				EmpApplicationLeaveAbolish leave);

		public HSSFWorkbook exportExcel(EmpApplicationLeaveAbolish leave);


}
