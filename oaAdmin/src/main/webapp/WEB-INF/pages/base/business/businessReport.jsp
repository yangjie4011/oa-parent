<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>出差统计报表</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/business/businessReport.js?v=20181231"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
	.form input {
	display:block;
	float:left;
	width:140px;
	height:34px;
	line-height:34px;
	border:1px solid #d9d9d9;
}
.select_v1{
	display:block;
	float:left;
	width:142px;
	height:34px;
	line-height:34px;
	border:1px solid #d9d9d9;
}
</style>
</head>
<body>
   <div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li id="listTab" rel="#old" class="active"><strong>人员</strong></li>
			<li id="detailTab" rel="#new"><strong>部门</strong></li>
			<li id="abateTab" rel="#new"><strong>时间</strong></li>
			<li id="abateTab" rel="#new"><strong>地点</strong></li>
		</ul>
	</div>   
	
	<div class="form-wrap">
			<div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"  onclick="getButton(this)"></i></div>
            	<div class="form-main">
	                
                <form id="queryform">
                <div class="form-main">
                <div class="col tabMenu" id="tabMenu0">
                    <div class="form">
                    	<label class="w">员工姓名：</label>
                    	<input type="text" class="form-text" id="cnName" name="cnName" value="">
                    </div> 
                </div>
                <div class="col tabMenu" id="tabMenu1">
                    <div class="form">
                        <label class="w">部门：</label>
                        <select id="firstDepart" name="departId" class="select_v1">
                        </select>
                    </div>
                </div>
                    
                <span id="tabMenu2">
                </span>
                
                <div class="col tabMenu" id="tabMenu3">
                	<div class="form">
                        <label class="w">地点：</label>
                        <select id="firstCity" class="select_v1">
                        </select>
                    </div>
                    
                    <div class="form" id="secondCityDiv" style="display:none">
                        <select id="secondCity" name="address" class="select_v1">
                        </select>
                    </div>
                </div>
                <div class="col">
                  <div class="form">
                       <label class="w">时间：</label>
                       <input type="text" id="year" class="Wdate" name="year" onClick="WdatePicker({dateFmt:'yyyy'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy'});" value="${nowYear}" />				
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
	</div>	
	<!-- 第一个tab -->		
	<div class="content daiban" style="overflow-x:auto">
        <div>
        <table>
	 		<thead>
		<tr>
			<th style="overflow-x:auto;width:100px;text-align:center;">时间</th>
               <th style="overflow-x:auto;width:100px;text-align:center;">姓名</th>
               <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
               <th style="overflow-x:auto;width:100px;text-align:center;">地点</th>
               <th style="overflow-x:auto;width:150px;text-align:center;">人数</th>
               <th style="overflow-x:auto;width:150px;text-align:center;">次数</th>
               <th style="overflow-x:auto;width:150px;text-align:center;">天数</th>
               <th style="overflow-x:auto;width:150px;text-align:center;">费用（暂无）</th>
		</tr>
	  		</thead>
            <tbody id="reportList">
            </tbody>
       </table>
        </div>
    </div>
      
        <input type="hidden" id="pageNo" value=""/>
        <div class="paging" id="commonPage"></div>
       
</body>
</html>