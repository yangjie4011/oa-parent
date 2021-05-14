package com.ule.oa.base.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.po.RemoteWorkRegister;
import com.ule.oa.base.util.jdbc.OaSqlMapper;
import com.ule.oa.common.persistence.SqlMapper;

/**
 * @ClassName: 远程工作管理
 * @Description: 远程工作管理
 * @author zhoujinliang
 * @date 2020年2月25日12:09:04
 */
public interface RemoteWorkRegisterMapper extends OaSqlMapper{
	
	int save(RemoteWorkRegister remoteWorkRegister);
		
	int updateById(RemoteWorkRegister remoteWorkRegister);
	/**
	 * 批量保存
	 * @param empMsgs
	 */
	Integer batchSave(List<RemoteWorkRegister> remoteWorkRegister);
	/**
	 *	根据员工id和月份查询数据
	 * @param RemoteWorkRegister
	 * @return
	 */

	List<RemoteWorkRegister> getDetailByEidAndMonth(RemoteWorkRegister remoteWorkRegister);
	
	/**
	 * 根据条件查询
	 * @param remoteWorkRegister
	 * @return
	 */
	List<RemoteWorkRegister> getListByCondition(RemoteWorkRegister remoteWorkRegister);
}
