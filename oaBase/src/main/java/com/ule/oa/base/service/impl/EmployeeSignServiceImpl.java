package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.AttnSignRecordMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.EmployeeSignService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

@Service
public class EmployeeSignServiceImpl implements EmployeeSignService {
	
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
	private AttnSignRecordMapper attnSignRecordMapper;
	@Autowired
	private EmployeeMapper employeeMapper;

	@Override
	public PageModel<Map<String, Object>> getList(Employee employee) {
		int page = employee.getPage() == null ? 0 : employee.getPage();
		int rows = employee.getRows() == null ? 0 : employee.getRows();
		
		PageModel<Map<String, Object>> pm = new PageModel<Map<String, Object>>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		
		Integer total = employeeMapper.getCount(employee);
		pm.setTotal(total);
		
		employee.setOffset(pm.getOffset());
		employee.setLimit(pm.getLimit());
		
		List<Long> jobStatusList = new ArrayList<Long>();
		jobStatusList.add(0L);
		jobStatusList.add(2L);
		employee.setJobStatusList(jobStatusList);
		
		List<Employee> employees = employeeMapper.getPageList(employee);
		
		List<Map<String, Object>> employeeList = new ArrayList<Map<String, Object>>();
		for(Employee data:employees){
			Map<String, Object> data1 = new HashMap<String, Object>();
			data1.put("id", data.getId());
			data1.put("code", data.getCode());
			data1.put("name", data.getCnName());
			data1.put("departName", data.getDepartName());
			data1.put("positionName", data.getPositionName());
			Employee reportToLeader = employeeMapper.getById(data.getReportToLeader());
			data1.put("reportToLeader", reportToLeader!=null?reportToLeader.getCnName():"");
			employeeList.add(data1);
		}
		pm.setRows(employeeList);
		return pm;
	}

	@Override
	public Map<String, Object> sign(Long employeeId) throws Exception {
		logger.info("员工ID="+employeeId+"立即签到");
		Map<String, Object> result = new HashMap<String, Object>();
		Employee employee = employeeMapper.getById(employeeId);
		Date signTime = new Date();
		if(employee!=null){
			AttnSignRecord signRecord = new AttnSignRecord();
			signRecord.setCompanyId(employee.getCompanyId());
			signRecord.setEmployeeId(employee.getId());
			signRecord.setEmployeeName(employee.getCnName());
			signRecord.setSignTime(signTime);
			signRecord.setCreateTime(signTime);
			signRecord.setCreateUser("OA_SIGN");
			signRecord.setDelFlag(0);
			signRecord.setRemark("OA后台立即签到");
			signRecord.setType(4);
			attnSignRecordMapper.insert(signRecord);
			result.put("sucess",true);
			result.put("msg",employee.getCnName()+"于"+DateUtils.format(signTime,DateUtils.FORMAT_SHORT_CN)+"已成功签到，签到时间为"+DateUtils.format(signTime,DateUtils.FORMAT_HH_MM)+"。");
		}else{
			throw new OaException("该员工不存在！");
		}
		return result;
	}

}
