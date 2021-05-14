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
					gotoPage(1);
				});
				gotoPage(1);
			}else if(i==1){
				tabType = 1;
				$("#detailDiv").hide();//隐藏详情表格
				$("#query").unbind("click");
				$("#query").click(function(){
					gotoPage(1);
				});
				gotoPage(1);
			}else if(i==2){
				tabType = 2;
				$("#detailDiv").hide();//隐藏详情表格
				$("#query").unbind("click");
				$("#query").click(function(){
					gotoPage(1);
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
function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=15";
	var url = "employeeClass/getHandlingListByPage.htm";
	if(tabType==0){
		url = "employeeClass/getHandlingListByPage.htm";
	}else if(tabType==1){
		url = "employeeClass/getHandledListByPage.htm";
	}else if(tabType==2){
		url = "employeeClass/getInvalidListByPage.htm";
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
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";//部门
				html += "<td style='text-align:center;'>"+response.rows[i].groupName+"</td>";//组别
				html += "<td style='text-align:center;'>"+response.rows[i].classMonth+"</td>";//年月
				html += "<td style='text-align:center;'>"+response.rows[i].classSettingPerson+"</td>";//排班人
				html += "<td style='text-align:center;'>"+response.rows[i].createTime+"</td>";//申请日期
				var isMove = "";
				if(response.rows[i].isMove==0){
					isMove = "排班";
				}else if(response.rows[i].isMove==1){
					isMove = "调班";
				}
				html += "<td style='text-align:center;'>"+isMove+"</td>";//类型
				html += "<td style='text-align:center;'>"+response.rows[i].classEmployeeNum+"</td>";//排班人数
				html += "<td style='text-align:center;'>"+response.rows[i].shouldTime+"</td>";//应出勤工时
				html += "<td style='text-align:center;'>"+response.rows[i].auditorName+"</td>";//排班批核人
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
		url:contextPath + "/employeeClass/getDetailById.htm",
		success:function(data){
			$("#reportTitle").find("thead").html("");
			$("#reportTitle").find("tbody").html("");
			$("#reportContent").find("thead").html("");
			$("#reportContent").find("tbody").html("");
			//标题
			$("#detailTitle").empty();
			$("#detailTitle").html("<strong>"+data.title+"</strong>");
			//申请日期
			$("#applyDate").val(data.applyDate);
			//状态
			$("#approvalStatus").val(data.approvalStatus);
			//加载标题
			var headTitle = "<tr>";
			var headContent = "<tr>";
			for(var i=0;i<data.weekDays.length;i++){
				if(i<=4){
					headTitle +="<th style='overflow-x:auto;width:100px;text-align:center;'>"+data.weekDays[i]+"</th>";
				}else{
					headContent +="<th style='overflow-x:auto;width:10px;text-align:center;'>"+data.weekDays[i]+"</th>";
				}
			}
			headTitle += "</tr>";
			headContent += "</tr>";
			$("#reportTitle").find("thead").append(headTitle);
			$("#reportContent").find("thead").append(headContent);
			//天
			var daysTitle = "<tr>";
			var daysContent = "<tr>";
			for(var i=0;i<data.days.length;i++){
				if(i<=4){
					daysTitle +="<td style='text-align:center;'>&nbsp;</td>";
				}else{
					daysContent +="<td style='text-align:center;'>"+data.days[i]+"</td>";
				}
			}
			daysTitle += "</tr>";
			daysContent += "</tr>";
			$("#reportTitle").find("tbody").append(daysTitle);
			$("#reportContent").find("tbody").append(daysContent);
			//排班详情
			var m = data.days.length;
			var shuldTimeCount = 0;//应出勤统计
			var employeeCount = 0;//人数统计
			var daysCount = 0;//排班天数统计
			var mustAttnTimeCount  = 0;//排班工时统计
			for(i=0;i<data.classDetail.length;i++){
				var classDetail = "<tr employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].employ_id+">";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].positionName+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].employ_name+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].should_time+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].dayCount+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].must_attn_time+"</td>";
				var classDetail1 = "<tr>";
				shuldTimeCount = shuldTimeCount + data.classDetail[i].should_time;
				employeeCount = employeeCount +1;
				daysCount = daysCount + data.classDetail[i].dayCount;
				mustAttnTimeCount = mustAttnTimeCount + data.classDetail[i].must_attn_time;
				for(var j=1;j<=m-5;j++){
					if(typeof(data.classDetail[i][j])=="undefined"){
						classDetail1 +="<td style='text-align:center;'>&nbsp;</td>";
					}else{
						if(data.classDetail[i][j].split(",")[3]=="notMove"){
							//白,2019-03-25,1,notMove-->原班次信息，日期，原班次id，没有调班
							classDetail1 +="<td onclick='showSettingInfo(this);' employName="+data.classDetail[i].employ_name+" isMove='notMove' date="+data.classDetail[i][j].split(",")[1]+" setId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;cursor:pointer;'>"+data.classDetail[i][j].split(",")[0]+"</td>";
						}else if(data.classDetail[i][j].split(",")[3]=="move"){
							//休,2019-03-27,0,move,早,41-->原班次信息，日期，原班次id，调班,调整后班次信息,调整后班次id
							//休息的班次不存在，默认id=0，特殊处理
							classDetail1 +="<td onclick='showSettingInfo(this);' employName="+data.classDetail[i].employ_name+" isMove='move' date="+data.classDetail[i][j].split(",")[1]+" setId="+data.classDetail[i][j].split(",")[2]+" newSetId="+data.classDetail[i][j].split(",")[5]+" style='text-align:center;color:blue;cursor:pointer;'>"+data.classDetail[i][j].split(",")[4]+"</td>";
						}
					}
				}
				classDetail += "</tr>";
				classDetail1 += "</tr>";
				$("#reportTitle").find("tbody").append(classDetail);
				$("#reportContent").find("tbody").append(classDetail1);
			}
			var countsTitle = "<tr>";
			var countsContent = "<tr>";
			countsTitle +="<td>排班统计</td>";
			countsTitle +="<td>"+employeeCount+"</td>";
			countsTitle +="<td>"+shuldTimeCount+"</td>";
			countsTitle +="<td>"+daysCount+"</td>";
			countsTitle +="<td>"+mustAttnTimeCount+"</td>";
			for(var j=1;j<=m-5;j++){
				countsContent +="<td style='text-align:center;'>&nbsp;</td>";
			}
			countsTitle += "</tr>";
			countsContent += "</tr>";
			$("#reportTitle").find("tbody").append(countsTitle);
			$("#reportContent").find("tbody").append(countsContent);
			$(".tableDiv").hide();//隐藏待办列表
			$("#commonPage").hide();//隐藏分页控件
			$(".buttonDiv").empty();
			var html = "";
			if(type==0){
				html += "<button processId="+processId+" onclick='getPassDiv(this);' class='btn-green btn-large'><span><i class='icon'></i>审核通过</span></button>";
				html += "<button processId="+processId+" onclick='getRefuseDiv(this);' class='btn-red btn-large'><span><i class='icon'></i>审核拒绝</span></button> ";
				html += "<button onclick='backWaitList();' class='btn-blue btn-large'><span><i class='icon'></i>取消</span></button>"; 
			}else{
				html += "<button id="+id+" onclick='exportDetailById(this);' class='btn-green btn-large'><span><i class='icon'></i>导出</span></button>";
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
 		url : contextPath + "/employeeClass/completeTask.htm",
 		success : function(response) {
 			if(response.sucess){
 				gotoPage($("#pageNo").val());
 			}else{
 				JEND.page.showError(response.msg);
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

//显示某员工某天班次信息
function showSettingInfo(obj){
	var isMove = $(obj).attr("isMove");
	if(isMove=="notMove"){
		var param = {
			date:$(obj).attr("date"),
			setId:$(obj).attr("setId")
		};
	}else{
		var param = {
			date:$(obj).attr("date"),
			setId:$(obj).attr("setId"),
			newSetId:$(obj).attr("newSetId")
		};
	}
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:param,
		url:contextPath + "/employeeClass/getSettingInfo.htm",
		success:function(data){
			$(".settingInfo").empty();
			var html="<div class='title'><strong><i class='icon'></i>"+$(obj).attr("employName")+"</strong>";
			html +="<i onclick='closeInfoDiv();' class='mo-houtai-box-close' style='width: 25px;height: 30px;'></i></div>";
			html +="<div class='form-main'><div class='col'><div class='form'><label class='w'  style='width:130px;'>排班日期</label>";
			if(isMove=="move"){
				html +="<span>"+data.date+"</span></div></div><div class='col'><div class='form'><label class='w' style='width:130px;'>&nbsp;</label><span>"+data.weekDay+"</span></div></div><div class='col'><div class='form'><label class='w' style='width:130px;'>原排班班次</label>";
			}else{
				html +="<span>"+data.date+"</span></div></div><div class='col'><div class='form'><label class='w' style='width:130px;'>&nbsp;</label><span>"+data.weekDay+"</span></div></div><div class='col'><div class='form'><label class='w' style='width:130px;'>班次信息</label>";
			}
			if(data.setName=="休息"){
				html +="<span>"+data.setName+"</span></div></div>";
			}else{
				html +="<span>"+data.setName+"&nbsp;"+data.startTime+"-"+data.endTime+"</span></div></div>";
			}
			if(isMove=="move"){
				html +="<div class='col'><div class='form'><label class='w' style='width:130px;'>调整后班次</label>";
				if(data.newSetName=="休息"){
					html +="<span>"+data.newSetName+"</span></div></div>";
				}else{
					html +="<span>"+data.newSetName+"&nbsp;"+data.newStartTime+"-"+data.newEndTime+"</span></div></div>";
				}
			}
			html +="<div class='col'><div class='button-wrap ml-4 tiaob'></div></div></div>";
		    $(".settingInfo").append(html);
			$("#settingInfo").show();
		}
	});
	
}

function closeInfoDiv(){
	$("#settingInfo").hide();
}

//导出
function exportDetailById(obj){
	window.location.href = basePath + "employeeClass/exportDetailById.htm?id="+$(obj).attr("id");
}