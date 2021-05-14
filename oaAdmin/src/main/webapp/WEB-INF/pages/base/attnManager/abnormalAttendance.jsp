<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>异常考勤</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/attnManager/abnormalAttendance.js?v=20200526"></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
</style>
</head>
<body>
	<input type="hidden" value="0" id="flagTab">
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch" id="parent">
			<li id="listTab" rel="#old" class="active"><strong>异常考勤单查询</strong></li>		
			<li id="detailTab" rel="#new"><strong>下属异常考勤单查询</strong></li>
		</ul>
	</div>
	<div class="content first" style="overflow-x:auto">
            
            <div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
                <input type="hidden" id="pageNo" value=""/>
                <input type="hidden" id="pageNo1" value=""/>
               <form id="queryform">
               <input type="hidden"  name="applyType" value="0"/>
                <div class="form-main">
                <!-- 代办  状态-->
               
                <div class="col">
                    <div class="form">
                    	<label class="w" style="width:100px;">员工编号：</label>
                    	<input type="text" class="form-text" id="code" name="code" value="">
                    </div>
                    
                    <div class="form">
                    	<label class="w" style="width:100px;">员工姓名：</label>
                    	<input type="text" class="form-text" id="cnName" name="cnName" value="">
                    </div>
                    
                    <div class="form">
                        <label class="w" style="width:100px;">部门：</label>
                        <select id="firstDepart" name="firstDepart" class="select_v1 firstDepart">
                        </select>
                    </div>
                </div>      
                
               
                <div class="col">
                	<div class="form">
	                    	<label class="w" style="width:100px;">状态：</label>
	                    	<select id="approvalStatus" name="approvalStatus" class="select_v1 approvalStatus">
	                        </select>
	                </div>
	                
                	<div class="form">
                        <label class="w" style="width:100px;">工时制：</label>
                        <select id="workType" name="workType" class="select_v1 workType">
                        </select>
                    </div>
                 
                </div>
                
                
               <div class="col">
		                <div class="form">
		                    	<label class="w" style="width:100px;">异常考勤日期：</label>
		                    	<input  type="text" class="Wdate applyStartDate" name="applyStartDate" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
							
						 </div>
						 <div class="form">		
							 <label class="w" style="width:50px;">至</label>
								<input  type="text" class="Wdate applyEndDate" name="applyEndDate" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />&nbsp;&nbsp;<a class="lastWeek" style="color:blue;">上周</a>&nbsp;&nbsp;<a class="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a class="lastMonth" style="color:blue;">上月</a>
		                 </div>
               </div> 
                    
                    
                </div>
             </form>
              <div class="col">  
                    <div class="button-wrap ml-4">
                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
                     </div> 
                </div> 
			</div>
            <div>
	            <table>
					 <thead>
						<tr>
							<th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">工时制</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">异常考勤日期</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">上班考勤时间</th>
			                
			                <th style="overflow-x:auto;width:100px;text-align:center;">下班考勤时间</th>
			                
			                <th style="overflow-x:auto;width:150px;text-align:center;">上班申诉考勤时间</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">下班申诉考勤时间</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">申诉理由</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">申请时间</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">批核人</th>
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
      	
      	
   <div class="content second" style="overflow-x:auto;display:none;">
            
            <div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
               <input type="hidden" id="pageNo" value=""/>
               <form id="queryform1">
               <input type="hidden"  name="applyType" value="1"/>
                <div class="form-main">
                <!-- 代办  状态-->
                <input type="hidden" id="downApprovalStatus" value=""/>
                <div class="col">
                    <div class="form">
                    	<label class="w" style="width:100px;">员工编号：</label>
                    	<input type="text" class="form-text" id="code" name="code" value="">
                    </div>
                    
                    <div class="form">
                    	<label class="w" style="width:100px;">员工姓名：</label>
                    	<input type="text" class="form-text" id="cnName" name="cnName" value="">
                    </div>
                    
                    
                    <div class="form">
                        <label class="w" style="width:100px;">部门：</label>
                        <select id="secondDepart" name="firstDepart" class="select_v1 firstDepart">
                        </select>
                    </div>
                    
                   
                </div>      
                <div class="col">
	                <div class="form">
		                    <label class="w" style="width:100px;">状态：</label>
		                    <select id="secondapprovalStatus" name="approvalStatus" class="select_v1 approvalStatus">
		                    </select>
		            </div>
	              	 <div class="form">
	                        <label class="w" style="width:100px;">工时制：</label>
	                        <select id="secondworkType" name="workType" class="select_v1 workType">
	                        </select>
	                   </div>
	                 <div class="form">
                    	<label class="w" style="width:100px;">申诉主管：</label>
                    	<input type="text" class="form-text" id="agentName" name="agentName" value="">
                    </div>
               </div>
                	
                     <div class="col">
		                <div class="form">
		                    	<label class="w" style="width:100px;">异常考勤日期：</label>
		                    	<input  type="text" class="Wdate applyStartDate" name="applyStartDate" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
							
						 </div>
						 <div class="form">		
							 <label class="w" style="width:50px;">至</label>
								<input  type="text" class="Wdate applyEndDate" name="applyEndDate" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />&nbsp;&nbsp;<a class="lastWeek" style="color:blue;">上周</a>&nbsp;&nbsp;<a class="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a class="lastMonth" style="color:blue;">上月</a>
		                 </div>
               		 </div> 
                    
                    
                     
                
           
                
                </div>
             </form>
              <div class="col">  
                    <div class="button-wrap ml-4">
                        <button id="query1" class="red-but"><span><i class="icon search"></i>查询</span></button> 
                    </div> 
                </div> 
			</div>
            <div>
	            <table>
					 <thead>
						<tr>
							<th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">工时制</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">申诉主管</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">异常考勤日期</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">上班考勤时间</th>			                
			                <th style="overflow-x:auto;width:100px;text-align:center;">下班考勤时间</th>			                
			                <th style="overflow-x:auto;width:150px;text-align:center;">上班申诉考勤时间</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">下班申诉考勤时间</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">申诉理由</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">申请时间</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">人事批核人</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">状态</th>			                
			                <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
						</tr>
					  </thead>
		              <tbody id="reportList1">
		              </tbody>
	            </table>
            </div>           
        </div>
      	<div class="paging" id="commonPage1"></div>   	
      	
      	
      	<div class="hgc_approvalBox" style="display:none;" id="detailDiv">
        <div class="approval">
            <h2>审批详情<i onclick="closeDiv();" class="close"></i></h2>
            <div class="approvalContent clearfix">
                <div class="approvalPerson fl">
                    <div class="selMatter clearfix">
                        <h4 class="fl">申请人</h4>
                        <div id="detailDiv1" class="selarea fr">
                           
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">所属部门</h4>
                        <div id="detailDiv2" class="selarea fr">
                           
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">申请日期</h4>
                        <div id="detailDiv3" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">考勤日期</h4>
                        <div id="detailDiv4" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">打卡时间</h4>
                        <div id="detailDiv5" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">申诉考勤时间</h4>
                        <div id="detailDiv6" class="selarea fr">
                          
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">申诉理由</h4>
                        <div id="detailDiv7" class="selarea fr">
                            
                        </div>
                    </div>
                </div>
                
                <div class="approvalProcess fr">
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
										<span style="float:left;">失效同意</span>
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
	<!-- 同意弹框 -->
	<!-- 拒绝弹框 -->
	<div class="commonBox popbox" id="attnRefuseDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>审批</strong>
						</div>
						<div class="form-main">
	
							<form id="attnRefuseDivForm" class="attnRefuseDivForm">
							    <input type="hidden" name="processInstanceId"/>
								<div class="col" style="margin-bottom:0px;">
									<div class="form">
										<label class="w"  style="width:130px;">审批处理：</label>
										<span style="float:left;">失效拒绝</span>
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
									<button class="red-but" onclick="attnRefuse();"  style="width:120px;">
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
	<!-- 拒绝弹框 -->
</body>
</html>