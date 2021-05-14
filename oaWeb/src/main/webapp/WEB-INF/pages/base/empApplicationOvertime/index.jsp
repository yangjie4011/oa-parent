<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>延时工作申请</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationOvertime/index.js?v=20190906"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="oa-late_work_apply">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>延时工作申请<a href="#" class="save fr"></a></h1>
        </header>

        <section class="edit-info">
            <c:if test="${wetherSchedule}">
               <p class="notice">如有跨天延时工作，“延时工作日期”请选择前一天排班日</p>
            </c:if>
            <div class="selMatter clearfix">
                <h4 class="fl">延时工作日期</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择日期" value="" id="late-work-date" data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">预计开始时间</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择时间" value="" id="start-time" data-nextday="0" data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">预计结束时间</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择时间" value="" id="end-time" data-nextday="0"  data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>

            <div class="selMatter clearfix datenum">
                <h4 class="fl">延时工作时长</h4>
                <div id="duration" duration="${duration }" class="selarea fr">
                    ${duration } 小时
                </div>
            </div>
            
            <div class="selMatter clearfix">
                <h4 class="fl">申请事由</h4>
                <div class="selarea fr applay-event">
                    <input type="text" readonly placeholder="请选择申请事由">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="project-info">
                <div class="selMatter clearfix">
                    <h4 class="fl">项目名称</h4>
                    <div class="selarea fr">
                        <input name="projectName" id="projectName" type="text" placeholder="请输入项目名称" />
                    </div>
                </div>
                <div class="selMatter clearfix">
                    <h4 class="fl">项目ID</h4>
                    <div class="selarea fr">
                        <input type="number" name="projectId" id="projectId" type="text" placeholder="请输入项目ID" />
                    </div>
                </div>
            </div>

            <div class="selMatter clearfix datenum texta">
                <h4 class="fl">事由说明</h4>
                <textarea id="reason" name="reason" rows="5" placeholder="请填写申请理由"></textarea>
            </div>
		<input type="hidden"  id="isWork" />
        </section>
        <input type="hidden" id="token" name="token" value="${token}"/>
        <div class="btn" onclick="save();">提交</div>
    </div>
</body>
</html>