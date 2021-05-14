<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>职位管理</title>
<%@include file="../../common/common.jsp"%>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<c:url value="/js/base/org/depart.js?v=20191106"/>"></script>

</head>
<body>
	<!-- 新增或编辑标识（1：新增，2：修改） -->
	<input type="hidden" id="addOrEdit"/>
	<input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	<input type="hidden" id="departId"/>

	<div>
		<div>
			<div class="grid-l200m mt5" id="mainDiv">
				<div class="col-left bdc p10" style="width: 20%;" id="mainDiv1">
					<div>
						<ul id="departTree" class="ztree"></ul>
					</div>
				</div>
				<div class="col-main pl10">
					<div class="oa mt5">
						<table id="departTable"></table>
					</div>
    			</div>
   			</div>
		</div>
	</div>
	
	<!-- 新增部门 -->
	<div id="addWin" class="easyui-window">
		<form id="addForm">
			<input type="hidden" name="parentId" id="addParentId"/>
			<table style="margin:auto;vertical-align: middle;border-collapse:separate;border-spacing:0px 10px;font-size:15px;">
				<tr>
					<td>部门类型</td>
					<td>
						<select id="addDepartType" name="type" onchange="setLeaderAndPower(1)">
						</select>
					</td>
				</tr>
				<tr>
					<td>部门名称</td>
					<td><input type="text" id="addName" name="name"></td>
				</tr>
				<tr>
					<td>部门负责人</td>
					<td>
						<input type="text" id="addLeaderName" readonly="readonly" onclick="getLeaderTree($('#addDepartType').val(),1);">
						<input type="hidden" id="addLeaderId" name="leader">
					</td>
				</tr>
				<tr>
					<th colspan="6"></th>
				</tr>
				<tr>
					<td>
						<a href="#" class="easyui-linkbutton" icon="icon-ok" onclick="saveDepart();">确定</a> 
					</td>
					<td>
						&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;
						<a href="#" class="easyui-linkbutton" icon="icon-cancel" onclick="closeWindown('addWin');">取消</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
	
	<!-- 更新部门 -->
	<div id="updateWin" class="easyui-window">
		<form id="updateForm">
			<input type="hidden" id="id" name="id"/>
			<input type="hidden" id="beforeLeaderId" name="beforeLeaderId"/>
			<input type="hidden" name="parentId" id="editParentId"/>
			
			<table style="margin:auto;vertical-align: middle;border-collapse:separate;border-spacing:0px 10px;font-size:15px;">
				<tr>
					<td>部门类型</td>
					<td>
						<select id="editDepartType" name="type">
							<option value="1">一级部门</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>部门名称</td>
					<td><input type="text" id="editName" name="name"></td>
				</tr>
				<tr>
					<td>部门负责人</td>
					<td>
						<input type="text" id="editLeaderName" readonly="readonly" onclick="getLeaderTree($('#editDepartType').val(),2);">
						<input type="hidden" id="editLeaderId" name="leader">
					</td>
				</tr>
				<tr>
					<th colspan="6"></th>
				</tr>
				<tr>
					<td>
						<a href="#" class="easyui-linkbutton" icon="icon-ok" onclick="updateDepart();">确定</a> 
					</td>
					<td>
						&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;
						<a href="#" class="easyui-linkbutton" icon="icon-cancel" onclick="closeWindown('updateWin');">取消</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>