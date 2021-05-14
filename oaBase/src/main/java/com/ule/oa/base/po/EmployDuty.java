package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmployDutyTbl;

/**
 * @ClassName: 员工值班表
 * @Description: 员工值班表
 * @author yangjie
 * @date 2018年1月23日
 */
@JsonInclude(Include.NON_NULL)
public class EmployDuty extends EmployDutyTbl{

	private static final long serialVersionUID = -4254531816134351660L;

}
