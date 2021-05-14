package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmployeeBaseInfoDTO;
import com.ule.oa.base.po.EmployeeBaseInfoParam;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * 员工基本信息，主要供报表，页面查询表头固定值部分使用
 * @author yangjie
 *
 */
public interface EmployeeBaseInfoMapper extends OaSqlMapper{
	
	/**
	 * 员工考勤明细-固定表头
	 * @param startTime
	 * @param endTime
	 * @param departId
	 * @param empTypeIdList
	 * @return
	 */
	List<EmployeeBaseInfoDTO> getYGKQMXReport(@Param("startTime")Date startTime, @Param("endTime")Date endTime,@Param("departId")Long departId,
			@Param("empTypeIdList")List<Long> empTypeIdList);
	
	List<EmployeeBaseInfoDTO> getBaseInfoList(EmployeeBaseInfoParam param);

}
