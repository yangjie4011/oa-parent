package com.ule.oa.base.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.MD5Encoder;

public class SendMailUtil {
	
	protected final static Logger logger = LoggerFactory.getLogger(SendMailUtil.class);
	private final static String EMAIL_HOST_ULE = (String)CustomPropertyPlaceholderConfigurer.getProperty("uleOa.sendMail.sendService");
	private final static Integer EMAIL_PORT_ULE = 25;
	private final static String EMAIL_AUTH_PRO_ULE = "mail.smtp.auth";
	private final static String EMAIL_AUTH_PRO_YES = "true";
	
	public static void main(String[] args) throws Exception {
//		List<Object[]> datas = new ArrayList<Object[]>();
//		String[] titles1 = { "11,223,44" };
//		datas.add(titles1);
//		String[] titles = { "11,223,44" };
//		HSSFWorkbook hSSFWorkbook = ExcelUtil.exportExcel(datas, titles,"workbook.xls");
//		sendMail(hSSFWorkbook,"jiwenhang@ule.com","邮件主题","附件名称");
		sendMail("jiwenhang@ule.com","出差流程-技术开发部-王文灿","http://oa.uletm.com/oaWeb/runTask/handle.htm?value=aa4e8a5db5457b9faf9577081639f946&taskId=847603&assigneeId=1656");
		sendMail("jiwenhang@ule.com","出差流程-技术开发部-王文灿","http://oa.uletm.com/oaWeb/runTask/handle.htm?value=aa4e8a5db5457b9faf9577081639f946&taskId=847603&assigneeId=1656");
	}
	
	public static void sendNormalMail(String content,String email,String cnName,String subject) throws Exception{
			try {
				if(StringUtils.isNotBlank(email)){
					logger.info("员工{}发送"+ subject +"邮件,邮箱{}开始...",cnName,email);
					sendMail(email, subject, content);
					logger.info("员工{}发送"+ subject +"邮件,邮箱{}结束!!!",cnName,email);
				}else{
					logger.info("员工{}邮箱帐号为空，不发送"+ subject +"邮件",cnName);
				}
				
			} catch (Exception e) {
				logger.error("员工{}"+ subject +"邮件发送失败,失败原因={}",cnName,e.getMessage());
				
				try {
					Thread.sleep(500);
				} catch (Exception e1) {
					logger.error("休眠500毫秒失败,原因={}",e1.getMessage());
				}
				
			}
	}

