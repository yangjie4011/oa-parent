package com.ule.oa.base.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpHandoverWork;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 离职工作交接
 * @Description: 离职工作交接
 * @author yangjie
 * @date 2017年5月31日
 */

public interface EmpHandoverWorkMapper  extends OaSqlMapper{
	
    void save(EmpHandoverWork empHandoverWork);
	
	int updateById(EmpHandoverWork empHandoverWork);
	
	EmpHandoverWork getById(@Param("id")Long id);
	
	void batchSave(List<EmpHandoverWork> list);
	
	List<EmpHandoverWork> getListByCondition(EmpHandoverWork empHandoverWork);
	
	int delete(EmpHandoverWork empHandoverWork);

}
