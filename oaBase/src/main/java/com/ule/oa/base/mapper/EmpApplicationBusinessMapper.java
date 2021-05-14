package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 出差申请表
 * @Description: 出差申请表
 * @author yangjie
 * @date 2017年6月13日
 */
public interface EmpApplicationBusinessMapper extends OaSqlMapper {
	
    void save(EmpApplicationBusiness business);
	
	int updateById(EmpApplicationBusiness business);
	
	EmpApplicationBusiness getById(Long id);
	
	int getReportCount(EmpApplicationBusiness business);
	
	List<EmpApplicationBusiness> getReportPageList(EmpApplicationBusiness business);

	EmpApplicationBusiness queryByProcessId(String instanceId);

	EmpApplicationBusiness queryByReportProcessId(String instanceId);
	
	//查看是否有审批中的单据
	Integer isExist(@Param("id")Long id, @Param("employeeId")Long employeeId);
	
	//根据原始单据id查看修改单
	EmpApplicationBusiness getUpdateBill(@Param("originalBillId")long originalBillId);
	
	//查询未完成的出差报告单据
	List<EmpApplicationBusiness> getUnCompleteReportList();
	
	int getUserGroupListCount(EmpApplicationBusiness business);
	
	List<EmpApplicationBusiness> getUserGroupList(EmpApplicationBusiness business);
	
	int getDepartGroupListCount(EmpApplicationBusiness business);
	
	List<EmpApplicationBusiness> getDepartGroupList(EmpApplicationBusiness business);
	
	List<EmpApplicationBusiness> getYearGroupList(EmpApplicationBusiness business);
	
	List<EmpApplicationBusiness> getAdreessGroupList(EmpApplicationBusiness business);
	
	int myTaskListCount(EmpApplicationBusiness business);
	
	List<EmpApplicationBusiness> myTaskList(EmpApplicationBusiness business);

	List<EmpApplicationBusiness> getEmpPageList(
			EmpApplicationBusiness empApplicationBusiness);

	Integer getEmpPageListCount(EmpApplicationBusiness empApplicationBusiness);

	List<EmpApplicationBusiness> getDepartList(
			EmpApplicationBusiness empApplicationBusiness);

	Integer getDepartListCount(EmpApplicationBusiness empApplicationBusiness);

}
