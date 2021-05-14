<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<link rel="icon" href="data:;base64,=">
<title>员工查询</title>
<%@include file="../../common/common2.jsp"%>
<link rel="stylesheet" href="http:<%=oaUle%>/ule/oa/c/personsel.css?v=2019101701">
<script src="../js/util/personsel.js?v=20191105"></script>
<script type="text/javascript" src="../js/base/position/quitNotify.js?v=20200701"/></script>
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
		height:36px;width:140px;
		margin-left: 0px;
	}
	
	.active {
      color: white;
      background-color: black;
    }
    .none {
      background-color: whitesmoke;
    }

	
	input.Wdate{
		width:178px;
	}
	input{
		width:138px;
	}
	.w{
		width:65px;
	}
	label {
    width: 70px;
	}
	.updateUserSpan i{
	  margin-top: 10px;
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
	                    	<input type="text" style="width: 50%;" class="form-text" name="code" value="">
	                    </div>
	                    <div class="form">
	                    	<label class="">员工姓名：</label>
	                    	<input type="text" style="width: 50%;"   class="form-text" name="cnName" value="">
	                    </div>
	                     <div class="form">
	                        <label class="">部门：</label>
	                        <select id="firstDepart" style="width: 150px;"  name="firstDepart" class="select_v1">
	                        </select>
	                    </div>
	                	<div class="form">
	                        <label class="">职位：</label>
	                        <input type="text" style="width: 50%;"  class="form-text" name="positionName" value="">	           
	                    </div>
	                    <div class="form">
	                        <label class="">状态：</label>
	                        <!--  0.在职 1.离职   2.待离职  3.已提出离职 （前台web做出离职操作） -->
	                        <select name="jobStatus" style="width:100px;" class="select_v1">
	                        	<option  value="">全部</option>
	                        	<option value="0">在职</option>
	                        	<option value="2">待离职</option>
	                        	<option value="3">已提出离职</option>
	                        </select>
	                    </div>
	                </div>
              		</form>
	                
	                <div class="col">  <!-- style="display:inline-block; margin:16px 0px 0px 0px;" -->
	                    <div class="button-wrap ml-4" >
	                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
	                        <button id="export" class="blue-but"><span><i class="icon add"></i>导出</span></button>    
	                    </div> 
	                </div> 
                </div>
			</div>

		<table>
			<!-- <colgroup>
				标题列，可以在这里设置宽度
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col width="1%" display = "none">
			</colgroup> -->
			<thead>
				<tr>
					<th style='text-align:center;'>员工编号</th>
					<th style='text-align:center;'>员工姓名</th>
					<th style='text-align:center;'>员工类型</th>
					<th style='text-align:center;'>入职日期</th>
					<th style="overflow-x:auto;width:100px;text-align:center;color:red;">离职日期</th>
					<th style='text-align:center;'>部门</th>
					<th style='text-align:center;'>职位</th>
					<th style='text-align:center;'>汇报对象</th>
					<th style='text-align:center;'>部门负责人</th>
					<th style='text-align:center;'>状态</th>
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
	<!-- 改 删 操作框 -->
	<!-- 修改开始-->
   	<!-- 更新 -->
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top:10%; left: 27.8% ;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>员工离职操作</strong>
						</div>
						<div class="form-main">
							<form id="updateForm" class="updateForm">
								<input type="hidden" id="id" name="id" value=""/>
								<input type="hidden" id="quitIds" name="ids" value=""/>
								<input type="hidden" id="version" name="version" value=""/>
								<input type="hidden" id="firstEntryTime2" name="firstEntryTime" value=""/>
								<input type="hidden" id="beforeWorkAge" name="beforeWorkAge" value="">
								<input type="hidden" id="quitUpdateBefore" name="quitUpdateBefore" value="">
								<div class="col">
									<div class="form">
										<label class="w" >员工编号</label><input type="text"
											class="form-text" name="code" value="" readonly="true">
									</div>
									<div class="form">
										<label class="w" >员工姓名</label><input type="text"
											class="form-text" name="cnName" value="" readonly="true">
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w" >员工类型</label><input type="text"
											class="form-text" name="empTypeName" value="" readonly="true">
									</div>
									<div class="form">
										<label class="w" >所属部门</label><input type="text"
											class="form-text" name="departName" readonly="true">
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w" >职位信息</label><input type="text"
											class="form-text" name="positionName" readonly="true">
									</div>
									<div class="form">
										<label class="w" >离职日期</label>
										<input id="quitTime" type="text" class="Wdate" name="quitTime" onfocus="WdatePicker({skin:'whyGreen',minDate: '%y-%M-%d' })"/>
	                    	      		<!-- 改期 -->	 
	                    	      	    <input id="quitTimeUpdateDate" class="Wdate" name="quitTimeUpdateTime" type="text" onfocus="WdatePicker({skin:'whyGreen',minDate: '%y-%M-%d' })"/>
									</div>
								</div>
								
								<div class="col">
									<div class="form">
										<label class="w" >通知人员</label>
									</div>
									<input type="text"
											class="form-text" name="pageStr" id="clickMe" style="border: 1px solid #d9d9d9; width:176px; height: 34px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;">
									
								</div>
								<div class="col">
									<div class="form">
										<label class="w" style="width: 85px;">默认发送人员</label>
										<span class="updateUserSpan">
										  	 <c:forEach items="${sendMailEmps}" var="i" varStatus="status" >
										    	<span id="${i.id}" <c:if  test="${status.index!=0 }"> style="margin-left:25px;"</c:if> >${i.displayName}<input type="hidden" name="quitNotiesCode" value="${i.displayCode}"><i class="del_i" onclick="delThisUser(${i.id})"></i></span>
										     </c:forEach>
									     </span>
									    &nbsp;&nbsp;&nbsp;&nbsp;<i  onclick="insertUserShow()" class="icon-qita"></i>									     
									</div>
								</div>
								
								<div class="col">
									<div class="form">
										<label class="w" style="width: 100px;" >另通知人员邮箱(多个人以,隔开)</label>
									</div>
									<input type="text"
											class="form-text" name="sendEmail" id="sendEmail" style="border: 1px solid #d9d9d9; height: 34px; margin-left:-8px; text-indent: 10px;width:380px; color: #666; font-size: 12px;">
									
								</div>
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button id="zcqr" class="red-but" onclick="reconfirmQuit();"  style="width:120px;">
										<span>确认</span>
									</button>
									<button id="zcqx" class="small grey-but"
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
	</div>         
	<!-- 取消离职 离职时间清空  -->
	<div class="commonBox popbox" id="updateDiv2" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 29% ;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>取消离职</strong>
						</div>
						<div class="form-main">

							<form id="updateForm2" class="updateForm">
								<input type="hidden" id="id2" name="id" value=""/>
								<input type="hidden" id="cancelQuitIds" name="ids" value=""/>
								<input type="hidden" id="version2" name="version" value=""/>
								<input type="hidden" name="quitTime" id="quitTimeEnd" value=""/>						
								<input type="hidden" name="jobStatus" value="0"/>	
								<input type="hidden" name="firstEntryTime" id="firstEntryTimeEnd"/>	
								<input type="hidden" id="beforeWorkAge2" name="beforeWorkAge" value="">
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">员工姓名</label><input type="text"
											class="form-text" name="cnName" value="" readonly="true">
									</div>
								</div>
								<div class="col">
									<div class="form">
											<label class="w" style="width:130px;" >离职日期</label>
											<input id="cancelQuitTime" type="text"  name="quitTime" readonly="readonly"/>	                    	      
		                    	    </div>
								</div>
								<div class="col">																		
									<div class="form">
										<label class="w"  style="width:130px;">所属部门</label><input type="text"
											class="form-text" name="departName" readonly="true">
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">职位信息</label><input type="text"
											class="form-text" name="positionName" readonly="true">
									</div>
								</div>
								
								<div class="col">
									<div class="form">
										<label class="w" style="width: 130px" >通知人员</label>
									</div>
									<input type="text"
											class="form-text" name="pageStr" id="cancelQuit" style="border: 1px solid #d9d9d9; width:176px; height: 34px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;">									
								</div>
								
								<div class="col">
									<div class="form">
										<label class="w" style="width: 85px;">默认发送人员</label>
										<span class="updateUserSpan">
										  	 <c:forEach items="${sendMailEmps}" var="i" varStatus="status" >
										    	<span id="${i.id}" <c:if  test="${status.index!=0 }"> style="margin-left:25px;"</c:if> >${i.displayName}<input type="hidden" name="quitNotiesCode" value="${i.displayCode}"><i class="del_i" onclick="delThisUser(${i.id})"></i></span>
										     </c:forEach>
									     </span>
									    &nbsp;&nbsp;&nbsp;&nbsp;<i  onclick="insertUserShow()" class="icon-qita"></i>									     
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w" style="width: 100px;" >另通知人员邮箱(多个人以,隔开)</label>
									</div>
									<input type="text"
											class="form-text" name="sendEmail" id="cancleSendEmail" style="border: 1px solid #d9d9d9; height: 34px; margin-left:-8px; text-indent: 10px;width:380px; color: #666; font-size: 12px;">
									
								</div>			
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button style="width :120px" class="red-but" onclick="cancleQuit();">
										<span>取消离职</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv('updateDiv2');">
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
    
	<!-- 离职 -->
    
    <div class="commonBox popbox" id="deleteDiv2" style="display:none">
	  <input type="hidden" id="deleteId" name="deleteId"/>
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 20%; left: 33%;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>离职信息确认</strong>
						</div>
						<div class="form-main">

							<div class="col">
								<div class="form">
									是否修改此员工离职信息？
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button  class="red-but" onclick="updatePostion();" style="width:120px;">
										<span>确定</span>
									</button>
									<button  class="small grey-but"
										onclick="closeDiv('deleteDiv2');" style="width:120px;">
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
	
	
	<div class="commonBox popbox" id="deleteDiv1" style="display:none">
	  <input type="hidden" id="deleteId" name="deleteId"/>
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 20%; left: 33%;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>离职信息确认</strong>
						</div>
						<div class="form-main">

							<div class="col">
								<div class="form">
									是否修改此员工离职时间信息？
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button  class="red-but" onclick="updateEmpQuitTime();" style="width:120px;">
										<span>确定</span>
									</button>
									<button  class="small grey-but"
										onclick="closeDiv('deleteDiv1');" style="width:120px;">
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
	
	
	<div class="commonBox popbox" id="updateUesrDiv" style="display:none">
	  <input type="hidden" id="deleteId" name="deleteId"/>
		<div class="popbox-bg"></div>
		<div class="popbox-center conf" style="top: 20%; position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>新增默认发送人员</strong>
						</div>
						
						<div class="form-main">
						<form id="insertUserForm" class="updateForm">
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;">发送人员名称</label><input type="text"
											class="form-text" name="displayName" value="" id="displayName">
									</div>
								</div>
								<div class="col">																		
									<div class="form">
										<label class="w"  style="width:130px;">邮箱地址</label><input type="text"
											class="form-text" name="displayCode" id="displayCode">
									</div>
								</div>
								<input type="hidden" name="description"  value="离职默认通知">
								<input type="hidden" name="code" value="quitNoticeEmail" >
								<input type="hidden" name="companyId"  value="1">
							</form>		
							<div class="col">
								<div class="button-wrap ml-4">
									<button  class="red-but" onclick="insertUser();" style="width:120px;">
										<span>确定</span>
									</button>
									<button  class="small grey-but"
										onclick="closeDiv('updateUesrDiv');" style="width:120px;">
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
    </div>
    <div class="paging" id="commonPage"></div>
            

</body>
</html>
