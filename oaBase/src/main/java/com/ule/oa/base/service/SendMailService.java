package com.ule.oa.base.service;

import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.SendMail;
import com.ule.oa.common.exception.OaException;

public interface SendMailService {
	/**
	  * save
	  * @Title: save
	  * @Description: 保存oa发送邮件信息
	  * @param model    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void save(SendMail model);
	/**
	 * getById
	 * @param id
	 * @return
	 */
	public SendMail getById(Long id); 
	
	/**
	  * updateById
	  * @Title: updateById
	  * @Description: 根据主键修改邮件信息
	  * @param sendMail
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(SendMail sendMail);
	
	/**
	  * getListByCondition
	  * @Title: getListByCondition
	  * @Description: 根据条件获取邮件信息
	  * @param sendMail
	  * @return    设定文件
	  * List<SendMail>    返回类型
	  * @throws
	 */
	public List<SendMail> getListByCondition(SendMail sendMail);
	
	/**
	 * oa发送邮件
	 * @param userName
	 * @param password
	 * @param sendMail
	 * @return
	 * @throws OaException
	 */
	public Map<String, Object> sendMail(String userName, String password, SendMail sendMail);
	
	/**
	 * 定时发审批邮件
	 * @throws OaException
	 */
	public void definiteSendMail();
	
	/**
	 * 批量新增
	 * @param model
	 */
	void batchSave(List<SendMail> model);
	
	/**
	 * 任务审批发邮件
	 * @param email
	 * @param processName
	 * @param commentType
	 */
	void sendCommentMail(String email, String processName,String taskName, Integer commentType);

	public void sendTaskEmail(List<Long> empIds,String taskName,String departName,String applyName) throws OaException;
	
	/**
	 * 定时发邮件
	 */
	public void sendEmail() throws Exception;
	
}
