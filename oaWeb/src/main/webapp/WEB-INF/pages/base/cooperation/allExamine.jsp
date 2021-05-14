<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>全部审批</title>
<script type="text/javascript" src="<c:url value="/js/util/dateUtil.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/common/oaCommon.js?v=20180627"/>"></script>
<script type="text/javascript" src="<c:url value="/js/base/cooperation/allExamine.js?v=20180627"/>"></script>
</head>
<body class="mt-44 b-f2f2f2">
    <div class="oa-all_examine">
        <header>
            <h1 class="clearfix">
                <a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>
            </h1>
        </header>
		<div class="head-search">
            <div class="search">
                <i></i><input type="text" id="nameOrCode" placeholder="搜索员工编号或姓名" />
            </div>
            <div class="searchBtn" onclick="onSearchBtn();">搜索</div>
        </div>
        <div class="under-kq seach-area">
            <div class="sel selCompanyPartment">
	            <i></i>
	            <p>选择部门</p>
	            <rr><input type="hidden" id="partId"/></rr>
	        </div>
            <div class="sel">
                <i class="date"></i>
                <p><input type="text" name="" id="examineDate" readonly value="开始日期"></p>
            </div>
            <div class="sel">
                <i class="date"></i>
                <p><input type="text" name="" id="examineDate1" readonly value="结束日期"></p>
            </div>
        </div>
        <div class="tab-info tabmodule2 position-bot" style="top: 205px">
        	 <ol class="info-con current" id="allExamineDiv">
        	 	<div id="allExamineList"></div>
        	 	<div id="theEnd"></div>
        	 </ol>
        </div>
    </div>
</body>
</html>