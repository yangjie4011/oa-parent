<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>协作审批</title>
<script type="text/javascript" src="<c:url value="/js/util/dateUtil.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/common/oaCommon.js?v=20180627"/>"></script>
<script type="text/javascript" src="<c:url value="/js/base/cooperation/my_examine.js?v=20181125"/>"></script>

</head>
<body class="b-f2f2f2 mt-95">
    <div class="oa-examine">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>协作审批&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</h1>
        </header>
		<input type="hidden" value="${isLeader }" id="isLeader"/>
		<input type="hidden" value="${current }" id="current"/>
        <ul class="nav-tab">
        	<c:if test="${current == 0}">
	            <li class="current" onclick="myExamineing(1);"><p>待办</p></li>
	            <li class="" onclick="myExamined(2);"><p>已办</p></li>
	            <li class="" onclick="myAbate(3);"><p>失效</p></li>
         	</c:if>
         	<c:if test="${current != 0}">
	            <li class="" onclick="myExamineing(1);"><p>待办</p></li>
	            <li class="current" onclick="myExamined(2);"><p>已办</p></li>
	            <li class="" onclick="myAbate(3);"><p>失效</p></li>
         	</c:if>
        </ul>
        <div class="tab-info tabmodule2">
            <ol class="info-con current">
            	<div id="myExamineingList"></div>
            </ol>
            <ol class="info-con current">
            	<div id="myExaminedList"></div>
            	<div id="theEnd"></div>
            </ol>
            <ol class="info-con current">
            	<div id="myAbateList"></div>
            	<div id="myAbatetheEnd"></div>
            </ol>
        </div>
    </div>
</body>
</html>