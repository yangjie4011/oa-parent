package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Maps;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.AttnWorkHoursMapper;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmpApplicationLeaveDetailMapper;
import com.ule.oa.base.mapper.EmpApplicationLeaveMapper;
import com.ule.oa.base.mapper.EmpLeaveMapper;
import com.ule.oa.base.mapper.LeaveRecordDetailMapper;
import com.ule.oa.base.mapper.LeaveRecordMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.po.AnnualVacation;
import com.ule.oa.base.po.AttnWorkHours;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationLeaveDetail;
import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.base.po.EmpLeave;
import com.ule.oa.base.po.EmpLeaveReport;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.LeaveDaysGBTypeResultDto;
import com.ule.oa.base.po.LeaveRecord;
import com.ule.oa.base.po.LeaveRecordDetail;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.AnnualVacationService;
import com.ule.oa.base.service.ClassSettingService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.EmpApplicationLeaveDetailService;
import com.ule.oa.base.service.EmpApplicationLeaveService;
import com.ule.oa.base.service.EmpFamilyMemberService;
import com.ule.oa.base.service.EmpLeaveReportService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmpMsgService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.base.util.AttnStatisticsUtil;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.http.IPUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @ClassName: 请假申请
 * @Description: 请假申请
 * @author yangjie
 * @date 2017年6月14日
 */
