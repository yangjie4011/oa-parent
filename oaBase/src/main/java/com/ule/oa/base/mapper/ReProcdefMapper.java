package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.ReProcdef;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: ReProcdefMapper
 * @Description: 业务流程定义数据表
 * @author mahaitao
 * @date 2017年5月27日 13:08
*/
public interface ReProcdefMapper extends OaSqlMapper{

	/**
	 * 根据ID获取
	 * @param id
	 * @return
	 */
	ReProcdef getById(Long id);
	
	/**
	 * 根据code获取
	 * @param code
	 * @return
	 */
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
	 * 根据条件查询
	 * @param reProcdef
	 * @return
	 */
	List<ReProcdef> getListByCondition(ReProcdef reProcdef);
	
}
