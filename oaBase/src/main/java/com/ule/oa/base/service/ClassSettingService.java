package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.ClassSetting;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: ClassSettingService
  * @Description: 班次设置业务层
  * @author minsheng
  * @date 2017年7月31日 下午3:46:04
 */
public interface ClassSettingService {
	/**
	  * getList(查询所有的班次信息)
	  * @Title: getList
	  * @Description: 查询所有的班次信息
	  * @return    设定文件
	  * List<ClassSetting>    返回类型
	  * @throws
	 */
	List<ClassSetting> getList();
	
	/**
	 * @throws OaException 
	  * save(保存班次信息)
	  * @Title: save
	  * @Description: 保存班次信息
	  * @param classSetting
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer save(ClassSetting classSetting) throws OaException;
	
	/**
	  * updateById(更新班次信息)
	  * @Title: updateById
	  * @Description: 更新班次信息
	  * @param classSetting
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public Integer updateById(ClassSetting classSetting);
	
	/**
	  * getListByCondition(根据条件获取排班信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取排班信息
	  * @param classSetting
	  * @return    设定文件
	  * List<ClassSetting>    返回类型
	  * @throws
	 */
	public List<ClassSetting> getListByCondition(ClassSetting classSetting);
	
	//获取员工指定日期排班
	public ClassSetting getByEmpAndDate(ApplicationEmployeeClassDetail classDetail);
	
	//获取员工指定日期排班
	public ClassSetting getByEmpAndDate1(ApplicationEmployeeClassDetail classDetail);
	
	public ClassSetting getById(Long id);
	
	/**
	  * deleteById(删除班次信息)
	  * @Title: deleteById
	  * @Description: 更新班次信息
	  * @param classSetting
	  * @return    设定文件
	  * Integer    返回类型
	  * @throws
	 */
	public void deleteById(ClassSetting classSetting) throws OaException;

	//新增班次查询
	PageModel<ClassSetting> getReportPageList(ClassSetting classSet);

	public Integer updateClassName(ClassSetting classSet) throws OaException;
}
