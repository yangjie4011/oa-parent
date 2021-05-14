<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>考勤设置</title>
<%@include file="../../common/common2.jsp"%>
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<%=basePath%>js/base/attnManager/attnSetting.js?v=20180606"/></script>
</head>
<body>
	<input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	<div class="datatitle">
		<ul class="fl jui-tabswitch">
			<li id="listTab" rel="#old" class="active"><strong>修改考勤时间</strong></li>
			<li id="detailTab" rel="#new"><strong>修改查询</strong></li>
		</ul>
	</div>
	<div class="content">
		<div class="form-wrap"   style="height: 100%; float: left;border-right:1px solid #d9d9d9" id="mainDiv0">
			<div class="form-main" >

				<form id="queryform">

					<div class="col">
						<div class="form">
							<label class="w">选择员工：</label> 
							<select id="employees" name="employees" class="select_v1">
							</select>
						</div>
					</div>

					<div class="col">
						<div class="form">
							<label class="w">考勤日期：</label> 
							<input id="workDate" type="text" class="Wdate" name="workDate" onClick="WdatePicker()" 
							readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/> 
						</div>
					</div>

					<div class="col">
						<div class="form">
							<label class="w">上班考勤：</label> 
							<input id="startTime" type="text" class="Wdate" name="startTime" onClick="WdatePicker()" 
							readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/> 
						</div>
					</div>

					<div class="col">
					    <div class="form">
							<label class="w">下班考勤：</label> 
							<input id="endTime" type="text" class="Wdate" name="endTime" onClick="WdatePicker()" 
							readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/> 
						</div>
					</div>

				</form>

				<div class="col">
					<div class="button-wrap ml-4">
						<button id="saveAttn" class="red-but">
							<span><i class="icon search"></i>确定</span>
						</button>
					</div>
				</div>

				<div class="col">
					<div class="button-wrap ml-4">
						<button id="cancelAttn" class="blue-but">
							<span><i class="icon cancel"></i>取消</span>
						</button>
					</div>
				</div>
			</div>
		</div>
		<div  style="width: 28%; height: 100%; float: left;border-right:1px solid #d9d9d9;border-left:1px solid #d9d9d9" id="mainDiv1">
			<ul id="departTree" class="ztree"></ul>
		</div>
		<div id="dataDiv"  style="width: 28%; float: left;padding-left:10px;display:block;">
			<div class="title">
				<strong><i class="icon search2"></i>选择人员</strong>
			</div>
            <div class="form-main">   
                <!-- <div class="col">  
                    <div class="button-wrap ml-0">
                        <button id="add" class="blue-but"><span><i class="icon add"></i>新建</span></button>          
                    </div> 
                </div> --> 
                </div>
			<table id="positionTable1">
				<colgroup>
					<col width="20%">
					<col width="30%">
					<col width="50%">
				</colgroup>
				
				<thead>
					<tr>
						<th style="text-align: center;"></th>
						<th style="text-align: center;">员工编号</th>
						<th style="text-align: center;">员工姓名</th>
					</tr>
				</thead>
				<tbody id="positionList">
				</tbody>
			</table>
			<div class="paging" id="commonPage"></div>
		</div>
	</div>	
	
	<div id="confirmWin" class="hidden" style="height: 100px;">
		<div class="datatable column2 dt-w120 fl mt5">
			<input id = "deleteID" name="id" type="hidden"/><!-- 要删除的id隐藏在这 -->
			<dl class="col2 nodt mt5">
				<dd style="text-align:right;">
					<span> 
						<a id="btn-confirm" class="btn-red  btn-common">确定</a>
					</span>
					<span class="ml5 "> 
						<a class="btn-gray btn-cancel btn-common">取消</a>
					</span>
				</dd>
			</dl>
		</div>
	</div>

</body>
</html>