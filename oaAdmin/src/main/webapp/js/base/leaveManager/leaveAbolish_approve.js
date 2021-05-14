$(function(){
	gotoPage();
	gotoPage1();
	gotoPage2();
	$("#query").click(function(){
		gotoPage(1);
	});
	$(".jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		$(obj).click(function(){
			if(i==0){
				$("input[name='selectAll']").prop("checked",false);
				$(".daiban").show();
				$("#commonPage").show();
				$(".yiban").hide();
				$("#commonPage1").hide();
				$(".abate").hide();
				$("#commonPage2").hide();
				$("#query").unbind("click");
				$("#query").click(function(){
					gotoPage(1);
				});
				$("#listTab").click(function(){
					gotoPage(1);
				});
			}else if(i==1){
				$(".daiban").hide();
				$("#commonPage").hide();
				$(".abate").hide();
				$("#commonPage2").hide();
				$(".yiban").show();
				$("#commonPage1").show();
				$("#query").unbind("click");
				$("#query").click(function(){
					gotoPage1(1);
				});
				$("#detailTab").click(function(){
					gotoPage1(1);
				});
			}else if(i==2){
				$("input[name='selectAllAbate']").prop("checked",false);
				$(".daiban").hide();
				$("#commonPage").hide();
				$(".yiban").hide();
				$("#commonPage1").hide();
				$(".abate").show();
				$("#commonPage2").show();
				$("#query").unbind("click");
				$("#query").click(function(){
					gotoPage2(1);
				});
				$("#abateTab").click(function(){
					gotoPage2(1);
				});
			}
		})
		$("input[name='selectAll']").prop("checked",false);
		$("input[name='selectAllAbate']").prop("checked",false);
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
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=15";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationLeaveBack/getApproveIngPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'><input processInstanceId="+response.rows[i].processInstanceId+" type='checkbox' name='selectSingle' class='selectSingle'/></td>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
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
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].startTimeDetail)?response.rows[i].startTimeDetail.substring(0,16):'')+"</td>";
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].actualEndTime)?response.rows[i].actualEndTime.substring(0,16):'')+"</td>";
				if(response.rows[i].leaveType==5){
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDaysDetail-response.rows[i].leaveDays)+"天("+(response.rows[i].leaveHoursDetail-response.rows[i].leaveHours)+"小时)</td>";
				}else{
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDaysDetail-response.rows[i].leaveDays)+"天</td>";
				}
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].startTime)?response.rows[i].startTime.substring(0,16):'')+"</td>";
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].endTime)?response.rows[i].endTime.substring(0,16):'')+"</td>";
				if(response.rows[i].leaveType==5){
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDays)+"天("+response.rows[i].leaveHours+"小时)</td>";
				}else{
					if(response.rows[i].leaveType==7 && response.rows[i].leaveHours!=null){
						html += "<td style='text-align:center;'>"+response.rows[i].leaveDays+"+"+response.rows[i].leaveHours+"</td>";
					}else{
						html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDays)+"天</td>";
					}
				}
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].createTime)?response.rows[i].createTime.substring(5,16):'')+"</td>";
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
				}
				html += "<td style='text-align:center;'>"+approvalStatus+"</td>";
				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a>&nbsp;<a onclick='getPassDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>同意</a>&nbsp;<a onclick='getRefuseDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>拒绝</a></td>";
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			selectAll();
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			$("#token").val(XMLHttpRequest.getResponseHeader('token'));
			pageLoading(false);//关闭动画
        }
	});
}

function gotoPage1(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo1").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo1").val() + "&rows=15";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationLeaveBack/myLeaveTaskList.htm",
		success:function(response){
			$("#reportList1").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
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
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].startTimeDetail)?response.rows[i].startTimeDetail.substring(0,16):'')+"</td>";
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].actualEndTime)?response.rows[i].actualEndTime.substring(0,16):'')+"</td>";
				if(response.rows[i].leaveType==5){
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDaysDetail-response.rows[i].leaveDays)+"天("+(response.rows[i].leaveHoursDetail-response.rows[i].leaveHours)+"小时)</td>";
				}else{
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDaysDetail-response.rows[i].leaveDays)+"天</td>";
				}
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].startTime)?response.rows[i].startTime.substring(0,16):'')+"</td>";
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].endTime)?response.rows[i].endTime.substring(0,16):'')+"</td>";
				if(response.rows[i].leaveType==5){
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDays)+"天("+getValByUndefined(response.rows[i].leaveHours)+"小时)</td>";
				}else{
					if(response.rows[i].leaveType==7 && response.rows[i].leaveHours!=null){
						html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDays)+"+"+getValByUndefined(response.rows[i].leaveHours)+"</td>";
					}else{
						html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDays)+"天</td>";
					}
				}
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].createTime)?response.rows[i].createTime.substring(0,16):'')+"</td>";
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
				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a</td>";
				html += "</tr>";
			}
			$("#reportList1").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPageByName("commonPage1",response,page,"gotoPage1");
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}

