<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>月度假期明细表</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript"	src="../js/base/attnManager/monthLeaveDetail.js?v=20190424"/></script>
<style type="text/css">
th, td {
	text-align: center !important;
	word-break: keep-all;
	white-space: nowrap;
}
</style>
</head>
<body>
	<!--这个是好的 -->
	<div class="content" style="overflow-x: auto">
		<div class="form-wrap">
			<div class="title">
				<strong><i class="icon search1"></i>查询条件</strong><i
					class="icon arrow-down1"></i>
			</div>
			<div class="form-main">
				<form id="queryform">
					<input type="hidden" id="currentCompanyId"
						value="${requestScope.companyId }" /> <input type="hidden"
						id="pageNo" value="" />
					<div class="col">
						<div class="form">
							<label class="w">部门：</label> <select id="firstDepart"
								name="firstDepart" class="select_v1">
							</select>
						</div>
						<div class="form">
							<label class="w">年月：</label> <input id="yearAndMonth" type="text"
								class="Wdate" name="yearAndMonth" onClick="WdatePicker()"
								readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM'});" value="${startTime }"/>
						</div>
					</div>
					<div class="col">
						<div class="form">
	                    	<label class="w">员工编号</label>
	                    	<input  type="text"  name="code" />							                 
 						</div>
						<div class="form">
	                    	<label class="w">员工姓名</label>
	                    	<input  type="text"  name="cnName" />							                 
 					</div>
					</div>
				</form>
				<div class="col">
					<div class="button-wrap ml-4">
						<button id="query" class="red-but">
							<span><i class="icon search"></i>查询</span>
						</button>
						<button id="export" class="blue-but">
							<span><i class="icon add"></i>导出</span>
						</button>
					</div>
				</div>
			</div>
		</div>
		<table border="1">
			<thead>
				<tr>
					<th rowspan="2">员工编号</th>
					<th rowspan="2">员工姓名</th>
					<th colspan="31">年假</th>
					<th colspan="31">事假</th>
					<th colspan="31">病假</th>
					<th colspan="31">调休</th>
					<th colspan="31">产假</th>
					<th colspan="31">陪产假</th>
					<th colspan="31">丧假</th>
					<th colspan="31">婚假</th>
					<th colspan="31">产前假</th>
					<th colspan="31">流产假</th>
				</tr>
				<tr>
					<c:forEach var="i" begin="1" end="10">
						<c:forEach begin="1" end="31" varStatus="vs">
							<th class="monDay">${vs.index }</th>
						</c:forEach>
					</c:forEach>
				</tr>
			</thead>
			<tbody id="reportList">
			</tbody>
		</table>
	</div>
	<div class="paging" id="commonPage"></div>
</body>
</html>