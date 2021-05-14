package com.ule.oa.base.service;

import java.util.Date;
import java.util.Map;

import com.ule.oa.base.po.RemoteAbnormalRemoveDTO;
import com.ule.oa.common.utils.PageModel;

/**
 * 远程异常消除
 * @author yangjie
 *
 */
public interface RemoteAbnormalRemoveService {
	
	/**
	 * 分页查询员工远程异常数据
	 * @param param
	 * @return
	 */
	public PageModel<RemoteAbnormalRemoveDTO> getPageList(RemoteAbnormalRemoveDTO param);
	
	/**
	 * 审阅
	 * @param employeeId
	 * @param registerDate
	 * @param approvalStatus
	 * @param reason
	 * @return
	 */
	public Map<String,String> review(Long employeeId,Date registerDate,Integer approvalStatus,String reason,int i);
	
	/**
	 * 修复登记班次和实际班次不一致的数据
	 */
	public void repairDate();

}
