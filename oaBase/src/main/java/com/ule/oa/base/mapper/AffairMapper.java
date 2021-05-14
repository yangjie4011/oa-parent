package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.Affair;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
  * @ClassName: AffairMapper
  * @Description: TODO
  * @author wufei
  * @date 2017年5月17日 上午9:56:25
 */
public interface AffairMapper extends OaSqlMapper{
	
	/**
     * getListByCondition(根据条件获取所有日程信息)
     * @Title: getListByCondition
     * @Description: 根据条件获取所有用户信息
     * @param affair
     * @return    设定文件
     * List<Affair>    返回类型
     * @throws
    */
   List<Affair> getListByCondition(Affair affair);
   
	/**
	  * save(保存日程信息)
	  * @Title: save
	  * @Description: 保存日程信息
	  * @param affair
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
    int save(Affair affair);
    
    /**
      * updateById(更新日程信息)
      * @Title: updateById
      * @Description: 更新日程信息
      * @param affair
      * @return    设定文件
      * int    返回类型
      * @throws
     */
    int updateById(Affair affair);
    
    
}