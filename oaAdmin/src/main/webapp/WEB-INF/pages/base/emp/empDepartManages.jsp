<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>员工部门管理</title>
<link rel="icon" href="data:;base64,=">
<%@include file="../../common/common2.jsp"%>
<link rel="stylesheet" href="http:<%=oaUle%>/ule/oa/c/personsel.css?v=2019101701">
<script src="../js/util/personsel.js?v=20191105"></script>
<script type="text/javascript" src="../js/base/emp/empDepartManages.js?v=201904252"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
</style>
</head>
<body>
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch">
			<li><strong>员工部门管理</strong></li>
		</ul>
	</div>
	<div class="popbox-center" style="padding-top:100px;padding-left:100px;">
		<form id="queryform">
		    	<div class="form-main">
		           <div class="col">
					<div class="form">
						<label class="w"  style="width:130px;">选择员工:</label>
					</div>
					<input type="text"
							class="form-text" name="pageStr" id="clickMe" style="border: 1px solid #d9d9d9; width:176px; height: 34px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;">
		      	</div>
		           <div class="col">                 
		             <div class="form">
		             	<label class="w" style="width:130px;height:25px;">调整部门:</label>
					<select id="firstDepart" style="width: 150px;"  name="firstDepart" class="select_v1">
		            </select>
		             </div>                                                  
		           </div>
		            <div class="col">    
		            </div>
		         </div>
		         <input type="hidden" name="noticeStr" id="empDepartIds" >
		   </form> 
		   <div class="col">
				<div class="button-wrap ml-4">
					<button id="zcqr" class="red-but" onclick="reconfirm();"  style="width:120px;">
						<span>确认</span>
					</button>
				</div>
			</div>
	</div>
			  
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 20%; left: 33%;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>修改员工部门再次确认</strong>
						</div>
						<div class="form-main">

							<div class="col">
								<div class="form">
									是否修改此员工部门？
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button  class="red-but" onclick="update();" style="width:120px;">
										<span>确定</span>
									</button>
									<button  class="small grey-but"
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