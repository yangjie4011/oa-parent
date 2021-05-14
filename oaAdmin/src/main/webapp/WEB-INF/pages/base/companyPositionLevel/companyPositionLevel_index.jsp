<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>职级管理</title>
<%@include file="../../common/common.jsp"%>
<script type="text/javascript" src="<c:url value="/js/base/companyPositionLevel/companyPositionLevel_index.js?v=20170516"/>"></script>

</head>
<body>
	<div class="datapage">
		<input type="hidden" id="detailId" value="">
		<div class="datatitle datatitle-fixed">
			<ul class="fl jui-tabswitch">
				<li class="active" rel="#listTable" id="listTitle">列表</li>
			</ul>
		</div>
		<div style="padding-top:32px;">
			<div id="listTable">
				<div style="height: 40px;" class="bd">
					<div class="datatable column4 dt-w60 fl mt5">
						<form id="queryform">
							<dl class="col1">
								<dt>职位等级名称:</dt>
								<dd>
									<span class="ctrl-input">
										<input type="text" name="name">
									</span>
								</dd>
							</dl>
							<dl class="col1">
								<dt>职位等级编码:</dt>
								<dd>
									<span class="ctrl-input">
										<input type="text" name="code">
									</span>
								</dd>
							</dl>
							<dl class="col1 nodt">
								<dd class="fr">
									<span> <a id="query"
										class="btn-red  btn-common">&nbsp;查&nbsp;&nbsp;询&nbsp;</a>
									</span> 
									<span> <a id="reset"
										class="btn-red  btn-common">&nbsp;重&nbsp;&nbsp;置&nbsp;</a>
									</span> 
									<span> <a id="insert"
										class="btn-red  btn-common">&nbsp;新&nbsp;&nbsp;增&nbsp;</a>
									</span> 
								</dd>
							</dl>
						</form>
					</div>
				</div>
		
					
				<div class="mt5" style="height: 400px; clear: both;">
					<table id="dataTable"></table>
				</div>
			</div>
			
			
	<div id="addWin" class="hidden" style="height: 350px;">
	<form id="addForm">
		<div class="datatable column2 dt-w120 fl mt5">
			<dl class="col1">
				<dt><required>*</required>公司名称:</dt>
				<dd>
					<span class="ctrl-select">
						<select id="companyId" name="companyId">
							<option value="">请选择</option>
						</select>
					</span>
				</dd>
			</dl>
			<dl class="col2">
				<dt><required>*</required>配置名称:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="displayName">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt><required>*</required>配置码:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="code">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt><required>*</required>序号:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="rank" onkeyup="value=value.replace(/[^\d]/g,'')" >
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>隐藏码:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="displayCode">
					</span>
				</dd>
			</dl>
			<!-- <dl class="col1">
				<dt>有效状态:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="delFlag" readonly="readonly" disabled="disabled">
					</span>
				</dd>
			</dl> -->
			<dl class="col1">
				<dt>用户自定义1:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="userDef1">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>用户自定义2:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="userDef2">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>用户自定义3:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="userDef3">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>用户自定义4:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="userDef4">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>用户自定义5:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="userDef5">
					</span>
				</dd>
			</dl>
			<dl class="col2">
				<dt>备注:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="remark">
					</span>
				</dd>
			</dl>
			<dl class="col2">
				<dt>描述:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="description">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt class="required"><required>*</required>手机号码:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="phone" id="phone" class="txt-input easyui-validatebox" data-options="required:true,validType:['isBlank','mobile']"style="width:50%;float:left">
					    <a id="getValidateAdd" class="btn-red  btn-common" style="width:30%;float:left;margin-left:5px;">发送验证码</a>
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt class="required"><required>*</required>验证码:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="validateCode">
					</span>
				</dd>
			</dl>
			<dl class="col2 nodt mt5">
				<dd style="text-align:right;">
					<span> 
						<a id="btn-add" class="btn-red  btn-common">确定</a>
					</span>
					<span class="ml5 "> 
						<a class="btn-gray btn-cancel btn-common">取消</a>
					</span>
				</dd>
			</dl>
		</div>
		</form>
	</div>	
	<div id="updateWin" class="hidden" style="height: 350px;">
		<form id="updateForm">
		<input type="hidden" name="id" readonly="readonly">
		<div class="datatable column2 dt-w120 fl mt5">
			<dl class="col1">
				<dt>公司名称:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="companyName" readonly="readonly" disabled="disabled">
					</span>
				</dd>
			</dl>
			<dl class="col2">
				<dt>配置名称:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="displayName">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>配置码:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="code">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>序号:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="rank" onkeyup="value=value.replace(/[^\d]/g,'')" >
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>隐藏码:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="displayCode">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>有效状态:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="delFlag" readonly="readonly" disabled="disabled">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>用户自定义1:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="userDef1">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>用户自定义2:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="userDef2">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>用户自定义3:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="userDef3">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>用户自定义4:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="userDef4">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt>用户自定义5:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="userDef5">
					</span>
				</dd>
			</dl>
			<dl class="col2">
				<dt>备注:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="remark">
					</span>
				</dd>
			</dl>
			<dl class="col2">
				<dt>描述:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="description">
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt class="required"><required>*</required>手机号码:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="phone" id="phone" class="txt-input easyui-validatebox" data-options="required:true,validType:['isBlank','mobile']"style="width:50%;float:left">
					    <a id="getValidateCodeUpd" class="btn-red  btn-common" style="width:30%;float:left;margin-left:5px;">发送验证码</a>
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt class="required"><required>*</required>验证码:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="validateCode">
					</span>
				</dd>
			</dl>
			
			<dl class="col2 nodt mt5">
				<dd style="text-align:right;">
					<span> 
						<a id="btn-update" class="btn-red  btn-common">确定</a>
					</span>
					<span class="ml5 "> 
						<a class="btn-gray btn-cancel btn-common">取消</a>
					</span>
				</dd>
			</dl>
		</div>
		</form>
	</div>	
	<div id="deleteWin" class="hidden" style="height: 100px;">
		<form id="deleteForm">
		<div class="datatable column2 dt-w120 fl mt5">
			<dl class="col1">
				<dt class="required"><required>*</required>手机号码:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="phone" id="phone" class="txt-input easyui-validatebox" data-options="required:true,validType:['isBlank','mobile']"style="width:50%;float:left">
					    <a id="getValidateCodeDel" class="btn-red  btn-common" style="width:30%;float:left;margin-left:5px;">发送验证码</a>
					</span>
				</dd>
			</dl>
			<dl class="col1">
				<dt class="required"><required>*</required>验证码:</dt>
				<dd>
					<span class="ctrl-input">
						<input type="text" name="validateCode">
					</span>
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