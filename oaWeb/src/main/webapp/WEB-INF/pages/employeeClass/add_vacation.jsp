<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>编辑排班</title>
<script type="text/javascript" src="<%=basePath%>js/employeeClass/add_vacation.js?v=20180408"/></script>
</head>
<body class="mt-44 b-f2f2f2">
    <div class="paiban-chaxun">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>employeeClass/index.htm" class="goback fl"><i class="back sr"></i></a>排班<a class="toproblem fr"></a></h1>
        </header>
    </div>
    <input type="hidden" id="token" name="token" value="${token}"/>
    <div class="banci-list" id="add_paiban">
            <ul>
                <li>
                    <div class="main">
                        <h4 class="l">排班人</h4>
                        <div class="r">
                               ${classSetPerson }
                        </div>
                    </div>
                </li>
                <li id="holiday">
                        <div class="main">
                            <h4 class="l">节假日</h4>
                            <div class="r">
                                <input type="text" readonly="" placeholder="元旦" value="元旦">
                                <i class="icon"></i>
                            </div>
                        </div>
                    </li>
                    <li id="depart">
                        <div class="main">
                            <h4 class="l">参与部门</h4>
                            <div class="r">
                                <input departId="" value="" type="text" readonly="" placeholder="全部" >
                                <i class="icon"></i>
                            </div>
                        </div>
                    </li>
            </ul>
        </div>
        <div onclick="addClassDuty()" class="foot-btn">
            保存
        </div>
    <script>
        $(function () {
            listTypeSelect.init('addpaiban');
        })
    </script>
</body>
</html>