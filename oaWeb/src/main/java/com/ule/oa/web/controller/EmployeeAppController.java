package com.ule.oa.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.mapper.UserMapper;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmployeeAppService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.web.util.ReturnUrlUtil;

@Controller
@RequestMapping("employeeApp")
public class EmployeeAppController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmployeeAppService employeeAppService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EmployeeService employeeService;
	@Resource
	private EmpPositionService empPositionService;
	@Resource
	private ConfigService configService;
	@Resource
	private DepartService departService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private UserMapper userMapper;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	
	/**
	  * addressList(通讯录页面)
	  * @Title: addressList
	  * @Description: 通讯录页面
	  * @param request
	  * @return
	  * @throws OaException    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@RequestMapping("/addressList.htm")
	@ResponseBody
	public ModelAndView addressList(HttpServletRequest request, String urlType) throws OaException{
		request.setAttribute("currentCompanyId", companyService.getCurrentCompanyId());
		request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		return new ModelAndView("base/addressList/employee_address_list");
	} 
	
	/**
	  * index(员工查询)
	  * @Title: index
	  * @Description: 员工查询
	  * @param request
	  * @return
	  * @throws OaException    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@RequestMapping("/index.htm")
	@ResponseBody
	public ModelAndView index(HttpServletRequest request, String urlType) throws OaException{
		request.setAttribute("currentCompanyId", companyService.getCurrentCompanyId());
		request.setAttribute("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		return new ModelAndView("base/employee/employee_index_app");
	}
	
	/**
	  * getPageList(分页查询员工信息)
	  * @Title: getPageList
	  * @Description: 分页查询员工信息
	  * @param position
	  * @return    设定文件
	  * PageModel<employee>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<EmployeeApp> getPageList(EmployeeApp employeeApp,Integer pageNo,Integer pageSize,String classDate){
		employeeApp.setPage(pageNo);
		employeeApp.setRows(pageSize);
		
		PageModel<EmployeeApp> pm = new PageModel<EmployeeApp>();
		pm.setRows(new java.util.ArrayList<EmployeeApp>());
		pm.setTotal(0);
		pm.setPageNo(1);
		
		try {
			long startTime = System.currentTimeMillis();
			logger.info("查询员工信息开始... ...");
			
			pm = employeeAppService.getPageList(employeeApp);
			if(classDate!=null&&!"".equals(classDate)){
				Long whetherScheduleId = null;
				Config configCondition = new Config();
				configCondition.setCode("whetherScheduling");//是否排班
				List<Config> whetherSchedulingList = configService.getListByCondition(configCondition);
	            for(Config config:whetherSchedulingList){
					if("yes".equals(config.getDisplayCode())){
						whetherScheduleId = config.getId();
						break;
					}
				}
				//去除部门负责人和排班人
				List<EmployeeApp> result = new ArrayList<EmployeeApp>();
				for(EmployeeApp app:pm.getRows()){
					if(departService.checkLeaderIsDh(app.getId())){
						continue;
					}
					if(whetherScheduleId!=null&&whetherScheduleId.equals(app.getWhetherScheduling())){
						continue;
					}
					result.add(app);
				}
				pm.setRows(result);
			}
			long endTime = System.currentTimeMillis();
			logger.info("查询员工信息结束,耗时={}",(endTime - startTime) / 1000+"秒");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	  * getPageList(分页查询员工信息)
	  * @Title: getPageList
	  * @Description: 分页查询员工信息
	  * @param position
	  * @return    设定文件
	  * PageModel<employee>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getPageListByCondition.htm")
	public PageModel<EmployeeApp> getPageListByCondition(EmployeeApp employeeApp,Integer pageNo,Integer pageSize,String classDate){
		employeeApp.setPage(pageNo);
		employeeApp.setRows(pageSize);
		
		PageModel<EmployeeApp> pm = new PageModel<EmployeeApp>();
		pm.setRows(new java.util.ArrayList<EmployeeApp>());
		pm.setTotal(0);
		pm.setPageNo(1);
		
		try {
			long startTime = System.currentTimeMillis();
			logger.info("查询员工信息开始... ...");
			
			pm = employeeAppService.getPageList(employeeApp);
			//去除部门负责人和排班人
			List<EmployeeApp> result = new ArrayList<EmployeeApp>();
			for(EmployeeApp app:pm.getRows()){
				if(departService.checkLeaderIsDh(app.getId())){
					continue;
				}
				EmployeeClass employeeClass = new EmployeeClass();
				employeeClass.setEmployId(app.getId());
				employeeClass.setClassDate(DateUtils.parse(classDate, DateUtils.FORMAT_SHORT));
				EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
				if(empClass!=null){
					continue;
				}
				result.add(app);
			}
			pm.setRows(result);
			long endTime = System.currentTimeMillis();
			logger.info("查询员工信息结束,耗时={}",(endTime - startTime) / 1000+"秒");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	  * uploadPic(在职员工管理页面--上传照片)
	  * @Title: uploadPic
	  * @Description: 在职员工管理页面--上传照片
	  * @param file
	  * @param employee
	  * @return    设定文件
	  * JSON    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/uploadPic.htm")
	public Map<String,Object> uploadPic(@RequestParam("file") CommonsMultipartFile file,EmployeeApp employee){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("flag", false);
		
		try{
			map.put("picUrl", employeeAppService.uploadPic(file,employee));
			map.put("flag", true);
			map.put("msg","上传照片成功");
		}catch(Exception e){
			map.put("msg", e.getMessage());
		}
		return map;
	}
	
	/**
	 * @throws OaException 
	 * @throws IOException 
	  * toEmployeeCheck(跳转到查看页面)
	  * @Title: toEmployeeCheck
	  * @Description: 跳转到查看页面
	  * @param employee
	  * @return    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/toEmployeeCheck.htm")
	public ModelAndView toEmployeeCheck(HttpServletResponse response,Employee employee,String urlType) throws IOException, OaException{
		String authority = null;//权限类型，hr或者other
		Long employeeId = employee.getId();
		Long loginEmployeeId = employeeService.getCurrentEmployeeId();
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		if(null == loginEmployeeId){
			response.sendError(404, "当前请求无效");
		}else{
			paraMap.put("id",employeeId);
			authority = getEmpAuthority(loginEmployeeId);
			paraMap.put("authority", authority);
			paraMap.put("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		}
		
		return new ModelAndView("base/employee/employee_check_app",paraMap);//可编辑页面
		//return new ModelAndView("base/employee/employee_check_app_index",paraMap);//仅仅可查看页面
	}
	
	//返回个人履历
	@ResponseBody
	@RequestMapping("/toMyInfoCheck.htm")
	public ModelAndView toMyInfoCheck(HttpServletResponse response,String urlType) throws IOException, OaException{
		String authority = null;//权限类型，hr或者other
		Long employeeId = employeeService.getCurrentEmployeeId();
		Map<String,Object> paraMap = new HashMap<String,Object>();
		
		if(null == employeeId){
			response.sendError(404, "当前请求无效");
		}else{
			paraMap.put("id",employeeId);
			authority = getEmpAuthority(employeeId);
			paraMap.put("authority", authority);
			paraMap.put("returnUrl", ReturnUrlUtil.getReturnUrl(urlType));
		}
		
		return new ModelAndView("base/employee/employee_check_app",paraMap);//可编辑页面
		//return new ModelAndView("base/employee/employee_check_app_index",paraMap);//仅仅可查看页面
	}
	
	private String getEmpAuthority(Long loginEmployeeId){
		String authority="other";
		//查看该人员部门是否具有hr权限,经周园园确认，只有HRBP助理，人力资源助理，人力资源及行政总监，人力资源经理，高级HRBP，HRBP 这6个岗位具有权限
		EmpPosition pModel = new EmpPosition();
		pModel.setEmployeeId(loginEmployeeId);
		EmpPosition empPosition = empPositionService.getByCondition(pModel);
		String positionId = empPosition.getPositionId().toString();
		
		Config cModel = new Config();
		cModel.setCode("empRecordPosition");
		Config config = configService.getByCondition(cModel);
		if(null != config){
			String displayCode = config.getDisplayCode();
			String[] displayCodeArr = displayCode.split(",");
			Boolean contain = ArrayUtils.contains(displayCodeArr,positionId);
			if(contain){
				authority="hr";
			}else{
				
			}
		}else{
			
		}
		
		return authority;
	}
	
	/**
	 * @throws IOException 
	 * @throws OaException 
	  * getEmpInfo(跳转到查看页面)
	  * @Title: getEmpInfo
	  * @Description: 跳转到查看页面
	  * @param employee
	  * @return    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getEmpInfo.htm")
	public EmployeeApp getEmpInfo(EmployeeApp employee){
		return employeeAppService.getEmpInfo(employee);
	}
	
	/**
	 * @throws Exception 
	  * updateEmpBaseInfo(更新-基本信息)
	  * @Title: updateEmpBaseInfo
	  * @Description: 更新员工基本信息
	  * @param employee
	  * @return    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/updateEmpBaseInfo.htm")
	public boolean updateEmpBaseInfo(EmployeeApp employee) throws Exception{
		return employeeAppService.updateEmpBaseInfo(employee);
	}
	
	/**
	  * getEmpMobileByUserName(根据用户名获取员工电话)
	  * @Title: getEmpMobileByUserName
	  * @Description: 根据用户名获取员工电话
	  * @param userName
	  * @return
	  * @throws OaException    设定文件
	  * EmployeeApp    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getEmpMobileByUserName.htm")
	public JSON getEmpMobileByUserName(String userName){
		try {
			return JsonWriter.successfulResult(employeeAppService.getEmpMobileByUserName(userName));
		} catch (OaException e) {
			return JsonWriter.failedMessage(e.getMessage());
		}
	}
	
	/**
	  * 查看详细信息
	  * @Title: toPersonDetail
	  * @Description: 跳转到查看页面
	  * @param employee
	  * @return    设定文件
	  * ModelAndView    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/toPersonDetail.htm")
	public ModelAndView toPersonDetail(Employee employee){
		ModelMap map = new ModelMap(); 
		
		Employee empDetail = employeeService.getEmpDetailByCondition(employee);	
		
		if(null != empDetail){
			map.put("picUrl", empDetail.getPicture());
			map.put("cnName", empDetail.getCnName());
			map.put("departName", empDetail.getEmpDepart().getDepart().getName());
			map.put("positionName", empDetail.getEmpPosition().getPosition().getPositionName());	
			map.put("reportToLeader", empDetail.getReportToLeaderName());	
			map.put("extensionNumber", empDetail.getExtensionNumber());
			map.put("email",empDetail.getEmail());
			map.put("mobile", empDetail.getMobile());
			map.put("telephone", empDetail.getTelephone());
		}
		
		return new ModelAndView("base/addressList/person_detail",map);
	}	
	
	/**
	  * 更新基础信息
	  * @Title: updateById
	  * @param employee    设定文件
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/updateById.htm")
	public ModelMap updateById(Employee employee){
		ModelMap map = new ModelMap(); 
		try{
			employeeService.updateById(employee);
			//同步手机号sso
			if(StringUtils.isNotBlank(employee.getMobile())){
				//查询通行证账号
				User user = userMapper.getByEmployeeId(employee.getId());
				if(user!=null){
					HashMap<String, String> paramMap = new HashMap<String,String>();
					Employee old = employeeService.getById(employee.getId());
					paramMap.put("username", user.getUserName());
					paramMap.put("email", old.getEmail());
					paramMap.put("telephone", employee.getMobile());
					if(paramMap.size()>0) {
						
						HttpRequest req = new HttpRequest.Builder().url(ConfigConstants.MODIFY_INFO_URL).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
			    				ContentCoverter.formConvertAsString(paramMap)).build();
			    		HttpResponse rep = client.sendRequest(req);
						String response = rep.fullBody();
						
						logger.info("修改员工信息updateById:"+old.getCnName()+"返回="+response);
					}
				}
			}
			map.put("result", "修改成功！");
		}catch (Exception e){
			map.put("result", "修改失败！");
		}
		return map;	
	}
	
	/**
	  * exportExcel(导出员工列表--测试方法，请勿使用)
	  * @Title: exportExcel
	  * @Description: 导出员工列表--测试方法，请勿使用
	  * @param response    设定文件
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/exportExcel.htm")
	public void exportExcel(HttpServletResponse response){
		HSSFWorkbook workbook = employeeAppService.exportExcel();
		if (null != workbook) {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment; filename=file.xls");
			OutputStream out = null;
			try {
				out = response.getOutputStream();
				workbook.write(out);
				out.flush();
			} catch (IOException e) {
				logger.info("export employee error ioexception", e);
			} finally {
				if (null != out) {
					try {
						out.close();
					} catch (IOException e) {
						logger.info("export employee error ioexception", e);
					}
				}
			}
		}
	}
	
	@ResponseBody
	@RequestMapping("/getEmpByInfo.htm")
	public Map<String,Object> getEmpByInfo(Employee param){
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			if((param.getMobile()==null||"".equals(param.getMobile()))&&(param.getEmail()==null||"".equals(param.getEmail()))){
				result.put("code", "9999");
				result.put("message","手机号和邮箱不能同时为空");
				return result;
			}
			Map<String,Object> emplyee = employeeService.getEmpByInfo(param);
			if(emplyee!=null){
				result.put("code", "0000");
				result.put("employee", emplyee);
			}else{
				result.put("code", "1111");
				result.put("message","查不到员工信息");
			}
		}catch(Exception e){
			result.put("code", "9999");
			result.put("message",e.getMessage());
		}		
       return result;
	}
}
