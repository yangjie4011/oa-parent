<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>员工履历编辑</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/emp/employee_report_update.js?v=20201211"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
	
	input {
		border:1px solid #d9d9d9;
	}
	
</style>
</head>
<body>
	<div class="content" style="overflow-y:auto;">
			
        <div style="height:50px;">
             <div style="float:left;">
                 <h2 style="font-size: 25px;">员工履历表</h2>
             </div>
	         <div style="float:left;padding-left:20px;">
	              <button onclick="toIndexHtml();" style="width:60px;" class="btn-green btn-large"><span><i class="icon"></i>返回</span></button>      
	         </div>
        </div>
        <input type="hidden" id="employeeId" value="${trackInfo.employee.id }"/>
        <input type="hidden" id="version" value="${trackInfo.employee.version }"/>
        <input type="hidden" id="backType" value="${backType}"/>
        <div style="width:100%;height: 1px;width:100%; border-top: solid #ACC0D8 1px;"></div>
        
        <div style="width:95%;margin-top:10px;">
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
		            	<input type="text" class="form-text"  style="border:0px;" value="${trackInfo.employee.cnName }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">员工编号：</label>
		            	<input type="text" class="form-text"  style="border:0px;" value="${trackInfo.employee.code }">
		            </div>                                                  
				</div>
				
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">部门：</label>
		            	<input type="text" class="form-text"  style="border:0px;" value="${trackInfo.depart.name }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">职位：</label>
		            	<input type="text" class="form-text"  style="border:0px;" value="${trackInfo.position.positionName }">
		            </div>                                                  
				</div>
				
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">汇报对象：</label>
		            	<input type="text" class="form-text"  style="border:0px;" value="${trackInfo.reportToLeader.cnName } <c:if test="${empty trackInfo.reportToLeader.cnName}">无</c:if>">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">部门负责人：</label>
		            	<input type="text" class="form-text"  style="border:0px;" value="${trackInfo.departLeader.cnName }<c:if test="${empty trackInfo.departLeader.cnName}">无</c:if>">
		            </div>                                                  
				</div>
				
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">员工类型：</label>
		            	<input type="text" class="form-text" style="border:0px;" value="${trackInfo.empType.typeCName }">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">在职状态：</label>
		            	<input type="text" class="form-text"  style="border:0px;" value="<c:if test="${trackInfo.employee.jobStatus==0}">在职</c:if><c:if test="${trackInfo.employee.jobStatus==1}">离职</c:if><c:if test="${trackInfo.employee.jobStatus==2}">待离职</c:if>">
		            </div>                                                  
				</div>
				
	        </div>
	        <div style="float:left;width:20%;">
	            <img style="width:150px;height:200px;" src="${trackInfo.employee.photo }"  />
	        </div>
        </div>  
        
	    <div style="float:left;width:1380px;" class="datatitle">
			<ul class="fl jui-tabswitch">
				<li  id="baseInfo" class="active"><strong>基本信息</strong></li>
				<li  id="payrollInfo"><strong>在职信息</strong></li>
				<li  id="educationExperience"><strong>教育经历</strong></li>		
				<li  id="trainingCertificate"><strong>培训证书</strong></li>		
				<li  id="workExperience"><strong>工作经历</strong></li>		
				<li  id="urgentContact"><strong>紧急联系人</strong></li>		
				<li  id="spouseInfo"><strong>配偶信息</strong></li>		
				<li  id="childrenInfo"><strong>子女信息</strong></li>		
				<li  id="achievementAndRewardMerit"><strong>业绩与奖惩</strong></li>		
				<li  id="trainRecord"><strong>培训记录</strong></li>		
				<li  id="contractSignRecord"><strong>合同签订记录</strong></li>		
				<li  id="assessRecord"><strong>考核记录</strong></li>
				<li  id="postRecord"><strong>岗位记录</strong></li>				
			</ul>
		</div>
		
		<!-- 基本信息 -->
        <div class="tabDiv" id="baseInfo" style="float:left;width:100%;text-algin:center;padding-top:20px;">
		    <div class="col">                 
	            <div class="form">
	            	<label class="w" style="width:150px;">姓名：</label>
	            	<input id="cnName" type="text" class="form-text"  value="${trackInfo.employee.cnName }">
	            </div>  
	            <div class="form">
	            	<label class="w" style="width:150px;">英文名：</label>
	            	<input id="engName" type="text" class="form-text" value="${trackInfo.employee.engName }">
	            </div>                                                  
			</div>   
			<div class="col">                 
	            <div class="form">
	            	<label class="w" style="width:150px;">性别：</label>
	            	<select id="sex" class="select_v1">
	            	    <option value="">请选择</option>
	            	    <option <c:if test="${trackInfo.employee.sex==0}">selected</c:if> value="0">男</option>
                        <option <c:if test="${trackInfo.employee.sex==1}">selected</c:if> value="1">女</option>
                    </select>
	            </div>  
	            <div class="form">
	            	<label class="w" style="width:150px;">婚姻状况：</label>
	            	<select id="maritalStatus" class="select_v1">
	            	    <option value="">请选择</option> 
                        <c:forEach var="item" items="${trackInfo.config.maritalStatus}" varStatus="status">
                        	<option <c:if test="${trackInfo.employee.maritalStatus == item.id}">selected="selected"</c:if> value="${item.id }">${item.displayName }</option> 
                        </c:forEach>
                    </select>
	            </div>                                                  
			</div>   
			<div class="col">                 
	            <div class="form">
	            	<label class="w" style="width:150px;">国籍：</label>
	            	<select id="country" class="select_v1">
	            	    <option value="">请选择</option>
                    	<c:forEach var="item" items="${trackInfo.comConfig.country}" varStatus="status">
                        	<option <c:if test="${trackInfo.employee.country == item.id}">selected="selected"</c:if> value="${item.id }">${item.displayName }</option> 
                        </c:forEach>
                    </select>
	            </div>  
	            <div class="form">
	            	<label class="w" style="width:150px;">民族：</label>
	            	<select id="nation" class="select_v1">
                        <option value="">请选择</option>
                    	<c:forEach var="item" items="${trackInfo.config.nation}" varStatus="status">
                        	<option <c:if test="${trackInfo.employee.nation == item.id}">selected="selected"</c:if> value="${item.id }">${item.displayName }</option> 
                        </c:forEach>
                    </select>
	            </div>                                                  
			</div>   
			<div class="col">                 
	            <div class="form">
	            	<label class="w" style="width:150px;">出生日期：</label>
	            	<input value="<fmt:formatDate value="${trackInfo.employee.birthday }"  pattern="yyyy-MM-dd"  />" id="birthday" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
	            </div>  
	            <div class="form">
	            	<label class="w" style="width:150px;">年龄：</label>
	            	<input value="${trackInfo.employee.age }" id="age" type="text" class="form-text">
	            </div>     
	            <div class="form">
	            	<label class="w" style="width:150px;">员工识别号：</label>
	            	<input value="${trackInfo.employee.identificationNum }" id="identificationNum" type="text" class="form-text" readonly/>
	            </div>                                                  
			</div>  
			<div class="col">                 
	            <div class="form">
	            	<label class="w" style="width:150px;">政治面貌：</label>
	            	<select id="politicalStatus" class="select_v1">
	            		<option value="">请选择</option>
                    	<c:forEach var="item" items="${trackInfo.config.politicalStatus}" varStatus="status">
                        	<option <c:if test="${trackInfo.employee.politicalStatus == item.id}">selected="selected"</c:if> value="${item.id }">${item.displayName }</option> 
                        </c:forEach>  
                    </select>
	            </div>  
	            <div class="form">
	            	<label class="w" style="width:150px;">文化程度：</label>
	            	<select id="degreeOfEducation" class="select_v1">
	            		<option value="">请选择</option>
                    	<c:forEach var="item" items="${trackInfo.config.educationLevel}" varStatus="status">
                        	<option <c:if test="${trackInfo.employee.degreeOfEducation == item.id}">selected="selected"</c:if> value="${item.id }">${item.displayName }</option> 
                        </c:forEach> 
                    </select>
	            </div>                                                  
			</div>   
			<div class="col">                 
	            <div class="form">
	            	<label class="w" style="width:150px;">户籍：</label>
	            	<input value="${trackInfo.employee.householdRegister}" id="householdRegister" type="text" class="form-text">
	            </div>  
	            <div class="form">
	            	<label class="w" style="width:150px;">在沪居住地：</label>
	            	<input value="${trackInfo.employee.address}" id="address" type="text" class="form-text">
	            </div>                                                  
			</div>   
			<div class="col">                 
	            <div class="form">
	            	<label class="w" style="width:150px;">行业相关性：</label>
	            	<select id="industryRelevance" class="select_v1">
	            		<option value="">请选择</option>
                    	<c:forEach var="item" items="${trackInfo.comConfig.industryCorrelation}" varStatus="status">
                        	<option <c:if test="${trackInfo.employee.industryRelevance == item.id}">selected="selected"</c:if> value="${item.id }">${item.displayName }</option> 
                        </c:forEach> 
                    </select>
	            </div>  
	            <div class="form">
	            	<label class="w" style="width:150px;">从业背景：</label>
	            	<input value="${trackInfo.employee.workingBackground}" id="workingBackground" type="text" class="form-text">
	            </div>                                                  
			</div>  
			<div class="col">                 
	            <div class="form">
	            	<label class="w" style="width:150px;">手机号码：</label>
	            	<input value="${trackInfo.employee.mobile}" id="mobile" type="text" class="form-text">
	            </div>  
	            <div class="form">
	            	<label class="w" style="width:150px;">备注：</label>
	            	<input value="${trackInfo.employee.remark}" id="remark" type="text" class="form-text">
	            </div>                                        
			</div> 
			<div class="col">                 
	           <div class="form">
	            	<label class="w" style="width:150px;">员工照片：</label>
	            	<button class="btn btn-success btn-sm" id="uploadEventBtn"  type="button">选择文件</button> 
	            	<input value="" id="employeePhoto" type="hidden"> 
	            </div>   
	            <div class="form">
	            	<label class="w" style="width:0px;">&nbsp;</label>
					<input id="uploadEventPath" class="form-text" disabled="disabled"  type="text" placeholder="--- 未选择excel表 ---" style="font-size:15px;text-align:center;font-color:gray;font-style:oblique" >                                           
	            </div> 
	            <div class="form">
	            	<label class="w" style="width:0px;">&nbsp;</label>                 
					<button type="button" class="btn btn-success btn-sm"  onclick="signExcel.uploadBtn()" >上传</button> 
	            </div>  
	            <div class="form">
	            	<label class="w" style="width:0px;">&nbsp;</label>
					<form enctype="multipart/form-data" id="batchUpload"  action="employeeRecord/uploadEmployeePhoto.html" method="post">                                                                                  
                       <input type="file" name="file"  style="width:0px;height:0px;" id="uploadEventFile">
					</form>
	            </div>                                                    
			</div> 
			<div class="col" style="margin-top:30px;">
				<div class="button-wrap ml-4">
					<button onclick="saveBaseInfo();" class="blue-but">
						<span>保存</span>
					</button>
				</div>
		     </div>
	       </div>
	        
	        <!-- 在职信息 -->
	        <div class="tabDiv" id="payrollInfo" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
		        <div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">公司名称：</label>
		            	<select id="companyName" class="select_v1">
		            		<option value="">请选择</option>
	                    	<c:forEach var="item" items="${trackInfo.companyList}" varStatus="status">
	                        	<option <c:if test="${trackInfo.employee.companyId == item.id}">selected="selected"</c:if> value="${item.id }">${item.name }</option> 
	                        </c:forEach> 
                        </select>
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">员工编号：</label>
		            	<input value="${trackInfo.employee.code }" id="code" type="text" class="form-text" >
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">员工类型：</label>
		            	<select id="empTypeId" class="select_v1">
		            		<option value="">请选择</option>
		            	    <c:forEach var="item" items="${trackInfo.empTypeList}" varStatus="status">
	                        	<option <c:if test="${trackInfo.employee.empTypeId == item.id}">selected="selected"</c:if> value="${item.id }">${item.typeCName }</option> 
	                        </c:forEach> 
                        </select>
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">工时种类：</label>
		            	<select id="workType" class="select_v1">
		            	    <option value="">请选择</option>
		            	    <c:forEach var="item" items="${trackInfo.comConfig.typeOfWork}" varStatus="status">
	                        	<option <c:if test="${trackInfo.employee.workType == item.id}">selected="selected"</c:if> value="${item.id }">${item.displayName }</option> 
	                        </c:forEach> 
                        </select>
		            </div>                                                  
				</div> 
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">楼层：</label>
		            	<select id="floorCode" class="select_v1">
		            	    <option value="">请选择</option>
		            	    <c:forEach var="item" items="${trackInfo.floorList}" varStatus="status">
	                        	<option <c:if test="${trackInfo.employee.floorCode == item.floorNum}">selected="selected"</c:if> value="${item.floorNum }">${item.name }</option> 
	                        </c:forEach> 
                        </select>
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">座位号：</label>
		            	<input value="${trackInfo.employee.seatCode}" id="seatCode" type="text" class="form-text">
		            </div>                                                  
				</div>  
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">通行证：</label>
		            	<input value="${trackInfo.username}" id="userName" type="text" class="form-text" readonly>
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">邮箱：</label>
		            	<input value="${trackInfo.employee.email }" id="email" type="text" class="form-text" >
		            </div>                                                  
				</div>  
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">部门：</label>
		            	<select onchange="getDepartLeader(this);" id="depart" class="select_v1">
		            	    <option value="">请选择</option>
		            	    <c:forEach var="item" items="${trackInfo.departList}" varStatus="status">
	                        	<option <c:if test="${trackInfo.depart.id == item.id}">selected="selected"</c:if> value="${item.id }">${item.name }</option> 
	                        </c:forEach> 
                         </select>
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">汇报对象：</label>
		            	<select id="reportToLeader" class="select_v1">
		            	    <option value="">请选择</option>
		            	    <c:forEach var="item" items="${trackInfo.reportToLeaderList}" varStatus="status">
	                        	<option <c:if test="${trackInfo.employee.reportToLeader == item.id}">selected="selected"</c:if> value="${item.id }">${item.cnName }</option> 
	                        </c:forEach> 
                        </select>
		            </div>                                                  
				</div> 
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">部门负责人：</label>
		            	<input value="${trackInfo.departLeader.cnName }" id="departLeader" type="text" class="form-text" readonly>
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">职位：</label>
		            	<select id="positionId" class="select_v1" onchange="getPositionLevelAndSeqList(this);">
		            	    <option value="">请选择</option>
		            	    <c:forEach var="item" items="${trackInfo.positionList}" varStatus="status">
	                        	<option <c:if test="${trackInfo.position.id == item.id}">selected="selected"</c:if> value="${item.id }">${item.positionName }</option> 
	                        </c:forEach> 
                        </select>
		            </div>                                                  
				</div>
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">职级：</label>
		            	<select id="positionLevel" class="select_v1">
		            	    <option value="">请选择</option>
		            	    <c:forEach var="item" items="${trackInfo.levelList}" varStatus="status">
	                        	<option <c:if test="${trackInfo.employee.positionLevel == item}">selected="selected"</c:if> value="${item}">${item }</option> 
	                        </c:forEach> 
                         </select>
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">职位序列：</label>
		            	<select id="positionSeq" class="select_v1">
		            	    <option value="">请选择</option>
		            	    <c:forEach var="item" items="${trackInfo.seqList}" varStatus="status">
	                        	<option <c:if test="${trackInfo.employee.positionSeq == item}">selected="selected"</c:if> value="${item}">${item }</option> 
	                        </c:forEach> 
                        </select>
		            </div>                                                  
				</div>  
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">职称：</label>
		            	<input value="${trackInfo.employee.positionTitle}" id="positionTitle" type="text" class="form-text">
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">是否排班：</label>
		            	<select id="whetherScheduling" class="select_v1">
		            	    <option value="">请选择</option>
		            	    <c:forEach var="item" items="${trackInfo.config.whetherScheduling}" varStatus="status">
	                        	<option <c:if test="${trackInfo.employee.whetherScheduling == item.id}">selected="selected"</c:if> value="${item.id }">${item.displayName }</option> 
	                        </c:forEach> 
                        </select>
		            </div>                                                  
				</div>    
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">入职时间：</label>
		            	<input value="<fmt:formatDate value="${trackInfo.employee.firstEntryTime }"  pattern="yyyy-MM-dd"  />" id="firstEntryTime" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">试用期到期日：</label>
		            	<input value="<fmt:formatDate value="${trackInfo.employee.probationEndTime }"  pattern="yyyy-MM-dd"  />" id="probationEndTime" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
		            </div>                                                  
				</div>   
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">司龄：</label>
		            	<input value="${trackInfo.ourAge}" id="ourAge" type="text" class="form-text" readonly>年
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">入司前工龄：</label>
		            	<input value="${trackInfo.beforeWorkAge }" id="beforeWorkAge" type="text" class="form-text"/>年
		            </div>                                                  
				</div>  
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">总司龄：</label>
		            	<input value="${trackInfo.workAge}" id="workAge" type="text" class="form-text" readonly>年
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">离职时间：</label>
		            	<input value="<fmt:formatDate value="${trackInfo.employee.quitTime }"  pattern="yyyy-MM-dd"  />" id="quitTime" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
		            </div>                                                  
				</div>  
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">合同到期日：</label>
		            	<input value="<fmt:formatDate value="${trackInfo.employee.protocolEndTime }"  pattern="yyyy-MM-dd"  />" id="protocolEndTime" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
		            </div>  
		            <div class="form">
		            	<label class="w" style="width:150px;">在职状态：</label>
		            	<select id="jobStatus" class="select_v1">
		            	    <option value="">请选择</option>
		            	    <option <c:if test="${trackInfo.employee.jobStatus ==0}">selected="selected"</c:if> value="0">在职</option>
                            <option <c:if test="${trackInfo.employee.jobStatus ==1}">selected="selected"</c:if> value="1">离职</option>
                            <option <c:if test="${trackInfo.employee.jobStatus ==2}">selected="selected"</c:if> value="2">待离职</option>
                        </select>
		            </div>                                                  
				</div> 
				<div class="col">                 
		            <div class="form">
		            	<label class="w" style="width:150px;">邮乐账号：</label>
		            	<input value="${trackInfo.employee.uleAccount}" id="uleAccount" type="text" class="form-text">
		            </div>  
		            <div class="form">
                        <label class="w">工作地点：</label>
                        <select id="workAddressProvince" class="select_v1">
                        </select>
                        <input type="hidden" id="workProvinceHidden" value="${trackInfo.employee.workAddressProvince }"/>
                    </div>
                    <div class="form">
                        <select id="workAddressCity" class="select_v1">
                        </select>
                        <input type="hidden" id="workCityHidden" value="${trackInfo.employee.workAddressCity }"/>
                    </div>                                                
				</div>  
				<div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="savePayrollInfo();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
			     </div>   
	        </div>
	        
	        <!-- 教育经历 -->
	        <div class="tabDiv" id="educationExperience" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addEducationExperience();" class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
						<td>起止时间</td>
						<td>学校名称</td>
						<td>专业</td>
						<td>学历</td>
						<td>学位</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.empSchoolList}" varStatus="status">
						<tr>
							<td id="${item.id }">
							    <input value="<fmt:formatDate value="${item.startTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />-
							    <input value="<fmt:formatDate value="${item.endTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
							</td>
							<td><input value="${item.school }" type="text" class="form-text" ></td>
							<td><input value="${item.major }" type="text" class="form-text" ></td>
							<td>
								<select class="select_v1" style="float:none;">
				            	    <option value="">请选择</option>
				            	    <c:forEach var="item1" items="${trackInfo.config.educationLevel}" varStatus="status">
                        	        	<option <c:if test="${item.education == item1.id}">selected="selected"</c:if> value="${item1.id }">${item1.displayName }</option> 
                                    </c:forEach>  
	                            </select>
	                        </td>
	                        <td>
								<select class="select_v1" style="float:none;">
				            	    <option value="">请选择</option>
				            	    <c:forEach var="item1" items="${trackInfo.config.degree}" varStatus="status">
                        	        	<option <c:if test="${item.degree == item1.id}">selected="selected"</c:if> value="${item1.id }">${item1.displayName }</option> 
                                    </c:forEach>  
	                            </select>
	                        </td>
							<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
					    </tr>
					</c:forEach>
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="saveEducationExperience();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
	       
	       <!--  隐藏的学历学位，用于新增copy-->
	       <div id="educationLevel" style="display:none;">
		       <select class="select_v1" style="float:none;">
	           	    <option value="">请选择</option>
	           	    <c:forEach var="item1" items="${trackInfo.config.educationLevel}" varStatus="status">
	                	<option <c:if test="${item.education == item1.id}">selected="selected"</c:if> value="${item1.id }">${item1.displayName }</option> 
	                </c:forEach>  
		        </select>
	       </div>
	       <div id="degree" style="display:none;">
		       <select  class="select_v1" style="float:none;">
	          	    <option value="">请选择</option>
	          	    <c:forEach var="item1" items="${trackInfo.config.degree}" varStatus="status">
	                	<option <c:if test="${item.degree == item1.id}">selected="selected"</c:if> value="${item1.id }">${item1.displayName }</option> 
	                </c:forEach>  
	            </select>
	       </div>
	         <!-- 隐藏的学历学位，用于新增copy -->
	        
	        <!-- 培训证书 -->
	        <div class="tabDiv" id="trainingCertificate" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addTrainingCertificate();"  class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
						<td>起止时间</td>
						<td>培训机构</td>
						<td>培训内容</td>
						<td>所获证书</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.empTrainingList}" varStatus="status">
						<c:if test="${item.isCompanyTraining==0}">
							<tr id="${item.id }">
								<td>
								    <input value="<fmt:formatDate value="${item.startTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />-
								    <input value="<fmt:formatDate value="${item.endTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
								</td>
								<td><input value="${item.trainingInstitutions }" type="text" class="form-text" ></td>
								<td><input value="${item.content }" type="text" class="form-text" ></td>
								<td><input value="${item.obtainCertificate }" type="text" class="form-text" ></td>
								<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
							</tr>
						</c:if>
					</c:forEach>
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="saveTrainingCertificate();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
	        
	        <!-- 工作经历 -->
	        <div class="tabDiv" id="workExperience" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addWorkExperience();" class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
						<td>起止时间</td>
						<td>单位名称</td>
						<td>职位/职称</td>
						<td>主办业务</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.workRecordList}" varStatus="status">
						<tr id="${item.id }">
							<td>
							    <input value="<fmt:formatDate value="${item.startTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />-
							    <input value="<fmt:formatDate value="${item.endTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
							</td>
							<td><input value="${item.companyName }" type="text" class="form-text" ></td>
							<td><input value="${item.positionName}" type="text" class="form-text" ></td>
							<td><input value="${item.positionTask }" type="text" class="form-text" ></td>
							<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
						</tr>
					</c:forEach>
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="saveWorkExperience();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
	        
	        <!-- 紧急联系人 -->
	        <div class="tabDiv" id="urgentContact" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addUrgentContact();" class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
						<td>序号</td>
						<td>称谓</td>
						<td>姓名</td>
						<td>电话</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.urgentContactList}" varStatus="status">
						<tr id="${item.id }">
							<td>
								<select  class="select_v1" style="float:none;">
					          	    <option value="">请选择</option>
					                <option <c:if test="${item.priority == 1}"> selected="selected" </c:if> value="1">第一联系人</option> 
					                <option <c:if test="${item.priority == 2}"> selected="selected" </c:if> value="2">第二联系人</option>
					                <option <c:if test="${item.priority == 3}"> selected="selected" </c:if> value="3">第三联系人</option>
					                <option <c:if test="${item.priority == 4}"> selected="selected" </c:if> value="4">第四联系人</option>
					                <option <c:if test="${item.priority == 5}"> selected="selected" </c:if> value="5">第五联系人</option>
		                        </select>
							</td>
							<td><input value="${item.shortName }" type="text" class="form-text" ></td>
							<td><input value="${item.name }" type="text" class="form-text" ></td>
							<td><input value="${item.mobile }" type="text" class="form-text" ></td>
							<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
						</tr>
					</c:forEach>
					
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="saveEmergencyContact();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
	        
	        <!-- 配偶信息 -->
	        <div class="tabDiv" id="spouseInfo" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addSpouseInfo();" class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
						<td>姓名</td>
						<td>单位名称</td>
						<td>工作电话</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.familyMemberList}" varStatus="status">
					    <c:if test="${item.relation == 0}">
						    <tr id="${item.id }">
								<td><input value="${item.memberName }" type="text" class="form-text" ></td>
								<td><input value="${item.memberCompanyName }" type="text" class="form-text" ></td>
								<td><input value="${item.memberMobile }" type="text" class="form-text" ></td>
								<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
							</tr>
					    </c:if>
					</c:forEach>
					
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="saveSpouseInfo();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
            
            <!-- 子女信息 -->
	        <div class="tabDiv" id="childrenInfo" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addChildrenInfo();" class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
						<td>姓名</td>
						<td>出生日期</td>
						<td>子女性别</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.familyMemberList}" varStatus="status">
						<c:if test="${item.relation == 1}">
							<tr id="${item.id }">
								<td><input value="${item.memberName }" type="text" class="form-text" ></td>
								<td><input value="<fmt:formatDate value="${item.birthday }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" /></td>
								<td>
								    <select class="select_v1" style="float:none;">
					            	    <option value="">请选择</option>
					            	    <option <c:if test="${item.memberSex == 0}">selected</c:if> value="0">男</option>
			                            <option <c:if test="${item.memberSex == 1}">selected</c:if> value="1">女</option>
		                            </select>
		                        </td>
								<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
							</tr>
						</c:if>
					</c:forEach>
					
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="saveChildrenInfo();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
	        
	        <!-- 业绩与奖惩 -->
	        <div class="tabDiv" id="achievementAndRewardMerit" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addAchievementAndRewardMerit();" class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
						<td>奖惩时间</td>
						<td>奖惩内容</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.achievementList}" varStatus="status">
			            <tr id="${item.id }">
							<td><input value="<fmt:formatDate value="${item.processTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" /></td>
							<td>
							    <input value="${item.content }" type="text" class="form-text" >
	                        </td>
							<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
						</tr>
		            </c:forEach>
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="saveAchievementAndRewardMerit();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
	        
	        <!-- 培训记录 -->
	        <div class="tabDiv" id="trainRecord" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addTrainRecord();" class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
						<td>培训时间</td>
						<td>培训项目/内容</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.empTrainingList}" varStatus="status">
						<c:if test="${item.isCompanyTraining==1 }">
							<tr id="${item.id }">
								<td>
								    <input value="<fmt:formatDate value="${item.startTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
								</td>
								<td><input value="${item.trainingProName }" type="text" class="form-text" ></td>
								<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
							</tr>
						</c:if>
					</c:forEach>
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="saveTrainRecord();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
	        
	        <!-- 合同签订记录-->
	        <div class="tabDiv" id="contractSignRecord" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addContractSignRecord();" class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
					    <td>合同编号</td>
						<td>签订日期</td>
						<td>合同期限</td>
						<td>试用到期日</td>
						<td>合同到期日</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.empContractList}" varStatus="status">
						<tr>
							<td><input value="${item.contractCode }" type="text" class="form-text" ></td>
							<td>
							    <input value="<fmt:formatDate value="${item.contractSignedDate }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
	                        </td>
	                        <td>
	                            <input value="<fmt:formatDate value="${item.contractStartTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
	                            -
	                            <input value="<fmt:formatDate value="${item.contractEndTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
	                        </td>
	                        <td>
	                            <input value="${item.probationExpire }" type="text" class="form-text" >
	                        </td>
	                        <td>
	                            <input value="${item.contractPeriod }" type="text" class="form-text" >
	                        </td>
							<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
						</tr>
					</c:forEach>
					
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="saveContractSignRecord();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
	        
	        <!-- 考核记录-->
	        <div class="tabDiv" id="assessRecord" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addAssessRecord();" class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
					    <td>考核日期</td>
						<td>考核成绩</td>
						<td>考核结论</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.empAppraiseList}" varStatus="status">
						<tr>
							<td>
							    <input value="<fmt:formatDate value="${item.annualExaminationTime }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
	                        </td>
	                        <td>
	                            <input value="${item.score }" type="text" class="form-text" >
	                        </td>
	                        <td>
	                            <input value="${item.conclusion }" type="text" class="form-text" >
	                        </td>
							<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
						</tr>
					</c:forEach>
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="saveAssessRecord();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
	        
	        <!-- 岗位记录-->
	        <div class="tabDiv" id="postRecord" style="display:none;float:left;width:100%;text-algin:center;padding-top:20px;">
	            <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="addPostRecord();" class="blue-but">
							<span>新增</span>
						</button>
					</div>
			    </div>   
		        <table>
					<tr>
					    <td>生效/调整日期</td>
						<td>部门</td>
						<td>职位</td>
						<td>说明</td>
						<td>&nbsp;</td>
					</tr>
					<c:forEach var="item" items="${trackInfo.empPostRecordList}" varStatus="status">
						<tr>
	                        <td>
	                            <input value="<fmt:formatDate value="${item.effectiveDate }"  pattern="yyyy-MM-dd"  />" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
	                        </td>
	                        <td>
	                            <select class="select_v1" style="float:none;">
				            	    <option value="">请选择</option>
				            	    <c:forEach var="item1" items="${trackInfo.departList}" varStatus="status">
			                        	<option <c:if test="${item.departId == item1.id}">selected="selected"</c:if> value="${item1.id }">${item1.name }</option> 
			                        </c:forEach> 
		                         </select>
	                        </td>
	                        <td>
		                        <select class="select_v1" style="float:none;">
				            	    <option value="">请选择</option>
				            	    <c:forEach var="item1" items="${trackInfo.positionList}" varStatus="status">
			                        	<option <c:if test="${item.positionId == item1.id}">selected="selected"</c:if> value="${item1.id }">${item1.positionName }</option> 
			                        </c:forEach> 
		                        </select>
	                        </td>
	                        <td>
	                            <input value="${item.remark }" type="text" class="form-text" >
	                        </td>
							<td><a onclick="delTr(this);" style="color:blue;">删除</a></td>
						</tr>
					</c:forEach>
			    </table>
			    <div class="col" style="margin-top:30px;">
					<div class="button-wrap ml-4">
						<button onclick="savePostRecord();" class="blue-but">
							<span>保存</span>
						</button>
					</div>
		        </div>
	        </div>
	        
	       <!--  隐藏的部门，用于新增copy-->
	       <div id="departList" style="display:none;">
		       <select onchange="getPositionBydepart(this);" class="select_v1" style="float:none;">
	           	    <option value="">请选择</option>
	           	    <c:forEach var="item" items="${trackInfo.departList}" varStatus="status">
	                	<option value="${item.id }">${item.name }</option> 
	                </c:forEach>  
		        </select>
	       </div>
	       <!--  隐藏的部门，用于新增copy-->
 
     </div>

</body>

</html>