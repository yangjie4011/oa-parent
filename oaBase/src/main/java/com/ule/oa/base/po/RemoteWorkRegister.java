package com.ule.oa.base.po;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.CompanyTbl;
import com.ule.oa.base.po.tbl.RemoteWorkRegisterTbl;

/**
  * @ClassName: RemoteWorkRegister
  * @Description: 远程登记表
  * @author zhoujinliang
  * @date 2020年2月24日11:26:20
 */
@JsonInclude(Include.NON_NULL)
public class RemoteWorkRegister extends RemoteWorkRegisterTbl {
	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -3253044824838969316L;
	
	private Date startTime;
	private Date endTime;
	private String classsSetName;//排班名称
	
	public String getClasssSetName() {
		return classsSetName;
	}
	public void setClasssSetName(String classsSetName) {
		this.classsSetName = classsSetName;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	
}
