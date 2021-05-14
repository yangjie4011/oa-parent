<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>排班查询</title>
<script type="text/javascript" src="<%=basePath%>js/employeeClass/query.js?v=20180426"/></script>
</head>
<body class="mt-44 b-f2f2f2">
    <div class="paiban-chaxun">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>排班查询<a class="toproblem fr"></a></h1>
        </header>
        <input type="hidden" id="token" name="token" value="${token}"/>
        <div class="tab">
            <ul>
                <li class="on">
                    <p>日常排班</p>
                </li>
                <li>
                    <p>法定节假日</p>
                </li>
            </ul>
        </div>
        <div class="main">
            <ul>
                <!-- 日常排班 -->
                <li class="tab-main on">
                    <div class="sel">
                        <div class="selMatter clearfix" id="selBumen">
                            <h4 class="fl">部门</h4>
                            <div class="selarea fr applay-event">
                                <input id="depart" type="text" readonly="" placeholder="全部" value="全部" partid="1">
                                <i class="sr icon"></i>
                            </div>
                        </div>
                        <div class="selMatter clearfix">
                            <h4 class="fl">年月</h4>
                            <div class="selarea fr applay-event">
                                <input type="text" readonly="" id="selDate" placeholder="请选择年月">
                                <i class="sr icon"></i>
                            </div>
                        </div>
                    </div>
                    <div onclick="getEmployeeClassByDepartAndMonth();" class="blue-btn">查询</div>
                    <div class="bottom-main" id="employeeClassList">
                    </div>
                </li>
                <!-- 法定节假日排班 -->
                <li class="tab-main right-tab">
                    <div class="sel">
                        <div class="selMatter clearfix" id="selBumen2">
                            <h4 class="fl">部门</h4>
                            <div class="selarea fr applay-event">
                                <input id="depart1" type="text" readonly="" placeholder="全部" value="全部" partid="1">
                                <i class="sr icon"></i>
                            </div>
                        </div>
                        <div class="selMatter clearfix">
                            <h4 class="fl">法定假期</h4>
                            <div class="selarea fr applay-event">
                                <input type="text" readonly="" id="selDate2" placeholder="请选择假日">
                                <i class="sr icon"></i>
                            </div>
                        </div>
                        <div class="selMatter clearfix">
                            <h4 class="fl">年份</h4>
                            <div class="selarea fr applay-event">
                                <input type="text" readonly="" id="year" placeholder="请选择年份">
                                <i class="sr icon"></i>
                            </div>
                        </div> 
                    </div>
                    <div class="blue-btn" onclick="getEmployeeDutyByDepartAndVacation();">查询</div>
                    <div class="bottom-main" id="employeeDutyList">
                    </div>
                </li>
            </ul>
        </div>

    </div>
    <script>
        $(function () {
            listTypeSelect.init('type002');
        })
    </script>
</body>
</html>