<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>延时工作申请</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationOvertime/approve.js?v=20180530"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>延时工作申请<a class="toproblem fr"></a></h1>
        </header>
        <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${overtime.processInstanceId}&statu=${overtime.approvalStatus}&title=延时工作申请">
                <div class="img"></div>
                <div class="p1">
                <c:if test="${overtime.approvalStatus==100}">
                 处理中，等待<span>[${taskVO.actName}]</span>审批
                    </c:if>
                    <c:if test="${overtime.approvalStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${overtime.approvalStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${overtime.approvalStatus==400}">
                         已撤销
                     </c:if>
                     <c:if test="${overtime.approvalStatus==500}">
                         已失效
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${overtime.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
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
                                <p>${overtime.cnName}</p>
                            </div>
                            <div class="font-font">
                                <span>所属部门</span>
                                <p>${overtime.departName}</p>
                            </div>
                            <div class="font-font">
                                <span>申请日期</span>
                                <p><fmt:formatDate value="${overtime.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></p>
                            </div>
                            <div class="font-font">
                                <span>延时工作日期</span>
                                <p><fmt:formatDate value="${overtime.applyDate}"  pattern="yyyy-MM-dd"  /></p>
                            </div>
                            <div class="font-font">
                                <span>预计工作时长</span>
                                <p><fmt:formatDate value="${overtime.expectStartTime}"  pattern="HH:mm"  />-<c:if test="${overtime.expectStartTime.getHours()>=overtime.expectEndTime.getHours()}">
                                      次日
                                </c:if>
                                <fmt:formatDate value="${overtime.expectEndTime}"  pattern="HH:mm"  />&nbsp;&nbsp;${overtime.expectDuration}小时</p>
                            </div>
                            <c:if test="${overtime.actualStartTime!=null}">
                            <div class="font-font">
                                <span>实际工作时长</span>
                                <p><fmt:formatDate value="${overtime.actualStartTime}"  pattern="HH:mm"  />-<c:if test="${overtime.actualStartTime.getHours()>=overtime.actualEndTime.getHours()}">
                                      次日
                                </c:if><fmt:formatDate value="${overtime.actualEndTime}"  pattern="HH:mm"  />&nbsp;&nbsp;${overtime.actualDuration}小时</p>
                            </div>
                            </c:if>
                            <div class="font-font">
                                <span>申请事由</span>
                                <p>  <c:if test="${overtime.applyType==100}">
                                项目
                                </c:if>
                                <c:if test="${overtime.applyType==200}">
                               会议
                                </c:if>
                                <c:if test="${overtime.applyType==300}">
                                日常工作
                                </c:if>
                                <c:if test="${overtime.applyType==400}">
                                其他
                                </c:if></p>
                            </div>
                            <c:if test="${overtime.applyType==100}">
                               <div class="font-font">
	                                <span>项目名称</span>
	                                <p>${overtime.projectName}</p>
                               </div>
                            </c:if>
                            <div class="font-font">
                                <span>事由说明</span>
                                <p>${overtime.reason}</p>
                            </div>
                        </div>
                    </div>
                    <input type="hidden" id="token" name="token" value="${token}"/>
                    <c:if test="${canApprove&&!isSelf}">
	                     <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写审批意见（非必填）" cols="30" rows="10"></textarea>
	                     </div>
                         <div class="double-btn">
                           <div id="${overtime.processInstanceId}" onclick="refuse(this);" class="l"><em>拒绝</em></div>
                           <div id="${overtime.processInstanceId}"  onclick="pass(this);" class="r"><em>同意</em></div>
                         </div>
                   </c:if>
                   <c:if test="${overtime.approvalStatus==100 and isSelf}">
                   		<div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
	                     </div>
	                   <div id="${overtime.processInstanceId}" onclick="cancelRunTask(this)" class="foot-btn cancel-apply"><em>撤回申请</em></div>
                   </c:if>
                </li>
            </ul>
        </div>
    </div>
</body>
</html>