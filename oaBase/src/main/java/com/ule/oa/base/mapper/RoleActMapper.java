package com.ule.oa.base.mapper;

import com.ule.oa.base.po.RoleAct;

public interface RoleActMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoleAct record);

    int insertSelective(RoleAct record);

    RoleAct selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoleAct record);

    int updateByPrimaryKey(RoleAct record);
}