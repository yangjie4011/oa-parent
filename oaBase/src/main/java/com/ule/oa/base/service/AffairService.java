package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.Affair;

/**
  * @ClassName: AffairService
  * @Description: 日程信息业务层接口
  * @author wufei
  * @date 2017年5月17日 上午9:56:25
 */
public interface AffairService {
	
	/**
	 * 根据条件查询日程信息
	 * @param affair
	 * @return
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
