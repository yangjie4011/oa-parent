<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>角色管理</title>
<%@include file="../../common/common2.jsp"%>

<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>

<script type="text/javascript" src="../js/base/role/role_resource.js?v=20200606"/></script>
</head>
<body style="overflow-x:auto">
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li class="active"><strong>菜单权限</strong></li>
		</ul>
	</div>
	<br/>
	<div class="form-main">
		<div class="col">
			<div class="form">
		        <label class="w">部门：</label>
		        <select id="firstDepart" name="departId" class="select_v1" onChange='initAllRoleList()'>
		        </select>
		    </div>
	    </div>
	    <div class="col">
			<button id="addRole" onclick= "addRole()" class="blue-but" style="margin-bottom: 10px;"><span><i class="icon add"></i>新增角色</span></button>
		</div>
	</div>
	<div>
	<input id="departId" type="hidden">
	<input id="chooseRoleId" type="hidden">
	<input id="roleList" type="hidden">
	
	<div class="form-main">
        <div class="col4">
            <div class="form">
                <label class="w" style="width:130px;">当前选中权限角色：</label><input id="chooseRoleName" style="border:none;" type="text" value="" ></input>
                <button id="save" onclick= "save()" class="blue-but small"><span>保存</span></button>
	            <button id="delete" onclick= "del()" class="red-but small"><span>删除</span></button>
            </div>
        </div>
    </div>
	
	</div>
	<br/>
	<br/>
	<div class="content" >
		<table border="1">
      		<thead>
      			<tr>
      				<th style="overflow-x:auto;width:100px;text-align:center;">权限角色</th>
      				<th style="overflow-x:auto;width:100px;text-align:center;">备注</th>
      				<th style="overflow-x:auto;width:100px;text-align:center;">后台菜单</th>
      				<th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
      			</tr>
      		</thead>
      		<tbody id="roleInfoDiv">
      		</tbody>
   		</table>  
    </div>
    <div class="commonBox popbox" id="addRoleDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
					
							<div class="title">
								<strong><i class="icon add-user"></i>新增权限角色</strong>
							</div>
						
						<div class="form-main">

							<form id="addRoleForm" class="insretForm">
								<input id="addDepart" name = departId type="hidden">
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">部门:</label><input id="roleDepartName" type="text"
											style= "border:none" class="form-text">
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">角色名称:</label><input id="roleName" type="text"
											class="form-text" name="name" value="" >
									</div>
								</div>
							
								<div class="col">																		
									<div class="form">
										<label class="w"  style="width:130px;">备注:</label><input id="remark" type="textarea"
											class="form-text" name="remark" >
									</div>
								</div>
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button id="zcqx" class="small grey-but"
										onclick="closeDiv();" style="width:120px;">
										<span>取消</span>
									</button>
									<button id="zcqr" class="red-but" onclick="add();"  style="width:120px;">
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
	<!-- 新增角色弹框 -->
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
	
	<!-- 删除角色确认弹框 -->
	<div class="commonBox popbox" id="delRoleDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
					
							<div class="title">
								<strong><i class="icon add-user"></i>删除</strong>
							</div>
						
						<div class="form-main">

							<form id="delRoleForm" class="delForm">
								<div class="col">
									<div class="form">
										<label class="w"  style="width:180px;">是否确认删除当前权限角色？</label>
									</div>
								</div>
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button id="zcqx" class="small grey-but"
										onclick="closeDiv();" style="width:120px;">
										<span>取消</span>
									</button>
									<button id="zcqr" class="red-but" onclick="delRole();"  style="width:120px;">
										<span>删除</span>
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
	<!-- 确认删除 -->
</body>
</html>