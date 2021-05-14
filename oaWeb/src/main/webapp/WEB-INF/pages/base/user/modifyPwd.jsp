<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@include file="../../common/common.jsp"%>
<script src="<%=basePath%>js/base/user/modifyPwd.js?v=20180129004"  type="text/javascript"/></script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="data:;base64,=">
<title>修改密码</title>
</head>
	<body>
		<form id="modifyForm">
		    <div class="mima">
		        <header>
		            <h1 class="clearfix"><a onclick="javascript:window.history.go(-1)" class="goback fl"><i class="back sr"></i></a>修改密码<a href="#" class="save fr"></a></h1>
		        </header>
		        <div class="main">
		            <div class="module_v3">
		                <i class="p2"></i>
		                <input type="password" placeholder="请输入原密码" name="oldPwd" id="oldPwd"/>
		            </div>
		            <div class="module_v3">
		                <i class="p2"></i>
		                <input type="password" placeholder="请输入新密码" name="password" id="password"/>
		            </div>
		            <div class="module_v3">
		                <i class="p2"></i>
		                <input type="password" placeholder="请再次输入新密码" id="password2"/>
		            </div>
		        </div>
		        <div class="bot">
		            <div class="long-btn" onclick="save();">保存</div>
		        </div>
		    </div>
	    </form>
	</body>
</html>