package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ule.oa.base.mapper.EmpQuitRecordMapper;
import com.ule.oa.base.mapper.EmployeeAppMapper;
import com.ule.oa.base.mapper.EmployeeClassMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.RabcUserRoleMapper;
import com.ule.oa.base.mapper.ScheduleGroupMapper;
import com.ule.oa.base.mapper.UserMapper;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.QuitHistory;
import com.ule.oa.base.po.RabcUserRole;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmpQuitRecordService;
import com.ule.oa.base.service.QuitHistoryService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.json.JSONUtils;

/**
  * @ClassName: EmpQuitRecordServiceImpl
  * @Description: 员工离职记录业务层
  * @author minsheng
  * @date 2017年7月27日 下午2:12:41
 */
@Service
public class EmpQuitRecordServiceImpl implements EmpQuitRecordService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpQuitRecordMapper empQuitRecordMapper;
	@Autowired
	private EmployeeAppMapper employeeAppMapper;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private QuitHistoryService quitHistoryService;
	@Autowired
	private ScheduleGroupMapper scheduleGroupMapper;
	@Autowired
	private EmployeeClassMapper employeeClassMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private RabcUserRoleMapper rabcUserRoleMapper;
	@Autowired
	private UserService userService;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	
	
	/**
	 * @throws OaException 
	  * empJobStatusDoc(离职员工归档)
	  * @Title: empJobStatusDoc
	  * @Description: 离职员工归档
	  * void    返回类型
	  * @throws
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void empJobStatusDoc() throws OaException{
		//1.生成员工离职记录
		int recordCount = empQuitRecordMapper.saveEmpLeaveTime();
		//2.循环 一条条改成离职状态 在保存历史数据
		//把数据  待离职的员工查出来 然后循环修改状态
		//小于等于
		Employee empjob=new Employee();
		  empjob.setJobStatus(2);
		  empjob.setQuitEltTimeEnd(new Date());
			List<Employee> quitList = employeeMapper.getQuitPageList(empjob);
			if(quitList.size()>0){
				for (Employee employeeTemplet : quitList) {
					//先改离职状态在保存历史数据
					employeeAppMapper.updateEmpJobStatusById(employeeTemplet.getId());
					
					Employee LeaderName = null;
					Employee departName = null;
					if(employeeTemplet.getReportToLeader()!=null){
						LeaderName = employeeMapper.getById(employeeTemplet.getReportToLeader());
					}
					if(employeeTemplet.getDepartLeaderId()!=null){
						departName = employeeMapper.getById(employeeTemplet.getDepartLeaderId());
					}	
					
					//离职操作时保存数据  
					QuitHistory quitHistory=new QuitHistory();
					Date createTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
					quitHistory.setEmployeeId(employeeTemplet.getId());
					quitHistory.setEmployeeType(employeeTemplet.getEmpTypeName());
					quitHistory.setDepartName(employeeTemplet.getDepartName());
					quitHistory.setPositionName(employeeTemplet.getPositionName());
					quitHistory.setReportLeader(LeaderName!=null?LeaderName.getCnName():"");
					quitHistory.setDepartHeader(departName!=null?departName.getCnName():"");
					quitHistory.setCreateTime(createTime);
					quitHistory.setCreateUser("system");
					quitHistory.setDelFlag(0);
					quitHistoryService.save(quitHistory);
					//离职后删除员工排班班组
					scheduleGroupMapper.delByEmpId(employeeTemplet.getId());
					//离职后删除员工排班班次
					EmployeeClass employeeClass = new EmployeeClass();
					employeeClass.setUpdateTime(createTime);
					employeeClass.setUpdateUser("system");
					employeeClass.setEmployId(employeeTemplet.getId());
					employeeClass.setClassDate(createTime);
					employeeClassMapper.deleteByQuitTime(employeeClass);
					
					User user = userMapper.getByEmployeeId(employeeTemplet.getId());
                    //将sso  通行证注销掉
					try{
						if(user!=null){
		         			HashMap<String, String> paramExistMap = new HashMap<String,String>();
		         			paramExistMap.put("searchType", "1");
		         			paramExistMap.put("searchValue", user.getUserName());
		         			
         					HttpRequest req = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_IS_EXIST).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
         		    				ContentCoverter.formConvertAsString(paramExistMap)).build();
         		    		HttpResponse rep = client.sendRequest(req);
         					String responseExist = rep.fullBody();
         					
         					Map<?, ?> responseExistMap = JSONUtils.readAsMap(responseExist);
         				    String resultCodeExist = (String)responseExistMap.get("errorCode");
         		            if("".equals(resultCodeExist)){
         		            	logger.info("注销员工通行证start:username="+user.getUserName());
    							HashMap<String, String> paramMap = new HashMap<String,String>();
    							paramMap.put("username", user.getUserName());
    							paramMap.put("jobStatus", "3");
         						
         						HttpRequest req1 = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_QUIT).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
             		    				ContentCoverter.formConvertAsString(paramMap)).build();
             		    		HttpResponse rep1 = client.sendRequest(req1);
             					String response = rep1.fullBody();
         						
         						Map<?, ?> responseMap = JSONUtils.readAsMap(response);
         						String resultCode = (String)responseMap.get("errorCode");
         						logger.info("注销员工通行证end:resultCode="+resultCode);
         		            }else{
         		            	logger.info("注销员工通行证start:username="+user.getUserName()+"不存在");
         		            }
						}
					}catch(Exception e){
						logger.error("注销员工通行证error:"+e.getMessage());
					}
					//清空后台该用户所拥有权限角色
					User quitUsers = userMapper.getByEmployeeId(employeeTemplet.getId());
					RabcUserRole rabcUserRole=new RabcUserRole();
					rabcUserRole.setUserId(quitUsers.getId());
					rabcUserRole.setDelFlag(1);
					rabcUserRole.setUpdateTime(new Date());
					rabcUserRole.setUpdateUser("system");
					rabcUserRoleMapper.updateUserRole(rabcUserRole);
					
					
//					//删除登录user数据
//					User delUserP = new User();
//					delUserP.setDelFlag(1);
//					delUserP.setUpdateTime(new Date());
//					delUserP.setUpdateUser("systtem");
//					delUserP.setEmployeeId(employeeTemplet.getId());
//					userMapper.updateByEmployeeId(delUserP);
				}
			}	
		if(recordCount != quitList.size()){
			throw new OaException("生成员工离职记录记录数为={},更新员工离职状态记录数为={},两个数据不一致不做更新操作",recordCount + "",quitList.size() + "");
		}else{
			logger.info("生成员工离职记录记录数为={},更新员工离职状态记录数为={}",recordCount + "",quitList.size() + "");
		}
	}
}
