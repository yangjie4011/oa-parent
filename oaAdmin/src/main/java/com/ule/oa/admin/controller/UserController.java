package com.ule.oa.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.UserService;

/**
  * @ClassName: UserController
  * @Description: 用户信息
  * @author minsheng
  * @date 2017年5月11日 下午2:49:42
 */
@Controller
@RequestMapping("/user")
public class UserController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserService userService;
	
	@RequestMapping("/getByCondition.htm")
	@ResponseBody
	public User getByCondition(User user){
		return userService.getByCondition(user);
	}
	
	/**
	  * toModifyPwd(跳转到修改密码页面)
	  * @Title: toModifyPwd
	  * @Description: 跳转到修改密码页面
	  * @return    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@RequestMapping("/toModifyPwd.htm")
	@ResponseBody
	public ModelAndView toModifyPwd(){
		return new ModelAndView("base/user/modifyPwd");
	}
	
	
	/**
	  * toResetPwd(跳转到重置密码页面)
	  * @Title: toResetPwd
	  * @Description: 跳转到重置密码页面
	  * @return    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@RequestMapping("/toResetPwd.htm")
	@ResponseBody
	public ModelAndView toResetPwd(){
		return new ModelAndView("base/user/resetPwd");
	}

	
}
