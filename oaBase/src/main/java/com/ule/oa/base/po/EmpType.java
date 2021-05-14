package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpTypeTbl;

@JsonInclude(Include.NON_NULL)
public class EmpType extends EmpTypeTbl{

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	private static final long serialVersionUID = 8700635633778273738L;
    
	
	private Long emptypeId;


	public Long getEmptypeId() {
		return emptypeId;
	}


	public void setEmptypeId(Long emptypeId) {
		this.emptypeId = emptypeId;
	}
	
	
}