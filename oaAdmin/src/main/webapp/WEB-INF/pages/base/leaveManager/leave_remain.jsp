<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>剩余假期</title>
<%@include file="../../common/common2.jsp"%>
<link href="<%=basePath %>css/common/table.css?v=20181220" rel="stylesheet"/>
<script type="text/javascript" src="../js/base/leaveManager/leave_remain.js?v=20190924"/></script>
<style type="text/css"> 
	th,td {
	text-align:center !important; 
	word-break: keep-all;
	white-space:nowrap;
	}
	.redfont{
		color: red;
	}
	.tdRightSolid{
		border-right:#333 solid 1px;
	}
</style>
</head>
<body>
	<div class="content" style="overflow-x:auto">
		
		<div class="form-wrap">
			    <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
            	<input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	            <input type="hidden" id="pageNo" value=""/>
                <form id="queryform">
            	
            	 <div class="form-main">
	                
	                <div class="col">
	                    <div class="form">
	                    	<label class="w">员工编号：</label>
	                    	<input type="text" class="form-text" name="code" value="">
	                    </div>
	                    
	                    <div class="form">
	                    	<label class="w">员工姓名：</label>
	                    	<input type="text" class="form-text" name="cnName" value="">
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
	                        <label class="w">工时制：</label>
	                        <select id="workType" name="workType" class="select_v1">
	                        </select>
	                    </div>
	                    
	                    <div class="form">
	                    	<label class="w">状态：</label>
	                    	<select id="jobStatus" name="jobStatus" class="select_v1">
	                    		<option value="">全部</option>
								<option value="0">在职</option>
								<option value="1">离职</option>
								<option value="2">待离职</option>
	                        </select>
	                    </div>
	                    
	                    <div class="form">
	                    	<label class="w">年份：</label>
	                    	<input id="year" type="text" class="Wdate" name="year" onClick="WdatePicker({dateFmt:'yyyy'})" readonly="readonly" value="${nowYear}" onfocus="WdatePicker({dateFmt:'yyyy'})";  />
	                	</div>
	                	
	        
	                </div>  
	                <div class="col">
	                   <div class="form">
	                    	<label class="">入职日期：</label>
	                    	<input id="startTime" type="text" class="Wdate" name="firstEntryTimeBegin" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    </div>
	                    
	                    <div class="form">
	                        <label class="margin-left">至&nbsp;&nbsp;</label>
							<input id="endTime" type="text" class="Wdate" name="firstEntryTimeEnd" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
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
	                <div class="col">  
	                    <div class="button-wrap ml-4">    
	                        <div style="visibility: none;">
								<form enctype="multipart/form-data" id="batchUpload"  action="empLeave/importEmpLeaveList.html" method="post" class="form-horizontal">    
								    <button class="btn btn-success btn-sm" id="uploadEventBtn"  type="button" >选择文件</button>  
								    <input type="file" name="file"  style="width:0px;height:0px;" id="uploadEventFile">  
									    <div class="form">
									    	<input id="uploadEventPath" class="form-text" disabled="disabled"  type="text" placeholder="--- 未选择excel表 ---" style="font-size:15px;text-align:center;font-color:gray;font-style:oblique" >                                           
									    </div>
								    <button type="button" class="btn btn-success btn-sm"  onclick="signExcel.uploadBtn()" >上传</button>
								    <a class="btn-blue icon-xiazai btn-large" align ="left" href="<%=basePath %>/template/试用部门假期信息.xlsx">下载模板</a>
								</form> 
	                    	</div>         
	                        <button style="display:none;" id="exportNew" class="blue-but"><span><i class="icon add"></i>导出</span></button>          
	                    </div> 
	                </div> 
                </div>
			</div>
			
			<div class="hgc_restHoliday" id="hgc_restHoliday">
                <ul>
		           <li class="title">
		                <div class="baseInfo clearfix">
		                    <span style="background: none;" class="showHolidayInfoBtn"></span>
		                    <span>员工编号</span>
		                    <span>员工姓名</span>
		                    <span>入职日期</span>
		                    <span>部门</span>
		                    <span>离职日期</span>
		                    <span>在职状态</span>
		                    <span>操作</span>
		                </div>
		            </li>
                </ul>
            </div>			
 </br>
   <div class="paging" id="commonPage"></div>
	
	<div class="commonBox popbox" id="counter" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top:10%; right: 0% ;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
				<i onclick="closeCounterDiv();" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
					<div class="form-wrap">
						<div class="title">
							<strong>假期计算器</strong> 
						</div>
						<div class="form-main">
							<table>
								<tr>			
									<td style="width: 50%;padding:6px 0px;">员工编号:<span id="countCode" class="spanClass"></span></td>
									<td colspan="2" style="padding:6px 0px;">离职日期:<input id="countQuitTime" type="text" class="Wdate redfont" name="quitTime" onchange="quitCount()"  onfocus="WdatePicker({skin:'whyGreen'})"/>
										<input id="countemployeeId" type="hidden">
										&nbsp;&nbsp;&nbsp;
										<input type="button" style='height:22px;width:40px;background-color:#ff4046;color:white;font-weight:bold;border-radius:10px;' value="查询"/>
									</td>
								</tr>
								<tr>
									<td style="padding:6px 0px;">员工姓名：<span id="countCnName" class="spanClass"></span></td>
									<td style="padding:6px 0px;">入职日期：<span id="firstEntryTime" class="spanClass"></span></td>
								</tr>
								<tr>
									<td class="tdRightSolid" style="padding:6px 0px;">当年年假总天数：<span id="totalDays" class="spanClass"></span>天</td>
									<td style="padding:6px 0px;">当年带薪病假总天数：<span id="totalSickDays" class="spanClass"></span>天</td>
								</tr>
								<tr>
									<td style="padding:6px 0px;" class="tdRightSolid">截止离职日期年假：<span id="toQuitTotalLeaveDays" class="spanClass redfont"></span>天</td>
									<td style="padding:6px 0px;">截止离职日期带薪病假：<span id="toQuitTotalSickDays" class="spanClass redfont"></span>天</td>
								</tr>
								<tr>
									<td style="padding:6px 0px;" class="tdRightSolid">已休当年年假：<span id="totalUsedLeaveDays" class="spanClass"></span>天</td>
									<td style="padding:6px 0px;">已休带薪病假：<span id="uesdPaidSickDays" class="spanClass"></span>天</td>
								</tr>
								<tr>
									<td style="padding:6px 0px;" class="tdRightSolid">剩余当年年假：<span id="totalAllowRemainDays" class="spanClass redfont"></span>天</td>
									<td style="padding:6px 0px;">剩余带薪病假：<span id="allowPaidSickDays" class="spanClass redfont"></span>天</td>
								</tr>
								<tr>
									<td style="padding:6px 0px;" class="tdRightSolid">剩余法定年假：<span id="legalLeaveDays" class="spanClass redfont"></span>天</td>
									<td style="padding:6px 0px;">已休非带薪病假：<span id="uesdUnPaidSickDays" class="spanClass"></span>天</td>
								</tr>
									<td style="padding:6px 0px;" class="tdRightSolid">剩余福利年假：<span id="welfareLeaveDays" class="spanClass redfont"></span>天</td>
									<td style="padding:6px 0px;">冻结带薪病假天数：<span id="sickBlockDays" class="spanClass"></span>天</td>
								<tr>
									<td style="padding:6px 0px;" class="tdRightSolid"></td>							
									<td style="padding:6px 0px;"></td>
								</tr>
								<tr>
									<td style="padding:6px 0px;" class="tdRightSolid">去年剩余年假：<span id="lastYearTotalAllowRemainDays" class="spanClass"></span>天</td>
									<td style="padding:6px 0px;">冻结非带薪病假天数：<span id="unSickBlockDays" class="spanClass"></span>天</td>
								</tr>
								<tr>
									<td style="padding:6px 0px;" class="tdRightSolid">去年剩余法定年假：<span id="lastYearLegalLeaveDays" class="spanClass"></span>天</td>
									<td style="padding:6px 0px;"></td>
								</tr>
								<tr>
									<td style="padding:6px 0px;" class="tdRightSolid">去年剩余福利年假：<span id="lastYearWelfareLeaveDays" class="spanClass"></span>天</td>
									<td style="padding:6px 0px;"></td>
								</tr>
								<tr>
									<td style="padding:6px 0px;" class="tdRightSolid">冻结年假天数：<span id="totalBlockedDays" class="spanClass"></span>天</td>
									<td style="padding:6px 0px;"></td>
								</tr>
								
								<tr>
									<td style="padding:6px 0px;" class="tdRightSolid"></td>
									<td style="padding:6px 0px;"></td>
								</tr>
							</table>
						</div>
					<!-- form-main end -->
				</div>
			</div>
		</div>
	</div>         
