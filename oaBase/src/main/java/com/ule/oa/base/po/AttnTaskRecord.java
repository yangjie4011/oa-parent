package com.ule.oa.base.po;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.AttnTaskRecordTbl;

/**
  * @ClassName: AttnTaskRecord
  * @Description: 记录员工考勤计算状态
  * @author zhangjintao
  * @date 2017年6月29日 下午2:38:25
 */
@JsonInclude(Include.NON_NULL)
public class AttnTaskRecord extends AttnTaskRecordTbl{

	private Integer  offset;
	private Integer limit;
	private Long companyId;
	private Date startTime;//开始日期，条件，或者kafka防重消费用
	private Date endTime;//结束日期，条件，或者kafka防重消费用
    private String employeeIds;//员工id，多个需要用逗号隔开，接口接受参数用
    private List<Long> ids;

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
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public String getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(String employeeIds) {
		this.employeeIds = employeeIds;
	}
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	
}