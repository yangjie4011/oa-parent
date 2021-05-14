$(function(){
	gotoPage(1);
	$("#query1").click(function(){
		gotoPage(1);
	});
	$("#query2").click(function(){
		doneUpAttnPage(1);
	});
	$("#query3").click(function(){
		DownAttnPage(1);
	});
	$("#query4").click(function(){
		doneDownAttnPage(1);
	});
	$("#query5").click(function(){
		gotoPage5(1);
	});
	$("#query6").click(function(){
		gotoPage6(1);
	});

	$(".lastWeek").click(function(){
		setLastWeekByClass("applyStartDate","applyEndDate", 'yyyy-MM-dd')
	});
	//本月
	$(".thisMonth").click(function(){
		setCurrentMonthTimeByClass("applyStartDate","applyEndDate", 'yyyy-MM-dd');
	});
	//上月
	$(".lastMonth").click(function(){
		setLastMonthTimeByClass("applyStartDate","applyEndDate", 'yyyy-MM-dd');
	});
	
	var currentCompanyId = $("#currentCompanyId").val();
	//根据 class 为 workType 初始化工时类型
	initWorkTypebyClass(currentCompanyId);
	//根据 class 为 approvalStatus 初始化工时类型
	initApprovalStatusbyClass(currentCompanyId);
	//初始化多个部门 根据class来
	getFirstDepartByClass();
	

	
	//页面大tab布局
	$(".jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		$(obj).click(function(){
			if(i==0){
				sonList(0,0);
				$(".first").show();
				$("#commonPage1").show();
				$(".third").hide();
				$("#commonPage3").hide();		
				$("#flagTab").val(0);
				//gotoPage(1);
			}else if(i==1){
				sonList(1,0);
				$(".first").hide();
				$("#commonPage1").hide();
				$(".third").show();
				$("#commonPage3").show();				
				$("#flagTab").val(1);
				//DownAttnPage(1);
			}
		})
	});
});

//页面小tab布局
function sonList(j,k){
	//如果j=null就判断开始了
	var flagTab="";
	if(j==null || typeof(j)=="undefined"){
		flagTab = $("#flagTab").val();
		if(flagTab==""){
			flagTab = 0;
		}
	}
	$(".content").hide();
	$(".paging").hide();
	//去除所有文本框值
	$(".workType").val("");
	$(".applyStartDate").val("");
	$(".applyEndDate").val("");
	$(".firstDepart").val("");
	$(".approvalStatus").val("");
	$("input[name='code']").val("");
	$("input[name='cnName']").val("");
	$("input[name='applyName']").val("");
	$("input[name='agentName']").val("");
	if(k==0){
		if(flagTab==0){//代办异常考勤
			$(".first").show();
			$("#commonPage1").show();
			gotoPage(1);
		}else if(flagTab==1){//下属异常考勤代办
			$(".third").show();
			$("#commonPage3").show();
			DownAttnPage(1);
		}
	}else if(k==1){
		if(flagTab==0){//已办异常考勤
			$(".second").show();
			$("#commonPage2").show();
			doneUpAttnPage(1);
		}else if(flagTab==1){//已办下属异常考勤
			$(".fourth").show();
			$("#commonPage4").show();
			doneDownAttnPage(1);
		}
	}else if(k==2){
		if(flagTab==0){
			$(".five").show();
			$("#commonPage5").show();
			gotoPage5(1);
		}else if(flagTab==1){
			$(".six").show();
			$("#commonPage6").show();
			gotoPage6(1);
		}
	}
}

