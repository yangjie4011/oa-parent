<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>日常考勤管理</title>
<%@include file="../../common/common2.jsp"%>
<link rel="stylesheet" href="http:<%=oaUle%>/ule/oa/c/personsel.css?v=2019101701">
<script src="../js/util/personsel.js?v=20191105"></script>
<script type="text/javascript" src="../js/base/attnManager/revisionAttendance.js?v=20190729"/></script>
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
.form label.w{
	width: 78px;
}
</style>
</head>
<body>
   <div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<c:if test="${relativeFlag }">
				<li id="listTab" rel="#old" class="active"><strong>相对修改</strong></li>
			</c:if>
			<c:if test="${absoluteFlag }">
			    <li id="detailTab" rel="#new"><strong>绝对修改</strong></li>
			</c:if>
			<c:if test="${queryFlag }">
				<li id="thridTab" rel="#third"><strong>修改查询</strong></li>
			</c:if>
		</ul>
	</div>
	<div class="form-wrap">
			  
			</div>
		<!-- 第一个table -->
	<c:if test="${relativeFlag }">
		<div class="content daiban" style="overflow-x:auto">         
					<div class="popbox-main" style="">
						<div class="cun-pop-content">
							<div class="form-wrap">
								<div class="title">
									<strong><i class="icon add-user"></i>相对修改</strong>
								</div>
								<div class="form-main">
									<form id="relativeForm" class="relativeForm">
									
									<div class="col"><label class="w" >相对修改定义：为所选员工输入一条考勤记录，报表中标注为“HR输入”，与当天其他考勤申请和单据做考勤对比计算</label></div>
										<div class="col">
											<div class="form">
											<input type="hidden" value="2" name="dataType" id="dateType">
											<input type="hidden" name="employeeIds" id="relativeemployeeIds">
												<label class="w" >选择员工:</label><input type="text"
													class="form-text" value="" id="relativeclickMe" >
											</div>
										</div>
										<div class="col">
											<div class="form">
												<label class="w" >考勤日期:</label><input id="relativeAttnTime" type="text" class="Wdate" name="attnDate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd' ,onpicked: relativeChangeTime})"/>
											</div>
										</div>
										<div class="col">
											<div class="form">
												<label class="w" >上班考勤:</label>
													<input  type="text" class="Wdate" id="relativeStatr" name="attnStartTime" disabled="disabled" onfocus="WdatePicker({ dateFmt: ' HH:mm:ss' })"/>												
											</div>
										</div>
										<div class="col">
											<div class="form">
												<label class="w" >下班考勤:</label>
												<input  type="text" class="Wdate"id="relativeEnd" name="endTime" disabled="disabled" onfocus="WdatePicker({ dateFmt: ' HH:mm:ss' })"/>													
											</div>
										</div>
										
										<div class="col">
											<div class="form">
												<label class="w" >考勤时间输入</label>
												<input  type="text" class="Wdate"id="attnTimePrint" name="startTime" onfocus="WdatePicker({ dateFmt: ' HH:mm:ss' })"/>													
											    <div style="float: left; width: 80px;margin-left:8px;">
													<input style="width:15px;" type="checkbox" id="nextDay">&nbsp;次日  
												</div>
											</div>
										</div>
										
										<div class="col">
											<div class="form">
												<label class="w" >说明:</label>
											</div>
											<textarea placeholder="只限输入50字" name="remark"  id="relativeRemark" style="border: 1px solid #d9d9d9; width:176px; height: 55px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8"
											 onKeyUp="if(this.value.length>100) this.value=this.value.substr(0,50)" maxlength="50"></textarea>		
										</div>
										<div class="col" >
											<div class="form" style="width: 1600px">
												<label class="w" >修改类型:</label>
												相对修改
											</div>
										</div>
										<div class="col" ></div>
									</form>
		
									<div class="col">
										<div class="button-wrap ml-4">
											<button id="relativezcqr" class="red-but" onclick="relativeSure();"  style="width:120px;">
												<span>确认</span>
											</button>
											<button id="relativezcqx" class="small grey-but"
												onclick="closeDiv('updateDiv');" style="width:120px;">
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
       </c:if> 
       <c:if test="${absoluteFlag }">
       <div class="content yiban" style="overflow-x:auto;display:none;">
            <div class="popbox-main" style="">
						<div class="cun-pop-content">
							<div class="form-wrap">
								<div class="title">
									<strong><i class="icon add-user"></i>绝对修改</strong>
								</div>
								<div class="form-main">
									<form id="absolutForm" class="absolutForm">
									<div class="col"><label class="w" >绝对修改定义：所选员工当天考勤仅按修改的数据做最终记录，任何其他申请或单据无法影响当天考勤，报表中标注为“HR修改”</label></div>
									
										<div class="col">
											<div class="form">
											<input type="hidden" value="3" name="dataType" id="absolutDateType">
											<input type="hidden" name="employeeIds" id="absolutemployeeIds">
												<label class="w" >选择员工:</label><input type="text"
													class="form-text"  value="" id="absolutclickMe"  >
											</div>
										</div>
										<div class="col">
											<div class="form">
												<label class="w" >考勤日期:</label><input id="absolutAttnDate" type="text" class="Wdate" name="attnDate" onfocus="WdatePicker({ dateFmt: 'yyyy-MM-dd',onpicked: absolutChangeTime })"/>
											</div>
										</div>
										<div class="col">
											<div class="form">
												<label class="w" >上班考勤:</label>
													<input type="text" class="Wdate" id="absolutStartTime" name="startTime" onfocus="WdatePicker({ dateFmt: ' HH:mm:ss' })"/>	
													
													<span id="upAttnTimeSpan"><input style="width:15px; margin-left:8px;" type="CheckBox" name="upCard" id="upAttnTime" value="1"    onclick="startTimeHover()" >&nbsp;未刷卡</span>
											</div>
										</div>
										<div class="col">
											<div class="form">
												<label class="w" >下班考勤:</label>
												<input  type="text" class="Wdate"  id="absolutEndTime" name="endTime" onfocus="WdatePicker({ dateFmt: ' HH:mm:ss' })"/>													
												
												<div style="float: left; width: 80px;">
													<span id="downAttnTimeSpan"><input  style="width:15px; margin-left:8px;" type="CheckBox" id="downAttnTime" value="1" name="downCard" onclick="endTimeHover()">&nbsp;未刷卡</span> 	
												</div>
												<div style="float: left; width: 80px;">
													<input style="width:15px;" type="CheckBox" size="2"  name="ciri" value="3" id="AttntowDay">&nbsp;次日  
												</div>
											</div>
										</div>
										
										<div class="col">
											<div class="form">
												<label class="w" >说明:</label>
											</div>
											<textarea placeholder="只限输入50字" name="remark"  id="absolutRemark" style="border: 1px solid #d9d9d9; width:176px; height: 55px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8"
											onKeyUp="if(this.value.length>100) this.value=this.value.substr(0,50)" maxlength="50"></textarea>		
										</div>
										<div class="col" >
											<div class="form" style="width: 1600px">
												<label class="w" >修改类型:</label>
												绝对修改
											</div>
										</div>
										
										<div class="col" ></div>
									</form>
		
									<div class="col">
										<div class="button-wrap ml-4">
											<button id="absolutezcqr" class="red-but" onclick="absolutSure();"  style="width:120px;">
												<span>确认</span>
											</button>
											<button id="absolutezcqx" class="small grey-but"
												onclick="closeDiv('updateDiv');" style="width:120px;">
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
        </c:if>
        <!-- 第三个table -->
        <c:if test="${queryFlag }">
        <div class="content attnHistoryQuery" style="overflow-x:auto;display:none;">
        <div class="form-wrap">
			<div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"></i></div>
            	<div class="form-main" style="min-width:1700px;">
	                
                <form id="queryform">
	            <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	            <input type="hidden" id="pageNo" name="page" value=""/>
	                <div class="col">
	                    <div class="form">
	                    	<label class="">员工编号：</label>
	                    	<input type="text" style="width: 50%;" class="form-text" name="code" value="">
	                    </div>
	                    <div class="form">
	                    	<label class="">员工姓名：</label>
	                    	<input type="text" style="width: 50%;"   class="form-text" name="cnName" value="">
	                    </div>
	                     <div class="form">
	                        <label class="">部门：</label>
	                        <select id="firstDepart" style="width: 150px;"  name="departId" class="select_v1">
	                        </select>
	                    </div>
	                	
	                    <div class="form">
	                        <label class="">类型：</label>
	                        <select name="type" style="width:100px;" class="select_v1">
	                        	<option  value="">全部</option>
	                        	<option value="2">相对修改</option>
	                        	<option value="3">绝对修改</option>
	                        </select>
	                    </div>
	               </div>
	              	 <div class="col">     
	                    <div class="form">
	                    	<label class="">考勤日期：</label>
	                    	<input id="startTime" type="text" class="Wdate" name="startTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
	                    	       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    	<label class="margin-left"> &nbsp;至&nbsp;&nbsp;</label>
							<input id="endTime" type="text" class="Wdate" name="endTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" 
							       readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
	                    </div>
	                </div>
              		</form>
	                
	                <div class="col">  <!-- style="display:inline-block; margin:16px 0px 0px 0px;" -->
	                    <div class="button-wrap ml-4" >
	                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button>   
	                    </div> 
	                </div> 
                </div>
			</div>
        
            <div>
	            <table>
	                 <thead>
	                    <tr>
	                        <th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">考勤日期</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">类型</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">输入考勤时间</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">修改后上班时间</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">修改后下班时间</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">操作时间</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">操作人</th>
			               	<th style="overflow-x:auto;width:100px;text-align:center;">说明</th>
			               	
						</tr>
					  </thead>
		              <tbody id="reportList">
		              </tbody>
	            </table>
            </div>
        </div>
        </c:if>
        <div class="paging" id="commonPage"></div>
  <!--相对修改再次确认-->
    
    <div class="commonBox popbox" id="relativeDiv" style="display:none">
	  <input type="hidden" id="deleteId" name="deleteId"/>
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 20%; left: 33%;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap" style="height:130px">
						<div class="title">
							<strong><i class="icon"></i>相对修改再次确认</strong>
						</div>
						<div class="form" style="height: 100px; width:450px;">	
							<div class="col">
								<div class="form">			
									是否确认输入员工的考勤时间为 :<span id="upRelativeAttn"></span>
								</div>
							</div>			
							<div class="col">
								<div class="button-wrap ml-4">
									<button  class="red-but" style="width:120px;" onclick="zcrelativeSure();">
										<span>确定</span>
									</button>
									<button  class="small grey-but" style="width:120px;"
										onclick="closeDiv('relativeDiv');">
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
	
 <!--绝对修改再次确认-->
    
    <div class="commonBox popbox" id="absoluteDiv" style="display:none">
	  <input type="hidden" id="deleteId" name="deleteId"/>
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 20%; left: 33%;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap" style="height:200px">
						<div class="title">
							<strong><i class="icon"></i>绝对修改再次确认</strong>
						</div>
						<div class="form-main">
							<div class="col">
								<div class="form" style="height: 100px; width:450px;">									
									提示：是否确定修改员工的考勤时间为
									<br>上班考勤:<span id="upAbsoluteAttn"></span>
									<br>下班考勤:<span id="downAbsoluteAttn"></span>
									<div class="col">
										<div class="button-wrap ml-4" style="width: 430px;">
											<button  class="red-but" onclick="zcabsolutSure();" style="width:120px;">
												<span>确定</span>
											</button>
											<button  class="small grey-but"
												onclick="closeDiv('absoluteDiv');" style="width:120px;">
												<span>取消</span>
											</button>
										</div>
									</div>
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
		<div class="popbox-center conf" style="margin-top: -15%; position: absolute;">
			<div class="popbox-main" style="height: 210px;">
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
</body>
</html>