<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@include file="../../common/common.jsp"%>
<script src="<%=basePath%>js/static/resetPwd.js?v=2017070704007"  type="text/javascript"/></script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="data:;base64,=">
<title>OA—忘记密码</title>
</head>
	<body>
		<form id="modifyForm">
		    <div class="mima">
		        <header>
		            <h1 class="clearfix"><a onclick="javascript:window.history.go(-1)" class="goback fl"><i class="back sr"></i></a>忘记密码<a href="#" class="save fr"></a></h1>
		        </header>
		        <div class="main">
		        	<div class="module_v3 bor-none">
		                <i class="p3"></i>
		                <input type="text" placeholder="确认找回的帐号" name="userName" id="username" onblur="getPhone(this.value);"/>
		            </div>
		            <div class="module_v3 bor-none">
		                <i class="p3"></i>
		                <input type="text" placeholder="请输入手机号" name="phone" id="phone" readonly="readonly"/>
		                <div class="phone"></div>
		            </div>
		            <div class="module_v3">
		                <i class="p3"></i>
		                <input type="text" placeholder="请输入验证码" name="randomCode" id = "randomCode"/>
		                <div class="yanz">获取验证码</div>
		            </div>
		            <div class="module_v3">
		                <i class="p2"></i>
		                <input type="password" placeholder="请输入新密码" name="password" id="password"/>
		            </div>
		        </div>
		        <div class="bot">
		            <div class="long-btn" onclick="save();">保存</div>
		        </div>
		    </div>
	    </form>
	</body>
</html>