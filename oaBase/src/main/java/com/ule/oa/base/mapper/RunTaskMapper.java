package com.ule.oa.base.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.RunTask;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: RunTaskMapper
 * @Description: 流程启动记录表
 * @author mahaitao
 * @date 2017年5月27日 13:08
*/
public interface RunTaskMapper extends OaSqlMapper{

	/**
	 * 根据ID获取
	 * @param id
	 * @return
	 */
	RunTask getById(Long id);
	
	/**
	 * 获取我的协作列表
	 * @param RunTask
	 * @return
	 */
	List<RunTask> getPageList(RunTask runTask);
	
	/**
	 * 获取我的协作总数
	 * @param RunTask
	 * @return
	 */
	Integer getCount(RunTask runTask);
	
	/**
	 * 获取我的申请列表
	 * @param RunTask
	 * @return
	 */
	List<RunTask> getApplyPageList(RunTask runTask);
	
	/**
	 * 获取我的申请总数
	 * @param RunTask
	 * @return
	 */
	Integer getApplyCount(RunTask runTask);
	
	/**
	 * 保存
	 * @param RunTask
	 * @return
	 */
	Long save(RunTask runTask);
	
	/**
	 * 批量保存
	 * @param RunTask
	 */
	void batchSave(RunTask runTask);
	
	/**
	 * 更新
	 * @param id
	 * @return
	 */
	Integer updateById(RunTask runTask);
	
	/**
	 * 根据实体获取
	 * @param entityId
	 * @return
	 */
	List<RunTask> getRunTask(RunTask runTask);
	
	List<RunTask> allExaminePageList(RunTask runTask);
	
	RunTask getByRuProcdefId(@Param("ruProcdefId")Long ruProcdefId);
	
}
