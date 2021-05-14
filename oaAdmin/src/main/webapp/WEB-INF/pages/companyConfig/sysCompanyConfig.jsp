<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>公司配置</title>
<%@include file="../common/common2.jsp"%>
<script type="text/javascript" src="../js/companyConfig/sysCompanyConfig.js?v=20180426"/></script>
<style type="text/css">
	th,td {
	text-align:center !important;
	word-break: keep-all;
	white-space:nowrap;
	}
</style>
</head>
<body>
	<!-- 新增或编辑标识（1：新增，2：修改） -->
	<input type="hidden" id="addOrEidt"/>
	<input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	<input type="hidden" id="pageNo" value=""/><!-- yeshu -->
	

	<div class="content" style="overflow-x:auto" id="pageAll">
            
            <div class="form-wrap">
			   <div class="title"><strong><i class="icon search1"></i>操作</strong><i class="icon arrow-down1"  onclick="getButton(this)"></i></div>
               <input type="hidden" id="currentCompanyId" value="${requestScope.id }"/>
               <form id="queryform">
               
                <div class="form-main">
                
                <div class="col">                    
                    <div class="form">
                    	<label class="w" style="width:100px;">公司名称：</label>
                    	<select id="SelectPositionSeq" name="companyId" class="select_v1"></select>
                    </div>
                </div>      
                <div class="col">	               
	                <div class="form">
	                    	<label class="w" style="width:100px;">配置编码</label>
	                    	<input  type="text"  name="code" />							                 
 					</div>                 	
                </div>                   
                </div>
             </form>
              <div class="col">  
                    <div class="button-wrap ml-4">
                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
                        <button id="add" class="blue-but"><span><i class="icon add"></i>添加</span></button>                    
                    </div> 
                </div> 
			</div>
            <div>
	            <table>
					 <thead>
						<tr>
							<th style="overflow-x:auto;width:100px;text-align:center;">所属公司</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">配置编码</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">显示中文值</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">显示英文值</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">排序</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">扩展字段1</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">扩展字段2</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">扩展字段3</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">扩展字段4</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">扩展字段5</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">是否删除</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">描述</th>	
			                <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>		                
						</tr>
					  </thead>
		              <tbody id="reportList">
		              </tbody>
	            </table>
            </div>
            
            <!-- 新增职位 -->
	<div class="commonBox popbox" id="addDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>新增公司配置信息</strong>
						</div>

						<div class="form-main">

							<form id="addForm">
								<div class="col">
									<div class="form">
										<label class="w">所属公司</label> 
										<select id="addPositionSeq" name="companyId" class="select_v1">
						                </select>
									</div>
									<div class="form">
										<label class="w">排序</label><input type="text"
											class="form-text" name="rank" value="" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')">
									</div>
								</div>

								<div class="col">
									<div class="form">
										<label class="w">配置编码</label><input type="text"
											class="form-text" name="code" value="">
									</div>
									<div class="form">
										<label class="w">后台编码</label><input type="text"
											class="form-text" name="DisplayCode" value="">
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w">显示中文值</label><input type="text"
											class="form-text" name="description" value="">
									</div>
									<div class="form">
										<label class="w">显示英文值</label><input type="text"
											class="form-text" name="displayName" value="">
									</div>
								</div>						
							
								<div class="col">
									<div class="form">
										<label class="w">扩展字段1</label><input type="text"
											class="form-text" name="userDef1" value="">
									</div>
									<div class="form">
										<label class="w">扩展字段2</label><input type="text"
											class="form-text" name="userDef2" value="">
									</div>
								</div>
								
								<div class="col">
									<div class="form">
										<label class="w">扩展字段3</label><input type="text"
											class="form-text" name="userDef3" value="">
									</div>
									<div class="form">
										<label class="w">扩展字段4</label><input type="text"
											class="form-text" name="userDef4" value="">
									</div>
								</div>
								
								<div class="col">									
									<div class="form">
										<label class="w">扩展字段5</label><input type="text"
											class="form-text" name="userDef5" value="">
									</div>
									<div class="form">
										<label class="w">备注</label><input type="text"
											class="form-text" name="remark" value="">
									</div>
								</div>																						
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button class="red-but" onclick="savePosition();">
										<span>保存</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv('addDiv');">
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
            
            <!-- end  修改开始-->
   		<!-- 更新 -->
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>更新公司配置信息</strong>
						</div>
						<div class="form-main">

							<form id="updateForm">
								<input type="hidden" id="eidtCompanyId" name="companyId"/>
								<input type="hidden" id="editDepartId" name="departId"/>
								<input type="hidden" id="id" name="id" value=""/>
								<div class="col">
									<div class="form">
										<label class="w">所属公司</label> 
										<select id="updatePositionSeq" name="positionSeqId" class="select_v1" disabled="true">
						                </select>
									</div>
									<div class="form">
										<label class="w">排序</label><input type="text"
										id="rank"	class="form-text" name="rank" value="">
									</div>
								</div>

								<div class="col">
									<div class="form">
										<label class="w">配置编码</label><input type="text"
											class="form-text" name="code" value="">
									</div>
									<div class="form">
										<label class="w">后台编码</label><input type="text"
											class="form-text" name="displayCode" value="">
									</div>
								</div>
								<div class="col">
									<div class="form">
										<label class="w">显示中文值</label><input type="text"
											class="form-text" name="description" value="" >
									</div>
									<div class="form">
										<label class="w">显示英文值</label><input type="text"
											class="form-text" name="displayName" value="">
									</div>
								</div>						
							
								<div class="col">
									<div class="form">
										<label class="w">扩展字段1</label><input type="text"
											class="form-text" name="userDef1" value="">
									</div>
									<div class="form">
										<label class="w">扩展字段2</label><input type="text"
											class="form-text" name="userDef2" value="">
									</div>
								</div>
								
								<div class="col">
									<div class="form">
										<label class="w">扩展字段3</label><input type="text"
											class="form-text" name="userDef3" value="">
									</div>
									<div class="form">
										<label class="w">扩展字段4</label><input type="text"
											class="form-text" name="userDef4" value="">
									</div>
								</div>
								
								<div class="col">									
									<div class="form">
										<label class="w">扩展字段5</label><input type="text"
											class="form-text" name="userDef5" value="">
									</div>
									<div class="form">
										<label class="w">描述</label><input type="text"
											class="form-text" name="remark" value="">
									</div>
								</div>	
								<div class="col">																		
									<div class="form">
										<label class="w">是否删除</label><input type="text"
											class="form-text" name="delFlag" readonly="true">
									</div>
								</div>															
								
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button class="red-but" onclick="updatePostion();">
										<span>保存</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv('updateDiv');">
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
		<div class="popbox-center conf" style="margin-top: 0px;">
			<div class="popbox-main" style="height: 200px;">
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
     
    <!-- shanchu -->
    
    <div class="commonBox popbox" id="deleteDiv" style="display:none">
	  <input type="hidden" id="deleteId" name="deleteId"/>
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>删除当前配置？</strong>
						</div>
						<div class="form-main">

							<div class="col">
								<div class="form">
									确定删除当前配置吗？
								</div>
							</div>
							<div class="col">
								<div class="button-wrap ml-4">
									<button class="red-but" onclick="deletePostion();">
										<span>确定</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv('deleteDiv');">
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
    </div>
    <div class="paging" id="commonPage"></div>
</body>
</html>