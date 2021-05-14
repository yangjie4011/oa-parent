<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>编辑值班</title>
<script type="text/javascript" src="<%=basePath%>js/employeeClass/update_vacation.js?v=20180408"/></script>
</head>
<body class="mt-44 b-f2f2f2 mb-50">
    <div>
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>employeeClass/index.htm" class="goback fl"><i class="back sr"></i></a>编辑值班<a id="${duty.id }" onclick="save(this);" class="toproblem fr">保存</a></h1>
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
            <input type="hidden" id="hoildayName" value="${duty.vacationName }"/>
            <input type="hidden" id="departyName" value="${duty.departName }"/>
            <input type="hidden" id="departId" value="${duty.departId }"/>
            <input type="hidden" id="token" value="${token }"/>
        </div>
    </div>
    <div class="tab-list">
        <c:forEach var="item" items="${vacationList}" varStatus="status">
             <c:if test="${status.first==true}">
                 <div class="tab-list-li on">
	             <c:if test="${dutyDetailMap[item.annualDate]!=null&&dutyDetailMap[item.annualDate].size()>0}">
	                  <c:forEach var="item1" items="${dutyDetailMap[item.annualDate]}" varStatus="status1">
		                           <div class="paiban-list">
						               <ul id="${item1.id }">
						                   <li>
						                       <div class="head">值班事项 </div>
						                   </li>
						                   <li>
						                       <div class="main">
						                           <h4 class="l">法定假期</h4>
						                           <div class="r">
						                               ${duty.vacationName }
						                           </div>
						                       </div>
						                   </li>
						                   <li>
						                       <div class="main">
						                           <h4 class="l">值班部门</h4>
						                           <div class="r">
						                               ${duty.departName }
						                           </div>
						                       </div>
						                   </li>
						                   <li id="selstaff${status.index}${status1.index}"  employeeIds="${item1.employeeIds }">
						                       <div class="main">
						                           <h4 class="l">值班人员</h4>
						                           <div class="r">
						                               <span>${item1.employeeNames }</span>
						                               <i class="icon"></i>
						                           </div>
						                       </div>
						                   </li>
						                   <li class="starttime">
						                       <div class="main">
						                           <h4 class="l">开始时间</h4>
						                           <div class="r">
						                               <input value="<fmt:formatDate value="${item1.startTime}"  pattern="HH:mm"  />" id="stime${status.index}${status1.index}" type="text" readonly="" placeholder="请选择开始时间" data-lcalendar="2017-06-17,9999-12-31">
						                               <i class="icon"></i>
						                           </div>
						                       </div>
						                   </li>
						                   <li  class="endtime">
						                       <div class="main">
						                           <h4 class="l">结束时间</h4>
						                           <div class="r">
						                               <input value="<fmt:formatDate value="${item1.endTime}"  pattern="HH:mm"  />" id="etime${status.index}${status1.index}" type="text" readonly="" placeholder="请选择结束时间" data-lcalendar="2017-06-17,9999-12-31">
						                               <i class="icon"></i>
						                           </div>
						                       </div>
						                   </li>
						                   <li workHours="${item1.workHours}">
						                       <div class="main">
						                           <h4 class="l">工作小时数</h4>
						                           <div id="worktime${status.index}${status1.index}" class="r">
						                               ${item1.workHours}h
						                           </div>
						                       </div>
						                   </li>
						                   <li>
						                       <textarea  placeholder="请输入值班事项" cols="30" rows="10">${item1.dutyItem}</textarea>
						                   </li>
						                   <li class="delDuty">                    
						                       <div class="del">删除</div>                
						                   </li>
						               </ul>
					              </div>
	                  </c:forEach>
	                  </c:if>
	                  <c:if test="${dutyDetailMap[item.annualDate]==null||dutyDetailMap[item.annualDate].size()==0}">
				           <div class="paiban-list">
				               <ul id="">
				                   <li>
				                       <div class="head">值班事项 </div>
				                   </li>
				                   <li>
				                       <div class="main">
				                           <h4 class="l">法定假期</h4>
				                           <div class="r">
				                               ${duty.vacationName }
				                           </div>
				                       </div>
				                   </li>
				                   <li>
				                       <div class="main">
				                           <h4 class="l">值班部门</h4>
				                           <div class="r">
				                               ${duty.departName }
				                           </div>
				                       </div>
				                   </li>
				                   <li id="selstaff${status.index}0" employeeIds="">
				                       <div class="main">
				                           <h4 class="l">值班人员</h4>
				                           <div class="r">
				                               <span>请选择值班人员</span>
				                               <i class="icon"></i>
				                           </div>
				                       </div>
				                   </li>
				                   <li class="starttime">
				                       <div class="main">
				                           <h4 class="l">开始时间</h4>
				                           <div class="r">
				                               <input id="stime${status.index}0" type="text" readonly="" placeholder="请选择开始时间" data-lcalendar="2017-06-17,9999-12-31">
				                               <i class="icon"></i>
				                           </div>
				                       </div>
				                   </li>
				                   <li  class="endtime">
				                       <div class="main">
				                           <h4 class="l">结束时间</h4>
				                           <div class="r">
				                               <input id="etime${status.index}0" type="text" readonly="" placeholder="请选择结束时间" data-lcalendar="2017-06-17,9999-12-31">
				                               <i class="icon"></i>
				                           </div>
				                       </div>
				                   </li>
				                   <li workHours="0">
				                       <div class="main">
				                           <h4 class="l">工作小时数</h4>
				                           <div id="worktime${status.index}0" class="r">
				                               0h
				                           </div>
				                       </div>
				                   </li>
				                   <li>
				                       <textarea  placeholder="请输入值班事项" cols="30" rows="10"></textarea>
				                   </li>
				                   <li class="delDuty">                    
						               <div class="del">删除</div>                
						           </li>
				               </ul>
				           </div>
	                  </c:if>
	                  </div>
	             </c:if>
             <c:if test="${status.first==false}">
	             <div class="tab-list-li">
			           <c:if test="${dutyDetailMap[item.annualDate]!=null&&dutyDetailMap[item.annualDate].size()>0}">
			                <c:forEach var="item1" items="${dutyDetailMap[item.annualDate]}" varStatus="status1">
		                           <div class="paiban-list">
						               <ul id="${item1.id }">
						                   <li>
						                       <div class="head">值班事项 </div>
						                   </li>
						                   <li>
						                       <div class="main">
						                           <h4 class="l">法定假期</h4>
						                           <div class="r">
						                               ${duty.vacationName }
						                           </div>
						                       </div>
						                   </li>
						                   <li>
						                       <div class="main">
						                           <h4 class="l">值班部门</h4>
						                           <div class="r">
						                               ${duty.departName }
						                           </div>
						                       </div>
						                   </li>
						                   <li id="selstaff${status.index}${status1.index}" employeeIds="${item1.employeeIds }">
						                       <div class="main">
						                           <h4 class="l">值班人员</h4>
						                           <div class="r">
						                               <span>${item1.employeeNames }</span>
						                               <i class="icon"></i>
						                           </div>
						                       </div>
						                   </li>
						                   <li class="starttime">
						                       <div class="main">
						                           <h4 class="l">开始时间</h4>
						                           <div class="r">
						                               <input value="<fmt:formatDate value="${item1.startTime}"  pattern="HH:mm"  />" id="stime${status.index}${status1.index}" type="text" readonly="" placeholder="请选择开始时间" data-lcalendar="2017-06-17,9999-12-31">
						                               <i class="icon"></i>
						                           </div>
						                       </div>
						                   </li>
						                   <li  class="endtime">
						                       <div class="main">
						                           <h4 class="l">结束时间</h4>
						                           <div class="r">
						                               <input value="<fmt:formatDate value="${item1.endTime}"  pattern="HH:mm"  />" id="etime${status.index}${status1.index}" type="text" readonly="" placeholder="请选择结束时间" data-lcalendar="2017-06-17,9999-12-31">
						                               <i class="icon"></i>
						                           </div>
						                       </div>
						                   </li>
						                   <li workHours="${item1.workHours}">
						                       <div class="main">
						                           <h4 class="l">工作小时数</h4>
						                           <div id="worktime${status.index}${status1.index}" class="r">
						                               ${item1.workHours}h
						                           </div>
						                       </div>
						                   </li>
						                   <li>
						                       <textarea  placeholder="请输入值班事项" cols="30" rows="10">${item1.dutyItem}</textarea>
						                   </li>
						                   <li class="delDuty">                    
						                       <div class="del">删除</div>                
						                   </li>
						               </ul>
					              </div>
	                      </c:forEach>
			           </c:if>
			           <c:if test="${dutyDetailMap[item.annualDate]==null||dutyDetailMap[item.annualDate].size()==0}">
			               <div class="paiban-list">
				               <ul id="">
				                   <li>
				                       <div class="head">值班事项</div>
				                   </li>
				                   <li>
				                       <div class="main">
				                           <h4 class="l">法定假期</h4>
				                           <div class="r">
				                               ${duty.vacationName }
				                           </div>
				                       </div>
				                   </li>
				                   <li>
				                       <div class="main">
				                           <h4 class="l">值班部门</h4>
				                           <div class="r">
				                               ${duty.departName }
				                           </div>
				                       </div>
				                   </li>
				                   <li id="selstaff${status.index}0" employeeIds="">
				                       <div class="main">
				                           <h4 class="l">值班人员</h4>
				                           <div class="r">
				                               <span>请选择值班人员</span>
				                               <i class="icon"></i>
				                           </div>
				                       </div>
				                   </li>
				                   <li  class="starttime">
				                       <div class="main">
				                           <h4 class="l">开始时间</h4>
				                           <div class="r">
				                               <input id="stime${status.index}0" type="text" readonly="" placeholder="请选择开始时间" data-lcalendar="2017-06-17,9999-12-31">
				                               <i class="icon"></i>
				                           </div>
				                       </div>
				                   </li>
				                   <li  class="endtime">
				                       <div class="main">
				                           <h4 class="l">结束时间</h4>
				                           <div class="r">
				                               <input id="etime${status.index}0" type="text" readonly="" placeholder="请选择结束时间" data-lcalendar="2017-06-17,9999-12-31">
				                               <i class="icon"></i>
				                           </div>
				                       </div>
				                   </li>
				                   <li workHours="0">
				                       <div class="main">
				                           <h4 class="l">工作小时数</h4>
				                           <div id="worktime${status.index}0" class="r">
				                               0h
				                           </div>
				                       </div>
				                   </li>
				                   <li>
				                       <textarea  placeholder="请输入值班事项" cols="30" rows="10"></textarea>
				                   </li>
				                   <li class="delDuty">                    
						               <div class="del">删除</div>                
						           </li>
				               </ul>
		                  </div>
			           </c:if>
	             </div>
             </c:if>
        </c:forEach>
    </div>

    <div class="foot-btn">
        添加值班事项
    </div>
    <script>
    $(function () {
        listTypeSelect.init('bianjipaibjjr');
    })
   </script>
</body>
</html>