<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>外出查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/outgoingManage/outgoingQuery.js?v=20200526"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
</style>
</head>
<body>
	<div class="content">
		<div class="form-wrap">
			<div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"></i></div>
            	<div class="form-main">
	                
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
	                   	 	<input id="startDate" type="text" class="Wdate applyStartDate" name="startDate" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'});" />
							<label class="w" style="width:30px;">至</label>
							<input id="endDate" type="text" class="Wdate applyStartDate" name="endDate" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startTime\')}'});" />&nbsp;&nbsp;<a id="lastWeek" style="color:blue;">上周</a>&nbsp;&nbsp;<a id="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a id="lastMonth" style="color:blue;">上月</a>	                    	
	                    </div>
	                    
	                   	                    
	                </div> 
	                
	                <div class="col">
	                	<div class="form">
	                    	<label class="w">状态：</label>
	                    	<select id="approvalStatus" name="approvalStatus" class="select_v1">
	                        </select>
	                    </div>
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
	                        <button id="export" class="blue-but"><span><i class="icon add"></i>导出</span></button>          
	                    </div> 
	                </div> 
                </div>
			</div>
            
			<table>
				 <thead>
					<tr>
						<th style="text-align:center;">员工编号</th>
		                <th style="text-align:center;">员工姓名</th>
		                <th style="text-align:center;">部门</th>
		                <th style="text-align:center;">职位名称</th>
		                <th style="text-align:center;">工时制</th>
		                <th style="text-align:center;">开始时间</th>
		                <th style="text-align:center;">结束时间</th>
		                <th style="text-align:center;">外出时长</th>
		                <th style="text-align:center;">外出地点</th>
		                <th style="text-align:center;">外出事由</th>
		                <th style="text-align:center;">申请日期</th>
		                <th style="text-align:center;">批核人</th>
		                <th style="text-align:center;">状态</th>
		                <th style="text-align:center;">操作</th>
					</tr>
				  </thead>
	              <tbody id="reportList">
	              </tbody>
            </table>
        </div>
        
        <!-- 详情弹框 -->
    <div class="hgc_approvalBox" style="display:none;" id="detailDiv">
        <div class="approval">
            <h2>审批详情<i onclick="closeDiv();" class="close"></i></h2>
            <div class="approvalContent clearfix">
                <div class="approvalPerson fl">
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
        </div>
    </div>
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
        <div class="paging" id="commonPage"></div>
</body>
</html>