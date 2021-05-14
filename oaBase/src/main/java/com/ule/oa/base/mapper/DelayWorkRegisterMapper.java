package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.DelayWorkRegister;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface DelayWorkRegisterMapper extends OaSqlMapper {
	
	//根据月份查询该员工所有延时工作登记
	List<DelayWorkRegister> getDelayWorkRegisterByMonth(@Param("employeeId")Long employeeId, @Param("fristDay")Date fristDay, @Param("lastDay")Date lastDay);
	
	//查询当前时间以前所有未匹配考勤的登记数据
	List<DelayWorkRegister> getUnMatchedListByDelayDate(@Param("delayDate")Date delayDate);
	
	//匹配实际考勤
	int matchActaulTime(DelayWorkRegister match);
	
	//根据员工id与日期查询延时工作记录
	DelayWorkRegister getDelayWorkDetail(@Param("employeeId")Long employeeId, @Param("delayDate")Date delayDate);
	
	//初始登记
	void insertDelayWorkDetail(DelayWorkRegister delayWorkRegister);
	
	//修改登记
	void updateDelayWorkDetail(DelayWorkRegister delayWorkRegister);

	//确认登记
	void confirmDelayWorkDetail(DelayWorkRegister delayWorkRegister);
	
	//删除登记
	void deleteDelayWorkDetail(DelayWorkRegister delayWorkRegister);
	
	//根据id查询延时工作详情
	DelayWorkRegister getById(@Param("id")Long id);
	
	List<DelayWorkRegister> getListByDate(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

}
