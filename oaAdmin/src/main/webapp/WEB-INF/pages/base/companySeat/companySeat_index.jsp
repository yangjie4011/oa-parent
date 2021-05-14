<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>座位图</title>
<%@include file="../../common/common.jsp"%>
<script type="text/javascript" src= "<%=basePath %>js/common/common.js?v=<%=random %>"/></script>
<script type="text/javascript" src= "<%=basePath %>js/base/companySeat/companySeat_index.js?v=<%=random %>"/></script>

</head>
<body>
	<div class="datatable fl mt5">
		<form id="queryform">
			<div>
				<span>&nbsp;所在楼层:&nbsp;</span>
				<span>
					<select id="floor" style="width: 80px;"></select>
				</span>
			</div>
			<br>
			<div>
				<span>&nbsp;<img alt="座位图" id="seatImg" width="900px" height="750px"></span>
			</div>
		</form>
	</div>
</body>
</html>