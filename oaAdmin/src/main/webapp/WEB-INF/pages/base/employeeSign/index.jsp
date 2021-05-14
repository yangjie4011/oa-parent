<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>员工签到</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/employeeSign/index.js?v=20180918"/></script>
<style type="text/css"> 
	th,td {
	text-align:center !important; 
	word-break: keep-all;
	white-space:nowrap;
	}
	.redfont{
		color: red;
	}
	.tdRightSolid{
		border-right:#333 solid 1px;
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
	                    	<input type="text" id="singCode" class="form-text" name="code" value="">
	                    </div>
	                    
	                    <div class="form">
	                    	<label class="w">员工姓名：</label>
	                    	<input type="text" id="singName"  class="form-text" name="cnName" value="">
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
            
            <p style="color:red;">提醒：员工签到只能本人操作，若发现有代签到者，根据公司制度，员工本人与代签到者将按弄虚作假严重违反公司规章制度处理，严重者将予以开除!</p>
			</br>
			<p>当前时间：<span id="currentTime"></span></p>
			</br>
			<table>
				<colgroup>
				<!-- 标题列，可以在这里设置宽度 -->
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
					<th>部门</th>
					<th>职位</th>
					<th>汇报对象</th>
					<th>操作</th>
				</tr>
			</thead>
	              <tbody id="reportList">
	              </tbody>
            </table>
        </div>     
        <div class="paging" id="commonPage"></div>
        
    <div class="commonBox popbox" id="signDiv" style="display:none">
	    <input type="hidden" id="employeeId" name="employeeId"/>
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 20%; left: 33%;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>员工签到</strong>
						</div>
						<div class="form-main">

							<div class="col">
								<div class="form" id="confirmTitle">
									
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button  class="red-but" onclick="sign();" style="width:120px;">
										<span>确定</span>
									</button>
									<button  class="small grey-but"
										onclick="closeDiv('signDiv');" style="width:120px;">
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
	
   <div class="commonBox popbox" id="successDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 20%; left: 33%;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>员工签到</strong>
						</div>
						<div class="form-main">

							<div class="col">
								<div class="form">
									签到成功！
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button  class="blue-but" onclick="closeDiv('successDiv');" style="width:120px;">
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
	
</body>
</html>