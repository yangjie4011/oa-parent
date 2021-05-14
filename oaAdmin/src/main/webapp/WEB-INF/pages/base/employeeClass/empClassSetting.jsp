<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>班次设置</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/employeeClass/empClassSetting.js?v=2019101701"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
</style>
</head>
<body>

	<div class="content" style="overflow-x:auto">
          <table>
			<!-- <colgroup> 
				标题列，可以在这里设置宽度
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col width="1%" display = "none">
			</colgroup> -->
			<button id="insert" class="red-but" style="margin-bottom: 10px;"><span><i class="icon add"></i>添加班次</span></button> 

			<thead>
				<tr>
					<th style='text-align:center;'>班次名称</th>
					<th style='text-align:center;'>简称</th>
					<th style='text-align:center;'>考勤时间</th>
					<th style='text-align:center;'>应出勤工时（小时）</th>
					<th style='text-align:center;'>适用组别</th>
					<th style='text-align:left;'>操作</th>
				</tr>
			</thead>
			<tbody id="reportList">
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
		</table>
		 <input type="hidden" id="pageNo" name="page" value=""/>
		<div class="paging" id="commonPage"></div>
    </div>
	
	<!-- 改 删 操作框 -->
	<!-- 修改开始-->
   	<!-- 更新 -->
	<div class="commonBox popbox" id="insertDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
					
							<div class="title">
								<strong><i class="icon add-user"></i>添加班次</strong>
							</div>
						
						<div class="form-main">

							<form id="insretForm" class="insretForm">
								<input type="hidden" id="isInterDay" name="isInterDay" value=""/>
								
								<input type="hidden" id="version" name="version" value=""/>
								
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">班次名称:</label><input type="text"
											class="form-text" name="fullName" value="" >
									</div>
								</div>
							
								<div class="col">																		
									<div class="form">
										<label class="w"  style="width:130px;">班次简称:</label><input type="text"
											class="form-text" name="name" >
									</div>
								</div>
								
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">上班时间:</label>
										<input  type="text" class="Wdate" name="startTime" id="startTime" onfocus="WdatePicker({dateFmt:'HH:mm',onpicked: calculatedHours})" 
							      			 readonly="readonly" onclick="WdatePicker({dateFmt:'HH:mm',onpicked: calculatedHours})"/>
									</div>
								</div>	
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">下班时间:</label>
										<input  type="text" class="Wdate" name="endTime" id="endTime"  onfocus="WdatePicker({dateFmt:'HH:mm',onpicked: calculatedHours})" 
	                    	      			 readonly="readonly" onclick="WdatePicker({dateFmt:'HH:mm',onpicked: calculatedHours})" />	
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">应出勤工时:</label>
										<input type="text" class="form-text" readonly="readonly" id="shouldTime" name="mustAttnTime" >
									</div>
								</div>
								
								<div class="col">
									<div class="form">
										 <label class="w"  style="width:130px;">适用组别:</label>
										 
										 <span class="ctrl-mselect" style="margin-left:130px;width: 190px;">
									    	<ul id="selectGroupIds" >
									    		 <c:forEach items="${scheduleList}" var="i" >
									    		 	<li><label><input type="checkbox" value="${i.id}">${i.departName}_${i.name}</label></li>
									    		 </c:forEach>
									    	</ul>
									    </span>
									 </div>   
								</div>
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button id="zcqr" class="red-but" onclick="save();"  style="width:120px;">
										<span>保存</span>
									</button>
									<button id="zcqx" class="small grey-but"
										onclick="closeDiv();" style="width:120px;">
										<span>返回</span>
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
	
	
	<!-- 修改 -->
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
					
							<div class="title">
								<strong><i class="icon add-user"></i>修改班次</strong>
							</div>
						
						<div class="form-main">

							<form id="updateForm" class="updateForm">
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">班次名称:</label><input type="text"
											class="form-text" name="fullName" value="" >
											<input type="hidden" name="id" id="updateClassId" />
									</div>
								</div>
							 
								<div class="col">																		
									<div class="form">
										<label class="w"  style="width:130px;">班次简称:</label><input type="text"
											class="form-text" name="name" >
									</div>
								</div>
								
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">上班时间:</label>
										<input  type="text" class="Wdate" name="startTime" readonly="readonly" />
									</div>
								</div>	
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">下班时间:</label>
										<input  type="text" class="Wdate" name="endTime"  readonly="readonly"  />	
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">应出勤工时:</label>
										<input type="text" class="form-text" readonly="readonly"  name="mustAttnTime" >
									</div>
								</div>
								<div class="col">
									<div class="form">
										 <label class="w"  style="width:130px;">适用组别:</label>
										 
										 <span class="ctrl-mselect" style="margin-left:130px;width: 190px;">
									    	<ul id="updateGroupIds" >
									    		 <c:forEach items="${scheduleList}" var="i" >
									    		 	<li id="groupIds${i.id}"><label><input type="checkbox" id="checkboxs${i.id}" value="${i.id}">${i.departName}_${i.name}</label></li>
									    		 </c:forEach>
									    	</ul>
									    </span>
									 </div>   
								</div>
								
								
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button id="zcqr" class="red-but" onclick="updateClassName();"  style="width:120px;">
										<span>保存</span>
									</button>
									<button id="zcqx" class="small grey-but"
										onclick="closeDiv();" style="width:120px;">
										<span>返回</span>
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
	
	
	
	
	<div class="commonBox popbox" id="reconfirmDiv" style="display:none">
		<div class="popbox-bg"></div>    
		<div class="popbox-center" style="top: 10%; left: 30%">  
			<div class="popbox-main" style="">
				<div class="cun-pop-content"> 
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>再次确认</strong>
							<i onclick="closeCounterDiv();" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
							
						</div>
						<div class="form-main">

							<form id="addForm" class="updateEnableForm">
								<input type="hidden" id="classId" value="">
								<input type="hidden" id="enableFlagId" value="">
								<div class="col">
									<div class="form">
										<label class="w"  style="width:180px;">确认<span id="enableStatus">班次  </span><span id="enableClassName"></span>?</label>
										<span id="pb_dateAfter"></span>
									</div>
								</div>	
								
							</form>

							<div class="col">
								 <button class="red-but btn-middle" onclick="updateSetting()"><span><i class="icon"></i>确定</span></button> 
              					 <button class="red-but btn-middle" onclick="closeCounterDiv()"><span><i class="icon"></i>取消</span></button>      
							</div>
						</div>
					</div>
					<!-- form-main end -->
				</div>
			</div>
		</div>
	</div>	
	
	<div class="commonBox popbox" id="delectDiv" style="display:none">
		<div class="popbox-bg"></div>    
		<div class="popbox-center" style="top: 10%; left: 30%">  
			<div class="popbox-main" style="">
				<div class="cun-pop-content"> 
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>再次确认</strong>
							<i onclick="closeCounterDiv();" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
							
						</div>
						<div class="form-main">

							<form id="addForm" class="updateForm">
								<input type="hidden" id="delFlagId" value="">
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">确认删除班次 <span id="delClassName"></span>？</label>
									</div>
								</div>	
								
							</form>

							<div class="col">
								 <button class="red-but btn-middle" onclick="deleteSave()"><span><i class="icon"></i>确定</span></button> 
              					 <button class="red-but btn-middle" onclick="closeCounterDiv()"><span><i class="icon"></i>取消</span></button>      
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