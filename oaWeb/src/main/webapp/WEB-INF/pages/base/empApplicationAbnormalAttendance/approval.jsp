<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>异常考勤申诉</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationAbnormalAttendance/approval.js?v=20180409"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>异常考勤申诉<a class="toproblem fr"></a></h1>
        </header>
        <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${attendance.processInstanceId}&statu=${attendance.approvalStatus}&title=异常考勤申诉">
                <div class="img"></div>
                <div class="p1">
                <c:if test="${attendance.approvalStatus==100}">
                 处理中，等待<span>[${actName}]</span>审批
                    </c:if>
                    <c:if test="${attendance.approvalStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${attendance.approvalStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${attendance.approvalStatus==400}">
                         已撤销
                     </c:if>
                     <c:if test="${attendance.approvalStatus==500}">
                         已失效
                     </c:if>
         			<c:if test="${attendance.approvalStatus==600}">
                         失效同意
                     </c:if>
                     <c:if test="${attendance.approvalStatus==700}">
                         失效拒绝
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${attendance.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
                <i></i>
            </a>
        </div>
        <div class="main">
            <ul>
                <li>
                    <div class="blk">
                        <div class="pad-tb">
                            <c:if test="${attendance.applyType==0}">
	                            <div class="font-font">
	                                <span>申请人</span>
	                                <p>${attendance.cnName}</p>
	                            </div>
                            </c:if>
                            <c:if test="${attendance.applyType==1}">
	                            <div class="font-font">
	                                <span>异常考勤员工</span>
	                                <p>${attendance.cnName}</p>
	                            </div>
	                            <div class="font-font">
	                                <span>代申诉人</span>
	                                <p>${attendance.agentName}</p>
	                            </div>
                            </c:if>
                            <div class="font-font">
                                <span>所属部门</span>
                                <p>${attendance.departName}</p>
                            </div>
                            <div class="font-font">
                                <span>申请日期</span>
                                <p><fmt:formatDate value="${attendance.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></p>
                            </div>
                            <div class="font-font">
                                <span>考勤日期</span>
                                <p><fmt:formatDate value="${attendance.abnormalDate}"  pattern="yyyy-MM-dd"  /></p>
                            </div>
                            <div class="font-font">
                                <span>打卡时间</span>
                                <p>
                                 <c:if test="${attendance.startPunchTime==null}">
                                空卡
                                </c:if>
                                <c:if test="${attendance.startPunchTime!=null}">
                                <fmt:formatDate value="${attendance.startPunchTime}"  pattern="HH:mm"  />
                                </c:if>
                                -
                                <c:if test="${attendance.endPunchTime==null}">
                                空卡
                                </c:if>
                                <c:if test="${attendance.endPunchTime!=null}">
                               <fmt:formatDate value="${attendance.endPunchTime}"  pattern="HH:mm"  />
                                </c:if>
                                </p>
                            </div>
                            <div class="font-font">
                                <span>申诉考勤时间</span>
                                <p><fmt:formatDate value="${attendance.startTime}"  pattern="HH:mm"  />&nbsp;-&nbsp;<fmt:formatDate value="${attendance.endTime}"  pattern="HH:mm"  /></p>
                            </div>
                            <div class="font-font">
                                <span>申诉理由</span>
                                <p>${attendance.reason}</p>
                            </div>
                        </div>
                    </div>
                    <input type="hidden" id="token" name="token" value="${token}"/>
                    <c:if test="${canApprove&&!isSelf}">
	                     <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写审批意见（非必填）" cols="30" rows="10"></textarea>
	                     </div>
                         <div class="double-btn">
                           <div id="${attendance.processInstanceId}" onclick="refuse(this);" class="l"><em>拒绝</em></div>
                           <div id="${attendance.processInstanceId}"  onclick="pass(this);" class="r"><em>同意</em></div>
                         </div>
                    </c:if>
                    <c:if test="${isSelf}">
                   		<div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
	                     </div>
	                   <div id="${attendance.processInstanceId}" onclick="cancelRunTask(this)" class="foot-btn cancel-apply"><em>撤回申请</em></div>
                    </c:if>
                </li>
            </ul>
        </div>
    </div>
</body>
</html>