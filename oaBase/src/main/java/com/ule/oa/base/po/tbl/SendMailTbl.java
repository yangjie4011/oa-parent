package com.ule.oa.base.po.tbl;

import java.io.Serializable;

import com.ule.oa.common.po.CommonPo;

/**
  * @ClassName: SendMailTbl
  * @Description: oa发送邮件表
  * @author wufei
  * @date 2017年6月30日 下午1:35:40
 */
public class SendMailTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = -5381656729339691043L;

	private Long id;

    private String receiver;//收件人，多个收件人分号隔开

    private String carbonCopy;//抄送人，多个抄送人分号隔开

    private String blindCarbonCopy;//密送人，多个抄送人分号隔开

    private String subject;//主题
    
    private Integer isSave;//是否保存到发件箱
    
    private Integer isReceipt;//是否需要回执
    
    private Integer isPriority;//是否紧急邮件
    
    private Integer oaMail;//OA邮件标记：0-普通邮件 1-oa邮件
    
    private String text;//内容
    
    private String nickName;//发件人别名
    
    private Integer sendStatus;//发送状态 1-已发送 0-未发送

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getCarbonCopy() {
		return carbonCopy;
	}

	public void setCarbonCopy(String carbonCopy) {
		this.carbonCopy = carbonCopy;
	}

	public String getBlindCarbonCopy() {
		return blindCarbonCopy;
	}

	public void setBlindCarbonCopy(String blindCarbonCopy) {
		this.blindCarbonCopy = blindCarbonCopy;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getIsSave() {
		return isSave;
	}

	public void setIsSave(Integer isSave) {
		this.isSave = isSave;
	}

	public Integer getIsReceipt() {
		return isReceipt;
	}

	public void setIsReceipt(Integer isReceipt) {
		this.isReceipt = isReceipt;
	}

	public Integer getIsPriority() {
		return isPriority;
	}

	public void setIsPriority(Integer isPriority) {
		this.isPriority = isPriority;
	}

	public Integer getOaMail() {
		return oaMail;
	}

	public void setOaMail(Integer oaMail) {
		this.oaMail = oaMail;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(Integer sendStatus) {
		this.sendStatus = sendStatus;
	}
    
}