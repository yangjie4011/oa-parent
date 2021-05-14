package com.ule.oa.base.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.mapper.EmployeeAppMapper;
import com.ule.oa.base.mapper.UserMapper;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.CompanyPositionLevel;
import com.ule.oa.base.po.CompanyPositionSeq;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpContract;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.EmpType;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.CompanyPositionLevelService;
import com.ule.oa.base.service.CompanyPositionSeqService;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpContractService;
import com.ule.oa.base.service.EmpDepartService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmpTypeService;
import com.ule.oa.base.service.EmployeeAppService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.CommonUtils;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.UploadUtil;
import com.ule.oa.common.utils.json.JSONUtils;

@Service
public class EmployeeAppServiceImpl implements EmployeeAppService {
	@Autowired
	private EmployeeAppMapper employeeAppMapper;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmpDepartService empDepartService;
	@Autowired
	private EmpPositionService empPositionService;
	@Autowired
	private DepartService departService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private CompanyPositionLevelService companyPositionLevelService;
	@Autowired
	private CompanyPositionSeqService companyPositionSeqService;
	@Autowired
	private EmpContractService empContractService;
	@Autowired
	private EmpTypeService empTypeService;
	@Autowired
	private UserMapper userMapper;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	
	/**
	  * getPageList(分页查询员工信息)
	  * @Title: getPageList
	  * @Description: 分页查询员工信息
	  * @param employee
	  * @return    设定文件
	  * PageModel<Employee>    返回类型
	  * @throws
	 */
	@Override
	public PageModel<EmployeeApp> getPageList(EmployeeApp employee){
		int page = employee.getPage() == null ? 0 : employee.getPage();
		int rows = employee.getRows() == null ? 0 : employee.getRows();
		
		PageModel<EmployeeApp> pm = new PageModel<EmployeeApp>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = employeeAppMapper.getCount(employee);
		pm.setTotal(total);
		employee.setOffset(pm.getOffset());
		employee.setLimit(pm.getLimit());
		List<EmployeeApp> employees=employeeAppMapper.getPageList(employee);
		//查询部门信息
		//getDepartName(employees);
		
		pm.setRows(employees);
		return pm;
	}
	
	public void getDepartName(List<EmployeeApp> employees){
		
		Depart depart = null;
		for(EmployeeApp employee : employees){
			//userService.getEmpPicByEmp(employee);//设置员工头像
			depart = employee.getDepart();
			
			if(null != depart){
				Long departId = depart.getId();
				
				Depart dp = departService.getById(departId);
				if(null != dp){
					if(dp.getType() != 1 && null != dp.getParentId())
					{   
						dp.setName(setDepartName(dp, dp.getName()));
					}
					
					employee.setDepart(dp);
				}
			}
			
			//封装职位
			Position position = positionService.getByEmpId(employee.getId());
			employee.setPosition(position);
		}
	}
	
	/**
	  * getDepartName(根据部门id获取一级部门+当前部门)
	  * @Title: getDepartName
	  * @Description: 根据部门id获取一级部门+当前部门
	  * @param departId
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	public String getDepartName(Long departId){
		Depart dp = departService.getById(departId);
		if(null != dp){
			if(dp.getType() != 1 && null != dp.getParentId())
			{   
				return setDepartName(dp, dp.getName());
			}else{
				return dp.getName();
			}
		}else{
			return "";
		}
	}
	
	public String setDepartName(Depart parentDepart,String childDepartName){
		Depart model = new Depart();
		model.setId(parentDepart.getParentId());
		Depart dp = departService.getByCondition(model);
		
		if(null != dp){
			if(dp.getType().intValue() == 1 || null == dp.getParentId()){//当前查找的节点是一级部门或者当前部门是挂在虚拟部门下面
				return dp.getName() + "_" + childDepartName;
			}else{
				return setDepartName(dp, childDepartName);
			}
		}else{
			return parentDepart.getName();
		}
	}
	
	/**
	  * getById(根据员工表主键获取员工信息)
	  * @Title: getById
	  * @Description: 根据员工表主键获取员工信息
	  * @param id
	  * @return    设定文件
	  * Employee    返回类型
	  * @throws
	 */
	@Override
	public EmployeeApp getById(Long id){
		return employeeAppMapper.getById(id);
	}
	
