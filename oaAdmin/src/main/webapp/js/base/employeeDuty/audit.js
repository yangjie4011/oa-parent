var tabType = 0;//标记是在哪个Tab
$(function(){
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
				$("#detailDiv").hide();//隐藏详情表格
				$("#query").unbind("click");
				$("#query").click(function(){
					gotoPage(1,tabType);
				});
				gotoPage(1);
			}else if(i==1){
				tabType = 1;
				$("#detailDiv").hide();//隐藏详情表格
				$("#query").unbind("click");
				$("#query").click(function(){
					gotoPage(1,tabType);
				});
				gotoPage(1);
			}else if(i==2){
				tabType = 2;
				$("#detailDiv").hide();//隐藏详情表格
				$("#query").unbind("click");
				$("#query").click(function(){
					gotoPage(1,tabType);
				});
				gotoPage(1);
			}
		})
	});
	
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化单据状态
	initApprovalStatus(currentCompanyId);
	//初始化部门
	getScheduleDepartList();
	//根据部门获取组别
	$("#firstDepart").change(function(){
		getGroupListByDepartId(this.value);
	});
	
	//本月
	$("#thisMonth").click(function(){
		$("#month").val(getMonthStartDate('yyyy-MM'));
	});
	//上月
	$("#lastMonth").click(function(){
		$("#month").val(getLastMonthStartDate('yyyy-MM'));
	});
});

//分页查询待办列表
function gotoPage(page,type){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=15";
	var url = "duty/getHandlingListByPage.htm";
	if(tabType==0){
		url = "duty/getHandlingListByPage.htm";
	}else if(tabType==1){
		url = "duty/getHandledListByPage.htm";
	}else if(tabType==2){
		url = "duty/getInvalidListByPage.htm";
	}
	$("#showTh").hide();
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
				html += "<td style='text-align:center;'>"+response.rows[i].year+"</td>";//部门
				html += "<td style='text-align:center;'>"+response.rows[i].vacationName+"</td>";//节假日
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";//部门
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].classSettingPerson)+"</td>";//排班人
				html += "<td style='text-align:center;'>"+response.rows[i].submitDate+"</td>";//申请日期
				var isMove = "";
				if(response.rows[i].type==0){
					isMove = "值班安排";
				}else if(response.rows[i].type==1){
					isMove = "值班调班";
				}else if(response.rows[i].type==2){
					isMove = "人事调班";
				}
				html += "<td style='text-align:center;'>"+isMove+"</td>";//类型
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].dutyNum)+"</td>";//排班人数
				if(tabType==1){
					$("#showTh").show();
					
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].hrAuditor)+"</td>";//排班批核人
				}
				var approvalStatus = "";
				if(response.rows[i].approvalStatus==100){
					approvalStatus = "待审批";
				}else if(response.rows[i].approvalStatus==200){
					approvalStatus = "已完成";
				}else if(response.rows[i].approvalStatus==300){
					approvalStatus = "已拒绝";
				}else if(response.rows[i].approvalStatus==400){
					approvalStatus = "已撤销";
				}else if(response.rows[i].approvalStatus==500){
					approvalStatus = "已失效";
				}
				html += "<td style='text-align:center;'>"+approvalStatus+"</td>";
				if(tabType==0){
					html += "<td style='text-align:center;'><a onclick='showDetail("+response.rows[i].id+","+response.rows[i].processInstanceId+","+tabType+");' style='color:blue;' href='#'>审核</a></td>";
				}else{
					html += "<td style='text-align:center;'><a onclick='showDetail("+response.rows[i].id+",null,"+tabType+");' style='color:blue;' href='#'>查看</a></td>";
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
			pageLoading(false);//关闭动画
        }
	});
}

