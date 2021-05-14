<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@include file="../../common/common.jsp"%>
    <link rel="icon" href="data:;base64,=">
    <title>详情</title>
</head>
<body class="b-f2f2f2 mt-220">
    <div class="oa-person_detail">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>employeeApp/addressList.htm" class="goback fl"><i class="back sr"></i></a>详情<a href="#" class="save fr"></a></h1>
            <section class="bodyInfo">
                <div class="head" id="ePic">
                	<c:choose>
                		<c:when test="${empty picUrl }">
                			<!-- <img src="//dfsread-service.http.beta.uledns.com/ule/oa/i/head.jpg" alt=""> -->
                			<img src="//i0.beta.ulecdn.com/ule/oa/i/head.jpg" alt="">
                		</c:when>
                		<c:otherwise>
                			<img src="${picUrl }" alt="">
                		</c:otherwise>
                	</c:choose>
                </div>
                <p class="name">${cnName }</p>
                <p>${departName }／${positionName }</p>
                <p>汇报人   ${reportToLeader}</p>
            </section>
        </header>

        <section class="workInfo">
            <div class="selMatter clearfix datenum">
                <h4 class="fl">分机</h4>
                <div class="selarea fr">
                    ${extensionNumber }
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">邮箱</h4>
                <div class="selarea fr">
                    ${email }
                </div>
            </div>
<!--             <div class="selMatter clearfix datenum phonenum"> -->
<!--                 <h4 class="fl">手机</h4> -->
<!--                 <div class="selarea fr"> -->
<!--                     <span id="showMobile"></span> -->
<!--                     <i class="mobileShow open"></i> -->
<!--                 </div> -->
<%--                 <input type="hidden" id="realMobile" value="${mobile }"/> --%>
<!--             </div> -->
            <div class="selMatter clearfix datenum">
                <h4 class="fl">座位号</h4>
                <div class="selarea fr">
                    ${telephone }
                </div>
            </div>
        </section>
    </div>
    
    <script type="text/javascript">
	var realMobile = $("#realMobile").val();
	var dealedMobile = ''; 
	if(realMobile != ''){
		dealedMobile = realMobile.substr(0, 3) + '****' + realMobile.substr(7); 
	}else{
		$(".mobileShow").hide();
	}
	
    $(function(){
    	
    	$(".mobileShow").click(function(){
    		showReal();
    	});
    	
    	$(".mobileShow").click();
    });
    
    var showReal = function(){
    	if($(".mobileShow").hasClass("close")){
        	$(".mobileShow").removeClass("close");
        	$(".mobileShow").addClass("open");
        	$("#showMobile").text(realMobile);
    	}else{
    		$(".mobileShow").removeClass("open");
        	$(".mobileShow").addClass("close");
        	$("#showMobile").text(dealedMobile);
    	}
    }
    </script>
</body>
</html>