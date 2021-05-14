$(function(){
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化工时类型
	initWorkType(currentCompanyId);
	//初始化员工类型
	initEmployeeType(currentCompanyId);
	//初始化是否排班
	initWhetherScheduling();
	//初始化单据状态
	initApprovalStatus(currentCompanyId);
	//初始化部门
	getFirstDepart();
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
	});
	
	$("#export").click(function() {
		var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
		window.location.href = basePath + "empApplicationLeave/exportReport.htm?"+data;
	});
	
	//上周
	$("#lastWeek").click(function(){
		setLastWeek("applyStartDate","applyEndDate", 'yyyy-MM-dd')
	});
	//本月
	$("#thisMonth").click(function(){
		setCurrentMonthTime("applyStartDate","applyEndDate", 'yyyy-MM-dd');
	});
	//上月
	$("#lastMonth").click(function(){
		setLastMonthTime("applyStartDate","applyEndDate", 'yyyy-MM-dd');
	});
});

//假期详情
function showLeaveDetail(){
	$(".artical-box-bg").show();
}
function closeLeaveDetail(){
	$(".artical-box-bg").hide();
}


function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=10";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationLeave/getReportPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].positionName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].workType+"</td>";
				var leaveType = "";
				if(response.rows[i].leaveType==1){
					leaveType = "年假";
				}else if(response.rows[i].leaveType==2){
					leaveType = "病假";
				}else if(response.rows[i].leaveType==3){
					leaveType = "婚假";
				}else if(response.rows[i].leaveType==4){
					leaveType = "哺乳假";
				}else if(response.rows[i].leaveType==5){
					leaveType = "调休";
				}else if(response.rows[i].leaveType==6){
					leaveType = "产前假";
				}else if(response.rows[i].leaveType==7){
					leaveType = "产假";
				}else if(response.rows[i].leaveType==8){
					leaveType = "流产假";
				}else if(response.rows[i].leaveType==9){
					leaveType = "陪产假";
				}else if(response.rows[i].leaveType==10){
					leaveType = "丧假";
				}else if(response.rows[i].leaveType==11){
					leaveType = "事假";
				}else if(response.rows[i].leaveType==12){
					leaveType = "其他";
				}
				html += "<td style='text-align:center;'>"+leaveType+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				if(response.rows[i].leaveType==5){
					html += "<td style='text-align:center;'>"+response.rows[i].leaveDays+"("+response.rows[i].leaveHours+"小时)</td>";
				}else{
					if(response.rows[i].leaveType==7 && response.rows[i].leaveHours!=null){
						html += "<td style='text-align:center;'>"+response.rows[i].leaveDays+"+"+response.rows[i].leaveHours+"</td>";
					}else{
						html += "<td style='text-align:center;'>"+response.rows[i].leaveDays+"</td>";
					}
					
				}
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].submitDate+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].auditUser+"</td>";
				var approvalStatus = "";
				if(response.rows[i].approvalStatus==100){
					approvalStatus = "待审批";
				}else if(response.rows[i].approvalStatus==200){
					approvalStatus = "已审批";
				}else if(response.rows[i].approvalStatus==300){
					approvalStatus = "已拒绝";
				}else if(response.rows[i].approvalStatus==400){
					approvalStatus = "已撤销";
				}else if(response.rows[i].approvalStatus==500){
					approvalStatus = "已失效";
				}else if(response.rows[i].approvalStatus==600){
					approvalStatus = "失效同意";
				}else if(response.rows[i].approvalStatus==700){
					approvalStatus = "失效拒绝";
				}
				html += "<td style='text-align:center;'>"+approvalStatus+"</td>";
				if(response.rows[i].approvalStatus==100){
					html += "<td style='text-align:left;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='showRefuseDiv(this);' style='color:blue;' href='#'>拒绝</a></td>";
				}else if(response.rows[i].approvalStatus==500){
					html += "<td style='text-align:left;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='getPassDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>失效同意</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='getRefuseDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>失效拒绝</a></td>";
				}else{
					html += "<td style='text-align:left;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a></td>";
				}
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			$("#token").val(XMLHttpRequest.getResponseHeader('token'));
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}

