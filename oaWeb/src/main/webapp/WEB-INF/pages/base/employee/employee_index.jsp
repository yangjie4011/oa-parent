<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>在职员工管理</title>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<c:url value="/js/common/oaCommon.js?v=20170515"/>"></script>
<script type="text/javascript" src="<c:url value="/js/base/employee/employee_index.js?v=20170517"/>"></script>
</head>
<body>
	<div class="col-main pl10" style="padding-top:20px;">
		<input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
		<form id="queryForm">
			<table>
				<tr>
					<td>
						员工类型&nbsp;&nbsp;
					</td>
					<td>
						<select id="empTypeId" name="empTypeId"><option value="">请选择</option></select>
					</td>
					<td>
						合作公司&nbsp;&nbsp;
					</td>
					<td>
						<select id="coopCompanyId" name="coopCompany.id"><option value="">请选择</option></select>
					</td>
				</tr>
				<tr>
					<td>
						部门&nbsp;&nbsp;
					</td>
					<td>
						<input type="text" id="departName" onclick="getDepartTree();" readonly="readonly"/>
						<input type="hidden" name="departId" id="departId"/>
						<a href="#" onclick="clearDepart();">清空</a>
					</td>
					<td>
						职位&nbsp;&nbsp;
					</td>
					<td>
						<select id="positionId" name="positionId"></select>
					</td>
				</tr>
				<tr>
					<td>
						员工姓名&nbsp;&nbsp;
					</td>
					<td>
						<input type="text" name="cnName"/>
					</td>
					<td>
						员工编号&nbsp;&nbsp;
					</td>
					<td>
						<input type="text" name="code"/>
					</td>
				</tr>
				<tr>
					<td>
						婚姻状况&nbsp;&nbsp;
					</td>
					<td>
						<select id="maritalStatus" name="maritalStatus">
						</select>
					</td>
					<td>
						合同到期时间&nbsp;&nbsp;
					</td>
					<td>
						<input type="text" name="contractEndTime1"/>&nbsp;至&nbsp;<input type="text" name="contractEndTime2"/>
					</td>
				</tr>
				<tr>
					<td>
						性别&nbsp;&nbsp;
					</td>
					<td>
						<select id="sex" name="sex">
							<option value="">请选择</option>
							<option value="0">男</option>
							<option value="1">女</option>
						</select>
					</td>
					<td>
						状态&nbsp;&nbsp;
					</td>
					<td>
						<select id="jobStatus" name="jobStatus">
							<option value="">全部</option>
							<option value="0">在职</option>
							<option value="1">离职</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						试用期到期时间&nbsp;&nbsp;
					</td>
					<td>
						<input type="text"/>&nbsp;至&nbsp;<input type="text"/>
					</td>
				</tr>
				<tr>
					<td>
						<input type="button" value="查询" id="query"/>
					</td>
				</tr>
			</table>
		</form>
	
	
		<div class="oa mt5">
			<table id="employeeTable"></table>
		</div>
	</div>
	
	<!-- 弹出部门树 -->
	<div class="datapage" style="display:none;" id="showDepartTree">
		<div style="padding-top:10px;">
			<div class="grid-l200m mt5" style="height:500px;">
				<div class="col-left bdc p10" style="width: 250px;">
					<div>
						<ul id="departTree" class="ztree"></ul>
					</div>
				</div>
   			</div>
		</div>
	</div>
	
	
	<form id="checkForm" style="display:none" action="<%=basePath%>employee/toEmployeeCheck.htm" method="post">
		<input type="hidden" id="checkEmployeeId" name="id"/>
	</form>
	
	<form id="editForm" style="display:none" action="<%=basePath%>employee/toEmployeeEdit.htm" method="post">
		<input type="hidden" id="eidtEmployeeId" name="id"/>
	</form>
	
	
	<!-- 离职页面 -->
	<div id="deleteWin" class="hidden" style="height: 100px;">
		<form id="deleteForm">
		<div class="datatable column2 dt-w120 fl mt5">
			<dl class="col1">
				<dd>
					<input type="text" id="quitTime"/>
				</dd>
			</dl>
			<dl class="col1">
				<dd>
					<input type="text" id="salaryBalanceDate"/>
				</dd>
			</dl>
			<input id = "deleteID" name="id" type="hidden"/><!-- 要删除的id隐藏在这 -->
			<dl class="col2 nodt mt5">
				<dd style="text-align:right;">
					<span> 
						<a id="btn-delete" class="btn-red  btn-common">确定</a>
					</span>
					<span class="ml5 "> 
						<a class="btn-gray btn-cancel btn-common">取消</a>
					</span>
				</dd>
			</dl>
		</div>
		</form>
	</div>
</body>
</html>