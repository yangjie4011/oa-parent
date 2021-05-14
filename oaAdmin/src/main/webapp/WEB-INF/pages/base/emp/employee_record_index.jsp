<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<link rel="icon" href="data:;base64,=">
<title>员工履历管理</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/emp/employee_record_index.js?v=20191201"/></script>
<style type="text/css">
	.areaon{color:#fff; background:yellow;}
	.margin-left{margin-left:10px;}
	th,td {
	word-break: keep-all;
	white-space:nowrap;
	}
	
	label{
	width: 85px;
	}
	.select_v1{
		height:36px;width:150px;
		margin-left: 0px;
	}
	
	.active {
      color: white;
      background-color: black;
    }
    .none {
      background-color: whitesmoke;
    }

	input.select_v1{
		width:150px;
	}
	input.Wdate{
		width:148px;
	}
	input{
		width:150px;
	}
</style>
</head>
<body>
	<div class="content"><!-- style="width: 1710px;" -->
		<div class="form-wrap">
			<div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"></i></div>
            	<div class="form-main" style="min-width:1700px;">
	                
	                <form id="queryform">
			            <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
			            <input type="hidden" id="pageNo" name="page" value=""/>
			                <div class="col">
			                    <div class="form">
			                    	<label class="">员工编号：</label>
			                    	<input type="text" style="width:150px;" class="form-text" name="code" value="">
			                    </div>
			                    <div class="form">
			                    	<label class="">员工姓名：</label>
			                    	<input type="text" style="width:150px;" class="form-text" name="cnName" value="">
			                    </div>
			                	
			                </div>   
	              	</form>
		               
	                <div class="col">  
	                    <div class="button-wrap ml-4"  style="margin-left:-60px">
	                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button>
	                    </div> 
	                </div>
                </div>
		</div>

		<table>
			<thead>
				<tr>
					<th style='text-align:center;'>员工编号</th>
					<th style='text-align:center;'>姓名</th>
					<th style='text-align:center;'>员工类型</th>
					<th style='text-align:center;'>部门</th>
					<th style='text-align:center;'>职位</th>
					<th style='text-align:center;'>工时制</th>
					<th style='text-align:left;'>操作</th>
				</tr>
			</thead>
			<tbody id="reportList">
				<tr>
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
    <div class="paging" id="commonPage"></div>
           
</body>
</html>