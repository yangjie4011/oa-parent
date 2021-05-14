package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.ReIdentitylink;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: HiActinstTbl
 * @Description: 流程节点人员配置表
 * @author mahaitao
 * @date 2017年6月5日 14:58
*/
public interface ReIdentitylinkMapper extends OaSqlMapper{
	/**
	 * 根据ID获取
	 * @param id
	 * @return
	 */
	ReIdentitylink getById(Long id);
	
	/**
	 * 获取列表
	 * @param ReIdentitylink
	 * @return
	 */
	List<ReIdentitylink> getList(ReIdentitylink reIdentitylink);
	
	/**
	 * 获取列表
	 * @param ReIdentitylink
	 * @return
	 */
	List<ReIdentitylink> getPageList(ReIdentitylink reIdentitylink);
	
	/**
	 * 获取总数
	 * @param ReIdentitylink
	 * @return
	 */
	Integer getCount(ReIdentitylink reIdentitylink);
	
	/**
	 * 保存
	 * @param ReIdentitylink
	 * @return
	 */
	Long save(ReIdentitylink reIdentitylink);
	
	/**
	 * 批量保存
	 * @param ReIdentitylink
	 */
	void batchSave(ReIdentitylink reIdentitylink);
	
	/**
	 * 更新
	 * @param id
	 * @return
	 */
	Integer updateById(ReIdentitylink reIdentitylink);
}
