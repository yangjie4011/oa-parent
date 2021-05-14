package com.ule.oa.base.service;

import java.util.Map;

import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RemoteWorkRegister;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 远程工作管理
 * @Description: 远程工作管理
 * @author zhoujinliang
 * @date 2020年2月25日12:09:04
 */
public interface RemoteWorkRegisterService {
	
	public int save(RemoteWorkRegister remoteWorkRegister);
	
	public int updateById(RemoteWorkRegister remoteWorkRegister);
	
	

	public Map<String, Object> queryWorkRegistByCondition(Long empId,
			String month);

	public void saveMapStr(String info);

	public Map<String, Object>  isWorkDate(String date,Long employeeId);

}
