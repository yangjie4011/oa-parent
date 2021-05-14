<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>导入打卡数据</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/attnManager/importSignRecord.js?v=20180301"/></script>
<script type="text/javascript" src="../js/util/dateUtil.js?v=2018031902"/></script>
</head>
<body>
    <div style="margin:0 auto ;margin-top:100px; width:500px;height:180px;border:1px solid;border-radius:25px;padding: 10px;">
		<form enctype="multipart/form-data" id="batchUpload"  action="empAttn/uploadSignExcel.html" method="post" class="form-horizontal">    
		    <button class="btn btn-success btn-sm" id="uploadEventBtn"  type="button" >选择文件</button>  
		    <input type="file" name="file"  style="width:0px;height:0px;" id="uploadEventFile">  
		    <input id="uploadEventPath"  disabled="disabled"  type="text" placeholder="请选择excel表" style="border: 1px solid #e6e6e6; height: 26px;width: 200px;" >                                           
		</form>  
		<div style="float:left;width:100%;margin-top:10px;">
			<button type="button" class="btn btn-success btn-sm"  onclick="signExcel.uploadBtn()" >上传</button>
			&nbsp;&nbsp;&nbsp;<a align ="left" href="<%=basePath %>/template/signRecordTemplate.xlsx">下载模板</a>
		</div>
		<div style="float:left;width:100%;margin-top:10px;"><button type="button" class="btn btn-success btn-sm"  onclick="signExcel.openCalculate()" >重算考勤</button></div>
	</div>
	
    <div id = "calculateDiv" style="margin:0 auto ;margin-top:20px; width:500px;height:200px;border:1px solid;border-radius:25px;padding: 10px;display:none;">
		<form id="calculateForm"  action="empAttn/reCalculate.html" method="post""> 
		<div style="float:left;width:100%;margin-top:10px;">
		            开始时间 （含）： 
           	 <input id="startTime" type="text" class="Wdate" name="startTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
           	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" value="${startTime }"/>
		</div>
		<div style="float:left;width:100%;margin-top:10px;">
		           结束时间 （含）： 
			<input id="endTime" type="text" class="Wdate" name="endTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
			       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" value="${endTime }"/>
		</div>  
		</form>  
		<div style="float:left;width:100%;margin-top:10px;"><button type="button" class="btn btn-success btn-sm"  onclick="signExcel.reCalculate()" >提交重算</button></div>
        <p style="float:left;width:100%;margin-top:10px;">温馨提示：只能计算最近7天内的考勤，如果只算一天的考勤，请选择相同的开始时间和结束时间。
        </p>
	</div>
</body>
</html>