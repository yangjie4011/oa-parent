<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>员工履历</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/emp/employee_record_scan.js?v=20191201"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
</style>
</head>
<body>
	<div class="content" style="overflow-x:auto">
			
        <div style="height:50px;">
             <div style="float:left;width:75%;">
                 <h2 align="center" style="font-size: 25px;">员工履历表</h2>
             </div>
	         <div style="float:left;width:25%;">
	              <button onclick="downLoad();" style="width:60px;" class="btn-blue btn-large"><span><i class="icon"></i>下载</span></button> 
	              <button onclick="toEditHtm();" style="width:60px;" class="btn-green btn-large"><span><i class="icon"></i>编辑</span></button>
	              <button onclick="toIndexHtm();" style="width:60px;" class="btn-green btn-large"><span><i class="icon"></i>返回</span></button>      
	         </div>
        </div>
        <input type="hidden" id="employeeId" value="${trackInfo.employee.id }">
        <div style="height: 1px;width:100%; border-top: solid #ACC0D8 1px;"></div>
        
        <div style="margin-left:20px;width:95%;margin-top:10px;">
	        <div style="float:left;width:75%;">
		        <div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">公司名称：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.company.name }">
		            </div>                                                  
				</div>
				
			    <div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">员工姓名：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.cnName }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">员工编号：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.code }">
		            </div>                                                  
				</div>
				
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">部门：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.depart.name }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">职位：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.position.positionName }">
		            </div>                                                  
				</div>
				
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">汇报对象：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.reportToLeader.cnName }<c:if test="${empty trackInfo.reportToLeader.cnName}">无</c:if>">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">部门负责人：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.departLeader.cnName }<c:if test="${empty trackInfo.departLeader.cnName}">无</c:if>">
		            </div>                                                  
				</div>
				
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">员工类型：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.empType.typeCName }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">在职状态：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:if test="${trackInfo.employee.jobStatus==0}">在职</c:if><c:if test="${trackInfo.employee.jobStatus==1}">离职</c:if><c:if test="${trackInfo.employee.jobStatus==2}">待离职</c:if>">
		            </div>                                                  
				</div>
				
	        </div>
	        <div style="float:left;width:20%;">
	            <img style="width:150px;height:200px;" src="${trackInfo.employee.photo }" />
	        </div>
        </div>
      
        
        <div style="margin-left:20px;width:70%;text-algin:center;">
	        <div>
	        	<div class="datatitle">
					<ul class="fl jui-tabswitch">
						<li id="listTab" rel="#old" class="active"><strong>基本信息</strong></li>		
					</ul>
				</div>
				</br>
			    <div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">性别：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:if test="${trackInfo.employee.sex==0}">男</c:if><c:if test="${trackInfo.employee.sex==1}">女</c:if>">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">婚姻状况：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.config.maritalStatus}" varStatus="status"><c:if test="${trackInfo.employee.maritalStatus == item.id}">${item.displayName }</c:if></c:forEach>">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">国籍：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.comConfig.country}" varStatus="status"><c:if test="${trackInfo.employee.country == item.id}">${item.displayName }</c:if></c:forEach>">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">民族：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.config.nation}" varStatus="status"><c:if test="${trackInfo.employee.nation == item.id}">${item.displayName }</c:if> </c:forEach>">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">出生日期：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<fmt:formatDate value="${trackInfo.employee.birthday }"  pattern="yyyy-MM-dd"  />">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">年龄：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.age }">
		            </div>   
		            <div class="form">
		            	<label class="w" style="width:150px;">员工识别号：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.identificationNum }">
		            </div>                                               
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">政治面貌：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.config.politicalStatus}" varStatus="status"><c:if test="${trackInfo.employee.politicalStatus == item.id}">${item.displayName }</c:if>  </c:forEach>">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">文化程度：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.config.educationLevel}" varStatus="status"><c:if test="${trackInfo.employee.degreeOfEducation == item.id}"> ${item.displayName }</c:if></c:forEach>">
		            </div>                                                  
				</div>  
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">户籍：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.householdRegister }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">在沪居住地：</label>
						<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.address }"> 
		            </div>                                              
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">行业相关性：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.comConfig.industryCorrelation}" varStatus="status"><c:if test="${trackInfo.employee.industryRelevance == item.id}">${item.displayName }</c:if></c:forEach>">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">从业背景：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.workingBackground }">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">手机号码：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.mobile }">
		            </div>                                         
				</div>   
	        </div>
	        <div>
		        <div class="datatitle">
					<ul class="fl jui-tabswitch">
						<li id="listTab" rel="#old" class="active"><strong>在职信息</strong></li>		
					</ul>
				</div> 
				</br>
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">公司名称：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.companyList}" varStatus="status"><c:if test="${trackInfo.employee.companyId == item.id}">${item.name }</c:if></c:forEach>">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">员工编号：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.code }">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">员工类型：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.empTypeList}" varStatus="status"><c:if test="${trackInfo.employee.empTypeId == item.id}">${item.typeCName }</c:if> </c:forEach>">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">工时种类：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.comConfig.typeOfWork}" varStatus="status"><c:if test="${trackInfo.employee.workType == item.id}">${item.displayName }</c:if> </c:forEach>">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">楼层：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.floorList}" varStatus="status"><c:if test="${trackInfo.employee.floorCode == item.floorNum}">${item.name }</c:if></c:forEach>">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">座位号：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.seatCode }">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">通行证：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.username }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">邮箱：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.email }">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">部门：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.departList}" varStatus="status"><c:if test="${trackInfo.depart.id == item.id}">${item.name }</c:if></c:forEach>">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">汇报对象：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.reportToLeaderList}" varStatus="status"><c:if test="${trackInfo.employee.reportToLeader == item.id}">${item.cnName }</c:if></c:forEach>">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">部门负责人：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.departLeader.cnName }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">职位：</label>
	                    <input type="text" class="form-text" style="border:0px;"  value="${trackInfo.position.positionName }">
		            </div>                                                  
				</div> 
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">职级：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.positionLevel }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">职位序列：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.positionSeq }">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">职称：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.positionTitle }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">是否排班：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:forEach var="item" items="${trackInfo.config.whetherScheduling}" varStatus="status"><c:if test="${trackInfo.employee.whetherScheduling == item.id}">${item.displayName }</c:if> </c:forEach>">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">入职时间：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<fmt:formatDate value="${trackInfo.employee.firstEntryTime }"  pattern="yyyy-MM-dd"  />">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">试用期到期日：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<fmt:formatDate value="${trackInfo.employee.probationEndTime }"  pattern="yyyy-MM-dd"  />">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">司龄：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.ourAge }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">入司前工龄：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.beforeWorkAge }">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">总司龄：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.workAge }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">离职时间：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<fmt:formatDate value="${trackInfo.employee.quitTime }"  pattern="yyyy-MM-dd"  />">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">合同到期日：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<fmt:formatDate value="${trackInfo.employee.protocolEndTime }"  pattern="yyyy-MM-dd"  />">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">在职状态：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="<c:if test="${trackInfo.employee.jobStatus ==0}">在职</c:if><c:if test="${trackInfo.employee.jobStatus ==1}">离职</c:if><c:if test="${trackInfo.employee.jobStatus ==2}">待离职</c:if>">
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">邮乐账号：</label>
		            	<input type="text" class="form-text" style="border:0px;"  value="${trackInfo.employee.uleAccount}">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">工作地点：</label>
		            	<input type="text" class="form-text" style="border:0px;" value="${trackInfo.employee.workAddressProvince }">
		            	<input type="text" class="form-text" style="border:0px;" value="${trackInfo.employee.workAddressCity }">
		            </div>                                                  
				</div>   
	        </div>
			<div>
				<div class="datatitle">
					<ul class="fl jui-tabswitch">
						<li id="listTab" rel="#old" class="active"><strong>教育经历</strong></li>		
					</ul>
				</div> 
				</br>
				<table>
					<tr>
						<td>起止时间</td>
						<td>学校名称</td>
						<td>学历</td>
						<td>学位</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.empSchoolList}" varStatus="status">
						<tr>
							<td>
							    <fmt:formatDate value="${item.startTime }"  pattern="yyyy-MM-dd"  />
							    至
							    <fmt:formatDate value="${item.endTime }"  pattern="yyyy-MM-dd"  />
							</td>
							<td>${item.school }</td>
							<td>${item.major }</td>
							<td>
		            	        <c:forEach var="item1" items="${trackInfo.config.educationLevel}" varStatus="status">
                      	        	<c:if test="${item.education == item1.id}">${item1.displayName }</c:if> 
                                </c:forEach>  
	                        </td>
	                        <td>
			            	    <c:forEach var="item1" items="${trackInfo.config.degree}" varStatus="status">
                       	        	<c:if test="${item.degree == item1.id}">${item1.displayName }</c:if>  
                                </c:forEach>  
	                        </td>
					    </tr>
					</c:forEach>
				</table>
			</div>
			<div>
				<div class="datatitle">
					<ul class="fl jui-tabswitch">
						<li id="listTab" rel="#old" class="active"><strong>培训证书</strong></li>		
					</ul>
				</div>  
				</br>
				<table>
					<tr>
						<td>起止时间</td>
						<td>培训机构</td>
						<td>培训内容</td>
						<td>所获证书</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.empTrainingList}" varStatus="status">
						<c:if test="${item.isCompanyTraining==0}">
							<tr>
								<td>
								    <fmt:formatDate value="${item.startTime }"  pattern="yyyy-MM-dd"  />
								    至
								    <fmt:formatDate value="${item.endTime }"  pattern="yyyy-MM-dd"  />
								</td>
								<td>${item.trainingInstitutions }</td>
								<td>${item.content }</td>
								<td>${item.obtainCertificate }</td>
							</tr>
						</c:if>
					</c:forEach>
				</table>
			</div>
			<div>
				<div class="datatitle">
					<ul class="fl jui-tabswitch">
						<li id="listTab" rel="#old" class="active"><strong>工作经历</strong></li>		
					</ul>
				</div>  
				</br>
				<table>
					<tr>
						<td>起止时间</td>
						<td>工作单位</td>
						<td>职位/职称</td>
						<td>主办业务</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.workRecordList}" varStatus="status">
						<tr>
							<td>
							    <fmt:formatDate value="${item.startTime }"  pattern="yyyy-MM-dd"  />
							    至
							    <fmt:formatDate value="${item.endTime }"  pattern="yyyy-MM-dd"  />
							</td>
							<td>${item.companyName }</td>
							<td>${item.positionName}</td>
							<td>${item.positionTask }</td>
						</tr>
					</c:forEach>
				</table>
			</div>
        </div>
     </div>

</body>

</html>