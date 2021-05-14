<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>选择资源</title>
<%@include file="../../common/common2.jsp"%>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<c:url value="/js/base/resource/resource_tree.js?v=20171214"/>"></script>

</head>
<body>
      <div class="content" style="width: 1710px;">
            <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
            <input type="hidden" id="roleId" value="${requestScope.roleId }"/>
            <div class="form-wrap">
<!-- 				<div class="title">&nbsp;Home&nbsp;|<strong><i class="icon"></i>设置权限</strong>&nbsp;|</div> -->
				<div class="title"><strong><i class="icon"></i>设置权限</strong></div>
			</div>
            <ul id="resourceTree" class="ztree"></ul>
            <c:if test="${requestScope.type ==1}">
               <div class="col">
	               <div class="button-wrap ml-4">
	                    <button onclick="saveSet();" class="blue-but"><span>保存设置</span></button>
	               </div>
               </div>
            </c:if>
            
       </div>
</body>
</html>