package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.tbl.DailyHeathSignTbl;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * 每日健康打卡
 * @author yangjie
 *
 */
public interface DailyHeathSignMapper extends OaSqlMapper{
	
	/**
	 * 批量插入
	 * @param list
	 * @return
	 */
	int batchSave(List<DailyHeathSignTbl> list);
	
	/**
	 * 根据条件查询登记记录
	 * @param employeeId
	 * @param type
	 * @param signDate
	 * @return
	 */
	List<DailyHeathSignTbl> getByEmployeeIdAndSignDate(@Param("employeeId")Long employeeId,@Param("type")Integer type,@Param("signDate")Date signDate);
	
	List<Long> getSignEmployeeIdByDate(@Param("signDate")Date signDate);
	
	List<Long> getSignEmployeeIdByDateAndDepart(@Param("signDate")Date signDate,@Param("departId")Long departId);
	
	List<Long> getNeedNoticeLeader(@Param("departId")Long departId);
	
    List<Map<String,Object>> getBaseInfo();
    
    List<DailyHeathSignTbl> getAnswerByTypeAndSignDate(@Param("employeeIdList")List<Long> employeeIdList,@Param("type")Integer type,
    		@Param("signDate")Date signDate);
	
	

}
