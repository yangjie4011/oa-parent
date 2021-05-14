<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>我的消息</title>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<%=basePath%>js/common/oaCommon.js?v=20170515"/>"></script>
<script type="text/javascript" src="<%=basePath%>js/base/empMsg/index.js?v=2017052334"/>"></script>
</head>
<body>
	<form class="dataform datatable" style="padding-bottom: 0px;" >
	    <div class="datatitle">
		    <ul class="fl jui-tabswitch">
		       <li id="all" rel="#fastTab" class="active">全部<span></span></li>
		       <li class="" id="unRead" rel="#commonTab">未读</li>
		    </ul>
		</div>
	</form>
	<div class="oa mt5">
		<table id="userMsgList"></table>
	</div>
</body>
</html>