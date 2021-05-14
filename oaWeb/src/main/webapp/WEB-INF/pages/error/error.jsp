<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>OA—内部错误页面</title>
</head>
<body>
	错误状态码:	${code }	<br/>
	错误信息:	${msg }		<br/>
	备注:		${remark }	<br/>
</body>
</html>