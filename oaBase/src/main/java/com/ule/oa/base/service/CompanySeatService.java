package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.CompanySeat;


/**
 * 
  * @ClassName: CompanySeatService
  * @Description: 公司座位信息业务层
  * @author jiwenhang
  * @date 2017年5月23日 下午3:17:31
 */
public interface CompanySeatService {
	
	List<CompanySeat> getListByCondition(CompanySeat model);

}
