
$(function(){
	//初始化部门
	getFirstDepart();
	$("#query").click(function(){
		$("#pageNo").val(1);
		returnList();
	});
});

function gotoPage(page){
	if($("#year").val()==""){     
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择年份！");
		});
		return;
	}
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
		data:data,
		url:contextPath + "/empApplicationOvertime/getOvertimeManagePageList.htm",
		success:function(response){
			$("#reportList0").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].cnName)+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].allowRemainDays)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].usedDays)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].quitTime)+"</td>";
				var jobStatusStr="";
				if(Number(response.rows[i].jobStatus)==0){
					jobStatusStr="在职";
				}else if(Number(response.rows[i].jobStatus)==2){
					jobStatusStr="待离职";
				}
				html += "<td style='text-align:center;'>"+getValByUndefined(jobStatusStr)+"</td>";
				html += "<td><a style='color:blue;'  onclick='insert("+response.rows[i].employeeId+",\""+response.rows[i].cnName+"\",\""+response.rows[i].code+"\")'>新增</a>  <a style='color:blue;'  onclick='queryInfo("+response.rows[i].employeeId+","+$("#year").val()+")'>详情</a></td>";
				html += "</tr>";
			}	
			
			$("#reportList0").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPage("commonPage",response,page);
			pageLoading(false);//关闭动画
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}

function insert(employeeId,cnName,code){
	$("#insertDiv").show();
	$("#employeeId").val(employeeId);
	$(".insertForm :text[name='cnName']").val(cnName);
	$(".insertForm :text[name='code']").val(code);
	
	$("#insertRemark").val("");
}

function saveMsg(){
	var startTime=$("#startTime").val();
	var endTime=$("#endTime").val();
	if(startTime=="" || endTime==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请填写调休有效期时间");
		});
		return; 
	}
	if($(".insertForm :text[name='allowRemainDays']").val()=="" ||$(".insertForm :text[name='allowRemainDays']").val()==null){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请填写调休小时数");
		});
		return;
	}
	
	secondConfirmation({
	   "msg":"是否确认新增调休？",
		sureFn:function(){
			save();
		}
	});
}
function save(){
	var data=$("#insertForm").serialize();
	pageLoading(true);//加载动画	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		url:contextPath + "/empApplicationOvertime/saveOverTimeManage.htm?"+data,
		success:function(data){
				if(data.success){
					var year=$(".insertForm :text[name='endTimeFormat']").val().substring(0,4);
					$(".insertForm :text[name='startTimeFormat']").val("");
					$(".insertForm :text[name='endTimeFormat']").val("");
					$(".insertForm :text[name='cnName']").val("");
					$(".insertForm :text[name='code']").val("");
					$(".insertForm :text[name='endTiem']").val("");
					$(".insertForm :text[name='allowRemainDays']").val("");					
					closeDiv('insertDiv');
					var display =$('#overTimeListShow').css('display');
					if(display == 'none'){
						queryInfo($("#employeeId").val(),year);
					}else{
						returnList();
					}
				}
				var type;
				if(data.success){
					type="success";
				}else{
					type="error";
				}
				JEND.page.alert({type:type, message:data.message});
				$("#insertRemark").text("");
			pageLoading(false);
		},complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}

//提示框关闭
var closeDiv = function(divName){
	$("#"+divName).hide();
}


