<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>值班审核</title>
<script type="text/javascript" src="<%=basePath%>js/employeeClass/approval_vacation.js?v=20180514"/></script>
</head>
<body class="mt-44 b-f2f2f2 index-kaoqin checkOnDuty">
    <div>
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>值班审核</h1>
        </header>
        <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${duty.processInstanceId}&statu=${duty.approvalStatus}&title=值班审核">
                <div class="img"></div>
                <div class="p1">
                    <c:if test="${duty.approvalStatus==100}">
                         处理中，等待<span>[${taskVO.actName}]</span>审批
                    </c:if>
                    <c:if test="${duty.approvalStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${duty.approvalStatus==300}">
                         已打回
                     </c:if>
                     <c:if test="${duty.approvalStatus==400}">
                         已撤销
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${duty.createTime}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
                <i></i>
            </a>
        </div>
        <div class="y-silde">
            <ul>
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
            <input type="hidden" id="token" name="token" value="${token}"/>
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
   
	<c:if test="${canApprove}">
	   <div class="oa-textarea">
	        <textarea id="approvalReason" name="approvalReason" placeholder="请填写审批意见（非必填）" cols="30" rows="10"></textarea>
	   </div>
       <div class="double-btn">
            <div  id="${duty.processInstanceId }" onclick="refuseVacation(this);" class="l"><em>打回</em></div>
            <div  id="${duty.processInstanceId }" onclick="passVacation(this);" class="r"><em>同意</em></div>
       </div>
    </c:if>
    <c:if test="${duty.approvalStatus==100 and isSelf}">
        <div class="oa-textarea">
	        <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
	    </div>
        <div id="${duty.processInstanceId}" onclick="cancelRunTask(this)" class="foot-btn cancel-apply"><em>撤回申请</em></div>
    </c:if>
    <script>
        $(function () {
            OA.ySilde();
        })
    </script>
</body>
</html>