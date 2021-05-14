<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>入职查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/position/entryQuery.js?v=20190508"/></script>
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
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"  onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId}"/>
                <input type="hidden" id="pageNo" value=""/>
               <form id="queryform">
               
                <div class="form-main">
                
                <div class="col">                
	                <div class="form">
	                	<label class="w" style="width:100px;">入职日期:</label>
	                    <input id="employmentDate" type="text" class="Wdate" name="employmentDateBegin" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'employmentDateEnd\')}'});" />
	                </div>	 
	                <div class="form">
	                    <label class="w" style="width:100px;">至</label>
						<input id="employmentDateEnd" type="text" class="Wdate" name="employmentDateEnd" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'employmentDate\')}'});" />&nbsp;&nbsp;<a id="lastWeekFirst" style="color:blue;">上周</a>&nbsp;&nbsp;<a id="thisMonthFirst" style="color:blue;">本月</a>&nbsp;&nbsp;<a id="lastMonthFirst" style="color:blue;">上月</a>
	                </div>                                                   
                </div>      
                
               <div class="col">                
	                <div class="form">
	                	<label class="w" style="width:100px;">申请日期:</label>
	                    <input id="createTime" type="text" class="Wdate" name="createTimeBegin" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'},maxDate:'#F{$dp.$D(\'employmentDateEnd\')}'});" />
	                </div>	  
	                <div class="form">
	                	<label class="w" style="width:100px;">至</label>
						<input id="createTimeEnd" type="text" class="Wdate" name="createTimeEnd" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'createTime\')}'});" />&nbsp;&nbsp;<a id="lastWeek" style="color:blue;">上周</a>&nbsp;&nbsp;<a id="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a id="lastMonth" style="color:blue;">上月</a>
	                </div>                                                  
               </div>
                                         
                
                <div class="col">
	                <div class="form">
	                    	<label class="w" style="width:100px;">状态：</label>
	                    	<select id="entryStatus" name="entryStatus" class="select_v1">
	                    		<option value="">请选择</option>
	                    		<option value="1">未入职</option>
	                    		<option value="2">待入职</option>
	                    		<option value="3">已入职</option>
	                        </select>
	                </div>
	                
	                <div class="form">
	                    	<label class="w" style="width:100px;">延期</label>
	                    	<select id="yanqi" name="yanqi" class="select_v1">
	                        	<option value="">请选择</option>
	                    		<option value="1">是</option>
	                    		<option value="2">否</option>
	                        </select>
	            	</div>
                </div> 
                
                <div class="col">
	            	<div class="form">
                        <label class="w" style="width:100px;">部门：</label>
                        <select id="firstDepart" name="firstDepart" class="select_v1">
                        </select>
                    </div>
                    <div class="form">
                    	<label class="w" style="width:100px;">员工姓名</label>
                    	<input type="text" class="form-text" id="cnName" name="cnName" value="">
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
							<th style="overflow-x:auto;width:100px;text-align:center;">公司名称</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">员工类型</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">姓名</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">入职日期</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">汇报对象</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门负责人</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">职位</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">工作邮箱</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">申请日期</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">入职状态</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">是否延期</th>
			               <th style="overflow-x:auto;width:150px;text-align:center;">操作</th>
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