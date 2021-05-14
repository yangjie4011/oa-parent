package com.ule.oa.base.po.tbl;

import java.io.Serializable;
import java.util.Date;

import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.DateUtils;
/**
 * @ClassName: Base_BoardRoom_Due
 * @Description: 会议室预定表
 * @author zhoujinliang
 * @date 2018年7月4日
 */
public class BoardroomDueTbl extends CommonPo implements Serializable{
    
	private static final long serialVersionUID = 2573074225162809662L;

	private Long id;

    private Long companyId;//公司ID

    private Long floorId;//楼层ID

    private Long boardroomId;//会议室ID

    private String boardroomName;//会议室名称

    private Date meetingDate;//会议日期

    private String startTime;//开始时间

    private String endTime;//结束时间

    private String meetingSubject;//会议主题

    private Long meetingModeratorId;//会议主持人ID

    private String meetingModerator;//会议主持人

    private String participant;//会议参与人
    
    private String participantId;//会议参与人id

    private Long status;//会议状态:100-未开始,200-进行中,300-已结束

    private Long delay;//是否延期:1-是,0-否

    private String delayTime;//延期时间

    private Long employeeId;//申请人ID

    private Long version;//版本号

    //private Date createTime;//创建/申请时间

    //private String createUser;//创建/申请人

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

    public Long getBoardroomId() {
		return boardroomId;
	}

	public void setBoardroomId(Long boardroomId) {
		this.boardroomId = boardroomId;
	}

	public String getBoardroomName() {
		return boardroomName;
	}

	public void setBoardroomName(String boardroomName) {
		this.boardroomName = boardroomName;
	}

	public Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
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

	public Long getMeetingModeratorId() {
        return meetingModeratorId;
    }

    public void setMeetingModeratorId(Long meetingModeratorId) {
        this.meetingModeratorId = meetingModeratorId;
    }

    public String getMeetingModerator() {
        return meetingModerator;
    }

    public void setMeetingModerator(String meetingModerator) {
        this.meetingModerator = meetingModerator == null ? null : meetingModerator.trim();
    }

    public Long getStatus() {
    	Date now = new Date();
    	Date start = DateUtils.parse(this.startTime.toString());
    	Date end = DateUtils.parse(this.endTime.toString());
    	if(now.before(start)){
    		status = 100L;
    	}else if(now.after(end)){
    		status = 300L;
    	}else{
    		status = 200L;
    	}
    	return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public String getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(String delayTime) {
        this.delayTime = delayTime == null ? null : delayTime.trim();
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getMeetingSubject() {
        return meetingSubject;
    }

    public void setMeetingSubject(String meetingSubject) {
        this.meetingSubject = meetingSubject == null ? null : meetingSubject.trim();
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant == null ? null : participant.trim();
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId == null ? null : participantId.trim();
    }
}