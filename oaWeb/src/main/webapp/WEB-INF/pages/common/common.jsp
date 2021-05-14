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
	String oaUle = (String) com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer.getProperty("OA_ULE");
	String oaI1Ule = (String) com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer.getProperty("OA_I1_URE");
	String oaI2Ule = (String) com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer.getProperty("OA_I2_URE");
	request.setAttribute("oaUle",oaUle);
	request.setAttribute("oaI1Ule",oaI1Ule);
	request.setAttribute("oaI2Ule",oaI2Ule);
	
	String referer = request.getHeader("referer");
	String titleMsg = "试用版";
	
%>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">

<script type="text/javascript">
	var contextPath = "<%=path%>";
	var basePath = "<%=basePath%>";
	
</script>

<script src="<%=basePath%>js/static/jquery-3.2.1.js" type="text/javascript"></script>
<script src="<%=basePath%>js/static/ajaxfileupload.js" type="text/javascript"></script>

<script src="<%=basePath%>js/util/form-json.js" type="text/javascript"></script>
<script src="<%=domainUle %>/j/jend/jend.js" type="text/javascript"></script>
<script src="<%=domainUle %>/ape/v2/common/js/jend.ape.js" type="text/javascript"></script>
<script src="<%=domainUle %>/ape/v2/cs/frameset.js" type="text/javascript"></script> 
<!-- 通用js -->
<script src="<%=basePath %>js/common/oaCommon.js?v=20190114" type="text/javascript"></script>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta content="telephone=no" name="format-detection">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="apple-mobile-web-app-capable" content="yes">
<!-- uc强制竖屏 -->
<meta name="screen-orientation" content="portrait">
<!-- UC强制全屏 -->
<meta name="full-screen" content="yes">
<!-- UC应用模式 -->
<meta name="browsermode" content="application">
<!-- QQ强制竖屏 -->
<meta name="x5-orientation" content="portrait">
<!-- QQ强制全屏 -->
<meta name="x5-fullscreen" content="true">
<!-- QQ应用模式 -->
<meta name="x5-page-mode" content="app">

<!--插件资源-->
<script src="<%=oaI2Ule %>/ule/oa/j/lib/iscroll5.js?v=20190114"></script>
<link href="<%=oaI1Ule %>/ule/oa/c/common.css" rel="stylesheet">
<!--页面资源-->
<link href="<%=oaI1Ule %>/ule/oa/c/index.css" rel="stylesheet">
<link href="<%=oaI1Ule %>/ule/oa/c/swiper.min.css" rel="stylesheet">
 <!--插件资源-->
<script src="<%=oaI2Ule %>/ule/oa/j/lib/zepto.min.js?v=20190114"></script>
<script src="<%=oaI2Ule %>/ule/oa/j/lib/lCalendar.js?v=20190114"></script>
 <script src="<%=oaI2Ule %>/ule/oa/j/lib/swiper.jquery.min.js?v=20190114"></script>
<!--页面资源-->
<!-- <script src="<%=basePath %>js/common/common.js" type="text/javascript"></script> -->
<script src="<%=oaI2Ule %>/ule/oa/j/common.js?v=20190114"></script>
<script src="<%=oaI2Ule %>/ule/oa/j/index.js?v=20200313"></script>

<script type="text/javascript">
	
	$(function(){
		//获得随心邮域名信息
		/* $.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:{'code':'sxyDomain'},
			url:contextPath + "/sysConfig/getListByCondition.htm",
			success:function(data){
				sxyDomain = data[0].displayCode;
				$("#sxyDomain").val(sxyDomain);
			}
		}); */
		
// 		var callbackMaileSuiXinYou = getCookieValue("callbackMaileSuiXinYou");
// 		if(callbackMaileSuiXinYou != null && callbackMaileSuiXinYou == 1) {
// 			var divObj=document.createElement("div"); 
// 		    divObj.setAttribute("class","return_heart"); 
// 		    divObj.setAttribute("id","return_heart"); 
// 		    var url = "https://"+$("#sxyDomain").val()+"/index.php?s=/addon/Tom/Tomt/index/token/gh_89640de82fb6.html";
// 		    //divObj.innerHTML="<a href='"+url+"'>随心邮</a>";
// 		    divObj.innerHTML="<a href='#' id='sxyEmailIcon'>随心邮</a>";
// 		    var first=document.body.firstChild;//得到页面的第一个元素 
// 		    document.body.insertBefore(divObj,first);//在得到的第一个元素之前插入 
// 		}
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

	//获取cookie
	function getCookieValue(cname) {
	    var name = cname + "=";
	    var ca = document.cookie.split(';');
	    for(var i=0; i<ca.length; i++) {
	        var c = ca[i];
	        while (c.charAt(0)==' ') c = c.substring(1);
	        if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
	    }
	    return "";
	}
	
	// 返回上一页 
	function historyBack(){ 
		location.replace(document.referrer);
	} 

	function getHours(outStart,outStartT,outEnd,outEndT) {
		var hours = 0;
		if(parseInt(outEnd) >= parseInt(outStart)) {
			hours = parseInt(outEnd) - parseInt(outStart);
		} else {
			hours = parseInt(24) - parseInt(outStart);
			hours = hours + parseInt(outEnd);
		}
		if(outStartT == 30) {
			hours = hours- 0.5;
		}
		if(outEndT==30) {
			hours = hours + 0.5;
		}
		return hours;
	}

	function cancelRunTask(obj) {
		//TODO:撤回操作
		OA.twoSurePop({
			tips:'确认撤回吗？',
			sureFn:function(){
				OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{message:$("#approvalReason").val(),processId:$(obj).attr("id"),token:$("#token").val()},
					url:contextPath + "/ruProcdef/back.htm",
					success:function(data){
						if(data.success){
							window.location.href=contextPath + "/ruProcdef/my_examine.htm?urlType=6";
						}else{
							OA.pageLoading(false);
							OA.titlePup(data.message,'lose');
						}
					}
				});
			},
			cancelFn:function(){

			}
		})
	}
		
</script>