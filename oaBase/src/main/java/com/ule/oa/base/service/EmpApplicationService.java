package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.base.po.EmpSchool;
import com.ule.oa.base.po.EmpTraining;
import com.ule.oa.base.po.EmpUrgentContact;
import com.ule.oa.base.po.EmpWorkRecord;
import com.ule.oa.base.po.Employee;

public interface EmpApplicationService {

	/**
	  * 提交申请时，检查是否存在待审核数据
	  * @Title: checkExistApply
	  * @param employee
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer checkExistApply(Employee employee);

	/**
	  * 员工资料修改申请
	  * @Title: apply
	  * @param employee    设定文件
	  * void    返回类型
	  * @throws
	 */
	void apply(Employee employee);

	/**
	  * 查看提交申请的资料主信息
	  * @Title: check
	  * @param employee    设定文件
	  * void    返回类型
	  * @throws
	 */
	Employee checkEmployeeMain(Employee employee);

	/**
	  * 查看提交申请的资料家庭成员信息
	  * @Title: checkEmployeeFamily
	  * @param employee
	  * @return    设定文件
	  * List<EmpFamilyMember>    返回类型
	  * @throws
	 */
	List<EmpFamilyMember> checkEmployeeFamily(Employee employee);

	/**
	  * 查看提交申请的紧急联系人信息
	  * @Title: checkEmployeeUrgent
	  * @param employee
	  * @return    设定文件
	  * List<EmpUrgentContact>    返回类型
	  * @throws
	 */
	List<EmpUrgentContact> checkEmployeeUrgent(Employee employee);

	/**
	  * 查看提交申请的教育信息
	  * @Title: checkEmployeeSchool
	  * @param employee
	  * @return    设定文件
	  * List<EmpSchool>    返回类型
	  * @throws
	 */
	List<EmpSchool> checkEmployeeSchool(Employee employee);

	/**
	  * 查看提交申请的培训信息
	  * @Title: checkEmployeeTraining
	  * @param employee
	  * @return    设定文件
	  * List<EmpTraining>    返回类型
	  * @throws
	 */
	List<EmpTraining> checkEmployeeTraining(Employee employee);

	/**
	  * 查看提交申请的教育信息
	  * @Title: checkEmployeeWork
	  * @Description: TODO
	  * @param employee
	  * @return    设定文件
	  * List<EmpWorkRecord>    返回类型
	  * @throws
	 */
	List<EmpWorkRecord> checkEmployeeWork(Employee employee);

	/**
<<<<<<< .mine
	 * @throws OaException 
=======
	 * @throws Exception 
>>>>>>> .r384504
	  * 资料申请审核通过
	  * @Title: pass
	  * @param employee    设定文件
	  * void    返回类型
	  * @throws
	 */
	void pass(Employee employee) throws Exception;

	/**
	  * 资料申请审核驳回
	  * @Title: reject
	  * @param employee    设定文件
	  * void    返回类型
	  * @throws
	 */
	void reject(Employee employee);

}
