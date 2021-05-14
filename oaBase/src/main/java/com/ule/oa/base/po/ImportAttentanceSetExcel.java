package com.ule.oa.base.po;

import java.util.Date;

import com.ule.oa.common.utils.excel.Excel;

public class ImportAttentanceSetExcel {
	
	@Excel(name="员工编号")
	private String code;	//员工编号
	@Excel(name="员工姓名")
	private String cnName;	//中文名
	@Excel(name="班次")
	private String classSetName;	//班次简称
	@Excel(name="生效年份")
	private String year;	//年
	@Excel(name="生效月份")
	private String month; //月
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getClassSetName() {
		return classSetName;
	}
	public void setClassSetName(String classSetName) {
		this.classSetName = classSetName;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}

}
