<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>汇报对象管理</title>
<%@include file="../../common/common2.jsp"%>
<!-- 查询人员 -->
<link rel="stylesheet" href="http:<%=oaUle%>/ule/oa/c/personsel.css?v=2019101701">
<script src="../js/util/personsel.js?v=20191105"></script>

<script type="text/javascript" src="<c:url value="/js/base/emp/reportObjectManage.js?v=2019061002"/>"></script>

</head>
<body>
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li class="active"><strong>汇报对象管理</strong></li>
		</ul>
	</div>
	<div class="popbox-center" style="padding-top:100px;padding-left:100px;">
		<div class="form-main">
			<div class="col">
				<div class="form">
					<label class="w"  style="width:130px;">选择员工:</label>
					<input type="hidden" name="employeeIds" id="employeeIds">
					<input id="chooseEmployee" type="text" style="width:180px;height:25px" class="form-text">
				</div>
			</div>
			<div class="col">
				<div class="form">
			         <label class="w" style="width:130px;height:25px">汇报对象:</label>
			         <select id="employeeLeader" name="reportToLeader" class="select_v1">
			         	<option value= "">请选择</option>
			         </select>	
			     </div>
		     </div>
		     <div class="col">
		     </div>
		     <div class="col">
				<div class="button-wrap ml-4">
					<button id="zcqx" class="blue-but" style="width:120px;" onclick="save();">
						<span>确认</span>
					</button>
				</div>
			</div>
	     </div>
     </div>
</body>
</html>