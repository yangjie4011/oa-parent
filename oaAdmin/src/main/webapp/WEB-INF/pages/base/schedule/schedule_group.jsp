<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>排班组别管理</title>
<%@include file="../../common/common.jsp"%>
<!-- 查询人员 -->
<link rel="stylesheet" href="http://i0.ulecdn.com/ule/oa/c/personsel.css?v=2019030701">
<script src="../js/util/personsel.js?v=20191105"></script>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<c:url value="/js/base/schedule/schedule_group.js?v=20200218"/>"></script>
</head>
<body>
	<!-- 新增或编辑标识（1：新增，2：修改） -->
	<input type="hidden" id="addOrEdit"/>
	<input type="hidden" id="currentCompanyId" value="${requestScope.companyId}"/>
	<input type="hidden" id="departId"/>
	<input type="hidden" id="scheduleGroupId"/>
	<div>
		<div>
			<div class="grid-l200m mt5" id="mainDiv">
				<div class="col-left bdc p10" style="width: 20%;" id="treeDiv">
					<div>
						<ul id="scheduleGroupTree" class="ztree"></ul>
					</div>
				</div>
				<div class="col-main pl10">
					<div class="oa mt5">
						<table id="scheduleGroupTable"></table>
					</div>
    			</div>
   			</div>
		</div>
	</div>
	<div style="display:none" id="dialogParentDiv">
	 <!-- 新增或编辑排班组别 -->
	<div id="saveWin" class="easyui-window" closed="true">
		<form id="saveForm">
			<input type="hidden" name="id" id="groupId"/>
			<input type="hidden" name="departId" id="addParentId"/>
			<table  style="margin:auto;vertical-align: middle;border-collapse:separate;border-spacing:0px 10px;font-size:15px;" >
				<tr>
					<td>分组名称：</td>
					<td><input type="text" style="border:solid 1px #000000" id="name" name="name"></td>
				</tr>
				<tr>
					<td>排班人：</td>
					<td>
						<input type="hidden" name="scheduler" id="scheduler">
						<input type="text" style="border:solid 1px #000000" class="form-text" value="" id="chooseScheduler" >
					</td>
				</tr>
				<tr>
					<td>排班审核人：</td>
					<td>
						<input type="hidden" name="auditor" id="auditor">
						<input type="text" style="border:solid 1px #000000" class="form-text" value="" id="chooseAuditor" >
					</td>
				</tr>
				<tr>
					<th colspan="6"></th>
				</tr>
				<tr>
					<td>
						<a id="saveSchduleGroup" href="#" class="easyui-linkbutton" icon="icon-ok">确定</a> 
					</td>
					<td>
						&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;
						<a href="#" class="easyui-linkbutton" icon="icon-cancel" onclick="closeWindow();">取消</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	
	<!-- 组员管理 -->
	<div class="easyui-window" id="groupManagementWin" closed="true" style="width:700px;height:400px">
		<div class="easyui-layout" fit="true" id="groupEmp">
			<div region="west" split="true" style="width:200px;">
				<br/>&nbsp;
				<input id="code" class="easyui-textbox" data-options="prompt:'员工编号'" style="width:80px;height:32px">
				&nbsp;
				<input id="cnName" class="easyui-textbox" data-options="prompt:'姓名'" style="width:80px;height:32px">
				<br/>
				<br/>&nbsp;
				<input id="firstEntryTime" class="easyui-datebox" data-options="prompt:'入职日期'" style="width:80px;height:32px">
				&emsp;
				<a href="#" id="searchUnGroupEmp" iconCls="icon-search" class="easyui-linkbutton" onClick="getUngroupedEmp()">查询</a>
				<br/>&emsp;
				<table id="unGroupEmp" class="easyui-datagrid" style="width:190px;height:190px"
					idField="id"
					iconCls="icon-save">
					<thead>
						<tr>
						</tr>
					</thead>
				</table>
				<br/>
				&nbsp;
				<a href="#" class="easyui-linkbutton" iconCls="icon-reload" onclick="cleanCheck();">清空已选</a>
				&emsp;
				<a href="#" class="easyui-linkbutton" iconCls="icon-add" onclick="addMember();">添加组员</a>
			</div>
			<div region="center" border="false" border="false">
					<div style="padding:10px;">
						<input id="searchExistEmpList" class="easyui-searchbox" data-options="prompt:'请输入员工编号或员工姓名'" style="width:450px"></input>
						<br/>
						<br/>
						<div> 
						<table id="existEmpList" class="easyui-datagrid" style="width:400px;height:300px">
						    <thead>
								<tr>
								</tr>
						    </thead>
						</table>
						<div> 
					</div>
			</div>
		</div>
	</div>
	</div>
</body>
</html>