<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>假期历史</title>
<meta charset="UTF-8">
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationLeave/leave_record.js?v=20200531"/></script>
</head>
<style>
.selMon{
	position: absolute;
    width: 55px;
    height: 25px;
    text-align: center;
    line-height: 25px;
    font-size: 12px;
    color: #fff;
    border-radius: 3px;
    background: rgba(255,255,255,.3);
    right: 15px;
    top: 25%;
    padding-right: 10px;

}

.selMon ul{
	position: absolute;
    width: 100%;
    top: 26px;
    left: 0;
    z-index: 99;
}

.selMon:after {
    content: '';
    position: absolute;
    width: 0;
    height: 0;
    border: 4px solid transparent;
    border-top-color: #fff;
    top: 50%;
    transform: translateY(-20%);
    right: 10px;
}


</style>

<body class="b-f2f2f2" style="margin-top: 60px;">
    <div class="oa-timecard">
        <header style="height: 60px;">
        	<h1 class="clearfix"><a href="<%=basePath%>empLeave/myLeaveView.htm" class="goback fl"><i class="back sr"></i></a><p>假期历史</p>
        		<div class="selMon">
                    <span class="thisYear"  style="padding:0;position:relative;font-size:12px;" id="thisYear">当年</span>
                    <span class="lastYear"  style="display:none;padding:0;position:relative;font-size:12px;" id="lastYear">去年</span>
                    <ul class="lastYearUl" style="display:none" id="lastYearUl">
                        <li><span style="padding:0;position:relative;font-size:12px;">去年</span></li>
                    </ul>
                </div>
        	</h1>
        </header>
        <ul class="punchtime"></ul>
        <a href="<%=basePath%>empLeave/myLeaveView.htm" ><div class="foot-btn">返回</div></a>
    </div>

</body>

</html>