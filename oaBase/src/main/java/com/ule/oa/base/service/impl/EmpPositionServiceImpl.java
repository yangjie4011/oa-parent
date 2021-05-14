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
			throw new OaException("您当前修改的员工职位信息已经被其它人修改过，请重新编辑");
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
	  * updateEmpPositionInfo(更新-在职信息)
	  * @Title: updateEmpPositionInfo
	  * @Description: 更新员工在职信息
	  * @param employee
	  * @return
	  * @throws Exception    设定文件
	  * boolean    返回类型
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
				//更新员工信息
				if(null != employee.getFirstEntryTime()){
					//入职日期变了，司龄相应改变
					String firstEntryTime = DateUtils.format(joinTime, DateUtils.FORMAT_SHORT);
					//若设置了离职日期,司龄直接算到离职那天
					Integer days = quitTime == null ? DateUtils.countDays(firstEntryTime,DateUtils.FORMAT_SHORT):DateUtils.getIntervalDays(joinTime,quitTime);//入职天数
					Double years = days/(365*1.0);
					employee.setOurAge(years);//司龄
					double beforeWorkAge = employee.getBeforeWorkAge()!=null?employee.getBeforeWorkAge().doubleValue():0d;
					employee.setWorkAge(years+beforeWorkAge);//总工龄
				}
				
				if(null != quitTime){
					empLeaveService.updateLeaveByEmpInfo(employee);
				}
				employeeAppService.updateEmpBaseInfo(employee);
				
				if(null != employee.getFingerprintId()){/**更改了指纹ID，可以优化判断是否有值变化**/
				    String OA_SERVICE_URL = configCacheManager.getConfigDisplayCode("MO_SERVICE_URL");
					//校验指纹ID是否存在，存在并且oa_employee_id为空才能入职
					String userid = "";
					
					HashMap<String, String> paramMap3 = new HashMap<String,String>();
					paramMap3.put("fingerprintId", String.valueOf(employee.getFingerprintId()));
					//OA_SERVICE_URL = "http://test.ule.com:8080/oaService";
					logger.info("入职根据指纹ID查询用户地址:"+OA_SERVICE_URL+"/attnUsers/selectByFingerprintId.htm");
					
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
					
					//将OA员工ID关联到考勤机user表
					AttnUsers attnUsers = new AttnUsers();
					attnUsers.setUserid(Integer.valueOf(userid));
					attnUsers.setOaEmpId(employee.getId());
					HashMap<String, String> paramMap4 = new HashMap<String,String>();
					paramMap4.put("jsonData", JSONUtils.write(attnUsers));
					logger.info("入职关联employeeId地址:"+OA_SERVICE_URL+"/attnUsers/updateById.htm");
					
					HttpRequest req4 = new HttpRequest.Builder().url(OA_SERVICE_URL+"/attnUsers/updateById.htm").post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
		    				ContentCoverter.formConvertAsString(paramMap4)).build();
		    		HttpResponse rep4 = client.sendRequest(req4);
					String response4 = rep4.fullBody();
					
					logger.info("关联考勤机结果:"+response4);
					
				}
				
				//更新员工职位职位信息（职位表中记录了职级和职位序列）
				if(null != employee.getEmpPosition()){
					/***************更新/保存员工和职位关系 ,职级,职位序列 start****************/
					//1.根据员工id找到员工职位信息
					employee.getEmpPosition().setEmployeeId(employeeId);
					EmpPosition ep = this.getByCondition(employee.getEmpPosition());
					//2.如果员工存在职位，则更新，否则就新增
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
					/***************更新/保存员工和职位关系,职级,职位序列  end****************/
					
				}
				
				//更新员工部门信息
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
