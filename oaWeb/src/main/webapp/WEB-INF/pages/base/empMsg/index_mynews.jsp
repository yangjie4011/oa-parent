<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>我的消息</title>
<script type="text/javascript" src="<%=basePath%>js/util/dateUtil.js"></script>
<script type="text/javascript" src="<%=basePath%>js/common/oaCommon.js?v=20170523"/></script>
<script type="text/javascript" src="<%=basePath%>js/base/empMsg/index_mynews.js?v=201170627"/></script>

</head>
<body class="mt-44">
     <div class="oa-mynews">
        <header>
            <h1 class="clearfix"><a  href="<%=basePath%>login/index.htm" class="goback fl"><i class="back sr"></i></a>我的消息<a href="#" class="toproblem fr"></a></h1>
        </header>
        <ul class="news-list">
            <div id="mynewsList"></div>
	        <div id="theEnd"></div>
        </ul>
    </div>
</body>
</html>