
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>排班查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/employeeClass/empClassQuery.js?v=20191201"/></script>
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
                <input type="hidden" id="departStr">
                <input type="hidden" id="monthStr">
                 <input type="hidden" id="groupIdStr">
               <form id="queryform">
               
               <div class="form-main">
                  
               <div class="col">                 
	                <div class="form">
	                	<label class="w">年月</label>
						<input id="month" type="text" class="Wdate" name="month" onClick="WdatePicker({dateFmt:'yyyy-MM'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM'});" value="${nowMonth}" />&nbsp;&nbsp;<a id="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a id="lastMonth" style="color:blue;">上月</a>	                	
	                </div>                                                  
               </div>
                                         
               <div class="col">
	            	<div class="form">
                        <label class="w">部门：</label>
                        <select id="firstDepart" name="firstDepart" class="select_v1">
                        </select>
                    </div>
                	<div class="form">
	                   <label class="w">组别：</label>
	                   <select id="groupId" name="groupId" class="select_v1">
	                       <option value="">请选择</option>
	                   </select>
	           		 </div>
                </div>
                
                
                </div>
             </form>
             <div class="col">  
                    <div class="button-wrap ml-4">
                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button>      
                   		<button id="exports" class="red-but"><span><i class="icon add"></i>导出</span></button>      
                   		
                    </div> 
              </div> 
			</div>
			
	        <div id="showTitle" style="display: none;">
		          <h2 id="titleClass" align="center" style="font-size: 32px"></h2>
		          <h6>
		       	 <label class="w">申请日期：<input id="applyDate" type="text" class="form-text" style="border:0px;" value="2017-05-25">				
		      	 	    &nbsp; &nbsp;&nbsp;&nbsp;状态：已通过
		      	 	 </label>   
		     	 </h6>
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
   <div id="moveSingle" style="display:none;height:300px;">
	  <div class="form-main" style="position:absolute;top:50px;"> 
           <div class="col">
              <div class="form">
                 <label class="w">选择人员</label>
                 <input readonly="readonly" id="move_name" type="text" class="form-text" value="">
               </div>
          </div>  
          <div class="col">
          	    <div class="form">
                     <label class="w">选择班次：</label>
                 </div>
            </div>
           <div class="col">                 
              <div class="form">
              	<label class="w">选择日期</label>
			    <input  id="move_date" type="text" class="Wdate" readonly="readonly"/>
              </div>                                                  
            </div>
      </div>
      <div class="col" style="position:absolute;bottom:10px;">  
          <div class="button-wrap ml-3">
              <button class="red-but btn-middle" onclick="save()"><span><i class="icon"></i>确定</span></button> 
              <button class="red-but btn-middle" onclick="cancel()"><span><i class="icon"></i>取消</span></button>      
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
							<strong><i class="icon add-user"></i>调班</strong>
							<i onclick="closeCounterDiv();" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
							
						</div>
						<div class="form-main">

							<form id="insertForm" class="updateForm">
								<input type="hidden" id="id" name="id" value=""/>
								<input type="hidden" id="version" name="version" value=""/>
								<input type="hidden" id="classEmpId" name="empId" value=""/>
								
								<input type="hidden" id="classDate" name="classDate" value=""/>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">员工姓名:</label>
											
										<span id="pb_name"></span>
									</div>
								</div>
							
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">排班日期:</label>
										<span id="pb_date"></span>
									</div>
								</div>	
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">选择班次:</label>
											
										<select id="classSet" class="select_v1">
                     					</select>
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">备注:</label>
											
										<input id="remark" name="remark" type="text" class="form-text" value="">
									</div>
								</div>
								
							</form>

							<div class="col">
								 <button class="red-but btn-middle" onclick="reconfirm()"><span><i class="icon"></i>确定</span></button> 
              					 <button class="red-but btn-middle" onclick="closeCounterDiv()"><span><i class="icon"></i>取消</span></button>      
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

							<form id="addForm" class="updateForm">
								
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">原班次信息:</label>
										<span id="pb_dateAfter"></span>
									</div>
								</div>	
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">调整后班次:</label>
										
										<span id="pb_dateBefore"></span>
									</div>
								</div>
								
							</form>

							<div class="col">
								 <button class="red-but btn-middle" onclick="save()"><span><i class="icon"></i>确定</span></button> 
              					 <button class="red-but btn-middle" onclick="closeCounterDiv()"><span><i class="icon"></i>取消</span></button>      
							</div>
						</div>
					</div>
					<!-- form-main end -->
				</div>
			</div>
		</div>
	</div>
	
	
	<!-- 排班查询 调班查询 -->
	<div class="showTable" style="display:none">
	<div class="content" style="overflow-x:auto">
          <table>
			<thead>
				<tr>
					<th style='text-align:center;'>部门</th>
					<th style='text-align:center;'>组别</th>
					<th style='text-align:center;'>年月</th>
					<th style='text-align:center;'>排班人</th>
					<th style='text-align:center;'>申请日期</th>
					<th style='text-align:center;'>排班人数</th>
					<th style='text-align:center;'>应出勤工时</th>
					<th style='text-align:center;'>排班批核人</th>
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
					<td></td>
				</tr>
			</tbody>
		</table>
		
		
		<!--  调班查询 -->
		<table >
			<thead>
				<tr>
					<th style='text-align:center;'>流水编号</th>
					<th style='text-align:center;'>部门</th>
					<th style='text-align:center;'>组别</th>
					<th style='text-align:center;'>被调班员工人数</th>
					<th style='text-align:center;'>调班日期</th>
					<th style='text-align:center;'>调班类型</th>
					<th style='text-align:center;'>调班人</th>
					<th style='text-align:center;'>生成时间</th>
					<th style='text-align:center;'>备注</th>
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
					<td></td>
					<td></td>
				</tr>
			</tbody>
		</table>
		 <input type="hidden" id="pageNo" name="page" value=""/>
		<div class="paging" id="commonPage"></div>
    </div>
  </div> 
   
				<!-- 班次信息弹框 -->
				<div class="commonBox popbox" id="settingInfo" style="display:none">
					<div class="popbox-bg"></div>    
					<div class="popbox-center" style="top: 10%; left: 30%">  
						<div class="popbox-main" style="">
							<div class="cun-pop-content"> 
								<div class="form-wrap settingInfo">
									<div class="title">
										<strong><i class="icon"></i>海岛大亨</strong>
										<i onclick="closeCounterDiv();" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
									</div>
									<div class="form-main">
			                               <div class="col">
												<div class="form">
													<label class="w"  style="width:130px;">2019-03-12</label>
													<span>星期五</span>
												</div>
											</div>
											<div class="col">
												<div class="form">
													<label class="w"  style="width:130px;">原排班班次</label>
													<span>A班&nbsp;12:00-18:00</span>	
												</div>
											</div>
											<div class="col">
												<div class="form">
													<label class="w"  style="width:130px;">调整后班次</label>
													<span>B班&nbsp;12:00-18:00</span>	
												</div>
											</div>
											<div class="col">
												<div class="button-wrap ml-4 tiaob">
													<span id="changeClassSpan"></span>
												</div>
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
