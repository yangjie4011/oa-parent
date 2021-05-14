package com.ule.oa.base.service;

import com.ule.oa.base.po.Role;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: RoleService
  * @Description: 角色管理业务层接口
  * @author minsheng
  * @date 2017年12月6日 下午3:28:10
 */
public interface RoleService {
	public PageModel<Role> getPageList(Role role);
	public Role getByCondition(Role role);
	public int save(Role role) throws OaException;
	public int update(Role role) throws OaException;
	public int deleteById(Long id) throws OaException;
	public int forzen(Role role) throws OaException;
	
}
