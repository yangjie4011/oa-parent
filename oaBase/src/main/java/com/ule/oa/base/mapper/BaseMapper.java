package com.ule.oa.base.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

public interface BaseMapper<T> extends Mapper<T> , InsertListMapper<T>{

}
