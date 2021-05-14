<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>异常考勤申诉</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationAbnormalAttendance/index.js?v=20190701"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="oa-kaoqin_apply">
        <header>
            <h1 class="clearfix"><a <c:if test="${applyType==0}">href="<%=basePath%>${returnUrl}"</c:if><c:if test="${applyType==1}">href="<%=basePath%>empAttn/subIndex.htm?employId=${employId}&startTime=${startTime }&endTime=${endTime }"</c:if> class="goback fl"><i class="back sr"></i></a>异常考勤申诉<a href="#" class="save fr"></a></h1>
        </header>

        <section class="edit-info">
            <c:if test="${applyType==0}">
               <p class="notice">每月2次申诉机会，填写上下班打卡时间各记1次机会</p>
               <p class="notice">，且每个考勤日期仅可申请一次</p>
            </c:if>
            <div class="selMatter clearfix datenum" <c:if test="${applyType==0}">style="display:none"</c:if>>
                <h4 class="fl">异常考勤员工</h4>
                <div class="selarea fr">
                   ${employeeName}
                </div>
            </div>
            <div class="selMatter clearfix datenum" <c:if test="${applyType==0}">style="display:none"</c:if>>
                <h4 class="fl">代申诉人</h4>
                <div class="selarea fr">
                 ${agentName}
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">考勤日期</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择日期" id="kaoqin-date" data-lcalendar="2017-01-01,9999-12-12" value="${attnDate }">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">考勤时间</h4>
                <div class="selarea fr">
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">申诉上班打卡</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择当日考勤时间" id="kaoqin-start"  data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">申诉下班打卡</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择当日考勤时间" id="kaoqin-end" data-nextday="0" data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>

            <div class="selMatter clearfix texta">
                <h4 class="fl">申诉理由</h4>
                <textarea id="reason" rows="5" placeholder="请填写考勤异常原因，例如：忘打卡，地铁故障等"></textarea>
            </div>
        </section>
        <input type="hidden" id="token" name="token" value="${token}"/>
        <input type="hidden" id="type" name=""type"" value="0"/>
        <input type="hidden" id="startPunchTime" name="startPunchTime" value=""/>
        <input type="hidden" id="endPunchTime" name="endPunchTime" value=""/>
        <input type="hidden" id="applyType" name=""applyType"" value="${applyType}"/>
        <input type="hidden" id="employId" name=""employId"" value="${employId}"/>
        <div class="btn" onclick="save();">提交</div>
    </div>
</body>
</html>