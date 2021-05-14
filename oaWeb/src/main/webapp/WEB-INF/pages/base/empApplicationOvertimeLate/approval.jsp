<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>晚到申请</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationOvertimeLate/approve.js?v=20180408"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>晚到申请<a class="toproblem fr"></a></h1>
        </header>
        <div class="new-head">
            <a href="<%=basePath%>empApplicationOvertimeLate/process.htm?ruProcdefId=${ruProcdefId }&overtimeLateId=${overtimeLate.id}">
                <div class="img"></div>
               <div class="p1">
                <c:if test="${runTask.processStatus==100}">
                 处理中，等待<span>[
                  <c:forEach var="item" items="${hiActinstList}" varStatus="status">
                        <c:if test="${status.last==false}">
                             ${item.nodeModuleName},
                        </c:if>
                        <c:if test="${status.last==true}">
                             ${item.nodeModuleName}
                        </c:if>
	              </c:forEach>
	              ]</span>审批
                    </c:if>
                    <c:if test="${runTask.processStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${runTask.processStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${runTask.processStatus==400}">
                         已撤销
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${overtimeLate.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
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
                                <p>${overtimeLate.cnName}</p>
                            </div>
                            <div class="font-font">
                                <span>所属部门</span>
                                <p>${overtimeLate.departName}</p>
                            </div>
                            <div class="font-font">
                                <span>申请日期</span>
                                <p><fmt:formatDate value="${overtimeLate.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></p>
                            </div>
                            <div class="font-font">
                                <span>延时工作日期</span>
                                <p><fmt:formatDate value="${overtimeLate.overTimeDate}"  pattern="yyyy-MM-dd"  /></p>
                            </div>
                            <div class="font-font">
                                <span>延迟工作时间</span>
                                <p><fmt:formatDate value="${overtimeLate.overTimeStartTime}"  pattern="HH:mm"  />-<fmt:formatDate value="${overtimeLate.overTimeEndTime}"  pattern="HH:mm"  /></p>
                            </div>
                            <div class="font-font">
                                <span>允许晚到时间</span>
                                <p>${overtimeLate.allowTime}</p>
                            </div>
                            <div class="font-font">
                                <span>实际晚到时间</span>
                                <p>${overtimeLate.actualTime}</p>
                            </div>
                            <div class="font-font">
                                <span>申诉理由</span>
                                <p>${overtimeLate.reason}</p>
                            </div>
                        </div>
                    </div>
                    <input type="hidden" id="token" name="token" value="${token}"/>
                    <input type="hidden" id="nodeCode" name="nodeCode" value="${nodeCode }"/>
                    <input type="hidden" id="ruProcdefId" name="ruProcdefId" value="${ruProcdefId }"/>
                   <c:if test="${nodeCode!=null and nodeCode != 'isEmployeeSelf'}">
	                     <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写审批意见（非必填）" cols="30" rows="10"></textarea>
	                     </div>
                         <div class="double-btn">
                           <div id="${overtimeLate.id}" onclick="refuse(this);" class="l"><em>拒绝</em></div>
                           <div id="${overtimeLate.id}"  onclick="pass(this);" class="r"><em>同意</em></div>
                         </div>
                   </c:if>
                   <c:if test="${nodeCode == 'isEmployeeSelf'}">
                   		<div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
	                     </div>
	                   <div onclick="cancelRunTask()" class="foot-btn cancel-apply"><em>撤回申请</em></div>
                   </c:if>
                </li>
            </ul>
        </div>
    </div>
</body>
<!-- <body class="b-f2f2f2 mt-95">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>晚到申请<a class="toproblem fr"></a></h1>
        </header>
        <ul class="nav-tab">
            <li class="current">
                <p>申请内容</p>
            </li>
            <li>
                <p>审批过程</p>
            </li>
        </ul>
        <div class="main">
            <ul>
                <li>
                    <div class="blk">
                        <div class="mb head">
                            <div class="img"><img width="70px" height="70px" src="${imgUrl}" /></div>
                            <div class="p1">${overtimeLate.cnName}</div>
                            <div class="p3">${overtimeLate.departName}／${overtimeLate.positionName}</div>
                        </div>
                        <div class="mb pad-tb">
                            <div class="font-font">
                                <span>申请时间</span>
                                <p><fmt:formatDate value="${overtimeLate.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></p>
                            </div>
                            <div class="font-font">
                                <span>当前状态</span>
                                <p>
                                <c:if test="${runTask.processStatus==100}">
                                    处理中
                                </c:if>
                                <c:if test="${runTask.processStatus==200}">
                                    已完成
                                </c:if>
                                <c:if test="${runTask.processStatus==300}">
                                    已拒绝
                                </c:if>
                                <c:if test="${runTask.processStatus==400}">
                                    已撤销
                                </c:if>
                                </p>
                            </div>
                        </div>
                        <div class="pad-tb">
                            <div class="font-font">
                                <span>延迟工作日期</span>
                                <p><fmt:formatDate value="${overtimeLate.overTimeDate}"  pattern="yyyy-MM-dd"  /></p>
                            </div>
                            <div class="font-font">
                                <span>延迟工作时间</span>
                                <p><fmt:formatDate value="${overtimeLate.overTimeStartTime}"  pattern="HH:mm"  />-<fmt:formatDate value="${overtimeLate.overTimeEndTime}"  pattern="HH:mm"  /></p>
                            </div>
                            <div class="font-font">
                                <span>允许晚到时间</span>
                                <p>${overtimeLate.allowTime}</p>
                            </div>
                            <div class="font-font">
                                <span>实际晚到时间</span>
                                <p>${overtimeLate.actualTime}</p>
                            </div>
                            <div class="font-font">
                                <span>申诉理由</span>
                                <p>${overtimeLate.reason}</p>
                            </div>
                        </div>
                    </div>
                    <input type="hidden" id="token" name="token" value="${token}"/>
                    <input type="hidden" id="nodeCode" name="nodeCode" value="${nodeCode }"/>
                    <input type="hidden" id="ruProcdefId" name="ruProcdefId" value="${ruProcdefId }"/>
                   <c:if test="${nodeCode!=null and nodeCode != 'isEmployeeSelf'}">
	                     <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写审批意见（非必填）" cols="30" rows="10"></textarea>
	                     </div>
                         <div class="double-btn">
                           <div id="${overtimeLate.id}" onclick="refuse(this);" class="l"><em>拒绝</em></div>
                           <div id="${overtimeLate.id}"  onclick="pass(this);" class="r"><em>同意</em></div>
                         </div>
                   </c:if>
                   <c:if test="${nodeCode == 'isEmployeeSelf'}">
                   		<div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
	                     </div>
	                   <div onclick="cancelRunTask()" class="foot-btn cancel-apply"><em>撤回申请</em></div>
                   </c:if>
                </li>
                <li  style="display: none;">
                    <div class="blk">
                        <div class="head">
                            <div class="img"><img width="70px" height="70px" src="${imgUrl}" /></div>
                            <div class="p1">${overtimeLate.cnName}</div>
                            <div class="p3">${overtimeLate.departName}／${overtimeLate.positionName}</div>
                        </div>
                    </div>
                    <div class="bottom-main">
                        <div class="line">
                        </div>
                        <ul>
                        <c:forEach var="item" items="${hiActinstList}" varStatus="status"> 
	                                     <li>
			                                <rou></rou>
			                                <san></san>
			                                <p>${item.nodeName}</p>
			                                <div class="r">
			                                    <div class="p1"><c:if test="${item.assignee==null || item.assignee==''}">&nbsp;</c:if>${item.assignee}</div>
			                                    <div class="p2"><c:if test="${item.endTime==null}">&nbsp;</c:if><fmt:formatDate value="${item.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
			                                    <div class="p3 red">
			                                          <c:if test="${item.status==100}">
			                                          未完成
			                                          </c:if>
			                                          <c:if test="${item.status==200}">
			                                          审批通过
			                                          </c:if>
			                                          <c:if test="${item.status==300}">
			                                          审批拒绝
			                                          </c:if>
			                                          <c:if test="${item.status==400}">
			                                          撤消
			                                          </c:if>
			                                          <c:if test="${item.status==500}">
			                                          提交
			                                          </c:if>
			                                    </div>
			                                    <c:if test="${item.opinion!=null&&item.opinion!=''}">
			                                       <div class="p4"><i>审批意见：</i>${item.opinion}</div>
			                                    </c:if>
			                                </div>
			                              </li>
                        </c:forEach>         
                        </ul>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</body> -->
</html>