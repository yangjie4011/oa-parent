<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="data:;base64,=">
<title>办公自动化系统后台—主页</title>
<base target="mainFrame"/>
<%@include file="../common/common.jsp"%>
</head>

<body>
	<c:forEach items="${resList }" var="res">
		<h3><a href='<%=basePath%>${res.url }'>${res.menuName }</a><br/></h3>
	</c:forEach>
</body>