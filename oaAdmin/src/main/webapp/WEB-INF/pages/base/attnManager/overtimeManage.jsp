
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>调休小时数管理</title>
<%@include file="../../common/common2.jsp"%>
<link rel="stylesheet" href="http:<%=oaUle%>/ule/oa/c/personsel.css?v=20191201">
<script src="../js/util/personsel.js?v=20191105"></script>

<script type="text/javascript" src="../js/base/attnManager/overtimeManage.js?v=2019082601"/></script>
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
            
            <div class="form-wrap">    
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"  onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId}"/>
                <input type="hidden" id="pageNo" value=""/>
                
               <form id="queryform">
               
               <div class="form-main">
                  
               <div class="col">       
              		<div class="form">
                        <label class="w">部门：</label>
                        <select id="firstDepart" name="departId" class="select_v1">
                        </select>
                    </div>
                         
	                <div class="form">
	                	<label class="w">年份：</label>
						<input  type="text" class="Wdate" id="year" name="year" onClick="WdatePicker({dateFmt:'yyyy'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy'});" value="${nowYear}" />                	
	                </div>                                                  
               </div>
                                         
            	<div class="col">
                    <div class="form">
                    	<label class="w">员工编号：</label>
                    	<input type="text" class="form-text" id="code" name="code" value="">
                    </div>
                    
                    <div class="form">
                    	<label class="w">员工姓名：</label>
                    	<input type="text" class="form-text" id="cnName" name="cnName" value="">
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
               <div style="float:left;width:50%;overflow-x:auto">
				    <table border="1" id="reportTitle">
					    <thead>
					    </thead>
		                <tbody>
		                </tbody>
			        </table>
		       </div>
	           <div style="float:left;width:50%;overflow-x:auto">
			       <table border="1" id="reportContent">
					   <thead>
					   </thead>
				       <tbody>
				       </tbody>
			   	   </table>
	           </div>
            </div>
            
            <div class="tablebox"></div>
            
        </div>
   
   <div class="commonBox popbox" id="insertDiv" style="display:none">
		<div class="popbox-bg"></div>    
		<div class="popbox-center" style="top: 10%; left: 30%">  
			<div class="popbox-main" style="">
				<div class="cun-pop-content"> 
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>新增调休</strong>
							<i onclick="closeDiv('insertDiv');" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
							
						</div>
						<div class="form-main">

							<form id="insertForm" class="insertForm">
								<input type="hidden" id="insertParentId" name="parendId" value=""/>
								<input type="hidden" id="employeeId" name="employeeId" value=""/>
								<div class="col">
									<div class="form">
										<label class="w">员工编号:</label>
											
										<input type="text"  class="form-text" readonly="readonly" name="code" value="">
									</div>
									<div class="form">
										<label class="w">员工姓名:</label>
											
										<input type="text" id="insertMe" readonly="readonly" class="form-text" name="cnName" value="">
									</div>
								</div>
							
								<div class="col">
									<div class="form">
				                    	<label class="">调休有效期：</label>
										 <input id="startTime" type="text" class="Wdate" name="startTimeFormat" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
				                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'})"/>    
				                    	<label class="w" style="width:30px;">至</label>
										<input id="endTime" type="text" class="Wdate" name="endTimeFormat" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
										       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startTime\')}'})"/>        
										       
										       
										       
				                    </div>
								</div>	
								<div class="col">
									<div class="form">
										<label class="w" style="width: 75px;" >调休小时数:</label>
										<input type="text"  class="form-text" name="allowRemainDays" onkeyup="value=checkData(value)" 
											onbeforepaste="clipboardData.setData('text', checkData(clipboardData.getData('text')))" ondblclick="this.value='';">
										（最小单位0.5小时）
									</div>
								</div>
								<div class="col">	
									<div class="form">
										<label class="w" >备注:</label>
										<textarea placeholder="限50个字" name="remark" id="insertRemark"  style="border: 1px solid #d9d9d9; width:176px;   height: 55px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8" onkeyup="if(this.value.length>100) this.value=this.value.substr(0,50)" maxlength="50"></textarea>	
									</div>	
								</div>
								
							</form>

							<div class="col" style="margin-top:60px; text-align:center;margin-bottom:3px;">
              					 <button class="red-but btn-middle" style="float:none;" onclick="closeDiv('insertDiv')"><span><i class="icon"></i>取消</span></button>
								 <button class="blue-but btn-middle" style="float:none;" onclick="saveMsg()"><span><i class="icon"></i>确定</span></button>       
							</div>
						</div>
					</div>
					<!-- form-main end -->
				</div>
			</div>
		</div>
	</div>
	
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>    
		<div class="popbox-center" style="top: 10%; left: 30%">  
			<div class="popbox-main" style="">
				<div class="cun-pop-content"> 
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>修改调休</strong>
							<i onclick="closeDiv('updateDiv');" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
							
						</div>
						<div class="form-main">

							<form id="updateForm" class="updateForm">
								<input type="hidden" id="updateParentId" name="parendId" value=""/>
								<input type="hidden" id="updateEmployeeId" name="employeeId" value=""/>
								<input type="hidden"  id="updateId" name="id" value=""/>
								<div class="col">
									<div class="form">
										<label class="w">员工编号:</label>
											
										<input type="text" style="width:150px;" class="form-text" readonly="readonly" name="code" value="">
									</div>
									<div class="form">
										<label class="w">员工姓名:</label>
											
										<input type="text" id="insertMe" style="width:150px;" class="form-text" readonly="readonly" name="cnName" value="">
									</div>
								</div>
							
								<div class="col">
									<div class="form">
				                    	<label class="">调休有效期：</label>
				                    	<input id="startTimeUpdate" type="text" class="Wdate" name="startTimeFormat" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
				                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTimeUpdate\')}'})"/>    
				                    	<label class="w" style="width:30px;">至</label>
										<input id="endTimeUpdate" type="text" class="Wdate" name="endTimeFormat" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
										       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startTimeUpdate\')}'})"/>    
										           
				                    </div>
								</div>	
								<div class="col">
									<div class="form">
										<label class="w" style="width: 75px;">调休小时数:</label>
											
										<input type="text"  class="form-text" name="allowRemainDays" onkeyup="value=checkData(value)" 
											onbeforepaste="clipboardData.setData('text', checkData(clipboardData.getData('text')))" ondblclick="this.value='';">
										
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w">已使用:</label>
											
										<span id="usedDays"></span>
									</div>
								</div>
								<div class="col">	
									<div class="form">
										<label class="w" >备注:</label>
										<textarea placeholder="限50个字" name="remark"  id="updateRemark" style="border: 1px solid #d9d9d9; width:176px;   height: 55px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8" onkeyup="if(this.value.length>100) this.value=this.value.substr(0,50)" maxlength="50"></textarea>	
									</div>	
								</div>
								
							</form>

							<div class="col"   style="margin-top:60px; text-align:center;margin-bottom:3px;">
              					 <button class="red-but btn-middle" style="float:none;" onclick="closeDiv('updateDiv')"><span><i class="icon"></i>取消</span></button>
								 <button class="blue-but btn-middle" style="float:none;" onclick="updateMsg()"><span><i class="icon"></i>确定</span></button>       
							</div>
						</div>
					</div>
					<!-- form-main end -->
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	
	
	
	<div id="overTimeListShow">
	    <table>
			 <thead>
				<tr>
				   <th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">当年剩余调休小时数</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">当年已用调休小时数</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">离职日期</th>
	               <th style="overflow-x:auto;width:150px;text-align:center;">在职状态</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
				</tr>
			  </thead>
	            <tbody id="reportList0">
	            </tbody>
	    </table>
	    <input type="hidden" id="pageNo" name="page" value=""/>
		<div class="paging" id="commonPage"></div>
	</div> 
	
	
	
	
	
	
	
	
	<!-- 排班查询 调班查询 -->
	<div class="showTable" style="display:none">
	<div class="content" style="overflow-x:auto">
          <table>
			<thead>
				<tr>
				   <th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">当年剩余调休小时数</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">当年已用调休小时数</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">离职日期</th>
	               <th style="overflow-x:auto;width:150px;text-align:center;">在职状态</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
				</tr>
			</thead>
			<tbody id="reportList1">
				<tr>
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
		
		
		<!--  调班查询 -->
		<table >
			<thead>
				<tr>
				   <th style="overflow-x:auto;width:100px;text-align:center;">年份</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">调休小时数</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">已使用</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">调休有效期（开始）</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">调休有效期（结束）</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">状态</th>
	               <th style="overflow-x:auto;width:150px;text-align:center;">备注</th>
	               <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
				</tr>
			</thead>
			<tbody id="reportList2" >
				<tr>
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
		 <input type="hidden" id="pageNo1" name="page" value=""/>
		<div class="paging" id="commonPage1"></div>
		<input type="hidden" id="empId">
		<input type="hidden" id="empYear">
    </div>
  </div> 
   
   
</body>

</html>
