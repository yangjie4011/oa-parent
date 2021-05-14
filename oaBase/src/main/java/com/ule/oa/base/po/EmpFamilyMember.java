package com.ule.oa.base.po;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpFamilyMemberTbl;

/**
  * @ClassName: EmpFamilyMenber
  * @Description: 员工家庭成员表
  * @author minsheng
  * @date 2017年5月8日 下午1:45:13
 */
@JsonInclude(Include.NON_NULL)
public class EmpFamilyMember extends EmpFamilyMemberTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 2800751308170596134L;
	
	private Date startDate;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	

}
