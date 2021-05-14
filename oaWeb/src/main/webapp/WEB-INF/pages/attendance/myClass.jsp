<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="../common/common.jsp"%>
<style type="text/css">
  .workLog{
    height: 40px;
    line-height: 40px;
    padding-left: 40px;
    font-size: 16px;
    color: #333;
    text-align: left;
  }
</style>
<link rel="icon" href="data:;base64,=">
  <title>我的排班</title>
    <meta charset="UTF-8">
</head>

<body class="mt-44">
    <div class="index-scheduling">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>login/index.htm" class="goback fl"><i class="back sr"></i></a>我的排班<a class="toproblem fr"></a></h1>
        </header>
        <div class="selMonth">
            <input type="text" class="all-date" id="all-date" data-lcalendar="2015-01-01,9999-12-31" value="${yearMonth }" readOnly /><!-- 2017年06月 -->
        </div>
        <div id="calendarBox"></div>
        <!-- <div class="showWorkInfo">2017年6月8日，农历五月十四，星期四</div>
        <p class="workTime">工作时间：09:00 — 18:45</p> --> 
        
        
        <c:forEach var="item" items="${list}" varStatus="status"> 
	        <div class="showWorkInfo"><fmt:formatDate type="date" value="${item.classDate }" pattern="yyyy年MM月dd日 "/></div>
	        <c:if test="${item.name != null }">
	            <p class="workTime">班次： ${item.name }&nbsp;&nbsp;工作时间：<fmt:formatDate type="date" value="${item.startTime }" pattern="HH:mm"/> — <fmt:formatDate type="date" value="${item.endTime }" pattern="HH:mm"/></p>
                <p class="workLog"></p>
            </c:if>
	        <c:if test="${item.name == null }">
	            <p class="workTime">休息</p>
	            <p class="workLog"></p>
            </c:if>
        </c:forEach> 
    </div>
    
    <%-- <script src="<%=basePath%>js/attendance/h_date.js"></script> --%>
    <script src="<%=oaI2Ule%>/ule/oa/j/lib/h_date.js"></script>
</body>

</html>