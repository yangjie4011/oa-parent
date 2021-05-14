<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>编辑排班</title>
<script type="text/javascript" src="<%=basePath%>js/employeeClass/h_date.js?v=20170207"/></script>
<script type="text/javascript" src="<%=basePath%>js/employeeClass/update.js?v=20180408"/></script>
</head>
<style type="text/css">
   .selMonth:after{content:;}
</style>
<body class="mt-44">
    <div class="bj-paiban">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>employeeClass/index.htm" class="goback fl"><i class="back sr"></i></a>编辑排班<a class="toproblem fr"></a></h1>
        </header>
        <input id="departId" type="hidden" value="${departId}">
        <input id="classMonth" type="hidden" value="${classMonth}">
        <input id="classdetailId" type="hidden" value="${classdetailId}">
        <input id="token" type="hidden" value="${token}">
        <div class="y-silde">
                <ul id="employeeList">
                 
                </ul>
            </div>
        <div class="selMonth">
            <input type="text" class="all-date" id="all-date" data-lcalendar="2015-01-01,9999-12-31" value="${month}" readOnly />
        </div>
        <div id="calendarBox"></div>
        <div class="showWorkInfo">2017年6月8日，农历五月十四，星期四</div>
        <p class="workTime">工作时间：09:00 — 18:45</p>
    </div>
    <div onclick="save();" class="foot-btn">
        保存
    </div>
</body>
</html>