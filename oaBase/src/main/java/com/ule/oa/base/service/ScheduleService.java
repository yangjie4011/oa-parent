package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

public interface ScheduleService {
  
	List<Map<String, String>> getTreeList(Depart depart);

	List<ScheduleGroup> getGroupListByDepartId(Long departId);

	List<ScheduleGroup> getGroupInfoByGroupId(Long groupId);

	void saveScheduleGroup(ScheduleGroup group) throws OaException;

	List<Employee> getAllGroupEmp(Long groupId, String condition);

	List<Employee> getUngroupedEmp(Employee emp);

	void addMember(String empList, Long groupId) throws OaException;

	void delMember(Long empId, Long groupId) throws OaException;

	List<Depart> getScheduleDepartList();
	
	//根据排班人查询排班组别列表
	List<ScheduleGroup> getListByScheduler(Long scheduler);

	HSSFWorkbook exportScheduleTemplate(Long departId, Long groupId);

	Map<String, String> importScheduleTemplate(MultipartFile file, Long departId, Long groupId) throws OaException, Exception;

	List<ScheduleGroup> getScheduleList();
	
	void deleteScheduleGroup(Long groupId) throws OaException;
	PageModel<Employee> getEmpClassListByCondition(Employee empClass);
	/**
	 * 修改部门是否需要排班属性
	 */
	int updateDepartWhetherScheduling(Depart depart);

	//修改排班属性 如果为否 则删除排班组
	void updateEmpClassInfo(String isWhetherScheduling, String empClassIds);
	
	/**
	 * 调班模板下载
	 * @param month
	 * @param departId
	 * @param groupId
	 * @return
	 */
	HSSFWorkbook exportChangeClassTemplate(String month,Long departId, Long groupId);
	
	/**
	 * 导入调班
	 * @param file
	 * @param departId
	 * @param groupId
	 * @return
	 * @throws OaException
	 * @throws Exception
	 */
	Map<String, String> importChangeClassTemplate(MultipartFile file,String month,Long departId, Long groupId) throws OaException, Exception;



}
