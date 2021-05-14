package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.ClassSetPersonMapper;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.po.ClassSetPerson;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.service.ClassSetPersonService;

/**
 * @ClassName: 排班人表
 * @Description: 排班人表
 * @author yangjie
 * @date 2017年9月12日
 */
@Service
public class ClassSetPersonServiceImpl implements ClassSetPersonService {
	
	@Autowired
	private ClassSetPersonMapper classSetPersonMapper;
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;

	@Override
	public ClassSetPerson getByEmployeeId(Long employeeId) {
		return classSetPersonMapper.getByEmployeeId(employeeId);
	}
	
	@Override
	public List<ClassSetPerson> getAll(){
		return classSetPersonMapper.getAll();
	}

	@Override
	public boolean isSetPerson(Long employeeId) {
		boolean flag =false;
		List<ScheduleGroup> schedulerList = scheduleGroupMapper.getListByScheduler(employeeId);
		for(ScheduleGroup group:schedulerList){
			if(group.getScheduler().equals(employeeId)){
				flag = true;
				break;
			}
		}
		return flag;
	}
	

}
