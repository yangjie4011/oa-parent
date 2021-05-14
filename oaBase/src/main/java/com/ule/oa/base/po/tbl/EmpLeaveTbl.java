package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ule.oa.common.po.CommonPo;
/**
 * @ClassName: Base_Emp_Leave
 * @Description: 员工假期表
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class EmpLeaveTbl  extends CommonPo  implements Serializable{
      /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -6030387711869809903L;

	private Long id;

    private Long companyId;

    private Long employeeId;

    private Integer year;//假期所属年月

    private Integer type;//假期类型
    
    private Double raduix;//假期基数

    private Double allowDays;//允许可休假期天数

    private Double actualDays;//实际可休假天数/小时

    private Double usedDays;//已休假天数/小时

    private Double blockedDays;//占用天数

    private Double allowRemainDays;//允许可休假剩余天数/小时

    private Integer category;//二级分类

    private Long parendId;//父节点id
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;//假期有效开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;//假期有效期结束时间

    private Integer isActive;//是否有效

    private String remark;
    
    private Integer delFlag;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String updateUser;

    private Long version;
    
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getRaduix() {
		return raduix;
	}

	public void setRaduix(Double raduix) {
		this.raduix = raduix;
	}

	public Double getAllowDays() {
		return allowDays;
	}

	public void setAllowDays(Double allowDays) {
		this.allowDays = allowDays;
	}

	public Double getActualDays() {
		return actualDays;
	}

	public void setActualDays(Double actualDays) {
		this.actualDays = actualDays;
	}

	public Double getUsedDays() {
		return usedDays;
	}

	public void setUsedDays(Double usedDays) {
		this.usedDays = usedDays;
	}

	public Double getBlockedDays() {
		return blockedDays;
	}

	public void setBlockedDays(Double blockedDays) {
		this.blockedDays = blockedDays;
	}

	public Double getAllowRemainDays() {
		return allowRemainDays;
	}

	public void setAllowRemainDays(Double allowRemainDays) {
		this.allowRemainDays = allowRemainDays;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Long getParendId() {
		return parendId;
	}

	public void setParendId(Long parendId) {
		this.parendId = parendId;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}