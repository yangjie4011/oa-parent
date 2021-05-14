<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>假期申请</title>
</head>
<body class="b-f2f2f2 mt-44">
    <div class="index-kaoqin">
        <c:if test="${billType eq 'leave'}">
	        <header>
	         	<h1 class="clearfix"><a href="<%=basePath%>empLeave/leaveRecord.htm" class="goback fl"><i class="back sr"></i></a>请假申请<a class="toproblem fr"></a></h1>
	        </header>
	        <div class="new-head">
	            <a href="#">
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
	                                <p>${leave.cnName}</p>
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
	                </li>
	            </ul>
	        </div>
        </c:if>
        
        
        <c:if test="${billType eq 'cancelLeave'}">
	        <header>
	            <h1 class="clearfix"><a href="<%=basePath%>empLeave/leaveRecord.htm" class="goback fl"><i class="back sr"></i></a>销假申请<a class="toproblem fr"></a></h1>
	        </header>
	        <div class="new-head">
	            <a href="#">
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
	        </div>
        </c:if>
      
    </div>
</body>
</html>