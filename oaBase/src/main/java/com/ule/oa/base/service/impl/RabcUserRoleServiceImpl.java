package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.RabcResourceMapper;
import com.ule.oa.base.mapper.RabcPrivilegeMapper;
import com.ule.oa.base.mapper.RabcRoleMapper;
import com.ule.oa.base.mapper.RabcUserRoleMapper;
import com.ule.oa.base.mapper.UserMapper;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.po.RabcUserRole;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.RabcRoleService;
import com.ule.oa.base.service.RabcUserRoleService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

@Service
public class RabcUserRoleServiceImpl implements RabcUserRoleService {

	@Autowired
	private RabcRoleMapper rabcRoleMapper;
	
	@Autowired
	private RabcUserRoleMapper rabcUserRoleMapper;

	@Autowired
	private RabcResourceMapper rabcResourceMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RabcPrivilegeMapper rabcPrivilegeMapper;
	
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private RabcRoleService uRoleService;

	@Autowired
	private ConfigService configService;

	


	/**
	 * 删除用户
	 * @throws OaException 
	 */
	@Override
	public void delUserRole(Long userId) throws OaException {
		User user = userService.getCurrentUser();
		if(userId == null){
			throw new OaException("请选择角色！");
		}
		RabcUserRole uRole = new RabcUserRole();
		uRole.setUserId(userId);
		uRole.setUpdateTime(new Date());
		uRole.setUpdateUser(user.getUserName());
		uRole.setDelFlag(1);
		//修改用户
		rabcUserRoleMapper.updateUserRole(uRole);
	}



	@Override
	public void updateUserById(User user) throws OaException {
		// TODO Auto-generated method stub
		user.setUpdateTime(new Date());
		User currentUser = userService.getCurrentUser();
		user.setUpdateUser(currentUser.getUserName());
		userMapper.updateById(user);
		this.delUserRole(user.getId());
		if(user.getRoleIdList()!=null && user.getRoleIdList().size()>0){
			this.addUserRoleList(user.getRoleIdList(),user.getId());
		}
	}
	//停用角色
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void forzenUserById(User user) throws OaException {
		// TODO Auto-generated method stub
		user.setUpdateTime(new Date());
		User currentUser = userService.getCurrentUser();
		user.setUpdateUser(currentUser.getUserName());
		userMapper.updateById(user);
	}
	
	private void addUserRoleList(List<Long> roleIdList,Long uid) throws OaException {
		// TODO Auto-generated method stub
		
		User user = userService.getCurrentUser();
		if(roleIdList == null  || roleIdList.size()==0){
			throw new OaException("用户不能为空！");
		}
		//先查询有没有此值 有则状态改为0 没有则新增
		
		
		//先查询数据库 之前拥有的权限 和这list集合做对比 存在相同的修改 多余的就新增
		List<Long> selectedRoleIdByDepartIdAndUserId = rabcUserRoleMapper.getSelectedRoleIdByDepartIdAndUserId(null,uid);
		
		List<Long> exists=new ArrayList<Long>(roleIdList);//新增的元素 add添加
		List<Long> notexists=new ArrayList<Long>(roleIdList);//存在相同的元素 update改状态del_flag=0
		
		exists.removeAll(selectedRoleIdByDepartIdAndUserId);
		notexists.removeAll(exists);
		if(notexists!=null && notexists.size()>0){
			rabcUserRoleMapper.updateUserRoleSInit(notexists,uid,user.getUserName());
		}
		if(roleIdList!=null && roleIdList.size()>0){
			rabcUserRoleMapper.savesInit(roleIdList,uid,user.getUserName());
		}
	}


