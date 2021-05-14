package com.ule.oa.base.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.RolePrivilege;
import com.ule.oa.base.util.jdbc.OaSqlMapper;


public interface RolePrivilegeMapper extends OaSqlMapper{
    RolePrivilege getById(Long id);

    int save(RolePrivilege record);

    int updateById(RolePrivilege record);
    
    List<RolePrivilege> getByRoleId(@Param("roleId") Long roleId);
}