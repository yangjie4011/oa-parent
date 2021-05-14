package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpAchievement;
import com.ule.oa.base.po.EmpSchool;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface EmpAchievementMapper extends OaSqlMapper{
    int save(EmpAchievement empAchievement);

    EmpAchievement selectById(Long id);

    int updateById(EmpAchievement empAchievement);
    
    List<EmpAchievement> getListByCondition(EmpAchievement empAchievement);
    
    List<EmpAchievement> getListByEmployeeId(@Param("employeeId")Long employeeId);
    
    int saveBatch(List<EmpAchievement> empAchievements);
	
	int deleteByEmployeeId(@Param("employeeId")Long employeeId,@Param("updateUser")String updateUser,@Param("updateTime")Date updateTime);

}