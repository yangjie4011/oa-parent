<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="icon" href="data:;base64,=">
<title>部门</title>
<%@ include file="/WEB-INF/pages/common/common1.jsp"%>

<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src= "<%=basePath %>js/base/org/depart.js?v=<%=random %>"></script>
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
	<div class="datapage">
	<div style="padding-top:32px;">
		<div class="grid-l200m mt5">
			<!-- 左侧树形部门展示begin -->
			<div class="col-left bdc p10" style="width: 280px;">
				<!-- 隐藏部门id -->
				<input type="hidden" id="departId">
				<input type="hidden" id="parentName" value="${parentName }">
				<div>
					<ul id="tree" class="ztree"></ul>
				</div>
			</div>
			<!-- 左侧树形部门展示end -->
			
			<!-- 右侧列表展示begin -->
			<div class="col-main pl10">
				<div class="oa mt5">
					<div id="departTableDiv">
						<table id="departTable"></table>
					</div>
    			</div>
			</div>
			<!-- 右侧列表展示end -->
		</div>
	</div>
</div>

<!-- 新增弹框begin -->
<div id="addWin" class="hidden" style="height: 350px;">
	<form id="addForm">
		<!-- 隐藏部门id -->
		<input type="hidden" name="id" readonly="readonly">
		<!-- 隐藏父节点id -->
		<input type="hidden" name="parentId" id="parentId" readonly="readonly">
		<div class="datatable column2 dt-w120 fl mt5">
			<dl class="col2">
				<dt>部门名称:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="name">
					</span>
				</dd>
			</dl>
			<dl class="col2">
				<dt>组织说明:</dt>
				<dd>
					<span class="ctrl-input">
						<textarea rows="4" cols="8" name="remark" ></textarea>
					</span>
				</dd>
			</dl>
			<dl class="col2 nodt mt5">
				<dd style="text-align:right;">
					<span> 
						<a id="btn-add" class="btn-red  btn-common" onclick="addDepart()">确定</a>
					</span>
					<span class="ml5 "> 
						<a class="btn-gray btn-cancel btn-common" onclick="cancel()">取消</a>
					</span>
				</dd>
			</dl>
		</div>
	</form>
</div>	
<!-- 新增弹框end -->

<!-- 编辑弹框begin -->
<div id="updateWin" class="hidden" style="height: 350px;">
	<form id="updateForm">
		<input type="hidden" name="id" readonly="readonly">
		<div class="datatable column2 dt-w120 fl mt5">
			<dl class="col2">
				<dt>部门名称:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="name" id="name">
					</span>
				</dd>
			</dl>
			<dl class="col2">
				<dt style="text-align:right;"></dt>
				<dd>
					<input id="type" name="type" type="checkbox" onclick="changeType();" value="1"/>&nbsp;有独立管理权限
				</dd>
			</dl>
			<dl class="col2" id="leaderDL" style="display:none;">
				<dt>部门负责人:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="leader">
					</span>
				</dd>
			</dl>
			<dl class="col2" id="codeDL" style="display:none;">
				<dt><required>*</required>行使权力人:</dt>
				<dd>
					<span>
						<input type="text" name="power" style="width:300px;">
					</span>
					<span><a id="btn-choose" class="btn-common" onclick="choosePower()">选择</a></span>
				</dd>
			</dl>
			<dl class="col2">
				<dt>组织说明:</dt>
				<dd>
					<span class="ctrl-input">
						<textarea rows="4" cols="8" name="remark"></textarea>
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