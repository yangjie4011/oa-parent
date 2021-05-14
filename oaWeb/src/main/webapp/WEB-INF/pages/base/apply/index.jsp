<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>我的申请</title>
<script type="text/javascript" src="<%=basePath %>/js/util/dateUtil.js"></script>
<script type="text/javascript" src="<%=basePath %>/js/common/oaCommon.js?v=20180627"></script>
<script type="text/javascript" src="<%=basePath %>/js/base/apply/index.js?v=20181128"></script>
</head>
<body class="b-f2f2f2 mt-95">
    <div class="oa-apply">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}"   class="goback fl"><i class="back sr"></i></a>我的申请<a href="<%=basePath%>login/allRun.htm" class="correcting fr">发起申请</a></h1>
        </header>
        
        <ul class="correct-nav">
            <li class="current" onclick="processingLI();">处理中</li>
            <li onclick="processedLI();">已完成</li>
            <li onclick="processbackLI();">已撤回</li>
            <li onclick="processallLI();">全部</li>
        </ul>
        <div class="tab-info apply-tab-info tabmodule2">
            <ol class="info-con tabs-apply-con current">
            	<div id="processing"></div>
            	<div id="processingEnd"></div>
            </ol>
            <ol class="info-con tabs-apply-con">
            	<div id="processed"></div>
            	<div id="processedEnd"></div>
            </ol>
            <ol class="info-con tabs-apply-con">
                <div id="processback"></div>
               	<div id="processbackEnd"></div>
            </ol>
            <ol class="info-con tabs-apply-con">
                <div id="processall"></div>
                <div id="processallEnd"></div>
            </ol>
        </div>
    </div>

</body>
</html>