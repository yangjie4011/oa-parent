package com.ule.oa.base.mapper;

import com.ule.oa.base.po.EmpQuitRecord;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
  * @ClassName: EmpQuitRecordMapper
  * @Description: 员工离职记录dao层
  * @author minsheng
  * @date 2017年7月27日 下午2:11:15
 */
public interface EmpQuitRecordMapper extends OaSqlMapper{
    int save(EmpQuitRecord record);

    EmpQuitRecord getById(Long id);

    int updateById(EmpQuitRecord record);
    
    int saveEmpLeaveTime();
}