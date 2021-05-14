<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>排班</title>
</head>
<body class="mt-44 b-f2f2f2">
    <div class="paiban-chaxun">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>排班<a class="toproblem fr"></a></h1>
        </header>
        <div class="tab">
            <ul>
                <c:if test="${isSetPerson == true}">
	                <li class="on">
	                    <p>日常排班</p>
	                </li>
                </c:if>
                <c:if test="${isDh == true}">
	                <li <c:if test="${isSetPerson == false}">class="on"</c:if>>
	                    <p>法定节假日</p>
	                </li>
                </c:if>
            </ul>
        </div>
        <input type="hidden" id="token" name="token" value="${token}"/>
        <input type="hidden" id="isShowAll" name="isShowAll" value="0"/>
        <input type="hidden" id="lastMonth" name="lastMonth" value="${lastMonth}"/>
        <input type="hidden" id="isSetPerson"  value="${isSetPerson}"/>
        <input type="hidden" id="isDh" value="${isDh}"/>
        <div class="main">
            <ul>
                <!-- 日常排班 -->
                <c:if test="${isSetPerson == true}">
                   <li class="tab-main right-tab on">
                        <div class="moudle-add">
                            <span>班次说明</span>
                        </div>
                        <div class="moudle-class">
                            <ul id="classSetList">
                            
                            </ul>
                        </div>
                        <div class="moudle-add">
                                <span>排班设置</span>
                        </div>
                        <div id="employeeClassList">

                        </div> 
                    </li>
                </c:if>
                <!-- 法定节假日排班 -->
                <c:if test="${isDh == true}">
	                <li class="tab-main right-tab">
	                    <div class="moudle-add">
	                        <span>排班设置</span>
	                     	<span onclick="toAddVacation();" class="r">新增</span>
	                    </div>
	                    <div id="employeeDutyList">
	       
	                    </div>
	                </li>
                </c:if>
            </ul>
        </div>

    </div>
    <script type="text/javascript" src="<%=basePath%>js/employeeClass/index.js?v=20190411"/></script>
    <script>
        $(function () {
            listTypeSelect.init('type001');
        })
    </script>
</body>
</html>