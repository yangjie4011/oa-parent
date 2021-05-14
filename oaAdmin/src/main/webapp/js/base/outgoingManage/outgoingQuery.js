$(function(){
	//查询按钮点击事件
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	
	//导出点击事件
	$("#export").click(function(){
		exportReport();
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
		setLastWeek("startDate","endDate", 'yyyy-MM-dd');
	});
	//本月
	$("#thisMonth").click(function(){
		setCurrentMonthTime("startDate","endDate", 'yyyy-MM-dd');
	});
	//上月
	$("#lastMonth").click(function(){
		setLastMonthTime("startDate","endDate", 'yyyy-MM-dd');
	});
});

//导出查询报表
function exportReport(){
	var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
	
	window.location.href = basePath + "/empApplicationOutgoing/exportReport.htm?" + data;
}

//分页查询外出报表
function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	if(!isEmpty($("#startDate").val())){
		var startTime = $("#startDate").val() + " 00:00:00";
		$("#startTime").val(startTime);
	}
	if(!isEmpty($("#endDate").val())){
		var endTime = $("#endDate").val() + " 00:00:00";
		$("#endTime").val(endTime);
	}
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=10";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationOutgoing/getReportPageList.htm",
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
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].duration+"小时</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].address+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].submitDate+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].auditUser+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].approvalStatusDesc+"</td>";
				if(response.rows[i].approvalStatus==500){
					html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='getPassDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>失效同意</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='getRefuseDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>失效拒绝</a></td>";				
				}else{
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>详情</a></td>";
				}
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			$("#startTime").val("");
			$("#endTime").val("");
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
	
	
}
function getDetailDiv(processInstanceId){
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processInstanceId:processInstanceId},
 		url : contextPath + "/empApplicationOutgoing/getOutgoingDetail.htm",
 		success : function(response) {
 			if(response.success){
 				//申请人
 				$("#empCnName").html("");
 				$("#empCnName").html(response.outgoingDetail.cnName);
 				//所属部门
 				$("#empDepart").html("");
 				$("#empDepart").html(response.outgoingDetail.departName);
 				//申请时间
 				$("#submitDate").html("");
 				$("#submitDate").html(response.outgoingDetail.submitDate);
 				//外出日期
 				$("#outDate").html("");
 				$("#outDate").html(response.outgoingDetail.outDate);
 				//外出时间
 				$("#outTime").html("");
 				var startTime = response.outgoingDetail.startTime;
 				var startHour = startTime.substring(11,16);
 				var endTime = response.outgoingDetail.endTime;
 				var endHour = endTime.substring(11,16);
 				var outTime = startHour+"-"+endHour;
 				$("#outTime").html(outTime);
 				//外出时长
 				$("#duration").html("");
 				$("#duration").html(response.outgoingDetail.duration+"小时");
 				//外出地点
 				$("#address").html("");
 				$("#address").html(response.outgoingDetail.address);
 				//外出理由
 				$("#reason").html("");
 				$("#reason").html(response.outgoingDetail.reason);
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
 				$("#detailDiv").show();
 			}else{
 				JEND.page.showError(response.msg);
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
//获取通过确认弹框
function getPassDiv(processInstanceId){
	$("#passDiv").show();
	$("#passDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}

//审核通过
function pass(){
	var processInstanceIds = $("#passDivForm").find("input[name='processInstanceId']").val();
	var commentType = "overduePass";
	var comment = $("#passDivForm").find("textarea[name='approvalReason']").val();
	invalidPass(processInstanceIds,comment,commentType);
	$("#passDivForm").find("textarea[name='approvalReason']").val("");
	backWaitList();
}

//获取拒绝弹框
function getRefuseDiv(processInstanceId){
	$("#refuseDiv").show();
	$("#refuseDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}
//审核拒绝
function refuse(){
	var processInstanceIds = $("#refuseDivForm").find("input[name='processInstanceId']").val();
	var commentType = "overdueRefuse";
	var comment = $("#refuseDivForm").find("textarea[name='approvalReason']").val();
	invalidRefuse(processInstanceIds,comment,commentType);
	$("#refuseDivForm").find("textarea[name='approvalReason']").val("");
	backWaitList();
}

function invalidPass(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processId:processInstanceIds,comment:comment,commentType:commentType},
		url : contextPath + "/empApplicationOutgoing/invalidPass.htm",
 		success : function(response) {
 			pageLoading(false);//关闭动画
 			if(response.sucess){
 				gotoPage($("#pageNo").val());
 				JEND.page.alert({type:"success", message:response.msg});
 			}else{
 				JEND.page.showError(response.msg);
 			}
 			closeDiv();
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
 	});
}

function invalidRefuse(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processId:processInstanceIds,comment:comment,commentType:commentType},
		url : contextPath + "/empApplicationOutgoing/invalidRefuse.htm",
 		success : function(response) {
 			pageLoading(false);//关闭动画
 			if(response.sucess){
 				gotoPage($("#pageNo").val());
 				JEND.page.alert({type:"success", message:response.msg});
 			}else{
 				JEND.page.showError(response.msg);
 			}
 			closeDiv();
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
 	});
}
function isEmpty(obj){
    if(typeof obj == "undefined" || obj == null || obj == ""){
        return true;
    }else{
        return false;
    }
}
function closeDiv(){
	$("#passDiv").hide();
	$("#refuseDiv").hide();
	$("#detailDiv").hide();
}