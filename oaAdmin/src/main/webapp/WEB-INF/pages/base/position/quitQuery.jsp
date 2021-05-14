<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>离职查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/position/quitQuery.js?v=20190424"/></script>
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
			                	<label class="w">离职日期:</label>
			                    <input id="employmentDate" type="text" class="Wdate" name="quitStartTime" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'employmentDateEnd\')}'});" />
			                </div>	  
			                <div class="form">
			                    <label>至</label>
								<input id="employmentDateEnd" type="text" class="Wdate" name="quitEndTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
			                </div>   
			                <div class="form">
		                    	<label class="w">员工姓名</label>
		                    	<input type="text" class="form-text" id="contractCode" name="employeeName" value="">
		                    </div>   
		                    <div class="form">
		                        <label class="w">部门：</label>
		                        <select id="firstDepart" name="departName" class="select_v1">
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
		                 </div> 
                    </div>
			</div>
            <div>
	            <table>
					 <thead>
						<tr>
			                <th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">员工类型</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">入职日期</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;color:red;">离职日期</th>			                
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>		
			                <th style="overflow-x:auto;width:150px;text-align:center;">职位</th>	                
			                <th style="overflow-x:auto;width:150px;text-align:center;">汇报对象</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门负责人</th>
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