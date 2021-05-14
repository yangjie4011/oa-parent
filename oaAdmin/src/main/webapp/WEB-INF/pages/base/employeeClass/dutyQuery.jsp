<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>值班查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/employeeClass/dutyQuery.js?v=20191201"/></script>
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
               <form id="queryform">
	               <div class="form-main"> 
		               
		               <div class="col">    
		               		
		               		<div class="form">
			                	<label class="w">年份</label>
						        <input id="year" type="text" class="Wdate" name="year" onClick="WdatePicker({dateFmt:'yyyy'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy'});"  value="${thisYear}"/>
			                </div>                 
			                
			                <div class="form">
			                	<label class="w">法定节假日</label>
									<select id="vacationName" name="vacationName" class="select_v1">
									   <option value="">请选择法定节假日</option>
									   <option value="元旦">元旦</option>
									   <option value="春节">春节</option>
									   <option value="清明节">清明节</option>
									   <option value="劳动节">劳动节</option>
									   <option value="端午节">端午节</option>
									   <option value="中秋节">中秋节</option>
									   <option value="国庆节">国庆节</option>
			                        </select>
			                </div> 
			                
			                <div class="form">
		                        <label class="w">值班部门：</label>
		                        <select id="firstDepart" name="departId" class="select_v1">
		                        </select>
		                    </div>                                                   
		               </div>
	                </div>
               </form>
               <div class="col">  
                    <div class="button-wrap ml-4">
                        <button id="queryDuty" class="red-but"><span><i class="icon search"></i>查询</span></button>    
                        <button id="exports" class="blue-but"><span><i class="icon download"></i>导出</span></button>        
                    </div> 
		       </div>
            </div>
            
            <!-- 值班与流水 -->
			<div class="showTable" style="display:none;">
				<div class="content" style="overflow-x:auto">
			          <table>
						<thead>
							<tr>
								<th style='text-align:center;'>值班部门</th>
								<th style='text-align:center;'>年份</th>
								<th style='text-align:center;'>法定节假日</th>
								<th style='text-align:center;'>值班排班人</th>
								<th style='text-align:center;'>申请日期</th>
								<th style='text-align:center;'>值班人数</th>
								<th style='text-align:center;'>值班批核人</th>
								<th style='text-align:left;'>操作</th>
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
								<th style='text-align:center;'>流水编号</th>
								<th style='text-align:center;'>值班部门</th>
								<th style='text-align:center;'>年份</th>
								<th style='text-align:center;'>法定节假日</th>
								<th style='text-align:center;'>调班类型</th>
								<th style='text-align:center;'>值班调班人</th>
								<th style='text-align:center;'>生成时间</th>
								<th style='text-align:left;'>操作</th>
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
			    </div>
		  </div> 
		  <!-- 详情  -->
		  <div id="detailDiv">
		   
		  </div>
		 
		  <!-- 新增或者编辑值班明细弹框 -->
		  <div class="commonBox popbox" id="showAddOrUpdateDiv" style="display:none;">
				<div class="popbox-bg"></div>
				<div class="popbox-center" style="top: 10%; left: 30%">
					<div class="popbox-main">
						<div class="cun-pop-content">
							<div class="form-wrap">
							
								<div class="title">
									<strong><i class="icon"></i>值班安排</strong>
								</div>
								
								<div class="form-main">
		
										<div class="col">
											<div class="form">
												<label class="w"  style="width:130px;">日期:</label>
												<select id="duty_date" class="select_v1">
												   <option value="">请选择值班日期</option>
						                        </select>
											</div>
										</div>
									 
										<div class="col">																		
											<div class="form">
												<label class="w"  style="width:130px;">值班人员:</label>
												<input id="employ_id" type="text" class="form-text" readonly>
											</div>
										</div>
										
										<div class="col">
											<div class="form">
												<label class="w"  style="width:130px;">时间要求</label>
											</div>
										</div>	
										<div class="col">
											<div class="form">
												<label class="w" style="width:130px;">From：</label>
												<input id="start_time" type="text" class="Wdate" style="width:70px;" readonly="readonly" onClick="WdatePicker({dateFmt:'HH:mm',onpicked: checkStartTime})"  onfocus="WdatePicker({dateFmt:'HH:mm',onpicked: checkStartTime});"/>
											</div>
											<div class="form">
												<label class="w">To：</label>
												<input  id="end_time" type="text" class="Wdate" style="width:70px;" readonly="readonly" onClick="WdatePicker({dateFmt:'HH:mm',onpicked: checkStartTime})"  onfocus="WdatePicker({dateFmt:'HH:mm',onpicked: checkStartTime});"/>
											</div>
											
										</div>	
										<div class="col">
											<div class="form">
												<label class="w"  style="width:130px;">工作小时数:</label>
												<input id="work_hours" type="text" style="width:50px;" class="form-text" readonly="readonly" >小时
											</div>
										</div>
										<div class="col">
											<div class="form">
												<label class="w"  style="width:130px;">工作内容简述:</label>
												<textarea placeholder="只限输入50字"  onKeyUp="if(this.value.length>100) this.value=this.value.substr(0,50)" maxlength="50" id="work_item" style="height:20px;" cols="50" rows="8"></textarea>
											</div>
										</div>
										<div class="col">
											<div class="form">
												<label class="w"  style="width:130px;">备注:</label>
												<textarea placeholder="只限输入50字"  onKeyUp="if(this.value.length>100) this.value=this.value.substr(0,50)" maxlength="50" id="remark" style="height:20px;" cols="50" rows="8"></textarea>		
											</div>
										</div>
		
									<div class="col">
										<div class="button-wrap ml-4">
											<button onclick="closeAddOrUpdateDiv();" class="red-but"  style="width:120px;">
												<span>取消</span>
											</button>
											<button id="uptateOrAddDutyDetail" class="small grey-but" style="width:120px;">
												<span>确认</span>
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
		 <!-- 新增或者编辑值班明细弹框 -->
		 <!-- 选择员工 -->
		 <div id="showGetDeptList"></div>
		 <!-- 流水详情 -->
		 <div id="historyDetail"></div>
		 <!-- 流水详情 -->
     </div>
</body>
</html>