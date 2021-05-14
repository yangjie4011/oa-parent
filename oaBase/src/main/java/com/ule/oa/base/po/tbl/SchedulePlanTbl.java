package com.ule.oa.base.po.tbl;

import java.io.Serializable;

import com.ule.oa.common.po.CommonPo;

/**
  * @ClassName: SchedulePlanTbl
  * @Description: 班次设置表
  * @author wufei
  * @date 2017年6月2日
 */
public class SchedulePlanTbl extends CommonPo implements Serializable{
	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 4393651742607392579L;

	private Long id;

    private String planName;//班次名称

    private Long color;//排班颜色
    
    private Double totalTime;//应出勤工时（小时）

    private String startTime;//开始时间

    private String endTime;//结束时间
    
    private Integer rank;//排序

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public Long getColor() {
		return color;
	}

	public void setColor(Long color) {
		this.color = color;
	}

	public Double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Double totalTime) {
		this.totalTime = totalTime;
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

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

}
