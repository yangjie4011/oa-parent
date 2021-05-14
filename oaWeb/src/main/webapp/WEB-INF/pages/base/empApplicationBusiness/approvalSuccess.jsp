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
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationBusiness/approvalSuccess.js?v=20181026995"></script>

</head>
<body class="b-f2f2f2 mt-44">
  <div class="index-kaoqin">
    <div class="oa-travel_apply">
         <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>出差审批
            <c:if test="${business.approvalStatus== 200 && isSelf}">
                <a id="${business.id}"  onclick="exportPdf(this)" class="toproblem fr">打印</a>
            </c:if>
            <c:if test="${business.approvalStatus != 200 ||  !isSelf}">
                <a class="toproblem fr"></a>
            </c:if>
            </h1>
        </header>
        <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${business.processinstanceId}&statu=${business.approvalStatus}&title=出差">
                <div class="img"></div>
               <div class="p1">
                <c:if test="${business.approvalStatus==100}">
                 处理中，等待<span>[${actName}]</span>审批
                    </c:if>
                    <c:if test="${business.approvalStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${business.approvalStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${business.approvalStatus==400}">
                         已撤销
                     </c:if>
                     <c:if test="${business.approvalStatus==500}">
                         已失效
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${business.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
                <i></i>
            </a>
        </div>
        <section class="edit-info">
            <h3>出差计划</h3>
            <input  type='hidden'  id="addressNum" />
           <div class="selMatter clearfix">
                <h4 class="fl">去程日期</h4>
                <div class="selarea fr">
                     
                    <p><fmt:formatDate value="${business.startTime}"  pattern="yyyy-MM-dd"  /></p>
                    
                    
                </div>
            </div>
            <div class="selMatter clearfix">
                <h4 class="fl">返程日期</h4>
                <div class="selarea fr">
               		
               		<p><fmt:formatDate value="${business.endTime}"  pattern="yyyy-MM-dd"  /></p>
                
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">时长</h4>
                <div duration="0" id="duration" class="selarea fr">
                     ${business.duration} 天
                </div>
            </div>
            
            <div class="selMatter clearfix">
                <h4 class="fl">出差始发地</h4>
                   <div class="selarea fr">
                    <input id="startAddress"  type="text"  placeholder="请输入出差始发地" onclick=""/>                	
               	  </div>
            </div>
            
            <div class="selMatter clearfix">
            <div >
                <h4 class="fl">地点</h4>
                <div class="selarea fr">
                    <input id="firstAddress" type="text" name="adressVal"  placeholder="请输入出差地点" onclick=""/>
                	<input  type='hidden'  id="travelProvince" />   
                	<input  type='hidden'  id="travelCity" />   
                </div>
            </div>
            </div>
            <div class="selMatter clearfix addAddress" id="addAddress" style="display:show">
                
            </div>
            
            <div class="addAddressNum" id="addAddressNum" style="display:none">
                <input type="hidden" id="addAddressNum2"  value="0"/>
                <input type="hidden" id="addAddressNum3"  value="0"/>
                <input type="hidden" id="addAddressNum4"  value="0"/>
                <input type="hidden" id="addAddressNum5"  value="0"/>
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
           <h3 id="detail" style="display:none;">每日行程及工作计划安排</h3>
           <div class="textinput">
            </div>
            <input type="hidden" id="token" name="token" value="${token}"/>
            <input type="hidden" id="address"  value="${business.address}"/>
            	<c:if test="${business.approvalStatus == 100 && isSelf}">
                  	<div class="b">
                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
                    </div>
	                   <div id="${business.processinstanceId}" onclick="cancelRunTask(this)" class="foot-btn cancel-apply"><em>撤回申请</em></div>
                  </c:if>
                <%-- <c:if test="${business.approvalStatus ==200 && isSelf}">
                       <div id="${business.id}" onclick="tobusinessBack(this);" class="foot-btn cancel-apply" style="font-size: 8px;"><em>修改出差申请</em></div>
             	</c:if> --%>
        </section>

    </div>
 </div>  
</body>
</html>