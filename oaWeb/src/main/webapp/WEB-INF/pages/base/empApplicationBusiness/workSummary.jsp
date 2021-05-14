<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>出差总结报告</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationBusiness/workSummary.js?v=20190701"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="oa-travel_summary">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>出差总结报告<a href="#" class="save fr"></a></h1>
        </header>

        <section class="edit-info">
            <h3>出差计划</h3>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">去程日期</h4>
                <div class="selarea fr">
                    <fmt:formatDate value="${business.startTime}"  pattern="yyyy-MM-dd"  />
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">返程日期</h4>
                <div class="selarea fr">
                    <fmt:formatDate value="${business.endTime}"  pattern="yyyy-MM-dd"  />
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">时长</h4>
                <div class="selarea fr">
                    ${business.duration} 天
                </div>
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl2">出差行程</h4>
                   ${business.address}
            </div>
            <div class="selMatter clearfix datenum">
                <h4 class="fl">出差事由</h4>
                <div class="selarea fr">
                   ${business.reason}
                </div>
            </div>

            <div class="selMatter clearfix datenum">
                <h4 class="fl">交通工具</h4>
                <div class="selarea fr">
                    <c:if test="${business.vehicle==100}">
				       火车
				   </c:if>
				   <c:if test="${business.vehicle==200}">
				       飞机
				   </c:if>
				   <c:if test="${business.vehicle==300}">
				       汽车
				   </c:if>
                </div>
            </div>

            <h3>每日行程及任务完成情况</h3>
            <c:forEach var="item" items="${detailList}" varStatus="status"> 
		           <div id="${item.id}" class="textinput">
		             <h4><fmt:formatDate value="${item.workStartDate}"  pattern="yyyy-MM-dd"  />&nbsp至&nbsp<fmt:formatDate value="${item.workEndDate}"  pattern="yyyy-MM-dd"  /></h4>
		             <textarea rows="5" placeholder="例：1. 情况分析（制定计划的根据）&#13;&#10;      2. 工作任务和要求（做什么）&#13;&#10;      3. 工作的方法、步骤和措施"></textarea>
		           </div>
				</c:forEach>
			<div class="selMatter clearfix texta">
                <h4 class="fl">备注</h4>
                <textarea id="remark" rows="5" placeholder="(非必填)"></textarea>
            </div>	
			<input type="hidden" id="token" name="token" value="${token}"/>
            <div class="btn" id="${business.id}" onclick="save(this);">提交</div>
        </section>

    </div>
</body>
</html>