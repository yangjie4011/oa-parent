package com.ule.oa.base.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.http.HttpUtils;
import com.ule.oa.common.utils.json.JSONUtils;
import com.ule.oa.web.service.impl.BaseServiceTest;

/**
  * @ClassName: UserServiceImplTest
  * @Description: 登录用户单元测试
  * @author minsheng
  * @date 2018年1月11日 下午5:39:13
 */
public class UserServiceImplTest extends BaseServiceTest{
	@Autowired
	private UserService userService;
	@Autowired
	private CompanyConfigService companyConfigService;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Test
	public void testGetEmpPic(){
		Employee emp = new Employee();
		emp.setEmail("minsheng@ule.com");
		
		try{
			//获取随心邮用户头像接口地址
			CompanyConfig companyConfig = new CompanyConfig();
			companyConfig.setCode("sxyEmpPic");
			CompanyConfig cc = companyConfigService.getByCondition(companyConfig);
			String picUrl = "";
			if(null != cc && StringUtils.isNotBlank(cc.getDisplayCode())){
				picUrl = cc.getDisplayCode();
			}else{
				return;
			}
			  
			HashMap<String, String> paramMap = new HashMap<String,String>();
			paramMap.put("username", emp.getEmail());
			String picReStr = HttpUtils.sendByPost(picUrl, paramMap,false);
				
			Map<?, ?> picReMap = JSONUtils.readAsMap(picReStr);
				
			String empPic = "";
			Map<?, ?> msgReMap = (Map<?, ?>)picReMap.get("ret_msg");
			if("000".equals((String)picReMap.get("ret_code"))){
				empPic = (String)msgReMap.get("headimgurl");
			}else{
				logger.error("调用随心邮获取用户头像失败,原因={}",(String)msgReMap.get("message") );
			}
			
			System.out.println(empPic);
		}catch(Exception e){
			logger.error("获取随心邮用户头像失败,原因={}",e.getMessage());
		}
	}
}
