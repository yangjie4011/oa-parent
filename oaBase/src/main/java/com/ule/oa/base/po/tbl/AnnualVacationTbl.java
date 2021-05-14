package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 每年假期表
 * @Description: 每年假期表
 * @author yangjie
 * @date 2017年6月16日
 */
public class AnnualVacationTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 882151465688608843L;
	
	private Long id;
	//假期日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date annualDate;
	//类型：1-工作日、2-年休假、3-法定节假日、4-节假日
	private Integer type;
	//时间：0-全天、1半天
	private Integer dateType;
	//主题
	private String subject;
	//描述
	private String content;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	public Date getAnnualDate() {
		return annualDate;
	}
	public void setAnnualDate(Date annualDate) {
		this.annualDate = annualDate;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getDateType() {
		return dateType;
	}
	public void setDateType(Integer dateType) {
		this.dateType = dateType;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
