package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpApplicationBusinessDetail;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 出差申请明细表
 * @Description: 出差申请明细表
 * @author yangjie
 * @date 2017年6月13日
 */
public interface EmpApplicationBusinessDetailMapper extends OaSqlMapper {
	
	void batchSave(List<EmpApplicationBusinessDetail> list);
	
	List<EmpApplicationBusinessDetail> getListByCondition(@Param("businessId")Long businessId);
	
    int save(EmpApplicationBusinessDetail businessDetail);
	
	int updateById(EmpApplicationBusinessDetail businessDetail);
	
	int delete(EmpApplicationBusinessDetail businessDetail);

}
