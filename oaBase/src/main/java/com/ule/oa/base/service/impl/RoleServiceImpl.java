package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.RoleMapper;
import com.ule.oa.base.po.Role;
import com.ule.oa.base.service.RoleService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: RoleServiceImpl
  * @Description: 角色管理业务层
  * @author minsheng
  * @date 2017年12月6日 下午3:28:40
 */
@Service
public class RoleServiceImpl implements RoleService {
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private UserService userService;
	
	public PageModel<Role> getPageList(Role role){
		int page = role.getPage() == null ? 0 : role.getPage();
		int rows = role.getRows() == null ? 0 : role.getRows();
		
		PageModel<Role> pm = new PageModel<Role>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = roleMapper.getCount(role);
		pm.setTotal(total);
		
		role.setOffset(pm.getOffset());
		role.setLimit(pm.getLimit());
		
		List<Role> roles = roleMapper.getPageList(role);
		pm.setRows(roles);
		return pm;
	}
	
	public List<Role> getListByCondition(Role role){
		return roleMapper.getListByCondition(role);
	}
	
	public Role getByCondition(Role role){
		List<Role> roles = getListByCondition(role);
		
		if(null != roles && roles.size() > 0){
			return roles.get(0);
		}
		
		return role;
	}
	
	@Override
	public int save(Role role) throws OaException{
		Role old = new Role();
		old.setCompanyId(role.getCompanyId());
		old.setUnRoleName(role.getRoleName());
		
		old = getByCondition(old); 
		if(null != old && old.getId() != null){
			throw new OaException("角色名称不能重复");
		}
		
		role.setCreateTime(new Date());
		role.setCreateUser(userService.getCurrentAccount());
		return roleMapper.save(role);
	}
	
	@Override
	public int update(Role role) throws OaException{
		Role currentRole = roleMapper.getById(role.getId());
		if(null == currentRole){
			throw new OaException("您当前修改的角色不存在");
		}else if(!role.getRoleName().equals(currentRole.getRoleName())){//如果角色名字修改过，则校验唯一性
			Role old = new Role();
			old.setCompanyId(role.getCompanyId());
			old.setUnRoleName(role.getRoleName());
			
			old = getByCondition(old); 
			if(null != old && old.getId() != null){
				throw new OaException("角色名称不能重复");
			}
		}
		
		role.setUpdateTime(new Date());
		role.setUpdateUser(userService.getCurrentAccount());
		return roleMapper.updateById(role);
	}
	
	public int deleteById(Long id) throws OaException{
		Role currentRole = roleMapper.getById(id);
		
		if(null == currentRole){
			return 1;
		}
		
		currentRole.setDelFlag(ConfigConstants.IS_YES_INTEGER);
		currentRole.setUpdateTime(new Date());
		currentRole.setUpdateUser(userService.getCurrentAccount());
		return roleMapper.updateById(currentRole);
	}


	@Override
	public int forzen(Role role) throws OaException {
		return roleMapper.updateById(role);
	}
}
