<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>员工工作日志</title>
<script type="text/javascript" src="<%=basePath%>js/workLog/approval.js?v=20200309"/></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>消下属缺勤<a class="toproblem fr"></a></h1>
        </header>
        <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${workLog.processId}&statu=${workLog.approvalStatus}&title=员工工作日志">
                <div class="img"></div>
                <div class="p1">
                <c:if test="${workLog.approvalStatus==100}">
                 处理中，等待<span>[${actName}]</span>审批
                    </c:if>
                    <c:if test="${workLog.approvalStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${workLog.approvalStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${workLog.approvalStatus==400}">
                         已撤销
                     </c:if>
                     <c:if test="${workLog.approvalStatus==500}">
                         已失效
                     </c:if>
                     <c:if test="${workLog.approvalStatus==600}">
                         失效同意
                     </c:if>
                      <c:if test="${workLog.approvalStatus==700}">
                         失效拒绝
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${workLog.createTime}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
                <i></i>
            </a>
        </div>
        <div class="main">
            <ul>
                <li>
                    <div class="blk">
                        <div class="pad-tb">
                          <div class="font-font">
                                <span>员工姓名</span>
                                <p>${workLog.cnName}</p>
                            </div>
                            <div class="font-font">
                                <span>所属部门</span>
                                <p>${workLog.departName}</p>
                            </div>
                            <div class="font-font">
                                <span>日期</span>
                                <p><fmt:formatDate value="${workLog.workDate}"  pattern="yyyy-MM-dd"  /></p>
                            </div>
                        </div>
                    </div>
                    <div class="blk-h">工作内容与输出</div>
                    <c:forEach var="item" items="${workContentList}" varStatus="status"> 
	                    <div class="blk-f">
	                        <div class="xq">
	                            <ol>
	                                <li>${status.index+1}丶${item}</li>
	                            </ol>
	                        </div>
	                    </div>
                    </c:forEach>
                    <div class="blk-h" style="margin-top:10px;">下一个工作日工作计划</div>
                    <c:forEach var="item" items="${workPlanList}" varStatus="status"> 
	                    <div class="blk-f">
	                        <div class="xq">
	                            <ol>
	                                <li>${status.index+1}丶${item}</li>
	                            </ol>
	                        </div>
	                    </div>
                    </c:forEach>
                    <div class="blk-h" style="margin-top:10px;">遇到困难和问题</div>
                    <c:forEach var="item" items="${workProblemList}" varStatus="status"> 
	                    <div class="blk-f">
	                        <div class="xq">
	                            <ol>
	                                <li>${status.index+1}丶${item}</li>
	                            </ol>
	                        </div>
	                    </div>
                    </c:forEach>
                    <input type="hidden" id="token" name="token" value="${token}"/>
                    <c:if test="${canApprove&&index=='wddb'}">
	                     <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写审批意见（非必填）" cols="30" rows="10"></textarea>
	                     </div>
                         <div class="double-btn">
                           <div id="${workLog.processId}"  onclick="refuse(this);" class="l"><em>拒绝</em></div>
                           <div id="${workLog.processId}"  onclick="pass(this);" class="r"><em>同意</em></div>
                         </div>
                   </c:if>
                   <c:if test="${isSelf&&index=='wdsq'}">
                   		<div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
	                     </div>
	                   <div id="${workLog.processId}" onclick="cancelRunTask(this)" class="foot-btn cancel-apply"><em>撤回申请</em></div>
                   </c:if>
                </li>
            </ul>
        </div>
    </div>
</body>
</html>