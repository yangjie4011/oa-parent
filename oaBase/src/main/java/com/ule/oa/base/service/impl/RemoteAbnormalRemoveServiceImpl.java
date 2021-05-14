package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.mapper.AttnWorkHoursMapper;
import com.ule.oa.base.mapper.RemoteAbnormalRemoveMapper;
import com.ule.oa.base.mapper.RemoteWorkRegisterMapper;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.RemoteAbnormalRemoveDTO;
import com.ule.oa.base.po.RemoteWorkRegister;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.RemoteAbnormalRemoveService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.util.AttnStatisticsUtil;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
 * 远程异常消除
 * @author yangjie
 *
 */
@Service
public class RemoteAbnormalRemoveServiceImpl implements
		RemoteAbnormalRemoveService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RemoteAbnormalRemoveMapper remoteAbnormalRemoveMapper;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private UserService userService;
	@Autowired
	private AttnWorkHoursMapper attnWorkHoursMapper;
	@Autowired
	private ConfigCacheManager configCacheManager;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private RemoteWorkRegisterMapper remoteWorkRegisterMapper;
	@Autowired
	private EmployeeClassService employeeClassService;

	@Override
	public PageModel<RemoteAbnormalRemoveDTO> getPageList(
			RemoteAbnormalRemoveDTO param) {
		long time1 = System.currentTimeMillis();
		
		int page = param.getPage() == null ? 0 : param.getPage();
		int rows = param.getRows() == null ? 0 : param.getRows();
		
		PageModel<RemoteAbnormalRemoveDTO> pm = new PageModel<RemoteAbnormalRemoveDTO>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		
        
        //工时类型
        CompanyConfig companyConfigConditon = new CompanyConfig();//查询配置表条件
		companyConfigConditon.setCode("typeOfWork");//工时类型
		List<CompanyConfig> workTypeList = companyConfigService.getListByCondition(companyConfigConditon);
		Map<Long,String> workTypeMap =  workTypeList.stream().collect(Collectors.toMap(CompanyConfig :: getId, CompanyConfig :: getDisplayName));
		
		
		Integer total = remoteAbnormalRemoveMapper.getEmployeeCount(param);
		pm.setTotal(total);
		param.setOffset(pm.getOffset());
		param.setLimit(pm.getLimit());
		List<RemoteAbnormalRemoveDTO> list = remoteAbnormalRemoveMapper.getEmployeeList(param);
		for(RemoteAbnormalRemoveDTO data:list){
			if(workTypeMap!=null&&workTypeMap.containsKey(data.getWorkType())){
				data.setWorkTypeName(workTypeMap.get(data.getWorkType()));
			}
		}

		pm.setRows(list);
		
		long time2 = System.currentTimeMillis();
		logger.info("RemoteAbnormalRemoveService getPageList uses time is:"+(time2-time1));
		return pm;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> review(Long employeeId, Date registerDate,
			Integer approvalStatus, String reason,int i) {
		User user = userService.getCurrentUser();
		Map<String,String> result = new HashMap<String,String>();
		logger.info("review审阅入参：employeeId="+employeeId+";registerDate="+registerDate+";approvalStatus="+approvalStatus+";reason="+reason+"i="+i);
		
		if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(registerDate, DateUtils.FORMAT_SHORT),false)){
			logger.error("review审阅入参：employeeId="+employeeId+";registerDate="+registerDate+"已超过审阅时间");
			result.put("sucess", "false");
			result.put("msg", "\n第" + (i + 1) + "条数据： 已超过审阅时间.");
			return result;
		}
		
		RemoteAbnormalRemoveDTO oldRegister = remoteAbnormalRemoveMapper.getByEmployeeIdAndRegisterDate(employeeId,registerDate);
		if(oldRegister==null){
			logger.error("RemoteAbnormalRemoveService review:该条登记数据已不存在。");
			result.put("sucess", "false");
			result.put("msg", "\n第" + (i + 1) + "条数据： 该条登记数据已不存在.");
			return result;
		}
		//删除老的远程消异常考勤数据
		AttnWorkHours oldDataP = new AttnWorkHours();
		oldDataP.setEmployId(employeeId);
		oldDataP.setWorkDate(registerDate);
		oldDataP.setUpdateTime(new Date());
		oldDataP.setUpdateUser(user.getEmployee().getCnName());
		oldDataP.setDataReason(reason);
		attnWorkHoursMapper.deleteRemoteAbnormalRemoveDate(oldDataP);
		
		if(approvalStatus==0){
			//通过（新增考勤数据）
			AttnWorkHours addAttnWorkHours = new AttnWorkHours();
			addAttnWorkHours.setCompanyId(user.getCompanyId());
			addAttnWorkHours.setWorkDate(registerDate);
			addAttnWorkHours.setEmployId(employeeId);
			addAttnWorkHours.setDataType(6);
			addAttnWorkHours.setDataReason(reason);
			addAttnWorkHours.setDelFlag(0);
			Integer strat =  Integer.valueOf(DateUtils.format(oldRegister.getClassStartTime(), "HHmm"));
			Integer end =  Integer.valueOf(DateUtils.format(oldRegister.getClassEndTime(), "HHmm"));
			Date classStartTime = DateUtils.parse(DateUtils.format(registerDate, DateUtils.FORMAT_SHORT)+" "+DateUtils.format(oldRegister.getClassStartTime(), DateUtils.FORMAT_HH_MM_SS), DateUtils.FORMAT_LONG);
			Date classEndTime = DateUtils.parse(DateUtils.format(registerDate, DateUtils.FORMAT_SHORT)+" "+DateUtils.format(oldRegister.getClassEndTime(), DateUtils.FORMAT_HH_MM_SS), DateUtils.FORMAT_LONG);
			if(end<strat){
				classEndTime = DateUtils.addDay(classEndTime, 1);
			}
			addAttnWorkHours.setStartTime(classStartTime);
			addAttnWorkHours.setEndTime(classEndTime);
			addAttnWorkHours.setCreateTime(new Date());
			addAttnWorkHours.setCreateUser(user.getEmployee().getCnName());
		//	attnWorkHoursMapper.save(addAttnWorkHours);
			AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
		    attnStatisticsUtil.calWorkHours(user.getCompanyId(), 
		    		employeeId,"",registerDate,oldRegister.getMustAttnTime()+1,"6",addAttnWorkHours.getStartTime(),addAttnWorkHours.getEndTime(),null,reason,0L);
		}
		//修改该条单据状态并且重新计算考勤
		remoteAbnormalRemoveMapper.updateApprovalStatusById(oldRegister.getId(), reason,
				approvalStatus, new Date(), user.getEmployee().getCnName());
		
		result.put("sucess", "true");
		return result;
	}

	@Override
	public void repairDate() {
		//查询登记班次和实际班次不一致数据
		List<RemoteWorkRegister> list = remoteWorkRegisterMapper.getListByCondition(null);
		for(RemoteWorkRegister data:list){
			
			logger.info("repairDate:employeeId="+data.getEmployeeId()+";date="+data.getRegisterDate());
			
			try{
				Long employeeId = data.getEmployeeId();
				Date registerDate = data.getRegisterDate();
				Long classId = data.getClasssSettingId();
				//查询当天改员工的班次
				EmployeeClass employeeClass = new EmployeeClass(); 
				employeeClass.setEmployId(employeeId);
				employeeClass.setClassDate(registerDate);
				employeeClass = employeeClassService.getEmployeeClassSetting(employeeClass);
				if(employeeClass!=null&&employeeClass.getClassSettingId()!=null){
					//班次不一致的数据
					if(classId!=null&&employeeClass.getClassSettingId()!=null&&classId.longValue()!=employeeClass.getClassSettingId().longValue()){
						RemoteWorkRegister update = new RemoteWorkRegister();
						update.setId(data.getId());
						update.setClasssSettingId(employeeClass.getClassSettingId());
						remoteWorkRegisterMapper.updateById(update);
						//查询是否有考勤数据
						AttnWorkHours oldWorkHours = attnWorkHoursMapper.getRemoteAbnormalRemoveDate(registerDate, employeeId);
						if(oldWorkHours!=null&&oldWorkHours.getId()!=null){
							Integer strat =  Integer.valueOf(DateUtils.format(employeeClass.getStartTime(), "HHmm"));
							Integer end =  Integer.valueOf(DateUtils.format(employeeClass.getEndTime(), "HHmm"));
							Date classStartTime = DateUtils.parse(DateUtils.format(registerDate, DateUtils.FORMAT_SHORT)+" "+DateUtils.format(employeeClass.getStartTime(), DateUtils.FORMAT_HH_MM_SS), DateUtils.FORMAT_LONG);
							Date classEndTime = DateUtils.parse(DateUtils.format(registerDate, DateUtils.FORMAT_SHORT)+" "+DateUtils.format(employeeClass.getEndTime(), DateUtils.FORMAT_HH_MM_SS), DateUtils.FORMAT_LONG);
							if(end<strat){
								classEndTime = DateUtils.addDay(classEndTime, 1);
							}
							attnWorkHoursMapper.repairDate(classStartTime, classEndTime, oldWorkHours.getId());
						}
					}
				}
			}catch(Exception e){
				logger.error("repairDate:employeeId="+data.getEmployeeId()+";"+e.getMessage());
			}
			
		}
		
	}

}
