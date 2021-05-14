package com.ule.oa.base.mapper;

import com.ule.oa.base.po.EmpMidtermAssessment;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface EmpMidtermAssessmentMapper extends OaSqlMapper{

	EmpMidtermAssessment getByEmpId(Long employeeId);

	int save(EmpMidtermAssessment model);

	int update(EmpMidtermAssessment model);

	EmpMidtermAssessment getById(Long id);
}