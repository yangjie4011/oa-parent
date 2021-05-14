package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.ReProcdef;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: ReProcdefMapper
 * @Description: 业务流程定义数据表
 * @author mahaitao
 * @date 2017年6月01日 09:08
*/
public interface ReProcdefService {

	/**
	 * 根据ID获取
	 * @param id
	 * @return
	 */
	ReProcdef getById(Long id);
	
	ReProcdef getByCode(String code);
	
	/**
	 * 获取列表
	 * @param ReProcdef
	 * @return
	 */
	List<ReProcdef> getPageList(ReProcdef reProcdef);
	
	/**
	 * 获取总数
	 * @param ReProcdef
	 * @return
	 */
	Integer getCount(ReProcdef reProcdef);
	
	/**
	 * 保存
	 * @param ReProcdef
	 * @return
	 */
	Long save(ReProcdef reProcdef);
	
	/**
	 * 批量保存
	 * @param ReProcdef
	 */
	void batchSave(ReProcdef reProcdef);
	
	/**
	 * 更新
	 * @param id
	 * @return
	 */
	Integer updateById(Long id);
	
	/**
	 * 分页查询
	 * @param reProcdef
	 * @return
	 */
	public PageModel<ReProcdef> getByPagenation(ReProcdef reProcdef);
	
	/**
	 * 根据条件查询
	 * @param reProcdef
	 * @return
	 */
	List<ReProcdef> getListByCondition(ReProcdef reProcdef);
}
