var typeNum=0;
$(function(){
	//window.onload=areaSeled.init;
	gotoPage();
	$("#query").click(function(){
		gotoPage(1);
	});
	$("#son .jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		$(obj).click(function(){
			if(i==0){
				$(".showApproveDiv").show();
			}else if(i==1){
				$(".showApproveDiv").hide();
			}else if(i==2){
				$(".showApproveDiv").hide();
			}
			typeNum=i;
			gotoPage();
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
		setLastWeek("startTime","endTime", 'yyyy-MM-dd');
	});
	//本月
	$("#thisMonth").click(function(){
		setCurrentMonthTime("startTime","endTime", 'yyyy-MM-dd');
	});
	//上月
	$("#lastMonth").click(function(){
		setLastMonthTime("startTime","endTime", 'yyyy-MM-dd');
	});
});


function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=10"+ "&typeNum="+typeNum+"";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empApplicationBusiness/getApproveList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				if(typeNum==0){
					html += "<td style='text-align:center;'><input processInstanceId="+response.rows[i].processinstanceId+" type='checkbox' name='selectSingle' class='selectSingle'/></td>";
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
				//交通工具
				if("100" == response.rows[i].vehicle){
					html += "<td style='text-align:center;'>火车</td>";
				}else if("200" == response.rows[i].vehicle){
					html += "<td style='text-align:center;'>飞机</td>";
				}else if("300" == response.rows[i].vehicle){
					html += "<td style='text-align:center;'>汽车</td>";
				}else{
					html += "<td style='text-align:center;'></td>";
				}
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].submitDate+"</td>";
				
				if(typeNum==0){
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processinstanceId+");' style='color:blue;' href='#'>详情</a>&nbsp;&nbsp;<a onclick='getPassDiv("+response.rows[i].processinstanceId+");'  processInstanceId="+response.rows[i].processinstanceId+" style='color:blue;' href='#'>同意</a>&nbsp;&nbsp;<a onclick='getRefuseDiv("+response.rows[i].processinstanceId+");' style='color:blue;'  processInstanceId="+response.rows[i].processinstanceId+" href='#'>拒绝</a></td>";
				}else if(typeNum==1){
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processinstanceId+");' style='color:blue;' href='#'>详情</a></td>";
				}else if(typeNum==2){
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processinstanceId+");' style='color:blue;' href='#'>详情</a>&nbsp;&nbsp;<a onclick='getPassAbateDiv("+response.rows[i].processinstanceId+");'  processInstanceId="+response.rows[i].processinstanceId+" style='color:blue;' href='#'>失效同意</a>&nbsp;&nbsp;<a onclick='getRefuseAbateDiv("+response.rows[i].processinstanceId+");' style='color:blue;'  processInstanceId="+response.rows[i].processinstanceId+" href='#'>失效拒绝</a></td>";
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


function closeDiv(){
	$("#fastApproveDiv").hide();
	$("#passDiv").hide();
	$("#refuseDiv").hide();
	$("#detailDiv").hide();
	$("#passAbateDiv").hide();
	$("#refuseAbateDiv").hide();
	
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
	$("#passDivForm").find("textarea[name='approvalReason']").val("");
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
	$("#refuseDivForm").find("textarea[name='approvalReason']").val("");
}

function getPassAbateDiv(processInstanceId){
	$("#passAbateDiv").show();
	$("#passAbateForm").find("input[name='processInstanceId']").val(processInstanceId);
}

function passAbate(){
	var processInstanceIds = $("#passAbateForm").find("input[name='processInstanceId']").val();
	var commentType = "overduePass";
	var comment = $("#passAbateForm").find("textarea[name='approvalReason']").val();
	completePassAbate(processInstanceIds,comment,commentType);
	$("#passDivForm").find("textarea[name='approvalReason']").val("");
}

function getRefuseAbateDiv(processInstanceId){
	$("#refuseAbateDiv").show();
	$("#refuseAbateDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}

function refuseAbate(){
	var processInstanceIds = $("#refuseAbateDivForm").find("input[name='processInstanceId']").val();
	var commentType = "overdueRefuse";
	var comment = $("#refuseDivForm").find("textarea[name='approvalReason']").val();
	completeRefuseAbate(processInstanceIds,comment,commentType);
	$("#refuseDivForm").find("textarea[name='approvalReason']").val("");
}






function getDetailDiv(processId){
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processId:processId},
 		url : contextPath + "/empApplicationBusiness/getProcessbyId.htm",
 		success : function(response) {
 			if(response.success){
				$("#getButton").hide();
				//申请人
				$("#detailDiv1").html(response.empBusiness.cnName);
				//所属部门
				$("#detailDiv2").html(response.empBusiness.departName);
				//申请时间
				$("#detailDiv3").html(response.empBusiness.submitDate);
				//考勤日期
				$("#detailDiv4").html(response.empBusiness.startTime);		
            				
				
				//打卡时间
				$("#detailDiv5").html(response.empBusiness.endTime);
				//申诉考勤时间
				$("#detailDiv6").html(response.empBusiness.duration+"天");
				$("#detailDiv7").html(response.empBusiness.address);
				//申诉理由
				$("#detailDiv8").html(response.empBusiness.reason);
				var vehicleStr="";
				if(response.empBusiness.vehicle==100){
					vehicleStr="火车";
				}else if(response.empBusiness.vehicle==200){
					vehicleStr="飞机";
				}else if(response.empBusiness.vehicle==300){
					vehicleStr="汽车";
				}
				$("#detailDiv9").html(vehicleStr);
				
				var detail="";
				if(response.detailList!=null){
					
					for(var i=0;i<response.detailList.length;i++){
						detail+="<p style='width: 260px;'>"+response.detailList[i].workStartDate+"&nbsp至&nbsp"+response.detailList[i].workEndDate+"</p>";
						if(response.detailList[i].workPlan!=null && response.detailList[i].workPlan!=""){
							detail+="<ol><li style='width: 260px;'>"+response.detailList[i].workPlan+"</li></ol>";
						}
						if( response.detailList[i].workObjective!=null && response.detailList[i].workObjective!='' ){
							detail+="<ol style='border-top:rgb(242,242,242) 1px solid;'><li style='width: 260px;'>"+response.detailList[i].workObjective+"</li></<ol><br/>";
						}
					}
				}
 				$("#detailDiv10").html(detail);
				
				
				
				
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
				if(typeNum!=0){
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
		data : {processIds:processInstanceIds,comment:comment,commentType:commentType},
 		url : contextPath + "/empApplicationBusiness/completeTask.htm",
 		success : function(response) {
 			if(response.success){
 				gotoPage($("#pageNo").val());
 				JEND.page.alert({type:"success", message:response.msg});
 			}else{
 				gotoPage($("#pageNo").val());
 				JEND.page.showError(response.msg);
 			}
 			closeDiv();
		},
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
			pageLoading(false);//关闭动画
        }
 	});
}

function completePassAbate(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processIds:processInstanceIds,comment:comment,commentType:commentType},
 		url : contextPath + "/empApplicationBusiness/completePassAbate.htm",
 		success : function(response) {
 			if(response.success){
 				gotoPage($("#pageNo").val());
 				JEND.page.alert({type:"success", message:response.result.msg});
 			}else{
 				gotoPage($("#pageNo").val());
 				JEND.page.showError(response.result.msg);
 			}
 			closeDiv();
		},
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
			pageLoading(false);//关闭动画
        }
 	});
}

function completeRefuseAbate(processInstanceIds,comment,commentType){
	pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {processIds:processInstanceIds,comment:comment,commentType:commentType},
 		url : contextPath + "/empApplicationBusiness/completeRefuseAbate.htm",
 		success : function(response) {
 			if(response.success){
 				gotoPage($("#pageNo").val());
 				JEND.page.alert({type:"success", message:response.result.msg});
 			}else{
 				gotoPage($("#pageNo").val());
 				JEND.page.showError(response.result.msg);
 			}
 			closeDiv();
		},
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
			pageLoading(false);//关闭动画
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
