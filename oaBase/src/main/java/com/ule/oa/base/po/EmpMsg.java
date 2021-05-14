package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpMsgTbl;

/**
 * @ClassName: 我的消息
 * @Description: 我的消息
 * @author yangjie
 * @date 2017年6月8日
 */

@JsonInclude(Include.NON_NULL)
public class EmpMsg extends EmpMsgTbl{
	
	private static final long serialVersionUID = 8887806587579620855L;
	
	public static final int READ_FLAG_NO = 1;//未读
	public static final int READ_FLAG_YES = 2;//已读
	
	//类型:100-系统、200-流程
	public static final Long type_100 = 100L;
	public static final Long type_200 = 200L;
	
	//创建时间
	private String crTime;

	public String getCrTime() {
		return crTime;
	}

	public void setCrTime(String crTime) {
		this.crTime = crTime;
	}
	
	

}
