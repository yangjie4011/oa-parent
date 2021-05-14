<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<%@include file="../common/common.jsp"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="data:;base64,=">
<title>登录</title>
<script src="<%=basePath%>js/static/login.js?v=201806511"  type="text/javascript"/></script>
</head>
	<body>
		<form id="loginForm">
			<!-- 默认不勾选 -->
			<input type="hidden" id="remFlag" name="remFlag" value=""/>
			<input type="hidden" id="forwardSuccessUrl" value="${requestScope.forwardSuccessUrl }"/>
		
		    <div class="enter">
		        <div class="top">
		            <div class="head-img" id="ePic"></div>
		        </div>
		        <div class="main">
		            <div class="module_v3">
		                <i class="p1"></i>
		                <input type="text" name="username" id="username" placeholder="请输入用户名" onkeyup="checkPicByName(this.value)"/>
		            </div>
		            <div class="module_v3">
		                <i class="p2"></i>
		                <input type="password" name="password" id="password" placeholder="请输入密码" />
		            </div>
		        </div>
		        <div class="bot">
		            <div class="long-btn">登录</div>
		            <div class="t">
		                <div class="radio">记住密码</div>
		                <a href="http://passport2.uletm.com/user/modify-password">忘记密码？</a>
		            </div> 
		        </div>
		    </div>
	    </form>
	</body>
</html>