package com.ule.oa.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.DailyHeathSignTbl;
import com.ule.oa.base.service.DailyHeathSignService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.RedisUtils;
import com.ule.oa.common.utils.UUIDGenerator;
import com.ule.oa.web.util.Token;

/**
 * @ClassName: DayHeathController
 * @Description: 每日健康打卡
 * @author yangjie
 * @date 2020-02-14
*/
@Controller
@RequestMapping("dayHeath")
public class DayHeathController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserService userService;
	@Autowired
	private DailyHeathSignService dailyHeathSignService;
	
	//每日健康打卡首页
	@RequestMapping("/index.htm")
	@Token(generate=true)//生成token
	public String index(HttpServletRequest request){
		User user = userService.getCurrentUser();
		List<DailyHeathSignTbl> signInfo = dailyHeathSignService
    			.getByEmployeeIdAndSignDate(user.getEmployee().getId(), 1, null);
		request.setAttribute("needBaseInfo",true);
		if(signInfo!=null&&signInfo.size()>0){
			request.setAttribute("needBaseInfo",false);
		}
		return "dayHeath/sign";
	}
	
	//保存
	@ResponseBody
	@RequestMapping("/save.htm")
	public Map<String, Object> save(HttpServletRequest request){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			map = dailyHeathSignService.save(request,user);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"健康打卡-提交："+e.getMessage());
			String uuId = UUIDGenerator.generate();
            try {
				RedisUtils.setString(String.valueOf(user.getEmployee().getId()),uuId,1800000);
				map.put("token", uuId);
			} catch (Exception e1) {
				logger.error(user.getEmployee().getCnName()+"健康打卡-提交："+e1.getMessage());
			}
			map.put("repeatFlag", false);
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/sendMailToNoSignEmp.htm")
	public Map<String, Object> sendMailToNoSignEmp(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			 dailyHeathSignService.sendMailToNoSignEmp();
			 map.put("message", "发送成功");
			 map.put("success",true);
		}catch(Exception e){
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/sendMailToLeaderOfNoSignEmp.htm")
	public Map<String, Object> sendMailToLeaderOfNoSignEmp(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		try{
			 dailyHeathSignService.sendMailToLeaderOfNoSignEmp();
			 map.put("message", "发送成功");
			 map.put("success",true);
		}catch(Exception e){
			map.put("message", e.getMessage());
			map.put("success",false);
			return map;
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/exportReport.htm")
	public void exportReport(String date,HttpServletResponse response) throws IOException{
	
		OutputStream os = null;
		try{
			Date signDate = DateUtils.parse(date, DateUtils.FORMAT_SIMPLE);
			HSSFWorkbook workbook = dailyHeathSignService.exportSignDataByDate(signDate);
			//设置返回类型
			String fileName = URLEncoder.encode("健康打卡报表"+date+".xls","UTF-8");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename="+fileName);
			os = response.getOutputStream();
			if(workbook!=null){
				workbook.write(os);
				os.flush();
				os.close();
			}
		}catch(Exception e){
			logger.error("健康报表导出失败,原因="+e.toString());
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
    }

}
