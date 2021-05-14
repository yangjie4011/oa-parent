package com.ule.oa.web.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.SchedulePlan;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.SchedulePlanService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * 
  * @ClassName: SchedulePlanController
  * @Description: 班次
  * @author wufei
  * @date 2017年6月2日 
 */
@Controller
@RequestMapping("/schedulePlan")
public class SchedulePlanController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SchedulePlanService schedulePlanService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 班次设置界面
	 * @return
	 */
	@RequestMapping("/index.htm")
	public String index(){
		return "base/schedulePlan/schedulePlan_set";
	}
	
	/**
	 * 员工排班设置
	 * @return
	 */
	@RequestMapping("/shift.htm")
	public String shift(){
		return "base/schedulePlan/schedulePlan_shift";
	}
	
	/**
	 * 班次设置列表 
	 * 分页查询
	 * @param schedulePlan
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/pageList.htm")
	public PageModel<SchedulePlan> pageList(SchedulePlan schedulePlan){
		PageModel<SchedulePlan> pm = new PageModel<SchedulePlan>();
		pm.setRows(new java.util.ArrayList<SchedulePlan>());
		pm.setTotal(0);
		pm.setPageNo(1);
		try {
			pm = schedulePlanService.getByPagenation(schedulePlan);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	/**
	 * 查询排班list
	 * @param request
	 * @param schedulePlan
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getList.htm")
	public String getList(HttpServletRequest request, SchedulePlan schedulePlan){
		List<SchedulePlan> schedulePlanList = schedulePlanService.getListByCondition(schedulePlan);
		return JSONUtils.write(schedulePlanList);
	}
	
	/**
	 * 新增
	 * @param request
	 * @param schedulePlan
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/save.htm")
	public JSON insert(HttpServletRequest request, SchedulePlan schedulePlan){
		JSON json=new JSON(false, null, null);
		Date currentTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		//获取当前用户
		User user = userService.getCurrentUser();
		schedulePlan.setCreateUser(user.getEmployee() != null ? user.getEmployee().getCnName() : "");
		schedulePlan.setCreateTime(currentTime);
		schedulePlan.setDelFlag(SchedulePlan.STATUS_NORMAL);
		try{
			schedulePlanService.save(schedulePlan);
			json.setSuccess(true);
			json.setMessage("保存成功!");
		}catch(Exception e){
			json.setSuccess(false);
			json.setMessage("保存失败!");
		}
		return json;
	}
	
	/**
	 * 修改
	 * @param request
	 * @param schedulePlan
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/update.htm")
	public JSON update(HttpServletRequest request, SchedulePlan schedulePlan){
		JSON json = new JSON(false, null, null);
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		//获取当前用户
		User user = userService.getCurrentUser();
		try {
			schedulePlan.setUpdateUser(user.getEmployee() != null ? user.getEmployee().getCnName() : "");
			schedulePlan.setUpdateTime(updateTime);
			schedulePlanService.updateById(schedulePlan);
			json.setSuccess(true);
			json.setMessage("修改成功!");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMessage("修改失败!");
		}
		return json;
	}
	
	/**
	 * 删除
	 * @param request
	 * @param schedulePlan
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/delete.htm")
	public JSON delete(HttpServletRequest request, String id){
		SchedulePlan schedulePlan = new SchedulePlan();
		JSON json=new JSON(false, null, null);
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		//获取当前用户
		User user = userService.getCurrentUser();
		try {
			schedulePlan.setUpdateUser(user.getEmployee() != null ? user.getEmployee().getCnName() : "");
			schedulePlan.setId(Long.valueOf(id));
			schedulePlan.setUpdateTime(updateTime);
			schedulePlan.setDelFlag(SchedulePlan.STATUS_NORMAL);
			schedulePlanService.updateById(schedulePlan);
			json.setSuccess(true);
			json.setMessage("删除成功!");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMessage("删除失败!");
		}
		return json;
	}
}