function gotoPage(page){
	$("input[name='selectAll']").prop("checked",false);
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo1").val(page);
	var data = $("#queryform1").serialize() + "&page=" + $("#pageNo1").val() + "&rows=10";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationAbnormalAttendance/getAttnApprovePageList.htm",
		success:function(response){
			$("#reportList1").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'><input processInstanceId="+response.rows[i].processInstanceId+" type='checkbox' name='selectSingle' class='selectSingle'/></td>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].workType1+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].abnormalDate+"</td>";
				var upAttn='空卡';
              	var downAttn='空卡';
 				if(response.rows[i].startPunchTime!=null){
 					upAttn=(response.rows[i].startPunchTime).substring(11,16);
 				}
 				if(response.rows[i].endPunchTime!=null){
 					downAttn=(response.rows[i].endPunchTime).substring(11,16);
 				} 		
				html += "<td style='text-align:center;'>"+upAttn+"-"+downAttn+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].auditUser+"</td>";
				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a>&nbsp;<a onclick='getPassDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>同意</a>&nbsp;<a onclick='getRefuseDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>拒绝</a></td>";
				html += "</tr>";
			}
			$("#reportList1").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			selectAll("selectAll","selectSingle");
			initPage("commonPage1",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}

function doneUpAttnPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo2").val(page);
	var data = $("#queryform2").serialize() + "&page=" + $("#pageNo2").val() + "&rows=15";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationAbnormalAttendance/myAttnTaskList.htm",
		success:function(response){
			$("#reportList2").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].workType1+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].abnormalDate+"</td>";
				var upAttn='空卡';
              	var downAttn='空卡';
 				if(response.rows[i].startPunchTime!=null){
 					upAttn=(response.rows[i].startPunchTime).substring(11,16);
 				}
 				if(response.rows[i].endPunchTime!=null){
 					downAttn=(response.rows[i].endPunchTime).substring(11,16);
 				} 		
				html += "<td style='text-align:center;'>"+upAttn+"-"+downAttn+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].auditUser+"</td>";
				
				html += "<td style='text-align:center;'>"+response.rows[i].hrAuditUser+"</td>";
				var approvalStatus = "";
				if(response.rows[i].approvalStatus==100){
					approvalStatus = "待审批";
				}else if(response.rows[i].approvalStatus==200){
					approvalStatus = "同意";
				}else if(response.rows[i].approvalStatus==300){
					approvalStatus = "拒绝";
				}else if(response.rows[i].approvalStatus==400){
					approvalStatus = "已撤销";
				}else if(response.rows[i].approvalStatus==500){
					approvalStatus = "已失效";
				}
				html += "<td style='text-align:center;'>"+approvalStatus+"</td>";
				
				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a></td>";

				html += "</tr>";
			}
			$("#reportList2").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPageByName("commonPage2",response,page,"doneUpAttnPage");
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}


function DownAttnPage(page){
	$("input[name='selectAll1']").prop("checked",false);
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo3").val(page);
	var data = $("#queryform3").serialize() + "&page=" + $("#pageNo3").val() + "&rows=15";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationAbnormalAttendance/getAttnApprovePageList.htm",
		success:function(response){
			$("#reportList3").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'><input processInstanceId="+response.rows[i].processInstanceId+" type='checkbox' name='selectSingle1' class='selectSingle'/></td>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].workType1+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].agentName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].abnormalDate+"</td>";
				var upAttn='空卡';
              	var downAttn='空卡';
 				if(response.rows[i].startPunchTime!=null){
 					upAttn=(response.rows[i].startPunchTime).substring(11,16);
 				}
 				if(response.rows[i].endPunchTime!=null){
 					downAttn=(response.rows[i].endPunchTime).substring(11,16);
 				} 		
				html += "<td style='text-align:center;'>"+upAttn+"-"+downAttn+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a>&nbsp;<a onclick='getPassDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>同意</a>&nbsp;<a onclick='getRefuseDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>拒绝</a></td>";
				html += "</tr>";
			}
			$("#reportList3").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			selectAll("selectAll1","selectSingle1");
			initPageByName("commonPage3",response,page,"DownAttnPage");
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}

function doneDownAttnPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo4").val(page);
	var data = $("#queryform4").serialize() + "&page=" + $("#pageNo4").val() + "&rows=15";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationAbnormalAttendance/myAttnTaskList.htm",
		success:function(response){
			$("#reportList4").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].workType1+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].agentName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].abnormalDate+"</td>";
				var upAttn='空卡';
              	var downAttn='空卡';
 				if(response.rows[i].startPunchTime!=null){
 					upAttn=(response.rows[i].startPunchTime).substring(11,16);
 				}
 				if(response.rows[i].endPunchTime!=null){
 					downAttn=(response.rows[i].endPunchTime).substring(11,16);
 				} 		
				html += "<td style='text-align:center;'>"+upAttn+"-"+downAttn+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].hrAuditUser+"</td>";
				var approvalStatus = "";
				if(response.rows[i].approvalStatus==100){
					approvalStatus = "待审批";
				}else if(response.rows[i].approvalStatus==200){
					approvalStatus = "同意";
				}else if(response.rows[i].approvalStatus==300){
					approvalStatus = "拒绝";
				}else if(response.rows[i].approvalStatus==400){
					approvalStatus = "已撤销";
				}else if(response.rows[i].approvalStatus==500){
					approvalStatus = "已失效";
				}
				html += "<td style='text-align:center;'>"+approvalStatus+"</td>";

				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a</td>";
				html += "</tr>";
			}
			$("#reportList4").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPageByName("commonPage4",response,page,"doneDownAttnPage");
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}

//失效异常考勤
function gotoPage5(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo5").val(page);
	var data = $("#queryform5").serialize() + "&page=" + $("#pageNo5").val() + "&rows=10";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationAbnormalAttendance/getOverdueListByPage.htm",
		success:function(response){
			$("#reportList5").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				//html += "<td style='text-align:center;'><input processInstanceId="+response.rows[i].processInstanceId+" type='checkbox' name='selectSingle' class='selectSingle'/></td>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].workType1+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].abnormalDate+"</td>";
				var upAttn='空卡';
              	var downAttn='空卡';
 				if(response.rows[i].startPunchTime!=null){
 					upAttn=(response.rows[i].startPunchTime).substring(11,16);
 				}
 				if(response.rows[i].endPunchTime!=null){
 					downAttn=(response.rows[i].endPunchTime).substring(11,16);
 				} 		
				html += "<td style='text-align:center;'>"+upAttn+"-"+downAttn+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].auditUser+"</td>";
				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a></td>";
				html += "</tr>";
			}
			$("#reportList5").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPageByName("commonPage5",response,page,"gotoPage5");
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}

//失效下属异常考勤
function gotoPage6(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo6").val(page);
	var data = $("#queryform6").serialize() + "&page=" + $("#pageNo6").val() + "&rows=15";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationAbnormalAttendance/getOverdueListByPage.htm",
		success:function(response){
			$("#reportList6").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				//html += "<td style='text-align:center;'><input processInstanceId="+response.rows[i].processInstanceId+" type='checkbox' name='selectSingle1' class='selectSingle'/></td>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].workType1+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].agentName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].abnormalDate+"</td>";
				var upAttn='空卡';
              	var downAttn='空卡';
 				if(response.rows[i].startPunchTime!=null){
 					upAttn=(response.rows[i].startPunchTime).substring(11,16);
 				}
 				if(response.rows[i].endPunchTime!=null){
 					downAttn=(response.rows[i].endPunchTime).substring(11,16);
 				} 		
				html += "<td style='text-align:center;'>"+upAttn+"-"+downAttn+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a>&nbsp;<a onclick='getPassDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>同意</a>&nbsp;<a onclick='getRefuseDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>拒绝</a></td>";
				html += "</tr>";
			}
			$("#reportList6").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPageByName("commonPage6",response,page,"gotoPage6");
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}


