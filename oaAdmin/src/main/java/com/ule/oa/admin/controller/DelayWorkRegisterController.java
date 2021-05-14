package com.ule.oa.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
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
import org.springframework.web.servlet.ModelAndView;
import com.ule.oa.admin.util.IsMenu;
import com.ule.oa.admin.util.IsOperation;
import com.ule.oa.admin.util.ResponseUtil;
import com.ule.oa.base.po.DelayWorkRegister;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.RabcResource;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.DelayWorkRegisterService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.RabcResourceService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;


@Controller
@RequestMapping("delayWork")
public class DelayWorkRegisterController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DepartService departService;
	
	@Autowired
	private RabcResourceService resourceService;
	
	@Autowired
	private DelayWorkRegisterService delayWorkRegisterService;
	
	
	@IsMenu
	@RequestMapping("/delayWorkRegister.htm")
	public ModelAndView delayWorkRegister(HttpServletRequest request){

		User user = userService.getCurrentUser();
		List<Depart> departList = departService.getSubordinateDepartList();
		String thisMonth = DateUtils.getYearAndMonth(new Date());
		//查询员工所有菜单权限
    	List<RabcResource> resList = resourceService.getAllAdminTabListByUserId(user.getId());
    	boolean supervisorRegister = false;//主管登记权限标记
    	boolean hrRegister = false;//人事登记权限标记
    	for(RabcResource data:resList){
			if("supervisorRegister".equals(data.getCode())){
				supervisorRegister = true;
			}
			if("hrRegister".equals(data.getCode())){
				hrRegister = true;
			}
    	}
    	request.setAttribute("supervisorRegister", supervisorRegister);
    	request.setAttribute("hrRegister", hrRegister);
		request.setAttribute("departList", departList);
		request.setAttribute("thisMonth", thisMonth);
		return new ModelAndView("base/delayWork/delayWorkRegister");
	}

	
	
	//考勤管理-延时工作管理-延时工作登记-查询
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/getDelayWorkPage.htm") 
	public Map<String,Object> getDelayWorkPage(Long departId,String leaderName,String month,String empCode,String empCnName,Integer page,Integer rows){
	
		Map<String,Object> result = new HashMap<String,Object>();
		//分页
		PageModel<Map<String,Object>> pm=new PageModel<Map<String,Object>>();
		pm.setRows(new java.util.ArrayList<Map<String,Object>>());
		try{
			result = delayWorkRegisterService.getDelayWorkPage(departId,leaderName,month,empCode,empCnName,page,rows);
		}catch(Exception e){
			logger.error("getDelayWorkPage:"+e.getMessage());
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	
	/**
	 * 获取登记详情
	 * @param empId
	 * @param delayDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getDelayWorkDetail.htm") 
	public Map<String,Object> getDelayWorkDetail(Long empId,String delayDate){
	
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			Date date = DateUtils.parse(delayDate,DateUtils.FORMAT_SHORT);
			result = delayWorkRegisterService.getDelayWorkDetail(empId,date);
		}catch(Exception e){
			logger.error("getDelayWorkDetail:"+e.getMessage());
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	
	/**
	 *提交延时工作登记
	 * @param delayWorkRegister
	 * @return
	 */
	//考勤管理-延时工作管理-延时工作登记-提交
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/commitDelayWorkDetail.htm") 
	public Map<String,Object> commitDelayWorkDetail(DelayWorkRegister delayWorkRegister){
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = delayWorkRegisterService.commitDelayWorkDetail(delayWorkRegister);
		}catch(OaException o){
			logger.error("commitDelayWorkDetail:"+o.getMessage());
			result.put("message",o.getMessage());
			result.put("success",false);
		}catch(Exception e){
			logger.error("commitDelayWorkDetail:"+e.getMessage());
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	/**
	 * 确认延时工作登记
	 * @param delayWorkRegister
	 * @return
	 */
	//考勤管理-延时工作管理-延时工作登记-确认
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/confirmDelayWorkDetail.htm") 
	public Map<String,Object> confirmDelayWorkDetail(DelayWorkRegister delayWorkRegister){
	
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = delayWorkRegisterService.confirmDelayWorkDetail(delayWorkRegister);
		}catch(OaException o){
			logger.error("confirmDelayWorkDetail:"+o.getMessage());
			result.put("message",o.getMessage());
			result.put("success",false);
		}catch(Exception e){
			logger.error("confirmDelayWorkDetail:"+e.getMessage());
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	/**
	 * 删除延时工作登记
	 * @param delayWorkRegister
	 * @return
	 */
	//考勤管理-延时工作管理-延时工作登记-删除
	@IsOperation(returnType=false)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/deleteDelayWorkDetail.htm") 
	public Map<String,Object> deleteDelayWorkDetail(DelayWorkRegister delayWorkRegister){
	
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = delayWorkRegisterService.deleteDelayWorkDetail(delayWorkRegister);
		}catch(OaException o){
			logger.error("deleteDelayWorkDetail:"+o.getMessage());
			result.put("message",o.getMessage());
			result.put("success",false);
		}catch(Exception e){
			logger.error("deleteDelayWorkDetail:"+e.getMessage());
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}
	
	 /**
	  * 导出延时工作登记表
	  * @param departId
	  * @param month
	  * @param empCode
	  * @param empCnName
	  * @param response
	  * @param request
	  * @throws IOException
	  */
	//考勤管理-延时工作管理-延时工作登记-导出
	@IsOperation(returnType=true)//需要校验操作权限
	@ResponseBody
	@RequestMapping("/exportDelayWorkPage.htm")
	public void exportDelayWorkPage(Long departId,String leaderName,String month,String empCode,String empCnName,HttpServletResponse response,HttpServletRequest request) throws IOException{
		
		OutputStream os = null;
		try {
			ResponseUtil.setDownLoadExeclInfo(response,request,"延时工作登记表_");
			HSSFWorkbook hSSFWorkbook = delayWorkRegisterService.exportDelayWorkPage(departId,leaderName, month, empCode,empCnName);
			os = response.getOutputStream();
			hSSFWorkbook.write(os);
            os.flush();
            os.close();
		} catch (Exception e) {
			logger.error("导出延时工作登记表失败,原因="+e.toString());
		}finally{
			if(null != os){
				os.flush();
				os.close();
			}
		}
	}
	
	//复核员工月延时工作时长
	@ResponseBody
	@RequestMapping("/toReview.htm")
	public Map<String,Object> toReview(Long empId,String month) throws IOException{
		
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			result = delayWorkRegisterService.toReview(empId,month);
		}catch(Exception e){
			logger.error("toReview:"+e.getMessage());
			result.put("message",e.getMessage());
			result.put("success",false);
		}
		return result;
	}

		

}
