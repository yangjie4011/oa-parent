package com.ule.oa.base.po.tbl;

import java.util.Date;
/**
 * @ClassName: AttnUsersTbl(VO表)
 * @Description: 考勤详细信息VO表
 * @author zhoujinliang
 * @date 2018年7月4日
 */
public class AttnUsersTbl {
	//员工id
    private Integer userid;
    //部门id
    private Integer deptid;
    //姓名
    private String name;
    //员工编号
    private String usercode;
    //性别
    private String sex;
    //工作
    private String job;
    //连接时间
    private Date joindate;
    //身份证号码
    private String idcard;
    //电话
    private String phone;
    //邮箱
    private String email;
    //地址
    private String address;
    //记录
    private String notes;
    //时间范围类型（翻译不通）
    private Byte timerangetype;
    //组id
    private Integer groupid;
    //团队id
    private Integer teamid;
    //队id
    private Integer crewid;
    //生日
    private Date birthday;
    //籍贯   
    private String nativeplace;
    //离职日期
    private Date dimissiondate;
    //流量数值
    private Integer floornumber;
    //时间范围 几天
    private Integer timerangedays;
    //OA员工id
    private Long oaEmpId;
    //相片组
    private byte[] photo;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getDeptid() {
        return deptid;
    }

    public void setDeptid(Integer deptid) {
        this.deptid = deptid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode == null ? null : usercode.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job == null ? null : job.trim();
    }

    public Date getJoindate() {
        return joindate;
    }

    public void setJoindate(Date joindate) {
        this.joindate = joindate;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard == null ? null : idcard.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? null : notes.trim();
    }

    public Byte getTimerangetype() {
        return timerangetype;
    }

    public void setTimerangetype(Byte timerangetype) {
        this.timerangetype = timerangetype;
    }

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public Integer getTeamid() {
        return teamid;
    }

    public void setTeamid(Integer teamid) {
        this.teamid = teamid;
    }

    public Integer getCrewid() {
        return crewid;
    }

    public void setCrewid(Integer crewid) {
        this.crewid = crewid;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getNativeplace() {
        return nativeplace;
    }

    public void setNativeplace(String nativeplace) {
        this.nativeplace = nativeplace == null ? null : nativeplace.trim();
    }

    public Date getDimissiondate() {
        return dimissiondate;
    }

    public void setDimissiondate(Date dimissiondate) {
        this.dimissiondate = dimissiondate;
    }

    public Integer getFloornumber() {
        return floornumber;
    }

    public void setFloornumber(Integer floornumber) {
        this.floornumber = floornumber;
    }

    public Integer getTimerangedays() {
        return timerangedays;
    }

    public void setTimerangedays(Integer timerangedays) {
        this.timerangedays = timerangedays;
    }

    public Long getOaEmpId() {
        return oaEmpId;
    }

    public void setOaEmpId(Long oaEmpId) {
        this.oaEmpId = oaEmpId;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

}
