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
	
	String domainUle1 = (String) com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer.getProperty("DOMAIN_ULE1");
	request.setAttribute("domainUle1",domainUle1);
	
	String oaUle = (String) com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer.getProperty("OA_ULE");
	request.setAttribute("oaUle",oaUle);
	
%>

<link href="<%=basePath %>css/common/ape.css" rel="stylesheet"/>
<link href="<%=basePath %>css/common/oa.css?v=20200506" rel="stylesheet"/>
<link rel="stylesheet" type="text/css" href="<%=domainUle %>/cun/platform/c/common_v1.css?v=20200319">
<link rel="stylesheet" type="text/css" href="<%=basePath %>css/common/oaCommon.css?v=20171213039">
<script src="<%=domainUle1 %>/cun/platform/j/jquery-1.8.2.min.js" type="text/javascript"></script>
<script src="<%=domainUle1 %>/cun/platform/j/common.js?v=20200319" type="text/javascript"></script>
<script src="<%=domainUle %>/j/jend/jend.js" type="text/javascript"></script>
<script src="<%=domainUle %>/ape/v2/common/js/jend.ape.js" type="text/javascript"></script>
<style type="text/css">
	table td {
		padding: 6px 20px;
		line-height: 20px;
	}
</style>
<!-- 通用js -->
<script src="<%=basePath %>js/common/oaCommon.js?v=20190725" type="text/javascript"></script>
<script language="javascript" type="text/javascript" src="<%=basePath %>js/common/My97DatePicker/WdatePicker.js"></script>
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
	
	function initPage(pageClassName, response, commonPage) {
		initPageByName(pageClassName, response, commonPage,"gotoPage"); 
	}

	function initPageByName(pageClassName, response, commonPage, functionName) {
		var total = response.totalPages;
		var count = response.total;
		$("#"+pageClassName).empty();
			var pageHtml = "<div class='list-page fr'><span>共<strong>" + count + "</strong>条</span>";
			if(commonPage == 1) {
				pageHtml = pageHtml + "<a class='first disabled' href='#'>首页</a><a class='prev disabled' href='#'>上一页</a>";
			} else {
				var upon = Number(commonPage) - 1;
				pageHtml = pageHtml + "<a class='first' onclick='"+functionName+"(1);'>首页</a><a class='prev' onclick='"+functionName+"("+upon+");'>上一页</a>";
			}
			var fi = 1;
			var ei = total;
			var type = 0;
			if(total > 10) {
				if(commonPage <= 4) {
					type = 1;
					ei = 6;
				} else if(total - commonPage > 4) {
					type = 2;
					fi = Number(commonPage) - 2;
					ei = Number(commonPage) + 2;
				} else if(total - commonPage <= 4) {
					type = 3;
					fi = Number(total) - 5;
				}
			} 
			if(type == 2 || type == 3) {
				pageHtml = pageHtml + "<a onclick='"+functionName+"(1);'>1</a>";
				pageHtml = pageHtml + "<a class='c' onclick='"+functionName+"("+(fi-1)+");'><span>…</span></a>";
			}
			for (var i = fi; i <= ei; i++) {
				if(commonPage == i) {
					pageHtml = pageHtml + "<a class='on' onclick='"+functionName+"("+i+");'>"+i+"</a>"; 
				} else	{
					pageHtml = pageHtml + "<a onclick='"+functionName+"("+i+");'>"+i+"</a> ";
				}
			}
			if(type == 1 || type == 2) {
				pageHtml = pageHtml + "<a class='c' onclick='"+functionName+"("+(ei+1)+");'><span>…</span></a>";
				pageHtml = pageHtml + "<a class='c' onclick='"+functionName+"("+total+");'>"+total+"</a> ";
			}
			if(commonPage == total) {
				pageHtml = pageHtml + "<a class='next disabled' href='#'>下一页</a><a class='last disabled' href='#'>尾页</a>";
			} else {
				var upon = Number(commonPage) + 1;
				pageHtml = pageHtml + "<a class='next' onclick='"+functionName+"("+upon+");'>下一页</a><a class='last' onclick='"+functionName+"("+total+");'>尾页</a>";
			}
			pageHtml = pageHtml + "<span>共<strong>" + total + "</strong>页</span></div>"; 
			$("#" + pageClassName).append(pageHtml);
	}
</script>