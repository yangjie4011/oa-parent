<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>排班员工管理</title>
<%@include file="../../common/common2.jsp"%>

<link rel="stylesheet" href="http:<%=oaUle%>/ule/oa/c/personsel.css?v=2019101701">
<script src="../js/util/personsel.js?v=20191105"></script>
<script type="text/javascript" src="../js/base/schedule/empManagement.js?v=2020022101"/></script>
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
				<li id="leaderTab" rel="#old"  class="active"><strong>查询</strong></li>					
				<c:if  test="${empClassUpdate}">
					<li id="hrTab" rel="#old"><strong>修改</strong></li>	
				</c:if>					
		</ul>
	</div>
	<div class="content first" style="overflow-x:auto" id="selectedBut">
            
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
                    	<label class="w">员工编号：</label>
                    	<input type="text" class="form-text" id="code" name="code" value="">
                    </div>
                    
                    <div class="form">
                    	<label class="w">员工姓名：</label>
                    	<input type="text" class="form-text" id="cnName" name="cnName" value="">
                 </div>
                 <!-- <div class="form">
                        <label class="w">部门：</label>
                        <select id="firstDepart" name="departId" class="select_v1 firstDepart">
                        <option value= "">请选择</option>

						</select>
                    </div>  --> 
                    <div class="form">
	                        <label class="w">汇报对象：</label>
	                        <input type="text" class="form-text" id="noticeStr" name="pageStr" value="">
	                </div>
                </div>
               
               
                <div class="col">
                    <!-- <div class="form">
                    	<label class="w">排班组别：</label>
                    	<input type="text" class="form-text" id="code" name="empClass" value="">
                    </div> -->
                    
                    <div class="form">
                    	<label class="w">排班人：</label>
                    	<input type="text" class="form-text" id="cnName" name="scheduleCnName" value="">
                    </div>
                    
                    
                    <div class="col">		                
	                    <div class="form">
	                        <label class="w">排班部门：</label>
	                        <select id="firstDepart" name="departId" class="select_v1">  
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
           
                    
                    
                </div>
             </form> 
            <div class="col">  
                    <div class="button-wrap ml-4">
                    	<button  id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
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
			                <th style="overflow-x:auto;width:100px;text-align:center;">是否排班</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">排班组别</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">排班人</th>
						</tr>
					  </thead>
		              <tbody id="reportList">
		              </tbody>
	            </table>
            </div>
        </div>
        <div class="paging" id="commonPage"></div>
            

      	<div  id="updateDiv" style="display:none">
		
		
		<div class="popbox-center" style="padding-top:100px;padding-left:100px;">
		<form id="selectform">
		    	<div class="form-main">
		           <div class="col">
					<div class="form">
						<label class="w"  style="width:130px;">选择员工:</label>
					</div>
					<input type="text"
							class="form-text"  name="pageNameStr" id="clickMe" style="border: 1px solid #d9d9d9; width:176px; height: 34px; margin-left:-8px; text-indent: 10px; color: #666; font-size: 12px;">
		      	</div>
		           <div class="col">                 
		             <div class="form">
		             	<label class="w" style="width:130px;height:25px;">更改排班:</label>
					<select id="whetherScheduling" style="width: 150px;"  name="pageStr" class="select_v1">
						<option value="yes">是</option>
						<option value="no">否</option>
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
			  
	<div class="commonBox popbox" id="updateZcQrDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 20%; left: 33%;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>修改员工排班再次确认</strong>
						</div>
						<div class="form-main">

							<div class="col">
								<div class="form">
									是否确认修改所选员工为排班员工
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button  class="red-but" onclick="update();" style="width:120px;">
										<span>确定</span>
									</button>
									<button  class="small grey-but"
										onclick="closeDiv('updateZcQrDiv');" style="width:120px;">
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
		
		
	</div>

      	
</body>
</html>