//获取排班明细表格
function showDetail(id,processId,type){
	//查询排班明细生成表格
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{id:id},
		url:contextPath + "/duty/getDetailById.htm",
		success:function(data){
			$("#detailList").find("thead").html("");
			$("#detailList").find("tbody").html("");
			$("#dutyMoveList").find("thead").html("");
			$("#dutyMoveList").find("tbody").html("");
			$("#isMoveOne").text("");
			$("#isMoveTwo").text("");
			//标题
			$("#detailTitle").empty();
			$("#detailTitle").html("<strong>"+data.title+"</strong>");
			//申请日期
			$("#companyName").text(data.companyName);
			//部门
			$("#applyDepartName").text(data.classDetail[0].departName);
			//状态
			$("#approvalStatusNames").text(data.approvalStatus);
			//加载标题
			var head = "<tr>";
			for(var i=0;i<data.weekDays.length;i++){
				
				if(data.weekDays[i]=="时间要求"){
					head +="<th colspan='2' style='width:80px;'>"+data.weekDays[i]+"</th>";
				}else{
					head +="<th style='overflow-x:auto;width:10px;text-align:center;'>"+data.weekDays[i]+"</th>";
				}
				
			}
			if(data.dutyMoveList.length>0){
				$("#dutyMoveList").find("thead").append(head);
				$("#isMoveOne").text("调整后:");
				$("#isMoveTwo").text("调整前:");
			}
			
			head += "</tr>";
			$("#detailList").find("thead").append(head);
		   
			for(i=0;i<data.classDetail.length;i++){
				var classDetail = "<tr employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].vacationDate+">";
				classDetail +="<td style='text-align:center;'>"+getValByUndefined(data.classDetail[i].vacationDate.substring(5,7)+"月"+data.classDetail[i].vacationDate.substring(8,10)+"日")+"</td>";
				classDetail +="<td style='text-align:center;'>星期"+getValByUndefined(data.classDetail[i].weekStr)+"</td>";
				classDetail +="<td style='text-align:center;'>"+getValByUndefined(data.classDetail[i].codes)+"</td>";
				classDetail +="<td style='text-align:center;'>"+getValByUndefined(data.classDetail[i].names)+"</td>";
				
				classDetail +="<td style='text-align:center;'>From:"+getValByUndefined(data.classDetail[i].startTime.substring(10,16))+"</td>";
				classDetail +="<td style='text-align:center;'>To:"+getValByUndefined(data.classDetail[i].endTime.substring(10,16))+"</td>";

				classDetail += "<td style='text-align:center;'>"+getValByUndefined(data.classDetail[i].workHours)+"</td>";
			
				classDetail +="<td style='text-align:center;'>"+getValByUndefined(data.classDetail[i].dutyItem)+"</td>";
				classDetail +="<td style='text-align:center;'>"+getValByUndefined(data.classDetail[i].remarks)+"</td>";
				classDetail += "</tr>";
				$("#detailList").find("tbody").append(classDetail);
			}
			
			//值班之前的数据
			for(i=0;i<data.dutyMoveList.length;i++){
				var dutyMoveList = "<tr employName="+data.dutyMoveList[i].employ_name+" employId="+data.dutyMoveList[i].vacationDate+">";
				dutyMoveList +="<td style='text-align:center;'>"+getValByUndefined(data.dutyMoveList[i].vacationDate.substring(5,7)+"月"+data.dutyMoveList[i].vacationDate.substring(8,10)+"日")+"</td>";
				dutyMoveList +="<td style='text-align:center;'>星期"+getValByUndefined(data.dutyMoveList[i].weekStr)+"</td>";
				dutyMoveList +="<td style='text-align:center;'>"+getValByUndefined(data.dutyMoveList[i].codes)+"</td>";
				dutyMoveList +="<td style='text-align:center;'>"+getValByUndefined(data.dutyMoveList[i].names)+"</td>";
				
				dutyMoveList +="<td style='text-align:center;'>From:"+getValByUndefined(data.dutyMoveList[i].startTime.substring(10,16))+"</td>";
				dutyMoveList +="<td style='text-align:center;'>To:"+getValByUndefined(data.dutyMoveList[i].endTime.substring(10,16))+"</td>";

				dutyMoveList += "<td style='text-align:center;'>"+getValByUndefined(data.dutyMoveList[i].workHours)+"</td>";
			
				dutyMoveList +="<td style='text-align:center;'>"+getValByUndefined(data.dutyMoveList[i].dutyItem)+"</td>";
				dutyMoveList +="<td style='text-align:center;'>"+getValByUndefined(data.dutyMoveList[i].remarks)+"</td>";
				dutyMoveList += "</tr>";
				$("#dutyMoveList").find("tbody").append(dutyMoveList);
			}
			
			
			
			
			$(".tableDiv").hide();//隐藏待办列表
			$("#commonPage").hide();//隐藏分页控件
			$(".buttonDiv").empty();
			var html = "";
			if(type==0){
				html += "<button processId="+processId+" onclick='getPassDiv(this);' class='btn-green btn-large'><span><i class='icon'></i>审核通过</span></button>";
				html += "<button processId="+processId+" onclick='getRefuseDiv(this);' class='btn-red btn-large'><span><i class='icon'></i>审核拒绝</span></button> ";
				html += "<button onclick='backWaitList();' class='btn-blue btn-large'><span><i class='icon'></i>取消</span></button>"; 
			}else{
				html += "<button  id="+id+" onclick='exportDetailById(this);' class='btn-green btn-large'><span><i class='icon'></i>导出</span></button>";
				//html += "<button vacationName="+data.classDetail[0].vacationName+" year="+data.classDetail[0].year+" departId="+data.classDetail[0].departId+" id="+id+" onclick='exportDetailById(this);' class='btn-green btn-large'><span><i class='icon'></i>导出</span></button>";
				html += "<button onclick='backWaitList();' class='btn-blue btn-large'><span><i class='icon'></i>返回</span></button>"; 
			}
			$(".buttonDiv").append(html);
			$("#detailDiv").show();//显示详情表格
		}
	});
	
}

//获取通过确认弹框
function getPassDiv(obj){
	$("#passDiv").show();
	$("#passDivForm").find("input[name='processInstanceId']").val($(obj).attr("processId"));
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
	$("#refuseDivForm").find("input[name='processInstanceId']").val($(obj).attr("processId"));
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
 		url : contextPath + "/duty/completeTask.htm",
 		success : function(response) {
 			if(response.success){
 				gotoPage($("#pageNo").val());
 			}else{
 				JEND.page.showError(response.message);
 			}
		},
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
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
}


function closeInfoDiv(){
	$("#settingInfo").hide();
}

//导出
function exportDetailById(obj){
	window.location.href = basePath + "duty/exportDetailById.htm?id="+$(obj).attr("id");

	//window.location.href = basePath + "duty/exportDetailById.htm?vacationName="+$(obj).attr("vacationName")+"&year="+$(obj).attr("year")+"&departId="+$(obj).attr("departId");
}