package com.ule.oa.base.po;

import com.ule.oa.base.po.tbl.RoleTbl;

/**
  * @ClassName: Role
  * @Description: 角色管理表
  * @author minsheng
  * @date 2017年12月6日 下午4:00:43
 */
public class Role extends RoleTbl{

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 5564120553345605803L;
    
	private Integer page;
	private Integer rows;
	private Integer offset;
	private Integer limit;
	private String unRoleName;//角色名称唯一
	
	public String getUnRoleName() {
		return unRoleName;
	}
	public void setUnRoleName(String unRoleName) {
		this.unRoleName = unRoleName;
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
}