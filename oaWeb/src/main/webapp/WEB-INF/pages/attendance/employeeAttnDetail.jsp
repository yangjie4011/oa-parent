<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>员工考勤异常次数查看</title>
    <meta charset="UTF-8">
</head>

<body class="b-f2f2f2 mt-160">
    <div class="oa-timecard day">
        <header>
            <h1 class="clearfix"><a href="javascript:window.history.go(-1)" class="goback fl"><i class="back sr"></i></a><p>员工考勤明细</p></h1>
            <div class="head-list">
                <p class="q">${date }</p>
                <p class="w">${departName }</p>
                <p class="e">${showType }共<span>${total }</span>人</p>
                <input type="hidden" id="date" value="${date }">
                <input type="hidden" id="departId" value="${departId }">
                <input type="hidden" id="type" value="${type }">
                <input type="hidden" id="timeType" value="${timeType }">
            </div>
        </header>
        <div class="banci-list">
            <ul>
                <!-- <li>
                    <a href="month_chaxun.html">
                        <div class="main">
                            <h4 class="l">陈姗姗</h4>
                            <div class="r">
                                <div class="num">4次</div>
                                <i class="icon"></i>
                            </div>
                        </div>
                    </a>
                </li> -->
            </ul>
        </div>
    </div>
    <form id="form" action="<%=basePath%>empAttn/empAttnTimesDetail.htm" method="post"><!-- 用来进行post请求的form -->
	    <input type="hidden" name="date" id="formDate" value="${date }"/>
	    <input type="hidden" name="employeeId" id="formEmployeeId"/>
	    <input type="hidden" name="employeeName" id="formEmployeeName"/>
	    <input type="hidden" name="departName" id="formDepartName"/>
	    <input type="hidden" name="count" id="formCount"/>
        <input type="hidden" name="type" value="${type }">
        <input type="hidden" name="timeType" value="${timeType }">
    </form>
<script type="text/javascript">
$(function(){
	
	getEmpAttnTimes();
})

var getEmpAttnTimes = function(){
	
	var date,departId,type,timeType;
	
	date = $("#date").val();
	departId = $("#departId").val();
	type = $("#type").val();
	timeType = $("#timeType").val();
	$.ajax({
	    type:"POST",
		url : contextPath + "/empAttn/getEmpAttnTimes.htm",
		data : {date:date,departId:departId,type:type,timeType:timeType},
		dataType : 'json',
		success : function(response) {
			
			$(".banci-list > ul").empty();
			if(null != response && '' != response){
				var li = "";
				$.each(response,function(index,item){
					
					li= li+'<li><input type="hidden" class="employeeId" value="'+
					item.employeeId +'"/><input type="hidden" class="departName" value="'+
					item.departName +'"/><div class="main"><h4 class="l employeeName">'+
					item.employeeName+'</h4><div class="r"><div class="num">'+
					item.count+'次</div><i class="icon"></i></div></div></a></li>';
				})
				$(".banci-list > ul").append(li);
				//绑定点击事件
				$("li").click(function(){
					var li = $(this);
					liClick(li);
				});
			}
		}
	 });
}

var liClick = function(li){
	var employeeId = li.find(".employeeId").val();
	var employeeName = li.find(".employeeName").text();
	var departName = li.find(".departName").val();
	var count = li.find(".num").text();
	
	$("#form #formEmployeeId").val(employeeId);
	$("#form #formEmployeeName").val(employeeName);
	$("#form #formDepartName").val(departName);
	$("#form #formCount").val(count);
	$("#form").submit();
}
</script>
</body>

</html>