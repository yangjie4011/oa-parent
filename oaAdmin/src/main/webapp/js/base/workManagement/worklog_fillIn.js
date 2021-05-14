$(function(){
	var currentCompanyId = $("#currentCompanyId").val();
	queryWorkLogDetalInfoByMonth();
});

//查询工作日志一个月的提交信息
function queryWorkLogDetalInfoByMonth(){
	pageLoading(true);//加载动画
	//查询排班明细生成表格
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{month:$("#month").val()},
		url:contextPath + "/workLog/queryWorkLogDetalInfoByMonth.htm",
		success:function(data){
			if(data.success){
				$("#detailListTitle").find("thead").html("");
				$("#detailListTitle").find("tbody").html("");
				$("#detailList").find("thead").html("");
				$("#detailList").find("tbody").html("");
				var headText = "<tr>";
				var head = "<tr>";
				for(var i=0;i<data.weekDays.length;i++){
					if(i<=1){
						headText +="<th style='overflow-x:auto;width:10px;text-align:center; height: 19px;'>"+data.weekDays[i]+"</th>";
					}else{
						head +="<th style='overflow-x:auto;width:10px;text-align:center;'>"+data.weekDays[i]+"</th>";
					}
				}
				headText += "</tr>";
				head += "</tr>";
				$("#detailListTitle").find("thead").append(headText);
				$("#detailList").find("thead").append(head);
				//天
				var daysTitle = "<tr>";
				var days = "<tr>";
				for(var i=0;i<data.days.length;i++){
					if(i<=1){
						daysTitle +="<td style='text-align:center;  height: 19px;'>"+data.days[i]+"</td>";
					}else{
						days +="<td style='text-align:center;'>"+data.days[i]+"</td>";
					}
				}
				daysTitle += "</tr>";
				days += "</tr>";
				$("#detailListTitle").find("tbody").append(daysTitle);
				$("#detailList").find("tbody").append(days);
				//工作日志详情
				var logsTitle = "<tr>";
				var logs = "<tr>";
				for(i=0;i<data.workLogDetail.length;i++){
					if(i<=1){
						logsTitle +="<td style='text-align:center;  height: 19px;'>"+data.workLogDetail[i]+"</td>";
					}else{
						logs +="<td onclick='showDetail(this)' date="+data.dates[i]+" style='text-align:center;'>"+data.workLogDetail[i]+"</td>";
					}
				}
				logsTitle += "</tr>";
				logs += "</tr>";
				$("#detailListTitle").find("tbody").append(logsTitle);
				$("#detailList").find("tbody").append(logs);
			}else{

			}
			pageLoading(false);//关闭动画
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}

function showDetail(obj){
	var type = $(obj).text().trim();
	var date = $(obj).attr("date");
	$("#workDate").val(date);
	if("未填"==type){
		$(".seachWorkLog").hide();
		$(".saveWorkLogDiv").show();
		$("#showTitle").hide();
		
	}else if("审阅中"==type){
		getProcessInfoDiv(date);
	}
}

//获取流程详情弹框
function getProcessInfoDiv(date){	
	pageLoading(true);
	$.ajax({
		async:true,
		type:'post',
		timeout : 5000,
		dataType:'json',
		data:{workDate:date},
		url:contextPath + "/workLog/getProcessInfoDiv.htm",
		success:function(data){
			if(data.success){
				$(".processInfo").empty();
				$(".seachWorkLog").hide();
				$("#showTitle").hide();
				var html = '<div class="form-main" style="height:auto;"><div class="col" style="height:auto;"><div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">员工姓名</label><input type="text"  readonly="readonly" value='+data.cnName+'></div>';
				html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">所属部门</label><input type="text"  readonly="readonly" value='+data.departName+'></div>';
				html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">日期</label><input type="text"  readonly="readonly" value='+data.workDate+'></div></div>'
				html +='<div class="col" style="height:auto;"><div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">审阅状态</label><input type="text"  readonly="readonly" value='+data.approvalStatus+'>&nbsp;&nbsp;<a leaderComment='+data.leaderComment+' hrComment='+data.hrComment+' onclick="getComment(this)" style="color:blue;" href="#">批示意见</a></div>';
                html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">主管审阅</label><input type="text"  readonly="readonly" value='+data.leaderStatus+'></div><div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">人事审阅</label>';
                html +='<input type="text" readonly="readonly" value='+data.hrStatus+'></div></div></div>';	
				for(var i=0;i<data.maxSize;i++){
					if(i==0){
						html +='<div class="form-main" style="height:auto;"><div class="col" style="height:auto;"><div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">工作内容与输出</label>';
						html += '<textarea style="width:300px;height:80px;padding:0;" rows="" cols="" readonly="readonly">'+data.workContent[i]+'</textarea></div>';
						html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">下个工作日工作计划</label><textarea style="width:300px;height:80px;padding:0;" rows="" cols="" readonly="readonly">'+data.workPlan[i]+'</textarea></div>';
						html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">遇到的困难和问题</label><textarea style="width:300px;height:80px;padding:0;" rows="" cols="" readonly="readonly">'+data.workProblem[i]+'</textarea></div></div></div>';
					}else{
						html +='<div class="form-main" style="height:auto;"><div class="col" style="height:auto;"><div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">&nbsp;</label>';
						if(data.workContent.length-i>0){
							html += '<textarea style="width:300px;height:80px;padding:0;" rows="" cols="" readonly="readonly">'+data.workContent[i]+'</textarea></div>';
						}else{
							html += '<textarea style="width:300px;height:80px;padding:0;border:0px;" rows="" cols="" readonly="readonly"></textarea></div>';
						}
						if(data.workPlan.length-i>0){
							html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">&nbsp;</label><textarea style="width:300px;height:80px;padding:0;" rows="" cols="" readonly="readonly">'+data.workPlan[i]+'</textarea></div>';
						}else{
							html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">&nbsp;</label><textarea style="width:300px;height:80px;padding:0;border:0px;" rows="" cols="" readonly="readonly"></textarea></div>';
						}
						if(data.workProblem.length-i>0){
							html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">&nbsp;</label><textarea style="width:300px;height:80px;padding:0;" rows="" cols="" readonly="readonly">'+data.workProblem[i]+'</textarea></div></div></div>';
						}else{
							html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">&nbsp;</label><textarea style="width:300px;height:80px;padding:0;border:0px;" rows="" cols="" readonly="readonly"></textarea></div></div></div>';
						}
					}
				}
                html += '<div class="col"><div class="buttonDiv button-wrap ml-4"><button onclick="closeProcesInfo();" class="btn-blue btn-large"><span><i class="icon"></i>关闭</span></button></div></div>';
                $(".processInfo").append(html);
			}
			pageLoading(false);
		},
		complete:function(XMLHttpRequest,status){
			pageLoading(false);
		},
		error:function(data) {
			pageLoading(false);
			console.log("error" + data);
		}
	});
	
}

function getComment(obj){
	var leaderComment = $(obj).attr("leaderComment");
	var hrComment = $(obj).attr("hrComment");
	$("#leaderComment").val(leaderComment);
	$("#hrComment").val(hrComment);
	$("#commentDiv").show();
}

function closeComment(){
	$("#leaderComment").val("");
	$("#hrComment").val("");
	$("#commentDiv").hide();
}

function closeProcesInfo(){
	$(".processInfo").empty();
	$(".seachWorkLog").show();
	$("#showTitle").show();
}

function saveWorkLog(){
	
	var workDate = $("#workDate").val();
	var workContent = "";
	var workContentFlag  = false;
	$(".workContent").find("textarea").each(function(){
		if($(this).val().trim()==""){
			workContentFlag = true;
			return;
		}
		workContent = workContent+$(this).val().trim()+"===";
	});
	if(workContentFlag){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("工作内容与输出不能为空！");
		});
		return;
	}
	
	var workPlan = "";
	var workPlanFlag  = false;
	$(".workPlan").find("textarea").each(function(){
		if($(this).val().trim()==""){
			workPlanFlag = true;
			return;
		}
		workPlan = workPlan+$(this).val().trim()+"===";
	});
	if(workPlanFlag){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("下一个工作日工作计划不能为空！");
		});
		return;
	}
	
	var workProblem = "";
	$(".workProblem").find("textarea").each(function(){
		workProblem = workProblem+$(this).val().trim()+"===";
	});
	
	var param = {
		workDate:workDate,
		workContent:workContent.trim().substring(0,workContent.trim().length-3),
		nextDayWorkPlan:workPlan.trim().substring(0,workPlan.trim().length-3),
		workProblem:workProblem.trim().substring(0,workProblem.trim().length-3)
	};
	
	secondConfirmation({
	   "msg":"是否确认提交？",
		sureFn:function(){
			pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:param,
				url:contextPath + "/workLog/save.htm",
				success:function(data){
					JEND.load('util.dialog', function() {
						JEND.util.dialog.alert(data.message);
					});
				},
				complete:function(XMLHttpRequest,status){
					pageLoading(false);
				},
				error:function(data) {
					console.log("error" + data);
				}
			});
		}
	});
	
}


