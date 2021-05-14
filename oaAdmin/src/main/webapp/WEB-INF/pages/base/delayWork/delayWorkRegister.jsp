<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>延时工作登记</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/delayWork/delayWorkRegister.js?v=20200528"/></script>
<style type="text/css">
	th,td {
		text-align:center !important;
		word-break: keep-all;
		white-space:nowrap;
	}	
	table td {
		padding: 6px 6px;
	}
</style>
</head>
<body>
	<input type="hidden" value="0" id="flagTab">
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch" id="parent">
			<c:if  test="${supervisorRegister}"> 
				<li id="leaderTab" rel="#old"  class="active"><strong>主管登记</strong></li>	
			</c:if> 
			<c:if  test="${hrRegister}">
				<li id="hrTab" rel="#old"><strong>人事登记</strong></li>	
			</c:if>	
		</ul>
	</div>
	<div class="content first" style="overflow-x:auto">
            
            <div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
                <input type="hidden" id="pageNo" value=""/>
                <input type="hidden" id="pageNo1" value=""/>
               <form id="queryform">
                <div class="form-main">
                <!-- 代办  状态-->
               
               <div class="col">
                 <div class="form">
                        <label class="w">部门：</label>
                        <select id="departId" name="departId" class="select_v1 firstDepart">
                        <option value= "">请选择</option>
							 <c:forEach items="${departList}" var="deptList" varStatus="status">
							 	<option <c:if  test="${status.index==0 && supervisorRegister}"> selected="selected"</c:if> value= "${deptList.id}">${deptList.name}</option>             			                        	
							</c:forEach>
						</select>
                    </div>  
                    <div class="form">
	                        <label class="w">汇报对象：</label>
	                        <input type="text" class="form-text" id="leaderName" name="leaderName" value="">
	                </div>
                    
                    <div class="form">
                        <label class="w">月份：</label>
                        <input id="month" type="text" class="Wdate" name="month" onClick="WdatePicker({dateFmt:'yyyy-MM',maxDate:'%y-{%M}'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM',maxDate:'%y-{%M}'});" value="${thisMonth}" />				
                    </div>   
                </div>
               
               
                <div class="col">
                    <div class="form">
                    	<label class="w">员工编号：</label>
                    	<input type="text" class="form-text" id="code" name="empCode" value="">
                    </div>
                    
                    <div class="form">
                    	<label class="w">员工姓名：</label>
                    	<input type="text" class="form-text" id="cnName" name="empCnName" value="">
                    </div>
                </div>  
                <h4 style="color: red; padding: 32px; display:none;" id="messagePrompt">
                	提示：若下方表格中的延时工作小时数的字体以蓝色显示，则表明该天的延时工作时长与实际考勤记录不匹配，请各位leader尽快确认修改其实际延时工作时长，以便员工及时在当月使用，员工当月的出勤多余小时数仅可在当月使用，leader最晚于次月3日之前修改完成，逾期将无法登记修改，请知晓！
                </h4>
           
                    
                    
                </div>
             </form> 
            <div class="col" style="display:none;" id="queryInfo">  
                    <div class="button-wrap ml-4">
                        <button id="query"  class="red-but"><span><i class="icon search"></i>查询</span></button>
                        <button id="export" class="blue-but"><span><i class="icon download"></i>导出</span></button> 
                     </div> 
                </div> 
			</div>
            <div>
            	<div style="float:left;width:25%;">
			      <table border="1" id="reportListTitle">
					  <thead>
					  </thead>
			             <tbody>
			             </tbody>
		          </table>
		      	</div>
            <div style="float:left;width:75%;overflow-x:auto">
            	<table border="1" id="reportList">
			  	<thead>
			  	</thead>
	             <tbody>
	             </tbody>
	          	</table>
            </div>
            </div>           
        </div>
      	<div class="paging" id="commonPage"></div>


      	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>    
		<div class="popbox-center" id="updateDivCen" style="top: 0%; left: 30%">  
			<div class="popbox-main" style="">
				<div class="cun-pop-content"  id="cun-pop-content" style="height:430px;overflow-y: auto;"> 
					<div class="form-wrap">
						<div class="title">
							<strong id="titleStr"><i class="icon add-user"></i></strong>
							<i onclick="closeDiv();" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
							
						</div>
						<div class="">

							<form id="updateForm" class="updateForm">
								<input type="hidden" id="employeeId" name="employeeId" value=""/>
								
								<input type="hidden" id="id" name="id" value=""/>
								
								<input type="hidden" id="numFlag" />
								
								<input type="hidden" id="unWorkDay" />
								
								<input type="hidden" name="isConfirm" id="isConfirm"/>
								
								<input type="hidden" id="hiddenTrIndex" />
								
								<input type="hidden" id="hiddenTdIndex" />
								
								<input type="hidden" id="updateType" />
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">员工编号:</label>
											
										<span id="pb_code"></span>
									</div>
									<div class="form">
										<label class="w"  style="width:130px;">员工姓名:</label>
											
										<span id="pb_name"></span>
									</div>
								</div>
							
								<div class="col">
									<div class="form">
										<div style=" float: left;"><label class="w"  style="width:130px;">延时工作日期:</label>
										<span id="pb_date" ></span></div>
										<input type="hidden" id="delayDate" name="delayDate" value=""> 
										<div style="margin-left: 50px; float: left;">周<span id="weekStr"></span></div>
									</div>
								</div>
								
								<div class="col">
					                <div class="form">
					                    	<label class="w" style="width:130px;">延时工作起止日期:</label>
					                    	<input  type="text" class="Wdate applyStartDate" id="startDate" onblur="changeHours()"  readonly="readonly" onfocus="WdatePicker({dateFmt:'HH:mm',onpicked: changeHours});" />
											<input type="hidden"  id="startTime">
									</div>	
										
								 	<div class="form">		
											<label class="w" style="width:30px;">至</label>
											<input  type="text" class="Wdate applyStartDate" id="endDate" onblur="changeHours()"   readonly="readonly" onfocus="WdatePicker({dateFmt:'HH:mm',onpicked: changeHours});" />										
									 		<input type="hidden" id="endTime">
									 </div>
				              	</div> 
								
								
								
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">延时工作时长:</label>
										
										<input  type="text" id="workHours"  onkeyup="if(isNaN(value))execCommand('undo')" onafterpaste="if(isNaN(value))execCommand('undo')" style="width:40px;"  readonly="readonly"/>小时	
										
										<input type="hidden" id="actualHoursFlag">
									</div>
								</div>
								
								<div class="col" style="margin-bottom: margin-bottom: 15px;">
									<div class="form">
										<label class="w"  style="width:130px;">工作内容描述:</label>
										
										<textarea placeholder="限50个字以内" style="width:250px; height: 80px;" id="delayItem" name="delayItem" onkeyup="if(this.value.length>100) this.value=this.value.substr(0,50)" maxlength="50"></textarea>
										
									</div>
								</div>
							</form>
		                    <div style="text-align:center;margin-bottom:3px;">
		                    	 <button class="blue-but btn-middle" style="float:none;" id="reconfirmOrSubmit" ><span id="reconfirmOrSubmitStr"><i class="icon"></i>提交</span></button> 
              					 <button class="red-but btn-middle"  style="float:none;" onclick="deleteDelayWork(this)" id="delBut"><span><i class="icon" ></i>删除</span></button> 
              					 <button class="red-but btn-middle"  style="float:none;" onclick="closeDiv()"><span><i class="icon"></i>取消</span></button>      
		                    </div>
								
					
						</div>

					<!-- form-main end -->
				</div>
			</div>
		</div>
	</div>
	</div>

      	
</body>
</html>
