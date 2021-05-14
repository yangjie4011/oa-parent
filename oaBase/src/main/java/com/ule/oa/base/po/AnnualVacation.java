package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import com.ule.oa.base.po.tbl.AnnualVacationTbl;

/**
 * @ClassName: 每年假期表
 * @Description: 每年假期表
 * @author yangjie
 * @date 2017年6月16日
 */
public class AnnualVacation extends AnnualVacationTbl {
	
	private static final long serialVersionUID = 8470636549274870540L;
	
	//类型：1-工作日、2-年休假、3-法定节假日、4-节假日
	public static final Integer YYPE_WORK = 1;
	public static final Integer YYPE_YEAR = 2;
	public static final Integer YYPE_LEGAL = 3;
	public static final Integer YYPE_VACATION = 4;
	
	private Date startDate;
	
	private Date endDate;
	private Integer year;
	
	private List<Integer> typeList;
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	private List<String> annualDateList;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<String> getAnnualDateList() {
		return annualDateList;
	}

	public void setAnnualDateList(List<String> annualDateList) {
		this.annualDateList = annualDateList;
	}

	public List<Integer> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<Integer> typeList) {
		this.typeList = typeList;
	}

}
