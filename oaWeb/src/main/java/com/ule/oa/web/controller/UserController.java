package com.ule.oa.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;

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
	
//	/**
//	  * updatePwd(修改密码-针对邮乐用户)
//	  * @Title: updatePwd
//	  * @Description: 修改密码
//	  * @param user
//	  * @return    设定文件
//	  * JSON    返回类型
//	  * @throws
//	 */
//	@ResponseBody
//	@RequestMapping("/modifyPwd.htm")
//	public JSON modifyPwd(User user){
//		try{
//			userService.modifyUleUserPwd(user);
//		}catch(Exception e){
//			return JsonWriter.failedMessage("修改密码失败，原因:"+e.getMessage());
//		}
//		
//		return JsonWriter.successfulJson();
//	}
	
	
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
	
//	/**
//	  * resetPwd(重置密码)
//	  * @Title: resetPwd
//	  * @Description: 重置密码
//	  * @param user
//	  * @return    设定文件
//	  * JSON    返回类型
//	  * @throws
//	 */
//	@ResponseBody
//	@RequestMapping("/resetPwd.htm")
//	public JSON resetPwd(User user){
//		try{
//			userService.resetUleUserPwd(user);
//		}catch(Exception e){
//			return JsonWriter.failedMessage("重置密码失败，原因:"+e.getMessage());
//		}
//		
//		return JsonWriter.successfulJson();
//	}
	
	/**
	  * getEmpPic(获取员工头像)
	  * @Title: getEmpPic
	  * @Description: 获取员工头像
	  * @param empPic
	  * @param email
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getEmpPic.htm")
	public JSON getEmpPic(){
		User user = userService.getCurrentUser();
		Employee emp = user.getEmployee();
		String empPic = userService.getEmpPicByEmp(emp.getPicture(),emp.getEmail());
		
		//首页上显示完图片后，异步将图片信息放入到缓存中
		new Thread(new Runnable() {
			public void run() {
				user.getEmployee().setPicture(empPic);
			}
		}).start();
		
		return JsonWriter.successfulMessage(empPic);
	}
}
