package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.DailyHeathSignMapper;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.DailyHeathSignTbl;
import com.ule.oa.base.service.DailyHeathSignService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;

/**
 * 健康打卡
 * @author yangjie
 *
 */
@Service
public class DailyHeathSignServiceImpl implements DailyHeathSignService {
	
	@Autowired
	private DailyHeathSignMapper dailyHeathSignMapper;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
    private DepartMapper departMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> save(HttpServletRequest request, User user) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		
		String list = request.getParameter("detailList");
		List<DailyHeathSignTbl> detailList = new ArrayList<DailyHeathSignTbl>();
		JSONArray array = JSONArray.fromObject(list);
		
		if(user==null||user.getEmployee()==null){
			throw new OaException("请重新登陆系统！");
		}
		
		if(array==null||array.size()<=0){
			throw new OaException("健康打卡数据为空！");
		}
		
		Date signDate = new Date();
		List<DailyHeathSignTbl> isSign = dailyHeathSignMapper
				.getByEmployeeIdAndSignDate(user.getEmployee().getId(), 2, signDate);
		if(isSign!=null&&isSign.size()>0){
			throw new OaException("今日已成功打卡！");
		}
		
		for(int i=0;i<array.size();i++){
			DailyHeathSignTbl detail = new DailyHeathSignTbl();
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			
			if(jsonObject.getInt("type")==1){
				detail.setType(1);
				detail.setEmployeeId(user.getEmployee().getId());
				detail.setQuestionNum(jsonObject.getInt("question_num"));
				detail.setQuestion(jsonObject.getString("question"));
				detail.setAnswerNum(jsonObject.getInt("answer_num"));
				detail.setAnswer(jsonObject.getString("answer"));
				detail.setCreateTime(new Date());
				detail.setCreateUser(user.getEmployee().getCnName());
				detailList.add(detail);
			}else{
				detail.setSignDate(signDate);
				detail.setType(2);
				detail.setEmployeeId(user.getEmployee().getId());
				detail.setQuestionNum(jsonObject.getInt("question_num"));
				detail.setQuestion(jsonObject.getString("question"));
				detail.setAnswerNum(jsonObject.getInt("answer_num"));
				detail.setAnswer(jsonObject.getString("answer"));
				detail.setCreateTime(new Date());
				detail.setCreateUser(user.getEmployee().getCnName());
				detailList.add(detail);
			}
		}
		dailyHeathSignMapper.batchSave(detailList);
		map.put("success", true);
		return map;
	}

	@Override
	public List<DailyHeathSignTbl> getByEmployeeIdAndSignDate(Long employeeId,
			Integer type, Date signDate) {
		return dailyHeathSignMapper.getByEmployeeIdAndSignDate(employeeId, type, signDate);
	}

	@Override
	public void sendMailToNoSignEmp() {
		//检查当日所有未健康登记的员工，发送邮件给员工本人+领导（汇报对象直到总监副总监部门负责人任意一个）
		Date signDate = new Date();
		//查询当日登记过的员工
		List<Long> signEmployeeIdList = dailyHeathSignMapper.getSignEmployeeIdByDate(signDate);
		//查出所有未登记过的员工
		List<Long> jobStatusList = new ArrayList<Long>();
		jobStatusList.add(0L);
		jobStatusList.add(2L);
		Employee notSignParam = new Employee();
		notSignParam.setJobStatusList(jobStatusList);
		if(signEmployeeIdList!=null&&signEmployeeIdList.size()>0){
			notSignParam.setAbsenteeismids(signEmployeeIdList);
		}
		List<Employee> notSignList = employeeMapper.getListByCondition(notSignParam);
		
		for(Employee data:notSignList){
			String content = "Dear "+data.getCnName()+":<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 您今日还未完成“健康打卡”，请尽快前往随心邮H5端MO系统完成打卡！！！（10点30分系统将截止今日的数据统计。）";
			content += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;疫情期间，请大家注意防护！";
				
			List<Employee> sendList = new ArrayList<Employee>();
			sendList.add(data);
			//申请人第一级汇报对象
			Employee assignee = employeeService.getById(data.getId());
			//
			int i= 0;
			while(true){
				i = i + 1;
				if(i>10){
					break;//员工汇报关系不可能超过10级
				}
				if(assignee!=null){
					//判断该人职位序列是总监，副总监，VP，COO或者部门负责人
					if(employeeService.hasPowerSuperior(assignee.getReportToLeader())){
						sendList.add(assignee);
						break;
					}else{
						assignee = employeeService.getById(assignee.getReportToLeader());
						if(assignee!=null){
							sendList.add(assignee);
						}
					}
				}else{
					break;
				}
			}
			for(Employee send:sendList){
				try {
					if(!"TOM001".equals(send.getCode())){
						SendMailUtil.sendNormalMail(content, send.getEmail(),send.getCnName(), "每日健康打卡提醒");
					}
				} catch (Exception e) {
					
				}
			}
			
		}
		
	}

	@Override
	public void sendMailToLeaderOfNoSignEmp() {
		/**
		 * 检查所有符合条件的领导，发送未登记员工统计数据
		 */
		
		 Date signDate = new Date();
		//查询所有部门
		List<Depart> departList = departMapper.getAllDepart();
		
		List<Long> jobStatusList = new ArrayList<Long>();
		jobStatusList.add(0L);
		jobStatusList.add(2L);
		
		for(Depart data:departList){
            
			if("117".equals(data.getCode())){
			   //技术开发部，只抄送到总监副总监，先找到该部门所有职位符合的领导
		       List<Long> needNoticeEmpId = dailyHeathSignMapper.getNeedNoticeLeader(data.getId());
		       for(Long employeeId:needNoticeEmpId){
		    	   Employee leader = employeeMapper.getById(employeeId);
		    	   //查找下属员工
		    	  List<Employee> subList =  employeeMapper.getByReportToLeadersAndDepartId(employeeId,data.getId());
		    	  List<Long> signEmployeeIdList = dailyHeathSignMapper.getSignEmployeeIdByDateAndDepart(signDate, data.getId());
		          Map<Long,Long> signEmployeeIdMap = new HashMap<Long,Long>();
		    	  for(Long empId:signEmployeeIdList){
		    		  signEmployeeIdMap.put(empId, empId);
		    	  }
		    	  //找出没有登记的下属
		    	  List<Long> notSignIdList = new ArrayList<Long>();
		    	  for(Employee sub:subList){
		    		  if((signEmployeeIdMap==null)||(signEmployeeIdMap!=null&&!signEmployeeIdMap.containsKey(sub.getId()))){
		    			  notSignIdList.add(sub.getId());
		    		  }
		    	  }
		    	  if(notSignIdList!=null&&notSignIdList.size()>0){
			    		Employee notSignP = new Employee();
			    		notSignP.setIds(notSignIdList);
			    		notSignP.setJobStatusList(jobStatusList);
			    		 
			    		List<Employee> notSignList = employeeMapper.getListByCondition(notSignP);
			    		String title = "未完成每日健康打卡名单—"+data.getName()+DateUtils.format(signDate, DateUtils.FORMAT_SHORT_CN);
						
						String content = "<br/>截止"+DateUtils.format(signDate, DateUtils.FORMAT_SHORT_CN)
								+"星期"+DateUtils.getWeek(signDate)+"10:30，"+data.getName()+"未完成每日健康打卡人员有：";
						String nameStr = "";
					    for(Employee notsign:notSignList){
					    	nameStr +=notsign.getCnName()+",";
					    }
					    nameStr = nameStr.substring(0,nameStr.length()-1);
					    content = content + nameStr + "。<br/>请安排员工尽快完成此项工作！";
					    try {
					    	if(!"TOM001".equals(leader.getCode())){
					    		SendMailUtil.sendNormalMail(content, leader.getEmail(),leader.getCnName(), title);
					    	}
							SendMailUtil.sendNormalMail(content, "ulehr@ule.com","ulehr", title);
					    } catch (Exception e) {
					}
		    	  }
		       }
			}else{
			   //其他部门抄送到部门负责人
			   if(data.getLeader()!=null&&!"100".equals(data.getCode())&&!"140".equals(data.getCode())){
				   Employee leader = employeeMapper.getById(data.getLeader());
				   if(leader!=null){
					   //查询当日登记过的员工
					    List<Long> signEmployeeIdList = dailyHeathSignMapper.getSignEmployeeIdByDateAndDepart(signDate, data.getId());
						Employee notSignParam = new Employee();
						notSignParam.setJobStatusList(jobStatusList);
						if(signEmployeeIdList!=null&&signEmployeeIdList.size()>0){
							notSignParam.setAbsenteeismids(signEmployeeIdList);
						}
						List<Employee> totalList = employeeMapper.getListByDepartId(data.getId());
						List<Long> totalIdList = new ArrayList<Long>();
						for(Employee tid:totalList){
							totalIdList.add(tid.getId());
						}
						if(totalIdList!=null&&totalIdList.size()>0){
							notSignParam.setIds(totalIdList);
						}
						notSignParam.setJobStatusList(jobStatusList);
						List<Employee> notSignList = employeeMapper.getListByCondition(notSignParam);
						
						if(notSignList!=null&&notSignList.size()>0){
							String title = "未完成每日健康打卡名单—"+data.getName()+DateUtils.format(signDate, DateUtils.FORMAT_SHORT_CN);
							
							String content = "<br/>截止"+DateUtils.format(signDate, DateUtils.FORMAT_SHORT_CN)
									+"星期"+DateUtils.getWeek(signDate)+"10:30，"+data.getName()+"未完成每日健康打卡人员有：";
							String nameStr = "";
						    for(Employee notsign:notSignList){
						    	nameStr +=notsign.getCnName()+",";
						    }
						    nameStr = nameStr.substring(0,nameStr.length()-1);
						    content = content + nameStr + "。<br/>请安排员工尽快完成此项工作！";
						    try {
						    	if(!"TOM001".equals(leader.getCode())){
						    		SendMailUtil.sendNormalMail(content, leader.getEmail(),leader.getCnName(), title);
						    	}
								SendMailUtil.sendNormalMail(content, "ulehr@ule.com","ulehr", title);
							} catch (Exception e) {
							}
						
						}
				   }
			   }
				
			}
		}
		
	}

	@Override
	public HSSFWorkbook exportSignDataByDate(Date signDate) {
		/**
		 * 根据日期导出登记数据
		 */
		
		String signDateStr = DateUtils.format(signDate, DateUtils.FORMAT_SHORT_CN);
		
		//查询当日登记过的员工
	    List<Long> signEmployeeIdList = dailyHeathSignMapper.getSignEmployeeIdByDate(signDate);
	    
	    //查询基本信息
		List<Map<String,Object>> employeeInfoList = dailyHeathSignMapper.getBaseInfo();
		
		//查询只填一次的数据
		List<DailyHeathSignTbl> baseInfoList = dailyHeathSignMapper
				.getAnswerByTypeAndSignDate(null, 1, null);
		Map<Long,List<DailyHeathSignTbl>> baseInfoMap = baseInfoList.stream().collect(Collectors.groupingBy(DailyHeathSignTbl :: getEmployeeId));
		
		//查询每天的数据
		List<DailyHeathSignTbl> dailyInfoList = dailyHeathSignMapper
				.getAnswerByTypeAndSignDate(signEmployeeIdList,2, signDate);
		Map<Long,List<DailyHeathSignTbl>> dailyInfoMap = dailyInfoList.stream().collect(Collectors.groupingBy(DailyHeathSignTbl :: getEmployeeId));
		
		for(Map<String,Object> data:employeeInfoList){
			String workAddress = "";
			if(data.get("workProvince")!=null){
				workAddress = (String) data.get("workProvince");
			}
			if(data.get("workCity")!=null){
				workAddress += (String) data.get("workCity");
			}
			data.put("workAddress", workAddress);
			data.put("signDate", signDateStr);
				  
			Long employeeId = (Long) data.get("id");
			if(baseInfoMap!=null&&baseInfoMap.containsKey(employeeId)){
				List<DailyHeathSignTbl> baseAnswerList = baseInfoMap.get(employeeId);
				for(DailyHeathSignTbl ans1:baseAnswerList){
					String key = "answer_"+String.valueOf(ans1.getAnswerNum());
					data.put(key, ans1.getAnswer());
				}
				
			}
			if(dailyInfoMap!=null&&dailyInfoMap.containsKey(employeeId)){
				List<DailyHeathSignTbl> dailyAnswerList = dailyInfoMap.get(employeeId);
				for(DailyHeathSignTbl ans1:dailyAnswerList){
					String key = "answer_"+String.valueOf(ans1.getAnswerNum());
					data.put(key, ans1.getAnswer());
				}
				
			}
		}
		
		String[] keys={"code", "cnName","type_c_name", "departName", "mobile","workAddress","signDate","answer_1","answer_2", "answer_3", 
				"answer_4", "answer_5", "answer_6", "answer_7", "answer_8", "answer_9", "answer_10",
				"answer_11","answer_12","answer_13","answer_14","answer_15","answer_16"};
		String[] titles={"员工编号", "姓名","员工类型", "部门","手机号码","工作地点","日期","户籍", "籍贯", "现上海常住地址", "是否接触过湖北地区人员", 
				"2020年春节休假是否离开过上海(或工作城市)", "是否去过或途径湖北", "离开上海（或工作城市）去的地方", "离沪日期", 		
				"返沪日期","自驾、飞机应填航班号、火车应填班次等","当前所在地","今天你的个人健康状况","是否封城封村","被隔离情况",
				"近期是否有密切接触以下人员：（含家属）","家属健康情况"}; 
		return ExcelUtil.exportExcel(employeeInfoList, keys, titles, "健康登记报表.xls");
	}

}
