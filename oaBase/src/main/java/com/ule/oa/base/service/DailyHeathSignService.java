package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.DailyHeathSignTbl;

/**
 * 每日健康打卡
 * @author yangjie
 *
 */
public interface DailyHeathSignService {
	
	Map<String,Object> save(HttpServletRequest request,User user) throws Exception;
	
	List<DailyHeathSignTbl> getByEmployeeIdAndSignDate(Long employeeId,Integer type,Date signDate);
	
	/**
	 * （to员工本人抄送直接主管直至部门负责人）
	 */
	void sendMailToNoSignEmp();
	
	/**
	 * 部门负责人和ulehr@ule.com，技术开发部仅为6个总监副总监)
	 */
	void sendMailToLeaderOfNoSignEmp();
	
	public HSSFWorkbook exportSignDataByDate(Date signDate);

}
