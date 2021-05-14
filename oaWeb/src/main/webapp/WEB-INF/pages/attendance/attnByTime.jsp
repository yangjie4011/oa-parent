
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = com.ule.oa.common.utils.UrlRedirect.getHttpUrl(request) + "/";
	request.setAttribute("basePath",basePath);

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
<table>
<tr>
   <td>统计开始时间（yyyy-mm-dd）</td>
   <td><input type="text" id="startTime" value="2018-4-1"/></td>
</tr>
<tr>
   <td>统计结束时间（yyyy-mm-dd）</td>
   <td><input type="text" id="endTime" value="2018-4-15"/></td>
</tr>
<tr>
   <td>员工ID（可为多个，以英文逗号隔开）</td>
   <td><input type="text" id="employeeIds" value=""/></td>
</tr>
<tr>
   <td colspan="2"><a href="javascript:start();">提交</a></td>
</tr>
</table>
<script type="text/javascript" src="<%=basePath%>js/static/jquery-3.2.1.js?v=20170515"></script>
<script type="text/javascript">
	var contextPath = "<%=path%>";
	var basePath = "<%=basePath%>";
	/* transNormal/startAttnByTime.htm */
	 var start = function(){
		var startTime = $("#startTime").val();
		var endTime = $("#endTime").val();
		var employeeIds = $("#employeeIds").val(); 
		
		if(startTime=='' || endTime==''){
			alert("两个时间都不得为空");
			return;
		}
		
	 	$.ajax({
	 	    type:"POST",
	 		url : basePath + "transNormal/startAttnByTime.htm",
	 		data : {startTime:startTime,endTime:endTime,employeeIds:employeeIds},
	 		dataType : 'json',
	 		success : function(response) {
	 			if(response == null || ''==response) {
	 				alert("好像出错了！");
	 			} else {
	 				alert(response.response);
	 			}
	 		}
	 	 });
	 }
</script>
</body>
</html>