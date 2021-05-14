package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
 * 每日健康打卡表
 * @author yangjie
 *
 */
public class DailyHeathSignTbl implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7672710570774000408L;
	
	private Long id;
	
	private Integer type;//1-只需填写一次，2-每日填写
	
	private Date signDate;//打卡日期
	
	private Long employeeId;//员工id
	
	private Integer questionNum;//问题编号
	
	private String question;//问题
	
	private Integer answerNum;//答案编号
	
	private String answer;//答案
	
	private Date createTime;
	
	private String createUser;
	
	private Date updateTime;
	
	private String updateUser;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getQuestionNum() {
		return questionNum;
	}

	public void setQuestionNum(Integer questionNum) {
		this.questionNum = questionNum;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Integer getAnswerNum() {
		return answerNum;
	}

	public void setAnswerNum(Integer answerNum) {
		this.answerNum = answerNum;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	

}
