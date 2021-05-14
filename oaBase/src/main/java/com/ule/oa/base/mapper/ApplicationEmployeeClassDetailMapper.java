package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.util.jdbc.OaSqlMapper;


/**
 * @ClassName: 排班申请明细表
 * @Description: 排班申请明细表
 * @author yangjie
 * @date 2017年8月30日
 */
public interface ApplicationEmployeeClassDetailMapper extends OaSqlMapper{

	List<ApplicationEmployeeClassDetail> getApplicationEmployeeClassList(ApplicationEmployeeClassDetail condition);
	
	Integer save(ApplicationEmployeeClassDetail applicationEmployeeClass);
	
	Integer batchSave(List<ApplicationEmployeeClassDetail> list);
	
	List<ApplicationEmployeeClassDetail> selectByCondition(ApplicationEmployeeClassDetail classDetail);
	
	Integer updateByCondition(ApplicationEmployeeClassDetail classDetail);
	
	Integer checkEmployeeisClassSet(ApplicationEmployeeClassDetail classDetail);
	
	List<ApplicationEmployeeClassDetail> getEmployeeClassHours(ApplicationEmployeeClassDetail condition);
	//获取员工一个月每天的班次
	List<ApplicationEmployeeClassDetail> getEmployeeClassSetByMonth(ApplicationEmployeeClassDetail condition);
	
	void deleteByEmployeeClassId(ApplicationEmployeeClassDetail condition);
	
	List<ApplicationEmployeeClassDetail> getAreadyExsitList(ApplicationEmployeeClassDetail classDetail);
	
	int updateById(ApplicationEmployeeClassDetail classDetail);
	
	int updateSetIdById(ApplicationEmployeeClassDetail classDetail);
	
	Integer checkEmployeeisNew(ApplicationEmployeeClassDetail classDetail);
	
	List<ApplicationEmployeeClassDetail> getclassEmployeeNum(@Param("attnApplicationEmployClassId")Long attnApplicationEmployClassId);
	
	List<ApplicationEmployeeClassDetail> getEmployeeClassHoursByDates(ApplicationEmployeeClassDetail condition);
	
	int deleteByQuitTime(ApplicationEmployeeClassDetail condition);
	
	List<ApplicationEmployeeClassDetail> getEmployeeById(ApplicationEmployeeClassDetail condition);

}
