<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>员工查询</title>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<%=basePath%>js/common/oaCommon.js?v=20170515"/>"</script>
<script type="text/javascript" src="<%=basePath%>js/base/employee/employee_index_app.js?v=20181015"/></script>
</head>


<body class="b-f2f2f2 mt-44">
    <div class="under-kq staff_search">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>员工查询<a class="toproblem fr"></a></h1>
        </header>
        <div class="t searchStaff">
            <div class="returnsearch">
                                                          取消
            </div>
            <div class="search">
                <i></i><input type="text" id="nameOrCode" placeholder="搜索员工编号或姓名" name="nameOrCode"/>
            </div>
            <div class="searchBtn" id="searchBtn">搜索</div>
        </div>
        <div class="sel selCompanyPartment">
            <i></i>
            <p>选择部门</p>
            <rr><input type="hidden" id="partId"/></rr>
        </div>
        <div class="three-tab">
            <ul>
                <li class="on onTheJob"><span>在职</span></li>
                <li class="offTheJob"><span>离职</span></li>
                <li class="allTheJob"><span>全部</span></li>
            </ul>
            <input type="hidden" id="jobStatus" name="jobStatus" value="0">
        </div>
        <div class="right-arrow position-bot top-199">
        	<ul id="empList">
        	</ul>
        </div>
        <div id="theEnd"></div>
    </div>

	<form id="checkForm" style="display:none" action="<%=basePath%>employeeApp/toEmployeeCheck.htm?urlType=9" method="post">
		<input type="hidden" id="checkEmployeeId" name="id"/>
	</form>
</body>
</html>