package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.LeaveRecordDetail;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @author yangjie
 * 假期流水明细表
 */
public interface LeaveRecordDetailMapper extends OaSqlMapper{
	
	int save(LeaveRecordDetail recordDetail);
	
	int batchSave(List<LeaveRecordDetail> recordDetailList);
	
	List<LeaveRecordDetail> selectByCondition(LeaveRecordDetail recordDetail);
	
	int updateById(LeaveRecordDetail recordDetail);
	
	void deleteByCondition(LeaveRecordDetail recordDetail);

	List<LeaveRecordDetail> getByApplicationAbolishLeaveId(@Param("empApplicationleaveId")Long empApplicationleaveId);

}
