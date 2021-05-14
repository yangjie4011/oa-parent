var tabType = 0;//标记是在哪个Tab
$(function(){
	getFirstDepart();
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
});

//分页查询待办列表
function gotoPage(page){
	$("input[name='selectAll']").prop("checked",false);
	pageLoading(true);//加载动画
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=15";
	var url = "removeSubordinateAbsence/getPendingListByPage.htm";
	if(tabType==0){
		$(".showApproveDiv").show();
		$("#auditUserTd").hide();
		url = "removeSubordinateAbsence/getPendingListByPage.htm";
	}else if(tabType==1){
		$(".showApproveDiv").hide();
		$("#auditUserTd").show();
		url = "removeSubordinateAbsence/getDoneListByPage.htm";
	}else if(tabType==2){
		$(".showApproveDiv").hide();
		$("#auditUserTd").show();
		url = "removeSubordinateAbsence/getInvalidListByPage.htm";
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
					html += "<td style='text-align:center;'><input processInstanceId="+response.rows[i].processinstanceId+" type='checkbox' name='selectSingle' class='selectSingle'/></td>";
				}
				html += "<td style='text-align:center;'>"+response.rows[i].empCode+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].empName+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaderName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].overHoursOfAttendance)+"</td>";
				html += "<td style='text-align:center;'>"+(getValByUndefined(response.rows[i].yesterdayOffTime)==""?"空卡":response.rows[i].yesterdayOffTime)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].attendanceDate)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].attendanceHour)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].removeAbsenceHours)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].removeAbsenceReason)+"</td>";
				if(tabType!=0){
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].auditUser)+"</td>";	
				}
				var approvalStatus = "";
				if(response.rows[i].approalStatus==100){
					approvalStatus = "待审批";
				}else if(response.rows[i].approalStatus==200){
					approvalStatus = "已审批";
				}else if(response.rows[i].approalStatus==300){
					approvalStatus = "已拒绝";
				}else if(response.rows[i].approalStatus==400){
					approvalStatus = "已撤销";
				}else if(response.rows[i].approalStatus==500){
					approvalStatus = "已失效";
				}else if(response.rows[i].approalStatus==600){
					approvalStatus = "失效同意";
				}else if(response.rows[i].approalStatus==700){
					approvalStatus = "失效拒绝";
				}
				html += "<td style='text-align:center;'>"+getValByUndefined(approvalStatus)+"</td>";
				if(tabType==0){
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processinstanceId+");' style='color:blue;' href='#'>详情</a>&nbsp;&nbsp;<a onclick='getPassDiv(this);'  processInstanceId="+response.rows[i].processinstanceId+" style='color:blue;' href='#'>同意</a>&nbsp;&nbsp;<a onclick='getRefuseDiv(this);' style='color:blue;'  processInstanceId="+response.rows[i].processinstanceId+" href='#'>拒绝</a></td>";
				}else{
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processinstanceId+");' style='color:blue;' href='#'>详情</a></td>";
				}
				html += "</tr>";
			}
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
function getPassDiv(obj){
	$("#passDiv").show();
	$("#passDivForm").find("input[name='processInstanceId']").val($(obj).attr("processinstanceId"));
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
function getRefuseDiv(obj){
	$("#refuseDiv").show();
	$("#refuseDivForm").find("input[name='processInstanceId']").val($(obj).attr("processinstanceId"));
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
		url : contextPath + "/removeSubordinateAbsence/completeTask.htm",
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
			//closeDiv();
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
 		url : contextPath + "/removeSubordinateAbsence/completeTask.htm",
 		success : function(response) {
			pageLoading(false);//关闭动画
 			if(response.sucess){
 				$(".box-icon").removeClass("tishi");
 				$(".box-icon").addClass("yes");
 				JEND.page.alert({type:"success", message:response.msg});
 			}else{
 				$(".box-icon").removeClass("yes");
 				$(".box-icon").addClass("tishi");
 				JEND.page.showError(response.msg);
 			}
 			$("#fastApproveForm").find("textarea[name='approvalReason']").val("");
 			$("#passDivForm").find("textarea[name='approvalReason']").val("");
 			$("#refuseDivForm").find("textarea[name='approvalReason']").val("");
			gotoPage(1);	
			closeDiv();
		},
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
        }
 	});
}
//详情
function getDetailDiv(processId){
	$.ajax({
		async : true,
		type : "post",
		dataType:"json",
		data : {processId:processId},
		url : contextPath + "/removeSubordinateAbsence/getProcessbyId.htm",
		success : function(response) {
			if(response.sucess){
				$("#getButton").hide();
				//申请人
				$("#detailDiv1").html(response.subAttn.empName);
				//所属部门
				$("#detailDiv2").html(response.subAttn.leaderName);
				//申请时间
				$("#detailDiv3").html(response.subAttn.overHoursOfAttendance);
				//考勤日期
				$("#detailDiv4").html(((response.subAttn.yesterdayOffTime==null|| response.subAttn.yesterdayOffTime=='')?'空卡':response.subAttn.yesterdayOffTime));		
            				
				
				//打卡时间
				$("#detailDiv5").html(response.subAttn.attendanceDate);
				//申诉考勤时间
				$("#detailDiv6").html(response.subAttn.attendanceHour);
				$("#detailDiv7").html(response.subAttn.removeAbsenceHours);
				//申诉理由
				$("#detailDiv8").html(response.subAttn.removeAbsenceReason);
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
					}
					
					
					else if(response.flows[i].statu==0){
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
				if(response.subAttn.approalStatus!=100){
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