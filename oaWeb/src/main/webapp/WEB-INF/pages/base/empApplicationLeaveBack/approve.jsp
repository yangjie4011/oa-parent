<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>销假审批</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationLeave/approve.js?v=20180416"></script>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>销假审批<a class="toproblem fr"></a></h1>
        </header>
        <div class="new-head">
            <a href="<%=basePath%>ruProcdef/viewTaskFlow.htm?instanceId=${leaveAbolish.processInstanceId}&statu=${leaveAbolish.approvalStatus}">
                <div class="img"></div>
                <div class="p1">
                 <c:if test="${leaveAbolish.approvalStatus==100}">
                         处理中，等待<span>[${actName}]</span>审批
                    </c:if>
                    <c:if test="${leaveAbolish.approvalStatus==200}">
                         已完成
                     </c:if>
                     <c:if test="${leaveAbolish.approvalStatus==300}">
                         已拒绝
                     </c:if>
                     <c:if test="${leaveAbolish.approvalStatus==400}">
                         已撤销
                     </c:if>
                     <c:if test="${leaveAbolish.approvalStatus==500}">
                         已失效
                     </c:if>
                     <c:if test="${leaveAbolish.approvalStatus==600}">
         失效同意
                     </c:if>
                     <c:if test="${leaveAbolish.approvalStatus==700}">
         失效拒绝
                     </c:if>
                </div>
                <div class="p3"><fmt:formatDate value="${leaveAbolish.createTime}"  pattern="yyyy-MM-dd HH:mm:ss"  /></div>
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
                                <p>${leave.cnName}</p>
                            </div>
                            <div class="font-font">
                                <span>所属部门</span>
                                <p>${leave.departName}</p>
                            </div>
                            <div class="font-font">
                                <span>申请时间</span>
                                <p><fmt:formatDate value="${leaveAbolish.createTime}"  pattern="yyyy-MM-dd HH:mm:ss"  /></p>
                            </div>
                        </div>
                    </div>
                    <div class="blk-h">原假期信息</div>
                    <!-- 哺乳假 -->
                    <c:if test="${leaveDetail.leaveType==4}">
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
	                                <p><fmt:formatDate value="${leaveDetail.startTime}"  pattern="HH:mm"  />-<fmt:formatDate value="${leaveDetail.endTime}"  pattern="HH:mm"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>哺乳假小时数</span>
	                                <p>${leaveDetail.childrenNum}小时/天</p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                        </div>
                        </div>
                    </c:if>
                    <!-- 丧假 -->
                    <c:if test="${leaveDetail.leaveType==10}">
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
	                                   <c:if test="${leaveDetail.relatives==100}">
	                                   父母
	                                   </c:if>
	                                   <c:if test="${leaveDetail.relatives==200}">
	                                   配偶
	                                   </c:if>
	                                   <c:if test="${leaveDetail.relatives==300}">
	                                 子女  
	                                   </c:if>
	                                   <c:if test="${leaveDetail.relatives==400}">
	                          祖父母         
	                                   </c:if>
	                                   <c:if test="${leaveDetail.relatives==500}">
	                           外祖父母        
	                                   </c:if>
	                                   <c:if test="${leaveDetail.relatives==600}">
	                        兄弟姐妹           
	                                   </c:if>
	                                   <c:if test="${leaveDetail.relatives==700}">
	                   配偶父母                
	                                   </c:if>
	                                </p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                        </div>
                        </div>
                    </c:if>
                    <!-- 产前假 -->
                    <c:if test="${leaveDetail.leaveType==6}">
                        <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>产前假</p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>预产期</span>
	                                <p><fmt:formatDate value="${leaveDetail.expectedDate}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束日期</span>
	                                <c:if test="${leaveDetail.endTime!=null}">
	                                   <p><fmt:formatDate value="${leaveDetail.endTime}"  pattern="yyyy-MM-dd"  /></p>
	                                </c:if>
	                                <c:if test="${leaveDetail.endTime==null}">
	                                   <p>&nbsp;</p>
	                                </c:if>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                        </div>
                        </div>
                    </c:if>
                    <!-- 产假 -->
                    <c:if test="${leaveDetail.leaveType==7}">
                       <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>产假</p>
	                            </div>
                                <div class="font-font">
	                                <span>子女数量</span>
	                                <p>${leaveDetail.childrenNum}</p>
                                </div>
                                <div class="font-font">
	                                <span>生产情况</span>
	                                <p>
	                                <c:if test="${leaveDetail.birthType==100}">
	                                顺产
	                                </c:if>
	                                <c:if test="${leaveDetail.birthType==200}">
	                                难产
	                                </c:if>
	                                </p>
                                </div>
                                 <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${leaveDetail.endTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                        </div>
                        </div>
                    </c:if>
                    <!-- 流产假 -->
                    <c:if test="${leaveDetail.leaveType==8}">
                        <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>流产假</p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${leaveDetail.endTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                        </div>
                        </div>
                    </c:if>
                    
                     <!-- 婚假 -->
                    <c:if test="${leaveDetail.leaveType==3}">
                        <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>婚假</p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${leaveDetail.endTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                        </div>
                        </div>
                    </c:if>
                    <!-- 陪产假 -->
                    <c:if test="${leaveDetail.leaveType==9}">
                        <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>陪产假</p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${leaveDetail.endTime}"  pattern="yyyy-MM-dd"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>请假天数</span>
	                                <p>${leave.duration}天</p>
                                </div>
	                        </div>
                        </div>
                    </c:if>
                    
                    <c:if test="${leaveDetail.leaveType!=4&&leaveDetail.leaveType!=6&&leaveDetail.leaveType!=7&&leaveDetail.leaveType!=8&&leaveDetail.leaveType!=10&&leaveDetail.leaveType!=3&&leaveDetail.leaveType!=9}">
	                    <div class="blk">
	                        <div class="pad-tb">
	                            <div class="font-font">
	                                <span>假期类型</span>
	                                <p>
	                                  <c:if test="${leaveDetail.leaveType==1}">
		                                   年假
									   </c:if>
									   <c:if test="${leaveDetail.leaveType==2}">
		                                   病假
									   </c:if>
									   <c:if test="${leaveDetail.leaveType==5}">
		                                   调休
									   </c:if>
									   <c:if test="${leaveDetail.leaveType==11}">
		                                    事假
									   </c:if> 
									   <c:if test="${leaveDetail.leaveType==12}">
		                                   其他
									   </c:if>  
	                                </p>
	                            </div>
	                            <div class="font-font">
	                                <span>开始时间</span>
	                                <p><fmt:formatDate value="${leave.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>结束时间</span>
	                                <p><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></p>
	                            </div>
	                            <div class="font-font">
	                                <span>时长</span>
	                                <p>${leave.duration}天</p>
	                            </div>
	                        </div>
	                    </div>
                    </c:if>
                  	<div class="blk-h">  销假时间</div>
                    <div class="blk">
	                    <div class="font-font">
	                        <span>开始时间</span>
			                   <div id="end_time" >
				                  <c:if test="${leaveDetail.leaveType==1}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==2}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==3}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==4}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==5}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==6}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==7}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==8}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==9}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==10}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==11}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==12}"><fmt:formatDate value="${leaveAbolish.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
			                  </div>
	                    </div>
	                   <div class="font-font">
	                        <span>结束时间</span>
				                <div id="end_time" class="selarea fr">
				                  <c:if test="${leaveDetail.leaveType==1}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==2}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==3}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==4}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==5}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==6}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==7}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==8}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==9}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==10}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==11}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
		                          <c:if test="${leaveDetail.leaveType==12}"><fmt:formatDate value="${leaveAbolish.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
				                </div>
		              	</div>
	                  	<div class="font-font">
	                        <span>时长</span>
		                <div id="back_leaveDays" class="selarea fr">
		                
		                   ${leaveAbolish.leaveDays}天<c:if test="${leaveDetail.leaveType==5}">	${leaveAbolish.leaveHours}小时</c:if>
		                </div>
		            </div>
		            </div>
		           <c:if test="${leave.duration-leaveAbolish.leaveDays > 0}">
                   <div class="blk-h">销假后假期起止时间</div>
                    <div class="blk">
                        <div class="pad-tb">
                            <div class="font-font">
                                <span>开始时间</span>
                                <p id="actual_start_time">
                                    <c:if test="${leaveDetail.leaveType==1}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==2}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==3}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==4}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==5}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==6}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==7}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==8}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==9}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==10}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==11}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
                                    <c:if test="${leaveDetail.leaveType==12}"><fmt:formatDate value="${leaveDetail.startTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
                                </p>
                            </div>
                            <div class="font-font">
                                <span>结束时间</span>
                                <p id="actual_end_time">
                                     ${actualEndTime}
	                            </p>
                            </div>
                            <div class="font-font">
                                <span>时长</span>
                                <p id="actual_leaveDays">
                                	${leave.duration-leaveAbolish.leaveDays}天	<c:if test="${leaveDetail.leaveType==5}">${leaveDetail.leaveHours-leaveAbolish.leaveHours}小时</c:if>
                                	
                                </p>
                 
                            </div>
                        </div>
                    </div>
		           </c:if>
                    
                </li>
            </ul>
            
            <c:if test="${canApprove&&!isSelf}">
	                     <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写审批意见（非必填）" cols="30" rows="10"></textarea>
	                     </div>
                         <div class="double-btn">
                           <div id="${leaveAbolish.processInstanceId}" onclick="refuse(this);" class="l"><em>拒绝</em></div>
                           <div id="${leaveAbolish.processInstanceId}"  onclick="pass(this);" class="r"><em>同意</em></div>
                         </div>
                   </c:if>
             <c:if test="${leaveAbolish.approvalStatus ==100 && isSelf}">
	                   <div class="b">
	                        <textarea id="approvalReason" name="approvalReason" placeholder="请填写撤回原因（非必填）" cols="30" rows="10"></textarea>
	                     </div>
	                   <div id="${leaveAbolish.processInstanceId}" onclick="cancelRunTask(this)" class="foot-btn cancel-apply"><em>撤回申请</em></div>
              </c:if>
            
        </div>
    </div>
    <input type="hidden" id="token" name="token" value="${token}"/>
</body>
</html>