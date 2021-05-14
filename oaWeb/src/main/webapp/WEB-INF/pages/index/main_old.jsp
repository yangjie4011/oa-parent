<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>工作台</title>
<meta charset="UTF-8">
<script type="text/javascript" src="<%=basePath%>js/index/main.js?v=20170617"></script>
</head>

<body class="b-f2f2f2 mt-155">
<!-- <div class="return_heart" id="return_heart"><a href="#">随心邮</a></div> -->
    <div class="oa-index">
        <header>
            <h1 class="clearfix">
	            <a href="<%=basePath%>employee/indexPerson.htm" class="toperson fl"><i class="personal sr"></i></a>工作台
	            <a href="<%=basePath%>empMsg/index_mynews.htm" class="tonews fr"><i class="news sr">
	            	 <div id="unReadMsgCount"></div>
	            </i></a>
            </h1>
            <ul class="nav-list">
                <li>
                    <a href="<%=basePath%>empAttn/index.htm?urlType=2">
                            <i class="icon sr"></i>
                            <em class="text">我的考勤</em>
                        </a>
                </li>
                <li>
                    <a href="<%=basePath%>empLeave/myLeaveView.htm?urlType=2">
                            <i class="icon sr"></i>
                            <em class="text">我的假期</em>
                        </a>
                </li>
                <li>
                    <a href="<%=basePath%>ruProcdef/my_examine.htm?urlType=2">
                    		<i class="icon sr">
	                    		<div id="rpCount"></div>
                    		</i>
                            <em class="text">协作审批</em>
                        </a>
                </li>
                <li>
                    <a href="<%=basePath%>employeeClass/myClassView.htm">
                            <i class="icon sr"></i>
                            <em class="text">我的排班</em>
                        </a>
                </li>
            </ul>
        </header>
        <div class="module-v1">
         <!-- 发生产去掉这一行 end-->
            <ul class="func-list">
                <li class="p1"><a href="<%=basePath%>empApplicationLeave/index.htm?urlType=1" title="请假"><i></i>请假</a></li>
                <c:if test="${ruCode30 == 30}">
	                <li class="p2"><a href="<%=basePath%>empApplicationOvertime/index.htm?urlType=1" title="延时工作"><i></i>延时工作</a></li>
				</c:if>
                <li class="p3"><a href="<%=basePath%>empApplicationAbnormalAttendance/index.htm?urlType=1" title="异常考勤"><i></i>异常考勤</a></li>
                <c:if test="${isSetPerson == true || isDh == true}">
                   <li class="p16"><a href="<%=basePath%>employeeClass/index.htm?urlType=1" title="排班"><i></i>排班</a></li>
                </c:if>
                <li class="p4"><a href="<%=basePath%>runTask/index.htm?urlType=2" title="我的申请"><i></i>我的申请</a></li>
                <c:if test="${depart == 107 }">
                    <li class="p17"><a href="<%=basePath%>employeeClass/query.htm?urlType=1" title="排班查询"><i></i>排班查询</a></li>
	                <li class="p5"><a href="<%=basePath%>employeeApp/index.htm?urlType=1" title="员工查询"><i></i>员工查询</a></li>
	                <li class="p7"><a href="<%=basePath%>runTask/allExamine.htm?urlType=1"><i></i>全部审批</a></li>
	                <li class="p6"><i><a href="<%=basePath%>empAttn/toEmployeeAttn.htm?urlType=1" title="考勤查询"><i></i>考勤查询</a></li>
	                
                </c:if>
                <li class="p6"><a href="<%=basePath%>employeeApp/addressList.htm?urlType=1" title="通讯录"><i></i>通讯录</a></li>
                <li class="p14"><a href="<%=basePath%>login/indexAdd.htm?urlType=1"><i></i>更多</a></li>
            </ul>
        </div>
        <div class="module_v2">
            <div class="tab-info">
                <ul>
                    <li class="tabs-title current" onclick="ruProcdef();">待办
                    	<span id="unCount1"></span>
                    </li>
                    <li class="tabs-title" onclick="empMsg();">消息提醒
                    	<span id="unCount2"></span>
                    </li>
                </ul>
                <ol class="info-con tabs-con current">
                    <div id="ruProcdefList"></div>
                    <div id="ruProcdefMore"></div>
                </ol>
                <ol class="info-con tabs-con">
                    <div id="empMsgList"></div>
                    <div id="empMsgMore"></div>
                </ol>
            </div>
        </div>
        <div id="noData"></div>
    </div>
</body>
</html>