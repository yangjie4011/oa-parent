<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%@include file="../../common/common.jsp"%>
<link type="text/css" rel="stylesheet" href="<%=domainUle%>/cun/platform/c/common.css">
<link type="text/css" rel="stylesheet" href="<%=domainUle%>/cun/platform/c/text.css">
<script src="<%=basePath%>js/static/resetPwd.js?v=2017070704013"  type="text/javascript"/></script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="data:;base64,=">
<title>办公自动化系统后台—忘记密码</title>
</head>
	<body>
		<!--修改密码-->
		<div class="change_mb"><i class="cm_icon" ></i>重置密码</div>
		<div id="pass">
		    <div id="updatePass">
		        <form id="updateForm" name="" method="post" action="">
		        	<div class="col">
		                <div class="form" id="oldpass" >
		                    <label>请输入帐号</label>
	                    	<input type="text" placeholder="确认找回的帐号" name="userName" id="username" class="form-text" onblur="getPhone(this.value);"/>
		                </div>
		            </div>
		            
		            <div class="col">
		                <div class="form">
		                    <label>手机号</label>
		                    <input type="text" name="phone" id="phone" class="form-text" readonly="readonly"/>		  
		                </div>
		            </div>
		            
		            <div class="col">
		                <div class="form">
		                    <label>请输入验证码</label>
		                   	<input type="text" placeholder="请输入验证码" name="randomCode" class="form-text" id="randomCode"/>
		                    &nbsp;&nbsp;&nbsp;&nbsp;
		                    <button id="btn" type="button"  state="off" value="获取验证码"  class="blue-but" style="margin-left: 8px" ><span class="yanz" id="getyzm">获取验证码</span></button>
		                </div>
		            </div>
		        
		            <div class="col">
		                <div class="form" id="newpass1">
		                    <label>请输入新密码</label>
		                    <input type="password"  name="password" id="password" placeholder="请输入新密码">		                    
		                    <i class="login-icon eye" id="eye1" state="off"></i>		                    
		                </div>
		            </div> 		            
		            <div class="col">
		                <div class="form" id="newpass2">
		                    <label>再确认新密码</label>
		                    <input type="password" placeholder="请再次确认新密码" id="password2" name="password2"/>
		                    <i class="login-icon eye" id="eye2" state="off"></i>
		                </div>
		                <div class="form cred" style="display:none;"><i class="icon no"></i>两次输入的密码不一致</div>
		            </div> 
		        </form>
	            <div class="button-wrap">
                	<button class="red-but disabled"><span><i class="icon"></i>忘记密码</span></button>            
            	</div>	
            	<div class="safetip"  style="margin-top: 125px;">
		            <i class="icon ps-jt"></i>
		            <ul>
		                <li id="safe1" >安全程度：<i class="login-icon safe"></i></li>
		                <li id="safe2" class="ml20"><i class="login-icon safe"></i>密码不少于8位字符</li>
		                <li id="safe3" class="ml20"><i class="login-icon safe"></i>密码必须包含特殊字符、大写字母、小写字母、数字其中两种</li>
		            </ul>
		        </div>
		    </div>
		</div>
	</body>
</html>