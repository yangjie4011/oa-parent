<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>员工考勤明细</title>
    <meta charset="UTF-8">
</head>

<body class="mt-44 b-f2f2f2">
    <div class="paiban-chaxun">
        <header>
            <h1 class="clearfix"><a href="javascript:window.history.go(-1)" class="goback fl"><i class="back sr"></i></a>员工考勤明细<a class="toproblem fr"></a></h1>
        </header>
        <div class="tab">
            <ul>
                <li class="on">
                    <p>按日统计</p>
                </li>
                <li>
                    <p>按月统计</p>
                </li>
            </ul>
        </div>
        <div class="tab-list">
            <div class="kqmx-list on">
                <ul>
                    <li>
                        <div class="head">
                            <div class="l"><input id="byday" type="text" readonly="readonly" value="${today }" class="date"/><i></i></div>
                            <div class="r selCompanyPartment"><input type="text" readonly="readonly" value="全公司" id="companyDay" class="departName"/>
                            <input type="hidden" id="departIdByDay" class="departId"/>
                            <input type="hidden" id="timeType" class="timeType" value="day"/><i></i>
                            </div>
                        </div>
                    </li>
                    <li>
                        <div class="list-show">
                            <p>迟到</p>
                            <p class="blue"><span id="dayLateNumber"></span>人</p>
                        </div>
                        <div class="list-show">
                            <p>早退</p>
                            <p class="blue"><span id="dayEarlyNumber"></span>人</p>
                        </div>
                    </li>
                    <li>
                        <div class="list-show">
                            <p>缺勤</p>
                            <p><span id="dayLackNumber"></span>人</p>
                        </div>
                        <div class="list-show">
                            <p>旷工</p>
                            <p class="blue"><span id="dayAbsentNumber"></span>人</p>
                        </div>
                    </li>
                </ul>
            </div>
            <div class="kqmx-list">
                <ul>
                    <li>
                        <div class="head">
                            <div class="l"><input id="bymonth" type="text" readonly="readonly" value="${thisMonth }" class="date"/><i></i></div>
                            <div class="r"><input type="text" value="全公司" id="companyMonth" name="companyMonth" class="departName"/>
                            <input type="hidden" id="departIdByMonth" class="departId"/>
                            <input type="hidden" id="timeType" class="timeType" value="month"/><i></i></div>
                        </div>
                    </li>
                    <li>
                        <div class="list-show">
                            <p>迟到</p>
                            <p class="blue"><span id="monthLateNumber"></span>人</p>
                        </div>
                        <div class="list-show">
                            <p>早退</p>
                            <p class="blue"><span id="monthEarlyNumber"></span>人</p>
                        </div>
                    </li>
                    <li>
                        <div class="list-show">
                            <p>缺勤</p>
                            <p class="blue"><span id="monthLackNumber"></span>人</p>
                        </div>
                        <div class="list-show">
                            <p>旷工</p>
                            <p class="blue"><span id="monthAbsentNumber"></span>人</p>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <a href="<%=basePath%>empAttn/toAttnExport.htm"><div class="daochu-baob">报表</div></a>
    <form id="form" action="<%=basePath%>empAttn/employeeAttnDetail.htm" method="post"><!-- 用来进行post请求的form -->
	    <input type="hidden" name="date" id="formDate"/>
	    <input type="hidden" name="departId" id="formDepartId"/>
	    <input type="hidden" name="departName" id="formDepartName"/>
	    <input type="hidden" name="type" id="formType"/>
	    <input type="hidden" name="timeType" id="formTimeType"/>
	    <input type="hidden" name="total" id="formTotal"/>
    </form>
    <script>
        $(function () {
            listTypeSelect.init('daochumx');
        	
        	var date;
        	var departId,departName;
            
            var getNumberByDay = function(){
            	
            	date = $("#byday").val();
            	departId = $("#departIdByDay").val();
            	
            	$.ajax({
            	    type:"POST",
            		url : contextPath + "/empAttn/getEmpAttnNumber.htm",
            		data : {date:date,departId:departId,type:"day"},
            		dataType : 'json',
            		success : function(response) {
            			console.info("结果：\r"+JSON.stringify(response));
            			$("#dayLateNumber").text(response.cltNumber);
            			$("#dayEarlyNumber").text(response.letNumber);
            			$("#dayLackNumber").text(response.latNumber);
            			$("#dayAbsentNumber").text(response.atNumber);
            		}
            	 });
            }
            
            var getNumberByMonth = function(){
            	
            	date = $("#bymonth").val();
            	departId = $("#departIdByMonth").val();
            	
            	$.ajax({
            	    type:"POST",
            		url : contextPath + "/empAttn/getEmpAttnNumber.htm",
            		data : {date:date,departId:departId,type:"month"},
            		dataType : 'json',
            		success : function(response) {
            			console.info("结果：\r"+JSON.stringify(response));
            			$("#monthLateNumber").text(response.cltNumber);
            			$("#monthEarlyNumber").text(response.letNumber);
            			$("#monthLackNumber").text(response.latNumber);
            			$("#monthAbsentNumber").text(response.atNumber);
            		}
            	 });
            }
            
            getNumberByDay();
            getNumberByMonth();
            
            $("#byday").on('input',function(e){  
            	getNumberByDay();
            });  
            $("#companyDay").bind('change',function(){
            	getNumberByDay();
            });
            
            $("#bymonth").on('input',function(e){  
            	getNumberByMonth();
            });
            $("#companyMonth").bind('change',function(){
            	getNumberByMonth();
            }); 
            
            
            //点击模块明细，跳转
            $(".list-show").click(function(){
            	var type,total,timeType;
            	var id = $(this).find("span").attr("id");
            	var parent  = $(this).closest(".kqmx-list");
            	
            	date = parent.find(".date").val();
            	departId = parent.find(".departId").val();
            	departName = parent.find(".departName").val();
            	timeType = parent.find(".timeType").val();
            	
            	//console.info("this id is :"+id);
            	total = $("#"+id).text();
            	if(total=="" || total == "0"){
            		return false;
            	}else{
            		if(id=="dayLateNumber" || id=="monthLateNumber" ){
            			type="late";
            		}else if(id=="dayEarlyNumber" || id=="monthEarlyNumber" ){
            			type="early";
            		}else if(id=="dayLackNumber" || id=="monthLackNumber" ){
            			type="lack";
            		}else if(id=="dayAbsentNumber" || id=="monthAbsentNumber" ){
            			type="absent";
            		}
            	}
            	
            	$("#formDate").val(date);
            	$("#formDepartId").val(departId);
            	$("#formDepartName").val(departName);
            	$("#formType").val(type);
            	$("#formTotal").val(total);
            	$("#formTimeType").val(timeType);
            	$("#form").submit();
            })
        })
    </script>
</body>
</html>