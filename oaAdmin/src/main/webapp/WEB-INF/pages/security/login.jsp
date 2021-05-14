<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<%@include file="../common/common.jsp"%>
<link rel="stylesheet" type="text/css" href="<%=domainUle%>/jifen/cloud/c/admin/170502/hfer.css"/>
<link rel="stylesheet" type="text/css" href="<%=domainUle%>/c/cas/initForm.css"/>
<link rel="icon" type="image/png" href="<%=domainUle%>/i/hp/100806/ulelogo.png" />
<script src="<%=basePath%>js/static/login.js?v=20180129002"  type="text/javascript"/></script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="data:;base64,=">
<title>办公自动化系统后台-登录</title>
</head>
	<body>
		<form id="loginForm">
			<!-- 默认不勾选 -->
			<input type="hidden" id="remFlag" name="remFlag" value=""/>
			
			<div class="header">
				<div class="head-main hfer-con">
					<div class="head-logo">
						<a href="javascript:(0)" class="logo-ule">邮乐网</a>办公自动化系统
					</div>
				</div>
			</div>
			<div id="wrapper">
			<div>
				<div class="wrapper-box">
				</div>
			</div>
			<div class="login-box">
				<div class="login-form">
					<span class="box-title">登录</span>
					<div class="ph-wrap">
						<span class="email-icon">账号</span>
						<input type="text" name="username" id="username" class="account" autocomplete="off" placeholder="请输入用户名" onkeyup="checkPicByName(this.value)"/>
						<span class="del delph"></span>
					</div>
					<div class="input-wrap">
						<span class="pwd-icon">密码</span>
						<input type="password" name="password" id="password" autocomplete="off" class="password" placeholder="请输入密码" />
						<span class="del delpw"></span>
					</div>
					<input type="button" id="formSubmit" value="立即登录" tabindex="4" class="login-btn"/>
					<div class="autologin">
						<span class="forgot"><a href="http://passport2.uletm.com/user/modify-password" target="_blank" class="blueLink">忘记密码?</a></span>
					</div>
				
				</div>
			</div>
			<!--浮标-->
			<div class="wrapper-container">
				<span  class="wrapper-btn"></span>
			</div>
		</div>
	    </form>
	</body>
</html>