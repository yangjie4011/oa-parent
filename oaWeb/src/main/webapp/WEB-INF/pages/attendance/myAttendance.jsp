<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>我的考勤</title>
    <meta charset="UTF-8">
</head>


<body class="b-f2f2f2 mt-180">
    <input type="hidden" id="workTypeName" value="${workTypeName}">
    <form id="myAttendForm" method="post">
          <input type="hidden" id="urlType" name="urlType" value="${urlType}">  <!-- 返回的页面 -->
          <input type="hidden" id="startTime" name="startTime" value="${startTime}">  <!-- 用来传值统计出勤年月 -->
          <input type="hidden" id="endTime" name="endTime" value="${endTime}">
          <input type="hidden" id="employId" name="employId" value="${employId}">
    </form>
    <form id="toApplyForm" method="post">
          <input type="hidden" id="urlType" name="urlType" value="3">  <!-- 返回我的考勤-->
          <input type="hidden" id="attnDate" name="attnDate"> 
          <input type="hidden" id="startWorkTime" name="startWorkTime">
          <input type="hidden" id="endWorkTime" name="endWorkTime">
          <input type="hidden" id="employId" name="employId" value="${employId}">
          <input type="hidden" id="type" name="type" value="0">
    </form>
    <div class="oa-timecard">
        <header>
        	<h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a><p>我的考勤</p>
        	<c:if test="${isLeader == true}">
	        	<%-- <a href="<%=basePath%>empAttn/subAttendance.htm?employId=${employId}&startTime=${startTime }&endTime=${endTime }" class="correcting fr">下属考勤</a> --%>
	        	<a href="javascript:toSubAttendance()" class="correcting fr">下属考勤</a>
        	</c:if>
        	<c:if test="${isLeader != true}">
	        	<a href="#" class="correcting fr">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
        	</c:if>
        	</h1>
            <section class="attendance">
                <div class="timeCalculation">
                    <div class="attendanceType">
                        <span class="attenMonthShow">本月出勤</span>
                        <c:if test="${totalAllAttnTime >= monthTotalMustAttnTime}">
                            <p><strong>已达标</strong></p>
                        </c:if>
                        <c:if test="${totalAllAttnTime < monthTotalMustAttnTime}">
                            <p><strong>${totalAllAttnTime }</strong>小时</p>
                        </c:if>
                    </div>
                    <div class="attendanceType">
                        <span>出勤率</span>
                        <p><strong id = attnProportion></strong></p>
                        <input type="hidden" id="proportion" value="${proportion }" />
                    </div>
                    <div class="attendanceType">
                        <span>应出勤时长</span>
                        <p><strong>${totalMustAttnTime}</strong>小时</p>
                    </div>
                </div>
                <div class="selMon">
                    <span class="thisMonth" id="thisMonth">本月</span>
                    <span class="lastMonth" style="display:none" id="lastMonth">上月</span>
                    <ul class="lastMonthUl" style="display:none" id="lastMonthUl">
                        <li><span>上月</span></li>
                    </ul>
                </div>
                <div class="proportion"><canvas id="timecanv" width="100" height="100"></canvas></div>
                <input type="hidden" id="totalTime" value="${totalMustAttnTime}">
                <input type="hidden" id="specificTime" value="${totalAllAttnTime}">
            </section>
        </header>

        <p class="timecardIntro">仅显示异常考勤</p>

        <ul class="punchtime">
        </ul>
        <div id="complaintId" ></div>
    </div>
    <script type="text/javascript" src="<%=basePath%>js/attendance/myAttendance.js?v=20200403"></script>
</body>

</html>