function gotoPage2(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo2").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo2").val() + "&rows=15";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationLeaveBack/getAbatePageList.htm",
		success:function(response){
			$("#reportList2").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'><input processInstanceId="+response.rows[i].processInstanceId+" type='checkbox' name='selectSingleAbate' class='selectSingle'/></td>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
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
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].startTimeDetail)?response.rows[i].startTimeDetail.substring(0,16):'')+"</td>";
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].actualEndTime)?response.rows[i].actualEndTime.substring(0,16):'')+"</td>";
				if(response.rows[i].leaveType==5){
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDaysDetail-response.rows[i].leaveDays)+"天("+(response.rows[i].leaveHoursDetail-response.rows[i].leaveHours)+"小时)</td>";
				}else{
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDaysDetail-response.rows[i].leaveDays)+"天</td>";
				}
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].startTime)?response.rows[i].startTime.substring(0,16):'')+"</td>";
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].endTime)?response.rows[i].endTime.substring(0,16):'')+"</td>";
				if(response.rows[i].leaveType==5){
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDays)+"天("+getValByUndefined(response.rows[i].leaveHours)+"小时)</td>";
				}else{
					if(response.rows[i].leaveType==7 && response.rows[i].leaveHours!=null){
						html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDays)+"+"+getValByUndefined(response.rows[i].leaveHours)+"天</td>";
					}else{
						html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveDays)+"天</td>";
					}
				}
				html += "<td style='text-align:center;'>"+(isVlues(response.rows[i].createTime)?response.rows[i].createTime.substring(0,16):'')+"</td>";
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
				if(response.rows[i].approvalStatus==500){
					html += "<td style='text-align:left;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='getPassDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>失效同意</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='getRefuseDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>失效拒绝</a></td>";
				}else{
					html += "<td style='text-align:left;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a></td>";
				}
				html += "</tr>";
			}
			$("#reportList2").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			selectAllAbate();
			initPage("commonPage2",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}


function selectAll(){
	$("input[name='selectAll']").click(function(){
		 if($(this).prop("checked") == true){
			 $("input[name='selectSingle']").attr("checked",true);
		 }else{
			 $("input[name='selectSingle']").attr("checked",false);
		 }
	});
	$("input[name='selectSingle']").click(function(){
		var i = 0;
		$("input[name='selectSingle']").each(function(){
			if($(this).prop("checked") == true){
				i = i+1;
			}else{
				i = i-1;
			}
		});
		if(i !=0 && i == $("input[name='selectSingle']").length){
			$("input[name='selectAll']").attr("checked",true);
		}else{
			$("input[name='selectAll']").attr("checked",false);
		}
	});
};


function selectAllAbate(){
	$("input[name='selectAllAbate']").click(function(){
		 if($(this).prop("checked") == true){
			 $("input[name='selectSingleAbate']").attr("checked",true);
		 }else{
			 $("input[name='selectSingleAbate']").attr("checked",false);
		 }
	});
	$("input[name='selectSingleAbate']").click(function(){
		var i = 0;
		$("input[name='selectSingleAbate']").each(function(){
			if($(this).prop("checked") == true){
				i = i+1;
			}else{
				i = i-1;
			}
		});
		if(i !=0 && i == $("input[name='selectSingleAbate']").length){
			$("input[name='selectSingleAbate']").attr("checked",true);
		}else{
			$("input[name='selectSingleAbate']").attr("checked",false);
		}
	});
};


function getBatchApproveDiv(){
	if($("input[name='selectSingle']:checked").length<=0){
		JEND.page.showError('请选择需要审批的销假申请单！');
		return;
	}
	$("#fastApproveDiv").show();
}

