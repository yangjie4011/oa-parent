package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RequestParamQueryEmpCondition;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: EmployeeService
  * @Description: 员工个人信息业务层
  * @author minsheng
  * @date 2017年5月9日 上午8:59:38
 */
public interface EmployeeService {
	/**
	  * save(保存个人信息)
	  * @Title: save
	  * @Description: 保存个人信息
	  * @param employee    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void save(Employee employee);
	
	/**
	  * updateById(更新个人信息)
	  * @Title: updateById
	  * @Description: 更新个人信息
	  * @param employee
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(Employee employee);
	
	/**
	  * getListByCondition(根据查询条件查询所有的员工个人信息)
	  * @Title: getListByCondition
	  * @Description: 根据查询条件查询所有的员工个人信息
	  * @param employee
	  * @return    设定文件
	  * List<Employee>    返回类型
	  * @throws
	 */
	public List<Employee> getListByCondition(Employee employee);
	
	int getListByConditionCount(Employee employee);
	
	/**
	  * getPageList(分页查询员工信息)
	  * @Title: getPageList
	  * @Description: 分页查询员工信息
	  * @param employee
	  * @return    设定文件
	  * PageModel<Employee>    返回类型
	  * @throws
	 */
	public PageModel<Employee> getPageList(Employee employee);
	
	/**
	  * getSelectDivList()
	  * @Title: getSelectDivList
	  * @Description: 获取后台员工选择弹框数据
	  * @param employee
	  * @return    设定文件
	  * PageModel<Employee>    返回类型
	  * @throws
	 */
	public PageModel<Employee> getSelectDivList(Employee employee);
	
	
	/**
	  * getPageList(分页查询离职员工信息)
	  * @Title: getPageList
	  * @Description: 分页查询离职员工信息
	  * @param employee
	  * @return    设定文件
	  * PageModel<Employee>    返回类型
	  * @throws
	 */
	public PageModel<Employee> getQuitPageList(Employee employee);
	
	/**
	 * @throws OaException 
	  * getCurrentEmployeeId(获取当前登录员工id)
	  * @Title: getCurrentEmployeeId
	  * @Description: 获取当前登录员工id
	  * @return    设定文件
	  * Long    返回类型
	  * @throws
	 */
	public Long getCurrentEmployeeId() throws OaException;
	
	/**
	  * getCurrentEmployee(获取当前登录员工信息)
	  * @Title: getCurrentEmployee
	  * @Description: 获取当前登录员工信息
	  * @return    设定文件
	  * Employee    返回类型
	  * @throws
	 */
	public Employee getCurrentEmployee() throws OaException;
	
	/**
	  * getByCondition(根据条件查询员工信息)
	  * @Title: getByCondition
	  * @Description: 根据条件查询员工信息
	  * @return    设定文件
	  * Long    返回类型
	  * @throws
	 */
	public Employee getByCondition(Employee employee);
	
	/**
	 * 一级部门,查询该部门下所有部门的员工数
	 * @param departId
	 * @return
	 */
	int getEmpCountByDepartId(Long departId);
	
	/**
	 * 非一级部门,查询该部门的员工数 
	 * @param departId
	 * @return
	 */
	int getEmpTotalByDepartId(Long departId);
	
	/**
	 * 
	  * getDepartEmpsList(根据部门ID获取部门员工列表)
	  * @Title: getDepartEmpsList
	  * @param id
	  * @return    设定文件
	  * List<Employee>    返回类型
	  * @throws
	 */
	public List<Employee> getDepartEmpsList(Long id);
	
	/**
	 * 根据部门id查询该部门下的所有员工的姓名,职称等信息
	 * @param departName
	 * @return
	 */
	List<Employee> getEmpsByDepart(String departId);
	
	/**
	 * 获取部门负责人姓名和职位
	 * @param id
	 * @return
	 */
	Employee getLeaderById(Long id);
	
	/**
	 * 根据部门获取所有汇报对象
	 * @param employee
	 * @return
	 */
	List<Employee> getReportPerson(Employee employee);
	
	/**
	  * getEmpDetailByCondition(获取员工相关详细信息)
	  * @Title: getEmpDetailByCondition
	  * @Description: 获取员工相关详细信息
	  * @param employee
	  * @return    设定文件
	  * Employee    返回类型
	  * @throws
	 */
	public Employee getEmpDetailByCondition(Employee employee);

