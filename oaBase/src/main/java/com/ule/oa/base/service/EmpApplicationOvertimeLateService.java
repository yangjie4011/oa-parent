package com.ule.oa.base.service;

import java.util.Map;

import com.ule.oa.base.po.EmpApplicationOvertimeLate;
import com.ule.oa.base.po.User;

/**
 * @ClassName: 晚到申请
 * @Description: 晚到申请
 * @author yangjie
 * @date 2017年6月19日
 */
public interface EmpApplicationOvertimeLateService {
	
	public Map<String,Object> save(EmpApplicationOvertimeLate userOvertimeLate,User user) throws Exception;
	
	public Map<String,Object> updateById(EmpApplicationOvertimeLate userOvertimeLate,User user) throws Exception;
	
	public EmpApplicationOvertimeLate getById(Long id);
	
	public int getEaoByEmpAndDateCount(EmpApplicationOvertimeLate userOvertimeLate);


}
