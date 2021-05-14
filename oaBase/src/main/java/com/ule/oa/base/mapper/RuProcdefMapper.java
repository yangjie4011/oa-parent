package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.RuProcdef;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: RunTaskMapper
 * @Description: 流程待办表
 * @author mahaitao
 * @date 2017年5月27日 13:08
*/
public interface RuProcdefMapper extends OaSqlMapper{

	/**
	 * 根据ID获取待办事项
	 * @param id
	 * @return
	 */
	RuProcdef getById(Long id);
	
	/**
	 * 获取待办事项列表
	 * @param ruProcdef
	 * @return
	 */
	List<RuProcdef> getList(RuProcdef ruProcdef);
	
	/**
	 * 分页查询待办事项列表
	 * @param ruProcdef
	 * @return
	 */
	List<RuProcdef> getPageList(RuProcdef ruProcdef);
	
	/**
	 * 获取待办事项总数
	 * @param ruProcdef
	 * @return
	 */
	Integer getCount(RuProcdef ruProcdef);
	
	/**
	 * 保存待办事项
	 * @param ruProcdef
	 * @return
	 */
	Long save(RuProcdef ruProcdef);
	
	/**
	 * 批量保存待办事项
	 * @param ruProcdef
	 */
	void batchSave(List<RuProcdef> ruProcdef);
	
	/**
	 * 更新代办事项
	 * @param id
	 * @return
	 */
	Integer updateById(RuProcdef ruProcdef);
	
	/**
	 * 获取查看页面
	 * @param ruTaskId
	 * @return
	 */
	RuProcdef getUrlByRuTaskId(Long ruTaskId);
	
	/**
	 * 查询运行流程节点未完成待办数
	 * @param ruProcdef
	 * @return
	 */
	Integer getNodeCodeCount(RuProcdef ruProcdef);
	
	/**
	 * 更新普通节点其他人员状态为已删除
	 * @param ruProcdef
	 * @return
	 */
	Integer updateByNodeCode(RuProcdef ruProcdef);
	
	/**
	 * 查询运行流程模块未完成待办数
	 * @param ruProcdef
	 * @return
	 */
	Integer getNodeModuleCount(RuProcdef ruProcdef);
	
	/**
	 * 指定流程改善消息
	 * @param ruProcdef
	 * @return
	 */
	List<RuProcdef> getMsgList(RuProcdef ruProcdef);
	
	RuProcdef getRunTaskByCodeAndEntry(RuProcdef runTask);
	
	List<RuProcdef> getRunTaskListByCodeAndEntry(RuProcdef runTask);
}
