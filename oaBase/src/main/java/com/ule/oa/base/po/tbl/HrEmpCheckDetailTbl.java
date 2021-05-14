package com.ule.oa.base.po.tbl;

import java.io.Serializable;

import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 离职办理清单详细
 * @Description: 离职办理清单详细
 * @author yangjie
 * @date 2017年6月6日
 */
public class HrEmpCheckDetailTbl extends CommonPo implements Serializable{

	private static final long serialVersionUID = 4665050647823022120L;
	
	private Long id;
	//部门名称
	private String departName;
	//检查项目
	private String checkItem;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getCheckItem() {
		return checkItem;
	}
	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}

}
