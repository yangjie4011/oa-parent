<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>消下属缺勤</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/delayWork/removeSubordinateAbsence.js?v=20200528"/></script>
<style type="text/css">
	th,td {
		text-align:center !important;
		word-break: keep-all;
		white-space:nowrap;
	}	
	table td {
		padding: 6px 6px;
	}
	#updateDiv .col {
	    margin-bottom: 3px;
	}
</style>
</head>
<body>
	<input type="hidden" value="0" id="flagTab">
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch" id="parent">			
				<li rel="#old" class="active"><strong>消下属缺勤</strong></li>
		</ul>
	</div>
	<div class="content first" style="overflow-x:auto">
            
            <div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
               <input type="hidden" id="pageNo" value=""/>
               <form id="queryform">
                <div class="form-main">
                <!-- 代办  状态-->
               
               <div class="col">
                 <div class="form">
                        <label class="w">部门：</label>
                        <select id="departId" name="departId" class="select_v1 firstDepart">
                        	<option value="">请选择</option>  
                        	<c:forEach items="${departList}" var="deptList">
                        		 
								 <option value= "${deptList.id}">${deptList.name}</option>             	
                        	</c:forEach>
                        </select>
                    </div>  
                    <div class="form">
                        <label class="w">月份：</label>
                        <input id="month" type="text" class="Wdate" name="month" onClick="WdatePicker({dateFmt:'yyyy-MM',maxDate:'%y-{%M}'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM',maxDate:'%y-{%M}'});" value="${thisMonth}" />				
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
                        <button id="query"  class="red-but"><span><i class="icon search"></i>查询</span></button>
                    </div> 
                </div> 
              
			</div>
			  <div id="h3InfoShow" style="display:none;font-size:16px;margin-bottom:8px;"> <span style="color:black">■ 待处理</span>   <span style="color:blue">■ 审核中</span>  <span style="color:#666">■ 已处理</span> </div>
            <div >
            	<div style="float:left;width:55%;">
			      <table border="1" id="reportListTitle">
					  <thead>
					  </thead>
			             <tbody>
			             </tbody>
		          </table>
		      	</div>
            <div style="float:left;width:45%;overflow-x:auto">
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


      	<div class="commonBox popbox" id="updateDiv" style="display:none">
	</div>

      	
</body>
</html>
