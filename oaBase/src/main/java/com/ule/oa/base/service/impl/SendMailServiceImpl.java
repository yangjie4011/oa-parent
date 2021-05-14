package com.ule.oa.base.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Lists;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.mapper.SendMailMapper;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.base.util.SendMailUtil;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.MD5Encoder;
import net.sf.json.JSONObject;

@Service
public class SendMailServiceImpl implements SendMailService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SendMailMapper sendMailMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private ConfigCacheManager configCacheManager;
	@Autowired
	private EmployeeService employeeService;
	
	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	
	@Resource(name="threadPoolTaskExecutor")
	private ThreadPoolTaskExecutor pool;
	
	private class SendMailRun implements Runnable {
		
		private String userName;
		private String password;
		private SendMail sendMail;
		
		SendMailRun(String userName, String password, SendMail sendMail) {
			this.userName = userName;
			this.password = password;
			this.sendMail = sendMail;
		}
		
		public void run() {
			sendMail(userName, password, sendMail);
		}
	}
	
	private class SendMailRun1 implements Runnable {
		
		private String receiver;
		private String subject;
		private String text;
		
		SendMailRun1(String receiver, String subject, String text) {
			this.receiver = receiver;
			this.subject = subject;
			this.text = text;
		}
		
		public void run() {
			try {
				SendMailUtil.sendMail(receiver, subject, text);
			} catch (MessagingException e) {
			} catch (IOException e) {
			}
		}
	}
	
	@Override
	public void save(SendMail model) {
		Date currentTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		
		User user = userService.getCurrentUser();
		
		model.setSendStatus(SendMail.SEND_STATUS_NO);
		model.setDelFlag(SendMail.STATUS_NORMAL);
		model.setVersion(0L); 
		model.setCreateUser(user==null?"":user.getEmployee().getCnName());
		model.setCreateTime(currentTime);
		sendMailMapper.save(model);
	}
	
	@Override
	public int updateById(SendMail model) {
		return sendMailMapper.updateById(model);
	}

	@Override
	public List<SendMail> getListByCondition(SendMail model) {
		return sendMailMapper.getListByCondition(model);
	}

	@Override
	public SendMail getById(Long id) {
		return sendMailMapper.getById(id);
	}
	
	@Override
	public void definiteSendMail() {
		SendMail sendMail = new SendMail();
		sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
		List<SendMail> sendMailList = sendMailMapper.getListByCondition(sendMail);
		if(sendMailList != null && sendMailList.size() > 0) {
			for (SendMail sm : sendMailList) {
				sendMail(ConfigConstants.OA_SENDMAIL_USERNAME, ConfigConstants.OA_SENDMAIL_PASSWORD, sm);
			}
		}
	}

	@Override
	public Map<String, Object> sendMail(String userName, String password, SendMail sendMail) {
		Map<String, Object> returnMap = new HashMap<String,Object>();
		Date currentTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		Map<String, String> requestMap = new HashMap<String,String>();
		try {
			if(sendMail != null) {
				requestMap.put("userName", userName == null ? "" : userName);
				requestMap.put("password", password == null ? "" : password);
				requestMap.put("to", sendMail.getReceiver() == null ? "" : sendMail.getReceiver());
				requestMap.put("cc", sendMail.getCarbonCopy() == null ? "" : sendMail.getCarbonCopy());
				requestMap.put("bcc", sendMail.getBlindCarbonCopy() == null ? "" : sendMail.getBlindCarbonCopy());
				requestMap.put("subject", sendMail.getSubject() == null ? "" : sendMail.getSubject());
				if(sendMail.getIsSave() != null && sendMail.getIsSave().intValue() == SendMail.IS_SAVE_YES.intValue()) {
					requestMap.put("savetosendbox", "true");
				} else {
					requestMap.put("savetosendbox", "false");
				}
				if(sendMail.getIsReceipt() != null && sendMail.getIsReceipt().intValue() == SendMail.IS_RECEIPT_YES.intValue()) {
					requestMap.put("readreceipt", "true");
				} else {
					requestMap.put("readreceipt", "false");
				}
				if(sendMail.getIsPriority() != null && sendMail.getIsPriority().intValue() == SendMail.IS_PRIORITY_YES.intValue()) {
					requestMap.put("priority", "true");
				} else {
					requestMap.put("priority", "false");
				}
				if(sendMail.getOaMail() != null && sendMail.getOaMail().intValue() == 1) {
					requestMap.put("oamail", "{\"type\":\"oa\",\"url\":\"" + sendMail.getText() + "\"}");
				} else {
					requestMap.put("oamail", "0");
				}
				requestMap.put("text", sendMail.getText() == null ? "" : sendMail.getText());
				requestMap.put("nickName", sendMail.getNickName() == null ? "" : sendMail.getNickName());
				logger.info("sendMail 调用邮件接口sendMail pull start...");
				logger.info("sendMail 调用邮件接口传入的参数：requestMap：" + requestMap);
				long start = System.currentTimeMillis();
				
				HttpRequest req = new HttpRequest.Builder().url(ConfigConstants.OA_SENDMAIL_URL).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
	    				ContentCoverter.formConvertAsString(requestMap)).build();
	    		HttpResponse rep = client.sendRequest(req);
				String response = rep.fullBody();
				
			//	logger.info("sendMail 调用邮件接口返回值：" + response);
				
				SendMail sm = new SendMail();
				sm.setId(sendMail.getId());
				sm.setSendStatus(SendMail.SEND_STATUS_YES); //更新发送状态为已发送
				sm.setUpdateTime(currentTime);
				sm.setVersion(sendMail.getVersion());
				sm.setUpdateUser("SYSTEM");
				sendMailMapper.updateById(sm);
				
				long end = System.currentTimeMillis();
				logger.info("sendMail 调用邮件接口sendMail invoked end and used time is:"+ (end - start));
				//if(req != null) {
					JSONObject reqJson = JSONObject.fromObject(response);
					String code = reqJson.get("code") + "";
					String codemsg = reqJson.get("codemsg") + "";
					logger.info("sendMail:email="+sendMail.getReceiver()+"返回结果 :code="+code+";msg="+codemsg);
					//if("001".equals(code)) {
						
					//} 
					returnMap.put("code", code);
					returnMap.put("codemsg", codemsg);
				//}
			}
		} catch (Exception e) {
			logger.info("发送消息失败:id" + sendMail.getId());
		} 
		return returnMap;
	}
	
	@Override
	public void batchSave(List<SendMail> model) {
		//XXX:定时发邮件
		for (SendMail sendMail : model) {
			this.save(sendMail);
		}
	}
	@Override
	public void sendCommentMail(String email , String processName ,String taskName, Integer commentType) {
		String subject = "";
		String text = "";
		if(commentType.intValue()==ConfigConstants.BACK_STATUS.intValue()) {
			return ;
		}else if(commentType.intValue()==ConfigConstants.PASS_STATUS.intValue()) {
			subject = "审批通过通知";
			text = "您的【"+processName+"】审批已完成了哦！";
		}else if(commentType.intValue()==ConfigConstants.REFUSE_STATUS.intValue()){
			subject = "审批拒绝通知";
			text = "很遗憾，您的【"+processName+"】审批已被拒绝！";
		}else {
			subject = "审批通知";
			text = "您的【"+processName+"】已被["+taskName+"]审批！";
		}
		List<SendMail> sendMailList = new ArrayList<SendMail>();
		SendMail sendMail = new SendMail();
		sendMail.setReceiver(email);
		sendMail.setSubject(subject);
		sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
		sendMail.setText(text);
		sendMail.setOaMail(SendMail.OA_MAIL_P);
		sendMailList.add(sendMail);
		batchSave(sendMailList);
	}

	@Override
	public void sendTaskEmail(List<Long> empIds,String taskName,String departName,String applyName) throws OaException {
		List<Employee> emps = new ArrayList<>();
		if(empIds!=null&&empIds.size()>0){
			emps = employeeService.getByEmpIdList(empIds);
		}
		List<SendMail> model = Lists.newArrayList();
		String subject = taskName+"-"+departName+"-"+applyName;
		for (Employee emp : emps) {
			if(emp.getEmail()!=null) {
			SendMail sendMail = new SendMail();
			sendMail.setReceiver(emp.getEmail());
			sendMail.setSubject(subject);
			sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
			sendMail.setText("Dear "+emp.getCnName()+"："+"<br/>"+"您已收到 "+applyName+" 在MO系统中提交的"+taskName+"，请及时处理。");
			sendMail.setOaMail(SendMail.OA_MAIL_P);
			model.add(sendMail);
			}
		}
		batchSave(model);
	}

	@Override
	public void sendEmail() throws Exception {
		SendMail model = new SendMail();
		model.setSendStatus(0);
		model.setCreateTimeStart(DateUtils.addMonth(new Date(), -3));
		List<SendMail> emails = sendMailMapper.getListByCondition(model);
		if(null == emails ) {
			logger.info("当前时间["+DateUtils.format(new Date())+"]没有需要发送邮件");
			return;
		}
		long startTime = System.currentTimeMillis()/1000;
		logger.info("发邮件定时开始:"+"此次发送"+emails.size()+"封邮件。");
		for (SendMail sendMail : emails) {
			try{
				if(sendMail.getOaMail().intValue() == 1) { 
					pool.execute(new SendMailRun(ConfigConstants.OA_SENDMAIL_USERNAME, ConfigConstants.OA_SENDMAIL_PASSWORD, sendMail));
				}else {
					sendMail.setSendStatus(1);
					sendMail.setUpdateTime(new Date());
					this.updateById(sendMail);	
					pool.execute(new SendMailRun1(sendMail.getReceiver(), sendMail.getSubject(), sendMail.getText()));
				}
			}catch(Exception e){
				logger.error("邮件发送失败："+sendMail.getId()+";msg="+e.getMessage());
			}
			
		}
		long endTime = System.currentTimeMillis()/1000;
		logger.info("发送"+emails.size()+"封邮件成功,用时"+(endTime-startTime)+"秒");
	}
}
