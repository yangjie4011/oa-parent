<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<link rel="icon" href="data:;base64,=">
<title>考勤明细查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/attnManager/empAttnDetailReport.js?v=20190423"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
	
</style>
</head>
<body>
	<div class="content" style="width: 1710px;">
		<div class="form-wrap">
			<div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"></i></div>
             	<div class="form-main">
	                
                <form id="queryform">
	            <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	            <input type="hidden" id="pageNo" name="page" value=""/>
	                <div class="col">
	                    <div class="form">
	                    	<label class="w">员工编号：</label>
	                    	<input type="text" class="form-text" name="code" value="">
	                    </div>
	                    
	                    <div class="form">
	                    	<label class="w">员工姓名：</label>
	                    	<input type="text" class="form-text" name="cnName" value="">
	                    </div>
	                </div>      
	                
	                <div class="col">
	                	<div class="form">
	                        <label class="w">工时制：</label>
	                        <select id="workType" name="workType" class="select_v1">
	                        </select>
	                    </div>
	                    
	                    <div class="form">
	                        <label class="w">员工类型：</label>
	                        <select id="empTypeId" name="empTypeId" class="select_v1">
	                        </select>
	                    </div>
	                </div>
	                
	                <div class="col">
	                	<div class="form">
	                        <label class="w">是否排班：</label>
	                        <select id="whetherScheduling" name="whetherScheduling" class="select_v1">
	                        </select>
	                    </div>
	                
	                    <div class="form">
	                    	<label class="w">考勤日期：</label>
	                    	<input id="startTime" type="text" class="Wdate" name="startTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" value="${startTime }"/>
							<input id="endTime" type="text" class="Wdate" name="endTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" value="${endTime }"/>
							       &nbsp;&nbsp;<a id="lastWeek" style="color:blue;">上周</a>
			                       &nbsp;&nbsp;<a id="thisMonth" style="color:blue;">本月</a>
			                       &nbsp;&nbsp;<a id="lastMonth" style="color:blue;">上月</a>
	                    </div>
	                </div> 
	                
	                <div class="col">
	                	<div class="form">
	                    	<label class="w">考勤状态：</label>
	                    	<select id="attnStatus" name="attnStatus" class="select_v1">
	                    	       <option value="" selected="selected">请选择</option>
	                    	       <option value="0">正常</option>
	                    	       <option value="1">异常</option>
	                        </select>
	                    </div>
	                    <div class="form">
	                        <label class="w">部门：</label>
	                        <select id="firstDepart" name="firstDepart" class="select_v1">
	                        </select>
	                    </div>
	                    
	                    <div class="form" id="secondDepartDiv" style="display:none">
	                        <select id="secondDepart" name="secondDepart" class="select_v1">
	                        </select>
	                    </div>
	                </div>
              		</form>
	                
	                <div class="col">  
	                    <div class="button-wrap ml-4">
	                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
	                        <button id="export" class="blue-but"><span><i class="icon add"></i>导出</span></button>          
	                    </div> 
	                </div> 
                </div>
			</div>

		<table>
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
				<col>
				<col>
				<col>
			</colgroup>
			<thead>
				<tr>
					<th rowspan="2">员工编号</th>
					<th rowspan="2">姓名</th>
					<th rowspan="2">部门</th>
					<th rowspan="2">职位</th>
					<th rowspan="2">工时制</th>
					<th rowspan="2">日期</th>
					<th rowspan="2">星期</th>
					<th rowspan="2">班次</th>
					<th colspan="2">上班打卡</th>
					<th colspan="2">下班打卡</th>
					<th colspan="2">上班考勤</th>
					<th colspan="2">下班考勤</th>
					<th rowspan="2">应出勤（时）</th>
					<th rowspan="2">已出勤（时）</th>
					<th rowspan="2">缺勤（时）</th>
					<th rowspan="2">迟到（分）</th>
					<th rowspan="2">早退（分）</th>
					<th rowspan="2">旷工（天）</th>
					<th rowspan="2">关联单据</th>
				</tr>
				<tr>
					<th>打卡时间</th>
					<th>打卡结果</th>
					<th>打卡时间</th>
					<th>打卡结果</th>
					<th>考勤时间</th>
					<th>考勤结果</th>
					<th>考勤时间</th>
					<th>考勤结果</th>
				</tr>
			</thead>
			<tbody id="reportList">
				<tr style="mso-yfti-irow: 2; mso-yfti-lastrow: yes; height: 12.75pt">
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
		</table>
        </div>
      	<div class="paging" id="commonPage">
      	</div>  
</body>
</html>