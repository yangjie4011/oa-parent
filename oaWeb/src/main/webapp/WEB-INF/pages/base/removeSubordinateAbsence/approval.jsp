<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>消下属缺勤单</title>
<script type="text/javascript" src="<%=basePath%>js/base/removeSubordinateAbsence/approval.js?v=20190704"/></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>消下属缺勤<a class="toproblem fr"></a></h1>
        </header>
        <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${removeSubordinateAbsence.processinstanceId}&statu=${removeSubordinateAbsence.approalStatus}&title=消下属缺勤">
                <div class="img"></div>
                <div class="p1">
                <c:if test="${removeSubordinateAbsence.approalStatus==100}">
                 处理中，等待<span>[${actName}]</span>审批
                    </c:if>
                    <c:if test="${removeSubordinateAbsence.approalStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${removeSubordinateAbsence.approalStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${removeSubordinateAbsence.approalStatus==400}">
                         已撤销
                     </c:if>
                     <c:if test="${removeSubordinateAbsence.approalStatus==500}">
                         已失效
                     </c:if>
                     <c:if test="${removeSubordinateAbsence.approalStatus==600}">
                         失效同意
                     </c:if>
                      <c:if test="${removeSubordinateAbsence.approalStatus==700}">
                         失效拒绝
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${removeSubordinateAbsence.createTime}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
                <i></i>
            </a>
        </div>
        <div class="main">
            <ul>
                <li>
                    <div class="blk">
                        <div class="pad-tb">
                            <div class="font-font">
                                <span>消缺勤员工姓名</span>
                                <p>${removeSubordinateAbsence.empName}</p>
                            </div>
                            <div class="font-font">
                                <span>申诉主管</span>
                                <p>${removeSubordinateAbsence.submitterName}</p>
                            </div>
                            <div class="font-font">
                                <span>出勤多余小时数</span>
                                <p>${removeSubordinateAbsence.overHoursOfAttendance}小时</p>
                            </div>
                            <div class="font-font">
                                <span>前一天下班时间</span>
                            	<c:if test="${removeSubordinateAbsence.yesterdayOffTime != null}">
                                <p><fmt:formatDate value="${removeSubordinateAbsence.yesterdayOffTime}"  pattern="yyyy-MM-dd HH:mm:ss"  /></p>
                                </c:if>
                                <c:if test="${removeSubordinateAbsence.yesterdayOffTime == null}">
                                <p>空卡</p>
                                </c:if>
                            </div>
                            <div class="font-font">
                                <span>考勤日期</span>
                                <p><fmt:formatDate value="${removeSubordinateAbsence.attendanceDate}"  pattern="yyyy-MM-dd"  /></p>
                            </div>
                            <div class="font-font">
                                <span>考勤时间</span>
                                <p>${removeSubordinateAbsence.attendanceHour}</p>
                            </div>
                            <div class="font-font">
                                <span>消缺勤小时数</span>
                                <p>${removeSubordinateAbsence.removeAbsenceHours}小时</p>
                            </div>
                            <div class="font-font">
                                <span>消缺勤理由</span>
                                <p>${removeSubordinateAbsence.removeAbsenceReason}</p>
                            </div>
                        </div>
                    </div>
                    <input type="hidden" id="token" name="token" value="${token}"/>
                    <c:if test="${canApprove&&!isSelf}">
	                     <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写审批意见（非必填）" cols="30" rows="10"></textarea>
	                     </div>
                         <div class="double-btn">
                           <div id="${removeSubordinateAbsence.processinstanceId}"  onclick="refuse(this);" class="l"><em>拒绝</em></div>
                           <div id="${removeSubordinateAbsence.processinstanceId}"  onclick="pass(this);" class="r"><em>同意</em></div>
                         </div>
                   </c:if>
                   <c:if test="${isSelf}">
                   		<div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
	                     </div>
	                   <div id="${removeSubordinateAbsence.processinstanceId}" onclick="cancelRunTask(this)" class="foot-btn cancel-apply"><em>撤回申请</em></div>
                   </c:if>
                </li>
            </ul>
        </div>
    </div>
</body>
</html>