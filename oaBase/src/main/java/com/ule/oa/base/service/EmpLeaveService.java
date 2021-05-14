package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.po.LeaveRecord;
import com.ule.oa.base.po.RequestQueryLeaveRecord;
import com.ule.oa.base.po.ResponseQueryLeaveRecord;
import com.ule.oa.base.po.ResponseQueryLeaveRecordDetail;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

public interface EmpLeaveService {
	/**
	  * updateEmpLeaveApply(请假申请调的接口)
	  * @Title: updateEmpLeaveApply
	  * @Description: 请假申请调的接口
	  * @param planLeaves
	  * @return
	  * @throws Exception    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	public boolean updateEmpLeaveApply(List<Map<String, Object>> planLeaves) throws Exception;
	
	/**
	  * updateEmpLeaveApply(销假申请调的接口)
	  * @Title: updateEmpLeaveApply
	  * @Description: 销假申请调的接口
	  * @param planLeaves
	  * @return
	  * @throws Exception    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	public boolean updateEmpLeaveAbolish(List<Map<String, Object>> planLeaves) throws Exception;
	
	/**
	  * updateEmpLeaveAudit(审批结束后调的接口)
	  * @Title: updateEmpLeaveAudit
	  * @Description: 审批结束后调的接口
	  * @param planLeaves
	  * @return
	  * @throws Exception    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	public boolean updateEmpLeaveAudit(List<Map<String, Object>> planLeaves) throws Exception;
	
	/**
	 * hr登记直接扣减病假
	 * @param planLeaves
	 * @return
	 * @throws Exception
	 */
	public boolean updateSickEmpLeaveByHrRegister(List<Map<String, Object>> planLeaves) throws Exception;
	
	/**
	 * @throws OaException 
	  * 查询年假，病假，调休汇总信息（我的假期页面）
	  * @Title: getMyViewLeave
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	Map<String, Object> getMyViewLeave(Long empId) throws OaException;

	/**
	 * @throws OaException 
	  * 我的考勤详细年假查询
	  * @Title: getYearLeaveView
	  * @param employeeId
	  * @param year
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String, Object> getYearLeaveView(Long employeeId, int year) throws OaException;

	/**
	 * @throws OaException 
	  * 我的考勤详细年假查询
	  * @Title: getSickLeaveView
	  * @param employeeId
	  * @param year
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String, Object> getSickLeaveView(Long employeeId, int year) throws OaException;
	
	public EmpLeave getByCondition(EmpLeave empLeave) throws OaException;
	
	public List<EmpLeave> getByListCondition(EmpLeave empLeave) throws OaException;
	
	public int updateById(EmpLeave record) throws OaException;
    
	public int save(EmpLeave record) throws OaException;
	
	/**
	  * calculationOurAge(根据入职时间自动计算员工司龄)
	  * @Title: calculationOurAge
	  * @Description: 根据入职时间自动计算员工司龄
	  * void    返回类型
	  * @throws
	 */
	public void calculationOurAge();
	
	/**
	  * calculationYearLeave(自动计算可用年假和实际年假)
	  * @Title: calculationYearLeave
	  * @Description: 自动计算可用年假和实际年假
	  * void    返回类型
	  * @throws
	 */
	public void calculationYearLeave();
	
	/**
	  * calculationSickLeaveByEmpId(根据员工id计算病假)
	  * @Title: calculationSickLeaveByEmpId
	  * @Description: 根据员工id计算病假
	  * @param empId    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void calculationSickLeaveByEmpId(Long empId);
	
	/**
	  * calculationSickLeave(自动计算病假实际可用天数)
	  * @Title: calculationSickLeave
	  * @Description: 自动计算病假实际可用天数
	  * void    返回类型
	  * @throws
	 */
	public void calculationSickLeave();
	
	/**
	  * initNextYearLeave(初始化下一年的年假)
	  * @Title: initNextYearLeave
	  * @Description: 初始化下一年的年假
	  * void    返回类型
	  * @throws
	 */
	public void initNextYearLeave();
	
