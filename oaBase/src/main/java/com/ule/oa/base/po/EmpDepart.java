package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpDepartTbl;

/**
  * @ClassName: EmpDepart
  * @Description: 员工部门表
  * @author minsheng
  * @date 2017年5月8日 下午1:44:59
 */
@JsonInclude(Include.NON_NULL)
public class EmpDepart extends EmpDepartTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -6340480526281754046L;
	
	//自定义对象
	private Depart depart;

	public Depart getDepart() {
		return depart;
	}

	public void setDepart(Depart depart) {
		this.depart = depart;
	}
}
