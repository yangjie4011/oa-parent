package com.ule.oa.base.service;

import java.util.Date;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ule.oa.base.po.AttentanceSetDTO;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.User;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

/**
 * 考勤设置
 * @author yangjie
 *
 */
public interface AttentanceSetService {
	
	/**
	 * 查询可以弹性工作设置的员工列表（除排班属性的所有员工）
	 * @param param
	 * @return
	 */
	public PageModel<AttentanceSetDTO> getPageList(AttentanceSetDTO param);
	
	/**
	 * 导入弹性工时设置模板
	 * @param file
	 * @return
	 * @throws OaException
	 * @throws Exception
	 */
	public Map<String, Object> importTemplate(MultipartFile file) throws OaException,Exception;
	
	/**
	 * 初始化指定员工排班数据
	 * @param employee
	 * @param classSettingId
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @param vacationMap
	 * @param employeeClassMap
	 */
	public void initEmployClass(Employee employee,Long classSettingId,User user,Date startDate,Date endDate,Map<Date,Integer> vacationMap,Map<Date,EmployeeClass> employeeClassMap,Long groupId);
	
	/**
	 * 
	 * @param month(yyyy-MM 月分)
	 */
	public void initAllEmployClass(String month);
	
	/**
	 * 上月月底+本月一号发送邮件通知班次不是9-18点的员工
	 */
	void sendMailToClassChangeEmp();

}