	/**
	 * 邮件发送列数超过255的workbook
	 * @param xSSFWorkbook
	 * @param to
	 * @param subject
	 * @param sttachmentName
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static void sendMailForLargeColWorkbook(XSSFWorkbook xSSFWorkbook, String to,String subject,String sttachmentName) throws MessagingException, IOException	{
		JavaMailSenderImpl senderMail = new JavaMailSenderImpl();

		// 设定 Mail Server
		senderMail.setHost(EMAIL_HOST_ULE);
		senderMail.setPort(EMAIL_PORT_ULE);

		Properties prop = new Properties();
		prop.setProperty(EMAIL_AUTH_PRO_ULE, EMAIL_AUTH_PRO_YES);
		// SMTP验证时，需要用户名和密码
		senderMail.setUsername(ConfigConstants.OA_SENDMAIL_USERNAME);
		senderMail.setPassword(ConfigConstants.OA_SENDMAIL_PASSWORD);
		senderMail.setJavaMailProperties(prop); // 如果要密码验证,这里必须加,不然会报553错误

		// 发送HTML格式的邮件
		// 建立邮件信息，可发送HTML格式
		MimeMessage mimeMessage = senderMail.createMimeMessage(); // MimeMessage-->java的
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true, "UTF-8"); // MimeMessageHelper-->spring的 不加后面两个参数会乱码

		// 设置收件人，主题，内容
		messageHelper.setSubject(subject);
		messageHelper.setFrom(ConfigConstants.OA_SENDMAIL_USERNAME);
		messageHelper.setTo(to);

		StringBuffer str = new StringBuffer();
		str.append("<html><head></head><body><h1>" + sttachmentName + "</h1></body></html>");
		messageHelper.setText(str.toString(), true); // 为true-->发送转义HTML
		String fileName = ConfigConstants.OA_RSAKEY  + formatPath(MD5Encoder.md5Hex(DateUtils.getNow())) + ".xls";
		
		FileOutputStream fileOut = null;  
		
        fileOut = new FileOutputStream(fileName);  
        xSSFWorkbook.write(fileOut);
        FileSystemResource file = new FileSystemResource(new File(fileName));
		messageHelper.addAttachment(sttachmentName+" .xls",file);
		senderMail.send(mimeMessage); // 这个是发送带附件的
        if(fileOut != null){  
                fileOut.close();  
        }  
        File delfile = new File(fileName);
        if(!delfile.delete()) {
        	
        }
	}
	
	/**
	  * sendMail(发送普通邮件-不带附件)
	  * @Title: sendMail
	  * @Description: 发送普通邮件-不带附件
	  * @param to
	  * @param subject
	  * @param sttachmentName
	  * @throws MessagingException
	  * @throws IOException    设定文件
	  * void    返回类型
	  * @throws
	 */
	public static void sendMail(String to,String subject,String sttachmentName) throws MessagingException, IOException	{
		JavaMailSenderImpl senderMail = new JavaMailSenderImpl();

		// 设定 Mail Server
		senderMail.setHost(EMAIL_HOST_ULE);
		senderMail.setPort(EMAIL_PORT_ULE);

		Properties prop = new Properties();
		prop.setProperty("mail.smtp.auth", "true");
		// SMTP验证时，需要用户名和密码
		senderMail.setUsername(ConfigConstants.OA_SENDMAIL_USERNAME);
		senderMail.setPassword(ConfigConstants.OA_SENDMAIL_PASSWORD);
		senderMail.setJavaMailProperties(prop); // 如果要密码验证,这里必须加,不然会报553错误

		// 发送HTML格式的邮件
		// 建立邮件信息，可发送HTML格式
		MimeMessage mimeMessage = senderMail.createMimeMessage(); // MimeMessage-->java的
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true, "UTF-8"); // MimeMessageHelper-->spring的 不加后面两个参数会乱码
		
		// 设置收件人，主题，内容
		messageHelper.setSubject(subject);
		messageHelper.setFrom(ConfigConstants.OA_SENDMAIL_USERNAME);
		messageHelper.setTo(to);
		
		StringBuffer str = new StringBuffer();
		str.append("<html><head></head><body>" + sttachmentName + "</body></html>");
		messageHelper.setText(str.toString(), true); // 为true-->发送转义HTML
		
