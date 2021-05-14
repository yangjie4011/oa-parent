package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.RabcRole;
import com.ule.oa.base.po.User;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

public interface RabcUserRoleService {

	void delUserRole(Long roleId) throws OaException;

	void updateUserById(User user) throws OaException;

	PageModel<User> getPageList(User user);

	User getUserById(Integer id);

	void modifyUserByIds(User user) throws OaException;

	Map<String, Object> getRoleInfobyDepartIdAndUserId(Long departId,
			Long userId);

	List<RabcRole> getDepartRoleListByUserId(Long userId);

	List<Integer> getDeptIdsByUser(Long employeeId);

	void forzenUserById(User user) throws OaException;
	
	List<Depart> getDepartByUser(Long employeeId);
	
	void delUserRole(Long employeeId,Long departId,String roleName,String updateUser,Date updateTime);
	
	void initUserRole(Long employeeId,Long departId,String roleName,String createUser,Date createTime);
	
	void delByRoleId(Long roleId,Date updateTime,String updateUser);

}
