package com.ule.oa.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.User;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.service.EmpMsgService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * @ClassName: 我的消息
 * @Description: 我的消息
 * @author yangjie
 * @date 2017年6月8日
 */
@Controller
@RequestMapping("empMsg")
public class EmpMsgController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserService userService;
	@Autowired
	private EmpMsgService empMsgService;
	
	//我的消息列表页
	@RequestMapping("/index.htm")
	public String index(HttpServletRequest request){
		return "base/empMsg/index";
	}
	
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<EmpMsg> getPageList(EmpMsg userMsg){
		User user = userService.getCurrentUser();
		userMsg.setEmployeeId(user.getEmployeeId());
		PageModel<EmpMsg> pm=new PageModel<EmpMsg>();
		pm.setRows(new java.util.ArrayList<EmpMsg>());
		pm.setTotal(0);
		pm.setPageNo(1);
		
		try {
			pm = empMsgService.getPageList(userMsg);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	 * 分页查询未读
	 * @param userMsg
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getUnReadPageList.htm")
	public PageModel<EmpMsg> getUnReadPageList(EmpMsg userMsg, String flag){
		if("main".equals(flag)) {
			userMsg.setOffset(0);
			userMsg.setLimit(5);
		}
		User user = userService.getCurrentUser();
		userMsg.setEmployeeId(user.getEmployeeId());
		userMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
		PageModel<EmpMsg> pm=new PageModel<EmpMsg>();
		pm.setRows(new java.util.ArrayList<EmpMsg>());
		pm.setTotal(0);
		pm.setPageNo(1);
		try {
			pm = empMsgService.getPageList(userMsg);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	/**
	 * 查询未读消息数量
	 * @param userMsg
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getUnReadCount.htm")
	public String getUnReadCount(EmpMsg userMsg){
		User user = userService.getCurrentUser();
		userMsg.setEmployeeId(user.getEmployeeId());
		userMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
		Integer unReadCount = empMsgService.getCount(userMsg);
		return JSONUtils.write(unReadCount);
	}
	
	//我的消息列表页
	@RequestMapping("/index_mynews.htm")
	public String indexMynews(HttpServletRequest request){
		return "base/empMsg/index_mynews";
	}
	
	
	/**
	 * 分页查询未读
	 * @param userMsg
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getList.htm")
	public PageModel<EmpMsg> getList(EmpMsg userMsg, String flag, Integer pageNo, Integer pageSize){
		userMsg.setOffset(pageNo*pageSize);
		userMsg.setLimit(pageSize);
		User user = userService.getCurrentUser();
		userMsg.setEmployeeId(user.getEmployeeId());
		PageModel<EmpMsg> pm=new PageModel<EmpMsg>();
		pm.setRows(new java.util.ArrayList<EmpMsg>());
		pm.setTotal(0);
		pm.setPageNo(1);
		try {
			pm = empMsgService.getList(userMsg);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pm;
	}
	
	//已读
	@ResponseBody
	@RequestMapping("/read.htm")
	public Map<String, Object> read(EmpMsg userMsg){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			userMsg.setReadFlag(EmpMsg.READ_FLAG_YES);
			userMsg.setReadDate(new Date());
			userMsg.setUpdateUser(user.getEmployee().getCnName());
			userMsg.setUpdateTime(new Date());
			empMsgService.updateById(userMsg);
			map.put("message", "保存成功");
			map.put("success", true);
		}catch(Exception e){
			map.put("message", "保存失败");
			map.put("success",false);
		}
		return map;
	}

}
