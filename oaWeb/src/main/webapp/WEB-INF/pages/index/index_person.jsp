<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<%@include file="../common/common.jsp"%>
	<link rel="icon" href="data:;base64,=">
  	<title>我</title>
    <meta charset="UTF-8">
</head>

<body class="mt-170 b-f2f2f2">
    <div class="oa-personal">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>login/index.htm" class="goback fl"><i class="back sr"></i></a>我<a href="#" class="toproblem fr"></a></h1>
            <section class="my-info">
                <div class="head"  style="background:#fff url('${empPic }') no-repeat center;background-size:contain;-webkit-background-size:contain;"><%-- <img src="${empPic }" alt=""> --%></div>
                <div class="name">
                    <p>${cnName }</p>
                    <!-- <p>产品管理部－产品部／产品经理</p> -->
                    <p>${departName }／${positionName }</p>
                    <p>汇报人：${reportToLeader}</p>
                </div>
            </section>
        </header>

        <div class="person-nav">
            <p class="resume"><a href="<%=basePath%>employeeApp/toMyInfoCheck.htm?urlType=8">个人履历</a><i class="sr"></i></p>
            <c:if test="${workAddressType == 0}">
	            <p class="check"><a href="<%=basePath%>empAttn/index.htm?urlType=1">我的考勤</a><i class="sr"></i></p>
	            <p class="holiday"><a href="<%=basePath%>empLeave/myLeaveView.htm?urlType=1">我的假期</a><i class="sr"></i></p>
            </c:if>
            <p class="batch"><a href="<%=basePath%>ruProcdef/my_examine.htm?urlType=1&current=1">我的审批</a><i class="sr"></i></p>
            <p class="apply"><a href="<%=basePath%>runTask/index.htm?urlType=1">我的申请</a><i class="sr"></i></p>
            <p class="change"><a href="<%=basePath%>user/toModifyPwd.htm">修改密码</a><i class="sr"></i></p>
        </div>
        <a href="javascript:logout()">
            <div class="foot-btn loginout">退出登录</div>
        </a>
        
    </div>
<script src="<%=basePath%>js/static/login.js?v=20170705"  type="text/javascript"/></script>
</body>

</html>