<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>外出申请</title>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="javascript:window.history.back(-1)" class="goback fl"><i class="back sr"></i></a>审批流程<a class="toproblem fr"></a></h1>
        </header>
        <div class="main">
            <div class="t-blue">
               <c:if test="${hiActinstList[0].status==100||hiActinstList[0].status==null}">
	                <div class="left">
	                    <p> 
	                        <c:if test="${hiActinstList[0].assignee==null||hiActinstList[0].assignee==''}">
							      ${hiActinstList[0].nodeModuleName}
							</c:if>
							<c:if test="${hiActinstList[0].assignee!=null&&hiActinstList[0].assignee!=''}">
							      ${hiActinstList[0].assignee}&nbsp;${hiActinstList[0].positionName}
							</c:if>
			            </p>
	                </div>
	                <div class="right">待审批</div>
               </c:if>
               <c:if test="${hiActinstList[0].status==200}">
                   <div class="left">
	                    <p><c:if test="${hiActinstList[0].endTime==null}">&nbsp;</c:if><fmt:formatDate value="${hiActinstList[0].endTime}"  pattern="yyyy-MM-dd HH:mm"  /></p>
	                    <p>
	                        <c:if test="${hiActinstList[0].assignee==null||hiActinstList[0].assignee==''}">
							      ${hiActinstList[0].nodeModuleName}
							</c:if>
							<c:if test="${hiActinstList[0].assignee!=null&&hiActinstList[0].assignee!=''}">
							      ${hiActinstList[0].assignee}&nbsp;${hiActinstList[0].positionName}
							</c:if>
	                    </p>
	                    <p>审批意见：<c:if test="${hiActinstList[0].opinion!=null}">
							                                   ${hiActinstList[0].opinion}
							       </c:if>
							       <c:if test="${hiActinstList[0].opinion==null||hiActinstList[0].opinion==''}">
							                                   无
							       </c:if>
				        </p>
	                </div>
	                <div class="right">审批通过</div>
               </c:if>
               <c:if test="${hiActinstList[0].status==300}">
                    <div class="left">
	                    <p><c:if test="${hiActinstList[0].endTime==null}">&nbsp;</c:if><fmt:formatDate value="${hiActinstList[0].endTime}"  pattern="yyyy-MM-dd HH:mm"  /></p>
	                    <p>
	                        <c:if test="${hiActinstList[0].assignee==null||hiActinstList[0].assignee==''}">
							      ${hiActinstList[0].nodeModuleName}
							</c:if>
							<c:if test="${hiActinstList[0].assignee!=null&&hiActinstList[0].assignee!=''}">
							      ${hiActinstList[0].assignee}&nbsp;${hiActinstList[0].positionName}
							</c:if>
	                    </p>
	                    <p>审批意见：<c:if test="${hiActinstList[0].opinion!=null}">
							                                   ${hiActinstList[0].opinion}
							       </c:if>
							       <c:if test="${hiActinstList[0].opinion==null||hiActinstList[0].opinion==''}">
							                                   无
							       </c:if>
	                    </p>
	                </div>
	                <div class="right">审批拒绝</div>
               </c:if>
               <c:if test="${hiActinstList[0].status==400}">
                    <div class="left">
	                    <p><c:if test="${hiActinstList[0].endTime==null}">&nbsp;</c:if><fmt:formatDate value="${hiActinstList[0].endTime}"  pattern="yyyy-MM-dd HH:mm"  /></p>
	                    <p>
	                       <c:if test="${hiActinstList[0].assignee==null||hiActinstList[0].assignee==''}">
							      ${hiActinstList[0].nodeModuleName}
							</c:if>
							<c:if test="${hiActinstList[0].assignee!=null&&hiActinstList[0].assignee!=''}">
							      ${hiActinstList[0].assignee}&nbsp;${hiActinstList[0].positionName}
							</c:if>
	                    </p>
	                    <p>撤销原因：<c:if test="${hiActinstList[0].opinion!=null}">
							                                   ${hiActinstList[0].opinion}
							       </c:if>
							       <c:if test="${hiActinstList[0].opinion==null||hiActinstList[0].opinion==''}">
							                                   无
							       </c:if>
					    </p>
	                </div>
	                <div class="right">撤销申请</div>
               </c:if>
            </div>
            <div class="bottom-main">
                        <ul>
                        <c:forEach var="item" items="${hiActinstList}" varStatus="status"> 
	                                          <c:if test="${item.status==100||item.status==null}">
	                                                <li>
							                            <rou></rou>
							                            <div class="r">
							                                <div class="p1">
							                                 <c:if test="${item.assignee==null||item.assignee==''}">
							                                     ${item.nodeModuleName}
							                                  </c:if>
							                                  <c:if test="${item.assignee!=null&&item.assignee!=''}">
							                                     ${item.assignee}&nbsp;${item.positionName}
							                                  </c:if>
							                                </div>
							                                <div class="p3 gren">待审批</div>
							                            </div>
	                                                 </li>
	                                          </c:if>
	                                          <c:if test="${item.status==200}">
	                                            <c:if test="${item.nodeCode=='RESIGN_HR'}">
	                                                  <li>
	                                                     <tab class="gren">流程结束<san></san></tab>
	                                                        <div class="r">
	                                                           <div class="p1"><c:if test="${item.endTime==null}">&nbsp;</c:if><fmt:formatDate value="${item.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
	                                                           <div class="p2">${item.assignee}&nbsp;${item.positionName}</div>
	                                                           <div class="p3 gren">审批通过</div>
	                                                           <div class="p4"><i>审批意见：</i><c:if test="${item.opinion!=null}">
							                                   ${item.opinion}
							                                </c:if>
							                                <c:if test="${item.opinion==null||item.opinion==''}">
							                                   无
							                                </c:if></div>
	                                                        </div>
	                                                  </li>
                                                </c:if>
                                                <c:if test="${item.nodeCode!='RESIGN_HR'}">
	                                                <li>
							                            <rou></rou>
							                            <div class="r">
							                                <div class="p1"><c:if test="${item.endTime==null}">&nbsp;</c:if><fmt:formatDate value="${item.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
							                                <div class="p2">${item.assignee}&nbsp;${item.positionName}</div>
							                                <div class="p3 gren">审批通过</div>
							                                <div class="p4"><i>审批意见：</i>
							                                <c:if test="${item.opinion!=null}">
							                                   ${item.opinion}
							                                </c:if>
							                                <c:if test="${item.opinion==null||item.opinion==''}">
							                                   无
							                                </c:if>
							                                </div>
							                            </div>
	                                                 </li>
                                                </c:if>
	                                          </c:if>
	                                          <c:if test="${item.status==300}">
	                                             <li>
                                                      <tab class="gren">流程结束<san></san></tab>
					                                  <div class="r">
					                                      <div class="p1"><c:if test="${item.endTime==null}">&nbsp;</c:if><fmt:formatDate value="${item.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
					                                      <div class="p2">${item.assignee}&nbsp;${item.positionName}</div>
					                                      <div class="p3 red">审批拒绝</div>
					                                      <div class="p4"><i>审批意见：</i>
					                                        <c:if test="${item.opinion!=null}">
							                                   ${item.opinion}
							                                </c:if>
							                                <c:if test="${item.opinion==null||item.opinion==''}">
							                                   无
							                                </c:if></div>
					                                  </div>
                                                  </li>
	                                          </c:if>
	                                          <c:if test="${item.status==400}">
                                                  <li>
                                                      <tab class="gren">流程结束<san></san></tab>
					                                  <div class="r">
					                                      <div class="p1"><c:if test="${item.endTime==null}">&nbsp;</c:if><fmt:formatDate value="${item.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
					                                     <div class="p2">${item.assignee}&nbsp;${item.positionName}</div>
					                                      <div class="p3 gren">撤销</div>
					                                      <div class="p4"><i>撤销原因：</i>
					                                        <c:if test="${item.opinion!=null}">
							                                   ${item.opinion}
							                                </c:if>
							                                <c:if test="${item.opinion==null||item.opinion==''}">
							                                   无
							                                </c:if>
					                                      </div>
					                                  </div>
                                                  </li>
	                                          </c:if>
	                                          <c:if test="${item.status==500}">
	                                              <li>
                                                     <tab class="blue">提出申请<san></san></tab>
                                                        <div class="r">
                                                           <div class="p1"><c:if test="${item.endTime==null}">&nbsp;</c:if><fmt:formatDate value="${item.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
                                                           <div class="p2">${item.assignee}&nbsp;${item.positionName}</div>
                                                           <div class="p3 blue">发起审批</div>
                                                           <div class="p4"><i>申请理由：</i>${userOutgoing.reason}</div>
                                                        </div>
                                                  </li>
	                                          </c:if>
                        </c:forEach>
                        </ul>
                    </div>
        </div>
    </div>
     <script>
    $(function(){
       $('.index-kaoqin .main .bottom-main').css('top',$('.t-blue').height() + 80) 
    })
    </script>
</body>
</html>