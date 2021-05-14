package com.ule.oa.common.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PageModel<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<T> rows;

	private Map<String, List<Map<String, Object>>> mapList;
	
	private int total;


	// 每页记录数
	private int pageSize;

	// 当前页码
	private int pageNo;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	// 取得总页数
	public int getTotalPages() {
		if (pageSize > 0) {
			return (total - 1) / pageSize + 1;
		}
		return 0;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	// 取得上一页码
	public int getPreviousPageNo() {
		if (this.getPageNo() <= 1) {
			return 1;
		}else {
			return this.getPageNo() - 1;
		}
			
	}

	// 取得下一页码
	public int getNextPageNo() {
		if (this.getPageNo() >= this.getTotalPages()) {
			return this.getTotalPages();
		}else {
			return this.getPageNo() + 1;
		}
			
	}

	public int getOffset() {
		if(getPageSize() * (getPageNo() - 1)<0){
			return 0;
		}
		return getPageSize() * (getPageNo() - 1);
	}

	public int getLimit() {
		return getPageSize();
	}

	public int getTotal() {
		return total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public void setTotal(int total) {
		this.total = total;
		this.getTotalPages();
	}

	public boolean hasNextPage() {
		return getNextPageNo() > getPageNo();
	}
	

	public Map<String, List<Map<String, Object>>> getMapList() {
		return mapList;
	}

	public void setMapList(Map<String, List<Map<String, Object>>> mapList) {
		this.mapList = mapList;
	}

}
