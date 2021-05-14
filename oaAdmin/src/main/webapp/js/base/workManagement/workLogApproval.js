$(function(){
	//初始化部门
	getFirstDepart();
	gotoPage(1);
	$(".jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		$(obj).click(function(){
			gotoPage(1);
		})
	});
});

function gotoPage(page){
	if(!page){
		page = 1;
	}
	var index = "leaderTab";
	$(".jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		if($(obj).hasClass("active")){
			index = $(obj).attr("id");
		}
	});
	pageLoading(true);//加载动画
	$("#pageNo").val(page);
	var data = $("#queryform").serialize()+ "&page=" + $("#pageNo").val() + "&rows=10" + "&index="+index;
	$.ajax({
		async:true,
		type:'post',
		data:data,
		dataType:'json',
		url:contextPath + "/workLog/getWorkLogPage.htm",
		success:function(data){
			
			$("#reportList").find("thead").html("");
			$("#reportList").find("tbody").html("");
			$("#reportListTitle").find("thead").html("");
			$("#reportListTitle").find("tbody").html("");
			//加载标题
			if(data.success){
				
				var headText = "<tr>";
				var head = "<tr>";
				for(var i=0;i<data.weekDays.length;i++){
					if(i<=1){
						headText +="<th style='overflow-x:auto;text-align:center;'>"+data.weekDays[i]+"</th>";
					}else{
						head +="<th style='overflow-x:auto;text-align:center;'>"+data.weekDays[i]+"</th>";
					}
				}
				headText += "</tr>";
				head += "</tr>";
				$("#reportListTitle").find("thead").append(headText);
				$("#reportList").find("thead").append(head);
				
				//天
				var daysTitle = "<tr>";
				var days = "<tr>";
				for(var i=0;i<data.days.length;i++){
					if(i<=1){
						daysTitle +="<td style='text-align:center;'>"+data.days[i]+"</td>";
					}else{
						days +="<td style='text-align:center;'>"+data.days[i]+"</td>";
					}
				}
				daysTitle += "</tr>";
				days += "</tr>";
				$("#reportListTitle").find("thead").append(daysTitle);
				$("#reportList").find("tbody").append(days);
				
				var m = data.days.length;
				if(data.page.rows!=null){
					for(i=0;i<data.page.rows.length;i++){
						var classDetailTitle = "<tr class='parentTr' empCnName="+data.page.rows[i].cnName+" empId="+data.page.rows[i].employeeId+">";
						classDetailTitle +="<td style='text-align:center;'>"+data.page.rows[i].empCode+"</td>";
						classDetailTitle +="<td style='text-align:center;'>"+data.page.rows[i].empCnName+"</td>";
						classDetailTitle+="</tr>"
						var count=0;
						var classDetail="<tr class='parentTr'>";
						for(var j=1;j<=m-2;j++){
							var date = data.dates[j-1];
							var approvalStatus = data.page.rows[i][date].split("_")[0];
							var color = data.page.rows[i][date].split("_")[1];
							if("审阅中"==approvalStatus||"待审阅"==approvalStatus){
								classDetail +="<td style='color:"+color+"' onClick='getProcessInfoDiv("+data.page.rows[i].empId+",\""+date+"\")'>"+approvalStatus+"</td>";
							}else{
								classDetail +="<td style='color:"+color+"'>"+approvalStatus+"</td>";
							}
						}
						classDetail += "</tr>";
						$("#reportListTitle").find("tbody").append(classDetailTitle);
						$("#reportList").find("tbody").append(classDetail);
					}
				}	
				if(data != null && data.page.pageNo != null) {
					page = data.page.pageNo;
				}
				initPage("commonPage",data.page,page);
			}else{
				JEND.load('util.dialog', function() {
					if(data.message==null){
						JEND.util.dialog.alert("查询失败");
					}else{
						JEND.util.dialog.alert(data.message);
					}
					
				});
			}
			
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
	
}

//获取流程详情弹框
function getProcessInfoDiv(employeeId,date){	
	pageLoading(true);
	$.ajax({
		async:true,
		type:'post',
		timeout : 5000,
		dataType:'json',
		data:{workDate:date,employeeId:employeeId},
		url:contextPath + "/workLog/getProcessInfoDiv.htm",
		success:function(data){
			if(data.success){
				$(".processInfo").empty();
				$(".searchDiv").hide();
				$("#table").hide();
				$("#commonPage").hide();
				var html = '<div class="form-main" style="height:auto;"><div class="col" style="height:auto;"><div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">员工姓名</label><input type="text"  readonly="readonly" value='+data.cnName+'></div>';
				html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">所属部门</label><input type="text"  readonly="readonly" value='+data.departName+'></div>';
				html +='<div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">日期</label><input type="text"  readonly="readonly" value='+data.workDate+'></div></div>'
				html +='<div class="col" style="height:auto;"><div class="form" style="height:auto;"><label class="w" style="width:130px;height:auto;">审阅状态</label><input type="text"  readonly="readonly" value='+data.approvalStatus+'></div>';
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
                html += '<div class="col"><div class="buttonDiv button-wrap ml-4"><button onclick="closeProcesInfo();" class="btn-orange btn-large"><span><i class="icon"></i>关闭</span></button><button onclick="pass('+data.processId+');" class="btn-green btn-large"><span><i class="icon"></i>通过</span></button><button onclick="refuse('+data.processId+');" class="btn-red btn-large"><span><i class="icon"></i>拒绝</span></button></div></div>';
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

function pass(processId){
	var commentType = "pass";
	completeTask(processId,commentType);
}

function refuse(processId){
	var commentType = "refuse";
	completeTask(processId,commentType);
}

function completeTask(processId,commentType){
	var msg = "&nbsp;";
	if(commentType == "pass"){
		msg = "是否确认通过？";
	}else if(commentType == "refuse"){
		msg = "是否确认拒绝？";
	}
	secondConfirmation({
		   "msg":msg,
			sureFn:function(){
				pageLoading(true);
				$.ajax({
			 		async : true,
			 		type : "post",
					dataType:"json",
					data : {processId:processId,commentType:commentType},
			 		url : contextPath + "/workLog/completeTask.htm",
			 		success : function(response) {
			 			if(response.success){
			 				JEND.page.alert({type:"success", message:response.message});
			 			}else{
			 				JEND.page.showError(response.message);
			 			}
					},
					complete:function(XMLHttpRequest,textStatus){ 
						closeProcesInfo();
						pageLoading(false);//关闭动画
						gotoPage($("#pageNo").val());
			        }
			 	});
			}
		});
}

function closeProcesInfo(){
	$(".processInfo").empty();
	$(".searchDiv").show();
	$("#table").show();
	$("#commonPage").show();
}