	/**
	  * getCurrEmp(查看当前员工信息)
	  * @Title: getCurrEmp
	  * @Description: 查看当前员工信息
	  * @return    设定文件
	  * Employee    返回类型
	  * @throws
	 */
	@Override
	public EmployeeApp getCurrEmp(){
		return employeeAppMapper.getById(getCurrentEmployeeId());
	}
	
	/**
	  * getCurrentEmployeeId(获取当前登录员工id)
	  * @Title: getCurrentEmployeeId
	  * @Description: 获取当前登录员工id
	  * @return    设定文件
	  * Long    返回类型
	  * @throws
	 */
	@Override
	public Long getCurrentEmployeeId(){
		User user = userService.getCurrentUser();
		if(user == null){
			return null;
		}
		
		return user.getEmployeeId();
	}
	
	/**
	  * getEmpInfo(获取员工详细信息)
	  * @Title: getEmpInfo
	  * @Description: 获取员工详细信息
	  * @param employee
	  * @return    设定文件
	  * EmployeeApp    返回类型
	  * @throws
	 */
	public EmployeeApp getEmpInfo(EmployeeApp employee){
		CompanyConfig companyConfig = null;
		Config config = null;
		EmployeeApp emp = getById(employee.getId());
		
		//查找员工上级（//1.根据员工id找到部门id，2.根据部门id找到部门负责人，3：根据部门负责人找到员工）
		if(null != emp){
			if(emp.getWorkAddressProvince()==null){
				emp.setWorkAddressProvince("");
			}
			if(emp.getWorkAddressCity()==null){
				emp.setWorkAddressCity("");
			}
			EmpDepart ed = new EmpDepart();
			ed.setEmployeeId(employee.getId());
			ed = empDepartService.getByCondition(ed);
		
			if(null != ed){
				Depart d = departService.getById(ed.getDepartId());
				if(null != d){
					EmployeeApp eApp = getById(d.getLeader());
					if(null != eApp){
						emp.setLeaderName(eApp.getCnName());
					}
				}
			}
			
			final String empPic = userService.getEmpPicByEmp(emp);
			emp.setPicture(empPic);
		}else{
			emp = new EmployeeApp();
			return emp;
		}
		
		//员工类型
		if(null != emp.getEmpTypeId()){
			
			EmpType empType = new EmpType();
			empType.setId(emp.getEmpTypeId());
			EmpType e = empTypeService.getByCondition(empType);
			
			if(null != e){emp.setEmpTypeName(e.getTypeCName());}
		}
		
		//政治面貌
		if(null != emp && null != emp.getPoliticalStatus() && StringUtils.isBlank(emp.getPoliticalStatusOther())){
			config = new Config();
			config.setId(emp.getPoliticalStatus());
			
			Config c = configService.getByCondition(config);
			if(null != c){emp.setPoliticalName(c.getDisplayName());}
		}else{
			emp.setPoliticalName(emp.getPoliticalStatusOther());
		}
		
		//文化程度
		if(null != emp ){
			if(null != emp.getDegreeOfEducation() && StringUtils.isBlank(emp.getDegreeOfEducationOther())){
				config = new Config();
				config.setId(emp.getDegreeOfEducation());
				
				Config c = configService.getByCondition(config);
				if(null != c){emp.setDegreeOfEducationName(c.getDisplayName());}
			}else{
				emp.setDegreeOfEducationName(emp.getDegreeOfEducationOther());
			}
		}
		
		//行业相关性
		if(null != emp && null != emp.getIndustryRelevance() && StringUtils.isBlank(emp.getIndustryRelevanceOther())){
			companyConfig = new CompanyConfig();
			companyConfig.setId(emp.getIndustryRelevance());
			companyConfig.setCompanyId(emp.getCompanyId());
		
			CompanyConfig cc = companyConfigService.getByCondition(companyConfig);
			if(null != cc){emp.setIndustryRelevanceName(cc.getDisplayName());}
		}else{
			emp.setIndustryRelevanceName(emp.getIndustryRelevanceOther());
		}
		
		//婚姻状况
		if(null != emp && null != emp.getMaritalStatus()){
			config = new Config();
			config.setId(emp.getMaritalStatus());
			
			Config c = configService.getByCondition(config);
			if(null != c){emp.setMaritalStatusName(c.getDisplayName());}
		}
		
		//民族
		if(null != emp && null != emp.getNation()){
			config = new Config();
			config.setId(emp.getNation());
			
			Config c = configService.getByCondition(config);
			if(null != c){emp.setNationName(c.getDisplayName());}
		}
		
		//国籍
		if(null != emp && null != emp.getCountry() && StringUtils.isBlank(emp.getCountryOther())){
			companyConfig = new CompanyConfig();
			companyConfig.setId(emp.getCountry().longValue());
			
			CompanyConfig c = companyConfigService.getByCondition(companyConfig);
			if(null != c){emp.setCountryName(c.getDisplayName());}
		}else{
			emp.setCountryName(emp.getCountryOther());
		}
		
		//工时类型
		if(null != emp && null != emp.getWorkType()){
			companyConfig = new CompanyConfig();
			companyConfig.setId(emp.getWorkType());
			
			CompanyConfig cc = companyConfigService.getByCondition(companyConfig);
			if(null != cc){emp.setWorkTypeName(cc.getDisplayName());}
		}
		
		//是否排班
		if(null != emp.getWhetherScheduling()){
			config = new Config();
			config.setId(emp.getWhetherScheduling());
			
			Config c = configService.getByCondition(config);
			if(null != c){emp.setWhetherSchedulingName(c.getDisplayName());}
		}
		
		//封装公司
		Company co = new Company();
		if(null != emp && null != emp.getCompanyId()){
			Company c = new Company();
			c.setId(emp.getCompanyId());
			co = companyService.getByCondition(c);
			
			if(null == co){co = new Company();}
		}
		emp.setCompany(co);
		
		//查询部门信息
		Depart depart = new Depart();
		if(null != emp && null != emp.getId()){
			EmpDepart empDepart = new EmpDepart();
			empDepart.setEmployeeId(emp.getId());
			EmpDepart ed = empDepartService.getByCondition(empDepart);
			
			if(null != ed){
				depart = departService.getById(ed.getDepartId());
				
				if(null != depart){
					if(null != depart.getLeader()){
						employee = this.getById(depart.getLeader());
						
						if(null != employee){depart.setLeaderName(employee.getCnName());}
					}
				}else{
					depart = new Depart();
				}
			}
		}
		emp.setDepart(depart);
		
		//查询职位信息
		Position po = new Position();
		CompanyPositionLevel cp = new CompanyPositionLevel();
		CompanyPositionSeq cps = new CompanyPositionSeq();
		if(null != emp && null != emp.getId()){
			EmpPosition empPosition = new EmpPosition();
			empPosition.setEmployeeId(emp.getId());
			
			EmpPosition ep = empPositionService.getByCondition(empPosition);
			if(null != ep && null != ep.getPositionId()){
				po = positionService.getById(ep.getPositionId());
				
				if(null != po){
					//职级
					CompanyPositionLevel pl = new CompanyPositionLevel();
					pl.setId(po.getPositionLevelId());
					pl.setCompanyId(po.getCompanyId());
					
					cp = companyPositionLevelService.getByCondition(pl);
					if(null == cp){cp = new CompanyPositionLevel();}
					
					//职位序列
					CompanyPositionSeq ps = new CompanyPositionSeq();
					ps.setId(po.getPositionSeqId());
					ps.setCompanyId(po.getCompanyId());
					
					cps = companyPositionSeqService.getByCondition(ps);
					if(null == cps){cps = new CompanyPositionSeq();}
					
				}else{
					po = new Position();
				}
			}
		}
		po.setCompanyPositionSeq(cps);
		po.setCompanyPositionLevel(cp);
		emp.setPosition(po);
		
		//合同信息
		EmpContract empContract = new EmpContract();
		empContract.setCompanyId(emp.getCompanyId());
		empContract.setEmployeeId(emp.getId());
		empContract.setIsActive(EmpContract.IS_ACTIVE_NORMAL);
		EmpContract contract = empContractService.getByCondition(empContract);
		if(null == contract){
			contract = new EmpContract();
		}
		emp.setEmpContract(contract);
		
		if(null != emp.getReportToLeader()){//汇报人
			EmployeeApp leaderEmp = employeeAppMapper.getById(emp.getReportToLeader());
			emp.setReportToLeaderName(leaderEmp.getCnName());
		}
		
		return emp;
	}
	
