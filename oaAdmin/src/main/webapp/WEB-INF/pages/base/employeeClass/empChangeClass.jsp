<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>调班</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/employeeClass/empChangeClass.js?v=20200220"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
</style>
</head>
<body>
	<input type="hidden" value="0" id="flagTab">
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li><strong>调班</strong></li>
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
	                	<label class="w">年月</label>
						<input id="month" type="text" class="Wdate" name="month" onClick="WdatePicker({dateFmt:'yyyy-MM'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM'});" value="${nowMonth}" />&nbsp;&nbsp;<a id="thisMonth" style="color:blue;">本月</a>&nbsp;&nbsp;<a id="lastMonth" style="color:blue;">上月</a>	                	
	                </div>                                                  
               </div>
                                         
            	 <div class="col">		                
	                    <div class="form">
	                        <label class="w">排班部门：</label>
	                        <select id="firstDepart" name="firstDepart" class="select_v1">  
	                            <option value="">请选择</option>
	                            <c:if test="${requestScope.isScheduler}">
	                                  <c:if test="${requestScope.isScheduler}">
			                                <c:forEach items="${requestScope.departList}" var="depart" varStatus="status">
			                                     <option value="${depart.id}">${depart.name}</option>
			                                </c:forEach>
	                                  </c:if>
	                            </c:if>
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
						<button class="blue-but" id="uploadEventBtn"  type="button" ><span>选择文件</span></button>  
                    	<form enctype="multipart/form-data" id="batchUpload" method="post" class="form-horizontal">    
                    		<input type="hidden" id="uploadDepart" name="departId">
                    		<input type="hidden" id="uploadGroup" name="groupId">
                    		<input type="hidden" id="uploadMonth" name="month">
						    <input type="file" name="file"  style="width:0px;height:0px;" id="uploadEventFile">  
						    <div class="form">
						    	<input id="uploadEventPath" class="form-text" disabled="disabled"  type="text" placeholder="-未选择excel表-" style="font-size:12px;text-align:center;font-color:gray;font-style:oblique" >                                           
						    </div>
						</form>
						<button type="button" class="blue-but"  onclick="signExcel.uploadBtn()" ><span><i class="icon-shangchuan"></i>上传调班</span></button>
                    </div> 
             </div>
             <div class="col">  
                    <div class="button-wrap ml-4">
                    	<button id="downloadScheduleTemplate" class="blue-but"><span><i class="icon download"></i>下载模板</span></button> 
                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button>       
                   		
                    </div> 
              </div> 
			</div>
			
			<div class="datatitle" style="padding-left:0px;">
				<ul class="fl jui-tabswitch" id="parent">
					<li id="listTab" rel="#old" class="active"><strong>调班</strong></li>		
					<li id="detailTab" rel="#new"><strong>调班记录</strong></li>
				</ul>
			</div>
		</div>	
			
			<div>
				<div id="showTitle" style="display: none;">
	                 <h2 id="titleClass" align="center" style="font-size: 32px">
	                 </h2>
	             	<h6> <label class="w">组别：<span id="groupName"></span>			
             	 	 <span id="classStatus"></span>
             	 	 </label>   
             	 	 </h6>
             	 </div>
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
			<div class="tablebox"></div>
			
			
			
			
			
          
      	<div class="paging" id="commonPage"></div>
      	
      	
   <div class="content second" style="overflow-x:auto; width:100%; display:none;">
            <div>
	            <table>
					 <thead>
						<tr>
							<th style="overflow-x:auto;width:100px;text-align:center;">流水编号</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">组别</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">被调班员工人数</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">调班日期</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">调班人</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">状态</th>			                
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
							<strong><i class="icon add-user"></i>调班</strong>
							<i onclick="closeCounterDiv();" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
							
						</div>
						<div class="form-main">

							<form id="updateForm" class="updateForm">
								<input type="hidden" id="id" name="id" value=""/>
								<input type="hidden" id="version" name="version" value=""/>
								<input type="hidden" id="classEmpId" name="empId" value=""/>
								<input type="hidden" id="emp_date"  value=""/>
								
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
										<label class="w"  style="width:130px;"></label>
										<span id="pb_weekDay"></span>
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">选择班次:</label>
											
										<select id="classSet" class="select_v1">
                     					</select>
									</div>
								</div>
								<!-- <div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">备注:</label>
											
										<input id="remark" name="remark" type="text" class="form-text" value="">
									</div>
								</div> -->
								
							</form>

							<div class="col" id="classbut">
								<!--  <button class="red-but btn-middle" onclick="reconfirm()"><span><i class="icon"></i>确定</span></button> 
              					 <button class="red-but btn-middle" onclick="closeCounterDiv()"><span><i class="icon"></i>取消</span></button>      
 -->						</div>
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
									<label class="w"  style="width:130px;">确认要修改此排班？</label>
								</div>
							</div>	
						</form>

						<div class="col">
							<button class="red-but btn-middle" onclick="save()"><span><i class="icon"></i>确定</span></button> 
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