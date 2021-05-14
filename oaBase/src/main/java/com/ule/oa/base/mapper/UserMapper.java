package com.ule.oa.base.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.User;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
  * @ClassName: UserMapper
  * @Description: TODO
  * @author minsheng
  * @date 2017年5月10日 下午2:58:43
 */
public interface UserMapper extends OaSqlMapper{
	
	/**
	  * save(根据员工code查询用户信息)
	  * @Title: getByCode
	  * @Description: 根据员工code查询用户信息
	  * @param user
	  * @return    设定文件
	  * User    返回类型
	  * @throws
	 */
	User getByCode(@Param("code")String code);
	
	/**
	  * save(保存用户信息)
	  * @Title: save
	  * @Description: 保存用户信息
	  * @param user
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
    int save(User user);
    
    /**
      * updateById(更新用户信息)
      * @Title: updateById
      * @Description: 更新用户信息
      * @param user
      * @return    设定文件
      * int    返回类型
      * @throws
     */
    int updateById(User user);
    
    /**
     * updateByEmployeeId(更新用户信息)
     * @Title: updateByEmployeeId
     * @Description: 更新用户信息
     * @param user
     * @return    设定文件
     * int    返回类型
     * @throws
    */
    int updateByEmployeeId(User user);
    
    /**
      * getListByCondition(根据条件获取所有用户信息)
      * @Title: getListByCondition
      * @Description: 根据条件获取所有用户信息
      * @param user
      * @return    设定文件
      * List<User>    返回类型
      * @throws
     */
    List<User> getListByCondition(User user);
	
	//更具员工ID查询登录用户
	User getByEmployeeId(@Param("employeeId")Long employeeId);

	Integer getUserManageListCount(User user);

	List<User> getUserManageList(User user);
	
	User getUserInfoById(Integer id);
	
	List<Integer> getDeptIdsByUserId(Long employeeId);

	
	List<User> getUserInfoByNameOrCode(User user);
	
	 /**
     * getDeptDataByUser(获取用户的数据权限-按部门员工)
     * @Title: getDeptDataByUser
     * @Description: 获取用户的数据权限-按部门员工
     * @param employeeId
     * @return    设定文件
     * List<Long> 返回类型
     * @throws
    */
	List<Long> getDeptDataByUser(@Param("employId")Long employeeId);
	
	
	User getLoginUser(User user);

}