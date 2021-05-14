<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = com.ule.oa.common.utils.UrlRedirect.getHttpUrl(request) + "/";
	request.setAttribute("basePath",basePath);
	String random = String.valueOf(new SimpleDateFormat("yyyyMMddhhmm").format(new Date()));
	
	String domainUle = (String) com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer.getProperty("DOMAIN_ULE");
	request.setAttribute("domainUle",domainUle);
	
	String oaUle = (String) com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer.getProperty("OA_ULE");
	request.setAttribute("oaUle",oaUle);
	
	String referer = request.getHeader("referer");

%>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">
<script src="<%=basePath%>js/static/jquery-3.2.1.js" type="text/javascript"></script>

<script src="<%=basePath%>js/util/form-json.js" type="text/javascript"></script>
<script src="<%=domainUle %>/j/jend/jend.js" type="text/javascript"></script>
<script src="<%=domainUle %>/ape/v2/common/js/jend.ape.js" type="text/javascript"></script>
<script src="<%=domainUle %>/ape/v2/cs/frameset.js" type="text/javascript"></script> 
<!-- 通用js -->
<script src="<%=basePath %>js/common/oaCommon.js?v=20171123005" type="text/javascript"></script>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta content="telephone=no" name="format-detection">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="apple-mobile-web-app-capable" content="yes">
<!-- uc强制竖屏 -->
<meta name="screen-orientation" content="portrait">
<!-- UC强制全屏 -->
<meta name="full-screen" content="yes">
<!-- UC应用模式 -->
<meta name="browsermode" content="application">
<!-- QQ强制竖屏 -->
<meta name="x5-orientation" content="portrait">
<!-- QQ强制全屏 -->
<meta name="x5-fullscreen" content="true">
<!-- QQ应用模式 -->
<meta name="x5-page-mode" content="app">

<!--插件资源-->
<script src="https://i2.beta.ulecdn.com/ule/oa/j/lib/iscroll5.js"></script>
<link href="https://i1.beta.ulecdn.com/ule/oa/c/common.css" rel="stylesheet">
<!--页面资源-->
<link href="https://i1.beta.ulecdn.com/ule/oa/c/index.css" rel="stylesheet">
 <!--插件资源-->
<script src="https://i2.beta.ulecdn.com/ule/oa/j/lib/zepto.min.js"></script>
<script src="https://i2.beta.ulecdn.com/ule/oa/j/lib/lCalendar.js"></script>
<!--页面资源-->
<script src="https://i2.beta.ulecdn.com/ule/oa/j/common.js?v=20181218"></script>
<script src="https://i2.beta.ulecdn.com/ule/oa/j/index.js?v=20181218"></script>
<script type="text/javascript">
	var contextPath = "<%=path%>";
	var basePath = "<%=basePath%>";
	$(function(){
		// 设置jQuery Ajax全局的参数  
		$.ajaxSetup({
			cache : false,
			dataType : "json",
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				switch (XMLHttpRequest.status) {
				case (500):
					OA.titlePup('操作失败！','lose');
					break;
				case (400):
					break;
				}
			},
			complete: function(XMLHttpRequest,textStatus){
				var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
				if(sessionstatus == "timeout"){
					window.location.href = XMLHttpRequest.getResponseHeader("Location");
			 	}
				if (XMLHttpRequest.status == 401) {
					OA.titlePup('无权限执行此操作！','lose');
				}
			}
		});
	});

	// 返回上一页 
	function historyBack(){ 
		location.replace(document.referrer);
	} 
</script>