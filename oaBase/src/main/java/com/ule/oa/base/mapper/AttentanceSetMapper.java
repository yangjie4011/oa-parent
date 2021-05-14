package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.AttentanceSetDTO;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * 考勤设置
 * @author yangjie
 *
 */
public interface AttentanceSetMapper extends OaSqlMapper{
	
	List<AttentanceSetDTO> getEmployeeList(AttentanceSetDTO param);
	
	Integer getEmployeeCount(AttentanceSetDTO param);
	
	AttentanceSetDTO getEmployeeClassInfo(@Param("employId")Long employId,@Param("startDate")Date startDate,@Param("endDate")Date endDate);

}
