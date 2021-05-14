<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = com.ule.oa.common.utils.UrlRedirect.getHttpUrl(request) + "/";
	request.setAttribute("basePath",basePath);
	String random = String.valueOf(new SimpleDateFormat("yyyyMMddhhmm").format(new Date()));
	
	String domainUle = (String) com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer.getProperty("DOMAIN_ULE");
	request.setAttribute("domainUle",domainUle);
%>
<link href="<%=basePath %>css/common/ape.css" rel="stylesheet"/>
<link href="<%=basePath %>css/common/oa.css" rel="stylesheet"/>

<script src="<%=basePath%>js/static/jquery-3.2.1.js" type="text/javascript"></script>
<script src="<%=basePath%>js/util/easyui/jquery.easyui.min.js" type="text/javascript"></script>
<link href="<%=basePath%>css/util/easyui/easyui.css" rel="stylesheet"/>
<link href="<%=basePath%>css/util/easyui/icon.css" rel="stylesheet"/>
<script src="<%=basePath%>js/util/easyui/easyui-lang-zh_CN.js" type="text/javascript"></script>


<script src="<%=basePath%>js/util/form-json.js" type="text/javascript"></script>
<script src="<%=domainUle %>/j/jend/jend.js" type="text/javascript"></script>
<script src="<%=domainUle %>/ape/v2/common/js/jend.ape.js" type="text/javascript"></script>
<script src="<%=domainUle %>/ape/v2/cs/frameset.js" type="text/javascript"></script>
<!-- 通用js -->
<script src="<%=basePath %>js/common/oaCommon.js?v=20171207001" type="text/javascript"></script>
<script type="text/javascript">
	var contextPath = "<%=path%>";
	var basePath = "<%=basePath%>";

	$(function(){
		// 设置jQuery Ajax全局的参数  
		$.ajaxSetup({
			cache : false,
			dataType : "json",
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				switch (XMLHttpRequest.status) {
				case (500):
				    OA.titlePup('操作失败！','lose');
					break;
				case (400):
					break;
				}
			},
			complete: function(XMLHttpRequest,textStatus){
				var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
				if(sessionstatus == "timeout"){
					window.location.href = XMLHttpRequest.getResponseHeader("Location");
			 	}
				if (XMLHttpRequest.status == 401) {
					OA.titlePup('无权限执行此操作！','lose');
				}
			}
		});
	});
</script>