<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>请假审批</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationLeave/approve.js?v=20180417"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>请假审批<a class="toproblem fr"></a></h1>
        </header>
        <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${leave.processInstanceId}&statu=${leave.approvalStatus}&title=请假审批">
                <div class="img"></div>
                <div class="p1">
                 <c:if test="${leave.approvalStatus==100}">
                         处理中，等待<span>[${actName}]</span>审批
                    </c:if>
                    <c:if test="${leave.approvalStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${leave.approvalStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${leave.approvalStatus==400}">
                         已撤销
                     </c:if>
                     <c:if test="${leave.approvalStatus==500}">
                         已失效
                     </c:if>
                      <c:if test="${leave.approvalStatus==600}">
                         失效同意
                     </c:if>
                      <c:if test="${leave.approvalStatus==700}">
                         失效拒绝
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${leave.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
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
                                <p>${leave.cnName}</p>&nbsp;<a onclick="showLeaveDetail();" style="font-size:75%;color:#24b4f5;" href="#">假期详情</a>
                            </div>
                            <div class="font-font">
                                <span>所属部门</span>
                                <p>${leave.departName}</p>
                            </div>
                            <div class="font-font">
                                <span>申请时间</span>
                                <p><fmt:formatDate value="${leave.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  /></p>
                            </div>
                        </div>
                    </div>
                    <div class="blk-h">请假信息</div>
                    <!-- 哺乳假 -->
                    <c:if test="${detailList[0].leaveType==4}">
                        <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>哺乳假</p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${leave.startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假时间</span>
	                                <p><fmt:formatDate value="${detailList[0].startTime}"  pattern="HH:mm"  />-<fmt:formatDate value="${detailList[0].endTime}"  pattern="HH:mm"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>哺乳假小时数</span>
	                                <p>${detailList[0].childrenNum}小时/天</p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                            <div class="font-font">
	                                <span>请假事由</span>
	                                <p>${leave.reason}</p>
	                            </div>
	                        </div>
                        </div>
                    </c:if>
                    <!-- 丧假 -->
                    <c:if test="${detailList[0].leaveType==10}">
                        <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>丧假</p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${leave.startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>亲属关系</span>
	                                <p>
	                                   <c:if test="${detailList[0].relatives==100}">
	                                   父母
	                                   </c:if>
	                                   <c:if test="${detailList[0].relatives==200}">
	                                   配偶
	                                   </c:if>
	                                   <c:if test="${detailList[0].relatives==300}">
	                                 子女  
	                                   </c:if>
	                                   <c:if test="${detailList[0].relatives==400}">
	                          祖父母         
	                                   </c:if>
	                                   <c:if test="${detailList[0].relatives==500}">
	                           外祖父母        
	                                   </c:if>
	                                   <c:if test="${detailList[0].relatives==600}">
	                        兄弟姐妹           
	                                   </c:if>
	                                   <c:if test="${detailList[0].relatives==700}">
	                   配偶父母                
	                                   </c:if>
	                                </p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                            <div class="font-font">
	                                <span>请假事由</span>
	                                <p>${leave.reason}</p>
	                            </div>
	                        </div>
                        </div>
                    </c:if>
                    <!-- 产前假 -->
                    <c:if test="${detailList[0].leaveType==6}">
                        <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>产前假</p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${detailList[0].startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>预产期</span>
	                                <p><fmt:formatDate value="${detailList[0].expectedDate}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束日期</span>
	                                <c:if test="${detailList[0].endTime!=null}">
	                                   <p><fmt:formatDate value="${detailList[0].endTime}"  pattern="yyyy-MM-dd"  /></p>
	                                </c:if>
	                                <c:if test="${detailList[0].endTime==null}">
	                                   <p>&nbsp;</p>
	                                </c:if>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                            <div class="font-font">
	                                <span>请假事由</span>
	                                <p>${leave.reason}</p>
	                            </div>
	                        </div>
                        </div>
                    </c:if>
                    <!-- 产假 -->
                    <c:if test="${detailList[0].leaveType==7}">
                       <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>产假</p>
	                            </div>
                                <div class="font-font">
	                                <span>子女数量</span>
	                                <p>${detailList[0].childrenNum}</p>
                                </div>
                                <div class="font-font">
	                                <span>生产情况</span>
	                                <p>
	                                <c:if test="${detailList[0].birthType==100}">
	                                顺产
	                                </c:if>
	                                <c:if test="${detailList[0].birthType==200}">
	                                难产
	                                </c:if>
	                                </p>
                                </div>
                                 <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${detailList[0].startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${detailList[0].endTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${detailList[0].leaveDays} 
	                                <c:if test="${detailList[0].leaveHours!=null}">
	                                  + ${detailList[0].leaveHours}
	                                </c:if>
	                                	天</p>
                                </div>
	                            <div class="font-font">
	                                <span>请假事由</span>
	                                <p>${leave.reason}</p>
	                            </div>
	                        </div>
                        </div>
                    </c:if>
                    <!-- 流产假 -->
                    <c:if test="${detailList[0].leaveType==8}">
                        <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>流产假</p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${detailList[0].startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${detailList[0].endTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                            <div class="font-font">
	                                <span>请假事由</span>
	                                <p>${leave.reason}</p>
	                            </div>
	                        </div>
                        </div>
                    </c:if>
                    
                    <!-- 婚假 -->
                    <c:if test="${detailList[0].leaveType==3}">
                        <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>婚假</p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${detailList[0].startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${detailList[0].endTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                        </div>
                        </div>
                    </c:if>
                    <!-- 陪产假 -->
                    <c:if test="${detailList[0].leaveType==9}">
                        <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>陪产假</p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${detailList[0].startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${detailList[0].endTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                        </div>
                        </div>
                    </c:if>
                    
                    
                    
                    
                    <c:if test="${detailList[0].leaveType!=4&&detailList[0].leaveType!=6&&detailList[0].leaveType!=7&&detailList[0].leaveType!=8&&detailList[0].leaveType!=10&&detailList[0].leaveType!=3&&detailList[0].leaveType!=9}">
	                    <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>
	                                <c:forEach var="item" items="${detailList}" varStatus="status">
	                                  <c:if test="${item.leaveType==1}">
		                                  <c:if test="${status.last==true}">
		                                   年假${item.leaveDays}天
		                                  </c:if>
		                                  <c:if test="${status.last==false}">
		                                   年假${item.leaveDays}天\
		                                  </c:if>
									   </c:if>
									   <c:if test="${item.leaveType==2}">
										   <c:if test="${status.last==true}">
		                                   病假${item.leaveDays}天
		                                   </c:if>
		                                   <c:if test="${status.last==false}">
		                                   病假${item.leaveDays}天\
		                                   </c:if>
									   </c:if>
									   <c:if test="${item.leaveType==5}">
										   <c:if test="${status.last==true}">
		                                   调休${item.leaveHours}小时
		                                   </c:if>
		                                   <c:if test="${status.last==false}">
		                                   调休${item.leaveHours}小时\
		                                   </c:if>
									   </c:if> 
									   <c:if test="${item.leaveType==11}">
										   <c:if test="${status.last==true}">
		                                     事假${item.leaveDays}天
		                                   </c:if>
		                                   <c:if test="${status.last==false}">
		                                     事假${item.leaveDays}天\
		                                   </c:if>  
									   </c:if> 
									   <c:if test="${item.leaveType==12}">
										   <c:if test="${status.last==true}">
		                                   其他${item.leaveDays}天
		                                   </c:if>
		                                   <c:if test="${status.last==false}">
		                                   其他${item.leaveDays}天\
		                                   </c:if>
									   </c:if>  
	                                </c:forEach>
	                                </p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${leave.startTime}"  pattern="MM-dd HH:mm"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${leave.endTime}"  pattern="MM-dd HH:mm"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>时长</span>
	                                <p>${leave.duration}天</p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假事由</span>
	                                <p>${leave.reason}</p>
	                            </div>
	                        </div>
	                    </div>
                    </c:if>
                    <div class="blk-h">假期联系信息</div>
                    <div class="blk">
                        <div class="pad-tb">
                            <div class="font-font">
                                <span>紧急联系电话</span>
                                <p>${leave.mobile}</p>
                            </div>
                            <div class="font-font">
                                <span>假期代理人</span>
                                <p>${leave.agent}</p>
                            </div>
                            <div class="font-font">
                                <span>代理人联系电话</span>
                                <p>${leave.agentMobile}</p>
                            </div>
                        </div>
                    </div>
                    <input type="hidden" id="token" name="token" value="${token}"/>
                    <input type="hidden" id="nodeCode" name="nodeCode" value="${nodeCode }"/>
                    <input type="hidden" id="ruProcdefId" name="ruProcdefId" value="${ruProcdefId }"/>
                    <c:if test="${canApprove&&!isSelf}">
	                     <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写审批意见（非必填）" cols="30" rows="10"></textarea>
	                     </div>
                         <div class="double-btn">
                           <div id="${leave.processInstanceId}" onclick="refuse(this);" class="l"><em>拒绝</em></div>
                           <div id="${leave.processInstanceId}"  onclick="pass(this);" class="r"><em>同意</em></div>
                         </div>
                   </c:if>
                   <c:if test="${leave.approvalStatus ==100 && isSelf}">
	                   <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
	                     </div>
	                   <div id="${leave.processInstanceId}" onclick="cancelRunTask(this)" class="foot-btn cancel-apply"><em>撤回申请</em></div>
                   </c:if>
                   <c:if test="${leave.approvalStatus ==200 && isSelf}">
                       <div class="b">
                      	 <div id="${leave.id}" onclick="toLeaveBack(this);" class="foot-btn cancel-apply"><em>销假申请</em></div>
                  		</div>
					</c:if> 
                </li>
            </ul>
        </div>
    </div>
    <div class="artical-box-bg" style="display:none;">
        <div class="artical-box">
            <h2>假期详情</h2>
            <div class="artical-module">
                <h3>剩余假期</h3>
                <div class="module-list">
                    <p>
                        <em>
                            <b class="bg-red"></b>年假</em>
                        <span>${allowAnnualLeave}天</span>
                    </p>
                    <p>
                        <em>
                            <b class="bg-red"></b>病假</em>
                        <span>${allowSickLeave}天</span>
                    </p>
                    <p>
                        <em>
                            <b class="bg-red"></b>调休</em>
                        <span>${allowDayOff}小时</span>
                    </p>
                </div>
            </div>
            <div class="artical-module">
                <h3>已用假期</h3>
                <div class="module-list">
                    <p>
                        <em>
                            <b class="bg-blue"></b>年假</em>
                        <span>${usedAnnualLeave}天</span>
                    </p>
                    <p>
                        <em>
                            <b class="bg-blue"></b>病假</em>
                        <span>${usedSickLeave}天</span>
                    </p>
                    <p>
                        <em>
                            <b class="bg-blue"></b>调休</em>
                        <span>${usedDayOff}小时</span>
                    </p>
                    <p>
                        <em>
                            <b class="bg-blue"></b>事假</em>
                        <span>${affairsLeave}天</span>
                    </p>
                </div>
            </div>
            <i class="close" onclick="closeLeaveDetail();"></i>
        </div>
    </div>
</body>
</html>