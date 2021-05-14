<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>月度假期</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/attnManager/monthLeaveCount.js?v=20190424"/></script>
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
	                        <label class="w">部门：</label>
	                        <select id="firstDepart" name="firstDepart" class="select_v1">
	                        </select>
	                    </div>
	                    <div class="form">
	                    	<label class="w">年月：</label>
	                    	<input id="yearAndMonth" type="text" class="Wdate" name="yearAndMonth" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM'});" />
	                	</div>	                   
	                </div>
	                <div class="col">
						<div class="form">
	                    	<label class="w" >员工编号</label>
	                    	<input  type="text"  name="code" />							                 
 						</div>
						<div class="form">
	                    	<label class="w" >员工姓名</label>
	                    	<input  type="text"  name="cnName" />							                 
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
            
			<table border="1">			
			<thead>	
				<tr>
					<th>员工编号</th>
					<th>员工姓名</th>
					<th>部门</th>
					<th>当月年假次数</th>
					<th>当月年假天数</th>
					<th>当月病假次数</th>
					<th>当月病假天数</th>
					<th>当月事假次数</th>
					<th>当月事假天数</th>
					<th>当月调休次数</th>
					<th>当月调休小时数</th>
					<th>当月产假次数</th>
					<th>当月产假天数</th>
					<th>当月陪产假次数</th>
					<th>当月陪产假天数</th>
					<th>当月丧假次数</th>
					<th>当月丧假天数</th>
					<th>当月婚假次数</th>
					<th>当月婚假天数</th>					
					<th>当月产前假次数</th>
					<th>当月产前假天数</th>
					<th>当月流产假次数</th>
					<th>当月流产假天数</th>				
				</tr>				
			</thead>
	              <tbody id="reportList">
	              </tbody>
            </table>
        </div>     
        <div class="paging" id="commonPage"></div>
</body>
</html>