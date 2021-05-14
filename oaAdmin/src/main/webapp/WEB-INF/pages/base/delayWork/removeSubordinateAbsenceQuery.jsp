<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>消下属缺勤查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/delayWork/removeSubordinateAbsenceQuery.js?v=202020526"/></script>
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
			<li id="listTab" rel="#old" class="active"><strong>消下属缺勤查询</strong></li>
		</ul>
	</div>
	<div class="content first" style="overflow-x:auto">
            
            <div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i id="getButton" class="icon arrow-down1" onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
                <input type="hidden" id="pageNo" value=""/>
                
               <input type="hidden"  name="applyType" value="0"/>
               <form id="queryform">
                <div class="form-main">
                <!-- 代办  状态-->
               
                <div class="col">
                    <div class="form">
                    	<label class="w">员工编号：</label>
                    	<input type="text" class="form-text" id="empCode" name="empCode" value="">
                    </div>
                    
                    <div class="form">
                    	<label class="w">员工姓名：</label>
                    	<input type="text" class="form-text" id="empName" name="empName" value="">
                    </div>
                    <div class="form">
                        <label class="w">申诉主管：</label>
                       <input id="leaderName" type="text"  name="leaderName" >
               		 </div>  
                </div>    
                
               <div class="col">
               		   <div class="form">
	                       <label class="w">部门：</label>
	                       <select id="firstDepart" name="departId" class="select_v1 firstDepart">
	                       </select>
	                   </div>
		                <div class="form">
		                    	<label class="w">考勤日期：</label>
		                    	<input  type="text" id="startTime" class="Wdate applyStartDate" name="startTime" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
							
						 </div>
						 <div class="form">		
							 <label class="w">至</label>
								<input  type="text" id="endTime" class="Wdate applyEndDate" name="endTime" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});" />
		                 </div>
               </div> 
                    
                    
                </div>
             </form>
              <div class="col">  
                    <div class="button-wrap ml-4">
                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
                        <button id="export" class="blue-but"><span><i class="icon download"></i>导出</span></button> 
                     </div> 
                </div> 
			</div>
            <div>
	            <table>
					 <thead>
						<tr>
							<th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">消缺勤员工姓名</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">申诉主管</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">出勤多余小时数</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">前一天下班时间</th>			            
			                <th style="overflow-x:auto;width:150px;text-align:center;">考勤日期</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">考勤时间</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">消缺勤小时数</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">消缺勤理由</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">人事批核人</th>
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
      	
      	<div class="hgc_approvalBox" style="display:none;" id="detailDiv">
        <div class="approval">
            <h2>消下属缺勤<i onclick="closeDiv();" class="close"></i></h2>
            <div class="approvalContent clearfix">
                <div class="approvalPerson fl">
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