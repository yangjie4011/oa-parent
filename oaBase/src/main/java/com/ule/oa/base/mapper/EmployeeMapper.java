package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.CompanyPositionSeq;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RequestParamQueryEmpCondition;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
  * @ClassName: EmployeeMapper
  * @Description: 员工个人信息
  * @author minsheng
  * @date 2017年5月9日 上午8:58:53
 */	
	public interface EmployeeMapper extends OaSqlMapper{

	void save(Employee employee);
	
	int updateById(Employee employee);
	
	int updateAutoCaLculateLeave(Employee employee);
	
	List<Employee> getListByCondition(Employee employee);

	int getListByConditionCount(Employee employee);
	
	List<Employee> getDepartEmpsList(Long id);
	
	List<Employee> getPageList(Employee employee);
	
	Integer getCount(Employee employee);

	List<Employee> getQuitPageList(Employee employee);
	
	Integer getQuitCount(Employee employee);
	
	
	/**
	 * 一级部门,查询该部门下所有部门的员工数
	 * @param departId
	 * @return
	 */
	int getEmpTotalByDepartId(@Param("departId")Long departId);
	
	/**
	 * 非一级部门,查询该部门的员工数 
	 * @param departId
	 * @return
	 */
	int getEmpCountByDepartId(@Param("departId")Long departId);
	
	List<Employee> getEmpsByDepart(String departName);
	
	Employee getLeaderById(@Param("id")Long id);
	
	List<Employee> getMLevalEmpsByDepart(Employee employee);
	
	int getMLevalEmpsCountByDepart(@Param("departId")Long departId);

	Integer getListCountForBoardroomDue(Employee employee);

	List<Employee> getListForBoardroomDue(Employee employee);

	Employee getForEmpSelfAssessmentById(Long id);
	
	/**
	 * 根据部门id查询该部门的M级别负责人（按照职位序列从高到底排序）
	 * @param departId
	 * @return
	 */
	List<Employee> getMLeaderByDepartId(@Param("departId")Long departId);
	
	
	Employee getById(Long id);
	
	//获取员工以及部门信息
	List<Employee> getByEmpId(Long id);
	
	/**
	 * 根据姓名或编号查询员工信息
	 * @param employee
	 * @return
	 */
	public List<Employee> getEmpListByCondition(Employee employee);
	
	/**
	 * 根据姓名或编号查询员工信息count
	 * @param employee
	 * @return
	 */
	Integer getEmpListByConditionCount(Employee employee);

	//获取考勤用户
	List<Employee> getAttnEmpListByCondition(Employee employee);
	
	/**
	 * 查询部门负责人
	 * @param departId
	 * @return
	 */
	Employee getLeaderByDepartId(Long departId);
	
	/**
	 * 查询行使权力人
	 * @param departId
	 * @return
	 */
	Employee getPowerByDepartId(Long departId);
	
	Integer getEmpListByNameCount(@Param("cnName")String cnName);
	
	Integer getEmpListLikeNameCount(@Param("cnName")String cnName);
	
	List<Employee> getByEmpIdList(@Param("list")List<Long> list);
	
	Integer getEmployyeeIdCount(@Param("employeeId")String employeeId);

	List<Employee> getAllMLevalEmps(Employee condition);
	
	List<Employee> getListByDepartId(@Param("departId")Long departId);
	
	List<Employee> getListByDepart(@Param("departId")Long departId);
	
	List<Employee> getReportList(Employee employee);
	
	int getReportListCount(Employee employee);
	
	int getReportListLeverCount(Employee employee);

	void updReporterBatch(Employee e);

	Map<String,Object> queryDepartHeadIdByEmpId(Long empId);

	Map<String, String> queryEmpBaseInfoById(Long empId);

	List<Employee> queryByNameOrCode(String nameOrCode);
	
	public Map<String,Object> getEmpByPhone(Employee employee);
	
	List<Employee> getDepartHeaderList();

	Long getDepartId(String departName);

	void saveDepart(Depart depart);

	Long getEmpTypeId(String empType);

	int queryEmpList(@Param("code")String code);

	void updateReporter(@Param("reportToLeaderCode")String reportToLeaderCode, @Param("code")String code);

	void updateDepartLeader(@Param("departLeaderCode")String departLeaderCode, @Param("departId")Long departId);

	void initPass(@Param("pingYin")String pingYin, @Param("empId")Long empId,@Param("userId")String userId);

	void initEmpAndDepart(@Param("empId")Long empId, @Param("departId")Long departId, @Param("userId")String userId);

	void initPosition(@Param("empId")Long empId, @Param("positionId")Long positionId, @Param("userId")String userId);

	String getMaxCode();

	Long getWorkTypeId(@Param("workType")String workType);

	Long getWhetherSchedulId(@Param("whetherScheduling")String whetherScheduling);

	Integer getEmpListByCodeCount(@Param("empCode")String empCode);

	Long getEmpIdByCode(@Param("code")String code);

	String getDepartMaxCode();

	Long getPositionSeqId(@Param("poistionSeq")String poistionSeq);

	Integer getPositionSeqMaxRank();

	void savePositionSeq(CompanyPositionSeq companyPositionSeq);

	Long getPositionIdByName(@Param("positionTitle")String positionTitle);

	Long getPositionLevelId(@Param("poistionLevel")String poistionLevel);

	Integer getPositionMaxRank();

	Integer getEmailCount(@Param("email")String email);
	
	//还原是否自动计算年假属性
	void resetAutoCalculateLeave();

	List<Employee> getImportEmployeeList(@Param("empCodeList")List<String> empCodeList);

	void updateReporterToLeader(@Param("empId")Long empId,@Param("reportToLeaderId")Long reportToLeaderId);
	
	//根据组别id查询员工信息
	List<Map<String,Object>> getEmployeeMapByGroupId(@Param("groupId")Long groupId,@Param("classMonth")Date classMonth);
	
	List<String> getAssigneeIdListByProcinstId(@Param("procinstId")String procinstId);
	
	//查询所有汇报对象
	List<Employee> getAllReportPerson();

	//根据工时类型汇报对象部门查询所有员工
	List<Employee> getAllEmpByWorkTypeAndLeaderAndDepart(RequestParamQueryEmpCondition requestParamQueryEmpByCondition);

	Integer getStandardEmpCountByCount(RequestParamQueryEmpCondition requestParamQueryEmpByCondition);
	
	//判断员工是否离职
	Integer isQuitThisDay(@Param("empId")Long empId, @Param("delayDate")Date delayDate);
	
	//查询多个汇报对象下的所有下属员工
	List<Employee> getByReportToLeaders(@Param("reportToLeaderList")List<Long> reportToLeaderList);
	
	//查询多个汇报对象某个部门下的所有下属员工
	List<Employee> getByReportToLeadersAndDepartId(@Param("employeeId") Long reportToLeader,@Param("departId")Long departId);
	
	/**
	 * 根据code查询员工详细信息
	 * @param code
	 * @return
	 */
	Employee getInfoByCode(@Param("code")String code);
	
	/**
	 * 根据员工编号前缀获取最大编号
	 * @param prefix
	 * @return
	 */
	Integer getMaxCodeByPrefix(@Param("prefix")String prefix);
	
	/**
	 * 根据中文名和出生日期查询员工列表获取识别号
	 * @param prefix
	 * @return
	 */
	List<Employee> getIdentificationNum(@Param("cnName")String cnName, @Param("birthDate")Date birthDate);
	
	/**
	 * 根据出生日期查询员工列表
	 * @param prefix
	 * @return
	 */
	List<Employee> getListByBirthDate( @Param("birthDate")Date birthDate);
	
	/**
	 * 根据出生日期相似查询员工识别号
	 * @param birthdayStr
	 * @return
	 */
	List<Employee> getEmpByBirthday(@Param("birthdayStr")String birthdayStr);
	
	//获取后台员工弹框部门树数据
	List<Map<String,Object>> getEmployeeTreeList();
	
	List<Map<String,Object>> getEmployeeSeqList();
	
	List<Employee> getAttnReportNeedList(@Param("monthStart") Date monthStart,@Param("empTypeIdList")List<Long> empTypeIdList);
	
	/**
	 * 根据编号批量查询
	 * @param codeList
	 * @return
	 */
	List<Employee> getListByCodes(@Param("codeList")List<String> codeList);
	
	/**
	 * 根据员工识别号获取员工数据
	 * @param identificationNum
	 * @return
	 */
	public List<Employee> getByIdentificationNum(@Param("identificationNum")String identificationNum);
	
}
