$(function(){
	getFirstDepart();
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	$("#export").click(function() {
		var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
		window.location.href = basePath + "empApplicationOvertime/exportReport.htm?"+data;
	});
	$("#export").click(function(){
		window.location.href = basePath + "removeSubordinateAbsence/exportReport.htm?"+$("#queryform").serialize() ;
	})
	//页面大tab布局
	$(".jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		$(obj).click(function(){
			$(".workType").val("");
			$(".applyStartDate").val("");
			$(".applyEndDate").val("");
			$(".firstDepart").val("");
			$(".approvalStatus").val("");
			
			$("input[name='code']").val("");
			$("input[name='cnName']").val("");
			$("input[name='applyName']").val("");
			$("input[name='agentName']").val("");
			if(i==0){
				$(".first").show();
				$("#commonPage").show();
				gotoPage(1);
			}
		})
	});
});

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
		url:  basePath + "removeSubordinateAbsence/getReportPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].empCode)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].empName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaderName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].overHoursOfAttendance)+"</td>";
				html += "<td style='text-align:center;'>"+(getValByUndefined(response.rows[i].yesterdayOffTime)==""?"空卡":response.rows[i].yesterdayOffTime)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].attendanceDate)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].attendanceHour)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].removeAbsenceHours)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].removeAbsenceReason)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].auditUser)+"</td>";
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
				html += "<td style='text-align:center;'>"+approvalStatus+"</td>";
				if(response.rows[i].approalStatus==500){
					html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processinstanceId+");'>详情</a>&nbsp;&nbsp;<a id="+response.rows[i].processinstanceId+" onclick='getPassDiv("+response.rows[i].processinstanceId+");' style='color:blue;' href='#'>失效同意</a>&nbsp;&nbsp;<a id="+response.rows[i].processinstanceId+" onclick='getAttnRefuseDiv("+response.rows[i].processinstanceId+");' style='color:blue;' href='#'>失效拒绝</a></td>";				
				}else{
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processinstanceId+");' style='color:blue;' href='#'>详情</a></td>";
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
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
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
//关闭窗口操作
function closeDiv(){
	$("#fastApproveDiv").hide();
	$("#passDiv").hide();
	$("#attnRefuseDiv").hide();
	$("#refuseDiv").hide();
	$("#detailDiv").hide();
	
	$("#getButton").show();
}

function getPassDiv(processInstanceId){
	$("#passDiv").show();
	$("#passDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}
function getAttnRefuseDiv(processInstanceId){
	$("#attnRefuseDiv").show();
	$("#attnRefuseDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}

function pass(){
	var processInstanceIds = $("#passDivForm").find("input[name='processInstanceId']").val();
	var commentType = "overduePass";
	var comment = $("#passDivForm").find("textarea[name='approvalReason']").val();
	completePass(processInstanceIds,comment,commentType);
}

function attnRefuse(){
	var processInstanceIds = $("#attnRefuseDivForm").find("input[name='processInstanceId']").val();
	var commentType = "overdueRefuse";
	var comment = $("#attnRefuseDivForm").find("textarea[name='approvalReason']").val();
	completeRefuse(processInstanceIds,comment,commentType);
}


function completePass(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processIds:processInstanceIds,comment:comment,commentType:commentType},
 		url : contextPath + "/removeSubordinateAbsence/completePass.htm",
 		success : function(response) {
 			if(response.sucess){
 				gotoPage($("#pageNo").val());
 				JEND.page.alert({type:"success", message:response.msg});
 			}else{
 				JEND.page.showError(response.msg);
 			}
 			$("#passDivForm").find("textarea[name='approvalReason']").val("");
 			$("#attnRefuseDivForm").find("textarea[name='approvalReason']").val("");
 			pageLoading(false);//关闭动画
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

function completeRefuse(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processIds:processInstanceIds,comment:comment,commentType:commentType},
 		url : contextPath + "/removeSubordinateAbsence/completeRefuse.htm",
 		success : function(response) {
 			if(response.sucess){
 				gotoPage($("#pageNo").val());
 				JEND.page.alert({type:"success", message:response.msg});
 			}else{
 				JEND.page.showError(response.msg);
 			}
 			$("#passDivForm").find("textarea[name='approvalReason']").val("");
 			$("#attnRefuseDivForm").find("textarea[name='approvalReason']").val("");
 			pageLoading(false);//关闭动画
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