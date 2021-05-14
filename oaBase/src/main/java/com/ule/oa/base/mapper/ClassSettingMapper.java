package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.util.jdbc.OaSqlMapper;


public interface ClassSettingMapper extends OaSqlMapper{
	public List<ClassSetting> getList();
	public Integer save(ClassSetting classSetting);
	public Integer updateById(ClassSetting classSetting);
	public Integer getCount(ClassSetting classSetting);
	public List<ClassSetting> getListByCondition(ClassSetting classSetting);
	//获取员工指定日期排班
	public ClassSetting getByEmpAndDate(ApplicationEmployeeClassDetail classDetail);
	
	public List<ClassSetting> getByIds(List<Long> ids);
	
	public ClassSetting getById(Long id);
	
	//获取员工指定日期排班
	public ClassSetting getByEmpAndDate1(ApplicationEmployeeClassDetail classDetail);
	
	//查询后台班次 不加delflag条件
    public Integer getPageCount(ClassSetting classSetting);
	
    public List<ClassSetting> getPageListByCondition(ClassSetting classSetting);
	
    public Long getIdByName(@Param("name")String name);
    
    public ClassSetting getByName(@Param("name")String name);
}