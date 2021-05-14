<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="icon" href="data:;base64,=">
<title>部门</title>
<%@ include file="/WEB-INF/pages/common/common1.jsp"%>

<script type="text/javascript" src= "<%=basePath %>js/base/org/edit.js?v=<%=random %>"></script>
<style type="text/css">
	.csp:hover{
		background-color: blue
	}
	dt{
		text-align:right;
	}
	required{
		color:red;
	}
</style>
</head>
<body >
<!-- 编辑弹框begin -->
<div id="updateWin" style="height: 350px;">
	<form id="updateForm">
		<input type="hidden" name="id" readonly="readonly">
		<div class="datatable column2 dt-w120 fl mt5">
			<dl class="col2">
				<dt>部门名称:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="hidden" name="id" id="id" value="${depart.id }">
						<input type="text" name="name" id="name" value="${depart.name }">
					</span>
				</dd>
			</dl>
			<dl class="col2">
				<dt style="text-align:right;"></dt>
				<dd>
					<input id="type" name="type" type="checkbox" onclick="changeType();" value="${depart.type }"/>&nbsp;有独立管理权限
				</dd>
			</dl>
			<dl class="col2" id="leaderDL" style="display:none;">
				<dt>部门负责人:</dt>
				<dd>
					<!-- <span class="ctrl-input">
						<input type="text" name="leader" id="leader" readonly="true">
					</span> -->
					<span id="leader"></span>
					<input type="hidden" name="leader" id="leaderId">
				</dd>
			</dl>
			<dl class="col2" id="powerDL" style="display:none;">
				<dt><required>*</required>行使权力人:</dt>
				<dd>
					<span>
						<input type="text" id="powerName" style="width:600px;" value="${depart.powerName }" readonly="true"/>
						<input type="hidden" name="power" id="power" value="${depart.power }" />
					</span>
					<span><a id="btn-choose" class="btn-common" onclick="choosePower(this)">选择</a></span>
				</dd>
			</dl>
			<dl class="col2">
				<dt>组织说明:</dt>
				<dd>
					<span class="ctrl-input">
						<textarea rows="4" cols="8" name="remark">${depart.remark }</textarea>
					</span>
				</dd>
			</dl>
			<dl class="col2 nodt mt5">
				<dd style="text-align:right;">
					<span> 
						<a id="btn-update" class="btn-red  btn-common" onclick="updateDepart()">确定</a>
					</span>
					<span class="ml5 "> 
						<a class="btn-gray btn-cancel btn-common" onclick="cancel()">取消</a>
					</span>
				</dd>
			</dl>
		</div>
	</form>
</div>	
<!-- 编辑弹框end -->
</body>
</html>