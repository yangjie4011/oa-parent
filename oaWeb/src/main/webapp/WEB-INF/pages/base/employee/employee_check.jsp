<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>查看员工</title>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<c:url value="/js/common/oaCommon.js?v=20170515"/>"></script>
<script type="text/javascript" src="<c:url value="/js/base/employee/employee_check_app.js?v=20200520"/>"></script>
</head>
<body>
	<input type="hidden" id="id" name="id" value="${requestScope.id }"/>

	<div style="margin-left: 30px;margin-top: 10px;margin-bottom: 50px;">
		<!-- 员工信息与成长履历表 -->
		<div>员工信息与成长履历表</div>
		<table>
			<tr>
				<td>
					公司名称：
				</td>
				<td>
					<span id="coopCompanyName"></span>
				</td>
				<td>
					员工编号：
				</td>
				<td>
					<span id="code"></span>
				</td>
			</tr>
			<tr>
				<td>
					部门：
				</td>
				<td>
					<span id="departName"></span>
				</td>
				<td>
					汇报对象：
				</td>
				<td>
					<span id="employeeLeader"></span>
				</td>
			</tr>
			<tr>
				<td>
					部门负责人：
				</td>
				<td>
					<span id="departLeader"></span>
				</td>
				<td>
					职位：
				</td>
				<td>
					<span id="positionName"></span>
				</td>
			</tr>
			<tr>
				<td>
					员工类型：
				</td>
				<td>
					<span id="empType"></span>
				</td>
				<td>
					入职日期：
				</td>
				<td>
					<span id="firstEntryTime"></span>
				</td>
			</tr>
			<tr>
				<td>
					离职日期：
				</td>
				<td>
					<span id="quitTime"></span>
				</td>
			</tr>
		</table>
		
		<!-- 基础信息 -->
		<br/>
		<hr/>
		<br/>
		<div>基础信息&nbsp;&nbsp;</div>
		<table border="1" id="empBaseTable">
			<tr>
				<td>
					中文姓名：
				</td>
				<td style="width: 80px;">
					<span id="cnName"></span>
				</td>
				<td>
					英文名：
				</td>
				<td style="width: 80px;">
					<span id="engName"></span>
				</td>
				<td>
					出生日期：
				</td>
				<td style="width: 120px;">
					<span id="birthday"></span>
				</td>
			</tr>
			<tr>
				<td>
					性别：
				</td>
				<td>
					<span id="sex"></span>
				</td>
				<td>
					年龄：
				</td>
				<td>
					<span id="age"></span>
				</td>
				<td>
					户籍：
				</td>
				<td>
					<span id="householdRegister"></span>
				</td>
			</tr>
			<tr>
				<td>
					政治面貌：
				</td>
				<td>
					<span id="politicalName"></span>
				</td>
				<td>
					文化程度：
				</td>
				<td>
					<span id="degreeOfEducationName"></span>
				</td>
				<td>
					试用期到期日：
				</td>
				<td>
					<span id="probationExpire"></span>
				</td>
			</tr>
			<tr>
				<td>
					职称：
				</td>
				<td>
					<span id="positionTitle"></span>
				</td>
				<td>
					职级：
				</td>
				<td>
					<span id="positionLevelName"></span>
				</td>
				<td>
					职位序列：
				</td>
				<td>
					<span id="positionSeqName"></span>
				</td>
			</tr>
			<tr>
				<td>
					司龄：
				</td>
				<td>
					<span id="ourAge"></span>
				</td>
				<td>
					合同续签次数：
				</td>
				<td>
					<span id="contractRenewal"></span>
				</td>
				<td>
					合同到期日：
				</td>
				<td>
					<span id="contractEndTime"></span>
				</td>
			</tr>
			<tr>
				<td>
					行业相关性：
				</td>
				<td>
					<span id="industryRelevanceName"></span>
				</td>
				<td>
					从业背景：
				</td>
				<td>
					<span id="workingBackground"></span>
				</td>
				<td>
					工作邮箱：
				</td>
				<td>
					<span id="email"></span>
				</td>
			</tr>
			<tr>
				<td>
					婚姻状况：
				</td>
				<td>
					<span id="maritalStatusName"></span>
				</td>
				<td>
					民族：
				</td>
				<td>
					<span id="nationName"></span>
				</td>
				<td>
					邮乐帐号：
				</td>
				<td>
					<span id="uleAccount"></span>
				</td>
			</tr>
			<tr>
				<td colspan="3">
					工时类型：
				</td>
				<td>
					<span id="workTypeName"></span>
				</td>
				<td>
					是否排班：
				</td>
				<td>
					<span id="whetherSchedulingName"></span>
				</td>
			</tr>
			<tr>
				<td>
					在沪居住地址：
				</td>
				<td colspan="3">
					<span id="address"></span>
				</td>
				<td>
					手机：
				</td>
				<td>
					<span id="mobile"></span>
				</td>
			</tr>
		</table>
		
		<!-- 配偶信息 -->
		<br/>
		<div>配偶信息&nbsp;&nbsp;</div>
		<table border="1" id="spouseTable">
			<tr>
				<td style='width: 10px;align:center;' id="spouseHead">配偶&nbsp;&nbsp;</td>
				<td style='width: 120px;align:center;'>姓名&nbsp;&nbsp;</td>
				<td style='width: 250px;align:center;'>工作单位&nbsp;&nbsp;</td>
				<td style='width: 100px;align:center;'>电话&nbsp;&nbsp;</td>
			</tr>
		</table>	
		
		<!-- 子女信息 -->
		<br/>
		<div><span>子女信息&nbsp;&nbsp;</span></div>
		<table border="1" id="childTable">
			<tr>
				<td style='width: 10px;align:center;' id="childHead">子女情况&nbsp;&nbsp;</td>
				<td style='width: 120px;align:center;'>子女姓名&nbsp;&nbsp;</td>
				<td style='width: 130px;align:center;'>出生日期&nbsp;&nbsp;</td>
				<td style='width: 80px;align:center;'>子女性别&nbsp;&nbsp;</td>
			</tr>
		</table>	
		
		<!-- 紧急联系人 -->
		<br/>
		<div>
			<span>紧急联系人&nbsp;&nbsp;</span>
		</div>
		<table border="1"  id="urgentContactTable">
			<tr>
				<td style="width:100px;">联系人&nbsp;&nbsp;</td>
				<td style="width: 80px;">称谓&nbsp;&nbsp;</td>
				<td style="width: 150px;">姓名&nbsp;&nbsp;</td>
				<td style="width: 100px;">电话&nbsp;&nbsp;</td>
			</tr>
		</table>
		
		<!-- 教育经历 -->
		<br/>
		<div><span>教育经历&nbsp;&nbsp;</span></div>
		<table border="1" id="schoolTable">
			<tr>
				<td style="width: 250px;" align="center" colspan="2">起止时间&nbsp;&nbsp;</td>
				<td style="width: 180px;" align="center">学校名称&nbsp;&nbsp;</td>
				<td style="width: 100px;" align="center">专业&nbsp;&nbsp;</td>
				<td style="width: 80px;" align="center">学历&nbsp;&nbsp;</td>
				<td style="width: 80px;" align="center">学位&nbsp;&nbsp;</td>
			</tr>
		</table>
		
		<!-- 培训和证书 -->
		<br/>
		<div><span>培训和证书&nbsp;&nbsp;</span></div>
		<table border="1" id="tranningTable">
			<tr>
				<td style="width: 250px;" align="center" colspan="2">起止时间</td>
				<td style="width: 180px;" align="center">培训机构</td>
				<td style="width: 180px;" align="center">培训内容</td>
				<td style="width: 100px;" align="center">所获证书</td>
			</tr>
		</table>
		
		<!-- 工作经历 -->
		<br/>
		<div><span>工作经历&nbsp;&nbsp;</span></div>
		<table border = 1 id="workTable">
			<tr>
				<td style="width: 250px;" align="center" colspan="2">起止时间&nbsp;&nbsp;</td>
				<td style="width: 180px;" align="center">单位名称&nbsp;&nbsp;</td>
				<td style="width: 100px;" align="center">职位&nbsp;&nbsp;</td>
				<td style="width: 120px;" align="center">职称&nbsp;&nbsp;</td>
				<td style="width: 120px;" align="center">主办业务&nbsp;&nbsp;</td>
			</tr>
		</table>
	</div>
</body>
</html>