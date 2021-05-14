package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.CompanyPositionLevelTbl;
@JsonInclude(Include.NON_NULL)
public class CompanyPositionLevel extends CompanyPositionLevelTbl{
    
	private Integer page;
	private Integer rows;
	private Integer offset;
	private Integer limit;
	private String companyName;
	
	/**
	 * 职位等级常量 P-普通员工 M-管理层 	I-
	 */
	public static final String COMPANY_LEVEL_CODE_P = "P";
	public static final String COMPANY_LEVEL_CODE_M = "M";
	public static final String COMPANY_LEVEL_CODE_I = "I";
	
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getRows() {
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
  
}