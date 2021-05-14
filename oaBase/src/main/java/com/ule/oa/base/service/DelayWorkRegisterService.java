package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ule.oa.base.po.DelayWorkRegister;
import com.ule.oa.common.exception.OaException;

public interface DelayWorkRegisterService {
	
	//匹配延时工作
	public void macthDelayWork(DelayWorkRegister match) throws OaException;

	Map<String, Object> getDelayWorkPage(Long departId, String leaderName, String month, String empCode, String empCnName, Integer page, Integer rows);

	Map<String, Object> getDelayWorkDetail(Long empId, Date delayDate) throws OaException;
	
	//查询当前时间以前所有未匹配考勤的登记数据
	List<DelayWorkRegister> getUnMatchedListByDelayDate(Date delayDate);
	
	//保存延时工作登记
	public Map<String, Object> commitDelayWorkDetail(DelayWorkRegister delayWorkRegister) throws OaException;

	public Map<String, Object> confirmDelayWorkDetail(DelayWorkRegister delayWorkRegister) throws OaException;

	public Map<String, Object> deleteDelayWorkDetail(DelayWorkRegister delayWorkRegister) throws OaException;

	public HSSFWorkbook exportDelayWorkPage(Long departId, String leaderName, String date, String empCode, String empCnName);
	
	public Map<String,Object> getActualHours(Long employeeId,Date delayDate);

	public Map<String, Object> toReview(Long empId, String month);

}
