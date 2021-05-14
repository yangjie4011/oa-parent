package com.ule.oa.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.ule.oa.base.po.Affair;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.AffairService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.http.HttpWriter;
import com.ule.oa.common.utils.json.JSONUtils;

/**
  * @ClassName: ScheduleController
  * @Description: 日程管理
  * @author wufei
  * @date 2017年5月16日 下午13:54:07
 */
@Controller
@RequestMapping("/schedule")
public class ScheduleController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private AffairService affairService; 
	@Autowired
	private UserService userService;
	
	/**
	 * 进入日程页面初始化数据
	 * @param request
	 * @param txtdatetimeshow 页面传入的日期（*日*周*月）
	 * @param viewtype 页面传入的日期类型（日周月）
	 * @return
	 */
	@RequestMapping("/index.htm")
	public String index(HttpServletRequest request, String txtdatetimeshow, String viewtype){
		logger.info("index pull start...");
		long start = System.currentTimeMillis();
		//处理页面传入的中文乱码问题
		if(txtdatetimeshow != null) {
			try {
				txtdatetimeshow = URLDecoder.decode(txtdatetimeshow, "utf-8");
			} catch (UnsupportedEncodingException e) {
				logger.info("日期格式不正确");
			}
		}
		//定义日期格式
		SimpleDateFormat f1 = new SimpleDateFormat("yyyy年MM月dd");
		SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd");
		//定义开始结束时间
		String startTime = "";
		String endTime = "";
		//定义日程对象和日程集合
		Affair affair = new Affair();
		List<Affair> affairList = new ArrayList<Affair>();
		//判断是查询一日还是一周还是一月的日程信息,如果viewtype为空,则默认查询一周
		if(viewtype == null || "".equals(viewtype)) {
			viewtype = "week";
		} else if("day".equals(viewtype)) {
			try {
				Date dayDate = f1.parse(txtdatetimeshow);
				startTime = f2.format(dayDate) + " 00:00:00";
				endTime = f2.format(dayDate) + " 23:59:59";
			} catch (ParseException e) {
				logger.info("日期格式转换错误");
			}
		} else if("week".equals(viewtype) || "month".equals(viewtype)) {
			Date endDate = null;
			if(StringUtils.isNotBlank(txtdatetimeshow)) {
				String[] times =txtdatetimeshow.replaceAll(" ", "").split("-");
				try {
					//开始日期格式化
					Date startDate = f1.parse(times[0]);
					startTime = f2.format(startDate) + " 00:00:00";
					//结束日期格式化,如果结束日期含有'年',则直接格式化,如果不含有'年',则需先拼接日期格式再格式化
					if(times[1] != null && (times[1].indexOf("年") != -1)){
						endDate = f1.parse(times[1]);
					} else {
						String end = times[0].substring(0, 5) + "" + times[1];
						endDate = f1.parse(end);
					}
					endTime = f2.format(endDate) + " 23:59:59";
				} catch (ParseException e) {
					logger.info("日期格式转换错误");
				}
			}
		}
		affair.setBeginTime(DateUtils.parse(startTime));
		affair.setEndTime(DateUtils.parse(endTime));
		//根据条件查询日程信息
		affairList = affairService.getListByCondition(affair);
		request.setAttribute("affairList", JSONUtils.write(affairList));
		request.setAttribute("viewtype", viewtype);
		long end = System.currentTimeMillis();
		logger.info("index use time:"+ (end - start));
		return "base/schedule/schedule_index";
	}
	
	/**
	 * 查询日程信息
	 * @param affair
	 * @return
	 */
	@RequestMapping("/getList")
	public void getPageList( HttpServletResponse response, Affair affair, Model model){
		logger.info("getList pull start...");
		long start = System.currentTimeMillis();
		Map<String, String> result = new HashMap<String, String>();
		List<Affair> affairList = new ArrayList<Affair>();
		affairList = affairService.getListByCondition(affair);
		model.addAttribute("affairList", affairList);
		result.put("code", "0000");
		result.put("message","操作成功");
		result.put("affairList", JSONUtils.write(affairList));
		long end = System.currentTimeMillis();
		logger.info("getList use time:"+ (end - start));
		HttpWriter.writerJson(response, JSON.toJSONString(result));
	}
	
	/**
	 * 新增日程信息
	 * @param request
	 * @param response
	 * @param affair
	 */
	@RequestMapping("/add.htm")
	public void add(HttpServletRequest request, HttpServletResponse response, String param){
		logger.info("addAffair pull start...");
		long start = System.currentTimeMillis();
		try {
			param = URLDecoder.decode(param, "utf-8");
		} catch (UnsupportedEncodingException e) {
			
		}
		JSONArray jsonArray = JSONArray.fromObject(param);
		Affair affair = new Affair();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put(jsonObject.getString("name"), jsonObject.getString("value"));
			if(map.get("CalendarTitle") != null && !"".equals(map.get("CalendarTitle"))) {
				affair.setContent(map.get("CalendarTitle"));
			}
			if(map.get("CalendarStartTime") != null && !"".equals(map.get("CalendarStartTime"))) {
				affair.setBeginTime(DateUtils.parse(map.get("CalendarStartTime"), DateUtils.FORMAT_LONG_MM));
			}
			if(map.get("CalendarEndTime") != null && !"".equals(map.get("CalendarEndTime"))) {
				affair.setEndTime(DateUtils.parse(map.get("CalendarEndTime"), DateUtils.FORMAT_LONG_MM));
			}
		}
		Map<String, String> returnMap = new HashMap<String, String>();
		affair.setDelFlag(Affair.STATUS_NORMAL);
		affair.setCreateTime(new Date());
		int ret = affairService.save(affair);
		if(ret > 0) {
			returnMap.put("code", "0000");
			returnMap.put("message","操作成功");
		} else {
			returnMap.put("code", "9999");
			returnMap.put("message","操作失败");
		}
		HttpWriter.writerJson(response, JSON.toJSONString(returnMap));
		long end = System.currentTimeMillis();
		logger.info("addAffair use time:"+ (end - start));
	}
	
	/**
	 * 修改日程信息
	 * @param request
	 * @param response
	 * @param affair
	 */
	@RequestMapping("/update.htm")
	public void update(HttpServletRequest request, HttpServletResponse response,  String affairId, String contentTitle){
		logger.info("update pull start...");
		long start = System.currentTimeMillis();
		try {
			contentTitle = URLDecoder.decode(contentTitle, "utf-8");
		} catch (UnsupportedEncodingException e) {
			
		}
		//获取当前登录人
		User user = userService.getCurrentUser();
		Map<String, String> returnMap = new HashMap<String, String>();
		Affair affair = new Affair();
		affair.setId(Long.valueOf(affairId));
		affair.setContent(contentTitle);
		affair.setUpdateUser(user.getEmployee() != null ? user.getEmployee().getCnName() : "");
		affair.setUpdateTime(new Date());
		int ret = affairService.updateById(affair);
		if(ret > 0) {
			returnMap.put("code", "0000");
			returnMap.put("message","操作成功");
		} else {
			returnMap.put("code", "9999");
			returnMap.put("message","操作失败");
		}
		HttpWriter.writerJson(response, JSON.toJSONString(returnMap));
		long end = System.currentTimeMillis();
		logger.info("update use time:"+ (end - start));
	}
	
	/**
	 * 删除
	 * @param request
	 * @param response
	 * @param affairId
	 * @return
	 */
	@RequestMapping("/delete.htm")
	public void delete(HttpServletRequest request, HttpServletResponse response, String affairId){ 
		logger.info("delete pull start...");
		long start = System.currentTimeMillis();
		Map<String, String> result = new HashMap<String, String>();
		Affair affair = new Affair();
		affair.setId(Long.valueOf(affairId));
		affair.setDelFlag(Affair.STATUS_DELETE);
		affair.setUpdateTime(new Date());
		int ret = affairService.updateById(affair);
		if(ret > 0) {
			result.put("code", "0000");
			result.put("message", "操作成功");
		} else {
			result.put("code", "9999");
			result.put("message", "操作失败");
		}
		long end = System.currentTimeMillis();
		logger.info("delete use time:"+ (end - start));
		HttpWriter.writerJson(response, JSON.toJSONString(result));
	}
	
}
