package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.DepartPositionMapper;
import com.ule.oa.base.mapper.EmpDepartMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.RabcRoleMapper;
import com.ule.oa.base.mapper.RabcRolePrivilegeMapper;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.RabcRoleService;
import com.ule.oa.base.service.RabcUserRoleService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.DateUtils;

@Service
public class DepartServiceImpl implements DepartService{

	@Autowired
	private DepartMapper departMapper;
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	@Autowired
	private RabcUserRoleService rabcUserRoleService;
	@Autowired
	private RabcRoleService rabcRoleService;
	@Autowired
	private EmpDepartMapper empDepartMapper;
	@Autowired
	private DepartPositionMapper departPositionMapper;
	@Autowired
	private RabcRolePrivilegeMapper rabcRolePrivilegeMapper;
	@Autowired
	private RabcRoleMapper rabcRoleMapper;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void save(Depart model) {
		User user = userService.getCurrentUser();
		model.setParentId(null);
		model.setIsShowInMo(0);
		departMapper.save(model);
		rabcRoleService.initDepartRoles(model.getId(), model.getName(), user);
	}

	@Override
	public int updateById(Depart model) {
		return departMapper.updateById(model);
	}

	@Override
	public List<Depart> getListByCondition(Depart model) {
		return departMapper.getListByCondition(model);
	}

	@Override
	public List<Depart> getListByLeaderOrPower(Depart model) {
		return departMapper.getListByLeaderOrPower(model);
	}

	@Override
	public List<Depart> getDepartList(Long parentId) {
		Depart model = new Depart();
		model.setParentId(parentId);
		List<Depart> list = departMapper.getChildrenList(model);
		return list;
	}
	
	public List<Depart> getDepart(List<Depart> departList, Long parentId, Long currentParentId, List<Depart> returnlist){
		List<Depart> list = new ArrayList<Depart>();
		Integer currentIdDepart = null;
		int i = 0;
		for (Depart depart : departList) {
			if(currentParentId.equals(depart.getParentId())){
				list.add(depart);
				if(parentId.equals(currentParentId)){
					returnlist.add(depart);
				}
			}
			if(null == currentIdDepart){
				if(currentParentId.equals(depart.getId())){
					currentIdDepart = i;
				}
			}
			i++;
		}
		if(list.isEmpty()){
			if(null != currentIdDepart){
				departList.get(currentIdDepart).setEmpCount(0);
			}
			return departList;
		}else{
			if(null != currentIdDepart){
				Integer empCount = departList.get(currentIdDepart).getEmpCount() == null ? 0 : departList.get(currentIdDepart).getEmpCount();
				departList.get(currentIdDepart).setEmpCount(empCount + list.size());
			}
			for (Depart depart : list) {
				departList = getDepart(departList, parentId, depart.getId(), returnlist);
			}
			return departList;
		}
	}

	@Override
	public List<Map<String, String>> getTreeList(Depart model) {
		List<Depart> listByCondition = departMapper.getListByCondition(model);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		for (Depart depart : listByCondition) {
			map = new HashMap<String, String>();
			map.put("id", depart.getId() + "");
			map.put("pId", depart.getParentId() == null ? "0" : depart.getParentId() + "");
			map.put("companyId", depart.getCompanyId() == null ? "" : depart.getCompanyId() + "");
			map.put("name", depart.getName());
			map.put("leader", depart.getLeader()==null ? "0":depart.getLeader()+"");
			map.put("leaderName", getLeaderName(depart.getLeader()));
			map.put("power",depart.getPower()==null ? "0":depart.getPower()+"");
			map.put("rank", depart.getRank() + "");
			list.add(map);
		}
		return list;
	}
	
	@Override
	public List<Map<String, String>> getTreeAppList(Depart depart) {
		List<Depart> listByCondition = departMapper.getTreeAppCondition(depart);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		for (Depart depart1 : listByCondition) {
			map = new HashMap<String, String>();
			map.put("id", depart1.getId() + "");
			map.put("pId", depart1.getParentId() == null ? "0" : depart1.getParentId() + "");
			map.put("companyId", depart1.getCompanyId() == null ? "" : depart1.getCompanyId() + "");
			map.put("name", depart1.getName());
			map.put("leader", depart1.getLeader()==null ? "0":depart1.getLeader()+"");
			map.put("leaderName", getLeaderName(depart1.getLeader()));
			map.put("power",depart1.getPower()==null ? "0":depart1.getPower()+"");
			map.put("rank", depart1.getRank() + "");
			map.put("type", depart1.getType() + "");
			list.add(map);
		}
		return list;
	}
	
	public String getLeaderName(Long id){
		if(null == id) {return "";}
		Employee employee = employeeService.getById(id);
		
		if(null != employee){
			return employee.getCnName();
		}
		
		return "";
	}

