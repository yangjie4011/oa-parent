var tabType = 0;//标记是在哪个Tab
$(function(){
	
	tabType = 0;
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	$(".jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		$(obj).click(function(){
			$(".tableDiv").show();
			$("#pageNo").val(1);
			if(i==0){
				tabType = 0;
			}else if(i==1){
				tabType = 1;
			}else if(i==2){
				tabType = 2;
			}
			gotoPage(1);
		})
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

//分页查询待办列表
function gotoPage(page){
	$("input[name='selectAll']").prop("checked",false);
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
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=15";
	var url = "empApplicationOutgoing/getPendingListByPage.htm";
	if(tabType==0){
		$(".showApproveDiv").show();
		$("#auditUserTd").hide();
		url = "empApplicationOutgoing/getApprovePageList.htm";
	}else if(tabType==1){
		$(".showApproveDiv").hide();
		$("#auditUserTd").show();
		url = "empApplicationOutgoing/getAuditedPageList.htm";
	}else if(tabType==2){
		$(".showApproveDiv").hide();
		$("#auditUserTd").show();
		url = "empApplicationOutgoing/getInvalidPageList.htm";
	}
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + url,
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				if(tabType==0){
					html += "<td style='text-align:center;'><input processInstanceId="+response.rows[i].processInstanceId+" type='checkbox' name='selectSingle' class='selectSingle'/></td>";
				}
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].positionName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].workType+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].duration+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].address+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].submitDate+"</td>";
				if(tabType!=0){
					html += "<td style='text-align:center;'>"+response.rows[i].auditUser+"</td>";	
				}
				html += "<td style='text-align:center;'>"+response.rows[i].approvalStatusDesc+"</td>";
				if(tabType==0){
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>详情</a>&nbsp;&nbsp;<a onclick='getPassDiv("+response.rows[i].processInstanceId+");'style='color:blue;' href='#'>同意</a>&nbsp;&nbsp;<a onclick='getRefuseDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>拒绝</a></td>";
				}else{
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>详情</a></td>";
				}
				html += "</tr>";
			}
			$("#startTime").val("");
			$("#endTime").val("");
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			selectAll("selectAll","selectSingle");
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
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
	var commentType = "pass";
	var comment = $("#passDivForm").find("textarea[name='approvalReason']").val();
	completeTask(processInstanceIds,comment,commentType);
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
	var commentType = "refuse";
	var comment = $("#refuseDivForm").find("textarea[name='approvalReason']").val();
	completeTask(processInstanceIds,comment,commentType);
	$("#refuseDivForm").find("textarea[name='approvalReason']").val("");
	backWaitList();
}

function completeTask(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processIds:processInstanceIds,comment:comment,commentType:commentType},
		url : contextPath + "/empApplicationOutgoing/completeTask.htm",
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

//返回待办列表
function backWaitList(){
	$(".tableDiv").show();//显示待办列表
	$("#commonPage").show();//显示分页控件
	$("#detailDiv").hide();//隐藏详情表格
}

//关闭审核弹框
function closeDiv(obj){
	//$(obj).parents(".commonBox").hide();
	$("#passDiv").hide();$("#refuseDiv").hide();
	$("#detailDiv").hide();
	$("#fastApproveDiv").hide();
	$("#getButton").show();
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

//多选框 操作
function getBatchApproveDiv(){
	if($("input[name='selectSingle']:checked").length<=0){
		JEND.page.showError('请选择需要审批的消下属缺勤单！');
		return;
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
	completeTask(processInstanceIds,comment,commentType);
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
 				if(response.outgoingDetail.approvalStatus!=100){
 					$("#approvalDiv").hide();
 				}else{
 					$("#approvalDiv").show();
 				}
				$("#passDetail").unbind("click");
				$("#refuseDetail").unbind("click");
				$("#passDetail").bind("click", function(){
					getPassDiv(processInstanceId);
				});
				$("#refuseDetail").bind("click", function(){
              	getRefuseDiv(processInstanceId);
				});
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
function isEmpty(obj){
    if(typeof obj == "undefined" || obj == null || obj == ""){
        return true;
    }else{
        return false;
    }
}