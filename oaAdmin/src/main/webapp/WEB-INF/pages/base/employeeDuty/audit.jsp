<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>值班审批</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/employeeDuty/audit.js?v=20200514"/></script>
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
	                        <label class="w">年份：</label>
							<input id="year" type="text" class="Wdate" name="year" onClick="WdatePicker({dateFmt:'yyyy'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy'});" value="${thisYear}" />	
	                    </div>
	                    <div class="form">
	                        <label class="w" style="width: 80px;">值班部门：</label>
	                        <select id="depart" name="departId" class="select_v1">  
	                        	<option value="">请选择</option>
	                            	<c:forEach items="${requestScope.departList}" var="depart" varStatus="status">
	                                     <option value="${depart.id}">${depart.name}</option>
	                                </c:forEach>
	                         </select>
	                    </div>
	                    <div class="form">
	                    	<label class="w" style="width: 80px;">法定节假日：</label>
	                    	<select id="vacation" name="vacationName" class="select_v1">
	                    	   <option value="">请选择法定节假日</option>
							   <option value="元旦">元旦</option>
							   <option value="春节">春节</option>
							   <option value="清明节">清明节</option>
							   <option value="劳动节">劳动节</option>
							   <option value="端午节">端午节</option>
							   <option value="中秋节">中秋节</option>
							   <option value="国庆节">国庆节</option>
	                    	</select>
	                    </div>
		                 <div class="form">
	                    	<label class="w">状态：</label>
	                    	<select id="approvalStatus" name="approvalStatus" class="select_v1">
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
			            <table >
							 <thead>
								<tr>
					                <th style="overflow-x:auto;width:100px;text-align:center;">年份</th>								
					                <th style="overflow-x:auto;width:100px;text-align:center;">法定节假日</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
					                <th style="overflow-x:auto;width:100px;text-align:center;">值班排班人</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">申请日期</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">类型</th>
					                <th style="overflow-x:auto;width:150px;text-align:center;">值班人数</th>
					                <th style="overflow-x:auto;width:150px;text-align:center; display: none;" id="showTh">人事审批人</th>
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
	                      <label class="w">公司：</label>
	                      <span id="companyName"></span>
	                   </div>
	                   <div class="form">
	                      <label class="w">部门：</label>
	                      <span id="applyDepartName"></span>
	                   </div>
	                   <div class="form">
	                      <label class="w">状态：</label>
	                      <span id="approvalStatusNames"></span>
	                    </div>
	                </div>
	             	<h4 id="isMoveOne"></h4>
		            <table border="1" id="detailList">
						  <thead>
						  </thead>
			              <tbody>
			              </tbody>
		            </table>
		            <h4 id="isMoveTwo"></h4>
		            <table border="1" id="dutyMoveList">
						  <thead>
						  </thead>
			              <tbody>
			              </tbody>
		            </table>
		            
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
				
</body>
</html>
