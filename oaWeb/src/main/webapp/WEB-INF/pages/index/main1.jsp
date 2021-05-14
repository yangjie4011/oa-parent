<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>OA—主页</title>
</head>
<body>
	<marquee>欢迎光顾主页</marquee>
	
	<a href="<c:url value="/login/logout.htm"/>">退出系统</a><br/>
	<a href="<c:url value="/companyConfig/index.htm"/>">公司配置表</a><br/>
	<a href="<c:url value="/sysConfig/index.htm"/>">系统配置表</a><br/>
	<a href="<c:url value="/depart/index.htm"/>">部门管理</a><br/>
	<a href="<c:url value="/companyPositionLevel/index.htm"/>">职位等级</a><br/>
	<a href="<c:url value="/companyPositionSeq/index.htm"/>">职位序列</a><br/>
	<a href="<c:url value="/position/index.htm"/>">职位管理</a><br/>
	<a href="<c:url value="/employee/index.htm"/>">在职员工管理</a><br/>
	<a href="<c:url value="/schedule/index.htm"/>">日程管理</a><br/>
	<a href="<c:url value="/ruProcdef/index.htm"/>">我的协作</a><br/>
	<a href="<c:url value="/employeeRegister/toRegister.htm"/>">新员工入职登记</a><br/>
	<a href="<c:url value="/employeeRegister/toHandle.htm?id=7&type=1"/>">新员工入职登记行政处理</a><br/>
	<a href="<c:url value="/employeeRegister/toHandle.htm?id=7&type=2"/>">新员工入职登记it处理</a><br/>
	<a href="<c:url value="/employeeRegister/toHandle.htm?id=7&type=3"/>">新员工入职登记人事处理</a><br/>
	<a href="<c:url value="/empResign/toSubmit.htm"/>">离职申请提交</a><br/>
	<a href="<c:url value="/empResign/toHandle.htm?id=1"/>">离职申请处理</a><br/>
	<a href="<c:url value="/depart/orgChartIndex.htm"/>">组织机构图</a><br/>
	<a href="<c:url value="/handoverWork/index.htm?empResignId=1"/>">离职工作交接</a><br/>
	<a href="<c:url value="/depart/orgEmpChartIndex.htm"/>">部门汇报关系图</a><br/>
	<a href="<%=basePath %>/runTask/start.htm?code=20"/>启动流程</a><br/>
	<a href="<%=basePath %>/runTask/view.htm?ruTaskId=10"/>查看流程</a><br/>
	<a href="<%=basePath %>/runTask/edit.htm?ruProcdefId=19"/>处理流程</a><br/>
	<a href="<c:url value="/handoverWork/handover.htm?empResignId=1&type=1"/>">工作交接</a><br/>
	<a href="<c:url value="/handoverWork/handover.htm?empResignId=1&type=2"/>">内容验收</a><br/>
	<a href="<c:url value="/handoverWork/handover.htm?empResignId=1&type=3"/>">确认审批</a><br/>
	<a href="<c:url value="/handoverWork/checkList.htm?empResignId=4"/>">离职手续办理</a><br/>
	<a href="<c:url value="/handoverWork/checkListToDepart.htm?empResignId=4&type=1"/>">分配各部门</a><br/>
	<a href="<c:url value="/schedulePlan/index.htm"/>">班次设置</a><br/>
	<a href="<c:url value="/schedulePlan/shift.htm"/>">员工排班</a><br/>
	<a href="<c:url value="/runTask/index.htm"/>">我的申请</a><br/>
	<a href="<c:url value="/reProcdef/index.htm"/>">新增申请</a><br/>
	<a href="<c:url value="/empMsg/index.htm"/>">我的消息</a><br/>
	<a href="<c:url value="/empApplicationOutgoing/index.htm"/>">外出申请</a><br/>
	<a href="<c:url value="/empApplicationOutgoing/approval.htm?outgoingId=1"/>">外出申请审批</a><br/>
	<a href="<c:url value="/empApplicationOvertime/index.htm"/>">延时工作申请</a><br/>
	<a href="<c:url value="/empApplicationOvertime/approval.htm?overtimeId=1"/>">延时工作审批</a><br/>
	<a href="<c:url value="/empApplicationBusiness/index.htm"/>">出差申请</a><br/>
	<a href="<c:url value="/empApplicationBusiness/approval.htm?businessId=4"/>">出差审批</a><br/>
	<a href="<c:url value="/agentSetting/index.htm"/>">流程代理</a><br/>
	<a href="<c:url value="/empApplicationLeave/index.htm"/>">请假申请</a><br/>
	<a href="<c:url value="/empApplicationLeave/approval.htm?leaveId=2"/>">请假审批</a><br/>
	<a href="<c:url value="/empApplicationAbnormalAttendance/index.htm"/>">考勤异常消除申请</a><br/>
	<a href="<c:url value="/empApplicationAbnormalAttendance/approval.htm?attendanceId=1"/>">考勤异常消除审批</a><br/>
</body>
</html>