package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
  * @ClassName: EmployeeMapper
  * @Description: 员工个人信息
  * @author minsheng
  * @date 2017年5月9日 上午8:58:53
 */
public interface EmployeeAppMapper extends OaSqlMapper{
	EmployeeApp getById(Long id);
	List<EmployeeApp> getPageList(EmployeeApp employee);
	Integer getCount(EmployeeApp employee);
	int updateById(EmployeeApp employee);
	List<EmployeeApp> getListByCondition(EmployeeApp employee);
	int updateAllEmpOurAge(@Param("days")int days);
	int updateEmpJobStatus();
	int updateEmpJobStatusById(@Param("id")Long id);
	List<EmployeeApp> getInitEmpList(@Param("empTypeIdList")List<Long> empTypeIdList);
}