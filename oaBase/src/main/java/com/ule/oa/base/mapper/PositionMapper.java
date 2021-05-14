package com.ule.oa.base.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ule.oa.base.po.DepartPosition;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface PositionMapper extends OaSqlMapper{
	Position getById(Long id);
	
	/**
	  * save(保存职位信息)
	  * @Title: save
	  * @Description: 保存职位信息
	  * @param record
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	int save(Position position);

	/**
	  * getListByCondition(根据条件查询职位信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件查询职位信息
	  * @param id
	  * @return    设定文件
	  * List<Position>    返回类型
	  * @throws
	 */
	List<Position> getListByCondition(Position position);
	
	/**
	  * getTreeList(获得职位树)
	  * @Title: getTreeList
	  * @Description: 获得职位树
	  * @param position
	  * @return    设定文件
	  * List<Position>    返回类型
	  * @throws
	 */
	List<Position> getTreeList(Position position);
	
	/**
	  * getPageList(分页查询部门职位信息)
	  * @Title: getPageList
	  * @Description: 分页查询部门职位信息
	  * @param DepartPosition
	  * @return    设定文件
	  * List<Position>    返回类型
	  * @throws
	 */
	List<Position> getPageList(DepartPosition departPosition);
	
	/**
	  * getCountByCompAndDepart(查询部门职位总记录数)
	  * @Title: getCountByCompAndDepart
	  * @Description: 查询部门职位总记录数
	  * @param position
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	Integer getCount(Position position);
	
	/**
	  * updateById(根据主键更新职位信息)
	  * @Title: updateById
	  * @Description: 根据主键更新职位信息
	  * @param record
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	int updateById(Position position);
	
	/**
	  * getByCondition(根据条件查询职位信息)
	  * @Title: getByCondition
	  * @Description: 根据条件查询职位信息
	  * @param position
	  * @return    设定文件
	  * List<Position>    返回类型
	  * @throws
	 */
	Position getByCondition(Position position);
	
	Position getByEmpId(@Param("employeeId")Long employeeId);
}