<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>选择职位</title>
<%@include file="../../common/common.jsp"%>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<c:url value="/js/base/position/position_tree.js?v=20170515"/>"></script>

</head>
<body>
	<!-- 弹出职位树 -->
	<div style="padding-top:10px;">
		<div class="grid-l200m mt5" style="height:500px;">
			<div class="col-left bdc p10" style="width: 250px;">
				<div>
					<ul id="positionTree" class="ztree"></ul>
				</div>
			</div>
		</div>
	</div>
</body>
</html>