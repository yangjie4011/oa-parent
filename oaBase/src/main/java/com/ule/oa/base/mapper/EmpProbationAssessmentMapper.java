package com.ule.oa.base.mapper;

import com.ule.oa.base.po.EmpProbationAssessment;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface EmpProbationAssessmentMapper extends OaSqlMapper{

	EmpProbationAssessment getByEmpId(Long employeeId);

	void save(EmpProbationAssessment model);

	void update(EmpProbationAssessment model);

	EmpProbationAssessment getById(Long id);
	
}