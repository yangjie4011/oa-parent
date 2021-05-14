<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>休假登记</title>
<%@include file="../../common/common2.jsp"%>
<link rel="stylesheet" href="http:<%=oaUle%>/ule/oa/c/personsel.css?v=20200602">
<script src="../js/util/personsel.js?v=20200521"></script>
<script type="text/javascript" src="../js/base/leaveManager/leave_apply_register.js?v=20200606"/></script>
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
.form label.w{
	width: 78px;
}
</style>
</head>
<body>
   <div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li id="leaveRegister" class="active"><strong>休假登记</strong></li>
		    <li id="leaveRegisterSearch"><strong>登记查询</strong></li>
		</ul>
	</div>
	<div class="content leaveRegister" style="overflow-x:auto">         
		<div class="popbox-main" style="">
			<div class="cun-pop-content">
				<div class="form-wrap">
					<div class="form-main">
						<div class="col">
							<div class="form">
							<input type="hidden" name="employeeIds" id="relativeemployeeIds">
								<label class="w" >选择员工：</label><input type="text"
									class="form-text" value="" id="relativeclickMe" >
							</div>
						</div>
						<div class="col">
							<div class="form">
		                        <label class="w">假期类型：</label>
		                        <select onchange="changeLeaveType(this);" id="leaveType" class="select_v1">
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
						</div>
						
						<!-- 年假，病假，调休，事假，其它 -->
						<div class="normalLeave">
							<div class="col">
								<div class="form">
									<label class="w" >开始时间：</label><input id="normalStartDate" placeholder="请选择开始时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked:getNoamalLeaveStartHours})"/>
								</div>
								<div class="form">
			                        <label class=""></label>
			                        <select onchange="calNormalLeaveDays();" id="normalStartTime" style="width:100px;" class="select_v1">
	                                        
			                        </select>
			                    </div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >结束时间：</label><input id="normalEndDate" placeholder="请选择结束时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: getNoamalLeaveEndHours})"/>
								</div>
								<div class="form">
			                        <label class=""></label>
			                        <select onchange="calNormalLeaveDays();" id="normalEndTime" style="width:100px;" class="select_v1">
										
			                        </select>
			                    </div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >登记理由：</label>
								</div>
								<textarea id="normalReason" placeholder="请输入登记理由" style="border: 1px solid #d9d9d9; width:176px; height: 55px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8"></textarea>		
							</div>
							<div class="col" style="margin-top:50px;">
								<div class="form">
									<label class="w" >请假天数：</label>
									<input leaveDays="0" id="normalLeaveDays" type="text" class="form-text" readonly="readonly"/>
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button onclick="confirmRegisterLeave();" class="red-but" style="width:120px;"><span>登记</span></button>
								</div>
							</div>
							<input type="hidden" id="token" value="${token}"/>
						</div>
						<!-- 年假，病假，调休，事假，其它 -->
						
						<!-- 婚假 -->
						<div class="marriageLeave" style="display:none;">
							<div class="col">
								<div class="form">
									<label class="w" >开始时间：</label><input id="marriageStartDate" placeholder="请选择开始时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calMarriageLeaveDays})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >结束时间：</label><input id="marriageEndDate" placeholder="请选择结束时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calMarriageLeaveDays})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >登记理由：</label>
								</div>
								<textarea id="marriageReason" placeholder="请输入登记理由" style="border: 1px solid #d9d9d9; width:176px; height: 55px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8"></textarea>		
							</div>
							<div class="col" style="margin-top:50px;">
								<div class="form">
									<label class="w" >请假天数：</label>
									<input leaveDays="0" id="marriageLeaveDays" type="text" class="form-text" readonly="readonly"/>
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button onclick="confirmRegisterLeave();" class="red-but" style="width:120px;"><span>登记</span></button>
								</div>
							</div>
						</div>
						<!-- 婚假 -->
						
						<!-- 哺乳假 -->
						<div class="lactationLeave" style="display:none;">
							<div class="col">
								<div class="form">
									<label class="w" >开始时间：</label><input id="lactationStartDate" placeholder="请选择开始时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calLactationLeaveDays})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >结束时间：</label><input id="lactationEndDate" placeholder="请选择结束时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calLactationLeaveDays})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >子女数：</label>
									<input id="lactationChildrenNum" class="form-text" type="text" readonly="readonly"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >小时数：</label>
									<input id="lactationLeaveHours" type="text" class="form-text" readonly="readonly"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
			                        <label class="w">请假时间：</label>
			                        <select id="lactationTime" class="select_v1">
			                        	
			                        </select>
		                        </div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >请假天数：</label>
									<input leaveDays="0" id="lactationLeaveDays" type="text" class="form-text" readonly="readonly"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >登记理由：</label>
								</div>
								<textarea id="lactationReason" placeholder="请输入登记理由" style="border: 1px solid #d9d9d9; width:176px; height: 55px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8"></textarea>		
							</div>
							<div class="col" style="margin-top:50px;">
								<div class="button-wrap ml-4">
									<button onclick="confirmRegisterLeave();" class="red-but" style="width:120px;"><span>登记</span></button>
								</div>
							</div>
						</div>
						<!-- 哺乳假 -->
						
						<!-- 产前假 -->
						<div class="prenatalLeave" style="display:none;">
							<div class="col">
								<div class="form">
									<label class="w" >开始时间：</label><input id="prenatalStartDate" placeholder="请选择开始时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calPrenatalLeaveDays})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >预产期：</label><input id="prenatalEndDate" placeholder="请选择预产期" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calPrenatalLeaveDays})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >登记理由：</label>
								</div>
								<textarea id="prenatalReason" placeholder="请输入登记理由" style="border: 1px solid #d9d9d9; width:176px; height: 55px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8"></textarea>		
							</div>
							<div class="col" style="margin-top:50px;">
								<div class="form">
									<label class="w" >请假天数：</label>
									<input leaveDays="0" id="prenatalLeaveDays" type="text" class="form-text" readonly="readonly"/>
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button onclick="confirmRegisterLeave();" class="red-but" style="width:120px;"><span>登记</span></button>
								</div>
							</div>
						</div>
						<!-- 产前假 -->
						
						<!-- 产假 -->
						<div class="maternityLeave" style="display:none;">
							<div class="col">
								<div class="form">
									<label class="w" >出生日期：</label><input id="childrenBirthday" placeholder="请选择出生时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calMaternityLeaveDaysByRule})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
			                    	<label class="w">子女数量：</label>
			                    	<input id="maternityChildrenNum" type="number" class="form-text" oninput="calMaternityLeaveDaysByRule();" value="1">
			                    </div>
							</div>
							<div class="col">
								<div class="form">
			                        <label class="w">生产情况：</label>
			                        <select onchange="calMaternityLeaveDaysByRule();" id="livingState" class="select_v1">
			                        	<option value="100">顺产</option>
			                        	<option value="200">难产</option>
			                        </select>
		                        </div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >开始时间：</label><input id="maternityStartDate" placeholder="请选择开始时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd',onpicked:calMaternityLeaveDays})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >结束时间：</label><input id="maternityEndDate" placeholder="请选择结束时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd'})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >登记理由：</label>
								</div>
								<textarea id="maternityReason" placeholder="请输入登记理由" style="border: 1px solid #d9d9d9; width:176px; height: 55px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8"></textarea>		
							</div>
							<div class="col" style="margin-top:50px;">
								<div class="form">
									<label class="w" >请假天数：</label>
									<input leaveDays="0" id="maternityLeaveDays" type="text" class="form-text" readonly="readonly"/>
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button onclick="confirmRegisterLeave();" class="red-but" style="width:120px;"><span>登记</span></button>
								</div>
							</div>
						</div>
						<!-- 产假 -->
						
						<!-- 流产假 -->
						<div class="abortionLeave" style="display:none;">
							<div class="col">
								<div class="form">
									<label class="w" >开始时间：</label><input id="abortionStartDate" placeholder="请选择开始时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calAbortionLeaveDaysByRule})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
			                        <label class="w">妊娠周期：</label>
			                        <select onchange="calAbortionLeaveDaysByRule" id="treatmentCycle" class="select_v1">
			                        	<option value="100">4个月以下</option>
			                        	<option value="200">4个月以上（含4个月）</option>
			                        </select>
		                        </div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >结束时间：</label><input id="abortionEndDate" placeholder="请选择结束时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calAbortionLeaveDays})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >登记理由：</label>
								</div>
								<textarea id="abortionReason" placeholder="请输入登记理由" style="border: 1px solid #d9d9d9; width:176px; height: 55px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8"></textarea>		
							</div>
							<div class="col" style="margin-top:50px;">
								<div class="form">
									<label class="w" >请假天数：</label>
									<input leaveDays="0" id="abortionLeaveDays" type="text" class="form-text" readonly="readonly"/>
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button onclick="confirmRegisterLeave();" class="red-but" style="width:120px;"><span>登记</span></button>
								</div>
							</div>
						</div>
						<!-- 流产假 -->
						
						<!-- 丧假 -->
						<div class="bereavementLeave" style="display:none;">
							<div class="col">
								<div class="form">
									<label class="w" >开始时间：</label><input id="bereavementStartDate" placeholder="请选择开始时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calBereavementLeaveDays})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
			                        <label class="w">亲属关系：</label>
			                        <select onchange="calBereavementLeaveDays();" id="relation" class="select_v1">
			                        	<option value="100">父母</option>
			                        	<option value="200">配偶</option>
			                        	<option value="300">子女</option>
			                        	<option value="400">祖父母</option>
			                        	<option value="500">外祖父母</option>
			                        	<option value="600">兄弟姐妹</option>
			                        	<option value="700">配偶父母</option>
			                        </select>
		                        </div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >结束时间：</label><input id="bereavementEndDate" placeholder="请选择结束时间" type="text" class="Wdate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: calBereavementLeaveDays})"/>
								</div>
							</div>
							<div class="col">
								<div class="form">
									<label class="w" >登记理由：</label>
								</div>
								<textarea id="bereavementReason" placeholder="请输入登记理由" style="border: 1px solid #d9d9d9; width:176px; height: 55px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8"></textarea>		
							</div>
							<div class="col" style="margin-top:50px;">
								<div class="form">
									<label class="w" >请假天数：</label>
									<input leaveDays="0" id="bereavementLeaveDays" type="text" class="form-text" readonly="readonly"/>
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button onclick="confirmRegisterLeave();" class="red-but" style="width:120px;"><span>登记</span></button>
								</div>
							</div>
						</div>
						<!-- 丧假 -->
					</div>
				</div>
			</div>
		</div>  
   </div>
       
   
   <div class="content leaveRegisterSearch" style="overflow-x:auto;display:none;">
        <div class="form-wrap">
			<div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"></i></div>
            	<div class="form-main" style="min-width:1700px;">
	                
                <form id="queryform">
	            <input type="hidden" id="pageNo" name="page" value=""/>
	                <div class="col">
	                    <div class="form">
	                    	<label class="">员工编号：</label>
	                    	<input type="text" class="form-text" name="code" value="">
	                    </div>
	                    <div class="form">
	                    	<label class="">员工姓名：</label>
	                    	<input type="text" class="form-text" name="cnName" value="">
	                    </div>
	                    <div class="form">
	                        <label class="">部门：</label>
	                        <select id="firstDepart" style="width: 150px;"  name="departId" class="select_v1">
	                        </select>
	                    </div>
	                    <div class="form">
	                        <label class="">类型：</label>
	                        <select name="leaveType" style="width:150px;" class="select_v1">
	                        	<option  value="">请选择</option>
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
	               </div>
	               <div class="col">     
	                    <div class="form">
	                    	<label class="">休假日期：</label>
	                    	<input name="startDate" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    	<label>&nbsp;至&nbsp;</label>
							<input name="endDate" type="text" class="Wdate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    </div>
	                </div>
              		</form>
	                
	                <div class="col">
	                    <div class="button-wrap ml-4" >
	                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button>   
	                    </div> 
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
			                 <th style="overflow-x:auto;width:100px;text-align:center;">职位名称</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">工时制</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">请假类型</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">开始时间</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">结束时间</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">请假天数</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">请假事由</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">登记日期</th>
			               	<th style="overflow-x:auto;width:100px;text-align:center;">登记人</th>
			               	
						</tr>
					  </thead>
		              <tbody id="reportList">
		              </tbody>
	            </table>
            </div>
     </div>
     <div class="paging" id="commonPage"></div>       
</body>
</html>