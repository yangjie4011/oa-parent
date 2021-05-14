package com.ule.oa.base.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeDuty;
import com.ule.oa.base.po.ApplicationEmployeeDutyDetail;
import com.ule.oa.base.po.EmployeeDuty;
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
public interface ApplicationEmployeeDutyService {
	
	//新增值班
	public void addEmployeeDuty(User user,ApplicationEmployeeDuty duty) throws OaException;
	//编辑值班
	public void updateEmployeeDuty(User user,Long dutyId,List<ApplicationEmployeeDutyDetail> list) throws OaException;
	
	public void updateDuty(ApplicationEmployeeDuty duty) throws OaException;
	//值班调班
	public Map<String, Object> moveEmployeeDuty(User user,Long dutyId,List<ApplicationEmployeeDutyDetail> list) throws OaException;
	//提交审核
	public Map<String,Object> submitApprove(Long dutyId,User user) throws OaException;
	
	public List<ApplicationEmployeeDuty> getByCondition(ApplicationEmployeeDuty duty);
	
	public ApplicationEmployeeDuty getById(Long id);
	
	//删除排班申请
	public void deleteEmployeeDuty(Long id,User user) throws Exception;
	
	public List<ApplicationEmployeeDutyDetail> selectByCondition(ApplicationEmployeeDutyDetail duty);
	
	/**
	  * exportEmpDutyReprotById(导出值班查询报表)
	  * @Title: exportEmpDutyReprotById
	  * @Description: 导出值班查询报表
	  * @param dutyId
	  * @throws Exception    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void exportEmpDutyReprotById(Long departId,String vacationName,String year,User user) throws Exception;
	
	public List<Map<String,Object>> queryDutyByCondition(String vacationName,String year,Long departId) throws Exception;
	
	//考勤管理-法定节假日值班管理-值班查询-查询
	public Map<String,Object> queryDuty(String vacationName,String year,Long departId) throws Exception;
	
	//查看值班详细数据
	public Map<String,Object> showDutyDetail(Long departId,String year,String vacationName);
	
	//考勤管理-法定节假日值班管理-值班查询-提交
	public  Map<String,Object> commitDutyByHr(HttpServletRequest request,String departId,String year,String vacationName,String info);
	
	//考勤管理-法定节假日值班管理-值班查询-查看（流水）
	public  Map<String,Object> showHistoryDetail(Long id);
	
	public HSSFWorkbook exportDuty(String vacationName,String year,Long departId);
	
	//根据部门，年份，节假日查询统计值班数据
	public List<EmployeeDuty> querySingleDutyByCondition(String vacationName,String year,Long departId) throws Exception;
	
	//排班申请最终同步数据操作
	public void endHandle(String processInstanceId,User user);
	
	/**
	 * 审批排班
	 * @param processId
	 * @param commentType
	 * @throws Exception
	 */
	public void completeTask(HttpServletRequest request,String processId, String comment ,String commentType) throws Exception;
	
	public ApplicationEmployeeDuty queryByProcessInstanceId(String processInstanceId);
	
	/**
	 * 业务类转化为显示
	 * @param taskVO
	 * @param processInstanceId
	 */
	public void setValueToVO(TaskVO taskVO, String processInstanceId);
	//查询未提交的值班安排
	
	public PageModel<ApplicationEmployeeDuty> getHandlingListByPage(
				ApplicationEmployeeDuty employeeDuty);
	public Map<String, Object> getDetailById(Long id);
	public HSSFWorkbook exportDutyDetailById(Long id);
	
	public HSSFWorkbook exportScheduleTemplate(Long departId, String year, String vacation);
	
	//查询未提交的值班安排
	public Map<String, Object> getUnCommitDuty(Long depart, String year, String vacation) throws OaException;

	//保存值班安排申请单
	public void saveApplicationDutyDetail(Long departId, String year, String vacation, List<ApplicationEmployeeDutyDetail> dutyDetailList) throws OaException;
	
	//提交值班
	public void commitDuty(HttpServletRequest request,Long departId, String year, String vacation) throws OaException;
	//值班记录
	public PageModel<ApplicationEmployeeDuty> getDutyRecord(ApplicationEmployeeDuty requestParam);
	
	//根据申请单id查询值班详情
	public Map<String, Object> getDutyDetailById(Long id) throws OaException;
	//导入排班模板
	public Map<String, String> importDutyTemplate(MultipartFile file, Long departId, String year, String vacation) throws OaException, IOException;
	public PageModel<ApplicationEmployeeDuty> getDutyTaskListByPage(
			ApplicationEmployeeDuty employeeDuty);
	

}