	@Override
	public List<Employee> getEmpList(Long id) {
		return employeeService.getEmpsByDepart(String.valueOf(id));
	};

	@Override
	public Depart getById(Long id) {
		return departMapper.getById(id);
	}
	
	@Override
	public Depart getByCondition(Depart model) {
		List<Depart> listDepart = getListByCondition(model);
		
		if(null != listDepart && listDepart.size()>0){
			Depart depart = listDepart.get(0);
			//设置部门负责人
			Long leader = depart.getLeader();
			if(null != leader){
				Employee emp = employeeService.getById(leader);
				depart.setLeaderName(null != emp ? emp.getCnName() : "");
			}else{
				depart.setLeaderName("");
			}
			
			//设置权利行使人
			Long power = depart.getPower();
			if(null != power){
				Employee emp = employeeService.getById(power);
				depart.setPowerName(null != emp ? emp.getCnName() : "");
			}else{
				depart.setPowerName("");
			}
			
			return depart;
		}
		
		return model;
	}

	@Override
	public List<Employee> getPowerList(String departId) {
		return employeeService.getEmpsByDepart(departId);
	}
	
	@Override
	public Depart getInfoById(Long id) {
		Depart depart = departMapper.getInfoById(id);
		depart.setParentName("");
		if(depart.getType()>1){
			while(true){
				Depart pDepart = departMapper.getInfoById(depart.getParentId());
				depart.setParentId(pDepart.getParentId());
				if(pDepart.getType().intValue()==1){
					depart.setParentName(pDepart.getName());
					break;
				}
			}
		}
		return depart;
	}

	@Override
	public List<Depart> getDListByParentId(Depart depart) {
		List<Depart> list = departMapper.getDListByParentId(depart);
		if (null != list && list.size() > 0) {  
            for (int i = 0; i < list.size(); i++) {  
            	Depart dp = list.get(i);
				Depart dp2 = new Depart();
				dp2.setParentId(dp.getId());
				dp.setChildren(this.getDListByParentId(dp2));
				if(dp.getType() != null && dp.getType().intValue() == 1) {
					//一级部门,查询该部门下所有部门的员工数
					dp.setEmpCount(employeeService.getEmpTotalByDepartId(dp.getId()));
					dp.setName(dp.getName() + '(' + dp.getEmpCount() + ')');
				} else {
					//非一级部门,查询该部门下的员工数
					dp.setEmpCount(employeeService.getEmpCountByDepartId(dp.getId()));
					dp.setName(dp.getName() + '(' + dp.getEmpCount() + ')');
				}
            }
	    }
		return list;
	}

	@Override
	public List<Depart> getDEListByParentId(Depart depart) {
		List<Depart> list = departMapper.getDListByParentId(depart);
		if (null != list && list.size() > 0) {  
            for (int i = 0; i < list.size(); i++) {  
            	Depart dp = list.get(i);
				Depart dp2 = new Depart();
				dp2.setParentId(dp.getId());
				dp.setChildren(this.getDEListByParentId(dp2));
				if(dp.getType() != null && (dp.getType().intValue() == 1 || dp.getType().intValue() == 0)) {
					//一级部门,查询该部门下所有部门的员工数
					dp.setEmpCount(employeeService.getEmpTotalByDepartId(dp.getId()));
					if(dp.getParentId() == null) {
						dp.setName(dp.getName());
					} else {
						dp.setName(dp.getName() + "(" + dp.getEmpCount() + ")");
					}
				} else {
					//非一级部门,查询该部门下的员工数
					dp.setEmpCount(employeeService.getEmpCountByDepartId(dp.getId()));
					if(dp.getParentId() == null) {
						dp.setName(dp.getName());
					} else {
						dp.setName(dp.getName() + "(" + dp.getEmpCount() + ")");
					}
					dp.setFlag("1");
					//查询部门下的员工
			    	List<Depart> dList = new ArrayList<Depart>();
			    	List<Employee> empList = employeeService.getEmpsByDepart(dp.getId()+"");
			    	if(empList != null && empList.size() > 0) {
						for (Employee employee : empList) {
							Depart dp3 = new Depart();
							dp3.setName(employee.getDepartName());
							dp3.setTitle(employee.getCnName() + ' ' + employee.getPositionName());
							dList.add(dp3);
						}
						dp.setChildren(dList);
			    	}
				}
            }
	    } 
		return list;
	}

	@Override
	public Depart getInfoByEmpId(Long empId) {
		Depart depart = departMapper.getByEmpId(empId);
		return depart;
	}

