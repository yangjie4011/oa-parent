package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 请假申请明细
 * @Description: 请假申请明细
 * @author yangjie
 * @date 2017年6月14日
 */
public interface EmpApplicationLeaveDetailMapper extends OaSqlMapper {
	
    void batchSave(List<EmpApplicationLeaveDetail> list);
	
	List<EmpApplicationLeaveDetail> getListByCondition(@Param("leaveId")Long leaveId);
	
    int save(EmpApplicationLeaveDetail leaveDetail);
	
	int updateById(EmpApplicationLeaveDetail leaveDetail);
	
	int updateByLeaveId(EmpApplicationLeaveDetail leaveDetail);
	
	int delete(EmpApplicationLeaveDetail leaveDetail);
	
	List<EmpApplicationLeaveDetail> getListByEmployee(EmpApplicationLeaveDetail leaveDetail);

	Integer continuityDate(EmpApplicationLeaveDetail leaveDetail);
	
	List<Map<String,Object>> getOtherReprt(EmpApplicationLeave empApplicationLeave);
}
