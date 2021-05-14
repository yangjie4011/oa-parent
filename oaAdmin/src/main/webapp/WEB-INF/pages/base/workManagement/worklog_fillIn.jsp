<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>工作日志填写</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/workManagement/worklog_fillIn.js?v=20200310"/></script>
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
			<li><strong>工作日志填写</strong></li>
		</ul>
	</div>
	<div class="content" style="overflow-x:auto">
       		<div class="form-wrap seachWorkLog">    
				   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"  onclick="getButton(this)"></i></div>
	               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId}"/>
	               <form id="queryform">
		               <div class="form-main">
		                  
			               <div class="col">                 
				                <div class="form">
				                	<label class="w">月份</label>
									<input id="month" type="text" class="Wdate" name="month" onClick="WdatePicker({dateFmt:'yyyy-MM'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM'});" value="${month}" />
				                </div>                                                  
			               </div>
			                                
		                </div>
	             </form>
	            <div class="col">  
	                   <div class="button-wrap ml-4">
	                       <button onclick="queryWorkLogDetalInfoByMonth()" class="red-but"><span><i class="icon search"></i>查询</span></button>       
	                   </div> 
	    		</div> 
          
			</div>
	
			<div class="form-wrap saveWorkLogDiv" style="display:none;">    
			      
			      <div class="form-main" style="height:auto;">
		               <div class="col" style="height:auto;">                 
			                <div class="form" style="height:auto;">
			                	<label class="w" style="width:130px;height:auto;">员工姓名</label>
								<input type="text"  readonly="readonly" value="${cnName}"/>
			                </div>    
			                <div class="form" style="height:auto;">
			                	<label class="w" style="width:130px;height:auto;">所属部门</label>
								<input type="text"  readonly="readonly" value="${departName}"/>
			                </div>                                                  
		               </div>
		                       
	              </div>
			      
			      <div class="form-main" style="height:auto;">
	                  
		               <div class="col" style="height:auto;">                 
			                <div class="form" style="height:auto;">
			                	<label class="w" style="width:130px;height:auto;">日期</label>
								<input id="workDate" type="text"  readonly="readonly"/>
			                </div>                                                  
		               </div>
		                       
	                </div>
			
	               <div class="form-main workContent" style="height:auto;">
	                  
		               <div class="col" style="height:auto;">                 
			                <div class="form" style="height:auto;">
			                	<label class="w" style="width:130px;height:auto;">工作内容与输出</label>
								<textarea placeholder="填写当日完成工作进度、工作任务或工作产出（限100字）" style="width:300px;height:80px;padding:0;" rows="" cols=""></textarea>&nbsp;&nbsp;<a onclick="addWorkContent();" class="btn-orange btn-large"><span><i class="icon-jia1"></i>新增</span></a>
			                </div>                                                  
		               </div>
		                       
	                </div>
	                
	                <div class="form-main workPlan" style="height:auto;">
	                 	<div class="col" style="height:auto;">                 
			                <div class="form" style="height:auto;">
			                	<label class="w" style="width:130px;height:auto;">下个工作日工作计划</label>
								<textarea placeholder="（限100字）" style="width:300px;height:80px;padding:0;" rows="" cols=""></textarea>&nbsp;&nbsp;<a onclick="addWorkPlan();" class="btn-orange btn-large"><span><i class="icon-jia1"></i>新增</span></a>
			                </div>                                                  
		               </div>
	                </div>
	                
	                <div class="form-main workProblem" style="height:auto;">
		                 <div class="col" style="height:auto;">                 
				                <div class="form" style="height:auto;">
				                	<label class="w" style="width:130px;height:auto;">遇到的困难和问题</label>
									<textarea placeholder="（限100字）" style="width:300px;height:80px;padding:0;" rows="" cols=""></textarea>&nbsp;&nbsp;<a onclick="addWorkProblem();" class="btn-orange btn-large"><span><i class="icon-jia1"></i>新增</span></a>
				                </div>                                                  
			               </div>
	                </div>
	                
	                <div class="col">  
	                   <div class="buttonDiv button-wrap ml-4">
	                      <button onclick="saveWorkLog();" class="btn-green btn-large"><span><i class="icon"></i>提交</span></button>
	                      <button onclick="closeWorkLog();" class="btn-blue btn-large"><span><i class="icon"></i>关闭</span></button>
	                   </div> 
    			   </div> 
	                
			</div>
			
			<div class="form-wrap processInfo">    
	                
			</div>
	
	 </div>	
		
	<div id="showTitle">
				
           	<div style="float:left;width:50%;overflow-x:auto">
		      <table border="1" id="detailListTitle">
				  <thead>
				  </thead>
		             <tbody>
		             </tbody>
	          </table>
	      	</div>
            <div style="float:left;width:50%;overflow-x:auto">
            	<table border="1" id="detailList">
				  <thead>
				  </thead>
	              <tbody>
	              </tbody>
          		 </table>
            </div>
    </div>
    
    <div class="commonBox popbox" id="commentDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>批示意见</strong>
						</div>
						<div class="form-main" style="height:auto;">
							<form>
								<div class="col" style="margin-bottom:0px;height:auto;">
									<div class="form" style="height:auto;">
										<label class="w" style="width:130px;height:auto;">主管意见：</label>
										<textarea id="leaderComment" style="width:178px;height:50px;padding:0;" rows="" cols=""></textarea>
									</div>
								</div>
								<div class="col" style="margin-bottom:0px;height:auto;">
									<div class="form" style="height:auto;">
										<label class="w"  style="width:130px;height:auto;">人事意见：</label>
										<textarea id="hrComment" style="width:178px;height:50px;padding:0;" rows="" cols=""></textarea>
									</div>
								</div>
							</form>
	
							<div class="col" style="margin-top:30px;">
								<div class="button-wrap ml-4">
									<button class="red-but" style="width:120px;" onclick="closeComment();">
										<span>确定</span>
									</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div> 
 	
</body>
</html>