	/**
	  * initNextSickLeave(初始化下一年的病假)
	  * @Title: initNextSickLeave
	  * @Description: 初始化下一年的病假
	  * void    返回类型
	  * @throws
	 */
	public void initNextSickLeave();
	
	/**
	  * calculationYearLeaveByEmpId(根据员工id计算员工假期)
	  * @Title: calculationYearLeaveByEmpId
	  * @Description: 根据员工id计算员工假期
	  * @param empId    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void calculationYearLeaveByEmpId(Long empId);
	
	/**
	 * @throws OaException 
	  * generateDefaultYearLeave(生成默认的年假)
	  * @Title: generateDefaultYearLeave
	  * @Description: 生成默认的年假
	  * @param empId		员工id
	  * @param companyId	公司id
	  * @param year    		假期所属年份
	  * @throws
	 */
	public void generateDefaultYearLeave(Employee emp,Integer year,Date entryTime) throws OaException;
	
	/**
	  * generateDefaultSickLeave(生成默认的病假)
	  * @Title: generateDefaultSickLeave
	  * @Description: 生成默认的病假
	  * @param empId		员工id
	  * @param companyId	公司id
	  * @param year    		假期所属年份
	  * @throws
	 */
	public void generateDefaultSickLeave(Employee emp,Integer year,Date entryTime) throws OaException;
	
	/**
	  * updateTimeByIds(刷数据专用)
	  * @Title: updateTimeByIds
	  * @Description: 刷数据专用
	  * @param empLeave
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateTimeByIds(EmpLeave empLeave);
	
	/**
	  * checkLeaveIsValidate(验证假期有效性)
	  * @Title: checkLeaveIsValidate
	  * @Description: 验证假期有效性
	  * @param planleave
	  * @return
	  * @throws OaException    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	public boolean checkLeaveIsValidate(EmpLeave planleave) throws OaException;

	
	/**
	 * 修复考勤数据
	 * @param id
	 */
	void repairLeaveDatas(Long id);
	
	/**
	 * @throws Exception 
	 * @throws OaException 
	  * splitLeave(如果申请的假期存在跨年或者跨延期节点，则需要拆分后扣减假期)
	  * @Title: splitLeave
	  * @Description: 如果申请的假期存在跨年或者跨延期节点，则需要拆分后扣减假期
	  * @return    设定文件
	  * List<EmpLeave>    返回类型
	  * @throws
	 */
	public List<EmpLeave> splitLeave(EmpLeave planleave) throws Exception;
	
	/**
	  * getReportPageList(查询假期余额报表)
	  * @Title: getReportPageList
	  * @Description: 查询假期余额报表
	  * @param emp
	  * @return    设定文件
	  * PageModel<EmpLeave>    返回类型
	  * @throws
	 */
	public PageModel<EmpLeave> getReportPageList(Employee emp);
	
	/**
	  * exportReport(导出假期余额报表)
	  * @Title: exportReport
	  * @Description: 导出假期余额报表
	  * @param emp
	  * @return    设定文件
	  * HSSFWorkbook    返回类型
	  * @throws
	 */
	public HSSFWorkbook exportReport(Employee emp);
	
	/**
	  * exportReport(导出剩余假期报表)
	  * @Title: exportReport
	  * @Description: 导出剩余假期报表
	  * @param emp
	  * @return    设定文件
	  * HSSFWorkbook    返回类型
	  * @throws
	 */
	public HSSFWorkbook exportLeaveRemainReport(Employee emp);
	
	/**
	  * exportReport(导出剩余假期报表<已用根据流水统计>)
	  * @Title: exportReport
	  * @Description: 导出剩余假期报表
	  * @param emp
	  * @return    设定文件
	  * HSSFWorkbook    返回类型
	  * @throws
	 */
	public HSSFWorkbook exportLeaveRemainReportNew(Employee emp);
	
	public void repairSickLeave() throws OaException;
	