function getBatchApproveAbateDiv(){
	if($("input[name='selectSingleAbate']:checked").length<=0){
		JEND.page.showError('请选择需要审批的销假申请单！');
		return;
	}
	$("#fastApproveAbateDiv").show();
}

function closeDiv(){
	$("#fastApproveAbateDiv").hide();
	$("#fastApproveDiv").hide();
	$("#passDiv").hide();
	$("#refuseDiv").hide();
	$("#detailDiv").hide();
}

function fastApprove(){
	var processInstanceIds = "";
	$("input[name='selectSingle']:checked").each(function(){
		processInstanceIds += $(this).attr("processInstanceId")+",";
	});
	var commentType = $("#fastApproveForm").find("input[name='commentType']:checked").val();
	var comment = $("#fastApproveForm").find("textarea[name='approvalReason']").val();
	processInstanceIds = processInstanceIds.substring(0,processInstanceIds.length-1);
	completeLeave(processInstanceIds,comment,commentType);
}

function fastApproveAbate(){
	var processInstanceIds = "";
	$("input[name='selectSingleAbate']:checked").each(function(){
		processInstanceIds += $(this).attr("processInstanceId")+",";
	});
	var commentType = $("#fastApproveAbateForm").find("input[name='commentType']:checked").val();
	var comment = $("#fastApproveAbateForm").find("textarea[name='approvalReason']").val();
	processInstanceIds = processInstanceIds.substring(0,processInstanceIds.length-1);
	completeLeave(processInstanceIds,comment,commentType);
}

function getPassDiv(processInstanceId){
	$("#passDiv").show();
	$("#passDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}

function pass(){
	var processInstanceIds = $("#passDivForm").find("input[name='processInstanceId']").val();
	var commentType = "pass";
	var comment = $("#passDivForm").find("textarea[name='approvalReason']").val();
	completeLeave(processInstanceIds,comment,commentType);
}

function getRefuseDiv(processInstanceId){
	$("#refuseDiv").show();
	$("#refuseDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}

function refuse(){
	var processInstanceIds = $("#refuseDivForm").find("input[name='processInstanceId']").val();
	var commentType = "refuse";
	var comment = $("#refuseDivForm").find("textarea[name='approvalReason']").val();
	completeLeave(processInstanceIds,comment,commentType);
}

function getDetailDiv(processId){
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processId:processId},
 		url : contextPath + "/empApplicationLeaveBack/getleaveAbolishDetail.htm",
 		success : function(response) {
 			if(response.sucess){
 				var approveStatus = response.leaveAbolish.approvalStatus;
 				var leaveType = response.detailList.leaveType;
 				var leaveDays = response.detailList.leaveDays;
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
 					leaveDaysC = leaveDays+"天"+"（"+response.detailList.leaveHours+"小时）";
 				}else if(leaveType==6){
 					leaveTypeC = "产前假";
 					leaveDaysC = leaveDays+"天";
 				}else if(leaveType==7){
 					leaveTypeC = "产假";
 					leaveDaysC = leaveDays+"天";
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
 				$("#detailDiv3").html(response.leave.createTime);
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
 				

 				//销假开始时间
 				$("#detailDiv9").html("");
 				$("#detailDiv9").html(response.leaveAbolish.startTime);
 				//销假结束时间
 				$("#detailDiv10").html("");
 				$("#detailDiv10").html(response.leaveAbolish.endTime);
 				//销假时长
 				$("#detailDiv11").html("");
 				if(leaveType==5){
 					$("#detailDiv11").html(response.leaveAbolish.leaveDays+"天"+"（"+response.leaveAbolish.leaveHours+"小时）");
 				}else{
 					$("#detailDiv11").html(response.leaveAbolish.leaveDays+"天");
 				}
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

function completeLeave(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processIds:processInstanceIds,comment:comment,commentType:commentType,token:$("#token").val()},
 		url : contextPath + "/empApplicationLeaveBack/completeLeave.htm",
 		success : function(response) {
 			if(response.sucess){
 				gotoPage($("#pageNo").val());
 			}else{
 				JEND.page.showError(response.msg);
 			}
 			$("#token").val(response.token);
		},  
        error : function(XMLHttpRequest,textStatus) { 
        	if(XMLHttpRequest.status=="9999"){
        		JEND.page.showError("请勿重复提交，请刷新页面重试。");
			}
        },
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
			pageLoading(false);//关闭动画
        }
 	});
}