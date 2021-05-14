package com.ule.oa.base.po;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.SendMailTbl;

/**
  * @ClassName: SendMail
  * @Description: oa发送邮件表
  * @author wufei
  * @date 2017年6月30日 下午1:39:42
 */
@JsonInclude(Include.NON_NULL)
public class SendMail extends SendMailTbl {

	private static final long serialVersionUID = -860920710401850723L;
	
	/** 是否保存到发件箱 */
	public static final Integer IS_SAVE_YES = 0; //是
	public static final Integer IS_SAVE_NO = 1; //否
	/** 是否需要回执*/
	public static final Integer IS_RECEIPT_YES = 0; //是
	public static final Integer IS_RECEIPT_NO = 1; //否
	/** 是否紧急邮件*/
	public static final Integer IS_PRIORITY_YES = 0; //是
	public static final Integer IS_PRIORITY_NO = 1; //否
	/** 发送状态*/
	public static final Integer SEND_STATUS_YES = 1; //已发送
	public static final Integer SEND_STATUS_NO = 0; //未发送
	//OA邮件标记：0-普通邮件 1-oa邮件
	public static final Integer OA_MAIL_OA = 1;
	public static final Integer OA_MAIL_P = 0; 
	
	public static final String CODE_ERROR = "000";//发送失败（账号密码错误或服务器出错）
	public static final String CODE_OK = "001";//发送成功
	public static final String CODE_NULL = "002";//非空项为空
	
	private Date createTimeStart;

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}
	
}