	/**
	  * getCurrentDepart(获取当前登录用户对应的部门)
	  * @Title: getCurrentDepart
	  * @Description: 获取当前登录用户对应的部门
	  * @return
	  * @throws OaException    设定文件
	  * Depart    返回类型
	  * @throws
	 */
	public Depart getCurrentDepart() throws OaException{
		User user = userService.getCurrentUser();
		
		if(null == user){
			throw new OaException("获取当前登录用户对应的部门信息为空，请重新登录");
		}
		
		return user.getDepart();
	}
	
	public Long getCurrentDepartId() throws OaException{
		return getCurrentDepart().getId();
	}

	@Override
	public boolean checkLeaderIsDh(Long leader) {
		List<Depart> dhList = departMapper.getLeaders();
		boolean flag = false;
		for(Depart dh:dhList){
			if(dh.getLeader().equals(leader)){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	@Override
	public List<Depart> getByParentId(Long parentId){
		return departMapper.getByParentId(parentId);
	}
	
	/**
	  * getFirstDepart(获得一级部门)
	  * @Title: getFirstDepart
	  * @Description: 获得一级部门
	  * @return    设定文件
	  * List<Depart>    返回类型
	  * @throws
	 */
	public List<Depart> getFirstDepart(){
		return departMapper.getFirstDepart();
	}
    
    /**
     * 根据部门id获取一级部门+当前部门
     */
	@Override
	public String setDepartName(Depart parentDepart,String childDepartName){
		Depart model = new Depart();
		model.setId(parentDepart.getParentId());
		Depart dp = getByCondition(model);
		
		if(null != dp){
			if(dp.getType().intValue() == 1 || null == dp.getParentId()){//当前查找的节点是一级部门或者当前部门是挂在虚拟部门下面
				return dp.getName() + "_" + childDepartName;
			}else{
				return setDepartName(dp, childDepartName);
			}
		}else{
			return parentDepart.getName();
		}
	}

	@Override
	public String getDepartAllLeaveName(Long departId) {

		String departName = "";
		
		if(null != departId){
			Depart depart = getById(departId);
			if(null != depart){
				
				if(depart.getType().intValue() == 1 || null == depart.getParentId()){
					departName = depart.getName();
				}else{
					departName = setDepartName(depart, depart.getName());
				}
			}
			
		}else{
			
		}
		return departName;
	}

	@Override
	public List<Integer> getDepartList(Integer firstDepart,Integer secondDepart){
		
		List<Integer> departList = new ArrayList<Integer>();
		if(secondDepart!=null){
			departList.add(secondDepart);
		}else if(secondDepart==null&&firstDepart!=null){
			
			departList.add(firstDepart);
			List<Depart> list = getByParentId(Long.valueOf(String.valueOf(firstDepart)));
		    
		    for(Depart depart:list){
		    	departList.add(Integer.valueOf(String.valueOf(depart.getId())));
		    };
		}

		
		if(departList.isEmpty()){
			departList = null;
		}
		return departList;
	}

	@Override
	public List<Map<String, Object>> getEmployeeTreeList(Depart model) {
		//部门树信息
		List<Map<String,Object>> departTree = departMapper.getEmployeeTreeList();
		//员工信息
		List<Map<String,Object>> employeeTree = employeeMapper.getEmployeeTreeList();
		departTree.addAll(employeeTree);
		return departTree;
	}

	@Override
	public List<Depart> getAllDepart() {
		return departMapper.getAllDepart();
	}
	
	/**
	 * 查询员工下所有负责部门
	 */
	@Override
	public List<Depart> getAllDepartByLeaderId(Long empId) {
		List<Depart> departList = departMapper.getAllDepartByLeaderId(empId);
		return departList;
	}

	@Override
	public List<Employee> getEmpListByDepartAndCondition(Long departId, String condition) {
		List<Employee> empList = departMapper.getEmpListByDepartAndCondition(departId,condition);
		return empList;
	}

	@Override
	public List<Depart> getAllDepartByEmpList(List<Employee> empList) {
		List<Depart> departList = departMapper.getAllDepartByEmpList(empList);
		return departList;
	}
	
	//获得登录人所负责的部门和其下属标准工时员工的部门集合
	@Override
	public List<Depart> getSubordinateDepartList() {
		
		User user = userService.getCurrentUser();
		List<Depart> departList = rabcUserRoleService.getDepartByUser(user.getEmployeeId());

		return departList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void updateDeptLeaderById(Depart depart) throws OaException{
		if(depart.getId()==null){
			throw new OaException("参数有误！");
		}
		//获取当前登录人
		User user = userService.getCurrentUser();
		Date currentTime = new Date();
		Depart old = departMapper.getById(depart.getId());
		if(old==null){
			throw new OaException("参数有误！");
		}
		depart.setUpdateTime(currentTime);
		depart.setUpdateUser(user.getEmployee().getCnName());
		//修改部门负责人权限数据
		if(old.getLeader()==null){
			//初始化部门负责人该部门权限信息
			if(depart.getLeader()!=null){
				rabcUserRoleService.initUserRole(depart.getLeader(), 
						old.getId(), "部门负责人", user.getEmployee().getCnName(), currentTime);
			}
		}else{
			if(depart.getLeader()!=null){
				//部门负责人有变更
				if(depart.getLeader().longValue()!=old.getLeader().longValue()){
					//删除老部门负责人权限信息
					rabcUserRoleService.delUserRole(old.getLeader(), old.getId(), "部门负责人", user.getEmployee().getCnName(), currentTime);
					//初始化新部门负责人该部门权限信息
					rabcUserRoleService.initUserRole(depart.getLeader(), old.getId(), "部门负责人", user.getEmployee().getCnName(), currentTime);
				}
			}else{
				//删除老部门负责人权限信息
				rabcUserRoleService.delUserRole(old.getLeader(), old.getId(), "部门负责人", user.getEmployee().getCnName(), currentTime);
			}
		}
		//修改部门信息
		departMapper.updateById(depart);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void deleteById(Long departId) throws OaException {
		
		if(departId==null){
			throw new OaException("参数有误！");
		}
		
		Depart depart = departMapper.getById(departId);
		if(depart==null){
			throw new OaException("参数有误！");
		}
		
		User user = userService.getCurrentUser();
		Date currentTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		
		//判断部门下面是否存在在职员工
		List<EmpDepart> empDepartList = empDepartMapper.getPayRollList(departId);
		if(null != empDepartList && empDepartList.size() > 0){
			throw new OaException("删除失败,当前部门下面存在员工！");
		}
		
		//部门职位关系删除
		departPositionMapper.delByDepartId(departId,currentTime,user.getEmployee().getCnName());
		
		//部门拥有的权限数据
		List<RabcRole> roleList = rabcRoleService.getAllURoleListByDepartId(departId);
		for(RabcRole data:roleList){
			
			//删除角色权限关系，
			rabcRolePrivilegeMapper.delByRoleId(data.getId(),currentTime,user.getEmployee().getCnName());
			
			//删除角色用户关系
			rabcUserRoleService.delByRoleId(data.getId(),currentTime,user.getEmployee().getCnName());
			
			//删除部门下的角色
			data.setDelFlag(1);
			data.setUpdateTime(currentTime);
			data.setUpdateUser(user.getEmployee().getCnName());
			rabcRoleMapper.deleteRole(data);
		}
		
		//删除部门
		depart.setDelFlag(1);
		depart.setUpdateTime(currentTime);
		depart.setUpdateUser(user.getEmployee().getCnName());
		departMapper.updateById(depart);

	}

	@Override
	public Map<String, Object> getDepartAndEmpListByCodeOrName(String code,String name) {
		Map<String,Object> result = new HashMap<String,Object>();
		if(StringUtils.isBlank(name)&&StringUtils.isBlank(code)) {
			result.put("code", "1111");
			result.put("msg", "参数有误！");
			return result;
		}
		Depart depart = departMapper.getByCodeOrName(code, name);
		if(depart == null) {
			result.put("code", "2222");
			result.put("msg", "未查到部门信息！");
			return result;
		}
		Map<String,Object> departMap = new HashMap<String,Object>();
		departMap.put("name", depart.getName());
		departMap.put("code", depart.getCode());
		departMap.put("leaderName", "");
		departMap.put("leaderEmail", "");
		departMap.put("leaderPhone", "");
		departMap.put("leaderTel", "");
		List<Map<String,String>> empListMap = new ArrayList<Map<String,String>>();
		departMap.put("empList", empListMap);
		if(depart.getLeader()!=null) {
			Employee leader = employeeMapper.getById(depart.getLeader());
			if(leader!=null) {
				departMap.put("leaderName", leader.getCnName());
				departMap.put("leaderEmail", leader.getEmail());
				departMap.put("leaderPhone", leader.getMobile());
				departMap.put("leaderTel", leader.getExtensionNumber());
			}
		}
		
		List<Employee> empList = employeeMapper.getListByDepart(depart.getId());
		for(Employee data:empList) {
			Map<String,String> map = new HashMap<String,String>();
			map.put("code", data.getCode());
			map.put("name", data.getCnName());
			map.put("email", data.getEmail());
			map.put("phone", data.getMobile());
			map.put("tel", data.getExtensionNumber());
			map.put("username", data.getEngName());
			map.put("status", "在职");
			if(data.getJobStatus().equals(1)) {
				map.put("status", "离职");
			}else if(data.getJobStatus().equals(2)) {
				map.put("status", "待离职");
			}
			map.put("tel", data.getExtensionNumber());
			empListMap.add(map);
		}
		departMap.put("empList", empListMap);
		result.put("depart", departMap);
		result.put("code", "0000");
		result.put("msg", "查询成功！");
		return result;
	}
}
