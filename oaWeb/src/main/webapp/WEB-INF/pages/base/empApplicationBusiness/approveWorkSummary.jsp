<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>出差总结报告审批</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationBusiness/approveWorkSummary.js?v=20190701"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>出差总结报告审批
            <c:if test="${(business.approvalReportStatus== 200 && isSelf)|| (business.approvalReportStatus== 500 && isSelf) }">
                <a id="${business.id}" onclick="exportPdf(this)" class="toproblem fr">打印</a>
            </c:if>
            <c:if test="${business.approvalStatus != 200 && !isSelf}">
                <a class="toproblem fr"></a>
            </c:if>
            </h1>
        </header>
        <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${business.processinstanceReportId}&statu=${business.approvalReportStatus}&title=出差总结报告">
                <div class="img"></div>
                <div class="p1">
                 <c:if test="${business.approvalReportStatus==0}">
     	提出申请
                    </c:if>
                <c:if test="${business.approvalReportStatus==100}">
                 处理中，等待<span>[${actName}]</span>审批
                    </c:if>
                    <c:if test="${business.approvalReportStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${business.approvalReportStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${business.approvalReportStatus==400}">
                         已撤销
                     </c:if>
                     <c:if test="${business.approvalReportStatus==500}">
                         已失效
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${business.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
                <i></i>
            </a>
        </div>
        <div class="main">
            <ul>
                <li>
                    <div class="blk">
                        <div class="pad-tb">
                            <div class="font-font">
                                <span>申请人</span>
                                <p>${business.cnName}</p>
                            </div>
                            <div class="font-font">
                                <span>所属部门</span>
                                <p>${business.departName}</p>
                            </div>
                            <div class="font-font">
                                <span>申请日期</span>
                                <p><fmt:formatDate value="${business.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></p>
                            </div>
                        </div>
                    </div>
                    <div class="blk-h">出差计划</div>
                    <div class="blk">
                        <div class="pad-tb">
                            <div class="font-font">
                                <span>去程日期</span>
                                <p><fmt:formatDate value="${business.startTime}"  pattern="yyyy-MM-dd"  /></p>
                            </div>
                            <div class="font-font">
                                <span>返程日期</span>
                                <p><fmt:formatDate value="${business.endTime}"  pattern="yyyy-MM-dd"  /></p>
                            </div>
                            <div class="font-font">
                                <span>时长</span>
                                <p>${business.duration} 天</p>
                            </div>
                            <div class="font-font">
                                <span>出差行程</span>
                                <p>${business.address}</p>
                            </div>
                            <div class="font-font">
                                <span>出差事由</span>
                                <p> 
	                                <c:if test="${business.businessType==100}">
					                                              项目出差/${business.reason}
					                </c:if>
					                <c:if test="${business.businessType==200}">
					                                            业务出差/${business.reason}
					                </c:if>
				                </p>
                            </div>
                            <div class="font-font">
                                <span>交通工具</span>
                                <p>  
	                                <c:if test="${business.vehicle==100}">
					                                火车                         
					                </c:if>
					                <c:if test="${business.vehicle==200}">
					                                飞机                   
					                </c:if>
				                </p>
                            </div>
                        </div>
                    </div>
                    <div class="blk-h">每日行程及任务完成情况</div>
                   <c:forEach var="item" items="${detailList}" varStatus="status"> 
                    <div class="blk-f">
                        <div class="xq">
                           <p><fmt:formatDate value="${item.workStartDate}"  pattern="yyyy-MM-dd"  />&nbsp至&nbsp<fmt:formatDate value="${item.workEndDate}"  pattern="yyyy-MM-dd"  /></p>
                            <ol>
                                <li>${item.workSummary}</li>
                            </ol>
                        </div>
                    </div>
                    </c:forEach>
                     <input type="hidden" id="token" name="token" value="${token}"/>
                    <c:if test="${canApprove}">
	                     <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写审批意见（非必填）" cols="30" rows="10"></textarea>
	                     </div>
                         <div class="double-btn">
                           <div id="${business.processinstanceReportId}" onclick="refuse(this);" class="l"><em>拒绝</em></div>
                           <div id="${business.processinstanceReportId}"  onclick="pass(this);" class="r"><em>同意</em></div>
                         </div>
                    </c:if>
                </li>
            </ul>
        </div>
    </div>
</body>
</html>