package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;
/**
 * @ClassName: TransNormalTbl（VO）
 * @Description: 超常规VO类(注释没标清楚)
 * @author zhoujinliang
 * @date 2018年7月4日
*/
public class TransNormalTbl implements Serializable{
      /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -6058573571837969284L;
    //自动id
	private Integer autoid;
    //
    private Date evttime;
    //
    private Integer lineid;
    //
    private Integer unitid;
    //
    private Integer doorid;
    //
    private Integer cardzn;
    //卡号id
    private Integer cardid;
    //用户id
    private Integer userid;
    //
    private Integer evtcode;
    //
    private Boolean hasphoto;
    //
    private String cardno;
    //
    private String cardhex;
    //相片组
    private byte[] photo;

    public Integer getAutoid() {
        return autoid;
    }

    public void setAutoid(Integer autoid) {
        this.autoid = autoid;
    }

    public Date getEvttime() {
        return evttime;
    }

    public void setEvttime(Date evttime) {
        this.evttime = evttime;
    }

    public Integer getLineid() {
        return lineid;
    }

    public void setLineid(Integer lineid) {
        this.lineid = lineid;
    }

    public Integer getUnitid() {
        return unitid;
    }

    public void setUnitid(Integer unitid) {
        this.unitid = unitid;
    }

    public Integer getDoorid() {
        return doorid;
    }

    public void setDoorid(Integer doorid) {
        this.doorid = doorid;
    }

    public Integer getCardzn() {
        return cardzn;
    }

    public void setCardzn(Integer cardzn) {
        this.cardzn = cardzn;
    }

    public Integer getCardid() {
        return cardid;
    }

    public void setCardid(Integer cardid) {
        this.cardid = cardid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getEvtcode() {
        return evtcode;
    }

    public void setEvtcode(Integer evtcode) {
        this.evtcode = evtcode;
    }

    public Boolean getHasphoto() {
        return hasphoto;
    }

    public void setHasphoto(Boolean hasphoto) {
        this.hasphoto = hasphoto;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno == null ? null : cardno.trim();
    }

    public String getCardhex() {
        return cardhex;
    }

    public void setCardhex(String cardhex) {
        this.cardhex = cardhex == null ? null : cardhex.trim();
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}