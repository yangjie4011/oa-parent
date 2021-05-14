package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.AttnTaskRecord;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface AttnTaskRecordMapper  extends OaSqlMapper{

    int insert(AttnTaskRecord record);

    List<AttnTaskRecord> selectByCondition(AttnTaskRecord attnTaskRecord);

    int updateById(AttnTaskRecord record);
    
    int saveBatch(List<AttnTaskRecord> list);

	Date getMaxDateOfTask();

	List<Employee> selectEmpByAttnTask(AttnTaskRecord attnTaskRecordCondition);

	int deleteByDate(@Param("attnDate")Date attnDate, @Param("ids")List<Long> ids);
}