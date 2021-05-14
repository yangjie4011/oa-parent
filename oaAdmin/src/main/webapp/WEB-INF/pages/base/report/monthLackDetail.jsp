<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>月度缺勤总数表</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/report/monthLackDetail.js?v=20190424"/></script>
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
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)" ></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
                <input type="hidden" id="pageNo" value=""/>
               <form id="queryform">
               
                <div class="form-main">
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
	                    	<label class="w">年月：</label>
	                    	<input id="inputStartTime" type="text" class="Wdate" onClick="WdatePicker()" 
	                    	readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM'});" value="${startTime }"/>
	                    	<input type="hidden" id="startTime" name="startTime"/>
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
                </div>
             </form>
              <div class="col">  
                    <div class="button-wrap ml-4">
                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
                        <button id="export" class="blue-but"><span><i class="icon add"></i>导出</span></button>          
                    </div> 
                </div> 
			</div>
            <div>
	            <table>
					 <thead>
						<tr>
							<th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">姓名</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">职位名称</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">工时制</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">班次</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">月度</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">迟到+早退（分）</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">旷工（天）</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">缺勤（时）</th>
						</tr>
					  </thead>
		              <tbody id="reportList">
		              </tbody>
	            </table>
            </div>
        </div>
       <div class="paging" id="commonPage"></div>
</body>
</html>