	/**
	 * @throws Exception 
	  * updateEmpBaseInfo(更新员工基本信息)
	  * @Title: updateEmpBaseInfo
	  * @Description: 更新员工基本信息
	  * @param employee
	  * @return    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean updateEmpBaseInfo(EmployeeApp employee) throws Exception{
		boolean flag = false;
		
		try{
			//TODO:更改手机号或者邮箱的时候同时调用SSO修改接口更新信息
			//MODIFY_PWD_URL   email,telephone,离职状态:status
			if(StringUtils.isNotBlank(employee.getTelephone())||
					StringUtils.isNotBlank(employee.getEmail())) {
				//查询通行证账号
				User user = userMapper.getByEmployeeId(employee.getId());
				if(user!=null){
					HashMap<String, String> paramMap = new HashMap<String,String>();
					EmployeeApp old = this.employeeAppMapper.getById(employee.getId());
					paramMap.put("username", user.getUserName());
					paramMap.put("email", employee.getEmail());
					paramMap.put("telephone", old.getMobile());
					if(paramMap.size()>0) {
						
						HttpRequest req = new HttpRequest.Builder().url(ConfigConstants.MODIFY_INFO_URL).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
			    				ContentCoverter.formConvertAsString(paramMap)).build();
			    		HttpResponse rep = client.sendRequest(req);
						String response = rep.fullBody();
						
						Map<?, ?> responseMap = JSONUtils.readAsMap(response);
						String resultCode = (String)responseMap.get("errorMsg");
						if(!"".equals(resultCode)){
							throw new OaException("修改失败:员工信息同步到SSO系统中出错,请联系开发人员！");
						}
					}
				}
			}
			int updatCount = employeeAppMapper.updateById(employee);
			if(updatCount == 0){
				throw new OaException("当前员工信息已经被其它人修改过，请重新编辑员工信息");
			}
			flag = true;
		}catch(Exception e){
			throw e;
		}
		
		return flag;
	}
	
	/**
	  * updateEmpInfo(更新员工基本信息-不带事务)
	  * @Title: updateEmpInfo
	  * @Description: 更新员工基本信息-不带事务
	  * @param employee
	  * @return
	  * @throws Exception    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	@Override
	public boolean updateEmpInfo(EmployeeApp employee) throws Exception{
		boolean flag = false;
		
		try{
			int updatCount = employeeAppMapper.updateById(employee);
			if(updatCount == 0){
				throw new OaException("当前员工信息已经被其它人修改过，请重新编辑员工信息");
			}
			flag = true;
		}catch(Exception e){
			throw e;
		}
		
		return flag;
	}
	
	/**
	  * uploadPic(上传员工照片)
	  * @Title: uploadPic
	  * @Description: 上传员工照片
	  * @param file
	  * @param employee
	  * @throws Exception    设定文件
	  * void    返回类型
	  * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public String uploadPic(CommonsMultipartFile file, EmployeeApp employee) throws Exception{
		EmployeeApp emp = employeeAppMapper.getById(employee.getId());
		String picUlr = "";
		long defaultSize = 10240000;//默认上传大小10M
		
		if(null != emp){
	 		String filename = file.getOriginalFilename();
	 		long fileSize = file.getSize();
	 		if(fileSize > defaultSize){
	 			throw new OaException("请上传10M以下的头像!");
	 		}
	 		//check文件上传类型
	 		if(CommonUtils.checkFileSuffix(ConfigConstants.PIC_UPLOAD_TYPE, filename)){
				String fileFullName = "uleOA/pic/employee/" + DateUtils.getNow().replace(" ", "").replace("-", "").replace(":", "") + "/" + filename;
				File temfile = null;
				try {
					temfile = new File(filename);
					FileUtils.writeByteArrayToFile(temfile, UploadUtil.fileToByteArray(file));
					//上传图片，返回图片url
					String result = UploadUtil.uploadToQiNiu(temfile, fileFullName,	ConfigConstants.QINIU_HOST);
					Map<String, String> resultMap = (Map<String, String>) JSONUtils.readAsMap(result);
					picUlr = resultMap.get("url");
					if(null != resultMap && "success".equals(resultMap.get("status")) && StringUtils.isNotBlank(picUlr)){
						//保存员工信息
						emp.setPicture(picUlr);
						emp.setUpdateTime(new Date());
						emp.setUpdateUser(userService.getCurrentAccount());
						employeeAppMapper.updateById(emp);
						
						User user = userService.getCurrentUser();
						user.getEmployee().setPicture(picUlr);
					}else{
						throw new Exception("保存失败!");
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					if (null != temfile) {
						if(!temfile.delete()) {
							
						}
					}
				}
	 		}else{
	 			throw new OaException("系统不支持当前上传图片类型,请重新选择图片!");
	 		}
			
			return picUlr;
		}else{
			throw new OaException("当前编辑的员工不存在!");
		}
	}
	
	/**
	  * getListByCondition(根据条件获取所有员工信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有员工信息
	  * @param employee
	  * @return    设定文件
	  * List<EmployeeApp>    返回类型
	  * @throws
	 */
	public List<EmployeeApp> getListByCondition(EmployeeApp employee){
		return employeeAppMapper.getListByCondition(employee);
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
	public EmployeeApp getEmpMobileByUserName(String userName) throws OaException{
		User oldU = new User();
		oldU.setUserName(userName);
		oldU = userService.getByCondition(oldU);
		
		Long empId = 0L;
		if(null != oldU && null != oldU.getId()){
			empId = oldU.getEmployeeId();
		}else{
			throw new OaException("您输入的账户不对，请重新输入");
		}
		  
		//验证输入的手机号和入职登记的手机号是否一致
		EmployeeApp emp = getById(empId);
		if(null == emp || null == emp.getId()){
			throw new OaException("您输入的账户没有入职信息");
		}
		String mobile = emp.getMobile();
		if(StringUtils.isBlank(mobile)){
			throw new OaException("您入职登记的时候没有填写手机号,无法修改密码");
		}
		
		return emp;
	}
	
	/**
	  * updateAllEmpOurAge(更新所有员工的司龄)
	  * @Title: updateAllEmpOurAge
	  * @Description: 更新所有员工的司龄
	  * @return
	  * @throws OaException    设定文件
	  * EmployeeApp    返回类型
	  * @throws
	 */ 
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public int updateAllEmpOurAge(int days) throws OaException{
		return employeeAppMapper.updateAllEmpOurAge(days);
	}
	
	
	@Override
	public HSSFWorkbook exportExcel(){
		List<EmployeeApp> employees = employeeAppMapper.getListByCondition(null);
		List<Object[]> dataList = new ArrayList<Object[]>();//需要导出的数据
		String[] titleArray = new String[] { "员工编号","员工姓名","英文名" };//excel标题
		
		for(EmployeeApp emp : employees){
			Object[] data = new Object[] { emp.getCode(), emp.getCnName(), emp.getEngName() };
			dataList.add(data);
		}
		
		return ExcelUtil.exportExcel(dataList, titleArray, "员工列表");
	}
}
