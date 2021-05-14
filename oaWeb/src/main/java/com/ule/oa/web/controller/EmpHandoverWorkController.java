package com.ule.oa.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpHandoverWork;
import com.ule.oa.base.po.EmpResign;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.HrEmpCheckList;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpHandoverWorkService;
import com.ule.oa.base.service.EmpResignService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.HrEmpCheckListService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.DateUtils;

/**
 * @ClassName: 离职工作交接
 * @Description: 离职工作交接
 * @author yangjie
 * @date 2017年5月27日
 */

@Controller
@RequestMapping("handoverWork")
public class EmpHandoverWorkController {
	
	@Autowired
	private EmpHandoverWorkService empHandoverWorkService;
	@Autowired
	private EmpResignService empResignService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private DepartService departService;
	@Autowired
	private HrEmpCheckListService hrEmpCheckListService;
	@Autowired
	private UserService userService;
	
	//离职工作交接首页
	@RequestMapping("/index.htm")
	public String index(HttpServletRequest request,Long empResignId, String nodeCode, Long ruProcdefId){
		EmpResign empResign = empResignService.getById(empResignId);
		request.setAttribute("empResign", empResign);
		request.setAttribute("nodeCode", nodeCode);
		request.setAttribute("ruProcdefId", ruProcdefId);
		EmpHandoverWork work = new EmpHandoverWork();
		work.setEmpResignId(empResignId);
		//交接明细
		List<EmpHandoverWork> list = empHandoverWorkService.getListByCondition(work);
		if(list!=null&&list.size()>0){
			request.setAttribute("list", list);
			Depart depart = departService.getById(empResign.getDepartId());
			Employee param = new Employee();
//			param.setDepartId(empResign.getDepartId());
			List<Employee> returnList = employeeService.getListByCondition(param);
			List receiverList = new ArrayList();
			for(Employee em:returnList){
				Map map = new HashMap();
				map.put("id", em.getId());
				map.put("cnName", em.getCnName());
				receiverList.add(map);
			}
			request.setAttribute("receiverList", receiverList);
			request.setAttribute("leaderId", depart.getLeader());
			return "base/handover/update";
		}
		return "base/handover/index";
	}
	
	//工作交接提交
	@ResponseBody
	@RequestMapping("/save.htm")
	public Map<String, Object> save(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		String empResignId = request.getParameter("empResignId");
		String ruProcdefId = request.getParameter("ruProcdefId");
		String list = request.getParameter("list");
		if(StringUtils.isBlank(empResignId)){
			map.put("message", "辞职申请ID不能为空");
			return map;
		}
		//获取汇报对象
		EmpResign empResign = empResignService.getById(Long.valueOf(empResignId));
		Employee employee = employeeService.getById(empResign.getEmployeeId());
		//获取汇报对象
		List<EmpHandoverWork> workList = new ArrayList<EmpHandoverWork>();
		JSONArray array = JSONArray.fromObject(list);
		for(int i=0;i<array.size();i++){
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			EmpHandoverWork work = new EmpHandoverWork();
			work.setEmpResignId(Long.valueOf(empResignId));
			work.setDelFlag(CommonPo.STATUS_NORMAL);
			work.setNumber(jsonObject.getInt("number"));
			work.setHandoverContent(jsonObject.getString("handoverContent"));
			work.setIsTransfer(jsonObject.getInt("isTransfer"));
			work.setRemark(jsonObject.getString("remark"));
			work.setCreateTime(new Date());
			work.setCreateUser("");
			work.setIsHandoverCompleted(EmpHandoverWork.HANDOVERCOMPLETED_NO);
			work.setLeaderStatus(EmpHandoverWork.LEADERSTATUS_NO);
			work.setDhStatus(EmpHandoverWork.DHSTATUS_NO);
//			work.setDhId(employee.getLeader());
			workList.add(work);
		}
		try{
			empHandoverWorkService.batchSave(workList, Long.valueOf(ruProcdefId), Long.valueOf(empResignId));
			map.put("success", true);
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("message", "保存失败");
		}
		return map;
	}
	