</div>

<input id="employeeId" type="hidden">

 <!-- 修改弹框 -->
  <div class="commonBox popbox" id="updateYearDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="overflow-y:auto;height:400px;">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>年假修改</strong>
						</div>
						<div class="form-main">
	
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w">员工编号：</label>
										<input type="text" class="form-text codeUpdate" value="" readonly="true">
									</div>
								    <div class="form">
										<label class="w">员工姓名：</label>
										<input type="text" class="form-text nameUpdate" value="" readonly="true">
									</div>
								</div>
						</div>		
						<div class="title">
							 <strong><i class="icon"></i>当年年假</strong>
						</div>
					    <div class="form-main">
								
							
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:70px;">法定总数：</label>
										<div class="num_btn">
										    <input id="leave2_old" type="hidden">
										    <input id="leave2" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
								       </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">-</label>
									</div>
									<div class="form">
										<label class="w"  style="width:70px;">法定已用：</label>
										<div class="num_btn">
										    <input id="leave14_old" type="hidden">
										    <input id="leave14" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
								       </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">=</label>
									</div>
									<div class="form">
										<label class="w"  style="width:70px;">法定剩余：</label>
										<input type="text" style="width:50px;" class="form-text" id="leave7" value="" readonly="true">&nbsp;天
									</div>
								</div>
								
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:70px;">福利总数：</label>
									    <div class="num_btn">
									        <input id="leave3_old" type="hidden">
										    <input id="leave3" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
								        </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">-</label>
									</div>
									<div class="form">
										<label class="w"  style="width:70px;">福利已用：</label>
										<div class="num_btn">
									     	<input id="leave15_old" type="hidden">
										    <input id="leave15" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
								        </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">=</label>
									</div>
									<div class="form">
										<label class="w"  style="width:70px;">福利剩余：</label>
										<input type="text" style="width:50px;" class="form-text" id="leave8" value="" readonly="true">&nbsp;天
									</div>
								</div>
							</div>
							<div class="title">
						        <strong><i class="icon"></i>去年年假</strong>
					        </div>
							<div class="form-main">
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:70px;">法定总数：</label>
										<div class="num_btn">
										    <input id="leave32_old" type="hidden">
										    <input id="leave32" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
								        </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">-</label>
									</div>
									<div class="form">
										<label class="w"  style="width:70px;">法定已用：</label>
										<div class="num_btn">
									    	<input id="leave34_old" type="hidden">
										    <input id="leave34" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
								        </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">=</label>
									</div>
									<div class="form">
										<label class="w"  style="width:70px;">法定剩余：</label>
										<input type="text" style="width:50px;" class="form-text" id="leave5" value="" readonly="true">&nbsp;天
									</div>
								</div>
								
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:70px;">福利总数：</label>
										<div class="num_btn">
										    <input id="leave33_old" type="hidden">
										    <input id="leave33" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
								        </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">-</label>
									</div>
									<div class="form">
										<label class="w"  style="width:70px;">福利已用：</label>
										<div class="num_btn">
										    <input id="leave35_old" type="hidden">
										    <input id="leave35" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
								        </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">=</label>
									</div>
									<div class="form">
										<label class="w"  style="width:70px;">福利剩余：</label>
										<input type="text" style="width:50px;" class="form-text" id="leave6" value="" readonly="true">&nbsp;天
									</div>
								</div>
						</div>
						<div class="form-main">
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:70px;">备注：</label>
										<input type="text" class="form-text" id="remarkYear" value="">
									</div>
								</div>	

								<div class="col" style="margin-top:30px;">
									<div class="button-wrap ml-4">
										<button class="small red-but" onclick="confirmYearLeave();">
											<span>确定</span>
										</button>
										<button class="small grey-but"
											onclick="closeDiv();">
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
	<!-- 修改弹框 -->
	
  <!-- 修改弹框 -->
  <div class="commonBox popbox" id="updateSickDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>已用病假修改</strong>
						</div>
						<div class="form-main">
	
							<form>
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w">员工编号：</label>
										<input type="text" class="form-text codeUpdate"  value="" readonly="true">
									</div>
								    <div class="form">
										<label class="w">员工姓名：</label>
										<input type="text" class="form-text nameUpdate"  value="" readonly="true">
									</div>
								</div>
								
								
							
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:130px;">当年带薪已用：</label>
										<div class="num_btn">
										    <input id="leave17_old" type="hidden">
										    <input id="leave17" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
								    <div class="form">
										<label class="w"  style="width:92px;"></label>
								    </div>
									<div class="form">
										<label class="w"  style="width:130px;">当年非带薪已用：</label>
										<div class="num_btn">
										    <input id="leave18_old" type="hidden">
										    <input id="leave18" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
								</div>
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w">备注：</label>
										<input type="text" class="form-text" id="remarkSick" value="">
									</div>
								</div>	
							</form>
	
							<div class="col" style="margin-top:30px;">
								<div class="button-wrap ml-4">
									<button class="small red-but" onclick="confirmSickLeave();">
										<span>确定</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv();">
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
	<!-- 修改弹框 -->
	
   <!-- 修改弹框 -->
  <div class="commonBox popbox" id="updateOffDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>调休修改</strong>
						</div>
						<div class="form-main">
	
							<form>
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w">员工编号：</label>
										<input type="text" class="form-text codeUpdate" value="" readonly="true">&nbsp;&nbsp;&nbsp;
									</div>
								    <div class="form">
										<label class="w">员工姓名：</label>
										<input type="text" class="form-text nameUpdate" value="" readonly="true">
									</div>
								</div>
							
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w">当年剩余：</label>
										<div class="num_btn">
										    <input id="leave11_old" type="hidden">
										    <input id="leave11" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:30px;">小时</label>
								    </div>
									<div class="form">
										<label class="w">当年已用：</label>
										<div class="num_btn">
										    <input id="leave20_old" type="hidden">
										    <input id="leave20" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:30px;">小时</label>
								    </div>
								    <div class="form">
										<label class="w">去年剩余：</label>
										<div class="num_btn">
										    <input id="leave36_old" type="hidden">
										    <input id="leave36" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:30px;">小时</label>
								    </div>
									<div class="form">
										<label class="w">去年已用：</label>
										<div class="num_btn">
										    <input id="leave37_old" type="hidden">
										    <input id="leave37" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:30px;">小时</label>
								    </div>
								</div>
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w">备注：</label>
										<input type="text" class="form-text" id="remarkOff" value="">
									</div>
								</div>	
							</form>
	
							<div class="col" style="margin-top:30px;">
								<div class="button-wrap ml-4">
									<button class="small red-but" onclick="confirmOffLeave();">
										<span>确定</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv();">
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
	<!-- 修改弹框 -->
	
	 <!-- 修改弹框 -->
  <div class="commonBox popbox" id="updateOtherDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>其他修改</strong>
						</div>
						<div class="form-main">
	
							<form>
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w">员工编号：</label>
										<input type="text" class="form-text codeUpdate"  value="" readonly="true">
									</div>
								    <div class="form">
										<label class="w">员工姓名：</label>
										<input type="text" class="form-text nameUpdate"  value="" readonly="true">
									</div>
								</div>
							
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w">事假：</label>
										<div class="num_btn">
										    <input id="leave19_old" type="hidden">
										    <input id="leave19" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
									<div class="form">
										<label class="w">婚假：</label>
										<div class="num_btn">
										    <input id="leave21_old" type="hidden">
										    <input id="leave21" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
								    <div class="form">
										<label class="w">丧假：</label>
										<div class="num_btn">
										    <input id="leave22_old" type="hidden">
										    <input id="leave22" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
								</div>
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w">陪产假：</label>
										<div class="num_btn">
										    <input id="leave23_old" type="hidden">
										    <input id="leave23" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
									<div class="form">
										<label class="w">产前假：</label>
										<div class="num_btn">
										    <input id="leave24_old" type="hidden">
										    <input id="leave24" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
									<div class="form">
										<label class="w">产假：</label>
										<div class="num_btn">
										    <input id="leave25_old" type="hidden">
										    <input id="leave25" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
								</div>
								<div class="col" style="margin-bottom:5px;">
								    <div class="form">
										<label class="w">哺乳假：</label>
										<div class="num_btn">
										    <input id="leave26_old" type="hidden">
										    <input id="leave26" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
									<div class="form">
										<label class="w">流产假：</label>
										<div class="num_btn">
										    <input id="leave27_old" type="hidden">
										    <input id="leave27" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
									<div class="form">
										<label class="w">其他：</label>
										<div class="num_btn">
										    <input id="leave28_old" type="hidden">
										    <input id="leave28" type="text" class="num" value="" style="text-indent: 0px;">
										    <div class="my_rl">
										        <span class="add" onclick="add(this);">+</span>
										        <span class="reduce" onclick="reduce(this);">-</span>
										    </div>
							            </div>
									</div>
									<div class="form">
										<label class="w"  style="width:20px;">天</label>
								    </div>
								</div>
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w">备注：</label>
										<input type="text" class="form-text" id="remarkOther" value="" style="text-indent: 0px;">
									</div>
								</div>
							</form>
	
							<div class="col" style="margin-top:30px;">
								<div class="button-wrap ml-4">
									<button class="small red-but" onclick="confirmOtherLeave();">
										<span>确定</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv();">
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
	<!-- 修改弹框 -->
	<!-- 设置弹窗 -->
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>导入详情</strong>
						</div>
							<textarea id="importMsg" maxlength="800" warp="virtual" style="border:0px"></textarea>
						<div class="col">
							<div class="button-wrap ml-4" >
								<button class="small grey-but"
									onclick="closeDiv();" style="float:right">
									<span>返回</span>
								</button>
							</div>
						</div>
			        </div>
		        </div>
		    </div>
		</div>
	</div>
	<!-- 设置弹窗 -->
	
	<!-- 确认弹框弹窗 -->
	<div class="commonBox popbox" id="confirmDiv1" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 40%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>请确认是否将</strong>
						</div>
							<textarea id="confirmMsg1" style="border:0px"></textarea>
						<div class="col">
							<div class="button-wrap ml-4" >
								<button class="red-but" onclick="saveYearLeave();"  style="width:120px;">
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
		    </div>
		</div>
	</div>
	<!-- 确认弹框弹窗 -->
	
	<!-- 确认弹框弹窗 -->
	<div class="commonBox popbox" id="confirmDiv2" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 40%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>请确认是否将</strong>
						</div>
							<textarea id="confirmMsg2" style="border:0px"></textarea>
						<div class="col">
							<div class="button-wrap ml-4" >
								<button class="red-but" onclick="saveSickLeave();"  style="width:120px;">
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
		    </div>
		</div>
	</div>
	<!-- 确认弹框弹窗 -->
	<!-- 确认弹框弹窗 -->
	<div class="commonBox popbox" id="confirmDiv3" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 40%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>请确认是否将</strong>
						</div>
							<textarea id="confirmMsg3" style="border:0px"></textarea>
						<div class="col">
							<div class="button-wrap ml-4" >
								<button class="red-but" onclick="saveOffLeave();"  style="width:120px;">
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
		    </div>
		</div>
	</div>
	<!-- 确认弹框弹窗 -->
	<!-- 确认弹框弹窗 -->
	<div class="commonBox popbox" id="confirmDiv4" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 40%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>请确认是否将</strong>
						</div>
							<textarea id="confirmMsg4" style="border:0px"></textarea>
						<div class="col">
							<div class="button-wrap ml-4" >
								<button class="red-but" onclick="saveOtherLeave();"  style="width:120px;">
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
		    </div>
		</div>
	</div>
	<!-- 确认弹框弹窗 -->
</body>
</html>