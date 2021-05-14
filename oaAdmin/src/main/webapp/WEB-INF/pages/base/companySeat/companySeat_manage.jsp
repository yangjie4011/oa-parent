<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>座位图管理</title>
<%@include file="../../common/common.jsp"%>
<script type="text/javascript" src= "<%=basePath %>js/common/common.js?v=<%=random %>"/></script>
<script type="text/javascript" src= "<%=basePath %>js/base/companySeat/companySeat_manage.js?v=<%=random %>"/></script>

</head>
<body>
	<div class="fl mt5">
		<form id="queryform">
			<div>
				<span>&nbsp;楼层:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
				<span>
					<select id="floor" name="id" style="width: 80px;">
					</select>
				</span>
			</div>
			<br>
			<div>
				<span>&nbsp;<img alt="座位图" id="seatImg" width="600px" height="450px"></span>
			</div>
			<br>
			<div>
				<span>&nbsp;座位图上传:&nbsp;<input type="file" name="file" id="file"></span>
			</div>
			<br>
			<div>
				<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<a id="save" class="btn-red  btn-common" onclick="save()">保存</a>
				</span>
			</div>
		</form>
	</div>
	
</body>
</html>