	//离职申请修改
	@ResponseBody
	@RequestMapping("/update.htm")
	public Map<String, Object> update(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		String empResignId = request.getParameter("empResignId");
		String list = request.getParameter("list");
		if(StringUtils.isBlank(empResignId)){
			map.put("message", "辞职申请ID不能为空");
			return map;
		}
		//获取汇报对象
		EmpResign empResign = empResignService.getById(Long.valueOf(empResignId));
		Employee employee = employeeService.getById(empResign.getEmployeeId());
		//获取汇报对象
		List<EmpHandoverWork> workList = new ArrayList<EmpHandoverWork>();
		JSONArray array = JSONArray.fromObject(list);
		for(int i=0;i<array.size();i++){
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			EmpHandoverWork work = new EmpHandoverWork();
			if(Integer.valueOf(String.valueOf(jsonObject.get("delFlag"))).intValue()==CommonPo.STATUS_DELETE.intValue()){
				work.setId(jsonObject.getLong("id"));
				work.setDelFlag(CommonPo.STATUS_DELETE);
			}else{
				if(!StringUtils.isBlank((String.valueOf(jsonObject.get("id"))))&&jsonObject.get("id")!=null){
					work.setId(Long.valueOf(String.valueOf(jsonObject.get("id"))));
				}
				work.setDelFlag(CommonPo.STATUS_NORMAL);
				work.setEmpResignId(Long.valueOf(empResignId));
				work.setNumber(jsonObject.getInt("number"));
				work.setHandoverContent(jsonObject.getString("handoverContent"));
				work.setReceiverId(jsonObject.getLong("receiverId"));
				work.setReceiver(jsonObject.getString("receiver"));
				work.setIsTransfer(jsonObject.getInt("isTransfer"));
				work.setRemark(jsonObject.getString("remark"));
				work.setIsHandoverCompleted(EmpHandoverWork.HANDOVERCOMPLETED_NO);
				work.setLeaderId(jsonObject.getLong("leaderId"));
				work.setLeaderStatus(EmpHandoverWork.LEADERSTATUS_NO);
				work.setDhStatus(EmpHandoverWork.DHSTATUS_NO);
//				work.setDhId(employee.getLeader());
			}
			workList.add(work);
		}
		try{
			empHandoverWorkService.update(workList,"");
			map.put("success", true);
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("message", "保存失败");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("/confirm.htm")
	public Map<String, Object> confirm(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		String empResignId = request.getParameter("empResignId");
		String ruProcdefId = request.getParameter("ruProcdefId");
		String list = request.getParameter("list");
		if(StringUtils.isBlank(empResignId)){
			map.put("message", "辞职申请ID不能为空");
			return map;
		}
		//获取汇报对象
		EmpResign empResign = empResignService.getById(Long.valueOf(empResignId));
		Employee employee = employeeService.getById(empResign.getEmployeeId());
		//获取汇报对象
		List<EmpHandoverWork> workList = new ArrayList<EmpHandoverWork>();
		JSONArray array = JSONArray.fromObject(list);
		for(int i=0;i<array.size();i++){
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			EmpHandoverWork work = new EmpHandoverWork();
			if(Integer.valueOf(String.valueOf(jsonObject.get("delFlag"))).intValue()==CommonPo.STATUS_DELETE.intValue()){
				work.setId(jsonObject.getLong("id"));
				work.setDelFlag(CommonPo.STATUS_DELETE);
			}else{
				if(!StringUtils.isBlank((String.valueOf(jsonObject.get("id"))))&&jsonObject.get("id")!=null){
					work.setId(Long.valueOf(String.valueOf(jsonObject.get("id"))));
				}
				work.setDelFlag(CommonPo.STATUS_NORMAL);
				work.setEmpResignId(Long.valueOf(empResignId));
				work.setNumber(jsonObject.getInt("number"));
				work.setHandoverContent(jsonObject.getString("handoverContent"));
				work.setReceiverId(jsonObject.getLong("receiverId"));
				work.setReceiver(jsonObject.getString("receiver"));
				work.setIsTransfer(jsonObject.getInt("isTransfer"));
				work.setRemark(jsonObject.getString("remark"));
				work.setIsHandoverCompleted(EmpHandoverWork.HANDOVERCOMPLETED_NO);
				work.setLeaderId(jsonObject.getLong("leaderId"));
				work.setLeaderStatus(EmpHandoverWork.LEADERSTATUS_NO);
				work.setDhStatus(EmpHandoverWork.DHSTATUS_NO);
//				work.setDhId(employee.getLeader());
			}
			workList.add(work);
		}
		try{
//			empHandoverWorkService.confirm(list);
			map.put("success", true);
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("message", "保存失败");
		}
		return map;
	}
	
	//交接页面
	@RequestMapping("/handover.htm")
	public String handover(HttpServletRequest request,Long empResignId,Long receiverId, String nodeCode, Long ruProcdefId){
		EmpResign empResign = empResignService.getById(empResignId);
		request.setAttribute("empResign", empResign);
		request.setAttribute("nodeCode", nodeCode);
		request.setAttribute("ruProcdefId", ruProcdefId);
		EmpHandoverWork work = new EmpHandoverWork();
		work.setEmpResignId(empResignId);
		if(receiverId != null){
			work.setReceiverId(receiverId);
		}
		//交接明细
		List<EmpHandoverWork> list = empHandoverWorkService.getListByCondition(work);
		if(list!=null&&list.size()>0){
			request.setAttribute("list", list);
		}
		return "base/handover/handover";
	}
	
	//确认完成工作交接
	@ResponseBody
	@RequestMapping("/confirmHandover.htm")
	public Map<String, Object> confirmHandover(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		String list = request.getParameter("list");
		List<EmpHandoverWork> workList = new ArrayList<EmpHandoverWork>();
		JSONArray array = JSONArray.fromObject(list);
		for(int i=0;i<array.size();i++){
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			EmpHandoverWork work = new EmpHandoverWork();
			work.setId(jsonObject.getLong("id"));
			work.setIsHandoverCompleted(EmpHandoverWork.HANDOVERCOMPLETED_YES);
			work.setCompleteHandoverDate(DateUtils.parse(jsonObject.getString("completeHandoverDate"),DateUtils.FORMAT_SHORT));
			work.setUpdateTime(new Date());
			work.setUpdateUser("");
			workList.add(work);
		}
		try{
			empHandoverWorkService.confirmHandover(workList);
			map.put("success", true);
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("message", "保存失败");
		}
		return map;
	}
	
	//未完成工作交接
	@ResponseBody
	@RequestMapping("/backHandoverCompleted.htm")
	public Map<String, Object> backHandoverCompleted(EmpHandoverWork work){
		Map<String,Object> map = new HashMap<String, Object>();
		work.setUpdateTime(new Date());
		work.setUpdateUser("");
		work.setIsHandoverCompleted(EmpHandoverWork.HANDOVERCOMPLETED_NO);
		try{
			empHandoverWorkService.updateById(work);
			map.put("success", true);
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("message", "保存失败");
		}
		return map;
	}
	
	//验收
	@ResponseBody
	@RequestMapping("/accept.htm")
	public Map<String, Object> accept(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		String list = request.getParameter("list");
		List<EmpHandoverWork> workList = new ArrayList<EmpHandoverWork>();
		JSONArray array = JSONArray.fromObject(list);
		for(int i=0;i<array.size();i++){
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			EmpHandoverWork work = new EmpHandoverWork();
			work.setId(jsonObject.getLong("id"));
			work.setLeaderStatus(EmpHandoverWork.LEADERSTATUS_YES);
			work.setUpdateTime(new Date());
			work.setUpdateUser("");
			workList.add(work);
		}
		try{
			empHandoverWorkService.confirmHandover(workList);
			map.put("success", true);
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("message", "保存失败");
		}
		return map;
	}
	
	//审批
	@ResponseBody
	@RequestMapping("/approve.htm")
	public Map<String, Object> approve(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String, Object>();
		String list = request.getParameter("list");
		String empResignId = request.getParameter("empResignId");
		List<EmpHandoverWork> workList = new ArrayList<EmpHandoverWork>();
		JSONArray array = JSONArray.fromObject(list);
		for(int i=0;i<array.size();i++){
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			EmpHandoverWork work = new EmpHandoverWork();
			work.setId(jsonObject.getLong("id"));
			work.setDhStatus(EmpHandoverWork.DHSTATUS_YES);
			work.setUpdateTime(new Date());
			work.setUpdateUser("");
			workList.add(work);
		}
		try{
			empHandoverWorkService.approve(workList,empResignId);
			map.put("success", true);
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("message", "保存失败");
		}
		return map;
	}
	
	//离职手续办理清单
	@RequestMapping("/checkList.htm")
	public String checkList(HttpServletRequest request,Long empResignId){
		EmpResign empResign = empResignService.getById(empResignId);
		request.setAttribute("empResign", empResign);
		//离职手续办理清单
		LinkedHashMap<String,List<HrEmpCheckList>> map = new LinkedHashMap<String,List<HrEmpCheckList>>();
		HrEmpCheckList hrEmpCheckList = new HrEmpCheckList();
		hrEmpCheckList.setEmpResignId(empResignId);
		List<HrEmpCheckList> list = hrEmpCheckListService.getListByCondition(hrEmpCheckList);
		for(HrEmpCheckList checkList:list){
			if(map.containsKey(checkList.getDepartName())){
				map.get(checkList.getDepartName()).add(checkList);
			}else{
				List<HrEmpCheckList> param = new ArrayList<HrEmpCheckList>();
				param.add(checkList);
				map.put(checkList.getDepartName(), param);
			}
		}
		request.setAttribute("map", map);
		return "base/handover/checkList";
	}
	
	//离职手续办理清单分配各部门
	@RequestMapping("/checkListToDepart.htm")
	public String checkListToDepart(HttpServletRequest request,Long empResignId,String type){
		EmpResign empResign = empResignService.getById(empResignId);
		request.setAttribute("empResign", empResign);
		HrEmpCheckList hrEmpCheckList = new HrEmpCheckList();
		hrEmpCheckList.setEmpResignId(empResignId);
		//工作部门
		if("1".equals(type)){
			hrEmpCheckList.setDepartName("工作部门");
		}else if("2".equals(type)){
			hrEmpCheckList.setDepartName("行政部门");
		}else if("3".equals(type)){
			hrEmpCheckList.setDepartName("IT部门");
		}else if("4".equals(type)){
			hrEmpCheckList.setDepartName("财务部");
		}else if("5".equals(type)){
			hrEmpCheckList.setDepartName("法务部");
		}else if("6".equals(type)){
			hrEmpCheckList.setDepartName("后台权限关闭");
		}else if("7".equals(type)){
			hrEmpCheckList.setDepartName("人力资源部");
		}
		List<HrEmpCheckList> list = hrEmpCheckListService.getListByCondition(hrEmpCheckList);
		request.setAttribute("list", list);
		//交接明细
		return "base/handover/checkListToDepart";
	}
	
	//各部门完成交接
	@ResponseBody
	@RequestMapping("/checkByDepart.htm")
	public Map<String, Object> checkByDepart(HttpServletRequest request){
		User user = userService.getCurrentUser();
		Map<String,Object> map = new HashMap<String, Object>();
		String list = request.getParameter("list");
		String remark = request.getParameter("remark");
		List<HrEmpCheckList> hrEmpCheckList = new ArrayList<HrEmpCheckList>();
		JSONArray array = JSONArray.fromObject(list);
		for(int i=0;i<array.size();i++){
			JSONObject jsonObject = JSONObject.fromObject(array.get(i), new JsonConfig());
			HrEmpCheckList checkList = new HrEmpCheckList();
			checkList.setId(jsonObject.getLong("id"));
			checkList.setCheckStatus(jsonObject.getInt("checkStatus"));
			checkList.setUpdateTime(new Date());
			checkList.setRemark(remark);
			checkList.setApprover(user.getEmployee().getCnName());
			checkList.setApproverId(user.getEmployeeId());
			checkList.setApproverDate(new Date());
			checkList.setUpdateUser(user.getEmployee().getCnName());
			hrEmpCheckList.add(checkList);
		}
		try{
			hrEmpCheckListService.checkByDepart(hrEmpCheckList);
			map.put("success", true);
			map.put("message", "保存成功");
		}catch(Exception e){
			map.put("message", "保存失败");
		}
		return map;
	}
	
}