	/**
	  * getRemainRest(获得剩余调休)
	  * @Title: getRemainRest
	  * @Description: 获得剩余调休
	  * @param empLeave
	  * @return    设定文件
	  * EmpLeave    返回类型
	  * @throws
	 */
	public EmpLeave getRemainRest(EmpLeave empLeave);

    /**
     * 调休详情页面
     * @param employeeId
     * @param year
     * @return
     */
	public Map<String, Object> getRestLeaveView(Long employeeId, int year);

	public PageModel<Map<String, Object>> MonthLeaveDetailPageImpl(Employee cp);
	
	public PageModel<Map<String, Object>> MonthLeaveCountPageImpl(Employee cp);

	public XSSFWorkbook exportReportMonthLeaveDetail(Employee cp) throws Exception;
	
	public HSSFWorkbook exportReportMonthLeaveCount(Employee cp)throws Exception;

	public void updateLeaveByEmpInfo(EmployeeApp employee) throws Exception;
	
	public void updateQuitLeaveByEmpInfo(EmployeeApp employee) throws Exception;

	void updateSickLeaveByEmpInfo(EmployeeApp employee) throws Exception;
	
	/**
	 * 根据用户id修改已使用假期数据
	 * @param empLeavePO
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updateUsedLeaveByEmpId(EmpLeave empLeave) throws Exception;
	
	/**
	 * 根据用户id修改假期数据
	 * @param empLeavePO
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updateLeaveByEmpId(EmpLeave empLeave) throws Exception;
	
	//年休假基数分页查询
	public PageModel<Map<String,Object>> getLeaveRadixList(Employee employee);

	/**
	 * 假期计算器
	 * @param endTime
	 * @param employeeId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryEmpLeaveInfoByDate(Date endTime, Long employeeId)throws Exception;
	
	/**
	 * 
	 */
	public Double getActualYearDays(Date endTime, Long employeeId)throws Exception;
	
	/**
	 * 修改员工假期基数
	 * @param legalRaduix
	 * @param welfareRaduix
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updateLeaveRuduix(Double legalRaduix, Double welfareRaduix, Integer type,Long employeeId,String remarkRecord)throws Exception;

	public void leaveFailure();
	//各种失效流程定时
	public void Failure();
	
	/**
     * getReaminLeaveList(获得剩余假期明细)
     * @Title: getReaminLeaveList
     * @Description: 获得剩余假期明细
     * @param record
     * @return    设定文件
     * List<Employee>    返回类型
     * @throws
    */
    List<EmpLeave> getReaminLeaveList(EmpLeave record);
    //病假拆分 新增非带薪病假 根据当前年
	public void SickLeaveSplit();
	
	//删除2018及以后的除（年假，病假，调休）的数据
	public void deleteOtherLeaveAfter2018();
	
    /**
     * 导入假期
     * @param file
     * @return
     * @throws OaException
     * @throws Exception
     */
	public String importEmpLeaveList(MultipartFile file) throws OaException,Exception;
	
	/**
	 * 扣减年假事假
	 * @param file
	 * @return
	 * @throws OaException
	 * @throws Exception
	 */
	public String reduceAffairAndYearLeave(MultipartFile file) throws OaException,Exception;

	public PageModel<ResponseQueryLeaveRecord> getLeaveRecordList(RequestQueryLeaveRecord requestQueryLeaveRecord);
	
	//2019春节统一扣减年假
	public void reduceYearLeave(Long employeeId,List<String> dateList,String employeeName,String workTypeDisplayCode,String schedulingDisplayCode)throws Exception;
	
	//获取其它调休
	public Double getOtherRestLeaveCount(Long employeeId);
	
	//扣减失效的其它调休
	public void reduceInvalidOtherRestLeave(EmpLeave data);
	
	//查询失效的其它调休
	public List<EmpLeave> getInvalidOtherRestLeaveList(Date endTime);
	
	public void initNextYearLeaveByYear(String year,Integer betDays);
	

}
