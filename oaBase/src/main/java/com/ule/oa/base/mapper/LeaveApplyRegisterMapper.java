package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.LeaveApplyRegister;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * 休假登记
 * @author yangjie
 *
 */
public interface LeaveApplyRegisterMapper extends OaSqlMapper{
	
	 /**
	  * 保存登记单据
	  * @param data
	  */
	 void save(LeaveApplyRegister data);
	 
	 /**
	  * 按条件查询休假登记列表
	  * @param data
	  * @return
	  */
	 List<LeaveApplyRegister> getRegisterLeaveListByCondition(LeaveApplyRegister data);
	 
	 /**
	  * 按条件查询休假登记总数
	  * @param data
	  * @return
	  */
	 Integer getRegisterLeaveCountByCondition(LeaveApplyRegister data);
	 
}
