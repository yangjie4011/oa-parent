package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 我的消息
 * @Description: 我的消息
 * @author yangjie
 * @date 2017年6月8日
 */
public interface EmpMsgService {
	
	public int save(EmpMsg userMsg);
	
	public int updateById(EmpMsg userMsg);
	
	public List<EmpMsg> getListByCondition(EmpMsg userMsg);
	
	public PageModel<EmpMsg> getPageList(EmpMsg userMsg);
	
	public PageModel<EmpMsg> getList(EmpMsg userMsg);
	
	public Integer getCount(EmpMsg userMsg);

}