@Service
public class EmpApplicationLeaveServiceImpl implements EmpApplicationLeaveService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpApplicationLeaveMapper empApplicationLeaveMapper;
	@Autowired
	private EmpApplicationLeaveDetailMapper empApplicationLeaveDetailMapper;
	@Autowired
	private AnnualVacationService annualVacationService;
	@Autowired
	private UserService userService;
	@Autowired
	private ActivitiServiceImpl activitiServiceImpl;
	@Autowired
	private EmpMsgService empMsgService;
	@Autowired
	private EmpLeaveService empLeaveService;
	@Autowired
	private EmployeeService employeeService;
	@Resource
	private ConfigService configService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private EmpLeaveReportService empLeaveReportService;
	@Autowired
	private EmpApplicationLeaveDetailService empApplicationLeaveDetailService;
	@Autowired
	private ViewTaskInfoService viewTaskInfoService;
	@Autowired
	private SendMailService	sendMailService;
	@Autowired
	private ClassSettingService classSettingService;
	@Autowired
	private LeaveRecordMapper leaveRecordMapper;
	@Autowired
	private LeaveRecordDetailMapper leaveRecordDetailMapper;
	@Autowired
	private DepartMapper departMapper;
	@Autowired
	private EmpLeaveMapper empLeaveMapper;
	@Autowired
	private RabcUserMapper rabcUserMapper;
	@Autowired
	private AttnWorkHoursMapper attnWorkHoursMapper;
	@Autowired
	private EmpFamilyMemberService empFamilyMemberService;
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> updateById(EmpApplicationLeave leave) throws Exception{
	    Map<String,Object> map = new HashMap<String, Object>();
	    User user = userService.getCurrentUser();
	    if(leave.getApprovalStatus() != null && (leave.getApprovalStatus() != EmpApplicationLeave.APPROVAL_STATUS_YES) || (leave.getApprovalStatus().intValue() != ConfigConstants.OVERDUEPASS_STATUS)) {
	    	empApplicationLeaveMapper.updateById(leave);
	    }
	    EmpApplicationLeave old = empApplicationLeaveMapper.getById(leave.getId());
		if(leave.getApprovalStatus().intValue() == EmpApplicationLeave.APPROVAL_STATUS_YES ||leave.getApprovalStatus().intValue() == ConfigConstants.OVERDUEPASS_STATUS) {
				if(leave.getApprovalStatus() != null && (leave.getApprovalStatus().intValue() == EmpApplicationLeave.APPROVAL_STATUS_YES) || (leave.getApprovalStatus().intValue() == ConfigConstants.OVERDUEPASS_STATUS)) {
					empApplicationLeaveMapper.updateById(leave);
				}
				lockVecation(leave.getId(),true,user);
				
				//添加报表数据
				List<EmpApplicationLeaveDetail> list = empApplicationLeaveDetailMapper.getListByCondition(old.getId());
				//end
				if(null != list && !list.isEmpty()){
					List<EmpLeaveReport> empLeaveReportList = new ArrayList<EmpLeaveReport>();
					for (EmpApplicationLeaveDetail eald : list) {
						if(null != eald){
							assembleReport(old.getEmployeeId(), old.getDepartId(), old.getCnName(), eald, empLeaveReportList);
						}
					}
				}

				//充工时
				Employee employee = employeeService.getById(old.getEmployeeId());
				AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
				for(EmpApplicationLeaveDetail detail:list){
					Double leaveHours = null;
					//哺乳假
					if(detail.getLeaveType().intValue() == 4) {
						if(detail.getChildrenNum() != null) {
							leaveHours = Double.valueOf(detail.getChildrenNum());
						} else {
							leaveHours = 1d;
						}
					}
					//产前假取预产期
					if(detail.getLeaveType().intValue() == 6){
						attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, detail.getStartTime(), detail.getExpectedDate(),RunTask.RUN_CODE_60, String.valueOf(detail.getLeaveType()), leaveHours,0,old.getReason(),old.getId());
					}else if(detail.getLeaveType().intValue() == 4){
						attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, detail.getStartTime(), detail.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(detail.getLeaveType()), leaveHours,detail.getDayType(),old.getReason(),old.getId());
					}else{
						attnStatisticsUtil.calWorkHours(user.getEmployee().getCnName(),employee, detail.getStartTime(), detail.getEndTime(),RunTask.RUN_CODE_60, String.valueOf(detail.getLeaveType()), leaveHours,0,old.getReason(),old.getId());
					}
				}
		} else if(leave.getApprovalStatus().intValue() == ConfigConstants.BACK_STATUS.intValue()||leave.getApprovalStatus().intValue() == ConfigConstants.REFUSE_STATUS.intValue()||leave.getApprovalStatus().intValue() == ConfigConstants.OVERDUEREFUSE_STATUS.intValue()) {
			//审核不通过，消除假期
			lockVecation(leave.getId(),false,user);
		}
		map.put("success", true);
		return map;
	}
	
	//根据请假明细组装报表
	@Override
	public void assembleReport(Long employeeId, Long departId, String employeeName, EmpApplicationLeaveDetail detail, List<EmpLeaveReport> empLeaveReportList){
		//请假开始时间
		Date startTime = detail.getStartTime();
		//请假结束时间
		Date endTime = detail.getEndTime();
		//取出请假时间段内的所有日期
		List<Date> findDates = null;
		try {
			findDates = DateUtils.findDates(startTime, endTime);
		} catch (Exception e) {
			logger.error("拼装假期明细报表数据出错!假期[" + detail.getLeaveId() + "]开始结束时间有误!", e);
		}
		EmpLeaveReport er = null;
		if(null != findDates && findDates.size() > 0){
			Date currentDate = null;
			for(int i = 0; i < findDates.size(); i ++){
				currentDate = findDates.get(i);
				er = new EmpLeaveReport();
				er.setEmployeeId(employeeId);
				er.setDepartId(departId);
				er.setEmployeeName(employeeName);
				er.setType(detail.getLeaveType());
				if(detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_3.intValue()
						||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_9.intValue()||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_10.intValue()){
					//婚假，陪产假，丧假;（自然日）
					EmployeeClass employeeClass = new EmployeeClass();
					employeeClass.setEmployId(employeeId);
					employeeClass.setClassDate(currentDate);
					EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
					if(empClass!=null){
						String strDate = DateUtils.format(currentDate, DateUtils.FORMAT_SHORT);
						er.setMonth(strDate.substring(0, 7));
						setTimeByDay(er ,strDate.substring(8, 10), 1);
						empLeaveReportList.add(er);
					}
				}else if(detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_6.intValue()
						||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_7.intValue()||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_8.intValue()){
					//产假，产前假，流产假
					String strDate = DateUtils.format(currentDate, DateUtils.FORMAT_SHORT);
					er.setMonth(strDate.substring(0, 7));
					setTimeByDay(er ,strDate.substring(8, 10), 1);
					empLeaveReportList.add(er);
				}else if(detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_5.intValue()){
					//调休
					//判断当天是否上班，上班的话计算天数或者小时数
					EmployeeClass employeeClass = new EmployeeClass();
					employeeClass.setEmployId(employeeId);
					employeeClass.setClassDate(currentDate);
					EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
					//当天最大调休数
					double leaveHours = 0;
					//上班
					if(empClass!=null){
						boolean standard = ("09:00".equals(DateUtils.format(empClass.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(empClass.getEndTime(), "HH:mm")))?true:false;
						boolean standard1 = (empClass.getMustAttnTime()==8)?true:false;
						boolean standard2 = (empClass.getMustAttnTime()>=16)?true:false;
						boolean standard3 = (empClass.getMustAttnTime()>=10&&empClass.getMustAttnTime()<16)?true:false;
						leaveHours = empClass.getMustAttnTime();
						if(standard){
							if(i == 0){
								//第一天
								if(findDates.size()==1){
									if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass.getStartTime(), "HH")).intValue() 
											&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 240), "HH")).intValue()){
										leaveHours = 3;
									}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass.getStartTime(), "HH")).intValue()
											&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass.getEndTime(), "HH")).intValue()){
										leaveHours = 8;
									}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 180), "HH")).intValue() 
											&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass.getEndTime(), "HH")).intValue()){
										leaveHours = 5;
									}
								}else{
									if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 180), "HH")).intValue()){
										leaveHours = 5;
									}
									if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass.getStartTime(), "HH")).intValue()){
										leaveHours = 8;
									}
								}
							}else if(i == findDates.size() - 1 && i >= 1){
								//最后一天
								if(Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 240), "HH")).intValue()){
									leaveHours = 3;
								}
								if(Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass.getEndTime(), "HH")).intValue()){
									leaveHours = 8;
								}
							}
						}else if(standard1){
							if(i == 0){
								//第一天
								if(findDates.size()==1){
									if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass.getStartTime(), "HH")).intValue() 
											&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getEndTime(), -300), "HH")).intValue()){
										leaveHours = 4;
									}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass.getStartTime(), "HH")).intValue() 
											&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass.getEndTime(), "HH")).intValue()){
										leaveHours = 8;
									}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 300), "HH")).intValue() 
											&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass.getEndTime(), "HH")).intValue()){
										leaveHours = 4;
									}
								}else{
									if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getEndTime(), -300), "HH")).intValue()){
										leaveHours = 4;
									}
									if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass.getStartTime(), "HH")).intValue()){
										leaveHours = 8;
									}
								}
							}else if(i == findDates.size() - 1 && i >= 1){
								//最后一天
								if(Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 300), "HH")).intValue()){
									leaveHours = 4;
								}
								if(Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass.getEndTime(), "HH")).intValue()){
									leaveHours = 8;
								}
							}
						}else if(standard2){
							if(i == 0){
								if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass.getStartTime(), "HH")).intValue()){
									leaveHours = 16;
								}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 240), "HH")).intValue()){
									leaveHours = 12;
								}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 480), "HH")).intValue()){
									leaveHours = 8;
								}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 720), "HH")).intValue()){
									leaveHours = 4;
								}
							}
						}else if(standard3){
							if(i == 0){
								if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass.getStartTime(), "HH")).intValue()){
									leaveHours = 12;
								}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 240), "HH")).intValue()){
									leaveHours = 8;
								}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 480), "HH")).intValue()){
									leaveHours = 4;
								}
							}
						}
					}
					er.setMonth(DateUtils.getYearAndMonth(currentDate));
					setTimeByDay(er , DateUtils.getDayOfMonth(currentDate), leaveHours);
					empLeaveReportList.add(er);
				}else if(detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_1.intValue()||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_2.intValue() || detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_4.intValue()
						||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_11.intValue()||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_12.intValue()){
					//年假，病假，事假，其他
					//判断当天是否上班，上班的话计算天数或者小时数
					EmployeeClass employeeClass2 = new EmployeeClass();
					employeeClass2.setEmployId(employeeId);
					employeeClass2.setClassDate(currentDate);
					EmployeeClass empClass2 = employeeClassService.getEmployeeClassSetting(employeeClass2);
					//上班
					if(empClass2!=null){
						if(detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_4.intValue()){
							//哺乳假
							er.setMonth(DateUtils.getYearAndMonth(findDates.get(i)));
							setTimeByDay(er , DateUtils.getDayOfMonth(findDates.get(i)), detail.getChildrenNum());
						}else{
							boolean standard = ("09:00".equals(DateUtils.format(empClass2.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(empClass2.getEndTime(), "HH:mm")))?true:false;
							boolean standard1 = (empClass2.getMustAttnTime()==8)?true:false;
							boolean standard2 = (empClass2.getMustAttnTime()>=16)?true:false;
							boolean standard3 = (empClass2.getMustAttnTime()>=10&&empClass2.getMustAttnTime()<16)?true:false;
							boolean standard4 = (empClass2.getMustAttnTime()<8)?true:false;
							//当天实际最大请假小时数
							double trueLeaveDays = 0;
							if(standard){
								if(i == 0){
									//第一天
									if(findDates.size()==1){
										if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass2.getStartTime(), "HH")).intValue() 
												&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 300), "HH")).intValue()){
											trueLeaveDays = 0.5;
										}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass2.getStartTime(), "HH")).intValue() 
												&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass2.getEndTime(), "HH")).intValue()){
											trueLeaveDays = 1;
										}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 180), "HH")).intValue() 
												&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass2.getEndTime(), "HH")).intValue()){
											trueLeaveDays = 0.5;
										}
									}else{
										if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 180), "HH")).intValue()){
											trueLeaveDays = 0.5;
										}
										if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass2.getStartTime(), "HH")).intValue()){
											trueLeaveDays = 1;
										}
									}
								}else if(i == findDates.size() - 1 && i >= 1){
									//最后一天
									if(Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 240), "HH")).intValue()){
										trueLeaveDays = 0.5;
									}
									if(Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass2.getEndTime(), "HH")).intValue()){
										trueLeaveDays = 1;
									}
								}else{
									trueLeaveDays = 1;
								}
							}else if(standard1){
								if(i == 0){
									//第一天
									if(findDates.size()==1){
										if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass2.getStartTime(), "HH")).intValue() 
												&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getEndTime(), -240), "HH")).intValue()){
											trueLeaveDays = 0.5;
										}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass2.getStartTime(), "HH")).intValue() 
												&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass2.getEndTime(), "HH")).intValue()){
											trueLeaveDays = 1;
										}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 180), "HH")).intValue() 
												&& Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass2.getEndTime(), "HH")).intValue()){
											trueLeaveDays = 0.5;
										}
									}else{
										if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getEndTime(), -240), "HH")).intValue()){
											trueLeaveDays = 0.5;
										}
										if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass2.getStartTime(), "HH")).intValue()){
											trueLeaveDays = 1;
										}
									}
								}else if(i == findDates.size() - 1 && i >= 1){
									//最后一天
									if(Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 300), "HH")).intValue()){
										trueLeaveDays = 0.5;
									}
									if(Integer.parseInt(DateUtils.getHours(endTime)) == Integer.valueOf(DateUtils.format(empClass2.getEndTime(), "HH")).intValue()){
										trueLeaveDays = 1;
									}
								}else{
									trueLeaveDays = 1;
								}
							}else if(standard2){
								if(i == 0){
									if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass2.getStartTime(), "HH")).intValue()){
										trueLeaveDays = 2;
									}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 240), "HH")).intValue()){
										trueLeaveDays = 1.5;
									}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 480), "HH")).intValue()){
										trueLeaveDays = 1;
									}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 720), "HH")).intValue()){
										trueLeaveDays = 0.5;
									}
								}else{
									trueLeaveDays = 2;
								}
							}else if(standard3){
								if(i == 0){
									if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(empClass2.getStartTime(), "HH")).intValue()){
										trueLeaveDays = 1.5;
									}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 240), "HH")).intValue()){
										trueLeaveDays = 1;
									}else if(Integer.parseInt(DateUtils.getHours(startTime)) == Integer.valueOf(DateUtils.format(DateUtils.addMinute(empClass2.getStartTime(), 480), "HH")).intValue()){
										trueLeaveDays = 0.5;
									}
								}else{
									trueLeaveDays = 1.5;
								}
							}else if(standard4){
								trueLeaveDays = 1;
							}
							er.setMonth(DateUtils.getYearAndMonth(currentDate));
							setTimeByDay(er, DateUtils.getDayOfMonth(currentDate), trueLeaveDays);
						}
						empLeaveReportList.add(er);
					}
				}
			}
		}
		
		//将相同id,月份,假期类型的数据合并起来
		Map<String, EmpLeaveReport> temMap = new HashMap<String, EmpLeaveReport>();
		if(null != empLeaveReportList && !empLeaveReportList.isEmpty()){
			String key = "";
			for (EmpLeaveReport empLeaveReport : empLeaveReportList) {
				if(null != empLeaveReport){
					key = empLeaveReport.getEmployeeId() + "_" + empLeaveReport.getMonth() + "_" + empLeaveReport.getType() + "_" + empLeaveReport.getDepartId();
					if(temMap.containsKey(key)){
						mergeLeaveReportData(temMap.get(key), empLeaveReport);
					}else{
						temMap.put(key, empLeaveReport);
					}
				}
			}
		}
		
		List<EmpLeaveReport> batchSaveList = new ArrayList<EmpLeaveReport>();
		List<EmpLeaveReport> batchUpdateList = new ArrayList<EmpLeaveReport>();
		List<String> batchSaveKeyList = new ArrayList<String>();
		List<String> batchUpdateKeyList = new ArrayList<String>();
		
		//从数据库中查询相同employeeId,type,部门,月份的数据;若数据库中存在,则合并后更新,否则插入数据
		Set<String> keySet = temMap.keySet();
		String[] keyArr = null;
		EmpLeaveReport model = new EmpLeaveReport();
		List<EmpLeaveReport> elrList= null;
		if(null != keySet && !keySet.isEmpty()){
			String temKey = "";
			for (String key : keySet) {
				keyArr = key.split("_");
				model.setEmployeeId(Long.parseLong(keyArr[0]));
				model.setMonth(keyArr[1]);
				model.setDepartId(Long.parseLong(keyArr[3]));
				model.setType(Integer.parseInt(keyArr[2]));
				elrList = empLeaveReportService.getListByCondition(model);
				if(null == elrList || elrList.isEmpty()){
					//报表中没有数据,则为新增数据
					batchSaveList.add(temMap.get(key));
					batchSaveKeyList.add(key);
				}else{
					for (EmpLeaveReport elr : elrList) {
						temKey = elr.getEmployeeId() + "_" + elr.getMonth() + "_" + elr.getType() + "_" + elr.getDepartId();
						if(key.equals(temKey)){
							mergeLeaveReportData(temMap.get(key), elr);
							batchUpdateKeyList.add(key);
							batchUpdateList.add(temMap.get(key));
						}
					}
				}
			}
			
			if(null != batchUpdateKeyList && !batchUpdateKeyList.isEmpty() && (batchUpdateKeyList.size() + batchSaveKeyList.size()) < keySet.size()){
				for (String key : keySet) {
					if(!batchUpdateKeyList.contains(key) && !batchSaveKeyList.contains(key)){
						batchSaveList.add(temMap.get(key));
					}
				}
			}
		}
		if(null != batchSaveList && !batchSaveList.isEmpty()){
			//批量插入数据
			empLeaveReportService.asyncBatchSave(batchSaveList);
		}
		if(null != batchUpdateList && !batchUpdateList.isEmpty()){
			//批量更新数据
			empLeaveReportService.batchUpdate(batchUpdateList);
		}
	}
		
	/**
	 * 将假期报表数据合并到目标
	 * @param sourceEmpLeaveReport 目标数据
	 * @param empLeaveReport2
	 */
	private void mergeLeaveReportData(EmpLeaveReport sourceEmpLeaveReport,
			EmpLeaveReport empLeaveReport) {
		if(null != empLeaveReport.getId()){
			sourceEmpLeaveReport.setId(empLeaveReport.getId());
		}
		sourceEmpLeaveReport.setDate1(empLeaveReport.getDate1() + sourceEmpLeaveReport.getDate1());
		sourceEmpLeaveReport.setDate2(empLeaveReport.getDate2() + sourceEmpLeaveReport.getDate2());
		sourceEmpLeaveReport.setDate3(empLeaveReport.getDate3() + sourceEmpLeaveReport.getDate3());
		sourceEmpLeaveReport.setDate4(empLeaveReport.getDate4() + sourceEmpLeaveReport.getDate4());
		sourceEmpLeaveReport.setDate5(empLeaveReport.getDate5() + sourceEmpLeaveReport.getDate5());
		sourceEmpLeaveReport.setDate6(empLeaveReport.getDate6() + sourceEmpLeaveReport.getDate6());
		sourceEmpLeaveReport.setDate7(empLeaveReport.getDate7() + sourceEmpLeaveReport.getDate7());
		sourceEmpLeaveReport.setDate8(empLeaveReport.getDate8() + sourceEmpLeaveReport.getDate8());
		sourceEmpLeaveReport.setDate9(empLeaveReport.getDate9() + sourceEmpLeaveReport.getDate9());
		sourceEmpLeaveReport.setDate10(empLeaveReport.getDate10() + sourceEmpLeaveReport.getDate10());
		sourceEmpLeaveReport.setDate11(empLeaveReport.getDate11() + sourceEmpLeaveReport.getDate11());
		sourceEmpLeaveReport.setDate12(empLeaveReport.getDate12() + sourceEmpLeaveReport.getDate12());
		sourceEmpLeaveReport.setDate13(empLeaveReport.getDate13() + sourceEmpLeaveReport.getDate13());
		sourceEmpLeaveReport.setDate14(empLeaveReport.getDate14() + sourceEmpLeaveReport.getDate14());
		sourceEmpLeaveReport.setDate15(empLeaveReport.getDate15() + sourceEmpLeaveReport.getDate15());
		sourceEmpLeaveReport.setDate16(empLeaveReport.getDate16() + sourceEmpLeaveReport.getDate16());
		sourceEmpLeaveReport.setDate17(empLeaveReport.getDate17() + sourceEmpLeaveReport.getDate17());
		sourceEmpLeaveReport.setDate18(empLeaveReport.getDate18() + sourceEmpLeaveReport.getDate18());
		sourceEmpLeaveReport.setDate19(empLeaveReport.getDate19() + sourceEmpLeaveReport.getDate19());
		sourceEmpLeaveReport.setDate20(empLeaveReport.getDate20() + sourceEmpLeaveReport.getDate20());
		sourceEmpLeaveReport.setDate21(empLeaveReport.getDate21() + sourceEmpLeaveReport.getDate21());
		sourceEmpLeaveReport.setDate22(empLeaveReport.getDate22() + sourceEmpLeaveReport.getDate22());
		sourceEmpLeaveReport.setDate23(empLeaveReport.getDate23() + sourceEmpLeaveReport.getDate23());
		sourceEmpLeaveReport.setDate24(empLeaveReport.getDate24() + sourceEmpLeaveReport.getDate24());
		sourceEmpLeaveReport.setDate25(empLeaveReport.getDate25() + sourceEmpLeaveReport.getDate25());
		sourceEmpLeaveReport.setDate26(empLeaveReport.getDate26() + sourceEmpLeaveReport.getDate26());
		sourceEmpLeaveReport.setDate27(empLeaveReport.getDate27() + sourceEmpLeaveReport.getDate27());
		sourceEmpLeaveReport.setDate28(empLeaveReport.getDate28() + sourceEmpLeaveReport.getDate28());
		sourceEmpLeaveReport.setDate29(empLeaveReport.getDate29() + sourceEmpLeaveReport.getDate29());
		sourceEmpLeaveReport.setDate30(empLeaveReport.getDate30() + sourceEmpLeaveReport.getDate30());
		sourceEmpLeaveReport.setDate31(empLeaveReport.getDate31() + sourceEmpLeaveReport.getDate31());
	}

	@SuppressWarnings("unused")
	private boolean isHourBefore(Date startWorkTime, Date endWorkTime) {
		startWorkTime = DateUtils.parse(DateUtils.format(startWorkTime, DateUtils.FORMAT_HH_MM_SS), DateUtils.FORMAT_HH_MM_SS);
		endWorkTime = DateUtils.parse( DateUtils.format(endWorkTime, DateUtils.FORMAT_HH_MM_SS), DateUtils.FORMAT_HH_MM_SS);
		return DateUtils.isBefore(startWorkTime, endWorkTime);
	}

	/**
	 * 根据当前日期将请假天数设置到假期报表数据中
	 * @param day 当前日期
	 */
	private void setTimeByDay(EmpLeaveReport er, String strDay, double hoursOrDays) {
		int day = Integer.parseInt(strDay);
		switch (day) {
		case 1:
			er.setDate1(hoursOrDays);
			break;
		case 2:
			er.setDate2(hoursOrDays);
			break;
		case 3:
			er.setDate3(hoursOrDays);
			break;
		case 4:
			er.setDate4(hoursOrDays);
			break;
		case 5:
			er.setDate5(hoursOrDays);
			break;
		case 6:
			er.setDate6(hoursOrDays);
			break;
		case 7:
			er.setDate7(hoursOrDays);
			break;
		case 8:
			er.setDate8(hoursOrDays);
			break;
		case 9:
			er.setDate9(hoursOrDays);
			break;
		case 10:
			er.setDate10(hoursOrDays);
			break;
		case 11:
			er.setDate11(hoursOrDays);
			break;
		case 12:
			er.setDate12(hoursOrDays);
			break;
		case 13:
			er.setDate13(hoursOrDays);
			break;
		case 14:
			er.setDate14(hoursOrDays);
			break;
		case 15:
			er.setDate15(hoursOrDays);
			break;
		case 16:
			er.setDate16(hoursOrDays);
			break;
		case 17:
			er.setDate17(hoursOrDays);
			break;
		case 18:
			er.setDate18(hoursOrDays);
			break;
		case 19:
			er.setDate19(hoursOrDays);
			break;
		case 20:
			er.setDate20(hoursOrDays);
			break;
		case 21:
			er.setDate21(hoursOrDays);
			break;
		case 22:
			er.setDate22(hoursOrDays);
			break;
		case 23:
			er.setDate23(hoursOrDays);
			break;
		case 24:
			er.setDate24(hoursOrDays);
			break;
		case 25:
			er.setDate25(hoursOrDays);
			break;
		case 26:
			er.setDate26(hoursOrDays);
			break;
		case 27:
			er.setDate27(hoursOrDays);
			break;
		case 28:
			er.setDate28(hoursOrDays);
			break;
		case 29:
			er.setDate29(hoursOrDays);
			break;
		case 30:
			er.setDate30(hoursOrDays);
			break;
		case 31:
			er.setDate31(hoursOrDays);
			break;
		default:
			break;
		}
	}

	@Override
	public EmpApplicationLeave getById(Long id) {
		return empApplicationLeaveMapper.getById(id);
	}

	@Override
	public List<EmpApplicationLeaveDetail> getLeaveDetailList(Long leaveId) {
		return empApplicationLeaveDetailMapper.getListByCondition(leaveId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String,Object> save(HttpServletRequest request, User user) throws Exception{
	   
		logger.info("请假申请单save:start。。");
		Long time1 = System.currentTimeMillis();
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		String list = request.getParameter("businessDetailList");
		String mobile = request.getParameter("mobile");
		String agentId = request.getParameter("agentId");
		String agentMobile = request.getParameter("agentMobile");
		String reason = request.getParameter("reason");
		String toPersions = request.getParameter("toPersions");
		String toEmails = request.getParameter("toEmails");
		
		logger.info("请假申请单save入参:list="+list+";mobile="+mobile+";agentId="+agentId+";agentMobile="+agentMobile
				+";reason="+reason+";toPersions="+toPersions+";toEmails="+toEmails
			+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		if(StringUtils.isBlank(mobile)){
			throw new OaException("紧急联系电话不能为空！");
		}
		String pattern1 = "^1[3456789]\\d{9}$";
		Pattern r1 = Pattern.compile(pattern1);
		Matcher m1 = r1.matcher(mobile);
		if(!m1.matches()){
			throw new OaException("手机号格式不正确！");
		}
		
		if(StringUtils.isBlank(reason)){
			throw new OaException("请假事由不能为空！");
		}
		
		EmpApplicationLeave leave = new EmpApplicationLeave();
		leave.setEmployeeId(user.getEmployeeId());
		leave.setCnName(user.getEmployee().getCnName());
		leave.setCode(user.getEmployee().getCode());
		leave.setDepartId(user.getDepart().getId());
		leave.setDepartName(user.getDepart().getName());
		leave.setPositionId(user.getPosition().getId());
		leave.setPositionName(user.getPosition().getPositionName());
		leave.setReason(reason);
		leave.setMobile(mobile);
		leave.setToPersions(toPersions);
		leave.setToEmails(toEmails);
		String agentEmail = "";//假期代理人邮箱
		if(StringUtils.isNotBlank(agentId)){
			if(agentId.equals(String.valueOf(user.getEmployeeId()))){
				throw new OaException("假期代理人不能为是本人！");
			}
			leave.setAgentId(Long.valueOf(agentId));
			Employee agent = employeeService.getById(Long.valueOf(agentId));
			if(agent!=null){
				leave.setAgent(agent.getCnName());
				agentEmail = agent.getEmail();
			}else{
				throw new OaException("假期代理人不能为空！");
			}
		}else{
			throw new OaException("假期代理人不能为空！");
		}
		leave.setAgentMobile(agentMobile);
		leave.setApprovalStatus(EmpApplicationLeave.APPROVAL_STATUS_WAIT);
		leave.setDelFlag(CommonPo.STATUS_NORMAL);
		leave.setVersion(null);
		leave.setCreateTime(new Date());
		leave.setCreateUser(user.getEmployee().getCnName());
		leave.setSubmitDate(new Date());
		leave.setApprovalStatus(EmpApplicationLeave.APPROVAL_STATUS_WAIT);
		leave.setLeaveTypeFlag(2);
		List<EmpApplicationLeaveDetail> detailList = new ArrayList<EmpApplicationLeaveDetail>();
		JSONArray array = JSONArray.fromObject(list);
		//最小开始时间
		Date minStartTime = null;
		//最大结束时间
		Date maxEndTime = null;
		//请假总时间
		Double duration = 0.0;
		//年假是否能透支
		boolean overDrawFlag = configService.checkYearLeaveCanOverDraw();
		
		//最晚发起时间
		Date latestLaunchTime = null;
		Date now = new Date();
		
		for(int i=0;i<array.size();i++){
			EmpApplicationLeaveDetail detail = new EmpApplicationLeaveDetail();
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			
			if(!jsonObject.containsKey("leaveType")){
				throw new OaException("假期类型不能为空！");
			}
			
			//入职未满3个月，只能请病假、调休、事假、其他！
			int firstEntryTimeNext3Month = Integer.valueOf(DateUtils.format(DateUtils.addMonth(user.getEmployee().getFirstEntryTime(), 3), DateUtils.FORMAT_SIMPLE)).intValue();
			int leaveStartTime = Integer.valueOf(DateUtils.format(DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG),DateUtils.FORMAT_SIMPLE)).intValue();
			if(jsonObject.getInt("leaveType")!=2&&jsonObject.getInt("leaveType")!=5&&jsonObject.getInt("leaveType")!=11&&jsonObject.getInt("leaveType")!=12){
				if(leaveStartTime<firstEntryTimeNext3Month){
					throw new OaException("入职未满3个月，只能请病假、调休、事假、其他！");
				}
			}
			
			//有剩余年假请申请年假（请事假的时候）
			if(jsonObject.getInt("leaveType")==11){
			    //事假最晚发起时间：请假前一天24点前
			    latestLaunchTime = DateUtils.parse(jsonObject.getString("startTime").substring(0, 10)+" 00:00:00",DateUtils.FORMAT_LONG);
			    if(now.getTime()>latestLaunchTime.getTime()) {
                    throw new OaException("事假最晚发起时间：请假前一天24点前！");
                }
			    if(overDrawFlag) {
					EmpLeave record = new EmpLeave();
					record.setEmployeeId(user.getEmployee().getId());
					record.setType(ConfigConstants.LEAVE_TYPE_1);
					double reaminDays = 0;
					int year = Integer.valueOf(DateUtils.format(new Date(),"yyyy"));
					if(leaveStartTime>=firstEntryTimeNext3Month){
						//判断请的是否是上一年的年假
						if(Integer.valueOf(DateUtils.format(DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG),"yyyy")).intValue()==Integer.valueOf(DateUtils.format(DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG),"yyyy")).intValue()&&
								Integer.valueOf(DateUtils.format(DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG),"yyyy")).intValue()==year-1){
							    record.setYear(year-1);
				                List<EmpLeave> listCurrent = empLeaveService.getReaminLeaveList(record);
								for(EmpLeave data:listCurrent){
									reaminDays = reaminDays + data.getAllowRemainDays();
								}
						}else{
							    record.setYear(year);
				                List<EmpLeave> listCurrent = empLeaveService.getReaminLeaveList(record);
								for(EmpLeave data:listCurrent){
									reaminDays = reaminDays + data.getAllowRemainDays();
								}
								//最好是根据4-1区分假期有效期
								if(Integer.valueOf(DateUtils.format(new Date(),DateUtils.FORMAT_SIMPLE)).intValue()<
										Integer.valueOf(String.valueOf(year)+"0401").intValue()){
									record.setYear(year-1);
									List<EmpLeave> listLast = empLeaveService.getReaminLeaveList(record);
									for(EmpLeave data:listLast){
										reaminDays = reaminDays + data.getAllowRemainDays();
									}
								}
						}
					}
					if(reaminDays>0){
						throw new OaException("有剩余年假请先申请年假！");
					}
				}else {
					if(leaveStartTime>=firstEntryTimeNext3Month) {
						Double actualDays = empLeaveService.getActualYearDays(DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG), user.getEmployee().getId());
						if(actualDays>0){
							throw new OaException("有剩余年假请先申请年假！");
						}
					}
				}
			}
			
			if(!jsonObject.containsKey("startTime")){
				throw new OaException("开始日期不能为空！");
			}
			//次月5个工作日前可以申请上个月及以后的请假，次月5个工作日后可以申请本月及以后的请假
			Config configCondition = new Config();
			configCondition.setCode("timeLimit5");
			List<Config> limit = configService.getListByCondition(configCondition);
			int num = Integer.valueOf(limit.get(0).getDisplayCode());
			String nowDate10 = DateUtils.format(annualVacationService.get5WorkingDayNextmonth(num), DateUtils.FORMAT_SHORT);
			int nowCountDays = DateUtils.getIntervalDays(DateUtils.parse(nowDate10, DateUtils.FORMAT_SHORT),DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT),DateUtils.FORMAT_SHORT));
			if(nowCountDays > 0) {
				String nowDate01 = DateUtils.getNow("yyyy-MM") + "-01";
				int nowCountDays01 = DateUtils.getIntervalDays(DateUtils.parse(nowDate01, DateUtils.FORMAT_SHORT),DateUtils.parse(jsonObject.getString("startTime").substring(0, 10),DateUtils.FORMAT_SHORT));
			
				if(jsonObject.getInt("leaveType")!=ConfigConstants.LEAVE_TYPE_7.intValue()){  //产假无开始时间限制 产前假 先占时弄个条件		
					if(nowCountDays01 < 0) {
						throw new OaException("本月已经超过3个工作日，只能申请本月假期！");
					}
				}
			} else {
				String nowDate01 = DateUtils.getNow("yyyy-MM") + "-01";
				Date addDate = DateUtils.addMonth(DateUtils.parse(nowDate01, DateUtils.FORMAT_SHORT), -1);
				int nowCountDays01 = DateUtils.getIntervalDays(addDate,DateUtils.parse(jsonObject.getString("startTime").substring(0, 10),DateUtils.FORMAT_SHORT));
				if(nowCountDays01 < 0) {
					throw new OaException("只能申请上月及本月假期！");
				}
			}
			if(jsonObject.getInt("leaveType")==6){
				if(!jsonObject.containsKey("expectedDate")){
					throw new OaException("预产期不能为空！");
				}
			}else{
				if(!jsonObject.containsKey("endTime")){
					throw new OaException("结束日期不能为空！");
				}
			}
			if(!jsonObject.containsKey("leaveDays")){
				throw new OaException("请假天数不能为空！");
			}
			if(jsonObject.getDouble("leaveDays")==0){
				throw new OaException("请假天数不能为0！");
			}
			if(jsonObject.getInt("leaveType")==4){
				//哺乳假
			    //哺乳最晚发起时间：需要享受的第一个工作日16点前
                latestLaunchTime = DateUtils.parse(jsonObject.getString("startTime").substring(0, 10)+" 16:00:00",DateUtils.FORMAT_LONG);
                if(now.getTime()>latestLaunchTime.getTime()) {
                    throw new OaException("哺乳假最晚发起时间：需要享受的第一个工作日16点前！");
                }
				if(array.size()>1){
					throw new OaException("哺乳假只能单独请！");
				}
				if(!jsonObject.containsKey("childrenNum")){
					throw new OaException("子女数不能为空！");
				}
				if(!jsonObject.containsKey("dayType")){
					throw new OaException("请假时间不能为空！");
				}
				if(jsonObject.getInt("childrenNum")==0){
					throw new OaException("子女数不能为0！");
				}
				detail.setLeaveType(jsonObject.getInt("leaveType"));
				detail.setStartTime(DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG));
				detail.setEndTime(DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG));
				detail.setLeaveDays(jsonObject.getDouble("leaveDays"));
				detail.setChildrenNum(jsonObject.getInt("childrenNum"));
				detail.setDayType(jsonObject.getInt("dayType"));
				detail.setLeaveHours(jsonObject.getInt("childrenNum")*jsonObject.getDouble("leaveDays"));
				detail.setDelFlag(CommonPo.STATUS_NORMAL);
				detail.setVersion(null);
				detail.setCreateTime(new Date());
				detail.setCreateUser(user.getEmployee().getCnName());
				detailList.add(detail);
				duration+=jsonObject.getDouble("leaveDays");
			}else if(jsonObject.getInt("leaveType")==7){
				//产假
				if(array.size()>1){
					throw new OaException("产假只能单独请！");
				}
				if(!jsonObject.containsKey("childrenNum")){
					throw new OaException("子女数量不能为空！");
				}
				if(!jsonObject.containsKey("birthType")){
					throw new OaException("生产情况不能为空！");
				}
				if(jsonObject.getInt("childrenNum")==0){
					throw new OaException("子女数量不能为0！");
				}
				leave.setLeaveTypeFlag(0);
				detail.setLeaveType(jsonObject.getInt("leaveType"));
				detail.setStartTime(DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG));
				detail.setEndTime(DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG));
				detail.setLeaveDays(jsonObject.getDouble("leaveDays"));
				//有拼接的产假 存放到LeaveHours中
				if(jsonObject.getDouble("leavesNote")!=0){
					detail.setLeaveHours(jsonObject.getDouble("leavesNote"));
				}
				detail.setChildrenNum(jsonObject.getInt("childrenNum"));
				detail.setBirthType(jsonObject.getInt("birthType"));
				detail.setDelFlag(CommonPo.STATUS_NORMAL);
				detail.setVersion(null);
				detail.setCreateTime(new Date());
				detail.setCreateUser(user.getEmployee().getCnName());
				detailList.add(detail);
				duration+=jsonObject.getDouble("leaveDays");
			}else if(jsonObject.getInt("leaveType")==5){
				//调休
			    //调休最晚发起时间：请假前一天24点前
                latestLaunchTime = DateUtils.parse(jsonObject.getString("startTime").substring(0, 10)+" 00:00:00",DateUtils.FORMAT_LONG);
                if(now.getTime()>latestLaunchTime.getTime()) {
                    throw new OaException("调休最晚发起时间：请假前一天24点前！");
                }
				if(!jsonObject.containsKey("leaveHours")){
					throw new OaException("调休小时数不能为空！");
				}
				if(jsonObject.getDouble("leaveHours")==0){
					throw new OaException("调休小时数不能为0！");
				}
				detail.setLeaveType(jsonObject.getInt("leaveType"));
				detail.setStartTime(DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG));
				detail.setEndTime(DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG));
				detail.setLeaveDays(jsonObject.getDouble("leaveDays"));
				detail.setLeaveHours(jsonObject.getDouble("leaveHours"));
				detail.setDelFlag(CommonPo.STATUS_NORMAL);
				detail.setVersion(null);
				detail.setCreateTime(new Date());
				detail.setCreateUser(user.getEmployee().getCnName());
				detailList.add(detail);
				duration+=jsonObject.getDouble("leaveDays");
			}else if(jsonObject.getInt("leaveType")==10){
			    //丧假最晚发起时间：请假前一天24点前
			    latestLaunchTime = DateUtils.parse(jsonObject.getString("startTime").substring(0, 10)+" 00:00:00",DateUtils.FORMAT_LONG);
                if(now.getTime()>latestLaunchTime.getTime()) {
                    throw new OaException("丧假最晚发起时间：请假前一天24点前！");
                }
				//丧假
				if(array.size()>1){
					throw new OaException("丧假只能单独请！");
				}
				if(jsonObject.getInt("leaveType")==10){
					if(jsonObject.getDouble("leaveDays")>3){
						throw new OaException("丧假最多只能请3天！");
					}
				}
				detail.setLeaveType(jsonObject.getInt("leaveType"));
				detail.setStartTime(DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG));
				detail.setEndTime(DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG));
				detail.setLeaveDays(jsonObject.getDouble("leaveDays"));
				detail.setDelFlag(CommonPo.STATUS_NORMAL);
				detail.setVersion(null);
				detail.setCreateTime(new Date());
				detail.setCreateUser(user.getEmployee().getCnName());
				detail.setRelatives(jsonObject.getInt("relatives"));
				detailList.add(detail);
				duration+=jsonObject.getDouble("leaveDays");
			}else if(jsonObject.getInt("leaveType")==6){
				//产前假
				if(array.size()>1){
					throw new OaException("产前假只能单独请！");
				}
				leave.setLeaveTypeFlag(1);
				detail.setLeaveType(jsonObject.getInt("leaveType"));
				detail.setStartTime(DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG));
				detail.setExpectedDate(DateUtils.parse(jsonObject.getString("expectedDate"),DateUtils.FORMAT_LONG));
				detail.setLeaveDays(jsonObject.getDouble("leaveDays"));
				detail.setDelFlag(CommonPo.STATUS_NORMAL);
				detail.setVersion(null);
				detail.setCreateTime(new Date());
				detail.setCreateUser(user.getEmployee().getCnName());
				detailList.add(detail);
				duration+=jsonObject.getDouble("leaveDays");
			}else{
			    if(jsonObject.getInt("leaveType")==2){
			        //病假最晚发起时间：请假当天10点前
			        latestLaunchTime = DateUtils.parse(jsonObject.getString("startTime").substring(0, 10)+" 10:00:00",DateUtils.FORMAT_LONG);
			        if(now.getTime()>latestLaunchTime.getTime()) {
			            throw new OaException("病假最晚发起时间：请假当天10点前！");
			        }
                }
				if(jsonObject.getInt("leaveType")==1){
				    //年假最晚发起时间：请假前一天24点前
	                latestLaunchTime = DateUtils.parse(jsonObject.getString("startTime").substring(0, 10)+" 00:00:00",DateUtils.FORMAT_LONG);
	                if(now.getTime()>latestLaunchTime.getTime()) {
	                    throw new OaException("年假最晚发起时间：请假前一天24点前！");
	                }
					//2020年假暂时不允许透支
					if(!overDrawFlag) {
						Double actualDays = empLeaveService.getActualYearDays(DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG), user.getEmployee().getId());
					    if(actualDays<jsonObject.getDouble("leaveDays")){
					    	throw new OaException("申请假期天数已超过请假当天可用天数！");
					    }
					}
				}
				if(jsonObject.getInt("leaveType")==8){
				    //流产假最晚发起时间：请假当天10点前
				    latestLaunchTime = DateUtils.parse(jsonObject.getString("startTime").substring(0, 10)+" 10:00:00",DateUtils.FORMAT_LONG);
                    if(now.getTime()>latestLaunchTime.getTime()) {
                        throw new OaException("流产假最晚发起时间：请假当天10点前！");
                    }
					if(array.size()>1){
						throw new OaException("流产假只能单独请！");
					}
				}
				if(jsonObject.getInt("leaveType")==3){
				    //婚假最晚发起时间：请假前一天24点前
				    latestLaunchTime = DateUtils.parse(jsonObject.getString("startTime").substring(0, 10)+" 00:00:00",DateUtils.FORMAT_LONG);
                    if(now.getTime()>latestLaunchTime.getTime()) {
                        throw new OaException("婚假最晚发起时间：请假前一天24点前！");
                    }
					if(jsonObject.getDouble("leaveDays")>10){
						throw new OaException("婚假最多只能请10天！");
					}
					if(array.size()>1){
						throw new OaException("婚假只能单独请！");
					}
				}
				if(jsonObject.getInt("leaveType")==9){
				    //陪产假最晚发起时间：请假前一天24点前
	                latestLaunchTime = DateUtils.parse(jsonObject.getString("startTime").substring(0, 10)+" 00:00:00",DateUtils.FORMAT_LONG);
	                if(now.getTime()>latestLaunchTime.getTime()) {
	                    throw new OaException("陪产假最晚发起时间：请假前一天24点前！");
	                }
					if(jsonObject.getDouble("leaveDays")>10){
						throw new OaException("陪产假最多只能请10天！");
					}
				}
				if(jsonObject.getInt("leaveType")==12) {
				    //其他假最晚发起时间：请假前一天24点前
                    latestLaunchTime = DateUtils.parse(jsonObject.getString("startTime").substring(0, 10)+" 00:00:00",DateUtils.FORMAT_LONG);
                    if(now.getTime()>latestLaunchTime.getTime()) {
                        throw new OaException("其他假最晚发起时间：请假前一天24点前！");
                    }
				}
				detail.setLeaveType(jsonObject.getInt("leaveType"));
				detail.setStartTime(DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG));
				detail.setEndTime(DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG));
				detail.setLeaveDays(jsonObject.getDouble("leaveDays"));
				detail.setDelFlag(CommonPo.STATUS_NORMAL);
				detail.setVersion(null);
				detail.setCreateTime(now);
				detail.setCreateUser(user.getEmployee().getCnName());
				detailList.add(detail);
				duration+=jsonObject.getDouble("leaveDays");
			}
			//获取minStartTime和maxEndTime
			if(i==0){
				minStartTime = DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG);
				if(JSONObject.fromObject(array.get(i), new JsonConfig()).getInt("leaveType")==6){
					maxEndTime = DateUtils.parse(jsonObject.getString("expectedDate"),DateUtils.FORMAT_LONG);
				}else{
					maxEndTime = DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG);
				}
			}else{
				if(DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG).getTime()<minStartTime.getTime()){
					minStartTime = DateUtils.parse(jsonObject.getString("startTime"),DateUtils.FORMAT_LONG);
				}
				if(DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG).getTime()>maxEndTime.getTime()){
					maxEndTime = DateUtils.parse(jsonObject.getString("endTime"),DateUtils.FORMAT_LONG);
				}
			}
			if(jsonObject.getInt("leaveType")==7 && jsonObject.getDouble("leavesNote")!=0){				
				leave.setDuration(duration+jsonObject.getDouble("leavesNote"));
			}else{
				leave.setDuration(duration);
			}
			leave.setStartTime(minStartTime);
			leave.setEndTime(maxEndTime);
			EmpApplicationLeaveDetail leaveDetail = new EmpApplicationLeaveDetail();
			leaveDetail.setEmployeeId(user.getEmployeeId());
			leaveDetail.setStartTime(minStartTime);
			leaveDetail.setEndTime(maxEndTime);
		}
		
		String processInstanceId = activitiServiceImpl.startByKey("leave");
		//流程与业务数据关联
		leave.setProcessInstanceId(processInstanceId);
		empApplicationLeaveMapper.save(leave);
		if(leave.getId()!=null){
			for(EmpApplicationLeaveDetail detail:detailList){
				detail.setLeaveId(leave.getId());
			}
			empApplicationLeaveDetailMapper.batchSave(detailList);
		}

		//-----------------start-----------------------保存流程节点信息
		ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
		flow.setAssigneeName(leave.getCnName());
		flow.setDepartName(leave.getDepartName());
		flow.setPositionName(leave.getPositionName());
		flow.setFinishTime(leave.getSubmitDate());
		flow.setComment(leave.getReason());
		flow.setProcessId(processInstanceId);
		flow.setProcessKey(ConfigConstants.LEAVE_KEY);
		flow.setStatu(0);
		viewTaskInfoService.save(flow);
		//调用接口使用,锁定假期
		List<Map<String, Object>> planLeaves = new ArrayList<Map<String, Object>>();
		for(EmpApplicationLeaveDetail detail:detailList){
			if(detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_1.intValue()
					||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_5.intValue()){
				Map<String, Object> planLeave = new HashMap<String,Object>();
				planLeave.put("companyId", user.getEmployee().getCompanyId());
				planLeave.put("employeeId", user.getEmployee().getId());
				planLeave.put("type", detail.getLeaveType());
				planLeave.put("planDays", detail.getLeaveDays());
				planLeave.put("planStartTime",detail.getStartTime());
				planLeave.put("planEndTime",detail.getEndTime());
				planLeave.put("optUser", user.getEmployee().getCnName());
				planLeave.put("empapplicationleaveId", leave.getId());
				if(detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_5.intValue()){
					planLeave.put("planDays",detail.getLeaveHours());
				}
				planLeaves.add(planLeave);
			}
		}
		
		boolean flag = true;
		if(planLeaves!=null&&planLeaves.size()>0){
			logger.info("锁定假期接口入参：="+planLeaves.toString());
			flag = empLeaveService.updateEmpLeaveApply(planLeaves);		
			if(!flag){
				throw new OaException("假期扣减存在问题!");
			}
		}
		
		//将天数条件加到流程中
		Map<String,Object> v = Maps.newHashMap();
		v.put("leaveDay", leave.getDuration());
		activitiServiceImpl.setVariablesByProcessInstanceId(processInstanceId,v);
	
		//发送请假邮件给指定员工
		List<String> emailList = new ArrayList<String>();
		//假期代理人邮箱
		if(agentEmail!=null&&!"".equals(agentEmail)){
			emailList.add(agentEmail);
		}
		//邮件抄送人邮箱
		List<Long> employeeIds = new ArrayList<Long>();
		if(toPersions!=null&&!"".equals(toPersions)){
			for(String person:toPersions.split(",")){
				employeeIds.add(Long.valueOf(person));
			}
		}
		if(employeeIds!=null&&employeeIds.size()>0){
			List<Employee> toPersionsList = employeeService.getListByIds(employeeIds);
			for(Employee data:toPersionsList){
				emailList.add(data.getEmail());
			}
		}
		//抄送邮箱
		if(toEmails!=null&&!"".equals(toEmails)){
			for(String toEmail:toEmails.split(",")){
				if(!toEmail.equals(agentEmail)){
					emailList.add(toEmail);
				}
			}
		}
		//申请人逐级汇报对象到部门负责人为止
		Long reportToLeader = user.getEmployee().getReportToLeader();//申请人汇报对象
		List<Long> reportToLeaderList = new ArrayList<Long>();
		int count = 0;
		while(true){
			count = count + 1;
			if(reportToLeader!=null){
				reportToLeaderList.add(reportToLeader);
				//判断是否是部门负责人
				List<Depart> departList = departMapper.getAllDepartByLeaderId(reportToLeader);
				if(departList!=null&&departList.size()>0){
					break;
				}else{
					Employee reportToLeaderObject = employeeService.getById(reportToLeader);
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
		if(reportToLeaderList!=null&&reportToLeaderList.size()>0){
			List<Employee> leaderList = employeeService.getListByIds(reportToLeaderList);
			for(Employee data:leaderList){
				emailList.add(data.getEmail());
			}
		}
		//申请人是  部门负责人或者技术开发部员工上级领导是部门负责人，需抄送邮件给杨总
		boolean isSendToCoo = false;
		if(user.getEmployee().getId()!=null){
			List<Depart> departList = departMapper.getAllDepartByLeaderId(user.getEmployee().getId());
			if(departList!=null&&departList.size()>0){
				isSendToCoo = true;
			}else{
				//判断是否是技术开发部
				if("117".equals(user.getDepart().getCode())){
					if(user.getEmployee().getReportToLeader()!=null){
						departList = departMapper.getAllDepartByLeaderId(user.getEmployee().getReportToLeader());
						if(departList!=null&&departList.size()>0){
							isSendToCoo = true;
						}
					}
				}
			}
		}
		if(isSendToCoo){
			Employee coo =new Employee();
			coo.setCode("TOM001");
		    List<Employee> cooList = employeeService.getListByCondition(coo);
		    if(cooList!=null&&cooList.size()>0){
		    	emailList.add(cooList.get(0).getEmail());
		    }
		}
		//邮件去重
		List<String> removeRepeatList = new ArrayList<String>();
		Map<String,String> removeRepeatMap = new HashMap<String,String>();
		for(String data:emailList){
			if(data!=null&&!"".equals(data)){
				if(!(removeRepeatMap!=null&&removeRepeatMap.containsKey(data))){
					removeRepeatMap.put(data, data);
					removeRepeatList.add(data);
				}
			}
		}
		//发送邮件
		sendMail(removeRepeatList,leave);
		
		//发送休假异常邮件
		if(!detailList.isEmpty()) {
			
			try {
				sendAbnormalApplyEmail(detailList.get(0).getLeaveType(),user,leave.getDuration(),leave.getStartTime(),leave.getEndTime());
			}catch(Exception e) {
				logger.error("请假申请单sendAbnormalApplyEmail",e);
			}
			
		}
		map.put("success", true);
		
		Long time2 = System.currentTimeMillis();
		logger.info("请假申请单save:use time="+(time2-time1));
		logger.info("请假申请单save:end。。");
		
	    return map;
	}

   public void sendMail(List<String> emailList,EmpApplicationLeave leave){
	    //XX部XXX（人名）将于X年X月X日至X年X月X日休假，假期代理人：XXX，请知晓！
	   String text = leave.getDepartName()+leave.getCnName()+"已申请"
			      +DateUtils.format(leave.getStartTime(),DateUtils.FORMAT_LONG_CN_MM)+"至"+DateUtils.format(leave.getEndTime(),DateUtils.FORMAT_LONG_CN_MM)
			      +"休假，假期代理人："+leave.getAgent()+"，请知晓！";
        List<SendMail> sendMailList = new ArrayList<SendMail>();
		for(String email:emailList){
			if(email!=null&&!"".equals(email)){
				SendMail sendMail = new SendMail();
				sendMail.setReceiver(email);
				sendMail.setSubject("员工申请休假通知——"+leave.getCnName());
				sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
				sendMail.setText(text);
				sendMail.setOaMail(SendMail.OA_MAIL_P);
				sendMailList.add(sendMail);
			}
		}
		sendMailService.batchSave(sendMailList);	
	}
	
	@Override
	public Map<String, Object> saveAbolishLeave(HttpServletRequest request, User user) throws Exception {
		//TODO:销假保存:待确认问题：①假期归还；②销假显示；
		//1.保存销假信息
		//2.修改请假状态
		//
		return null;
	}
	@Override
	public void lockVecation(Long leaveId,boolean applyStatus,User user) throws Exception {
		//调用接口使用,锁定假期
		List<EmpApplicationLeaveDetail> list = empApplicationLeaveDetailMapper.getListByCondition(leaveId);
		List<Map<String, Object>> planLeaves = new ArrayList<Map<String, Object>>();
		EmpApplicationLeave old = empApplicationLeaveMapper.getById(leaveId);
		Employee employee = employeeService.getById(old.getEmployeeId());
		for(EmpApplicationLeaveDetail detail:list){
			if(detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_1.intValue()||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_2.intValue()||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_5.intValue()){
				Map<String, Object> planLeave = new HashMap<String,Object>();
				planLeave.put("companyId", employee.getCompanyId());
				planLeave.put("employeeId", employee.getId());
				planLeave.put("type", detail.getLeaveType());
				planLeave.put("planDays", detail.getLeaveDays());
				planLeave.put("planStartTime",detail.getStartTime());
				planLeave.put("planEndTime",detail.getEndTime());
				planLeave.put("applyStatus", applyStatus);
				planLeave.put("optUser", user.getEmployee().getCnName());
				planLeave.put("empapplicationleaveId", detail.getLeaveId());
				if(detail.getLeaveType().intValue()==5){
					planLeave.put("planDays",detail.getLeaveHours());
				}
				planLeaves.add(planLeave);
			}else{
				//除病假，年假，调休之外的假期，审批通过之后生成一份base_emp_leave记录（按年来）
				if(applyStatus){
					//流水
					LeaveRecord record = new LeaveRecord();
					record.setEmployeeId(employee.getId());
					record.setType(detail.getLeaveType());
					record.setDays(old.getDuration());
					record.setBillId(old.getId());
					record.setBillType(ConfigConstants.LEAVE_KEY);
					record.setDaysUnit(0);//单位
					record.setCreateTime(new Date());
					record.setCreateUser(user.getEmployee().getCnName());
					record.setDelFlag(0);
					record.setSource(0);
					record.setRemark("请假申请");
					leaveRecordMapper.save(record);
					if(detail.getEndTime()==null){
						detail.setEndTime(old.getEndTime());
					}
					//不跨年
					if(DateUtils.format(detail.getStartTime(),"yyyy").equals(DateUtils.format(detail.getEndTime(),"yyyy"))){
						EmpLeave isExist = new EmpLeave();
						isExist.setType(detail.getLeaveType());
						isExist.setYear(Integer.valueOf(DateUtils.format(detail.getStartTime(), "yyyy")));
						isExist.setEmployeeId(employee.getId());
						List<EmpLeave> isExistList = empLeaveService.getByListCondition(isExist);
						if(isExistList!=null&&isExistList.size()>0){
							isExistList.get(0).setUsedDays(isExistList.get(0).getUsedDays()+detail.getLeaveDays());
							empLeaveService.updateById(isExistList.get(0));
							//流水明细
							saveRecordDetail(record.getId(),isExistList.get(0).getId(),detail.getLeaveDays(),user);
						}else{
							EmpLeave empLeave = new EmpLeave();
							empLeave.setCompanyId(employee.getCompanyId());
							empLeave.setEmployeeId(employee.getId());
							empLeave.setYear(Integer.valueOf(DateUtils.format(detail.getStartTime(), "yyyy")));
							empLeave.setType(detail.getLeaveType());
							empLeave.setUsedDays(detail.getLeaveDays());
							empLeave.setCategory(0);
							empLeave.setIsActive(0);
							empLeave.setCreateTime(new Date());
							empLeave.setCreateUser( user.getEmployee().getCnName());
							empLeave.setDelFlag(0);
							empLeaveService.save(empLeave);
							//流水明细
							saveRecordDetail(record.getId(),empLeave.getId(),detail.getLeaveDays(),user);
						}
					}else{
						//跨年先要算出第一年的天数
						//婚假，陪产假，丧假，产前假，产假，流产假，哺乳假
						Double leaveDaysCurrent = 0d;
						if(detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_3.intValue()
								||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_9.intValue()
								||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_10.intValue()
								||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_6.intValue()
								||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_7.intValue()
								||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_8.intValue()
								||detail.getLeaveType().intValue()==ConfigConstants.LEAVE_TYPE_4.intValue()){
							Date endDate = DateUtils.parse(DateUtils.format(detail.getStartTime(), "yyyy")+"-12-31", DateUtils.FORMAT_SHORT);
							leaveDaysCurrent = DateUtils.getIntervalDays(detail.getStartTime(), endDate).doubleValue()+1;
						}else{
							//先看第一年12-31是否有排班，没有依次往前推，推到有排班的那一天，取出下班时间，算出第一年的请假天数
							Date endDate = DateUtils.parse(DateUtils.format(detail.getStartTime(), "yyyy")+"-12-31", DateUtils.FORMAT_SHORT);
							EmployeeClass employeeClass = new EmployeeClass();
							employeeClass.setEmployId(employee.getId());
							employeeClass.setClassDate(endDate);
							EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
							while(true){
								if(empClass!=null){
									break;
								}
								endDate = DateUtils.addDay(endDate, -1);
								employeeClass.setClassDate(endDate);
								empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
							}
							//并非所有的假期都走这个方法的（婚假，陪产假之类的）
							Map<String, Object> leaveDaysCurrentMap = calculatedLeaveDays(employee.getId(),DateUtils.format(detail.getStartTime(), DateUtils.FORMAT_SHORT),
								DateUtils.format(detail.getStartTime(),DateUtils.FORMAT_HH),DateUtils.format(empClass.getClassDate(), DateUtils.FORMAT_SHORT)
								,DateUtils.format(empClass.getEndTime(),DateUtils.FORMAT_HH),String.valueOf(detail.getLeaveType()));
							leaveDaysCurrent = Double.valueOf(String.valueOf(leaveDaysCurrentMap.get("leaveDays")));
						}
						EmpLeave isExist = new EmpLeave();
						isExist.setType(detail.getLeaveType());
						isExist.setYear(Integer.valueOf(DateUtils.format(detail.getStartTime(), "yyyy")));
						isExist.setEmployeeId(employee.getId());
						List<EmpLeave> isExistList = empLeaveService.getByListCondition(isExist);
						if(isExistList!=null&&isExistList.size()>0){
							isExistList.get(0).setUsedDays(isExistList.get(0).getUsedDays()+leaveDaysCurrent);
							empLeaveService.updateById(isExistList.get(0));
							//流水明细
							saveRecordDetail(record.getId(),isExistList.get(0).getId(),leaveDaysCurrent,user);
						}else{
							EmpLeave empLeave = new EmpLeave();
							empLeave.setCompanyId(employee.getCompanyId());
							empLeave.setEmployeeId(employee.getId());
							empLeave.setYear(Integer.valueOf(DateUtils.format(detail.getStartTime(), "yyyy")));
							empLeave.setType(detail.getLeaveType());
							empLeave.setUsedDays(leaveDaysCurrent);
							empLeave.setCategory(0);
							empLeave.setIsActive(0);
							empLeave.setCreateTime(new Date());
							empLeave.setCreateUser(user.getEmployee().getCnName());
							empLeave.setDelFlag(0);
							empLeaveService.save(empLeave);
							//流水明细
							saveRecordDetail(record.getId(),empLeave.getId(),leaveDaysCurrent,user);
						}
						isExist.setYear(Integer.valueOf(DateUtils.format(detail.getEndTime(), "yyyy")));
						List<EmpLeave> isExistListNext = empLeaveService.getByListCondition(isExist);
						if(isExistListNext!=null&&isExistListNext.size()>0){
							isExistListNext.get(0).setUsedDays(isExistListNext.get(0).getUsedDays()+detail.getLeaveDays()-leaveDaysCurrent);
							empLeaveService.updateById(isExistListNext.get(0));
							//流水明细
							saveRecordDetail(record.getId(),isExistListNext.get(0).getId(),detail.getLeaveDays()-leaveDaysCurrent,user);
						}else{
							EmpLeave empLeave = new EmpLeave();
							empLeave.setCompanyId(employee.getCompanyId());
							empLeave.setEmployeeId(employee.getId());
							empLeave.setYear(Integer.valueOf(DateUtils.format(detail.getEndTime(), "yyyy")));
							empLeave.setType(detail.getLeaveType());
							empLeave.setUsedDays(detail.getLeaveDays()-leaveDaysCurrent);
							empLeave.setCategory(0);
							empLeave.setIsActive(0);
							empLeave.setCreateTime(new Date());
							empLeave.setCreateUser(user.getEmployee().getCnName());
							empLeave.setDelFlag(0);
							empLeaveService.save(empLeave);
							//流水明细
							saveRecordDetail(record.getId(),empLeave.getId(),detail.getLeaveDays()-leaveDaysCurrent,user);
						}
					}
				}
			}
		}
		logger.info("锁定假期接口入参：="+planLeaves.toString());
		if(planLeaves!=null&&planLeaves.size()>0){
			empLeaveService.updateEmpLeaveAudit(planLeaves);
		}
	}
	
	public void saveRecordDetail(Long recordId,Long baseEmpLeaveId,Double days,
			User user){
		LeaveRecordDetail recordDetail = new LeaveRecordDetail();
		recordDetail.setLeaveRecordId(recordId);
		recordDetail.setBaseEmpLeaveId(baseEmpLeaveId);
		recordDetail.setDays(days);
		recordDetail.setCreateTime(new Date());
		recordDetail.setCreateUser(user.getEmployee().getCnName());
		recordDetail.setDelFlag(0);
		List<LeaveRecordDetail> isExist1 = leaveRecordDetailMapper.selectByCondition(recordDetail);
		if(isExist1!=null&&isExist1.size()>0){
			isExist1.get(0).setDays(isExist1.get(0).getDays()+days);
			leaveRecordDetailMapper.updateById(isExist1.get(0));
		}else{
			leaveRecordDetailMapper.save(recordDetail);
		}
	}
	
	@Override
	public Map<String, Object> calculatedLeaveDays(Long empId,String startTime1,String startTime2,String endTime1,String endTime2,String leaveType){
		Employee emp = employeeService.getById(empId);
		
		Map<Long,ClassSetting> csMap = new HashMap<Long, ClassSetting>();
		List<ClassSetting> csList = classSettingService.getList();
		for (ClassSetting classSetting : csList) {
			csMap.put(classSetting.getId(), classSetting);
		}
		Map<String,Object> map = new HashMap<String, Object>();
		//开始时间
		Date dayStart = DateUtils.parse(startTime1,DateUtils.FORMAT_SHORT);
		//结束时间
		Date dayEnd = DateUtils.parse(endTime1,DateUtils.FORMAT_SHORT);
		String beginData = "";
		String endData = "";
		int length = DateUtils.getIntervalDays(dayStart, dayEnd) + 1;
		if(OaCommon.getLeaveMap().containsKey(leaveType)) {
			beginData = startTime1;
			endData = endTime1;
		} 
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
			if(StringUtils.isBlank(startTime1)||StringUtils.isBlank(startTime2)||
					StringUtils.isBlank(endTime1)||StringUtils.isBlank(endTime2)){
				map.put("message","请假时间不能为空");
				map.put("success",false);
				return map;
			}
			double leaveDays = 0;
			double leaveHours = 0;
			
			boolean isEndTime = true;
			List<String> datas = new ArrayList<String>();
			do{
				datas.add(DateUtils.format(dayStart, DateUtils.FORMAT_SHORT));
				dayStart = DateUtils.addDay(dayStart, 1);
				if(DateUtils.getIntervalDays(dayStart, dayEnd) < 0) {
					isEndTime = false;
				}
			} while(isEndTime);
			if(OaCommon.getLeaveMap().containsKey(leaveType)) {
				leaveDays = length;
			}else{
				resultMap = employeeClassService.getEmployeeClassSetting(emp,datas);
				beginData = (String) resultMap.get("beginData");
				endData = (String) resultMap.get("endData");
				for (Map.Entry<String,Object> entry : resultMap.entrySet()) {
					if(!entry.getKey().equals("beginData") && !entry.getKey().equals("endData")&&!entry.getKey().equals(startTime1)&&!entry.getKey().equals(endTime1)) {
						EmployeeClass result_e = (EmployeeClass) resultMap.get(entry.getKey());
						if(result_e != null) {
							if(result_e.getClassDate() != null) {
								boolean standard = ("09:00".equals(DateUtils.format(result_e.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(result_e.getEndTime(), "HH:mm")))?true:false;
								boolean standard1 = (result_e.getMustAttnTime()==8)?true:false;
								boolean standard2 = (result_e.getMustAttnTime()>=16)?true:false;
								boolean standard3 = (result_e.getMustAttnTime()>=10&&result_e.getMustAttnTime()<16)?true:false;
								boolean standard4 = (result_e.getMustAttnTime()<8)?true:false;
								if(standard){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 8;
									}
									leaveDays = leaveDays + 1;
								}else if(standard1){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 8;
									}
									leaveDays = leaveDays + 1;
								}else if(standard2){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 16;
									}
									leaveDays = leaveDays + 2;
								}else if(standard3){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + 12;
									}
									leaveDays = leaveDays + 1.5;
								}else if(standard4){
									if("5".equals(leaveType)){
										leaveHours = leaveHours + result_e.getMustAttnTime();
									}
									leaveDays = leaveDays + 1;
								}
							}
						}
					}
				}
			}
			
			if(beginData.equals(startTime1)) {
				EmployeeClass classSetting = (EmployeeClass) resultMap.get(beginData);
				Boolean isCommon = false;
			    if(classSetting != null){
					isCommon = true;
				}
				if(isCommon) {
					boolean standard = ("09:00".equals(DateUtils.format(classSetting.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(classSetting.getEndTime(), "HH:mm")))?true:false;
					boolean standard1 = (classSetting.getMustAttnTime()==8)?true:false;
					boolean standard2 = (classSetting.getMustAttnTime()>=16)?true:false;
					boolean standard3 = (classSetting.getMustAttnTime()>=10&&classSetting.getMustAttnTime()<16)?true:false;
					boolean standard4 = (classSetting.getMustAttnTime()<8)?true:false;
					if(standard){
						if(length == 1){
							if((Integer.valueOf(startTime2).intValue()==9&&Integer.valueOf(endTime2).intValue()==14)||(Integer.valueOf(startTime2).intValue()==9&&Integer.valueOf(endTime2).intValue()==13)){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 3;
									leaveDays = leaveDays + 0.5;
								}else{
									leaveDays = leaveDays + 0.5;
								}
							}else if(Integer.valueOf(startTime2).intValue()==12&&Integer.valueOf(endTime2).intValue()==18){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 5;
								}
								leaveDays = leaveDays + 0.5;
							}else if(Integer.valueOf(startTime2).intValue()==9&&Integer.valueOf(endTime2).intValue()==18){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==9){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==12){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 5;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard1){
						if(length == 1){
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(),-300), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 300), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 300), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard2){
						if(length == 1){
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 16;
								}
								leaveDays = leaveDays + 2;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(),240), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 720), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 16;
								}
								leaveDays = leaveDays + 2;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(),240), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 720), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard3){
						if(length == 1){
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 240), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()
									&& Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}else{
							if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getStartTime(), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 12;
								}
								leaveDays = leaveDays + 1.5;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 240), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 8;
								}
								leaveDays = leaveDays + 1;
							}else if(Integer.valueOf(startTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getStartTime(), 480), "HH")).intValue()){
								if("5".equals(leaveType)){
									leaveHours = leaveHours + 4;
								}
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard4){
						if("5".equals(leaveType)){
							leaveHours = leaveHours + classSetting.getMustAttnTime();
						}
						leaveDays = leaveDays + 1;
					}
				}
			}
			
			if(length >1 && endData.equals(endTime1)) {
				EmployeeClass classSetting = (EmployeeClass) resultMap.get(endData);
				Boolean isCommon = false; 
				if(classSetting != null){
					isCommon = true;
				}
				if(isCommon) {
					boolean standard = ("09:00".equals(DateUtils.format(classSetting.getStartTime(), "HH:mm"))&&"18:00".equals(DateUtils.format(classSetting.getEndTime(), "HH:mm")))?true:false;
					boolean standard1 = (classSetting.getMustAttnTime()==8)?true:false;
					boolean standard2 = (classSetting.getMustAttnTime()>=16)?true:false;
					boolean standard3 = (classSetting.getMustAttnTime()>=10&&classSetting.getMustAttnTime()<16)?true:false;
					boolean standard4 = (classSetting.getMustAttnTime()<8)?true:false;
					if(standard){
						if(Integer.valueOf(endTime2).intValue()==18){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 8;
							}
							leaveDays = leaveDays + 1;
						}else if(Integer.valueOf(endTime2).intValue()==14||Integer.valueOf(endTime2).intValue()==13){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 3;
								leaveDays = leaveDays + 0.5;
							}else{
								leaveDays = leaveDays + 0.5;
							}
						}
					}else if(standard1){
						if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 8;
							}
							leaveDays = leaveDays + 1;
						}else if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(DateUtils.addMinute(classSetting.getEndTime(), -300), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 4;
							}
							leaveDays = leaveDays + 0.5;
						}
					}else if(standard2){
						if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 16;
							}
							leaveDays = leaveDays + 2;
						}
					}else if(standard3){
						if(Integer.valueOf(endTime2).intValue()==Integer.valueOf(DateUtils.format(classSetting.getEndTime(), "HH")).intValue()){
							if("5".equals(leaveType)){
								leaveHours = leaveHours + 12;
							}
							leaveDays = leaveDays + 1.5;
						}
					}else if(standard4){
						if("5".equals(leaveType)){
							leaveHours = leaveHours + classSetting.getMustAttnTime();
						}
						leaveDays = leaveDays + 1;
					}
				} 
			}
			map.put("leaveDays",leaveDays);
			map.put("leaveHours",leaveHours);
			map.put("success", true);
		}catch(Exception e){
			logger.error(emp.getCnName()+"请假-计算假期："+e.toString());
			map.put("success",false);
		}
		return map;
	}

	@Override
	public List<EmpApplicationLeaveDetail> getListByEmployee(
			EmpApplicationLeaveDetail leaveDetail) {
		return empApplicationLeaveDetailMapper.getListByEmployee(leaveDetail);
	}

	@Override
	public Boolean isContinuityDate(EmpApplicationLeaveDetail leaveDetail) {
		Integer ontinuityDateCount = empApplicationLeaveDetailMapper.continuityDate(leaveDetail);
		if(ontinuityDateCount != null && ontinuityDateCount > 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public List<EmpApplicationLeave> getListByCondition(EmpApplicationLeave model) {
		return empApplicationLeaveMapper.getListByCondition(model);
	}

	@Override
	public PageModel<EmpApplicationLeave> getReportPageList(
			EmpApplicationLeave leave) {
		
		if(leave.getApplyStartDate()!=null){
			leave.setApplyStartDate(DateUtils.parse(DateUtils.format(leave.getApplyStartDate(), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG));
		}
		if(leave.getApplyEndDate()!=null){
			leave.setApplyEndDate(DateUtils.parse(DateUtils.format(leave.getApplyEndDate(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
		}
		
		int page = leave.getPage() == null ? 0 : leave.getPage();
		int rows = leave.getRows() == null ? 0 : leave.getRows();
		
		PageModel<EmpApplicationLeave> pm = new PageModel<EmpApplicationLeave>();

		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			leave.setOffset(pm.getOffset());
			leave.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<EmpApplicationLeave>());
			return pm;
		}else{
			leave.setCurrentUserDepart(deptDataByUserList);//数据权限
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				leave.setSubEmployeeIdList(subEmployeeIdList);
			}
			pm.setPageNo(page);
			pm.setPageSize(rows);
			Integer total = empApplicationLeaveMapper.getReportCount(leave);
			pm.setTotal(total);
			
			leave.setOffset(pm.getOffset());
			leave.setLimit(pm.getLimit());
			
			List<EmpApplicationLeave> roles = empApplicationLeaveMapper.getReportPageList(leave);
			for(EmpApplicationLeave og:roles){
				//小于3天，批核人是汇报对象，否则是部门负责人   11
				try{
					ViewTaskInfoTbl taskInfo = null;
					if(og.getLeaveDays()>=3){
						taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.LEAVE_KEY,true);
					}else{
						taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.LEAVE_KEY,false);
					}
					og.setAuditUser("");
					if(null != taskInfo){
						og.setAuditUser(taskInfo.getAssigneeName());
					}
				}catch(Exception e){
					og.setAuditUser(" ");
				}
			}
			pm.setRows(roles);
			return pm;
		}	
	}

	@Override
	public HSSFWorkbook exportExcel(EmpApplicationLeave leave) {
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			leave.setCurrentUserDepart(deptDataByUserList);
			List<Long> subEmployeeIdList = employeeService.getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				leave.setSubEmployeeIdList(subEmployeeIdList);
			}
			List<Map<String,Object>> sMapList = new ArrayList<Map<String,Object>>();
			if(leave.getApplyStartDate()!=null){
				leave.setApplyStartDate(DateUtils.parse(DateUtils.format(leave.getApplyStartDate(), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG));
			}
			if(leave.getApplyEndDate()!=null){
				leave.setApplyEndDate(DateUtils.parse(DateUtils.format(leave.getApplyEndDate(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
			}
			List<EmpApplicationLeave> list = empApplicationLeaveMapper.getExportReportList(leave);
			for(EmpApplicationLeave og:list){
				//小于3天，批核人是汇报对象，否则是部门负责人
				try{
					ViewTaskInfoTbl taskInfo = null;
					if(og.getLeaveDays()>=3){
						taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.LEAVE_KEY,true);
					}else{
						taskInfo = viewTaskInfoService.getFirstAuditUser(og.getProcessInstanceId(),ConfigConstants.LEAVE_KEY,false);
					}
					og.setAuditUser("");
					if(null != taskInfo){
						og.setAuditUser(taskInfo.getAssigneeName());
					}
				}catch(Exception e){
					og.setAuditUser(" ");
				}
			}
			if(list!=null&&list.size()>0) {
				for (EmpApplicationLeave re : list) {
					//封装数据
					Map<String,Object> sdoMap = new HashMap<String, Object>();
					sdoMap.put("code",re.getCode());
					sdoMap.put("cnName",re.getCnName());
					sdoMap.put("departName",re.getDepartName());
					sdoMap.put("positionName",re.getPositionName());
					sdoMap.put("workType",re.getWorkType());
					String leaveType = "";
					if(re.getLeaveType().intValue()==1){
						leaveType = "年假";
					}else if(re.getLeaveType().intValue()==2){
						leaveType = "病假";
					}else if(re.getLeaveType().intValue()==3){
						leaveType = "婚假";
					}else if(re.getLeaveType().intValue()==4){
						leaveType = "哺乳假";
					}else if(re.getLeaveType().intValue()==5){
						leaveType = "调休";
					}else if(re.getLeaveType().intValue()==6){
						leaveType = "产前假";
					}else if(re.getLeaveType().intValue()==7){
						leaveType = "产假";
					}else if(re.getLeaveType().intValue()==8){
						leaveType = "流产假";
					}else if(re.getLeaveType().intValue()==9){
						leaveType = "陪产假";
					}else if(re.getLeaveType().intValue()==10){
						leaveType = "丧假";
					}else if(re.getLeaveType().intValue()==11){
						leaveType = "事假";
					}else if(re.getLeaveType().intValue()==12){
						leaveType = "其他";
					}
					sdoMap.put("leaveType",leaveType);
					sdoMap.put("startTime",DateUtils.format(re.getStartTime(), DateUtils.FORMAT_LONG));
					sdoMap.put("endTime",DateUtils.format(re.getEndTime(), DateUtils.FORMAT_LONG));
					if(re.getLeaveType().intValue()==5){
						sdoMap.put("leaveDays",re.getLeaveDays()+"("+re.getLeaveHours()+"小时)");
					}else{
						sdoMap.put("leaveDays",re.getLeaveDays());
					}
					sdoMap.put("reason",re.getReason());
					sdoMap.put("submitDate",DateUtils.format(re.getSubmitDate(), DateUtils.FORMAT_LONG));
					sdoMap.put("auditUser",re.getAuditUser());
					String approvalStatus = "";
					if(re.getApprovalStatus().intValue()==100){
						approvalStatus = "待审批";
					}else if(re.getApprovalStatus().intValue()==200){
						approvalStatus = "已审批";
					}else if(re.getApprovalStatus().intValue()==300){
						approvalStatus = "已拒绝";
					}else if(re.getApprovalStatus().intValue()==400){
						approvalStatus = "已撤销";
					}
					sdoMap.put("approvalStatus",approvalStatus);
					sMapList.add(sdoMap);
				}
			}
			String[] keys={"code", "cnName", "departName", "positionName","workType","leaveType","startTime", "endTime", "leaveDays", "reason","submitDate","auditUser","approvalStatus"};
			String[] titles={"员工编号", "员工姓名", "部门","职位名称","工时制", "请假类型", "开始日期", "结束日期", "请假天数", "请假事由", "申请日期", "批核人","状态"}; 
			return ExcelUtil.exportExcel(sMapList, keys, titles, "请假查询报表.xls");
		}	
	}

	@Override
	public List<EmpApplicationLeave> getListByEmployeeId(Long employeeId) {
		return empApplicationLeaveMapper.getListByEmployeeId(employeeId);
	}

	@Override
	public Map<String, Object> getLeaveCountInfoByEmpId(Long employeeId){
		int currentYear = Integer.valueOf(DateUtils.getYear(new Date()));
		int lastYear = currentYear -1;
		/**判断是否为4月第6个工作日**/
		Date theSixthWorkDay = annualVacationService.getWorkingDayOfMonth(DateUtils.parse(String.valueOf(currentYear)+"-04-01",DateUtils.FORMAT_SHORT), 6);
		Date thisDay = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT), DateUtils.FORMAT_SHORT);
		Double allowAnnualLeave = 0d;//剩余年假
		Double usedAnnualLeave = 0d;//已用年假（只记当前年份）
		Double allowSickLeave = 0d;//剩余病假
		Double usedSickLeave = 0d;//已用病假
		Double allowDayOff = 0d;//剩余调休
		Double usedDayOff = 0d;//已用调休
		Double usedAffairsLeave = 0d;//已用事假
		
		//年假
		EmpLeave yearListP = new EmpLeave();
		yearListP.setEmployeeId(employeeId);
		yearListP.setType(ConfigConstants.LEAVE_TYPE_1);
		List<Integer> yearList = new ArrayList<Integer>();
		yearList.add(currentYear);
		yearList.add(lastYear);
		yearListP.setYearList(yearList);
		List<EmpLeave> yearLeaveList = empLeaveMapper.getListByCondition(yearListP);
		for(EmpLeave leave : yearLeaveList){
			//今年
			if(leave.getYear().intValue()==currentYear&&leave.getCategory().intValue()==0){
				allowAnnualLeave = allowAnnualLeave + (leave.getAllowRemainDays()!=null?leave.getAllowRemainDays():0);
				usedAnnualLeave = usedAnnualLeave + (leave.getUsedDays()!=null?leave.getUsedDays():0);
			}
            //去年
            if(leave.getYear().intValue()==lastYear&&leave.getCategory().intValue()==0){
            	if(thisDay.before(theSixthWorkDay)){
            		allowAnnualLeave = allowAnnualLeave + (leave.getAllowRemainDays()!=null?leave.getAllowRemainDays():0);
        		}
			}
		}
		
		//病假
		EmpLeave sickListP = new EmpLeave();
		sickListP.setEmployeeId(employeeId);
		sickListP.setType(ConfigConstants.LEAVE_TYPE_2);
		sickListP.setYear(currentYear);
		List<EmpLeave> sickLeaveList = empLeaveMapper.getListByCondition(sickListP);
		for(EmpLeave leave : sickLeaveList){
			if(leave.getCategory().intValue()==0){
				allowSickLeave = allowSickLeave + (leave.getAllowRemainDays()!=null?leave.getAllowRemainDays():0);
				usedSickLeave = usedSickLeave + (leave.getUsedDays()!=null?leave.getUsedDays():0);
			}
			if(leave.getCategory().intValue()==2){
				usedSickLeave = usedSickLeave + (leave.getUsedDays()!=null?leave.getUsedDays():0);
			}
		}
		
		//调休
		EmpLeave offListP = new EmpLeave();
		offListP.setEmployeeId(employeeId);
		offListP.setType(ConfigConstants.LEAVE_TYPE_5);
		offListP.setYearList(yearList);
		offListP.setCategory(0);
		List<EmpLeave> offLeaveList = empLeaveMapper.getListByCondition(offListP);
		for(EmpLeave leave : offLeaveList){
			if(leave.getCategory().intValue()==0&&leave.getYear().intValue()==currentYear){
				allowDayOff = allowDayOff + (leave.getAllowRemainDays()!=null?leave.getAllowRemainDays():0);
				usedDayOff = usedDayOff + (leave.getUsedDays()!=null?leave.getUsedDays():0);
			}
			if(leave.getCategory().intValue()==0&&leave.getYear().intValue()==lastYear){
				if(thisDay.before(theSixthWorkDay)){
					allowDayOff = allowDayOff + (leave.getAllowRemainDays()!=null?leave.getAllowRemainDays():0);
				}
			}
		}
		
		//时间
		EmpLeave affairsListP = new EmpLeave();
		affairsListP.setEmployeeId(employeeId);
		affairsListP.setType(ConfigConstants.LEAVE_TYPE_11);
		affairsListP.setYear(currentYear);
		List<EmpLeave> affairsLeaveList = empLeaveMapper.getListByCondition(affairsListP);
		for(EmpLeave leave : affairsLeaveList){
			usedAffairsLeave = usedAffairsLeave + (leave.getUsedDays()!=null?leave.getUsedDays():0);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("allowAnnualLeave", allowAnnualLeave);
		result.put("usedAnnualLeave", usedAnnualLeave);
		result.put("allowSickLeave", allowSickLeave);
		result.put("usedSickLeave", usedSickLeave);
		result.put("allowDayOff", allowDayOff);
		result.put("usedDayOff", usedDayOff);
		result.put("affairsLeave", usedAffairsLeave);
		
		return result;
	}

	/**
	 * 根据流程实例id查询请假单
	 */
	@Override
	public EmpApplicationLeave queryByProcessInstanceId(String instanceId) {
		return empApplicationLeaveMapper.queryByProcessId(instanceId);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
	public String completeTasks(HttpServletRequest request,String[] instanceIds,String comment, String commentType) throws Exception {
		StringBuffer result = new StringBuffer();
		for (String instanceId : instanceIds) {
			try {
				completeTask(request,instanceId,comment,commentType);
			}catch(Exception e) {
				logger.error("流程实例ID="+instanceId+"的请假单，审批失败！");
				result.append(instanceId);
			}
			continue;
		}
		return result.toString();
	}
	/**
	 * 办理假期任务
	 * @throws Exception 
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTask(HttpServletRequest request,String instanceId,String comment, String commentType) throws Exception {
		   
		logger.info("请假申请单completeTask:start。。");
		Long time1 = System.currentTimeMillis();
		
		User user = userService.getCurrentUser();
		logger.info("请假申请单completeTask入参:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		Task task = activitiServiceImpl.queryTaskByProcessInstanceId(instanceId);
		EmpApplicationLeave leave = queryByProcessInstanceId(instanceId);
		if(null == task) {
			throw new OaException("该条假期已被结束流程,请确认后再操作。");
		}else if(!StringUtils.equalsIgnoreCase(task.getAssignee(), user.getEmployeeId().toString())
				&&!StringUtils.equalsIgnoreCase(leave.getEmployeeId().toString(), user.getEmployeeId().toString())){
			task = activitiServiceImpl.queryTaskByTaskIdAndCandidate(task.getId(), user.getEmployeeId().toString());
			if(task == null) {
				throw new OaException("该条假期没有权限操作,请确认后再操作。");
			}
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		
		// 请假
		if(leave!=null){
		//产假无开始时间限制	
		if(annualVacationService.check5WorkingDayNextmonth(DateUtils.format(leave.getStartTime(), DateUtils.FORMAT_SHORT),type) && leave.getLeaveTypeFlag()!=0){			
			logger.error(userService.getCurrentUser().getEmployee().getCnName()+"审批员工Id="+leave.getEmployeeId()+"的请假：已超出有效时间,无法操作！");
			throw new OaException("已超出有效时间,无法操作！");
		}else{
				Integer approvalStatus ;
				if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
					approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
					
				}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
					approvalStatus=ConfigConstants.REFUSE_STATUS;
				}else {
					approvalStatus=ConfigConstants.BACK_STATUS;
				}
				leave.setApprovalStatus(approvalStatus);
				leave.setNodeCode(task.getName());
				updateById(leave);
				//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
				if(StringUtils.isBlank(task.getAssignee())) {
					activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
				}
				sendMailService.sendCommentMail(employeeService.getByEmpId(leave.getEmployeeId()).get(0).getEmail(), "请假申请单", task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
				//-----------------start-----------------------保存流程节点信息
				ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
				flow.setAssigneeName(user.getEmployee().getCnName());
				flow.setDepartName(user.getDepart().getName());
				flow.setPositionName(user.getPosition().getPositionName());
				flow.setFinishTime(new Date());
				flow.setComment(comment);
				flow.setProcessId(instanceId);
				flow.setProcessKey(activitiServiceImpl.queryProcessByInstanceId(instanceId).getKey());
				flow.setStatu(approvalStatus);
				viewTaskInfoService.save(flow);
				//-----------------end-------------------------
				Map<String,Object> isAdmin = new HashMap<String,Object>();
				isAdmin.put("isAdmin", false);
				activitiServiceImpl.completeTask(task.getId(), comment,isAdmin,commentType);
			}
		}else{
			logger.error("流程实例为"+instanceId+"的请假审批数据不存在！");
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("请假申请单completeTask:use time="+(time2-time1));
		logger.info("请假申请单completeTask:end。。");
		
	}
	
	/**
	 * 业务对象转换为展示对象
	 */
	@Override
	public void setValueToVO(TaskVO taskVO, String processInstanceId) {
		EmpApplicationLeave leaveInfo = queryByProcessInstanceId(processInstanceId);
		if(leaveInfo!=null){
			List<EmpApplicationLeaveDetail> list = empApplicationLeaveDetailService.getList(leaveInfo.getId());
			taskVO.setProcessName("请假申请单");
			String redirectUrl = "/empApplicationLeave/approval.htm?flag=no&leaveId="+leaveInfo.getId();
			if(!(taskVO.getProcessStatu()==null)) {
				redirectUrl = "/empApplicationLeave/approval.htm?flag=can&leaveId="+leaveInfo.getId();
			}
			taskVO.setRedirectUrl(redirectUrl);
			taskVO.setCreatorDepart(leaveInfo.getDepartName());
			taskVO.setProcessStatu(leaveInfo.getApprovalStatus());
			taskVO.setOperation(ConfigConstants.STATUS.getOperationByStatu(leaveInfo.getApprovalStatus()));
			taskVO.setCreator(leaveInfo.getCreateUser());
			taskVO.setCreateTime(leaveInfo.getCreateTime());
			taskVO.setView3(OaCommon.getLeaveTypeMap().get(list.get(0).getLeaveType()));
			taskVO.setView4(DateUtils.format(leaveInfo.getStartTime(), ConfigConstants.DATEFORMAT)+"~"+DateUtils.format(leaveInfo.getEndTime(), ConfigConstants.DATEFORMAT) + "&nbsp;&nbsp;" + leaveInfo.getDuration() + "天");
			taskVO.setView5(leaveInfo.getReason());
			taskVO.setReProcdefCode("60");
			taskVO.setProcessId(leaveInfo.getProcessInstanceId());
			taskVO.setResourceId(String.valueOf(leaveInfo.getId()));
		}else{
			logger.error("根据processInstanceId="+processInstanceId+"未查到请假单据。");
		}
	}

	@Override
	public void updateProcessInstanceId(EmpApplicationLeave newLeave) {
		empApplicationLeaveMapper.updateById(newLeave);
	}

	@Override
	public void sendRefuseLeaveNotice(String processInstanceId,String comment) {
		EmpApplicationLeave leave = empApplicationLeaveMapper.queryByProcessId(processInstanceId);
		if(leave!=null){
			Employee employee = employeeService.getById(leave.getEmployeeId());
			if(employee!=null){
				List<SendMail> sendMailList = new ArrayList<SendMail>();
				SendMail sendMail = new SendMail();
				sendMail.setReceiver(employee.getEmail());
				sendMail.setSubject("审批拒绝通知");
				sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
				sendMail.setText(comment);
				sendMail.setOaMail(SendMail.OA_MAIL_P);
				sendMailList.add(sendMail);	
				if(sendMailList!=null&&sendMailList.size()>0){
					sendMailService.batchSave(sendMailList);
				}
				EmpMsg dhMsg = new EmpMsg();
				dhMsg.setDelFlag(CommonPo.STATUS_NORMAL);
				dhMsg.setType(EmpMsg.type_200);
				dhMsg.setCompanyId(employee.getCompanyId());
				dhMsg.setEmployeeId(employee.getId());
				dhMsg.setTitle("审批拒绝通知");
				dhMsg.setContent(comment);
				dhMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
				dhMsg.setCreateTime(new Date());
				empMsgService.save(dhMsg);
			}
		}
	}

	@Override
	public PageModel<EmpApplicationLeave> myLeaveTaskList(
			EmpApplicationLeave leave) {
		if(leave.getApplyStartDate()!=null){
			leave.setApplyStartDate(DateUtils.parse(DateUtils.format(leave.getApplyStartDate(), DateUtils.FORMAT_SHORT)+" 00:00:01", DateUtils.FORMAT_LONG));
		}
		if(leave.getApplyEndDate()!=null){
			leave.setApplyEndDate(DateUtils.parse(DateUtils.format(leave.getApplyEndDate(), DateUtils.FORMAT_SHORT)+" 23:59:59", DateUtils.FORMAT_LONG));
		}
		
		int page = leave.getPage() == null ? 0 : leave.getPage();
		int rows = leave.getRows() == null ? 0 : leave.getRows();
		
		PageModel<EmpApplicationLeave> pm = new PageModel<EmpApplicationLeave>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = empApplicationLeaveMapper.myLeaveTaskListCount(leave);
		pm.setTotal(total);
		
		leave.setOffset(pm.getOffset());
		leave.setLimit(pm.getLimit());
		
		List<EmpApplicationLeave> roles = empApplicationLeaveMapper.myLeaveTaskList(leave);
		pm.setRows(roles);
		return pm;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void completeTaskByAdmin(HttpServletRequest request,String instanceId, String comment,
		String commentType, User user, Task task) throws Exception {
		
		logger.info("请假申请单completeTaskByAdmin:start。。");
		Long time1 = System.currentTimeMillis();
		logger.info("请假申请单completeTaskByAdmin入参:instanceId="+instanceId+";comment="+comment+";commentType="+commentType
				+";operator="+user.getEmployee().getCnName()+";ip="+IPUtils.getIpAddress(request));
		
		EmpApplicationLeave leave = queryByProcessInstanceId(instanceId);
		if(null == task) {
			throw new OaException("该条假期已被结束流程,请确认后再操作。");
		}
		boolean type = StringUtils.startsWithIgnoreCase(task.getTaskDefinitionKey(), "end_");
		// 请假
		if(leave!=null){
			Integer approvalStatus ;
			if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.PASS)) {
				approvalStatus=type?ConfigConstants.PASS_STATUS:ConfigConstants.DOING_STATUS;
				
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEPASS)){
				approvalStatus=type?ConfigConstants.OVERDUEPASS_STATUS:ConfigConstants.DOING_STATUS;							
 			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.REFUSE)) {
				approvalStatus=ConfigConstants.REFUSE_STATUS;
			}else if(StringUtils.equalsIgnoreCase(commentType, ConfigConstants.OVERDUEREFUSE)){
				approvalStatus=ConfigConstants.OVERDUEREFUSE_STATUS;
 			}else {
				approvalStatus=ConfigConstants.BACK_STATUS;
			}
			leave.setApprovalStatus(approvalStatus);
			leave.setNodeCode(task.getName());
			updateById(leave);
			//判断当前节点是否有办理人assignee,没有则将当前代理人设为办理人
			if(StringUtils.isBlank(task.getAssignee())) {
				activitiServiceImpl.claimTask(task.getId(), String.valueOf(userService.getCurrentUser().getEmployeeId()));
			}
			sendMailService.sendCommentMail(employeeService.getByEmpId(leave.getEmployeeId()).get(0).getEmail(), "请假申请单", task.getName()+"-"+user.getEmployee().getCnName(), approvalStatus);
			//-----------------start-----------------------保存流程节点信息
			ViewTaskInfoTbl flow = new ViewTaskInfoTbl();
			flow.setAssigneeName(user.getEmployee().getCnName());
			flow.setDepartName(user.getDepart().getName());
			flow.setPositionName(user.getPosition().getPositionName());
			flow.setFinishTime(new Date());
			flow.setComment(comment);
			flow.setProcessId(instanceId);
			flow.setProcessKey(activitiServiceImpl.queryProcessByInstanceId(instanceId).getKey());
			flow.setStatu(approvalStatus);
			viewTaskInfoService.save(flow);
			//-----------------end-------------------------
			Map<String,Object> isAdmin = new HashMap<String,Object>();
			isAdmin.put("isAdmin", true);
			activitiServiceImpl.completeTask(task.getId(), comment,isAdmin,commentType);
		}else{
			logger.error("流程实例为"+instanceId+"的请假审批数据不存在！");
		}
		
		Long time2 = System.currentTimeMillis();
		logger.info("请假申请单completeTaskByAdmin:use time="+(time2-time1));
		logger.info("请假申请单completeTaskByAdmin:end。。");
		
	}

	@Override
	public PageModel<EmpApplicationLeave> getAbatePageList(
			EmpApplicationLeave leave) {
		// TODO Auto-generated method stub
		int page = leave.getPage() == null ? 0 : leave.getPage();
		int rows = leave.getRows() == null ? 0 : leave.getRows();
		
		PageModel<EmpApplicationLeave> pm = new PageModel<EmpApplicationLeave>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
 		Integer total = empApplicationLeaveMapper.getReportCount(leave);
		pm.setTotal(total);
		
		
		leave.setOffset(pm.getOffset());
		leave.setLimit(pm.getLimit());
		
		List<EmpApplicationLeave> roles = empApplicationLeaveMapper.getReportPageList(leave);
		pm.setRows(roles);
		return pm;
	}

	@Override
	public void repairErrorEmpClassApply() {
		
		//查询2020-03-01之后的所有请假单据
		
		EmpApplicationLeave param = new EmpApplicationLeave();
		param.setApplyStartDate(DateUtils.parse("2020-03-01", DateUtils.FORMAT_SHORT));
		param.setApplyEndDate(DateUtils.parse("2020-07-01", DateUtils.FORMAT_SHORT));
		
		List<EmpApplicationLeave> roles = empApplicationLeaveMapper.getReportPageList(param);
		
		for(EmpApplicationLeave leave:roles){
			//年假，事假，病假，调休，其它
			if(leave.getLeaveType()!=null&&(leave.getLeaveType().intValue()==1
		       ||leave.getLeaveType().intValue()==2||leave.getLeaveType().intValue()==5
		       ||leave.getLeaveType().intValue()==11||leave.getLeaveType().intValue()==12)){
			   Date startTime = leave.getStartTime();//请假开始时间
			   Date endTime = leave.getEndTime();//请假结束时间
			   //查询开始时间班次
			   EmployeeClass employeeClassSP = new EmployeeClass();
			   employeeClassSP.setEmployId(leave.getEmployeeId());
			   employeeClassSP.setClassDate(startTime);
			   
			   EmployeeClass empClassS = employeeClassService.getEmployeeClassSetting(employeeClassSP);
			   if(empClassS!=null&&empClassS.getStartTime()!=null){
				   String startM = DateUtils.format(empClassS.getStartTime(), "mm");
				   String applyStartM = DateUtils.format(startTime, "mm");
				   //半点对不上的请假单数据
				   if("30".equals(startM)&&!"30".equals(applyStartM)){
					   Date applyStartMNew = DateUtils.parse(DateUtils.format(startTime, "yyyy-MM-dd HH")+":30:00", DateUtils.FORMAT_LONG);
					   EmpApplicationLeave updateApplyS = new EmpApplicationLeave();
					   updateApplyS.setId(leave.getId());
					   updateApplyS.setStartTime(applyStartMNew);
					   updateApplyS.setUpdateTime(new Date());
					   empApplicationLeaveMapper.updateById(updateApplyS);
					   EmpApplicationLeaveDetail applyStartMNew1 = new EmpApplicationLeaveDetail();
					   applyStartMNew1.setLeaveId(leave.getId());
					   applyStartMNew1.setStartTime(applyStartMNew);
					   applyStartMNew1.setUpdateTime(new Date());
					   empApplicationLeaveDetailMapper.updateByLeaveId(applyStartMNew1);
				   }
			   }
			   
			   employeeClassSP.setClassDate(endTime);
			   EmployeeClass empClassE = employeeClassService.getEmployeeClassSetting(employeeClassSP);
			   if(empClassE!=null&&empClassE.getEndTime()!=null){
				   String endM = DateUtils.format(empClassE.getEndTime(), "mm");
				   String applyEndM = DateUtils.format(endTime, "mm");
				   //半点对不上的请假单数据
				   if("30".equals(endM)&&!"30".equals(applyEndM)){
					   Date applyEndMNew = DateUtils.parse(DateUtils.format(endTime, "yyyy-MM-dd HH")+":30:00", DateUtils.FORMAT_LONG);
					   EmpApplicationLeave updateApplyE = new EmpApplicationLeave();
					   updateApplyE.setId(leave.getId());
					   updateApplyE.setEndTime(applyEndMNew);
					   updateApplyE.setUpdateTime(new Date());
					   empApplicationLeaveMapper.updateById(updateApplyE);
					   EmpApplicationLeaveDetail applyEndMNew1 = new EmpApplicationLeaveDetail();
					   applyEndMNew1.setLeaveId(leave.getId());
					   applyEndMNew1.setEndTime(applyEndMNew);
					   applyEndMNew1.setUpdateTime(new Date());
					   empApplicationLeaveDetailMapper.updateByLeaveId(applyEndMNew1);
				   }
			   }
			}
		}
		
		//修复attn_work_hours数据
		List<AttnWorkHours> workHourList = attnWorkHoursMapper.getNeedRepairList();
		for(AttnWorkHours workHour:workHourList){
			//查询开始时间班次
		    EmployeeClass employeeClass = new EmployeeClass();
		    employeeClass.setEmployId(workHour.getEmployId());
		    employeeClass.setClassDate(workHour.getWorkDate());
		   
		    EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
		    
		    if(empClass!=null&&empClass.getStartTime()!=null){
		    	   String startM = DateUtils.format(empClass.getStartTime(), "mm");
				   String applyStartM = DateUtils.format(workHour.getStartTime(), "mm");
				   //半点对不上的数据
				   if("30".equals(startM)&&!"30".equals(applyStartM)){
					   Date applyStart = DateUtils.parse(DateUtils.format(workHour.getStartTime(), "yyyy-MM-dd HH")+":30:00", DateUtils.FORMAT_LONG);
					   Date applyEnd = DateUtils.parse(DateUtils.format(workHour.getEndTime(), "yyyy-MM-dd HH")+":30:00", DateUtils.FORMAT_LONG);
					   attnWorkHoursMapper.repairDate(applyStart,applyEnd,workHour.getId());
				   }
		    }
		}
		
	}

	@Override
	public void repairErrorWorkHours(Long id, Integer delFlag) {
		attnWorkHoursMapper.updateById1(id,delFlag);
		
	}

	@Override
	public Map<String, Object> updateLeaveTime(Long id, String startTime, String endTime) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		//查询单据
		EmpApplicationLeave leave = empApplicationLeaveMapper.getById(id);
		
		if(leave != null) {
			
			Employee employee = employeeService.getById(leave.getEmployeeId());
			
			//申请单明细
			List<EmpApplicationLeaveDetail> list = empApplicationLeaveDetailMapper.getListByCondition(leave.getId());
			
			Date applyStartTime = DateUtils.parse(startTime, DateUtils.FORMAT_LONG);
			Date applyEndTime = DateUtils.parse(endTime, DateUtils.FORMAT_LONG);
			
			//修改申请单时间
			EmpApplicationLeave updateLeaveTime = new EmpApplicationLeave();
			updateLeaveTime.setStartTime(applyStartTime);
			updateLeaveTime.setEndTime(applyEndTime);
			updateLeaveTime.setId(leave.getId());
			empApplicationLeaveMapper.updateById(updateLeaveTime);
			
			//删除考勤数据
			AttnWorkHours attnWorkHours = new AttnWorkHours();
			attnWorkHours.setDelFlag(CommonPo.STATUS_DELETE);
			attnWorkHours.setBillId(leave.getId());
			attnWorkHours.setDataType(Integer.valueOf(RunTask.RUN_CODE_60));
			attnWorkHours.setUpdateTime(new Date());
			attnWorkHours.setUpdateUser("system");
			attnWorkHoursMapper.deleteByBillId(attnWorkHours);
			
			AttnStatisticsUtil attnStatisticsUtil= new AttnStatisticsUtil();
			
			for(EmpApplicationLeaveDetail data:list) {
				EmpApplicationLeaveDetail updateLeaveDetailTime = new EmpApplicationLeaveDetail();
				updateLeaveDetailTime.setId(data.getId());
				updateLeaveDetailTime.setStartTime(applyStartTime);
				updateLeaveDetailTime.setEndTime(applyEndTime);
				empApplicationLeaveDetailMapper.updateById(updateLeaveDetailTime);
				
				//重新计算考勤
				attnStatisticsUtil.calWorkHours("system",employee, applyStartTime, applyEndTime,RunTask.RUN_CODE_60, String.valueOf(data.getLeaveType()),data.getLeaveDays(),0,"",leave.getId());
			}
			
			result.put("msg", "修改成功");
		}else {
			result.put("msg", "单据不存在");
		}
		
		return result;
	}

	@Override
	public Map<String, Object> getLeaveHours(Long employeeId, String type, String date, String leaveType) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		EmployeeClass employeeClass = new EmployeeClass();
		employeeClass.setEmployId(employeeId);
		employeeClass.setClassDate(DateUtils.parse(date, DateUtils.FORMAT_SHORT));
		EmployeeClass empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
		String hoursList = "";
		if (empClass != null) {
			map.put("code", "0000");
			// 09-18的标准时间
			boolean standard = ("09:00".equals(DateUtils.format(empClass.getStartTime(), "HH:mm"))
					&& "18:00".equals(DateUtils.format(empClass.getEndTime(), "HH:mm"))) ? true : false;
			// type：1-开始时间，2-结束时间
			if ("1".equals(type)) {
				// 陪产假 也不需要小时数
				if (Integer.parseInt(leaveType) == 9) {

				} else if (standard) {
					hoursList = "09:00,12:00";
					map.put("list", hoursList);
				} else if(empClass.getMustAttnTime() < 8){
					hoursList = DateUtils.format(empClass.getStartTime(), "HH:mm");
					map.put("list", hoursList);
				}else if (empClass.getMustAttnTime() == 8) {
					hoursList = DateUtils.format(empClass.getStartTime(), "HH:mm") + ","
							+ DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 300), "HH:mm");
					map.put("list", hoursList);
				} else {
					// 非标准的
					if (empClass.getMustAttnTime() >= 10 && empClass.getMustAttnTime() < 16) {
						hoursList = DateUtils.format(empClass.getStartTime(), "HH:mm") + ","
								+ DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 240), "HH:mm") + ","
								+ DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 480), "HH:mm");
						map.put("list", hoursList);
					} else if (empClass.getMustAttnTime() >= 16) {
						hoursList = DateUtils.format(empClass.getStartTime(), "HH:mm") + ","
								+ DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 240), "HH:mm") + ","
								+ DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 480), "HH:mm") + ","
								+ DateUtils.format(DateUtils.addMinute(empClass.getStartTime(), 720), "HH:mm");
						map.put("list", hoursList);
					}
				}
			} else if ("2".equals(type)) {
				if (standard) {
					if ("5".equals(leaveType)) {
						hoursList = "13:00,18:00";
					} else {
						hoursList = "14:00,18:00";
					}
					map.put("list", hoursList);
				} else if(empClass.getMustAttnTime() < 8){
					hoursList = DateUtils.format(empClass.getEndTime(), "HH:mm");
					map.put("list", hoursList);
				}else if (empClass.getMustAttnTime() == 8) {
					hoursList = DateUtils.format(DateUtils.addMinute(empClass.getEndTime(), -300), "HH:mm") + ","
							+ DateUtils.format(empClass.getEndTime(), "HH:mm");
					map.put("list", hoursList);
				} else {
					hoursList = DateUtils.format(empClass.getEndTime(), "HH:mm");
					map.put("list", hoursList);
				}
			}
		} else {
			employeeClass.setClassDate(DateUtils.addDay(DateUtils.parse(date, DateUtils.FORMAT_SHORT), -1));
			empClass = employeeClassService.getEmployeeClassSetting(employeeClass);
			if (empClass != null && empClass.getIsInterDay() == 1 && "2".equals(type)) {
				map.put("code", "0000");
				hoursList = DateUtils.format(empClass.getEndTime(), "HH:mm");
				map.put("list", hoursList);
			} else {
				map.put("code", "1111");
				map.put("message", "今天不需要请假！");
			}
		}
		return map;
		
	}

	@Override
	public Map<String, Object> getChildren(Long employeeId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(employeeId==null) {
				map.put("childrenNum", 0);
			}
			EmpFamilyMember empFamilyMember = new EmpFamilyMember();
			empFamilyMember.setEmployeeId(employeeId);
			empFamilyMember.setRelation(1);
			// 出生日期要大于当前时间往前推一年
			empFamilyMember.setStartDate(DateUtils.addYear(new Date(), -1));
			List<EmpFamilyMember> list = empFamilyMemberService.getListByCondition(empFamilyMember);
			map.put("childrenNum", 0);
			if (list != null && list.size() > 0) {
				map.put("childrenNum", list.size());
				// “结束日期”自动对应最近一个孩子的出生日期满一年的前一天
				map.put("endTime", DateUtils.format(
						DateUtils.addDay(DateUtils.addYear(list.get(0).getBirthday(), 1), -1), DateUtils.FORMAT_SHORT));
			}
		} catch (Exception e) {
			logger.error(employeeId + "请假-获取子女数：",e);
		}
		return map;
	}

	@Override
	public Map<String, Object> getExtraMaternityLeaveDays(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		String startTime = request.getParameter("startTime");
		String days = request.getParameter("leaveDays");
		// String empId = request.getParameter("empId");
		Date startTimeFlag = DateUtils.parse(startTime, DateUtils.FORMAT_SHORT);
		Date endTimeFlag = null;
		User user = userService.getCurrentUser();
		try {
			int countLegal = 0;// 判断最后一个月 有多少天 法定节假日
								// “生育假30天”：即产假的最后30天，遇到法定节假日顺延
			// 结束时间带 最后一个月判断法定假日 开始时间 往后推 请假天数
			Date endTimeStartFlag = DateUtils.addDay(startTimeFlag, Integer.parseInt(days) - 1);
			Date endTimeStartVal = DateUtils.addDay(startTimeFlag, Integer.parseInt(days) - 1);
			// 在减30天
			Date endTimeBefore = DateUtils.addDay(endTimeStartVal, -29);
			boolean DateEndFlag = true;
			while (DateEndFlag) {
				// 计算法定
				AnnualVacation vacation = new AnnualVacation();
				vacation.setType(3);
				vacation.setStartDate(endTimeBefore);
				vacation.setEndDate(endTimeStartVal);
				List<AnnualVacation> listByCondition = annualVacationService.getListByCondition(vacation);
				if (listByCondition.size() > 0) {
					Date annualDateEnd = listByCondition.get(listByCondition.size() - 1).getAnnualDate();
					int compareDate = DateUtils.compareDate(annualDateEnd, endTimeStartVal);
					countLegal = listByCondition.size();
					DateEndFlag = false;
					if (compareDate != 2) {// 第一个时间大于等于 第二个时间
						endTimeStartVal = DateUtils.addDay(endTimeStartVal, 1);
						countLegal = listByCondition.size();
						DateEndFlag = true;
					}
				} else {
					DateEndFlag = false;
				}
			}
			endTimeFlag = DateUtils.addDay(endTimeStartFlag, countLegal);
			map.put("startTimeFlag", DateUtils.format(startTimeFlag, DateUtils.FORMAT_SHORT));
			map.put("endTimeFlag", DateUtils.format(endTimeFlag, DateUtils.FORMAT_SHORT));
 			map.put("addLegalTime", countLegal);
		} catch (Exception e) {
			logger.error(user.getEmployee().getCnName() + "请假-获取产前假信息：" + e.toString());
			map.put("startTimeFlag", DateUtils.format(startTimeFlag, DateUtils.FORMAT_SHORT));
			if (endTimeFlag == null) {
				endTimeFlag = DateUtils.addDay(startTimeFlag, Integer.parseInt(days));
			}
			map.put("endTimeFlag", DateUtils.format(endTimeFlag, DateUtils.FORMAT_SHORT));
		}
		return map;
	}
    
	/**
	 * 发送异常请假邮件
	 * @param leaveType
	 * @param employee
	 * @param leaveDays
	 * @param startTime
	 */
	private void sendAbnormalApplyEmail(Integer leaveType,User user,Double leaveDays,Date startTime,Date endTime) {
		
		//往前推30天
		Date paramDate = DateUtils.addDay(startTime, -30);
		List<Integer> leaveTypeList = new ArrayList<Integer>();
		if(ConfigConstants.LEAVE_TYPE_11.equals(leaveType)) {
			//事假连续2天及以上
			if(leaveDays>=2) {
				//发送邮件
				sendAbnormalLeaveEmail1(user,startTime,endTime,"事假",leaveDays);
			}else {
				//30天内事假2天及以上
				Map<String,Object> result = getInOneMonthLeaveDaysByTypes(paramDate,startTime,user);
				Double affairDays = result!=null?(double) result.get("affairDays"):0d;
				if(affairDays>=2) {
					//查询30天内的最小开始时间
					leaveTypeList.add(ConfigConstants.LEAVE_TYPE_11);
					Date minStartTime = empApplicationLeaveMapper.getInOneMonthMinStartTimeByTypes(user.getEmployeeId(),paramDate,startTime,leaveTypeList);
					sendAbnormalLeaveEmail2(user,minStartTime,"事假",affairDays);
				}
			}
			
		}else if(ConfigConstants.LEAVE_TYPE_2.equals(leaveType)) {
			//病假连续3天及以上
			if(leaveDays>=3) {
				//发送邮件
				sendAbnormalLeaveEmail1(user,startTime,endTime,"病假",leaveDays);
			}else {
				//30天内病假、事假、其他假期累计5天及以上
				Map<String,Object> result = getInOneMonthLeaveDaysByTypes(paramDate,startTime,user);
				Double totalDays = result!=null?(double) result.get("totalDays"):0d;
				if(totalDays>=5) {
					//查询30天内的最小开始时间
					leaveTypeList.add(ConfigConstants.LEAVE_TYPE_11);
					leaveTypeList.add(ConfigConstants.LEAVE_TYPE_2);
					leaveTypeList.add(ConfigConstants.LEAVE_TYPE_12);
					Date minStartTime = empApplicationLeaveMapper.getInOneMonthMinStartTimeByTypes(user.getEmployeeId(),paramDate,startTime,leaveTypeList);
					String leaveTypes = result!=null? (String) result.get("leaveTypes"):"";
					sendAbnormalLeaveEmail2(user,minStartTime,leaveTypes,totalDays);
				}
			}
			
		}else if(ConfigConstants.LEAVE_TYPE_12.equals(leaveType)) {
			//其它假连续2天及以上
			if(leaveDays>=2) {
				//发送邮件
				sendAbnormalLeaveEmail1(user,startTime,endTime,"其他假",leaveDays);
			}else {
				//30天内其他假期2天及以上
				Map<String,Object> result = getInOneMonthLeaveDaysByTypes(paramDate,startTime,user);
				Double otherDays = result!=null?(double) result.get("otherDays"):0d;
				if(otherDays>=2) {
					//查询30天内的最小开始时间
					leaveTypeList.add(ConfigConstants.LEAVE_TYPE_12);
					Date minStartTime = empApplicationLeaveMapper.getInOneMonthMinStartTimeByTypes(user.getEmployeeId(),paramDate,startTime,leaveTypeList);
					sendAbnormalLeaveEmail2(user,minStartTime,"其他",otherDays);
				}
			}
			
		}
			
	}
	
	private void sendAbnormalLeaveEmail1(User user,Date startTime,Date endTime,String leaveType,Double leaveDays) {
		String content = "";
		SendMail sendMail = new SendMail();
		sendMail.setReceiver("ulehr@ule.com");
		sendMail.setSubject(user.getEmployee().getCnName()+"异常休假提醒");
		sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
		sendMail.setOaMail(SendMail.OA_MAIL_P);
		content += user.getDepart().getName()+"，"+ user.getEmployee().getCode()+"，"+user.getEmployee().getCnName()+"&nbsp;在";
		content +=DateUtils.format(startTime, DateUtils.FORMAT_SHORT_CN)+"至";
		content +=DateUtils.format(endTime, DateUtils.FORMAT_SHORT_CN)+"，"+"连续申请";
		content +=leaveType+leaveDays+"天，请HR注意。";
		sendMail.setText(content);
		sendMailService.save(sendMail);
	}
	
	private void sendAbnormalLeaveEmail2(User user,Date startTime,String leaveTypes,Double leaveDays) {
		String content = "";
		SendMail sendMail = new SendMail();
		sendMail.setReceiver("ulehr@ule.com");
		sendMail.setSubject(user.getEmployee().getCnName()+"异常休假提醒");
		sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
		sendMail.setOaMail(SendMail.OA_MAIL_P);
		content += user.getDepart().getName()+"，"+ user.getEmployee().getCode()+"，"+user.getEmployee().getCnName()+"&nbsp;自";
		content +=DateUtils.format(startTime, DateUtils.FORMAT_SHORT_CN)+"起，已累计申请";
	    content += "<span style='color:blue;'>"+leaveTypes+"</span>";
		content +="假期达"+leaveDays+"天，请HR注意。";
		sendMail.setText(content);
		sendMailService.save(sendMail);
	}
	
	/**
	 * 获取一个月内请假总数
	 * @param startTime
	 * @param user
	 * @return
	 */
	private Map<String,Object> getInOneMonthLeaveDaysByTypes(Date paramDate,Date startTime,User user){
		
		Map<String,Object> result = new HashMap<String,Object>();
		//查询请假时间大于等于paramDate或者paramDate在请假时间段内的总事假天数
		List<LeaveDaysGBTypeResultDto> list = empApplicationLeaveMapper.getInOneMonthLeaveDaysByTypes(user.getEmployeeId(), paramDate, startTime);
		Double sickDays = 0d;//病假天数
		Double affairDays = 0d;//事假天数
		Double otherDays = 0d;//其他假天数
		Double totalDays = 0d;//病假，事假，其他的总天数
		String leaveTypes = "";//30天内包含的请假类型
		for(LeaveDaysGBTypeResultDto data:list) {
			totalDays += data.getLeaveDays()!=null?data.getLeaveDays():0d;
			if(ConfigConstants.LEAVE_TYPE_11.equals(data.getLeaveType())) {
				affairDays += data.getLeaveDays()!=null?data.getLeaveDays():0d;
				leaveTypes +="事假，";
			}else if(ConfigConstants.LEAVE_TYPE_2.equals(data.getLeaveType())) {
				sickDays += data.getLeaveDays()!=null?data.getLeaveDays():0d;
				leaveTypes +="病假，";
			}else if(ConfigConstants.LEAVE_TYPE_12.equals(data.getLeaveType())) {
				otherDays += data.getLeaveDays()!=null?data.getLeaveDays():0d;
				leaveTypes +="其他，";
			}
		}
		leaveTypes = StringUtils.isNotBlank(leaveTypes)?leaveTypes.substring(0,leaveTypes.length()-1):"";
		result.put("sickDays", sickDays);
		result.put("affairDays", affairDays);
		result.put("otherDays", otherDays);
		result.put("totalDays", totalDays);
		result.put("leaveTypes", leaveTypes);
		return result;
	}

}
