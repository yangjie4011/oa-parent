<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<link rel="icon" href="data:;base64,=">
<title>员工查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/emp/empQueryList.js?v=20190917"/></script>
<style type="text/css">
	.areaon{color:#fff; background:yellow;}
	.margin-left{margin-left:10px;}
	th,td {
	word-break: keep-all;
	white-space:nowrap;
	}
	
	label{
	width: 85px;
	}
	.select_v1{
		height:36px;width:150px;
		margin-left: 0px;
	}
	
	.active {
      color: white;
      background-color: black;
    }
    .none {
      background-color: whitesmoke;
    }

	input.select_v1{
		width:150px;
	}
	input.Wdate{
		width:148px;
	}
	input{
		width:150px;
	}
</style>
</head>
<body>
	<div class="content"><!-- style="width: 1710px;" -->
		<div class="form-wrap">
			<div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"></i></div>
            	<div class="form-main" style="min-width:1700px;">
	                
                <form id="queryform">
	            <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	            <input type="hidden" id="pageNo" name="page" value=""/>
	                <div class="col">
	                    <div class="form">
	                    	<label class="">员工编号：</label>
	                    	<input type="text" style="width:150px;" class="form-text" name="code" value="">
	                    </div>
	                    <div class="form">
	                    	<label class="">员工姓名：</label>
	                    	<input type="text" style="width:150px;" class="form-text" name="cnName" value="">
	                    </div>
	                	<div class="form">
	                        <label class="">工时制：</label>
	                        <select id="workType" style="height:36px;width:150px;" name="workType" class="select_v1">
	                        </select>
	                    </div>
	                    <div class="form">
	                        <label class="">员工类型：</label>
	                        <select id="empTypeId" style="height:36px;width:150px;"name="empTypeId" class="select_v1">
	                        </select>
	                    </div>
	                </div>   
	                
	                <div class="col">
	                	<div class="form">
	                        <label class="">性   别：</label> 
	                        <select id="sex" name="sex" class="select_v1"><option value="">请选择</option>
								<option value="0">男</option>
								<option value="1">女</option>
							</select>
						</div>
	                	<div class="form">
	                        <label class=""> 婚姻状况： </label>
	                        <select id="maritalStatus" name="maritalStatus" class="select_v1"><option value="">请选择</option>
							</select>
	                    </div>
	                	<div class="form">
	                        <label class="">在职状态：</label>
	                        <select id="jobStatus" name="jobStatus" class="select_v1"><option value="">请选择</option>
								<option value="0">在职</option>
								<option value="1">离职</option>
								<option value="2">待离职</option>
							</select>
	                    </div>
	                	<div class="form">
	                        <label class="">学&nbsp;&nbsp;历：</label>
	                        <select id="degreeOfEducation" name="degreeOfEducation" class="select_v1">
	                        </select>
	                    </div>
	                </div> 
	                
	                <div class="col">
	                    <div class="form">
	                        <label class="">部门：</label>
	                        <select id="firstDepart" name="firstDepart" class="select_v1">
	                        </select>
	                    </div>
	                    
	                    <div class="form" id="secondDepartDiv" style="display:none">
	                        <select id="secondDepart" name="secondDepart" class="select_v1">
	                        </select>
	                    </div>
	                	<div class="form">
	                        <label class="">汇报对象：</label>
	                        <select id="employeeLeader" name="reportToLeader" class="select_v1">
	                        </select>
	                    </div>
	                	<div class="form">
	                        <label class="">职位：</label>
	                        <select id="position" name="positionId" class="select_v1">
	                        </select>
	                    </div>
	                	<div class="form">
	                        <label class="">年龄：</label>
	                    	<input type="number" style="width:150px;" class="form-text" name="age" value="">
	                    </div>
	                </div>   
	                
	                <div class="col">
	                    <div class="form">
	                    	<label class="">合同到期日：</label>
	                    	<input id="startTime" type="text" class="Wdate" name="protocolEndTimeBegin" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    	<label class="margin-left"> &nbsp;至&nbsp;&nbsp;</label>
							<input id="endTime" type="text" class="Wdate" name="protocolEndTimeEnd" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    </div>
	                    
	                    <div class="form">
	                    	<label class="">入职日期：</label>
	                    	<input id="startTime" type="text" class="Wdate" name="firstEntryTimeBegin" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    	<label class="margin-left"> &nbsp;至&nbsp;&nbsp;</label>
							<input id="endTime" type="text" class="Wdate" name="firstEntryTimeEnd" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    </div>
	                </div> 
	                
	                <div class="col">
	                    <div class="form">
	                    	<label class="">试用到期日：</label>
	                    	<input id="startTime" type="text" class="Wdate" name="probationEndTimeBegin" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    	<label class="margin-left"> &nbsp;至&nbsp;&nbsp;</label>
							<input id="endTime" type="text" class="Wdate" name="probationEndTimeEnd" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    </div>
	                    
	                    <div class="form">
	                    	<label class="">离职日期：</label>
	                    	<input id="startTime" type="text" class="Wdate" name="quitTimeBegin" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    	<label class="margin-left"> &nbsp;至&nbsp;&nbsp;</label>
							<input id="endTime" type="text" class="Wdate" name="   " onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    </div>
	                </div>
              		</form>
	                

	                <div class="col">  <!-- style="display:inline-block; margin:16px 0px 0px 0px;" -->
	                    <div class="button-wrap ml-4"  style="margin-left:-60px">
	                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
	                        <button id="export" class="blue-but"><span><i class="icon add"></i>导出</span></button>    
	                        <button id="updateBatch" class="blue-but"><span><i class="icon edit"></i>批量修改</span></button>        
							<div style="visibility: none;">
								<form enctype="multipart/form-data" id="batchUpload"  action="employee/importEmployeeList.html" method="post" class="form-horizontal">    
								    <button class="btn btn-success btn-sm" id="uploadEventBtn"  type="button" >选择文件</button>  
								    <input type="file" name="file"  style="width:0px;height:0px;" id="uploadEventFile">  
								    	<div class="form">
									    	<input id="uploadEventPath" class="form-text" disabled="disabled"  type="text" placeholder="--- 未选择excel表 ---" style="font-size:15px;text-align:center;font-color:gray;font-style:oblique" >                                           
									    </div>                                           
									<button type="button" class="btn btn-success btn-sm"  onclick="signExcel.uploadBtn()" >上传</button> 
									<a class="btn-blue icon-xiazai btn-large" align ="left" href="<%=basePath %>/template/初始化员工信息.xlsx">下载模板</a>
								</form>
	                    	</div> 
	                    </div> 
	                </div>
                </div>
			</div>

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
			<thead>
				<tr>
					<th style='text-align:left;'><input id="checkAll" type ='checkbox'/></th>
					<th style='text-align:center;'>员工编号</th>
					<th style='text-align:center;'>姓名</th>
					<th style='text-align:center;'>员工类型</th>
					<th style='text-align:center;'>部门</th>
					<th style='text-align:center;'>职位</th>
					<th style='text-align:center;'>工时制</th>
					<th style='text-align:center;'>状态</th>
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
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
		</table>
	<!-- 改 删 操作框 -->
	<!-- 修改开始-->
   	<!-- 更新 -->
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>员工离职操作</strong>
						</div>
						<div class="form-main">

							<form id="updateForm" class="updateForm">
								<input type="hidden" id="id" name="id" value=""/>
								<input type="hidden" id="version" name="version" value=""/>
								<input type="hidden" id="firstEntryTime" name="firstEntryTime" value=""/>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">离职人员</label><input type="text"
											class="form-text" name="cnName" value="" readonly="true">
									</div>
								</div>
							
								<div class="col">																		
									<div class="form">
										<label class="w"  style="width:130px;">所属部门</label><input type="text"
											class="form-text" name="departName" readonly="true">
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">职位信息</label><input type="text"
											class="form-text" name="positionName" readonly="true">
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">离职日期</label>
										<input id="quitTime" type="text" class="Wdate" name="quitTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	      			 readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
									</div>
								</div>
								<!-- <div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">工资结算日期</label>
										<input id="salaryBalanceDate" type="text" class="Wdate" name="salaryBalanceDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							      			 readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
									</div>
								</div>	 -->
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button id="zcqr" class="red-but" onclick="reconfirmQuit();"  style="width:120px;">
										<span>保存</span>
									</button>
									<button id="zcqx" class="small grey-but"
										onclick="closeDiv('updateDiv');" style="width:120px;">
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
	<!-- 修改指纹id  -->
	<div class="commonBox popbox" id="updateDiv2" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>更新员工指纹ID</strong>
						</div>
						<div class="form-main">

							<form id="updateForm2" class="updateForm">
								<input type="hidden" id="id2" name="id" value=""/>
								<input type="hidden" id="version2" name="version" value=""/>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">员工姓名</label><input type="text"
											class="form-text" name="cnName" value="" readonly="true">
									</div>
								</div>
							
								<div class="col">																		
									<div class="form">
										<label class="w"  style="width:130px;">所属部门</label><input type="text"
											class="form-text" name="departName" readonly="true">
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">职位信息</label><input type="text"
											class="form-text" name="positionName" readonly="true">
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">指纹ID</label><input type="text"
											class="form-text" name="fingerprintId" id="fingerprintId">
									</div>
								</div>
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button class="red-but" onclick="updateEmpFingerprintId();">
										<span>保存</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv('updateDiv2');">
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
     
    <div class="commonBox popbox" id="messageBox" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center conf" style="margin-top: 0px;">
			<div class="popbox-main" style="height: 200px;">
				<div class="cun-pop-content">
					<div class="cun-pop-title">
						<h4>提示</h4>
					</div>
					<div class="cun-pop-contentframe">
						<i class="box-icon yes"></i>
						<p id="messageContent">更新成功！</p>
					</div>
					<div class="button-wrap" style="width: 180px;">
						<button class="red-but" id="messageButton" onclick="closeDiv('messageBox');">
							<span>确认</span>
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>        
    
    <!-- shanchu -->
    
    <div class="commonBox popbox" id="deleteDiv" style="display:none">
	  <input type="hidden" id="deleteId" name="deleteId"/>
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>删除员工</strong>
						</div>
						<div class="form-main">

							<div class="col">
								<div class="form">
									确定删除此员工吗？
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button class="red-but" onclick="deletePostion();" style="width:120px;">
										<span>确定</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv('deleteDiv');" style="width:120px;">
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
	<!-- 离职 -->
    
    <div class="commonBox popbox" id="deleteDiv2" style="display:none">
	  <input type="hidden" id="deleteId" name="deleteId"/>
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 20%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>离职确认</strong>
						</div>
						<div class="form-main">

							<div class="col">
								<div class="form">
									是否确认此员工离职？
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button  class="red-but" onclick="updatePostion();" style="width:120px;">
										<span>确定</span>
									</button>
									<button  class="small grey-but"
										onclick="closeDiv('deleteDiv2');" style="width:120px;">
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
		<!-- end -->
    </div>
    <div class="paging" id="commonPage"></div>
            
    <!-- 批量修改汇报对象 -->
	<div class="commonBox popbox" id="updateReporterDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>批量修改汇报对象</strong>
						</div>

						<div class="form-main">
							<form id="addForm">
								<div class="col" style="height:100px;">
									<div class="form">
										<label class="w" style="width:100px;">修改人员：</label>
										<span id = "nameDisplay" style="width:200px;height:80px;display:block;vetical-align:top;float:left;" ></span>
										<input id = "employIdDisplay" type = "hidden"/> 
									</div>									
								</div>
								<div class="col">                    
				                    <div class="form"><label class="w" style="width:100px;">所属部门：</label>
	                                <select id="firstDepartOfUpd" name="firstDepart" class="select_v1">
				                    </select>
				                    </div>
				                </div>		
								<div class="col">                    
				                    <div class="form"><label class="w" style="width:100px;">汇报对象：</label>
				                    <select id="employeeLeaderOfUpd" name="employeeLeader" class="select_v1">
				                    </select>
				                    </div>
				                </div>
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button class="red-but" onclick="updateBatch();" style="width:120px;">
										<span>保存</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv('updateReporterDiv');" style="width:120px;">
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
	<!-- 设置弹窗 -->
	<div class="commonBox popbox" id="importDiv" style="display:none">
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
									onclick="closeImportDiv();" style="float:right">
									<span>返回</span>
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>