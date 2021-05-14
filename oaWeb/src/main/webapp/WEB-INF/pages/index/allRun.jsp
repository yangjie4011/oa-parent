<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>发起申请</title>
<script type="text/javascript" src="<%=basePath%>js/index/main.js?v=201706161"></script>
</head>
<body class="b-f2f2f2 mt-55">
       <div class="icon-add">
           <header>
            <h1 class="clearfix"><a href="<%=basePath%>login/index.htm"  class="goback fl"><i class="back sr"></i></a>发起申请&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</h1>
           </header>
           <div class="main">
               <div class="h">考勤流程 (${allRunCount })</div>
               <ul class="func-list">
                   <c:if test="${workAddressType == 0}">
                   		<li class="p1"><a href="<%=basePath%>empApplicationLeave/index.htm?urlType=7" title="请假"><i></i>请假</a></li>
                   </c:if>
                   <c:if test="${workAddressType == 0}">
	               		<li class="p2"><a href="<%=basePath%>empApplicationOvertime/index.htm?urlType=7" title="延时工作"><i></i>延时工作</a></li>
				   </c:if>
				   <c:if test="${workAddressType == 0}">
                   		<li class="p3"><a href="<%=basePath%>empApplicationAbnormalAttendance/index.htm?urlType=7" title="异常考勤"><i></i>异常考勤</a></li>
                   </c:if>
                   <li class="p8"><a href="<%=basePath%>empApplicationBusiness/index.htm?urlType=7" title="出差"><i></i>出差</a></li>
                   <li class="p9"><a href="<%=basePath%>empApplicationOutgoing/index.htm?urlType=7" title="外出"><i></i>外出</a></li>
               </ul>
           </div>
       </div>
   </body>
</html>