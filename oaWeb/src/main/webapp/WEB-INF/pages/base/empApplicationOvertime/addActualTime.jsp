<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>延时工作申请</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationOvertime/approve.js?v=20190918"></script>
</head>
<body class="b-f2f2f2 mt-44">
        <div class="oa-late_work_apply">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>延时工作申请<a href="#" class="save fr"></a></h1>
        </header>
        <section class="edit-info">
            <input type="hidden" id="isWork" value="${isWork }"/>
            <div class="selMatter clearfix">
                <h4 class="fl">延时工作日期</h4>
                <div class="selarea fr">
                     <input value="<fmt:formatDate value="${overtime.applyDate}"  pattern="yyyy-MM-dd"  />" type="text" readonly placeholder="请选择日期" id="late-work-date" data-lcalendar="<fmt:formatDate value="${overtime.applyDate}"  pattern="yyyy-MM-dd"  />,<fmt:formatDate value="${overtime.applyDate}"  pattern="yyyy-MM-dd"  />">
                     <i class="sr icon"></i>
                </div>
             </div>
             <div class="selMatter clearfix">
                <h4 class="fl">预计工作时长</h4>
                <div class="selarea fr">
                   <fmt:formatDate value="${overtime.expectStartTime}"  pattern="HH:mm"  />-<c:if test="${overtime.expectStartTime.getHours()>=overtime.expectEndTime.getHours()}">
                                      次日
                                </c:if><fmt:formatDate value="${overtime.expectEndTime}"  pattern="HH:mm"  />&nbsp;&nbsp;${overtime.expectDuration}小时
                </div>
             </div>
             <div class="selMatter clearfix">
                <h4 class="fl">实际开始时间</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择时间" id="start-time" value="${startTime}"  data-nextday="${s_data_nextday }"  data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
             </div>
             <div class="selMatter clearfix">
                <h4 class="fl">实际结束时间</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择时间" id="end-time"  data-nextday="${e_data_nextday }"  data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">实际工作时长</h4>
                <div class="selarea fr" id="duration" duration="${duration}">
                </div>
                <input type="hidden" value="" id="actualDuration"/>
            </div>
        </section>
        <div class="double-btn">
	        <div class="l"  id="${overtime.processInstanceId}"  onclick="cancelRunTask(this)" ><em>撤回</em></div>
	        <div applyDate="<fmt:formatDate value="${overtime.applyDate}"  pattern="yyyy-MM-dd"  />" id="${overtime.processInstanceId}" class="r" onclick="addActualTime(this);"><em>提交</em></div>
        </div>
        
        
       <!--   <div class="lateWorkApplyCancel"  id="${overtime.processInstanceId}"  onclick="cancelRunTask(this)" ><em>撤回申请</em></div>
	    <div applyDate="<fmt:formatDate value="${overtime.applyDate}"  pattern="yyyy-MM-dd"  />" id="${overtime.processInstanceId}" class="oa-late-work-btn" onclick="addActualTime(this);">提交</div>-->
        <input type="hidden" id="token" name="token" value="${token}"/>
        <input type="hidden" id="data_nextday" name="data_nextday" value="${data_nextday}"/>
    </div>
</body>
</html>