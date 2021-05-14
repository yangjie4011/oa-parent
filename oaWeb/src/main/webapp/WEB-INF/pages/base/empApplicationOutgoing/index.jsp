<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>外出申请</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationOutgoing/index.js?v=20210311"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="oa-out_apply">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>外出申请<a href="#" class="save fr"></a></h1>
        </header>

        <section class="edit-info">
            <div class="selMatter clearfix">
                <h4 class="fl">外出日期</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择外出日期" id="out-time" data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">外出开始时间</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择时间" id="out-start" data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">外出结束时间</h4>
                <div class="selarea fr">
                    <input type="text" readonly placeholder="请选择时间" id="out-end" data-lcalendar="2017-01-01,9999-12-12">
                    <i class="sr icon"></i>
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">外出时长</h4>
                <div duration="0" id="duration" class="selarea fr">
                   
                </div>
            </div>

            <div class="selMatter clearfix">
                <h4 class="fl">外出地点</h4>
                <div class="selarea fr">
                    <input name="address" id="address" type="text"/>
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">联系电话</h4>
                <div class="selarea fr">
                    <input name="mobile" id="mobile" type="number" oninput="if(value.length>11)value=value.slice(0,11)" placeholder="请输入手机号码" value="${mobile}"/>
                </div>
            </div>

            <div class="selMatter clearfix datenum texta">
                <h4 class="fl">外出事由</h4>
                <textarea id="reason" name="reason" rows="5" placeholder="请填写申请理由"></textarea>
            </div>

        </section>
         <input type="hidden" id="token" name="token" value="${token}"/>
        <div class="btn" onclick="save();">提交</div>
    </div>
</body>
</html>