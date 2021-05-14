<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>考勤设置--修改考勤时间</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="<%=basePath%>js/base/attnManager/attnClassShow.js?v=20180605"></script>
</head>
<body>
	<input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
	<input type="hidden" id="pageNo" name="page" value=""/>
	<div class="datatitle">
		<ul class="fl jui-tabswitch">
			<li id="listTab" rel="#old" class="active"><strong>日常考勤设置</strong></li>
			<li id="detailTab" rel="#new"><strong>年度假期设置</strong></li>
		</ul>
	</div>
	<br/>
	<br/>
	<div class="content">
		
		<table>
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
				    <th style="display:none;">id</th>
					<th>班次名称</th>
					<th>考勤时间</th>
					<th>工时</th>
					<th>允许迟到分钟数</th>
					<th>考勤部门</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody id="reportList">
				<tr style="mso-yfti-irow: 2; mso-yfti-lastrow: yes; height: 12.75pt">
				    <td class="classId">2</td>
					<td>2</td>
					<td>2</td>
					<td>2</td>
					<td>2</td>
					<td>2</td>
					<td>2</td>
				</tr>
			</tbody>
		</table>

		    <div class="paging" id="commonPage">
            </div>
        </div>
        
     <!-- 设置弹窗 -->
	<div class="commonBox popbox" id="updateDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>编辑</strong>
						</div>
                        <input id ="settingId" type = "hidden"/> 
                        <input id ="version" type = "hidden"/> 
						<div class="form-main">
							<form id="addForm">
								<div class="col">                    
				                    <div class="form"><label class="w" style="width:100px;">上班打卡：</label>
	                                <input id = "startTime" type = "text"/> 
				                    </div>
				                </div>		
								<div class="col">                    
				                    <div class="form"><label class="w" style="width:100px;">下班打卡：</label>
				                    <input id = "endTime" type = "text"/> 
				                    </div>
				                </div>
				                <div class="col">                    
				                    <div class="form"><label class="w" style="width:100px;">允许迟到分钟数：</label>
				                    <input id = "allowLateTime" type = "text"/>分
				                    </div>
				                </div>
							</form>

							<div class="col">
								<div class="button-wrap ml-4">
									<button class="red-but" onclick="updateSetting();" style="width:120px;">
										<span>确定</span>
									</button>
									<button class="small grey-but"
										onclick="closeDiv();" style="width:120px;">
										<span>返回</span>
									</button>
								</div>
							</div>
						</div>
					</div>
					<!-- 设置弹窗 -->
				</div>
			</div>
		</div>
	</div>
</body>
</html>