function queryInfo(empid,year){
	$("#overTimeListShow").hide();
	$(".showTable").show();
	
	if($("#pageNo1").val()==null){
		$("#pageNo1").val(1);
	}
	
	pageLoading(true);//加载动画
	if (typeof(year) == "undefined" ||year==""){
		var data="employeeId="+empid+"&page=" + $("#pageNo1").val() + "&rows=10";
	}else{
		var data="employeeId="+empid+"&year=" + year+"&page=" + $("#pageNo1").val() + "&rows=10";
	}
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:data,
		url:contextPath + "/empApplicationOvertime/queryInfoByEmp.htm",
		success:function(response){
			$("#reportList1").empty();
			var html = "";
			if(response.overtime!=null){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.overtime.code+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.overtime.cnName)+"</td>";
				html += "<td style='text-align:center;'>"+response.overtime.departName+"</td>";
				html += "<td style='text-align:center;'>"+(isVlues(response.overtime.allowRemainDays)==false?0:response.overtime.allowRemainDays)+"</td>";
				html += "<td style='text-align:center;'>"+(isVlues(response.overtime.usedDays)==false?0:response.overtime.usedDays)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.overtime.quitTime)+"</td>";
				var jobStatusStr="";
				if(Number(response.overtime.jobStatus)==0){
					jobStatusStr="在职";
				}else if(Number(response.overtime.jobStatus)==1){
					jobStatusStr="离职";
				}else if(Number(response.overtime.jobStatus)==2){
					jobStatusStr="已提出离职";
				}
				html += "<td style='text-align:center;'>"+getValByUndefined(jobStatusStr)+"</td>";
				html += "<td><a style='color:blue;'  onclick='insert("+response.overtime.employeeId+",\""+response.overtime.cnName+"\",\""+response.overtime.code+"\")'>新增</a>  <a style='color:blue;'  onclick='returnList()'>返回</a></td>";
				html += "</tr>";
				$("#empId").val(response.overtime.employeeId);
				$("#empYear").val(response.overtime.year);
			}	
			
			$("#reportList2").empty();
			if(response.pm!=null && response.pm.rows.length>0){
				var html1 = "";			
				for(var i=0;i<response.pm.rows.length;i++){
					html1 += "<tr>";
					html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].year)+"</td>";
					html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].allowRemainDays)+"</td>";
					html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].usedDays)+"</td>";
					html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].startTime.substring(0,10))+"</td>";
					html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].endTime.substring(0,10))+"</td>";
					var isActiveStr="";
					var isUpdate="<td></td>";
					if(Number(response.pm.rows[i].isActive)==0){
						isActiveStr="生效";
						isUpdate="<td><a style='color:blue;'  onclick='updateById("+response.pm.rows[i].id+")'>修改</a></td>";
					}else if(Number(response.pm.rows[i].isActive)==1){
						isActiveStr="失效";
						isUpdate="<td></td>";
					}
					html1 += "<td style='text-align:center;'>"+isActiveStr+"</td>";
					html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].remark)+"</td>"; //
					html1 += isUpdate;
					html1 += "</tr>";
				}	
				
				
				$("#reportList2").append(html1);
				if(response.pm != null && response.pm.pageNo != null) {
					page = response.pm.pageNo;
				}
				initPageByName("commonPage1",response.pm,page,"gotoQueryInfo");
			}

			$("#reportList1").append(html);
			
			pageLoading(false);//关闭动画
		}/*,
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }*/
	});
	
}
function gotoQueryInfo(page){
	$("#pageNo1").val(page);
	var year=((typeof($("#empYear").val()) == "undefined"||$("#empYear").val()==null||$("#empYear").val()=="")?$("#year").val():$("#empYear").val());
	queryInfo($("#empId").val(),parseInt(year));
}

function updateById(id){
	$.post(basePath+"/empApplicationOvertime/queryInfoById.htm",{id:id},function(data){
		if(data){
			$("#updateId").val(data.overtime.id);
			$("#updateEmployeeId").val(data.overtime.employeeId);
			$(".updateForm :text[name='cnName']").val(data.overtime.cnName);
            $(".updateForm :text[name='code']").val(data.overtime.code);
            $(".updateForm :text[name='startTimeFormat']").val(data.overtime.startTime.substring(0,10));
            $(".updateForm :text[name='endTimeFormat']").val(data.overtime.endTime.substring(0,10));
            $(".updateForm :text[name='allowRemainDays']").val(data.overtime.allowRemainDays);
            $("#usedDays").text(data.overtime.usedDays);
            $("#updateRemark").val(data.overtime.remark);
		}
	},'json')
	$("#updateDiv").show();
}


function updateMsg(){
	var startTime=$("#startTimeUpdate").val();
	var endTime=$("#endTimeUpdate").val();
	if(startTime=="" || endTime==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请填写调休有效期时间");
		});
		return; 
	}
	if($(".updateForm :text[name='allowRemainDays']").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请填写调休小时数");
		});
		return;
	}
	secondConfirmation({
	   "msg":"是否确认修改调休？",
		sureFn:function(){
			update();
		}
	});
}
function update(){
	var data=$("#updateForm").serialize();
	pageLoading(true);//加载动画	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		url:contextPath + "/empApplicationOvertime/updateOverTimeManage.htm?"+data,
		success:function(data){
				if(data.success){
					var year=$(".updateForm :text[name='endTimeFormat']").val().substring(0,4);
					$(".updateForm :text[name='startTimeFormat']").val("");
					$(".updateForm :text[name='endTimeFormat']").val("");
					$(".updateForm :text[name='cnName']").val("");
					$(".updateForm :text[name='code']").val("");
					$(".updateForm :text[name='endTiem']").val("");
					$(".updateForm :text[name='allowRemainDays']").val("");
					$("#updateRemark").val("");
					closeDiv('updateDiv');
				}
				var type;
				if(data.success){
					type="success";
				}else{
					type="error";
				}
				JEND.page.alert({type:type, message:data.message});
				queryInfo($("#updateEmployeeId").val(),year);
			pageLoading(false);
		},complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}





function returnList(){
	$(".showTable").hide();
	$("#overTimeListShow").show();
	gotoPage($("#pageNo").val());
}
