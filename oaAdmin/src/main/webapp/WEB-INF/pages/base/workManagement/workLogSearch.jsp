<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>工作日志查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/workManagement/workLogSearch.js?v=20200310"/></script>
<style type="text/css">
	th,td {
		text-align:center !important;
		word-break: keep-all;
		white-space:nowrap;
	}	
	table td {
		padding: 6px 6px;
	}
</style>
</head>
<body>
	<input type="hidden" value="0" id="flagTab">
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li id="leaderTab" class="active"><strong>工作日志查询</strong></li>	
		</ul>
	</div>
	<div class="content first" style="overflow-x:auto">
            
            <div class="form-wrap searchDiv">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
                <input type="hidden" id="pageNo" value=""/>
               <form id="queryform">
               <div class="form-main">
               
               <div class="col">
                  
                   <div class="form">
                        <label class="w">月份：</label>
                        <input value="${month}" id="month" type="text" class="Wdate" name="month" onClick="WdatePicker({dateFmt:'yyyy-MM',maxDate:'%y-{%M}'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM',maxDate:'%y-{%M}'});"/>				
                   </div>   
                  
                   <div class="form">
	                        <label class="">部门：</label>
	                        <select id="firstDepart" name="departId" class="select_v1">
	                        </select>
	                </div>
               
                    <div class="form">
	                        <label class="w">汇报对象：</label>
	                        <input type="text" class="form-text" id="leaderName" name="leaderName" value="">
	                </div>
                    
                </div>
               
               
                <div class="col">
                    <div class="form">
                    	<label class="w">员工编号：</label>
                    	<input type="text" class="form-text" id="code" name="empCode" value="">
                    </div>
                    
                    <div class="form">
                    	<label class="w">员工姓名：</label>
                    	<input type="text" class="form-text" id="cnName" name="empCnName" value="">
                    </div>
                </div>  

                </div>
             </form> 
           
            <div class="col">  
                    <div class="button-wrap ml-4">
                        <button onclick="gotoPage();"  class="red-but"><span><i class="icon search"></i>查询</span></button>
                        <button id="export" class="blue-but"><span><i class="icon download"></i>导出</span></button> 
                     </div> 
            </div> 
			
			</div>
			
			<div class="form-wrap processInfo">    
	              
			</div>
			
            <div id="table">
            	<div style="float:left;width:25%;">
			      <table border="1" id="reportListTitle">
					  <thead>
					  </thead>
			             <tbody>
			             </tbody>
		          </table>
		      	</div>
            <div style="float:left;width:75%;overflow-x:auto">
            	<table border="1" id="reportList">
			  	<thead>
			  	</thead>
	             <tbody>
	             </tbody>
	          	</table>
            </div>
            </div>           
        </div>
      	<div class="paging" id="commonPage"></div>
      	
</body>
</html>
