<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="../../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>请勿随意使用</title>
<style type="text/css">
.btn-red {
    color: #ffffff;
    background-color: #ca1921;
    border-color: #ca1921;
}
.btn-common {
    overflow: visible !ie;
    display: inline-block;
    padding: 0 10px;
    text-decoration: none;
    border: solid 1px #ccc;
    cursor: pointer;
    height: 22px;
    line-height: 22px;
    font-size: 12px;
    vertical-align: middle;
    }
</style>
</head>
<body>
	<div style="margin-top:50px;">
	<span style="width:50px;height:30px;">
	    输入你的sql（限一条）
	</span> 
	&nbsp;&nbsp;&nbsp;&nbsp;
	<span> 
		<a id="btn-add" class="btn-red  btn-common">确定</a>
	</span>
	<br>
	<div style="margin-top:20px;">
	<textarea rows="20" cols="100" id="sqlStr"  placeholder="乱用此功能将负法律责任！"></textarea>
	
	</div>
	</div>
	<script type="text/javascript">
	$(function(){
		$("#btn-add").click(
				function(){
					$("#btn-add").attr("disabled",true); 
			 		var sqlStr = $("#sqlStr").val();
			 		if(!sqlStr){
			 			return false;
			 		}
					$.ajax({
						async:true,
						type:'post',
						dataType:'json',
						data:{sqlStr:sqlStr},
						url:contextPath + "/sysConfig/execute.htm",
						success:function(data){
							alert("提示信息:"+ data.message);
						},
						complete:function(){  
							$("#btn-add").attr("disabled",false);
				        }
					});
				}
		);
	})
	</script>
</body>
</html>