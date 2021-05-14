<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>月度加班汇总</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/report/overtime.js?v=20190424"/></script>
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
                </div>      
                
                <div class="col">
               	   <div class="form">
                        <label class="w">工时制：</label>
                        <select id="workType" name="workType" class="select_v1">
                        </select>
                    </div>
                    
                    <div class="form">
                        <label class="w">员工类型：</label>
                        <select id="empTypeId" name="empTypeId" class="select_v1">
                        </select>
                    </div>
                </div>
                
                <div class="col">
                	<div class="form">
	                        <label class="w">是否排班：</label>
	                        <select id="whetherScheduling" name="whetherScheduling" class="select_v1">
	                        </select>
	                </div>
                    <div class="form">
	                    	<label class="w">年月：</label>
	                    	<input id="yearAndMonth" type="text" class="Wdate" name="yearAndMonth" onClick="WdatePicker()" readonly="readonly" onfocus="WdatePicker({dateFmt:'yyyy-MM'});" />
	                 </div>
                	
                </div> 
                <div class="col">
                    <!-- <div class="form">
                    	<label class="w">状态：</label>
                    	<select id="approvalStatus" name="approvalStatus" class="select_v1">
                        </select>
	                 </div> -->
	                 
	                 <div class="form">
                        <label class="w">部门：</label>
                        <select id="firstDepart" name="firstDepart" class="select_v1">
                        </select>
                    </div>
                    
                    <div class="form" id="secondDepartDiv" style="display:none">
                        <select id="secondDepart" name="secondDepart" class="select_v1">
                        </select>
                    </div>
                </div>
                </div>
             </form>
              <div class="col">  
                    <div class="button-wrap ml-4">
                        <button id="query" class="red-but"><span><i class="icon search"></i>查询</span></button> 
                        <button id="export" class="blue-but"><span><i class="icon add"></i>导出</span></button>          
                    </div> 
                </div> 
			</div>
            <div>
	            <table>
					 <thead>
						<tr>
							<th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">申请人</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">部门</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">职位</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">年月</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">1.5倍小时数</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">2倍小时数</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">3倍小时数</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">加班总计小时数</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">剩余调休小时数</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">晚间餐费次数</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">晚间交通费次数（21:00-22:59）</th>
			                <th style="overflow-x:auto;width:150px;text-align:center;">晚间交通费次数（23点以后）</th>
						</tr>
					  </thead>
		              <tbody id="reportList">
		              </tbody>
	            </table>
            </div>
        </div>
        <div class="paging" id="commonPage"></div>
</body>
</html>