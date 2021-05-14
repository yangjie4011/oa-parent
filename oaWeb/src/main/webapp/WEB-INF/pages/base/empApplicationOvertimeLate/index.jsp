<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>晚到申请</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationOvertimeLate/index.js?v=20180408"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="oa-late_apply">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>晚到申请<a href="#" class="save fr"></a></h1>
        </header>

        <section class="edit-info">
            <div class="selMatter clearfix">
               <h4 class="fl">延时工作日期</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择日期" id="late-date" data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div> 
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">延迟工作时间</h4>
                <div endWorkTime="" class="selarea fr">

                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">允许晚到时间</h4>
                <div allowTime="" class="selarea fr">
                    
                </div>
            </div>
            <div class="selMatter clearfix datenum texta">
                <h4 class="fl">实际晚到时间</h4>
                <div actualTime="" class="selarea fr">
                    
                </div>
                <textarea id="reason" rows="5" placeholder="请填写申请理由"></textarea>
            </div>
        <input type="hidden" id="type" value=""/>
        </section>
        <input type="hidden" id="token" name="token" value="${token}"/>
        <div class="btn" onclick="save();">提交</div>
    </div>
</body>
</html>