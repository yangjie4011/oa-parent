package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.ClassSettingMapper;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.ClassSetPerson;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.ApplicationEmployeeClassDetailService;
import com.ule.oa.base.service.ApplicationEmployeeClassService;
import com.ule.oa.base.service.ClassSetPersonService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: ClassSettingServiceImpl
  * @Description: 班次设置业务层
  * @author minsheng
  * @date 2017年7月31日 下午3:11:08
 */
@Service
public class ClassSettingServiceImpl implements ClassSettingService{
	
	@Autowired
	private ClassSettingMapper classSettingMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private ClassSetPersonService classSetPersonService;
	@Autowired
	private ApplicationEmployeeClassDetailService applicationEmployeeClassDetailService;
	@Autowired
	private ApplicationEmployeeClassService applicationEmployeeClassService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private DepartMapper departMapper;
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;
	
	/**
	  * getList(查询所有的班次信息)
	  * @Title: getList
	  * @Description: 查询所有的班次信息
	  * @return    设定文件
	  * List<ClassSetting>    返回类型
	  * @throws
	 */
	@Override
	public List<ClassSetting> getList() {
		return classSettingMapper.getList();
	}

	/**
	 * @throws OaException 
	  * save(保存班次信息)
	  * @Title: save
	  * @Description: 保存班次信息
	  * @param classSetting
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public Integer save(ClassSetting classSetting) throws OaException{
		//每个班次最少6小时的工时（满足扣除满5减1规则），少于8小时的班次
		if(classSetting.getMustAttnTime()<6){
			throw new OaException("请最少安排6小时工作！");
		}
		User user = userService.getCurrentUser();
		if(classSetting.getName()==null||"".equals(classSetting.getName())){
			throw new OaException("班次简称不能为空！");
		}
		if(classSetting.getFullName()==null||"".equals(classSetting.getFullName())){
			throw new OaException("班次名称不能为空！");
		}
		if(classSetting.getName().trim().length()>1){
			throw new OaException("班次简称只能是一个字！");
		}
		if(classSetting.getStartTime()==null){
			throw new OaException("上班时间不能为空！");
		}
		if(classSetting.getEndTime()==null){
			throw new OaException("下班时间不能为空！");
		}
		/*ClassSetPerson setPerson = classSetPersonService.getByEmployeeId(user.getEmployee().getId());
		if(setPerson==null){
			throw new OaException("没有排班权限！");
		}*/
		Employee emp = employeeMapper.getById(user.getEmployeeId());
		classSetting.setCreateTime(new Date());
		classSetting.setCreateUser(userService.getCurrentAccount());
		classSetting.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		classSetting.setVersion(ConfigConstants.DEFAULT_VERSION);
		classSetting.setCompanyId(user.getCompanyId());
		//不可设置     完全相同的简称
		List<ClassSetting> list = classSettingMapper.getList();
		boolean flag = false;
		for(ClassSetting set:list){
			if(classSetting.getName().equals(set.getName())){
				flag = true;
				if(flag){
					throw new OaException("新增失败：该班次 简称 已存在！");
				}
				break;
			}
			if(DateUtils.format(classSetting.getStartTime(),"HH:mm:ss").equals(DateUtils.format(set.getStartTime(),"HH:mm:ss"))
					&&DateUtils.format(classSetting.getEndTime(),"HH:mm:ss").equals(DateUtils.format(set.getEndTime(),"HH:mm:ss"))){
				flag = true;
				if(flag){
					throw new OaException("新增失败：该班次 时间段 已存在！");
				}
				break;
			}
		}
		return classSettingMapper.save(classSetting);
	}

	/**
	  * updateById(更新班次信息)
	  * @Title: updateById
	  * @Description: 更新班次信息
	  * @param classSetting
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public Integer updateById(ClassSetting classSetting) {
		return classSettingMapper.updateById(classSetting);
	}
	
	/**
	  * getListByCondition(根据条件获取排班信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取排班信息
	  * @param classSetting
	  * @return    设定文件
	  * List<ClassSetting>    返回类型
	  * @throws
	 */
	public List<ClassSetting> getListByCondition(ClassSetting classSetting){
		return classSettingMapper.getListByCondition(classSetting);
	}
	
	//获取员工指定日期排班
	public ClassSetting getByEmpAndDate(ApplicationEmployeeClassDetail classDetail){
		return classSettingMapper.getByEmpAndDate(classDetail);
	}
	
	//获取员工指定日期排班
	public ClassSetting getByEmpAndDate1(ApplicationEmployeeClassDetail classDetail){
		return classSettingMapper.getByEmpAndDate1(classDetail);
	}

	@Override
	public ClassSetting getById(Long id) {
		return classSettingMapper.getById(id);
	}

	@Override
	public void deleteById(ClassSetting classSetting) throws OaException{
		User user = userService.getCurrentUser();
		
		//查询当前时间及以后是否有班次正在使用，有的话不能删除（排班申请表中待审批是否存在待删除班次）
		ApplicationEmployeeClass applicationEmployeeClass = new ApplicationEmployeeClass();
		applicationEmployeeClass.setApprovalStatus(ConfigConstants.DOING_STATUS);
		List<ApplicationEmployeeClass> list0 = applicationEmployeeClassService.getByCondition(applicationEmployeeClass);
		for(ApplicationEmployeeClass data:list0){
			ApplicationEmployeeClassDetail detail = new ApplicationEmployeeClassDetail();
			detail.setAttnApplicationEmployClassId(data.getId());
			detail.setClassSettingId(classSetting.getId());
			List<ApplicationEmployeeClassDetail> detailList = applicationEmployeeClassDetailService.selectByCondition(detail);
			if(detailList!=null&&detailList.size()>0){
				throw new OaException("班次正在使用，不能删除！");
			}
		}
		
		//查询当前时间及以后是否有班次正在使用，有的话不能删除（排班表是否存在待删除班次）
		EmployeeClass employeeClass = new EmployeeClass();
		employeeClass.setClassSettingId(classSetting.getId());
		employeeClass.setStartTime(new Date());
		List<EmployeeClass> list1 = employeeClassService.selectByCondition(employeeClass);
		if(list1!=null&&list1.size()>0){
			throw new OaException("班次正在使用，不能删除！");
		}
		
		classSetting.setUpdateUser(user.getEmployee().getCnName());
		classSetting.setUpdateTime(new Date());
		classSetting.setDelFlag(1);
		if(classSettingMapper.updateById(classSetting)<=0){
			throw new OaException("删除失败！");
		}
	}
	


	@Override
	public PageModel<ClassSetting> getReportPageList(ClassSetting classSet) {
		PageModel<ClassSetting> pm = new PageModel<ClassSetting>();
		
		int page = classSet.getPage() == null ? 0 : classSet.getPage();
		int rows = classSet.getRows() == null ? 0 : classSet.getRows();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		classSet.setLimit(pm.getLimit());
		classSet.setOffset(pm.getOffset());
		
		List<ClassSetting> byCondition = classSettingMapper.getPageListByCondition(classSet);
		for (ClassSetting classSetting : byCondition) {
			if(classSetting.getGroupIds()!=null && !"".equals(classSetting.getGroupIds())){
				String[] splitGroupIds = classSetting.getGroupIds().split(",");
				String groupNamesStr="";
				if(splitGroupIds.length>0){
					for (int i = 0; i < splitGroupIds.length; i++) {
						String departName = departMapper.getByGroupId(Long.parseLong(splitGroupIds[i]));
						ScheduleGroup groupById = scheduleGroupMapper.getGroupById(Long.parseLong(splitGroupIds[i]));
						if(groupById!=null){
							groupNamesStr+=departName+"_"+groupById.getName()+",";
						}
					}
					groupNamesStr = StringUtils.isNotBlank(groupNamesStr)?groupNamesStr.substring(0, groupNamesStr.length()-1):"";
				}else{
					//只有一个 id
					String departName = departMapper.getByGroupId(Long.parseLong(classSetting.getGroupIds()));	
					ScheduleGroup groupById = scheduleGroupMapper.getGroupById(Long.parseLong(splitGroupIds[0]));
					if(groupById!=null){
						groupNamesStr=departName+"_"+groupById.getName();
					}
					
				}
				classSetting.setGroupNames(groupNamesStr);
			}
		}
		pm.setRows(byCondition);
		pm.setTotal(classSettingMapper.getPageCount(classSet));
		// TODO Auto-generated method stub
		return pm;
	}

	@Override
	public Integer updateClassName(ClassSetting classSetting) throws OaException {
		// TODO Auto-generated method stub
		if(classSetting.getName()==null||"".equals(classSetting.getName())){
			throw new OaException("班次简称不能为空！");
		}
		if(classSetting.getFullName()==null||"".equals(classSetting.getFullName())){
			throw new OaException("班次名称不能为空！");
		}
		if(classSetting.getName().trim().length()>1){
			throw new OaException("班次简称只能是一个字！");
		}
		List<ClassSetting> list = classSettingMapper.getList();
		boolean flag = false;
		for(ClassSetting set:list){
			if(!classSetting.getId().equals(set.getId())){
				if(classSetting.getName().equals(set.getName())){
					flag = true;
					if(flag){
						throw new OaException("修改失败：该班次 简称 已存在！");
					}
					break;
				}
			}
		}
		return classSettingMapper.updateById(classSetting);
	}

}