	@Override
	public PageModel<User> getPageList(User user) {
		// TODO Auto-generated method stub
		int page = user.getPage() == null ? 0 : user.getPage();
		int rows = user.getRows() == null ? 0 : user.getRows();
	
		PageModel<User> pm = new PageModel<User>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		
		List<Long> needEmpTypeIdList = configService.getNeedEmpTypeIdList();
		user.setEmpTypeIdList(needEmpTypeIdList);
		Integer total = userMapper.getUserManageListCount(user);
		pm.setTotal(total);
		user.setOffset(pm.getOffset());
		user.setLimit(pm.getLimit());
		List<User> employees = userMapper.getUserManageList(user);
		pm.setRows(employees);
		return pm;
	}
	@Override
	public User getUserById(Integer id) {
		// TODO Auto-generated method stub
		return userMapper.getUserInfoById(id);
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void modifyUserByIds(User user) throws OaException {
		for (String empIds : user.getEmployIdsStr().split("，")) {
			User byEmployeeId = userMapper.getByEmployeeId(Long.parseLong(empIds));
			byEmployeeId.setRoleIdList(user.getRoleIdList());
			this.updateUserById(byEmployeeId);
		}
	}

	@Override
	public Map<String, Object> getRoleInfobyDepartIdAndUserId(Long departId,
			Long userId) {
		// TODO Auto-generated method stub
		//部门下的所有权限
		Map<String,Object> result = new HashMap<String,Object>();
		
		List<RabcRole> departRoleList = uRoleService.getAllURoleListByDepartId(departId);
		
		List<Long> selectedRoleIdList = rabcUserRoleMapper.getSelectedRoleIdByDepartIdAndUserId(departId, userId);
		for (int i = 0; i < departRoleList.size(); i++) {
			for (int j = 0; j < selectedRoleIdList.size(); j++) {
				if(departRoleList.get(i).getId().equals(selectedRoleIdList.get(j))){
					departRoleList.get(i).setIsSelected(1);
				}
			}
		}
		result.put("departRoleList", departRoleList);
		return result;
	}

	@Override
	public List<RabcRole> getDepartRoleListByUserId(Long userId) {
		// TODO Auto-generated method stub
		return  rabcUserRoleMapper.getDepartRoleListByUserId(userId);
	}

	//通过用户id来查询拥有的部门id
	@Override
	public List<Integer> getDeptIdsByUser(Long employeeId) {
		if(employeeId!=null){
			return  rabcUserRoleMapper.getDeptIdsByUser(employeeId);
		}else{
			return  new ArrayList<Integer>();
		}
		
	}

	@Override
	public List<Depart> getDepartByUser(Long employeeId) {
		return  rabcUserRoleMapper.getDepartByUser(employeeId);
	}



	@Override
	public void delUserRole(Long employeeId, Long departId, String roleName,String updateUser,Date updateTime) {
		//查询用户信息
		User user = userMapper.getByEmployeeId(employeeId);
		//查询角色信息
		List<RabcRole> roleList = rabcRoleMapper.getListByDepartIdAndName(departId, roleName);
		if(user!=null){
			for(RabcRole data:roleList){
				RabcUserRole del = new RabcUserRole();
				del.setRoleId(data.getId());
				del.setUserId(user.getId());
				del.setDelFlag(1);
				del.setUpdateTime(updateTime);
				del.setUpdateUser(updateUser);
				rabcUserRoleMapper.updateUserRole(del);
			}
		}
	}



	@Override
	public void initUserRole(Long employeeId, Long departId, String roleName,String createUser,Date createTime) {
		//查询用户信息
		User user = userMapper.getByEmployeeId(employeeId);
		//查询角色信息
		List<RabcRole> roleList = rabcRoleMapper.getListByDepartIdAndName(departId, roleName);
		if(user!=null){
			for(RabcRole data:roleList){
				RabcUserRole add = new RabcUserRole();
				add.setRoleId(data.getId());
				add.setUserId(user.getId());
				add.setDelFlag(0);
				add.setCreateTime(createTime);
				add.setCreateUser(createUser);
				rabcUserRoleMapper.save(add);
			}
		}
		
	}



	@Override
	public void delByRoleId(Long roleId,Date updateTime,String updateUser) {
		rabcUserRoleMapper.delByRoleId(roleId,updateTime,updateUser);
	}
	
}
