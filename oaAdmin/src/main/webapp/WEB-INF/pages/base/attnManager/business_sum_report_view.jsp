<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>出差总结报告-查看页面</title>
<%@include file="../../common/common2.jsp"%>
</head>

<body>
	<div class="content">
		<div class='col'>
			<div class='form'>
				<div>
					<label class='w'>申请人</label>
					${business.cnName }
				</div>
				<div>
					<label class='w'>所属部门</label>
					${business.departName }
				</div>
				<div>
					<label class='w'>申请日期</label>
					<fmt:formatDate value="${business.submitDate}"  pattern="yyyy-MM-dd HH:mm:ss"  />
				</div>
			</div>
		</div>
		<hr/>
		
		<div class='col'>
			<div class='form'>
				<div>
					<label class='w'>出差日期</label>
					<fmt:formatDate value="${business.startTime}"  pattern="yyyy-MM-dd HH:mm:ss"  />
					&nbsp;&nbsp;至&nbsp;&nbsp;
					<fmt:formatDate value="${business.endTime}"  pattern="yyyy-MM-dd HH:mm:ss"  />
				</div>
				<div>
					<label class='w'>出差时长</label>
					${business.duration}天
				</div>
				<div>
					<label class='w'>出差地点</label>
					上海&nbsp;至&nbsp;${business.address}
				</div>
				<div>
					<label class='w'>出差事由</label>
					${business.reason}
				</div>
				<div>
					<label class='w'>交通工具</label>
					<c:choose>
						<c:when test="${business.vehicle==100}">
							 火车 
						</c:when>
						<c:when test="${business.vehicle==200}">
							 飞机 
						</c:when>
						<c:when test="${business.vehicle==300}">
							 汽车
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
		<hr/>
		
		<div class='col'>
			<div class='form'>
				<div>
					<font class='w'>每日行程及工作完成情况</font>
				</div>
				<c:forEach var="item" items="${detailList}" varStatus="vs"> 
				<div>
					<label class='w'><fmt:formatDate value="${item.businessDate}"  pattern="yyyy-MM-dd"  /></label>
               		${item.workSummary}
				</div>
				</c:forEach>
			</div>
		</div>
		<hr/>
		
		<div class='col'>
			<div class='form'>
				<font class='w'>流程进程</font>
				<table>
					<tr>
						<td>流程名称</td>
						<td>审批时间</td>
						<td>审批状态</td>
						<td>操作人</td>
					</tr>
					<c:forEach items="${hiActinstList }" var="hia" varStatus="vs">
						<tr>
							<td>
								
							<c:choose>
								<c:when test="${hia.taskId=='START'}">填写出差总结报告</c:when>
								<c:when test="${hia.taskId=='RESIGN_DH'}">部门负责人审批</c:when>
								<c:when test="${hia.taskId=='VEP_1'}">分管副总裁审批</c:when>
								<c:when test="${hia.taskId=='VEP_2'}">分管副总裁审批</c:when>
								<c:when test="${hia.taskId=='RESIGN_COO'}">COO审批</c:when>
								<c:otherwise>
									${hia.positionName} 待审批
								</c:otherwise>
							</c:choose>
							</td>
							<td><fmt:formatDate value="${hia.finishTime}"  pattern="yyyy-MM-dd HH:mm:ss"  /></td>							
							<td>
							<c:choose>
								<c:when test="${hia.statu==0}">申请中</c:when>
								<c:when test="${hia.statu==100}">审批中</c:when>
								<c:when test="${hia.statu==200}">审批通过</c:when>
								<c:when test="${hia.statu==300}">审批拒绝</c:when>
								<c:when test="${hia.statu==400}">申请撤回</c:when>
								<c:otherwise>
									待审批
								</c:otherwise>
							</c:choose>
							</td>
							<td>${hia.assigneeName}</td>
						</tr>
						
					</c:forEach>
				</table>
			</div>
		</div>
		
		<div class="col">  
            <div class="button-wrap ml-4">
                <button id="query" class="red-but" onclick="javascript:history.go(-1);"><span><i class="icon search"></i>返回</span></button> 
            </div> 
        </div> 
    </div>
</body>
</html>