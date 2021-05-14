package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.DepartPosition;
import com.ule.oa.base.util.jdbc.OaSqlMapper;


public interface DepartPositionMapper extends OaSqlMapper{
	int save(DepartPosition departPosition);

	int updateById(DepartPosition departPosition);
	
	List<DepartPosition> getListByCondition(DepartPosition departPosition);
	
	int delByPositionId(Long positionId);
	
	int delByDepartId(@Param("departId")Long departId,@Param("updateTime")Date updateTime,@Param("updateUser")String updateUser);
}