<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <%@include file="../common/common.jsp"%>
    <link rel="icon" href="data:;base64,=">
    <title>下属考勤</title>
    <script type="text/javascript" src="<%=basePath%>js/attendance/subAttendance.js?v=20170815"></script>
</head>

<body class="b-f2f2f2 mt-44">

	<form id="subForm" method="post">
	    <input type="hidden" id="startTime" name="startTime" value="${startTime}">
	    <input type="hidden" id="endTime" name="endTime" value="${endTime}">
	    <input type="hidden" id="employId" name="employId" value="${employId}"><!-- 我的员工ID -->
	    <input type="hidden" id="reportToLeader" name="reportToLeader" value="${employId}"><!-- 记录点击的下级员工的汇报对象，也就是我的员工ID，因为#employeeId会被替换，所以这里保存 -->
          <input type="hidden" id="urlType" name="urlType" value="3">  <!-- 返回我的考勤-->
	</form>    
    <div class="under-kq staff_search">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>下属考勤<a class="toproblem fr"></a></h1>
        </header>
        <div class="t searchStaff">
            <div class="returnsearch">取消</div>
            <div class="search">
                <i></i><input type="text" id="nameOrCode" placeholder="搜索员工编号或姓名">
            </div>
            <div class="searchBtn">搜索</div>
        </div>
       <!--  <div class="sel selCompanyPartment">
            <i></i>
            <p>选择部门</p>
            <rr><input type="hidden" id="partId"/></rr>
        </div> -->
        <div class="main">
            <ul class="liUl">
                <!-- <li>
                    <div>洒洒水</div>
                    <div>本月累计出勤：12331小时</div>
                    <div class="p3">异常考勤：123次</div>
                    <i></i>
                </li> -->
            </ul>
        </div>
    </div>
</body>

</html>