	/**
	 * 
	  * getListForBoardroomDue(会议预定页面获取员工列表)
	  * @Title: getListForBoardroomDue
	  * @param employee
	  * @return    设定文件
	  * PageModel<Employee>    返回类型
	  * @throws
	 */
	public PageModel<Employee> getListForBoardroomDue(Employee employee);

	/**
	 * 
	  * getForEmpSelfAssessmentById(通过ID为员工自我评估页面获取员工信息)
	  * @Title: getForEmpSelfAssessmentById
	  * @Description: 通过ID为员工自我评估页面获取员工信息
	  * @param id
	  * @return    设定文件
	  * Employee    返回类型
	  * @throws
	 */
	public Employee getForEmpSelfAssessmentById(Long id);
	
	/**
	 * 根据部门id查询该部门的M级别负责人（按照职位序列从高到底排序）
	 * @param departId
	 * @return
	 */
	List<Employee> getMLeaderByDepartId(Long departId);
	
	/**
	  * getById(根据主键查询员工信息)
	  * @Title: getById
	  * @Description: 根据主键查询员工信息
	  * @param id
	  * @return    设定文件
	  * Employee    返回类型
	  * @throws
	 */
	Employee getById(Long id);

	/**
	 * @throws Exception 
	  * saveEmployeeInfo(保存员工基本信息)
	  * @Title: saveEmployeeInfo
	  * @Description: 保存员工基本信息
	  * @param file
	  * @param employee    设定文件
	  * void    返回类型
	  * @throws
	 */
	void saveEmployeeInfo(CommonsMultipartFile file, Employee employee) throws Exception;
	
	/**
	  * saveEmployeeBase(保存员工基础信息)
	  * @Title: saveEmployeeBase
	  * @Description: 保存员工基础信息
	  * @param file
	  * @param employee
	  * @throws Exception    设定文件
	  * @see com.ule.oa.base.service.EmployeeService#saveEmployeeInfo(org.springframework.web.multipart.commons.CommonsMultipartFile, com.ule.oa.base.po.Employee)
	  * @throws
	 */
	public void saveEmployeeBase(Employee employee) throws Exception;
	
	public List<Employee> getByEmpId(Long id);

	/**
	 * 根据姓名或编号查询员工信息
	 * @param employee
	 * @return
	 */
	PageModel<Employee> getEmpListByCondition(Employee employee);
	
	/**
	  * 获取考勤用户
	  * @Title: getAttnEmpListByCondition
	  * @param employee
	  * @return    设定文件
	  * List<Employee>    返回类型
	  * @throws
	 */
	List<Employee> getAttnEmpListByCondition(Employee employee);
	
	//新员工入职校验是否有同名的存在
    Integer getEmpListByNameCount(String cnName);
    //新员工入职校验是否有同名的存在
	Integer getEmpListLikeNameCount(String cnName);
	//流程审批，查看页面职位信息
	List<Employee> getByEmpIdList(List<Long> list);
	
	//查询员工信息（包括部门，职位信息）
	List<Employee> getByIds(List<Long> ids);
	
	//查询员工信息（不包括部门，职位信息）
	List<Employee> getListByIds(List<Long> ids);

	/**
	  * 获取汇报对象
	  * @Title: getReportPersonList
	  * @param id
	  * @return    设定文件
	  * List<Employee>    返回类型
	  * @throws
	 */
	public List<Employee> getReportPersonList(Long id);
	
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
	public Employee getEmpMobileByUserName(String userName) throws OaException;
	
	public void getDepartName(List<Employee> employees);

	public HSSFWorkbook exportEmployeeList(Employee employee);

	/**
	 * 批量修改汇报对象
	 * @param reporterId
	 * @param employeeIds
	 * @throws OaException 
	 */
	public void updReporterBatch(Long reporterId, String employeeIds) throws OaException;
	
