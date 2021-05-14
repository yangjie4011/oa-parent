package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;
import com.ule.oa.base.po.ActPermission;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface ActPermissionMapper  extends BaseMapper<ActPermission> ,OaSqlMapper{
	
	List<Long> getEmpIdByPermission(ActPermission actPermission);

	List<Long> getEmpIdByParam(Map<String, Object> param);
}