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

//导出查询报表
function exportReport(){
	var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
	
	window.location.href = basePath + "/empApplicationBusiness/exportReport.htm?" + data;
}

//分页查询出差报表
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
		url:  basePath + "/empApplicationBusiness/getReportPageList.htm",
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
				if("200" == response.rows[i].approvalStatus){
					html += "<td style='text-align:center;'><a href="+basePath+"empApplicationBusiness/toView.htm?id="+response.rows[i].id+">查看</a></td>";
				}else{//只有单据出差申请单审批通过以后才会存在出差总结报告
					html += "<td style='text-align:center;'></td>";
				}
				html += "<td style='text-align:center;'>"+response.rows[i].submitDate+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].auditUser+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].approvalStatusDesc+"</td>";
				/*if(response.rows[i].approvalStatus==100){
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processinstanceId+");' style='color:blue;' href='#'>详情</a>&nbsp;&nbsp;<a onclick='getPassDiv("+response.rows[i].processinstanceId+");'  processInstanceId="+response.rows[i].processinstanceId+" style='color:blue;' href='#'>同意</a>&nbsp;&nbsp;<a onclick='getRefuseDiv("+response.rows[i].processinstanceId+");' style='color:blue;'  processInstanceId="+response.rows[i].processinstanceId+" href='#'>拒绝</a></td>";
				}else */
				if(response.rows[i].approvalStatus==500){
					html += "<td style='text-align:center;'><a onclick='getDetailDiv("+response.rows[i].processinstanceId+");' style='color:blue;' href='#'>详情</a>&nbsp;&nbsp;<a onclick='getPassAbateDiv("+response.rows[i].processinstanceId+");'  processInstanceId="+response.rows[i].processinstanceId+" style='color:blue;' href='#'>失效同意</a>&nbsp;&nbsp;<a onclick='getRefuseAbateDiv("+response.rows[i].processinstanceId+");' style='color:blue;'  processInstanceId="+response.rows[i].processinstanceId+" href='#'>失效拒绝</a></td>";
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


function closeDiv(){
	$("#fastApproveDiv").hide();
	$("#passDiv").hide();
	$("#refuseDiv").hide();
	$("#detailDiv").hide();
	$("#passAbateDiv").hide();
	$("#refuseAbateDiv").hide();
	
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
	$("#passAbateForm").find("textarea[name='approvalReason']").val("");
}

function getRefuseAbateDiv(processInstanceId){
	$("#refuseAbateDiv").show();
	$("#refuseAbateDivForm").find("input[name='processInstanceId']").val(processInstanceId);
}

function refuseAbate(){
	var processInstanceIds = $("#refuseAbateDivForm").find("input[name='processInstanceId']").val();
	var commentType = "overdueRefuse";
	var comment = $("#refuseAbateDivForm").find("textarea[name='approvalReason']").val();
	completeRefuseAbate(processInstanceIds,comment,commentType);
	$("#refuseAbateDivForm").find("textarea[name='approvalReason']").val("");
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
 				$("#passAbateForm").find("textarea[name='approvalReason']").val("");
 			}else{
 				gotoPage($("#pageNo").val());
 				JEND.page.showError(response.result.msg);
 			}
 			closeDiv();
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
 				$("#refuseAbateDivForm").find("textarea[name='approvalReason']").val("");
 			}else{
 				gotoPage($("#pageNo").val());
 				JEND.page.showError(response.result.msg);
 			}
 			closeDiv();
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