function closeWorkLog(){
	$(".seachWorkLog").show();
	$(".saveWorkLogDiv").hide();
	$("#showTitle").show();
	queryWorkLogDetalInfoByMonth();
}

//新增工作内容
function addWorkContent(){
	var html = '<div class="col" style="height:auto;"><div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">工作内容与输出</label><textarea placeholder="填写当日完成工作进度、工作任务或工作产出（限100字）" style="width:300px;height:80px;padding:0;" rows="" cols=""></textarea>&nbsp;&nbsp;<a class="icon-shanchu" href="#" onclick="delWorkContent(this);"></a></div></div>';
	$(".workContent").append(html);
}

//删除工作内容
function delWorkContent(obj){
	$(obj).parent().parent().remove();
	$(obj).remove();
}

//新增工作计划
function addWorkPlan(){
	var html = '<div class="col" style="height:auto;"><div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">下个工作日工作计划</label><textarea placeholder="（限100字）" style="width:300px;height:80px;padding:0;" rows="" cols=""></textarea>&nbsp;&nbsp;<a class="icon-shanchu" href="#" onclick="delWorkPlan(this);"></a></div></div>';
	$(".workPlan").append(html);
}
//删除工作计划
function delWorkPlan(obj){
	$(obj).parent().parent().remove();
	$(obj).remove();
}

//新增工作问题
function addWorkProblem(){
	var html = '<div class="col" style="height:auto;"><div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">遇到的困难和问题</label><textarea placeholder="（限100字）" style="width:300px;height:80px;padding:0;" rows="" cols=""></textarea>&nbsp;&nbsp;<a class="icon-shanchu" href="#" onclick="delWorkProblem(this);"></a></div></div>';
	$(".workProblem").append(html);
}

//删除工作问题
function delWorkProblem(obj){
	$(obj).parent().parent().remove();
	$(obj).remove();
}