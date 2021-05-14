<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>更多应用</title>
</head>
<body class="b-f2f2f2 mt-55">
       <div class="icon-add">
           <header>
            <h1 class="clearfix"><a href="<%=basePath%>login/index.htm"  class="goback fl"><i class="back sr"></i></a>更多应用&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</h1>
           </header>
           <div class="main-h" style="display: none;">
               <div class="h">
                   <div class="mid">
                       <p>首页应用</p>
                       <ul>
                           <li></li>
                           <li></li>
                           <li></li>
                           <li></li>
                       </ul>
                       <i>编辑</i>
                   </div>
               </div>
           </div>
           <div class="main">
               <div class="h">全部流程 (${allRunCount })</div>
               <ul class="func-list">
                   <c:if test="${workAddressType == 0 || workAddressProvince eq '北京'}">
                   		<li class="p1"><a href="<%=basePath%>empApplicationLeave/index.htm?urlType=2" title="请假"><i></i>请假</a></li>
                   </c:if>
                   <c:if test="${workAddressType == 0 || workAddressProvince eq '北京'}">
	               		<li class="p2"><a href="<%=basePath%>empApplicationOvertime/index.htm?urlType=2" title="延时工作"><i></i>延时工作</a></li>
				   </c:if>
				   <c:if test="${workAddressType == 0 || workAddressProvince eq '北京'}">
                   		<li class="p3"><a href="<%=basePath%>empApplicationAbnormalAttendance/index.htm?urlType=2" title="异常考勤"><i></i>异常考勤</a></li>
                   </c:if>
                   <c:if test="${(isSetPerson==true || isDh==true) && (workAddressType == 0 || workAddressProvince eq '北京')}">
                      <li class="p16"><a href="<%=basePath%>employeeClass/index.htm?urlType=1" title="排班"><i></i>排班</a></li>
                   </c:if>
                   <li class="p8"><a href="<%=basePath%>empApplicationBusiness/index.htm?urlType=2" title="出差"><i></i>出差</a></li>
                   <li class="p9"><a href="<%=basePath%>empApplicationOutgoing/index.htm?urlType=2" title="外出"><i></i>外出</a></li>
               </ul>
           </div>
           <c:if test="${depart == 107 }">
	           <div class="main">
	               <div class="h">人事管理 (3)</div>
	               <ul class="func-list">
                       <li class="p17"><a href="<%=basePath%>employeeClass/query.htm?urlType=2" title="排班查询"><i></i>排班查询</a></li>
	                   <li class="p5"><a href="<%=basePath%>employeeApp/index.htm?urlType=2" title="员工查询"><i></i>员工查询</a></li>
	                   <li class="p15"><a href="<%=basePath%>employeeRegister/toRegister.htm?urlType=2" title="新员工入职"><i></i>新员工入职</a></li>
	                   <li class="p7"><a href="<%=basePath%>runTask/allExamine.htm?urlType=2"><i></i>全部审批</a></li>
	                   <li class="p7"><i><a href="<%=basePath%>empAttn/toEmployeeAttn.htm?urlType=2" title="考勤查询"><i></i>考勤查询</a></li>
	               </ul>
	           </div>
           </c:if>
           <div class="main">
               <div class="h">常用资料 (1)</div>
               <ul class="func-list">
                   <li class="p6"><i><a href="<%=basePath%>employeeApp/addressList.htm?urlType=2" title="通讯录"><i></i>通讯录</a></li>
               </ul>
           </div>
       </div>
   </body>
</html>