package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.ConfigTbl;

@JsonInclude(Include.NON_NULL)
public class Config extends ConfigTbl{
	
	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -5889089211725789044L;
	private Integer page;
	private Integer rows;
	private Integer offset;
	private Integer limit;
	public final static String CLASS_TYPE = "whetherScheduling";//排班code
	public final static String CLASS_TYPE_YES = "yes";//需要排班
	public final static String CLASS_TYPE_NO = "no";//不需要排班
	public final static String MUST_ARRANGE_OF_WORK_MIND = "mustArrangeOfWorkMind";
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