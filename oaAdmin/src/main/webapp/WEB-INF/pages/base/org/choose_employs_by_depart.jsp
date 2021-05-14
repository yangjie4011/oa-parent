<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="icon" href="data:;base64,=">
<title>部门</title>
<%@ include file="/WEB-INF/pages/common/common1.jsp"%>

<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src= "<%=basePath %>js/base/org/choose_employs_by_depart.js?v=<%=random %>"></script>
<style type="text/css">
	.csp:hover{
		background-color: blue
	}
	dt{
		text-align:right;
	}
	required{
		color:red;
	}
</style>
</head>
<body >
	<div class="datapage">
		<div style="padding-top:32px;">
			<div class="grid-l200m mt5">
				<!-- 左侧树形部门展示begin -->
				<div class="col-left bdc p10" style="width: 240px;">
					<input type="hidden" id="departId">
					<div>
						<ul id="departTree" class="ztree"></ul>
					</div>
				</div>
				<!-- 左侧树形部门展示end -->
				
				<!-- 右侧列表展示begin -->
				<div class="col-main pl10">
					<div class="oa mt5">
						<div id="employTableDiv">
							<table id="employTable"></table>
						</div>
	    			</div>
				</div>
				<!-- 右侧列表展示end -->
			</div>
		</div>
	</div>
</body>
</html>