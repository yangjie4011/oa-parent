<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>职位管理</title>
<%@include file="../../common/common2.jsp"%>
<!-- ZTree树形插件 -->  
<link rel="stylesheet" href="<%=basePath%>css/util/ztree/zTreeStyle.css" type="text/css"> 
<script type="text/javascript" src="<%=basePath%>js/util/ztree/jquery.ztree.all-3.5.min.js?v=<%=random %>"></script>
<script type="text/javascript" src="<c:url value="/js/base/position/position_index.js?v=20191111"/>"></script>


</head>
<body>
	<!-- 新增或编辑标识（1：新增，2：修改） -->
	<input type="hidden" id="addOrEidt"/>
	<input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	<input type="hidden" id="departId"/>
	<input type="hidden" id="clickedCompanyId"/>
	<input type="hidden" id="pageNo" value=""/>

	<div class="form-wrap" style="width: 100%;height:600px;">
		<div  style="width: 18%; height: 100%; float: left;border-right:1px solid #d9d9d9"id="mainDiv1">
			<ul id="departTree" class="ztree"></ul>
		</div>
		<div id="dataDiv"  style="width: 80%; float: left;padding-left:10px;display:none;">
			<div class="title">
				<strong>
				<i class="icon search2"></i>部门职位管理 </strong>
					<i class="icon arrow-down1"  onclick="getButton(this)">
				</i>		
			</div>
            <div class="form-main">   
                <div class="col">  
                    <div class="button-wrap ml-0">
                        <button id="add" class="blue-but"><span><i class="icon add"></i>新建</span></button>          
                    </div> 
                </div> 
                </div>
			<table id="positionTable1">
				<colgroup>
					<col width="25%">
					<col width="25%">
					<col width="25%">
					<col width="25%">
				</colgroup>
				<thead>
					<tr>
						<th style="text-align: center;">职位名称</th>
						<th style="text-align: center;">职位类别</th>
						<th style="text-align: center;">操作</th>
					</tr>
				</thead>
				<tbody id="positionList">
				</tbody>
			</table>
			<div class="paging" id="commonPage"></div>
		</div>
	</div>

	<!-- 新增职位 -->
	<div class="commonBox popbox" id="addDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>新增职位</strong>
						</div>

						<div class="form-main">

							<form id="addForm">
								<input type="hidden" id="addCompanyId" name="companyId" /> 
								<input type="hidden" id="addDepartId" name="departId" />
								<div class="col">
									<div class="form">
										<label class="w">职位名称</label><input type="text"
											class="form-text" name="positionName" value="">
									</div>
								</div>

								<div class="col">
									<div class="form">
										<label class="w">职位类别</label>
										<select id="addPositionLevel" name="positionLevelId" class="select_v1">
						                </select>
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
			<!-- 更新 -->
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon add-user"></i>更新职位</strong>
						</div>
						<div class="form-main">

							<form id="updateForm">
								<input type="hidden" id="eidtCompanyId" name="companyId"/>
								<input type="hidden" id="editDepartId" name="departId"/>
								<input type="hidden" id="id" name="id"/>
								<div class="col">
									<div class="form">
										<label class="w">职位名称</label><input type="text"
											 id="positionName" class="form-text" name="positionName" value="">
									</div>
								</div>

								<div class="col">
									<div class="form">
										<label class="w">职位类别</label>
										<select id="editPositionLevel" name="positionLevelId" class="select_v1">
						                </select>
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

	<div class="commonBox popbox" id="deleteDiv" style="display:none">
		<input type="hidden" id="deleteId" name="deleteId"/>
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>删除职位？</strong>
						</div>
						<div class="form-main">

							<div class="col">
								<div class="form">
									确定删除此职位吗？
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
</body>
</html>