package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: 每年假期表
 * @Description:每年假期表
 * @author yangjie
 * @date 2017年6月16日
 */
public interface AnnualVacationMapper extends OaSqlMapper {
	
	List<AnnualVacation> getListByCondition(AnnualVacation vacation);
	
	List<AnnualVacation> getList();

	AnnualVacation getVacationByDate(@Param("annualDate")Date annualDate);

	void save(AnnualVacation annualVacation);

	void updateById(AnnualVacation annualVacation);

	List<Map<String,Object>> getAnnualDateByYearAndSubject(@Param("year")String year, @Param("vacation")String vacation);

	List<Date> getAnnualDateByCondition(AnnualVacation vacation);
	
	List<AnnualVacation> getListByPage(AnnualVacation vacation);
	
	Integer getCount(AnnualVacation vacation);

}
