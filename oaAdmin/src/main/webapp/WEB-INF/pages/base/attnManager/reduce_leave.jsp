<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="data:;base64,=">
<title>扣除假期</title>
<%@include file="../../common/common2.jsp"%>
<script type="text/javascript" src="../js/base/attnManager/reduce_leave.js?v=20200313"/></script>
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
			    <div class="title"><strong><i class="icon search1"></i>查询条件</strong><i class="icon arrow-down1" onclick="getButton(this)"></i></div>
	                <p>&nbsp;</p>
	                <div class="col">  
	                    <div class="button-wrap ml-4">    
	                        <div style="visibility: none;">
								<form enctype="multipart/form-data" id="batchUpload"  action="empLeave/reduceAffairAndYearLeave.htm" method="post" class="form-horizontal">    
								    <button class="btn btn-success btn-sm" id="uploadEventBtn"  type="button" >选择文件</button>  
								    <input type="file" name="file"  style="width:0px;height:0px;" id="uploadEventFile">  
									    <div class="form">
									    	<input id="uploadEventPath" class="form-text" disabled="disabled"  type="text" placeholder="--- 未选择excel表 ---" style="font-size:15px;text-align:center;font-color:gray;font-style:oblique" >                                           
									    </div>
								    <button type="button" class="btn btn-success btn-sm"  onclick="signExcel.uploadBtn()" >上传</button>
								</form> 
	                    	</div>         
	                       
	                    </div> 
	                </div> 
                </div>
			</div>
</body>
</html>