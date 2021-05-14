package com.ule.oa.base.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import sun.misc.BASE64Encoder;

import com.sun.mail.util.BASE64EncoderStream;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.mapper.CompanyConfigMapper;
import com.ule.oa.base.mapper.CompanyFloorMapper;
import com.ule.oa.base.mapper.CompanyMapper;
import com.ule.oa.base.mapper.CompanyPositionLevelMapper;
import com.ule.oa.base.mapper.ConfigMapper;
import com.ule.oa.base.mapper.EmpAchievementMapper;
import com.ule.oa.base.mapper.EmpAppraiseMapper;
import com.ule.oa.base.mapper.EmpContractMapper;
import com.ule.oa.base.mapper.EmpFamilyMemberMapper;
import com.ule.oa.base.mapper.EmpPostRecordMapper;
import com.ule.oa.base.mapper.EmpSchoolMapper;
import com.ule.oa.base.mapper.EmpTrainingMapper;
import com.ule.oa.base.mapper.EmpTypeMapper;
import com.ule.oa.base.mapper.EmpUrgentContactMapper;
import com.ule.oa.base.mapper.EmpWorkRecordMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.UserMapper;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.CompanyFloor;
import com.ule.oa.base.po.CompanyPositionLevel;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpAchievement;
import com.ule.oa.base.po.EmpAppraise;
import com.ule.oa.base.po.EmpContract;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.EmpPostRecord;
import com.ule.oa.base.po.EmpSchool;
import com.ule.oa.base.po.EmpTraining;
import com.ule.oa.base.po.EmpType;
import com.ule.oa.base.po.EmpUrgentContact;
import com.ule.oa.base.po.EmpWorkRecord;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpDepartService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmployeeRecordService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.CommonUtils;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.UploadUtil;
import com.ule.oa.common.utils.json.JSONUtils;

