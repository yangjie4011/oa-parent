<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>消下属缺勤审批</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/delayWork/removeSubordinateAbsenceAudit.js?v=20200526"/></script>
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
               <form id="queryform">
               
                <div class="form-main" style="display:none;">
	                <div class="col">                 
		                <div class="form">
		                	<label class="w">员工编号</label>
							<input id="empCode" type="text"  name="empCode" >
		                </div>   
		                <div class="form">
		                	<label class="w">姓名</label>
							<input id="empName" type="text"  name="empName" >
					   </div>     
		                <div class="form">
	                        <label class="w" style="width:100px;">申诉主管：</label>
	                       <input id="leaderName" type="text"  name="leaderName" >
	               		 </div>                                                  
	                </div>
       
	                <div class="col">		                
	                    <div class="form">
	                        <label class="w">部门：</label>
	                        <select id="firstDepart" name="departId" class="select_v1">
	                        </select>
	                    </div>
	                    <div class="form">
	                    	<label class="">考勤日期：</label>
	                    	<input id="startTime" type="text" class="Wdate applyStartDate" name="startTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    	<label class="margin-left"> &nbsp;至&nbsp;&nbsp;</label>
							<input id="endTime" type="text" class="Wdate applyEndDate" name="endTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>       
	                    </div>
	                </div>
	                
	              
	                
               </div>
             </form>
              <div class="col">  
                    <div class="button-wrap ml-4" style="display:none;">
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
					                <th style="overflow-x:auto;width:100px;text-align:center;">消缺勤员工姓名</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">申诉主管</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">出勤多余小时数</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">前一天下班时间</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">考勤日期</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">考勤时间</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">消缺勤小时数</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">消缺勤理由</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;" id="auditUserTd"  style="display:none;">人事批核人</th>
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
	
	
	<div class="hgc_approvalBox" style="display:none;" id="detailDiv">
        <div class="approval">
            <h2>消下属缺勤<i onclick="closeDiv();" class="close"></i></h2>
            <div class="approvalContent clearfix">
                <div class="approvalPerson fl" style="height: 350px;" >
                    <div class="selMatter clearfix">
                        <h4 class="fl">消缺勤员工姓名</h4>
                        <div id="detailDiv1" class="selarea fr">
                           
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">申诉主管</h4>
                        <div id="detailDiv2" class="selarea fr">
                           
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">出勤多余小时数</h4>
                        <div id="detailDiv3" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">前一天下班时间</h4>
                        <div id="detailDiv4" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">考勤日期</h4>
                        <div id="detailDiv5" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">考勤时间</h4>
                        <div id="detailDiv6" class="selarea fr">
                          
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">消缺勤小时数</h4>
                        <div id="detailDiv7" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix leave">
                        <h4 class="fl">消缺勤理由</h4>
                        <div id="detailDiv8" class="selarea fr">
                            
                        </div>
                    </div>
                </div>
                
                <div class="approvalProcess fr" style="height: 350px;">
                    <ul id="taskFlow">
                        <li>
                            <em class="gren">流程结束<i></i></em>
                            <div class="r">
                                <div class="p1">21312</div>
                                <div class="p2">维权群无</div>
                                <div class="p3 red">审批拒绝</div>
                                <div class="p4"><i>审批意见:</i>
                                                                                                        无
                                </div>
                            </div>
                        </li>
                        <li>
                            <b></b>
                            <div class="r">
                                <div class="p1">
                                    人事部门
                                </div>
                                <div class="p3 gren">待审批</div>
                            </div>
                        </li>
                        <li>
                            <b></b>
                            <div class="r">
                                <div class="p1">2018-07-05 13:54</div>
                                <div class="p2">OACS02&nbsp;开发经理</div>
                                <div class="p3 gren">审批通过</div>
                                <div class="p4">
                                    <i>审批意见:</i>
                                    无
                                </div>
                            </div>
                        </li>
                        <li>
                            <em class="blue">提出申请
                                <i></i>
                            </em>
                            <div class="r">
                                <div class="p1">2018-07-05 13:53</div>
                                <div class="p2">OACS1717&nbsp;高级开发工程师</div>
                                <div class="p3 blue">发起申请</div>
                                <div class="p4">
                                    <i>申请理由:</i>测试后台数据</div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="col" style="margin-top:10px;" id="detail">
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
    </div>
	
</body>
</html>