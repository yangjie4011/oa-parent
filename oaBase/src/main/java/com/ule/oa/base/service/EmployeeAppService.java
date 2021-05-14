package com.ule.oa.base.service;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;


/**
  * @ClassName: EmployeeService
  * @Description: 员工个人信息业务层
  * @author minsheng
  * @date 2017年5月9日 上午8:59:38
 */
public interface EmployeeAppService {
	/**
	  * getById(根据员工表主键获取员工信息)
	  * @Title: getById
	  * @Description: 根据员工表主键获取员工信息
	  * @param id
	  * @return    设定文件
	  * Employee    返回类型
	  * @throws
	 */
	public EmployeeApp getById(Long id);
	
	/**
	  * getPageList(分页查询员工信息)
	  * @Title: getPageList
	  * @Description: 分页查询员工信息
	  * @param employee
	  * @return    设定文件
	  * PageModel<Employee>    返回类型
	  * @throws
	 */
	public PageModel<EmployeeApp> getPageList(EmployeeApp employee);
	
	/**
	  * getCurrentEmployeeId(获取当前登录员工id)
	  * @Title: getCurrentEmployeeId
	  * @Description: 获取当前登录员工id
	  * @return    设定文件
	  * Long    返回类型
	  * @throws
	 */
	public Long getCurrentEmployeeId();
	
	/**
	  * getCurrEmp(查看当前员工信息)
	  * @Title: getCurrEmp
	  * @Description: 查看当前员工信息
	  * @return    设定文件
	  * @throws
	 */
	public EmployeeApp getCurrEmp();
	
	/**
	  * getEmpInfo(获取员工详细信息)
	  * @Title: getEmpInfo
	  * @Description: 获取员工详细信息
	  * @param employee
	  * @return    设定文件
	  * EmployeeApp    返回类型
	  * @throws
	 */
	public EmployeeApp getEmpInfo(EmployeeApp employee);
	
	/**
	 * @throws Exception 
	  * updateEmpBaseInfo(更新员工基本信息)
	  * @Title: updateEmpBaseInfo
	  * @Description: 更新员工基本信息
	  * @param employee
	  * @return    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	public boolean updateEmpBaseInfo(EmployeeApp employee) throws Exception;
	
	/**
	  * updateEmpInfo(更新员工基本信息-不带事务)
	  * @Title: updateEmpInfo
	  * @Description: 更新员工基本信息-不带事务
	  * @param employee
	  * @return
	  * @throws Exception    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	public boolean updateEmpInfo(EmployeeApp employee) throws Exception;
	
	/**
	 * @return 
	  * uploadPic(上传员工照片)
	  * @Title: uploadPic
	  * @Description: 上传员工照片
	  * @param file
	  * @param employee
	  * @throws Exception    设定文件
	  * void    返回类型
	  * @throws
	 */
	public String uploadPic(CommonsMultipartFile file, EmployeeApp employee) throws Exception;
	
	/**
	  * getListByCondition(根据条件获取所有员工信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有员工信息
	  * @param employee
	  * @return    设定文件
	  * List<EmployeeApp>    返回类型
	  * @throws
	 */
	public List<EmployeeApp> getListByCondition(EmployeeApp employee);
	
	/**
	  * getEmpMobileByUserName(根据用户名获取员工电话)
	  * @Title: getEmpMobileByUserName
	  * @Description: 根据用户名获取员工电话
	  * @param userName
	  * @return
	  * @throws OaException    设定文件
	  * EmployeeApp    返回类型
	  * @throws
	 */
	public EmployeeApp getEmpMobileByUserName(String userName) throws OaException;
	
	/**
	  * updateAllEmpOurAge(更新所有员工的司龄)
	  * @Title: updateAllEmpOurAge
	  * @Description: 更新所有员工的司龄
	  * @return
	  * @throws OaException    设定文件
	  * EmployeeApp    返回类型
	  * @throws
	 */
	public int updateAllEmpOurAge(int days) throws OaException;
	
	/**
	  * exportExcel(导出excel)
	  * @Title: exportExcel
	  * @Description: 导出excel
	  * @return    设定文件
	  * HSSFWorkbook    返回类型
	  * @throws
	 */
	public HSSFWorkbook exportExcel();
	
	/**
	  * getDepartName(根据部门id获取一级部门+当前部门)
	  * @Title: getDepartName
	  * @Description: 根据部门id获取一级部门+当前部门
	  * @param departId
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	public String getDepartName(Long departId);
}