@Service
public class EmployeeRecordServiceImpl implements EmployeeRecordService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private DepartService departService;
	@Autowired
	private ConfigMapper configMapper;
	@Autowired
	private CompanyConfigMapper companyConfigMapper;
	@Autowired
	private EmpDepartService empDepartService;
	@Autowired
	private EmpPositionService empPositionService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private EmpTypeMapper empTypeMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private CompanyFloorMapper companyFloorMapper;
	@Autowired
	private CompanyPositionLevelMapper companyPositionLevelMapper;
	@Autowired
	private EmpSchoolMapper empSchoolMapper;
	@Autowired
	private EmpTrainingMapper empTrainingMapper;
	@Autowired
	private EmpWorkRecordMapper empWorkRecordMapper;
	@Autowired
	private EmpUrgentContactMapper empUrgentContactMapper;
	@Autowired
	private EmpFamilyMemberMapper empFamilyMemberMapper;
	@Autowired
	private EmpAchievementMapper empAchievementMapper;
	@Autowired
	private EmpContractMapper empContractMapper;
	@Autowired
	private EmpAppraiseMapper empAppraiseMapper;
	@Autowired
	private EmpPostRecordMapper empPostRecordMapper;
	@Autowired
	private UserService userService;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();

	@Override
	public PageModel<Employee> getListByPage(Employee employee) {

		int page = employee.getPage() == null ? 0 : employee.getPage();
		int rows = employee.getRows() == null ? 0 : employee.getRows();

		PageModel<Employee> pm = new PageModel<Employee>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
			
		Integer total = employeeMapper.getCount(employee);
		pm.setTotal(total);
		employee.setOffset(pm.getOffset());
		employee.setLimit(pm.getLimit());
		List<Employee> employees = employeeMapper.getPageList(employee);
		// 获得部门名称（一级部门+"_"+当前部门）
		getDepartName(employees);
	
		pm.setRows(employees);
		return pm;

	}
	
	public void getDepartName(List<Employee> employees) {
		for (Employee employee : employees) {
			if (null != employee.getDepart()) {
				Long departId = employee.getDepart().getId();

				Depart dp = departService.getById(departId);
				if (null != dp) {
					if (dp.getType() != 1 && null != dp.getParentId()) {
						dp.setName(setDepartName(dp, dp.getName()));
					}

					employee.setDepart(dp);
				}
			} else {
				employee.setDepart(new Depart());
			}
		}
	}
	
	public String setDepartName(Depart parentDepart, String childDepartName) {
		Depart model = new Depart();
		model.setId(parentDepart.getParentId());
		Depart dp = departService.getByCondition(model);

		if (null != dp) {
			if (dp.getType().intValue() == 1 || null == dp.getParentId()) {// 当前查找的节点是一级部门或者当前部门是挂在虚拟部门下面
				return dp.getName() + "_" + childDepartName;
			} else {
				return setDepartName(dp, childDepartName);
			}
		} else {
			return parentDepart.getName();
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveBaseInfo(Employee employee) throws OaException{
		Map<String,Object> result = new HashMap<String,Object>();
        if(employee.getId()==null){
        	result.put("sucess",false);
     		result.put("msg","参数有误！");
     		return result;
        }
        
        Employee old = employeeMapper.getById(employee.getId());
       
        if(old==null){
        	result.put("sucess",false);
     		result.put("msg","参数有误！");
     		return result;
        }
        
        User opt = userService.getCurrentUser();
        //手机号不为空，同步到passport通行证
		if(StringUtils.isNotBlank(employee.getMobile())) {
			//查询通行证账号
			User user = userMapper.getByEmployeeId(employee.getId());
			if(user!=null){
				HashMap<String, String> paramMap = new HashMap<String,String>();
				
				paramMap.put("username", user.getUserName());
				paramMap.put("email", old.getEmail());
				paramMap.put("telephone", employee.getMobile());
				if(paramMap.size()>0) {
					
					HttpRequest req = new HttpRequest.Builder().url(ConfigConstants.MODIFY_INFO_URL).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
		    				ContentCoverter.formConvertAsString(paramMap)).build();
		    		HttpResponse rep = null;
					try {
						rep = client.sendRequest(req);
						String response = rep.fullBody();
						
						Map<?, ?> responseMap = JSONUtils.readAsMap(response);
						String resultCode = (String)responseMap.get("errorMsg");
						if(!"".equals(resultCode)){
							throw new OaException("修改失败:员工信息同步到SSO系统中出错,请联系开发人员！");
						}
					} catch (IOException e) {
						logger.error("修改失败:员工信息同步到SSO系统中出错,请联系开发人员！");
					}
				}
			}
		}
		employee.setUpdateTime(new Date());
		employee.setUpdateUser(opt.getEmployee().getCnName());
        employeeMapper.updateById(employee);
        result.put("sucess",true);
		result.put("msg","保存成功！");
		return result;
	}
	
	@Override
	public Map<String, Object> savePayrollInfo(Employee employee) throws Exception{
		
		Map<String,Object> result = new HashMap<String,Object>();
		if(employee==null){
			result.put("sucess",false);
     		result.put("msg","参数有误！");
     		return result;
		}
		if(employee.getId()==null){
			result.put("sucess",false);
     		result.put("msg","参数有误！");
     		return result;
		}
		
		Employee old = employeeMapper.getById(employee.getId());
		
		 if(old==null){
        	result.put("sucess",false);
     		result.put("msg","参数有误！");
     		return result;
	     }
	        
		User opt = userService.getCurrentUser();
        //邮箱不为空，同步到passport通行证
		if(StringUtils.isNotBlank(employee.getEmail())) {
			//查询通行证账号
			User user = userMapper.getByEmployeeId(employee.getId());
			if(user!=null){
				HashMap<String, String> paramMap = new HashMap<String,String>();
				
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
		
		if(employee.getPositionId()!=null){
			EmpPosition param = new EmpPosition();
			param.setEmployeeId(employee.getId());
			EmpPosition ep = empPositionService.getByCondition(param);
			if(null != ep && null != ep.getId()){
				ep.setPositionId(employee.getPositionId());
				ep.setUpdateTime(new Date());
				ep.setUpdateUser(userService.getCurrentAccount());
				empPositionService.updateById(ep);
			}
		}

		if(employee.getDepartId()!=null){
			EmpDepart param = new EmpDepart();
			param.setEmployeeId(employee.getId());
			EmpDepart ed = empDepartService.getByCondition(param);
			if(null != ed && null != ed.getId()){
				ed.setDepartId(employee.getDepartId());
				ed.setUpdateTime(new Date());
				ed.setUpdateUser(userService.getCurrentAccount());
				empDepartService.updateById(ed);
			}
		}
		
		if(StringUtils.isNotBlank(employee.getWorkAddressProvince())){
			employee.setWorkAddressType(1);
			if("上海".equals(employee.getWorkAddressProvince())){
				employee.setWorkAddressType(0);
			}	
		}
		
		employee.setUpdateUser(opt.getEmployee().getCnName());
		employee.setUpdateTime(new Date());
		employeeMapper.updateById(employee);
		
		result.put("sucess",true);
		result.put("msg","保存成功！");
		return result;
	}

	@Override
	public Map<String, Object> getUpdateInfo(Long employeeId) {
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		if(employeeId==null){
			return result;
		}
		
		//基本信息
		Employee employee = employeeMapper.getById(employeeId);
		result.put("employee", employee);
		if(employee==null){
			return result;
		}
		DecimalFormat df = new DecimalFormat("#.00");
		result.put("ourAge",(employee.getOurAge()!=null&&employee.getOurAge()>0)?df.format(employee.getOurAge()):0);
		result.put("beforeWorkAge", (employee.getBeforeWorkAge()!=null&&employee.getBeforeWorkAge()>0)?df.format(employee.getBeforeWorkAge()):0);
		result.put("workAge", (employee.getWorkAge()!=null&&employee.getWorkAge()>0)?df.format(employee.getWorkAge()):0);
		
		//获取编辑页面select基础信息数据
		List<String> configCodeList = new ArrayList<String>();
		configCodeList.add("politicalStatus");
		configCodeList.add("educationLevel");
		configCodeList.add("whetherScheduling");
		configCodeList.add("nation");
		configCodeList.add("maritalStatus");
		configCodeList.add("degree");
		
		List<Config> configList = configMapper.getListByCodes(configCodeList);
		Map<String,List<Config>> configMap = configList.stream().collect(Collectors.groupingBy(Config :: getCode));
		result.put("config", configMap);
		
		List<String> comConfigCodeList = new ArrayList<String>();
		comConfigCodeList.add("employeeType");
		comConfigCodeList.add("country");
		comConfigCodeList.add("industryCorrelation");
		comConfigCodeList.add("typeOfWork");
		List<CompanyConfig> comConfigList = companyConfigMapper.getListByCodes(comConfigCodeList);
		Map<String,List<CompanyConfig>> comConfigMap = comConfigList.stream().collect(Collectors.groupingBy(CompanyConfig :: getCode));
		result.put("comConfig", comConfigMap);
		
		//职位
 		EmpPosition ep = new EmpPosition();
 		ep.setEmployeeId(employeeId);
 		EmpPosition empPosition = empPositionService.getByCondition(ep);
 		if(null != empPosition && null != empPosition.getPositionId()){
 			Position position = positionService.getById(empPosition.getPositionId());
 			if(null != position){
 				result.put("position", position);
 				//职级，职位序列列表
 				if(position.getPositionLevelId()!=null){
 					CompanyPositionLevel pl = companyPositionLevelMapper.getById(position.getPositionLevelId());
 					List<String> seqList = positionService.getSeqList(pl.getCode());
 					List<String> levelList = positionService.getLevelList(pl.getCode());
 					result.put("seqList", seqList);
 					result.put("levelList", levelList);
 				}
 			}
 		}
 		
 		//汇报对象
 		if(employee!=null&&employee.getReportToLeader()!=null){
 			Employee reportToLeader = employeeMapper.getById(employee.getReportToLeader());
 			result.put("reportToLeader", reportToLeader);
 		}
		
		//公司列表
		List<Company> companyList = companyMapper.getListByCondition(null);
		result.put("companyList", companyList);
		Map<Long,Company> companyMap = companyList.stream().collect(Collectors.toMap(Company::getId, Function.identity(), (key1, key2) -> key2));
		if(employee.getCompanyId()!=null){
			result.put("company", companyMap.get(employee.getCompanyId()));
		}
		
		//员工类型列表
		List<EmpType> empTypeList = empTypeMapper.getListByCondition(null);
		result.put("empTypeList", empTypeList);
		Map<Long,EmpType> empTypeMap = empTypeList.stream().collect(Collectors.toMap(EmpType::getId, Function.identity(), (key1, key2) -> key2));
		if(empTypeMap!=null&&empTypeMap.containsKey(employee.getEmpTypeId())){
			result.put("empType", empTypeMap.get(employee.getEmpTypeId()));
		}
		
		//部门列表
		List<Depart> departList = departService.getListByCondition(null);
		result.put("departList", departList);
		Map<Long,Depart> departMap = departList.stream().collect(Collectors.toMap(Depart::getId, Function.identity(), (key1, key2) -> key2));
		EmpDepart ed  = new EmpDepart();
 		ed.setEmployeeId(employeeId);
 		EmpDepart empDepart = empDepartService.getByCondition(ed);
		if(null != empDepart && null != empDepart.getDepartId()){
			result.put("depart", departMap.get(empDepart.getDepartId()));
			//部门负责人
			if(departMap.containsKey(empDepart.getDepartId())&&departMap.get(empDepart.getDepartId()).getLeader()!=null){
				Employee departLeader = employeeMapper.getById(departMap.get(empDepart.getDepartId()).getLeader());
				result.put("departLeader", departLeader);
			}
		}
		
		//汇报对象列表
		List<Employee> reportToLeaderList = employeeMapper.getAllMLevalEmps(null);
		result.put("reportToLeaderList", reportToLeaderList);
		
		//职位列表
		Position positionListCon = new Position();
		positionListCon.setDepartId(empDepart!=null?empDepart.getDepartId():null);
		List<Position> positionList = positionService.getListByCondition(positionListCon);
		result.put("positionList", positionList);
		
		//楼层列表
		List<CompanyFloor> floorList = companyFloorMapper.getListByCondition(null);
		result.put("floorList", floorList);
		
		//通行证
		User user = userMapper.getByEmployeeId(employeeId);
		result.put("username", user.getUserName());
		
		//教育经历
		List<EmpSchool> empSchoolList  = empSchoolMapper.getListByEmployeeId(employeeId);
		result.put("empSchoolList", empSchoolList);
		
		//培训证书
		List<EmpTraining> empTrainingList = empTrainingMapper.getListByEmployeeId(employeeId);
		result.put("empTrainingList", empTrainingList);
		
		//工作经历
		List<EmpWorkRecord> workRecordList = empWorkRecordMapper.getListByEmployeeId(employeeId);
		result.put("workRecordList", workRecordList);
		
		//紧急联系人
		List<EmpUrgentContact> urgentContactList = empUrgentContactMapper.getListByEmployeeId(employeeId);
		result.put("urgentContactList", urgentContactList);
		
		//家属信息
		List<EmpFamilyMember> familyMemberList = empFamilyMemberMapper.getListByEmployeeId(employeeId);
		result.put("familyMemberList", familyMemberList);
		
		//业绩与奖惩
		List<EmpAchievement> achievementList = empAchievementMapper.getListByEmployeeId(employeeId);
		result.put("achievementList", achievementList);
		
		//合同签订记录
		List<EmpContract> empContractList = empContractMapper.getListByEmployeeId(employeeId);
		result.put("empContractList", empContractList);
		
		//考核记录
		List<EmpAppraise> empAppraiseList = empAppraiseMapper.getListByEmployeeId(employeeId);
		result.put("empAppraiseList", empAppraiseList);
		
		//岗位记录
		List<EmpPostRecord> empPostRecordList = empPostRecordMapper.getListByEmployeeId(employeeId);
		result.put("empPostRecordList", empPostRecordList);
		
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveEducationExperience(String empSchoolListStr,Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有教育经历数据
		empSchoolMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date());
		
		if(StringUtils.isNotBlank(empSchoolListStr)&&!"[]".equals(empSchoolListStr)){
			JSONArray array = JSONArray.fromObject(empSchoolListStr);
			List<EmpSchool> empSchoolList = new ArrayList<EmpSchool>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpSchool add = new EmpSchool();
				add.setStartTime(DateUtils.parse(jsonObject.getString("startTime"), DateUtils.FORMAT_SHORT));
				add.setEndTime(DateUtils.parse(jsonObject.getString("endTime"), DateUtils.FORMAT_SHORT));
				add.setSchool(jsonObject.getString("school"));
				add.setMajor(jsonObject.getString("major"));
				add.setEducation(jsonObject.getInt("education"));
				add.setDegree(jsonObject.getInt("degree"));
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				empSchoolList.add(add);
			}
			empSchoolMapper.saveBatch(empSchoolList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveTrainingCertificate(
			String empTrainingListStr, Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有培训证书数据
		empTrainingMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date(),0);
		
		if(StringUtils.isNotBlank(empTrainingListStr)&&!"[]".equals(empTrainingListStr)){
			JSONArray array = JSONArray.fromObject(empTrainingListStr);
			List<EmpTraining> empTrainingList = new ArrayList<EmpTraining>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpTraining add = new EmpTraining();
				add.setStartTime(DateUtils.parse(jsonObject.getString("startTime"), DateUtils.FORMAT_SHORT));
				add.setEndTime(DateUtils.parse(jsonObject.getString("endTime"), DateUtils.FORMAT_SHORT));
				add.setTrainingInstitutions(jsonObject.getString("trainingInstitutions"));
				add.setContent(jsonObject.getString("content"));
				add.setObtainCertificate(jsonObject.getString("obtainCertificate"));
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				add.setIsCompanyTraining(0);//培训证书
				empTrainingList.add(add);
			}
			empTrainingMapper.saveBatch(empTrainingList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveWorkExperience(String empWorkRecordListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有工作经历数据
		empWorkRecordMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date());
		
		if(StringUtils.isNotBlank(empWorkRecordListStr)&&!"[]".equals(empWorkRecordListStr)){
			JSONArray array = JSONArray.fromObject(empWorkRecordListStr);
			List<EmpWorkRecord> empWorkRecordList = new ArrayList<EmpWorkRecord>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpWorkRecord add = new EmpWorkRecord();
				add.setStartTime(DateUtils.parse(jsonObject.getString("startTime"), DateUtils.FORMAT_SHORT));
				add.setEndTime(DateUtils.parse(jsonObject.getString("endTime"), DateUtils.FORMAT_SHORT));
				add.setCompanyName(jsonObject.getString("companyName"));
				add.setPositionName(jsonObject.getString("positionName"));
				add.setPositionTask(jsonObject.getString("positionTask"));
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				empWorkRecordList.add(add);
			}
			empWorkRecordMapper.saveBatch(empWorkRecordList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveEmergencyContact(
			String empUrgentContactListStr, Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有紧急联系人数据
		empUrgentContactMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date());
		
		if(StringUtils.isNotBlank(empUrgentContactListStr)&&!"[]".equals(empUrgentContactListStr)){
			JSONArray array = JSONArray.fromObject(empUrgentContactListStr);
			List<EmpUrgentContact> empUrgentContactList = new ArrayList<EmpUrgentContact>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpUrgentContact add = new EmpUrgentContact();
				add.setPriority(jsonObject.getInt("priority"));
				add.setShortName(jsonObject.getString("shortName"));
				add.setName(jsonObject.getString("name"));
				add.setMobile(jsonObject.getString("mobile"));
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				empUrgentContactList.add(add);
			}
			empUrgentContactMapper.saveBatch(empUrgentContactList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveSpouseInfo(String spouseInfoListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		Employee employee = employeeMapper.getById(employeeId);
		if(employee==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有配偶数据
		empFamilyMemberMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date(),0);
		
		if(StringUtils.isNotBlank(spouseInfoListStr)&&!"[]".equals(spouseInfoListStr)){
			JSONArray array = JSONArray.fromObject(spouseInfoListStr);
			List<EmpFamilyMember> empFamilyMemberList = new ArrayList<EmpFamilyMember>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpFamilyMember add = new EmpFamilyMember();
				add.setMemberName(jsonObject.getString("memberName"));
				add.setMemberCompanyName(jsonObject.getString("memberCompanyName"));
				add.setMemberMobile(jsonObject.getString("memberMobile"));
				add.setRelation(0);
				add.setMemberSex(employee.getSex()==0?1:0);//性别
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				empFamilyMemberList.add(add);
			}
			empFamilyMemberMapper.saveBatch(empFamilyMemberList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveChildrenInfo(String childrenInfoListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有子女数据
		empFamilyMemberMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date(),1);
		
		if(StringUtils.isNotBlank(childrenInfoListStr)&&!"[]".equals(childrenInfoListStr)){
			JSONArray array = JSONArray.fromObject(childrenInfoListStr);
			List<EmpFamilyMember> empFamilyMemberList = new ArrayList<EmpFamilyMember>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpFamilyMember add = new EmpFamilyMember();
				add.setMemberName(jsonObject.getString("memberName"));
				add.setMemberSex(jsonObject.getInt("memberSex"));//性别
				add.setBirthday(DateUtils.parse(jsonObject.getString("birthday"), DateUtils.FORMAT_SHORT));
				add.setRelation(1);
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				empFamilyMemberList.add(add);
			}
			empFamilyMemberMapper.saveBatch(empFamilyMemberList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveAchievementAndRewardMerit(
			String empAchievementListStr, Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有业绩与奖惩
		empAchievementMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date());
		
		if(StringUtils.isNotBlank(empAchievementListStr)&&!"[]".equals(empAchievementListStr)){
			JSONArray array = JSONArray.fromObject(empAchievementListStr);
			List<EmpAchievement> empAchievementList = new ArrayList<EmpAchievement>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpAchievement add = new EmpAchievement();
				add.setProcessTime(DateUtils.parse(jsonObject.getString("processTime"), DateUtils.FORMAT_SHORT));
				add.setContent(jsonObject.getString("content"));
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				empAchievementList.add(add);
			}
			empAchievementMapper.saveBatch(empAchievementList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveTrainRecord(String trainRecordListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有培训记录
		empTrainingMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date(),1);
		
		if(StringUtils.isNotBlank(trainRecordListStr)&&!"[]".equals(trainRecordListStr)){
			JSONArray array = JSONArray.fromObject(trainRecordListStr);
			List<EmpTraining> trainRecordList = new ArrayList<EmpTraining>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpTraining add = new EmpTraining();
				add.setStartTime(DateUtils.parse(jsonObject.getString("startTime"), DateUtils.FORMAT_SHORT));
				add.setTrainingProName(jsonObject.getString("trainingProName"));
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				add.setIsCompanyTraining(1);//培训记录
				trainRecordList.add(add);
			}
			empTrainingMapper.saveBatch(trainRecordList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveContractSignRecord(
			String empContractListStr, Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有合同签订记录
		empContractMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date());
		
		if(StringUtils.isNotBlank(empContractListStr)&&!"[]".equals(empContractListStr)){
			JSONArray array = JSONArray.fromObject(empContractListStr);
			List<EmpContract> empContractList = new ArrayList<EmpContract>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpContract add = new EmpContract();
				add.setContractCode(jsonObject.getString("contractCode"));
				add.setContractSignedDate(DateUtils.parse(jsonObject.getString("contractSignedDate"), DateUtils.FORMAT_SHORT));
				add.setContractStartTime(DateUtils.parse(jsonObject.getString("contractStartTime"), DateUtils.FORMAT_SHORT));
				add.setContractEndTime(DateUtils.parse(jsonObject.getString("contractEndTime"), DateUtils.FORMAT_SHORT));
				add.setProbationExpire(jsonObject.getDouble("probationExpire"));
				add.setContractPeriod(jsonObject.getDouble("contractPeriod"));
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setIsActive(1);//生效
				add.setDelFlag(0);
				add.setCompanyId(user.getCompanyId());
				empContractList.add(add);
			}
			empContractMapper.saveBatch(empContractList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveAssessRecord(String empAppraiseListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有考核记录
		empAppraiseMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date());
		
		if(StringUtils.isNotBlank(empAppraiseListStr)&&!"[]".equals(empAppraiseListStr)){
			JSONArray array = JSONArray.fromObject(empAppraiseListStr);
			List<EmpAppraise> empAppraiseList = new ArrayList<EmpAppraise>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpAppraise add = new EmpAppraise();
				add.setAnnualExaminationTime(DateUtils.parse(jsonObject.getString("annualExaminationTime"), DateUtils.FORMAT_SHORT));
				add.setScore(jsonObject.getString("score"));
				add.setConclusion(jsonObject.getString("conclusion"));
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				empAppraiseList.add(add);
			}
			empAppraiseMapper.saveBatch(empAppraiseList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> savePostRecord(String postRecordListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","参数有误！");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//删除该员工所有考核记录
		empPostRecordMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date());
		
		if(StringUtils.isNotBlank(postRecordListStr)&&!"[]".equals(postRecordListStr)){
			JSONArray array = JSONArray.fromObject(postRecordListStr);
			List<EmpPostRecord> empPostRecordList = new ArrayList<EmpPostRecord>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpPostRecord add = new EmpPostRecord();
				add.setEffectiveDate(DateUtils.parse(jsonObject.getString("effectiveDate"), DateUtils.FORMAT_SHORT));
				add.setDepartId(jsonObject.getLong("departId"));
				add.setPositionId(jsonObject.getLong("positionId"));
				add.setRemark(jsonObject.getString("remark"));
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				empPostRecordList.add(add);
			}
			empPostRecordMapper.saveBatch(empPostRecordList);
		}
		result.put("sucess",true);
 		result.put("msg","保存成功！");
		return result;
	}

	@Override
	public Map<String, Object> getPositionLevelAndSeqList(Long positionId) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		Position position = positionService.getById(positionId);
		if(null != position){
			//职级，职位序列列表
			if(position.getPositionLevelId()!=null){
				CompanyPositionLevel pl = companyPositionLevelMapper.getById(position.getPositionLevelId());
				List<String> seqList = positionService.getSeqList(pl.getCode());
				List<String> levelList = positionService.getLevelList(pl.getCode());
				result.put("seqList", seqList);
				result.put("levelList", levelList);
				result.put("success", true);
			}else{
				result.put("success", false);
			}
		}else{
			result.put("success", false);
		}
		
		return result;
	}

	@Override
	public Map<String, Object> getScanInfo(Long employeeId) {
		Map<String,Object> result = new HashMap<String,Object>();
		
		if(employeeId==null){
			return result;
		}
		
		//基本信息
		Employee employee = employeeMapper.getById(employeeId);
		result.put("employee", employee);
		if(employee==null){
			return result;
		}
		DecimalFormat df = new DecimalFormat("#.00");
		result.put("ourAge",(employee.getOurAge()!=null&&employee.getOurAge()>0)?df.format(employee.getOurAge()):0);
		result.put("beforeWorkAge", (employee.getBeforeWorkAge()!=null&&employee.getBeforeWorkAge()>0)?df.format(employee.getBeforeWorkAge()):0);
		result.put("workAge", (employee.getWorkAge()!=null&&employee.getWorkAge()>0)?df.format(employee.getWorkAge()):0);
		
		//获取编辑页面select基础信息数据
		List<String> configCodeList = new ArrayList<String>();
		configCodeList.add("politicalStatus");
		configCodeList.add("educationLevel");
		configCodeList.add("whetherScheduling");
		configCodeList.add("nation");
		configCodeList.add("maritalStatus");
		configCodeList.add("degree");
		
		List<Config> configList = configMapper.getListByCodes(configCodeList);
		Map<String,List<Config>> configMap = configList.stream().collect(Collectors.groupingBy(Config :: getCode));
		result.put("config", configMap);
		
		List<String> comConfigCodeList = new ArrayList<String>();
		comConfigCodeList.add("employeeType");
		comConfigCodeList.add("country");
		comConfigCodeList.add("industryCorrelation");
		comConfigCodeList.add("typeOfWork");
		List<CompanyConfig> comConfigList = companyConfigMapper.getListByCodes(comConfigCodeList);
		Map<String,List<CompanyConfig>> comConfigMap = comConfigList.stream().collect(Collectors.groupingBy(CompanyConfig :: getCode));
		result.put("comConfig", comConfigMap);
		
		//职位
 		EmpPosition ep = new EmpPosition();
 		ep.setEmployeeId(employeeId);
 		EmpPosition empPosition = empPositionService.getByCondition(ep);
 		if(null != empPosition && null != empPosition.getPositionId()){
 			Position position = positionService.getById(empPosition.getPositionId());
 			if(null != position){
 				result.put("position", position);
 				//职级，职位序列列表
 				if(position.getPositionLevelId()!=null){
 					CompanyPositionLevel pl = companyPositionLevelMapper.getById(position.getPositionLevelId());
 					List<String> seqList = positionService.getSeqList(pl.getCode());
 					List<String> levelList = positionService.getLevelList(pl.getCode());
 					result.put("seqList", seqList);
 					result.put("levelList", levelList);
 				}
 			}
 		}
 		
 		//汇报对象
 		if(employee!=null&&employee.getReportToLeader()!=null){
 			Employee reportToLeader = employeeMapper.getById(employee.getReportToLeader());
 			result.put("reportToLeader", reportToLeader);
 		}
		
		//公司列表
		List<Company> companyList = companyMapper.getListByCondition(null);
		result.put("companyList", companyList);
		Map<Long,Company> companyMap = companyList.stream().collect(Collectors.toMap(Company::getId, Function.identity(), (key1, key2) -> key2));
		if(employee.getCompanyId()!=null){
			result.put("company", companyMap.get(employee.getCompanyId()));
		}
		
		//员工类型列表
		List<EmpType> empTypeList = empTypeMapper.getListByCondition(null);
		result.put("empTypeList", empTypeList);
		Map<Long,EmpType> empTypeMap = empTypeList.stream().collect(Collectors.toMap(EmpType::getId, Function.identity(), (key1, key2) -> key2));
		if(empTypeMap!=null&&empTypeMap.containsKey(employee.getEmpTypeId())){
			result.put("empType", empTypeMap.get(employee.getEmpTypeId()));
		}
		
		//部门列表
		List<Depart> departList = departService.getListByCondition(null);
		result.put("departList", departList);
		Map<Long,Depart> departMap = departList.stream().collect(Collectors.toMap(Depart::getId, Function.identity(), (key1, key2) -> key2));
		EmpDepart ed  = new EmpDepart();
 		ed.setEmployeeId(employeeId);
 		EmpDepart empDepart = empDepartService.getByCondition(ed);
		if(null != empDepart && null != empDepart.getDepartId()){
			result.put("depart", departMap.get(empDepart.getDepartId()));
			//部门负责人
			if(departMap.containsKey(empDepart.getDepartId())&&departMap.get(empDepart.getDepartId()).getLeader()!=null){
				Employee departLeader = employeeMapper.getById(departMap.get(empDepart.getDepartId()).getLeader());
				result.put("departLeader", departLeader);
			}
		}
		
		//汇报对象列表
		List<Employee> reportToLeaderList = employeeMapper.getAllMLevalEmps(null);
		result.put("reportToLeaderList", reportToLeaderList);
		
		//职位列表
		Position positionListCon = new Position();
		positionListCon.setDepartId(empDepart!=null?empDepart.getDepartId():null);
		List<Position> positionList = positionService.getListByCondition(positionListCon);
		result.put("positionList", positionList);
		
		//楼层列表
		List<CompanyFloor> floorList = companyFloorMapper.getListByCondition(null);
		result.put("floorList", floorList);
		
		//通行证
		User user = userMapper.getByEmployeeId(employeeId);
		result.put("username", user.getUserName());
		
		//教育经历
		List<EmpSchool> empSchoolList  = empSchoolMapper.getListByEmployeeId(employeeId);
		result.put("empSchoolList", empSchoolList);
		
		//培训证书
		List<EmpTraining> empTrainingList = empTrainingMapper.getListByEmployeeId(employeeId);
		result.put("empTrainingList", empTrainingList);
		
		//工作经历
		List<EmpWorkRecord> workRecordList = empWorkRecordMapper.getListByEmployeeId(employeeId);
		result.put("workRecordList", workRecordList);
		
		//紧急联系人
		List<EmpUrgentContact> urgentContactList = empUrgentContactMapper.getListByEmployeeId(employeeId);
		result.put("urgentContactList", urgentContactList);
		
		//家属信息
		List<EmpFamilyMember> familyMemberList = empFamilyMemberMapper.getListByEmployeeId(employeeId);
		result.put("familyMemberList", familyMemberList);
		
		//业绩与奖惩
		List<EmpAchievement> achievementList = empAchievementMapper.getListByEmployeeId(employeeId);
		result.put("achievementList", achievementList);
		
		//合同签订记录
		List<EmpContract> empContractList = empContractMapper.getListByEmployeeId(employeeId);
		result.put("empContractList", empContractList);
		
		//考核记录
		List<EmpAppraise> empAppraiseList = empAppraiseMapper.getListByEmployeeId(employeeId);
		result.put("empAppraiseList", empAppraiseList);
		
		//岗位记录
		List<EmpPostRecord> empPostRecordList = empPostRecordMapper.getListByEmployeeId(employeeId);
		result.put("empPostRecordList", empPostRecordList);
		
		return result;
	}

	@Override
	public Map<String, Object> uploadEmployeePhoto(CommonsMultipartFile file) throws Exception{
		Map<String,Object> data = new HashMap<String,Object>();
		
		if(file==null){
			data.put("success", false);
			data.put("message", "请选择一个图片！");
			return data;
		}
		
		String picUlr = "";
		long defaultSize = 10240000;//默认上传大小10M
		
 		String filename = file.getOriginalFilename();
 		long fileSize = file.getSize();
 		if(fileSize > defaultSize){
 			throw new OaException("请上传10M以下的头像!");
 		}
 		//check文件上传类型
 		if(CommonUtils.checkFileSuffix(ConfigConstants.PIC_UPLOAD_TYPE, filename)){
			String fileFullName = "uleOA/photo/employee/" + DateUtils.getNow().replace(" ", "").replace("-", "").replace(":", "") + "/" + filename;
			File temfile = null;
			try {
				temfile = new File(filename);
				FileUtils.writeByteArrayToFile(temfile, UploadUtil.fileToByteArray(file));
				//上传图片，返回图片url
				String result = UploadUtil.uploadToQiNiu(temfile, fileFullName,	ConfigConstants.QINIU_HOST);
				Map<String, String> resultMap = (Map<String, String>) JSONUtils.readAsMap(result);
				if(null != resultMap && "success".equals(resultMap.get("status"))){
					picUlr = resultMap.get("url");
					data.put("success", true);
					data.put("message", "上传成功！");
					data.put("picUlr", picUlr);
				}else{
					throw new Exception("上传失败!");
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
		
		return data;

	}
	
	@SuppressWarnings("deprecation")
	public String getImageBase(String src) {
        
		String photo = "";
		
		if(StringUtils.isBlank(src)){
              return photo;
        }
        
		URL url = null;
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        HttpURLConnection httpUrl = null;
        try {
            url = new URL(src);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            httpUrl.getInputStream();
            is = httpUrl.getInputStream();
 
            outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while ((len = is.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            // 对字节数组Base64编码
            photo = new BASE64Encoder().encode(outStream.toByteArray());
        } catch (Exception e) {
        	photo = "";
            logger.error("getImageBase:下载员工照片失败！");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    
                }
            }
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
        }
		return photo;
         
	 }

	@Override
	public Map<String, Object> getDownLoadInfo(Long employeeId) {
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		//模板数据初始化空值
		result.put("companyName", "");//公司名称
		result.put("employeeName", "");//员工名称
		result.put("employeeCode", "");//员工编号
		result.put("departName", "");//部门名称
		result.put("positionName", "");//职位
		result.put("reportToLeader", "");//汇报对象
		result.put("departLeader", "");//部门负责人
		result.put("employeeType", "");//员工类型
		result.put("jobStatus", "");//在职状态
		result.put("photo","");//照片
		result.put("sex", "");//性别
        result.put("marryStatus", "");//婚姻状态
        result.put("country", "");//国家
        result.put("nation", "");//民族
        result.put("birthDay", "");//出生日期
        result.put("age", "");//年龄
        result.put("politicalStatus", "");//政治面貌
        result.put("educationLevel", "");//文化程度
        result.put("householdRegister", "");//户籍
        result.put("address", "");//在沪居住地
        result.put("industryRelevance", "");//行业相关性
        result.put("workingBackground", "");//从业背景
        result.put("mobile", "");//手机号
        result.put("workType", "");//工时种类
        result.put("floorCode", "");//楼层
        result.put("seatCode", "");//座位号
        result.put("userName", "");//通行证
        result.put("email", "");//邮箱
        result.put("positionLevel", "");//职级
        result.put("positionSeq", "");//职位序列
        result.put("positionTitle", "");//职称
        result.put("whetherScheduling", "");//是否排班
        result.put("entryTime", "");//入职时间
        result.put("probationEndTime", "");//使用期到期日
        result.put("ourAge", "");//司龄
        result.put("quitTime", "");//离职时间
        result.put("protocolEndTime", "");//合同到期日
        result.put("uleAccount", "");//邮乐账号
        result.put("workAddress", "");//工作地点
    
		if(employeeId==null){
			return result;
		}
		
		Employee employee = employeeMapper.getById(employeeId);
		if(employee==null){
			return result;
		}
		
		if(employee.getCompanyId()!=null){
			Company company = companyMapper.getById(employee.getCompanyId());
			result.put("companyName", company!=null?company.getName():"");//公司名称
		}
		result.put("employeeName", StringUtils.isNotBlank(employee.getCnName())?employee.getCnName():"");//员工名称
		result.put("employeeCode", StringUtils.isNotBlank(employee.getCode())?employee.getCode():"");//员工编号
		
		EmpDepart ed  = new EmpDepart();
 		ed.setEmployeeId(employeeId);
 		EmpDepart empDepart = empDepartService.getByCondition(ed);
		if(null != empDepart && null != empDepart.getDepartId()){
			Depart depart = departService.getById(empDepart.getDepartId());
			result.put("departName", depart!=null?depart.getName():"");//部门名称
			if(depart!=null&&depart.getLeader()!=null){
				Employee departLeader = employeeMapper.getById(depart.getLeader());
				result.put("departLeader", departLeader.getCnName());//部门负责人
			}
		}
		
 		EmpPosition ep = new EmpPosition();
 		ep.setEmployeeId(employeeId);
 		EmpPosition empPosition = empPositionService.getByCondition(ep);
 		if(null != empPosition && null != empPosition.getPositionId()){
 			Position position = positionService.getById(empPosition.getPositionId());
 			result.put("positionName", position!=null?position.getPositionName():"");//职位
 		}
 		
 		if(employee!=null&&employee.getReportToLeader()!=null){
 			Employee reportToLeader = employeeMapper.getById(employeeId);
 			result.put("reportToLeader", reportToLeader!=null?reportToLeader.getCnName():"");//汇报对象
 		}
		
 		if(employee.getEmpTypeId()!=null){
 			EmpType empType = empTypeMapper.getById(employee.getEmpTypeId());
 			result.put("employeeType", empType!=null?empType.getTypeCName():"");//员工类型
 		}
 		
 		switch(employee.getJobStatus()){
 			case 0:
 				result.put("jobStatus", "在职");//在职状态
 				break;
 			case 1:
 				result.put("jobStatus", "离职");//在职状态
 				break;
 			case 2:
 				result.put("jobStatus", "待离职");//在职状态
 				break;
 			default:
 				break;
 		 
 		}
 		
 		result.put("photo",getImageBase(employee.getPhoto()));//照片
 		
 		switch(employee.getSex()){
			case 0:
				result.put("sex", "男");//性别
				break;
			case 1:
				result.put("sex", "女");//性别
				break;
			default:
				break;
		 
		}
 		
 		List<String> configCodeList = new ArrayList<String>();
		configCodeList.add("politicalStatus");
		configCodeList.add("educationLevel");
		configCodeList.add("whetherScheduling");
		configCodeList.add("nation");
		configCodeList.add("maritalStatus");
		configCodeList.add("degree");
		List<Config> configList = configMapper.getListByCodes(configCodeList);
 		Map<Long,Config> configMap = configList.stream().collect(Collectors.toMap(Config::getId, Function.identity(), (key1, key2) -> key2));
 		
 		if(employee.getMaritalStatus()!=null&&configMap.containsKey(employee.getMaritalStatus())){
 			result.put("marryStatus", configMap.get(employee.getMaritalStatus()).getDisplayName());//婚姻状态
 		}
 		
 		if(employee.getNation()!=null&&configMap.containsKey(employee.getNation())){
 			result.put("nation", configMap.get(employee.getNation()).getDisplayName());//民族
 		}
        
 		if(employee.getPoliticalStatus()!=null&&configMap.containsKey(employee.getPoliticalStatus())){
 			 result.put("politicalStatus", configMap.get(employee.getPoliticalStatus()).getDisplayName());//政治面貌
 		}
 		
 		if(employee.getDegreeOfEducation()!=null&&configMap.containsKey(employee.getDegreeOfEducation())){
 			 result.put("educationLevel", configMap.get(employee.getDegreeOfEducation()).getDisplayName());//文化程度
 		}
 		
 		if(employee.getWhetherScheduling()!=null&&configMap.containsKey(employee.getWhetherScheduling())){
 			result.put("whetherScheduling", configMap.get(employee.getWhetherScheduling()).getDisplayName());//是否排班
 		}

		List<String> comConfigCodeList = new ArrayList<String>();
		comConfigCodeList.add("country");
		comConfigCodeList.add("industryCorrelation");
		comConfigCodeList.add("typeOfWork");
		List<CompanyConfig> comConfigList = companyConfigMapper.getListByCodes(comConfigCodeList);
		Map<Long,CompanyConfig> comConfigMap = comConfigList.stream().collect(Collectors.toMap(CompanyConfig::getId, Function.identity(), (key1, key2) -> key2));
 		
		if(employee.getCountry()!=null&&comConfigMap.containsKey(employee.getCountry())){
			result.put("country", comConfigMap.get(employee.getCountry()).getDisplayName());//国家
		}
 		
 		if(employee.getIndustryRelevance()!=null&&comConfigMap.containsKey(employee.getIndustryRelevance())){
 			result.put("industryRelevance", comConfigMap.get(employee.getIndustryRelevance()).getDisplayName());//行业相关性
 		}
 		
 		if(employee.getWorkType()!=null&&comConfigMap.containsKey(employee.getWorkType())){
 			 result.put("workType", comConfigMap.get(employee.getWorkType()).getDisplayName());//工时种类
 		}
        
 		result.put("birthDay", employee.getBirthday()!=null?DateUtils.format(employee.getBirthday(), DateUtils.FORMAT_SHORT):"");//出生日期
 		result.put("age", employee.getAge()!=null?employee.getAge():"");//年龄
 		result.put("householdRegister", StringUtils.isNotBlank(employee.getHouseholdRegister())?employee.getHouseholdRegister():"");//户籍
 		result.put("address", StringUtils.isNotBlank(employee.getAddress())?employee.getAddress():"");//在沪居住地
 		result.put("workingBackground", StringUtils.isNotBlank(employee.getWorkingBackground())?employee.getWorkingBackground():"");//从业背景
 		result.put("mobile", StringUtils.isNotBlank(employee.getMobile())?employee.getMobile():"");//手机号
 		result.put("email", StringUtils.isNotBlank(employee.getEmail())?employee.getEmail():"");//邮箱
 		result.put("seatCode", StringUtils.isNotBlank(employee.getSeatCode())?employee.getSeatCode():"");//座位号
 		result.put("positionLevel", StringUtils.isNotBlank(employee.getPositionLevel())?employee.getPositionLevel():"");//职级
        result.put("positionSeq", StringUtils.isNotBlank(employee.getPositionSeq())?employee.getPositionSeq():"");//职位序列
        result.put("positionTitle", StringUtils.isNotBlank(employee.getPositionTitle())?employee.getPositionTitle():"");//职称
        result.put("entryTime", employee.getFirstEntryTime()!=null?DateUtils.format(employee.getFirstEntryTime(), DateUtils.FORMAT_SHORT):"");//入职时间
        result.put("probationEndTime", employee.getProbationEndTime()!=null?DateUtils.format(employee.getProbationEndTime(), DateUtils.FORMAT_SHORT):"");//使用期到期日
        result.put("ourAge", employee.getOurAge()!=null?employee.getOurAge():"");//司龄
        result.put("quitTime", employee.getQuitTime()!=null?DateUtils.format(employee.getQuitTime(), DateUtils.FORMAT_SHORT):"");//离职时间
        result.put("protocolEndTime", employee.getProtocolEndTime()!=null?DateUtils.format(employee.getProtocolEndTime(), DateUtils.FORMAT_SHORT):"");//合同到期日
        result.put("uleAccount", StringUtils.isNotBlank(employee.getUleAccount())?employee.getUleAccount():"");//邮乐账号
        String workAddress = (StringUtils.isNotBlank(employee.getWorkAddressProvince())?employee.getWorkAddressProvince():"")
        		+" " + (StringUtils.isNotBlank(employee.getWorkAddressCity())?employee.getWorkAddressCity():"");
        result.put("workAddress",workAddress);//工作地点
 		
        //通行证
		User user = userMapper.getByEmployeeId(employeeId);
		result.put("userName", user!=null?user.getUserName():"");//通行证
        
        result.put("floorCode", StringUtils.isNotBlank(employee.getFloorCode())?employee.getFloorCode():"");//楼层
       
        //教育经历
  		List<EmpSchool> empSchoolList  = empSchoolMapper.getListByEmployeeId(employeeId);
  		List<Map<String,Object>> schoolList = new ArrayList<Map<String,Object>>();
  		for(EmpSchool school : empSchoolList){
  			Map<String,Object> data = new HashMap<String,Object>();
  			data.put("school_time", DateUtils.format(school.getStartTime(), DateUtils.FORMAT_SHORT)+ " 至  "+DateUtils.format(school.getEndTime(), DateUtils.FORMAT_SHORT));
  			data.put("school_name", school.getSchool());
  			data.put("major", school.getMajor());
  			data.put("education", "");
  			if(configMap!=null&&school.getEducation()!=null&&configMap.containsKey(school.getEducation())){
  				data.put("education", configMap.get(school.getEducation()).getDisplayName());
  			}
  			schoolList.add(data);
  		}
  		result.put("schoolList", schoolList);
  		
        //培训证书
		List<EmpTraining> empTrainingList = empTrainingMapper.getListByEmployeeId(employeeId);
        List<Map<String,Object>> trainList = new ArrayList<Map<String,Object>>();
        for(EmpTraining train:empTrainingList){
          if(train.getIsCompanyTraining()==0){
        	  Map<String,Object> data = new HashMap<String,Object>();
              data.put("t_time", DateUtils.format(train.getStartTime(), DateUtils.FORMAT_SHORT)+" 至  "+DateUtils.format(train.getEndTime(), DateUtils.FORMAT_SHORT));
              data.put("t_institution", train.getTrainingInstitutions());
              data.put("t_certificate", train.getObtainCertificate());
              data.put("t_content", train.getContent());
              trainList.add(data);
          }
        }
        result.put("trainList", trainList);

        //工作经历
		List<EmpWorkRecord> workRecordList = empWorkRecordMapper.getListByEmployeeId(employeeId);
        List<Map<String,Object>> workList = new ArrayList<Map<String,Object>>();
        for(EmpWorkRecord work:workRecordList){
	        Map<String,Object> data = new HashMap<String,Object>();
	        data.put("w_time", DateUtils.format(work.getStartTime(), DateUtils.FORMAT_SHORT)+" 至  "+DateUtils.format(work.getEndTime(), DateUtils.FORMAT_SHORT));
	        data.put("w_c_name", work.getCompanyName());
	        data.put("w_position", work.getPositionName());
	        data.put("w_task", work.getPositionTask());
	        workList.add(data);
        }
        result.put("workList", workList);
		return result;
	}
	
}
