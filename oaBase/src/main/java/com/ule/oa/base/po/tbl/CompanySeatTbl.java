package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;
/**
 * @ClassName: Base_Company_Seat
 * @Description: 公司座位信息
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class CompanySeatTbl implements Serializable{
	/*
	 * Base_Company_Seat(公司座位信息)
	 * */
	private static final long serialVersionUID = -5436839403292883984L;

	private Long id;

    private Long floorId;//楼层ID

    private String seatNo;//座位编号

    private Date createTime;

    private String createUser;

    private Date updateTime;

    private String updateUser;

    private Integer delFlag;

    private String remark;

    private Integer isLocked;//是否占用,1-是,0-否

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo == null ? null : seatNo.trim();
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

    public Integer getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Integer isLocked) {
        this.isLocked = isLocked;
    }
}