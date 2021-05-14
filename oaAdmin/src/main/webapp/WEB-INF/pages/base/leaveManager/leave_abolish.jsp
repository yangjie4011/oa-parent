<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>销假查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/leaveManager/leave_abolish.js?v=20200526"/></script>
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
<body>
	<div class="content" style="overflow-x:auto">
            
            <div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
               <input type="hidden" id="pageNo" value=""/>
               <input type="hidden" id="token" value="${token}"/>
               <form id="queryform">
               
                <div class="form-main">
                
                <div class="col">
                    <div class="form">
                    	<label class="w">员工编号：</label>
                    	<input type="text" class="form-text" id="code" name="code" value="">
                    </div>
                    
                    <div class="form">
                    	<label class="w">员工姓名：</label>
                    	<input type="text" class="form-text" id="cnName" name="cnName" value="">
                    </div>
                    
                    <div class="form">
                    	<label class="w">假期类型：</label>
                    	<select id="leaveType" name="leaveType" class="select_v1">
                            <option value="">全部</option>
                            <option value="1">年假</option>
                            <option value="2">病假</option>
                            <option value="3">婚假</option>
                            <option value="4">哺乳假</option>
                            <option value="5">调休</option>
                            <option value="6">产前假</option>
                            <option value="7">产假</option>
                            <option value="8">流产假</option>
                            <option value="9">陪产假</option>
                            <option value="10">丧假</option>
                            <option value="11">事假</option>
                            <option value="12">其他</option>
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
	                    	<label class="w">状态：</label>
	                    	<select id="approvalStatus" name="approvalStatus" class="select_v1">
	                        </select>
	                </div>
                    
                    <div class="form">
                        <label class="w">工时制：</label>
                        <select id="workType" name="workType" class="select_v1">
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
                
                
                <div class="col">
	                <div class="form">
	                    	<label class="w">销假日期：</label>
	                    	<input id="applyStartDate" type="text" class="Wdate" name="applyStartDate" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
	                 </div>
	                 
	                 <div class="form">
	                    	<label class="w">至&nbsp;&nbsp;</label>
							<input id="applyEndDate" type="text" class="Wdate" name="applyEndDate" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />&nbsp;&nbsp;<a id="lastWeek" style="color:blue;">上周</a>&nbsp;&nbsp;<a id="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a id="lastMonth" style="color:blue;">上月</a>
	                 </div>
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
            <div>
	            <table>
					 <thead>
						<tr>
							<th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">职位名称</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">工时制</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">请假类型</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">销假后假期开始时间</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">销假后假期结束时间</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">假期天数</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">销假开始时间</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">销假结束时间</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">销假天数</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">销假申请日期</th>
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
        <!-- 拒绝弹框 -->
        <div class="commonBox popbox" id="refuseDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>假期拒绝</strong>
						</div>
						<div class="form-main">

							<form id="refuseForm" class="refuseForm">
								<div class="col" style="margin-bottom:0px;">
									<div class="form">
										<label class="w"  style="width:130px;">员工姓名：</label><input type="text"
											class="form-text" id="employeeName" value="" style="border:none;" readonly="true">
									</div>
								</div>
								<div class="col" style="margin-bottom:0px;">
									<div class="form">
										<label class="w"  style="width:130px;">假期类型：</label><input type="text"
											class="form-text" id="leaveType1" value="" style="border:none;" readonly="true">
									</div>
								</div>
								
								<div class="col" style="margin-bottom:0px;">
									<div class="form">
										<label class="w"  style="width:130px;">开始时间：</label><input type="text"
											class="form-text" id="startTime" value="" style="border:none;" readonly="true">
									</div>
								</div>
								
								<div class="col" style="margin-bottom:0px;">
									<div class="form">
										<label class="w"  style="width:130px;">结束时间：</label><input type="text"
											class="form-text" id="endTime" value="" style="border:none;" readonly="true">
									</div>
								</div>
								
								<div class="col" style="margin-bottom:0px;">
									<div class="form">
										<label class="w"  style="width:130px;">请假天数：</label><input type="text"
											class="form-text" id="leaveDays" value="" style="border:none;" readonly="true">
									</div>
								</div>
								
								<div class="col" style="margin-bottom:0px;">
									<div class="form">
										<label class="w"  style="width:130px;">审批意见：</label>
										<textarea id="reason" style="width:178px;height:50px;padding:0;" rows="" cols=""></textarea>
									</div>
								</div>
							</form>

							<div class="col" style="margin-top:30px;">
								<div class="button-wrap ml-4">
									<button id="refuseLeave" class="red-but" processInstanceId="" onclick="refuseLeave(this);"  style="width:120px;">
										<span>拒绝</span>
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
	 
	 <!-- 详情弹框 -->
    <div class="hgc_approvalBox" style="display:none;" id="detailDiv">
        <div class="approval">
            <h2>审批详情<i onclick="closeBigDiv();" class="close"></i></h2>
            <div class="approvalContent clearfix">
                <div class="approvalPerson fl">
                    <div class="selMatter clearfix">
                        <h4 class="fl">申请人</h4>
                        <div class="selarea fr">
                            <span id="detailDiv1"></span>&nbsp;<a onclick="showLeaveDetail();" style="font-size:75%;color:#24b4f5;" href="#">假期详情</a>
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">所属部门</h4>
                        <div id="detailDiv2" class="selarea fr">
                           
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">申请时间</h4>
                        <div id="detailDiv3" class="selarea fr">
                            
                        </div>
                    </div>
                    <h3>原假期信息</h3>
                    <div class="selMatter clearfix">
                        <h4 class="fl">假期类型</h4>
                        <div id="detailDiv4" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">开始时间</h4>
                        <div id="detailDiv5" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">结束时间</h4>
                        <div id="detailDiv6" class="selarea fr">
                          
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">时长</h4>
                        <div id="detailDiv7" class="selarea fr">
                            
                        </div>
                    </div>
                    <h3>销假时间</h3>
                    <div class="selMatter clearfix">
                        <h4 class="fl">开始时间</h4>
                        <div id="detailDiv9" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">结束时间</h4>
                        <div id="detailDiv10" class="selarea fr">
                          
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">时长</h4>
                        <div id="detailDiv11" class="selarea fr">
                           
                        </div>
                    </div>
                </div>
                <div class="approvalProcess fr">
                    <ul id="taskFlow" style="height:350px;">
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
            <div class="col" style="margin-top:30px;" id="detail">
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
    <!-- 详情弹框 -->
    <!-- 详情弹框 -->
     <div class="artical-box-bg" style="display:none;">
        <div class="artical-box">
            <h2>假期详情</h2>
            <div class="artical-module">
                <h3>剩余假期</h3>
                <div class="module-list">
                    <p>
                        <em>
                            <b class="bg-red"></b>年假</em>
                        <span id="allowAnnualLeave"></span>
                    </p>
                    <p>
                        <em>
                            <b class="bg-red"></b>病假</em>
                        <span id="allowSickLeave"></span>
                    </p>
                    <p>
                        <em>
                            <b class="bg-red"></b>调休</em>
                        <span id="allowDayOff"></span>
                    </p>
                </div>
            </div>
            <div class="artical-module">
                <h3>已用假期</h3>
                <div class="module-list">
                    <p>
                        <em>
                            <b class="bg-blue"></b>年假</em>
                        <span id="usedAnnualLeave"></span>
                    </p>
                    <p>
                        <em>
                            <b class="bg-blue"></b>病假</em>
                        <span id="usedSickLeave"></span>
                    </p>
                    <p>
                        <em>
                            <b class="bg-blue"></b>调休</em>
                        <span id="usedDayOff"></span>
                    </p>
                    <p>
                        <em>
                            <b class="bg-blue"></b>事假</em>
                        <span id="affairsLeave"></span>
                    </p>
                </div>
            </div>
            <i class="close" onclick="closeLeaveDetail();"></i>
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
    <div class="commonBox popbox" id="refuseAbateDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>审批</strong>
						</div>
						<div class="form-main">
	
							<form id="refuseAbateDivForm" class="refuseAbateDivForm">
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
									<button class="red-but" onclick="refuseAbate();"  style="width:120px;">
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