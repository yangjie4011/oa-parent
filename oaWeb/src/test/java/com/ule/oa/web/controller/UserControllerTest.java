package com.ule.oa.web.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.BaseControllerTest;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.exception.SmsException;

public class UserControllerTest extends BaseControllerTest{
	@Autowired
	private UserService userService;

//	@Test
//	public void testModifyPwd() {
//		User user = new User();
//		user.setPassword("@Software2016");
//		user.setUserName("minsheng");
//		
//		try {
//			boolean flag = userService.modifyUleUserPwd(user);
//			
//			System.out.println(flag);
//		} catch (OaException e) {
//			e.printStackTrace();
//		} catch (SmsException e) {
//			e.printStackTrace();
//		}
//	}

////	@Test
//	public void testResetPwd() {
//		User user = new User();
//		user.setUserName("minsheng");
//		
//		try {
//			boolean flag = userService.resetUleUserPwd(user);
//			
//			System.out.println(flag);
//		} catch (OaException e) {
//			e.printStackTrace();
//		} catch (SmsException e) {
//			e.printStackTrace();
//		}
//	}

}
