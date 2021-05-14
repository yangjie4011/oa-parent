<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>节假日新增</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/vacation/toAdd.js?v=20201202"/></script>
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
	<div class="content first" style="overflow-x:auto">
	
	<div class="">

			<form id="addForm">
				
			
				<input  type="hidden" id="dateType" name="dateType" value="0"/>
				<div class="col">
	                <div class="form">
	                    	<label class="w" style="width:130px;">日期:</label>
	                    	<input  type="text" class="Wdate" id="annualDate" name="annualDate" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
					</div>	
				 
              	</div> 
				<div class="col">
	                <div class="form">
	                    	<label class="w" style="width:130px;">年份:</label>
	                    	<input  type="text" class="form-text" id="year" name="year"/>
					</div>	
				 
              	</div> 
				<div class="col">
	                <div class="form">
	                    	<label class="w" style="width:130px;">类型:</label>
	                    	<input  type="text" class="form-text" id="type" name="type"/>
					</div>	
				 
              	</div> 
              	<div class="col">
	                <div class="form">
	                    	<label class="w" style="width:130px;">节日:</label>
	                    	<input  type="text" class="form-text" id="subject" name="subject"/>
					</div>	
				 
              	</div> 
              	<div class="col">
	                <div class="form">
	                    	<label class="w" style="width:130px;">备注:</label>
	                    	<input  type="text" class="form-text" id="content" name="content"/>
					</div>	
				 
              	</div> 
			</form>
                  <div style="text-align:center;margin-bottom:3px;">
                  	 <button id="add" class="blue-but btn-middle"><span><i class="icon"></i>提交</span></button>    
                  </div>
				
	
		</div>
	
	</div>
            

            




      	
</body>
</html>
