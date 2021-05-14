package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.AttnUpdateLogMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.po.AttnUpdateLog;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AttnUpdateLogService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.PageModel;

@Service
public class AttnUpdateLogServiceImpl implements AttnUpdateLogService {
	
	@Resource
	private AttnUpdateLogMapper attnUpdateLogMapper;
	
	@Resource
	private UserService userService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private RabcUserMapper rabcUserMapper;

	@Override
	public PageModel<AttnUpdateLog> getPageList(AttnUpdateLog attnUpdateLog) {
		int page = attnUpdateLog.getPage() == null ? 0 : attnUpdateLog.getPage();
		int rows = attnUpdateLog.getRows() == null ? 0 : attnUpdateLog.getRows();
		
		PageModel<AttnUpdateLog> pm = new PageModel<AttnUpdateLog>();
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			attnUpdateLog.setOffset(pm.getOffset());
			attnUpdateLog.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<AttnUpdateLog>());
			return pm;
		}else{
			attnUpdateLog.setCurrentUserDepart(deptDataByUserList);
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				attnUpdateLog.setSubEmployeeIdList(subEmployeeIdList);
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total = attnUpdateLogMapper.selectCountByCondition(attnUpdateLog);
			pm.setTotal(total);
			
			attnUpdateLog.setOffset(pm.getOffset());
			attnUpdateLog.setLimit(pm.getLimit());
			
			List<AttnUpdateLog> list = attnUpdateLogMapper.selectByCondition(attnUpdateLog);
			pm.setRows(list);
			return pm;
		}	
	}

}
