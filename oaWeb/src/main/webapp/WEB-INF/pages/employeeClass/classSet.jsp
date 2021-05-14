<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>新增班次</title>
<script type="text/javascript" src="<%=basePath%>js/employeeClass/classSet.js?v=20180408"/></script>
</head>
<body class="mt-44 b-f2f2f2">
    <div>
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>employeeClass/index.htm" class="goback fl"><i class="back sr"></i></a>新增班次<a class="toproblem fr"></a></h1>
        </header>
    </div>
    <div class="banci-list">
        <ul>
            <li>
                <div class="main">
                    <h4 class="l">班次名称</h4>
                    <div class="r">
                          <input type="text" placeholder="请填写班次名称" id="name">
                          <span>班</span>
                    </div>
                </div>
            </li>
            <li>
                <div class="main">
                    <h4 class="l">上班</h4>
                    <div class="r">
                        <input type="text" readonly="" placeholder="请填写开始时间" id="start" >
                        <i class="icon"></i>
                    </div>
                </div>
            </li>
            <li>
                <div class="main">
                    <h4 class="l">下班</h4>
                    <div class="r">
                        <input type="text" readonly="" placeholder="请填写结束时间" id="end" >
                        <i class="icon"></i>
                    </div>
                </div>
            </li>
            <li>
                    <div class="main">
                        <h4 class="l">出勤工时</h4>
                        <div id="mustAttnTime" mustAttnTime="" class="r">
                            0h
                        </div>
                    </div>
                </li>
        </ul>
    </div>
    <input type="hidden" id="token" name="token" value="${token}"/>
    <div onclick="addClassSet();"class="foot-btn">
        确认新增
    </div>
    <script>
            $(function(){
                listTypeSelect.init('addbanci');
            })
    </script>
</body>
</html>