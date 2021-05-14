<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>年休假基数</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/leaveManager/leave_radix.js?v=20190425"/></script>
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
			<div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1"></i></div>
            	<div class="form-main">
	                
                <form id="queryform">
	            <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	            <input type="hidden" id="pageNo" value=""/>
	                <div class="col">
	                    <div class="form">
	                    	<label class="w">员工编号：</label>
	                    	<input type="text" class="form-text" name="code" value="">
	                    </div>
	                    
	                    <div class="form">
	                    	<label class="w">员工姓名：</label>
	                    	<input type="text" class="form-text" name="cnName" value="">
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
            
			<table border="1">
				<colgroup>
				<!-- 标题列，可以在这里设置宽度 -->
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
				<col>
			</colgroup>
			<thead>
				<tr>
					<th>员工编号</th>
					<th>员工姓名</th>
					<th>入职日期</th>
					<th>部门</th>
					<th>工时制</th>
					<th>法定年假基数</th>
					<th>福利年假基数</th>
					<th>总年假天数</th>
					<th>操作</th>
					<th>修改记录</th>
				</tr>
			</thead>
	              <tbody id="reportList">
	              </tbody>
            </table>
        </div>     
        <div class="paging" id="commonPage"></div>
       
        <!-- 修改弹框 -->
        <div class="commonBox popbox" id="updateDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>${year}年休假基数修改</strong>
						</div>
						<div class="form-main">
	
							<form id="updateDivForm" class="updateDivForm">
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:130px;">员工编号：</label>
										<input type="text" class="form-text" id="codeUpdate" value="" readonly="true">
									</div>
								</div>
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:130px;">员工姓名：</label>
										<input type="text" class="form-text" id="nameUpdate" value="" readonly="true">
									</div>
								</div>
							
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:130px;">法定年假基数：</label>
										<input type="text" class="form-text" id="legalCount" value="">&nbsp;天
									</div>
								</div>
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:130px;">福利年假基数：</label>
										<input type="text" class="form-text" id="welfareCount" value="">&nbsp;天
									</div>
								</div>
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:130px;">备注：</label>
										<input type="text" class="form-text"  id="remarkRecord">
									</div>
								</div>
								
								
								<div class="col" style="margin-bottom:5px;">
									<div class="form">
										<label class="w"  style="width:130px;">&nbsp;</label>
										<input style="width:13px;" type="radio" name="updateType" value="0" checked="checked"><span style="float:left;">仅当年修改</span><span style="float:left;width:20px;">&nbsp;&nbsp;&nbsp;</span><input style="width:13px;" type="radio" name="updateType" value="2"><span style="float:left;">永久修改</span>
									</div>
								</div>
							</form>
	
							<div class="col" style="margin-top:30px;">
								<div class="button-wrap ml-4">
									<button class="red-but" onclick="updateLeaveRadix();"  style="width:120px;">
										<span>确定</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv();" style="width:120px;">
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
	<!-- 修改弹框 -->
	  
    <!-- 记录弹框 -->
    <div class="commonBox popbox" id="recordDiv" style="display:none;">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 0.5%; left: 0.5%;width:99%;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
				    <i onclick="closeDiv();" class="mo-houtai-box-close"></i>
					</br>员工编号：<span id="codeQuery"></span>&nbsp;&nbsp;员工姓名：<span id="nameQuery"></span>				
							<br>
					<div class="form-wrap" style="overflow-y:auto;height:400px;">
					    <div class="title">
							<strong><i class="icon"></i>年休假基数修改记录</strong>
						</div>
					    <table border="1">
							<colgroup>
								<!-- 标题列，可以在这里设置宽度 -->
								<col>
								<col>
								<col>
								<col>
								<col>
								<col>
								<col>
						    </colgroup>
							<thead>
								<tr>
									<th>修改日期</th>
									<th>修改前法定基数</th>
									<th>修改后法定基数</th>
									<th>修改前福利基数</th>
									<th>修改后福利基数</th>
									<th>修改类型</th>
									<th>备注</th>
									<th>操作人</th>
								</tr>
							</thead>
				            <tbody id="recordList">
				               <tr>
									<td>2018-07-21</td>
									<td>5</td>
									<td>6</td>
									<td>5</td>
									<td>6</td>
									<td>永久修改</td>
									<td>范德西</td>
								</tr>
								<tr>
									<td>2018-07-21</td>
									<td>5</td>
									<td>6</td>
									<td>5</td>
									<td>6</td>
									<td>永久修改</td>
									<td>范德西</td>
								</tr>
								<tr>
									<td>2018-07-21</td>
									<td>5</td>
									<td>6</td>
									<td>5</td>
									<td>6</td>
									<td>永久修改</td>
									<td>范德西</td>
								</tr>
				            </tbody>
			            </table>
					</div>
				</div>
			</div>
		</div>
	</div> 
	<!-- 记录弹框 -->
	<input id="employeeId" type="hidden"/>
</body>
</html>