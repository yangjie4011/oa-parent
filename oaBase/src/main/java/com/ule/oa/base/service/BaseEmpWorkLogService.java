package com.ule.oa.base.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import com.ule.oa.base.po.BaseEmpWorkLog;
import com.ule.oa.common.exception.OaException;

/**
 * 员工工作日志
 * @author yangjie
 *
 */
@Service
public interface BaseEmpWorkLogService {
	
	/**
	 * 保存
	 * @param workLog
	 * @return
	 * @throws OaException
	 */
	Map<String,Object> save(BaseEmpWorkLog workLog) throws OaException;
	
	/**
	 * 获取流程详情弹框
	 * @param workDate
	 * @return
	 * @throws OaException
	 */
	Map<String,Object> getProcessInfoDiv(String workDate,Long employeeId) throws OaException;
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	BaseEmpWorkLog getById(Long id);
	
	/**
	 * 审批流程
	 * @param request
	 * @param processId
	 * @param comment
	 * @param commentType
	 * @throws OaException
	 */
	void completeTask(HttpServletRequest request,String processId, String comment, String commentType) throws OaException;
	
	
	Map<String,Object> queryWorkLogDetalInfoByMonth(String month);
	
	/**
	 * 审阅分页查询
	 * @param departId
	 * @param leaderName
	 * @param month
	 * @param empCode
	 * @param empCnName
	 * @param page
	 * @param rows
	 * @return
	 */
	Map<String, Object> getApporvalPage(String month,Long departId,String leaderName,String empCode,String empCnName,Integer page, Integer rows,String index);
	
	/**
	 * 工作日志查询
	 * @param month
	 * @param departId
	 * @param leaderName
	 * @param empCode
	 * @param empCnName
	 * @param page
	 * @param rows
	 * @return
	 */
	Map<String, Object> getWorkLogSearchPage(String month,Long departId,String leaderName,String empCode,String empCnName,Integer page, Integer rows);
	
	/**
	 * 工作日志导出
	 * @param month
	 * @param departId
	 * @param leaderName
	 * @param empCode
	 * @param empCnName
	 * @return
	 */
	public HSSFWorkbook exportWorkLog(String month,Long departId,String leaderName,String empCode,String empCnName);
	
	/**
	 * 发送未登记提醒
	 */
	public void sendUnSignNotice();

}
