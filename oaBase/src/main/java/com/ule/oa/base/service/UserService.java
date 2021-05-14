package com.ule.oa.base.service;

import java.util.List;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.po.User;
import com.ule.oa.common.exception.OaException;

/**
  * @ClassName: UserService
  * @Description: 用户信息业务层接口
  * @author minsheng
  * @date 2017年5月10日 下午2:56:25
 */
public interface UserService {
	
	/**
	  * save(根据员工code查询用户信息)
	  * @Title: getByCode
	  * @Description: 根据员工code查询用户信息
	  * @param user
	  * @return    设定文件
	  * User    返回类型
	  * @throws
	 */
	User getByCode(String code);
	
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
     * getListByCondition(根据条件获取所有用户信息)
     * @Title: getListByCondition
     * @Description: 根据条件获取所有用户信息
     * @param user
     * @return    设定文件
     * List<User>    返回类型
     * @throws
    */
   List<User> getListByCondition(User user);
   
   /**
    * getByCondition(根据条件获取用户信息)
    * @Title: getByCondition
    * @Description: 根据条件获取用户信息
    * @param user
    * @return    设定文件
    * User    返回类型
    * @throws
   */
  public User getByCondition(User user);
  
  /**
   * getCurrentUser(获取当前登录用户)
   * @Title: getCurrentUser
   * @Description: 获取当前登录用户
   * @return    设定文件
   * User    返回类型
   * @throws
  */
  public User getCurrentUser();
  
  /**
   * getCurrentAccount(这里用一句话描述这个方法的作用)
   * @Title: getCurrentAccount
   * @Description: 获取当前登录用户名
   * @return    设定文件
   * String    返回类型
   * @throws
  */
 public String getCurrentAccount();
 
 /**
   * getEmpPic(获取用户头像)
   * @Title: getEmpPic
   * @Description: 获取用户头像
   * @return    设定文件
   * String    返回类型
   * @throws
  */
 public String getEmpPicByEmp(Employee emp);
 
 /**
  * getEmpPic(获取用户头像)
  * @Title: getEmpPic
  * @Description: 获取用户头像
  * @return    设定文件
  * String    返回类型
  * @throws
 */
 public String getEmpPicByEmp(EmployeeApp emp);
 
 public String getEmpPicByEmp(String empPic,String email);
 
	/**
	  * @throws OaException 
	   * checkUser(邮乐用户验证通行证帐号密码)
	   * @Title: checkUser
	   * @Description: 邮乐用户验证通行证帐号密码
	   * @param userName
	   * @param password
	   * @return
	   * @throws UleException    设定文件
	   * User    返回类型
	   * @throws
   */
    public User checkUser(String userName,String password) throws OaException;

 	boolean haveApprovalAuthority();
 
	User getUserByEmployeeId(Long employeeId);
	
	User getLoginUser(User user);
}
