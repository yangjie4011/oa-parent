package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.TransNormal;
import com.ule.oa.base.util.jdbc.OaSqlMapper;


public interface AttnSignRecordMapper extends OaSqlMapper{

	/**
	  * 得到记录最大的考勤员工ID
	  * @Title: getMaxAttnId
	  * @Description: TODO
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer getMaxAttnId(@Param("startTime") Date startTime);

	/**
	  * saveTransToAttnSignBatch数据迁移
	  * @Title: saveTransToAttnSignBatch
	  * @param list
	  * @param uleId
	  * @param currentTime
	  * @param createUser
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	Integer saveTransToAttnSignBatch(@Param("list")List<TransNormal> list,@Param("uleId")Long uleId,
			@Param("currentTime") Date currentTime,@Param("createUser")String createUser);
	
	List<AttnSignRecord> getSignRecordList(AttnSignRecord condition);
	
	//获取9点前的打卡记录
	List<AttnSignRecord> getListBefore9(AttnSignRecord condition);
	
	int insert(AttnSignRecord signRecord);

	List<Map<String, Object>> getEmpSignRecordReport(@Param("startTime")Date startTime, @Param("endTime")Date endTime,@Param("departId")Long departId,@Param("empTypeIdList")List<Long> empTypeIdList);
    
	/**
	 * 实时打卡明细报表（优化-单表查询）
	 * @param startTime
	 * @param endTime
	 * @param employeeIdList
	 * @return
	 */
	List<AttnSignRecord> getSSDKMXReport(@Param("startTime")Date startTime, @Param("endTime")Date endTime,@Param("employeeIdList")List<Long> employeeIdList);
	
	/**
	  * getFirstSignRecordByCondition(根据条件获得员工最早的一次打卡时间)
	  * @Title: getFirstSignRecordByCondition
	  * @Description: 根据条件获得员工最早的一次打卡时间
	  * @param condition
	  * @return    设定文件
	  * AttnSignRecord    返回类型
	  * @throws
	 */
	AttnSignRecord getFirstSignRecordByCondition(AttnSignRecord attnSignRecord);
	
	/**
	  * getAbsenteeismWhiteList(获取过滤（期间含假期，异常考勤，出差）的白名单)
	  * @Title: getAbsenteeismWhiteList
	  * @Description: 获取过滤（期间含假期，异常考勤，出差）的白名单
	  * @param condition
	  * @return    设定文件
	  * List   返回类型
	  * @throws
	 */
	List<Long> getAbsenteeismWhiteList(@Param("startTime")Date startTime, @Param("endTime")Date endTime);
	/**
	  * getAbsenteeismWhiteList(获取过滤（期间含假期，异常考勤，出差）的白名单)
	  * @Title: getAbsenteeismWhiteList
	  * @Description: 获取过滤（期间含假期，异常考勤，出差）的白名单
	  * @param condition
	  * @return    设定文件
	  * List   返回类型
	  * @throws
	 */
	AttnSignRecord queryAlertMaxTime(@Param("startTime")Date startTime, @Param("employeeId")Long employeeId);
	
	/**
	  * getADepartmentLeaders(获取部门领导的id)
	  * @Title: getADepartmentLeaders
	  * @Description: 获取部门领导的id
	  * @param condition
	  * @return    设定文件
	  * List   返回类型
	  * @throws
	 */
	List<Long> getADepartmentLeaders();
	
	/**
	  * getAttnThirdAbsenteeismAlertEmp(获取三天内是否打卡 旷工)
	  * @Title: getAttnThirdAbsenteeismAlertEmp
	  * @Description: 根据条件获得员工最早的一次打卡时间
	  * @param condition
	  * @return    设定文件
	  * AttnSignRecord    返回类型
	  * @throws
	 */
	List<Long> getAttnThirdAbsenteeismAlertEmp(AttnSignRecord attnSignRecord);
	
	
	/**
	  * getAttnLastDayAbsenteeismAlertEmp(近段时间 获得员工最后的一次打卡时间)
	  * @Title: getAttnLastDayAbsenteeismAlertEmp
	  * @Description: 近段时间 获得员工最后的一次打卡时间
	  * @param condition
	  * @return    设定文件
	  * AttnSignRecord    返回类型
	  * @throws
	 */
	AttnSignRecord getAttnLastDayAbsenteeismAlertEmp(AttnSignRecord attnSignRecord);
}