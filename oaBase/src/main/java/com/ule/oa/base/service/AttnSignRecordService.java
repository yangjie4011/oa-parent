package com.ule.oa.base.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.AttnSignRecord;
import com.ule.oa.base.po.TransNormal;

public interface AttnSignRecordService {

	Integer getMaxAttnId();

	Integer saveTransToAttnSignBatch(List<TransNormal> list, Long uleId, Date currentTime, String createUser);
	
	List<AttnSignRecord> getSignRecordList(AttnSignRecord condition);
	
	List<AttnSignRecord> getListBefore9(AttnSignRecord condition);
	
	Integer save(AttnSignRecord signRecord);

	/**
	  * 员工打卡明细报表
	  * @Title: getEmpSignRecordReport
	  * @param startTime
	  * @param endTime
	  * @param departId
	  * @return    设定文件
	  * List<Map<String,Object>>    返回类型
	  * @throws
	 */
	List<Map<String, Object>> getEmpSignRecordReport(Date startTime,
			Date endTime, Long departId,List<Long> empTypeIdList);
	
	/**
	 * 实时打卡明细报表
	 * @param startTime
	 * @param endTime
	 * @param departId
	 * @param empTypeIdList
	 * @return
	 */
	List<Map<String, Object>> getSSDKMXReport(Date startTime,
			Date endTime, Long departId,List<Long> empTypeIdList);
	
	/**
	 * @throws Exception 
	  * signRecordExMsgRemind(员工考勤异常消息提醒-排班员工不提醒)
	  * @Title: signRecordExMsgRemind
	  * @Description: 员工考勤异常消息提醒-排班员工不提醒
	  * void    返回类型
	  * @throws
	 */
	public void signRecordExMsgRemind();
	
	/**
	 * @throws Exception 
	  * absenteeismAlert(员工旷工提醒)
	  * @Title: signRecordExMsgRemind
	  * @Description: 员工旷工提醒 超三天 发邮件提醒
	  * void    返回类型
	  * @throws
	 */
	public void absenteeismAlert();
	
	public Map<String,Object> locationCheckIn(String ip,String locationResult);

}
