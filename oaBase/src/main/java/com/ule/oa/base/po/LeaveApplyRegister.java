package com.ule.oa.base.po;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.ule.oa.base.po.tbl.LeaveApplyRegisterTbl;

/**
 * 休假登记
 * @author yangjie
 *
 */
public class LeaveApplyRegister extends LeaveApplyRegisterTbl {

	private static final long serialVersionUID = 4197693178799647524L;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;//查询条件-休假开始日期
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;//查询条件-休假结束日期
	
	private String token;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
    
}
