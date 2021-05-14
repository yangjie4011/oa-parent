package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.Position;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: PositionService
  * @Description: 职位管理
  * @author minsheng
  * @date 2017年5月12日 上午10:57:05
 */
public interface PositionService {
	public Position getById(Long id);
	
	/**
	  * savePosition(保存职位信息)
	  * @Title: savePosition
	  * @Description: 保存职位信息
	  * @param position
	  * @throws OaException    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void savePosition(Position position) throws OaException;
	
	/**
	  * save(保存职位信息)
	  * @Title: save
	  * @Description: 保存职位信息
	  * @param record
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int save(Position position) throws OaException;
	
	/**
	 * @throws Exception 
	  * delete(删除职位信息)
	  * @Title: delete
	  * @Description: 删除职位信息
	  * @param position
	  * void    返回类型
	  * @throws
	 */
	public void delete(Position position) throws Exception;

	/**
	  * getListByCondition(根据条件查询职位信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件查询职位信息
	  * @param id
	  * @return    设定文件
	  * List<Position>    返回类型
	  * @throws
	 */
	public List<Position> getListByCondition(Position position);
	
	
	public Map<String,List<String>> getPosSeqAndLv(Position position);
	
	/**
	  * getByCondition(根据条件查询职位信息)
	  * @Title: getByCondition
	  * @Description: 根据条件查询职位信息
	  * @param id
	  * @return    设定文件
	  * List<Position>    返回类型
	  * @throws
	 */
	public Position getByCondition(Position position);

	/**
	 * @throws Exception 
	  * updateById(根据主键更新职位信息)
	  * @Title: updateById
	  * @Description: 根据主键更新职位信息
	  * @param record
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(Position position) throws Exception;
	
	/**
	  * getPageList(分页查询职位信息)
	  * @Title: getPageList
	  * @Description: 分页查询职位信息
	  * @param position
	  * @return    设定文件
	  * PageModel<Position>    返回类型
	  * @throws
	 */
	public PageModel<Position> getPageList(Position position);
	
	/**
	 * @throws Exception 
	 * @return 
	  * saveOrUpdate(保存或更新职位信息)
	  * @Title: saveOrUpdate
	  * @Description: 保存或更新职位信息
	  * @param position
	  * @return    设定文件
	  * void    返回类型
	  * @throws
	 */
	public int update(Position position) throws Exception;
	
	/**
	  * getTreeList(获得部门树)
	  * @Title: getTreeList
	  * @Description: 获得部门树
	  * @param position
	  * @return    设定文件
	  * List<Map<String, String>>    返回类型
	  * @throws
	 */
	public List<Map<String, String>> getTreeList(Position position);
	
	public Position getCurrentPosition() throws OaException;
	
	public Position getByEmpId(Long employeeId);
	
	public List<String> getSeqList(String positionType);
	
	public List<String> getLevelList(String positionType);
}
