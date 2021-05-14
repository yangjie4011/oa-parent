package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.User;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 排班申请表
 * @Description: 排班申请表
 * @author yangjie
 * @date 2017年8月31日
 */
public interface ApplicationEmployeeClassService {
	
	//生成下个月的排班申请数据
	public void addLastMonth(User user) throws Exception;
	
	//根据条件查询排班申请
	public List<ApplicationEmployeeClass> getByCondition(ApplicationEmployeeClass applicationEmployeeClass);
	
	//保存排班信息
	public void save(Long classdetailId,String classMonth,Map<String,Map<String,String>> map,Map<String,Map<String,String>> map1,User user) throws OaException;
	
	//提交审核
	public Map<String,Object> submitApprove(Long classdetailId,User user) throws OaException;
	
	//删除排班申请
	public void deleteEmployeeClass(Long id,User user) throws Exception;
	
	//提交调班申请
	public Map<String,Object> moveEmployeeClass(HttpServletRequest request,Long classdetailId,User user,Map<String,Map<String,String>> map,Map<String,Map<String,String>> map1) throws OaException;
	
	//Hr单独提交调班申请
	public Map<String,Object> HrMoveEmployeeClass(Long classdetailId,User user,Map<String,Map<String,String>> map,Map<String,Map<String,String>> map1) throws OaException;

	/**
	  * exportEmpClassReprotById(导出排班查询报表)
	  * @Title: exportEmpClassReprotById
	  * @Description: 导出排班查询报表
	  * @param empClassId
	  * @throws Exception    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void exportEmpClassReprotById(Long empClassId) throws Exception;
	
	public List<ApplicationEmployeeClass> getPassList(ApplicationEmployeeClass applicationEmployeeClass);
	
	//后台排班报表导出
	public HSSFWorkbook exportClass(String month,Long departId, Long groupId) throws Exception;
	
	//排班申请最终同步数据操作
	public void endHandle(String processInstanceId,User user);
	
	/**
	 * 审批排班
	 * @param processId
	 * @param commentType
	 * @throws Exception
	 */
	public void completeTask(HttpServletRequest request,String processId, String comment ,String commentType) throws Exception;
	
	public ApplicationEmployeeClass queryByProcessInstanceId(String processInstanceId);
	
	/**
	 * 业务类转化为显示
	 * @param taskVO
	 * @param processInstanceId
	 */
	public void setValueToVO(TaskVO taskVO, String processInstanceId);
	/**
	 * 保存流程实例id
	 * @param newScheduling
	 */
	public void updateProcessInstanceId(ApplicationEmployeeClass newScheduling);
	
	//分页查询待办列表
	public PageModel<ApplicationEmployeeClass> getHandlingListByPage(ApplicationEmployeeClass employeeclass);
	
	//分页查询已办列表
	public PageModel<ApplicationEmployeeClass> getHandledListByPage(ApplicationEmployeeClass employeeclass);
	
	//获取排班申请单详情数据
	public Map<String,Object> getDetailById(Long id);
	
	//分页查询失效列表
	public PageModel<ApplicationEmployeeClass> getInvalidListByPage(ApplicationEmployeeClass employeeclass);
	
	//获取班次弹框详细信息
	public Map<String,String> getSettingInfo(String date,Long setId,Long newSetId);
	
	/**
	 * 人事后台Admin调班
	 * @param date
	 * @param employId
	 * @param setId
	 * @param remark 
	 * @throws OaException
	 */
	public Map<String,Object> hrMoveAtAdmin(HttpServletRequest request,String date,Long employId,Long setId, String remark) throws OaException;
	
	//获取班次弹框详细信息
	public Map<String, String> getSettingInfos(String date, Long setId, Long classId,Long empId);

	public Double getShouldTime(Date classMonth, Date lastDay);
	
	//导出排班详情数据
	public HSSFWorkbook exportDetailById(Long id) throws Exception;

	public PageModel<ApplicationEmployeeClass> queryChangeClass(
			ApplicationEmployeeClass empClassTemp);
	
	//获取未提交审核的排班申请数据
	public Map<String,Object> getUnCommitData(Long departId,Long groupId);
	
	//保存排班数据
	public Map<String,Object> saveSchedule(HttpServletRequest request,String info,Long departId,Long groupId);
	
	//排班提交审核
	public Map<String,Object> commitSchedule(HttpServletRequest request,Long departId,Long groupId) throws OaException;
	
	//后台调班
	public Map<String,Object> changeClassSet(HttpServletRequest request,String month,Long departId,Long groupId,String info) throws OaException;
	
	//排班记录
	public PageModel<ApplicationEmployeeClass> getScheduleRecord(
			ApplicationEmployeeClass condition);
}
