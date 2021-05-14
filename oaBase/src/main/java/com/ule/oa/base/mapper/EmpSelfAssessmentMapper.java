package com.ule.oa.base.mapper;

import com.ule.oa.base.po.EmpSelfAssessment;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

public interface EmpSelfAssessmentMapper extends OaSqlMapper{

	Long save(EmpSelfAssessment model);

	EmpSelfAssessment getByEmpId(Long employeeId);

	Long update(EmpSelfAssessment model);

	EmpSelfAssessment getById(Long id);

}