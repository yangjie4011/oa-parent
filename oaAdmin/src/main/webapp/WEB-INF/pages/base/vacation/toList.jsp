<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>节假日查询</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/vacation/toList.js?v=20201202"/></script>
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
							<th style="overflow-x:auto;width:100px;text-align:center;">日期</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">年份</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">类型</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">节日</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">备注</th>
			                <th style="overflow-x:auto;width:100px;text-align:center;">操作</th>
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