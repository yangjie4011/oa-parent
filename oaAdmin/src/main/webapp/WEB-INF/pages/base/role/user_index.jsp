<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>用户管理</title>
<%@include file="../../common/common2.jsp"%>
<link rel="stylesheet" href="http:<%=oaUle%>/ule/oa/c/personsel.css?v=2019101701">
<script src="../js/util/personsel.js?v=20191105"></script>
<script type="text/javascript" src="../js/base/role/user_index.js?v=20190920"/></script>
</head>
<body>
	<div class="content" style="overflow-x:auto">
            
            <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
            <input type="hidden" id="pageNo" value=""/>
            <div class="form-wrap" >
				<div class="title"><strong><i class="icon search1"></i>用户管理</strong><i class="icon arrow-down1"></i></div>
                <form id="queryFrom" class="queryFrom">
	                <div class="form-main" >
		                <div class="col">
		                    <div class="form"><label class="w">姓名编号：</label><input type="text" class="form-text" name="code" value=""></div>		                  					            				            			        
			            	
			            	<div class="form"><label class="w">权限角色：</label><input type="text" class="form-text" name="roleStr" value=""></div>	
			            	
			            	
			            	 <div class="form"><label class="w">状态：</label>
			            	 	<select name="isLocked" class="select_v1">
			            	 		<option value="">全部</option>
			            	 		<option value="0">可用</option>
			            	 		<option value="1">禁用</option>
			            	 	</select>
			            	 
			            	 </div>		                  		
			            	
			            </div>
	                </div>  
                </form>
                <div class="col">  
                    <div class="button-wrap ml-4">
                        <button id="query" class="blue-but"><span><i class="icon search"></i>查询</span></button>   
                    </div> 
                </div> 
                </div>
			</div>
            
			<table>
				 <thead>
					<tr>
						<th style="text-align:center;">员工编号</th>
		                <th style="text-align:center;">姓名</th>
		                <th style="text-align:center;">备注</th>
		                <th style="text-align:center;">修改人</th>
		                <th style="text-align:center;">最后修改时间</th>
		                <th style="text-align:center;">状态</th>
		                <th style="text-align:center;">操作</th>
					</tr>
				  </thead>
	              <tbody id="roleList">
		            
	              </tbody>
            </table>
            
            <div class="paging" id="commonPage">
               
            </div>
            
   
	
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top:10%; left: 27.8% ;position: absolute;">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>修改用户</strong>
						</div>
						<div class="form-main">
							<form id="updateForm" class="updateForm">
							<input type="hidden" name="id" id="updateId" value="">
								<div class="col">
									<div class="form">
										<label class="w" >员工名称</label>
										<input type="text" class="form-text" id="cnName" readonly="readonly" >
										
										<label class="w" >员工编号</label>
										<input type="text" class="form-text" id="updateCode" name="code" readonly="readonly" >
									</div>
								</div>	
								<div class="col" style="height:240px;">
									<div class="form">
									
									<div style="float: left; width:280px;">
										<label class="w" >权限角色</label>	
										<div class="form">
					                        <select id="firstDepart" name="firstDepart" class="select_v1">
					                        </select>
					                    </div>
										
										<div style="margin-top:40px;">
										 <div style="margin-left:75px;width: 175px; height:200px; overflow-y:scroll; border: 1px solid gray;">							
									   		<div id="rbacRoleList">
									   		</div>
										</div>
									</div>
								</div>	
								
								<div style="float: left;width:220px; height:240px; overflow-y:scroll; border: 1px solid gray; " >
								<ul class="updateUserSpan">
								  	 						     
								</div>
										
									
							</div>
						</div>
								
								<div class="col">	
									<div class="form">
										<label class="w" >备注</label>
										<textarea placeholder="限20个字" name="remark"  id="updateRemark"  style="border: 1px solid #d9d9d9;   width:350px; height: 55px; text-indent: 10px; color: #666; font-size: 12px;" cols="50" rows="8" onkeyup="if(this.value.length>40) this.value=this.value.substr(0,20)" maxlength="20"></textarea>	
									</div>	
								</div>
								
							</form>

							<div class="col"  style="margin-top:55px;">
								<div class="button-wrap ml-4">
									<button id="zcqx" class="small red-but" onclick="closeDiv('updateDiv');" style="width:120px;">
										<span>取消</span>
									</button>
									
									<button id="zcqr" class="blue-but" onclick="updateSaveDiv();"  style="width:120px;">
										<span>保存</span>
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
   	
   	<div class="commonBox popbox" id="updateShowDiv" style="display:none">
		<div class="popbox-bg"></div>    
		<div class="popbox-center" style="top: 10%; left: 30%">  
			<div class="popbox-main" style="">
				<div class="cun-pop-content"> 
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>再次确认</strong>
							<i onclick="closeDiv('updateShowDiv')" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
							
						</div>
						<div class="form-main">
						
								<input type="hidden" id="updateUid"/>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:130px;"> 是否保存修改内容？</label>
									</div>
								</div>	
								

							<div class="col">
								 <button class="blue-but btn-middle" onclick="update()"><span><i class="icon"></i>确定</span></button> 
              					 <button class="red-but btn-middle" onclick="closeDiv('updateShowDiv')"><span><i class="icon"></i>取消</span></button>      
							</div>
						</div>
					</div>
					<!-- form-main end -->
				</div>
			</div>
		</div>
	</div>
	
	
	<div class="commonBox popbox" id="activeDiv" style="display:none">
		<div class="popbox-bg"></div>    
		<div class="popbox-center" style="top: 10%; left: 30%">  
			<div class="popbox-main" style="">
				<div class="cun-pop-content"> 
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>再次确认</strong>
							<i onclick="closeDiv('activeDiv');" class="mo-houtai-box-close" style="width: 25px;height: 30px;"></i>
							
						</div>
						<div class="form-main">
								<input type="hidden" id="activeId"/>
								<input type="hidden" id="activeUid"/>
								<div class="col">
									<div class="form">
										<label class="w"  style="width:180px;"> 确认<sapn id="active"></sapn>用户   <sapn id="activeName"></sapn>？</label>
									</div>
								</div>	
								

							<div class="col">
								 <button class="blue-but btn-middle" onclick="forzen()"><span><i class="icon"></i>确定</span></button> 
              					 <button class="red-but btn-middle" onclick="closeDiv('activeDiv')"><span><i class="icon"></i>取消</span></button>      
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
