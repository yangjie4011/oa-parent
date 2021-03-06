package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.mapper.EmpPositionMapper;
import com.ule.oa.base.po.AttnUsers;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpContractService;
import com.ule.oa.base.service.EmpDepartService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmployeeAppService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.MD5Encoder;
import com.ule.oa.common.utils.json.JSONUtils;

@Service
public class EmpPositionServiceImpl implements EmpPositionService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpPositionMapper empPositionMapper;
	@Autowired
	private EmployeeAppService employeeAppService;
	@Autowired
	private EmpDepartService empDepartService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private DepartService departService;
	@Autowired
	private UserService userService;
	@Autowired
	private EmpContractService empContractService;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Autowired
	private EmpLeaveService empLeaveService;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();

	@Override
	public void save(EmpPosition model) {
		empPositionMapper.save(model);
	}

	@Override
	public int updateById(EmpPosition model) throws OaException {
		int updateCount = empPositionMapper.updateById(model);
		
		if(updateCount == 0){
			throw new OaException("?????????????????????????????????????????????????????????????????????????????????");
		}
		
		return updateCount;
	}

	@Override
	public List<EmpPosition> getListByCondition(EmpPosition model) {
		return empPositionMapper.getListByCondition(model);
	}

	@Override
	public EmpPosition getByCondition(EmpPosition model){
		List<EmpPosition> list = getListByCondition(model);
		
		if(null != list && list.size()>0){
			return list.get(0);
		}
		
		return model;
	}
	
	/**
	  * updateEmpPositionInfo(??????-????????????)
	  * @Title: updateEmpPositionInfo
	  * @Description: ????????????????????????
	  * @param employee
	  * @return
	  * @throws Exception    ????????????
	  * boolean    ????????????
	  * @throws
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean updateEmpPositionInfo(EmployeeApp employee) throws Exception{
		boolean flag = false;
		 Date quitTime = employee.getQuitTime();
		 Date joinTime = employee.getFirstEntryTime();
			if(null != employee){
				Long employeeId = employee.getId();
				//??????????????????
				if(null != employee.getFirstEntryTime()){
					//???????????????????????????????????????
					String firstEntryTime = DateUtils.format(joinTime, DateUtils.FORMAT_SHORT);
					//????????????????????????,??????????????????????????????
					Integer days = quitTime == null ? DateUtils.countDays(firstEntryTime,DateUtils.FORMAT_SHORT):DateUtils.getIntervalDays(joinTime,quitTime);//????????????
					Double years = days/(365*1.0);
					employee.setOurAge(years);//??????
					double beforeWorkAge = employee.getBeforeWorkAge()!=null?employee.getBeforeWorkAge().doubleValue():0d;
					employee.setWorkAge(years+beforeWorkAge);//?????????
				}
				
				if(null != quitTime){
					empLeaveService.updateLeaveByEmpInfo(employee);
				}
				employeeAppService.updateEmpBaseInfo(employee);
				
				if(null != employee.getFingerprintId()){/**???????????????ID???????????????????????????????????????**/
				    String OA_SERVICE_URL = configCacheManager.getConfigDisplayCode("MO_SERVICE_URL");
					//????????????ID???????????????????????????oa_employee_id??????????????????
					String userid = "";
					
					HashMap<String, String> paramMap3 = new HashMap<String,String>();
					paramMap3.put("fingerprintId", String.valueOf(employee.getFingerprintId()));
					//OA_SERVICE_URL = "http://test.ule.com:8080/oaService";
					logger.info("??????????????????ID??????????????????:"+OA_SERVICE_URL+"/attnUsers/selectByFingerprintId.htm");
					
					HttpRequest req3 = new HttpRequest.Builder().url(OA_SERVICE_URL+"/attnUsers/selectByFingerprintId.htm").post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
		    				ContentCoverter.formConvertAsString(paramMap3)).build();
		    		HttpResponse rep3 = client.sendRequest(req3);
					String response3 = rep3.fullBody();
					
					Map<?, ?> responseMap3 = JSONUtils.readAsMap(response3);
					String resultCode = (String)responseMap3.get("result");
					if("success".equals(resultCode)){
						userid = (String)responseMap3.get("userid");
					}else{
						throw new OaException((String)responseMap3.get("message"));
					}
					
					//???OA??????ID??????????????????user???
					AttnUsers attnUsers = new AttnUsers();
					attnUsers.setUserid(Integer.valueOf(userid));
					attnUsers.setOaEmpId(employee.getId());
					HashMap<String, String> paramMap4 = new HashMap<String,String>();
					paramMap4.put("jsonData", JSONUtils.write(attnUsers));
					logger.info("????????????employeeId??????:"+OA_SERVICE_URL+"/attnUsers/updateById.htm");
					
					HttpRequest req4 = new HttpRequest.Builder().url(OA_SERVICE_URL+"/attnUsers/updateById.htm").post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
		    				ContentCoverter.formConvertAsString(paramMap4)).build();
		    		HttpResponse rep4 = client.sendRequest(req4);
					String response4 = rep4.fullBody();
					
					logger.info("?????????????????????:"+response4);
					
				}
				
				//??????????????????????????????????????????????????????????????????????????????
				if(null != employee.getEmpPosition()){
					/***************??????/??????????????????????????? ,??????,???????????? start****************/
					//1.????????????id????????????????????????
					employee.getEmpPosition().setEmployeeId(employeeId);
					EmpPosition ep = this.getByCondition(employee.getEmpPosition());
					//2.??????????????????????????????????????????????????????
					if(null != ep && null != ep.getId()){
						ep.setPositionId(employee.getEmpPosition().getPosition().getId());
						ep.setUpdateTime(new Date());
						ep.setUpdateUser(userService.getCurrentAccount());
						this.updateById(ep);
					}else{
						ep = new EmpPosition();
						ep.setEmployeeId(employeeId);
						ep.setPositionId(employee.getEmpPosition().getPosition().getId());
						ep.setCreateTime(new Date());
						ep.setCreateUser(userService.getCurrentAccount());
						ep.setDelFlag(ConfigConstants.IS_NO_INTEGER);
						ep.setVersion(ConfigConstants.DEFAULT_VERSION);
						this.save(ep);
					}
					/***************??????/???????????????????????????,??????,????????????  end****************/
					
				}
				
				//????????????????????????
				employee.getEmpDepart().setEmployeeId(employeeId);
				EmpDepart ed = empDepartService.getByCondition(employee.getEmpDepart());
				if(null != ed && null != ed.getId()){
					ed.setDepartId(employee.getEmpDepart().getDepart().getId());
					ed.setUpdateTime(new Date());
					ed.setUpdateUser(userService.getCurrentAccount());
					empDepartService.updateById(ed);
				}else{
					ed = new EmpDepart();
					ed.setEmployeeId(employeeId);
					ed.setDepartId(employee.getEmpDepart().getDepart().getId());
					ed.setCreateTime(new Date());
					ed.setCreateUser(userService.getCurrentAccount());
					ed.setDelFlag(ConfigConstants.IS_NO_INTEGER);
					ed.setVersion(ConfigConstants.DEFAULT_VERSION);
					empDepartService.save(ed);
				}
			}
			
			flag = true;
		
		return flag;
	}
	public static void main(String[] args) {
		System.out.println(MD5Encoder.md5Hex("hejia@ule.com"));
	}
}
