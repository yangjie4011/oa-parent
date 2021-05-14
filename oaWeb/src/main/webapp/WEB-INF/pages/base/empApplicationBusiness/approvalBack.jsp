<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>出差申请</title>

<link rel="stylesheet" href="http://i0.ulecdn.com/ule/oa/c/region.css?v=2018062222">
<script type="text/javascript" src="http://i0.ulecdn.com/ule/oa/j/region.js?v=2018062334"></script>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationBusiness/approvalBack.js?v=20191209"></script>

</head>
<body class="b-f2f2f2 mt-44">
<!--   <div class="index-kaoqin">-->
    <div class="oa-travel_apply">
         <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>出差申请
           
            <c:if test="${business.approvalStatus != 200 ||  !isSelf}">
                <a class="toproblem fr"></a>
            </c:if>
            </h1>
        </header>
        <section class="edit-info">
       		 <p class="notice">往返一次回到出差始发地记一次出差</p>
            <h3>出差计划</h3>
            <input  type='hidden'  id="addressNum" />
           <div class="selMatter clearfix">
                <h4 class="fl">去程日期</h4>
                <div class="selarea fr" >
                   <input type="text" readonly placeholder="请选择日期" id="go-date" data-lcalendar="<fmt:formatDate value="${business.startTime}"  pattern="yyyy-MM-dd"  />,9999-12-12" value="<fmt:formatDate value="${business.startTime}"  pattern="yyyy-MM-dd"  />" data-lcalendar="<fmt:formatDate value="${business.startTime}"  pattern="yyyy-MM-dd"  />,<fmt:formatDate value="${business.endTime}"  pattern="yyyy-MM-dd"/>">
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">返程日期</h4>
                <div class="selarea fr">
               		<input type="text" readonly placeholder="请选择日期" id="back-date" data-lcalendar="<fmt:formatDate value="${business.startTime}"  pattern="yyyy-MM-dd"  />,9999-12-12" value="<fmt:formatDate value="${business.endTime}"  pattern="yyyy-MM-dd"  />" data-lcalendar="<fmt:formatDate value="${business.startTime}"  pattern="yyyy-MM-dd"  />,<fmt:formatDate value="${business.endTime}"  pattern="yyyy-MM-dd"/>">
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">时长</h4>
                <div duration="${business.duration}" id="duration" class="selarea fr">
                     ${business.duration}天 
                </div>
            </div>
            
            <div class="selMatter clearfix">
                <h4 class="fl">出差始发地</h4>
                   <div class="selarea fr">
                    <input id="travelAddress0" class="hiddenTravelClass" type="text"  placeholder="请输入出差始发地" disabled="disabled"/>                	
               	 	<input  type='hidden' class='hiddenTravelProvince beginEndProvince'  id="travelProvinceVal0" />   
                	<input  type='hidden'  class='hiddenTravelCity beginEndPCity' id="travelCityVal0" />
               	  </div>
            </div>
            
            <div class="selMatter clearfix">
            <div class='insertBox'  id='insertBox1'>
                <h4 class="fl">地点</h4>               
                <div class="selarea fr">
                    <input id="travelAddress1" class="hiddenTravelClass travelAddressVla" type="text" name="adressVal"  placeholder="请输入出差地点" onclick=""/>
                	<input  type='hidden' class='hiddenTravelProvince travelProvinceVal1' id="travelProvince" />
                	<input  type='hidden' class='hiddenTravelCity travelCityVal1' id="travelCity" />   
                </div>
                
            </div>
            </div>
            <div class="selMatter clearfix addAddress" id="addAddress" style="display:show">
                
            </div>
            
            <div class="addAddressNum" id="addAddressNum" style="display:none">
                <input type="hidden" id="travelProvince1" value="${business.travelProvince1}"/>
            	<input type="hidden" id="travelProvince2" value="${business.travelProvince2}"/>
            	<input type="hidden" id="travelProvince3" value="${business.travelProvince3}"/>
            	<input type="hidden" id="travelProvince4" value="${business.travelProvince4}"/>
            	<input type="hidden" id="travelProvince5" value="${business.travelProvince5}"/>
            	
            	<input type="hidden" id="travelCity1" value="${business.travelCity1}"/>
            	<input type="hidden" id="travelCity2" value="${business.travelCity2}"/>
            	<input type="hidden" id="travelCity3" value="${business.travelCity3}"/>
            	<input type="hidden" id="travelCity4" value="${business.travelCity4}"/>            	
            	<input type="hidden" id="travelCity5" value="${business.travelCity5}"/>
            	
            	<input type="hidden" id="travelProvinceStart" value="${business.startProvinceAddress}"/>            	
            	<input type="hidden" id="travelCityValStart" value="${business.startCityAddress}"/>
            	
            	<input type="hidden" id="businessId" value="${business.id}"/>
            	
            </div>
            
            <div class="selMatter clearfix datenum">
                <h4 class="fl2">出差行程</h4>
                    <span id="beginTravelEnd" style='width:330px;height:70px;'>
                    	${business.address}
                    </span>
            </div>
            

            <div class="selMatter clearfix">
                <h4 class="fl">交通工具</h4>
                <div class="selarea fr">
	                <c:if test="${business.vehicle==100}">
	               		<span>火车</span>
	                </c:if>
	                <c:if test="${business.vehicle==200}">
	               		<span>飞机</span>
	                </c:if>
                </div>
            </div>
            <div class="selMatter clearfix texta">
                <h4 class="fl">出差事由</h4>
                <div class="selarea fr">
                    <c:if test="${business.businessType==100}">
	               		<span>项目</span>
	                </c:if>
	                <c:if test="${business.businessType==200}">
	               		<span>业务</span>
	                </c:if>
                </div>
                <textarea id="reason" rows="5" placeholder="请填写项目／业务名称">${business.reason}</textarea>
            </div>
           <h3 id="detail">每日行程及工作计划安排</h3>
           <div class="textinput">
            </div>
            <input type="hidden" id="token" name="token" value="${token}"/>
            <input type="hidden" id="originalBillId" name="originalBillId" value="${business.id}"/>
            <input type="hidden" id="address"  value="${business.address}"/>
            <input type="hidden" id="token" name="token" value="${token}"/>
            <div class="btn" onclick="save()">提交</div>
        </section>

    </div>
 <!--  </div>  -->
</body>
</html>