<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>远程工作登记</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/workManagement/teleworkRegistration.js?v=20200330"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;1
	word-break: keep-all;
	white-space:nowrap;
	}
</style>
</head>
<body>
	<input type="hidden" value="0" id="flagTab">
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li><strong>远程工作登记</strong></li>
		</ul>
	</div>
	<div class="content first" style="overflow-x:auto">
            <input type="hidden" id="isScheduler" value="${requestScope.isScheduler }"/>
            <input type="hidden" id="isMenu" value="true"/>
           <div class="form-wrap">    
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"  onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId}"/>
                <input type="hidden" id="pageNo" value=""/>
                <input type="hidden" id="monthSave" value=""/>
                <input type="hidden" id="firstDepartSave" value=""/>
                <input type="hidden" id="groupIdSave" value=""/>
               <form id="queryform">
               
               <div class="form-main">
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
	                        <select id="firstDepart" name="firstDepart" class="select_v1">
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
	                	<label class="w">年月</label>
						<input id="month" type="text" class="Wdate" name="month" onClick="WdatePicker({dateFmt:'yyyy-MM'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM'});" value="${nowMonth}" />&nbsp;&nbsp;<a id="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a id="lastMonth" style="color:blue;">上月</a>	                	
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
			
			
		</div>	
			
			<div id="showTitle" style="display: none;">
				
            	<div style="float:left;width:50%;overflow-x:auto">
			      <table border="1" id="reportListTitle">
					  <thead>
					  </thead>
			             <tbody>
			             </tbody>
		          </table>
		      	</div>
	            <div style="float:left;width:50%;overflow-x:auto">
	            	<table border="1" id="reportList">
					  <thead>
					  </thead>
		              <tbody>
		              </tbody>
           		 </table>
	            </div>
            </div> 
            
            <div class="col">  
                <div class="buttonDiv button-wrap ml-4" id="btnHidden" style="display: none;">
                   <button onclick='saveDiv();' class='btn-green btn-large'><span><i class='icon'></i>保存</span></button>
					<button onclick='returnMap();' class='btn-blue btn-large'><span><i class='icon'></i>返回</span></button>
                   
                </div> 
            </div> 
            
            
			<div class="tablebox"></div>
			
			
			
			
			
          
      	<div class="paging" id="commonPage"></div>
      	
      	
   <div class="content second" style="overflow-x:auto; width:100%; display:none;">
            <div>
	            <table>
					 <thead>
						<tr>
							<th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">汇报对象</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">工时制</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">月份</th>		                
			                <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
						</tr>
					  </thead>
		              <tbody id="reportList1">
		              </tbody>
	            </table>
            </div>           
        </div>
      	<div class="paging" id="commonPage1"></div>   	
      	
      	
    
    <div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>    
		<div class="popbox-center" style="top: 10%; left: 30%">  
			<div class="popbox-main" style="">
				<div class="cun-pop-content"> 
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>远程登记</strong>
							<i onclick="closeCounterDiv();" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
							
						</div>
						<div class="form-main">

							<form id="updateForm" class="updateForm">
								<input type="hidden" id="employeeId" name="employeeId" value=""/>
								<input type="hidden" id="registerDate"  value=""/>
								<input type="hidden" id="classsSettingId" name="classsSettingId" value=""/>
								<div class="col">
									<div class="form">
										<span id="weekStr"></span>
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
										<label class="w"  style="width:130px;">远程:</label>
											
										<select id="isRemote" class="select_v1">
											<option value="0">是</option>
											<option value="2">否</option>
                     					</select>
									</div>
								</div>
								
								
							</form>
							<div class="col" style="margin-top:30px;">
									<div class="button-wrap ml-4">
										<button id="setClassSet" class="red-but" style="width:120px;">
											<span>确定</span>
										</button>
										<button class="small grey-but"
											onclick="closeCounterDiv();" style="width:120px;">
											<span>取消</span>
										</button>
									</div>
							</div>
							
							<!-- <div class="col" id="classbut">
								 <button class="red-but btn-middle" onclick="saveDiv()"><span><i class="icon"></i>确定</span></button> 
              					 <button class="red-but btn-middle" onclick="closeCounterDiv()"><span><i class="icon"></i>取消</span></button>      
							</div> -->
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
						<form >
							<div class="col">
								<div class="form">
									<label class="w"  style="width:130px;">确认远程登记？</label>
								</div>
							</div>	
						</form>

						<div class="col">
							<button class="red-but btn-middle" onclick="saveWorkMap()"><span><i class="icon"></i>确定</span></button> 
              				<button class="red-but btn-middle" onclick="closeCounterDiv()"><span><i class="icon"></i>取消</span></button>      
						</div>
						
					</div>
					<!-- form-main end -->
				</div>
			</div>
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
	
	
    <!-- 
    	json{
    		1716_2019-04-01:{
    			date:2019-04-01,
    			classId:5
    		},
    		1717:{
    			date:2019-04-01,
    			classId:5
    		}
    	}
    
     -->
</body>
</html>