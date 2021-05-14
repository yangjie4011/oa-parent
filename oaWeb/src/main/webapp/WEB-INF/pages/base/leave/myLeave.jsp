<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@include file="../../common/common.jsp"%>
    <link rel="icon" href="data:;base64,=">
    <title>我的假期</title>
    <meta charset="UTF-8">
</head>

<body class="b-f2f2f2 mt-55">
    <div class="oa-vacation">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>我的假期<a href="<%=basePath%>empLeave/leaveRecord.htm" class="save fr" style="width:auto;">假期历史</a></h1>
        </header>
        <input type="hidden" name="employeeId" value="${employeeId }"/>
        <input type="hidden" name="year" value="${year }"/>
	        <section class="vacation-module year" <c:if test="${empty remainYearHolidayDays}"> style="display: none;" </c:if>>
		        <input type="hidden" id="vacation_all_year" value="${totalYearHolidayDays }">
				<input type="hidden" id="vacation_year" value="${remainYearHolidayDays }">
	        	<a href="<%=basePath%>empLeave/yearLeaveView.htm?employeeId=${employeeId }&year=${year }">
	            <h3><i class="icon sr"></i>年假<i class="sr arrow"></i></h3>
	            <div class="vacation-info">
	                <div class="canva-info">
	                    <canvas id="canva-n" class="canva" width="100" height="80"></canvas>
	                    <div class="pic-info">
	                        <strong><fmt:formatNumber type="number" pattern="#.#" value="${remainYearHolidayDays }"></fmt:formatNumber></strong>
	                        <span>剩余(天)</span>
	                    </div>
	                </div>
	                <div class="text-info">
	                    <p class="p1">今年共<strong> <fmt:formatNumber type="number" pattern="#.#" value="${totalYearHolidayDays }"/> </strong>天　已用<strong> <fmt:formatNumber type="number" pattern="#.#" value="${usedYearHolidayDays }"/> </strong>天  </p>
	                    <c:if test="${!overDrawFlag}">
                             <p class="p1">今日可用<strong> <fmt:formatNumber type="number" pattern="#.#" value="${endTodayDays }"/> </strong>天</p>                           
	                    </c:if>
	                </div>
	            </div>
	            </a>
	        </section>

        <section class="vacation-module ill" <c:if test="${empty totalSickHolidayDays}"> style="display: none;" </c:if>>
	        <input type="hidden" id="vacation_all_ill" value="${totalSickHolidayDays }">
			<input type="hidden" id="vacation_ill" value="${remainSickHolidayDays }">
	        <a href="<%=basePath%>empLeave/sickLeaveView.htm?employeeId=${employeeId }&year=${year }">
            <h3><i class="icon sr"></i>带薪病假<i class="sr arrow"></i></h3>
            <div class="vacation-info">
                <div class="canva-info">
                    <canvas id="canva-b" class="canva" width="100" height="80"></canvas>
                    <div class="pic-info">
                        <strong><fmt:formatNumber type="number" pattern="#.#" value="${remainSickHolidayDays }"/></strong>
                        <span>剩余(天)</span>
                    </div>
                </div>
                <div class="text-info">
                    <p class="p1">共<strong> <fmt:formatNumber type="number" pattern="#.#" value="${totalSickHolidayDays }"/> </strong>天　已用<strong> <fmt:formatNumber type="number" pattern="#.#" value="${usedSickHolidayDays }"/> </strong>天</p>
                    <%-- <p class="p2">有效期：<fmt:formatDate type="date" value="${sickStartTime }" pattern="MM-dd"/>至
                    <fmt:formatDate type="date" value="${sickEndTime }" pattern="MM-dd"/></p> --%>
                </div>
            </div>
        </a>
        </section>
		
        <section class="vacation-module time" <c:if test="${empty remainRestHolidayDays}"> style="display: none;" </c:if>>
       		<input type="hidden" id="vacation_all_time" value="${totalRestHolidayDays }"/>
            <input type="hidden" id="vacation_time" value="${remainRestHolidayDays }" />
	        <a href="<%=basePath%>empLeave/restLeaveView.htm?employeeId=${employeeId }&year=${year }">
            <h3><i class="icon sr"></i>调休小时数<i class="sr arrow"></i></h3>
            <div class="vacation-info">
                <div class="canva-info">
                    <canvas id="canva-t" class="canva" width="100" height="80"></canvas>
                    <div class="pic-info">
                        <strong><fmt:formatNumber type="number" pattern="#.#" value="${remainRestHolidayDays }"/></strong>
                        <span>剩余(小时)</span>
                    </div>
                </div>
                <div class="text-info">
                    <p class="p1"><%-- 共<strong> <fmt:formatNumber type="number" pattern="#.#" value="${totalRestHolidayDays }"/> </strong>小时 --%></p>
                    <%-- <p class="p2">有效期：<fmt:formatDate type="date" value="${restStartTime }" pattern="MM-dd"/>至
                    <fmt:formatDate type="date" value="${restEndTime }" pattern="MM-dd"/></p> --%>
                </div>
            </div>
        </section>
        <a href="<%=basePath%>empApplicationLeave/index.htm?urlType=4" ><div class="foot-btn">假期申请</div></a>
    </div>

</body>

</html>