package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.EmpApplicationAbnormalAttendanceMapper;
import com.ule.oa.base.mapper.EmpDepartMapper;
import com.ule.oa.base.mapper.RuProcdefMapper;
import com.ule.oa.base.mapper.RunTaskMapper;
import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.RuProcdef;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationLeaveDetailService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.RuProcdefService;
import com.ule.oa.base.service.UserTaskService;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;


@Service
public class RuProcdefServiceImpl implements RuProcdefService{

	@Autowired
	private RuProcdefMapper ruProcdefMapper;
	
	@Autowired
	private DepartService departService;
	
	@Autowired
	private RunTaskMapper runTaskMapper;
	@Autowired
	private EmpDepartMapper empDepartMapper;
	@Autowired
	private UserTaskService userTaskService;
	
	
	@Autowired
	private EmpApplicationAbnormalAttendanceMapper empApplicationAbnormalAttendanceMapper;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	
	@Autowired
	private EmpApplicationLeaveService empApplicationLeaveService;
	@Autowired
	private EmpApplicationLeaveDetailService empApplicationLeaveDetailService;
	@Override
	public RuProcdef getById(Long id) {
		return ruProcdefMapper.getById(id);
	}

	@Override
	public List<RuProcdef> getList(RuProcdef ruProcdef) {
		return ruProcdefMapper.getList(ruProcdef);
	}

	@Override
	public List<RuProcdef> getPageList(RuProcdef ruProcdef) {
		return ruProcdefMapper.getPageList(ruProcdef);
	}

	@Override
	//FIXME : 销假
	public Integer getCount(RuProcdef ruProcdef) {
		//以前代办数据
		Integer old_count = ruProcdefMapper.getCount(ruProcdef);
		//现在代办数据
		Integer new_count = activitiServiceImpl.getUserHaveTaskIds(ruProcdef.getAssigneeId()).size();
		return old_count+new_count;
	}

	@Override
	public Long save(RuProcdef ruProcdef) {
		return ruProcdefMapper.save(ruProcdef);
	}

	@Override
	public void batchSave(List<RuProcdef> ruProcdef) {
		ruProcdefMapper.batchSave(ruProcdef);
	}

	@Override
	public Integer updateById(RuProcdef ruProcdef) {
		return ruProcdefMapper.updateById(ruProcdef);
	}

	@Override
	public RuProcdef getUrlByRuTaskId(Long ruTaskId) {
		return ruProcdefMapper.getUrlByRuTaskId(ruTaskId);
	}
	
	@Override
	public PageModel<RuProcdef> getByPagenation(RuProcdef ruProcdef) {
		int page = ruProcdef.getPage() == null ? 0 : ruProcdef.getPage();
		int rows = ruProcdef.getRows() == null ? 0 : ruProcdef.getRows();
		
		PageModel<RuProcdef> pm = new PageModel<RuProcdef>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		
//		int total = userTaskService.queryTaskCountByEmpId(null);
		
//		pm.setTotal(total);
		List<Task> tasks = activitiServiceImpl.getUserHaveTasks(ruProcdef.getAssigneeId());
		
		List<RuProcdef> ruProcdefList = ruProcdefMapper.getPageList(ruProcdef);
		
		
		getRunTaskList(tasks);
		pm.setRows(ruProcdefList);
		return pm;
	}
	
	@Override
	public Integer getNodeCodeCount(RuProcdef ruProcdef) {
		return ruProcdefMapper.getNodeCodeCount(ruProcdef);
	}

	@Override
	public Integer getNodeModuleCount(RuProcdef ruProcdef) {
		return ruProcdefMapper.getNodeModuleCount(ruProcdef);
	}

	@Override
	public Integer updateByNodeCode(RuProcdef ruProcdef) {
		return ruProcdefMapper.updateByNodeCode(ruProcdef);
	}
	
	
	public List<RuProcdef> getRunTaskList(List<Task> tasks) {
		List<RuProcdef> ruselt = Lists.newArrayList();
		
		return ruselt;
	}

	@Override
	public List<RuProcdef> getRunTaskListByCodeAndEntry(RuProcdef runTask) {
		return ruProcdefMapper.getRunTaskListByCodeAndEntry(runTask);
	}
}
