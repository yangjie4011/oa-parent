<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">·
<title>查看值班</title>
<script type="text/javascript" src="<%=basePath%>js/employeeClass/hr_move_vacation.js?v=20180408"/></script>
</head>
<body class="mt-44 b-f2f2f2 mb-50">
    <div>
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>employeeClass/query.htm" class="goback fl"><i class="back sr"></i></a>查看值班</h1>
        </header>
        <div class="y-silde">
            <ul id="vacationList">
                <c:forEach var="item1" items="${vacationList}" varStatus="status">
                   <c:if test="${status.first==true}">
	                    <li class="on">
	                      <p><fmt:formatDate value="${item1.annualDate}"  pattern="yyyy-MM-dd"  /></p>
	                    </li>
                   </c:if>
                   <c:if test="${status.first==false}">
	                   <li>
	                      <p><fmt:formatDate value="${item1.annualDate}"  pattern="yyyy-MM-dd"  /></p>
	                   </li>
                   </c:if>
                </c:forEach>
            </ul>
            <input type="hidden" id="token" value="${token }"/>
        </div>
    </div>
      <div class="tab-list">
        <c:forEach var="item" items="${vacationList}" varStatus="status">
             <c:if test="${status.first==true}">
             <div class="tab-list-li on">
             <c:forEach var="item1" items="${dutyDetailList}" varStatus="status1">
                  <c:if test="${item.annualDate==item1.vacationDate}">
			           <div class="paiban-list">
			               <ul>
			                   <li>
			                       <div class="head">值班事项</div>
			                   </li>
			                   <li>
			                       <div class="main">
			                           <h4 class="l">法定假期</h4>
			                           <div class="r">
			                               ${vacationName}
			                           </div>
			                       </div>
			                   </li>
			                   <li>
			                       <div class="main">
			                           <h4 class="l">值班部门</h4>
			                           <div class="r">
			                               ${departName}
			                           </div>
			                       </div>
			                   </li>
			                   <li>
			                       <div class="main">
			                           <h4 class="l">值班人员</h4>
			                           <div class="r">
			                               ${item1.employeeNames}
			                           </div>
			                       </div>
			                   </li>
			                   <li>
			                       <div class="main">
			                           <h4 class="l">开始时间</h4>
			                           <div class="r">
			                               <fmt:formatDate value="${item1.startTime}"  pattern="HH:mm"  />
			                           </div>
			                       </div>
			                   </li>
			                   <li>
			                       <div class="main">
			                           <h4 class="l">结束时间</h4>
			                           <div class="r">
			                               <fmt:formatDate value="${item1.endTime}"  pattern="HH:mm"  />
			                           </div>
			                       </div>
			                   </li>
			                   <li>
			                       <div class="main">
			                           <h4 class="l">工作小时数</h4>
			                           <div class="r">
			                              ${item1.workHours }h
			                           </div>
			                       </div>
			                   </li>
			                   <li>
			                       <textarea readonly=""  placeholder="请输入值班事项" cols="30" rows="10">${item1.dutyItem}</textarea>
			                   </li>
			               </ul>
			           </div>
	             
                  </c:if>
             </c:forEach>
             </div>
             </c:if>
             <c:if test="${status.first==false}">
             <div class="tab-list-li">
             <c:forEach var="item1" items="${dutyDetailList}" varStatus="status1">
		           <c:if test="${item.annualDate==item1.vacationDate}">
		            <div class="paiban-list">
		               <ul>
		                   <li>
		                       <div class="head">值班事项</div>
		                   </li>
		                   <li>
		                       <div class="main">
		                           <h4 class="l">法定假期</h4>
		                           <div class="r">
		                               ${vacationName }
		                           </div>
		                       </div>
		                   </li>
		                   <li>
		                       <div class="main">
		                           <h4 class="l">值班部门</h4>
		                           <div class="r">
		                               ${departName }
		                           </div>
		                       </div>
		                   </li>
		                   <li>
		                       <div class="main">
		                           <h4 class="l">值班人员</h4>
		                           <div class="r">
		                               ${item1.employeeNames}
		                           </div>
		                       </div>
		                   </li>
		                   <li>
		                       <div class="main">
		                           <h4 class="l">开始时间</h4>
		                           <div class="r">
		                               <fmt:formatDate value="${item1.startTime}"  pattern="HH:mm"  />
		                           </div>
		                       </div>
		                   </li>
		                   <li>
		                       <div class="main">
		                           <h4 class="l">结束时间</h4>
		                           <div class="r">
		                               <fmt:formatDate value="${item1.endTime}"  pattern="HH:mm"  />
		                           </div>
		                       </div>
		                   </li>
		                   <li>
		                       <div class="main">
		                           <h4 class="l">工作小时数</h4>
		                           <div class="r">
		                               ${item1.workHours }h
		                           </div>
		                       </div>
		                   </li>
		                   <li>
		                       <textarea readonly="" cols="30" rows="10">${item1.dutyItem}</textarea>
		                   </li>
		               </ul>
		           </div>
		           </c:if>
		     </c:forEach>
             </div>
             </c:if>
        </c:forEach>
    </div>
    <script>
    $(function () {
        listTypeSelect.init('bianjipaibjjr');
    })
   </script>
</body>
</html>