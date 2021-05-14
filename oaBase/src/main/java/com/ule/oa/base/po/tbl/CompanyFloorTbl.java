package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: Base_Company_Floor
 * @Description: 公司楼层
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class CompanyFloorTbl implements Serializable{
	
	private static final long serialVersionUID = 3033239424248099208L;

	private Long id;

    private Long companyId;//公司ID

    private Long floorNum;//楼层

    private String name;//名称

    private String floorSeatPicUrl;//座位图URL

    private String remark;

    private Date updateTime;

    private String updateUser;

    private Integer delFlag;

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

    public Long getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(Long floorNum) {
        this.floorNum = floorNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getFloorSeatPicUrl() {
        return floorSeatPicUrl;
    }

    public void setFloorSeatPicUrl(String floorSeatPicUrl) {
        this.floorSeatPicUrl = floorSeatPicUrl == null ? null : floorSeatPicUrl.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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
}