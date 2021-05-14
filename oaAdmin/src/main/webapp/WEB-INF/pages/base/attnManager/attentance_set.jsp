<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>考勤设置</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/attnManager/attentance_set.js?v=20200224"/></script>
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
	<div class="datatitle" style="padding-left:0px;">
		<ul class="fl jui-tabswitch" id="parent">
				<li rel="#old"  class="active"><strong>弹性工时设置</strong></li>								
		</ul>
	</div>
            
    <div class="form-wrap">
	   <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
             <input type="hidden" id="currentCompanyId" value="${requestScope.companyId }"/>
              <input type="hidden" id="pageNo" value=""/>
             <form id="queryform">
              <div class="form-main">
             
             <div class="col">
             	  <div class="form">
                  	<label class="w">员工编号：</label>
                  	<input type="text" class="form-text" id="code" name="code" value="">
                  </div>
                  
                  <div class="form">
                  	<label class="w">员工姓名：</label>
                  	<input type="text" class="form-text" id="cnName" name="cnName" value="">
                  </div>
                  <div class="form">
                       <label class="w">部门：</label>
                       <select id="firstDepart" name="departId" class="select_v1 firstDepart">
                      		<option value= "">请选择</option>

						</select>
                  </div>
                
              </div>
             
             
              <div class="col">
	              <div class="form">
                       <label class="w">汇报对象：</label>
                      <select id="employeeLeader" name="reportToLeader" class="select_v1">
	                   </select>
                  </div>
                   <div class="form">
                       <label class="w">工时制：</label>
                       <select id="workType" name="workType" class="select_v1">
                           <option value="">请选择</option>
                       </select>
                   </div>
                   <div class="form">
                       <label class="w">月份：</label>
						<input id="month" type="text" class="Wdate" name="month" onClick="WdatePicker({dateFmt:'yyyy-MM'})" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM'});" value="${month}" />
                   </div>
                   <div class="form">
	                        <label class="">在职状态：</label>
	                        <select id="jobStatus" name="jobStatus" class="select_v1"><option value="">请选择</option>
								<option value="0">在职</option>
								<option value="1">离职</option>
								<option value="2">待离职</option>
							</select>
	                </div>
               </div>
                  
                  
              </div>
           </form> 
           <div class="col">  
                    <div class="button-wrap ml-4">
						<button class="blue-but" id="uploadEventBtn"  type="button" ><span>选择文件</span></button>  
                    	<form enctype="multipart/form-data" id="batchUpload" method="post" class="form-horizontal">    
                    		<input type="hidden" id="uploadDepart" name="departId">
                    		<input type="hidden" id="uploadGroup" name="groupId">
                    		<input type="hidden" id="uploadMonth" name="month">
						    <input type="file" name="file"  style="width:0px;height:0px;" id="uploadEventFile">  
						    <div class="form">
						    	<input id="uploadEventPath" class="form-text" disabled="disabled"  type="text" placeholder="-未选择excel表-" style="font-size:12px;text-align:center;font-color:gray;font-style:oblique" >                                           
						    </div>
						</form>
						<button type="button" class="blue-but"  onclick="signExcel.uploadBtn()" ><span><i class="icon-shangchuan"></i>上传模板</span></button>
                    </div> 
           </div>
          <div class="col">  
                  <div class="button-wrap ml-4">
                    <a class="btn-blue icon-xiazai btn-large" align ="left" href="<%=basePath %>/template/弹性工时设置.xlsx">下载模板</a>
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
	                <th style="overflow-x:auto;width:100px;text-align:center;">汇报对象</th>
	                <th style="overflow-x:auto;width:100px;text-align:center;">工时制</th>
	                <th style="overflow-x:auto;width:100px;text-align:center;">班次</th>
	                <th style="overflow-x:auto;width:100px;text-align:center;">班次时间</th>
				</tr>
			  </thead>
              <tbody id="reportList">
              </tbody>
           </table>
    </div>
    <div class="paging" id="commonPage"></div>
        
    <!-- 设置弹窗 -->
	<div class="commonBox popbox" id="importDiv" style="display:none">
		<div class="popbox-bg"></div>
		<div class="popbox-center" style="top: 10%; left: 30%">
			<div class="popbox-main" style="">
				<div class="cun-pop-content">
					<div class="form-wrap">
						<div class="title">
							<strong><i class="icon"></i>导入详情</strong>
						</div>
							<textarea id="importMsg" maxlength="800" warp="virtual" style="border:0px"></textarea>
						<div class="col">
							<div class="button-wrap ml-4" >
								<button class="small grey-but"
									onclick="closeImportDiv();" style="float:right">
									<span>返回</span>
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>   

      	
</body>
</html>
