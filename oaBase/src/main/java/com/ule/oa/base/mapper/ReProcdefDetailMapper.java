package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.ReProcdefDetail;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: ReProcdefDetailMapper
 * @Description: 业务流程定义数据明细表
 * @author mahaitao
 * @date 2017年5月27日 10:08
*/
public interface ReProcdefDetailMapper extends OaSqlMapper{

	/**
	 * 根据ID获取业务流程定义数据明细
	 * @param id
	 * @return
	 */
	ReProcdefDetail getById(Long id);
	
	/**
	 * 获取业务流程定义数据明细列表分页查询
	 * @param ReProcdefDetail
	 * @return
	 */
	List<ReProcdefDetail> getPageList(ReProcdefDetail reProcdefDetail);
	
	/**
	 * 获取业务流程定义数据明细列表
	 * @param ReProcdefDetail
	 * @return
	 */
	List<ReProcdefDetail> getList(ReProcdefDetail reProcdefDetail);
	
	/**
	 * 获取业务流程定义数据明细总数分页查询
	 * @param ReProcdefDetail
	 * @return
	 */
	Integer getCount(ReProcdefDetail reProcdefDetail);
	
	/**
	 * 保存业务流程定义数据明细
	 * @param ReProcdefDetail
	 * @return
	 */
	Long save(ReProcdefDetail reProcdefDetail);
	
	/**
	 * 批量保存业务流程定义数据明细
	 * @param ReProcdefDetail
	 */
	void batchSave(ReProcdefDetail reProcdefDetail);
	
	/**
	 * 更新业务流程定义数据明细
	 * @param id
	 * @return
	 */
	Integer updateById(Long id);
}