//多选框 操作
function getBatchApproveDiv(type){
	if($("input[name='selectSingle']:checked").length<=0){
		if(type==1){
			JEND.page.showError('请选择需要审批的异常考勤单！');
			return;
		}else{
			JEND.page.showError('请选择需要审批的下属异常考勤单！');
			return;
		}
	}
	$("#fastApproveDiv").show();
}
//审批 确认
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
//多选框 单点 审批 确认
function completeLeave(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processIds:processInstanceIds,comment:comment,commentType:commentType},
 		url : contextPath + "/empApplicationAbnormalAttendance/completeAttn.htm",
 		success : function(response) {
 			if(response.sucess){
 				$(".box-icon").removeClass("tishi");
 				$(".box-icon").addClass("yes");
 				$("#messageBox #messageContent").text("审批成功");
 				$("#messageBox").show();
 			}else{
 				$(".box-icon").removeClass("yes");
 				$(".box-icon").addClass("tishi");
 				$("#messageBox #messageContent").text(response.msg);
 				$("#messageBox").show();
 			}
 			$("#attnRefuseDivForm").find("textarea[name='approvalReason']").val("");
 			$("#passDivForm").find("textarea[name='approvalReason']").val("");
			DownAttnPage(1);
			gotoPage(1);	
		},
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
			pageLoading(false);//关闭动画
        }
 	});
}
//关闭窗口操作
function closeDiv(divName){
	$("#fastApproveDiv").hide();
	$("#passDiv").hide();
	$("#refuseDiv").hide();
	$("#detailDiv").hide();
	$("#"+divName).hide();
}
//单点 同意
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

function attnRefuse(){
	var processInstanceIds = $("#attnRefuseDivForm").find("input[name='processInstanceId']").val();
	var commentType = "refuse";
	var comment = $("#attnRefuseDivForm").find("textarea[name='approvalReason']").val();
	completeLeave(processInstanceIds,comment,commentType);
	$("#attnRefuseDivForm").find("textarea[name='approvalReason']").val("");
}


//单点 拒绝
function getRefuseDiv(processInstanceId){
	$("#refuseDiv").show();
	$("#refuseDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}
function refuse(){
	var processInstanceIds = $("#refuseDivForm").find("input[name='processInstanceId']").val();
	var commentType = "refuse";
	var comment = $("#refuseDivForm").find("textarea[name='approvalReason']").val();
	completeLeave(processInstanceIds,comment,commentType);
	$("#refuseDivForm").find("textarea[name='approvalReason']").val("");
}

//详情
function getDetailDiv(processId){
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processId:processId},
 		url : contextPath + "/empApplicationAbnormalAttendance/getAttnDetail.htm",
 		success : function(response) {
 			if(response.sucess){
 				//申请人
 				$("#detailDiv1").html(response.attn.cnName);
 				//所属部门
 				$("#detailDiv2").html(response.attn.departName);
 				//申请时间
 				$("#detailDiv3").html(response.attn.submitDate);
 				//考勤日期
 				$("#detailDiv4").html(response.attn.abnormalDate);			
              	var upAttn='空卡';
              	var downAttn='空卡';
 				if(response.attn.startPunchTime!=null){
 					upAttn=(response.attn.startPunchTime).substring(11,16);
 				}
 				if(response.attn.endPunchTime!=null){
 					downAttn=(response.attn.endPunchTime).substring(11,16);
 				} 				
 				//打卡时间
 				$("#detailDiv5").html(upAttn+'-'+downAttn);
 				//申诉考勤时间
 				$("#detailDiv6").html((response.attn.startTime.substring(11,16)) + '-'+(response.attn.endTime.substring(11,16)));
 				//申诉理由
 				$("#detailDiv7").html(response.attn.reason);
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
 				
 				if(response.attn.approvalStatus!=100){
 					$("#detail").hide();
 				}else{
 					$("#detail").show();
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
 				
 			}else{
 				JEND.page.showError(response.msg);
 			}
		},
		complete:function(XMLHttpRequest,textStatus){  
			
        }
 	});
}
//全选
function selectAll(allName,singleName){
	$("input[name='"+allName+"']").click(function(){
		 if($(this).prop("checked") == true){
			 $("input[name='"+singleName+"']").attr("checked",true);
		 }else{
			 $("input[name='"+singleName+"']").attr("checked",false);
		 }
	});
	$("input[name='"+singleName+"']").click(function(){
		var i = 0;
		$("input[name='"+singleName+"']").each(function(){
			if($(this).prop("checked") == true){
				i = i+1;
			}else{
				i = i-1;
			}
		});
		if(i !=0 && i == $("input[name='"+singleName+"']").length){
			$("input[name='"+allName+"']").attr("checked",true);
		}else{
			$("input[name='"+allName+"']").attr("checked",false);
		}
	});
};