function showRefuseDiv(obj){
	$("#employeeName").val($(obj).parents("tr").find("td:eq(1)").text());
	$("#leaveType1").val($(obj).parents("tr").find("td:eq(5)").text());
	$("#startTime").val($(obj).parents("tr").find("td:eq(6)").text());
	$("#endTime").val($(obj).parents("tr").find("td:eq(7)").text());
	$("#leaveDays").val($(obj).parents("tr").find("td:eq(8)").text());
	$("#reason").val("已提出离职，未审批通过的假期请重新申请。");
	$("#refuseLeave").attr("processInstanceId",$(obj).attr("id"))
	$("#refuseDiv").show();
}

function closeDiv(){
	$("#refuseDiv").hide();
	$("#passDiv").hide();
	$("#refuseAbateDiv").hide();
}

function getPassDiv(processInstanceId){
	$("#passDiv").show();
	$("#passDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}
function getRefuseDiv(processInstanceId){
	$("#refuseAbateDiv").show();
	$("#refuseAbateDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}

function pass(){
	var processInstanceIds = $("#passDivForm").find("input[name='processInstanceId']").val();
	var comment = $("#passDivForm").find("textarea[name='approvalReason']").val();
	passInvalidLeave(processInstanceIds,comment);
}

function refuseAbate(){
	var processInstanceIds = $("#refuseAbateDivForm").find("input[name='processInstanceId']").val();
	var comment = $("#refuseAbateDivForm").find("textarea[name='approvalReason']").val();
	refuseInvalidLeave(processInstanceIds,comment);
}

function passInvalidLeave(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processId:processInstanceIds,comment:comment,token:$("#token").val()},
 		url : contextPath + "/empApplicationLeave/passInvalidLeave.htm",
 		success : function(response) {
 			if(response.result.flag){
 				gotoPage($("#pageNo").val());
 			}else{
 				JEND.page.showError(response.msg);
 			}
 			$("#refuseAbateDivForm").find("textarea[name='approvalReason']").val("");
 			$("#passDivForm").find("textarea[name='approvalReason']").val("");
 			$("#token").val(response.result.token);
		},  
        error : function(XMLHttpRequest,textStatus) { 
        	if(XMLHttpRequest.status=="9999"){
        		JEND.page.showError("请勿重复提交，请刷新页面重试。");
			}
        },
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
 	});
}

function refuseInvalidLeave(processInstanceIds,comment){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processId:processInstanceIds,comment:comment,token:$("#token").val()},
 		url : contextPath + "/empApplicationLeave/refuseInvalidLeave.htm",
 		success : function(response) {
 			if(response.result.flag){
 				gotoPage($("#pageNo").val());
 			}else{
 				JEND.page.showError(response.msg);
 			}
 			$("#token").val(response.result.token);
 			$("#refuseAbateDivForm").find("textarea[name='approvalReason']").val("");
 			$("#passDivForm").find("textarea[name='approvalReason']").val("");
		},  
        error : function(XMLHttpRequest,textStatus) { 
        	if(XMLHttpRequest.status=="9999"){
        		JEND.page.showError("请勿重复提交，请刷新页面重试。");
			}
        },
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
 	});
}

function refuseLeave(obj){
	pageLoading(true);//加载动画
	if($(obj).attr("processInstanceId")=="undefined"){
		JEND.page.showError('流程id为空');
		pageLoading(false);//关闭动画
		return;
	}
	
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processInstanceId:$(obj).attr("processInstanceId"),comment:$("#reason").val(),token:$("#token").val()},
 		url : contextPath + "/empApplicationLeave/refuseLeave.htm",
 		success : function(response) {
 			if(response.result.flag){
 				gotoPage($("#pageNo").val());
 			}else{
 				JEND.page.showError(response.result.message);
 			}
 			$("#token").val(response.result.token);
		},  
        error : function(XMLHttpRequest,textStatus) { 
        	if(XMLHttpRequest.status=="9999"){
        		JEND.page.showError("请勿重复提交，请刷新页面重试。");
			}
        },
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			closeDiv();
			pageLoading(false);//关闭动画
        }
 	});
}

