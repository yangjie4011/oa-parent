package com.ule.oa.base.po.tbl;

import java.io.Serializable;

import com.ule.oa.common.po.CommonPo;
/**
 * @ClassName: Base_BoardRoom
 * @Description: 会议室
 * @author zhoujinliang
 * @date 2018年7月4日
 */
public class BoardroomTbl extends CommonPo implements Serializable{
	
	private static final long serialVersionUID = -4579494647136310917L;

	private Long id;

    private Long companyId;//公司ID

    private Long floorId;//楼层ID

    private String name;//名称

    private String seatQuantity;//座位数量

    private String equipments;//设备情况

    private Integer openStatus;//开放状态:1-开放,0-未开放

    private String remark;

    private Integer isLocked;//是否被占用:1-是,0-否

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

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSeatQuantity() {
        return seatQuantity;
    }

    public void setSeatQuantity(String seatQuantity) {
        this.seatQuantity = seatQuantity == null ? null : seatQuantity.trim();
    }

    public String getEquipments() {
        return equipments;
    }

    public void setEquipments(String equipments) {
        this.equipments = equipments == null ? null : equipments.trim();
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
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