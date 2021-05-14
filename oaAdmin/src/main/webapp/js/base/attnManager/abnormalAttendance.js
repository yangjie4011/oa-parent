$(function(){
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	$("#query1").click(function(){
		gotoPage1(1);
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
	
	//初始化员工类型
	initEmployeeType(currentCompanyId);
	//初始化是否排班
	initWhetherScheduling();

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
				$(".second").hide();
				$("#commonPage1").hide();	
				gotoPage(1);
				
			}else if(i==1){
				$(".first").hide();
				$("#commonPage").hide();
				$(".second").show();
				$("#commonPage1").show();				
				$("#flagTab").val(1);
				gotoPage1(1);
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
		url:  basePath + "empApplicationAbnormalAttendance/getReportPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";

				html += "<td style='text-align:center;'>"+response.rows[i].workType1+"</td>";

				html += "<td style='text-align:center;'>"+response.rows[i].abnormalDate+"</td>";
				if(response.rows[i].startPunchTime==null){
					html += "<td style='text-align:center;'>&nbsp;</td>";
				}else{
					html += "<td style='text-align:center;'>"+response.rows[i].startPunchTime+"</td>";
				}
				if(response.rows[i].endPunchTime==null){
					html += "<td style='text-align:center;'>&nbsp;</td>";
				}else{
					html += "<td style='text-align:center;'>"+response.rows[i].endPunchTime+"</td>";
				}
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].reason)+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].submitDate+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].auditUser)+"</td>";
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
					html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='getPassDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>失效同意</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='getAttnRefuseDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>失效拒绝</a></td>";
				}else{
					html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a></td>";
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

function gotoPage1(page){
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
		url:  basePath + "empApplicationAbnormalAttendance/getSubReportPageList.htm",
		success:function(response){
			$("#reportList1").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].workType1+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].agentName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].abnormalDate+"</td>";				
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].startPunchTime)+"</td>";		
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].endPunchTime)+"</td>";			
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].reason)+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].submitDate+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].hrAuditUser)+"</td>";
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
					html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='getPassDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>失效同意</a>&nbsp;&nbsp;<a id="+response.rows[i].processInstanceId+" onclick='getAttnRefuseDiv("+response.rows[i].processInstanceId+");' style='color:blue;' href='#'>失效拒绝</a></td>";
				}else{
					html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getDetailDiv("+response.rows[i].processInstanceId+");'>详情</a></td>";
				}
				html += "</tr>";
			}
			$("#reportList1").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPageByName("commonPage1",response,page,"gotoPage1");
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
	var comment = $("#passDivForm").find("textarea[name='approvalReason']").val();
	passInvalidAbnormalAttendance(processInstanceIds,comment);
}

function attnRefuse(){
	var processInstanceIds = $("#attnRefuseDivForm").find("input[name='processInstanceId']").val();
	var comment = $("#attnRefuseDivForm").find("textarea[name='approvalReason']").val();
	refuseInvalidAbnormalAttendance(processInstanceIds,comment);
}


function passInvalidAbnormalAttendance(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processId:processInstanceIds,comment:comment},
 		url : contextPath + "/empApplicationAbnormalAttendance/passInvalidAbnormalAttendance.htm",
 		success : function(response) {
 			if(response.result.flag){
 				gotoPage($("#pageNo").val());
 			}else{
 				JEND.page.showError(response.msg);
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

function refuseInvalidAbnormalAttendance(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processId:processInstanceIds,comment:comment},
 		url : contextPath + "/empApplicationAbnormalAttendance/refuseInvalidAbnormalAttendance.htm",
 		success : function(response) {
 			if(response.result.flag){
 				gotoPage($("#pageNo").val());
 			}else{
 				JEND.page.showError(response.msg);
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