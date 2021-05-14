package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.common.OaCommon;
import com.ule.oa.base.mapper.ApplyEmployClassMapper;
import com.ule.oa.base.mapper.ApplyEmployDutyMapper;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmpApplicationAbnormalAttendanceMapper;
import com.ule.oa.base.mapper.EmpApplicationBusinessMapper;
import com.ule.oa.base.mapper.EmpApplicationLeaveMapper;
import com.ule.oa.base.mapper.EmpApplicationOutgoingMapper;
import com.ule.oa.base.mapper.EmpApplicationOvertimeLateMapper;
import com.ule.oa.base.mapper.EmpApplicationOvertimeMapper;
import com.ule.oa.base.mapper.EmpMsgMapper;
import com.ule.oa.base.mapper.EmpPositionMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.HiActinstMapper;
import com.ule.oa.base.mapper.ReIdentitylinkMapper;
import com.ule.oa.base.mapper.ReProcdefDetailMapper;
import com.ule.oa.base.mapper.ReProcdefMapper;
import com.ule.oa.base.mapper.RuProcdefMapper;
import com.ule.oa.base.mapper.RunTaskMapper;
import com.ule.oa.base.po.ApplyEmployClass;
import com.ule.oa.base.po.ApplyEmployDuty;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpApplicationAbnormalAttendance;
import com.ule.oa.base.po.EmpApplicationBusiness;
import com.ule.oa.base.po.EmpApplicationLeave;
import com.ule.oa.base.po.EmpApplicationOutgoing;
import com.ule.oa.base.po.EmpApplicationOvertime;
import com.ule.oa.base.po.EmpApplicationOvertimeLate;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.po.ReIdentitylink;
import com.ule.oa.base.po.ReProcdef;
import com.ule.oa.base.po.ReProcdefDetail;
import com.ule.oa.base.po.RuProcdef;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RuProcdefService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.MD5Encoder;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: RunTaskServiceImpl
  * @Description:  流程管理
  * @author mahaitao
  * @date 2017年6月1日 上午11:27:49
 */