	/**
	 * 逻辑删除 入职七天以内的员工 del_flag=1
	 * @param reporterId
	 * @param employeeIds
	 * @throws OaException 
	 */
	public void deleteEmp(int id);
	/**
	 * 员工离职 信息修改
	 * @param reporterId
	 * @param employeeIds
	 * @throws Exception 
	 * @throws OaException 
	 */
	public Map<String,Object> updateEmpQuitInfo(Employee employee) throws Exception;
	
	public Map<String,Object> updateEmpFingerprintId(Employee employee) throws Exception;

	public Employee queryEmpInfoById(Employee employee);

	public Map<String,Object> getEmpByInfo(Employee employee);

	
	/**
	 * 根据员工id查询部门负责人id
	 * @param empId
	 * @return
	 */
	public Map<String,Object> queryDepartHeadIdByEmpId(Long empId);
	
	
	public boolean isLeaderByEmpId(Long empId);
	
	/**
	 * 根据员工id查询基本信息
	 * @param valueOf
	 * @return
	 */
	public Map<String, String> queryEmpBaseInfoById(Long empId);
	
	/**
	 * 判断员工是否是PM部门
	 * @param proposerId
	 * @return
	 */
	public boolean isPMDepart(Long proposerId);
	
	/**
	 * 判断员工是否是技术开发部
	 * @param proposerId
	 * @return
	 */
	public boolean isTDDepart(Long proposerId);

	public List<Employee> queryByNameOrCode(String nameOrCode);

	Map<String, Object> updateQuitYearinfoSendMail(Employee listEmp) throws Exception;

	HSSFWorkbook exportQuitEmployeeList(Employee employee);

	List<EmpPosition> sendMailPositionName();
	
	List<Employee> getDepartHeaderList();

	public Map<String, Object> importEmployee(MultipartFile file) throws OaException,Exception;

	public void updateEmpReportLeader(Map<Long, String> updateEmpMap);

	public PageModel<Employee> getDeptList(Employee employee);
	
	public boolean hasPowerSuperior(Long employeeId);
	
	//查询待办任务当前节点所有待办人
	List<String> getAssigneeIdListByProcinstId(String procinstId);

	public Map<String, Object> updateEmpDepart(Employee employee) throws Exception;

	public List<Employee> getAllReportPerson();

	public Map<String, Object> saveReportPerson(String empIds, String employeeLeader) throws OaException;
	
	//根据工时类型汇报对象部门查询所有员工
	public List<Employee> getAllEmpByWorkTypeAndLeaderAndDepart(RequestParamQueryEmpCondition requestQueryEmpByConditionParam);
	
	//计算满足条件的标准工时的员工总数
	public Integer getStandardEmpCountByCount(RequestParamQueryEmpCondition requestParamQueryEmpByCondition);
	
	//判断员工是否离职
	public Integer isQuitThisDay(Long empId, Date delayDate);
	
	//获取指定员工所有下属列表
	public List<Long> getSubEmployeeList(Long employeeId);
	
	//获取指定员工某个部门所有下属列表
	public List<Long> getSubEmployeeList(Long employeeId,Long departId,boolean isGetAll);
	
	/**
	  * getInfoByCode(根据code员工信息)
	  * @Title: getInfoByCode
	  * @Description: 根据code员工信息
	  * @param code
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> getInfoByCode(String code);
	
	/**
	  * getInfoByCode(根据code员工信息)
	  * @Title: getInfoByCode
	  * @Description: 根据code员工信息
	  * @param code
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Employee getEmployeeByCode(String code);
	
	/**
	  * getIdentificationNum(根据名字和出生日期生成识别号)
	  * @Title: getIdentificationNum
	  * @Description: 根据名字和出生日期生成识别号
	  * @param cnName
	  * @param birthDate
	  * @return   设定文件
	  * String    返回类型
	  * @throws
	 */
	public Map<String,Object> getIdentificationNum(String cnName,Date birthDate);
	
	public List<Map<String,Object>> getEmployeeSeqList();
	
	/**
	 * 获取最大员工编号
	 * @param prefix 前缀
	 * @return
	 */
	public String getMaxCode(String prefix);
	
	/**
	 * 根据编号批量查询
	 * @param codeList
	 * @return
	 */
	List<Employee> getListByCodes(List<String> codeList);
	
	/**
	 * 根据员工识别号获取员工信息
	 */
	List<Employee> getByIdentificationNum(String identificationNum);
}
