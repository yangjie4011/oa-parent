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
		// ?????????????????????????????????+"_"+???????????????
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
			if (dp.getType().intValue() == 1 || null == dp.getParentId()) {// ?????????????????????????????????????????????????????????????????????????????????
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
     		result.put("msg","???????????????");
     		return result;
        }
        
        Employee old = employeeMapper.getById(employee.getId());
       
        if(old==null){
        	result.put("sucess",false);
     		result.put("msg","???????????????");
     		return result;
        }
        
        User opt = userService.getCurrentUser();
        //??????????????????????????????passport?????????
		if(StringUtils.isNotBlank(employee.getMobile())) {
			//?????????????????????
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
							throw new OaException("????????????:?????????????????????SSO???????????????,????????????????????????");
						}
					} catch (IOException e) {
						logger.error("????????????:?????????????????????SSO???????????????,????????????????????????");
					}
				}
			}
		}
		employee.setUpdateTime(new Date());
		employee.setUpdateUser(opt.getEmployee().getCnName());
        employeeMapper.updateById(employee);
        result.put("sucess",true);
		result.put("msg","???????????????");
		return result;
	}
	
	@Override
	public Map<String, Object> savePayrollInfo(Employee employee) throws Exception{
		
		Map<String,Object> result = new HashMap<String,Object>();
		if(employee==null){
			result.put("sucess",false);
     		result.put("msg","???????????????");
     		return result;
		}
		if(employee.getId()==null){
			result.put("sucess",false);
     		result.put("msg","???????????????");
     		return result;
		}
		
		Employee old = employeeMapper.getById(employee.getId());
		
		 if(old==null){
        	result.put("sucess",false);
     		result.put("msg","???????????????");
     		return result;
	     }
	        
		User opt = userService.getCurrentUser();
        //???????????????????????????passport?????????
		if(StringUtils.isNotBlank(employee.getEmail())) {
			//?????????????????????
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
						throw new OaException("????????????:?????????????????????SSO???????????????,????????????????????????");
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
			if("??????".equals(employee.getWorkAddressProvince())){
				employee.setWorkAddressType(0);
			}	
		}
		
		employee.setUpdateUser(opt.getEmployee().getCnName());
		employee.setUpdateTime(new Date());
		employeeMapper.updateById(employee);
		
		result.put("sucess",true);
		result.put("msg","???????????????");
		return result;
	}

	@Override
	public Map<String, Object> getUpdateInfo(Long employeeId) {
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		if(employeeId==null){
			return result;
		}
		
		//????????????
		Employee employee = employeeMapper.getById(employeeId);
		result.put("employee", employee);
		if(employee==null){
			return result;
		}
		DecimalFormat df = new DecimalFormat("#.00");
		result.put("ourAge",(employee.getOurAge()!=null&&employee.getOurAge()>0)?df.format(employee.getOurAge()):0);
		result.put("beforeWorkAge", (employee.getBeforeWorkAge()!=null&&employee.getBeforeWorkAge()>0)?df.format(employee.getBeforeWorkAge()):0);
		result.put("workAge", (employee.getWorkAge()!=null&&employee.getWorkAge()>0)?df.format(employee.getWorkAge()):0);
		
		//??????????????????select??????????????????
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
		
		//??????
 		EmpPosition ep = new EmpPosition();
 		ep.setEmployeeId(employeeId);
 		EmpPosition empPosition = empPositionService.getByCondition(ep);
 		if(null != empPosition && null != empPosition.getPositionId()){
 			Position position = positionService.getById(empPosition.getPositionId());
 			if(null != position){
 				result.put("position", position);
 				//???????????????????????????
 				if(position.getPositionLevelId()!=null){
 					CompanyPositionLevel pl = companyPositionLevelMapper.getById(position.getPositionLevelId());
 					List<String> seqList = positionService.getSeqList(pl.getCode());
 					List<String> levelList = positionService.getLevelList(pl.getCode());
 					result.put("seqList", seqList);
 					result.put("levelList", levelList);
 				}
 			}
 		}
 		
 		//????????????
 		if(employee!=null&&employee.getReportToLeader()!=null){
 			Employee reportToLeader = employeeMapper.getById(employee.getReportToLeader());
 			result.put("reportToLeader", reportToLeader);
 		}
		
		//????????????
		List<Company> companyList = companyMapper.getListByCondition(null);
		result.put("companyList", companyList);
		Map<Long,Company> companyMap = companyList.stream().collect(Collectors.toMap(Company::getId, Function.identity(), (key1, key2) -> key2));
		if(employee.getCompanyId()!=null){
			result.put("company", companyMap.get(employee.getCompanyId()));
		}
		
		//??????????????????
		List<EmpType> empTypeList = empTypeMapper.getListByCondition(null);
		result.put("empTypeList", empTypeList);
		Map<Long,EmpType> empTypeMap = empTypeList.stream().collect(Collectors.toMap(EmpType::getId, Function.identity(), (key1, key2) -> key2));
		if(empTypeMap!=null&&empTypeMap.containsKey(employee.getEmpTypeId())){
			result.put("empType", empTypeMap.get(employee.getEmpTypeId()));
		}
		
		//????????????
		List<Depart> departList = departService.getListByCondition(null);
		result.put("departList", departList);
		Map<Long,Depart> departMap = departList.stream().collect(Collectors.toMap(Depart::getId, Function.identity(), (key1, key2) -> key2));
		EmpDepart ed  = new EmpDepart();
 		ed.setEmployeeId(employeeId);
 		EmpDepart empDepart = empDepartService.getByCondition(ed);
		if(null != empDepart && null != empDepart.getDepartId()){
			result.put("depart", departMap.get(empDepart.getDepartId()));
			//???????????????
			if(departMap.containsKey(empDepart.getDepartId())&&departMap.get(empDepart.getDepartId()).getLeader()!=null){
				Employee departLeader = employeeMapper.getById(departMap.get(empDepart.getDepartId()).getLeader());
				result.put("departLeader", departLeader);
			}
		}
		
		//??????????????????
		List<Employee> reportToLeaderList = employeeMapper.getAllMLevalEmps(null);
		result.put("reportToLeaderList", reportToLeaderList);
		
		//????????????
		Position positionListCon = new Position();
		positionListCon.setDepartId(empDepart!=null?empDepart.getDepartId():null);
		List<Position> positionList = positionService.getListByCondition(positionListCon);
		result.put("positionList", positionList);
		
		//????????????
		List<CompanyFloor> floorList = companyFloorMapper.getListByCondition(null);
		result.put("floorList", floorList);
		
		//?????????
		User user = userMapper.getByEmployeeId(employeeId);
		result.put("username", user.getUserName());
		
		//????????????
		List<EmpSchool> empSchoolList  = empSchoolMapper.getListByEmployeeId(employeeId);
		result.put("empSchoolList", empSchoolList);
		
		//????????????
		List<EmpTraining> empTrainingList = empTrainingMapper.getListByEmployeeId(employeeId);
		result.put("empTrainingList", empTrainingList);
		
		//????????????
		List<EmpWorkRecord> workRecordList = empWorkRecordMapper.getListByEmployeeId(employeeId);
		result.put("workRecordList", workRecordList);
		
		//???????????????
		List<EmpUrgentContact> urgentContactList = empUrgentContactMapper.getListByEmployeeId(employeeId);
		result.put("urgentContactList", urgentContactList);
		
		//????????????
		List<EmpFamilyMember> familyMemberList = empFamilyMemberMapper.getListByEmployeeId(employeeId);
		result.put("familyMemberList", familyMemberList);
		
		//???????????????
		List<EmpAchievement> achievementList = empAchievementMapper.getListByEmployeeId(employeeId);
		result.put("achievementList", achievementList);
		
		//??????????????????
		List<EmpContract> empContractList = empContractMapper.getListByEmployeeId(employeeId);
		result.put("empContractList", empContractList);
		
		//????????????
		List<EmpAppraise> empAppraiseList = empAppraiseMapper.getListByEmployeeId(employeeId);
		result.put("empAppraiseList", empAppraiseList);
		
		//????????????
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
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//???????????????????????????????????????
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
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveTrainingCertificate(
			String empTrainingListStr, Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//???????????????????????????????????????
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
				add.setIsCompanyTraining(0);//????????????
				empTrainingList.add(add);
			}
			empTrainingMapper.saveBatch(empTrainingList);
		}
		result.put("sucess",true);
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveWorkExperience(String empWorkRecordListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//???????????????????????????????????????
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
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveEmergencyContact(
			String empUrgentContactListStr, Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//??????????????????????????????????????????
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
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveSpouseInfo(String spouseInfoListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		Employee employee = employeeMapper.getById(employeeId);
		if(employee==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//?????????????????????????????????
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
				add.setMemberSex(employee.getSex()==0?1:0);//??????
				add.setCreateTime(new Date());
				add.setCreateUser(user.getEmployee().getCnName());
				add.setEmployeeId(employeeId);
				add.setDelFlag(0);
				empFamilyMemberList.add(add);
			}
			empFamilyMemberMapper.saveBatch(empFamilyMemberList);
		}
		result.put("sucess",true);
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveChildrenInfo(String childrenInfoListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//?????????????????????????????????
		empFamilyMemberMapper.deleteByEmployeeId(employeeId, user.getEmployee().getCnName(), new Date(),1);
		
		if(StringUtils.isNotBlank(childrenInfoListStr)&&!"[]".equals(childrenInfoListStr)){
			JSONArray array = JSONArray.fromObject(childrenInfoListStr);
			List<EmpFamilyMember> empFamilyMemberList = new ArrayList<EmpFamilyMember>();
			for (int i = 0; i < array.size(); i++) {			
				JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
				EmpFamilyMember add = new EmpFamilyMember();
				add.setMemberName(jsonObject.getString("memberName"));
				add.setMemberSex(jsonObject.getInt("memberSex"));//??????
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
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveAchievementAndRewardMerit(
			String empAchievementListStr, Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//????????????????????????????????????
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
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveTrainRecord(String trainRecordListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//?????????????????????????????????
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
				add.setIsCompanyTraining(1);//????????????
				trainRecordList.add(add);
			}
			empTrainingMapper.saveBatch(trainRecordList);
		}
		result.put("sucess",true);
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveContractSignRecord(
			String empContractListStr, Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//???????????????????????????????????????
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
				add.setIsActive(1);//??????
				add.setDelFlag(0);
				add.setCompanyId(user.getCompanyId());
				empContractList.add(add);
			}
			empContractMapper.saveBatch(empContractList);
		}
		result.put("sucess",true);
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> saveAssessRecord(String empAppraiseListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//?????????????????????????????????
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
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String, Object> savePostRecord(String postRecordListStr,
			Long employeeId) throws OaException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(employeeId==null){
	    	result.put("sucess",false);
	 		result.put("msg","???????????????");
	 		return result;
	    }
		
		User user = userService.getCurrentUser();
		//?????????????????????????????????
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
 		result.put("msg","???????????????");
		return result;
	}

	@Override
	public Map<String, Object> getPositionLevelAndSeqList(Long positionId) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		Position position = positionService.getById(positionId);
		if(null != position){
			//???????????????????????????
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
		
		//????????????
		Employee employee = employeeMapper.getById(employeeId);
		result.put("employee", employee);
		if(employee==null){
			return result;
		}
		DecimalFormat df = new DecimalFormat("#.00");
		result.put("ourAge",(employee.getOurAge()!=null&&employee.getOurAge()>0)?df.format(employee.getOurAge()):0);
		result.put("beforeWorkAge", (employee.getBeforeWorkAge()!=null&&employee.getBeforeWorkAge()>0)?df.format(employee.getBeforeWorkAge()):0);
		result.put("workAge", (employee.getWorkAge()!=null&&employee.getWorkAge()>0)?df.format(employee.getWorkAge()):0);
		
		//??????????????????select??????????????????
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
		
		//??????
 		EmpPosition ep = new EmpPosition();
 		ep.setEmployeeId(employeeId);
 		EmpPosition empPosition = empPositionService.getByCondition(ep);
 		if(null != empPosition && null != empPosition.getPositionId()){
 			Position position = positionService.getById(empPosition.getPositionId());
 			if(null != position){
 				result.put("position", position);
 				//???????????????????????????
 				if(position.getPositionLevelId()!=null){
 					CompanyPositionLevel pl = companyPositionLevelMapper.getById(position.getPositionLevelId());
 					List<String> seqList = positionService.getSeqList(pl.getCode());
 					List<String> levelList = positionService.getLevelList(pl.getCode());
 					result.put("seqList", seqList);
 					result.put("levelList", levelList);
 				}
 			}
 		}
 		
 		//????????????
 		if(employee!=null&&employee.getReportToLeader()!=null){
 			Employee reportToLeader = employeeMapper.getById(employee.getReportToLeader());
 			result.put("reportToLeader", reportToLeader);
 		}
		
		//????????????
		List<Company> companyList = companyMapper.getListByCondition(null);
		result.put("companyList", companyList);
		Map<Long,Company> companyMap = companyList.stream().collect(Collectors.toMap(Company::getId, Function.identity(), (key1, key2) -> key2));
		if(employee.getCompanyId()!=null){
			result.put("company", companyMap.get(employee.getCompanyId()));
		}
		
		//??????????????????
		List<EmpType> empTypeList = empTypeMapper.getListByCondition(null);
		result.put("empTypeList", empTypeList);
		Map<Long,EmpType> empTypeMap = empTypeList.stream().collect(Collectors.toMap(EmpType::getId, Function.identity(), (key1, key2) -> key2));
		if(empTypeMap!=null&&empTypeMap.containsKey(employee.getEmpTypeId())){
			result.put("empType", empTypeMap.get(employee.getEmpTypeId()));
		}
		
		//????????????
		List<Depart> departList = departService.getListByCondition(null);
		result.put("departList", departList);
		Map<Long,Depart> departMap = departList.stream().collect(Collectors.toMap(Depart::getId, Function.identity(), (key1, key2) -> key2));
		EmpDepart ed  = new EmpDepart();
 		ed.setEmployeeId(employeeId);
 		EmpDepart empDepart = empDepartService.getByCondition(ed);
		if(null != empDepart && null != empDepart.getDepartId()){
			result.put("depart", departMap.get(empDepart.getDepartId()));
			//???????????????
			if(departMap.containsKey(empDepart.getDepartId())&&departMap.get(empDepart.getDepartId()).getLeader()!=null){
				Employee departLeader = employeeMapper.getById(departMap.get(empDepart.getDepartId()).getLeader());
				result.put("departLeader", departLeader);
			}
		}
		
		//??????????????????
		List<Employee> reportToLeaderList = employeeMapper.getAllMLevalEmps(null);
		result.put("reportToLeaderList", reportToLeaderList);
		
		//????????????
		Position positionListCon = new Position();
		positionListCon.setDepartId(empDepart!=null?empDepart.getDepartId():null);
		List<Position> positionList = positionService.getListByCondition(positionListCon);
		result.put("positionList", positionList);
		
		//????????????
		List<CompanyFloor> floorList = companyFloorMapper.getListByCondition(null);
		result.put("floorList", floorList);
		
		//?????????
		User user = userMapper.getByEmployeeId(employeeId);
		result.put("username", user.getUserName());
		
		//????????????
		List<EmpSchool> empSchoolList  = empSchoolMapper.getListByEmployeeId(employeeId);
		result.put("empSchoolList", empSchoolList);
		
		//????????????
		List<EmpTraining> empTrainingList = empTrainingMapper.getListByEmployeeId(employeeId);
		result.put("empTrainingList", empTrainingList);
		
		//????????????
		List<EmpWorkRecord> workRecordList = empWorkRecordMapper.getListByEmployeeId(employeeId);
		result.put("workRecordList", workRecordList);
		
		//???????????????
		List<EmpUrgentContact> urgentContactList = empUrgentContactMapper.getListByEmployeeId(employeeId);
		result.put("urgentContactList", urgentContactList);
		
		//????????????
		List<EmpFamilyMember> familyMemberList = empFamilyMemberMapper.getListByEmployeeId(employeeId);
		result.put("familyMemberList", familyMemberList);
		
		//???????????????
		List<EmpAchievement> achievementList = empAchievementMapper.getListByEmployeeId(employeeId);
		result.put("achievementList", achievementList);
		
		//??????????????????
		List<EmpContract> empContractList = empContractMapper.getListByEmployeeId(employeeId);
		result.put("empContractList", empContractList);
		
		//????????????
		List<EmpAppraise> empAppraiseList = empAppraiseMapper.getListByEmployeeId(employeeId);
		result.put("empAppraiseList", empAppraiseList);
		
		//????????????
		List<EmpPostRecord> empPostRecordList = empPostRecordMapper.getListByEmployeeId(employeeId);
		result.put("empPostRecordList", empPostRecordList);
		
		return result;
	}

	@Override
	public Map<String, Object> uploadEmployeePhoto(CommonsMultipartFile file) throws Exception{
		Map<String,Object> data = new HashMap<String,Object>();
		
		if(file==null){
			data.put("success", false);
			data.put("message", "????????????????????????");
			return data;
		}
		
		String picUlr = "";
		long defaultSize = 10240000;//??????????????????10M
		
 		String filename = file.getOriginalFilename();
 		long fileSize = file.getSize();
 		if(fileSize > defaultSize){
 			throw new OaException("?????????10M???????????????!");
 		}
 		//check??????????????????
 		if(CommonUtils.checkFileSuffix(ConfigConstants.PIC_UPLOAD_TYPE, filename)){
			String fileFullName = "uleOA/photo/employee/" + DateUtils.getNow().replace(" ", "").replace("-", "").replace(":", "") + "/" + filename;
			File temfile = null;
			try {
				temfile = new File(filename);
				FileUtils.writeByteArrayToFile(temfile, UploadUtil.fileToByteArray(file));
				//???????????????????????????url
				String result = UploadUtil.uploadToQiNiu(temfile, fileFullName,	ConfigConstants.QINIU_HOST);
				Map<String, String> resultMap = (Map<String, String>) JSONUtils.readAsMap(result);
				if(null != resultMap && "success".equals(resultMap.get("status"))){
					picUlr = resultMap.get("url");
					data.put("success", true);
					data.put("message", "???????????????");
					data.put("picUlr", picUlr);
				}else{
					throw new Exception("????????????!");
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
 			throw new OaException("???????????????????????????????????????,?????????????????????!");
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
            //????????????Buffer?????????
            byte[] buffer = new byte[1024];
            //??????????????????????????????????????????-1???????????????????????????
            int len = 0;
            //????????????????????????buffer????????????????????????
            while ((len = is.read(buffer)) != -1) {
                //???????????????buffer???????????????????????????????????????????????????????????????len?????????????????????
                outStream.write(buffer, 0, len);
            }
            // ???????????????Base64??????
            photo = new BASE64Encoder().encode(outStream.toByteArray());
        } catch (Exception e) {
        	photo = "";
            logger.error("getImageBase:???????????????????????????");
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
		
		//???????????????????????????
		result.put("companyName", "");//????????????
		result.put("employeeName", "");//????????????
		result.put("employeeCode", "");//????????????
		result.put("departName", "");//????????????
		result.put("positionName", "");//??????
		result.put("reportToLeader", "");//????????????
		result.put("departLeader", "");//???????????????
		result.put("employeeType", "");//????????????
		result.put("jobStatus", "");//????????????
		result.put("photo","");//??????
		result.put("sex", "");//??????
        result.put("marryStatus", "");//????????????
        result.put("country", "");//??????
        result.put("nation", "");//??????
        result.put("birthDay", "");//????????????
        result.put("age", "");//??????
        result.put("politicalStatus", "");//????????????
        result.put("educationLevel", "");//????????????
        result.put("householdRegister", "");//??????
        result.put("address", "");//???????????????
        result.put("industryRelevance", "");//???????????????
        result.put("workingBackground", "");//????????????
        result.put("mobile", "");//?????????
        result.put("workType", "");//????????????
        result.put("floorCode", "");//??????
        result.put("seatCode", "");//?????????
        result.put("userName", "");//?????????
        result.put("email", "");//??????
        result.put("positionLevel", "");//??????
        result.put("positionSeq", "");//????????????
        result.put("positionTitle", "");//??????
        result.put("whetherScheduling", "");//????????????
        result.put("entryTime", "");//????????????
        result.put("probationEndTime", "");//??????????????????
        result.put("ourAge", "");//??????
        result.put("quitTime", "");//????????????
        result.put("protocolEndTime", "");//???????????????
        result.put("uleAccount", "");//????????????
        result.put("workAddress", "");//????????????
    
		if(employeeId==null){
			return result;
		}
		
		Employee employee = employeeMapper.getById(employeeId);
		if(employee==null){
			return result;
		}
		
		if(employee.getCompanyId()!=null){
			Company company = companyMapper.getById(employee.getCompanyId());
			result.put("companyName", company!=null?company.getName():"");//????????????
		}
		result.put("employeeName", StringUtils.isNotBlank(employee.getCnName())?employee.getCnName():"");//????????????
		result.put("employeeCode", StringUtils.isNotBlank(employee.getCode())?employee.getCode():"");//????????????
		
		EmpDepart ed  = new EmpDepart();
 		ed.setEmployeeId(employeeId);
 		EmpDepart empDepart = empDepartService.getByCondition(ed);
		if(null != empDepart && null != empDepart.getDepartId()){
			Depart depart = departService.getById(empDepart.getDepartId());
			result.put("departName", depart!=null?depart.getName():"");//????????????
			if(depart!=null&&depart.getLeader()!=null){
				Employee departLeader = employeeMapper.getById(depart.getLeader());
				result.put("departLeader", departLeader.getCnName());//???????????????
			}
		}
		
 		EmpPosition ep = new EmpPosition();
 		ep.setEmployeeId(employeeId);
 		EmpPosition empPosition = empPositionService.getByCondition(ep);
 		if(null != empPosition && null != empPosition.getPositionId()){
 			Position position = positionService.getById(empPosition.getPositionId());
 			result.put("positionName", position!=null?position.getPositionName():"");//??????
 		}
 		
 		if(employee!=null&&employee.getReportToLeader()!=null){
 			Employee reportToLeader = employeeMapper.getById(employeeId);
 			result.put("reportToLeader", reportToLeader!=null?reportToLeader.getCnName():"");//????????????
 		}
		
 		if(employee.getEmpTypeId()!=null){
 			EmpType empType = empTypeMapper.getById(employee.getEmpTypeId());
 			result.put("employeeType", empType!=null?empType.getTypeCName():"");//????????????
 		}
 		
 		switch(employee.getJobStatus()){
 			case 0:
 				result.put("jobStatus", "??????");//????????????
 				break;
 			case 1:
 				result.put("jobStatus", "??????");//????????????
 				break;
 			case 2:
 				result.put("jobStatus", "?????????");//????????????
 				break;
 			default:
 				break;
 		 
 		}
 		
 		result.put("photo",getImageBase(employee.getPhoto()));//??????
 		
 		switch(employee.getSex()){
			case 0:
				result.put("sex", "???");//??????
				break;
			case 1:
				result.put("sex", "???");//??????
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
 			result.put("marryStatus", configMap.get(employee.getMaritalStatus()).getDisplayName());//????????????
 		}
 		
 		if(employee.getNation()!=null&&configMap.containsKey(employee.getNation())){
 			result.put("nation", configMap.get(employee.getNation()).getDisplayName());//??????
 		}
        
 		if(employee.getPoliticalStatus()!=null&&configMap.containsKey(employee.getPoliticalStatus())){
 			 result.put("politicalStatus", configMap.get(employee.getPoliticalStatus()).getDisplayName());//????????????
 		}
 		
 		if(employee.getDegreeOfEducation()!=null&&configMap.containsKey(employee.getDegreeOfEducation())){
 			 result.put("educationLevel", configMap.get(employee.getDegreeOfEducation()).getDisplayName());//????????????
 		}
 		
 		if(employee.getWhetherScheduling()!=null&&configMap.containsKey(employee.getWhetherScheduling())){
 			result.put("whetherScheduling", configMap.get(employee.getWhetherScheduling()).getDisplayName());//????????????
 		}

		List<String> comConfigCodeList = new ArrayList<String>();
		comConfigCodeList.add("country");
		comConfigCodeList.add("industryCorrelation");
		comConfigCodeList.add("typeOfWork");
		List<CompanyConfig> comConfigList = companyConfigMapper.getListByCodes(comConfigCodeList);
		Map<Long,CompanyConfig> comConfigMap = comConfigList.stream().collect(Collectors.toMap(CompanyConfig::getId, Function.identity(), (key1, key2) -> key2));
 		
		if(employee.getCountry()!=null&&comConfigMap.containsKey(employee.getCountry())){
			result.put("country", comConfigMap.get(employee.getCountry()).getDisplayName());//??????
		}
 		
 		if(employee.getIndustryRelevance()!=null&&comConfigMap.containsKey(employee.getIndustryRelevance())){
 			result.put("industryRelevance", comConfigMap.get(employee.getIndustryRelevance()).getDisplayName());//???????????????
 		}
 		
 		if(employee.getWorkType()!=null&&comConfigMap.containsKey(employee.getWorkType())){
 			 result.put("workType", comConfigMap.get(employee.getWorkType()).getDisplayName());//????????????
 		}
        
 		result.put("birthDay", employee.getBirthday()!=null?DateUtils.format(employee.getBirthday(), DateUtils.FORMAT_SHORT):"");//????????????
 		result.put("age", employee.getAge()!=null?employee.getAge():"");//??????
 		result.put("householdRegister", StringUtils.isNotBlank(employee.getHouseholdRegister())?employee.getHouseholdRegister():"");//??????
 		result.put("address", StringUtils.isNotBlank(employee.getAddress())?employee.getAddress():"");//???????????????
 		result.put("workingBackground", StringUtils.isNotBlank(employee.getWorkingBackground())?employee.getWorkingBackground():"");//????????????
 		result.put("mobile", StringUtils.isNotBlank(employee.getMobile())?employee.getMobile():"");//?????????
 		result.put("email", StringUtils.isNotBlank(employee.getEmail())?employee.getEmail():"");//??????
 		result.put("seatCode", StringUtils.isNotBlank(employee.getSeatCode())?employee.getSeatCode():"");//?????????
 		result.put("positionLevel", StringUtils.isNotBlank(employee.getPositionLevel())?employee.getPositionLevel():"");//??????
        result.put("positionSeq", StringUtils.isNotBlank(employee.getPositionSeq())?employee.getPositionSeq():"");//????????????
        result.put("positionTitle", StringUtils.isNotBlank(employee.getPositionTitle())?employee.getPositionTitle():"");//??????
        result.put("entryTime", employee.getFirstEntryTime()!=null?DateUtils.format(employee.getFirstEntryTime(), DateUtils.FORMAT_SHORT):"");//????????????
        result.put("probationEndTime", employee.getProbationEndTime()!=null?DateUtils.format(employee.getProbationEndTime(), DateUtils.FORMAT_SHORT):"");//??????????????????
        result.put("ourAge", employee.getOurAge()!=null?employee.getOurAge():"");//??????
        result.put("quitTime", employee.getQuitTime()!=null?DateUtils.format(employee.getQuitTime(), DateUtils.FORMAT_SHORT):"");//????????????
        result.put("protocolEndTime", employee.getProtocolEndTime()!=null?DateUtils.format(employee.getProtocolEndTime(), DateUtils.FORMAT_SHORT):"");//???????????????
        result.put("uleAccount", StringUtils.isNotBlank(employee.getUleAccount())?employee.getUleAccount():"");//????????????
        String workAddress = (StringUtils.isNotBlank(employee.getWorkAddressProvince())?employee.getWorkAddressProvince():"")
        		+" " + (StringUtils.isNotBlank(employee.getWorkAddressCity())?employee.getWorkAddressCity():"");
        result.put("workAddress",workAddress);//????????????
 		
        //?????????
		User user = userMapper.getByEmployeeId(employeeId);
		result.put("userName", user!=null?user.getUserName():"");//?????????
        
        result.put("floorCode", StringUtils.isNotBlank(employee.getFloorCode())?employee.getFloorCode():"");//??????
       
        //????????????
  		List<EmpSchool> empSchoolList  = empSchoolMapper.getListByEmployeeId(employeeId);
  		List<Map<String,Object>> schoolList = new ArrayList<Map<String,Object>>();
  		for(EmpSchool school : empSchoolList){
  			Map<String,Object> data = new HashMap<String,Object>();
  			data.put("school_time", DateUtils.format(school.getStartTime(), DateUtils.FORMAT_SHORT)+ " ???  "+DateUtils.format(school.getEndTime(), DateUtils.FORMAT_SHORT));
  			data.put("school_name", school.getSchool());
  			data.put("major", school.getMajor());
  			data.put("education", "");
  			if(configMap!=null&&school.getEducation()!=null&&configMap.containsKey(school.getEducation())){
  				data.put("education", configMap.get(school.getEducation()).getDisplayName());
  			}
  			schoolList.add(data);
  		}
  		result.put("schoolList", schoolList);
  		
        //????????????
		List<EmpTraining> empTrainingList = empTrainingMapper.getListByEmployeeId(employeeId);
        List<Map<String,Object>> trainList = new ArrayList<Map<String,Object>>();
        for(EmpTraining train:empTrainingList){
          if(train.getIsCompanyTraining()==0){
        	  Map<String,Object> data = new HashMap<String,Object>();
              data.put("t_time", DateUtils.format(train.getStartTime(), DateUtils.FORMAT_SHORT)+" ???  "+DateUtils.format(train.getEndTime(), DateUtils.FORMAT_SHORT));
              data.put("t_institution", train.getTrainingInstitutions());
              data.put("t_certificate", train.getObtainCertificate());
              data.put("t_content", train.getContent());
              trainList.add(data);
          }
        }
        result.put("trainList", trainList);

        //????????????
		List<EmpWorkRecord> workRecordList = empWorkRecordMapper.getListByEmployeeId(employeeId);
        List<Map<String,Object>> workList = new ArrayList<Map<String,Object>>();
        for(EmpWorkRecord work:workRecordList){
	        Map<String,Object> data = new HashMap<String,Object>();
	        data.put("w_time", DateUtils.format(work.getStartTime(), DateUtils.FORMAT_SHORT)+" ???  "+DateUtils.format(work.getEndTime(), DateUtils.FORMAT_SHORT));
	        data.put("w_c_name", work.getCompanyName());
	        data.put("w_position", work.getPositionName());
	        data.put("w_task", work.getPositionTask());
	        workList.add(data);
        }
        result.put("workList", workList);
		return result;
	}
	
}
