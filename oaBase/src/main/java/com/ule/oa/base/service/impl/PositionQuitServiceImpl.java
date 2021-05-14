package com.ule.oa.base.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.text.SimpleDateFormat;
import com.ule.oa.base.mapper.CompanyConfigMapper;
import com.ule.oa.base.mapper.CompanyMapper;
import com.ule.oa.base.mapper.EmpPositionMapper;
import com.ule.oa.base.mapper.EmpTypeMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.PositionQuitMapper;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.EmpType;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.po.HrEmpResign;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.QuitHistory;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationRegisterService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.HiActinstService;
import com.ule.oa.base.service.PositionQuitService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 离职操作
 * @Description: 离职操作
 * @author zhoujinliang
 * @date 2018年3月21日16:49:07
 */
@Service
public class PositionQuitServiceImpl implements
PositionQuitService{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PositionQuitMapper EmpEntryRegistrationMapper;
	@Autowired
	private HiActinstService hiActinstService;
	@Autowired
	private EmpLeaveService empLeaveService;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Resource
	private DepartService departService;
	@Autowired
	private EmpPositionMapper empPositionMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private CompanyConfigMapper companyConfigMapper;
	@Autowired
	private EmpApplicationRegisterService empApplicationRegisterService;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private EmpTypeMapper empTypeMapper;
	
	@Override
	public PageModel<QuitHistory> getReportPageList(
			QuitHistory quit) {
		int page = quit.getPage() == null ? 0 : quit.getPage();
		int rows = quit.getRows() == null ? 0 : quit.getRows();	
		PageModel<QuitHistory> pm = new PageModel<QuitHistory>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		int total = EmpEntryRegistrationMapper.getReportCount(quit);
		pm.setTotal(total);
		quit.setOffset(pm.getOffset());
		quit.setLimit(pm.getLimit());				
		//quit.setDepartName(departService.getDepartAllLeaveName(quit.getDepartId()));
		List<QuitHistory> roles = EmpEntryRegistrationMapper.getReportPageList(quit);
		pm.setRows(roles);
		return pm;
	}



	@Override
	public int getEaoByEmpAndDateCount(Employee quit) {
		return 0;
	}



	@Override
	public HSSFWorkbook exportExcel(Employee quit) {
	    List<Map<String,Object>> datas = getExportReportList(quit);    
			for (Map<String, Object> o : datas) {
				//封装数据
				o.put("companyName",o.get("companyName"));
				o.put("empTypeName",o.get("empTypeName"));
				o.put("cnName",o.get("cn_name"));
				Date ENTRY_DATE,RESIGNATION_date,Quit_time;				
				ENTRY_DATE = (Date) o.get("first_Entry_time");
				RESIGNATION_date = (Date) o.get("Quit_time");
				Quit_time = (Date) o.get("Quit_time");
				o.put("quitTime", DateUtils.format(Quit_time,"yyyy年MM月dd日  HH时mm分ss秒"));
				o.put("firstEntryTime", DateUtils.format(ENTRY_DATE,"yyyy年MM月dd日  HH时mm分ss秒"));
				o.put("RESIGNATION_date", DateUtils.format(RESIGNATION_date,"yyyy年MM月dd日  HH时mm分ss秒"));				
				o.put("departmentName",o.get("departmentName"));
				o.put("leaderName",o.get("leaderName"));
				o.put("departName",o.get("departName"));
				o.put("positionName",o.get("position_name"));			
									
			}	
		String[] keys={"companyName", "empTypeName", "id","cnName", "firstEntryTime","Quit_time","departName","leaderName", "departmentName","positionName", "RESIGNATION_date"};
		String[] titles={"公司名称", "员工类型","员工编号","姓名","入职日期","离职日期","部门", "汇报对象", "部门负责人", "职位", "申请日期"}; 
		return ExcelUtil.exportExcel(datas,keys,titles,"离职查询.xls");
	}
	
	


	@Override
	public List<Map<String, Object>> getExportReportList(Employee quit) {
		return EmpEntryRegistrationMapper.getExportReportList(quit);
	}



	@Override
	public int insertEmpQuitInfo(HrEmpResign hrEmpResign) {
		return 0;
	}



	@Override
	@Transactional(rollbackFor=Exception.class)
	public Map<String, Object> updateEmpQuitInfo(Employee employee) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();	
			Employee emps=new Employee();
			emps.setId(employee.getId());
			List<Employee> quitPageList = employeeMapper.getQuitPageList(emps);
			if(quitPageList.size()==0){
				logger.info("离职员工 查询失败 ");
				throw new Exception("离职员工 查询失败");
			}
			Employee old = quitPageList.get(0);
			EmployeeApp employeeApp=new EmployeeApp();
			employeeApp.setCnName(old.getCnName());
			employeeApp.setQuitTime(null);//离职日期置空
			employeeApp.setFirstEntryTime(old.getFirstEntryTime());
			employeeApp.setId(employee.getId());
			employeeApp.setVersion(employee.getVersion());
			employeeApp.setBeforeWorkAge(old.getBeforeWorkAge());
			//根据条件计算年假数据
			empLeaveService.updateQuitLeaveByEmpInfo(employeeApp);
			//1、点击离职做一个方法 把入职时间 离职时间 id 算出病假的假期基数， 2、取消离职时 把基数重新算出来 根据  入职时间id 离职时间为当前时间 算出来 同上方法一样
			//根据id来计算病假数据
			empLeaveService.updateSickLeaveByEmpInfo(employeeApp);		
			//时间做一个唯一标识 数据库在进行判断  数据库 值为空
			employee.setQuitTimeStauts("1");	
			int updateById =employeeMapper.updateById(employee);
			User user=new User();
			user.setEmployeeId(employee.getId());
			user.setCompanyId(employee.getCompanyId());
			List<User> listByCondition = userService.getListByCondition(user);
			//发邮件给相关人员
			if(old!=null){
				//获取所有需要通知的员工id
				EmpPosition empPosition = new EmpPosition();
				List<EmpPosition> positionName = employeeService.sendMailPositionName();
				List<SendMail> sendMailList = new ArrayList<SendMail>();
				
				//发给默认通知的 pmo 等员工
				String pageStr = employee.getNoticeStr();
				String[] sendPmosEmail = pageStr.split(",");
				if(sendPmosEmail.length>0 && StringUtils.isNotBlank(sendPmosEmail[0])){
					for (int i = 0; i < sendPmosEmail.length; i++) {
						SendMail sendMail = sendEmpCancelQuitEmail(employee,sendPmosEmail[i]);
						sendMailList.add(sendMail);
					}
				}	
				logger.info("发送邮件给默认发送员工组"+old.getCnName()+" 取消离职通知 记录数为={}",sendPmosEmail.length+1 + "");
				//发送邮件给 人事、行政全组 、IT全组
				for(EmpPosition emp:positionName){					
					empApplicationRegisterService.sendMsg(emp.getEmployeeId(),user,"取消离职通知——"+employee.getDepartName()+" "+employee.getCnName(),employee.getDepartName()+employee.getCnName()+"员工已取消离职,请各部门知晓");
					Employee receiver = employeeMapper.getById(emp.getEmployeeId());
					if(receiver!=null){
						SendMail sendMail = sendEmpCancelQuitEmail(employee,receiver.getEmail());
						sendMailList.add(sendMail);
					}
				}
				logger.info("发送邮件给 人事、行政全组 、IT全组"+old.getCnName()+" 取消离职通知 记录数为={}",positionName.size() + "");
				
				List<Employee> pageList = employeeMapper.getPageList(employee);
				if(pageList.size()==0){
					logger.info("离职员工 查询失败 ");
					throw new Exception("离职员工 查询失败");
				}
				Employee empInfo = pageList.get(0);					
				Employee departName = employeeMapper.getById(empInfo.getDepartLeaderId());
			   
				// 发邮件给相关人员 部门负责人 直接负责人			
				empApplicationRegisterService.sendMsg(old.getReportToLeader(),user,"取消离职通知——"+employee.getDepartName()+" "+employee.getCnName(),employee.getDepartName()+employee.getCnName()+"员工已取消离职,请各部门知晓");
				//新员工逐级汇报对象到部门负责人为止
				Long reportToLeader = empInfo.getReportToLeader();//申请人汇报对象
				int count = 0;
				while(true){
					count = count + 1;
					if(reportToLeader!=null){
						//判断是否是部门负责人
						List<Depart> departList = departService.getAllDepartByLeaderId(reportToLeader);
						if(departList!=null&&departList.size()>0){
							break;
						}else{
							Employee reportToLeaderObject = employeeService.getById(reportToLeader);
							if(reportToLeaderObject!=null){
								
								SendMail sendMail = sendEmpCancelQuitEmail(employee,reportToLeaderObject.getEmail());
								
								sendMailList.add(sendMail);				
							}
							reportToLeader = reportToLeaderObject!=null?reportToLeaderObject.getReportToLeader():null;
						}
					}else{
						logger.info("请假申请:申请人汇报对象为空。");
					}
					//对象关系过多，自动跳出循环，防止死循环
					if(count>10){
						break;
					}
				}
					
				if(departName!=null&&departName.getId()!=null){
					empApplicationRegisterService.sendMsg(departName.getId(),user,"取消离职通知——"+employee.getDepartName()+" "+employee.getCnName(),employee.getDepartName()+employee.getCnName()+"员工已取消离职,请各部门知晓");
					Employee receiverDh = employeeMapper.getById(departName.getId());
					if(receiverDh!=null){
						
						SendMail sendMail = sendEmpCancelQuitEmail(employee,receiverDh.getEmail());
						
						sendMailList.add(sendMail);				
					}
				}
				logger.info("发送邮件给 直接主管，部门负责人 "+old.getCnName()+"员工取消离职通知 ");
					
				//循环发送邮件给要通知的员工
				for (int i = 0; i < employee.getIds().size(); i++) {
					empApplicationRegisterService.sendMsg(departName.getId(),user,"取消离职通知——"+employee.getDepartName()+" "+employee.getCnName(),employee.getDepartName()+employee.getCnName()+"员工已取消离职,请各部门知晓");
					Employee notiyEmp = employeeMapper.getById(employee.getIds().get(i));
					if(notiyEmp!=null){
						SendMail sendMail = sendEmpCancelQuitEmail(employee,notiyEmp.getEmail());
						
						sendMailList.add(sendMail);				
					}
				}
				logger.info("发送邮件给 选定相关通知人"+old.getCnName()+" 取消离职通知"+"共计发送 指定通知员工 "+employee.getIds().size()+" 位");
				
				//循环发送指定发送的人邮件给要通知的员工
				if(StringUtils.isNotBlank(employee.getSendEmail())){
					if(employee.getSendEmail().contains(",")){
						String[] sendemial = employee.getSendEmail().split(",");
						for (int i = 0; i < sendemial.length; i++) {
							if(sendemial!=null){
								SendMail sendMail = sendEmpCancelQuitEmail(employee,sendemial[i]);
								
								sendMailList.add(sendMail);	
							}
						}
					}else{
						
						SendMail sendMail = sendEmpCancelQuitEmail(employee,employee.getSendEmail());
						
						sendMailList.add(sendMail);	
					}
					logger.info("发送邮件给 选定相关通知人邮箱"+employee.getSendEmail()+" 取消离职通知");
				}
				
				if(sendMailList!=null&&sendMailList.size()>0){
					//list根据邮箱名去重
					Set<String> set = new  HashSet<String>(); 
			         List<SendMail> newList = new  ArrayList<SendMail>(); 
			         for (SendMail cd:sendMailList) {
			            if(set.add(cd.getReceiver())){
			                newList.add(cd);
			            }
			        }
					sendMailService.batchSave(newList);
				}	
				if(updateById>0 && sendMailList!=null){
					result.put("message", "修改成功");
					result.put("flag", true);
				}
			}
			return result;
	}
	
	public SendMail sendEmpCancelQuitEmail(Employee employee,String email) throws IOException{
		User user = userService.getCurrentUser();
			//发送邮件封装参数
			//日期转化
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			String nowDateStr = sdf.format(new Date());
			//公司
			Company company = companyMapper.getById(employee.getCompanyId());
			//员工信息
			Employee empTemplet=employeeMapper.getById(employee.getId());
			//员工类型
			EmpType empType = empTypeMapper.getById(empTemplet.getEmpTypeId());
			SendMail sendMail = new SendMail();
			sendMail.setReceiver(email);
			String isMoDepartNameAndTypeName="";
			if("外包员工".equals(empType.getTypeCName()) || "实习生".equals(empType.getTypeCName())){
				if("实习生".equals(empType.getTypeCName())){
					sendMail.setSubject("取消离职通知——"+employee.getCnName()+"("+empType.getTypeCName()+")");
				}else{
					sendMail.setSubject("取消离职通知——"+employee.getCnName()+"(外包)");
				}
				//外包 实习生 拼接在员工姓名前面
				if(StringUtils.isNotBlank(employee.getCnName())){
					if(StringUtils.isNotBlank(empType.getTypeCName())){
						isMoDepartNameAndTypeName=empType.getTypeCName()+" "+employee.getCnName();
					}else{
						isMoDepartNameAndTypeName="员工 "+employee.getCnName();
					}
				}
			}else{
				if(StringUtils.isNotBlank(employee.getCnName())){
					isMoDepartNameAndTypeName="员工 "+employee.getCnName();
				}
				sendMail.setSubject("取消离职通知——"+employee.getCnName());
			}
			String params[]={
				StringUtils.isEmpty(employee.getDepartName()) ? "" : employee.getDepartName(),
				isMoDepartNameAndTypeName,
				nowDateStr,
				user.getEmployee().getCnName(),
				user.getDepart().getName()
			};
			String templetPropertie = "empCancelQuitTemplet";
			String msg = empApplicationRegisterService.readEmailTemplet(params, templetPropertie);
	      
			
			sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
			sendMail.setText(msg);
			sendMail.setOaMail(SendMail.OA_MAIL_P);
			
			return sendMail;		
	}

	@Override
	public Map<String, Object> updateEmpQuitDate(Employee employee) {
		Map<String,Object> map = new HashMap<String,Object>();				
		try{
			int updateById =employeeMapper.updateById(employee);
			if(updateById>0){
				map.put("message", "修改成功");
				map.put("flag", true);
			}		
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "修改失败,msg="+e.getMessage());
		}
		return map;
	}



	@Override
	public List<CompanyConfig> quitSendMailEmps() {
		// TODO Auto-generated method stub
		CompanyConfig quitconfig=new CompanyConfig();
		quitconfig.setCode("quitNoticeEmail");
		return  companyConfigMapper.getListByCondition(quitconfig);
	}
}
