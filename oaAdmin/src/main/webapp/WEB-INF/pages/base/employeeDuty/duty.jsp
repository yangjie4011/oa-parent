<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>值班</title>
<%@include file="../../common/common2.jsp"%>
<script src="<%=basePath%>js/util/easyui/jquery.easyui.min.js" type="text/javascript"></script>
<link href="<%=basePath%>css/util/easyui/easyui.css" rel="stylesheet"/>
<link href="<%=basePath%>css/util/easyui/icon.css" rel="stylesheet"/>
<script src="<%=basePath%>js/util/easyui/easyui-lang-zh_CN.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/base/employeeDuty/duty.js?v=20191201"/></script>
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
</style>
</head>
<body style="overflow-x:auto">
   <div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li class="active"><strong>值班</strong></li>
		</ul>
	</div>
	<div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
               <form id="queryform">
               
                <div class="form-main">
            
	                <div class="col">	
	                	<div class="form">
	                        <label class="w">年份</label>
							<input id="searchYear" type="text" class="Wdate" name="year" onClick="WdatePicker({dateFmt:'yyyy'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy'});" value="${thisYear}" />	
	                    </div>	                
	                    <div class="form">
	                        <label class="w">值班部门</label>
	                        <select id="searchDepart" name="departId" class="select_v1">  
	                        	<option value="">请选择</option>
	                            	<c:forEach items="${requestScope.departList}" var="depart" varStatus="status">
	                                     <option value="${depart.id}">${depart.name}</option>
	                                </c:forEach>
	                         </select>
	                    </div>
	                    <div class="form">
	                    	<label class="w">法定节假日</label>
	                    	<select id="searchVacation" name="vacationName" class="select_v1">
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
	                </div>
               </div>
             </form>
              <div class="col">  
                    <div class="button-wrap ml-4">
						<button class="blue-but" id="uploadEventBtn"  type="button" ><span><i class="icon-wenjianjia"></i>选择文件</span></button>  
                    	<form enctype="multipart/form-data" id="batchUpload" method="post" class="form-horizontal">    
                    		<input type="hidden" id="uploadDepart" name="departId">
                    		<input type="hidden" id="uploadYear" name="year">
                    		<input type="hidden" id="uploadVacation" name="vacationName">
						    <input type="file" name="file"  style="width:0px;height:0px;" id="uploadEventFile">  
						    <div class="form">
						    	<input id="uploadEventPath" class="form-text" disabled="disabled"  type="text" placeholder="-未选择excel表-" style="font-size:12px;text-align:center;font-color:gray;font-style:oblique" >                                           
						    </div>
						</form>
						<button type="button" class="blue-but"  onclick="signExcel.uploadBtn()" ><span><i class="icon-shangchuan"></i>上传值班</span></button>
                    </div> 
                </div>  <div class="col">  
                	<div class="button-wrap ml-4">
                		<button id="downloadDutyTemplate" class="blue-but"><span><i class="icon download"></i>下载模板</span></button> 
                        <button  onclick="getUnCommitData(0);" class="blue-but"><span><i class="icon add"></i>新增值班</span></button> 
                	</div>
                </div>
                <h1>&nbsp值班提醒：法定节假日安排最晚请于节假日前第4个工作日提交！</h1>
                <br/>
                <div class="datatitle" style="padding-left:0px;">
					<ul class="fl jui-tabswitch" id="dutyTab">
						<li  rel="#old" class="active"><strong>值班安排</strong></li>
				        <li  rel="#new"><strong>值班记录</strong></li>
					</ul>
	            </div>
			</div>
			
	        
	        <div id="detailDiv" style="display:none;">
	        		<input type="hidden" id="depart">
	        		<input type="hidden" id="year">
	        		<input type="hidden" id="vacation">
			        <div id="detailTitle" class="title" style="text-align:center;"><strong></strong></div>
			        <div class="col">
	           	      <div class="form">
	                      <label class="w">公司：</label>
	                      <input id="companyName" type="text" class="form-text" style="border:0px;">
	                   </div>
	                   <div class="form">
	                      <label class="w">部门：</label>
	                      <input id="departName" type="text" class="form-text" style="border:0px;">
	                   </div>
	                   <div class="form">
	                      <label class="w">状态：</label>
	                      <input id="approvalStatus" type="text" class="form-text" style="border:0px;">
	                   </div>
	                </div>
	             
		            <table border="1" id="detailList">
						  <thead>
						  	<tr>
						  		<th style="overflow-x:auto;width:50px;text-align:center;"></th>
								<th style="overflow-x:auto;width:75px;text-align:center;">日期</th>
								<th style="overflow-x:auto;width:75px;text-align:center;">星期</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th>
				                <th style="overflow-x:auto;width:200px;text-align:center;" colspan='2'>时间要求</th>
				                <th style="overflow-x:auto;width:80px;text-align:center;">工作小时数</th>
				                <th style="overflow-x:auto;width:200px;text-align:center;">工作内容简述</th>
				                <th style="overflow-x:auto;width:150px;text-align:center;">备注</th>			                
							</tr>
						  </thead>
			              <tbody>
			              </tbody>
		            </table>
		            
		             <div class="col">  
	                    <div class="buttonDiv button-wrap ml-4">
	                       
	                    </div> 
                     </div> 
		    </div>
			
			<div class="content scheduleRecord" style="overflow-x:auto;display:none;">
	            <div>
		            <table >
						 <thead>
							<tr>
								<th style="overflow-x:auto;width:100px;text-align:center;">年份</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">法定节假日</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">值班排班人</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">提交日期</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">人事审核人</th>
				                <th style="overflow-x:auto;width:100px;text-align:center;">状态</th>		                
				                <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
							</tr>
						  </thead>
			              <tbody id="reportList">
			              </tbody>
		            </table>
	            </div>           
	        </div>
		    <div class="paging" id="commonPage" style="display:none;"></div>
		    <input type="hidden" id="pageNo" value=""/>
	     
	    <!-- 新增值班明细弹框 -->
		  <div class="commonBox popbox" id="dutyPlanDiv" style="display:none;height:500px">
				<div class="popbox-bg"></div>
				<div class="popbox-center" style="top: 10%; left: 30%">
					<div class="popbox-main" style="">
						<div class="cun-pop-content">
							<div class="form-wrap">
							
								<div class="title">
									<strong><i class="icon"></i>值班安排</strong>
								</div>
								
								<div class="form-main">
		
									<form id="dutyPlanForm" class="updateForm">
										<input id="dutyPlanYear" name="year" type="hidden">
										<input id="dutyPlanVavation" name="vacationName" type="hidden">
										<input id="dutyPlanDepart" name="departId" type="hidden">
										<input id="dutyPlanStartTime" name="startTime" type="hidden">
										<input id="dutyPlanEndTime" name="endTime" type="hidden">
										<div class="col">
											<div class="form">
												<label class="w"  style="width:130px;">日期:</label>
												<select id="dutyDate" name="vacationDate" class="select_v1">
						                        </select>
											</div>
										</div>
									 
										<div class="col">																		
											<div class="form">
												<label class="w"  style="width:130px;">值班人员:</label>
												<input id="dutyEmployee" type="text" style="width:180px;height:25px" class="form-text" onclick="getDutyEmployee(this)">
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
												<input id="from" type=text" class="Wdate" style="width:70px;" readonly="readonly" onClick="WdatePicker({dateFmt:'HH:mm'})"  onfocus="WdatePicker({onpicked=checkStartTime,dateFmt:'HH:mm'});" onblur="checkStartTime()"/>
											</div>
											<div class="form">
												<label class="w" >To：</label>
												<input id="to" type="text" class="Wdate" style="width:70px;" readonly="readonly" onClick="WdatePicker({dateFmt:'HH:mm'})"  onfocus="WdatePicker({onpicked=checkEndTime,dateFmt:'HH:mm'});" onblur="checkEndTime()"/>
											</div>
										</div>		
										<div class="col">
											<div class="form">
												<label class="w"  style="width:130px;">工作小时数:</label>
												<input id="workHours" name="workHours" type="text" style="width:50px;" class="form-text" readonly="readonly" >小时
											</div>
										</div>
										<div class="col">
											<div class="form">
												<label class="w"  style="width:130px;">工作内容简述:</label>
												<textarea placeholder="只限输入50字" id="dutyItem" maxlength="50" onchange="this.value=this.value.substring(0, 50)" onkeydown="this.value=this.value.substring(0, 50)" onkeyup="this.value=this.value.substring(0, 50)" name="dutyItem" style="height:20px;" cols="50" rows="8"></textarea>
											</div>
										</div>
										<div class="col">
											<div class="form">
												<label class="w"  style="width:130px;">备注:</label>
												<textarea placeholder="只限输入50字" id="remarks" maxlength="50" onchange="this.value=this.value.substring(0, 50)" onkeydown="this.value=this.value.substring(0, 50)" onkeyup="this.value=this.value.substring(0, 50)" name="remarks" style="height:20px;" cols="50" rows="8"></textarea>
											</div>
										</div>
									</form>
		
									<div class="col">
										<div class="button-wrap ml-4">
											<button id="zcqr" class="small grey-but"  style="width:120px;" onclick="closeDiv();">
												<span>取消</span>
											</button>
											<button id="zcqx" class="red-but" style="width:120px;" onclick="addDutyPlan();">
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
     </div>
     <!-- 选择员工 -->
	<div id="showGetDeptList"></div>
</body>
</html>
