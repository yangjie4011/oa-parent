package com.ule.oa.base.po;

import com.ule.oa.base.po.tbl.DelayWorkRegisterTbl;

public class DelayWorkRegister extends DelayWorkRegisterTbl{

	private static final long serialVersionUID = -5098542645854362235L;
	
	//修改类型(1.初始登记 2.未匹配3.匹配考勤且匹配结果相等4.匹配考勤且结果不相等5.确认)
	private Integer updateType;
	
	//字体颜色
	private String fontColor;
	
	//星期
	private String week;
	
	//传递参数
	private String startTime;
	
	//传递参数
	private String endTime;
	
	//是否工作日(0.工作日1.非工作日)
	private Integer isWorkDay;

	public Integer getUpdateType() {
		return updateType;
	}

	public void setUpdateType(Integer updateType) {
		this.updateType = updateType;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getIsWorkDay() {
		return isWorkDay;
	}

	public void setIsWorkDay(Integer isWorkDay) {
		this.isWorkDay = isWorkDay;
	}

	
	
	
}
