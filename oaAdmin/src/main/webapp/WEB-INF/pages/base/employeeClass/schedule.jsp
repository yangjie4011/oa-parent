<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>排班</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/employeeClass/schedule.js?v=20191201"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
	.form input {
	display:block;
	float:left;
	width:140px;
	height:34px;
	line-height:34px;
	border:1px solid #d9d9d9;
}
.select_v1{
	display:block;
	float:left;
	width:142px;
	height:34px;
	line-height:34px;
	border:1px solid #d9d9d9;
}
</style>
</head>
<body style="overflow-x:auto">
   <div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li class="active"><strong>排班</strong></li>
		</ul>
	</div>
	<div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
               <input type="hidden" id="isScheduler" value="${requestScope.isScheduler }"/>
               <input type="hidden" id="groupIdStr"/>
               <form id="queryform">
               
                <div class="form-main">
            
	                <div class="col">		                
	                    <div class="form">
	                        <label class="w">排班部门：</label>
	                        <select id="firstDepart" name="firstDepart" class="select_v1">  
	                            <option value="">请选择</option>
	                            <c:if test="${requestScope.isScheduler}">
	                                  <c:if test="${requestScope.isScheduler}">
			                                <c:forEach items="${requestScope.departList}" var="depart" varStatus="status">
			                                     <option value="${depart.id}">${depart.name}</option>
			                                </c:forEach>
	                                  </c:if>
	                            </c:if>
	                         </select>
	                    </div>
	                    <div class="form">
	                        <label class="w">组别：</label>
	                        <select id="groupId" name="groupId" class="select_v1">
	                            <option value="">请选择</option>
	                        </select>
	                    </div>
	                </div>
               </div>
             </form>
              <div class="col">  
                    <div class="button-wrap ml-4">
						<button class="blue-but" id="uploadEventBtn"  type="button" ><span>选择文件</span></button>  
                    	<form enctype="multipart/form-data" id="batchUpload" method="post" class="form-horizontal">    
                    		<input type="hidden" id="uploadDepart" name="departId">
                    		<input type="hidden" id="uploadGroup" name="groupId">
						    <input type="file" name="file"  style="width:0px;height:0px;" id="uploadEventFile">  
						    <div class="form">
						    	<input id="uploadEventPath" class="form-text" disabled="disabled"  type="text" placeholder="-未选择excel表-" style="font-size:12px;text-align:center;font-color:gray;font-style:oblique" >                                           
						    </div>
						</form>
						<button type="button" class="blue-but"  onclick="signExcel.uploadBtn()" ><span><i class="icon-shangchuan"></i>上传排班</span></button>
                    </div> 
                </div>  <div class="col">  
                	<div class="button-wrap ml-4">
                		<button id="downloadScheduleTemplate" class="blue-but"><span><i class="icon download"></i>下载模板</span></button> 
                        <button  onclick="getUnCommitData(0);" class="blue-but"><span><i class="icon add"></i>新增排班</span></button> 
                	</div>
                </div>
                <div class="datatitle" style="padding-left:0px;">
					<ul class="fl jui-tabswitch" id="scheduleTab">
						<li  rel="#old" class="active"><strong>排班</strong></li>
				        <li  rel="#new"><strong>排班记录</strong></li>
					</ul>
	            </div>
			</div>
			
	        
	        <div id="detailDiv" style="display:none;">
			        <div id="detailTitle" class="title" style="text-align:center;"><strong></strong></div>
			        <div class="col">
	           	      <div class="form">
	                      <label class="w">组别：</label>
	                      <input id="groupName" type="text" class="form-text" style="border:0px;">
	                   </div>
	                   <div class="form">
	                      <label class="w">状态：</label>
	                      <input id="approvalStatus" type="text" class="form-text" style="border:0px;">
	                   </div>
	                </div>
	                
	               <div>
		            	<div style="float:left;width:50%;overflow-x:auto ">
					      <table border="1" id="detailListTitle">
							  <thead>
							  </thead>
					             <tbody>
					             </tbody>
				          </table>
				      	</div>
			            <div style="float:left;width:50%;overflow-x:auto">
			            	<table border="1" id="detailList">
								  <thead>
								  </thead>
					         	  <tbody>
					              </tbody>
				            </table>
			            </div>
		            </div>           
	                
		            
		             <div class="col">  
	                    <div class="buttonDiv button-wrap ml-4">
	                       
	                    </div> 
                     </div> 
		    </div>
			
			<div class="content scheduleRecord" style="overflow-x:auto;display:none;">
	            <div>
		            <table>
						 <thead>
							<tr>
								<th style="overflow-x:auto;width:100px;text-align:center;">年月</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">组别</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">排班人</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">排班日期</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">排班审核人</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">状态</th>			                
				                <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
							</tr>
						  </thead>
			              <tbody id="reportList">
			              </tbody>
		            </table>
	            </div>           
	        </div>
		    <div class="paging" id="commonPage" style="display:none;"></div>
		    <input type="hidden" id="pageNo" value=""/>
	     
	    <!-- 选择班次弹框 -->
	    <div class="commonBox popbox" id="classSetDiv" style="display:none;">
			<div class="popbox-bg"></div>
			<div class="popbox-center" style="top: 0%; left: 30%">
				<div class="popbox-main" style="">
					<div class="cun-pop-content">
						<div class="form-wrap">
							<div class="title">
								<strong><i class="icon"></i>选择班次</strong>
							</div>
							<div class="form-main">
		
								<form id="classSetDivForm" class="classSetDivForm">
									<div class="col" style="margin-bottom:0px;">
									   <div class="form">
					                      <label class="w">员工姓名：</label>
					                      <input id="employName" type="text" class="form-text" style="border:0px;">
					                      <input id="employId" type="hidden">
					                   </div>
									</div>
									<div class="col" style="margin-bottom:0px;">
									   <div class="form">
					                      <label class="w">排班日期：</label>
					                      <input id="classDate" type="text" class="form-text" style="border:0px;">
					                   </div>
									</div>
									
									<div class="col" style="margin-bottom:0px;">
									    <div class="form">
											<label class="w">选择班次：</label>
											<select id="classSet" class="select_v1"></select>
									    </div>
									</div>
								</form>
		
								<div class="col" style="margin-top:30px;">
									<div class="button-wrap ml-4">
										<button id="setClassSet" class="red-but" style="width:120px;">
											<span>确定</span>
										</button>
										<button class="small grey-but"
											onclick="closeDiv();" style="width:120px;">
											<span>取消</span>
										</button>
									</div>
								</div>
							</div>
						</div>
						<!-- form-main end -->
					</div>
				</div>
			</div>
		</div> 
	    <!-- 选择班次弹框 -->
</body>
</html>
