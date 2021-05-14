package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.PositionTbl;

/**
  * @ClassName: Position
  * @Description: 职位表
  * @author minsheng
  * @date 2017年5月8日 下午1:47:10
 */
@JsonInclude(Include.NON_NULL)
public class Position extends PositionTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 8996579958795124662L;

	private Integer page;
	private Integer rows;
	private Integer offset;
	private Integer limit;
	
	//自定义对象
	private CompanyPositionLevel companyPositionLevel;
	private CompanyPositionSeq companyPositionSeq;
	
	//自定义字段
	private Long departId;//部门id
	private String parentPositionName;//上级职位
	private Integer seqRank; //序列排序
	public CompanyPositionLevel getCompanyPositionLevel() {
		return companyPositionLevel;
	}
	public void setCompanyPositionLevel(CompanyPositionLevel companyPositionLevel) {
		this.companyPositionLevel = companyPositionLevel;
	}
	public CompanyPositionSeq getCompanyPositionSeq() {
		return companyPositionSeq;
	}
	public void setCompanyPositionSeq(CompanyPositionSeq companyPositionSeq) {
		this.companyPositionSeq = companyPositionSeq;
	}
	public String getParentPositionName() {
		return parentPositionName;
	}
	public void setParentPositionName(String parentPositionName) {
		this.parentPositionName = parentPositionName;
	}
	public Long getDepartId() {
		return departId;
	}
	public void setDepartId(Long departId) {
		this.departId = departId;
	}
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
	public Integer getSeqRank() {
		return seqRank;
	}
	public void setSeqRank(Integer seqRank) {
		this.seqRank = seqRank;
	}
	
}
