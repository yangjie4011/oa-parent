<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>排班审核</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/employeeClass/audit.js?v=20191201"/></script>
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
			<li id="abateTab" rel="#new"><strong>失效</strong></li>
		</ul>
	</div>
	<div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1 arrow-down1-close" onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
                <input type="hidden" id="pageNo" value=""/>
               <form id="queryform">
               
                <div class="form-main" style="display:none;">
                
	                <div class="col">                 
		                <div class="form">
		                	<label class="w">年月</label>
							<input id="month" type="text" class="Wdate" name="month" onClick="WdatePicker({dateFmt:'yyyy-MM'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM'});" />&nbsp;&nbsp;<a id="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a id="lastMonth" style="color:blue;">上月</a>
		                </div>                                                  
	                </div>
       
	                <div class="col">		                
	                    <div class="form">
	                        <label class="w">排班部门：</label>
	                        <select id="firstDepart" name="firstDepart" class="select_v1">
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
                    <div class="button-wrap ml-4" style="display:none;">
                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
                    </div> 
                </div> 
			</div>
			
			<!-- 第一个tab -->		
			<div class="content tableDiv" style="overflow-x:auto">
		            
		            <div>
			            <table>
							 <thead>
								<tr>
					                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">组别</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">年月</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">排班人</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">申请日期</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">类型</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">排班人数</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">应出勤工时</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">排班批核人</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">状态</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
								</tr>
							  </thead>
				              <tbody id="reportList">
				              </tbody>
			            </table>
		            </div>
		        </div>
		        
		        <div id="detailDiv" style="display:none;">
			        <div id="detailTitle" class="title" style="text-align:center;"><strong>标题</strong></div>
			        <div class="col">
	           	      <div class="form">
	                      <label class="w">申请日期：</label>
	                      <input id="applyDate" type="text" class="form-text" style="border:0px;" value="2017-05-25">
	                   </div>
	                   <div class="form">
	                      <label class="w">状态：</label>
	                      <input id="approvalStatus" type="text" class="form-text" style="border:0px;">
	                   </div>
	                </div>
	             
		             <div>
		               <div style="float:left;width:50%;overflow-x:auto">
						    <table border="1" id="reportTitle">
							    <thead>
							    </thead>
				                <tbody>
				                </tbody>
					        </table>
				       </div>
			           <div style="float:left;width:50%;overflow-x:auto">
					       <table border="1" id="reportContent">
							   <thead>
							   </thead>
						       <tbody>
						       </tbody>
					   	   </table>
			           </div>
		            </div>
		            
		             <div class="col">  
	                    <div class="buttonDiv button-wrap ml-4">
	                        <button id="passProcessId" onclick="getPassDiv(this);" class="btn-green btn-large"><span><i class="icon"></i>审核通过</span></button> 
	                        <button id="refuseProcessId" onclick="getRefuseDiv(this);" class="btn-red btn-large"><span><i class="icon"></i>审核拒绝</span></button> 
	                        <button onclick="backWaitList();" class="btn-blue btn-large"><span><i class="icon"></i>取消</span></button>         
	                    </div> 
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
				<!-- 班次信息弹框 -->
				<div class="commonBox popbox" id="settingInfo" style="display:none">
					<div class="popbox-bg"></div>    
					<div class="popbox-center" style="top: 10%; left: 30%">  
						<div class="popbox-main" style="">
							<div class="cun-pop-content"> 
								<div class="form-wrap settingInfo">
									<div class="title">
										<strong><i class="icon"></i>海岛大亨</strong>
										<i onclick="closeInfoDiv();" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
									</div>
									<div class="form-main">
			                               <div class="col">
												<div class="form">
													<label class="w"  style="width:130px;">2019-03-12</label>
													<span>星期五</span>
												</div>
											</div>
											<div class="col">
												<div class="form">
													<label class="w"  style="width:130px;">原排班班次</label>
													<span>A班&nbsp;12:00-18:00</span>	
												</div>
											</div>
											<div class="col">
												<div class="form">
													<label class="w"  style="width:130px;">调整后班次</label>
													<span>B班&nbsp;12:00-18:00</span>	
												</div>
											</div>
											<div class="col">
												<div class="button-wrap ml-4 tiaob">
													
												</div>
											</div>
									</div>
								</div>
								<!-- form-main end -->
							</div>
						</div>
					</div>
				</div>
</body>
</html>