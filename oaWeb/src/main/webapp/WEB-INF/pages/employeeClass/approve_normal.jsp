<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>排班审核</title>
<script type="text/javascript" src="<%=basePath%>js/employeeClass/h_date.js?v=20170207"/></script>
<script type="text/javascript" src="<%=basePath%>js/employeeClass/approval_normal.js?v=20190315"/></script>
</head>
<body class="mt-44">
    <div class="bj-paiban index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>排班审核<a class="toproblem fr"></a></h1>
        </header>
         <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${employeeClass.processInstanceId}&statu=${employeeClass.approvalStatus}&title=排班审核">
                <div class="img"></div>
                <div class="p1">
                    <c:if test="${employeeClass.approvalStatus==100}">
                         处理中，等待<span>[${taskVO.actName}]</span>审批
                    </c:if>
                    <c:if test="${employeeClass.approvalStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${employeeClass.approvalStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${employeeClass.approvalStatus==400}">
                         已撤销
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${employeeClass.createTime}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
                <i></i>
            </a>
        </div>
        <input id="departId" type="hidden" value="${employeeClass.departId}">
        <input id="isMove" type="hidden" value="${employeeClass.isMove}">
        <input id="classMonth" type="hidden" value="${classMonth}">
        <input id="classdetailId" type="hidden" value="${employeeClass.id}">
        <input type="hidden" id="token" name="token" value="${token}"/>
        <div class="y-silde">
                <ul id="employeeList">

                </ul>
            </div>
        <div class="selMonth">
            <input type="text" class="all-date" id="all-date" data-lcalendar="2015-01-01,9999-12-31" value="${month}" readOnly />
        </div>
        <div id="calendarBox"></div>
        <div class="showWorkInfo">2017年6月8日，农历五月十四，星期四</div>
        <p class="workTime">班次： 常  工作时间：09:00 — 18:00</p>
        <c:if test="${employeeClass.isMove==1}">
           <p class="adjustWorkTime">调整为： 常  工作时间：09:00 — 18:00</p>
        </c:if>
    </div>
    <c:if test="${canApprove}">
       <div class="double-btn">
           <div id="${employeeClass.processInstanceId}" onclick="refuseNormal(this);" class="l"><em>拒绝</em></div>
           <div id="${employeeClass.processInstanceId}"  onclick="passNormal(this);" class="r"><em>同意</em></div>
       </div>
   </c:if>
   <c:if test="${employeeClass.approvalStatus==100 and isSelf}">
        <div id="${employeeClass.processInstanceId}" onclick="cancelRunTask(this)" class="foot-btn cancel-apply"><em>撤回申请</em></div>
   </c:if>
    <script>
            $(function(){
                OA.ySilde();
            })
        </script>
</body>
</html>