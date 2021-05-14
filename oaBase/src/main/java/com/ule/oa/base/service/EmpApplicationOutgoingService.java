package com.ule.oa.base.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;

import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.po.User;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 外出申请
 * @Description: 外出申请
 * @author yangjie
 * @date 2017年6月9日
 */
public interface EmpApplicationOutgoingService {
	
    public Map<String,Object> save(EmpApplicationOutgoing userOutgoing) throws Exception;
	
	public Map<String,Object> updateById(EmpApplicationOutgoing userOutgoing) throws Exception;
	
	public EmpApplicationOutgoing getById(Long id);
	
	/**
	  * getReportPageList(分页查询外出查询报表)
	  * @Title: getReportPageList
	  * @Description: 分页查询外出查询报表
	  * @param empApplicationOutgoing
	  * @return    设定文件
	  * PageModel<EmpApplicationOutgoing>    返回类型
	  * @throws
	 */
	public PageModel<EmpApplicationOutgoing> getReportPageList(EmpApplicationOutgoing empApplicationOutgoing);
	
	public HSSFWorkbook exportReport(EmpApplicationOutgoing empApplicationOutgoing);

	/**
	 * 外出汇总统计
	 * @param empApplicationOutgoing
	 * @return
	 */
	public PageModel<EmpApplicationOutgoing> getOutTotalPageList(EmpApplicationOutgoing empApplicationOutgoing);

	/**
	 * 外出汇总统计报表导出
	 * @param empApplicationOutgoing
	 * @return
	 */
	public HSSFWorkbook exportOutGoingTotal(EmpApplicationOutgoing empApplicationOutgoing);

	public EmpApplicationOutgoing queryByProcessInstanceId(String instanceId);

	public void completeTask(HttpServletRequest request,String instanceId, String comment, String commentType) throws Exception;

	public void setValueToVO(TaskVO taskVO, String processInstanceId);
	
	/**
	 * 修改流程实例id
	 * @param newOutgoing
	 */
	public void updateProcessInstanceId(EmpApplicationOutgoing newOutgoing);

	public PageModel<EmpApplicationOutgoing> getApprovePageList(EmpApplicationOutgoing empApplicationOutgoing);

	public PageModel<EmpApplicationOutgoing> getInvalidPageList(EmpApplicationOutgoing empApplicationOutgoing);

	public PageModel<EmpApplicationOutgoing> getAuditedPageList(EmpApplicationOutgoing empApplicationOutgoing);

	public void completeTaskByAdmin(HttpServletRequest request,String processId, String comment, String commentType,User user,Task task) throws Exception;
}
