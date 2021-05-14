package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.ClassSetPersonTbl;

/**
 * @ClassName: 排班人表
 * @Description: 排班人表
 * @author yangjie
 * @date 2017年9月12日
 */
@JsonInclude(Include.NON_NULL)
public class ClassSetPerson extends ClassSetPersonTbl {

	private static final long serialVersionUID = 1L;

}
