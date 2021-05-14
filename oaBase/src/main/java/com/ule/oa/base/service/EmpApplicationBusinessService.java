package com.ule.oa.base.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;

import com.itextpdf.text.DocumentException;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationBusinessDetail;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 出差申请表
 * @Description: 出差申请表
 * @author yangjie
 * @date 2017年6月13日
 */
public interface EmpApplicationBusinessService {
	
	/**
	 * 
	 * @param business
	 * @param type 1：出差，2：出差总结 
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> updateById(EmpApplicationBusiness business, String type) throws Exception;
	
	public EmpApplicationBusiness getById(Long id);
	
	public List<EmpApplicationBusinessDetail> getBusinessDetailList(Long businessId);
	
	public Map<String,Object> save(HttpServletRequest request,User user) throws Exception;
	
	public Map<String,Object> saveWorkSummary(HttpServletRequest request,User user) throws Exception;
	
	/**
	 * @throws OaException 
	 * @param l 
	 * @throws MessagingException 
	 * @throws IOException 
	 * @throws DocumentException 
	  * exportApplyResultByBusinessId(导出出差申请表)
	  * @Title: exportApplyResultByBusinessId
	  * @Description: 导出出差申请表
	  * @throws
	 */
	public void exportApplyResultByBusinessId(Long businessId,Long runId) throws DocumentException, IOException, MessagingException, OaException;
	
	/**
	  * exportApplyReportByBusinessId(导出出差总结报告)
	  * @Title: exportApplyReportByBusinessId
	  * @Description: 导出出差总结报告
	  * @throws
	 */
	public void exportApplyReportByBusinessId(Long businessId,Long ruProcdefId);
	
	/**
	  * getReportPageList(分页查询出差查询报表)
	  * @Title: getReportPageList
	  * @Description: 分页查询出差查询报表
	  * @param empApplicationOutgoing
	  * @return    设定文件
	  * PageModel<EmpApplicationOutgoing>    返回类型
	  * @throws
	 */
	public PageModel<EmpApplicationBusiness> getReportPageList(EmpApplicationBusiness empApplicationBusiness);
	
	/**
	  * exportReport(导出出差查询报表)
	  * @Title: exportReport
	  * @Description: 导出出差查询报表
	  * @param empApplicationOutgoing
	  * @return    设定文件
	  * HSSFWorkbook    返回类型
	  * @throws
	 */
	public HSSFWorkbook exportReport(EmpApplicationBusiness empApplicationBusiness);

	public void setValueToVO(TaskVO taskVO, String processInstanceId);

	public EmpApplicationBusiness queryByProcessInstanceId(String instanceId);
	
	public void setValueToReportVO(TaskVO taskVO, String processInstanceId);

	public EmpApplicationBusiness queryByReportProcessInstanceId(String instanceId);
	
	public void completeTask(HttpServletRequest request,String instanceId, String comment, String commentType) throws Exception;

	public void completeReportTask(HttpServletRequest request,String instanceId, String comment, String commentType) throws Exception;

	public Map<String, String> startReportTask(String reProcdefCode, Employee employee, String entityId) throws Exception;
	
	/**
	 * 修改流程实例id
	 * @param newBusiness
	 */
	public void updateProcessInstanceId(EmpApplicationBusiness newBusiness);

	public Map<String, Object> update(HttpServletRequest request, User user) throws Exception;
	
	public void completeReportTaskBySystem(String instanceId, String comment, String commentType) throws Exception;

	public PageModel<EmpApplicationBusiness> getGroupListByInfo(
			EmpApplicationBusiness empApplicationBusiness, Integer typeNum);

	public PageModel<EmpApplicationBusiness> getApproveList(
			EmpApplicationBusiness empApplicationBusiness, Integer typeNum);

	public HSSFWorkbook exportGroupReport(
			EmpApplicationBusiness empApplicationBusiness, Integer typeNum);

	public void completeTaskByAdmin(HttpServletRequest request,String processId, String comment,
			String commentType, User user, Task task) throws Exception;
	
	public void completeTaskBySystem(HttpServletRequest request,String instanceId, String comment, String commentType) throws Exception;

}
