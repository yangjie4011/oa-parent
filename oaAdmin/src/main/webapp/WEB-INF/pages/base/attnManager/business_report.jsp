<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>出差查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/attnManager/business_report.js?v=202020526"/></script>
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
	                    	
 							<label class="w">出差日期:</label>
	                   	 	<input id="startTime" type="text" class="Wdate" name="startTime" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'});" />
							<label class="w" style="width:30px;">至</label>
							<input id="endTime" type="text" class="Wdate" name="endTime" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startTime\')}'});" />&nbsp;&nbsp;<a id="lastWeek" style="color:blue;">上周</a>&nbsp;&nbsp;<a id="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a id="lastMonth" style="color:blue;">上月</a>
 				
	                    </div>
	                </div> 
	                
	                <div class="col">
	                	<div class="form">
	                    	<label class="w">状态：</label>
	                    	<select id="approvalStatus" name="approvalStatus" class="select_v1">
	                        </select>
	                    </div>
	                    <div class="form">
	                    	<label class="w">交通工具：</label>
	                    	<select id="vehicle" name="vehicle" class="select_v1">
	                    		<option value="">请选择</option>
	                    		<option value="100">火车</option>
	                    		<option value="200">飞机</option>
	                        </select>
	                    </div>
	                </div>
	                
	                <div class="col">
	                	<div class="form">
	                        <label class="w">出差地点：</label>
	                        <input type="text" class="form-text" name="address" value="">
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
				<colgroup>
					<!-- 标题列，可以在这里设置宽度 -->
					<col >
					<col >
					<col >
					<col >
					<col >
					<col >
					<col >
					<col >
					<col >
					<col >
					<col >
					<col >
					<col >
					<col >
					<col >
				 </colgroup>
				 <thead>
					<tr>
						<th style="text-align:center;">员工编号</th>
		                <th style="text-align:center;">员工姓名</th>
		                <th style="text-align:center;">部门</th>
		                <th style="text-align:center;">职位名称</th>
		                <th style="text-align:center;">工时制</th>
		                <th style="text-align:center;">开始时间</th>
		                <th style="text-align:center;">结束时间</th>
		                <th style="text-align:center;">出差天数</th>
		                <th style="text-align:center;">出差地点</th>
		                <th style="text-align:center;">交通工具</th>
		                <th style="text-align:center;">出差事由</th>
		                <th style="text-align:center;">出差总结报告</th>
		                <th style="text-align:center;">申请日期</th>
		                <th style="text-align:center;">批核人</th>
		                <th style="text-align:center;">状态</th>
		                <th style="text-align:center;">详情</th>
					</tr>
				  </thead>
	              <tbody id="reportList">
	              </tbody>
            </table>            
        </div>
        <div class="paging" id="commonPage">
        </div>
        
    
	 <!-- 失效同意弹框 -->
    <div class="commonBox popbox" id="passAbateDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>审批</strong>
						</div>
						<div class="form-main">
	
							<form id="passAbateForm" class="passAbateForm">
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
									<button class="red-but" onclick="passAbate();"  style="width:120px;">
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
	 
	
	<!-- 失效拒绝弹框 -->
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
	
	
	
	
	<!-- 详情弹框 -->
    <div class="hgc_approvalBox" style="display:none;" id="detailDiv">
        <div class="approval" style="height: 500px;">
            <h2>审批详情<i onclick="closeDiv();" class="close"></i></h2>
            <div class="approvalContent clearfix">
                <div class="approvalPerson fl">
                    <div class="selMatter clearfix">
                        <h4 class="fl">申请人</h4>
                        <div class="selarea fr">
                            <span id="detailDiv1"></span>
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
                    <h3>出差计划</h3>
                    <div class="selMatter clearfix">
                        <h4 class="fl">去程日期</h4>
                        <div id="detailDiv4" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">返程日期</h4>
                        <div id="detailDiv5" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">时长</h4>
                        <div id="detailDiv6" class="selarea fr">
                          
                        </div>
                    </div>
                    <div class="selMatter clearfix leave">
                        <h4 class="fl">出差行程</h4>
                        <div id="detailDiv7" class="selarea fr">
                            
                        </div>
                    </div>
                    <div class="selMatter clearfix leave">
                        <h4 class="fl">出差事由</h4>
                        <div id="detailDiv8" class="selarea fr">
                           
                        </div>
                    </div>
                    <div class="selMatter clearfix">
                        <h4 class="fl">交通工具</h4>
                        <div id="detailDiv9" class="selarea fr">
                           
                        </div>
                    </div>
                    
                    <h3>每日行程及工作计划</h3>
                    <div class="selMatter clearfix">
                        <h4 id="detailDiv10">日期</h4>
                    </div>
                </div>
                <div class="approvalProcess fr" style="width: 55%;height: 400px;overflow: auto;">
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
</body>
</html>