@Service
public class RunTaskServiceImpl implements RunTaskService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RunTaskMapper runTaskMapper;
	@Autowired
	private ReProcdefMapper reProcdefMapper;
	@Autowired
	private ReProcdefDetailMapper reProcdefDetailMapper;
	@Autowired
	private HiActinstMapper hiActinstMapper;
	@Autowired
	private RuProcdefMapper ruProcdefMapper;
	@Autowired
	private RuProcdefService ruProcdefService;
	@Autowired
	private ReIdentitylinkMapper reIdentitylinkMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private DepartService departService;
	@Autowired
	private EmpPositionMapper empPositionMapper;
	@Autowired
	private SendMailService	sendMailService;
	@Autowired
	private DepartMapper departMapper;
	@Autowired
	private EmpMsgMapper empMsgMapper;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private EmpApplicationOvertimeMapper empApplicationOvertimeMapper;
	@Autowired
	private EmpApplicationAbnormalAttendanceMapper empApplicationAbnormalAttendanceMapper;
	@Autowired
	private EmpApplicationLeaveMapper empApplicationLeaveMapper;
	@Autowired
	private EmpApplicationOutgoingMapper empApplicationOutgoingMapper;
	@Autowired
	private EmpApplicationBusinessMapper empApplicationBusinessMapper;
	@Autowired
	private EmpApplicationOvertimeLateMapper empApplicationOvertimeLateMapper;
	@Autowired
	private ApplyEmployDutyMapper applyEmployDutyMapper;
	@Autowired
	private ApplyEmployClassMapper applyEmployClassMapper;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> cancel(Long ruTaskId, User user, String opinion){
		Map<String, String> returnMap = new HashMap<String, String>();
		try{
			//关闭其他人的待办给关了
			RuProcdef ruProcdef = new RuProcdef();
			ruProcdef.setRuTaskId(ruTaskId);
			ruProcdef.setDelFlag(CommonPo.STATUS_DELETE);
			ruProcdefService.updateByNodeCode(ruProcdef);
			
			//保存流程进程 
			HiActinst hiActinst = new HiActinst();
			hiActinst.setRuTaskId(ruProcdef.getRuTaskId());
			hiActinst =  hiActinstMapper.getList(hiActinst).get(0);
			hiActinst.setOpinion(opinion);
			hiActinst.setStartTime(new Date());
			hiActinst.setDelFlag(CommonPo.STATUS_NORMAL);
			hiActinst.setCreateTime(new Date());
			hiActinst.setCreateUser(user.getEmployee().getCnName());
			hiActinst.setAssigneeId(user.getEmployeeId());
			hiActinst.setAssignee(user.getEmployee().getCnName());
			hiActinst.setEndTime(new Date());
			hiActinst.setDuration(0L);
			hiActinst.setIsStart(HiActinst.IS_START_N);
			hiActinst.setStatus(HiActinst.STATUS_400);
			//删除未处理流程进程
			hiActinst.setDelFlag(CommonPo.STATUS_DELETE);
			hiActinst.setUpdateUser(user.getEmployee().getCnName());
			hiActinstMapper.refuseByRunId(hiActinst);
			hiActinst.setIsStart(HiActinst.IS_START_Y);
			hiActinst.setDelFlag(CommonPo.STATUS_NORMAL);
			hiActinstMapper.save(hiActinst);
			//修改流程状态为已撤回
			RunTask runTask = new RunTask();
			runTask.setId(ruTaskId);
			runTask.setProcessStatus(RunTask.PROCESS_STATUS_400);
			runTask.setUpdateUser(user.getEmployee().getCnName());
			runTaskMapper.updateById(runTask);
			returnMap.put("code", OaCommon.CODE_OK);
			returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_OK));
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"撤销流程：" + e.toString());
			returnMap.put("code", OaCommon.CODE_ERROR);
			returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_ERROR));
			return returnMap;
		}
		return returnMap;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> refuse(Long ruProcdefId, User user, String opinion){
		Map<String, String> returnMap = new HashMap<String, String>();
		//更新流程为已完成
		try{
			RuProcdef ruProcdef = ruProcdefService.getById(ruProcdefId);
			if(!ruProcdef.getProcessStatus().equals(RuProcdef.PROCESS_STATUS_100)) {
				returnMap.put("code", OaCommon.CODE_ERROR);
				returnMap.put("msg", "节点状态不是待审批，请确认操作");
				return returnMap;
			}
			ruProcdef.setUpdateUser(user.getEmployee().getCnName());
			ruProcdef.setProcessStatus(RuProcdef.PROCESS_STATUS_300);
			ruProcdefService.updateById(ruProcdef);
			
			//关闭其他人的待办给关了
			ruProcdef.setDelFlag(CommonPo.STATUS_DELETE);
			ruProcdef.setNodeCode(null);
			ruProcdefService.updateByNodeCode(ruProcdef);
			
			//保存流程进程 
			HiActinst hiActinst = new HiActinst();
			hiActinst.setRuTaskId(ruProcdef.getRuTaskId());
			hiActinst.setNodeCode(ruProcdef.getNodeCode());
			hiActinst.setOpinion(opinion);
			if(!ruProcdef.getNodeType().equals(RuProcdef.NODE_TYPE_P)) {
				hiActinst.setAssigneeId(user.getEmployeeId());
			}
			saveHiActinst(user, hiActinst,HiActinst.STATUS_300, opinion);
			
			//删除未处理流程进程
			hiActinst.setUpdateUser(user.getEmployee().getCnName());
			hiActinstMapper.refuseByRunId(hiActinst);
			/*//删除待办消息
			EmpMsg empMsg = new EmpMsg();
			empMsg.setDelFlag(CommonPo.STATUS_DELETE);
			empMsg.setRuTaskId(ruProcdef.getRuTaskId());
			empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
			empMsgMapper.delRuTaskIdORNodeCode(empMsg);*/
			//修改流程状态为已拒绝
			RunTask runTask = new RunTask();
			runTask.setId(ruProcdef.getRuTaskId());
			runTask.setProcessStatus(RunTask.PROCESS_STATUS_300);
			runTask.setUpdateUser(user.getEmployee().getCnName());
			runTaskMapper.updateById(runTask);
			returnMap.put("code", OaCommon.CODE_OK);
			returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_OK));
			
			runTask = runTaskMapper.getById(ruProcdef.getRuTaskId());
			EmpMsg empMsg = new EmpMsg();
			empMsg.setDelFlag(CommonPo.STATUS_NORMAL);
			empMsg.setType(EmpMsg.type_200);
			empMsg.setCompanyId(user.getCompanyId());
			empMsg.setEmployeeId(runTask.getCreatorId());
			empMsg.setRuTaskId(ruProcdef.getRuTaskId());
			empMsg.setNodeCode(ReProcdefDetail.NODE_CODE_END);
			empMsg.setTitle("审批拒绝通知");
			empMsg.setContent("很遗憾，您的【"+runTask.getProcessName()+"】审批已被拒绝！");
			empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
			empMsg.setCreateTime(new Date());
			empMsgMapper.save(empMsg);
			
			Employee employee = employeeMapper.getById(runTask.getCreatorId());
			if(employee.getEmail() != null && !employee.getEmail().equals("")) {
				List<SendMail> sendMailList = new ArrayList<SendMail>();
				SendMail sendMail = new SendMail();
				sendMail.setReceiver(employee.getEmail());
				sendMail.setSubject("审批拒绝通知");
				sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
				sendMail.setText("很遗憾，您的【"+runTask.getProcessName()+"】审批已被拒绝！");
				sendMail.setOaMail(SendMail.OA_MAIL_P);
				sendMailList.add(sendMail);
				sendMailService.batchSave(sendMailList);	
			}
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"拒绝流程：" + e.toString());
			returnMap.put("code", OaCommon.CODE_ERROR);
			returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_ERROR));
			return returnMap;
		}
		return returnMap;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> startFlow(String code, User user, String entityId){
		return startFlow(code, user, entityId, null,true);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> startFlow(String code, User user, String entityId, String nextNodeCode){
		return startFlow(code, user, entityId, nextNodeCode,true);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> timingStartFlow(String code, Employee employee, String entityId){
		Map<String, String> returnMap = new HashMap<String, String>();
		try{
			//1:拿到流程
			ReProcdef reProcdef = reProcdefMapper.getByCode(code);
			ReProcdefDetail reProcdefDetail = new ReProcdefDetail();
			reProcdefDetail.setReProcdefCode(code);
			reProcdefDetail.setNodeCode(ReProcdefDetail.NODE_CODE_START);
			//获取启动节点
			ReProcdefDetail  reProcdefDetailStart = reProcdefDetailMapper.getList(reProcdefDetail).get(0);
			//启动流程
			RunTask runTask = new RunTask();
			runTask.setProcessType(reProcdef.getProcessType());
			runTask.setReProcdefCode(reProcdef.getCode());
			runTask.setProcessName(reProcdef.getProcessName());
			runTask.setCreatorId(employee.getId());
			runTask.setCreator(employee.getCnName());
			runTask.setStartTime(new Date());
			runTask.setNodeModule(reProcdefDetailStart.getNodeModule());
			runTask.setNodeModuleName(reProcdefDetailStart.getNodeModuleName());
			runTask.setNodeModuleType(reProcdefDetailStart.getNodeModuleType());
			runTask.setRedirectUrl(reProcdefDetailStart.getRedirectUrl());
			runTask.setDelFlag(CommonPo.STATUS_NORMAL);
			runTask.setCreateTime(new Date());
			runTask.setCreateUser(employee.getCnName());
			runTask.setEntityId(entityId);
			runTask.setProcessStatus(RunTask.PROCESS_STATUS_100);
			//获取启动流程Id
			runTaskMapper.save(runTask);
			//处理任务流程日志
			HiActinst hiActinst = new HiActinst();
			hiActinst.setReProcdefCode(code);
			hiActinst.setRuTaskId(runTask.getId());
			hiActinst.setNodeModuleType(reProcdefDetailStart.getNodeModuleType());
			hiActinst.setNodeModule(reProcdefDetailStart.getNodeModule());
			hiActinst.setNodeModuleName(reProcdefDetailStart.getNodeModuleName());
			hiActinst.setNodeCode(reProcdefDetailStart.getNodeCode());
			hiActinst.setNodeName(reProcdefDetailStart.getNodeName());
			hiActinst.setNodeType(reProcdefDetailStart.getNodeType());
			hiActinst.setStartTime(new Date());
			hiActinst.setDelFlag(CommonPo.STATUS_NORMAL);
			hiActinst.setCreateTime(new Date());
			hiActinst.setCreateUser(employee.getCnName());
			hiActinst.setAssigneeId(employee.getId());
			hiActinst.setAssignee(employee.getCnName());
			hiActinst.setDuration(0L);
			hiActinst.setIsStart(HiActinst.IS_START_N);
			hiActinst.setStatus(HiActinst.STATUS_100);
			hiActinstMapper.save(hiActinst);
			
			RuProcdef ruProcdef = new RuProcdef();
			ruProcdef.setRuTaskId(runTask.getId());
			ruProcdef.setProcessType(reProcdef.getProcessType());
			ruProcdef.setProcessName(reProcdef.getProcessName());
			ruProcdef.setProcessStatus(RuProcdef.PROCESS_STATUS_100);
			ruProcdef.setRedirectUrl(reProcdef.getRedirectUrl());
			ruProcdef.setStartTime(new Date());
			ruProcdef.setReProcdefCode(reProcdefDetailStart.getReProcdefCode());
			ruProcdef.setNodeCode(reProcdefDetailStart.getNodeCode());
			ruProcdef.setNodeName(reProcdefDetailStart.getNodeName());
			ruProcdef.setNodeModule(reProcdefDetailStart.getNodeModule());
			ruProcdef.setNodeModuleName(reProcdefDetailStart.getNodeModuleName());
			ruProcdef.setNodeModuleType(reProcdefDetailStart.getNodeModuleType());
			ruProcdef.setNodeType(reProcdefDetailStart.getNodeType());
			ruProcdef.setDelFlag(CommonPo.STATUS_NORMAL);
			ruProcdef.setCreateTime(new Date());
			ruProcdef.setCreateUser(employee.getCnName());
			ruProcdef.setAssignee(employee.getCnName());
			ruProcdef.setAssigneeId(employee.getId());
			ruProcdef.setEntityId(entityId);
			ruProcdefMapper.save(ruProcdef);
			returnMap.put("code", OaCommon.CODE_OK);
			returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_OK));
		}catch(Exception e){
			logger.error(employee.getCnName()+"定时启动流程：" + e.toString());
			returnMap.put("code", OaCommon.CODE_ERROR);
			returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_ERROR));
			return returnMap;
		}
		return returnMap;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> startFlow(String code, User user, String entityId, String nextNodeCode, Boolean isAgent){
		Map<String, String> returnMap = new HashMap<String, String>();
		try{
			//1:拿到流程
			ReProcdef reProcdef = reProcdefMapper.getByCode(code);
			ReProcdefDetail reProcdefDetail = new ReProcdefDetail();
			reProcdefDetail.setReProcdefCode(code);
			reProcdefDetail.setNodeCode(ReProcdefDetail.NODE_CODE_START);
			//获取启动节点
			ReProcdefDetail  reProcdefDetailStart = reProcdefDetailMapper.getList(reProcdefDetail).get(0);
			//启动流程
			RunTask runTask = new RunTask();
			runTask.setProcessType(reProcdef.getProcessType());
			runTask.setReProcdefCode(reProcdef.getCode());
			runTask.setProcessName(reProcdef.getProcessName());
			runTask.setCreatorId(user.getEmployeeId());
			runTask.setCreator(user.getEmployee().getCnName());
			runTask.setStartTime(new Date());
			runTask.setNodeModule(reProcdefDetailStart.getNodeModule());
			runTask.setNodeModuleName(reProcdefDetailStart.getNodeModuleName());
			runTask.setNodeModuleType(reProcdefDetailStart.getNodeModuleType());
			runTask.setRedirectUrl(reProcdefDetailStart.getRedirectUrl());
			runTask.setDelFlag(CommonPo.STATUS_NORMAL);
			runTask.setCreateTime(new Date());
			runTask.setCreateUser(user.getEmployee().getCnName());
			runTask.setEntityId(entityId);
			runTask.setProcessStatus(RunTask.PROCESS_STATUS_100);
			//获取启动流程Id
			runTaskMapper.save(runTask);
			//处理任务流程日志
			HiActinst hiActinst = new HiActinst();
			hiActinst.setReProcdefCode(code);
			hiActinst.setRuTaskId(runTask.getId());
			hiActinst.setNodeModuleType(reProcdefDetailStart.getNodeModuleType());
			hiActinst.setNodeModule(reProcdefDetailStart.getNodeModule());
			hiActinst.setNodeModuleName(reProcdefDetailStart.getNodeModuleName());
			hiActinst.setNodeCode(reProcdefDetailStart.getNodeCode());
			hiActinst.setNodeName(reProcdefDetailStart.getNodeName());
			hiActinst.setNodeType(reProcdefDetailStart.getNodeType());
			hiActinst.setStartTime(new Date());
			hiActinst.setDelFlag(CommonPo.STATUS_NORMAL);
			hiActinst.setCreateTime(new Date());
			hiActinst.setCreateUser(user.getEmployee().getCnName());
			hiActinst.setAssigneeId(user.getEmployeeId());
			hiActinst.setAssignee(user.getEmployee().getCnName());
			hiActinst.setEndTime(new Date());
			hiActinst.setDuration(0L);
			hiActinst.setIsStart(HiActinst.IS_START_Y);
			hiActinst.setStatus(HiActinst.STATUS_500);
			hiActinstMapper.save(hiActinst);
			if(nextNodeCode == null) {
				nextNodeCode = reProcdefDetailStart.getNextNodeCode();
			}
			returnMap = nextFlow(code,reProcdef.getProcessType(),reProcdef.getProcessName(), runTask, nextNodeCode, user, "",entityId,null,isAgent);
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"启动流程：" + e.toString());
			returnMap.put("code", OaCommon.CODE_ERROR);
			returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_ERROR));
			return returnMap;
		}
		return returnMap;
	}

	
	@Override
	public Map<String, String> nextFlow(Long ruProcdefId, User user, String opinion, String entityId,String nextCodeValue, Boolean isAgent){
		return nextFlow(ruProcdefId, user, null, opinion, entityId,nextCodeValue);
	}
	
	
	@Override
	public Map<String, String> nextFlow(Long ruProcdefId, User user,Map<Long, String> empIdMap, String opinion, String entityId,String nextCodeValue, Boolean isAgent){
		Map<String, String> returnMap = new HashMap<String, String>();
		try{
			//更新流程为已完成
			RuProcdef ruProcdef = ruProcdefService.getById(ruProcdefId);
			
			if(!ruProcdef.getProcessStatus().equals(RuProcdef.PROCESS_STATUS_100)) {
				returnMap.put("code", OaCommon.CODE_ERROR);
				returnMap.put("msg", "节点状态不是待审批，请确认操作");
				return returnMap;
			}
			
			ruProcdef.setUpdateUser(user.getEmployee().getCnName());
			ruProcdef.setProcessStatus(RuProcdef.PROCESS_STATUS_200);
			ruProcdefService.updateById(ruProcdef);
			//判断当前节点是否会签
			HiActinst hiActinst = new HiActinst();
			hiActinst.setRuTaskId(ruProcdef.getRuTaskId());
			hiActinst.setNodeCode(ruProcdef.getNodeCode());
			if(ruProcdef.getNodeType().longValue() == RuProcdef.NODE_TYPE_H.longValue()) {
				hiActinst.setAssigneeId(user.getEmployeeId());
				Integer nodeCodeCount = ruProcdefService.getNodeCodeCount(ruProcdef);
				if(nodeCodeCount > 0) {
					saveHiActinst(user, hiActinst, HiActinst.STATUS_200, opinion);
					returnMap.put("code", OaCommon.CODE_OK);
					returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_OK));
					return returnMap;
				} 
			} else if(ruProcdef.getNodeType().longValue() == RuProcdef.NODE_TYPE_P.longValue()) {
				//普通节点其他人的待办给关了
				ruProcdef.setDelFlag(CommonPo.STATUS_DELETE);
				ruProcdefService.updateByNodeCode(ruProcdef);
				/*EmpMsg empMsg = new EmpMsg();
				empMsg.setDelFlag(CommonPo.STATUS_DELETE);
				empMsg.setRuTaskId(ruProcdef.getRuTaskId());
				empMsg.setNodeCode(ruProcdef.getNodeCode());
				empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
				empMsgMapper.delRuTaskIdORNodeCode(empMsg);*/
			} else {
				hiActinst.setAssigneeId(user.getEmployeeId());
			}
			//获取流程进程
			saveHiActinst(user, hiActinst, HiActinst.STATUS_200, opinion);
			
			//获取下一个节点CODE
			ReProcdefDetail reProcdefDetail = new ReProcdefDetail();
			reProcdefDetail.setReProcdefCode(ruProcdef.getReProcdefCode());
			reProcdefDetail.setNodeCode(ruProcdef.getNodeCode());
			if(nextCodeValue == null || nextCodeValue.equals("")) {
				ReProcdefDetail  nextReProcdefDetail = reProcdefDetailMapper.getList(reProcdefDetail).get(0);
				nextCodeValue = nextReProcdefDetail.getNextNodeCode();
			}
			//先判断当前节点模块与下一个节点模块是否相同
			String[] nextNodes = nextCodeValue.split(",");
			ReProcdefDetail nextNodeDetail = new ReProcdefDetail();
			nextNodeDetail.setReProcdefCode(ruProcdef.getReProcdefCode());
			nextNodeDetail.setNodeCode(nextNodes[0]);
			ReProcdefDetail  nextNodeModuleValue = reProcdefDetailMapper.getList(nextNodeDetail).get(0);
			if(!nextNodeModuleValue.getNodeModule().equals(ruProcdef.getNodeModule())) {
				//判断当前节点模块是否会签
				if(ruProcdef.getNodeModuleType().longValue()== RuProcdef.NODE_MODULE_TYPE_H.longValue()) {
					ruProcdef.setProcessStatus(RuProcdef.PROCESS_STATUS_100);
					Integer nodeModuleCount = ruProcdefService.getNodeModuleCount(ruProcdef);
					if(nodeModuleCount > 0) {
						returnMap.put("code", OaCommon.CODE_OK);
						returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_OK));
						return returnMap;
					}
				}
			}
			//判断下一个节点是否结束状态
			RunTask runTask = runTaskMapper.getById(ruProcdef.getRuTaskId());
			if(nextCodeValue.equals(ReProcdefDetail.NODE_CODE_END)) {
				runTask.setProcessStatus(RunTask.PROCESS_STATUS_200);
				runTask.setUpdateUser(user.getEmployee().getCnName());
				runTaskMapper.updateById(runTask);
				
				EmpMsg empMsg = new EmpMsg();
				empMsg.setDelFlag(CommonPo.STATUS_NORMAL);
				empMsg.setType(EmpMsg.type_200);
				empMsg.setCompanyId(user.getCompanyId());
				empMsg.setEmployeeId(runTask.getCreatorId());
				empMsg.setRuTaskId(runTask.getId());
				empMsg.setNodeCode(ReProcdefDetail.NODE_CODE_END);
				empMsg.setTitle("审批通过通知");
				empMsg.setContent("您的【"+runTask.getProcessName()+"】审批已完成了哦！");
				empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
				empMsg.setCreateTime(new Date());
				empMsgMapper.save(empMsg);
				Employee employee = employeeMapper.getById(runTask.getCreatorId());
				if(employee.getEmail() != null && !employee.getEmail().equals("")) {
					List<SendMail> sendMailList = new ArrayList<SendMail>();
					SendMail sendMail = new SendMail();
					sendMail.setReceiver(employee.getEmail());
					sendMail.setSubject("审批通过通知");
					sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
					sendMail.setText("您的【"+runTask.getProcessName()+"】审批已完成了哦！");
					sendMail.setOaMail(SendMail.OA_MAIL_P);
					sendMailList.add(sendMail);
					sendMailService.batchSave(sendMailList);	
				}
				returnMap.put("code", OaCommon.CODE_OK);
				returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_OK));
			} else {
				//执行下个节点
				returnMap = nextFlow(ruProcdef.getReProcdefCode(),ruProcdef.getProcessType(),ruProcdef.getProcessName(), runTask, nextCodeValue, user, opinion,entityId,empIdMap,isAgent);
			}
		}catch(Exception e){
			logger.error(user.getEmployee().getCnName()+"启动下一个流程：" + e.toString());
			returnMap.put("code", OaCommon.CODE_ERROR);
			returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_ERROR));
			return returnMap;
		}
		return returnMap;
	}

	/**
	 * 更新流程日志
	 * @param user
	 * @param hiActinst
	 */
	public void saveHiActinst(User user, HiActinst hiActinst, Long status, String opinion) {
		hiActinst =  hiActinstMapper.getList(hiActinst).get(0);
		//更新流程进程状态
		hiActinst.setAssigneeId(user.getEmployeeId());
		hiActinst.setAssignee(user.getEmployee().getCnName());
		hiActinst.setIsStart(HiActinst.IS_START_Y);
		hiActinst.setUpdateUser(user.getEmployee().getCnName());
		hiActinst.setStatus(status);
		hiActinst.setEndTime(new Date());
		hiActinst.setOpinion(opinion);
		hiActinst.setDuration(hiActinst.getEndTime().getTime() - hiActinst.getStartTime().getTime());
		hiActinstMapper.updateById(hiActinst);
	}
	
	@Override
	public Map<String, String> nextFlow(String reProcdefCode,Long processType, String processName, RunTask runTask, String nextNode, User nodeUser, String opinion, String entityId,Map<Long, String> empIdMap, Boolean isAgent){
		Map<String, String> returnMap = new HashMap<String, String>();
		try{
			List<String> nodeCodes = new ArrayList<String>();
			String[] nextNodes = nextNode.split(",");
			for (String string : nextNodes) {
				nodeCodes.add(string);
			}
			ReProcdefDetail reProcdefDetail = new ReProcdefDetail();
			reProcdefDetail.setReProcdefCode(reProcdefCode);
			reProcdefDetail.setNodeCodes(nodeCodes);
			List<ReProcdefDetail>  reProcdefDetailList = reProcdefDetailMapper.getList(reProcdefDetail);
			//处理任务流程日志
			List<HiActinst> hiActinstList = new ArrayList<HiActinst>();
			List<RuProcdef> ruProcdefList = new ArrayList<RuProcdef>();
//			List<EmpMsg> empmsgList = new ArrayList<EmpMsg>();
			Map<Long,SendMail> senMailMap = new HashMap<Long, SendMail>();
			for (ReProcdefDetail reProcdefDetail2 : reProcdefDetailList) {
				List<Employee> employees = new ArrayList<Employee>();
				if(reProcdefDetail2.getNodeType().longValue() == RuProcdef.NODE_TYPE_DE.longValue()) {
					List<Long> employeeIds = new ArrayList<Long>(); 
					for (Map.Entry<Long, String> entry : empIdMap.entrySet()) {
						employeeIds.add(entry.getKey());
					}
					Employee employeeIdsUser = new Employee();
					employeeIdsUser.setIds(employeeIds);
					employees = employeeService.getListByCondition(employeeIdsUser);
				} else {
					employees = getUsers(runTask, reProcdefCode, reProcdefDetail2.getNodeCode(),isAgent);
				}
				if(reProcdefDetail2.getNodeType().longValue() == RuProcdef.NODE_TYPE_H.longValue() || employees.size() == 1 || reProcdefDetail2.getNodeType().longValue() == RuProcdef.NODE_TYPE_DE.longValue()) {
					//流程操作记录表：节点是会签和签收人只有一人时待办人直接插入进去,
					for (Employee employee : employees) {
						HiActinst hiActinst = new HiActinst();
						hiActinst.setOpinion("");
						hiActinst.setReProcdefCode(reProcdefCode);
						hiActinst.setRuTaskId(runTask.getId());
						hiActinst.setNodeModuleType(reProcdefDetail2.getNodeModuleType());
						hiActinst.setNodeModule(reProcdefDetail2.getNodeModule());
						hiActinst.setNodeModuleName(reProcdefDetail2.getNodeModuleName());
						hiActinst.setNodeCode(reProcdefDetail2.getNodeCode());
						hiActinst.setNodeName(reProcdefDetail2.getNodeName());
						hiActinst.setNodeType(reProcdefDetail2.getNodeType());
						hiActinst.setStartTime(new Date());
						hiActinst.setDelFlag(CommonPo.STATUS_NORMAL);
						hiActinst.setCreateTime(new Date());
						hiActinst.setCreateUser(nodeUser.getEmployee().getCnName());
						hiActinst.setIsStart(HiActinst.IS_START_N);
						hiActinst.setAssignee(employee.getCnName());
						hiActinst.setAssigneeId(employee.getId());
						//如果当前节点执行人只有一人或者是会签时需要设置上去
						hiActinstList.add(hiActinst);
					}
				} else {
					HiActinst hiActinst = new HiActinst();
					hiActinst.setOpinion("");
					hiActinst.setReProcdefCode(reProcdefCode);
					hiActinst.setRuTaskId(runTask.getId());
					hiActinst.setNodeModuleType(reProcdefDetail2.getNodeModuleType());
					hiActinst.setNodeModuleName(reProcdefDetail2.getNodeModuleName());
					hiActinst.setNodeModule(reProcdefDetail2.getNodeModule());
					hiActinst.setNodeCode(reProcdefDetail2.getNodeCode());
					hiActinst.setNodeName(reProcdefDetail2.getNodeName());
					hiActinst.setNodeType(reProcdefDetail2.getNodeType());
					hiActinst.setStartTime(new Date());
					hiActinst.setDelFlag(CommonPo.STATUS_NORMAL);
					hiActinst.setCreateTime(new Date());
					hiActinst.setCreateUser(nodeUser.getEmployee().getCnName());
					hiActinst.setIsStart(HiActinst.IS_START_N);
					//如果当前节点执行人只有一人或者是会签时需要设置上去
					hiActinstList.add(hiActinst);
				}
				Depart depart = departMapper.getByEmpId(runTask.getCreatorId());
				//设置待办事项表
				for (Employee employee : employees) {
					RuProcdef ruProcdef = new RuProcdef();
					ruProcdef.setRuTaskId(runTask.getId());
					ruProcdef.setProcessType(processType);
					ruProcdef.setProcessName(processName);
					ruProcdef.setProcessStatus(RuProcdef.PROCESS_STATUS_100);
					ruProcdef.setRedirectUrl(reProcdefDetail2.getRedirectUrl());
					ruProcdef.setStartTime(new Date());
					ruProcdef.setReProcdefCode(reProcdefDetail2.getReProcdefCode());
					ruProcdef.setNodeCode(reProcdefDetail2.getNodeCode());
					ruProcdef.setNodeName(reProcdefDetail2.getNodeName());
					ruProcdef.setNodeModule(reProcdefDetail2.getNodeModule());
					ruProcdef.setNodeModuleName(reProcdefDetail2.getNodeModuleName());
					ruProcdef.setNodeModuleType(reProcdefDetail2.getNodeModuleType());
					ruProcdef.setNodeType(reProcdefDetail2.getNodeType());
					ruProcdef.setDelFlag(CommonPo.STATUS_NORMAL);
					ruProcdef.setCreateTime(new Date());
					ruProcdef.setCreateUser(nodeUser.getEmployee().getCnName());
					ruProcdef.setAssignee(employee.getCnName());
					ruProcdef.setAssigneeId(employee.getId());
					if(reProcdefDetail2.getNodeType().longValue() == RuProcdef.NODE_TYPE_DE.longValue()) {
						//单独任务执行人需要传递
						ruProcdef.setEntityId(empIdMap.get(employee.getId()));
					} else {
						ruProcdef.setEntityId(entityId);
					}
					ruProcdefList.add(ruProcdef);
					/*//消息
					EmpMsg empMsg = new EmpMsg();
					empMsg.setDelFlag(CommonPo.STATUS_NORMAL);
					empMsg.setType(EmpMsg.type_200);
					empMsg.setCompanyId(employee.getCompanyId());
					empMsg.setEmployeeId(employee.getId());
					empMsg.setRuTaskId(runTask.getId());
					empMsg.setNodeCode(reProcdefDetail2.getNodeCode());
					Depart depart = departService.getInfoById(employee.getId());
					empMsg.setTitle(RunTask.PROCESS_TYPE_MAP.get(runTask.getProcessType()) + "-" + runTask.getProcessName() + "-" + depart.getName() + "-" + employee.getCnName());
					empMsg.setContent(RunTask.PROCESS_TYPE_MAP.get(runTask.getProcessType()) + "-" + runTask.getProcessName() + "-" + depart.getName() + "-" + employee.getCnName());
					empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
					empMsg.setCreateTime(new Date());
					empmsgList.add(empMsg);*/
					if(employee.getEmail() != null) {
						String OA_URL = configCacheManager.getConfigDisplayCode("MO_WEB_URL");
						SendMail sendMail = new SendMail();
						sendMail.setReceiver(employee.getEmail());
						
						StringBuffer sb = new StringBuffer();
//						sb.append(RunTask.PROCESS_TYPE_MAP.get(runTask.getProcessType()))
//						.append("-")
						sb.append(runTask.getProcessName() + "-")
						.append(depart.getLeaderDeptName() != null ? depart.getLeaderDeptName() + "-" + depart.getName() : "" + depart.getName())
						.append("-" + runTask.getCreator());
						sendMail.setSubject(sb.toString());
						sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
//						sendMail.setText(ConfigConstants.OA_URL + "/runTask/handle.htm?value="+MD5Encoder.encode(employee.getEmail())+"&callbackMail="+employee.getEmail()+"&ruProcdefId=");
						sendMail.setText(OA_URL + "/runTask/handle.htm?value="+MD5Encoder.md5Hex(employee.getEmail())+"&ruProcdefId=");
//						sendMail.setText(ConfigConstants.OA_URL + "/runTask/handle.htm?ruProcdefId=");
						sendMail.setOaMail(SendMail.OA_MAIL_OA);
						senMailMap.put(employee.getId(), sendMail);
					}
				}
			}
			if(hiActinstList.size() > 0) {
				hiActinstMapper.batchSave(hiActinstList);
			}
			List<SendMail> sendMailList = new ArrayList<SendMail>();
			//增加待办事项
			if(ruProcdefList.size() > 0) {
//				ruProcdefMapper.batchSave(ruProcdefList);
				for (RuProcdef ruProcdef : ruProcdefList) {
					ruProcdefMapper.save(ruProcdef);
					if(senMailMap.containsKey(ruProcdef.getAssigneeId())) {
						SendMail sm = senMailMap.get(ruProcdef.getAssigneeId());
						sm.setText(sm.getText() + ruProcdef.getId());
						sendMailList.add(sm);
					}
				}
			}
			if(sendMailList.size() > 0) {
				sendMailService.batchSave(sendMailList);	
			}
		}catch(Exception e){
			logger.error(nodeUser.getEmployee().getCnName()+"启动下一个流程：" + e.toString());
			returnMap.put("code", OaCommon.CODE_ERROR);
			returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_ERROR));
			return returnMap;
		}
		returnMap.put("code", OaCommon.CODE_OK);
		returnMap.put("msg", OaCommon.getCodeMap().get(OaCommon.CODE_OK));
		return returnMap;
	}
	
	public List<Employee> getUsers(RunTask runTask, String reProcdefCode, String nodeCode, Boolean isAgent){
		//1:拿到流程
		ReProcdef reProcdef = reProcdefMapper.getByCode(runTask.getReProcdefCode());
		
		ReIdentitylink reIdentitylink = new ReIdentitylink();
		reIdentitylink.setReProcdefCode(reProcdefCode);
		reIdentitylink.setNodeCode(nodeCode);
		List<ReIdentitylink> list = reIdentitylinkMapper.getList(reIdentitylink);
		List<Long> employeeLong = new ArrayList<Long>();
		//不使用代理
		List<Long> otherLong = new ArrayList<Long>();
		Depart depart = departService.getInfoByEmpId(runTask.getCreatorId());
		for (ReIdentitylink reIdentitylink2 : list) {
			if(reIdentitylink2.getType().toUpperCase().equals(ReIdentitylink.TYPE_USER)) {
				employeeLong.add(reIdentitylink2.getEmployeeId());
			} else if(reIdentitylink2.getType().toUpperCase().equals(ReIdentitylink.TYPE_DH)) {
				if(reProcdef.getApprovalType().longValue() == ReProcdef.APPROVAL_TYPE_100){
					if(String.valueOf(runTask.getCreatorId()).equals(depart.getLeaderId())){
						employeeLong.add(Long.valueOf(depart.getManagerId()));
					}else{
						otherLong.add(Long.valueOf(depart.getLeaderId()));
					}
				}else{
					employeeLong.add(Long.valueOf(depart.getManagerId()));
				}
			} else if(reIdentitylink2.getType().toUpperCase().equals(ReIdentitylink.TYPE_POSITION)) {
				EmpPosition config = new EmpPosition();
				config.setPositionId(reIdentitylink2.getGroupId());
				List<EmpPosition> empPosList = empPositionMapper.getListByCondition(config);
				for (EmpPosition empPosition : empPosList) {
					employeeLong.add(empPosition.getEmployeeId());
				}
			} else if(reIdentitylink2.getType().toUpperCase().equals(ReIdentitylink.TYPE_LEADER)) {
				employeeLong.add(Long.valueOf(depart.getManagerId()));
			} else if(reIdentitylink2.getType().toUpperCase().equals(ReIdentitylink.TYPE_SELF)) {
				otherLong.add(runTask.getCreatorId());
			}
		}
		Employee employee = new Employee();
		List<Employee> employeeList  = new ArrayList<Employee>();
		List<Long> newEmployeelong = new ArrayList<Long>();
		//代理人
		Map<Long, String> authMaps = new HashMap<Long, String>();

		for (Long long1 : employeeLong) {
			if(authMaps.containsKey(long1)) {
				newEmployeelong.add(Long.valueOf(depart.getManagerId()));
			} else {
				newEmployeelong.add(long1);
			}
		}
		for (Long long1 : otherLong) {
			newEmployeelong.add(long1);
		}
		if(newEmployeelong!=null&&newEmployeelong.size()>0){
			employee.setIds(newEmployeelong);
			employeeList = employeeService.getListByCondition(employee);
		}
		return employeeList;
	}
	
	@Override
	public RunTask getById(Long id) {
		return runTaskMapper.getById(id);
	}

	@Override
	public List<RunTask> getPageList(RunTask runTask) {
		return runTaskMapper.getPageList(runTask);
	}

	@Override
	public Integer getCount(RunTask runTask) {
		return runTaskMapper.getCount(runTask);
	}

	@Override
	public Long save(RunTask runTask) {
		return runTaskMapper.save(runTask);
	}

	@Override
	public void batchSave(RunTask runTask) {
		runTaskMapper.batchSave(runTask);		
	}

	@Override
	public Integer updateById(RunTask runTask) {
		return runTaskMapper.updateById(runTask);
	}
	
	@Override
	public PageModel<RunTask> getByPagenation(RunTask runTask) {
		int page = runTask.getPage() == null ? 0 : runTask.getPage();
		int rows = runTask.getRows() == null ? 0 : runTask.getRows();
		
		PageModel<RunTask> pm = new PageModel<RunTask>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		int total = runTaskMapper.getCount(runTask);
		pm.setTotal(total);
		runTask.setOffset(pm.getOffset());
		runTask.setLimit(pm.getLimit());
		List<RunTask> runTaskList = runTaskMapper.getPageList(runTask);
		getRunTaskList(runTaskList);
		pm.setRows(runTaskList);
		return pm;
	}

	@Override
	public PageModel<RunTask> getApplyByPagenation(RunTask runTask) {
		int page = runTask.getPage() == null ? 0 : runTask.getPage();
		int rows = runTask.getRows() == null ? 0 : runTask.getRows();
		
		PageModel<RunTask> pm = new PageModel<RunTask>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		int total = runTaskMapper.getApplyCount(runTask);
		pm.setTotal(total);
		if(runTask.getOffset() == null) {
			runTask.setOffset(pm.getOffset());
		}
		if(runTask.getLimit() == null) {
			runTask.setLimit(pm.getOffset());
		}
		List<RunTask> runTaskList = runTaskMapper.getApplyPageList(runTask);
		getRunTaskList(runTaskList);
		pm.setRows(runTaskList);
		return pm;
	}

	@Override
	public Map<String, String> nextFlow(Long ruProcdefId, User user,
			Map<Long, String> empIdMap, String opinion, String entityId,String nextCodeValue){
		return nextFlow(ruProcdefId, user, empIdMap, opinion, entityId,nextCodeValue,true);
	}

	@Override
	public Map<String, String> nextFlow(Long ruProcdefId, User user,
			String opinion, String entityId){
		return nextFlow(ruProcdefId, user, opinion, entityId,null,true);
	}

	@Override
	public Map<String, String> nextFlow(String reProcdefId, Long processType,
			String processName, RunTask runTask, String nodeCode, User user,
			String opinion, String entityId, Map<Long, String> empIdMap){
		return nextFlow(reProcdefId, processType, processName, runTask, nodeCode, user, opinion, entityId, empIdMap, true);
	}

	@Override
	public RunTask getRunTask(RunTask runTask) {
		List<RunTask> runTasks = runTaskMapper.getRunTask(runTask);
		return runTasks.size()>0?runTasks.get(0):null;
	}

	@Override
	public PageModel<RunTask> allExaminePageList(RunTask runTask) {
		int page = runTask.getPage() == null ? 0 : runTask.getPage();
		int rows = runTask.getRows() == null ? 0 : runTask.getRows();
		PageModel<RunTask> pm = new PageModel<RunTask>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		List<RunTask> runTaskList = runTaskMapper.allExaminePageList(runTask);
		getRunTaskList(runTaskList);
		pm.setRows(runTaskList);
		return pm;
	}
	
	public void getRunTaskList(List<RunTask> runTaskList) {
		Map<Long, Depart> empMap = new HashMap<Long, Depart>();
		for (RunTask runTask2 : runTaskList) {
			Depart depart = new Depart();
			if(empMap.containsKey(runTask2.getCreatorId())) {
				depart = empMap.get(runTask2.getCreatorId());
			} else {
				depart = departMapper.getByEmpId(runTask2.getCreatorId());
				depart.setLeaderDeptName(departService.getDepartAllLeaveName(depart.getId()));
				empMap.put(runTask2.getCreatorId(), depart);
			}
			runTask2.setCreatorDepart(depart.getLeaderDeptName() != null ? depart.getLeaderDeptName() : depart.getName());
			//下属异常考勤，获取异常考勤员工
			if(RunTask.RUN_CODE_120.equals(runTask2.getReProcdefCode())){
				EmpApplicationAbnormalAttendance attendance = empApplicationAbnormalAttendanceMapper.getById(Long.valueOf(runTask2.getEntityId()));
				runTask2.setSubordinateName(attendance.getCnName());
			}
		}
	}

	@Override
	public boolean isDh(Long ruProcdefId, Long entityId) {
		if(entityId == null) {
			RunTask runTask = runTaskMapper.getByRuProcdefId(ruProcdefId);
			entityId = runTask.getCreatorId();
		} 
		Depart depart = departMapper.getByEmpId(entityId);
		Employee leaderEmp = employeeMapper.getLeaderByDepartId(depart.getId());
		if(leaderEmp != null) {
			if(leaderEmp.getId().equals(entityId)) {
				return true;
			}
		} else {
			Employee powerEmp = employeeMapper.getPowerByDepartId(depart.getId());
			if(powerEmp != null) {
				if(powerEmp.getId().equals(entityId)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void updateEntyStatus(RuProcdef ruProcdef,User user, Integer approvalStatus) {
		if (RunTask.RUN_CODE_30.equals(ruProcdef.getReProcdefCode())) {// 加班
			EmpApplicationOvertime old = new EmpApplicationOvertime();
			old.setApprovalStatus(approvalStatus);
			old.setId(Long.valueOf(ruProcdef.getEntityId()));
			empApplicationOvertimeMapper.updateById(old);
		} else if (RunTask.RUN_CODE_41.equals(ruProcdef.getReProcdefCode())||RunTask.RUN_CODE_40.equals(ruProcdef.getReProcdefCode())||RunTask.RUN_CODE_120.equals(ruProcdef.getReProcdefCode())) {// 异常考勤
			EmpApplicationAbnormalAttendance old = new EmpApplicationAbnormalAttendance();
			old.setApprovalStatus(approvalStatus);
			old.setId(Long.valueOf(ruProcdef.getEntityId()));
			empApplicationAbnormalAttendanceMapper.updateById(old);
		} else if (RunTask.RUN_CODE_60.equals(ruProcdef.getReProcdefCode())) {// 请假
			EmpApplicationLeave old = new EmpApplicationLeave();
			old.setApprovalStatus(approvalStatus);
			old.setId(Long.valueOf(ruProcdef.getEntityId()));
			empApplicationLeaveMapper.updateById(old);
		} else if (RunTask.RUN_CODE_70.equals(ruProcdef.getReProcdefCode())) {// 外出
			EmpApplicationOutgoing old = new EmpApplicationOutgoing();
			old.setApprovalStatus(approvalStatus);
			old.setId(Long.valueOf(ruProcdef.getEntityId()));
			empApplicationOutgoingMapper.updateById(old);
		} else if (RunTask.RUN_CODE_80.equals(ruProcdef.getReProcdefCode())||RunTask.RUN_CODE_81.equals(ruProcdef.getReProcdefCode())) { // 出差
			EmpApplicationBusiness old = new EmpApplicationBusiness();
			old.setApprovalStatus(approvalStatus);
			old.setId(Long.valueOf(ruProcdef.getEntityId()));
			empApplicationBusinessMapper.updateById(old);
		} else if (RunTask.RUN_CODE_100.equals(ruProcdef.getReProcdefCode())) { // 晚到
			EmpApplicationOvertimeLate old = new EmpApplicationOvertimeLate();
			old.setApprovalStatus(approvalStatus);
			old.setId(Long.valueOf(ruProcdef.getEntityId()));
			empApplicationOvertimeLateMapper.updateById(old);
		} else if (RunTask.RUN_CODE_90.equals(ruProcdef.getReProcdefCode())||RunTask.RUN_CODE_91.equals(ruProcdef.getReProcdefCode())) { // 出差总结报告
			EmpApplicationBusiness old = new EmpApplicationBusiness();
			old.setApprovalStatus(approvalStatus);
			old.setId(Long.valueOf(ruProcdef.getEntityId()));
			empApplicationBusinessMapper.updateById(old);
		}else if(RunTask.RUN_CODE_50.equals(ruProcdef.getReProcdefCode())){//排班
			ApplyEmployClass old = new ApplyEmployClass();
			old.setApprovalStatus(approvalStatus);
			old.setId(Long.valueOf(ruProcdef.getEntityId()));
			applyEmployClassMapper.updateById(old);
		}else if(RunTask.RUN_CODE_110.equals(ruProcdef.getReProcdefCode())){//值班
			ApplyEmployDuty old = new ApplyEmployDuty();
			old.setApprovalStatus(approvalStatus);
			old.setId(Long.valueOf(ruProcdef.getEntityId()));
			applyEmployDutyMapper.updateById(old);
		}
		  
	}

	@Override
	public void runTaskMsg() {
		//XXX:任务超时,消息提醒
		RuProcdef ruProcdef = new RuProcdef();
		List<String> reProcdefCodes = new ArrayList<String>();
		Map<Long, Employee> employeeMap = new HashMap<Long, Employee>();
		//考勤类所有审批流程  待部门负责人处理状态，提出申请的次月3日前
		if(DateUtils.getIntervalDays(DateUtils.parse(DateUtils.getNow("yyyy-MM") + "-03",DateUtils.FORMAT_SHORT),
				DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_SHORT),DateUtils.FORMAT_SHORT)) == 0) {
			ruProcdef.setNodeCode("RESIGN_DH");
			ruProcdef.setLtCreateTime(DateUtils.parse(DateUtils.getNow(), "yyyy-MM"));
			reProcdefCodes.add("30");
			reProcdefCodes.add("40");
			reProcdefCodes.add("60");
			reProcdefCodes.add("70");
			reProcdefCodes.add("80");
			reProcdefCodes.add("90");
			reProcdefCodes.add("100");
			ruProcdef.setReProcdefCodes(reProcdefCodes);
			List<RuProcdef> msgList = ruProcdefMapper.getMsgList(ruProcdef);
			for (RuProcdef ruProcdef2 : msgList) {
				//申请人
				Employee createUser = employeeMapper.getById(ruProcdef2.getCreatorId());
				if(createUser!=null&&createUser.getJobStatus().intValue()!=ConfigConstants.JOB_STATUS_1.intValue()){
					//申请人未离职，发送消息给待办人
					EmpMsg empMsg = new EmpMsg();
					Employee employee = new Employee();
					if(employeeMap.containsKey(ruProcdef2.getAssigneeId())) {
						employee = employeeMap.get(ruProcdef2.getAssigneeId());
					} else {
						employee = employeeMapper.getById(ruProcdef2.getAssigneeId());
						employeeMap.put(ruProcdef2.getAssigneeId(), employee);
					}
					empMsg.setDelFlag(CommonPo.STATUS_NORMAL);
					empMsg.setType(EmpMsg.type_200);
					empMsg.setCompanyId(employee.getCompanyId());
					empMsg.setEmployeeId(ruProcdef2.getAssigneeId());
					empMsg.setRuTaskId(ruProcdef2.getRuTaskId());
					empMsg.setNodeCode(ruProcdef2.getNodeCode());
					empMsg.setTitle("审批临期提醒");
					StringBuffer sb = new StringBuffer();
					sb.append(ruProcdef2.getCreator())
					.append("员工的【")
					.append(ruProcdef2.getProcessName())
					.append("】还等待您审批中，请尽快处理哦！");
					empMsg.setContent(sb.toString());
					empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
					empMsg.setCreateTime(new Date());
					empMsgMapper.save(empMsg);
				}
			}
		}
			
		//出差流程 出差总结报告，待申请人处理状态，在出差返程日的第二个工作日发送
		ruProcdef = new RuProcdef();
		ruProcdef.setNodeCode("START");
		reProcdefCodes = new ArrayList<String>();
		reProcdefCodes.add("90");
		ruProcdef.setReProcdefCodes(reProcdefCodes);
		ruProcdef.setCreateTime(DateUtils.addDay(DateUtils.getToday(), -1));
		List<RuProcdef> msgList = ruProcdefMapper.getMsgList(ruProcdef);
		for (RuProcdef ruProcdef2 : msgList) {
			//申请人
			Employee createUser = employeeMapper.getById(ruProcdef2.getCreatorId());
			if(createUser!=null&&createUser.getJobStatus().intValue()!=ConfigConstants.JOB_STATUS_1.intValue()){
				//申请人未离职，发送消息给待办人
				EmpMsg empMsg = new EmpMsg();
				Employee employee = new Employee();
				if(employeeMap.containsKey(ruProcdef2.getAssigneeId())) {
					employee = employeeMap.get(ruProcdef2.getAssigneeId());
				} else {
					employee = employeeMapper.getById(ruProcdef2.getAssigneeId());
					employeeMap.put(ruProcdef2.getAssigneeId(), employee);
				}
				empMsg.setDelFlag(CommonPo.STATUS_NORMAL);
				empMsg.setType(EmpMsg.type_200);
				empMsg.setCompanyId(employee.getCompanyId());
				empMsg.setEmployeeId(ruProcdef2.getAssigneeId());
				empMsg.setRuTaskId(ruProcdef2.getRuTaskId());
				empMsg.setNodeCode(ruProcdef2.getNodeCode());
				empMsg.setTitle("协作临期提醒");
				empMsg.setContent("您的出差总结报告尚未完成，请尽快在出差返回公司的3个工作日内处理完成！");
				empMsg.setReadFlag(EmpMsg.READ_FLAG_NO);
				empMsg.setCreateTime(new Date());
				empMsgMapper.save(empMsg);
			}
		}
	}
}
