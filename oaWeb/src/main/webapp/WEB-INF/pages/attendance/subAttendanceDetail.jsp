<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <%@include file="../common/common.jsp"%>
    <link rel="icon" href="data:;base64,=">
    <title>下属考勤明细</title>
</head>


<body class="b-f2f2f2 mt-180">
    <input type="hidden" id="workTypeName" value="${workTypeName}">
    <form id="subDetailForm" method="post">
          <input type="hidden" id="urlType" name="urlType" value="${urlType}">  <!-- 返回的页面 -->
          <input type="hidden" id="startTime" name="startTime" value="${startTime}">  <!-- 用来传值统计出勤年月 -->
          <input type="hidden" id="endTime" name="endTime" value="${endTime}">
          <input type="hidden" id="employId" name="employId" value="${employId}">
    </form>
    <form id="toApplyForm" method="post">
          <input type="hidden" id="urlType" name="urlType" value="${urlType}">  <!-- 返回下属考勤-->
          <input type="hidden" id="attnDate" name="attnDate"> 
          <input type="hidden" id="startWorkTime" name="startWorkTime">
          <input type="hidden" id="endWorkTime" name="endWorkTime">
          <input type="hidden" id="startTime" name="startTime" value="${startTime}">
          <input type="hidden" id="endTime" name="endTime" value="${endTime}">
          <input type="hidden" id="employId" name="employId" value="${employId}">
          <input type="hidden" id="type" name="type" value="1">
    </form>
    <div class="oa-timecard">
        <header>
        	<h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a><p>${employeeName }</p>
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
                        <p><strong><fmt:formatNumber type="number" value="${proportion }" pattern="#.#" maxFractionDigits="2"/>%</strong></p>
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
    <script type="text/javascript" src="<%=basePath%>js/attendance/subAttendanceDetail.js?v=20190722"></script>
</body>

</html>