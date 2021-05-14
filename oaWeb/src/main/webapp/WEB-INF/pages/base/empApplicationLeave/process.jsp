<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>${title}</title>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="javascript:window.history.back(-1)" class="goback fl"><i class="back sr"></i></a>审批流程<a class="toproblem fr"></a></h1>
        </header>
        <div class="main">
            <div class="t-blue">
            	
            	<c:if test="${statu==0}">
	                <div class="left">
	                    <p> 
	                        <c:if test="${flows[0].assigneeName==null||flows[0].assigneeName==''}">
							      ${flows[0].departName}
							</c:if>
							<c:if test="${flows[0].assigneeName!=null&&flows[0].assigneeName!=''}">
							      ${flows[0].assigneeName}&nbsp;${flows[0].positionName}
							</c:if>
			            </p>
	                </div>
	                <div class="right">待提交申请</div>
               </c:if>
               <c:if test="${statu==100}">
	                <div class="left">
	                    <p> 
	                        <c:if test="${flows[0].assigneeName==null||flows[0].assigneeName==''}">
							      ${flows[0].departName}
							</c:if>
							<c:if test="${flows[0].assigneeName!=null&&flows[0].assigneeName!=''}">
							      ${flows[0].assigneeName}&nbsp;${flows[0].positionName}
							</c:if>
			            </p>
	                </div>
	                <div class="right">待审批</div>
               </c:if>
               
               <c:if test="${statu==200}">
                   <div class="left">
	                    <p><c:if test="${flows[0].finishTime==null}">&nbsp;</c:if><fmt:formatDate value="${flows[0].finishTime}"  pattern="yyyy-MM-dd HH:mm"  /></p>
	                    <p>
	                         <c:if test="${flows[0].assigneeName==null||flows[0].assigneeName==''}">
							      ${flows[0].departName}
							</c:if>
							<c:if test="${flows[0].assigneeName!=null&&flows[0].assigneeName!=''}">
							      ${flows[0].assigneeName}&nbsp;${flows[0].positionName}
							</c:if>
	                    </p>
	                    <p>审批意见：<c:if test="${flows[0].comment!=null}">
							                                   ${flows[0].comment}
							       </c:if>
							       <c:if test="${flows[0].comment==null||flows[0].comment==''}">
							                                   无
							       </c:if>
				        </p>
	                </div>
	                <div class="right">审批通过</div>
               </c:if>
               
               
               <c:if test="${statu==300}">
                    <div class="left">
	                    <p><c:if test="${flows[0].finishTime==null}">&nbsp;</c:if><fmt:formatDate value="${flows[0].finishTime}"  pattern="yyyy-MM-dd HH:mm"  /></p>
	                    <p>
	                        <c:if test="${flows[0].assigneeName==null||flows[0].assigneeName==''}">
							      ${flows[0].departName}
							</c:if>
							<c:if test="${flows[0].assigneeName!=null&&flows[0].assigneeName!=''}">
							      ${flows[0].assigneeName}&nbsp;${flows[0].positionName}
							</c:if>
	                    </p>
	                    <p>审批意见：<c:if test="${flows[0].comment!=null}">
							                                   ${flows[0].comment}
							       </c:if>
							       <c:if test="${flows[0].comment==null||flows[0].comment==''}">
							                                   无
							       </c:if>
	                    </p>
	                </div>
	                <div class="right">审批拒绝</div>
               </c:if>
               <c:if test="${statu==400}">
                    <div class="left">
	                    <p><c:if test="${flows[0].finishTime==null}">&nbsp;</c:if><fmt:formatDate value="${flows[0].finishTime}"  pattern="yyyy-MM-dd HH:mm"  /></p>
	                    <p>
	                        <c:if test="${flows[0].assigneeName==null||flows[0].assigneeName==''}">
							      ${flows[0].departName}
							</c:if>
							<c:if test="${flows[0].assigneeName!=null&&flows[0].assigneeName!=''}">
							      ${flows[0].assigneeName}&nbsp;${flows[0].positionName}
							</c:if>
	                    </p>
	                    <p>撤销原因：<c:if test="${flows[0].comment!=null}">
							                                   ${flows[0].comment}
							       </c:if>
							       <c:if test="${flows[0].comment==null||flows[0].comment==''}">
							                                   无
							       </c:if>
	                    </p>
	                </div>
	                <div class="right">撤销申请</div>
               </c:if>
            </div>
            <div class="bottom-main">
                        <ul>
                        <c:forEach var="flow" items="${flows}"> 
	                                          <c:if test="${flow.statu==null}">
	                                                <li>
							                            <rou></rou>
							                            <div class="r">
							                                <div class="p1">
							                                 <c:if test="${flow.assigneeName==null||flow.assigneeName==''}">
							                                     ${flow.departName}
							                                  </c:if>
							                                  <c:if test="${flow.assigneeName!=null&&flow.assigneeName!=''}">
							                                     ${flow.assigneeName}&nbsp;${flow.positionName}
							                                  </c:if>
							                                </div>
							                                <div class="p3 gren">待审批</div>
							                            </div>
	                                                 </li>
	                                          </c:if>
	                                            <c:if test="${flow.statu==200}">
	                                                  <li>
	                                                     <tab class="gren">流程结束<san></san></tab>
	                                                        <div class="r">
	                                                           <div class="p1"><c:if test="${flow.finishTime==null}">&nbsp;</c:if><fmt:formatDate value="${flow.finishTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
	                                                           <div class="p2">${flow.assigneeName}&nbsp;${flow.positionName}</div>
	                                                           <div class="p3 gren">审批通过</div>
	                                                           <div class="p4"><i>审批意见:</i><c:if test="${flow.comment!=null}">
							                                   ${flow.comment}
							                                </c:if>
							                                <c:if test="${flow.comment==null||flow.comment==''}">
							                                   无
							                                </c:if></div>
	                                                        </div>
	                                                  </li>
                                                </c:if>
                                                <c:if test="${flow.statu==100}">
	                                                <li>
							                            <rou></rou>
							                            <div class="r">
							                                <div class="p1"><c:if test="${flow.finishTime==null}">&nbsp;</c:if><fmt:formatDate value="${flow.finishTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
							                                <div class="p2">${flow.assigneeName}&nbsp;${flow.positionName}</div>
							                                <div class="p3 gren">审批通过</div>
							                                <div class="p4"><i>审批意见:</i>
							                                <c:if test="${flow.comment!=null}">
							                                   ${flow.comment}
							                                </c:if>
							                                <c:if test="${flow.comment==null||flow.comment==''}">
							                                   无
							                                </c:if>
							                                </div>
							                            </div>
	                                                 </li>
                                                </c:if>
	                                          <c:if test="${flow.statu==300}">
	                                             <li>
                                                      <tab class="gren">流程结束<san></san></tab>
					                                  <div class="r">
					                                      <div class="p1"><c:if test="${flow.finishTime==null}">&nbsp;</c:if><fmt:formatDate value="${flow.finishTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
					                                      <div class="p2">${flow.assigneeName}&nbsp;${item.positionNameName}</div>
					                                      <div class="p3 red">审批拒绝</div>
					                                      <div class="p4"><i>审批意见:</i>
					                                        <c:if test="${flow.comment!=null}">
							                                   ${flow.comment}
							                                </c:if>
							                                <c:if test="${flow.comment==null||flow.comment==''}">
							                                   无
							                                </c:if>
							                             </div>
					                                  </div>
                                                  </li>
	                                          </c:if>
	                                          <c:if test="${flow.statu==400}">
                                                  <li>
                                                      <tab class="gren">流程结束<san></san></tab>
					                                  <div class="r">
					                                      <div class="p1"><c:if test="${flow.finishTime==null}">&nbsp;</c:if><fmt:formatDate value="${flow.finishTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
					                                      <div class="p2">${flow.assigneeName}&nbsp;${flow.positionName}</div>
					                                      <div class="p3 gren">撤销</div>
					                                      <div class="p4"><i>撤销原因:</i>
					                                        <c:if test="${flow.comment!=null}">
							                                   ${flow.comment}
							                                </c:if>
							                                <c:if test="${flow.comment==null||flow.comment==''}">
							                                   无
							                                </c:if>
					                                      </div>
					                                  </div>
                                                  </li>
	                                          </c:if>
	                                          <c:if test="${flow.statu==600}">
                                                  <li>
                                                      <tab class="gren">流程结束<san></san></tab>
					                                  <div class="r">
					                                      <div class="p1"><c:if test="${flow.finishTime==null}">&nbsp;</c:if><fmt:formatDate value="${flow.finishTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
					                                      <div class="p2">${flow.assigneeName}&nbsp;${flow.positionName}</div>
					                                      <div class="p3 gren">失效同意</div>
					                                      <div class="p4"><i>同意原因:</i>
					                                        <c:if test="${flow.comment!=null}">
							                                   ${flow.comment}
							                                </c:if>
							                                <c:if test="${flow.comment==null||flow.comment==''}">
							                                   无
							                                </c:if>
					                                      </div>
					                                  </div>
                                                  </li>
	                                          </c:if>
	                                          <c:if test="${flow.statu==700}">
                                                  <li>
                                                      <tab class="gren">流程结束<san></san></tab>
					                                  <div class="r">
					                                      <div class="p1"><c:if test="${flow.finishTime==null}">&nbsp;</c:if><fmt:formatDate value="${flow.finishTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
					                                      <div class="p2">${flow.assigneeName}&nbsp;${flow.positionName}</div>
					                                      <div class="p3 gren">失效拒绝</div>
					                                      <div class="p4"><i>拒绝原因:</i>
					                                        <c:if test="${flow.comment!=null}">
							                                   ${flow.comment}
							                                </c:if>
							                                <c:if test="${flow.comment==null||flow.comment==''}">
							                                   无
							                                </c:if>
					                                      </div>
					                                  </div>
                                                  </li>
	                                          </c:if>
                                          	<c:if test="${flow.statu==0}">
	                                              <li>
                                                     <tab class="blue">提出申请<san></san></tab>
                                                        <div class="r">
                                                           <div class="p1"><c:if test="${flow.finishTime==null}">&nbsp;</c:if><fmt:formatDate value="${flow.finishTime}"  pattern="yyyy-MM-dd HH:mm"  /></div>
                                                           <div class="p2">${flow.assigneeName}&nbsp;${flow.positionName}</div>
                                                           <div class="p3 blue">发起申请</div>
                                                           <div class="p4"><i>申请理由:</i>
                                                           <c:if test="${flow.comment==null||flow.comment==''}">
							                                   无
							                                </c:if>
                                                            <c:if test="${flow.comment!=null&&flow.comment!=''}">
							           ${flow.comment}
							                                </c:if>
                                                           </div>
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