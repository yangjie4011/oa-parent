<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="data:;base64,=">
<title>办公自动化系统后台—主页</title>
<%@include file="../common/common.jsp"%>
<link rel="icon" type="image/x-icon" href="favicon.ico"  />
<link rel="stylesheet" type="text/css"  href="<%=domainUle%>/cun/platform/c/common_v1.css?v1" />
<script src="<%=basePath%>js/index/main.js?v=20200324"  type="text/javascript"/></script>
</head>
<body>
	<div class="container">
		<div class="header">
			<div class="bar">
				<div class="fl">
	           		<a href="javascript:(0)" class="logo-ule">邮乐网</a>办公自动化系统
				</div>
	            <div class="fr">
	                <ul>
	                    <li>
	                    	你好，
	                    	<div id="myCenter" class="myCenter"><a class="blue-f-d fb" href="">${emp.cnName }<i class="iad"></i></a>
								<div class="usetrool">
			                		<a href="">修改密码</a>
			                		<a href="">修改手机号</a>
			                	</div>                    	
		                    </div>
	                   	</li>
	                    <li>
	                    	<a href="<%=basePath%>login/logout.htm" title=""><i class="icon exit" title="退出"></i></a>
	                    </li>
	                </ul>
	            </div>
	        </div><!-- bar end -->
	        <div class="nav clear">
	        	<c:forEach items="${resList }" var="resource">
	        		<a href="${resource.url }" title="${resource.resourceName }" onclick="getSecMenu(${resource.id })">${resource.resourceName }</a>
	        	</c:forEach>
	        </div><!-- nav end -->
	    </div><!-- header end -->
	    
	    <div class="main clear" id="bottomDiv">
	    	<div id="leftDiv" style="width:10%;border-right:1px solid #d9d9d9;border-top:1px solid #d9d9d9;border-bottom:1px solid #d9d9d9;float:left;background-color:#f1f1f1;">
	        	<div id="secMenu">
	            	<iframe src="<%=basePath%>login/toLeft.htm" name="leftFrame" id="leftFrame" height="100%" width="100%" scrolling="no" frameborder="0"></iframe>
	            </div>
	        </div>
	    
	    	<div id="mainDiv">
	    		<iframe src="${pageContext.request.contextPath}/login/toDefault.htm" name="mainFrame" id="mainFrame" height="100%" width="100%" scrolling="no" frameborder="0"></iframe>	    	
	    	</div>
	    </div>
	    
	</div><!-- container end -->
</body>
</html>