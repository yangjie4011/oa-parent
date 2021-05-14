<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>数据权限</title>
<%@include file="../../common/common2.jsp"%>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>

<script type="text/javascript" src="../js/base/role/user_dept.js?v=2019042303"/></script>
</head>
<body style="overflow-x:auto">
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li class="active"><strong>数据权限</strong></li>
		</ul>
	</div>
	
	<input id="hiddenUid" type="hidden">
	<input id="chooseRoleId" type="hidden">
	<input id="roleList" type="hidden">
	<div class="form-wrap">
        	<form id="queryFrom" class="queryFrom">
	                <div class="form-main">
		                <div class="col">
		                    <div class="form"><label class="w">用户名称：</label><input type="text" class="form-text" name="nickname" value=""></div>		                
		                    <div class="form"><label class="w">姓名编号：</label><input type="text" class="form-text" name="code" value=""></div>		                  		
			            </div>
	                </div>  
                </form>
            <div class="col">  
                <div class="button-wrap ml-4">
            		<button id="query" onclick= "query()" class="blue-but small"><span>查询</span></button>                
                	<button id="save" onclick= "save()" class="blue-but small"><span>保存</span></button>
                	<button id="returnbox" onclick= "retrunBox()" class="red-but small"><span>返回</span></button>
            	</div>
            </div>
    </div>
	<div class="content" >
		<table border="1">
      		<thead>
      			<tr>
      				<th style="overflow-x:auto;width:100px;text-align:center;">用户</th>
      				<th style="overflow-x:auto;width:100px;text-align:center;">备注</th>
      				<th style="overflow-x:auto;width:100px;text-align:center;">姓名</th>
      				<th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
      				<th style="overflow-x:auto;width:100px;text-align:center;">部门数据授权</th>
      			</tr>
      		</thead>
      		<tbody id="roleInfoDiv">
      		</tbody>
   		</table>  
    </div>
	<!-- 保存菜单确认弹框 -->
	<div class="commonBox popbox" id="saveResourceDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
					
							<div class="title">
								<strong><i class="icon add-user"></i>保存</strong>
							</div>
						
						<div class="form-main">

							<form id="saveResourceForm" class="insretForm">
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">请确认是否保存修改？</label>
									</div>
								</div>
							</form>
							<div class="col">
								<div class="button-wrap ml-4">
									<button id="zcqx" class="small grey-but"
										onclick="closeDiv();" style="width:120px;">
										<span>取消</span>
									</button>
									<button id="zcqr" class="red-but" onclick="saveResource();"  style="width:120px;">
										<span>保存</span>
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
	<!-- 确认保存 -->
	
</body>
</html>