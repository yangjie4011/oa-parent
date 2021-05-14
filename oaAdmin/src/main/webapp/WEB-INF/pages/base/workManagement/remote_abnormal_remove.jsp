<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<link rel="icon" href="data:;base64,=">
<title>远程异常消除</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/workManagement/remote_abnormal_remove.js?v=20200226"/></script>
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
	                        <label class="">部门：</label>
	                        <select id="firstDepart" name="departId" class="select_v1">
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
	                </div>   
	                
	                <div class="col">
	                	<div class="form">
	                        <label class="">工时制：</label>
	                        <select id="workType" style="height:36px;width:150px;" name="workType" class="select_v1">
	                        </select>
	                    </div>
	                	<div class="form">
	                        <label class="">状态：</label>
	                        <select id="approvalStatus" name="approvalStatus" class="select_v1"><option value="">请选择</option>
								<option value="0">通过</option>
								<option value="1">不通过</option>
								<option value="2">未提交</option>
							</select>
	                    </div>
	                	<div class="form">
	                    	<label class="">日期：</label>
	                    	<input value="${date }" id="startDate" type="text" class="Wdate" name="startDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    	<label class="margin-left"> &nbsp;至&nbsp;&nbsp;</label>
							<input value="${date }" id="endDate" type="text" class="Wdate" name="endDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    </div>
	                </div> 
	                
              		</form>
	                

	                <div class="col">  <!-- style="display:inline-block; margin:16px 0px 0px 0px;" -->
	                    <div class="button-wrap ml-4"  style="margin-left:-60px">
	                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button>
	                   		<button onclick="getPassDiv(this,1)" class="blue-but"><span><i class="icon edit"></i>快速审批</span></button>        
	                    </div> 
	                </div>
                </div>
			</div>

		<table>
			<thead>
				<tr>
					<th style='text-align:left;width:10px;'><input id="checkAll" type ='checkbox'/></th>
					<th style='text-align:center;'>员工编号</th>
					<th style='text-align:center;'>姓名</th>
					<th style='text-align:center;'>部门</th>
					<th style='text-align:center;'>汇报对象</th>
					<th style='text-align:center;'>工时制</th>
					<th style='text-align:center;'>班次</th>
					<th style='text-align:center;'>异常考勤日期</th>
					<th style='text-align:center;'>状态</th>
					<th style='text-align:center;'>批核人</th>
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
					<td></td>
					<td></td>
				</tr>
			</tbody>
		</table>
    </div>
    <div class="paging" id="commonPage"></div>
   <!-- 审阅弹框 -->
    <div class="commonBox popbox" id="updateReporterDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>审阅</strong>
						</div>

						<div class="form-main">
							<form id="addForm">
								<div class="col" style="margin-bottom:0px;">
									<div class="form">
										<label class="w" style="width:130px;">审阅处理：</label>
										<input style="width:13px;" type="radio" name="pssStatus" value="0" checked="checked"><span style="float:left;">通过</span><span style="float:left;width:20px;">&nbsp;&nbsp;&nbsp;</span><input style="width:13px;" type="radio" name="pssStatus" value="1"><span style="float:left;">不通过</span><span style="float:left;width:20px;">&nbsp;&nbsp;&nbsp;</span><input style="width:13px;" type="radio" name="pssStatus" value="2"><span style="float:left;">未提交</span>
									</div>									
								</div>
								<div class="col" style="margin-bottom:30px;">
									<div class="form">
										<label class="w" style="width:130px;">审批意见：</label>
										<textarea name="approvalReason" style="width:178px;height:50px;padding:0;" rows="" cols=""></textarea>
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
	<input type="hidden" id="employeeIds" value=""/>
	<input type="hidden" id="registerDates" value=""/>
</body>
</html>