<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>假期余额</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/leaveManager/leaveReamin_report.js?v=20190423"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
</style>
</head>
<body><!--这个是好的 -->
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
	                    	<label class="w">状态：</label>
	                    	<select id="jobStatus" name="jobStatus" class="select_v1">
	                    		<option value="">全部</option>
								<option value="0">在职</option>
								<option value="1">离职</option>
	                        </select>
	                    </div>
	                </div>
	                
	                <div class="col">
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
					<th rowspan="2">员工编号</th>
					<th rowspan="2">员工姓名</th>
					<th rowspan="2">部门</th>
					<th rowspan="2">离职日期</th>
					<th rowspan="2">在职状态</th>
					<th colspan="4">当年假期总数</th>
					<th colspan="7">剩余假期天数</th>
					<th colspan="3">截止目前剩余假期天数</th>
					<th colspan="15">当年已使用假期天数</th>
					<th colspan="2">透支假期天数</th>
				</tr>
				<tr>
					<th>年假</th>
					<th>法定年假</th>
					<th>福利年假</th>
					<th>带薪病假</th>
					<th>去年法定年假</th>
					<th>去年福利年假</th>
					<th>当年法定年假</th>
					<th>当年福利年假</th>
					<th>年假</th>
					<th>带薪病假</th>
					<th>调休小时数</th>
					<th>当年法定年假</th>
					<th>当年福利年假</th>
					<th>带薪病假</th>
					<th>法定年假</th>
					<th>福利年假</th>
					<th>年假</th>
					<th>带薪病假</th>
					<th>非带薪病假</th>
					<th>事假</th>
					<th>调休小时数</th>
					<th>婚假</th>
					<th>丧假</th>
					<th>陪产假</th>
					<th>产前假</th>
					<th>产假</th>
					<th>哺乳假</th>
					<th>流产假</th>
					<th>其他</th>
					<th>年假</th>
					<th>带薪病假</th>
				</tr>
			</thead>
	              <tbody id="reportList">
	              </tbody>
            </table>
        </div>     
        <div class="paging" id="commonPage"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>