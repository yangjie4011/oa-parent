<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="data:;base64,=">
<title>办公自动化系统后台—主页</title>
<%@include file="../common/common2.jsp"%>
<!-- <link rel="icon" type="image/x-icon" href="favicon.ico"  /> -->
<link rel="stylesheet" type="text/css"  href="<%=domainUle%>/cun/platform/c/common_v1.css?v1" />
<script src="<%=domainUle1 %>/cun/platform/j/jquery-1.8.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>js/index/main.js?v=20210316"  type="text/javascript"></script>
<style type="text/css">
	.areaon{color:#fff; background:red;}
	table td {
		padding: 6px 20px;
		line-height: 20px;
	}
</style>
</head>
<body>
<div class="container">
	<div class="header" id="topDiv">
		<div class="bar">
			<div class="fl">
           		<a href="javascript:(0)" class="logo-ule">邮乐网</a>办公自动化系统
           		<!-- <span class="region">河南省</span> -->
			</div>
            <div class="fr">
                <ul>
                    <li>你好，<div id="myCenter" class="myCenter"><a class="blue-f-d fb" href="">${emp.cnName}<i class="iad"></i></a>
						<div class="usetrool">
	                		<a onclick="update()">修改密码</a>
	                	</div>                    	
                    </div>
                   	</li>
                    <li><a  href="<%=basePath%>login/logout.htm" title=""><i class="icon exit" title="退出"></i></a></li>
                </ul>
            </div> 
        </div><!-- bar end -->
        <div class="nav clear" id = "rowMenuDiv">
        	<c:forEach items="${resList }" var="resource" varStatus="vs">
      			<a href="${resource.url }" title="${resource.resourceName}" onclick="rowMenuClick(${resource.id })"  name="myResource"  id="${resource.id }">${resource.resourceName}</a>
        	</c:forEach>
        </div><!-- nav end -->
    </div><!-- header end -->
    <div class="main clear" id="mainDiv">
    	<div class="menu" id="lefeMenuDiv">  	  
        </div>   
    	<div class="content" id="contentDiv" style="height:100%;width:100%;padding:0 170px;">
             <iframe src="${pageContext.request.contextPath}/login/toDefault.htm" 
                     name="mainFrame" width="100%" height="100%" id="mainFrame" scrolling="yes" frameborder="0">
             </iframe>
        </div>   
    </div>
 
    
    <!-- end  修改开始-->
   	<!-- 更新 -->
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 38%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>修改密码</strong>
						</div>
						<div class="form-main">
							<form id="updateForm">
								<input type="hidden" id="id" name="id" value="${emp.id}"/>
								<div class="col">
									<div class="form">
										<label class="w">原密码</label><input type="password"
											class="form-text" name="oldPwd" id="oldPwd" placeholder="请输入原密码"/>
									</div>									
								</div>
								
								<div class="col">
									<div class="form">
										<label class="w">新密码</label><input type="password"
											class="form-text"  name="password" id="password" placeholder="请输入新密码">
									</div>									
								</div>
								
								<div class="col">
									<div class="form">
										<label class="w">确认新密码</label><input type="password"
											class="form-text" id="password2" placeholder="请再次输入新密码">
									</div>									
								</div>
																		
								
							</form>	
				
							
							<div class="col">
								<div class="button-wrap ml-4">
									<button class="red-but" onclick="save();">
										<span>保存</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv('updateDiv');">
										<span>返回</span>
									</button>
								</div>
							</div>
						</div>
					</div>
					<!-- form-main end -->
				</div>
			</div>
		</div>
	</div>         
     
    <div class="commonBox popbox" id="messageBox" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center conf" style="margin-top: 0px;">
			<div class="popbox-main" style="height: 200px;">
				<div class="cun-pop-content">
					<div class="cun-pop-title">
						<h4>提示</h4>
					</div>
					<div class="cun-pop-contentframe">
						<i class="box-icon yes"></i>
						<p id="messageContent">更新成功！</p>
					</div>
					<div class="button-wrap" style="width: 180px;">
						<button class="red-but" id="messageButton" onclick="closeDiv('messageBox');">
							<span>确认</span>
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>    
</div><!-- container end -->
</body>
</html>