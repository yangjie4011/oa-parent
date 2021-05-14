package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.CompanyConfigTbl;

@JsonInclude(Include.NON_NULL)
public class CompanyConfig extends CompanyConfigTbl{

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 6167488583030029071L;
    
	private String name;//公司名称
	private Integer page;
	private Integer rows;
	private Integer offset;
	private Integer limit;
	private String companyName;
	public final static String TYPE_OF_WORK = "typeOfWork";//工时类型
	public final static String TYPE_OF_WORK_ZH = "comprehensive";//综合工时类型
	public final static String TYPE_OF_WORK_BZ = "standard";//标准工时类型
	public final static String SIGN_RECORD_EX_MSG_REMAIND_EXCEPT = "signRecordExMsgRemindExcept";//忘打卡和晚到提醒(不适用人群)
	public final static String ATTN_EX_MSG_REMIND_EXCEPT = "attnExMsgRemindExcept";//异常考勤提醒(不适用人群)
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}