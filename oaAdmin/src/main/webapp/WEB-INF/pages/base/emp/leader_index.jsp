<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>选择部门负责人</title>
<%@include file="../../common/common.jsp"%>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<c:url value="/js/base/emp/leader_index.js?v=20171205004"/>"></script>

</head>
<body>
	<div class="grid-l200m mt5" id="mainDiv">
		<div class="oa mt5">
			<form id="queryform">
				<table>
					<tr>
						<td>员工中文名:</td>
						<td>
							<input type="text" name="cnName">
						</td>
						<td>
							<input type="button" id="query" icon="icon-search" value="查询" />
						</td>
					</tr>
				</table>
			</form>
			
			<table id="empTable"></table>
		</div>
   	</div>
</body>
</html>