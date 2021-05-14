<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>假期流水</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/leaveManager/leave_record.js?v=20190423"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
</style>
</head>
<body>
	<div class="content" style="overflow-x:auto">
		<div class="form-wrap">
			<div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"></i></div>
            	<div class="form-main">
	                
                <form id="queryform">
	            <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	            <input type="hidden" id="pageNo" value=""/>
	                <div class="col">
	                    <div class="form">
	                    	<label class="w">员工编号：</label>
	                    	<input type="text" class="form-text" name="employeeCode" value="">
	                    </div>
	                    
	                    <div class="form">
	                    	<label class="w">员工姓名：</label>
	                    	<input type="text" class="form-text" name="employeeName" value="">
	                    </div>
	                    <div class="form">
	                    	<label class="w">流水类型：</label>
	                    	<select id="billType" name="source" class="select_v1">
	                    		<option value="">全部</option>
								<option value= 0>系统</option>
								<option value= 1>HR修改</option>
	                        </select>
	                    </div>
	                    <div class="form">
	                    	<label class="w">假期类型：</label>
	                    	<select id="billType" name="leaveType" class="select_v1">
	                    		<option value="">全部</option>
								<option value= 1>年假</option>
								<option value= 2>病假</option>
								<option value= 3>婚假</option>
								<option value= 4>哺乳假</option>
								<option value= 5>调休</option>
								<option value= 6>产前假</option>
								<option value= 7>产假</option>
								<option value= 8>流产假</option>
								<option value= 9>陪产假</option>
								<option value= 10>丧假</option>
								<option value= 11>事假</option>
								<option value= 12>其它</option>
								<option value= 12>已用调休</option>
	                        </select>
	                    </div>
	                 </div>
	                 <div class="col">
		                   <div class="form">
		                    	<label class="">生成时间：</label>
		                    	<input id="startTime" type="text" class="Wdate" name="createStartDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
		                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
		                    </div>
	                    
	                    	<div class="form">
	                        <label class="margin-left">&nbsp;至&nbsp;</label>
							<input id="endTime" type="text" class="Wdate" name="createEndDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    </div>
	                </div>      
              		</form>
	                
	                <div class="col">  
	                    <div class="button-wrap ml-4">
	                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
	                    </div> 
	                </div> 
                </div>
			</div>
            <table border="1">
            	<colgroup>
				<!-- 标题列，可以在这里设置宽度 -->
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
			</colgroup>
			<thead>
				<tr>
					<th>员工编号</th>
					<th>员工姓名</th>
					<th>部门</th>
					<th>入职日期</th>
					<th>离职日期</th>
					<th>在职状态</th>
				</tr>
			</thead>
	              <tbody id="employeeList">
	              </tbody>
            </table>
            <HR>
            <br />
			<table border="1">
				<colgroup>
				<!-- 标题列，可以在这里设置宽度 -->
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
			</colgroup>
			<thead>
				<tr>
					<th>流水编号</th>
					<th>员工编号</th>
					<th>员工姓名</th>
					<th>假期类型</th>
					<th>子类型</th>
					<th>年份</th>
					<th>修改类型</th>
					<th>数量</th>
					<th>单位</th>
					<th>流水类型</th>
					<th>操作人</th>
					<th>备注</th>
					<th>生成时间</th>
				</tr>
			</thead>
	              <tbody id="reportList">
	              </tbody>
            </table>
        </div>     
        <div class="paging" id="commonPage"></div>
</body>
</html>