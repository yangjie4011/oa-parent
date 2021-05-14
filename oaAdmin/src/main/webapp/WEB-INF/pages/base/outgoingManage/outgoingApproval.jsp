<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>外出审批</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/outgoingManage/outgoingApproval.js?v=20200526"/></script>
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
			<li id="listTab" rel="#old" class="active"><strong>待办</strong></li>
			<li id="detailTab" rel="#new"><strong>已办</strong></li>
			<li id="abateTab" rel="#lew"><strong>失效</strong></li>
		</ul>
	</div>
	<div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1 arrow-down1-close" id="getButton" onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
                <input type="hidden" id="pageNo" value=""/>
                </br>
               <form id="queryform">
	            <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	            <input type="hidden" id="pageNo" value=""/>
	                <div class="col">
	                    <div class="form">
	                    	<label class="w">员工编号：</label>
	                    	<input type="text" class="form-text" name="code" value="">
	                    </div>
	                    
	                    <div class="form">
	                    	<label class="w">员工姓名：</label>
	                    	<input type="text" class="form-text" name="cnName" value="">
	                    </div>
	                </div>      
	                
	                <div class="col">
	                	<div class="form">
	                        <label class="w">工时制：</label>
	                        <select id="workType" name="workType" class="select_v1">
	                        </select>
	                    </div>
	                    
	                    <div class="form">
	                        <label class="w">员工类型：</label>
	                        <select id="empTypeId" name="empTypeId" class="select_v1">
	                        </select>
	                    </div>
	                </div>
	                
	                <div class="col">
	                	<div class="form">
	                        <label class="w">是否排班：</label>
	                        <select id="whetherScheduling" name="whetherScheduling" class="select_v1">
	                        </select>
	                    </div>
	                
	                    <div class="form">
	                   		<input type="hidden" id="startTime" name="startTime" value=""/>
	                   		<input type="hidden" id="endTime" name="endTime" value=""/>
	                    	<label class="w">外出日期:</label>
	                   	 	<input id="startDate" type="text" class="Wdate" name="startDate" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'});" />
							<label class="w" style="width:30px;">至</label>
							<input id="endDate" type="text" class="Wdate" name="endDate" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startTime\')}'});" />&nbsp;&nbsp;<a id="lastWeek" style="color:blue;">上周</a>&nbsp;&nbsp;<a id="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a id="lastMonth" style="color:blue;">上月</a>	                    	
	                    </div>
	                    
	                   	                    
	                </div> 
	                
	                <div class="col">
	                    <div class="form">
	                        <label class="w">部门：</label>
	                        <select id="firstDepart" name="firstDepart" class="select_v1">
	                        </select>
	                    </div>
	                    
	                    <div class="form" id="secondDepartDiv" style="display:none">
	                        <select id="secondDepart" name="secondDepart" class="select_v1">
	                        </select>
	                    </div>
	                </div>
              		</form>
              <div class="col">  
                    <div class="button-wrap ml-4">
                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
                    </div> 
                </div> 
			</div>
			<!-- 第一个tab -->		
			<div class="content tableDiv" style="overflow-x:auto">
		            
		            <div>
		            <button onclick="getBatchApproveDiv();" class="showApproveDiv blue-but small" style="display:none;margin-bottom:5px;"><span><i class="icon"></i>快速审批</span></button>
			            <table>
							 <thead>
								<tr>
									<th style="overflow-x:auto;width:100px;text-align:center;" class="showApproveDiv"><input type="checkbox" name="selectAll"/></th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">职位名称</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">工时制</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">开始时间</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">结束时间</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">外出时长</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">外出地点</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">外出事由</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">申请日期</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;" id="auditUserTd"  style="display:none;">批核人</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">状态</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
								</tr>
							  </thead>
				              <tbody id="reportList">
				              </tbody>
			            </table>
		            </div>
		        </div>
		        
		 
		        <div class="paging" id="commonPage"></div>
		        
			    <!-- 同意弹框 -->
			    <div class="commonBox popbox" id="passDiv" style="display:none;">
					<div class="popbox-bg"></div>
					<div class="popbox-center" style="top: 0%; left: 30%">
						<div class="popbox-main" style="">
							<div class="cun-pop-content">
								<div class="form-wrap">
									<div class="title">
										<strong><i class="icon"></i>审批</strong>
									</div>
									<div class="form-main">
				
										<form id="passDivForm" class="passDivForm">
										    <input type="hidden" name="processInstanceId"/>
											<div class="col" style="margin-bottom:0px;">
												<div class="form">
													<label class="w"  style="width:130px;">审批处理：</label>
													<span style="float:left;">同意</span>
												</div>
											</div>
											
											<div class="col" style="margin-bottom:0px;">
												<div class="form">
													<label class="w"  style="width:130px;">审批意见：</label>
													<textarea name="approvalReason" style="width:178px;height:50px;padding:0;" rows="" cols=""></textarea>
												</div>
											</div>
										</form>
				
										<div class="col" style="margin-top:30px;">
											<div class="button-wrap ml-4">
												<button class="red-but" onclick="pass();"  style="width:120px;">
													<span>确定</span>
												</button>
												<button class="small grey-but"
													onclick="closeDiv(this);" style="width:120px;">
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
				<!-- 同意弹框 -->
				
				 <!-- 拒绝弹框 -->
			    <div class="commonBox popbox" id="refuseDiv" style="display:none;">
					<div class="popbox-bg"></div>
					<div class="popbox-center" style="top: 0%; left: 30%">
						<div class="popbox-main" style="">
							<div class="cun-pop-content">
								<div class="form-wrap">
									<div class="title">
										<strong><i class="icon"></i>审批</strong>
									</div>
									<div class="form-main">
				
										<form id="refuseDivForm" class="refuseDivForm">
										    <input type="hidden" name="processInstanceId"/>
											<div class="col" style="margin-bottom:0px;">
												<div class="form">
													<label class="w"  style="width:130px;">审批处理：</label>
													<span style="float:left;">拒绝</span>
												</div>
											</div>
											
											<div class="col" style="margin-bottom:0px;">
												<div class="form">
													<label class="w"  style="width:130px;">审批意见：</label>
													<textarea name="approvalReason" style="width:178px;height:50px;padding:0;" rows="" cols=""></textarea>
												</div>
											</div>
										</form>
				
										<div class="col" style="margin-top:30px;">
											<div class="button-wrap ml-4">
												<button class="red-but" onclick="refuse();"  style="width:120px;">
													<span>确定</span>
												</button>
												<button class="small grey-but"
													onclick="closeDiv(this);" style="width:120px;">
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
				<!-- 拒绝弹框 -->
				
				<!-- 快速审批弹框 -->
       <div class="commonBox popbox" id="fastApproveDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>审批</strong>
						</div>
						<div class="form-main">

							<form id="fastApproveForm" class="fastApproveForm">
								<div class="col" style="margin-bottom:0px;">
									<div class="form">
										<label class="w"  style="width:130px;">审批处理：</label>
										<input style="width:13px;" type="radio" name="commentType" value="pass" checked="checked"><span style="float:left;">同意</span><span style="float:left;width:20px;">&nbsp;&nbsp;&nbsp;</span><input style="width:13px;" type="radio" name="commentType" value="refuse"><span style="float:left;">拒绝</span>
									</div>
								</div>
								
								<div class="col" style="margin-bottom:0px;">
									<div class="form">
										<label class="w"  style="width:130px;">审批意见：</label>
										<textarea name="approvalReason" style="width:178px;height:50px;padding:0;" rows="" cols=""></textarea>
									</div>
								</div>
							</form>

							<div class="col" style="margin-top:30px;">
								<div class="button-wrap ml-4">
									<button class="red-but" onclick="fastApprove();"  style="width:120px;">
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
	<!-- 快速审批弹框 -->
	
	
	<!-- 详情弹框 -->
    <div class="hgc_approvalBox" style="display:none;" id="detailDiv">
        <div class="approval">
            <h2>审批详情<i onclick="closeDiv();" class="close"></i></h2>
            <div class="approvalContent clearfix">
                <div class="approvalPerson fl" style="height: 350px;">
                    <div class="selMatter clearfix">
                        <h4 class="fl">申请人</h4>
                        <div id="empCnName" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">所属部门</h4>
                        <div id="empDepart" class="selarea fr">
                           
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">申请日期</h4>
                        <div id="submitDate" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">外出日期</h4>
                        <div id="outDate" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">外出时间</h4>
                        <div id="outTime" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">外出时长</h4>
                        <div id="duration" class="selarea fr">
                          
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">外出地点</h4>
                        <div id="address" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix leave">
                        <h4 class="fl">外出事由</h4>
                        <div id="reason" class="selarea fr">
                           
                        </div>
                    </div>
                </div>
                <div class="approvalProcess fr">
                    <ul id="taskFlow">
                    </ul>
                </div>
			</div>
			<div class="col" style="margin-top:10px;" id="approvalDiv">
					<div class="button-wrap">
					<button id="passDetail" class="red-but" style="width:120px;">
						<span>同意</span>
					</button>
					<button id="refuseDetail" class="small grey-but" style="width:120px;">
						<span>拒绝</span>
					</button>
					</div>
            </div>
        </div>
	
</body>
</html>