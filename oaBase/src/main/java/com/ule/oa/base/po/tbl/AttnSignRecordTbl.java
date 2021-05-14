package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;
/**
 * @ClassName: AttnSignRecordTbl
 * @Description: 考勤打卡记录表
 * @author zhoujinliang
 * @date 2018年7月4日
 */
public class AttnSignRecordTbl implements Serializable{
	
	private static final long serialVersionUID = -1151277830867851861L;

	private Long id;

	//公司ID
    private Long companyId;

    //员工ID
    private Long employeeId;

    //员工姓名
    private String employeeName;

    //打卡时间
    private Date signTime;

    //导入打卡数据时，记录的最大Id
    private Long attnRecordId;

    private Date createTime;

    private String createUser;

    private Date updateTime;

    private String updateUser;

    //删除标记0：未删除，1：删除
    private Integer delFlag;

    private String remark;
    
    //0-默认值，1-考勤异常充入,5-定位签到
    private Integer type;
    
    private String ip;//ip地址
    
    private String locationResult;//定位结果集
    
    private String address;//地址
    
    private Double distance;

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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName == null ? null : employeeName.trim();
    }

    public Date getSignTime() {
        return signTime;
    }

    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    public Long getAttnRecordId() {
        return attnRecordId;
    }

    public void setAttnRecordId(Long attnRecordId) {
        this.attnRecordId = attnRecordId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLocationResult() {
		return locationResult;
	}

	public void setLocationResult(String locationResult) {
		this.locationResult = locationResult;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
}