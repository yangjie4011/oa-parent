 package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.po.ApplicationEmployeeClass;
import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.ScheduleGroup;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.ApplicationEmployeeClassDetailService;
import com.ule.oa.base.service.ApplicationEmployeeClassService;
import com.ule.oa.base.service.ClassSetPersonService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.web.util.Token;

@Controller
@RequestMapping("classSetting")
public class ClassSettingController {
	@Autowired
	private ClassSettingService classSettingService;
	@Autowired
	private UserService userService;
	@Autowired
	ApplicationEmployeeClassDetailService applicationEmployeeClassDetailService;
	@Autowired
	ApplicationEmployeeClassService applicationEmployeeClassService;
	@Resource
	private ClassSetPersonService classSetPersonService;
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;
	
	/**
	  * save(班次保存)
	  * @Title: save
	  * @Description: 班次保存
	  * @param classSetting
	  * @return    设定文件
	  * JSON    返回类型
	  * @throws
	 */
	@RequestMapping("/save.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String, Object> save(HttpServletRequest request,ClassSetting classSetting){
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			classSettingService.save(classSetting);
			map.put("message", "新增成功!");
			map.put("success", true);
			return map;
		} catch (Exception e) {
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
	
	/**
	  * getListByCondition(根据条件获取班次信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取班次信息
	  * @param classSetting
	  * @return    设定文件
	  * List<ClassSetting>    返回类型
	  * @throws
	 */
	@RequestMapping("/getListByCondition.htm")
	@ResponseBody
	public List<ClassSetting> getListByCondition(ClassSetting classSetting){
		List<ClassSetting> result = new ArrayList<ClassSetting>();
		User user = userService.getCurrentUser();
		if(classSetting.getDepartId()==null){
			List<ScheduleGroup> schedulerList = scheduleGroupMapper.getListByScheduler(user.getEmployee().getId());
			if(schedulerList!=null&&schedulerList.size()>0){
				//默认加一个休息班次
				ClassSetting rest = new ClassSetting();
				rest.setName("休");
				rest.setMustAttnTime(0d);
				rest.setIsInterDay(0);
				rest.setId(0L);
				rest.setStartTime(DateUtils.parse("00:00:00","hh:mm:ss"));
				rest.setEndTime(DateUtils.parse("00:00:00","hh:mm:ss"));
				result.add(rest);
				List<ClassSetting> list = classSettingService.getListByCondition(classSetting);
				for(ClassSetting setting:list){
					result.add(setting);
				}
			}
		}else{
			//默认加一个休息班次
			ClassSetting rest = new ClassSetting();
			rest.setName("休");
			rest.setMustAttnTime(0d);
			rest.setIsInterDay(0);
			rest.setId(0L);
			rest.setStartTime(DateUtils.parse("00:00:00","hh:mm:ss"));
			rest.setEndTime(DateUtils.parse("00:00:00","hh:mm:ss"));
			result.add(rest);
			List<ClassSetting> list = classSettingService.getListByCondition(classSetting);
			for(ClassSetting setting:list){
				result.add(setting);
			}
		}
		return result;
	}
	
	@RequestMapping("/getByEmpAndDate.htm")
	@ResponseBody
	public Map<String,String> getByEmpAndDate(Long employClassId,Long employeeId,String classDate,Integer isMove){
		Map<String,String> result = new HashMap<String,String>();
		try{
			//查询本次申请单
			ApplicationEmployeeClass employClassP = new ApplicationEmployeeClass();
			employClassP.setId(employClassId);
			List<ApplicationEmployeeClass> employClassList = applicationEmployeeClassService.getByCondition(employClassP);
			if(employClassList!=null&&employClassList.size()>0){
				if(employClassList.get(0).getIsMove().intValue()==ApplicationEmployeeClass.IS_MOVE_0||isMove.intValue()==ApplicationEmployeeClass.IS_MOVE_0){
					//原始申请单
					ApplicationEmployeeClassDetail classDetail = new ApplicationEmployeeClassDetail();
					classDetail.setClassDate(DateUtils.parse(classDate, DateUtils.FORMAT_SHORT));
					classDetail.setEmployId(employeeId);
					ClassSetting classSetting = new ClassSetting();
					if(employClassList.get(0).getApprovalStatus()!=null&&
							employClassList.get(0).getApprovalStatus().intValue()==ConfigConstants.PASS_STATUS.intValue()){
						classSetting = classSettingService.getByEmpAndDate(classDetail);
					}else{
						classSetting = classSettingService.getByEmpAndDate1(classDetail);
					}
					if(classSetting==null){
						result.put("startTime", "");
						result.put("endTime", "");
					}else{
						result.put("startTime", DateUtils.format(classSetting.getStartTime(), "HH:mm"));
						result.put("endTime", DateUtils.format(classSetting.getEndTime(), "HH:mm"));
						result.put("name", classSetting.getName());
					}
				}else{
					//调班申请单
					ApplicationEmployeeClass employClassP1 = new ApplicationEmployeeClass();
					employClassP1.setDepartId(employClassList.get(0).getDepartId());
					employClassP1.setClassMonth(employClassList.get(0).getClassMonth());
					employClassP1.setApprovalStatus(ConfigConstants.PASS_STATUS);
					List<ApplicationEmployeeClass> classList  = applicationEmployeeClassService.getPassList(employClassP1);
					List<ApplicationEmployeeClassDetail> detailList = new ArrayList<ApplicationEmployeeClassDetail>();
					if(employClassList.get(0).getApprovalStatus()!=null&&
							employClassList.get(0).getApprovalStatus().intValue()==ConfigConstants.PASS_STATUS.intValue()){
						//通过了查找前一个通过的申请单当日的排班申请数据
						ApplicationEmployeeClassDetail classDetailP = new ApplicationEmployeeClassDetail();
						classDetailP.setClassDate(DateUtils.parse(classDate, DateUtils.FORMAT_SHORT));
						classDetailP.setEmployId(employeeId);
						classDetailP.setAttnApplicationEmployClassId(classList.get(classList.size()-2).getId());
						detailList = applicationEmployeeClassDetailService.selectByCondition(classDetailP);
					}else{
						//未通过，查找最后一个通过的申请单
						ApplicationEmployeeClassDetail classDetailP = new ApplicationEmployeeClassDetail();
						classDetailP.setClassDate(DateUtils.parse(classDate, DateUtils.FORMAT_SHORT));
						classDetailP.setEmployId(employeeId);
						classDetailP.setAttnApplicationEmployClassId(classList.get(classList.size()-1).getId());
						detailList = applicationEmployeeClassDetailService.selectByCondition(classDetailP);
					}
					if(detailList!=null&&detailList.size()>0){
						if(detailList.get(0).getClassSettingId()!=null){
							ClassSetting classSetting = classSettingService.getById(detailList.get(0).getClassSettingId());
							result.put("startTime", DateUtils.format(classSetting.getStartTime(), "HH:mm"));
							result.put("endTime", DateUtils.format(classSetting.getEndTime(), "HH:mm"));
							result.put("name", classSetting.getName());
						}else{
							result.put("startTime", "");
							result.put("endTime", "");
						}
					}else{
						ApplicationEmployeeClassDetail classDetail = new ApplicationEmployeeClassDetail();
						classDetail.setClassDate(DateUtils.parse(classDate, DateUtils.FORMAT_SHORT));
						classDetail.setEmployId(employeeId);
						ClassSetting classSetting = new ClassSetting();
						classSetting = classSettingService.getByEmpAndDate(classDetail);
						if(classSetting==null){
							result.put("startTime", "");
							result.put("endTime", "");
						}else{
							result.put("startTime", DateUtils.format(classSetting.getStartTime(), "HH:mm"));
							result.put("endTime", DateUtils.format(classSetting.getEndTime(), "HH:mm"));
							result.put("name", classSetting.getName());
						}
					}
				}
			}
		}catch(Exception e){
			
		}
		return result;
	}
	
	/**
	  * save(班次删除)
	  * @Title: delete
	  * @Description: 班次删除
	  * @param classSetting
	  * @return    设定文件
	  * JSON    返回类型
	  * @throws
	 */
	@RequestMapping("/delete.htm")
	@ResponseBody
	@Token(remove = true)
	public Map<String, Object> delete(HttpServletRequest request,ClassSetting classSetting){
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			classSettingService.deleteById(classSetting);
			map.put("message", "删除成功!");
			map.put("success", true);
			return map;
		} catch (Exception e) {
			map.put("repeatFlag", false);
			map.put("message",e.getMessage());
			map.put("success",false);
			return map;
		}
	}
}