		senderMail.send(mimeMessage);
	}
	
	public static void sendExcelMail(HSSFWorkbook hSSFWorkbook, String to,String subject,String sttachmentName) throws Exception{
		int count = 5;
		while(count >= 0){//邮件发送失败后继续尝试6次
			count--;
			
			try {
				if(StringUtils.isNotBlank(to)){
					logger.info("{}发送至邮箱[{}]开始",subject,to);
					sendMail(hSSFWorkbook, to, subject,sttachmentName);
					logger.info("{}发送至邮箱[{}]结束",subject,to);
				}else{
					logger.info("邮箱帐号为空，不发送"+ subject +"邮件");
				}
				
				count = -1;//结束发送
			} catch (Exception e) {
				logger.error(subject +"邮件发送失败,失败原因={}",e.getMessage());
				
				try {
					Thread.sleep(500);
				} catch (Exception e1) {
					logger.error("休眠500毫秒失败,原因={}",e1.getMessage());
				}
				
				if(count == 0){
					throw e;
				}
			}
		}
	}
	
	public static void sendMail(HSSFWorkbook hSSFWorkbook, String to,String subject,String sttachmentName) throws MessagingException, IOException	{
		JavaMailSenderImpl senderMail = new JavaMailSenderImpl();

		// 设定 Mail Server
		senderMail.setHost(EMAIL_HOST_ULE);
		senderMail.setPort(EMAIL_PORT_ULE);

		Properties prop = new Properties();
		prop.setProperty(EMAIL_AUTH_PRO_ULE, EMAIL_AUTH_PRO_YES);
		// SMTP验证时，需要用户名和密码
		senderMail.setUsername(ConfigConstants.OA_SENDMAIL_USERNAME);
		senderMail.setPassword(ConfigConstants.OA_SENDMAIL_PASSWORD);
		senderMail.setJavaMailProperties(prop); // 如果要密码验证,这里必须加,不然会报553错误

		// 发送HTML格式的邮件
		// 建立邮件信息，可发送HTML格式
		MimeMessage mimeMessage = senderMail.createMimeMessage(); // MimeMessage-->java的
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true, "UTF-8"); // MimeMessageHelper-->spring的 不加后面两个参数会乱码

		// 设置收件人，主题，内容
		messageHelper.setSubject(subject);
		messageHelper.setFrom(ConfigConstants.OA_SENDMAIL_USERNAME);
		messageHelper.setTo(to);

		StringBuffer str = new StringBuffer();
		str.append("<html><head></head><body><h1>" + sttachmentName + "</h1></body></html>");
		messageHelper.setText(str.toString(), true); // 为true-->发送转义HTML
		String fileName = ConfigConstants.OA_RSAKEY  + formatPath(MD5Encoder.md5Hex(DateUtils.getNow())) + ".xls";
		
		FileOutputStream fileOut = null;  
		
        fileOut = new FileOutputStream(fileName);  
        hSSFWorkbook.write(fileOut);
        FileSystemResource file = new FileSystemResource(new File(fileName));
		messageHelper.addAttachment(sttachmentName+" .xls",file);
		senderMail.send(mimeMessage); // 这个是发送带附件的
        if(fileOut != null){  
           fileOut.close();
        }  
        File delfile = new File(fileName);
        if(!delfile.delete()) {
        	
        }
	}
	
	public static void sendPdfMail(String to,String subject,String filePath) throws MessagingException, IOException	{
		JavaMailSenderImpl senderMail = new JavaMailSenderImpl();

		// 设定 Mail Server
		senderMail.setHost(EMAIL_HOST_ULE);
		senderMail.setPort(EMAIL_PORT_ULE);

		Properties prop = new Properties();
		prop.setProperty(EMAIL_AUTH_PRO_ULE, EMAIL_AUTH_PRO_YES);
		// SMTP验证时，需要用户名和密码
		senderMail.setUsername(ConfigConstants.OA_SENDMAIL_USERNAME);
		senderMail.setPassword(ConfigConstants.OA_SENDMAIL_PASSWORD);
		senderMail.setJavaMailProperties(prop); // 如果要密码验证,这里必须加,不然会报553错误

		// 发送HTML格式的邮件
		// 建立邮件信息，可发送HTML格式
		MimeMessage mimeMessage = senderMail.createMimeMessage(); // MimeMessage-->java的
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true, "UTF-8"); // MimeMessageHelper-->spring的 不加后面两个参数会乱码

		// 设置收件人，主题，内容
		messageHelper.setSubject(subject);
		messageHelper.setFrom(ConfigConstants.OA_SENDMAIL_USERNAME);
		messageHelper.setTo(to);

		StringBuffer str = new StringBuffer();
		str.append("<html><head></head><body><h1>" + subject + "</h1></body></html>");
		messageHelper.setText(str.toString(), true); // 为true-->发送转义HTML
		
        FileSystemResource file = new FileSystemResource(new File(filePath));
		messageHelper.addAttachment(subject + ".pdf",file);
		senderMail.send(mimeMessage); // 这个是发送带附件的

		File delfile = new File(filePath);
		if(!delfile.delete()) {
			
		}
	}
	
	public static InputStream getExcelISForAs(HSSFWorkbook hSSFWorkbook)throws Exception {
		InputStream excelStream = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		hSSFWorkbook.write(out);
		excelStream = new ByteArrayInputStream(out.toByteArray());
		out.close();
		return excelStream;

	}
	
	/**避免java.io.FileNotFoundException: D:\UyDrylO\TCwOwPtx0wLv\A==.xls (系统找不到指定的路径。**/
	public static String formatPath(String path){
		
		path = path.replaceAll("/", "");
		
		return path;
	}
}