function closeBigDiv(){
	$("#fastApproveDiv").hide();
	$("#passDiv").hide();
	$("#refuseDiv").hide();
	$("#detailDiv").hide();
}

function getDetailDiv(processId){
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processId:processId},
 		url : contextPath + "/empApplicationLeave/getLeaveDetail.htm",
 		success : function(response) {
 			if(response.sucess){
 				var approveStatus = response.leave.approvalStatus;
 				var leaveType = response.detailList[0].leaveType;
 				var leaveDays = response.detailList[0].leaveDays;
 				var leaveTypeC = "";
 				var leaveDaysC = "";
 				if(leaveType==1){
 					leaveTypeC = "年假";
 					leaveDaysC = leaveDays+"天";
 				}else if(leaveType==2){
 					leaveTypeC = "病假";
 					leaveDaysC = leaveDays+"天";
 				}else if(leaveType==3){
 					leaveTypeC = "婚假";
 					leaveDaysC = leaveDays+"天";
 				}else if(leaveType==4){
 					leaveTypeC = "哺乳假";
 					leaveDaysC = leaveDays+"天";
 				}else if(leaveType==5){
 					leaveTypeC = "调休";
 					leaveDaysC = leaveDays+"天"+"（"+response.detailList[0].leaveHours+"小时）";
 				}else if(leaveType==6){
 					leaveTypeC = "产前假";
 					leaveDaysC = leaveDays+"天";
 				}else if(leaveType==7){
 					leaveTypeC = "产假";
 					var extraDays = (typeof(response.detailList[0].leaveHours)=="undefined"==true||response.detailList[0].leaveHours<=0)?0:response.detailList[0].leaveHours;
 					leaveDaysC = leaveDays+"天";
 					if(extraDays>0){
 						leaveDaysC = leaveDays+" + "+extraDays+"天";
 					}
 				}else if(leaveType==8){
 					leaveTypeC = "流产假";
 					leaveDaysC = leaveDays+"天";
 				}else if(leaveType==9){
 					leaveTypeC = "陪产假";
 					leaveDaysC = leaveDays+"天";
 				}else if(leaveType==10){
 					leaveTypeC = "丧假";
 					leaveDaysC = leaveDays+"天";
 				}else if(leaveType==11){
 					leaveTypeC = "事假";
 					leaveDaysC = leaveDays+"天";
 				}else if(leaveType==12){
 					leaveTypeC = "其他";
 					leaveDaysC = leaveDays+"天";
 				}
 				if(approveStatus!=100){
 					$("#detail").hide();
 				}else{
 					$("#detail").show();
 				}
 				//申请人
 				$("#detailDiv1").html("");
 				$("#detailDiv1").html(response.leave.cnName);
 				//所属部门
 				$("#detailDiv2").html("");
 				$("#detailDiv2").html(response.leave.departName);
 				//申请时间
 				$("#detailDiv3").html("");
 				$("#detailDiv3").html(response.leave.submitDate);
 				//假期类型
 				$("#detailDiv4").html("");
 				$("#detailDiv4").html(leaveTypeC);
 				//开始时间
 				$("#detailDiv5").html("");
 				$("#detailDiv5").html(response.leave.startTime);
 				//结束时间
 				$("#detailDiv6").html("");
 				$("#detailDiv6").html(response.leave.endTime);
 				//时长
 				$("#detailDiv7").html("");
 				$("#detailDiv7").html(leaveDaysC);
 				//请假事由
 				$("#detailDiv8").html("");
 				$("#detailDiv8").html(response.leave.reason);
 				//紧急联系电话
 				$("#detailDiv9").html("");
 				$("#detailDiv9").html(response.leave.mobile);
 				//假期代理人
 				$("#detailDiv10").html("");
 				$("#detailDiv10").html(response.leave.agent);
 				//代理人联系电话
 				$("#detailDiv11").html("");
 				$("#detailDiv11").html(response.leave.agentMobile);
 				var length = response.flows.length;
 				$("#taskFlow").empty();
 				for(var i=0;i<length;i++){
 					if(response.flows[i].statu==null){
 						var li ="<li><b></b><div class='r'><div class='p1'>";
 						if(response.flows[i].assigneeName==null||response.flows[i].assigneeName==""){
 							li+=response.flows[i].departName+"</div>";
 						}else{
 							li+=response.flows[i].assigneeName+"&nbsp;"+response.flows[i].positionName+"</div>";
 						}
 						li+=" <div class='p3 gren'>待审批</div></div></li>";
 						$("#taskFlow").append(li);
 					}else if(response.flows[i].statu==200){
 						var li = "<li><em class='gren'>流程结束<i></i></em><div class='r'> <div class='p1'>";
 						if(response.flows[i].finishTime==null){
 							li += "&nbsp;</div>";
 						}else{
 							li += response.flows[i].finishTime+"</div>";
 						}
 						li+="<div class='p2'>"+response.flows[i].assigneeName+"&nbsp;"+response.flows[i].positionName+"</div>";
 						li+="<div class='p3 gren'>审批通过</div><div class='p4'><i>审批意见:</i>";
                        if(response.flows[i].comment!=null&&response.flows[i].comment!=""){
                        	li+=response.flows[i].comment+"</div></div></li>";
                        }else{
                        	li+="无</div></div></li>";
                        }
                        $("#taskFlow").append(li);     	
 					}else if(response.flows[i].statu==100){
 					    var li = "<li><b></b><div class='r'><div class='p1'>";
 					    if(response.flows[i].finishTime==null){
							li += "&nbsp;</div>";
					    }else{
							li += response.flows[i].finishTime+"</div>";
					    }
 					    li+="<div class='p2'>"+response.flows[i].assigneeName+"&nbsp;"+response.flows[i].positionName+"</div>";
 					    li+="<div class='p3 gren'>审批通过</div><div class='p4'><i>审批意见:</i>";
	 				    if(response.flows[i].comment!=null&&response.flows[i].comment!=""){
	                      	li+=response.flows[i].comment+"</div></div></li>";
	                    }else{
	                      	li+="无</div></div></li>";
	                    }
	 				    $("#taskFlow").append(li);
 					}else if(response.flows[i].statu==300){
 						var li = "<li><em class='gren'>流程结束<i></i></em>";
 						li+="<div class='r'><div class='p1'>";
 						if(response.flows[i].finishTime==null){
							li += "&nbsp;</div>";
					    }else{
							li += response.flows[i].finishTime+"</div>";
					    }
 					    li+="<div class='p2'>"+response.flows[i].assigneeName+"&nbsp;"+response.flows[i].positionName+"</div>"; 
 					    li+="<div class='p3 red'>审批拒绝</div><div class='p4'><i>审批意见:</i>";
 					    if(response.flows[i].comment!=null&&response.flows[i].comment!=""){
	                      	li+=response.flows[i].comment+"</div></div></li>";
	                    }else{
	                      	li+="无</div></div></li>";
	                    }
 					    $("#taskFlow").append(li);
 					}else if(response.flows[i].statu==400){
 						var li = "<li><em class='gren'>流程结束<i></i></em>";
 						li+="<div class='r'><div class='p1'>";
 						if(response.flows[i].finishTime==null){
							li += "&nbsp;</div>";
					    }else{
							li += response.flows[i].finishTime+"</div>";
					    }
 						li+="<div class='p2'>"+response.flows[i].assigneeName+"&nbsp;"+response.flows[i].positionName+"</div>"; 
 						li+="<div class='p3 gren'>撤销</div><div class='p4'><i>撤销原因:</i>";
 						if(response.flows[i].comment!=null&&response.flows[i].comment!=""){
	                      	li+=response.flows[i].comment+"</div></div></li>";
	                    }else{
	                      	li+="无</div></div></li>";
	                    }
 						$("#taskFlow").append(li);
 					}else if(response.flows[i].statu==600){
 						var li = "<li><em class='gren'>流程结束<i></i></em>";
 						li+="<div class='r'><div class='p1'>";
 						if(response.flows[i].finishTime==null){
							li += "&nbsp;</div>";
					    }else{
							li += response.flows[i].finishTime+"</div>";
					    }
 						li+="<div class='p2'>"+response.flows[i].assigneeName+"&nbsp;"+response.flows[i].positionName+"</div>"; 
 						li+="<div class='p3 gren'>失效同意</div><div class='p4'><i>同意原因:</i>";
 						if(response.flows[i].comment!=null&&response.flows[i].comment!=""){
	                      	li+=response.flows[i].comment+"</div></div></li>";
	                    }else{
	                      	li+="无</div></div></li>";
	                    }
 						$("#taskFlow").append(li);
 					}else if(response.flows[i].statu==700){
 						var li = "<li><em class='gren'>流程结束<i></i></em>";
 						li+="<div class='r'><div class='p1'>";
 						if(response.flows[i].finishTime==null){
							li += "&nbsp;</div>";
					    }else{
							li += response.flows[i].finishTime+"</div>";
					    }
 						li+="<div class='p2'>"+response.flows[i].assigneeName+"&nbsp;"+response.flows[i].positionName+"</div>"; 
 						li+="<div class='p3 gren'>失效拒绝</div><div class='p4'><i>拒绝原因:</i>";
 						if(response.flows[i].comment!=null&&response.flows[i].comment!=""){
	                      	li+=response.flows[i].comment+"</div></div></li>";
	                    }else{
	                      	li+="无</div></div></li>";
	                    }
 						$("#taskFlow").append(li);
 					}else if(response.flows[i].statu==0){
 						var li = "<li><em class='blue'>提出申请<i></i></em>";
 						li+="<div class='r'><div class='p1'>";
 						if(response.flows[i].finishTime==null){
							li += "&nbsp;</div>";
					    }else{
							li += response.flows[i].finishTime+"</div>";
					    }
 						li+="<div class='p2'>"+response.flows[i].assigneeName+"&nbsp;"+response.flows[i].positionName+"</div>"; 
 						li+="<div class='p3 blue'>发起申请</div><div class='p4'><i>申请理由:</i>";
 						if(response.flows[i].comment!=null&&response.flows[i].comment!=""){
	                      	li+=response.flows[i].comment+"</div></div></li>";
	                    }else{
	                      	li+="无</div></div></li>";
	                    }
 						$("#taskFlow").append(li);  
 					}
 				}
 				$("#passDetail").unbind("click");
 				$("#refuseDetail").unbind("click");
 				$("#passDetail").bind("click", function(){
 					getPassDiv(processId);
 				});
                $("#refuseDetail").bind("click", function(){
                	getRefuseDiv(processId);
 				});
 				$("#detailDiv").show();
 				//假期详情
 				$("#allowAnnualLeave").html(response.leaveInfo.allowAnnualLeave+"天");
 				$("#allowSickLeave").html(response.leaveInfo.allowSickLeave+"天");
 				$("#allowDayOff").html(response.leaveInfo.allowDayOff+"小时");
 				$("#usedAnnualLeave").html(response.leaveInfo.usedAnnualLeave+"天");
 				$("#usedSickLeave").html(response.leaveInfo.usedSickLeave+"天");
 				$("#usedDayOff").html(response.leaveInfo.usedDayOff+"小时");
 				$("#affairsLeave").html(response.leaveInfo.affairsLeave+"天");
 			}else{
 				JEND.page.showError(response.msg);
 			}
		},
		complete:function(XMLHttpRequest,textStatus){  
			
        }
 	});
}