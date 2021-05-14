<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>销假申请</title>
<script type="text/javascript" src="<%=basePath%>js/base/empApplicationLeaveBack/index.js?v=20202025"></script>
</head>
<body class="b-f2f2f2 mt-44 pb-54">
    <div class="index-kaoqin">
        <header>
            <h1 class="clearfix"><a href="<%=basePath%>${returnUrl}" class="goback fl"><i class="back sr"></i></a>销假申请<a href="#" class="save fr"></a></h1>
        </header>
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
                    <div class="blk-h">原假期信息</div>
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
	                                <p>${leave.duration}天</p>
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
		                                   年假
									   </c:if>
									   <c:if test="${item.leaveType==2}">
		                                   病假
									   </c:if> 
									   <c:if test="${item.leaveType==5}">
		                                    调休${item.leaveHours}小时
									   </c:if> 
									   <c:if test="${item.leaveType==11}">
		                                    事假
									   </c:if> 
									   <c:if test="${item.leaveType==12}">
		                                   其他
									   </c:if>  
	                                </c:forEach>
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
                    <div class="blk-h">销假时间</div>
                    <section class="edit-info">
                    <p class="notice" style="text-align: left; font-size: 16px; padding-left: 16px; padding-right: 16px;">只需选择销假开始时间,表示取消从该天开始至原假期结束时间的假期申请</p>
                	<p class="notice" style="text-align: left; font-size: 16px;" ></p>
                    </section>
		            <div class="selMatter clearfix">
		                <h4 class="fl">开始时间</h4>
		                <div class="selarea fr">
 							<input type="text" readonly placeholder="请选择日期" id="start_time" value="" data-lcalendar="<fmt:formatDate value="${leave.startTime}"  pattern="yyyy-MM-dd"  />,<fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd"/>">						
 						<i class="sr icon"></i>
		                </div>
		            </div>
		            <div class="selMatter clearfix">
		                <h4 class="fl">结束时间</h4>
		                <div id="end_time" class="selarea fr">
		                  <c:if test="${detailList[0].leaveType==1}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
                          <c:if test="${detailList[0].leaveType==2}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
                          <c:if test="${detailList[0].leaveType==3}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
                          <c:if test="${detailList[0].leaveType==4}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
                          <c:if test="${detailList[0].leaveType==5}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
                          <c:if test="${detailList[0].leaveType==6}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
                          <c:if test="${detailList[0].leaveType==7}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
                          <c:if test="${detailList[0].leaveType==8}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
                          <c:if test="${detailList[0].leaveType==9}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
                          <c:if test="${detailList[0].leaveType==10}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd"  /></c:if>
                          <c:if test="${detailList[0].leaveType==11}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
                          <c:if test="${detailList[0].leaveType==12}"><fmt:formatDate value="${leave.endTime}"  pattern="yyyy-MM-dd HH:mm"  /></c:if>
		                </div>
		            </div>
		             <div class="selMatter clearfix">
		                <h4 class="fl">时长</h4>
		                <div id="back_leaveDays" class="selarea fr">
		                   
		                </div>
		            </div>
                </li>
            </ul>
        </div>
        <div class="oa-late-work-btn" leaveId="${leave.id}" onclick="save(this);">提交</div>
    </div>  
    <c:if test="${leave.startTime!=null}">
	    <input type="hidden" id="startTimeBefor" value="<fmt:formatDate  value="${leave.startTime}"  pattern="yyyy-MM-dd HH:mm:ss"  />"/>
	    <input type="hidden" id="endTimeBefor" value="<fmt:formatDate  value="${leave.endTime}"  pattern="yyyy-MM-dd HH:mm:ss"  />"/>
    </c:if>
    
    <c:if test="${detailList[0].startTime!=null}">
    	<input type="hidden" id="startTimeBefor" value="<fmt:formatDate  value="${detailList[0].startTime}"  pattern="yyyy-MM-dd" />"/>
	    <input type="hidden" id="endTimeBefor" value="<fmt:formatDate  value="${detailList[0].endTime}"  pattern="yyyy-MM-dd" />"/>  	
    </c:if>
    
    <input type="hidden" id="token" name="token" value="${token}"/>
    <input type="hidden" id="leaveType" name="leaveType" value="${detailList[0].leaveType}"/>
    <input type="hidden" id="leaveDays" name="leaveDays" value="${leave.duration}"/>
    <input type="hidden" id="leaveHours" name="leaveHours" value="${detailList[0].leaveHours}"/>
    <input type="hidden" id="leaveApplicationId" name="leaveApplicationId" value="${leave.id}"/>
</body>
</html>