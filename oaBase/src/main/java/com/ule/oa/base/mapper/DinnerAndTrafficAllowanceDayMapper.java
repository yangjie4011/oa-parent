package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.tbl.DinnerAndTrafficAllowanceDayTbl;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * 餐补与交通补贴月度总结
 * @author yangjie
 *
 */
public interface DinnerAndTrafficAllowanceDayMapper extends OaSqlMapper{
	
	int batchSave(List<DinnerAndTrafficAllowanceDayTbl> list);
	
	int deleteByReportId(@Param("reportId")Long reportId,@Param("updateUser")String updateUser);

}
