package com.ule.oa.base.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ule.oa.base.po.RemoteAbnormalRemoveDTO;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * 远程异常消除
 * @author yangjie
 *
 */
public interface RemoteAbnormalRemoveMapper extends OaSqlMapper{
	
	/**
	 * 获取员工列表
	 * @param param
	 * @return
	 */
	List<RemoteAbnormalRemoveDTO> getEmployeeList(RemoteAbnormalRemoveDTO param);
	
	/**
	 * 获取员工数量
	 * @param param
	 * @return
	 */
	Integer getEmployeeCount(RemoteAbnormalRemoveDTO param);
	
	/**
	 * 查询远程异常登记数据
	 * @param employeeId
	 * @param registerDate
	 * @return
	 */
	RemoteAbnormalRemoveDTO getByEmployeeIdAndRegisterDate(@Param("employeeId")Long employeeId,@Param("registerDate")Date registerDate);
	
	int updateApprovalStatusById(@Param("id")Long id,@Param("reason")String reason,@Param("approvalStatus")Integer approvalStatus,@Param("updateTime")Date updateTime,@Param("updateUser")String updateUser);

}
