$(function(){
	$("#queryDuty").click(function(){
		query();
	});
	var currentCompanyId = $("#currentCompanyId").val();
	
	//初始化部门
	getFirstDepart();
	
	$("#exports").click(function() {
		if($("#year").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("年份不能为空！");
			});
			return;
		}
		if($("#vacationName").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("法定节假日不能为空！");
			});
			return;
		}
		if($("#firstDepart").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("值班部门不能为空！");
			});
			return;
		}
		var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
		window.location.href = basePath + "duty/exportDuty.htm?"+data;
	});	
});

function query(){
	if($("#firstDepart").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("部门不能为空！");
		});
		return;
	}
	if($("#year").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("年份不能为空！");
		});
		return;
	}
	if($("#vacationName").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("法定节假日不能为空！");
		});
		return;
	}
	pageLoading(true);//开启动画
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{vacationName:$("#vacationName").val(),year:$("#year").val(),departId:$("#firstDepart").val()},
		url:contextPath + "/duty/queryDuty.htm",
		success:function(data){
			if(data.success){
				//值班
				$("#reportList1").empty();
				$("#detailDiv").empty();
				var dutyHtml = "";
				dutyHtml += "<tr>";
				dutyHtml += "<td style='text-align:center;'>"+data.duty.departName+"</td>";
				dutyHtml += "<td style='text-align:center;'>"+data.duty.year+"</td>";
				dutyHtml += "<td style='text-align:center;'>"+data.duty.vacationName+"</td>";
				dutyHtml += "<td style='text-align:center;'>"+data.duty.classSetPerson+"</td>";
				dutyHtml += "<td style='text-align:center;'>"+data.duty.applyDate+"</td>";
				dutyHtml += "<td style='text-align:center;'>"+data.duty.dutyNum+"</td>";
				dutyHtml += "<td style='text-align:center;'>"+data.duty.auditor+"</td>";
				dutyHtml += "<td style='text-align:center;'><a style='color:blue;' vacationName="+data.duty.vacationName+" year="+data.duty.year+" departId="+data.duty.departId+" href='#' onclick='showDutyDetail(this,0);'>查看</a></td></tr>";
				$("#reportList1").append(dutyHtml);
				$("#reportList2").empty();
				//流水
				var historyHtml = "";
				for(var i=0;i<data.historyList.length;i++){
					historyHtml += "<tr>";
					historyHtml += "<td style='text-align:center;'>"+data.historyList[i].id+"</td>";
					historyHtml += "<td style='text-align:center;'>"+data.historyList[i].departName+"</td>";
					historyHtml += "<td style='text-align:center;'>"+data.historyList[i].year+"</td>";
					historyHtml += "<td style='text-align:center;'>"+data.historyList[i].vacationName+"</td>";
					if(data.historyList[i].type==1){
						historyHtml += "<td style='text-align:center;'>值班调班</td>";
					}else{
						historyHtml += "<td style='text-align:center;'>人事调班</td>";
					}
					historyHtml += "<td style='text-align:center;'>"+data.historyList[i].createUser+"</td>";
					historyHtml += "<td style='text-align:center;'>"+data.historyList[i].createTime+"</td>";
					historyHtml += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='showHistoryDetail("+data.historyList[i].id+");'>查看</a></td></tr>";
				}
				$("#reportList2").append(historyHtml);
				$(".showTable").show();
			}else{
				JEND.page.alert({type:"error", message:data.message});
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

//查看值班详情
function showDutyDetail(obj,type){
	pageLoading(true);//开启动画
	var showObj = $(obj);
	var vacationName = $(obj).attr("vacationName");
	var year = $(obj).attr("year");
	var departId = $(obj).attr("departId");
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{vacationName:vacationName,year:year,departId:departId},
		url:contextPath + "/duty/showDutyDetail.htm",
		success:function(data){
			if(data.success){
				$("#detailDiv").empty();
				var detailDiv = "";
				detailDiv += "<div class='title' style='text-align:center;'><strong>"+data.title+"</strong></div>";
				detailDiv +="<div class='col'> <div class='form'><label class='w'>公司：</label><input type='text' class='form-text' style='border:0px;' value="+data.companyName+"></div>";
				detailDiv +="<div class='form'><label class='w'>部门：</label><input type='text' class='form-text' style='border:0px;' value="+data.departName+"></div></div><table border='1'><thead><tr>";
				//编辑
				if(type==1){
					detailDiv +="<th style='text-align:center;width:80px;'>&nbsp;</th>";
				}
				detailDiv +="<th style='text-align:center;width:80px;'>日期</th>";
				detailDiv += "<th style='text-align:center;width:80px;'>星期</th><th style='text-align:center;width:100px;'>员工编号</th><th style='text-align:center;width:100px;'>员工姓名</th><th style='text-align:center;width:200px;' colspan='2'>时间要求</th>";
				detailDiv +="<th style='text-align:center;width:100px;'>工作小时数</th><th style='text-align:center;width:200px;'>工作内容描述</th><th style='text-align:center;width:200px;'>备注</th></tr></thead><tbody>";
				for(var i=0;i<data.list.length;i++){
					detailDiv += "<tr source="+data.list[i].source+" id="+data.list[i].id+">";
					//编辑
					if(type==1){
						//排班申请生成的值班不允许直接修改
						if(data.list[i].source==1){
							detailDiv += '<td employId='+data.list[i].employId+' startTime='+data.list[i].startTime+' endTime='+data.list[i].endTime+' style="text-align:center;">&nbsp;</td>';
						}else{
							detailDiv += '<td employId='+data.list[i].employId+' startTime='+data.list[i].startTime+' endTime='+data.list[i].endTime+' style="text-align:center;"><a style="color:blue;text-decoration: none;" class="icon-bianji" href="#" onclick="showAddOrUpdateDiv(this,1,\''+vacationName+'\','+departId+','+year+');">&nbsp;</a><a class="icon-shanchu" style="color:red;text-decoration: none;" href="#" onclick="deleteDutyDetail(this);">&nbsp;</a></td>';
						}
					}
					detailDiv += "<td style='text-align:center;'>"+data.list[i].dutyDate+"</td>";
					detailDiv += "<td style='text-align:center;'>"+data.list[i].weekDay+"</td>";
					detailDiv += "<td style='text-align:center;'>"+data.list[i].employCode+"</td>";
					detailDiv += "<td style='text-align:center;'>"+data.list[i].employName+"</td>";
					detailDiv += "<td style='text-align:center;'>From："+data.list[i].startTime+"</td>";
					detailDiv += "<td style='text-align:center;'>To："+data.list[i].endTime+"</td>";
					detailDiv += "<td style='text-align:center;'>"+data.list[i].workHours+"</td>";
					if(typeof(data.list[i].dutyItem)=="undefined"){
						detailDiv += "<td style='text-align:center;'>&nbsp;</td>";
					}else{
						detailDiv += "<td style='text-align:center;'>"+data.list[i].dutyItem+"</td>";
					}
					if(typeof(data.list[i].remark)=="undefined"){
						detailDiv += "<td style='text-align:center;'>&nbsp;</td></tr>";
					}else{
						detailDiv += "<td style='text-align:center;'>"+data.list[i].remark+"</td></tr>";
					}
				}
				detailDiv += "</tbody></table>";
				detailDiv +="<div class='col'><div class='buttonDiv button-wrap ml-4'>";
				if(type==0){
					detailDiv +="<button id='showDutyDetail' class='btn-green btn-large'><span><i class='icon'></i>值班调班</span></button><button onclick='backWaitList();' class='btn-blue btn-large'><span><i class='icon'></i>返回</span></button>";
				}else if(type==1){
					detailDiv +='<button onclick="commitDuty('+departId+','+year+',\''+vacationName+'\');" class="btn-green btn-large"><span><i class="icon"></i>提交</span></button><button onclick="showAddOrUpdateDiv(this,0,\''+vacationName+'\','+departId+','+year+');" class="btn-blue btn-large"><span><i class="icon-add"></i>新增值班</span></button><button id="showDutyDetail1" class="btn-blue btn-large"><span><i class="icon"></i>返回</span></button>';
				}
				detailDiv +="</div></div>";
				$(".showTable").hide();
				$("#detailDiv").append(detailDiv);
				$("#showDutyDetail").unbind();
				$("#showDutyDetail").bind("click",function(){
					showDutyDetail($(showObj),1);
				});
				$("#showDutyDetail1").unbind();
				$("#showDutyDetail1").bind("click",function(){
					showDutyDetail($(showObj),0);
				});
			}else{
				JEND.page.alert({type:"error", message:data.message});
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

//返回列表页面
function backWaitList(){
	$(".showTable").show();
	$("#detailDiv").empty();
	$("#historyDetail").empty();
}

//展示新增/编辑值班弹框
function showAddOrUpdateDiv(obj,type,vacationName,departId,year){
	pageLoading(true);//打开动画
	//查询节假日天数
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{vacation:vacationName,year:year},
		url:contextPath + "/duty/getDutyDateList.htm",
		success:function(data){
			if(data.success){
				//加载日期
				$("#duty_date").empty();
				var html = "<option value=''>请选择值班日期</option>";
				$("#duty_date").append(html);
				for(var i=0;i<data.dateList.length;i++){
					var option = "";
					if(data.dateList[i].day==$(obj).parents("tr").find("td:eq(1)").text()){
						if(data.dateList[i].type==3){
							option = "<option weekday="+data.dateList[i].weekDay+" value="+data.dateList[i].day+" selected='selected'>"+data.dateList[i].day+"("+data.dateList[i].subject+")</option>";
						}else{
						    option = "<option weekday="+data.dateList[i].weekDay+" value="+data.dateList[i].day+" selected='selected'>"+data.dateList[i].day+"</option>";
						}
					}else{
						if(data.dateList[i].type==3){
							option = "<option weekday="+data.dateList[i].weekDay+" value="+data.dateList[i].day+">"+data.dateList[i].day+"("+data.dateList[i].subject+")</option>";
						}else{
							option = "<option weekday="+data.dateList[i].weekDay+" value="+data.dateList[i].day+">"+data.dateList[i].day+"</option>";
						}
					}
					$("#duty_date").append(option);
				}
				//加载编辑数据
				if(type!=0){
					$("#employ_id").val($(obj).parents("tr").find("td:eq(4)").text());
					$("#employ_id").attr("employId",$(obj).parents("td").attr("employId"));
					$("#start_time").val($(obj).parents("td").attr("startTime"));
					$("#end_time").val($(obj).parents("td").attr("endTime"));
					$("#work_hours").val($(obj).parents("tr").find("td:eq(7)").text());
					$("#work_item").val($(obj).parents("tr").find("td:eq(8)").text());
					$("#remark").val($(obj).parents("tr").find("td:eq(9)").text());
				}
				$("#uptateOrAddDutyDetail").unbind();
				$("#uptateOrAddDutyDetail").bind("click",function(){
					uptateOrAddDutyDetail($(obj),type,vacationName,departId,year);
				})
				$("#employ_id").unbind();
				if(type==0){
					$("#employ_id").bind("click",function(){
						showGetDeptList(departId,this);
					})
				}
				$("#showAddOrUpdateDiv").show();
			}else{
				JEND.page.alert({type:"error", message:data.message});
			}
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
	
}

//关闭新增值班弹框
function closeAddOrUpdateDiv(){
	$("#showAddOrUpdateDiv").hide();
	$("#duty_date").val("");
	$("#employ_id").val("");
	$("#start_time").val("");
	$("#end_time").val("");
	$("#work_hours").val("");
	$("#work_item").val("");
	$("#remark").val("");
	$("#employ_id").attr("employId","");
}

//编辑/新增值班明细(确认)
function uptateOrAddDutyDetail(obj,type,vacationName,departId,year){
	//
	var employId = $("#employ_id").attr("employId");//员工标识
	var employName = $("#employ_id").val();//员工姓名
	var employCode = $("#employ_id").attr("employCode");//员工编号
	var startTime = $("#start_time").val();//开始时间
	var endTime = $("#end_time").val();//结束时间
	var dutyDate = $("#duty_date").val();//日期
	var weekDay = $("#duty_date").find("option:selected").attr("weekDay");//星期
	var workHours = $("#work_hours").val();//工作小时数
	var workItem = $("#work_item").val();//工作内容描述
	var remark = $("#remark").val();//备注
	//非空校验
	if(dutyDate==""||employName==""||startTime==""||endTime==""||workItem==""){
		JEND.page.alert({type:"error", message:"请完善值班安排内容！"});
		return;
	}
	if(!checkUnique(employId,dutyDate,type)){
		JEND.page.alert({type:"error", message:"员工"+employName+"于"+dutyDate+"已存在值班！"});
		return;
	}
	if(type==0){
		//新增
		var detailDiv = "<tr>";
		detailDiv += '<td employId='+employId+' startTime='+startTime+' endTime='+endTime+' style="text-align:center;"><a style="color:blue;text-decoration: none;" class="icon-bianji" href="#" onclick="showAddOrUpdateDiv(this,1,\''+vacationName+'\','+departId+','+year+');">&nbsp;</a><a style="color:red;text-decoration: none;" class="icon-shanchu" href="#" onclick="deleteDutyDetail(this);">&nbsp;</a></td>';
		detailDiv += "<td style='text-align:center;'>"+dutyDate+"</td>";
		detailDiv += "<td style='text-align:center;'>"+weekDay+"</td>";
		detailDiv += "<td style='text-align:center;'>"+employCode+"</td>";
		detailDiv += "<td style='text-align:center;'>"+employName+"</td>";
		detailDiv += "<td style='text-align:center;'>From："+startTime+"</td>";
		detailDiv += "<td style='text-align:center;'>To："+endTime+"</td>";
		detailDiv += "<td style='text-align:center;'>"+workHours+"</td>";
		detailDiv += "<td style='text-align:center;'>"+workItem+"</td>";
		detailDiv += "<td style='text-align:center;'>"+remark+"</td></tr>";
		$("#detailDiv").find("tbody").append(detailDiv);
	}else{
		//编辑
		$(obj).parents("td").attr("startTime",startTime);
		$(obj).parents("td").attr("endTime",endTime);
		$(obj).parents("tr").find("td:eq(1)").text(dutyDate);
		$(obj).parents("tr").find("td:eq(2)").text(weekDay);
		$(obj).parents("tr").find("td:eq(5)").text("From："+startTime);
		$(obj).parents("tr").find("td:eq(6)").text("To："+endTime);
		$(obj).parents("tr").find("td:eq(7)").text(workHours);
		$(obj).parents("tr").find("td:eq(8)").text(workItem);
		$(obj).parents("tr").find("td:eq(9)").text(remark);
	}
	closeAddOrUpdateDiv();//关闭弹框
}

//校验一个人一天只能有一条值班数据
function checkUnique(employId,dutyDate,type){
	var flag = false;
	var count = 0;
	$("#detailDiv").find("tbody").find("tr").each(function(){
		var id = $(this).find("td:eq(0)").attr("employId").trim();//员工id
		var date = $(this).find("td:eq(1)").text().trim();//值班日期
		if((id+"_"+date)==(employId+"_"+dutyDate)){
			count = count+1;
		}
	});
	if(type==0){//新增
		if(count<=0){
			flag = true;
		}
	}else{//编辑
		if(count<=1){
			flag = true;
		}
	}
	return flag;
}

function checkStartTime(){
	var startTime = $("#start_time").val();
	if(startTime != ""){
		var minute = startTime.split(":")[1];
		if(0<minute<30){
			$("#start_time").val(startTime.split(":")[0]+":00")
		}
		if(minute >= 30){
			$("#start_time").val(startTime.split(":")[0]+":30")
		}
		calculatedHours();
	}
}
function checkEndTime(){
	var endTime = $("#end_time").val();
	if(endTime != ""){
		var minute = endTime.split(":")[1];
		if(0<minute<30){
			$("#end_time").val(endTime.split(":")[0]+":00")
		}
		if(minute >= 30){
			$("#end_time").val(endTime.split(":")[0]+":30")
		}
		calculatedHours();
	}
}
//计算小时数
function calculatedHours(){
	if(typeof($("#start_time").val())!="undefined" && $("#start_time").val()!="" && typeof($("#end_time").val())!="undefined" && $("#end_time").val()!=""){
		var startTime=$("#start_time").val().substring(0,2);
		var endTime=$("#end_time").val().substring(0,2);
		//如果结束时间是0点则结束时间设为24点
		if(endTime == 0){
			endTime = 24;
		}
		var startHours=($("#start_time").val().substring(3,5)==30)?0.5:0;
		var endHours=($("#end_time").val().substring(3,5)==30)?0.5:0;
		
		var hours;
		if(startTime<endTime){
			hours=(parseFloat(endTime)+parseFloat(endHours))-(parseFloat(startTime)+parseFloat(startHours));
			if(hours>=10){
				hours=hours-2;
			}else if(hours>=5){
				hours=hours-1;
			}
			$("#work_hours").val(hours);
			if($("#work_hours").val() == "NaN"){
				$("#work_hours").val("")
			}
		}else{
			$("#work_hours").val(0);
		}
	}
}

//删除值班
function deleteDutyDetail(obj){
	var employName = $(obj).parents("tr").find("td:eq(4)").text();
	var dutyDate = $(obj).parents("tr").find("td:eq(1)").text();
	secondConfirmation({
		"msg":"确认删除 "+employName+" 于"+dutyDate+"的值班？",
		sureFn:function(){
			$(obj).parents("tr").remove();
		}
	});
}

//提交值班
function commitDuty(departId,year,vacationName){
	//遍历table
	var list = new Array();
	$("#detailDiv").find("tbody").find("tr").each(function(){
		if($(this).attr("source")!=1){
			var employId = $(this).find("td:eq(0)").attr("employId").trim();//员工标识
			var dutyDate = $(this).find("td:eq(1)").text().trim();//日期
			var startTime = $(this).find("td:eq(0)").attr("startTime").trim();//开始时间
			var endTime = $(this).find("td:eq(0)").attr("endTime").trim();//结束时间
			var workHours = $(this).find("td:eq(7)").text().trim();//工作小时数
			var workItems = $(this).find("td:eq(8)").text().trim();//工作内容描述
			var remark = $(this).find("td:eq(9)").text().trim();//备注
			var employName = $(this).find("td:eq(4)").text().trim();//员工标识
			var param = {
				dutyDate:dutyDate,
				employId:employId,
				employName:employName,
				startTime:startTime,
				endTime:endTime,
				workHours:workHours,
				workItems:workItems,
				remark:remark
			};
			list.push(param);
		}
	});
	
	secondConfirmation({
		"msg":"是否确认提交？",
		sureFn:function(){
			pageLoading(true);//加载动画
			$.ajax({
		 		async : true,
		 		type : "post",
				dataType:"json",
				data : {departId:departId,year:year,vacationName:vacationName,info:JSON.stringify(list)},
		 		url : contextPath + "/duty/commitDutyByHr.htm",
		 		success : function(response) {
		 			if(response.success){
		 				query();
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
	});
}

function showHistoryDetail(id){
	pageLoading(true);//打开动画
	//查询节假日天数
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{id:id},
		url:contextPath + "/duty/showHistoryDetail.htm",
		success:function(data){
			if(data.success){
				$("#historyDetail").empty();
				$(".showTable").hide();
				var html = '<div class="title" style="text-align:center;"><strong>'+data.title+'</strong></div>';
				html += '<div class="col"><div class="form"><label class="w">公司：</label><input type="text" class="form-text" style="border:0px;" value='+data.companyName+'>';
				html += '</div><div class="form"><label class="w">部门：</label><input type="text" class="form-text" style="border:0px;" value='+data.departName+'></div></div>';
				html += '<h4>调整后：</h4><table border="1"><thead><th style="overflow-x:auto;width:70px;text-align:center;">日期</th>';
				html += '<th style="overflow-x:auto;width:70px;text-align:center;">星期</th> <th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>';
				html += '<th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th><th style="overflow-x:auto;width:150px;text-align:center;" colspan="2">时间要求</th>';
				html += '<th style="overflow-x:auto;width:100px;text-align:center;">工作小时数</th><th style="overflow-x:auto;width:200px;text-align:center;">工作内容简述</th>	';
				html += '<th style="overflow-x:auto;width:200px;text-align:center;">备注</th></thead><tbody>';
				for(var i=0;i<data.list.length;i++){
					if(data.list[i].isMove==0){
						html += '<tr><td style="text-align:center;">'+getValByUndefined(data.list[i].vacationDate)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].weekStr)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].codes)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].names)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].startTime.substring(10,16))+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].endTime.substring(10,16))+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].workHours)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].dutyItem)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].remarks)+'</td></tr>';
					}
				}
				html += '</tbody></table>';
				html += '<h4>调整前：</h4><table border="1"><thead><th style="overflow-x:auto;width:70px;text-align:center;">日期</th>';
				html += '<th style="overflow-x:auto;width:70px;text-align:center;">星期</th> <th style="overflow-x:auto;width:100px;text-align:center;">员工编号</th>';
				html += '<th style="overflow-x:auto;width:100px;text-align:center;">员工姓名</th><th style="overflow-x:auto;width:150px;text-align:center;" colspan="2">时间要求</th>';
				html += '<th style="overflow-x:auto;width:100px;text-align:center;">工作小时数</th><th style="overflow-x:auto;width:200px;text-align:center;">工作内容简述</th>	';
				html += '<th style="overflow-x:auto;width:200px;text-align:center;">备注</th></thead><tbody>';
				for(var i=0;i<data.list.length;i++){
					if(data.list[i].isMove==1){
						html += '<tr><td style="text-align:center;">'+getValByUndefined(data.list[i].vacationDate)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].weekStr)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].codes)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].names)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].startTime.substring(10,16))+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].endTime.substring(10,16))+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].workHours)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].dutyItem)+'</td>';
						html += '<td style="text-align:center;">'+getValByUndefined(data.list[i].remarks)+'</td></tr>';
					}
				}
				html += '</tbody></table>';
				html += '<div class="col"><div class="buttonDiv button-wrap ml-4"><button onclick="backWaitList();" class="btn-blue btn-large"><span><i class="icon"></i>返回</span></button></div></div>';
				$("#historyDetail").append(html);
			}else{
				JEND.page.alert({type:"error", message:data.message});
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

